
#include "jace/JNIHelper.h"

#ifndef JACE_OS_DEP_H
#include "jace/os_dep.h"
#endif

#ifndef JACE_NAMESPACE_H
#include "jace/namespace.h"
#endif

#ifndef JACE_JFACTORY_H
#include "jace/JFactory.h"
#endif
using jace::JFactory;

#ifndef JACE_JOBJECT_H
#include "jace/proxy/JObject.h"
#endif
using jace::proxy::JObject;

#ifndef JACE_PEER_H
#include "jace/Peer.h"
#endif
using jace::Peer;

#ifndef JACE_VM_LOADER
#include "jace/VmLoader.h"
#endif
using ::jace::VmLoader;

#include <cstdarg>
#include <stdlib.h>

#include <iostream>
using std::cout;
using std::endl;

#ifdef SUPPORTS_SSTREAM
  #include <sstream>
  using std::stringstream;
#else
  #include <strstream>
  using std::strstream;
#endif

#include <algorithm>
using std::copy;
using std::replace;

#include <vector>
using std::vector;

#include <map>
using std::map;

#include <string>
using std::string;

#pragma warning(push)
#pragma warning(disable: 4103 4244 4512)
#include <boost/thread/mutex.hpp>
#include <boost/thread/tss.hpp>
using boost::mutex;
#pragma warning(pop)

BEGIN_NAMESPACE_2( jace, helper )


// A reference to the java virtual machine.
// We're under the assumption that there will always only be one of these.
//
JavaVM* javaVM = 0;

auto_ptr<VmLoader> globalLoader( 0 );

// A workaround for Ctrl-C causing problems.
// If Ctrl-C isn't handled by the program, and it causes
// a SIGTERM, the VM automatically shuts down. Meanwhile
// we still attempt to free references. Since the VM isn't
// there anymore, we core dump.
//
// Since there is no means for querying a VM to see if it is still
// alive, we have to come up with something.
// By making a global variable with destructor, we hope to discover
// that the program is shutting down, and disable interaction
// with the VM. This seems to work ok on Windows, but not with
// gcc. *shrugs* For now, those people will have to handle Ctrl-C.
//
// One other important note for gcc users: It seems that a trivial
// re-arranging of these global variables can cause very strange linker
// errors to occur. The shared jace library still builds, but when applications
// try to link with the library, it's as if none of the library code is actually
// there. I can't see how this behavior is anything but a bug in gcc
// (currently 3.2.2).
//
//
bool globalHasShutdown = false;
/**
 * Ensures that the JVM does not shut down while it is being used.
 */
boost::mutex shutdownMutex;

// The map of all of the java class factories.
//
typedef map<string,JFactory*> FactoryMap;

FactoryMap* getFactoryMap() {
  static FactoryMap factoryMap;
  return &factoryMap;
}

// A quick hack to get a string representation for any value
// This should probably go into some framework utility class
#ifdef SUPPORTS_SSTREAM

  template <class T> string toString( T value ) {
    stringstream stream;
    stream << value;
    return stream.str();
  }

#else

  template <class T> string toString( T value ) {
    strstream stream;
    stream << value;
    return string( stream.str(), stream.pcount() );
  }

#endif

void classLoaderDestructor( jobject* value ) {

	// Invoked by setClassLoader() or when the thread exits
	if ( value == 0 )
		return;
	mutex::scoped_lock lock(shutdownMutex);
	if ( hasShutdown() )
		return;

	// Read the thread state
	JavaVM* jvm = getJavaVM();
	::jace::VmLoader* loader = getVmLoader();
	JNIEnv* env;
	bool isDetached = jvm->GetEnv( (void**) &env, loader->version() ) == JNI_EDETACHED;
	assert(!isDetached);

	env = helper::attach();
	helper::deleteGlobalRef( env, *value );
	delete[] value;

	// Restore the thread state
	if (isDetached)
		jace::helper::detach();
}

boost::thread_specific_ptr<jobject> threadClassLoader(classLoaderDestructor);

std::string asString( JNIEnv* env, jstring str ) {
  const char* utfString = env->GetStringUTFChars( str, 0 );
  if ( ! utfString ) {
    std::string msg = "Unable to retrieve the character string for an exception message.";
    throw JNIException( msg );
  }
  std::string stdString = utfString;
  env->ReleaseStringUTFChars( str, utfString );
  return stdString;
}


::jace::VmLoader* getVmLoader() {
  return globalLoader.get();
}


