#!/bin/bash

# config.sh: master configuration file for the scripts

# Running this command directly has no effect,
# but you can tweak the settings to your liking.

# Set the amount of RAM available to the command line tools.
JFLAGS="-Xmx512m"

# Set the NO_UPDATE_CHECK flag to skip the update check.
#NO_UPDATE_CHECK=1

# Skip the update check if the NO_UPDATE_CHECK flag is set.
if [ -n "$NO_UPDATE_CHECK" ]
then
  JFLAGS="$JFLAGS -Dbioformats_can_do_upgrade_check=false"
fi

# If you are behind a proxy server, the host name and port must be set.
#PROXY_HOST=
#PROXY_PORT=

JFLAGS="$JFLAGS -Dhttp.proxyHost=$PROXY_HOST -Dhttp.proxyPort=$PROXY_PORT"
