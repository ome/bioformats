
#ifndef JACE_JARGUMENTS_H
#define JACE_JARGUMENTS_H

#ifndef JACE_OS_DEP_H
#include "jace/os_dep.h"
#endif

#ifndef JACE_NAMESPACE_H
#include "jace/namespace.h"
#endif

#ifndef JACE_JVALUE_H
#include "jace/proxy/JValue.h"
#endif

#include <list>

BEGIN_NAMESPACE( jace )

/**
 * Represents the list of arguments for a java method.
 *
 * @author Toby Reyelts
 *
 */
class JArguments {

public:

/**
 * Constructs a new argument list.
 *
 */
JACE_API JArguments();

/**
 * Adds a new JValue to the argument list.
 *
 * The order that the arguments are added is significant. They must 
 * be in the same order as the parameter types for the method.
 *
 */
JACE_API JArguments& add( ::jace::proxy::JValue& value );


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
JACE_API JArguments& operator<<( ::jace::proxy::JValue& value );


/**
 * Returns this JArguments as a list of JValue*'s.
 *
 */
JACE_API std::list< ::jace::proxy::JValue*> asList() const;


private:

std::list< ::jace::proxy::JValue*> mList;

};


END_NAMESPACE( jace )

#endif
