
#ifndef JACE_PEER_H
#define JACE_PEER_H

#ifndef JACE_OS_DEP_H
#include "jace/os_dep.h"
#endif

#ifndef JACE_NAMESPACE_H
#include "jace/namespace.h"
#endif

#ifndef JACE_JNI_HELPER_H
#include "jace/JNIHelper.h"
#endif

#ifndef JACE_JCLASS_H
#include "jace/JClass.h"
#endif

BEGIN_NAMESPACE( jace )

/**
 * The base class for all Jace Peers.
 *
 * Peers should never be instantiated or called directly by any code.
 * Rather, Peers are created, destroyed, and invoked upon indirectly 
 * via the Java Virtual Machine when the corresponding Java class is
 * created, destroyed, or has native methods invoked upon it.
 *
 * @author Toby Reyelts
 *
 */
class Peer {

  public:

  /**
   * Creates a new Peer based on the specified object.
   *
   */
  JACE_API Peer( jobject obj );

  /**
   * Called by Jace to initialize the Peer immediately after it has been constructed.
   *
   * Empty by default, this should be overriden by Developers to provide
   * any Peer specific initialization they require.
   */
  JACE_API virtual void initialize();
  
  /**
   * Destroys this Peer.
   *
   */
  JACE_API virtual ~Peer();

  /**
   * Called by Jace to initialize the Peer immediately before it is destroyed.
   *
   * Empty by default, this should be overriden by Developers to provide
   * any Peer specific destruction they require.
   */
  JACE_API virtual void destroy();

  /**
   * Returns the class type of the Java Peer.
   *
   */
  JACE_API virtual const JClass* getJavaJniClass() const throw ( JNIException ) = 0;

  protected:

  /**
   * Returns a global reference to the Java object represented
   * by this Peer. Each allocated reference must be released 
   * with a call to releaseGlobalRef.
   *
   * Under normal circumstances, it should not be necessary to use this method,
   * as the Java object can be manipulated through the public interface
   * of the Peer.
   *
   */  
  JACE_API jobject getGlobalRef();

  /**
   * Releases the specified global reference that was allocated
   * by a call to getGlobalRef.
   *
   */
  JACE_API void releaseGlobalRef( jobject ref );
 
  private:

  /* The weak reference 
   */
  jweak mWeakRef;
};

END_NAMESPACE( jace )

#endif // #ifndef JACE_PEER_H
