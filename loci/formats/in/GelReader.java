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
import loci.formats.*;

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

  // -- Constructor --

  /** Constructs a new GEL reader. */
  public GelReader() {
    super("Molecular Dynamics GEL TIFF", new String[] {"gel"});
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) { return false; }

  // -- Internal BaseTiffReader API methods --

  /* @see loci.formats.BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    core.imageCount[0]--;

    try {
      long fmt = TiffTools.getIFDLongValue(ifds[1], MD_FILETAG, true, 128);
      addMeta("Data format", fmt == 2 ? "square root" : "linear");
    }
    catch (FormatException exc) {
      trace(exc);
    }

    TiffRational scale =
      (TiffRational) TiffTools.getIFDValue(ifds[1], MD_SCALE_PIXEL);
    addMeta("Scale factor", scale == null ? new TiffRational(1, 1) : scale);

    // ignore MD_COLOR_TABLE

    String lab = (String) TiffTools.getIFDValue(ifds[1], MD_LAB_NAME);
    addMeta("Lab name", lab == null ? "unknown" : lab);

    String info = (String) TiffTools.getIFDValue(ifds[1], MD_SAMPLE_INFO);
    addMeta("Sample info", info == null ? "unknown" : info);

    String prepDate = (String) TiffTools.getIFDValue(ifds[1], MD_PREP_DATE);
    addMeta("Date prepared", prepDate == null ? "unknown" : prepDate);

    String prepTime = (String) TiffTools.getIFDValue(ifds[1], MD_PREP_TIME);
    addMeta("Time prepared", prepTime == null ? "unknown" : prepTime);

    String units = (String) TiffTools.getIFDValue(ifds[1], MD_FILE_UNITS);
    addMeta("File units", units == null ? "unknown" : units);

    core.sizeT[series] = core.imageCount[series];

    MetadataStore store = getMetadataStore();
    store.setDimensions(new Float(scale.floatValue()),
      new Float(scale.floatValue()), null, null, null, null);
  }

}
