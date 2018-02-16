#!/usr/bin/env bash

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

# Prepare the flags.
if [ -z "$BF_FLAGS" ]
then
   BF_FLAGS=""
fi

# Set the max heap size.
if [ -z "$BF_MAX_MEM" ]
then
  # Set a reasonable default max heap size.
  BF_MAX_MEM="512m"
fi
BF_FLAGS="$BF_FLAGS -Xmx$BF_MAX_MEM"

# Skip the update check if the NO_UPDATE_CHECK flag is set.
if [ -n "$NO_UPDATE_CHECK" ]
then
  BF_FLAGS="$BF_FLAGS -Dbioformats_can_do_upgrade_check=false"
fi

# Run profiling if the BF_PROFILE flag is set.
if [ -n "$BF_PROFILE" ]
then
  # Set default profiling depth
  if [ -z "$BF_PROFILE_DEPTH" ]
  then
    BF_PROFILE_DEPTH="30"
  fi
  BF_FLAGS="$BF_FLAGS -agentlib:hprof=cpu=samples,depth=$BF_PROFILE_DEPTH,file=$BF_PROG.hprof"
fi

# Use any available proxy settings.
BF_FLAGS="$BF_FLAGS -Dhttp.proxyHost=$PROXY_HOST -Dhttp.proxyPort=$PROXY_PORT"

# Run the command!
if [ -n "$BF_DEVEL" ]
then
  # Developer environment variable set; launch with existing classpath.
  java $BF_FLAGS $BF_PROG "$@"
else
  # Running from maven build in source dir
  if [ "$BF_IN_MVN_SRCDIR" = "true" ]
  then
    BF_CP="$BF_JAR_DIR/*:$BF_CP"
  # Running from ant build in source dir
  elif [ "$BF_IN_ANT_SRCDIR" = "true" ]
  then
    BF_CP="$BF_JAR_DIR/bioformats_package.jar:$BF_CP"
  # Developer environment variable unset; add JAR libraries to classpath.
  elif [ -d "$BF_JAR_DIR/jar" ]
  then
    BF_CP="$BF_JAR_DIR/jar/*:$BF_CP"
  elif [ -f "$BF_JAR_DIR/bioformats_package.jar" ]
  then
    BF_CP="$BF_JAR_DIR/bioformats_package.jar:$BF_CP"
  else
    # Libraries not found; issue an error.
    echo "Required JAR libraries not found. Please download:"
    echo "  bioformats_package.jar"
    echo "from:"
    echo "  http://downloads.openmicroscopy.org/latest/bio-formats"
    if [ -d "$BF_JAR_DIR/jar" ]
    then
      echo "and place in $BF_JAR_DIR/jar."
    else
      echo "and place in $BF_JAR_DIR."
    fi
    exit 2
  fi
  java $BF_FLAGS -cp "$BF_DIR:$BF_CP" $BF_PROG "$@"
fi
