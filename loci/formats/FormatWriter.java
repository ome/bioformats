//
// FormatWriter.java
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

import java.awt.Image;
import java.awt.image.ColorModel;
import java.io.IOException;

/** Abstract superclass of all biological file format writers. */
public abstract class FormatWriter extends FormatHandler
  implements IFormatWriter
{

  // -- Constants --

  /** Debugging flag. */
  public static boolean debug = false;

  /** Debugging level. 1=basic, 2=extended, 3=everything, 4=insane. */
  public static int debugLevel = 1;

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

  /* @see IFormatWriter#saveImage(String, Image, boolean) */
  public abstract void saveImage(String id, Image image, boolean last)
    throws FormatException, IOException;

  /* @see IFormatWriter#save(String, Image, boolean) */ 
  public void save(String id, Image image, boolean last)
    throws FormatException, IOException
  {
    saveImage(id, image, last);
  }

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

  /* @see IFormatWriter#getPixelTypes(String) */
  public int[] getPixelTypes(String id) throws FormatException, IOException {
    return new int[] {FormatTools.UINT8, FormatTools.UINT16,
      FormatTools.UINT32, FormatTools.FLOAT};
  }

  /* @see IFormatWriter#isSupportedType(String, int) */
  public boolean isSupportedType(String id, int type)
    throws FormatException, IOException
  {
    int[] types = getPixelTypes(id);
    for (int i=0; i<types.length; i++) {
      if (type == types[i]) return true;
    }
    return false;
  }

  /* @see IFormatWriter#testConvert(String[]) */
  public boolean testConvert(String[] args)
    throws FormatException, IOException
  {
    return FormatTools.testConvert(this, args);
  }

}
