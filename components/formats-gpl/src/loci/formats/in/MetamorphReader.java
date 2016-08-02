/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffIFDEntry;
import loci.formats.tiff.TiffParser;
import loci.formats.tiff.TiffRational;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Temperature;
import ome.units.quantity.Time;
import ome.units.UNITS;

/**
 * Reader is the file format reader for Metamorph STK files.
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Sebastien Huart Sebastien dot Huart at curie.fr
 */
public class MetamorphReader extends BaseTiffReader {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(MetamorphReader.class);

  public static final String SHORT_DATE_FORMAT = "yyyyMMdd HH:mm:ss";
  public static final String LONG_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

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
  private double zoom, stepSize;
  private Double exposureTime;
  private Vector<String> waveNames;
  private Vector<String> stageNames;
  private long[] internalStamps;
  private double[] zDistances;
  private Length[] stageX, stageY;
  private double zStart;
  private Double sizeX = null, sizeY = null;
  private double tempZ;
  private boolean validZ;

  private Double gain;

  private int mmPlanes; //number of metamorph planes

  private MetamorphReader[][] stkReaders;

  /** List of STK files in the dataset. */
  private String[][] stks;

  private String ndFilename;
  private boolean canLookForND = true;

  private boolean[] firstSeriesChannels;

  private boolean bizarreMultichannelAcquisition = false;

  private int openFiles = 0;

  private boolean hasStagePositions = false;
  private boolean hasChipOffsets = false;
  private boolean hasAbsoluteZ = false;
  private boolean hasAbsoluteZValid = false;

  // -- Constructor --

  /** Constructs a new Metamorph reader. */
  public MetamorphReader() {
    super("Metamorph STK", new String[] {"stk", "nd", "tif", "tiff"});
    domains = new String[] {FormatTools.LM_DOMAIN};
    hasCompanionFiles = true;
    suffixSufficient = false;
    datasetDescription = "One or more .stk or .tif/.tiff files plus an " +
      "optional .nd file";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    Location location = new Location(name);
    if (!location.exists()) {
        return false;
    }
    if (checkSuffix(name, "nd")) return true;
    if (open) {
      location = location.getAbsoluteFile();
      Location parent = location.getParentFile();

      String baseName = location.getName();

      while (baseName.indexOf("_") >= 0) {
        baseName = baseName.substring(0, baseName.lastIndexOf("_"));
        if (checkSuffix(name, suffixes) &&
          (new Location(parent, baseName + ".nd").exists() ||
          new Location(parent, baseName + ".ND").exists()))
        {
          return true;
        }
        if (checkSuffix(name, suffixes) &&
          (new Location(parent, baseName + ".htd").exists() ||
          new Location(parent, baseName + ".HTD").exists()))
        {
          return false;
        }
      }
    }
    return super.isThisType(name, open);
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser tp = new TiffParser(stream);
    IFD ifd = tp.getFirstIFD();
    if (ifd == null) return false;
    String software = ifd.getIFDTextValue(IFD.SOFTWARE);
    boolean validSoftware = software != null &&
      software.trim().toLowerCase().startsWith("metamorph");
    return validSoftware || (ifd.containsKey(UIC1TAG) &&
      ifd.containsKey(UIC3TAG) && ifd.containsKey(UIC4TAG));
  }

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return !checkSuffix(id, ND_SUFFIX);
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    if (checkSuffix(id, ND_SUFFIX)) return FormatTools.MUST_GROUP;

    Location l = new Location(id).getAbsoluteFile();
    String[] files = l.getParentFile().list();

    for (String file : files) {
      if (checkSuffix(file, ND_SUFFIX) &&
       l.getName().startsWith(file.substring(0, file.lastIndexOf("."))))
      {
        return FormatTools.MUST_GROUP;
      }
    }

    return FormatTools.CANNOT_GROUP;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (!noPixels && stks == null) return new String[] {currentId};
    else if (stks == null) return new String[0];

    Vector<String> v = new Vector<String>();
    if (ndFilename != null) v.add(ndFilename);
    if (!noPixels) {
      for (String stk : stks[getSeries()]) {
        if (stk != null && new Location(stk).exists()) {
          v.add(stk);
        }
      }
    }
    return v.toArray(new String[v.size()]);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    if (stks == null) {
      return super.openBytes(no, buf, x, y, w, h);
    }

    int[] coords = FormatTools.getZCTCoords(this, no % getSizeZ());
    int ndx = no / getSizeZ();
    if (bizarreMultichannelAcquisition) {
      int[] pos = getZCTCoords(no);
      ndx = getIndex(pos[0], 0, pos[2]) / getSizeZ();
    }
    if (stks[getSeries()].length == 1) ndx = 0;
    String file = stks[getSeries()][ndx];
    if (file == null) return buf;

