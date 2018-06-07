/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2015 - 2017 Open Microscopy Environment:
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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import loci.common.Location;

import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;

import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

/**
 * SPCReader is the file format reader for
 * Becker &amp; Hickl SPC-Image SPC FIFO files.
 *
 * @author Ian i.munro at imperial.ac.uk
 * 
 * Ported from Matlab code kindly provided by ulorenzo at ikasle.ehu.eus.
 * This format is documented in the TCSPC Handbook
 * available from http://www.becker-hickl.com/handbookphp.htm
 * See Page 675 FIFO Data Files (SPC-134, SPC-144, SPC-154, SPC-830).
 * Note that this code assumes the presence of pixel clock events and 
 * therefore the MacroTime information is not used.
 */
public class SPCReader extends FormatReader {
  
  // -- Constants --

  // Setup file text field strings.
  public static final String TAC_RANGE = "SP_TAC_R";
  public static final String TAC_GAIN = "SP_TAC_G";
  
  /*
   * No of bits by which the ADC value is shifted.
   * there are 12 bits in the file format so shifting by 8 bits 
   * leaves 4 bits of resolution ie 16 timebins.
   * shifting by 7 bits gives 32 timebins etc.
   */
  private final int adcResShift = 6;

  // -- Fields --
  
  /** List of all files to open */
  private ArrayList<String> allFiles;
  
  /** Number of time bins in lifetime histogram. */
  protected int nTimebins;

  /** Number of spectral nChannels. */
  protected int nChannels;

  /*
   * Array to hold re-ordered data for all the timeBins in one channel at one 
     real-time point.
   */
  protected byte[] Tstore = null;
  ByteBuffer tstoreb;

  /*
   * Currently stored channel
   */
  protected int storedChannel = -1;

  /*
   * Currently stored real time data-cube
   */
  protected int storedT = -1;

  /*
   * Current position in image
   */
  private int currentPixel;
  private int currentLine;
  private int currentFrame;
  
  
  /*
   * Buffer for reading from files
   */
  protected int bufLength;
  protected byte[] rawBuf;
  private int nBuffers;  // no of buffers read

  /*
   * Image size 
   */
  private int nLines;
  private int nFrames;
  private int nPixels;
  
  /*
   * Flag indicating that a Frame clock has been detected.
   * true until the first line clock in that frame is detected
   */
  private boolean endOfFrameFlag;
  
  /*
   * bits per pixel
   */
  private int bpp;
  
  /*
   * Length in bytes of data in a single timebin.
   */
  private int binSize;
  
  /*
   * Requested channel- photons in other channels are ignored.
   */
  private int channel;
  
  /*
   * Position of each frame clock in the .spc file.
   */
  List<Integer> frameClockList;
  
  /*
   * Position of the end of each frame in the .spc file.
   */
  List<Integer> endOfFrameList;
  
  /*
   * Flag to indicate single-line mode.
   */
  private boolean lineMode;
  
  /*
   * .spc file id 
   */
  private String spcId;
  
  
  
  
 
  // -- Constructor --

  /** Constructs a new SPC reader. */
  public SPCReader() {
    super("SPC FIFO Data", new String[] {"spc", "set"});
    domains = new String[] {FormatTools.FLIM_DOMAIN};
    suffixSufficient = true;
    hasCompanionFiles = true;
    datasetDescription = "One .spc file and similarly named .set file";
    
  }
  

