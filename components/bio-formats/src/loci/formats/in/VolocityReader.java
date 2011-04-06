//
// VolocityReader.java
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
import java.util.ArrayList;

import loci.common.ByteArrayHandle;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import ome.metakit.MetakitException;
import ome.metakit.MetakitReader;

import ome.xml.model.primitives.PositiveInteger;


/**
 * VolocityReader is the file format reader for Volocity library files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/VolocityReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/VolocityReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class VolocityReader extends FormatReader {

  // -- Constants --

  private static final String DATA_DIR = "Data";

  private static final int SIGNATURE_SIZE = 13;

  // -- Fields --

  private String[][] pixelsFiles;
  private String[] timestampFiles;
  private ArrayList<String> extraFiles;

  private int[] planePadding;

  private Object[][] sampleTable, stringTable;
  private Location dir = null;
  private int[] blockSize;

  // -- Constructor --

  /** Constructs a new Volocity reader. */
  public VolocityReader() {
    super("Volocity Library", "mvd2");
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    ArrayList<String> files = new ArrayList<String>();
    files.addAll(extraFiles);
    for (int c=0; c<getEffectiveSizeC(); c++) {
      files.add(pixelsFiles[getSeries()][c]);
    }
    files.add(timestampFiles[getSeries()]);
    return files.toArray(new String[files.size()]);
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 2;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    String check = stream.readString(blockLen);
    return check.equals("JL") || check.equals("LJ");
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int[] zct = getZCTCoords(no);

    RandomAccessInputStream pix =
      new RandomAccessInputStream(pixelsFiles[getSeries()][zct[1]]);

    int planeSize = FormatTools.getPlaneSize(this);
    int planesInFile = (int) (pix.length() / planeSize);
    int planeIndex = no / getEffectiveSizeC();
    if (planesInFile == getSizeT()) {
      planeIndex = zct[2];
    }

    int padding = zct[2] * planePadding[getSeries()];

    long offset =
      (long) blockSize[getSeries()] + planeIndex * planeSize + padding;
    if (offset >= pix.length()) {
      return buf;
    }
    pix.seek(offset);
    readPlane(pix, x, y, w, h, buf);
    pix.close();

    if (getRGBChannelCount() == 4) {
      // stored as ARGB, need to swap to RGBA
      for (int i=0; i<buf.length/4; i++) {
        byte a = buf[i * 4];
        buf[i * 4] = buf[i * 4 + 1];
        buf[i * 4 + 1] = buf[i * 4 + 2];
        buf[i * 4 + 2] = buf[i * 4 + 3];
        buf[i * 4 + 3] = a;
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      pixelsFiles = null;
      extraFiles = null;
      timestampFiles = null;
      planePadding = null;
      sampleTable = null;
      stringTable = null;
      dir = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    extraFiles = new ArrayList<String>();

    Location file = new Location(id).getAbsoluteFile();
    extraFiles.add(file.getAbsolutePath());

    Location parentDir = file.getParentFile();
    dir = new Location(parentDir, DATA_DIR);

    if (!dir.exists()) {
      throw new FormatException("Could not find data directory.");
    }

    try {
      MetakitReader reader = new MetakitReader(id);
      sampleTable = reader.getTableData(1);
      stringTable = reader.getTableData(2);
    }
    catch (MetakitException e) {
      throw new FormatException(e);
    }

    ArrayList<String> stackNames = new ArrayList<String>();
    ArrayList<Integer> parentIDs = new ArrayList<Integer>();

    for (int i=0; i<sampleTable.length; i++) {
      Integer stringID = (Integer) sampleTable[i][11];
      String name = getString(stringID);

      int channelIndex = getChildIndex((Integer) sampleTable[i][0], "Channels");

      if (i > 0 && (Integer) sampleTable[i][2] == 1 && (channelIndex >= 0 ||
        (sampleTable[i][14] != null && !sampleTable[i][14].equals(0))))
      {
        if (channelIndex < 0) {
          RandomAccessInputStream s = getStream(i);
          s.seek(22);
          int x = s.readInt();
          int y = s.readInt();
          int z = s.readInt();
          if (x * y * z != 0 && x * y * z < s.length()) {
            stackNames.add(name);
            parentIDs.add((Integer) sampleTable[i][0]);
          }
        }
        else {
          stackNames.add(name);
          parentIDs.add((Integer) sampleTable[i][0]);
        }
      }
    }

    core = new CoreMetadata[parentIDs.size()];
    String[][] channelNames = new String[core.length][];
    Double[] physicalX = new Double[core.length];
    Double[] physicalY = new Double[core.length];
    Double[] physicalZ = new Double[core.length];
    Double[] magnification = new Double[core.length];
    String[] detector = new String[core.length];
    String[] description = new String[core.length];

    pixelsFiles = new String[core.length][];
    timestampFiles = new String[core.length];

    for (int i=0; i<parentIDs.size(); i++) {
      core[i] = new CoreMetadata();
      Integer parent = parentIDs.get(i);

      int channelIndex = getChildIndex(parent, "Channels");
      if (channelIndex >= 0) {
        Integer[] channels =
          getAllChildren((Integer) sampleTable[channelIndex][0]);
        core[i].sizeC = channels.length;
        pixelsFiles[i] = new String[core[i].sizeC];

        channelNames[i] = new String[channels.length];
        for (int c=0; c<channels.length; c++) {
          channelNames[i][c] =
            getString((Integer) sampleTable[channels[c]][11]);

          RandomAccessInputStream data = getStream(channels[c]);
          data.seek(22);
          int stackID = data.readInt();
          pixelsFiles[i][c] =
            new Location(dir, stackID + ".aisf").getAbsolutePath();
          data.close();
        }
      }
      else {
        pixelsFiles[i] = new String[1];
        for (int row=0; row<sampleTable.length; row++) {
          if (parent.equals(sampleTable[row][0])) {
            Object o = sampleTable[row][14];
            if (o != null) {
              String fileLink = o.toString().trim() + ".dat";
              pixelsFiles[i][0] = new Location(dir, fileLink).getAbsolutePath();
            }
            break;
          }
        }
      }

      RandomAccessInputStream data = null;

      int timestampIndex = getChildIndex(parent, "Timepoint times stream");
      if (timestampIndex >= 0) {
        data = getStream(timestampIndex);
        data.seek(22);
        int timestampID = data.readInt();
        timestampFiles[i] =
          new Location(dir, timestampID + ".atsf").getAbsolutePath();
        data.close();
      }

      int xIndex = getChildIndex(parent, "um/pixel (X)");
      if (xIndex >= 0) {
        data = getStream(xIndex);
        data.seek(SIGNATURE_SIZE);
        physicalX[i] = data.readDouble();
        data.close();
      }

      int yIndex = getChildIndex(parent, "um/pixel (Y)");
      if (yIndex >= 0) {
        data = getStream(yIndex);
        data.seek(SIGNATURE_SIZE);
        physicalY[i] = data.readDouble();
        data.close();
      }

      int zIndex = getChildIndex(parent, "um/pixel (Z)");
      if (zIndex >= 0) {
        data = getStream(zIndex);
        data.seek(SIGNATURE_SIZE);
        physicalZ[i] = data.readDouble();
        data.close();
      }

      int objectiveIndex = getChildIndex(parent, "Microscope Objective");
      if (objectiveIndex >= 0) {
        data = getStream(objectiveIndex);
        data.seek(SIGNATURE_SIZE);
        magnification[i] = data.readDouble();
        data.close();
      }

      int detectorIndex = getChildIndex(parent, "Camera/Detector");
      if (detectorIndex >= 0) {
        data = getStream(detectorIndex);
        data.seek(SIGNATURE_SIZE);
        int len = data.readInt();
        detector[i] = data.readString(len);
        data.close();
      }

      int descriptionIndex = getChildIndex(parent, "Experiment Description");
      if (descriptionIndex >= 0) {
        data = getStream(descriptionIndex);
        data.seek(SIGNATURE_SIZE);
        int len = data.readInt();
        description[i] = data.readString(len);
        data.close();
      }
    }

    planePadding = new int[core.length];
    blockSize = new int[core.length];

    double[][][] stamps = new double[core.length][][];

    for (int i=0; i<core.length; i++) {
      setSeries(i);

      core[i].littleEndian = true;

      if (timestampFiles[i] != null) {
        RandomAccessInputStream s =
          new RandomAccessInputStream(timestampFiles[i]);
        s.seek(17);
        s.order(isLittleEndian());
        core[i].sizeT = s.readInt();
        s.close();
      }
      else {
        core[i].sizeT = 1;
      }

      core[i].rgb = false;
      core[i].interleaved = true;
      core[i].dimensionOrder = "XYCZT";

      RandomAccessInputStream s =
        new RandomAccessInputStream(pixelsFiles[i][0]);
      s.order(isLittleEndian());

      if (checkSuffix(pixelsFiles[i][0], "aisf")) {
        s.seek(18);
        blockSize[i] = s.readShort() * 256;
        s.skipBytes(5);
        int x = s.readInt();
        int y = s.readInt();
        int zStart = s.readInt();
        int w = s.readInt();
        int h = s.readInt();

        core[i].sizeX = w - x;
        core[i].sizeY = h - y;
        core[i].sizeZ = s.readInt() - zStart;
        core[i].imageCount = getSizeZ() * getSizeC() * getSizeT();

        int planesPerFile = getSizeZ() * getSizeT();
        int planeSize = FormatTools.getPlaneSize(this);
        int bytesPerPlane = (int) ((s.length() - blockSize[i]) / planesPerFile);

        int bytesPerPixel = 0;
        while (bytesPerPlane >= planeSize) {
          bytesPerPixel++;
          bytesPerPlane -= planeSize;
        }

        if ((bytesPerPixel % 3) == 0) {
          core[i].sizeC *= 3;
          core[i].rgb = true;
          bytesPerPixel /= 3;
        }

        core[i].pixelType =
          FormatTools.pixelTypeFromBytes(bytesPerPixel, false, false);

        // full timepoints are padded to have a multiple of 256 bytes
        int timepoint = FormatTools.getPlaneSize(this) * getSizeZ();
        planePadding[i] = blockSize[i] - (timepoint % blockSize[i]);
        if (planePadding[i] == blockSize[i]) {
          planePadding[i] = 0;
        }
      }
      else {
        s.seek(22);
        core[i].sizeX = s.readInt();
        core[i].sizeY = s.readInt();
        core[i].sizeZ = s.readInt();
        core[i].sizeC = 4;
        core[i].imageCount = getSizeZ() * getSizeT();
        core[i].rgb = true;
        core[i].pixelType = FormatTools.UINT8;
        blockSize[i] = 99;
        planePadding[i] = 0;
      }
      s.close();
    }
    setSeries(0);

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    String instrument = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrument, 0);

    for (int i=0; i<getSeriesCount(); i++) {
      store.setImageInstrumentRef(instrument, i);

      setSeries(i);
      store.setImageName(stackNames.get(i), i);
      store.setImageDescription(description[i], i);
      if (channelNames[i] != null) {
        for (int c=0; c<getEffectiveSizeC(); c++) {
          store.setChannelName(channelNames[i][c], i, c);
        }
      }
      store.setPixelsPhysicalSizeX(physicalX[i], i);
      store.setPixelsPhysicalSizeY(physicalY[i], i);
      store.setPixelsPhysicalSizeZ(physicalZ[i], i);

      String objective = MetadataTools.createLSID("Objective", 0, i);
      store.setObjectiveID(objective, 0, i);
      if (magnification[i] != null) {
        store.setObjectiveNominalMagnification(
          new PositiveInteger(magnification[i].intValue()), 0, i);
      }
      store.setObjectiveCorrection(getCorrection("Other"), 0, i);
      store.setObjectiveImmersion(getImmersion("Other"), 0, i);
      store.setImageObjectiveSettingsID(objective, i);

      String detectorID = MetadataTools.createLSID("Detector", 0, i);
      store.setDetectorID(detectorID, 0, i);
      store.setDetectorModel(detector[i], 0, i);

      for (int c=0; c<getEffectiveSizeC(); c++) {
        store.setDetectorSettingsID(detectorID, i, c);
      }
    }
    setSeries(0);
  }

  private String getString(Integer stringID) {
    for (int row=0; row<stringTable.length; row++) {
      if (stringID.equals(stringTable[row][0])) {
        String s = (String) stringTable[row][1];
        if (s != null) {
          s = s.trim();
        }
        return s;
      }
    }
    return null;
  }

  private int getChildIndex(Integer parentID, String childName) {
    for (int row=0; row<sampleTable.length; row++) {
      if (parentID.equals(sampleTable[row][1])) {
        String name = getString((Integer) sampleTable[row][11]);
        if (childName.equals(name)) {
          return row;
        }
      }
    }
    return -1;
  }

  private Integer[] getAllChildren(Integer parentID) {
    ArrayList<Integer> children = new ArrayList<Integer>();
    for (int row=0; row<sampleTable.length; row++) {
      if (parentID.equals(sampleTable[row][1])) {
        children.add(row);
      }
    }
    return children.toArray(new Integer[children.size()]);
  }

  private RandomAccessInputStream getStream(int row) throws IOException {
    Object o = sampleTable[row][14];
    String fileLink = o == null ? "0" : o.toString().trim();
    RandomAccessInputStream data = null;
    if (fileLink.equals("0")) {
      data = new RandomAccessInputStream((byte[]) sampleTable[row][13]);
    }
    else {
      fileLink = new Location(dir, fileLink + ".dat").getAbsolutePath();
      data = new RandomAccessInputStream(fileLink);
    }

    data.order(true);
    return data;
  }

}
