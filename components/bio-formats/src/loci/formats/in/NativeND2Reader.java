//
// NativeND2Reader.java
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

import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import loci.common.RandomAccessInputStream;
import loci.common.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.ByteVector;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEG2000Codec;
import loci.formats.codec.ZlibCodec;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * NativeND2Reader is the file format reader for Nikon ND2 files.
 * The JAI ImageIO library is required to use this reader; it is available from
 * http://jai-imageio.dev.java.net. Note that JAI ImageIO is bundled with a
 * version of the JJ2000 library, so it is important that either:
 * (1) the JJ2000 jar file is *not* in the classpath; or
 * (2) the JAI jar file precedes JJ2000 in the classpath.
 *
 * Thanks to Tom Caswell for additions to the ND2 metadata parsing logic.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/NativeND2Reader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/NativeND2Reader.java">SVN</a></dd></dl>
 */
public class NativeND2Reader extends FormatReader {

  // -- Constants --

  public static final int ND2_MAGIC_BYTES = 0x6a502020;

  // -- Fields --

  /** Array of image offsets. */
  private long[][] offsets;

  /** Whether or not the pixel data is compressed using JPEG 2000. */
  private boolean isJPEG;

  /** Whether or not the pixel data is losslessly compressed. */
  private boolean isLossless;

  private Vector<Long> zs = new Vector<Long>();
  private Vector<Long> ts = new Vector<Long>();
  private Vector<Double> tsT = new Vector<Double>();

  private int numSeries;

  private float pixelSizeX, pixelSizeY, pixelSizeZ;
  private String voltage, mag, na, objectiveModel, immersion, correction;

  private Vector<String> channelNames, modality, binning;
  private Vector<Float> speed, gain, temperature, exposureTime;
  private Vector<Integer> exWave, emWave, power;
  private Vector<Hashtable<String, String>> rois;

  private String cameraModel;

  private int fieldIndex;

  // -- Constructor --

