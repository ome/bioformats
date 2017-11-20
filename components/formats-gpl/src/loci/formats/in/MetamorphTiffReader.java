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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import ome.units.quantity.Length;
import ome.units.quantity.Temperature;
import ome.units.quantity.Time;
import ome.units.UNITS;

import org.apache.commons.lang.ArrayUtils;

/**
 * MetamorphTiffReader is the file format reader for TIFF files produced by
 * Metamorph software version 7.5 and above.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Thomas Caswell tcaswell at uchicago.edu
 */
public class MetamorphTiffReader extends BaseTiffReader {

  // -- Constants --

  private static final String DATE_FORMAT = "yyyyMMdd HH:mm:ss";

  // -- Fields --

  private String[] files;
  private int wellCount = 0;
  private int fieldRowCount = 0;
  private int fieldColumnCount = 0;
  private boolean dualCamera = false;

  // -- Constructor --

  /** Constructs a new Metamorph TIFF reader. */
  public MetamorphTiffReader() {
    super("Metamorph TIFF", new String[] {"tif", "tiff"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.LM_DOMAIN, FormatTools.HCS_DOMAIN};
    datasetDescription = "One or more .tif/.tiff files";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser tp = new TiffParser(stream);
    String comment = tp.getComment();
    if (comment == null) return false;
    comment = comment.trim();
    return comment.startsWith("<MetaData>") && comment.endsWith("</MetaData>");
  }

  /* @see loci.formats.IFormatReader#getDomains() */
  @Override
  public String[] getDomains() {
    FormatTools.assertId(currentId, true, 1);
    String[] domain = new String[1];
    domain[0] =
      files.length == 1 ? FormatTools.LM_DOMAIN : FormatTools.HCS_DOMAIN;
    return domain;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    return noPixels ? ArrayUtils.EMPTY_STRING_ARRAY : files;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      files = null;
      wellCount = 0;
      fieldRowCount = 0;
      fieldColumnCount = 0;
      dualCamera = false;
    }
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (getSeriesCount() == 1 && files.length == 1 && !dualCamera) {
      return super.openBytes(no, buf, x, y, w, h);
    }

    int[] lengths = new int[] {getSizeZ(), getEffectiveSizeC(),
      fieldColumnCount, fieldRowCount, wellCount, getSizeT()};

    int[] zct = getZCTCoords(no);
    Well well = getWell(getSeries());
    int[] position = new int[] {zct[0], zct[1], well.fieldCol,
      well.fieldRow, well.well, zct[2]};

    if (dualCamera) {
      position[1] = 0;
    }

    int fileIndex = FormatTools.positionToRaster(lengths, position);
    final RandomAccessInputStream s;
    if (fileIndex < files.length) {
      s = new RandomAccessInputStream(files[fileIndex]);
    }
    else {
      s = new RandomAccessInputStream(files[0]);
    }
    TiffParser parser = new TiffParser(s);
    int ndx = getSeries() * getSizeZ() * getSizeT() + no;
    int[] pos = getZCTCoords(no);
    if (dualCamera) {
      int channel = pos[1];
      pos[1] = 0;
      ndx = getSeries() * getSizeZ() * getSizeT() +
        FormatTools.positionToRaster(new int[] {getSizeZ(), 1, getSizeT()}, pos);
      pos[1] = channel;
    }
    IFD ifd = files.length == 1 ? ifds.get(ndx) : parser.getFirstIFD();
    int realX = dualCamera ? (pos[1] == 0 ? x : x + pos[1] * getSizeX()) : x;
    parser.getSamples(ifd, buf, realX, y, w, h);
    s.close();

    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    List<String> uniqueChannels = new ArrayList<String>();
    final List<Double> uniqueZs = new ArrayList<Double>();
    final List<Length> stageX = new ArrayList<Length>();
    final List<Length> stageY = new ArrayList<Length>();

    CoreMetadata m = core.get(0);

    String filename = id.substring(id.lastIndexOf(File.separator) + 1);
    filename = filename.substring(0, filename.indexOf('.'));
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

        final List<Double> zPositions = handler.getZPositions();
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
      int fieldRow = getWellRow(stageLabel);
      int fieldColumn = getWellColumn(stageLabel);

      wellCount = lastField - field + 1;
      fieldRowCount = lastWellRow - fieldRow + 1;
      fieldColumnCount = lastWellColumn - fieldColumn + 1;
      m.sizeC = uniqueChannels.size();
      m.sizeZ = uniqueZs.size();
    }
    else {
      files = new String[] {id};
      wellCount = 1;
      fieldRowCount = 1;
      fieldColumnCount = 1;
      m.sizeC = 0;
    }

