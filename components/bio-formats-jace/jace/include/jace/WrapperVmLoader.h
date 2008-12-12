
#ifndef JACE_OS_DEP_H
#include "jace/os_dep.h"
#endif

#ifndef JACE_WRAPPER_VM_LOADER
#define JACE_WRAPPER_VM_LOADER

#ifndef JACE_NAMESPACE_H
#include "jace/namespace.h"
#endif

#ifndef JACE_JNI_EXCEPTION_H
#include "jace/JNIException.h"
#endif

#ifndef JACE_VM_LOADER
#include "jace/VmLoader.h"
#endif

#include <jni.h>

#include <string>

BEGIN_NAMESPACE( jace )

/**
 * Wraps a preexisting Virtual Machine.
 *
 * @author Gili Tzabari
 *
 */
class WrapperVmLoader : public ::jace::VmLoader {

  public:

	/**
   * Wraps a preexisting JNIEnv* object in order to tap into its JVM.
   *
   * @param env - The JNIEnv* object to wrap
   *
   * @throws JNIException if an error occurs while trying to look up the VM.
   */
  JACE_API WrapperVmLoader( JNIEnv* env );
	/**
   * Wraps a preexisting JavaVM* object.
   *
   * @param javaVM - The JavaVM* object to wrap
   *
   * @throws JNIException if an error occurs while trying to look up the VM.
   */
  JACE_API WrapperVmLoader( JavaVM* javaVM );

	JACE_API void loadVm() throw ( ::jace::JNIException );
  JACE_API void unloadVm();
  ::jace::VmLoader* clone() const;
  virtual jint version();
  jint getCreatedJavaVMs( JavaVM **vmBuf, jsize bufLen, jsize *nVMs );
  jint createJavaVM( JavaVM **pvm, void **env, void *args );

	private:

	JavaVM* javaVM;
	jint jniVersion;
};

END_NAMESPACE( jace )

#endif // JACE_WRAPPER_VM_LOADER
