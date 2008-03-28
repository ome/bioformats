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
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

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
  private float voxelX = 1f, voxelY = 1f, voxelZ = 1f, voxelC = 1f, voxelT = 1f;

  /** First image. */
  private BufferedImage zeroImage = null;

  // hardware settings
  private String[] gains, voltages, offsets, channelNames, lensNA;
  private String mag, detManu, objManu, comment;
  private Double gamma;

  // -- Constructor --

  /** Constructs a new Fluoview TIFF reader. */
  public FluoviewReader() {
    super("Olympus Fluoview/ABD TIFF", new String[] {"tif", "tiff"});
    blockCheckLen = 524288;
    suffixSufficient = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    try {
      RandomAccessStream stream = new RandomAccessStream(block);
      Hashtable ifd = TiffTools.getFirstIFD(stream);
      stream.close();
      String com = TiffTools.getComment(ifd);
      if (com == null) com = "";
      return com.indexOf(FLUOVIEW_MAGIC_STRING) != -1 &&
        ifd.containsKey(new Integer(MMHEADER)) ||
        ifd.containsKey(new Integer(MMSTAMP));
    }
    catch (IOException e) {
      if (debug) LogTools.trace(e);
    }
    catch (ArrayIndexOutOfBoundsException e) {
      if (debug) LogTools.trace(e);
    }
    return false;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (core.sizeY[0] == TiffTools.getImageLength(ifds[0])) {
      return super.openBytes(no, buf, x, y, w, h);
    }
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length, w, h);

    byte[] b = new byte[w * h *
      getRGBChannelCount() * FormatTools.getBytesPerPixel(core.pixelType[0])];
    super.openBytes(0, buf, x, y, w, h);
    System.arraycopy(b, no*b.length, buf, 0, b.length);
    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatReader#close() */
  public void close() throws IOException {
    super.close();
    voxelX = voxelY = voxelZ = voxelC = voxelT = 1f;
    zeroImage = null;
  }

  // -- Internal BaseTiffReader API methods --

  /* @see loci.formats.BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    // First, we want to determine whether this file is a Fluoview TIFF.
    // Originally, Andor TIFF had its own reader; however, the two formats are
    // very similar, so it made more sense to merge the two formats into one
    // reader.

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
        if (core.sizeY[0] == 0) core.sizeY[0] = size;
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
    if (core.sizeZ[0] > ifds.length) core.sizeZ[0] = ifds.length;
    if (core.sizeT[0] > ifds.length) core.sizeT[0] = ifds.length;

    if (core.imageCount[0] == 1 && (core.sizeT[0] == core.sizeY[0] ||
      core.sizeZ[0] == core.sizeY[0]) && (core.sizeT[0] > core.imageCount[0] ||
      core.sizeZ[0] > core.imageCount[0]))
    {
      core.sizeY[0] = 1;
      core.imageCount[0] = core.sizeZ[0] * core.sizeT[0] * core.sizeC[0];
    }

    // cut up the comment, if necessary
    comment = TiffTools.getComment(ifds[0]);

    gains = new String[core.sizeC[0]];
    offsets = new String[core.sizeC[0]];
    voltages = new String[core.sizeC[0]];
    channelNames = new String[core.sizeC[0]];
    lensNA = new String[core.sizeC[0]];

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
            if (key.startsWith("Gain Ch")) {
              for (int i=0; i<gains.length; i++) {
                if (gains[i] == null) {
                  gains[i] = value;
                  break;
                }
              }
            }
            else if (key.startsWith("PMT Voltage Ch")) {
              for (int i=0; i<voltages.length; i++) {
                if (voltages[i] == null) {
                  voltages[i] = value;
                  break;
                }
              }
            }
            else if (key.startsWith("Offset Ch")) {
              for (int i=0; i<offsets.length; i++) {
                if (offsets[i] == null) {
                  offsets[i] = value;
                  break;
                }
              }
            }
            else if (key.equals("Magnification")) mag = value;
            else if (key.equals("System Configuration")) detManu = value;
            else if (key.equals("Objective Lens")) objManu = value;
            else if (key.equals("Gamma")) gamma = new Double(value);
            else if (key.startsWith("Channel ") && key.endsWith("Dye")) {
              for (int i=0; i<channelNames.length; i++) {
                if (channelNames[i] == null) {
                  channelNames[i] = value;
                  break;
                }
              }
            }
            else if (key.startsWith("Confocal Aperture-Ch")) {
              for (int i=0; i<lensNA.length; i++) {
                if (lensNA[i] == null) {
                  lensNA[i] = value.substring(0, value.length() - 2);
                  break;
                }
              }
            }
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
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    store.setImageName("", 0);
    store.setImageDescription(comment, 0);
    store.setImageCreationDate(
      DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), 0);

    // populate Dimensions
    store.setDimensionsPhysicalSizeX(new Float(voxelX), 0, 0);
    store.setDimensionsPhysicalSizeY(new Float(voxelY), 0, 0);
    store.setDimensionsPhysicalSizeZ(new Float(voxelZ), 0, 0);
    store.setDimensionsTimeIncrement(new Float(voxelT), 0, 0);
    if ((int) voxelC > 0) {
      store.setDimensionsWaveIncrement(new Integer((int) voxelC), 0, 0);
    }

    for (int i=0; i<core.sizeC[0]; i++) {
      if (channelNames[i] != null) {
        store.setLogicalChannelName(channelNames[i].trim(), 0, i);
      }
      if (lensNA[i] != null) {
        store.setObjectiveLensNA(new Float(lensNA[i]), 0, i);
      }
      //if (gains[i] != null) {
      //  store.setDetectorSettingsGain(new Float(gains[i]), 0, i);
      //}
      //if (offsets[i] != null) {
      //  store.setDetectorSettingsOffset(new Float(offsets[i]), 0, i);
      //}
    }

    /*
    for (int i=0; i<core.sizeC[0]; i++) {
      // CTR CHECK
//      store.setDisplayChannel(new Integer(i), null, null,
//        gamma == null ? null : new Float(gamma.floatValue()), null);

      if (voltages[i] != null) {
        if (detManu != null) store.setDetectorManufacturer(detManu, 0, 0);
        store.setDetectorVoltage(new Float(voltages[i]), 0, 0);
      }
    }

    if (mag != null && mag.toLowerCase().endsWith("x")) {
      mag = mag.substring(0, mag.length() - 1);
    }
    else if (mag == null) mag = "1";

    if (objManu != null) store.setObjectiveManufacturer(objManu, 0, 0);
    if (mag != null) {
      store.setObjectiveCalibratedMagnification(new Float(mag), 0, 0);
    }
    */
  }

}
