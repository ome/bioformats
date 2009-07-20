//
// GelReader.java
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
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.TiffRational;

/**
 * GelReader is the file format reader for
 * Molecular Dynamics GEL TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/GelReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/GelReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class GelReader extends BaseTiffReader {

  // -- Constants --

  public static final String DATE_FORMAT = "yyyy:MM:dd HH:mm:ss";

  // GEL TIFF private IFD tags.
  private static final int MD_FILETAG = 33445;
  private static final int MD_SCALE_PIXEL = 33446;
  private static final int MD_LAB_NAME = 33448;
  private static final int MD_SAMPLE_INFO = 33449;
  private static final int MD_PREP_DATE = 33450;
  private static final int MD_PREP_TIME = 33451;
  private static final int MD_FILE_UNITS = 33452;

  // Scaling options
  private static final int SQUARE_ROOT = 2;
  private static final int LINEAR = 128;

  // -- Constructor --

  /** Constructs a new GEL reader. */
  public GelReader() {
    super("Amersham Biosciences GEL", new String[] {"gel"});
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    IFD ifd = ifds.get(no);

    boolean sqrt = ifd.getIFDLongValue(MD_FILETAG, true, LINEAR) == SQUARE_ROOT;

    if (sqrt) {
      float scale = ((TiffRational)
        ifd.getIFDValue(MD_SCALE_PIXEL)).floatValue();

      byte[] tmp = new byte[buf.length];
      super.openBytes(no, tmp, x, y, w, h);

      int originalBytes = ifd.getBitsPerSample()[0] / 8;

      for (int i=0; i<tmp.length/4; i++) {
        long value = DataTools.bytesToShort(tmp, i*originalBytes,
          originalBytes, isLittleEndian());
        long square = value * value;
        float pixel = square * scale;
        DataTools.unpackBytes(Float.floatToIntBits(pixel), buf, i*4, 4,
          isLittleEndian());
      }
    }
    else super.openBytes(no, buf, x, y, w, h);

    return buf;
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initMetadata() */
  protected void initMetadata() throws FormatException, IOException {
    ifds = tiffParser.getIFDs();
    if (ifds.size() > 1) {
      IFDList tmpIFDs = ifds;
      ifds = new IFDList();
      ifds.ensureCapacity(tmpIFDs.size() / 2);
      for (int i=0; i<ifds.size(); i++) {
        IFD ifd = new IFD();
        ifds.set(i, ifd);
        ifd.putAll(tmpIFDs.get(i*2 + 1));
        ifd.putAll(tmpIFDs.get(i*2));
      }
    }

    super.initStandardMetadata();

    IFD firstIFD = ifds.get(0);

    long fmt = firstIFD.getIFDLongValue(MD_FILETAG, true, LINEAR);
    if (fmt == SQUARE_ROOT) core[0].pixelType = FormatTools.FLOAT;
    addGlobalMeta("Data format", fmt == SQUARE_ROOT ? "square root" : "linear");

    TiffRational scale =
      (TiffRational) firstIFD.getIFDValue(MD_SCALE_PIXEL);
    addGlobalMeta("Scale factor", scale == null ?
      new TiffRational(1, 1) : scale);

    // ignore MD_COLOR_TABLE

    String lab = firstIFD.getIFDStringValue(MD_LAB_NAME, false);
    addGlobalMeta("Lab name", lab);

    String info = firstIFD.getIFDStringValue(MD_SAMPLE_INFO, false);
    addGlobalMeta("Sample info", info);

    String prepDate = firstIFD.getIFDStringValue(MD_PREP_DATE, false);
    addGlobalMeta("Date prepared", prepDate);

    String prepTime = firstIFD.getIFDStringValue(MD_PREP_TIME, false);
    addGlobalMeta("Time prepared", prepTime);

    String units = firstIFD.getIFDStringValue(MD_FILE_UNITS, false);
    addGlobalMeta("File units", units);

    core[0].imageCount = ifds.size();
    core[0].sizeT = getImageCount();

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);
    store.setImageDescription(info, 0);

    if (prepTime != null) {
      SimpleDateFormat parse = new SimpleDateFormat(DATE_FORMAT);
      Date date = parse.parse(prepTime, new ParsePosition(0));
      SimpleDateFormat sdf = new SimpleDateFormat(DateTools.ISO8601_FORMAT);
      store.setImageCreationDate(sdf.format(date), 0);
    }
    else {
      MetadataTools.setDefaultCreationDate(store, getCurrentFile(), 0);
    }

    Float pixelSize = new Float(scale.floatValue());
    store.setDimensionsPhysicalSizeX(pixelSize, 0, 0);
    store.setDimensionsPhysicalSizeY(pixelSize, 0, 0);
  }

}
