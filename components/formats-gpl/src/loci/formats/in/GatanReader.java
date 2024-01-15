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
import java.text.Collator;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.units.quantity.ElectricPotential;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.UNITS;
import ome.units.unit.Unit;
import ome.xml.model.primitives.Color;

/**
 * GatanReader is the file format reader for Gatan files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class GatanReader extends FormatReader {

  // -- Constants --

  public static final int DM3_MAGIC_BYTES = 3;
  public static final int DM4_MAGIC_BYTES = 4;

  /** Tag types. */
  private static final int GROUP = 20;
  private static final int VALUE = 21;

  /** Data types. */
  private static final int ARRAY = 15;
  private static final int SHORT = 2;
  private static final int USHORT = 4;
  private static final int INT = 3;
  private static final int UINT = 5;
  private static final int FLOAT = 6;
  private static final int DOUBLE = 7;
  private static final int BYTE = 8;
  private static final int UBYTE = 9;
  private static final int CHAR = 10;
  private static final int UNKNOWN = 11;
  private static final int UNKNOWN2 = 12;

  /** Shape types */
  private static final int LINE = 2;
  private static final int RECTANGLE = 5;
  private static final int ELLIPSE = 6;
  private static final int TEXT = 13;

  public static final String SPLIT_MONTAGE = "gatan.split_montage";
  public static final boolean SPLIT_MONTAGE_DEFAULT = true;

  // -- Fields --

  /** Offset to pixel data. */
  private long pixelOffset;

  /** List of pixel sizes. */
  private List<Double> pixelSizes;
  private List<String> units;

  private long numPixelBytes;

  private boolean signed;
  private long timestamp;
  private double gamma, mag, voltage;
  private String info;

  private Length posX, posY, posZ;
  private double sampleTime;

  private boolean adjustEndianness = true;
  private int version;

  private transient List<ROIShape> shapes;

  private transient boolean foundMontage = false;
  private transient List<Length> stageX = new ArrayList<Length>();
  private transient List<Length> stageY = new ArrayList<Length>();
  private transient List<Length> stageZ = new ArrayList<Length>();

  // -- Constructor --

  /** Constructs a new Gatan reader. */
  public GatanReader() {
    super("Gatan Digital Micrograph", new String[] {"dm3", "dm4"});
    domains = new String[] {FormatTools.EM_DOMAIN};
    suffixNecessary = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 4;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    int check = stream.readInt();
    return check == DM3_MAGIC_BYTES || check == DM4_MAGIC_BYTES;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int planeIndex = getSeries() * getImageCount() + no;

    long planeOffset = (long) planeIndex * FormatTools.getPlaneSize(this);
    in.seek(pixelOffset + planeOffset);
    readPlane(in, x, y, w, h, buf);
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      pixelOffset = 0;
      numPixelBytes = 0;
      pixelSizes = null;
      signed = false;
      timestamp = 0;
      gamma = mag = voltage = 0;
      info = null;
      adjustEndianness = true;
      version = 0;
      posX = posY = posZ = null;
      sampleTime = 0;
      units = null;
      shapes = null;
      foundMontage = false;
      stageX.clear();
      stageY.clear();
      stageZ.clear();
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected ArrayList<String> getAvailableOptions() {
    ArrayList<String> optionsList = super.getAvailableOptions();
    optionsList.add(SPLIT_MONTAGE);
    return optionsList;
  }

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    pixelOffset = 0;
    CoreMetadata m = core.get(0);

    LOGGER.info("Verifying Gatan format");

    m.littleEndian = false;
    pixelSizes = new ArrayList<Double>();
    units = new ArrayList<String>();
    shapes = new ArrayList<ROIShape>();

    in.order(isLittleEndian());

    // only support version 3
    version = in.readInt();
    if (version != 3 && version != 4) {
      throw new FormatException("invalid header");
    }

    LOGGER.info("Reading tags");

    in.skipBytes(4);
    skipPadding();
    m.littleEndian = in.readInt() != 1;
    in.order(isLittleEndian());

    // TagGroup instance

    in.skipBytes(2);
    skipPadding();
    int numTags = in.readInt();
    if (numTags > in.length()) {
      m.littleEndian = !isLittleEndian();
      in.order(isLittleEndian());
      adjustEndianness = false;
    }
    LOGGER.debug("tags ({}) {", numTags);
    try {
      parseTags(numTags, null, "  ");
    } catch (Exception e) {
       throw new FormatException("Unable to parse metadata tag", e);
    }
    LOGGER.debug("}");

    LOGGER.info("Populating metadata");

    m.littleEndian = true;

    if (getSizeX() == 0 || getSizeY() == 0) {
      throw new FormatException("Dimensions information not found");
    }

    if (m.sizeZ == 0) {
      m.sizeZ = 1;
    }
    m.sizeC = 1;
    m.sizeT = 1;
    m.dimensionOrder = "XYZTC";
    m.imageCount = getSizeZ() * getSizeC() * getSizeT();

    int bytes = (int) (numPixelBytes / (getSizeX() * getSizeY() * (long) getImageCount()));
    if (bytes != FormatTools.getBytesPerPixel(getPixelType())) {
      m.pixelType = FormatTools.pixelTypeFromBytes(bytes, signed, false);
    }

    m.rgb = false;
    m.interleaved = false;
    m.metadataComplete = true;
    m.indexed = false;
    m.falseColor = false;

    if (foundMontage && splitMontage() && stageX.size() > 1) {
      if (m.sizeZ > 1) {
        m.sizeZ /= stageX.size();
        m.imageCount = getSizeZ() * getSizeC() * getSizeT();

        for (int i=1; i<stageX.size(); i++) {
          core.add(new CoreMetadata(core.get(0)));
        }
      }
    }

    // The metadata store we're working with.
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      int index = 0;
      if (pixelSizes.size() > 4) {
        index = pixelSizes.size() - 3;
      }
      else if (pixelSizes.size() == 4) {
        if (Math.abs(pixelSizes.get(0) - 1.0) < Constants.EPSILON) {
          index = pixelSizes.size() - 2;
        }
      }
      if (index + 2 < pixelSizes.size() &&
        Math.abs(pixelSizes.get(index + 1) - pixelSizes.get(index + 2)) < Constants.EPSILON)
      {
        if (Math.abs(pixelSizes.get(index) - pixelSizes.get(index + 1)) > Constants.EPSILON &&
          getSizeY() > 1)
        {
          index++;
        }
      }

      if (index < pixelSizes.size() - 1) {
        Double x = pixelSizes.get(index);
        Double y = pixelSizes.get(index + 1);
        String xUnits = index < units.size() ? units.get(index) : "";
        String yUnits = index + 1 < units.size() ? units.get(index + 1) : "";
        Length sizeX = FormatTools.getPhysicalSizeX(x, convertUnits(xUnits));
        Length sizeY = FormatTools.getPhysicalSizeY(y, convertUnits(yUnits));
        if (sizeX != null) {
          for (int i=0; i<getSeriesCount(); i++) {
            store.setPixelsPhysicalSizeX(sizeX, i);
          }
        }
        if (sizeY != null) {
          for (int i=0; i<getSeriesCount(); i++) {
            store.setPixelsPhysicalSizeY(sizeY, i);
          }
        }

        if (index < pixelSizes.size() - 2) {
          Double z = pixelSizes.get(index + 2);
          String zUnits = index + 2 < units.size() ? units.get(index + 2) : "";
          Length sizeZ = FormatTools.getPhysicalSizeZ(z, convertUnits(zUnits));
          if (sizeZ != null) {
            for (int i=0; i<getSeriesCount(); i++) {
              store.setPixelsPhysicalSizeZ(sizeZ, i);
            }
          }
        }
      }

      String instrument = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrument, 0);

      String objective = MetadataTools.createLSID("Objective", 0, 0);
      store.setObjectiveID(objective, 0, 0);
      store.setObjectiveCorrection(MetadataTools.getCorrection("Unknown"), 0, 0);
      store.setObjectiveImmersion(MetadataTools.getImmersion("Unknown"), 0, 0);
      store.setObjectiveNominalMagnification(mag, 0, 0);

      String detector = MetadataTools.createLSID("Detector", 0, 0);
      store.setDetectorID(detector, 0, 0);

      String mode = null;
      if (info == null) info = "";
      String[] scopeInfo = info.split("\\(");
      for (String token : scopeInfo) {
        token = token.trim();
        if (token.startsWith("Mode")) {
          if (token.indexOf(' ') > 0) {
            token = token.substring(token.indexOf(' ')).trim();
          }
          if (token.indexOf(' ') > 0) {
            mode = token.substring(0, token.indexOf(' ')).trim();
          }
          else {
            mode = token;
          }
          if (mode.equals("TEM")) mode = "Other";
        }
      }

      int digits = String.valueOf(getSeriesCount() + 1).length();
      for (int i=0; i<getSeriesCount(); i++) {
        if (foundMontage && getSeriesCount() > 1) {
          store.setImageName(
            String.format("Tile #%0" + digits + "d", i + 1), i);
        }
        store.setImageInstrumentRef(instrument, i);
        store.setObjectiveSettingsID(objective, i);
        store.setDetectorSettingsID(detector, i, 0);
        store.setDetectorSettingsVoltage(new ElectricPotential(voltage, UNITS.VOLT),
              i, 0);

        if (mode != null) {
          store.setChannelAcquisitionMode(
            MetadataTools.getAcquisitionMode(mode), i, 0);
        }

        if (foundMontage && i < stageX.size()) {
          store.setPlanePositionX(stageX.get(i), i, 0);
          store.setPlanePositionY(stageY.get(i), i, 0);
          store.setPlanePositionZ(stageZ.get(i), i, 0);
        }
        else {
          store.setPlanePositionX(posX, i, 0);
          store.setPlanePositionY(posY, i, 0);
          store.setPlanePositionZ(posZ, i, 0);
        }

        for (int p=0; p<getImageCount(); p++) {
          store.setPlaneExposureTime(new Time(sampleTime, UNITS.SECOND), i, p);
        }
      }
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.NO_OVERLAYS &&
      shapes.size() > 0)
    {
      int nextROI = 0;
      for (int i=0; i<shapes.size(); i++) {
        ROIShape shape = shapes.get(i);
        String shapeID = null;

        switch (shape.type) {
          case LINE:
            shapeID = createROI(store, nextROI);
            store.setLineID(shapeID, nextROI, 0);
            store.setLineX1(shape.x1, nextROI, 0);
            store.setLineY1(shape.y1, nextROI, 0);
            store.setLineX2(shape.x2, nextROI, 0);
            store.setLineY2(shape.y2, nextROI, 0);
            store.setLineText(shape.text, nextROI, 0);
            store.setLineFontSize(shape.fontSize, nextROI, 0);
            store.setLineStrokeColor(shape.strokeColor, nextROI, 0);
            nextROI++;
            break;
          case TEXT:
            shapeID = createROI(store, nextROI);
            store.setLabelID(shapeID, nextROI, 0);
            store.setLabelX(shape.x1, nextROI, 0);
            store.setLabelY(shape.y1, nextROI, 0);
            store.setLabelText(shape.text, nextROI, 0);
            store.setLabelFontSize(shape.fontSize, nextROI, 0);
            store.setLabelStrokeColor(shape.strokeColor, nextROI, 0);
            nextROI++;
            break;
          case ELLIPSE:
            shapeID = createROI(store, nextROI);
            store.setEllipseID(shapeID, nextROI, 0);

            double radiusX = (shape.x2 - shape.x1) / 2;
            double radiusY = (shape.y2 - shape.y1) / 2;

            store.setEllipseX(shape.x1 + radiusX, nextROI, 0);
            store.setEllipseY(shape.y1 + radiusY, nextROI, 0);
            store.setEllipseRadiusX(radiusX, nextROI, 0);
            store.setEllipseRadiusY(radiusY, nextROI, 0);
            store.setEllipseText(shape.text, nextROI, 0);
            store.setEllipseFontSize(shape.fontSize, nextROI, 0);
            store.setEllipseStrokeColor(shape.strokeColor, nextROI, 0);
            nextROI++;
            break;
          case RECTANGLE:
            shapeID = createROI(store, nextROI);
            store.setRectangleID(shapeID, nextROI, 0);
            store.setRectangleX(shape.x1, nextROI, 0);
            store.setRectangleY(shape.y1, nextROI, 0);
            store.setRectangleWidth(shape.x2 - shape.x1, nextROI, 0);
            store.setRectangleHeight(shape.y2 - shape.y1, nextROI, 0);
            store.setRectangleText(shape.text, nextROI, 0);
            store.setRectangleFontSize(shape.fontSize, nextROI, 0);
            store.setRectangleStrokeColor(shape.strokeColor, nextROI, 0);
            nextROI++;
            break;
          default:
            LOGGER.warn("Unknown ROI type: {}", shape.type);
        }
      }
    }
  }

  public boolean splitMontage() {
    MetadataOptions options = getMetadataOptions();
    if (options instanceof DynamicMetadataOptions) {
      return ((DynamicMetadataOptions) options).getBoolean(
          SPLIT_MONTAGE, SPLIT_MONTAGE_DEFAULT);
    }
    return SPLIT_MONTAGE_DEFAULT;
  }

  // -- Helper methods --

  /**
   * Parses Gatan DM3 tags.
   * Information on the DM3 structure found at:
   * http://rsb.info.nih.gov/ij/plugins/DM3Format.gj.html and
   * http://www-hrem.msm.cam.ac.uk/~cbb/info/dmformat/
   *
   * The basic structure is this: the file is comprised of a list of tags.
   * Each tag is either a data tag or a group tag.  Group tags are simply
   * containers for more group and data tags, where data tags contain actual
   * metadata.  Each data tag is comprised of a type (byte, short, etc.),
   * a label, and a value.
   */
  private void parseTags(int numTags, String parent, String indent)
    throws FormatException, IOException, ParseException
  {
    if ("Montage".equals(parent)) {
      foundMontage = true;
    }
    for (int i=0; i<numTags; i++) {
      if (in.getFilePointer() + 3 >= in.length()) break;

      byte type = in.readByte();  // can be 21 (data) or 20 (tag group)
      int length = in.readShort();

      // image data is in tag with type 21 and label 'Data'
      // image dimensions are in type 20 tag with 2 type 15 tags
      // bytes per pixel is in type 21 tag with label 'PixelDepth'

      String labelString = null;
      String value = null;

      if (type == VALUE) {
        labelString = in.readByteToString(length);
        skipPadding();
        skipPadding();
        int skip = in.readInt(); // equal to '%%%%' / 623191333
        skipPadding();
        int n = in.readInt();
        skipPadding();
        int dataType = in.readInt();
        String sb = labelString;
        if (sb.length() > 32) {
          sb = sb.substring(0, 20) + "... (" + sb.length() + ")";
        }
        LOGGER.debug("{}{}: n={}, dataType={}, label={}",
          new Object[] {indent, i, n, dataType, sb});
        if (skip != 623191333) LOGGER.warn("Skip mismatch: {}", skip);
        if (n == 1) {
          if ("Dimensions".equals(parent) && labelString.length() == 0) {
            if (adjustEndianness) in.order(!in.isLittleEndian());
            if (i == 0) {
              core.get(0).sizeX = in.readInt();
            }
            else if (i == 1) core.get(0).sizeY = in.readInt();
            else if (i == 2) {
              core.get(0).sizeZ = in.readInt();
            }
            if (adjustEndianness) in.order(!in.isLittleEndian());
          }
          else value = String.valueOf(readValue(dataType));
        }
        else if (n == 2) {
          if (dataType == 18) { // this should always be true
            length = in.readInt();
          }
          else LOGGER.warn("dataType mismatch: {}", dataType);
          value = in.readString(length);
        }
        else if (n == 3) {
          if (dataType == GROUP) {  // this should always be true
            skipPadding();
            dataType = in.readInt();
            long dataLength = 0;
            if (version == 4) {
              dataLength = in.readLong();
            }
            else {
              dataLength = in.readInt();
            }
            length = (int) (dataLength & 0xffffffff);
            if (labelString.equals("Data")) {
              if (dataLength > 0) {
                pixelOffset = in.getFilePointer();
                in.seek(in.getFilePointer() + getNumBytes(dataType) * dataLength);
                numPixelBytes = in.getFilePointer() - pixelOffset;
              }
            }
            else {
              if (dataType == 10) in.skipBytes(length);
              else value = in.readByteToString(length * 2);
            }
          }
          else LOGGER.warn("dataType mismatch: {}", dataType);
        }
        else {
          // this is a normal struct of simple types
          if (dataType == ARRAY) {
            in.skipBytes(4);
            skipPadding();
            skipPadding();
            int numFields = in.readInt();
            long startFP = in.getFilePointer();
            final StringBuilder s = new StringBuilder();
            in.skipBytes(4);
            skipPadding();
            long baseFP = in.getFilePointer();
            if (version == 4) {
              baseFP += 4;
            }
            int width = version == 4 ? 16 : 8;
            for (int j=0; j<numFields; j++) {
              in.seek(baseFP + j * width);
              dataType = in.readInt();
              in.seek(startFP + numFields * width + j * getNumBytes(dataType));
              s.append(readValue(dataType));
              if (j < numFields - 1) s.append(", ");
            }
            value = s.toString();
          }
          else if (dataType == GROUP) {
            // this is an array of structs
            skipPadding();
            dataType = in.readInt();
            if (dataType == ARRAY) { // should always be true
              in.skipBytes(4);
              skipPadding();
              skipPadding();
              int numFields = in.readInt();
              int[] dataTypes = new int[numFields];
              long baseFP = in.getFilePointer() + 12;
              for (int j=0; j<numFields; j++) {
                in.skipBytes(4);
                if (version == 4) {
                  in.seek(baseFP + j * 16);
                }
                dataTypes[j] = in.readInt();
              }
              skipPadding();
              int len = in.readInt();

              double[][] values = new double[numFields][len];

              for (int k=0; k<len; k++) {
                for (int q=0; q<numFields; q++) {
                  values[q][k] = readValue(dataTypes[q]);
                }
              }
            }
            else LOGGER.warn("dataType mismatch: {}", dataType);
          }
        }
      }
      else if (type == GROUP) {
        labelString = in.readByteToString(length);
        in.skipBytes(2);
        skipPadding();
        skipPadding();
        skipPadding();
        int num = in.readInt();
        LOGGER.debug("{}{}: group({}) {} {", new Object[] {indent, i, num, labelString});
        parseTags(num, labelString.isEmpty() ? parent : labelString, indent + "  ");
        LOGGER.debug("{}}", indent);
      }
      else if (type == 23) {
        in.skipBytes(5);
        i--;
      }
      else {
        LOGGER.debug("{}{}: unknown type: {}", new Object[] {indent, i, type});
        LOGGER.debug("  unknown type @ fp = {}", in.getFilePointer());
      }

      NumberFormat f = NumberFormat.getInstance(Locale.ENGLISH);
      if (value != null) {
        value = value.replaceAll("\0", "");
        addGlobalMeta(labelString, value);

        if (parent != null &&
          (parent.equals("AnnotationGroupList") ||
          parent.equals("DocumentObjectList")))
        {
          // ROI found
          ROIShape shape = new ROIShape();
          if (labelString.equals("AnnotationType")) {
            shape.type = DataTools.parseDouble(value).intValue();
            shapes.add(shape);
          }
          else if (shapes.size() > 0) {
            shape = shapes.get(shapes.size() - 1);
          }

          if (labelString.equals("Rectangle")) {
            String[] points = value.split(",");
            shape.y1 = DataTools.parseDouble(points[0].trim());
            shape.x1 = DataTools.parseDouble(points[1].trim());
            shape.y2 = DataTools.parseDouble(points[2].trim());
            shape.x2 = DataTools.parseDouble(points[3].trim());
          }
          else if (labelString.equals("Text")) {
            shape.text = value;
          }
          else if (labelString.equals("ForegroundColor")) {
            String[] colors = value.split(",");
            int red = DataTools.parseDouble(colors[0].trim()).intValue() & 0xff;
            int green = DataTools.parseDouble(colors[1].trim()).intValue() & 0xff;
            int blue = DataTools.parseDouble(colors[2].trim()).intValue() & 0xff;
            shape.strokeColor = new Color(red, green, blue, 255);
          }
        }
        else if (parent != null && parent.equals("TextFormat")) {
          if (labelString.equals("FontSize")) {
            ROIShape shape = shapes.get(shapes.size() - 1);
            shape.fontSize = FormatTools.getFontSize(DataTools.parseDouble(value).intValue());
          }
        }
        else if (foundMontage &&
          parent != null && parent.equals("Stage Position"))
        {
          if (labelString.equals("Stage X")) {
            stageX.add(
              new Length(DataTools.parseDouble(value), UNITS.REFERENCEFRAME));
          }
          else if (labelString.equals("Stage Y")) {
            stageY.add(
              new Length(DataTools.parseDouble(value), UNITS.REFERENCEFRAME));
          }
          else if (labelString.equals("Stage Z")) {
            stageZ.add(
              new Length(DataTools.parseDouble(value), UNITS.REFERENCEFRAME));
          }
        }

        boolean validPhysicalSize = parent != null && (parent.equals("Dimension") ||
          ((pixelSizes.size() == 4 || units.size() == 4) && parent.equals("2")));
        if (labelString.equals("Scale") && validPhysicalSize) {
          if (value.indexOf(',') == -1) {
            pixelSizes.add(f.parse(value).doubleValue());
          }
        }
        else if (labelString.equals("Units") && validPhysicalSize) {
          // make sure that we don't add more units than sizes
          if (pixelSizes.size() == units.size() + 1) {
            units.add(value);
          }
        }
        else if (labelString.equals("LowLimit")) {
          signed = f.parse(value).doubleValue() < 0;
        }
        else if (labelString.equals("Acquisition Start Time (epoch)")) {
          timestamp = f.parse(value).longValue();
        }
        else if (labelString.equals("Voltage")) {
          voltage = f.parse(value).doubleValue();
        }
        else if (labelString.equals("Microscope Info")) info = value;
        else if (labelString.equals("Indicated Magnification")) {
          mag = f.parse(value).doubleValue();
        }
        else if (labelString.equals("Gamma")) {
          gamma = f.parse(value).doubleValue();
        }
        else if (labelString.startsWith("xPos")) {
          final Double number = f.parse(value).doubleValue();
          posX = new Length(number, UNITS.REFERENCEFRAME);
        }
        else if (labelString.startsWith("yPos")) {
          final Double number = f.parse(value).doubleValue();
          posY = new Length(number, UNITS.REFERENCEFRAME);
        }
        else if (labelString.startsWith("Specimen position")) {
          final Double number = f.parse(value).doubleValue();
          posZ = new Length(number, UNITS.REFERENCEFRAME);
        }
        else if (labelString.equals("Sample Time")) {
          sampleTime = f.parse(value).doubleValue();
        }
        else if (labelString.equals("DataType")) {
          int pixelType = f.parse(value).intValue();
          switch (pixelType) {
            case 1:
              core.get(0).pixelType = FormatTools.INT16;
              break;
            case 10:
              core.get(0).pixelType = FormatTools.UINT16;
              break;
            case 2:
              core.get(0).pixelType = FormatTools.FLOAT;
              break;
            case 12:
              core.get(0).pixelType = FormatTools.DOUBLE;
              break;
            case 9:
              core.get(0).pixelType = FormatTools.INT8;
              break;
            case 6:
              core.get(0).pixelType = FormatTools.UINT8;
              break;
            case 7:
              core.get(0).pixelType = FormatTools.INT32;
              break;
            case 11:
              core.get(0).pixelType = FormatTools.UINT32;
          }
        }

        value = null;
      }
    }
  }

  private double readValue(int type) throws IOException {
    switch (type) {
      case SHORT:
      case USHORT:
        return in.readShort();
      case INT:
      case UINT:
        if (adjustEndianness) in.order(!in.isLittleEndian());
        int i = in.readInt();
        if (adjustEndianness) in.order(!in.isLittleEndian());
        return i;
      case FLOAT:
        if (adjustEndianness) in.order(!in.isLittleEndian());
        float f = in.readFloat();
        if (adjustEndianness) in.order(!in.isLittleEndian());
        return f;
      case DOUBLE:
        if (adjustEndianness) in.order(!in.isLittleEndian());
        double dbl = in.readDouble();
        if (adjustEndianness) in.order(!in.isLittleEndian());
        return dbl;
      case BYTE:
      case UBYTE:
      case CHAR:
        return in.readByte();
      case UNKNOWN:
      case UNKNOWN2:
        if (adjustEndianness) in.order(!in.isLittleEndian());
        long l = in.readLong();
        if (adjustEndianness) in.order(!in.isLittleEndian());
        return l;
    }
    return 0;
  }

  private int getNumBytes(int type) {
    switch (type) {
      case SHORT:
      case USHORT:
        return 2;
      case INT:
      case UINT:
      case FLOAT:
        return 4;
      case DOUBLE:
        return 8;
      case BYTE:
      case UBYTE:
      case CHAR:
        return 1;
      case UNKNOWN:
      case UNKNOWN2:
        return 8;
    }
    return 0;
  }

  private void skipPadding() throws IOException {
    if (version == 4) {
      in.skipBytes(4);
    }
  }

  private Unit<Length> convertUnits(String units) {
    Collator c = Collator.getInstance(Locale.ENGLISH);
    if (units != null) {
      if (c.compare("nm", units) == 0) {
        return UNITS.NANOMETER;
      } else if (c.compare("um", units) != 0 && c.compare("µm", units) != 0) {
        LOGGER.warn("Not adjusting for unknown units: {}", units);
      }
    }
    return UNITS.MICROMETER;
  }

  /**
   * Create an empty ROI and link it to the Image.
   * @param store MetadataStore in which to create the ROI
   * @param index ROI index
   * @return corresponding Shape ID to be used with the new ROI
   */
  private String createROI(MetadataStore store, int index) {
    String roi = MetadataTools.createLSID("ROI", index);
    store.setROIID(roi, index);
    store.setImageROIRef(roi, 0, index);

    return MetadataTools.createLSID("Shape", index, 0);
  }

  class ROIShape {
    public int type;
    public double x1;
    public double y1;
    public double x2;
    public double y2;
    public String text;
    public Length fontSize;
    public Color strokeColor;
  }

}