  /** Constructs a new ND2 reader. */
  public NativeND2Reader() {
    super("Nikon ND2", new String[] {"nd2", "jp2"});
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 8;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    stream.seek(4);
    return stream.readInt() == ND2_MAGIC_BYTES;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    in.seek(offsets[series][no]);

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int pixel = bpp * getRGBChannelCount();

    long maxFP = no == getImageCount() - 1 ?
      in.length() : offsets[series][no + 1];

    CodecOptions options = new CodecOptions();
    options.littleEndian = isLittleEndian();
    options.interleaved = isInterleaved();
    options.maxBytes = (int) maxFP;

    if (isJPEG) {
      byte[] tmp = new JPEG2000Codec().decompress(in, options);
      for (int row=y; row<h+y; row++) {
        System.arraycopy(tmp, pixel * row * getSizeX(), buf,
          pixel * w * (row - y), pixel * w);
      }
      System.arraycopy(tmp, 0, buf, 0, (int) Math.min(tmp.length, buf.length));
      tmp = null;
    }
    else if (isLossless) {
      // plane is compressed using ZLIB

      int effectiveX = getSizeX();
      if ((getSizeX() % 2) != 0) effectiveX++;
      byte[] t = new ZlibCodec().decompress(in, options);

      for (int row=0; row<h; row++) {
        int offset = (row + y) * effectiveX * pixel + x * pixel;
        if (offset + w * pixel <= t.length) {
          System.arraycopy(t, offset, buf, row * w * pixel, w * pixel);
        }
      }
    }
    else {
      // plane is not compressed
      readPlane(in, x, y, w, h, buf);
    }
    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();

    offsets = null;
    zs.clear();
    ts.clear();
    isJPEG = isLossless = false;
    numSeries = 0;
    tsT.clear();

    pixelSizeX = pixelSizeY = pixelSizeZ = 0f;
    voltage = mag = na = objectiveModel = immersion = correction = null;
    channelNames = null;
    binning = null;
    speed = null;
    gain = null;
    temperature = null;
    exposureTime = null;
    modality = null;
    exWave = null;
    emWave = null;
    power = null;
    cameraModel = null;
    fieldIndex = 0;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    channelNames = new Vector<String>();
    binning = new Vector<String>();
    speed = new Vector<Float>();
    gain = new Vector<Float>();
    temperature = new Vector<Float>();
    exposureTime = new Vector<Float>();
    modality = new Vector<String>();
    exWave = new Vector<Integer>();
    emWave = new Vector<Integer>();
    power = new Vector<Integer>();
    rois = new Vector<Hashtable<String, String>>();

    in = new RandomAccessInputStream(id);

    if (in.read() == -38 && in.read() == -50) {
      // newer version of ND2 - doesn't use JPEG2000

      isJPEG = false;
      in.seek(0);
      in.order(true);

      // assemble offsets to each block

      Vector<Long> imageOffsets = new Vector<Long>();
      Vector<int[]> imageLengths = new Vector<int[]>();
      Vector<Long> xmlOffsets = new Vector<Long>();
      Vector<int[]> xmlLengths = new Vector<int[]>();
      Vector<Long> customDataOffsets = new Vector<Long>();
      Vector<int[]> customDataLengths = new Vector<int[]>();

      while (in.getFilePointer() < in.length() && in.getFilePointer() >= 0) {
        int b1 = in.read();
        int b2 = in.read();
        int b3 = in.read();
        int b4 = in.read();
        while (b1 != -38 || b2 != -50 || b3 != -66 || b4 != 10) {
          if (in.getFilePointer() >= in.length() - 1) break;
          b1 = b2;
          b2 = b3;
          b3 = b4;
          b4 = in.read();
        }
        if (in.getFilePointer() >= in.length() - 1) break;

        int lenOne = in.readInt();
        int lenTwo = in.readInt();
        int len = lenOne + lenTwo;
        in.skipBytes(4);

        String blockType = in.readString(12);
        long fp = in.getFilePointer() - 12;
        int skip = len - 12 - lenOne * 2;
        if (skip <= 0) skip += lenOne * 2;
        in.skipBytes(skip);

        if (blockType.startsWith("ImageDataSeq")) {
          imageOffsets.add(new Long(fp));
          imageLengths.add(new int[] {lenOne, lenTwo});
        }
        else if (blockType.startsWith("Image")) {
          xmlOffsets.add(new Long(fp));
          xmlLengths.add(new int[] {lenOne, lenTwo});
        }
        else if (blockType.startsWith("CustomData|A")) {
          customDataOffsets.add(new Long(fp));
          customDataLengths.add(new int[] {lenOne, lenTwo});
        }
      }

      // parse XML blocks

      DefaultHandler handler = new ND2Handler();
      ByteVector xml = new ByteVector();

      for (int i=0; i<xmlOffsets.size(); i++) {
        long offset = xmlOffsets.get(i).longValue();
        int[] p = xmlLengths.get(i);
        int length = p[0] + p[1];

        byte[] b = new byte[length];
        in.seek(offset);
        in.read(b);

        // strip out invalid characters
        int off = 0;
        for (int j=0; j<length; j++) {
          char c = (char) b[j];
          if ((off == 0 && c == '!') || c == 0) off = j + 1;
          if (Character.isISOControl(c) || !Character.isDefined(c)) {
            b[j] = (byte) ' ';
          }
        }

        if (length - off >= 5 && b[off] == '<' && b[off + 1] == '?' &&
          b[off + 2] == 'x' && b[off + 3] == 'm' && b[off + 4] == 'l')
        {
          boolean endBracketFound = false;
          while (!endBracketFound) {
            if (b[off++] == '>') {
              endBracketFound = true;
            }
          }
          xml.add(b, off, b.length - off);
        }
      }

      String xmlString = new String(xml.toByteArray());
      xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ND2>" +
        xmlString + "</ND2>";

      core[0].dimensionOrder = "";

      XMLTools.parseXML(xmlString, handler);

      // rearrange image data offsets

      if (numSeries == 0) numSeries = 1;

      if (getSizeZ() == 0) {
        for (int i=0; i<getSeriesCount(); i++) {
          core[i].sizeZ = 1;
        }
      }
      if (getSizeT() == 0) {
        for (int i=0; i<getSeriesCount(); i++) {
          core[i].sizeT = 1;
        }
      }

      // calculate the image count
      for (int i=0; i<getSeriesCount(); i++) {
        core[i].imageCount = getSizeZ() * getSizeT() * getSizeC();
        if (imageOffsets.size() < core[i].imageCount) {
          core[i].imageCount /= getSizeC();
        }
        if (core[i].imageCount > imageOffsets.size() / getSeriesCount()) {
          if (core[i].imageCount == imageOffsets.size()) {
            CoreMetadata originalCore = core[0];
            core = new CoreMetadata[] {originalCore};
            numSeries = 1;
            break;
          }
          else {
            core[i].imageCount = imageOffsets.size() / getSeriesCount();
            core[i].sizeZ = 1;
            core[i].sizeT = core[i].imageCount;
          }
        }
      }

      if (numSeries * getImageCount() == 1 && imageOffsets.size() > 1) {
        for (int i=0; i<getSeriesCount(); i++) {
          core[i].imageCount = imageOffsets.size() / getSeriesCount();
          core[i].sizeZ = getImageCount();
          core[i].sizeT = 1;
        }
      }

      if (getSizeC() > 1 && getDimensionOrder().indexOf("C") == -1) {
        core[0].dimensionOrder = "C" + getDimensionOrder();
      }

      core[0].dimensionOrder = "XY" + getDimensionOrder();
      if (getDimensionOrder().indexOf("Z") == -1) core[0].dimensionOrder += "Z";
      if (getDimensionOrder().indexOf("C") == -1) core[0].dimensionOrder += "C";
      if (getDimensionOrder().indexOf("T") == -1) core[0].dimensionOrder += "T";

      offsets = new long[numSeries][getImageCount()];

      int[] lengths = new int[4];
      int nextChar = 2;
      for (int i=0; i<lengths.length; i++) {
        if (i == fieldIndex) lengths[i] = core.length;
        else {
          char axis = getDimensionOrder().charAt(nextChar++);
          if (axis == 'Z') lengths[i] = getSizeZ();
          else if (axis == 'C') lengths[i] = getSizeC();
          else if (axis == 'T') lengths[i] = getSizeT();
        }
      }
      int[] zctLengths = new int[4];
      System.arraycopy(lengths, 0, zctLengths, 0, lengths.length);
      zctLengths[fieldIndex] = 1;

      for (int i=0; i<imageOffsets.size(); i++) {
        long offset = imageOffsets.get(i).longValue();
        int[] p = imageLengths.get(i);
        int length = p[0] + p[1];

        in.seek(offset);
        byte[] b = new byte[length];
        in.read(b);

        StringBuffer sb = new StringBuffer();
        int pt = 13;
        while (b[pt] != '!') {
          sb.append((char) b[pt++]);
        }
        int ndx = Integer.parseInt(sb.toString());

        if (getSizeC() == 0) {
          int sizeC = length / (getSizeX() * getSizeY() *
            FormatTools.getBytesPerPixel(getPixelType()));
          for (int q=0; q<getSeriesCount(); q++) {
            core[q].sizeC = sizeC;
          }
        }

        int[] pos = FormatTools.rasterToPosition(lengths, ndx);
        int seriesIndex = pos[fieldIndex];
        pos[fieldIndex] = 0;
        int plane = FormatTools.positionToRaster(zctLengths, pos);

        if (seriesIndex < offsets.length && plane < offsets[seriesIndex].length)
        {
          offsets[seriesIndex][plane] = offset + p[0] + 8;
        }
        b = null;
      }

      Vector<long[]> tmpOffsets = new Vector<long[]>();
      for (int i=0; i<offsets.length; i++) {
        if (offsets[i][0] > 0) tmpOffsets.add(offsets[i]);
      }

      offsets = new long[tmpOffsets.size()][];
      for (int i=0; i<tmpOffsets.size(); i++) {
        offsets[i] = tmpOffsets.get(i);
      }

      if (offsets.length != getSeriesCount()) {
        int x = getSizeX();
        int y = getSizeY();
        int c = getSizeC();
        int pixelType = getPixelType();
        boolean rgb = isRGB();
        String order = getDimensionOrder();
        core = new CoreMetadata[offsets.length];
        for (int i=0; i<offsets.length; i++) {
          core[i] = new CoreMetadata();
          core[i].sizeX = x;
          core[i].sizeY = y;
          core[i].sizeC = c == 0 ? 1 : c;
          core[i].pixelType = pixelType;
          core[i].rgb = rgb;
          core[i].sizeZ = 1;
          core[i].dimensionOrder = order;

          int invalid = 0;
          for (int q=0; q<offsets[i].length; q++) {
            if (offsets[i][q] == 0) invalid++;
          }
          core[i].imageCount = offsets[i].length - invalid;
          core[i].sizeT = core[i].imageCount / (rgb ? 1 : core[i].sizeC);
          if (core[i].sizeT == 0) core[i].sizeT = 1;
        }
      }
      else {
        for (int i=0; i<getSeriesCount(); i++) {
          core[i].sizeX = getSizeX();
          core[i].sizeY = getSizeY();
          core[i].sizeC = getSizeC() == 0 ? 1 : getSizeC();
          core[i].sizeZ = getSizeZ() == 0 ? 1 : getSizeZ();
          core[i].sizeT = getSizeT() == 0 ? 1 : getSizeT();
          core[i].imageCount = getImageCount();
          core[i].pixelType = getPixelType();
          core[i].dimensionOrder = getDimensionOrder();
        }
      }

      for (int i=0; i<getSeriesCount(); i++) {
        core[i].rgb = getSizeC() > 1;
        core[i].littleEndian = true;
        core[i].interleaved = true;
        core[i].indexed = false;
        core[i].falseColor = false;
        core[i].metadataComplete = true;
        core[i].imageCount = core[i].sizeZ * core[i].sizeT;
        if (!core[i].rgb) core[i].imageCount *= core[i].sizeC;
      }

      // read first CustomData block

      if (customDataOffsets.size() > 0) {
        in.seek(customDataOffsets.get(0).longValue());
        int[] p = customDataLengths.get(0);
        int len = p[0] + p[1];

        int timestampBytes = imageOffsets.size() * 8;
        in.skipBytes(len - timestampBytes);

        // the acqtimecache is a undeliniated stream of doubles

        for (int series=0; series<getSeriesCount(); series++) {
          setSeries(series);
          for (int plane=0; plane<getImageCount(); plane++) {
            // timestamps are stored in ms; we want them in seconds
            double time = in.readDouble() / 1000;
            tsT.add(new Double(time));
            addSeriesMeta("timestamp " + plane, time);
          }
        }
        setSeries(0);
      }

      populateMetadataStore();
      return;
    }
    else in.seek(0);

    // older version of ND2 - uses JPEG 2000 compression

    isJPEG = true;

    status("Calculating image offsets");

    Vector<Long> vs = new Vector<Long>();

    long pos = in.getFilePointer();
    boolean lastBoxFound = false;
    int length = 0;
    int box = 0;

    // assemble offsets to each plane

    int x = 0, y = 0, c = 0, type = 0;

    while (!lastBoxFound) {
      pos = in.getFilePointer();
      length = in.readInt();
      long nextPos = pos + length;
      if (nextPos < 0 || nextPos >= in.length() || length == 0) {
        lastBoxFound = true;
      }
      box = in.readInt();
      pos = in.getFilePointer();
      length -= 8;

      if (box == 0x6a703263) {
        vs.add(new Long(pos));
      }
      else if (box == 0x6a703268) {
        in.skipBytes(4);
        String s = in.readString(4);
        if (s.equals("ihdr")) {
          y = in.readInt();
          x = in.readInt();
          c = in.readShort();
          type = in.readInt();
          if (type == 0xf070100 || type == 0xf070000) type = FormatTools.UINT16;
          else type = FormatTools.UINT8;
        }
      }
      if (!lastBoxFound && box != 0x6a703268) in.skipBytes(length);
    }

    status("Finding XML metadata");

    // read XML metadata from the end of the file

    in.seek(vs.get(vs.size() - 1).longValue());

    boolean found = false;
    long off = -1;
    byte[] buf = new byte[8192];
    while (!found && in.getFilePointer() < in.length()) {
      int read = 0;
      if (in.getFilePointer() == vs.get(vs.size() - 1).longValue()) {
        read = in.read(buf);
      }
      else {
        System.arraycopy(buf, buf.length - 10, buf, 0, 10);
        read = in.read(buf, 10, buf.length - 10);
      }

      if (read == buf.length) read -= 10;
      for (int i=0; i<read+9; i++) {
        if (buf[i] == (byte) 0xff && buf[i+1] == (byte) 0xd9) {
          found = true;
          off = in.getFilePointer() - (read + 10) + i;
          i = buf.length;
          break;
        }
      }
    }

    buf = null;

    status("Parsing XML");

    if (off > 0 && off < in.length() - 5 && (in.length() - off - 5) > 14) {
      in.seek(off + 4);

      StringBuffer sb = new StringBuffer();
      // stored XML doesn't have a root node - add one, so that we can parse
      // using SAX

      sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><NIKON>");

      String s = null;
      int blockLength = 0;

      while (in.getFilePointer() < in.length()) {
        blockLength = in.readShort();
        if (blockLength < 2) break;
        s = in.readString(blockLength - 2);
        s = s.replaceAll("<!--.+?>", ""); // remove comments
        int openBracket = s.indexOf("<");
        if (openBracket == -1) continue;
        int closedBracket = s.lastIndexOf(">") + 1;
        if (closedBracket < openBracket) continue;
        s = s.substring(openBracket, closedBracket).trim();
        if (s.indexOf("VCAL") == -1 && s.indexOf("jp2cLUNK") == -1) {
          sb.append(s);
        }
      }

      sb.append("</NIKON>");

      status("Finished assembling XML string");

      DefaultHandler handler = new ND2Handler();

      // strip out invalid characters
      int offset = 0;
      int len = sb.length();
      for (int i=0; i<len; i++) {
        char ch = sb.charAt(i);
        if (offset == 0 && ch == '!') offset = i + 1;

        if (Character.isISOControl(ch) || !Character.isDefined(ch)) {
          sb.setCharAt(i, ' ');
        }
      }

      core[0].dimensionOrder = "";

      String xml = sb.toString().substring(offset, len - offset);
      XMLTools.parseXML(xml, handler);
      xml = null;
    }

    status("Populating metadata");

    core[0].pixelType = FormatTools.UINT8;
    offsets = new long[1][2];
    offsets[0][0] = vs.get(0).longValue();
    if (offsets[0].length > 1 && vs.size() > 1) {
      offsets[0][1] = vs.get(1).longValue();
    }

    in.seek(offsets[0][0]);

    if (getSizeC() == 0) core[0].sizeC = 1;
    int numBands = c;
    c = numBands > 1 ? numBands : getSizeC();
    if (numBands == 1 && getImageCount() == 1) c = 1;
    for (int i=0; i<getSeriesCount(); i++) {
      core[i].sizeC = c;
      core[i].rgb = numBands > 1;
      core[i].pixelType = type;
    }

    if (isRGB() && getDimensionOrder().indexOf("C") == -1) {
      core[0].dimensionOrder = "C" + getDimensionOrder();
    }

    if (getDimensionOrder().indexOf("Z") == -1) core[0].dimensionOrder += "Z";
    if (getDimensionOrder().indexOf("C") == -1) core[0].dimensionOrder += "C";
    if (getDimensionOrder().indexOf("T") == -1) core[0].dimensionOrder += "T";
    core[0].dimensionOrder = "XY" + getDimensionOrder();

    if (getImageCount() == 0) {
      core[0].imageCount = vs.size();
      core[0].sizeZ = (int) Math.max(zs.size(), 1);
      core[0].sizeT = (int) Math.max(ts.size(), 1);
      int channels = isRGB() ? 1 : getSizeC();
      if (channels * getSizeZ() * getSizeT() != getImageCount()) {
        core[0].sizeZ = 1;
        core[0].sizeT = getImageCount() / channels;
        core[0].imageCount = getSizeZ() * getSizeT() * channels;
      }
    }

    if (getSizeZ() == 0) core[0].sizeZ = 1;
    if (getSizeT() == 0) core[0].sizeT = 1;

    for (int i=0; i<getSeriesCount(); i++) {
      core[i].sizeZ = getSizeZ();
      core[i].sizeT = getSizeT();
      core[i].imageCount = getSizeZ() * getSizeT() * (isRGB() ? 1 : getSizeC());
      core[i].dimensionOrder = getDimensionOrder();
      core[i].sizeX = x;
      core[i].sizeY = y;
      core[i].interleaved = false;
      core[i].littleEndian = false;
      core[i].metadataComplete = true;
    }

    int nplanes = getSizeZ() * getEffectiveSizeC();
    if (numSeries == 0) numSeries = 1;
    if (numSeries * nplanes * getSizeT() > vs.size()) {
      numSeries = vs.size() / (nplanes * getSizeT());
    }
    offsets = new long[numSeries][getImageCount()];

    for (int i=0; i<getSizeT(); i++) {
      for (int j=0; j<numSeries; j++) {
        for (int q=0; q<nplanes; q++) {
          offsets[j][i*nplanes + q] = vs.remove(0).longValue();
        }
      }
    }

    populateMetadataStore();
  }

