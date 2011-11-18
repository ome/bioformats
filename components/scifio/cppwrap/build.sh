#!/bin/sh

# build.sh - A script for use by continuous integration systems,
#            which generates and builds the Bio-Formats C++ bindings.

cd "$(dirname "$0")"/..

# find Maven v2 executable
VER=`mvn -v | head -1 | sed -e 's/Apache Maven //' | sed 's/\..*//'`
if [ "$VER" = "2" ]
then
  MVN="mvn"
else
  VER=`mvn2 -v | head -1 | sed -e 's/Apache Maven //' | sed 's/\..*//'`
  if [ "$VER" = "2" ]
  then
    MVN="mvn2"
  else
    echo "Maven v2.x is required to build."
    exit 1
  fi
fi

set -ex
$MVN -DskipTests clean package cppwrap:wrap dependency:copy-dependencies
cd target/cppwrap
mkdir -p build
cd build
cmake ..
make
make package
