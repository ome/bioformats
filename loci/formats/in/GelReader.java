//
// GelReader.java
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
import java.text.*;
import java.util.Date;
import java.util.Hashtable;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * GelReader is the file format reader for
 * Molecular Dynamics GEL TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/GelReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/GelReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class GelReader extends BaseTiffReader {

  // -- Constants --

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
    super("Molecular Dynamics GEL TIFF", new String[] {"gel"});
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    boolean sqrt = TiffTools.getIFDLongValue(ifds[no], MD_FILETAG,
      true, LINEAR) == SQUARE_ROOT;

    if (sqrt) {
      float scale = ((TiffRational) TiffTools.getIFDValue(ifds[no],
        MD_SCALE_PIXEL)).floatValue();

      byte[] tmp = new byte[buf.length];
      super.openBytes(no, tmp, x, y, w, h);

      int originalBytes = TiffTools.getBitsPerSample(ifds[no])[0] / 8;

      for (int i=0; i<tmp.length/4; i++) {
        long value = DataTools.bytesToShort(tmp, i*originalBytes,
          originalBytes, core.littleEndian[0]);
        long square = value * value;
        float pixel = square * scale;
        DataTools.unpackBytes(Float.floatToIntBits(pixel), buf, i*4, 4,
          core.littleEndian[0]);
      }
    }
    else super.openBytes(no, buf, x, y, w, h);

    return buf;
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initMetadata() */
  protected void initMetadata() throws FormatException, IOException {
    if (ifds.length > 1) {
      Hashtable[] tmpIFDs = ifds;
      ifds = new Hashtable[tmpIFDs.length / 2];
      for (int i=0; i<ifds.length; i++) {
        ifds[i] = new Hashtable();
        ifds[i].putAll(tmpIFDs[i*2 + 1]);
        ifds[i].putAll(tmpIFDs[i*2]);
      }
    }

    super.initMetadata();

    long format = TiffTools.getIFDLongValue(ifds[0], MD_FILETAG, true, LINEAR);
    if (format == SQUARE_ROOT) core.pixelType[0] = FormatTools.FLOAT;
    addMeta("Data format", format == SQUARE_ROOT ? "square root" : "linear");

    TiffRational scale =
      (TiffRational) TiffTools.getIFDValue(ifds[0], MD_SCALE_PIXEL);
    addMeta("Scale factor", scale == null ? new TiffRational(1, 1) : scale);

    // ignore MD_COLOR_TABLE

    String lab = (String) TiffTools.getIFDValue(ifds[0], MD_LAB_NAME);
    addMeta("Lab name", lab);

    String info = (String) TiffTools.getIFDValue(ifds[0], MD_SAMPLE_INFO);
    addMeta("Sample info", info);

    String prepDate = (String) TiffTools.getIFDValue(ifds[0], MD_PREP_DATE);
    addMeta("Date prepared", prepDate);

    String prepTime = (String) TiffTools.getIFDValue(ifds[0], MD_PREP_TIME);
    addMeta("Time prepared", prepTime);

    String units = (String) TiffTools.getIFDValue(ifds[0], MD_FILE_UNITS);
    addMeta("File units", units);

    core.imageCount[series] = ifds.length;
    core.sizeT[series] = core.imageCount[series];

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    store.setImageName("", 0);
    store.setImageDescription(info, 0);

    if (prepTime != null) {
      SimpleDateFormat parse = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
      Date date = parse.parse(prepTime, new ParsePosition(0));
      SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      store.setImageCreationDate(fmt.format(date), 0);
    }
    else {
      store.setImageCreationDate(
        DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), 0);
    }

    MetadataTools.populatePixels(store, this);
    Float pixelSize = new Float(scale.floatValue());
    store.setDimensionsPhysicalSizeX(pixelSize, 0, 0);
    store.setDimensionsPhysicalSizeY(pixelSize, 0, 0);
  }

}
