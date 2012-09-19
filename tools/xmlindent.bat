@echo off

rem xmlindent.bat: a batch file for prettifying blocks of XML

rem Required JARs: loci_tools.jar

rem JAR libraries must be in the same directory as this
rem command line script for the command to function.

set DIR=%~dp0
if "%DIR:~-1%" == "\" set DIR=%DIR:~0,-1%

set PROG=loci.formats.tools.XMLIndent
call "%DIR%\launch.bat" %*
