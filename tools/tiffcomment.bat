@echo off

rem tiffcomment.bat: a batch file for extracting the comment
rem                  (OME-XML block or otherwise) from a TIFF file

rem Required JARs: loci_tools.jar

rem JAR libraries must be in the same directory as this
rem command line script for the command to function.

set DIR=%~dp0
if "%DIR:~-1%" == "\" set DIR=%DIR:~0,-1%

set PROG=loci.formats.tools.TiffComment
call "%DIR%\launch.bat" %*
