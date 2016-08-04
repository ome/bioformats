/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

  private static final int MAX_SIZE = 2048;

  // -- Fields --

  private int initializedSeries = -1;
  private int initializedPlane = -1;

  private ArrayList<String> files = new ArrayList<String>();

  private String[][][] tileFiles;
  private String[] jpeg;

  private JPEGTurboService service;
  private HashMap<String, long[]> restartMarkers =
    new HashMap<String, long[]>();

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
    f.add(jpeg[getSeries()]);
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

    String file = jpeg[getCoreIndex()];

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
      if (initializedSeries != getCoreIndex() || initializedPlane != no) {
        service.close();
        if (restartMarkers.containsKey(file)) {
          service.setRestartMarkers(restartMarkers.get(file));
        }
        else {
          service.setRestartMarkers(null);
        }
        // closing the service will close this file
        RandomAccessInputStream s = new RandomAccessInputStream(file);
        service.initialize(s, getSizeX(), getSizeY());
        restartMarkers.put(file, service.getRestartMarkers());

        initializedSeries = getCoreIndex();
        initializedPlane = no;
      }

      service.getTile(buf, x, y, w, h);
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
      jpeg = null;
      restartMarkers.clear();
      initializedSeries = -1;
      initializedPlane = -1;
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
    int nRows = Integer.parseInt(slideInfo.get("NoJpegRows"));
    int nCols = Integer.parseInt(slideInfo.get("NoJpegColumns"));

    String imageFile = slideInfo.get("ImageFile");
    String mapFile = slideInfo.get("MapFile");
    String optimisationFile = slideInfo.get("OptimisationFile");
    String macroFile = slideInfo.get("MacroImage");

    Double physicalWidth = new Double(slideInfo.get("PhysicalWidth"));
    Double physicalHeight = new Double(slideInfo.get("PhysicalHeight"));
    Double magnification = new Double(slideInfo.get("SourceLens"));

    Double macroWidth = new Double(slideInfo.get("PhysicalMacroWidth"));
    Double macroHeight = new Double(slideInfo.get("PhysicalMacroHeight"));

    for (String key : slideInfo.keySet()) {
      addGlobalMeta(key, slideInfo.get(key));
    }

    Location dir = new Location(id).getParentFile();

    tileFiles = new String[nLayers][nRows][nCols];

    for (int layer=0; layer<nLayers; layer++) {
      for (int row=0; row<nRows; row++) {
        for (int col=0; col<nCols; col++) {
          tileFiles[layer][row][col] =
            slideInfo.get("ImageFile(" + col + "," + row + ")");
          if (tileFiles[layer][row][col] != null) {
            files.add(
              new Location(dir, tileFiles[layer][row][col]).getAbsolutePath());
          }
        }
      }
    }

    if (imageFile != null) {
      imageFile = new Location(dir, imageFile).getAbsolutePath();
    }
    if (mapFile != null) {
      mapFile = new Location(dir, mapFile).getAbsolutePath();
    }
    if (optimisationFile != null) {
      optimisationFile = new Location(dir, optimisationFile).getAbsolutePath();
      files.add(optimisationFile);
    }
    if (macroFile != null) {
      macroFile = new Location(dir, macroFile).getAbsolutePath();
    }

    jpeg = new String[3];

    int seriesCount = 3;

    core.clear();
    for (int i=0; i<seriesCount; i++) {
      String file = null;
      switch (i) {
        case 0:
          file = imageFile;
          break;
        case 1:
          file = macroFile;
          break;
        case 2:
          file = mapFile;
          break;
      }

      jpeg[i] = file;
      JPEGTileDecoder decoder = new JPEGTileDecoder();
      RandomAccessInputStream s = new RandomAccessInputStream(file);
      int[] dims = decoder.preprocess(s);
      s.close();

      CoreMetadata m = new CoreMetadata();
      m.sizeX = dims[0];
      m.sizeY = dims[1];
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
