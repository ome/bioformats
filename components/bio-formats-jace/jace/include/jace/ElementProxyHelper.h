
#ifndef JACE_ELEMENT_PROXY_HELPER
#define JACE_ELEMENT_PROXY_HELPER

#ifndef JACE_OS_DEP_H
#include "os_dep.h"
#endif

#ifndef JACE_NAMESPACE_H
#include "namespace.h"
#endif

#ifndef JACE_JOBJECT_H
#include "jace/proxy/JObject.h"
#endif

#include "jni.h"

BEGIN_NAMESPACE_2( jace, ElementProxyHelper )

JACE_API void assign( const jace::proxy::JObject& element, int mIndex, jarray parent );

END_NAMESPACE_2( jace, ElementProxyHelper )

#endif
