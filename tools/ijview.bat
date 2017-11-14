@echo off

rem ijview.bat: a batch file for displaying an image file in ImageJ
rem             using the Bio-Formats Importer plugin

rem Required JARs: loci_tools.jar or bioformats_package.jar, ij.jar

setlocal
set BF_DIR=%~dp0
if "%BF_DIR:~-1%" == "\" set BF_DIR=%BF_DIR:~0,-1%

call "%BF_DIR%\config.bat"

if "%BF_DEVEL%" == "" (
  rem Developer environment variable unset; add JAR libraries to classpath.
  if exist "%BF_JAR_DIR%\jar\ij.jar" (
    set BF_CP="%BF_JAR_DIR%\jar\ij.jar"
  ) else if exist "%BF_JAR_DIR%\ij.jar" (
    set BF_CP="%BF_JAR_DIR%\ij.jar"
  ) else (
    rem Libraries not found; issue an error.
    echo Required JAR libraries not found. Please download:
    echo   ij.jar
    echo from:
    echo   http://imagej.nih.gov/ij/upgrade/
    if exist "%BF_JAR_DIR%\jar\" (
      echo and place in %BF_JAR_DIR%/jar.
    ) else (
      echo and place in %BF_JAR_DIR%.
    )
    goto end
  )
)

set BF_PROG=loci.plugins.in.Importer
call "%BF_DIR%\bf.bat" %*

:end