    // the original file is a .nd file, so we need to construct a new reader
    // for the constituent STK files
    stkReaders[getSeries()][ndx].setMetadataOptions(
        new DefaultMetadataOptions(MetadataLevel.MINIMUM));
    int plane = stks[getSeries()].length == 1 ? no : coords[0];
    try {
      if (!file.equals(stkReaders[getSeries()][ndx].getCurrentFile())) {
        openFiles++;
      }
      stkReaders[getSeries()][ndx].setId(file);

      if (bizarreMultichannelAcquisition) {
        int realX = getZCTCoords(no)[1] == 0 ? x : x + getSizeX();
        stkReaders[getSeries()][ndx].openBytes(plane, buf, realX, y, w, h);
      }
      else {
        stkReaders[getSeries()][ndx].openBytes(plane, buf, x, y, w, h);
      }
    }
    finally {
      int count = stkReaders[getSeries()][ndx].getImageCount();
      if (plane == count - 1 || openFiles > 128) {
        stkReaders[getSeries()][ndx].close();
        openFiles--;
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (stkReaders != null) {
      for (MetamorphReader[] s : stkReaders) {
        if (s != null) {
          for (MetamorphReader reader : s) {
            if (reader != null) reader.close(fileOnly);
          }
        }
      }
    }
    if (!fileOnly) {
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
      zDistances = null;
      stageX = stageY = null;
      firstSeriesChannels = null;
      sizeX = sizeY = null;
      tempZ = 0d;
      validZ = false;
      stkReaders = null;
      gain = null;
      bizarreMultichannelAcquisition = false;
      openFiles = 0;
      hasStagePositions = false;
      hasChipOffsets = false;
      hasAbsoluteZ = false;
      hasAbsoluteZValid = false;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    if (checkSuffix(id, ND_SUFFIX)) {
      LOGGER.info("Initializing " + id);
      // find an associated STK file
      String stkFile = id.substring(0, id.lastIndexOf("."));
      if (stkFile.indexOf(File.separator) != -1) {
        stkFile = stkFile.substring(stkFile.lastIndexOf(File.separator) + 1);
      }
      Location parent = new Location(id).getAbsoluteFile().getParentFile();
      LOGGER.info("Looking for STK file in {}", parent.getAbsolutePath());
      String[] dirList = parent.list(true);
      for (String f : dirList) {
        int underscore = f.indexOf("_");
        if (underscore < 0) underscore = f.indexOf(".");
        if (underscore < 0) underscore = f.length();
        String prefix = f.substring(0, underscore);

        if ((f.equals(stkFile) || stkFile.startsWith(prefix)) &&
          checkSuffix(f, STK_SUFFIX))
        {
          stkFile = new Location(parent.getAbsolutePath(), f).getAbsolutePath();
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
    else if (canLookForND && isGroupFiles()) {
      // an STK file was passed to initFile
      // let's check the parent directory for an .nd file
      Location stk = new Location(id).getAbsoluteFile();
      String stkName = stk.getName();
      String stkPrefix = stkName;
      if (stkPrefix.indexOf("_") >= 0) {
        stkPrefix = stkPrefix.substring(0, stkPrefix.indexOf("_") + 1);
      }
      Location parent = stk.getParentFile();
      String[] list = parent.list(true);
      int matchingChars = 0;
      for (String f : list) {
        if (checkSuffix(f, ND_SUFFIX)) {
          String prefix = f.substring(0, f.lastIndexOf("."));
          if (prefix.indexOf("_") >= 0) {
            prefix = prefix.substring(0, prefix.indexOf("_") + 1);
          }
          if (stkName.startsWith(prefix) || prefix.equals(stkPrefix)) {
            int charCount = 0;
            for (int i=0; i<f.length(); i++) {
              if (i >= stkName.length()) {
                break;
              }
              if (f.charAt(i) == stkName.charAt(i)) {
                charCount++;
              }
              else {
                break;
              }
            }

            if (charCount > matchingChars || (charCount == matchingChars &&
              f.charAt(charCount) == '.'))
            {
              ndfile = new Location(parent, f).getAbsoluteFile();
              matchingChars = charCount;
            }
          }
        }
      }
    }

    String creationTime = null;

    if (ndfile != null && ndfile.exists() &&
      (fileGroupOption(id) == FormatTools.MUST_GROUP || isGroupFiles()))
    {
      // parse key/value pairs from .nd file

      int zc = getSizeZ(), cc = getSizeC(), tc = getSizeT();
      int nstages = 0;
      String z = null, c = null, t = null;
      Vector<Boolean> hasZ = new Vector<Boolean>();
      waveNames = new Vector<String>();
      stageNames = new Vector<String>();
      boolean useWaveNames = true;

      ndFilename = ndfile.getAbsolutePath();
      String[] lines = DataTools.readFile(ndFilename).split("\n");

      boolean globalDoZ = true;
      boolean doTimelapse = false;

      StringBuilder currentValue = new StringBuilder();
      String key = "";

      for (String line : lines) {
        int comma = line.indexOf(",");
        if (comma <= 0) {
          currentValue.append("\n");
          currentValue.append(line);
          continue;
        }

        String value = currentValue.toString();
        addGlobalMeta(key, value);
        if (key.equals("NZSteps")) z = value;
        else if (key.equals("DoTimelapse")) {
          doTimelapse = Boolean.parseBoolean(value);
        }
        else if (key.equals("NWavelengths")) c = value;
        else if (key.equals("NTimePoints")) t = value;
        else if (key.startsWith("WaveDoZ")) {
          hasZ.add(new Boolean(value.toLowerCase()));
        }
        else if (key.startsWith("WaveName")) {
          String waveName = value.substring(1, value.length() - 1);
          if (waveName.equals("Both lasers") || waveName.startsWith("DUAL")) {
            bizarreMultichannelAcquisition = true;
          }
          waveNames.add(waveName);
        }
        else if (key.startsWith("Stage")) {
          stageNames.add(value);
        }
        else if (key.startsWith("StartTime")) {
          creationTime = value;
        }
        else if (key.equals("ZStepSize")) {
          value = value.replace(',', '.');
          stepSize = Double.parseDouble(value);
        }
        else if (key.equals("NStagePositions")) {
          nstages = Integer.parseInt(value);
        }
        else if (key.equals("WaveInFileName")) {
          useWaveNames = Boolean.parseBoolean(value);
        }
        else if (key.equals("DoZSeries")) {
          globalDoZ = new Boolean(value.toLowerCase());
        }

        key = line.substring(1, comma - 1).trim();
        currentValue.delete(0, currentValue.length());
        currentValue.append(line.substring(comma + 1).trim());
      }

      if (!globalDoZ) {
        for (int i=0; i<hasZ.size(); i++) {
          hasZ.set(i, false);
        }
      }

      // figure out how many files we need

      if (z != null) zc = Integer.parseInt(z);
      if (c != null) cc = Integer.parseInt(c);
      if (t != null) tc = Integer.parseInt(t);
      else if (!doTimelapse) {
        tc = 1;
      }

      if (cc == 0) cc = 1;
      if (cc == 1 && bizarreMultichannelAcquisition) {
        cc = 2;
      }
      if (tc == 0) {
        tc = 1;
      }

      int numFiles = cc * tc;
      if (nstages > 0) numFiles *= nstages;

      // determine series count
      int stagesCount = nstages == 0 ? 1 : nstages;
      int seriesCount = stagesCount;
      firstSeriesChannels = new boolean[cc];
      Arrays.fill(firstSeriesChannels, true);
      boolean differentZs = false;
      for (int i=0; i<cc; i++) {
        boolean hasZ1 = i < hasZ.size() && hasZ.get(i).booleanValue();
        boolean hasZ2 = i != 0 && (i - 1 < hasZ.size()) &&
          hasZ.get(i - 1).booleanValue();
        if (i > 0 && hasZ1 != hasZ2 && globalDoZ) {
          if (!differentZs) seriesCount *= 2;
          differentZs = true;
        }
      }

      int channelsInFirstSeries = cc;
      if (differentZs) {
        channelsInFirstSeries = 0;
        for (int i=0; i<cc; i++) {
          if ((!hasZ.get(0) && i == 0) ||
            (hasZ.get(0) && hasZ.get(i).booleanValue()))
          {
            channelsInFirstSeries++;
          }
          else firstSeriesChannels[i] = false;
        }
      }

      stks = new String[seriesCount][];
      if (seriesCount == 1) stks[0] = new String[numFiles];
      else if (differentZs) {
        for (int i=0; i<stagesCount; i++) {
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

      // build list of STK files

      boolean anyZ = hasZ.contains(Boolean.TRUE);
      int[] pt = new int[seriesCount];
      for (int i=0; i<tc; i++) {
        for (int s=0; s<stagesCount; s++) {
          for (int j=0; j<cc; j++) {
            boolean validZ = j >= hasZ.size() || hasZ.get(j).booleanValue();
            int seriesNdx = s * (seriesCount / stagesCount);

            if ((seriesCount != 1 &&
              (!validZ || (hasZ.size() > 0  && !hasZ.get(0)))) ||
              (nstages == 0 && ((!validZ && cc > 1) || seriesCount > 1)))
            {
              if (anyZ && j > 0 && seriesNdx < seriesCount - 1 &&
                (!validZ || !hasZ.get(0)))
              {
                seriesNdx++;
              }
            }
            if (seriesNdx >= stks.length || seriesNdx >= pt.length ||
              pt[seriesNdx] >= stks[seriesNdx].length)
            {
              continue;
            }
            stks[seriesNdx][pt[seriesNdx]] = prefix;
            if (j < waveNames.size() && waveNames.get(j) != null) {
              stks[seriesNdx][pt[seriesNdx]] += "_w" + (j + 1);
              if (useWaveNames) {
                String waveName = waveNames.get(j);
                // If there are underscores in the wavelength name, translate
                // them to hyphens. (See #558)
                waveName = waveName.replace('_', '-');
                // If there are slashes (forward or backward) in the wavelength
                // name, translate them to hyphens. (See #5922)
                waveName = waveName.replace('/', '-');
                waveName = waveName.replace('\\', '-');
                waveName = waveName.replace('(', '-');
                waveName = waveName.replace(')', '-');
                stks[seriesNdx][pt[seriesNdx]] += waveName;
              }
            }
            if (nstages > 0) {
              stks[seriesNdx][pt[seriesNdx]] += "_s" + (s + 1);
            }
            if (tc > 1 || doTimelapse) {
              stks[seriesNdx][pt[seriesNdx]] += "_t" + (i + 1) + ".STK";
            }
            else stks[seriesNdx][pt[seriesNdx]] += ".STK";
            pt[seriesNdx]++;
          }
        }
      }

      ndfile = ndfile.getAbsoluteFile();

      // check that each STK file exists

      for (int s=0; s<stks.length; s++) {
        for (int f=0; f<stks[s].length; f++) {
          Location l = new Location(ndfile.getParent(), stks[s][f]);
          stks[s][f] = getRealSTKFile(l);
        }
      }

      String file = locateFirstValidFile();
      if (file == null) {
        throw new FormatException(
            "Unable to locate at least one valid STK file!");
      }

      RandomAccessInputStream s = new RandomAccessInputStream(file, 16);
      TiffParser tp = new TiffParser(s);
      IFD ifd = tp.getFirstIFD();
      CoreMetadata ms0 = core.get(0);
      s.close();
      ms0.sizeX = (int) ifd.getImageWidth();
      ms0.sizeY = (int) ifd.getImageLength();

      if (bizarreMultichannelAcquisition) {
        ms0.sizeX /= 2;
      }

      ms0.sizeZ = hasZ.size() > 0 && !hasZ.get(0) ? 1 : zc;
      ms0.sizeC = cc;
      ms0.sizeT = tc;
      ms0.imageCount = getSizeZ() * getSizeC() * getSizeT();
      ms0.dimensionOrder = "XYZCT";

      if (stks != null && stks.length > 1) {
        // Note that core can't be replaced with newCore until the end of this block.
        ArrayList<CoreMetadata> newCore = new ArrayList<CoreMetadata>();
        for (int i=0; i<stks.length; i++) {
          CoreMetadata ms = new CoreMetadata();
          newCore.add(ms);
          ms.sizeX = getSizeX();
          ms.sizeY = getSizeY();
          ms.sizeZ = getSizeZ();
          ms.sizeC = getSizeC();
          ms.sizeT = getSizeT();
          ms.pixelType = getPixelType();
          ms.imageCount = getImageCount();
          ms.dimensionOrder = getDimensionOrder();
          ms.rgb = isRGB();
          ms.littleEndian = isLittleEndian();
          ms.interleaved = isInterleaved();
          ms.orderCertain = true;
        }
        if (stks.length > nstages) {
          for (int j=0; j<stagesCount; j++) {
            int idx = j * 2 + 1;
            CoreMetadata midx = newCore.get(idx);
            CoreMetadata pmidx = newCore.get(j * 2);
            pmidx.sizeC = stks[j * 2].length / getSizeT();
            midx.sizeC = stks[idx].length / midx.sizeT;
            midx.sizeZ =
             hasZ.size() > 1 && hasZ.get(1) && core.get(0).sizeZ == 1 ? zc : 1;
            pmidx.imageCount = pmidx.sizeC *
              pmidx.sizeT * pmidx.sizeZ;
            midx.imageCount =
              midx.sizeC * midx.sizeT * midx.sizeZ;
          }
        }
        core = newCore;
      }
    }

    if (stks == null) {
      stkReaders = new MetamorphReader[1][1];
      stkReaders[0][0] = new MetamorphReader();
      stkReaders[0][0].setCanLookForND(false);
    }
    else {
      stkReaders = new MetamorphReader[stks.length][];
      for (int i=0; i<stks.length; i++) {
        stkReaders[i] = new MetamorphReader[stks[i].length];
        for (int j=0; j<stkReaders[i].length; j++) {
          stkReaders[i][j] = new MetamorphReader();
          stkReaders[i][j].setCanLookForND(false);
          if (j > 0) {
            stkReaders[i][j].setMetadataOptions(
              new DefaultMetadataOptions(MetadataLevel.MINIMUM));
          }
        }
      }
    }

    Vector<String> timestamps = null;
    MetamorphHandler handler = null;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);
    String detectorID = MetadataTools.createLSID("Detector", 0, 0);

    for (int i=0; i<getSeriesCount(); i++) {
      setSeries(i);
      handler = new MetamorphHandler(getSeriesMetadata());

      String instrumentID = MetadataTools.createLSID("Instrument", i);
      store.setInstrumentID(instrumentID, i);
      store.setImageInstrumentRef(instrumentID, i);

      if (i == 0) {
        store.setDetectorID(detectorID, 0, 0);
        store.setDetectorType(getDetectorType("Other"), 0, 0);
      }

      String comment = getFirstComment(i);
      if (comment != null && comment.startsWith("<MetaData>")) {
        try {
          XMLTools.parseXML(XMLTools.sanitizeXML(comment), handler);
        }
        catch (IOException e) { }
      }

      if (creationTime != null) {
        String date = DateTools.formatDate(creationTime, SHORT_DATE_FORMAT, ".");
        if (date != null) {
          store.setImageAcquisitionDate(new Timestamp(date), 0);
        }
      }

      store.setImageName(makeImageName(i).trim(), i);

      if (getMetadataOptions().getMetadataLevel() == MetadataLevel.MINIMUM) {
        continue;
      }
      store.setImageDescription("", i);

      store.setImagingEnvironmentTemperature(
              new Temperature(handler.getTemperature(), UNITS.CELSIUS), i);

      if (sizeX == null) sizeX = handler.getPixelSizeX();
      if (sizeY == null) sizeY = handler.getPixelSizeY();

      Length physicalSizeX = FormatTools.getPhysicalSizeX(sizeX);
      Length physicalSizeY = FormatTools.getPhysicalSizeY(sizeY);
      if (physicalSizeX != null) {
        store.setPixelsPhysicalSizeX(physicalSizeX, i);
      }
      if (physicalSizeY != null) {
        store.setPixelsPhysicalSizeY(physicalSizeY, i);
      }
      if (zDistances != null) {
        stepSize = zDistances[0];
      }
      else {
        Vector<Double> zPositions = new Vector<Double>();
        Vector<Double> uniqueZ = new Vector<Double>();
    	  
        for (IFD ifd : ifds) {
          MetamorphHandler zPlaneHandler = new MetamorphHandler();

          String zComment = ifd.getComment();
          if (zComment != null &&
              zComment.startsWith("<MetaData>"))
          {
            try {
              XMLTools.parseXML(XMLTools.sanitizeXML(zComment),
                  zPlaneHandler);
            }
            catch (IOException e) { }
          }

          zPositions = zPlaneHandler.getZPositions();
          for (Double z : zPositions) {
            if (!uniqueZ.contains(z)) uniqueZ.add(z);
          }
        } 
        if (uniqueZ.size() > 1 && uniqueZ.size() == getSizeZ()) {
          BigDecimal lastZ = BigDecimal.valueOf(uniqueZ.get(uniqueZ.size() - 1));
          BigDecimal firstZ = BigDecimal.valueOf(uniqueZ.get(0));
          BigDecimal zRange = (lastZ.subtract(firstZ)).abs();
          BigDecimal zSize = BigDecimal.valueOf((double)(getSizeZ() - 1));
          MathContext mc = new MathContext(10, RoundingMode.HALF_UP);
          stepSize = zRange.divide(zSize, mc).doubleValue();
        }
      }
      
      Length physicalSizeZ = FormatTools.getPhysicalSizeZ(stepSize);
      if (physicalSizeZ != null) {
        store.setPixelsPhysicalSizeZ(physicalSizeZ, i);
      }

      String objectiveID = MetadataTools.createLSID("Objective", i, 0);
      store.setObjectiveID(objectiveID, i, 0);
      if (handler.getLensNA() != 0) {
          store.setObjectiveLensNA(handler.getLensNA(), i, 0);
      }
      store.setObjectiveSettingsID(objectiveID, i);

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
          store.setChannelName(waveNames.get(waveIndex).trim(), i, c);
        }
        if (handler.getBinning() != null) binning = handler.getBinning();
        if (binning != null) {
          store.setDetectorSettingsBinning(getBinning(binning), i, c);
        }
        if (handler.getReadOutRate() != 0) {
          store.setDetectorSettingsReadOutRate(
                  new Frequency(handler.getReadOutRate(), UNITS.HERTZ), i, c);
        }

        if (gain == null) {
          gain = handler.getGain();
        }

        if (gain != null) {
          store.setDetectorSettingsGain(gain, i, c);
        }
        store.setDetectorSettingsID(detectorID, i, c);

        if (wave != null && waveIndex < wave.length) {
          Length wavelength =
            FormatTools.getWavelength(wave[waveIndex]);

          if ((int) wave[waveIndex] >= 1) {
            // link LightSource to Image
            String lightSourceID =
              MetadataTools.createLSID("LightSource", i, c);
            store.setLaserID(lightSourceID, i, c);
            store.setChannelLightSourceSettingsID(lightSourceID, i, c);
            store.setLaserType(getLaserType("Other"), i, c);
            store.setLaserLaserMedium(getLaserMedium("Other"), i, c);

            if (wavelength != null) {
              store.setChannelLightSourceSettingsWavelength(wavelength, i, c);
            }
          }
        }
        waveIndex++;
      }

      timestamps = handler.getTimestamps();

      for (int t=0; t<timestamps.size(); t++) {
        String date = DateTools.convertDate(DateTools.getTime(
          timestamps.get(t), SHORT_DATE_FORMAT, "."), DateTools.UNIX,
          SHORT_DATE_FORMAT + ".SSS");
        addSeriesMetaList("timestamp", date);
      }

      long startDate = 0;
      if (timestamps.size() > 0) {
        startDate = DateTools.getTime(timestamps.get(0), SHORT_DATE_FORMAT, ".");
      }

      final Length positionX = handler.getStagePositionX();
      final Length positionY = handler.getStagePositionY();
      Vector<Double> exposureTimes = handler.getExposures();
      if (exposureTimes.size() == 0) {
        for (int p=0; p<getImageCount(); p++) {
          exposureTimes.add(exposureTime);
        }
      }
      else if (exposureTimes.size() == 1 && exposureTimes.size() < getSizeC()) {
        for (int c=1; c<getSizeC(); c++) {
          MetamorphHandler channelHandler = new MetamorphHandler();

          String channelComment = getComment(i, c);
          if (channelComment != null &&
            channelComment.startsWith("<MetaData>"))
          {
            try {
              XMLTools.parseXML(XMLTools.sanitizeXML(channelComment),
                channelHandler);
            }
            catch (IOException e) { }
          }

          Vector<Double> channelExpTime = channelHandler.getExposures();
          exposureTimes.add(channelExpTime.get(0));
        }
      }

      int lastFile = -1;
      IFDList lastIFDs = null;
      IFD lastIFD = null;
      long[] lastOffsets = null;

      double distance = zStart;
      TiffParser tp = null;
      RandomAccessInputStream stream = null;

      for (int p=0; p<getImageCount(); p++) {
        int[] coords = getZCTCoords(p);
        Double deltaT = Double.valueOf(0);
        Double expTime = exposureTime;
        Double xmlZPosition = null;

        int fileIndex = getIndex(0, 0, coords[2]) / getSizeZ();
        if (fileIndex >= 0) {
          String file = stks == null ? currentId : stks[i][fileIndex];
          if (file != null) {
            if (fileIndex != lastFile) {
              if (stream != null) {
                stream.close();
              }
              stream = new RandomAccessInputStream(file, 16);
              tp = new TiffParser(stream);
              tp.checkHeader();
              IFDList f = tp.getIFDs();
              if (f.size() > 0) {
                lastFile = fileIndex;
                lastIFDs = f;
              }
              else {
                file = null;
                stks[i][fileIndex] = null;
              }
            }
          }

          if (file != null) {
            lastIFD = lastIFDs.get(p % lastIFDs.size());
            Object commentEntry = lastIFD.get(IFD.IMAGE_DESCRIPTION);
            if (commentEntry != null) {
              if (commentEntry instanceof String) {
                comment = (String) commentEntry;
              }
              else if (commentEntry instanceof TiffIFDEntry) {
                comment = tp.getIFDValue((TiffIFDEntry) commentEntry).toString();
              }
            }
            if (comment != null) comment = comment.trim();
            if (comment != null && comment.startsWith("<MetaData>")) {
              String[] lines = comment.split("\n");

              timestamps = new Vector<String>();

              for (String line : lines) {
                line = line.trim();
                if (line.startsWith("<prop")) {
                  int firstQuote = line.indexOf("\"") + 1;
                  int lastQuote = line.lastIndexOf("\"");
                  String key =
                    line.substring(firstQuote, line.indexOf("\"", firstQuote));
                  String value = line.substring(
                    line.lastIndexOf("\"", lastQuote - 1) + 1, lastQuote);

                  if (key.equals("z-position")) {
                    xmlZPosition = new Double(value);
                  }
                  else if (key.equals("acquisition-time-local")) {
                    timestamps.add(value);
                  }
                }
              }
            }
          }
        }

        int index = 0;

        if (timestamps.size() > 0) {
          if (coords[2] < timestamps.size()) index = coords[2];
          String stamp = timestamps.get(index);
          long ms = DateTools.getTime(stamp, SHORT_DATE_FORMAT, ".");
          deltaT = new Double((ms - startDate) / 1000.0);
        }
        else if (internalStamps != null && p < internalStamps.length) {
          long delta = internalStamps[p] - internalStamps[0];
          deltaT = new Double(delta / 1000.0);
          if (coords[2] < exposureTimes.size()) index = coords[2];
        }

        if (index == 0 && p > 0 && exposureTimes.size() > 0) {
          index = coords[1] % exposureTimes.size();
        }

        if (index < exposureTimes.size()) {
          expTime = exposureTimes.get(index);
        }
        if (deltaT != null) {
          store.setPlaneDeltaT(new Time(deltaT, UNITS.SECOND), i, p);
        }
        if (expTime != null) {
          store.setPlaneExposureTime(new Time(expTime, UNITS.SECOND), i, p);
        }

        if (stageX != null && p < stageX.length) {
          store.setPlanePositionX(stageX[p], i, p);
        }
        else if (positionX != null) {
          store.setPlanePositionX(positionX, i, p);
        }
        if (stageY != null && p < stageY.length) {
          store.setPlanePositionY(stageY[p], i, p);
        }
        else if (positionY != null) {
          store.setPlanePositionY(positionY, i, p);
        }
        if (zDistances != null && p < zDistances.length) {
          if (p > 0) {
            if (zDistances[p] != 0d) distance += zDistances[p];
            else distance += zDistances[0];
          }
          final Length zPos = new Length(distance, UNITS.REFERENCEFRAME);
          store.setPlanePositionZ(zPos, i, p);
        }
        else if (xmlZPosition != null) {
          final Length zPos = new Length(xmlZPosition, UNITS.REFERENCEFRAME);
          store.setPlanePositionZ(zPos, i, p);
        }
      }

      if (stream != null) {
        stream.close();
      }
    }
    setSeries(0);
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  @Override
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    CoreMetadata ms0 = core.get(0);

    ms0.sizeZ = 1;
    ms0.sizeT = 0;
    int rgbChannels = getSizeC();

    // Now that the base TIFF standard metadata has been parsed, we need to
    // parse out the STK metadata from the UIC4TAG.

    TiffIFDEntry uic1tagEntry = null;
    TiffIFDEntry uic2tagEntry = null;
    TiffIFDEntry uic4tagEntry = null;

    try {
      uic1tagEntry = tiffParser.getFirstIFDEntry(UIC1TAG);
      uic2tagEntry = tiffParser.getFirstIFDEntry(UIC2TAG);
      uic4tagEntry = tiffParser.getFirstIFDEntry(UIC4TAG);
    }
    catch (IllegalArgumentException exc) {
      LOGGER.debug("Unknown tag", exc);
    }

    try {
      if (uic4tagEntry != null) {
        mmPlanes = uic4tagEntry.getValueCount();
      }
      if (mmPlanes == 0) {
        mmPlanes = ifds.size();
      }
      if (uic2tagEntry != null) {
        parseUIC2Tags(uic2tagEntry.getValueOffset());
      }
      if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
        if (uic4tagEntry != null) {
          parseUIC4Tags(uic4tagEntry.getValueOffset());
        }
        if (uic1tagEntry != null) {
          parseUIC1Tags(uic1tagEntry.getValueOffset(),
            uic1tagEntry.getValueCount());
        }
      }
      in.seek(uic4tagEntry.getValueOffset());
    }
    catch (NullPointerException exc) {
      LOGGER.debug("", exc);
    }
    catch (IOException exc) {
      LOGGER.debug("Failed to parse proprietary tags", exc);
    }

    try {
      // copy ifds into a new array of Hashtables that will accommodate the
      // additional image planes
      IFD firstIFD = ifds.get(0);
      long[] uic2 = firstIFD.getIFDLongArray(UIC2TAG);
      if (uic2 == null) {
        throw new FormatException("Invalid Metamorph file. Tag " + UIC2TAG +
          " not found.");
      }
      ms0.imageCount = uic2.length;

      Object entry = firstIFD.getIFDValue(UIC3TAG);
      TiffRational[] uic3 = entry instanceof TiffRational[] ?
        (TiffRational[]) entry : new TiffRational[] {(TiffRational) entry};
      wave = new double[uic3.length];
      Vector<Double> uniqueWavelengths = new Vector<Double>();
      for (int i=0; i<uic3.length; i++) {
        wave[i] = uic3[i].doubleValue();
        addSeriesMeta("Wavelength [" + intFormatMax(i, mmPlanes) + "]",
          wave[i]);
        Double v = new Double(wave[i]);
        if (!uniqueWavelengths.contains(v)) uniqueWavelengths.add(v);
      }

      if (getSizeC() == 1) {
        ms0.sizeC = uniqueWavelengths.size();

        if (getSizeC() < getImageCount() &&
          getSizeC() > (getImageCount() - getSizeC()) &&
          (getImageCount() % getSizeC()) != 0)
        {
          ms0.sizeC = getImageCount();
        }
      }

      IFDList tempIFDs = new IFDList();

      long[] oldOffsets = firstIFD.getStripOffsets();
      long[] stripByteCounts = firstIFD.getStripByteCounts();
      int rowsPerStrip = (int) firstIFD.getRowsPerStrip()[0];
      int stripsPerImage = getSizeY() / rowsPerStrip;
      if (stripsPerImage * rowsPerStrip != getSizeY()) stripsPerImage++;

      PhotoInterp check = firstIFD.getPhotometricInterpretation();
      if (check == PhotoInterp.RGB_PALETTE) {
        firstIFD.putIFDValue(IFD.PHOTOMETRIC_INTERPRETATION,
          PhotoInterp.BLACK_IS_ZERO);
      }

      emWavelength = firstIFD.getIFDLongArray(UIC3TAG);

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

        temp.putIFDValue(IFD.STRIP_OFFSETS, newOffsets);

        long[] newByteCounts = new long[stripsPerImage];
        if (stripsPerImage * i < stripByteCounts.length) {
          System.arraycopy(stripByteCounts, stripsPerImage * i, newByteCounts,
            0, stripsPerImage);
        }
        else {
          Arrays.fill(newByteCounts, stripByteCounts[0]);
        }
        temp.putIFDValue(IFD.STRIP_BYTE_COUNTS, newByteCounts);

        tempIFDs.add(temp);
      }
      ifds = tempIFDs;
    }
    catch (IllegalArgumentException exc) {
      LOGGER.debug("Unknown tag", exc);
    }
    catch (NullPointerException exc) {
      LOGGER.debug("", exc);
    }
    catch (FormatException exc) {
      LOGGER.debug("Failed to build list of IFDs", exc);
    }

    // parse (mangle) TIFF comment
    String descr = ifds.get(0).getComment();
    if (descr != null) {
      String[] lines = descr.split("\n");
      final StringBuilder sb = new StringBuilder();
      for (int i=0; i<lines.length; i++) {
        String line = lines[i].trim();

        if (line.startsWith("<") && line.endsWith(">")) {
          // XML comment; this will have already been parsed so can be ignored
          break;
        }

        int colon = line.indexOf(":");

        if (colon < 0) {
          // normal line (not a key/value pair)
          if (line.length() > 0) {
            // not a blank line
            sb.append(line);
            sb.append("  ");
          }
        }
        else {
          String descrValue = null;
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
          String value = line.substring(colon + 1).trim();
          addSeriesMeta(key, value);
          if (key.equals("Exposure")) {
            if (value.indexOf("=") != -1) {
              value = value.substring(value.indexOf("=") + 1).trim();
            }
            if (value.indexOf(" ") != -1) {
              value = value.substring(0, value.indexOf(" "));
            }
            try {
              value = value.replace(',', '.');
              double exposure = Double.parseDouble(value);
              exposureTime = new Double(exposure / 1000);
            }
            catch (NumberFormatException e) { }
          }
          else if (key.equals("Bit Depth")) {
            if (value.indexOf("-") != -1) {
              value = value.substring(0, value.indexOf("-"));
            }
            try {
              ms0.bitsPerPixel = Integer.parseInt(value);
            }
            catch (NumberFormatException e) { }
          }
          else if (key.equals("Gain")) {
            int space = value.indexOf(" ");
            if (space != -1) {
              int nextSpace = value.indexOf(" ", space + 1);
              if (nextSpace < 0) {
                nextSpace = value.length();
              }
              try {
                gain = new Double(value.substring(space, nextSpace));
              }
              catch (NumberFormatException e) { }
            }
          }
        }
      }

      // replace comment with trimmed version
      descr = sb.toString().trim();
      if (descr.equals("")) metadata.remove("Comment");
      else addSeriesMeta("Comment", descr);
    }

    ms0.sizeT = getImageCount() / (getSizeZ() * (getSizeC() / rgbChannels));
    if (getSizeT() * getSizeZ() * (getSizeC() / rgbChannels) !=
      getImageCount())
    {
      ms0.sizeT = 1;
      ms0.sizeZ = getImageCount() / (getSizeC() / rgbChannels);
    }

    // if '_t' is present in the file name, swap Z and T sizes
    // this file was probably part of a larger dataset, but the .nd file is
    //  missing

    String filename =
      currentId.substring(currentId.lastIndexOf(File.separator) + 1);
    if (filename.indexOf("_t") != -1 && getSizeT() > 1) {
      int z = getSizeZ();
      ms0.sizeZ = getSizeT();
      ms0.sizeT = z;
    }
    if (getSizeZ() == 0) ms0.sizeZ = 1;
    if (getSizeT() == 0) ms0.sizeT = 1;

    if (getSizeZ() * getSizeT() * (isRGB() ? 1 : getSizeC()) != getImageCount())
    {
      ms0.sizeZ = getImageCount();
      ms0.sizeT = 1;
      if (!isRGB()) ms0.sizeC = 1;
    }
  }

  // -- Helper methods --

  /**
   * Check that the given STK file exists.  If it does, then return the
   * absolute path.  If it does not, then apply various formatting rules until
   * an existing file is found.
   *
   * @return the absolute path of an STK file, or null if no STK file is found.
   */
  private String getRealSTKFile(Location l) {
    if (l.exists()) return l.getAbsolutePath();
    String name = l.getName();
    String parent = l.getParent();

    if (name.indexOf("_") > 0) {
      String prefix = name.substring(0, name.indexOf("_"));
      String suffix = name.substring(name.indexOf("_"));

      String basePrefix = new Location(currentId).getName();
      int end = basePrefix.indexOf("_");
      if (end < 0) end = basePrefix.indexOf(".");
      basePrefix = basePrefix.substring(0, end);

      if (!basePrefix.equals(prefix)) {
        name = basePrefix + suffix;
        Location p = new Location(parent, name);
        if (p.exists()) return p.getAbsolutePath();
      }
    }

    // '%' can be converted to '-'
    if (name.indexOf("%") != -1) {
      name = name.replaceAll("%", "-");
      l = new Location(parent, name);
      if (!l.exists()) {
        // try replacing extension
        name = name.substring(0, name.lastIndexOf(".")) + ".TIF";
        l = new Location(parent, name);
        if (!l.exists()) {
          name = name.substring(0, name.lastIndexOf(".")) + ".tif";
          l = new Location(parent, name);
          return l.exists() ? l.getAbsolutePath() : null;
        }
      }
    }

    if (!l.exists()) {
      // try replacing extension
      int index = name.lastIndexOf(".");
      if (index < 0) index = name.length();
      name = name.substring(0, index) + ".TIF";
      l = new Location(parent, name);
      if (!l.exists()) {
        name = name.substring(0, name.lastIndexOf(".")) + ".tif";
        l = new Location(parent, name);
        if (!l.exists()) {
          name = name.substring(0, name.lastIndexOf(".")) + ".stk";
          l = new Location(parent, name);
          return l.exists() ? l.getAbsolutePath() : null;
        }
      }
    }
    return l.getAbsolutePath();
  }

  /**
   * Returns the TIFF comment from the first IFD of the first STK file in the
   * given series.
   */
  private String getFirstComment(int i) throws IOException {
    return getComment(i, 0);
  }

  private String getComment(int i, int no) throws IOException {
    if (stks != null && stks[i][no] != null) {
      RandomAccessInputStream stream = new RandomAccessInputStream(stks[i][no], 16);
      TiffParser tp = new TiffParser(stream);
      String comment = tp.getComment();
      stream.close();
      return comment;
    }
    return ifds.get(0).getComment();
  }

  /** Create an appropriate name for the given series. */
  private String makeImageName(int i) {
    String name = "";
    if (stageNames != null && stageNames.size() > 0) {
      int stagePosition = i / (getSeriesCount() / stageNames.size());
      name += "Stage" + (stagePosition + 1) + " " + stageNames.get(stagePosition);
    }

    if (firstSeriesChannels != null &&
      (stageNames == null || stageNames.size() == 0 ||
      stageNames.size() != getSeriesCount()))
    {
      if (name.length() > 0) {
        name += "; ";
      }
      for (int c=0; c<firstSeriesChannels.length; c++) {
        if (firstSeriesChannels[c] == ((i % 2) == 0) && c < waveNames.size()) {
          name += waveNames.get(c) + "/";
        }
      }
      if (name.length() > 0) {
        name = name.substring(0, name.length() - 1);
      }
    }
    return name;
  }

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
      if (in.getFilePointer() + 8 > in.length()) break;
      zDistances[i] = readRational(in).doubleValue();
      addSeriesMeta("zDistance[" + iAsString + "]", zDistances[i]);

      if (zDistances[i] != 0.0) core.get(0).sizeZ++;

      cDate = decodeDate(in.readInt());
      cTime = decodeTime(in.readInt());

      internalStamps[i] = DateTools.getTime(cDate + " " + cTime,
        LONG_DATE_FORMAT, ":");

      addSeriesMeta("creationDate[" + iAsString + "]", cDate);
      addSeriesMeta("creationTime[" + iAsString + "]", cTime);
      // modification date and time are skipped as they all seem equal to 0...?
      in.skip(8);
    }
    if (getSizeZ() == 0) core.get(0).sizeZ = 1;

    in.seek(saveLoc);
  }

