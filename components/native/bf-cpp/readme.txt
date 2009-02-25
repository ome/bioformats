Bio-Formats C++ bindings
------------------------

This package provides language bindings for calling into the Bio-Formats Java
library from C++ in a cross-platform manner. As of this writing the bindings
are functional only on Linux, but work is actively being done to get them
working on Windows and Mac OS X systems.


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

==> Java Runtime Environment -- http://java.sun.com/
Since Bio-Formats is written in Java, the JRE is ultimately necessary to
execute the Bio-Formats code. In addition, the Jace proxy class generator is
written in Java, so you must have a working Java installation both to compile
the Bio-Formats C++ bindings, and to use them at runtime.


INSTALLING COMPILE-TIME DEPENDENCIES -- ALL

Regardless of your platform, you will need to check out the Jace source code:

  svn co https://jace.svn.sourceforge.net/svnroot/jace/trunk /path/to/jace -r 6

Where /path/to/jace is the desired location of your Jace source code checkout.
The "-r 6" flag retrieves revision 6, which is the most recent known-to-work
revision.

The above command assumes you have the svn command line tool for Subversion
installed. It should also work fine to plug in the above information to any
graphical Subversion client (e.g., TortoiseSVN).


INSTALLING COMPILE-TIME DEPENDENCIES -- LINUX

To install many of these dependencies on Debian-based Linux, execute:

  sudo apt-get install ant cmake boost-thread-dev subversion sun-java6-jre

Other Linux flavors may have similar packages available; check your package
manager.


INSTALLING COMPILE-TIME DEPENDENCIES -- MAC OS X

Mac OS X users will need to visit the appropriate web sites and download and
install the relevant binaries for Ant, CMake and Subversion.

To install the Boost Thread library, follow these steps:

1) Visit www.boost.org in your web browser.
2) Click the "Getting Started Guide" link.
3) Click the "Getting Started on Unix variants" link in the lower right corner.
4) Download the Boost distribution using the link in the "1 Get Boost" section.
5) Unpack the Boost distribution into a temporary directory; e.g.:

  tar xjf boost_1_38_0.tar.bz2

6) Build the source using the "Easy Build and Install"; e.g.:

  cd boost_1_38_0
  sudo ./configure
  sudo make install

This will install Boost into a subdirectory of /usr/local; e.g.:

  /usr/local/include/boost-1_38

7) Depending on your version of Boost, you may need to edit CMakeLists.txt to
   tweak the Boost_ADDITIONAL_VERSIONS variable to include your version.


INSTALLING COMPILE-TIME DEPENDENCIES -- WINDOWS

Windows users will need to visit the appropriate web sites and download and
install the relevant binaries.

[TODO: Finish these instructions for Windows.]


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

If all goes well, the build system will:

1) Build the Jace Java and C++ libraries;
2) Generate the Bio-Formats C++ proxy classes;
3) Build the Bio-Formats C++ shared library;
4) Build the showinf command line tool, for testing the functionality.

Please be patient, as the build may require several minutes to complete.

Afterwards, a new subdirectory called "build" will contain the following files:

1) libjace.so / libjace.jnilib / jace.dll : Jace shared library
2) libbfjace.so / libbfjace.dylib / bfjace.dll : Bio-Formats C++ bindings
3) jace-runtime.jar : Jace Java classes needed at runtime
4) loci_tools.jar : Bio-Formats Java library needed at runtime
5) showinf / showinf.exe : Example command line application

Items 1-4 are necessary and required to deploy Bio-Formats with your C++
application. All other files, including the showinf program and various build
files generated by CMake, are not needed.

If you prefer, instead of using the loci_tools.jar bundle, you can provide
individual JAR files as appropriate for your application. For details, see:

  http://www.loci.wisc.edu/ome/formats-library.html

Please direct questions to the Bio-Formats team at bioformats@loci.wisc.edu.
