//
// showinfJNI.cpp
//

/*
OME Bio-Formats C++ bindings for native access to Bio-Formats Java library.
Copyright (c) 2008-@year@, UW-Madison LOCI.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the UW-Madison LOCI nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY UW-MADISON LOCI ''AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL UW-MADISON LOCI BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/*
IMPORTANT NOTE: Although this software is distributed according to a
"BSD-style" license, it requires the Bio-Formats Java library to do
anything useful, which is licensed under the GPL v2 or later.
As such, if you wish to distribute this software with Bio-Formats itself,
your combined work must be distributed under the terms of the GPL.
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
Finally, copy loci_tools.jar to the same folder and run one of:
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

  // NB: This program requires loci_tools.jar in the same directory.
  string classpath = "-Djava.class.path";
  const int numJars = 1;
  string jars[numJars] = {
    "loci_tools.jar"
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
