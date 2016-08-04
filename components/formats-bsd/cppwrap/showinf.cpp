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

// A partial C++ version of the Bio-Formats showinf command line utility.
// For the original Java version, see:
//   components/bio-formats-tools/src/loci/formats/tools/ImageInfo.java

// for JVM initialization
#include "javaTools.h"

// for Bio-Formats C++ bindings
#include "formats-api-${release.version}.h"
#include "formats-bsd-${release.version}.h"
#include "formats-common-${release.version}.h"
using jace::JNIException;
using jace::proxy::java::io::IOException;
using jace::proxy::java::lang::Object;
using jace::proxy::java::lang::String;
using jace::proxy::java::util::Hashtable;
using jace::proxy::loci::common::xml::XMLTools;
using jace::proxy::loci::formats::ChannelFiller;
using jace::proxy::loci::formats::ChannelMerger;
using jace::proxy::loci::formats::ChannelSeparator;
using jace::proxy::loci::formats::DimensionSwapper;
using jace::proxy::loci::formats::FilePattern;
using jace::proxy::loci::formats::FileStitcher;
using jace::proxy::loci::formats::FormatException;
using jace::proxy::loci::formats::FormatTools;
using jace::proxy::loci::formats::IFormatHandler;
using jace::proxy::loci::formats::IFormatReader;
using jace::proxy::loci::formats::ImageReader;
using jace::proxy::loci::formats::MetadataTools;
using jace::proxy::loci::formats::meta::MetadataRetrieve;
using jace::proxy::loci::formats::meta::MetadataStore;
using jace::proxy::loci::formats::services::OMEXMLService;
using jace::proxy::loci::formats::services::OMEXMLServiceImpl;

#include <iostream>
using std::cout;
using std::endl;

#include <exception>
using std::exception;

#include <string>
using std::string;

// for atoi on some platforms
#include <stdio.h>
#include <stdlib.h>

// for INT_MAX
#include <limits.h>

#define tf(x) (x ? "true" : "false")

// -- Fields --

String* id = NULL;
bool printVersion = false;
bool pixels = true;
bool doCore = true;
bool doMeta = true;
bool filter = true;
bool thumbs = false;
bool cMerge = false;
bool stitch = false;
bool separate = false;
bool expand = false;
bool omexml = false;
bool normalize = false;
String* omexmlVersion = NULL;
int firstPlane = 0;
int lastPlane = INT_MAX;
int series = 0;
int xCoordinate = 0, yCoordinate = 0, width = 0, height = 0;
String* swapOrder = NULL;
String* shuffleOrder = NULL;

IFormatReader* reader = NULL;
ImageReader* imageReader = NULL;
FileStitcher* fileStitcher = NULL;
ChannelFiller* channelFiller = NULL;
ChannelSeparator* channelSeparator = NULL;
ChannelMerger* channelMerger = NULL;
DimensionSwapper* dimSwapper = NULL;

OMEXMLService *service = NULL;

// -- Methods --

void parseArgs(int argc, const char *argv[]) {
  for (int i=1; i<argc; i++) {
    string arg = argv[i];
    if (arg.substr(0, 1).compare("-") == 0) {
      if (arg.compare("-nopix") == 0) pixels = false;
      else if (arg.compare("-version") == 0) printVersion = true;
      else if (arg.compare("-nocore") == 0) doCore = false;
      else if (arg.compare("-nometa") == 0) doMeta = false;
      else if (arg.compare("-nofilter") == 0) filter = false;
      else if (arg.compare("-thumbs") == 0) thumbs = true;
      else if (arg.compare("-merge") == 0) cMerge = true;
      else if (arg.compare("-stitch") == 0) stitch = true;
      else if (arg.compare("-separate") == 0) separate = true;
      else if (arg.compare("-expand") == 0) expand = true;
      else if (arg.compare("-omexml") == 0) omexml = true;
      else if (arg.compare("-normalize") == 0) normalize = true;
      else if (arg.compare("-xmlversion") == 0) {
        omexmlVersion = new String(argv[++i]);
      }
      else if (arg.compare("-crop") == 0) {
        String cropInfo(argv[++i]);
        StringArray cropTokens = cropInfo.split(",");
        xCoordinate = atoi(string(cropTokens[0]).c_str());
        yCoordinate = atoi(string(cropTokens[1]).c_str());
        width = atoi(string(cropTokens[2]).c_str());
        height = atoi(string(cropTokens[3]).c_str());
      }
      else if (arg.compare("-range") == 0) {
        firstPlane = atoi(argv[++i]);
        lastPlane = atoi(argv[++i]);
      }
      else if (arg.compare("-series") == 0) {
        series = atoi(argv[++i]);
      }
      else if (arg.compare("-swap") == 0) {
        swapOrder = new String(argv[++i]);
      }
      else if (arg.compare("-shuffle") == 0) {
        shuffleOrder = new String(argv[++i]);
      }
      else cout << "Ignoring unknown command flag: " << arg << endl;
    }
    else {
      if (!id) id = new String(arg);
      else cout << "Ignoring unknown argument: " << arg << endl;
    }
  }
}

