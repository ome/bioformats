MIPAV plugin for opening life sciences images using Bio-Formats.

-------------------------------------------------------------------------------
Steps to try out the plugin:

1) Download the bioformats_package.jar trunk build from:
     https://www.openmicroscopy.org/bio-formats/downloads
   and save it in your MIPAV directory.

2) Compile the plugin with:
     javac -cp /path/to/mipav:/path/to/mipav/bioformats_package.jar \
       PlugInBioFormatsImporter.java
   where "/path/to/mipav" is the location of your MIPAV installation.

3) Copy the resultant PlugInBioFormatsImporter*.class files into your user
   mipav/plugins folder. On some systems and/or with some versions of MIPAV,
   you may need to copy the class files to your main mipav folder instead.

4) Add bioformats_package.jar to MIPAV's class path:
     * On Mac OS X, edit the mipav.app/Contents/Info.plist file.
     * On Windows, edit the C:\Program Files\mipav\mipav.lax file.

5) Run MIPAV and a new "BioFormatsImporter - read image" menu item will appear
   in the Plugins > File submenu.

-------------------------------------------------------------------------------
The plugin works, but there are a couple of problems and open questions:

1) For the dimensional extents, it seems like MIPAV specifically wants an int
   array of <= size 4, arranged {width, height, numZSlices, numTimePoints}. Is
   this correct? The MIPAV API appears to support multidimensional data in
   general, but when I try to use a longer extents array an exception is
   thrown.  Also, must the order be XYZT? What about other types of dimensions?
   What are MIPAV's assumptions?

2) Does MIPAV support multichannel data for weird numbers of channels? For
   example, if I have 12-channel data, how do I represent that using the
   ModelImage API? Due to this uncertainty, the Bio-Formats plugin supports
   only single-channel data at the moment.
