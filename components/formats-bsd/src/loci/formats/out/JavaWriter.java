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

package loci.formats.out;

import java.io.File;
import java.io.IOException;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.meta.MetadataRetrieve;

/**
 * JavaWriter is the file format writer for Java source code.
 * At the moment, this code is just a very simple container for pixel data.
 */
public class JavaWriter extends FormatWriter {

  // -- Constructor --

  public JavaWriter() { super("Java source code", "java"); }

  // -- IFormatWriter API methods --

  /**
   * @see loci.formats.IFormatWriter#saveBytes(int, byte[], int, int, int, int)
   */
  @Override
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    checkParams(no, buf, x, y, w, h);
    if (!isFullPlane(x, y, w, h)) {
      throw new FormatException(
        "JavaWriter does not yet support saving image tiles.");
    }

    // check pixel type
    MetadataRetrieve meta = getMetadataRetrieve();
    String pixelType = meta.getPixelsType(series).toString();
    int type = FormatTools.pixelTypeFromString(pixelType);
    if (!DataTools.containsValue(getPixelTypes(), type)) {
      throw new FormatException("Unsupported image type '" + pixelType + "'.");
    }
    int bpp = FormatTools.getBytesPerPixel(type);
    boolean fp = FormatTools.isFloatingPoint(type);
    boolean little = false;
    if (meta.getPixelsBigEndian(series) != null) {
      little = !meta.getPixelsBigEndian(series).booleanValue();
    }
    else if (meta.getPixelsBinDataCount(series) == 0) {
      little = !meta.getPixelsBinDataBigEndian(series, 0).booleanValue();
    }

    // write array
    String varName = "series" + series + "Plane" + no;
    Object array = DataTools.makeDataArray(buf, bpp, fp, little);

    out.seek(out.length());
    if (array instanceof byte[]) {
      writePlane(varName, (byte[]) array, w, h);
    }
    else if (array instanceof short[]) {
      writePlane(varName, (short[]) array, w, h);
    }
    else if (array instanceof int[]) {
      writePlane(varName, (int[]) array, w, h);
    }
    else if (array instanceof long[]) {
      writePlane(varName, (long[]) array, w, h);
    }
    else if (array instanceof float[]) {
      writePlane(varName, (float[]) array, w, h);
    }
    else if (array instanceof double[]) {
      writePlane(varName, (double[]) array, w, h);
    }
  }

  /* @see loci.formats.IFormatWriter#canDoStacks() */
  @Override
  public boolean canDoStacks() { return true; }

  /* @see loci.formats.IFormatWriter#getPixelTypes(String) */
  @Override
  public int[] getPixelTypes(String codec) {
    return new int[] {
      FormatTools.INT8,
      FormatTools.UINT8,
      FormatTools.UINT16,
      FormatTools.UINT32,
      FormatTools.INT32,
      FormatTools.FLOAT,
      FormatTools.DOUBLE
    };
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.FormatWriter#setId(String) */
  @Override
  public void setId(String id) throws FormatException, IOException {
    super.setId(id);
    if (out.length() == 0) writeHeader();
  }

  /* @see loci.formats.IFormatHandler#close() */
  @Override
  public void close() throws IOException {
    if (out != null) writeFooter();
    super.close();
  }

  // -- Helper methods --

  protected void writeHeader() throws IOException {
    String className = currentId.substring(0, currentId.length() - 5);
    className = className.substring(className.lastIndexOf(File.separator) + 1);

    out.writeLine("//");
    out.writeLine("// " + className + ".java");
    out.writeLine("//");
    out.writeLine("");
    out.writeLine("// Generated by Bio-Formats v" + FormatTools.VERSION);
    out.writeLine("// Generated on " + DateTools.getTimestamp());
    out.writeLine("");
    out.writeLine("public class " + className + " {");
    out.writeLine("");
  }

  protected void writePlane(String varName, byte[] array, int w, int h)
    throws IOException
  {
    int i = 0;
    out.writeLine("  public byte[][] " + varName + " = {");
    for (int y=0; y<h; y++) {
      out.writeBytes("    {");
      for (int x=0; x<w; x++) {
        out.writeBytes(String.valueOf(array[i++]));
        if (x < w - 1) out.writeBytes(", ");
        else out.writeBytes("}");
      }
      if (y < h - 1) out.writeLine(",");
      else out.writeLine("");
    }
    out.writeLine("  };");
    out.writeLine("");
  }

  protected void writePlane(String varName, short[] array, int w, int h)
    throws IOException
  {
    int i = 0;
    out.writeLine("  public short[][] " + varName + " = {");
    for (int y=0; y<h; y++) {
      out.writeBytes("    {");
      for (int x=0; x<w; x++) {
        out.writeBytes(String.valueOf(array[i++]));
        if (x < w - 1) out.writeBytes(", ");
        else out.writeBytes("}");
      }
      if (y < h - 1) out.writeLine(",");
      else out.writeLine("");
    }
    out.writeLine("  };");
    out.writeLine("");
  }

  protected void writePlane(String varName, int[] array, int w, int h)
    throws IOException
  {
    int i = 0;
    out.writeLine("  public int[][] " + varName + " = {");
    for (int y=0; y<h; y++) {
      out.writeBytes("    {");
      for (int x=0; x<w; x++) {
        out.writeBytes(String.valueOf(array[i++]));
        if (x < w - 1) out.writeBytes(", ");
        else out.writeBytes("}");
      }
      if (y < h - 1) out.writeLine(",");
      else out.writeLine("");
    }
    out.writeLine("  };");
    out.writeLine("");
  }

  protected void writePlane(String varName, long[] array, int w, int h)
    throws IOException
  {
    int i = 0;
    out.writeLine("  public long[][] " + varName + " = {");
    for (int y=0; y<h; y++) {
      out.writeBytes("    {");
      for (int x=0; x<w; x++) {
        out.writeBytes(String.valueOf(array[i++]));
        if (x < w - 1) out.writeBytes(", ");
        else out.writeBytes("}");
      }
      if (y < h - 1) out.writeLine(",");
      else out.writeLine("");
    }
    out.writeLine("  };");
    out.writeLine("");
  }

  protected void writePlane(String varName, float[] array, int w, int h)
    throws IOException
  {
    int i = 0;
    out.writeLine("  public float[][] " + varName + " = {");
    for (int y=0; y<h; y++) {
      out.writeBytes("    {");
      for (int x=0; x<w; x++) {
        out.writeBytes(String.valueOf(array[i++]));
        if (x < w - 1) out.writeBytes(", ");
        else out.writeBytes("}");
      }
      if (y < h - 1) out.writeLine(",");
      else out.writeLine("");
    }
    out.writeLine("  };");
    out.writeLine("");
  }

  protected void writePlane(String varName, double[] array, int w, int h)
    throws IOException
  {
    int i = 0;
    out.writeLine("  public double[][] " + varName + " = {");
    for (int y=0; y<h; y++) {
      out.writeBytes("    {");
      for (int x=0; x<w; x++) {
        out.writeBytes(String.valueOf(array[i++]));
        if (x < w - 1) out.writeBytes(", ");
        else out.writeBytes("}");
      }
      if (y < h - 1) out.writeLine(",");
      else out.writeLine("");
    }
    out.writeLine("  };");
    out.writeLine("");
  }

  protected void writeFooter() throws IOException {
    out.writeLine("}");
  }

}