  // -- Helper class --

  /** SAX handler for parsing XML. */
  class ND2Handler extends DefaultHandler {
    private String prefix = null;
    private String prevRuntype = null;

    public void endElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      if (qName.equals("CalibrationSeq") || qName.equals("MetadataSeq")) {
        prefix = null;
      }
    }

    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      String value = attributes.getValue("value");
      if (qName.equals("uiWidth")) {
        core[0].sizeX = Integer.parseInt(value);
      }
      else if (qName.equals("uiWidthBytes") || qName.equals("uiBpcInMemory")) {
        int div = qName.equals("uiWidthBytes") ? getSizeX() : 8;
        int bytes = Integer.parseInt(value) / div;

        switch (bytes) {
          case 2:
            core[0].pixelType = FormatTools.UINT16;
            break;
          case 4:
            core[0].pixelType = FormatTools.UINT32;
            break;
          default:
            core[0].pixelType = FormatTools.UINT8;
        }
        parseKeyAndValue(qName, value, prevRuntype);
      }
      else if (qName.startsWith("item_")) {
        int v = Integer.parseInt(qName.substring(qName.indexOf("_") + 1));
        if (v == numSeries) {
          fieldIndex = getDimensionOrder().length();
          numSeries++;
        }
      }
      else if (qName.equals("uiCompCount")) {
        int v = Integer.parseInt(value);
        core[0].sizeC = (int) Math.max(getSizeC(), v);
      }
      else if (qName.equals("uiHeight")) {
        core[0].sizeY = Integer.parseInt(value);
      }
      else if (qName.startsWith("TextInfo")) {
        parseKeyAndValue(qName, attributes.getValue("Text"), prevRuntype);
        parseKeyAndValue(qName, value, prevRuntype);
      }
      else if (qName.equals("dCompressionParam")) {
        isLossless = Integer.parseInt(value) > 0;
        parseKeyAndValue(qName, value, prevRuntype);
      }
      else if (qName.equals("CalibrationSeq") || qName.equals("MetadataSeq")) {
        prefix = qName + " " + attributes.getValue("_SEQUENCE_INDEX");
      }
      else if (qName.equals("HorizontalLine") || qName.equals("VerticalLine") ||
        qName.equals("Text"))
      {
        Hashtable<String, String> roi = new Hashtable<String, String>();
        roi.put("ROIType", qName);
        for (int q=0; q<attributes.getLength(); q++) {
          roi.put(attributes.getQName(q), attributes.getValue(q));
        }
        rois.add(roi);
      }
      else {
        StringBuffer sb = new StringBuffer();
        if (prefix != null) {
          sb.append(prefix);
          sb.append(" ");
        }
        sb.append(qName);
        parseKeyAndValue(sb.toString(), value, prevRuntype);
      }

