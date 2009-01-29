
#ifndef JACE_TYPES_JBOOLEAN_H
#define JACE_TYPES_JBOOLEAN_H

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
 * A representation of the java primitive boolean.
 *
 * @author Toby Reyelts
 *
 */
class JBoolean : public ::jace::proxy::JValue {


public:


/**
 * Creates a new instance with the given value.
 *
 */
JACE_API JBoolean( jvalue value );


/**
 * Creates a new instance with the given value.
 *
 */
JACE_API JBoolean( jboolean value );


/**
 * Destroys the existing java object.
 *
 */
JACE_API virtual ~JBoolean();


/**
 * Returns the value of this instance.
 *
 */
JACE_API operator jboolean() const;

/**
 * Returns the value of this instance.
 *
 */
JACE_API jboolean getBoolean() const;


/**
 * Compares this JBoolean to another.
 *
 */
JACE_API bool operator==( const JBoolean& boolean_ ) const;


/**
 * Compares this JBoolean to another.
 *
 */
JACE_API bool operator!=( const JBoolean& boolean_ ) const;


/**
 * Compares this JBoolean to a jboolean.
 *
 */
JACE_API bool operator==( jboolean val ) const;


/**
 * Compares this JBoolean to a jboolean.
 *
 */
JACE_API bool operator!=( jboolean val ) const;


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

#endif // #ifndef JACE_TYPES_JBOOLEAN_H

