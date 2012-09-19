@echo off

rem ijview.bat: a batch file for displaying an image file in ImageJ
rem             using the Bio-Formats Importer plugin

rem Required JARs: loci_tools.jar, ij.jar

rem JAR libraries must be in the same directory as this
rem command line script for the command to function.

set DIR=%~dp0
if "%DIR:~-1%" == "\" set DIR=%DIR:~0,-1%

call "%DIR%\config.bat"

if "%SCIFIO_DEVEL%" == "" (
  rem Developer environment variable unset; look for proper libraries.
  if not exist "%DIR%\ij.jar" goto missing
)

set PROG=loci.plugins.in.Importer
set CPAUX="%DIR%\ij.jar"
call "%DIR%\launch.bat" %*
goto end

:missing
echo Required JAR libraries not found. Please download:
echo   ij.jar
echo from:
echo   http://imagej.nih.gov/ij/upgrade/
echo and place in the same directory as the command line tools.

:end
