//
// FlexReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
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
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;
import loci.common.*;
import loci.formats.*;
import loci.formats.meta.*;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * FlexReader is a file format reader for Evotec Flex files.
 * To use it, the LuraWave decoder library, lwf_jsdk2.6.jar, must be available,
 * and a LuraWave license key must be specified in the lurawave.license system
 * property (e.g., <code>-Dlurawave.license=XXXX</code> on the command line).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/FlexReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/FlexReader.java">SVN</a></dd></dl>
 */
public class FlexReader extends FormatReader {

  // -- Constants --

  /** Custom IFD entry for Flex XML. */
  protected static final int FLEX = 65200;

  // -- Fields --

  /** Scale factor for each image. */
  protected double[][][] factors;

  /** Camera binning values. */
  private int binX, binY;

  private int plateCount;
  private int wellCount;
  private int fieldCount;

  private Vector<String> channelNames;
  private Vector<Float> xPositions, yPositions;

  private Vector<String> measurementFiles;

  /**
   * List of .flex files belonging to this dataset.
   * Indices into the array are the well row and well column.
   */
  private String[][] flexFiles;

  private Hashtable[][][] ifds;

  /** Specifies the row and column index into 'flexFiles' for a given well. */
  private int[][] wellNumber;

  // -- Constructor --

