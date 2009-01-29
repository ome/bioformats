
/**
 * Contains the template specialization prototypes for the template functions in javacast.
 *
 * This file is only for internal use of the JACE library.
 *
 */

BEGIN_NAMESPACE( jace )

/**
 * Performs a safe cast from a JObject to a jobject.
 *
 * For example,
 *
 *  Object obj;
 *  jobject jObj = java_cast<jobject>( obj );
 *
 */
template <> inline
JACE_API jobject java_cast<jobject>( const ::jace::proxy::JObject& obj );

END_NAMESPACE( jace )

