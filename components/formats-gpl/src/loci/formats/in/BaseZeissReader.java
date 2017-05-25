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
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.lang.Math;

import loci.common.DateTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatTools;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.MetadataTools;
import loci.formats.meta.DummyMetadata;
import loci.formats.meta.MetadataStore;

import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.Timestamp;

import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.UNITS;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * BaseZeissReader contains common functionality required by
 * readers for Zeiss AxioVision formats.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public abstract class BaseZeissReader extends FormatReader {

  // -- Fields --

  /** Number of bytes per pixel. */
  protected int bpp;

  protected String[] imageFiles;
  protected int[] offsets;
  protected int[][] coordinates;

  protected transient Timestamp acquisitionDate;
  protected Map<Integer, String> timestamps, exposureTime;
  protected int cIndex = -1;
  protected boolean isJPEG, isZlib;

  protected int realWidth, realHeight;


  protected List<String> tagsToParse;
  protected int nextEmWave = 0, nextExWave = 0, nextChName = 0;
  protected final Map<Integer, Length> stageX = new HashMap<Integer, Length>();
  protected final Map<Integer, Length> stageY = new HashMap<Integer, Length>();
  protected int timepoint = 0;

  protected int[] channelColors;
  protected int lastPlane = 0;
  protected final Map<Integer, Integer> tiles =
      new HashMap<Integer, Integer>();

  protected final Map<Integer, Double> detectorGain =
      new HashMap<Integer, Double>();
  protected final Map<Integer, Double> detectorOffset =
      new HashMap<Integer, Double>();
  protected final Map<Integer, Length> emWavelength =
      new HashMap<Integer, Length>();
  protected final Map<Integer, Length> exWavelength =
      new HashMap<Integer, Length>();
  protected final Map<Integer, String> channelName =
      new HashMap<Integer, String>();
  protected Double physicalSizeX, physicalSizeY, physicalSizeZ;
  protected int rowCount, colCount, rawCount;
  protected String imageDescription;

  protected Set<Integer> channelIndices = new HashSet<Integer>();
  protected Set<Integer> zIndices = new HashSet<Integer>();
  protected Set<Integer> timepointIndices = new HashSet<Integer>();
  protected Set<Integer> tileIndices = new HashSet<Integer>();

  protected List<String> roiIDs = new ArrayList<String>();

  // Layer annotations (contain Shapes, i.e. ROIs).
  public ArrayList<Layer> layers = new ArrayList<BaseZeissReader.Layer>();


  // -- Constructor --

  /** Constructs a new ZeissZVI reader. */
  public BaseZeissReader(String name, String suffix) {
    super(name, suffix);
  }

  /** Constructs a new ZeissZVI reader. */
  public BaseZeissReader(String name, String[] suffixes) {
    super(name, suffixes);
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFileMain(String id) throws FormatException, IOException {
    MetadataStore store = makeFilterMetadata();
    initVars(id);
    fillMetadataPass1(store);
    fillMetadataPass2(store);
    fillMetadataPass3(store);
    fillMetadataPass4(store);
    fillMetadataPass5(store);
    fillMetadataPass6(store);
    MetadataTools.populatePixels(store, this, true);
    storeROIs(store);
    fillMetadataPass7(store);
  }

  protected void initVars(String id) throws FormatException, IOException {
    timestamps = new HashMap<Integer, String>();
    exposureTime = new HashMap<Integer, String>();
    tagsToParse = new ArrayList<String>();
  }

  protected void countImages() {
    if (getImageCount() == 0)
      core.get(0).imageCount = 1;
    offsets = new int[getImageCount()];
    coordinates = new int[getImageCount()][3];
    imageFiles = new String[getImageCount()];
  }

  /**
   * Read and store basic dimensions in model
   * @param store
   * @throws FormatException
   * @throws IOException
   */
  protected void fillMetadataPass1(MetadataStore store) throws FormatException, IOException {
  }

  /**
   * Read and store basic dimensions in model
   * @param store
   * @throws FormatException
   * @throws IOException
   */
  protected void fillMetadataPass2(MetadataStore store) throws FormatException, IOException {
    LOGGER.info("Populating metadata");
    stageX.clear();
    stageY.clear();

    CoreMetadata m = core.get(0);

    m.sizeZ = zIndices.size();
    m.sizeT = timepointIndices.size();
    m.sizeC = channelIndices.size();

    m.littleEndian = true;
    m.interleaved = true;
    m.falseColor = true;
    m.metadataComplete = true;

    m.imageCount = getSizeZ() * getSizeT() * getSizeC();

    if (getImageCount() == 0 || getImageCount() == 1) {
      m.imageCount = 1;
      m.sizeZ = 1;
      m.sizeC = 1;
      m.sizeT = 1;
    }
    m.rgb = (bpp % 3) == 0;
    if (isRGB()) m.sizeC *= 3;
  }

  /**
   * Read and store basic dimensions in model
   * @param store
   * @throws FormatException
   * @throws IOException
   */
  protected void fillMetadataPass3(MetadataStore store) throws FormatException, IOException {
  }

  /**
   * Read and store basic dimensions in model
   * @param store
   * @throws FormatException
   * @throws IOException
   */
  protected void fillMetadataPass4(MetadataStore store) throws FormatException, IOException {

    int totalTiles = offsets.length / getImageCount();

    if (totalTiles <= 1) {
      totalTiles = 1;
    }

    if (totalTiles > 1) {
      for (int i=1; i<totalTiles; i++) {
        core.add(new CoreMetadata(this, 0));
      }
    }

    CoreMetadata m = core.get(0);

    m.dimensionOrder = "XY";
    if (isRGB()) m.dimensionOrder += 'C';
    for (int i=0; i<coordinates.length-1; i++) {
      int[] zct1 = coordinates[i];
      int[] zct2 = coordinates[i + 1];
      int deltaZ = zct2[0] - zct1[0];
      int deltaC = zct2[1] - zct1[1];
      int deltaT = zct2[2] - zct1[2];
      if (deltaZ > 0 && getDimensionOrder().indexOf('Z') == -1) {
        m.dimensionOrder += 'Z';
      }
      if (deltaC > 0 && getDimensionOrder().indexOf('C') == -1) {
        m.dimensionOrder += 'C';
      }
      if (deltaT > 0 && getDimensionOrder().indexOf('T') == -1) {
        m.dimensionOrder += 'T';
      }
    }
    m.dimensionOrder =
        MetadataTools.makeSaneDimensionOrder(getDimensionOrder());
  }

  /**
   * Read and store basic dimensions in model
   * @param store
   * @throws FormatException
   * @throws IOException
   */
  protected void fillMetadataPass5(MetadataStore store) throws FormatException, IOException {
  }

  /**
   * Read and store basic dimensions in model
   * @param store
   * @throws FormatException
   * @throws IOException
   */
  protected void fillMetadataPass6(MetadataStore store) throws FormatException, IOException {
    CoreMetadata m = core.get(0);

    if (getSizeX() == 0) {
      m.sizeX = 1;
    }
    if (getSizeY() == 0) {
      m.sizeY = 1;
    }

    if (bpp == 1 || bpp == 3) m.pixelType = FormatTools.UINT8;
    else if (bpp == 2 || bpp == 6) m.pixelType = FormatTools.UINT16;
    if (isJPEG) m.pixelType = FormatTools.UINT8;

    m.bitsPerPixel = FormatTools.getBytesPerPixel(m.pixelType) * 8;
    m.indexed = !isRGB() && channelColors != null;

    // We shouldn't need to copy the coremetadata here.  This
    // indicates a need for better ordering of the initialization, or
    // rather not using the size of core as the series count until
    // it's filled.
    for (int i=1; i<core.size(); i++) {
      core.set(i, new CoreMetadata(core.get(0)));
    }
  }

  /**
   * Store basic dimensions in model
   * @param store
   * @throws FormatException
   * @throws IOException
   */
  protected void fillMetadataPass7(MetadataStore store) throws FormatException, IOException {
    for (int i=0; i<getSeriesCount(); i++) {
      long firstStamp = 0;
      if (timestamps.size() > 0) {
        String timestamp = timestamps.get(0);
        store.setImageAcquisitionDate(new Timestamp(timestamp), i);
      } else if (acquisitionDate != null) {
        store.setImageAcquisitionDate(acquisitionDate, i);
      }
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      // link Instrument and Image
      String instrumentID = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrumentID, 0);

      String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
      store.setObjectiveID(objectiveID, 0, 0);
      store.setObjectiveCorrection(getCorrection("Other"), 0, 0);
      store.setObjectiveImmersion(getImmersion("Other"), 0, 0);

      Integer[] channelKeys = channelName.keySet().toArray(
          new Integer[channelName.size()]);
      Arrays.sort(channelKeys);

      // link DetectorSettings to an actual Detector
      for (int i=0; i<getEffectiveSizeC(); i++) {
        String detectorID = MetadataTools.createLSID("Detector", 0, i);
        store.setDetectorID(detectorID, 0, i);
        store.setDetectorType(getDetectorType("Other"), 0, i);

        for (int s=0; s<getSeriesCount(); s++) {
          int c = i;
          if (i < channelKeys.length) {
            c = channelKeys[i];
          }

          store.setDetectorSettingsID(detectorID, s, i);
          store.setDetectorSettingsGain(detectorGain.get(c), s, i);
          store.setDetectorSettingsOffset(detectorOffset.get(c), s, i);

          store.setChannelName(channelName.get(c), s, i);
          store.setChannelEmissionWavelength(emWavelength.get(c), s, i);
          store.setChannelExcitationWavelength(exWavelength.get(c), s, i);

          if (channelColors != null && i < channelColors.length) {
            int color = channelColors[i];

            int red = color & 0xff;
            int green = (color & 0xff00) >> 8;
            int blue = (color & 0xff0000) >> 16;

            store.setChannelColor(new Color(red, green, blue, 255), s, i);
          }
        }
      }

      for (int i=0; i<getSeriesCount(); i++) {
        store.setImageInstrumentRef(instrumentID, i);
        store.setObjectiveSettingsID(objectiveID, i);

        if (imageDescription != null) {
          store.setImageDescription(imageDescription, i);
        }
        if (getSeriesCount() > 1) {
          store.setImageName("Tile #" + (i + 1), i);
        }

        Length sizeX = FormatTools.getPhysicalSizeX(physicalSizeX);
        Length sizeY = FormatTools.getPhysicalSizeY(physicalSizeY);
        Length sizeZ = FormatTools.getPhysicalSizeZ(physicalSizeZ);

        if (sizeX != null) {
          store.setPixelsPhysicalSizeX(sizeX, i);
        }
        if (sizeY != null) {
          store.setPixelsPhysicalSizeY(sizeY, i);
        }
        if (sizeZ != null) {
          store.setPixelsPhysicalSizeZ(sizeZ, i);
        }

        Timestamp firstStamp = null;
        if (timestamps.get(0) != null) {
          firstStamp = new Timestamp(timestamps.get(0));
        }

        for (int plane=0; plane<getImageCount(); plane++) {
          int[] zct = getZCTCoords(plane);
          int expIndex = zct[1];
          if (channelKeys.length > 0) {
            expIndex += channelKeys[0];
          }
          String exposure = exposureTime.get(expIndex);
          if (exposure == null && exposureTime.size() == 1) {
            exposure = exposureTime.values().iterator().next();
          }
          Double exp = 0d;
          try { exp = new Double(exposure); }
          catch (NumberFormatException e) { }
          catch (NullPointerException e) { }
          store.setPlaneExposureTime(new Time(exp, UNITS.SECOND), i, plane);

          int posIndex = i * getImageCount() + plane;

          if (posIndex < timestamps.size() && firstStamp != null) {
            Timestamp timestamp = new Timestamp(timestamps.get(posIndex));
            long difference = timestamp.asInstant().getMillis() - firstStamp.asInstant().getMillis();
            double delta = (double) difference;
            store.setPlaneDeltaT(new Time(delta, UNITS.MILLISECOND), i, plane);
          }

          if (stageX.get(posIndex) != null) {
            store.setPlanePositionX(stageX.get(posIndex), i, plane);
          }
          if (stageY.get(posIndex) != null) {
            store.setPlanePositionY(stageY.get(posIndex), i, plane);
          }
        }
      }

      for (int i=0; i<getSeriesCount(); i++) {
        for (int roi=0; roi<roiIDs.size(); roi++) {
          store.setImageROIRef(roiIDs.get(roi), i, roi);
        }
      }
    }
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return getSizeY();
  }

  /**
   * Store basic dimensions in model
   * @param store
   * @throws FormatException
   * @throws IOException
   */
  protected void storeROIs(MetadataStore store) throws FormatException, IOException {
    int roiIndex = 0;
    int shapeIndex = 0;
    String shapeID;
    StringBuilder points;

    for (Layer layer : layers) {
      for (Shape shape : layer.shapes) {
        shapeIndex = 0;
        if (shape.type == FeatureType.UNKNOWN || shape.type == FeatureType.LUT)
          continue; // Not representable at this point.

        String roiID = MetadataTools.createLSID("ROI", roiIndex);

        roiIDs.add(roiID);
        store.setROIID(roiID, roiIndex);
        if (shape.name != null)
          store.setROIName(shape.name, roiIndex);
        if (shape.text != null && shape.text.length() == 0)
          shape.text = null;

        switch (shape.type) {
          case POINT:
          case POINTS:
          case MEAS_POINT:
          case MEAS_POINTS:
            for (int i = 0; i < (shape.points.length / 2); i++) {
              shapeID = MetadataTools.createLSID("Shape", roiIndex, shapeIndex);
              store.setPointID(shapeID, roiIndex, shapeIndex);
              store.setPointX(shape.points[(i*2)], roiIndex, shapeIndex);
              store.setPointY(shape.points[(i*2)+1], roiIndex, shapeIndex);
              if (shape.text != null && i == 0)
                store.setPointText(shape.text, roiIndex, shapeIndex);
              shapeIndex++;
            }
            break;
          case LINE:
          case MEAS_LINE:
          case MEAS_PROFILE: // Uses a line as the profile path, but we can't handle that yet.
            shapeID = MetadataTools.createLSID("Shape", roiIndex, shapeIndex);
            store.setLineID(shapeID, roiIndex, shapeIndex);
            store.setLineX1(shape.points[0], roiIndex, shapeIndex);
            store.setLineY1(shape.points[1], roiIndex, shapeIndex);
            store.setLineX2(shape.points[2], roiIndex, shapeIndex);
            store.setLineY2(shape.points[3], roiIndex, shapeIndex);
            if (shape.text != null)
              store.setLineText(shape.text, roiIndex, shapeIndex);
            shapeIndex++;
            break;
          case CALIPER:
          case MEAS_CALIPER:
          case MULTIPLE_CALIPER:
          case MEAS_MULTIPLE_CALIPER:
          case DISTANCE:
          case MEAS_DISTANCE:
          case MULTIPLE_DISTANCE:
          case MEAS_MULTIPLE_DISTANCE:
            // Note that the last line is the baseline.
            // First point of each line before the last line is on the baseline,
            // second point is the actual editable point chosen by the user.
            int caliperPoints = shape.points.length / 2;
            if (shape.type == FeatureType.CALIPER || shape.type == FeatureType.MEAS_CALIPER)
              caliperPoints -= 2; // Contains third line which we don't need for caliper types.
            // This isn't true for distance types, but it's only for display, so don't store for the time being.
            // This is a discrepancy between distance (which stores it) and multiple distance (which does not) in
            // the Zeiss shape types.  Conversely, caliper stores it when it's not used for this type.
            // Here, we store a set of lines, starting at the baseline, finally followed by the baseline, for
            // all these types.
            for (int i = 0; i < caliperPoints; i++) {
              shapeID = MetadataTools.createLSID("Shape", roiIndex, shapeIndex);
              store.setLineID(shapeID, roiIndex, shapeIndex);
              store.setLineX1(shape.points[(i*2)+0], roiIndex, shapeIndex);
              store.setLineY1(shape.points[(i*2)+1], roiIndex, shapeIndex);
              store.setLineX2(shape.points[(i*2)+2], roiIndex, shapeIndex);
              store.setLineY2(shape.points[(i*2)+3], roiIndex, shapeIndex);
              if (shape.text != null && i == caliperPoints - 2) // Store label on baseline
                store.setLineText(shape.text, roiIndex, shapeIndex);
              shapeIndex++;
            }
            break;
          case ANGLE3:
          case MEAS_ANGLE3:
          case ANGLE4:
          case MEAS_ANGLE4:
            // First point of each line is the common origin (if any)
            for (int i = 0; i < 4; i+=2) {
              shapeID = MetadataTools.createLSID("Shape", roiIndex, shapeIndex);
              store.setLineID(shapeID, roiIndex, shapeIndex);
              store.setLineX1(shape.points[(i*2)+0], roiIndex, shapeIndex);
              store.setLineY1(shape.points[(i*2)+1], roiIndex, shapeIndex);
              store.setLineX2(shape.points[(i*2)+2], roiIndex, shapeIndex);
              store.setLineY2(shape.points[(i*2)+3], roiIndex, shapeIndex);
              if (shape.text != null && i == 0)
                store.setLineText(shape.text, roiIndex, shapeIndex);
              shapeIndex++;
            }
            break;
          case CIRCLE:
          case MEAS_CIRCLE:
            // Compute radius from line length.
            double xdiff = Math.abs(shape.points[0] - shape.points[2]);
            double ydiff = Math.abs(shape.points[1] - shape.points[3]);
            double radius = Math.sqrt(Math.pow(xdiff, 2.0) + Math.pow(ydiff, 2.0));
            shapeID = MetadataTools.createLSID("Shape", roiIndex, shapeIndex);
            store.setEllipseID(shapeID, roiIndex, shapeIndex);
            store.setEllipseX(shape.points[0], roiIndex, shapeIndex);
            store.setEllipseY(shape.points[1], roiIndex, shapeIndex);
            store.setEllipseRadiusX(radius, roiIndex, shapeIndex);
            store.setEllipseRadiusY(radius, roiIndex, shapeIndex);
            if (shape.text != null)
              store.setEllipseText(shape.text, roiIndex, shapeIndex);
            shapeIndex++;
            shapeID = MetadataTools.createLSID("Shape", roiIndex, shapeIndex);
            store.setLineID(shapeID, roiIndex, shapeIndex);
            store.setLineX1(shape.points[0], roiIndex, shapeIndex);
            store.setLineY1(shape.points[1], roiIndex, shapeIndex);
            store.setLineX2(shape.points[2], roiIndex, shapeIndex);
            store.setLineY2(shape.points[3], roiIndex, shapeIndex);
            shapeIndex++;
            break;
          case SCALE_BAR:
            // TODO: Set line ends.  Also set scale text.
            shapeID = MetadataTools.createLSID("Shape", roiIndex, shapeIndex);
            store.setLineID(shapeID, roiIndex, shapeIndex);
            store.setLineX1(shape.points[0], roiIndex, shapeIndex);
            store.setLineY1(shape.points[1], roiIndex, shapeIndex);
            store.setLineX2(shape.points[2], roiIndex, shapeIndex);
            store.setLineY2(shape.points[3], roiIndex, shapeIndex);
            store.setLineText(shape.text, roiIndex, shapeIndex);
            shapeIndex++;
            shapeID = MetadataTools.createLSID("Shape", roiIndex, shapeIndex);
            store.setLabelID(shapeID, roiIndex, shapeIndex);
            store.setLabelX(shape.points[0], roiIndex, shapeIndex);
            store.setLabelY(shape.points[1], roiIndex, shapeIndex);
            if (shape.text != null)
              store.setLabelText(shape.text, roiIndex, shapeIndex);
            shapeIndex++;
            // Adding a rectangle is a hack to make the length get displayed in ImageJ.  This should be removed.
            shapeID = MetadataTools.createLSID("Shape", roiIndex, shapeIndex);
            store.setRectangleID(shapeID, roiIndex, shapeIndex);
            store.setRectangleX(shape.points[0], roiIndex, shapeIndex);
            store.setRectangleY(shape.points[1], roiIndex, shapeIndex);
            store.setRectangleWidth(shape.points[2] - shape.points[0], roiIndex, shapeIndex);
            store.setRectangleHeight(shape.points[3] - shape.points[1], roiIndex, shapeIndex);
            if (shape.text != null)
              store.setRectangleText(shape.text, roiIndex, shapeIndex);
            shapeIndex++;
            break;
          case POLYLINE_OPEN:
          case MEAS_POLYLINE_OPEN:
          case POLYLINE_CLOSED:
          case MEAS_POLYLINE_CLOSED:
          case SPLINE_OPEN:
          case MEAS_SPLINE_OPEN:
          case SPLINE_CLOSED:
          case MEAS_SPLINE_CLOSED:
            // Currently splines not representable in model, so use polyline.
            points = new StringBuilder();
            for (int p=0; p < shape.points.length; p+=2) {
              points.append(shape.points[p+0]);
              points.append(",");
              points.append(shape.points[p+1]);
              if (p < (shape.points.length - 2))
                points.append(" ");
            }
            shapeID = MetadataTools.createLSID("Shape", roiIndex, shapeIndex);
            boolean closed = (shape.type == FeatureType.POLYLINE_CLOSED || shape.type == FeatureType.SPLINE_CLOSED ||
                shape.type == FeatureType.MEAS_POLYLINE_CLOSED || shape.type == FeatureType.MEAS_SPLINE_CLOSED);
            if (closed) {
              store.setPolygonID(shapeID, roiIndex, shapeIndex);
              store.setPolygonPoints(points.toString(), roiIndex, shapeIndex);
              if (shape.text != null)
                  store.setPolygonText(shape.text, roiIndex, shapeIndex);
            } else {
              store.setPolylineID(shapeID, roiIndex, shapeIndex);
              store.setPolylinePoints(points.toString(), roiIndex, shapeIndex);
              if (shape.text != null)
                  store.setPolylineText(shape.text, roiIndex, shapeIndex);
            }
            shapeIndex++;
            break;
          case ALIGNED_RECTANGLE:
          case MEAS_ALIGNED_RECTANGLE:
          case TEXT:
            // TODO: Determine top-left corner for text display?
            // We create a separate text and rectangle objects; this might need changing in the future.
            shapeID = MetadataTools.createLSID("Shape", roiIndex, shapeIndex);
            store.setLabelID(shapeID, roiIndex, shapeIndex);
            store.setLabelX(shape.points[0], roiIndex, shapeIndex);
            store.setLabelY(shape.points[1], roiIndex, shapeIndex);
            if (shape.text != null)
              store.setLabelText(shape.text, roiIndex, shapeIndex);
            shapeIndex++;
            shapeID = MetadataTools.createLSID("Shape", roiIndex, shapeIndex);
            store.setRectangleID(shapeID, roiIndex, shapeIndex);
            store.setRectangleX(shape.points[0], roiIndex, shapeIndex);
            store.setRectangleY(shape.points[1], roiIndex, shapeIndex);
            store.setRectangleWidth(shape.points[4] - shape.points[0], roiIndex, shapeIndex);
            store.setRectangleHeight(shape.points[5] - shape.points[1], roiIndex, shapeIndex);
            if (shape.text != null)
              store.setRectangleText(shape.text, roiIndex, shapeIndex);
            shapeIndex++;
            break;
          case RECTANGLE:
          case MEAS_RECTANGLE:
            points = new StringBuilder();
            for (int p=0; p < 8; p+=2) {
              points.append(shape.points[p+0]);
              points.append(",");
              points.append(shape.points[p+1]);
              if (p < 3) points.append(" ");
            }
            shapeID = MetadataTools.createLSID("Shape", roiIndex, shapeIndex);
            store.setPolygonID(shapeID, roiIndex, shapeIndex);
            store.setPolygonPoints(points.toString(), roiIndex, shapeIndex);
            if (shape.text != null)
              store.setPolygonText(shape.text, roiIndex, shapeIndex);
            shapeIndex++;
            break;
          case ELLIPSE:
          case MEAS_ELLIPSE:
            shapeID = MetadataTools.createLSID("Shape", roiIndex, shapeIndex);
            store.setEllipseID(shapeID, roiIndex, shapeIndex);
            store.setEllipseX((shape.points[0] + shape.points[4])/2, roiIndex, shapeIndex);
            store.setEllipseY((shape.points[1] + shape.points[5])/2, roiIndex, shapeIndex);
            store.setEllipseRadiusX((shape.points[4] - shape.points[0])/2.0, roiIndex, shapeIndex);
            store.setEllipseRadiusY((shape.points[5] - shape.points[1])/2.0, roiIndex, shapeIndex);
            if (shape.text != null)
              store.setEllipseText(shape.text, roiIndex, shapeIndex);
            shapeIndex++;
            break;
          case LENGTH:
          case MEAS_LENGTH:
            // Three lines.  First is the length, second and third are guide lines ending at the first line.
            for (int i = 0; i < 6; i+=2) {
              shapeID = MetadataTools.createLSID("Shape", roiIndex, shapeIndex);
              store.setLineID(shapeID, roiIndex, shapeIndex);
              store.setLineX1(shape.points[(i*2)+0], roiIndex, shapeIndex);
              store.setLineY1(shape.points[(i*2)+1], roiIndex, shapeIndex);
              store.setLineX2(shape.points[(i*2)+2], roiIndex, shapeIndex);
              store.setLineY2(shape.points[(i*2)+3], roiIndex, shapeIndex);
              if (shape.text != null && i == 0)
                store.setLineText(shape.text, roiIndex, shapeIndex);
              shapeIndex++;
            }
            break;
            // case LUT:
            // The lookup table shape is a rectangle gradient.  We could generate this
            // as a series of 256 coloured rectangles with some labels.
            //    break;
        default:
          break;
        }
        roiIndex++;
      }
    }
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    int pixelType = getPixelType();
    if ((pixelType != FormatTools.INT8 && pixelType != FormatTools.UINT8) ||
        !isIndexed())
    {
      return null;
    }
    byte[][] lut = new byte[3][256];
    int channel = getZCTCoords(lastPlane)[1];
    if (channel >= channelColors.length) return null;
    int color = channelColors[channel];

    float red = (color & 0xff) / 255f;
    float green = ((color & 0xff00) >> 8) / 255f;
    float blue = ((color & 0xff0000) >> 16) / 255f;

    for (int i=0; i<lut[0].length; i++) {
      lut[0][i] = (byte) (red * i);
      lut[1][i] = (byte) (green * i);
      lut[2][i] = (byte) (blue * i);
    }

    return lut;
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    int pixelType = getPixelType();
    if ((pixelType != FormatTools.INT16 && pixelType != FormatTools.UINT16) ||
        !isIndexed())
    {
      return null;
    }
    short[][] lut = new short[3][65536];
    int channel = getZCTCoords(lastPlane)[1];
    if (channel >= channelColors.length) return null;
    int color = channelColors[channel];

    float red = (color & 0xff) / 255f;
    float green = ((color & 0xff00) >> 8) / 255f;
    float blue = ((color & 0xff0000) >> 16) / 255f;

    for (int i=0; i<lut[0].length; i++) {
      lut[0][i] = (short) (red * i);
      lut[1][i] = (short) (green * i);
      lut[2][i] = (short) (blue * i);
    }
    return lut;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      acquisitionDate = null;
      timestamps = exposureTime = null;
      offsets = null;
      coordinates = null;
      imageFiles = null;
      cIndex = -1;
      bpp = 0;
      isJPEG = isZlib = false;

      tagsToParse = null;
      nextEmWave = nextExWave = nextChName = 0;
      realWidth = realHeight = 0;
      stageX.clear();
      stageY.clear();
      channelColors = null;
      lastPlane = 0;
      tiles.clear();
      detectorGain.clear();
      detectorOffset.clear();
      emWavelength.clear();
      exWavelength.clear();
      channelName.clear();
      physicalSizeX = physicalSizeY = physicalSizeZ = null;
      rowCount = colCount = rawCount = 0;
      imageDescription = null;
      channelIndices.clear();
      zIndices.clear();
      timepointIndices.clear();
      tileIndices.clear();
      timepoint = 0;
      roiIDs.clear();
      layers.clear();
    }
  }

  // -- Internal FormatReader API methods --

  void
  parseMainTags(int image, MetadataStore store, ArrayList<Tag> tags)
    throws FormatException, IOException {
    int effectiveSizeC = 0;
    try {
      effectiveSizeC = getEffectiveSizeC();
    }
    catch (ArithmeticException e) { }

    for (Tag t : tags)
    {
      String key = t.getKey();
      String value = t.getValue();

      try {
        if (key.equals("Image Channel Index")) {
          cIndex = Integer.parseInt(value);
          addGlobalMetaList(key, cIndex);
          continue;
        }
        else if (key.equals("ImageWidth")) {
          int v = Integer.parseInt(value);
          if (getSizeX() == 0 || v < getSizeX()) {
            core.get(0).sizeX = v;
          }
          if (realWidth == 0 && v > realWidth) realWidth = v;
        }
        else if (key.equals("ImageHeight")) {
          int v = Integer.parseInt(value);
          if (getSizeY() == 0 || v < getSizeY()) core.get(0).sizeY = v;
          if (realHeight == 0 || v > realHeight) realHeight = v;
        }

        if (cIndex != -1) key += " " + cIndex;
        if (!key.startsWith("Camera Acquisition Time") && !key.startsWith("ImageRelativeTime")) {
          String metavalue = value;
          if (key.endsWith("Date")) {
            try {
              metavalue = DateTools.convertDate(parseTimestamp(value), DateTools.UNIX, DateTools.ISO8601_FORMAT_MS);
            }
            catch (Exception e) {
            }
          }
          addGlobalMeta(key, metavalue);
        }

        if (key.startsWith("ImageTile") && !(store instanceof DummyMetadata)) {
          if (!tiles.containsKey(new Integer(value))) {
            tiles.put(Integer.valueOf(value), 1);
          }
          else {
            int v = tiles.get(new Integer(value)).intValue() + 1;
            tiles.put(new Integer(value), new Integer(v));
          }
        }

        if (key.startsWith("MultiChannel Color")) {
          if (cIndex >= 0 && cIndex < effectiveSizeC) {
            if (channelColors == null ||
                effectiveSizeC > channelColors.length)
            {
              channelColors = new int[effectiveSizeC];
            }
            if (channelColors[cIndex] == 0) {
              channelColors[cIndex] = Integer.parseInt(value);
            }
          }
          else if (cIndex == effectiveSizeC && channelColors != null &&
            channelColors[0] == 0)
          {
            System.arraycopy(
              channelColors, 1, channelColors, 0, channelColors.length - 1);
            channelColors[cIndex - 1] = Integer.parseInt(value);
          }
          else if (channelColors != null && channelColors[0] > 0 &&
            channelColors.length > 1)
          {
            int c = 1;
            while (c < channelColors.length - 1 && channelColors[c] != 0) {
              c++;
            }
            if (channelColors[c] == 0) {
              channelColors[c] = Integer.parseInt(value);
            }
          }
        }
        else if (key.startsWith("Scale Factor for X") && physicalSizeX == null)
        {
          physicalSizeX = Double.parseDouble(value);
        }
        else if (key.startsWith("Scale Factor for Y") && physicalSizeY == null)
        {
          physicalSizeY = Double.parseDouble(value);
        }
        else if (key.startsWith("Scale Factor for Z") && physicalSizeZ == null)
        {
          physicalSizeZ = Double.parseDouble(value);
        }
        else if (key.startsWith("Number Rows") && rowCount == 0)
        {
          rowCount = parseInt(value);
        }
        else if (key.startsWith("Number Columns") && colCount == 0)
        {
          colCount = parseInt(value);
        }
        else if (key.startsWith("NumberOfRawImages") && rawCount == 0)
        {
          rawCount = parseInt(value);
        }
        else if (key.startsWith("Emission Wavelength")) {
          if (cIndex != -1) {
            Double wave = new Double(value);
            Length emission = FormatTools.getEmissionWavelength(wave);
            if (emission != null) {
              emWavelength.put(cIndex, emission);
            }
          }
        }
        else if (key.startsWith("Excitation Wavelength")) {
          if (cIndex != -1) {
            Double wave = new Double(Double.parseDouble(value));
            Length excitation = FormatTools.getExcitationWavelength(wave);
            if (excitation != null) {
              exWavelength.put(cIndex, excitation);
            }
          }
        }
        else if (key.startsWith("Channel Name")) {
          if (cIndex != -1) {
            channelName.put(cIndex, value);
          }
        }
        else if (key.startsWith("Exposure Time [ms]")) {
          if (exposureTime.get(new Integer(cIndex)) == null) {
            double exp = Double.parseDouble(value) / 1000;
            exposureTime.put(new Integer(cIndex), String.valueOf(exp));
          }
        }
        else if (key.startsWith("User Name")) {
          String[] username = value.split(" ");
          if (username.length >= 2) {
            String id = MetadataTools.createLSID("Experimenter", 0);
            store.setExperimenterID(id, 0);
            store.setExperimenterFirstName(username[0], 0);
            store.setExperimenterLastName(username[username.length - 1], 0);
          }
        }
        else if (key.equals("User company")) {
          String id = MetadataTools.createLSID("Experimenter", 0);
          store.setExperimenterID(id, 0);
          store.setExperimenterInstitution(value, 0);
        }
        else if (key.startsWith("Objective Magnification")) {
          Double magnification = Double.parseDouble(value);
          store.setObjectiveNominalMagnification(magnification, 0, 0);
        }
        else if (key.startsWith("Objective ID")) {
          store.setObjectiveID("Objective:" + value, 0, 0);
          store.setObjectiveCorrection(getCorrection("Other"), 0, 0);
          store.setObjectiveImmersion(getImmersion("Other"), 0, 0);
        }
        else if (key.startsWith("Objective N.A.")) {
          store.setObjectiveLensNA(new Double(value), 0, 0);
        }
        else if (key.startsWith("Objective Name")) {
          String[] tokens = value.split(" ");
          for (int q=0; q<tokens.length; q++) {
            int slash = tokens[q].indexOf('/');
            if (slash != -1 && slash - q > 0) {
              Double mag = 
                  Double.parseDouble(tokens[q].substring(0, slash - q));
              String na = tokens[q].substring(slash + 1);
              store.setObjectiveNominalMagnification(mag, 0, 0);
              store.setObjectiveLensNA(new Double(na), 0, 0);
              store.setObjectiveCorrection(getCorrection(tokens[q - 1]), 0, 0);
              break;
            }
          }
        }
        else if (key.startsWith("Objective Working Distance")) {
          store.setObjectiveWorkingDistance(new Length(new Double(value), UNITS.MICROMETER), 0, 0);
        }
        else if (key.startsWith("Objective Immersion Type")) {
          String immersion = "Other";
          switch (Integer.parseInt(value)) {
            // case 1: no immersion
            case 2:
              immersion = "Oil";
              break;
            case 3:
              immersion = "Water";
              break;
          }
          store.setObjectiveImmersion(getImmersion(immersion), 0, 0);
        }
        else if (key.startsWith("Stage Position X")) {
          final Double number = Double.valueOf(value);
          stageX.put(image, new Length(number, UNITS.REFERENCEFRAME));
          addGlobalMetaList("X position for position", value);
        }
        else if (key.startsWith("Stage Position Y")) {
          final Double number = Double.valueOf(value);
          stageY.put(image, new Length(number, UNITS.REFERENCEFRAME));
          addGlobalMetaList("Y position for position", value);
        }
        else if (key.startsWith("Orca Analog Gain")) {
          detectorGain.put(cIndex, new Double(value));
        }
        else if (key.startsWith("Orca Analog Offset")) {
          detectorOffset.put(cIndex, new Double(value));
        }
        else if (key.startsWith("Comments")) {
          imageDescription = value;
        }
        else if (key.startsWith("Acquisition Date")) {
          acquisitionDate = new Timestamp(DateTools.convertDate(parseTimestamp(value), DateTools.UNIX, DateTools.ISO8601_FORMAT_MS));
        }
        else if (image >= 0 && key.startsWith("Camera Acquisition Time")) { // Note Double variant for TIFF XML.
          String date = DateTools.convertDate(parseTimestamp(value), DateTools.UNIX, DateTools.ISO8601_FORMAT_MS);
          addSeriesMetaList(key, date);
          if (timepoint != 0) { // First timepoint is duplicated for some reason.
            timestamps.put(timepoint - 1, date);
          } else {
            timestamps.put(timepoint, date);
          }
          timepoint++;
        }
        else if (image >= 0 && key.startsWith("ImageRelativeTime")) {
          Time time = new Time(Double.parseDouble(value), UNITS.DAY);
          String timestr = time.value().toString() + " " + time.unit().getSymbol();
          addSeriesMetaList(key, timestr);
        }
      }
      catch (NumberFormatException e) { }
    }
  }



  /**
   * Parse an Integer from a String.
   * @param number the number to parse
   * @return an Integer.  0 if number was null.
   */
  protected static int parseInt(String number) {
    return parseInt(number, 0);
  }

  /**
   * Parse an Integer from a String.
   * @param number the number to parse
   * @param defaultnum the number to return if number is null or empty
   * @return an Integer.  0 if number was null.
   */
  protected static int parseInt(String number, int defaultnum) {
    if (number != null && number.trim().length() > 0) {
      return Integer.parseInt(number);
    }
    return defaultnum;
  }

  /**
   * Parse timestamp from string.  Note this may be ZVI-specific
   * due to the use of locale-specific date formats in the TIFF XML.
   * @param s
   * @return a timestamp
   */
  private long parseTimestamp(String s) {
    try
      {
        double dstamp = Double.parseDouble(s); // Time in days since the ZVI epoch
        DateTime epoch = new DateTime(1900, 1, 1, 0, 0, DateTimeZone.UTC); // 1900-01-01 00:00:00

        long millisInDay = 24L * 60L * 60L * 1000L;
        int days = (int) Math.floor(dstamp);
        int millis = (int)((dstamp - days) * millisInDay + 0.5);

        days -= 1; // We start on day 1
        if (days > 60) { // Date prior to 1900-03-01
          days -= 1; // 1900-02-29 is considered a valid date by Excel; correct for this.
        }

        DateTime dtstamp = epoch.plusDays(days);
        dtstamp = dtstamp.plusMillis(millis);

        return dtstamp.getMillis();
      }
    catch (NumberFormatException e)
      {
        // Not a Double; one file (BY4741NQ2.zvi) contains a string in
        // place of the float64 defined in the spec, so try parsing
        // using US date format.  May be a historical AxioVision bug.
        String us_format = "MM/dd/yyyy hh:mm:ss aa";
        return DateTools.getTime(s, us_format, null);
      }
  }

  public enum Context { MAIN, SCALING, PLANE }

  /**
   * Content of a single tag from a Tags block.
   */
  public class Tag
  {
    // index number of the tag in the XML.  Not useful except for parsing validation.
    private int index;
    // key number of the tag (I element).  Needs mapping to a descriptive name.
    private int keyid;
    // value of the tag (V element).
    private String value;
    // category (presumed) of the tag (A element).
    private int category;
    // Context in which this tag is found.
    private Context context;

    /**
     * Constructor.
     * All variables are initialized to be invalid to permit later validation of correct parsing.
     * @param index the index number of the tag.
     * @param context the context (main, scaling or plane).
     */
    public Tag(int index, Context context)
    {
      this.index = index;
      keyid = -1;
      value = null;
      category = -1;
      this.context = context;
    }

    /**
     * Constructor.  The key id will be automatically set.
     * @param keyid the key number of the tag.
     * @param value the value of the tag.
     * @param context the context (main, scaling or plane).
     */
    public Tag(int keyid, String value, Context context)
    {
      this.index = 0;
      this.keyid = keyid;
      this.value = value;
      this.category = 0;
      this.context = context;
    }

    public void setKey(int keyid)
    {
      this.keyid = keyid;
    }

    public String getKey()
    {
      return getKey(keyid);
    }

    public int getKeyID()
    {
      return keyid;
    }

    public void setValue(String value)
    {
      this.value = value;
    }

    public String getValue()
    {
      return value;
    }

    public void setIndex(int index)
    {
      this.index = index;
    }

    public int getIndex()
    {
      return index;
    }

    public void setCategory(int category)
    {
      this.category = category;
    }

    public int getCategory()
    {
      return category;
    }

    /**
     * Check if the tag is valid (key, value and category have been set).
     * @return true if valid, otherwise false.
     */
    public boolean valid () {
      return keyid != -1 && value != null && category != -1;
    }

    @Override
    public String toString()
    {
      String s = new String();
      s += keyid + " (" + getKey() + ") = " + getValue();
      switch (context)
      {
        case MAIN:
          s += " [main]"; break;
        case SCALING:
          s += " [scaling]"; break;
        case PLANE:
          s += " [plane]"; break;
        default:
          s+= " [unknown]"; break;
      }
      return s;
    }

    /** Return the string corresponding to the given ID. */
    protected String getKey(int tagID) {
      switch (tagID) {
        case -1: return "INVALID";
        case 222: return "Compression";
        case 257: return "DateMappingTable";
        case 258: return "BlackValue"; // "Black Value"
        case 259: return "WhiteValue"; // "White Value"
        case 260: return "ImageDataMappingAutoRange";
        case 261: return "Thumbnail"; // "Image Thumbnail"
        case 262: return "GammaValue"; // "Gamma Value"
        case 264: return "ImageOverExposure";
        case 265: return "ImageRelativeTime1";
        case 266: return "ImageRelativeTime2";
        case 267: return "ImageRelativeTime3";
        case 268: return "ImageRelativeTime4";
        case 300: return "ImageRelativeTime";
        case 301: return "ImageBaseTime1"; // Also "ImageBaseTimeFirst"
        case 302: return "ImageBaseTime2";
        case 303: return "ImageBaseTime3";
        case 305: return "ImageBaseTime4";
        case 333: return "RelFocusPosition1";
        case 334: return "RelFocusPosition2";
        case 513: return "ObjectType";
        case 515: return "ImageWidth"; // "Image Width (Pixel)"
        case 516: return "ImageHeight"; // "Image Height (Pixel)"
        case 517: return "Number Raw Count"; // "ImageCountRaw"
        case 518: return "PixelType"; // "Pixel Type"
        case 519: return "NumberOfRawImages"; // "Number Raw Images"
        case 520: return "ImageSize"; // "Image Size"
        case 521: return "CompressionFactorForSave";
        case 522: return "DocumentSaveFlags";
        case 523: return "Acquisition pause annotation";
        case 530: return "Document Subtype";
        case 531: return "Acquisition Bit Depth";
        case 532: return "Image Memory Usage (RAM)";
        case 534: return "Z-Stack single representative";
        case 769: return "Scale Factor for X";
        case 770: return "Scale Unit for X";
        case 771: return "Scale Width";
        case 772: return "Scale Factor for Y";
        case 773: return "Scale Unit for Y";
        case 774: return "Scale Height";
        case 775: return "Scale Factor for Z";
        case 776: return "Scale Unit for Z";
        case 777: return "Scale Depth";
        case 778: return "Scaling Parent";
        case 1001: return "Date";
        case 1002: return "Code";
        case 1003: return "Source";
        case 1004: return "Message";
        case 1025: return "Camera Acquisition Time"; // Also "Acquisition Date" in older AxioVision versions, and "CameraImageAcquisitionTime" in the spec.  Double for ZVI, nonportable date string for TIFF.
        case 1026: return "8-bit Acquisition";
        case 1027: return "Camera Bit Depth";
        case 1029: return "MonoReferenceLow";
        case 1030: return "MonoReferenceHigh";
        case 1031: return "RedReferenceLow";
        case 1032: return "RedReferenceHigh";
        case 1033: return "GreenReferenceLow";
        case 1034: return "GreenReferenceHigh";
        case 1035: return "BlueReferenceLow";
        case 1036: return "BlueReferenceHigh";
        case 1041: return "FrameGrabber Name"; // "Framegrabber Name" in spec.
        case 1042: return "Camera";
        case 1044: return "CameraTriggerSignalType";
        case 1045: return "CameraTriggerEnable";
        case 1046: return "GrabberTimeout";
        case 1047: return "Camera Acquisition Time"; // Undocumented in spec., but appears to be 1025 as string double value.  Avoids the need for fragile date parsing.
        case 1281: return "MultiChannelEnabled";
        case 1282: return "MultiChannel Color"; // False colour for the channel; "Multichannel Colour" in spec.
        case 1283: return "MultiChannel Weight"; // False colour weighting; "Multichannel Weight" in spec.
        case 1284: return "Channel Name";
        case 1536: return "DocumentInformationGroup";
        case 1537: if (context == Context.SCALING)
          return "Scale Unit for Z"; // Only within a Scalings block.
        else
          return "Title";
        case 1538: return "Author";
        case 1539: return "Keywords";
        case 1540: return "Comments";
        case 1541: return "SampleID";
        case 1542: return "Subject";
        case 1543: return "RevisionNumber";
        case 1544: return "Save Folder";
        case 1545: return "FileLink";
        case 1546: return "Document Type";
        case 1547: return "Storage Media";
        case 1548: return "File ID";
        case 1549: return "Reference";
        case 1550: return "File Date";
        case 1551: return "File Size";
        case 1553: return "Filename";
        case 1792: return "ProjectGroup";
        case 1793: return "Acquisition Date";
        case 1794: return "Last modified by";
        case 1795: return "User Company";
        case 1796: return "User Company Logo";
        case 1797: return "Image";
        case 1800: return "User ID";
        case 1801: return "User Name";
        case 1802: return "User City";
        case 1803: return "User Address";
        case 1804: return "User Country";
        case 1805: return "User Phone";
        case 1806: return "User Fax";
        case 2049: return "Objective Name";
        case 2050: return "Optovar";
        case 2051: return "Reflector";
        case 2052: return "Condenser Contrast";
        case 2053: return "Transmitted Light Filter 1";
        case 2054: return "Transmitted Light Filter 2";
        case 2055: return "Reflected Light Shutter";
        case 2056: return "Condenser Front Lens";
        case 2057: return "Excitation Filter Name"; // "Excitation Filer Name" in the spec, but must be a typo.
        case 2060: return "Transmitted Light Fieldstop Aperture";
        case 2061: return "Reflected Light Aperture";
        case 2062: return "Condenser N.A.";
        case 2063: return "Light Path";
        case 2064: return "HalogenLampOn";
        case 2065: return "Halogen Lamp Mode";
        case 2066: return "Halogen Lamp Voltage";
        case 2068: return "Fluorescence Lamp Level";
        case 2069: return "Fluorescence Lamp Intensity";
        case 2070: return "LightManagerEnabled"; // "Light Manager is Enabled" in spec.
        case 2071: return "tag_ID_2071"; // Undocumented in spec.
        case 2072: return "Focus Position";
        case 2073: return "Stage Position X";
        case 2074: return "Stage Position Y";
        case 2075: return "Microscope Name";
        case 2076: return "Objective Magnification";
        case 2077: return "Objective N.A.";
        case 2078: return "MicroscopeIllumination"; // "Microscope Illumination" in spec.
        case 2079: return "External Shutter 1";
        case 2080: return "External Shutter 2";
        case 2081: return "External Shutter 3";
        case 2082: return "External Filter Wheel 1 Name";
        case 2083: return "External Filter Wheel 2 Name";
        case 2084: return "Parfocal Correction";
        case 2086: return "External Shutter 4";
        case 2087: return "External Shutter 5";
        case 2088: return "External Shutter 6";
        case 2089: return "External Filter Wheel 3 Name";
        case 2090: return "External Filter Wheel 4 Name";
        case 2103: return "Objective Turret Position";
        case 2104: return "Objective Contrast Method";
        case 2105: return "Objective Immersion Type";
        case 2107: return "Reflector Position";
        case 2109: return "Transmitted Light Filter 1 Position";
        case 2110: return "Transmitted Light Filter 2 Position";
        case 2112: return "Excitation Filter Position";
        case 2113: return "Lamp Mirror Position";
        case 2114: return "External Filter Wheel 1 Position";
        case 2115: return "External Filter Wheel 2 Position";
        case 2116: return "External Filter Wheel 3 Position";
        case 2117: return "External Filter Wheel 4 Position";
        case 2118: return "Lightmanager Mode";
        case 2119: return "Halogen Lamp Calibration";
        case 2120: return "CondenserNAGoSpeed";
        case 2121: return "TransmittedLightFieldstopGoSpeed";
        case 2122: return "OptovarGoSpeed";
        case 2123: return "Focus calibrated";
        case 2124: return "FocusBasicPosition";
        case 2125: return "FocusPower";
        case 2126: return "FocusBacklash";
        case 2127: return "FocusMeasurementOrigin";
        case 2128: return "FocusMeasurementDistance";
        case 2129: return "FocusSpeed";
        case 2130: return "FocusGoSpeed";
        case 2131: return "FocusDistance"; // "Focus Distance" in spec
        case 2132: return "FocusInitPosition";
        case 2133: return "Stage calibrated";
        case 2134: return "StagePower";
        case 2135: return "StageXBacklash";
        case 2136: return "StageYBacklash";
        case 2137: return "StageSpeedX"; // "Stage Speed X"
        case 2138: return "StageSpeedY"; // "Stage Speed Y"
        case 2139: return "StageSpeed"; // "Stage Speed"
        case 2140: return "StageGoSpeedX"; // "Stage Go Speed X"
        case 2141: return "StageGoSpeedY"; // "Stage Go Speed Y"
        case 2142: return "StageStepDistanceX"; // "Stage Step Distance X"
        case 2143: return "StageStepDistanceY"; // "Stage Step Distance Y"
        case 2144: return "StageInitialisationPositionX"; // "Stage Initialisation Position X"
        case 2145: return "StageInitialisationPositionY"; // "Stage Initialisation Position Y"
        case 2146: return "MicroscopeMagnification";
        case 2147: return "ReflectorMagnification"; // "Reflector Magnification"
        case 2148: return "LampMirrorPosition"; // "Lamp Mirror Position"
        case 2149: return "FocusDepth";
        case 2150: return "MicroscopeType"; // "Microscope Type"
        case 2151: return "Objective Working Distance";
        case 2152: return "ReflectedLightApertureGoSpeed";
        case 2153: return "External Shutter";
        case 2154: return "ObjectiveImmersionStop"; // "Objective Immersion Stop"
        case 2155: return "Focus Start Speed";
        case 2156: return "Focus Acceleration";
        case 2157: return "ReflectedLightFieldstop"; // "Reflected Light Fieldstop"
        case 2158: return "ReflectedLightFieldstopGoSpeed";
        case 2159: return "ReflectedLightFilter 1"; // "Reflected Light Filter 1"
        case 2160: return "ReflectedLightFilter 2"; // "Reflected Light Filter 2"
        case 2161: return "ReflectedLightFilter1Position"; // "Reflected Light Filter 1 Position"
        case 2162: return "ReflectedLightFilter2Position"; // "Reflected Light Filter 2 Position"
        case 2163: return "TransmittedLightAttenuator"; // "Transmitted Light Attenuator"
        case 2164: return "ReflectedLightAttenuator"; // "Reflected Light Attenuator"
        case 2165: return "Transmitted Light Shutter";
        case 2166: return "TransmittedLightAttenuatorGoSpeed";
        case 2167: return "ReflectedLightAttenuatorGoSpeed";
        case 2176: return "TransmittedLightVirtualFilterPosition";
        case 2177: return "TransmittedLightVirtualFilter"; // "Transmitted Light Virtual Filter"
        case 2178: return "ReflectedLightVirtualFilterPosition";
        case 2179: return "ReflectedLightVirtualFilter"; // "Reflected Light Virtual Filter"
        case 2180: return "ReflectedLightHalogenLampMode"; // "Reflected Light Halogen Lamp Mode"
        case 2181: return "ReflectedLightHalogenLampVoltage"; // "Reflected Light Halogen Lamp Voltage"
        case 2182: return "ReflectedLightHalogenLampColorTemperature"; // "Reflected Light Halogen Lamp Colour Temperature" in spec
        case 2183: return "ContrastManagerMode"; // "Contrastmanager Mode"
        case 2184: return "Dazzle Protection Active";
        case 2195: return "Zoom";
        case 2196: return "ZoomGoSpeed";
        case 2197: return "LightZoom"; // "Light Zoom"
        case 2198: return "LightZoomGoSpeed";
        case 2199: return "LightZoomCoupled"; // "Lightzoom Coupled", probably "LightZoom Coupled"
        case 2200: return "TransmittedLightHalogenLampMode"; // "Transmitted Light Halogen Lamp Mode"
        case 2201: return "TransmittedLightHalogenLampVoltage"; // "Transmitted Light Halogen Lamp Voltage"
        case 2202: return "TransmittedLightHalogenLampColorTemperature"; // "Transmitted Light Halogen Colour Temperature" in spec
        case 2203: return "Reflected Coldlight Mode";
        case 2204: return "Reflected Coldlight Intensity";
        case 2205: return "Reflected Coldlight Color Temperature"; // "Reflected Coldlight Colour Temperature" in spec
        case 2206: return "Transmitted Coldlight Mode";
        case 2207: return "Transmitted Coldlight Intensity";
        case 2208: return "Transmitted Coldlight Color Temperature"; // "Transmitted Coldlight Colour Temperature" in spec
        case 2209: return "Infinityspace Portchanger Position";
        case 2210: return "Beamsplitter Infinity Space";
        case 2211: return "TwoTv VisCamChanger Position";
        case 2212: return "Beamsplitter Ocular";
        case 2213: return "TwoTv CamerasChanger Position";
        case 2214: return "Beamsplitter Cameras";
        case 2215: return "Ocular Shutter";
        case 2216: return "TwoTv CamerasChangerCube";
        case 2217: return "LightWaveLength";
        case 2218: return "Ocular Magnification";
        case 2219: return "Camera Adapter Magnification";
        case 2220: return "Microscope Port";
        case 2221: return "Ocular Total Magnification";
        case 2222: return "Field of View";
        case 2223: return "Ocular";
        case 2224: return "CameraAdapter";
        case 2225: return "StageJoystickEnabled";
        case 2226: return "ContrastManager Contrast Method"; // "ContrastManagerContrastMethod" in spec
        case 2229: return "CamerasChanger Beamsplitter Type"; // "CamerasChanger BeamSplitter Type"
        case 2235: return "Rearport Slider Position";
        case 2236: return "Rearport Source";
        case 2237: return "Beamsplitter Type Infinity Space";
        case 2238: return "Fluorescence Attenuator";
        case 2239: return "Fluorescence Attenuator Position";
        case 2247: return "tag_ID_2247";
        case 2252: return "tag_ID_2252";
        case 2253: return "tag_ID_2253";
        case 2254: return "tag_ID_2254";
        case 2255: return "tag_ID_2255";
        case 2256: return "tag_ID_2256";
        case 2257: return "tag_ID_2257";
        case 2258: return "tag_ID_2258";
        case 2259: return "tag_ID_2259";
        case 2260: return "tag_ID_2260";
        case 2261: return "Objective ID";
        case 2262: return "Reflector ID";
        case 2307: return "Camera Framestart Left"; // Camera Frame Left
        case 2308: return "Camera Framestart Top"; // Camera Frame Top
        case 2309: return "Camera Frame Width";
        case 2310: return "Camera Frame Height";
        case 2311: return "Camera Binning";
        case 2312: return "CameraFrameFull";
        case 2313: return "CameraFramePixelDistance";
        case 2318: return "DataFormatUseScaling";
        case 2319: return "CameraFrameImageOrientation";
        case 2320: return "VideoMonochromeSignalType";
        case 2321: return "VideoColorSignalType";
        case 2322: return "MeteorChannelInput";
        case 2323: return "MeteorChannelSync";
        case 2324: return "WhiteBalanceEnabled";
        case 2325: return "CameraWhiteBalanceRed";
        case 2326: return "CameraWhiteBalanceGreen";
        case 2327: return "CameraWhiteBalanceBlue";
        case 2331: return "CameraFrameScalingFactor";
        case 2562: return "Meteor Camera Type";
        case 2564: return "Exposure Time [ms]";
        case 2568: return "CameraExposureTimeAutoCalculate";
        case 2569: return "Meteor Gain Value";
        case 2571: return "Meteor Gain Automatic";
        case 2572: return "MeteorAdjustHue";
        case 2573: return "MeteorAdjustSaturation";
        case 2574: return "MeteorAdjustRedLow";
        case 2575: return "MeteorAdjustGreenLow";
        case 2576: return "Meteor Blue Low";
        case 2577: return "MeteorAdjustRedHigh";
        case 2578: return "MeteorAdjustGreenHigh";
        case 2579: return "MeteorBlue High";
        case 2582: return "CameraExposureTimeCalculationControl";
        case 2585: return "AxioCamFadingCorrectionEnable";
        case 2587: return "CameraLiveImage";
        case 2588: return "CameraLiveEnabled";
        case 2589: return "LiveImageSyncObjectName";
        case 2590: return "CameraLiveSpeed";
        case 2591: return "CameraImage";
        case 2592: return "CameraImageWidth";
        case 2593: return "CameraImageHeight";
        case 2594: return "CameraImagePixelType";
        case 2595: return "CameraImageShMemoryName";
        case 2596: return "CameraLiveImageWidth";
        case 2597: return "CameraLiveImageHeight";
        case 2598: return "CameraLiveImagePixelType";
        case 2599: return "CameraLiveImageShMemoryName";
        case 2600: return "CameraLiveMaximumSpeed";
        case 2601: return "CameraLiveBinning";
        case 2602: return "CameraLiveGainValue";
        case 2603: return "CameraLiveExposureTimeValue";
        case 2604: return "CameraLiveScalingFactor";
        case 2817: return "Image Index U";
        case 2818: return "Image Index V";
        case 2819: return "Image Index Z";
        case 2820: return "Image Channel Index"; // "Image Index C"
        case 2821: return "Image Index T";
        case 2822: return "ImageTile Index"; // "Image Index T"
        case 2823: return "Image acquisition Index";
        case 2824: return "ImageCount Tiles";
        case 2825: return "ImageCount A";
        case 2827: return "Image Index S"; // "Image Index S"
        case 2828: return "Image Index Raw";
        case 2832: return "Image Count Z";
        case 2833: return "Image Count C";
        case 2834: return "Image Count T";
        case 2838: return "Number Rows"; // "Image Count U"
        case 2839: return "Number Columns"; // "Image Count V"
        case 2840: return "Image Count S";
        case 2841: return "Original Stage Position X";
        case 2842: return "Original Stage Position Y";
        case 3088: return "LayerDrawFlags";
        case 3334: return "RemainingTime"; // "Remaining Time"
        case 3585: return "User Field 1";
        case 3586: return "User Field 2";
        case 3587: return "User Field 3";
        case 3588: return "User Field 4";
        case 3589: return "User Field 5";
        case 3590: return "User Field 6";
        case 3591: return "User Field 7";
        case 3592: return "User Field 8";
        case 3593: return "User Field 9";
        case 3594: return "User Field 10";
        case 3840: return "ID";
        case 3841: return "Name";
        case 3842: return "Value";
        case 5501: return "PvCamClockingMode";
        case 8193: return "Autofocus Status Report";
        case 8194: return "Autofocus Position";
        case 8195: return "Autofocus Position Offset";
        case 8196: return "Autofocus Empty Field Threshold";
        case 8197: return "Autofocus Calibration Name";
        case 8198: return "Autofocus Current Calibration Item";
        case 20478: return "tag_ID_20478";
        case 65537: return "CameraFrameFullWidth";
        case 65538: return "CameraFrameFullHeight";
        case 65541: return "AxioCam Shutter Signal";
        case 65542: return "AxioCam Delay Time";
        case 65543: return "AxioCam Shutter Control";
        case 65544: return "AxioCam BlackRefIsCalculated";
        case 65545: return "AxioCam Black Reference";
        case 65547: return "Camera Shading Correction";
        case 65550: return "AxioCam Enhance Color";
        case 65551: return "AxioCam NIR Mode";
        case 65552: return "CameraShutterCloseDelay";
        case 65553: return "CameraWhiteBalanceAutoCalculate";
        case 65556: return "AxioCam NIR Mode Available";
        case 65557: return "AxioCam Fading Correction Available";
        case 65559: return "AxioCam Enhance Color Available";
        case 65565: return "MeteorVideoNorm";
        case 65566: return "MeteorAdjustWhiteReference";
        case 65567: return "MeteorBlackReference";
        case 65568: return "MeteorChannelInputCountMono";
        case 65570: return "MeteorChannelInputCountRGB";
        case 65571: return "MeteorEnableVCR";
        case 65572: return "Meteor Brightness";
        case 65573: return "Meteor Contrast";
        case 65575: return "AxioCam Selector"; // "AxioCamSelector"
        case 65576: return "AxioCam Type";
        case 65577: return "AxioCam Info"; // "AxioCamInfo"
        case 65580: return "AxioCam Resolution";
        case 65581: return "AxioCam Color Model"; // "AxioCam Colour Model"
        case 65582: return "AxioCam MicroScanning";
        case 65585: return "Amplification Index";
        case 65586: return "Device Command"; // "DeviceCommand"
        case 65587: return "BeamLocation";
        case 65588: return "ComponentType";
        case 65589: return "ControllerType";
        case 65590: return "CameraWhiteBalanceCalculationRedPaint";
        case 65591: return "CameraWhiteBalanceCalculationBluePaint";
        case 65592: return "CameraWhiteBalanceSetRed";
        case 65593: return "CameraWhiteBalanceSetGreen";
        case 65594: return "CameraWhiteBalanceSetBlue";
        case 65595: return "CameraWhiteBalanceSetTargetRed";
        case 65596: return "CameraWhiteBalanceSetTargetGreen";
        case 65597: return "CameraWhiteBalanceSetTargetBlue";
        case 65598: return "ApotomeCamCalibrationMode";
        case 65599: return "ApoTome Grid Position";
        case 65600: return "ApotomeCamScannerPosition";
        case 65601: return "ApoTome Full Phase Shift";
        case 65602: return "ApoTome Grid Name";
        case 65603: return "ApoTome Staining";
        case 65604: return "ApoTome Processing Mode";
        case 65605: return "ApotomeCamLiveCombineMode";
        case 65606: return "ApoTome Filter Name";
        case 65607: return "Apotome Filter Strength";
        case 65608: return "ApotomeCamFilterHarmonics";
        case 65609: return "ApoTome Grating Period";
        case 65610: return "ApoTome Auto Shutter Used";
        case 65611: return "Apotome Cam Status"; // "ApoTomeCamStatus"
        case 65612: return "ApotomeCamNormalize";
        case 65613: return "ApotomeCamSettingsManager";
        case 65614: return "DeepviewCamSupervisorMode";
        case 65615: return "DeepView Processing";
        case 65616: return "DeepviewCamFilterName";
        case 65617: return "DeepviewCamStatus";
        case 65618: return "DeepviewCamSettingsManager";
        case 65619: return "DeviceScalingName";
        case 65620: return "CameraShadingIsCalculated";
        case 65621: return "CameraShadingCalculationName";
        case 65622: return "CameraShadingAutoCalculate";
        case 65623: return "CameraTriggerAvailable";
        case 65626: return "CameraShutterAvailable";
        case 65627: return "AxioCam ShutterMicroScanningEnable"; // "AxioCamShutterMicroScanningEnable"
        case 65628: return "ApotomeCamLiveFocus";
        case 65629: return "DeviceInitStatus";
        case 65630: return "DeviceErrorStatus";
        case 65631: return "ApotomeCamSliderInGridPosition";
        case 65632: return "Orca NIR Mode Used";
        case 65633: return "Orca Analog Gain";
        case 65634: return "Orca Analog Offset";
        case 65635: return "Orca Binning";
        case 65636: return "Orca Bit Depth";
        case 65637: return "ApoTome Averaging Count";
        case 65638: return "DeepView DoF";
        case 65639: return "DeepView EDoF";
        case 65643: return "DeepView Slider Name";
        case 65651: return "tag_ID_65651";
        case 65652: return "tag_ID_65652";
        case 65655: return "DeepView Slider Name"; // Also "Camera NIR Mode Enabled"
        case 65657: return "tag_ID_65657";
        case 65658: return "tag_ID_65658";
        case 65661: return "tag_ID_65661";
        case 65662: return "tag_ID_65662"; // Camera driver?

        case 5439491: return "Acquisition Software";
        case 16777488: return "Excitation Wavelength";
        case 16777489: return "Emission Wavelength";
        case 101515267: return "File Name";
        case 101253123:
        case 101777411:
          return "Image Name";
        default: return "tag_ID_" + tagID;
      }
    }
  }

  /*
   * AvioVision ROI/Annotation properties
   */

  /**
   * Feature types.  Two major catagories, "annotations" and "measurements".
   *
   * NOTE: Some of the point types are relative to a defined coordinate system (not yet seen).
   */
  enum FeatureType {
    UNKNOWN(-1, 0),
    POINT(0, 1), // Single point (1 point for xy location)
    POINTS(1, 0), // Set of points (n points)
    LINE(2, 2), // Single line (2 points)
    CALIPER(3, 6), // Distance at right angles to baseline; two intersecting perpendicular lines; Same as DISTANCE, but omit drawing the last line.
    DISTANCE(4, 6), // Distance between two parallel lines; three lines drawn at right angles; Pair1: distance being measured, p2 and p3 are the caliper ends.  Note that p2/3 are for display only; they need recomputing if edited.
    MULTIPLE_CALIPER(5, -4), // Multiple distances at right angles to baseline; The last pair is the baseline.  All preceding pairs are distances to the baseline.  First point is on the baseline.  If the baseline is moved, the baseline points need recomputing. 
    MULTIPLE_DISTANCE(5, -4), // Multiple distances between two parallel lines; Same as for 5.  But, an extra line the same length as the baseline is drawn at the other end of each line; this extra line is not stored.
    ANGLE3(7, 4), // In degrees (4 points--2 lines with common origin); angle determined from intersection
    ANGLE4(8, 4), // In degrees (4 points--2 lines with no common origin); angle determined from (virtual) intersection
    CIRCLE(9, 2), // Circle (2 points defining radius); Point 1 is centre, point 2 is edge.  Draw circle from this. (radius line + circle)
    SCALE_BAR(10, 2), // Scale bar (2 points); Start and end of scale bar line.
    POLYLINE_OPEN(12, -2), // Open polyline (>= 2 points)
    ALIGNED_RECTANGLE(13, 5), // Simple rectangle (5 points); can contain tag name/value or text string; points are a special case of open polyline
    RECTANGLE(14, 5), // Rectangle in any orientation (5 points); special case of open polyline; second point is rotation origin in UI, handles controlling size and rotation
    ELLIPSE(15, 5), // Ellipse drawn inside rectangle (as for aligned rectangle); can't be rotated.
    POLYLINE_CLOSED(16, -2), // Outline/polygon of straight lines.
    TEXT(17, 5), // Text box (5 points for closed bounding box like a rectangle); in UI hiding text also hides the rectangle outline.
    LENGTH(18, 6), // Distance (6 points--3 lines, first line is the real distance, second two are guides)
    SPLINE_OPEN(19, -3), // Open spline curve (probably natural spline) (>= 3 points); curve passes through all points
    SPLINE_CLOSED(20, -3), // Closed spline curve (probably natural spline) (>= 3 points); curve passes through all points
    LUT(21, 5), // Rectangle defining area for LUT gradient with adjacent text labels; gradient drawn inside rectangle, with labels down the right edge
    // Type 25 segfaults AxioVision :(  But icon looks like some sort of polyline/outline shape.
    // Type 26 same icon as 25 but nothing shows; may be >10 points.
    // Type 27 looks like a numbered points list, but nothing shown.
    //         Like type 1, but not selectable or editable.
    MEAS_PROFILE(28, 2), // Line for line profile; line thickness controls averaging (measurement); the profile itself is not stored, and needs computing.
    // Type 29 is a text box (rectangle with text), but icon is a set square + clock--maybe timeseries measurement?
    //   Points: 5
    // Type 30 same as 29, but rectangle + clock.
    //   Points: 5
    // Type 31 same as 29, but icon is a circle divided into coloured r/g/b thirds - channel related?
    //   Points: 5
    MEAS_POINT(32, 1), // Same as POINT, with label (measurement)
    MEAS_POINTS(33, 0), //Same as POINTS, with label (measurement)
    MEAS_LINE(34, 2), // Same as LINE, with label (measurement)
    MEAS_CALIPER(35, 6), // Same as CALIPER, with label along baseline [not distance?] (measurement)
    MEAS_DISTANCE(36, 6), // Same as DISTANCE, with label along baseline [not distance?] (measurement)
    MEAS_MULTIPLE_CALIPER(37, -4), // Same as MULTIPLE_CALIPER, with label along baseline [not distance?] (measurement)
    MEAS_MULTIPLE_DISTANCE(38, -4), // Same as MULTIPLE_DISTANCE, with label along baseline [not distance?] (measurement)
    MEAS_ANGLE3(39, 4), // Same as ANGLE3, with label (measurement)
    MEAS_ANGLE4(40, 4), // Same as ANGLE4, with label (measurement)
    MEAS_CIRCLE(41, 2), // Same as CIRCLE, with label along radius line from centre (measurement)
    MEAS_POLYLINE_OPEN(42, -2), // Same as POLYLINE_OPEN, with label to bottom right of top/right-most point (measurement)
    MEAS_ALIGNED_RECTANGLE(43, 5), // Same as ALIGNED_RECTANGLE, with label inside (measurement)
    MEAS_RECTANGLE(44, 5), // Same as RECTANGLE, with label to bottom right of top/right-most point (measurement)
    MEAS_ELLIPSE(45, 5), // Same as ELLIPSE, with label in top of bounding box as for rectangle (measurement)
    MEAS_POLYLINE_CLOSED(46, -2), // Same as POLYLINE_CLOSED, with label to bottom right of top/right-most point (measurement)
    MEAS_LENGTH(48, 6), // Same as LENGTH, with label (measurement)
    MEAS_SPLINE_OPEN(49, -3), // Same as SPLINE_OPEN, with label to bottom right of top/right-most point (measurement)
    MEAS_SPLINE_CLOSED(50, -3); // Same as SPLINE_CLOSED, with label to bottom right of top/right-most point (measurement)
    // Types 51,52,53 are a rectangle + text.  Text not editable; text not updated (not a measurement?).
    // Icons suggest time and/or 3D, either Z or 3D shapes.
    // Types 54, 55.  Like 6.
    // Types 56, 58, 59, 60.  No bboxes, no displayed ROI, icon is closed polyline.
    // Type 57.  Like 8.
    // Types 11, 22, 23, 24, 47, 61, 62, 63, 236.  Text, not editable or movable.
    //     This appears to be the default (invalid?) shape.  Or more likely, shapes which we haven't created valid metadata for, which are extensions to AxioVision.

    private static final Map<Integer,FeatureType> lookup = new HashMap<Integer,FeatureType>();

    static {
      for (FeatureType d : EnumSet.allOf(FeatureType.class))
        lookup.put(d.getValue(), d);
      // Alternative mappings:
      lookup.put(284, MEAS_PROFILE); // Is only the low byte 24?  What are the higher bytes?
    }

    private int value;
    private int points; // Number of points.  0=unlimited, negative is >= n

    private FeatureType (int value, int points) {
      this.value = value;
      this.points = points;
    }

    public int getValue() { return value; }

    public int getPoints() { return points; }

    boolean checkPoints(int points) {
      boolean status = false;
      if (this.points == 0)
        status = true;
      else if (this.points > 0 && points == this.points)
        status = true;
      else if (this.points < 0 && points >= (-this.points))
        status = true;
      return status;
    }

    public static FeatureType get(int value) {
      FeatureType ret = lookup.get(value);
      if (ret == null)
        ret = UNKNOWN;
      return ret;
    }
  }

  /**
   * Line drawing styles.
   */
  enum DrawStyle
  {
    NO_LINE(0), // Transparent
    SOLID(1),
    DOT(3),
    DASH(2),
    DASH_DOT(4),
    DASH_DOT_DOT(5);

    private static final Map<Integer,DrawStyle> lookup = new HashMap<Integer,DrawStyle>();

    static {
      for (DrawStyle d : EnumSet.allOf(DrawStyle.class))
        lookup.put(d.getValue(), d);
    }

    private int value;

    private DrawStyle (int value) {
      this.value = value;
    }

    public int getValue() { return value; }

    public static DrawStyle get(int value) {
      DrawStyle ret = lookup.get(value);
      if (ret == null)
        ret = SOLID;
      return ret;
    }
  }
  /**
   * Fill styles.
   */
  enum FillStyle
  {
    NONE (1), // No fill
    SOLID(0),
    HORIZONTAL_LINE(2), // ----
    VERTICAL_LINE(3), // ||||
    DOWNWARDS_DIAGONAL(4), // \\\\
    UPWARDS_DIAGONAL(5),// ////
    CROSS(6), // #### Cross hatching
    DIAGONAL_CROSS(7); // #### cross hatching rotated 45 degrees

    private static final Map<Integer,FillStyle> lookup = new HashMap<Integer,FillStyle>();

    static {
      for (FillStyle f : EnumSet.allOf(FillStyle.class))
        lookup.put(f.getValue(), f);
    }

    private int value;

    private FillStyle (int value) {
      this.value = value;
    }

    public int getValue() { return value; }

    public static FillStyle get(int value) {
      FillStyle ret = lookup.get(value);
      if (ret == null)
        ret = SOLID;
      return ret;
    }
  }
  /**
   * Line ending styles.
   */
  enum LineEndStyle
  {
    NONE (1), // No ends
    ARROWS(5), // Arrowheads (unfilled)
    END_TICKS(2), // Scale bar end marks (lines)
    FILLED_ARROWS(3); // Arrowheads (filled)

    private static final Map<Integer,LineEndStyle> lookup = new HashMap<Integer,LineEndStyle>();

    static {
      for (LineEndStyle f : EnumSet.allOf(LineEndStyle.class))
        lookup.put(f.getValue(), f);
    }

    private int value;

    private LineEndStyle (int value) {
      this.value = value;
    }

    public int getValue() { return value; }

    public static LineEndStyle get(int value) {
      LineEndStyle ret = lookup.get(value);
      if (ret == null)
        ret = ARROWS;
      return ret;
    }
  }

  /**
   * Point styles.
   */
  enum PointStyle
  {
    NONE (0), // Blank
    PLUS(2), // +
    CROSS(7), // ×
    SQUARE(1), // □
    DIAMOND(3), // ◇
    ASTERISK(4), // ✳ 8-spoke asterisk
    ARROW(5), // > Right, open arrowhead
    BAR(6), // | Vertical bar
    FILLED_ARROW(8), // ➤ Right, filled arrowhead
    CROSSHAIR(9); // ⊕
    // It does not appear to be possible to rotate arrowheads.  The point is at the tip of the arrowhead, not centred as for the others.

    private static final Map<Integer,PointStyle> lookup = new HashMap<Integer,PointStyle>();

    static {
      for (PointStyle f : EnumSet.allOf(PointStyle.class))
        lookup.put(f.getValue(), f);
    }

    private int value;

    private PointStyle (int value) {
      this.value = value;
    }

    public int getValue() { return value; }

    public static PointStyle get(int value) {
      PointStyle ret = lookup.get(value);
      if (ret == null)
        ret = CROSS;
      return ret;
    }
  }

  /*
   * Line ending positions.
   */
  enum LineEndPositions
  {
    NONE (0), // No ends
    LEFT(1), // Left end
    RIGHT(2), // Right end
    BOTH(3); // Both left and right ends

    private static final Map<Integer,LineEndPositions> lookup = new HashMap<Integer,LineEndPositions>();

    static {
      for (LineEndPositions f : EnumSet.allOf(LineEndPositions.class))
        lookup.put(f.getValue(), f);
    }

    private int value;

    private LineEndPositions (int value) {
      this.value = value;
    }

    public int getValue() { return value; }

    public static LineEndPositions get(int value) {
      LineEndPositions ret = lookup.get(value);
      if (ret == null)
        ret = NONE;
      return ret;
    }
  }

  /*
   * Text alignment.  Justification can be set to left/centre/right.  None means that no text is displayed,
   * and so is a display toggle rather than an alignment settings.  This turns off display of the bounding
   * rectangle for the text type.
   */
  enum TextAlignment
  {
    LEFT(0),
    CENTER(1),
    RIGHT(2),
    NONE(3);

    private static final Map<Integer,TextAlignment> lookup = new HashMap<Integer,TextAlignment>();

    static {
      for (TextAlignment f : EnumSet.allOf(TextAlignment.class))
        lookup.put(f.getValue(), f);
    }

    private int value;

    private TextAlignment (int value) {
      this.value = value;
    }

    public int getValue() { return value; }

    public static TextAlignment get(int value) {
      TextAlignment ret = lookup.get(value);
      if (ret == null)
        ret = LEFT;
      return ret;
    }
  }

  /*
   * Character sets.  Mapping from <windows.h> values to Java charset names.  Not all mappings are supported.
   * Pages not representable in unicode will get a null Charset.
   */
  enum Charset
  {
    ANSI(0,"windows-1252"),
    MAC_CHARSET(77,"MacRoman"),
    SHIFTJIS_CHARSET(128,"x-MS932_0213"),
    HANGUL_CHARSET(129,"x-windows-949"),
    GB2313_CHARSET(134,"x-mswin-936"),
    CHINESEBIG5_CHARSET(136,"x-windows-950"),
    GREEK_CHARSET(161,"windows-1253"),
    TURKISH_CHARSET(162,"windows-1254"),
    VIETNAMESE_CHARSET(163,"CP-1258"),
    HEBREW_CHARSET(177,"windows-1255"),
    ARABIC_CHARSET(178,"windows-1256"),
    BALTIC_CHARSET(186,"windows-1257"),
    RUSSIAN_CHARSET(204,"windows-1251"),
    THAI_CHARSET(222,"x-windows-874"),
    EASTEUROPE_CHARSET(238,"windows-1250");

    private static final Map<Integer,Charset> lookup = new HashMap<Integer,Charset>();

    static {
      for (Charset f : EnumSet.allOf(Charset.class))
        lookup.put(f.getValue(), f);
    }

    private int value;
    String name;

    private Charset (int value, String name) {
      this.value = value;
      this.name = name;
    }

    public int getValue() { return value; }

    public String getName() {  return name; }

    public static Charset get(int value) {
      Charset ret = lookup.get(value);
      if (ret == null)
        ret = ANSI;
      return ret;
    }
  }

  /*
   * Colours (in AxioVision UI; not represented in output except as RGB values):
   *   black 0/0/0
   *   white 255/255/255
   *   dark grey 64/64/64
   *   bright grey 224/224/224
   *   grey 128/128/128
   *   silver 192/192/192
   *   maroon 128/0/0
   *   red 255/0/0
   *   olive 128/128/0
   *   yellow 255/255/0
   *   green 0/128/0
   *   lime 0/255/0
   *   teal 0/128/128
   *   aqua 0/255/255
   *   navy 0/0/128
   *   blue 0/0/255
   *   purple 128/0/128
   *   fuschia 255/0/255
   *
   */

  /**
   * Shape class representing the metadata within an AxioVision Shape ROI
   */
  class Shape
  {
    int id = 0; // Shape number <Key>
    FeatureType type = FeatureType.UNKNOWN;
    int unknown1, unknown2, unknown3; // Pos 4-15
    // Bounding box (pixels from top left)
    int x1 = 0, y1 = 0, x2 = 0, y2 = 0, width = 0, height = 0;
    int unknown4, unknown5, unknown6, unknown7; // Pos 32-47.  38-40 is probably a colour.
    // From <ShapeAttributes>, typically an array of comma-separated uint8 numbers. First number is the total number of elements.
    // Drawing settings:
    byte fb = (byte) (0xFF&0xFF);
    int fillColour = parseColor(fb, fb,fb); // 48-50
    int textColour = parseColor(fb, fb,fb); // 52-54
    int drawColour = parseColor(fb, fb,fb); // 56-58
    int lineWidth = 1; // Limited to 0,1,2,3,4,5,6,7,8,10,15,20,25,30 (AxioVision interface)
    DrawStyle drawStyle = DrawStyle.NO_LINE; // Line drawing style
    FillStyle fillStyle = FillStyle.NONE; // Fill style
    int unknown8; // 72-75
    int unknown10, unknown11, unknown12; // Pos 96-111
    int unknown13, unknown14, unknown15, unknown16; // Pos 112-127
    int unknown17, unknown18, unknown19; // Pos 128-135, 140-143

    LineEndStyle lineEndStyle = LineEndStyle.NONE; // Line ending style
    PointStyle pointStyle = PointStyle.CROSS; // Line ending style
    int lineEndSize = 10; // Line ending shape size (pixels).  Also used for point sizes.
    LineEndPositions lineEndPositions = LineEndPositions.NONE; // Line ending positions
    // Font settings:
    String fontName;
    int fontSize = 10;
    int fontWeight = 300;
    boolean bold = false;
    boolean italic = false;
    boolean underline = false;
    boolean strikeout = false;
    Charset charset = Charset.ANSI;
    TextAlignment textAlignment= TextAlignment.LEFT;
    //int zpos; // Z position of annotation (relative to others?)
    // Label settings:
    String name; // Annotation name (UI only).
    String text; // Text displayed as part of the annotation (e.g. measurement value or label).
    Tag tagID; // Set if displaying a tag name.  Note Text will automatically include the tag name.
    boolean displayTag;
    // Misc:
    int handleSize; // UI setting?
    // Points
    int pointCount = 0;
    double points[];

    @Override
    public String toString() {
      String s = new String();
      s += "  SHAPE: " + id;
      s += "    Type=" + type;
      s += "    Unknown1=" + Long.toHexString(unknown1&0xFFFFFFFFL) + " Unknown2=" + Long.toHexString(unknown2&0xFFFFFFFFL) + " Unknown3=" + Long.toHexString(unknown3&0xFFFFFFFFL) + "\n";
      s += "    Bbox=" + x1 + "," + y1 + "  " + x2 + "," + y2 + "\n";
      s += "    Unknown4=" + Long.toHexString(unknown4&0xFFFFFFFFL) + " Unknown5=" + Long.toHexString(unknown5&0xFFFFFFFFL) + " Unknown6=" + Long.toHexString(unknown6&0xFFFFFFFFL) + " Unknown7=" + Long.toHexString(unknown7&0xFFFFFFFFL) + "\n";
      s += "    Unknown8=" + Long.toHexString(unknown8&0xFFFFFFFFL) + "\n";
      s += "    Unknown10=" + Long.toHexString(unknown10&0xFFFFFFFFL) + " Unknown11=" + Long.toHexString(unknown11&0xFFFFFFFFL) + " Unknown12=" + Long.toHexString(unknown12&0xFFFFFFFFL) + "\n";
      s += "    Unknown13=" + Long.toHexString(unknown13&0xFFFFFFFFL) + " Unknown14=" + Long.toHexString(unknown14&0xFFFFFFFFL) + " Unknown15=" + Long.toHexString(unknown15&0xFFFFFFFFL) + " Unknown16=" + Long.toHexString(unknown16&0xFFFFFFFFL) + "\n";
      s += "    Unknown17=" + Long.toHexString(unknown17&0xFFFFFFFFL) + " Unknown18=" + Long.toHexString(unknown18&0xFFFFFFFFL) + " Unknown19=" + Long.toHexString(unknown19&0xFFFFFFFFL) + "\n";
      s += "    fillColour=" + Long.toHexString(fillColour&0xFFFFFFFFL)
          + " textColour=" + Long.toHexString((textColour&0xFFFFFFFFL))
          + " drawColour=" + Long.toHexString((drawColour&0xFFFFFFFFL)) + "\n";
      s += "    drawStyle=" + drawStyle
          +  " fillStyle=" + fillStyle
          +  " lineEndStyle=" + lineEndStyle
          +  " lineEndSize=" + lineEndSize
          +  " lineEndPositions=" + lineEndPositions + "\n";
      s += "displayTag=" + displayTag + "\n";
      s += "  fontName=" + fontName + " size="+fontSize + " weight="+fontWeight + " bold="+bold + " italic=" + italic + " underline="+underline + " strikeout="+strikeout + " alignment=" + textAlignment + " charset=" + charset + "\n";
      s += "  name: " + name + "\n";
      s += "  text: " + text + "\n";
      //s += "  Zpos: " + zpos;
      s += "  tagID: " + tagID.getKey() + "\n";
      s += "  handleSize:" + handleSize + "\n";
      s += "  pointCount:" + pointCount + "\n    ";
      for (double point : points)
        s+=point + ", ";
      s+= "\n";
      return s;
    }
  }

  /**
   * Layer class representing an AxioVision layer; used to contain a
   * collection of shapes.
   */
  class Layer
  {
    int key; // Layer number
    String flags; // Unknown.
    public String name; // Layer name. (Assumed.)
    public ArrayList<Shape> shapes = new ArrayList<Shape>(); // List of shape objects, displayed deepest first, topmost last.

    @Override
    public String toString() {
      String s = new String();
      s += "LAYER: " + key;
      if (name != null)
        s += " (" + name + ")";
      s+= "\n";
      for (Shape shape : shapes) {
        s += shape;
      }
      return s;
    }
  }

  // TODO: Replace me with a proper Color class when available.
  protected static int parseColor(byte r, byte g, byte b) {
    return ((r&0xFF) << 24) | ((g&0xFF) << 16) | ((b&0xFF) << 8);
  }
}
