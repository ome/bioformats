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

import java.awt.image.BufferedImage;
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
    // TODO: expand 8-bit -> 16-bit with multiplication by factor[no]
    return super.openBytes(no, buf);
  }

  /* @see loci.formats.IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    // TODO: expand 8-bit -> 16-bit with multiplication by factor[no]
    return super.openImage(no);
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
      System.err.println("Warning: mismatch between image count, " +
        "names and factors (count=" + core.imageCount[0] +
        ", names=" + nsize + ", factors=" + fsize + ")");
    }
    for (int i=0; i<nsize; i++) addMeta("Name " + i, n.get(i));
    for (int i=0; i<fsize; i++) addMeta("Factor " + i, f.get(i));

    // parse factor values
    factors = new double[core.imageCount[0]];
    for (int i=0; i<fsize; i++) {
      String factor = (String) f.get(i);
      double q = 1;
      try {
        q = Double.parseDouble(factor);
      }
      catch (NumberFormatException exc) {
        if (debug) {
          System.err.println("Warning: invalid factor #" + i + ": " + factor);
        }
      }
      factors[i] = q;
    }
    Arrays.fill(factors, fsize, factors.length, 1);
  }

  // -- Helper classes --

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
