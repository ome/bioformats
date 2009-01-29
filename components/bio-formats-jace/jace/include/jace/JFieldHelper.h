
#ifndef JACE_JFIELD_HELPER_H
#define JACE_JFIELD_HELPER_H

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

#include <string>

BEGIN_NAMESPACE( jace )

class JFieldHelper {

  public:

  JACE_API JFieldHelper( const std::string& name, const jace::JClass* typeClass );

  JACE_API jvalue getField( jace::proxy::JObject& object );
  JACE_API jvalue getField( const jace::JClass* jClass );
  JACE_API jfieldID getFieldID( const jace::JClass* parentClass, bool isStatic );
  JACE_API jfieldID getFieldID();

  private:
	/**
	 * Prevent copying.
	 */
	JFieldHelper& operator=(JFieldHelper&);

  jfieldID mFieldID;
  const std::string mName;
  const JClass* mTypeClass;
};

END_NAMESPACE( jace )

#endif
