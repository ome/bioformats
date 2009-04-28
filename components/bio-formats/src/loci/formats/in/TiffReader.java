//
// TiffReader.java
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

package loci.formats.in;

import java.io.IOException;
import java.util.*;
import loci.common.*;
import loci.formats.*;

/**
 * TiffReader is the file format reader for regular TIFF files,
 * not of any specific TIFF variant.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/TiffReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/TiffReader.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 */
public class TiffReader extends BaseTiffReader {

  // -- Constants --

  public static final String[] TIFF_SUFFIXES =
    {"tif", "tiff", "tf2", "tf8", "btf"};

  // -- Constructor --

  /** Constructs a new Tiff reader. */
  public TiffReader() {
    super("Tagged Image File Format", TIFF_SUFFIXES);
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();
    String comment = TiffTools.getComment(ifds[0]);

    status("Checking comment style");

    if (ifds.length > 1) core[0].orderCertain = false;

    // check for ImageJ-style TIFF comment
    boolean ij = comment != null && comment.startsWith("ImageJ=");
    if (ij) {
      int nl = comment.indexOf("\n");
      put("ImageJ", nl < 0 ? comment.substring(7) : comment.substring(7, nl));
      metadata.remove("Comment");

      int z = 1, t = 1;
      int c = getSizeC();

      // parse ZCT sizes
      StringTokenizer st = new StringTokenizer(comment, "\n");
      while (st.hasMoreTokens()) {
        String token = st.nextToken();
        int value = 0;
        int eq = token.indexOf("=");
        if (eq != -1 && eq + 1 < token.length()) {
          try {
            value = Integer.parseInt(token.substring(eq + 1));
          }
          catch (NumberFormatException e) {
            if (debug) trace(e);
          }
        }

        if (token.startsWith("channels=")) c = value;
        else if (token.startsWith("slices=")) z = value;
        else if (token.startsWith("frames=")) t = value;
      }
      if (z * c * t == c) {
        t = getImageCount();
      }
      core[0].dimensionOrder = "XYCZT";

      if (z * t * (isRGB() ? 1 : c) == ifds.length) {
        core[0].sizeZ = z;
        core[0].sizeT = t;
        core[0].sizeC = c;
      }
      else if (ifds.length == 1 && z * t > ifds.length &&
        TiffTools.getCompression(ifds[0]) == TiffTools.UNCOMPRESSED)
      {
        // file is likely corrupt (missing end IFDs)
        //
        // ImageJ writes TIFF files like this:
        // IFD #0
        // comment
        // all pixel data
        // IFD #1
        // IFD #2
        // ...
        //
        // since we know where the pixel data is, we can create fake
        // IFDs in an attempt to read the rest of the pixels

        int planeSize = getSizeX() * getSizeY() * getRGBChannelCount() *
          FormatTools.getBytesPerPixel(getPixelType());
        long[] stripOffsets = TiffTools.getStripOffsets(ifds[0]);
        long[] stripByteCounts = TiffTools.getStripByteCounts(ifds[0]);

        long endOfFirstPlane = stripOffsets[stripOffsets.length - 1] +
          stripByteCounts[stripByteCounts.length - 1];
        long totalBytes = in.length() - endOfFirstPlane;
        int totalPlanes = (int) (totalBytes / planeSize) + 1;

        Hashtable ifd = ifds[0];
        ifds = new Hashtable[totalPlanes];
        ifds[0] = ifd;
        for (int i=1; i<totalPlanes; i++) {
          ifds[i] = new Hashtable(ifds[0]);
          long[] prevOffsets = TiffTools.getStripOffsets(ifds[i - 1]);
          long[] offsets = new long[stripOffsets.length];
          offsets[0] = prevOffsets[prevOffsets.length - 1] +
            stripByteCounts[stripByteCounts.length - 1];
          for (int j=1; j<offsets.length; j++) {
            offsets[j] = offsets[j - 1] + stripByteCounts[j - 1];
          }
          ifds[i].put(new Integer(TiffTools.STRIP_OFFSETS), offsets);
        }

        core[0].sizeZ = ifds.length;
        core[0].imageCount = ifds.length;
      }
      else {
        core[0].sizeT = ifds.length;
        core[0].imageCount = ifds.length;
      }
    }

    // check for MetaMorph-style TIFF comment
    Object software = TiffTools.getIFDValue(ifds[0], TiffTools.SOFTWARE);
    String check = software instanceof String ? (String) software :
      software instanceof String[] ? ((String[]) software)[0] : null;
    boolean metamorph = comment != null && software != null &&
      check.indexOf("MetaMorph") != -1;
    put("MetaMorph", metamorph ? "yes" : "no");

    if (metamorph) {
      // parse key/value pairs
      StringTokenizer st = new StringTokenizer(comment, "\n");
      while (st.hasMoreTokens()) {
        String line = st.nextToken();
        int colon = line.indexOf(":");
        if (colon < 0) {
          addMeta("Comment", line);
          continue;
        }
        String key = line.substring(0, colon);
        String value = line.substring(colon + 1);
        addMeta(key, value);
      }
    }
  }

}
