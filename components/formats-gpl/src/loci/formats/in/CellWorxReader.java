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
import java.util.HashMap;
import java.util.List;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;

import ome.xml.meta.MetadataConverter;
import ome.xml.meta.OMEXMLMetadataRoot;
import ome.xml.model.Image;
import ome.xml.model.Instrument;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;
import ome.units.UNITS;
import ome.units.quantity.Length;

/**
 * CellWorxReader is the file format reader for CellWorx .pnl files.
 */
public class CellWorxReader extends FormatReader {

  // -- Constants --

  private static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss yyyy";

  // -- Fields --

  private boolean[][] fieldMap;
  private String[][][] wellFiles;
  private String[][] logFiles;
  private int fieldCount = 0;
  private boolean doChannels = false;

  private String plateLogFile;
  private String zMapFile;

  private String lastFile;
  private IFormatReader lastReader;

  private OMEXMLService service;
  private HashMap<Integer, Timestamp> timestamps =
    new HashMap<Integer, Timestamp>();

  private String[] directoryList;

  // -- Constructor --

  /** Constructs a new CellWorx reader. */
  public CellWorxReader() {
    super("CellWorx", new String[] {"pnl", "htd", "log"});
    domains = new String[] {FormatTools.HCS_DOMAIN};
    hasCompanionFiles = true;
    datasetDescription = "One .htd file plus one or more .pnl or " +
      ".tif files and optionally one or more .log files";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, "pnl") || checkSuffix(name, "htd")) {
      return super.isThisType(name, open);
    }
    if (!open) return false;

    Location current = new Location(name).getAbsoluteFile();
    Location parent = current.getParentFile();

    String htdName = current.getName();
    while (htdName.indexOf('_') > 0) {
      htdName = htdName.substring(0, htdName.lastIndexOf("_"));
      if (new Location(parent, htdName + ".htd").exists() ||
        new Location(parent, htdName + ".HTD").exists())
      {
        return checkSuffix(name, "log") || isGroupFiles();
      }
    }
    return false;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    final List<String> files = new ArrayList<String>();
    files.add(currentId);
    if (plateLogFile != null && new Location(plateLogFile).exists()) {
      files.add(plateLogFile);
    }
    if (zMapFile != null) files.add(zMapFile);

    int row = getWellRow(getSeries());
    int col = getWellColumn(getSeries());

    if (new Location(logFiles[row][col]).exists()) {
      files.add(logFiles[row][col]);
    }
    if (!noPixels) {
      if (checkSuffix(wellFiles[row][col][0], "pnl")) {
        if (new Location(wellFiles[row][col][0]).exists()) {
          files.add(wellFiles[row][col][0]);
        }
      }
      else {
        for (String f : wellFiles[row][col]) {
          if (new Location(f).exists()) {
            files.add(f);
          }
        }
      }
    }
    return files.toArray(new String[files.size()]);
  }

  /* @see loci.formats.IFormatReader#getUsedFiles(boolean) */
  @Override
  public String[] getUsedFiles(boolean noPixels) {
    String[] files = super.getUsedFiles(noPixels);

    List<String> allFiles = new ArrayList<String>();
    for (String f : files) {
      allFiles.add(f);
    }
    if (directoryList != null) {
      Location root = new Location(currentId).getParentFile();
      for (String f : directoryList) {
        if (f.toLowerCase().indexOf("_thumb") > 0) {
          String path = new Location(root, f).getAbsolutePath();
          if (!allFiles.contains(path)) {
            allFiles.add(path);
          }
        }
      }
    }
    return allFiles.toArray(new String[allFiles.size()]);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    int fieldIndex = getSeries() % fieldCount;

    String file = getFile(getSeries(), no);
    if (file == null) {
      Arrays.fill(buf, (byte) 0);
      return buf;
    }

    if (lastFile == null || lastReader == null || !file.equals(lastFile) ||
      lastReader.getCurrentFile() == null)
    {
      if (lastReader != null) {
        lastReader.close();
      }
      try {
        lastReader = getReader(file, false);
      }
      catch (IOException e) {
        // this almost always means that the file does not exist
        LOGGER.debug("", e);
        return buf;
      }
      lastFile = file;
    }

    int planeIndex = no;
    if (lastReader.getSeriesCount() == fieldCount) {
      lastReader.setSeries(fieldIndex);
    }
    else {
      int[] zct = getZCTCoords(no);
      planeIndex = zct[0];
    }
    lastReader.openBytes(planeIndex, buf, x, y, w, h);
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      fieldMap = null;
      wellFiles = null;
      logFiles = null;
      fieldCount = 0;
      plateLogFile = null;
      zMapFile = null;
      lastFile = null;
      if (lastReader != null) {
        lastReader.close();
      }
      lastReader = null;
      doChannels = false;
      service = null;
      timestamps.clear();
      directoryList = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    // first, make sure that we have the .htd file

    if (!checkSuffix(id, "htd")) {
      LOGGER.info("Searching for .htd file");
      String base = new Location(id).getAbsolutePath();
      base = base.substring(0, base.lastIndexOf("_"));
      id = base + ".HTD";

      if (!new Location(id).exists()) {
        Location parent = new Location(id).getAbsoluteFile().getParentFile();
        directoryList = parent.list(true);
        for (String f : directoryList) {
          if (checkSuffix(f, "htd")) {
            id = new Location(parent, f).getAbsolutePath();
            LOGGER.info("Found .htd file {}", f);
            break;
          }
        }
      }
    }

    super.initFile(id);
    if (directoryList == null) {
      Location rootDir = new Location(id).getAbsoluteFile().getParentFile();
      directoryList = rootDir.list(true);
      Arrays.sort(directoryList);
    }

    try {
      ServiceFactory factory = new ServiceFactory();
      service = factory.getInstance(OMEXMLService.class);
    }
    catch (DependencyException exc) {
      throw new FormatException("Could not create OME-XML store.", exc);
    }

    String plateData = DataTools.readFile(id);
    String[] lines = plateData.split("\n");
    int xWells = 0, yWells = 0;
    int xFields = 0, yFields = 0;
    String[] wavelengths = null;
    int nTimepoints = 1;
    int zSteps = 1;

    // determine dataset dimensions
    for (String line : lines) {
      int split = line.indexOf("\",");
      if (split < 1) continue;
      String key = line.substring(1, split).trim();
      String value = line.substring(split + 2).trim();

      if (key.equals("XWells")) {
        xWells = Integer.parseInt(value);
      }
      else if (key.equals("YWells")) {
        yWells = Integer.parseInt(value);
        wellFiles = new String[yWells][xWells][];
        logFiles = new String[yWells][xWells];
      }
      else if (key.startsWith("WellsSelection")) {
        int row = Integer.parseInt(key.substring(14)) - 1;
        String[] mapping = value.split(",");
        for (int col=0; col<xWells; col++) {
          if (new Boolean(mapping[col].trim()).booleanValue()) {
            wellFiles[row][col] = new String[1];
          }
        }
      }
      else if (key.equals("XSites")) {
        xFields = Integer.parseInt(value);
      }
      else if (key.equals("YSites")) {
        yFields = Integer.parseInt(value);
        fieldMap = new boolean[yFields][xFields];
      }
      else if (key.equals("TimePoints")) {
        nTimepoints = Integer.parseInt(value);
      }
      else if (key.equals("ZSteps")) {
        zSteps = Integer.parseInt(value);
      }
      else if (key.startsWith("SiteSelection")) {
        int row = Integer.parseInt(key.substring(13)) - 1;
        String[] mapping = value.split(",");
        for (int col=0; col<xFields; col++) {
          fieldMap[row][col] = new Boolean(mapping[col].trim()).booleanValue();
        }
      }
      else if (key.equals("Waves")) {
        doChannels = new Boolean(value.toLowerCase());
      }
      else if (key.equals("NWavelengths")) {
        wavelengths = new String[Integer.parseInt(value)];
      }
      else if (key.startsWith("WaveName")) {
        int index = Integer.parseInt(key.substring(8)) - 1;
        wavelengths[index] = value.replaceAll("\"", "");
      }
    }

    for (int row=0; row<fieldMap.length; row++) {
      for (int col=0; col<fieldMap[row].length; col++) {
        if (fieldMap[row][col]) fieldCount++;
      }
    }

    // find pixels files
    String plateName = new Location(id).getAbsolutePath();
    plateName = plateName.substring(0, plateName.lastIndexOf(".")) + "_";
    int wellCount = 0;
    for (int row=0; row<wellFiles.length; row++) {
      for (int col=0; col<wellFiles[row].length; col++) {
        if (wellFiles[row][col] != null) {
          wellCount++;
          char rowLetter = (char) (row + 'A');
          String base = plateName + rowLetter + String.format("%02d", col + 1);
          wellFiles[row][col][0] = base + ".pnl";
          logFiles[row][col] = base + "_scan.log";

          if (!new Location(wellFiles[row][col][0]).exists()) {
            // using TIFF files instead

            wellFiles[row][col] = getTiffFiles(
              plateName, rowLetter, col, wavelengths.length, nTimepoints, zSteps);
          }
        }
      }
    }

    plateLogFile = plateName + "scan.log";

    String serialNumber = null;

    if (new Location(plateLogFile).exists()) {
      String[] f = DataTools.readFile(plateLogFile).split("\n");
      for (String line : f) {
        if (line.trim().startsWith("Z Map File")) {
          String file = line.substring(line.indexOf(':') + 1);
          file = file.substring(file.lastIndexOf("/") + 1).trim();
          String parent = new Location(id).getAbsoluteFile().getParent();
          zMapFile = new Location(parent, file).getAbsolutePath();
        }
        else if (line.trim().startsWith("Scanner SN")) {
          serialNumber = line.substring(line.indexOf(':') + 1).trim();
        }
      }
    }

    int seriesCount = fieldCount * wellCount;

    int planeIndex = 0;
    int seriesIndex = 0;
    String file = getFile(seriesIndex, planeIndex);
    while (!new Location(file).exists()) {
      if (planeIndex <  zSteps * nTimepoints * wavelengths.length) {
        planeIndex++;
      }
      else if (seriesIndex < seriesCount - 1) {
        planeIndex = 0;
        seriesIndex++;
      }
      else {
        break;
      }
      file = getFile(seriesIndex, planeIndex);
    }
    IFormatReader pnl = getReader(file, true);

    core.clear();
    for (int i=0; i<seriesCount; i++) {
      CoreMetadata ms = new CoreMetadata();
      core.add(ms);
      setSeries(i);
      ms.littleEndian = pnl.isLittleEndian();
      ms.sizeX = pnl.getSizeX();
      ms.sizeY = pnl.getSizeY();
      ms.pixelType = pnl.getPixelType();
      ms.sizeZ = zSteps;
      ms.sizeT = nTimepoints;
      ms.sizeC = wavelengths.length;
      ms.imageCount = getSizeZ() * getSizeC() * getSizeT();
      ms.dimensionOrder = "XYCZT";
      ms.rgb = false;
      ms.interleaved = pnl.isInterleaved();
    }

    OMEXMLMetadata readerMetadata = (OMEXMLMetadata) pnl.getMetadataStore();
    OMEXMLMetadataRoot root = (OMEXMLMetadataRoot) readerMetadata.getRoot();
    Instrument instrument = root.getInstrument(0);
    List<Image> images = root.copyImageList();

    OMEXMLMetadataRoot convertRoot = new OMEXMLMetadataRoot();
    convertRoot.addInstrument(instrument);
    for (int i=0; i<core.size()/images.size(); i++) {
      for (Image img : images) {
        convertRoot.addImage(img);
      }
    }
    OMEXMLMetadata convertMetadata;
    try {
      convertMetadata = service.createOMEXMLMetadata();
    }
    catch (ServiceException exc) {
      throw new FormatException("Could not create OME-XML store.", exc);
    }

    convertMetadata.setRoot(convertRoot);

    pnl.close();

    MetadataStore store = makeFilterMetadata();
    MetadataConverter.convertMetadata(convertMetadata, store);
    MetadataTools.populatePixels(store, this, true);

    // check for stage positions in each file

    MetadataLevel metadataLevel = metadataOptions.getMetadataLevel();
    for (int s=0; s<getSeriesCount(); s++) {
      setSeries(s);

      if (metadataLevel != MetadataLevel.MINIMUM) {
        String firstFile = null;
        int plane = 0;
        while ((firstFile == null || !new Location(firstFile).exists()) &&
          plane < getImageCount())
        {
          firstFile = getFile(s, plane);
          plane++;
        }
        if (firstFile != null && new Location(firstFile).exists()) {
          try (IFormatReader helper = getReader(firstFile, true)) {
            IMetadata meta = (IMetadata) helper.getMetadataStore();
            int pnlSeries = s % helper.getSeriesCount();
            Length posX = meta.getPlanePositionX(pnlSeries, 0);
            Length posY = meta.getPlanePositionY(pnlSeries, 0);
            Length posZ = meta.getPlanePositionZ(pnlSeries, 0);

            for (int p=0; p<getImageCount(); p++) {
              if (posX != null) {
                store.setPlanePositionX(posX, s, p);
              }
              if (posY != null) {
                store.setPlanePositionY(posY, s, p);
              }
              if (posZ != null) {
                store.setPlanePositionZ(posZ, s, p);
              }
            }
          }
        }
      }
      else {
        for (int p=0; p<getImageCount(); p++) {
          store.setPlanePositionX(null, s, p);
          store.setPlanePositionY(null, s, p);
          store.setPlanePositionZ(null, s, p);
        }
      }
    }
    setSeries(0);

    // set up plate linkages

    String plateID = MetadataTools.createLSID("Plate", 0);

    Location plate = new Location(id).getAbsoluteFile();

    store.setPlateID(plateID, 0);

    plateName = plate.getName();
    if (plateName.indexOf('.') > 0) {
      plateName = plateName.substring(0, plateName.lastIndexOf('.'));
    }
    store.setPlateName(plateName, 0);

    store.setPlateRows(new PositiveInteger(wellFiles.length), 0);
    store.setPlateColumns(new PositiveInteger(wellFiles[0].length), 0);

    for (int i=0; i<core.size(); i++) {
      store.setImageID(MetadataTools.createLSID("Image", i), i);
    }

    String plateAcqID = MetadataTools.createLSID("PlateAcquisition", 0, 0);
    store.setPlateAcquisitionID(plateAcqID, 0, 0);

    PositiveInteger fieldCount =
      FormatTools.getMaxFieldCount(fieldMap.length * fieldMap[0].length);

    if (fieldCount != null) {
      store.setPlateAcquisitionMaximumFieldCount(fieldCount, 0, 0);
    }

    int nextImage = 0;
    for (int row=0; row<wellFiles.length; row++) {
      for (int col=0; col<wellFiles[row].length; col++) {
        int wellIndex = row * wellFiles[row].length + col;
        String wellID = MetadataTools.createLSID("Well", 0, wellIndex);
        store.setWellID(wellID, 0, wellIndex);
        store.setWellColumn(new NonNegativeInteger(col), 0, wellIndex);
        store.setWellRow(new NonNegativeInteger(row), 0, wellIndex);

        int fieldIndex = 0;
        for (int fieldRow=0; fieldRow<fieldMap.length; fieldRow++) {
          for (int fieldCol=0; fieldCol<fieldMap[fieldRow].length; fieldCol++) {
            if (fieldMap[fieldRow][fieldCol] && wellFiles[row][col] != null) {
              String wellSampleID = MetadataTools.createLSID("WellSample",
                0, wellIndex, fieldIndex);
              store.setWellSampleID(wellSampleID, 0, wellIndex, fieldIndex);
              String imageID = MetadataTools.createLSID("Image", nextImage);
              store.setWellSampleImageRef(imageID, 0, wellIndex, fieldIndex);
              store.setWellSampleIndex(
                new NonNegativeInteger(nextImage), 0, wellIndex, fieldIndex);

              store.setPlateAcquisitionWellSampleRef(
                wellSampleID, 0, 0, nextImage);

              String well = (char) (row + 'A') + String.format("%02d", col + 1);
              store.setImageName(
                "Well " + well + " Field #" + (fieldIndex + 1), nextImage);
              nextImage++;
              fieldIndex++;
            }
          }
        }
      }
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      if (serialNumber != null) {
        store.setMicroscopeSerialNumber(serialNumber, 0);
      }

      for (int well=0; well<wellCount; well++) {
        parseWellLogFile(well, store);
      }
      if (timestamps.size() > 0) {
        store.setPlateAcquisitionStartTime(timestamps.get(0), 0, 0);
        store.setPlateAcquisitionEndTime(
          timestamps.get(timestamps.size() - 1), 0, 0);
      }
      for (int i=0; i<core.size(); i++) {
        for (int c=0; c<getSizeC(); c++) {
          if (c < wavelengths.length && wavelengths[c] != null) {
            store.setChannelName(wavelengths[c], i, c);
          }
        }
      }
    }
  }

  // -- Helper methods --

  /** Retrieve the well index corresponding to the given series. */
  private int getWell(int seriesIndex) {
    int wellIndex = seriesIndex / fieldCount;
    int counter = -1;
    for (int row=0; row<wellFiles.length; row++) {
      for (int col=0; col<wellFiles[row].length; col++) {
        if (wellFiles[row][col] != null) counter++;
        if (counter == wellIndex) return row * wellFiles[row].length + col;
      }
    }
    return -1;
  }

  /** Retrieve the well row corresponding to the given series. */
  private int getWellRow(int seriesIndex) {
    int well = getWell(seriesIndex);
    return well / wellFiles[0].length;
  }

  /** Retrieve the well column corresponding to the given series. */
  private int getWellColumn(int seriesIndex) {
    int well = getWell(seriesIndex);
    return well % wellFiles[0].length;
  }

  /** Retrieve the .pnl file corresponding to the given series. */
  private String getFile(int seriesIndex, int no) {
    int row = getWellRow(seriesIndex);
    int col = getWellColumn(seriesIndex);
    int field = seriesIndex % fieldCount;
    if (wellFiles[row][col].length == 0) {
      return wellFiles[row][col][0];
    }

    int imageCount = wellFiles[row][col].length / fieldCount;
    if (field * imageCount + no < wellFiles[row][col].length) {
      return wellFiles[row][col][field * imageCount + no];
    }
    else if (field < wellFiles[row][col].length) {
      return wellFiles[row][col][field];
    }
    else if (imageCount == 0 && wellFiles[row][col].length == 1) {
      return wellFiles[row][col][0];
    }
    return null;
  }

  /** Parse metadata from a well log file. */
  private void parseWellLogFile(int wellIndex, MetadataStore store)
    throws IOException
  {
    int seriesIndex = wellIndex * fieldCount;
    int row = getWellRow(seriesIndex);
    int col = getWellColumn(seriesIndex);
    int well = row * wellFiles[0].length + col;
    String logFile = logFiles[row][col];
    if (!new Location(logFile).exists()) {
      return;
    }
    LOGGER.debug("Parsing log file for well {}{}", (char) (row + 'A'), col + 1);

    int oldSeries = getSeries();
    setSeries(seriesIndex);

    String data = DataTools.readFile(logFile);
    String[] lines = data.split("\n");
    for (String line : lines) {
      line = line.trim();
      int separator = line.indexOf(':');
      if (separator < 0) continue;
      String key = line.substring(0, separator).trim();
      String value = line.substring(separator + 1).trim();

      addSeriesMeta(key, value);

      if (key.equals("Date")) {
        String date = DateTools.formatDate(value, DATE_FORMAT);
        for (int field=0; field<fieldCount; field++) {
          if (date != null) {
            int imageIndex = seriesIndex + field;
            timestamps.put(imageIndex, new Timestamp(date));
            store.setImageAcquisitionDate(
              timestamps.get(imageIndex), imageIndex);
          }
        }
      }
      else if (key.equals("Scan Origin")) {
        String[] axes = value.split(",");
        Double posX = new Double(axes[0]);
        Double posY = new Double(axes[1]);
        for (int fieldRow=0; fieldRow<fieldMap.length; fieldRow++) {
          for (int fieldCol=0; fieldCol<fieldMap[fieldRow].length; fieldCol++) {
            if (fieldMap[fieldRow][fieldCol] && wellFiles[row][col] != null) {
              int field = fieldRow * fieldMap[fieldRow].length + fieldCol;
              Length px = new Length(posX, UNITS.REFERENCEFRAME);
              Length py = new Length(posY, UNITS.REFERENCEFRAME);
              store.setWellSamplePositionX(px, 0, well, field);
              store.setWellSamplePositionY(py, 0, well, field);

              addGlobalMetaList("X position for position", axes[0]);
              addGlobalMetaList("Y position for position", axes[1]);
            }
          }
        }
      }
      else if (key.equals("Scan Area")) {
        int s = value.indexOf('x');
        if (s > 0) {
          int end = value.indexOf(" ", s + 2);
          Double xSize = new Double(value.substring(0, s).trim());
          Double ySize = new Double(value.substring(s + 1, end).trim());

          Length x = FormatTools.getPhysicalSizeX(xSize / getSizeX());
          Length y = FormatTools.getPhysicalSizeY(ySize / getSizeY());

          for (int field=0; field<fieldCount; field++) {
            int index = seriesIndex + field;
            if (x != null) {
              store.setPixelsPhysicalSizeX(x, index);
            }
            if (y != null) {
              store.setPixelsPhysicalSizeY(y, index);
            }
          }
        }
      }
      else if (key.startsWith("Channel")) {
        int start = key.indexOf(' ') + 1;
        int end = key.indexOf(" ", start);
        if (end < 0) end = key.length();
        int index = Integer.parseInt(key.substring(start, end)) - 1;

        String[] tokens = value.split(",");
        for (String token : tokens) {
          token = token.trim();
          if (token.startsWith("gain")) {
            String instrumentID = MetadataTools.createLSID("Instrument", 0);
            Double gain = new Double(token.replaceAll("gain ", ""));
            String detectorID = MetadataTools.createLSID("Detector", 0, 0);

            store.setInstrumentID(instrumentID, 0);
            store.setDetectorID(detectorID, 0, 0);

            for (int field=0; field<fieldCount; field++) {
              store.setImageInstrumentRef(instrumentID, seriesIndex + field);
              store.setDetectorSettingsGain(gain, seriesIndex + field, index);
              store.setDetectorSettingsID(detectorID,
                seriesIndex + field, index);
            }
          }
          else if (token.startsWith("EX")) {
            int slash = token.indexOf('/');
            if (slash > 0) {
              String ex = token.substring(0, slash).trim();
              String em = token.substring(slash + 1).trim();

              if (ex.indexOf(' ') > 0) ex = ex.substring(ex.indexOf(' ') + 1);
              if (em.indexOf(' ') > 0) {
                em = em.substring(em.indexOf(' ') + 1);
                if (em.indexOf(' ') > 0) {
                  em = em.substring(0, em.indexOf(' '));
                }
              }

              Double emission = new Double(em);
              Double excitation = new Double(ex);

              Length exWave = FormatTools.getExcitationWavelength(excitation);
              Length emWave = FormatTools.getEmissionWavelength(emission);

              for (int field=0; field<fieldCount; field++) {
                if (exWave != null) {
                  store.setChannelExcitationWavelength(
                    exWave, seriesIndex + field, index);
                }
                if (emWave != null) {
                  store.setChannelEmissionWavelength(
                    emWave, seriesIndex + field, index);
                }
              }
            }
          }
        }
      }
    }

    setSeries(oldSeries);
  }

  private IFormatReader getReader(String file, boolean omexml)
    throws FormatException, IOException
  {
    IFormatReader pnl = new DeltavisionReader();
    if (checkSuffix(file, "tif")) {
      pnl = new MetamorphReader();
    }

    if (omexml) {
      IMetadata metadata;
      try {
        metadata = service.createOMEXMLMetadata();
      }
      catch (ServiceException exc) {
        throw new FormatException("Could not create OME-XML store.", exc);
      }
      pnl.setMetadataStore(metadata);
    }
    pnl.setId(file);
    return pnl;
  }

  private String[] getTiffFiles(String plateName, char rowLetter, int col,
    int channels, int nTimepoints, int zSteps)
    throws FormatException
  {
    String well = rowLetter + String.format("%02d", col + 1);
    String base = plateName + well;

    String[] files = new String[fieldCount * channels * nTimepoints * zSteps];

    int nextFile = 0;
    for (int field=0; field<fieldCount; field++) {
      for (int channel=0; channel<channels; channel++) {
        for (int t=0; t<nTimepoints; t++, nextFile++) {
          String file = base;
          if (fieldCount > 1) {
           file += "_s" + (field + 1);
          }
          if (doChannels || channels > 1) {
            file += "_w" + (channel + 1);
          }
          if (nTimepoints > 1) {
            file += "_t" + nTimepoints;
          }
          files[nextFile] = file + ".tif";

          if (!new Location(files[nextFile]).exists()) {
            files[nextFile] = file + ".TIF";
          }
        }
      }
    }

    boolean noneExist = true;
    for (String file : files) {
      if (file != null && new Location(file).exists()) {
        noneExist = false;
        break;
      }
    }

    if (noneExist) {
      nextFile = 0;
      Location parent =
        new Location(currentId).getAbsoluteFile().getParentFile();
      if (directoryList == null) {
        directoryList = parent.list(true);
        Arrays.sort(directoryList);
      }
      for (String f : directoryList) {
        if (checkSuffix(f, new String [] {"tif", "tiff", "pnl"})) {
          String path = new Location(parent, f).getAbsolutePath();
          if (path.startsWith(base) && path.toLowerCase().indexOf("_thumb") < 0)
          {
            files[nextFile++] = path;
            noneExist = false;
          }
        }
      }

      if (noneExist) {
        // if all else fails, look for a directory structure:
        //  * file.htd
        //  * TimePoint_<t>
        //    * ZStep_<z>
        //      * file_<...>.tif
        base = base.substring(base.lastIndexOf(File.separator) + 1);
        nextFile = 0;
        for (int i=0; i<nTimepoints; i++) {
          Location dir = new Location(parent, "TimePoint_" + (i + 1));
          if (dir.exists() && dir.isDirectory()) {
            for (int z=0; z<zSteps; z++) {
              Location file = new Location(dir, "ZStep_" + (z + 1));
              if (file.exists() && file.isDirectory()) {
                String[] zList = file.list(true);
                Arrays.sort(zList);
                for (String f : zList) {
                  String path = new Location(file, f).getAbsolutePath();
                  if (f.startsWith(base) && path.indexOf("_thumb") < 0) {
                    if (nextFile < files.length) {
                      files[nextFile] = path;
                    }
                    nextFile++;
                  }
                }
              }
            }
          }
        }
        if (nextFile != files.length) {
          throw new FormatException(
            "Well " + well + " expected " + files.length +
            " files; found " + nextFile);
        }
      }
    }

    return files;
  }

}
