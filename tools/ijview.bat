@echo off

rem ijview.bat: a batch file for displaying an image file in ImageJ
rem             using the Bio-Formats Importer plugin

rem Required JARs: loci_tools.jar, ij.jar

rem JAR libraries must be in the same directory as this
rem command line script for the command to function.

rem If you are a developer working from source and have
rem the LOCI classes in your CLASSPATH, you can set the
rem LOCI_DEVEL environment variable to use them instead.

set PROG=loci.plugins.in.Importer
set DIR=%~dp0
if "%DIR:~1%" == ":\" (
  set DIR1=%DIR%
) else (
  rem Remove trailing backslash
  set DIR1=%DIR:~0,-1%
)

if "%LOCI_DEVEL%" == "" (
  rem Developer environment variable unset; look for proper libraries
  if not exist "%DIR%ij.jar" goto missing
  if exist "%DIR%loci_tools.jar" goto found
  if exist "%DIR%bio-formats.jar" goto found
  goto missing
) else (
  rem Developer environment variable set; try to launch
  java -mx512m %PROG% %*
  goto end
)

:found
rem Library found; try to launch
java -mx512m -cp "%DIR1%";"%DIR%bio-formats.jar";"%DIR%loci_tools.jar";"%DIR%ij.jar" %PROG% %*
goto end

:missing
echo Required JAR libraries not found. Please download:
echo   loci_tools.jar
echo from:
echo   http://www.loci.wisc.edu/bio-formats/downloads
echo and:
echo   ij.jar
echo from:
echo   http://rsb.info.nih.gov/ij/upgrade/
echo and place in the same directory as the command line tools.

:end