void printUsage() {
  cout << "To test read a file in any format, run:" << endl <<
    "  showinf file [-nopix] [-nocore] [-nometa] [-thumbs] " << endl <<
    "    [-merge] [-stitch] [-separate] [-expand] [-omexml]" << endl <<
    "    [-normalize] [-range firstPlane lastPlane] [-series num]" << endl <<
    "    [-swap inputOrder] [-shuffle outputOrder]" << endl <<
    "    [-xmlversion v] [-crop x,y,w,h]" << endl <<
    "" << endl <<
    "  -version: print the library version and exit" << endl <<
    "      file: the image file to read" << endl <<
    "    -nopix: read metadata only, not pixels" << endl <<
    "   -nocore: do not output core metadata" << endl <<
    "   -nometa: do not parse format-specific metadata table" << endl <<
    " -nofilter: do not filter metadata fields" << endl <<
    "   -thumbs: read thumbnails instead of normal pixels" << endl <<
    "    -merge: combine separate channels into RGB image" << endl <<
    "   -stitch: stitch files with similar names" << endl <<
    " -separate: split RGB image into separate channels" << endl <<
    "   -expand: expand indexed color to RGB" << endl <<
    "   -omexml: populate OME-XML metadata" << endl <<
    "-normalize: normalize floating point images*" << endl <<
    "    -range: specify range of planes to read (inclusive)" << endl <<
    "   -series: specify which image series to read" << endl <<
    "     -swap: override the default input dimension order" << endl <<
    "  -shuffle: override the default output dimension order" << endl <<
    "  -xmlversion: specify which OME-XML version to generate" << endl <<
  //  "     -crop: crop images before displaying; argument is 'x,y,w,h'" << endl <<
    "" << endl <<
    "* = may result in loss of precision" << endl <<
    "" << endl;
}

void configureReaderPreInit() {
  if (omexml) {
    jobject null = NULL;
    String xml(null);
    if (omexmlVersion == NULL) omexmlVersion = new String(null);

    MetadataStore store = service->createOMEXMLMetadata(xml, *omexmlVersion);

    reader->setOriginalMetadataPopulated(true);
    if (!store.isNull()) reader->setMetadataStore(store);
  }

  // determine format
  cout << "Checking file format ";
  cout << "[" << imageReader->getFormat(*id) << "]" << endl;

  cout << "Initializing reader" << endl;
  if (stitch) {
    reader = fileStitcher = new FileStitcher(*reader, true);
    String pat = FilePattern::findPattern(*id);
    if (!pat.isNull()) id = new String(pat);
  }
  if (expand) reader = channelFiller = new ChannelFiller(*reader);
  if (separate) reader = channelSeparator = new ChannelSeparator(*reader);
  if (cMerge) reader = channelMerger = new ChannelMerger(*reader);
  if (swapOrder || shuffleOrder) {
    reader = dimSwapper = new DimensionSwapper(*reader);
  }

  ((IFormatHandler*) reader)->close();
  reader->setNormalized(normalize);
  reader->setMetadataFiltered(filter);
}

void configureReaderPostInit() {
  if (swapOrder) dimSwapper->swapDimensions(*swapOrder);
  if (shuffleOrder) dimSwapper->setOutputOrder(*shuffleOrder);
}

void checkWarnings() {
  int pixelType = reader->getPixelType();
  if (!normalize && (pixelType == FormatTools::FLOAT() ||
    pixelType == FormatTools::DOUBLE()))
  {
    cout << "Warning: Java does not support "
      "display of unnormalized floating point data." << endl;
    cout << "Please use the '-normalize' option "
      "to avoid receiving a cryptic exception." << endl;
  }

  if (reader->isRGB() && reader->getRGBChannelCount() > 4) {
    cout << "Warning: Java does not support "
      "merging more than 4 channels." << endl;
    cout << "Please use the '-separate' option "
      "to avoid receiving a cryptic exception." << endl;
  }
}

