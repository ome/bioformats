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
import java.util.zip.*;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.xml.parsers.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * ND2Reader is the file format reader for Nikon ND2 files.
 * The JAI ImageIO library is required to use this reader; it is available from
 * http://jai-imageio.dev.java.net. Note that JAI ImageIO is bundled with a
 * version of the JJ2000 library, so it is important that either:
 * (1) the JJ2000 jar file is *not* in the classpath; or
 * (2) the JAI jar file precedes JJ2000 in the classpath.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/ND2Reader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/ND2Reader.java">SVN</a></dd></dl>
 */
public class ND2Reader extends FormatReader {

  // -- Constants --

  private static final String NO_J2K_MSG =
    "The JAI Image I/O Tools are required to read ND2 files. Please " +
    "obtain jai_imageio.jar from http://loci.wisc.edu/ome/formats.html";

  private static final String J2K_READER =
    "com.sun.media.imageioimpl.plugins.jpeg2000.J2KImageReader";

  /** Factory for generating SAX parsers. */
  public static final SAXParserFactory SAX_FACTORY =
    SAXParserFactory.newInstance();

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
        if (debug) LogTools.trace(exc);
        noJ2k = true;
      }
      catch (NoClassDefFoundError err) {
        if (debug) LogTools.trace(err);
        noJ2k = true;
      }
      catch (RuntimeException exc) {
        // HACK: workaround for bug in Apache Axis2
        String msg = exc.getMessage();
        if (msg != null && msg.indexOf("ClassNotFound") < 0) throw exc;
        if (debug) LogTools.trace(exc);
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
    catch (Throwable t) {
      noJ2k = true;
      if (debug) LogTools.trace(t);
    }
    return ru;
  }

  // -- Fields --

  /** Array of image offsets. */
  private long[][] offsets;

  /** Whether or not the pixel data is compressed using JPEG 2000. */
  private boolean isJPEG;

  /** Whether or not the pixel data is losslessly compressed. */
  private boolean isLossless;

  private boolean adjustImageCount;

  private Vector zs = new Vector();
  private Vector ts = new Vector();

  private int numSeries;

  private float pixelSizeX, pixelSizeY, pixelSizeZ;
  private String gain, voltage, mag, na;

  // -- Constructor --

  /** Constructs a new ND2 reader. */
  public ND2Reader() { super("Nikon ND2", new String[] {"nd2", "jp2"}); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    if (block.length < 8) return false;
    return block[4] == 0x6a && block[5] == 0x50 && block[6] == 0x20 &&
      block[7] == 0x20;
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

    in.seek(offsets[series][no]);

    int bpp = FormatTools.getBytesPerPixel(core.pixelType[series]);
    int pixel = bpp * getRGBChannelCount();

    if (isJPEG) {
      BufferedImage b = openImage(no, x, y, w, h);
      byte[][] pixels = ImageTools.getPixelBytes(b, false);
      if (pixels.length == 1 && getRGBChannelCount() > 1) {
        pixels = ImageTools.splitChannels(pixels[series], getRGBChannelCount(),
          bpp, false, !core.interleaved[series]);
      }
      for (int i=0; i<getRGBChannelCount(); i++) {
        System.arraycopy(pixels[i], 0, buf, i*pixels[i].length,
          pixels[i].length);
      }
      pixels = null;
    }
    else if (isLossless) {
      int plane = core.sizeX[series] * core.sizeY[series] * pixel;
      byte[] b = new byte[plane];
      in.read(b);

      byte[] t = null;
      if ((core.sizeX[series] % 2) != 0) {
        t = new byte[(core.sizeX[series] + 1) * core.sizeY[series] * pixel];
      }
      else t = new byte[plane];

      Inflater decompresser = new Inflater();
      decompresser.setInput(b);
      try { decompresser.inflate(t); }
      catch (DataFormatException e) { throw new FormatException(e); }
      decompresser.end();

      int width = core.sizeX[series];
      if ((width % 2) != 0) width++;

      for (int row=0; row<h; row++) {
        System.arraycopy(t, (row + y) * width * pixel + x * pixel,
          buf, row * w * pixel, w * pixel);
      }
    }
    else {
      in.skipBytes(y * core.sizeX[series] * pixel);
      for (int row=0; row<h; row++) {
        in.skipBytes(x * pixel);
        in.read(buf, row * w * pixel, w * pixel);
        in.skipBytes(pixel * (core.sizeX[series] - w - x));
      }
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#openImage(int, int, int, int, int) */
  public BufferedImage openImage(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (!isJPEG) {
      return ImageTools.makeImage(openBytes(no, x, y, w, h), core.sizeX[series],
        core.sizeY[series], getRGBChannelCount(), core.interleaved[series],
        FormatTools.getBytesPerPixel(core.pixelType[series]),
        core.littleEndian[series]);
    }

    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);

    in.seek(offsets[series][no]);

    long len = no < core.imageCount[series] - 1 ? offsets[series][no + 1] -
      offsets[series][no] : in.length() - offsets[series][no];

    byte[] b = new byte[(int) len];
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

    return img.getSubimage(x, y, w, h);
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatReader#close() */
  public void close() throws IOException {
    super.close();

    offsets = null;
    zs.clear();
    ts.clear();
    adjustImageCount = isJPEG = isLossless = false;
    numSeries = 0;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("ND2Reader.initFile(" + id + ")");
    if (noJ2k) throw new FormatException(NO_J2K_MSG);
    super.initFile(id);

    in = new RandomAccessStream(id);

    if (in.read() == -38 && in.read() == -50) {
      // newer version of ND2 - doesn't use JPEG2000

      isJPEG = false;
      in.seek(0);
      in.order(true);

      byte[] b = new byte[1024 * 1024];
      int numValidPlanes = 0;
      while (in.getFilePointer() < in.length()) {
        if (in.read() == -38 && in.read() == -50 && in.read() == -66 &&
          in.read() == 10)
        {
          // found a data chunk
          int len = in.readInt() + in.readInt();
          if (len > b.length) {
            // make sure size at least doubles, for efficiency
            int size = b.length + b.length;
            if (size < len) size = len;
            b = new byte[size];
          }
          in.skipBytes(4);

          if (debug) {
            debug("Reading chunk of size " + len +
              " at position " + in.getFilePointer());
          }
          int n = in.read(b, 0, len);
          if (n < len) break;

          if (len >= 12 && b[0] == 'I' && b[1] == 'm' && b[2] == 'a' &&
            b[3] == 'g' && b[4] == 'e' && b[5] == 'D' && b[6] == 'a' &&
            b[7] == 't' && b[8] == 'a' && b[9] == 'S' && b[10] == 'e' &&
            b[11] == 'q') // b.startsWith("ImageDataSeq")
          {
            // found pixel data

            StringBuffer sb = new StringBuffer();
            int pt = 13;
            while (b[pt] != '!') {
              sb.append((char) b[pt]);
              pt++;
            }
            int ndx = Integer.parseInt(sb.toString());
            if (core.sizeC[0] == 0 && core.sizeX[0] != 0 && core.sizeY[0] != 0)
            {
              Arrays.fill(core.sizeC, len / (core.sizeX[0] * core.sizeY[0] *
                FormatTools.getBytesPerPixel(core.pixelType[0])));
            }
            int seriesNdx = ndx / (core.sizeT[0] * core.sizeZ[0]);
            int plane = ndx % (core.sizeT[0] * core.sizeZ[0]);
            if (seriesNdx >= offsets.length) {
              long[][] tmpOffsets = offsets;
              offsets = new long[seriesNdx + 1][tmpOffsets[0].length];
              for (int i=0; i<tmpOffsets.length; i++) {
                System.arraycopy(tmpOffsets[i], 0, offsets[i], 0,
                  offsets[i].length);
              }
            }
            offsets[seriesNdx][plane] =
              in.getFilePointer() - len + sb.length() + 21;
            while (offsets[seriesNdx][plane] - in.getFilePointer() +
              len - sb.length() < 22)
            {
              offsets[seriesNdx][plane]++;
            }
            numValidPlanes++;
          }
          else if (len >= 5 && b[0] == 'I' && b[1] == 'm' && b[2] == 'a' &&
            b[3] == 'g' && b[4] == 'e') // b.startsWith("Image")
          {
            // XML metadata

            ND2Handler handler = new ND2Handler();

            // strip out invalid characters
            int off = 0;
            for (int i=0; i<len; i++) {
              char c = (char) b[i];
              if (off == 0 && c == '!') off = i + 1;

              if (Character.isISOControl(c) || !Character.isDefined(c)) {
                b[i] = (byte) ' ';
              }
            }

            if (len - off >= 5 && b[off] == '<' && b[off + 1] == '?' &&
              b[off + 2] == 'x' && b[off + 3] == 'm' &&
              b[off + 4] == 'l') // b.substring(off, off + 5).equals("<?xml")
            {
              ByteArrayInputStream s =
                new ByteArrayInputStream(b, off, len - off);

              try {
                SAXParser parser = SAX_FACTORY.newSAXParser();
                parser.parse(s, handler);
              }
              catch (ParserConfigurationException exc) {
                throw new FormatException(exc);
              }
              catch (SAXException exc) {
                throw new FormatException(exc);
              }

              // adjust SizeT, if necessary
              long planeSize = core.sizeX[0] * core.sizeY[0] *
                FormatTools.getBytesPerPixel(core.pixelType[0]) * core.sizeC[0];
              if (planeSize*core.imageCount[0]*core.imageCount.length >=
                in.length() && !isLossless)
              {
                int approxPlanes = (int) (in.length() / planeSize);
                core.sizeT[0] = approxPlanes / core.imageCount.length;
                if (core.sizeT[0] * core.imageCount.length < approxPlanes) {
                  core.sizeT[0]++;
                }
                core.imageCount[0] = core.sizeT[0];
                core.sizeZ[0] = 1;
              }
            }
          }

          if (core.imageCount[0] > 0 && offsets == null) {
            if (numSeries == 0) numSeries = 1;
            offsets = new long[numSeries][core.imageCount[0]];
            if (numSeries > 1) {
              int x = core.sizeX[0];
              int y = core.sizeY[0];
              int z = core.sizeZ[0];
              int c = core.sizeC[0];
              int t = core.sizeT[0];
              int count = core.imageCount[0];
              int pixelType = core.pixelType[0];
              core = new CoreMetadata(numSeries);
              Arrays.fill(core.sizeX, x);
              Arrays.fill(core.sizeY, y);
              Arrays.fill(core.sizeZ, z);
              Arrays.fill(core.sizeC, c);
              Arrays.fill(core.sizeT, t);
              Arrays.fill(core.imageCount, count);
              Arrays.fill(core.pixelType, pixelType);
            }
          }

          if (in.getFilePointer() < in.length() - 1) {
            if (in.read() != -38) in.skipBytes(15);
            else in.seek(in.getFilePointer() - 1);
          }
        }
      }

      Arrays.fill(core.sizeX, core.sizeX[0]);
      Arrays.fill(core.sizeY, core.sizeY[0]);
      Arrays.fill(core.sizeC, core.sizeC[0] == 0 ? 1 : core.sizeC[0]);
      Arrays.fill(core.sizeZ, core.sizeZ[0] == 0 ? 1 : core.sizeZ[0]);
      Arrays.fill(core.sizeT, core.sizeT[0] == 0 ? 1 : core.sizeT[0]);
      Arrays.fill(core.imageCount, core.imageCount[0]);
      Arrays.fill(core.pixelType, core.pixelType[0]);

      if (core.sizeC[0] == 0) Arrays.fill(core.sizeC, 1);
      Arrays.fill(core.currentOrder, "XYCZT");
      Arrays.fill(core.rgb, core.sizeC[0] > 1);

      adjustImageCount = false;
      for (int i=0; i<offsets.length; i++) {
        for (int j=1; j<core.imageCount[i]; j++) {
          if (offsets[i][j] < offsets[i][j - 1]) {
            adjustImageCount = true;
            break;
          }
        }
      }

      if (core.sizeC[0] > 1 &&
        core.imageCount[0] > core.sizeT[0] * core.sizeZ[0])
      {
        if (adjustImageCount) {
          int n = numValidPlanes / core.sizeT.length;
          Arrays.fill(core.sizeT, n == 0 ? 1 : n);
        }
        Arrays.fill(core.imageCount, core.sizeT[0] * core.sizeZ[0]);
      }
      Arrays.fill(core.littleEndian, true);
      Arrays.fill(core.interleaved, true);
      Arrays.fill(core.indexed, false);
      Arrays.fill(core.falseColor, false);
      Arrays.fill(core.metadataComplete, true);

      for (int i=0; i<offsets.length; i++) {
        for (int j=0; j<offsets[i].length; j++) {
          if (offsets[i][j] == 0 && core.sizeT[i] > j) {
            core.sizeT[i] = j;
            core.imageCount[i] = core.sizeZ[i] * core.sizeT[i];
            break;
          }
        }
      }

      MetadataStore store =
        new FilterMetadata(getMetadataStore(), isMetadataFiltered());
      MetadataTools.populatePixels(store, this);
      for (int i=0; i<core.sizeC.length; i++) {
        store.setImageName("Series " + i, i);
        store.setImageCreationDate(
          DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), i);
        // CTR CHECK
//        for (int j=0; j<core.sizeC[i]; j++) {
//          store.setLogicalChannel(j, null, null, null, null, null, null, null,
//            null, null, null, null, null, null, null, null, null, null, null,
//            null, null, null, null, null, new Integer(i));
//        }
      }

      return;
    }
    else in.seek(0);

    isJPEG = true;

    status("Calculating image offsets");

    Vector vs = new Vector();

    long pos = in.getFilePointer();
    boolean lastBoxFound = false;
    int length = 0;
    int box = 0;

    while (!lastBoxFound) {
      pos = in.getFilePointer();
      length = in.readInt();
      if (pos + length >= in.length() || length == 0) lastBoxFound = true;
      box = in.readInt();
      pos = in.getFilePointer();
      length -= 8;

      if (box == 0x6a703263) {
        vs.add(new Long(in.getFilePointer()));
      }
      if (!lastBoxFound) in.seek(pos + length);
    }

    status("Finding XML metadata");

    // read XML metadata from the end of the file

    in.seek(((Long) vs.get(vs.size() - 1)).longValue());

    boolean found = false;
    long off = -1;
    byte[] buf = new byte[2048];
    while (!found && in.getFilePointer() < in.length()) {
      int read = 0;
      if (in.getFilePointer() == ((Long) vs.get(vs.size() - 1)).longValue()) {
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
      in.seek(off + 5);
      byte[] b = new byte[(int) (in.length() - off - 5)];
      in.readFully(b);
      String xml = new String(b);

      // assume that this XML string will be malformed, since that's how both
      // sample files are; this means we need to manually parse it :-(

      // strip out binary data at the end - this is irrelevant for our purposes
      xml = xml.substring(0, xml.lastIndexOf("</MetadataSeq>") + 14);

      // strip out all comments
      xml = xml.replaceAll("<!--*-->", "");

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
                int end = s.indexOf("\"", eq + 2);
                if (eq < 0 || end < 0) break;
                String key = s.substring(0, eq).trim();
                String value = s.substring(eq + 2, end).trim();

                // strip out the data types
                if (key.indexOf("runtype") == -1) {
                  if (prefix.startsWith("Metadata_V1.2")) {
                    prefix = "";
                  }
                  String effectiveKey = prefix + " " + pre + " " + key;
                  if (effectiveKey.endsWith("dTimeMSec value")) {
                    long v = (long) Double.parseDouble(value);
                    if (!ts.contains(new Long(v))) {
                      ts.add(new Long(v));
                    }
                  }
                  else if (effectiveKey.endsWith("dZPos value")) {
                    long v = (long) Double.parseDouble(value);
                    if (!zs.contains(new Long(v))) {
                      zs.add(new Long(v));
                    }
                  }
                  else if (effectiveKey.endsWith("uiComp value")) {
                    if (core.sizeC[0] == 0) {
                      core.sizeC[0] = Integer.parseInt(value);
                    }
                  }
                  else if (effectiveKey.endsWith("uiCount value")) {
                    if (core.sizeT[0] == 0) {
                      core.sizeT[0] = Integer.parseInt(value);
                    }
                  }
                  else if (effectiveKey.endsWith("TextInfoItem Text")) {
                    value = value.replaceAll("&#x000d;&#x000a;", "\n");
                    StringTokenizer tokens = new StringTokenizer(value, "\n");
                    while (tokens.hasMoreTokens()) {
                      String t = tokens.nextToken().trim();
                      if (t.startsWith("Dimensions:")) {
                        t = t.substring(11);
                        StringTokenizer dims = new StringTokenizer(t, " x ");
                        while (dims.hasMoreTokens()) {
                          String dim = dims.nextToken().trim();
                          int idx = dim.indexOf("(");
                          int v = Integer.parseInt(dim.substring(idx + 1,
                            dim.indexOf(")", idx)));
                          if (dim.startsWith("XY")) {
                            numSeries = v;
                            if (numSeries > 1) {
                              int x = core.sizeX[0];
                              int y = core.sizeY[0];
                              int z = core.sizeZ[0];
                              int tSize = core.sizeT[0];
                              int c = core.sizeC[0];
                              core = new CoreMetadata(numSeries);
                              Arrays.fill(core.sizeX, x);
                              Arrays.fill(core.sizeY, y);
                              Arrays.fill(core.sizeZ, z);
                              Arrays.fill(core.sizeC, c);
                              Arrays.fill(core.sizeT, tSize);
                            }
                          }
                          else if (dim.startsWith("T")) {
                            Arrays.fill(core.sizeT, v);
                          }
                          else if (dim.startsWith("Z")) {
                            Arrays.fill(core.sizeZ, v);
                          }
                          else {
                            Arrays.fill(core.sizeC, v);
                          }
                        }

                        if (core.sizeZ[0] == 0) Arrays.fill(core.sizeZ, 1);
                        if (core.sizeC[0] == 0) Arrays.fill(core.sizeC, 1);
                        if (core.sizeT[0] == 0) Arrays.fill(core.sizeT, 1);

                        int count =
                          core.sizeZ[0] * core.sizeC[0] * core.sizeT[0];
                        Arrays.fill(core.imageCount, count);
                      }
                    }
                  }
                  parseKeyAndValue(effectiveKey, value);
                }
                s = s.substring(s.indexOf("\"", eq + 2) + 1);
              }
            }
          }
        }
      }
      b = null;
      xml = null;
      st = null;
    }

    status("Populating metadata");
    if (core.imageCount[0] == 0) {
      core.sizeZ[0] = zs.size() == 0 ? 1 : zs.size();
      core.sizeT[0] = ts.size() == 0 ? 1 : ts.size();
      core.sizeC[0] = (vs.size() + 1) / (core.sizeT[0] * core.sizeZ[0]);
      core.imageCount[0] = vs.size();
      while (core.imageCount[0] % core.sizeC[0] != 0) core.imageCount[0]--;
      while (core.sizeC[0] * core.sizeZ[0] * core.sizeT[0] > core.imageCount[0])
      {
        if (core.sizeZ[0] < core.sizeT[0]) core.sizeT[0]--;
        else core.sizeZ[0]--;
      }
    }

    if (core.sizeC[0] * core.sizeZ[0] * core.sizeT[0] != core.imageCount[0]) {
      core.sizeZ[0] = zs.size();
      core.sizeT[0] = ts.size();
      core.imageCount[0] = core.sizeC[0] * core.sizeZ[0] * core.sizeT[0];
    }

    if (core.imageCount[0] == 0) core.imageCount[0] = 1;
    if (core.sizeZ[0] == 0) core.sizeZ[0] = 1;
    if (core.sizeC[0] == 0) core.sizeC[0] = 1;
    if (core.sizeT[0] == 0) core.sizeT[0] = 1;

    Arrays.fill(core.sizeZ, core.sizeZ[0]);
    Arrays.fill(core.sizeT, core.sizeT[0]);
    Arrays.fill(core.imageCount, core.imageCount[0]);
    Arrays.fill(core.currentOrder, "XYCZT");

    core.pixelType[0] = FormatTools.UINT8;
    offsets = new long[1][2];
    offsets[0][0] = ((Long) vs.get(0)).longValue();
    if (offsets[0].length > 1 && vs.size() > 1) {
      offsets[0][1] = ((Long) vs.get(1)).longValue();
    }

    in.seek(offsets[0][0]);

    long len = 0 < core.imageCount[0] - 1 ? offsets[0][1] -
      offsets[0][0] : in.length() - offsets[0][0];

    byte[] b = new byte[(int) len];
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

    Arrays.fill(core.sizeX, img.getWidth());
    Arrays.fill(core.sizeY, img.getHeight());
    if (core.sizeC[0] == 0) core.sizeC[0] = 1;
    int numBands = img.getRaster().getNumBands();
    int c = numBands > 1 ? numBands : core.sizeC[0];
    Arrays.fill(core.sizeC, c);
    Arrays.fill(core.rgb, numBands > 1);
    Arrays.fill(core.pixelType, ImageTools.getPixelType(img));

    if (!core.rgb[0] && core.imageCount[0] == 1) {
      Arrays.fill(core.sizeC, 1);
    }

    if (vs.size() < core.imageCount[0]) {
      Arrays.fill(core.imageCount, vs.size());
    }

    if (numSeries == 0) numSeries = 1;
    offsets = new long[numSeries][core.imageCount[0]];

    for (int i=0; i<core.sizeT[0]; i++) {
      for (int j=0; j<numSeries; j++) {
        for (int q=0; q<core.sizeZ[0]; q++) {
          for (int k=0; k<getEffectiveSizeC(); k++) {
            offsets[j][i*core.sizeZ[0]*getEffectiveSizeC() +
              q*getEffectiveSizeC() + k] = ((Long) vs.remove(0)).longValue();
          }
        }
      }
    }

    Arrays.fill(core.interleaved, false);
    Arrays.fill(core.littleEndian, false);
    Arrays.fill(core.metadataComplete, true);

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);
    for (int i=0; i<numSeries; i++) {
      store.setImageName(currentId, i);

      store.setDimensionsPhysicalSizeX(new Float(pixelSizeX), i, 0);
      store.setDimensionsPhysicalSizeY(new Float(pixelSizeY), i, 0);
      store.setDimensionsPhysicalSizeZ(new Float(pixelSizeZ), i, 0);
      // CTR CHECK
//      for (int j=0; j<core.sizeC[0]; j++) {
//        store.setLogicalChannel(j, null, null, null, null, null, null, null,
//          null, null, null, null, null, null, null, null, null, null, null,
//          null, null, null, null, null, ii);
//      }
    }

    String prefix = "MetadataSeq _SEQUENCE_INDEX=\"0\" ";

    /*
    // populate DetectorSettings
    if (gain != null) store.setDetectorSettingsGain(new Float(gain), 0, 0);

    // populate Detector
    // CTR CHECK
    if (voltage != null) store.setDetectorVoltage(new Float(voltage), 0, 0);

    // populate Objective
    // CTR CHECK
    if (na != null) store.setObjectiveLensNA(new Float(na), 0, 0);
    if (mag != null) {
      store.setObjectiveCalibratedMagnification(new Float(mag), 0, 0);
    }
    */
  }

  // -- Helper class --

  /** SAX handler for parsing XML. */
  class ND2Handler extends DefaultHandler {
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      if (qName.equals("uiWidth")) {
        core.sizeX[0] = Integer.parseInt(attributes.getValue("value"));
      }
      else if (qName.equals("uiWidthBytes")) {
        int bytes =
          Integer.parseInt(attributes.getValue("value")) / core.sizeX[0];
        switch (bytes) {
          case 2:
            core.pixelType[0] = FormatTools.UINT16;
            break;
          case 4:
            core.pixelType[0] = FormatTools.UINT32;
            break;
          default: core.pixelType[0] = FormatTools.UINT8;
        }
      }
      else if (qName.startsWith("item_")) {
        int v = Integer.parseInt(qName.substring(qName.indexOf("_") + 1));
        if (v == numSeries) numSeries++;
      }
      else if (qName.equals("uiComp")) {
        core.sizeC[0] = Integer.parseInt(attributes.getValue("value"));
      }
      else if (qName.equals("uiBpcInMemory")) {
        if (attributes.getValue("value") == null) return;
        int bits = Integer.parseInt(attributes.getValue("value"));
        int bytes = bits / 8;
        switch (bytes) {
          case 1:
            core.pixelType[0] = FormatTools.UINT8;
            break;
          case 2:
            core.pixelType[0] = FormatTools.UINT16;
            break;
          case 4:
            core.pixelType[0] = FormatTools.UINT32;
            break;
          default: core.pixelType[0] = FormatTools.UINT8;
        }
        parseKeyAndValue(qName, attributes.getValue("value"));
      }
      else if (qName.equals("uiHeight")) {
        core.sizeY[0] = Integer.parseInt(attributes.getValue("value"));
      }
      else if (qName.equals("uiCount")) {
        int n = Integer.parseInt(attributes.getValue("value"));
        if (core.imageCount[0] == 0) {
          core.imageCount[0] = n;
          core.sizeT[0] = n;
          core.sizeZ[0] = 1;
        }
      }
      else if (qName.equals("uiSequenceCount")) {
        int n = Integer.parseInt(attributes.getValue("value"));
        if (n > 0 && (core.imageCount[0] == 0 || core.sizeT[0] == 0 ||
          n < core.imageCount[0]))
        {
          core.imageCount[0] = n;
          core.sizeT[0] = n;
          core.sizeZ[0] = 1;
        }
      }
      else if (qName.startsWith("TextInfoItem")) {
        String value = attributes.getValue("value");
        if (value.indexOf("Dimensions") != -1) {
          int ndx = value.indexOf("Dimensions");
          value = value.substring(ndx + 11, value.indexOf("\n", ndx)).trim();
          StringTokenizer st = new StringTokenizer(value, " x ");
          while (st.hasMoreTokens()) {
            String token = st.nextToken().trim();
            ndx = token.indexOf("(") + 1;
            if (ndx == 0) continue;
            int v = Integer.parseInt(token.substring(ndx, token.indexOf(")")));
            if (token.startsWith("T")) {
              core.sizeT[0] = v;
            }
            else if (token.startsWith("Z")) core.sizeZ[0] = v;
            else if (!token.startsWith("XY")) core.sizeC[0] = v;
          }
          if (core.sizeZ[0] == 0) core.sizeZ[0] = 1;
          if (core.sizeC[0] == 0) core.sizeC[0] = 1;
          if (core.sizeT[0] == 0) {
            core.sizeT[0] = 1;
          }
          core.imageCount[0] = core.sizeZ[0] * core.sizeC[0] * core.sizeT[0];
        }
      }
      else if (qName.equals("dCompressionParam")) {
        int v = Integer.parseInt(attributes.getValue("value"));
        isLossless = v > 0;
        parseKeyAndValue(qName, attributes.getValue("value"));
      }
      else {
        parseKeyAndValue(qName, attributes.getValue("value"));
      }
    }
  }

  // -- Helper methods --

  private void parseKeyAndValue(String key, String value) {
    addMeta(key, value);
    if (key.equals("CalibrationSeq _SEQUENCE_INDEX=\"0\" dCalibration value")) {
      pixelSizeX = Float.parseFloat(value);
      pixelSizeY = pixelSizeX;
    }
    else if (key.equals("CalibrationSeq _SEQUENCE_INDEX=\"0\" dAspect value")) {
      pixelSizeZ = Float.parseFloat(value);
    }
    else if (key.equals("MetadataSeq _SEQUENCE_INDEX=\"0\" dGain value")) {
      gain = value;
    }
    else if (key.equals("MetadataSeq _SEQUENCE_INDEX=\"0\" dLampVoltage value"))
    {
      voltage = value;
    }
    else if (key.equals("MetadataSeq " +
      "_SEQUENCE_INDEX=\"0\" dObjectiveMag value"))
    {
      mag = value;
    }
    else if (key.equals("MetadataSeq _SEQUENCE_INDEX=\"0\" dObjectiveNA value"))
    {
      na = value;
    }
  }

}
