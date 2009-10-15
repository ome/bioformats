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

#if defined (_WIN32)
#define PATHSEP string(";")
#else
#define PATHSEP string(":")
#endif

// -- Methods --

/* Initializes the Java virtual machine. */
void createJVM() {
  cout << "Creating JVM... ";
  StaticVmLoader loader(JNI_VERSION_1_4);
  OptionList list;
  list.push_back(jace::ClassPath(
    "jace-runtime.jar" + PATHSEP +
    "bio-formats.jar" + PATHSEP +
    "loci_tools.jar"));
  list.push_back(jace::CustomOption("-Xcheck:jni"));
  list.push_back(jace::CustomOption("-Xmx256m"));
  //list.push_back(jace::CustomOption("-verbose:jni"));
  jace::helper::createVm(loader, list, false);
  cout << "JVM created." << endl;
}

/*
 * Demonstrates the minimum amount of metadata
 * necessary to write out an image plane.
 */
bool minWrite(int argc, const char *argv[]) {
  if (argc < 2) {
    cout << "Please specify an output file name." << endl;
    return -1;
  }
  string id = argv[1];

  // create blank 512x512 image
  cout << "Creating random image..." << endl;
  int w = 512, h = 512;
  int pixelType = FormatTools::UINT16();

  //byte[] img = new byte[w * h * FormatTools.getBytesPerPixel(pixelType)];
  int planeSize = w * h * FormatTools::getBytesPerPixel(pixelType);
  JArray<JByte> img(planeSize); // pre-allocate buffer

  // fill with random data
  for (int i=0; i<planeSize; i++) img[i] = rand();

  // create metadata object with minimum required metadata fields
  cout << "Populating metadata..." << endl;
  IMetadata meta = MetadataTools::createOMEXMLMetadata();
  meta.createRoot();
  meta.setPixelsBigEndian(Boolean(1), 0, 0);
  meta.setPixelsDimensionOrder("XYZCT", 0, 0);
  meta.setPixelsPixelType(FormatTools::getPixelTypeString(pixelType), 0, 0);
  meta.setPixelsSizeX(Integer(w), 0, 0);
  meta.setPixelsSizeY(Integer(h), 0, 0);
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

int main(int argc, const char *argv[]) {
  try {
    createJVM();
    minWrite(argc, argv);
  }
  catch (FormatException& fe) {
    fe.printStackTrace();
    return -2;
  }
  catch (IOException& ioe) {
    ioe.printStackTrace();
    return -3;
  }
  catch (JNIException& jniException) {
    cout << "An unexpected JNI error occurred. " << jniException.what() << endl;
    return -4;
  }
  catch (std::exception& e) {
    cout << "An unexpected C++ error occurred. " << e.what() << endl;
    return -5;
  }
}
