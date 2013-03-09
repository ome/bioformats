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
import loci.common.LogTools;
import loci.common.RandomAccessInputStream;
import loci.common.XMLTools;
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
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataStore;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * OMEXMLReader is the file format reader for OME-XML files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/OMEXMLReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/OMEXMLReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class OMEXMLReader extends FormatReader {

  // -- Constants --

  private static final String NO_OME_JAVA_MSG =
    "The Java OME-XML library is required to read OME-XML files. Please " +
    "obtain ome-xml.jar from http://loci.wisc.edu/ome/formats.html";

  // -- Static fields --

  private static boolean noOME = false;

  static {
    try {
      Class.forName("ome.xml.OMEXMLNode");
    }
    catch (Throwable t) {
      noOME = true;
      LogTools.traceDebug(t);
    }
  }

  // -- Fields --

  // compression value and offset for each BinData element
  private Vector<Long> binDataOffsets;
  private Vector<Long> binDataLengths;
  private Vector<String> compression;

  private String omexml;
  private boolean hasSPW = false;

  // -- Constructor --

  /** Constructs a new OME-XML reader. */
  public OMEXMLReader() {
    super("OME-XML", "ome");
    domains = FormatTools.ALL_DOMAINS;
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
      FormatTools.NON_HCS_DOMAINS;
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
    long length = binDataLengths.get(index).longValue();
    String compress = compression.get(index);

    in.seek(offset - 64);

    // offset is approximate, we will need to skip a few bytes
    boolean foundBinData = false;
    byte[] check = new byte[8192];
    int overlap = 14;
    int n = in.read(check, 0, overlap);

    while (!foundBinData) {
      int r = in.read(check, overlap, check.length - overlap);
      if (r <= 0) throw new IOException("Cannot read from input stream");
      n += r;
      String checkString = new String(check);
      int pos = 0;
      while (checkString.indexOf("BinData", pos) != -1 &&
        pos < checkString.length() && pos >= 0)
      {
        int idx = checkString.indexOf("BinData", pos) + 7;
        pos = idx + 1;
        boolean foundBeginning = false;
        int openBracket = idx;
        while (!foundBeginning && openBracket >= 1) {
          openBracket--;
          foundBeginning = checkString.charAt(openBracket) == '<';
        }
        if (checkString.charAt(openBracket + 1) == '/') continue;
        foundBinData = true;
        in.seek(in.getFilePointer() - n + idx);
        while (true) {
          r = in.read();
          if (r <= 0) {
            throw new IOException("EOF looking for terminating > character");
          }
          if (r == '>') break;
        }
        if (foundBinData) break;
      }
      if (!foundBinData) {
        System.arraycopy(check, check.length - overlap, check, 0, overlap);
        n = overlap;
      }
    }

    if (length < 0 && index + 1 < binDataOffsets.size()) {
      length = binDataOffsets.get(index + 1).longValue() - offset;
    }
    else if (length < 0) {
      length = in.length() - offset;
    }

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
      binDataLengths = null;
      omexml = null;
      hasSPW = false;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("OMEXMLReader.initFile(" + id + ")");
    if (noOME) throw new MissingLibraryException(NO_OME_JAVA_MSG);
    super.initFile(id);

    in = new RandomAccessInputStream(id);

    binDataOffsets = new Vector<Long>();
    binDataLengths = new Vector<Long>();
    compression = new Vector<String>();

    DefaultHandler handler = new OMEXMLHandler();
    XMLTools.parseXML(in, handler);

    if (binDataOffsets.size() == 0) {
      throw new FormatException("Pixel data not found");
    }

    status("Populating metadata");

    IMetadata omexmlMeta = MetadataTools.createOMEXMLMetadata(omexml);

    if (omexmlMeta == null) {
      throw new FormatException("ome-xml.jar is required to read " +
        "OME-XML files. Please download it from " +
        "http://loci.wisc.edu/ome/formats-library.html");
    }

    hasSPW = omexmlMeta.getPlateCount() > 0;

    Hashtable originalMetadata = MetadataTools.getOriginalMetadata(omexmlMeta);
    if (originalMetadata != null) metadata = originalMetadata;

    int numDatasets = omexmlMeta.getImageCount();

    core = new CoreMetadata[numDatasets];

    int oldSeries = getSeries();

    for (int i=0; i<numDatasets; i++) {
      setSeries(i);

      core[i] = new CoreMetadata();

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
      core[i].dimensionOrder = omexmlMeta.getPixelsDimensionOrder(i, 0);
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

      String type = pixType.toLowerCase();
      boolean signed = type.charAt(0) != 'u';
      if (type.endsWith("16")) {
        core[i].pixelType = signed ? FormatTools.INT16 : FormatTools.UINT16;
      }
      else if (type.endsWith("32")) {
        core[i].pixelType = signed ? FormatTools.INT32 : FormatTools.UINT32;
      }
      else if (type.equals("float")) {
        core[i].pixelType = FormatTools.FLOAT;
      }
      else {
        core[i].pixelType = signed ? FormatTools.INT8 : FormatTools.UINT8;
      }
      core[i].orderCertain = true;
    }
    setSeries(oldSeries);

    // populate assigned metadata store with the
    // contents of the internal OME-XML metadata object
    MetadataStore store = getMetadataStore();

    MetadataTools.ensureValidOMEXML(omexmlMeta);
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
