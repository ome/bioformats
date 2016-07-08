/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2016 Open Microscopy Environment:
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

package loci.plugins.util;

import ij.IJ;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.LUT;
import ij.process.ShortProcessor;

import java.io.IOException;

import loci.common.DataTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageTools;
import loci.formats.ReaderWrapper;
import loci.plugins.BF;

/**
 * A low-level reader for {@link ij.process.ImageProcessor} objects.
 * For a higher-level reader that returns {@link ij.ImagePlus} objects,
 * see {@link loci.plugins.in.ImagePlusReader} instead.
 */
public class ImageProcessorReader extends ReaderWrapper {

  // -- Utility methods --

  /**
   * Converts the given reader into a ImageProcessorReader, wrapping if needed.
   */
  public static ImageProcessorReader makeImageProcessorReader(IFormatReader r) {
    if (r instanceof ImageProcessorReader) return (ImageProcessorReader) r;
    return new ImageProcessorReader(r);
  }

  // -- Constructors --

  /** Constructs an ImageProcessorReader around a new image reader. */
  public ImageProcessorReader() { super(LociPrefs.makeImageReader()); }

  /** Constructs an ImageProcessorReader with the given reader. */
  public ImageProcessorReader(IFormatReader r) { super(r); }

  // -- ImageProcessorReader methods --

  /**
   * Creates an ImageJ image processor object
   * for the image plane at the given position.
   *
   * @param no Position of image plane.
   */
  public ImageProcessor[] openProcessors(int no)
    throws FormatException, IOException
  {
    return openProcessors(no, 0, 0, getSizeX(), getSizeY());
  }

  public ImageProcessor[] openThumbProcessors(int no)
    throws FormatException, IOException
  {
    // read byte array
    byte[] b = openThumbBytes(no);

    int c = getRGBChannelCount();
    int type = getPixelType();
    int bpp = FormatTools.getBytesPerPixel(type);
    boolean interleave = isInterleaved();

    int w = getThumbSizeX();
    int h = getThumbSizeY();

    if (b.length != w * h * c * bpp && b.length != w * h * bpp) {
      throw new FormatException("Invalid byte array length: " + b.length +
        " (expected w=" + w + ", h=" + h + ", c=" + c + ", bpp=" + bpp + ")");
    }

    // create a color model for this plane (null means default)
    final LUT cm = createColorModel();

    // convert byte array to appropriate primitive array type
    boolean isFloat = FormatTools.isFloatingPoint(type);
    boolean isLittle = isLittleEndian();
    boolean isSigned = FormatTools.isSigned(type);

    // construct image processors
    ImageProcessor[] ip = new ImageProcessor[c];
    for (int i=0; i<c; i++) {
      byte[] channel =
        ImageTools.splitChannels(b, i, c, bpp, false, interleave);
      Object pixels = DataTools.makeDataArray(channel, bpp, isFloat, isLittle);
      if (pixels instanceof byte[]) {
        byte[] q = (byte[]) pixels;
        if (q.length != w * h) {
          byte[] tmp = q;
          q = new byte[w * h];
          System.arraycopy(tmp, 0, q, 0, Math.min(q.length, tmp.length));
        }
        if (isSigned) q = DataTools.makeSigned(q);

        ip[i] = new ByteProcessor(w, h, q, null);
        if (cm != null) ip[i].setColorModel(cm);
      }
      else if (pixels instanceof short[]) {
        short[] q = (short[]) pixels;
        if (q.length != w * h) {
          short[] tmp = q;
          q = new short[w * h];
          System.arraycopy(tmp, 0, q, 0, Math.min(q.length, tmp.length));
        }
        if (isSigned) q = DataTools.makeSigned(q);

        ip[i] = new ShortProcessor(w, h, q, cm);
      }
      else if (pixels instanceof int[]) {
        int[] q = (int[]) pixels;
        if (q.length != w * h) {
          int[] tmp = q;
          q = new int[w * h];
          System.arraycopy(tmp, 0, q, 0, Math.min(q.length, tmp.length));
        }

        ip[i] = new FloatProcessor(w, h, q);
      }
      else if (pixels instanceof float[]) {
        float[] q = (float[]) pixels;
        if (q.length != w * h) {
          float[] tmp = q;
          q = new float[w * h];
          System.arraycopy(tmp, 0, q, 0, Math.min(q.length, tmp.length));
        }
        ip[i] = new FloatProcessor(w, h, q, null);
      }
      else if (pixels instanceof double[]) {
        double[] q = (double[]) pixels;
        if (q.length != w * h) {
          double[] tmp = q;
          q = new double[w * h];
          System.arraycopy(tmp, 0, q, 0, Math.min(q.length, tmp.length));
        }
        ip[i] = new FloatProcessor(w, h, q);
      }
    }

    return ip;
  }

