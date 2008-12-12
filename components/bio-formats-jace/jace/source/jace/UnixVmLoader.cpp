
#include "jace/UnixVmLoader.h"

#ifdef JACE_GENERIC_UNIX

using ::jace::UnixVmLoader;
using ::jace::VmLoader;
using ::jace::JNIException;

using std::string;

#include <dlfcn.h>

BEGIN_NAMESPACE( jace )

UnixVmLoader::UnixVmLoader( std::string path_, jint jniVer ) : 
  jniVersion( jniVer ), path( path_ ), lib( 0 ) {

  createJavaVMPtr = 0;
  getCreatedJavaVMsPtr = 0; 
}

jint UnixVmLoader::createJavaVM( JavaVM **pvm, void **env, void *args ) {
  return createJavaVMPtr( pvm, env, args );
}

jint UnixVmLoader::getCreatedJavaVMs( JavaVM **vmBuf, jsize bufLen, jsize *nVMs ) {
  return getCreatedJavaVMsPtr( vmBuf, bufLen, nVMs );
}

void UnixVmLoader::loadVm() throw ( JNIException ) {

  lib = dlopen( path.c_str(), RTLD_NOW | RTLD_GLOBAL );

  if ( ! lib ) {
    string msg = "Unable to load the library at " + path;
    throw JNIException( msg );
  }

  createJavaVMPtr = ( CreateJavaVM_t ) dlsym( lib, "JNI_CreateJavaVM" );

  if ( ! createJavaVMPtr ) {
    string msg = "Unable to resolve the function, JNI_CreateJavaVM from library " + path;
    throw JNIException( msg );
  }

  getCreatedJavaVMsPtr = ( GetCreatedJavaVMs_t ) dlsym( lib, "JNI_GetCreatedJavaVMs" );

  if ( ! getCreatedJavaVMsPtr ) {
    string msg = "Unable to resolve the function, JNI_GetCreatedJavaVMs from library " 
      + path;
    throw JNIException( msg );
  }
}


void UnixVmLoader::unloadVm() {
  if ( lib ) {
    dlclose( lib );
    lib = 0;
  }
}

VmLoader* UnixVmLoader::clone() const {

  UnixVmLoader* loader = new UnixVmLoader( path, jniVersion );

  loader->lib = lib;
  loader->createJavaVMPtr = createJavaVMPtr;
  loader->getCreatedJavaVMsPtr = getCreatedJavaVMsPtr;

  return loader;
}

jint UnixVmLoader::version() {
  return jniVersion;
}

END_NAMESPACE( jace )

#endif // JACE_GENERIC_UNIX

