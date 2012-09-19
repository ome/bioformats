@echo off

rem editor.bat: a batch file for launching the OME Metadata Editor

rem Required JARs: loci_tools.jar, ome-editor.jar,
rem                ome-java.jar, ome-java-deprecated.jar

rem JAR libraries must be in the same directory as this
rem command line script for the command to function.

set DIR=%~dp0
if "%DIR:~-1%" == "\" set DIR=%DIR:~0,-1%

call "%DIR%\config.bat"

if "%SCIFIO_DEVEL%" == "" (
  rem Developer environment variable unset; look for proper libraries.
  if not exist "%DIR%\ome-editor.jar" goto missing
  if not exist "%DIR%\ome-java.jar" goto missing
  if not exist "%DIR%\ome-java-deprecated.jar" goto missing
)

set PROG=loci.ome.editor.MetadataEditor
set CPAUX="%DIR%\ome-editor.jar";"%DIR%\ome-java.jar";"%DIR%\ome-java-deprecated.jar"
call "%DIR%\launch.bat" %*
goto end

:missing
echo Required JAR libraries not found. Please download:
echo   http://www.loci.wisc.edu/software/daily/ome-editor.jar
echo   http://www.loci.wisc.edu/software/daily/ome-java.jar
echo   http://www.loci.wisc.edu/software/daily/ome-java-deprecated.jar
echo and place in the same directory as the command line tools.
echo.
echo Please note that the OME Metadata Editor is legacy software that
echo has been discontinued. Use at your own risk."

:end