void setVmLoader( const ::jace::VmLoader& loader ) {
  globalLoader = auto_ptr<VmLoader>( loader.clone() );
}


void createVm( const VmLoader& loader,
               const OptionList& options,
               bool ignoreUnrecognized ) {

  setVmLoader( loader );
  globalLoader->loadVm();

  JavaVM* vm;
  JNIEnv* env;

  JavaVMInitArgs vm_args;
  JavaVMOption* jniOptions = options.createJniOptions();

  vm_args.version = globalLoader->version();
  vm_args.options = jniOptions;
  vm_args.nOptions = jint( options.size() );

  vm_args.ignoreUnrecognized = ignoreUnrecognized;

  jint rc = globalLoader->createJavaVM( &vm, reinterpret_cast<void**>( &env ), &vm_args );

  options.destroyJniOptions( jniOptions );

  if ( rc != 0 ) {
    string msg = "Unable to create the virtual machine. The error was " + toString( rc );
    throw JNIException( msg );
  }
	registerShutdownHook( env );
}


void registerShutdownHook( JNIEnv *env ) {
  jclass hookClass = env->FindClass( "jace/util/ShutdownHook" );
  if ( ! hookClass ) {
    string msg = "Assert failed: Unable to find the class, jace.util.ShutdownHook.";
    throw JNIException( msg );
  }

  jmethodID hookGetInstance = env->GetStaticMethodID( hookClass, "getInstance", "()Ljace/util/ShutdownHook;" );
  if ( ! hookGetInstance ) {
		deleteLocalRef( env, hookClass );
    string msg = "Assert failed: Unable to find the method, ShutdownHook.getInstance().";
    throw JNIException( msg );
  }

	jobject hookObject = env->CallStaticObjectMethod( hookClass, hookGetInstance );
	if ( ! hookObject )	{
		deleteLocalRef( env, hookClass );
    string msg = "Unable to invoke ShutdownHook.getInstance()";
		try
		{
			helper::catchAndThrow();
		}
		catch (JNIException& e)
		{
			msg.append("\ncaused by:\n");
			msg.append(e.what());
		}
    throw JNIException( msg );
	}

  jmethodID hookRegisterIfNecessary = env->GetMethodID( hookClass, "registerIfNecessary", "()V" );
  if ( ! hookRegisterIfNecessary ) {
		deleteLocalRef( env, hookObject );
		deleteLocalRef( env, hookClass );
    string msg = "Assert failed: Unable to find the method, ShutdownHook.registerIfNecessary().";
    throw JNIException( msg );
  }

	env->CallObjectMethodA( hookObject, hookRegisterIfNecessary, 0 );
	try
	{
		helper::catchAndThrow();
	}
	catch (JNIException& e)
	{
		string msg = "Exception thrown invoking ShutdownHook.registerIfNecessary()";
		msg.append("\ncaused by:\n");
		msg.append(e.what());
		throw JNIException( msg );
	}
	deleteLocalRef( env, hookObject );
	deleteLocalRef( env, hookClass );
}


/**
 * Invoked by jace.util.ShutdownHook on VM shutdown.
 */
extern "C" JNIEXPORT void JNICALL Java_jace_util_ShutdownHook_signalVMShutdown( JNIEnv*, jclass )
{
	mutex::scoped_lock lock(shutdownMutex);
	globalHasShutdown = true;
}


/**
 * Returns the current java virtual machine.
 *
 */
JavaVM* getJavaVM() throw ( JNIException ) {

  if ( ! javaVM ) {

    jsize numVMs;
		if (globalLoader.get() == 0) {
      string msg = string( "JNIHelper::getJavaVM\n" ) +
                   "Unable to find the JVM loader";
			throw JNIException( msg );
		}
    jint result = globalLoader->getCreatedJavaVMs( &javaVM, 1, &numVMs );

    if ( result != 0 ) {
      string msg = string( "JNIHelper::getJavaVM\n" ) +
                   "Unable to find the JVM. The specific JNI error code is " +
                   toString( result );
      throw JNIException( msg );
    }

    if ( numVMs != 1 ) {
      string msg = string( "JNIHelper::getJavaVM\n" ) +
                   "Looking for exactly 1 JVM, but " +
                   toString( numVMs ) +
                   " were found.";
      throw JNIException( msg );
    }
  }

  return javaVM;
}


void shutdown() {
	mutex::scoped_lock lock(shutdownMutex);
  globalHasShutdown = true;

	// Currently (JDK 1.5) JVM unloading is not supported and DestroyJavaVM() always returns failure.
	// We do our best to ensure that the JVM is not used past this point.
	globalLoader->unloadVm();
}


