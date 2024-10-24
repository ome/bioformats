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

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import ome.units.quantity.Time;
import ome.units.UNITS;

/**
 * SIFReader is the file format reader for Andor SIF files.
 */
public class SIFReader extends FormatReader {

  // -- Constants --

  private static final String MAGIC_STRING = "Andor Technology";

  // -- Fields --

  private long pixelOffset;

  // -- Constructor --

  /** Constructs a new SIF reader. */
  public SIFReader() {
    super("Andor SIF", "sif");
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 16;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(blockLen).equals(MAGIC_STRING);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    in.seek(pixelOffset + (long)no * (long)FormatTools.getPlaneSize(this));
    readPlane(in, x, y, w, h, buf);
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      pixelOffset = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    CoreMetadata m = core.get(0);

    String line;
    String[] tokens;

    // Magic line
    addGlobalMetaList("Line", in.readLine());
    addGlobalMetaList("Line", in.readLine());

    // Parse SIF version
    addGlobalMetaList("Line", line = in.readLine());
    tokens = line.split(" ");
    int sifVersion = Integer.parseInt(tokens[0]);

    // Read header contents until "Pixel number" line with enough tokens to contain size information
    while (!(line = in.readLine()).startsWith("Pixel number")
            || (tokens = line.split(" ")).length < 3) {
      addGlobalMetaList("Line", line.trim());
    }

    // Read channels, Z sections, and image count from this line
    // X and Y from this line are not accurate for cropped images
    m.sizeC = Integer.parseInt(tokens[2]);
    m.sizeZ = Integer.parseInt(tokens[5]);
    m.sizeT = Integer.parseInt(tokens[6]);
    m.imageCount = getSizeZ() * getSizeT() * getSizeC();

    // Subsequent lines contain updated information for each subimage
    for (int i = 0; i < getSizeZ(); i++) {
      line = in.readLine();
      tokens = line.split(" ");

      int x1 = Integer.parseInt(tokens[1]);
      int y1 = Integer.parseInt(tokens[2]);
      int x2 = Integer.parseInt(tokens[3]);
      int y2 = Integer.parseInt(tokens[4]);
      int x3 = Integer.parseInt(tokens[5]);
      int y3 = Integer.parseInt(tokens[6]);
      m.sizeX = Math.abs(x1 - x2) + x3;
      m.sizeY = Math.abs(y1 - y2) + y3;
    }

    // Parse timestamps for each frame
    double[] timestamp = new double[getImageCount()];
    String[] lines = readFixedWidthTable(getImageCount());
    for (int i = 0; i < lines.length; i++) {
      timestamp[i] = Double.parseDouble(lines[i].trim());
    }

    pixelOffset = in.getFilePointer();

    // Some SIF versions contain an additional flag and data block following timestamps.
    // If present, the pixelOffset must be adjusted to after this data.
    // TODO: Is there any way to parse this segment while guaranteeing that we aren't reading image data?
    //  I.e., is it possible that the first two bytes of image data are 0x300A ("0\n")?
    byte flag = in.readByte();
    byte flagTerminator = in.readByte();

    if (flag == '1' && flagTerminator == '\n') {
      if (sifVersion == 65567) {
        // SIF Version 65567 contains an additional table for all frames
        readFixedWidthTable(getImageCount());

        pixelOffset = in.getFilePointer();
      }
    } else if (flag == '0' && flagTerminator == '\n') {
      pixelOffset = in.getFilePointer();
    }

    m.pixelType = FormatTools.FLOAT;
    m.dimensionOrder = "XYCZT";
    m.littleEndian = true;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this,
      getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      for (int i=0; i<getImageCount(); i++) {
        store.setPlaneDeltaT(new Time(timestamp[i], UNITS.MICROSECOND), 0, i);
      }
    }
  }

  private String[] readFixedWidthTable(int rows) throws IOException {
    // Read a line of the fixed-width table to know total size
    String line = in.readLine();
    int lineSize = line.length();

    // Read the entire table
    ByteBuffer buffer = ByteBuffer.allocate(lineSize * (rows - 1));
    in.read(buffer);
    ((Buffer)buffer).rewind();

    String[] lines = new String[rows];
    lines[0] = line;

    byte[] lineChars = new byte[lineSize];
    for (int i = 1; i < rows; i++) {
      buffer.get(lineChars);
      lines[i] = new String(lineChars, StandardCharsets.ISO_8859_1);
    }

    return lines;
  }

}
