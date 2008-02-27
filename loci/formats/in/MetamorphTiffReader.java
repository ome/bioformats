//
// MetamorphTiffReader.java
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
import java.util.*;
import javax.xml.parsers.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * MetamorphTiffReader is the file format reader for TIFF files produced by
 * Metamorph software version 7.5 and above.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/MetamorphTiffReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/MetamorphTiffReader.java">SVN</a></dd></dl>
 */
public class MetamorphTiffReader extends BaseTiffReader {

  // -- Constants --

  /** Factory for generating SAX parsers. */
  public static final SAXParserFactory SAX_FACTORY =
    SAXParserFactory.newInstance();

  // -- Fields --

  private float pixelSizeX, pixelSizeY;
  private float[] zPositions;
  private int[] wavelengths;
  private int zpPointer, wavePointer;
  private float temperature;
  private String date, imageName;

  // -- Constructor --

  /** Constructs a new Metamorph TIFF reader. */
  public MetamorphTiffReader() {
    super("Metamorph TIFF", new String[] {"tif", "tiff"});
    blockCheckLen = 524288;
    suffixSufficient = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    try {
      RandomAccessStream stream = new RandomAccessStream(block);
      String comment = TiffTools.getComment(TiffTools.getFirstIFD(stream));
      return comment != null && comment.trim().startsWith("<MetaData>");
    }
    catch (IOException e) {
      if (debug) LogTools.trace(e);
    }
    catch (ArrayIndexOutOfBoundsException e) {
      if (debug) LogTools.trace(e);
    }
    return false;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("MetamorphTiffReader.initFile(" + id + ")");
    super.initFile(id);

    String[] comments = new String[ifds.length];
    zPositions = new float[ifds.length];
    wavelengths = new int[ifds.length];
    zpPointer = 0;
    wavePointer = 0;
    MetamorphHandler handler = new MetamorphHandler();
    try {
      SAXParser parser = SAX_FACTORY.newSAXParser();
      for (int i=0; i<comments.length; i++) {
        comments[i] = TiffTools.getComment(ifds[i]);
        parser.parse(new ByteArrayInputStream(comments[i].getBytes()), handler);
      }
    }
    catch (SAXException e) {
      if (debug) LogTools.trace(e);
    }
    catch (ParserConfigurationException e) {
      if (debug) LogTools.trace(e);
    }

    core.sizeC[0] = core.sizeZ[0] = 0;

    Vector uniqueZ = new Vector();
    Vector uniqueC = new Vector();
    for (int i=0; i<zPositions.length; i++) {
      Float z = new Float(zPositions[i]);
      Integer c = new Integer(wavelengths[i]);
      if (!uniqueZ.contains(z)) {
        uniqueZ.add(z);
        core.sizeZ[0]++;
      }
      if (!uniqueC.contains(c)) {
        uniqueC.add(c);
        core.sizeC[0]++;
      }
    }

    core.sizeT[0] = ifds.length / (core.sizeZ[0] * core.sizeC[0]);

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    store.setImageName(imageName, 0);
    store.setImageDescription("", 0);

    SimpleDateFormat parse = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SS");
    Date d = parse.parse(date, new ParsePosition(0));
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

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
      if (id != null && value != null) {
        if (id.equals("Description")) {
          metadata.remove("Comment");
          String k = null, v = null;
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
        else if (id.equals("acquisition-time-local")) date = value;
        else if (id.equals("image-name")) imageName = value;
      }
    }
  }

}
