//
// ICSReader.java
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
import java.util.StringTokenizer;
import java.util.NoSuchElementException;

/**
 * ICSReader is the file format reader for ICS (Image Cytometry Standard)
 * files. More information on ICS can be found at http://libics.sourceforge.net
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */

public class ICSReader extends FormatReader {

  // -- Fields --

  /** Current filename. */
  protected String currentIcsId;
  protected String currentIdsId;

  /** Current file. */
  protected RandomAccessFile idsIn; // IDS file
  protected File icsIn; // ICS file

  /** Flag indicating whether current file is little endian. */
  protected boolean littleEndian;

  /** Number of images. */
  protected int numImages;

  /**
   * Dimensions in the following order:
   * 1) bits per pixel
   * 2) width
   * 3) height
   * 4) z
   * 5) channels
   * 6) timepoints
   */
  protected int[] dimensions = new int[6];


  // -- Constructor --

  /** Constructs a new ICSReader. */
  public ICSReader() {
    super("Image Cytometry Standard", new String[] {"ics", "ids"});
  }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for an ICS file. */
  public boolean isThisType(byte[] block) {
    return false;
  }

  /** Determines the number of images in the given ICS file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if(!id.equals(currentIdsId) && !id.equals(currentIcsId)) initFile(id);
    return numImages;
  }

  /**
   * Obtains the bytes for the specified image from the given ICS file.
   * Note : this method will be added to the FormatReader API.
   */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentIdsId) && !id.equals(currentIcsId)) initFile(id);

    int width = dimensions[1];
    int height = dimensions[2];
    int numSamples = width * height;
    int channels = 1;

    idsIn.seek((dimensions[0]/8) * width * height * no);
    byte[] data = new byte[(dimensions[0]/8) * width * height];
    idsIn.readFully(data);

    if(dimensions[0] == 8) {
      // case for 8 bit data
      return data;
    }
    else if(dimensions[0] == 16) {
      // case for 16 bit data
      byte[] rawData = new byte[channels * numSamples];
      for (int i=0; i<rawData.length; i++) {
        rawData[i] = (byte) DataTools.bytesToShort(data, 2 * i, littleEndian);
      }
      return rawData;
    }
    else if(dimensions[0] == 32) {
      // case for 32 bit data -- could be broken
      byte[] rawData = new byte[channels * numSamples];
      for (int i=0; i<rawData.length; i++) {
        rawData[i] = (byte) DataTools.bytesToInt(data, 4 * i, littleEndian);
      }
      return rawData;
    }
    else throw new FormatException("Sorry, " +
      dimensions[0] + " bits per sample is not supported");
  }

  /** Obtains the specified image from the given ICS file. */
  public Image open(String id, int no) throws FormatException, IOException {
    if(!id.equals(currentIdsId) && !id.equals(currentIcsId)) initFile(id);

    byte[] data = openBytes(id, no);
    int width = dimensions[1];
    int height = dimensions[2];
    int numSamples = width * height;
    int channels = 1;

    return ImageTools.makeImage(data, width, height, channels, false);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (idsIn != null) idsIn.close();
    idsIn = null;
    icsIn = null;
    currentIcsId = null;
    currentIdsId = null;
  }

  /** Initializes the given IPLab file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    String icsId = id, idsId = id;
    int dot = id.lastIndexOf(".");
    String ext = dot < 0 ? "" : id.substring(dot + 1).toLowerCase();
    if(ext.equals("ics")) {
      // convert C to D regardless of case
      char[] c = idsId.toCharArray();
      c[c.length - 2]++;
      idsId = new String(c);
    }
    else if(ext.equals("ids")) {
      // convert D to C regardless of case
      char[] c = icsId.toCharArray();
      c[c.length - 2]--;
      id = icsId = new String(c);
    }

    if (icsId == null) throw new FormatException("No ICS file found.");
    File icsFile = new File(icsId);
    if (!icsFile.exists()) throw new FormatException("ICS file not found.");
    if (idsId == null) throw new FormatException("No IDS file found.");
    File idsFile = new File(idsId);
    if (!idsFile.exists()) throw new FormatException("IDS file not found.");
    currentIcsId = icsId;
    currentIdsId = idsId;
    icsIn = icsFile;
    idsIn = new RandomAccessFile(currentIdsId, "r");

    BufferedReader reader = new BufferedReader(new FileReader(icsIn));
    String line = reader.readLine();
    line = reader.readLine();
    StringTokenizer t;
    String token;
    while (line != null) {
      t = new StringTokenizer(line);
      while(t.hasMoreTokens()) {
        token = t.nextToken();
        if (!token.equals("layout") && !token.equals("representation") &&
          !token.equals("parameter") && !token.equals("history") &&
          !token.equals("sensor"))
        {
          if (t.countTokens() < 3) {
            try {
              metadata.put(token, t.nextToken());
            }
            catch (NoSuchElementException e) { }
          }
          else {
            String meta = t.nextToken();
            while (t.hasMoreTokens()) {
              meta = meta + " " + t.nextToken();
            }
            metadata.put(token, meta);
          }
        }
      }
      line = reader.readLine();
    }

    String images = (String) metadata.get("sizes");
    String order = (String) metadata.get("order");
    // bpp, width, height, z, channels
    StringTokenizer t1 = new StringTokenizer(images);
    StringTokenizer t2 = new StringTokenizer(order);

    for(int i=0; i<dimensions.length; i++) {
      dimensions[i] = 1;
    }

    String imageToken;
    String orderToken;
    while (t1.hasMoreTokens() && t2.hasMoreTokens()) {
      imageToken = t1.nextToken();
      orderToken = t2.nextToken();
      if (orderToken.equals("bits")) {
        dimensions[0] = Integer.parseInt(imageToken);
      }
      else if(orderToken.equals("x")) {
        dimensions[1] = Integer.parseInt(imageToken);
      }
      else if(orderToken.equals("y")) {
        dimensions[2] = Integer.parseInt(imageToken);
      }
      else if(orderToken.equals("z")) {
        dimensions[3] = Integer.parseInt(imageToken);
      }
      else if(orderToken.equals("ch")) {
        dimensions[4] = Integer.parseInt(imageToken);
      }
      else {
        dimensions[5] = Integer.parseInt(imageToken);
      }
    }

    numImages = dimensions[3] * dimensions[4] * dimensions[5];

    String endian = (String) metadata.get("byte_order");
    littleEndian = true;

    if (endian != null) {
      StringTokenizer endianness = new StringTokenizer(endian);
      int firstByte = 0;
      int lastByte = 0;

      for(int i=0; i<endianness.countTokens(); i++) {
        if (i == 0) firstByte = Integer.parseInt(endianness.nextToken());
        else lastByte = Integer.parseInt(endianness.nextToken());
      }
      if (lastByte < firstByte) littleEndian = false;
    }

    // initialize OME metadata

    if (ome != null) {
      OMETools.setAttribute(ome, "Pixels", "SizeX", "" + dimensions[1]);
      OMETools.setAttribute(ome, "Pixels", "SizeY", "" + dimensions[2]);
      OMETools.setAttribute(ome, "Pixels", "SizeZ", "" + dimensions[3]);
      OMETools.setAttribute(ome, "Pixels", "SizeC", "" + dimensions[4]);
      OMETools.setAttribute(ome, "Pixels", "SizeT", "" + dimensions[5]);
      OMETools.setAttribute(ome, "Pixels", "BigEndian", "" + !littleEndian);
      OMETools.setAttribute(ome, "Image", "Name",
        "" + metadata.get("filename"));

      String ord = (String) metadata.get("order");
      ord = ord.substring(ord.indexOf("x"));
      char[] tempOrder = new char[(ord.length() / 2) + 1];
      int pt = 0;
      for (int i=0; i<ord.length(); i+=2) {
        tempOrder[pt] = ord.charAt(i);
        pt++;
      }
      ord = new String(tempOrder);
      ord = order.toUpperCase();

      if (ord.indexOf("Z") == -1) ord = ord + "Z";
      if (ord.indexOf("T") == -1) ord = ord + "T";
      if (ord.indexOf("C") == -1) ord = ord + "C";
      OMETools.setAttribute(ome, "Pixels", "DimensionOrder", ord);

      String bits = (String) metadata.get("significant_bits");
      String fmt = (String) metadata.get("format");
      String sign = (String) metadata.get("sign");

      String type;
      if (sign.equals("unsigned")) type = "U";
      else type = "";

      if (fmt.equals("real")) type = "float";
      else if (fmt.equals("integer")) {
        type = type + "int" + bits;
      }
      OMETools.setAttribute(ome, "Pixels", "PixelType", type);
    }
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new ICSReader().testRead(args);
  }

}