    // parse XML comment

    MetamorphHandler handler = new MetamorphHandler(getGlobalMetadata());
    final List<Length> xPositions = new ArrayList<Length>();
    final List<Length> yPositions = new ArrayList<Length>();

    for (IFD ifd : ifds) {
      String xml = XMLTools.sanitizeXML(ifd.getComment());
      XMLTools.parseXML(xml, handler);

      final Length x = handler.getStagePositionX();
      final Length y = handler.getStagePositionY();

      if (xPositions.isEmpty()) {
        xPositions.add(x);
        yPositions.add(y);
      }
      else {
        final Length previousX = xPositions.get(xPositions.size() - 1);
        final Length previousY = yPositions.get(yPositions.size() - 1);

        final double x1 = x.value(UNITS.REFERENCEFRAME).doubleValue();
        final double x2 = previousX.value(UNITS.REFERENCEFRAME).doubleValue();
        final double y1 = y.value(UNITS.REFERENCEFRAME).doubleValue();
        final double y2 = previousY.value(UNITS.REFERENCEFRAME).doubleValue();

        if (Math.abs(x1 - x2) > 0.21 || Math.abs(y1 - y2) > 0.21) {
          xPositions.add(x);
          yPositions.add(y);
        }
      }
    }

    if (xPositions.size() > 1) {
      fieldRowCount = xPositions.size();
    }

    final List<Integer> wavelengths = handler.getWavelengths();
    final List<Double> zPositions = handler.getZPositions();
    dualCamera = handler.hasDualCamera();

    // calculate axis sizes

    final List<Integer> uniqueC = new ArrayList<Integer>();
    for (Integer c : wavelengths) {
      if (!uniqueC.contains(c)) {
        uniqueC.add(c);
      }
    }
    int effectiveC = uniqueC.size();
    if (effectiveC == 0) effectiveC = 1;
    if (getSizeC() == 0) m.sizeC = 1;
    int samples = ifds.get(0).getSamplesPerPixel();
    m.sizeC *= effectiveC * samples;

    final List<Double> uniqueZ = new ArrayList<Double>();
    for (Double z : zPositions) {
      if (!uniqueZ.contains(z)) uniqueZ.add(z);
    }

    if (getSizeZ() == 0) m.sizeZ = 1;
    if (uniqueZ.size() > 1) m.sizeZ *= uniqueZ.size();

    Double physicalSizeZ = null;
    if (m.sizeZ > 1) {
      Double zRange = zPositions.get(zPositions.size() - 1) - zPositions.get(0);
      physicalSizeZ = Math.abs(zRange);
      physicalSizeZ /= (m.sizeZ - 1);
    }

    int totalPlanes = files.length * ifds.size();
    effectiveC = getSizeC() / samples;

    // if the channel name and Z position are unique
    // for each plane, then prefer unique channels over unique Zs
    // the division by uniqueC.size is not a typo - it's meant to
    // account for the multiple actual Z sections
    if (effectiveC * getSizeZ() > totalPlanes &&
      (effectiveC * (getSizeZ() / uniqueC.size()) == totalPlanes ||
      effectiveC == totalPlanes))
    {
      if (getSizeZ() >= uniqueC.size()) {
        m.sizeZ /= uniqueC.size();
      }
      else {
        m.sizeZ = 1;
      }
    }

    m.sizeT = totalPlanes /
      (wellCount * fieldRowCount * fieldColumnCount * getSizeZ() * effectiveC);
    if (getSizeT() == 0) m.sizeT = 1;

    int seriesCount = wellCount * fieldRowCount * fieldColumnCount;

    if (seriesCount > 1 && getSizeZ() > totalPlanes / seriesCount ||
      totalPlanes > getSizeZ() * getSizeT() * effectiveC)
    {
      m.sizeZ = 1;
      m.sizeT = totalPlanes / (seriesCount * effectiveC);
    }

    m.imageCount = getSizeZ() * getSizeT() * effectiveC;

    if (dualCamera) {
      m.sizeX /= 2;
      m.sizeC *= 2;
      m.imageCount *= 2;
    }

    if (seriesCount > 1) {
      core.clear();
      for (int i=0; i<seriesCount; i++) {
        core.add(m);
      }
    }

