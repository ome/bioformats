@echo off

rem cachegen.bat: a batch file for generating cache (memo) files

rem Required JARs: loci_tools.jar or bioformats_package.jar

setlocal
set BF_DIR=%~dp0
if "%BF_DIR:~-1%" == "\" set BF_DIR=%BF_DIR:~0,-1%

set BF_PROG=loci.formats.tools.GenerateCache
call "%BF_DIR%\bf.bat" %*
