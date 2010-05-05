//
// NRRDReader.java
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

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

/**
 * File format reader for NRRD files; see http://teem.sourceforge.net/nrrd.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/NRRDReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/NRRDReader.java">SVN</a></dd></dl>
 */
public class NRRDReader extends FormatReader {

  // -- Constants --

  public static final String NRRD_MAGIC_STRING = "NRRD";

  // -- Fields --

  /** Helper reader. */
  private ImageReader helper;

  /** Name of data file, if the current extension is 'nhdr'. */
  private String dataFile;

  /** Data encoding. */
  private String encoding;

  /** Offset to pixel data. */
  private long offset;

  private String[] pixelSizes;

  private boolean lookForCompanion = true;

  // -- Constructor --

  /** Constructs a new NRRD reader. */
  public NRRDReader() {
    super("NRRD", new String[] {"nrrd", "nhdr"});
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return checkSuffix(id, "nrrd");
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (super.isThisType(name, open)) return true;

    // look for a matching .nhdr file
    return new Location(name + ".nhdr").exists();
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = NRRD_MAGIC_STRING.length();
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(blockLen).startsWith(NRRD_MAGIC_STRING);
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (noPixels) {
      if (dataFile == null) return null;
      return new String[] {currentId};
    }
    if (dataFile == null) return new String[] {currentId};
    return new String[] {currentId, dataFile};
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    // TODO : add support for additional encoding types
    if (dataFile == null) {
      if (encoding.equals("raw")) {
        in.seek(offset + no * FormatTools.getPlaneSize(this));

        readPlane(in, x, y, w, h, buf);
        return buf;
      }
      else throw new FormatException("Unsupported encoding: " + encoding);
    }
    return helper.openBytes(no, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (helper != null) helper.close(fileOnly);
    if (!fileOnly) {
      helper = null;
      dataFile = encoding = null;
      offset = 0;
      pixelSizes = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    // make sure we actually have the .nrrd/.nhdr file
    if (!checkSuffix(id, "nhdr") && !checkSuffix(id, "nrrd")) {
      id += ".nhdr";
    }

    in = new RandomAccessInputStream(id);
    helper = new ImageReader();

    String key, v;

    int numDimensions = 0;

    core[0].sizeX = 1;
    core[0].sizeY = 1;
    core[0].sizeZ = 1;
    core[0].sizeC = 1;
    core[0].sizeT = 1;
    core[0].dimensionOrder = "XYCZT";

    String line = in.readLine();
    while (line != null && line.length() > 0) {
      if (!line.startsWith("#") && !line.startsWith("NRRD")) {
        // parse key/value pair
        key = line.substring(0, line.indexOf(":")).trim();
        v = line.substring(line.indexOf(":") + 1).trim();
        addGlobalMeta(key, v);

        if (key.equals("type")) {
          if (v.indexOf("char") != -1 || v.indexOf("8") != -1) {
            core[0].pixelType = FormatTools.UINT8;
          }
          else if (v.indexOf("short") != -1 || v.indexOf("16") != -1) {
            core[0].pixelType = FormatTools.UINT16;
          }
          else if (v.equals("int") || v.equals("signed int") ||
            v.equals("int32") || v.equals("int32_t") || v.equals("uint") ||
            v.equals("unsigned int") || v.equals("uint32") ||
            v.equals("uint32_t"))
          {
            core[0].pixelType = FormatTools.UINT32;
          }
          else if (v.equals("float")) core[0].pixelType = FormatTools.FLOAT;
          else if (v.equals("double")) core[0].pixelType = FormatTools.DOUBLE;
          else throw new FormatException("Unsupported data type: " + v);
        }
        else if (key.equals("dimension")) {
          numDimensions = Integer.parseInt(v);
        }
        else if (key.equals("sizes")) {
          StringTokenizer tokens = new StringTokenizer(v, " ");
          for (int i=0; i<numDimensions; i++) {
            String t = tokens.nextToken();
            int size = Integer.parseInt(t);

            if (numDimensions >= 3 && i == 0 && size > 1 && size <= 4) {
              core[0].sizeC = size;
            }
            else if (i == 0 || (getSizeC() > 1 && i == 1)) {
              core[0].sizeX = size;
            }
            else if (i == 1 || (getSizeC() > 1 && i == 2)) {
              core[0].sizeY = size;
            }
            else if (i == 2 || (getSizeC() > 1 && i == 3)) {
              core[0].sizeZ = size;
            }
            else if (i == 3 || (getSizeC() > 1 && i == 4)) {
              core[0].sizeT = size;
            }
          }
        }
        else if (key.equals("data file") || key.equals("datafile")) {
          dataFile = v;
        }
        else if (key.equals("encoding")) encoding = v;
        else if (key.equals("endian")) {
          core[0].littleEndian = v.equals("little");
        }
        else if (key.equals("spacings")) {
          pixelSizes = v.split(" ");
        }
      }

      line = in.readLine();
      if (line != null) line = line.trim();
    }

    // nrrd files store pixel data in addition to metadata
    // nhdr files don't store pixel data, but instead provide a path to the
    //   pixels file (this can be any format)

    if (dataFile == null) offset = in.getFilePointer();
    else {
      offset = 0;
      Location f = new Location(currentId).getAbsoluteFile();
      Location parent = f.getParentFile();
      if (f.exists() && parent != null) {
        dataFile = new Location(parent, dataFile).getAbsolutePath();
      }
      // calling setId on dataFile will cause this reader to pick up
      // the data file
      String name = dataFile.substring(dataFile.lastIndexOf(File.separator));
      Location.mapId(name, dataFile);
      helper.setId(name);
    }

    core[0].rgb = getSizeC() > 1;
    core[0].interleaved = true;
    core[0].imageCount = getSizeZ() * getSizeT();
    core[0].indexed = false;
    core[0].falseColor = false;
    core[0].metadataComplete = true;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
    MetadataTools.setDefaultCreationDate(store, id, 0);

    if (getMetadataOptions().getMetadataLevel() == MetadataLevel.ALL) {
      for (int i=0; i<pixelSizes.length; i++) {
        Double d = new Double(pixelSizes[i].trim());
        if (i == 0) store.setPixelsPhysicalSizeX(d, 0);
        else if (i == 1) store.setPixelsPhysicalSizeY(d, 0);
        else if (i == 2) store.setPixelsPhysicalSizeZ(d, 0);
      }
    }
  }

}
