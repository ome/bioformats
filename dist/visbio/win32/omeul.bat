@echo off
set JAVA=java
if exist jre\bin\java set JAVA=jre\bin\java
%JAVA% -mx512m -cp bio-formats.jar;commons-httpclient-2.0-rc2.jar;commons-logging.jar;forms-1.0.4.jar;ij.jar;looks-1.2.2.jar;ome-java.jar;visad-lite.jar;visbio.jar;xmlrpc-1.2-b1.jar -Dswing.defaultlaf=com.jgoodies.plaf.windows.ExtWindowsLookAndFeel loci.visbio.ome.ImageUploader %1 %2 %3 %4 %5 %6 %7 %8 %9
