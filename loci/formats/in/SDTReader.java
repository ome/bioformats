//
// SDTReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
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

import java.io.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * SDTReader is the file format reader for
 * Becker &amp; Hickl SPC-Image SDT files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/SDTReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/SDTReader.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class SDTReader extends FormatReader {

  // -- Fields --

  /** Object containing SDT header information. */
  protected SDTInfo info;

  /** Offset to binary data. */
  protected int off;

  /** Number of time bins in lifetime histogram. */
  protected int timeBins;

  /** Number of spectral channels. */
  protected int channels;

  /** Whether to combine lifetime bins into single intensity image planes. */
  protected boolean intensity = false;

  // -- Constructor --

  /** Constructs a new SDT reader. */
  public SDTReader() {
    super("SPCImage Data", "sdt");
  }

  // -- SDTReader API methods --

  /**
   * Toggles whether the reader should return intensity
   * data only (the sum of each lifetime histogram).
   */
  public void setIntensity(boolean intensity) {
    FormatTools.assertId(currentId, false, 1);
    this.intensity = intensity;
  }

  /**
   * Gets whether the reader is combining each lifetime
   * histogram into a summed intensity image plane.
   */
  public boolean isIntensity() { return intensity; }

  /** Gets the number of bins in the lifetime histogram. */
  public int getTimeBinCount() {
    return timeBins;
  }

  /** Gets the number of spectral channels. */
  public int getChannelCount() {
    return channels;
  }

  /** Gets object containing SDT header information. */
  public SDTInfo getInfo() {
    return info;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) { return false; }

  /* @see loci.formats.IFormatReader#getChannelDimLengths() */
  public int[] getChannelDimLengths() {
    FormatTools.assertId(currentId, true, 1);
    return intensity ? new int[] {channels} : new int[] {timeBins, channels};
  }

  /* @see loci.formats.IFormatReader#getChannelDimTypes() */
  public String[] getChannelDimTypes() {
    FormatTools.assertId(currentId, true, 1);
    return intensity ? new String[] {FormatTools.SPECTRA} :
      new String[] {FormatTools.LIFETIME, FormatTools.SPECTRA};
  }

  /* @see loci.formats.IFormatReader#isInterleaved(int) */
  public boolean isInterleaved(int subC) {
    FormatTools.assertId(currentId, true, 1);
    return !intensity && subC == 0;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length, w, h);

    int sizeX = core.sizeX[series];
    int sizeY = core.sizeY[series];
    int bpp = FormatTools.getBytesPerPixel(core.pixelType[series]);
    boolean little = core.littleEndian[series];

    boolean direct = !intensity && x == 0 && y == 0 && w == sizeX && h == sizeY;
    byte[] b = direct ? buf : new byte[sizeY * sizeX * timeBins * bpp];
    in.seek(off + no * b.length);
    in.readFully(b);
    if (direct) return buf; // no cropping required

    int scan = intensity ? bpp : timeBins * bpp;

    for (int row=0; row<h; row++) {
      int yi = (y + row) * sizeX * timeBins * bpp;
      int ri = row * w * scan;
      for (int col=0; col<w; col++) {
        int xi = yi + (x + col) * timeBins * bpp;
        int ci = ri + col * scan;
        if (intensity) {
          // combine all lifetime bins into single intensity value
          short sum = 0;
          for (int t=0; t<timeBins; t++) {
            sum += DataTools.bytesToShort(b, xi + t * bpp, little);
          }
          DataTools.unpackShort(sum, buf, ci, little);
        }
        else {
          // each lifetime bin is a separate interleaved "channel"
          System.arraycopy(b, xi, buf, ci, scan);
        }
      }
    }
    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    off = timeBins = channels = 0;
    info = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
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

    core.sizeX[0] = info.width;
    core.sizeY[0] = info.height;
    core.sizeZ[0] = 1;
    core.sizeC[0] = intensity ? channels : timeBins * channels;
    core.sizeT[0] = 1;
    core.currentOrder[0] = "XYZTC";
    core.pixelType[0] = FormatTools.UINT16;
    core.rgb[0] = !intensity;
    core.littleEndian[0] = true;
    core.imageCount[0] = channels;
    core.indexed[0] = false;
    core.falseColor[0] = false;
    core.metadataComplete[0] = true;

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    store.setImageName("", 0);
    MetadataTools.setDefaultCreationDate(store, id, 0);
    MetadataTools.populatePixels(store, this);
    // CTR CHECK
//    for (int i=0; i<core.sizeC[0]; i++) {
//      store.setLogicalChannel(i, null, null, null, null, null, null, null,
//        null, null, null, null, null, null, null, null, null, null, null, null,
//        null, null, null, null, null);
//    }
  }

}
