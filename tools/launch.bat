@echo off

rem launch.bat: the batch file that actually launches a command line tool

set DIR=%~dp0
if "%DIR:~-1%" == "\" set DIR=%DIR:~0,-1%

rem Include the master configuration file.
call "%DIR%\config.bat"

rem Check that a command to run was specified.
if "%PROG%" == "" (
  echo The command to launch must be set in the PROG environment variable.
  goto end
)

rem Set the max heap size.
if "%SCIFIO_MAX_MEM%" == "" (
  rem Set a reasonable default max heap size.
  set SCIFIO_MAX_MEM=512m
)
set JFLAGS=%JFLAGS% -Xmx%SCIFIO_MAX_MEM%

rem Skip the update check if the NO_UPDATE_CHECK flag is set.
if not "%NO_UPDATE_CHECK%" == "" (
  set JFLAGS=%JFLAGS% -Dbioformats_can_do_upgrade_check=false
)

rem Use any available proxy settings.
set JFLAGS=%JFLAGS% -Dhttp.proxyHost=%PROXY_HOST% -Dhttp.proxyPort=%PROXY_PORT%

rem Run the command!
if "%SCIFIO_DEVEL%" == "" (
  rem Developer environment variable unset; look for proper libraries.
  if exist "%DIR%\loci_tools.jar" goto found
  if exist "%DIR%\bio-formats.jar" goto found
  goto missing
) else (
  rem Developer environment variable set; launch with existing classpath.
  java %JFLAGS% %PROG% %*
  goto end
)

:found
rem Library found; add JAR libraries to classpath and launch.
java %JFLAGS% -cp "%DIR%";"%DIR%\bio-formats.jar";"%DIR%\loci_tools.jar";%CPAUX% %PROG% %*
goto end

:missing
echo Required JAR libraries not found. Please download:
echo   loci_tools.jar
echo from:
echo   http://www.loci.wisc.edu/bio-formats/downloads
echo and place in the same directory as the command line tools.

:end
set PROG=
set CPAUX=
