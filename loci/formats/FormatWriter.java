//
// FormatWriter.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats;

import java.awt.Image;
import java.io.IOException;

/** Abstract superclass of all biological file format writers. */
public abstract class FormatWriter extends FormatHandler {

  // -- Fields --

  /** Frame rate to use when writing in frames per second, if applicable. */
  protected int fps = 10;


  // -- Constructors --

  /** Constructs a format writer with the given name and default suffix. */
  public FormatWriter(String format, String suffix) { super(format, suffix); }

  /** Constructs a format writer with the given name and default suffixes. */
  public FormatWriter(String format, String[] suffixes) {
    super(format, suffixes);
  }


  // -- Abstract FormatWriter API methods --

  /**
   * Saves the given image to the specified (possibly already open) file.
   * If this image is the last one in the file, the last flag must be set.
   */
  public abstract void save(String id, Image image, boolean last)
    throws FormatException, IOException;


  // -- FormatWriter API methods --

  /** Saves the given images to the specified file. */
  public void save(String id, Image[] images)
    throws FormatException, IOException
  {
    for (int i=0; i<images.length; i++) {
      save(id, images[i], i == images.length - 1);
    }
  }

  /** Sets the frames per second to use when writing. */
  public void setFramesPerSecond(int fps) { this.fps = fps; }

  /** Gets the frames per second to use when writing. */
  public int getFramesPerSecond() { return fps; }

  /** A utility method for converting a file from the command line. */
  public void testConvert(String[] args) throws FormatException, IOException {
    String className = getClass().getName();
    if (args == null || args.length < 2) {
      System.out.println("To convert a file to " + format + " format, run:");
      System.out.println("  java " + className + " in_file out_file");
      return;
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
      Image image = reader.open(in, i);
      long m = System.currentTimeMillis();
      save(out, image, i == num - 1);
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
  }

}
