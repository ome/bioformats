//
// MinMaxCalculator.java
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

package loci.formats;

import java.awt.image.*;
import java.io.IOException;
import java.util.*;

/** Logic to compute minimum and maximum values for each channel. */
public class MinMaxCalculator extends ReaderWrapper {

  // -- Fields --

  /** Min values for each channel. */
  protected double[][] chanMin;

  /** Max values for each channel. */
  protected double[][] chanMax;

  /** Min values for each plane. */
  protected double[][] planeMin;

  /** Max values for each plane. */
  protected double[][] planeMax;

  /** Number of planes for which min/max computations have been completed. */
  protected int[] minMaxDone;

  // -- Constructors --

  /** Constructs a MinMaxCalculator around a new image reader. */
  public MinMaxCalculator() { super(); }

  /** Constructs a MinMaxCalculator with the given reader. */
  public MinMaxCalculator(IFormatReader r) { super(r); }

  // -- MinMaxCalculator API methods --

  /**
   * Retrieves a specified channel's global minimum.
   * Returns null if some of the image planes have not been read.
   */
  public Double getChannelGlobalMinimum(int theC)
    throws FormatException, IOException
  {
    FormatTools.assertId(getCurrentFile(), true, 2);
    if (theC < 0 || theC >= getSizeC()) {
      throw new FormatException("Invalid channel index: " + theC);
    }

    // check that all planes have been reade
    if (minMaxDone == null || minMaxDone[getSeries()] < getImageCount()) {
      return null;
    }
    return new Double(chanMin[getSeries()][theC]);
  }

  /**
   * Retrieves a specified channel's global maximum.
   * Returns null if some of the image planes have not been read.
   */
  public Double getChannelGlobalMaximum(int theC)
    throws FormatException, IOException
  {
    FormatTools.assertId(getCurrentFile(), true, 2);
    if (theC < 0 || theC >= getSizeC()) {
      throw new FormatException("Invalid channel index: " + theC);
    }

    // check that all planes have been reade
    if (minMaxDone == null || minMaxDone[getSeries()] < getImageCount()) {
      return null;
    }
    return new Double(chanMax[getSeries()][theC]);
  }

  /**
   * Retrieves the specified channel's minimum based on the images that have
   * been read.  Returns null if no image planes have been read yet.
   */
  public Double getChannelKnownMinimum(int theC)
    throws FormatException, IOException
  {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return chanMin == null ? null : new Double(chanMin[getSeries()][theC]);
  }

  /**
   * Retrieves the specified channel's maximum based on the images that
   * have been read.  Returns null if no image planes have been read yet.
   */
  public Double getChannelKnownMaximum(int theC)
    throws FormatException, IOException
  {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return chanMax == null ? null : new Double(chanMax[getSeries()][theC]);
  }

  /**
   * Retrieves the minimum pixel value for the specified plane.  If each
   * image plane contains more than one channel (i.e.,
   * {@link #getRGBChannelCount(String)}), returns the maximum value for each
   * embedded channel.  Returns null if the plane has not already been read.
   */
  public Double[] getPlaneMinimum(int no) throws FormatException, IOException {
    FormatTools.assertId(getCurrentFile(), true, 2);
    if (planeMin == null) return null;

    int numRGB = getRGBChannelCount();
    int pBase = no * numRGB;
    if (planeMin[getSeries()][pBase] != planeMin[getSeries()][pBase]) {
      return null;
    }

    Double[] min = new Double[numRGB];
    for (int c=0; c<numRGB; c++) {
      min[c] = new Double(planeMin[getSeries()][pBase + c]);
    }
    return min;
  }

  /**
   * Retrieves the maximum pixel value for the specified plane.  If each
   * image plane contains more than one channel (i.e.,
   * {@link #getRGBChannelCount(String)}), returns the maximum value for each
   * embedded channel.  Returns null if the plane has not already been read.
   */
  public Double[] getPlaneMaximum(int no) throws FormatException, IOException {
    FormatTools.assertId(getCurrentFile(), true, 2);
    if (planeMax == null) return null;

    int numRGB = getRGBChannelCount();
    int pBase = no * numRGB;
    if (planeMax[getSeries()][pBase] != planeMax[getSeries()][pBase]) {
      return null;
    }

    Double[] max = new Double[numRGB];
    for (int c=0; c<numRGB; c++) {
      max[c] = new Double(planeMax[getSeries()][pBase + c]);
    }
    return max;
  }

