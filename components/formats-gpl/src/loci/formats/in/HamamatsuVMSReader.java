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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import loci.common.Constants;
import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.Region;
import loci.common.services.ServiceException;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.JPEGTileDecoder;
import loci.formats.meta.MetadataStore;
import loci.formats.services.JPEGTurboService;
import loci.formats.services.JPEGTurboServiceImpl;

import ome.xml.model.primitives.PositiveFloat;
import ome.units.quantity.Length;

/**
 * HamamatsuVMSReader is the file format reader for Hamamatsu VMS datasets.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class HamamatsuVMSReader extends FormatReader {

  // -- Constants --

  private static final int MAX_JPEG_SIZE = 61440;
  private static final int MAX_SIZE = 2048;

  // -- Fields --

  private int initializedSeries = -1;
  private int initializedPlane = -1;
  private String initializedFile = null;

  private ArrayList<String> files = new ArrayList<String>();

  private String[][][] tileFiles;

  private JPEGTurboService service;
  private HashMap<String, long[]> restartMarkers =
    new HashMap<String, long[]>();

  private String macroFile;
  private String mapFile;
  private int nRows;
  private int nCols;

  // -- Constructor --

  /** Constructs a new Hamamatsu VMS reader. */
  public HamamatsuVMSReader() {
    super("Hamamatsu VMS", "vms");
    domains = new String[] {FormatTools.HISTOLOGY_DOMAIN};
    datasetDescription = "One .vms file plus several .jpg files";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    return 1024;
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return 1024;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    if (noPixels) {
      return new String[] {currentId};
    }

    ArrayList<String> f = new ArrayList<String>();
    f.add(currentId);
    f.addAll(files);
    return f.toArray(new String[f.size()]);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int startCol = x / MAX_JPEG_SIZE;
    int startRow = y / MAX_JPEG_SIZE;

    String file = null;
    switch (getCoreIndex()) {
      case 0:
        file = tileFiles[no][startRow][startCol];
        break;
      case 1:
        file = macroFile;
        break;
      case 2:
        file = mapFile;
        break;
    }

    if (getSizeX() <= MAX_SIZE || getSizeY() <= MAX_SIZE) {
      JPEGReader reader = new JPEGReader();
      reader.setId(file);
      reader.openBytes(0, buf, x, y, w, h);
      reader.close();
      return buf;
    }

    if (service == null) {
      service = new JPEGTurboServiceImpl();
    }

    try {
      Region image = new Region(x, y, w, h);
      for (int row=startRow; row<nRows; row++) {
        for (int col=startCol; col<nCols; col++) {
          Region tile = new Region(col * MAX_JPEG_SIZE, row * MAX_JPEG_SIZE,
            col == nCols - 1 ? getSizeX() % MAX_JPEG_SIZE : MAX_JPEG_SIZE,
            row == nRows - 1 ? getSizeY() % MAX_JPEG_SIZE : MAX_JPEG_SIZE);
          if (!tile.intersects(image)) {
            continue;
          }
          file = tileFiles[no][row][col];
          if (initializedSeries != getCoreIndex() || initializedPlane != no ||
            !file.equals(initializedFile))
          {
            service.close();
            if (restartMarkers.containsKey(file)) {
              service.setRestartMarkers(restartMarkers.get(file));
            }
            else {
              service.setRestartMarkers(null);
            }
            // closing the service will close this file
            RandomAccessInputStream s = new RandomAccessInputStream(file);
            service.initialize(s, tile.width, tile.height);
            restartMarkers.put(file, service.getRestartMarkers());

            initializedSeries = getCoreIndex();
            initializedPlane = no;
            initializedFile = file;
          }

          Region intersection = tile.intersection(image);

          int tileX = intersection.x % MAX_JPEG_SIZE;
          int tileY = intersection.y % MAX_JPEG_SIZE;

          int rowLen = intersection.width * getRGBChannelCount();
          byte[] b = new byte[rowLen * intersection.height];

          service.getTile(b, tileX, tileY, intersection.width, intersection.height);

          for (int tileRow=0; tileRow<intersection.height; tileRow++) {
            int src = tileRow * rowLen;
            int dest = (((intersection.y + tileRow - y) * w) + (intersection.x - x)) * getRGBChannelCount();
            System.arraycopy(b, src, buf, dest, rowLen);
          }
        }
      }
    }
    catch (ServiceException e) {
      throw new FormatException(e);
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      tileFiles = null;
      files.clear();
      if (service != null) {
        service.close();
        service = null;
      }
      restartMarkers.clear();
      initializedSeries = -1;
      initializedPlane = -1;
      nRows = 0;
      nCols = 0;
      initializedFile = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    in = new RandomAccessInputStream(id);
    IniParser parser = new IniParser();
    IniList layout = parser.parseINI(new BufferedReader(
      new InputStreamReader(in, Constants.ENCODING)));
    IniTable slideInfo = layout.getTable("Virtual Microscope Specimen");

    int nLayers = Integer.parseInt(slideInfo.get("NoLayers"));
    nRows = Integer.parseInt(slideInfo.get("NoJpegRows"));
    nCols = Integer.parseInt(slideInfo.get("NoJpegColumns"));

    String imageFile = slideInfo.get("ImageFile");
    mapFile = slideInfo.get("MapFile");
    String optimisationFile = slideInfo.get("OptimisationFile");
    macroFile = slideInfo.get("MacroImage");

    Double physicalWidth = new Double(slideInfo.get("PhysicalWidth"));
    Double physicalHeight = new Double(slideInfo.get("PhysicalHeight"));
    Double magnification = new Double(slideInfo.get("SourceLens"));

    Double macroWidth = new Double(slideInfo.get("PhysicalMacroWidth"));
    Double macroHeight = new Double(slideInfo.get("PhysicalMacroHeight"));

    for (String key : slideInfo.keySet()) {
      addGlobalMeta(key, slideInfo.get(key));
    }

    Location dir = new Location(id).getAbsoluteFile().getParentFile();

    if (imageFile != null) {
      imageFile = new Location(dir, imageFile).getAbsolutePath();
      files.add(imageFile);
    }

    tileFiles = new String[nLayers][nRows][nCols];
    tileFiles[0][0][0] = imageFile;

    for (int layer=0; layer<nLayers; layer++) {
      for (int row=0; row<nRows; row++) {
        for (int col=0; col<nCols; col++) {
          String f = slideInfo.get("ImageFile(" + col + "," + row + ")");
          if (f != null) {
            f = new Location(dir, f).getAbsolutePath();
            tileFiles[layer][row][col] = f;
            files.add(f);
          }
        }
      }
    }

    if (mapFile != null) {
      mapFile = new Location(dir, mapFile).getAbsolutePath();
      files.add(mapFile);
    }
    if (optimisationFile != null) {
      optimisationFile = new Location(dir, optimisationFile).getAbsolutePath();
      files.add(optimisationFile);
    }
    if (macroFile != null) {
      macroFile = new Location(dir, macroFile).getAbsolutePath();
      files.add(macroFile);
    }

    int seriesCount = 3;

    core.clear();
    for (int i=0; i<seriesCount; i++) {
      String file = null;
      switch (i) {
        case 0:
          file = tileFiles[0][nRows-1][nCols-1];
          break;
        case 1:
          file = macroFile;
          break;
        case 2:
          file = mapFile;
          break;
      }

      JPEGTileDecoder decoder = new JPEGTileDecoder();
      RandomAccessInputStream s = new RandomAccessInputStream(file);
      int[] dims = decoder.preprocess(s);
      s.close();

      CoreMetadata m = new CoreMetadata();
      if (i == 0) {
        m.sizeX = (MAX_JPEG_SIZE * (nCols - 1)) + dims[0];
        m.sizeY = (MAX_JPEG_SIZE * (nRows - 1)) + dims[1];
      }
      else {
        m.sizeX = dims[0];
        m.sizeY = dims[1];
      }
      m.sizeZ = 1;
      m.sizeC = 3;
      m.sizeT = 1;
      m.rgb = true;
      m.imageCount = 1;
      m.dimensionOrder = "XYCZT";
      m.pixelType = FormatTools.UINT8;
      m.interleaved = m.sizeX > MAX_SIZE && m.sizeY > MAX_SIZE;
      m.thumbnail = i > 0;
      core.add(m);
    }

    CoreMetadata ms0 = core.get(0);

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    String path = new Location(currentId).getAbsoluteFile().getName();

    store.setImageName(path + " full resolution", 0);
    store.setImageName(path + " macro", 1);
    store.setImageName(path + " map", 2);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      Length sizeX =
        FormatTools.getPhysicalSizeX(physicalWidth / ms0.sizeX);
      Length sizeY =
        FormatTools.getPhysicalSizeY(physicalHeight / ms0.sizeY);
      Length macroSizeX =
        FormatTools.getPhysicalSizeX(macroWidth / core.get(1).sizeX);
      Length macroSizeY =
        FormatTools.getPhysicalSizeY(macroHeight / core.get(1).sizeY);

      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }
      if (macroSizeX != null) {
        store.setPixelsPhysicalSizeX(macroSizeX, 1);
      }
      if (macroSizeY != null) {
        store.setPixelsPhysicalSizeY(macroSizeY, 1);
      }

      String instrumentID = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrumentID, 0);
      store.setImageInstrumentRef(instrumentID, 0);

      String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
      store.setObjectiveID(objectiveID, 0, 0);
      store.setObjectiveNominalMagnification(magnification, 0, 0);
      store.setObjectiveSettingsID(objectiveID, 0);
    }
  }

}
