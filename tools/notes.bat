@echo off

rem notes.bat: a batch file for launching OME Notes

rem Required JARs: loci_tools.jar, ome-notes.jar

rem JAR libraries must be in the same directory as this
rem command line script for the command to function.

set DIR=%~dp0
if "%DIR:~-1%" == "\" set DIR=%DIR:~0,-1%

call "%DIR%\config.bat"

if "%SCIFIO_DEVEL%" == "" (
  rem Developer environment variable unset; look for proper libraries.
  if not exist "%DIR%\ome-notes.jar" goto missing
)

set PROG=loci.ome.notes.Notes
set CPAUX="%DIR%\ome-notes.jar"
call "%DIR%\launch.bat" %*
goto end

:missing
echo Required JAR libraries not found. Please download:
echo   http://www.loci.wisc.edu/software/daily/ome-notes.jar
echo and place in the same directory as the command line tools.
echo.
echo Please note that OME Notes is legacy software that
echo has been discontinued. Use at your own risk."

:end
