
#ifndef JACE_JNI_HELPER_H
#define JACE_JNI_HELPER_H

#ifndef JACE_OS_DEP_H
#include "jace/os_dep.h"
#endif

#ifndef JACE_NAMESPACE_H
#include "jace/namespace.h"
#endif

#ifndef JACE_JNI_EXCEPTION_H
#include "jace/JNIException.h"
#endif

#ifndef JACE_JFACTORY_H
#include "jace/JFactory.h"
#endif

#ifndef JACE_VM_LOADER
#include "jace/VmLoader.h"
#endif

#ifndef JACE_OPTION_LIST_H
#include "jace/OptionList.h"
#endif

BEGIN_NAMESPACE( jace )
class Peer;
END_NAMESPACE( jace )

#include <jni.h>


BEGIN_NAMESPACE_2( jace, helper )


/**
 * A namespace for helper functions which make it easier to 
 * use the JNI API.
 *
 * @author Toby Reyelts
 *
 */


/**
 * Creates a new Java Virtual Machine using the specified loader
 * with the specified options.
 *
 * To link with a virtual machine you may specify any dynamic loading
 * VmLoader (for example, UnixVmLoader or Win32VmLoader).
 *
 * This call results in a call to setVmLoader internally.
 *
 */
JACE_API void createVm( const ::jace::VmLoader& loader, 
               const ::jace::OptionList& options, 
               bool ignoreUnrecognized = true );

/**
 * Returns the current VmLoader.
 *
 */
JACE_API ::jace::VmLoader* getVmLoader();

/**
 * Sets the current VmLoader. This method can be used to implement a custom vm
 * loading policy outside of createVm.
 *
 */
JACE_API void setVmLoader( const ::jace::VmLoader& loader );

/**
 * Registers the ShutdownHook using Runtime.addShutdownHook().
 *
 * @param env the JNI environment
 */
JACE_API void registerShutdownHook( JNIEnv* env );

/**
 * Returns the current java virtual machine.
 *
 * @throws JNIException if an error occurs while trying to retrieve
 * the virtual machine.
 *
 */
JACE_API JavaVM* getJavaVM() throw ( ::jace::JNIException );


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
JACE_API JNIEnv* attach() throw ( ::jace::JNIException );


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
JACE_API JNIEnv* attach(const jobject threadGroup, const char* name, const bool daemon) throw ( ::jace::JNIException );


/**
 * Detaches the current thread from the virtual machine.
 *
 * @see DetachCurrentThread
 */
JACE_API void detach() throw ();


/**
 * Tells Jace that the Java virtual machine has been shutdown,
 * and that it shouldn't try to re-attach any more threads.
 * After calling this function, most other functions will fail.
 *
 * This method is most appropriately called after a call to DestroyJavaVM.
 *
 */
JACE_API void shutdown();


/**
 * A central point for allocating new local references.
 * These references must be deallocated by a call to deleteLocalRef.
 *
 * @throws JNIException if the local reference can not be allocated.
 */
JACE_API jobject newLocalRef( JNIEnv* env, jobject ref );


/**
 * A central point for deleting local references.
 *
 */
JACE_API void deleteLocalRef( JNIEnv* env, jobject localRef );


/**
 * A central point for allocating new global references.
 * These references must be deallocated by a call to deleteGlobalRef.
 *
 * @throws JNIException if the global reference can not be allocated.
 */
JACE_API jobject newGlobalRef( JNIEnv* env, jobject ref );


/**
 * A central point for deleting global references.
 *
 */
JACE_API void deleteGlobalRef( JNIEnv* env, jobject globalRef );


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
JACE_API void enlist( ::jace::JFactory* factory );


/**
 * Checks to see if a java exception has been thrown.
 * 
 * If an exception has been thrown, a corresponding C++ proxy
 * exception is constructed and thrown.
 * 
 */
JACE_API void catchAndThrow();

/**
 * Returns the Peer for a given java Peer.
 *
 */
JACE_API ::jace::Peer* getPeer( jobject jPeer );

/**
 * Returns the ClassLoader being used by the current thread.
 *
 */
JACE_API jobject getClassLoader();

/**
 * Sets the ClassLoader to be used by the current thread.
 *
 * By default, Jace uses the JNIEnv->FindClass() to load classes,
 * but if a thread ClassLoader is defined then it is used to load
 * classes instead. A thread ClassLoader must be defined under
 * Java Webstart, Applets or any other framework that makes use
 * of custom ClassLoaders to load classes.
 *
 * NOTE: You must setClassLoader( 0 ) to release the ClassLoader
 *       reference or detach() will do it for you on thread shutdown.
 *
 */
JACE_API void setClassLoader( jobject classLoader );

/**
 * Returns the result of calling Object.toString() on obj.
 * Useful for low level debugging.
 */
JACE_API std::string toString( jobject obj );

/**
 * Prints toString( obj ) to cout.
 */
JACE_API void print( jobject obj );

/**
 * Prints the result of calling Object.getClass().toString() on obj to cout.
 */
JACE_API void printClass( jobject obj );

/**
 * Checks to see if the system has shutdown.
 */
JACE_API bool hasShutdown();

END_NAMESPACE_2( jace, helper )

#endif
