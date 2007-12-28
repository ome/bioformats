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
import java.util.Vector;

import loci.formats.*;

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

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    int plane = core.sizeX[series] * core.sizeY[series] * 2;

    long offset = ((Long) pixelOffsets.get(series)).longValue() + plane * no;
    in.seek(offset);
    in.read(buf);
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
    in.order(true);

    status("Finding offsets to pixel data");

    in.skipBytes(4);
    core.littleEndian[0] = in.read() == 0x49;

    metadataOffsets = new Vector();
    pixelOffsets = new Vector();
    pixelLengths = new Vector();

    in.seek(0);

    while (in.getFilePointer() < in.length() - 8) {
      in.skipBytes(4);
      int checkOne = in.read();
      int checkTwo = in.read();
      if (checkOne == 'I' && checkTwo == 'I') {
        metadataOffsets.add(new Long(in.getFilePointer() - 6));
        if (in.read() == 0) in.skipBytes(249);
        else in.skipBytes(121);
      }
      else {
        String s = null;
        long fp = in.getFilePointer() - 6;
        in.seek(fp);
        int len = in.read();
        if (len > 0 && len <= 32) {
          byte[] b = new byte[len];
          in.read(b);
          s = new String(b);
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
          in.seek(fp);
          pixelOffsets.add(new Long(fp));
          try {
            byte[] buf = new byte[8192];
            boolean found = false;
            int n = in.read(buf);

            while (!found && in.getFilePointer() < in.length()) {
              for (int i=0; i<buf.length-6; i++) {
                if (buf[i] == 'h' && buf[i+4] == 'I' && buf[i+5] == 'I') {
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

    core = new CoreMetadata(pixelOffsets.size() - 1);

    status("Determining dimensions");

    // determine total number of pixel bytes

    long pixelBytes = 0;
    for (int i=0; i<pixelLengths.size(); i++) {
      pixelBytes += ((Long) pixelLengths.get(i)).longValue();
    }

    // try to find the width and height
    int iCount = 0;
    int hCount = 0;
    int uCount = 0;
    int prevSeries = -1;
    int prevSeriesU = -1;
    for (int i=0; i<metadataOffsets.size(); i++) {
      long off = ((Long) metadataOffsets.get(i)).longValue();
      in.seek(off);
      int n = in.read();
      if (n == 'i') {
        iCount++;
        in.skipBytes(81);
        for (int j=1; j<pixelOffsets.size(); j++) {
          if ((in.getFilePointer() <
            ((Long) pixelOffsets.get(j)).longValue()) ||
            (j == pixelOffsets.size() - 1))
          {
            core.sizeY[j - 1] = in.readShort();
            if (prevSeries != j - 1) {
              iCount = 1;
            }
            prevSeries = j - 1;
            core.sizeC[j - 1] = iCount;
            break;
          }
        }
      }
      else if (n == 'u') {
        uCount++;
        for (int j=1; j<pixelOffsets.size(); j++) {
          if ((in.getFilePointer() <
            ((Long) pixelOffsets.get(j)).longValue()) ||
            (j == pixelOffsets.size() - 1))
          {
            if (prevSeriesU != j - 1) {
              uCount = 1;
            }
            prevSeriesU = j - 1;
            core.sizeT[j - 1] = uCount;
            break;
          }
        }
      }
      else if (n == 'h') hCount++;
    }

    for (int i=0; i<core.sizeX.length; i++) {
      long pixels = ((Long) pixelLengths.get(i)).longValue() / 2;
      core.sizeZ[i] = 1;
      core.imageCount[i] = core.sizeZ[i] * core.sizeT[i] * core.sizeC[i];
      core.sizeX[i] = (int) (pixels / (core.imageCount[i] * core.sizeY[i]));
      core.pixelType[i] = FormatTools.UINT16;
      core.currentOrder[i] = "XYZTC";
      core.littleEndian[i] = true;
      core.indexed[i] = false;
      core.falseColor[i] = false;
      core.metadataComplete[i] = true;
    }

    MetadataStore store = getMetadataStore();
    FormatTools.populatePixels(store, this);

    for (int i=0; i<core.sizeX.length; i++) {
      Integer ii = new Integer(i);
      store.setImage(currentId, null, null, ii);
      for (int j=0; j<core.sizeC[i]; j++) {
        store.setLogicalChannel(j, null, null, null, null, null, null, null,
          null, null, null, null, null, null, null, null, null, null, null,
          null, null, null, null, null, ii);
      }
    }
  }

}
