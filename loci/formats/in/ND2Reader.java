//
// ND2Reader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.MemoryCacheImageInputStream;
import loci.formats.*;

/**
 * ND2Reader is the file format reader for Nikon ND2 files.
 * The JAI ImageIO library is required to use this reader; it is available from
 * http://jai-imageio.dev.java.net. Note that JAI ImageIO is bundled with a
 * version of the JJ2000 library, so it is important that either:
 * (1) the JJ2000 jar file is *not* in the classpath; or
 * (2) the JAI jar file precedes JJ2000 in the classpath.
 */
public class ND2Reader extends FormatReader {

  // -- Constants --

  private static final String NO_J2K_MSG =
    "The JAI Image I/O Tools are required to read ND2 files. Please " +
    "obtain jai_imageio.jar from http://loci.wisc.edu/ome/formats.html";

  private static final String J2K_READER =
    "com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageReader";

  // -- Static fields --

  private static boolean noJ2k = false;
  private static ReflectedUniverse r = createReflectedUniverse();

  private static ReflectedUniverse createReflectedUniverse() {
    // NB: ImageJ does not access the jai_imageio classes with the normal
    // class loading scheme, and thus the necessary service provider stuff is
    // not automatically registered. Instead, we register the J2KImageReader
    // with the IIORegistry manually, merely so that we can obtain a
    // J2KImageReaderSpi object from the IIORegistry's service provider
    // lookup function, then use it to construct a J2KImageReader object
    // directly, which we can use to process ND2 files one plane at a time.

    ReflectedUniverse ru = null;
    try {
      // register J2KImageReader with IIORegistry
      String j2kReaderSpi = J2K_READER + "Spi";
      Class j2kSpiClass = null;
      try {
        j2kSpiClass = Class.forName(j2kReaderSpi);
      }
      catch (ClassNotFoundException exc) {
        if (debug) exc.printStackTrace();
        noJ2k = true;
      }
      catch (NoClassDefFoundError err) {
        if (debug) err.printStackTrace();
        noJ2k = true;
      }
      IIORegistry registry = IIORegistry.getDefaultInstance();
      if (j2kSpiClass != null) {
        Iterator providers = ServiceRegistry.lookupProviders(j2kSpiClass);
        registry.registerServiceProviders(providers);
      }

      // obtain J2KImageReaderSpi instance from IIORegistry
      Object j2kSpi = registry.getServiceProviderByClass(j2kSpiClass);

      ru = new ReflectedUniverse();

      // for computing offsets in initFile
      ru.exec("import jj2000.j2k.fileformat.reader.FileFormatReader");
      ru.exec("import jj2000.j2k.io.BEBufferedRandomAccessFile");
      ru.exec("import jj2000.j2k.util.ISRandomAccessIO");

      // for reading pixel data in openImage
      ru.exec("import " + J2K_READER);
      ru.setVar("j2kSpi", j2kSpi);
      ru.exec("j2kReader = new J2KImageReader(j2kSpi)");
    }
    catch (Throwable exc) {
      if (debug) exc.printStackTrace();
      noJ2k = true;
    }
    return ru;
  }

  // -- Fields --

  /** Current file */
  private RandomAccessStream in;

  /** Number of image planes in the file. */
  protected int numImages = 0;

  /** Array of image offsets. */
  private long[] offsets;

  /** Number of valid bits per pixel */
  private int[] validBits;

  private boolean rgb;

  // -- Constructor --

  /** Constructs a new ND2 reader. */
  public ND2Reader() { super("Nikon ND2", new String[] {"nd2", "jp2"}); }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for an ND2 file. */
  public boolean isThisType(byte[] block) {
    if (block.length < 8) return false;
    return block[4] == 0x6a && block[5] == 0x50 && block[6] == 0x20 &&
      block[7] == 0x20;
  }

  /** Determines the number of images in the given ND2 file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numImages;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return rgb;
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    return false;
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return true;
  }

  /**
   * Obtains the specified image from the
   * given ND2 file as a byte array.
   */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    byte[][] pixels = ImageTools.getPixelBytes(openImage(id, no), false);

    if (pixels.length == 1 || sizeC[0] == 1) {
      return pixels[0];
    }

