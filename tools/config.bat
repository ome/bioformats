@echo off

rem config.bat: master configuration file for the batch files

rem Running this command directly has no effect,
rem but you can tweak the settings to your liking.

rem Set the amount of RAM available to the command line tools.
set JFLAGS=-Xmx512m

rem Set the NO_UPDATE_CHECK flag to skip the update check.
rem NO_UPDATE_CHECK=1

rem Skip the update check if the NO_UPDATE_CHECK flag is set.
if not "%NO_UPDATE_CHECK%" == "" (
  rem Skip the update check if the NO_UPDATE_CHECK flag is set
  set JFLAGS=%JFLAGS% -Dbioformats_can_do_upgrade_check=false
)

rem If you are behind a proxy server, the host name and port must be set.
set PROXY_HOST=
set PROXY_PORT=

set JFLAGS=%JFLAGS% -Dhttp.proxyHost=%PROXY_HOST% -Dhttp.proxyPort=%PROXY_PORT%
