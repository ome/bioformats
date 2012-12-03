@echo off

rem ijview.bat: a batch file for displaying an image file in ImageJ
rem             using the Bio-Formats Importer plugin

rem Required JARs: loci_tools.jar, ij.jar

setlocal
set SCIFIO_DIR=%~dp0
if "%SCIFIO_DIR:~-1%" == "\" set SCIFIO_DIR=%SCIFIO_DIR:~0,-1%

call "%SCIFIO_DIR%\config.bat"

if "%SCIFIO_DEVEL%" == "" (
  rem Developer environment variable unset; add JAR libraries to classpath.
  if exist "%SCIFIO_JAR_DIR%\ij.jar" (
    set SCIFIO_CP="%SCIFIO_JAR_DIR%\ij.jar"
  ) else (
    rem Libraries not found; issue an error.
    echo Required JAR libraries not found. Please download:
    echo   ij.jar
    echo from:
    echo   http://imagej.nih.gov/ij/upgrade/
    echo and place in the same directory as the command line tools.
    goto end
  )
  if exist "%SCIFIO_JAR_DIR%\loci_plugins.jar" (
    set SCIFIO_CP=%SCIFIO_CP%;"%SCIFIO_JAR_DIR%\loci_plugins.jar"
  ) else if not exist "%SCIFIO_JAR_DIR%\loci_tools.jar" (
    rem Libraries not found; issue an error.
    echo Required JAR libraries not found. Please download:
    echo   loci_tools.jar
    echo from:
    echo   http://www.openmicroscopy.org/site/products/bio-formats/downloads
    echo and place in the same directory as the command line tools.
    goto end
  )
)

set SCIFIO_PROG=loci.plugins.in.Importer
call "%SCIFIO_DIR%\scifio.bat" %*

:end
