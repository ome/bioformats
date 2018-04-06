/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

package loci.formats;

import java.io.IOException;
import java.util.Arrays;

import loci.common.DataTools;
import loci.formats.meta.IMinMaxStore;

/**
 * Logic to compute minimum and maximum values for each channel.
 */
public class MinMaxCalculator extends ReaderWrapper {

  // -- Utility methods --

  /** Converts the given reader into a MinMaxCalculator, wrapping if needed. */
  public static MinMaxCalculator makeMinMaxCalculator(IFormatReader r) {
    if (r instanceof MinMaxCalculator) return (MinMaxCalculator) r;
    return new MinMaxCalculator(r);
  }

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

  /** Consumer of channel global minima and maxima */
  protected IMinMaxStore minMaxStore;

  // -- Constructors --

  /** Constructs a MinMaxCalculator around a new image reader. */
  public MinMaxCalculator() { super(); }

  /** Constructs a MinMaxCalculator with the given reader. */
  public MinMaxCalculator(IFormatReader r) { super(r); }

  /**
   * Sets the active min-max store for the calculator. Whenever a channel's
   * global minimum and maximum calculation has been completed this store is
   * notified.
   * @param store See above.
   */
  public void setMinMaxStore(IMinMaxStore store) {
    minMaxStore = store;
  }

  /**
   * Retrieves the current active min-max store for the calculator.
   * @return See above.
   */
  public IMinMaxStore getMinMaxStore() {
    return minMaxStore;
  }

  // -- MinMaxCalculator API methods --

  /**
   * Retrieves a specified channel's global minimum.
   * Returns null if some of the image planes have not been read.
   *
   * @throws IOException Not actually thrown.
   */
  public Double getChannelGlobalMinimum(int theC)
    throws FormatException, IOException
  {
    FormatTools.assertId(getCurrentFile(), true, 2);
    if (theC < 0 || theC >= getSizeC()) {
      throw new FormatException("Invalid channel index: " + theC);
    }

    int series = getCoreIndex();

    // check that all planes have been read
    if (minMaxDone == null || minMaxDone[series] < getImageCount()) {
      return null;
    }
    return new Double(chanMin[series][theC]);
  }

  /**
   * Retrieves a specified channel's global maximum.
   * Returns null if some of the image planes have not been read.
   * @throws IOException Not actually thrown.
   */
  public Double getChannelGlobalMaximum(int theC)
    throws FormatException, IOException
  {
    FormatTools.assertId(getCurrentFile(), true, 2);
    if (theC < 0 || theC >= getSizeC()) {
      throw new FormatException("Invalid channel index: " + theC);
    }

    int series = getCoreIndex();

    // check that all planes have been read
    if (minMaxDone == null || minMaxDone[series] < getImageCount()) {
      return null;
    }
    return new Double(chanMax[series][theC]);
  }

  /**
   * Retrieves the specified channel's minimum based on the images that have
   * been read.  Returns null if no image planes have been read yet.
   *
   * @throws FormatException Not actually thrown.
   * @throws IOException Not actually thrown.
   */
  public Double getChannelKnownMinimum(int theC)
    throws FormatException, IOException
  {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return chanMin == null ? null : new Double(chanMin[getCoreIndex()][theC]);
  }

  /**
   * Retrieves the specified channel's maximum based on the images that
   * have been read.  Returns null if no image planes have been read yet.
   *
   * @throws FormatException Not actually thrown.
   * @throws IOException Not actually thrown.
   */
  public Double getChannelKnownMaximum(int theC)
    throws FormatException, IOException
  {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return chanMax == null ? null : new Double(chanMax[getCoreIndex()][theC]);
  }

