@echo off

rem bfview.bat: a batch file for displaying an image file in the image viewer

rem Required JARs: loci_tools.jar

rem JAR libraries must be in the same directory as this
rem command line script for the command to function.

rem If you are a developer working from source and have
rem the LOCI classes in your CLASSPATH, you can set the
rem LOCI_DEVEL environment variable to use them instead.

echo The 'bfview' command is now deprecated.  Please use the 'showinf' command instead.
showinf %*
