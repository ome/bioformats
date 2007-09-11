//
// SDTInfo.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

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
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/SDTInfo.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/SDTInfo.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class SDTInfo {

  // -- Constants --

  public static final short BH_HEADER_CHKSUM = 0x55aa;
  public static final short BH_HEADER_NOT_VALID = 0x1111;
  public static final short BH_HEADER_VALID = 0x5555;

  /** For .set files (setup only). */
  public static final String SETUP_IDENTIFIER = "SPC Setup Script File";

  /** For normal .sdt files (setup + data). */
  public static final String DATA_IDENTIFIER = "SPC Setup & Data File";

  /**
   * For .sdt files created automatically in Continuous Flow mode measurement
   * (no setup, only data).
   */
  public static final String FLOW_DATA_IDENTIFIER = "SPC Flow Data File";

  /**
   * For .sdt files created using DLL function SPC_save_data_to_sdtfile
   * (no setup, only data).
   */
  public static final String DLL_DATA_IDENTIFIER = "SPC DLL Data File";

  /**
   * For .sdt files created in FIFO mode
   * (setup, data blocks = Decay, FCS, FIDA, FILDA &amp; MCS curves
   * for each used routing channel).
   */
  public static final String FCS_DATA_IDENTIFIER = "SPC FCS Data File";

  public static final String X_STRING = "#SP [SP_SCAN_X,I,";
  public static final String Y_STRING = "#SP [SP_SCAN_Y,I,";
  public static final String T_STRING = "#SP [SP_ADC_RE,I,";
  public static final String C_STRING = "#SP [SP_SCAN_RX,I,";

  // -- Fields --

  public int width, height, timeBins, channels;

  // -- Fields - File header --

  /** Software revision number (lower 4 bits &gt;= 10(decimal)). */
  public short revision;

  /**
   * Offset of the info part which contains general text
   * information (Title, date, time, contents etc.).
   */
  public int infoOffs;

  /** Length of the info part. */
  public short infoLength;

  /**
   * Offset of the setup text data
   * (system parameters, display parameters, trace parameters etc.).
   */
  public int setupOffs;

  /** Length of the setup data. */
  public short setupLength;

  /** Offset of the first data block. */
  public int dataBlockOffs;

  /**
   * no_of_data_blocks valid only when in 0 .. 0x7ffe range,
   * if equal to 0x7fff  the  field 'reserved1' contains
   * valid no_of_data_blocks.
   */
  public short noOfDataBlocks;

  // length of the longest block in the file
  public int dataBlockLength;

  // offset to 1st. measurement description block
  // (system parameters connected to data blocks)
  public int measDescBlockOffs;

  // number of measurement description blocks
  public short noOfMeasDescBlocks;

  // length of the measurement description blocks
  public short measDescBlockLength;

  // valid: 0x5555, not valid: 0x1111
  public int headerValid;

  // reserved1 now contains noOfDataBlocks
  public long reserved1; // unsigned

  public int reserved2;

  // checksum of file header
  public int chksum;

  // -- Fields - File Info --

  public String info;

  // -- Fields -- Setup --

  public String setup;

  // -- Fields - MeasureInfo --

  public boolean hasMeasureInfo;

  /** Time of creation. */
  public String time;

  /** Date of creation. */
  public String date;

  /** Serial number of the module. */
  public String modSerNo;

  public short measMode;
  public float cfdLL;
  public float cfdLH;
  public float cfdZC;
  public float cfdHF;
  public float synZC;
  public short synFD;
  public float synHF;
  public float tacR;
  public short tacG;
  public float tacOF;
  public float tacLL;
  public float tacLH;
  public short adcRE;
  public short ealDE;
  public short ncx;
  public short ncy;
  public int page;
  public float colT;
  public float repT;
  public short stopt;
  public int overfl;
  public short useMotor;
  public int steps;
  public float offset;
  public short dither;
  public short incr;
  public short memBank;

  /** Module type. */
  public String modType;

  public float synTH;
  public short deadTimeComp;

  /** 2 = disabled line markers. */
  public short polarityL;

  public short polarityF;
  public short polarityP;

  /** Line predivider = 2 ** (linediv). */
  public short linediv;

  public short accumulate;
  public int flbckY;
  public int flbckX;
  public int bordU;
  public int bordL;
  public float pixTime;
  public short pixClk;
  public short trigger;
  public int scanX;
  public int scanY;
  public int scanRX;
  public int scanRY;
  public short fifoTyp;
  public int epxDiv;
  public int modTypeCode;

  /** New in v.8.4. */
  public int modFpgaVer;

  public float overflowCorrFactor;
  public int adcZoom;

  /** Cycles (accumulation cycles in FLOW mode). */
  public int cycles;

  // -- Fields - MeasStopInfo --

  public boolean hasMeasStopInfo;

  /** Last SPC_test_state return value (status). */
  public int status;

  /** Scan clocks bits 2-0 (frame, line, pixel), rates_read - bit 15. */
  public int flags;

  /**
   * Time from start to  - disarm (simple measurement)
   * - or to the end of the cycle (for complex measurement).
   */
  public float stopTime;

  /** Current step (if multi-step measurement). */
  public int curStep;

  /**
   * Current cycle (accumulation cycle in FLOW mode) -
   * (if multi-cycle measurement).
   */
  public int curCycle;

  /** Current measured page. */
  public int curPage;

  /** Minimum rates during the measurement. */
  public float minSyncRate;

  /** (-1.0 - not set). */
  public float minCfdRate;

  public float minTacRate;
  public float minAdcRate;

  /** Maximum rates during the measurement. */
  public float maxSyncRate;

  /** (-1.0 - not set). */
  public float maxCfdRate;

  public float maxTacRate;
  public float maxAdcRate;
  public int mReserved1;
  public float mReserved2;

  // -- Fields - MeasFCSInfo --

  public boolean hasMeasFCSInfo;

  /** Routing channel number. */
  public int chan;

  /**
   * Bit 0 = 1 - decay curve calculated.
   * Bit 1 = 1 - fcs   curve calculated.
   * Bit 2 = 1 - FIDA  curve calculated.
   * Bit 3 = 1 - FILDA curve calculated.
   * Bit 4 = 1 - MCS curve calculated.
   * Bit 5 = 1 - 3D Image calculated.
   */
  public int fcsDecayCalc;

  /** Macro time clock in 0.1 ns units. */
  public long mtResol; // unsigned

  /** Correlation time [ms]. */
  public float cortime;

  /** No of photons. */
  public long calcPhotons; // unsigned

  /** No of FCS values. */
  public int fcsPoints;

  /** Macro time of the last photon. */
  public float endTime;

  /**
   * No of Fifo overruns
   * when &gt; 0  fcs curve &amp; endTime are not valid.
   */
  public int overruns;

  /**
   * 0 - linear FCS with log binning (100 bins/log)
   * when bit 15 = 1 (0x8000) - Multi-Tau FCS
   * where bits 14-0 = ktau parameter.
   */
  public int fcsType;

  /**
   * Cross FCS routing channel number
   * when chan = crossChan and mod == crossMod - Auto FCS
   * otherwise - Cross FCS.
   */
  public int crossChan;

  /** Module number. */
  public int mod;

  /** Cross FCS module number. */
  public int crossMod;

  /** Macro time clock of cross FCS module in 0.1 ns units. */
  public long crossMtResol; // unsigned

  // -- Fields - extended MeasureInfo -

  public boolean hasExtendedMeasureInfo;

  /**
   * 4 subsequent fields valid only for Camera mode
   * or FIFO_IMAGE mode.
   */
  public int imageX;
  public int imageY;
  public int imageRX;
  public int imageRY;

  /** Gain for XY ADCs (SPC930). */
  public short xyGain;

  /** Use or not  Master Clock (SPC140 multi-module). */
  public short masterClock;

  /** ADC sample delay (SPC-930). */
  public short adcDE;

  /** Detector type (SPC-930 in camera mode). */
  public short detType;

  /** X axis representation (SPC-930). */
  public short xAxis;

  // -- Fields - MeasHISTInfo --

  public boolean hasMeasHISTInfo;

  /** Interval time [ms] for FIDA histogram. */
  public float fidaTime;

  /** Interval time [ms] for FILDA histogram. */
  public float fildaTime;

  /** No of FIDA values. */
  public int fidaPoints;

  /** No of FILDA values. */
  public int fildaPoints;

  /** Interval time [ms] for MCS histogram. */
  public float mcsTime;

  /** No of MCS values. */
  public int mcsPoints;

  // -- Fields - BHFileBlockHeader --

  /**
   * Number of the block in the file.
   * Valid only when in 0..0x7ffe range, otherwise use lblock_no field
   * obsolete now, lblock_no contains full block no information.
   */
  public short blockNo;

  /** Offset of the data block from the beginning of the file. */
  public int dataOffs;

  /** Offset to the data block header of the next data block. */
  public int nextBlockOffs;

  /** See blockType defines below. */
  public int blockType;

  /**
   * Number of the measurement description block
   * corresponding to this data block.
   */
  public short measDescBlockNo;

  /** Long blockNo - see remarks below. */
  public long lblockNo; // unsigned

  /** reserved2 now contains block (set) length. */
  public long blockLength; // unsigned

  // -- Constructor --

  /**
   * Constructs a new SDT header by reading values from the given input source,
   * populating the given metadata table.
   */
  public SDTInfo(RandomAccessStream in, Hashtable meta) throws IOException {
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
    if (meta != null) {
      final String bhfileHeader = "bhfileHeader.";
      meta.put(bhfileHeader + "revision", new Short(revision));
      meta.put(bhfileHeader + "infoOffs", new Integer(infoOffs));
      meta.put(bhfileHeader + "infoLength", new Short(infoLength));
      meta.put(bhfileHeader + "setupOffs", new Integer(setupOffs));
      meta.put(bhfileHeader + "dataBlockOffs", new Integer(dataBlockOffs));
      meta.put(bhfileHeader + "noOfDataBlocks", new Short(noOfDataBlocks));
      meta.put(bhfileHeader + "dataBlockLength",
        new Integer(dataBlockLength));
      meta.put(bhfileHeader + "measDescBlockOffs",
        new Integer(measDescBlockOffs));
      meta.put(bhfileHeader + "noOfMeasDescBlocks",
        new Short(noOfMeasDescBlocks));
      meta.put(bhfileHeader + "measDescBlockLength",
        new Integer(measDescBlockLength));
      meta.put(bhfileHeader + "headerValid", new Integer(headerValid));
      meta.put(bhfileHeader + "reserved1", new Long(reserved1));
      meta.put(bhfileHeader + "reserved2", new Integer(reserved2));
      meta.put(bhfileHeader + "chksum", new Integer(chksum));
    }

    // read file info
    in.seek(infoOffs);
    byte[] infoBytes = new byte[infoLength];
    in.readFully(infoBytes);
    info = new String(infoBytes);

    // save file info to metadata table
    if (meta != null) meta.put("File Info", info);
    // TODO: parse individual parameters from info string and store them

    // read setup
    in.seek(setupOffs);
    byte[] setupBytes = new byte[setupLength];
    in.readFully(setupBytes);
    setup = new String(setupBytes);

    // save setup to metadata table
    if (meta != null) meta.put("Setup", setup);
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
        if (meta != null) {
          final String measureInfo = "MeasureInfo.";
          meta.put(measureInfo + "time", time);
          meta.put(measureInfo + "date", date);
          meta.put(measureInfo + "modSerNo", modSerNo);
          meta.put(measureInfo + "measMode", new Short(measMode));
          meta.put(measureInfo + "cfdLL", new Float(cfdLL));
          meta.put(measureInfo + "cfdLH", new Float(cfdLH));
          meta.put(measureInfo + "cfdZC", new Float(cfdZC));
          meta.put(measureInfo + "cfdHF", new Float(cfdHF));
          meta.put(measureInfo + "synZC", new Float(synZC));
          meta.put(measureInfo + "synFD", new Short(synFD));
          meta.put(measureInfo + "synHF", new Float(synHF));
          meta.put(measureInfo + "tacR", new Float(tacR));
          meta.put(measureInfo + "tacG", new Short(tacG));
          meta.put(measureInfo + "tacOF", new Float(tacOF));
          meta.put(measureInfo + "tacLL", new Float(tacLL));
          meta.put(measureInfo + "tacLH", new Float(tacLH));
          meta.put(measureInfo + "adcRE", new Short(adcRE));
          meta.put(measureInfo + "ealDE", new Short(ealDE));
          meta.put(measureInfo + "ncx", new Short(ncx));
          meta.put(measureInfo + "ncy", new Short(ncy));
          meta.put(measureInfo + "page", new Integer(page));
          meta.put(measureInfo + "colT", new Float(colT));
          meta.put(measureInfo + "repT", new Float(repT));
          meta.put(measureInfo + "stopt", new Short(stopt));
          meta.put(measureInfo + "overfl", new Integer(overfl));
          meta.put(measureInfo + "useMotor", new Short(useMotor));
          meta.put(measureInfo + "steps", new Integer(steps));
          meta.put(measureInfo + "offset", new Float(offset));
          meta.put(measureInfo + "dither", new Short(dither));
          meta.put(measureInfo + "incr", new Short(incr));
          meta.put(measureInfo + "memBank", new Short(memBank));
          meta.put(measureInfo + "modType", modType);
          meta.put(measureInfo + "synTH", new Float(synTH));
          meta.put(measureInfo + "deadTimeComp", new Short(deadTimeComp));
          meta.put(measureInfo + "polarityL", new Short(polarityL));
          meta.put(measureInfo + "polarityF", new Short(polarityF));
          meta.put(measureInfo + "polarityP", new Short(polarityP));
          meta.put(measureInfo + "linediv", new Short(linediv));
          meta.put(measureInfo + "accumulate", new Short(accumulate));
          meta.put(measureInfo + "flbckY", new Integer(flbckY));
          meta.put(measureInfo + "flbckX", new Integer(flbckX));
          meta.put(measureInfo + "bordU", new Integer(bordU));
          meta.put(measureInfo + "bordL", new Integer(bordL));
          meta.put(measureInfo + "pixTime", new Float(pixTime));
          meta.put(measureInfo + "pixClk", new Short(pixClk));
          meta.put(measureInfo + "trigger", new Short(trigger));
          meta.put(measureInfo + "scanX", new Integer(scanX));
          meta.put(measureInfo + "scanY", new Integer(scanY));
          meta.put(measureInfo + "scanRX", new Integer(scanRX));
          meta.put(measureInfo + "scanRY", new Integer(scanRY));
          meta.put(measureInfo + "fifoTyp", new Short(fifoTyp));
          meta.put(measureInfo + "epxDiv", new Integer(epxDiv));
          meta.put(measureInfo + "modTypeCode", new Integer(modTypeCode));
          meta.put(measureInfo + "modFpgaVer", new Integer(modFpgaVer));
          meta.put(measureInfo + "overflowCorrFactor",
            new Float(overflowCorrFactor));
          meta.put(measureInfo + "adcZoom", new Integer(adcZoom));
          meta.put(measureInfo + "cycles", new Integer(cycles));
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
        if (meta != null) {
          final String measStopInfo = "MeasStopInfo.";
          meta.put(measStopInfo + "status", new Integer(status));
          meta.put(measStopInfo + "flags", new Integer(flags));
          meta.put(measStopInfo + "stopTime", new Float(stopTime));
          meta.put(measStopInfo + "curStep", new Integer(curStep));
          meta.put(measStopInfo + "curCycle", new Integer(curCycle));
          meta.put(measStopInfo + "curPage", new Integer(curPage));
          meta.put(measStopInfo + "minSyncRate", new Float(minSyncRate));
          meta.put(measStopInfo + "minCfdRate", new Float(minCfdRate));
          meta.put(measStopInfo + "minTacRate", new Float(minTacRate));
          meta.put(measStopInfo + "minAdcRate", new Float(minAdcRate));
          meta.put(measStopInfo + "maxSyncRate", new Float(maxSyncRate));
          meta.put(measStopInfo + "maxCfdRate", new Float(maxCfdRate));
          meta.put(measStopInfo + "maxTacRate", new Float(maxTacRate));
          meta.put(measStopInfo + "maxAdcRate", new Float(maxAdcRate));
          meta.put(measStopInfo + "reserved1", new Integer(mReserved1));
          meta.put(measStopInfo + "reserved2", new Float(mReserved2));
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
        if (meta != null) {
          final String measFCSInfo = "MeasFCSInfo.";
          meta.put(measFCSInfo + "chan", new Integer(chan));
          meta.put(measFCSInfo + "fcsDecayCalc", new Integer(fcsDecayCalc));
          meta.put(measFCSInfo + "mtResol", new Long(mtResol));
          meta.put(measFCSInfo + "cortime", new Float(cortime));
          meta.put(measFCSInfo + "calcPhotons", new Long(calcPhotons));
          meta.put(measFCSInfo + "fcsPoints", new Integer(fcsPoints));
          meta.put(measFCSInfo + "endTime", new Float(endTime));
          meta.put(measFCSInfo + "overruns", new Integer(overruns));
          meta.put(measFCSInfo + "fcsType", new Integer(fcsType));
          meta.put(measFCSInfo + "crossChan", new Integer(crossChan));
          meta.put(measFCSInfo + "mod", new Integer(mod));
          meta.put(measFCSInfo + "crossMod", new Integer(crossMod));
          meta.put(measFCSInfo + "crossMtResol", new Float(crossMtResol));
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
        if (meta != null) {
          final String measureInfo = "MeasureInfo.";
          meta.put(measureInfo + "imageX", new Integer(imageX));
          meta.put(measureInfo + "imageY", new Integer(imageY));
          meta.put(measureInfo + "imageRX", new Integer(imageRX));
          meta.put(measureInfo + "imageRY", new Integer(imageRY));
          meta.put(measureInfo + "xyGain", new Short(xyGain));
          meta.put(measureInfo + "masterClock", new Short(masterClock));
          meta.put(measureInfo + "adcDE", new Short(adcDE));
          meta.put(measureInfo + "detType", new Short(detType));
          meta.put(measureInfo + "xAxis", new Short(xAxis));
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
        if (meta != null) {
          final String measHISTInfo = "MeasHISTInfo.";
          meta.put(measHISTInfo + "fidaTime", new Float(fidaTime));
          meta.put(measHISTInfo + "fildaTime", new Float(fildaTime));
          meta.put(measHISTInfo + "fidaPoints", new Integer(fidaPoints));
          meta.put(measHISTInfo + "fildaPoints", new Integer(fildaPoints));
          meta.put(measHISTInfo + "mcsTime", new Float(mcsTime));
          meta.put(measHISTInfo + "mcsPoints", new Integer(mcsPoints));
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
    if (meta != null) {
      final String bhFileBlockHeader = "BHFileBlockHeader.";
      meta.put(bhFileBlockHeader + "blockNo", new Short(blockNo));
      meta.put(bhFileBlockHeader + "dataOffs", new Integer(dataOffs));
      meta.put(bhFileBlockHeader + "nextBlockOffs",
        new Integer(nextBlockOffs));
      meta.put(bhFileBlockHeader + "blockType", new Integer(blockType));
      meta.put(bhFileBlockHeader + "measDescBlockNo",
        new Short(measDescBlockNo));
      meta.put(bhFileBlockHeader + "lblockNo", new Long(lblockNo));
      meta.put(bhFileBlockHeader + "blockLength", new Long(blockLength));
    }
  }

}
