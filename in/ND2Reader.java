//
// ND2Reader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

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
import java.util.StringTokenizer;
import javax.imageio.*;
import loci.formats.*;

/** 
 * ND2Reader is the file format reader for Nikon ND2 files.
 * The JAI library is required to use this reader; it is available from
 * http://jai-imageio.dev.java.net.  Note that JAI is bundled with a version
 * of the JJ2000 library, so it is important that either (1) the JJ2000 jar
 * file is *not* in the classpath; or (2) the JAI jar file precedes JJ2000 in
 * the classpath.
 */
public class ND2Reader extends FormatReader {

  // -- Constants --

  private static final String NO_JAI_MSG =
    "You need to install JAI from http://jai-imageio.dev.java.net";

  // -- Static fields --
 
  private static boolean noJAI = false;
  private static ReflectedUniverse r = createReflectedUniverse();

  private static ReflectedUniverse createReflectedUniverse() {
    r = null;
    try {
      r = new ReflectedUniverse();
      r.exec("import jj2000.j2k.fileformat.reader.FileFormatReader");
      r.exec("import jj2000.j2k.io.BEBufferedRandomAccessFile");
    }
    catch (Throwable exc) { noJAI = true; }
    return r;
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

    byte[] b = new byte[0];
   
    if (no < getImageCount(id) - 1) {
      b = new byte[(int) (offsets[no + 1] - offsets[no])];
    }
    else b = new byte[(int) (in.length() - offsets[no])]; 
    in.read(b);

    BufferedImage img = ImageIO.read(new ByteArrayInputStream(b));

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

    ColorModel cm = ImageTools.makeColorModel(sizeC[0], dataType, validBits);
    return ImageTools.makeBuffered(img);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    currentId = null;
  }

  /** Initializes the given ND2 file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("initFile(" + id + ")");
    if (noJAI) throw new FormatException(NO_JAI_MSG);
    super.initFile(id);

    // make sure that a JPEG 2000 reader is available
    String[] fnames = ImageIO.getReaderFormatNames();
    boolean foundReader = false;
    for (int i=0; i<fnames.length; i++) {
      foundReader = fnames[i].equals("JPEG 2000");
      if (foundReader) i = fnames.length;
    }
    if (!foundReader) throw new FormatException(NO_JAI_MSG);

    in = new RandomAccessStream(getMappedId(id));

    try {
      r.setVar("id", getMappedId(id));
      r.setVar("read", "r");
      r.exec("in = new BEBufferedRandomAccessFile(id, read)");
      r.setVar("j2kMetadata", null);
      r.exec("ff = new FileFormatReader(in, j2kMetadata)");

      r.exec("ff.readFileFormat()");
      r.exec("offsets = ff.getCodeStreamPos()");
      offsets = (long[]) r.getVar("offsets");
    }
    catch (ReflectException e) { throw new FormatException(e); }

    numImages = offsets.length;

    pixelType[0] = FormatReader.UINT8;

    BufferedImage img = openImage(id, 0);
    sizeX[0] = img.getWidth();
    sizeY[0] = img.getHeight();
    rgb = img.getRaster().getNumBands() > 1;

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

    // read XML metadata from the end of the file

    in.seek(offsets[offsets.length - 1]);

    boolean found = false;
    int off = -1;
    byte[] buf = new byte[2048];
    while (!found) {
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
      byte[] b = new byte[(int) (in.length() - off)];
      in.read(b);
      String xml = new String(b);

      // assume that this XML string will be malformed, since that's how both
      // sample files are; this means we need to manually parse it :-(

      // first, let's strip out all comments

      while (xml.indexOf("<!--") != -1) {
        int start = xml.indexOf("<!--");
        int end = xml.indexOf("-->", start);
        String pre = xml.substring(0, start);
        String post = xml.substring(end + 3);
        xml = pre + post;
      }

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
                  metadata.put(prefix + " " + pre + " " + key, value);
                }
                s = s.substring(s.indexOf("\"", eq + 2) + 1);
             }
            }
          }
        }
      }
    }

    String sigBits =
      (String) metadata.get("AdvancedImageAttributes SignificantBits value");
    int bits = 0;
    if (sigBits != null && sigBits.length() > 0) {
      bits = Integer.parseInt(sigBits.trim());
    }

    // determine the pixel size
    String pixX = (String) metadata.get(
      "CalibrationSeq _SEQUENCE_INDEX=\"0\" dCalibration value");
    String pixZ = (String) metadata.get(
      "CalibrationSeq _SEQUENCE_INDEX=\"0\" dAspect value");

    float pixSizeX = 0f;
    float pixSizeZ = 0f;

    if (pixX != null && pixX.length() > 0) {
      pixSizeX = Float.parseFloat(pixX.trim());
    }
    if (pixZ != null && pixZ.length() > 0) {
      pixSizeZ = Float.parseFloat(pixZ.trim());
    }

    String channels =
      (String) metadata.get("AdvancedImageAttributes VirtualComponents value");
    if (channels != null && channels.length() > 0) {
      sizeC[0] = 3 - Integer.parseInt(channels.trim());
    }
    else sizeC[0] = img.getRaster().getNumBands();

    sizeT[0] = numImages / (!rgb ? sizeC[0] : 1);
    sizeZ[0] = 1;
    orderCertain[0] = false;
    currentOrder[0] = sizeC[0] > 1 ? "XYCTZ" : "XYTZC";
    pixelType[0] = ImageTools.getPixelType(img);

    if (!rgb) {
      while (sizeC[0] * sizeT[0] * sizeZ[0] < numImages) numImages--;
    }

    if (bits != 0) {
      validBits = new int[sizeC[0] == 2 ? 3 : sizeC[0]];
      for (int i=0; i<validBits.length; i++) validBits[i] = bits;
    }
    else validBits = null;

    MetadataStore store = getMetadataStore(id);
    store.setPixels(
      new Integer(sizeX[0]),
      new Integer(sizeY[0]),
      new Integer(1),
      new Integer(sizeC[0]),
      new Integer(numImages),
      new Integer(pixelType[0]),
      new Boolean(isLittleEndian(id)),
      currentOrder[0],
      null);

    store.setDimensions(new Float(pixSizeX), new Float(pixSizeX),
      new Float(pixSizeZ), null, null, null);
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new ND2Reader().testRead(args);
  }

}
