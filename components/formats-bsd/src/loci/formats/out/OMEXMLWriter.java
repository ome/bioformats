/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.out;

import java.io.IOException;
import java.util.Vector;

import loci.common.Constants;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.BaseHandler;
import loci.common.xml.XMLTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.ImageTools;
import loci.formats.MissingLibraryException;
import loci.formats.codec.Base64Codec;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.CompressionType;
import loci.formats.codec.JPEG2000Codec;
import loci.formats.codec.JPEGCodec;
import loci.formats.codec.ZlibCodec;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;
import loci.formats.services.OMEXMLServiceImpl;

import ome.xml.meta.OMEXMLMetadataRoot;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * OMEXMLWriter is the file format writer for OME-XML files.
 */
public class OMEXMLWriter extends FormatWriter {

  // -- Fields --

  private Vector<String> xmlFragments;
  private String currentFragment;
  private OMEXMLService service;

  // -- Constructor --

  public OMEXMLWriter() {
    super("OME-XML", new String[] {"ome", "ome.xml"});
    compressionTypes =
      new String[] {CompressionType.UNCOMPRESSED.getCompression(),
        CompressionType.ZLIB.getCompression()};
    compression = compressionTypes[0];
  }

  // -- FormatWriter API methods --

  /* @see loci.formats.FormatWriter#setId(String) */
  @Override
  public void setId(String id) throws FormatException, IOException {
    if (id.equals(currentId)) {
      return;
    }
    super.setId(id);

    MetadataRetrieve retrieve = getMetadataRetrieve();

    String xml;
    try {
      ServiceFactory factory = new ServiceFactory();
      service = factory.getInstance(OMEXMLService.class);
      xml = service.getOMEXML(retrieve);
      OMEXMLMetadata noBin = service.createOMEXMLMetadata(xml);
      service.removeBinData(noBin);

      OMEXMLMetadataRoot root = (OMEXMLMetadataRoot) noBin.getRoot();
      root.setCreator(FormatTools.CREATOR);
      xml = service.getOMEXML(noBin);
    }
    catch (DependencyException de) {
      throw new MissingLibraryException(OMEXMLServiceImpl.NO_OME_XML_MSG, de);
    }
    catch (ServiceException se) {
      throw new FormatException(se);
    }

    xmlFragments = new Vector<String>();
    currentFragment = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    XMLTools.parseXML(xml, new OMEHandler());

    xmlFragments.add(currentFragment);
  }

  /* @see loci.formats.IFormatHandler#close() */
  @Override
  public void close() throws IOException {
    if (out != null) {
      out.writeBytes(xmlFragments.get(xmlFragments.size() - 1));
    }
    if (getMetadataOptions().isValidate()) {
      try {
        MetadataRetrieve r = getMetadataRetrieve();
        String omexml = service.getOMEXML(r);
        service.validateOMEXML(omexml);
      } catch (ServiceException | NullPointerException e) {
        LOGGER.warn("OMEXMLService unable to create OME-XML metadata object.", e);
      }
    }
    super.close();
    xmlFragments = null;
    service = null;
  }

  // -- IFormatWriter API methods --

  /**
   * @see loci.formats.IFormatWriter#saveBytes(int, byte[], int, int, int, int)
   */
  @Override
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    checkParams(no, buf, x, y, w, h);
    if (!isFullPlane(x, y, w, h)) {
      throw new FormatException(
        "OMEXMLWriter does not yet support saving image tiles.");
    }
    MetadataRetrieve retrieve = getMetadataRetrieve();

    if (no == 0) {
      out.writeBytes(xmlFragments.get(series));
    }

    String type = retrieve.getPixelsType(series).toString();
    int pixelType = FormatTools.pixelTypeFromString(type);
    int bytes = FormatTools.getBytesPerPixel(pixelType);
    int nChannels = getSamplesPerPixel();
    int sizeX = retrieve.getPixelsSizeX(series).getValue().intValue();
    int sizeY = retrieve.getPixelsSizeY(series).getValue().intValue();
    int planeSize = sizeX * sizeY * bytes;
    boolean bigEndian = false;
    if (retrieve.getPixelsBigEndian(series) != null) {
      bigEndian = retrieve.getPixelsBigEndian(series).booleanValue();
    }
    else if (retrieve.getPixelsBinDataCount(series) == 0) {
      bigEndian = retrieve.getPixelsBinDataBigEndian(series, 0).booleanValue();
    }

