#!/bin/bash

# config.sh: master configuration file for the scripts

# Running this command directly has no effect,
# but you can tweak the settings to your liking.

# Set the amount of RAM available to the command line tools.
JFLAGS="-Xmx512m"

# If you are behind a proxy server, the host name and port must be set.
#PROXY_HOST=
#PROXY_PORT=

JFLAGS="$JFLAGS -Dhttp.proxyHost=$PROXY_HOST -Dhttp.proxyPort=$PROXY_PORT"
