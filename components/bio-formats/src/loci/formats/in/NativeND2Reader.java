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
import java.util.ArrayList;
import java.util.Hashtable;

import loci.common.ByteArrayHandle;
import loci.common.Constants;
import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.ImageTools;
import loci.formats.MetadataTools;
import loci.formats.codec.Codec;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEG2000Codec;
import loci.formats.codec.ZlibCodec;
import loci.formats.meta.MetadataStore;

import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;

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
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/NativeND2Reader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/NativeND2Reader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class NativeND2Reader extends FormatReader {

  // -- Constants --

  public static final long ND2_MAGIC_BYTES_1 = 0xdacebe0aL;
  public static final long ND2_MAGIC_BYTES_2 = 0x6a502020L;

  // -- Fields --

  /** Array of image offsets. */
  private long[][] offsets;

  /** Whether or not the pixel data is compressed using JPEG 2000. */
  private boolean isJPEG;

  /** Codec to use when decompressing pixel data. */
  private Codec codec;

  /** Whether or not the pixel data is losslessly compressed. */
  private boolean isLossless;

  private ArrayList<Double> tsT = new ArrayList<Double>();

  private int fieldIndex;

  private long xOffset, yOffset, zOffset;

  private ArrayList<Double> posX;
  private ArrayList<Double> posY;
  private ArrayList<Double> posZ;

  private Hashtable<String, Integer> channelColors;
  private boolean split = false;
  private int lastChannel = 0;
  private int[] colors;

  private int nXFields;

  // -- Constructor --

  /** Constructs a new ND2 reader. */
  public NativeND2Reader() {
    super("Nikon ND2", new String[] {"nd2", "jp2"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 8;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    long magic1 = stream.readInt() & 0xffffffffL;
    long magic2 = stream.readInt() & 0xffffffffL;
    return magic1 == ND2_MAGIC_BYTES_1 || magic2 == ND2_MAGIC_BYTES_2;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() {
    if (FormatTools.getBytesPerPixel(getPixelType()) != 1 ||
      !isIndexed() || lastChannel < 0 || lastChannel >= colors.length)
    {
      return null;
    }

    int color = colors[lastChannel];
    if (color == 0) return null;

    byte[][] lut = new byte[3][256];

    int index = -1;
    if (color > 0 && color < 256) index = 0;
    else if (color >= 256 && color < 65280) index = 1;
    else if (color > 65280 && color <= 16711680) index = 2;

    for (int i=0; i<256; i++) {
      if (index == -1) {
        lut[0][i] = (byte) i;
        lut[1][i] = (byte) i;
        lut[2][i] = (byte) i;
      }
      else {
        lut[index][i] = (byte) i;
      }
    }

    return lut;
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() {
    if (FormatTools.getBytesPerPixel(getPixelType()) != 2 ||
      !isIndexed() || lastChannel < 0 || lastChannel >= colors.length)
    {
      return null;
    }

    int color = colors[lastChannel];
    if (color == 0) return null;

    short[][] lut = new short[3][65536];

    int index = -1;
    if (color > 0 && color < 256) index = 0;
    else if (color >= 256 && color <= 65280) index = 1;
    else if (color > 65280 && color <= 16711680) index = 2;

    for (int i=0; i<65536; i++) {
      if (index == -1) {
        lut[0][i] = (short) i;
        lut[1][i] = (short) i;
        lut[2][i] = (short) i;
      }
      else {
        lut[index][i] = (short) i;
      }
    }

    return lut;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    lastChannel = split ? no % getSizeC() : 0;
    int planeIndex = split ? no / getSizeC() : no;
    in.seek(offsets[series][planeIndex]);

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int pixel = bpp * getRGBChannelCount();
    if (split) pixel *= getSizeC();

    int totalPlanes = split ? getImageCount() / getSizeC() : getImageCount();

    long maxFP = planeIndex == totalPlanes - 1 ?
      in.length() : offsets[series][planeIndex + 1];

    CodecOptions options = new CodecOptions();
    options.littleEndian = isLittleEndian();
    options.interleaved = isInterleaved();
    options.maxBytes = (int) maxFP;

    int scanlinePad = isJPEG ? 0 : getSizeX() % 2;
    if (scanlinePad == 1) {
      if (split && !isLossless && (nXFields % 2) != 0) {
        scanlinePad = 0;
      }
    }

    if (isJPEG || isLossless) {
      if (codec == null) codec = createCodec(isJPEG);
      byte[] t = null;
      try {
        t = codec.decompress(in, options);
      }
      catch (IOException e) {
        LOGGER.debug("Failed to decompress; plane may be corrupt", e);
        return buf;
      }
      if ((getSizeX() + scanlinePad) * getSizeY() * pixel > t.length) {
        // one padding pixel per row total, instead of one padding pixel
        // per channel per row
        int rowLength = getSizeX() * pixel + scanlinePad * bpp;
        int destLength = w * pixel;

        int p = rowLength * y + x * pixel;
        byte[] pix = new byte[destLength * h];
        for (int row=0; row<h; row++) {
          if (p + destLength <= t.length) {
            System.arraycopy(t, p, pix, row * destLength, destLength);
            int skip = pixel * (getSizeX() - w - x) + scanlinePad * bpp;
            p += destLength + skip;
          }
          else {
            break;
          }
        }

        if (split) {
          pix = ImageTools.splitChannels(pix, lastChannel, getEffectiveSizeC(),
            bpp, false, true);
        }
        System.arraycopy(pix, 0, buf, 0, pix.length);
      }
      else {
        copyPixels(x, y, w, h, bpp, scanlinePad, t, buf, split);
      }
      t = null;
    }
    else if (split && (getSizeC() <= 4 || scanlinePad == 0) && nXFields <= 1) {
      byte[] pix = new byte[(getSizeX() + scanlinePad) * getSizeY() * pixel];
      in.read(pix);
      copyPixels(x, y, w, h, bpp, scanlinePad, pix, buf, split);
      pix = null;
    }
    else if (split) {
      // one padding pixel per row total, instead of one padding pixel
      // per channel per row
      int rowLength = getSizeX() * pixel + scanlinePad * bpp;
      int destLength = w * pixel;

      in.skipBytes(rowLength * y + x * pixel);
      byte[] pix = new byte[destLength * h];
      for (int row=0; row<h; row++) {
        in.read(pix, row * destLength, destLength);
        in.skipBytes(pixel * (getSizeX() - w - x) + scanlinePad * bpp);
      }

      pix = ImageTools.splitChannels(pix, lastChannel, getEffectiveSizeC(),
        bpp, false, true);
      System.arraycopy(pix, 0, buf, 0, pix.length);
    }
    else {
      // plane is not compressed
      readPlane(in, x, y, w, h, scanlinePad, buf);
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      offsets = null;
      isJPEG = isLossless = false;
      codec = null;
      tsT.clear();

      fieldIndex = 0;
      xOffset = yOffset = zOffset = 0;
      posX = posY = posZ = null;
      channelColors = null;
      split = false;
      nXFields = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    in = new RandomAccessInputStream(id);

    channelColors = new Hashtable<String, Integer>();

    if (in.read() == -38 && in.read() == -50) {
      // newer version of ND2 - doesn't use JPEG2000
      LOGGER.info("Searching for blocks");

      isJPEG = false;
      in.seek(0);
      in.order(true);

      // assemble offsets to each block

      ArrayList<String> imageNames = new ArrayList<String>();
      ArrayList<Long> imageOffsets = new ArrayList<Long>();
      ArrayList<int[]> imageLengths = new ArrayList<int[]>();
      ArrayList<Long> customDataOffsets = new ArrayList<Long>();
      ArrayList<int[]> customDataLengths = new ArrayList<int[]>();

      ByteArrayHandle xml = new ByteArrayHandle();
      StringBuffer name = new StringBuffer();

      // search for blocks
      byte[] sigBytes = {-38, -50, -66, 10}; // 0xDACEBE0A
      while (in.getFilePointer() < in.length() - 1 && in.getFilePointer() >= 0)
      {
        byte[] buf = new byte[1024];
        int foundIndex = -1;
        in.read(buf, 0, sigBytes.length);
        while (foundIndex == -1 && in.getFilePointer() < in.length()) {
          int n = in.read(buf, sigBytes.length, buf.length - sigBytes.length);
          for (int i=0; i<buf.length-sigBytes.length; i++) {
            for (int j=0; j<sigBytes.length; j++) {
              if (buf[i + j] != sigBytes[j]) break;
              if (j == sigBytes.length - 1) foundIndex = i;
            }
            if (foundIndex != -1) break;
          }
          if (foundIndex == -1) {
            System.arraycopy(buf, buf.length - sigBytes.length - 1,
              buf, 0, sigBytes.length);
          }
          else in.seek(in.getFilePointer() - n + foundIndex);
        }
        if (in.getFilePointer() >= in.length() || foundIndex == -1) {
          break;
        }

        if (in.getFilePointer() > in.length() - 24) break;

        int lenOne = in.readInt();
        int lenTwo = in.readInt();
        int len = lenOne + lenTwo;
        in.skipBytes(4);

        long fp = in.getFilePointer();
        String blockType = in.readString(12);

        int percent = (int) (100 * fp / in.length());
        LOGGER.info("Parsing block '{}' {}%", blockType, percent);

        int skip = len - 12 - lenOne * 2;
        if (skip <= 0) skip += lenOne * 2;

        if (blockType.startsWith("ImageDataSeq")) {
          imageOffsets.add(new Long(fp));
          imageLengths.add(new int[] {lenOne, lenTwo});
          char b = (char) in.readByte();
          while (b != '!') {
            name.append(b);
            b = (char) in.readByte();
          }
          imageNames.add(name.toString());
          name = name.delete(0, name.length());
        }
        else if (blockType.startsWith("Image") ||
          blockType.startsWith("CustomDataVa"))
        {
          if (blockType.equals("ImageAttribu") && imageOffsets.size() > 0) {
            in.skipBytes(6);
            long endFP = in.getFilePointer() + lenOne + lenTwo - 18;
            while (in.read() == 0);

            boolean canBeLossless = true;

            while (in.getFilePointer() < endFP) {
              int nameLen = in.read();
              long start = in.getFilePointer();
              String attributeName =
                DataTools.stripString(in.readString(nameLen * 2));
              if (attributeName.startsWith("xml ") ||
                attributeName.startsWith("l version"))
              {
                if (attributeName.startsWith("xml ")) {
                  in.seek(start - 2);
                }
                else {
                  in.seek(start - 4);
                }
                attributeName = in.readCString();
                String xmlString = XMLTools.sanitizeXML(attributeName.trim());
                xmlString =
                  xmlString.substring(0, xmlString.lastIndexOf(">") + 1);
                try {
                  ND2Handler handler = new ND2Handler(core);
                  XMLTools.parseXML(xmlString, handler);
                }
                catch (IOException e) {
                  LOGGER.debug("Could not parse XML", e);
                }
                in.seek(in.getFilePointer() - 8);
                break;
              }

              int valueOrLength = in.readInt();

              addGlobalMeta(attributeName, valueOrLength);

              if (attributeName.equals("uiWidth")) {
                core[0].sizeX = valueOrLength;
              }
              else if (attributeName.equals("uiHeight")) {
                core[0].sizeY = valueOrLength;
              }
              else if (attributeName.equals("uiComp")) {
                core[0].sizeC = valueOrLength;
              }
              else if (attributeName.equals("uiBpcInMemory")) {
                core[0].pixelType = FormatTools.pixelTypeFromBytes(
                  valueOrLength / 8, false, false);
              }
              else if (attributeName.equals("dCompressionParam")) {
                isLossless = valueOrLength >= 0;
              }
              else if (attributeName.equals("eCompression")) {
                canBeLossless = valueOrLength <= 0;
              }
              else if (attributeName.equals("SLxImageAttributes")) {
                int toSkip = valueOrLength - 5;
                if ((toSkip % 2) == 1) {
                  toSkip++;
                }
                in.skipBytes(toSkip);
              }
              in.skipBytes(1);
            }

            isLossless = isLossless && canBeLossless;
          }
          else {
            int length = lenOne + lenTwo - 12;
            byte[] b = new byte[length];
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
              xml.write(b, off, b.length - off);
            }
          }
          skip = 0;
        }
        else if (getMetadataOptions().getMetadataLevel() !=
          MetadataLevel.MINIMUM)
        {
          if (blockType.startsWith("CustomData|A")) {
            customDataOffsets.add(new Long(fp));
            customDataLengths.add(new int[] {lenOne, lenTwo});
          }
          else if (blockType.startsWith("CustomData|Z")) {
            int nDoubles = (lenOne + lenTwo) / 8;
            zOffset = fp + 8 * (nDoubles - imageOffsets.size());
          }
          else if (blockType.startsWith("CustomData|X")) {
            int nDoubles = (lenOne + lenTwo) / 8;
            xOffset = fp + 8 * (nDoubles - imageOffsets.size());
          }
          else if (blockType.startsWith("CustomData|Y")) {
            int nDoubles = (lenOne + lenTwo) / 8;
            yOffset = fp + 8 * (nDoubles - imageOffsets.size());
          }
        }
        in.skipBytes(skip);
      }

      // parse XML blocks

      String xmlString =
        new String(xml.getBytes(), 0, (int) xml.length(), Constants.ENCODING);
      xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ND2>" +
        xmlString + "</ND2>";
      xmlString = XMLTools.sanitizeXML(xmlString);

      core[0].dimensionOrder = "";

      ND2Handler handler = new ND2Handler(core);
      XMLTools.parseXML(xmlString, handler);

      channelColors = handler.getChannelColors();
      if (!isLossless) {
        isLossless = handler.isLossless();
      }
      fieldIndex = handler.getFieldIndex();
      core = handler.getCoreMetadata();
      Hashtable<String, Object> globalMetadata = handler.getMetadata();
      nXFields = 0;
      for (String key : globalMetadata.keySet()) {
        addGlobalMeta(key, globalMetadata.get(key));
        if (key.equals("iXFields")) {
          nXFields = Integer.parseInt(globalMetadata.get(key).toString());
        }
        else if (key.equals("ChannelCount")) {
          for (int i=0; i<getSeriesCount(); i++) {
            if (core[i].sizeC == 0) {
              core[i].sizeC =
                Integer.parseInt(globalMetadata.get(key).toString());
              if (core[i].sizeC > 1) {
                core[i].rgb = true;
              }
            }
          }
        }
        else if (key.equals("uiBpcInMemory")) {
          int bpc = Integer.parseInt(globalMetadata.get(key).toString());
          core[0].pixelType = FormatTools.pixelTypeFromBytes(
            bpc / 8, false, false);
        }
      }

      int numSeries = core.length;

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
      if (getSizeC() == 0) {
        for (int i=0; i<getSeriesCount(); i++) {
          core[i].sizeC = 1;
        }
      }

      // make sure the channel count is reasonable
      // sometimes the XML will indicate that there are multiple channels,
      // when in fact there is only one channel

      long firstOffset = imageOffsets.get(0);
      long secondOffset =
        imageOffsets.size() > 1 ? imageOffsets.get(1) : in.length();
      long availableBytes = secondOffset - firstOffset;

      // make sure that we have the compression setting correct
      // it's not always easy to tell from the metadata
      isLossless = true;

      long fp = in.getFilePointer();
      int[] firstLengths = imageLengths.get(0);
      in.seek(firstOffset + firstLengths[0] + 8);

      if (codec == null) codec = createCodec(false);
      try {
        CodecOptions options = new CodecOptions();
        options.littleEndian = isLittleEndian();
        options.interleaved = true;
        options.maxBytes = (int) secondOffset;
        byte[] t = codec.decompress(in, options);

        if (t.length == 2 * getSizeX() * getSizeY() &&
          getPixelType() == FormatTools.INT8)
        {
          core[0].pixelType = FormatTools.UINT16;
        }
        availableBytes = t.length;
      }
      catch (IOException e) {
        isLossless = false;
      }

      in.seek(fp);

      int planeSize = getSizeX() * getSizeY() * getSizeC() *
        FormatTools.getBytesPerPixel(getPixelType());

      if (availableBytes < planeSize) {
        LOGGER.debug("Correcting SizeC: was {}", getSizeC());
        LOGGER.debug("plane size = {}", planeSize);
        LOGGER.debug("available bytes = {}", availableBytes);
        if (isLossless) {
          planeSize *= 2;
        }
        core[0].sizeC = (int) (availableBytes / (planeSize / getSizeC()));
        if (getSizeC() == 0) {
          core[0].sizeC = 1;
        }
      }
      else if (availableBytes > planeSize * 3) {
        core[0].sizeC = 3;
        core[0].rgb = true;
      }
      else if (availableBytes >= planeSize * 2 &&
        getPixelType() == FormatTools.INT8)
      {
        core[0].pixelType = FormatTools.UINT16;
      }

      if (getSizeT() == imageOffsets.size() && getSeriesCount() > 1) {
        CoreMetadata firstCore = core[0];
        core = new CoreMetadata[] {firstCore};
      }

      // calculate the image count
      for (int i=0; i<getSeriesCount(); i++) {
        core[i].imageCount = getSizeZ() * getSizeT() * getSizeC();
        if (imageOffsets.size() / getSeriesCount() < core[i].imageCount) {
          core[i].imageCount /= getSizeC();
        }
        if (core[i].imageCount > imageOffsets.size() / getSeriesCount()) {
          if (core[i].imageCount == imageOffsets.size()) {
            CoreMetadata originalCore = core[0];
            core = new CoreMetadata[] {originalCore};
            numSeries = 1;
            break;
          }
          else if (imageOffsets.size() % core[i].sizeT == 0) {
            core[i].imageCount = imageOffsets.size() / getSeriesCount();
            core[i].sizeZ = core[i].imageCount / core[i].sizeT;
            core[i].dimensionOrder = "CZT";
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

      if (getDimensionOrder().equals("T")) {
        fieldIndex = 0;
      }
      else if (getDimensionOrder().equals("ZT") && fieldIndex == 2) {
        fieldIndex--;
      }

      if (getSizeC() > 1 && getDimensionOrder().indexOf("C") == -1) {
        core[0].dimensionOrder = "C" + getDimensionOrder();
        fieldIndex++;
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
          else if (axis == 'C') lengths[i] = 1;
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

        if (getSizeC() == 0) {
          int sizeC = length / (getSizeX() * getSizeY() *
            FormatTools.getBytesPerPixel(getPixelType()));
          for (int q=0; q<getSeriesCount(); q++) {
            core[q].sizeC = sizeC;
          }
        }

        String imageName = imageNames.get(i);
        int ndx = Integer.parseInt(imageName.replaceAll("\\D", ""));

        int[] pos = FormatTools.rasterToPosition(lengths, ndx);
        int seriesIndex = pos[fieldIndex];
        pos[fieldIndex] = 0;
        int plane = FormatTools.positionToRaster(zctLengths, pos);

        if (seriesIndex < offsets.length && plane < offsets[seriesIndex].length)
        {
          offsets[seriesIndex][plane] = offset + p[0] + 8;
        }
      }

      ArrayList<long[]> tmpOffsets = new ArrayList<long[]>();
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
        int bitsPerPixel = getBitsPerPixel();
        boolean rgb = isRGB();
        String order = getDimensionOrder();
        core = new CoreMetadata[offsets.length];
        for (int i=0; i<offsets.length; i++) {
          core[i] = new CoreMetadata();
          core[i].sizeX = x;
          core[i].sizeY = y;
          core[i].sizeC = c == 0 ? 1 : c;
          core[i].pixelType = pixelType;
          core[i].bitsPerPixel = bitsPerPixel;
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
          core[i].bitsPerPixel = getBitsPerPixel();
          core[i].dimensionOrder = getDimensionOrder();
        }
      }

      split = getSizeC() > 1;
      for (int i=0; i<getSeriesCount(); i++) {
        core[i].rgb = false;
        core[i].littleEndian = true;
        core[i].interleaved = false;
        core[i].indexed = channelColors.size() > 0;
        core[i].falseColor = true;
        core[i].metadataComplete = true;
        core[i].imageCount = core[i].sizeZ * core[i].sizeT * core[i].sizeC;
      }

      // read first CustomData block

      if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
        posX = handler.getXPositions();
        posY = handler.getYPositions();
        posZ = handler.getZPositions();

        if (customDataOffsets.size() > 0) {
          in.seek(customDataOffsets.get(0).longValue());
          int[] p = customDataLengths.get(0);
          int len = p[0] + p[1];

          int timestampBytes = imageOffsets.size() * 8;
          in.skipBytes(len - timestampBytes);

          // the acqtimecache is a undeliniated stream of doubles

          for (int series=0; series<getSeriesCount(); series++) {
            setSeries(series);
            int count = split ? getImageCount() / getSizeC() : getImageCount();
            for (int plane=0; plane<count; plane++) {
              // timestamps are stored in ms; we want them in seconds
              double time = in.readDouble() / 1000;
              tsT.add(new Double(time));
              addSeriesMeta("timestamp " + plane, time);
            }
          }
          setSeries(0);
        }

        if (posX.size() == 0 && xOffset != 0) {
          in.seek(xOffset);
          for (int i=0; i<imageOffsets.size(); i++) {
            posX.add(new Double(in.readDouble()));
          }
        }
        if (posY.size() == 0 && yOffset != 0) {
          in.seek(yOffset);
          for (int i=0; i<imageOffsets.size(); i++) {
            posY.add(new Double(in.readDouble()));
          }
        }
        if (posZ.size() == 0 && zOffset != 0) {
          in.seek(zOffset);
          for (int i=0; i<imageOffsets.size(); i++) {
            posZ.add(new Double(in.readDouble()));
          }
        }
      }

      populateMetadataStore(handler);
      return;
    }
    else in.seek(0);

    // older version of ND2 - uses JPEG 2000 compression

    isJPEG = true;

    LOGGER.info("Calculating image offsets");

    ArrayList<Long> vs = new ArrayList<Long>();

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

    LOGGER.info("Finding XML metadata");

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

    LOGGER.info("Parsing XML");

    ArrayList<Long> zs = new ArrayList<Long>();
    ArrayList<Long> ts = new ArrayList<Long>();

    int numSeries = 0;
    ND2Handler handler = null;
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
        blockLength -= 2;
        if (blockLength + in.getFilePointer() >= in.length()) {
          blockLength = (int) (in.length() - in.getFilePointer());
        }
        s = in.readString(blockLength);
        s = s.replaceAll("<!--.+?>", ""); // remove comments
        int openBracket = s.indexOf("<");
        if (openBracket == -1) continue;
        int closedBracket = s.lastIndexOf(">") + 1;
        if (closedBracket < openBracket) continue;
        s = s.substring(openBracket, closedBracket).trim();
        if (s.indexOf("CalibrationSeq") == -1 && s.indexOf("VCAL") == -1 &&
          s.indexOf("jp2cLUNK") == -1)
        {
          sb.append(s);
        }
      }

      sb.append("</NIKON>");

      LOGGER.info("Finished assembling XML string");

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

      String xml = sb.substring(offset, len - offset);
      handler = new ND2Handler(core);
      try {
        XMLTools.parseXML(XMLTools.sanitizeXML(xml), handler);
      }
      catch (IOException e) { }
      xml = null;

      isLossless = handler.isLossless();
      fieldIndex = handler.getFieldIndex();
      zs = handler.getZSections();
      ts = handler.getTimepoints();
      numSeries = handler.getSeriesCount();
      core = handler.getCoreMetadata();
      Hashtable<String, Object> globalMetadata = handler.getMetadata();
      for (String key : globalMetadata.keySet()) {
        addGlobalMeta(key, globalMetadata.get(key));
      }
    }

    LOGGER.info("Populating metadata");

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

    if (getDimensionOrder() == null) core[0].dimensionOrder = "";

    if (getSizeC() > 1) {
      core[0].dimensionOrder = getDimensionOrder().replaceAll("C", "");
      core[0].dimensionOrder = "C" + getDimensionOrder();
      fieldIndex++;
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

    populateMetadataStore(handler);
  }

  // -- Helper methods --

  private void populateMetadataStore(ND2Handler handler) throws FormatException
  {
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    String filename = new Location(getCurrentFile()).getName();
    if (handler != null) {
      ArrayList<String> posNames = handler.getPositionNames();
      for (int i=0; i<getSeriesCount(); i++) {
        String suffix =
          i < posNames.size() ? posNames.get(i) : "(series " + (i + 1) + ")";
        String name = filename + " " + suffix;
        store.setImageName(name.trim(), i);
      }
    }

    colors = new int[getEffectiveSizeC()];

    ArrayList<String> channelNames = null;
    if (handler != null) {
      channelNames = handler.getChannelNames();
      for (int i=0; i<getSeriesCount(); i++) {
        for (int c=0; c<getEffectiveSizeC(); c++) {
          int index = i * getSizeC() + c;
          if (index < channelNames.size()) {
            String channelName = channelNames.get(index);
            Integer channelColor = channelColors.get(channelName);
            colors[c] = channelColor == null ? 0 : channelColor.intValue();
          }
        }
      }
    }

    if (getMetadataOptions().getMetadataLevel() == MetadataLevel.MINIMUM) {
      return;
    }

    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrumentID, 0);

    for (int i=0; i<getSeriesCount(); i++) {
      // link Instrument and Image
      store.setImageInstrumentRef(instrumentID, i);
    }

    // populate Dimensions data
    if (handler != null) {
      for (int i=0; i<getSeriesCount(); i++) {
        double sizeX = handler.getPixelSizeX();
        double sizeY = handler.getPixelSizeY();
        double sizeZ = handler.getPixelSizeZ();

        if (sizeX > 0) {
          store.setPixelsPhysicalSizeX(new PositiveFloat(sizeX), i);
        }
        else {
          LOGGER.warn("Expected positive value for PhysicalSizeX; got {}",
            sizeX);
        }
        if (sizeY > 0) {
          store.setPixelsPhysicalSizeY(new PositiveFloat(sizeY), i);
        }
        else {
          LOGGER.warn("Expected positive value for PhysicalSizeY; got {}",
            sizeY);
        }
        if (sizeZ > 0) {
          store.setPixelsPhysicalSizeZ(new PositiveFloat(sizeZ), i);
        }
        else {
          LOGGER.warn("Expected positive value for PhysicalSizeZ; got {}",
            sizeZ);
        }
      }
    }

    // populate PlaneTiming and StagePosition data
    ArrayList<Double> exposureTime = null;
    if (handler != null) handler.getExposureTimes();
    for (int i=0; i<getSeriesCount(); i++) {
      if (tsT.size() > 0) {
        setSeries(i);
        for (int n=0; n<getImageCount(); n++) {
          int[] coords = getZCTCoords(n);
          int stampIndex = coords[2] + i * getSizeT();
          if (tsT.size() == getImageCount()) stampIndex = n;
          else if (tsT.size() == getSizeZ()) {
            stampIndex = coords[0];
          }
          double stamp = tsT.get(stampIndex).doubleValue();
          store.setPlaneDeltaT(stamp, i, n);

          int index = i * getSizeC() + coords[1];
          if (exposureTime != null && index < exposureTime.size()) {
            store.setPlaneExposureTime(exposureTime.get(index), i, n);
          }
        }
      }

      if (handler != null) {
        if (posX == null) posX = handler.getXPositions();
        if (posY == null) posY = handler.getYPositions();
        if (posZ == null) posZ = handler.getZPositions();
      }

      String pos = "for position #" + (i + 1);
      for (int n=0; n<getImageCount(); n++) {
        int index = i * getImageCount() + n;
        if (posX != null) {
          if (index >= posX.size()) index = i;
          if (index < posX.size()) {
            String key = "X position ";
            store.setPlanePositionX(posX.get(index), i, n);
            addSeriesMeta(key + (i + 1), posX.get(index));
            addGlobalMeta(key + pos, posX.get(index));
          }
        }
        if (posY != null) {
          if (index < posY.size()) {
            String key = "Y position ";
            store.setPlanePositionY(posY.get(index), i, n);
            addSeriesMeta(key + (i + 1), posY.get(index));
            addGlobalMeta(key + pos, posY.get(index));
          }
        }
        if (posZ != null) {
          if (index < posZ.size()) {
            store.setPlanePositionZ(posZ.get(index), i, n);
            String key = "Z position " + pos + ", plane #" + (n + 1);
            addSeriesMeta(key, posZ.get(index));
            addGlobalMeta(key, posZ.get(index));
          }
        }
      }
    }

    if (handler == null) {
      setSeries(0);
      return;
    }

    String detectorID = MetadataTools.createLSID("Detector", 0, 0);
    store.setDetectorID(detectorID, 0, 0);
    store.setDetectorModel(handler.getCameraModel(), 0, 0);
    store.setDetectorType(getDetectorType("Other"), 0, 0);

    ArrayList<String> modality = handler.getModalities();
    ArrayList<String> binning = handler.getBinnings();
    ArrayList<Double> speed = handler.getSpeeds();
    ArrayList<Double> gain = handler.getGains();
    ArrayList<Double> temperature = handler.getTemperatures();
    ArrayList<Integer> exWave = handler.getExcitationWavelengths();
    ArrayList<Integer> emWave = handler.getEmissionWavelengths();
    ArrayList<Integer> power = handler.getPowers();
    ArrayList<Hashtable<String, String>> rois = handler.getROIs();

    for (int i=0; i<getSeriesCount(); i++) {
      for (int c=0; c<getEffectiveSizeC(); c++) {
        int index = i * getSizeC() + c;
        Double pinholeSize = handler.getPinholeSize();
        if (pinholeSize != null) {
          store.setChannelPinholeSize(pinholeSize, i, c);
        }
        if (index < channelNames.size()) {
          String channelName = channelNames.get(index);
          store.setChannelName(channelName, i, c);
        }
        else if (channelNames.size() == getSizeC()) {
          store.setChannelName(channelNames.get(c), i, c);
        }
        if (index < modality.size()) {
          store.setChannelAcquisitionMode(
            getAcquisitionMode(modality.get(index)), i, c);
        }
        if (index < emWave.size()) {
          if (emWave.get(index) > 0) {
            store.setChannelEmissionWavelength(
              new PositiveInteger(emWave.get(index)), i, c);
          }
          else {
            LOGGER.warn(
              "Expected positive value for EmissionWavelength; got {}",
              emWave.get(index));
          }
        }
        if (index < exWave.size()) {
          if (exWave.get(index) > 0) {
            store.setChannelExcitationWavelength(
              new PositiveInteger(exWave.get(index)), i, c);
          }
          else {
            LOGGER.warn(
              "Expected positive value for ExcitationWavelength; got {}",
              exWave.get(index));
          }
        }
        if (index < binning.size()) {
          store.setDetectorSettingsBinning(
            getBinning(binning.get(index)), i, c);
        }
        if (index < gain.size()) {
          store.setDetectorSettingsGain(gain.get(index), i, c);
        }
        if (index < speed.size()) {
          store.setDetectorSettingsReadOutRate(speed.get(index), i, c);
        }
        store.setDetectorSettingsID(detectorID, i, c);
      }
    }

    for (int i=0; i<getSeriesCount(); i++) {
      if (i * getSizeC() < temperature.size()) {
        Double temp = temperature.get(i * getSizeC());
        store.setImagingEnvironmentTemperature(temp, i);
      }
    }

    // populate DetectorSettings
    Double voltage = handler.getVoltage();
    if (voltage != null) {
      store.setDetectorSettingsVoltage(voltage, 0, 0);
    }

    // populate Objective
    Double na = handler.getNumericalAperture();
    if (na != null) {
      store.setObjectiveLensNA(na, 0, 0);
    }
    Double mag = handler.getMagnification();
    if (mag != null) {
      store.setObjectiveCalibratedMagnification(mag, 0, 0);
    }
    store.setObjectiveModel(handler.getObjectiveModel(), 0, 0);

    String immersion = handler.getImmersion();
    if (immersion == null) immersion = "Other";
    store.setObjectiveImmersion(getImmersion(immersion), 0, 0);

    String correction = handler.getCorrection();
    if (correction == null || correction.length() == 0) correction = "Other";
    store.setObjectiveCorrection(getCorrection(correction), 0, 0);

    // link Objective to Image
    String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
    store.setObjectiveID(objectiveID, 0, 0);

    Double refractiveIndex = handler.getRefractiveIndex();

    for (int i=0; i<getSeriesCount(); i++) {
      store.setImageObjectiveSettingsID(objectiveID, i);
      if (refractiveIndex != null) {
        store.setImageObjectiveSettingsRefractiveIndex(refractiveIndex, i);
      }
    }

    setSeries(0);

    // populate ROI data

    if (getMetadataOptions().getMetadataLevel() == MetadataLevel.NO_OVERLAYS) {
      return;
    }
    handler.populateROIs(store);
  }

  private Codec createCodec(boolean isJPEG) {
    return isJPEG ? new JPEG2000Codec() : new ZlibCodec();
  }

  private void copyPixels(int x, int y, int w, int h, int bpp, int scanlinePad,
    byte[] pix, byte[] buf, boolean split)
    throws IOException
  {
    if (split) {
      pix = ImageTools.splitChannels(pix, lastChannel, getEffectiveSizeC(), bpp,
        false, true);
    }
    RandomAccessInputStream s = new RandomAccessInputStream(pix);
    readPlane(s, x, y, w, h, scanlinePad, buf);
    s.close();
  }

}