  /**
   * UIC4 metadata parser
   *
   * UIC4 Table contains per-plane blocks of metadata
   * stage X/Y positions,
   * camera chip offsets,
   * stage labels...
   * @param uic4offset offset of UIC4 table (not tiff-compliant)
   * @throws IOException
   */
  private void parseUIC4Tags(long uic4offset) throws IOException {
    long saveLoc = in.getFilePointer();
    in.seek(uic4offset);
    if (in.getFilePointer() + 2 >= in.length()) return;

    tempZ = 0d;
    validZ = false;

    short id = in.readShort();
    while (id != 0) {
      switch (id) {
        case 28:
          readStagePositions();
          hasStagePositions = true;
          break;
        case 29:
          readRationals(
            new String[] {"cameraXChipOffset", "cameraYChipOffset"});
          hasChipOffsets = true;
          break;
        case 37:
          readStageLabels();
          break;
        case 40:
          readRationals(new String[] {"UIC4 absoluteZ"});
          hasAbsoluteZ = true;
          break;
        case 41:
          readAbsoluteZValid();
          hasAbsoluteZValid = true;
          break;
        case 46:
          in.skipBytes(mmPlanes * 8); // TODO
          break;
        default:
          in.skipBytes(4);
      }
      id = in.readShort();
    }
    in.seek(saveLoc);

    if (validZ) zStart = tempZ;
  }

