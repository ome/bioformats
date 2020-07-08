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
import java.util.Comparator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.UnsupportedCompressionException;
import loci.formats.codec.ZlibCodec;
import loci.formats.meta.MetadataStore;
import loci.formats.services.MDBService;
import ome.xml.model.enums.NamingConvention;
import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.units.quantity.Length;
import ome.units.quantity.Time;

/**
 * Reader for Cellomics C01 files.
 */
public class CellomicsReader extends FormatReader {

  // -- Constants --

  public static final int C01_MAGIC_BYTES = 16;

  // -- Fields --

  // A typical Cellomics file name is
  // WHICA-VTI1_090915160001_A01f00o1.DIB
  // The plate name is:
  // WHICA-VTI1_090915160001
  // The well name is A01
  // The site / field is 00
  // the channel is 1
  //
  // The channel prefix can be "o" or "d"
  // Both site and channel are optional.
  //
  // The pattern greedily captures:
  // The plate name in group 1
  // The well name in group 2
  // The field, optionally, in group 3
  // The channel, optionally, in group 4

  private static final Pattern PATTERN_O = Pattern.compile("(.*)_(\\p{Alpha}\\d{2})(f\\d{2,3})?(o\\d+)?[^_]+$");
  private static final Pattern PATTERN_D = Pattern.compile("(.*)_(\\p{Alpha}\\d{2})(f\\d{2,3})?(d\\d+)?[^_]+$");

  private Pattern cellomicsPattern;

  private ArrayList<ChannelFile> files = new ArrayList<ChannelFile>();

  private ArrayList<String> metadataFiles = new ArrayList<String>();

  // -- Constructor --

  /** Constructs a new Cellomics reader. */
  public CellomicsReader() {
    super("Cellomics C01", new String[] {"c01", "dib"});
    domains = new String[] {FormatTools.LM_DOMAIN, FormatTools.HCS_DOMAIN};
    datasetDescription = "One or more .c01 files";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 4;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readInt() == C01_MAGIC_BYTES;
  }

  /* @see loci.formats.IFormatReader#getDomains() */
  @Override
  public String[] getDomains() {
    FormatTools.assertId(currentId, true, 1);
    return new String[] {FormatTools.HCS_DOMAIN};
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int[] zct = getZCTCoords(no);

    ChannelFile file = lookupFile(getSeries(), zct[1]);
    if (file != null && file.filename != null) {
      try (RandomAccessInputStream s = getDecompressedStream(file.filename)) {
        int planeSize = FormatTools.getPlaneSize(this);
        s.seek(52 + zct[0] * planeSize);
        readPlane(s, x, y, w, h, buf);
      }
    }
    else {
      Arrays.fill(buf, (byte) 0);
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      files.clear();
      cellomicsPattern = null;
    }
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    if (noPixels) {
      return metadataFiles.toArray(new String[metadataFiles.size()]);
    }

    ArrayList<String> seriesFiles = new ArrayList<String>();
    seriesFiles.addAll(metadataFiles);
    for (int c=0; c<getSizeC(); c++) {
      ChannelFile f = lookupFile(getSeries(), c);
      if (f != null && f.filename != null) {
        seriesFiles.add(f.filename);
      }
    }
    return seriesFiles.toArray(new String[seriesFiles.size()]);
  }

  /* @see loci.formats.IFormatReader#getUsedFiles(boolean) */
  public String[] getUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    if (noPixels) {
      return metadataFiles.toArray(new String[metadataFiles.size()]);
    }

    ArrayList<String> allFiles = new ArrayList<String>();
    allFiles.addAll(metadataFiles);
    for (ChannelFile f : files) {
      allFiles.add(f.filename);
    }
    return allFiles.toArray(new String[allFiles.size()]);
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    // look for files with similar names
    Location baseFile = new Location(id).getAbsoluteFile();
    Location parent = baseFile.getParentFile();
    ArrayList<String> pixelFiles = new ArrayList<String>();

    String plateName = getPlateName(baseFile.getName());
    String mdbFile = null;

    if (plateName != null && isGroupFiles()) {
      String[] list = parent.list();
      for (String f : list) {
        boolean hasPlateName = plateName.equals(getPlateName(f));
        Location loc = new Location(parent, f);
        if (hasPlateName && (checkSuffix(f, "c01") || checkSuffix(f, "dib"))) {
          if ((!f.startsWith(".") || !loc.isHidden()) && getChannel(f) >= 0) {
            pixelFiles.add(loc.getAbsolutePath());
          }
        }
        else if (hasPlateName) {
          metadataFiles.add(loc.getAbsolutePath());

          if (checkSuffix(f, "mdb")) {
            mdbFile = loc.getAbsolutePath();
          }
        }
      }
    }
    else pixelFiles.add(id);

