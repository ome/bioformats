@echo off

rem xmlvalid.bat: a batch file for validating XML files

rem Required JARs: loci_tools.jar

rem JAR libraries must be in the same directory as this
rem command line script for the command to function.

rem If you are a developer working from source and have
rem the LOCI classes in your CLASSPATH, you can set the
rem LOCI_DEVEL environment variable to use them instead.

set PROG=loci.formats.tools.XMLValidate
set DIR=%~dp0
if "%DIR:~1%" == ":\" (
  set DIR1=%DIR%
) else (
  rem Remove trailing backslash
  set DIR1=%DIR:~0,-1%
)

rem If you are behind a proxy server, the host name and port must be set here.

set PROXY_HOST=
set PROXY_PORT=

if "%LOCI_DEVEL%" == "" (
  rem Developer environment variable unset; look for proper libraries
  if exist "%DIR%loci_tools.jar" goto found
  if exist "%DIR%bio-formats.jar" goto found
  goto missing
) else (
  rem Developer environment variable set; try to launch
  java -mx512m -Dhttp.proxyHost=%PROXY_HOST% -Dhttp.proxyPort=%PROXY_PORT% %PROG% %*
  goto end
)

:found
rem Library found; try to launch
java -mx512m -Dhttp.proxyHost=%PROXY_HOST% -Dhttp.proxyPort=%PROXY_PORT% -cp "%DIR1%";"%DIR%bio-formats.jar";"%DIR%loci_tools.jar" %PROG% %*
goto end

:missing
echo Required JAR libraries not found. Please download:
echo   loci_tools.jar
echo from:
echo   http://www.openmicroscopy.org/site/products/bio-formats/downloads
echo and place in the same directory as the command line tools.

:end
