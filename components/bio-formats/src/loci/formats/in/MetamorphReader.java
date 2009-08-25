//
// MetamorphReader.java
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

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Vector;

import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffIFDEntry;
import loci.formats.tiff.TiffParser;
import loci.formats.tiff.TiffRational;

/**
 * Reader is the file format reader for Metamorph STK files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/MetamorphReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/MetamorphReader.java">SVN</a></dd></dl>
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Sebastien Huart Sebastien dot Huart at curie.fr
 */
public class MetamorphReader extends BaseTiffReader {

  // -- Constants --

  public static final String SHORT_DATE_FORMAT = "yyyyMMdd HH:mm:ss";
  public static final String MEDIUM_DATE_FORMAT = "yyyyMMdd HH:mm:ss.SSS";
  public static final String LONG_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss:SSS";

  public static final String[] ND_SUFFIX = {"nd"};
  public static final String[] STK_SUFFIX = {"stk", "tif", "tiff"};

  // IFD tag numbers of important fields
  private static final int METAMORPH_ID = 33628;
  private static final int UIC1TAG = METAMORPH_ID;
  private static final int UIC2TAG = 33629;
  private static final int UIC3TAG = 33630;
  private static final int UIC4TAG = 33631;

  // -- Fields --

  /** The TIFF's name */
  private String imageName;

  /** The TIFF's creation date */
  private String imageCreationDate;

  /** The TIFF's emWavelength */
  private long[] emWavelength;

  private double[] wave;

  private String binning;
  private float zoom, stepSize;
  private Float exposureTime;
  private Vector<String> waveNames;
  private Vector<String> stageNames;
  private long[] internalStamps;
  private double[] zDistances, stageX, stageY;

  private int mmPlanes; //number of metamorph planes

  private MetamorphReader stkReader;

  /** List of STK files in the dataset. */
  private String[][] stks;

  private String ndFilename;
  private boolean canLookForND = true;

  private boolean[] firstSeriesChannels;

  // -- Constructor --

