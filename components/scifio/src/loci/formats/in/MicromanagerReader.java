//
// MicromanagerReader.java
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
import java.util.StringTokenizer;
import java.util.Vector;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.xml.XMLTools;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * MicromanagerReader is the file format reader for Micro-Manager files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/MicromanagerReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/MicromanagerReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class MicromanagerReader extends FormatReader {

  // -- Constants --

  public static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

  /** File containing extra metadata. */
  private static final String METADATA = "metadata.txt";

  /**
   * Optional file containing additional acquisition parameters.
   * (And yes, the spelling is correct.)
   */
  private static final String XML = "Acqusition.xml";

  // -- Fields --

  /** Helper reader for TIFF files. */
  private MinimalTiffReader tiffReader;

  /** List of TIFF files to open. */
  private Vector<String> tiffs;

  private String metadataFile;
  private String xmlFile;

  private String[] channels;

  private String comment, time;
  private Double exposureTime, sliceThickness, pixelSize;
  private Double[] timestamps;

  private int gain;
  private String binning, detectorID, detectorModel, detectorManufacturer;
  private double temperature;
  private Vector<Double> voltage;
  private String cameraRef;
  private String cameraMode;

  // -- Constructor --

  /** Constructs a new Micromanager reader. */
  public MicromanagerReader() {
    super("Micro-Manager", new String[] {"tif", "tiff", "txt", "xml"});
    domains = new String[] {FormatTools.LM_DOMAIN};
    hasCompanionFiles = true;
    datasetDescription = "A 'metadata.txt' file plus or or more .tif files";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (!open) return false; // not allowed to touch the file system
    if (name.equals(METADATA) || name.endsWith(File.separator + METADATA) ||
      name.equals(XML) || name.endsWith(File.separator + XML))
    {
      final int blockSize = 8192;
      try {
        RandomAccessInputStream stream = new RandomAccessInputStream(name);
        long length = stream.length();
        String data = stream.readString((int) Math.min(blockSize, length));
        stream.close();
        return length > 0 && (data.indexOf("Micro-Manager") >= 0 ||
          data.indexOf("micromanager") >= 0);
      }
      catch (IOException e) {
        return false;
      }
    }
    try {
      Location parent = new Location(name).getAbsoluteFile().getParentFile();
      Location metaFile = new Location(parent, METADATA);
      RandomAccessInputStream s = new RandomAccessInputStream(name);
      boolean validTIFF = isThisType(s);
      s.close();
      return validTIFF && isThisType(metaFile.getAbsolutePath(), open);
    }
    catch (NullPointerException e) { }
    catch (IOException e) { }
    return false;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException
  {
    if (tiffReader == null) tiffReader = new MinimalTiffReader();
    return tiffReader.isThisType(stream);
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    Vector<String> files = new Vector<String>();
    files.add(metadataFile);
    if (xmlFile != null) {
      files.add(xmlFile);
    }
    if (!noPixels) files.addAll(tiffs);
    return files.toArray(new String[files.size()]);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (new Location(tiffs.get(no)).exists()) {
      tiffReader.setId(tiffs.get(no));
      return tiffReader.openBytes(0, buf, x, y, w, h);
    }
    LOGGER.warn("File for image #{} ({}) is missing.", no, tiffs.get(no));
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (tiffReader != null) tiffReader.close(fileOnly);
    if (!fileOnly) {
      tiffReader = null;
      tiffs = null;
      comment = time = null;
      exposureTime = sliceThickness = pixelSize = null;
      timestamps = null;
      metadataFile = null;
      channels = null;
      gain = 0;
      binning = detectorID = detectorModel = detectorManufacturer = null;
      temperature = 0;
      voltage = null;
      cameraRef = cameraMode = null;
      xmlFile = null;
    }
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    return tiffReader.getOptimalTileWidth();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return tiffReader.getOptimalTileWidth();
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  public void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    tiffReader = new MinimalTiffReader();

    LOGGER.info("Reading metadata file");

    // find metadata.txt

    Location file = new Location(currentId).getAbsoluteFile();
    Location parentFile = file.getParentFile();
    metadataFile = METADATA;
    String parent = "";
    if (file.exists()) {
      metadataFile = new Location(parentFile, METADATA).getAbsolutePath();
      parent = parentFile.getAbsolutePath() + File.separator;
    }
    in = new RandomAccessInputStream(metadataFile);

    // usually a small file, so we can afford to read it into memory

    String s = DataTools.readFile(metadataFile);

    LOGGER.info("Finding image file names");

    // find the name of a TIFF file
    String baseTiff = null;
    tiffs = new Vector<String>();
    int pos = 0;
    while (true) {
      pos = s.indexOf("FileName", pos);
      if (pos == -1 || pos >= in.length()) break;
      String name = s.substring(s.indexOf(":", pos), s.indexOf(",", pos));
      baseTiff = parent + name.substring(3, name.length() - 1);
      pos++;
    }

    // now parse the rest of the metadata

    // metadata.txt looks something like this:
    //
    // {
    //   "Section Name": {
    //      "Key": "Value",
    //      "Array key": [
    //        first array value, second array value
    //      ]
    //   }
    //
    // }

    LOGGER.info("Populating metadata");

    Vector<Double> stamps = new Vector<Double>();
    voltage = new Vector<Double>();

    StringTokenizer st = new StringTokenizer(s, "\n");
    int[] slice = new int[3];
    while (st.hasMoreTokens()) {
      String token = st.nextToken().trim();
      boolean open = token.indexOf("[") != -1;
      boolean closed = token.indexOf("]") != -1;
      if (open || (!open && !closed && !token.equals("{") &&
        !token.startsWith("}")))
      {
        int quote = token.indexOf("\"") + 1;
        String key = token.substring(quote, token.indexOf("\"", quote));
        String value = null;

        if (open == closed) {
          value = token.substring(token.indexOf(":") + 1);
        }
        else if (!closed) {
          StringBuffer valueBuffer = new StringBuffer();
          while (!closed) {
            token = st.nextToken();
            closed = token.indexOf("]") != -1;
            valueBuffer.append(token);
          }
          value = valueBuffer.toString();
          value = value.replaceAll("\n", "");
        }
        if (value == null) continue;

        int startIndex = value.indexOf("[");
        int endIndex = value.indexOf("]");
        if (endIndex == -1) endIndex = value.length();

        value = value.substring(startIndex + 1, endIndex).trim();
        if (value.length() == 0) {
          continue;
        }
        value = value.substring(0, value.length() - 1);
        value = value.replaceAll("\"", "");
        if (value.endsWith(",")) value = value.substring(0, value.length() - 1);
        addGlobalMeta(key, value);
        if (key.equals("Channels")) core[0].sizeC = Integer.parseInt(value);
        else if (key.equals("ChNames")) {
          channels = value.split(",");
          for (int q=0; q<channels.length; q++) {
            channels[q] = channels[q].replaceAll("\"", "").trim();
          }
        }
        else if (key.equals("Frames")) {
          core[0].sizeT = Integer.parseInt(value);
        }
        else if (key.equals("Slices")) {
          core[0].sizeZ = Integer.parseInt(value);
        }
        else if (key.equals("PixelSize_um")) {
          pixelSize = new Double(value);
        }
        else if (key.equals("z-step_um")) {
          sliceThickness = new Double(value);
        }
        else if (key.equals("Time")) time = value;
        else if (key.equals("Comment")) comment = value;
      }

      if (token.startsWith("\"FrameKey")) {
        int dash = token.indexOf("-") + 1;
        int nextDash = token.indexOf("-", dash);
        slice[2] = Integer.parseInt(token.substring(dash, nextDash));
        dash = nextDash + 1;
        nextDash = token.indexOf("-", dash);
        slice[1] = Integer.parseInt(token.substring(dash, nextDash));
        dash = nextDash + 1;
        slice[0] = Integer.parseInt(token.substring(dash,
          token.indexOf("\"", dash)));

        token = st.nextToken().trim();
        String key = "", value = "";
        boolean valueArray = false;

        while (!token.startsWith("}")) {
          if (valueArray) {
            if (token.trim().equals("],")) {
              valueArray = false;
            }
            else {
              value += token.trim().replaceAll("\"", "");
              token = st.nextToken().trim();
              continue;
            }
          }
          else {
            int colon = token.indexOf(":");
            key = token.substring(1, colon).trim();
            value = token.substring(colon + 1, token.length() - 1).trim();

            key = key.replaceAll("\"", "");
            value = value.replaceAll("\"", "");

            if (token.trim().endsWith("[")) {
              valueArray = true;
              token = st.nextToken().trim();
              continue;
            }
          }

          addGlobalMeta(key, value);

          if (key.equals("Exposure-ms")) {
            double t = Double.parseDouble(value);
            exposureTime = new Double(t / 1000);
          }
          else if (key.equals("ElapsedTime-ms")) {
            double t = Double.parseDouble(value);
            stamps.add(new Double(t / 1000));
          }
          else if (key.equals("Core-Camera")) cameraRef = value;
          else if (key.equals(cameraRef + "-Binning")) {
            if (value.indexOf("x") != -1) binning = value;
            else binning = value + "x" + value;
          }
          else if (key.equals(cameraRef + "-CameraID")) detectorID = value;
          else if (key.equals(cameraRef + "-CameraName")) detectorModel = value;
          else if (key.equals(cameraRef + "-Gain")) {
            gain = (int) Double.parseDouble(value);
          }
          else if (key.equals(cameraRef + "-Name")) {
            detectorManufacturer = value;
          }
          else if (key.equals(cameraRef + "-Temperature")) {
            temperature = Double.parseDouble(value);
          }
          else if (key.equals(cameraRef + "-CCDMode")) {
            cameraMode = value;
          }
          else if (key.startsWith("DAC-") && key.endsWith("-Volts")) {
            voltage.add(new Double(value));
          }

          token = st.nextToken().trim();
        }
      }
    }

    timestamps = stamps.toArray(new Double[stamps.size()]);
    Arrays.sort(timestamps);

    // look for the optional companion XML file

    parentFile = new Location(currentId).getAbsoluteFile().getParentFile();
    if (new Location(parentFile, XML).exists()) {
      xmlFile = new Location(parent, XML).getAbsolutePath();
      parseXMLFile();
    }

    // build list of TIFF files

    buildTIFFList(baseTiff);

    if (tiffs.size() == 0) {
      Vector<String> uniqueZ = new Vector<String>();
      Vector<String> uniqueC = new Vector<String>();
      Vector<String> uniqueT = new Vector<String>();

      Location dir = new Location(currentId).getAbsoluteFile().getParentFile();
      String[] files = dir.list(true);
      Arrays.sort(files);
      for (String f : files) {
        if (checkSuffix(f, "tif") || checkSuffix(f, "tiff")) {
          String[] blocks = f.split("_");
          if (!uniqueT.contains(blocks[1])) uniqueT.add(blocks[1]);
          if (!uniqueC.contains(blocks[2])) uniqueC.add(blocks[2]);
          if (!uniqueZ.contains(blocks[3])) uniqueZ.add(blocks[3]);

          tiffs.add(new Location(dir, f).getAbsolutePath());
        }
      }

      core[0].sizeZ = uniqueZ.size();
      core[0].sizeC = uniqueC.size();
      core[0].sizeT = uniqueT.size();

      if (tiffs.size() == 0) {
        throw new FormatException("Could not find TIFF files.");
      }
    }

    tiffReader.setId(tiffs.get(0));

    if (getSizeZ() == 0) core[0].sizeZ = 1;
    if (getSizeT() == 0) core[0].sizeT = tiffs.size() / getSizeC();

    core[0].sizeX = tiffReader.getSizeX();
    core[0].sizeY = tiffReader.getSizeY();
    core[0].dimensionOrder = "XYZCT";
    core[0].pixelType = tiffReader.getPixelType();
    core[0].rgb = tiffReader.isRGB();
    core[0].interleaved = false;
    core[0].littleEndian = tiffReader.isLittleEndian();
    core[0].imageCount = getSizeZ() * getSizeC() * getSizeT();
    core[0].indexed = false;
    core[0].falseColor = false;
    core[0].metadataComplete = true;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);
    if (time != null) {
      String date = DateTools.formatDate(time, DATE_FORMAT);
      store.setImageAcquiredDate(date, 0);
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      store.setImageDescription(comment, 0);

      // link Instrument and Image
      String instrumentID = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrumentID, 0);
      store.setImageInstrumentRef(instrumentID, 0);

      for (int i=0; i<channels.length; i++) {
        store.setChannelName(channels[i], 0, i);
      }

      if (pixelSize != null && pixelSize > 0) {
        store.setPixelsPhysicalSizeX(new PositiveFloat(pixelSize), 0);
        store.setPixelsPhysicalSizeY(new PositiveFloat(pixelSize), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeX; got {}",
          pixelSize);
      }
      if (sliceThickness != null && sliceThickness > 0) {
        store.setPixelsPhysicalSizeZ(new PositiveFloat(sliceThickness), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeZ; got {}",
          sliceThickness);
      }

      for (int i=0; i<getImageCount(); i++) {
        store.setPlaneExposureTime(exposureTime, 0, i);
        if (i < timestamps.length) {
          store.setPlaneDeltaT(timestamps[i], 0, i);
        }
      }

      String serialNumber = detectorID;
      detectorID = MetadataTools.createLSID("Detector", 0, 0);

      for (int i=0; i<channels.length; i++) {
        store.setDetectorSettingsBinning(getBinning(binning), 0, i);
        store.setDetectorSettingsGain(new Double(gain), 0, i);
        if (i < voltage.size()) {
          store.setDetectorSettingsVoltage(voltage.get(i), 0, i);
        }
        store.setDetectorSettingsID(detectorID, 0, i);
      }

      store.setDetectorID(detectorID, 0, 0);
      if (detectorModel != null) {
        store.setDetectorModel(detectorModel, 0, 0);
      }

      if (serialNumber != null) {
        store.setDetectorSerialNumber(serialNumber, 0, 0);
      }

      if (detectorManufacturer != null) {
        store.setDetectorManufacturer(detectorManufacturer, 0, 0);
      }

      if (cameraMode == null) cameraMode = "Other";
      store.setDetectorType(getDetectorType(cameraMode), 0, 0);
      store.setImagingEnvironmentTemperature(temperature, 0);
    }
  }

  // -- Helper methods --

  /**
   * Populate the list of TIFF files using the given file name as a pattern.
   */
  private void buildTIFFList(String baseTiff) {
    LOGGER.info("Building list of TIFFs");
    String prefix = "";
    if (baseTiff.indexOf(File.separator) != -1) {
      prefix = baseTiff.substring(0, baseTiff.lastIndexOf(File.separator) + 1);
      baseTiff = baseTiff.substring(baseTiff.lastIndexOf(File.separator) + 1);
    }

    String[] blocks = baseTiff.split("_");
    StringBuffer filename = new StringBuffer();
    for (int t=0; t<getSizeT(); t++) {
      for (int c=0; c<getSizeC(); c++) {
        for (int z=0; z<getSizeZ(); z++) {
          // file names are of format:
          // img_<T>_<channel name>_<T>.tif
          filename.append(prefix);
          filename.append(blocks[0]);
          filename.append("_");

          int zeros = blocks[1].length() - String.valueOf(t).length();
          for (int q=0; q<zeros; q++) {
            filename.append("0");
          }
          filename.append(t);
          filename.append("_");

          filename.append(channels[c]);
          filename.append("_");

          zeros = blocks[3].length() - String.valueOf(z).length() - 4;
          for (int q=0; q<zeros; q++) {
            filename.append("0");
          }
          filename.append(z);
          filename.append(".tif");

          tiffs.add(filename.toString());
          filename.delete(0, filename.length());
        }
      }
    }
  }

  /** Parse metadata values from the Acqusition.xml file. */
  private void parseXMLFile() throws IOException {
    String xmlData = DataTools.readFile(xmlFile);
    xmlData = XMLTools.sanitizeXML(xmlData);

    DefaultHandler handler = new MicromanagerHandler();
    XMLTools.parseXML(xmlData, handler);
  }

  // -- Helper classes --

  /** SAX handler for parsing Acqusition.xml. */
  class MicromanagerHandler extends DefaultHandler {
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      if (qName.equals("entry")) {
        String key = attributes.getValue("key");
        String value = attributes.getValue("value");

        addGlobalMeta(key, value);
      }
    }
  }

}
