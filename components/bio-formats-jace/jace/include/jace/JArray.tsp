
BEGIN_NAMESPACE( jace )

/**
 * Contains the template specialization prototypes for the template class, ElementProxy.
 *
 * This file is only for internal use of the JACE library.
 *
 */

template <>
JACE_API JArray< ::jace::proxy::types::JBoolean>::JArray( int size );

template <>
JACE_API ElementProxy< ::jace::proxy::types::JBoolean> JArray< ::jace::proxy::types::JBoolean>::operator[]( const int& index );

template <>
JACE_API const ElementProxy< ::jace::proxy::types::JBoolean> JArray< ::jace::proxy::types::JBoolean>::operator[]( const int& index ) const;

template <>
JACE_API JArray< ::jace::proxy::types::JByte>::JArray( int size );

template <>
JACE_API ElementProxy< ::jace::proxy::types::JByte> JArray< ::jace::proxy::types::JByte>::operator[]( const int& index );

template <>
JACE_API const ElementProxy< ::jace::proxy::types::JByte> JArray< ::jace::proxy::types::JByte>::operator[]( const int& index ) const;

template <>
JACE_API JArray< ::jace::proxy::types::JChar>::JArray( int size );

template <>
JACE_API ElementProxy< ::jace::proxy::types::JChar> JArray< ::jace::proxy::types::JChar>::operator[]( const int& index );

template <>
JACE_API const ElementProxy< ::jace::proxy::types::JChar> JArray< ::jace::proxy::types::JChar>::operator[]( const int& index ) const;

template <>
JACE_API JArray< ::jace::proxy::types::JDouble>::JArray( int size );

template <>
JACE_API ElementProxy< ::jace::proxy::types::JDouble> JArray< ::jace::proxy::types::JDouble>::operator[]( const int& index );

template <>
JACE_API const ElementProxy< ::jace::proxy::types::JDouble> JArray< ::jace::proxy::types::JDouble>::operator[]( const int& index ) const;

template <>
JACE_API JArray< ::jace::proxy::types::JFloat>::JArray( int size );

template <>
JACE_API ElementProxy< ::jace::proxy::types::JFloat> JArray< ::jace::proxy::types::JFloat>::operator[]( const int& index );

template <>
JACE_API const ElementProxy< ::jace::proxy::types::JFloat> JArray< ::jace::proxy::types::JFloat>::operator[]( const int& index ) const;

template <>
JACE_API JArray< ::jace::proxy::types::JInt>::JArray( int size );

template <>
JACE_API ElementProxy< ::jace::proxy::types::JInt> JArray< ::jace::proxy::types::JInt>::operator[]( const int& index );

template <>
JACE_API const ElementProxy< ::jace::proxy::types::JInt> JArray< ::jace::proxy::types::JInt>::operator[]( const int& index ) const;

template <>
JACE_API JArray< ::jace::proxy::types::JLong>::JArray( int size );

template <>
JACE_API ElementProxy< ::jace::proxy::types::JLong> JArray< ::jace::proxy::types::JLong>::operator[]( const int& index );

template <>
JACE_API const ElementProxy< ::jace::proxy::types::JLong> JArray< ::jace::proxy::types::JLong>::operator[]( const int& index ) const;

template <>
JACE_API JArray< ::jace::proxy::types::JShort>::JArray( int size );

template <>
JACE_API ElementProxy< ::jace::proxy::types::JShort> JArray< ::jace::proxy::types::JShort>::operator[]( const int& index );

template <>
JACE_API const ElementProxy< ::jace::proxy::types::JShort> JArray< ::jace::proxy::types::JShort>::operator[]( const int& index ) const;

END_NAMESPACE( jace )

