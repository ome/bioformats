@echo off

rem scifio.bat: the batch file that actually launches a command line tool

setlocal
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
if not "%SCIFIO_DEVEL%" == "" (
  rem Developer environment variable set; launch with existing classpath.
  java %SCIFIO_FLAGS% %SCIFIO_PROG% %*
  goto end
)

rem Developer environment variable unset; add JAR libraries to classpath.
if exist "%SCIFIO_JAR_DIR%\bio-formats.jar" (
  set SCIFIO_CP=%SCIFIO_CP%;"%SCIFIO_JAR_DIR%\bio-formats.jar";"%SCIFIO_JAR_DIR%\scifio-tools.jar"
) else if exist "%SCIFIO_JAR_DIR%\loci_tools.jar" (
  set SCIFIO_CP=%SCIFIO_CP%;"%SCIFIO_JAR_DIR%\loci_tools.jar"
) else (
  rem Libraries not found; issue an error.
  echo Required JAR libraries not found. Please download:
  echo   loci_tools.jar
  echo from:
  echo   http://www.openmicroscopy.org/site/products/bio-formats/downloads
  echo and place in the same directory as the command line tools.
  goto end
)

java %SCIFIO_FLAGS% -cp "%SCIFIO_DIR%";%SCIFIO_CP% %SCIFIO_PROG% %*

:end
