//
// VisitechReader.java
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

import java.io.*;
import java.util.*;
import loci.formats.*;

/**
 * VisitechReader is the file format reader for Visitech XYS files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/VisitechReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/VisitechReader.java">SVN</a></dd></dl>
 */
public class VisitechReader extends FormatReader {

  // -- Fields --

  /** Files in this dataset. */
  private Vector files;

  // -- Constructor --

  public VisitechReader() {
    super("Visitech XYS", new String[] {"xys", "html"});
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return false;
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    int plane = core.sizeX[0] * core.sizeY[0] *
      FormatTools.getBytesPerPixel(core.pixelType[0]);

    int div = core.sizeZ[0] * core.sizeT[0];
    int fileIndex = no / div;
    int planeIndex = no % div;

    String file = (String) files.get(fileIndex);
    RandomAccessStream s = new RandomAccessStream(file);
    s.skipBytes(374);
    while (s.read() != (byte) 0xf0);
    s.skipBytes((plane + 164) * planeIndex + 1);
    s.read(buf);
    s.close();
    return buf;
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    if (files == null) return new String[0];
    return (String[]) files.toArray(new String[0]);
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    // first, make sure we have the HTML file

    if (!id.toLowerCase().endsWith("html")) {
      Location file = new Location(id).getAbsoluteFile();
      String path = file.exists() ? file.getPath() : id;
      int ndx = path.lastIndexOf(File.separator);
      String base = path.substring(ndx + 1, path.indexOf(" ", ndx));

      String suffix = " Report.html";
      currentId = null;
      initFile(file.exists() ? new Location(file.getParent(),
        base + suffix).getAbsolutePath() : base + suffix);
      return;
    }

    // parse the HTML file

    in = new RandomAccessStream(id);
    String s = in.readString((int) in.length());

    // strip out "style", "a", and "script" tags

    s = s.replaceAll("<[bB][rR]>", "\n");
    s = s.replaceAll("<[sS][tT][yY][lL][eE]\\p{ASCII}*?" +
      "[sS][tT][yY][lL][eE]>", "");
    s = s.replaceAll("<[sS][cC][rR][iI][pP][tT]\\p{ASCII}*?" +
      "[sS][cC][rR][iI][pP][tT]>", "");

    StringTokenizer st = new StringTokenizer(s, "\n");
    String token = null, key = null, value = null;
    while (st.hasMoreTokens()) {
      token = st.nextToken().trim();

      if ((token.startsWith("<") && !token.startsWith("</")) ||
        token.indexOf("pixels") != -1)
      {
        token = token.replaceAll("<.*?>", "");
        int ndx = token.indexOf(":");

        if (ndx != -1) {
          key = token.substring(0, ndx).trim();
          value = token.substring(ndx + 1).trim();

          if (key.equals("Number of steps")) {
            core.sizeZ[0] = Integer.parseInt(value);
          }
          else if (key.equals("Image bit depth")) {
            int bits = Integer.parseInt(value);
            while ((bits % 8) != 0) bits++;
            switch (bits) {
              case 16:
                core.pixelType[0] = FormatTools.UINT16;
                break;
              case 32:
                core.pixelType[0] = FormatTools.UINT32;
                break;
              default: core.pixelType[0] = FormatTools.UINT8;
            }
          }
          else if (key.equals("Image dimensions")) {
            int n = value.indexOf(",");
            core.sizeX[0] = Integer.parseInt(value.substring(1, n).trim());
            core.sizeY[0] = Integer.parseInt(value.substring(n + 1,
              value.length() - 1).trim());
          }

          addMeta(key, value);
        }

        if (token.indexOf("pixels") != -1) {
          core.sizeC[0]++;
          core.imageCount[0] +=
            Integer.parseInt(token.substring(0, token.indexOf(" ")));
        }
      }
    }

    core.sizeT[0] = core.imageCount[0] / (core.sizeZ[0] * core.sizeC[0]);
    core.rgb[0] = false;
    core.currentOrder[0] = "XYZTC";
    core.interleaved[0] = false;
    core.littleEndian[0] = true;
    core.indexed[0] = false;
    core.falseColor[0] = false;
    core.metadataComplete[0] = true;

    // find pixels files - we think there is one channel per file

    files = new Vector();

    int ndx = currentId.lastIndexOf(File.separator);
    String base = currentId.substring(ndx + 1, currentId.indexOf(" ", ndx));

    File f = new File(currentId).getAbsoluteFile();

    for (int i=0; i<core.sizeC[0]; i++) {
      files.add((f.exists() ? f.getParent() + File.separator : "") + base +
        " " + (i + 1) + ".xys");
    }
    files.add(currentId);

    MetadataStore store = getMetadataStore();
    store.setImage(currentId, null, null, null);
    FormatTools.populatePixels(store, this);

    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null);
    }

  }

}
