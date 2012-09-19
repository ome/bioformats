@echo off

rem omeul.bat: a command-line client-side import tool for OME

rem Required JARs: loci_tools.jar

rem JAR libraries must be in the same directory as this
rem command line script for the command to function.

set DIR=%~dp0
if "%DIR:~-1%" == "\" set DIR=%DIR:~0,-1%

set PROG=loci.formats.ome.OMEWriter
call "%DIR%\launch.bat" %*
