/*
 * #%L
 * OME library for reading the JPEG XR file format.
 * %%
 * Copyright (C) 2013 - 2015 Open Microscopy Environment:
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

package ome.jxr.ifd;

import java.nio.ByteBuffer;
import java.util.Arrays;

import ome.jxr.image.OutputColorFormat;

/**
 * Enumeration of available pixel format entries. Naming of entries follows
 * Rec.ITU-T T.832 (01/2012) - table A.6. Internally, the identifier for each
 * entry omits the {@link PixelFormat#COMMON_PART}.
 *
 * <dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 */
public enum PixelFormat {

  RGB24(0x0D, 3, PixelType.UINT8, OutputColorFormat.RGB),
  BGR24(0x0C, 3, PixelType.UINT8, OutputColorFormat.RGB),
  BGR32(0x0E, 3, PixelType.UINT8, OutputColorFormat.RGB),
  RGB48(0x15, 3, PixelType.UINT16, OutputColorFormat.RGB),
  RGB48FixedPoint(0x12, 3, PixelType.SINT16, OutputColorFormat.RGB),
  RGB48Half(0x3B, 3, PixelType.FLOAT16, OutputColorFormat.RGB),
  RGB96FixedPoint(0x18, 3, PixelType.SINT32, OutputColorFormat.RGB),
  RGB64FixedPoint(0x40, 3, PixelType.SINT16, OutputColorFormat.RGB),
  RGB64Half(0x42, 3, PixelType.FLOAT16, OutputColorFormat.RGB),
  RGB128FixedPoint(0x41, 3, PixelType.SINT32, OutputColorFormat.RGB),
  RGB128Float(0x1B, 3, PixelType.FLOAT32, OutputColorFormat.RGB),
  BGRA32(0x0F, 4, true, PixelType.UINT8, OutputColorFormat.RGB),
  RGBA64(0x16, 4, true, PixelType.UINT16, OutputColorFormat.RGB),
  RGBA64FixedPoint(0x1D, 4, true, PixelType.SINT16, OutputColorFormat.RGB),
  RGBA64Half(0x3A, 4, true, PixelType.FLOAT16, OutputColorFormat.RGB),
  RGBA128FixedPoint(0x1E, 4, true, PixelType.SINT32, OutputColorFormat.RGB),
  RGBA128Float(0x19, 4, true, PixelType.FLOAT32, OutputColorFormat.RGB),
  PBGRA32(0x10, 4, true, PixelType.UINT8, OutputColorFormat.RGB),
  PRGBA64(0x17, 4, true, PixelType.UINT8, OutputColorFormat.RGB),
  PRGBA128Float(0x1A, 4, true, PixelType.FLOAT32, OutputColorFormat.RGB),
  CMYK32(0x1c, 4, false, PixelType.UINT8, OutputColorFormat.CMYK),
  CMYKA40(0x2c, 5, true, PixelType.UINT8, OutputColorFormat.CMYK),
  CMYK64(0x1f, 4, false, PixelType.UINT16, OutputColorFormat.CMYK),
  CMYKA80(0x2d, 5, true, PixelType.UINT16, OutputColorFormat.CMYK),
  CHANNELS_3_24(0x20, 3, false, PixelType.UINT8, OutputColorFormat.NCOMPONENT),
  CHANNELS_4_32(0x21, 4, false, PixelType.UINT8, OutputColorFormat.NCOMPONENT),
  CHANNELS_5_40(0x22, 5, false, PixelType.UINT8, OutputColorFormat.NCOMPONENT),
  CHANNELS_6_48(0x23, 6, false, PixelType.UINT8, OutputColorFormat.NCOMPONENT),
  CHANNELS_7_56(0x24, 7, false, PixelType.UINT8, OutputColorFormat.NCOMPONENT),
  CHANNELS_8_64(0x25, 8, false, PixelType.UINT8, OutputColorFormat.NCOMPONENT),
  CHANNELS_3_Alpha32(0x2e, 4, true, PixelType.UINT8, OutputColorFormat.NCOMPONENT),
  CHANNELS_4_Alpha40(0x2f, 5, true, PixelType.UINT8, OutputColorFormat.NCOMPONENT),
  CHANNELS_5_Alpha48(0x30, 6, true, PixelType.UINT8, OutputColorFormat.NCOMPONENT),
  CHANNELS_6_Alpha56(0x31, 7, true, PixelType.UINT8, OutputColorFormat.NCOMPONENT),
  CHANNELS_7_Alpha64(0x32, 8, true, PixelType.UINT8, OutputColorFormat.NCOMPONENT),
  CHANNELS_8_Alpha72(0x33, 9, true, PixelType.UINT8, OutputColorFormat.NCOMPONENT),
  CHANNELS_3_48(0x26, 3, false, PixelType.UINT16, OutputColorFormat.NCOMPONENT),
  CHANNELS_4_64(0x27, 4, false, PixelType.UINT16, OutputColorFormat.NCOMPONENT),
  CHANNELS_5_80(0x28, 5, false, PixelType.UINT16, OutputColorFormat.NCOMPONENT),
  CHANNELS_6_96(0x29, 6, false, PixelType.UINT16, OutputColorFormat.NCOMPONENT),
  CHANNELS_7_112(0x2a, 7, false, PixelType.UINT16, OutputColorFormat.NCOMPONENT),
  CHANNELS_8_128(0x2b, 8, false, PixelType.UINT16, OutputColorFormat.NCOMPONENT),
  CHANNELS_3_Alpha64(0x34, 4, true, PixelType.UINT16, OutputColorFormat.NCOMPONENT),
  CHANNELS_4_Alpha80(0x35, 5, true, PixelType.UINT16, OutputColorFormat.NCOMPONENT),
  CHANNELS_5_Alpha96(0x36, 6, true, PixelType.UINT16, OutputColorFormat.NCOMPONENT),
  CHANNELS_6_Alpha112(0x37, 7, true, PixelType.UINT16, OutputColorFormat.NCOMPONENT),
  CHANNELS_7_Alpha128(0x38, 8, true, PixelType.UINT16, OutputColorFormat.NCOMPONENT),
  CHANNELS_8_Alpha144(0x39, 9, true, PixelType.UINT16, OutputColorFormat.NCOMPONENT),
  GRAY8(0x08, 1, false, PixelType.UINT8, OutputColorFormat.YONLY),
  GRAY16(0x0b, 1, false, PixelType.UINT16, OutputColorFormat.YONLY),
  GRAYFixedPoint16(0x13, 1, false, PixelType.SINT16, OutputColorFormat.YONLY),
  GRAYHALF16(0x3e, 1, false, PixelType.FLOAT16, OutputColorFormat.YONLY),
  GRAYFixedPoint32(0x3f, 1, false, PixelType.SINT32, OutputColorFormat.YONLY),
  GRAYFLOAT32(0x11, 1, false, PixelType.FLOAT32, OutputColorFormat.YONLY),
  BLACKWHITE(0x05, 1, false, PixelType.UINT1, OutputColorFormat.YONLY),
  BGR555(0x09, 3, false, PixelType.UINT16, OutputColorFormat.RGB),
  BGR565(0x0a, 3, false, PixelType.UINT16, OutputColorFormat.RGB),
  BGR101010(0x14, 3, false, PixelType.UINT10, OutputColorFormat.RGB),
  RGBE32(0x3d, 3, false, PixelType.FLOAT8, OutputColorFormat.RGBE),
  CMYKDIRECT32(0x54, 4, false, PixelType.UINT8, OutputColorFormat.CMYKDIRECT),
  CMYKDIRECT64(0x55, 4, false, PixelType.UINT16, OutputColorFormat.CMYKDIRECT),
  CMYKDIRECTAlpha40(0x56, 5, true, PixelType.UINT8, OutputColorFormat.CMYKDIRECT),
  CMYKDIRECTAlpha80(0x43, 5, true, PixelType.UINT16, OutputColorFormat.CMYKDIRECT),
  YCC420(0x44, 3, false, PixelType.UINT8, OutputColorFormat.YUV420),
  YCC422_16(0x45, 3, false, PixelType.UINT8, OutputColorFormat.YUV422),
  YCC422_20(0x46, 3, false, PixelType.UINT10, OutputColorFormat.YUV422),
  YCC422_32(0x47, 3, false, PixelType.UINT16, OutputColorFormat.YUV422),
  YCC444_24(0x48, 3, false, PixelType.UINT8, OutputColorFormat.YUV444),
  YCC444_30(0x49, 3, false, PixelType.UINT10, OutputColorFormat.YUV444),
  YCC444_48(0x4a, 3, false, PixelType.UINT16, OutputColorFormat.YUV444),
  YCC444FixedPoint(0x4b, 3, false, PixelType.SINT16, OutputColorFormat.YUV444),
  YCC420Alpha20(0x4c, 4, true, PixelType.UINT8, OutputColorFormat.YUV420),
  YCC422Alpha24(0x4d, 4, true, PixelType.UINT8, OutputColorFormat.YUV422),
  YCC422Alpha30(0x4e, 4, true, PixelType.UINT10, OutputColorFormat.YUV422),
  YCC422Alpha48(0x4f, 4, true, PixelType.UINT16, OutputColorFormat.YUV422),
  YCC444Alpha32(0x50, 4, true, PixelType.UINT8, OutputColorFormat.YUV444),
  YCC444Alpha40(0x51, 4, true, PixelType.UINT10, OutputColorFormat.YUV444),
  YCC444Alpha64(0x52, 4, true, PixelType.UINT16, OutputColorFormat.YUV444),
  YCC444AlphaFixedPoint(0x53, 4, true, PixelType.SINT16, OutputColorFormat.YUV444);

