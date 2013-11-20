/*
 * #%L
 * OME library for reading the JPEG XR file format.
 * %%
 * Copyright (C) 2013 Open Microscopy Environment:
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

/**
 * Enumeration of available Pixel Format entries. Naming of entries follows
 * Rec.ITU-T T.832 (01/2012) - table A.6.
 *
 * <dl>
 * <dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-jxr/src/ome/jxr/ifd/PixelFormat.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-jxr/src/ome/jxr/ifd/PixelFormat.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 */
public enum PixelFormat {

  RGB24(PixelFormat.COMMON_PART + "0D", 3, PixelType.UINT8),
  BGR24(PixelFormat.COMMON_PART + "0C", 3, PixelType.UINT8),
  BGR32(PixelFormat.COMMON_PART + "0E", 3, PixelType.UINT8),
  RGB48(PixelFormat.COMMON_PART + "15", 3, PixelType.UINT16),
  RGB48FixedPoint(PixelFormat.COMMON_PART + "12", 3, PixelType.SINT16),
  RGB48Half(PixelFormat.COMMON_PART + "3B", 3, PixelType.FLOAT16),
  RGB96FixedPoint(PixelFormat.COMMON_PART + "18", 3, PixelType.SINT32),
  RGB64FixedPoint(PixelFormat.COMMON_PART + "40", 3, PixelType.SINT16),
  RGB64Half(PixelFormat.COMMON_PART + "42", 3, PixelType.FLOAT16),
  RGB128FixedPoint(PixelFormat.COMMON_PART + "41", 3, PixelType.SINT32),
  RGB128BFloat(PixelFormat.COMMON_PART + "1B", 3, PixelType.FLOAT32),
  BGRA32(PixelFormat.COMMON_PART + "0F", 4, true, PixelType.UINT8),
  RGBA64(PixelFormat.COMMON_PART + "16", 4, true, PixelType.UINT16),
  RGBA64FixedPoint(PixelFormat.COMMON_PART + "1D", 4, true, PixelType.SINT16),
  RGBA64Half(PixelFormat.COMMON_PART + "3A", 4, true, PixelType.FLOAT16),
  RGBA128FixedPoint(PixelFormat.COMMON_PART + "1E", 4, true, PixelType.SINT32),
  RGBA128Float(PixelFormat.COMMON_PART + "19", 4, true, PixelType.FLOAT32),
  PBGRA32(PixelFormat.COMMON_PART + "10", 4, true, PixelType.UINT8),
  PRGBA64(PixelFormat.COMMON_PART + "17", 4, true, PixelType.UINT8),
  PRGBA128Float(PixelFormat.COMMON_PART + "1A", 4, true, PixelType.FLOAT32);

  public final static String COMMON_PART = "24C3DD6F034EFE4BB1853D77768DC9"; 

  private String id;
  private int numberOfChannels;
  private boolean alphaChannel;
  private PixelType pixelType;

  private PixelFormat(String id, int numberOfChannels, PixelType pixelType) {
    this(id, numberOfChannels, false, pixelType);
  }

  private PixelFormat(String id, int numberOfChannels, boolean alphaChannel,
      PixelType pixelType) {
    this.id = id;
    this.numberOfChannels = numberOfChannels;
    this.alphaChannel = alphaChannel;
    this.pixelType = pixelType;
  }

  public static PixelFormat findById(String id) {
    for (PixelFormat format : PixelFormat.values()) {
      if (format.getId().equals(id)) {
        return format;
      }
    }
    throw new IllegalArgumentException("Unspecified pixel type id: "
        + PixelFormat.COMMON_PART + id);
  }

  public String getId() {
    return id;
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

}
