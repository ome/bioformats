/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;

import loci.common.ByteArrayHandle;
import loci.common.DataTools;
import loci.common.IRandomAccess;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.MissingLibraryException;
import loci.formats.codec.LZOCodec;
import loci.formats.meta.MetadataStore;
import loci.formats.services.MetakitService;

import ome.units.quantity.Time;
import ome.units.quantity.Length;
import ome.units.UNITS;

/**
 * VolocityReader is the file format reader for Volocity library files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class VolocityReader extends FormatReader {

  // -- Constants --

  private static final String DATA_DIR = "Data";
  private static final String EMBEDDED_STREAM = "embedded-stream.raw";

  private static final int SIGNATURE_SIZE = 13;

  // -- Fields --

  private ArrayList<Stack> stacks;
  private ArrayList<String> extraFiles;
  private Object[][] sampleTable, stringTable;
  private Location dir = null;

  private ArrayList<Double[]> timestamps = new ArrayList<Double[]>();

  // -- Constructor --

  /** Constructs a new Volocity reader. */
  public VolocityReader() {
    super("Volocity Library",
      new String[] {"mvd2", "aisf", "aiix", "dat", "atsf"});
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
    hasCompanionFiles = true;
    datasetDescription = "One .mvd2 file plus a 'Data' directory";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    ArrayList<String> files = new ArrayList<String>();
    files.addAll(extraFiles);
    if (!noPixels) {
      Stack stack = stacks.get(getSeries());
      for (int c=0; c<getEffectiveSizeC(); c++) {
        files.add(stack.pixelsFiles[c]);
      }
      if (stack.timestampFile != null) {
        files.add(stack.timestampFile);
      }
    }
    return files.toArray(new String[files.size()]);
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, "mvd2")) {
      return super.isThisType(name, open);
    }

    if (open && checkSuffix(name, suffixes)) {
      Location file = new Location(name).getAbsoluteFile();
      Location parent = file.getParentFile();
      if (parent != null) {
        parent = parent.getParentFile();
        if (parent != null) {
          parent = parent.getParentFile();
          if (parent != null) {
            Location mvd2 = new Location(parent, parent.getName() + ".mvd2");
            return mvd2.exists() && super.isThisType(mvd2.getAbsolutePath());
          }
        }
      }
    }
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 2;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    String check = stream.readString(blockLen);
    return check.equals("JL") || check.equals("LJ");
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int[] zct = getZCTCoords(no);

    Stack stack = stacks.get(getSeries());

    if (!new Location(stack.pixelsFiles[zct[1]]).exists()) {
      Arrays.fill(buf, (byte) 0);
      return buf;
    }

    RandomAccessInputStream pix =
      new RandomAccessInputStream(stack.pixelsFiles[zct[1]]);

    int padding = zct[2] * stack.planePadding;

    long planeSize = FormatTools.getPlaneSize(this);
    int planesInFile = (int) (pix.length() / planeSize);
    int planeIndex = no / getEffectiveSizeC();
    if (planesInFile == getSizeT()) {
      planeIndex = zct[2];

      int block = stack.blockSize;
      padding = block - (int) (planeSize % block);
      if (padding == block) {
        padding = 0;
      }
      padding *= zct[2];
    }

    long offset = (long) stack.blockSize + planeIndex * planeSize + padding;
    if (offset >= pix.length()) {
      pix.close();
      return buf;
    }
    pix.seek(offset);

    if (stack.clippingData) {
      pix.seek(offset - 3);
      ByteArrayHandle v = new ByteArrayHandle();
      while (v.length() < FormatTools.getPlaneSize(this) &&
        pix.getFilePointer() < pix.length())
      {
        try {
          byte[] b = new LZOCodec().decompress(pix, null);
          pix.skipBytes(4);
          v.write(b);
        }
        catch (IOException e) { }
      }
      RandomAccessInputStream s = new RandomAccessInputStream(v);
      s.seek(0);
      readPlane(s, x, y, w, h, buf);
      s.close();
    }
    else {
      if (pix.getFilePointer() + planeSize > pix.length()) {
        pix.close();
        return buf;
      }
      readPlane(pix, x, y, w, h, buf);
    }
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
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      stacks = null;
      sampleTable = null;
      stringTable = null;
      dir = null;
      timestamps.clear();
      Location.mapFile(EMBEDDED_STREAM, null);
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    if (!checkSuffix(id, "mvd2")) {
      Location file = new Location(id).getAbsoluteFile();
      Location parent = file.getParentFile().getParentFile();
      if (parent == null) {
        throw new FormatException("Unable to locate parent file to " + id);
      }
      String[] files = parent.list(true);
      for (String f : files) {
        if (checkSuffix(f, "mvd2")) {
          id = new Location(parent, f).getAbsolutePath();
          break;
        }
      }
    }

    super.initFile(id);

    stacks = new ArrayList<Stack>();
    extraFiles = new ArrayList<String>();

    Location file = new Location(id).getAbsoluteFile();
    extraFiles.add(file.getAbsolutePath());

    Location parentDir = file.getParentFile();
    dir = new Location(parentDir, DATA_DIR);

    if (dir.exists()) {
      String[] files = dir.list(true);
      for (String f : files) {
        if (!checkSuffix(f, "aisf") && !checkSuffix(f, "atsf")) {
          extraFiles.add(new Location(dir, f).getAbsolutePath());
        }
      }
    }

    try {
      ServiceFactory factory = new ServiceFactory();
      MetakitService reader = factory.getInstance(MetakitService.class);
      reader.initialize(id);
      sampleTable = reader.getTableData(1);
      stringTable = reader.getTableData(2);

      reader.close();
    }
    catch (DependencyException e) {
      throw new MissingLibraryException("Could not find Metakit library", e);
    }

    ArrayList<String> stackNames = new ArrayList<String>();
    ArrayList<Integer> parentIDs = new ArrayList<Integer>();

    for (int i=0; i<sampleTable.length; i++) {
      Integer stringID = (Integer) sampleTable[i][11];
      String name = getString(stringID);

      int channelIndex = getChildIndex((Integer) sampleTable[i][0], "Channels");

      if (i > 0 && (Integer) sampleTable[i][2] == 1 && (channelIndex >= 0 ||
        (sampleTable[i][14] != null && !sampleTable[i][14].equals(0)) ||
        ((byte[]) sampleTable[i][13]).length > 21))
      {
        if (channelIndex < 0) {
          RandomAccessInputStream s = getStream(i);
          s.seek(0);
          if (s.read() != 'I') {
            s.order(false);
          }
          s.seek(22);
          int x = s.readInt();
          int y = s.readInt();
          int z = s.readInt();
          if (x * y * z > 0 && x * y * z < (s.length() * 3)) {
            stackNames.add(name);
            parentIDs.add((Integer) sampleTable[i][0]);
          }
          s.close();
        }
        else {
          stackNames.add(name);
          parentIDs.add((Integer) sampleTable[i][0]);
        }
      }
    }

    for (int i=0; i<parentIDs.size(); i++) {
      Stack stack = new Stack();
      stack.core = new CoreMetadata();
      Integer parent = parentIDs.get(i);

      int channelIndex = getChildIndex(parent, "Channels");
      if (channelIndex >= 0) {
        Integer[] channels =
          getAllChildren((Integer) sampleTable[channelIndex][0]);
        stack.core.sizeC = channels.length;
        stack.pixelsFiles = new String[stack.core.sizeC];

        stack.channelNames = new String[channels.length];
        for (int c=0; c<channels.length; c++) {
          stack.channelNames[c] =
            getString((Integer) sampleTable[channels[c]][11]);

          RandomAccessInputStream data = getStream(channels[c]);
          if (data.length() > 22) {
            data.seek(22);
            int stackID = data.readInt();
            Location f = new Location(dir, stackID + ".aisf");
            if (!f.exists()) {
              f = new Location(dir, DataTools.swap(stackID) + ".aisf");
            }
            stack.pixelsFiles[c] = f.getAbsolutePath();
          }
          else {
            Integer child =
              getAllChildren((Integer) sampleTable[channels[c]][0])[0];
            stack.pixelsFiles[c] =
              getFile((Integer) sampleTable[child][0], dir);
          }
          data.close();
        }
      }
      else {
        stack.pixelsFiles = new String[1];
        stack.pixelsFiles[0] = getFile(parent, dir);

        if (stack.pixelsFiles[0] == null ||
          !new Location(stack.pixelsFiles[0]).exists())
        {
          int row = -1;
          for (int r=0; r<sampleTable.length; r++) {
            if (sampleTable[r][0].equals(parent)) {
              row = r;
              break;
            }
          }

          stack.pixelsFiles[0] = EMBEDDED_STREAM;
          IRandomAccess data =
            new ByteArrayHandle((byte[]) sampleTable[row][13]);
          Location.mapFile(stack.pixelsFiles[0], data);
        }
      }

      RandomAccessInputStream data = null;

      int timestampIndex = getChildIndex(parent, "Timepoint times stream");
      if (timestampIndex >= 0) {
        data = getStream(timestampIndex);
        data.seek(22);
        int timestampID = data.readInt();
        Location f = new Location(dir, timestampID + ".atsf");
        if (!f.exists()) {
          f = new Location(dir, DataTools.swap(timestampID) + ".atsf");
        }
        stack.timestampFile = f.getAbsolutePath();
        data.close();
      }

      int xIndex = getChildIndex(parent, "um/pixel (X)");
      if (xIndex >= 0) {
        data = getStream(xIndex);
        data.seek(SIGNATURE_SIZE);
        stack.physicalX = data.readDouble();
        data.close();
      }

      int yIndex = getChildIndex(parent, "um/pixel (Y)");
      if (yIndex >= 0) {
        data = getStream(yIndex);
        data.seek(SIGNATURE_SIZE);
        stack.physicalY = data.readDouble();
        data.close();
      }

      int zIndex = getChildIndex(parent, "um/pixel (Z)");
      if (zIndex >= 0) {
        data = getStream(zIndex);
        data.seek(SIGNATURE_SIZE);
        stack.physicalZ = data.readDouble();
        data.close();
      }

      timestampIndex = getChildIndex(parent, "TimepointTimes");
      if (timestampIndex >= 0) {
        data = getStream(timestampIndex);
        data.seek(SIGNATURE_SIZE);
        data.close();
      }

      int objectiveIndex = getChildIndex(parent, "Microscope Objective");
      if (objectiveIndex >= 0) {
        data = getStream(objectiveIndex);
        data.seek(SIGNATURE_SIZE);
        stack.magnification = data.readDouble();
        data.close();
      }

      int detectorIndex = getChildIndex(parent, "Camera/Detector");
      if (detectorIndex >= 0) {
        data = getStream(detectorIndex);
        data.seek(SIGNATURE_SIZE);
        int len = data.readInt();
        stack.detector = data.readString(len);
        data.close();
      }

      int descriptionIndex = getChildIndex(parent, "Experiment Description");
      if (descriptionIndex >= 0) {
        data = getStream(descriptionIndex);
        data.seek(SIGNATURE_SIZE);
        int len = data.readInt();
        stack.description = data.readString(len);
        data.close();
      }

      int xLocationIndex = getChildIndex(parent, "X Location");
      if (xLocationIndex >= 0) {
        data = getStream(xLocationIndex);
        data.seek(SIGNATURE_SIZE);
        stack.xLocation = data.readDouble();
        data.close();
      }

      int yLocationIndex = getChildIndex(parent, "Y Location");
      if (yLocationIndex >= 0) {
        data = getStream(yLocationIndex);
        data.seek(SIGNATURE_SIZE);
        stack.yLocation = data.readDouble();
        data.close();
      }

      int zLocationIndex = getChildIndex(parent, "Z Location");
      if (zLocationIndex >= 0) {
        data = getStream(zLocationIndex);
        data.seek(SIGNATURE_SIZE);
        stack.zLocation = data.readDouble();
        data.close();
      }

      stacks.add(stack);
    }

    // split up channels as necessary

    for (int i=0; i<stacks.size(); i++) {
      Stack stack = stacks.get(i);

      if (!new Location(stack.pixelsFiles[0]).exists()) {
        stacks.remove(i);
        i--;
        continue;
      }

      RandomAccessInputStream base =
        new RandomAccessInputStream(stack.pixelsFiles[0]);
      long baseLength = base.length();
      base.close();

      for (int q=1; q<stack.pixelsFiles.length; q++) {
        if (!new Location(stack.pixelsFiles[q]).exists()) {
          continue;
        }
        base = new RandomAccessInputStream(stack.pixelsFiles[q]);
        long length = base.length();
        base.close();

        if (length > baseLength) {
          // split the stack
          Stack newStack = new Stack();

          newStack.timestampFile = stack.timestampFile;
          newStack.core = new CoreMetadata();
          newStack.physicalX = stack.physicalX;
          newStack.physicalY = stack.physicalY;
          newStack.physicalZ = stack.physicalZ;
          newStack.magnification = stack.magnification;
          newStack.detector = stack.detector;
          newStack.description = stack.description;
          newStack.xLocation = stack.xLocation;
          newStack.yLocation = stack.yLocation;
          newStack.zLocation = stack.zLocation;

          String[] pixels = stack.pixelsFiles;
          newStack.pixelsFiles = new String[pixels.length - q];
          System.arraycopy(pixels, q, newStack.pixelsFiles, 0,
            newStack.pixelsFiles.length);
          stack.pixelsFiles = new String[q];
          System.arraycopy(pixels, 0, stack.pixelsFiles, 0, q);

          String[] channels = stack.channelNames;
          newStack.channelNames = new String[channels.length - q];
          System.arraycopy(channels, q, newStack.channelNames, 0,
            newStack.channelNames.length);
          stack.channelNames = new String[q];
          System.arraycopy(channels, 0, stack.channelNames, 0, q);

          newStack.core.sizeC = newStack.channelNames.length;
          stack.core.sizeC = stack.channelNames.length;

          stacks.add(i + 1, newStack);
          stackNames.add(i + 1, stackNames.get(i));
        }
      }
    }

    int seriesCount = stacks.size();
    core.clear();
    for (int i=0; i<seriesCount; i++) {
      Stack stack = stacks.get(i);
      CoreMetadata ms = stack.core;
      core.add(ms);

      setSeries(i);

      ms.littleEndian = true;

      if (stack.timestampFile != null) {
        RandomAccessInputStream s =
          new RandomAccessInputStream(stack.timestampFile);
        s.seek(0);
        if (s.read() != 'I') {
          ms.littleEndian = false;
        }
        s.seek(17);
        s.order(isLittleEndian());
        ms.sizeT = s.readInt();

        Double firstStamp = null;
        Double[] stamps = new Double[ms.sizeT];
        for (int t=0; t<ms.sizeT; t++) {
          // timestamps are stored in microseconds
          double timestamp = s.readLong() / 1000000.0;
          if (firstStamp == null) {
            firstStamp = timestamp;
          }
          stamps[t] = timestamp - firstStamp;
        }
        timestamps.add(stamps);

        s.close();
      }
      else {
        ms.sizeT = 1;
      }

      ms.rgb = false;
      ms.interleaved = true;
      ms.dimensionOrder = "XYCZT";

      RandomAccessInputStream s =
        new RandomAccessInputStream(stack.pixelsFiles[0]);
      s.order(isLittleEndian());

      if (checkSuffix(stack.pixelsFiles[0], "aisf")) {
        s.seek(18);
        stack.blockSize = s.readShort() * 256;
        s.skipBytes(5);
        int x = s.readInt();
        int y = s.readInt();
        int zStart = s.readInt();
        int w = s.readInt();
        int h = s.readInt();

        if (w - x < 0 || h - y < 0 || (w - x) * (h - y) < 0) {
          ms.littleEndian = !isLittleEndian();
          s.order(isLittleEndian());
          s.seek(s.getFilePointer() - 20);
          x = s.readInt();
          y = s.readInt();
          zStart = s.readInt();
          w = s.readInt();
          h = s.readInt();
        }

        ms.sizeX = w - x;
        ms.sizeY = h - y;
        ms.sizeZ = s.readInt() - zStart;
        ms.imageCount = getSizeZ() * getSizeC() * getSizeT();
        ms.pixelType = FormatTools.INT8;

        int planesPerFile = getSizeZ() * getSizeT();
        int planeSize = FormatTools.getPlaneSize(this);
        int bytesPerPlane =
          (int) ((s.length() - stack.blockSize) / planesPerFile);

        int bytesPerPixel = 0;
        while (bytesPerPlane >= planeSize) {
          bytesPerPixel++;
          bytesPerPlane -= planeSize;
        }

        if ((bytesPerPixel % 3) == 0) {
          ms.sizeC *= 3;
          ms.rgb = true;
          bytesPerPixel /= 3;
        }

        ms.pixelType = FormatTools.pixelTypeFromBytes(
          bytesPerPixel, false, bytesPerPixel > 2);

        // full timepoints are padded to have a multiple of 256 bytes
        int timepoint = FormatTools.getPlaneSize(this) * getSizeZ();
        stack.planePadding = stack.blockSize - (timepoint % stack.blockSize);
        if (stack.planePadding == stack.blockSize) {
          stack.planePadding = 0;
        }
      }
      else {
        boolean embedded = Location.getMappedFile(EMBEDDED_STREAM) != null;

        s.seek(0);
        if (s.read() != 'I') {
          ms.littleEndian = false;
          s.order(false);
        }

        s.seek(22);
        ms.sizeX = s.readInt();
        ms.sizeY = s.readInt();
        ms.sizeZ = s.readInt();
        ms.sizeC = embedded ? 1 : 4;
        ms.imageCount = getSizeZ() * getSizeT();
        ms.rgb = ms.sizeC > 1;
        ms.pixelType = FormatTools.UINT8;
        stack.blockSize = embedded ? (int) s.getFilePointer() : 99;
        stack.planePadding = 0;

        if (s.length() > ms.sizeX * ms.sizeY * ms.sizeZ * 6) {
          ms.pixelType = FormatTools.UINT16;
          ms.sizeC = 3;
          ms.rgb = true;
        }

        if (s.length() <
          (ms.sizeX * ms.sizeY * ms.sizeZ * ms.sizeC))
        {
          ms.rgb = false;
          ms.sizeC = 1;
          long pixels = ms.sizeX * ms.sizeY * ms.sizeZ;
          double approximateBytes = (double) s.length() / pixels;
          int bytes = (int) Math.ceil(approximateBytes);
          if (bytes == 0) {
            bytes = 1;
          }
          else if (bytes == 3) {
            bytes = 2;
          }
          ms.pixelType =
            FormatTools.pixelTypeFromBytes(bytes, false, false);
          s.seek(70);
          stack.blockSize = s.readInt();
          stack.clippingData = true;
        }
      }
      s.close();
    }
    setSeries(0);

    for (int i=0; i<getSeriesCount(); i++) {
      setSeries(i);

      Stack stack = stacks.get(i);

      addSeriesMeta("Name", stackNames.get(i));
      addSeriesMeta("Pixel width (in microns)", stack.physicalX);
      addSeriesMeta("Pixel height (in microns)", stack.physicalY);
      addSeriesMeta("Z step (in microns)", stack.physicalZ);
      addSeriesMeta("Objective magnification", stack.magnification);
      addSeriesMeta("Camera/Detector", stack.detector);
      addSeriesMeta("Description", stack.description);
      addSeriesMeta("X Location", stack.xLocation);
      addSeriesMeta("Y Location", stack.yLocation);
      addSeriesMeta("Z Location", stack.zLocation);

      if (stack.channelNames != null) {
        for (int c=0; c<stack.channelNames.length; c++) {
          addSeriesMetaList("Channel", stack.channelNames[c]);
        }
      }
    }
    setSeries(0);

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    String instrument = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrument, 0);

    for (int i=0; i<getSeriesCount(); i++) {
      store.setImageInstrumentRef(instrument, i);

      setSeries(i);
      Stack stack = stacks.get(i);
      store.setImageName(stackNames.get(i), i);
      store.setImageDescription(stack.description, i);
      if (stack.channelNames != null) {
        for (int c=0; c<getEffectiveSizeC(); c++) {
          store.setChannelName(stack.channelNames[c], i, c);
        }
      }

      Length sizeX = FormatTools.getPhysicalSizeX(stack.physicalX);
      Length sizeY = FormatTools.getPhysicalSizeY(stack.physicalY);
      Length sizeZ = FormatTools.getPhysicalSizeZ(stack.physicalZ);
      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, i);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, i);
      }
      if (sizeZ != null) {
        store.setPixelsPhysicalSizeZ(sizeZ, i);
      }

      String objective = MetadataTools.createLSID("Objective", 0, i);
      store.setObjectiveID(objective, 0, i);
      store.setObjectiveNominalMagnification(stack.magnification, 0, i);
      store.setObjectiveCorrection(getCorrection("Other"), 0, i);
      store.setObjectiveImmersion(getImmersion("Other"), 0, i);
      store.setObjectiveSettingsID(objective, i);

      String detectorID = MetadataTools.createLSID("Detector", 0, i);
      store.setDetectorID(detectorID, 0, i);
      store.setDetectorModel(stack.detector, 0, i);

      for (int c=0; c<getEffectiveSizeC(); c++) {
        store.setDetectorSettingsID(detectorID, i, c);
      }

      for (int img=0; img<getImageCount(); img++) {
        int[] coords = getZCTCoords(img);
        int z = coords[0];
        final Length xLoc = new Length(stack.xLocation, UNITS.REFERENCEFRAME);
        final Length yLoc = new Length(stack.yLocation, UNITS.REFERENCEFRAME);
        store.setPlanePositionX(xLoc, i, img);
        store.setPlanePositionY(yLoc, i, img);
        if (stack.physicalZ != null) {
          final double zLocNumber = stack.zLocation + z * stack.physicalZ;
          final Length zLoc = new Length(zLocNumber, UNITS.REFERENCEFRAME);
          store.setPlanePositionZ(zLoc, i, img);
        }

        if (i < timestamps.size() && coords[2] < timestamps.get(i).length && timestamps.get(i)[coords[2]] != null) {
          store.setPlaneDeltaT(new Time(timestamps.get(i)[coords[2]], UNITS.SECOND), i, img);
        }
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

  private String getFile(Integer parent, Location dir) {
    for (int row=0; row<sampleTable.length; row++) {
      if (parent.equals(sampleTable[row][0])) {
        Object o = sampleTable[row][14];
        if (o != null) {
          String fileLink = o.toString().trim() + ".dat";
          return new Location(dir, fileLink).getAbsolutePath();
        }
      }
    }
    return null;
  }

  // -- Helper class --

  class Stack {
    public String[] pixelsFiles;
    public String timestampFile;
    public int planePadding;
    public int blockSize;
    public boolean clippingData;

    public CoreMetadata core;
    public String[] channelNames;
    public Double physicalX;
    public Double physicalY;
    public Double physicalZ;
    public Double magnification;
    public String detector;
    public String description;
    public double xLocation;
    public double yLocation;
    public double zLocation;
  }

}