  /** Constructs a new Flex reader. */
  public FlexReader() { super("Evotec Flex", "flex"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream in) throws IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    Vector<String> files = new Vector<String>();
    for (int i=0; i<flexFiles.length; i++) {
      for (int j=0; j<flexFiles[i].length; j++) {
        if (flexFiles[i][j] != null) files.add(flexFiles[i][j]);
      }
    }
    for (String file : measurementFiles) {
      files.add(file);
    }
    return files.toArray(new String[0]);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int[] lengths = new int[] {fieldCount, wellCount, plateCount};
    int[] pos = FormatTools.rasterToPosition(lengths, getSeries());

    int imageNumber = getImageCount() * pos[0] + no;

    int wellRow = wellNumber[pos[1]][0];
    int wellCol = wellNumber[pos[1]][1];

    Hashtable ifd = ifds[wellRow][wellCol][imageNumber];
    RandomAccessInputStream s =
      new RandomAccessInputStream(flexFiles[wellRow][wellCol]);

    int nBytes = TiffTools.getBitsPerSample(ifd)[0] / 8;

    // expand pixel values with multiplication by factor[no]
    byte[] bytes = TiffTools.getSamples(ifd, s, buf, x, y, w, h);
    s.close();

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int num = bytes.length / bpp;

    for (int i=num-1; i>=0; i--) {
      int q = nBytes == 1 ? bytes[i] & 0xff :
        DataTools.bytesToInt(bytes, i * bpp, bpp, isLittleEndian());
      q = (int) (q * factors[wellRow][wellCol][imageNumber]);
      DataTools.unpackBytes(q, buf, i * bpp, bpp, isLittleEndian());
    }

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    factors = null;
    binX = binY = 0;
    plateCount = wellCount = fieldCount = 0;
    channelNames = null;
    measurementFiles = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("FlexReader.initFile(" + id + ")");
    super.initFile(id);

    measurementFiles = new Vector<String>();

    boolean doGrouping = true;

    Location currentFile = new Location(id).getAbsoluteFile();

    int nRows = 0, nCols = 0;
    Hashtable<String, String> v = new Hashtable<String, String>();

    try {
      String name = currentFile.getName();
      int wellRow = Integer.parseInt(name.substring(0, 3));
      int wellCol = Integer.parseInt(name.substring(3, 6));
      if (wellRow > nRows) nRows = wellRow;
      if (wellCol > nCols) nCols = wellCol;
      v.put(wellRow + "," + wellCol, currentFile.getAbsolutePath());
    }
    catch (NumberFormatException e) {
      if (debug) trace(e);
      doGrouping = false;
    }

    if (!isGroupFiles()) doGrouping = false;
    if (isGroupFiles()) findMeasurementFiles(currentFile);

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    if (doGrouping) {
      // group together .flex files that are in the same directory

      Location dir = currentFile.getParentFile();
      String[] files = dir.list();

      for (String file : files) {
        // file names should be nnnnnnnnn.flex, where 'n' is 0-9
        if (file.endsWith(".flex") && file.length() == 14 && !id.endsWith(file))
        {
          int wellRow = Integer.parseInt(file.substring(0, 3));
          int wellCol = Integer.parseInt(file.substring(3, 6));
          if (wellRow > nRows) nRows = wellRow;
          if (wellCol > nCols) nCols = wellCol;
          String path = new Location(dir, file).getAbsolutePath();
          v.put(wellRow + "," + wellCol, path);
        }
        else if (!file.startsWith(".") && !id.endsWith(file)) {
          doGrouping = false;
          break;
        }
      }

      if (doGrouping) {
        flexFiles = new String[nRows][nCols];
        ifds = new Hashtable[nRows][nCols][];
        factors = new double[nRows][nCols][];
        wellCount = v.size();

        wellNumber = new int[wellCount][2];

        RandomAccessInputStream s = null;

        boolean firstFile = true;

        int nextWell = 0;
        for (int row=0; row<flexFiles.length; row++) {
          for (int col=0; col<flexFiles[row].length; col++) {
            flexFiles[row][col] = v.get((row + 1) + "," + (col + 1));
            if (flexFiles[row][col] == null) continue;
            wellNumber[nextWell][0] = row;
            wellNumber[nextWell++][1] = col;
            s = new RandomAccessInputStream(flexFiles[row][col]);
            ifds[row][col] = TiffTools.getIFDs(s);
            s.close();

            parseFlexFile(row, col, firstFile, store);
            if (firstFile) firstFile = false;
          }
        }
      }
    }

    if (!doGrouping) {
      wellCount = 1;
      flexFiles = new String[1][1];
      ifds = new Hashtable[1][1][];
      factors = new double[1][1][];
      wellNumber = new int[][] {{0, 0}};

      flexFiles[0][0] = currentFile.getAbsolutePath();
      RandomAccessInputStream s = new RandomAccessInputStream(flexFiles[0][0]);
      ifds[0][0] = TiffTools.getIFDs(s);
      s.close();

      parseFlexFile(0, 0, true, store);
    }

    wellCount = v.size() == 0 ? 1 : v.size();

    MetadataTools.populatePixels(store, this);
    store.setInstrumentID("Instrument:0", 0);

    int[] lengths = new int[] {fieldCount, wellCount, plateCount};

    for (int i=0; i<getSeriesCount(); i++) {
      int[] pos = FormatTools.rasterToPosition(lengths, i);

      store.setImageID("Image:" + i, i);
      store.setImageInstrumentRef("Instrument:0", i);
      store.setImageName("Well Row #" + wellNumber[pos[1]][0] + ", Column #" +
        wellNumber[pos[1]][1] + "; Field #" + pos[0], i);

      store.setWellSampleIndex(new Integer(i), pos[2], pos[1], pos[0]);
      store.setWellSampleImageRef("Image:" + i, pos[2], pos[1], pos[0]);
      if (pos[0] < xPositions.size()) {
        store.setWellSamplePosX(xPositions.get(pos[0]), pos[2], pos[1], pos[0]);
      }
      if (pos[0] < yPositions.size()) {
        store.setWellSamplePosY(yPositions.get(pos[0]), pos[2], pos[1], pos[0]);
      }

      store.setWellRow(new Integer(wellNumber[pos[1]][0]), pos[2], pos[1]);
      store.setWellColumn(new Integer(wellNumber[pos[1]][1]), pos[2], pos[1]);
    }
  }

  // -- Helper methods --

