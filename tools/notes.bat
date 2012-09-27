@echo off

rem notes.bat: a batch file for launching OME Notes

rem Please note that OME Notes is legacy software
rem that has been discontinued. Use at your own risk.

rem Required JARs: loci_tools.jar, ome-notes.jar

set SCIFIO_DIR=%~dp0
if "%SCIFIO_DIR:~-1%" == "\" set SCIFIO_DIR=%SCIFIO_DIR:~0,-1%

call "%SCIFIO_DIR%\config.bat"

if "%SCIFIO_DEVEL%" == "" (
  rem Developer environment variable unset; add JAR libraries to classpath.
  if exist "%SCIFIO_JAR_DIR%\ome-notes.jar" (
    set SCIFIO_CP="%SCIFIO_JAR_DIR%\ome-notes.jar"
  )
  else (
    rem Libraries not found; issue an error.
    echo Required JAR libraries not found. Please download:
    echo   http://www.loci.wisc.edu/software/daily/ome-notes.jar
    echo and place in the same directory as the command line tools.
    echo.
    goto end
  )
)

set SCIFIO_PROG=loci.ome.notes.Notes
call "%SCIFIO_DIR%\launch.bat" %*

:end