/* Reads core metadata from the currently initialized reader. */
void readCoreMetadata() {
  if (!doCore) return; // skip core metadata printout

  // read basic metadata
  cout << endl;
  cout << "Reading core metadata" << endl;
  cout << "Filename = " << *id << endl;
  StringArray used = reader->getUsedFiles();
  int usedLength = used.isNull() ? -1 : (int) used.length();
  bool usedValid = usedLength > 0;
  if (usedValid) {
    for (int u=0; u<usedLength; u++) {
      if (used[u].isNull()) {
        usedValid = false;
        break;
      }
    }
  }
  if (!usedValid) {
    cout <<
      "************ Warning: invalid used files list ************" << endl;
  }
  if (used.isNull()) {
    cout << "Used files = null" << endl;
  }
  else if (usedLength == 0) {
    cout << "Used files = []" << endl;
  }
  else if (usedLength > 1) {
    cout << "Used files:" << endl;
    for (int u=0; u<usedLength; u++) cout << "\t" << used[u] << endl;
  }
  else if (id && !id->equals(used[0])) {
    cout << "Used files = [" << used[0] << "]" << endl;
  }
  int seriesCount = reader->getSeriesCount();
  cout << "Series count = " << seriesCount << endl;
  MetadataStore ms = reader->getMetadataStore();
  MetadataRetrieve mr = service->asRetrieve(ms);
  for (int j=0; j<seriesCount; j++) {
    reader->setSeries(j);

    // read basic metadata for series #i
    int imageCount = reader->getImageCount();
    bool rgb = reader->isRGB();
    int sizeX = reader->getSizeX();
    int sizeY = reader->getSizeY();
    int sizeZ = reader->getSizeZ();
    int sizeC = reader->getSizeC();
    int sizeT = reader->getSizeT();
    int pixelType = reader->getPixelType();
    int effSizeC = reader->getEffectiveSizeC();
    int rgbChanCount = reader->getRGBChannelCount();
    bool indexed = reader->isIndexed();
    bool falseColor = reader->isFalseColor();
    ByteArray2D table8 = reader->get8BitLookupTable();
    ShortArray2D table16 = reader->get16BitLookupTable();
    int thumbSizeX = reader->getThumbSizeX();
    int thumbSizeY = reader->getThumbSizeY();
    bool little = reader->isLittleEndian();
    String dimOrder = reader->getDimensionOrder();
    bool orderCertain = reader->isOrderCertain();
    bool thumbnail = reader->isThumbnailSeries();
    bool interleaved = reader->isInterleaved();
    bool metadataComplete = reader->isMetadataComplete();

    // output basic metadata for series #i
    cout << "Series #" << j;
    if (j < mr.getImageCount()) {
      cout << " -- " << mr.getImageName(j);
    }
    cout << ":" << endl;
    cout << "\tImage count = " << imageCount << endl;
    cout << "\tRGB = " << tf(rgb) << " (" << rgbChanCount << ")";
    if (cMerge) cout << " (merged)";
    else if (separate) cout << " (separated)";
    cout << endl;
    if (rgb != (rgbChanCount != 1)) {
      cout << "\t************ Warning: RGB mismatch ************" << endl;
    }
    cout << "\tInterleaved = " << tf(interleaved) << endl;
    cout << "\tIndexed = " << tf(indexed) << " (" <<
      (falseColor ? "false" : "true") << " color";
    if (!table8.isNull()) {
      int numTables8 = table8.length();
      cout << ", 8-bit LUT: " << numTables8 << " x ";
      cout << "?" << endl;//TEMP
      //ByteArray firstTable8 = table8[0]; // FIXME
      //if (firstTable8.isNull()) cout << "null";
      //else cout << firstTable8.length();
    }
    if (!table16.isNull()) {
      int numTables16 = table16.length();
      cout << ", 16-bit LUT: " << numTables16 << " x ";
      cout << "?" << endl;//TEMP
      //ShortArray firstTable16 = table16[0]; // FIXME
      //if (firstTable16.isNull()) cout << "null";
      //else cout << firstTable16.length();
    }
    cout << ")" << endl;
    if (indexed && table8.isNull() && table16.isNull()) {
      cout << "\t************ Warning: no LUT ************" << endl;
    }
    if (!table8.isNull() && !table16.isNull()) {
      cout << "\t************ Warning: multiple LUTs ************" << endl;
    }
    cout << "\tWidth = " << sizeX << endl;
    cout << "\tHeight = " << sizeY << endl;
    cout << "\tSizeZ = " << sizeZ << endl;
    cout << "\tSizeT = " << sizeT << endl;
    cout << "\tSizeC = " << sizeC;
    if (sizeC != effSizeC) cout << " (effectively " << effSizeC << ")" << endl;
    if (imageCount != sizeZ * effSizeC * sizeT) {
      cout << "\t************ Warning: ZCT mismatch ************" << endl;
    }
    cout << "\tThumbnail size = " <<
      thumbSizeX << " x " << thumbSizeY << endl;
    cout << "\tEndianness = " <<
      (little ? "intel (little)" : "motorola (big)") << endl;
    cout << "\tDimension order = " << dimOrder <<
      (orderCertain ? " (certain)" : " (uncertain)") << endl;
    cout << "\tPixel type = " <<
      FormatTools::getPixelTypeString(pixelType) << endl;
    cout << "\tMetadata complete = " << tf(metadataComplete) << endl;
    cout << "\tThumbnail series = " << tf(thumbnail) << endl;
  }
}

