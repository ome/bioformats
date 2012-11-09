#!/bin/bash

# config.sh: master configuration file for the scripts

# Running this command directly has no effect,
# but you can tweak the settings to your liking.

# Set the amount of RAM available to the command line tools.
# Use "m" suffix for megabytes, "g" for gigabytes; e.g., 2g = 2 GB.
#SCIFIO_MAX_MEM=1g

# Set the NO_UPDATE_CHECK flag to skip the update check.
#NO_UPDATE_CHECK=1

# If you are behind a proxy server, the host name and port must be set.
#PROXY_HOST=
#PROXY_PORT=

# If your CLASSPATH already includes the needed classes,
# you can set the SCIFIO_DEVEL environment variable to
# disable the required JAR library checks.
#SCIFIO_DEVEL=1

# Set the directory containing the JAR libraries.
if [ -z "$SCIFIO_JAR_DIR" ]
then
  if [ -d "$SCIFIO_DIR/../artifacts" ]
  then
    # Scripts reside in a git working copy.
    # Look for JARs in the artifacts directory.
    SCIFIO_JAR_DIR="$SCIFIO_DIR/../artifacts"
  else
    # Scripts reside in a standalone distribution.
    # Look for JARs in the same directory as the scripts.
    SCIFIO_JAR_DIR="$SCIFIO_DIR"
  fi
fi