  private void readStagePositions() throws IOException {
    stageX = new Length[mmPlanes];
    stageY = new Length[mmPlanes];
    String pos;
    for (int i=0; i<mmPlanes; i++) {
      pos = intFormatMax(i, mmPlanes);
      final Double posX = Double.valueOf(readRational(in).doubleValue());
      final Double posY = Double.valueOf(readRational(in).doubleValue());
      stageX[i] = new Length(posX, UNITS.REFERENCEFRAME);
      stageY[i] = new Length(posY, UNITS.REFERENCEFRAME);
      addSeriesMeta("stageX[" + pos + "]", posX);
      addSeriesMeta("stageY[" + pos + "]", posY);
      addGlobalMeta("X position for position #" + (getSeries() + 1), posX);
      addGlobalMeta("Y position for position #" + (getSeries() + 1), posY);
    }
  }

  private void readRationals(String[] labels) throws IOException {
    String pos;
    Set<Double> uniqueZ = new HashSet<Double>();
    for (int i=0; i<mmPlanes; i++) {
      pos = intFormatMax(i, mmPlanes);
      for (int q=0; q<labels.length; q++) {
        double v = readRational(in).doubleValue();
        if (labels[q].endsWith("absoluteZ")) {
          if (i == 0) {
            tempZ = v;
          }
          uniqueZ.add(v);
        }
        addSeriesMeta(labels[q] + "[" + pos + "]", v);
      }
    }
    if (uniqueZ.size() == mmPlanes) {
      core.get(0).sizeZ = mmPlanes;
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
      int valid = in.readInt();
      addSeriesMeta("absoluteZValid[" + intFormatMax(i, mmPlanes) + "]", valid);
      if (i == 0) {
        validZ = valid == 1;
      }
    }
  }

