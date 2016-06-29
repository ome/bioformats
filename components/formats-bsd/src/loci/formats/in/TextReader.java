/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.in;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.IRandomAccess;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

/**
 * Reader for text files containing tables of data. All image planes
 * are stored in memory as 32-bit floats until the file is closed,
 * so very large text documents will require commensurate available RAM.
 *
 * Text format is flexible, but assumed to be in tabular form with a consistent
 * number of columns, and a labeled header line immediately preceding the data.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class TextReader extends FormatReader {

  // -- Constants --

  private static final boolean LITTLE_ENDIAN = false;
  private static final String LABEL_X = "x";
  private static final String LABEL_Y = "y";

  /** How often to report progress during initialization, in milliseconds. */
  private static final long TIME_OFFSET = 2000;

  // -- Fields --

  /**
   * Because we have no way of indexing into the text file efficiently
   * in general, we cheat and store the entire file's data in a giant array.
   */
  private float[][] data;

  /** Current row number. */
  private int row;

  /** Number of tokens per row. */
  private int rowLength;

  /** Column index for X coordinate. */
  private int xIndex = -1;

  /** Column index for Y coordinate. */
  private int yIndex = -1;

  /** List of channel labels. */
  private String[] channels;

  /** Image width. */
  private int sizeX;

  /** Image height. */
  private int sizeY;

  // -- Constructor --

  /** Constructs a new text reader. */
  public TextReader() {
    super("Text", new String[] {"txt", "csv"});
    suffixSufficient = false;
  }

  // -- TextReader methods --

  /** Gets the label for the given channel. */
  public String getChannelLabel(int c) {
    FormatTools.assertId(currentId, true, 1);
    return channels[c];
  }

  // -- IFormatReader methods --

  /* @see IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 8192;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    row = 0;
    String data = stream.readString(blockLen);
    List<String> lines = Arrays.asList(data.split("\n"));
    String[] line = getNextLine(lines);
    if (line == null) return false;
    int headerRows = 0;
    try {
      headerRows = parseFileHeader(lines);
    }
    catch (FormatException e) { }
    return headerRows > 0;
  }

  /* @see IFormatReader#openBytes(int, byte[], int, int, int, int) */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    // copy floating point data into byte buffer
    final float[] plane = data[no];
    int q = 0;
    for (int j=0; j<h; j++) {
      final int yy = y + j;
      for (int i=0; i<w; i++) {
        final int xx = x + i;
        final int index = yy * sizeX + xx;
        final int bits = Float.floatToIntBits(plane[index]);
        DataTools.unpackBytes(bits, buf, q, 4, LITTLE_ENDIAN);
        q += 4;
      }
    }

    return buf;
  }

  /* @see IFormatReader#openPlane(int, int, int, int, int int) */
  @Override
  public Object openPlane(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    return data[no];
  }

  /* @see IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      data = null;
      rowLength = 0;
      xIndex = yIndex = -1;
      channels = null;
      sizeX = sizeY = 0;
      row = 0;
    }
  }

  // -- IFormatHandler methods --

  /* @see IFormatHandler#getNativeDataType() */
  @Override
  public Class<?> getNativeDataType() {
    return float[].class;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    // read file into memory
    LOGGER.info("Reading file");
    List<String> lines = readFile(id);

    // parse file header
    LOGGER.info("Parsing file header");
    final int headerRows = parseFileHeader(lines);

    LOGGER.info("Creating images");

    // allocate memory for image data
    final int sizeZ = 1, sizeT = 1; // no Z or T for now
    final int sizeC = channels.length;
    final int imageCount = sizeZ * sizeC * sizeT;
    final int planeSize = sizeX * sizeY;
    data = new float[imageCount][planeSize];

    // flag all values as missing by default
    for (int i=0; i<imageCount; i++) Arrays.fill(data[i], Float.NaN);

    // read data into float array
    parseTableData(lines, headerRows);

    LOGGER.info("Populating metadata");

    // populate core metadata
    populateCoreMetadata(sizeX, sizeY, sizeZ, sizeC, sizeT);

    // populate OME metadata
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

  // -- Helper methods --

  private List<String> readFile(String id) throws IOException {
    List<String> lines = new ArrayList<String>();
    long time = System.currentTimeMillis();
    IRandomAccess handle = Location.getMappedFile(id);
    if (handle == null) {
      // HACK: Read using vanilla BufferedReader, since it's faster.
      String mapId = Location.getMappedId(id);
      BufferedReader in = new BufferedReader(
        new InputStreamReader(new FileInputStream(mapId), Constants.ENCODING));
      int no = 0;
      while (true) {
        no++;
        time = checkTime(time, no, 0, 0);
        String line = in.readLine();
        if (line == null) break; // eof
        lines.add(line);
      }
      in.close();
    }
    else {
      // read data using RandomAccessInputStream (data may not be a file)
      RandomAccessInputStream in = new RandomAccessInputStream(handle);
      int no = 0;
      while (true) {
        no++;
        time = checkTime(time, no, in.getFilePointer(), in.length());
        String line = in.readLine();
        if (line == null) break; // eof
        lines.add(line);
      }
      in.close();
    }
    return lines;
  }

  /**
   * Parses the file looking for the file header.
   * Determines image extents (sets sizeX and sizeY).
   * Determines channel names (populates channels array).
   *
   * @return number of rows in the header
   */
  private int parseFileHeader(List<String> lines) throws FormatException {
    String[] lastTokens = null;
    double[] rowData = null;
    while (true) {
      String[] tokens = getNextLine(lines);
      if (tokens == null) throw new FormatException("No tabular data found");
      if (tokens.length >= 3 && // need at least 3 columns of data
        lastTokens != null && lastTokens.length == tokens.length)
      {
        // consistent number of tokens; might be the header and first data row

        // allocate rowData as needed
        if (rowData == null || rowData.length != tokens.length) {
          rowData = new double[tokens.length];
        }

        // try to parse the first data row
        if (getRowData(tokens, rowData)) {
          LOGGER.info("Found header on line " + (row - 1));
          // looks like tabular data; assume previous line is the header
          parseHeaderRow(lastTokens);
          break;
        }
      }
      lastTokens = tokens;
    }
    final int headerRows = row - 1;

    if (xIndex < 0) throw new FormatException("No X coordinate column found");
    if (yIndex < 0) throw new FormatException("No Y coordinate column found");

    // search remainder of tabular data for X and Y extents
    boolean checkRow = true;
    while (true) {
      if (checkRow) {
        // expand dimensional extents as needed
        int x = getX(rowData);
        if (x < 0) {
          throw new FormatException("Row #" + row + ": invalid X: " + x);
        }
        if (sizeX <= x) sizeX = x + 1;
        int y = getY(rowData);
        if (y < 0) {
          throw new FormatException("Row #" + row + ": invalid Y: " + x);
        }
        if (sizeY <= y) sizeY = y + 1;
      }

      // parse next row
      String[] tokens = getNextLine(lines);
      if (tokens == null) break; // eof
      checkRow = getRowData(tokens, rowData);
    }

    return headerRows;
  }

  /** Reads the tabular data into the data array. */
  private void parseTableData(List<String> lines, int linesToSkip) {
    row = linesToSkip; // skip header lines

    double[] rowData = new double[rowLength];
    while (true) {
      String[] tokens = getNextLine(lines);
      if (tokens == null) break; // eof
      if (tokens.length != rowLength) {
        LOGGER.warn("Ignoring deviant row #" + row);
        continue;
      }

      // parse values from row
      boolean success = getRowData(tokens, rowData);
      if (!success) {
        LOGGER.warn("Ignoring non-numeric row #" + row);
        continue;
      }

      // copy values into array
      assignValues(rowData);
    }
  }

  /** Populates the {@link CoreMetadata} values. */
  private void populateCoreMetadata(int sizeX, int sizeY,
    int sizeZ, int sizeC, int sizeT)
  {
    CoreMetadata m = core.get(0);
    m.sizeX = sizeX;
    m.sizeY = sizeY;
    m.sizeZ = sizeZ;
    m.sizeC = sizeC;
    m.sizeT = sizeT;
    m.pixelType = FormatTools.FLOAT;
    m.bitsPerPixel = 32;
    m.imageCount = sizeZ * sizeC * sizeT;
    m.dimensionOrder = "XYZCT";
    m.orderCertain = true;
    m.littleEndian = LITTLE_ENDIAN;
    m.metadataComplete = true;
  }

  /**
   * Parses numerical row data from the given tokens.
   *
   * @param tokens list of token strings to parse
   * @param rowData array to fill in with the data; length must match tokens
   *
   * @return true if the data could be parsed
   */
  private boolean getRowData(String[] tokens, double[] rowData) {
    try {
      for (int i=0; i<tokens.length; i++) {
        rowData[i] = Double.parseDouble(tokens[i]);
      }
      return true;
    }
    catch (NumberFormatException exc) {
      // not a data row
      return false;
    }
  }

  /** Populates rowLength, xIndex, yIndex, and channels. */
  private void parseHeaderRow(String[] tokens) {
    rowLength = tokens.length;
    List<String> channelsList = new ArrayList<String>();
    for (int i=0; i<rowLength; i++) {
      String token = tokens[i];
      if (token.equals(LABEL_X)) xIndex = i;
      else if (token.equals(LABEL_Y)) yIndex = i;
      else {
        // treat column as a channel
        channelsList.add(token);
      }
    }
    channels = channelsList.toArray(new String[0]);
  }

  /** Assigns values from the given row into the data array. */
  private void assignValues(double[] rowData) {
    int x = getX(rowData);
    int y = getY(rowData);
    int c = 0;
    int index = sizeX * y + x;
    for (int i=0; i<rowLength; i++) {
      if (i == xIndex || i == yIndex) continue;
      data[c++][index] = (float) rowData[i];
    }
  }

  private long checkTime(long time, int no, long pos, long len) {
    long t = System.currentTimeMillis();
    if (t - time > TIME_OFFSET) {
      // some time has passed; report progress
      if (len > 0) {
        int percent = (int) (100 * pos / len);
        LOGGER.info("Reading line " + no + " (" + percent + "%)");
      }
      else LOGGER.info("Reading line " + no);
      time = t;
    }
    return time;
  }

  private int getX(double[] rowData) { return (int) rowData[xIndex]; }
  private int getY(double[] rowData) { return (int) rowData[yIndex]; }

  private String[] getNextLine(List<String> lines) {
    while (true) {
      if (row >= lines.size()) return null; // end of list
      String line = lines.get(row++);
      line = line.trim();
      if (line.equals("")) continue; // skip blank lines
      return line.split("[\\s,]");
    }
  }

}
