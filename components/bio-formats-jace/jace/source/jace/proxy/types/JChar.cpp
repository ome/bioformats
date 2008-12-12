

#include "jace/proxy/types/JChar.h"

#ifndef JACE_JCLASS_IMPL_H
#include "jace/JClassImpl.h"
#endif
using jace::JClassImpl;

#include <iostream>
using std::ostream;

BEGIN_NAMESPACE_3( jace, proxy, types )


JChar::JChar( jvalue value ) {
  setJavaJniValue( value );
}


JChar::JChar( jchar char_ ) {
  jvalue value;
  value.c = char_;
  setJavaJniValue( value );
}

JChar::~JChar() {}

JChar::operator jchar() const {
  return getJavaJniValue().c;
}

jchar JChar::getChar() const {
  return getJavaJniValue().c;
}

bool JChar::operator==( const JChar& char_ ) const {
  return char_.getChar() == getChar();
}

bool JChar::operator!=( const JChar& char_ ) const {
  return !( *this == char_ );
}

bool JChar::operator==( jchar val ) const {
  return val == getChar();
}

bool JChar::operator!=( jchar val ) const {
  return ! ( *this == val );
}

const JClass* JChar::staticGetJavaJniClass() throw ( JNIException ) {
  static JClassImpl javaClass( "char", "C" );
  return &javaClass;
}

const JClass* JChar::getJavaJniClass() const throw ( JNIException ) {
  return JChar::staticGetJavaJniClass();
}

ostream& operator<<( ostream& stream, const JChar& val ) {
  return stream << ( char ) val.getChar();
}


END_NAMESPACE_3( jace, proxy, types )

