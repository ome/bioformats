
#include "jace/ElementProxyHelper.h"

using jace::proxy::JObject;

#ifndef JACE_JNI_HELPER_H
#include "jace/JNIHelper.h"
#endif

BEGIN_NAMESPACE_2( jace, ElementProxyHelper )

void assign( const JObject& element, int mIndex, jarray parent ) {
  JNIEnv* env = helper::attach();
  jobjectArray array = static_cast<jobjectArray>( parent );
  jobject val = element.getJavaJniObject();
  env->SetObjectArrayElement( array, mIndex, val );
}

END_NAMESPACE_2( jace, ElementProxyHelper )
