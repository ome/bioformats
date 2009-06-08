//
// FlexReader.java
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
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;

import loci.common.DataTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.TiffTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

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
public class FlexReader extends BaseTiffReader {

  // -- Constants --

  /** Custom IFD entry for Flex XML. */
  public static final int FLEX = 65200;

  // -- Fields --

  /** Scale factor for each image. */
  protected double[] factors;

  private Vector<ImageInfo> images;

  private int plateCount;
  private int wellCount;
  private int fieldCount;

  private Vector<String> channelNames;

  private Hashtable<String, Vector<Float>> xPositions;
  private Hashtable<String, Vector<Float>> yPositions;

  private Vector<String> lightSourceIDs;
  private Vector<String> subLayoutIDs;
  private Hashtable<String[], Float> power;

  // -- Constructor --

  /** Constructs a new Flex reader. */
  public FlexReader() { super("Evotec Flex", "flex"); }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);

    int ifdIndex = getSeries() * getImageCount() + no;

    int nBytes = TiffTools.getBitsPerSample(ifds[ifdIndex])[0] / 8;

    // expand pixel values with multiplication by factor[no]
    byte[] bytes = TiffTools.getSamples(ifds[ifdIndex], in, buf, x, y, w, h);

    if (getPixelType() == FormatTools.UINT8) {
      int num = bytes.length;
      for (int i=num-1; i>=0; i--) {
        int q = (int) ((bytes[i] & 0xff) * factors[ifdIndex]);
        bytes[i] = (byte) (q & 0xff);
      }
    }
    else if (getPixelType() == FormatTools.UINT16) {
      int num = bytes.length / 2;
      for (int i=num-1; i>=0; i--) {
        int q = nBytes == 1 ? (int) ((bytes[i] & 0xff) * factors[ifdIndex]) :
          (int) (DataTools.bytesToInt(bytes, i*2, 2, isLittleEndian()) *
          factors[ifdIndex]);
        DataTools.unpackBytes(q, bytes, i * 2, 2, isLittleEndian());
      }
    }
    else if (getPixelType() == FormatTools.UINT32) {
      int num = bytes.length / 4;
      for (int i=num-1; i>=0; i--) {
        int q = nBytes == 1 ? (int) ((bytes[i] & 0xff) * factors[ifdIndex]) :
          (int) (DataTools.bytesToInt(bytes, i*4, nBytes,
          isLittleEndian()) * factors[ifdIndex]);
        DataTools.unpackBytes(q, bytes, i * 4, 4, isLittleEndian());
      }
    }
    System.arraycopy(bytes, 0, buf, 0, bytes.length);
    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    factors = null;
    plateCount = wellCount = fieldCount = 0;
    channelNames = null;
  }

  // -- Internal BaseTiffReader API methods --

  /* @see loci.formats.BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    core[0].dimensionOrder = "XYCZT";
    core[0].sizeC = 0;
    core[0].sizeT = 0;
    core[0].sizeZ = 0;

    images = new Vector<ImageInfo>();
    channelNames = new Vector<String>();
    xPositions = new Hashtable<String, Vector<Float>>();
    yPositions = new Hashtable<String, Vector<Float>>();
    lightSourceIDs = new Vector<String>();
    subLayoutIDs = new Vector<String>();
    power = new Hashtable<String[], Float>();

    // parse factors from XML
    String xml = (String) TiffTools.getIFDValue(ifds[0],
      FLEX, true, String.class);

    // HACK - workaround for Windows and Mac OS X bug where
    // SAX parser fails due to improperly handled mu (181) characters.
    byte[] c = xml.getBytes();
    for (int i=0; i<c.length; i++) {
      if (c[i] > '~' || (c[i] != '\t' && c[i] < ' ')) c[i] = ' ';
    }

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    Vector<String> n = new Vector<String>();
    Vector<String> f = new Vector<String>();
    DefaultHandler handler = new FlexHandler(n, f, store);
    DataTools.parseXML(c, handler);

    core[0].sizeC = (int) Math.max(channelNames.size(), 1);

    if (getSizeZ() == 0) core[0].sizeZ = 1;
    if (getSizeT() == 0) core[0].sizeT = 1;
    if (plateCount == 0) plateCount = 1;
    if (wellCount == 0) wellCount = 1;
    if (fieldCount == 0) fieldCount = 1;

    core[0].imageCount = getSizeZ() * getSizeC() * getSizeT();
    int seriesCount = plateCount * wellCount * fieldCount;

    if (getImageCount() * seriesCount < ifds.length) {
      if (fieldCount == 1) {
      	fieldCount = ifds.length / getSizeC();
      	seriesCount = plateCount * wellCount * fieldCount;
      }
      core[0].imageCount = ifds.length / seriesCount;
      // Z-stacks are more likely than t-series (especially in Flex version 1.0)
      if (getSizeZ() <= 1) core[0].sizeZ = getImageCount() / getSizeC();
      core[0].sizeT = getImageCount() / (getSizeC() * getSizeZ());
    }
    else if (getImageCount() == ifds.length) {
      seriesCount = 1;
    }

    CoreMetadata oldCore = core[0];
    core = new CoreMetadata[seriesCount];
    core[0] = oldCore;

    for (int i=1; i<seriesCount; i++) {
      core[i] = new CoreMetadata();
      core[i].sizeX = getSizeX();
      core[i].sizeY = getSizeY();
      core[i].sizeZ = getSizeZ();
      core[i].sizeC = getSizeC();
      core[i].sizeT = getSizeT();
      core[i].imageCount = getSizeZ() * getSizeC() * getSizeT();
      core[i].dimensionOrder = getDimensionOrder();
      core[i].rgb = isRGB();
      core[i].interleaved = isInterleaved();
      core[i].indexed = isIndexed();
      core[i].littleEndian = isLittleEndian();
    }

    int totalPlanes = getSeriesCount() * getImageCount();

    // verify factor count
    int nsize = n.size();
    int fsize = f.size();
    if (nsize != fsize || nsize != totalPlanes) {
      warn("Mismatch between image count, " +
        "names and factors (count=" + totalPlanes +
        ", names=" + nsize + ", factors=" + fsize + ")");
    }
    for (int i=0; i<nsize; i++) addMeta("Name " + i, n.get(i));
    for (int i=0; i<fsize; i++) addMeta("Factor " + i, f.get(i));

    // parse factor values
    factors = new double[totalPlanes];
    int max = 0;
    for (int i=0; i<(int) Math.min(fsize, totalPlanes); i++) {
      String factor = f.get(i);
      double q = 1;
      try {
        q = Double.parseDouble(factor);
      }
      catch (NumberFormatException exc) {
        warn("Invalid factor #" + i + ": " + factor);
      }
      factors[i] = q;
      if (q > factors[max]) max = i;
    }
    if (fsize < factors.length) {
      Arrays.fill(factors, fsize, factors.length, 1);
    }

    // determine pixel type
    if (factors[max] > 256) core[0].pixelType = FormatTools.UINT32;
    else if (factors[max] > 1) core[0].pixelType = FormatTools.UINT16;
    for (int i=1; i<core.length; i++) {
      core[i].pixelType = getPixelType();
    }

    MetadataTools.populatePixels(store, this, true);
    store.setInstrumentID("Instrument:0", 0);

    int[] lengths = new int[] {plateCount, wellCount, fieldCount};

    for (int i=0; i<getSeriesCount(); i++) {
      int[] pos = FormatTools.rasterToPosition(lengths, i);

      for (int q=0; q<getImageCount(); q++) {
        if (i * getImageCount() + q >= images.size()) break;
        ImageInfo image = images.get(i * getImageCount() + q);
        store.setStagePositionPositionX(new Float(image.positionX), i, 0, q);
        store.setStagePositionPositionY(new Float(image.positionY), i, 0, q);
        store.setStagePositionPositionZ(new Float(image.positionZ), i, 0, q);
        store.setPlaneTimingDeltaT(
          new Float(image.measurementTimeOffset), i, 0, q);
        store.setPlaneTimingExposureTime(
          new Float(image.exposureTime), i, 0, q);
      }

      if (i * getImageCount() < images.size()) {
        ImageInfo image = images.get(i * getImageCount());

        store.setImageCreationDate(image.date, i);

        store.setDimensionsPhysicalSizeX(new Float(image.resolutionX), i, 0);
        store.setDimensionsPhysicalSizeY(new Float(image.resolutionY), i, 0);
        store.setObjectiveSettingsObjective("Objective:" + image.objective, i);
      }

      for (int ch=0; ch<getEffectiveSizeC(); ch++) {
        if (i * getImageCount() + ch >= images.size()) break;
        ImageInfo image = images.get(i * getImageCount() + ch);
        store.setDetectorSettingsDetector("Detector:" + image.camera, i, ch);
        store.setDetectorSettingsBinning(image.binX + "x" + image.binY, i, ch);
      }

      store.setImageID("Image:" + i, i);
      store.setImageInstrumentRef("Instrument:0", i);
      store.setWellSampleIndex(new Integer(i), pos[0], pos[1], pos[2]);
      store.setWellSampleImageRef("Image:" + i, pos[0], pos[1], pos[2]);

      if (pos[1] < subLayoutIDs.size()) {
        String sublayout = subLayoutIDs.get(pos[1]);
        Float xpos = xPositions.get(sublayout).get(pos[2]);
        Float ypos = yPositions.get(sublayout).get(pos[2]);
        store.setWellSamplePosX(xpos, pos[0], pos[1], pos[2]);
        store.setWellSamplePosY(ypos, pos[0], pos[1], pos[2]);
      }
    }
  }

  /* @see loci.formats.in.BaseTiffReader#initMetadata() */
  protected void initMetadata() throws FormatException, IOException {
    initStandardMetadata();
  }

  // -- Helper classes --

  /** SAX handler for parsing XML. */
  public class FlexHandler extends DefaultHandler {
    private Vector<String> names, factors;
    private MetadataStore store;

    private int nextLightSource = 0;
    private int nextLaser = -1;

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

    private ImageInfo currentImage;

    private Hashtable<String, Integer> fieldCounts;
    private String currentSublayout;

    private String parentQName, currentQName;
    private String sliderName;

    private String lightSourceComboID, lightSourceID;

    public FlexHandler(Vector<String> names, Vector<String> factors,
      MetadataStore store)
    {
      this.names = names;
      this.factors = factors;
      this.store = store;
      fieldCounts = new Hashtable<String, Integer>();
    }

    public void characters(char[] ch, int start, int length) {
      String value = new String(ch, start, length);

      if (currentQName.equals("SublayoutRef")) {
        fieldCount = fieldCounts.get(value).intValue();
        subLayoutIDs.add(value);
      }

      if (currentQName.equals("PlateName")) {
        store.setPlateName(value, nextPlate - 1);
        addMeta("Plate " + (nextPlate - 1) + " Name", value);
      }
      else if (parentQName.equals("Plate")) {
        addMeta("Plate " + (nextPlate - 1) + " " + currentQName, value);
      }
      else if (parentQName.equals("WellShape")) {
        addMeta("Plate " + (nextPlate - 1) + " WellShape " + currentQName,
          value);
      }
      else if (currentQName.equals("Magnification")) {
        try {
          store.setObjectiveNominalMagnification(
            new Integer((int) Float.parseFloat(value)), 0, nextObjective);
        }
        catch (NumberFormatException e) { }
      }
      else if (currentQName.equals("NumAperture")) {
        store.setObjectiveLensNA(new Float(value), 0, nextObjective);
      }
      else if (currentQName.equals("Immersion")) {
        store.setObjectiveImmersion(value, 0, nextObjective);
      }
      else if (currentQName.equals("OffsetX") || currentQName.equals("OffsetY"))
      {
        addMeta("Sublayout " + (nextSublayout - 1) + " Field " +
          (nextField - 1) + " " + currentQName, value);
        Float offset = new Float(Float.parseFloat(value) * 10000);
        if (currentQName.equals("OffsetX")) {
          Vector<Float> v = xPositions.get(currentSublayout);
          if (v == null) v = new Vector<Float>();
          v.add(offset);
          xPositions.put(currentSublayout, v);
        }
        else {
          Vector<Float> v = yPositions.get(currentSublayout);
          if (v == null) v = new Vector<Float>();
          v.add(offset);
          yPositions.put(currentSublayout, v);
        }
      }
      else if (currentQName.equals("OffsetZ")) {
        addMeta("Stack " + (nextStack - 1) + " Plane " + (nextPlane - 1) +
          " OffsetZ", value);
      }
      else if (currentQName.equals("Power")) {
        addMeta("LightSourceCombination " + (nextLightSourceCombination - 1) +
          " LightSourceRef " + (nextLightSourceRef - 1) + " Power", value);
        power.put(new String[] {lightSourceComboID, lightSourceID},
          new Float(value));
      }
      else if (currentQName.equals("CameraBinningX")) {
        try {
          currentImage.binX = Integer.parseInt(value);
        }
        catch (NumberFormatException e) { }
      }
      else if (currentQName.equals("CameraBinningY")) {
        try {
          currentImage.binY = Integer.parseInt(value);
        }
        catch (NumberFormatException e) { }
      }
      else if (currentQName.equals("ImageResolutionX")) {
        try {
          currentImage.resolutionX = Float.parseFloat(value);
        }
        catch (NumberFormatException e) { }
      }
      else if (currentQName.equals("ImageResolutionY")) {
        try {
          currentImage.resolutionY = Float.parseFloat(value);
        }
        catch (NumberFormatException e) { }
      }
      else if (currentQName.equals("ObjectiveRef")) {
        currentImage.objective = value;
      }
      else if (currentQName.equals("CameraExposureTime")) {
        try {
          currentImage.exposureTime = Float.parseFloat(value);
        }
        catch (NumberFormatException e) { }
      }
      else if (currentQName.equals("PositionX")) {
        try {
          currentImage.positionX = Float.parseFloat(value);
        }
        catch (NumberFormatException e) { }
      }
      else if (currentQName.equals("PositionY")) {
        try {
          currentImage.positionY = Float.parseFloat(value);
        }
        catch (NumberFormatException e) { }
      }
      else if (currentQName.equals("PositionZ")) {
        try {
          currentImage.positionZ = Float.parseFloat(value);
        }
        catch (NumberFormatException e) { }
      }
      else if (currentQName.equals("MeasurementTimeOffset")) {
        try {
          currentImage.measurementTimeOffset = Float.parseFloat(value);
        }
        catch (NumberFormatException e) { }
      }
      else if (currentQName.equals("DateTime")) {
        currentImage.date = value;
      }
      else if (currentQName.equals("CameraRef")) {
        currentImage.camera = value;
      }
      else if (currentQName.equals("LightSourceCombinationRef")) {
        currentImage.lightSource = value;
      }
      else if (parentQName.equals("Image")) {
        addMeta("Image " + (nextImage - 1) + " " + currentQName, value);

        if (currentQName.equals("DateTime") && nextImage == 1) {
          store.setImageCreationDate(value, 0);
        }
      }
      else if (parentQName.equals("Well")) {
        addMeta("Well " + (nextWell - 1) + " " + currentQName, value);
      }
      else if (parentQName.equals("FLIM")) {
        addMeta("FLIM " + nextImage + " " + currentQName, value);
      }
      else if (currentQName.equals("Wavelength") &&
        parentQName.equals("LightSource"))
      {
        store.setLaserWavelength(new Integer(value), 0, nextLaser);
        store.setLaserType("Unknown", 0, nextLaser);
        store.setLaserLaserMedium("Unknown", 0, nextLaser);
        addMeta("Laser " + nextLaser + " Wavelength", value);
      }
    }

    public void startElement(String uri,
      String localName, String qName, Attributes attributes)
    {
      currentQName = qName;
      if (qName.equals("Array")) {
        int len = attributes.getLength();
        for (int i=0; i<len; i++) {
          String name = attributes.getQName(i);
          if (name.equals("Name")) {
            String imageName = attributes.getValue(i);
            names.add(imageName);
            // some datasets have image names of the form "1_Exp1Cam1"
            // we're assuming that "Exp1Cam1" is the channel name, and
            // "1" is the T index

            // Actually this would be from the old flex format.
            // The 1_, 2_... represent any series,  whether they are time
            // points, Z-sections or number of fields == number of series.
            // I would be more innclined to associate that index with the
            // number of series or Z-stacks.
            imageName = imageName.substring(imageName.indexOf("_") + 1);
            if (!channelNames.contains(imageName)) {
              channelNames.add(imageName);
            }
          }
          else if (name.equals("Factor")) factors.add(attributes.getValue(i));
          else if (name.equals("Description")) {
            store.setImageDescription(attributes.getValue(i), 0);
          }
        }
      }
      else if (qName.equals("LightSource")) {
        parentQName = qName;
        String id = attributes.getValue("ID");
        String type = attributes.getValue("LightSourceType");
        addMeta("LightSource " + nextLightSource + " ID", id);
        addMeta("LightSource " + nextLightSource + " Type", type);

        store.setLightSourceID("LightSource:" + id, 0, nextLightSource);

        nextLaser++;
        nextLightSource++;
      }
      else if (qName.equals("Slider")) {
        parentQName = qName;
        addAllAttributes("Slider " + nextSlider, attributes);
        sliderName = attributes.getValue("Name");
        nextSlider++;
      }
      else if (qName.equals("Filter")) {
        addAllAttributes("Filter " + nextFilter, attributes);
        store.setFilterID("Filter:" + attributes.getValue("ID"), 0, nextFilter);
        store.setFilterFilterWheel(sliderName, 0, nextFilter);
        nextFilter++;
      }
      else if (qName.equals("Camera")) {
        parentQName = qName;
        addAllAttributes("Camera " + nextCamera, attributes);
        store.setDetectorID("Detector:" + attributes.getValue("ID"),
          0, nextCamera);
        store.setDetectorType(attributes.getValue("CameraType"), 0, nextCamera);
        nextCamera++;
      }
      else if (qName.startsWith("PixelSize") && parentQName.equals("Camera")) {
        addAllAttributes("Camera " + (nextCamera - 1) + " " + qName,
          attributes);
      }
      else if (qName.equals("Objective")) {
        parentQName = qName;
        nextObjective++;

        store.setObjectiveID("Objective:" + attributes.getValue("ID"),
          0, nextObjective);
        store.setObjectiveCorrection("Unknown", 0, nextObjective);
      }
      else if (qName.equals("Sublayout")) {
        parentQName = qName;
        nextField = 0;
        addAllAttributes("Sublayout " + nextSublayout, attributes);
        nextSublayout++;

        String id = attributes.getValue("ID");
        if (id != null) {
          currentSublayout = id;
          if (!fieldCounts.containsKey(currentSublayout)) {
            fieldCounts.put(currentSublayout, new Integer(0));
          }
        }
      }
      else if (qName.equals("Field")) {
        parentQName = qName;
        addAllAttributes("Sublayout " + (nextSublayout - 1) + " Field " +
          nextField, attributes);
        nextField++;
        int fieldNo = 0;
        try { fieldNo = Integer.parseInt(attributes.getValue("No")); }
        catch (NumberFormatException e) { }
        int count = fieldCounts.get(currentSublayout).intValue();
        if (fieldNo > count) count++;
        fieldCounts.put(currentSublayout, new Integer(count));
      }
      else if (qName.equals("Stack")) {
        nextPlane = 0;
        addAllAttributes("Stack " + nextStack, attributes);
        nextStack++;
      }
      else if (qName.equals("Plane")) {
        parentQName = qName;
        addAllAttributes("Stack " + (nextStack - 1) + " Plane " + nextPlane,
          attributes);
        nextPlane++;
        int planeNo = 0;
        try { planeNo = Integer.parseInt(attributes.getValue("No")); }
        catch (NumberFormatException e) { }
        if (planeNo > getSizeZ()) core[0].sizeZ++;
      }
      else if (qName.equals("Kinetic")) {
        addAllAttributes("Kinetic " + nextKinetic, attributes);
        nextKinetic++;
      }
      else if (qName.equals("Dispensing")) {
        addAllAttributes("Dispensin " + nextDispensing, attributes);
        nextDispensing++;
      }
      else if (qName.equals("LightSourceCombination")) {
        nextLightSourceRef = 0;
        addAllAttributes("LightSourceCombination " + nextLightSourceCombination,
          attributes);
        nextLightSourceCombination++;
        lightSourceComboID = attributes.getValue("ID");
        lightSourceIDs.add(lightSourceComboID);
      }
      else if (qName.equals("LightSourceRef")) {
        parentQName = qName;
        lightSourceID = attributes.getValue("ID");
      }
      else if (qName.equals("FilterCombination")) {
        parentQName = qName;
        nextSliderRef = 0;
        addAllAttributes("FilterCombination " + nextFilterCombination,
          attributes);
        nextFilterCombination++;
      }
      else if (qName.equals("SliderRef")) {
        parentQName = qName;
      }
      else if (qName.equals("Image")) {
        parentQName = qName;
        addAllAttributes("Image " + nextImage, attributes);

        nextImage++;

        currentImage = new ImageInfo();

        //Implemented for FLEX v1.7 and below
        String x = attributes.getValue("CameraBinningX");
        String y = attributes.getValue("CameraBinningY");
        if (x != null) {
          try {
            currentImage.binX = Integer.parseInt(x);
          }
          catch (NumberFormatException e) { }
        }
        if (y != null) {
          try {
            currentImage.binY = Integer.parseInt(y);
          }
          catch (NumberFormatException e) { }
        }
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
        int row = 0, col = 0;
        try {
          row = Integer.parseInt(attributes.getValue("Row")) - 1;
        }
        catch (NumberFormatException e) { }
        try {
          col = Integer.parseInt(attributes.getValue("Col")) - 1;
        }
        catch (NumberFormatException e) { }
        addMeta("Well " + ndx + " Row", row);
        addMeta("Well " + ndx + " Col", col);
        store.setWellRow(new Integer(row), 0, ndx);
        store.setWellColumn(new Integer(col), 0, ndx);
      }
      else if (qName.equals("Status")) {
        addMeta("Status", attributes.getValue("StatusString"));
      }
      else if (qName.equals("ImageResolutionX")) {
        parentQName = qName;
        currentImage.resolutionXUnit = attributes.getValue("Unit");
      }
      else if (qName.equals("ImageResolutionY")) {
        parentQName = qName;
        currentImage.resolutionYUnit = attributes.getValue("Unit");
      }
      else if (qName.equals("PositionX")) {
        parentQName = qName;
        currentImage.positionXUnit = attributes.getValue("Unit");
      }
      else if (qName.equals("PositionY")) {
        parentQName = qName;
        currentImage.positionYUnit = attributes.getValue("Unit");
      }
      else if (qName.equals("PositionZ")) {
        parentQName = qName;
        currentImage.positionZUnit = attributes.getValue("Unit");
      }
      else if (qName.equals("FLIM")) {
        parentQName = qName;
        addAllAttributes("FLIM " + nextImage, attributes);
      }
    }

    public void endElement(String uri, String localName, String qName) {
      if (qName.equals(parentQName)) parentQName = "";
      if (qName.equals("Image")) {
        currentImage.adjustForUnits();
        images.add(currentImage);
      }
    }

    // -- Helper methods --

    private void addAllAttributes(String keyPrefix, Attributes attributes) {
      for (int i=0; i<attributes.getLength(); i++) {
        String name = attributes.getQName(i);
        addMeta(keyPrefix + " " + name, attributes.getValue(name));
      }
    }
  }

  /** Stores metadata for a single image. */
  class ImageInfo {
    public String objective;
    public String camera;
    public float exposureTime;
    public int binX, binY;
    public float positionX, positionY, positionZ;
    public String positionXUnit, positionYUnit, positionZUnit;
    public float measurementTimeOffset;
    public String date;
    public float resolutionX, resolutionY;
    public String resolutionXUnit, resolutionYUnit;
    public String lightSource;

    public void adjustForUnits() {
      positionX = changeToMicrons(positionX, positionXUnit);
      positionY = changeToMicrons(positionY, positionYUnit);
      positionZ = changeToMicrons(positionZ, positionZUnit);
      resolutionX = changeToMicrons(resolutionX, resolutionXUnit);
      resolutionY = changeToMicrons(resolutionY, resolutionYUnit);
    }

    private float changeToMicrons(float value, String oldUnit) {
      if (oldUnit == null || oldUnit.equals("um")) return value;
      float mul = 1f;

      if (oldUnit.equals("m")) mul = 10000f;
      else if (oldUnit.equals("mm")) mul = 100f;
      else if (oldUnit.equals("cm")) mul = 1000f;

      return value * mul;
    }

  }

}
