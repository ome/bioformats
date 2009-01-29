
#ifndef JACE_TYPES_JLONG_H
#define JACE_TYPES_JLONG_H

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

#ifndef JACE_TYPES_JINT_H
#include "jace/proxy/types/JInt.h"
#endif

BEGIN_NAMESPACE_3( jace, proxy, types )


/** 
 * A representation of the java primitive long.
 *
 * @author Toby Reyelts
 *
 */
class JLong : public JValue {

public:

/**
 * Creates a new instance with the given value.
 *
 */
JACE_API JLong( jvalue value );


/**
 * Creates a new instance with the given value.
 *
 */
JACE_API JLong( jlong long_ );


/**
 * Creates a new instance with the given value.
 *
 */
JACE_API JLong( const ::jace::proxy::types::JInt& int_ );


/**
 * Destroys the existing java object.
 *
 */
JACE_API virtual ~JLong();


/**
 * Returns the value of this instance.
 *
 */
JACE_API operator jlong() const;


/**
 * Returns the value of this instance.
 *
 */
JACE_API jlong getLong() const;


/**
 * Compares this instance to another.
 *
 */
JACE_API bool operator==( const JLong& long_ ) const;


/**
 * Compares this instance to another.
 *
 */
JACE_API bool operator!=( const JLong& long_ ) const;


/**
 * Compares this instance to a primitive.
 *
 */
JACE_API bool operator==( jlong val ) const;


/**
 * Compares this instance to a primitive.
 *
 */
JACE_API bool operator!=( jlong val ) const;


/**
 * Returns the JClass for this class.
 *
 */
JACE_API static const ::jace::JClass* staticGetJavaJniClass() throw ( ::jace::JNIException );

/**
 * Retrieves the JavaClass for this JObject.
 *
 * @throw JNIException if an error occurs while trying to retrieve the class.
 *
 */
JACE_API virtual const ::jace::JClass* getJavaJniClass() const throw ( ::jace::JNIException );

};


END_NAMESPACE_3( jace, proxy, types )

#endif // #ifndef JACE_TYPES_JLONG_H