  /** Constructs a new Metamorph reader. */
  public MetamorphReader() {
    super("Metamorph STK", new String[] {"stk", "nd"});
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    if (checkSuffix(id, ND_SUFFIX)) return FormatTools.MUST_GROUP;

    Location l = new Location(id).getAbsoluteFile();
    String[] files = l.getParentFile().list();

    for (int i=0; i<files.length; i++) {
      if (checkSuffix(files[i], ND_SUFFIX) &&
       id.startsWith(files[i].substring(0, files[i].lastIndexOf("."))))
      {
        return FormatTools.MUST_GROUP;
      }
    }

    return FormatTools.CANNOT_GROUP;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (stks == null) return new String[] {currentId};

    Vector<String> v = new Vector<String>();
    if (ndFilename != null) v.add(ndFilename);
    if (!noPixels) v.addAll(Arrays.asList(stks[getSeries()]));
    return v.toArray(new String[v.size()]);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    if (stks == null) {
      return super.openBytes(no, buf, x, y, w, h);
    }

    int[] coords = FormatTools.getZCTCoords(this, no % getSizeZ());
    int ndx = no / getSizeZ();
    if (stks[series].length == 1) ndx = 0;
    String file = stks[series][ndx];

    // the original file is a .nd file, so we need to construct a new reader
    // for the constituent STK files
    if (stkReader == null) {
      stkReader = new MetamorphReader();
      stkReader.setCanLookForND(false);
    }
    stkReader.setId(file);
    int plane = stks[series].length == 1 ? no : coords[0];
    stkReader.openBytes(plane, buf, x, y, w, h);
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (stkReader != null) stkReader.close(fileOnly);
    if (!fileOnly) {
      stkReader = null;
      imageName = imageCreationDate = null;
      emWavelength = null;
      stks = null;
      mmPlanes = 0;
      ndFilename = null;
      wave = null;
      binning = null;
      zoom = stepSize = 0;
      exposureTime = null;
      waveNames = stageNames = null;
      internalStamps = null;
      zDistances = stageX = stageY = null;
      canLookForND = true;
      firstSeriesChannels = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (checkSuffix(id, ND_SUFFIX)) {
      status("Initializing " + id);
      // find an associated STK file
      String stkFile = id.substring(0, id.lastIndexOf("."));
      if (stkFile.indexOf(File.separator) != -1) {
        stkFile = stkFile.substring(stkFile.lastIndexOf(File.separator) + 1);
      }
      String parentPath = id.substring(0, id.lastIndexOf(File.separator) + 1);
      Location parent = new Location(parentPath).getAbsoluteFile();
      status("Looking for STK file in " + parent.getAbsolutePath());
      String[] dirList = parent.list();
      for (int i=0; i<dirList.length; i++) {
        if (dirList[i].indexOf(stkFile) != -1 &&
          checkSuffix(dirList[i], STK_SUFFIX))
        {
          stkFile = new Location(
            parent.getAbsolutePath(), dirList[i]).getAbsolutePath();
          break;
        }
      }

      if (!checkSuffix(stkFile, STK_SUFFIX)) {
        throw new FormatException("STK file not found in " +
          parent.getAbsolutePath() + ".");
      }

      super.initFile(stkFile);
    }
    else super.initFile(id);

    Location ndfile = null;

    if (checkSuffix(id, ND_SUFFIX)) ndfile = new Location(id);
    else if (canLookForND) {
      // an STK file was passed to initFile
      // let's check the parent directory for an .nd file
      Location parent = new Location(id).getAbsoluteFile().getParentFile();
      String[] list = parent.list();
      for (int i=0; i<list.length; i++) {
        if (checkSuffix(list[i], ND_SUFFIX)) {
          String prefix = list[i].substring(0, list[i].lastIndexOf("."));
          if (currentId.startsWith(prefix)) {
            ndfile = new Location(parent, list[i]).getAbsoluteFile();
            break;
          }
        }
      }
    }

    String creationTime = null;

    if (ndfile != null && ndfile.exists() &&
      (fileGroupOption(id) == FormatTools.MUST_GROUP || isGroupFiles()))
    {
      // parse key/value pairs from .nd file

      ndFilename = ndfile.getAbsolutePath();

      RandomAccessInputStream ndStream =
        new RandomAccessInputStream(ndFilename);
      String line = ndStream.readLine().trim();

      int zc = getSizeZ(), cc = getSizeC(), tc = getSizeT();
      int nstages = 0;
      String z = null, c = null, t = null;
      Vector hasZ = new Vector();
      waveNames = new Vector<String>();
      stageNames = new Vector<String>();
      boolean useWaveNames = true;

      while (!line.equals("\"EndFile\"")) {
        String key = line.substring(1, line.indexOf(",") - 1).trim();
        String value = line.substring(line.indexOf(",") + 1).trim();

        addGlobalMeta(key, value);
        if (key.equals("NZSteps")) z = value;
        else if (key.equals("NWavelengths")) c = value;
        else if (key.equals("NTimePoints")) t = value;
        else if (key.startsWith("WaveDoZ")) {
          hasZ.add(new Boolean(value.toLowerCase()));
        }
        else if (key.startsWith("WaveName")) {
          waveNames.add(value);
        }
        else if (key.startsWith("Stage")) {
          stageNames.add(value);
        }
        else if (key.startsWith("StartTime")) {
          creationTime = value;
        }
        else if (key.equals("ZStepSize")) {
          stepSize = Float.parseFloat(value);
        }
        else if (key.equals("NStagePositions")) {
          nstages = Integer.parseInt(value);
        }
        else if (key.equals("WaveInFileName")) {
          useWaveNames = Boolean.parseBoolean(value);
        }

        line = ndStream.readLine().trim();
      }

      // figure out how many files we need

      if (z != null) zc = Integer.parseInt(z);
      if (c != null) cc = Integer.parseInt(c);
      if (t != null) tc = Integer.parseInt(t);

      int numFiles = cc * tc;
      if (nstages > 0) numFiles *= nstages;

      // determine series count

      int seriesCount = nstages == 0 ? 1 : nstages;
      firstSeriesChannels = new boolean[cc];
      Arrays.fill(firstSeriesChannels, true);
      boolean differentZs = false;
      for (int i=0; i<cc; i++) {
        boolean hasZ1 = ((Boolean) hasZ.get(i)).booleanValue();
        boolean hasZ2 = i != 0 && ((Boolean) hasZ.get(i - 1)).booleanValue();
        if (i > 0 && hasZ1 != hasZ2) {
          if (!differentZs) seriesCount *= 2;
          differentZs = true;
        }
      }

      int channelsInFirstSeries = cc;
      if (differentZs) {
        channelsInFirstSeries = 0;
        for (int i=0; i<cc; i++) {
          if (((Boolean) hasZ.get(i)).booleanValue()) channelsInFirstSeries++;
          else firstSeriesChannels[i] = false;
        }
      }

      stks = new String[seriesCount][];
      if (seriesCount == 1) stks[0] = new String[numFiles];
      else if (differentZs) {
        int stages = nstages == 0 ? 1 : nstages;
        for (int i=0; i<stages; i++) {
          stks[i * 2] = new String[channelsInFirstSeries * tc];
          stks[i * 2 + 1] = new String[(cc - channelsInFirstSeries) * tc];
        }
      }
      else {
        for (int i=0; i<stks.length; i++) {
          stks[i] = new String[numFiles / stks.length];
        }
      }

      String prefix = ndfile.getPath();
      prefix = prefix.substring(prefix.lastIndexOf(File.separator) + 1,
        prefix.lastIndexOf("."));

      for (int i=0; i<cc; i++) {
        if (waveNames.get(i) != null) {
          String name = waveNames.get(i);
          waveNames.setElementAt(name.substring(1, name.length() - 1), i);
        }
      }

      // build list of STK files

      int[] pt = new int[seriesCount];
      for (int i=0; i<tc; i++) {
        int ns = nstages == 0 ? 1 : nstages;
        for (int s=0; s<ns; s++) {
          for (int j=0; j<cc; j++) {
            boolean validZ = ((Boolean) hasZ.get(j)).booleanValue();
            int seriesNdx = s * (seriesCount / ns);
            seriesNdx += (seriesCount == 1 || validZ) ? 0 : 1;
            stks[seriesNdx][pt[seriesNdx]] = prefix;
            if (waveNames.get(j) != null) {
              stks[seriesNdx][pt[seriesNdx]] += "_w" + (j + 1);
              if (useWaveNames) {
                stks[seriesNdx][pt[seriesNdx]] += waveNames.get(j);
              }
            }
            if (nstages > 0) {
              stks[seriesNdx][pt[seriesNdx]] += "_s" + (s + 1);
            }
            stks[seriesNdx][pt[seriesNdx]] += "_t" + (i + 1) + ".STK";
            pt[seriesNdx]++;
          }
        }
      }

      ndfile = ndfile.getAbsoluteFile();

      // check that each STK file exists

      for (int s=0; s<stks.length; s++) {
        for (int f=0; f<stks[s].length; f++) {
          Location l = new Location(ndfile.getParent(), stks[s][f]);
          if (!l.exists()) {
            // '%' can be converted to '-'
            if (stks[s][f].indexOf("%") != -1) {
              stks[s][f] = stks[s][f].replaceAll("%", "-");
              l = new Location(ndfile.getParent(), stks[s][f]);
              if (!l.exists()) {
                // try replacing extension
                stks[s][f] = stks[s][f].substring(0,
                  stks[s][f].lastIndexOf(".")) + ".TIF";
                l = new Location(ndfile.getParent(), stks[s][f]);
                if (!l.exists()) {
                  stks[s][f] = stks[s][f].substring(0,
                    stks[s][f].lastIndexOf(".")) + ".tif";
                  l = new Location(ndfile.getParent(), stks[s][f]);
                  if (!l.exists()) {
                    String filename = stks[s][f];
                    stks = null;
                    throw new FormatException("Missing STK file: " + filename);
                  }
                }
              }
            }

            if (!l.exists()) {
              // try replacing extension
              stks[s][f] = stks[s][f].substring(0,
                stks[s][f].lastIndexOf(".")) + ".TIF";
              l = new Location(ndfile.getParent(), stks[s][f]);
              if (!l.exists()) {
                stks[s][f] = stks[s][f].substring(0,
                  stks[s][f].lastIndexOf(".")) + ".tif";
                l = new Location(ndfile.getParent(), stks[s][f]);
                if (!l.exists()) {
                  String filename = stks[s][f];
                  stks = null;
                  throw new FormatException("Missing STK file: " + filename);
                }
              }
            }
          }
          if (stks != null) stks[s][f] = l.getAbsolutePath();
          else break;
        }
        if (stks == null) break;
      }

      RandomAccessInputStream s = new RandomAccessInputStream(stks[0][0]);
      TiffParser tp = new TiffParser(s);
      IFD ifd = tp.getFirstIFD();
      s.close();
      core[0].sizeX = (int) ifd.getImageWidth();
      core[0].sizeY = (int) ifd.getImageLength();

      core[0].sizeZ = zc;
      core[0].sizeC = cc;
      core[0].sizeT = tc;
      core[0].imageCount = zc * tc * cc;
      core[0].dimensionOrder = "XYZCT";

      if (stks != null && stks.length > 1) {
        CoreMetadata[] newCore = new CoreMetadata[stks.length];
        for (int i=0; i<stks.length; i++) {
          newCore[i] = new CoreMetadata();
          newCore[i].sizeX = getSizeX();
          newCore[i].sizeY = getSizeY();
          newCore[i].sizeZ = getSizeZ();
          newCore[i].sizeC = getSizeC();
          newCore[i].sizeT = getSizeT();
          newCore[i].pixelType = getPixelType();
          newCore[i].imageCount = getImageCount();
          newCore[i].dimensionOrder = getDimensionOrder();
          newCore[i].rgb = isRGB();
          newCore[i].littleEndian = isLittleEndian();
          newCore[i].interleaved = isInterleaved();
          newCore[i].orderCertain = true;
        }
        if (stks.length > nstages) {
          int ns = nstages == 0 ? 1 : nstages;
          for (int j=0; j<ns; j++) {
            newCore[j * 2].sizeC = stks[j * 2].length / getSizeT();
            newCore[j * 2 + 1].sizeC =
              stks[j * 2 + 1].length / newCore[j * 2 + 1].sizeT;
            newCore[j * 2 + 1].sizeZ = 1;
            newCore[j * 2].imageCount = newCore[j * 2].sizeC *
              newCore[j * 2].sizeT * newCore[j * 2].sizeZ;
            newCore[j * 2 + 1].imageCount =
              newCore[j * 2 + 1].sizeC * newCore[j * 2 + 1].sizeT;
          }
        }
        core = newCore;
      }
    }

    Vector timestamps = null;
    MetamorphHandler handler = null;

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this, true);
    String detectorID = MetadataTools.createLSID("Detector", 0, 0);

    for (int i=0; i<getSeriesCount(); i++) {
      setSeries(i);
      handler = new MetamorphHandler(getSeriesMetadata());

      String instrumentID = MetadataTools.createLSID("Instrument", i);
      store.setInstrumentID(instrumentID, i);
      store.setImageInstrumentRef(instrumentID, i);

      String comment = null;

      if (stks != null) {
        RandomAccessInputStream stream =
          new RandomAccessInputStream(stks[i][0]);
        TiffParser tp = new TiffParser(stream);
        IFD ifd = tp.getFirstIFD();
        stream.close();
        comment = ifd.getComment();
      }
      else {
        comment = ifds.get(0).getComment();
      }
      if (comment != null && comment.startsWith("<MetaData>")) {
        XMLTools.parseXML(comment, handler);
      }

      if (creationTime != null) {
        String date = DateTools.formatDate(creationTime, SHORT_DATE_FORMAT);
        store.setImageCreationDate(date, 0);
      }
      else if (i > 0) MetadataTools.setDefaultCreationDate(store, id, i);

      String name = "";
      if (stageNames != null && stageNames.size() > 0) {
        int stagePosition = i / (getSeriesCount() / stageNames.size());
        name += "Stage " + stageNames.get(stagePosition) + "; ";
      }

      if (firstSeriesChannels != null) {
        for (int c=0; c<firstSeriesChannels.length; c++) {
          if (firstSeriesChannels[c] == ((i % 2) == 0)) {
            name += waveNames.get(c) + "/";
          }
        }
        name = name.substring(0, name.length() - 1);
      }

      store.setImageName(name, i);
      store.setImageDescription("", i);

      store.setImagingEnvironmentTemperature(
        new Float(handler.getTemperature()), i);
      store.setDimensionsPhysicalSizeX(
        new Float(handler.getPixelSizeX()), i, 0);
      store.setDimensionsPhysicalSizeY(
        new Float(handler.getPixelSizeY()), i, 0);
      if (zDistances != null) {
        stepSize = (float) zDistances[0];
      }
      store.setDimensionsPhysicalSizeZ(new Float(stepSize), i, 0);

      int waveIndex = 0;
      for (int c=0; c<getEffectiveSizeC(); c++) {
        if (firstSeriesChannels == null ||
          (stageNames != null && stageNames.size() == getSeriesCount()))
        {
          waveIndex = c;
        }
        else if (firstSeriesChannels != null) {
          int s = i % 2;
          while (firstSeriesChannels[waveIndex] == (s == 1) &&
            waveIndex < firstSeriesChannels.length)
          {
            waveIndex++;
          }
        }

        if (waveNames != null && waveIndex < waveNames.size()) {
          store.setLogicalChannelName(waveNames.get(waveIndex), i, c);
        }
        store.setDetectorSettingsBinning(binning, i, c);
        if (handler.getBinning() != null) {
          store.setDetectorSettingsBinning(handler.getBinning(), i, c);
        }
        if (handler.getReadOutRate() != 0) {
          store.setDetectorSettingsReadOutRate(
            new Float(handler.getReadOutRate()), i, c);
        }
        store.setDetectorSettingsDetector(detectorID, i, c);

        if (wave != null && waveIndex < wave.length &&
          (int) wave[waveIndex] >= 1)
        {
          store.setLightSourceSettingsWavelength(
            new Integer((int) wave[waveIndex]), i, c);

          // link LightSource to Image
          String lightSourceID = MetadataTools.createLSID("LightSource", i, c);
          store.setLightSourceID(lightSourceID, i, c);
          store.setLightSourceSettingsLightSource(lightSourceID, i, c);
          store.setLaserType("Unknown", i, c);
        }
        waveIndex++;
      }

      timestamps = handler.getTimestamps();

      for (int t=0; t<timestamps.size(); t++) {
        addSeriesMeta("timestamp " + t, DateTools.formatDate(
          (String) timestamps.get(t), MEDIUM_DATE_FORMAT));
      }

      long startDate = 0;
      if (timestamps.size() > 0) {
        startDate = DateTools.getTime((String)
          timestamps.get(0), MEDIUM_DATE_FORMAT);
      }

      Float positionX = new Float(handler.getStagePositionX());
      Float positionY = new Float(handler.getStagePositionY());
      Vector exposureTimes = handler.getExposures();
      if (exposureTimes.size() == 0) {
        for (int p=0; p<getImageCount(); p++) {
          exposureTimes.add(exposureTime);
        }
      }

      int lastFile = -1;
      IFD lastIFD = null;

      for (int p=0; p<getImageCount(); p++) {
        int[] coords = getZCTCoords(p);
        Float deltaT = 0f;
        Float exposureTime = 0f;

        if (coords[2] > 0 && stks != null) {
          int fileIndex = getIndex(0, 0, coords[2]) / getSizeZ();
          if (fileIndex != lastFile) {
            lastFile = fileIndex;
            RandomAccessInputStream stream =
              new RandomAccessInputStream(stks[i][lastFile]);
            TiffParser tp = new TiffParser(stream);
            lastIFD = tp.getFirstIFD();
            stream.close();
          }
          comment = lastIFD.getComment();
          handler = new MetamorphHandler(getSeriesMetadata());
          if (comment != null && comment.startsWith("<MetaData>")) {
            XMLTools.parseXML(comment, handler);
          }
          timestamps = handler.getTimestamps();
          exposureTimes = handler.getExposures();
        }

        int index = 0;

        if (timestamps.size() > 0) {
          if (coords[2] < timestamps.size()) index = coords[2];
          String stamp = (String) timestamps.get(index);
          long ms = DateTools.getTime(stamp, MEDIUM_DATE_FORMAT);
          deltaT = new Float((ms - startDate) / 1000f);
        }
        else if (internalStamps != null && p < internalStamps.length) {
          long delta = internalStamps[p] - internalStamps[0];
          deltaT = new Float(delta / 1000f);
          if (coords[2] < exposureTimes.size()) index = coords[2];
        }

        if (index < exposureTimes.size()) {
          exposureTime = (Float) exposureTimes.get(index);
        }

        store.setPlaneTimingDeltaT(deltaT, i, 0, p);
        store.setPlaneTimingExposureTime(exposureTime, i, 0, p);

        if (stageX != null && p < stageX.length) {
          store.setStagePositionPositionX(
            new Float((float) stageX[p]), i, 0, p);
        }
        if (stageY != null && p < stageY.length) {
          store.setStagePositionPositionY(
            new Float((float) stageY[p]), i, 0, p);
        }
      }
    }
    setSeries(0);

    store.setDetectorID(detectorID, 0, 0);
    store.setDetectorZoom(new Float(zoom), 0, 0);
    if (handler != null && handler.getZoom() != 0f) {
      store.setDetectorZoom(new Float(handler.getZoom()), 0, 0);
    }
    store.setDetectorType("Unknown", 0, 0);
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    core[0].sizeZ = 1;
    core[0].sizeT = 0;
    int rgbChannels = getSizeC();

    try {
      // Now that the base TIFF standard metadata has been parsed, we need to
      // parse out the STK metadata from the UIC4TAG.
      TiffIFDEntry uic1tagEntry = tiffParser.getFirstIFDEntry(UIC1TAG);
      TiffIFDEntry uic2tagEntry = tiffParser.getFirstIFDEntry(UIC2TAG);
      TiffIFDEntry uic4tagEntry = tiffParser.getFirstIFDEntry(UIC4TAG);
      mmPlanes = uic4tagEntry.getValueCount();
      parseUIC2Tags(uic2tagEntry.getValueOffset());
      parseUIC4Tags(uic4tagEntry.getValueOffset());
      parseUIC1Tags(uic1tagEntry.getValueOffset(),
        uic1tagEntry.getValueCount());
      in.seek(uic4tagEntry.getValueOffset());

      // copy ifds into a new array of Hashtables that will accommodate the
      // additional image planes
      IFD firstIFD = ifds.get(0);
      long[] uic2 = firstIFD.getIFDLongArray(UIC2TAG, true);
      core[0].imageCount = uic2.length;

      Object entry = firstIFD.getIFDValue(UIC3TAG);
      TiffRational[] uic3 = entry instanceof TiffRational[] ?
        (TiffRational[]) entry : new TiffRational[] {(TiffRational) entry};
      wave = new double[uic3.length];
      Vector uniqueWavelengths = new Vector();
      for (int i=0; i<uic3.length; i++) {
        wave[i] = uic3[i].doubleValue();
        addSeriesMeta("Wavelength [" + intFormatMax(i, mmPlanes) + "]",
          wave[i]);
        Double v = new Double(wave[i]);
        if (!uniqueWavelengths.contains(v)) uniqueWavelengths.add(v);
      }

      core[0].sizeC *= uniqueWavelengths.size();

      IFDList tempIFDs = new IFDList();

      long[] oldOffsets = firstIFD.getStripOffsets();
      long[] stripByteCounts = firstIFD.getStripByteCounts();
      int rowsPerStrip = (int) firstIFD.getRowsPerStrip()[0];
      int stripsPerImage = getSizeY() / rowsPerStrip;
      if (stripsPerImage * rowsPerStrip != getSizeY()) stripsPerImage++;

      int check = firstIFD.getPhotometricInterpretation();
      if (check == PhotoInterp.RGB_PALETTE) {
        firstIFD.putIFDValue(IFD.PHOTOMETRIC_INTERPRETATION,
          PhotoInterp.BLACK_IS_ZERO);
      }

      emWavelength = firstIFD.getIFDLongArray(UIC3TAG, true);

      // for each image plane, construct an IFD hashtable

      IFD temp;
      for (int i=0; i<getImageCount(); i++) {
        // copy data from the first IFD
        temp = new IFD(firstIFD);

        // now we need a StripOffsets entry - the original IFD doesn't have this

        long[] newOffsets = new long[stripsPerImage];
        if (stripsPerImage * (i + 1) <= oldOffsets.length) {
          System.arraycopy(oldOffsets, stripsPerImage * i, newOffsets, 0,
            stripsPerImage);
        }
        else {
          System.arraycopy(oldOffsets, 0, newOffsets, 0, stripsPerImage);
          long image = (stripByteCounts[0] / rowsPerStrip) * getSizeY();
          for (int q=0; q<stripsPerImage; q++) {
            newOffsets[q] += i * image;
          }
        }

        temp.put(new Integer(IFD.STRIP_OFFSETS), newOffsets);

        long[] newByteCounts = new long[stripsPerImage];
        if (stripsPerImage * i < stripByteCounts.length) {
          System.arraycopy(stripByteCounts, stripsPerImage * i, newByteCounts,
            0, stripsPerImage);
        }
        else {
          Arrays.fill(newByteCounts, stripByteCounts[0]);
        }
        temp.put(new Integer(IFD.STRIP_BYTE_COUNTS), newByteCounts);

        tempIFDs.add(temp);
      }
      ifds = tempIFDs;
    }
    catch (IllegalArgumentException exc) { traceDebug(exc); } // unknown tag
    catch (NullPointerException exc) { traceDebug(exc); }
    catch (IOException exc) { traceDebug(exc); }
    catch (FormatException exc) { traceDebug(exc); }

    // parse (mangle) TIFF comment
    String descr = ifds.get(0).getComment();
    if (descr != null) {
      String[] lines = descr.split("\n");
      StringBuffer sb = new StringBuffer();
      for (int i=0; i<lines.length; i++) {
        String line = lines[i].trim();
        int colon = line.indexOf(": ");

        String descrValue = null;

        if (colon < 0) {
          // normal line (not a key/value pair)
          if (line.length() > 0) {
            // not a blank line
            descrValue = line;
          }
        }
        else {
          if (i == 0) {
            // first line could be mangled; make a reasonable guess
            int dot = line.lastIndexOf(".", colon);
            if (dot >= 0) {
              descrValue = line.substring(0, dot + 1);
            }
            line = line.substring(dot + 1);
            colon -= dot + 1;
          }

          // append value to description
          if (descrValue != null) {
            sb.append(descrValue);
            if (!descrValue.endsWith(".")) sb.append(".");
            sb.append("  ");
          }

          // add key/value pair embedded in comment as separate metadata
          String key = line.substring(0, colon);
          String value = line.substring(colon + 2);
          addSeriesMeta(key, value);
          if (key.equals("Exposure")) {
            if (value.indexOf(" ") != -1) {
              value = value.substring(0, value.indexOf(" "));
            }
            float exposure = Float.parseFloat(value);
            exposureTime = new Float(exposure / 1000);
          }
        }
      }

      // replace comment with trimmed version
      descr = sb.toString().trim();
      if (descr.equals("")) metadata.remove("Comment");
      else addSeriesMeta("Comment", descr);
    }

    core[0].sizeT = getImageCount() / (getSizeZ() * (getSizeC() / rgbChannels));
    if (getSizeT() * getSizeZ() * (getSizeC() / rgbChannels) !=
      getImageCount())
    {
      core[0].sizeT = 1;
      core[0].sizeZ = getImageCount() / (getSizeC() / rgbChannels);
    }

    // if '_t' is present in the file name, swap Z and T sizes
    // this file was probably part of a larger dataset, but the .nd file is
    //  missing

    String filename =
      currentId.substring(currentId.lastIndexOf(File.separator) + 1);
    if (filename.indexOf("_t") != -1 && getSizeT() > 1) {
      int z = getSizeZ();
      core[0].sizeZ = getSizeT();
      core[0].sizeT = z;
    }
    if (getSizeZ() == 0) core[0].sizeZ = 1;
    if (getSizeT() == 0) core[0].sizeT = 1;
  }

