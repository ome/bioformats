//
// SlidebookReader.java
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

import java.io.*;
import java.util.Arrays;
import java.util.Vector;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * SlidebookReader is the file format reader for 3I Slidebook files.
 * The strategies employed by this reader are highly suboptimal, as we
 * have very little information on the Slidebook format.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/SlidebookReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/SlidebookReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class SlidebookReader extends FormatReader {

  // -- Fields --

  private Vector metadataOffsets;
  private Vector pixelOffsets;
  private Vector pixelLengths;

  // -- Constructor --

  /** Constructs a new Slidebook reader. */
  public SlidebookReader() { super("Olympus Slidebook", "sld"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    if (block.length < 8) return false;
    return block[0] == 0x6c && block[1] == 0 && block[2] == 0 &&
      block[3] == 1 && block[4] == 0x49 && block[5] == 0x49 && block[6] == 0;
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

    int plane = core.sizeX[series] * core.sizeY[series] * 2;

    long offset = ((Long) pixelOffsets.get(series)).longValue() + plane * no;
    in.seek(offset + y * core.sizeX[series] * 2);

    for (int row=0; row<h; row++) {
      in.skipBytes(x * 2);
      in.read(buf, row * w * 2, w * 2);
      in.skipBytes(2 * (core.sizeX[series] - w - x));
    }

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    metadataOffsets = pixelOffsets = pixelLengths = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("SlidebookReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

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
    core.littleEndian[0] = in.read() == 0x49;
    in.order(core.littleEndian[0]);

    metadataOffsets = new Vector();
    pixelOffsets = new Vector();
    pixelLengths = new Vector();

    in.seek(0);

    // gather offsets to metadata and pixel data blocks

    while (in.getFilePointer() < in.length() - 8) {
      in.skipBytes(4);
      int checkOne = in.read();
      int checkTwo = in.read();
      if ((checkOne == 'I' && checkTwo == 'I') ||
        (checkOne == 'M' && checkTwo == 'M'))
      {
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
            in.skipBytes(52);
          }
        }
        else if (s != null && s.indexOf("Decon") != -1) {
          in.seek(fp);
          while (in.read() != ']');
        }
        else {
          if ((fp % 2) == 1) fp -= 2;
          in.seek(fp);
          pixelOffsets.add(new Long(fp));
          try {
            byte[] buf = new byte[8192];
            boolean found = false;
            int n = in.read(buf);

            while (!found && in.getFilePointer() < in.length()) {
              for (int i=0; i<buf.length-6; i++) {
                if ((buf[i] == 'h' && buf[i+4] == 'I' && buf[i+5] == 'I') ||
                  (buf[i+1] == 'h' && buf[i+4] == 'M' && buf[i+5] == 'M'))
                {
                  found = true;
                  in.seek(in.getFilePointer() - n + i - 20);
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
              pixelLengths.add(new Long(in.getFilePointer() - fp));
            }
            else pixelOffsets.remove(pixelOffsets.size() - 1);
          }
          catch (EOFException e) {
            pixelOffsets.remove(pixelOffsets.size() - 1);
          }
        }
      }
    }

    for (int i=0; i<pixelOffsets.size(); i++) {
      long length = ((Long) pixelLengths.get(i)).longValue();
      long offset = ((Long) pixelOffsets.get(i)).longValue();
      in.seek(offset);
      byte checkByte = in.readByte();
      if (length + offset >= in.length()) {
        pixelOffsets.remove(i);
        pixelLengths.remove(i);
        i--;
      }
      else if (checkByte == 'l') {
        long lengthSum = ((Long) pixelLengths.get(0)).longValue();
        while (pixelLengths.size() > 1) {
          int size = pixelLengths.size() - 1;
          lengthSum += ((Long) pixelLengths.get(size)).longValue();
          pixelLengths.remove(size);
          pixelOffsets.remove(size);
        }

        for (int q=0; q<metadataOffsets.size(); q++) {
          long mOffset = ((Long) metadataOffsets.get(q)).longValue();
          if (mOffset > lengthSum) {
            lengthSum = mOffset - offset;
            break;
          }
        }

        pixelLengths.setElementAt(new Long(lengthSum), 0);
        break;
      }
    }

    if (pixelOffsets.size() > 1) {
      boolean little = core.littleEndian[0];
      core = new CoreMetadata(pixelOffsets.size());
      Arrays.fill(core.littleEndian, little);
    }

    status("Determining dimensions");

    // determine total number of pixel bytes

    long pixelBytes = 0;
    for (int i=0; i<pixelLengths.size(); i++) {
      pixelBytes += ((Long) pixelLengths.get(i)).longValue();
    }

    String[] imageNames = new String[core.sizeX.length];
    int nextName = 0;

    // try to find the width and height
    int iCount = 0;
    int hCount = 0;
    int uCount = 0;
    int prevSeries = -1;
    int prevSeriesU = -1;
    for (int i=0; i<metadataOffsets.size(); i++) {
      long off = ((Long) metadataOffsets.get(i)).longValue();
      in.seek(off);
      char n = (char) in.readShort();
      if (n == 'i') {
        iCount++;
        in.skipBytes(78);
        int start = 0;
        for (int j=start; j<pixelOffsets.size(); j++) {
          long length = ((Long) pixelLengths.get(j)).longValue();
          long offset = ((Long) pixelOffsets.get(j)).longValue();
          long end = j == pixelOffsets.size() - 1 ? in.length() :
            ((Long) pixelOffsets.get(j + 1)).longValue();
          if (in.getFilePointer() >= (length + offset) &&
            in.getFilePointer() < end)
          {
            if (core.sizeX[j - start] == 0) {
              core.sizeX[j - start] = in.readShort();
              core.sizeY[j - start] = in.readShort();
              int checkX = in.readShort();
              int checkY = in.readShort();
              int div = in.readShort();
              //if (checkX >= 128 && div == 1) div = 2;
              core.sizeX[j - start] /= div;
              div = in.readShort();
              //if (checkY>= 128 && div == 1) div = 2;
              core.sizeY[j - start] /= div;
            }
            if (prevSeries != j - start) {
              iCount = 1;
            }
            prevSeries = j - start;
            core.sizeC[j - start] = iCount;
            break;
          }
        }
      }
      else if (n == 'u') {
        uCount++;
        int start = 0;
        for (int j=start; j<pixelOffsets.size(); j++) {
          long length = ((Long) pixelLengths.get(j)).longValue();
          long offset = ((Long) pixelOffsets.get(j)).longValue();
          long end = j == pixelOffsets.size() - 1 ? in.length() :
            ((Long) pixelOffsets.get(j + 1)).longValue();
          if (in.getFilePointer() >= (length + offset) &&
            in.getFilePointer() < end)
          {
            if (prevSeriesU != j - start) {
              uCount = 1;
            }
            prevSeriesU = j - start;
            core.sizeZ[j - start] = uCount;
            break;
          }
        }
      }
      else if (n == 'h') hCount++;
      else if (n == 'j') {
        // this block should contain an image name
        in.skipBytes(4);
        in.order(!core.littleEndian[0]);
        int length = in.readShort();
        in.order(core.littleEndian[0]);
        in.skipBytes(3);
        // read image name, removing all non-whitespace and
        // non-alphanumeric characters
        imageNames[nextName++] =
          in.readString(length).replaceAll("[\\W&&[\\S]]", "");
      }
    }

    for (int i=0; i<core.sizeX.length; i++) {
      long pixels = ((Long) pixelLengths.get(i)).longValue() / 2;
      boolean x = true;
      while (core.sizeX[i] * core.sizeY[i] * core.sizeC[i] * core.sizeZ[i] >
        pixels)
      {
        if (x) core.sizeX[i] /= 2;
        else core.sizeY[i] /= 2;
        x = !x;
      }
      if (core.sizeZ[i] == 0) core.sizeZ[i] = 1;
      core.sizeT[i] = (int) (pixels /
        (core.sizeX[i] * core.sizeY[i] * core.sizeZ[i] * core.sizeC[i]));
      if (core.sizeT[i] == 0) core.sizeT[i] = 1;
      core.imageCount[i] = core.sizeZ[i] * core.sizeT[i] * core.sizeC[i];
      core.pixelType[i] = FormatTools.UINT16;
      core.currentOrder[i] = "XYZTC";
      core.indexed[i] = false;
      core.falseColor[i] = false;
      core.metadataComplete[i] = true;
    }

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);

    for (int i=0; i<core.sizeX.length; i++) {
      store.setImageName(imageNames[i], i);
      store.setImageCreationDate(
        DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), i);
    }
  }

}
