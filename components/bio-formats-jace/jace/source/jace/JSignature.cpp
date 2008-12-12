
#include "jace/JSignature.h"

#include <string>
using std::string;

#include <list>
using std::list;


/* The template implementation.
 *
 */
BEGIN_NAMESPACE( jace )

/**
 * Constructs a new JSignature with the given return type.
 *
 */
JSignature::JSignature( const JClass& resultType ) :
  mTypes(),
  mResultType( &resultType ) { 
}


/**
 * Returns a string representation of this JSignature.
 *
 * The string representation is formatted as a fully qualified method signature.
 *
 * For example, the following JSignature:
 *
 *   JSignature( Void::staticGetJavaJniClass() ) signature 
 *     << String::staticGetJavaJniClass() 
 *     << Url::staticGetJavaJniClass();
 *
 * returns the following string from a call toString:
 *
 *   "(Ljava/lang/String;Ljava/net/URL;)V"
 *
 */
string JSignature::toString() const {

  string signature = "(";

  typedef list<const JClass*> ClassList;

  ClassList::const_iterator end = mTypes.end();

  for ( ClassList::const_iterator i = mTypes.begin();
        i != end;
        ++i ) {
    signature.append( (*i)->getNameAsType() );
  }

  signature.append( ")" );
  signature.append( mResultType->getNameAsType() );

  return signature;
}


/**
 * Adds a new argument type to the method signature.
 *
 * A JSignature may have any arbitrary number of argument types.
 *
 */
JSignature& JSignature::operator<<( const JClass& argumentType ) {
  mTypes.push_back( &argumentType );
  return *this;
}


END_NAMESPACE( jace )
