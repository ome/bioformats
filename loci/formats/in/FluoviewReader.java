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

import java.io.*;
import java.util.*;
import loci.formats.*;

/**
 * FluoviewReader is the file format reader for
 * Olympus Fluoview TIFF files AND Andor Bio-imaging Division (ABD) TIFF files.
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

  /** Flag indicating this is a Fluoview file. */
  private boolean isFluoview;

  /** Pixel dimensions for this file. */
  private float voxelX = 0f, voxelY = 0f, voxelZ = 0f, voxelC = 0f, voxelT = 0f;

  // -- Constructor --

  /** Constructs a new Fluoview TIFF reader. */
  public FluoviewReader() {
    super("Olympus Fluoview/Andor Bio-imaging TIFF",
      new String[] {"tif", "tiff"});
  }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a Fluoview TIFF file. */
  public boolean isThisType(byte[] block) {
    if (!TiffTools.isValidHeader(block)) return false;

    if (block.length < 3) return false;
    if (block.length < 8) return true;

    String test = new String(block);
    if (test.indexOf(FLUOVIEW_MAGIC_STRING) != -1) {
      isFluoview = true;
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

  /* @see loci.formats.IFormatReader#getChannelGlobalMinimum(String, int) */
  public Double getChannelGlobalMinimum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    String s = (String) getMeta("Map min");
    if (s == null) return null;
    return new Double(s);
  }

  /* @see loci.formats.IFormatReader#getChannelGlobalMaximum(String, int) */
  public Double getChannelGlobalMaximum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    String s = (String) getMeta("Map max");
    if (s == null) return null;
    return new Double(s);
  }

  /* @see loci.formats.IFormatReader#isMinMaxPopulated(String) */
  public boolean isMinMaxPopulated(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return getMeta("Min value") != null && getMeta("Max value") != null;
  }

  // -- IFormatHandler API methods --

  /**
   * Checks if the given string is a valid filename for an Fluoview TIFF file.
   * @param open If true, the (existing) file is opened for further analysis,
   *   since the file extension is insufficient to confirm that the file is in
   *   Fluoview TIFF format.
   */
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
    isFluoview = new String(buf).indexOf(FLUOVIEW_MAGIC_STRING) != -1;

    short[] s = TiffTools.getIFDShortArray(ifds[0], MMHEADER, true);
    byte[] mmheader = new byte[s.length];
    for (int i=0; i<mmheader.length; i++) {
      mmheader[i] = (byte) s[i];
      if (mmheader[i] < 0) mmheader[i]++;
    }

    RandomAccessStream ras = new RandomAccessStream(mmheader);
    ras.order(isLittleEndian(currentId));

    put("Header Flag", ras.readShort());
    put("Image Type", (char) ras.read());

    byte[] nameBytes = new byte[257];
    ras.read(nameBytes);
    put("Image name", new String(nameBytes));

    ras.skipBytes(4); // skip pointer to data field

    put("Number of colors", ras.readInt());
    ras.skipBytes(4); // skip pointer to palette field
    ras.skipBytes(4); // skip pointer to other palette field

    put("Comment size", ras.readInt());
    ras.skipBytes(4); // skip pointer to comment field

    // read dimension information
    byte[] dimNameBytes = new byte[16];
    byte[] dimCalibrationUnits = new byte[64];
    for (int i=0; i<10; i++) {
      ras.read(dimNameBytes);
      int size = ras.readInt();
      double origin = ras.readDouble();
      double resolution = ras.readDouble();
      ras.read(dimCalibrationUnits);

      put("Dimension " + (i+1) + " Name", new String(dimNameBytes));
      put("Dimension " + (i+1) + " Size", size);
      put("Dimension " + (i+1) + " Origin", origin);
      put("Dimension " + (i+1) + " Resolution", resolution);
      put("Dimension " + (i+1) + " Units", new String(dimCalibrationUnits));
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
    ras.read(dimNameBytes);
    put("Gray Channel Name", new String(dimNameBytes));
    put("Gray Channel Size", ras.readInt());
    put("Gray Channel Origin", ras.readDouble());
    put("Gray Channel Resolution", ras.readDouble());
    ras.read(dimCalibrationUnits);
    put("Gray Channel Units", new String(dimCalibrationUnits));

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

    sizeZ[0] = sizeC[0] = sizeT[0] = 1;
    currentOrder[0] = "XY";

    for (int i=0; i<10; i++) {
      String name = (String) getMeta("Dimension " + (i+1) + " Name");
      Integer size = (Integer) getMeta("Dimension " + (i+1) + " Size");
      Double voxel = (Double) getMeta("Dimension " + (i+1) + " Resolution");
      if (name == null || size == null || size.intValue() == 0) continue;
      name = name.toLowerCase().trim();
      if (name.length() == 0) continue;

      if (name.equals("x")) {
        sizeX[0] = size.intValue();
        if (voxel != null) voxelX = voxel.floatValue();
      }
      else if (name.equals("y")) {
        sizeY[0] = size.intValue();
        if (voxel != null) voxelY = voxel.floatValue();
      }
      else if (name.equals("z") || name.equals("event")) {
        sizeZ[0] *= size.intValue();
        if (currentOrder[0].indexOf("Z") == -1) currentOrder[0] += "Z";
        if (voxel != null) voxelZ = voxel.floatValue();
      }
      else if (name.equals("ch") || name.equals("wavelength")) {
        sizeC[0] *= size.intValue();
        if (currentOrder[0].indexOf("C") == -1) currentOrder[0] += "C";
        if (voxel != null) voxelC = voxel.floatValue();
      }
      else {
        sizeT[0] *= size.intValue();
        if (currentOrder[0].indexOf("T") == -1) currentOrder[0] += "T";
        if (voxel != null) voxelT = voxel.floatValue();
      }
    }

    if (currentOrder[0].indexOf("Z") == -1) currentOrder[0] += "Z";
    if (currentOrder[0].indexOf("T") == -1) currentOrder[0] += "T";
    if (currentOrder[0].indexOf("C") == -1) currentOrder[0] += "C";

    numImages = ifds.length;

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

  protected void initMetadataStore() {
    super.initMetadataStore();
    try {
      MetadataStore store = getMetadataStore(currentId);
      store.setDimensions(new Float(voxelX), new Float(voxelY),
        new Float(voxelZ), new Float(voxelC), new Float(voxelT), null);

      Double gamma = (Double) getMeta("Gamma");
      for (int i=0; i<sizeC[0]; i++) {
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
    catch (FormatException fe) {
      if (debug) fe.printStackTrace();
    }
    catch (IOException ie) {
      if (debug) ie.printStackTrace();
    }
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new FluoviewReader().testRead(args);
  }

}
