/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.in;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.imageio.ImageIO;

import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.gui.AWTImageTools;
import loci.formats.meta.MetadataStore;

/**
 * MNGReader is the file format reader for Multiple-image Network Graphics
 * (MNG) files.  Does not support JNG (JPEG Network Graphics).
 */
public class MNGReader extends BIFormatReader {

  // -- Constants --

  public static final long MNG_MAGIC_BYTES = 0x8a4d4e470d0a1a0aL;

  // -- Fields --

  private List<SeriesInfo> seriesInfo;

  private boolean isJNG = false;

  // -- Constructor --

  /** Constructs a new MNG reader. */
  public MNGReader() {
    super("Multiple-image Network Graphics", "mng");
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 8;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readLong() == MNG_MAGIC_BYTES;
  }

  /* @see loci.formats.IFormatReader#openPlane(int, int, int, int, int int) */
  @Override
  public Object openPlane(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, -1, x, y, w, h);

    SeriesInfo info = seriesInfo.get(getSeries());
    long offset = info.offsets.get(no);
    in.seek(offset);
    long end = info.lengths.get(no);
    BufferedImage img = readImage(end);

    // reconstruct the image to use an appropriate raster
    // ImageIO often returns images that cannot be scaled because a
    // BytePackedRaster is used
    return AWTImageTools.getSubimage(img, isLittleEndian(), x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      seriesInfo = null;
      isJNG = false;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    in.order(false);

    LOGGER.info("Verifying MNG format");

    seriesInfo = new ArrayList<SeriesInfo>();
    seriesInfo.add(new SeriesInfo());

    in.skipBytes(12);

    if (!"MHDR".equals(in.readString(4))) {
      throw new FormatException("Invalid MNG file.");
    }

    LOGGER.info("Reading dimensions");

    in.skipBytes(32);

    final List<Long> stack = new ArrayList<Long>();
    int maxIterations = 0;
    int currentIteration = 0;

    LOGGER.info("Finding image offsets");

    // read sequence of [len, code, value] tags

    while (in.getFilePointer() < in.length()) {
      int len = in.readInt();
      String code = in.readString(4);

      long fp = in.getFilePointer();

      if (code.equals("IHDR")) {
        seriesInfo.get(0).offsets.add(fp - 8);
      }
      else if (code.equals("JDAT")) {
        isJNG = true;
        seriesInfo.get(0).offsets.add(fp);
      }
      else if (code.equals("IEND")) {
        seriesInfo.get(0).lengths.add(fp + len + 4);
      }
      else if (code.equals("LOOP")) {
        stack.add(fp + len + 4);
        in.skipBytes(1);
        maxIterations = in.readInt();
      }
      else if (code.equals("ENDL")) {
        long seek = stack.get(stack.size() - 1).longValue();
        if (currentIteration < maxIterations) {
          in.seek(seek);
          currentIteration++;
        }
        else {
          stack.remove(stack.size() - 1);
          maxIterations = 0;
          currentIteration = 0;
        }
      }

      in.seek(fp + len + 4);
    }

    LOGGER.info("Populating metadata");

    // easiest way to get image dimensions is by opening the first plane

    Hashtable<String, List<Long>> seriesOffsets = new Hashtable<String, List<Long>>();
    Hashtable<String, List<Long>> seriesLengths = new Hashtable<String, List<Long>>();

    SeriesInfo info = seriesInfo.get(0);
    addGlobalMeta("Number of frames", info.offsets.size());
    for (int i=0; i<info.offsets.size(); i++) {
      long offset = info.offsets.get(i);
      in.seek(offset);
      long end = info.lengths.get(i);
      if (end < offset) continue;
      BufferedImage img = readImage(end);
      String data = img.getWidth() + "-" + img.getHeight() + "-" +
        img.getRaster().getNumBands() + "-" + AWTImageTools.getPixelType(img);
      List<Long> v = seriesOffsets.get(data);
      if (v == null) {
        v = new ArrayList<Long>();
        seriesOffsets.put(data, v);
      }
      v.add(offset);

      v = seriesLengths.get(data);
      if (v == null) {
        v = new ArrayList<Long>();
        seriesLengths.put(data, v);
      }
      v.add(end);
    }

    String[] keys = seriesOffsets.keySet().toArray(new String[0]);

    if (keys.length == 0) {
      throw new FormatException("Pixel data not found.");
    }

    int seriesCount = keys.length;
    core.clear();

    seriesInfo.clear();
    for (int i=0; i<seriesCount; i++) {
      CoreMetadata ms = new CoreMetadata();
      core.add(ms);
      String[] tokens = keys[i].split("-");
      ms.sizeX = Integer.parseInt(tokens[0]);
      ms.sizeY = Integer.parseInt(tokens[1]);
      ms.sizeC = Integer.parseInt(tokens[2]);
      ms.pixelType = Integer.parseInt(tokens[3]);
      ms.rgb = ms.sizeC > 1;
      ms.sizeZ = 1;
      ms.dimensionOrder = "XYCZT";
      ms.interleaved = false;
      ms.metadataComplete = true;
      ms.indexed = false;
      ms.littleEndian = false;
      ms.falseColor = false;

      SeriesInfo inf = new SeriesInfo();
      inf.offsets = seriesOffsets.get(keys[i]);
      inf.lengths = seriesLengths.get(keys[i]);
      seriesInfo.add(inf);

      ms.imageCount = inf.offsets.size();
      ms.sizeT = ms.imageCount;
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
    for (int i=0; i<getSeriesCount(); i++) {
      store.setImageName("Series " + (i + 1), i);
    }
  }

  // -- Helper methods --

  private BufferedImage readImage(long end) throws IOException {
    int headerSize = isJNG ? 0 : 8;
    byte[] b = new byte[(int) (end - in.getFilePointer() + headerSize)];
    in.read(b, headerSize, b.length - headerSize);
    if (!isJNG) {
      b[0] = (byte) 0x89;
      b[1] = 0x50;
      b[2] = 0x4e;
      b[3] = 0x47;
      b[4] = 0x0d;
      b[5] = 0x0a;
      b[6] = 0x1a;
      b[7] = 0x0a;
    }
    return ImageIO.read(new ByteArrayInputStream(b));
  }

  // -- Helper class --

  private class SeriesInfo {
    public List<Long> offsets = new ArrayList<Long>();
    public List<Long> lengths = new ArrayList<Long>();
  }

}
