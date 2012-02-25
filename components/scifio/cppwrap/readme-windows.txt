See readme.txt for an overview of the Bio-Formats C++ bindings build process.


COMPILE-TIME DEPENDENCIES -- WINDOWS

Windows users will need to visit the appropriate web sites and download and
install the relevant binaries for all the dependencies.

To configure the tools, you will need to edit or create several environment
variables on your system. Access them by clicking the "Environment Variables"
button from Control Panel, System, Advanced tab. Use semicolons to separate
multiple directories in the PATH variable.

Details follow.


COMPILE-TIME DEPENDENCIES -- WINDOWS -- MAVEN

Download Maven from its web site (http://maven.apache.org/).

Unpack the Maven archive into your Program Files, then add the folder's bin
subdirectory to your PATH environment variable; e.g.:

  C:\Program Files\apache-maven-3.0.4\bin

Once set, new Command Prompts will recognize "mvn" as a valid command.


COMPILE-TIME DEPENDENCIES -- WINDOWS -- CMAKE

Download and run the CMake installer from its web site (http://cmake.org/).

During installation, select the "Add CMake to the system PATH for all
users" option to ensure that Bio-Formats build system can find your CMake
executable.

Once installed, new Command Prompts will recognize "cmake" and "cmake-gui" as
valid commands.


COMPILE-TIME DEPENDENCIES -- WINDOWS -- BOOST

The easiest way to install the Boost Thread library on Windows is to use the
free installer from BoostPro:

  http://www.boostpro.com/download/

When running the installer:

  * Under "Compilers," check the version of Visual C++ matching your system.
  * Under "Variants," check all eight boxes.
  * When choosing components, check "Boost DateTime" and "Boost Thread."


COMPILE-TIME DEPENDENCIES -- WINDOWS -- JAVA DEVELOPMENT KIT

Download and install the JDK from its web site:

  http://www.oracle.com/technetwork/java/javase/downloads/

After the installation is complete, create a new environment variable called
JAVA_HOME pointing to your Java installation; e.g.:

  C:\Program Files\Java\jdk1.6.0_25

Setting JAVA_HOME is the easiest way to ensure that Maven can locate Java.

You will also need to append your JDK's client or server VM folder to the PATH;
e.g.:

  %JAVA_HOME%\jre\bin\client

This step ensures that a directory containing jvm.dll is present in the PATH.
If you do not perform this step, you will receive a runtime error when
attempting to initialize a JVM from native code.

Optionally, you can add the bin subdirectory to the PATH; e.g.:

  %JAVA_HOME%\bin

Once set, new Command Prompts will recognize (e.g.) "javac" as a valid command.


COMPILE-TIME DEPENDENCIES -- WINDOWS -- VISUAL C++

In addition to the other prerequisites, you will also need a working copy of
Visual C++. We have tested compilation with Visual C++ 2005 Professional and
Visual C++ 2008 Express; other versions may or may not work.

You can download Visual C++ Express for free from:

  http://www.microsoft.com/express/

You must launch the environment at least once before you will be able to
compile the Bio-Formats C++ bindings.


HOW TO BUILD - WINDOWS

Run Command Prompt and change to your Bio-Formats working copy. Then run:

  # generate the Bio-Formats C++ bindings
  cd components\scifio
  mvn -DskipTests package cppwrap:wrap

  # build the Bio-Formats C++ bindings
  cd target\cppwrap
  mkdir build
  cd build
  cmake-gui ..

The CMake GUI will open. Click the Configure button, and a dialog will appear.
Select your installed version of Visual Studio, and click Finish.

Once configuration is complete, click Configure again, repeating as necessary
until the Generate button becomes available. Then click Generate. Once
generation is complete, close the CMake window.

Back at the Command Prompt, type:

  start scifio.sln

The solution will then open in Visual Studio. Press F7 to compile it (or select Build Solution from the Build menu).
