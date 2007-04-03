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

  /** Minimum values for each channel. */
  protected double[][] chanMin;

  /** Maximum values for each channel. */
  protected double[][] chanMax;

  /** Minimum values for each plane. */
  protected double[][] planeMin;

  /** Maximum values for each plane. */
  protected double[][] planeMax;

  /** Number of planes for which min/max computations have been completed. */
  protected int[] minMaxDone;

  // -- Constructors --


  // -- IFormatReader API methods -- 

  /* @see loci.formats.IFormatReader#openImage(String, int) */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    BufferedImage b = super.openImage(id, no);
    updateMinMax(b, no);
    return b;
  }

  /* @see loci.formats.IFormatReader#openBytes(String, int) */
  public byte[] openBytes(String id, int no) throws FormatException, IOException
  {
    byte[] b = super.openBytes(id, no);
    updateMinMax(b, no);
    return b;
  }

  /* @see loci.formats.IFormatReader#openBytes(String, int, byte[]) */
  public byte[] openBytes(String id, int no, byte[] buf)
    throws FormatException, IOException
  {
    byte[] b = super.openBytes(id, no, buf);
    updateMinMax(b, no);
    return b;
  }

  // -- MinMaxCalculator API methods --

  /**
   * Retrieves a specified channel's global minimum.
   * Returns null if some of the image planes have not been read.
   */
  public Double getChannelGlobalMinimum(String id, int theC)
    throws FormatException, IOException
  {
    if (theC < 0 || theC >= getSizeC(id)) {
      throw new FormatException("Invalid channel index: " + theC);
    }
  
    // check that all planes have been reade 
    if (minMaxDone == null || minMaxDone[getSeries(id)] < getImageCount(id)) {
      return null;
    }
    return new Double(chanMin[getSeries(id)][theC]);
  }

  /**
   * Retrieves a specified channel's global maximum.
   * Returns null if some of the image planes have not been read.
   */
  public Double getChannelGlobalMaximum(String id, int theC)
    throws FormatException, IOException
  {
    if (theC < 0 || theC >= getSizeC(id)) {
      throw new FormatException("Invalid channel index: " + theC);
    }
  
    // check that all planes have been reade
    if (minMaxDone == null || minMaxDone[getSeries(id)] < getImageCount(id)) {
      return null;
    }
    return new Double(chanMax[getSeries(id)][theC]); 
  }

  /**
   * Retrieves the specified channel's minimum based on the images that have
   * been read.  Returns null if no image planes have been read yet.
   */
  public Double getChannelKnownMinimum(String id, int theC)
    throws FormatException, IOException
  {
    return chanMin == null ? null : new Double(chanMin[getSeries(id)][theC]);
  }

  /**
   * Retrieves the specified channel's maximum based on the images that
   * have been read.  Returns null if no image planes have been read yet.
   */
  public Double getChannelKnownMaximum(String id, int theC)
    throws FormatException, IOException
  {
    return chanMax == null ? null : new Double(chanMax[getSeries(id)][theC]);
  }

  /**
   * Retrieves the minimum pixel value for the specified plane.  If each
   * image plane contains more than one channel (i.e.,
   * {@link #getRGBChannelCount(String)}), returns the maximum value for each
   * embedded channel.  Returns null if the plane has not already been read.
   */
  public Double[] getPlaneMinimum(String id, int no)
    throws FormatException, IOException
  {
    if (planeMin == null) return null;

    int numRGB = getRGBChannelCount(id);
    int pBase = no * numRGB;
    if (planeMin[getSeries(id)][pBase] != planeMin[getSeries(id)][pBase]) {
      return null;
    } 

    Double[] min = new Double[numRGB];
    for (int c=0; c<numRGB; c++) {
      min[c] = new Double(planeMin[getSeries(id)][pBase + c]);
    }
    return min; 
  }

  /**
   * Retrieves the maximum pixel value for the specified plane.  If each
   * image plane contains more than one channel (i.e.,
   * {@link #getRGBChannelCount(String)}), returns the maximum value for each
   * embedded channel.  Returns null if the plane has not already been read.
   */
  public Double[] getPlaneMaximum(String id, int no)
    throws FormatException, IOException
  {
    if (planeMax == null) return null;

    int numRGB = getRGBChannelCount(id);
    int pBase = no * numRGB;
    if (planeMax[getSeries(id)][pBase] != planeMax[getSeries(id)][pBase]) {
      return null;
    } 

    Double[] max = new Double[numRGB];
    for (int c=0; c<numRGB; c++) {
      max[c] = new Double(planeMax[getSeries(id)][pBase + c]);
    }
    return max; 
  }

  /** 
   * Returns true if the values returned by 
   * getChannelGlobalMinimum/Maximum can be trusted.
   */
  public boolean isMinMaxPopulated(String id) 
    throws FormatException, IOException
  {
    return minMaxDone != null && minMaxDone[getSeries(id)] == getImageCount(id);
  }

  // -- Helper methods --

  /** Updates min/max values based on the given BufferedImage. */
  private void updateMinMax(BufferedImage b, int ndx)
    throws FormatException, IOException
  {
    if (b == null) return;
    String id = getCurrentFile(); 
    initMinMax();

    // check whether min/max values have already been computed for this plane
    if (planeMin[getSeries(id)][ndx] == planeMin[getSeries(id)][ndx]) return;

    int[] coords = getZCTCoords(getCurrentFile(), ndx);
    int numRGB = getRGBChannelCount(getCurrentFile());
    int cBase = coords[1] * numRGB;
    int pBase = ndx * numRGB;
    for (int c=0; c<numRGB; c++) {
      planeMin[getSeries(id)][pBase + c] = Double.POSITIVE_INFINITY;
      planeMax[getSeries(id)][pBase + c] = Double.NEGATIVE_INFINITY;
    }
 
    WritableRaster pixels = b.getRaster();
    for (int x=0; x<b.getWidth(); x++) {
      for (int y=0; y<b.getHeight(); y++) {
        for (int c=0; c<numRGB; c++) {
          double v = pixels.getSampleDouble(x, y, c);
          if (v > chanMax[getSeries(id)][cBase + c]) {
            chanMax[getSeries(id)][cBase + c] = v;
          } 
          if (v < chanMin[getSeries(id)][cBase + c]) {
            chanMin[getSeries(id)][cBase + c] = v;
          } 
          if (v > planeMax[getSeries(id)][pBase + c]) {
            planeMax[getSeries(id)][pBase + c] = v;
          } 
          if (v < planeMin[getSeries(id)][pBase + c]) {
            planeMin[getSeries(id)][pBase + c] = v;
          } 
        }
      }
    }
  
    minMaxDone[getSeries(id)]++; 
  }
  
  /** Updates min/max values based on the given byte array. */
  private void updateMinMax(byte[] b, int ndx)
    throws FormatException, IOException
  {
    if (b == null) return;
    String id = getCurrentFile(); 
    initMinMax();

    // check whether min/max values have already been computed for this plane
    if (planeMin[getSeries(id)][ndx] == planeMin[getSeries(id)][ndx]) return;

    boolean little = isLittleEndian(getCurrentFile());
    int bytes = FormatTools.getBytesPerPixel(getPixelType(getCurrentFile()));
    int pixels = getSizeX(getCurrentFile()) * getSizeY(getCurrentFile());
    boolean interleaved = isInterleaved(getCurrentFile());

    int[] coords = getZCTCoords(getCurrentFile(), ndx);
    int numRGB = getRGBChannelCount(getCurrentFile());
    int cBase = coords[1] * numRGB;
    int pBase = ndx * numRGB;
    for (int c=0; c<numRGB; c++) {
      planeMin[getSeries(id)][pBase + c] = Double.POSITIVE_INFINITY;
      planeMax[getSeries(id)][pBase + c] = Double.NEGATIVE_INFINITY;
    }
   
    byte[] value = new byte[bytes];
    for (int i=0; i<pixels; i++) {
      for (int c=0; c<numRGB; c++) {
        int idx = bytes * (interleaved ? i * numRGB + c : c * pixels + i);
        System.arraycopy(b, idx, value, 0, bytes);
        long bits = DataTools.bytesToLong(value, little);
        double v = Double.longBitsToDouble(bits);
        if (v > chanMax[getSeries(id)][cBase + c]) {
          chanMax[getSeries(id)][cBase + c] = v;
        } 
        if (v < chanMin[getSeries(id)][cBase + c]) {
          chanMin[getSeries(id)][cBase + c] = v;
        } 
        if (v > planeMax[getSeries(id)][pBase + c]) {
          planeMax[getSeries(id)][pBase + c] = v;
        } 
        if (v < planeMin[getSeries(id)][pBase + c]) {
          planeMin[getSeries(id)][pBase + c] = v;
        } 
     }
    }
  
    minMaxDone[getSeries(id)]++; 
  }

  /** Ensures internal min/max variables are initialized properly. */
  private void initMinMax() throws FormatException, IOException {
    int seriesCount = getSeriesCount(getCurrentFile());
    String id = getCurrentFile(); 

    if (chanMin == null) {
      chanMin = new double[seriesCount][];
      for (int i=0; i<seriesCount; i++) {
        chanMin[i] = new double[getSizeC(id)];
        Arrays.fill(chanMin[i], Double.POSITIVE_INFINITY);
      }
    }
    if (chanMax == null) {
      chanMax = new double[seriesCount][];
      for (int i=0; i<seriesCount; i++) {
        chanMax[i] = new double[getSizeC(id)];
        Arrays.fill(chanMax[i], Double.NEGATIVE_INFINITY);
      }
    }
    if (planeMin == null) { 
      planeMin = new double[seriesCount][];
      int oldSeries = getSeries(id);
      for (int i=0; i<seriesCount; i++) {
        setSeries(getCurrentFile(), i);
        int numRGB = getRGBChannelCount(getCurrentFile());
        planeMin[i] = new double[getImageCount(getCurrentFile()) * numRGB];
        Arrays.fill(planeMin[i], Double.NaN);
      }
      setSeries(getCurrentFile(), oldSeries); 
    }
    if (planeMax == null) { 
      planeMax = new double[seriesCount][];
      int oldSeries = getSeries(id);
      for (int i=0; i<seriesCount; i++) {
        setSeries(getCurrentFile(), i);
        int numRGB = getRGBChannelCount(getCurrentFile());
        planeMax[i] = new double[getImageCount(getCurrentFile()) * numRGB];
        Arrays.fill(planeMax[i], Double.NaN);
      }
      setSeries(getCurrentFile(), oldSeries); 
    }
    if (minMaxDone == null) minMaxDone = new int[seriesCount];

  }

}
