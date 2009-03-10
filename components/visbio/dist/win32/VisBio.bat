@echo off
set JAVA=java
if exist jre\bin\java set JAVA=jre\bin\java
%JAVA% -mx512m -Dswing.defaultlaf=com.jgoodies.plaf.windows.ExtWindowsLookAndFeel -jar visbio.jar %1 %2 %3 %4 %5 %6 %7 %8 %9
