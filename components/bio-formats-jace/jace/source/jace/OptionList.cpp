
#include "jace/OptionList.h"
using ::jace::OptionList;
using ::jace::Option;
using ::jace::SystemProperty;
using ::jace::Verbose;
using ::jace::JavaAgent;
using ::jace::CustomOption;
using ::jace::Hook;
using ::jace::VfprintfHook;
using ::jace::AbortHook;
using ::jace::ExitHook;

#include <string>
using std::string;

#include <cstring>

#include <vector>
using std::vector;

#ifdef JACE_BROKEN_USING_DECLARATION
  using std::vector<OptionList::OptionPtr>;
  using std::vector<std::string>;
#endif

#include <algorithm>
using std::copy;

#include <iterator>
using std::back_inserter;

OptionList::OptionList() : options() {
}

void OptionList::push_back( const Option& option ) {
  OptionPtr ptr( option.clone() );
  options.push_back( ptr );
}

size_t OptionList::size() const {
  return options.size();
}

std::vector<OptionList::OptionPtr>::const_iterator OptionList::begin() const {
  return options.begin();
}
 
std::vector<OptionList::OptionPtr>::const_iterator OptionList::end() const {
  return options.end();
}

namespace {
  char* stringDup( const char* str ) {
    size_t length = strlen( str );
    char* newStr = new char[ length + 1 ];
    strcpy( newStr, str );
    return newStr;
  }
}

JavaVMOption* OptionList::createJniOptions() const {

  JavaVMOption* jniOptions = new JavaVMOption[ size() ];
  
	std::vector<OptionPtr>::const_iterator it = begin();
  std::vector<OptionPtr>::const_iterator end_it = end();

  for ( int i = 0; it != end_it; ++it, ++i ) {
    jniOptions[ i ].optionString = stringDup( (*it)->stringValue().c_str() );
    jniOptions[ i ].extraInfo = (*it)->extraInfo();
  }

  return jniOptions;
}


void OptionList::destroyJniOptions( JavaVMOption* jniOptions ) const {

  for ( unsigned int i = 0; i < size(); ++i ) {
    delete[] (jniOptions[ i ].optionString);
  }

  delete[] jniOptions;
}

SystemProperty::SystemProperty( const std::string& name_, const std::string& value_ ) : 
  mName ( name_ ), mValue ( value_ ) {
}

SystemProperty::SystemProperty(const SystemProperty& other) :
	mName ( other.mName ), mValue ( other.mValue ) {
}

const std::string SystemProperty::name() {
  return mName;
}
  
const std::string SystemProperty::value() {
  return mValue;
}

const std::string SystemProperty::stringValue() const {
  return "-D" + mName + "=" + mValue;
}

void* SystemProperty::extraInfo() {
  return 0;
}

Option* SystemProperty::clone() const { 
  return new SystemProperty( mName, mValue ); 
}

std::string Verbose::toString(Verbose::ComponentType component) const
{
	switch (component)
	{
		case GC:
			return "gc";
		case JNI:
			return "jni";
		case CLASS:
			return "class";
		default:
			throw JNIException("Unknown component: " + component);
	}
}

Verbose::Verbose( ComponentType component ) : options( createVector( component ) ) {
}

Verbose::Verbose( std::vector<ComponentType>::const_iterator begin,
								  std::vector<ComponentType>::const_iterator end ) : 
	options ( createVector( begin, end ) ) {
}

Verbose::Verbose( const Verbose& other ) : 
	options ( other.options ) {
}

std::vector<Verbose::ComponentType> Verbose::createVector( ComponentType& component ) {
	std::vector<ComponentType> result;
	result.push_back(component);
	return result;
}

std::vector<Verbose::ComponentType> Verbose::createVector( std::vector<ComponentType>::const_iterator begin, 
																													 std::vector<ComponentType>::const_iterator end ) {
	std::vector<ComponentType> result;
	std::copy( begin, end, back_inserter( result ) );
	return result;
}

const std::string Verbose::stringValue() const {

  std::string buffer( "-verbose:" );

	vector<ComponentType>::const_iterator it = options.begin();
  vector<ComponentType>::const_iterator end = options.end();

  for ( ; it != end; ++it ) {
    buffer += toString(*it);
    if ( it + 1 != end ) {
      buffer += ",";
    }
  }

  return buffer;
}

void* Verbose::extraInfo() {
  return 0;
}

Option* Verbose::clone() const {
  return new Verbose( options.begin(), options.end() );
}

JavaAgent::JavaAgent( const std::string& path_ ) :
	mPath ( path_ ), mOptions ( "" ) {
}

JavaAgent::JavaAgent( const std::string& path_, const std::string& options_ ) :
	mPath ( path_ ), mOptions ( trim(options_) ) {
}

JavaAgent::JavaAgent( const JavaAgent& other) :
	mPath ( other.mPath ), mOptions ( other.mOptions ) {
}

std::string JavaAgent::trim( const std::string& text ) {
	// Trim Both leading and trailing spaces  
  size_t first = text.find_first_not_of(" \t"); // Find the first non-space character
  size_t last = text.find_last_not_of(" \t"); // Find the last non-space character
  
  // if all spaces or empty return an empty string
  if ( ( string::npos != first ) && ( string::npos != last ) )
		return text.substr( first, last - first + 1 );
	else
		return string();
}

const std::string JavaAgent::path() {
	return mPath;
}

const std::string JavaAgent::options() {
	return mOptions;
}

const std::string JavaAgent::stringValue() const {
	string result = "-javaagent:" + mPath;
	if (mOptions != "")
		result += "=" + mOptions;
	return result;
}

void* JavaAgent::extraInfo() {
  return 0;
}

Option* JavaAgent::clone() const {
  return new JavaAgent( mPath, mOptions );
}

CustomOption::CustomOption( const std::string& value_ ) : value( value_ ) {
}

CustomOption::CustomOption( const CustomOption& other ) :
	value ( other.value ) {
}

const std::string CustomOption::stringValue() const {
  return value;
}

void* CustomOption::extraInfo() {
  return 0;
}

Option* CustomOption::clone() const {
  return new CustomOption( value );
}


VfprintfHook::VfprintfHook( vfprintf_t hook_ ) : hook( hook_ ) {
}

const std::string VfprintfHook::stringValue() const {
  return "vfprintf";
}

void* VfprintfHook::extraInfo() {
  // Casting from a function pointer to an object pointer isn't valid C++
  // but JNI makes us do this.
  return ( void* ) hook;
}

Option* VfprintfHook::clone() const {
  return new VfprintfHook( hook );
}

ExitHook::ExitHook( exit_t hook_ ) : hook( hook_ ) {
}

const std::string ExitHook::stringValue() const {
  return "exit";
}

void* ExitHook::extraInfo() {
  // Casting from a function pointer to an object pointer isn't valid C++
  // but JNI makes us do this.
  return ( void* ) hook;
}

Option* ExitHook::clone() const {
  return new ExitHook( hook );
}

AbortHook::AbortHook( abort_t hook_ ) : hook( hook_ ) {
}

const std::string AbortHook::stringValue() const {
  return "abort";
}

void* AbortHook::extraInfo() {
  // Casting from a function pointer to an object pointer isn't valid C++
  // but JNI makes us do this.
  return ( void* ) hook;
}

Option* AbortHook::clone() const {
  return new AbortHook( hook );
}


