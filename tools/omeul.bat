@echo off

rem omeul.bat: a command-line client-side import tool for the OME Perl server

rem Please note that omeul is legacy software
rem that has been discontinued. Use at your own risk.

rem Required JARs: loci_tools.jar, ome_tools.jar

setlocal
set SCIFIO_DIR=%~dp0
if "%SCIFIO_DIR:~-1%" == "\" set SCIFIO_DIR=%SCIFIO_DIR:~0,-1%

call "%SCIFIO_DIR%\config.bat"

if "%SCIFIO_DEVEL%" == "" (
  rem Developer environment variable unset; add JAR libraries to classpath.
  if exist "%SCIFIO_JAR_DIR%\ome-io.jar" (
    set SCIFIO_CP="%SCIFIO_JAR_DIR%\ome-io.jar"
  ) else if exist "%SCIFIO_JAR_DIR%\ome_tools.jar" (
    set SCIFIO_CP="%SCIFIO_JAR_DIR%\ome_tools.jar"
  ) else (
    rem Libraries not found; issue an error.
    echo Required JAR libraries not found. Please download:
    echo   ome_tools.jar
    echo from:
    echo   http://www.openmicroscopy.org/site/products/bio-formats/downloads
    echo.
    goto end
  )
)

set SCIFIO_PROG=loci.ome.io.OMEWriter
call "%SCIFIO_DIR%\scifio.bat" %*

:end