    String[] filenames = pixelFiles.toArray(new String[pixelFiles.size()]);
    Arrays.sort(filenames, new Comparator<String>() {
        @Override
        public int compare(String f1, String f2) {

            int wellRow1 = getWellRow(f1);
            int wellCol1 = getWellColumn(f1);
            int field1 = getField(f1);
            int channel1 = getChannel(f1);

            int wellRow2 = getWellRow(f2);
            int wellCol2 = getWellColumn(f2);
            int field2 = getField(f2);
            int channel2 = getChannel(f2);

            if (wellRow1 < wellRow2){
                return -1;
            }else if (wellRow1 > wellRow2){
                return 1;
            }

            if (wellCol1 < wellCol2){
                return -1;
            }else if (wellCol1 > wellCol2){
                return 1;
            }

            if (field1 < field2){
                return -1;
            }else if (field1 > field2){
                return 1;
            }

            return channel1-channel2;

        }
    });

    int wellRows = 0;
    int wellColumns = 0;
    int fields = 0;

    ArrayList<Integer> uniqueRows = new ArrayList<Integer>();
    ArrayList<Integer> uniqueCols = new ArrayList<Integer>();
    ArrayList<Integer> uniqueFields = new ArrayList<Integer>();
    ArrayList<Integer> uniqueChannels = new ArrayList<Integer>();
    for (String f : filenames) {
      int wellRow = getWellRow(f);
      int wellCol = getWellColumn(f);
      int field = getField(f);
      int channel = getChannel(f);

      if (!uniqueRows.contains(wellRow)) uniqueRows.add(wellRow);
      if (!uniqueCols.contains(wellCol)) uniqueCols.add(wellCol);
      if (!uniqueFields.contains(field)) uniqueFields.add(field);
      if (!uniqueChannels.contains(channel)) uniqueChannels.add(channel);

      files.add(new ChannelFile(f, wellRow, wellCol, field, channel));
    }

    fields = uniqueFields.size();
    wellRows = uniqueRows.size();
    wellColumns = uniqueCols.size();

    for (int file=0; file<files.size(); file++) {
      ChannelFile f = files.get(file);
      f.series = file / uniqueChannels.size();
      f.channel = uniqueChannels.indexOf(f.channel);
    }

    core.clear();

    int seriesCount = files.size();
    if (uniqueChannels.size() > 0) {
      seriesCount /= uniqueChannels.size();
    }

    for (int i=0; i<seriesCount; i++) {
      core.add(new CoreMetadata());
    }

    in = getDecompressedStream(id);

    LOGGER.info("Reading header data");

    in.order(true);
    in.skipBytes(4);

    int x = in.readInt();
    int y = in.readInt();
    int nPlanes = in.readShort();
    int nBits = in.readShort();

    int compression = in.readInt();

    if (x * y * nPlanes * (nBits / 8) + 52 > in.length()) {
      throw new UnsupportedCompressionException(
        "Compressed pixel data is not yet supported.");
    }

    in.skipBytes(4);
    int pixelWidth = 0, pixelHeight = 0;

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      pixelWidth = in.readInt();
      pixelHeight = in.readInt();
      int colorUsed = in.readInt();
      int colorImportant = in.readInt();

      LOGGER.info("Populating metadata hashtable");

