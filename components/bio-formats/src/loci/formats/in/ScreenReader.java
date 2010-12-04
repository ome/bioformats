//
// ScreenReader.java
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
import java.util.Vector;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import ome.xml.model.enums.NamingConvention;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveInteger;

/**
 * ScreenReader is a generic reader for files in non-HCS formats that have been
 * organized into a screen.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/ScreenReader.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/ScreenReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class ScreenReader extends FormatReader {

  // -- Fields --

  private ImageReader[][] readers;
  private boolean[][][] plateMaps;

  // -- Constructor --

  /** Constructs a new screen reader. */
  public ScreenReader() {
    super("Screen", "");
    suffixSufficient = false;
    domains = new String[] {FormatTools.HCS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String filename, boolean open) {
    if (!open) return super.isThisType(filename, open); // no file system access
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (readers == null || readers[0][0].getCurrentFile() == null) {
      return null;
    }
    return readers[0][0].get8BitLookupTable();
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (readers == null || readers[0][0].getCurrentFile() == null) {
      return null;
    }
    return readers[0][0].get16BitLookupTable();
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int[] spwIndexes = getSPWIndexes(getSeries());
    return readers[spwIndexes[0]][spwIndexes[1]].openBytes(no, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    int[] spwIndexes = getSPWIndexes(getSeries());
    return readers[spwIndexes[0]][spwIndexes[1]].getSeriesUsedFiles(noPixels);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (readers != null) {
      for (ImageReader[] wells : readers) {
        for (ImageReader well : wells) {
          well.close(fileOnly);
        }
      }
    }
    if (!fileOnly) {
      readers = null;
      plateMaps = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    Location file = new Location(id).getAbsoluteFile();
    Location screen = null;
    if (isValidWellName(file.getAbsolutePath())) {
      screen = file.getParentFile();
      screen = screen.getParentFile();
    }
    else throw new FormatException(id + " is not a valid well name.");

    // build the list of plate directories
    Vector<String> tmpPlates = new Vector<String>();
    String[] screenList = screen.list(true);
    for (String f : screenList) {
      if (isValidPlateName(f)) {
        tmpPlates.add(new Location(screen, f).getAbsolutePath());
      }
    }
    String[] plates = tmpPlates.toArray(new String[tmpPlates.size()]);
    Arrays.sort(plates);

    readers = new ImageReader[plates.length][];
    String[][] files = new String[plates.length][];
    plateMaps = new boolean[plates.length][][];

    // build the list of well files for each plate
    Vector<String> tmpWells = new Vector<String>();
    int coreLength = 0;
    for (int plate=0; plate<plates.length; plate++) {
      String[] plateList = new Location(plates[plate]).list(true);
      Vector<String> uniqueRows = new Vector<String>();
      Vector<String> uniqueCols = new Vector<String>();
      for (String well : plateList) {
        if (isValidWellName(well)) {
          tmpWells.add(new Location(plates[plate], well).getAbsolutePath());
          String row = getRow(well);
          String col = getColumn(well);

          if (!uniqueRows.contains(row)) {
            uniqueRows.add(row);
          }
          if (!uniqueCols.contains(col)) {
            uniqueCols.add(col);
          }
        }
      }
      files[plate] = tmpWells.toArray(new String[tmpWells.size()]);
      Arrays.sort(files[plate]);
      readers[plate] = new ImageReader[files[plate].length];
      coreLength += files[plate].length;

      plateMaps[plate] = new boolean[uniqueRows.size()][uniqueCols.size()];

      tmpWells.clear();
    }

    // initialize each of the well files

    core = new CoreMetadata[coreLength];
    int nextCore = 0;
    for (int plate=0; plate<files.length; plate++) {
      for (int well=0; well<files[plate].length; well++) {
        readers[plate][well] = new ImageReader();
        readers[plate][well].setId(files[plate][well]);
        core[nextCore++] = readers[plate][well].getCoreMetadata()[0];

        String row = getRow(files[plate][well]);
        String col = getColumn(files[plate][well]);
        int rowIndex = (int) (row.charAt(0) - 'A');
        int colIndex = Integer.parseInt(col) - 1;

        plateMaps[plate][rowIndex][colIndex] = true;
      }
    }

    // populate HCS metadata

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    String screenID = MetadataTools.createLSID("Screen", 0);
    store.setScreenID(screenID, 0);
    store.setScreenName(screen.getName(), 0);

    for (int plate=0; plate<files.length; plate++) {
      String plateID = MetadataTools.createLSID("Plate", plate);
      store.setPlateID(plateID, plate);
      store.setScreenPlateRef(plateID, 0, plate);
      store.setPlateScreenRef(screenID, plate, 0);

      store.setPlateName(new Location(plates[plate]).getName(), plate);
      store.setPlateRows(new PositiveInteger(plateMaps[plate].length), plate);
      store.setPlateColumns(new PositiveInteger(plateMaps[plate][0].length),
        plate);
      store.setPlateRowNamingConvention(NamingConvention.LETTER, plate);
      store.setPlateColumnNamingConvention(NamingConvention.NUMBER, plate);

      for (int well=0; well<files[plate].length; well++) {
        int seriesIndex = getSeriesIndex(plate, well);

        int[] rowAndColumn = getRowAndColumn(plate, well);
        String wellID = MetadataTools.createLSID("Well", plate, well);
        store.setWellID(wellID, plate, well);
        store.setWellColumn(
          new NonNegativeInteger(rowAndColumn[1]), plate, well);
        store.setWellRow(new NonNegativeInteger(rowAndColumn[0]), plate, well);

        String imageID = MetadataTools.createLSID("Image", seriesIndex);
        String wellSampleID =
          MetadataTools.createLSID("WellSample", plate, well, 0);
        store.setWellSampleID(wellSampleID, plate, well, 0);
        store.setWellSampleImageRef(imageID, plate, well, 0);
        store.setWellSampleIndex(
          new NonNegativeInteger(seriesIndex), plate, well, 0);
      }
    }
  }

  // -- Helper methods --

  private boolean isValidWellName(String filename) {
    String row = getRow(filename);
    String col = getColumn(filename);

    try {
      Integer.parseInt(col);
      return true;
    }
    catch (NumberFormatException e) { }

    return false;
  }

  private boolean isValidPlateName(String filename) {
    return true;
  }

  private int getSeriesIndex(int plate, int well) {
    int seriesIndex = 0;
    for (int p=0; p<plate; p++) {
      for (int row=0; row<plateMaps[p].length; row++) {
        for (int col=0; col<plateMaps[p][row].length; col++) {
          if (plateMaps[p][row][col]) seriesIndex++;
        }
      }
    }
    int validWells = -1;
    for (int row=0; row<plateMaps[plate].length; row++) {
      for (int col=0; col<plateMaps[plate][row].length; col++) {
         if (plateMaps[plate][row][col]) {
            validWells++;
            seriesIndex++;

            if (validWells == well) {
              return seriesIndex - 1;
            }
         }
      }
    }
    return -1;
  }

  private int[] getSPWIndexes(int seriesIndex) {
    int validWells = 0;
    for (int plate=0; plate<plateMaps.length; plate++) {
      int wellIndex = 0;
      for (int row=0; row<plateMaps[plate].length; row++) {
        for (int col=0; col<plateMaps[plate][row].length; col++) {
          if (plateMaps[plate][row][col]) {
            validWells++;
            wellIndex++;

            if (validWells == seriesIndex + 1) {
              return new int[] {plate, wellIndex - 1};
            }
          }
        }
      }
    }

    return new int[] {-1, -1};
  }

  private int[] getRowAndColumn(int plate, int well) {
    int validWells = 0;
    for (int row=0; row<plateMaps[plate].length; row++) {
      for (int col=0; col<plateMaps[plate][row].length; col++) {
        if (plateMaps[plate][row][col]) {
          validWells++;

          if (validWells == well + 1) {
            return new int[] {row, col};
          }
        }
      }
    }
    return new int[] {-1, -1};
  }

  private String getRow(String well) {
    String wellName = well.substring(well.lastIndexOf(File.separator) + 1);
    return wellName.substring(0, 1).toUpperCase();
  }

  private String getColumn(String well) {
    String wellName = well.substring(well.lastIndexOf(File.separator) + 1);
    return wellName.substring(1);
  }

}
