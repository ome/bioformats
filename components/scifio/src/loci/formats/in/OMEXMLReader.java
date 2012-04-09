//
// OMEXMLReader.java
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

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
import loci.formats.codec.Base64Codec;
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

/**
 * OMEXMLReader is the file format reader for OME-XML files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/OMEXMLReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/OMEXMLReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class OMEXMLReader extends FormatReader {

  // -- Static fields --

  private static boolean noOME = false;

  static {
    try {
      Class.forName("ome.xml.OMEXMLNode");
    }
    catch (Throwable t) {
      noOME = true;
      LOGGER.debug(OMEXMLServiceImpl.NO_OME_XML_MSG, t);
    }
  }

  // -- Fields --

  // compression value and offset for each BinData element
  private Vector<BinData> binData;
  private Vector<Long> binDataOffsets;
  private Vector<String> compression;

  private String omexml;
  private boolean hasSPW = false;

  // -- Constructor --

  /** Constructs a new OME-XML reader. */
  public OMEXMLReader() {
    super("OME-XML", "ome");
    domains = FormatTools.NON_GRAPHICS_DOMAINS;
    suffixNecessary = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 64;
    String xml = stream.readString(blockLen);
    return xml.startsWith("<?xml") && xml.indexOf("<OME") >= 0;
  }

  /* @see loci.formats.IFormatReader#getDomains() */
  public String[] getDomains() {
    FormatTools.assertId(currentId, true, 1);
    return hasSPW ? new String[] {FormatTools.HCS_DOMAIN} :
      FormatTools.NON_SPECIAL_DOMAINS;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int index = no;
    for (int i=0; i<series; i++) {
      index += core[i].imageCount;
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

    byte[] pixels = new Base64Codec().decompress(in, options);

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
  protected void initFile(String id) throws FormatException, IOException {
    if (noOME) {
      throw new MissingLibraryException(OMEXMLServiceImpl.NO_OME_XML_MSG);
    }
    super.initFile(id);

    in = new RandomAccessInputStream(id);

    binData = new Vector<BinData>();
    binDataOffsets = new Vector<Long>();
    compression = new Vector<String>();

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

    if (binDataOffsets.size() == 0) {
      throw new FormatException("Pixel data not found");
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
    addGlobalMeta("Is SPW file", hasSPW);

    // TODO
    //Hashtable originalMetadata = omexmlMeta.getOriginalMetadata();
    //if (originalMetadata != null) metadata = originalMetadata;

    int numDatasets = omexmlMeta.getImageCount();

    core = new CoreMetadata[numDatasets];

    int oldSeries = getSeries();

    for (int i=0; i<numDatasets; i++) {
      setSeries(i);

      core[i] = new CoreMetadata();

      Integer w = omexmlMeta.getPixelsSizeX(i).getValue();
      Integer h = omexmlMeta.getPixelsSizeY(i).getValue();
      Integer t = omexmlMeta.getPixelsSizeT(i).getValue();
      Integer z = omexmlMeta.getPixelsSizeZ(i).getValue();
      Integer c = omexmlMeta.getPixelsSizeC(i).getValue();
      if (w == null || h == null || t == null || z == null | c == null) {
        throw new FormatException("Image dimensions not found");
      }

      Boolean endian = omexmlMeta.getPixelsBinDataBigEndian(i, 0);
      String pixType = omexmlMeta.getPixelsType(i).toString();
      core[i].dimensionOrder = omexmlMeta.getPixelsDimensionOrder(i).toString();
      core[i].sizeX = w.intValue();
      core[i].sizeY = h.intValue();
      core[i].sizeT = t.intValue();
      core[i].sizeZ = z.intValue();
      core[i].sizeC = c.intValue();
      core[i].imageCount = getSizeZ() * getSizeC() * getSizeT();
      core[i].littleEndian = endian == null ? false : !endian.booleanValue();
      core[i].rgb = false;
      core[i].interleaved = false;
      core[i].indexed = false;
      core[i].falseColor = true;
      core[i].pixelType = FormatTools.pixelTypeFromString(pixType);
      core[i].orderCertain = true;
    }
    setSeries(oldSeries);

    // populate assigned metadata store with the
    // contents of the internal OME-XML metadata object
    MetadataStore store = getMetadataStore();
    MetadataTools.populatePixels(store, this);
    service.convertMetadata(omexmlMeta, store);
  }

  // -- Helper class --

  class OMEXMLHandler extends BaseHandler {
    private StringBuffer xmlBuffer;
    private String currentQName;
    private Locator locator;

    public OMEXMLHandler() {
      xmlBuffer = new StringBuffer();
    }

    public void characters(char[] ch, int start, int length) {
      if (currentQName.indexOf("BinData") < 0) {
        xmlBuffer.append(new String(ch, start, length));
      }
    }

    public void endElement(String uri, String localName, String qName) {
      xmlBuffer.append("</");
      xmlBuffer.append(qName);
      xmlBuffer.append(">");
    }

    public void startElement(String ur, String localName, String qName,
      Attributes attributes)
    {
      currentQName = qName;

      if (qName.indexOf("BinData") == -1) {
        xmlBuffer.append("<");
        xmlBuffer.append(qName);
        for (int i=0; i<attributes.getLength(); i++) {
          String key = attributes.getQName(i);
          String value = attributes.getValue(i);
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
      else {
        binData.add(
          new BinData(locator.getLineNumber(), locator.getColumnNumber()));
        String compress = attributes.getValue("Compression");
        compression.add(compress == null ? "" : compress);

        xmlBuffer.append("<");
        xmlBuffer.append(qName);
        for (int i=0; i<attributes.getLength(); i++) {
          String key = attributes.getQName(i);
          String value = attributes.getValue(i);
          if (key.equals("Length")) value = "0";
          xmlBuffer.append(" ");
          xmlBuffer.append(key);
          xmlBuffer.append("=\"");
          xmlBuffer.append(value);
          xmlBuffer.append("\"");
        }
        xmlBuffer.append(">");
      }
    }

    public void endDocument() {
      omexml = xmlBuffer.toString();
    }

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
