//
// MIASReader.java
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
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.Region;
import loci.formats.CoreMetadata;
import loci.formats.FilePattern;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.TiffTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * MIASReader is the file format reader for Maia Scientific MIAS-2 datasets.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/MIASReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/MIASReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class MIASReader extends FormatReader {

  // -- Fields --

  /** TIFF files - indexed by plate, well, and file. */
  private String[][][] tiffs;

  /** Delegate reader. */
  private MinimalTiffReader reader;

  /** Path to file containing analysis results for all plates. */
  private String resultFile = null;

  /** Other files that contain analysis results. */
  private Vector<String> analysisFiles = null;

  private int[][] wellNumber;

  private int tileRows, tileCols;

  // -- Constructor --

  /** Constructs a new MIAS reader. */
  public MIASReader() {
    super("MIAS", new String[] {"tif", "tiff"});
    suffixSufficient = false;
    blockCheckLen = 1048576;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String filename, boolean open) {
    Location baseFile = new Location(filename).getAbsoluteFile();
    Location wellDir = baseFile.getParentFile();
    Location experiment = wellDir.getParentFile().getParentFile();

    return wellDir != null && experiment != null &&
      wellDir.getName().startsWith("Well") && super.isThisType(filename, open);
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    if (!FormatTools.validStream(stream, blockCheckLen, false)) return false;

    Hashtable ifd = TiffTools.getFirstIFD(stream);
    if (ifd == null) return false;

    Object s = TiffTools.getIFDValue(ifd, TiffTools.SOFTWARE);
    if (s == null) return false;
    String software = null;
    if (s instanceof String[]) software = ((String[]) s)[0];
    else software = s.toString();

    return software.startsWith("eaZYX");
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return reader == null ? null : reader.get8BitLookupTable();
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return reader == null ? null : reader.get16BitLookupTable();
  }

  /* @see loci.formats.IFormatReader#openThumbBytes(int) */
  public byte[] openThumbBytes(int no)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);

    // TODO : thumbnails take a long time to read

    int well = getWellNumber(getSeries());
    int plate = getPlateNumber(getSeries());

    reader.setId(tiffs[well][plate][no * tileRows * tileCols]);
    return reader.openThumbBytes(0);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int well = getWellNumber(getSeries());
    int plate = getPlateNumber(getSeries());

    if (tileRows == 1 && tileCols == 1) {
      reader.setId(tiffs[plate][well][no]);
      reader.openBytes(0, buf, x, y, w, h);
      return buf;
    }

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int outputRowLen = w * bpp;

    int tileWidth = getSizeX() / tileCols;
    int tileHeight = getSizeY() / tileRows;

    Region image = new Region(x, y, w, h);
    int outputRow = 0, outputCol = 0;
    Region intersection = null;

    byte[] tileBuf = null;

    for (int row=0; row<tileRows; row++) {
      for (int col=0; col<tileCols; col++) {
        Region tile =
          new Region(col * tileWidth, row * tileHeight, tileWidth, tileHeight);
        if (!tile.intersects(image)) continue;

        intersection = tile.intersection(image);
        intersection.x %= tileWidth;
        intersection.y %= tileHeight;

        reader.setId(tiffs[plate][well][(no*tileRows + row) * tileCols + col]);
        tileBuf = reader.openBytes(0, intersection.x, intersection.y,
          intersection.width, intersection.height);

        int rowLen = tileBuf.length / intersection.height;

        // copy tile into output image
        int outputOffset = outputRow * outputRowLen + outputCol;
        for (int trow=0; trow<intersection.height; trow++) {
          System.arraycopy(tileBuf, trow * rowLen, buf, outputOffset, rowLen);
          outputOffset += outputRowLen;
        }

        outputCol += rowLen;
      }
      if (intersection != null) {
        outputRow += intersection.height;
        outputCol = 0;
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#getUsedFiles(boolean) */
  public String[] getUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    Vector<String> files = new Vector<String>();
    if (!noPixels) {
      for (String[][] plates : tiffs) {
        for (String[] wells : plates) {
          for (String file : wells) {
            files.add(file);
          }
        }
      }
    }
    if (resultFile != null) files.add(resultFile);
    for (String file : analysisFiles) {
      if (!file.endsWith(".tif") || !noPixels) {
        files.add(file);
      }
    }
    return files.toArray(new String[0]);
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (reader != null) reader.close(fileOnly);
    if (!fileOnly) {
      reader = null;
      tiffs = null;
      tileRows = tileCols = 0;
      resultFile = null;
      analysisFiles = null;
      wellNumber = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("MIASReader.initFile(" + id + ")");
    super.initFile(id);

    // TODO : initFile currently accepts a constituent TIFF file.
    // Consider allowing the top level experiment directory to be passed in

    reader = new MinimalTiffReader();
    analysisFiles = new Vector<String>();

    // MIAS is a high content screening format which supports multiple plates,
    // wells and fields.
    // Most of the metadata comes from the directory hierarchy, as very little
    // metadata is present in the actual files.
    //
    // The directory hierarchy is:
    //
    // <experiment name>                        top level experiment directory
    //   Batchresults                           analysis results for experiment
    //   <plate number>_<plate barcode>         one directory for each plate
    //     results                              analysis results for plate
    //     Well<xxxx>                           one directory for each well
    //       mode<x>_z<xxx>_t<xxx>_im<x>_<x>.tif
    //
    // Each TIFF file contains a single grayscale plane.  The "mode" block
    // refers to the channel number; the "z" and "t" blocks refer to the
    // Z section and timepoint, respectively.  The "im<x>_<x>" block gives
    // the row and column coordinates of the image within a mosaic.
    //
    // We are initially given one of these TIFF files; from there, we need
    // to find the top level experiment directory and work our way down to
    // determine how many plates and wells are present.

    status("Building list of TIFF files");

    Location baseFile = new Location(id).getAbsoluteFile();
    Location experiment =
      baseFile.getParentFile().getParentFile().getParentFile();

    String experimentName = experiment.getName();

    Vector<String> plateDirs = new Vector<String>();

    String[] directories = experiment.list();
    Arrays.sort(directories);
    for (String dir : directories) {
      if (dir.equals("Batchresults")) {
        Location f = new Location(experiment.getAbsolutePath(), dir);
        String[] results = f.list();
        for (String result : results) {
          Location file = new Location(f.getAbsolutePath(), result);
          analysisFiles.add(file.getAbsolutePath());
          if (result.startsWith("NEO_Results")) {
            resultFile = file.getAbsolutePath();
          }
        }
      }
      else plateDirs.add(dir);
    }

    String[] plateDirectories = plateDirs.toArray(new String[0]);

    tiffs = new String[plateDirectories.length][][];

    int[][] zCount = new int[plateDirectories.length][];
    int[][] cCount = new int[plateDirectories.length][];
    int[][] tCount = new int[plateDirectories.length][];
    String[][] order = new String[plateDirectories.length][];
    wellNumber = new int[plateDirectories.length][];

    for (int i=0; i<plateDirectories.length; i++) {
      String plate = plateDirectories[i];
      Location plateDir = new Location(experiment.getAbsolutePath(), plate);
      String[] list = plateDir.list();
      Arrays.sort(list);
      Vector<String> wellDirectories = new Vector<String>();
      for (String dir : list) {
        Location f = new Location(plateDir.getAbsolutePath(), dir);
        if (f.getName().startsWith("Well")) {
          wellDirectories.add(f.getAbsolutePath());
        }
        else if (f.getName().equals("results")) {
          String[] resultsList = f.list();
          for (String result : resultsList) {
            // exclude proprietary program state files
            if (!result.endsWith(".sav") && !result.endsWith(".dsv")) {
              Location r = new Location(f.getAbsolutePath(), result);
              analysisFiles.add(r.getAbsolutePath());
            }
          }
        }
      }
      tiffs[i] = new String[wellDirectories.size()][];
      zCount[i] = new int[wellDirectories.size()];
      cCount[i] = new int[wellDirectories.size()];
      tCount[i] = new int[wellDirectories.size()];
      order[i] = new String[wellDirectories.size()];
      wellNumber[i] = new int[wellDirectories.size()];

      for (int j=0; j<wellDirectories.size(); j++) {
        Location well = new Location(wellDirectories.get(j));

        String wellName = well.getName().replaceAll("Well", "");
        wellNumber[i][j] = Integer.parseInt(wellName) - 1;

        String wellPath = well.getAbsolutePath();
        String[] tiffFiles = well.list();
        FilePattern fp = new FilePattern(tiffFiles[0], wellPath);
        String[] blocks = fp.getPrefixes();
        BigInteger[] firstNumber = fp.getFirst();
        BigInteger[] lastNumber = fp.getLast();
        BigInteger[] step = fp.getStep();

        order[i][j] = "";

        for (int block=0; block<blocks.length; block++) {
          blocks[block] = blocks[block].toLowerCase();
          blocks[block] =
            blocks[block].substring(blocks[block].lastIndexOf("_") + 1);
          if (blocks[block].equals("z")) {
            BigInteger tmp = lastNumber[block].subtract(firstNumber[block]);
            tmp = tmp.add(BigInteger.ONE);
            tmp = tmp.divide(step[block]);
            zCount[i][j] = tmp.intValue();
            order[i][j] += "Z";
          }
          else if (blocks[block].equals("t")) {
            BigInteger tmp = lastNumber[block].subtract(firstNumber[block]);
            tmp = tmp.add(BigInteger.ONE);
            tmp = tmp.divide(step[block]);
            tCount[i][j] = tmp.intValue();
            order[i][j] += "T";
          }
          else if (blocks[block].equals("mode")) {
            BigInteger tmp = lastNumber[block].subtract(firstNumber[block]);
            tmp = tmp.add(BigInteger.ONE);
            tmp = tmp.divide(step[block]);
            cCount[i][j] = tmp.intValue();
            order[i][j] += "C";
          }
          else if (blocks[block].equals("im")) {
            BigInteger tmp = lastNumber[block].subtract(firstNumber[block]);
            tmp = tmp.add(BigInteger.ONE);
            tmp = tmp.divide(step[block]);
            tileRows = tmp.intValue();
          }
          else if (blocks[block].equals("")) {
            BigInteger tmp = lastNumber[block].subtract(firstNumber[block]);
            tmp = tmp.add(BigInteger.ONE);
            tmp = tmp.divide(step[block]);
            tileCols = tmp.intValue();
          }
          else {
            throw new FormatException("Unsupported block '" + blocks[block]);
          }
        }

        order[i][j] += "YX";
        order[i][j] = new StringBuffer(order[i][j]).reverse().toString();

        Arrays.sort(tiffFiles);
        tiffs[i][j] = new String[tiffFiles.length];
        for (int k=0; k<tiffFiles.length; k++) {
          tiffs[i][j][k] =
            new Location(wellPath, tiffFiles[k]).getAbsolutePath();
        }
      }
    }

    // Populate core metadata

    status("Populating core metadata");

    int nSeries = 0;
    for (int i=0; i<tiffs.length; i++) {
      nSeries += tiffs[i].length;
    }

    core = new CoreMetadata[nSeries];
    for (int i=0; i<core.length; i++) {
      core[i] = new CoreMetadata();

      int plate = getPlateNumber(i);
      int well = getWellNumber(i);

      core[i].sizeZ = zCount[plate][well];
      core[i].sizeC = cCount[plate][well];
      core[i].sizeT = tCount[plate][well];

      if (core[i].sizeZ == 0) core[i].sizeZ = 1;
      if (core[i].sizeC == 0) core[i].sizeC = 1;
      if (core[i].sizeT == 0) core[i].sizeT = 1;

      reader.setId(tiffs[plate][well][0]);
      core[i].sizeX = reader.getSizeX() * tileCols;
      core[i].sizeY = reader.getSizeY() * tileRows;
      core[i].pixelType = reader.getPixelType();
      core[i].sizeC *= reader.getSizeC();
      core[i].rgb = reader.isRGB();
      core[i].littleEndian = reader.isLittleEndian();
      core[i].interleaved = reader.isInterleaved();
      core[i].indexed = reader.isIndexed();
      core[i].falseColor = reader.isFalseColor();
      core[i].dimensionOrder = order[plate][well];

      if (core[i].dimensionOrder.indexOf("Z") == -1) {
        core[i].dimensionOrder += "Z";
      }
      if (core[i].dimensionOrder.indexOf("C") == -1) {
        core[i].dimensionOrder += "C";
      }
      if (core[i].dimensionOrder.indexOf("T") == -1) {
        core[i].dimensionOrder += "T";
      }

      core[i].imageCount = core[i].sizeZ * core[i].sizeT * cCount[plate][well];
    }

    // Populate metadata hashtable

    status("Populating metadata hashtable");

    int wellCols = 1;

    if (resultFile != null) {
      RandomAccessInputStream s = new RandomAccessInputStream(resultFile);

      String colNames = null;
      Vector<String> rows = new Vector<String>();

      boolean doKeyValue = true;
      int nStarLines = 0;

      String analysisResults = s.readString((int) s.length());
      String[] lines = analysisResults.split("\n");

      for (String line : lines) {
        line = line.trim();
        if (line.length() == 0) continue;
        if (line.startsWith("******") && line.endsWith("******")) nStarLines++;

        if (doKeyValue) {
          String[] n = line.split("\t");
          if (n[0].endsWith(":")) n[0] = n[0].substring(0, n[0].length() - 1);
          if (n.length >= 2) addGlobalMeta(n[0], n[1]);
        }
        else {
          if (colNames == null) colNames = line;
          else rows.add(line);
        }
        if (nStarLines == 2) doKeyValue = false;
      }

      String[] cols = colNames.split("\t");

      for (String row : rows) {
        String[] d = row.split("\t");
        for (int col=3; col<cols.length; col++) {
          String key = "Plate " + d[0] + ", Well " + d[2] + " " + cols[col];
          addGlobalMeta(key, d[col]);

          if (cols[col].equals("AreaCode")) {
            String wellID = d[col];
            wellID = wellID.replaceAll("\\D", "");
            wellCols = Integer.parseInt(wellID);
          }
        }
      }
    }

    // Populate MetadataStore

    status("Populating MetadataStore");

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);

    store.setExperimentID("Experiment:" + experimentName, 0);
    store.setExperimentType("Other", 0);

    // populate SPW metadata
    for (int plate=0; plate<tiffs.length; plate++) {
      store.setPlateColumnNamingConvention("1", plate);
      store.setPlateRowNamingConvention("A", plate);

      String plateDir = plateDirectories[plate];
      plateDir = plateDir.substring(plateDir.lastIndexOf(File.separator) + 1);
      plateDir = plateDir.substring(plateDir.indexOf("-") + 1);
      store.setPlateName(plateDir, plate);
      store.setPlateExternalIdentifier(plateDir, plate);

      for (int well=0; well<tiffs[plate].length; well++) {
        int wellIndex = wellNumber[plate][well];
        store.setWellColumn(wellIndex % wellCols, plate, wellIndex);
        store.setWellRow(wellIndex / wellCols, plate, wellIndex);

        int imageNumber = getSeriesNumber(plate, well);
        store.setWellSampleImageRef("Image:" + imageNumber, plate,
          wellIndex, 0);
        store.setWellSampleIndex(new Integer(imageNumber), plate, wellIndex, 0);
      }
    }

    // populate Image/Pixels metadata
    for (int s=0; s<getSeriesCount(); s++) {
      int plate = getPlateNumber(s);
      int well = getWellNumber(s);
      well = wellNumber[plate][well];
      store.setImageExperimentRef("Experiment:" + experimentName, s);

      char wellRow = (char) ('A' + (well / wellCols));
      int wellCol = (well % wellCols) + 1;

      store.setImageID("Image:" + s, s);
      store.setImageName("Plate #" + plate + ", Well " + wellRow + wellCol, s);
      MetadataTools.setDefaultCreationDate(store, id, s);
    }

    // populate image-level ROIs
    for (String file : analysisFiles) {
      String name = new Location(file).getName();
      if (name.startsWith("Well") && name.endsWith(".txt")) {
        String plateName =
          new Location(file).getParentFile().getParentFile().getName();
        String plateIndex = plateName.substring(0, plateName.indexOf("-"));
        int plate = Integer.parseInt(plateIndex) - 1;

        String wellIndex = name.substring(4, name.indexOf("_"));
        int well = Integer.parseInt(wellIndex) - 1;

        int tIndex = name.indexOf("_t") + 2;
        String t = name.substring(tIndex, name.indexOf("_", tIndex));

        int zIndex = name.indexOf("_z") + 2;
        String zValue = name.substring(zIndex, name.indexOf("_", zIndex));

        int time = Integer.parseInt(t);
        int z = Integer.parseInt(zValue);

        int series = getSeriesNumber(plate, well);

        RandomAccessInputStream s = new RandomAccessInputStream(file);
        String data = s.readString((int) s.length());
        String[] lines = data.split("\n");
        int start = 0;
        while (!lines[start].startsWith("Label")) start++;

        String[] columns = lines[start].split("\t");
        Vector<String> columnNames = new Vector<String>();
        for (String v : columns) columnNames.add(v);

        for (int i=start+1; i<lines.length; i++) {
          int roi = i - start - 1;

          String[] v = lines[i].split("\t");

          float cx = Float.parseFloat(v[columnNames.indexOf("Col")]);
          float cy = Float.parseFloat(v[columnNames.indexOf("Row")]);

          store.setROIT0(new Integer(time), series, roi);
          store.setROIT1(new Integer(time), series, roi);
          store.setROIZ0(new Integer(z), series, roi);
          store.setROIZ1(new Integer(z), series, roi);

          store.setShapeText(v[columnNames.indexOf("Label")], series, roi, 0);
          store.setShapeTheT(new Integer(time), series, roi, 0);
          store.setShapeTheZ(new Integer(z), series, roi, 0);
          store.setCircleCx(v[columnNames.indexOf("Col")], series, roi, 0);
          store.setCircleCy(v[columnNames.indexOf("Row")], series, roi, 0);

          float diam = Float.parseFloat(v[columnNames.indexOf("Cell Diam.")]);
          String radius = String.valueOf(diam / 2);

          store.setCircleR(radius, series, roi, 0);

          // NB: other attributes are "Nucleus Area", "Cell Type", and
          // "Mean Nucleus Intens."
        }

        s.close();
      }
    }
  }

  // -- Helper methods --

  /** Retrieve the series corresponding to the given plate and well. */
  private int getSeriesNumber(int plate, int well) {
    int series = 0;
    for (int i=0; i<plate; i++) {
      series += tiffs[i].length;
    }
    return series + well;
  }

  /** Retrieve the well to which this series belongs. */
  private int getWellNumber(int series) {
    return getPlateAndWell(series)[1];
  }

  /** Retrieve the plate to which this series belongs. */
  private int getPlateNumber(int series) {
    return getPlateAndWell(series)[0];
  }

  /** Retrieve a two element array containing the plate and well indices. */
  private int[] getPlateAndWell(int series) {
    // NB: Don't use FormatTools.rasterToPosition(...), because each plate
    // could have a different number of wells.
    int wellNumber = series;
    int plateNumber = 0;
    while (wellNumber >= tiffs[plateNumber].length) {
      wellNumber -= tiffs[plateNumber].length;
      plateNumber++;
    }
    return new int[] {plateNumber, wellNumber};
  }

}
