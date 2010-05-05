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
import loci.common.xml.XMLTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
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

  // -- Constants --

  private static final String DATE_FORMAT = "yyyyMMdd HH:mm:ss.SSS";

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
    super.initFile(id);

    // parse XML comment

    MetamorphHandler handler = new MetamorphHandler(getGlobalMetadata());
    for (IFD ifd : ifds) {
      XMLTools.parseXML(ifd.getComment(), handler);
    }

    core[0].sizeC = 0;

    Vector<String> timestamps = handler.getTimestamps();
    Vector<Integer> wavelengths = handler.getWavelengths();
    Vector<Double> zPositions = handler.getZPositions();
    Vector<Double> exposures = handler.getExposures();

    // calculate axis sizes

    Vector<Integer> uniqueC = new Vector<Integer>();
    for (Integer c : wavelengths) {
      if (!uniqueC.contains(c)) {
        uniqueC.add(c);
      }
    }
    int effectiveC = uniqueC.size();
    if (effectiveC == 0) effectiveC = 1;
    core[0].sizeC = effectiveC * ifds.get(0).getSamplesPerPixel();

    Vector<Double> uniqueZ = new Vector<Double>();
    for (Double z : zPositions) {
      if (!uniqueZ.contains(z)) uniqueZ.add(z);
    }
    core[0].sizeZ = uniqueZ.size();
    core[0].sizeT = ifds.size() / (getSizeZ() * effectiveC);

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);
    store.setImageName(handler.getImageName(), 0);

    String date =
      DateTools.formatDate(handler.getDate(), DateTools.ISO8601_FORMAT);
    store.setImageAcquiredDate(date, 0);

    if (getMetadataOptions().getMetadataLevel() == MetadataLevel.ALL) {
      for (int i=0; i<timestamps.size(); i++) {
        long timestamp = DateTools.getTime(timestamps.get(i), DATE_FORMAT);
        addGlobalMeta("timestamp " + i, timestamp);
      }
      for (int i=0; i<exposures.size(); i++) {
        addGlobalMeta("exposure time " + i + " (ms)",
          exposures.get(i).floatValue() * 1000);
      }

      long startDate = 0;
      if (timestamps.size() > 0) {
        startDate = DateTools.getTime(timestamps.get(0), DATE_FORMAT);
      }

      store.setImageDescription("", 0);

      for (int i=0; i<getImageCount(); i++) {
        int[] coords = getZCTCoords(i);
        if (coords[2] < timestamps.size()) {
          String stamp = timestamps.get(coords[2]);
          long ms = DateTools.getTime(stamp, DATE_FORMAT);
          store.setPlaneDeltaT((ms - startDate) / 1000.0, 0, i);
        }
        if (i < exposures.size()) {
          store.setPlaneExposureTime(exposures.get(i), 0, i);
        }
      }

      store.setImagingEnvironmentTemperature(handler.getTemperature(), 0);
      store.setPixelsPhysicalSizeX(handler.getPixelSizeX(), 0);
      store.setPixelsPhysicalSizeY(handler.getPixelSizeY(), 0);
    }
  }
}
