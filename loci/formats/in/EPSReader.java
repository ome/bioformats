//
// EPSReader.java
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
import java.util.Hashtable;
import java.util.StringTokenizer;
import loci.formats.*;
import loci.formats.meta.MetadataStore;

/**
 * Reader is the file format reader for Encapsulated PostScript (EPS) files.
 * Some regular PostScript files are also supported.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/EPSReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/EPSReader.java">SVN</a></dd></dl>
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

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
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
        int bpp = FormatTools.getBytesPerPixel(core.pixelType[0]);
        in.skipBytes(y * core.sizeX[0] * bpp * core.sizeC[0]);
        for (int row=0; row<h; row++) {
          in.skipBytes(x * bpp * core.sizeC[0]);
          in.read(buf, row * w * bpp * core.sizeC[0], w * bpp * core.sizeC[0]);
          in.skipBytes(bpp * core.sizeC[0] * (core.sizeX[0] - w - x));
        }
        return buf;
      }

      byte[] b = new byte[w * h];
      in.skipBytes(2 * y * core.sizeX[0]);
      for (int row=0; row<h; row++) {
        in.skipBytes(x * 2);
        for (int col=0; col<w; col++) {
          b[row * w + col] = (byte) (in.readShort() & 0xff);
        }
        in.skipBytes(2 * (core.sizeX[0] - w - x));
      }

      for (int i=0; i<b.length; i++) {
        int ndx = b[i] & 0xff;
        for (int j=0; j<core.sizeC[0]; j++) {
          buf[i*core.sizeC[0] + j] = (byte) map[ndx + j*256];
        }
      }

      return buf;
    }

    RandomAccessStream ras = new RandomAccessStream(currentId);
    for (int line=0; line<=start; line++) {
      ras.readLine();
    }

    int bytes = FormatTools.getBytesPerPixel(core.pixelType[0]);
    if (binary) {
      ras.skipBytes(y * core.sizeX[0] * bytes * core.sizeC[0]);
      for (int row=0; row<h; row++) {
        ras.skipBytes(x * bytes * core.sizeC[0]);
        ras.read(buf, row*w*bytes*core.sizeC[0], w * bytes * core.sizeC[0]);
        ras.skipBytes(bytes * core.sizeC[0] * (core.sizeX[0] - w - h));
      }
    }
    else {
      String pix = ras.readString((int) (ras.length() - ras.getFilePointer()));
      pix = pix.replaceAll("\n", "");
      pix = pix.replaceAll("\r", "");

      int ndx = core.sizeC[0] * y * bytes * core.sizeX[0];
      int destNdx = 0;

      for (int row=0; row<h; row++) {
        ndx += x * core.sizeC[0] * bytes;
        for (int col=0; col<w*core.sizeC[0]*bytes; col++) {
          buf[destNdx++] =
            (byte) Integer.parseInt(pix.substring(2*ndx, 2*(ndx+1)), 16);
          ndx++;
        }
        ndx += core.sizeC[0] * bytes * (core.sizeX[0] - w - x);
      }
    }
    ras.close();
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
    if (debug) debug("EPSReader.initFile(" + id + ")");
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

      core.sizeX[0] = (int) TiffTools.getImageWidth(ifds[0]);
      core.sizeY[0] = (int) TiffTools.getImageLength(ifds[0]);
      core.sizeZ[0] = 1;
      core.sizeT[0] = 1;
      core.sizeC[0] = TiffTools.getSamplesPerPixel(ifds[0]);
      core.littleEndian[0] = TiffTools.isLittleEndian(ifds[0]);
      core.interleaved[0] = true;
      core.rgb[0] = core.sizeC[0] > 1;

      bps = TiffTools.getBitsPerSample(ifds[0])[0];
      switch (bps) {
        case 16: core.pixelType[0] = FormatTools.UINT16; break;
        case 32: core.pixelType[0] = FormatTools.UINT32; break;
        default: core.pixelType[0] = FormatTools.UINT8;
      }

      core.imageCount[0] = 1;
      core.currentOrder[0] = "XYCZT";
      core.metadataComplete[0] = true;
      core.indexed[0] = false;
      core.falseColor[0] = false;

      MetadataStore store = getMetadataStore();
      store.setImageName("", 0);
      store.setImageCreationDate(
        DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), 0);
      MetadataTools.populatePixels(store, this);
      return;
    }

    status("Finding image data");

    binary = false;

    String image = "image";
    int lineNum = 1;

    line = in.readLine();

    while (line != null) {
      if (line.trim().equals(image) || line.trim().endsWith(image)) {
        if (line.trim().endsWith(image) && !line.trim().startsWith(image)) {
          if (line.indexOf("colorimage") != -1) core.sizeC[0] = 3;
          StringTokenizer t = new StringTokenizer(line, " ");
          try {
            core.sizeX[0] = Integer.parseInt(t.nextToken());
            core.sizeY[0] = Integer.parseInt(t.nextToken());
            bps = Integer.parseInt(t.nextToken());
          }
          catch (NumberFormatException exc) {
            if (debug) trace(exc);
            core.sizeC[0] = Integer.parseInt(t.nextToken());
          }
        }

        start = lineNum;
        break;
      }
      else if (line.startsWith("%%")) {
        if (line.startsWith("%%BoundingBox:")) {
          line = line.substring(14);
          StringTokenizer t = new StringTokenizer(line, " ");
          int originX = Integer.parseInt(t.nextToken());
          int originY = Integer.parseInt(t.nextToken());
          core.sizeX[0] = Integer.parseInt(t.nextToken()) - originX;
          core.sizeY[0] = Integer.parseInt(t.nextToken()) - originY;

          addMeta("X-coordinate of origin", new Integer(originX));
          addMeta("Y-coordinate of origin", new Integer(originY));
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
        core.sizeX[0] = Integer.parseInt(t.nextToken());
        core.sizeY[0] = Integer.parseInt(t.nextToken());
        bps = Integer.parseInt(t.nextToken());
        core.sizeC[0] = Integer.parseInt(t.nextToken());
        while (t.hasMoreTokens()) {
          image = t.nextToken().trim();
          if (image.length() > 1) {
            image = image.substring(1, image.length() - 1);
          }
        }
      }
      lineNum++;
      line = in.readLine();
    }

    status("Populating metadata");

    if (bps == 0) bps = 8;

    if (core.sizeC[0] == 0) core.sizeC[0] = 1;

    core.sizeZ[0] = 1;
    core.sizeT[0] = 1;
    core.currentOrder[0] = "XYCZT";
    core.pixelType[0] = FormatTools.UINT8;
    core.rgb[0] = core.sizeC[0] == 3;
    core.interleaved[0] = true;
    core.littleEndian[0] = true;
    core.imageCount[0] = 1;

    // Populate metadata store

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore();
    store.setImageName("", 0);

    MetadataTools.populatePixels(store, this);
  }

}
