@echo off

rem bfview.bat: a batch file for displaying an image file in the image viewer

setlocal
set SCIFIO_DIR=%~dp0
if "%SCIFIO_DIR:~-1%" == "\" set SCIFIO_DIR=%SCIFIO_DIR:~0,-1%

echo The 'bfview' command is deprecated. Please use 'showinf' instead.

"%SCIFIO_DIR%\showinf" %*
