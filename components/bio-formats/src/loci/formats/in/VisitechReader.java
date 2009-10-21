//
// VisitechReader.java
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

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * VisitechReader is the file format reader for Visitech XYS files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/VisitechReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/VisitechReader.java">SVN</a></dd></dl>
 */
public class VisitechReader extends FormatReader {

  // -- Constants --

  public static final String[] HTML_SUFFIX = {"html"};

  // -- Fields --

  /** Files in this dataset. */
  private Vector<String> files;

  // -- Constructor --

  public VisitechReader() {
    super("Visitech XYS", new String[] {"xys", "html"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, "xys")) return true;

    // verify that there is an .xys file in the same directory
    if (name.indexOf(" ") == -1) return false;
    String prefix = name.substring(0, name.lastIndexOf(" "));
    Location xys = new Location(prefix + " 1.xys");
    return xys.exists();
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int plane = FormatTools.getPlaneSize(this);

    int div = getSizeZ() * getSizeT();
    int fileIndex = (series * getSizeC()) + no / div;
    int planeIndex = no % div;

    String file = files.get(fileIndex);
    RandomAccessInputStream s = new RandomAccessInputStream(file);
    s.seek(374);
    while (s.read() != (byte) 0xf0);
    s.skipBytes(1);
    if (s.readInt() == 0) s.skipBytes(4 + ((plane + 164) * planeIndex));
    else {
      if (planeIndex == 0) s.seek(s.getFilePointer() - 4);
      else s.skipBytes((plane + 164) * planeIndex - 4);
    }

    readPlane(s, x, y, w, h, buf);
    s.close();
    return buf;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    Vector<String> v = new Vector<String>();
    v.add(currentId);
    if (!noPixels && files != null) {
      int nFiles = getSizeC();
      for (int i=0; i<nFiles; i++) {
        v.add(files.get(getSeries() * nFiles + i));
      }
    }
    return v.toArray(new String[v.size()]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) files = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    // first, make sure we have the HTML file

    if (!checkSuffix(id, HTML_SUFFIX)) {
      String base = id.substring(0, id.lastIndexOf(" "));

      currentId = null;
      initFile(base + " Report.html");
      return;
    }

    // parse the HTML file

    String s = DataTools.readFile(id);

    // strip out "style", "a", and "script" tags

    s = s.replaceAll("<[bB][rR]>", "\n");
    s = s.replaceAll("<[sS][tT][yY][lL][eE]\\p{ASCII}*?" +
      "[sS][tT][yY][lL][eE]>", "");
    s = s.replaceAll("<[sS][cC][rR][iI][pP][tT]\\p{ASCII}*?" +
      "[sS][cC][rR][iI][pP][tT]>", "");

    StringTokenizer st = new StringTokenizer(s, "\n");
    String token = null, key = null, value = null;
    int numSeries = 0;
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
            core[0].sizeZ = Integer.parseInt(value);
          }
          else if (key.equals("Image bit depth")) {
            int bits = Integer.parseInt(value);
            while ((bits % 8) != 0) bits++;
            switch (bits) {
              case 16:
                core[0].pixelType = FormatTools.UINT16;
                break;
              case 32:
                core[0].pixelType = FormatTools.UINT32;
                break;
              default:
                core[0].pixelType = FormatTools.UINT8;
            }
          }
          else if (key.equals("Image dimensions")) {
            int n = value.indexOf(",");
            core[0].sizeX = Integer.parseInt(value.substring(1, n).trim());
            core[0].sizeY = Integer.parseInt(value.substring(n + 1,
              value.length() - 1).trim());
          }
          else if (key.startsWith("Channel Selection")) {
            core[0].sizeC++;
          }
          else if (key.startsWith("Microscope XY")) {
            numSeries++;
          }
          addGlobalMeta(key, value);
        }

        if (token.indexOf("pixels") != -1) {
          core[0].sizeC++;
          core[0].imageCount +=
            Integer.parseInt(token.substring(0, token.indexOf(" ")));
        }
        else if (token.startsWith("Time Series")) {
          int idx = token.indexOf(";") + 1;
          String ss = token.substring(idx, token.indexOf(" ", idx)).trim();
          core[0].sizeT = Integer.parseInt(ss);
        }
      }
    }

    if (getSizeT() == 0) {
      core[0].sizeT = getImageCount() / (getSizeZ() * getSizeC());
      if (getSizeT() == 0) core[0].sizeT = 1;
    }
    if (getImageCount() == 0) {
      core[0].imageCount = getSizeZ() * getSizeC() * getSizeT();
    }

    if (numSeries > 1) {
      int x = getSizeX();
      int y = getSizeY();
      int z = getSizeZ();
      int c = getSizeC() / numSeries;
      int t = getSizeT();
      int count = z * c * t;
      int ptype = getPixelType();
      core = new CoreMetadata[numSeries];
      for (int i=0; i<numSeries; i++) {
        core[i] = new CoreMetadata();
        core[i].sizeX = x;
        core[i].sizeY = y;
        core[i].sizeZ = z;
        core[i].sizeC = c;
        core[i].sizeT = t;
        core[i].imageCount = count;
        core[i].pixelType = ptype;
      }
    }

    for (int i=0; i<getSeriesCount(); i++) {
      core[i].rgb = false;
      core[i].dimensionOrder = "XYZTC";
      core[i].interleaved = false;
      core[i].littleEndian = true;
      core[i].indexed = false;
      core[i].falseColor = false;
      core[i].metadataComplete = true;
    }

    // find pixels files - we think there is one channel per file

    files = new Vector<String>();

    int ndx = currentId.lastIndexOf(File.separator) + 1;
    String base = currentId.substring(ndx, currentId.lastIndexOf(" "));

    File f = new File(currentId).getAbsoluteFile();
    String file = f.exists() ? f.getParent() + File.separator : "";

    if (numSeries == 0) numSeries = 1;
    for (int i=0; i<getSizeC()*numSeries; i++) {
      files.add(file + base + " " + (i + 1) + ".xys");
    }
    files.add(currentId);

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);
    for (int i=0; i<numSeries; i++) {
      store.setImageName("Position " + (i + 1), i);
      MetadataTools.setDefaultCreationDate(store, id, i);
    }
  }

}
