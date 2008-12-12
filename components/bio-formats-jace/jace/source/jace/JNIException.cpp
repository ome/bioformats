
#include "jace/JNIException.h"

#include <string>
using std::string;


BEGIN_NAMESPACE( jace )

JNIException::JNIException( const string& value ) throw () : 
  BaseException( value ) {
}


JNIException::JNIException( const JNIException& rhs ) throw () : 
  BaseException( rhs ) {
}


JNIException& JNIException::operator=( const JNIException& rhs ) throw () {

  if ( &rhs == this ) {
    return *this;
  }

  ( ( BaseException& ) *this ) = ( BaseException& ) rhs;

  return *this;
}


END_NAMESPACE( jace )