    byte[] b = new byte[sizeC[0] * pixels[0].length];
    for (int i=0; i<sizeC[0]; i++) {
      System.arraycopy(pixels[i], 0, b, i*pixels[0].length, pixels[i].length);
    }
    updateMinMax(b, no);
    return b;
  }

  /** Obtains the specified image from the given ND2 file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    in.seek(offsets[no]);

    byte[] b;
    if (no < getImageCount(id) - 1) {
      b = new byte[(int) (offsets[no + 1] - offsets[no])];
    }
    else b = new byte[(int) (in.length() - offsets[no])];
    in.readFully(b);

    ByteArrayInputStream bis = new ByteArrayInputStream(b);
    // NB: Even after registering J2KImageReader with
    // IIORegistry manually, the following still does not work:
    //BufferedImage img = ImageIO.read(bis);
    MemoryCacheImageInputStream mciis = new MemoryCacheImageInputStream(bis);
    BufferedImage img = null;
    try {
      r.setVar("mciis", mciis);
      r.exec("j2kReader.setInput(mciis)");
      r.setVar("zero", 0);
      r.setVar("param", null);
      img = (BufferedImage) r.exec("j2kReader.read(zero, param)");
    }
    catch (ReflectException exc) {
      throw new FormatException(exc);
    }
    bis.close();
    mciis.close();
    b = null;

    int dataType = 0;
    switch (pixelType[0]) {
      case FormatReader.INT8:
        throw new FormatException("Unsupported pixel type: int8");
      case FormatReader.UINT8:
        dataType = DataBuffer.TYPE_BYTE;
        break;
      case FormatReader.INT16:
        dataType = DataBuffer.TYPE_SHORT;
        break;
      case FormatReader.UINT16:
        dataType = DataBuffer.TYPE_USHORT;
        break;
      case FormatReader.INT32:
      case FormatReader.UINT32:
        dataType = DataBuffer.TYPE_INT;
        break;
      case FormatReader.FLOAT:
        dataType = DataBuffer.TYPE_FLOAT;
        break;
      case FormatReader.DOUBLE:
        dataType = DataBuffer.TYPE_DOUBLE;
        break;
    }

    updateMinMax(img, no);
    return img;
  }

  /* @see IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws FormatException, IOException {
    if (fileOnly && in != null) in.close();
    else if (!fileOnly) close();
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given ND2 file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("ND2Reader.initFile(" + id + ")");
    if (noJ2k) throw new FormatException(NO_J2K_MSG);
    super.initFile(id);

    in = new RandomAccessStream(id);

    try {
      File f = new File(Location.getMappedId(id));
      if (f.exists()) {
        r.setVar("id", Location.getMappedId(id));
        r.setVar("read", "r");
        r.setVar("size", 4096);
        r.exec("in = new BEBufferedRandomAccessFile(id, read, size)");
      }
      else {
        r.setVar("id", in);
        r.setVar("size", 65536);
        r.setVar("inc", 4096);
        r.setVar("max", (int) in.length());
        r.exec("in = new ISRandomAccessIO(id, size, inc, max)");
      }

      r.setVar("j2kMetadata", null);
      r.exec("ff = new FileFormatReader(in, j2kMetadata)");

      r.exec("ff.readFileFormat()");
      r.exec("offsets = ff.getCodeStreamPos()");
      offsets = (long[]) r.getVar("offsets");
    }
    catch (ReflectException e) { throw new FormatException(e); }

    numImages = offsets.length;

    pixelType[0] = FormatReader.UINT8;

    // read XML metadata from the end of the file

    in.seek(offsets[offsets.length - 1]);

    boolean found = false;
    int off = -1;
    byte[] buf = new byte[2048];
    while (!found && in.getFilePointer() < in.length()) {
      int read = 0;
      if (in.getFilePointer() == offsets[offsets.length - 1]) {
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
          off = (int) (in.getFilePointer() - (read+10) + i);
          i = buf.length;
          break;
        }
      }
    }

    if (off > 0 && off < in.length() - 5) {
      in.seek(off + 5);
      byte[] b = new byte[(int) (in.length() - off - 5)];
      in.readFully(b);
      String xml = new String(b);

      // assume that this XML string will be malformed, since that's how both
      // sample files are; this means we need to manually parse it :-(

      // first, let's strip out all comments

      StringBuffer sb = new StringBuffer(xml);
      while (sb.indexOf("<!--") != -1) {
        int ndx = sb.indexOf("<!--");
        sb.delete(ndx, sb.indexOf("-->", ndx));
      }
      xml = sb.toString();

      // strip out binary data at the end - this is irrelevant for our purposes
      xml = xml.substring(0, xml.lastIndexOf("</MetadataSeq>") + 14);

      // each chunk appears on a separate line, so split up the chunks

      StringTokenizer st = new StringTokenizer(xml, "\r\n");
      while (st.hasMoreTokens()) {
        String token = st.nextToken().trim();
        if (token.indexOf("<") != -1) {
          String prefix = token.substring(1, token.indexOf(">")).trim();
          token = token.substring(token.indexOf(">") + 1);

          while (token.indexOf("<") != -1) {
            int start = token.indexOf("<");
            String s = token.substring(start + 1, token.indexOf(">", start));
            token = token.substring(token.indexOf(">", start));

            // get the prefix for this tag
            if (s.indexOf(" ") != -1) {
              String pre = s.substring(0, s.indexOf(" ")).trim();
              s = s.substring(s.indexOf(" ") + 1);

              // get key/value pairs
              while (s.indexOf("=") != -1) {
                int eq = s.indexOf("=");
                String key = s.substring(0, eq).trim();
                String value =
                  s.substring(eq + 2, s.indexOf("\"", eq + 2)).trim();

                // strip out the data types
                if (key.indexOf("runtype") == -1) {
                  String effectiveKey = prefix + " " + pre + " " + key;
                  if (!metadata.containsKey(effectiveKey)) {
                    addMeta(effectiveKey, value);
                  }
                  else {
                    String v = (String) getMeta(effectiveKey);
                    boolean parse = true;
                    for (int i=0; i<v.length(); i++) {
                      if (Character.isLetter(v.charAt(i)) ||
                        Character.isWhitespace(v.charAt(i)))
                      {
                        parse = false;
                        break;
                      }
                    }
                    if (parse) {
                      addMeta(effectiveKey, value);
                    }
                  }
                }
                s = s.substring(s.indexOf("\"", eq + 2) + 1);
              }
            }
          }
        }
      }
    }

    BufferedImage img = openImage(id, 0);
    sizeX[0] = img.getWidth();
    sizeY[0] = img.getHeight();

    int numInvalid = 0;

    for (int i=1; i<offsets.length; i++) {
      if (offsets[i] - offsets[i - 1] < (sizeX[0] * sizeY[0] / 4)) {
        offsets[i - 1] = 0;
        numInvalid++;
      }
    }

    long[] tempOffsets = new long[numImages - numInvalid];
    int pt = 0;
    for (int i=0; i<offsets.length; i++) {
      if (offsets[i] != 0) {
        tempOffsets[pt] = offsets[i];
        pt++;
      }
    }
    offsets = tempOffsets;
    numImages = offsets.length;

    String sigBits =
      (String) getMeta("AdvancedImageAttributes SignificantBits value");
    int bits = 0;
    if (sigBits != null && sigBits.length() > 0) {
      bits = Integer.parseInt(sigBits.trim());
    }

    // determine the pixel size
    String pixX = (String)
      getMeta("CalibrationSeq _SEQUENCE_INDEX=\"0\" dCalibration value");
    String pixZ = (String)
      getMeta("CalibrationSeq _SEQUENCE_INDEX=\"0\" dAspect value");

    float pixSizeX = 0f;
    float pixSizeZ = 0f;

    if (pixX != null && pixX.length() > 0) {
      pixSizeX = Float.parseFloat(pixX.trim());
    }
    if (pixZ != null && pixZ.length() > 0) {
      pixSizeZ = Float.parseFloat(pixZ.trim());
    }

    String c = (String)
      getMeta("MetadataSeq _SEQUENCE_INDEX=\"0\" uiCompCount value");
    if (c != null) sizeC[0] = Integer.parseInt(c);
    else sizeC[0] = openImage(id, 0).getRaster().getNumBands();
    if (sizeC[0] == 2) sizeC[0] = 1;

    long[] timestamps = new long[numImages];
    long[] zstamps = new long[numImages];

    for (int i=0; i<numImages; i++) {
      String pre = "MetadataSeq _SEQUENCE_INDEX=\"" + i + "\" ";
      String tstamp = (String) getMeta(pre + "dTimeMSec value");
      String zstamp = (String) getMeta(pre + "dZPos value");
      if (tstamp != null) timestamps[i] = (long) Float.parseFloat(tstamp);
      if (zstamp != null) zstamps[i] = (long) Float.parseFloat(zstamp);
    }

    Vector zs = new Vector();
    Vector ts = new Vector();
    for (int i=0; i<numImages; i++) {
      if (!zs.contains(new Long(zstamps[i]))) {
        sizeZ[0]++;
        zs.add(new Long(zstamps[i]));
      }
      if (!ts.contains(new Long(timestamps[i]))) {
        sizeT[0]++;
        ts.add(new Long(timestamps[i]));
      }
    }

    currentOrder[0] = "XY";
    long deltaT = timestamps.length > 1 ? timestamps[1] - timestamps[0] : 1;
    long deltaZ = zstamps.length > 1 ? zstamps[1] - zstamps[0] : 1;

    if (deltaT < deltaZ || deltaZ == 0) currentOrder[0] += "CTZ";
    else currentOrder[0] += "CZT";

    // we calculate this directly (instead of calling getEffectiveSizeC) because
    // sizeZ and sizeT have not been accurately set yet
    int effectiveC = (sizeC[0] / 3) + 1;

    if (numImages < sizeZ[0] * sizeT[0]) {
      if (sizeT[0] == numImages) {
        sizeT[0] /= sizeZ[0] * effectiveC;
        while (numImages > sizeZ[0] * sizeT[0] * effectiveC) {
          sizeT[0]++;
        }
      }
      else if (sizeZ[0] == numImages) {
        sizeZ[0] /= sizeT[0] * effectiveC;
        while (numImages > sizeZ[0] * sizeT[0] * effectiveC) {
          sizeZ[0]++;
        }
      }

      if (numImages < sizeZ[0] * sizeT[0] * effectiveC) {
        if (sizeZ[0] < sizeT[0]) {
          sizeZ[0]--;
          while (numImages > sizeZ[0] * sizeT[0] * effectiveC) {
            sizeT[0]++;
          }
          while (numImages < sizeZ[0] * sizeT[0] * effectiveC) {
            sizeT[0]--;
          }
        }
        else {
          sizeT[0]--;
          while (numImages > sizeZ[0] * sizeT[0] * effectiveC) {
            sizeZ[0]++;
          }
          if (numImages < sizeZ[0] * sizeT[0] * effectiveC) {
            sizeZ[0]--;
          }
        }
        while (numImages > sizeZ[0] * sizeT[0] * effectiveC) {
          numImages--;
        }
      }
    }

    if (bits != 0) {
      validBits = new int[sizeC[0] == 2 ? 3 : sizeC[0]];
      for (int i=0; i<validBits.length; i++) validBits[i] = bits;
    }
    else validBits = null;

    if (validBits == null) pixelType[0] = FormatReader.UINT8;
    else {
      int bpp = validBits[0];
      while (bpp % 8 != 0) bpp++;
      switch (bpp) {
        case 8:
          pixelType[0] = FormatReader.UINT8;
          break;
        case 16:
          pixelType[0] = FormatReader.UINT16;
          break;
        case 32:
          pixelType[0] = FormatReader.UINT32;
          break;
        default:
          throw new FormatException("Unsupported bits per pixel: " + bpp);
      }

    }

    rgb = sizeC[0] == 3;

    MetadataStore store = getMetadataStore(id);
    store.setPixels(
      new Integer(sizeX[0]),
      new Integer(sizeY[0]),
      new Integer(sizeZ[0]),
      new Integer(sizeC[0]),
      new Integer(sizeT[0]),
      new Integer(pixelType[0]),
      new Boolean(isLittleEndian(id)),
      currentOrder[0],
      null,
      null);

    store.setDimensions(new Float(pixSizeX), new Float(pixSizeX),
      new Float(pixSizeZ), null, null, null);
    for (int i=0; i<sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null);
    }

    String prefix = "MetadataSeq _SEQUENCE_INDEX=\"0\" ";

    String gain = (String) getMeta(prefix + "dGain value");
    String voltage = (String) getMeta(prefix + "dLampVoltage value");
    String mag = (String) getMeta(prefix + "dObjectiveMag value");
    String na = (String) getMeta(prefix + "dObjectiveNA value");

    store.setDetector(null, null, null, null,
      gain == null ? null : new Float(gain),
      voltage == null ? null : new Float(voltage), null, null, null);
    store.setObjective(null, null, null, na == null ? null : new Float(na),
      mag == null ? null : new Float(mag), null, null);
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new ND2Reader().testRead(args);
  }

}
