@echo off

rem config.bat: master configuration file for the batch files

rem Running this command directly has no effect,
rem but you can tweak the settings to your liking.

rem Set the amount of RAM available to the command line tools.
set JFLAGS=-Xmx512m

rem If you are behind a proxy server, the host name and port must be set.
set PROXY_HOST=
set PROXY_PORT=

set JFLAGS=%JFLAGS% -Dhttp.proxyHost=%PROXY_HOST% -Dhttp.proxyPort=%PROXY_PORT%
