@echo off

rem bfview.bat: a batch file for displaying an image file in the image viewer

set DIR=%~dp0
if "%DIR:~-1%" == "\" set DIR=%DIR:~0,-1%

echo The 'bfview' command is deprecated. Please use 'showinf' instead.

"%DIR%\showinf" %*
