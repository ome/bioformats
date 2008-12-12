
#ifndef JACE_JARRAY_HELPER
#define JACE_JARRAY_HELPER

#ifndef JACE_OS_DEP_H
#include "os_dep.h"
#endif

#ifndef JACE_NAMESPACE_H
#include "namespace.h"
#endif

#ifndef JACE_JOBJECT_H
#include "jace/proxy/JObject.h"
#endif

#ifndef JACE_JCLASS_H
#include "jace/JClass.h"
#endif

#ifndef JACE_TYPES_JINT_H
#include "jace/proxy/types/JInt.h"
#endif

#include "jni.h"

BEGIN_NAMESPACE_2( jace, JArrayHelper )

JACE_API jobjectArray newArray( int size, const jace::JClass* elementClass );
JACE_API jace::proxy::types::JInt getLength( jobject obj );
JACE_API jvalue getElement( jobject obj, int index );

END_NAMESPACE_2( jace, JArrayHelper )

#endif

