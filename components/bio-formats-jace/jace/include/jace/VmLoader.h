
#ifndef JACE_VM_LOADER
#define JACE_VM_LOADER

#ifndef JACE_OS_DEP_H
#include "jace/os_dep.h"
#endif

#ifndef JACE_NAMESPACE_H
#include "jace/namespace.h"
#endif

#ifndef JACE_JNI_EXCEPTION_H
#include "jace/JNIException.h"
#endif

#include <jni.h>

BEGIN_NAMESPACE( jace )

/**
 * The base interface for virtual machine loaders.
 * 
 * To create a virtual machine using jace::helper::createVm(),
 * you need to specify a VmLoader.
 *
 * The default VmLoader is DefaultVmLoader. This loader statically links
 * to the VM, and thus works on every platform, although it limits your choice
 * to the virtual machine you compiled and linked against.
 *
 * In order to dynamically load a virtual machine, you must #define
 * JACE_WANT_DYNAMIC_LOAD. This keeps DefaultVmLoader from trying to statically
 * link with a VM. Then, you must instantiate your platform specific VmLoader.
 *
 * For Windows, you can use Win32VmLoader. It is capable of querying the registry
 * to discover different flavors of installed virtual machines.
 *
 * For generic flavors of Unix, you can use UnixVmLoader. It uses the standard
 * dlsym function to load a virtual machine.
 *
 * You may also subclass VmLoader for yourself if your platform isn't supported
 * or if you want tighter control over the loading and unloading process.
 *
 * @author Toby Reyelts
 */
class VmLoader {

  public:

  /**
   * Loads the virtual machine into memory.
   *
   */
  JACE_API virtual void loadVm() throw ( ::jace::JNIException ) = 0;

  /**
   * Unloads the virtual machine from memory.
   *
   */
  JACE_API virtual void unloadVm() = 0;

  /**
   * Returns the version of the virtual machine to be loaded.
	 * The implementation should cache this value because it is used on JVM shutdown
   * and might lead to deadlocks if jace::helper::attach() is called at that point.
   */
  JACE_API virtual jint version() = 0;

  /**
   * Creates a new dynamically allocated clone of this VmLoader.
   *
   */
  JACE_API virtual VmLoader* clone() const = 0;

  /**
   * Calls into JNI_CreateJavaVM.
   *
   */
  JACE_API virtual jint createJavaVM( JavaVM **pvm, void **env, void *args ) = 0;

  /**
   * Calls into JNI_GetCreatedJavaVMs.
   *
   */
  JACE_API virtual jint getCreatedJavaVMs( JavaVM **vmBuf, jsize bufLen, jsize *nVMs ) = 0;

  virtual ~VmLoader() {
  }

};

END_NAMESPACE( jace )

#endif // JACE_VM_LOADER

