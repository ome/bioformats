//
// FluoviewReader.java
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

import java.io.IOException;
import java.util.StringTokenizer;

import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;

/**
 * FluoviewReader is the file format reader for
 * Olympus Fluoview TIFF files AND Andor Bio-imaging Division (ABD) TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/FluoviewReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/FluoviewReader.java">SVN</a></dd></dl>
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class FluoviewReader extends BaseTiffReader {

  // -- Constants --

  /** String identifying a Fluoview file. */
  private static final String FLUOVIEW_MAGIC_STRING = "FLUOVIEW";

  /** Private TIFF tags */
  private static final int MMHEADER = 34361;
  private static final int MMSTAMP = 34362;

  // -- Fields --

  /** Pixel dimensions for this file. */
  private float voxelX = 1f, voxelY = 1f, voxelZ = 1f, voxelC = 1f, voxelT = 1f;

  private String dimensionOrder;

  // hardware settings
  private String[] gains, voltages, offsets, channelNames, lensNA;
  private String mag, detManu, objManu, comment;
  private Double gamma;

  // -- Constructor --

  /** Constructs a new Fluoview TIFF reader. */
  public FluoviewReader() {
    super("Olympus Fluoview/ABD TIFF", new String[] {"tif", "tiff"});
    suffixSufficient = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser tp = new TiffParser(stream);
    IFD ifd = tp.getFirstIFD();
    if (ifd == null) return false;
    String com = ifd.getComment();
    if (com == null) com = "";
    return com.indexOf(FLUOVIEW_MAGIC_STRING) != -1 &&
      ifd.containsKey(new Integer(MMHEADER)) ||
      ifd.containsKey(new Integer(MMSTAMP));
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    // the 'series' axis can be in any position relative to Z, C and T
    // we need to convert the plane number within the series into an IFD number
    int[] lengths = new int[4];
    int[] pos = getZCTCoords(no);
    int[] realPos = new int[4];
    for (int i=2; i<dimensionOrder.length(); i++) {
      char axis = dimensionOrder.charAt(i);
      if (axis == 'Z') {
        lengths[i - 2] = getSizeZ();
        realPos[i - 2] = pos[0];
      }
      else if (axis == 'C') {
        lengths[i - 2] = getEffectiveSizeC();
        realPos[i - 2] = pos[1];
      }
      else if (axis == 'T') {
        lengths[i - 2] = getSizeT();
        realPos[i - 2] = pos[2];
      }
      else if (axis == 'S') {
        lengths[i - 2] = getSeriesCount();
        realPos[i - 2] = getSeries();
      }
    }

    int image = FormatTools.positionToRaster(lengths, realPos);

    if (getSizeY() == ifds.get(0).getImageLength()) {
      tiffParser.getSamples(ifds.get(image), buf, x, y, w, h);
    }
    else {
      FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
      tiffParser.getSamples(ifds.get(0), buf, x, image, w, 1);
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      voxelX = voxelY = voxelZ = voxelC = voxelT = 1f;
      dimensionOrder = null;
      gains = voltages = offsets = channelNames = lensNA = null;
      mag = detManu = objManu = comment = null;
      gamma = null;
    }
  }

  // -- Internal BaseTiffReader API methods --

  /* @see loci.formats.BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    // First, we want to determine whether this file is a Fluoview TIFF.
    // Originally, Andor TIFF had its own reader; however, the two formats are
    // very similar, so it made more sense to merge the two formats into one
    // reader.

    short[] s = ifds.get(0).getIFDShortArray(MMHEADER, true);
    byte[] mmheader = new byte[s.length];
    for (int i=0; i<mmheader.length; i++) {
      mmheader[i] = (byte) s[i];
      if (mmheader[i] < 0) mmheader[i]++;
    }

    RandomAccessInputStream ras = new RandomAccessInputStream(mmheader);
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

    double[][] stamps = new double[8][ifds.size()];
    for (int i=0; i<ifds.size(); i++) {
      s = ifds.get(i).getIFDShortArray(MMSTAMP, true);
      byte[] stamp = new byte[s.length];
      for (int j=0; j<s.length; j++) {
        stamp[j] = (byte) s[j];
        if (stamp[j] < 0) stamp[j]++;
      }
      ras = new RandomAccessInputStream(stamp);

      // each stamp is 8 doubles, representing the position on dimensions 3-10
      for (int j=0; j<8; j++) {
        stamps[j][i] = ras.readDouble();
      }
    }

    // calculate the dimension order and axis sizes

    dimensionOrder = "XY";
    int seriesCount = 1;
    core[0].sizeZ = core[0].sizeC = core[0].sizeT = 1;

    for (int i=0; i<10; i++) {
      String name = names[i];
      int size = sizes[i];
      float voxel = (float) resolutions[i];
      if (name == null || size == 0) continue;
      name = name.toLowerCase().trim();
      if (name.length() == 0) continue;

      if (name.equals("x")) {
        voxelX = voxel;
      }
      else if (name.equals("y")) {
        voxelY = voxel;
      }
      else if (name.equals("z") || name.equals("event")) {
        core[0].sizeZ *= size;
        if (dimensionOrder.indexOf("Z") == -1) {
          dimensionOrder += "Z";
        }
        voxelZ = voxel;
      }
      else if (name.equals("ch") || name.equals("wavelength")) {
        core[0].sizeC *= size;
        if (dimensionOrder.indexOf("C") == -1) {
          dimensionOrder += "C";
        }
        voxelC = voxel;
      }
      else if (name.equals("time") || name.equals("t") ||
        name.equals("animation"))
      {
        core[0].sizeT *= size;
        if (dimensionOrder.indexOf("T") == -1) {
          dimensionOrder += "T";
        }
        voxelT = voxel;
      }
      else {
        if (dimensionOrder.indexOf("S") == -1) dimensionOrder += "S";
        seriesCount *= size;
      }
    }

    if (dimensionOrder.indexOf("Z") == -1) dimensionOrder += "Z";
    if (dimensionOrder.indexOf("T") == -1) dimensionOrder += "T";
    if (dimensionOrder.indexOf("C") == -1) dimensionOrder += "C";
    if (dimensionOrder.indexOf("S") == -1) dimensionOrder += "S";

    core[0].imageCount = ifds.size() / seriesCount;
    if (getSizeZ() > getImageCount()) core[0].sizeZ = getImageCount();
    if (getSizeT() > getImageCount()) core[0].sizeT = getImageCount();

    if (getImageCount() == 1 && (getSizeT() == getSizeY() ||
      getSizeZ() == getSizeY()) && (getSizeT() > getImageCount() ||
      getSizeZ() > getImageCount()))
    {
      core[0].sizeY = 1;
      core[0].imageCount = getSizeZ() * getSizeC() * getSizeT();
    }
    core[0].dimensionOrder = dimensionOrder.replaceAll("S", "");

    if (seriesCount > 1) {
      CoreMetadata oldCore = core[0];
      core = new CoreMetadata[seriesCount];
      for (int i=0; i<seriesCount; i++) {
        core[i] = oldCore;
      }
    }

    // cut up the comment, if necessary
    comment = ifds.get(0).getComment();

    gains = new String[getSizeC()];
    offsets = new String[getSizeC()];
    voltages = new String[getSizeC()];
    channelNames = new String[getSizeC()];
    lensNA = new String[getSizeC()];

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
            addGlobalMeta(key, value);
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
    addGlobalMeta("Comment", comment);
  }

  /* @see loci.formats.in.BaseTiffReader#initMetadataStore() */
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    store.setImageDescription(comment, 0);

    // link Instrument and Image
    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrumentID, 0);
    store.setImageInstrumentRef(instrumentID, 0);

    // populate Dimensions
    store.setDimensionsPhysicalSizeX(new Float(voxelX), 0, 0);
    store.setDimensionsPhysicalSizeY(new Float(voxelY), 0, 0);
    store.setDimensionsPhysicalSizeZ(new Float(voxelZ), 0, 0);
    store.setDimensionsTimeIncrement(new Float(voxelT), 0, 0);
    if ((int) voxelC > 0) {
      store.setDimensionsWaveIncrement(new Integer((int) voxelC), 0, 0);
    }

    // populate LogicalChannel data

    for (int i=0; i<getSizeC(); i++) {
      if (channelNames[i] != null) {
        store.setLogicalChannelName(channelNames[i].trim(), 0, i);
      }
    }

    // populate Detector data

    for (int i=0; i<getSizeC(); i++) {
      // CTR CHECK
//      store.setDisplayChannel(new Integer(i), null, null,
//        gamma == null ? null : new Float(gamma.floatValue()), null);

      if (voltages[i] != null) {
        if (detManu != null) store.setDetectorManufacturer(detManu, 0, 0);
        store.setDetectorSettingsVoltage(new Float(voltages[i]), 0, 0);
      }
      if (gains[i] != null) {
        store.setDetectorSettingsGain(new Float(gains[i]), 0, i);
      }
      if (offsets[i] != null) {
        store.setDetectorSettingsOffset(new Float(offsets[i]), 0, i);
      }
      store.setDetectorType("Unknown", 0, i);

      // link DetectorSettings to an actual Detector
      String detectorID = MetadataTools.createLSID("Detector", 0, i);
      store.setDetectorID(detectorID, 0, i);
      store.setDetectorSettingsDetector(detectorID, 0, i);
    }

    // populate Objective data

    if (mag != null && mag.toLowerCase().endsWith("x")) {
      mag = mag.substring(0, mag.length() - 1);
    }
    else if (mag == null) mag = "1";

    store.setObjectiveCorrection("Unknown", 0, 0);
    store.setObjectiveImmersion("Unknown", 0, 0);

    if (objManu != null) {
      String[] objectiveData = objManu.split(" ");
      store.setObjectiveModel(objectiveData[0], 0, 0);
      if (objectiveData.length > 2) {
        store.setObjectiveImmersion(objectiveData[2], 0, 0);
      }
    }

    if (mag != null) {
      store.setObjectiveCalibratedMagnification(new Float(mag), 0, 0);
    }

    for (int i=0; i<getSizeC(); i++) {
      if (lensNA[i] != null) {
        store.setObjectiveLensNA(new Float(lensNA[i]), 0, i);
      }
    }

    // link Objective to Image using ObjectiveSettings
    String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
    store.setObjectiveID(objectiveID, 0, 0);
    store.setObjectiveSettingsObjective(objectiveID, 0);
  }

}
