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

import java.util.ArrayList;
import java.util.List;

/**
 * Enumeration of available IFD entries. Naming of entries follows
 * Rec.ITU-T T.832 (01/2012) - table A.4. {@link Integer#MAX_VALUE} has been
 * used to indicate a variable number of entry values. Some entries are required
 * in an image conforming to the specification. Such entries can be retrieved
 * using the {@link #getRequiredEntries() getRequiredEntries} method.
 *
 * <dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 */
public enum IFDEntry {
  
  DOCUMENT_NAME(0x010D, IFDEntryType.UTF8, Integer.MAX_VALUE),
  IMAGE_DESCRIPTION(0x010E, IFDEntryType.UTF8, Integer.MAX_VALUE),
  EQUIPMENT_MAKE(0x010F, IFDEntryType.UTF8, Integer.MAX_VALUE),
  EQUIPMENT_MODEL(0x0110, IFDEntryType.UTF8, Integer.MAX_VALUE),
  PAGE_NAME(0x011D, IFDEntryType.UTF8, Integer.MAX_VALUE),
  PAGE_NUMBER(0x0129, IFDEntryType.USHORT, 2),
  SOFTWARE_NAME_VERSION(0x0131, IFDEntryType.UTF8, Integer.MAX_VALUE),
  DATE_TIME(0x0132, IFDEntryType.UTF8, 20),
  ARTIST_NAME(0x013B, IFDEntryType.UTF8, Integer.MAX_VALUE),
  HOST_COMPUTER(0x013C, IFDEntryType.UTF8, Integer.MAX_VALUE),
  COPYRIGHT_NOTICE(0x8298, IFDEntryType.UTF8, Integer.MAX_VALUE),
  COLOR_SPACE(0xA001, IFDEntryType.USHORT, 1),
  PIXEL_FORMAT(0xBC01, IFDEntryType.BYTE, 16, true),
  SPATIAL_XFRM_PRIMARY(0xBC02, IFDEntryType.ULONG, 1),
  IMAGE_TYPE(0xBC04, IFDEntryType.ULONG, 1),
  PTM_COLOR_INFO(0xBC05, IFDEntryType.BYTE, 4),
  PROFILE_LEVEL_CONTAINER(0xBC06, IFDEntryType.BYTE, Integer.MAX_VALUE),
  IMAGE_WIDTH(0xBC80, IFDEntryType.ULONG, 1, true),
  IMAGE_HEIGHT(0xBC81, IFDEntryType.ULONG, 1, true),
  WIDTH_RESOLUTION(0xBC82, IFDEntryType.FLOAT, 1),
  HEIGHT_RESOLUTION(0xBC83, IFDEntryType.FLOAT, 1),
  IMAGE_OFFSET(0xBCC0, IFDEntryType.ULONG, 1, true),
  IMAGE_BYTE_COUNT(0xBCC1, IFDEntryType.ULONG, 1, true),
  ALPHA_OFFSET(0xBCC2, IFDEntryType.ULONG, 1),
  ALPHA_BYTE_COUNT(0xBCC3, IFDEntryType.ULONG, 1),
  IMAGE_BAND_PRESENCE(0xBCC4, IFDEntryType.BYTE, 1),
  ALPHA_BAND_PRESENCE(0xBCC5, IFDEntryType.BYTE, 1),
  PADDING_DATA(0xEA1C, IFDEntryType.UNDEFINED, Integer.MAX_VALUE);

  private final short tag;
  private final IFDEntryType entryType;
  private final short count;
  private final boolean required;

  private IFDEntry(int tag, IFDEntryType entryType, int count) {
    this(tag, entryType, count, false);
  }

  private IFDEntry(int tag, IFDEntryType entryType, int count,
      boolean required) {
    this.tag = (short) tag;
    this.entryType = entryType;
    this.count = (short) count;
    this.required = required;
  }

  public short getTag() {
    return tag;
  }

  public IFDEntryType getEntryType() {
    return entryType;
  }

  public int getCount() {
    return count;
  }

  public boolean isRequired() {
    return required;
  }

  public static List<IFDEntry> getRequiredEntries() {
    List<IFDEntry> requiredEntries = new ArrayList<IFDEntry>();
    for (IFDEntry entry : IFDEntry.values()) {
      if (entry.required) {
        requiredEntries.add(entry);
      }
    }
    return requiredEntries;
  }

  public static IFDEntry findByTag(short tag) {
    for (IFDEntry entry : IFDEntry.values()) {
      if (entry.getTag() == tag) {
        return entry;
      }
    }
    throw new IllegalArgumentException(String.format("Unspecified entry tag: %X", tag));
  }
}