  /**
   * Retrieves the minimum pixel value for the specified plane.
   * If each image plane contains more than one channel (i.e.,
   * {@link #getRGBChannelCount()} &gt; 1), returns the maximum value for each
   * embedded channel. Returns null if the plane has not already been read.
   *
   * @throws FormatException Not actually thrown.
   * @throws IOException Not actually thrown.
   */
  public Double[] getPlaneMinimum(int no) throws FormatException, IOException {
    FormatTools.assertId(getCurrentFile(), true, 2);
    if (planeMin == null) return null;

    int numRGB = getRGBChannelCount();
    int pBase = no * numRGB;
    int series = getCoreIndex();
    if (Double.isNaN(planeMin[series][pBase])) return null;

    Double[] min = new Double[numRGB];
    for (int c=0; c<numRGB; c++) {
      min[c] = new Double(planeMin[series][pBase + c]);
    }
    return min;
  }

  /**
   * Retrieves the maximum pixel value for the specified plane.
   * If each image plane contains more than one channel (i.e.,
   * {@link #getRGBChannelCount()} &gt; 1), returns the maximum value for each
   * embedded channel. Returns null if the plane has not already been read.
   *
   * @throws FormatException Not actually thrown.
   * @throws IOException Not actually thrown.
   */
  public Double[] getPlaneMaximum(int no) throws FormatException, IOException {
    FormatTools.assertId(getCurrentFile(), true, 2);
    if (planeMax == null) return null;

    int numRGB = getRGBChannelCount();
    int pBase = no * numRGB;
    int series = getCoreIndex();
    if (Double.isNaN(planeMax[series][pBase])) return null;

    Double[] max = new Double[numRGB];
    for (int c=0; c<numRGB; c++) {
      max[c] = new Double(planeMax[series][pBase + c]);
    }
    return max;
  }

  /**
   * Returns true if the values returned by
   * getChannelGlobalMinimum/Maximum can be trusted.
   *
   * @throws FormatException Not actually thrown.
   * @throws IOException Not actually thrown.
   */
  public boolean isMinMaxPopulated() throws FormatException, IOException {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return minMaxDone != null && minMaxDone[getCoreIndex()] == getImageCount();
  }

  // -- IFormatReader API methods --

  /* @see IFormatReader#openBytes(int) */
  @Override
  public byte[] openBytes(int no) throws FormatException, IOException {
    return openBytes(no, 0, 0, getSizeX(), getSizeY());
  }