  // -- IFormatReader API methods --
 
  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  } 

   /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    
    /*
     * N.B. The setup file for the example available at writing contains the string 
     * "FIFO_IMAGE measurement with module SPC-830"
     * as npted above this code is valid with data from te following modules:
     * (SPC-134, SPC-144, SPC-154, SPC-830)
     * ideally the .set file should be checked here for the occurence of one of these strings
     * but this could adversely affect performance so is currently checked in initFile
    */
    
    
    if (!(checkSuffix(name, "spc") || checkSuffix(name, "set") )) 
      return false;
    
    
    String extension = name.substring(name.lastIndexOf(".") + 1);
    String baseName = name.substring(0, name.lastIndexOf("."));
    
    if (!new Location(baseName + ".spc").exists())
      return false;
    
    return new Location(baseName + ".set").exists();
    
  }

   /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    String[] farray = allFiles.toArray(new String[allFiles.size()]);
    return farray;
  } 
  
   /* @see loci.formats.IFormatReader#reopenFile() */
  @Override
  public void reopenFile() throws IOException {
    if (in != null) {
      in.close();
    }
   
    in = new RandomAccessInputStream(spcId);
    in.order(true);
  }

   /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    
    Integer sizeT = getSizeT();
    channel = no/sizeT;
   
    no -= channel * sizeT;
            
    Integer T = no/nTimebins;
    
    no -= T * nTimebins;
            
    Integer timebin = no;
   
    bpp = 2;
    binSize = nPixels * nLines * bpp;
    
    if (Tstore == null) {
      Tstore = new byte[nPixels * nLines * bpp * nTimebins];
      tstoreb = ByteBuffer.wrap(Tstore); // Wrapper around underlying byte[].
      tstoreb.order(ByteOrder.LITTLE_ENDIAN);
    }
    int noOfBytes;
    
    
    // if the pre-stored data doesn't match that requested then read from the file
    if (storedT != T || storedChannel != channel)  {
      
      LOGGER.debug("T =  " + Integer.toString(T)   );
    
      // skip to first data after start of requested frame
      Integer frameClockPos = frameClockList.get(T);
      Integer endOfFramePos = endOfFrameList.get(T + 1);
      
    
      Integer frameLength = (int) (endOfFramePos - frameClockPos);
       
      in.seek(frameClockPos);
      noOfBytes = in.read(rawBuf, 0, frameLength);
      
      
      if (noOfBytes == frameLength) {
        
        currentLine = -1;
        currentFrame = -1;
        endOfFrameFlag = false;
        
        processBuffer(noOfBytes);
        storedT = T;
        storedChannel = channel;
      }
    }
    
   
    // copy 2D plane  from Tstore  into buf
    Integer iLineSize = nPixels * bpp;
    Integer oLineSize = w * bpp;
    
    // offset to correct timebin yth line and xth pixel
    
    int output = 0;
    if (!lineMode) {   //image Mode
      int input = (binSize * timebin) + (y * iLineSize) + (x * bpp);
      for (int line = 0; line < h; line++) {
        System.arraycopy(Tstore, input, buf, output, oLineSize);
        input += iLineSize;
        output += oLineSize;
      }
    }
    else  {  //line Mode
      ByteBuffer bufb = ByteBuffer.wrap(buf); // Wrapper around underlying byte[].
      bufb.order(ByteOrder.LITTLE_ENDIAN);
      // copy first line into buf
      int input = (binSize * timebin) + (x * bpp);
      System.arraycopy(Tstore, input, buf, output, oLineSize);
      input += iLineSize;
      // now sum all other lines
      Short s;
      for (int line = 1; line < nLines; line++) {
        for (int p = 0;p < oLineSize; p+=2)  {
          s = bufb.getShort(output + p);
          s = (short)(s + tstoreb.getShort(input + p));
          bufb.putShort(output + p, s);
        }
        input+=iLineSize;
      }
    }
    
    return buf;
  }


  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      Tstore = null;
      storedChannel = -1;
      storedT = -1;
      allFiles = null;
      frameClockList = new ArrayList<Integer>();

    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    
    super.initFile(id);
    
    allFiles = new ArrayList<String>();
    
    // get the working directory
    Location tmpFile = new Location(id).getAbsoluteFile();
    Location workingDir = tmpFile.getParentFile();
    if (workingDir == null) workingDir = new Location(".");
    String workingDirPath = workingDir.getPath();
    if (!workingDirPath.equals("")) workingDirPath += File.separator;
    String[] ls = workingDir.list(true);
    if (!new Location(id).exists()) {
      ls = Location.getIdMap().keySet().toArray(new String[0]);
      workingDirPath = "";
    }
    
    String name = tmpFile.getName();
    // generate the name of the two matching files
    String setName = null;
    String spcName = null;
    int pos = name.lastIndexOf(".");
    if (pos != -1) {
      setName = tmpFile.getName().substring(0, pos) + ".set";
      spcName = tmpFile.getName().substring(0, pos) + ".spc";
      for (String l : ls) {
        if (l.equalsIgnoreCase(setName)) 
          setName = l;
        if (l.equalsIgnoreCase(spcName)) 
          spcName = l;
      }
    }
      
     
    if (setName == null)  {
      throw new FormatException("Failed to find a matching .set file!");
    }
    if (spcName == null)  {
      throw new FormatException("Failed to find a matching .spc file!");
    }
    
    frameClockList = new ArrayList<>();
    endOfFrameList = new ArrayList<>();
     
    allFiles.add(workingDirPath + setName);
    allFiles.add(workingDirPath + spcName);
    in = new RandomAccessInputStream(workingDirPath + setName);
    
    LOGGER.info("Reading info from .set file");
    in.order(true);
  
    in.skip(8);
    
    Integer setuppos = in.readInt();
    
    Short setupcount = in.readShort();
    String module = "";
    
    try  {
      // Arbitrary length established by trial and error
      String header = in.readString(600);

      int index = header.indexOf("module SPC-");
      module = header.substring(index + 7, index + 14);
      
    }
    catch (IOException exc) {
      LOGGER.debug("Failed to read header from .set file!", exc);
    }
    
    
    if (!module.equalsIgnoreCase("SPC-134") && !module.equalsIgnoreCase("SPC-144") 
            && !module.equalsIgnoreCase("SPC-154") && !module.equalsIgnoreCase("SPC-830"))  {
      throw new FormatException("Failed to find a matching .set file!");
    } 
    
    // goto start of setup information
    in.seek(setuppos);
    
    
    String setup = in.readString(setupcount);
    
    in.close();
    
    // get the tac range from the setup information
    double tacRange = parseSetup(TAC_RANGE, setup);
    
    // get the tac range from the setup information
    double tacGain = parseSetup(TAC_GAIN, setup);
    
    
    double timeBase;
    if (tacGain != 0.0 && tacRange != 0.0)  {
      timeBase = 4095 * tacRange/(tacGain * 4096);
       // convert from s to ps 
      timeBase *= 1.000e12;
    }
    else  {
      throw new FormatException("Failed to parse setup file!");
    }
    
    
    LOGGER.debug("timeBase = " + Double.toString(timeBase));
    
    // Now read .spc file
     spcId = workingDirPath + spcName;
     in = new RandomAccessInputStream(spcId);
     in.order(true);

    LOGGER.info("Reading info from .spc file");
    
    /* The first 3 bytes in the file contain information about the 
     * macro time clock in 0.1 ns units ("500" for 50ns clock)
    */
    
    in.skip(3);
    /*
     * The 4th byte contains information about the number of 
     * routing channels in bits 3 to 6.
     * Bits 0-2 reserved bit 7 = 1 ("Data invalid")
    */
    byte routing  = in.readByte();
    
    if ((routing & 0x10) != 0)  {
      throw new FormatException("Invalid data!");
    }
    
    nChannels = (routing & 0x78) >>3;
     
    currentPixel = 0;
    currentLine = -1;
    currentFrame = -1;
    rawBuf = new byte[bufLength];
    endOfFrameFlag = false;
    nBuffers = 0;
    
    bufLength = 1024;
    rawBuf = new byte[bufLength];
    int noOfBytes;
    nBuffers = 0;
    byte adcL;
    byte adcLM;
    
    while ((noOfBytes = in.read(rawBuf)) != -1) {
        for (int bb = 3; bb < noOfBytes; bb += 4) {    //even nybble adc

          adcL = rawBuf[bb];                // get upper byte containing ADC data
          adcLM = (byte) (adcL & 0xF0);     // mask out upper 4 bits

          // at this point only the various clocks are of interest
          switch (adcLM) { 
            case (byte) 0x90:
              invalidAndMarkInit(bb);
              break;

            case (byte) 0xd0:         // Invalid, Mark and MTOV all set.
              invalidAndMarkInit(bb);     // Unsure about this-- Not well documented!
              break;

            default:
              break;

          }  // end switch
        } //end for
        nBuffers++;
      }
    
    nTimebins = (0xFFF >> adcResShift) + 1;
    nFrames = currentFrame - 1;
    
    addGlobalMeta("time bins", nTimebins);
    addGlobalMeta("nChannels", nChannels);

    addGlobalMeta("time base", timeBase);

    LOGGER.info("Populating metadata");

    CoreMetadata m = core.get(0);
    
    // This behaviour seems to be undocumented and possibly system specific
    // Duplicates the behaviour of U.Lorenzo's Matlab code.
    if (nLines < 530)  {    // return an image
      lineMode = false;
       m.sizeY = nLines;
    }
    else  {
      LOGGER.info("Single line mode");
      lineMode = true;    // return a single line
       m.sizeY = 1;
    }
    
    Integer frameLength;
    Integer maxFrameLength = 0;
    for (int T = 0; T < nFrames; T++)  {
      frameLength = (int) (endOfFrameList.get(T + 1) - frameClockList.get(T));
      if (frameLength > maxFrameLength)
        maxFrameLength = frameLength;
    }
    
    rawBuf = new byte[maxFrameLength];
    
  
    m.sizeX = nPixels;
    m.sizeZ = 1;
    m.sizeT =  nTimebins * nFrames;
    m.sizeC = nChannels;
    m.dimensionOrder = "XYZTC";
    m.pixelType = FormatTools.UINT16;
    m.rgb = false;
    m.littleEndian = true;
    m.imageCount = m.sizeZ * m.sizeC * m.sizeT;
    m.indexed = false;
    m.falseColor = false;
    m.metadataComplete = true;
 
    m.moduloT.type = FormatTools.LIFETIME;
    m.moduloT.parentType = FormatTools.SPECTRA;
    m.moduloT.typeDescription = "TCSPC";
    m.moduloT.start = 0;

    m.moduloT.step = timeBase / nTimebins;
    m.moduloT.end = m.moduloT.step * (nTimebins - 1);
    m.moduloT.unit = "ps";
    
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
    
    
  }
  
 
   private void processBuffer(int noOfBytes) {

    byte adcL, adcLM, routLM;
    
    for (int bb = 3; bb < noOfBytes; bb += 4) {    //even nybble adc

      adcL = rawBuf[bb];                // get upper byte containing ADC data
      adcLM = (byte) (adcL & 0xF0);     // mask out upper 4 bits

      switch (adcLM) {
        case (byte) 0xA0:     //gap
          break;

        case 0x20:
          LOGGER.debug(" Got GAP but not invalid!!!");
          break;

        case 0x40:		// photon + ovfl
          //startOfLineTime -= timeStep;
          photon(bb);
          break;

        case 0x00:		//photon  
          photon(bb);
          break;

        case (byte) 0x80:        // invalid photon
          break;

        case (byte) 0x90:
          invalidAndMark(bb);
          break;
          
        case (byte) 0xd0:         // Invalid, Mark and MTOV all set.
          invalidAndMark(bb);     // Unsure about this-- Not well documented!
          break;

        case (byte) 0xC0:  // timer overflow  
          routLM = rawBuf[bb - 3];			
          break;

        default:
          LOGGER.debug("Unrecognised pattern !!!");
          break;

      }   //end switch

    } //end for
  }
  
 
   
   private void invalidAndMark(int blockPtr) {

    byte routM = (byte) (rawBuf[blockPtr - 2] & 0xf0);
    
    switch (routM) {

      case 0x10:           // pixel clock

        currentPixel++;
        break;

      case 0x20:          //line clock

        //LOGGER.debug("Line clock");
        
        if (endOfFrameFlag) {
          currentLine = -1;
          endOfFrameFlag = false;
          currentFrame++;
 
        }
        currentLine++;
        currentPixel = 0;
        break;

      case 0x40:         // frame clock

        //LOGGER.debug("Frame clock!");
        endOfFrameFlag = true;
        break;

      case 0x60:			//	fandlClock shouldn't happen
        //AfxMessageBox(_T(" both frame and line clock!"));
        break;

      default:
      //LOGGER.debug( "got unknown mark");
    }

  }
   
    private void invalidAndMarkInit(int blockPtr) {

    byte routM = (byte) (rawBuf[blockPtr - 2] & 0xf0);
    Integer position;

    switch (routM) {

      case 0x10:           // pixel clock

        currentPixel++;
        break;

      case 0x20:          //line clock

        //LOGGER.debug("Line clock Init");
        if (currentFrame == 0 && currentLine == 1) {
          nPixels = currentPixel;
        }

        if (endOfFrameFlag) {
          currentLine = -1;
          endOfFrameFlag = false;
          currentFrame++;
          position = (blockPtr - 3) + (bufLength * nBuffers);
          endOfFrameList.add(position);
        }

        currentLine++;
        currentPixel = 0;
        
        break;

      case 0x40:         // frame clock

        LOGGER.debug("Frame clock Init!");
        
        if (currentFrame == 0) {
          nLines = currentLine + 1;
        }
        // Store position of start of word containing frame clock for later use
        position = (blockPtr - 3) + (bufLength * nBuffers);
        frameClockList.add(position);
        
        endOfFrameFlag = true;
        break;

      case 0x60:			//	fandlClock shouldn't happen
        //AfxMessageBox(_T(" both frame and line clock!"));
        break;

      default:
      //LOGGER.debug( "got unknown mark");
    }

  }
  
  
  private void photon(int blockPtr) {
   
    int adc = rawBuf[blockPtr] & 0x0F;   // 4 MSBs of the ADC 
    int currentChannel = (rawBuf[blockPtr - 2] & 0xF0) >> 4;
    
   
    
    if (currentChannel == channel || nChannels==1) {
      if (currentPixel < nPixels && currentLine > -1  && currentLine < (nLines + 1)) {  
        int pix =  bpp *((currentLine * nPixels) + currentPixel);
        
        int adcM = (rawBuf[blockPtr] & 0x0F)  << 8;        // 4 bottom bits are 4 MSBs of 12- bit ADC 
        adcM = adcM | (rawBuf[blockPtr - 1] & 0x0FF);      // get all 12 bits
        int  microTime = 4095 - adcM;
        int currentBin = microTime >> adcResShift; 
        pix += currentBin * binSize;
        Short intensity = tstoreb.getShort(pix);
        intensity++;
        tstoreb.putShort(pix, intensity);
      }
    }
  }
  
  private double parseSetup(String tag, String setup) {
    
    // Fields in setup text consist of a tag, followed by a type (either "I" or "F")
    // folowed by a text value
    // e.g. #SP [SP_TAC_G,I,4]
   
    
    int tagOffset = setup.indexOf(tag);
    String taggedString = setup.substring(tagOffset, tagOffset + 30);
    tagOffset = taggedString.indexOf(',');
    String tagType = taggedString.substring(tagOffset + 1, tagOffset + 2);
    String valueTxt = taggedString.substring(tagOffset + 3, taggedString.indexOf(']'));
    double value = 0.0;
    if (tagType.matches("I")) {
      value = Integer.parseInt(valueTxt);
    }
    if (tagType.matches("F")) {
      value = Double.parseDouble(valueTxt);
    }
    
    return value;
  }
  
  

}
