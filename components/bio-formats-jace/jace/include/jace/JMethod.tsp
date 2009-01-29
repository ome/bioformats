
/**
 * Contains the template specialization prototypes for the template class, JMethod.
 *
 * This file is only for internal use of the JACE library.
 *
 */

BEGIN_NAMESPACE( jace )

template <>
JACE_API ::jace::proxy::types::JBoolean JMethod< ::jace::proxy::types::JBoolean>::invoke( const JClass* jClass, const JArguments& arguments );

template <>
JACE_API ::jace::proxy::types::JBoolean JMethod< ::jace::proxy::types::JBoolean>::invoke( const ::jace::proxy::JObject& object, const JArguments& arguments );

template <>
JACE_API ::jace::proxy::types::JByte JMethod< ::jace::proxy::types::JByte>::invoke( const JClass* jClass, const JArguments& arguments );

template <>
JACE_API ::jace::proxy::types::JByte JMethod< ::jace::proxy::types::JByte>::invoke( const ::jace::proxy::JObject& object, const JArguments& arguments );

template <>
JACE_API ::jace::proxy::types::JChar JMethod< ::jace::proxy::types::JChar>::invoke( const JClass* jClass, const JArguments& arguments );

template <>
JACE_API ::jace::proxy::types::JChar JMethod< ::jace::proxy::types::JChar>::invoke( const ::jace::proxy::JObject& object, const JArguments& arguments );

template <>
JACE_API ::jace::proxy::types::JDouble JMethod< ::jace::proxy::types::JDouble>::invoke( const JClass* jClass, const JArguments& arguments );

template <>
JACE_API ::jace::proxy::types::JDouble JMethod< ::jace::proxy::types::JDouble>::invoke( const ::jace::proxy::JObject& object, const JArguments& arguments );

template <>
JACE_API ::jace::proxy::types::JFloat JMethod< ::jace::proxy::types::JFloat>::invoke( const JClass* jClass, const JArguments& arguments );

template <>
JACE_API ::jace::proxy::types::JFloat JMethod< ::jace::proxy::types::JFloat>::invoke( const ::jace::proxy::JObject& object, const JArguments& arguments );

template <>
JACE_API ::jace::proxy::types::JInt JMethod< ::jace::proxy::types::JInt>::invoke( const JClass* jClass, const JArguments& arguments );

template <>
JACE_API ::jace::proxy::types::JInt JMethod< ::jace::proxy::types::JInt>::invoke( const ::jace::proxy::JObject& object, const JArguments& arguments );

template <>
JACE_API ::jace::proxy::types::JLong JMethod< ::jace::proxy::types::JLong>::invoke( const JClass* jClass, const JArguments& arguments );

template <>
JACE_API ::jace::proxy::types::JLong JMethod< ::jace::proxy::types::JLong>::invoke( const ::jace::proxy::JObject& object, const JArguments& arguments );

template <>
JACE_API ::jace::proxy::types::JShort JMethod< ::jace::proxy::types::JShort>::invoke( const JClass* jClass, const JArguments& arguments );

template <>
JACE_API ::jace::proxy::types::JShort JMethod< ::jace::proxy::types::JShort>::invoke( const ::jace::proxy::JObject& object, const JArguments& arguments );

template <>
JACE_API ::jace::proxy::types::JVoid JMethod< ::jace::proxy::types::JVoid>::invoke( const JClass* jClass, const JArguments& arguments );

template <>
JACE_API ::jace::proxy::types::JVoid JMethod< ::jace::proxy::types::JVoid>::invoke( const ::jace::proxy::JObject& object, const JArguments& arguments );


END_NAMESPACE( jace )

