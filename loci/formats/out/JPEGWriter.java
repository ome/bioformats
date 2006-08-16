//
// JPEGWriter.java
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

package loci.formats.out;

import java.io.IOException;
import loci.formats.FormatException;

/** JPEGWriter is the file format writer for JPEG files. */
public class JPEGWriter extends ImageIOWriter {

  // -- Constructor --

  public JPEGWriter() {
    super("Joint Photographic Experts Group",
      new String[] {"jpg", "jpeg", "jpe"}, "jpeg");
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new JPEGWriter().testConvert(args);
  }

}