  /**
   * UIC1 entry parser
   * @throws IOException
   * @param uic1offset offset as found in the tiff tag 33628 (UIC1Tag)
   * @param uic1count number of entries in UIC1 table (not tiff-compliant)
   */
  private void parseUIC1Tags(long uic1offset, int uic1count) throws IOException
  {
    // Loop through and parse out each field. A field whose
    // code is "0" represents the end of the fields so we'll stop
    // when we reach that; much like a NULL terminated C string.
    long saveLoc = in.getFilePointer();
    in.seek(uic1offset);
    int currentID;
    long valOrOffset;
    // variable declarations, because switch is dumb
    int num, denom;
    String thedate, thetime;
    long lastOffset;

    tempZ = 0d;
    validZ = false;
    for (int i=0; i<uic1count; i++) {
      if (in.getFilePointer() >= in.length()) break;
      currentID = in.readInt();
      valOrOffset = in.readInt() & 0xffffffffL;
      lastOffset = in.getFilePointer();

      String key = getKey(currentID);
      Object value = String.valueOf(valOrOffset);

      boolean skipKey = false;
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
          if (valOrOffset < in.length()) {
            in.seek(valOrOffset);
            num = in.readInt();
            if (num + in.getFilePointer() >= in.length()) {
              num = (int) (in.length() - in.getFilePointer() - 1);
            }
            if (num >= 0) {
              value = in.readString(num);
            }
          }
          break;
        case 7:
          if (valOrOffset < in.length()) {
            in.seek(valOrOffset);
            num = in.readInt();
            if (num >= 0) {
              imageName = in.readString(num);
              value = imageName;
            }
          }
          break;
        case 8:
          if (valOrOffset == 1) value = "inside";
          else if (valOrOffset == 2) value = "outside";
          else value = "off";
          break;
        case 17: // oh how we hate you Julian format...
          if (valOrOffset < in.length()) {
            in.seek(valOrOffset);
            thedate = decodeDate(in.readInt());
            thetime = decodeTime(in.readInt());
            imageCreationDate = thedate + " " + thetime;
            value = imageCreationDate;
          }
          break;
        case 16:
          if (valOrOffset < in.length()) {
            in.seek(valOrOffset);
            thedate = decodeDate(in.readInt());
            thetime = decodeTime(in.readInt());
            value = thedate + " " + thetime;
          }
          break;
        case 26:
          if (valOrOffset < in.length()) {
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
          }
          break;
        case 28:
          if (valOrOffset < in.length()) {
            if (!hasStagePositions) {
              in.seek(valOrOffset);
              readStagePositions();
            }
            skipKey = true;
          }
          break;
        case 29:
          if (valOrOffset < in.length()) {
            if (!hasChipOffsets) {
              in.seek(valOrOffset);
              readRationals(
                new String[] {"cameraXChipOffset", "cameraYChipOffset"});
            }
            skipKey = true;
          }
          break;
        case 34:
          value = String.valueOf(in.readInt());
          break;
        case 42:
          if (valOrOffset < in.length()) {
            in.seek(valOrOffset);
            value = String.valueOf(in.readInt());
          }
          break;
        case 46:
          if (valOrOffset < in.length()) {
            in.seek(valOrOffset);
            int xBin = in.readInt();
            int yBin = in.readInt();
            binning = xBin + "x" + yBin;
            value = binning;
          }
          break;
        case 40:
          if (valOrOffset != 0 && valOrOffset < in.length()) {
            if (!hasAbsoluteZ) {
              in.seek(valOrOffset);
              readRationals(new String[] {"UIC1 absoluteZ"});
            }
            skipKey = true;
          }
          break;
        case 41:
          if (valOrOffset != 0 && valOrOffset < in.length()) {
            if (!hasAbsoluteZValid) {
              in.seek(valOrOffset);
              readAbsoluteZValid();
            }
            skipKey = true;
          }
          else if (valOrOffset == 0 && getSizeZ() < mmPlanes) {
            core.get(0).sizeZ = 1;
          }
          break;
        case 49:
          if (valOrOffset < in.length()) {
            in.seek(valOrOffset);
            readPlaneData();
            skipKey = true;
          }
          break;
      }
      if (!skipKey) {
        addSeriesMeta(key, value);
      }
      in.seek(lastOffset);

      if ("Zoom".equals(key) && value != null) {
        zoom = Double.parseDouble(value.toString());
      }
      if ("XCalibration".equals(key) && value != null) {
        if (value instanceof TiffRational) {
          sizeX = ((TiffRational) value).doubleValue();
        }
        else sizeX = new Double(value.toString());
      }
      if ("YCalibration".equals(key) && value != null) {
        if (value instanceof TiffRational) {
          sizeY = ((TiffRational) value).doubleValue();
        }
        else sizeY = new Double(value.toString());
      }
    }
    in.seek(saveLoc);

