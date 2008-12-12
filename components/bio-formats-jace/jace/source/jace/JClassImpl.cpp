
#include "jace/JClassImpl.h"

#ifndef JACE_JNI_HELPER_H
#include "jace/JNIHelper.h"
#endif

using std::string;
using std::auto_ptr;

BEGIN_NAMESPACE( jace )


/**
 * Creates a new JClassImpl with the given name, and 
 * type name.
 *
 * @param name - The name of this class, suitable for use
 * in a call to JNIEnv::FindClass.
 *
 * For example, "java/lang/Object"
 *
 * @param nameAsType The name of this class as a type, 
 * suitable for use in a call to JNIEnv::GetMethodID.
 *
 * For example, "Ljava/lang/Object;"
 *
 */
// JClassImpl::JClassImpl( const string& name, const string& nameAsType ) :
JClassImpl::JClassImpl( const string name, const string nameAsType ) :
  mName( name ), 
  mNameAsType( nameAsType ),
  mClass( 0 ) {
}


/**
 * Destroys this JClassImpl.
 *
 */
JClassImpl::~JClassImpl() throw () {

  if ( mClass ) {

    try {

      if ( helper::hasShutdown() ) {
        return;
      }

      JNIEnv* env = helper::attach();
      helper::deleteGlobalRef( env, mClass );
    }
    catch ( std::exception& e ) {

      ( void ) e; // Shut the compiler up.

      /* Purposely ignored.
       *
       * JClassImpls are often used statically, and don't undergo destruction until
       * after their VM has already been destroyed. At that time, the attach will fail.
       */

      // cout << "JClassImpl::~JClassImpl()" << endl;
      // cout << "Unable to release a global reference." << endl;
    }
  }
}


/**
 * Creates a new JClassImpl with the given name.
 *
 * @param name - The name of the class, suitable for use
 * in a call to JNIEnv::FindClass.
 *
 * For example, "java/lang/Object".
 *
 * ------------------------------------------------------
 *
 * The type name for the class is created by preprending
 * "L" and appending ";" to name.
 *
 * For example,
 *
 *  JClassImpl( "java/lang/String" );
 *
 * is equivalent to
 *
 *  JClassImpl( "java/lang/String", "Ljava/lang/String;" );
 *
 */
// JClassImpl::JClassImpl( const string& name ) : 
JClassImpl::JClassImpl( const string name ) : 
  mName( name ),
  mNameAsType( "L" + name + ";" ) {
}


/**
 * Returns the name of this class. suitable for use in a call to
 * JNIEnv::FindClass.
 *
 * For example, "java/lang/Object".
 *
 */
const string& JClassImpl::getName() const {
  return mName;
}


/**
 * Returns the name of this class as a type, suitable for use
 * in a call to JNIEnv::GetMethodID.
 *
 * For example, "Ljava/lang/Object;".
 *
 */
const string& JClassImpl::getNameAsType() const {
  return mNameAsType;
}


/**
 * Returns the JNI representation of this class.
 *
 */
jclass JClassImpl::getClass() const throw ( JNIException ) {

  if ( mClass ) {
    return mClass;
  }

  JNIEnv* env = helper::attach();

	jobject classLoader = jace::helper::getClassLoader();
	jclass localClass;

	if ( classLoader != 0 )
	{
		std::string binaryName( getName() );
		size_t i = 0;
		
		// Replace '/' by '.' in the name
		while (true) {

			i = binaryName.find( '/', i );
			if ( i != std::string::npos )
			{
				binaryName[i] = '.';
				++i;
			}
			else
				break;
		}
		jclass classLoaderClass = env->GetObjectClass( classLoader );
		jmethodID loadClass = env->GetMethodID( classLoaderClass, "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;" );
		if ( loadClass == 0 ) {
			string msg = "JClass::getClass - Unable to find the method JNIHelper::getClassLoader().loadClass()";
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
		jstring javaString = env->NewStringUTF( binaryName.c_str() );
		localClass = static_cast<jclass>( env->CallObjectMethod( classLoader, loadClass, javaString ) );
		env->DeleteLocalRef( javaString );
	}
	else
		localClass = env->FindClass( getName().c_str() );

  if ( ! localClass ) {
    string msg = "JClass::getClass - Unable to find the class <" + getName() + ">";
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

  mClass = static_cast<jclass>( helper::newGlobalRef( env, localClass ) );
  helper::deleteLocalRef( env, localClass );

  return mClass;
}


/**
 * Creates a duplicate instance of this JClass.
 *
 */
auto_ptr<JClass> JClassImpl::clone() const {
  return auto_ptr<JClass>( new JClassImpl( *this ) );
}

END_NAMESPACE( jace )
