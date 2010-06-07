//
// minimum_writer.cpp
//

/*
OME Bio-Formats C++ bindings for native access to Bio-Formats Java library.
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
*/

/*
IMPORTANT NOTE: Although this software is distributed according to a
"BSD-style" license, it requires the Bio-Formats Java library to do
anything useful, which is licensed under the GPL v2 or later.
As such, if you wish to distribute this software with Bio-Formats itself,
your combined work must be distributed under the terms of the GPL.
*/

// A C++ version of the Bio-Formats MinimumWriter example.
// For the original Java version, see:
//   components/bio-formats/utils/MinimumWriter.java

// for Bio-Formats C++ bindings
#include "bio-formats.h"
using jace::JNIException;
using jace::proxy::java::io::IOException;
using jace::proxy::java::lang::Boolean;
using jace::proxy::java::lang::Integer;
using jace::proxy::loci::formats::FormatException;
using jace::proxy::loci::formats::FormatTools;
using jace::proxy::loci::formats::ImageWriter;
using jace::proxy::loci::formats::MetadataTools;
using jace::proxy::loci::formats::meta::IMetadata;
#include "ome-xml.h"
using jace::proxy::ome::xml::model::enums::DimensionOrder;
using jace::proxy::ome::xml::model::enums::PixelType;
using jace::proxy::ome::xml::model::primitives::PositiveInteger;

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
  jace::StaticVmLoader loader(JNI_VERSION_1_4);
  jace::OptionList list;
  list.push_back(jace::ClassPath(
    "jace-runtime.jar" + PATHSEP +
    "bio-formats.jar" + PATHSEP +
    "loci_tools.jar"));
  list.push_back(jace::CustomOption("-Xcheck:jni"));
  list.push_back(jace::CustomOption("-Xmx256m"));
  list.push_back(jace::CustomOption("-Djava.awt.headless=true"));
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
  ByteArray img(planeSize); // pre-allocate buffer

  // fill with random data
  for (int i=0; i<planeSize; i++) img[i] = rand();

  // create metadata object with minimum required metadata fields
  cout << "Populating metadata..." << endl;
  IMetadata meta = MetadataTools::createOMEXMLMetadata();
  meta.createRoot();
  meta.setImageID("Image:0", 0);
  meta.setPixelsID("Pixels:0", 0);
  meta.setPixelsBinDataBigEndian(Boolean(1), 0, 0);
  meta.setPixelsDimensionOrder(DimensionOrder::XYZCT(), 0);
  meta.setPixelsType(
    PixelType::fromString(FormatTools::getPixelTypeString(pixelType)), 0);
  meta.setPixelsSizeX(PositiveInteger(Integer(w)), 0);
  meta.setPixelsSizeY(PositiveInteger(Integer(h)), 0);
  meta.setPixelsSizeZ(PositiveInteger(Integer(1)), 0);
  meta.setPixelsSizeC(PositiveInteger(Integer(1)), 0);
  meta.setPixelsSizeT(PositiveInteger(Integer(1)), 0);
  meta.setChannelID("Channel:0:0", 0, 0);
  meta.setChannelSamplesPerPixel(Integer(1), 0, 0);

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
