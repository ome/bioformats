//
// minimum_writer.cpp
//

/*
OME Bio-Formats C++ bindings for native access to Bio-Formats Java library.
Copyright (C) 2008-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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
*/

// A C++ version of the Bio-Formats MinimumWriter example.
// For the original Java version, see:
//   components/bio-formats/utils/MinimumWriter.java

// for Bio-Formats C++ bindings
#include "bio-formats.h"
#include "loci-common.h"

#include <iostream>
using std::cout;
using std::endl;

#include <string>
using std::string;

// for rand
#include <stdio.h>
#include <stdlib.h>

// Demonstrates the minimum amount of metadata
// necessary to write out an image plane.

int main(int argc, const char *argv[]) {
  if (argc < 1) {
    cout << "Please specify an output file name." << endl;
    return -1;
  }
  string id = argv[0];

  // create blank 512x512 image
  cout << "Creating random image..." << endl;
  int w = 512, h = 512;
  int pixelType = FormatTools::UINT16;
  bool bigEndian = Boolean::TRUE_Jace;

  //byte[] img = new byte[w * h * FormatTools.getBytesPerPixel(pixelType)];
  int planeSize = w * h * FormatTools::getBytesPerPixel(pixelType);
  JArray<JByte> img(planeSize); // pre-allocate buffer

  // fill with random data
  for (int i=0; i<planeSize; i++) img[i] = rand();

  // create metadata object with minimum required metadata fields
  IMetadata meta = MetadataTools::createOMEXMLMetadata();
  meta.createRoot();
  meta.setPixelsBigEndian(Boolean(bigEndian), 0, 0);
  meta.setPixelsDimensionOrder("XYZCT", 0, 0);
  meta.setPixelsPixelType(FormatTools::getPixelTypeString(pixelType), 0, 0);
  meta.setPixelsSizeX(Integer(w), 0, 0);
  meta.setPixelsSizeY(Integer(ih), 0, 0);
  meta.setPixelsSizeZ(Integer(1), 0, 0);
  meta.setPixelsSizeC(Integer(1), 0, 0);
  meta.setPixelsSizeT(Integer(1), 0, 0);
  meta.setLogicalChannelSamplesPerPixel(Integer(1), 0, 0);

  // write image plane to disk
  cout << "Writing image to '" << id << "'..." << endl;
  ImageWriter writer;
  writer.setMetadataRetrieve(meta);
  writer.setId(id);
  writer.saveBytes(img, true);
  writer.close();

  cout << "Done." << endl;
}
