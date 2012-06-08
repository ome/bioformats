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

import java.io.IOException;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.Timestamp;

/**
 * PDSReader is the file format reader for Perkin Elmer densitometer files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/PDSReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/PDSReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class PDSReader extends FormatReader {

  // -- Constants --

  private static final String PDS_MAGIC_STRING = " IDENTIFICATION";
  private static final String DATE_FORMAT = "HH:mm:ss  d-MMM-** yyyy";

  // -- Fields --

  private String pixelsFile;
  private int recordWidth;
  private int lutIndex = -1;
  private boolean reverseX = false, reverseY = false;

  // -- Constructor --

  /** Constructs a new PDS reader. */
  public PDSReader() {
    super("Perkin Elmer Densitometer", new String[] {"hdr", "img"});
    domains = new String[] {FormatTools.EM_DOMAIN};
    suffixSufficient = false;
    hasCompanionFiles = true;
    datasetDescription = "One .hdr file and a similarly-named .img file";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (!open) return false;
    if (checkSuffix(name, "hdr")) return super.isThisType(name, open);
    else if (checkSuffix(name, "img")) {
      String headerFile = name.substring(0, name.lastIndexOf(".")) + ".hdr";
      return new Location(headerFile).exists() && isThisType(headerFile, open);
    }
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 15;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(blockLen).equals(PDS_MAGIC_STRING);
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (lutIndex < 0 || lutIndex >= 3) return null;
    short[][] lut = new short[3][65536];
    for (int i=0; i<lut[lutIndex].length; i++) {
      lut[lutIndex][i] = (short) i;
    }

    return lut;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (noPixels) {
      return new String[] {currentId};
    }
    return new String[] {currentId, pixelsFile};
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int pad = recordWidth - (getSizeX() % recordWidth);
    RandomAccessInputStream pixels = new RandomAccessInputStream(pixelsFile);

    int realX = reverseX ? getSizeX() - w - x : x;
    int realY = reverseY ? getSizeY() - h - y : y;

    readPlane(pixels, realX, realY, w, h, pad, buf);
    pixels.close();

    if (reverseX) {
      for (int row=0; row<h; row++) {
        for (int col=0; col<w/2; col++) {
          int begin = 2 * (row * w + col);
          int end = 2 * (row * w + (w - col - 1));

          byte b0 = buf[begin];
          byte b1 = buf[begin + 1];
          buf[begin] = buf[end];
          buf[begin + 1] = buf[end + 1];
          buf[end] = b0;
          buf[end + 1] = b1;
        }
      }
    }

    if (reverseY) {
      for (int row=0; row<h/2; row++) {
        byte[] b = new byte[w * 2];
        int start = row * w * 2;
        int end = (h - row - 1) * w * 2;
        System.arraycopy(buf, start, b, 0, b.length);
        System.arraycopy(buf, end, buf, start, b.length);
        System.arraycopy(b, 0, buf, end, b.length);
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      recordWidth = 0;
      pixelsFile = null;
      lutIndex = -1;
      reverseX = reverseY = false;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (!checkSuffix(id, "hdr")) {
      String headerFile = id.substring(0, id.lastIndexOf(".")) + ".hdr";
      if (!new Location(headerFile).exists()) {
        headerFile = id.substring(0, id.lastIndexOf(".")) + ".HDR";
        if (!new Location(headerFile).exists()) {
          throw new FormatException("Could not find matching .hdr file.");
        }
      }

      initFile(headerFile);
      return;
    }

    super.initFile(id);

    String[] headerData = DataTools.readFile(id).split("\r\n");
    if (headerData.length == 1) {
      headerData = headerData[0].split("\r");
    }
    Double xPos = null, yPos = null;
    Double deltaX = null, deltaY = null;
    String date = null;

    for (String line : headerData) {
      int eq = line.indexOf("=");
      if (eq < 0) continue;
      int end = line.indexOf("/");
      if (end < 0) end = line.length();

      String key = line.substring(0, eq).trim();
      String value = line.substring(eq + 1, end).trim();

      if (key.equals("NXP")) {
        core[0].sizeX = Integer.parseInt(value);
      }
      else if (key.equals("NYP")) {
        core[0].sizeY = Integer.parseInt(value);
      }
      else if (key.equals("XPOS")) {
        xPos = new Double(value);
        addGlobalMeta("X position for position #1", xPos);
      }
      else if (key.equals("YPOS")) {
        yPos = new Double(value);
        addGlobalMeta("Y position for position #1", yPos);
      }
      else if (key.equals("SIGNX")) {
        reverseX = value.replaceAll("'", "").trim().equals("-");
      }
      else if (key.equals("SIGNY")) {
        reverseY = value.replaceAll("'", "").trim().equals("-");
      }
      else if (key.equals("DELTAX")) {
        deltaX = new Double(value);
      }
      else if (key.equals("DELTAY")) {
        deltaY = new Double(value);
      }
      else if (key.equals("COLOR")) {
        int color = Integer.parseInt(value);
        if (color == 4) {
          core[0].sizeC = 3;
          core[0].rgb = true;
        }
        else {
          core[0].sizeC = 1;
          core[0].rgb = false;
          lutIndex = color - 1;
          core[0].indexed = lutIndex >= 0;
        }
      }
      else if (key.equals("SCAN TIME")) {
        long modTime = new Location(currentId).getAbsoluteFile().lastModified();
        String year = DateTools.convertDate(modTime, DateTools.UNIX, "yyyy");
        date = value.replaceAll("'", "") + " " + year;
        date = DateTools.formatDate(date, DATE_FORMAT);
      }
      else if (key.equals("FILE REC LEN")) {
        recordWidth = Integer.parseInt(value) / 2;
      }

      addGlobalMeta(key, value);
    }

    core[0].sizeZ = 1;
    core[0].sizeT = 1;
    core[0].imageCount = 1;
    core[0].dimensionOrder = "XYCZT";
    core[0].pixelType = FormatTools.UINT16;
    core[0].littleEndian = true;

    String base = currentId.substring(0, currentId.lastIndexOf("."));
    pixelsFile = base + ".IMG";
    if (!new Location(pixelsFile).exists()) {
      pixelsFile = base + ".img";
    }

    boolean minimumMetadata =
      getMetadataOptions().getMetadataLevel() == MetadataLevel.MINIMUM;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, !minimumMetadata);

    if (date != null) {
      store.setImageAcquisitionDate(new Timestamp(date), 0);
    }

    if (!minimumMetadata) {
      store.setPlanePositionX(xPos, 0, 0);
      store.setPlanePositionY(yPos, 0, 0);
      if (deltaX != null && deltaX > 0) {
        store.setPixelsPhysicalSizeX(new PositiveFloat(deltaX), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeX; got {}",
          deltaX);
      }
      if (deltaY != null && deltaY > 0) {
        store.setPixelsPhysicalSizeY(new PositiveFloat(deltaY), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeY; got {}",
          deltaY);
      }
    }
  }

}
