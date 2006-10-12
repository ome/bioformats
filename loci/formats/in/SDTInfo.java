//
// SDTInfo.java
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

import java.io.IOException;
import java.util.Hashtable;
import loci.formats.RandomAccessStream;

/**
 * SDTInfo encapsulates the header information for
 * Becker &amp; Hickl SPC-Image SDT files.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class SDTInfo {

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

  protected int width, height, timeBins, channels;

  // -- Fields - File header --

  /** Software revision number (lower 4 bits &gt;= 10(decimal)). */
  protected short revision;

  /**
   * Offset of the info part which contains general text
   * information (Title, date, time, contents etc.).
   */
  protected int infoOffs;

  /** Length of the info part. */
  protected short infoLength;

  /**
   * Offset of the setup text data
   * (system parameters, display parameters, trace parameters etc.).
   */
  protected int setupOffs;

  /** Length of the setup data. */
  protected short setupLength;

  /** Offset of the first data block. */
  protected int dataBlockOffs;

  /**
   * no_of_data_blocks valid only when in 0 .. 0x7ffe range,
   * if equal to 0x7fff  the  field 'reserved1' contains
   * valid no_of_data_blocks.
   */
  protected short noOfDataBlocks;

  // length of the longest block in the file
  protected int dataBlockLength;

  // offset to 1st. measurement description block
  // (system parameters connected to data blocks)
  protected int measDescBlockOffs;

  // number of measurement description blocks
  protected short noOfMeasDescBlocks;

  // length of the measurement description blocks
  protected short measDescBlockLength;

  // valid: 0x5555, not valid: 0x1111
  protected int headerValid;

  // reserved1 now contains noOfDataBlocks
  protected long reserved1; // unsigned

  protected int reserved2;

  // checksum of file header
  protected int chksum;

  // -- Fields - File Info --

  protected String info;

  // -- Fields -- Setup --

  protected String setup;

  // -- Fields - MeasureInfo --

  protected boolean hasMeasureInfo;

  /** Time of creation. */
  protected String time;

  /** Date of creation. */
  protected String date;

  /** Serial number of the module. */
  protected String modSerNo;

  protected short measMode;
  protected float cfdLL;
  protected float cfdLH;
  protected float cfdZC;
  protected float cfdHF;
  protected float synZC;
  protected short synFD;
  protected float synHF;
  protected float tacR;
  protected short tacG;
  protected float tacOF;
  protected float tacLL;
  protected float tacLH;
  protected short adcRE;
  protected short ealDE;
  protected short ncx;
  protected short ncy;
  protected int page;
  protected float colT;
  protected float repT;
  protected short stopt;
  protected int overfl;
  protected short useMotor;
  protected int steps;
  protected float offset;
  protected short dither;
  protected short incr;
  protected short memBank;

  /** Module type. */
  protected String modType;

  protected float synTH;
  protected short deadTimeComp;

  /** 2 = disabled line markers. */
  protected short polarityL;

  protected short polarityF;
  protected short polarityP;

  /** Line predivider = 2 ** (linediv). */
  protected short linediv;

  protected short accumulate;
  protected int flbckY;
  protected int flbckX;
  protected int bordU;
  protected int bordL;
  protected float pixTime;
  protected short pixClk;
  protected short trigger;
  protected int scanX;
  protected int scanY;
  protected int scanRX;
  protected int scanRY;
  protected short fifoTyp;
  protected int epxDiv;
  protected int modTypeCode;

  /** New in v.8.4. */
  protected int modFpgaVer;

  protected float overflowCorrFactor;
  protected int adcZoom;

  /** Cycles (accumulation cycles in FLOW mode). */
  protected int cycles;

  // -- Fields - MeasStopInfo --

  protected boolean hasMeasStopInfo;

  /** Last SPC_test_state return value (status). */
  protected int status;

  /** Scan clocks bits 2-0 (frame, line, pixel), rates_read - bit 15. */
  protected int flags;

  /**
   * Time from start to  - disarm (simple measurement)
   * - or to the end of the cycle (for complex measurement).
   */
  protected float stopTime;

  /** Current step (if multi-step measurement). */
  protected int curStep;

  /**
   * Current cycle (accumulation cycle in FLOW mode) -
   * (if multi-cycle measurement).
   */
  protected int curCycle;

  /** Current measured page. */
  protected int curPage;

  /** Minimum rates during the measurement. */
  protected float minSyncRate;

  /** (-1.0 - not set). */
  protected float minCfdRate;

  protected float minTacRate;
  protected float minAdcRate;

  /** Maximum rates during the measurement. */
  protected float maxSyncRate;

  /** (-1.0 - not set). */
  protected float maxCfdRate;

  protected float maxTacRate;
  protected float maxAdcRate;
  protected int mReserved1;
  protected float mReserved2;

  // -- Fields - MeasFCSInfo --

  protected boolean hasMeasFCSInfo;

  /** Routing channel number. */
  protected int chan;

  /**
   * Bit 0 = 1 - decay curve calculated.
   * Bit 1 = 1 - fcs   curve calculated.
   * Bit 2 = 1 - FIDA  curve calculated.
   * Bit 3 = 1 - FILDA curve calculated.
   * Bit 4 = 1 - MCS curve calculated.
   * Bit 5 = 1 - 3D Image calculated.
   */
  protected int fcsDecayCalc;

  /** Macro time clock in 0.1 ns units. */
  protected long mtResol; // unsigned

  /** Correlation time [ms]. */
  protected float cortime;

  /** No of photons. */
  protected long calcPhotons; // unsigned

  /** No of FCS values. */
  protected int fcsPoints;

  /** Macro time of the last photon. */
  protected float endTime;

  /**
   * No of Fifo overruns
   * when &gt; 0  fcs curve &amp; endTime are not valid.
   */
  protected int overruns;

  /**
   * 0 - linear FCS with log binning (100 bins/log)
   * when bit 15 = 1 (0x8000) - Multi-Tau FCS
   * where bits 14-0 = ktau parameter.
   */
  protected int fcsType;

  /**
   * Cross FCS routing channel number
   * when chan = crossChan and mod == crossMod - Auto FCS
   * otherwise - Cross FCS.
   */
  protected int crossChan;

  /** Module number. */
  protected int mod;

  /** Cross FCS module number. */
  protected int crossMod;

  /** Macro time clock of cross FCS module in 0.1 ns units. */
  protected long crossMtResol; // unsigned

  // -- Fields - extended MeasureInfo -

  protected boolean hasExtendedMeasureInfo;

  /**
   * 4 subsequent fields valid only for Camera mode
   * or FIFO_IMAGE mode.
   */
  protected int imageX;
  protected int imageY;
  protected int imageRX;
  protected int imageRY;

  /** Gain for XY ADCs (SPC930). */
  protected short xyGain;

  /** Use or not  Master Clock (SPC140 multi-module). */
  protected short masterClock;

  /** ADC sample delay (SPC-930). */
  protected short adcDE;

  /** Detector type (SPC-930 in camera mode). */
  protected short detType;

  /** X axis representation (SPC-930). */
  protected short xAxis;

  // -- Fields - MeasHISTInfo --

  protected boolean hasMeasHISTInfo;

  /** Interval time [ms] for FIDA histogram. */
  protected float fidaTime;

  /** Interval time [ms] for FILDA histogram. */
  protected float fildaTime;

  /** No of FIDA values. */
  protected int fidaPoints;

  /** No of FILDA values. */
  protected int fildaPoints;

  /** Interval time [ms] for MCS histogram. */
  protected float mcsTime;

  /** No of MCS values. */
  protected int mcsPoints;

  // -- Fields - BHFileBlockHeader --

  /**
   * Number of the block in the file.
   * Valid only when in 0..0x7ffe range, otherwise use lblock_no field
   * obsolete now, lblock_no contains full block no information.
   */
  protected short blockNo;

  /** Offset of the data block from the beginning of the file. */
  protected int dataOffs;

  /** Offset to the data block header of the next data block. */
  protected int nextBlockOffs;

  /** See blockType defines below. */
  protected int blockType;

  /**
   * Number of the measurement description block
   * corresponding to this data block.
   */
  protected short measDescBlockNo;

  /** Long blockNo - see remarks below. */
  protected long lblockNo; // unsigned

  /** reserved2 now contains block (set) length. */
  protected long blockLength; // unsigned

  // -- Constructor --

  /**
   * Constructs a new SDT header by reading values from the given input source,
   * populating the given metadata table.
   */
  public SDTInfo(RandomAccessStream in, Hashtable metadata) throws IOException {
    // read bhfileHeader
    revision = in.readShort();
    infoOffs = in.readInt();
    infoLength = in.readShort();
    setupOffs = in.readInt();
    setupLength = in.readShort();
    dataBlockOffs = in.readInt();
    noOfDataBlocks = in.readShort();
    dataBlockLength = in.readInt();
    measDescBlockOffs = in.readInt();
    noOfMeasDescBlocks = in.readShort();
    measDescBlockLength = in.readShort();
    headerValid = in.readUnsignedShort();
    reserved1 = (0xffffffffL & in.readInt()); // unsigned
    reserved2 = in.readUnsignedShort();
    chksum = in.readUnsignedShort();

    // save bhfileHeader to metadata table
    if (metadata != null) {
      final String bhfileHeader = "bhfileHeader.";
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
    }

    // read file info
    in.seek(infoOffs);
    byte[] infoBytes = new byte[infoLength];
    in.readFully(infoBytes);
    info = new String(infoBytes);

    // save file info to metadata table
    if (metadata != null) metadata.put("File Info", info);
    // TODO: parse individual parameters from info string and store them

    // read setup
    in.seek(setupOffs);
    byte[] setupBytes = new byte[setupLength];
    in.readFully(setupBytes);
    setup = new String(setupBytes);

    // save setup to metadata table
    if (metadata != null) metadata.put("Setup", setup);
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

      hasMeasureInfo = measDescBlockLength >= 211;
      hasMeasStopInfo = measDescBlockLength >= 211 + 60;
      hasMeasFCSInfo = measDescBlockLength >= 211 + 60 + 38;
      hasExtendedMeasureInfo = measDescBlockLength >= 211 + 60 + 38 + 26;
      hasMeasHISTInfo = measDescBlockLength >= 211 + 60 + 38 + 26 + 24;

      if (hasMeasureInfo) {
        byte[] timeBytes = new byte[9];
        in.readFully(timeBytes);
        time = new String(timeBytes);

        byte[] dateBytes = new byte[11];
        in.readFully(dateBytes);
        date = new String(dateBytes);

        byte[] modSerNoBytes = new byte[16];
        in.readFully(modSerNoBytes);
        modSerNo = new String(modSerNoBytes);

        measMode = in.readShort();
        cfdLL = in.readFloat();
        cfdLH = in.readFloat();
        cfdZC = in.readFloat();
        cfdHF = in.readFloat();
        synZC = in.readFloat();
        synFD = in.readShort();
        synHF = in.readFloat();
        tacR = in.readFloat();
        tacG = in.readShort();
        tacOF = in.readFloat();
        tacLL = in.readFloat();
        tacLH = in.readFloat();
        adcRE = in.readShort();
        ealDE = in.readShort();
        ncx = in.readShort();
        ncy = in.readShort();
        page = in.readUnsignedShort();
        colT = in.readFloat();
        repT = in.readFloat();
        stopt = in.readShort();
        overfl = in.readUnsignedByte();
        useMotor = in.readShort();
        steps = in.readUnsignedShort();
        offset = in.readFloat();
        dither = in.readShort();
        incr = in.readShort();
        memBank = in.readShort();

        byte[] modTypeBytes = new byte[16];
        in.readFully(modTypeBytes);
        modType = new String(modTypeBytes);

        synTH = in.readFloat();
        deadTimeComp = in.readShort();
        polarityL = in.readShort();
        polarityF = in.readShort();
        polarityP = in.readShort();
        linediv = in.readShort();
        accumulate = in.readShort();
        flbckY = in.readInt();
        flbckX = in.readInt();
        bordU = in.readInt();
        bordL = in.readInt();
        pixTime = in.readFloat();
        pixClk = in.readShort();
        trigger = in.readShort();
        scanX = in.readInt();
        scanY = in.readInt();
        scanRX = in.readInt();
        scanRY = in.readInt();
        fifoTyp = in.readShort();
        epxDiv = in.readInt();
        modTypeCode = in.readUnsignedShort();
        modFpgaVer = in.readUnsignedShort();
        overflowCorrFactor = in.readFloat();
        adcZoom = in.readInt();
        cycles = in.readInt();

        // save MeasureInfo to metadata table
        if (metadata != null) {
          final String measureInfo = "MeasureInfo.";
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
        }

        // extract dimensional parameters from measure info
        width = scanX;
        height = scanY;
        timeBins = adcRE;
        channels = scanRX;
      }

      if (hasMeasStopInfo) {
        // MeasStopInfo - information collected when measurement is finished
        status = in.readUnsignedShort();
        flags = in.readUnsignedShort();
        stopTime = in.readFloat();
        curStep = in.readInt();
        curCycle = in.readInt();
        curPage = in.readInt();
        minSyncRate = in.readFloat();
        minCfdRate = in.readFloat();
        minTacRate = in.readFloat();
        minAdcRate = in.readFloat();
        maxSyncRate = in.readFloat();
        maxCfdRate = in.readFloat();
        maxTacRate = in.readFloat();
        maxAdcRate = in.readFloat();
        mReserved1 = in.readInt();
        mReserved2 = in.readFloat();

        // save MeasStopInfo to metadata table
        if (metadata != null) {
          final String measStopInfo = "MeasStopInfo.";
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
      }

      if (hasMeasFCSInfo) {
        // MeasFCSInfo - information collected when FIFO measurement is finished
        chan = in.readUnsignedShort();
        fcsDecayCalc = in.readUnsignedShort();
        mtResol = (0xffffffffL & in.readInt()); // unsigned
        cortime = in.readFloat();
        calcPhotons = (0xffffffffL & in.readInt()); // unsigned
        fcsPoints = in.readInt();
        endTime = in.readFloat();
        overruns = in.readUnsignedShort();
        fcsType = in.readUnsignedShort();
        crossChan = in.readUnsignedShort();
        mod = in.readUnsignedShort();
        crossMod = in.readUnsignedShort();
        crossMtResol = (0xffffffffL & in.readInt()); // unsigned

        // save MeasFCSInfo to metadata table
        if (metadata != null) {
          final String measFCSInfo = "MeasFCSInfo.";
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
      }

      if (hasExtendedMeasureInfo) {
        imageX = in.readInt();
        imageY = in.readInt();
        imageRX = in.readInt();
        imageRY = in.readInt();
        xyGain = in.readShort();
        masterClock = in.readShort();
        adcDE = in.readShort();
        detType = in.readShort();
        xAxis = in.readShort();

        // save extra MeasureInfo to metadata table
        if (metadata != null) {
          final String measureInfo = "MeasureInfo.";
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
      }

      if (hasMeasHISTInfo) {
        // MeasHISTInfo - extension of FCSInfo, valid only for FIFO meas
        // extension of MeasFCSInfo for other histograms (FIDA, FILDA, MCS)
        fidaTime = in.readFloat();
        fildaTime = in.readFloat();
        fidaPoints = in.readInt();
        fildaPoints = in.readInt();
        mcsTime = in.readFloat();
        mcsPoints = in.readInt();

        // save MeasHISTInfo to metadata table
        if (metadata != null) {
          final String measHISTInfo = "MeasHISTInfo.";
          metadata.put(measHISTInfo + "fidaTime", new Float(fidaTime));
          metadata.put(measHISTInfo + "fildaTime", new Float(fildaTime));
          metadata.put(measHISTInfo + "fidaPoints", new Integer(fidaPoints));
          metadata.put(measHISTInfo + "fildaPoints", new Integer(fildaPoints));
          metadata.put(measHISTInfo + "mcsTime", new Float(mcsTime));
          metadata.put(measHISTInfo + "mcsPoints", new Integer(mcsPoints));
        }
      }
    }

    in.seek(dataBlockOffs);

    // read BHFileBlockHeader
    blockNo = in.readShort();
    dataOffs = in.readInt();
    nextBlockOffs = in.readInt();
    blockType = in.readUnsignedShort();
    measDescBlockNo = in.readShort();
    lblockNo = (0xffffffffL & in.readInt()); // unsigned
    blockLength = (0xffffffffL & in.readInt()); // unsigned

    // save BHFileBlockHeader to metadata table
    if (metadata != null) {
      final String bhFileBlockHeader = "BHFileBlockHeader.";
      metadata.put(bhFileBlockHeader + "blockNo", new Short(blockNo));
      metadata.put(bhFileBlockHeader + "dataOffs", new Integer(dataOffs));
      metadata.put(bhFileBlockHeader + "nextBlockOffs",
        new Integer(nextBlockOffs));
      metadata.put(bhFileBlockHeader + "blockType", new Integer(blockType));
      metadata.put(bhFileBlockHeader + "measDescBlockNo",
        new Short(measDescBlockNo));
      metadata.put(bhFileBlockHeader + "lblockNo", new Long(lblockNo));
      metadata.put(bhFileBlockHeader + "blockLength", new Long(blockLength));
    }
  }

}
