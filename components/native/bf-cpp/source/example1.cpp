
#include "jace/JNIHelper.h"

#include "jace/StaticVmLoader.h"
using jace::StaticVmLoader;

#include "jace/OptionList.h"
using jace::OptionList;

#include "jace/JArray.h"
using jace::JArray;

#include "jace/JNIException.h"
using jace::JNIException;

#include "jace/proxy/java/lang/String.h"
#include "jace/proxy/java/lang/System.h"
#include "jace/proxy/java/io/PrintWriter.h"
#include "jace/proxy/java/io/IOException.h"
#include "jace/proxy/java/io/PrintStream.h"

using namespace jace::proxy::java::lang;
using namespace jace::proxy::java::io;

#include <string>
using std::string;

#include <exception>
using std::exception;

#include <iostream>
using std::cout;
using std::endl;

/**
 * A prototypical Hello World example.
 *
 * This program demonstrates a few different ways to print "Hello World"
 * to standard output. For more information about this example, please read
 * the "Jace Developer's Guide".
 *
 */
int main() {

  try {
    StaticVmLoader loader( JNI_VERSION_1_2 );
    OptionList list;
    list.push_back( jace::ClassPath( "jace-runtime.jar" ) );
    list.push_back( jace::CustomOption( "-Xcheck:jni" ) );
    list.push_back( jace::CustomOption( "-Xmx16M" ) );
    jace::helper::createVm( loader, list, false );

    for ( int i = 0; i < 1000; ++i ) {

      String s1 = "Hello World";
      cout << s1 << endl;

      String s2 = std::string( "Hello World" );
      cout << s2 << endl;

      String s3( "Hello World" );
      PrintStream out( System::out() );
      out.println( s3 );

      PrintWriter writer( System::out() );
      writer.println( "Hello World" );
      writer.flush();
     
      cout << i << endl;
    }
  
    return 0;
  }
  catch ( IOException& ioe ) {
    cout << ioe << endl;
    return -1;
  }
  catch ( JNIException& jniException ) {
    cout << "An unexpected JNI error occured. " << jniException.what() << endl;
    return -2;
  }
  catch ( std::exception& e ) {
    cout << "An unexpected C++ error occured. " << e.what() << endl;
    return -3;
  }
}


