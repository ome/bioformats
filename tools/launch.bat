@echo off

rem launch.bat: the batch file that actually launches a command line tool

set SCIFIO_DIR=%~dp0
if "%SCIFIO_DIR:~-1%" == "\" set SCIFIO_DIR=%SCIFIO_DIR:~0,-1%

rem Include the master configuration file.
call "%SCIFIO_DIR%\config.bat"

rem Check that a command to run was specified.
if "%SCIFIO_PROG%" == "" (
  echo The command to launch must be set in the SCIFIO_PROG environment variable.
  goto end
)

rem Set the max heap size.
if "%SCIFIO_MAX_MEM%" == "" (
  rem Set a reasonable default max heap size.
  set SCIFIO_MAX_MEM=512m
)
set SCIFIO_FLAGS=-Xmx%SCIFIO_MAX_MEM%

rem Skip the update check if the NO_UPDATE_CHECK flag is set.
if not "%NO_UPDATE_CHECK%" == "" (
  set SCIFIO_FLAGS=%SCIFIO_FLAGS% -Dbioformats_can_do_upgrade_check=false
)

rem Use any available proxy settings.
set SCIFIO_FLAGS=%SCIFIO_FLAGS% -Dhttp.proxyHost=%PROXY_HOST% -Dhttp.proxyPort=%PROXY_PORT%

rem Run the command!
if "%SCIFIO_DEVEL%" == "" (
  rem Developer environment variable unset; look for proper libraries.
  if exist "%SCIFIO_DIR%\loci_tools.jar" goto found
  if exist "%SCIFIO_DIR%\bio-formats.jar" goto found
  goto missing
) else (
  rem Developer environment variable set; launch with existing classpath.
  java %SCIFIO_FLAGS% %SCIFIO_PROG% %*
  goto end
)

:found
rem Library found; add JAR libraries to classpath and launch.
java %SCIFIO_FLAGS% -cp "%SCIFIO_DIR%";"%SCIFIO_DIR%\bio-formats.jar";"%SCIFIO_DIR%\loci_tools.jar";%SCIFIO_CP% %SCIFIO_PROG% %*
goto end

:missing
echo Required JAR libraries not found. Please download:
echo   loci_tools.jar
echo from:
echo   http://www.loci.wisc.edu/bio-formats/downloads
echo and place in the same directory as the command line tools.

:end
rem Unset temporary SCIFIO environment variables.
set SCIFIO_CP=
set SCIFIO_DIR=
set SCIFIO_FLAGS=
set SCIFIO_PROG=
