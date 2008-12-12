

#include "jace/proxy/types/JShort.h"

#ifndef JACE_JCLASS_IMPL_H
#include "jace/JClassImpl.h"
#endif
using jace::JClassImpl;

BEGIN_NAMESPACE_3( jace, proxy, types )


JShort::JShort( jvalue value ) {
  setJavaJniValue( value );
}


JShort::JShort( jshort short_ ) {
  jvalue value;
  value.s = short_;
  setJavaJniValue( value );
}

JShort::~JShort() {}

JShort::operator jshort() const {
  return getJavaJniValue().s;
}

jshort JShort::getShort() const {
  return getJavaJniValue().s;
}

bool JShort::operator==( const JShort& short_ ) const {
  return short_.getShort() == getShort();
}

bool JShort::operator!=( const JShort& short_ ) const {
  return !( *this == short_ );
}

bool JShort::operator==( jshort val ) const {
  return val == getShort();
}

bool JShort::operator!=( jshort val ) const {
  return ! ( *this == val );
}

const JClass* JShort::staticGetJavaJniClass() throw ( JNIException ) {
  static JClassImpl javaClass( "short", "S" );
  return &javaClass;
}

const JClass* JShort::getJavaJniClass() const throw ( JNIException ) {
  return JShort::staticGetJavaJniClass();
}


END_NAMESPACE_3( jace, proxy, types )

