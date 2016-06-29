/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

import loci.common.DataTools;

/**
 * A utility class with convenience methods for manipulating images
 * in primitive array form.
 *
 * To work with images in {@link java.awt.image.BufferedImage} form,
 * use the {@link loci.formats.gui.AWTImageTools} class.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public final class ImageTools {

  // -- Constructor --

  private ImageTools() { }

  // -- Image conversion --

  /**
   * Convert an arbitrary primitive type array with 3 samples per pixel to
   * a 3 x (width * height) byte array.
   */
  public static byte[][] make24Bits(Object pixels, int w, int h,
    boolean interleaved, boolean reverse)
  {
    return make24Bits(pixels, w, h, interleaved, reverse, null, null);
  }

  /**
   * Convert an arbitrary primitive type array with 3 samples per pixel to
   * a 3 x (width * height) byte array.  Scaling is performed according to
   * the specified minimum and maximum pixel values in the original image.
   *
   * If the minimum is null, it is assumed to be 0.
   * If the maximum is null, it is assumed to be 2^nbits - 1.
   */
  public static byte[][] make24Bits(Object pixels, int w, int h,
    boolean interleaved, boolean reverse, Double min, Double max)
  {
    int[] pix = make24Bits(pixels, w, h, interleaved, min, max);
    byte[][] rtn = new byte[3][pix.length];
    for (int i=0; i<pix.length; i++) {
      byte r = (byte) ((pix[i] >> 16) & 0xff);
      rtn[1][i] = (byte) ((pix[i] >> 8) & 0xff);
      byte b = (byte) (pix[i] & 0xff);
      rtn[0][i] = reverse ? b : r;
      rtn[2][i] = reverse ? r : b;
    }
    return rtn;
  }

  /**
   * Convert an arbitrary primitive type array with 3 samples per pixel to
   * an int array, i.e. RGB color with 8 bits per pixel.
   */
  public static int[] make24Bits(Object pixels, int w, int h,
    boolean interleaved)
  {
    return make24Bits(pixels, w, h, interleaved, null, null);
  }

  /**
   * Convert an arbitrary primitive type array with 3 samples per pixel to
   * an int array, i.e. RGB color with 8 bits per pixel.
   *
   * Scaling is performed according to
   * the specified minimum and maximum pixel values in the original image.
   *
   * If the minimum is null, it is assumed to be 0.
   * If the maximum is null, it is assumed to be 2^nbits - 1.
   */
  public static int[] make24Bits(Object pixels, int w, int h,
    boolean interleaved, Double min, Double max)
  {
    int[] rtn = new int[w * h];

    byte[] b = null;

    if (min == null) min = Double.valueOf(0);
    double newRange = 255d;

    // adapted from ImageJ's TypeConverter methods

    if (pixels instanceof byte[]) b = (byte[]) pixels;
    else if (pixels instanceof short[]) {
      if (max == null) max = new Double(0xffff);
      double range = max.doubleValue() - min.doubleValue();
      double mult = newRange / range;

      short[] s = (short[]) pixels;
      b = new byte[s.length];
      for (int i=0; i<s.length; i++) {
        b[i] = (byte) Math.abs((s[i] - min.doubleValue()) * mult);
      }
    }
    else if (pixels instanceof int[]) {
      if (max == null) max = new Double(0xffffffffL);
      double range = max.doubleValue() - min.doubleValue();
      double mult = newRange / range;

      int[] s = (int[]) pixels;
      b = new byte[s.length];
      for (int i=0; i<s.length; i++) {
        b[i] = (byte) (Math.abs((s[i] - min.doubleValue()) * mult));
      }
    }
    else if (pixels instanceof float[]) {
      if (max == null) max = new Double(Float.MAX_VALUE);
      double range = max.doubleValue() - min.doubleValue();
      double mult = newRange / range;

      float[] s = (float[]) pixels;
      b = new byte[s.length];
      for (int i=0; i<s.length; i++) {
        b[i] = (byte) ((s[i] - min.doubleValue()) * mult);
      }
    }
    else if (pixels instanceof double[]) {
      if (max == null) max = new Double(Double.MAX_VALUE);
      double range = max.doubleValue() - min.doubleValue();
      double mult = newRange / range;

      double[] s = (double[]) pixels;
      b = new byte[s.length];
      for (int i=0; i<s.length; i++) {
        b[i] = (byte) ((s[i] - min.doubleValue()) * mult);
      }
    }

    int c = b.length / rtn.length;

    for (int i=0; i<rtn.length; i++) {
      byte[] a = new byte[4];
      int maxC = Math.min(c, a.length);
      for (int j=maxC-1; j>=0; j--) {
        a[j] = b[interleaved ? i * c + j : i + j * w * h];
      }
      if (c == 1) {
        for (int j=1; j<a.length; j++) {
          a[j] = a[0];
        }
      }

      byte tmp = a[0];
      a[0] = a[2];
      a[2] = tmp;
      rtn[i] = DataTools.bytesToInt(a, true);
    }

    return rtn;
  }

  // -- Image manipulation --

  /**
   * Splits the given multi-channel array into a 2D array.
   * The "reverse" parameter is false if channels are in RGB order, true if
   * channels are in BGR order.
   */
  public static byte[] splitChannels(byte[] array, int index, int c, int bytes,
    boolean reverse, boolean interleaved)
  {
    return splitChannels(array, null, index, c, bytes, reverse, interleaved,
      array.length / c);
  }

  /**
   * Splits the given multi-channel array into a 2D array.
   * The "reverse" parameter is false if channels are in RGB order, true if
   * channels are in BGR order.  If the 'rtn' parameter is not null, the
   * specified channel will be copied into 'rtn'.
   *
   * The 'channelLength' parameter specifies the number of bytes that are
   * expected to be in a single channel.  In many cases, this will match
   * the value of 'rtn.length', but specifying it separately allows 'rtn' to
   * be larger than the size of a single channel.
   */
  public static byte[] splitChannels(byte[] array, byte[] rtn, int index, int c,
    int bytes, boolean reverse, boolean interleaved, int channelLength)
  {
    if (c == 1) return array;
    if (rtn == null) {
      rtn = new byte[array.length / c];
    }

    if (reverse) index = c - index - 1;

    if (!interleaved) {
      System.arraycopy(array, channelLength * index, rtn, 0, channelLength);
    }
    else {
      int next = 0;
      for (int i=0; i<array.length; i+=c*bytes) {
        for (int k=0; k<bytes; k++) {
          if (next < rtn.length) rtn[next] = array[i + index*bytes + k];
          next++;
        }
      }
    }
    return rtn;
  }

  /**
   * Pads (or crops) the byte array to the given width and height.
   * The image will be centered within the new bounds.
   */
  public static byte[] padImage(byte[] b, boolean interleaved, int c,
    int oldWidth, int width, int height)
  {
    int oldHeight = b.length / (oldWidth * c);
    byte[] padded = new byte[height * width * c];

    int wClip = (width - oldWidth) / 2;
    int hClip = (height - oldHeight) / 2;

    int h = height < oldHeight ? height : oldHeight;

    if (interleaved) {
      int len = oldWidth < width ? oldWidth : width;
      if (h == oldHeight) {
        for (int y=0; y<h*c; y++) {
          int oldIndex = oldWidth * y;
          int index = width * y;
          System.arraycopy(b, oldIndex, padded, index, len);
        }
      }
      else {
        for (int ch=0; ch<c; ch++) {
          for (int y=0; y<h; y++) {
            int oldIndex = oldWidth * ch * oldHeight + oldWidth * y;
            int index = width * ch * height + width * y;
            System.arraycopy(b, oldIndex, padded, index, len);
          }
        }
      }
    }
    else {
      int len = oldWidth < width ? oldWidth * c : width * c;
      for (int oy=0, y=0; oy<oldHeight; oy++, y++) {
        int oldIndex = oldWidth * c * y;
        int index = width * c * (y + hClip) + c * wClip;
        System.arraycopy(b, oldIndex, padded, index, len);
      }
    }
    return padded;
  }

  /**
   * Pads (or crops) the short array to the given width and height.
   * The image will be centered within the new bounds.
   */
  public static short[] padImage(short[] b, boolean interleaved, int c,
    int oldWidth, int width, int height)
  {
    int oldHeight = b.length / (oldWidth * c);
    short[] padded = new short[height * width * c];

    int wClip = (width - oldWidth) / 2;
    int hClip = (height - oldHeight) / 2;

    int h = height < oldHeight ? height : oldHeight;

    if (interleaved) {
      int len = oldWidth < width ? oldWidth : width;
      if (h == oldHeight) {
        for (int y=0; y<h*c; y++) {
          int oldIndex = oldWidth * y;
          int index = width * y;
          System.arraycopy(b, oldIndex, padded, index, len);
        }
      }
      else {
        for (int ch=0; ch<c; ch++) {
          for (int y=0; y<h; y++) {
            int oldIndex = oldWidth * ch * oldHeight + oldWidth * y;
            int index = width * ch * height + width * y;
            System.arraycopy(b, oldIndex, padded, index, len);
          }
        }
      }
    }
    else {
      int len = oldWidth < width ? oldWidth * c : width * c;
      for (int oy=0, y=0; oy<oldHeight; oy++, y++) {
        int oldIndex = oldWidth * c * y;
        int index = width * c * (y + hClip) + c * wClip;
        System.arraycopy(b, oldIndex, padded, index, len);
      }
    }
    return padded;
  }

  /**
   * Pads (or crops) the int array to the given width and height.
   * The image will be centered within the new bounds.
   */
  public static int[] padImage(int[] b, boolean interleaved, int c,
    int oldWidth, int width, int height)
  {
    int oldHeight = b.length / (oldWidth * c);
    int[] padded = new int[height * width * c];

    int wClip = (width - oldWidth) / 2;
    int hClip = (height - oldHeight) / 2;

    int h = height < oldHeight ? height : oldHeight;

    if (interleaved) {
      int len = oldWidth < width ? oldWidth : width;
      if (h == oldHeight) {
        for (int y=0; y<h*c; y++) {
          int oldIndex = oldWidth * y;
          int index = width * y;
          System.arraycopy(b, oldIndex, padded, index, len);
        }
      }
      else {
        for (int ch=0; ch<c; ch++) {
          for (int y=0; y<h; y++) {
            int oldIndex = oldWidth * ch * oldHeight + oldWidth * y;
            int index = width * ch * height + width * y;
            System.arraycopy(b, oldIndex, padded, index, len);
          }
        }
      }
    }
    else {
      int len = oldWidth < width ? oldWidth * c : width * c;
      for (int oy=0, y=0; oy<oldHeight; oy++, y++) {
        int oldIndex = oldWidth * c * y;
        int index = width * c * (y + hClip) + c * wClip;
        System.arraycopy(b, oldIndex, padded, index, len);
      }
    }
    return padded;
  }

  /**
   * Pads (or crops) the float array to the given width and height.
   * The image will be centered within the new bounds.
   */
  public static float[] padImage(float[] b, boolean interleaved, int c,
    int oldWidth, int width, int height)
  {
    int oldHeight = b.length / (oldWidth * c);
    float[] padded = new float[height * width * c];

    int wClip = (width - oldWidth) / 2;
    int hClip = (height - oldHeight) / 2;

    int h = height < oldHeight ? height : oldHeight;

    if (interleaved) {
      int len = oldWidth < width ? oldWidth : width;
      if (h == oldHeight) {
        for (int y=0; y<h*c; y++) {
          int oldIndex = oldWidth * y;
          int index = width * y;
          System.arraycopy(b, oldIndex, padded, index, len);
        }
      }
      else {
        for (int ch=0; ch<c; ch++) {
          for (int y=0; y<h; y++) {
            int oldIndex = oldWidth * ch * oldHeight + oldWidth * y;
            int index = width * ch * height + width * y;
            System.arraycopy(b, oldIndex, padded, index, len);
          }
        }
      }
    }
    else {
      int len = oldWidth < width ? oldWidth * c : width * c;
      for (int oy=0, y=0; oy<oldHeight; oy++, y++) {
        int oldIndex = oldWidth * c * y;
        int index = width * c * (y + hClip) + c * wClip;
        System.arraycopy(b, oldIndex, padded, index, len);
      }
    }
    return padded;
  }

  /**
   * Pads (or crops) the double array to the given width and height.
   * The image will be centered within the new bounds.
   */
  public static double[] padImage(double[] b, boolean interleaved, int c,
    int oldWidth, int width, int height)
  {
    int oldHeight = b.length / (oldWidth * c);
    double[] padded = new double[height * width * c];

    int wClip = (width - oldWidth) / 2;
    int hClip = (height - oldHeight) / 2;

    int h = height < oldHeight ? height : oldHeight;

    if (interleaved) {
      int len = oldWidth < width ? oldWidth : width;
      if (h == oldHeight) {
        for (int y=0; y<h*c; y++) {
          int oldIndex = oldWidth * y;
          int index = width * y;
          System.arraycopy(b, oldIndex, padded, index, len);
        }
      }
      else {
        for (int ch=0; ch<c; ch++) {
          for (int y=0; y<h; y++) {
            int oldIndex = oldWidth * ch * oldHeight + oldWidth * y;
            int index = width * ch * height + width * y;
            System.arraycopy(b, oldIndex, padded, index, len);
          }
        }
      }
    }
    else {
      int len = oldWidth < width ? oldWidth * c : width * c;
      for (int oy=0, y=0; oy<oldHeight; oy++, y++) {
        int oldIndex = oldWidth * c * y;
        int index = width * c * (y + hClip) + c * wClip;
        System.arraycopy(b, oldIndex, padded, index, len);
      }
    }
    return padded;
  }

  /**
   * Perform autoscaling on the given byte array;
   * map min to 0 and max to 255.  If the number of bytes per pixel is 1, then
   * nothing happens.
   */
  public static byte[] autoscale(byte[] b, int min, int max, int bpp,
    boolean little)
  {
    if (bpp == 1) return b;

    byte[] out = new byte[b.length / bpp];

    for (int i=0; i<b.length; i+=bpp) {
      int s = DataTools.bytesToInt(b, i, bpp, little);

      if (s >= max) s = 255;
      else if (s <= min) s = 0;
      else {
        float diff = max - min;
        float dist = (s - min) / diff;

        s = (int)(dist * 255);
      }

      out[i / bpp] = (byte) s;
    }
    return out;
  }

  /** Scan a plane for the channel min and max values. */
  public static Double[] scanData(byte[] plane, int bits, boolean littleEndian)
  {
    int max = 0;
    int min = Integer.MAX_VALUE;

    if (bits <= 8) {
      for (int j=0; j<plane.length; j++) {
        if (plane[j] < min) min = plane[j];
        if (plane[j] > max) max = plane[j];
      }
    }
    else if (bits == 16) {
      for (int j=0; j<plane.length; j+=2) {
        short s = DataTools.bytesToShort(plane, j, 2, littleEndian);
        if (s < min) min = s;
        if (s > max) max = s;
      }
    }
    else if (bits == 32) {
      for (int j=0; j<plane.length; j+=4) {
        int s = DataTools.bytesToInt(plane, j, 4, littleEndian);
        if (s < min) min = s;
        if (s > max) max = s;
      }
    }

    Double[] rtn = new Double[2];
    rtn[0] = new Double(min);
    rtn[1] = new Double(max);
    return rtn;
  }

  public static byte[] getSubimage(byte[] src, byte[] dest, int originalWidth,
    int originalHeight, int x, int y, int w, int h, int bpp, int channels,
    boolean interleaved)
  {
    for (int yy=y; yy<y + h; yy++) {
      for (int xx=x; xx<x + w; xx++) {
        for (int cc=0; cc<channels; cc++) {
          int oldNdx = -1, newNdx = -1;
          if (interleaved) {
            oldNdx = yy*originalWidth*bpp*channels + xx*bpp*channels + cc*bpp;
            newNdx = (yy - y)*w*bpp*channels + (xx - x)*bpp*channels + cc*bpp;
          }
          else {
            oldNdx =
              bpp*(cc*originalWidth*originalHeight + yy*originalWidth + xx);
            newNdx = bpp*(cc*w*h + (yy - y)*w + (xx - x));
          }
          System.arraycopy(src, oldNdx, dest, newNdx, bpp);
        }
      }
    }
    return dest;
  }

  // -- Indexed color conversion --

  /** Converts a LUT and an array of indices into an array of RGB tuples. */
  public static byte[][] indexedToRGB(byte[][] lut, byte[] b) {
    byte[][] rtn = new byte[lut.length][b.length];

    for (int i=0; i<b.length; i++) {
      for (int j=0; j<lut.length; j++) {
        rtn[j][i] = lut[j][b[i] & 0xff];
      }
    }
    return rtn;
  }

  /** Converts a LUT and an array of indices into an array of RGB tuples. */
  public static short[][] indexedToRGB(short[][] lut, byte[] b, boolean le) {
    short[][] rtn = new short[lut.length][b.length / 2];
    for (int i=0; i<b.length/2; i++) {
      for (int j=0; j<lut.length; j++) {
        int index = DataTools.bytesToInt(b, i*2, 2, le);
        rtn[j][i] = lut[j][index];
      }
    }
    return rtn;
  }

  public static byte[] interpolate(short[] s, byte[] buf, int[] bayerPattern,
    int width, int height, boolean littleEndian)
  {
    if (width == 1 && height == 1) {
      for (int i=0; i<buf.length; i++) {
        buf[i] = (byte) s[0];
      }
      return buf;
    }
    // use linear interpolation to fill in missing components

    int plane = width * height;

    for (int row=0; row<height; row++) {
      for (int col=0; col<width; col++) {
        //boolean evenRow = (row % 2) == 0;
        boolean evenCol = (col % 2) == 0;

        int index = (row % 2) * 2 + (col % 2);
        boolean needGreen = bayerPattern[index] != 1;
        boolean needRed = bayerPattern[index] != 0;
        boolean needBlue = bayerPattern[index] != 2;

        if (needGreen) {
          int sum = 0;
          int ncomps = 0;

          if (row > 0) {
            sum += s[plane + (row - 1) * width + col];
            ncomps++;
          }
          if (row < height - 1) {
            sum += s[plane + (row + 1) * width + col];
            ncomps++;
          }
          if (col > 0) {
            sum += s[plane + row * width + col - 1];
            ncomps++;
          }
          if (col < width- 1) {
            sum += s[plane + row*width + col + 1];
            ncomps++;
          }

          short v = (short) (sum / ncomps);
          DataTools.unpackBytes(v, buf,
            row*width*6 + col*6 + 2, 2, littleEndian);
        }
        else {
          DataTools.unpackBytes(s[plane + row*width + col],
            buf, row*width*6 + col*6 + 2, 2, littleEndian);
        }

        if (needRed) {
          int sum = 0;
          int ncomps = 0;
          if (!needBlue) {
            // four corners
            if (row > 0) {
              if (col > 0) {
                sum += s[(row-1)*width + col - 1];
                ncomps++;
              }
              if (col < width - 1) {
                sum += s[(row-1)*width + col + 1];
                ncomps++;
              }
            }
            if (row < height - 1) {
              if (col > 0) {
                sum += s[(row+1)*width + col - 1];
                ncomps++;
              }
              if (col < width - 1) {
                sum += s[(row+1)*width + col + 1];
                ncomps++;
              }
            }
          }
          else if ((evenCol && bayerPattern[index + 1] == 0) ||
            (!evenCol && bayerPattern[index - 1] == 0))
          {
            // horizontal
            if (col > 0) {
              sum += s[row*width + col - 1];
              ncomps++;
            }
            if (col < width - 1) {
              sum += s[row*width + col + 1];
              ncomps++;
            }
          }
          else {
            // vertical
            if (row > 0) {
              sum += s[(row - 1)*width + col];
              ncomps++;
            }
            if (row < height - 1) {
              sum += s[(row+1)*width + col];
              ncomps++;
            }
          }

          short v = (short) (sum / ncomps);
          DataTools.unpackBytes(v, buf, row*width*6 + col*6, 2, littleEndian);
        }
        else {
          DataTools.unpackBytes(s[row*width + col],
            buf, row*width*6 + col*6, 2, littleEndian);
        }

        if (needBlue) {
          int sum = 0;
          int ncomps = 0;
          if (!needRed) {
            // four corners
            if (row > 0) {
              if (col > 0) {
                sum += s[(2*height + row - 1)*width + col - 1];
                ncomps++;
              }
              if (col < width - 1) {
                sum += s[(2*height + row-1)*width + col + 1];
                ncomps++;
              }
            }
            if (row < height - 1) {
              if (col > 0) {
                sum += s[(2*height + row+1)*width + col - 1];
                ncomps++;
              }
              if (col < width - 1) {
                sum += s[(2*height + row+1)*width + col + 1];
                ncomps++;
              }
            }
          }
          else if ((evenCol && bayerPattern[index + 1] == 2) ||
            (!evenCol && bayerPattern[index - 1] == 2))
          {
            // horizontal
            if (col > 0) {
              sum += s[(2*height + row)*width + col - 1];
              ncomps++;
            }
            if (col < width - 1) {
              sum += s[(2*height + row)*width + col + 1];
              ncomps++;
            }
          }
          else {
            // vertical
            if (row > 0) {
              sum += s[(2*height + row - 1)*width + col];
              ncomps++;
            }
            if (row < height - 1) {
              sum += s[(2*height + row+1)*width + col];
              ncomps++;
            }
          }

          short v = (short) (sum / ncomps);
          DataTools.unpackBytes(v, buf,
            row*width*6 + col*6 + 4, 2, littleEndian);
        }
        else {
          DataTools.unpackBytes(s[2*plane + row*width + col],
            buf, row*width*6 + col*6 + 4, 2, littleEndian);
        }
      }
    }

    return buf;
  }

  public static void bgrToRgb(byte[] buf, boolean interleaved, int bpp, int c) {
    if (c < 3) return;
    if (interleaved) {
      for (int i=0; i<buf.length; i+=bpp*c) {
        for (int b=0; b<bpp; b++) {
          byte tmp = buf[i + b];
          buf[i + b] = buf[i + b + bpp * 2];
          buf[i + b + bpp * 2] = tmp;
        }
      }
    }
    else {
      byte[] channel = new byte[buf.length / (bpp * c)];
      System.arraycopy(buf, 0, channel, 0, channel.length);
      System.arraycopy(buf, channel.length * 2, buf, 0, channel.length);
      System.arraycopy(channel, 0, buf, channel.length * 2, channel.length);
    }
  }

}
