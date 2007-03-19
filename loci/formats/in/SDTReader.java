//
// SDTReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.awt.image.BufferedImage;
import java.io.*;
import loci.formats.*;

/**
 * SDTReader is the file format reader for
 * Becker &amp; Hickl SPC-Image SDT files.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class SDTReader extends FormatReader {

  // -- Fields --

  /** Current file. */
  protected RandomAccessStream in;

  /** Object containing SDT header information. */
  protected SDTInfo info;

  /** Offset to binary data. */
  protected int off;

  /** Number of time bins in lifetime histogram. */
  protected int timeBins;

  /** Number of spectral channels. */
  protected int channels;

  /** Whether to combine lifetime bins into single intensity image planes. */
  protected boolean intensity = true;

  // -- Constructor --

  /** Constructs a new SDT reader. */
  public SDTReader() { super("SPCImage Data", "sdt"); }

  // -- SDTReader API methods --

  /**
   * Toggles whether the reader should return intensity
   * data only (the sum of each lifetime histogram).
   */
  public void setIntensity(boolean intensity) { this.intensity = intensity; }

  /**
   * Gets whether the reader is combining each lifetime
   * histogram into a summed intensity image plane.
   */
  public boolean isIntensity() { return intensity; }

  /** Gets the number of bins in the lifetime histogram. */
  public int getTimeBinCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return timeBins;
  }

  /** Gets the number of spectral channels. */
  public int getChannelCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return channels;
  }

  /** Gets object containing SDT header information. */
  public SDTInfo getInfo(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return info;
  }

  public short[][][][] openData(String id) {
    // CTR TODO
    return null;
  }

  // -- IFormatReader API methods --

  /** Checks if the given block is a valid header for an SDT file. */
  public boolean isThisType(byte[] block) { return false; }

  /** Determines the number of images in the given SDT file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return intensity ? channels : (timeBins * channels);
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    return false;
  }

  /** Get the size of the C dimension. */
  public int getSizeC(String id) throws FormatException, IOException {
    int sc = super.getSizeC(id);
    return intensity ? sc : (timeBins * sc);
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    return true;
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return false;
  }

  /** Obtains the specified image from the given SDT file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    byte[] buf = new byte[2 * sizeX[series] * sizeY[series]];
    return openBytes(id, no, buf);
  }

  public byte[] openBytes(String id, int no, byte[] buf)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= timeBins * channels) {
      throw new FormatException("Invalid image number: " + no);
    }
    if (buf.length < 2 * sizeX[series] * sizeY[series]) {
      throw new FormatException("Buffer too small.");
    }

    if (intensity) {
      in.seek(off + 2 * sizeX[series] * sizeY[series] * timeBins * no);
      for (int y=0; y<sizeY[series]; y++) {
        for (int x=0; x<sizeX[series]; x++) {
          // read all lifetime bins at this pixel for this channel

          // combine lifetime bins into intensity value
          short sum = 0;
          for (int t=0; t<timeBins; t++) {
            sum += DataTools.read2SignedBytes(in, true);
          }
          int ndx = 2 * (sizeX[0] * y + x);
          buf[ndx] = (byte) (sum & 0xff);
          buf[ndx + 1] = (byte) ((sum >> 8) & 0xff);
        }
      }
    }
    else {
      int t = no % timeBins;
      int c = no / timeBins;
      in.seek(off + 2 * (sizeX[series] * sizeY[series] * timeBins * c + t));
      for (int y=0; y<sizeY[series]; y++) {
        for (int x=0; x<sizeX[series]; x++) {
          // read data only for the given lifetime bin
          int ndx = 2 * (sizeX[series] * y + x);
          in.readFully(buf, ndx, 2);
          in.skipBytes(timeBins);
        }
      }
    }
    updateMinMax(buf, no);
    return buf;
  }

  /** Obtains the specified image from the given SDT file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    BufferedImage b = ImageTools.makeImage(openBytes(id, no),
      sizeX[series], sizeY[series], 1, false, 2, true);
    updateMinMax(b, no);
    return b;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws FormatException, IOException {
    if (fileOnly && in != null) in.close();
    else if (!fileOnly) close();
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  // -- FormatReader API methods --

  /** Initializes the given SDT file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("SDTReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);
    in.order(true);

    status("Reading header");

    // read file header information
    info = new SDTInfo(in, metadata);
    off = info.dataBlockOffs + 22;
    timeBins = info.timeBins;
    channels = info.channels;
    addMeta("time bins", new Integer(timeBins));
    addMeta("channels", new Integer(channels));

    status("Populating metadata");

    sizeX[0] = info.width;
    sizeY[0] = info.height;
    sizeZ[0] = 1;
    sizeC[0] = channels;
    sizeT[0] = 1;
    currentOrder[0] = "XYZTC";
    pixelType[0] = FormatTools.UINT16;

    MetadataStore store = getMetadataStore(id);
    store.setPixels(new Integer(getSizeX(id)), new Integer(getSizeY(id)),
      new Integer(getSizeZ(id)), new Integer(getSizeC(id)),
      new Integer(getSizeT(id)), new Integer(pixelType[0]),
      new Boolean(!isLittleEndian(id)), getDimensionOrder(id), null, null);
    for (int i=0; i<sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null);
    }
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new SDTReader().testRead(args);
  }

}
