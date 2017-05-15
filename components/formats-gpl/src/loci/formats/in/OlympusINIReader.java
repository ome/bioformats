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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
import java.text.DecimalFormat;

import loci.common.Constants;
import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.Region;
import loci.common.services.ServiceException;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.JPEGTileDecoder;
import loci.formats.meta.MetadataStore;
import loci.formats.services.JPEGTurboService;
import loci.formats.services.JPEGTurboServiceImpl;

import ome.xml.model.primitives.PositiveFloat;
import ome.units.quantity.Length;

/**
 * Olympus SlideScan.ini is the file format reader for Olympus ini datasets.
 *
 * @author Paul Richards paulrichards321@gmail.com
 */
public class OlympusINIReader extends FormatReader {

  // -- Constants --

  private static String iniNames[] = { 
    "FinalScan.ini", "FinalCond.ini",
    "SlideScan.ini", "SlideCond.ini"
  };
  private ArrayList<String> iniPaths = new ArrayList<String>();
  private static String iImageWidth = "iImageWidth";
  private static String iImageHeight = "iImageHeight";
  private static String lXStageRef = "lXStageRef";
  private static String lYStageRef = "lYStageRef";
  private static String lYStepSize = "lYStepSize";
  private static String lXStepSize = "lXStepSize";
  private static String lXOffset = "lXOffset";
  private static String lYOffset = "lYOffset";
  private static String dMagnification = "dMagnification";
  class JpegFileXY {
    public int x, y;
    public int xPixel, yPixel;
    public String fileName;
  }
    
  class IniConf {
    private String name = new String();
    private boolean found = false;
    private int xMin = 0;
    private int xMax = 0;
    private int yMin = 0;
    private int yMax = 0; 
    private int xDiffMin = 0;
    private int yDiffMin = 0;
    private int xStepSize = 0;
    private int yStepSize = 0;
    private int pixelWidth = 0;
    private int pixelHeight = 0;
    private double xAdj = 0.0;
    private double yAdj = 0.0;
    private int totalTiles = 0;
    private int xAxis = 0;
    private int yAxis = 0;
    private int xOffset = 0;
    private int yOffset = 0;
    private long totalWidth = 0;
    private long totalHeight = 0;
    private boolean isPreviewSlide = false;
    private ArrayList<JpegFileXY> xyArr = new ArrayList<JpegFileXY>();
  }      
  public int atoi(String s) {
    int value = 0;
    try {
      if (s != null) {
        value = Integer.parseInt(s);
      }
    } catch (NumberFormatException ignore) { }
    return value;
  }

  private ArrayList<IniConf> iniConf = new ArrayList<IniConf>();
  private double xStart = 0.0;
  private double yStart = 0.0;  
  private int level = 0;
  private long baseWidth = 0;
  private long baseHeight = 0;
  private int xMax = 0;
  private int xMin = 0;
  private int yMax = 0;
  private int yMin = 0;
  private int xAxis = 0;
  private int yAxis = 0;
  private long actualWidth = 0;
  private long actualHeight = 0;
  private double magnification = 0;
  private boolean validObject = false;
  private byte bkgColor = (byte) 0xFF;
// -- Fields --

  private int initializedSeries = -1;
  private int initializedPlane = -1;
  private String initializedFile = null;
  private JPEGTurboService service;

  // -- Constructor --

  /** Constructs a new OlympusINIReader. */
  public OlympusINIReader() {
    super("Olympus SlideScan.ini", "ini");
    setFlattenedResolutions(true);  
    domains = new String[] {FormatTools.HISTOLOGY_DOMAIN};
    datasetDescription = "Four .ini files plus several .jpg files";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    if (validObject)
    return iniConf.get(0).pixelWidth;
    else
    return 752;
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    if (validObject)
    return iniConf.get(0).pixelHeight;
    else
    return 480;
  }