    if (validZ) zStart = tempZ;
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
    DateTime tm = new DateTime(millis, DateTimeZone.UTC);
    String hours = intFormat(tm.getHourOfDay(), 2);
    String minutes = intFormat(tm.getMinuteOfHour(), 2);
    String seconds = intFormat(tm.getSecondOfMinute(), 2);
    String ms = intFormat(tm.getMillisOfSecond(), 3);

    return hours + ":" + minutes + ":" + seconds + ":" + ms;
  }

  /** Formats an integer value with leading 0s if needed. */
  public static String intFormat(int myint, int digits) {
    return String.format("%0" + digits + "d", myint);
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

  /**
   * Locates the first valid file in the STK arrays.
   * @return Path to the first valid file.
   */
  private String locateFirstValidFile() {
    for (int q = 0; q < stks.length; q++) {
      for (int f = 0; f < stks.length; f++) {
        if (stks[q][f] != null) {
          return stks[q][f];
        }
      }
    }
    return null;
  }

  private TiffRational readRational(RandomAccessInputStream s)
    throws IOException
  {
    return readRational(s, s.getFilePointer());
  }

  private TiffRational readRational(RandomAccessInputStream s, long offset)
    throws IOException
  {
    if (offset >= s.length() - 8) {
      return null;
    }
    s.seek(offset);
    int num = s.readInt();
    int denom = s.readInt();
    return new TiffRational(num, denom);
  }

  private void setCanLookForND(boolean v) {
    FormatTools.assertId(currentId, false, 1);
    canLookForND = v;
  }

  private void readPlaneData() throws IOException {
    in.skipBytes(4);
    int keyLength = in.read();
    String key = in.readString(keyLength);
    in.skipBytes(4);

    int type = in.read();
    int index = 0;

    switch (type) {
      case 1:
        while (getGlobalMeta("Channel #" + index + " " + key) != null) {
          index++;
        }
        addGlobalMeta("Channel #" + index + " " + key, readRational(in).doubleValue());
        break;
      case 2:
        int valueLength = in.read();
        String value = in.readString(valueLength);
        if (valueLength == 0) {
          in.skipBytes(4);
          valueLength = in.read();
          value = in.readString(valueLength);
        }
        while (getGlobalMeta("Channel #" + index + " " + key) != null) {
          index++;
        }
        addGlobalMeta("Channel #" + index + " " + key, value);

        if (key.equals("_IllumSetting_")) {
          if (waveNames == null) waveNames = new Vector<String>();
          waveNames.add(value);
        }
        break;
    }
  }

  private String getKey(int id) {
    switch (id) {
      case 0: return "AutoScale";
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
      case 22: return "grayY";
      case 23: return "grayMin";
      case 24: return "grayMax";
      case 25: return "grayUnitName";
      case 26: return "StandardLUT";
      case 27: return "Wavelength";
      case 28: return "StagePosition";
      case 29: return "CameraChipOffset";
      case 30: return "OverlayMask";
      case 31: return "OverlayCompress";
      case 32: return "Overlay";
      case 33: return "SpecialOverlayMask";
      case 34: return "SpecialOverlayCompress";
      case 35: return "SpecialOverlay";
      case 36: return "ImageProperty";
      case 38: return "AutoScaleLoInfo";
      case 39: return "AutoScaleHiInfo";
      case 40: return "AbsoluteZ";
      case 41: return "AbsoluteZValid";
      case 42: return "Gamma";
      case 43: return "GammaRed";
      case 44: return "GammaGreen";
      case 45: return "GammaBlue";
      case 46: return "CameraBin";
      case 47: return "NewLUT";
      case 48: return "ImagePropertyEx";
      case 49: return "PlaneProperty";
      case 50: return "UserLutTable";
      case 51: return "RedAutoScaleInfo";
      case 52: return "RedAutoScaleLoInfo";
      case 53: return "RedAutoScaleHiInfo";
      case 54: return "RedMinScaleInfo";
      case 55: return "RedMaxScaleInfo";
      case 56: return "GreenAutoScaleInfo";
      case 57: return "GreenAutoScaleLoInfo";
      case 58: return "GreenAutoScaleHiInfo";
      case 59: return "GreenMinScaleInfo";
      case 60: return "GreenMaxScaleInfo";
      case 61: return "BlueAutoScaleInfo";
      case 62: return "BlueAutoScaleLoInfo";
      case 63: return "BlueAutoScaleHiInfo";
      case 64: return "BlueMinScaleInfo";
      case 65: return "BlueMaxScaleInfo";
      case 66: return "OverlayPlaneColor";
    }
    return null;
  }

}
