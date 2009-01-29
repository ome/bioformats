
#include "jace/proxy/JObject.h"

#ifndef JACE_JNI_HELPER_H
#include "jace/JNIHelper.h"
#endif

#ifndef JACE_JCLASS_IMPL_H
#include "jace/JClassImpl.h"
#endif
using jace::JClassImpl;

#ifndef JACE_JCONSTRUCTOR_H
#include "jace/JConstructor.h"
#endif
using jace::JConstructor;

#ifndef JACE_JMETHOD_H
#include "jace/JMethod.h"
#endif
using jace::JMethod;

#ifndef JACE_JARGUMENTS_H
#include "jace/JArguments.h"
#endif
using jace::JArguments;

#include <iostream>
using std::cout;
using std::endl;

#include <exception>
using std::exception;

BEGIN_NAMESPACE_2( jace, proxy )

/**
 * The static instance of the NoOp.
 */
const JObject::NoOp JObject::NO_OP;

/**
 * The static instance of the NoOp.
 */
const JObject::CopyConstructorSpecifier JObject::COPY_CONSTRUCTOR;

/**
 * Creates a new JObject wrapping the existing jvalue.
 *
 */
JObject::JObject( jvalue value ) {
  setJavaJniValue( value );
}

/**
 * Creates a new JObject.
 *
 */
JObject::JObject( jobject object ) {
  setJavaJniObject( object );
}


JObject::JObject() {
  jvalue newValue;
  newValue.l = 0;
  JValue::setJavaJniValue( newValue );
}


/**
 * Creates a new JObject that does not yet refer
 * to any java object.
 *
 * This constructor is provided for subclasses which
 * need to do their own initialization.
 *
 * @param noOp - A dummy argument that signifies that
 * this constructor should not do any work.
 *
 * All subclasses of JObject should provide this constructor
 * for their own subclasses.
 */
JObject::JObject( const NoOp& ) {

  /* By default, all objects start out with a null reference,
   * so, in case of an exception, destruction will not fail on
   * an unitialized reference.
   */
  jvalue newValue;
  newValue.l = 0;
  JValue::setJavaJniValue( newValue );
}


/**
 * Destroys the existing java object.
 *
 */
JObject::~JObject() throw () {

  try {
    jobject ref = getJavaJniValue().l;

    // Save an attach and a delete if we are a null object.
    if ( ref ) {
      JNIEnv* env = helper::attach();
      helper::deleteGlobalRef( env, ref );
    }
  }
  catch ( exception& e ) {
    cout << "JObject::~JObject - Unable to delete the global ref." << endl;
    cout << e.what() << endl;
  }
}


/** 
 * Returns the underlying JNI jobject for this JObject.
 *
 */
jobject JObject::getJavaJniObject() {
  return getJavaJniValue().l;
}


/** 
 * Returns the underlying JNI jobject for this JObject.
 *
 * This is simply a convenience method for retrieving the jobject
 * member from the jvalue returned from getJavaJniValue.
 *
 * Users of this method should be careful not to modify the
 * object through calls against the returned jobject.
 *
 */
jobject JObject::getJavaJniObject() const {
  return getJavaJniValue().l;
}


/**
 * Returns true if this JObject represents a null java reference.
 *
 * If this method returns true, it is not safe to call any proxy
 * method on this JObject. Doing so will invoke undefined behavior.
 *
 */
bool JObject::isNull() const {
  return getJavaJniObject() == 0;
}


/**
 * The implementation for operator=()
 *
 */
JObject& JObject::operator=( const JObject& object ) {
  setJavaJniObject( object.getJavaJniObject() );
  return *this;
}


/**
 * Sets the jobject for this JObject.
 *
 * This method is simply a convenience method for calling 
 * setValue( jvalue ) with a jobject.
 *
 */
void JObject::setJavaJniObject( jobject object ) throw ( JNIException ) {
  jvalue value;
  value.l = object;
  setJavaJniValue( value );
}


/**
 * This method sets the jobject for this JObject.
 *
 * @param object The jobject which represents this JObject.
 *
 * @throw JNIException if the JVM runs out of memory while 
 *   trying to create a new global reference.
 *
 */
void JObject::setJavaJniValue( jvalue value ) throw ( JNIException ) {

  JNIEnv* env = helper::attach();

  // If we have previously referenced another java object,
  // we release that reference here.
  jobject oldRef = getJavaJniValue().l;

  if ( oldRef ) {
    // helper::printClass( oldRef );
    // helper::print( oldRef );
    helper::deleteGlobalRef( env, oldRef );
  }

  // If this is a null ref, we save a little time by not creating
  // a new global ref.
  if ( ! value.l ) {
    // cout << "JObject::setJavaJniValue - Creating a NULL object." << endl;
    JValue::setJavaJniValue( value );
    return;
  }

  // cout << "JObject::setJavaJniValue - Creating a new reference to " << object << endl;
  jobject object = helper::newGlobalRef( env, value.l );
  // cout << "JObject::setJavaJniValue - Created the reference." << endl;

  jvalue newValue;
  newValue.l = object;

  // cout << "JObject::setJavaJniValue - Calling JValue::setJavaJniValue..." << endl;
  JValue::setJavaJniValue( newValue );
  // cout << "JObject::setJavaJniValue - Called JValue::setJavaJniValue." << endl;
}


/**
 * Constructs a new instance of this object 
 * with the given arguments.
 *
 * @return the JNI jobject representing the new object. 
 * The returned reference is a new local reference.
 *
 */
jobject JObject::newObject( const JArguments& arguments ) {
  return JConstructor( getJavaJniClass() ).invoke( arguments );
}


/**
 * Constructs a new instance of the given class
 * with the given arguments.
 *
 * @return the JNI jobject representing the new object. 
 * The returned reference is a local reference.
 *
 * @throws JNIException if a JNI error occurs while trying to locate the method.
 * @throws the corresponding C++ proxy exception, if a java exception
 *   is thrown during method execution.
 *
 */
jobject JObject::newObject( const JClass* jClass, const JArguments& arguments ) {
  return JConstructor( jClass ).invoke( arguments );
}

// Placeholder
const JClass* JObject::getJavaJniClass() const throw ( JNIException ) {
  return 0;
}

END_NAMESPACE_2( jace, proxy )

