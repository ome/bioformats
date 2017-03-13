/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

package loci.formats.in;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import loci.common.CBZip2InputStream;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.BaseHandler;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.MissingLibraryException;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEG2000Codec;
import loci.formats.codec.JPEGCodec;
import loci.formats.codec.ZlibCodec;
import loci.formats.meta.MetadataStore;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;
import loci.formats.services.OMEXMLServiceImpl;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;
import com.google.common.io.BaseEncoding;


/**
 * OMEXMLReader is the file format reader for OME-XML files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class OMEXMLReader extends FormatReader {

  // -- Fields --

  // compression value and offset for each BinData element
  private List<BinData> binData;
  private List<Long> binDataOffsets;
  private List<String> compression;

  private String omexml;
  private boolean hasSPW = false;

  // -- Constructor --

  /** Constructs a new OME-XML reader. */
  public OMEXMLReader() {
    super("OME-XML", new String[] {"ome", "ome.xml"});
    domains = FormatTools.NON_GRAPHICS_DOMAINS;
    suffixNecessary = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 64;
    String xml = stream.readString(blockLen);
    return xml.startsWith("<?xml") && xml.indexOf("<OME") >= 0;
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, "companion.ome")) {
      // pass binary-only files along to the OME-TIFF reader
      return false;
    }
    return super.isThisType(name, open);
  }

  /* @see loci.formats.IFormatReader#getDomains() */
  @Override
  public String[] getDomains() {
    FormatTools.assertId(currentId, true, 1);
    return hasSPW ? new String[] {FormatTools.HCS_DOMAIN} :
      FormatTools.NON_SPECIAL_DOMAINS;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (binDataOffsets.size() == 0) return buf;
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int index = no;
    int series = getSeries();
    for (int i=0; i<series; i++) {
      index += core.get(i).imageCount;
    }
    if (index >= binDataOffsets.size()) {
      index = binDataOffsets.size() - 1;
    }

    long offset = binDataOffsets.get(index).longValue();
    String compress = compression.get(index);

    in.seek(offset);

    int depth = FormatTools.getBytesPerPixel(getPixelType());
    int planeSize = getSizeX() * getSizeY() * depth;

    CodecOptions options = new CodecOptions();
    options.width = getSizeX();
    options.height = getSizeY();
    options.bitsPerSample = depth * 8;
    options.channels = getRGBChannelCount();
    options.maxBytes = planeSize;
    options.littleEndian = isLittleEndian();
    options.interleaved = isInterleaved();

    String encoded = in.readString("<");
    encoded = encoded.trim();
    if (encoded.length() == 0 || encoded.equals("<")) {
      LOGGER.debug("No pixel data for plane #{}", no);
      return buf;
    }
    encoded = encoded.substring(0, encoded.length() - 1);
    byte[] pixels =  BaseEncoding.base64().decode(encoded);
    // return a blank plane if no pixel data was stored
    if (pixels.length == 0) {
      LOGGER.debug("No pixel data for plane #{}", no);
      return buf;
    }

    // TODO: Create a method uncompress to handle all compression methods
    if (compress.equals("bzip2")) {
      byte[] tempPixels = pixels;
      pixels = new byte[tempPixels.length - 2];
      System.arraycopy(tempPixels, 2, pixels, 0, pixels.length);

      ByteArrayInputStream bais = new ByteArrayInputStream(pixels);
      CBZip2InputStream bzip = new CBZip2InputStream(bais);
      pixels = new byte[planeSize];
      bzip.read(pixels, 0, pixels.length);
      tempPixels = null;
      bais.close();
      bzip.close();
      bais = null;
      bzip = null;
    }
    else if (compress.equals("zlib")) {
      pixels = new ZlibCodec().decompress(pixels, options);
    }
    else if (compress.equals("J2K")) {
      pixels = new JPEG2000Codec().decompress(pixels, options);
    }
    else if (compress.equals("JPEG")) {
      pixels = new JPEGCodec().decompress(pixels, options);
    }

    for (int row=0; row<h; row++) {
      int off = (row + y) * getSizeX() * depth + x * depth;
      System.arraycopy(pixels, off, buf, row * w * depth, w * depth);
    }

    pixels = null;

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      compression = null;
      binDataOffsets = null;
      binData = null;
      omexml = null;
      hasSPW = false;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    in = new RandomAccessInputStream(id);
    in.setEncoding("ASCII");
    binData = new ArrayList<BinData>();
    binDataOffsets = new ArrayList<Long>();
    compression = new ArrayList<String>();

    DefaultHandler handler = new OMEXMLHandler();
    try {
      RandomAccessInputStream s = new RandomAccessInputStream(id);
      XMLTools.parseXML(s, handler);
      s.close();
    }
    catch (IOException e) {
      throw new FormatException("Malformed OME-XML", e);
    }

    int lineNumber = 1;
    for (BinData bin : binData) {
      int line = bin.getRow();
      int col = bin.getColumn();

      while (lineNumber < line) {
        in.readLine();
        lineNumber++;
      }
      binDataOffsets.add(in.getFilePointer() + col - 1);
    }

    LOGGER.info("Populating metadata");

    OMEXMLMetadata omexmlMeta;
    OMEXMLService service;
    try {
      ServiceFactory factory = new ServiceFactory();
      service = factory.getInstance(OMEXMLService.class);
      omexmlMeta = service.createOMEXMLMetadata(omexml);
    }
    catch (DependencyException de) {
      throw new MissingLibraryException(OMEXMLServiceImpl.NO_OME_XML_MSG, de);
    }
    catch (ServiceException se) {
      throw new FormatException(se);
    }

    hasSPW = omexmlMeta.getPlateCount() > 0;

    // TODO
    //Hashtable originalMetadata = omexmlMeta.getOriginalMetadata();
    //if (originalMetadata != null) metadata = originalMetadata;

    int numDatasets = omexmlMeta.getImageCount();

    int oldSeries = getSeries();
    core.clear();
    for (int i=0; i<numDatasets; i++) {
      CoreMetadata ms = new CoreMetadata();
      core.add(ms);

      setSeries(i);

      Integer w = omexmlMeta.getPixelsSizeX(i).getValue();
      Integer h = omexmlMeta.getPixelsSizeY(i).getValue();
      Integer t = omexmlMeta.getPixelsSizeT(i).getValue();
      Integer z = omexmlMeta.getPixelsSizeZ(i).getValue();
      Integer c = omexmlMeta.getPixelsSizeC(i).getValue();
      if (w == null || h == null || t == null || z == null | c == null) {
        throw new FormatException("Image dimensions not found");
      }

      Boolean endian = null;
      if (binData.size() > 0) {
        endian = false;
        if (omexmlMeta.getPixelsBigEndian(i) != null) {
          endian = omexmlMeta.getPixelsBigEndian(i).booleanValue();
        }
        else if (omexmlMeta.getPixelsBinDataCount(i) != 0) {
          endian = omexmlMeta.getPixelsBinDataBigEndian(i, 0).booleanValue();
        }
      }
      String pixType = omexmlMeta.getPixelsType(i).toString();
      ms.dimensionOrder = omexmlMeta.getPixelsDimensionOrder(i).toString();
      ms.sizeX = w.intValue();
      ms.sizeY = h.intValue();
      ms.sizeT = t.intValue();
      ms.sizeZ = z.intValue();
      ms.sizeC = c.intValue();
      ms.imageCount = getSizeZ() * getSizeC() * getSizeT();
      ms.littleEndian = endian == null ? false : !endian.booleanValue();
      ms.rgb = false;
      ms.interleaved = false;
      ms.indexed = false;
      ms.falseColor = true;
      ms.pixelType = FormatTools.pixelTypeFromString(pixType);
      ms.orderCertain = true;
      if (omexmlMeta.getPixelsSignificantBits(i) != null) {
        ms.bitsPerPixel = omexmlMeta.getPixelsSignificantBits(i).getValue();
      }
    }
    setSeries(oldSeries);

    // populate assigned metadata store with the
    // contents of the internal OME-XML metadata object
    MetadataStore store = getMetadataStore();
    service.convertMetadata(omexmlMeta, store);
    MetadataTools.populatePixels(store, this, false, false);
  }

  // -- Helper class --

  class OMEXMLHandler extends BaseHandler {
    private final StringBuilder xmlBuffer;
    private String currentQName;
    private Locator locator;
    private boolean inPixels;

    public OMEXMLHandler() {
      xmlBuffer = new StringBuilder();
      inPixels = false;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
      if (!inPixels || currentQName.indexOf("BinData") < 0) {
        xmlBuffer.append(new String(ch, start, length));
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      if (qName.indexOf("Pixels") != -1) {
        inPixels = false;
      }

      xmlBuffer.append("</");
      xmlBuffer.append(qName);
      xmlBuffer.append(">");
    }

    @Override
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      currentQName = qName;

      if (qName.indexOf("Pixels") != -1) {
        inPixels = true;
      }

      if (inPixels && qName.indexOf("BinData") != -1) {
        binData.add(
          new BinData(locator.getLineNumber(), locator.getColumnNumber()));
        String compress = attributes.getValue("Compression");
        compression.add(compress == null ? "" : compress);

        xmlBuffer.append("<");
        xmlBuffer.append(qName);
        for (int i=0; i<attributes.getLength(); i++) {
          String key = XMLTools.escapeXML(attributes.getQName(i));
          String value = XMLTools.escapeXML(attributes.getValue(i));
          if (key.equals("Length")) value = "0";
          xmlBuffer.append(" ");
          xmlBuffer.append(key);
          xmlBuffer.append("=\"");
          xmlBuffer.append(value);
          xmlBuffer.append("\"");
        }
        xmlBuffer.append(">");
      } else {
        xmlBuffer.append("<");
        xmlBuffer.append(qName);
        for (int i=0; i<attributes.getLength(); i++) {
          String key = XMLTools.escapeXML(attributes.getQName(i));
          String value = XMLTools.escapeXML(attributes.getValue(i));
          if (key.equals("BigEndian")) {
            String endian = value.toLowerCase();
            if (!endian.equals("true") && !endian.equals("false")) {
              // hack for files that specify 't' or 'f' instead of
              // 'true' or 'false'
              if (endian.startsWith("t")) endian = "true";
              else if (endian.startsWith("f")) endian = "false";
            }
            value = endian;
          }
          xmlBuffer.append(" ");
          xmlBuffer.append(key);
          xmlBuffer.append("=\"");
          xmlBuffer.append(value);
          xmlBuffer.append("\"");
        }
        xmlBuffer.append(">");
      }
    }

    @Override
    public void endDocument() {
      omexml = xmlBuffer.toString();
    }

    @Override
    public void setDocumentLocator(Locator locator) {
      this.locator = locator;
    }
  }

  class BinData {
    private int row;
    private int column;

    public BinData(int row, int column) {
      this.row = row;
      this.column = column;
    }

    public int getRow() { return row; }
    public int getColumn() { return column; }
  }

}