  // -- Helper methods --

  /**
   * Populates metadata fields with some contained in MetaMorph UIC2 Tag.
   * (for each plane: 6 integers:
   * zdistance numerator, zdistance denominator,
   * creation date, creation time, modif date, modif time)
   * @param uic2offset offset to UIC2 (33629) tag entries
   *
   * not a regular tiff tag (6*N entries, N being the tagCount)
   * @throws IOException
   */
  void parseUIC2Tags(long uic2offset) throws IOException {
    long saveLoc = in.getFilePointer();
    in.seek(uic2offset);

    /*number of days since the 1st of January 4713 B.C*/
    String cDate;
    /*milliseconds since 0:00*/
    String cTime;

    /*z step, distance separating previous slice from  current one*/
    String iAsString;

    zDistances = new double[mmPlanes];
    internalStamps = new long[mmPlanes];

    for (int i=0; i<mmPlanes; i++) {
      iAsString = intFormatMax(i, mmPlanes);
      zDistances[i] = readRational(in).doubleValue();
      addSeriesMeta("zDistance[" + iAsString + "]", zDistances[i]);

      if (zDistances[i] != 0.0) core[0].sizeZ++;

      cDate = decodeDate(in.readInt());
      cTime = decodeTime(in.readInt());

      internalStamps[i] = DateTools.getTime(cDate + " " + cTime,
        LONG_DATE_FORMAT);

      addSeriesMeta("creationDate[" + iAsString + "]", cDate);
      addSeriesMeta("creationTime[" + iAsString + "]", cTime);
      // modification date and time are skipped as they all seem equal to 0...?
      in.skip(8);
    }
    if (getSizeZ() == 0) core[0].sizeZ = 1;

    in.seek(saveLoc);
  }

