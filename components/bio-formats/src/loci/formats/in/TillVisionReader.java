//
// TillVisionReader.java
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

import java.io.*;
import java.util.*;
import loci.common.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * TillVisionReader is the file format reader for TillVision files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/TillVisionReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/TillVisionReader.java">SVN</a></dd></dl>
 */
public class TillVisionReader extends FormatReader {

  // -- Fields --

  private RandomAccessStream[] pixelsStream;

  // -- Constructor --

  /** Constructs a new TillVision reader. */
  public TillVisionReader() {
    super("TillVision", "vws");
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    return false;
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

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    pixelsStream[series].seek(no * getSizeX() * getSizeY() * bpp);
    readPlane(pixelsStream[series], x, y, w, h, buf);

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    if (pixelsStream != null) {
      for (int i=0; i<pixelsStream.length; i++) {
        if (pixelsStream[i] != null) {
          pixelsStream[i].close();
        }
      }
    }
    pixelsStream = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("TillVisionReader.initFile(" + id + ")");
    super.initFile(id);

    POITools poi = new POITools(id);
    Vector documents = poi.getDocumentList();

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    Vector pixelsFile = new Vector();

    for (int i=0; i<documents.size(); i++) {
      String name = (String) documents.get(i);
      if (name.equals("Root Entry/Contents")) {
        RandomAccessStream s = poi.getDocumentStream(name);

        // look for next-to-last occurence of 0x00f03fff

        byte[] b = new byte[(int) s.length()];
        s.read(b);
        int pos = 0;
        int nFound = 0;

        for (int q=b.length - 4; q>=0; q--) {
          if (b[q] == 0 && b[q + 1] == (byte) 0xf0 && b[q + 2] == 0x3f &&
            b[q + 3] == (byte) 0xff)
          {
            nFound++;
            if (nFound == 2) {
              pos = q;
              break;
            }
          }
        }

        s.seek(pos + 26);

        byte[] check = new byte[] {4, 0, 0, 4};

        s.order(false);
        while (s.getFilePointer() < s.length() - 2) {
          int len = s.readShort();
          if (len <= 0) break;
          String imageName = s.readString(len);
          store.setImageName(imageName, pixelsFile.size());
          s.skipBytes(6);
          s.order(true);
          len = s.readShort();
          if (len <= 0) break;
          String description = s.readString(len);
          store.setImageDescription(description, pixelsFile.size());

          // look for first occurence of 0x04000004

          int p = 0;
          while (s.getFilePointer() < s.length() - 64) {
            byte n = s.readByte();
            if (n == check[p]) p++;
            else {
              p = 0;
              if (n == check[p]) p++;
            }
            if (p == 4) break;
          }

          s.skipBytes(6);

          s.order(false);
          len = s.readShort();

          s.skipBytes(len + 7);
          len = s.readShort();

          if (len <= 0 || len > 512) break;
          pixelsFile.add(s.readString(len));
          s.skipBytes(50);
          len = s.readShort();
          if (len < 0) break;
          addMeta("Series " + (pixelsFile.size() - 1) + " palette",
            s.readString(len));
          s.skipBytes(16);
        }
      }
    }

    core = new CoreMetadata[pixelsFile.size()];

    String directory = new Location(currentId).getAbsoluteFile().getParent();

    pixelsStream = new RandomAccessStream[core.length];

    for (int i=0; i<core.length; i++) {
      core[i] = new CoreMetadata();

      // make sure that pixels file exists

      String file = (String) pixelsFile.get(i);

      file = file.replaceAll("/", File.separator);
      file = file.replace('\\', File.separatorChar);
      String oldFile = file;

      file = directory + File.separator + oldFile;

      if (!new Location(file).exists()) {
        oldFile = oldFile.substring(oldFile.lastIndexOf(File.separator) + 1);
        file = directory + File.separator + oldFile;
        if (!new Location(file).exists()) {
          throw new FormatException("Could not find pixels file '" + file);
        }
      }

      pixelsStream[i] = new RandomAccessStream(file);

      // read key/value pairs from .inf files

      int dot = file.lastIndexOf(".");
      String infFile = file.substring(0, dot) + ".inf";
      in = new RandomAccessStream(infFile);

      String data = in.readString((int) in.length());
      StringTokenizer lines = new StringTokenizer(data);

      while (lines.hasMoreTokens()) {
        String line = lines.nextToken().trim();
        if (line.startsWith("[") || line.indexOf("=") == -1) {
          continue;
        }

        int equal = line.indexOf("=");
        String key = line.substring(0, equal).trim();
        String value = line.substring(equal + 1).trim();

        addMeta(key, value);

        if (key.equals("Width")) core[i].sizeX = Integer.parseInt(value);
        else if (key.equals("Height")) core[i].sizeY = Integer.parseInt(value);
        else if (key.equals("Bands")) core[i].sizeC = Integer.parseInt(value);
        else if (key.equals("Slices")) core[i].sizeZ = Integer.parseInt(value);
        else if (key.equals("Frames")) core[i].sizeT = Integer.parseInt(value);
        else if (key.equals("Datatype")) {
          int type = Integer.parseInt(value);
          switch (type) {
            case 1:
              core[i].pixelType = FormatTools.INT8;
              break;
            case 2:
              core[i].pixelType = FormatTools.UINT8;
              break;
            case 3:
              core[i].pixelType = FormatTools.INT16;
              break;
            case 4:
              core[i].pixelType = FormatTools.UINT16;
              break;
            default:
              throw new FormatException("Unsupported data type: " + type);
          }
        }
      }

      core[i].imageCount = core[i].sizeZ * core[i].sizeC * core[i].sizeT;
      core[i].rgb = false;
      core[i].littleEndian = true;
      core[i].dimensionOrder = "XYCZT";
      MetadataTools.setDefaultCreationDate(store, id, i);
      in.close();
    }

    MetadataTools.populatePixels(store, this);
  }

}
