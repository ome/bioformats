@echo off

rem omeul.bat: a command-line client-side import tool for the OME Perl server

rem Required JARs: loci_tools.jar, ome_tools.jar

rem JAR libraries must be in the same directory as this
rem command line script for the command to function.

set DIR=%~dp0
if "%DIR:~-1%" == "\" set DIR=%DIR:~0,-1%

call "%DIR%\config.bat"

if "%SCIFIO_DEVEL%" == "" (
  rem Developer environment variable unset; look for proper libraries.
  if exist "%DIR%\ome_tools.jar" goto found
  if exist "%DIR%\ome-io.jar" goto found
  goto missing
)

:found
set PROG=loci.formats.ome.OMEWriter
set CPAUX="%DIR%\ome_tools.jar";"%DIR%\ome-io.jar"
call "%DIR%\launch.bat" %*
goto end

:missing
echo Required JAR libraries not found. Please download:
echo   ome_tools.jar
echo from:
echo   http://www.loci.wisc.edu/bio-formats/downloads
echo.
echo Please note that omeul is legacy software that
echo has been discontinued. Use at your own risk.

:end
