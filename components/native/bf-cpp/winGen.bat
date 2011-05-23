REM This script uses Jar2Lib to generate a CMakeLists.txt and the relevant proxy classes for bfcpp.
REM It will create a build directory.  All you have to do is open ccmake or run it manually from the command line.
REM To build bfcpp, configure and generate a visual studio solution using cmake, then open and build the Release solution.

set PROJECT_DIR=%cd%
set JAR_DIR=%PROJECT_DIR%\..\..\..\jar
set ARTIFACT_DIR=%PROJECT_DIR%\..\..\..\artifacts

java -cp %JAR_DIR%\jar2lib-1.0-SNAPSHOT-deps.jar;%JAR_DIR%\ij.jar;%JAR_DIR%\log4j-1.2.15.jar loci.jar2lib.Jar2Lib bfcpp "Bio-Formats C++ bindings" %ARTIFACT_DIR%\loci-common.jar %ARTIFACT_DIR%\loci_plugins.jar %ARTIFACT_DIR%\ome-xml.jar %ARTIFACT_DIR%\bio-formats.jar -conflicts %PROJECT_DIR%\conflicts.txt -header %PROJECT_DIR%\header.txt -extras %PROJECT_DIR%\cmake_extras.txt

cd build
cd bfcpp
mkdir build
