/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

// A C++ version of the Bio-Formats MinimumWriter example.
// For the original Java version, see:
//   components/formats-bsd/utils/MinimumWriter.java

// for JVM initialization
#include "javaTools.h"

// for Bio-Formats C++ bindings
#include "formats-api-${release.version}.h"
#include "formats-bsd-${release.version}.h"
#include "ome-xml-${release.version}.h"
using jace::JNIException;
using jace::proxy::java::io::IOException;
using jace::proxy::java::lang::Boolean;
using jace::proxy::java::lang::Integer;
using jace::proxy::loci::formats::FormatException;
using jace::proxy::loci::formats::FormatTools;
using jace::proxy::loci::formats::ImageWriter;
using jace::proxy::loci::formats::meta::IMetadata;
using jace::proxy::loci::formats::services::OMEXMLServiceImpl;
using jace::proxy::ome::xml::model::enums::DimensionOrder;
using jace::proxy::ome::xml::model::enums::PixelType;
using jace::proxy::ome::xml::model::primitives::PositiveInteger;

#include <iostream>
using std::cout;
using std::endl;

#include <exception>
using std::exception;

#include <string>
using std::string;

// for rand
#include <stdio.h>
#include <stdlib.h>

// -- Methods --

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

  OMEXMLServiceImpl service;
  IMetadata meta = service.createOMEXMLMetadata();

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
  writer.saveBytes(0, img);
  writer.close();

  cout << "Done." << endl;
}

int main(int argc, const char *argv[]) {
  try {
    JavaTools::createJVM();
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
  catch (exception& e) {
    cout << "An unexpected C++ error occurred. " << e.what() << endl;
    return -5;
  }
}
