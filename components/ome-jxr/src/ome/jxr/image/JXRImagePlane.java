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

package ome.jxr.image;

import ome.jxr.JXRException;
import ome.jxr.constants.File;
import ome.jxr.constants.Image;
import ome.scifio.io.BitBuffer;

public class JXRImagePlane {

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

  public JXRImagePlane(byte[] headerBytes)
      throws JXRException {
    BitBuffer bits = new BitBuffer(headerBytes);

    reservedB = bits.getBits(4);
    if (reservedB != Image.RESERVED_B) {
      throw new JXRException("Image codestream doesn't conform to"
          + "specification version: " + File.ENCODER_VERSION);
    }

    hardTilingFlag = (bits.getBits(1) == 1);

    // TODO: Refactor
    //if (encoderVersion != File.ENCODER_VERSION) {
      reservedC = bits.getBits(3);
    //} else {
    //  bits.skipBits(3);
    //}

    tilingFlag = (bits.getBits(1) == 1);
    frequencyModeCodestreamFlag = (bits.getBits(1) == 1);
    spatialXfrmSubordinate = bits.getBits(3);

    indexTablePresentFlag = (bits.getBits(1) == 1);
    if (frequencyModeCodestreamFlag && !indexTablePresentFlag) {
      throw new JXRException("Image codestream doesn't conform to"
          + "specification version: " + File.ENCODER_VERSION);
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

}