    String namespace =
      "xmlns=\"http://www.openmicroscopy.org/Schemas/OME/" +
      service.getLatestVersion() + "\"";

    for (int i=0; i<nChannels; i++) {
      byte[] b = ImageTools.splitChannels(buf, i, nChannels, bytes, false,
        interleaved);
      byte[] encodedPix = compress(b);

      StringBuffer plane = new StringBuffer("\n<BinData ");
      plane.append(namespace);
      plane.append(" Length=\"");
      plane.append(planeSize);
      plane.append("\"");
      plane.append(" BigEndian=\"");
      plane.append(bigEndian);
      plane.append("\"");
      if (compression != null && !compression.equals("Uncompressed")) {
        plane.append(" Compression=\"");
        plane.append(compression);
        plane.append("\"");
      }
      plane.append(">");
      plane.append(new String(encodedPix, Constants.ENCODING));
      plane.append("</BinData>");
      out.writeBytes(plane.toString());
    }
  }

  /* @see loci.formats.IFormatWriter#canDoStacks() */
  @Override
  public boolean canDoStacks() { return true; }

  /* @see loci.formats.IFormatWriter#getPixelTypes(String) */
  @Override
  public int[] getPixelTypes(String codec) {
    if (codec != null && (codec.equals("J2K") || codec.equals("JPEG"))) {
      return new int[] {FormatTools.INT8, FormatTools.UINT8};
    }
    return super.getPixelTypes(codec);
  }

  // -- Helper methods --

  /**
   * Compress the given byte array using the current codec.
   * The compressed data is then base64-encoded.
   */
  private byte[] compress(byte[] b) throws FormatException, IOException {
    MetadataRetrieve r = getMetadataRetrieve();
    String type = r.getPixelsType(series).toString();
    int pixelType = FormatTools.pixelTypeFromString(type);
    int bytes = FormatTools.getBytesPerPixel(pixelType);

    CodecOptions options = new CodecOptions();
    options.width = r.getPixelsSizeX(series).getValue().intValue();
    options.height = r.getPixelsSizeY(series).getValue().intValue();
    options.channels = 1;
    options.interleaved = false;
    options.signed = FormatTools.isSigned(pixelType);
    boolean littleEndian = false;
    if (r.getPixelsBigEndian(series) != null) {
      littleEndian = !r.getPixelsBigEndian(series).booleanValue();
    }
    else if (r.getPixelsBinDataCount(series) == 0) {
      littleEndian = !r.getPixelsBinDataBigEndian(series, 0).booleanValue();
    }
    options.littleEndian =littleEndian;
    options.bitsPerSample = bytes * 8;

    if (compression.equals("J2K")) {
      b = new JPEG2000Codec().compress(b, options);
    }
    else if (compression.equals("JPEG")) {
      b = new JPEGCodec().compress(b, options);
    }
    else if (compression.equals("zlib")) {
      b = new ZlibCodec().compress(b, options);
    }
    return new Base64Codec().compress(b, options);
  }

  // -- Helper class --

  class OMEHandler extends BaseHandler {
    @Override
    public void characters(char[] ch, int start, int length) {
      currentFragment += new String(ch, start, length);
    }

    @Override
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      StringBuffer toAppend = new StringBuffer("\n<");
      toAppend.append(XMLTools.escapeXML(qName));
      for (int i=0; i<attributes.getLength(); i++) {
        toAppend.append(" ");
        toAppend.append(XMLTools.escapeXML(attributes.getQName(i)));
        toAppend.append("=\"");
        toAppend.append(XMLTools.escapeXML(attributes.getValue(i)));
        toAppend.append("\"");
      }
      toAppend.append(">");
      currentFragment += toAppend.toString();
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      if (qName.equals("Pixels")) {
        xmlFragments.add(currentFragment);
        currentFragment = "";
      }
      currentFragment += "</" + qName + ">";
    }

  }

}
