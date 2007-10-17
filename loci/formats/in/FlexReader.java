//
// FlexReader.java
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;
import javax.xml.parsers.*;
import loci.formats.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * FlexReader is a file format reader for Evotec Flex files.
 * To use it, the LuraWave decoder library, lwf_jsdk2.6.jar, must be available,
 * and a LuraWave license key must be specified in the lurawave.license system
 * property (e.g., <code>-Dlurawave.license=XXXX</code> on the command line).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/FlexReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/FlexReader.java">SVN</a></dd></dl>
 */
public class FlexReader extends BaseTiffReader {

  // -- Constants --

  /** Custom IFD entry for Flex XML. */
  protected static final int FLEX = 65200;

  /** Factory for generating SAX parsers. */
  public static final SAXParserFactory SAX_FACTORY =
    SAXParserFactory.newInstance();

  // -- Fields --

  /** Scale factor for each image. */
  protected double[] factors;

  // -- Constructor --

  /** Constructs a new Flex reader. */
  public FlexReader() { super("Evotec Flex", "flex"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) { return false; }

  /* @see loci.formats.FormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);

    // expand pixel values with multiplication by factor[no]
    byte[] bytes = super.openBytes(no, buf);
    if (core.pixelType[0] == FormatTools.UINT8) {
      int num = bytes.length;
      for (int i=num-1; i>=0; i--) {
        int q = (int) ((bytes[i] & 0xff) * factors[no]);
        bytes[i] = (byte) (q & 0xff);
      }
    }
    if (core.pixelType[0] == FormatTools.UINT16) {
      int num = bytes.length / 2;
      for (int i=num-1; i>=0; i--) {
        int q = (int) ((bytes[i] & 0xff) * factors[no]);
        byte b0 = (byte) (q & 0xff);
        byte b1 = (byte) ((q >> 8) & 0xff);
        int ndx = 2 * i;
        if (core.littleEndian[0]) {
          bytes[ndx] = b0;
          bytes[ndx + 1] = b1;
        }
        else {
          bytes[ndx] = b1;
          bytes[ndx + 1] = b0;
        }
      }
    }
    else if (core.pixelType[0] == FormatTools.UINT32) {
      int num = bytes.length / 4;
      for (int i=num-1; i>=0; i--) {
        int q = (int) ((bytes[i] & 0xff) * factors[no]);
        byte b0 = (byte) (q & 0xff);
        byte b1 = (byte) ((q >> 8) & 0xff);
        byte b2 = (byte) ((q >> 16) & 0xff);
        byte b3 = (byte) ((q >> 24) & 0xff);
        int ndx = 4 * i;
        if (core.littleEndian[0]) {
          bytes[ndx] = b0;
          bytes[ndx + 1] = b1;
          bytes[ndx + 2] = b2;
          bytes[ndx + 3] = b3;
        }
        else {
          bytes[ndx] = b3;
          bytes[ndx + 1] = b2;
          bytes[ndx + 2] = b1;
          bytes[ndx + 3] = b0;
        }
      }
    }
    return bytes;
  }

  // -- Internal BaseTiffReader API methods --

  /* @see loci.formats.BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    core.orderCertain[0] = false;

    // parse factors from XML
    String xml = (String) TiffTools.getIFDValue(ifds[0],
      FLEX, true, String.class);
    Vector n = new Vector();
    Vector f = new Vector();
    FlexHandler handler = new FlexHandler(n, f);
    try {
      SAXParser saxParser = SAX_FACTORY.newSAXParser();
      saxParser.parse(new ByteArrayInputStream(xml.getBytes()), handler);
    }
    catch (ParserConfigurationException exc) {
      throw new FormatException(exc);
    }
    catch (SAXException exc) {
      throw new FormatException(exc);
    }

    // verify factor count
    int nsize = n.size();
    int fsize = f.size();
    if (debug && (nsize != fsize || nsize != core.imageCount[0])) {
      LogTools.println("Warning: mismatch between image count, " +
        "names and factors (count=" + core.imageCount[0] +
        ", names=" + nsize + ", factors=" + fsize + ")");
    }
    for (int i=0; i<nsize; i++) addMeta("Name " + i, n.get(i));
    for (int i=0; i<fsize; i++) addMeta("Factor " + i, f.get(i));

    // parse factor values
    factors = new double[core.imageCount[0]];
    int max = 0;
    for (int i=0; i<fsize; i++) {
      String factor = (String) f.get(i);
      double q = 1;
      try {
        q = Double.parseDouble(factor);
      }
      catch (NumberFormatException exc) {
        if (debug) {
          LogTools.println("Warning: invalid factor #" + i + ": " + factor);
        }
      }
      factors[i] = q;
      if (q > factors[max]) max = i;
    }
    Arrays.fill(factors, fsize, factors.length, 1);

    // determine pixel type
    if (factors[max] > 256) core.pixelType[0] = FormatTools.UINT32;
    else if (factors[max] > 1) core.pixelType[0] = FormatTools.UINT16;
    else core.pixelType[0] = FormatTools.UINT8;
  }

  // -- Helper classes --

  /** SAX handler for parsing XML. */
  public class FlexHandler extends DefaultHandler {
    private Vector names, factors;
    public FlexHandler(Vector names, Vector factors) {
      this.names = names;
      this.factors = factors;
    }
    public void startElement(String uri,
      String localName, String qName, Attributes attributes)
    {
      if (!qName.equals("Array")) return;
      int len = attributes.getLength();
      for (int i=0; i<len; i++) {
        String name = attributes.getQName(i);
        if (name.equals("Name")) names.add(attributes.getValue(i));
        else if (name.equals("Factor")) factors.add(attributes.getValue(i));
      }
    }
  }

}
