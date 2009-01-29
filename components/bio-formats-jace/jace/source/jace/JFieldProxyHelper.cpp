
#include "jace/JFieldProxyHelper.h"

using jace::proxy::JObject;
using jace::JClass;

#ifndef JACE_JNI_HELPER_H
#include "jace/JNIHelper.h"
#endif

BEGIN_NAMESPACE_2( jace, JFieldProxyHelper )


jobject assign( const JObject& field, jobject parent, jfieldID fieldID ) {

  JNIEnv* env = helper::attach();
  jobject object = field.getJavaJniObject();
  env->SetObjectField( parent, fieldID, object );

  return object;
}


jobject assign( const JObject& field, jclass parentClass, jfieldID fieldID ) {

  JNIEnv* env = helper::attach();
  jobject object = field.getJavaJniObject();

  env->SetStaticObjectField( parentClass, fieldID, object );

  return object;
}


END_NAMESPACE_2( jace, JFieldProxyHelper )
