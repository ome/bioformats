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

import java.io.*;
import java.text.*;
import java.util.*;
import loci.common.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

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
    blockCheckLen = 524288;
    suffixSufficient = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (!open) return false;
    try {
      RandomAccessStream stream = new RandomAccessStream(name);
      boolean isThisType = isThisType(stream);
      stream.close();
      return isThisType;
    }
    catch (IOException e) {
      if (debug) trace(e);
    }
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    if (!FormatTools.validStream(stream, blockCheckLen, false)) return false;
    String comment = TiffTools.getComment(TiffTools.getFirstIFD(stream));
    return comment != null && comment.trim().startsWith("<MetaData>");
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("MetamorphTiffReader.initFile(" + id + ")");
    super.initFile(id);

    String[] comments = new String[ifds.length];

    // parse XML comment

    MetamorphHandler handler = new MetamorphHandler(getMetadata());
    for (int i=0; i<comments.length; i++) {
      comments[i] = TiffTools.getComment(ifds[i]);
      DataTools.parseXML(comments[i], handler);
    }

    core[0].sizeC = 0;

    Vector timestamps = handler.getTimestamps();
    Vector wavelengths = handler.getWavelengths();
    Vector zPositions = handler.getZPositions();
    Vector exposures = handler.getExposures();

    // calculate axis sizes

    Vector uniqueC = new Vector();
    for (int i=0; i<zPositions.size(); i++) {
      Integer c = (Integer) wavelengths.get(i);
      if (!uniqueC.contains(c)) {
        uniqueC.add(c);
        core[0].sizeC++;
      }
    }

    core[0].sizeT = timestamps.size();
    if (core[0].sizeT == 0) core[0].sizeT = 1;
    core[0].sizeZ = ifds.length / (getSizeT() * getSizeC());

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this, true);
    store.setImageName(handler.getImageName(), 0);
    store.setImageDescription("", 0);

    SimpleDateFormat parse = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
    Date d = parse.parse(handler.getDate(), new ParsePosition(0));
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    SimpleDateFormat tsfmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    Date td;
    for (int i=0; i<timestamps.size(); i++) {
      td = parse.parse((String) timestamps.get(i), new ParsePosition(0));
      addMeta("timestamp " + i, tsfmt.format(td));
    }
    for (int i=0; i<exposures.size(); i++) {
      addMeta("exposure time " + i + " (ms)",
        ((Float) exposures.get(i)).floatValue() * 1000);
    }

    long startDate = 0;
    if (timestamps.size() > 0) {
      startDate =
        parse.parse((String) timestamps.get(0), new ParsePosition(0)).getTime();
    }

    for (int i=0; i<getImageCount(); i++) {
      int[] coords = getZCTCoords(i);
      if (coords[2] < timestamps.size()) {
        String stamp = (String) timestamps.get(coords[2]);
        long ms = parse.parse(stamp, new ParsePosition(0)).getTime();
        store.setPlaneTimingDeltaT(new Float((ms - startDate) / 1000f),
          0, 0, i);
        store.setPlaneTimingExposureTime((Float) exposures.get(i), 0, 0, i);
      }
    }

    store.setImageCreationDate(fmt.format(d), 0);

    store.setImagingEnvironmentTemperature(
      new Float(handler.getTemperature()), 0);
    store.setDimensionsPhysicalSizeX(new Float(handler.getPixelSizeX()), 0, 0);
    store.setDimensionsPhysicalSizeY(new Float(handler.getPixelSizeY()), 0, 0);
  }
}
