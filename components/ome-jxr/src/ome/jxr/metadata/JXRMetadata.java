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

package ome.jxr.metadata;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import loci.common.ByteArrayHandle;
import ome.jxr.JXRException;
import ome.jxr.ifd.IFDEntry;
import ome.jxr.ifd.PixelFormat;
import ome.scifio.common.DataTools;

/**
 * Provides access to metadata extracted from a JPEG XR file. Adds simple logic
 * to translate the raw byte data into primitive data types.
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 *
 * <dl>
 * <dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-jxr/src/ome/jxr/metadata/PixelType.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-jxr/src/ome/jxr/metadata/PixelType.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 */
public class JXRMetadata {

  // TODO: PTM_COLOR_INFO
  // TODO: PROFILE_LEVEL_CONTAINER
  // TODO: Verify that default values are returned where needed if tag not
  //       present. See spec.

  private Map<IFDEntry, byte[]> entries =
      new EnumMap<IFDEntry, byte[]>(IFDEntry.class);

  public void put(IFDEntry entry, byte[] value) {
    entries.put(entry, value);
  }

  public Integer getBitsPerPixel() throws JXRException {
    verifyRequiredElements();
    PixelFormat pixelFormat = PixelFormat.findById(entries
        .get(IFDEntry.PIXEL_FORMAT));
    return pixelFormat.getPixelType().getBits();
  }

  public Integer getNumberOfChannels() throws JXRException {
    verifyRequiredElements();
    PixelFormat pixelFormat = PixelFormat.findById(entries
        .get(IFDEntry.PIXEL_FORMAT));
    return pixelFormat.getNumberOfChannels();
  }

  public Long getImageWidth() throws JXRException {
    verifyRequiredElements();
    return DataTools.bytesToLong(entries.get(IFDEntry.IMAGE_WIDTH), true);
  }

  public Long getImageHeight() throws JXRException {
    verifyRequiredElements();
    return DataTools.bytesToLong(entries.get(IFDEntry.IMAGE_HEIGHT), true);
  }

  public Long getImageImageOffset() throws JXRException {
    verifyRequiredElements();
    return DataTools.bytesToLong(entries.get(IFDEntry.IMAGE_OFFSET), true);
  }

  public Long getImageByteCount() throws JXRException {
    verifyRequiredElements();
    return DataTools.bytesToLong(entries.get(IFDEntry.IMAGE_BYTE_COUNT), true);
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
    return nullOrString(entries.get(IFDEntry.DATE_TIME));
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
    return nullOrShort(entries.get(IFDEntry.COLOR_SPACE));
  }

  public Long getPrefferedSpatialTransformation() {
    return nullOrLong(entries.get(IFDEntry.SPATIAL_XFRM_PRIMARY));
  }

  public Long getImageType() {
    return nullOrLong(entries.get(IFDEntry.IMAGE_TYPE));
  }

  public Float getWidthResoulution() {
    return nullOrFloat(entries.get(IFDEntry.WIDTH_RESOLUTION));
  }

  public Float getHeightResoulution() {
    return nullOrFloat(entries.get(IFDEntry.HEIGHT_RESOLUTION));
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

  private void verifyRequiredElements() throws JXRException {
    if (!entries.isEmpty()) {
      if (!entries.keySet().containsAll(IFDEntry.getRequiredEntries())) {
        throw new JXRException("Metadata object is missing required IFD entries.");
      }
    }
  }

}
