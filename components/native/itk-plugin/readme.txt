Bio-Formats ITK plugin
----------------------

This package provides an ImageIO plugin for ITK that uses Bio-Formats
to read and write supported life sciences file formats.

The initial plugin implementation is complete. However, there are a number of
symbol lookup errors at runtime, so portions of the functionality are currently
commented out to avoid them.


BUILDING AND TESTING THE PLUGIN ON LINUX

1) Download and build the Insight Toolkit source code from:

  http://www.itk.org/ITK/resources/software.html

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

If you get this far, you will have noticed that the histogram is always filled
with 255s. This is because the plugin does not currently copy the planar data
between data structures, due to one of the symbol lookup errors.


SYMBOL LOOKUP ERRORS

As a rule of thumb, anything labeled "NB:" or "TEMP" in the
itkBioFormatsImageIO.cxx source file documents a known problem and/or
associated workaround at the moment, the most show-stopping of which are the
symbol lookup errors.

For example, from lines 260-2 of itkBioFormatsImageIO.cxx:

  // NB: Using brackets with a JArray causes a symbol lookup error on:
  //     _ZN4jace6helper12newGlobalRefEP10_Jv_JNIEnvP9__jobject
  //for (int i=0; i<bytesPerSubPlane; i++) data[p++] = buf[i];

Uncommenting the for loop and rerunning the build script yields:

  $ ./ImageHistogram1 image.tif
  *snip output*
  Reading image plane 1/1
  ./ImageHistogram1: symbol lookup error: libBioFormatsIO.so: undefined symbol: _ZN4jace6helper12newGlobalRefEP10_Jv_JNIEnvP9__jobject

Dumping the relevant symbols from libjace.so yields:

  $ nm libjace.so | grep newGlobalRef
  00025621 T _ZN4jace6helper12newGlobalRefEP7JNIEnv_P8_jobject

So the symbol is essentially there, but slightly different...
