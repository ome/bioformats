#!/bin/bash
# This script uses Jar2Lib to generate a CMakeLists.txt and the relevant proxy classes for bfcpp.
# It will create a build directory and open cmake for you.
# To build bfcpp, configure and generate a make file and use the command "make"

PROJECT_DIR=`cd "$(dirname $0)"; pwd`
JAR_DIR="$PROJECT_DIR/../../../jar"
ARTIFACT_DIR="$PROJECT_DIR/../../../artifacts"

java -cp \
"$JAR_DIR/jar2lib-1.0-SNAPSHOT-deps.jar":\
"$JAR_DIR/ij.jar":\
"$JAR_DIR/log4j-1.2.15.jar" \
  loci.jar2lib.Jar2Lib \
  bfcpp "Bio-Formats C++ bindings" \
  "$ARTIFACT_DIR/loci-common.jar" \
  "$ARTIFACT_DIR/loci_plugins.jar" \
  "$ARTIFACT_DIR/ome-xml.jar" \
  "$ARTIFACT_DIR/bio-formats.jar" \
  -conflicts "$PROJECT_DIR/conflicts.txt" \
  -header "$PROJECT_DIR/header.txt" \
  -extras "$PROJECT_DIR/cmake_extras.txt"


  cd bfcpp
  mkdir build
  cd build
  ccmake ..
  make
