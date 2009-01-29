
#ifndef JACE_JCLASS_IMPL_H
#define JACE_JCLASS_IMPL_H

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

#include <string>

BEGIN_NAMESPACE( jace )


/**
 * The implementation of the JClass interface.
 *
 * @author Toby Reyelts
 *
 */
class JClassImpl : public ::jace::JClass {


public:


/**
 * Creates a new JClassImpl with the given name, and 
 * type name.
 *
 * @param name - The name of this class, suitable for use
 * in a call to JNIEnv::FindClass.
 *
 * For example, "java/lang/Object"
 *
 * @param nameAsType The name of this class as a type, 
 * suitable for use in a call to JNIEnv::GetMethodID.
 *
 * For example, "Ljava/lang/Object;"
 *
 */
JACE_API JClassImpl( const std::string name, const std::string nameAsType );


/**
 * Creates a new JClassImpl with the given name.
 *
 * @param name - The name of the class, suitable for use
 * in a call to JNIEnv::FindClass.
 *
 * For example, "java/lang/Object".
 *
 * ------------------------------------------------------
 *
 * The type name for the class is created by preprending
 * "L" and appending ";" to name.
 *
 * For example,
 *
 *  JClassImpl( "java/lang/String" );
 *
 * is equivalent to
 *
 *  JClassImpl( "java/lang/String", "Ljava/lang/String;" );
 *
 */
// JClassImpl( const std::string& name );
JACE_API JClassImpl( const std::string name );

/**
 * Destroys this JClassImpl.
 *
 */
JACE_API virtual ~JClassImpl() throw ();


/**
 * Returns the name of this class. suitable for use in a call to
 * JNIEnv::FindClass.
 *
 * For example, "java/lang/Object".
 *
 */
JACE_API virtual const std::string& getName() const;


/**
 * Returns the name of this class as a type, suitable for use
 * in a call to JNIEnv::GetMethodID.
 *
 * For example, "Ljava/lang/Object;".
 *
 */
JACE_API virtual const std::string& getNameAsType() const;


/**
 * Returns the JNI representation of this class.
 *
 */
JACE_API virtual jclass getClass() const throw ( ::jace::JNIException );


/**
 * Creates a duplicate instance of this JClass.
 *
 */
JACE_API virtual std::auto_ptr<JClass> clone() const;


private:

std::string mName;
std::string mNameAsType;
mutable jclass mClass;

};


END_NAMESPACE( jace )

#endif
