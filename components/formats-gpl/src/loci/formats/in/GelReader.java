/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

package loci.formats.in;

import java.io.IOException;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.TiffParser;
import loci.formats.tiff.TiffRational;
import ome.xml.model.primitives.Timestamp;
import ome.units.quantity.Length;

/**
 * GelReader is the file format reader for
 * Molecular Dynamics GEL TIFF files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class GelReader extends BaseTiffReader {

  // -- Constants --

  public static final String DATE_FORMAT = "yyyy:MM:dd";
  public static final String TIME_FORMAT = "HH:mm:ss";
  public static final String DATE_TIME_FORMAT = DATE_FORMAT + " " + TIME_FORMAT;

  public static final String[] FORMATS =
    new String[] {DATE_TIME_FORMAT, DATE_FORMAT, TIME_FORMAT};

  // GEL TIFF private IFD tags.
  private static final int MD_FILETAG = 33445;
  private static final int MD_SCALE_PIXEL = 33446;
  private static final int MD_LAB_NAME = 33448;
  private static final int MD_SAMPLE_INFO = 33449;
  private static final int MD_PREP_DATE = 33450;
  private static final int MD_PREP_TIME = 33451;
  private static final int MD_FILE_UNITS = 33452;

  // Scaling options
  private static final int SQUARE_ROOT = 2;
  private static final int LINEAR = 128;

  // -- Fields --

  private long fmt;

  // -- Constructor --

  /** Constructs a new GEL reader. */
  public GelReader() {
    super("Amersham Biosciences GEL", new String[] {"gel"});
    domains = new String[] {FormatTools.GEL_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser parser = new TiffParser(stream);
    parser.setDoCaching(false);
    IFD ifd = parser.getFirstIFD();
    if (ifd == null) return false;
    return ifd.containsKey(MD_FILETAG);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    IFD ifd = ifds.get(no);

    if (fmt == SQUARE_ROOT) {
      if (tiffParser == null) {
        initTiffParser();
      }

      float scale =
        ((TiffRational) ifd.getIFDValue(MD_SCALE_PIXEL)).floatValue();

      byte[] tmp = new byte[buf.length];

      // DO NOT call super.openBytes here. MinimalTiffReader will interpret
      // the pixels as half-floats, when really they are unsigned shorts.
      tiffParser.getSamples(ifds.get(no), tmp, x, y, w, h);

      int originalBytes = ifd.getBitsPerSample()[0] / 8;

      for (int i=0; i<tmp.length/4; i++) {
        long value = DataTools.bytesToInt(tmp, i*originalBytes,
          originalBytes, isLittleEndian());
        long square = value * value;
        float pixel = square * scale;
        DataTools.unpackBytes(Float.floatToIntBits(pixel), buf, i*4, 4,
          isLittleEndian());
      }
    }
    else super.openBytes(no, buf, x, y, w, h);

    return buf;
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initMetadata() */
  @Override
  protected void initMetadata() throws FormatException, IOException {
    ifds = tiffParser.getMainIFDs();
    if (ifds.size() > 1) {
      IFDList tmpIFDs = ifds;
      ifds = new IFDList();
      for (int i=0; i<tmpIFDs.size()/2; i++) {
        IFD ifd = new IFD();
        ifds.add(ifd);
        ifd.putAll(tmpIFDs.get(i*2 + 1));
        ifd.putAll(tmpIFDs.get(i*2));
        tiffParser.fillInIFD(ifd);
      }
    }

    IFD firstIFD = ifds.get(0);
    tiffParser.fillInIFD(firstIFD);

    super.initStandardMetadata();

    fmt = firstIFD.getIFDLongValue(MD_FILETAG, LINEAR);
    if (fmt == SQUARE_ROOT) core.get(0).pixelType = FormatTools.FLOAT;

    TiffRational scale = (TiffRational) firstIFD.getIFDValue(MD_SCALE_PIXEL);
    if (scale == null) scale = new TiffRational(1, 1);

    core.get(0).imageCount = ifds.size();
    core.get(0).sizeT = getImageCount();

    // ignore MD_COLOR_TABLE

    String info = firstIFD.getIFDStringValue(MD_SAMPLE_INFO);
    String prepDate = firstIFD.getIFDStringValue(MD_PREP_DATE);
    String prepTime = firstIFD.getIFDStringValue(MD_PREP_TIME);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      String units = firstIFD.getIFDStringValue(MD_FILE_UNITS);
      String lab = firstIFD.getIFDStringValue(MD_LAB_NAME);

      addGlobalMeta("Scale factor", scale);
      addGlobalMeta("Lab name", lab);
      addGlobalMeta("Sample info", info);
      addGlobalMeta("Date prepared", prepDate);
      addGlobalMeta("Time prepared", prepTime);
      addGlobalMeta("File units", units);
      addGlobalMeta("Data format",
        fmt == SQUARE_ROOT ? "square root" : "linear");
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    String parsedDate = DateTools.formatDate(prepDate, FORMATS);
    String parsedTime = DateTools.formatDate(prepTime, FORMATS);

    if (parsedDate != null) {
      store.setImageAcquisitionDate(new Timestamp(parsedDate), 0);
    }
    else if (parsedTime != null) {
      store.setImageAcquisitionDate(new Timestamp(parsedTime), 0);
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      Double pixelSize = new Double(scale.doubleValue());
      Length sizeX = FormatTools.getPhysicalSizeX(pixelSize);
      Length sizeY = FormatTools.getPhysicalSizeY(pixelSize);
      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }
    }
  }

}
