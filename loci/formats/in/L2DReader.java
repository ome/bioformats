//
// L2DReader.java
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
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * L2DReader is the file format reader for Li-Cor L2D datasets.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/L2DReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/L2DReader.java">SVN</a></dd></dl>
 */
public class L2DReader extends FormatReader {

  // -- Fields --

  /** List of constituent TIFF files. */
  private Vector[] tiffs;

  /** List of all files in the dataset. */
  private Vector used;

  private TiffReader[][] readers;

  // -- Constructor --

  /** Construct a new L2D reader. */
  public L2DReader() {
    super("Li-Cor L2D", "l2d");
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return false;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length, w, h);

    return readers[series][no].openBytes(0, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    return (String[]) used.toArray(new String[0]);
  }

  /* @see loci.formats.IFormatReader#close() */
  public void close() throws IOException {
    super.close();
    tiffs = null;
    readers = null;
    used = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("L2DReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    used = new Vector();
    used.add(new Location(id).getAbsolutePath());

    Location parent = new Location(id).getAbsoluteFile().getParentFile();

    // parse key/value pairs from file - this gives us a list of scans

    Vector scans = new Vector();

    String line = in.readLine().trim();
    while (line != null && line.length() > 0) {
      if (!line.startsWith("#")) {
        String key = line.substring(0, line.indexOf("="));
        String value = line.substring(line.indexOf("=") + 1);
        addMeta(key, value);

        if (key.equals("ScanNames")) {
          StringTokenizer names = new StringTokenizer(value, ",");
          while (names.hasMoreTokens()) {
            scans.add(names.nextToken().trim());
          }
        }
      }
      line = in.readLine().trim();
    }
    in.close();

    // read metadata from each scan

    tiffs = new Vector[scans.size()];

    core = new CoreMetadata(scans.size());

    for (int i=0; i<scans.size(); i++) {
      tiffs[i] = new Vector();
      String scanName = (String) scans.get(i);
      Location scanDir = new Location(parent, scanName);

      // read .scn file from each scan

      String scanPath =
        new Location(scanDir, scanName + ".scn").getAbsolutePath();
      used.add(scanPath);
      RandomAccessStream scan = new RandomAccessStream(scanPath);
      line = scan.readLine().trim();
      while (line != null && line.length() > 0) {
        if (!line.startsWith("#")) {
          String key = line.substring(0, line.indexOf("="));
          String value = line.substring(line.indexOf("=") + 1);
          addMeta(scanName + " " + key, value);

          if (key.equals("ExperimentNames")) {
            // TODO : parse experiment metadata - this is typically a list of
            //        overlay shapes, or analysis data
          }
          else if (key.equals("ImageNames")) {
            StringTokenizer names = new StringTokenizer(value, ",");
            while (names.hasMoreTokens()) {
              String path = names.nextToken().trim();
              String tiff = new Location(scanDir, path).getAbsolutePath();
              used.add(tiff);
              tiffs[i].add(tiff);
            }
          }
        }
        line = scan.readLine().trim();
      }
    }

    readers = new TiffReader[scans.size()][];

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    for (int i=0; i<scans.size(); i++) {
      core.imageCount[i] = tiffs[i].size();
      core.sizeC[i] = tiffs[i].size();
      core.sizeT[i] = 1;
      core.sizeZ[i] = 1;
      core.currentOrder[i] = "XYCZT";

      readers[i] = new TiffReader[tiffs[i].size()];

      for (int t=0; t<tiffs[i].size(); t++) {
        readers[i][t] = new TiffReader();
        readers[i][t].setId((String) tiffs[i].get(t));
        if (t == 0) {
          core.sizeX[i] = readers[i][t].getSizeX();
          core.sizeY[i] = readers[i][t].getSizeY();
          core.sizeC[i] *= readers[i][t].getSizeC();
          core.rgb[i] = readers[i][t].isRGB();
          core.indexed[i] = readers[i][t].isIndexed();
          core.littleEndian[i] = readers[i][t].isLittleEndian();
          core.pixelType[i] = readers[i][t].getPixelType();
        }
      }
      store.setImageName("", i);
      store.setImageCreationDate(
        DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), i);
    }

    MetadataTools.populatePixels(store, this);
  }

}
