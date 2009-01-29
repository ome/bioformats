
#ifndef JACE_JFIELD_PROXY_HELPER_H
#define JACE_JFIELD_PROXY_HELPER_H

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

#include "jni.h"

BEGIN_NAMESPACE_2( jace, JFieldProxyHelper )

JACE_API jobject assign( const jace::proxy::JObject& field, jobject parent, jfieldID fieldID );
JACE_API jobject assign( const jace::proxy::JObject& field, jclass parentClass, jfieldID fieldID );

END_NAMESPACE_2( jace, JFieldProxyHelper )

#endif
