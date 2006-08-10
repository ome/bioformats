//
// CacheManager.java
//

package loci.browser;

import ij.ImagePlus;
import ij.process.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Vector;
import loci.formats.*;

/**
 * Manages the cache of planes for a virtual stack.
 */
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
  private int x, y, ch;

  /** Flag indicating endianness of the image. */
  private boolean littleEndian;

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
      ch = reader.getSizeC(filename);
      littleEndian = reader.isLittleEndian(filename);
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
      return createProcessor(cache.get(p), x, y, ch, littleEndian);
    }

    updateCache(pos);
    p = getPosition(pos);
    return createProcessor(cache.get(p), x, y, ch, littleEndian);
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
      if (!reader.isRGB(filename)) {
        for (int i=0; i<cacheSize; i++) {
          cache.add(trim(reader.openBytes(filename, i)));
          numbers.add(new Integer(i));
        }
      }
      else {
        for (int i=0; i<cacheSize; i++) {
          cache.add(reader.openImage(filename, i));
          numbers.add(new Integer(i));
        }
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
          if (!reader.isRGB(filename)) {
            cache.add(trim(reader.openBytes(filename, i)));
          }
          else cache.add(reader.openImage(filename, i));
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
          if (!reader.isRGB(filename)) {
            cache.add(count, trim(reader.openBytes(filename, i)));
          }
          else cache.add(count, reader.openImage(filename, i));
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
          if (!reader.isRGB(filename)) {
            cache.add(trim(reader.openBytes(filename, i)));
          }
          else cache.add(reader.openImage(filename, i));
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
  private ImageProcessor createProcessor(Object b, int w, int h, int c,
    boolean little)
  {
    if (b instanceof BufferedImage) {
      BufferedImage bi = (BufferedImage) b;

      return (new ImagePlus(filename, bi)).getProcessor();
    }
    else {
      byte[] by = (byte[]) b;
      if (by.length <= w * h) {
        ByteProcessor proc = new ByteProcessor(w, h);
        if (by.length == w * h) {
          proc.setPixels(by);
        }
        else {
          byte[] bytes = new byte[w * h];
          System.arraycopy(by, 0, bytes, 0, by.length);
          proc.setPixels(bytes);
        }
        return proc;
      }
      else if (by.length <= w * h * 2) {
        short[] shorts = new short[w * h];
        for (int i=0; i<by.length / 2; i++) {
          shorts[i] = DataTools.bytesToShort(by, i*2, 2, little);
        }
        ShortProcessor proc = new ShortProcessor(w, h);
        proc.setPixels(shorts);
        return proc;
      }
      else if (by.length <= w * h * 4) {
        int[] floats = new int[w * h];
        for (int i=0; i<by.length / 4; i++) {
          floats[i] = DataTools.bytesToInt(by, i*4, little);
        }
        return new FloatProcessor(w, h, floats);
      }
    }
    return null;
  }

  /** Trim a byte array to the correct size. */
  private byte[] trim(byte[] b) {
    if (b.length < x * y) {
      byte[] t = new byte[x * y];
      System.arraycopy(b, 0, t, 0, b.length);
      return t;
    }

    byte[] t = new byte[x * y * (b.length / (x * y))];
    System.arraycopy(b, 0, t, 0, t.length);
    return t;
  }

}
