//
// UnisokuReader.java
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

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;

/**
 * UnisokuReader is the file format reader for Unisoku STM files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/UnisokuReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/UnisokuReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class UnisokuReader extends FormatReader {

  // -- Constants --

  protected static final String UNISOKU_MAGIC_STRING = ":STM data";

  // -- Fields --

  private String datFile;

  // -- Constructor --

  /** Constructs a new Unisoku reader. */
  public UnisokuReader() {
    super("Unisoku STM", new String[] {"hdr", "dat"});
    domains = new String[] {FormatTools.SPM_DOMAIN};
    suffixSufficient = false;
    hasCompanionFiles = true;
    datasetDescription = "One .HDR file plus one similarly-named .DAT file";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, "hdr")) {
      return super.isThisType(name, open);
    }

    if (name.indexOf(".") < 0) {
      return false;
    }

    String baseName = name.substring(0, name.lastIndexOf("."));

    Location file = new Location(baseName + ".HDR");
    if (!file.exists()) {
      file = new Location(baseName + ".hdr");
    }

    if (!file.exists()) {
      return false;
    }
    return super.isThisType(file.getAbsolutePath(), open);
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 9;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return (stream.readString(blockLen)).indexOf(UNISOKU_MAGIC_STRING) >= 0;
  }

  /* @see loci.formats.IFormatReader#getUsedFiles(boolean) */
  public String[] getUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (noPixels) return new String[] {currentId};
    return new String[] {currentId, datFile};
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    RandomAccessInputStream dat = new RandomAccessInputStream(datFile);
    dat.order(isLittleEndian());
    readPlane(dat, x, y, w, h, buf);
    dat.close();

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      datFile = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    id = new Location(id).getAbsolutePath();

    if (checkSuffix(id, "dat")) {
      id = id.substring(0, id.lastIndexOf(".")) + ".HDR";
    }

    super.initFile(id);

    datFile = id.substring(0, id.lastIndexOf(".")) + ".DAT";

    String header = DataTools.readFile(id);
    String[] lines = header.split("\r");

    String imageName = null, remark = null, date = null;
    double pixelSizeX = 0d, pixelSizeY = 0d;

    for (int i=0; i<lines.length; ) {
      lines[i] = lines[i].trim();
      if (lines[i].startsWith(":")) {
        String key = lines[i++];
        StringBuffer data = new StringBuffer();
        while (i < lines.length && !lines[i].trim().startsWith(":")) {
          data.append(" ");
          data.append(lines[i++].trim());
        }
        String value = data.toString().trim();
        addGlobalMeta(key, value);
        String[] v = value.split(" ");

        if (key.equals(":data volume(x*y)")) {
          core[0].sizeX = Integer.parseInt(v[0]);
          core[0].sizeY = Integer.parseInt(v[1]);
        }
        else if (key.equals(":date; time")) {
          date = DateTools.formatDate(value, "MM/dd/yy HH:mm:ss");
        }
        else if (key.startsWith(":ascii flag; data type")) {
          value = value.substring(value.indexOf(" ") + 1);
          int type = Integer.parseInt(value);
          boolean signed = type % 2 == 1;
          int bytes = type / 2;
          core[0].pixelType =
            FormatTools.pixelTypeFromBytes(bytes, signed, bytes == 4);
        }
        else if (getMetadataOptions().getMetadataLevel() !=
          MetadataLevel.MINIMUM)
        {
          if (key.equals(":sample name")) {
            imageName = value;
          }
          else if (key.equals(":remark")) {
            remark = value;
          }
          else if (key.startsWith(":x_data ->")) {
            String unit = v[0];
            pixelSizeX = Double.parseDouble(v[2]) - Double.parseDouble(v[1]);
            pixelSizeX /= getSizeX();
            if (unit.equals("nm")) {
              pixelSizeX /= 1000;
            }
          }
          else if (key.startsWith(":y_data ->")) {
            String unit = v[0];
            pixelSizeY = Double.parseDouble(v[2]) - Double.parseDouble(v[1]);
            pixelSizeY /= getSizeY();
            if (unit.equals("nm")) {
              pixelSizeY /= 1000;
            }
          }
        }
      }
    }

    core[0].sizeZ = 1;
    core[0].sizeC = 1;
    core[0].sizeT = 1;
    core[0].imageCount = 1;
    core[0].rgb = false;
    core[0].interleaved = false;
    core[0].indexed = false;
    core[0].dimensionOrder = "XYZCT";
    core[0].littleEndian = true;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    store.setImageName(imageName, 0);
    store.setImageAcquiredDate(date, 0);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      store.setImageDescription(remark, 0);
      if (pixelSizeX > 0) {
        store.setPixelsPhysicalSizeX(new PositiveFloat(pixelSizeX), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeX; got {}",
          pixelSizeX);
      }
      if (pixelSizeY > 0) {
        store.setPixelsPhysicalSizeY(new PositiveFloat(pixelSizeY), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeY; got {}",
          pixelSizeY);
      }
    }
  }

}
