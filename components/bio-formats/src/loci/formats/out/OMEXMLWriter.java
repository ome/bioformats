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

import java.io.IOException;
import java.util.Vector;

import loci.common.RandomAccessOutputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.ImageTools;
import loci.formats.MetadataTools;
import loci.formats.MissingLibraryException;
import loci.formats.codec.Base64Codec;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEG2000Codec;
import loci.formats.codec.JPEGCodec;
import loci.formats.codec.ZlibCodec;
import loci.formats.in.OMETiffReader;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.services.OMEXMLService;

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

  private Vector<String> xmlFragments;
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
    initialized = false;
  }

  // -- IFormatWriter API methods --

  /* @see loci.formats.IFormatWriter#saveBytes(byte[], int, boolean, boolean) */
  public void saveBytes(byte[] buf, int series, boolean lastInSeries,
    boolean last) throws FormatException, IOException
  {
    MetadataRetrieve retrieve = getMetadataRetrieve();
    MetadataTools.verifyMinimumPopulated(retrieve, series);
    if (!initialized) {
      out = new RandomAccessOutputStream(currentId);

      String xml;
      try {
        ServiceFactory factory = new ServiceFactory();
        OMEXMLService service = factory.getInstance(OMEXMLService.class);
        xml = service.getOMEXML(retrieve);
      }
      catch (DependencyException de) {
        throw new MissingLibraryException(OMETiffReader.NO_OME_XML_MSG, de);
      }
      catch (ServiceException se) {
        throw new FormatException(se);
      }

      xmlFragments = new Vector<String>();
      currentFragment = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
      XMLTools.parseXML(xml, new OMEHandler());

      xmlFragments.add(currentFragment);

      out.writeBytes(xmlFragments.get(0));
      initialized = true;
    }
    boolean littleEndian =
      !retrieve.getPixelsBigEndian(series, 0).booleanValue();

    String type = retrieve.getPixelsPixelType(series, 0);
    int pixelType = FormatTools.pixelTypeFromString(type);

    CodecOptions options = new CodecOptions();
    options.width = retrieve.getPixelsSizeX(series, 0).intValue();
    options.height = retrieve.getPixelsSizeY(series, 0).intValue();
    options.channels = 1;
    options.interleaved = false;
    options.littleEndian = littleEndian;
    options.signed = FormatTools.isSigned(pixelType);

    // TODO: Create a method compress to handle all compression methods
    int bytes = FormatTools.getBytesPerPixel(pixelType);
    options.bitsPerSample = bytes * 8;

    Integer channels = retrieve.getLogicalChannelSamplesPerPixel(series, 0);
    if (channels == null) {
      LOGGER.warn("SamplesPerPixel #0 is null.  It is assumed to be 1.");
    }
    int nChannels = channels == null ? 1 : channels.intValue();

    for (int i=0; i<nChannels; i++) {
      byte[] b = ImageTools.splitChannels(buf, i, nChannels, bytes, false,
        interleaved);
      if (compression.equals("J2K")) {
        b = new JPEG2000Codec().compress(b, options);
      }
      else if (compression.equals("JPEG")) {
        b = new JPEGCodec().compress(b, options);
      }
      else if (compression.equals("zlib")) {
        b = new ZlibCodec().compress(b, options);
      }
      byte[] encodedPix = new Base64Codec().compress(b, options);

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
    }
    if (last) close();
  }

  /* @see loci.formats.IFormatWriter#canDoStacks() */
  public boolean canDoStacks() { return true; }

  /* @see loci.formats.IFormatWriter#getPixelTypes(String) */
  public int[] getPixelTypes(String codec) {
    if (codec.equals("J2K") || codec.equals("JPEG")) {
      return new int[] {FormatTools.INT8, FormatTools.UINT8};
    }
    return getPixelTypes();
  }

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
