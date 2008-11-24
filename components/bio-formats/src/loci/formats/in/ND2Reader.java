//
// ND2Reader.java
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

import java.awt.Point;
import java.io.*;
import java.util.*;
import loci.common.*;
import loci.formats.*;
import loci.formats.codec.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * ND2Reader is the file format reader for Nikon ND2 files.
 * The JAI ImageIO library is required to use this reader; it is available from
 * http://jai-imageio.dev.java.net. Note that JAI ImageIO is bundled with a
 * version of the JJ2000 library, so it is important that either:
 * (1) the JJ2000 jar file is *not* in the classpath; or
 * (2) the JAI jar file precedes JJ2000 in the classpath.
 *
 * Also note that there is alternate ND2 reader for Windows
 * (see LegacyND2Reader.java) that requires a native library. If that native
 * library is installed, then ND2Reader's logic will be bypassed.
 *
 * Thanks to Tom Caswell for additions to the ND2 metadata parsing logic.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/ND2Reader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/ND2Reader.java">SVN</a></dd></dl>
 */
public class ND2Reader extends FormatReader {

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
  private Vector tsT = new Vector();

  private int numSeries;

  private float pixelSizeX, pixelSizeY, pixelSizeZ;
  private String gain, voltage, mag, na;

  private LegacyND2Reader legacyReader;
  private boolean legacy = false;

  // -- Constructor --

  /** Constructs a new ND2 reader. */
  public ND2Reader() {
    super("Nikon ND2", new String[] {"nd2", "jp2"});
    blockCheckLen = 8;
  }

  // -- ND2Reader API methods --

  public void setLegacy(boolean legacy) {
    this.legacy = legacy;
    if (this.legacy) {
      if (legacyReader == null) legacyReader = new LegacyND2Reader();
    }
    else legacyReader = null;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    if (!FormatTools.validStream(stream, blockCheckLen, false)) return false;
    stream.seek(4);
    return stream.readInt() == 0x6a502020;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (legacy) return legacyReader.openBytes(no, buf, x, y, w, h);
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length, w, h);

    in.seek(offsets[series][no]);

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int pixel = bpp * getRGBChannelCount();

    long maxFP = no == getImageCount() - 1 ?
      in.length() : offsets[series][no + 1];

