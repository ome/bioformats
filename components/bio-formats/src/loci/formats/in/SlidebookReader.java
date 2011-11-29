//
// SlidebookReader.java
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

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * SlidebookReader is the file format reader for 3I Slidebook files.
 * The strategies employed by this reader are highly suboptimal, as we
 * have very little information on the Slidebook format.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/SlidebookReader.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/SlidebookReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class SlidebookReader extends FormatReader {

  // -- Constants --

  public static final long SLD_MAGIC_BYTES_1 = 0x6c000001L;
  public static final long SLD_MAGIC_BYTES_2 = 0xf5010201L;
  public static final long SLD_MAGIC_BYTES_3 = 0xf6010101L;

  // -- Fields --

  private Vector<Long> metadataOffsets;
  private Vector<Long> pixelOffsets;
  private Vector<Long> pixelEnds;
  private Vector<Long> pixelLengths;
  private Vector<Float> ndFilters;

  private boolean adjust = true;
  private boolean isSpool;
  private Hashtable<Integer, Integer> metadataInPlanes;

  // -- Constructor --

  /** Constructs a new Slidebook reader. */
  public SlidebookReader() {
    super("Olympus Slidebook", new String[] {"sld", "spl"});
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 8;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    long magicBytes = stream.readInt();
    return magicBytes == SLD_MAGIC_BYTES_1 || magicBytes == SLD_MAGIC_BYTES_2;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int plane = FormatTools.getPlaneSize(this);
    long offset = pixelOffsets.get(series).longValue() + plane * no;
    in.seek(offset);

    // if this is a spool file, there may be an extra metadata block here
    if (isSpool) {
      Integer[] keys = metadataInPlanes.keySet().toArray(new Integer[0]);
      Arrays.sort(keys);
      for (int key : keys) {
        if (key < no) {
          in.skipBytes(256);
        }
      }

      in.order(false);
      long magicBytes = (long) in.readInt() & 0xffffffffL;
      in.order(isLittleEndian());
      if (magicBytes == SLD_MAGIC_BYTES_3 && !metadataInPlanes.contains(no)) {
        metadataInPlanes.put(no, 0);
        in.skipBytes(252);
      }
      else in.seek(in.getFilePointer() - 4);
    }

    readPlane(in, x, y, w, h, buf);
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      metadataOffsets = pixelOffsets = pixelLengths = null;
      pixelEnds = null;
      ndFilters = null;
      isSpool = false;
      metadataInPlanes = null;
      adjust = true;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("SlidebookReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    isSpool = checkSuffix(id, "spl");
    if (isSpool) {
      metadataInPlanes = new Hashtable<Integer, Integer>();
    }

    status("Finding offsets to pixel data");

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
    core[0].littleEndian = in.read() == 0x49;
    in.order(isLittleEndian());

    metadataOffsets = new Vector<Long>();
    pixelOffsets = new Vector<Long>();
    pixelEnds = new Vector<Long>();
    pixelLengths = new Vector<Long>();
    ndFilters = new Vector<Float>();

    in.seek(0);

    // gather offsets to metadata and pixel data blocks

    while (in.getFilePointer() < in.length() - 8) {
      debug("Looking for block at " + in.getFilePointer());
      in.skipBytes(4);
      int checkOne = in.read();
      int checkTwo = in.read();
      if ((checkOne == 'I' && checkTwo == 'I') ||
        (checkOne == 'M' && checkTwo == 'M'))
      {
        debug("Found metadata offset: " + (in.getFilePointer() - 6));
        metadataOffsets.add(new Long(in.getFilePointer() - 6));
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
              debug("Found metadata offset: " + (in.getFilePointer() - 2));
              metadataOffsets.add(new Long(in.getFilePointer() - 2));
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

          debug("Found pixel offset at " + fp);
          pixelOffsets.add(new Long(fp));
          try {
            byte[] buf = new byte[8192];
            boolean found = false;
            int n = in.read(buf);

            while (!found && in.getFilePointer() < in.length()) {
              for (int i=0; i<n-6; i++) {
                if (((buf[i] == 'h' || buf[i] == 'i') && buf[i + 1] == 0 &&
                  buf[i + 4] == 'I' && buf[i + 5] == 'I') ||
                  (buf[i] == 0 && (buf[i + 1] == 'h' || buf[i + 1] == 'i') &&
                  buf[i + 4] == 'M' && buf[i + 5] == 'M'))
                {
                  found = true;
                  in.seek(in.getFilePointer() - n + i - 20);
                  if (buf[i] == 'i' || buf[i + 1] == 'i') {
                    pixelOffsets.remove(pixelOffsets.size() - 1);
                  }
                  else {
                    pixelEnds.add(in.getFilePointer());
                  }
                  break;
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
                  pixelOffsets.setElementAt(fp + 2, pixelOffsets.size() - 1);
                  length -= 2;
                }
                pixelLengths.add(new Long(length));
              }
            }
            else {
              pixelOffsets.remove(pixelOffsets.size() - 1);
            }
          }
          catch (EOFException e) {
            pixelOffsets.remove(pixelOffsets.size() - 1);
          }
        }
      }
    }

    for (int i=0; i<pixelOffsets.size(); i++) {
      long length = pixelLengths.get(i).longValue();
      long offset = pixelOffsets.get(i).longValue();

      int padding = isSpool ? 0 : 7;

      if (length + offset + padding > in.length()) {
        pixelOffsets.remove(i);
        pixelLengths.remove(i);
        i--;
      }
    }

    if (pixelOffsets.size() > 1) {
      boolean little = isLittleEndian();
      core = new CoreMetadata[pixelOffsets.size()];
      for (int i=0; i<getSeriesCount(); i++) {
        core[i] = new CoreMetadata();
        core[i].littleEndian = little;
      }
    }

    status("Determining dimensions");

    // determine total number of pixel bytes

    Vector<Float> pixelSize = new Vector<Float>();
    String objective = null;
    Vector<Float> pixelSizeZ = new Vector<Float>();

    long pixelBytes = 0;
    for (int i=0; i<pixelLengths.size(); i++) {
      pixelBytes += pixelLengths.get(i).longValue();
    }

    String[] imageNames = new String[getSeriesCount()];
    Vector<String> channelNames = new Vector<String>();
    int nextName = 0;

    // try to find the width and height
    int iCount = 0;
    int hCount = 0;
    int uCount = 0;
    int prevSeries = -1;
    int prevSeriesU = -1;
    int nextChannel = 0;
    for (int i=0; i<metadataOffsets.size(); i++) {
      long off = metadataOffsets.get(i).longValue();
      in.seek(off);
      long next = i == metadataOffsets.size() - 1 ? in.length() :
        metadataOffsets.get(i + 1).longValue();
      int totalBlocks = (int) ((next - off) / 128);

      long prevOffset = 0;

      // if there are more than 100 blocks, we probably found a pixel block
      // by accident (but we'll check the first block anyway)
      if (totalBlocks > 1000) totalBlocks = 1;
      for (int q=0; q<totalBlocks; q++) {
        if (withinPixels(off + q * 128)) break;
        in.seek(off + q * 128);
        char n = (char) in.readShort();

        if (in.getFilePointer() < prevOffset) {
          in.seek(prevOffset);
        }

        if (in.getFilePointer() == prevOffset) {
          if (in.getFilePointer() + 256 < in.length()) {
            in.skipBytes(254);
            n = (char) in.readShort();
          }
          else break;
        }

        if (!isValidCharacter(n)) {
          while (!isValidCharacter(n) && in.getFilePointer() + 2 < in.length())
          {
            n = (char) in.readShort();
          }
        }
        if (!isValidCharacter(n)) {
          break;
        }
        prevOffset = in.getFilePointer();

        if (n == 'i') {
          iCount++;

          in.skipBytes(14);

          for (int j=0; j<pixelOffsets.size(); j++) {
            long end = j == pixelOffsets.size() - 1 ? in.length() :
              pixelOffsets.get(j + 1).longValue();
            if (in.getFilePointer() < end && core[j].sizeZ == 0) {
              core[j].sizeZ = in.readShort();
              break;
            }
          }

          in.skipBytes(78);
          if (in.getFilePointer() + 4 >= in.length()) {
            break;
          }
          pixelSizeZ.add(new Float(in.readFloat()));
          in.seek(in.getFilePointer() - 20);

          for (int j=0; j<pixelOffsets.size(); j++) {
            long end = j == pixelOffsets.size() - 1 ? in.length() :
              pixelOffsets.get(j + 1).longValue();
            if (in.getFilePointer() < end) {
              if (core[j].sizeX == 0) {
                core[j].sizeX = in.readShort();
                core[j].sizeY = in.readShort();
                int checkX = in.readShort();
                int checkY = in.readShort();
                int div = in.readShort();
                core[j].sizeX /= (div == 0 ? 1 : div);
                div = in.readShort();
                core[j].sizeY /= (div == 0 ? 1 : div);
              }
              if (prevSeries != j) {
                iCount = 1;
              }
              prevSeries = j;
              core[j].sizeC = (int) Math.ceil(Math.sqrt(iCount));
              break;
            }
          }
        }
        else if (n == 'u') {
          uCount++;
          for (int j=0; j<pixelOffsets.size(); j++) {
            long end = j == pixelOffsets.size() - 1 ? in.length() :
              pixelOffsets.get(j + 1).longValue();
            if (in.getFilePointer() < end) {
              if (prevSeriesU != j) {
                uCount = 1;
              }
              prevSeriesU = j;
              break;
            }
          }
        }
        else if (n == 'h') hCount++;
        else if (n == 'j') {
          in.skipBytes(2);
          String check = in.readString(2);
          if (check.equals("II") || check.equals("MM")) {
            // this block should contain an image name
            in.skipBytes(10);
            if (nextName < imageNames.length) {
              imageNames[nextName++] = in.readCString().trim();
            }

            long fp = in.getFilePointer();
            if ((in.getFilePointer() % 2) == 1) in.skipBytes(1);
            while (in.readShort() == 0);
            in.skipBytes(18);
            long baseFP = in.getFilePointer();
            if (in.getFilePointer() - fp > 123 && (fp % 2) == 0) {
              in.seek(fp + 123);
            }
            else {
              in.seek(in.getFilePointer() - 19);
            }

            in.skipBytes(8);

            int x = in.readInt();
            int y = in.readInt();

            if (x <= 0 || y <= 0 || x * y <= 0 || x * y > in.length()) {
              in.seek(baseFP);
              x = in.readInt();
              y = in.readInt();
              if (x <= 0 || y <= 0 || x * y <= 0 || x * y > in.length()) {
                in.seek(baseFP - 12);
                x = in.readInt();
                y = in.readInt();
              }
            }

            int div = in.readShort();
            x /= (div == 0 || div > 64 ? 1 : div);
            div = in.readShort();
            y /= (div == 0 || div > 64 ? 1 : div);

            if (core[nextName - 1].sizeY <= 0) {
              core[nextName - 1].sizeX = 0;
            }
            if (core[nextName - 1].sizeX <= 0) {
              core[nextName - 1].sizeY = 0;
            }

            if (x > 16 && (x < core[nextName - 1].sizeX ||
              core[nextName - 1].sizeX <= 0) && y > 16 &&
              (y < core[nextName - 1].sizeY || core[nextName - 1].sizeY <= 0))
            {
              core[nextName - 1].sizeX = x;
              core[nextName - 1].sizeY = y;
              adjust = false;
            }
          }
        }
        else if (n == 'm') {
          // this block should contain a channel name
          if (in.getFilePointer() > pixelOffsets.get(0).longValue()) {
            in.skipBytes(14);
            channelNames.add(in.readCString().trim());
          }
        }
        else if (n == 'd') {
          // objective info and pixel size X/Y
          in.skipBytes(6);
          long fp = in.getFilePointer();
          objective = in.readCString();
          in.seek(fp + 144);
          pixelSize.add(in.readFloat());
        }
        else if (n == 'e') {
          in.skipBytes(174);
          ndFilters.add(new Float(in.readFloat()));
          in.skipBytes(40);
          if (nextName < getSeriesCount()) {
            setSeries(nextName);
          }
          addSeriesMeta("channel " + ndFilters.size() + " intensification",
            in.readShort());
        }
        else if (n == 'k') {
          in.skipBytes(14);
          if (nextName > 0) setSeries(nextName - 1);
          addSeriesMeta("Mag. changer", in.readCString());
        }
        else if (isSpool) {
          // spool files don't necessarily have block identifiers
          for (int j=0; j<pixelOffsets.size(); j++) {
            long end = j == pixelOffsets.size() - 1 ? in.length() :
              pixelOffsets.get(j + 1).longValue();
            if (in.getFilePointer() < end) {
              in.skipBytes(16);
              core[j].sizeX = in.readShort();
              core[j].sizeY = in.readShort();
              break;
            }
          }
        }
      }
    }


    // prune out any invalid series
    Vector<CoreMetadata> validSeries = new Vector<CoreMetadata>();
    for (int i=0; i<getSeriesCount(); i++) {
      long pixels = pixelLengths.get(i).longValue() / 2;
      if (pixels >= core[i].sizeX * core[i].sizeY) {
        validSeries.add(core[i]);
      }
    }

    core = validSeries.toArray(new CoreMetadata[validSeries.size()]);

    for (int i=0; i<getSeriesCount(); i++) {
      setSeries(i);
      long pixels = pixelLengths.get(i).longValue() / 2;
      boolean x = true;

      if (getSizeC() == 0) core[i].sizeC = 1;
      if (getSizeZ() == 0) core[i].sizeZ = 1;

      long plane = pixels / (getSizeC() * getSizeZ());

      if (adjust) {
        if (getSizeX() == 0 && getSizeY() == 0) {
          core[i].sizeX = (int) Math.ceil(Math.sqrt(plane));
          core[i].sizeY = (int) Math.floor(Math.sqrt(plane));
        }

        boolean widthGreater = getSizeX() > getSizeY();
        while (getSizeX() * getSizeY() > plane) {
          if (x) core[i].sizeX /= 2;
          else core[i].sizeY /= 2;
          x = !x;
        }
        while (getSizeX() * getSizeY() < plane ||
          (getSizeX() < getSizeY() && widthGreater))
        {
          core[i].sizeX++;
          core[i].sizeY = (int) (plane / getSizeX());
        }
      }

      int nPlanes = getSizeZ() * getSizeC();

      core[i].sizeT = (int) (pixels / (getSizeX() * getSizeY() * nPlanes));
      while (getSizeX() * getSizeY() * nPlanes * getSizeT() > pixels) {
        core[i].sizeT--;
      }
      if (getSizeT() == 0) core[i].sizeT = 1;

      if (getSizeX() * getSizeY() * nPlanes * getSizeT() > pixels) {
        if (i > 0) {
          core[i].sizeC = core[0].sizeC;
          core[i].sizeZ = (int) (pixels /
            (getSizeX() * getSizeY() * getSizeC() * getSizeT()));

          // adjust the number of Z planes; the pixel count is likely wrong
          if (pixels >
            getSizeX() * getSizeY() * getSizeC() * getSizeT() * getSizeZ())
          {
            in.seek(pixelOffsets.get(i) + (pixels -
              getSizeX() * getSizeY() * getSizeC() * getSizeT() * getSizeZ()));
            String check = in.readString(4096);
            if (check.indexOf("II") >= 0 && check.indexOf("Annotation") >= 0) {
              core[i].sizeZ--;
            }
          }

          nPlanes = getSizeC() * getSizeZ();
        }
      }

      core[i].imageCount = nPlanes * getSizeT();
      core[i].pixelType = FormatTools.UINT16;
      core[i].dimensionOrder = "XYZTC";
      core[i].indexed = false;
      core[i].falseColor = false;
      core[i].metadataComplete = true;
    }
    setSeries(0);

    for (int i=0; i<getSeriesCount(); i++) {
      setSeries(i);
      long end = pixelEnds.get(i);
      long offset = end - (FormatTools.getPlaneSize(this) * getImageCount());
      pixelOffsets.setElementAt(offset, i);
    }
    setSeries(0);

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);

    // populate Image data

    for (int i=0; i<getSeriesCount(); i++) {
      if (imageNames[i] != null) store.setImageName(imageNames[i], i);
      MetadataTools.setDefaultCreationDate(store, id, i);
    }

    // link Instrument and Image
    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrumentID, 0);
    store.setImageInstrumentRef(instrumentID, 0);

    int index = 0;

    // populate Objective data
    store.setObjectiveModel(objective, 0, 0);
    store.setObjectiveCorrection("Unknown", 0, 0);
    store.setObjectiveImmersion("Unknown", 0, 0);

    // link Objective to Image
    String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
    store.setObjectiveID(objectiveID, 0, 0);
    store.setObjectiveSettingsObjective(objectiveID, 0);

    // populate Dimensions data

    for (int i=0; i<getSeriesCount(); i++) {
      if (i < pixelSize.size()) {
        store.setDimensionsPhysicalSizeX(new Float(pixelSize.get(i)), i, 0);
        store.setDimensionsPhysicalSizeY(new Float(pixelSize.get(i)), i, 0);
      }
      int idx = 0;
      for (int q=0; q<i; q++) {
        idx += core[q].sizeC;
      }

      if (idx < pixelSizeZ.size() && pixelSizeZ.get(idx) != null) {
        store.setDimensionsPhysicalSizeZ(pixelSizeZ.get(idx), i, 0);
      }
    }

    // populate LogicalChannel data

    for (int i=0; i<getSeriesCount(); i++) {
      setSeries(i);
      for (int c=0; c<getSizeC(); c++) {
        if (index < channelNames.size() && channelNames.get(index) != null) {
          store.setLogicalChannelName(channelNames.get(index), i, c);
          addSeriesMeta("channel " + c, channelNames.get(index));
        }
        if (index < ndFilters.size() && ndFilters.get(index) != null) {
          store.setLogicalChannelNdFilter(ndFilters.get(index), i, c);
          addSeriesMeta("channel " + c + " Neutral density",
            ndFilters.get(index));
        }
        index++;
      }
    }
    setSeries(0);
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

  private boolean isValidCharacter(char c) {
    return c == 'd' || c == 'e' || c == 'f' || c == 'g' || c == 'h' ||
      c == 'i' || c == 'j' || c == 'k' || c == 'l' || c == 'm' || c == 'n' ||
      c == 's' || c == 'u';
  }

}
