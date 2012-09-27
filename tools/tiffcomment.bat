@echo off

rem tiffcomment.bat: a batch file for extracting the comment
rem                  (OME-XML block or otherwise) from a TIFF file

rem Required JARs: loci_tools.jar

rem JAR libraries must be in the same directory as this
rem command line script for the command to function.

set SCIFIO_DIR=%~dp0
if "%SCIFIO_DIR:~-1%" == "\" set SCIFIO_DIR=%SCIFIO_DIR:~0,-1%

set SCIFIO_PROG=loci.formats.tools.TiffComment
call "%SCIFIO_DIR%\launch.bat" %*
