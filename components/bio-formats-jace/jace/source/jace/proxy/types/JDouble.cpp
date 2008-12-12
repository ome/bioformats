

#include "jace/proxy/types/JDouble.h"

#ifndef JACE_JCLASS_IMPL_H
#include "jace/JClassImpl.h"
#endif
using jace::JClassImpl;

BEGIN_NAMESPACE_3( jace, proxy, types )


JDouble::JDouble( jvalue value ) {
  setJavaJniValue( value );
}


JDouble::JDouble( jdouble double_ ) {
  jvalue value;
  value.d = double_;
  setJavaJniValue( value );
}

JDouble::~JDouble() {}

JDouble::operator jdouble() const {
  return getJavaJniValue().d;
}

jdouble JDouble::getDouble() const {
  return getJavaJniValue().d;
}

bool JDouble::operator==( const JDouble& double_ ) const {
  return double_.getDouble() == getDouble();
}

bool JDouble::operator!=( const JDouble& double_ ) const {
  return !( *this == double_ );
}

bool JDouble::operator==( jdouble val ) const {
  return val == getDouble();
}

bool JDouble::operator!=( jdouble val ) const {
  return ! ( *this == val );
}

const JClass* JDouble::staticGetJavaJniClass() throw ( JNIException ) {
  static JClassImpl javaClass( "double", "D" );
  return &javaClass;
}

const JClass* JDouble::getJavaJniClass() const throw ( JNIException ) {
  return JDouble::staticGetJavaJniClass();
}


END_NAMESPACE_3( jace, proxy, types )

