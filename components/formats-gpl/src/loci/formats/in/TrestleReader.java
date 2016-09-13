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

import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.codec.BitWriter;
import loci.formats.meta.MetadataStore;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.tiff.IFD;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffParser;

/**
 * TrestleReader is the file format reader for Trestle slide datasets.
 */
public class TrestleReader extends BaseTiffReader {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(TrestleReader.class);

  // -- Fields --

  private ArrayList<String> files;
  private String roiFile;
  private String roiDrawFile;
  private int[] overlaps;

  // -- Constructor --

  /** Constructs a new Trestle reader. */
  public TrestleReader() {
    super("Trestle", new String[] {"tif"});
    domains = new String[] {FormatTools.HISTOLOGY_DOMAIN};
    suffixSufficient = false;
    suffixNecessary = false;
    hasCompanionFiles = true;
    datasetDescription = "One .tif file plus several other similarly-named " +
      "files (e.g. *.FocalPlane-*, .sld, .slx, .ROI)";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    if (super.isThisType(name, open)) return true;

    if (!checkSuffix(name, "tif") && open) {
      Location current = new Location(name).getAbsoluteFile();
      Location parent = current.getParentFile();

      String tiff = current.getName();
      int index = tiff.lastIndexOf(".");
      if (index >= 0) {
        tiff = tiff.substring(0, index);
      }
      tiff += ".tif";

      Location tiffFile = new Location(parent, tiff);
      return tiffFile.exists() && isThisType(tiffFile.getAbsolutePath(), open);
    }
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser parser = new TiffParser(stream);
    IFD ifd = parser.getFirstIFD();
    if (ifd == null) return false;
    String copyright = ifd.getIFDTextValue(IFD.COPYRIGHT);
    if (copyright == null) return false;
    return copyright.indexOf("Trestle Corp.") >= 0;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    if (noPixels) {
      return files.toArray(new String[files.size()]);
    }
    String[] allFiles = new String[files.size() + 1];
    files.toArray(allFiles);
    allFiles[allFiles.length - 1] = currentId;
    return allFiles;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (core.size() == 1) {
      return super.openBytes(no, buf, x, y, w, h);
    }
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    tiffParser.getSamples(ifds.get(getCoreIndex()), buf, x, y, w, h,
      overlaps[getCoreIndex() * 2], overlaps[getCoreIndex() * 2 + 1]);
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      files = null;
    }
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    try {
      return (int) ifds.get(getCoreIndex()).getTileWidth();
    }
    catch (FormatException e) {
      LOGGER.debug("", e);
    }
    return super.getOptimalTileWidth();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    try {
      return (int) ifds.get(getCoreIndex()).getTileLength();
    }
    catch (FormatException e) {
      LOGGER.debug("", e);
    }
    return super.getOptimalTileHeight();
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    if (!checkSuffix(id, "tif")) {
      Location parent = new Location(id).getAbsoluteFile().getParentFile();
      String[] list = parent.list(true);
      for (String f : list) {
        if (checkSuffix(f, "tif")) {
          String path = new Location(parent, f).getAbsolutePath();
          if (isThisType(path)) {
            id = path;
            break;
          }
        }
      }
    }
    super.initFile(id);
  }

  // -- Internal BaseTiffReader API methods --

  /* @see loci.formats.in.BaseTiffReader#initStandardMetadata() */
  @Override
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    ifds = tiffParser.getIFDs();
    for (IFD ifd : ifds) {
      tiffParser.fillInIFD(ifd);
    }

    String comment = ifds.get(0).getComment();
    String[] values = comment.split(";");
    for (String v : values) {
      int eq = v.indexOf('=');
      if (eq < 0) continue;
      String key = v.substring(0, eq).trim();
      String value = v.substring(eq + 1).trim();
      addGlobalMeta(key, value);

      if (key.equals("OverlapsXY")) {
        String[] overlapValues = value.split(" ");
        overlaps = new int[ifds.size() * 2];
        for (int i=0; i<overlapValues.length; i++) {
          overlaps[i] = Integer.parseInt(overlapValues[i]);
        }
      }
    }

    int seriesCount = ifds.size();
    core.clear();
    for (int i=0; i<seriesCount; i++) {
      CoreMetadata c = new CoreMetadata();

      if (i == 0 && !hasFlattenedResolutions()) {
        c.resolutionCount = seriesCount;
      }
      core.add(c);
    }

    // repopulate core metadata

    for (int s=0; s<core.size(); s++) {
      CoreMetadata ms = core.get(s);
      IFD ifd = ifds.get(s);
      PhotoInterp p = ifd.getPhotometricInterpretation();
      int samples = ifd.getSamplesPerPixel();
      ms.rgb = samples > 1 || p == PhotoInterp.RGB;

      long numTileRows = ifd.getTilesPerColumn() - 1;
      long numTileCols = ifd.getTilesPerRow() - 1;

      int overlapX = overlaps[s * 2];
      int overlapY = overlaps[s * 2 + 1];

      ms.sizeX = (int) (ifd.getImageWidth() - (numTileCols * overlapX));
      ms.sizeY = (int) (ifd.getImageLength() - (numTileRows * overlapY));
      ms.sizeZ = 1;
      ms.sizeT = 1;
      ms.sizeC = ms.rgb ? samples : 1;
      ms.littleEndian = ifd.isLittleEndian();
      ms.indexed = p == PhotoInterp.RGB_PALETTE &&
        (get8BitLookupTable() != null || get16BitLookupTable() != null);
      ms.imageCount = 1;
      ms.pixelType = ifd.getPixelType();
      ms.metadataComplete = true;
      ms.interleaved = false;
      ms.falseColor = false;
      ms.dimensionOrder = "XYCZT";
      ms.thumbnail = s > 0;
    }

    // look for all of the other associated metadata files

    files = new ArrayList<String>();

    Location baseFile = new Location(currentId).getAbsoluteFile();
    Location parent = baseFile.getParentFile();
    String name = baseFile.getName();
    if (name.indexOf('.') >= 0) {
      name = name.substring(0, name.indexOf('.') + 1);
    }

    roiFile = new Location(parent, name + "ROI").getAbsolutePath();
    roiDrawFile = new Location(parent, name + "ROI-draw").getAbsolutePath();

    String[] list = parent.list(true);
    for (String f : list) {
      if (!f.equals(baseFile.getName())) {
        files.add(new Location(parent, f).getAbsolutePath());
      }
    }
  }

  /* @see loci.formats.BaseTiffReader#initMetadataStore() */
  @Override
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();

    MetadataStore store = makeFilterMetadata();

    for (int i=0; i<getSeriesCount(); i++) {
      store.setImageName("Series " + (i + 1), i);
    }

    MetadataLevel level = getMetadataOptions().getMetadataLevel();
    if (level != MetadataLevel.MINIMUM) {
      // do not store the mask data in OME-XML MetadataStores
      // doing so would guarantee invalid OME-XML, since the required BinData
      // will not be stored with the mask dimensions
      if (level != MetadataLevel.NO_OVERLAYS &&
        !(getMetadataStore() instanceof OMEXMLMetadata))
      {
        try {
          parseROIs(store);
        }
        catch (IOException e) {
          LOGGER.debug("Could not parse ROIs", e);
        }
      }
    }
  }

  // -- Helper methods --

  private void parseROIs(MetadataStore store)
    throws FormatException, IOException
  {
    String roiID = MetadataTools.createLSID("ROI", 0, 0);
    String maskID = MetadataTools.createLSID("Shape", 0, 0);

    store.setROIID(roiID, 0);
    store.setMaskID(maskID, 0, 0);

    String positionData = DataTools.readFile(roiDrawFile);
    String[] coordinates = positionData.split("[ ,]");

    double x1 = Double.parseDouble(coordinates[1]);
    double y1 = Double.parseDouble(coordinates[2]);
    double x2 = Double.parseDouble(coordinates[3]);
    double y2 = Double.parseDouble(coordinates[5]);

    store.setMaskX(x1, 0, 0);
    store.setMaskY(y1, 0, 0);
    store.setMaskWidth(x2 - x1, 0, 0);
    store.setMaskHeight(y2 - y1, 0, 0);

    store.setImageROIRef(roiID, 0, 0);

    ImageReader roiReader = new ImageReader();
    roiReader.setId(roiFile);
    byte[] roiPixels = roiReader.openBytes(0);
    roiReader.close();

    BitWriter bits = new BitWriter(roiPixels.length / 8);
    for (int i=0; i<roiPixels.length; i++) {
      bits.write(roiPixels[i] == 0 ? 0 : 1, 1);
    }
    store.setMaskBinData(bits.toByteArray(), 0, 0);
  }

}
