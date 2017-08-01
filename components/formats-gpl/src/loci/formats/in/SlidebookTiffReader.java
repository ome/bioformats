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
import java.util.ArrayList;
import java.util.Arrays;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;
import ome.units.UNITS;
import ome.units.quantity.Length;

/**
 */
public class SlidebookTiffReader extends BaseTiffReader {

  // -- Constants --

  public static final String SLIDEBOOK_MAGIC_STRING = "SlideBook";

  private static final int X_POS_TAG = 65000;
  private static final int Y_POS_TAG = 65001;
  private static final int Z_POS_TAG = 65002;
  private static final int CHANNEL_TAG = 65004;
  private static final int PHYSICAL_SIZE_TAG = 65007;
  private static final int MAGNIFICATION_TAG = 65005;

  // -- Fields --

  private int lastFile = 0;
  private MinimalTiffReader[] readers;
  private String[] files;
  private ArrayList<String> channelNames = new ArrayList<String>();

  // -- Constructor --

  public SlidebookTiffReader() {
    super("Slidebook TIFF", new String[] {"tif", "tiff"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser tp = new TiffParser(stream);
    IFD ifd = tp.getFirstIFD();
    if (ifd == null) return false;
    String software = ifd.getIFDTextValue(IFD.SOFTWARE);
    if (software == null) return false;
    return software.equals(SLIDEBOOK_MAGIC_STRING) &&
      ifd.getComment().length() == 0 && (ifd.containsKey(X_POS_TAG) ||
      ifd.containsKey(Y_POS_TAG) || ifd.containsKey(Z_POS_TAG) ||
      ifd.containsKey(CHANNEL_TAG) || ifd.containsKey(PHYSICAL_SIZE_TAG) ||
      ifd.containsKey(MAGNIFICATION_TAG));
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      if (readers != null) {
        for (MinimalTiffReader reader : readers) {
          if (reader != null) {
            reader.close();
          }
        }
      }
      lastFile = 0;
      readers = null;
      files = null;
      channelNames.clear();
    }
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    return noPixels ? null : files;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (readers == null || lastFile < 0 || lastFile >= readers.length ||
      readers[lastFile] == null)
    {
      return super.get8BitLookupTable();
    }
    return readers[lastFile].get8BitLookupTable();
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (readers == null || lastFile < 0 || lastFile >= readers.length ||
      readers[lastFile] == null)
    {
      return super.get16BitLookupTable();
    }
    return readers[lastFile].get16BitLookupTable();
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int file = no / getSizeT();
    int plane = no % getSizeT();

    lastFile = file;

    return readers[file].openBytes(plane, buf, x, y, w, h);
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  @Override
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    put("Slidebook", "yes");

    if (isGroupFiles()) {
      // look for other TIFF files that belong to this dataset

      String timestamp = getTimestamp(currentId);

      Location parent =
        new Location(currentId).getAbsoluteFile().getParentFile();
      if (parent == null) {
        throw new FormatException("Unable to locate parent file to " + currentId);
      }
      String[] list = parent.list(true);
      Arrays.sort(list);
      ArrayList<String> matchingFiles = new ArrayList<String>();
      for (String f : list) {
        String path = new Location(parent, f).getAbsolutePath();
        if (isThisType(path) && getTimestamp(path).equals(timestamp)) {
          matchingFiles.add(path);
        }
      }

      files = matchingFiles.toArray(new String[matchingFiles.size()]);
    }
    else {
      files = new String[] {currentId};
    }

    readers = new MinimalTiffReader[files.length];

    CoreMetadata m = core.get(0);

    m.imageCount = ifds.size() * files.length;
    m.sizeT = ifds.size();

    for (int i=0; i<readers.length; i++) {
      readers[i] = new MinimalTiffReader();
      readers[i].setId(files[i]);

      String channelName = getFirstChannel(files[i]);
      if (!channelNames.contains(channelName)) {
        channelNames.add(channelName);
      }
    }

    m.sizeC = channelNames.size();
    m.sizeZ = getImageCount() / (getSizeT() * getSizeC());
    m.dimensionOrder = "XYTCZ";
  }

  /* @see BaseTiffReader#initMetadataStore() */
  @Override
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    Location file = new Location(currentId).getAbsoluteFile();
    if (file.getParentFile() != null) {
      store.setImageName(file.getParentFile().getName(), 0);
    }
    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      for (int c=0; c<getEffectiveSizeC(); c++) {
        if (c < channelNames.size()) {
          String name = channelNames.get(c);
          if (name != null) {
            if (name.indexOf(':') > 0) {
              name = name.substring(name.indexOf(':') + 1);
            }
            if (name.indexOf(';') > 0) {
              name = name.substring(0, name.indexOf(';'));
            }

            store.setChannelName(name.trim(), 0, c);
          }
        }
      }

      IFD ifd = ifds.get(0);
      String physicalSize = ifd.getIFDTextValue(PHYSICAL_SIZE_TAG);
      if (physicalSize != null) {
        Double size = new Double(physicalSize);
        if (size > 0) {
          store.setPixelsPhysicalSizeX(FormatTools.getPhysicalSizeX(size), 0);
          store.setPixelsPhysicalSizeY(FormatTools.getPhysicalSizeY(size), 0);
        }
      }

      String mag = ifd.getIFDTextValue(MAGNIFICATION_TAG);
      if (mag != null) {
        store.setInstrumentID(MetadataTools.createLSID("Instrument", 0), 0);
        store.setObjectiveID(MetadataTools.createLSID("Objective", 0, 0), 0, 0);
        store.setObjectiveCorrection(getCorrection("Other"), 0, 0);
        store.setObjectiveImmersion(getImmersion("Other"), 0, 0);
        store.setObjectiveNominalMagnification(new Double(mag), 0, 0);
      }

      final Double xn = Double.valueOf(ifd.getIFDTextValue(X_POS_TAG));
      final Double yn = Double.valueOf(ifd.getIFDTextValue(Y_POS_TAG));
      final Double zn = Double.valueOf(ifd.getIFDTextValue(Z_POS_TAG));

      final Length xl = new Length(xn, UNITS.REFERENCEFRAME);
      final Length yl = new Length(yn, UNITS.REFERENCEFRAME);
      final Length zl = new Length(zn, UNITS.REFERENCEFRAME);

      for (int i=0; i<getImageCount(); i++) {
        store.setPlanePositionX(xl, 0, i);
        store.setPlanePositionY(yl, 0, i);
        store.setPlanePositionZ(zl, 0, i);
      }
    }
  }

  // -- Helper methods --

  private String getTimestamp(String path) throws FormatException, IOException {
    RandomAccessInputStream s = new RandomAccessInputStream(path);
    TiffParser parser = new TiffParser(s);
    IFD ifd = parser.getFirstIFD();
    Object date = ifd.getIFDValue(IFD.DATE_TIME);
    s.close();

    return date == null ? null : date.toString();
  }

  private String getFirstChannel(String path)
    throws FormatException, IOException
  {
    RandomAccessInputStream s = new RandomAccessInputStream(path);
    TiffParser parser = new TiffParser(s);
    IFD ifd = parser.getFirstIFD();
    Object channel = ifd.getIFDValue(CHANNEL_TAG);
    s.close();
    parser.getStream().close();

    return channel == null ? null : channel.toString();
  }

}
