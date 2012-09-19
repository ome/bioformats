@echo off

rem editor.bat: a batch file for launching the OME Metadata Editor

rem Required JARs: loci_tools.jar, ome-editor.jar,
rem                ome-java.jar, ome-java-deprecated.jar

rem JAR libraries must be in the same directory as this
rem command line script for the command to function.

rem If your CLASSPATH already includes the needed classes,
rem you can set the SCIFIO_DEVEL environment variable to
rem disable the required JAR library checks.

set DIR=%~dp0
if "%DIR:~-1%" == "\" set DIR=%DIR:~0,-1%
call "%DIR%\config.bat"

set PROG=loci.ome.editor.MetadataEditor

if "%SCIFIO_DEVEL%" == "" (
  rem Developer environment variable unset; look for proper libraries
  if not exist "%DIR%\ome-editor.jar" goto missing
  if not exist "%DIR%\ome-java.jar" goto missing
  if not exist "%DIR%\ome-java-deprecated.jar" goto missing
  if exist "%DIR%\loci_tools.jar" goto found
  if exist "%DIR%\bio-formats.jar" goto found
  goto missing
) else (
  rem Developer environment variable set; try to launch
  java -mx512m %PROG% %*
  goto end
)

:found
rem Library found; try to launch
java -mx512m -cp "%DIR%";"%DIR%\bio-formats.jar";"%DIR%\loci_tools.jar";"%DIR%\ome-editor.jar";"%DIR%\ome-java.jar";"%DIR%\ome-java-deprecated.jar" %PROG% %*
goto end

:missing
echo Required JAR libraries not found. Please download:
echo   loci_tools.jar
echo from:
echo   http://www.loci.wisc.edu/bio-formats/downloads
echo as well as the OME Metadata Notebook JARs from:
echo   http://www.loci.wisc.edu/software/daily/ome-editor.jar
echo   http://www.loci.wisc.edu/software/daily/ome-java.jar
echo   http://www.loci.wisc.edu/software/daily/ome-java-deprecated.jar
echo and place in the same directory as the command line tools.
echo.
echo Please note that the OME Metadata Notebook is legacy software that
echo has been discontinued. Use at your own risk."

:end
