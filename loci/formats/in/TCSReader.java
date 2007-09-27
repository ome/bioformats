//
// TCSReader.java
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

package loci.formats.in;

import java.io.IOException;
import java.text.*;
import java.util.*;
import loci.formats.*;

/**
 * TCSReader is the file format reader for Leica TCS TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/TCSReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/TCSReader.java">SVN</a></dd></dl>
 */
public class TCSReader extends BaseTiffReader {

  // -- Constructor --

  public TCSReader() {
    super("Leica TCS TIFF", new String[] {"tif", "tiff"});
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (!super.isThisType(name, open)) return false; // check extension
    if (!open) return true; // not allowed to check the file contents

    // just checking the filename isn't enough to differentiate between
    // Leica TCS and regular TIFF; open the file and check more thoroughly
    try {
      RandomAccessStream ras = new RandomAccessStream(name);
      Hashtable ifd = TiffTools.getFirstIFD(ras);
      ras.close();
      if (ifd == null) return false;

      String document = (String) ifd.get(new Integer(TiffTools.DOCUMENT_NAME));
      if (document == null) return false;
      return document.startsWith("CHANNEL");
    }
    catch (IOException e) { return false; }
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    int[] ch = new int[ifds.length];
    int[] idx = new int[ifds.length];
    long[] stamp = new long[ifds.length];

    int channelCount = 0;
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss.SSS");

    for (int i=0; i<ifds.length; i++) {
      String document =
        (String) ifds[i].get(new Integer(TiffTools.DOCUMENT_NAME));

      int index = document.indexOf("INDEX");
      String c = document.substring(8, index).trim();
      ch[i] = Integer.parseInt(c);
      if (ch[i] > channelCount) channelCount = ch[i];

      String n = document.substring(index + 6,
        document.indexOf(" ", index + 6)).trim();
      idx[i] = Integer.parseInt(n);

      String date = document.substring(document.indexOf(" ", index + 6),
        document.indexOf("FORMAT")).trim();
      stamp[i] = fmt.parse(date, new ParsePosition(0)).getTime();
    }

    core.sizeT[0] = 0;
    core.currentOrder[0] = core.rgb[0] ? "XYC" : "XY";

    // determine the axis sizes and ordering
    boolean unique = true;
    for (int i=0; i<stamp.length; i++) {
      for (int j=i+1; j<stamp.length; j++) {
        if (stamp[j] == stamp[i]) {
          unique = false;
          break;
        }
      }
      if (unique) {
        core.sizeT[0]++;
        if (core.currentOrder[0].indexOf("T") < 0) core.currentOrder[0] += "T";
      }
      else if (i > 0) {
        if ((ch[i] != ch[i - 1]) && core.currentOrder[0].indexOf("C") < 0) {
          core.currentOrder[0] += "C";
        }
        else if (core.currentOrder[0].indexOf("Z") < 0) {
          core.currentOrder[0] += "Z";
        }
      }
      unique = true;
    }

    if (core.currentOrder[0].indexOf("Z") < 0) core.currentOrder[0] += "Z";
    if (core.currentOrder[0].indexOf("C") < 0) core.currentOrder[0] += "C";
    if (core.currentOrder[0].indexOf("T") < 0) core.currentOrder[0] += "T";

    if (core.sizeT[0] == 0) core.sizeT[0] = 1;
    if (channelCount == 0) channelCount = 1;
    core.sizeZ[0] = ifds.length / (core.sizeT[0] * channelCount);
    core.sizeC[0] *= channelCount;
    core.imageCount[0] = core.sizeZ[0] * core.sizeT[0] * channelCount;

    // cut up comment

    String comment = (String) getMeta("Comment");
    if (comment != null && comment.startsWith("[")) {
      StringTokenizer st = new StringTokenizer(comment, "\n");
      while (st.hasMoreTokens()) {
        String token = st.nextToken();
        if (!token.startsWith("[")) {
          int eq = token.indexOf("=");
          String key = token.substring(0, eq);
          String value = token.substring(eq + 1);
          addMeta(key, value);
        }
      }
      metadata.remove("Comment");
    }
  }

}
