#!/bin/bash

# bf.sh: the script that actually launches a command line tool

BF_DIR=`dirname "$0"`

# Include the master configuration file.
source "$BF_DIR/config.sh"

# Check that a command to run was specified.
if [ -z "$BF_PROG" ]
then
  echo The command to launch must be set in the BF_PROG environment variable.
  exit 1
fi

# Set the max heap size.
if [ -z "$BF_MAX_MEM" ]
then
  # Set a reasonable default max heap size.
  BF_MAX_MEM="512m"
fi
BF_FLAGS="-Xmx$BF_MAX_MEM"

# Skip the update check if the NO_UPDATE_CHECK flag is set.
if [ -n "$NO_UPDATE_CHECK" ]
then
  BF_FLAGS="$BF_FLAGS -Dbioformats_can_do_upgrade_check=false"
fi

# Use any available proxy settings.
BF_FLAGS="$BF_FLAGS -Dhttp.proxyHost=$PROXY_HOST -Dhttp.proxyPort=$PROXY_PORT"

# Run the command!
if [ -n "$BF_DEVEL" ]
then
  # Developer environment variable set; launch with existing classpath.
  java $BF_FLAGS $BF_PROG "$@"
else
  # Developer environment variable unset; add JAR libraries to classpath.
  if [ -e "$BF_JAR_DIR/bio-formats.jar" ]
  then
    BF_CP="$BF_JAR_DIR/bio-formats.jar:$BF_JAR_DIR/bio-formats-tools.jar:$BF_CP"
  elif [ -e "$BF_JAR_DIR/bioformats_package.jar" ]
  then
    BF_CP="$BF_JAR_DIR/bioformats_package.jar:$BF_CP"
  elif [ -e "$BF_JAR_DIR/loci_tools.jar" ]
  then
    BF_CP="$BF_JAR_DIR/loci_tools.jar:$BF_CP"
  else
    # Libraries not found; issue an error.
    echo "Required JAR libraries not found. Please download:"
    echo "  bioformats_package.jar"
    echo "from:"
    echo "  http://www.openmicroscopy.org/site/products/bio-formats/downloads"
    echo "and place in the same directory as the command line tools."
    exit 2
  fi
  java $BF_FLAGS -cp "$BF_DIR:$BF_CP" $BF_PROG "$@"
fi
