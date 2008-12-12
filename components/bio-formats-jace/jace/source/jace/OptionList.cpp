
#include "jace/OptionList.h"
using ::jace::OptionList;
using ::jace::Option;
using ::jace::SystemProperty;
using ::jace::Verbose;
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
typedef std::vector<OptionList::OptionPtr> OptionPtr;

#ifdef JACE_BROKEN_USING_DECLARATION
  using std::vector<OptionList::OptionPtr>;
  using std::vector<std::string>;
#endif

#include <algorithm>
using std::copy;

#include <iterator>
using std::back_inserter;

std::string Verbose::Gc( "gc" );
std::string Verbose::Jni( "jni" );
std::string Verbose::Class( "class" );

OptionList::OptionList() : options() {
}

void OptionList::push_back( const Option& option ) {
  OptionPtr ptr( option.clone() );
  options.push_back( ptr );
}

size_t OptionList::size() const {
  return options.size();
}

OptionList::iterator OptionList::begin() const {
  return iterator( options.begin() );
}
 
OptionList::iterator OptionList::end() const {
  return iterator( options.end() );
}

namespace {
  char* stringDup( const char* str ) {
    size_t length = strlen( str );
    char* newStr = new char[ length + 1 ];
    strcpy( newStr, str );
    return newStr;
  }
}

OptionList::iterator::iterator( vector<OptionPtr>::iterator it_ ) { it = it_; }
OptionList::iterator OptionList::iterator::operator++() { return it++; }
OptionList::iterator OptionList::iterator::operator++( int i ) { return ++it; }
OptionList::iterator OptionList::iterator::operator--() { return it--; }
OptionList::iterator OptionList::iterator::operator--( int i ) { return --it; }
OptionList::OptionPtr OptionList::iterator::operator*() { return *it; }
bool OptionList::iterator::operator!=( const OptionList::iterator& it_ ) { return it_.it != it; }

JavaVMOption* OptionList::createJniOptions() const {

  JavaVMOption* jniOptions = new JavaVMOption[ size() ];
  
  iterator it = begin();
  iterator end_it = end();

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
  mName( name_ ), mValue( value_ ) {
}
  
std::string SystemProperty::name() {
  return mName;
}
  
std::string SystemProperty::value() {
  return mValue;
}

std::string SystemProperty::stringValue() {
  return "-D" + mName + "=" + mValue;
}

void* SystemProperty::extraInfo() {
  return 0;
}

Option* SystemProperty::clone() const { 
  return new SystemProperty( mName, mValue ); 
}

Verbose::Verbose( const std::string& option ) : options() {
  options.push_back( option );
}

Verbose::Verbose( const std::string& o1, const std::string& o2 ) : options() {
  options.push_back( o1 );
  options.push_back( o2 );
}

Verbose::Verbose( const std::string& o1, const std::string& o2, const std::string& o3 ) : options() {
  options.push_back( o1 );
  options.push_back( o2 );
  options.push_back( o3 );
}

Verbose::Verbose( std::vector<std::string>::iterator begin, std::vector<std::string>::iterator end ) {
  std::copy( begin, end, back_inserter( options ) );
}

std::string Verbose::stringValue() {

  std::string buffer( "-verbose:" );

  vector<string>::iterator it = options.begin();
  vector<string>::iterator end = options.end();

  for ( ; it != end; ++it ) {
    buffer += *it;
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

CustomOption::CustomOption( const std::string& value_ ) : value( value_ ) {
}

std::string CustomOption::stringValue() {
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

std::string VfprintfHook::stringValue() {
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

std::string ExitHook::stringValue() {
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

std::string AbortHook::stringValue() {
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


