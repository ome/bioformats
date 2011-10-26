#!/bin/sh

# build.sh - A script for use by continuous integration systems,
#            which generates and builds the Bio-Formats C++ bindings.

cd "$(dirname "$0")"/..
set -ex
mvn -DskipTests clean package cppwrap:wrap dependency:copy-dependencies
cd target/cppwrap
mkdir -p build
cd build
cmake ..
make
make package