      prevRuntype = attributes.getValue("runtype");
    }
  }

  // -- Helper methods --

  private void populateMetadataStore() {
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this, true);

    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrumentID, 0);

    // populate Image data
    for (int i=0; i<getSeriesCount(); i++) {
      store.setImageName("Series " + (i + 1), i);
      MetadataTools.setDefaultCreationDate(store, currentId, i);

      // link Instrument and Image
      store.setImageInstrumentRef(instrumentID, i);
    }

    // populate Dimensions data
    for (int i=0; i<getSeriesCount(); i++) {
      store.setDimensionsPhysicalSizeX(new Float(pixelSizeX), i, 0);
      store.setDimensionsPhysicalSizeY(new Float(pixelSizeY), i, 0);
      store.setDimensionsPhysicalSizeZ(new Float(pixelSizeZ), i, 0);
    }

    // populate PlaneTiming data
    for (int i=0; i<getSeriesCount(); i++) {
      if (tsT.size() > 0) {
        setSeries(i);
        for (int n=0; n<getImageCount(); n++) {
          int[] coords = getZCTCoords(n);
          int stampIndex = coords[2];
          if (tsT.size() == getImageCount()) stampIndex = n;
          float stamp = tsT.get(stampIndex).floatValue();
          store.setPlaneTimingDeltaT(new Float(stamp), i, 0, n);

          int index = i * getSizeC() + coords[1];
          if (index < exposureTime.size()) {
            store.setPlaneTimingExposureTime(exposureTime.get(index), i, 0, n);
          }
        }
      }
    }

    String detectorID = MetadataTools.createLSID("Detector", 0, 0);
    store.setDetectorID(detectorID, 0, 0);
    store.setDetectorModel(cameraModel, 0, 0);
    store.setDetectorType("Unknown", 0, 0);

    for (int i=0; i<getSeriesCount(); i++) {
      for (int c=0; c<getSizeC(); c++) {
        int index = i * getSizeC() + c;
        if (index < channelNames.size()) {
          store.setLogicalChannelName(channelNames.get(index), i, c);
        }
        if (index < modality.size()) {
          store.setLogicalChannelMode(modality.get(index), i, c);
        }
        if (index < emWave.size()) {
          store.setLogicalChannelEmWave(emWave.get(index), i, c);
        }
        if (index < exWave.size()) {
          store.setLogicalChannelExWave(exWave.get(index), i, c);
        }
        if (index < binning.size()) {
          store.setDetectorSettingsBinning(binning.get(index), i, c);
        }
        if (index < gain.size()) {
          store.setDetectorSettingsGain(gain.get(index), i, c);
        }
        if (index < speed.size()) {
          store.setDetectorSettingsReadOutRate(speed.get(index), i, c);
        }
        store.setDetectorSettingsDetector(detectorID, i, c);
      }
    }

    for (int i=0; i<getSeriesCount(); i++) {
      if (i * getSizeC() < temperature.size()) {
        Float temp = temperature.get(i * getSizeC());
        store.setImagingEnvironmentTemperature(temp, i);
      }
    }

    // populate DetectorSettings
    if (voltage != null) {
      store.setDetectorSettingsVoltage(new Float(voltage), 0, 0);
    }

    // populate Objective
    if (na != null) store.setObjectiveLensNA(new Float(na), 0, 0);
    if (mag != null) {
      store.setObjectiveCalibratedMagnification(new Float(mag), 0, 0);
    }
    if (objectiveModel != null) {
      store.setObjectiveModel(objectiveModel, 0, 0);
    }
    if (immersion == null) immersion = "Unknown";
    store.setObjectiveImmersion(immersion, 0, 0);
    if (correction == null || correction.length() == 0) correction = "Unknown";
    store.setObjectiveCorrection(correction, 0, 0);

    // link Objective to Image
    String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
    store.setObjectiveID(objectiveID, 0, 0);
    for (int i=0; i<getSeriesCount(); i++) {
      store.setObjectiveSettingsObjective(objectiveID, 0);
    }

    setSeries(0);

    // populate ROI data

    for (int r=0; r<rois.size(); r++) {
      Hashtable<String, String> roi = rois.get(r);
      String type = roi.get("ROIType");

      store.setShapeLocked(new Boolean(roi.get("locked")), 0, r, 0);
      store.setShapeStrokeWidth(new Integer(roi.get("line-width")), 0, r, 0);
      store.setShapeVisibility(new Boolean(roi.get("visible")), 0, r, 0);
      store.setShapeStrokeColor(roi.get("color"), 0, r, 0);

      if (type.equals("Text")) {
        store.setShapeFontFamily(roi.get("fFaceName"), 0, r, 0);
        store.setShapeFontSize(new Integer(roi.get("fHeight")), 0, r, 0);
        store.setShapeText(roi.get("eval-text"), 0, r, 0);
        store.setShapeFontWeight(roi.get("fWeight"), 0, r, 0);

        boolean italic = Integer.parseInt(roi.get("fItalic")) != 0;
        boolean underline = Integer.parseInt(roi.get("fUnderline")) != 0;
        boolean strikeOut = Integer.parseInt(roi.get("fStrikeOut")) != 0;
        store.setShapeFontStyle(italic ? "italic" : "normal", 0, r, 0);
        store.setShapeTextDecoration(underline ? "underline" : strikeOut ?
          "line-through" : "normal", 0, r, 0);

        String rectangle = roi.get("rectangle");
        String[] p = rectangle.split(",");
        double[] points = new double[p.length];
        for (int i=0; i<p.length; i++) {
          points[i] = Double.parseDouble(p[i]);
        }

        store.setRectX(p[0], 0, r, 0);
        store.setRectY(p[1], 0, r, 0);
        store.setRectWidth(String.valueOf(points[2] - points[0]), 0, r, 0);
        store.setRectHeight(String.valueOf(points[3] - points[1]), 0, r, 0);
      }
      else if (type.equals("HorizontalLine") || type.equals("VerticalLine")) {
        String segments = roi.get("segments");
        segments = segments.replaceAll("\\[", "");
        segments = segments.replaceAll("\\]", "");
        String[] points = segments.split("\\)");

        StringBuffer sb = new StringBuffer();
        for (int i=0; i<points.length; i++) {
          points[i] = points[i].substring(points[i].indexOf(":") + 1);
          sb.append(points[i]);
          if (i < points.length - 1) sb.append(" ");
        }
        store.setPolylinePoints(sb.toString(), 0, r, 0);
      }
    }
  }

  private void parseKeyAndValue(String key, String value, String runtype) {
    if (key == null || value == null) return;
    addGlobalMeta(key, value);
    if (key.endsWith("dCalibration")) {
      pixelSizeX = Float.parseFloat(value);
      pixelSizeY = pixelSizeX;
    }
    else if (key.endsWith("dZStep")) pixelSizeZ = Float.parseFloat(value);
    else if (key.endsWith("Gain")) gain.add(new Float(value));
    else if (key.endsWith("dLampVoltage")) voltage = value;
    else if (key.endsWith("dObjectiveMag") && mag == null) mag = value;
    else if (key.endsWith("dObjectiveNA")) na = value;
    else if (key.equals("sObjective") || key.equals("wsObjectiveName")) {
      String[] tokens = value.split(" ");
      int magIndex = -1;
      for (int i=0; i<tokens.length; i++) {
        if (tokens[i].indexOf("x") != -1) {
          magIndex = i;
          break;
        }
      }
      StringBuffer s = new StringBuffer();
      for (int i=0; i<magIndex; i++) {
        s.append(tokens[i]);
      }
      correction = s.toString();
      if (magIndex >= 0) {
        mag = tokens[magIndex].substring(0, tokens[magIndex].indexOf("x"));
      }
      if (magIndex + 1 < tokens.length) immersion = tokens[magIndex + 1];
    }
    else if (key.endsWith("dTimeMSec")) {
      long v = (long) Double.parseDouble(value);
      if (!ts.contains(new Long(v))) {
        ts.add(new Long(v));
        addGlobalMeta("number of timepoints", ts.size());
      }
    }
    else if (key.endsWith("dZPos")) {
      long v = (long) Double.parseDouble(value);
      if (!zs.contains(new Long(v))) {
        zs.add(new Long(v));
      }
    }
    else if (key.endsWith("uiCount")) {
      if (runtype != null) {
        if (runtype.endsWith("ZStackLoop")) {
          if (getSizeZ() == 0) {
            core[0].sizeZ = Integer.parseInt(value);
            core[0].dimensionOrder = "Z" + getDimensionOrder();
          }
        }
        else if (runtype.endsWith("TimeLoop")) {
          if (getSizeT() == 0) {
            core[0].sizeT = Integer.parseInt(value);
            core[0].dimensionOrder = "T" + getDimensionOrder();
          }
        }
      }
    }
    else if (key.equals("VirtualComponents")) {
      if (getSizeC() == 0) {
        core[0].sizeC = Integer.parseInt(value);
        core[0].dimensionOrder += "C" + getDimensionOrder();
      }
    }
    else if (key.startsWith("TextInfoItem") || key.endsWith("TextInfoItem")) {
      metadata.remove(key);
      value = value.replaceAll("&#x000d;&#x000a;", "\n");
      StringTokenizer tokens = new StringTokenizer(value, "\n");
      while (tokens.hasMoreTokens()) {
        String t = tokens.nextToken().trim();
        if (t.startsWith("Dimensions:")) {
          t = t.substring(11);
          StringTokenizer dims = new StringTokenizer(t, " x ");

          core[0].sizeZ = 1;
          core[0].sizeT = 1;
          core[0].sizeC = 1;

          while (dims.hasMoreTokens()) {
            String dim = dims.nextToken().trim();
            int v = Integer.parseInt(dim.replaceAll("\\D", ""));
            v = (int) Math.max(v, 1);
            if (dim.startsWith("XY")) {
              numSeries = v;
              if (numSeries > 1) {
                int x = getSizeX();
                int y = getSizeY();
                int z = getSizeZ();
                int tSize = getSizeT();
                int c = getSizeC();
                String order = getDimensionOrder();
                core = new CoreMetadata[numSeries];
                for (int i=0; i<numSeries; i++) {
                  core[i] = new CoreMetadata();
                  core[i].sizeX = x;
                  core[i].sizeY = y;
                  core[i].sizeZ = z == 0 ? 1 : z;
                  core[i].sizeC = c == 0 ? 1 : c;
                  core[i].sizeT = tSize == 0 ? 1 : tSize;
                  core[i].dimensionOrder = order;
                }
              }
            }
            else if (dim.startsWith("T")) core[0].sizeT = v;
            else if (dim.startsWith("Z")) core[0].sizeZ = v;
            else core[0].sizeC = v;
          }

          core[0].imageCount = getSizeZ() * getSizeC() * getSizeT();
        }
        else if (t.startsWith("Number of Picture Planes")) {
          core[0].sizeC = Integer.parseInt(t.replaceAll("\\D", ""));
        }
        else {
          String[] v = t.split(":");
          if (v.length == 2) {
            if (v[0].equals("Name")) {
              channelNames.add(v[1]);
            }
            else if (v[0].equals("Modality")) {
              modality.add(v[1]);
            }
            else if (v[0].equals("Camera Type")) {
              cameraModel = v[1];
            }
            else if (v[0].equals("Binning")) {
              binning.add(v[1]);
            }
            else if (v[0].equals("Readout Speed")) {
              int last = v[1].lastIndexOf(" ");
              if (last != -1) v[1] = v[1].substring(0, last);
              speed.add(new Float(v[1]));
            }
            else if (v[0].equals("Temperature")) {
              String temp = v[1].replaceAll("[\\D&&[^-.]]", "");
              temperature.add(new Float(temp));
            }
            else if (v[0].equals("Exposure")) {
              String[] s = v[1].trim().split(" ");
              try {
                float time = Float.parseFloat(s[0]);
                // TODO: check for other units
                if (s[1].equals("ms")) time /= 1000;
                exposureTime.add(new Float(time));
              }
              catch (NumberFormatException e) { }
            }
          }
          else if (v[0].startsWith("- Step")) {
            int space = v[0].indexOf(" ", v[0].indexOf("Step") + 1);
            int last = v[0].indexOf(" ", space + 1);
            if (last == -1) last = v[0].length();
            pixelSizeZ = Float.parseFloat(v[0].substring(space, last));
          }
          else if (v[0].equals("Line")) {
            String[] values = t.split(";");
            for (int q=0; q<values.length; q++) {
              int colon = values[q].indexOf(":");
              String nextKey = values[q].substring(0, colon).trim();
              String nextValue = values[q].substring(colon + 1).trim();
              if (nextKey.equals("Emission wavelength")) {
                emWave.add(new Integer(nextValue));
              }
              else if (nextKey.equals("Excitation wavelength")) {
                exWave.add(new Integer(nextValue));
              }
              else if (nextKey.equals("Power")) {
                power.add(new Integer((int) Float.parseFloat(nextValue)));
              }
            }
          }
        }
      }
    }
  }

}
