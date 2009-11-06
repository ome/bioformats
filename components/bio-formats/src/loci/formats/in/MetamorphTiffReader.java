//
// MetamorphTiffReader.java
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
import java.util.Vector;

import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.common.XMLTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.TiffParser;

/**
 * MetamorphTiffReader is the file format reader for TIFF files produced by
 * Metamorph software version 7.5 and above.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/MetamorphTiffReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/MetamorphTiffReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 * @author Thomas Caswell tcaswell at uchicago.edu
 */
public class MetamorphTiffReader extends BaseTiffReader {

  // -- Constructor --

  /** Constructs a new Metamorph TIFF reader. */
  public MetamorphTiffReader() {
    super("Metamorph TIFF", new String[] {"tif", "tiff"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser tp = new TiffParser(stream);
    String comment = tp.getComment();
    if (comment == null) return false;
    comment = comment.trim();
    return comment.startsWith("<MetaData>") && comment.endsWith("</MetaData>");
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("MetamorphTiffReader.initFile(" + id + ")");
    super.initFile(id);

    String[] comments = new String[ifds.size()];

    // parse XML comment

    MetamorphHandler handler = new MetamorphHandler(getGlobalMetadata());
    for (int i=0; i<comments.length; i++) {
      comments[i] = ifds.get(i).getComment();
      XMLTools.parseXML(comments[i], handler);
    }

    core[0].sizeC = 0;

    Vector<String> timestamps = handler.getTimestamps();
    Vector<Integer> wavelengths = handler.getWavelengths();
    Vector<Double> zPositions = handler.getZPositions();
    Vector<Double> exposures = handler.getExposures();

    // calculate axis sizes

    Vector<Integer> uniqueC = new Vector<Integer>();
    for (int i=0; i<wavelengths.size(); i++) {
      Integer c = wavelengths.get(i);
      if (!uniqueC.contains(c)) {
        uniqueC.add(c);
      }
    }
    core[0].sizeC = uniqueC.size();

    Vector<Double> uniqueZ = new Vector<Double>();
    for (int i=0; i<zPositions.size(); i++) {
      Double z = zPositions.get(i);
      if (!uniqueZ.contains(z)) uniqueZ.add(z);
    }
    core[0].sizeZ = uniqueZ.size();
    core[0].sizeT = ifds.size() / (getSizeZ() * getSizeC());

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this, true);
    store.setImageName(handler.getImageName(), 0);
    store.setImageDescription("", 0);

    String parse = "yyyyMMdd HH:mm:ss.SSS";

    for (int i=0; i<timestamps.size(); i++) {
      long timestamp = DateTools.getTime(timestamps.get(i), parse);
      addGlobalMeta("timestamp " + i, timestamp);
    }
    for (int i=0; i<exposures.size(); i++) {
      addGlobalMeta("exposure time " + i + " (ms)",
        exposures.get(i).floatValue() * 1000);
    }

    long startDate = 0;
    if (timestamps.size() > 0) {
      startDate = DateTools.getTime(timestamps.get(0), parse);
    }

    for (int i=0; i<getImageCount(); i++) {
      int[] coords = getZCTCoords(i);
      if (coords[2] < timestamps.size()) {
        String stamp = timestamps.get(coords[2]);
        long ms = DateTools.getTime(stamp, parse);
        store.setPlaneTimingDeltaT(new Double((ms - startDate) / 1000.0),
          0, 0, i);
      }
      if (i < exposures.size()) {
        store.setPlaneTimingExposureTime(exposures.get(i), 0, 0, i);
      }
    }

    String date =
      DateTools.formatDate(handler.getDate(), DateTools.ISO8601_FORMAT);
    store.setImageCreationDate(date, 0);

    store.setImagingEnvironmentTemperature(
      new Double(handler.getTemperature()), 0);
    store.setDimensionsPhysicalSizeX(new Double(handler.getPixelSizeX()), 0, 0);
    store.setDimensionsPhysicalSizeY(new Double(handler.getPixelSizeY()), 0, 0);
  }
}
