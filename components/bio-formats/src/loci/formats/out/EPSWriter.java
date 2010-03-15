//
// EPSWriter.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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

package loci.formats.out;

import java.io.IOException;

import loci.common.DataTools;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataRetrieve;

/**
 * EPSWriter is the file format writer for Encapsulated PostScript (EPS) files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/out/EPSWriter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/out/EPSWriter.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class EPSWriter extends FormatWriter {

  /** Current file. */
  protected RandomAccessOutputStream out;

  // -- Constructor --

  public EPSWriter() {
    super("Encapsulated PostScript", new String[] {"eps", "epsi"});
  }

  // -- IFormatWriter API methods --

  /* @see loci.formats.IFormatWriter#saveBytes(byte[], int, boolean, boolean) */
  public void saveBytes(byte[] buf, int series, boolean lastInSeries,
    boolean last) throws FormatException, IOException
  {
    if (buf == null) {
      throw new FormatException("Byte array is null");
    }

    out = new RandomAccessOutputStream(currentId);

    MetadataRetrieve meta = getMetadataRetrieve();
    MetadataTools.verifyMinimumPopulated(meta, series);
    int width = meta.getPixelsSizeX(series, 0).intValue();
    int height = meta.getPixelsSizeY(series, 0).intValue();
    int type =
      FormatTools.pixelTypeFromString(meta.getPixelsPixelType(series, 0));
    Integer channels = meta.getLogicalChannelSamplesPerPixel(series, 0);
    if (channels == null) {
      LOGGER.warn("SamplesPerPixel #0 is null.  It is assumed to be 1.");
    }
    int nChannels = channels == null ? 1 : channels.intValue();

    if (!DataTools.containsValue(getPixelTypes(), type)) {
      throw new FormatException("Unsupported image type '" +
        FormatTools.getPixelTypeString(type) + "'.");
    }

    // write the header

    out.writeBytes("%!PS-Adobe-2.0 EPSF-1.2\n");
    out.writeBytes("%%Title: " + currentId + "\n");
    out.writeBytes("%%Creator: LOCI Bio-Formats\n");
    out.writeBytes("%%Pages: 1\n");
    out.writeBytes("%%BoundingBox: 0 0 " + width + " " + height + "\n");
    out.writeBytes("%%EndComments\n\n");

    out.writeBytes("/ld {load def} bind def\n");
    out.writeBytes("/s /stroke ld /f /fill ld /m /moveto ld /l " +
      "/lineto ld /c /curveto ld /rgb {255 div 3 1 roll 255 div 3 1 " +
      "roll 255 div 3 1 roll setrgbcolor} def\n");
    out.writeBytes("0 0 translate\n");
    out.writeBytes(((float) width) + " " + ((float) height) + " scale\n");
    out.writeBytes("/picstr 40 string def\n");
    if (nChannels == 1) {
      out.writeBytes(width + " " + height + " 8 [" + width + " 0 0 " +
        (-1*height) + " 0 " + height + "] {currentfile picstr " +
        "readhexstring pop} image\n");
    }
    else {
      out.writeBytes(width + " " + height + " 8 [" + width + " 0 0 " +
        (-1*height) + " 0 " + height + "] {currentfile picstr " +
        "readhexstring pop} false 3 colorimage\n");
    }

    // write pixel data
    // for simplicity, write 80 char lines

    int planeSize = width * height;
    int charCount = 0;
    for (int i=0; i<buf.length; i++) {
      int index = interleaved || nChannels == 1 ? i :
        (i % nChannels) * planeSize + (i / nChannels);
      String s = Integer.toHexString(buf[index]);
      // only want last 2 characters of s
      if (s.length() > 1) s = s.substring(s.length() - 2);
      else s = "0" + s;
      out.writeBytes(s);
      charCount++;
      if (charCount == 40) {
        out.writeBytes("\n");
        charCount = 0;
      }
    }

    // write footer

    out.writeBytes("showpage\n");
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
