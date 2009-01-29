
#ifndef JACE_TYPES_JBYTE_H
#define JACE_TYPES_JBYTE_H

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
 * A representation of the java primitive byte.
 *
 * @author Toby Reyelts
 *
 */
class JByte : public ::jace::proxy::JValue {


public:


/**
 * Creates a new JByte with the given value.
 *
 */
JACE_API JByte( jvalue value );


/**
 * Creates a new JByte with the given value.
 *
 */
JACE_API JByte( jbyte byte );


/**
 * Destroys the existing java object.
 *
 */
JACE_API virtual ~JByte();


/**
 * Returns the byte value of this java byte.
 *
 */
JACE_API operator jbyte() const;


/**
 * Returns the byte value of this java byte.
 *
 */
JACE_API jbyte getByte() const;


/**
 * Compares this JByte to another.
 *
 */
JACE_API bool operator==( const JByte& byte_ ) const;


/**
 * Compares this JByte to another.
 *
 */
JACE_API bool operator!=( const JByte& byte_ ) const;


/**
 * Compares this JByte to a jbyte.
 *
 */
JACE_API bool operator==( jbyte val ) const;


/**
 * Compares this JByte to a jbyte.
 *
 */
JACE_API bool operator!=( jbyte val ) const;


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

#endif // #ifndef JACE_TYPES_JBYTE_H


