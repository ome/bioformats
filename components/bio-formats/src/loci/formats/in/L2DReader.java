//
// L2DReader.java
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
import java.util.StringTokenizer;
import java.util.Vector;

import loci.common.DateTools;
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
 * L2DReader is the file format reader for Li-Cor L2D datasets.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/L2DReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/L2DReader.java">SVN</a></dd></dl>
 */
public class L2DReader extends FormatReader {

  // -- Constants --

  public static final String DATE_FORMAT = "yyyy, m, d";

  // -- Fields --

  /** List of constituent TIFF files. */
  private Vector[] tiffs;

  /** List of all files in the dataset. */
  private Vector<String> used;

  private MinimalTiffReader reader;

  // -- Constructor --

  /** Construct a new L2D reader. */
  public L2DReader() {
    super("Li-Cor L2D", new String[] {"l2d", "scn"});
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    reader.setId((String) tiffs[series].get(no));
    return reader.openBytes(0, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#getUsedFiles(boolean) */
  public String[] getUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (noPixels) {
      Vector<String> files = new Vector<String>();
      for (int i=0; i<used.size(); i++) {
        String f = used.get(i).toLowerCase();
        if (!f.endsWith(".tif") && !f.endsWith(".tiff")) {
          files.add(used.get(i));
        }
      }
      return files.toArray(new String[0]);
    }
    return used.toArray(new String[0]);
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    tiffs = null;
    if (reader != null) reader.close();
    reader = null;
    used = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("L2DReader.initFile(" + id + ")");

    // NB: This format cannot be imported using omebf.
    // See Trac ticket #266 for details.

    if (id.toLowerCase().endsWith(".scn")) {
      // find the corresponding .l2d file
      Location parent = new Location(id).getAbsoluteFile().getParentFile();
      String[] list = parent.list();
      for (int i=0; i<list.length; i++) {
        if (list[i].toLowerCase().endsWith(".l2d")) {
          initFile(new Location(parent.getAbsolutePath(),
            list[i]).getAbsolutePath());
          break;
        }
      }
      return;
    }

    super.initFile(id);
    in = new RandomAccessInputStream(id);

    used = new Vector<String>();
    used.add(new Location(id).getAbsolutePath());

    Location parent = new Location(id).getAbsoluteFile().getParentFile();

    // parse key/value pairs from file - this gives us a list of scans

    Vector scans = new Vector();

    String line = in.readLine().trim();
    while (line != null && line.length() > 0) {
      if (!line.startsWith("#")) {
        String key = line.substring(0, line.indexOf("="));
        String value = line.substring(line.indexOf("=") + 1);
        addGlobalMeta(key, value);

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

    core = new CoreMetadata[scans.size()];

    Vector comments = new Vector();
    Vector wavelengths = new Vector();
    Vector dates = new Vector();
    String model = null;

    for (int i=0; i<scans.size(); i++) {
      setSeries(i);
      core[i] = new CoreMetadata();
      tiffs[i] = new Vector();
      String scanName = (String) scans.get(i);
      Location scanDir = new Location(parent, scanName);

      // read .scn file from each scan

      String scanPath =
        new Location(scanDir, scanName + ".scn").getAbsolutePath();
      addDirectory(scanDir.getAbsolutePath());
      RandomAccessInputStream scan = new RandomAccessInputStream(scanPath);
      line = scan.readLine().trim();
      while (line != null && line.length() > 0) {
        if (!line.startsWith("#")) {
          String key = line.substring(0, line.indexOf("="));
          String value = line.substring(line.indexOf("=") + 1);
          addSeriesMeta(key, value);

          if (key.equals("ExperimentNames")) {
            // TODO : parse experiment metadata - this is typically a list of
            //        overlay shapes, or analysis data
          }
          else if (key.equals("ImageNames")) {
            StringTokenizer names = new StringTokenizer(value, ",");
            while (names.hasMoreTokens()) {
              String path = names.nextToken().trim();
              String tiff = new Location(scanDir, path).getAbsolutePath();
              tiffs[i].add(tiff);
            }
          }
          else if (key.equals("Comments")) {
            comments.add(value);
          }
          else if (key.equals("ScanDate")) {
            dates.add(value);
          }
          else if (key.equals("ScannerName")) {
            model = value;
          }
          else if (key.equals("ScanChannels")) {
            wavelengths.add(value);
          }
        }
        line = scan.readLine().trim();
      }
      if (comments.size() == i) comments.add(null);
      if (dates.size() == i) dates.add(null);
      if (wavelengths.size() == i) wavelengths.add(null);
    }
    setSeries(0);

    reader = new MinimalTiffReader();

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    for (int i=0; i<scans.size(); i++) {
      core[i].imageCount = tiffs[i].size();
      core[i].sizeC = tiffs[i].size();
      core[i].sizeT = 1;
      core[i].sizeZ = 1;
      core[i].dimensionOrder = "XYCZT";

      for (int t=0; t<tiffs[i].size(); t++) {
        reader.setId((String) tiffs[i].get(t));
        if (t == 0) {
          core[i].sizeX = reader.getSizeX();
          core[i].sizeY = reader.getSizeY();
          core[i].sizeC *= reader.getSizeC();
          core[i].rgb = reader.isRGB();
          core[i].indexed = reader.isIndexed();
          core[i].littleEndian = reader.isLittleEndian();
          core[i].pixelType = reader.getPixelType();
        }
      }
    }

    MetadataTools.populatePixels(store, this);

    store.setInstrumentID("Instrument:0", 0);

    for (int i=0; i<scans.size(); i++) {
      store.setImageInstrumentRef("Instrument:0", i);

      store.setImageName((String) scans.get(i), i);
      store.setImageDescription((String) comments.get(i), i);

      String date = (String) dates.get(i);
      if (date != null) {
        date = DateTools.formatDate(date, DATE_FORMAT);
        store.setImageCreationDate(date, i);
      }
      else MetadataTools.setDefaultCreationDate(store, id, i);

      String c = (String) wavelengths.get(i);
      if (c != null) {
        String[] waves = c.split("[, ]");
        if (waves.length < getEffectiveSizeC()) {
          debug("Expected " + getEffectiveSizeC() + " wavelengths; got " +
            waves.length + " wavelengths.");
        }
        for (int q=0; q<waves.length; q++) {
          store.setLightSourceID("LightSource:" + q, 0, q);
          store.setLaserWavelength(new Integer(waves[q].trim()), 0, q);
          store.setLogicalChannelLightSource("LightSource:" + q, i, q);
        }
      }
    }

    store.setMicroscopeModel(model, 0);
  }

  // -- Helper methods --

  /**
   * Recursively add all of the files in the given directory to the
   * used file list.
   */
  private void addDirectory(String path) { Location dir = new Location(path);
    String[] files = dir.list();
    for (int i=0; i<files.length; i++) {
      Location file = new Location(path, files[i]);
      if (file.isDirectory()) {
        addDirectory(file.getAbsolutePath());
      }
      else {
        String check = files[i].toLowerCase();
        if (check.endsWith(".tif") || check.endsWith(".data") ||
          check.endsWith(".log") || check.endsWith(".scn"))
        {
          used.add(file.getAbsolutePath());
        }
      }
    }
  }

}
