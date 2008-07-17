//
// OMEXMLReader.java
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
import java.util.*;
import javax.xml.parsers.*;
import loci.formats.*;
import loci.formats.codec.*;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * OMEXMLReader is the file format reader for OME-XML files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/OMEXMLReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/OMEXMLReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class OMEXMLReader extends FormatReader {

  // -- Constants --

  private static final String NO_OME_JAVA_MSG =
    "The Java OME-XML library is required to read OME-XML files. Please " +
    "obtain ome-java.jar from http://loci.wisc.edu/ome/formats.html";

  private static final SAXParserFactory SAX_FACTORY =
    SAXParserFactory.newInstance();

  // -- Static fields --

  private static boolean noOME = false;

  static {
    try {
      Class.forName("ome.xml.OMEXMLNode");
    }
    catch (Throwable t) {
      noOME = true;
      if (debug) LogTools.trace(t);
    }
  }

  // -- Fields --

  // compression value and offset for each BinData element
  private Vector binDataOffsets;
  private Vector binDataLengths;
  private Vector compression;

  private String omexml;

  // -- Constructor --

  /** Constructs a new OME-XML reader. */
  public OMEXMLReader() { super("OME-XML", "ome"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    String xml = new String(block);
    return xml.startsWith("<?xml") && xml.indexOf("<OME") >= 0;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length, w, h);

    int index = no;
    for (int i=0; i<series; i++) {
      index += core.imageCount[series];
    }

    long offset = ((Long) binDataOffsets.get(index)).longValue();
    long length = ((Long) binDataLengths.get(index)).longValue();
    String compress = (String) compression.get(index);

    in.seek(offset - 64);

    // offset is approximate, we will need to skip a few bytes
    boolean foundBinData = false;
    byte[] check = new byte[8192];
    int overlap = 14;
    int n = in.read(check, 0, overlap);

    while (!foundBinData) {
      n += in.read(check, overlap, check.length - overlap);
      String checkString = new String(check);
      if (checkString.indexOf("<Bin") != -1) {
        int idx = checkString.indexOf("<Bin") + 4;
        foundBinData = true;
        in.seek(in.getFilePointer() - n + idx);
        while (in.read() != '>');
      }
      else {
        System.arraycopy(check, check.length - overlap, check, 0, overlap);
        n = overlap;
      }
    }

    if (length < 0 && index + 1 < binDataOffsets.size()) {
      length = ((Long) binDataOffsets.get(index + 1)).longValue() - offset;
    }
    else if (length < 0) {
      length = in.length() - offset;
    }

    String data = in.readString((int) length * 2);

    // retrieve the compressed pixel data

    int dataEnd = data.indexOf("<");
    if (dataEnd != -1) data = data.substring(0, dataEnd);

    Base64Codec e = new Base64Codec();
    byte[] pixels = e.base64Decode(data);
    data = null;

    int planeSize =
      getSizeX() * getSizeY() * FormatTools.getBytesPerPixel(getPixelType());

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
      pixels = new ZlibCodec().decompress(pixels, null);
    }

    int depth = FormatTools.getBytesPerPixel(getPixelType());
    for (int row=0; row<h; row++) {
      int off = (row + y) * getSizeX() * depth + x * depth;
      System.arraycopy(pixels, off, buf, row * w * depth, w * depth);
    }

    pixels = null;

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    compression = null;
    binDataOffsets = null;
    binDataLengths = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("OMEXMLReader.initFile(" + id + ")");
    if (noOME) throw new FormatException(NO_OME_JAVA_MSG);
    super.initFile(id);

    in = new RandomAccessStream(id);

    binDataOffsets = new Vector();
    binDataLengths = new Vector();
    compression = new Vector();

    OMEXMLHandler handler = new OMEXMLHandler();
    try {
      SAXParser saxParser = SAX_FACTORY.newSAXParser();
      saxParser.parse(in, handler);
    }
    catch (ParserConfigurationException exc) {
      throw new FormatException(exc);
    }
    catch (SAXException exc) {
      throw new FormatException(exc);
    }

    if (binDataOffsets.size() == 0) {
      throw new FormatException("Pixel data not found");
    }

    status("Populating metadata");

    MetadataRetrieve omexmlMeta = (MetadataRetrieve)
      MetadataTools.createOMEXMLMetadata(omexml);

    int numDatasets = omexmlMeta.getImageCount();

    core = new CoreMetadata(numDatasets);

    int oldSeries = getSeries();

    for (int i=0; i<numDatasets; i++) {
      setSeries(i);

      Integer w = omexmlMeta.getPixelsSizeX(i, 0);
      Integer h = omexmlMeta.getPixelsSizeY(i, 0);
      Integer t = omexmlMeta.getPixelsSizeT(i, 0);
      Integer z = omexmlMeta.getPixelsSizeZ(i, 0);
      Integer c = omexmlMeta.getPixelsSizeC(i, 0);
      if (w == null || h == null || t == null || z == null | c == null) {
        throw new FormatException("Image dimensions not found");
      }
      Boolean endian = omexmlMeta.getPixelsBigEndian(i, 0);
      String pixType = omexmlMeta.getPixelsPixelType(i, 0);
      core.currentOrder[i] = omexmlMeta.getPixelsDimensionOrder(i, 0);
      core.sizeX[i] = w.intValue();
      core.sizeY[i] = h.intValue();
      core.sizeT[i] = t.intValue();
      core.sizeZ[i] = z.intValue();
      core.sizeC[i] = c.intValue();
      core.imageCount[i] = getSizeZ() * getSizeC() * getSizeT();
      core.littleEndian[i] = endian == null ? false : !endian.booleanValue();
      core.rgb[i] = false;
      core.interleaved[i] = false;
      core.indexed[i] = false;
      core.falseColor[i] = false;

      String type = pixType.toLowerCase();
      boolean signed = type.charAt(0) != 'u';
      if (type.endsWith("16")) {
        core.pixelType[i] = signed ? FormatTools.INT16 : FormatTools.UINT16;
      }
      else if (type.endsWith("32")) {
        core.pixelType[i] = signed ? FormatTools.INT32 : FormatTools.UINT32;
      }
      else if (type.equals("float")) {
        core.pixelType[i] = FormatTools.FLOAT;
      }
      else {
        core.pixelType[i] = signed ? FormatTools.INT8 : FormatTools.UINT8;
      }
    }
    setSeries(oldSeries);
    Arrays.fill(core.orderCertain, true);

    // populate assigned metadata store with the
    // contents of the internal OME-XML metadata object
    MetadataStore store = getMetadataStore();

    MetadataTools.convertMetadata(omexmlMeta, store);
  }

  // -- Helper class --

  class OMEXMLHandler extends DefaultHandler {
    private StringBuffer xmlBuffer;
    private long nextBinDataOffset;
    private String currentQName;
    private boolean hadCharData;
    private int binDataChars;

    public OMEXMLHandler() {
      xmlBuffer = new StringBuffer();
      nextBinDataOffset = 0;
    }

    public void characters(char[] ch, int start, int length) {
      if (currentQName.indexOf("BinData") != -1) {
        binDataChars += length;
      }
      nextBinDataOffset += length;
      hadCharData = true;
    }

    public void endElement(String uri, String localName, String qName) {
      if (qName.indexOf("BinData") == -1) {
        xmlBuffer.append("</");
        xmlBuffer.append(qName);
        xmlBuffer.append(">");
      }
      else {
        binDataOffsets.add(new Long(nextBinDataOffset - binDataChars));
      }

      nextBinDataOffset += 2;
      if (!qName.equals(currentQName) || hadCharData) {
        nextBinDataOffset += qName.length();
      }
    }

    public void ignorableWhitespace(char[] ch, int start, int length) {
      nextBinDataOffset += length;
    }

    public void startElement(String ur, String localName, String qName,
      Attributes attributes)
    {
      hadCharData = false;
      currentQName = qName;

      if (qName.indexOf("BinData") == -1) {
        xmlBuffer.append("<");
        xmlBuffer.append(qName);
        for (int i=0; i<attributes.getLength(); i++) {
          String key = attributes.getQName(i);
          String value = attributes.getValue(i);
          xmlBuffer.append(" ");
          xmlBuffer.append(key);
          xmlBuffer.append("=\"");
          xmlBuffer.append(value);
          xmlBuffer.append("\"");
        }
        xmlBuffer.append(">");
      }
      else {
        String length = attributes.getValue("Length");
        if (length == null) {
          binDataLengths.add(new Long(-1));
        }
        else binDataLengths.add(new Long(length));
        String compress = attributes.getValue("Compression");
        compression.add(compress == null ? "" : compress);
        binDataChars = 0;
      }

      nextBinDataOffset += 2 + qName.length() + 4*attributes.getLength();
      for (int i=0; i<attributes.getLength(); i++) {
        nextBinDataOffset += attributes.getQName(i).length();
        nextBinDataOffset += attributes.getValue(i).length();
      }
    }

    public void endDocument() {
      omexml = xmlBuffer.toString();
    }

  }

}
