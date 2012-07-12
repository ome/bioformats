Bio-Formats ITK plugin
----------------------

This package provides an ImageIO plugin for ITK that uses Bio-Formats
to read and write supported life sciences file formats.

IMPORTANT NOTE: This implementation is a proof of concept that has been
supplanted by the BF-ITK plugin in the bf-itk-pipe folder.


BUILDING AND TESTING THE PLUGIN ON LINUX AND MAC OS X

1) Download and build the Insight Toolkit source code from:

     http://www.itk.org/ITK/resources/software.html

   Be sure to build with shared libraries (BUILD_SHARED_LIBS set to ON).

2) Follow the directions in components/native/bf-cpp to build the Bio-Formats
   C++ bindings.

3) Change to this directory (components/native/itk-plugin).

4) Run the (lame, temporary) build script:

     ITK_DIR=/path/to/itk/build sh build.sh 2> /dev/null

5) After the build succeeds, the script will copy dependent libraries
   (libjace.so, libbfjace.so, jace-runtime.jar and loci_tools.jar) into the ITK
   binaries directory, then suggest some commands to proceed in testing. These
   commands boil down to: a) set ITK_AUTOLOAD_PATH; b) run ImageHistogram1
   example program on a TIFF file; c) optionally, clear ITK_AUTOLOAD_PATH and
   rerun to compare against the results with ITK's built-in TIFF reader.

Please direct any questions to the Bio-Formats team:
http://loci.wisc.edu/bio-formats/contact