  /**
   * Returns an array of ImageProcessors that represent the given slice.
   * There is one ImageProcessor per RGB channel;
   * i.e., length of returned array == getRGBChannelCount().
   *
   * @param no Position of image plane.
   */
  public ImageProcessor[] openProcessors(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    // read byte array
    byte[] b = openBytes(no, x, y, w, h);

    int c = getRGBChannelCount();
    int type = getPixelType();
    int bpp = FormatTools.getBytesPerPixel(type);
    boolean interleave = isInterleaved();

    if (b.length != w * h * c * bpp && b.length != w * h * bpp) {
      throw new FormatException("Invalid byte array length: " + b.length +
        " (expected w=" + w + ", h=" + h + ", c=" + c + ", bpp=" + bpp + ")");
    }

    // create a color model for this plane (null means default)
    final LUT cm = createColorModel();

    // convert byte array to appropriate primitive array type
    boolean isFloat = FormatTools.isFloatingPoint(type);
    boolean isLittle = isLittleEndian();
    boolean isSigned = FormatTools.isSigned(type);

    // construct image processors
    ImageProcessor[] ip = new ImageProcessor[c];
    for (int i=0; i<c; i++) {
      byte[] channel =
        ImageTools.splitChannels(b, i, c, bpp, false, interleave);
      Object pixels = DataTools.makeDataArray(channel, bpp, isFloat, isLittle);
      if (pixels instanceof byte[]) {
        byte[] q = (byte[]) pixels;
        if (q.length != w * h) {
          byte[] tmp = q;
          q = new byte[w * h];
          System.arraycopy(tmp, 0, q, 0, Math.min(q.length, tmp.length));
        }
        if (isSigned) q = DataTools.makeSigned(q);

        ip[i] = new ByteProcessor(w, h, q, null);
        if (cm != null) ip[i].setColorModel(cm);
      }
      else if (pixels instanceof short[]) {
        short[] q = (short[]) pixels;
        if (q.length != w * h) {
          short[] tmp = q;
          q = new short[w * h];
          System.arraycopy(tmp, 0, q, 0, Math.min(q.length, tmp.length));
        }
        if (isSigned) q = DataTools.makeSigned(q);

        ip[i] = new ShortProcessor(w, h, q, cm);
      }
      else if (pixels instanceof int[]) {
        int[] q = (int[]) pixels;
        if (q.length != w * h) {
          int[] tmp = q;
          q = new int[w * h];
          System.arraycopy(tmp, 0, q, 0, Math.min(q.length, tmp.length));
        }

        ip[i] = new FloatProcessor(w, h, q);
      }
      else if (pixels instanceof float[]) {
        float[] q = (float[]) pixels;
        if (q.length != w * h) {
          float[] tmp = q;
          q = new float[w * h];
          System.arraycopy(tmp, 0, q, 0, Math.min(q.length, tmp.length));
        }
        ip[i] = new FloatProcessor(w, h, q, null);
      }
      else if (pixels instanceof double[]) {
        double[] q = (double[]) pixels;
        if (q.length != w * h) {
          double[] tmp = q;
          q = new double[w * h];
          System.arraycopy(tmp, 0, q, 0, Math.min(q.length, tmp.length));
        }
        ip[i] = new FloatProcessor(w, h, q);
      }
    }

    return ip;
  }