    if (isJPEG) {
      byte[] tmp = new JPEG2000Codec().decompress(in, new Object[] {
        new Boolean(isLittleEndian()),
        new Boolean(isInterleaved()), new Long(maxFP)});
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
      byte[] t = new ZlibCodec().decompress(in, null);

      for (int row=0; row<h; row++) {
        int offset = (row + y) * effectiveX * pixel + x * pixel;
        if (offset < t.length) {
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

  /* @see loci.formats.IFormatReader#setNormalized(boolean) */
  public void setNormalized(boolean normalize) {
    super.setNormalized(normalize);
    if (legacy) legacyReader.setNormalized(normalize);
  }

  /* @see loci.formats.IFormatReader#setMetadataCollected(boolean) */
  public void setMetadataCollected(boolean collect) {
    super.setMetadataCollected(collect);
    if (legacy) legacyReader.setMetadataCollected(collect);
  }

  /* @see loci.formats.IFormatReader#setOriginalMetadataPopulated(boolean) */
  public void setOriginalMetadataPopulated(boolean populate) {
    super.setOriginalMetadataPopulated(populate);
    if (legacy) legacyReader.setOriginalMetadataPopulated(populate);
  }

  /* @see loci.formats.IFormatReader#setMetadataFiltered(boolean) */
  public void setMetadataFiltered(boolean filter) {
    super.setMetadataFiltered(filter);
    if (legacy) legacyReader.setMetadataFiltered(filter);
  }

  /* @see loci.formats.IFormatReader#setMetadataStore(MetadataStore) */
  public void setMetadataStore(MetadataStore store) {
    super.setMetadataStore(store);
    if (legacy) legacyReader.setMetadataStore(store);
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();

    if (legacyReader != null) legacyReader.close();
    legacyReader = null;
    legacy = false;

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
    super.initFile(id);

    if (legacy) {
      legacyReader.close();
      legacyReader.setId(id);
      core = legacyReader.getCoreMetadata();
      metadataStore = legacyReader.getMetadataStore();
      return;
    }

    in = new RandomAccessStream(id);

    if (in.read() == -38 && in.read() == -50) {
      // newer version of ND2 - doesn't use JPEG2000

      isJPEG = false;
      in.seek(0);
      in.order(true);

      // assemble offsets to each block

      Vector imageOffsets = new Vector();
      Vector imageLengths = new Vector();
      Vector xmlOffsets = new Vector();
      Vector xmlLengths = new Vector();
      Vector customDataOffsets = new Vector();
      Vector customDataLengths = new Vector();

      while (in.getFilePointer() < in.length() && in.getFilePointer() >= 0) {
        while (in.read() != -38);
        in.skipBytes(3);

        int lenOne = in.readInt();
        int lenTwo = in.readInt();
        int len = lenOne + lenTwo;
        in.skipBytes(4);

        String blockType = in.readString(12);
        long fp = in.getFilePointer() - 12;
        in.skipBytes(len - 12);

        if (blockType.startsWith("ImageDataSeq")) {
          imageOffsets.add(new Long(fp));
          imageLengths.add(new Point(lenOne, lenTwo));
        }
        else if (blockType.startsWith("Image")) {
          xmlOffsets.add(new Long(fp));
          xmlLengths.add(new Point(lenOne, lenTwo));
        }
        else if (blockType.startsWith("CustomData|A")) {
          customDataOffsets.add(new Long(fp));
          customDataLengths.add(new Point(lenOne, lenTwo));
        }
      }

      // parse XML blocks

      DefaultHandler handler = new ND2Handler();
      ByteVector xml = new ByteVector();

      for (int i=0; i<xmlOffsets.size(); i++) {
        long offset = ((Long) xmlOffsets.get(i)).longValue();
        Point p = (Point) xmlLengths.get(i);
        int length = (int) (p.x + p.y);

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
      DataTools.parseXML(xmlString, handler);

      // adjust SizeT, if necessary
      long planeSize = getSizeX() * getSizeY() *
        FormatTools.getBytesPerPixel(getPixelType()) * getSizeC();
      if (planeSize * getImageCount() * getSeriesCount() >=
        in.length() && !isLossless)
      {
        int approxPlanes = (int) (in.length() / planeSize);
        core[0].sizeT = approxPlanes / getSeriesCount();
        if (getSizeT() * getSeriesCount() < approxPlanes) {
          core[0].sizeT++;
        }
        core[0].imageCount = getSizeT();
        core[0].sizeZ = 1;
      }

      // read first CustomData block

      if (customDataOffsets.size() > 0) {
        in.seek(((Long) customDataOffsets.get(0)).longValue());
        Point p = (Point) customDataLengths.get(0);
        int len = (int) (p.x + p.y);
        byte[] b = new byte[len];
        in.read(b);

        // the acqtimecache is a undeliniated stream of doubles
        int off = 0;
        for (int i=0; i<len; i++) {
          char c = (char) b[i];
          if ((off == 0 && c == '!')) off = i + 1;
        }
        for (int j = off; j<len; j+=8) {
          double time = DataTools.bytesToDouble(b, j, 8, true);
          tsT.add(new Double(time));
          addMeta("timestamp " + (tsT.size() - 1), time);
        }
        b = null;
      }

      // rearrange image data offsets

      if (numSeries == 0) numSeries = 1;

      offsets = new long[numSeries][getImageCount()];

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

      for (int i=0; i<imageOffsets.size(); i++) {
        long offset = ((Long) imageOffsets.get(i)).longValue();
        Point p = (Point) imageLengths.get(i);
        int length = (int) (p.x + p.y);

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

        int seriesIndex = ndx / (getSizeT() * getSizeZ());
        int plane = ndx % (getSizeT() * getSizeZ());

        offsets[seriesIndex][plane] = offset + p.x + 8;
        b = null;
      }

      Vector tmpOffsets = new Vector();
      for (int i=0; i<offsets.length; i++) {
        if (offsets[i][0] > 0) tmpOffsets.add(offsets[i]);
      }

      offsets = new long[tmpOffsets.size()][];
      for (int i=0; i<tmpOffsets.size(); i++) {
        offsets[i] = (long[]) tmpOffsets.get(i);
      }

      if (offsets.length != getSeriesCount()) {
        int x = getSizeX();
        int y = getSizeY();
        int c = getSizeC();
        int pixelType = getPixelType();
        boolean rgb = isRGB();
        core = new CoreMetadata[offsets.length];
        for (int i=0; i<offsets.length; i++) {
          core[i] = new CoreMetadata();
          core[i].sizeX = x;
          core[i].sizeY = y;
          core[i].sizeC = c == 0 ? 1 : c;
          core[i].pixelType = pixelType;
          core[i].rgb = rgb;
          core[i].sizeZ = 1;

          int invalid = 0;
          for (int q=0; q<offsets[i].length; q++) {
            if (offsets[i][q] == 0) invalid++;
          }
          core[i].sizeT = (offsets[i].length - invalid) / core[i].sizeC;
          if (core[i].sizeT == 0) core[i].sizeT = 1;
          core[i].imageCount = offsets[i].length - invalid;
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
        }
      }

      for (int i=0; i<getSeriesCount(); i++) {
        core[i].dimensionOrder = "XYCZT";
        core[i].rgb = getSizeC() > 1;
        core[i].littleEndian = true;
        core[i].interleaved = true;
        core[i].indexed = false;
        core[i].falseColor = false;
        core[i].metadataComplete = true;
      }

      adjustImageCount = false;

      for (int i=0; i<offsets.length; i++) {
        for (int j=1; j<core[i].imageCount; j++) {
          if (offsets[i][j] < offsets[i][j - 1]) {
            adjustImageCount = true;
            break;
          }
        }
      }

      if (getSizeC() > 1) {
        if (adjustImageCount) {
          int n = imageOffsets.size() / getSeriesCount();
          for (int i=0; i<getSeriesCount(); i++) {
            core[i].sizeT = n == 0 ? 1 : n;
          }
        }
        for (int i=0; i<getSeriesCount(); i++) {
          core[i].imageCount = getSizeT() * getSizeZ();
        }
      }

      for (int i=0; i<getSeriesCount(); i++) {
        core[i].imageCount = core[i].sizeZ * core[i].sizeT;
        if (!core[i].rgb) core[i].imageCount *= core[i].sizeC;
      }

      MetadataStore store =
        new FilterMetadata(getMetadataStore(), isMetadataFiltered());
      MetadataTools.populatePixels(store, this);

      // populate Image data
      for (int i=0; i<getSeriesCount(); i++) {
        store.setImageName("Series " + i, i);
        MetadataTools.setDefaultCreationDate(store, id, i);
      }

      // populate PlaneTiming data
      for (int i=0; i<getSeriesCount(); i++) {
        if (tsT.size() > 0) {
          setSeries(i);
          for (int n=0; n<getImageCount(); n++) {
            int[] coords = getZCTCoords(n);
            float stamp = ((Double) tsT.get(coords[2])).floatValue();
            store.setPlaneTimingDeltaT(new Float(stamp), i, 0, n);
          }
        }
      }
      setSeries(0);

      return;
    }
    else in.seek(0);

    // older version of ND2 - uses JPEG 2000 compression

    isJPEG = true;

    status("Calculating image offsets");

    Vector vs = new Vector();

    long pos = in.getFilePointer();
    boolean lastBoxFound = false;
    int length = 0;
    int box = 0;

    // assemble offsets to each plane

    int x = 0, y = 0, c = 0, type = 0;

    while (!lastBoxFound) {
      pos = in.getFilePointer();
      length = in.readInt();
      if (pos + length >= in.length() || length == 0) lastBoxFound = true;
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

    in.seek(((Long) vs.get(vs.size() - 1)).longValue());

    boolean found = false;
    long off = -1;
    byte[] buf = new byte[8192];
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
      String xml = in.readString((int) (in.length() - in.getFilePointer()));
      StringTokenizer st = new StringTokenizer(xml, "\n");
      StringBuffer sb = new StringBuffer();

      // stored XML doesn't have a root node - add one, so that we can parse
      // using SAX

      sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><NIKON>");

      while (st.hasMoreTokens()) {
        String token = st.nextToken().trim();
        if (token.indexOf("<!--") != -1 || token.indexOf("VCAL") != -1) {
          continue;
        }
        if (token.startsWith("<")) sb.append(token);
      }

      sb.append("</NIKON>");
      xml = sb.toString();

      status("Finished assembling XML string");

      DefaultHandler handler = new ND2Handler();

      // strip out invalid characters
      int offset = 0;
      byte[] b = xml.getBytes();
      int len = b.length;
      for (int i=0; i<len; i++) {
        char ch = (char) b[i];
        if (offset == 0 && ch == '!') offset = i + 1;

        if (Character.isISOControl(ch) || !Character.isDefined(ch)) {
          b[i] = (byte) ' ';
        }
      }
      DataTools.parseXML(new String(b, offset, len - offset), handler);
      xml = null;
    }

    status("Populating metadata");
    if (getImageCount() == 0) {
      core[0].sizeZ = zs.size() == 0 ? vs.size() : zs.size();
      core[0].sizeT = ts.size() == 0 ? 1 : ts.size();
      core[0].sizeC = (vs.size() + 1) / (getSizeT() * getSizeZ());
      core[0].imageCount = vs.size();
      while (getImageCount() % getSizeC() != 0) core[0].imageCount--;
      while (getSizeC() * getSizeZ() * getSizeT() > getImageCount()) {
        if (getSizeZ() < getSizeT()) core[0].sizeT--;
        else core[0].sizeZ--;
      }
    }

    if (getSizeC() * getSizeZ() * getSizeT() != getImageCount()) {
      core[0].sizeZ = zs.size();
      core[0].sizeT = ts.size();
      core[0].imageCount = getSizeC() * getSizeZ() * getSizeT();
      if (vs.size() > getImageCount()) {
        core[0].sizeT = vs.size() / (isRGB() ? 1 : getSizeC());
        core[0].imageCount = getSizeZ() * getSizeT();
        if (!isRGB()) core[0].imageCount *= getSizeC();
      }
    }

    if (getImageCount() == 0) core[0].imageCount = 1;
    if (getSizeZ() == 0) core[0].sizeZ = 1;
    if (getSizeC() == 0) core[0].sizeC = 1;
    if (getSizeT() == 0) core[0].sizeT = 1;

    for (int i=0; i<getSeriesCount(); i++) {
      core[i].sizeZ = getSizeZ();
      core[i].sizeT = getSizeT();
      core[i].imageCount = getImageCount();
      core[i].dimensionOrder = "XYCZT";
      core[i].sizeX = x;
      core[i].sizeY = y;
      core[i].interleaved = false;
      core[i].littleEndian = false;
      core[i].metadataComplete = true;
    }

    core[0].pixelType = FormatTools.UINT8;
    offsets = new long[1][2];
    offsets[0][0] = ((Long) vs.get(0)).longValue();
    if (offsets[0].length > 1 && vs.size() > 1) {
      offsets[0][1] = ((Long) vs.get(1)).longValue();
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

    if (isRGB() && getImageCount() > getSizeZ() * getSizeT()) {
      if (getSizeZ() > 1) core[0].sizeZ *= getSizeC();
      else core[0].sizeT *= getSizeC();
      for (int i=0; i<getSeriesCount(); i++) {
        core[i].sizeT = getSizeT();
        core[i].sizeZ = getSizeZ();
      }
    }

    if (vs.size() < getImageCount()) {
      for (int i=0; i<getSeriesCount(); i++) {
        core[i].imageCount = vs.size();
      }
    }

    if (numSeries == 0) numSeries = 1;
    offsets = new long[numSeries][getImageCount()];

    for (int i=0; i<getSizeT(); i++) {
      for (int j=0; j<numSeries; j++) {
        for (int q=0; q<getSizeZ(); q++) {
          for (int k=0; k<getEffectiveSizeC(); k++) {
            offsets[j][i*getSizeZ()*getEffectiveSizeC() +
              q*getEffectiveSizeC() + k] = ((Long) vs.remove(0)).longValue();
          }
        }
      }
    }

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);
    // populate Image data
    for (int i=0; i<getSeriesCount(); i++) {
      store.setImageName(currentId, i);
    }

    // populate Dimensions data
    for (int i=0; i<getSeriesCount(); i++) {
      store.setDimensionsPhysicalSizeX(new Float(pixelSizeX), i, 0);
      store.setDimensionsPhysicalSizeY(new Float(pixelSizeY), i, 0);
      store.setDimensionsPhysicalSizeZ(new Float(pixelSizeZ), i, 0);
    }

    // populate DetectorSettings
    if (gain != null) store.setDetectorSettingsGain(new Float(gain), 0, 0);
    if (voltage != null) {
      store.setDetectorSettingsVoltage(new Float(voltage), 0, 0);
    }

    // link DetectorSettings to an actual Detector
    store.setDetectorID("Detector:0", 0, 0);
    store.setDetectorSettingsDetector("Detector:0", 0, 0);

    // populate Objective
    if (na != null) store.setObjectiveLensNA(new Float(na), 0, 0);
    if (mag != null) {
      store.setObjectiveCalibratedMagnification(new Float(mag), 0, 0);
    }
  }

  // -- Helper class --

  /** SAX handler for parsing XML. */
  class ND2Handler extends DefaultHandler {
    private String prefix = null;

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
      if (qName.equals("uiWidth")) {
        core[0].sizeX = Integer.parseInt(attributes.getValue("value"));
      }
      else if (qName.equals("uiWidthBytes")) {
        int bytes = Integer.parseInt(attributes.getValue("value")) / getSizeX();
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
      }
      else if (qName.startsWith("item_")) {
        int v = Integer.parseInt(qName.substring(qName.indexOf("_") + 1));
        if (v == numSeries) numSeries++;
      }
      else if (qName.equals("uiCompCount")) {
        int v = Integer.parseInt(attributes.getValue("value"));
        core[0].sizeC = (int) Math.max(getSizeC(), v);
      }
      else if (qName.equals("uiBpcInMemory")) {
        if (attributes.getValue("value") == null) return;
        int bits = Integer.parseInt(attributes.getValue("value"));
        int bytes = bits / 8;
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
        parseKeyAndValue(qName, attributes.getValue("value"));
      }
      else if (qName.equals("uiHeight")) {
        core[0].sizeY = Integer.parseInt(attributes.getValue("value"));
      }
      else if (qName.equals("uiCount")) {
        int n = Integer.parseInt(attributes.getValue("value"));
        if (getImageCount() == 0) {
          core[0].imageCount = n;
          core[0].sizeT = n;
          core[0].sizeZ = 1;
        }
      }
      else if (qName.equals("uiSequenceCount")) {
        int n = Integer.parseInt(attributes.getValue("value"));
        if (n > 0 && (getImageCount() == 0 || getSizeT() == 0 ||
          n < getImageCount()))
        {
          core[0].imageCount = n;
          core[0].sizeT = n;
          core[0].sizeZ = 1;
        }
      }
      else if (qName.startsWith("TextInfo")) {
        parseKeyAndValue(qName, attributes.getValue("Text"));
        parseKeyAndValue(qName, attributes.getValue("value"));
      }
      else if (qName.equals("dCompressionParam")) {
        int v = Integer.parseInt(attributes.getValue("value"));
        isLossless = v > 0;
        parseKeyAndValue(qName, attributes.getValue("value"));
      }
      else if (qName.equals("CalibrationSeq") || qName.equals("MetadataSeq")) {
        prefix = qName + " " + attributes.getValue("_SEQUENCE_INDEX");
      }
      else {
        StringBuffer sb = new StringBuffer();
        if (prefix != null) {
          sb.append(prefix);
          sb.append(" ");
        }
        sb.append(qName);
        parseKeyAndValue(sb.toString(), attributes.getValue("value"));
      }
    }
  }

  // -- Helper methods --

  private void parseKeyAndValue(String key, String value) {
    if (key == null || value == null) return;
    addMeta(key, value);
    if (key.endsWith("dCalibration")) {
      pixelSizeX = Float.parseFloat(value);
      pixelSizeY = pixelSizeX;
    }
    else if (key.endsWith("dAspect")) pixelSizeZ = Float.parseFloat(value);
    else if (key.endsWith("dGain")) gain = value;
    else if (key.endsWith("dLampVoltage")) voltage = value;
    else if (key.endsWith("dObjectiveMag")) mag = value;
    else if (key.endsWith("dObjectiveNA")) na = value;
    else if (key.endsWith("dTimeMSec")) {
      long v = (long) Double.parseDouble(value);
      if (!ts.contains(new Long(v))) {
        ts.add(new Long(v));
        addMeta("number of timepoints", ts.size());
      }
    }
    else if (key.endsWith("dZPos")) {
      long v = (long) Double.parseDouble(value);
      if (!zs.contains(new Long(v))) {
        zs.add(new Long(v));
      }
    }
    else if (key.endsWith("uiCount")) {
      if (getSizeT() == 0) {
        core[0].sizeT = Integer.parseInt(value);
      }
    }
    else if (key.startsWith("TextInfoItem") || key.endsWith("TextInfoItem")) {
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
                int x = getSizeX();
                int y = getSizeY();
                int z = getSizeZ();
                int tSize = getSizeT();
                int c = getSizeC();
                core = new CoreMetadata[numSeries];
                for (int i=0; i<numSeries; i++) {
                  core[i] = new CoreMetadata();
                  core[i].sizeX = x;
                  core[i].sizeY = y;
                  core[i].sizeZ = z == 0 ? 1 : z;
                  core[i].sizeC = c == 0 ? 1 : c;
                  core[i].sizeT = tSize == 0 ? 1 : tSize;
                }
              }
            }
            else if (dim.startsWith("T")) {
              for (int i=0; i<getSeriesCount(); i++) {
                core[i].sizeT = v == 0 ? 1 : v;
              }
            }
            else if (dim.startsWith("Z")) {
              for (int i=0; i<getSeriesCount(); i++) {
                core[i].sizeZ = v == 0 ? 1 : v;
              }
            }
            else {
              for (int i=0; i<getSeriesCount(); i++) {
                core[i].sizeC = v == 0 ? 1 : v;
              }
            }
          }

          int count = getSizeZ() * getSizeC() * getSizeT();
          for (int i=0; i<getSeriesCount(); i++) {
            core[i].imageCount = count;
          }
        }
      }
    }
  }

}
