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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.ClassList;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.MissingLibraryException;
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

  private ImageReader[] readers;
  private boolean[][] plateMaps;
  private ClassList<IFormatReader> validReaders;

  // -- Constructor --

  /** Constructs a new screen reader. */
  public ScreenReader() {
    super("Screen", "");
    suffixSufficient = false;
    domains = new String[] {FormatTools.HCS_DOMAIN};

    ClassList<IFormatReader> classes = ImageReader.getDefaultReaderClasses();
    Class<? extends IFormatReader>[] classArray = classes.getClasses();
    validReaders = new ClassList<IFormatReader>(IFormatReader.class);
    for (Class<? extends IFormatReader> c : classArray) {
      if (!c.equals(ScreenReader.class)) {
        validReaders.addClass(c);
      }
    }
    hasCompanionFiles = true;
  }

  // -- IFormatReader API methods --

  @Override
  public void reopenFile() throws IOException {
    super.reopenFile();
    for (ImageReader reader : readers) {
      reader.getReader().reopenFile();
    }
  }

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String filename, boolean open) {
    if (!open) return super.isThisType(filename, open); // no file system access

    Location file = new Location(filename).getAbsoluteFile();
    String parent = file.getParent();
    boolean validNames =
      isValidWellName(file.getAbsolutePath()) && isValidPlateName(parent);

    ImageReader r = new ImageReader(validReaders);
    boolean validFormat = r.isThisType(filename);
    boolean singleFiles = false;
    try {
      singleFiles = r.isSingleFile(filename);
    }
    catch (FormatException e) { }
    catch (IOException e) { }

    return validNames && validFormat && singleFiles;
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

  /* @see loci.formats.IFormatReader#get8BitLookupTable(int) */
  @Override
  public byte[][] get8BitLookupTable(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (readers == null || readers[0].getCurrentFile() == null) {
      return null;
    }
    return readers[0].get8BitLookupTable(0);
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable(int) */
  @Override
  public short[][] get16BitLookupTable(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (readers == null || readers[0].getCurrentFile() == null) {
      return null;
    }
    return readers[0].get16BitLookupTable(0);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int[] spwIndexes = getSPWIndexes(getSeries());
    int well = spwIndexes[0];
    int field = spwIndexes[1];
    readers[well].setSeries(field);
    return readers[well].openBytes(no, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    int[] spwIndexes = getSPWIndexes(getSeries());
    int well = spwIndexes[0];

    final List<String> files = new ArrayList<String>();
    if (plateMetadataFiles != null) {
      for (String f : plateMetadataFiles) {
        files.add(f);
      }
    }
    String[] readerFiles = readers[well].getSeriesUsedFiles(noPixels);
    if (readerFiles != null) {
      for (String f : readerFiles) {
        files.add(f);
      }
    }
    return files.toArray(new String[files.size()]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (readers != null) {
      for (ImageReader well : readers) {
        well.close(fileOnly);
      }
    }
    if (!fileOnly) {
      readers = null;
      plateMaps = null;
      plateMetadataFiles = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    Location file = new Location(id).getAbsoluteFile();
    Location plate = null;
    Location screen = null;
    if (isValidWellName(file.getAbsolutePath())) {
      plate = file.getParentFile();
      screen = plate.getParentFile();
    }
    else throw new FormatException(id + " is not a valid well name.");

    // build the list of plate directories
    final List<String> metadataFiles = new ArrayList<String>();

    Comparator<String> c = new Comparator<String>() {
      @Override
      public int compare(String s1, String s2) {
        int row1 = (int) (getRow(s1).charAt(0) - 'A');
        int row2 = (int) (getRow(s2).charAt(0) - 'A');

        if (row1 != row2) {
          return row1 - row2;
        }

        int col1 = Integer.parseInt(getColumn(s1)) - 1;
        int col2 = Integer.parseInt(getColumn(s2)) - 1;
        return col1 - col2;
      }
    };

    // build the list of well files for each plate
    final List<String> tmpWells = new ArrayList<String>();

    String[] plateList = plate.list(true);
    int maxRow = 0, maxCol = 0;
    for (String well : plateList) {
      Location wellFile = new Location(plate, well);
      if (isValidWellName(wellFile.getAbsolutePath())) {
        String row = getRow(well);
        String col = getColumn(well);

        boolean canAdd = true;
        for (String tmpWell : tmpWells) {
          String tmpRow = getRow(tmpWell);
          String tmpCol = getColumn(tmpWell);
          if (tmpRow.equals(row) && tmpCol.equals(col)) {
            canAdd = false;
            break;
          }
        }
        if (!canAdd) continue;

        tmpWells.add(wellFile.getAbsolutePath());

        if ((row.charAt(0) - 'A') > maxRow) {
          maxRow = row.charAt(0) - 'A';
        }
        if (Integer.parseInt(col) - 1 > maxCol) {
          maxCol = Integer.parseInt(col) - 1;
        }
      }
      else if (!wellFile.isDirectory()) {
        metadataFiles.add(wellFile.getAbsolutePath());
      }
    }
    String[] files = tmpWells.toArray(new String[tmpWells.size()]);

    Arrays.sort(files, c);
    readers = new ImageReader[files.length];
    int coreLength = files.length;

    plateMaps = new boolean[maxRow + 1][maxCol + 1];
    plateMetadataFiles =
      metadataFiles.toArray(new String[metadataFiles.size()]);

    tmpWells.clear();
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

    int lastCount = 0;
    int[] seriesCounts = new int[files.length];
    core.clear();

    for (int well=0; well<files.length; well++) {
      readers[well] = new ImageReader(validReaders);
      readers[well].setMetadataStore(omexmlMeta);
      readers[well].setId(files[well]);
      List<CoreMetadata> wcore = readers[well].getCoreMetadataList();
      for (CoreMetadata cw : wcore) {
        core.add(cw);
      }
      seriesCounts[well] = readers[well].getSeriesCount();
      lastCount = seriesCounts[well];

      String row = getRow(files[well]);
      String col = getColumn(files[well]);
      int rowIndex = (int) (row.charAt(0) - 'A');
      int colIndex = Integer.parseInt(col) - 1;

      plateMaps[rowIndex][colIndex] = true;
    }

    OMEXMLMetadataRoot root = (OMEXMLMetadataRoot) omexmlMeta.getRoot();
    Image img = root.getImage(0);
    for (int i=lastCount; i<core.size(); i++) {
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
    store.setScreenName(screen.getName(), 0);

    String plateID = MetadataTools.createLSID("Plate", 0);
    store.setPlateID(plateID, 0);
    store.setScreenPlateRef(plateID, 0, 0);

    store.setPlateName(plate.getName(), 0);
    store.setPlateRows(new PositiveInteger(plateMaps.length), 0);
    store.setPlateColumns(new PositiveInteger(plateMaps[0].length), 0);
    store.setPlateRowNamingConvention(NamingConvention.LETTER, 0);
    store.setPlateColumnNamingConvention(NamingConvention.NUMBER, 0);

    int realWell = 0;
    for (int row=0; row<plateMaps.length; row++) {
      for (int col=0; col<plateMaps[row].length; col++) {
        int well = row * plateMaps[row].length + col;
        String wellID = MetadataTools.createLSID("Well", 0, well);
        store.setWellID(wellID, 0, well);
        store.setWellColumn(new NonNegativeInteger(col), 0, well);
        store.setWellRow(new NonNegativeInteger(row), 0, well);

        if (plateMaps[row][col]) {
          for (int field=0; field<seriesCounts[realWell]; field++) {
            int seriesIndex = getSeriesIndex(realWell, field);
            String imageID = MetadataTools.createLSID("Image", seriesIndex);
            store.setImageID(imageID, seriesIndex);
            store.setImageName("Well " + ((char) (row + 'A')) + (col + 1) +
              ", Field " + (field + 1), seriesIndex);
            String wellSampleID =
              MetadataTools.createLSID("WellSample", 0, well, field);
            store.setWellSampleID(wellSampleID, 0, well, field);
            store.setWellSampleImageRef(imageID, 0, well, field);
            store.setWellSampleIndex(
              new NonNegativeInteger(seriesIndex), 0, well, field);
          }
          realWell++;
        }
      }
    }
  }

  // -- Helper methods --

  private boolean isValidWellName(String filename) {
    if (new Location(filename).getAbsoluteFile().isDirectory()) return false;
    String row = getRow(filename);
    String col = getColumn(filename);

    try {
      Integer.parseInt(col);
      return col.length() <= 2 && Character.isLetter(row.charAt(0));
    }
    catch (NumberFormatException e) { }

    return false;
  }

  private boolean isValidPlateName(String filename) {
    return new Location(filename).isDirectory();
  }

  private int getSeriesIndex(int well, int field) {
    int fieldCount = readers[well].getSeriesCount();
    int seriesIndex = 0;
    int validWells = -1;
    for (int row=0; row<plateMaps.length; row++) {
      for (int col=0; col<plateMaps[row].length; col++) {
         if (plateMaps[row][col]) {
            validWells++;

            if (validWells == well) {
              return seriesIndex + field;
            }
            seriesIndex += readers[validWells].getSeriesCount();
         }
      }
    }
    return -1;
  }

  private int[] getSPWIndexes(int seriesIndex) {
    int s = 0;
    int wellIndex = 0;
    for (int row=0; row<plateMaps.length; row++) {
      for (int col=0; col<plateMaps[row].length; col++) {
        if (plateMaps[row][col]) {
          for (int i=0; i<readers[wellIndex].getSeriesCount(); i++) {
            s++;

            if (s == seriesIndex + 1) {
              return new int[] {wellIndex, i};
            }
          }
          wellIndex++;
        }
      }
    }

    return new int[] {-1, -1};
  }

  private int[] getRowAndColumn(int well) {
    int validWells = 0;
    for (int row=0; row<plateMaps.length; row++) {
      for (int col=0; col<plateMaps[row].length; col++) {
        if (plateMaps[row][col]) {
          validWells++;

          if (validWells == well + 1) {
            return new int[] {row, col};
          }
        }
      }
    }
    return new int[] {-1, -1};
  }

  private boolean hasValidWells(Location plate) {
    if (!plate.isDirectory()) return false;
    String[] wells = plate.list(true);
    for (String well : wells) {
      if (isValidWellName(new Location(plate, well).getAbsolutePath())) {
        return true;
      }
    }
    return false;
  }

  private String getRow(String well) {
    String wellName = well.substring(well.lastIndexOf(File.separator) + 1);
    char firstChar = Character.toUpperCase(wellName.charAt(0));
    while (wellName.indexOf("_") > 0 && (firstChar < 'A' || firstChar > 'P')) {
      wellName = wellName.substring(wellName.indexOf("_") + 1);
      firstChar = Character.toUpperCase(wellName.charAt(0));
    }
    return wellName.substring(0, 1).toUpperCase();
  }

  private String getColumn(String well) {
    String wellName = well.substring(well.lastIndexOf(File.separator) + 1);
    char firstChar = Character.toUpperCase(wellName.charAt(0));
    while (wellName.indexOf("_") > 0 && (firstChar < 'A' || firstChar > 'P')) {
      wellName = wellName.substring(wellName.indexOf("_") + 1);
      firstChar = Character.toUpperCase(wellName.charAt(0));
    }
    int end = wellName.lastIndexOf("_");
    if (end < 0) end = wellName.lastIndexOf(".");
    if (end < 0) end = wellName.length();
    if (end <= 1) return null;
    return wellName.substring(1, end);
  }

}
