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
import java.util.List;
import java.util.Map;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.units.quantity.Length;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

/**
 * L2DReader is the file format reader for Li-Cor L2D datasets.
 */
public class L2DReader extends FormatReader {

  // -- Constants --

  public static final String DATE_FORMAT = "yyyy, m, d";
  private static final String LICOR_MAGIC_STRING = "LI-COR LI2D";

  // -- Fields --

  /** List of constituent TIFF files. */
  private String[][] tiffs;

  /** List of all metadata files in the dataset. */
  private List<String>[] metadataFiles;

  private MinimalTiffReader reader;
  private int[] tileWidth, tileHeight;

  // -- Constructor --

  /** Construct a new L2D reader. */
  public L2DReader() {
    super("Li-Cor L2D", new String[] {"l2d", "scn", "tif"});
    domains = new String[] {FormatTools.GEL_DOMAIN};
    hasCompanionFiles = true;
    suffixSufficient = false;
    datasetDescription = "One .l2d file with one or more directories " +
      "containing .tif/.tiff files";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, "l2d") || checkSuffix(name, "scn")) {
      return super.isThisType(name, open);
    }
    if (!open) return false;

    Location location = new Location(name);
    if (!location.exists()) {
      return false;
    }
    Location parent = location.getAbsoluteFile().getParentFile();

    String scanName = location.getName();
    if (scanName.indexOf('_') >= 0) {
      scanName = scanName.substring(0, scanName.lastIndexOf("_"));
    }

    boolean hasScan = new Location(parent, scanName + ".scn").exists();

    return hasScan && new Location(parent, scanName).exists();
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = (int) Math.min(512, stream.length());
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    String check = stream.readString(blockLen);
    return check.indexOf(LICOR_MAGIC_STRING) >= 0;
  }

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    reader.setId(tiffs[getSeries()][no]);
    return reader.openBytes(0, buf, x, y, w, h);
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
    final List<String> files = new ArrayList<String>();
    files.add(currentId);
    if (metadataFiles != null && getSeries() < metadataFiles.length) {
      files.addAll(metadataFiles[getSeries()]);
    }
    if (!noPixels) {
      for (String tiff : tiffs[getSeries()]) {
        files.add(tiff);
      }
    }
    return files.toArray(new String[files.size()]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (reader != null) reader.close(fileOnly);
    if (!fileOnly) {
      tiffs = null;
      reader = null;
      metadataFiles = null;
      tileWidth = null;
      tileHeight = null;
    }
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    return tileWidth[getSeries()];
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return tileHeight[getSeries()];
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    // NB: This format cannot be imported using omebf.
    // See Trac ticket #266 for details.

    if (!checkSuffix(id, "l2d") && isGroupFiles()) {
      // find the corresponding .l2d file
      Location parent = new Location(id).getAbsoluteFile().getParentFile();
      parent = parent.getParentFile();
      String[] list = parent.list();
      for (String file : list) {
        if (checkSuffix(file, "l2d")) {
          initFile(new Location(parent, file).getAbsolutePath());
          return;
        }
      }
      throw new FormatException("Could not find .l2d file");
    }
    else if (!isGroupFiles()) {
      super.initFile(id);

      tiffs = new String[][] {{id}};

      TiffReader r = new TiffReader();
      r.setMetadataStore(getMetadataStore());
      r.setId(id);
      core = new ArrayList<CoreMetadata>(r.getCoreMetadataList());
      metadataStore = r.getMetadataStore();

      final Map<String, Object> globalMetadata = r.getGlobalMetadata();
      for (final Map.Entry<String, Object> entry : globalMetadata.entrySet()) {
        addGlobalMeta(entry.getKey(), entry.getValue());
      }
      r.close();
      reader = new MinimalTiffReader();
      return;
    }

    super.initFile(id);

    String[] scans = getScanNames();
    Location parent = new Location(id).getAbsoluteFile().getParentFile();

    // remove scan names that do not correspond to existing directories

    final List<String> validScans = new ArrayList<String>();
    for (String s : scans) {
      Location scanDir = new Location(parent, s);
      if (scanDir.exists() && scanDir.isDirectory()) validScans.add(s);
    }
    scans = validScans.toArray(new String[validScans.size()]);

    // read metadata from each scan

    tiffs = new String[scans.length][];
    metadataFiles = new List[scans.length];

    core = new ArrayList<CoreMetadata>(scans.length);

    String[] comments = new String[scans.length];
    String[] wavelengths = new String[scans.length];
    String[] dates = new String[scans.length];
    String model = null;

    tileWidth = new int[scans.length];
    tileHeight = new int[scans.length];

    core.clear();
    for (int i=0; i<scans.length; i++) {
      CoreMetadata ms = new CoreMetadata();
      core.add(ms);
      setSeries(i);
      metadataFiles[i] = new ArrayList<String>();
      String scanName = scans[i] + ".scn";
      Location scanDir = new Location(parent, scans[i]);

      // read .scn file from each scan

      String scanPath = new Location(scanDir, scanName).getAbsolutePath();
      addDirectory(scanDir.getAbsolutePath(), i);
      String scanData = DataTools.readFile(scanPath);
      String[] lines = scanData.split("\n");
      for (String line : lines) {
        if (!line.startsWith("#")) {
          String key = line.substring(0, line.indexOf('='));
          String value = line.substring(line.indexOf('=') + 1);
          addSeriesMeta(key, value);

          if (key.equals("ExperimentNames")) {
            // TODO : parse experiment metadata - this is typically a list of
            //        overlay shapes, or analysis data
          }
          else if (key.equals("ImageNames")) {
            tiffs[i] = value.split(",");
            for (int t=0; t<tiffs[i].length; t++) {
              tiffs[i][t] =
                new Location(scanDir, tiffs[i][t].trim()).getAbsolutePath();
            }
          }
          else if (key.equals("Comments")) {
            comments[i] = value;
          }
          else if (key.equals("ScanDate")) {
            dates[i] = value;
          }
          else if (key.equals("ScannerName")) {
            model = value;
          }
          else if (key.equals("ScanChannels")) {
            wavelengths[i] = value;
          }
        }
      }
    }
    setSeries(0);

    reader = new MinimalTiffReader();

    MetadataStore store = makeFilterMetadata();

    for (int i=0; i<getSeriesCount(); i++) {
      CoreMetadata ms = core.get(i);
      ms.imageCount = tiffs[i].length;
      ms.sizeC = tiffs[i].length;
      ms.sizeT = 1;
      ms.sizeZ = 1;
      ms.dimensionOrder = "XYCZT";

      reader.setId(tiffs[i][0]);
      ms.sizeX = reader.getSizeX();
      ms.sizeY = reader.getSizeY();
      ms.sizeC *= reader.getSizeC();
      ms.rgb = reader.isRGB();
      ms.indexed = reader.isIndexed();
      ms.littleEndian = reader.isLittleEndian();
      ms.pixelType = reader.getPixelType();
      tileWidth[i] = reader.getOptimalTileWidth();
      tileHeight[i] = reader.getOptimalTileHeight();
    }

    MetadataTools.populatePixels(store, this);

    for (int i=0; i<getSeriesCount(); i++) {
      store.setImageName(scans[i], i);
      if (dates[i] != null) {
        dates[i] = DateTools.formatDate(dates[i], DATE_FORMAT);
        if (dates[i] != null) {
          store.setImageAcquisitionDate(new Timestamp(dates[i]), i);
        }
      }
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      String instrumentID = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrumentID, 0);

      for (int i=0; i<getSeriesCount(); i++) {
        store.setImageInstrumentRef(instrumentID, i);
        store.setImageDescription(comments[i], i);

        if (wavelengths[i] != null) {
          String[] waves = wavelengths[i].split("[, ]");
          if (waves.length < getEffectiveSizeC()) {
            LOGGER.debug("Expected {} wavelengths; got {} wavelengths.",
              getEffectiveSizeC(), waves.length);
          }
          for (int q=0; q<waves.length; q++) {
            String laser = MetadataTools.createLSID("LightSource", 0, q);
            store.setLaserID(laser, 0, q);
            Double wave = new Double(waves[q].trim());
            Length wavelength = FormatTools.getWavelength(wave);
            if (wavelength != null) {
              store.setLaserWavelength(wavelength, 0, q);
            }
            store.setLaserType(getLaserType("Other"), 0, q);
            store.setLaserLaserMedium(getLaserMedium("Other"), 0, q);
            store.setChannelLightSourceSettingsID(laser, i, q);
          }
        }
      }

      store.setMicroscopeModel(model, 0);
      store.setMicroscopeType(getMicroscopeType("Other"), 0);
    }
  }

  // -- Helper methods --

  /**
   * Recursively add all of the files in the given directory to the
   * used file list.
   */
  private void addDirectory(String path, int series) {
    Location dir = new Location(path);
    String[] files = dir.list();
    if (files == null) return;
    for (String f : files) {
      Location file = new Location(path, f);
      if (file.isDirectory()) {
        addDirectory(file.getAbsolutePath(), series);
      }
      else if (checkSuffix(f, "scn")) {
        metadataFiles[series].add(file.getAbsolutePath());
      }
    }
  }

  /** Return a list of scan names. */
  private String[] getScanNames() throws IOException {
    String[] scans = null;

    String data = DataTools.readFile(currentId);
    String[] lines = data.split("\n");
    for (String line : lines) {
      if (!line.startsWith("#")) {
        String key = line.substring(0, line.indexOf('=')).trim();
        String value = line.substring(line.indexOf('=') + 1).trim();
        addGlobalMeta(key, value);

        if (key.equals("ScanNames")) {
          scans = value.split(",");
          for (int i=0; i<scans.length; i++) {
            scans[i] = scans[i].trim();
          }
        }
      }
    }
    return scans;
  }

}
