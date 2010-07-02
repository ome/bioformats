LIST OF COMPILE-TIME DEPENDENCIES -- WINDOWS

In addition to the prerequisites listed in readme.txt, you will also need a
working copy of Visual C++. We have tested compilation with Visual C++ 2005
Professional and Visual C++ 2008 Express; other versions may or may not work.

You can download Visual C++ 2008 Express for free from:

  http://www.microsoft.com/express/

You must launch the environment at least once before you will be able to
compile the Bio-Formats C++ bindings.


KNOWN GOTCHAS

1) With Visual C++ 2008 Express, CMake complains about CMAKE_MAKE_PROGRAM not
   being set. You can fix the error by using cmake-gui to explicitly select
   Visual C++ 9.0 as the target compiler, then rerunning the ant command.


INSTALLING COMPILE-TIME DEPENDENCIES -- WINDOWS

Windows users will need to visit the appropriate web sites and download and
install the relevant binaries for Ant, CMake, Subversion, Sun's Java
Development Kit, and Boost.

To configure the tools, you will need to edit or create several environment
variables on your system. Access them by clicking the "Environment Variables"
button from Control Panel, System, Advanced tab. Use semicolons to separate
multiple directories in the PATH variable.

Ant -- After unpacking Ant into your Program Files, add the bin subdirectory
to your PATH environment variable; e.g.:

  C:\Program Files\apache-ant-1.7.1\bin

Once set, new Command Prompts will recognize "ant" as a valid command.

CMake -- During installation, select the "Add CMake to the system PATH for all
users" option to ensure that Bio-Formats build system can find your CMake
executable.

Once installed, new Command Prompts will recognize "cmake" as a valid command.

Subversion -- We suggest using either the Tigris.org Apache 2.2 binary build,
or the graphical TortoiseSVN client. If you use command line svn, add the bin
subdirectory to your PATH environment variable; e.g.:

  C:\Program Files\svn-win32-1.6.0\bin

Once set, new Command Prompts will recognize "svn" as a valid command.

Java -- After installing Sun's JDK, create a new environment variable called
JAVA_HOME pointing to your Java installation; e.g.:

  C:\Program Files\Java\jdk1.6.0_13

Setting JAVA_HOME is the easiest way to ensure that Ant can locate Java.

You will also need to append your JDK's client or server VM folder to the PATH;
e.g.:

  %JAVA_HOME%\jre\bin\client

This step ensures that a directory containing jvm.dll is present in the PATH.
If you do not perform this step, you will receive a runtime error when
attempting to initialize a JVM from native code.

Optionally, you can add the bin subdirectory to the PATH; e.g.:

  %JAVA_HOME%\bin

Once set, new Command Prompts will recognize (e.g.) "javac" as a valid command.

Boost -- The easiest way to install the Boost Thread library on Windows is to
use the free installer from BoostPro:

  http://www.boostpro.com/products/free

When running the installer:

  * Under "Compilers," check the version of Visual C++ matching your system.
  * Under "Variants," check all eight boxes.
  * When choosing components, check "Boost DateTime" and "Boost Thread."


HOW TO COMPILE -- WINDOWS

The compilation directions in readme.txt are mostly accurate, except that:

1) Instead of compiling the C++ source from the command line, the build system
   launches Visual Studio to complete the build process. You need to select
   "Build project" from the Build menu to finish the build.

2) The shared libraries and executables are placed in a subdirectory of the
   build folder based on the active solution configuration, typically either
   "debug" or "release." The Ant script takes care of placing a copy of the
   necessary JAR files in both folders, but if you use a different
   configuration you will need to copy the JAR files into the correct
   subdirectory yourself.