  /**
   * UIC4 metadata parser
   *
   * UIC4 Table contains per-plane blocks of metadata
   * stage X/Y positions,
   * camera chip offsets,
   * stage labels...
   * @param long uic4offset: offset of UIC4 table (not tiff-compliant)
   * @throws IOException
   */
  private void parseUIC4Tags(long uic4offset) throws IOException {
    long saveLoc = in.getFilePointer();
    in.seek(uic4offset);
    short id = in.readShort();
    while (id != 0) {
      switch (id) {
        case 28:
          readStagePositions();
          break;
        case 29:
          readRationals(
            new String[] {"cameraXChipOffset", "cameraYChipOffset"});
          break;
        case 37:
          readStageLabels();
          break;
        case 40:
          readRationals(new String[] {"absoluteZ"});
          break;
        case 41:
          readAbsoluteZValid();
          break;
      }
      id = in.readShort();
    }
    in.seek(saveLoc);
  }

  private void readStagePositions() throws IOException {
    stageX = new double[mmPlanes];
    stageY = new double[mmPlanes];
    String pos;
    for (int i=0; i<mmPlanes; i++) {
      pos = intFormatMax(i, mmPlanes);
      stageX[i] = readRational(in).doubleValue();
      stageY[i] = readRational(in).doubleValue();
      addSeriesMeta("stageX[" + pos + "]", stageX[i]);
      addSeriesMeta("stageY[" + pos + "]", stageY[i]);
    }
  }

