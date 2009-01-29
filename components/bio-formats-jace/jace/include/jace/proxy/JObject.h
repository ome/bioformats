
#ifndef JACE_JOBJECT_H
#define JACE_JOBJECT_H

#ifndef JACE_OS_DEP_H
#include "jace/os_dep.h"
#endif

#ifndef JACE_NAMESPACE_H
#include "jace/namespace.h"
#endif

#ifndef JACE_JNI_HELPER_H
#include "jace/JNIHelper.h"
#endif

#ifndef JACE_JVALUE_H
#include "jace/proxy/JValue.h"
#endif

BEGIN_NAMESPACE( jace )
class JArguments;
END_NAMESPACE( jace )

BEGIN_NAMESPACE_2( jace, proxy )


/**
 * The abstract base class for all C++ proxy objects generated for java.
 * These proxy classes are a C++ representation of the corresponding
 * java class.
 *
 * ----------------------------------------------------------------------
 *
 * In addition to the rules in the JValue class, JObject's must
 * also adhere to the following set of rules:
 *
 * - All JObject's must be constructable from a JNI jobject.
 *   For example: String::String( jobject ).
 *
 * ----------------------------------------------------------------------
 *
 * JObjects may be created from existing jobjects, or they may
 * create new jobjects.
 *
 * For example, the code:
 * {
 *   String string;
 * }
 *
 * creates a new java.lang.String through JNI.
 *
 * The code:
 *
 * Java_com_foo_bar( jobject this, jstring aString ) {
 *   String( aString );
 * }
 *
 * does not create a new java.lang.String, but simply wraps an
 * existing java.lang.String in a C++ String class wrapper.
 *
 * In both cases, String creates a global reference to the jstring,
 * and does not release that global reference until it's lifetime
 * has ended.
 *
 * @author Toby Reyelts
 *
 */
class JObject : public ::jace::proxy::JValue {


public:


/**
 * Creates a new JObject wrapping the existing jvalue.
 *
 */
JACE_API JObject( jvalue value );


/**
 * Creates a new JObject wrapping the existing jobject.
 *
 */
JACE_API JObject( jobject object );


/**
 * Destroys the existing java object.
 *
 */
JACE_API virtual ~JObject() throw();


/**
 * Returns the underlying JNI jobject for this JObject.
 *
 * This is simply a convenience method for retrieving the jobject
 * member from the jvalue returned from getJavaJniValue.
 *
 * WARNING: The returned jobject is valid so long as its parent JObject is valid.
 * Given the code: <code>jobject myThread = Thread::currentThread.getJavaJniObject()</code>
 * the returned jobject will become invalid right after the assignment operation
 * because the enclosing Thread goes out of scope and destroys its associated jobject.
 *
 */
JACE_API jobject getJavaJniObject();


/**
 * Returns the underlying JNI jobject for this JObject. The jobject
 * reference has the same lifetime as this JObject.
 *
 * This is simply a convenience method for retrieving the jobject
 * member from the jvalue returned from getJavaJniValue.
 *
 * Users of this method should be careful not to modify the
 * object through calls against the returned jobject.
 *
 * This method should really be protected, but there is a bug in
 * Visual C++ which requires us to make this public.
 */
JACE_API jobject getJavaJniObject() const;


/**
 * Returns true if this JObject represents a null java reference.
 *
 * If this method returns true, it is not safe to call any proxy
 * method on this JObject. Doing so will invoke undefined behavior.
 *
 */
JACE_API bool isNull() const;


/**
 * An assignment operator for JObject's.
 */
JACE_API JObject& operator=( const JObject& object );


/**
 * Returns the JClass* that represents the static type of this class.
 * For example, for a String Java object, referred to by a C++ proxy Object,
 * this method returns JClassImpl( "java/lang/Object" ).
 *
 * The returned JClass* should not be deleted.
 *
 */
JACE_API virtual const JClass* getJavaJniClass() const throw ( JNIException );

protected:

/**
 * A dummy type used to signify that no operation
 * should take place. NoOp values are used to invoke
 * a no-op constructor.
 */
class NoOp {
  public:
  NoOp() {}
};


/**
 * A static instance of the NoOp class that can be used for
 * NoOp constructors.
 */
JACE_API static const NoOp NO_OP;


/**
 * A dummy type used to signify that the constructor is supposed
 * to represent a java copy constructor.
 */
class CopyConstructorSpecifier {
  public:
  CopyConstructorSpecifier() {}
};


/**
 * A static instance of the CopyConstructorSpecifier class that can be used for
 * copy constructors.
 */
JACE_API static const CopyConstructorSpecifier COPY_CONSTRUCTOR;


/**
 * This only exists for the broken way in which ElementProxys
 * must be handled. It operates in the same fashion as JObject( NoOp );
 *
 */
JACE_API JObject();


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
JACE_API JObject( const NoOp& noOp );


/**
 * Overridden so that a new global reference is created
 * for the JNI jobject which is specified in value.
 *
 * @param value The JNI jvalue which represents this JObject.
 * The jvalue must be must set with a jobject.
 *
 * @throws JNIException if the jobject has already been set,
 *   or if the JVM runs out of memory while trying to create
 *   a new global reference.
 *
 */
JACE_API virtual void setJavaJniValue( jvalue value ) throw ( JNIException );


/**
 * Sets the jobject for this JObject.
 *
 * This method is simply a convenience method for calling
 * setValue( jvalue ) with a jobject.
 *
 */
JACE_API void setJavaJniObject( jobject object ) throw ( JNIException );


/**
 * Constructs a new instance of this object
 * with the given arguments.
 *
 * @return the JNI jobject representing the new object.
 *
 * @throws JNIException if a JNI error occurs while trying to locate the method.
 * @throws the corresponding C++ proxy exception, if a java exception
 *   is thrown during method execution.
 *
 */
JACE_API jobject newObject( const JArguments& arguments );


/**
 * Constructs a new instance of the given class
 * with the given arguments.
 *
 * @return the JNI jobject representing the new object.
 *
 * @throws JNIException if a JNI error occurs while trying to locate the method.
 * @throws the corresponding C++ proxy exception, if a java exception
 *   is thrown during method execution.
 *
 */
JACE_API jobject newObject( const JClass* jClass, const JArguments& arguments );

};


END_NAMESPACE_2( jace, proxy )

#endif

