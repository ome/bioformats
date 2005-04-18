@echo off
javac *.java
jar cvfM plugins\ome_plugin.jar plugins.config *.java *.class
