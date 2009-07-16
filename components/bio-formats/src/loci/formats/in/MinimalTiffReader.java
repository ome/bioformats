//
// MinimalTiffReader.java
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

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.TiffTools;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;

/**
 * MinimalTiffReader is the superclass for file format readers compatible with
 * or derived from the TIFF 6.0 file format.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/MinimalTiffReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/MinimalTiffReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class MinimalTiffReader extends FormatReader {

  // -- Fields --

  /** List of IFDs for the current TIFF. */
  protected IFDList ifds;

  /** List of thumbnail IFDs for the current TIFF. */
  protected IFDList thumbnailIFDs;

  private int lastPlane;

  // -- Constructors --

  /** Constructs a new MinimalTiffReader. */
  public MinimalTiffReader() {
    super("Minimal TIFF", new String[] {"tif", "tiff"});
  }

  /** Constructs a new MinimalTiffReader. */
  public MinimalTiffReader(String name, String suffix) { super(name, suffix); }

  /** Constructs a new MinimalTiffReader. */
  public MinimalTiffReader(String name, String[] suffixes) {
    super(name, suffixes);
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    return TiffTools.isValidHeader(stream);
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (ifds == null || lastPlane < 0 || lastPlane > ifds.size()) return null;
    IFD lastIFD = ifds.get(lastPlane);
    int[] bits = TiffTools.getBitsPerSample(lastIFD);
    if (bits[0] <= 8) {
      int[] colorMap =
        TiffTools.getIFDIntArray(lastIFD, TiffTools.COLOR_MAP, false);
      if (colorMap == null) return null;

      byte[][] table = new byte[3][colorMap.length / 3];
      int next = 0;
      for (int j=0; j<table.length; j++) {
        for (int i=0; i<table[0].length; i++) {
          table[j][i] = (byte) ((colorMap[next++] >> 8) & 0xff);
        }
      }

      return table;
    }
    return null;
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (ifds == null || lastPlane < 0 || lastPlane > ifds.size()) return null;
    IFD lastIFD = ifds.get(lastPlane);
    int[] bits = TiffTools.getBitsPerSample(lastIFD);
    if (bits[0] <= 16 && bits[0] > 8) {
      int[] colorMap =
        TiffTools.getIFDIntArray(lastIFD, TiffTools.COLOR_MAP, false);
      if (colorMap == null || colorMap.length < 65536 * 3) return null;
      short[][] table = new short[3][colorMap.length / 3];
      int next = 0;
      for (int i=0; i<table.length; i++) {
        for (int j=0; j<table[0].length; j++) {
          if (isLittleEndian()) {
            table[i][j] = (short) (colorMap[next++] & 0xffff);
          }
          else {
            int n = colorMap[next++];
            table[i][j] =
              (short) (((n & 0xff0000) >> 8) | ((n & 0xff000000) >> 24));
          }
        }
      }
      return table;
    }
    return null;
  }

  /* @see loci.formats.FormatReader#getThumbSizeX() */
  public int getThumbSizeX() {
    if (thumbnailIFDs != null && thumbnailIFDs.size() > 0) {
      try {
        return (int) TiffTools.getImageWidth(thumbnailIFDs.get(0));
      }
      catch (FormatException e) {
        if (debug) trace(e);
      }
    }
    return super.getThumbSizeX();
  }

  /* @see loci.formats.FormatReader#getThumbSizeY() */
  public int getThumbSizeY() {
    if (thumbnailIFDs != null && thumbnailIFDs.size() > 0) {
      try {
        return (int) TiffTools.getImageLength(thumbnailIFDs.get(0));
      }
      catch (FormatException e) {
        if (debug) trace(e);
      }
    }
    return super.getThumbSizeY();
  }

  /* @see loci.formats.FormatReader#openThumbBytes(int) */
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (thumbnailIFDs == null || thumbnailIFDs.size() <= no) {
      return super.openThumbBytes(no);
    }
    int[] bps = TiffTools.getBitsPerSample(thumbnailIFDs.get(no));
    int b = bps[0];
    while ((b % 8) != 0) b++;
    b /= 8;
    if (b != FormatTools.getBytesPerPixel(getPixelType()) ||
      bps.length != getRGBChannelCount())
    {
      return super.openThumbBytes(no);
    }

    byte[] buf = new byte[getThumbSizeX() * getThumbSizeY() *
      getRGBChannelCount() * FormatTools.getBytesPerPixel(getPixelType())];
    return TiffTools.getSamples(thumbnailIFDs.get(no), in, buf);
  }

  /**
   * @see loci.formats.FormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    lastPlane = no;
    TiffTools.getSamples(ifds.get(no), in, buf, x, y, w, h);
    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    ifds = null;
    thumbnailIFDs = null;
    lastPlane = 0;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("MinimalTiffReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    boolean little = in.readShort() == 0x4949;
    in.order(little);

    status("Reading IFDs");

    ifds = TiffTools.getIFDs(in);
    if (ifds == null) throw new FormatException("No IFDs found");

    // separate thumbnail IFDs from regular IFDs

    IFDList v = new IFDList();
    IFDList thumbs = new IFDList();
    for (int i=0; i<ifds.size(); i++) {
      IFD ifd = ifds.get(i);
      boolean thumbnail = ifds.size() > 1 &&
        TiffTools.getIFDIntValue(ifd, TiffTools.NEW_SUBFILE_TYPE) == 1;
      if (thumbnail) thumbs.add(ifd);
      else if (ifd.get(new Integer(TiffTools.IMAGE_WIDTH)) != null) {
        v.add(ifd);
      }
    }

    ifds = v;
    thumbnailIFDs = thumbs;

    status("Populating metadata");

    core[0].imageCount = ifds.size();

    IFD firstIFD = ifds.get(0);

    int photo = TiffTools.getPhotometricInterpretation(firstIFD);
    int samples = TiffTools.getSamplesPerPixel(firstIFD);
    core[0].rgb = samples > 1 || photo == TiffTools.RGB;
    core[0].interleaved = false;
    core[0].littleEndian = TiffTools.isLittleEndian(firstIFD);

    core[0].sizeX = (int) TiffTools.getImageWidth(firstIFD);
    core[0].sizeY = (int) TiffTools.getImageLength(firstIFD);
    core[0].sizeZ = 1;
    core[0].sizeC = isRGB() ? samples : 1;
    core[0].sizeT = ifds.size();
    core[0].pixelType = TiffTools.getPixelType(firstIFD);
    core[0].metadataComplete = true;
    core[0].indexed = photo == TiffTools.RGB_PALETTE &&
      (get8BitLookupTable() != null || get16BitLookupTable() != null);
    if (isIndexed()) {
      core[0].sizeC = 1;
      core[0].rgb = false;
    }
    if (getSizeC() == 1 && !isIndexed()) core[0].rgb = false;
    core[0].falseColor = false;
    core[0].dimensionOrder = "XYCZT";
  }

}
