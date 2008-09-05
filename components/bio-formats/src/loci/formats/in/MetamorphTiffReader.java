//
// MetamorphTiffReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
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
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/MetamorphTiffReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/MetamorphTiffReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 * @author Thomas Caswell tcaswell at uchicago.edu
 */
public class MetamorphTiffReader extends BaseTiffReader {

  // -- Fields --

  private float pixelSizeX, pixelSizeY;
  private float[] zPositions;
  private int[] wavelengths;
  private int zpPointer, wavePointer;
  private float temperature;
  private String date, imageName;
  private Vector timestamps;

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
    if (debug) debug("MetamorphTiffReader.initFile(" + id + ")");
    super.initFile(id);

    timestamps = new Vector();

    String[] comments = new String[ifds.length];
    zPositions = new float[ifds.length];
    wavelengths = new int[ifds.length];
    zpPointer = 0;
    wavePointer = 0;

    // parse XML comment

    DefaultHandler handler = new MetamorphHandler();
    for (int i=0; i<comments.length; i++) {
      comments[i] = TiffTools.getComment(ifds[i]);
      DataTools.parseXML(comments[i], handler);
    }

    core[0].sizeC = 0;

    // calculate axis sizes

    Vector uniqueC = new Vector();
    for (int i=0; i<zPositions.length; i++) {
      Integer c = new Integer(wavelengths[i]);
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
    store.setImageName(imageName, 0);
    store.setImageDescription("", 0);

    SimpleDateFormat parse = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
    Date d = parse.parse(date, new ParsePosition(0));
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    SimpleDateFormat tsfmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    Date td;
    for (int i=0; i<timestamps.size(); i++) {
      td = parse.parse((String) timestamps.get(i), new ParsePosition(0));
      addMeta("timestamp " + i, tsfmt.format(td));
    }

    long startDate = 0;
    if (timestamps.size() > 0) {
      startDate =
        parse.parse((String) timestamps.get(0), new ParsePosition(0)).getTime();
    }

    for (int i=0; i<getImageCount(); i++) {
      int[] coords = getZCTCoords(i);
      store.setPlaneTheZ(new Integer(coords[0]), 0, 0, i);
      store.setPlaneTheC(new Integer(coords[1]), 0, 0, i);
      store.setPlaneTheT(new Integer(coords[2]), 0, 0, i);
      if (coords[2] < timestamps.size()) {
        String stamp = (String) timestamps.get(coords[2]);
        long ms = parse.parse(stamp, new ParsePosition(0)).getTime();
        store.setPlaneTimingDeltaT(new Float(ms - startDate), 0, 0, i);
        store.setPlaneTimingExposureTime(new Float(0), 0, 0, i);
      }
    }

    store.setImageCreationDate(fmt.format(d), 0);
    MetadataTools.populatePixels(store, this);

    store.setImagingEnvironmentTemperature(new Float(temperature), 0);
    store.setDimensionsPhysicalSizeX(new Float(pixelSizeX), 0, 0);
    store.setDimensionsPhysicalSizeY(new Float(pixelSizeY), 0, 0);
  }

  // -- Helper class --

  /** SAX handler for parsing XML. */
  class MetamorphHandler extends DefaultHandler {
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      String id = attributes.getValue("id");
      String value = attributes.getValue("value");
      String delim = "&#13;&#10;";
      if (id != null && value != null) {
        if (id.equals("Description")) {
          metadata.remove("Comment");

          String k = null, v = null;

          if (value.indexOf(delim) != -1) {
            int currentIndex = -delim.length();
            while (currentIndex != -1) {
              currentIndex += delim.length();
              int nextIndex = value.indexOf(delim, currentIndex);

              String line = null;
              if (nextIndex == -1) {
                line = value.substring(currentIndex, value.length());
              }
              else {
                line = value.substring(currentIndex, nextIndex);
              }
              currentIndex = nextIndex;

              int colon = line.indexOf(":");
              if (colon != -1) {
                k = line.substring(0, colon).trim();
                v = line.substring(colon + 1).trim();
                addMeta(k, v);
                if (k.equals("Temperature")) {
                  temperature = Float.parseFloat(v.trim());
                }
              }
            }
          }
          else {
            int colon = value.indexOf(":");
            while (colon != -1) {
              k = value.substring(0, colon);
              int space = value.lastIndexOf(" ", value.indexOf(":", colon + 1));
              if (space == -1) space = value.length();
              v = value.substring(colon + 1, space);
              addMeta(k, v);
              value = value.substring(space).trim();
              colon = value.indexOf(":");

              if (k.equals("Temperature")) {
                temperature = Float.parseFloat(v.trim());
              }
            }
          }
        }
        else addMeta(id, value);

        if (id.equals("spatial-calibration-x")) {
          pixelSizeX = Float.parseFloat(value);
        }
        else if (id.equals("spatial-calibration-y")) {
          pixelSizeY = Float.parseFloat(value);
        }
        else if (id.equals("z-position")) {
          zPositions[zpPointer++] = Float.parseFloat(value);
        }
        else if (id.equals("wavelength")) {
          wavelengths[wavePointer++] = Integer.parseInt(value);
        }
        else if (id.equals("acquisition-time-local")) {
          date = value;
          timestamps.add(date);
        }
        else if (id.equals("image-name")) imageName = value;
      }
    }
  }

}