    for (int s=0; s<wellCount * fieldRowCount * fieldColumnCount; s++) {
      if (files.length > 1) {
        int[] lengths = new int[] {getSizeZ(), getEffectiveSizeC(),
          fieldColumnCount, fieldRowCount, wellCount, getSizeT()};

        Well well = getWell(s);
        int[] position =
          new int[] {0, 0, well.fieldCol, well.fieldRow, well.well, 0};

        int fileIndex = FormatTools.positionToRaster(lengths, position);
        parseFile(files[fileIndex], handler);

        stageX.add(handler.getStagePositionX());
        stageY.add(handler.getStagePositionY());
      }
      else {
        stageX.add(xPositions.get(s));
        stageY.add(yPositions.get(s));
      }
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    // Only populate Plate/Well metadata if there are multiple wells.
    // The presence of just one well suggests that we don't actually have
    // an HCS dataset (and even if we did, the Plate/Well metadata would be
    // effectively useless).
    if (wellCount > 1) {
      store.setPlateID(MetadataTools.createLSID("Plate", 0), 0);
      store.setPlateRows(new PositiveInteger(1), 0);
      store.setPlateColumns(new PositiveInteger(wellCount), 0);
      store.setPlateRowNamingConvention(NamingConvention.LETTER, 0);
      store.setPlateColumnNamingConvention(NamingConvention.NUMBER, 0);

      store.setPlateAcquisitionID(
        MetadataTools.createLSID("PlateAcquisition", 0, 0), 0, 0);

      for (int well=0; well<wellCount; well++) {
        store.setWellID(MetadataTools.createLSID("Well", 0, well), 0, well);
        store.setWellRow(new NonNegativeInteger(0), 0, well);
        store.setWellColumn(new NonNegativeInteger(well), 0, well);

        for (int row=0; row<fieldRowCount; row++) {
          for (int col=0; col<fieldColumnCount; col++) {
            int field = row * fieldColumnCount + col;
            String wellSampleID =
              MetadataTools.createLSID("WellSample", 0, well, field);
            store.setWellSampleID(wellSampleID, 0, well, field);

            int seriesIndex = getSeriesIndex(row, col, well);
            String imageID = MetadataTools.createLSID("Image", seriesIndex);
            store.setImageID(imageID, seriesIndex);
            store.setWellSampleImageRef(imageID, 0, well, field);
            store.setWellSampleIndex(
              new NonNegativeInteger(seriesIndex), 0, well, field);
            store.setPlateAcquisitionWellSampleRef(wellSampleID, 0, 0, seriesIndex);
          }
        }
      }
    }

    final List<String> timestamps = handler.getTimestamps();
    final List<Double> exposures = handler.getExposures();
    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      for (int i=0; i<timestamps.size(); i++) {
        long timestamp = DateTools.getTime(timestamps.get(i), DATE_FORMAT, ".");
        addGlobalMetaList("timestamp", timestamp);
      }
      for (int i=0; i<exposures.size(); i++) {
        addGlobalMetaList("exposure time (ms)",
          exposures.get(i).floatValue() * 1000);
      }
    }