  // -- IFormatReader methods --

  @Override
  public Class<?> getNativeDataType() {
    return ImageProcessor[].class;
  }

  @Override
  public Object openPlane(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return openProcessors(no, x, y, w, h);
  }

  // -- Helper methods --

  private LUT createColorModel() throws FormatException, IOException {
    // NB: If a color table is present, we might as well use it,
    // regardless of the value of isIndexed.
    //if (!isIndexed()) return null;

    byte[][] byteTable = get8BitLookupTable(getPlane());
    if (byteTable == null) byteTable = convertTo8Bit(get16BitLookupTable(getPlane()));
    if (byteTable == null || byteTable.length == 0) return null;

    // extract red, green and blue elements
    final int colors = byteTable.length;
    final int samples = byteTable[0].length;
    final byte[] r = colors >= 1 ? byteTable[0] : new byte[samples];
    final byte[] g = colors >= 2 ? byteTable[1] : new byte[samples];
    final byte[] b = colors >= 3 ? byteTable[2] : new byte[samples];
    return new LUT(8, samples, r, g, b);
  }

  private byte[][] convertTo8Bit(short[][] shortTable) {
    if (shortTable == null) return null;
    byte[][] byteTable = new byte[shortTable.length][256];

    double max = Math.pow(2, getBitsPerPixel()) - 1;

    for (int c=0; c<byteTable.length; c++) {
      int len = Math.min(byteTable[c].length, shortTable[c].length);
      int valuesPerBin = shortTable[c].length / byteTable[c].length;
      int adjustPerBin = (int) ((max + 1) / byteTable[c].length);

      for (int i=0; i<len; i++) {
        // NB: you could generate the 8-bit LUT by casting the first 256 samples
        // in the 16-bit LUT to bytes.  However, this will not produce optimal
        // results; in many cases, you will end up with a completely black
        // 8-bit LUT even if the original 16-bit LUT contained non-zero samples.
        //
        // Another option would be to scale every 256th value in the 16-bit LUT;
        // this may be a bit faster, but will be less accurate than the
        // averaging approach taken below.

        // TODO: For non-continuous LUTs, this approach does not work well.
        //
        // For an example, try:
        //   'i16&pixelType=uint16&indexed=true&falseColor=true.fake'
        //
        // To fully resolve this issue, we would need to redither the image.
        //
        // At minimum, we should issue a warning to the ImageJ log whenever
        // this convertTo8Bit routine is invoked, so the user is informed.

        double average = 0;
        for (int p=0; p<valuesPerBin; p++) {
          final int index = i * valuesPerBin + p;
          final int value = 0xffff & shortTable[c][index];
          average += value;
        }
        average /= adjustPerBin;
        if (average > max) {
          average = (int) Math.min(max, average / valuesPerBin);
        }
        byteTable[c][i] = (byte) (255 * (average / max));
      }
    }

    if (IJ.debugMode) {
      final StringBuilder sb = new StringBuilder();
      BF.debug("Downsampled 16-bit LUT to 8-bit:");

      BF.debug("shortTable = {");
      for (int i=0; i<shortTable.length; i++) {
        sb.setLength(0);
        sb.append("\t{");
        for (int j=0; j<shortTable[i].length; j++) {
          sb.append(" ");
          sb.append(shortTable[i][j]);
        }
        sb.append(" }");
        BF.debug(sb.toString());
      }
      BF.debug("}");

      BF.debug("byteTable = {");
      for (int i=0; i<byteTable.length; i++) {
        sb.setLength(0);
        sb.append("\t{");
        for (int j=0; j<byteTable[i].length; j++) {
          sb.append(" ");
          sb.append(byteTable[i][j]);
        }
        sb.append(" }");
        BF.debug(sb.toString());
      }
      BF.debug("}");

    }

    return byteTable;
  }

}
