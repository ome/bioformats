INSTALLING COMPILE-TIME DEPENDENCIES -- WINDOWS

Windows users will need to visit the appropriate web sites and download and
install the relevant binaries for Ant, CMake, Subversion, and Sun's Java
Development Kit.

The easiest way to install the Boost Thread library on Windows is to use the
free installer from BoostPro:

  http://www.boostpro.com/products/free

When running the installer:

  * Under "Compilers," check the version of Visual C++ matching your system.
  * Under "Variants," check all eight boxes.
  * When choosing components, check "Boost DateTime" and "Boost Thread."

Once all the prerequisites are installed, you must update your PATH variable
(Control Panel > System > Advanced > Environment Variables), adding the
following directories:

  * C:\Program Files\apache-ant-1.7.0\bin
  * C:\Program Files\CMake 2.6\bin
  * C:\Program Files\Java\jdk1.6.0_03\jre\bin\client

Your exact paths will vary depending on where you installed each package.

The important things are A) to make sure that the "ant" and "cmake" commands
work from the command line without specifying a full path; and B) to include
a directory with jvm.dll to avoid runtime errors when initializing a JVM.


HOW TO COMPILE -- WINDOWS

The directions in readme.txt are mostly accurate, except that:

1) Instead of compiling the C++ source from the command line, the build system
   launches Visual Studio to complete the build process. You need to select
   "Build project" from the Build menu to finish the build.

2) The shared libraries and executables are placed in a subdirectory of the
   build folder based on the active solution configuration, typically either
   "debug" or "release." The Ant script takes care of placing a copy of the
   necessary JAR files in both folders, but if you use a different
   configuration you will need to copy the JAR files into the correct
   subdirectory yourself.
