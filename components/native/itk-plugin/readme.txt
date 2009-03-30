Bio-Formats ITK plugin
----------------------

This package provides an ImageIO plugin for ITK that uses Bio-Formats
to read and write supported life sciences file formats.

The implementation is still very preliminary, and may be substantially changed
before a stable release is announced.


LICENSE

OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

Adapted from the Slicer3 project: http://www.slicer.org/
http://viewvc.slicer.org/viewcvs.cgi/trunk/Libs/MGHImageIO/

See slicer-license.txt for Slicer3's licensing information.

For more information about the ITK Plugin IO mechanism, see:
http://www.itk.org/Wiki/Plugin_IO_mechanisms


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

Please direct questions to the Bio-Formats team at bioformats@loci.wisc.edu.