  /**
   * Parses XML metadata from the Flex file corresponding to the given well.
   * If the 'firstFile' flag is set, then the core metadata is also
   * populated.
   */
  private void parseFlexFile(int wellRow, int wellCol, boolean firstFile,
    MetadataStore store)
    throws FormatException, IOException
  {
    if (flexFiles[wellRow][wellCol] == null) return;

    if (channelNames == null) channelNames = new Vector<String>();
    if (xPositions == null) xPositions = new Vector<Float>();
    if (yPositions == null) yPositions = new Vector<Float>();

    // parse factors from XML
    String xml =
      (String) TiffTools.getIFDValue(ifds[wellRow][wellCol][0], FLEX);

    // HACK - workaround for Windows and Mac OS X bug where
    // SAX parser fails due to improperly handled mu (181) characters.
    byte[] c = xml.getBytes();
    for (int i=0; i<c.length; i++) {
      if (c[i] > '~' || (c[i] != '\t' && c[i] < ' ')) c[i] = ' ';
    }

    Vector n = new Vector();
    Vector f = new Vector();
    DefaultHandler handler = new FlexHandler(n, f, store, firstFile);
    DataTools.parseXML(c, handler);

    if (firstFile) populateCoreMetadata(wellRow, wellCol, n);

    int totalPlanes = getSeriesCount() * getImageCount();

    // verify factor count
    int nsize = n.size();
    int fsize = f.size();
    if (debug && (nsize != fsize || nsize != totalPlanes)) {
      LogTools.println("Warning: mismatch between image count, " +
        "names and factors (count=" + totalPlanes +
        ", names=" + nsize + ", factors=" + fsize + ")");
    }
    for (int i=0; i<nsize; i++) addGlobalMeta("Name " + i, n.get(i));
    for (int i=0; i<fsize; i++) addGlobalMeta("Factor " + i, f.get(i));

    // parse factor values
    factors[wellRow][wellCol] = new double[totalPlanes];
    int max = 0;
    for (int i=0; i<fsize; i++) {
      String factor = (String) f.get(i);
      double q = 1;
      try {
        q = Double.parseDouble(factor);
      }
      catch (NumberFormatException exc) {
        if (debug) {
          LogTools.println("Warning: invalid factor #" + i + ": " + factor);
        }
      }
      factors[wellRow][wellCol][i] = q;
      if (q > factors[wellRow][wellCol][max]) max = i;
    }
    Arrays.fill(factors[wellRow][wellCol], fsize,
      factors[wellRow][wellCol].length, 1);

    // determine pixel type
    if (factors[wellRow][wellCol][max] > 256) {
      core[0].pixelType = FormatTools.UINT32;
    }
    else if (factors[wellRow][wellCol][max] > 1) {
      core[0].pixelType = FormatTools.UINT16;
    }
    for (int i=1; i<core.length; i++) {
      core[i].pixelType = getPixelType();
    }
  }

