//
// KodakReader.java
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

import loci.common.Constants;
import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;

/**
 * KodakReader is the file format reader for Kodak Molecular Imaging .bip files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/KodakReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/KodakReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class KodakReader extends FormatReader {

  // -- Constants --

  private static final String MAGIC_STRING = "DTag";
  private static final String PIXELS_STRING = "BSfD";

  private static final String DATE_FORMAT = "HH:mm:ss 'on' MM/dd/yyyy";

  // -- Fields --

  private long pixelOffset;

  // -- Constructor --

  /** Constructs a new Kodak reader. */
  public KodakReader() {
    super("Kodak Molecular Imaging", "bip");
    domains = new String[] {FormatTools.GEL_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 16;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return (stream.readString(blockLen)).indexOf(MAGIC_STRING) >= 0;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    in.seek(pixelOffset);
    readPlane(in, x, y, w, h, buf);

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      pixelOffset = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    core[0].littleEndian = false;

    findString(PIXELS_STRING);
    pixelOffset = in.getFilePointer() + PIXELS_STRING.length() + 20;

    core[0].sizeX = 1024;
    core[0].sizeY = 1024;
    core[0].sizeZ = 1;
    core[0].sizeC = 1;
    core[0].sizeT = 1;
    core[0].imageCount = 1;
    core[0].dimensionOrder = "XYCZT";
    core[0].pixelType = FormatTools.FLOAT;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    readExtraMetadata(store);
  }

  // -- Helper methods --

  private void findString(String marker) throws IOException {
    byte[] buf = new byte[8192];
    int overlap = marker.length();

    in.read(buf, 0, overlap);

    while (in.getFilePointer() < in.length()) {
      in.read(buf, overlap, buf.length - overlap);

      for (int i=0; i<buf.length-overlap; i++) {
        if (marker.equals(
          new String(buf, i, marker.length(), Constants.ENCODING)))
        {
          in.seek(in.getFilePointer() - buf.length + i);
          return;
        }
      }

      System.arraycopy(buf, buf.length - overlap, buf, 0, overlap);
    }
  }

  private void readExtraMetadata(MetadataStore store) throws IOException {
    if (getMetadataOptions().getMetadataLevel() == MetadataLevel.MINIMUM) {
      return;
    }

    in.seek(0);

    findString("Image Capture Source");
    String metadata = in.readCString();

    String[] lines = metadata.split("\n");

    for (String line : lines) {
      int index = line.indexOf(":");
      if (index < 0 || line.startsWith("#") || line.startsWith("-")) {
        continue;
      }

      String key = line.substring(0, index).trim();
      String value = line.substring(index + 1).trim();

      addGlobalMeta(key, value);

      if (key.equals("Image Capture Source")) {
        String instrument = MetadataTools.createLSID("Instrument", 0);
        store.setInstrumentID(instrument, 0);
        store.setImageInstrumentRef(instrument, 0);
        store.setMicroscopeModel(value, 0);
      }
      else if (key.equals("Capture Time/Date")) {
        store.setImageAcquiredDate(DateTools.formatDate(value, DATE_FORMAT), 0);
      }
      else if (key.equals("Exposure Time")) {
        Double exposure = new Double(value.substring(0, value.indexOf(" ")));
        store.setPlaneExposureTime(exposure, 0, 0);
      }
      else if (key.equals("Vertical Resolution")) {
        // resolution stored in pixels per inch
        value = value.substring(0, value.indexOf(" "));
        Double size = new Double(value);
        if (size > 0) {
          size = 1.0 / (size * (1.0 / 25400));
          store.setPixelsPhysicalSizeY(new PositiveFloat(size), 0);
        }
        else {
          LOGGER.warn("Expected positive value for PhysicalSizeY; got {}",
            size);
        }
      }
      else if (key.equals("Horizontal Resolution")) {
        // resolution stored in pixels per inch
        value = value.substring(0, value.indexOf(" "));
        Double size = new Double(value);
        if (size > 0) {
          size = 1.0 / (size * (1.0 / 25400));
          store.setPixelsPhysicalSizeX(new PositiveFloat(size), 0);
        }
        else {
          LOGGER.warn("Expected positive value for PhysicalSizeX; got {}",
            size);
        }
      }
      else if (key.equals("CCD Temperature")) {
        Double temp = new Double(value.substring(0, value.indexOf(" ")));
        store.setImagingEnvironmentTemperature(temp, 0);
      }
    }
  }

}
