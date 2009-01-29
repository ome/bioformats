
#ifndef JACE_TYPES_JVOID
#define JACE_TYPES_JVOID

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
 * A class representing the java primitive type, void.
 *
 * @author Toby Reyelts
 *
 */
class JVoid : public JValue {


public:

/**
 * Returns the JClass for the Void type.
 *
 * @throw JNIException if an error occurs while trying to retrieve the class.
 *
 */
JACE_API virtual const ::jace::JClass* getJavaJniClass() const throw ( ::jace::JNIException );


/**
 * Returns the JClass for the Void type.
 *
 * @throw JNIException if an error occurs while trying to retrieve the class.
 *
 */
JACE_API static const ::jace::JClass* staticGetJavaJniClass() throw ( ::jace::JNIException );

};



END_NAMESPACE_3( jace, proxy, types )

#endif // #ifndef JACE_TYPES_JVOID


