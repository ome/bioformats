@echo off
cd "C:\Program Files\ImageJ"
jre\bin\java.exe -Xmx750m -cp "ij.jar;%CLASSPATH%" -Dplugins.dir="C:\java\loci\ome\ij" ij.ImageJ
cd "C:\java\loci\ome\ij"
