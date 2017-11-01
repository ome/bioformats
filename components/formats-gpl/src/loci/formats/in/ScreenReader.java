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
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;

import loci.common.DataTools;
import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.ClassList;
import loci.formats.CoreMetadata;
import loci.formats.DimensionSwapper;
import loci.formats.FileStitcher;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.Memoizer;
import loci.formats.MetadataTools;
import loci.formats.MissingLibraryException;
import loci.formats.AxisGuesser;
import loci.formats.meta.MetadataStore;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.ome.OMEXMLMetadataImpl;
import loci.formats.services.OMEXMLService;
import loci.formats.services.OMEXMLServiceImpl;

import ome.xml.meta.OMEXMLMetadataRoot;
import ome.xml.model.Image;
import ome.xml.model.enums.NamingConvention;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveInteger;

/**
 * ScreenReader is a generic reader for files in non-HCS formats that have been
 * organized into a screen.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class ScreenReader extends FormatReader {

  // -- Fields --

  private String[] plateMetadataFiles;
  private String[][] files;
  private String[] ordering;
  private int[][] axisTypes;
  private String[][] channelNames;
  private int[][][] fileIndexes;

  private Class<? extends IFormatReader> chosenReader = null;
  private transient IFormatReader reader;
  private ClassList<IFormatReader> validReaders;
  private int fields, wells;
  private HashMap<Integer, Integer> seriesMap = new HashMap<Integer, Integer>();
  private HashMap<Integer, Integer> spotMap = new HashMap<Integer, Integer>();

  private static int[] readAxisTypes(String iniEntry) throws FormatException {
    if (null == iniEntry) {
      return null;
    }
    int size = iniEntry.length();
    int[] ret = new int[size];
    for (int i = 0; i < size; i++) {
      ret[i] = AxisGuesser.getAxisType(String.valueOf(iniEntry.charAt(i)));
      if (ret[i] == AxisGuesser.UNKNOWN_AXIS) {
        throw new FormatException("invalid axis types: " + iniEntry);
      }
    }
    return ret;
  }

  // -- Constructor --

  /** Constructs a new screen reader. */
  public ScreenReader() {
    super("Screen", "screen");
    suffixNecessary = true;
    domains = new String[] {FormatTools.HCS_DOMAIN};

    ClassList<IFormatReader> classes = ImageReader.getDefaultReaderClasses();
    Class<? extends IFormatReader>[] classArray = classes.getClasses();
    validReaders = new ClassList<IFormatReader>(IFormatReader.class);
    for (Class<? extends IFormatReader> c : classArray) {
      if (!c.equals(ScreenReader.class) && !c.equals(OMETiffReader.class)) {
        validReaders.addClass(c);
      }
    }
    hasCompanionFiles = true;
  }

  // -- IFormatReader API methods --

  @Override
  public void reopenFile() throws IOException {
    ClassList<IFormatReader> classes = new ClassList<IFormatReader>(IFormatReader.class);
    classes.addClass(chosenReader);
    reader = new ImageReader(classes);
    reader.setMetadataOptions(new DynamicMetadataOptions(MetadataLevel.MINIMUM));
    // memoize whatever files are opened internally if possible.
    reader = Memoizer.wrap(getMetadataOptions(), reader);
  }

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    return false;
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
    if (reader == null || reader.getCurrentFile() == null) {
      return null;
    }
    return reader.get8BitLookupTable();
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (reader == null || reader.getCurrentFile() == null) {
      return null;
    }
    return reader.get16BitLookupTable();
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int spotIndex = seriesMap.get(getCoreIndex());
    int fileIndex = fileIndexes[spotIndex][no][0];
    int planeIndex = fileIndexes[spotIndex][no][1];
    reader.setId(files[spotIndex][fileIndex]);
    return reader.openBytes(planeIndex, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    int spotIndex = seriesMap.get(getCoreIndex());
    final Set<String> allFiles = new LinkedHashSet<String>();
    try {
      for (String file : files[spotIndex]) {
        reader.close();
        reader.setId(file);
        if (plateMetadataFiles != null) {
          for (String f : plateMetadataFiles) {
            allFiles.add(f);
          }
        }
        String[] readerFiles = reader.getSeriesUsedFiles(noPixels);
        if (readerFiles != null) {
          for (String f : readerFiles) {
            allFiles.add(f);
          }
        }
      }
    }
    catch (Exception e) {
      LOGGER.info("Could not initialize file for spot {}", spotIndex, e);
    }

    return allFiles.toArray(new String[allFiles.size()]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (reader != null) {
      reader.close(fileOnly);
    }
    if (!fileOnly) {
      reader = null;
      plateMetadataFiles = null;
      fields = 0;
      wells = 0;
      seriesMap.clear();
      spotMap.clear();
      files = null;
      ordering = null;
      axisTypes = null;
      channelNames = null;
      fileIndexes = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    Location parent = new Location(id).getAbsoluteFile().getParentFile();

    // ScreenReader only accepts an INI file which defines the plate -> file mappings
    //  * a "Plate" table is required, and defines the dimensions of the plate
    //  * a "MetadataFiles" table is optional, and specifies any extra files to attach
    //  * one uniquely named "Well" table is required for each well defined by the "Plate" table

    IniParser parser = new IniParser();
    String data = DataTools.readFile(id);
    IniList tables = parser.parseINI(new BufferedReader(new StringReader(data)));

    final List<String> metadataFiles = new ArrayList<String>();
    metadataFiles.add(id);
    IniTable metadataTable = tables.getTable("MetadataFiles");
    if (metadataTable != null) {
      String fileCount = metadataTable.get("Count");
      if (fileCount != null) {
        int count = 0;
        try {
          count = Integer.parseInt(fileCount);
        }
        catch (NumberFormatException e) { }
        for (int i=0; i<count; i++) {
          if (metadataTable.containsKey(String.valueOf(i))) {
            String file = metadataTable.get(String.valueOf(i));
            if (file.startsWith(File.separator)) {
              metadataFiles.add(file);
            }
            else {
              metadataFiles.add(new Location(parent, file).getAbsolutePath());
            }
          }
        }
      }
    }

    IniTable plateTable = tables.getTable("Plate");
    if (plateTable == null) {
      throw new FormatException("Missing Plate table");
    }

    // currently only supports the case where we have one file per well and field
    // multiple fields or wells per file is not supported, nor is splitting
    // a field across multiple files (wells are obviously split by field, but not by e.g. channel)

    int maxRow = Integer.parseInt(plateTable.get("Rows"));
    int maxCol = Integer.parseInt(plateTable.get("Columns"));
    fields = Integer.parseInt(plateTable.get("Fields"));

    wells = maxRow * maxCol;
    files = new String[wells * fields][1];
    ordering = new String[wells * fields];
    axisTypes = new int[wells * fields][];
    channelNames = new String[wells * fields][];
    fileIndexes = new int[wells * fields][][];

    for (int well=0; well<wells; well++) {
      IniTable wellTable = tables.getTable("Well " + well);
      if (wellTable == null) {
        throw new FormatException("Missing 'Well " + well + "' table");
      }
      int row = Integer.parseInt(wellTable.get("Row"));
      int col = Integer.parseInt(wellTable.get("Column"));

      int index = (row * maxCol + col) * fields;
      for (int field=0; field<fields; field++, index++) {
        String file = wellTable.get("Field_" + field);
        if (file != null) {
          if (file.startsWith(File.separator)) {
            files[index][0] = file;
          }
          else {
            files[index][0] = new Location(parent, file).getAbsolutePath();
          }
        }
        ordering[index] = wellTable.get("Dimensions");
        if (ordering[index] != null) {
          ordering[index] = "XY" + ordering[index];
        }
        axisTypes[index] = readAxisTypes(wellTable.get("AxisTypes"));
        String channelNamesEntry = wellTable.get("ChannelNames");
        if (null != channelNamesEntry) {
          channelNames[index] = channelNamesEntry.split(",", -1);
        }
      }
    }

    int coreLength = files.length;

    plateMetadataFiles =
      metadataFiles.toArray(new String[metadataFiles.size()]);

    metadataFiles.clear();

    // initialize each of the well files

    OMEXMLMetadata omexmlMeta;
    OMEXMLService service;
    try {
      ServiceFactory factory = new ServiceFactory();
      service = factory.getInstance(OMEXMLService.class);
      omexmlMeta = service.createOMEXMLMetadata();
    }
    catch (DependencyException de) {
      throw new MissingLibraryException(OMEXMLServiceImpl.NO_OME_XML_MSG, de);
    }
    catch (ServiceException se) {
      throw new FormatException(se);
    }

    core.clear();

    String excludeReadersEntry = plateTable.get("ExcludeReaders");
    if (null != excludeReadersEntry) {
      String[] excludeReaders = excludeReadersEntry.split(",", -1);
      for (String r : excludeReaders) {
        try {
          Class<? extends IFormatReader> c =
            Class.forName(r).asSubclass(IFormatReader.class);
          validReaders.removeClass(c);
        } catch (ClassNotFoundException e) {
          LOGGER.warn("Reader {} not found", r);
        }
      }
    }

    ImageReader iReader = new ImageReader(validReaders);
    FileStitcher stitcher = new FileStitcher(iReader, true);
    stitcher.setReaderClassList(validReaders);
    stitcher.setCanChangePattern(false);
    // After setReaderClassList
    reader = new DimensionSwapper(stitcher);
    reader.setMetadataStore(omexmlMeta);
    // Note: by passing this in we allow lower-level readers
    // to change our settings here.
    reader.setMetadataOptions(getMetadataOptions());

    for (int well=0; well<files.length; well++) {
      if (files[well] == null || files[well][0] == null) {
        continue;
      }
      LOGGER.debug("Initializing pattern {} for spot {}", files[well], well);
      reader.close();
      if (null != axisTypes[well]) {
        stitcher.overrideAxisTypes(axisTypes[well]);
      }
      reader.setId(files[well][0]);

      // At this point, we have a concrete reader. Use it
      // for all subsequent setId calls.
      if (chosenReader == null) {
        chosenReader = stitcher.unwrap().getClass();
        LOGGER.info("Using {} for all further calls.",
          chosenReader.getSimpleName());
        ClassList<IFormatReader> chosenReaders =
          new ClassList<IFormatReader>(IFormatReader.class);
        chosenReaders.addClass(chosenReader);
        stitcher.setReaderClassList(chosenReaders);
        // Re-initialize
        reader.setId(files[well][0]);
      }

      if (ordering[well] != null && reader instanceof DimensionSwapper) {
        ((DimensionSwapper) reader).swapDimensions(ordering[well]);
      }
      List<CoreMetadata> wcore = reader.getCoreMetadataList();
      core.add(wcore.get(0));
      if (wcore.size() > 1) {
        LOGGER.warn("Ignoring extra series for well #{}", well);
      }
      seriesMap.put(core.size() - 1, well);
      spotMap.put(well, core.size() - 1);

      files[well] = reader.getUsedFiles();

      fileIndexes[well] = new int[wcore.get(0).imageCount][];
      for (int plane=0; plane<fileIndexes[well].length; plane++) {
        fileIndexes[well][plane] = stitcher.computeIndices(plane);
      }
    }

    OMEXMLMetadataRoot root = (OMEXMLMetadataRoot) omexmlMeta.getRoot();
    Image img = root.getImage(0);
    for (int i=1; i<core.size(); i++) {
      root.addImage(img);
    }
    ((OMEXMLMetadataImpl) omexmlMeta).resolveReferences();
    omexmlMeta.setRoot(root);

    // populate HCS metadata

    MetadataStore store = makeFilterMetadata();
    service.convertMetadata(omexmlMeta, store);
    MetadataTools.populatePixels(store, this);

    String screenID = MetadataTools.createLSID("Screen", 0);
    store.setScreenID(screenID, 0);
    store.setScreenName(plateTable.get("ScreenName"), 0);

    String plateID = MetadataTools.createLSID("Plate", 0);
    store.setPlateID(plateID, 0);
    store.setScreenPlateRef(plateID, 0, 0);

    store.setPlateName(plateTable.get("Name"), 0);
    store.setPlateRows(new PositiveInteger(maxRow), 0);
    store.setPlateColumns(new PositiveInteger(maxCol), 0);
    store.setPlateRowNamingConvention(NamingConvention.LETTER, 0);
    store.setPlateColumnNamingConvention(NamingConvention.NUMBER, 0);

    int realWell = 0;
    for (int row=0; row<maxRow; row++) {
      for (int col=0; col<maxCol; col++) {
        int well = row * maxCol + col;
        String wellID = MetadataTools.createLSID("Well", 0, well);
        store.setWellID(wellID, 0, well);
        store.setWellColumn(new NonNegativeInteger(col), 0, well);
        store.setWellRow(new NonNegativeInteger(row), 0, well);

        int nextWellSample = 0;
        for (int field=0; field<fields; field++) {
          int spotIndex = well * fields + field;
          int seriesIndex = -1;
          if (spotMap.containsKey(spotIndex)) {
            seriesIndex = spotMap.get(spotIndex);
          }

          if (seriesIndex >= 0) {
            String wellSampleID =
              MetadataTools.createLSID("WellSample", 0, well, nextWellSample);
            store.setWellSampleID(wellSampleID, 0, well, nextWellSample);
            String imageID = MetadataTools.createLSID("Image", seriesIndex);
            store.setImageID(imageID, seriesIndex);
            store.setImageName("Well " + ((char) (row + 'A')) + (col + 1) +
              ", Field " + (field + 1), seriesIndex);
            store.setWellSampleImageRef(imageID, 0, well, nextWellSample);
            store.setWellSampleIndex(
              new NonNegativeInteger(seriesIndex), 0, well, nextWellSample);
            if (null != channelNames[spotIndex]) {
              for (int i = 0; i < channelNames[spotIndex].length; i++) {
                store.setChannelName(channelNames[spotIndex][i], seriesIndex, i);
              }
            }
            nextWellSample++;
          }
        }
      }
    }
  }

}
