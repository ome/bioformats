@echo off

rem editor.bat: a batch file for launching the OME Metadata Editor

rem Please note that the OME Metadata Editor is legacy software
rem that has been discontinued. Use at your own risk.

rem Required JARs: loci_tools.jar, ome-editor.jar,
rem                ome-java.jar, ome-java-deprecated.jar

setlocal
set SCIFIO_DIR=%~dp0
if "%SCIFIO_DIR:~-1%" == "\" set SCIFIO_DIR=%SCIFIO_DIR:~0,-1%

call "%SCIFIO_DIR%\config.bat"

if "%SCIFIO_DEVEL%" == "" (
  rem Developer environment variable unset; add JAR libraries to classpath.
  if not exist "%SCIFIO_JAR_DIR%\ome-editor.jar" goto missing
  if not exist "%SCIFIO_JAR_DIR%\ome-java.jar" goto missing
  if not exist "%SCIFIO_JAR_DIR%\ome-java-deprecated.jar" goto missing
  set SCIFIO_CP="%SCIFIO_JAR_DIR\ome-editor.jar"
  set SCIFIO_CP=%SCIFIO_CP%;"%SCIFIO_JAR_DIR\ome-java.jar"
  set SCIFIO_CP=%SCIFIO_CP%;"%SCIFIO_JAR_DIR\ome-java-deprecated.jar"
)

set SCIFIO_PROG=loci.ome.editor.MetadataEditor
call "%SCIFIO_DIR%\scifio.bat" %*
goto end

:missing
rem Libraries not found; issue an error.
echo Required JAR libraries not found. Please download:
echo   http://www.loci.wisc.edu/software/daily/ome-editor.jar
echo   http://www.loci.wisc.edu/software/daily/ome-java.jar
echo   http://www.loci.wisc.edu/software/daily/ome-java-deprecated.jar
echo and place in the same directory as the command line tools.
echo.

:end
