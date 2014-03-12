/*
 * #%L
 * OME library for reading the JPEG XR file format.
 * %%
 * Copyright (C) 2013 - 2014 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package ome.jxr.parser;

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import loci.formats.codec.BitBuffer;
import ome.jxr.JXRException;
import ome.jxr.constants.File;
import ome.jxr.constants.Image;
import ome.jxr.image.ImagePlane;
import ome.jxr.metadata.IFDMetadata;

/**
 * Decodes the image data from a JPEG XR image file. The data has to be in the
 * form of an input stream and accompanying metadata has to be present to aid
 * the decompression process.
 *
 * <dl>
 * <dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-jxr/src/ome/jxr/datastream/JXRDecoder.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-jxr/src/ome/jxr/datastream/JXRDecoder.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 */
public final class DatastreamParser extends Parser {

  private int encoderVersion;

  private int reservedB;

  private boolean hardTilingFlag;

  private int reservedC;

  private boolean tilingFlag;

  private boolean frequencyModeCodestreamFlag;

  private int spatialXfrmSubordinate;

  private boolean indexTablePresentFlag;

  private int overlapMode;

  private boolean shortHeaderFlag;

  private boolean longWordFlag;

  private boolean windowingFlag;

  private boolean trimFlexbitsFlag;

  private int reservedD;

  private boolean redBlueNotSwappedFlag;

  private boolean premultipliedAlphaFlag;

  private boolean alphaImagePlaneFlag;

  private int outputClrFmt;

  private int outputBitdepth;

  public DatastreamParser(Parser parentParser, RandomAccessInputStream stream)
      throws JXRException {
    super(parentParser, stream);
    IFDParser ifdParser = (IFDParser) getParentParser();
    FileParser fileParser = (FileParser) ifdParser.getParentParser();

    parsingOffset = ifdParser.getIFDMetadata().getImageOffset();
    encoderVersion = fileParser.getEncoderVersion();
  }

  @Override
  public void parse() throws JXRException {
    super.parse(parsingOffset);
    try {
      // parse image header
      parseImageHeader();
      //ImagePlane primaryImagePlane = new ImagePlane();
      // for each image plane:
      //   parse image plane header
      // parse coded tiles
    } catch (IOException ioe) {
      throw new JXRException(ioe);
    }

  }

  private void parseImageHeader() throws IOException, JXRException {
    checkIfGDISignaturePresent();
    extractImageHeaderMetadata();
  }

  private void checkIfGDISignaturePresent() throws IOException, JXRException {
    stream.seek(parsingOffset);
    String signature = stream.readString(Image.GDI_SIGNATURE.length());
    if (!Image.GDI_SIGNATURE.equals(signature)) {
      throw new JXRException("Missing required image signature.");
    }
  }

  private void extractImageHeaderMetadata() throws IOException, JXRException {
    byte[] headerBytes = new byte[4];
    stream.readFully(headerBytes);
    BitBuffer bits = new BitBuffer(headerBytes);

    reservedB = bits.getBits(4);
    if (reservedB != Image.RESERVED_B) {
      throw new JXRException("Codestream version mismatch! Decoder supports"
          + " only version: " + File.CODESTREAM_VERSION);
    }

    hardTilingFlag = (bits.getBits(1) == 1);

    if (encoderVersion != File.CODESTREAM_VERSION) {
      reservedC = bits.getBits(3);
    } else {
      bits.skipBits(3);
    }

    tilingFlag = (bits.getBits(1) == 1);
    frequencyModeCodestreamFlag = (bits.getBits(1) == 1);
    spatialXfrmSubordinate = bits.getBits(3);

    indexTablePresentFlag = (bits.getBits(1) == 1);
    if (frequencyModeCodestreamFlag && !indexTablePresentFlag) {
      throw new JXRException("Codestream version mismatch! Decoder supports"
          + " only version: " + File.CODESTREAM_VERSION);
    }

    overlapMode = bits.getBits(2);
    shortHeaderFlag = (bits.getBits(1) == 1);
    longWordFlag = (bits.getBits(1) == 1);
    windowingFlag = (bits.getBits(1) == 1);
    trimFlexbitsFlag = (bits.getBits(1) == 1);

    reservedD = bits.getBits(1);

    redBlueNotSwappedFlag = (bits.getBits(1) == 1);
    premultipliedAlphaFlag = (bits.getBits(1) == 1);
    alphaImagePlaneFlag = (bits.getBits(1) == 1);
    outputClrFmt = bits.getBits(4);
    outputBitdepth = bits.getBits(4);
  }

  public void close() throws IOException {
    super.close();
  }
}
