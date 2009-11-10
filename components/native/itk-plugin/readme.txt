Bio-Formats ITK plugin
----------------------

This package provides an ImageIO plugin for ITK that uses Bio-Formats
to read and write supported life sciences file formats.

The implementation is still very preliminary, and may be substantially changed
before a stable release is announced.


LICENSE

OME Bio-Formats ITK plugin for calling Bio-Formats from the Insight Toolkit.
Copyright (c) 2008-@year@, UW-Madison LOCI.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the UW-Madison LOCI nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY UW-MADISON LOCI ''AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL UW-MADISON LOCI BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

IMPORTANT NOTE: Although this software is distributed according to a
"BSD-style" license, it requires the OME Bio-Formats Java library to do
anything useful, which is licensed under the GPL v2 or later.
As such, if you wish to distribute this software with Bio-Formats itself,
your combined work must be distributed under the terms of the GPL.

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
