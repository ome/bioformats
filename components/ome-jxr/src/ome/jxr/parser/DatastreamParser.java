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
import ome.jxr.image.FrequencyBand;
import ome.jxr.image.Bitdepth;

/**
 * Parses the initial elements (image header, image plane headers(-s)) of the
 * image codestream. Conducts validation of values according to the file format
 * specification.
 *
 * <dl>
 * <dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-jxr/src/ome/jxr/parser/DatastreamParser.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-jxr/src/ome/jxr/parser/DatastreamParser.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 */
public final class DatastreamParser extends Parser {

  // Image header fields
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
  private ColorFormat outputClrFmt;
  private Bitdepth outputBitdepth;
  private int widthMinus1, heightMinus1;
  private int numVerTilesMinus1 = 1, numHorTilesMinus1 = 1;
  private short[] tileWidthInMB, tileHeightInMB;
  private int topMargin, leftMargin, bottomMargin, rightMargin;

  // Image plane header fields
  private ColorFormat internalClrFormat;
  private boolean scaledFlag;
  private FrequencyBand bandsPresent;
  private int chromaCenteringX, chromaCenteringY;
  private int numComponentsMinus1, numComponentsExtendedMinus16;
  private int shiftBits;
  private int lenMantissa, expBias;
  private boolean lpImagePlaneUniformFlag, hpImagePlaneUniformFlag;

  public DatastreamParser(Parser parentParser, RandomAccessInputStream stream)
      throws JXRException {
    super(parentParser, stream);
  }

  @Override
  public void parse() throws JXRException {
    super.parse(parsingOffset);

    IFDParser ifdParser = (IFDParser) getParentParser();
    FileParser fileParser = (FileParser) ifdParser.getParentParser();

    parsingOffset = ifdParser.getIFDMetadata().getImageOffset();
    encoderVersion = fileParser.getEncoderVersion();

    try {
      checkIfGDISignaturePresent();
      extractImageHeaderMetadata();
      verifyCodestreamConformance();
      parsePrimaryImagePlaneHeader();
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
    BitBuffer bits = streamBytesToBits(4);

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
    outputClrFmt = ColorFormat.findById(bits.getBits(4));
    outputBitdepth = Bitdepth.findById(bits.getBits(4));

    if (shortHeaderFlag) {
      widthMinus1 = stream.readUnsignedShort();
      heightMinus1 = stream.readUnsignedShort();
    } else {
      widthMinus1 = stream.readInt();
      heightMinus1 = stream.readInt();
    }

    if (tilingFlag) {
      bits = streamBytesToBits(3);
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
      bits = streamBytesToBits(3);
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
    if (!ColorFormat.RGB.equals(outputClrFmt) && redBlueNotSwappedFlag) {
      throw new JXRException("Wrong value of RED_BLUE_NOT_SWAPPED_FLAG.");
    }
    if ((ColorFormat.YUV420.equals(outputClrFmt) || ColorFormat.YUV422
        .equals(outputClrFmt)) && (widthMinus1 + 1) % 2 != 0) {
      throw new JXRException("Wrong value of WIDTH_MINUS1.");
    }
  }

  private void parsePrimaryImagePlaneHeader() throws IOException {
    BitBuffer bits = streamBytesToBits(1);

    ColorFormat internalClrFmt = ColorFormat.findById(bits.getBits(3));
    scaledFlag = (bits.getBits(1) == 1);
    bandsPresent = FrequencyBand.findById(bits.getBits(4));

    if (ColorFormat.YUV444.equals(internalClrFmt)
        || ColorFormat.YUV420.equals(internalClrFmt)
        || ColorFormat.YUV422.equals(internalClrFmt)) {
      bits = streamBytesToBits(1);
      if (ColorFormat.YUV420.equals(internalClrFmt)
          || ColorFormat.YUV422.equals(internalClrFmt)) {
        // Skip RESERVED_E_BIT in this version of the decoder
        bits.skipBits(1);
        chromaCenteringX = bits.getBits(3);
      } else {
        // Skip RESERVED_F in this version of the decoder
        bits.skipBits(4);
      }
      if (ColorFormat.YUV420.equals(internalClrFmt)) {
        // Skip RESERVED_G_BIT in this version of the decoder
        bits.skipBits(1);
        chromaCenteringY = bits.getBits(3);
      } else {
        // Skip RESERVED_H in this version of the decoder
        bits.skipBits(4);
      }
    } else if (ColorFormat.NCOMPONENT.equals(internalClrFmt)) {
      bits = streamBytesToBits(2);
      numComponentsMinus1 = bits.getBits(4);
      if (numComponentsMinus1 == 0xf) {
        numComponentsExtendedMinus16 = bits.getBits(12);
      } else {
        // Skip RESERVED_H in this version of the decoder
        bits.skipBits(4);
        stream.seek(stream.getFilePointer() - 1);
      }
    }

    if (Bitdepth.BD16.equals(outputBitdepth)
        || Bitdepth.BD16S.equals(outputBitdepth)
        || Bitdepth.BD32S.equals(outputBitdepth)) {
      bits = streamBytesToBits(1);
      int shiftBits = bits.getBits(8);
    }
    if (Bitdepth.BD32F.equals(outputBitdepth)) {
      bits = streamBytesToBits(2);
      lenMantissa = bits.getBits(8);
      expBias = bits.getBits(8);
    }

    bits = streamBytesToBits(1);
    boolean dcImagePlaneUniformFlag = (bits.getBits(1) == 1);
    if (dcImagePlaneUniformFlag) {
      // DC_QP()
    }

    if (!FrequencyBand.DCONLY.equals(bandsPresent)) {
      // Skip RESERVED_I_BIT in this version of the decoder
      bits.skipBits(1);
      lpImagePlaneUniformFlag = (bits.getBits(1) == 1);
      if (lpImagePlaneUniformFlag) {
        // NumLPQPs = 1
        // LP_QP()
      }
      if (!FrequencyBand.NOHIGHPASS.equals(bandsPresent)) {
        // Skip RESERVED_J_BIT in this version of the decoder
        bits.skipBits(1);
        hpImagePlaneUniformFlag = (bits.getBits(1) == 1);
        if (hpImagePlaneUniformFlag) {
          // NumHPQPs = 1
          // HP_QP()
        }
      }
    }

    while (!bits.isBitOnByteBoundary()) {
      bits.skipBits(1);
    }
  }

  private BitBuffer streamBytesToBits(int numberOfBytes) throws IOException {
    byte[] bytes = new byte[numberOfBytes];
    stream.readFully(bytes);
    return new BitBuffer(bytes);
  }

  public void close() throws IOException {
    super.close();
  }
}
