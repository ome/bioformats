//
// LIFReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Vector;

/**
 * LIFReader is the file format reader for Leica LIF files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class LIFReader extends FormatReader {

  // -- Fields --

  /** Current file. */
  protected RandomAccessFile in;

  /** Flag indicating whether current file is little endian. */
  protected boolean littleEndian;

  /** Number of image planes in the file. */
  protected int numImages = 0;

  /** Offsets to memory blocks, paired with their corresponding description. */
  protected Vector offsets;

  /** 
   * Dimension information for each image.
   * The first index specifies the image number, and the second specifies
   * the dimension from the following list:
   * 0) width
   * 1) height
   * 2) Z
   * 3) T
   * 4) channels (1 or 3)
   * 5) bits per pixel
   */
  protected int[][] dims;
  
  
  // -- Constructor --

  /** Constructs a new Leica LIF reader. */
  public LIFReader() { super("Leica Image File Format", "lif"); }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a LIF file. */
  public boolean isThisType(byte[] block) {
    return block[0] == 0x70;
  }

  /** Determines the number of images in the given LIF file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numImages;
  }

  /** Obtains the specified image from the given LIF file. */
  public BufferedImage open(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    int ndx = 0;
    int sum = 0;
    for (int i=0; i<dims.length; i++) {
      sum += (dims[i][2] * dims[i][3]);       
      if (no < sum) {
        ndx = i;
        i = dims.length;
      }  
    }       

    int width = dims[ndx][0];
    int height = dims[ndx][1];
    int bps = dims[ndx][5];
    double bytesPerPixel = bps / 8;
    
    int offset = ((Long) offsets.get(0)).intValue();
    in.seek((long) (offset + (width * height * bytesPerPixel * no)));
    byte[] data = new byte[(int) (width * height * bytesPerPixel * 3)];
    in.read(data);
   
    // pack the data appropriately

    if (bps == 8) { 
      return ImageTools.makeImage(data, width, height, 3, false);
    }
    else if (bps == 16) {
      short[] shortData = new short[width*height*3];
      for (int i=0; i<data.length; i+=2) {
        shortData[i/2] = DataTools.bytesToShort(data, i, littleEndian);
      }        
      return ImageTools.makeImage(shortData, width, height, 3, false);
    }
    else if (bps == 32) {
      float[] floatData = new float[width*height*3];
      for (int i=0; i<data.length; i+=4) {
        floatData[i/4] = 
          Float.intBitsToFloat(DataTools.bytesToInt(data, i, littleEndian));
      }        
      return ImageTools.makeImage(floatData, width, height, 3, false);
    }
    else {
      throw new FormatException("Sorry, bits per sample " + bps + 
        " not supported");        
    }
  }   

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given LIF file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    offsets = new Vector();
    in = new RandomAccessFile(id, "r");
    littleEndian = true;
    
    // read the header
    
    byte[] header = new byte[8];
    in.read(header);

    if ((header[0] != 0x70) && (header[3] != 0x70)) {
      throw new FormatException(id + " is not a valid Leica LIF file");
    }       

    int chunkLength = DataTools.bytesToInt(header, 4, 4, littleEndian);

    // read and parse the XML description
    
    byte[] xmlChunk = new byte[chunkLength];
    in.read(xmlChunk);

    if (xmlChunk[0] != 0x2a) {
      throw new FormatException("Invalid XML description");
    }        

    // number of Unicode characters in the XML block
    int nc = DataTools.bytesToInt(xmlChunk, 1, 4, littleEndian);
    String xml = new String(xmlChunk, 5, nc*2);
    xml = LeicaReader.stripString(xml);
  
    while (in.getFilePointer() < in.length()) {
      byte[] four = new byte[4];
      in.read(four);
      int check = DataTools.bytesToInt(four, littleEndian);
      if (check != 0x70) {
        throw new FormatException("Invalid Memory Block");
      }        
  
      in.read(four);
      int memLength = DataTools.bytesToInt(four, littleEndian);
    
      if (in.read() != 0x2a) {
        throw new FormatException("Invalid Memory Description");
      }          

      in.read(four);
      int blockLength = DataTools.bytesToInt(four, littleEndian);

      if (in.read() != 0x2a) {
        throw new FormatException("Invalid Memory Description");
      }       

      in.read(four);
      int descrLength = DataTools.bytesToInt(four, littleEndian);

      byte[] memDescr = new byte[2*descrLength];
      in.read(memDescr);
      String descr = new String(memDescr);
      descr = LeicaReader.stripString(descr);

      if (blockLength > 0) {
        offsets.add(new Long(in.getFilePointer()));
      }  
      in.skipBytes(blockLength);
    }
    numImages = offsets.size();
    dims = new int[numImages][6];
    initMetadata(xml);
  }

  // -- Utility methods --
  
  /** Parses a string of XML and puts the values in a Hashtable. */
  private void initMetadata(String xml) {
    Vector elements = new Vector();

    // first parse each element in the XML string

    while (xml.length() > 2) {
      String el = xml.substring(1, xml.indexOf(">"));
      xml = xml.substring(xml.indexOf(">") + 1);
      elements.add(el); 
    }

    // the first element contains version information

    String token = (String) elements.get(0);
    String key = token.substring(0, token.indexOf("\""));
    String value = token.substring(token.indexOf("\"") + 1, token.length()-1);
    metadata.put(key, value);

    int ndx = 1;
    int imageCounter = -2;
    int dimCounter = 0;
    int lutCounter = 0;
    while (ndx < elements.size()) {
      token = (String) elements.get(ndx);

      // only try to parse the element if we know it
      // contains a key/value pair
      if (token.indexOf("=") != -1) {
        while (token.length() > 2) {
          if (key.equals("Element Name")) {
            imageCounter++;
            dimCounter = 0;
            lutCounter = 0;
          }
           
          key = token.substring(0, token.indexOf("\"") - 1);
          value = token.substring(token.indexOf("\"") + 1,
            token.indexOf("\"", token.indexOf("\"") + 1));
          key = key.trim();
          value = value.trim();
                
          if (key.equals("NumberOfElements")) {
            dims[imageCounter][dimCounter] = Integer.parseInt(value);
            dimCounter++;
            if (dimCounter == 6) dimCounter = 0;
          }       
 
          if (key.equals("Resolution")) {
            int val = Integer.parseInt(value);
            if ((val % 8) != 0) val += (8 - (val % 8));
            dims[imageCounter][5] = val;
          }        
          
          if (key.equals("LUTName")) lutCounter++;
          if (lutCounter == 3) {
            dims[imageCounter][4] = lutCounter;      
            lutCounter = 0;
          }        
         
          token = 
            token.substring(token.indexOf("\"", token.indexOf("\"") + 1) + 1);
          metadata.put(key + " (image " + imageCounter + ")", value);
        }        
      }
      ndx++;
    }
     
    int originalNumImages = numImages;
    for (int i=1; i<=originalNumImages; i++) {
      if (dims[i-1][2] == 0) dims[i-1][2] = 1;
      if (dims[i-1][3] == 0) dims[i-1][3] = 1;
      numImages += ((dims[i-1][2]*dims[i-1][3]) - 1);
    }

    // initialize OME-XML

    if (ome != null) {
      String type = "int8";
      switch (dims[dims.length - 1][5]) {
        case 12: type = "int16"; break;
        case 16: type = "int16"; break;
        case 32: type = "float"; break;
      }
            
      int z = 0;
      int t = 0;
      for (int i=0; i<dims.length; i++) {
        z += (dims[i][2] == 1) ? 0 : dims[i][2];
        t += (dims[i][3] == 1) ? 0 : dims[i][3];
      }       

      if (t == 0) t++;
      if (z == 0) z++;
      while ((z*t) < numImages) {
        z++; 
      }         
            
      OMETools.setPixels(ome,
        new Integer(dims[dims.length - 1][0]), // SizeX
        new Integer(dims[dims.length - 1][1]), // SizeY
        new Integer(z), // SizeZ
        new Integer(1), // SizeC
        new Integer(t), // SizeT
        type, // PixelType
        new Boolean(!littleEndian), // BigEndian
        "XYZTC"); // DimensionOrder
    }
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new LIFReader().testRead(args);
  }

}
