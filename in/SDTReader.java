//
// SDTReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.awt.image.BufferedImage;
import java.io.*;
import loci.formats.*;

/**
 * SDTReader is the file format reader for
 * Becker &amp; Hickl SPC-Image SDT files.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class SDTReader extends FormatReader {

  // -- Constants --

  protected static final short BH_HEADER_CHKSUM = 0x55aa;
  protected static final short BH_HEADER_NOT_VALID = 0x1111;
  protected static final short BH_HEADER_VALID = 0x5555;

  /** For .set files (setup only). */
  protected static final String SETUP_IDENTIFIER = "SPC Setup Script File";

  /** For normal .sdt files (setup + data). */
  protected static final String DATA_IDENTIFIER = "SPC Setup & Data File";

  /**
   * For .sdt files created automatically in Continuous Flow mode measurement
   * (no setup, only data).
   */
  protected static final String FLOW_DATA_IDENTIFIER = "SPC Flow Data File";

  /**
   * For .sdt files created using DLL function SPC_save_data_to_sdtfile
   * (no setup, only data).
   */
  protected static final String DLL_DATA_IDENTIFIER = "SPC DLL Data File";

  /**
   * For .sdt files created in FIFO mode
   * (setup, data blocks = Decay, FCS, FIDA, FILDA &amp; MCS curves
   * for each used routing channel).
   */
  protected static final String FCS_DATA_IDENTIFIER = "SPC FCS Data File";

  protected static final String X_STRING = "#SP [SP_SCAN_X,I,";
  protected static final String Y_STRING = "#SP [SP_SCAN_Y,I,";
  protected static final String T_STRING = "#SP [SP_ADC_RE,I,";
  protected static final String C_STRING = "#SP [SP_SCAN_RX,I,";

  // -- Fields --

  /** Current file. */
  protected RandomAccessStream in;

  /** Length in bytes of current file. */
  protected int fileLen;

  /** Offset to binary data. */
  protected int off;

  /** Dimensions of the current SDT file's image planes. */
  protected int width, height;

  /** Number of time bins in lifetime histogram. */
  protected int timeBins;

  /** Number of spectral channels. */
  protected int channels;

  // -- Constructor --

  /** Constructs a new SDT reader. */
  public SDTReader() { super("SPCImage Data", "sdt"); }

  // -- SDTReader API methods --

  /** Gets the number of bins in the lifetime histogram. */
  public int getTimeBinCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return timeBins;
  }

  /** Gets the number of spectral channels. */
  public int getChannelCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return channels;
  }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for an SDT file. */
  public boolean isThisType(byte[] block) { return false; }

  /** Determines the number of images in the given SDT file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return timeBins * channels;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    return false;
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    return true;
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return false;
  }

  /** Obtains the specified image from the given SDT file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= timeBins * channels) {
      throw new FormatException("Invalid image number: " + no);
    }

    int t = no % timeBins;
    int c = no / timeBins;
    in.seek(off + 2 * (width * height * timeBins * c + t));
    byte[] data = new byte[2 * width * height];

    for (int y=0; y<height; y++) {
      for (int x=0; x<width; x++) {
        int ndx = width * y + x;
        in.readFully(data, ndx, 2);
        in.skipBytes(timeBins);
      }
    }
    return data;
  }

  /** Obtains the specified image from the given SDT file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    return ImageTools.makeImage(openBytes(id, no),
      width, height, 1, false, 2, true);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given SDT file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessStream(id);
    in.order(true);

    // read file header

    // software revision number (lower 4 bits >= 10(decimal))
    short revision = in.readShort();

    // offset of the info part which contains general text
    // information (Title, date, time, contents etc.)
    int infoOffs = in.readInt();

    // length of the info part
    short infoLength = in.readShort();

    // offset of the setup text data
    // (system parameters, display parameters, trace parameters etc.)
    int setupOffs = in.readInt();

    // length of the setup data
    short setupLength = in.readShort();

    // offset of the first data block
    int dataBlockOffs = in.readInt();

    // no_of_data_blocks valid only when in 0 .. 0x7ffe range,
    // if equal to 0x7fff  the  field 'reserved1' contains
    // valid no_of_data_blocks
    short noOfDataBlocks = in.readShort();

    // length of the longest block in the file
    int dataBlockLength = in.readInt();

    // offset to 1st. measurement description block
    // (system parameters connected to data blocks)
    int measDescBlockOffs = in.readInt();

    // number of measurement description blocks
    short noOfMeasDescBlocks = in.readShort();

    // length of the measurement description blocks
    short measDescBlockLength = in.readShort();

    // valid: 0x5555, not valid: 0x1111
    int headerValid = in.readUnsignedShort();

    // reserved1 now contains noOfDataBlocks
    long reserved1 = (0xffffffffL & in.readInt()); // unsigned

    int reserved2 = in.readUnsignedShort();

    // checksum of file header
    int chksum = in.readUnsignedShort();

    // save bhfileHeader to metadata table
    String bhfileHeader = "bhfileHeader.";
    metadata.put(bhfileHeader + "revision", new Short(revision));
    metadata.put(bhfileHeader + "infoOffs", new Integer(infoOffs));
    metadata.put(bhfileHeader + "infoLength", new Short(infoLength));
    metadata.put(bhfileHeader + "setupOffs", new Integer(setupOffs));
    metadata.put(bhfileHeader + "dataBlockOffs", new Integer(dataBlockOffs));
    metadata.put(bhfileHeader + "noOfDataBlocks", new Short(noOfDataBlocks));
    metadata.put(bhfileHeader + "dataBlockLength",
      new Integer(dataBlockLength));
    metadata.put(bhfileHeader + "measDescBlockOffs",
      new Integer(measDescBlockOffs));
    metadata.put(bhfileHeader + "noOfMeasDescBlocks",
      new Short(noOfMeasDescBlocks));
    metadata.put(bhfileHeader + "measDescBlockLength",
      new Integer(measDescBlockLength));
    metadata.put(bhfileHeader + "headerValid", new Integer(headerValid));
    metadata.put(bhfileHeader + "reserved1", new Long(reserved1));
    metadata.put(bhfileHeader + "reserved2", new Integer(reserved2));
    metadata.put(bhfileHeader + "chksum", new Integer(chksum));

    // read file info
    in.seek(infoOffs);
    byte[] infoBytes = new byte[infoLength];
    in.readFully(infoBytes);
    String info = new String(infoBytes);

    // save file info to metadata table
    metadata.put("File Info", info);
    // TODO: parse individual parameters from info string and store them

    // read setup
    in.seek(setupOffs);
    byte[] setupBytes = new byte[setupLength];
    in.readFully(setupBytes);
    String setup = new String(setupBytes);

    // save setup to metadata table
    metadata.put("Setup", setup);
    // TODO: parse individual parameters from setup string and store them

    // extract dimensional parameters from setup string
    int xIndex = setup.indexOf(X_STRING);
    if (xIndex > 0) {
      int ndx = xIndex + X_STRING.length();
      int end = setup.indexOf("]", ndx);
      width = Integer.parseInt(setup.substring(ndx, end));
    }
    int yIndex = setup.indexOf(Y_STRING);
    if (yIndex > 0) {
      int ndx = yIndex + Y_STRING.length();
      int end = setup.indexOf("]", ndx);
      height = Integer.parseInt(setup.substring(ndx, end));
    }
    int tIndex = setup.indexOf(T_STRING);
    if (tIndex > 0) {
      int ndx = tIndex + T_STRING.length();
      int end = setup.indexOf("]", ndx);
      timeBins = Integer.parseInt(setup.substring(ndx, end));
    }
    int cIndex = setup.indexOf(C_STRING);
    if (cIndex > 0) {
      int ndx = cIndex + C_STRING.length();
      int end = setup.indexOf("]", ndx);
      channels = Integer.parseInt(setup.substring(ndx, end));
    }

    // read measurement data
    if (noOfMeasDescBlocks > 0) {
      in.seek(measDescBlockOffs);

      if (measDescBlockLength >= 211) {
        byte[] timeBytes = new byte[9];
        in.readFully(timeBytes);
        String time = new String(timeBytes);

        byte[] dateBytes = new byte[11];
        in.readFully(dateBytes);
        String date = new String(dateBytes);

        byte[] modSerNoBytes = new byte[16];
        in.readFully(modSerNoBytes);
        String modSerNo = new String(modSerNoBytes);

        short measMode = in.readShort();
        float cfdLL = in.readFloat();
        float cfdLH = in.readFloat();
        float cfdZC = in.readFloat();
        float cfdHF = in.readFloat();
        float synZC = in.readFloat();
        short synFD = in.readShort();
        float synHF = in.readFloat();
        float tacR = in.readFloat();
        short tacG = in.readShort();
        float tacOF = in.readFloat();
        float tacLL = in.readFloat();
        float tacLH = in.readFloat();
        short adcRE = in.readShort();
        short ealDE = in.readShort();
        short ncx = in.readShort();
        short ncy = in.readShort();
        int page = in.readUnsignedShort();
        float colT = in.readFloat();
        float repT = in.readFloat();
        short stopt = in.readShort();
        int overfl = in.readUnsignedByte();
        short useMotor = in.readShort();
        int steps = in.readUnsignedShort();
        float offset = in.readFloat();
        short dither = in.readShort();
        short incr = in.readShort();
        short memBank = in.readShort();

        byte[] modTypeBytes = new byte[16];
        in.readFully(modTypeBytes);
        String modType = new String(modTypeBytes);

        float synTH = in.readFloat();
        short deadTimeComp = in.readShort();

        //  2 = disabled line markers
        short polarityL = in.readShort();

        short polarityF = in.readShort();
        short polarityP = in.readShort();

        // line predivider = 2 ** (linediv)
        short linediv = in.readShort();

        short accumulate = in.readShort();
        int flbckY = in.readInt();
        int flbckX = in.readInt();
        int bordU = in.readInt();
        int bordL = in.readInt();
        float pixTime = in.readFloat();
        short pixClk = in.readShort();
        short trigger = in.readShort();
        int scanX = in.readInt();
        int scanY = in.readInt();
        int scanRX = in.readInt();
        int scanRY = in.readInt();
        short fifoTyp = in.readShort();
        int epxDiv = in.readInt();
        int modTypeCode = in.readUnsignedShort();

        // new in v.8.4
        int modFpgaVer = in.readUnsignedShort();

        float overflowCorrFactor = in.readFloat();
        int adcZoom = in.readInt();

        // cycles (accumulation cycles in FLOW mode)
        int cycles = in.readInt();

        // save MeasureInfo to metadata table
        String measureInfo = "MeasureInfo.";
        metadata.put(measureInfo + "time", time);
        metadata.put(measureInfo + "date", date);
        metadata.put(measureInfo + "modSerNo", modSerNo);
        metadata.put(measureInfo + "measMode", new Short(measMode));
        metadata.put(measureInfo + "cfdLL", new Float(cfdLL));
        metadata.put(measureInfo + "cfdLH", new Float(cfdLH));
        metadata.put(measureInfo + "cfdZC", new Float(cfdZC));
        metadata.put(measureInfo + "cfdHF", new Float(cfdHF));
        metadata.put(measureInfo + "synZC", new Float(synZC));
        metadata.put(measureInfo + "synFD", new Short(synFD));
        metadata.put(measureInfo + "synHF", new Float(synHF));
        metadata.put(measureInfo + "tacR", new Float(tacR));
        metadata.put(measureInfo + "tacG", new Short(tacG));
        metadata.put(measureInfo + "tacOF", new Float(tacOF));
        metadata.put(measureInfo + "tacLL", new Float(tacLL));
        metadata.put(measureInfo + "tacLH", new Float(tacLH));
        metadata.put(measureInfo + "adcRE", new Short(adcRE));
        metadata.put(measureInfo + "ealDE", new Short(ealDE));
        metadata.put(measureInfo + "ncx", new Short(ncx));
        metadata.put(measureInfo + "ncy", new Short(ncy));
        metadata.put(measureInfo + "page", new Integer(page));
        metadata.put(measureInfo + "colT", new Float(colT));
        metadata.put(measureInfo + "repT", new Float(repT));
        metadata.put(measureInfo + "stopt", new Short(stopt));
        metadata.put(measureInfo + "overfl", new Integer(overfl));
        metadata.put(measureInfo + "useMotor", new Short(useMotor));
        metadata.put(measureInfo + "steps", new Integer(steps));
        metadata.put(measureInfo + "offset", new Float(offset));
        metadata.put(measureInfo + "dither", new Short(dither));
        metadata.put(measureInfo + "incr", new Short(incr));
        metadata.put(measureInfo + "memBank", new Short(memBank));
        metadata.put(measureInfo + "modType", modType);
        metadata.put(measureInfo + "synTH", new Float(synTH));
        metadata.put(measureInfo + "deadTimeComp", new Short(deadTimeComp));
        metadata.put(measureInfo + "polarityL", new Short(polarityL));
        metadata.put(measureInfo + "polarityF", new Short(polarityF));
        metadata.put(measureInfo + "polarityP", new Short(polarityP));
        metadata.put(measureInfo + "linediv", new Short(linediv));
        metadata.put(measureInfo + "accumulate", new Short(accumulate));
        metadata.put(measureInfo + "flbckY", new Integer(flbckY));
        metadata.put(measureInfo + "flbckX", new Integer(flbckX));
        metadata.put(measureInfo + "bordU", new Integer(bordU));
        metadata.put(measureInfo + "bordL", new Integer(bordL));
        metadata.put(measureInfo + "pixTime", new Float(pixTime));
        metadata.put(measureInfo + "pixClk", new Short(pixClk));
        metadata.put(measureInfo + "trigger", new Short(trigger));
        metadata.put(measureInfo + "scanX", new Integer(scanX));
        metadata.put(measureInfo + "scanY", new Integer(scanY));
        metadata.put(measureInfo + "scanRX", new Integer(scanRX));
        metadata.put(measureInfo + "scanRY", new Integer(scanRY));
        metadata.put(measureInfo + "fifoTyp", new Short(fifoTyp));
        metadata.put(measureInfo + "epxDiv", new Integer(epxDiv));
        metadata.put(measureInfo + "modTypeCode", new Integer(modTypeCode));
        metadata.put(measureInfo + "modFpgaVer", new Integer(modFpgaVer));
        metadata.put(measureInfo + "overflowCorrFactor",
          new Float(overflowCorrFactor));
        metadata.put(measureInfo + "adcZoom", new Integer(adcZoom));
        metadata.put(measureInfo + "cycles", new Integer(cycles));

        // extract dimensional parameters from measure info
        width = scanX;
        height = scanY;
        timeBins = adcRE;
        channels = scanRX;
      }

      if (measDescBlockLength >= 211 + 60) {
        // MeasStopInfo - information collected when measurement is finished

        // last SPC_test_state return value (status)
        int status = in.readUnsignedShort();

        // scan clocks bits 2-0 (frame, line, pixel), rates_read - bit 15
        int flags = in.readUnsignedShort();

        // time from start to  - disarm (simple measurement)
        // - or to the end of the cycle (for complex measurement)
        float stopTime = in.readFloat();

        // current step (if multi-step measurement)
        int curStep = in.readInt();

        // current cycle (accumulation cycle in FLOW mode) -
        // (if multi-cycle measurement)
        int curCycle = in.readInt();

        // current measured page
        int curPage = in.readInt();

        // minimum rates during the measurement
        float minSyncRate = in.readFloat();

        // (-1.0 - not set)
        float minCfdRate = in.readFloat();

        float minTacRate = in.readFloat();
        float minAdcRate = in.readFloat();

        // maximum rates during the measurement
        float maxSyncRate = in.readFloat();

        // (-1.0 - not set)
        float maxCfdRate = in.readFloat();

        float maxTacRate = in.readFloat();
        float maxAdcRate = in.readFloat();
        int mReserved1 = in.readInt();
        float mReserved2 = in.readFloat();

        // save MeasStopInfo to metadata table
        String measStopInfo = "MeasStopInfo.";
        metadata.put(measStopInfo + "status", new Integer(status));
        metadata.put(measStopInfo + "flags", new Integer(flags));
        metadata.put(measStopInfo + "stopTime", new Float(stopTime));
        metadata.put(measStopInfo + "curStep", new Integer(curStep));
        metadata.put(measStopInfo + "curCycle", new Integer(curCycle));
        metadata.put(measStopInfo + "curPage", new Integer(curPage));
        metadata.put(measStopInfo + "minSyncRate", new Float(minSyncRate));
        metadata.put(measStopInfo + "minCfdRate", new Float(minCfdRate));
        metadata.put(measStopInfo + "minTacRate", new Float(minTacRate));
        metadata.put(measStopInfo + "minAdcRate", new Float(minAdcRate));
        metadata.put(measStopInfo + "maxSyncRate", new Float(maxSyncRate));
        metadata.put(measStopInfo + "maxCfdRate", new Float(maxCfdRate));
        metadata.put(measStopInfo + "maxTacRate", new Float(maxTacRate));
        metadata.put(measStopInfo + "maxAdcRate", new Float(maxAdcRate));
        metadata.put(measStopInfo + "reserved1", new Integer(mReserved1));
        metadata.put(measStopInfo + "reserved2", new Float(mReserved2));
      }

      if (measDescBlockLength >= 211 + 60 + 38) {
        // MeasFCSInfo - information collected when FIFO measurement is finished

        // routing channel number
        int chan = in.readUnsignedShort();

        // bit 0 = 1 - decay curve calculated
        // bit 1 = 1 - fcs   curve calculated
        // bit 2 = 1 - FIDA  curve calculated
        // bit 3 = 1 - FILDA curve calculated
        // bit 4 = 1 - MCS curve calculated
        // bit 5 = 1 - 3D Image calculated
        int fcsDecayCalc = in.readUnsignedShort();

        // macro time clock in 0.1 ns units
        long mtResol = (0xffffffffL & in.readInt()); // unsigned

        // correlation time [ms]
        float cortime = in.readFloat();

        //  no of photons
        long calcPhotons = (0xffffffffL & in.readInt()); // unsigned

        // no of FCS values
        int fcsPoints = in.readInt();

        // macro time of the last photon
        float endTime = in.readFloat();

        // no of Fifo overruns
        // when > 0  fcs curve & endTime are not valid
        int overruns = in.readUnsignedShort();

        // 0 - linear FCS with log binning (100 bins/log)
        // when bit 15 = 1 (0x8000) - Multi-Tau FCS
        //           where bits 14-0 = ktau parameter
        int fcsType = in.readUnsignedShort();

        // cross FCS routing channel number
        //   when chan = crossChan and mod == crossMod - Auto FCS
        //        otherwise - Cross FCS
        int crossChan = in.readUnsignedShort();

        // module number
        int mod = in.readUnsignedShort();

        // cross FCS module number
        int crossMod = in.readUnsignedShort();

        // macro time clock of cross FCS module in 0.1 ns units
        long crossMtResol = (0xffffffffL & in.readInt()); // unsigned

        // save MeasFCSInfo to metadata table
        String measFCSInfo = "MeasFCSInfo.";
        metadata.put(measFCSInfo + "chan", new Integer(chan));
        metadata.put(measFCSInfo + "fcsDecayCalc", new Integer(fcsDecayCalc));
        metadata.put(measFCSInfo + "mtResol", new Long(mtResol));
        metadata.put(measFCSInfo + "cortime", new Float(cortime));
        metadata.put(measFCSInfo + "calcPhotons", new Long(calcPhotons));
        metadata.put(measFCSInfo + "fcsPoints", new Integer(fcsPoints));
        metadata.put(measFCSInfo + "endTime", new Float(endTime));
        metadata.put(measFCSInfo + "overruns", new Integer(overruns));
        metadata.put(measFCSInfo + "fcsType", new Integer(fcsType));
        metadata.put(measFCSInfo + "crossChan", new Integer(crossChan));
        metadata.put(measFCSInfo + "mod", new Integer(mod));
        metadata.put(measFCSInfo + "crossMod", new Integer(crossMod));
        metadata.put(measFCSInfo + "crossMtResol", new Float(crossMtResol));
      }

      if (measDescBlockLength >= 211 + 60 + 38 + 26) {
        // 4 subsequent fields valid only for Camera mode
        // or FIFO_IMAGE mode
        int imageX = in.readInt();
        int imageY = in.readInt();
        int imageRX = in.readInt();
        int imageRY = in.readInt();

        // gain for XY ADCs (SPC930)
        short xyGain = in.readShort();

        // use or not  Master Clock (SPC140 multi-module)
        short masterClock = in.readShort();

        // ADC sample delay (SPC-930)
        short adcDE = in.readShort();

        // detector type (SPC-930 in camera mode)
        short detType = in.readShort();

        // X axis representation (SPC-930)
        short xAxis = in.readShort();

        // save extra MeasureInfo
        String measureInfo = "MeasureInfo.";
        metadata.put(measureInfo + "imageX", new Integer(imageX));
        metadata.put(measureInfo + "imageY", new Integer(imageY));
        metadata.put(measureInfo + "imageRX", new Integer(imageRX));
        metadata.put(measureInfo + "imageRY", new Integer(imageRY));
        metadata.put(measureInfo + "xyGain", new Short(xyGain));
        metadata.put(measureInfo + "masterClock", new Short(masterClock));
        metadata.put(measureInfo + "adcDE", new Short(adcDE));
        metadata.put(measureInfo + "detType", new Short(detType));
        metadata.put(measureInfo + "xAxis", new Short(xAxis));
      }

      if (measDescBlockLength >= 211 + 60 + 38 + 26 + 24) {
        // MeasHISTInfo - extension of FCSInfo, valid only for FIFO meas
        // extension of MeasFCSInfo for other histograms (FIDA, FILDA, MCS)

        // interval time [ms] for FIDA histogram
        float fidaTime = in.readFloat();

        // interval time [ms] for FILDA histogram
        float fildaTime = in.readFloat();

        // no of FIDA values
        int fidaPoints = in.readInt();

        // no of FILDA values
        int fildaPoints = in.readInt();

        // interval time [ms] for MCS histogram
        float mcsTime = in.readFloat();

        // no of MCS values
        int mcsPoints = in.readInt();

        String measHISTInfo = "MeasHISTInfo.";
        metadata.put(measHISTInfo + "fidaTime", new Float(fidaTime));
        metadata.put(measHISTInfo + "fildaTime", new Float(fildaTime));
        metadata.put(measHISTInfo + "fidaPoints", new Integer(fidaPoints));
        metadata.put(measHISTInfo + "fildaPoints", new Integer(fildaPoints));
        metadata.put(measHISTInfo + "mcsTime", new Float(mcsTime));
        metadata.put(measHISTInfo + "mcsPoints", new Integer(mcsPoints));
      }
    }

    in.seek(dataBlockOffs);

    // number of the block in the file
    // valid only when in 0 .. 0x7ffe range, otherwise use lblock_no field
    // obsolete now, lblock_no contains full block no information
    short blockNo = in.readShort();

    // offset of the data block from the beginning of the file
    int dataOffs = in.readInt();

    // offset to the data block header of the next data block
    int nextBlockOffs = in.readInt();

    // see blockType defines below
    int blockType = in.readUnsignedShort();

    // Number of the measurement description block
    // corresponding to this data block
    short measDescBlockNo = in.readShort();

    // long blockNo - see remarks below
    long lblockNo = (0xffffffffL & in.readInt()); // unsigned

    // reserved2 now contains block (set) length
    long blockLength = (0xffffffffL & in.readInt()); // unsigned

    String bhFileBlockHeader = "BHFileBlockHeader.";
    metadata.put(bhFileBlockHeader + "blockNo", new Short(blockNo));
    metadata.put(bhFileBlockHeader + "dataOffs", new Integer(dataOffs));
    metadata.put(bhFileBlockHeader + "nextBlockOffs",
      new Integer(nextBlockOffs));
    metadata.put(bhFileBlockHeader + "blockType", new Integer(blockType));
    metadata.put(bhFileBlockHeader + "measDescBlockNo",
      new Short(measDescBlockNo));
    metadata.put(bhFileBlockHeader + "lblockNo", new Long(lblockNo));
    metadata.put(bhFileBlockHeader + "blockLength", new Long(blockLength));

    off = dataBlockOffs + 22;

    sizeX[0] = width;
    sizeY[0] = height;
    sizeZ[0] = 1;
    sizeC[0] = timeBins * channels;
    sizeT[0] = 1;
    currentOrder[0] = "XYZTC";
    pixelType[0] = FormatReader.INT16;

    MetadataStore store = getMetadataStore(id);
    store.setPixels(new Integer(getSizeX(id)), new Integer(getSizeY(id)),
      new Integer(getSizeZ(id)), new Integer(getSizeC(id)),
      new Integer(getSizeT(id)), new Integer(pixelType[0]),
      new Boolean(!isLittleEndian(id)), getDimensionOrder(id), null);
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new SDTReader().testRead(args);
  }

}
