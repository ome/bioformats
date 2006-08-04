//
// GelReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

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
 * @author Melissa Linkert linkert at cs.wisc.edu
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


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a GEL TIFF file. */
  public boolean isThisType(byte[] block) { return false; }

  /**
   * Allows the client to specify whether or not to separate channels.
   * By default, channels are left unseparated; thus if we encounter an RGB
   * image plane, it will be left as RGB and not split into 3 separate planes.
   */
  public void setSeparated(boolean separate) {
    separated = separate;
    super.setSeparated(separate);
  }

  /** Get the size of the T dimension. */
  public int getSizeT(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numImages;
  }

  // -- Internal BaseTiffReader API methods --

  /* (non-Javadoc)
   * @see loci.formats.BaseTiffReader#initStandardMetadata()
   */
  protected void initStandardMetadata() {
    super.initStandardMetadata();

    numImages--;

    try {
      long fmt = TiffTools.getIFDLongValue(ifds[1], MD_FILETAG, true, 128);
      metadata.put("Data format", fmt == 2 ? "square root" : "linear");
    }
    catch (FormatException f) {
      f.printStackTrace();
    }

    TiffRational scale =
      (TiffRational) TiffTools.getIFDValue(ifds[1], MD_SCALE_PIXEL);
    metadata.put("Scale factor",
      (scale == null) ? new TiffRational(1, 1) : scale);

    // ignore MD_COLOR_TABLE

    String lab = (String) TiffTools.getIFDValue(ifds[1], MD_LAB_NAME);
    metadata.put("Lab name", lab == null ? "unknown" : lab);

    String info = (String) TiffTools.getIFDValue(ifds[1], MD_SAMPLE_INFO);
    metadata.put("Sample info", info == null ? "unknown" : info);

    String prepDate = (String) TiffTools.getIFDValue(ifds[1], MD_PREP_DATE);
    metadata.put("Date prepared", prepDate == null ? "unknown" : prepDate);

    String prepTime = (String) TiffTools.getIFDValue(ifds[1], MD_PREP_TIME);
    metadata.put("Time prepared", prepTime == null ? "unknown" : prepTime);

    String units = (String) TiffTools.getIFDValue(ifds[1], MD_FILE_UNITS);
    metadata.put("File units", units == null ? "unknown" : units);
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new GelReader().testRead(args);
  }

}
