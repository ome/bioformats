#ifndef JACE_JAVACAST_H
#define JACE_JAVACAST_H

#ifndef JACE_JOBJECT_H
#include "jace/proxy/JObject.h"
#endif

#ifndef JACE_JCLASS_H
#include "jace/JClass.h"
#endif

#ifndef JACE_JNI_HELPER_H
#include "jace/JNIHelper.h"
#endif

#include <string>

BEGIN_NAMESPACE( jace )

/**
 * Performs a safe cast from one JObject subclass to another.
 *
 * For example,
 *
 *  Object stringAsObject = String( "Hello" );
 *  String string = java_cast<String>( stringAsObject );
 *
 * @throws JNIException if obj is not convertible to type T.
 *
 */
template <typename T> T java_cast( const ::jace::proxy::JObject& obj ) {
  return java_cast<T>( obj.getJavaJniObject() );
}


/**
 * Performs a safe cast from a jobject to a JObject subclass.
 *
 * For example,
 *
 *  foo( jobject jStringRef ) {
 *    String str = java_cast<String>( jStringRef );
 *  }
 *
 * @throws JNIException if obj is not convertible to type T.
 *
 */
template <typename T> T java_cast( jobject obj ) {

  JNIEnv* env = ::jace::helper::attach();

  if ( ! obj ) {
    return T( obj );
  }

  jclass argClass = env->GetObjectClass( obj );

  if ( ! argClass ) {
    std::string msg = "[java_cast] Failed to retrieve the class type for obj.";
    throw JNIException( msg );
  }

  const ::jace::JClass* resultClass = T::staticGetJavaJniClass();

  bool isValid = env->IsAssignableFrom( argClass, resultClass->getClass() );
  env->DeleteLocalRef( argClass );

  if ( isValid ) {
    return T( obj );
  }

  std::string msg = "Can not cast to " + resultClass->getName();
  throw JNIException( msg );
}


/**
 * Equal to Java's instanceof keyword.
 * Returns true if obj is non-null and can be cast to type T.
 *
 * For example,
 *
 *  Object stringAsObject = String( "Hello" );
 *
 *  if ( instanceof<String>( stringAsObject ) ) {
 *    String str = java_cast<String>( stringAsObject );
 *  }
 *
 *
 * @throws JNIException if obj is not convertible to type T.
 *
 */
template <typename T> bool instanceof( const ::jace::proxy::JObject& object ) {

  if ( object.isNull() ) {
    return false;
  }

  jobject obj = object.getJavaJniObject();

  JNIEnv* env = ::jace::helper::attach();

  jclass argClass = env->GetObjectClass( obj );

  if ( ! argClass ) {
    std::string msg = "[instanceof] Failed to retrieve the dynamic class type for object.";
    throw JNIException( msg );
  }

  const ::jace::JClass* resultClass = T::staticGetJavaJniClass();

  bool isValid = env->IsAssignableFrom( argClass, resultClass->getClass() );
  env->DeleteLocalRef( argClass );

  return isValid;
}

END_NAMESPACE( jace )

/**
 * For those (oddball) compilers that need the template specialization
 * definitions in the header.
 */
#ifdef PUT_TSDS_IN_HEADER
  #include "jace/javacast.tsd"
#else
  #include "jace/javacast.tsp"
#endif

#endif // #ifndef JACE_JAVACAST_H