    for (int s=0; s<seriesCount; s++) {
      setSeries(s);
      Well well = getWell(s);

      String name = handler.getImageName();
      if (seriesCount > 1) {
        name = "Field " + (char) (well.fieldRow + 'A') + (well.fieldCol + 1) +
          ", Well " + (well.well + 1) + ": " + name;
      }

      store.setImageName(name, s);

      String date =
        DateTools.formatDate(handler.getDate(), DateTools.ISO8601_FORMAT, ".");
      if (date != null) {
        store.setImageAcquisitionDate(new Timestamp(date), s);
      }

      if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
        long startDate = 0;
        if (timestamps.size() > 0) {
          startDate = DateTools.getTime(timestamps.get(0), DATE_FORMAT, ".");
        }

        store.setImageDescription("", s);

        int image = 0;
        for (int c=0; c<getEffectiveSizeC(); c++) {
          for (int t=0; t<getSizeT(); t++) {
            store.setPlaneTheZ(new NonNegativeInteger(0), s, image);
            store.setPlaneTheC(new NonNegativeInteger(c), s, image);
            store.setPlaneTheT(new NonNegativeInteger(t), s, image);

            if (t < timestamps.size()) {
              String stamp = timestamps.get(t);
              long ms = DateTools.getTime(stamp, DATE_FORMAT, ".");
              store.setPlaneDeltaT(new Time((ms - startDate) / 1000.0, UNITS.SECOND), s, image);
            }
            int exposureIndex = image;
            if (dualCamera) {
              exposureIndex /= getEffectiveSizeC();
            }
            if (exposures.size() == 1) {
              exposureIndex = 0;
            }
            if (exposureIndex < exposures.size() && exposures.get(exposureIndex) != null) {
              store.setPlaneExposureTime(new Time(exposures.get(exposureIndex), UNITS.SECOND), s, image);
            }
            if (s < stageX.size()) {
              store.setPlanePositionX(stageX.get(s), s, image);
            }
            if (s < stageY.size()) {
              store.setPlanePositionY(stageY.get(s), s, image);
            }
            image++;
          }
        }

        store.setImagingEnvironmentTemperature(
                new Temperature(handler.getTemperature(), UNITS.CELSIUS), s);

        Length sizeX =
          FormatTools.getPhysicalSizeX(handler.getPixelSizeX());
        Length sizeY =
          FormatTools.getPhysicalSizeY(handler.getPixelSizeY());
        Length sizeZ = physicalSizeZ == null ? null : 
          FormatTools.getPhysicalSizeZ(physicalSizeZ);

        if (sizeX != null) {
          store.setPixelsPhysicalSizeX(sizeX, s);
        }
        if (sizeY != null) {
          store.setPixelsPhysicalSizeY(sizeY, s);
        }
        if (sizeZ != null) {
          store.setPixelsPhysicalSizeZ(sizeZ, s);
        }

        if (uniqueChannels.size() == 0) {
          uniqueChannels = handler.getChannelNames();
        }

        for (int c=0; c<getEffectiveSizeC(); c++) {
          if (uniqueChannels.size() > c) {
            store.setChannelName(uniqueChannels.get(c), s, c);
          }
          else store.setChannelName(handler.getChannelName(), s, c);
          if (c < wavelengths.size()) {
            store.setChannelEmissionWavelength(
              FormatTools.getEmissionWavelength(Double.valueOf(wavelengths.get(c))), s, c);
          }
        }
      }
    }
  }

  // -- Helper methods --

  private int getSeriesIndex(int fieldRow, int fieldColumn, int well) {
    return FormatTools.positionToRaster(
      new int[] {fieldColumnCount, fieldRowCount, wellCount},
      new int[] {fieldColumn, fieldRow, well});
  }

  private Well getWell(int seriesIndex) {
    int[] coordinates = FormatTools.rasterToPosition(
      new int[] {fieldColumnCount, fieldRowCount, wellCount}, seriesIndex);
    return new Well(coordinates[1], coordinates[0], coordinates[2]);
  }

  private int getField(String stageLabel) {
    if (stageLabel.indexOf("Scan") < 0) return 0;
    String index = stageLabel.substring(0, stageLabel.indexOf(':')).trim();
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

    final List<String> validTIFFs = new ArrayList<String>();

    for (String tiff : tiffs) {
      if (!new Location(tiff).exists()) {
        String base = tiff.substring(tiff.lastIndexOf(File.separator) + 1);
        base = base.substring(0, base.indexOf('.'));
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
      if (File.separator.equals("\\")) {
        tiff = tiff.replaceAll("\\\\\\\\", "\\\\");
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
    String xml = XMLTools.sanitizeXML(firstIFD.getComment());
    XMLTools.parseXML(xml, handler);
    s.close();
  }

  // -- Helper classes --

  class Well {
    public int well;
    public int fieldRow;
    public int fieldCol;

    public Well(int fieldRow, int fieldCol, int well) {
      this.fieldRow = fieldRow;
      this.fieldCol = fieldCol;
      this.well = well;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof Well)) return false;
      Well w = (Well) o;

      return w.well == this.well && w.fieldRow == this.fieldRow &&
        w.fieldCol == this.fieldCol;
    }

    @Override
    public int hashCode() {
      return (well << 16) | (fieldRow << 8) | fieldCol;
    }
  }

  class NumericComparator implements Comparator<String> {
    @Override
    public int compare(String s1, String s2) {
      if (s1.equals(s2)) return 0;

      String base1 = s1.substring(s1.lastIndexOf(File.separator) + 1);
      String base2 = s2.substring(s2.lastIndexOf(File.separator) + 1);

      base1 = base1.substring(0, base1.indexOf('.'));
      base2 = base2.substring(0, base2.indexOf('.'));

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
