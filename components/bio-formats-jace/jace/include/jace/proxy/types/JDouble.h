
#ifndef JACE_TYPES_JDOUBLE_H
#define JACE_TYPES_JDOUBLE_H

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
 * A representation of the java primitive double.
 *
 * @author Toby Reyelts
 *
 */
class JACE_API JDouble : public JValue {


public:


/**
 * Creates a new JDouble with the given value.
 *
 */
JDouble( jvalue value );


/**
 * Creates a new JDouble with the given value.
 *
 */
JDouble( jdouble value );


/**
 * Destroys the existing java object.
 *
 */
virtual ~JDouble();

/**
 * Returns the value of this instance.
 *
 */
operator jdouble() const;


/**
 * Returns the value of this instance.
 *
 */
jdouble getDouble() const;


/**
 * Compares this instance to another.
 *
 */
bool operator==( const JDouble& value ) const;


/**
 * Compares this instance to another.
 *
 */
bool operator!=( const JDouble& value ) const;


/**
 * Compares this instance to another.
 *
 */
bool operator==( jdouble value ) const;


/**
 * Compares this instance to another.
 *
 */
bool operator!=( jdouble value ) const;


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

#endif // #ifndef JACE_TYPES_JDOUBLE_H