      addGlobalMeta("Image width", x);
      addGlobalMeta("Image height", y);
      addGlobalMeta("Number of planes", nPlanes);
      addGlobalMeta("Bits per pixel", nBits);
      addGlobalMeta("Compression", compression);
      addGlobalMeta("Pixels per meter (X)", pixelWidth);
      addGlobalMeta("Pixels per meter (Y)", pixelHeight);
      addGlobalMeta("Color used", colorUsed);
      addGlobalMeta("Color important", colorImportant);
    }

    Color[] channelColors = new Color[uniqueChannels.size()];
    String[] channelNames = new String[uniqueChannels.size()];
    Time[] exposureTimes = new Time[uniqueChannels.size()];
    if (mdbFile != null) {
      MDBService mdb = null;
      try {
        ServiceFactory factory = new ServiceFactory();
        mdb = factory.getInstance(MDBService.class);

        mdb.initialize(mdbFile);
        Vector<String[]> table = mdb.parseTable("asnProtocolChannel");
        if (table != null) {
          String[] header = table.get(0);
          int nameColumn = DataTools.indexOf(header, "Name");
          int exposureTimeColumn = DataTools.indexOf(header, "ExposureTime");
          int colorColumn = DataTools.indexOf(header, "CompositeColor");
          for (int r=1; r<table.size(); r++) {
            String[] row = table.get(r);
            if (nameColumn >= 0) {
              channelNames[r - 1] = row[nameColumn];
            }
            if (colorColumn >= 0) {
              try {
                int color = Integer.parseInt(row[colorColumn]);
                int alpha = (color >> 24) & 0xff;
                int blue = (color >> 16) & 0xff;
                int green = (color >> 8) & 0xff;
                int red = color & 0xff;

                channelColors[r - 1] = new Color(red, green, blue, alpha);
              }
              catch (NumberFormatException e) {
                LOGGER.debug("Could not parse channel color " + row[colorColumn], e);
              }
            }
            if (exposureTimeColumn >= 0) {
              try {
                double exposure = DataTools.parseDouble(row[exposureTimeColumn]);
                exposureTimes[r - 1] = FormatTools.getTime(exposure, null);
              }
              catch (NumberFormatException e) {
                LOGGER.debug("Could not parse exposure time " + row[exposureTimeColumn], e);
              }
            }
          }
        }
      }
      catch (DependencyException e) {
        LOGGER.warn("Could not parse MDB file", e);
      }
      finally {
        if (mdb != null) {
          mdb.close();
        }
      }
    }

    LOGGER.info("Populating core metadata");

    for (int i=0; i<getSeriesCount(); i++) {
      CoreMetadata ms = core.get(i);
      ms.sizeX = x;
      ms.sizeY = y;
      ms.sizeZ = nPlanes;
      ms.sizeT = 1;
      ms.sizeC = uniqueChannels.size();
      ms.imageCount = getSizeZ() * getSizeT() * getSizeC();
      ms.littleEndian = true;
      ms.dimensionOrder = "XYCZT";
      ms.pixelType =
        FormatTools.pixelTypeFromBytes(nBits / 8, false, false);
    }

    LOGGER.info("Populating metadata store");

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    store.setPlateID(MetadataTools.createLSID("Plate", 0), 0);
    store.setPlateName(plateName, 0);
    store.setPlateRowNamingConvention(NamingConvention.LETTER, 0);
    store.setPlateColumnNamingConvention(NamingConvention.NUMBER, 0);

    int realRows = wellRows;
    int realCols = wellColumns;

    if (getSeriesCount() == 1) {
      realRows = 1;
      realCols = 1;
    }
    else if (realRows <= 8 && realCols <= 12) {
      realRows = 8;
      realCols = 12;
    }
    else {
      realRows = 16;
      realCols = 24;
    }

    for (int row=0; row<realRows; row++) {
      for (int col=0; col<realCols; col++) {
        int well = row * realCols + col;

        if (getSeriesCount() == 1) {
          row = files.get(0).row;
          col = files.get(0).col;
        }

        store.setWellID(MetadataTools.createLSID("Well", 0, well), 0, well);
        store.setWellRow(new NonNegativeInteger(row), 0, well);
        store.setWellColumn(new NonNegativeInteger(col), 0, well);
      }
    }

    for (int i=0; i<getSeriesCount(); i++) {
      ChannelFile f = files.get(i * getSizeC());
      int row = f.row;
      int col = f.col;
      int field = f.field;
      int fieldIndex = uniqueFields.indexOf(field);
      store.setImageName(
        String.format("Well %s%02d, Field #%02d",
                  new String(Character.toChars(row+'A')),
                  col + 1, field), i);

      if (getSeriesCount() == 1) {
        row = 0;
        col = 0;
      }

      String imageID = MetadataTools.createLSID("Image", i);
      store.setImageID(imageID, i);
      if (row < realRows && col < realCols) {
        int wellIndex = row * realCols + col;

        String wellSampleID = MetadataTools.createLSID("WellSample",
          0, wellIndex, fieldIndex);
        store.setWellSampleID(wellSampleID, 0, wellIndex, fieldIndex);
        store.setWellSampleIndex(
          new NonNegativeInteger(i), 0, wellIndex, fieldIndex);
        store.setWellSampleImageRef(imageID, 0, wellIndex, fieldIndex);
      }
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      // physical dimensions are stored as pixels per meter - we want them
      // in microns per pixel
      double width = pixelWidth == 0 ? 0.0 : 1000000.0 / pixelWidth;
      double height = pixelHeight == 0 ? 0.0 : 1000000.0 / pixelHeight;

      Length sizeX = FormatTools.getPhysicalSizeX(width);
      Length sizeY = FormatTools.getPhysicalSizeY(height);
      for (int i=0; i<getSeriesCount(); i++) {
        if (sizeX != null) {
          store.setPixelsPhysicalSizeX(sizeX, i);
        }
        if (sizeY != null) {
          store.setPixelsPhysicalSizeY(sizeY, i);
        }

        for (int c=0; c<getEffectiveSizeC(); c++) {
          if (channelNames[c] != null) {
            store.setChannelName(channelNames[c], i, c);
          }
          if (channelColors[c] != null) {
            store.setChannelColor(channelColors[c], i, c);
          }
        }
        for (int p=0; p<getImageCount(); p++) {
          int[] zct = getZCTCoords(p);
          if (exposureTimes[zct[1]] != null) {
            store.setPlaneExposureTime(exposureTimes[zct[1]], i, p);
          }
        }
      }
    }
  }

  // -- Helper methods --

  private Matcher matchFilename(final String filename) {
    final String name = new Location(filename).getName();
    if (cellomicsPattern == null) {
      Matcher m = PATTERN_O.matcher(name);
      if (m.matches() && m.group(4) != null) {
        cellomicsPattern = PATTERN_O;
        return m;
      }
      else {
        cellomicsPattern = PATTERN_D;
      }
    }
    return cellomicsPattern.matcher(name);
  }
  private String getPlateName(final String filename) {
    Matcher m = matchFilename(filename);
    if (m.matches()) {
      return m.group(1);
    }
    return filename.substring(0, filename.lastIndexOf("."));
  }

  private String getWellName(String filename) {
    Matcher m = matchFilename(filename);
    if (m.matches()) {
        return m.group(2);
    }
    return null;
  }

  private int getWellRow(String filename) {
    String wellName = getWellName(filename);
    if ((wellName == null) || (wellName.length() < 1) ) return 0;
    int ord = wellName.toUpperCase().charAt(0) - 'A';
    if ((ord < 0) || (ord >= 26)) return 0;
    return ord;
  }

  private int getWellColumn(String filename) {
    String wellName = getWellName(filename);
    if ((wellName == null) || (wellName.length() <= 2)) return 0;
    if (! Character.isDigit(wellName.charAt(1))) return 0;
    if (! Character.isDigit(wellName.charAt(2))) return 0;
    return Integer.parseInt(wellName.substring(1, 3)) - 1;
  }

  private int getField(String filename) {
    Matcher m = matchFilename(filename);
    if (m.matches() && (m.group(3) != null)) {
      return Integer.parseInt(m.group(3).substring(1));
    }
    return 0;
  }

  private int getChannel(String filename) {
    Matcher m = matchFilename(filename);
    if (m.matches() && (m.group(4) != null)) {
      return Integer.parseInt(m.group(4).substring(1));
    }
    return -1;
  }

  private RandomAccessInputStream getDecompressedStream(String filename)
    throws FormatException, IOException
  {
    RandomAccessInputStream s = new RandomAccessInputStream(filename);
    if (checkSuffix(filename, "c01")) {
      LOGGER.info("Decompressing file");
      try {
        s.seek(4);
        ZlibCodec codec = new ZlibCodec();
        byte[] file = codec.decompress(s, null);
        return new RandomAccessInputStream(file);
      } finally {
        s.close();
      }
    }
    return s;
  }

  private ChannelFile lookupFile(int seriesIndex, int channel) {
    for (ChannelFile f : files) {
      if (f.series == seriesIndex && f.channel == channel) {
        return f;
      }
    }
    return null;
  }

  class ChannelFile {
    public String filename;
    public int row;
    public int col;
    public int field;
    public int channel;
    public int series;

    public ChannelFile(String filename, int row, int col, int field, int channel) {
      this.filename = filename;
      this.row = row;
      this.col = col;
      this.field = field;
      this.channel = channel;
    }
  }

}
