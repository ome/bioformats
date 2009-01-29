
#include "jace/WrapperVmLoader.h"
#include "jace/JNIHelper.h"
using ::jace::VmLoader;
using ::jace::WrapperVmLoader;
using ::jace::JNIException;

BEGIN_NAMESPACE_2( jace, helper )
extern JavaVM* javaVM;
END_NAMESPACE_2( jace, helper )

#include <string>
using std::string;

#ifdef SUPPORTS_SSTREAM
  #include <sstream>
  using std::stringstream;
#else
  #include <strstream>
  using std::strstream;
#endif

// A quick hack to get a string representation for any value
// This should probably go into some framework utility class
//
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


WrapperVmLoader::WrapperVmLoader( JNIEnv* env ) { 

	jint result = env->GetJavaVM( &::jace::helper::javaVM );

  if ( result != 0 ) {
    string msg = string( "WrapperVmLoader::WrapperVmLoader\n" ) +
                  "Unable to retrieve the JVM from the JNIEnv* object. The specific JNI error code is " +
                  toString( result );
    throw JNIException( msg );
  }

	jniVersion = env->GetVersion();
}

WrapperVmLoader::WrapperVmLoader( ) { 

	if ( ::jace::helper::javaVM == 0 ) {
    string msg = string( "WrapperVmLoader::WrapperVmLoader\n" ) +
                  "javaVM must be non-null";
    throw JNIException( msg );
	}

  JNIEnv* env;
  jint result = ::jace::helper::javaVM->AttachCurrentThread( reinterpret_cast<void**>( &env ), 0 );

  if ( result != 0 ) {
    string msg = "JNIHelper::attach\n" \
                 "Unable to attach the current thread. The specific JNI error code is " +
                 toString( result );
    throw JNIException( msg );
  }
	
	jniVersion = env->GetVersion();
}

void WrapperVmLoader::loadVm() throw ( JNIException ) {
	// The JVM has already been loaded by someone else, so we do nothing.
}

void WrapperVmLoader::unloadVm() {
	// We didn't load the JVM, so we don't get to unload it.
}

jint WrapperVmLoader::version() {
	return jniVersion;
}

jint WrapperVmLoader::createJavaVM( JavaVM **pvm, void **env, void * ) {

	if ( pvm == 0 || env == 0 )
		return JNI_EINVAL;
	*env = helper::attach();
	*pvm = ::jace::helper::javaVM;
	return JNI_OK;
}

jint WrapperVmLoader::getCreatedJavaVMs( JavaVM **vmBuf, jsize bufLen, jsize *nVMs ) {
	
	if ( bufLen < 1 || vmBuf == 0 )
		return JNI_EINVAL;
	*vmBuf = ::jace::helper::javaVM;
	*nVMs = 1;

  return JNI_OK;
}

VmLoader* WrapperVmLoader::clone() const {
	return new WrapperVmLoader();
}

