@echo off

rem ijview.bat: a batch file for displaying an image file in ImageJ
rem             using the Bio-Formats Importer plugin

rem Required JARs: loci_tools.jar, ij.jar

rem JAR libraries must be in the same directory as this
rem command line script for the command to function.  

rem If you are a developer working from source and have
rem the LOCI classes in your CLASSPATH, you can set the
rem LOCI_DEVEL environment variable to use them instead.

set PROG=loci.plugins.Importer

if "%LOCI_DEVEL%" == "" (
  rem Developer environment variable unset; look for proper libraries
  if not exist ij.jar goto missing
  if exist loci_tools.jar goto found
  if exist bio-formats.jar goto found
  goto missing
) else (
  rem Developer environment variable set; try to launch
  java -mx512m %PROG% %*
  goto end
)

:found
rem Library found; try to launch
java -mx512m -cp bio-formats.jar;ij.jar;loci_tools.jar %PROG% %*
goto end

:missing
echo Required JAR libraries not found. Please download:
echo   loci_tools.jar
echo from:
echo   http://www.loci.wisc.edu/ome/formats.html
echo and:
echo   ij.jar
echo from:
echo   http://rsb.info.nih.gov/ij/upgrade/
echo and place in the same directory as the command line tools.

:end
