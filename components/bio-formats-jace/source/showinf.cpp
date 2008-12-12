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
    list.push_back(jace::ClassPath("jace-runtime.jar"));
    list.push_back(jace::ClassPath("bio-formats.jar"));
    list.push_back(jace::CustomOption("-Xcheck:jni"));
    list.push_back(jace::CustomOption("-Xmx256m"));
    jace::helper::createVm(loader, list, false);
    cout << "JVM created." << endl;

    //ImageReader r();
    JArray<String> args(argc);
    for (int i=0; i<argc; i++) args[i] = argv[i];
    JBoolean result = ImageInfo::testRead(args);
    //ImageReader r();
    //JBoolean result = ImageInfo::testRead(r, args);

    return result ? 0 : 1;
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
