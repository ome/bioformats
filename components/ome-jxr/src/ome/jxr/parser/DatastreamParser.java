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
import ome.jxr.constants.Image;
import ome.jxr.image.ColorFormat;

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

  private int widthMinus1, heightMinus1;

  private int numVerTilesMinus1 = 1, numHorTilesMinus1 = 1;

  private short[] tileWidthInMB, tileHeightInMB;

  private int topMargin, leftMargin, bottomMargin, rightMargin;

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
      checkIfGDISignaturePresent();
      extractImageHeaderMetadata();
      verifyCodestreamConformance();
      // ImagePlane primaryImagePlane = new ImagePlane();
      // for each image plane:
      // parse image plane header
      // parse coded tiles
    } catch (IOException ioe) {
      throw new JXRException(ioe);
    }
  }

  private void checkIfGDISignaturePresent() throws IOException, JXRException {
    stream.seek(parsingOffset);
    String signature = stream.readString(Image.GDI_SIGNATURE.length());
    if (!Image.GDI_SIGNATURE.equals(signature)) {
      throw new JXRException("Missing required image signature.");
    }
  }

  private void extractImageHeaderMetadata() throws IOException {
    byte[] bytes = new byte[4];
    stream.readFully(bytes);
    BitBuffer bits = new BitBuffer(bytes);

    reservedB = bits.getBits(4);
    hardTilingFlag = (bits.getBits(1) == 1);

    // Skip RESERVED_C in this version of the decoder
    bits.skipBits(3);

    tilingFlag = (bits.getBits(1) == 1);
    frequencyModeCodestreamFlag = (bits.getBits(1) == 1);
    spatialXfrmSubordinate = bits.getBits(3);
    indexTablePresentFlag = (bits.getBits(1) == 1);
    overlapMode = bits.getBits(2);
    shortHeaderFlag = (bits.getBits(1) == 1);
    longWordFlag = (bits.getBits(1) == 1);
    windowingFlag = (bits.getBits(1) == 1);
    trimFlexbitsFlag = (bits.getBits(1) == 1);

    // Skip RESERVED_D in this version of the decoder
    bits.skipBits(1);

    redBlueNotSwappedFlag = (bits.getBits(1) == 1);
    premultipliedAlphaFlag = (bits.getBits(1) == 1);
    alphaImagePlaneFlag = (bits.getBits(1) == 1);
    outputClrFmt = bits.getBits(4);
    outputBitdepth = bits.getBits(4);

    if (shortHeaderFlag) {
      widthMinus1 = stream.readUnsignedShort();
      heightMinus1 = stream.readUnsignedShort();
    } else {
      widthMinus1 = stream.readInt();
      heightMinus1 = stream.readInt();
    }

    if (tilingFlag) {
      bytes = new byte[3];
      stream.readFully(bytes);
      bits = new BitBuffer(bytes);
      numVerTilesMinus1 = bits.getBits(12);
      numHorTilesMinus1 = bits.getBits(12);

      tileWidthInMB = new short[numVerTilesMinus1];
      tileHeightInMB = new short[numHorTilesMinus1];

      if (shortHeaderFlag) {
        for (int i = 0; i < numVerTilesMinus1; i++) {
          tileWidthInMB[i] = stream.readByte();
        }
        for (int i = 0; i < numHorTilesMinus1; i++) {
          tileHeightInMB[i] = stream.readByte();
        }
      } else {
        for (int i = 0; i < numVerTilesMinus1; i++) {
          tileWidthInMB[i] = stream.readShort();
        }
        for (int i = 0; i < numHorTilesMinus1; i++) {
          tileHeightInMB[i] = stream.readShort();
        }
      }
    }

    if (windowingFlag) {
      bytes = new byte[3];
      stream.readFully(bytes);
      bits = new BitBuffer(bytes);
      topMargin = bits.getBits(6);
      leftMargin = bits.getBits(6);
      bottomMargin = bits.getBits(6);
      rightMargin = bits.getBits(6);
    }
  }

  private void verifyCodestreamConformance() throws JXRException {
    if (reservedB != Image.RESERVED_B) {
      throw new JXRException("Wrong value of RESERVED_B. " + "Expected: "
          + Image.RESERVED_B + ", found: " + reservedB);
    }
    if (spatialXfrmSubordinate > Image.SPATIAL_XFRM_SUBORDINATE_MAX) {
      throw new JXRException("Wrong value of SPATIAL_XFRM_SUBORDINATE. "
          + "Expected [0.." + Image.SPATIAL_XFRM_SUBORDINATE_MAX + "], "
          + "found: " + spatialXfrmSubordinate);
    }
    if ((frequencyModeCodestreamFlag || numVerTilesMinus1 > 0 || numHorTilesMinus1 > 0)
        && !indexTablePresentFlag) {
      throw new JXRException(
          "FREQUENCY_MODE_CODESTREAM_FLAG missing required values.");
    }
    if (overlapMode == Image.OVERLAP_MODE_RESERVED) {
      throw new JXRException("Reserved value of OVERLAP_MODE: " + overlapMode);
    }
    if (outputClrFmt > Image.OUTPUT_CLR_FMT_MAX) {
      throw new JXRException("Wrong value of OUTPUT_CLR_FMT. "
          + "Expected [0.." + Image.OUTPUT_CLR_FMT_MAX + "], " + "found: "
          + outputClrFmt);
    }
    if (!ColorFormat.RGB.equals(ColorFormat.findById(outputClrFmt))
        && redBlueNotSwappedFlag) {
      throw new JXRException("Wrong value of RED_BLUE_NOT_SWAPPED_FLAG.");
    }
    if ((ColorFormat.YUV420.equals(ColorFormat.findById(outputClrFmt)) || ColorFormat.YUV422
        .equals(ColorFormat.findById(outputClrFmt)))
        && (widthMinus1 + 1) % 2 != 0) {
      throw new JXRException("Wrong value of WIDTH_MINUS1.");
    }
  }

  public void close() throws IOException {
    super.close();
  }
}
