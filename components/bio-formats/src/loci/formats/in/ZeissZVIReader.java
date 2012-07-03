/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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
import java.util.Vector;

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
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/ZeissZVIReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/ZeissZVIReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class ZeissZVIReader extends BaseZeissReader {

  // -- Constants --

  public static final int ZVI_MAGIC_BYTES = 0xd0cf11e0;

  private static final long ROI_SIGNATURE = 0x21fff6977547000dL;

  // -- Fields --

  protected POIService poi;
  protected String[] files;
  // -- Constructor --

  /** Constructs a new ZeissZVI reader. */
  public ZeissZVIReader() {
    super("Zeiss Vision Image (ZVI)", "zvi");
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
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
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    lastPlane = no;

    int bytes = FormatTools.getBytesPerPixel(getPixelType());
    int pixel = bytes * getRGBChannelCount();

    CodecOptions options = new CodecOptions();
    options.littleEndian = isLittleEndian();
    options.interleaved = isInterleaved();

    int index = no;
    if (getSeriesCount() > 1) {
      index += getSeries() * getImageCount();
    }

    if (index >= imageFiles.length) {
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
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (poi != null) poi.close();
    poi = null;
    files = null;
  }

  // -- Internal FormatReader API methods --

  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    super.initFileMain(id);
  }

  protected void initVars(String id) throws FormatException, IOException {
    super.initVars(id);
    try {
      ServiceFactory factory = new ServiceFactory();
      poi = factory.getInstance(POIService.class);
    }
    catch (DependencyException de) {
      throw new FormatException("POI library not found", de);
    }
    poi.initialize(Location.getMappedId(id));
    countImages();
  }

  /* @see loci.formats.FormatReader#initFile(String) */
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

        zIndices.add(zidx);
        timepointIndices.add(tidx);
        channelIndices.add(cidx);

        s.skipBytes(len);

        for (int q=0; q<5; q++) {
          getNextTag(s);
        }

        s.skipBytes(4);
        //if (getSizeX() == 0) {
          core[0].sizeX = s.readInt();
          core[0].sizeY = s.readInt();
        //}
        //else s.skipBytes(8);
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
        imageFiles[imageNum] = name;
        s.close();
      }
    }
  }

    protected void fillMetadataPass3(MetadataStore store) throws FormatException, IOException {
      super.fillMetadataPass3(store);

    // calculate tile dimensions and number of tiles
    if (core.length > 1) {
      Integer[] t = tiles.keySet().toArray(new Integer[tiles.size()]);
      Arrays.sort(t);
      Vector<Integer> tmpOffsets = new Vector<Integer>();
      Vector<String> tmpFiles = new Vector<String>();
      int index = 0;
      for (Integer key : t) {
        int nTiles = tiles.get(key).intValue();
        if (nTiles < getImageCount()) {
          tiles.remove(key);
        }
        else {
          for (int p=0; p<nTiles; p++) {
            tmpOffsets.add(new Integer(offsets[index + p]));
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

    protected void fillMetadataPass5(MetadataStore store) throws FormatException, IOException {
      super.fillMetadataPass5(store);

    for (String name : tagsToParse) {
      int imageNum = getImageNumber(name, -1);
      parseTags(imageNum, name, store);
      }
    }

  protected void countImages() {
    // count number of images
    files = (String[]) poi.getDocumentList().toArray(new String[0]);
    Arrays.sort(files, new Comparator() {
      public int compare(Object o1, Object o2) {
        int n1 = getImageNumber((String) o1, -1);
        int n2 = getImageNumber((String) o2, -1);

        return new Integer(n1).compareTo(new Integer(n2));
      }
    });
    core[0].imageCount = 0;

    for (String file : files) {
      String uname = file.toUpperCase();
      uname = uname.substring(uname.indexOf(File.separator) + 1);
      if (uname.endsWith("CONTENTS") && (uname.startsWith("IMAGE") ||
          uname.indexOf("ITEM") != -1) && poi.getFileSize(file) > 1024)
      {
        int imageNumber = getImageNumber(file, 0);
        if (imageNumber >= getImageCount()) {
          core[0].imageCount++;
        }
      }
    }
    super.countImages();
  }

  private int getImageNumber(String dirName, int defaultNumber) {
    if (dirName.toUpperCase().indexOf("ITEM") != -1) {
      int open = dirName.indexOf("(");
      int close = dirName.indexOf(")");
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
    s.order(true);

    // scan stream for offsets to each ROI

    Vector<Long> roiOffsets = new Vector<Long>();
    s.seek(0);
    while (s.getFilePointer() < s.length() - 8) {
      // find next ROI signature
      long signature = s.readLong() & 0xffffffffffffffffL;
      while (signature != ROI_SIGNATURE) {
        if (s.getFilePointer() >= s.length()) break;
        s.seek(s.getFilePointer() - 6);
        signature = s.readLong() & 0xffffffffffffffffL;
      }
      if (s.getFilePointer() < s.length()) {
        roiOffsets.add(new Long(s.getFilePointer()));
      }
    }

    Layer nlayer = new Layer();

    for (int shape=0; shape<roiOffsets.size(); shape++) {
      Shape nshape = new Shape();

      s.seek(roiOffsets.get(shape).longValue() + 18);

      int length = s.readInt();
      s.skipBytes(length + 10);
      nshape.type = FeatureType.get(s.readInt());
      s.skipBytes(8);

      // read the bounding box
      nshape.x1 = s.readInt();
      nshape.y1 = s.readInt();
      nshape.x2 = s.readInt();
      nshape.y2 = s.readInt();
      nshape.width = nshape.x2 - nshape.x1;
      nshape.height = nshape.y2 - nshape.y1;

      // read text label and font data
      long nextOffset =
          (shape < roiOffsets.size() - 1 ?
              roiOffsets.get(shape + 1).longValue() : s.length());

      long nameBlock = s.getFilePointer();
      long fontBlock = s.getFilePointer();
      long lastBlock = s.getFilePointer();
      while (s.getFilePointer() < nextOffset - 1) {
        while (s.readShort() != 8) {
          if (s.getFilePointer() >= nextOffset) break;
        }
        if (s.getFilePointer() >= nextOffset) break;
        if (s.getFilePointer() - lastBlock > 64 && lastBlock != fontBlock) {
          break;
        }
        nameBlock = fontBlock;
        fontBlock = lastBlock;
        lastBlock = s.getFilePointer();
      }

      s.seek(nameBlock);
      int strlen = s.readInt();
      if (strlen + s.getFilePointer() > s.length()) continue;
      nshape.name = DataTools.stripString(s.readString(strlen));

      s.seek(fontBlock);
      int fontLength = s.readInt();
      nshape.fontName = DataTools.stripString(s.readString(fontLength));
      s.skipBytes(2);
      int typeLength = s.readInt(); // This is probably skipping the ShapeAttributes structure, especially if it's 156 bytes long.
      s.skipBytes(typeLength);

      // read list of points that define this ROI
      s.skipBytes(10);
      nshape.pointCount = s.readInt();
      nshape.points = new double[nshape.pointCount*2];
      s.skipBytes(6);
      for (int p=0; p<nshape.pointCount; p++) {
        nshape.points[(p*2)] =  s.readDouble();
        nshape.points[(p*2)+1] =  s.readDouble();
      }

      nlayer.shapes.add(nshape);
    }
    layers.add(nlayer);
    s.close();
  }

}