  public final static byte[] COMMON_PART = { 0x24, (byte) 0xC3, (byte) 0xDD,
      0x6F, 0x03, 0x4E, (byte) 0xFE, 0x4B, (byte) 0xB1, (byte) 0x85, 0x3D,
      0x77, 0x76, (byte) 0x8D, (byte) 0xC9 };

  private byte id;
  private int numberOfChannels;
  private boolean alphaChannel;
  private PixelType pixelType;
  private OutputColorFormat colorFormat;

  private PixelFormat(int id, int numberOfChannels, PixelType pixelType,
      OutputColorFormat colorFormat) {
    this(id, numberOfChannels, false, pixelType, colorFormat);
  }

  private PixelFormat(int id, int numberOfChannels, boolean alphaChannel,
      PixelType pixelType, OutputColorFormat colorFormat) {
    this.id = (byte) id;
    this.numberOfChannels = numberOfChannels;
    this.alphaChannel = alphaChannel;
    this.pixelType = pixelType;
    this.colorFormat = colorFormat;
  }

  public byte getId() {
    return id;
  }

  public byte[] getCanonicalId() {
    ByteBuffer bb = ByteBuffer.allocate(COMMON_PART.length+1);
    bb.put(COMMON_PART);
    bb.put(id);
    return bb.array();
  }

  public int getNumberOfChannels() {
    return numberOfChannels;
  }

  public boolean hasAlphaChannel() {
    return alphaChannel;
  }

  public PixelType getPixelType() {
    return pixelType;
  }

  public OutputColorFormat getColorFormat() {
    return colorFormat;
  }

  public static PixelFormat findById(byte[] id) {
    for (PixelFormat format : PixelFormat.values()) {
      if (Arrays.equals(format.getCanonicalId(), id)) {
        return format;
      }
    }
    throw new IllegalArgumentException("Unspecified pixel type id: "
        + PixelFormat.COMMON_PART + id);
  }

}
