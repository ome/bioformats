@echo off

rem config.bat: master configuration file for the batch files

rem Running this command directly has no effect,
rem but you can tweak the settings to your liking.

rem Set the amount of RAM available to the command line tools.
set JFLAGS=-Xmx512m

rem Set the NO_UPDATE_CHECK flag to skip the update check.
rem NO_UPDATE_CHECK=1

rem If you are behind a proxy server, the host name and port must be set.
set PROXY_HOST=
set PROXY_PORT=

rem If your CLASSPATH already includes the needed classes,
rem you can set the SCIFIO_DEVEL environment variable to
rem disable the required JAR library checks.
rem SCIFIO_DEVEL=1
