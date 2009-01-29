
#ifndef JACE_JMETHOD_H
#define JACE_JMETHOD_H

#ifndef JACE_OS_DEP_H
#include "jace/os_dep.h"
#endif

#ifndef JACE_NAMESPACE_H
#include "jace/namespace.h"
#endif

#ifndef JACE_JCLASS_H
#include "jace/JClass.h"
#endif

#ifndef JACE_JOBJECT_H
#include "jace/proxy/JObject.h"
#endif

#ifndef JACE_JVALUE_H
#include "jace/proxy/JValue.h"
#endif

#ifndef JACE_JARGUMENTS_H
#include "jace/JArguments.h"
#endif

#ifndef JACE_JNI_EXCEPTION_H
#include "jace/JNIException.h"
#endif

#ifndef JACE_JSIGNATURE_H
#include "jace/JSignature.h"
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

#ifndef JACE_TYPES_JVOID
#include "jace/proxy/types/JVoid.h"
#endif

#include <jni.h>

#include <string>
#include <vector>
#include <list>


BEGIN_NAMESPACE( jace )


/**
 * Transforms a JArguments to a vector of jvalue's.
 */
JACE_API std::vector<jvalue> toVector( const JArguments& arguments );

/**
 * Represents a java method.
 *
 * @author Toby Reyelts
 *
 */
template <class ResultType> class JMethod {
public:
  /**
   * Creates a new JMethod representing the method with the
   * given name, belonging to the given class.
   *
   */
  JMethod( const std::string& name ) : mName( name ), mMethodID( 0 ) {}

	/**
	 * Invokes the method with the given arguments.
	 * The method is invoked on the supplied object.
	 *
	 * @throws JNIException if an error occurs while trying to invoke the method.
	 * @throws a matching C++ proxy, if a java exception is thrown by the method.
	 *
	 */
	ResultType invoke( const ::jace::proxy::JObject& object, const JArguments& arguments ) {

#ifdef JACE_CHECK_NULLS
		if ( object.isNull() ) {
			throw JNIException( "[JMethod.invoke] Can not invoke an instance method on a null object." );
		}
#endif

		// Get the methodID for the method matching the given arguments.
		jmethodID methodID = getMethodID( object.getJavaJniClass(), arguments );

		// Call the method.
		JNIEnv* env = helper::attach();
		jobject resultRef;

		if ( arguments.asList().size() > 0 )
			resultRef = env->CallObjectMethodA( object.getJavaJniObject(), methodID, &toVector( arguments )[ 0 ] );
		else
			resultRef = env->CallObjectMethod( object.getJavaJniObject(), methodID );

		// Catch any java exception that occured during the method call, and throw it as a C++ exception.
		helper::catchAndThrow();

		ResultType result( resultRef );
		helper::deleteLocalRef( env, resultRef );

		return result;
	}


	/**
	 * Invokes the method with the given arguments.
	 * The method is invoked statically, on the supplied class.
	 *
	 * @throws JNIException if an error occurs while trying to invoke the method.
	 * @throws a matching C++ proxy, if a java exception is thrown by the method.
	 *
	 */
	ResultType invoke( const JClass* jClass, const JArguments& arguments ) {

		// Get the methodID for the method matching the given arguments.
		jmethodID methodID = getMethodID( jClass, arguments, true );

		/* Call the method.
		*/
		JNIEnv* env = helper::attach();
		jobject resultRef;

		if ( arguments.asList().size() > 0 )
			resultRef = env->CallStaticObjectMethodA( jClass->getClass(), methodID, &toVector( arguments )[ 0 ] );
		else
			resultRef = env->CallStaticObjectMethod( jClass->getClass(), methodID );

		// Catch any java exception that occured during the method call, and throw it as a C++ exception.
		helper::catchAndThrow();

		ResultType result( resultRef );
		helper::deleteLocalRef( env, resultRef );

		return result;
	}


	// private:

	/**
	 * Returns the jmethodID matching the signature for the given arguments.
	 *
	 */
	jmethodID getMethodID( const JClass* jClass, const JArguments& arguments, bool isStatic = false ) {

		// We cache the jmethodID locally, so if we've already found it, we don't need to go looking for it again.
		if ( mMethodID ) {
			return mMethodID;
		}

		// If we don't already have the jmethodID, we need to determine the signature of this method.
		JSignature signature( *ResultType::staticGetJavaJniClass() );
		typedef std::list< ::jace::proxy::JValue* > ValueList;
		ValueList args = arguments.asList();

		ValueList::iterator i = args.begin();
		ValueList::iterator end = args.end();

		for ( ; i != end; ++i ) {
			::jace::proxy::JValue* value = *i;
			signature << *value->getJavaJniClass();
		}

		std::string methodSignature = signature.toString();

		// Now that we have the signature for the method, we could look in a global cache for the
		// jmethodID corresponding to this method, but for now, we'll always find it.
		JNIEnv* env = helper::attach();

		if ( isStatic ) {
			mMethodID = env->GetStaticMethodID( jClass->getClass(), mName.c_str(), methodSignature.c_str() );
		}
		else {
			mMethodID = env->GetMethodID( jClass->getClass(), mName.c_str(), methodSignature.c_str() );
		}

		if ( mMethodID == 0 ) {
			std::string msg = "JMethod::getMethodID\n" \
				"Unable to find method <" + mName + "> with signature <" + methodSignature + ">";
			try
			{
				helper::catchAndThrow();
			}
			catch (JNIException e)
			{
				msg.append("\ncaused by:\n");
				msg.append(e.what());
			}
			throw JNIException( msg );
		}

		//  cout << "JMethod::getMethodID() - Found the method:" << endl;
		//  cout << "  <" << mName << "> with signature <" << methodSignature << "> for " << jClass->getName() << endl;

		return mMethodID;
	}


	std::string mName;
	jmethodID mMethodID;

};

END_NAMESPACE( jace )

/**
 * For those (oddball) compilers that need the template specialization
 * definitions in the header.
 */
#ifdef PUT_TSDS_IN_HEADER
  #include "jace/JMethod.tsd"
#else
  #include "jace/JMethod.tsp"
#endif

#endif // #ifndef JACE_JMETHOD_H


