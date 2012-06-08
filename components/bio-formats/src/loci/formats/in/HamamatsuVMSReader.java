/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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

import loci.common.Constants;
import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;

/**
 * HamamatsuVMSReader is the file format reader for Hamamatsu VMS datasets.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/HamamatsuVMSReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/HamamatsuVMSReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class HamamatsuVMSReader extends FormatReader {

  // -- Constants --

  // -- Fields --

  private ArrayList<String> files = new ArrayList<String>();

  private String[][][] tileFiles;

  private TileJPEGReader[] jpeg;

  // -- Constructor --

  /** Constructs a new Hamamatsu VMS reader. */
  public HamamatsuVMSReader() {
    super("Hamamatsu VMS", "vms");
    domains = new String[] {FormatTools.HISTOLOGY_DOMAIN};
    datasetDescription = "One .vms file plus several .jpg files";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    if (noPixels) {
      return new String[] {currentId};
    }

    ArrayList<String> f = new ArrayList<String>();
    f.add(jpeg[getSeries()].getCurrentFile());
    f.addAll(files);
    return f.toArray(new String[f.size()]);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    jpeg[getSeries()].openBytes(no, buf, x, y, w, h);
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      tileFiles = null;
      files.clear();
      if (jpeg != null) {
        for (TileJPEGReader j : jpeg) {
          if (j != null) {
            j.close();
          }
        }
        jpeg = null;
      }
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    IniParser parser = new IniParser();
    IniList layout = parser.parseINI(new BufferedReader(
      new InputStreamReader(new FileInputStream(id), Constants.ENCODING)));
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

    Location dir = new Location(id).getAbsoluteFile().getParentFile();

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

    jpeg = new TileJPEGReader[3];
    core = new CoreMetadata[3];

    for (int i=0; i<core.length; i++) {
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

      jpeg[i] = new TileJPEGReader();
      jpeg[i].setId(file);
      core[i] = jpeg[i].getCoreMetadata()[0];
      core[i].thumbnail = i > 0;
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    String path = new Location(currentId).getAbsoluteFile().getName();

    store.setImageName(path + " full resolution", 0);
    store.setImageName(path + " macro", 1);
    store.setImageName(path + " map", 2);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      if (physicalWidth > 0) {
        store.setPixelsPhysicalSizeX(
          new PositiveFloat(physicalWidth / core[0].sizeX), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeX; got {}",
          physicalWidth / core[0].sizeX);
      }
      if (physicalHeight > 0) {
        store.setPixelsPhysicalSizeY(
          new PositiveFloat(physicalHeight / core[0].sizeY), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeY; got {}",
          physicalHeight / core[0].sizeY);
      }
      if (macroWidth > 0) {
        store.setPixelsPhysicalSizeX(
          new PositiveFloat(macroWidth / core[1].sizeX), 1);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeX; got {}",
          macroWidth / core[1].sizeX);
      }
      if (macroHeight > 0) {
        store.setPixelsPhysicalSizeY(
          new PositiveFloat(macroHeight / core[1].sizeY), 1);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeY; got {}",
          macroHeight / core[1].sizeY);
      }

      String instrumentID = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrumentID, 0);
      store.setImageInstrumentRef(instrumentID, 0);

      String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
      store.setObjectiveID(objectiveID, 0, 0);
      if (magnification > 0) {
        store.setObjectiveNominalMagnification(
          new PositiveInteger(magnification.intValue()), 0, 0);
      }
      else {
        LOGGER.warn("Expected positive value for NominalMagnification; got {}",
          magnification);
      }
      store.setObjectiveSettingsID(objectiveID, 0);
    }
  }

}