  private void readRationals(String[] labels) throws IOException {
    String pos;
    for (int i=0; i<mmPlanes; i++) {
      pos = intFormatMax(i, mmPlanes);
      for (int q=0; q<labels.length; q++) {
        addSeriesMeta(labels[q] + "[" + pos + "]",
          readRational(in).doubleValue());
      }
    }
  }

  void readStageLabels() throws IOException {
    int strlen;
    String iAsString;
    for (int i=0; i<mmPlanes; i++) {
      iAsString = intFormatMax(i, mmPlanes);
      strlen = in.readInt();
      addSeriesMeta("stageLabel[" + iAsString + "]", in.readString(strlen));
    }
  }

  void readAbsoluteZValid() throws IOException {
    for (int i=0; i<mmPlanes; i++) {
      addSeriesMeta("absoluteZValid[" + intFormatMax(i, mmPlanes) + "]",
        in.readInt());
    }
  }

  /**
   * UIC1 entry parser
   * @throws IOException
   * @param long uic1offset : offset as found in the tiff tag 33628 (UIC1Tag)
   * @param int uic1count : number of entries in UIC1 table (not tiff-compliant)
   */
  private void parseUIC1Tags(long uic1offset, int uic1count) throws IOException
  {
    // Loop through and parse out each field. A field whose
    // code is "0" represents the end of the fields so we'll stop
    // when we reach that; much like a NULL terminated C string.
    long saveLoc = in.getFilePointer();
    in.seek(uic1offset);
    int currentID, valOrOffset;
    // variable declarations, because switch is dumb
    int num, denom;
    String thedate, thetime;
    long lastOffset;
    for (int i=0; i<uic1count; i++) {
      currentID = in.readInt();
      valOrOffset = in.readInt();
      lastOffset = in.getFilePointer();

      String key = getKey(currentID);
      Object value = String.valueOf(valOrOffset);

      switch (currentID) {
        case 3:
          value = valOrOffset != 0 ? "on" : "off";
          break;
        case 4:
        case 5:
        case 21:
        case 22:
        case 23:
        case 24:
        case 38:
        case 39:
          value = readRational(in, valOrOffset);
          break;
        case 6:
        case 25:
          in.seek(valOrOffset);
          num = in.readInt();
          value = in.readString(num);
          break;
        case 7:
          in.seek(valOrOffset);
          num = in.readInt();
          imageName = in.readString(num);
          value = imageName;
          break;
        case 8:
          if (valOrOffset == 1) value = "inside";
          else if (valOrOffset == 2) value = "outside";
          else value = "off";
          break;
        case 17: // oh how we hate you Julian format...
          in.seek(valOrOffset);
          thedate = decodeDate(in.readInt());
          thetime = decodeTime(in.readInt());
          imageCreationDate = thedate + " " + thetime;
          value = imageCreationDate;
          break;
        case 16:
          in.seek(valOrOffset);
          thedate = decodeDate(in.readInt());
          thetime = decodeTime(in.readInt());
          value = thedate + " " + thetime;
          break;
        case 26:
          in.seek(valOrOffset);
          int standardLUT = in.readInt();
          switch (standardLUT) {
            case 0:
              value = "monochrome";
              break;
            case 1:
              value = "pseudocolor";
              break;
            case 2:
              value = "Red";
              break;
            case 3:
              value = "Green";
              break;
            case 4:
              value = "Blue";
              break;
            case 5:
              value = "user-defined";
              break;
            default:
              value = "monochrome";
          }
          break;
        case 34:
          value = String.valueOf(in.readInt());
          break;
        case 46:
          in.seek(valOrOffset);
          int xBin = in.readInt();
          int yBin = in.readInt();
          binning = xBin + "x" + yBin;
          value = binning;
          break;
      }
      addSeriesMeta(key, value);
      in.seek(lastOffset);

      if ("Zoom".equals(key) && value != null) {
        zoom = Float.parseFloat(value.toString());
      }
    }
    in.seek(saveLoc);
  }

