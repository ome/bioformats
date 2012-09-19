@echo off

rem xmlvalid.bat: a batch file for validating XML files

rem Required JARs: loci_tools.jar

rem JAR libraries must be in the same directory as this
rem command line script for the command to function.

set DIR=%~dp0
if "%DIR:~-1%" == "\" set DIR=%DIR:~0,-1%

set PROG=loci.formats.tools.XMLValidate
call "%DIR%\launch.bat" %*
