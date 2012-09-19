#!/bin/bash

# launch.sh: the script that actually launches a command line tool

DIR=`dirname "$0"`

# Include the master configuration file.
source "$DIR/config.sh"

# Check that a command to run was specified.
if [ -z "$PROG" ]
then
  echo The command to launch must be set in the PROG environment variable.
  exit 1
fi

# Skip the update check if the NO_UPDATE_CHECK flag is set.
if [ -n "$NO_UPDATE_CHECK" ]
then
  JFLAGS="$JFLAGS -Dbioformats_can_do_upgrade_check=false"
fi

# Use any available proxy settings.
JFLAGS="$JFLAGS -Dhttp.proxyHost=$PROXY_HOST -Dhttp.proxyPort=$PROXY_PORT"

# Run the command!
if [ -n "$SCIFIO_DEVEL" ]
then
  # Developer environment variable set; launch with existing classpath.
  java $JFLAGS $PROG "$@"
elif [ -e "$DIR/loci_tools.jar" ] || [ -e "$DIR/bio-formats.jar" ]
then
  # Developer environment variable unset; add JAR libraries to classpath.
  java $JFLAGS \
    -cp "$DIR:$DIR/bio-formats.jar:$DIR/loci_tools.jar:$CPAUX" $PROG "$@"
else
  # Libraries not found; issue an error.
  echo "Required JAR libraries not found. Please download:"
  echo "  loci_tools.jar"
  echo "from:"
  echo "  http://www.loci.wisc.edu/bio-formats/downloads"
  echo "and place in the same directory as the command line tools."
fi
