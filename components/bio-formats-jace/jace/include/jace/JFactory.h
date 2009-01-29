
#ifndef JACE_JFACTORY_H
#define JACE_JFACTORY_H

#ifndef JACE_OS_DEP_H
#include "jace/os_dep.h"
#endif

#ifndef JACE_NAMESPACE_H
#include "jace/namespace.h"
#endif

#include <jni.h>

#include <memory>
using std::auto_ptr;

BEGIN_NAMESPACE_2( jace, proxy )
class JValue;
END_NAMESPACE_2( jace, proxy )

BEGIN_NAMESPACE( jace )
class JClass;
END_NAMESPACE( jace )


BEGIN_NAMESPACE( jace )


/**
 * An interface for a factory that creates new instances
 * of a specific JValue subclass.
 *
 * @author Toby Reyelts
 *
 */
class JFactory {

public:

/**
 * Creates a new instance of the value type
 * for this JFactory.
 *
 */

/* We'd like to use the following definition, but due to a problem
 * with the Visual C++ compiler, this doesn't work.
 */
#if 0
  JACE_API virtual auto_ptr<JValue> create( jvalue val ) = 0;
#endif

JACE_API virtual ::jace::proxy::JValue* create( jvalue val ) = 0;


/**
 * Creates a new instance of the value type for this JFactory
 * and throws that instance.
 *
 * This method is equivalent to 
 *
 *   throw * ( JFactory::create( aValue ) ).get();
 *
 * except that the return value's real type is preserved and 
 * not sliced to a JValue upon being thrown.
 *
 */
JACE_API virtual void throwInstance( jvalue val ) = 0;

/**
 * Returns the class of which this factory
 * creates instances.
 *
 */
JACE_API virtual const ::jace::JClass* getClass() = 0;

/**
 * Destroys this JFactory.
 *
 */
JACE_API virtual ~JFactory();

};


END_NAMESPACE( jace )

#endif // #ifndef JACE_JFACTORY_H


