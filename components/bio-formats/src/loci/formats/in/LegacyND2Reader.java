//
// LegacyND2Reader.java
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

import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.MissingLibraryException;
import loci.formats.meta.MetadataStore;

/**
 * LegacyND2Reader is a file format reader for Nikon ND2 files that uses
 * the Nikon ND2 SDK - it is only usable on Windows machines.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/LegacyND2Reader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/LegacyND2Reader.java;hb=HEAD">Gitweb</a></dd></dl>
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

  private static final String URL_NIKON_ND2 =
    "http://www.loci.wisc.edu/bio-formats-format/nikon-nis-elements-nd2";
  private static final String NO_NIKON_MSG = "Nikon ND2 library not found. " +
    "Please see " + URL_NIKON_ND2 + " for details.";

  // -- Static initializers --

  private static boolean libraryFound = true;

  static {
    try {
      System.loadLibrary("LegacyND2Reader");
    }
    catch (UnsatisfiedLinkError e) {
      LOGGER.trace(NO_NIKON_MSG, e);
      libraryFound = false;
    }
  }

  // -- Constructor --

  public LegacyND2Reader() {
    super("Nikon ND2 (Legacy)", new String[] {"jp2", "nd2"});
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String file, boolean open) {
    return libraryFound && super.isThisType(file, open);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int[] zct = FormatTools.getZCTCoords(this, no);
    int bpc = FormatTools.getBytesPerPixel(getPixelType());
    byte[] b = new byte[FormatTools.getPlaneSize(this)];

    getImage(b, getSeries(), zct[0], zct[1], zct[2]);

    int pixel = bpc * getRGBChannelCount();
    int rowLen = w * pixel;
    for (int row=0; row<h; row++) {
      System.arraycopy(b, pixel * ((row + y) * getSizeX() + x), buf,
        row * rowLen, rowLen);
    }

    if (isRGB()) {
      int bpp = getSizeC() * bpc;
      int line = w * bpp;
      for (int row=0; row<h; row++) {
        for (int col=0; col<w; col++) {
          int base = row * line + col * bpp;
          for (int bb=0; bb<bpc; bb++) {
            byte blue = buf[base + bpc*(getSizeC() - 1) + bb];
            buf[base + bpc*(getSizeC() - 1) + bb] = buf[base + bb];
            buf[base + bb] = blue;
          }
        }
      }
    }
    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    try {
      openFile(id);
      int numSeries = getNumSeries();
      core = new CoreMetadata[numSeries];

      for (int i=0; i<numSeries; i++) {
        core[i] = new CoreMetadata();
        core[i].sizeX = getWidth(i);
        if (core[i].sizeX % 2 != 0) core[i].sizeX++;
        core[i].sizeY = getHeight(i);
        core[i].sizeZ = getZSlices(i);
        core[i].sizeT = getTFrames(i);
        core[i].sizeC = getChannels(i);
        int bytes = getBytesPerPixel(i);
        if (bytes % 3 == 0) {
          core[i].sizeC *= 3;
          bytes /= 3;
          core[i].rgb = true;
        }
        else core[i].rgb = false;

        core[i].pixelType = FormatTools.pixelTypeFromBytes(bytes, false, true);
        core[i].imageCount = core[i].sizeZ * core[i].sizeT;
        if (!core[i].rgb) core[i].imageCount *= core[i].sizeC;
        core[i].interleaved = true;
        core[i].littleEndian = true;
        core[i].dimensionOrder = "XYCZT";
        core[i].indexed = false;
        core[i].falseColor = false;
      }
    }
    catch (UnsatisfiedLinkError e) {
      throw new MissingLibraryException(NO_NIKON_MSG, e);
    }
    catch (Exception e) {
      throw new MissingLibraryException(NO_NIKON_MSG, e);
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
    for (int i=0; i<getSeriesCount(); i++) {
      store.setImageName("Series " + (i + 1), i);
    }
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