  /** Populate core metadata using the given list of image names. */
  private void populateCoreMetadata(int wellRow, int wellCol,
    Vector<String> imageNames)
    throws FormatException
  {
    if (getSizeZ() == 0 && getSizeC() == 0 && getSizeT() == 0) {
      Vector<String> uniqueChannels = new Vector<String>();
      for (int i=0; i<imageNames.size(); i++) {
        String name = imageNames.get(i);
        String[] tokens = name.split("_");
        if (tokens.length > 1) {
          // fields are indexed from 1
          int fieldIndex = Integer.parseInt(tokens[0]);
          if (fieldIndex > fieldCount) fieldCount = fieldIndex;
        }
        String channel = tokens[tokens.length - 1];
        if (!uniqueChannels.contains(channel)) uniqueChannels.add(channel);
      }
      if (fieldCount == 0) fieldCount = 1;
      core[0].sizeC = (int) Math.max(uniqueChannels.size(), 1);
      core[0].sizeZ = 1;
      core[0].sizeT = imageNames.size() / (fieldCount * getSizeC());
    }

    if (getSizeC() == 0) {
      core[0].sizeC = (int) Math.max(channelNames.size(), 1);
    }

    if (getSizeZ() == 0) core[0].sizeZ = 1;
    if (getSizeT() == 0) core[0].sizeT = 1;
    if (plateCount == 0) plateCount = 1;
    if (wellCount == 0) wellCount = 1;
    if (fieldCount == 0) fieldCount = 1;

    core[0].imageCount = getSizeZ() * getSizeC() * getSizeT();
    int seriesCount = plateCount * wellCount * fieldCount;

    if (getImageCount() * seriesCount < ifds[wellRow][wellCol].length) {
      core[0].imageCount = ifds[wellRow][wellCol].length / seriesCount;
      core[0].sizeZ = 1;
      core[0].sizeC = 1;
      core[0].sizeT = ifds[wellRow][wellCol].length / seriesCount;
    }

    Hashtable ifd = ifds[wellRow][wellCol][0];

    core[0].sizeX = (int) TiffTools.getImageWidth(ifd);
    core[0].sizeY = (int) TiffTools.getImageLength(ifd);
    core[0].dimensionOrder = "XYCZT";
    core[0].rgb = false;
    core[0].interleaved = false;
    core[0].indexed = false;
    core[0].littleEndian = TiffTools.isLittleEndian(ifd);

    if (seriesCount > 1) {
      CoreMetadata oldCore = core[0];
      core = new CoreMetadata[seriesCount];
      Arrays.fill(core, oldCore);
    }
  }

  /**
   * Search for measurement files (.mea, .res) that correspond to the
   * given Flex file.
   */
  private void findMeasurementFiles(Location flexFile) throws IOException {
    // we're assuming that the directory structure looks something like this:
    //
    //                        top level directory
    //                         /              \
    //           top level flex dir       top level measurement dir
    //              /     |    \                 /       |     \
    //        plate #0   ...   plate #n     plate #0    ...    plate #n
    //       /   |  \                        /   \
    //    .flex ... .flex                 .mea   .res
    //
    // or that the .mea and .res are in the same directory as the .flex files

    Location plateDir = flexFile.getParentFile();
    String[] files = plateDir.list();

    for (String file : files) {
      String lfile = file.toLowerCase();
      if (lfile.endsWith(".mea") || lfile.endsWith(".res")) {
        measurementFiles.add(new Location(plateDir, file).getAbsolutePath());
      }
    }
    if (measurementFiles.size() > 0) return;

    Location flexDir = plateDir.getParentFile();
    Location topDir = flexDir.getParentFile();
    Location measurementDir = null;

    String[] topDirList = topDir.list();
    for (String file : topDirList) {
      if (!flexDir.getAbsolutePath().endsWith(file)) {
        measurementDir = new Location(topDir, file);
        break;
      }
    }

    if (measurementDir == null) return;

    String[] measurementPlates = measurementDir.list();
    String plateName = plateDir.getName();
    plateDir = null;
    for (String file : measurementPlates) {
      if (file.indexOf(plateName) != -1 || plateName.indexOf(file) != -1) {
        plateDir = new Location(measurementDir, file);
        break;
      }
    }

    if (plateDir == null) return;

    files = plateDir.list();
    for (String file : files) {
      measurementFiles.add(new Location(plateDir, file).getAbsolutePath());
    }
  }

  // -- Helper classes --

  /** SAX handler for parsing XML. */
  public class FlexHandler extends DefaultHandler {
    private Vector names, factors;
    private MetadataStore store;

    private int nextLightSource = 0;
    private int nextLaser = -1;

    private int nextArrayImage = 0;
    private int nextSlider = 0;
    private int nextFilter = 0;
    private int nextCamera = 0;
    private int nextObjective = -1;
    private int nextSublayout = 0;
    private int nextField = 0;
    private int nextStack = 0;
    private int nextPlane = 0;
    private int nextKinetic = 0;
    private int nextDispensing = 0;
    private int nextImage = 0;
    private int nextLightSourceCombination = 0;
    private int nextLightSourceRef = 0;
    private int nextPlate = 0;
    private int nextWell = 0;
    private int nextSliderRef = 0;
    private int nextFilterCombination = 0;

