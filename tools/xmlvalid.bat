@echo off

rem xmlvalid.bat: a batch file for validating XML files

rem Required JARs: loci_tools.jar

setlocal
set SCIFIO_DIR=%~dp0
if "%SCIFIO_DIR:~-1%" == "\" set SCIFIO_DIR=%SCIFIO_DIR:~0,-1%

set SCIFIO_PROG=loci.formats.tools.XMLValidate
call "%SCIFIO_DIR%\scifio.bat" %*
