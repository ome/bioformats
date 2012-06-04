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

import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.Timestamp;

/**
 * LEOReader is the file format reader for LEO EM files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/LEOReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/LEOReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
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
    super("LEO", new String[] {"sxm", "tif", "tiff"});
    domains = new String[] {FormatTools.EM_DOMAIN};
    suffixSufficient = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser parser = new TiffParser(stream);
    parser.setDoCaching(false);
    IFD ifd = parser.getFirstIFD();
    if (ifd == null) return false;
    return ifd.containsKey(LEO_TAG);
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    String tag = ifds.get(0).getIFDTextValue(LEO_TAG);
    String[] lines = tag.split("\n");

    date = "";

    for (int line=10; line<lines.length; line++) {
      if (lines[line].equals("clock")) {
        date += lines[++line];
      }
      else if (lines[line].equals("date")) {
        date += " " + lines[++line];
      }
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      // physical sizes stored in meters
      xSize = Double.parseDouble(lines[3]) * 1000000;

      double eht = Double.parseDouble(lines[6]);
      double filament = Double.parseDouble(lines[7]);
      workingDistance = Double.parseDouble(lines[9]);
      addGlobalMeta("EHT", eht);
      addGlobalMeta("Filament", filament);
      addGlobalMeta("Working Distance", workingDistance);
      addGlobalMeta("Physical pixel size", xSize + " um");
      addGlobalMeta("Acquisition date", date);
    }
  }

  /* @see BaseTiffReader#initMetadataStore() */
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();

    MetadataStore store = makeFilterMetadata();

    date = DateTools.formatDate(date, "HH:mm dd-MMM-yyyy");
    store.setImageAcquisitionDate(new Timestamp(date), 0);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      if (xSize > 0) {
        store.setPixelsPhysicalSizeX(new PositiveFloat(xSize), 0);
        store.setPixelsPhysicalSizeY(new PositiveFloat(xSize), 0);
      }
      else {
        LOGGER.warn("Expected positive value for Physicalsize; got {}", xSize);
      }

      String instrument = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrument, 0);
      store.setImageInstrumentRef(instrument, 0);

      store.setObjectiveID(MetadataTools.createLSID("Objective", 0, 0), 0, 0);
      store.setObjectiveWorkingDistance(workingDistance, 0, 0);
      store.setObjectiveImmersion(getImmersion("Other"), 0, 0);
      store.setObjectiveCorrection(getCorrection("Other"), 0, 0);
    }
  }

}
