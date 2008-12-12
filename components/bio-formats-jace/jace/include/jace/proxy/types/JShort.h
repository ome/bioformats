
#ifndef JACE_TYPES_JSHORT_H
#define JACE_TYPES_JSHORT_H

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
 * A representation of the java primitive short.
 *
 * @author Toby Reyelts
 *
 */
class JACE_API JShort : public JValue {


public:


/**
 * Creates a new instance with the given value.
 *
 */
JShort( jvalue value );


/**
 * Creates a new instance with the given value.
 *
 */
JShort( jshort value );


/**
 * Destroys the existing java object.
 *
 */
virtual ~JShort();


/**
 * Returns the value of this instance.
 *
 */
operator jshort() const;


/**
 * Returns the value of this instance.
 *
 */
jshort getShort() const;


/**
 * Compares this instance to another.
 *
 */
bool operator==( const JShort& value ) const;


/**
 * Compares this instance to another.
 *
 */
bool operator!=( const JShort& value ) const;


/**
 * Compares this instance to another.
 *
 */
bool operator==( jshort value ) const;


/**
 * Compares this instance to another.
 *
 */
bool operator!=( jshort value ) const;


/**
 * Returns the JClass for this class.
 *
 */
static const ::jace::JClass* staticGetJavaJniClass() throw ( ::jace::JNIException );


/**
 * Returns the JClass for this instance.
 *
 */
virtual const ::jace::JClass* getJavaJniClass() const throw ( ::jace::JNIException );

};


END_NAMESPACE_3( jace, proxy, types )

#endif // #ifndef JACE_TYPES_JSHORT_H


