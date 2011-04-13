#!/bin/bash

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
  -header "$PROJECT_DIR/header.txt"
