//
// EPSWriter.java
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

package loci.formats.out;

import java.awt.Image;
import java.awt.image.*;
import java.io.*;
import loci.formats.*;

/**
 * EPSWriter is the file format writer for Encapsulated PostScript (EPS) files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/out/EPSWriter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/out/EPSWriter.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class EPSWriter extends FormatWriter {

  /** Current file. */
  protected RandomAccessFile out;

  // -- Constructor --

  public EPSWriter() {
    super("Encapsulated PostScript", new String[] {"eps", "epsi"});
  }

  // -- IFormatWriter API methods --

  /* @see loci.formats.IFormatWriter#saveImage(Image, boolean) */
  public void saveImage(Image image, boolean last)
    throws FormatException, IOException
  {
    if (image == null) {
      throw new FormatException("Image is null");
    }

    out = new RandomAccessFile(currentId, "rw");

    BufferedImage img = (cm == null) ?
      ImageTools.makeBuffered(image) : ImageTools.makeBuffered(image, cm);

    // get the width and height of the image
    int width = img.getWidth();
    int height = img.getHeight();

    // retrieve pixel data for this plane
    byte[][] byteData = ImageTools.getBytes(img);

    // write the header

    DataTools.writeString(out, "%!PS-Adobe-2.0 EPSF-1.2\n");
    DataTools.writeString(out, "%%Title: " + currentId + "\n");
    DataTools.writeString(out, "%%Creator: LOCI Bio-Formats\n");
    DataTools.writeString(out, "%%Pages: 1\n");
    DataTools.writeString(out, "%%BoundingBox: 0 0 " + width +
      " " + height + "\n");
    DataTools.writeString(out, "%%EndComments\n\n");

    DataTools.writeString(out, "/ld {load def} bind def\n");
    DataTools.writeString(out, "/s /stroke ld /f /fill ld /m /moveto ld /l " +
      "/lineto ld /c /curveto ld /rgb {255 div 3 1 roll 255 div 3 1 " +
      "roll 255 div 3 1 roll setrgbcolor} def\n");
    DataTools.writeString(out, "0 0 translate\n");
    DataTools.writeString(out, ((float) width) + " " + ((float) height) +
      " scale\n");
    DataTools.writeString(out, "/picstr 40 string def\n");
    if (byteData.length == 1) {
      DataTools.writeString(out, width + " " + height + " 8 [" + width +
        " 0 0 " + (-1*height) + " 0 " + height + "] {currentfile picstr " +
        "readhexstring pop} image\n");

      // write pixel data
      // for simplicity, write 80 char lines

      int charCount = 0;
      for (int i=0; i<byteData[0].length; i++) {
        for (int j=0; j<1; j++) {
          String s = Integer.toHexString(byteData[j][i]);
          // only want last 2 characters of s
          if (s.length() > 1) s = s.substring(s.length() - 2);
          else s = "0" + s;
          DataTools.writeString(out, s);
          charCount++;
          if (charCount == 40) {
            DataTools.writeString(out, "\n");
            charCount = 0;
          }
        }
      }
    }
    else {
      DataTools.writeString(out, width + " " + height + " 8 [" + width +
        " 0 0 " + (-1*height) + " 0 " + height + "] {currentfile picstr " +
        "readhexstring pop} false 3 colorimage\n");

      // write pixel data
      // for simplicity, write 80 char lines

      int charCount = 0;
      for (int i=0; i<byteData[0].length; i++) {
        for (int j=0; j<byteData.length; j++) {
          String s = Integer.toHexString(byteData[j][i]);
          // only want last 2 characters of s
          if (s.length() > 1) s = s.substring(s.length() - 2);
          else s = "0" + s;
          DataTools.writeString(out, s);
          charCount++;
          if (charCount == 40) {
            DataTools.writeString(out, "\n");
            charCount = 0;
          }
        }
      }
    }

    // write footer

    DataTools.writeString(out, "showpage\n");
    out.close();
  }

  /* @see loci.formats.IFormatWriter#getPixelTypes() */
  public int[] getPixelTypes() {
    return new int[] {FormatTools.UINT8};
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    if (out != null) out.close();
    out = null;
    currentId = null;
    initialized = false;
  }

}
