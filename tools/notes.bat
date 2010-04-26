@echo off

rem notes.bat: a batch file for launching OME Notes

rem Required JARs: loci_tools.jar, ome-notes.jar

rem JAR libraries must be in the same directory as this
rem command line script for the command to function.

rem If you are a developer working from source and have
rem the LOCI classes in your CLASSPATH, you can set the
rem LOCI_DEVEL environment variable to use them instead.

set PROG=loci.ome.notes.Notes
set DIR=%~dp0
if "%DIR:~1%" == ":\" (
  set DIR1=%DIR%
) else (
  rem Remove trailing backslash
  set DIR1=%DIR:~0,-1%
)

if "%LOCI_DEVEL%" == "" (
  rem Developer environment variable unset; look for proper libraries
  if not exist "%DIR%ome-notes.jar" goto missing
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
java -mx512m -cp "%DIR1%";"%DIR%bio-formats.jar";"%DIR%loci_tools.jar";"%DIR%ome-notes.jar" %PROG% %*
goto end

:missing
echo Required JAR libraries not found. Please download:
echo   loci_tools.jar
echo from:
echo   http://www.loci.wisc.edu/bio-formats/downloads
echo as well as the OME Notes JAR from:
echo   http://www.loci.wisc.edu/software/daily/ome-notes.jar
echo and place in the same directory as the command line tools.
echo.
echo Please note that OME Notes is legacy software that
echo has been discontinued. Use at your own risk."

:end