  // -- Utility methods --

  /** Converts a Julian date value into a human-readable string. */
  public static String decodeDate(int julian) {
    long a, b, c, d, e, alpha, z;
    short day, month, year;

    // code reused from the Metamorph data specification
    z = julian + 1;

    if (z < 2299161L) a = z;
    else {
      alpha = (long) ((z - 1867216.25) / 36524.25);
      a = z + 1 + alpha - alpha / 4;
    }

    b = (a > 1721423L ? a + 1524 : a + 1158);
    c = (long) ((b - 122.1) / 365.25);
    d = (long) (365.25 * c);
    e = (long) ((b - d) / 30.6001);

    day = (short) (b - d - (long) (30.6001 * e));
    month = (short) ((e < 13.5) ? e - 1 : e - 13);
    year = (short) ((month > 2.5) ? (c - 4716) : c - 4715);

    return intFormat(day, 2) + "/" + intFormat(month, 2) + "/" + year;
  }

  /** Converts a time value in milliseconds into a human-readable string. */
  public static String decodeTime(int millis) {
    Calendar time = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    time.setTimeInMillis(millis);
    String hours = intFormat(time.get(Calendar.HOUR_OF_DAY), 2);
    String minutes = intFormat(time.get(Calendar.MINUTE), 2);
    String seconds = intFormat(time.get(Calendar.SECOND), 2);
    String ms = intFormat(time.get(Calendar.MILLISECOND), 3);
    return hours + ":" + minutes + ":" + seconds + ":" + ms;
  }

