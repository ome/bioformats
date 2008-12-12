
#ifndef JACE_JENLISTER_H
#define JACE_JENLISTER_H

#ifndef JACE_OS_DEP_H
#include "jace/os_dep.h"
#endif

#ifndef JACE_NAMESPACE_H
#include "jace/namespace.h"
#endif

#ifndef JACE_JNI_HELPER_H
#include "jace/JNIHelper.h"
#endif

#ifndef JACE_JFACTORY_H
#include "jace/JFactory.h"
#endif

BEGIN_NAMESPACE( jace )

/**
 * An implementation of a JFactory that creates new instances
 * of JValues.
 *
 * This template class works with any subclass of JValue. It provides 
 * enlistment capabilities for Jace generated proxies.
 *
 * @author Toby Reyelts
 *
 */
template <class T> class JEnlister : public ::jace::JFactory {

public:

/**
 * Constructs this JEnlister and registers with the JNIHelper.
 *
 */
JEnlister() {
  helper::enlist( this );
}


/**
 * Creates a new instance of T.
 */

/* We'd like to use the following definition, but due to a problem
 * with the Visual C++ compiler, this doesn't work.
 */
#if 0

virtual auto_ptr<JValue> create( jvalue val ) {
  return auto_ptr<T>( new T( val ) );
}

#endif

virtual ::jace::proxy::JValue* create( jvalue val ) {
  return new T( val );
}


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
virtual void throwInstance( jvalue val ) {

  T t( val );
  JNIEnv* env = helper::attach();

  // We know that val is a jobject, because you can only throw exceptions.
  helper::deleteLocalRef( env, val.l );

  throw t;
}

/**
 * Returns the the class of which this factory
 * creates instances.
 *
 */
virtual const ::jace::JClass* getClass() {
  return T::staticGetJavaJniClass();
}


};


END_NAMESPACE( jace )

#endif // #ifndef JACE_JENLISTER_H

