/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEGCodec;
import loci.formats.codec.ZlibCodec;
import loci.formats.meta.DummyMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.services.POIService;

/**
 * ZeissZVIReader is the file format reader for Zeiss ZVI files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class ZeissZVIReader extends BaseZeissReader {

  // -- Constants --

  public static final int ZVI_MAGIC_BYTES = 0xd0cf11e0;

  private static final long ROI_SIGNATURE = 0x21fff6977547000dL;

  // -- Fields --

  protected transient POIService poi;
  protected String[] files;

  // -- Constructor --

  /** Constructs a new ZeissZVI reader. */
  public ZeissZVIReader() {
    super("Zeiss Vision Image (ZVI)", "zvi");
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 65536;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    int magic = stream.readInt();
    if (magic != ZVI_MAGIC_BYTES) return false;
    return true;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    lastPlane = no;

    if (poi == null) {
      initPOIService();
    }

    int bytes = FormatTools.getBytesPerPixel(getPixelType());
    int pixel = bytes * getRGBChannelCount();

    CodecOptions options = new CodecOptions();
    options.littleEndian = isLittleEndian();
    options.interleaved = isInterleaved();

    int index = -1;

    int[] coords = getZCTCoords(no);
    for (int q=0; q<coordinates.length; q++) {
      if (coordinates[q][0] == coords[0] && coordinates[q][1] == coords[1] &&
        coordinates[q][2] == coords[2] && coordinates[q][3] == getSeries())
      {
        index = q;
        break;
      }
    }
    LOGGER.trace("no = " + no + ", index = " + index);

    if (index < 0 || index >= imageFiles.length) {
      return buf;
    }

    RandomAccessInputStream s = poi.getDocumentStream(imageFiles[index]);
    s.seek(offsets[index]);

    int len = w * pixel;
    int row = getSizeX() * pixel;

    if (isJPEG) {
      byte[] t = new JPEGCodec().decompress(s, options);

      for (int yy=0; yy<h; yy++) {
        System.arraycopy(t, (yy + y) * row + x * pixel, buf, yy*len, len);
      }
    }
    else if (isZlib) {
      byte[] t = new ZlibCodec().decompress(s, options);
      for (int yy=0; yy<h; yy++) {
        int src = (yy + y) * row + x * pixel;
        int dest = yy * len;
        if (src + len <= t.length && dest + len <= buf.length) {
          System.arraycopy(t, src, buf, dest, len);
        }
        else break;
      }
    }
    else {
      readPlane(s, x, y, w, h, buf);
    }
    s.close();

    if (isRGB() && !isJPEG) {
      // reverse bytes in groups of 3 to account for BGR storage
      byte[] bb = new byte[bytes];
      for (int i=0; i<buf.length; i+=bpp) {
        System.arraycopy(buf, i + 2*bytes, bb, 0, bytes);
        System.arraycopy(buf, i, buf, i + 2*bytes, bytes);
        System.arraycopy(bb, 0, buf, i, bytes);
      }
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (poi != null) poi.close();
    poi = null;
    files = null;
  }

  // -- Internal FormatReader API methods --

  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    super.initFileMain(id);

    // double-check that the coordinates are valid
    // all of the image numbers must be accounted for

    Integer[] zs = zIndices.toArray(new Integer[zIndices.size()]);
    Integer[] cs = channelIndices.toArray(new Integer[channelIndices.size()]);
    Integer[] ts = timepointIndices.toArray(new Integer[timepointIndices.size()]);
    Integer[] tiles = tileIndices.toArray(new Integer[tileIndices.size()]);
    Arrays.sort(zs);
    Arrays.sort(cs);
    Arrays.sort(ts);
    Arrays.sort(tiles);

    for (int i=0; i<coordinates.length; i++) {
      coordinates[i][0] = Arrays.binarySearch(zs, coordinates[i][0]);
      coordinates[i][1] = Arrays.binarySearch(cs, coordinates[i][1]);
      coordinates[i][2] = Arrays.binarySearch(ts, coordinates[i][2]);
      coordinates[i][3] = Arrays.binarySearch(tiles, coordinates[i][3]);
      LOGGER.trace("corrected coordinate #{} = {}", i, coordinates[i]);
    }
  }

  @Override
  protected void initVars(String id) throws FormatException, IOException {
    super.initVars(id);
    initPOIService();
    countImages();
  }

  private void initPOIService() throws FormatException, IOException {
    try {
      ServiceFactory factory = new ServiceFactory();
      poi = factory.getInstance(POIService.class);
    }
    catch (DependencyException de) {
      throw new FormatException("POI library not found", de);
    }
    poi.initialize(Location.getMappedId(getCurrentFile()));
  }

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void fillMetadataPass1(MetadataStore store) throws FormatException, IOException {
    super.fillMetadataPass1(store);

    // parse each embedded file
    for (String name : files) {
      String relPath = name.substring(name.lastIndexOf(File.separator) + 1);
      if (!relPath.toUpperCase().equals("CONTENTS")) continue;
      String dirName = name.substring(0, name.lastIndexOf(File.separator));
      if (dirName.indexOf(File.separator) != -1) {
        dirName = dirName.substring(dirName.lastIndexOf(File.separator) + 1);
      }

      if (name.indexOf("Scaling") == -1 && dirName.equals("Tags")) {
        int imageNum = getImageNumber(name, -1);
        if (imageNum == -1) {
          parseTags(imageNum, name, new DummyMetadata());
        }
        else tagsToParse.add(name);
      }
      else if (dirName.equals("Shapes") && name.indexOf("Item") != -1) {
        int imageNum = getImageNumber(name, -1);
        if (imageNum != -1) {
          try {
            parseROIs(imageNum, name, store);
          }
          catch (IOException e) {
            LOGGER.debug("Could not parse all ROIs.", e);
          }
        }
      }
      else if (dirName.equals("Image") ||
          dirName.toUpperCase().indexOf("ITEM") != -1)
      {
        int imageNum = getImageNumber(dirName, getImageCount() == 1 ? 0 : -1);
        if (imageNum == -1) continue;

        // found a valid image stream
        RandomAccessInputStream s = poi.getDocumentStream(name);
        s.order(true);

        if (s.length() <= 1024) {
          s.close();
          continue;
        }

        for (int q=0; q<11; q++) {
          getNextTag(s);
        }

        s.skipBytes(2);
        int len = s.readInt() - 20;
        s.skipBytes(8);

        int zidx = s.readInt();
        int cidx = s.readInt();
        int tidx = s.readInt();
        s.skipBytes(4);
        int tileIndex = s.readInt();

        zIndices.add(zidx);
        timepointIndices.add(tidx);
        channelIndices.add(cidx);
        tileIndices.add(tileIndex);

        s.skipBytes(len - 8);

        for (int q=0; q<5; q++) {
          getNextTag(s);
        }

        s.skipBytes(4);
        core.get(0).sizeX = s.readInt();
        core.get(0).sizeY = s.readInt();
        s.skipBytes(4);

        if (bpp == 0) {
          bpp = s.readInt();
        }
        else s.skipBytes(4);
        s.skipBytes(4);

        int valid = s.readInt();

        String check = s.readString(4).trim();
        isZlib = (valid == 0 || valid == 1) && check.equals("WZL");
        isJPEG = (valid == 0 || valid == 1) && !isZlib;

        // save the offset to the pixel data

        offsets[imageNum] = (int) s.getFilePointer() - 4;

        if (isZlib) offsets[imageNum] += 8;
        coordinates[imageNum][0] = zidx;
        coordinates[imageNum][1] = cidx;
        coordinates[imageNum][2] = tidx;
        coordinates[imageNum][3] = tileIndex;
        LOGGER.trace("imageNum = {}, coordinate = {}", imageNum, coordinates[imageNum]);
        imageFiles[imageNum] = name;
        s.close();
      }
    }
  }

    @Override
    protected void fillMetadataPass3(MetadataStore store) throws FormatException, IOException {
      super.fillMetadataPass3(store);

    // calculate tile dimensions and number of tiles
    if (core.size() > 1) {
      Integer[] t = tiles.keySet().toArray(new Integer[tiles.size()]);
      Arrays.sort(t);
      final List<Integer> tmpOffsets = new ArrayList<Integer>();
      final List<String> tmpFiles = new ArrayList<String>();
      int index = 0;
      for (Integer key : t) {
        int nTiles = tiles.get(key).intValue();
        if (nTiles < getImageCount()) {
          tiles.remove(key);
        }
        else {
          for (int p=0; p<nTiles; p++) {
            tmpOffsets.add(offsets[index + p]);
            tmpFiles.add(imageFiles[index + p]);
          }
        }
        index += nTiles;
      }

      offsets = new int[tmpOffsets.size()];
      for (int i=0; i<offsets.length; i++) {
        offsets[i] = tmpOffsets.get(i).intValue();
      }
      imageFiles = tmpFiles.toArray(new String[tmpFiles.size()]);
    }
  }

    @Override
    protected void fillMetadataPass5(MetadataStore store) throws FormatException, IOException {
      super.fillMetadataPass5(store);

    for (String name : tagsToParse) {
      int imageNum = getImageNumber(name, -1);
      parseTags(imageNum, name, store);
      }
    }

  @Override
  protected void countImages() {
    // count number of images
    files = (String[]) poi.getDocumentList().toArray(new String[0]);
    Arrays.sort(files, new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        final Integer n1 = getImageNumber(o1, -1);
        final Integer n2 = getImageNumber(o2, -1);

        return n1.compareTo(n2);
      }
    });
    core.get(0).imageCount = 0;

    for (String file : files) {
      String uname = file.toUpperCase();
      uname = uname.substring(uname.indexOf(File.separator) + 1);
      if (uname.endsWith("CONTENTS") && (uname.startsWith("IMAGE") ||
          uname.indexOf("ITEM") != -1) && poi.getFileSize(file) > 1024)
      {
        int imageNumber = getImageNumber(file, 0);
        if (imageNumber >= getImageCount()) {
          core.get(0).imageCount++;
        }
      }
    }
    super.countImages();
    coordinates = new int[getSeriesCount() * getImageCount()][4];
  }

  private int getImageNumber(String dirName, int defaultNumber) {
    if (dirName.toUpperCase().indexOf("ITEM") != -1) {
      int open = dirName.indexOf('(');
      int close = dirName.indexOf(')');
      if (open < 0 || close < 0 || close < open) return defaultNumber;
      return Integer.parseInt(dirName.substring(open + 1, close));
    }
    return defaultNumber;
  }

  private String getNextTag(RandomAccessInputStream s) throws IOException {
    int type = s.readShort();
    switch (type) {
      case 0:
      case 1:
        return "";
      case 2:
        return String.valueOf(s.readShort());
      case 3:
      case 22:
      case 23:
        return String.valueOf(s.readInt());
      case 4:
        return String.valueOf(s.readFloat());
      case 5:
        return String.valueOf(s.readDouble());
      case 7:
      case 20:
      case 21:
        return String.valueOf(s.readLong());
      case 8:
      case 69:
        int len = s.readInt();
        return s.readString(len);
      case 9:
      case 13:
        s.skipBytes(16);
        return "";
      case 11:
        return String.valueOf(s.readShort()!=0);
      case 63:
      case 65:
        len = s.readInt();
        s.skipBytes(len);
        return "";
      case 66:
        len = s.readShort();
        return s.readString(len);
      default:
        long old = s.getFilePointer();
        while (s.readShort() != 3 &&
          s.getFilePointer() + 2 < s.length());
        long fp = s.getFilePointer() - 2;
        s.seek(old - 2);
        return s.readString((int) (fp - old + 2));
    }
  }

  /** Parse all of the tags in a stream. */
  private void parseTags(int image, String file, MetadataStore store)
    throws FormatException, IOException {
    ArrayList<Tag> tags = new ArrayList<Tag>();
    RandomAccessInputStream s = poi.getDocumentStream(file);
    s.order(true);

    s.seek(8);

    int count = s.readInt();


    for (int i=0; i<count; i++) {
      if (s.getFilePointer() + 2 >= s.length()) break;
      String value = DataTools.stripString(getNextTag(s));

      s.skipBytes(2);
      int tagID = s.readInt();

      s.skipBytes(6);

      tags.add(new Tag(tagID, value, Context.MAIN));
    }

    parseMainTags(image, store, tags);

    s.close();
  }

  /**
   * Parse ROI data from the given RandomAccessInputStream and store it in the
   * given MetadataStore.
   */
  private void parseROIs(int imageNum, String name, MetadataStore store)
    throws IOException {
    MetadataLevel level = getMetadataOptions().getMetadataLevel();
    if (level == MetadataLevel.MINIMUM || level == MetadataLevel.NO_OVERLAYS) {
      return;
    }

    RandomAccessInputStream s = poi.getDocumentStream(name);
    s.setEncoding("UTF-16LE");
    s.order(true);

    // scan stream for offsets to each ROI

    final List<Long> roiOffsets = new ArrayList<Long>();


    // Bytes 0x0-1 == 0x3
    // Bytes 0x2-5 == Layer version (04100010)
    // Byte 0x10 == Shape count

    s.seek(2);
    int layerversion = s.readInt();
    LOGGER.debug("LAYER@{} version={}", s.getFilePointer()-4, layerversion);

    // Layer name (assumed).  This is usually NULL in most files, so
    // we need to explicitly check whether it's a string prior to
    // parsing it.  The only file seen with a non-null string here did
    // not contain any shapes in the layer (purpose of the empty layer
    // unknown).
    String layername = null;
    {
      long tmp = s.getFilePointer();
      if (s.readShort() == 8) {
        s.seek(tmp);
        layername = parseROIString(s);
      }
      if (layername != null)
        LOGGER.debug("  Name={}", layername);
      else
        LOGGER.debug("  Name=NULL");
    }

    s.skipBytes(8);

    int roiCount = s.readShort();
    int roiFound = 0;
    LOGGER.debug("  ShapeCount@{} count={}", s.getFilePointer()-2, roiCount);

    // Add new layer for this set of shapes.
    Layer nlayer = new Layer();
    nlayer.name = layername;
    layers.add(nlayer);

    // Following signature (from sig start):
    // Bytes 0x12-15 == Shape version (0B200010)
    // Bytes 0x28-nn == Shape attributes (size is first short)

    while (roiFound < roiCount && s.getFilePointer() < s.length() - 8) {
      // find next ROI signature
      long signature = s.readLong() & 0xffffffffffffffffL;
      while (signature != ROI_SIGNATURE) {
        if (s.getFilePointer() >= s.length()) break;
        s.seek(s.getFilePointer() - 6);
        signature = s.readLong() & 0xffffffffffffffffL;
      }
      if (s.getFilePointer() >= s.length()) {
        break;
      }

      long roiOffset = s.getFilePointer() - 8;
      roiOffsets.add(roiOffset);

      LOGGER.debug("ROI@{}", roiOffset);

      // Found ROI; now fill out the shape details and add to the
      // layer.

      s.seek(roiOffset + 26);
      int length = s.readInt();

      Shape nshape = new Shape();
      s.skipBytes(length + 6);

      long shapeAttrOffset = s.getFilePointer();
      int shapeAttrLength = s.readInt();
      nshape.type = FeatureType.get(s.readInt());

      LOGGER.debug("  ShapeAttrs@{} len={}", shapeAttrOffset, shapeAttrLength);

      if (shapeAttrLength < 32) // Broken attrs.
        break;
      // read the bounding box
      s.skipBytes(8);
      nshape.x1 = s.readInt();
      nshape.y1 = s.readInt();
      nshape.x2 = s.readInt();
      nshape.y2 = s.readInt();
      nshape.width = nshape.x2 - nshape.x1;
      nshape.height = nshape.y2 - nshape.y1;

      LOGGER.debug("    Bounding Box");

      if (shapeAttrLength >= 72) { // Basic shape styling
        s.skipBytes(16);
        nshape.fillColour = s.readInt();
        nshape.textColour = s.readInt();
        nshape.drawColour = s.readInt();
        nshape.lineWidth = s.readInt();
        nshape.drawStyle = DrawStyle.get(s.readInt());
        nshape.fillStyle = FillStyle.get(s.readInt());
        nshape.strikeout = (s.readInt() != 0);

        LOGGER.debug("    Shape styles");
      }
      if (shapeAttrLength >= 100) { // Font styles
        // Windows TrueType font weighting.
        nshape.fontWeight = s.readInt();
        nshape.bold = (nshape.fontWeight >= 600);
        nshape.fontSize = s.readInt();
        nshape.italic = (s.readInt() != 0);
        nshape.underline = (s.readInt() != 0);
        nshape.textAlignment = TextAlignment.get(s.readInt());

        LOGGER.debug("    Font styles");
      }
      if (shapeAttrLength >= 148) { // Line styles
        s.skipBytes(36);
        nshape.lineEndStyle = BaseZeissReader.LineEndStyle.get(s.readInt());
        nshape.pointStyle = BaseZeissReader.PointStyle.get(s.readInt());
        nshape.lineEndSize = s.readInt();
        nshape.lineEndPositions = BaseZeissReader.LineEndPositions.get(s.readInt());

        LOGGER.debug("    Line styles");
      }
      if (shapeAttrLength >= 152) {
        nshape.displayTag = (s.readInt() != 0);
        LOGGER.debug("    Tag display");
      }
      if (shapeAttrLength >= 152) {
        nshape.charset = Charset.get(s.readInt());
        LOGGER.debug("    Charset");
      }

      // Label (text).  This label can be NULL in some files, so we
      // need to explicitly check whether it's a string prior to
      // parsing it.  It can also be present 0 or 2 bytes after the
      // ShapeAttrs block, so check for both eventualities.
      {
        long tmp = s.getFilePointer();
        for (int i=0; i<2; ++i) {
          if (s.readShort() == 8) {
            s.seek(tmp);
            nshape.text = parseROIString(s);
            break;
          }
        }

        if (nshape.text != null)
          LOGGER.debug("  Text={}", nshape.text);
        else
          LOGGER.debug("  Text=NULL");
      }

      // Tag ID
      if (s.getFilePointer() + 8 > s.length()) break;
      s.skipBytes(4);
      LOGGER.debug("  Tag@{}", s.getFilePointer());
      nshape.tagID = new Tag(s.readInt(), BaseZeissReader.Context.MAIN);
      LOGGER.debug("    TagID={}", nshape.tagID);

      // Font name
      nshape.fontName = parseROIString(s);
      if (nshape.fontName == null) break;
      LOGGER.debug("  Font name={}", nshape.fontName);

      // Label (name).
      nshape.name = parseROIString(s);
      if (nshape.name == null) break;
      LOGGER.debug("  Name={}", nshape.name);

      // Handle size and point count.
      if (s.getFilePointer() + 20 > s.length()) break;
      s.skipBytes(4);
      nshape.handleSize = s.readInt();
      s.skipBytes(2);
      nshape.pointCount = s.readInt();
      s.skipBytes(6);
      LOGGER.debug("  Handle size={}", nshape.handleSize);
      LOGGER.debug("  Point count={}", nshape.pointCount);

      if (s.getFilePointer() + (8*2*nshape.pointCount) > s.length()) break;
      nshape.points = new double[nshape.pointCount*2];
      for (int p=0; p<nshape.pointCount; p++) {
        nshape.points[(p*2)] =  s.readDouble();
        nshape.points[(p*2)+1] =  s.readDouble();
      }

      nlayer.shapes.add(nshape);

      ++roiFound;
    }

    if (roiCount != roiFound) {
        LOGGER.warn("Found {} ROIs, but {} ROIs expected", roiFound, roiCount);
    }

    s.close();
  }

  protected String parseROIString(RandomAccessInputStream s)
    throws IOException {
    // String is 0x0008 followed by int length for string+NUL.
    while (s.getFilePointer() < s.length() - 4 &&
      s.readShort() != 8);
    if (s.getFilePointer() >= s.length() - 8) return null;
    int strlen = s.readInt();
    if (strlen + s.getFilePointer() > s.length()) return null;
    // Strip off NUL.
    String text = null;
    if (strlen >= 2) { // Don't read NUL
        LOGGER.debug("  String@{} length={}", s.getFilePointer(), strlen);
        text = s.readString(strlen-2);
        s.skipBytes(2);
    } else {
        s.skipBytes(strlen);
    }

    return text;
  }

}
