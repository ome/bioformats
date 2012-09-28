@echo off

rem showinf.bat: a batch file for displaying information about a given
rem              image file, while displaying it in the image viewer

rem Required JARs: loci_tools.jar

setlocal
set SCIFIO_DIR=%~dp0
if "%SCIFIO_DIR:~-1%" == "\" set SCIFIO_DIR=%SCIFIO_DIR:~0,-1%

set SCIFIO_PROG=loci.formats.tools.ImageInfo
call "%SCIFIO_DIR%\scifio.bat" %*