/**
 * Attaches the current thread to the virtual machine and returns the appropriate 
 * JNIEnv for the thread. If the thread is already attached, this method method 
 * does nothing.
 *
 * This method is equivilent to attach(0, 0, false).
 *
 * @throws JNIException if an error occurs while trying to attach the current thread.
 * @see AttachCurrentThread
 * @see attach(const jobject, const char*, const bool)
 */
JNIEnv* attach() throw ( JNIException ) {

	return attach(0, 0, false);
}


/**
 * Attaches the current thread to the virtual machine
 * and returns the appropriate JNIEnv for the thread.
 * If the thread is already attached, this method method does nothing.
 *
 * @param threadGroup the ThreadGroup associated with the thread, or null
 * @param name the thread name, or null
 * @param daemon true if the thread should be attached as a daemon thread
 * @throws JNIException if an error occurs while trying to attach the current thread.
 * @see AttachCurrentThread
 * @see AttachCurrentThreadAsDaemon
 */
JNIEnv* attach(const jobject threadGroup, const char* name, const bool daemon) throw ( JNIException ) {

	mutex::scoped_lock lock(shutdownMutex);
  if ( hasShutdown() ) {
    throw JNIException( "The VM has already been shutdown." );
  }

  JNIEnv* env;
	JavaVMAttachArgs args = {0};
	args.version = globalLoader->version();
	args.group = threadGroup;
	if (name != 0)
		strcpy(args.name, name);
  jint result;
	if (!daemon)
		result = getJavaVM()->AttachCurrentThread( reinterpret_cast<void**>( &env ), &args );
	else
		result = getJavaVM()->AttachCurrentThreadAsDaemon( reinterpret_cast<void**>( &env ), &args );

  if ( result != 0 ) {
    string msg = "JNIHelper::attach\n" \
                 "Unable to attach the current thread. The specific JNI error code is " +
                 toString( result );
    throw JNIException( msg );
  }

  return env;
}


/**
 * Detaches the current thread from the virtual machine.
 *
 */
void detach() throw () {

  getJavaVM()->DetachCurrentThread();
}


/**
 * Enlists a new factory for a java class with the JNIHelper.
 *
 * All java classes should enlist with the JNIHelper on start-up.
 * They can do this by adding a static member variable
 * of type JEnlister to their class definition.
 *
 * For example, java::lang::Object has a static member variable,
 *
 *   static JEnlister<Object> enlister;
 *
 * which is all that is required to register a new factory
 * for itself.
 *
 */
void enlist( JFactory* factory ) {
  string name = factory->getClass()->getName();
  replace( name.begin(), name.end(), '/', '.' );
  getFactoryMap()->insert( FactoryMap::value_type( name, factory ) );
  //  cout << "helper::enlist - Enlisted " << name << endl;
}


jobject newLocalRef( JNIEnv* env, jobject ref ) {

  jobject localRef = env->NewLocalRef( ref );

  if ( ! localRef ) {
    string msg = string( "JNIHelper::newLocalRef\n" ) +
                 "Unable to create a new local reference.\n" +
                 "It is likely that you have exceeded the maximum local reference count.\n" +
                 "You can increase the maximum count with a call to EnsureLocalCapacity().";
    throw JNIException( msg );
  }

  return localRef;
}

void deleteLocalRef( JNIEnv* env, jobject localRef ) {

	mutex::scoped_lock lock(shutdownMutex);
  if ( hasShutdown() ) {
    return;
  }

  env->DeleteLocalRef( localRef );
}

jobject newGlobalRef( JNIEnv* env, jobject ref ) {

  jobject globalRef = env->NewGlobalRef( ref );

  if ( ! globalRef ) {
    string msg = string( "JNIHelper::newGlobalRef\n" ) +
                 "Unable to create a new global reference.\n" +
                 "It is likely that you have exceeded the max heap size of your virtual machine.";
    throw JNIException( msg );
  }

  return globalRef;
}

void deleteGlobalRef( JNIEnv* env, jobject globalRef ) {

	mutex::scoped_lock lock(shutdownMutex);
  if ( hasShutdown() ) {
    return;
  }

  env->DeleteGlobalRef( globalRef );
}

/**
 * Checks for a java exception.
 *
 * If a java exception has been thrown, the java exception is cleared,
 * and a corresponding C++ proxy exception is thrown.
 *
 * @internal We don't want to put a throw specification on this, because
 * it could throw any range of exceptions.
 *
 */
