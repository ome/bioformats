
#include "jace/JArguments.h"


#ifndef JACE_JVALUE_H
#include "jace/proxy/JValue.h"
#endif
using jace::proxy::JValue;

#include <list>
using std::list;


BEGIN_NAMESPACE( jace )

/**
 * Constructs a new argument list. 
 *
 */
JArguments::JArguments() : mList() {
}

/**
 * Adds a new JValue to the argument list.
 *
 * The order that the arguments are added is significant. They must 
 * be in the same order as the parameter types for the method.
 *
 */
JArguments& JArguments::add( JValue& value ) {
  mList.push_back( &value );
  return *this;
}


/**
 * An overloaded operator for JArguments::add.
 *
 * This method is provided to make code more maintainable. For example,
 *   JArguments arguments.add( arg1 ).add( arg2 ).add( arg3 ) ...
 *
 * can become,
 *   JArguments arguments << arg1
 *                        << arg2
 *                        << arg3;
 *
 * This syntax is noticeably different in temporary constructions:
 *
 *   method.invoke( JArguments().add( arg1 ).add( arg2 ).add( arg3 ) );
 *
 * versus
 *
 *   method.invoke( JArguments() << arg1 << arg2 << arg3 );
 *
 */
JArguments& JArguments::operator<<( JValue& value ) {
  return add( value );
}


/**
 * Returns this JArguments as a list of JValue*'s.
 *
 */
list<JValue*> JArguments::asList() const {
  return mList;
}


END_NAMESPACE( jace )
