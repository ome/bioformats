//
// JPEGWriter.java
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
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/** JPEGWriter is the file format writer for JPEG files. */
public class JPEGWriter extends FormatWriter {

  // -- Constructor --

  public JPEGWriter() {
    super("Joint Photographic Experts Group",
      new String[] {"jpg", "jpeg", "jpe"});
  }


  // -- FormatWriter API methods --

  /**
   * Saves the given image to the specified (possibly already open) file.
   * The last flag is ignored, since this writer produces single-image JPEGs.
   */
  public void save(String id, Image image, boolean last)
    throws FormatException, IOException
  {
    ImageIO.write(ImageTools.makeImage(image), "jpeg", new File(id));
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new JPEGWriter().testConvert(args);
  }

}
