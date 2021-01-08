/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import ome.units.quantity.Time;
import ome.units.quantity.Length;
import ome.units.UNITS;

/**
 * SlidebookReader is the file format reader for 3I Slidebook files.
 * The strategies employed by this reader are highly suboptimal, as we
 * have very little information on the Slidebook format.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class SlidebookReader extends FormatReader {

  // -- Constants --

  public static final int SLD_MAGIC_BYTES_1_0 = 0x006c;
  public static final int SLD_MAGIC_BYTES_1_1 = 0x0100;
  public static final int SLD_MAGIC_BYTES_1_2 = 0x0200;
  public static final int SLD_MAGIC_BYTES_2_0 = 0x01f5;
  public static final int SLD_MAGIC_BYTES_2_1 = 0x0102;

  public static final long SLD_MAGIC_BYTES_3 = 0xf6010101L;

  // -- Fields --

  private List<Long> metadataOffsets;
  private List<Long> pixelOffsets;
  private List<Long> pixelLengths;
  private List<Double> ndFilters;

  private Map<Integer, String> imageDescriptions;

  private long[][] planeOffset;

  private boolean adjust = true;
  private boolean isSpool;
  private Map<Integer, Integer> metadataInPlanes;

  // -- Constructor --

  /** Constructs a new Slidebook reader. */
  public SlidebookReader() {
    super("Olympus Slidebook", new String[] {"sld", "spl"});
    domains = new String[] {FormatTools.LM_DOMAIN};
    suffixSufficient = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 8;
    stream.seek(4);
    boolean littleEndian = stream.readString(2).equals("II");
    if (!FormatTools.validStream(stream, blockLen, littleEndian)) return false;
    int magicBytes1 = stream.readShort();
    int magicBytes2 = stream.readShort();


    return ((magicBytes2 & 0xff00) == SLD_MAGIC_BYTES_1_1 ||
      (magicBytes2 & 0xff00) == SLD_MAGIC_BYTES_1_2) &&
      (magicBytes1 == SLD_MAGIC_BYTES_1_0 ||
      magicBytes1 == SLD_MAGIC_BYTES_2_0);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    long offset = planeOffset[getSeries()][no];
    in.seek(offset);

    int[] zct = getZCTCoords(no);

    // if this is a spool file, there may be an extra metadata block here
    if (isSpool) {
      long len = pixelLengths.get(getSeries());
      long planeSize = FormatTools.getPlaneSize(this);
      long diff = len - getImageCount() * planeSize;
      in.seek(in.getFilePointer() - diff);

      Integer[] keys = metadataInPlanes.keySet().toArray(new Integer[0]);
      Arrays.sort(keys);
      for (int key : keys) {
        if (key < no) {
          in.skipBytes(256);
        }
      }

      // check next 8 blocks of 256 bytes, just in case

      long beginning = in.getFilePointer();

      for (int i=0; i<8; i++) {
        if (in.getFilePointer() + 4 >= in.length()) {
          in.seek(beginning);
          break;
        }
        in.order(false);
        long magicBytes = (long) in.readInt() & 0xffffffffL;
        in.order(isLittleEndian());
        if (magicBytes == SLD_MAGIC_BYTES_3 &&
            !metadataInPlanes.containsValue(no)) {
          metadataInPlanes.put(no, 0);
          in.skipBytes(252);
          break;
        }
        else if (i == 7) {
          in.seek(beginning);
        }
        else {
          in.skipBytes(252);
        }
      }
    }

    readPlane(in, x, y, w, h, buf);
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      metadataOffsets = pixelOffsets = pixelLengths = null;
      ndFilters = null;
      isSpool = false;
      metadataInPlanes = null;
      adjust = true;
      planeOffset = null;
      imageDescriptions = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    isSpool = checkSuffix(id, "spl");
    if (isSpool) {
      metadataInPlanes = new HashMap<Integer, Integer>();
    }

    LOGGER.info("Finding offsets to pixel data");

    // Slidebook files appear to be comprised of four types of blocks:
    // variable length pixel data blocks, 512 byte metadata blocks,
    // 128 byte metadata blocks, and variable length metadata blocks.
    //
    // Fixed-length metadata blocks begin with a 2 byte identifier,
    // e.g. 'i' or 'h'.
    // Following this are two unknown bytes (usually 256), then a 2 byte
    // endianness identifier - II or MM, for little or big endian, respectively.
    // Presumably these blocks contain useful information, but for the most
    // part we aren't sure what it is or how to extract it.
    //
    // Variable length metadata blocks begin with 0xffff and are
    // (as far as I know) always between two fixed-length metadata blocks.
    // These appear to be a relatively new addition to the format - they are
    // only present in files received on/after March 30, 2008.
    //
    // Each pixel data block corresponds to one series.
    // The first 'i' metadata block after each pixel data block contains
    // the width and height of the planes in that block - this can (and does)
    // vary between blocks.
    //
    // Z, C, and T sizes are computed heuristically based on the number of
    // metadata blocks of a specific type.

    in.skipBytes(4);
    core.get(0).littleEndian = in.read() == 0x49;
    in.order(isLittleEndian());

    metadataOffsets = new ArrayList<Long>();
    pixelOffsets = new ArrayList<Long>();
    pixelLengths = new ArrayList<Long>();
    ndFilters = new ArrayList<Double>();
    imageDescriptions = new HashMap<Integer, String>();

    in.seek(0);

    // gather offsets to metadata and pixel data blocks

    while (in.getFilePointer() < in.length() - 8) {
      LOGGER.debug("Looking for block at {}", in.getFilePointer());
      in.skipBytes(4);
      int checkOne = in.read();
      int checkTwo = in.read();
      if ((checkOne == 'I' && checkTwo == 'I') ||
        (checkOne == 'M' && checkTwo == 'M'))
      {
        LOGGER.debug("Found metadata offset: {}", (in.getFilePointer() - 6));
        metadataOffsets.add(in.getFilePointer() - 6);
        in.skipBytes(in.readShort() - 8);
      }
      else if (checkOne == -1 && checkTwo == -1) {
        boolean foundBlock = false;
        byte[] block = new byte[8192];
        in.read(block);
        while (!foundBlock) {
          for (int i=0; i<block.length-2; i++) {
            if ((block[i] == 'M' && block[i + 1] == 'M') ||
              (block[i] == 'I' && block[i + 1] == 'I'))
            {
              foundBlock = true;
              in.seek(in.getFilePointer() - block.length + i - 2);
              LOGGER.debug("Found metadata offset: {}",
                (in.getFilePointer() - 2));
              metadataOffsets.add(in.getFilePointer() - 2);
              in.skipBytes(in.readShort() - 5);
              break;
            }
          }
          if (!foundBlock) {
            block[0] = block[block.length - 2];
            block[1] = block[block.length - 1];
            in.read(block, 2, block.length - 2);
          }
        }

      }
      else {
        String s = null;
        long fp = in.getFilePointer() - 6;
        in.seek(fp);
        int len = in.read();
        if (len > 0 && len <= 32) {
          s = in.readString(len);
        }

        if (s != null && s.indexOf("Annotation") != -1) {
          if (s.equals("CTimelapseAnnotation")) {
            in.skipBytes(41);
            if (in.read() == 0) in.skipBytes(10);
            else in.seek(in.getFilePointer() - 1);
          }
          else if (s.equals("CIntensityBarAnnotation")) {
            in.skipBytes(56);
            int n = in.read();
            while (n == 0 || n < 6 || n > 0x80) n = in.read();
            in.seek(in.getFilePointer() - 1);
          }
          else if (s.equals("CCubeAnnotation")) {
            in.skipBytes(66);
            int n = in.read();
            if (n != 0) in.seek(in.getFilePointer() - 1);
          }
          else if (s.equals("CScaleBarAnnotation")) {
            in.skipBytes(38);
            int extra = in.read();
            if (extra <= 16) in.skipBytes(3 + extra);
            else in.skipBytes(2);
          }
        }
        else if (s != null && s.indexOf("Decon") != -1) {
          in.seek(fp);
          while (in.read() != ']');
        }
        else {
          if ((fp % 2) == 1) fp -= 2;
          in.seek(fp);

          // make sure there isn't another block nearby

          String checkString = in.readString(64);
          if (checkString.indexOf("II") != -1 ||
            checkString.indexOf("MM") != -1)
          {
            int index = checkString.indexOf("II");
            if (index == -1) index = checkString.indexOf("MM");
            in.seek(fp + index - 4);
            continue;
          }
          else in.seek(fp);

          LOGGER.debug("Found pixel offset at {}", fp);
          pixelOffsets.add(fp);
          try {
            byte[] buf = new byte[8192];
            boolean found = false;
            int n = in.read(buf);

            while (!found && in.getFilePointer() < in.length()) {
              for (int i=0; i<n-6; i++) {
                if ((buf[i + 4] == 'I' && buf[i + 5] == 'I') ||
                  (buf[i + 4] == 'M' && buf[i + 5] == 'M'))
                {
                  if (((buf[i] == 'h' || buf[i] == 'i') && buf[i + 1] == 0) ||
                    (buf[i] == 0 && (buf[i + 1] == 'h' || buf[i + 1] == 'i')))
                  {
                    found = true;
                    in.seek(in.getFilePointer() - n + i - 20);
                    if (buf[i] == 'i' || buf[i + 1] == 'i') {
                      pixelOffsets.remove(pixelOffsets.size() - 1);
                    }
                    break;
                  }
                  else if (((buf[i] == 'j' || buf[i] == 'k' || buf[i] == 'n') &&
                    buf[i + 1] == 0) || (buf[i] == 0 && (buf[i + 1] == 'j' ||
                    buf[i + 1] == 'k' || buf[i + 1] == 'n')) ||
                    (buf[i] == 'o' && buf[i + 1] == 'n'))
                  {
                    found = true;
                    pixelOffsets.remove(pixelOffsets.size() - 1);
                    in.seek(in.getFilePointer() - n + i - 20);
                    break;
                  }
                }
              }
              if (!found) {
                byte[] tmp = buf;
                buf = new byte[8192];
                System.arraycopy(tmp, tmp.length - 20, buf, 0, 20);
                n = in.read(buf, 20, buf.length - 20);
              }
            }

            if (in.getFilePointer() <= in.length()) {
              if (pixelOffsets.size() > pixelLengths.size()) {
                long length = in.getFilePointer() - fp;
                if (((length / 2) % 2) == 1) {
                  pixelOffsets.set(pixelOffsets.size() - 1, fp + 2);
                  length -= 2;
                }
                if (length >= 1024) {
                  pixelLengths.add(length);
                }
                else pixelOffsets.remove(pixelOffsets.size() - 1);
              }
            }
            else pixelOffsets.remove(pixelOffsets.size() - 1);
          }
          catch (EOFException e) {
            pixelOffsets.remove(pixelOffsets.size() - 1);
          }
        }
      }
    }

    final List<Long> orderedSeries = new ArrayList<Long>();
    final ListMultimap<Long, Integer> uniqueSeries =
        ArrayListMultimap.create();

    for (int i=0; i<pixelOffsets.size(); i++) {
      long length = pixelLengths.get(i).longValue();
      long offset = pixelOffsets.get(i).longValue();

      int padding = isSpool ? 0 : 7;

      if (length + offset + padding > in.length()) {
        pixelOffsets.remove(i);
        pixelLengths.remove(i);
        i--;
      }
      else {
        final List<Integer> v = uniqueSeries.get(length);
        if (v.isEmpty()) {
          orderedSeries.add(length);
        }
        uniqueSeries.put(length, i);
      }
    }

    if (pixelOffsets.size() > 1) {
      boolean little = isLittleEndian();

      int seriesCount = 0;
      for (final Long key : orderedSeries) {
        final List<Integer> pixelIndexes = uniqueSeries.get(key);
        int nBlocks = pixelIndexes.size();
        if (nBlocks == 0) {
          nBlocks++;
        }
        seriesCount += nBlocks;
      }

      core.clear();
      for (int i=0; i<seriesCount; i++) {
        CoreMetadata ms = new CoreMetadata();
        core.add(ms);
        ms.littleEndian = little;
      }
    }

    LOGGER.info("Determining dimensions");

    // determine total number of pixel bytes

    final Map<Integer, Float> pixelSize = new HashMap<Integer, Float>();
    final Map<Integer, String> objectives = new HashMap<Integer, String>();
    final Map<Integer, Integer> magnifications =
      new HashMap<Integer, Integer>();
    final List<Double> pixelSizeZ = new ArrayList<Double>();
    final List<Integer> exposureTimes = new ArrayList<Integer>();

    long pixelBytes = 0;
    for (int i=0; i<pixelLengths.size(); i++) {
      pixelBytes += pixelLengths.get(i).longValue();
    }

    String[] imageNames = new String[getSeriesCount()];
    final List<String> channelNames = new ArrayList<String>();
    int nextName = 0;

    int[] sizeX = new int[pixelOffsets.size()];
    int[] sizeY = new int[pixelOffsets.size()];
    int[] sizeZ = new int[pixelOffsets.size()];
    int[] sizeC = new int[pixelOffsets.size()];

    int[] divValues = new int[pixelOffsets.size()];

    // try to find the width and height
    int iCount = 0;
    int hCount = 0;
    int uCount = 0;
    int prevSeries = -1;
    int prevSeriesU = -1;
    int nextChannel = 0;
    for (int i=0; i<metadataOffsets.size(); i++) {
      long off = metadataOffsets.get(i).longValue();
      if (isSpool && off == 0) {
        off = 276;
      }
      in.seek(off);
      long next = i == metadataOffsets.size() - 1 ? in.length() :
        metadataOffsets.get(i + 1).longValue();
      int totalBlocks = (int) ((next - off) / 128);

      // if there are more than 100 blocks, we probably found a pixel block
      // by accident (but we'll check the first block anyway)
      //if (totalBlocks > 100) totalBlocks = 100;
      for (int q=0; q<totalBlocks; q++) {
        if (withinPixels(off + q * 128)) {
          continue;
        }
        in.seek(off + (long) q * 128);
        char n = (char) in.readShort();
        while (n == 0 && in.getFilePointer() < off + (q + 1) * 128) {
          n = (char) in.readShort();
        }
        if (in.getFilePointer() >= in.length() - 2) break;
        if (n == 'i') {
          iCount++;
          in.skipBytes(70);
          int expTime = in.readInt();
          if (expTime > 0) {
            exposureTimes.add(expTime);
          }
          in.skipBytes(20);
          final Double size = (double) in.readFloat();
          if (isGreaterThanEpsilon(size)) {
            pixelSizeZ.add(size);
          }
          else {
            pixelSizeZ.add(null);
          }
          in.seek(in.getFilePointer() - 20);

          for (int j=0; j<pixelOffsets.size(); j++) {
            long end = j == pixelOffsets.size() - 1 ? in.length() :
              pixelOffsets.get(j + 1).longValue();
            if (in.getFilePointer() < end) {
              if (sizeX[j] == 0) {
                int x = in.readShort();
                int y = in.readShort();
                if (x != 0 && y != 0) {
                  sizeX[j] = x;
                  sizeY[j] = y;
                  int checkX = in.readShort();
                  int checkY = in.readShort();
                  int div = in.readShort();
                  if (checkX == checkY) {
                    divValues[j] = div;
                    sizeX[j] /= (div == 0 ? 1 : div);
                    div = in.readShort();
                    sizeY[j] /= (div == 0 ? 1 : div);
                  }
                }
                else in.skipBytes(8);
              }
              if (prevSeries != j) {
                iCount = 1;
              }
              prevSeries = j;
              sizeC[j] = iCount;
              break;
            }
          }
        }
        else if (n == 'u') {
          uCount++;
          for (int j=0; j<getSeriesCount(); j++) {
            long end = j == getSeriesCount() - 1 ? in.length() :
              pixelOffsets.get(j + 1).longValue();
            if (in.getFilePointer() < end) {
              if (prevSeriesU != j) {
                uCount = 1;
              }
              prevSeriesU = j;
              sizeZ[j] = uCount;
              break;
            }
          }
        }
        else if (n == 'h') hCount++;
        else if (n == 'j') {
          in.skipBytes(2);
          String check = in.readString(2);
          if (check.equals("II") || check.equals("MM")) {
            long pointer = in.getFilePointer();
            // this block should contain an image name
            in.skipBytes(10);
            if (nextName < imageNames.length) {
              String name = readCString().trim();
              if (name.length() > 0) {
                imageNames[nextName++] = name;
              }
            }

            long fp = in.getFilePointer();
            if ((in.getFilePointer() % 2) == 1) in.skipBytes(1);
            while (in.readShort() == 0);
            if (in.readShort() == 0) {
              in.skipBytes(4);
            }
            else {
              in.skipBytes(16);
            }
            long diff = in.getFilePointer() - fp;
            if (diff > 123 && (fp % 2) == 0 && diff != 142 && diff != 143 &&
              diff != 130)
            {
              in.seek(fp + 123);
            }

            int x = in.readInt();
            int y = in.readInt();

            if (x > 0x8000 || y > 0x8000) {
              in.seek(in.getFilePointer() - 7);
              x = in.readInt();
              y = in.readInt();
            }
            else if (x == 0 || y == 0) {
              in.seek(in.getFilePointer() - 27);
              x = in.readInt();
              y = in.readInt();
            }

            int div = in.readShort();
            x /= (div == 0 || div > 0x100 ? 1 : div);
            div = in.readShort();
            y /= (div == 0 || div > 0x100 ? 1 : div);

            if (x > 0x10000 || y > 0x10000) {
              in.seek(in.getFilePointer() - 11);
              x = in.readInt();
              y = in.readInt();
              div = in.readShort();
              x /= (div == 0 ? 1 : div);
              div = in.readShort();
              y /= (div == 0 ? 1 : div);

              if (x > 0x10000 || y > 0x10000) {
                in.skipBytes(2);

                x = in.readInt();
                y = in.readInt();
                div = in.readShort();
                x /= (div == 0 ? 1 : div);
                div = in.readShort();
                y /= (div == 0 ? 1 : div);
              }
            }

            if (nextName >= 1 && x > 16 && (x < sizeX[nextName - 1] ||
              sizeX[nextName - 1] == 0) && y > 16 &&
              (y < sizeY[nextName - 1] || sizeY[nextName - 1] == 0))
            {
              sizeX[nextName - 1] = x;
              sizeY[nextName - 1] = y;
              adjust = false;
            }

            in.seek(pointer + 214);
            int validBits = in.readShort();
            if (nextName >= 1 && core.get(nextName - 1).bitsPerPixel == 0 &&
              validBits <= 16 && validBits > 0)
            {
              core.get(nextName - 1).bitsPerPixel = validBits;
            }
          }
        }
        else if (n == 'm') {
          // this block should contain a channel name
          if (in.getFilePointer() > pixelOffsets.get(0).longValue() || isSpool)
          {
            in.skipBytes(14);
            String name = readCString().trim();
            if (name.length() > 1) {
              channelNames.add(name);
            }
          }
        }
        else if (n == 'd') {
          // objective info and pixel size X/Y
          in.skipBytes(6);
          long fp = in.getFilePointer();
          while (in.read() == 0);
          in.seek(in.getFilePointer() - 1);
          long nSkipped = in.getFilePointer() - fp;
          if (nSkipped < 8) {
            in.skipBytes(8 - nSkipped);
          }
          String objective = readCString().trim();
          in.seek(fp + 144);
          float pixSize = in.readFloat();
          int magnification = in.readShort();

          int mult = 1;
          if (pixelSize.size() < divValues.length) {
            mult = divValues[pixelSize.size()];
          }
          float v = pixSize * mult;
          if (isGreaterThanEpsilon(v)) {
            pixelSize.put(nextName - 1, v);
            objectives.put(nextName - 1, objective);
            magnifications.put(nextName - 1, magnification);
          }
        }
        else if (n == 'e') {
          in.skipBytes(174);
          ndFilters.add((double) in.readFloat());
          in.skipBytes(40);
          if (nextName >= 0 && nextName < getSeriesCount()) {
            setSeries(nextName);
            addSeriesMetaList("channel intensification", in.readShort());
          }
        }
        else if (n == 'k') {
          in.skipBytes(14);
          if (nextName > 0) setSeries(nextName - 1);
          addSeriesMeta("Mag. changer", readCString());
        }
        else if (n == 'n') {
          long fp1 = in.getFilePointer();
          in.seek(in.getFilePointer() - 3);
          while (in.read() != 0) {
            in.seek(in.getFilePointer() - 2);
          }
          long fp2 = in.getFilePointer();
          int len = in.read() - 1;

          int currentSeries = 0;
          for (int j=0; j<pixelOffsets.size(); j++) {
            long end = j == pixelOffsets.size() - 1 ? in.length() :
              pixelOffsets.get(j + 1).longValue();
            if (in.getFilePointer() < end) {
              currentSeries = j;
              break;
            }
          }

          if (len > 0 && fp1 - fp2 != 2) {
            if (fp2 < fp1) {
              in.seek(in.getFilePointer() - 1);
              String descr = readCString();
              descr = descr.substring(0, descr.length() - 2);
              if (!descr.endsWith("Annotatio")) {
                imageDescriptions.put(currentSeries, descr.trim());
              }
            }
            else {
              imageDescriptions.put(currentSeries, in.readString(len).trim());
            }
          }
        }
        else if (isSpool) {
          // spool files don't necessarily have block identifiers
          for (int j=0; j<pixelOffsets.size(); j++) {
            long end = j == pixelOffsets.size() - 1 ? in.length() :
              pixelOffsets.get(j + 1).longValue();
            if (in.getFilePointer() < end) {
              in.skipBytes(14);
              int check = in.readShort();
              int x = in.readShort();
              int y = in.readShort();
              if (check == 0 && x > 16 && y > 16) {
                sizeX[j] = x;
                sizeY[j] = y;
              }
              adjust = false;
              break;
            }
          }
        }
      }
    }

    // TODO: extend the name matching to include "* Timepoint *"
    String currentName = imageNames[0];
    ArrayList<CoreMetadata> realCore = new ArrayList<CoreMetadata>();
    int t = 1;
    boolean noFlattening =
      currentName != null && currentName.equals("Untitled");
    for (int i=1; i<getSeriesCount(); i++) {
      if (imageNames[i] == null || !imageNames[i].equals(currentName) ||
        noFlattening ||
        (i == 1 && (sizeX[i - 1] != sizeX[i] || sizeY[i - 1] != sizeY[i] ||
        sizeC[i - 1] != sizeC[i] || sizeZ[i - 1] != sizeZ[i])))
      {
        currentName = imageNames[i];
        CoreMetadata nextCore = core.get(i - 1);
        nextCore.sizeT = t;
        realCore.add(nextCore);
        if (t == 1) {
          noFlattening = true;
        }
        t = 1;
        if (i == 1) {
          noFlattening = true;
        }
      }
      else {
        t++;
      }
    }
    core.get(getSeriesCount() - 1).sizeT = t;
    realCore.add(core.get(getSeriesCount() - 1));
    boolean flattened = false;
    if (core.size() != realCore.size() && !noFlattening) {
      flattened = true;
      core = realCore;
      orderedSeries.clear();
      uniqueSeries.clear();
      int nextIndex = 0;
      for (int i=0; i<core.size(); i++) {
        long thisSeries = (long) i;
        orderedSeries.add(thisSeries);
        uniqueSeries.put(thisSeries, nextIndex);

        long length = pixelLengths.get(nextIndex);
        length *= core.get(i).sizeT;
        pixelLengths.set(i, length);

        nextIndex += core.get(i).sizeT;
      }
    }

    planeOffset = new long[getSeriesCount()][];

    boolean divByTwo = false;
    boolean divZByTwo = false;

    int nextPixelIndex = 0;
    int nextBlock = 0;
    int nextOffsetIndex = 0;

    for (int i=0; i<getSeriesCount(); i++) {
      setSeries(i);
      CoreMetadata ms = core.get(i);

      List<Integer> pixelIndexes =
        uniqueSeries.get(orderedSeries.get(nextPixelIndex));
      int nBlocks = pixelIndexes.size();
      if (nextBlock >= nBlocks) {
        nextPixelIndex++;
        nextBlock = 0;
        pixelIndexes = uniqueSeries.get(orderedSeries.get(nextPixelIndex));
        nBlocks = pixelIndexes.size();
      }
      else {
        nextBlock++;
      }
      int index =
        pixelIndexes.size() == getSeriesCount() ? pixelIndexes.get(0) : i;

      long pixels = pixelLengths.get(index).longValue() / 2;
      boolean x = true;

      ms.sizeX = sizeX[index];
      ms.sizeY = sizeY[index];
      ms.sizeC = sizeC[index];
      ms.sizeZ = sizeZ[index];

      if (getSizeC() > 64) {
        // dimensions are probably incorrect
        ms.sizeC = 1;
        ms.sizeZ = 1;
        ms.sizeX /= 2;
        ms.sizeY /= 2;
      }

      boolean isMontage = false;
      if (i > 1 && ((imageNames[i] != null &&
        imageNames[i].startsWith("Montage")) || getSizeC() >= 32))
      {
        ms.sizeC = core.get(1).sizeC;
        ms.sizeZ = core.get(1).sizeZ;
        isMontage = true;
      }

      boolean cGreater = ms.sizeC > ms.sizeZ;

      if (isSpool) {
        if (ms.sizeC == 0) {
          ms.sizeC = channelNames.size();
        }
      }

      if (ms.sizeZ % nBlocks == 0 && nBlocks != getSizeC()) {
        int z = ms.sizeZ / nBlocks;
        if (z <= nBlocks) {
          ms.sizeZ = z;
        }
      }

      if (divByTwo) ms.sizeX /= 2;

      if (divZByTwo && ms.sizeC > 1) {
        ms.sizeZ = (int) ((pixels / (ms.sizeX * ms.sizeY)) / 2);
        ms.sizeC = 2;
      }

      if (getSizeC() == 0) ms.sizeC = 1;
      if (getSizeZ() == 0) ms.sizeZ = 1;

      long plane = pixels / (getSizeC() * getSizeZ());
      if (getSizeT() > 0) {
        plane /= getSizeT();
      }

      if (getSizeX() * getSizeY() == pixels) {
        if (getSizeC() == 2 && (getSizeX() % 2 == 0) && (getSizeY() % 2 == 0)) {
          if (getSizeC() != getSizeZ()) {
            ms.sizeX /= 2;
            divByTwo = true;
          }
          else {
            divZByTwo = true;
            ms.sizeC = 1;
          }
        }
        else {
          ms.sizeC = 1;
        }
        ms.sizeZ = 1;
      }
      else if (getSizeX() * getSizeY() * getSizeZ() == pixels) {
        if (getSizeC() == 2 && getSizeC() != getSizeZ() &&
          (getSizeX() % 2 == 0) && (getSizeY() % 2 == 0) && (i == 0 ||
           core.get(i - 1).sizeC > 1))
        {
          ms.sizeX /= 2;
          divByTwo = true;
        }
        else {
          ms.sizeC = 1;
          ms.sizeZ = (int) (pixels / (getSizeX() * getSizeY()));
        }
      }
      else if (getSizeX() * getSizeY() * getSizeC() == pixels) {
        ms.sizeC = (int) (pixels / (getSizeX() * getSizeY()));
        ms.sizeZ = 1;
      }
      else if ((getSizeX() / 2) * (getSizeY() / 2) * getSizeZ() == pixels) {
        ms.sizeX /= 2;
        ms.sizeY /= 2;
      }
      else if ((getSizeX() / 2) * (getSizeY() / 2) * getSizeC() *
        getSizeZ() * getSizeT() == pixels)
      {
        ms.sizeX /= 2;
        ms.sizeY /= 2;
      }
      else {
        boolean validSizes = true;
        try {
          DataTools.safeMultiply32(getSizeX(), getSizeY());
        }
        catch (IllegalArgumentException e) {
          validSizes = false;
        }
        if (getSizeX() == 0 || getSizeY() == 0 || !validSizes) {
          ms.sizeX = sizeX[index] / 256;
          ms.sizeY = sizeY[index] / 256;
        }
        long p = pixels / (getSizeX() * getSizeY());
        if (pixels == p * getSizeX() * getSizeY()) {
          if (p != getSizeC() * getSizeZ()) {
            if (getSizeC() > 1 && core.get(i).sizeZ >= (p / (getSizeC() - 1)) &&
              p >= getSizeC() - 1 && p > 2)
            {
              core.get(i).sizeC--;
              core.get(i).sizeZ = (int) (p / getSizeC());
            }
            else if (p % getSizeC() != 0) {
              core.get(i).sizeC = 1;
              core.get(i).sizeZ = (int) p;
            }
            else if (ms.sizeZ == p + 1) {
              ms.sizeC = 1;
              ms.sizeZ = 1;
              ms.sizeT = (int) p;
            }
            else if (getSizeC() > 1 &&
              ms.sizeZ == (p / (getSizeC() - 1)) + 1)
            {
              ms.sizeC--;
              ms.sizeZ = 1;
              ms.sizeT = (int) (p / getSizeC());
            }
            else {
              if (p > getSizeZ() && (p / getSizeZ() < getSizeZ() - 1)) {
                ms.sizeT = (int) (p / getSizeC());
                ms.sizeZ = 1;
              }
              else if (pixels % getSizeX() == 0 && pixels % getSizeY() == 0) {
                while (getSizeX() * getSizeY() > plane) {
                  ms.sizeX /= 2;
                  ms.sizeY /= 2;
                }
                int originalX = getSizeX();
                while (getSizeX() * getSizeY() < plane) {
                  ms.sizeX += originalX;
                  ms.sizeY = (int) (plane / getSizeX());
                }
                int newX = getSizeX() + originalX;
                if (newX * (plane / newX) == plane && !flattened) {
                  ms.sizeX = newX;
                  ms.sizeY = (int) (plane / newX);
                }
              }
              else if (!adjust) {
                ms.sizeZ = (int) (p / getSizeC());
              }
              else if (isMontage) {
                pixels /= getSizeC();
                while (pixels != getSizeX() * getSizeY() ||
                  (getSizeY() / getSizeX() > 2))
                {
                  ms.sizeX += 16;
                  ms.sizeY = (int) (pixels / getSizeX());
                }
              }
            }
          }
        }
        else if (isSpool) {
          ms.sizeZ = (int) (p / getSizeC());
        }
        else if (p == 0) {
          adjust = true;
          if (getSizeC() > 1) {
            if (getSizeC() == 3) {
              ms.sizeC = 2;
            }
            else {
              ms.sizeC = 1;
            }
          }
        }
        else {
          if (ms.sizeC > 1 && p <= ms.sizeC) {
            int z = getSizeZ();
            ms.sizeZ = 1;
            ms.sizeC = (int) p;
            ms.sizeT = 1;

            if (isMontage && pixels == getSizeX() * (pixels / getSizeX())) {
              pixels /= getSizeC();
              while (pixels != getSizeX() * getSizeY()) {
                ms.sizeX -= 16;
                ms.sizeY = (int) (pixels / getSizeX());
              }
            }
            else if (!isMontage) {
              ms.sizeZ = z;
              adjust = true;
            }
          }
          else if (isMontage) {
            pixels /= (getSizeC() * getSizeZ());
            int originalX = getSizeX();
            int originalY = getSizeY();
            boolean xGreater = getSizeX() > getSizeY();
            while (getSizeX() * getSizeY() != 0 && (
              pixels % (getSizeX() * getSizeY()) != 0 ||
              ((double) getSizeY() / getSizeX() > 2)))
            {
              ms.sizeX += originalX;
              ms.sizeY = (int) (pixels / getSizeX());
              if (!xGreater && getSizeX() >= getSizeY()) {
                break;
              }
            }
            if (getSizeX() * getSizeY() == 0) {
              if (pixels != getSizeX() * getSizeY()) {
                pixels *= getSizeC() * getSizeZ();
                ms.sizeX = originalX;
                ms.sizeY = originalY;
                isMontage = false;
              }
            }
            if (pixels % (originalX - (originalX / 4)) == 0) {
              int newX = originalX - (originalX / 4);
              int newY = (int) (pixels / newX);
              if (newX * newY == pixels) {
                ms.sizeX = newX;
                ms.sizeY = newY;
                isMontage = true;
                adjust = false;
              }
            }
          }
          else if (p != getSizeZ() * getSizeC()) {
            if (pixels % getSizeX() == 0 && pixels % getSizeY() == 0) {
              while (getSizeX() * getSizeY() > plane) {
                ms.sizeX /= 2;
                ms.sizeY /= 2;
              }
            }
            else {
              ms.sizeZ = 1;
              ms.sizeC = 1;
              ms.sizeT = (int) p;
            }
          }
        }
      }

      if (getSizeC() == 0) {
        ms.sizeC = 1;
      }
      if (getSizeZ() == 0) {
        ms.sizeZ = 1;
      }

      int div = getSizeC() * getSizeZ();
      if (getSizeT() > 0) {
        div *= getSizeT();
      }
      if (div > 1) {
        plane = pixels / div;
      }

      long diff = 2 * (pixels - (getSizeX() * getSizeY() * div));
      if ((pixelLengths.get(index).longValue() % 2) == 1) {
        diff++;
      }

      if (Math.abs(diff) > plane / 2) {
        diff = 0;
      }

      if (adjust && diff == 0) {
        double ratio = (double) getSizeX() / getSizeY();
        boolean widthGreater = getSizeX() > getSizeY();
        while (getSizeX() * getSizeY() > plane) {
          if (x) ms.sizeX /= 2;
          else ms.sizeY /= 2;
          x = !x;
        }
        if (getSizeX() * getSizeY() != plane) {
          while (ratio - ((double) getSizeX() / getSizeY()) >= 0.01) {
            boolean first = true;
            while (first || getSizeX() * getSizeY() < plane ||
              (getSizeX() < getSizeY() && widthGreater))
            {
              if (first) {
                first = false;
              }
              ms.sizeX++;
              ms.sizeY = (int) (plane / getSizeX());
            }
          }
        }
      }

      int nPlanes = getSizeZ() * getSizeC();
      ms.sizeT = (int) (pixels / (getSizeX() * getSizeY() * nPlanes));
      while (getSizeX() * getSizeY() * nPlanes * getSizeT() > pixels) {
        ms.sizeT--;
      }
      if (getSizeT() == 0) ms.sizeT = 1;

      if (cGreater && getSizeC() == 1 && getSizeZ() > 1) {
        ms.sizeC = getSizeZ();
        ms.sizeZ = 1;
      }

      ms.imageCount = nPlanes * getSizeT();
      ms.pixelType = FormatTools.UINT16;
      ms.dimensionOrder = nBlocks > 1 ? "XYZCT" : "XYZTC";
      ms.indexed = false;
      ms.falseColor = false;
      ms.metadataComplete = true;

      planeOffset[i] = new long[getImageCount()];
      int nextImage = 0;
      Integer pixelIndex = i;
      long offset = pixelOffsets.get(pixelIndex);
      int planeSize = getSizeX() * getSizeY() * 2;

      if (diff < planeSize) {
        offset += diff;
      }
      else {
        offset += (diff % planeSize);
      }

      long length = pixelLengths.get(pixelIndex);
      int planes = (int) (length / planeSize);
      if (planes > ms.imageCount) {
        planes = ms.imageCount;
      }

      for (int p=0; p<planes; p++, nextImage++) {
        int[] zct = getZCTCoords(p);
        if (flattened && zct[0] == 0 && zct[1] == 0) {
          offset = pixelOffsets.get(nextOffsetIndex++);

          if (zct[2] > 0 && planeOffset[i][nextImage - 1] % 2 != offset % 2 &&
            (offset - planeOffset[i][nextImage - 1] > 3 * getSizeX() * getSizeY()) &&
            diff == 0)
          {
            diff = 31;
          }
          if (diff < planeSize) {
            offset += diff;
          }
          else {
            offset += (diff % planeSize);
          }

          planeOffset[i][nextImage] = offset;
        }
        else if (flattened && zct[0] == 0) {
          int idx = getIndex(0, 0, zct[2]);
          planeOffset[i][nextImage] = planeOffset[i][idx] + zct[1] * planeSize;
        }
        else if (flattened) {
          planeOffset[i][nextImage] = planeOffset[i][nextImage - 1] + planeSize;
        }
        else if (nextImage < planeOffset[i].length) {
          planeOffset[i][nextImage] = offset + p * planeSize;
        }
      }
    }
    setSeries(0);

    if (pixelSizeZ.size() > 0) {
      int seriesIndex = 0;
      for (int q=0; q<getSeriesCount(); q++) {
        CoreMetadata msq = core.get(q);
        int inc = msq.sizeC * msq.sizeT;
        if (seriesIndex + inc > pixelSizeZ.size()) {
          int z = msq.sizeT;
          msq.sizeT = msq.sizeZ;
          msq.sizeZ = z;
          inc = msq.sizeC * msq.sizeT;
        }
        seriesIndex += inc;
      }
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    // populate Image data

    for (int i=0; i<getSeriesCount(); i++) {
      if (imageNames[i] != null) store.setImageName(imageNames[i], i);
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      for (int i=0; i<getSeriesCount(); i++) {
        if (imageDescriptions.containsKey(i)) {
          store.setImageDescription(imageDescriptions.get(i), i);
        }
        else {
          store.setImageDescription("", i);
        }
      }

      // link Instrument and Image
      String instrumentID = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrumentID, 0);
      for (int i=0; i<getSeriesCount(); i++) {
        store.setImageInstrumentRef(instrumentID, i);
      }

      int index = 0;

      // populate Objective data
      int objectiveIndex = 0;
      for (int i=0; i<getSeriesCount(); i++) {
        String objective = objectives.get(i);
        if (objective != null) {
          store.setObjectiveModel(objective, 0, objectiveIndex);
          store.setObjectiveCorrection(
            MetadataTools.getCorrection("Other"), 0, objectiveIndex);
          store.setObjectiveImmersion(MetadataTools.getImmersion("Other"), 0, objectiveIndex);
          if (magnifications != null && magnifications.get(i) > 0) {
            store.setObjectiveNominalMagnification(
                magnifications.get(i).doubleValue(), 0, objectiveIndex);
          }

          // link Objective to Image
          String objectiveID =
            MetadataTools.createLSID("Objective", 0, objectiveIndex);
          store.setObjectiveID(objectiveID, 0, objectiveIndex);
          if (i < getSeriesCount()) {
            store.setObjectiveSettingsID(objectiveID, i);
          }

          objectiveIndex++;
        }
      }

      // populate Dimensions data

      int exposureIndex = exposureTimes.size() - channelNames.size();
      if (exposureIndex >= 1) {
        exposureIndex++;
      }

      for (int i=0; i<getSeriesCount(); i++) {
        setSeries(i);
        if (pixelSize.get(i) != null) {
          final Double size = pixelSize.get(i).doubleValue();
          Length x = FormatTools.getPhysicalSizeX(size);
          Length y = FormatTools.getPhysicalSizeY(size);
          if (x != null) {
            store.setPixelsPhysicalSizeX(x, i);
          }
          if (y != null) {
            store.setPixelsPhysicalSizeY(y, i);
          }
        }
        int idx = 0;
        for (int q=0; q<i; q++) {
          idx += core.get(q).sizeC * core.get(q).sizeT;
        }

        if (idx < pixelSizeZ.size() && pixelSizeZ.get(idx) != null) {
          Length z = FormatTools.getPhysicalSizeZ(pixelSizeZ.get(idx));
          if (z != null) {
            store.setPixelsPhysicalSizeZ(z, i);
          }
        }

        for (int plane=0; plane<getImageCount(); plane++) {
          int c = getZCTCoords(plane)[1];
          if (exposureIndex + c < exposureTimes.size() &&
            exposureIndex + c >= 0 &&
            exposureTimes.get(exposureIndex + c) != null)
          {
            store.setPlaneExposureTime(
              new Time(exposureTimes.get(exposureIndex + c).doubleValue(),
                       UNITS.SECOND), i, plane);
          }
        }
        exposureIndex += getSizeC();
      }
      setSeries(0);

      // populate LogicalChannel data

      for (int i=0; i<getSeriesCount(); i++) {
        setSeries(i);
        for (int c=0; c<getSizeC(); c++) {
          if (index < channelNames.size() && channelNames.get(index) != null) {
            store.setChannelName(channelNames.get(index), i, c);
            addSeriesMetaList("channel", channelNames.get(index));
          }
          if (index < ndFilters.size() && ndFilters.get(index) != null) {
            store.setChannelNDFilter(ndFilters.get(index), i, c);
            addSeriesMeta("channel " + c + " Neutral density",
              ndFilters.get(index));
          }
          index++;
        }
      }
      setSeries(0);
    }
  }

  // -- Helper methods --

  private boolean withinPixels(long offset) {
    for (int i=0; i<pixelOffsets.size(); i++) {
      long pixelOffset = pixelOffsets.get(i).longValue();
      long pixelLength = pixelLengths.get(i).longValue();
      if (offset >= pixelOffset && offset < (pixelOffset + pixelLength)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns true if the given double is greater than epsilon (defined here as
   * 0.000001), i.e. positive and not a very, very small number.
   * Returns false if the given double is negative or less than epsilon.
   * See also: http://en.wikipedia.org/wiki/(%CE%B5,_%CE%B4)-definition_of_limit
   */
  private boolean isGreaterThanEpsilon(double v) {
    return v - Constants.EPSILON > 0;
  }

  private String readCString() throws IOException {
    return in.findString(true, 256, "\0");
  }

}
