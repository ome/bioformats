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
import java.util.Hashtable;
import java.util.Vector;

/**
 * LIFReader is the file format reader for Leica LIF files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class LIFReader extends FormatReader {

  // -- Fields --

  /** Current file. */
  protected RandomAccessStream in;

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
   * 6) extra dimensions
   */
  protected int[][] dims;

  private int width;
  private int height;
  private int c;
  private int bpp;

  
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
    return (!isRGB(id) || !separated) ? numImages : dims[0][4] * numImages;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return dims[0][4] > 1;
  }        
  
  /** Obtains the specified image from the given LIF file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    int ndx = 0;
    int sum = 0;
    for (int i=0; i<dims.length; i++) {
      sum += (dims[i][2] * dims[i][3] * dims[i][6]);
      if ((no / dims[0][4]) < sum) {
        ndx = i;
        i = dims.length;
      }
    }

    width = dims[ndx][0];
    height = dims[ndx][1];
    c = dims[ndx][4];
    if (c == 2) c--;
    bpp = dims[ndx][5];
    while (bpp % 8 !=0) bpp++;
    int bytesPerPixel = bpp / 8;

    int offset = ((Long) offsets.get(ndx)).intValue();
  
    // get the image number within this dataset

    int imageNum = no;
    for (int i=0; i<ndx; i++) {
      imageNum -= (dims[i][2] * dims[i][3] * dims[i][6]);
    }

    if (no == 0) {
      /* debug */ System.out.println("width : " + width);
      /* debug */ System.out.println("height : " + height);
      /* debug */ System.out.println("bpp : " + bpp);
      /* debug */ System.out.println("c : " + c);
    }        
    
    in.seek(offset + width * height * bytesPerPixel * imageNum);

    byte[] data = new byte[(int) (width * height * bytesPerPixel * c)];
    in.read(data);
   
    if (isRGB(id) && separated) {
      return ImageTools.splitChannels(data, 3, false, true)[no % 3];
    }
    else {
      return data;        
    }
  }

  /** Obtains the specified image from the given LIF file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    return ImageTools.makeImage(openBytes(id, no), width, height, 
      (!isRGB(id) || separated) ? 1 : c, false, bpp / 8, littleEndian);
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
    in = new RandomAccessStream(id);

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
    xml = DataTools.stripString(xml);

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
      descr = DataTools.stripString(descr);

      if (blockLength > 0) {
        offsets.add(new Long(in.getFilePointer()));
      }
      in.skipBytes(blockLength);
    }
    numImages = offsets.size();
    initMetadata(xml);
  }


  // -- Helper methods --

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

    // what we have right now is a vector of XML elements, which need to
    // be parsed into the appropriate image dimensions

    int ndx = 1;
    numImages = 0;
    int dimCounter = 0;
    int lutCounter = 0;

    // the image data we need starts with the token "ElementName='blah'" and
    // ends with the token "/ImageDescription"

    int numDatasets = 0;
    Vector widths = new Vector();
    Vector heights = new Vector();
    Vector zs = new Vector();
    Vector ts = new Vector();
    Vector channels = new Vector();
    Vector bps = new Vector();
    Vector extraDims = new Vector();

    while (ndx < elements.size()) {
      token = (String) elements.get(ndx);

      // if the element contains a key/value pair, parse it and put it in
      // the metadata hashtable

      String tmpToken = token;
      if (token.indexOf("=") != -1) {
        while (token.length() > 2) {
          key = token.substring(0, token.indexOf("\"") - 1);
          value = token.substring(token.indexOf("\"") + 1,
            token.indexOf("\"", token.indexOf("\"") + 1));

          token = token.substring(key.length() + value.length() + 3);

          key = key.trim();
          value = value.trim();
          metadata.put(key, value);
        }
      }
      token = tmpToken;

      if (token.startsWith("ElementName")) {
        // loop until we find "/ImageDescription"

        numDatasets++;
        int numChannels = 0;

        while (token.indexOf("/ImageDescription") == -1) {
          if (token.indexOf("=") != -1) {
            // create a small hashtable to store just this element's data

            Hashtable tmp = new Hashtable();
            while (token.length() > 2) {
              key = token.substring(0, token.indexOf("\"") - 1);
              value = token.substring(token.indexOf("\"") + 1,
                token.indexOf("\"", token.indexOf("\"") + 1));

              token = token.substring(key.length() + value.length() + 3);

              key = key.trim();
              value = value.trim();
              tmp.put(key, value);
            }

            if (tmp.get("ChannelDescriptionDataType") != null) {
              // found channel description block
              numChannels++;
              if (numChannels == 1) {
                bps.add(new Integer((String) tmp.get("Resolution")));
              }
            }
            else if (tmp.get("DimensionDescriptionDimID") != null) {
              // found dimension description block

              int w = Integer.parseInt((String) tmp.get("NumberOfElements"));
              int id = Integer.parseInt((String)
                tmp.get("DimensionDescriptionDimID"));

              int extras = 1;

              switch (id) {
                case 1: widths.add(new Integer(w)); break;
                case 2: heights.add(new Integer(w)); break;
                case 3: zs.add(new Integer(w)); break;
                case 4: ts.add(new Integer(w)); break;
                default: extras *= w;
              }

              extraDims.add(new Integer(extras));
            }
          }

          ndx++;
          token = (String) elements.get(ndx);
        }
        channels.add(new Integer(numChannels));

        if (zs.size() < channels.size()) zs.add(new Integer(1));
        if (ts.size() < channels.size()) ts.add(new Integer(1));
      }
      ndx++;
    }

    dims = new int[numDatasets][7];

    for (int i=0; i<numDatasets; i++) {
      dims[i][0] = ((Integer) widths.get(i)).intValue();
      dims[i][1] = ((Integer) heights.get(i)).intValue();
      dims[i][2] = ((Integer) zs.get(i)).intValue();
      dims[i][3] = ((Integer) ts.get(i)).intValue();
      dims[i][4] = ((Integer) channels.get(i)).intValue();
      dims[i][5] = ((Integer) bps.get(i)).intValue();
      dims[i][6] = ((Integer) extraDims.get(i)).intValue();

      numImages += (dims[i][2] * dims[i][3] * dims[i][6]);
    }

    // initialize OME-XML

    if (ome != null) {
      for (int i=0; i<dims.length; i++) {
        String type = "int8";
        switch (dims[i][5]) {
          case 12: type = "int16"; break;
          case 16: type = "int16"; break;
          case 32: type = "float"; break;
        }

        OMETools.setPixels(ome,
          new Integer(dims[i][0]), // SizeX
          new Integer(dims[i][1]), // SizeY
          new Integer(dims[i][2]), // SizeZ
          new Integer(dims[i][4]), // SizeC
          new Integer(dims[i][3]), // SizeT
          type, // PixelType
          new Boolean(!littleEndian), // BigEndian
          "XYZTC", i); // DimensionOrder
      }
    }
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new LIFReader().testRead(args);
  }

}
