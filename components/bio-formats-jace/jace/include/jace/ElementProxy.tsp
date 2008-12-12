
BEGIN_NAMESPACE( jace )

/**
 * Contains the template specialization prototypes for the template class, ElementProxy.
 *
 * This file is only for internal use of the JACE library.
 *
 */

template <>
ElementProxy< ::jace::proxy::types::JBoolean>::ElementProxy( jarray array, jvalue element, int index );

template <>
::jace::proxy::types::JBoolean& ElementProxy< ::jace::proxy::types::JBoolean>::operator=( const ::jace::proxy::types::JBoolean& type );

template <>
ElementProxy< ::jace::proxy::types::JByte>::ElementProxy( jarray array, jvalue element, int index );

template <>
::jace::proxy::types::JByte& ElementProxy< ::jace::proxy::types::JByte>::operator=( const ::jace::proxy::types::JByte& type );

template <>
ElementProxy< ::jace::proxy::types::JChar>::ElementProxy( jarray array, jvalue element, int index );

template <>
::jace::proxy::types::JChar& ElementProxy< ::jace::proxy::types::JChar>::operator=( const ::jace::proxy::types::JChar& type );

template <>
ElementProxy< ::jace::proxy::types::JDouble>::ElementProxy( jarray array, jvalue element, int index );

template <>
::jace::proxy::types::JDouble& ElementProxy< ::jace::proxy::types::JDouble>::operator=( const ::jace::proxy::types::JDouble& type );

template <>
ElementProxy< ::jace::proxy::types::JFloat>::ElementProxy( jarray array, jvalue element, int index );

template <>
::jace::proxy::types::JFloat& ElementProxy< ::jace::proxy::types::JFloat>::operator=( const ::jace::proxy::types::JFloat& type );

template <>
ElementProxy< ::jace::proxy::types::JInt>::ElementProxy( jarray array, jvalue element, int index );

template <>
::jace::proxy::types::JInt& ElementProxy< ::jace::proxy::types::JInt>::operator=( const ::jace::proxy::types::JInt& type );

template <>
ElementProxy< ::jace::proxy::types::JLong>::ElementProxy( jarray array, jvalue element, int index );

template <>
::jace::proxy::types::JLong& ElementProxy< ::jace::proxy::types::JLong>::operator=( const ::jace::proxy::types::JLong& type );

template <>
ElementProxy< ::jace::proxy::types::JShort>::ElementProxy( jarray array, jvalue element, int index );

template <>
::jace::proxy::types::JShort& ElementProxy< ::jace::proxy::types::JShort>::operator=( const ::jace::proxy::types::JShort& type );

END_NAMESPACE( jace )

