//
// showinf.cpp
//

#include "jace/JNIHelper.h"

#include "jace/StaticVmLoader.h"
using jace::StaticVmLoader;

#include "jace/OptionList.h"
using jace::OptionList;

#include "jace/JArray.h"
using jace::JArray;

#include "jace/JNIException.h"
using jace::JNIException;

#include "jace/proxy/types/JBoolean.h"
using jace::proxy::types::JBoolean;

#include "jace/proxy/types/JByte.h"
using jace::proxy::types::JByte;

#include "jace/proxy/java/lang/String.h"
#include "jace/proxy/java/io/IOException.h"
#include "jace/proxy/loci/formats/FormatException.h"
#include "jace/proxy/loci/formats/ImageReader.h"
#include "jace/proxy/loci/formats/tools/ImageInfo.h"

using namespace jace::proxy::java::lang;
using namespace jace::proxy::java::io;
using namespace jace::proxy::loci::formats;
using namespace jace::proxy::loci::formats::tools;

#include <string>
using std::string;

#include <exception>
using std::exception;

#include <iostream>
using std::cout;
using std::endl;

/** A C++ wrapped version of the showinf command line utility. */
int main(int argc, const char *argv[]) {
  try {
    cout << "Creating JVM..." << endl;
    StaticVmLoader loader(JNI_VERSION_1_4);
    OptionList list;
    list.push_back( jace::ClassPath( "jace-runtime.jar:bio-formats.jar" ) );
    list.push_back(jace::CustomOption("-Xcheck:jni"));
    list.push_back(jace::CustomOption("-Xmx256m"));
    jace::helper::createVm(loader, list, false);
    cout << "JVM created." << endl;

    cout << "Arguments:" << endl;
    for (int i=0; i<argc; i++) cout << "\t#" << i << ": " << argv[i] << endl;

    //typedef JArray<String> StringArray;
    //StringArray args(argc - 1);
    //for (int i=1; i<argc; i++) args[i - 1] = argv[i];
    //JBoolean result = ImageInfo::testRead(args);

    if (argc < 2) {
      cout << "Please specify a filename on the command line." << endl;
    }
    else {
      String id = argv[1];
      cout << "Initializing " << id << endl;
      //ImageReader r();
      //r.setId(id);
      //int w = r.getWidth();
      //int h = r.getHeight();
      //cout << "Image planes are " << w << " x " << h << endl;
      //r.openBytes(0);
      //JBoolean result = ImageInfo::testRead(r, args);

      //return result ? 0 : 1;
    }
  }
  catch (FormatException& fe) {
    cout << fe << endl;
    return -1;
  }
  catch (IOException& ioe) {
    cout << ioe << endl;
    return -2;
  }
  catch (JNIException& jniException) {
    cout << "An unexpected JNI error occurred. " << jniException.what() << endl;
    return -3;
  }
  catch (std::exception& e) {
    cout << "An unexpected C++ error occurred. " << e.what() << endl;
    return -4;
  }

}