  public void setBkgColor(byte newColor) {
    bkgColor = newColor;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    if (noPixels) {
      return new String[] {currentId};
    }
    ArrayList<String> f = new ArrayList<String>();
    f.add(currentId);
    for (int i=0; i<iniPaths.size(); i++)
    { 
      String path=iniPaths.get(i);
      if (currentId != null && currentId.toLowerCase().contains(iniNames[i].toLowerCase())==false)
      {
        f.add(path);
      }
    }
    for (int i=0; i<4; i++) {
      IniConf currIniConf = iniConf.get(i);
      if (currIniConf.found)
      {
        for (JpegFileXY xyfile : currIniConf.xyArr) {
          f.add(xyfile.fileName);
        }
      }
    }
    return f.toArray(new String[f.size()]);
  }

  /* @see loci.formats.IFormatReader#openThumbBytes(int) */
  @Override
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    if (core.size() == 1 || getSeries() >= getSeriesCount() - 2) {
      return super.openThumbBytes(no);
    }

    int smallestSeries = getSeriesCount() - 3;
    if (smallestSeries >= 0) {
      int thisSeries = getSeries();
      int resolution = getResolution();
      setSeries(smallestSeries);
      if (!hasFlattenedResolutions()) {
        setResolution(1);
      }
      byte[] thumb = FormatTools.openThumbBytes(this, no);
      setSeries(thisSeries);
      setResolution(resolution);
      return thumb;
    }
    return super.openThumbBytes(no);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int width, int height)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, width, height);
    if (!validObject) return buf;
    level=getCoreIndex();
    LOGGER.debug("Core Index="+level);
    if (level==-1 || level >= iniConf.size() || iniConf.get(level).found==false)
    {
      return buf;
    }
    IniConf currIniConf = iniConf.get(level);
    int actualWidth = (int) currIniConf.totalWidth;
    int actualHeight = (int) currIniConf.totalHeight;
    if (width <= 0 || height <= 0)
    {
      LOGGER.warn("width or height out of bounds: width=" + width + " height=" + height);
      return buf;
    } 
    int bmpSize=width*height*3;
    java.util.Arrays.fill(buf, 0, bmpSize, bkgColor);
    if (x>actualWidth || x<0 || y>actualHeight || y<0)
    {
      return buf;
    }
    int fileWidth=currIniConf.pixelWidth;
    int fileHeight=currIniConf.pixelHeight;
    int widthGrab=0, heightGrab=0;
    int totalTilesRead=0;
    boolean found=false;
    for (int tileNum=0; tileNum < currIniConf.totalTiles; tileNum++)
    {
      int xFilePos=currIniConf.xyArr.get(tileNum).xPixel;
      int yFilePos=currIniConf.xyArr.get(tileNum).yPixel;
      if (((x<xFilePos && x+width>xFilePos) || (x>=xFilePos && x<xFilePos+fileWidth)) &&
          ((y<yFilePos && y+height>yFilePos) || (y>=yFilePos && y<yFilePos+fileHeight)))
      {
        JPEGReader jpeg = new JPEGReader();
        int xRead=0;
        int xWrite=xFilePos-x;
        widthGrab=(x+width)-xFilePos;
        if (xWrite<0)
        {
          xWrite=0;
          xRead=x-xFilePos;
          widthGrab=fileWidth-xRead;
          if (widthGrab>width)
          {
            widthGrab=width;
          }
        }
        int yRead=0;
        int yWrite=yFilePos-y;
        heightGrab=(y+height)-yFilePos;
        if (yWrite<0)
        {
          yWrite=0;
          yRead=y-yFilePos;
          heightGrab=fileHeight-yRead;
          if (heightGrab>height)
          {
            heightGrab=height;
          }
        }
        if (yRead+heightGrab>fileHeight)
        {
          heightGrab=fileHeight-yRead;
        }
        if (xRead+widthGrab>fileWidth)
        {
          widthGrab=fileWidth-xRead;
        }
        LOGGER.debug("Filename to open: " + currIniConf.xyArr.get(tileNum).fileName + " xFilePos: " + xFilePos + " yFilePos: " + yFilePos + " widthGrab: " + widthGrab + " heightGrab: " + heightGrab + " xRead, xWrite: " + xRead + ", " + xWrite + " yRead, yWrite: " + yRead + ", " + yWrite);
        try {
          jpeg.setId(currIniConf.xyArr.get(tileNum).fileName);
          int jpegWidth=jpeg.getSizeX();
          int jpegHeight=jpeg.getSizeY();
          int jpegChannels=jpeg.getSizeC();
          if (heightGrab+yRead>jpegHeight)
          {
            heightGrab=jpegHeight-yRead;
            if (heightGrab < 0) heightGrab=0;
          }
          if (widthGrab+xRead>jpegWidth)
          {
            widthGrab=jpegWidth-xRead;
            if (widthGrab < 0) widthGrab=0;
          }
          int tempBmpSize=widthGrab*heightGrab*jpegChannels;
          if (tempBmpSize<=0)
          {
            tempBmpSize=1;
          }
          byte buf2[] = new byte[tempBmpSize];
          java.util.Arrays.fill(buf2, 0, tempBmpSize, bkgColor);
          jpeg.openBytes(0, buf2, xRead, yRead, widthGrab, heightGrab);
          jpeg.close();
          if (jpegChannels==3)
          {
            int scansize=widthGrab*heightGrab;
            for (int row=0; row<heightGrab; row++)
            {
              int desti=(yWrite*width*3)+(xWrite*3)+(row*width*3);
              int srci=widthGrab*row;
              if (desti+(3*widthGrab) > bmpSize || srci+widthGrab+(scansize*2) > buf2.length) 
              {
                LOGGER.warn("In OlympusINIReader::openBytes, pointer out of bounds: bmpSize=" + bmpSize + " desti=" + desti + " tempBmpSize=" + tempBmpSize + " srci=" + srci);
              }
              else
              {
                for (int i=0; i<widthGrab; i++)
                { // BGR RGB
                  buf[desti+2]=buf2[srci];
                  buf[desti+1]=buf2[srci+(scansize)];
                  buf[desti]=buf2[srci+(scansize*2)];
                  desti+=3;
                  srci++;
                }
              }
            }
          }
          else if (jpegChannels==1)
          {
            for (int row=yRead; row<heightGrab; row++)
            {
              int desti=(yWrite*width*3)+(xWrite*3)+(row*width*3);
              int srci=widthGrab*row;
              if (desti+(3*widthGrab) > bmpSize || srci+widthGrab > buf2.length) 
              {
                LOGGER.warn("In OlympusINIReader::openBytes, pointer out of bounds: bmpSize=" + bmpSize + " desti=" + desti + " tempBmpSize=" + tempBmpSize + " srci=" + srci);
              }
              else
              {
                //System.arraycopy(buf2, srci, buf, desti, rowlen);
                for (int i=0; i<widthGrab; i++)
                { // BGR RGB
                  buf[desti]=buf2[srci];
                  buf[desti+1]=buf2[srci];
                  buf[desti+2]=buf2[srci];
                  desti+=3;
                  srci++;
                }
              }
            }
          } 
        }
        catch (IOException e)
        {
          LOGGER.warn("Warning: failed to read " + currIniConf.xyArr.get(tileNum).fileName + ": " + e.getMessage());
          try {
            jpeg.close();
          } catch (IOException error) { }
        }
        found = true;
      }
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    validObject = false;
    iniConf.clear();
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    boolean yStepSizeFound = false;
    boolean xStepSizeFound = false;
    int xOffset = 0;
    int yOffset = 0;

    validObject = false;
    iniConf.clear();
    iniPaths.clear();
    for (int i=0; i<4; i++) {
      IniConf iniConfLocal = new IniConf();
      iniConf.add(iniConfLocal);
      iniPaths.add("");
    }
    String inputDir = null;
    for (int i=0; i<4; i++) {
      int namePos=id.indexOf(iniNames[i]); 
      if (namePos != -1) {
        inputDir = id.substring(0, namePos-1);
        break;
      }
    }
    if (inputDir != null && inputDir.length()>0) {
      char lastKey=inputDir.charAt(inputDir.length()-1);
      if (lastKey=='\\' || lastKey=='/') {
        inputDir=inputDir.substring(0, inputDir.length()-1);
      }
    }
    for (int fileNum = 0; fileNum < 4; fileNum++) {
      IniConf currIniConf = iniConf.get(fileNum);
      String inputName = inputDir;
      inputName += File.separator;
      inputName += iniNames[fileNum];

      LOGGER.debug("Trying to open " + inputName + "... ");
      in = new RandomAccessInputStream(inputName);
      IniParser parser = new IniParser();
      IniList layout = parser.parseINI(new BufferedReader(
        new InputStreamReader(in, Constants.ENCODING)));
      
      //*****************************************************
      // Parse Start of Header
      //*****************************************************
      IniTable slideHdr = layout.getTable("Header");
      if (slideHdr != null) {
        LOGGER.debug("Success!");
        String width = slideHdr.get(iImageWidth);
        if (width != null && width.length() > 0) {
          currIniConf.pixelWidth=atoi(width);
        }
        String height = slideHdr.get(iImageHeight);
        if (height != null && height.length() > 0) {
          currIniConf.pixelHeight=atoi(height);
        }
        String xStageSubStr = slideHdr.get(lXStageRef);
        if (xStageSubStr != null && xStageSubStr.length() > 0) {
          currIniConf.xAxis=atoi(xStageSubStr);
          xAxis=currIniConf.xAxis;
        }
        String yStageSubStr = slideHdr.get(lYStageRef);
        if (yStageSubStr != null && yStageSubStr.length() > 0) {
          currIniConf.yAxis=atoi(yStageSubStr);
          yAxis=currIniConf.yAxis;
        }
        String yStepSubStr = slideHdr.get(lYStepSize);
        if (yStepSubStr != null && yStepSubStr.length() > 0) {
          currIniConf.yStepSize = atoi(yStepSubStr);
          if (fileNum==0 && currIniConf.yStepSize > 0) yStepSizeFound = true;
          LOGGER.debug(" fileNum=" + fileNum + " yStepSize=" + currIniConf.xStepSize);
        }
        String xStepSubStr = slideHdr.get(lXStepSize);
        if (xStepSubStr != null && xStepSubStr.length() > 0) {
          currIniConf.xStepSize = atoi(xStepSubStr);
          if (fileNum==0 && currIniConf.xStepSize > 0) xStepSizeFound = true;
          LOGGER.debug(" fileNum=" + fileNum + " xStepSize=" + currIniConf.xStepSize);
        }
        String xOffsetSubStr = slideHdr.get(lXOffset);
        if (xOffsetSubStr != null && xOffsetSubStr.length() > 0) {
          xOffset = atoi(xOffsetSubStr);
          LOGGER.debug(" xOffset=" + xOffset);
        }
        String yOffsetSubStr = slideHdr.get(lYOffset);
        if (yOffsetSubStr != null && yOffsetSubStr.length() > 0) {
          yOffset = atoi(yOffsetSubStr);
          LOGGER.debug(" yOffset=" + yOffset);
        }
        String magSubStr = slideHdr.get(dMagnification);
        magnification = 40.0d;
        if (magSubStr != null && magSubStr.length() > 0) {
          try 
          {
            magnification = Double.parseDouble(magSubStr);
          } catch (NumberFormatException ignore) { }
          LOGGER.debug(" Magnification=" + magnification);
        }
      } else {
        LOGGER.debug("failed to find header in file. ");
        return;
      }
      
      //***************************************************
      // Grab Individual header x y coordinates from file
      //***************************************************
      List<String> headers = layout.getHeaders();
      for (String chunk : headers) {
        if (chunk.equalsIgnoreCase("Header") || chunk.length()==0) continue;
        LOGGER.debug("Header: "+chunk);
        JpegFileXY jpegxy = new JpegFileXY();
        jpegxy.fileName=inputDir;
        jpegxy.fileName += File.separator;
        jpegxy.fileName += chunk;
        jpegxy.fileName += ".jpg";
        boolean xFound = false;
        boolean yFound = false;
        slideHdr = layout.getTable(chunk);
        if (slideHdr != null) {
          String someNum = slideHdr.get("x");
          if (someNum != null && someNum.length()>0) {
            jpegxy.x = atoi(someNum);
            xFound = true;
          }
          someNum = slideHdr.get("y");
          if (someNum != null && someNum.length()>0) {
            jpegxy.y = atoi(someNum);
            yFound = true;
          }
        }
        if (xFound && yFound)
        {
          currIniConf.xyArr.add(jpegxy);
        }
      }
      if (currIniConf.xyArr.size()>0)
      {
        iniPaths.set(fileNum, inputName);
      }
    }
    
    yMin=0; yMax=0; xMin=0; xMax=0;
    boolean yMinSet=false, xMaxSet=false, xMinSet=false, yMaxSet=false;
    int totalLevels=0;
    for (int fileNum=0; fileNum < 4; fileNum++)
    {
      IniConf currIniConf=iniConf.get(fileNum);
      if (currIniConf.xyArr.size()==0) continue;
      totalLevels++;

      currIniConf.totalTiles = currIniConf.xyArr.size();
      if (currIniConf.pixelWidth<=0 || currIniConf.pixelHeight<=0)
      {
        JPEGReader reader = new JPEGReader();
        try {
          reader.setId(currIniConf.xyArr.get(0).fileName);
          currIniConf.pixelWidth=reader.getSizeX();
          currIniConf.pixelHeight=reader.getSizeY();
        } catch (IOException e) {
          LOGGER.error("Parsing jpeg failed on filename="+currIniConf.xyArr.get(0).fileName+" reason: "+e.getMessage());
          return;
        }
        finally {
          try { reader.close(); } catch (IOException e) { }
        }
      }
    
      JPEGReader reader = new JPEGReader();
      try {
        reader.setId(currIniConf.xyArr.get(0).fileName);
        currIniConf.pixelWidth=reader.getSizeX();
        currIniConf.pixelHeight=reader.getSizeY();
      } catch (IOException e) {
        LOGGER.error("Parsing jpeg failed on filename="+currIniConf.xyArr.get(0).fileName+" reason: "+e.getMessage());
        return;
      }
        finally {
          try { reader.close(); } catch (IOException e) { }
        }

      LOGGER.debug("fileName=" + currIniConf.name + " jpegWidth=" + currIniConf.pixelWidth + " jpegHeight=" + currIniConf.pixelHeight);
      currIniConf.found = true;
      
      //************************************************************************
      // Get the xmin and xmax values
      //************************************************************************
      Collections.sort(currIniConf.xyArr, new Comparator<JpegFileXY>() {
        @Override
        public int compare(JpegFileXY coord1, JpegFileXY coord2) {
          int result = coord1.x - coord2.x;
          if (result==0) result = coord1.y - coord2.y;
          return result;
        }
      });
 
      currIniConf.xMin = currIniConf.xyArr.get(0).x;
      currIniConf.xMax = currIniConf.xyArr.get(currIniConf.totalTiles-1).x;
      for (int i=0; i+1 < currIniConf.totalTiles; i++)
      {
        LOGGER.debug(" Sorted: x=" + currIniConf.xyArr.get(i).x + " y=" + currIniConf.xyArr.get(i).y);
        if (currIniConf.xyArr.get(i+1).x==currIniConf.xyArr.get(i).x)
        {
          int diff=currIniConf.xyArr.get(i+1).y - currIniConf.xyArr.get(i).y;
          if ((diff>0 && diff<currIniConf.yDiffMin) || (diff>0 && currIniConf.yDiffMin<1))
          {
            currIniConf.yDiffMin=diff;
          }
        }
      }

      //************************************************************************
      // Get the ymin and ymax values
      //************************************************************************
      Collections.sort(currIniConf.xyArr, new Comparator<JpegFileXY>() {
        @Override
        public int compare(JpegFileXY coord1, JpegFileXY coord2) {
          int result = coord1.y - coord2.y;
          if (result==0) result = coord1.x - coord2.x;
          return result;
        }
      });
      currIniConf.yMin=currIniConf.xyArr.get(0).y - currIniConf.yDiffMin;
      currIniConf.yMax=currIniConf.xyArr.get(currIniConf.totalTiles-1).y; // + currIniConf.yDiffMin;

      LOGGER.debug("fileName=" + currIniConf.name + " yDiffMin=" + currIniConf.yDiffMin + " yMin=" + currIniConf.yMin + " yMax=" + currIniConf.yMax + " yAxis=" + currIniConf.yAxis);
      for (int i=0; i+1 < currIniConf.totalTiles; i++)
      {
        //LOGGER.debug(" Sorted: x=" + currIniConf.xyArr[i].x + " y=" + currIniConf.xyArr.get(i).y);
        if (currIniConf.xyArr.get(i+1).y==currIniConf.xyArr.get(i).y)
        {
          int diff=currIniConf.xyArr.get(i+1).x - currIniConf.xyArr.get(i).x;
          if ((diff>0 && diff<currIniConf.xDiffMin) || (diff>0 && currIniConf.xDiffMin<1)) 
          {
            currIniConf.xDiffMin=diff;
          }
        }
      }
      currIniConf.xMin -= currIniConf.xDiffMin;
      if (fileNum<2)
      {
        //currIniConf.xMin += xOffset; // Note try removing this!
        //currIniConf.yMin += yOffset;
      }

      LOGGER.debug("fileName=" + currIniConf.name + " xDiffMin=" + currIniConf.xDiffMin + " xMin=" + currIniConf.xMin + " xMax=" + currIniConf.xMax);
      if (currIniConf.pixelWidth>0) 
      {
        if (currIniConf.xStepSize>0)
        {
          currIniConf.xAdj = (double) currIniConf.xStepSize / (double) currIniConf.pixelWidth;
        }
        else if (fileNum>0)
        {
          currIniConf.xAdj = (double) (iniConf.get(fileNum-1).xStepSize*4) / (double) currIniConf.pixelWidth;
          //currIniConf.xAdj = (double) (currIniConf.xDiffMin) / (double) currIniConf.pixelWidth;

        }
        LOGGER.debug("fileName=" + currIniConf.name + " Guessed xAdj=" + currIniConf.xAdj);
      }
      if (currIniConf.pixelHeight>0)
      {
        if (currIniConf.yStepSize>0)
        {
          currIniConf.yAdj = (double) currIniConf.yStepSize / (double) currIniConf.pixelHeight;
        }
        else
        {
          currIniConf.yAdj = (double) (iniConf.get(fileNum-1).yStepSize*4) / (double) currIniConf.pixelHeight;
          //currIniConf.yAdj = (double) (currIniConf.yDiffMin) / (double) currIniConf.pixelHeight;
        }
        LOGGER.debug("fileName=" + currIniConf.name + " Guessed yAdj=" + currIniConf.yAdj);
      }
      if ((yMinSet==false || currIniConf.yMin < yMin) && fileNum < 3)
      {
        yMin=currIniConf.yMin;
        yMinSet = true;
      }
      if ((yMaxSet==false || currIniConf.yMax > yMax) && fileNum < 3)
      {
        yMax=currIniConf.yMax;
        yMaxSet = true;
      }
      if ((xMinSet==false || currIniConf.xMin < xMin) && fileNum < 3)
      { 
        xMin=currIniConf.xMin;
        xMinSet = true;
      }
      if ((xMaxSet==false || currIniConf.xMax > xMax) && fileNum < 3)
      {
        xMax=currIniConf.xMax;
        xMaxSet = true;
      }
    }
    
    //*******************************************************************
    // Find the pyramid level lowest zoom and set that as current image
    //*******************************************************************
    level=-1;
    for (int min=3; min>=0; min--)
    {
      if (iniConf.get(min).found==true)
      {
        level=min;
        break;
      }
    }
    if (level==-1)
    {
      LOGGER.warn("File has no readable levels.");
      validObject = false;
      return;
    }
    validObject = true;

    //****************************************************************
    // Guess the total image width and height for each pyramid level
    //****************************************************************
    for (int fileNum=0; fileNum < 4; fileNum++)
    {
      IniConf currIniConf= iniConf.get(fileNum);
      
      int widthNextSlide;
      if (fileNum < 3)
      {
        widthNextSlide = iniConf.get(fileNum+1).xMax - iniConf.get(fileNum+1).xMin;
      }
      else
      {
        widthNextSlide = iniConf.get(fileNum).xMax - iniConf.get(fileNum).xMin;
      }
      int widthThisSlide = iniConf.get(fileNum).xMax - iniConf.get(fileNum).xMin;
      int slideWidthDiff = widthNextSlide-widthThisSlide;
      
      int heightNextSlide;
      if (fileNum < 3)
      {
        heightNextSlide = iniConf.get(fileNum+1).yMax - iniConf.get(fileNum+1).yMin;
      }
      else
      {
        heightNextSlide = iniConf.get(fileNum).yMax - iniConf.get(fileNum).yMin;
      }
      int heightThisSlide = iniConf.get(fileNum).yMax - iniConf.get(fileNum).yMin;
      int slideHeightDiff = heightNextSlide-heightThisSlide;
   
      currIniConf.totalWidth=(long)(((double)(xMax - xMin))/currIniConf.xAdj);
      currIniConf.totalHeight=(long)(((double)(yMax - yMin))/currIniConf.yAdj);

      if (level==fileNum)
      {
        actualWidth = currIniConf.totalWidth;
        actualHeight = currIniConf.totalHeight;
      }
      LOGGER.debug(" This Level Image Width=" + currIniConf.totalWidth + " Image Height=" + currIniConf.totalHeight);
    }

    //*****************************************************************
    // Calculate the x and y coordinate of each file starting pixels
    //*****************************************************************
    for (int fileNum=0; fileNum<4; fileNum++)
    {
      IniConf currIniConf=iniConf.get(fileNum);
      if (currIniConf.found==false) continue;
      
      int widthNextSlide;
      if (fileNum < 3)
      {
        widthNextSlide = iniConf.get(fileNum+1).xMax - iniConf.get(fileNum+1).xMin;
      }
      else
      {
        widthNextSlide = iniConf.get(fileNum).xMax - iniConf.get(fileNum).xMin;
      }
      int widthThisSlide = iniConf.get(fileNum).xMax - iniConf.get(fileNum).xMin;
      int slideWidthDiff = ((widthNextSlide-widthThisSlide)/2) - xOffset;
      
      int heightNextSlide;
      if (fileNum < 3)
      {
        heightNextSlide = iniConf.get(fileNum+1).yMax - iniConf.get(fileNum+1).yMin;
      }
      else
      {
        heightNextSlide = iniConf.get(fileNum).yMax - iniConf.get(fileNum).yMin;
      }
      int heightThisSlide = iniConf.get(fileNum).yMax - iniConf.get(fileNum).yMin;
      int slideHeightDiff = ((heightNextSlide-heightThisSlide)/2) - (2 * yOffset);

      for (int i=0; i<currIniConf.totalTiles; i++)
      {
        double xPixel;

        xPixel=(double)(xMax - currIniConf.xyArr.get(i).x)/(double)currIniConf.xAdj;
        currIniConf.xyArr.get(i).xPixel=(int)xPixel;
        
        double yPixel;
        yPixel=(double)(yMax - currIniConf.xyArr.get(i).y)/(double)currIniConf.yAdj;
        currIniConf.xyArr.get(i).yPixel=(int)yPixel;

        LOGGER.debug("filename=" + currIniConf.xyArr.get(i).fileName + " x=" + xPixel + " y=" + yPixel);
      }
      Collections.sort(currIniConf.xyArr, new Comparator<JpegFileXY>() {
        @Override
        public int compare(JpegFileXY coord1, JpegFileXY coord2) {
          int result = coord1.yPixel - coord2.yPixel;
          if (result == 0) result = coord1.xPixel - coord2.xPixel;
          return result;
        }
      });
    }
   
    Collections.sort(iniConf, new Comparator<IniConf>() {
      @Override
      public int compare(IniConf conf1, IniConf conf2) {
        long result = (long)(conf1.totalWidth * conf1.totalHeight) - (long)(conf2.totalWidth * conf2.totalHeight);
        if (result > 0) return -1;
        else if (result < 0) return 1;
        else return 0;
      }
    });

    baseWidth = iniConf.get(0).totalWidth;
    baseHeight = iniConf.get(0).totalHeight;
    LOGGER.debug("Total number of iniConf: " + iniConf.size());
    core.clear();
    for (int i = 0; i < 4; i++) {
      if (iniConf.get(i).found) {
       core.add(new CoreMetadata());
      }
    }
    int totalCores = 0;
    for (int i = 0; i < 4; i++) {
      if (iniConf.get(i).found) {
        CoreMetadata m = core.get(totalCores);
        m.sizeX = (int) iniConf.get(i).totalWidth;
        m.sizeY = (int) iniConf.get(i).totalHeight;
        m.sizeZ = 1;
        m.sizeC = 3;
        m.sizeT = 1;
        m.rgb = true;
        m.imageCount = 1;
        m.dimensionOrder = "XYCZT";
        m.pixelType = FormatTools.UINT8;
        m.interleaved = true;
        m.falseColor = false;
        m.thumbnail = false;
        m.orderCertain = true;
        m.bitsPerPixel = 8;
        m.thumbSizeX = 0;
        m.thumbSizeY = 0;
        for (int j = i+1; j < 4; j++) {
          if (iniConf.get(j).found) {
            m.thumbSizeX = (int) iniConf.get(j).totalWidth;
            m.thumbSizeY = (int) iniConf.get(j).totalHeight;
          }
        }
        m.littleEndian = false;
        m.indexed = false;
        if (i==0 && totalLevels > 2) 
        {
          //m.resolutionCount = 1;
          m.resolutionCount = totalLevels;
        }
        else 
        {
          m.thumbnail = true;
          m.resolutionCount = 1;
        }
        totalCores++;
      }
    }
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    String path = new Location(currentId).getAbsoluteFile().getName();

    if (totalLevels > 3)
    {
      DecimalFormat df = new DecimalFormat("##.##");
      df.setMaximumFractionDigits(2);
      //store.setImageName(path+" "+df.format(magnification) + "x", 0);
      store.setImageName("1 ("+df.format(magnification)+"x)", 0);
    }
    if (totalLevels > 2)
    {
      //store.setImageName(path+" 20x", totalLevels-3);
      store.setImageName("2 (20x)", totalLevels-3);
    }
    if (totalLevels > 1)
    {
      //store.setImageName(path+" 5x", totalLevels-2);
      store.setImageName("3 (5x)", totalLevels-2);
    }
    if (totalLevels > 0)
    {
      //store.setImageName(path+" 1.25x", totalLevels-1);
      store.setImageName("4 (1.25x)", totalLevels-1);
    }

    //CoreMetadata ms0 = core.get(0);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) 
    {
      String instrumentID = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrumentID, 0);

      String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
      store.setObjectiveID(objectiveID, 0, 0);
      store.setObjectiveNominalMagnification(magnification, 0, 0);

      store.setExperimenterID(MetadataTools.createLSID("Experimenter", 0), 0);


      totalCores = 0;
      for (int fileNum=0; fileNum < 4; fileNum++) 
      {
        IniConf currIniConf=iniConf.get(fileNum);
        if (currIniConf.found)
        {
          store.setImageInstrumentRef(instrumentID, totalCores);
          store.setObjectiveSettingsID(objectiveID, totalCores);
 
          Length sizeX = FormatTools.getPhysicalSizeX((double) currIniConf.xAdj);
          Length sizeY = FormatTools.getPhysicalSizeY((double) currIniConf.yAdj);
          if (sizeX != null) {
            store.setPixelsPhysicalSizeX(sizeX, totalCores);
          }
          if (sizeY != null) {
            store.setPixelsPhysicalSizeY(sizeY, totalCores);
          }
          totalCores++;
        }
      }
    }
    setSeries(0);
    return;
  }
}

