//
// TiffWriter.java
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

import java.awt.*;
import java.util.Hashtable;
import java.io.*;

/** TiffWriter is the file format writer for TIFF files. */
public class TiffWriter extends FormatWriter {

  // -- Fields --

  /** The current IFD, containing metadata for the plane(s) to be written. */
  private Hashtable currentIFD;

  /** The last offset written to. */
  private int lastOffset;

  /** Current output stream. */
  private OutputStream out;


  // -- Constructor --

  public TiffWriter() {
    super("Tagged Image File Format", new String[] {"tif", "tiff"});
    lastOffset = 0;
  }


  // -- TiffWriter API methods --

  /**
   * Saves the given image to the specified (possibly already open) file.
   * The IFD hashtable allows specification of TIFF parameters such as bit
   * depth, compression and units.  If this image is the last one in the file,
   * the last flag must be set.
   */
  public void saveImage(String id, Image image, Hashtable ifd, boolean last)
    throws IOException, FormatException
  {
    if (!id.equals(currentId)) {
      if (out != null) {
        System.err.println("Warning: abandoning previous TIFF file (" +
          currentId + ")");
        out.close();
      }
      currentId = id;
      out = new FileOutputStream(currentId);
      DataOutputStream dataOut = new DataOutputStream(out);
      dataOut.writeByte(TiffTools.BIG);
      dataOut.writeByte(TiffTools.BIG);
      dataOut.writeShort(TiffTools.MAGIC_NUMBER);
      dataOut.writeInt(8); // offset to first IFD
      lastOffset = 8;
    }
    lastOffset += TiffTools.writeImage(image, ifd, out, lastOffset, last);
    if (last) {
      out.close();
      out = null;
      currentId = null;
    }
  }


  // -- FormatWriter API methods --

  /**
   * Saves the given image to the specified (possibly already open) file.
   * If this image is the last one in the file, the last flag must be set.
   */
  public void save(String id, Image image, boolean last)
    throws FormatException, IOException
  {
    saveImage(id, image, null, last);
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new TiffWriter().testConvert(args);
  }

}
