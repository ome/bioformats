//
// FormatWriter.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

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

import java.awt.Image;
import java.awt.image.ColorModel;
import java.io.IOException;

/** Abstract superclass of all biological file format writers. */
public abstract class FormatWriter extends FormatHandler
  implements IFormatWriter
{

  // -- Fields --

  /** Frame rate to use when writing in frames per second, if applicable. */
  protected int fps = 10;

  /** Default color model. */
  protected ColorModel cm;

  /** Available compression types. */
  protected String[] compressionTypes;

  /** Current compression type. */
  protected String compression;

  // -- Constructors --

  /** Constructs a format writer with the given name and default suffix. */
  public FormatWriter(String format, String suffix) { super(format, suffix); }

  /** Constructs a format writer with the given name and default suffixes. */
  public FormatWriter(String format, String[] suffixes) {
    super(format, suffixes);
  }

  // -- IFormatWriter API methods --

  /**
   * Saves the given image to the specified (possibly already open) file.
   * If this image is the last one in the file, the last flag must be set.
   */
  public abstract void save(String id, Image image, boolean last)
    throws FormatException, IOException;

  /** Reports whether the writer can save multiple images to a single file. */
  public abstract boolean canDoStacks(String id);

  /** Sets the color model. */
  public void setColorModel(ColorModel model) { cm = model; }

  /** Gets the color model. */
  public ColorModel getColorModel() { return cm; }

  /** Sets the frames per second to use when writing. */
  public void setFramesPerSecond(int rate) { fps = rate; }

  /** Gets the frames per second to use when writing. */
  public int getFramesPerSecond() { return fps; }

  /** Get the available compression types. */
  public String[] getCompressionTypes() { return compressionTypes; }

  /** Set the current compression type. */
  public void setCompression(String compress) throws FormatException {
    // check that this is a valid type
    for (int i=0; i<compressionTypes.length; i++) {
      if (compressionTypes[i].equals(compress)) {
        compression = compress;
        return;
      }
    }
    throw new FormatException("Invalid compression type: " + compress);
  }

  /* @see loci.formats.IFormatWriter#testConvert(String[]) */
  public boolean testConvert(String[] args)
    throws FormatException, IOException
  {
    return testConvert(this, args);
  }

  // -- Utility methods --

  /** A utility method for converting a file from the command line. */
  public static boolean testConvert(IFormatWriter writer, String[] args)
    throws FormatException, IOException
  {
    String className = writer.getClass().getName();
    if (args == null || args.length < 2) {
      System.out.println("To convert a file to " + writer.getFormat() +
        " format, run:");
      System.out.println("  java " + className + " in_file out_file");
      return false;
    }
    String in = args[0];
    String out = args[1];
    System.out.print(in + " -> " + out + " ");

    ImageReader reader = new ImageReader();
    long start = System.currentTimeMillis();
    int num = reader.getImageCount(in);
    long mid = System.currentTimeMillis();
    long read = 0, write = 0;
    for (int i=0; i<num; i++) {
      long s = System.currentTimeMillis();
      Image image = reader.openImage(in, i);
      long m = System.currentTimeMillis();
      writer.save(out, image, i == num - 1);
      long e = System.currentTimeMillis();
      System.out.print(".");
      read += m - s;
      write += e - m;
    }
    long end = System.currentTimeMillis();
    System.out.println(" [done]");

    // output timing results
    float sec = (end - start) / 1000f;
    long initial = mid - start;
    float readAvg = (float) read / num;
    float writeAvg = (float) write / num;
    System.out.println(sec + "s elapsed (" +
      readAvg + "+" + writeAvg + "ms per image, " + initial + "ms overhead)");

    return true;
  }

}
