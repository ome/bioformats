
#include "jace/proxy/JValue.h"

BEGIN_NAMESPACE_2( jace, proxy )


/**
 * Constructs a new JValue.
 *
 */
JValue::JValue() {
  mValue.l = 0;
}


/** 
 * Returns the underlying JNI jvalue for this JValue.
 *
 */
jvalue JValue::getJavaJniValue() {
  return mValue;
}


/** 
 * Returns the underlying JNI jvalue for this JValue.
 *
 */
jvalue JValue::getJavaJniValue() const {
  return mValue;
}


void JValue::setJavaJniValue( jvalue value ) throw ( JNIException ) {
  mValue = value;
}

JValue::~JValue() {
}

END_NAMESPACE_2( jace, proxy )
