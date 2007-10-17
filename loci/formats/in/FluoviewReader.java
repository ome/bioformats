//
// FluoviewReader.java
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

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import loci.formats.*;

/**
 * FluoviewReader is the file format reader for
 * Olympus Fluoview TIFF files AND Andor Bio-imaging Division (ABD) TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/FluoviewReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/FluoviewReader.java">SVN</a></dd></dl>
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class FluoviewReader extends BaseTiffReader {

  // -- Constants --

  /** Maximum number of bytes to check for Fluoview header information. */
  private static final int BLOCK_CHECK_LEN = 16384;

  /** String identifying a Fluoview file. */
  private static final String FLUOVIEW_MAGIC_STRING = "FLUOVIEW";

  /** Private TIFF tags */
  private static final int MMHEADER = 34361;
  private static final int MMSTAMP = 34362;

  // -- Fields --

  /** Pixel dimensions for this file. */
  private float voxelX = 0f, voxelY = 0f, voxelZ = 0f, voxelC = 0f, voxelT = 0f;

  /** First image. */
  private BufferedImage zeroImage = null;

  // -- Constructor --

  /** Constructs a new Fluoview TIFF reader. */
  public FluoviewReader() {
    super("Olympus Fluoview/ABD TIFF", new String[] {"tif", "tiff"});
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    if (!TiffTools.isValidHeader(block)) return false;

    if (block.length < 3) return false;
    if (block.length < 8) return true;

    String test = new String(block);
    if (test.indexOf(FLUOVIEW_MAGIC_STRING) != -1) {
      return true;
    }

    int ifdlocation = DataTools.bytesToInt(block, 4, true);
    if (ifdlocation < 0 || ifdlocation + 1 > block.length) return false;
    else {
      int ifdnumber = DataTools.bytesToInt(block, ifdlocation, 2, true);
      for (int i=0; i<ifdnumber; i++) {
        if (ifdlocation + 3 + (i*12) > block.length) return false;
        else {
          int ifdtag = DataTools.bytesToInt(block, ifdlocation + 2 + (i*12),
            2, true);
          if (ifdtag == MMHEADER || ifdtag == MMSTAMP) return true;
        }
      }
    }
    return false;
  }

  /* @see loci.formats.IFormatReader#openBytes(int) */
  public byte[] openBytes(int no) throws FormatException, IOException {
    if (core.sizeY[0] == TiffTools.getImageLength(ifds[0])) {
      return super.openBytes(no);
    }
    return openBytes(no, new byte[core.sizeX[0] *
      FormatTools.getBytesPerPixel(core.pixelType[0])]);
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    if (core.sizeY[0] == TiffTools.getImageLength(ifds[0])) {
      return super.openBytes(no, buf);
    }
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    byte[] b = new byte[core.sizeX[0] *
      (int) TiffTools.getImageLength(ifds[0]) *
      getRGBChannelCount() * FormatTools.getBytesPerPixel(core.pixelType[0])];
    super.openBytes(0, b);
    System.arraycopy(b, 0, buf, 0, buf.length);
    return buf;
  }

  /* @see loci.formats.IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    if (core.sizeY[0] == TiffTools.getImageLength(ifds[0])) {
      return super.openImage(no);
    }

    if (zeroImage == null) zeroImage = super.openImage(0);
    return zeroImage.getSubimage(0, no, core.sizeX[0], 1);
  }

  /* @see loci.formats.IFormatReader#close() */
  public void close() throws IOException {
    super.close();
    zeroImage = null;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (!super.isThisType(name, open)) return false; // check extension

    // just checking the filename isn't enough to differentiate between
    // Fluoview and regular TIFF; open the file and check more thoroughly
    return open ? checkBytes(name, BLOCK_CHECK_LEN) : true;
  }

  // -- Internal BaseTiffReader API methods --

  /* @see loci.formats.BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    // First, we want to determine whether this file is a Fluoview TIFF.
    // Originally, Andor TIFF had its own reader; however, the two formats are
    // very similar, so it made more sense to merge the two formats into one
    // reader.

    byte[] buf = new byte[BLOCK_CHECK_LEN];
    in.seek(0);
    in.read(buf);

    short[] s = TiffTools.getIFDShortArray(ifds[0], MMHEADER, true);
    byte[] mmheader = new byte[s.length];
    for (int i=0; i<mmheader.length; i++) {
      mmheader[i] = (byte) s[i];
      if (mmheader[i] < 0) mmheader[i]++;
    }

    RandomAccessStream ras = new RandomAccessStream(mmheader);
    ras.order(isLittleEndian());

    put("Header Flag", ras.readShort());
    put("Image Type", ras.readChar());

    put("Image name", ras.readString(257));

    ras.skipBytes(4); // skip pointer to data field

    put("Number of colors", ras.readInt());
    ras.skipBytes(4); // skip pointer to palette field
    ras.skipBytes(4); // skip pointer to other palette field

    put("Comment size", ras.readInt());
    ras.skipBytes(4); // skip pointer to comment field

    // read dimension information
    String[] names = new String[10];
    int[] sizes = new int[10];
    double[] resolutions = new double[10];
    for (int i=0; i<10; i++) {
      names[i] = ras.readString(16);
      sizes[i] = ras.readInt();
      double origin = ras.readDouble();
      resolutions[i] = ras.readDouble();

      put("Dimension " + (i+1) + " Name", names[i]);
      put("Dimension " + (i+1) + " Size", sizes[i]);
      put("Dimension " + (i+1) + " Origin", origin);
      put("Dimension " + (i+1) + " Resolution", resolutions[i]);
      put("Dimension " + (i+1) + " Units", ras.readString(64));
    }

    ras.skipBytes(4); // skip pointer to spatial position data

    put("Map type", ras.readShort());
    put("Map min", ras.readDouble());
    put("Map max", ras.readDouble());
    put("Min value", ras.readDouble());
    put("Max value", ras.readDouble());

    ras.skipBytes(4); // skip pointer to map data

    put("Gamma", ras.readDouble());
    put("Offset", ras.readDouble());

    // read gray channel data
    put("Gray Channel Name", ras.readString(16));
    put("Gray Channel Size", ras.readInt());
    put("Gray Channel Origin", ras.readDouble());
    put("Gray Channel Resolution", ras.readDouble());
    put("Gray Channel Units", ras.readString(64));

    ras.skipBytes(4); // skip pointer to thumbnail data

    put("Voice field", ras.readInt());
    ras.skipBytes(4); // skip pointer to voice field

    // now we need to read the MMSTAMP data to determine dimension order

    double[][] stamps = new double[8][ifds.length];
    for (int i=0; i<ifds.length; i++) {
      s = TiffTools.getIFDShortArray(ifds[i], MMSTAMP, true);
      byte[] stamp = new byte[s.length];
      for (int j=0; j<s.length; j++) {
        stamp[j] = (byte) s[j];
        if (stamp[j] < 0) stamp[j]++;
      }
      ras = new RandomAccessStream(stamp);

      // each stamp is 8 doubles, representing the position on dimensions 3-10
      for (int j=0; j<8; j++) {
        stamps[j][i] = ras.readDouble();
      }
    }

    // calculate the dimension order and axis sizes

    core.sizeZ[0] = core.sizeC[0] = core.sizeT[0] = 1;
    core.currentOrder[0] = "XY";
    core.metadataComplete[0] = true;

    for (int i=0; i<10; i++) {
      String name = names[i];
      int size = sizes[i];
      float voxel = (float) resolutions[i];
      if (name == null || size == 0) continue;
      name = name.toLowerCase().trim();
      if (name.length() == 0) continue;

      if (name.equals("x")) {
        if (core.sizeX[0] == 0) core.sizeX[0] = size;
        voxelX = voxel;
      }
      else if (name.equals("y")) {
        core.sizeY[0] = size;
        voxelY = voxel;
      }
      else if (name.equals("z") || name.equals("event")) {
        core.sizeZ[0] *= size;
        if (core.currentOrder[0].indexOf("Z") == -1) {
          core.currentOrder[0] += "Z";
        }
        voxelZ = voxel;
      }
      else if (name.equals("ch") || name.equals("wavelength")) {
        core.sizeC[0] *= size;
        if (core.currentOrder[0].indexOf("C") == -1) {
          core.currentOrder[0] += "C";
        }
        voxelC = voxel;
      }
      else {
        core.sizeT[0] *= size;
        if (core.currentOrder[0].indexOf("T") == -1) {
          core.currentOrder[0] += "T";
        }
        voxelT = voxel;
      }
    }

    if (core.currentOrder[0].indexOf("Z") == -1) core.currentOrder[0] += "Z";
    if (core.currentOrder[0].indexOf("T") == -1) core.currentOrder[0] += "T";
    if (core.currentOrder[0].indexOf("C") == -1) core.currentOrder[0] += "C";

    core.imageCount[0] = ifds.length;

    if (core.imageCount[0] == 1 && (core.sizeT[0] == core.sizeY[0] ||
      core.sizeZ[0] == core.sizeY[0]) && (core.sizeT[0] > core.imageCount[0] ||
      core.sizeZ[0] > core.imageCount[0]))
    {
      core.sizeY[0] = 1;
      core.imageCount[0] = core.sizeZ[0] * core.sizeT[0] * core.sizeC[0];
    }

    // cut up the comment, if necessary
    String comment = (String) getMeta("Comment");

    if (comment != null && comment.startsWith("[")) {
      int start = comment.indexOf("[Acquisition Parameters]");
      int end = comment.indexOf("[Acquisition Parameters End]");
      if (start != -1 && end != -1 && end > start) {
        String parms = comment.substring(start + 24, end).trim();

        // this is an INI-style comment, with one key/value pair per line

        StringTokenizer st = new StringTokenizer(parms, "\n");
        while (st.hasMoreTokens()) {
          String token = st.nextToken();
          int eq = token.indexOf("=");
          if (eq != -1) {
            String key = token.substring(0, eq);
            String value = token.substring(eq + 1);
            addMeta(key, value);
          }
        }
      }

      start = comment.indexOf("[Version Info]");
      end = comment.indexOf("[Version Info End]");
      if (start != -1 && end != -1 && end > start) {
        comment = comment.substring(start + 14, end).trim();
        start = comment.indexOf("=") + 1;
        end = comment.indexOf("\n");
        if (end > start) comment = comment.substring(start, end).trim();
        else comment = comment.substring(start).trim();
      }
      else comment = "";
    }
    addMeta("Comment", comment);
  }

  /* @see loci.formats.in.BaseTiffReader#initMetadataStore() */
  protected void initMetadataStore() {
    super.initMetadataStore();
    MetadataStore store = getMetadataStore();
    store.setDimensions(new Float(voxelX), new Float(voxelY),
      new Float(voxelZ), new Float(voxelC), new Float(voxelT), null);

    Double gamma = (Double) getMeta("Gamma");
    for (int i=0; i<core.sizeC[0]; i++) {
      store.setDisplayChannel(new Integer(i), null, null,
        gamma == null ? null : new Float(gamma.floatValue()), null);

      String gain = (String) getMeta("Gain Ch" + (i+1));
      String voltage = (String) getMeta("PMT Voltage Ch" + (i+1));
      String offset = (String) getMeta("Offset Ch" + (i+1));

      if (gain != null || voltage != null || offset != null) {
        store.setDetector((String) getMeta("System Configuration"), null,
          null, null, gain == null ? null : new Float(gain),
          voltage == null ? null : new Float(voltage),
          offset == null ? null : new Float(offset), null, new Integer(i));
      }
    }

    String mag = (String) getMeta("Magnification");
    if (mag != null && mag.toLowerCase().endsWith("x")) {
      mag = mag.substring(0, mag.length() - 1);
    }
    else if (mag == null) mag = "1";
    store.setObjective((String) getMeta("Objective Lens"), null, null, null,
      new Float(mag), null, null);
  }

}
