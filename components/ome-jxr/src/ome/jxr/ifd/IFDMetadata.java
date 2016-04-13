/*
 * #%L
 * OME library for reading the JPEG XR file format.
 * %%
 * Copyright (C) 2013 - 2016 Open Microscopy Environment:
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

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import loci.common.ByteArrayHandle;
import loci.common.DataTools;
import ome.jxr.JXRException;

/**
 * Provides access to metadata extracted from the IFD part of a JPEG XR file.
 * Adds simple logic to translate the raw byte data into primitive data types.
 * The getters don't return primitive language types, so as to allow returning
 * <code>null</code>. That value indicates a missing metadata element and cannot
 * be confused with a numerical value (e.g. 0). This class also verifies value
 * validity on a very basic level (no inferring of metadata value combination
 * meanings), and where needed - returns a default as dictated by the ITU-T
 * specification.
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 *
 * @deprecated See <a href="http://blog.openmicroscopy.org/file-formats/community/2016/01/06/format-support">blog post</a>
 */
@Deprecated
public class IFDMetadata {

  // TODO: PTM_COLOR_INFO
  // TODO: PROFILE_LEVEL_CONTAINER

  private long fileSizeInBytes;

  private Map<IFDEntry, byte[]> entries =
      new EnumMap<IFDEntry, byte[]>(IFDEntry.class);

  public IFDMetadata(long fileSizeInBytes) {
    this.fileSizeInBytes = fileSizeInBytes;
  }

  public void put(IFDEntry entry, byte[] value) {
    entries.put(entry, value);
  }

  public Integer getBitsPerPixel() throws JXRException {
    verifyRequiredEntries();
    PixelFormat pixelFormat = PixelFormat.findById(entries
        .get(IFDEntry.PIXEL_FORMAT));
    return pixelFormat.getPixelType().getBits();
  }

  public Integer getNumberOfChannels() throws JXRException {
    verifyRequiredEntries();
    PixelFormat pixelFormat = PixelFormat.findById(entries
        .get(IFDEntry.PIXEL_FORMAT));
    return pixelFormat.getNumberOfChannels();
  }

  public Long getImageWidth() throws JXRException {
    verifyRequiredEntries();
    return DataTools.bytesToLong(entries.get(IFDEntry.IMAGE_WIDTH), true);
  }

  public Long getImageHeight() throws JXRException {
    verifyRequiredEntries();
    return DataTools.bytesToLong(entries.get(IFDEntry.IMAGE_HEIGHT), true);
  }

  public Integer getImageOffset() throws JXRException {
    verifyRequiredEntries();
    return DataTools.bytesToInt(entries.get(IFDEntry.IMAGE_OFFSET), true);
  }

  public Long getImageByteCount() throws JXRException {
    verifyRequiredEntries();
    Long value = DataTools.bytesToLong(entries.get(IFDEntry.IMAGE_BYTE_COUNT),
        true);
    return value != 0 ? value : fileSizeInBytes-getImageOffset();
  }

  public String getDocumentName() throws IOException {
    return nullOrString(entries.get(IFDEntry.DOCUMENT_NAME));
  }

  public String getImageDescription() throws IOException {
    return nullOrString(entries.get(IFDEntry.IMAGE_DESCRIPTION));
  }

  public String getEquipmentMake() throws IOException {
    return nullOrString(entries.get(IFDEntry.EQUIPMENT_MAKE));
  }

  public String getEquipmentModel() throws IOException {
    return nullOrString(entries.get(IFDEntry.EQUIPMENT_MODEL));
  }

  public String getPageName() throws IOException {
    return nullOrString(entries.get(IFDEntry.PAGE_NAME));
  }

  public Short getPageNumber() {
    return nullOrShort(entries.get(IFDEntry.PAGE_NUMBER));
  }

  public String getSoftwareNameVersion() throws IOException {
    return nullOrString(entries.get(IFDEntry.SOFTWARE_NAME_VERSION));
  }

  public String getDateTime() throws IOException {
    return parseDateTime(entries.get(IFDEntry.DATE_TIME));
  }

  public String getArtistName() throws IOException {
    return nullOrString(entries.get(IFDEntry.ARTIST_NAME));
  }

  public String getHostComputer() throws IOException {
    return nullOrString(entries.get(IFDEntry.HOST_COMPUTER));
  }

  public String getCopyrightNotice() throws IOException {
    return nullOrString(entries.get(IFDEntry.COPYRIGHT_NOTICE));
  }

  public Short getColorSpace() {
    return parseColorSpace(entries.get(IFDEntry.COLOR_SPACE));
  }

  public Long getPrefferedSpatialTransformation() {
    Long value = nullOrLong(entries.get(IFDEntry.SPATIAL_XFRM_PRIMARY));
    return value != null ? value : 0;
  }

  public Long getImageType() {
    return nullOrLong(entries.get(IFDEntry.IMAGE_TYPE));
  }

  public Float getWidthResoulution() {
    Float value = nullOrFloat(entries.get(IFDEntry.WIDTH_RESOLUTION));
    return (value == null || value == 0) ? 96 : value;
  }

  public Float getHeightResoulution() {
    Float value = nullOrFloat(entries.get(IFDEntry.HEIGHT_RESOLUTION));
    return (value == null || value == 0) ? 96 : value;
  }

  public Long getAlphaOffset() {
    return nullOrLong(entries.get(IFDEntry.ALPHA_OFFSET));
  }

  public Long getAlphaByteCount() {
    return nullOrLong(entries.get(IFDEntry.ALPHA_BYTE_COUNT));
  }

  public Short getImageBandPresence() {
    return nullOrShort(entries.get(IFDEntry.IMAGE_BAND_PRESENCE));
  }

  public Short getAlphaBandPresence() {
    return nullOrShort(entries.get(IFDEntry.ALPHA_BAND_PRESENCE));
  }

  private String parseDateTime(byte[] value) throws IOException {
    String date = nullOrString(value);
    String unknownDate = "[0 ]{4}:[0 ]{2}:[0 ]{2} [0 ]{2}:[0 ]{2}:[0 ]{2}";
    if (date == null || date.matches(unknownDate)) {
      return null;
    } else {
      return date;
    }
  }

  private Short parseColorSpace(byte[] value) {
    Short colorSpace = nullOrShort(value);
    if (colorSpace == null) {
      return null;
    } else {
      return colorSpace == 1 || colorSpace == 0xffff ? colorSpace
          : (short) 0xffff;
    }
  }

  private String nullOrString(byte[] value) throws IOException {
    if (value != null) {
      return new ByteArrayHandle(value).readUTF();
    } else {
      return null;
    }
  }

  private Short nullOrShort(byte[] value) {
    if (value != null) {
      return DataTools.bytesToShort(value, true);
    } else {
      return null;
    }
  }

  private Long nullOrLong(byte[] value) {
    if (value != null) {
      return DataTools.bytesToLong(value, true);
    } else {
      return null;
    }
  }

  private Float nullOrFloat(byte[] value) {
    if (value != null) {
      return DataTools.bytesToFloat(value, true);
    } else {
      return null;
    }
  }

  private void verifyRequiredEntries() throws JXRException {
    if (entries.isEmpty()
        || !entries.keySet().containsAll(IFDEntry.getRequiredEntries())) {
      throw new JXRException("Metadata object is missing required IFD entries.");
    }
  }

}
