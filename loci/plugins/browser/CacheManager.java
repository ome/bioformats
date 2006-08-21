//
// CacheManager.java
//

/*
LOCI 4D Data Browser package for quick browsing of 4D datasets in ImageJ.
Copyright (C) 2005-@year@ Francis Wong, Curtis Rueden and Melissa Linkert.

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

package loci.plugins.browser;

import ij.ImagePlus;
import ij.process.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Vector;
import loci.formats.*;

/** Manages the cache of planes for a virtual stack. */
public class CacheManager {

  // -- Fields --

  /** Number of planes to keep in the cache. */
  private int cacheSize = 1;

  /** Current position within the stack. */
  private int position = 1;

  /** Reader used to open images. */
  private FormatReader reader;

  /** Current file name. */
  private String filename;

  /** Cache of images. */
  private Vector cache;

  /** Plane numbers in the cache (in order). */
  private Vector numbers;

  /** Number of planes in the virtual stack. */
  private int stackSize = 1;

  /** Image dimensions. */
  private int x, y;

  // -- Constructor --

  public CacheManager(int size, FormatReader reader, String file) {
    this.reader = reader;
    try {
      if (reader.getImageCount(file) < size) size = reader.getImageCount(file);
      stackSize = reader.getImageCount(file);
    }
    catch (Exception e) { }
    cacheSize = size;
    cache = new Vector();
    numbers = new Vector();
    filename = file;
    position = 1;
    try {
      x = reader.getSizeX(filename);
      y = reader.getSizeY(filename);
    }
    catch (Exception e) { }
  }

  // -- CacheManager API methods --

  /** Get the current position. */
  public int getSlice() {
    return position;
  }

  /** Get the size of the cache. */
  public int getSize() {
    return cacheSize;
  }

  /** Set the number of images in the virtual stack. */
  public void setStackSize(int n) throws FormatException, IOException {
    if (n < reader.getImageCount(filename) && n >= 0) stackSize = n;
  }

  /** Get the specified slice. Updates the cache if necessary. */
  public ImageProcessor getSlice(int pos) {
    position = pos + 1;
    int p = getPosition(pos);
    if (p != -1) {
      return createProcessor((BufferedImage) cache.get(p), x, y);
    }

    updateCache(pos);
    p = getPosition(pos);
    return createProcessor((BufferedImage) cache.get(p), x, y);
  }

  /**
   * Return the position of a plane within the cache, or -1
   * if it doesn't exist.
   */
  public int getPosition(int no) {
    for (int i=0; i<numbers.size(); i++) {
      if (no == ((Integer) numbers.get(i)).intValue()) return i;
    }
    return -1;
  }

  /** Initialize the cache. */
  public void init() {
    try {
      for (int i=0; i<cacheSize; i++) {
        cache.add(reader.openImage(filename, i));
        numbers.add(new Integer(i));
      }
    }
    catch (Exception e) { }
  }

  // -- Helper methods --

  /** Update the cache. */
  private void updateCache(int pos) {
    int lowerBound = pos - cacheSize / 2;
    int upperBound = lowerBound + cacheSize;

    if (lowerBound < 0) {
      upperBound = cacheSize;
      lowerBound = 0;
    }

    if (upperBound >= stackSize) {
      lowerBound -= (upperBound - stackSize);
      upperBound = stackSize;
      if (lowerBound < 0) lowerBound = 0;
    }

    if (lowerBound >= ((Integer) numbers.get(0)).intValue() &&
      lowerBound <= ((Integer) numbers.get(cacheSize - 1)).intValue())
    {
      try {
       lowerBound =
          ((Integer) numbers.get(cacheSize - 1)).intValue() + 1;
        for (int i=lowerBound; i<upperBound; i++) {
          cache.add(reader.openImage(filename, i));
          numbers.add(new Integer(i));
        }
      }
      catch (Exception e) { }

      while (cache.size() > cacheSize) cache.remove(0);
      while (numbers.size() > cacheSize) numbers.remove(0);
    }
    else if (upperBound <= ((Integer) numbers.get(cacheSize - 1)).intValue() &&
      upperBound >= ((Integer) numbers.get(0)).intValue())
    {
      upperBound = ((Integer) numbers.get(0)).intValue();
      try {
        int count = 0;
        for (int i=lowerBound; i<upperBound; i++) {
          cache.add(count, reader.openImage(filename, i));
          numbers.add(count, new Integer(i));
          count++;
        }
      }
      catch (Exception e) { }
      while (cache.size() > cacheSize) {
        cache.remove(cache.size() - 1);
      }
      while (numbers.size() > cacheSize) {
        numbers.remove(numbers.size() - 1);
      }
    }
    else {
      cache.clear();
      numbers.clear();

      for (int i=lowerBound; i<upperBound; i++) {
        try {
          cache.add(reader.openImage(filename, i));
          numbers.add(new Integer(i));
        }
        catch (Exception e) { }
      }
    }
  }

  /**
   * Construct an ImageProcessor from the given byte array, with the given
   * parameters.  If the original image was RGB, then the byte array is
   * assumed to contain all three channels in "RRR...GGG...BBB" order.
   */
  private ImageProcessor createProcessor(BufferedImage b, int w, int h) {
    b = ImageTools.padImage(b, w, h);
    return (new ImagePlus(filename, b)).getProcessor();
  }

}