  /* @see IFormatReader#openBytes(int, byte[]) */
  @Override
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    return openBytes(no, buf, 0, 0, getSizeX(), getSizeY());
  }

  /* @see IFormatReader#openBytes(int, int, int, int, int) */
  @Override
  public byte[] openBytes(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    byte[] buf = new byte[w * h * getRGBChannelCount() *
      FormatTools.getBytesPerPixel(getPixelType())];
    return openBytes(no, buf, x, y, w, h);
  }

  /* @see IFormatReader#openBytes(int, byte[], int, int, int, int) */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(getCurrentFile(), true, 2);
    super.openBytes(no, buf, x, y, w, h);
    
    updateMinMax(no, buf, FormatTools.getBytesPerPixel(getPixelType()) * w * h);
    return buf;
  }

  /* @see IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    reader.close(fileOnly);
    if (!fileOnly) {
      chanMin = null;
      chanMax = null;
      planeMin = null;
      planeMax = null;
      minMaxDone = null;
    }
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#getNativeDataType() */
  @Override
  public Class<?> getNativeDataType() {
    return byte[].class;
  }

  // -- Helper methods --

  /**
   * Updates min/max values based on the given byte array.
   * @param no the image index within the file.
   * @param buf a pre-allocated buffer.
   * @param len as <code>buf</code> may be larger than the actual pixel count
   * having been written to it, the length (in bytes) of the those pixels.
   */
  protected void updateMinMax(int no, byte[] buf, int len)
    throws FormatException, IOException
  {
    if (buf == null) return;
    initMinMax();

    int numRGB = getRGBChannelCount();
    int series = getCoreIndex();
    int pixelType = getPixelType();
    int bpp = FormatTools.getBytesPerPixel(pixelType);
    int planeSize = getSizeX() * getSizeY() * bpp;
    // check whether min/max values have already been computed for this plane
    // and that the buffer requested is actually the entire plane
    if (len == planeSize
        && !Double.isNaN(planeMin[series][no * numRGB])) return;

    boolean little = isLittleEndian();
    
    int pixels = len / (bpp * numRGB);
    boolean interleaved = isInterleaved();

    int[] coords = getZCTCoords(no);
    int cBase = coords[1] * numRGB;
    int pBase = no * numRGB;
    for (int c=0; c<numRGB; c++) {
      planeMin[series][pBase + c] = Double.POSITIVE_INFINITY;
      planeMax[series][pBase + c] = Double.NEGATIVE_INFINITY;
    }

    boolean signed = FormatTools.isSigned(pixelType);

    long threshold = (long) Math.pow(2, bpp * 8 - 1);
    for (int i=0; i<pixels; i++) {
      for (int c=0; c<numRGB; c++) {
        int idx = bpp * (interleaved ? i * numRGB + c : c * pixels + i);
        long bits = DataTools.bytesToLong(buf, idx, bpp, little);
        if (signed) {
          if (bits >= threshold) bits -= 2*threshold;
        }
        double v = bits;
        if (pixelType == FormatTools.FLOAT) {
          v = Float.intBitsToFloat((int) bits);
        }
        else if (pixelType == FormatTools.DOUBLE) {
          v = Double.longBitsToDouble(bits);
        }

        if (v > chanMax[series][cBase + c]) {
          chanMax[series][cBase + c] = v;
        }
        if (v < chanMin[series][cBase + c]) {
          chanMin[series][cBase + c] = v;
        }
      }
    }

    for (int c=0; c<numRGB; c++) {
      if (chanMin[series][cBase + c] < planeMin[series][pBase + c]) {
        planeMin[series][pBase + c] = chanMin[series][cBase + c];
      }
      if (chanMax[series][cBase + c] > planeMax[series][pBase + c]) {
        planeMax[series][pBase + c] = chanMax[series][cBase + c];
      }
    }
    minMaxDone[series] = Math.max(minMaxDone[series], no + 1);

    if (minMaxDone[getCoreIndex()] == getImageCount() && minMaxStore != null) {
      for (int c=0; c<getSizeC(); c++) {
        minMaxStore.setChannelGlobalMinMax(c, chanMin[getCoreIndex()][c],
          chanMax[getCoreIndex()][c], getSeries());
      }
    }
  }

  /**
   * Ensures internal min/max variables are initialized properly. 
   *
   * @throws FormatException Not actually thrown.
   * @throws IOException Not actually thrown.
   */
  protected void initMinMax() throws FormatException, IOException {
    // call getCoreMetadataList() on base reader to prevent CoreMetadata from being cloned
    int seriesCount = unwrap().getCoreMetadataList().size();
    int oldSeries = getCoreIndex();

    if (chanMin == null) {
      chanMin = new double[seriesCount][];
      for (int i=0; i<seriesCount; i++) {
        setCoreIndex(i);
        chanMin[i] = new double[getSizeC()];
        Arrays.fill(chanMin[i], Double.POSITIVE_INFINITY);
      }
      setCoreIndex(oldSeries);
    }
    if (chanMax == null) {
      chanMax = new double[seriesCount][];
      for (int i=0; i<seriesCount; i++) {
        setCoreIndex(i);
        chanMax[i] = new double[getSizeC()];
        Arrays.fill(chanMax[i], Double.NEGATIVE_INFINITY);
      }
      setCoreIndex(oldSeries);
    }
    if (planeMin == null) {
      planeMin = new double[seriesCount][];
      for (int i=0; i<seriesCount; i++) {
        setCoreIndex(i);
        int numRGB = getRGBChannelCount();
        planeMin[i] = new double[getImageCount() * numRGB];
        Arrays.fill(planeMin[i], Double.NaN);
      }
      setCoreIndex(oldSeries);
    }
    if (planeMax == null) {
      planeMax = new double[seriesCount][];
      for (int i=0; i<seriesCount; i++) {
        setCoreIndex(i);
        int numRGB = getRGBChannelCount();
        planeMax[i] = new double[getImageCount() * numRGB];
        Arrays.fill(planeMax[i], Double.NaN);
      }
      setCoreIndex(oldSeries);
    }
    if (minMaxDone == null) minMaxDone = new int[seriesCount];
  }

}
