//
// OMEXMLReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

import java.awt.Image;
import java.io.*;

/**
 * OMEXMLReader is the file format reader for OME-XML files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class OMEXMLReader extends FormatReader {
  // **** CURRENT ISSUES ****
  // 1) when root node is created, all pixel data is lost
  // 2) don't know what the possible pixel compression types are
  // 3) Pixels/BigEndian attribute value not available (see OME2OME-CA.xslt)
        
  // -- Fields --

  /** Current file. */
  protected RandomAccessFile in;

  /** Flag indicating whether current file is little endian. */
  protected boolean littleEndian;

  /** Number of image planes in the file. */
  protected int numImages = 0;

  /** Number of bits per pixel. */
  protected int bpp = 1;
  
  // -- Constructor --

  /** Constructs a new OME-XML reader. */
  public OMEXMLReader() { super("OME-XML", "ome"); }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for an OME-XML file. */
  public boolean isThisType(byte[] block) {
    return false;
  }

  /** Determines the number of images in the given OME-XML file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numImages;
  }

  /** Obtains the specified image from the given OME-XML file. */
  public Image open(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    int width = Integer.parseInt(OMETools.getAttribute(ome, "Pixels", "SizeX"));
    int height = 
      Integer.parseInt(OMETools.getAttribute(ome, "Pixels", "SizeY"));
    int channels = 1;  
  
    // should grab data from next "BinData" element
    String binData = OMETools.getAttribute(ome, "Pixels", "Bin:BinData", no);
    byte[] data = binData.getBytes();
    
    String compression = 
      OMETools.getAttribute(ome, "Bin:BinData", "Compression");    
    byte[] pixels = new byte[0];

    if (compression.equals("zlib")) {
      pixels = TiffTools.deflateUncompress(data);
    }        
    
    return DataTools.makeImage(pixels, width, height, channels, false);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given OME-XML file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessFile(id, "r");

    // read in the whole file as a string
    byte[] data = new byte[(int) in.length()];
    in.readFully(data);
    String xml = new String(data);
    ome = OMETools.createRoot(xml);

    /* debug */
    System.out.println(OMETools.dumpXML(ome));
    
    if (ome != null) {
      // can't calculate numImages from Z/T/C dimensions, since SizeC can be
      // greater than 1 when there is only 1 plane (in the case of RGB data)
            
      numImages = OMETools.getNumNodes(ome, "Bin:BinData");     
     
      // can't retrieve the Pixels/BigEndian attribute value right now
      littleEndian = false;
      
      String type = OMETools.getAttribute(ome, "Pixels", "PixelType");
      if (type.endsWith("16")) bpp = 2;
      else if (type.endsWith("32")) bpp = 4;
      else if (type.equals("float")) bpp = 8;
      
      // set up a simple metadata hash table
      // this isn't really necessary for preserving metadata, but it
      // might be useful for displaying stuff

    }
    else {
      throw new FormatException("To use this feature, please install the " +
       "loci.ome.xml package, available from http://www.loci.wisc.edu/ome/");
    } 
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new OMEXMLReader().testRead(args);
  }

}
