
#ifndef JACE_TYPES_JCHAR_H
#define JACE_TYPES_JCHAR_H

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

#include <iostream>

BEGIN_NAMESPACE_3( jace, proxy, types )


/** 
 * A representation of the java primitive char.
 *
 * @author Toby Reyelts
 *
 */
class JChar : public JValue {


public:


/**
 * Creates a new JChar with the given value.
 *
 */
JACE_API JChar( jvalue value );


/**
 * Creates a new JChar with the given value.
 *
 */
JACE_API JChar( jchar char_ );


/**
 * Destroys the existing java object.
 *
 */
JACE_API virtual ~JChar();


/**
 * Returns the char value of this java char.
 *
 */
JACE_API operator jchar() const;

/**
 * Returns the char value of this java char.
 *
 */
JACE_API jchar getChar() const;


/**
 * Compares this instance to another.
 *
 */
JACE_API bool operator==( const JChar& char_ ) const;


/**
 * Compares this instance to another.
 *
 */
JACE_API bool operator!=( const JChar& char_ ) const;


/**
 * Compares this instance to another.
 *
 */
JACE_API bool operator==( jchar val ) const;


/**
 * Compares this instance to another.
 *
 */
JACE_API bool operator!=( jchar val ) const;


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

/**
 * Support printing of characters.
 *
 */
friend std::ostream& operator<<( std::ostream& stream, const JChar& val );


};


END_NAMESPACE_3( jace, proxy, types )

#endif // #ifndef JACE_TYPES_JCHAR_H


