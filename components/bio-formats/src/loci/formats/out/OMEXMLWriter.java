//
// OMEXMLWriter.java
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

package loci.formats.out;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Vector;
import loci.common.*;
import loci.formats.*;
import loci.formats.codec.*;
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
  private RandomAccessOutputStream out;
  private String currentFragment;

  // -- Constructor --

  public OMEXMLWriter() {
    super("OME-XML", "ome");
    compressionTypes =
      new String[] {"Uncompressed", "zlib", "J2K", "JPEG"};
    compression = compressionTypes[0];
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
    MetadataTools.verifyMinimumPopulated(retrieve, series);
    if (!initialized) {
      out = new RandomAccessOutputStream(currentId);

      String xml = MetadataTools.getOMEXML(retrieve);

      xmlFragments = new Vector();
      currentFragment = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
      DataTools.parseXML(xml, new OMEHandler());
      xmlFragments.add(currentFragment);

      out.writeBytes((String) xmlFragments.get(0));
      initialized = true;
    }
    boolean littleEndian =
      !retrieve.getPixelsBigEndian(series, 0).booleanValue();
    BufferedImage buffImage = AWTImageTools.makeBuffered(image);
    byte[][] pix = AWTImageTools.getPixelBytes(buffImage, littleEndian);

    String type = retrieve.getPixelsPixelType(series, 0);
    int pixelType = FormatTools.pixelTypeFromString(type);

    CodecOptions options = new CodecOptions();
    options.width = buffImage.getWidth();
    options.height = buffImage.getHeight();
    options.channels = 1;
    options.interleaved = false;
    options.littleEndian = littleEndian;
    options.signed = FormatTools.isSigned(pixelType);

    buffImage = null;

    for (int i = 0; i < pix.length; i++) {
      // TODO: Create a method compress to handle all compression methods
      int bytes = pix[i].length / (options.width * options.height);
      options.bitsPerSample = bytes * 8;

      if (compression.equals("J2K")) {
        pix[i] = new JPEG2000Codec().compress(pix[i], options);
      }
      else if (compression.equals("JPEG")) {
        pix[i] = new JPEGCodec().compress(pix[i], options);
      }
      else if (compression.equals("zlib")) {
        pix[i] = new ZlibCodec().compress(pix[i], options);
      }
      byte[] encodedPix = new Base64Codec().compress(pix[i], options);

      StringBuffer plane = new StringBuffer("\n<Bin:BinData Length=\"");
      plane.append(encodedPix.length);
      plane.append("\"");
      if (compression != null && !compression.equals("Uncompressed")) {
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
      close();
    }
  }

  /* @see loci.formats.IFormatWriter#canDoStacks() */
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
