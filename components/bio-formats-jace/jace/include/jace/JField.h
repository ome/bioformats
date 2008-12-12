
#ifndef JACE_JFIELD_H
#define JACE_JFIELD_H

#ifndef JACE_OS_DEP_H
#include "jace/os_dep.h"
#endif

#ifndef JACE_NAMESPACE_H
#include "jace/namespace.h"
#endif

#ifndef JACE_JCLASS_H
#include "jace/JClass.h"
#endif

#ifndef JACE_JOBJECT_H
#include "jace/proxy/JObject.h"
#endif

#ifndef JACE_JNI_EXCEPTION_H
#include "jace/JNIException.h"
#endif

#ifndef JACE_JFIELD_PROXY_H
#include "jace/JFieldProxy.h"
#endif

#ifndef JACE_JFIELD_HELPER_H
#include "jace/JFieldHelper.h"
#endif

#ifndef JACE_TYPES_JBOOLEAN_H
#include "jace/proxy/types/JBoolean.h"
#endif

#ifndef JACE_TYPES_JBYTE_H
#include "jace/proxy/types/JByte.h"
#endif

#ifndef JACE_TYPES_JCHAR_H
#include "jace/proxy/types/JChar.h"
#endif

#ifndef JACE_TYPES_JDOUBLE_H
#include "jace/proxy/types/JDouble.h"
#endif

#ifndef JACE_TYPES_JFLOAT_H
#include "jace/proxy/types/JFloat.h"
#endif

#ifndef JACE_TYPES_JINT_H
#include "jace/proxy/types/JInt.h"
#endif

#ifndef JACE_TYPES_JLONG_H
#include "jace/proxy/types/JLong.h"
#endif

#ifndef JACE_TYPES_JSHORT_H
#include "jace/proxy/types/JShort.h"
#endif

#include <jni.h>

#include <string>

BEGIN_NAMESPACE( jace )

/**
 * Represents a java field.
 *
 * @author Toby Reyelts
 *
 */
template <class Type> class JField {


public:


/**
 * Creates a new JField representing the field with the
 * given name.
 *
 */
JField( const std::string& name ) : helper( name, Type::staticGetJavaJniClass() ) {
}

/**
 * Retrieves the field belonging to the given object.
 *
 * @throws JNIException if an error occurs while trying to retrieve the field.
 *
 */
JFieldProxy<Type> get( ::jace::proxy::JObject& object ) {
  jvalue value = helper.getField( object );
  JFieldProxy<Type> fieldProxy( helper.getFieldID(), value, object.getJavaJniObject() );
  JNIEnv* env = ::jace::helper::attach();
  jace::helper::deleteLocalRef( env, value.l );
  return fieldProxy;
}


/**
 * Retrieves the value of the static field belonging to the given class.
 *
 * @throws JNIException if an error occurs while trying to retrieve the value.
 *
 */
JFieldProxy<Type> get( const ::jace::JClass* jClass ) {
  jvalue value = helper.getField( jClass );
  JFieldProxy<Type> fieldProxy( helper.getFieldID(), value, jClass->getClass() );
  JNIEnv* env = ::jace::helper::attach();
  jace::helper::deleteLocalRef( env, value.l );
  return fieldProxy;
}

private:

::jace::JFieldHelper helper;

jfieldID getFieldID( const ::jace::JClass* parentClass, bool isStatic = false ) {
  return helper.getFieldID( parentClass, isStatic );
}

};

END_NAMESPACE( jace )

/**
 * For those (oddball) compilers that need the template specialization
 * definitions in the header.
 */
#ifdef PUT_TSDS_IN_HEADER
  #include "jace/JField.tsd"
#else
  #include "jace/JField.tsp"
#endif

#endif

