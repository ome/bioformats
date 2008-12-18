
/**
 * This program demonstrates how arrays can be used in Jace.
 *
 * @author Toby Reyelts
 *
 */

/**
 * Jace has an optional array checking mechanism that you can turn on 
 * by #defining JACE_CHECK_ARRAYS in your code. This must be turned
 * on or off for your entire project.
 *
 */
#define JACE_CHECK_ARRAYS

#include "jace/JNIHelper.h"

#include "jace/proxy/types/JInt.h"
using jace::proxy::types::JInt;

#include "jace/proxy/java/lang/String.h"
using jace::proxy::java::lang::String;

#include "jace/proxy/java/lang/Integer.h"
using jace::proxy::java::lang::Integer;

#include "jace/proxy/java/lang/Object.h"
using jace::proxy::java::lang::Object;

#include "jace/JArray.h"
using jace::JArray;

#include "jace/StaticVmLoader.h"
using jace::StaticVmLoader;

#include "jace/OptionList.h"
using jace::OptionList;

#include "jace/JNIException.h"
using jace::JNIException;

#include <string>
using std::string;

#include <exception>
using std::exception;

#include <iostream>
using std::cout;
using std::endl;

#include <algorithm>
using std::for_each;

#include <functional>
using std::unary_function;

struct print : public unary_function<void,Object> {
  void operator()( Object obj ) {
    cout << "[print] " << obj << endl;
  }
};

int main() {

  try {
    // Standard Vm setup
    StaticVmLoader loader( JNI_VERSION_1_2 );
    OptionList list;
    list.push_back( jace::ClassPath( "jace-runtime.jar" ) );
    list.push_back( jace::CustomOption( "-Xcheck:jni" ) );
    list.push_back( jace::CustomOption( "-Xmx16M" ) );
    jace::helper::createVm( loader, list, false );

    typedef JArray<String> StringArray;

    // Creates a new array of Java String with 1000 null elements
    StringArray strArray( 1000 );

    // Fills the array with hello strings.
    for ( int i = 0; i < strArray.length(); ++i ) {
      strArray[ i ] = "Hello " + String::valueOf( JInt( i ) );
    }

    // Prints the contents of the array.
    // You can use JArray::operator[] for random access,
    for ( int j = 0; j < strArray.length(); ++j ) {
      cout << strArray[ j ] << endl;
    }

    // Traverse the array again, but this time with JArray::Iterator
    // JArray::Iterator is preferred for non-random access,
    // because it allows Jace to perform smart caching.
    for ( StringArray::Iterator it = strArray.begin(); it != strArray.end(); ++it ) {
      *it = *it + " again";
      cout << *it << endl;
    }

    // JArray::Iterator conforms to the Standard C++ Library's concept
    // of a random access iterator, and can be used that way.
    for_each( strArray.begin(), strArray.end(), print() );

    // Demonstrate some more random access iterator usage
    for ( StringArray::Iterator it2 = strArray.begin(); it2 < strArray.end() - 2; ) {
      it2 += 2;
      cout << "Forward two: " << *it2 << endl;
      it2 -= 1;
      cout << "Back one: " << *it2 << endl;
    }

    // As stated above, Jace can check for bad array access
    // Here, we demonstrate some array access checking
    #ifdef JACE_CHECK_ARRAYS

      try {
        StringArray::Iterator it = strArray.begin( strArray.length() + 1 );
      }
      catch ( JNIException& e ) {
        cout << "Caught a bad iterator construction:" << endl;
        cout << e.what() << endl;
      }

      try {
        StringArray::Iterator it = strArray.end();
        ++it;
      }
      catch ( JNIException& e ) {
        cout << "Caught a bad iterator advancement." << endl;
        cout << e.what() << endl;
      }

      try {
        StringArray::Iterator it = strArray.begin();
        --it;
      }
      catch ( JNIException& e ) {
        cout << "Caught a bad iterator rewind." << endl;
        cout << e.what() << endl;
      }
 
      try {
        cout << strArray[ strArray.length() ] << endl;
      }
      catch ( JNIException& e ) {
        cout << "Caught a bad array index." << endl;
        cout << e.what() << endl;
      }
    #endif
  }
  catch ( exception& e ) {
    cout << e.what() << endl;
    return -1;
  }

  return 0;
}


