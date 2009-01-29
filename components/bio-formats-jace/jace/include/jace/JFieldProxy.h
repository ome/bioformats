
#ifndef JACE_JFIELD_PROXY_H
#define JACE_JFIELD_PROXY_H

#ifndef JACE_OS_DEP_H
#include "jace/os_dep.h"
#endif

#ifndef JACE_NAMESPACE_H
#include "jace/namespace.h"
#endif

#ifndef JACE_JNI_HELPER_H
#include "jace/JNIHelper.h"
#endif

#ifndef JACE_JFIELD_PROXY_HELPER_H
#include "JFieldProxyHelper.h"
#endif

#ifndef JACE_TYPES_JBOOLEAN_H
#include "jace/proxy/types/JBoolean.h"
#endif

#ifndef JACE_TYPES_JBYTE_H
#include "jace/proxy/types/JByte.h"
#endif

#ifndef JACE_TYPES_JCHAR_H
#include "jace/proxy/types/JChar.h"
#endif

#ifndef JACE_TYPES_JDOUBLE_H
#include "jace/proxy/types/JDouble.h"
#endif

#ifndef JACE_TYPES_JFLOAT_H
#include "jace/proxy/types/JFloat.h"
#endif

#ifndef JACE_TYPES_JINT_H
#include "jace/proxy/types/JInt.h"
#endif

#ifndef JACE_TYPES_JLONG_H
#include "jace/proxy/types/JLong.h"
#endif

#ifndef JACE_TYPES_JSHORT_H
#include "jace/proxy/types/JShort.h"
#endif

BEGIN_NAMESPACE( jace )

/**
 * A JFieldProxy is a wrapper around a JField.
 *
 * A JFieldProxy makes sure that assignments can happen to class 
 * and instance fields. For example,
 *
 *
 * // Java class
 * public class Foo {
 *   public String bar;
 * }
 *
 * // C++ proxy class
 * class Foo : public Object {
 *   public:
 *   JFieldProxy<String> bar();
 * }
 *
 * // C++ code.
 * Foo.bar() = String( "Hello!" );
 *
 * @author Toby Reyelts
 *
 */
template <class FieldType> class JFieldProxy : public FieldType {

public:

/**
 * Creates a new JFieldProxy that belongs to the given object,
 * and represents the given value.
 *
 * This constructor should always be specialized away by subclasses.
 *
 */
JFieldProxy( jfieldID fieldID_, jvalue value, jobject parent_ ) :
  FieldType( value ), fieldID( fieldID_ ) {

  JNIEnv* env = ::jace::helper::attach();

  if ( parent_ ) {
    parent = ::jace::helper::newGlobalRef( env, parent_ ); 
  }
  else {
    parent = parent_;
  }

  parentClass = 0;
}


/**
 * Creates a new JFieldProxy that belongs to the given class,
 * and represents the given value. (The field is a static one).
 *
 * This constructor should always be specialized away by subclasses.
 *
 */
JFieldProxy( jfieldID fieldID_, jvalue value, jclass parentClass_ ) :
  FieldType( value ), fieldID( fieldID_ ) {

  parent = 0;
  JNIEnv* env = ::jace::helper::attach();
  parentClass = ::jace::helper::newGlobalRef( env, parentClass_ ); 
}


/**
 * Creates a new JFieldProxy that belongs to the given object,
 * and represents the given value.
 *
 * This copy constructor should always be specialized away by subclasses.
 *
 */
JFieldProxy( const JFieldProxy& object ) : 
  FieldType( object.getJavaJniValue() ) {
  JNIEnv* env = ::jace::helper::attach();
  if ( object.parent ) {
    parent = ::jace::helper::newGlobalRef( env, object.parent ); 
  }
  else {
    parent = 0;
  }

  if ( object.parentClass ) {
    parentClass = static_cast<jclass>( ::jace::helper::newGlobalRef( env, object.parentClass )); 
  }
  else {
    parentClass = 0;
  }
}


virtual ~JFieldProxy() throw() {

  if ( parent ) {
    try {
      JNIEnv* env = ::jace::helper::attach();
      ::jace::helper::deleteGlobalRef( env, parent );
    }
    catch ( std::exception& ) {
    }
  }

  if ( parentClass ) {
    try {
      JNIEnv* env = ::jace::helper::attach();
      ::jace::helper::deleteGlobalRef( env, parentClass );
    }
    catch ( std::exception& ) {
    }
  }
}

/**
 * If someone assigns to this proxy, they're really assigning
 * to the field.
 *
 */
FieldType& operator=( const FieldType& field ) {

  if ( parent ) {
    setJavaJniObject( JFieldProxyHelper::assign( field, parent, fieldID ) );
  }
  else {
    setJavaJniObject( JFieldProxyHelper::assign( field, parentClass, fieldID ) );
  }

  return *this;
}


private:
jobject parent;
jclass parentClass;
jfieldID fieldID;

};

END_NAMESPACE( jace )

/**
 * For those (oddball) compilers that need the template specialization
 * definitions in the header.
 */
#ifdef PUT_TSDS_IN_HEADER
  #include "jace/JFieldProxy.tsd"
#else
  #include "jace/JFieldProxy.tsp"
#endif

#endif // #ifndef JACE_JFIELD_PROXY_H

