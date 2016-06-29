/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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
import java.util.List;

import loci.common.DataTools;
import loci.common.Location;
import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.services.MDBService;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffParser;

import ome.xml.model.primitives.PositiveFloat;
import ome.units.quantity.Length;

/**
 * APLReader is the file format reader for Olympus APL files.
 */
public class APLReader extends FormatReader {

  // -- Constants --

  private static final String[] METADATA_SUFFIXES =
    new String[] {"apl", "tnb", "mtb" };

  // -- Fields --

  private String[] tiffFiles;
  private String[] xmlFiles;
  private transient TiffParser[] parser;
  private IFDList[] ifds;
  private List<String> used;

  // -- Constructor --

  /** Constructs a new APL reader. */
  public APLReader() {
    super("Olympus APL", new String[] {"apl", "tnb", "mtb", "tif"});
    domains = new String[] {FormatTools.LM_DOMAIN};
    hasCompanionFiles = true;
    suffixSufficient = false;
    datasetDescription = "One .apl file, one .mtb file, one .tnb file, and " +
      "a directory containing one or more .tif files";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, METADATA_SUFFIXES)) return true;
    if (checkSuffix(name, "tif") && open) {
      Location file = new Location(name).getAbsoluteFile();
      Location parent = file.getParentFile();
      if (parent != null) {
        try {
          parent = parent.getParentFile();
          parent = parent.getParentFile();
        }
        catch (NullPointerException e) {
          return false;
        }
        Location aplFile = new Location(parent, parent.getName() + ".apl");
        return aplFile.exists();
      }
    }
    return false;
  }

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    final List<String> files = new ArrayList<String>();
    files.addAll(used);

    if (getSeries() < xmlFiles.length) {
      Location xmlFile = new Location(xmlFiles[getSeries()]);
      if (xmlFile.exists() && !xmlFile.isDirectory()) {
        files.add(xmlFiles[getSeries()]);
      }
    }
    if (!noPixels && getSeries() < tiffFiles.length &&
      new Location(tiffFiles[getSeries()]).exists())
    {
      files.add(tiffFiles[getSeries()]);
    }
    return files.toArray(new String[files.size()]);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (parser == null) {
      parser = new TiffParser[getSeriesCount()];
    }
    if (parser[getSeries()] == null) {
      parser[getSeries()] = new TiffParser(tiffFiles[getSeries()]);
      parser[getSeries()].setDoCaching(false);
    }

    IFD ifd = ifds[getSeries()].get(no);
    return parser[getSeries()].getSamples(ifd, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      tiffFiles = null;
      xmlFiles = null;
      used = null;
      ifds = null;
      if (parser != null) {
        for (TiffParser p : parser) {
          if (p != null) {
            p.getStream().close();
          }
        }
      }
      parser = null;
    }
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    try {
      return (int) ifds[getSeries()].get(0).getTileWidth();
    }
    catch (FormatException e) {
      LOGGER.debug("Could not retrieve tile width", e);
    }
    return super.getOptimalTileWidth();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    try {
      return (int) ifds[getSeries()].get(0).getTileLength();
    }
    catch (FormatException e) {
      LOGGER.debug("Could not retrieve tile height", e);
    }
    return super.getOptimalTileHeight();
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    LOGGER.debug("Initializing {}", id);

    // find the corresponding .mtb file
    if (!checkSuffix(id, "mtb")) {
      if (checkSuffix(id, METADATA_SUFFIXES)) {
        int separator = id.lastIndexOf(File.separator);
        if (separator < 0) separator = 0;
        int underscore = id.lastIndexOf("_");
        if (underscore < separator || checkSuffix(id, "apl")) {
          underscore = id.lastIndexOf(".");
        }
        String mtbFile = id.substring(0, underscore) + "_d.mtb";
        if (!new Location(mtbFile).exists()) {
          throw new FormatException(".mtb file not found");
        }
        currentId = new Location(mtbFile).getAbsolutePath();
      }
      else {
        Location parent = new Location(id).getAbsoluteFile().getParentFile();
        parent = parent.getParentFile();
        String[] list = parent.list(true);
        for (String f : list) {
          if (checkSuffix(f, "mtb")) {
            currentId = new Location(parent, f).getAbsolutePath();
            break;
          }
        }
        if (!checkSuffix(currentId, "mtb")) {
          throw new FormatException(".mtb file not found");
        }
      }
    }

    String mtb = new Location(currentId).getAbsolutePath();
    LOGGER.debug("Reading .mtb file '{}'", mtb);

    MDBService mdb = null;
    try {
      ServiceFactory factory = new ServiceFactory();
      mdb = factory.getInstance(MDBService.class);
    }
    catch (DependencyException de) {
      throw new FormatException("MDB Tools Java library not found", de);
    }

    String[] columnNames = null;
    List<String[]> rows = null;
    try {
      mdb.initialize(mtb);
      rows = mdb.parseDatabase().get(0);

      columnNames = rows.get(0);
      String[] tmpNames = columnNames;
      columnNames = new String[tmpNames.length - 1];
      System.arraycopy(tmpNames, 1, columnNames, 0, columnNames.length);
    }
    finally {
      mdb.close();
    }

    // add full table to metadata hashtable

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      for (int i=1; i<rows.size(); i++) {
        String[] row = rows.get(i);
        for (int q=0; q<row.length; q++) {
          addGlobalMetaList(columnNames[q], row[q]);
        }
      }
    }

    used = new ArrayList<String>();
    used.add(mtb);
    String tnb = mtb.substring(0, mtb.lastIndexOf("."));
    if (tnb.lastIndexOf("_") > tnb.lastIndexOf(File.separator)) {
      tnb = tnb.substring(0, tnb.lastIndexOf("_"));
    }
    used.add(tnb + "_1.tnb");
    used.add(tnb + ".apl");
    String idPath = new Location(id).getAbsolutePath();
    if (!used.contains(idPath) && checkSuffix(idPath, METADATA_SUFFIXES)) {
      used.add(idPath);
    }

    // calculate indexes to relevant metadata

    int calibrationUnit = DataTools.indexOf(columnNames, "Calibration Unit");
    int colorChannels = DataTools.indexOf(columnNames, "Color Channels");
    int frames = DataTools.indexOf(columnNames, "Frames");
    int calibratedHeight = DataTools.indexOf(columnNames, "Height");
    int calibratedWidth = DataTools.indexOf(columnNames, "Width");
    int path = DataTools.indexOf(columnNames, "Image Path");
    if (path == -1) {
      path = DataTools.indexOf(columnNames, "Path");
    }
    int filename = DataTools.indexOf(columnNames, "File Name");
    int magnification = DataTools.indexOf(columnNames, "Magnification");
    int width = DataTools.indexOf(columnNames, "X-Resolution");
    int height = DataTools.indexOf(columnNames, "Y-Resolution");
    int imageType = DataTools.indexOf(columnNames, "Image Type");
    int imageName = DataTools.indexOf(columnNames, "Image Name");
    int zLayers = DataTools.indexOf(columnNames, "Z-Layers");

    String parentDirectory = mtb.substring(0, mtb.lastIndexOf(File.separator));

    // look for the directory that contains TIFF and XML files

    LOGGER.debug("Searching {} for a directory with TIFFs", parentDirectory);

    Location dir = new Location(parentDirectory);

    String topDirectory = null;

    int index = 2;
    String pathName = rows.get(index++)[path].trim();
    while (pathName.equals("") && index < rows.size()) {
      pathName = rows.get(index++)[path].trim();
    }

    pathName = pathName.replace('\\', File.separatorChar);
    pathName = pathName.replaceAll("/", File.separator);
    String[] dirs = pathName.split(
      File.separatorChar == '\\' ? "\\\\" : File.separator);
    for (int i=dirs.length-1; i>=0; i--) {
      if (dirs[i].indexOf("_DocumentFiles") > 0) {
        Location file = new Location(dir, dirs[i]);
        if (file.exists()) {
          topDirectory = file.getAbsolutePath();
          break;
        }
      }
    }

    if (topDirectory == null) {
      String[] list = dir.list();
      for (String f : list) {
        LOGGER.debug("  '{}'", f);
        Location file = new Location(dir, f);
        if (file.isDirectory() && f.indexOf("_DocumentFiles") > 0) {
          topDirectory = file.getAbsolutePath();
          LOGGER.debug("Found {}", topDirectory);
          break;
        }
      }
    }
    if (topDirectory == null) {
      throw new FormatException("Could not find a directory with TIFF files.");
    }

    final List<Integer> seriesIndexes = new ArrayList<Integer>();

    for (int i=1; i<rows.size(); i++) {
      String file = parseFilename(rows.get(i), filename, path);
      if (file.equals("")) continue;
      file = topDirectory + File.separator + file;
      if (new Location(file).exists() && checkSuffix(file, "tif")) {
        seriesIndexes.add(i);
      }
    }
    int seriesCount = seriesIndexes.size();

    core.clear();
    tiffFiles = new String[seriesCount];
    xmlFiles = new String[seriesCount];
    parser = new TiffParser[seriesCount];
    ifds = new IFDList[seriesCount];

    for (int i=0; i<seriesCount; i++) {
      CoreMetadata ms = new CoreMetadata();
      core.add(ms);

      int secondRow = seriesIndexes.get(i);
      int firstRow = secondRow - 1;
      String[] row2 = rows.get(firstRow);
      String[] row3 = rows.get(secondRow);

      if (frames > -1) {
        ms.sizeT = parseDimension(row3[frames]);
      }
      if (zLayers > -1) {
        ms.sizeZ = parseDimension(row3[zLayers]);
      }
      if (colorChannels > -1) {
        ms.sizeC = parseDimension(row3[colorChannels]);
      }
      else if (imageType > -1) {
        if (row3[imageType] != null && row3[imageType].equals("RGB")) {
          ms.sizeC = 3;
        }
      }
      ms.dimensionOrder = "XYCZT";

      if (ms.sizeZ == 0) ms.sizeZ = 1;
      if (ms.sizeC == 0) ms.sizeC = 1;
      if (ms.sizeT == 0) ms.sizeT = 1;

      xmlFiles[i] = topDirectory + File.separator + parseFilename(row2, filename, path);
      tiffFiles[i] = topDirectory + File.separator + parseFilename(row3, filename, path);

      parser[i] = new TiffParser(tiffFiles[i]);
      parser[i].setDoCaching(false);
      ifds[i] = parser[i].getIFDs();

      for (IFD ifd : ifds[i]) {
        parser[i].fillInIFD(ifd);
      }

      // get core metadata from TIFF file

      IFD ifd = ifds[i].get(0);
      PhotoInterp photo = ifd.getPhotometricInterpretation();
      int samples = ifd.getSamplesPerPixel();

      ms.sizeX = (int) ifd.getImageWidth();
      ms.sizeY = (int) ifd.getImageLength();
      ms.rgb = samples > 1 || photo == PhotoInterp.RGB;
      ms.pixelType = ifd.getPixelType();
      ms.littleEndian = ifd.isLittleEndian();
      ms.indexed = photo == PhotoInterp.RGB_PALETTE &&
        ifd.containsKey(IFD.COLOR_MAP);
      ms.imageCount = ifds[i].size();
      if (ms.sizeZ * ms.sizeT * (ms.rgb ? 1 : ms.sizeC) !=
        ms.imageCount)
      {
        ms.sizeT = ms.imageCount / (ms.rgb ? 1 : ms.sizeC);
        ms.sizeZ = 1;
      }
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    for (int i=0; i<seriesCount; i++) {
      String[] row = rows.get(seriesIndexes.get(i));

      // populate Image data
      MetadataTools.setDefaultCreationDate(store, mtb, i);
      store.setImageName(row[imageName].trim(), i);

      if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
        // populate Dimensions data

        // calculate physical X and Y sizes

        double realWidth = Double.parseDouble(row[calibratedWidth]);
        double realHeight = Double.parseDouble(row[calibratedHeight]);

        String units = row[calibrationUnit];

        CoreMetadata ms = core.get(i);
        double px = realWidth / ms.sizeX;
        double py = realHeight / ms.sizeY;

        Length physicalSizeX = FormatTools.getPhysicalSizeX(px, units);
        Length physicalSizeY = FormatTools.getPhysicalSizeY(py, units);

        if (physicalSizeX != null) {
          store.setPixelsPhysicalSizeX(physicalSizeX, i);
        }
        if (physicalSizeY != null) {
          store.setPixelsPhysicalSizeY(physicalSizeY, i);
        }
      }
    }
    setSeries(0);
  }

  // -- Helper methods --

  /**
   * Parse an integer from the given dimension String.
   *
   * @return the parsed integer, or 1 if parsing failed.
   */
  private int parseDimension(String dim) {
    try {
      return Integer.parseInt(dim);
    }
    catch (NumberFormatException e) {
      return 1;
    }
  }

  private String parseFilename(String[] row, int filenameIndex, int pathIndex) {
    String file = row[filenameIndex].trim();
    if (file != null && checkSuffix(file, "tif")){
      return file;
    }
    String filePath = row[pathIndex].trim();
    filePath = filePath.replace('\\', File.separatorChar);
    filePath = filePath.replaceAll("/", File.separator);
    String[] dirs = filePath.split(File.separatorChar == '\\' ? "\\\\" : File.separator);
    file = dirs[dirs.length-1].trim();
    return file; 
  }
}
