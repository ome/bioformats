
#include "jace/proxy/types/JVoid.h"

#ifndef JACE_JCLASS_IMPL_H
#include "jace/JClassImpl.h"
#endif
using jace::JClassImpl;

BEGIN_NAMESPACE_3( jace, proxy, types )

/**
 * Returns the JClass for this class.
 *
 */
const JClass* JVoid::staticGetJavaJniClass() throw ( JNIException ) {
  static JClassImpl javaClass( "void", "V" );
  return &javaClass;
}


/**
 * Returns the JClass for this class.
 *
 */
const JClass* JVoid::getJavaJniClass() const throw ( JNIException ) {
  return JVoid::staticGetJavaJniClass();
}


END_NAMESPACE_3( jace, proxy, types )

