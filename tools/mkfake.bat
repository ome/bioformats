@echo off

rem mkfake: a script for creating a fake file / directory structures
rem         on the file system

rem Required JARs: loci_tools.jar or bioformats_package.jar

setlocal
set SCIFIO_DIR=%~dp0
if "%SCIFIO_DIR:~-1%" == "\" set SCIFIO_DIR=%SCIFIO_DIR:~0,-1%

set SCIFIO_PROG=loci.formats.tools.ImageFaker
call "%SCIFIO_DIR%\scifio.bat" %*
