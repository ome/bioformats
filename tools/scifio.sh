#!/bin/bash

# scifio.sh: the script that actually launches a command line tool

SCIFIO_DIR=`dirname "$0"`

# Include the master configuration file.
source "$SCIFIO_DIR/config.sh"

# Check that a command to run was specified.
if [ -z "$SCIFIO_PROG" ]
then
  echo The command to launch must be set in the SCIFIO_PROG environment variable.
  exit 1
fi

# Set the max heap size.
if [ -z "$SCIFIO_MAX_MEM" ]
then
  # Set a reasonable default max heap size.
  SCIFIO_MAX_MEM="512m"
fi
SCIFIO_FLAGS="-Xmx$SCIFIO_MAX_MEM"

# Skip the update check if the NO_UPDATE_CHECK flag is set.
if [ -n "$NO_UPDATE_CHECK" ]
then
  SCIFIO_FLAGS="$SCIFIO_FLAGS -Dbioformats_can_do_upgrade_check=false"
fi

# Use any available proxy settings.
SCIFIO_FLAGS="$SCIFIO_FLAGS -Dhttp.proxyHost=$PROXY_HOST -Dhttp.proxyPort=$PROXY_PORT"

# Run the command!
if [ -n "$SCIFIO_DEVEL" ]
then
  # Developer environment variable set; launch with existing classpath.
  java $SCIFIO_FLAGS $SCIFIO_PROG "$@"
else
  # Developer environment variable unset; add JAR libraries to classpath.
  if [ -e "$SCIFIO_JAR_DIR/bio-formats.jar" ]
  then
    SCIFIO_CP="$SCIFIO_JAR_DIR/bio-formats.jar:$SCIFIO_CP"
  elif [ -e "$SCIFIO_JAR_DIR/loci_tools.jar" ]
  then
    SCIFIO_CP="$SCIFIO_JAR_DIR/loci_tools.jar:$SCIFIO_CP"
  else
    # Libraries not found; issue an error.
    echo "Required JAR libraries not found. Please download:"
    echo "  loci_tools.jar"
    echo "from:"
    echo "  http://www.openmicroscopy.org/site/products/bio-formats/downloads"
    echo "and place in the same directory as the command line tools."
    exit 2
  fi
  java $SCIFIO_FLAGS -cp "$SCIFIO_DIR:$SCIFIO_CP" $SCIFIO_PROG "$@"
fi
