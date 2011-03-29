//
// VolocityReader.java
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
import java.util.ArrayList;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

/**
 * VolocityReader is the file format reader for Volocity library files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/VolocityReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/VolocityReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class VolocityReader extends FormatReader {

  // -- Constants --

  private static final String DATA_DIR = "Data";
  private static final int HEADER_SIZE = 512;

  // -- Fields --

  private String[] pixelsFiles;
  private String[] lutFiles;
  private ArrayList<String> extraFiles;

  // -- Constructor --

  /** Constructs a new Volocity reader. */
  public VolocityReader() {
    super("Volocity Library", "mvd2");
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    ArrayList<String> files = new ArrayList<String>();
    files.addAll(extraFiles);
    files.add(pixelsFiles[getSeries()]);
    files.add(lutFiles[getSeries()]);
    return files.toArray(new String[files.size()]);
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 2;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    String check = stream.readString(blockLen);
    return check.equals("JL") || check.equals("LJ");
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    RandomAccessInputStream pix =
      new RandomAccessInputStream(pixelsFiles[getSeries()]);
    pix.seek(HEADER_SIZE + no * FormatTools.getPlaneSize(this));
    readPlane(pix, x, y, w, h, buf);
    pix.close();
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      pixelsFiles = null;
      lutFiles = null;
      extraFiles = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    extraFiles = new ArrayList<String>();

    Location file = new Location(id).getAbsoluteFile();
    extraFiles.add(file.getAbsolutePath());

    Location parent = file.getParentFile();
    Location dir = new Location(parent, DATA_DIR);

    String[] files = dir.list(true);
    ArrayList<String> pixels = new ArrayList<String>();
    ArrayList<String> luts = new ArrayList<String>();

    for (String f : files) {
      String path = new Location(dir, f).getAbsolutePath();
      if (checkSuffix(f, "aiix")) {
        luts.add(path);
      }
      else if (checkSuffix(f, "aisf")) {
        pixels.add(path);
      }
      else {
        extraFiles.add(path);
      }
    }

    pixelsFiles = pixels.toArray(new String[pixels.size()]);
    lutFiles = luts.toArray(new String[luts.size()]);

    core = new CoreMetadata[pixelsFiles.length];

    for (int i=0; i<core.length; i++) {
      core[i] = new CoreMetadata();
    }
  }

}