void readPixels() {
  cout << endl;
  cout << "Reading";
  if (reader->getSeriesCount() > 1) cout << " series #" << series;
  cout << " pixel data ";
  int num = reader->getImageCount();
  if (firstPlane < 0) firstPlane = 0;
  if (firstPlane >= num) firstPlane = num - 1;
  if (lastPlane < 0) lastPlane = 0;
  if (lastPlane >= num) lastPlane = num - 1;
  if (lastPlane < firstPlane) lastPlane = firstPlane;

  int sizeX = reader->getSizeX();
  int sizeY = reader->getSizeY();
  int sizeC = reader->getSizeC();

  if (width == 0) width = sizeX;
  if (height == 0) height = sizeY;

  int pixelType = reader->getPixelType();

  cout << "(" << firstPlane << "-" << lastPlane << ") ";
  for (int i=firstPlane; i<=lastPlane; i++) {
    flush(cout);
    if (thumbs) reader->openThumbBytes(i);
    else reader->openBytes(i, xCoordinate, yCoordinate, width, height);
    cout << ".";
  }
  cout << " ";
  cout << "[done]" << endl;
}

void printGlobalMetadata() {
  cout << endl;
  cout << "Reading global metadata" << endl;
  Hashtable meta = reader->getGlobalMetadata();
  StringArray keys = MetadataTools::keys(meta);
  for (int i=0; i<keys.length(); i++) {
    Object value = meta.get(keys[i]);
    cout << keys[i] << ": " << value << endl;
  }
}

void printOriginalMetadata() {
  cout << endl;
  int seriesCount = reader->getSeriesCount();
  if (seriesCount > 1) {
    cout << "Reading series #" << series << " metadata" << endl;
  }
  else {
    cout << "Reading series metadata" << endl;
  }
  Hashtable meta = reader->getSeriesMetadata();
  StringArray keys = MetadataTools::keys(meta);
  for (int i=0; i<keys.length(); i++) {
    Object value = meta.get(keys[i]);
    cout << keys[i] << ": " << value << endl;
  }
}

void printOMEXML() {
  cout << endl;
  MetadataStore ms = reader->getMetadataStore();
  String version = service->getOMEXMLVersion(ms);
  if (!version.isNull()) cout << "Generating OME-XML" << endl;
  else cout << "Generating OME-XML (schema version " << version << ")" << endl;
  MetadataRetrieve mr = service->asRetrieve(ms);
  if (!mr.isNull()) {
    String xml = service->getOMEXML(mr);
    cout << XMLTools::indentXML(xml) << endl;
    service->validateOMEXML(xml);
  }
  else {
    cout << "The metadata could not be converted to OME-XML." << endl;
    if (omexmlVersion) {
      cout << omexmlVersion <<
        " is probably not a legal schema version." << endl;
    }
    else {
      cout << "The OME-XML Java library is probably not available." << endl;
    }
  }
}

void destroyObjects() {
  delete id;
  id = NULL;
  delete omexmlVersion;
  omexmlVersion = NULL;
  delete swapOrder;
  swapOrder = NULL;
  delete shuffleOrder;
  shuffleOrder = NULL;

  delete imageReader;
  imageReader = NULL;
  delete fileStitcher;
  fileStitcher = NULL;
  delete channelFiller;
  channelFiller = NULL;
  delete channelSeparator;
  channelSeparator = NULL;
  delete channelMerger;
  channelMerger = NULL;
  delete dimSwapper;
  dimSwapper = NULL;
  delete service;
  service = NULL;
}

/* Displays information on the given file. */
bool testRead(int argc, const char *argv[]) {
  parseArgs(argc, argv);
  if (printVersion) {
    cout << "Version: " << FormatTools::VERSION() << endl;
    cout << "VCS revision: " << FormatTools::VCS_REVISION() << endl;
    cout << "Build date: " << FormatTools::DATE() << endl;
    return true;
  }

  if (!id) {
    printUsage();
    return false;
  }

  service = new OMEXMLServiceImpl;
  reader = imageReader = new ImageReader;

  configureReaderPreInit();

  reader->setId(*id);

  configureReaderPostInit();
  checkWarnings();
  readCoreMetadata();
  reader->setSeries(series);

  // read pixels
  if (pixels) readPixels();

  // read format-specific metadata table
  if (doMeta) {
    printGlobalMetadata();
    printOriginalMetadata();
  }

  // output and validate OME-XML
  if (omexml) printOMEXML();

  destroyObjects();

  return true;
}

int main(int argc, const char *argv[]) {
  try {
    JavaTools::createJVM();
    testRead(argc, argv);
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
