//
// LEOReader.java
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

import loci.common.DateTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;

/**
 * LEOReader is the file format reader for LEO EM files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/LEOReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/LEOReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class LEOReader extends BaseTiffReader {

  // -- Constants --

  public static final int LEO_TAG = 34118;

  // -- Fields --

  private double xSize;
  private String date;
  private double workingDistance;

  // -- Constructor --

  /** Constructs a new LEO reader. */
  public LEOReader() {
    super("LEO", "sxm");
    domains = new String[] {FormatTools.EM_DOMAIN};
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    String tag = ifds.get(0).getIFDTextValue(LEO_TAG);
    String[] lines = tag.split("\n");

    // physical sizes stored in meters
    xSize = Double.parseDouble(lines[3]) * 1000000;

    double eht = Double.parseDouble(lines[6]);
    double filament = Double.parseDouble(lines[7]);
    workingDistance = Double.parseDouble(lines[9]);

    date = "";

    for (int line=10; line<lines.length; line++) {
      if (lines[line].equals("clock")) {
        date += lines[++line];
      }
      else if (lines[line].equals("date")) {
        date += " " + lines[++line];
      }
    }

    addGlobalMeta("Acquisition date", date);
    addGlobalMeta("EHT", eht);
    addGlobalMeta("Filament", filament);
    addGlobalMeta("Working Distance", workingDistance);
    addGlobalMeta("Physical pixel size", xSize + " um");
  }

  /* @see BaseTiffReader#initMetadataStore() */
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    store.setDimensionsPhysicalSizeX(new Double(xSize), 0, 0);
    store.setDimensionsPhysicalSizeY(new Double(xSize), 0, 0);

    store.setObjectiveWorkingDistance(new Double(workingDistance), 0, 0);

    date = DateTools.formatDate(date, "HH:mm dd-MMM-yyyy");
    store.setImageCreationDate(date, 0);
  }

}