  /** Formats an integer value with leading 0s if needed. */
  public static String intFormat(int myint, int digits) {
    DecimalFormat df = new DecimalFormat();
    df.setMaximumIntegerDigits(digits);
    df.setMinimumIntegerDigits(digits);
    return df.format(myint);
  }

  /**
   * Formats an integer with leading 0 using maximum sequence number.
   *
   * @param myint integer to format
   * @param maxint max of "myint"
   * @return String
   */
  public static String intFormatMax(int myint, int maxint) {
    return intFormat(myint, String.valueOf(maxint).length());
  }

  private TiffRational readRational(RandomAccessInputStream s)
    throws IOException
  {
    return readRational(s, s.getFilePointer());
  }

  private TiffRational readRational(RandomAccessInputStream s, long offset)
    throws IOException
  {
    s.seek(offset);
    int num = s.readInt();
    int denom = s.readInt();
    return new TiffRational(num, denom);
  }

  private void setCanLookForND(boolean v) {
    FormatTools.assertId(currentId, false, 1);
    canLookForND = v;
  }

  private String getKey(int id) {
    switch (id) {
      case 1: return "MinScale";
      case 2: return "MaxScale";
      case 3: return "Spatial Calibration";
      case 4: return "XCalibration";
      case 5: return "YCalibration";
      case 6: return "CalibrationUnits";
      case 7: return "Name";
      case 8: return "ThreshState";
      case 9: return "ThreshStateRed";
      // there is no 10
      case 11: return "ThreshStateGreen";
      case 12: return "ThreshStateBlue";
      case 13: return "ThreshStateLo";
      case 14: return "ThreshStateHi";
      case 15: return "Zoom";
      case 16: return "DateTime";
      case 17: return "LastSavedTime";
      case 18: return "currentBuffer";
      case 19: return "grayFit";
      case 20: return "grayPointCount";
      case 21: return "grayX";
      case 22: return "gray";
      case 23: return "grayMin";
      case 24: return "grayMax";
      case 25: return "grayUnitName";
      case 26: return "StandardLUT";
      case 27: return "Wavelength";
      case 30: return "OverlayMask";
      case 31: return "OverlayCompress";
      case 32: return "Overlay";
      case 33: return "SpecialOverlayMask";
      case 34: return "SpecialOverlayCompress";
      case 35: return "SpecialOverlay";
      case 36: return "ImageProperty";
      case 38: return "AutoScaleLoInfo";
      case 39: return "AutoScaleHiInfo";
      case 42: return "Gamma";
      case 43: return "GammaRed";
      case 44: return "GammaGreen";
      case 45: return "GammaBlue";
      case 46: return "CameraBin";
    }
    return null;
  }

}
