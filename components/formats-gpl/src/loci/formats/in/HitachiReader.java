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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.ClassList;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import ome.units.quantity.Length;
import ome.units.UNITS;

import ome.xml.model.primitives.Timestamp;

/**
 * HitachiReader is the file format reader for S-4800 files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class HitachiReader extends FormatReader {

  // -- Constants --

  private static final String MAGIC = "[SemImageFile]";
  private static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";

  // -- Fields --

  private ImageReader helperReader;

  // -- Constructor --

  /** Constructs a new Hitachi reader. */
  public HitachiReader() {
    super("Hitachi", "txt");
    suffixSufficient = false;
    domains = new String[] {FormatTools.SEM_DOMAIN};
    hasCompanionFiles = true;
    datasetDescription =
      "One .txt file plus one similarly-named .tif, .bmp, or .jpg file";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    if (!open) {
      return false;
    }

    String base = name;
    if (base.indexOf('.') >= 0) {
      base = base.substring(0, base.lastIndexOf("."));
    }

    if (checkSuffix(name, "txt")) {
      Location bmp = new Location(base + ".bmp");
      Location jpg = new Location(base + ".jpg");
      Location tif = new Location(base + ".tif");

      if (!bmp.exists() && !jpg.exists() && !tif.exists()) {
        return false;
      }

      return super.isThisType(name, open);
    }

    String textFile = base + ".txt";
    return new Location(textFile).exists() && isThisType(textFile, open);
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = MAGIC.length();
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return (stream.readString(blockLen)).indexOf(MAGIC) >= 0;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    helperReader.openBytes(no, buf, x, y, w, h);
    return buf;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    if (noPixels) {
      return new String[] {currentId};
    }

    return new String[] {currentId, helperReader.getCurrentFile()};
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      if (helperReader != null) {
        helperReader.close();
      }
      helperReader = null;
    }
  }

  /* @see loci.formats.IFormatReader#reopenFile() */
  @Override
  public void reopenFile() throws IOException {
    super.reopenFile();
    helperReader.reopenFile();
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    if (!checkSuffix(id, "txt")) {
      String base = id;
      if (base.indexOf('.') >= 0) {
        base = base.substring(0, base.lastIndexOf("."));
      }

      id = base + ".txt";
      initFile(id);
      return;
    }

    super.initFile(id);

    String data = DataTools.readFile(id);

    IniParser parser = new IniParser();
    parser.setBackslashContinuesLine(false);
    IniList ini = parser.parseINI(new BufferedReader(new StringReader(data)));

    IniTable image = ini.getTable("SemImageFile");

    if (image == null) {
      throw new FormatException("Could not find 'SemImageFile' table.");
    }

    for (String key : image.keySet()) {
      if (!IniTable.HEADER_KEY.equals(key)) {
        addGlobalMeta(key, image.get(key));
      }
    }

    String imageName = image.get("SampleName");
    String pixelsFile = image.get("ImageName");
    String date = image.get("Date");
    String time = image.get("Time");

    Location parent = new Location(id).getAbsoluteFile().getParentFile();
    pixelsFile = new Location(parent, pixelsFile).getAbsolutePath();

    ClassList<IFormatReader> classes = ImageReader.getDefaultReaderClasses();
    Class<? extends IFormatReader>[] classArray = classes.getClasses();
    ClassList<IFormatReader> newClasses =
      new ClassList<IFormatReader>(IFormatReader.class);
    for (Class<? extends IFormatReader> c : classArray) {
      if (!c.equals(HitachiReader.class)) {
        newClasses.addClass(c);
      }
    }

    helperReader = new ImageReader(newClasses);
    helperReader.setId(pixelsFile);

    core = new ArrayList<CoreMetadata>(helperReader.getCoreMetadataList());

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this,
      getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM);

    store.setImageName(imageName, 0);

    date = DateTools.formatDate(date + " " + time, DATE_FORMAT);
    if (date != null) {
      store.setImageAcquisitionDate(new Timestamp(date), 0);
    }

    populateOMEMetadata(image, store);
  }

  // -- Helper methods --

  private void populateOMEMetadata(IniTable image, MetadataStore store) {
    if (getMetadataOptions().getMetadataLevel() == MetadataLevel.MINIMUM) {
      return;
    }

    String modelNumber = image.get("InstructName");
    String serialNumber = image.get("SerialNumber");
    Double pixelSize = new Double(image.get("PixelSize"));

    String workingDistance = image.get("WorkingDistance");

    final Double stagePosXn = Double.valueOf(image.get("StagePositionX"));
    final Double stagePosYn = Double.valueOf(image.get("StagePositionY"));
    final Double stagePosZn = Double.valueOf(image.get("StagePositionZ"));

    final Length stagePosXl = new Length(stagePosXn, UNITS.REFERENCEFRAME);
    final Length stagePosYl = new Length(stagePosYn, UNITS.REFERENCEFRAME);
    final Length stagePosZl = new Length(stagePosZn, UNITS.REFERENCEFRAME);

    Length sizeX = FormatTools.getPhysicalSizeX(pixelSize, UNITS.NANOMETER);
    Length sizeY = FormatTools.getPhysicalSizeY(pixelSize, UNITS.NANOMETER);
    if (sizeX != null) {
      store.setPixelsPhysicalSizeX(sizeX, 0);
    }
    if (sizeY != null) {
      store.setPixelsPhysicalSizeY(sizeY, 0);
    }

    store.setPlanePositionX(stagePosXl, 0, 0);
    store.setPlanePositionY(stagePosYl, 0, 0);
    store.setPlanePositionZ(stagePosZl, 0, 0);

    String instrument = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrument, 0);
    store.setImageInstrumentRef(instrument, 0);

    if (modelNumber != null) {
      store.setMicroscopeModel(modelNumber, 0);
    }
    if (serialNumber != null) {
      store.setMicroscopeSerialNumber(serialNumber, 0);
    }

    if (workingDistance != null) {
      int end = workingDistance.indexOf(' ');
      if (end < 0) end = workingDistance.length();

      workingDistance = workingDistance.substring(0, end);

      String objective = MetadataTools.createLSID("Objective", 0, 0);
      store.setObjectiveID(objective, 0, 0);
      store.setObjectiveSettingsID(objective, 0);
      store.setObjectiveWorkingDistance(new Length(new Double(workingDistance), UNITS.MICROMETER), 0, 0);
    }
  }
}
