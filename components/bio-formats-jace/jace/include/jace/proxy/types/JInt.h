
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
class JACE_API JInt : public JValue {

public:

/**
 * Creates a new instance with the given value.
 *
 */
JInt( jvalue value );


/**
 * Creates a new instance with the given value.
 *
 */
JInt( const jint int_ );


/**
 * Creates a new instance with the given value.
 *
 */
JInt( const ::jace::proxy::types::JByte& byte_ );


/**
 * Destroys the existing java object.
 *
 */
virtual ~JInt();


/**
 * Returns the value of this instance.
 *
 */
operator jint();


/**
 * Returns the value of this instance.
 *
 */
jint getInt() const;


friend std::ostream& operator<<( std::ostream& stream, const JInt& val );

/**
 * Compares this JInt to another.
 *
 */
bool operator==( const JInt& int_ ) const;


/**
 * Compares this JInt to another.
 *
 */
bool operator!=( const JInt& int_ ) const;


/**
 * Compares this JInt to a jint.
 *
 */
bool operator==( jint val ) const;


/**
 * Compares this JInt to a jint.
 *
 */
bool operator!=( jint val ) const;


/**
 * Returns the JClass for this class.
 *
 */
static const ::jace::JClass* staticGetJavaJniClass() throw ( ::jace::JNIException );

/**
 * Retrieves the JavaClass for this JObject.
 *
 * @throw JNIException if an error occurs while trying to retrieve the class.
 *
 */
virtual const ::jace::JClass* getJavaJniClass() const throw ( ::jace::JNIException );

};


END_NAMESPACE_3( jace, proxy, types )

#endif // #ifndef JACE_TYPES_JINT_H


