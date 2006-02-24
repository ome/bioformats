//
// ImageFamily.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2006 Curtis Rueden.

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

package loci.visbio.data;

import java.io.IOException;
import loci.formats.*;
import visad.data.BadFormException;

/** A container for VisAD data types that provide data as image planes. */
public class ImageFamily extends visad.data.bio.LociForm {

  public ImageFamily() {
    super(new ImageReader(), new ImageWriter());
  }

  public String getFormat(String id) throws BadFormException, IOException {
    try {
      return ((ImageReader) reader).getFormat(id);
    }
    catch (FormatException e) { throw new BadFormException(e.getMessage()); }
  }

  public FormatReader getReader(Class c) {
    return ((ImageReader) reader).getReader(c);
  }

  public FormatWriter getWriter(Class c) {
    return ((ImageWriter) writer).getWriter(c);
  }

  public static void main(String[] args) throws Exception {
    new ImageFamily().testRead(args);
  }

}
