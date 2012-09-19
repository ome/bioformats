@echo off

rem showinf.bat: a batch file for displaying information about a given
rem              image file, while displaying it in the image viewer

rem Required JARs: loci_tools.jar

rem JAR libraries must be in the same directory as this
rem command line script for the command to function.

set DIR=%~dp0
if "%DIR:~-1%" == "\" set DIR=%DIR:~0,-1%

set PROG=loci.formats.tools.ImageInfo
call "%DIR%\launch.bat" %*
