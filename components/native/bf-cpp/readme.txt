Bio-Formats C++ bindings
------------------------

This package provides language bindings for calling into the Bio-Formats Java
library from C++ in a cross-platform manner. As of this writing the bindings
are functional with GCC on Linux and Mac OS X systems, as well as with Visual
C++ 2005 and Visual C++ 2008 on Windows.


LICENSE

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

IMPORTANT NOTE: Although this software is distributed according to a
"BSD-style" license, it requires the OME Bio-Formats Java library to do
anything useful, which is licensed under the GPL v2 or later.
As such, if you wish to distribute this software with Bio-Formats itself,
your combined work must be distributed under the terms of the GPL.


LIST OF COMPILE-TIME DEPENDENCIES

To build the Bio-Formats C++ bindings, the following modules are required:

==> Apache Ant -- http://ant.apache.org/
Ant is a cross-platform, Java-based build tool. As with the other components in
LOCI's Java software repository, Ant is used for the top-level build system.

==> CMake -- http://www.cmake.org/
CMake is a cross-platform, open source build system generator, commonly used to
build C++ projects in a platform-independent manner. CMake supports GNU make
as well as Microsoft Visual Studio, allowing the Bio-Formats C++ bindings to be
compiled on Windows, Mac OS X, Linux and potentially other platforms.

==> Jace -- http://sourceforge.net/projects/jace/
Jace is a set of C++ and Java libraries and programs that simplify and empower
calling into Java code via JNI from C++. It is used to provide a set of C++
proxy classes corresponding to the Bio-Formats Java library classes.

==> Boost Thread -- http://www.boost.org/
Boost is a project providing open source portable C++ source libraries. It has
become a suite of de facto standard libraries for C++. Jace requires the Boost
Thread module in order to handle C++ threads in a platform independent way.

==> Subversion -- http://subversion.tigris.org/
Subversion is not strictly necessary, but is useful for checking out the
Jace source code (see "Installing Compile-Time Dependencies" below).

==> Java Development Kit -- http://java.sun.com/
At runtime, only the JRE is necessary to execute the Bio-Formats code.
However, at compile time, the full J2SE development kit is required for two
reasons. First, the Jace proxy class generator is written in Java, so Ant needs
a working Java compiler to compile Jace itself. Second, on Windows, only the
JDK comes bundled with the JVM shared library (jvm.lib) necessary to link with
Java.


INSTALLING COMPILE-TIME DEPENDENCIES

Regardless of your platform, you will need to check out the Jace source code:

  svn co -r 39 https://jace.svn.sourceforge.net/svnroot/jace/trunk /path/to/jace

Where /path/to/jace is the desired location of your Jace source code checkout.
The "-r 39" matters, since Jace revisions 40 and later may not work with bf-cpp.

As of this writing, the most recent revision was 52, and it does not compile
on some systems. We currently recommend revision 39.

If you attempt to use revision 46 or later, the Jace directory structure
is different, and you will need to edit two paths:
  1. jace-java.dir in build.properties
  2. JACE_CPP_DIR in jace/Prerequisites.cmake

The above command assumes you have the svn command line tool for Subversion
installed. It should also work fine to plug in the above information to any
graphical Subversion client (e.g., TortoiseSVN).

For more information on installing dependencies, see the readme file for your
specific platform: readme-linux.txt, readme-macosx.txt or readme-windows.txt.


HOW TO COMPILE

Once you have the required tools and libraries installed, you must first build
the loci_tools.jar library:

1) Change to the root directory of the checkout;
2) Execute the command:

  ant tools

Finally, you can compile the Bio-Formats C++ bindings:

1) Change to the components/native/bf-cpp directory;
2) Execute the command:

  ant -Djace.home=/path/to/jace

Where /path/to/jace is the location of your Jace source code checkout.
Do not use a relative path, and use forward slashes, even on Windows.

If all goes well, the build system will:

1) Build the Jace Java and C++ libraries;
2) Generate the Bio-Formats C++ proxy classes;
3) Build the Bio-Formats C++ shared library;
4) Build the showinf command line tool, for testing the functionality.

Please be patient, as the build may require several minutes to complete.


BUILD RESULTS

Afterwards, the build subdirectory will contain the following files:

1) libjace.so / libjace.jnilib / jace.dll : Jace shared library
2) libbfcpp.so / libbfcpp.dylib / bfcpp.dll : Bio-Formats C++ bindings
3) jace-runtime.jar : Jace Java classes needed at runtime
4) loci_tools.jar : Bio-Formats Java library needed at runtime
5) showinf / showinf.exe : Example command line application

Items 1-4 are necessary and required to deploy Bio-Formats with your C++
application. All other files, including the showinf program and various build
files generated by CMake, are not needed.

If you prefer, instead of using the loci_tools.jar bundle, you can provide
individual JAR files as appropriate for your application. For details, see:

  http://www.loci.wisc.edu/bio-formats/bio-formats-java-library

Please direct questions to the Bio-Formats team at bioformats@loci.wisc.edu.
