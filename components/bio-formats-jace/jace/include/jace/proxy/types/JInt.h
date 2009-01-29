
#ifndef JACE_TYPES_JINT_H
#define JACE_TYPES_JINT_H

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

#ifndef JACE_TYPES_JBYTE_H
#include "jace/proxy/types/JByte.h"
#endif

#include <iostream>

BEGIN_NAMESPACE_3( jace, proxy, types )


/** 
 * A representation of the java primitive int.
 *
 * @author Toby Reyelts
 *
 */
class JInt : public JValue {

public:

/**
 * Creates a new instance with the given value.
 *
 */
JACE_API JInt( jvalue value );


/**
 * Creates a new instance with the given value.
 *
 */
JACE_API JInt( const jint int_ );


/**
 * Creates a new instance with the given value.
 *
 */
JACE_API JInt( const ::jace::proxy::types::JByte& byte_ );


/**
 * Destroys the existing java object.
 *
 */
JACE_API virtual ~JInt();


/**
 * Returns the value of this instance.
 *
 */
JACE_API operator jint();


/**
 * Returns the value of this instance.
 *
 */
JACE_API jint getInt() const;


/**
 * Compares this JInt to another.
 *
 */
JACE_API bool operator==( const JInt& int_ ) const;


/**
 * Compares this JInt to another.
 *
 */
JACE_API bool operator!=( const JInt& int_ ) const;


/**
 * Compares this JInt to a jint.
 *
 */
JACE_API bool operator==( jint val ) const;


/**
 * Compares this JInt to a jint.
 *
 */
JACE_API bool operator!=( jint val ) const;


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

friend std::ostream& operator<<( std::ostream& stream, const JInt& val );

};


END_NAMESPACE_3( jace, proxy, types )

#endif // #ifndef JACE_TYPES_JINT_H