  /**
   * Returns true if the values returned by
   * getChannelGlobalMinimum/Maximum can be trusted.
   */
  public boolean isMinMaxPopulated() throws FormatException, IOException {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return minMaxDone != null && minMaxDone[getSeries()] == getImageCount();
  }

  // -- IFormatReader API methods --

  /* @see IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    FormatTools.assertId(getCurrentFile(), true, 2);
    BufferedImage b = super.openImage(no);
    updateMinMax(b, no);
    return b;
  }

  /* @see IFormatReader#openBytes(int) */
  public byte[] openBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(getCurrentFile(), true, 2);
    byte[] b = super.openBytes(no);
    updateMinMax(b, no);
    return b;
  }

  /* @see IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(getCurrentFile(), true, 2);
    super.openBytes(no, buf);
    updateMinMax(buf, no);
    return buf;
  }

  /* @see IFormatReader#close() */
  public void close() throws IOException {
    reader.close();
    chanMin = null;
    chanMax = null;
    planeMin = null;
    planeMax = null;
    minMaxDone = null;
  }

  // -- Helper methods --

  /** Updates min/max values based on the given BufferedImage. */
  protected void updateMinMax(BufferedImage b, int ndx)
    throws FormatException, IOException
  {
    if (b == null) return;
    initMinMax();

    int numRGB = getRGBChannelCount();

    // check whether min/max values have already been computed for this plane
    if (planeMin[getSeries()][ndx * numRGB] ==
      planeMin[getSeries()][ndx * numRGB])
    {
      return;
    }

    int[] coords = getZCTCoords(ndx);
    int cBase = coords[1] * numRGB;
    int pBase = ndx * numRGB;
    for (int c=0; c<numRGB; c++) {
      planeMin[getSeries()][pBase + c] = Double.POSITIVE_INFINITY;
      planeMax[getSeries()][pBase + c] = Double.NEGATIVE_INFINITY;
    }

    WritableRaster pixels = b.getRaster();
    for (int x=0; x<b.getWidth(); x++) {
      for (int y=0; y<b.getHeight(); y++) {
        for (int c=0; c<numRGB; c++) {
          double v = pixels.getSampleDouble(x, y, c);
          if (v > chanMax[getSeries()][cBase + c]) {
            chanMax[getSeries()][cBase + c] = v;
          }
          if (v < chanMin[getSeries()][cBase + c]) {
            chanMin[getSeries()][cBase + c] = v;
          }
          if (v > planeMax[getSeries()][pBase + c]) {
            planeMax[getSeries()][pBase + c] = v;
          }
          if (v < planeMin[getSeries()][pBase + c]) {
            planeMin[getSeries()][pBase + c] = v;
          }
        }
      }
    }

    minMaxDone[getSeries()]++;

    if (minMaxDone[getSeries()] == getImageCount()) {
      MetadataStore store = getMetadataStore();
      for (int c=0; c<getSizeC(); c++) {
        store.setChannelGlobalMinMax(c, new Double(chanMin[getSeries()][c]),
          new Double(chanMax[getSeries()][c]), new Integer(getSeries()));
      }
    }
  }

  /** Updates min/max values based on the given byte array. */
  protected void updateMinMax(byte[] b, int ndx)
    throws FormatException, IOException
  {
    if (b == null) return;
    initMinMax();

    int numRGB = getRGBChannelCount();

    // check whether min/max values have already been computed for this plane
    if (planeMin[getSeries()][ndx * numRGB] ==
      planeMin[getSeries()][ndx * numRGB])
    {
      return;
    }

    boolean little = isLittleEndian();
    int bytes = FormatTools.getBytesPerPixel(getPixelType());
    int pixels = getSizeX() * getSizeY();
    boolean interleaved = isInterleaved();

    int[] coords = getZCTCoords(ndx);
    int cBase = coords[1] * numRGB;
    int pBase = ndx * numRGB;
    for (int c=0; c<numRGB; c++) {
      planeMin[getSeries()][pBase + c] = Double.POSITIVE_INFINITY;
      planeMax[getSeries()][pBase + c] = Double.NEGATIVE_INFINITY;
    }

    boolean fp = getPixelType() == FormatTools.FLOAT ||
      getPixelType() == FormatTools.DOUBLE;

    for (int i=0; i<pixels; i++) {
      for (int c=0; c<numRGB; c++) {
        int idx = bytes * (interleaved ? i * numRGB + c : c * pixels + i);
        long bits = DataTools.bytesToLong(b, idx, bytes, little);
        double v = fp ? Double.longBitsToDouble(bits) : (double) bits;
        if (v > chanMax[getSeries()][cBase + c]) {
          chanMax[getSeries()][cBase + c] = v;
        }
        if (v < chanMin[getSeries()][cBase + c]) {
          chanMin[getSeries()][cBase + c] = v;
        }
        if (v > planeMax[getSeries()][ndx]) {
          planeMax[getSeries()][ndx] = v;
        }
        if (v < planeMin[getSeries()][pBase + c]) {
          planeMin[getSeries()][pBase + c] = v;
        }
      }
    }

    minMaxDone[getSeries()]++;

    if (minMaxDone[getSeries()] == getImageCount()) {
      MetadataStore store = getMetadataStore();
      for (int c=0; c<getSizeC(); c++) {
        store.setChannelGlobalMinMax(c, new Double(chanMin[getSeries()][c]),
          new Double(chanMax[getSeries()][c]), new Integer(getSeries()));
      }
    }
  }

  /** Ensures internal min/max variables are initialized properly. */
  protected void initMinMax() throws FormatException, IOException {
    int seriesCount = getSeriesCount();

    if (chanMin == null) {
      chanMin = new double[seriesCount][];
      for (int i=0; i<seriesCount; i++) {
        chanMin[i] = new double[getSizeC()];
        Arrays.fill(chanMin[i], Double.POSITIVE_INFINITY);
      }
    }
    if (chanMax == null) {
      chanMax = new double[seriesCount][];
      for (int i=0; i<seriesCount; i++) {
        chanMax[i] = new double[getSizeC()];
        Arrays.fill(chanMax[i], Double.NEGATIVE_INFINITY);
      }
    }
    if (planeMin == null) {
      planeMin = new double[seriesCount][];
      int oldSeries = getSeries();
      for (int i=0; i<seriesCount; i++) {
        setSeries(i);
        int numRGB = getRGBChannelCount();
        planeMin[i] = new double[getImageCount() * numRGB];
        Arrays.fill(planeMin[i], Double.NaN);
      }
      setSeries(oldSeries);
    }
    if (planeMax == null) {
      planeMax = new double[seriesCount][];
      int oldSeries = getSeries();
      for (int i=0; i<seriesCount; i++) {
        setSeries(i);
        int numRGB = getRGBChannelCount();
        planeMax[i] = new double[getImageCount() * numRGB];
        Arrays.fill(planeMax[i], Double.NaN);
      }
      setSeries(oldSeries);
    }
    if (minMaxDone == null) minMaxDone = new int[seriesCount];
  }

  // -- Deprecated IFormatReader API methods --

  /** @deprecated Replaced by {@link #openImage(int)} */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    setId(id);
    BufferedImage b = super.openImage(no);
    updateMinMax(b, no);
    return b;
  }

  /** @deprecated Replaced by {@link #openBytes(int)} */
  public byte[] openBytes(String id, int no) throws FormatException, IOException
  {
    setId(id);
    byte[] b = super.openBytes(no);
    updateMinMax(b, no);
    return b;
  }

  /** @deprecated Replaced by {@link #openBytes(int, byte[])} */
  public byte[] openBytes(String id, int no, byte[] buf)
    throws FormatException, IOException
  {
    setId(id);
    super.openBytes(no, buf);
    updateMinMax(buf, no);
    return buf;
  }

}
