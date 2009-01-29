
#ifndef JACE_TYPES_JFLOAT_H
#define JACE_TYPES_JFLOAT_H

#ifndef JACE_OS_DEP_H
#include "jace/os_dep.h"
#endif

#ifndef JACE_NAMESPACE_H
#include "jace/namespace.h"
#endif

#ifndef JACE_JNI_HELPER_H
#include "jace/JNIHelper.h"
#endif

#ifndef JACE_JCLASS_H
#include "jace/JClass.h"
#endif

#ifndef JACE_JVALUE_H
#include "jace/proxy/JValue.h"
#endif

#ifndef JACE_JNI_EXCEPTION_H
#include "jace/JNIException.h"
#endif

BEGIN_NAMESPACE_3( jace, proxy, types )


/** 
 * A representation of the java primitive float.
 *
 * @author Toby Reyelts
 *
 */
class JFloat : public JValue {


public:


/**
 * Creates a new instance with the given value.
 *
 */
JACE_API JFloat( jvalue value );


/**
 * Creates a new instance with the given value.
 *
 */
JACE_API JFloat( jfloat value );


/**
 * Destroys the existing java object.
 *
 */
JACE_API virtual ~JFloat();


/**
 * Returns the value of this instance.
 *
 */
JACE_API operator jfloat() const;

/**
 * Returns the value of this instance.
 *
 */
JACE_API jfloat getFloat() const;


/**
 * Compares this instance to another.
 *
 */
JACE_API bool operator==( const JFloat& value ) const;


/**
 * Compares this instance to another.
 *
 */
JACE_API bool operator!=( const JFloat& value ) const;


/**
 * Compares this instance to another.
 *
 */
JACE_API bool operator==( jfloat value ) const;


/**
 * Compares this instance to another.
 *
 */
JACE_API bool operator!=( jfloat value ) const;


/**
 * Returns the JClass for this class.
 *
 */
JACE_API static const ::jace::JClass* staticGetJavaJniClass() throw ( ::jace::JNIException );


/**
 * Returns the JClass for this instance.
 *
 */
JACE_API virtual const ::jace::JClass* getJavaJniClass() const throw ( ::jace::JNIException );

};


END_NAMESPACE_3( jace, proxy, types )

#endif // #ifndef JACE_TYPES_JFLOAT_H


