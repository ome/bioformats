/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

// An example of how to call Bio-Formats from C++ using raw JNI
// (i.e., without the C++ bindings).

/*
On all platforms, make sure the JAVA_HOME environment variable points to your
top-level Java SDK installation directory.

-------------------------------------------------------------------------------
Assuming you have Visual Studio 8.0 or similar installed,
compile on Windows with (all on one line):
  cl showinfJNI.cpp /EHsc
    /I "%JAVA_HOME%\include" /I "%JAVA_HOME%\include\win32"
    /link /libpath:"%JAVA_HOME%\lib" jvm.lib

To enable the CL command, you may first need to execute:
  "C:\Program Files\Microsoft Visual Studio 8\VC\bin\vcvars32.bat"

To compile and run, you will also need to add the following directory to
your PATH (Control Panel > System > Advanced > Environment Variables):
  %JAVA_HOME%\jre\bin\client

-------------------------------------------------------------------------------
Compile on Mac OS X with:
  c++ showinfJNI.cpp -o showinfJNI -framework JavaVM

-------------------------------------------------------------------------------
Compile on 32-bit Linux with:
  c++ showinfJNI.cpp -o showinfJNI \
    -I $JAVA_HOME/include -I $JAVA_HOME/include/linux \
    -L $JAVA_HOME/jre/lib/i386/client -ljvm

Depending on your flavor of 32-bit Linux, you may also need to execute:
  export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$JAVA_HOME/jre/lib/i386/client

-------------------------------------------------------------------------------
Compile on 64-bit Linux with:
  c++ showinfJNI.cpp -o showinfJNI \
    -I $JAVA_HOME/include -I $JAVA_HOME/include/linux \
    -L $JAVA_HOME/jre/lib/amd64/server -ljvm

Depending on your flavor of 64-bit Linux, you may also need to execute:
  export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$JAVA_HOME/jre/lib/amd64/server

-------------------------------------------------------------------------------
Finally, copy bioformats_package.jar to the same folder and
run one of:

  ./showinfJNI
  showinfJNI.exe

*/

#include <iostream>
using std::cout;
using std::endl;

#include <string>
using std::string;

#include <stdlib.h>
#include <jni.h>

#if defined (_WIN32)
#define PATHSEP string(";")
#else
#define PATHSEP string(":")
#endif

int main(int argc, char* argv[]) {
  JavaVM *jvm;
  JNIEnv *env;

  // NB: This program requires bioformats_package.jar in the same directory.
  string classpath = "-Djava.class.path";
  const int numJars = 1;
  string jars[numJars] = {
    "bioformats_package.jar"
  };
  for (int i=0; i<numJars; i++) {
    classpath += i == 0 ? "=" : PATHSEP;
    classpath += jars[i];
  }
  cout << "Classpath = " << classpath << endl;

  // get the default initialization arguments and set the class path
  JavaVMInitArgs vm_args;
  JNI_GetDefaultJavaVMInitArgs(&vm_args);
  const int numOptions = 1;
  JavaVMOption options[numOptions];
  options[0].optionString = (char*) classpath.c_str();
  //options[1].optionString = "-verbose:jni";
  vm_args.version = JNI_VERSION_1_4; // VM version 1.4
  vm_args.options = options;
  vm_args.nOptions = numOptions;

  // load and initialize a Java VM, return a JNI interface pointer in env
  if (JNI_CreateJavaVM(&jvm, (void**) &env, &vm_args)) {
    cout << "Failed to create the JVM" << endl;
    exit(1);
  }

  // invoke the ImageInfo.main(String[]) method using the JNI
  jclass imageInfoClass = env->FindClass("loci/formats/tools/ImageInfo");
  cout << "Got ImageInfo class: " << imageInfoClass << endl;
  jmethodID mid = env->GetStaticMethodID(imageInfoClass,
    "main", "([Ljava/lang/String;)V");
  cout << "Got main method: " << mid << endl;
  jclass stringClass = env->FindClass("java/lang/String");
  cout << "Got String class: " << stringClass << endl;

  jobjectArray args = env->NewObjectArray(argc - 1, stringClass, 0);
  for (int i=1; i<argc; i++) {
    jstring arg = env->NewStringUTF(argv[i]);
    env->SetObjectArrayElement(args, i - 1, arg);
  }
  cout << "Got object array: " << args << endl;

  env->CallStaticVoidMethod(imageInfoClass, mid, args);

  jvm->DestroyJavaVM();
}
