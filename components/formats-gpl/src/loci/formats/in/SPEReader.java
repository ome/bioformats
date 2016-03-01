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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

/**
 * SPEReader is the file format reader for Princeton Instruments SPE .spe files.
 * XML Footer introduced in Princeton Instruments SPE Ver 3.0 not supported
 *
 * See public specification document:
 * ftp://ftp.princetoninstruments.com/public/Manuals/Princeton%20Instruments/SPE%203.0%20File%20Format%20Specification.pdf
 *
 * @author David Gault d.gault at dundee.ac.uk
 */
public class SPEReader extends FormatReader {

  // -- Constants --
  public static final int FLOAT = 0;
  public static final int INT32 = 1;
  public static final int INT16 = 2;
  public static final int UNINT16 = 3;
  public static final int UNINT32 = 4;
  
  public static final List<SpeHeaderEntry> coreMetaData = Arrays.asList(SpeHeaderEntry.DATATYPE, 
      SpeHeaderEntry.HEIGHT, SpeHeaderEntry.WIDTH, SpeHeaderEntry.NUM_FRAMES, 
      SpeHeaderEntry.XML_OFFSET, SpeHeaderEntry.HEADER_VER); 
  
  // -- Fields --
  private SpeHeader header;

  // -- Constructor --
  /** Constructs a new SPE reader. */
  public SPEReader() {
    super("Princeton Instruments SPE", "spe");
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 4;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return true;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (header != null) {
      FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
      
      long planeSize = FormatTools.getPlaneSize(this);
      long offset = header.getHeaderSize() + no * planeSize;

      if (offset + planeSize <= in.length() && offset >= 0) {
        in.seek(offset);
        readPlane(in, x, y, w, h, buf);
      }
    }
    else {
      throw new FormatException("Header file not found.");
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      header = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    CoreMetadata m = core.get(0);
    
    byte[] h = new byte[SpeHeader.headerSize];
    in.read(h, 0, h.length);
    header = new SpeHeader(h);
    
    int speType = header.getShort(SpeHeaderEntry.DATATYPE);
    switch (speType) {
      case FLOAT:
        m.pixelType = FormatTools.FLOAT;
        break;
      case INT32:
        m.pixelType = FormatTools.INT32;
        break;
      case INT16:
        m.pixelType = FormatTools.INT16;
        break;
      case UNINT16:
        m.pixelType = FormatTools.UINT16;
        break;
      case UNINT32:
        m.pixelType = FormatTools.UINT32;
        break;
      default:
        throw new FormatException("Invalid pixel type");
    }

    int numFrames = header.getInt(SpeHeaderEntry.NUM_FRAMES);
    if (numFrames < 1 && header.getStackSize() >= 1) {
      numFrames = header.getStackSize();
    }
    
    m.sizeX = header.getShort(SpeHeaderEntry.WIDTH);
    m.sizeY = header.getShort(SpeHeaderEntry.HEIGHT);
    m.sizeZ = 1;
    m.sizeC = 1;
    m.sizeT = numFrames;
    m.imageCount = getSizeZ() * getSizeT();
    
    m.rgb = false;
    m.indexed = false;
    m.dimensionOrder = "XYZTC";
    m.interleaved = false;
    m.littleEndian = true;
    
    in.order(isLittleEndian());
    
    int headerVer = header.getInt(SpeHeaderEntry.HEADER_VER);
    long xmlOffset = header.getLong(SpeHeaderEntry.XML_OFFSET);
   
    if (headerVer >= 3 || xmlOffset > 0) {
      LOGGER.debug("Metadata stored in Princeton Instruments SPE Version 3 Footer XML not supported");
      m.metadataComplete = false;
    }
    else {
      m.metadataComplete = true;
    }

    MetadataStore store = makeFilterMetadata();
    populateGlobalMetaData(); 
    MetadataTools.populatePixels(store, this);
    populateRoiMetaData(store);
  }

  // -- Helper methods --
  /** This function is used to populate values not contained in Core Meta Data to the Global Meta Data */
  private void populateGlobalMetaData() throws FormatException, UnsupportedEncodingException
  {
    for (SpeHeaderEntry headerEntry : SpeHeaderEntry.values()) {
      if (!coreMetaData.contains(headerEntry)) {
        switch (headerEntry.type) {
          case INT:
            int intValue = header.getInt(headerEntry);
              if (intValue > 0) {
                addGlobalMeta(headerEntry.name(), intValue);
              }
              break;
          case SHORT:
            int shortValue = header.getShort(headerEntry);
            if (shortValue > 0) {
              addGlobalMeta(headerEntry.name(), shortValue);
            }
            break;
          case LONG:
            long longValue = header.getLong(headerEntry);
            if (longValue > 0) {
              addGlobalMeta(headerEntry.name(), longValue);
            }
            break;
          case BYTE:
            int byteValue = header.getByte(headerEntry);
            if (byteValue > 0) {
              addGlobalMeta(headerEntry.name(), byteValue);
            }
            break;
          case STRING:
            String stringValue = header.getString(headerEntry);
            if (stringValue != null && stringValue.length() > 0) {
              addGlobalMeta(headerEntry.name(), stringValue);
            }
            break;
          case INTARRAY:
            int [] intArrayValue = header.getIntArray(headerEntry);
            if (intArrayValue != null && intArrayValue.length > 0) {
              addGlobalMeta(headerEntry.name(), Arrays.toString(intArrayValue));
            }
            break;
          case LONGARRAY:
            long [] longArrayValue = header.getLongArray(headerEntry);
            if (longArrayValue != null && longArrayValue.length > 0 ) {
              addGlobalMeta(headerEntry.name(), Arrays.toString(longArrayValue));
            }
            break;
          case SHORTARRAY:
            int [] shortArrayValue = header.getShortArray(headerEntry);
            if (shortArrayValue != null && shortArrayValue.length > 0) {

              addGlobalMeta(headerEntry.name(), Arrays.toString(shortArrayValue));
            }
            break;
          default: break;
        }
      }
    }
  }
  
  /** Reads and populates ROI Meta Data values. Upto 10 rectangular ROI's may be supported */
  private void populateRoiMetaData(MetadataStore store) throws FormatException 
  {
    int numROIs = header.getShort(SpeHeaderEntry.NUM_ROIS);
    if (numROIs > 0) {
      SpeROI[] rois = header.getROIs();
      int roiIndex = 0;
      for (SpeROI roi : rois) {
        String prefix = "ROI " + (roiIndex + 1);
        addGlobalMeta(prefix + " Start X", roi.getStartX());
        addGlobalMeta(prefix + " End X", roi.getEndX());
        addGlobalMeta(prefix + " Group X", roi.getGroupX());
        addGlobalMeta(prefix + " Start Y", roi.getStartY());
        addGlobalMeta(prefix + " End Y", roi.getEndY());
        addGlobalMeta(prefix + " Group Y", roi.getGroupY());

        String roiID = MetadataTools.createLSID("ROI", roiIndex);
        store.setROIID(roiID, roiIndex);
        for (int i=0; i<core.size(); i++) {
          store.setImageROIRef(roiID, i, roiIndex);
        }
        store.setLabelID(MetadataTools.createLSID("Shape", roiIndex, 0), roiIndex, 0);
        store.setLabelText(prefix + ", X-Binning = " + roi.getGroupX() + ", Y-Binning = " + roi.getGroupY(), roiIndex, 0);
        store.setLabelX((double)roi.getStartX(), roiIndex, 0);
        store.setLabelY((double)roi.getStartY(), roiIndex, 0);

        store.setRectangleID(MetadataTools.createLSID("Shape", roiIndex, 1), roiIndex, 1);
        store.setRectangleX((double)roi.getStartX(), roiIndex, 1);
        store.setRectangleY((double)roi.getStartY(), roiIndex, 1);
        store.setRectangleWidth((double)roi.getEndX() - (double)roi.getStartX(), roiIndex, 1);
        store.setRectangleHeight((double)roi.getEndY() - (double)roi.getStartY(), roiIndex, 1);

        roiIndex++;
      }
    }
  }

  // -- Private classes --
  /** SpeHeaderType is an Enum representing the available data types stored in the SPE header  */
  private enum SpeHeaderType {
    FLOAT,
    LONG,
    INT,
    SHORT,
    BYTE,
    STRING,
    LONGARRAY,
    INTARRAY,
    SHORTARRAY,
    ROIARRAY;
  }
  
  /** SpeHeaderEntry is an Enum which provides a list of all available SPE header entries along 
   * with the offset value from the top of the header in bytes and their associated data type */
  private enum SpeHeaderEntry {
    CONTROLLER_VER(0,SpeHeaderType.SHORT),
    LOGIC_OUTPUT(2,SpeHeaderType.SHORT),
    AMP_MODE(4,SpeHeaderType.SHORT),
    X_DIMENSION(6,SpeHeaderType.SHORT),
    MODE(8,SpeHeaderType.SHORT),
    EXPOSURE(10,SpeHeaderType.INT),
    VIRTUAL_XDIM(14,SpeHeaderType.SHORT),
    VIRTUAL_YDIM(16,SpeHeaderType.SHORT),
    Y_DIMENSION(18,SpeHeaderType.SHORT),
    DATE(20,SpeHeaderType.BYTE),
    VIRTUAL_CHIP(30,SpeHeaderType.SHORT),
    NOSCAN(34,SpeHeaderType.SHORT),
    DETECTOR_TEMP(36,SpeHeaderType.INT),
    DETECTOR_TYPE(40,SpeHeaderType.SHORT),
    WIDTH(42,SpeHeaderType.SHORT),
    TRIGGER_DIODE(44,SpeHeaderType.SHORT),
    DELAY_TIME(46,SpeHeaderType.INT),
    SHUTTER_CTRL(50,SpeHeaderType.SHORT),
    ABSORB_LIVE(52,SpeHeaderType.SHORT),
    ABSORB_MODE(54,SpeHeaderType.SHORT),
    CAN_VRTL_CHIP(56,SpeHeaderType.SHORT),
    THRESHOLD_MIN_LIVE(58,SpeHeaderType.SHORT),
    THRESHOLD_MIN_VAL(60,SpeHeaderType.INT),
    THRESHOLD_MAX_LIVE(64,SpeHeaderType.SHORT),
    THRESHOLD_MAX_VAL(66,SpeHeaderType.INT),
    AUTO_SPECTRO(70,SpeHeaderType.SHORT),
  
    SPEC_CENTER_WAVELEN(72,SpeHeaderType.INT),
    SPEC_GLUE_FLAG(76,SpeHeaderType.SHORT),
    SPEC_GLUE_START(78,SpeHeaderType.INT),
    SPEC_GLUE_END(82,SpeHeaderType.INT),
    SPEC_GLUE_MIN_OVRLP(86,SpeHeaderType.INT),
    SPEC_GLUE_FINAL_RES(90,SpeHeaderType.INT),
    PULSAR_TYPE(94,SpeHeaderType.SHORT),
    CHIP_FLAG(96,SpeHeaderType.SHORT),
    X_PRE_PIXELS(98,SpeHeaderType.SHORT),
    X_POST_PIXELS(100,SpeHeaderType.SHORT),
    Y_PRE_PIXELS(102,SpeHeaderType.SHORT),
    Y_POST_PIXELS(104,SpeHeaderType.SHORT),
    ASYNCH(106,SpeHeaderType.SHORT),
    DATATYPE(108,SpeHeaderType.SHORT),
    PULSER_MODE(110,SpeHeaderType.SHORT),
    PULSER_CHIP_ACCUMS(112,SpeHeaderType.SHORT),
    PULSE_REP_EXP(114,SpeHeaderType.INT),
    PULSE_REP_WIDTH(118,SpeHeaderType.INT),
    PULSE_REP_DELAY(122,SpeHeaderType.INT),
    PULSE_START_WIDTH(126,SpeHeaderType.INT),
    PULSE_END_WIDTH(130,SpeHeaderType.INT),
    PULSE_START_DELAY(134,SpeHeaderType.INT),
    PULSE_END_DELAY(138,SpeHeaderType.INT),
    PULSE_INC_MODE(142,SpeHeaderType.SHORT),
    PI_MAX_USED(144,SpeHeaderType.SHORT),
    PI_MAX_MODE(146,SpeHeaderType.SHORT),
    PI_MAX_GAIN(148,SpeHeaderType.SHORT),
    BCKGRND_SUB(150,SpeHeaderType.SHORT),
    PI_MAX_2NS_BRD(152,SpeHeaderType.SHORT),
    MIN_BLK(154,SpeHeaderType.SHORT),
    NUM_IN_BLK(156,SpeHeaderType.SHORT),
    SPEC_MIRR_LOC(158,SpeHeaderType.SHORTARRAY),
    SPEC_SLIT_LOC(162,SpeHeaderType.SHORTARRAY),

    CUS_TIMING_FLAG(170,SpeHeaderType.SHORT),
    EXP_TIME_LOCAL(172,SpeHeaderType.STRING),
    EXP_TIME_UTC(179,SpeHeaderType.STRING),
    EXPOSURE_UNITS(186,SpeHeaderType.SHORT),
    ADC_OFFSET(188,SpeHeaderType.SHORT),
    ADC_RATE(190,SpeHeaderType.SHORT),
    ADC_TYPE(192,SpeHeaderType.SHORT),
    ADC_RESOLUTION(194,SpeHeaderType.SHORT),
    ADC_BIT_ADJUST(196,SpeHeaderType.SHORT),
    GAIN(198,SpeHeaderType.SHORT),
    COMMENTS(200,SpeHeaderType.STRING),
    GEOMETRIC(600,SpeHeaderType.SHORT),
    X_LABEL(602,SpeHeaderType.STRING),
    CLEANS(618,SpeHeaderType.SHORT),
    LFLOAT(620,SpeHeaderType.SHORT),
    SPEC_MIRROR_POS(622,SpeHeaderType.SHORTARRAY),
    SPEC_SLIT_POS(626,SpeHeaderType.INTARRAY),
    AUTO_CLEAN(642,SpeHeaderType.SHORT),
    CONT_CLEAN(644,SpeHeaderType.SHORT),
    ABSORB_STRIP_NUM(646,SpeHeaderType.SHORT),
    SPEC_SLIT_POS_UNITS(648,SpeHeaderType.SHORT),
    SPEC_GROOVES(650,SpeHeaderType.INT),
    SOURCE_COMP(654,SpeHeaderType.SHORT),
    HEIGHT(656,SpeHeaderType.SHORT),
    SCRAMBLE(658,SpeHeaderType.SHORT),
    LEXPOS(660,SpeHeaderType.SHORT),
    EXT_TRIGGER(662,SpeHeaderType.SHORT),
    LNOSCAN(664,SpeHeaderType.INT),
    ACCUMULATIONS(668,SpeHeaderType.INT),
    READOUT_TIME(672,SpeHeaderType.INT),
    TRIGGER_MODE(676,SpeHeaderType.SHORT),
    XML_OFFSET(678,SpeHeaderType.LONG),
    VERSION(688,SpeHeaderType.STRING),
    TYPE(704,SpeHeaderType.SHORT),
  
    FLAT_FIELD(706,SpeHeaderType.SHORT),
    KINETIC_TRIGGER(724,SpeHeaderType.SHORT),
    DATA_LABEL(726,SpeHeaderType.STRING),
    SPARE4(742,SpeHeaderType.STRING),
    PULSE_FILENAME(1178,SpeHeaderType.STRING),
    ABSORB_FILENAME(1298,SpeHeaderType.STRING),
    EXP_REPEATS(1418,SpeHeaderType.INT),
    EXP_ACCUMS(1422,SpeHeaderType.INT),
    YT_FLAG(1426,SpeHeaderType.SHORT),
    VERT_CLOCK_SPEED(1428,SpeHeaderType.INT),
    HW_ACCUM(1432,SpeHeaderType.SHORT),
    STORE_SYNC(1434,SpeHeaderType.SHORT),
    BLEMISH_APPLIED(1436,SpeHeaderType.SHORT),
    COSMIC_APPLIED(1438,SpeHeaderType.SHORT),
    COSMIC_TYPE(1440,SpeHeaderType.SHORT),
    COSMIC_THRESHOLD(1442,SpeHeaderType.INT),
    NUM_FRAMES(1446,SpeHeaderType.INT),
    MAX_INTENSITY(1450,SpeHeaderType.INT),
    MIN_INTENSITY(1454,SpeHeaderType.INT),
    Y_LABEL(1458,SpeHeaderType.STRING),
    SHUTTER_TYPE(1474,SpeHeaderType.SHORT),
    SHUTTER_COMP(1476,SpeHeaderType.INT),
    READOUT_MODE(1480,SpeHeaderType.SHORT),
    WINDOW_SIZE(1482,SpeHeaderType.SHORT),
    CLOCK_SPEED(1484,SpeHeaderType.SHORT),
    INTERFACE_TYPE(1486,SpeHeaderType.SHORT),
    NUM_EXP_ROIS(1488,SpeHeaderType.SHORT),
    CONTROLLER_NUM(1506,SpeHeaderType.SHORT),
    SOFTWARE(1508,SpeHeaderType.SHORT),
    NUM_ROIS(1510,SpeHeaderType.SHORT),
    ROI_BEGIN(1512,SpeHeaderType.ROIARRAY),
  
    FLAT_FIELD_FILE(1632,SpeHeaderType.STRING),
    BACKGROUND_FILE(1752,SpeHeaderType.STRING),
    BLEMISH_FILE(1872,SpeHeaderType.STRING),
    HEADER_VER(1992, SpeHeaderType.INT),
    YT_INFO(1996, SpeHeaderType.STRING),
    WINVIEW_ID(2996, SpeHeaderType.INT),
  
    //X Calibration Structure
    X_SCALING_OFFSET(3000,SpeHeaderType.LONG),
    X_SCALING_FACTOR(3008,SpeHeaderType.LONG),
    X_SCALING_UNIT(3016,SpeHeaderType.BYTE),
    X_RESERVED(3017,SpeHeaderType.BYTE),
    X_SPECIAL_STRING(3018,SpeHeaderType.STRING),
    X_RESERVED2(3058,SpeHeaderType.STRING),
    X_CALIB_VALID(3098,SpeHeaderType.BYTE),
    X_INPUT_UNIT(3099,SpeHeaderType.BYTE),
    X_POLYNUM_UNIT(3100,SpeHeaderType.BYTE),
    X_POLYNUM_ORDER(3101,SpeHeaderType.BYTE),
    X_CALIB_COUNT(3102,SpeHeaderType.BYTE),
    X_PIXEL_POSITION(3103,SpeHeaderType.BYTE),
    X_CALIB_VALUE(3183,SpeHeaderType.LONGARRAY),
    X_POLYNUM_COEFF(3263,SpeHeaderType.LONGARRAY),
    X_LASER_POS(3311,SpeHeaderType.LONGARRAY),
    X_RESERVED3(3319,SpeHeaderType.LONG),
    X_CALIB_FLAG(3320,SpeHeaderType.BYTE),
    X_CALIB_LABEL(3321,SpeHeaderType.STRING),
    X_EXPANSION(3402,SpeHeaderType.STRING),
  
    //Y Calibration Structure
    Y_SCALING_OFFSET(3489,SpeHeaderType.LONG),
    Y_SCALING_FACTOR(3497,SpeHeaderType.LONG),
    Y_SCALING_UNIT(3505,SpeHeaderType.BYTE),
    Y_RESERVED(3506,SpeHeaderType.STRING),
    Y_SPECIAL_STRING(3507,SpeHeaderType.STRING),
    Y_RESERVED2(3547,SpeHeaderType.BYTE),
    Y_CALIB_VALID(3587,SpeHeaderType.BYTE),
    Y_INPUT_UNIT(3588,SpeHeaderType.BYTE),
    Y_POLYNUM_UNIT(3589,SpeHeaderType.BYTE),
    Y_POLYNUM_ORDER(3590,SpeHeaderType.BYTE),
    Y_CALIB_COUNT(3591,SpeHeaderType.BYTE),
    Y_PIXEL_POSITION(3592,SpeHeaderType.BYTE),
    Y_CALIB_VALUE(3672,SpeHeaderType.LONGARRAY),
    Y_POLYNUM_COEFF(3752,SpeHeaderType.LONGARRAY),
    Y_LASER_POS(3800,SpeHeaderType.LONGARRAY),
    Y_RESERVED3(3808,SpeHeaderType.LONG),
    Y_CALIB_FLAG(3809,SpeHeaderType.BYTE),
    Y_CALIB_LABEL(3810,SpeHeaderType.STRING),
    Y_EXPANSION(3891,SpeHeaderType.STRING),
  
    //End Calibration Structure
    INTENSITY_STRING(3978,SpeHeaderType.STRING),
    SPARE6(4018,SpeHeaderType.STRING),
    SPEC_TYPE(4043,SpeHeaderType.BYTE),
    SPEC_MODEL(4044,SpeHeaderType.BYTE),
    PULSE_BURST_USED(4045,SpeHeaderType.BYTE),
    PULSE_BURST_COUNT(4046,SpeHeaderType.INT),
    PULSE_BURST_PERIOD(4050,SpeHeaderType.LONG),
    PULSE_BRACKET_USED(4058,SpeHeaderType.BYTE),
    PULSE_BRACKET_TYPE(4059,SpeHeaderType.BYTE),
    PULSE_TIMECONST_FAST(4060,SpeHeaderType.LONG),
    PULSE_AMP_FAST(4068,SpeHeaderType.LONG),
    PULSE_TIMECONST_SLOW(4076,SpeHeaderType.LONG),
    PULSE_AMP_SLOW(4084,SpeHeaderType.LONG),
    ANALOG_GAIN(4092,SpeHeaderType.SHORT),
    AV_GAIN_USED(4094,SpeHeaderType.SHORT),
    AV_GAIN(4096,SpeHeaderType.SHORT),
    LAST_VALUE(4098,SpeHeaderType.SHORT);
  
    private final int offset;  
    private final SpeHeaderType type; 
  
    /** Constructor for SpeHeaderEntry 
     * @param entryOffset The number of bytes by which the header entry is offset from the start of the header
     * @param entryType   The expected datatype to be parsed and read at this header entry location*/
    SpeHeaderEntry(int entryOffset, SpeHeaderType entryType) {
      this.offset = entryOffset;
      this.type = entryType;
    }
  }

  /** Private class to represent the data for a single ROI in the SPE format */
  private class SpeROI {
    private int startX;
    private int endX;
    private int groupX;
    private int startY;
    private int endY;
    private int groupY;
  
    /** Constructor for SpeROI objects 
     * @param xStart  The starting X axis location for the ROI(the top leftmost point)
     * @param xEnd    The ending X axis location for the ROI(the top rightmost point) 
     * @param xGroup  The binning or grouping value used along the X axis
     * @param yStart  The starting Y axis location for the ROI(the top leftmost point)
     * @param yEnd    The ending Y axis location for the ROI(the bottom leftmost point) 
     * @param yGroup  The binning or grouping value used along the Y axis */
    public SpeROI(int xStart, int xEnd, int xGroup, int yStart, int yEnd, int yGroup) {
      startX = xStart;
      endX = xEnd;
      groupX = xGroup;
      startY = yStart;
      endY = yEnd;
      groupY = yGroup;
    }
  
    /** @return The starting X axis location for the ROI(the top leftmost point) */
    public int getStartX() {
      return startX;
    }
  
    /** @return The ending X axis location for the ROI(the top rightmost point) */
    public int getEndX() {
      return endX;
    }
    
    /** @return The binning or grouping value used along the X axis */
    public int getGroupX() {
      return groupX;
    }
  
    /** @return The starting Y axis location for the ROI(the top leftmost point) */
    public int getStartY() {
      return startY;
    }
  
    /** @return The ending Y axis location for the ROI(the bottom leftmost point) */
    public int getEndY() {
      return endY;
    }
  
    /** @return The binning or grouping value used along the Y axis */
    public int getGroupY() {
      return groupY;
    }
  }

  /** SpeHeader holds a byte array containing the SPE binary data header along
   *  with associated methods for reading SPE Header Entry values in various formats*/
  private class SpeHeader {
    private static final int headerSize = 4100;

    private byte[] header;

    /** Constructor for SpeHeader
     *  @param  header  The binary data header read from the associated SPE file  */
    public SpeHeader(byte[] header) {
      this.header = header;
    }

    /** @return  The raw binary data header  */
    public byte[] getHeader() {
      return header;
    }

    /** @param  header  The binary data header to be used */
    public void setHeader(byte[] header) {
      this.header = header;
    }
  
    /** @return  The expected size of the SPE data header */
    public int getHeaderSize() {
      return headerSize;
    }

    /** 
     *  Method used to retrieve data values for Byte entries into the SPE header table
     *  @param  entry  The header entry to read
     *  @return The value read at the given header entry location
     *  @throws FormatException Thrown if the value at the given header entry location is not of type Byte */
    public int getByte(SpeHeaderEntry entry) throws FormatException {
      if (entry.type != SpeHeaderType.BYTE) {
        throw new FormatException("Attempted to read Spe Header Entry " + entry.name() + " as Byte. Expected data type was " + entry.type.name());
      }
      return getByte(entry.offset);
    }
  
    /** 
     *  Private method used to retrieve a Byte value from the SPE header
     *  @param  index  The index of the expected value in the header array
     *  @return The value read at the given header entry location */
    private int getByte(int index) {
      int value = header[index];
      if (value < 0) value += 256;
      return value;
    }
  
    /** 
     *  Private method used to set a Byte value in the SPE header
     *  @param  index   The starting index in the header array in which to write the value
     *  @param  value   The Byte value to be written to the SPE header */
    private boolean setByte(int index, int value) {
      if (value >= 0 && value < 128) {
        header[index] = (byte)value;
        return true;
      } else if (value >= 128 && value <256) {
        header[index] = (byte)(value - 256);
        return true;
      }
      return false;
    }
  
    /** 
     *  Method used to retrieve data values for Short entries into the SPE header table
     *  @param  entry  The header entry to read
     *  @return The value read at the given header entry location
     *  @throws FormatException Thrown if the value at the given header entry location is not of type Short */
    public int getShort(SpeHeaderEntry entry) throws FormatException {
      if (entry.type != SpeHeaderType.SHORT) {
        throw new FormatException("Attempted to read Spe Header Entry " + entry.name() + " as Short. Expected data type was " + entry.type.name());
      }
      return getShort(entry.offset);
    }
  
    /** 
     *  Private method used to retrieve a Short value from the SPE header
     *  @param  index  The index of the expected value in the header array
     *  @return The value read at the given header entry location */
    private int getShort(int index) {
      int b1 = getByte(index);
      int b2 = getByte(index + 1);
      return ((b2 << 8) | b1);
    }

    /** 
     *  Private method used to set a Short value in the SPE header
     *  @param  index   The starting index in the header array in which to write the value
     *  @param  value   The Short value to be written to the SPE header */
    private boolean setShort(int index, int value) {
      if (value >= -32768 && value < 32768) {
        int b1 = value & 0xff;
        int b2 = (value >> 8) & 0xff;
        setByte(index, b1);
        setByte(index + 1, b2);
        return true;
      } else {
        return false;
      }
    }
  
    /** 
     *  Method used to retrieve data values for Integer entries into the SPE header table
     *  @param  entry  The header entry to read
     *  @return The value read at the given header entry location
     *  @throws FormatException Thrown if the value at the given header entry location is not of type Integer */
    public int getInt(SpeHeaderEntry entry) throws FormatException {
      if (entry.type != SpeHeaderType.INT) {
        throw new FormatException("Attempted to read Spe Header Entry " + entry.name() + " as Int. Expected data type was " + entry.type.name());
      }
      return getInt(entry.offset);
    }
  
    /** 
     *  Private method used to retrieve an Integer value from the SPE header
     *  @param  index  The index of the expected value in the header array
     *  @return The value read at the given header entry location */
    private int getInt(int index) {
      return DataTools.bytesToInt(header, index, true);
    }
  
    /** 
     *  Private method used to set an Integer value in the SPE header
     *  @param  index   The starting index in the header array in which to write the value
     *  @param  value   The Integer value to be written to the SPE header */
    private void setInt(int index, int value) {
      byte[] intValue = DataTools.intToBytes(value, true);
      for (int i = 0; i < intValue.length; i++) {
        setByte(index + i, intValue[i]);
      }
    }
    
    /** 
     *  Method used to retrieve data values for Long enteries into the SPE header table
     *  @param  entry  The header entry to read
     *  @return The value read at the given header entry location
     *  @throws FormatException Thrown if the value at the given header entry location is not of type Long */
    public long getLong(SpeHeaderEntry entry) throws FormatException {
      if (entry.type != SpeHeaderType.LONG) {
        throw new FormatException("Attempted to read Spe Header Entry " + entry.name() + " as Long. Expected data type was " + entry.type.name());
      }
      return getLong(entry.offset);
    }
  
    /** 
     *  Private method used to retrieve a Long value from the SPE header
     *  @param  index  The index of the expected value in the header array
     *  @return The value read at the given header entry location */
    private long getLong(int index) {
      return DataTools.bytesToLong(header, index, true);
    }

    /** 
     *  Private method used to set a Long value in the SPE header
     *  @param  index   The starting index in the header array in which to write the value
     *  @param  value   The Long value to be written to the SPE header */
    private void setLong(int index, int value) {
      byte[] longValue = DataTools.longToBytes(value, true);
      for (int i = 0; i < longValue.length; i++) {
        setByte(index + i, longValue[i]);
      }
    }
  
    /** 
     *  Method used to retrieve data values for String entries into the SPE header table
     *  @param  entry  The header entry to read
     *  @return The value read at the given header entry location
     *  @throws FormatException Thrown if the value at the given header entry location is not of type String or if the length of the String cannot be determined 
     *  @throws UnsupportedEncodingException  Thrown if the String value found in the header location is not in UTF-8 encoding*/
    public String getString(SpeHeaderEntry entry) throws FormatException, UnsupportedEncodingException {
      if (entry.type != SpeHeaderType.STRING) {
        throw new FormatException("Attempted to read Spe Header Entry " + entry.name() + " as String. Expected data type was " + entry.type.name());
      }
      SpeHeaderEntry nextEntry = SpeHeaderEntry.values()[entry.ordinal() + 1];
      if (nextEntry == null ) {
        throw new FormatException("Could not determind length of String value for " + entry.name());
      }
      int length = nextEntry.offset - entry.offset;
      return getString(entry.offset, length);
    }
  
    /** 
     *  Private method used to retrieve a String value from the SPE header
     *  @param  index   The starting index of the expected value in the header array
     *  @param  length  The length of the String to be read from the array
     *  @return The String value read at the given header entry location 
     *  @throws UnsupportedEncodingException Thrown if the String read at the given location is not in UTF-8 encoding*/
    private String getString(int index, int length) throws UnsupportedEncodingException {
      byte [] byteValues = new byte [length];
      for (int i=0; i<length; i++) {
        byteValues[i] = header[index + i];
      }
      String returnString = new String(byteValues, Constants.ENCODING);
      return returnString.trim();
    }
  
    /** 
     *  Private method used to set a String value in the SPE header
     *  @param  index   The starting index in the header array in which to write the String
     *  @param  string  The String to be written to the SPE header */
    private void setString(int index, String string) {
      for (int i=0; i<string.length(); i++) {
        setByte(index + i, (int)string.charAt(i));
      }
    }
    
    /** 
     *  Method used to retrieve data values for Integer Array entries into the SPE header table
     *  @param  entry  The header entry to read
     *  @return The array of Integer values read at the given header entry location. Returns null if all values read are 0.
     *  @throws FormatException Thrown if the value at the given header entry location is not of type Integer Array */
    public int[] getIntArray(SpeHeaderEntry entry) throws FormatException {
      if (entry.type != SpeHeaderType.INTARRAY) {
        throw new FormatException("Attempted to read Spe Header Entry " + entry.name() + " as Int Array. Expected data type was " + entry.type.name());
      }
      SpeHeaderEntry nextEntry = SpeHeaderEntry.values()[entry.ordinal() + 1];
      if (nextEntry == null ) {
        throw new FormatException("Could not determind length of Int Array for " + entry.name());
      }
      int length = nextEntry.offset - entry.offset;
      int arraySize = length/4;
      int [] returnArray = new int [arraySize];
      int offset = entry.offset;
      boolean isEmpty = true;
      for (int i = 0; i < arraySize; i++) {
        int value = getInt(offset);
        if (value > 0) {
          isEmpty = false;
        }
        returnArray[i] = value;
        offset += 4;
      }
      if (isEmpty) {
        returnArray = null;
      }
      return returnArray;
    }

    /** 
     *  Method used to retrieve data values for Short Array entries into the SPE header table
     *  @param  entry  The header entry to read
     *  @return The array of Short values read at the given header entry location. Returns null if all values read are 0.
     *  @throws FormatException Thrown if the value at the given header entry location is not of type Short Array */
    public int[] getShortArray(SpeHeaderEntry entry) throws FormatException {
      if (entry.type != SpeHeaderType.SHORTARRAY) {
        throw new FormatException("Attempted to read Spe Header Entry " + entry.name() + " as Short Array. Expected data type was " + entry.type.name());
      }
      SpeHeaderEntry nextEntry = SpeHeaderEntry.values()[entry.ordinal() + 1];
      if (nextEntry == null ) {
        throw new FormatException("Could not determind length of Short Array for " + entry.name());
      }
      int length = nextEntry.offset - entry.offset;
      int arraySize = length/2;
      int [] returnArray = new int [arraySize];
      int offset = entry.offset;
      boolean isEmpty = true;
      for (int i = 0; i < arraySize; i++) {
        int value = getShort(offset);
        if (value > 0) {
          isEmpty = false;
        }
        returnArray[i] = value;
        offset += 2;
      }
      if (isEmpty) {
        returnArray = null;
      }
      return returnArray;
    }
  
    /** 
     *  Method used to retrieve data values for Long Array entries into the SPE header table
     *  @param  entry  The header entry to read
     *  @return The array of Long values read at the given header entry location. Returns null if all values read are 0.
     *  @throws FormatException Thrown if the value at the given header entry location is not of type Long Array */
    public long[] getLongArray(SpeHeaderEntry entry) throws FormatException {
      if (entry.type != SpeHeaderType.LONGARRAY) {
        throw new FormatException("Attempted to read Spe Header Entry " + entry.name() + " as Long Array. Expected data type was " + entry.type.name());
      }
      SpeHeaderEntry nextEntry = SpeHeaderEntry.values()[entry.ordinal() + 1];
      if (nextEntry == null ) {
        throw new FormatException("Could not determind length of Long Array for " + entry.name());
      }
      int length = nextEntry.offset - entry.offset;
      int arraySize = length/8;
      long [] returnArray = new long [arraySize];
      int offset = entry.offset;
      boolean isEmpty = true;
      for (int i = 0; i < arraySize; i++) {
        long value = getLong(offset);
        if (value > 0) {
          isEmpty = false;
        }
        returnArray[i] = value;
        offset += 8;
      }
      if (isEmpty) {
        returnArray = null;
      }
      return returnArray;
    }
 
    /** 
     *  Method used to read and return meta data for upto 10 rectangular ROI's stored in the SPE header table
     *  @return An array of SpeROI objects, each containing meta data for a single ROI
     *  @throws FormatException Thrown if any of the values read during the call are not in the correct format */
    public SpeROI[] getROIs() throws FormatException {
      SpeROI[] roiArray = null;
      int numROIs = getShort(SpeHeaderEntry.NUM_ROIS);
      if (numROIs > 0) {
        roiArray = new SpeROI[numROIs];
        int offset = SpeHeaderEntry.ROI_BEGIN.offset;
        for (int i = 0; i < numROIs; i++) {
          int startX = getShort(offset);
          int endX = getShort(offset + 2);
          int groupX = getShort(offset + 4);
          int startY = getShort(offset + 6);
          int endY = getShort(offset + 8);
          int groupY = getShort(offset + 10);        
        
          roiArray[i] = new SpeROI(startX, endX, groupX, startY, endY, groupY);
          offset += 12;
        }
      }
      return roiArray;
    }

    /** 
     *  Method used to determined the stack size of the SPE file. This should usually be the value in the Number of Frames header entry,
     *  but other older entry values are also still supported if still in use.
     *  @return The stack size of the SPE file
     *  @throws FormatException Thrown if any of the values read during the call are not in the correct format */
    public int getStackSize() throws FormatException {
      int stripe = getShort(SpeHeaderEntry.HEIGHT); // Y Dim of raw data
      int noscan = getShort(SpeHeaderEntry.NOSCAN); // Old num scans should always be -1
      int numFrames = getInt(SpeHeaderEntry.NUM_FRAMES);
      if (stripe == 0 || noscan == 0) return numFrames;
      if (noscan == 65535) {
        int lnoscan = getInt(SpeHeaderEntry.LNOSCAN); // Number of scans early winx
        if (lnoscan == -1 || lnoscan == 0) {
          return numFrames;} 
        else {
          return lnoscan / stripe;}
      } 
      else {
        return noscan / stripe;
      }
    }
  }
}
