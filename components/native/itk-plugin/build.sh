#!/bin/sh

# Simple script to build ITK plugin on Linux and Mac OS X systems.

# locate ITK
if [ -e "$ITK_DIR/ITKConfig.cmake" ]
then
  echo "Found ITK at: $ITK_DIR"
else
  echo Please set ITK_DIR to point to your ITK build directory.
  exit 1
fi

# locate Jace
if [ ! -e "$JACE_DIR/build.xml" ]
then
  JACE_DIR=`grep JACE_DIR ../bf-cpp/build/CMakeCache.txt | sed -e 's/[^=]*=//'`;
fi
if [ -e "$JACE_DIR/build.xml" ]
then
  echo "Found Jace at: $JACE_DIR"
else
  echo Please set JACE_DIR to point to your toplevel Jace directory.
  exit 2
fi

# invoke CMake
echo
echo -------- STARTING BUILD --------
mkdir build
cd build
cmake -D ITK_DIR=$ITK_DIR -D JACE_DIR=$JACE_DIR ..
make
echo -------- BUILD COMPLETE --------

# copy shared libraries into ITK binaries directory
echo
echo "Copying Bio-Formats Java libraries into ITK binaries directory..."
cp -v ../../bf-cpp/build/*.jar "$ITK_DIR/bin"
echo "Copying Bio-Formats C++ shared libraries into ITK binaries directory..."
cp -v \
  ../../bf-cpp/build/*.so \
  ../../bf-cpp/build/*.dylib \
  ../../bf-cpp/build/*.jnilib \
  "$ITK_DIR/bin"

# give some instructions on how to proceed
echo
echo "You can test the plugin with the following commands:"
echo
echo "  export ITK_AUTOLOAD_PATH=\"`pwd`/lib/ITKFactories\""
echo "  cd \"$ITK_DIR/bin\""
echo "  ./ImageHistogram1 /path/to/sample/dataset"
echo
echo "If you use a TIFF file, you can compare the histogram results between"
echo "the Bio-Formats importer and the built-in ITK TIFF reader by setting"
echo "or unsetting the ITK_AUTOLOAD_PATH variable respectively."
echo
