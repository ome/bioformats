//
// EPSReader.java
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

import java.io.*;
import java.util.Hashtable;
import java.util.StringTokenizer;
import loci.common.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * Reader is the file format reader for Encapsulated PostScript (EPS) files.
 * Some regular PostScript files are also supported.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/EPSReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/EPSReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class EPSReader extends FormatReader {

  // -- Fields --

  /** Bits per sample. */
  private int bps;

  /** Starting line of pixel data. */
  private int start;

  /** Flag indicating binary data. */
  private boolean binary;

  private boolean isTiff;
  private Hashtable[] ifds;

  // -- Constructor --

  /** Constructs a new EPS reader. */
  public EPSReader() {
    super("Encapsulated PostScript", new String[] {"eps", "epsi", "ps"});
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

    if (isTiff) {
      long[] offsets = TiffTools.getStripOffsets(ifds[0]);
      in.seek(offsets[0]);

      int[] map = TiffTools.getIFDIntArray(ifds[0], TiffTools.COLOR_MAP, false);
      if (map == null) {
        readPlane(in, x, y, w, h, buf);
        return buf;
      }

      byte[] b = new byte[w * h];
      in.skipBytes(2 * y * getSizeX());
      for (int row=0; row<h; row++) {
        in.skipBytes(x * 2);
        for (int col=0; col<w; col++) {
          b[row * w + col] = (byte) (in.readShort() & 0xff);
        }
        in.skipBytes(2 * (getSizeX() - w - x));
      }

      for (int i=0; i<b.length; i++) {
        int ndx = b[i] & 0xff;
        for (int j=0; j<getSizeC(); j++) {
          buf[i*getSizeC() + j] = (byte) map[ndx + j*256];
        }
      }

      return buf;
    }

    if (start == 0) {
      throw new FormatException("Vector data not supported.");
    }

    in.seek(0);
    for (int line=0; line<=start; line++) {
      in.readLine();
    }

    int bytes = FormatTools.getBytesPerPixel(getPixelType());
    if (binary) {
      // pixels are stored as raw bytes
      readPlane(in, x, y, w, h, buf);
    }
    else {
      // pixels are stored as a 2 character hexadecimal value
      String pix = in.readString((int) (in.length() - in.getFilePointer()));
      pix = pix.replaceAll("\n", "");
      pix = pix.replaceAll("\r", "");

      int ndx = getSizeC() * y * bytes * getSizeX();
      int destNdx = 0;

      for (int row=0; row<h; row++) {
        ndx += x * getSizeC() * bytes;
        for (int col=0; col<w*getSizeC()*bytes; col++) {
          buf[destNdx++] =
            (byte) Integer.parseInt(pix.substring(2*ndx, 2*(ndx+1)), 16);
          ndx++;
        }
        ndx += getSizeC() * bytes * (getSizeX() - w - x);
      }
    }
    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    bps = start = 0;
    binary = isTiff = false;
    ifds = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("EPSReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    status("Verifying EPS format");

    String line = in.readLine();
    if (!line.trim().startsWith("%!PS")) {
      // read the TIFF preview

      isTiff = true;

      in.order(true);
      in.seek(20);
      int offset = in.readInt();
      int len = in.readInt();

      byte[] b = new byte[len];
      in.seek(offset);
      in.read(b);

      in = new RandomAccessStream(b);
      ifds = TiffTools.getIFDs(in);

      core[0].sizeX = (int) TiffTools.getImageWidth(ifds[0]);
      core[0].sizeY = (int) TiffTools.getImageLength(ifds[0]);
      core[0].sizeZ = 1;
      core[0].sizeT = 1;
      core[0].sizeC = TiffTools.getSamplesPerPixel(ifds[0]);
      core[0].littleEndian = TiffTools.isLittleEndian(ifds[0]);
      core[0].interleaved = true;
      core[0].rgb = getSizeC() > 1;

      bps = TiffTools.getBitsPerSample(ifds[0])[0];
      switch (bps) {
        case 16:
          core[0].pixelType = FormatTools.UINT16;
          break;
        case 32:
          core[0].pixelType = FormatTools.UINT32;
          break;
        default:
          core[0].pixelType = FormatTools.UINT8;
      }

      core[0].imageCount = 1;
      core[0].dimensionOrder = "XYCZT";
      core[0].metadataComplete = true;
      core[0].indexed = false;
      core[0].falseColor = false;

      MetadataStore store =
        new FilterMetadata(getMetadataStore(), isMetadataFiltered());
      MetadataTools.populatePixels(store, this);
      store.setImageName("", 0);
      MetadataTools.setDefaultCreationDate(store, id, 0);
      return;
    }

    status("Finding image data");

    binary = false;

    String image = "image";
    int lineNum = 1;

    line = in.readLine().trim();

    while (line != null && !line.equals("%%EOF")) {
      if (line.endsWith(image)) {
        if (!line.startsWith(image)) {
          if (line.indexOf("colorimage") != -1) core[0].sizeC = 3;
          StringTokenizer t = new StringTokenizer(line, " ");
          try {
            core[0].sizeX = Integer.parseInt(t.nextToken());
            core[0].sizeY = Integer.parseInt(t.nextToken());
            bps = Integer.parseInt(t.nextToken());
          }
          catch (NumberFormatException exc) {
            if (debug) trace(exc);
            core[0].sizeC = Integer.parseInt(t.nextToken());
          }
        }

        start = lineNum;
        break;
      }
      else if (line.startsWith("%%")) {
        if (line.startsWith("%%BoundingBox:")) {
          line = line.substring(14);
          StringTokenizer t = new StringTokenizer(line, " ");
          int originX = Integer.parseInt(t.nextToken().trim());
          int originY = Integer.parseInt(t.nextToken().trim());
          core[0].sizeX = Integer.parseInt(t.nextToken().trim()) - originX;
          core[0].sizeY = Integer.parseInt(t.nextToken().trim()) - originY;

          addMeta("X-coordinate of origin", originX);
          addMeta("Y-coordinate of origin", originY);
        }
        else if (line.startsWith("%%BeginBinary")) {
          binary = true;
        }
        else {
          // parse key/value pairs

          int ndx = line.indexOf(":");
          if (ndx != -1) {
            String key = line.substring(0, ndx);
            String value = line.substring(ndx + 1);
            addMeta(key, value);
          }
        }
      }
      else if (line.startsWith("%ImageData:")) {
        line = line.substring(11);
        StringTokenizer t = new StringTokenizer(line, " ");
        core[0].sizeX = Integer.parseInt(t.nextToken());
        core[0].sizeY = Integer.parseInt(t.nextToken());
        bps = Integer.parseInt(t.nextToken());
        core[0].sizeC = Integer.parseInt(t.nextToken());
        while (t.hasMoreTokens()) {
          image = t.nextToken().trim();
          if (image.length() > 1) {
            image = image.substring(1, image.length() - 1);
          }
        }
      }
      lineNum++;
      line = in.readLine().trim();
    }

    status("Populating metadata");

    if (bps == 0) bps = 8;

    if (getSizeC() == 0) core[0].sizeC = 1;

    core[0].sizeZ = 1;
    core[0].sizeT = 1;
    core[0].dimensionOrder = "XYCZT";
    core[0].pixelType = FormatTools.UINT8;
    core[0].rgb = getSizeC() == 3;
    core[0].interleaved = true;
    core[0].littleEndian = true;
    core[0].imageCount = 1;

    // Populate metadata store

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore();
    MetadataTools.populatePixels(store, this);
    store.setImageName("", 0);
    MetadataTools.setDefaultCreationDate(store, currentId, 0);
  }

}
