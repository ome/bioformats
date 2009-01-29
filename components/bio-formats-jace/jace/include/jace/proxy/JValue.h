
#ifndef JACE_JVALUE_H
#define JACE_JVALUE_H

#ifndef JACE_OS_DEP_H
#include "jace/os_dep.h"
#endif

#ifndef JACE_NAMESPACE_H
#include "jace/namespace.h"
#endif

#ifndef JACE_JCLASS_H
#include "jace/JClass.h"
#endif

#ifndef JACE_JNI_EXCEPTION_H
#include "jace/JNIException.h"
#endif


BEGIN_NAMESPACE_2( jace, proxy )


/**
 * The base class for all java values.
 *
 * All java values are represented by the JValue class. For example,
 * the java primitive, int, is represented by the class, JInt, which 
 * derives directly from JValue. The java object, java.lang.String, 
 * is represented by the class, String, which has JValue at the 
 * top of its inheritance hierarchy.
 *
 * Classes which derive from JValue need to abide by the following set of rules:
 *
 * - All JValues must be constructable from a JNI jvalue.
 *   For example: JInt::JInt( jvalue value );
 *
 * - All JValues must implement the methods: 
 *
 *   static const JClass* staticGetJavaJniClass() throw ( JNIException ) and
 *   const JClass* getJavaJniClass() const throw ( JNIException )
 *
 *   staticGetJavaJniClass must return the same value as getJavaJniClass().
 * For example, Object implements staticGetJavaJniClass() and getJavaJniClass()
 * in a preferred fashion:
 *
 *   const JClass* Object::staticGetJavaJniClass() throw ( JNIException ) {
 *     static JClassImpl javaClass( "java/lang/Object" );
 *     return &javaClass;
 *   }
 *
 *   const JClass* Object::getJavaJniClass() const throw ( JNIException ) {
 *     return Object::staticGetJavaJniClass();
 *   }
 *
 * @author Toby Reyelts
 *
 */
class JValue {

public:

/**
 * Constructs a new JValue.
 *
 */
JACE_API JValue();


/**
 * Destroys the existing JValue.
 *
 */
JACE_API virtual ~JValue();


/** 
 * Returns the underlying JNI jvalue for this JValue.
 *
 */
JACE_API jvalue getJavaJniValue();


/** 
 * Returns the underlying JNI jvalue for this JValue.
 *
 * Callers of this method should be careful not to call modifying
 * methods on the returned jvalue.
 *
 * This method should really be protected, but there is a bug in
 * Visual C++ which requires us to make this public.
 *
 */
JACE_API jvalue getJavaJniValue() const;


/**
 * Retrieves the JClass for this JValue.
 *
 * @throw JNIException if an error occurs while trying to retrieve the class.
 *
 */
JACE_API virtual const ::jace::JClass* getJavaJniClass() const = 0;


protected:

/**
 * Sets the jvalue for this JValue.
 *
 * This method should only be called once during the lifetime
 * of this JValue, during the construction of a JValue.
 *
 * @param value The jvalue which represents this JValue.
 *
 * @throws JNIException if the jobject has already been set, 
 *   or if the JVM runs out of memory while trying to create 
 *   a new global reference.
 *
 */
JACE_API virtual void setJavaJniValue( jvalue value ) throw ( ::jace::JNIException );


private:

/* The underlying JNI value.
 */
jvalue mValue;

};


END_NAMESPACE_2( jace, proxy )

#endif
