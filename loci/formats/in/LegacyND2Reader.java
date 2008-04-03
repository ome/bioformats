//
// LegacyND2Reader.java
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

import java.io.*;
import java.util.Arrays;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * LegacyND2Reader is a file format reader for Nikon ND2 files that uses
 * the Nikon ND2 SDK - it is only usable on Windows machines.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/LegacyND2Reader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/LegacyND2Reader.java">SVN</a></dd></dl>
 */
public class LegacyND2Reader extends FormatReader {

  // -- Constants --

  /** Modality types. */
  private static final int WIDE_FIELD = 0;
  private static final int BRIGHT_FIELD = 1;
  private static final int LASER_SCAN_CONFOCAL = 2;
  private static final int SPIN_DISK_CONFOCAL = 3;
  private static final int SWEPT_FIELD_CONFOCAL = 4;
  private static final int MULTI_PHOTON = 5;

  private static final String NO_NIKON_MSG =
    "Nikon ND2 library not found.  Please see " +
    "http://loci.wisc.edu/ome/formats.html for details.";

  // -- Static initializers --

  private static boolean libraryFound = true;

  static {
    try {
      System.loadLibrary("LegacyND2Reader");
    }
    catch (UnsatisfiedLinkError e) {
      if (debug) LogTools.trace(e);
      libraryFound = false;
    }
  }

  // -- Constructor --

  public LegacyND2Reader() {
    super("Nikon ND2 (Legacy)", new String[] {"jp2", "nd2"});
  }

  // -- IFormatReader API methods --

  /* @see IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String file, boolean open) {
    return libraryFound && super.isThisType(file, open);
  }

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return false;
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

    int[] zct = FormatTools.getZCTCoords(this, no);
    int bpc = FormatTools.getBytesPerPixel(core.pixelType[series]);
    byte[] b = new byte[core.sizeX[series] * core.sizeY[series] * bpc *
      getRGBChannelCount()];

    getImage(b, getSeries(), zct[0], zct[1], zct[2]);

    int pixel = bpc * getRGBChannelCount();
    int rowLen = w * pixel;
    for (int row=0; row<h; row++) {
      System.arraycopy(b, (row + y) * core.sizeX[series] * pixel + x * pixel,
        buf, row * rowLen, rowLen);
    }

    if (core.rgb[series]) {
      int bpp = core.sizeC[series] * bpc;
      int line = w * bpp;
      for (int row=0; row<h; row++) {
        for (int col=0; col<w; col++) {
          for (int bb=0; bb<bpc; bb++) {
            byte blue =
              buf[row*line + col*bpp + bpc*(core.sizeC[series] - 1) + bb];
            buf[row*line + col*bpp + bpc*(core.sizeC[series] - 1) + bb] =
              buf[row*line + col*bpp + bb];
            buf[row*line + col*bpp + bb] = blue;
          }
        }
      }
    }
    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("LegacyND2Reader.initFile(" + id + ")");
    super.initFile(id);

    try {
      openFile(id);
      int numSeries = getNumSeries();
      core = new CoreMetadata(numSeries);

      for (int i=0; i<numSeries; i++) {
        core.sizeX[i] = getWidth(i);
        if (core.sizeX[i] % 2 != 0) core.sizeX[i]++;
        core.sizeY[i] = getHeight(i);
        core.sizeZ[i] = getZSlices(i);
        core.sizeT[i] = getTFrames(i);
        core.sizeC[i] = getChannels(i);
        int bytes = getBytesPerPixel(i);
        if (bytes % 3 == 0) {
          core.sizeC[i] *= 3;
          bytes /= 3;
          core.rgb[i] = true;
        }
        else core.rgb[i] = false;

        switch (bytes) {
          case 1:
            core.pixelType[i] = FormatTools.UINT8;
            break;
          case 2:
            core.pixelType[i] = FormatTools.UINT16;
            break;
          case 4:
            core.pixelType[i] = FormatTools.FLOAT;
            break;
        }
        core.imageCount[i] = core.sizeZ[i] * core.sizeT[i];
        if (!core.rgb[i]) core.imageCount[i] *= core.sizeC[i];
      }
      Arrays.fill(core.interleaved, true);
      Arrays.fill(core.littleEndian, true);
      Arrays.fill(core.currentOrder, "XYCZT");
      Arrays.fill(core.indexed, false);
      Arrays.fill(core.falseColor, false);
    }
    catch (Exception e) {
      throw new FormatException(NO_NIKON_MSG);
    }

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    for (int i=0; i<core.sizeX.length; i++) {
      store.setImageName("Series " + i, i);
      store.setImageCreationDate(
        DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), i);
    }
    MetadataTools.populatePixels(store, this);
    // CTR CHECK
//    for (int i=0; i<core.sizeX.length; i++) {
//      for (int j=0; j<core.sizeC[i]; j++) {
//        store.setLogicalChannel(j, null, null, null, null, null, null, null,
//          null, null, null, null, null, null, null, null, null, null, null,
//          null, null, null, null, null, new Integer(i));
//      }
//    }
  }

  // -- Native methods --

  public native void openFile(String filename);
  public native int getNumSeries();
  public native int getWidth(int s);
  public native int getHeight(int s);
  public native int getZSlices(int s);
  public native int getTFrames(int s);
  public native int getChannels(int s);
  public native int getBytesPerPixel(int s);
  public native byte[] getImage(byte[] buf, int s, int z, int c, int t);
  public native double getDX(int s, int z, int c, int t);
  public native double getDY(int s, int z, int c, int t);
  public native double getDZ(int s, int z, int c, int t);
  public native double getDT(int s, int z, int c, int t);
  public native double getWavelength(int s, int z, int c, int t);
  public native String getChannelName(int s, int z, int c, int t);
  public native double getMagnification(int s, int z, int c, int t);
  public native double getNA(int s, int z, int c, int t);
  public native String getObjectiveName(int s, int z, int c, int t);
  public native int getModality(int s, int z, int c, int t);

}
