//
// MetamorphTiffReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FilePattern;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;

import ome.xml.model.enums.NamingConvention;
import ome.xml.model.primitives.NonNegativeInteger;

/**
 * MetamorphTiffReader is the file format reader for TIFF files produced by
 * Metamorph software version 7.5 and above.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/MetamorphTiffReader.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/MetamorphTiffReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Thomas Caswell tcaswell at uchicago.edu
 */
public class MetamorphTiffReader extends BaseTiffReader {

  // -- Constants --

  private static final String DATE_FORMAT = "yyyyMMdd HH:mm:ss.SSS";

  // -- Fields --

  private String[] files;
  private int fieldCount = 0;
  private int wellRowCount = 0;
  private int wellColumnCount = 0;

  // -- Constructor --

  /** Constructs a new Metamorph TIFF reader. */
  public MetamorphTiffReader() {
    super("Metamorph TIFF", new String[] {"tif", "tiff"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.LM_DOMAIN, FormatTools.HCS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser tp = new TiffParser(stream);
    String comment = tp.getComment();
    if (comment == null) return false;
    comment = comment.trim();
    return comment.startsWith("<MetaData>") && comment.endsWith("</MetaData>");
  }

  /* @see loci.formats.IFormatReader#getDomains() */
  public String[] getDomains() {
    FormatTools.assertId(currentId, true, 1);
    String[] domain = new String[1];
    domain[0] =
      files.length == 1 ? FormatTools.LM_DOMAIN : FormatTools.HCS_DOMAIN;
    return domain;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    return noPixels ? new String[0] : files;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (getSeriesCount() == 1) return super.openBytes(no, buf, x, y, w, h);

    int[] lengths = new int[] {getSizeZ(), getEffectiveSizeC(), fieldCount,
      wellColumnCount, wellRowCount, getSizeT()};

    int[] zct = getZCTCoords(no);
    Well well = getWell(getSeries());
    int[] position = new int[] {zct[0], zct[1], well.field, well.wellCol,
      well.wellRow, zct[2]};

    int fileIndex = FormatTools.positionToRaster(lengths, position);
    RandomAccessInputStream s = new RandomAccessInputStream(files[fileIndex]);
    TiffParser parser = new TiffParser(s);
    IFD ifd = parser.getFirstIFD();
    parser.getSamples(ifd, buf, x, y, w, h);
    s.close();

    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    Vector<String> uniqueChannels = new Vector<String>();
    Vector<Double> uniqueZs = new Vector<Double>();

    String filename = id.substring(id.lastIndexOf(File.separator) + 1);
    filename = filename.substring(0, filename.indexOf("."));
    boolean integerFilename = true;
    try {
      Integer.parseInt(filename);
    }
    catch (NumberFormatException e) {
      integerFilename = false;
    }

    if (integerFilename && ifds.size() == 1 &&
      ifds.get(0).getIFDIntValue(IFD.NEW_SUBFILE_TYPE) == 2)
    {
      // look for other files in the dataset

      findTIFFs();

      String stageLabel = null;

      for (String tiff : files) {
        MetamorphHandler handler = new MetamorphHandler();
        parseFile(tiff, handler);

        String label = handler.getStageLabel();
        if (stageLabel == null) {
          stageLabel = label;
        }
        else if (!label.equals(stageLabel)) {
          break;
        }

        if (!uniqueChannels.contains(handler.getChannelName())) {
          uniqueChannels.add(handler.getChannelName());
        }

        Vector<Double> zPositions = handler.getZPositions();
        Double pos = Math.rint(zPositions.get(0));

        if (!uniqueZs.contains(pos)) {
          uniqueZs.add(pos);
        }
      }

      MetamorphHandler handler = new MetamorphHandler();
      parseFile(files[files.length - 1], handler);

      String lastStageLabel = handler.getStageLabel();
      int lastField = getField(lastStageLabel);
      int lastWellRow = getWellRow(lastStageLabel);
      int lastWellColumn = getWellColumn(lastStageLabel);

      int field = getField(stageLabel);
      int wellRow = getWellRow(stageLabel);
      int wellColumn = getWellColumn(stageLabel);

      fieldCount = lastField - field + 1;
      wellRowCount = lastWellRow - wellRow + 1;
      wellColumnCount = lastWellColumn - wellColumn + 1;
      core[0].sizeC = uniqueChannels.size();
      core[0].sizeZ = uniqueZs.size();
    }
    else {
      files = new String[] {id};
      fieldCount = 1;
      wellRowCount = 1;
      wellColumnCount = 1;
      core[0].sizeC = 0;
    }

    // parse XML comment

    MetamorphHandler handler = new MetamorphHandler(getGlobalMetadata());
    for (IFD ifd : ifds) {
      XMLTools.parseXML(ifd.getComment(), handler);
    }

    Vector<Integer> wavelengths = handler.getWavelengths();
    Vector<Double> zPositions = handler.getZPositions();

    // calculate axis sizes

    Vector<Integer> uniqueC = new Vector<Integer>();
    for (Integer c : wavelengths) {
      if (!uniqueC.contains(c)) {
        uniqueC.add(c);
      }
    }
    int effectiveC = uniqueC.size();
    if (effectiveC == 0) effectiveC = 1;
    if (getSizeC() == 0) core[0].sizeC = 1;
    int samples = ifds.get(0).getSamplesPerPixel();
    core[0].sizeC *= effectiveC * samples;

    Vector<Double> uniqueZ = new Vector<Double>();
    for (Double z : zPositions) {
      if (!uniqueZ.contains(z)) uniqueZ.add(z);
    }
    if (getSizeZ() == 0) core[0].sizeZ = 1;
    core[0].sizeZ *= uniqueZ.size();

    int totalPlanes = files.length * ifds.size();
    effectiveC = getSizeC() / samples;
    core[0].sizeT = totalPlanes /
      (fieldCount * wellRowCount * wellColumnCount * getSizeZ() * effectiveC);
    if (getSizeT() == 0) core[0].sizeT = 1;
    core[0].imageCount = getSizeZ() * getSizeT() * effectiveC;

    int seriesCount = fieldCount * wellRowCount * wellColumnCount;
    if (seriesCount > 1) {
      CoreMetadata oldCore = core[0];
      core = new CoreMetadata[seriesCount];
      for (int i=0; i<seriesCount; i++) {
        core[i] = oldCore;
      }
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    store.setPlateID(MetadataTools.createLSID("Plate", 0), 0);
    store.setPlateRowNamingConvention(NamingConvention.LETTER, 0);
    store.setPlateColumnNamingConvention(NamingConvention.NUMBER, 0);

    for (int row=0; row<wellRowCount; row++) {
      for (int col=0; col<wellColumnCount; col++) {
        int wellIndex = row * wellColumnCount + col;

        store.setWellID(
          MetadataTools.createLSID("Well", 0, wellIndex), 0, wellIndex);
        store.setWellRow(new NonNegativeInteger(row), 0, wellIndex);
        store.setWellColumn(new NonNegativeInteger(col), 0, wellIndex);

        for (int field=0; field<fieldCount; field++) {
          String wellSampleID =
            MetadataTools.createLSID("WellSample", 0, wellIndex, field);
          store.setWellSampleID(wellSampleID, 0, wellIndex, field);

          int seriesIndex = getSeriesIndex(row, col, field);
          String imageID = MetadataTools.createLSID("Image", seriesIndex);
          store.setImageID(imageID, seriesIndex);
          store.setWellSampleImageRef(imageID, 0, wellIndex, field);
          store.setWellSampleIndex(
            new NonNegativeInteger(seriesIndex), 0, wellIndex, field);
        }
      }
    }

    for (int s=0; s<seriesCount; s++) {
      setSeries(s);
      Well well = getWell(s);

      String name = handler.getImageName();
      if (seriesCount > 1) {
        name = "Field #" + (well.field + 1) + ", Well " +
          (char) (well.wellRow + 'A') + (well.wellCol + 1) + ": " + name;
      }

      store.setImageName(name, s);

      String date =
        DateTools.formatDate(handler.getDate(), DateTools.ISO8601_FORMAT);
      store.setImageAcquiredDate(date, s);

      if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
        Vector<String> timestamps = handler.getTimestamps();
        Vector<Double> exposures = handler.getExposures();

        for (int i=0; i<timestamps.size(); i++) {
          long timestamp = DateTools.getTime(timestamps.get(i), DATE_FORMAT);
          addSeriesMeta("timestamp " + i, timestamp);
        }
        for (int i=0; i<exposures.size(); i++) {
          addSeriesMeta("exposure time " + i + " (ms)",
            exposures.get(i).floatValue() * 1000);
        }

        long startDate = 0;
        if (timestamps.size() > 0) {
          startDate = DateTools.getTime(timestamps.get(0), DATE_FORMAT);
        }

        store.setImageDescription("", s);

        for (int i=0; i<getImageCount(); i++) {
          int[] coords = getZCTCoords(i);
          if (coords[2] < timestamps.size()) {
            String stamp = timestamps.get(coords[2]);
            long ms = DateTools.getTime(stamp, DATE_FORMAT);
            store.setPlaneDeltaT((ms - startDate) / 1000.0, s, i);
          }
          if (i < exposures.size()) {
            store.setPlaneExposureTime(exposures.get(i), s, i);
          }
        }

        store.setImagingEnvironmentTemperature(handler.getTemperature(), s);
        store.setPixelsPhysicalSizeX(handler.getPixelSizeX(), s);
        store.setPixelsPhysicalSizeY(handler.getPixelSizeY(), s);

        for (int c=0; c<getEffectiveSizeC(); c++) {
          if (uniqueChannels.size() > c) {
            store.setChannelName(uniqueChannels.get(c), s, c);
          }
          else store.setChannelName(handler.getChannelName(), s, c);
        }
      }
    }
  }

  // -- Helper methods --

  private int getSeriesIndex(int wellRow, int wellColumn, int field) {
    return FormatTools.positionToRaster(
      new int[] {fieldCount, wellColumnCount, wellRowCount},
      new int[] {field, wellColumn, wellRow});
  }

  private Well getWell(int seriesIndex) {
    int[] coordinates = FormatTools.rasterToPosition(
      new int[] {fieldCount, wellColumnCount, wellRowCount}, seriesIndex);
    return new Well(coordinates[2], coordinates[1], coordinates[0]);
  }

  private int getField(String stageLabel) {
    if (stageLabel.indexOf("Scan") < 0) return 0;
    String index = stageLabel.substring(0, stageLabel.indexOf(":")).trim();
    return Integer.parseInt(index) - 1;
  }

  private int getWellRow(String stageLabel) {
    int scanIndex = stageLabel.indexOf("Scan");
    if (scanIndex < 0) return 0;
    scanIndex = stageLabel.indexOf(" ", scanIndex) + 1;
    return (int) (stageLabel.charAt(scanIndex) - 'A');
  }

  private int getWellColumn(String stageLabel) {
    int scanIndex = stageLabel.indexOf("Scan");
    if (scanIndex < 0) return 0;
    scanIndex = stageLabel.indexOf(" ", scanIndex) + 2;
    return Integer.parseInt(stageLabel.substring(scanIndex)) - 1;
  }

  private void findTIFFs() throws IOException {
    Location baseFile = new Location(currentId).getAbsoluteFile();
    Location parent = baseFile.getParentFile();
    FilePattern pattern = new FilePattern(baseFile);
    String[] tiffs = pattern.getFiles();
    NumericComparator comparator = new NumericComparator();
    Arrays.sort(tiffs, comparator);

    Vector<String> validTIFFs = new Vector<String>();

    for (String tiff : tiffs) {
      if (!new Location(tiff).exists()) {
        String base = tiff.substring(tiff.lastIndexOf(File.separator) + 1);
        base = base.substring(0, base.indexOf("."));
        String suffix = tiff.substring(tiff.lastIndexOf("."));
        while (base.length() < 3) {
          base = "0" + base;
        }
        Location test = new Location(parent, base + suffix);
        if (test.exists()) {
          tiff = test.getAbsolutePath();
        }
        else continue;
      }
      validTIFFs.add(tiff);
    }

    files = validTIFFs.toArray(new String[validTIFFs.size()]);
  }

  private void parseFile(String tiff, MetamorphHandler handler)
    throws IOException
  {
    RandomAccessInputStream s = new RandomAccessInputStream(tiff);
    TiffParser parser = new TiffParser(s);
    IFD firstIFD = parser.getFirstIFD();
    XMLTools.parseXML(firstIFD.getComment(), handler);
    s.close();
  }

  // -- Helper classes --

  class Well {
    public int field;
    public int wellRow;
    public int wellCol;

    public Well(int wellRow, int wellCol, int field) {
      this.wellRow = wellRow;
      this.wellCol = wellCol;
      this.field = field;
    }

    public boolean equals(Object o) {
      if (!(o instanceof Well)) return false;
      Well w = (Well) o;

      return w.field == this.field && w.wellRow == this.wellRow &&
        w.wellCol == this.wellCol;
    }

    public int hashCode() {
      return (field << 16) | (wellRow << 8) | wellCol;
    }
  }

  class NumericComparator implements Comparator<String> {
    public int compare(String s1, String s2) {
      if (s1.equals(s2)) return 0;

      String base1 = s1.substring(s1.lastIndexOf(File.separator) + 1);
      String base2 = s2.substring(s2.lastIndexOf(File.separator) + 1);

      base1 = base1.substring(0, base1.indexOf("."));
      base2 = base2.substring(0, base2.indexOf("."));

      try {
        int num1 = Integer.parseInt(base1);
        int num2 = Integer.parseInt(base2);

        if (num1 == num2) return 0;
        return num1 < num2 ? -1 : 1;
      }
      catch (NumberFormatException e) { }
      return 0;
    }
  }

}