    private String parentQName;
    private String currentQName;

    private boolean populateCore = true;

    public FlexHandler(Vector names, Vector factors, MetadataStore store,
      boolean populateCore)
    {
      this.names = names;
      this.factors = factors;
      this.store = store;
      this.populateCore = populateCore;
    }

    public void characters(char[] ch, int start, int length) {
      String value = new String(ch, start, length);
      if (currentQName.equals("PlateName")) {
        store.setPlateName(value, nextPlate - 1);
        addGlobalMeta("Plate " + (nextPlate - 1) + " Name", value);
      }
      else if (parentQName.equals("Plate")) {
        addGlobalMeta("Plate " + (nextPlate - 1) + " " + currentQName, value);
      }
      else if (parentQName.equals("WellShape")) {
        addGlobalMeta("Plate " + (nextPlate - 1) + " WellShape " + currentQName,
          value);
      }
      else if (currentQName.equals("Wavelength")) {
        store.setLaserWavelength(new Integer(value), 0, nextLaser);
        addGlobalMeta("Laser " + nextLaser + " Wavelength", value);
      }
      else if (currentQName.equals("Magnification")) {
        store.setObjectiveCalibratedMagnification(new Float(value), 0,
          nextObjective);
      }
      else if (currentQName.equals("NumAperture")) {
        store.setObjectiveLensNA(new Float(value), 0, nextObjective);
      }
      else if (currentQName.equals("Immersion")) {
        store.setObjectiveImmersion(value, 0, nextObjective);
      }
      else if (currentQName.equals("OffsetX") || currentQName.equals("OffsetY"))
      {
        addGlobalMeta("Sublayout " + (nextSublayout - 1) + " Field " +
          (nextField - 1) + " " + currentQName, value);
        Float offset = new Float(value);
        if (currentQName.equals("OffsetX")) xPositions.add(offset);
        else yPositions.add(offset);
      }
      else if (currentQName.equals("OffsetZ")) {
        addGlobalMeta("Stack " + (nextStack - 1) + " Plane " + (nextPlane - 1) +
          " OffsetZ", value);
      }
      else if (currentQName.equals("Power")) {
        addGlobalMeta("LightSourceCombination " + (nextLightSourceCombination - 1) +
          " LightSourceRef " + (nextLightSourceRef - 1) + " Power", value);
      }
      else if (parentQName.equals("Image")) {
        addGlobalMeta("Image " + (nextImage - 1) + " " + currentQName, value);

        if (currentQName.equals("DateTime") && nextImage == 1) {
          store.setImageCreationDate(value, 0);
        }
        else if (currentQName.equals("CameraBinningX")) {
          binX = Integer.parseInt(value);
        }
        else if (currentQName.equals("CameraBinningY")) {
          binY = Integer.parseInt(value);
        }
        else if (currentQName.equals("LightSourceCombinationRef")) {
          if (!channelNames.contains(value)) channelNames.add(value);
        }
      }
      else if (parentQName.equals("ImageResolutionX")) {
        store.setDimensionsPhysicalSizeX(new Float(value), nextImage - 1, 0);
      }
      else if (parentQName.equals("ImageResolutionY")) {
        store.setDimensionsPhysicalSizeY(new Float(value), nextImage - 1, 0);
      }
      else if (parentQName.equals("Well")) {
        addGlobalMeta("Well " + (nextWell - 1) + " " + currentQName, value);
      }
    }

