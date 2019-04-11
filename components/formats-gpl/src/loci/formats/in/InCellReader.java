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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.xml.BaseHandler;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.IMinMaxStore;
import loci.formats.meta.MetadataStore;

import ome.xml.model.enums.Binning;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import ome.units.quantity.Length;
import ome.units.quantity.Temperature;
import ome.units.quantity.Time;
import ome.units.UNITS;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * InCellReader is the file format reader for InCell 1000/2000 datasets.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class InCellReader extends FormatReader {

  // -- Constants --

  public static final String INCELL_MAGIC_STRING = "IN Cell Analyzer";
  public static final String CYTELL_MAGIC_STRING = "Cytell";

  private static final String[] PIXELS_SUFFIXES =
    new String[] {"tif", "tiff", "im"};
  private static final String[] METADATA_SUFFIXES =
    new String[] {"xml", "xlog"};

  // -- Fields --

  private boolean[][] plateMap;

  private Image[][][][] imageFiles;
  private MinimalTiffReader tiffReader;
  private List<Double> emWaves, exWaves;
  private List<String> channelNames;
  private int totalImages;
  private int imageWidth, imageHeight;
  private String creationDate;
  private String rowName = "A", colName = "1";
  private int fieldCount;

  private int wellRows, wellCols;
  private Map<Integer, int[]> wellCoordinates;
  private Map<Integer, Length> posX, posY;
  private int offsetPointCounter;

  private boolean[][] exclude;

  private List<Integer> channelsPerTimepoint;
  private boolean oneTimepointPerSeries;
  private int totalChannels;

  private List<String> metadataFiles;

  private Binning bin;
  private Length x, y;
  private Double gain;
  private Double temperature;
  private Double refractive;

  private List<String> exFilters = new ArrayList<String>();
  private List<String> emFilters = new ArrayList<String>();

  // -- Constructor --

  /** Constructs a new InCell 1000/2000 reader. */
  public InCellReader() {
    super("InCell 1000/2000",
      new String[] {"xdce", "xml", "tiff", "tif", "xlog"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.HCS_DOMAIN};
    hasCompanionFiles = true;
    datasetDescription = "One .xdce file with at least one .tif/.tiff or " +
      ".im file";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, "xdce") || checkSuffix(name, "xml")) {
      return super.isThisType(name, open);
    }
    return false;
  }

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 2048;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    String check = stream.readString(blockLen);
    return check.indexOf(INCELL_MAGIC_STRING) >= 0 ||
      check.indexOf(CYTELL_MAGIC_STRING) >= 0;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return tiffReader == null ? null : tiffReader.get8BitLookupTable();
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return tiffReader == null ? null : tiffReader.get16BitLookupTable();
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int[] coordinates = getZCTCoords(no);

    int well = getWellFromSeries(getSeries());
    int field = getFieldFromSeries(getSeries());
    int timepoint = oneTimepointPerSeries ?
      getSeries() % channelsPerTimepoint.size() : coordinates[2];
    int image = getIndex(coordinates[0], coordinates[1], 0);

    if (imageFiles[well][field][timepoint][image] == null) return buf;
    String filename = imageFiles[well][field][timepoint][image].filename;
    if (filename == null || !(new Location(filename).exists())) return buf;

    if (imageFiles[well][field][timepoint][image].isTiff) {
      try {
        tiffReader.setId(filename);
        return tiffReader.openBytes(0, buf, x, y, w, h);
      }
      catch (FormatException e) {
        LOGGER.debug("", e);
      }
      catch (IOException e) {
        LOGGER.debug("", e);
      }
      return buf;
    }

    // pixels are stored in .im files
    RandomAccessInputStream s = new RandomAccessInputStream(filename);
    if (s.length() > FormatTools.getPlaneSize(this)) {
      s.seek(128);
      readPlane(s, x, y, w, h, buf);
    }
    s.close();
    return buf;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    final List<String> files = new ArrayList<String>();
    files.add(currentId);
    files.addAll(metadataFiles);
    if (!noPixels && imageFiles != null) {
      int well = getWellFromSeries(getSeries());
      int field = getFieldFromSeries(getSeries());
      for (Image[] timepoints : imageFiles[well][field]) {
        for (Image plane : timepoints) {
          if (plane != null && plane.filename != null) {
            if (new Location(plane.filename).exists()) {
              files.add(plane.filename);
            }
            if (plane.thumbnailFile != null && new Location(plane.thumbnailFile).exists()) {
              files.add(plane.thumbnailFile);
            }
          }
        }
      }
    }
    return files.toArray(new String[files.size()]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (tiffReader != null) tiffReader.close(fileOnly);

    if (!fileOnly) {
      imageFiles = null;
      tiffReader = null;
      totalImages = 0;
      emWaves = exWaves = null;
      channelNames = null;
      wellCoordinates = null;
      posX = null;
      posY = null;
      offsetPointCounter = 0;
      creationDate = null;
      wellRows = wellCols = 0;
      fieldCount = 0;
      exclude = null;
      metadataFiles = null;
      imageWidth = imageHeight = 0;
      rowName = "A";
      colName = "1";
      channelsPerTimepoint = null;
      oneTimepointPerSeries = false;
      totalChannels = 0;
      plateMap = null;
      bin = null;
      x = null;
      y = null;
      gain = null;
      temperature = null;
      refractive = null;
      emFilters.clear();
      exFilters.clear();
    }
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    for (Image[][][] well : imageFiles) {
      for (Image[][] field : well) {
        for (Image[] timepoint : field) {
          for (Image img : timepoint) {
            if (img != null) {
              if (img.isTiff && tiffReader != null) {
                return tiffReader.getOptimalTileWidth();
              }
              break;
            }
          }
        }
      }
    }
    return super.getOptimalTileWidth();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    for (Image[][][] well : imageFiles) {
      for (Image[][] field : well) {
        for (Image[] timepoint : field) {
          for (Image img : timepoint) {
            if (img != null) {
              if (img.isTiff && tiffReader != null) {
                return tiffReader.getOptimalTileHeight();
              }
              break;
            }
          }
        }
      }
    }
    return super.getOptimalTileHeight();
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    // make sure that we have the .xdce (or .xml) file
    if (checkSuffix(id, PIXELS_SUFFIXES) || checkSuffix(id, "xlog")) {
      Location currentFile = new Location(id).getAbsoluteFile();
      Location parent = currentFile.getParentFile();
      String[] list = parent.list(true);
      for (String f : list) {
        if (checkSuffix(f, new String[] {"xdce", "xml"})) {
          String path = new Location(parent, f).getAbsolutePath();
          if (isThisType(path)) {
            // make sure that the .xdce file references the current file
            // this ensures that the correct file is chosen if multiple
            // .xdce files are the same directory
            String data = DataTools.readFile(path);
            if (data.indexOf(currentFile.getName()) >= 0) {
              id = path;
              break;
            }
          }
        }
      }
    }

    super.initFile(id);
    in = new RandomAccessInputStream(id);

    channelNames = new ArrayList<String>();
    emWaves = new ArrayList<Double>();
    exWaves = new ArrayList<Double>();
    channelsPerTimepoint = new ArrayList<Integer>();
    metadataFiles = new ArrayList<String>();

    // parse metadata from the .xdce or .xml file

    wellCoordinates = new HashMap<Integer, int[]>();
    posX = new HashMap<Integer, Length>();
    posY = new HashMap<Integer, Length>();
    offsetPointCounter = 0;

    byte[] b = new byte[(int) in.length()];
    in.read(b);

    CoreMetadata ms0 = core.get(0);

    ms0.dimensionOrder = "XYZCT";

    MetadataStore store = makeFilterMetadata();
    DefaultHandler handler = new MinimalInCellHandler();
    XMLTools.parseXML(b, handler);

    if (getSizeZ() == 0) ms0.sizeZ = 1;
    if (getSizeC() == 0) ms0.sizeC = 1;
    if (getSizeT() == 0) ms0.sizeT = 1;

    if (totalImages == 0) {
      ms0.imageCount = getSizeC() * getSizeZ() * getSizeT();
      totalImages = getImageCount() * wellRows * wellCols * fieldCount;
      Location parent = new Location(currentId).getAbsoluteFile().getParentFile();

      for (int row=0; row<wellRows; row++) {
        for (int col=0; col<wellCols; col++) {
          plateMap[row][col] = true;
          for (int field=0; field<fieldCount; field++) {
            for (int t=0; t<getSizeT(); t++) {
              for (int image=0; image<getSizeC() * getSizeZ(); image++) {
                // this could be expanded to allow for timepoint indexes
                // in the file name, as well as allowing the filter names to
                // be omitted
                int channel = getZCTCoords(image)[1];
                Image plane = new Image();
                String filename = FormatTools.getWellRowName(row) + " - " +
                  (col + 1) + "(fld " + (field + 1) +
                  " wv " + exFilters.get(channel) + " - " + emFilters.get(channel) + ").tif";
                Location path = new Location(parent, filename);
                if (path.exists()) {
                  plane.filename = path.getAbsolutePath();
                }
                else {
                  LOGGER.debug("Missing file {}", filename);
                }

                imageFiles[row * wellCols + col][field][t][image] = plane;
              }
            }
          }
        }
      }
    }

    for (int t=imageFiles[0][0].length-1; t>=0; t--) {
      boolean allNull = true;
      for (int well=0; well<imageFiles.length; well++) {
        for (int field=0; field<imageFiles[well].length; field++) {
          for (int p=0; p<imageFiles[well][field][t].length; p++) {
            if (imageFiles[well][field][t][p] != null) {
              allNull = false;
              break;
            }
          }
        }
      }
      if (allNull) {
        ms0.sizeT--;
      }
    }

    int seriesCount = 0;

    if (exclude != null) {
      for (int row=0; row<wellRows; row++) {
        for (int col=0; col<wellCols; col++) {
          if (!exclude[row][col]) {
            int well = row * wellCols + col;
            boolean hasNonNullImage = false;
            for (int field=0; field<imageFiles[well].length; field++) {
              for (int t=0; t<imageFiles[well][field].length; t++) {
                for (int p=0; p<imageFiles[well][field][t].length; p++) {
                  if (imageFiles[well][field][t][p] != null) {
                    hasNonNullImage = true;
                    break;
                  }
                }
              }
            }
            if (hasNonNullImage) {
              seriesCount += imageFiles[well].length;
            }
          }
        }
      }
      int expectedSeries = totalImages / (getSizeZ() * getSizeC() * getSizeT());
      if (expectedSeries > 0) {
        seriesCount = (int) Math.min(seriesCount, expectedSeries);
      }
    }
    else seriesCount = totalImages / (getSizeZ() * getSizeC() * getSizeT());

    totalChannels = getSizeC();

    oneTimepointPerSeries = false;
    for (int i=1; i<channelsPerTimepoint.size(); i++) {
      if (!channelsPerTimepoint.get(i).equals(channelsPerTimepoint.get(i - 1)))
      {
        oneTimepointPerSeries = true;
        break;
      }
    }
    if (oneTimepointPerSeries) {
      int imageCount = 0;
      for (Integer timepoint : channelsPerTimepoint) {
        imageCount += timepoint.intValue() * getSizeZ();
      }
      seriesCount = (totalImages / imageCount) * getSizeT();
    }

    int sizeT = getSizeT();
    int sizeC = getSizeC();
    int z = getSizeZ();
    int t = oneTimepointPerSeries ? 1 : getSizeT();

    core.clear();
    for (int i=0; i<seriesCount; i++) {
      int c = oneTimepointPerSeries ?
        channelsPerTimepoint.get(i % sizeT).intValue() : sizeC;

      CoreMetadata ms = new CoreMetadata();
      core.add(ms);
      ms.sizeZ = z;
      ms.sizeC = c;
      ms.sizeT = t;
      ms.imageCount = z * c * t;
      ms.dimensionOrder = "XYZCT";
    }

    int wellIndex = getWellFromSeries(0);
    int fieldIndex = getFieldFromSeries(0);

    String filename = imageFiles[wellIndex][fieldIndex][0][0].filename;
    boolean isTiff = imageFiles[wellIndex][fieldIndex][0][0].isTiff;

    if (isTiff && filename != null) {
      tiffReader = new MinimalTiffReader();
      tiffReader.setId(filename);
      for (int i=0; i<seriesCount; i++) {
        CoreMetadata ms = core.get(i);
        ms.sizeX = tiffReader.getSizeX();
        ms.sizeY = tiffReader.getSizeY();
        ms.interleaved = tiffReader.isInterleaved();
        ms.indexed = tiffReader.isIndexed();
        ms.rgb = tiffReader.isRGB();
        ms.pixelType = tiffReader.getPixelType();
        ms.littleEndian = tiffReader.isLittleEndian();
      }
    }
    else {
      for (int i=0; i<seriesCount; i++) {
        CoreMetadata ms = core.get(i);
        ms.sizeX = imageWidth;
        ms.sizeY = imageHeight;
        ms.interleaved = false;
        ms.indexed = false;
        ms.rgb = false;
        ms.pixelType = FormatTools.UINT16;
        ms.littleEndian = true;
      }
    }

    MetadataTools.populatePixels(store, this, true);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      handler = new InCellHandler(store);
      XMLTools.parseXML(b, handler);
    }

    String rowNaming =
      Character.isDigit(rowName.charAt(0)) ? "Number" : "Letter";
    String colNaming =
      Character.isDigit(colName.charAt(0)) ? "Number" : "Letter";

    String plateName = currentId;
    int begin = plateName.lastIndexOf(File.separator) + 1;
    int end = plateName.lastIndexOf(".");
    plateName = plateName.substring(begin, end);

    store.setPlateID(MetadataTools.createLSID("Plate", 0), 0);
    store.setPlateName(plateName, 0);
    store.setPlateRowNamingConvention(MetadataTools.getNamingConvention(rowNaming), 0);
    store.setPlateColumnNamingConvention(MetadataTools.getNamingConvention(colNaming), 0);
    store.setPlateRows(new PositiveInteger(wellRows), 0);
    store.setPlateColumns(new PositiveInteger(wellCols), 0);

    String plateAcqID = MetadataTools.createLSID("PlateAcquisition", 0, 0);
    store.setPlateAcquisitionID(plateAcqID, 0, 0);

    PositiveInteger maxFieldCount = FormatTools.getMaxFieldCount(fieldCount);
    if (maxFieldCount != null) {
      store.setPlateAcquisitionMaximumFieldCount(maxFieldCount, 0, 0);
    }

    // populate Image data

    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    String experimentID = MetadataTools.createLSID("Experiment", 0);
    store.setInstrumentID(instrumentID, 0);
    store.setExperimentID(experimentID, 0);

    String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
    store.setObjectiveID(objectiveID, 0, 0);

    String detectorID = MetadataTools.createLSID("Detector", 0, 0);
    store.setDetectorID(detectorID, 0, 0);

    int prevWell = -1;
    for (int i=0; i<seriesCount; i++) {
      store.setObjectiveSettingsID(objectiveID, i);

      if (refractive != null) {
        store.setObjectiveSettingsRefractiveIndex(refractive, i);
      }
      if (x != null) {
        store.setPixelsPhysicalSizeX(x, i);
      }
      if (y != null) {
        store.setPixelsPhysicalSizeY(y, i);
      }

      int well = getWellFromSeries(i);
      int field = getFieldFromSeries(i);
      int totalTimepoints =
        oneTimepointPerSeries ? channelsPerTimepoint.size() : 1;
      int timepoint = oneTimepointPerSeries ? (i % totalTimepoints) + 1 : -1;

      String imageID = MetadataTools.createLSID("Image", i);
      store.setImageID(imageID, i);
      store.setImageInstrumentRef(instrumentID, i);
      store.setImageExperimentRef(experimentID, i);

      int wellRow = well / wellCols;
      int wellCol = well % wellCols;
      wellIndex = i / (fieldCount * totalTimepoints);

      if (well != prevWell) {
        String wellID = MetadataTools.createLSID("Well", 0, wellIndex);
        store.setWellID(wellID, 0, wellIndex);
        store.setWellRow(new NonNegativeInteger(wellRow), 0, wellIndex);
        store.setWellColumn(new NonNegativeInteger(wellCol), 0, wellIndex);
        prevWell = well;
      }

      char rowChar = rowName.charAt(rowName.length() - 1);
      char colChar = colName.charAt(colName.length() - 1);
      String row = rowName.substring(0, rowName.length() - 1);
      String col = colName.substring(0, colName.length() - 1);

      if (Character.isDigit(rowChar)) {
        row += wellRow + Integer.parseInt(String.valueOf(rowChar));
      }
      else row += (char) (rowChar + wellRow);

      if (Character.isDigit(colChar)) {
        col += wellCol + Integer.parseInt(String.valueOf(colChar));
      }
      else col += (char) (colChar + wellCol);

      String imageName = "Well " + row + "-" + col + ", Field #" + (field + 1);
      if (timepoint >= 0) {
        imageName += ", Timepoint #" + timepoint;
      }

      store.setImageName(imageName, i);
      if (creationDate != null) {
        store.setImageAcquisitionDate(new Timestamp(creationDate), i);
      }

      timepoint--;
      if (timepoint < 0) timepoint = 0;
      int sampleIndex = field * totalTimepoints + timepoint;

      String wellSampleID =
        MetadataTools.createLSID("WellSample", 0, wellIndex, sampleIndex);
      store.setWellSampleID(wellSampleID, 0, wellIndex, sampleIndex);
      store.setWellSampleIndex(new NonNegativeInteger(i), 0, wellIndex, sampleIndex);
      store.setWellSampleImageRef(imageID, 0, wellIndex, sampleIndex);
      if (posX.containsKey(field)) {
        store.setWellSamplePositionX(posX.get(field), 0, wellIndex, sampleIndex);
      }
      if (posY.containsKey(field)) {
        store.setWellSamplePositionY(posY.get(field), 0, wellIndex, sampleIndex);
      }

      store.setPlateAcquisitionWellSampleRef(wellSampleID, 0, 0, i);
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      // populate PlaneTiming data

      for (int i=0; i<seriesCount; i++) {
        setSeries(i);

        int well = getWellFromSeries(i);
        int field = getFieldFromSeries(i);
        int timepoint = oneTimepointPerSeries ?
          i % channelsPerTimepoint.size() : 0;
        Double[][] minMax = new Double[getEffectiveSizeC()][2];
        for (int time=0; time<getSizeT(); time++) {
          if (!oneTimepointPerSeries) timepoint = time;
          int c = channelsPerTimepoint.get(timepoint).intValue();
          for (int q=0; q<getSizeZ()*c; q++) {
            Image img = imageFiles[well][field][timepoint][q];
            if (img == null) continue;
            int plane = time * getSizeZ() * c + q;
            if (img.deltaT != null) {
              store.setPlaneDeltaT(new Time(img.deltaT, UNITS.SECOND), i, plane);
            }
            if (img.exposure != null) {
              store.setPlaneExposureTime(new Time(img.exposure, UNITS.SECOND), i, plane);
            }

            store.setPlanePositionX(posX.get(field), i, plane);
            store.setPlanePositionY(posY.get(field), i, plane);
            store.setPlanePositionZ(img.zPosition, i, plane);

            int channel = getZCTCoords(plane)[1];
            if (img.min != null && (minMax[channel][0] == null ||
              img.min < minMax[channel][0]))
            {
              minMax[channel][0] = img.min;
            }
            if (img.max != null && (minMax[channel][1] == null ||
              img.max > minMax[channel][1]))
            {
              minMax[channel][1] = img.max;
            }
          }
        }

        // populate LogicalChannel data
        for (int q=0; q<getEffectiveSizeC(); q++) {
          if (q < channelNames.size()) {
            store.setChannelName(channelNames.get(q), i, q);
          }
          if (q < emWaves.size()) {
            Double wave = emWaves.get(q);
            Length emission = FormatTools.getEmissionWavelength(wave);
            if (emission != null) {
              store.setChannelEmissionWavelength(emission, i, q);
            }
          }
          if (q < exWaves.size()) {
            Double wave = exWaves.get(q);
            Length excitation = FormatTools.getExcitationWavelength(wave);
            if (excitation != null) {
              store.setChannelExcitationWavelength(excitation, i, q);
            }
          }

          if (detectorID != null) {
            store.setDetectorSettingsID(detectorID, i, q);
            if (bin != null) {
              store.setDetectorSettingsBinning(bin, i, q);
            }
            if (gain != null) {
              store.setDetectorSettingsGain(gain, i, q);
            }
          }
          LOGGER.trace("series {} channel {}: min = {}, max = {}", i, q, minMax[q][0], minMax[q][1]);
          if (store instanceof IMinMaxStore) {
            IMinMaxStore minMaxStore = (IMinMaxStore) store;
            minMaxStore.setChannelGlobalMinMax(q, minMax[q][0], minMax[q][1], i);
          }
        }
        if (temperature != null) {
          store.setImagingEnvironmentTemperature(
                  new Temperature(temperature, UNITS.CELSIUS), i);
        }
      }
      setSeries(0);

      // populate Plate data

      store.setPlateWellOriginX(new Length(0.5, UNITS.MICROMETER), 0);
      store.setPlateWellOriginY(new Length(0.5, UNITS.MICROMETER), 0);
    }
  }

  // -- Helper methods --

  private int getFieldFromSeries(int series) {
    if (oneTimepointPerSeries) series /= channelsPerTimepoint.size();
    return series % fieldCount;
  }

  private int getWellFromSeries(int series) {
    if (oneTimepointPerSeries) series /= channelsPerTimepoint.size();
    int well = series / fieldCount;

    int counter = -1;

    for (int row=0; row<plateMap.length; row++) {
      for (int col=0; col<plateMap[row].length; col++) {
        if (plateMap[row][col]) {
          counter++;
        }
        if (counter == well) {
          return row * wellCols + col;
        }
      }
    }
    return -1;
  }

  // -- Helper classes --

  class MinimalInCellHandler extends BaseHandler {
    private String currentImageFile;
    private String currentThumbnail;
    private int wellRow, wellCol;
    private int nChannels = 0;
    private boolean doT = true;
    private boolean doZ = true;
    private Image lastImage = null;

    @Override
    public void endElement(String uri, String localName, String qName) {
      if (qName.equals("PlateMap")) {
        int sizeT = getSizeT();
        if (sizeT == 0) {
          // There has been no <TimeSchedule> in the <PlateMap> defined to
          // populate channelsPerTimepoint so we have to assume that there is
          // only one timepoint otherwise the imageFiles array below will not
          // be correctly initialized.
          sizeT = 1;
        }
        if (channelsPerTimepoint.size() == 0) {
          // There has been no <TimeSchedule> in the <PlateMap> defined to
          // populate channelsPerTimepoint so we have to assume that all
          // channels are being acquired.
          CoreMetadata ms0 = core.get(0);
          channelsPerTimepoint.add(ms0.sizeC);
        }
        imageFiles = new Image[wellRows * wellCols][fieldCount][sizeT][];
        for (int well=0; well<wellRows*wellCols; well++) {
          for (int field=0; field<fieldCount; field++) {
            for (int t=0; t<sizeT; t++) {
              int channels = channelsPerTimepoint.get(t).intValue();
              imageFiles[well][field][t] = new Image[channels * getSizeZ()];
            }
          }
        }
      }
      else if (qName.equals("TimePoint")) {
        channelsPerTimepoint.add(nChannels);
        nChannels = 0;
      }
      else if (qName.equals("Times")) {
        if (channelsPerTimepoint.size() == 0) {
          channelsPerTimepoint.add(getSizeC());
        }
        for (int i=0; i<channelsPerTimepoint.size(); i++) {
          int c = channelsPerTimepoint.get(i).intValue();
          if (c == 0) {
            channelsPerTimepoint.set(i, getSizeC());
          }
        }
      }
    }

    @Override
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      CoreMetadata ms0 = core.get(0);
      if (qName.equals("Plate")) {
        wellRows = Integer.parseInt(attributes.getValue("rows"));
        wellCols = Integer.parseInt(attributes.getValue("columns"));
        plateMap = new boolean[wellRows][wellCols];
      }
      else if (qName.equals("Exclude")) {
        if (exclude == null) exclude = new boolean[wellRows][wellCols];
        int row = Integer.parseInt(attributes.getValue("row")) - 1;
        int col = Integer.parseInt(attributes.getValue("col")) - 1;
        exclude[row][col] = true;
      }
      else if (qName.equals("Images")) {
        totalImages = Integer.parseInt(attributes.getValue("number"));
      }
      else if (qName.equals("Image")) {
        String file = attributes.getValue("filename");
        String thumb = attributes.getValue("thumbnail");
        Location current = new Location(currentId).getAbsoluteFile();

        Location imageFile = new Location(current.getParentFile(), file);
        currentImageFile = imageFile.getAbsolutePath();
        currentThumbnail =
          new Location(current.getParentFile(), thumb).getAbsolutePath();
      }
      else if (qName.equals("Identifier")) {
        int field = Integer.parseInt(attributes.getValue("field_index"));
        int z = Integer.parseInt(attributes.getValue("z_index"));
        int c = Integer.parseInt(attributes.getValue("wave_index"));
        int t = Integer.parseInt(attributes.getValue("time_index"));
        int channels = channelsPerTimepoint.get(t).intValue();

        int index = FormatTools.getIndex("XYZCT", getSizeZ(),
          channels, 1, getSizeZ() * channels, z, c, 0);

        Image img = new Image();
        img.thumbnailFile = currentThumbnail;
        Location file = new Location(currentImageFile);
        img.filename = file.exists() ? currentImageFile : null;
        if (img.filename == null) {
          LOGGER.debug("{} does not exist.", currentImageFile);
        }
        currentImageFile = currentImageFile.toLowerCase();
        img.isTiff = currentImageFile.endsWith(".tif") ||
          currentImageFile.endsWith(".tiff");
        imageFiles[wellRow * wellCols + wellCol][field][t][index] = img;
        lastImage = img;
      }
      else if (qName.equals("MinMaxMean")) {
        if (lastImage != null) {
          lastImage.min = DataTools.parseDouble(attributes.getValue("min"));
          lastImage.max = DataTools.parseDouble(attributes.getValue("max"));
        }
      }
      else if (qName.equals("offset_point")) {
        fieldCount++;
      }
      else if (qName.equals("TimePoint") && doT) {
        ms0.sizeT++;
      }
      else if (qName.equals("Wavelength")) {
        String fusion = attributes.getValue("fusion_wave");
        if (fusion.equals("false")) ms0.sizeC++;
        String mode = attributes.getValue("imaging_mode");
        if (mode != null) {
          doZ = mode.equals("3-D");
        }
      }
      else if (qName.equals("AcqWave")) {
        nChannels++;
      }
      else if (qName.equals("ZDimensionParameters")) {
        String nz = attributes.getValue("number_of_slices");
        if (nz != null && doZ) {
          ms0.sizeZ = Integer.parseInt(nz);
        }
        else ms0.sizeZ = 1;
      }
      else if (qName.equals("Row")) {
        wellRow = Integer.parseInt(attributes.getValue("number")) - 1;
      }
      else if (qName.equals("Column")) {
        wellCol = Integer.parseInt(attributes.getValue("number")) - 1;
        plateMap[wellRow][wellCol] = true;
      }
      else if (qName.equals("Size")) {
        imageWidth = Integer.parseInt(attributes.getValue("width"));
        imageHeight = Integer.parseInt(attributes.getValue("height"));
      }
      else if (qName.equals("NamingRows")) {
        rowName = attributes.getValue("begin");
      }
      else if (qName.equals("NamingColumns")) {
        colName = attributes.getValue("begin");
      }
      else if (qName.equals("TimeSchedule")) {
        doT = Boolean.parseBoolean(attributes.getValue("enabled"));
      }
      else if (qName.equals("ExcitationFilter")) {
        exFilters.add(attributes.getValue("name"));
      }
      else if (qName.equals("EmissionFilter")) {
        emFilters.add(attributes.getValue("name"));
      }
    }
  }

  /** SAX handler for parsing XML. */
  class InCellHandler extends BaseHandler {
    private String currentQName;
    private boolean openImage;
    private MetadataStore store;
    private int nextPlate = 0;
    private int currentRow = -1, currentCol = -1;
    private int currentField = 0;
    private int currentImage, currentPlane;
    private Double timestamp, exposure;
    private Length zPosition;

    public InCellHandler(MetadataStore store) {
      this.store = store;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
      if (currentQName.equals("UserComment")) {
        String value = new String(ch, start, length);
        store.setImageDescription(value, 0);
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      if (qName.equals("Image")) {
        wellCoordinates.put(currentField,
          new int[] {currentRow, currentCol});
        openImage = false;

        int well = currentRow * wellCols + currentCol;
        Image img = imageFiles[well][currentField][currentImage][currentPlane];
        if (img != null) {
          img.deltaT = timestamp;
          img.exposure = exposure;
        }
      }
    }

    @Override
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      currentQName = qName;
      for (int i=0; i<attributes.getLength(); i++) {
        String key = qName + " - " + attributes.getQName(i);
        if (getMetadataValue(key) != null) {
          break;
        }
        addGlobalMeta(key, attributes.getValue(i));
      }

      if (qName.equals("Microscopy")) {
        String experimentID = MetadataTools.createLSID("Experiment", 0);
        store.setExperimentID(experimentID, 0);
        try {
          store.setExperimentType(
            MetadataTools.getExperimentType(attributes.getValue("type")), 0);
        }
        catch (FormatException e) {
          LOGGER.warn("", e);
        }
      }
      else if (qName.equals("Image")) {
        openImage = true;
        double time =
          Double.parseDouble(attributes.getValue("acquisition_time_ms"));
        timestamp = time / 1000;
      }
      else if (qName.equals("Identifier")) {
        currentField = Integer.parseInt(attributes.getValue("field_index"));
        int z = Integer.parseInt(attributes.getValue("z_index"));
        int c = Integer.parseInt(attributes.getValue("wave_index"));
        int t = Integer.parseInt(attributes.getValue("time_index"));
        currentImage = t;
        currentPlane = z * getSizeC() + c;
        int well = currentRow * wellCols + currentCol;
        Image img = imageFiles[well][currentField][currentImage][currentPlane];
        if (img != null) {
          img.zPosition = zPosition;
        }
      }
      else if (qName.equals("FocusPosition")) {
        final Double z = Double.valueOf(attributes.getValue("z"));
        zPosition = new Length(z, UNITS.REFERENCEFRAME);
      }
      else if (qName.equals("Creation")) {
        String date = attributes.getValue("date"); // yyyy-mm-dd
        String time = attributes.getValue("time"); // hh:mm:ss
        creationDate = date + "T" + time;
      }
      else if (qName.equals("ObjectiveCalibration")) {
        Double mag =
          Double.parseDouble(attributes.getValue("magnification"));
        store.setObjectiveNominalMagnification(mag, 0, 0);
        store.setObjectiveLensNA(new Double(
          attributes.getValue("numerical_aperture")), 0, 0);
        try {
         store.setObjectiveImmersion(MetadataTools.getImmersion("Other"), 0, 0);
        }
        catch (FormatException e) {
          LOGGER.warn("", e);
        }

        String objective = attributes.getValue("objective_name");
        String[] tokens = objective.split("_");

        store.setObjectiveManufacturer(tokens[0], 0, 0);
        String correction = tokens.length > 2 ? tokens[2] : "Other";
        try {
          store.setObjectiveCorrection(MetadataTools.getCorrection(correction), 0, 0);
        }
        catch (FormatException e) {
          LOGGER.warn("", e);
        }

        Double pixelSizeX = new Double(attributes.getValue("pixel_width"));
        Double pixelSizeY = new Double(attributes.getValue("pixel_height"));
        refractive = new Double(attributes.getValue("refractive_index"));

        x = FormatTools.getPhysicalSizeX(pixelSizeX);
        y = FormatTools.getPhysicalSizeY(pixelSizeY);
      }
      else if (qName.equals("ExcitationFilter")) {
        String wave = attributes.getValue("wavelength");
        if (wave != null) exWaves.add(new Double(wave));
      }
      else if (qName.equals("EmissionFilter")) {
        String wave = attributes.getValue("wavelength");
        if (wave != null) emWaves.add(new Double(wave));
        channelNames.add(attributes.getValue("name"));
      }
      else if (qName.equals("Camera")) {
        store.setDetectorModel(attributes.getValue("name"), 0, 0);
        try {
          store.setDetectorType(MetadataTools.getDetectorType("Other"), 0, 0);
        }
        catch (FormatException e) {
          LOGGER.warn("", e);
        }
      }
      else if (qName.equals("Binning")) {
        String binning = attributes.getValue("value");
        try {
          bin = MetadataTools.getBinning(binning);
        }
        catch (FormatException e) { }
      }
      else if (qName.equals("Gain")) {
        String value = attributes.getValue("value");
        if (value == null) {
          return;
        }
        try {
          gain = new Double(value);
        }
        catch (NumberFormatException e) {
          LOGGER.debug("Could not parse gain '" + value + "'", e);
        }
      }
      else if (qName.equals("PlateTemperature")) {
        temperature = new Double(attributes.getValue("value"));
      }
      else if (qName.equals("Plate")) {
        nextPlate++;
      }
      else if (qName.equals("Row")) {
        currentRow = Integer.parseInt(attributes.getValue("number")) - 1;
      }
      else if (qName.equals("Column")) {
        currentCol = Integer.parseInt(attributes.getValue("number")) - 1;
      }
      else if (qName.equals("Exposure") && openImage) {
        double exp = Double.parseDouble(attributes.getValue("time"));
        exposure = exp / 1000;
      }
      else if (qName.equals("offset_point")) {
        String x = attributes.getValue("x");
        String y = attributes.getValue("y");
        Integer index = DataTools.parseInteger(attributes.getValue("index"));
        if (null == index) {
          index = offsetPointCounter++;
        }

        posX.put(index, new Length(Double.valueOf(x), UNITS.REFERENCEFRAME));
        posY.put(index, new Length(Double.valueOf(y), UNITS.REFERENCEFRAME));

        addGlobalMetaList("X position for position", x);
        addGlobalMetaList("Y position for position", y);
      }
    }
  }

  class Image {
    public String filename;
    public String thumbnailFile;
    public boolean isTiff;
    public Double deltaT, exposure;
    public Length zPosition;
    public Double min;
    public Double max;
  }

}
