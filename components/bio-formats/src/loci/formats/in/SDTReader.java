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
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

/**
 * SDTReader is the file format reader for
 * Becker &amp; Hickl SPC-Image SDT files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/SDTReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/SDTReader.java;hb=HEAD">Gitweb</a></dd></dl>
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
    domains = new String[] {FormatTools.FLIM_DOMAIN};
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
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int sizeX = getSizeX();
    int sizeY = getSizeY();
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    boolean little = isLittleEndian();

    // HACK
    // adjust the number of time bins if DimensionSwapper was used
    if (buf.length == sizeY * sizeX * bpp && timeBins > 1) {
      timeBins = 1;
    }

    int paddedWidth = sizeX + ((4 - (sizeX % 4)) % 4);
    int planeSize = paddedWidth * sizeY * timeBins * bpp;

    boolean direct = !intensity;
    byte[] b = direct ? buf : new byte[sizeY * sizeX * timeBins * bpp];
    in.seek(off + no * planeSize + y * paddedWidth * bpp * timeBins);

    for (int row=0; row<h; row++) {
      in.skipBytes(x * bpp * timeBins);
      in.read(b, row * bpp * timeBins * w, w * timeBins * bpp);
      in.skipBytes(bpp * timeBins * (paddedWidth - x - w));
    }

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
          DataTools.unpackBytes(sum, buf, ci, 2, little);
        }
        else {
          // each lifetime bin is a separate interleaved "channel"
          System.arraycopy(b, xi, buf, ci, scan);
        }
      }
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      off = timeBins = channels = 0;
      info = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    in.order(true);

    LOGGER.info("Reading header");

    // read file header information
    info = new SDTInfo(in, metadata);
    off = info.dataBlockOffs + 22;
    timeBins = info.timeBins;
    channels = info.channels;
    addGlobalMeta("time bins", timeBins);
    addGlobalMeta("channels", channels);
    addGlobalMeta("time base", 1e9 * info.tacR / info.tacG);

    LOGGER.info("Populating metadata");

    core[0].sizeX = info.width;
    core[0].sizeY = info.height;
    core[0].sizeZ = 1;
    core[0].sizeC = intensity ? channels : timeBins * channels;
    core[0].sizeT = 1;
    core[0].dimensionOrder = "XYZTC";
    core[0].pixelType = FormatTools.UINT16;
    core[0].rgb = !intensity && getSizeC() > 1;
    core[0].littleEndian = true;
    core[0].imageCount = channels;
    core[0].indexed = false;
    core[0].falseColor = false;
    core[0].metadataComplete = true;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

}