void catchAndThrow() {

  JNIEnv* env = attach();

  if ( ! env->ExceptionCheck() ) {
    return;
  }

  jthrowable jexception = env->ExceptionOccurred();

  // cout << "helper::catchAndThrow() - Discovered an exception: " << endl;
  // print( jexception );

  env->ExceptionClear();

  /* Find the fully qualified name for the exception type, so
   * we can find a matching C++ proxy exception.
   *
   * In java, this looks like:
   *   String typeName = exception.getClass().getName();
   */
  //  cout << "helper::catchAndThrow() - Retrieving the exception class type..." << endl;
  jclass throwableClass = env->FindClass( "java/lang/Throwable" );

  if ( ! throwableClass ) {
    string msg = "Assert failed: Unable to find the class, java.lang.Throwable.";
    throw JNIException( msg );
  }

  jclass classClass = env->FindClass( "java/lang/Class" );

  if ( ! classClass ) {
    string msg = "Assert failed: Unable to find the class, java.lang.Class.";
    throw JNIException( msg );
  }

  jmethodID throwableGetClass = env->GetMethodID( throwableClass, "getClass", "()Ljava/lang/Class;" );

  if ( ! throwableGetClass ) {
    string msg = "Assert failed: Unable to find the method, Throwable.getClass().";
    throw JNIException( msg );
  }

  deleteLocalRef( env, throwableClass );

  jmethodID classGetName = env->GetMethodID( classClass, "getName", "()Ljava/lang/String;" );

  if ( ! classGetName ) {
    string msg = "Assert failed: Unable to find the method, Class.getName().";
    throw JNIException( msg );
  }

  jmethodID classGetSuperclass = env->GetMethodID( classClass, "getSuperclass", "()Ljava/lang/Class;" );

  if ( ! classGetSuperclass ) {
    string msg = "Assert failed: Unable to find the method, Class.getSuperclass().";
    throw JNIException( msg );
  }

  deleteLocalRef( env, classClass );

  jobject exceptionClass = env->CallObjectMethod( jexception, throwableGetClass );

  if ( env->ExceptionOccurred() ) {
    env->ExceptionDescribe();
    string msg = "helper::catchAndThrow()\n" \
                 "An error occurred while trying to call getClass() on the thrown exception.";
    throw JNIException( msg );
  }

  jstring exceptionType = static_cast<jstring>( env->CallObjectMethod( exceptionClass, classGetName ) );

  if ( env->ExceptionOccurred() ) {
    env->ExceptionDescribe();
    string msg = "helper::catchAndThrow()\n" \
                 "An error occurred while trying to call getName() on the class of the thrown exception.";
    throw JNIException( msg );
  }

  string exceptionTypeString = asString( env, exceptionType );

  /* Now, find the matching factory for this exception type.
   */
  while ( true ) {

    FactoryMap::iterator it = getFactoryMap()->find( exceptionTypeString );

    /* If we couldn't find a match, try to find the parent exception type.
     */
    if ( it == getFactoryMap()->end() ) {

      // cout << "Finding super class for " << endl;
      // print( exceptionClass );

      jobject superClass = env->CallObjectMethod( exceptionClass, classGetSuperclass );

      if ( env->ExceptionOccurred() ) {
        env->ExceptionDescribe();
        string msg = "helper::catchAndThrow()\n" \
                     "An error occurred while trying to call getSuperclass() on the thrown exception.";
        throw JNIException( msg );
      }

      /* We get NULL if we've already reached java.lang.Object, in which case,
       * we couldn't find any match at all.
       */
      if ( ! superClass ) {
        break;
      }

      deleteLocalRef( env, exceptionClass );
      deleteLocalRef( env, exceptionType );
      exceptionClass = superClass;

      exceptionType = static_cast<jstring>( env->CallObjectMethod( exceptionClass, classGetName ) );

      if ( env->ExceptionOccurred() ) {
        env->ExceptionDescribe();
        string msg = "helper::catchAndThrow()\n" \
                     "An error occurred while trying to call getName() on the superclass " \
                     "of the thrown exception.";
        throw JNIException( msg );
      }

      exceptionTypeString = asString( env, exceptionType );
			if ( exceptionTypeString == "java.lang.Object" ) {
				/*
				 * Couldn't find a matching exception. Abort!
				 */
				break;
			}
      continue;
    }

    // Ask the factory to throw the exception.
    // cout << "helper::catchAndThrow() - Throwing the exception " << endl;
    // print( jexception );

    jvalue value;
    value.l = jexception;
    it->second->throwInstance( value );
  }

	exceptionClass = env->CallObjectMethod( jexception, throwableGetClass );

  if ( env->ExceptionOccurred() ) {
    env->ExceptionDescribe();
    string msg = "helper::catchAndThrow()\n" \
                 "An error occurred while trying to call getClass() on the thrown exception.";
    throw JNIException( msg );
  }

  exceptionType = static_cast<jstring>( env->CallObjectMethod( exceptionClass, classGetName ) );

  if ( env->ExceptionOccurred() ) {
    env->ExceptionDescribe();
    string msg = "helper::catchAndThrow()\n" \
                 "An error occurred while trying to call getName() on the class of the thrown exception.";
    throw JNIException( msg );
  }

  exceptionTypeString = asString( env, exceptionType );
  //    cout << "Unable to find an enlisted class factory matching the type <" + exceptionTypeString + ">" << endl;
  //    cout << "Throwing Exception instead." << endl;
  string msg = string( "Can't find any linked in parent exception for " ) + exceptionTypeString + "\n";
  throw JNIException( msg );
}