    public void startElement(String uri,
      String localName, String qName, Attributes attributes)
    {
      currentQName = qName;
      if (qName.equals("Array")) {
        int len = attributes.getLength();
        for (int i=0; i<len; i++, nextArrayImage++) {
          String name = attributes.getQName(i);
          if (name.equals("Name")) {
            names.add(attributes.getValue(i));
            //if (nextArrayImage == 0) {
            //  store.setImageName(attributes.getValue(i), 0);
            //}
          }
          else if (name.equals("Factor")) factors.add(attributes.getValue(i));
          //else if (name.equals("Description") && nextArrayImage == 0) {
          //  store.setImageDescription(attributes.getValue(i), 0);
          //}
        }
      }
      else if (qName.equals("LightSource")) {
        parentQName = qName;
        String id = attributes.getValue("ID");
        String type = attributes.getValue("LightSourceType");
        addGlobalMeta("LightSource " + nextLightSource + " ID", id);
        addGlobalMeta("LightSource " + nextLightSource + " Type", type);

        if (type.equals("Laser")) nextLaser++;
        nextLightSource++;
      }
      else if (qName.equals("Slider")) {
        parentQName = qName;
        for (int i=0; i<attributes.getLength(); i++) {
          addGlobalMeta("Slider " + nextSlider + " " + attributes.getQName(i),
            attributes.getValue(i));
        }
        nextSlider++;
      }
      else if (qName.equals("Filter")) {
        for (int i=0; i<attributes.getLength(); i++) {
          addGlobalMeta("Filter " + nextFilter + " " + attributes.getQName(i),
            attributes.getValue(i));
        }
        nextFilter++;
      }
      else if (qName.equals("Camera")) {
        parentQName = qName;
        for (int i=0; i<attributes.getLength(); i++) {
          addGlobalMeta("Camera " + nextCamera + " " + attributes.getQName(i),
            attributes.getValue(i));
        }
        nextCamera++;
      }
      else if (qName.startsWith("PixelSize") && parentQName.equals("Camera"))
      {
        for (int i=0; i<attributes.getLength(); i++) {
          addGlobalMeta("Camera " + (nextCamera - 1) + " " + qName + " " +
            attributes.getQName(i), attributes.getValue(i));
        }
      }
      else if (qName.equals("Objective")) {
        parentQName = qName;
        nextObjective++;

        // link Objective to Image using ObjectiveSettings
        store.setObjectiveID("Objective:" + nextObjective, 0, 0);
        store.setObjectiveSettingsObjective("Objective:" + nextObjective, 0);
        store.setObjectiveCorrection("Unknown", 0, 0);
      }
      else if (qName.equals("Sublayout")) {
        parentQName = qName;
        nextField = 0;
        for (int i=0; i<attributes.getLength(); i++) {
          addGlobalMeta("Sublayout " + nextSublayout + " " + attributes.getQName(i),
            attributes.getValue(i));
        }
        nextSublayout++;
      }
      else if (qName.equals("Field")) {
        parentQName = qName;
        for (int i=0; i<attributes.getLength(); i++) {
          addGlobalMeta("Sublayout " + (nextSublayout - 1) + " Field " + nextField +
            " " + attributes.getQName(i), attributes.getValue(i));
        }
        nextField++;
        int fieldNo = Integer.parseInt(attributes.getValue("No"));
        if (fieldNo > fieldCount) fieldCount++;
      }
      else if (qName.equals("Stack")) {
        nextPlane = 0;
        for (int i=0; i<attributes.getLength(); i++) {
          addGlobalMeta("Stack " + nextStack + " " + attributes.getQName(i),
            attributes.getValue(i));
        }
        nextStack++;
      }
      else if (qName.equals("Plane")) {
        parentQName = qName;
        for (int i=0; i<attributes.getLength(); i++) {
          addGlobalMeta("Stack " + (nextStack - 1) + " Plane " + nextPlane +
            " " + attributes.getQName(i), attributes.getValue(i));
        }
        nextPlane++;
        int planeNo = Integer.parseInt(attributes.getValue("No"));
        if (planeNo > getSizeZ() && populateCore) core[0].sizeZ++;
      }
      else if (qName.equals("Kinetic")) {
        for (int i=0; i<attributes.getLength(); i++) {
          addGlobalMeta("Kinetic " + nextKinetic + " " + attributes.getQName(i),
            attributes.getValue(i));
        }
        nextKinetic++;
      }
      else if (qName.equals("Dispensing")) {
        for (int i=0; i<attributes.getLength(); i++) {
          addGlobalMeta("Dispensing " + nextDispensing + " " + attributes.getQName(i),
            attributes.getValue(i));
        }
        nextDispensing++;
      }
      else if (qName.equals("LightSourceCombination")) {
        nextLightSourceRef = 0;
        for (int i=0; i<attributes.getLength(); i++) {
          addGlobalMeta("LightSourceCombination " + nextLightSourceCombination +
            " " + attributes.getQName(i), attributes.getValue(i));
        }
        nextLightSourceCombination++;
      }
      else if (qName.equals("LightSourceRef")) {
        parentQName = qName;
        for (int i=0; i<attributes.getLength(); i++) {
          addGlobalMeta("LightSourceCombination " + (nextLightSourceCombination - 1) +
            " LightSourceRef " + nextLightSourceRef + " " +
            attributes.getQName(i), attributes.getValue(i));
        }
        nextLightSourceRef++;
      }
      else if (qName.equals("FilterCombination")) {
        parentQName = qName;
        nextSliderRef = 0;
        for (int i=0; i<attributes.getLength(); i++) {
          addGlobalMeta("FilterCombination " + nextFilterCombination + " " +
            attributes.getQName(i), attributes.getValue(i));
        }
        nextFilterCombination++;
      }
      else if (qName.equals("SliderRef")) {
        for (int i=0; i<attributes.getLength(); i++) {
          addGlobalMeta("FilterCombination " + (nextFilterCombination - 1) +
            " SliderRef " + nextSliderRef + " " + attributes.getQName(i),
            attributes.getValue(i));
        }
        nextSliderRef++;
      }
      else if (qName.equals("Image")) {
        parentQName = qName;
        for (int i=0; i<attributes.getLength(); i++) {
          addGlobalMeta("Image " + nextImage + " " + attributes.getQName(i),
            attributes.getValue(i));
        }

        nextImage++;

        //Implemented for FLEX v1.7 and below
        String x = attributes.getValue("CameraBinningX");
        String y = attributes.getValue("CameraBinningY");
        if (x != null) binX = Integer.parseInt(x);
        if (y != null) binY = Integer.parseInt(y);
      }
      else if (qName.equals("Plate") || qName.equals("WellShape") ||
        qName.equals("Well"))
      {
        parentQName = qName;
        if (qName.equals("Plate")) {
          nextPlate++;
          plateCount++;
        }
        else if (qName.equals("Well")) {
          nextWell++;
          wellCount++;
        }
      }
      else if (qName.equals("WellCoordinate")) {
        int ndx = nextWell - 1;
        addGlobalMeta("Well" + ndx + " Row", attributes.getValue("Row"));
        addGlobalMeta("Well" + ndx + " Col", attributes.getValue("Col"));
        //store.setWellRow(new Integer(attributes.getValue("Row")), 0, ndx);
        //store.setWellColumn(new Integer(attributes.getValue("Col")), 0, ndx);
      }
      else if (qName.equals("Status")) {
        addGlobalMeta("Status", attributes.getValue("StatusString"));
      }
      else if(qName.equals("ImageResolutionX")) {
        parentQName = qName;
      //TODO: definition of the dimension type and unit Where to store?
        for (int i=0; i<attributes.getLength(); i++) {
          //addGlobalMeta("Image " + nextImage + " " + attributes.getQName(i), attributes.getValue(i));
        }
      }
      else if(qName.equals("ImageResolutionY")) {
        parentQName = qName;
        //TODO: definition of the dimension type and unit Where to store?
        for (int i=0; i<attributes.getLength(); i++) {
          //addGlobalMeta("Image " + nextImage + " " + attributes.getQName(i),attributes.getValue(i));
        }
      }
    }
  }

}
