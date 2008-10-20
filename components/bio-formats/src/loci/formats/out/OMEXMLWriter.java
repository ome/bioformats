//
// OMEXMLWriter.java
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

package loci.formats.out;

import java.awt.Image;
import java.io.*;
import java.util.Vector;
import loci.common.*;
import loci.formats.*;
import loci.formats.codec.Base64Codec;
import loci.formats.codec.ZlibCodec;
import loci.formats.meta.MetadataRetrieve;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * OMEXMLWriter is the file format writer for OME-XML files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/out/OMEXMLWriter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/out/OMEXMLWriter.java">SVN</a></dd></dl>
 */
public class OMEXMLWriter extends FormatWriter {

  // -- Fields --

  private Vector xmlFragments;
  private RandomAccessFile out;
  private String currentFragment;

  // -- Constructor --

  public OMEXMLWriter() {
    super("OME-XML", new String[] {"ome"});
    compressionTypes = new String[] {"none", "zlib"};
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    if (out != null) out.close();
    out = null;
    xmlFragments = null;
  }

  // -- IFormatWriter API methods --

  /* @see loci.formats.IFormatWriter#saveImage(Image, int, boolean, boolean) */
  public void saveImage(Image image, int series, boolean lastInSeries,
    boolean last) throws FormatException, IOException
  {
    MetadataRetrieve retrieve = getMetadataRetrieve();
    if (!initialized) {
      out = new RandomAccessFile(currentId, "rw");

      String xml = MetadataTools.getOMEXML(retrieve);

      xmlFragments = new Vector();
      currentFragment = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
      DataTools.parseXML(xml, new OMEHandler());
      xmlFragments.add(currentFragment);

      out.writeBytes((String) xmlFragments.get(0));
      initialized = true;
    }

    byte[][] pix = ImageTools.getPixelBytes(ImageTools.makeBuffered(image),
      !retrieve.getPixelsBigEndian(series, 0).booleanValue());
    for (int i=0; i<pix.length; i++) {
      byte[] encodedPix = new Base64Codec().compress(pix[i], 0, 0, null, null);

      if (compression.equals("zlib")) {
        encodedPix = new ZlibCodec().compress(encodedPix, 0, 0, null, null);
      }

      StringBuffer plane = new StringBuffer("\n<Bin:BinData Length=\"");
      plane.append(encodedPix.length);
      plane.append("\"");
      if (compression != null && !compression.equals("none")) {
        plane.append(" Compression=\"");
        plane.append(compression);
        plane.append("\"");
      }
      plane.append(">");
      plane.append(new String(encodedPix));
      plane.append("</Bin:BinData>");
      out.writeBytes(plane.toString());
    }

    if (lastInSeries) {
      out.writeBytes((String) xmlFragments.get(series + 1));
    }
  }

  public boolean canDoStacks() { return true; }

  // -- Helper class --

  class OMEHandler extends DefaultHandler {
    public void characters(char[] ch, int start, int length) {
      currentFragment += new String(ch, start, length);
    }

    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      StringBuffer toAppend = new StringBuffer("\n<");
      toAppend.append(qName);
      for (int i=0; i<attributes.getLength(); i++) {
        toAppend.append(" ");
        toAppend.append(attributes.getQName(i));
        toAppend.append("=\"");
        toAppend.append(attributes.getValue(i));
        toAppend.append("\"");
      }
      toAppend.append(">");
      currentFragment += toAppend.toString();
      if (qName.equals("Pixels")) {
        xmlFragments.add(currentFragment);
        currentFragment = "";
      }
    }

    public void endElement(String uri, String localName, String qName) {
      currentFragment += "</" + qName + ">";
    }

  }

}