::jace::Peer* getPeer( jobject jPeer ) {

  JNIEnv* env = attach();

  jclass peerClass = env->GetObjectClass( jPeer );
  jmethodID handleID = env->GetMethodID( peerClass, "jaceGetNativeHandle", "()J" );

  if ( ! handleID ) {
    string msg = "Unable to locate the method, \"jaceGetNativeHandle\".\n" \
                 "The class has not been properly enhanced.";
		try
		{
			helper::catchAndThrow();
		}
		catch (JNIException& e)
		{
			msg.append("\ncaused by:\n");
			msg.append(e.what());
		}
    throw JNIException( msg );
  }

  jlong nativeHandle = env->CallLongMethod( jPeer, handleID );
  catchAndThrow();

  ::jace::Peer* peer = reinterpret_cast< ::jace::Peer*>( nativeHandle );

  return peer;
}

/**
 * Returns the ClassLoader being used by the current thread.
 *
 */
jobject getClassLoader() {
	jobject* value = threadClassLoader.get();
	if (value == 0)
		return 0;
	return value[0];
}

void setClassLoader(jobject classLoader) {

	JNIEnv* env = attach();

	// boost::thread_specific_ptr can only store a pointer to jobject, but someone needs to keep
	// the underlying jobject alive so we use a dynamically-allocated array.
	jobject* ptr = new jobject[1];
	if ( classLoader != 0 )
		ptr[0] = newGlobalRef( env, classLoader );
	else
		ptr[0] = 0;
	try
	{
		threadClassLoader.reset(ptr);
	}
	catch ( boost::thread_resource_error& e) {
		throw JNIException( e.what() );
	}
}

std::string toString( jobject obj ) {

  JNIEnv* env = attach();

  jclass objectClass = env->FindClass( "java/lang/Object" );

  if ( ! objectClass ) {
    string msg = "Assert failed: Unable to find the class, java.lang.Object.";
		try
		{
			helper::catchAndThrow();
		}
		catch (JNIException& e)
		{
			msg.append("\ncaused by:\n");
			msg.append(e.what());
		}
    throw JNIException( msg );
  }

  jmethodID toString = env->GetMethodID( objectClass, "toString", "()Ljava/lang/String;" );

  if ( ! toString ) {
    string msg = "Assert failed: Unable to find the method, Object.toString().";
		try
		{
			helper::catchAndThrow();
		}
		catch (JNIException& e)
		{
			msg.append("\ncaused by:\n");
			msg.append(e.what());
		}
    throw JNIException( msg );
  }

  jstring javaStr = static_cast<jstring>( env->CallObjectMethod( obj, toString ) );

  const char* strBuf = env->GetStringUTFChars( javaStr, 0 );
  string value = string( strBuf );

  env->ReleaseStringUTFChars( javaStr, strBuf );

  deleteLocalRef( env, javaStr );
  deleteLocalRef( env, objectClass );

  return value;
}

void print( jobject obj ) {
  cout << toString( obj ) << endl;
}

void printClass( jobject obj ) {
  JNIEnv* env = attach();
  jclass objClass = env->GetObjectClass( obj );
  print( objClass );
  deleteLocalRef( env, objClass );
}


bool hasShutdown() {
  return globalHasShutdown;
}

END_NAMESPACE_2( jace, helper )

