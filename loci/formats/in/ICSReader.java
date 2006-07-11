//
// ICSReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden, Chris Allan
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
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import java.util.zip.*;
import loci.formats.*;

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
  protected RandomAccessStream idsIn; // IDS file
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

  /** Flag indicating whether current file is v2.0. */
  protected boolean versionTwo;

  /** Image data. */
  protected byte[] data;

  /** Dimension order. */
  private String order;


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

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    return false;
  }

  /** Get the size of the X dimension. */
  public int getSizeX(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return dimensions[1];
  }

  /** Get the size of the Y dimension. */
  public int getSizeY(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return dimensions[2];
  }

  /** Get the size of the Z dimension. */
  public int getSizeZ(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return dimensions[3];
  }

  /** Get the size of the C dimension. */
  public int getSizeC(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return dimensions[4];
  }

  /** Get the size of the T dimension. */
  public int getSizeT(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return dimensions[5];
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return littleEndian;
  }

  /**
   * Return a five-character string representing the dimension order
   * within the file.
   */
  public String getDimensionOrder(String id) throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return order;
  }

  /** Obtains the specified image from the given ICS file, as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentIdsId) && !id.equals(currentIcsId)) initFile(id);

    int width = dimensions[1];
    int height = dimensions[2];
    int numSamples = width * height;
    int channels = 1;

    int offset = width * height * (dimensions[0] / 8) * no;
    byte[] plane = new byte[width * height * (dimensions[0] / 8)];
    System.arraycopy(data, offset, plane, 0, plane.length);

    // if it's version two, we need to flip the plane upside down
    if (versionTwo) {
      byte[] t = new byte[plane.length];
      int len = width * (dimensions[0] / 8);
      int off = (height - 1) * len;
      int newOff = 0;
      for (int i=0; i<height; i++) {
        System.arraycopy(plane, off, t, newOff, len);
        off -= len;
        newOff += len;
      }
      return t;
    }
    return plane;
  }

  /** Obtains the specified image from the given ICS file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentIdsId) && !id.equals(currentIcsId)) initFile(id);

    byte[] plane = openBytes(id, no);
    int width = dimensions[1];
    int height = dimensions[2];
    int numSamples = width * height;
    int channels = 1;

    if (dimensions[0] == 32) {
      int[][] b = new int[1][plane.length / 4];
      for (int i=0; i<b[0].length; i++) {
        b[0][i] = DataTools.bytesToInt(plane, i*4, 4, littleEndian);
      }
      return ImageTools.makeImage(b, width, height);
    }
    else return ImageTools.makeImage(plane, width, height, channels, false,
      dimensions[0] / 8, littleEndian);
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

    // check if we have a v2 ICS file
    RandomAccessFile f = new RandomAccessFile(icsId, "r");
    byte[] b = new byte[17];
    f.read(b);
    if (new String(b).trim().equals("ics_version\t2.0")) {
      idsIn = new RandomAccessStream(icsId);
      versionTwo = true;
    }
    else {
      if (idsId == null) throw new FormatException("No IDS file found.");
      File idsFile = new File(idsId);
      if (!idsFile.exists()) throw new FormatException("IDS file not found.");
      currentIdsId = idsId;
      idsIn = new RandomAccessStream(idsId);
    }

    currentIcsId = icsId;

    icsIn = icsFile;

    BufferedReader reader = new BufferedReader(new FileReader(icsIn));
    String line = reader.readLine();
    line = reader.readLine();
    StringTokenizer t;
    String token;
    while (line != null && !line.trim().equals("end")) {
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
    String ord = (String) metadata.get("order");
    // bpp, width, height, z, channels
    StringTokenizer t1 = new StringTokenizer(images);
    StringTokenizer t2 = new StringTokenizer(ord);

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

    int width = dimensions[1];
    int height = dimensions[2];

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

    String test = (String) metadata.get("compression");
    boolean gzip = (test == null) ? false : test.equals("gzip");

    if (versionTwo) {
      String s = idsIn.readLine();
      while(!s.trim().equals("end")) s = idsIn.readLine();
    }
    data = new byte[(int) (idsIn.length() - idsIn.getFilePointer())];

    // extra check is because some of our datasets are labeled as 'gzip', and
    // have a valid GZIP header, but are actually uncompressed
    if (gzip &&
      ((data.length / (numImages) < (width * height * dimensions[0]/8))))
    {
      idsIn.read(data);
      byte[] buf = new byte[8192];
      ByteVector v = new ByteVector();
      try {
        GZIPInputStream decompressor =
          new GZIPInputStream(new ByteArrayInputStream(data));
        int r = decompressor.read(buf, 0, buf.length);
        while (r > 0) {
          v.add(buf, 0, r);
          r = decompressor.read(buf, 0, buf.length);
        }
        data = v.toByteArray();
      }
      catch (Exception dfe) {
        dfe.printStackTrace();
        throw new FormatException("Error uncompressing gzip'ed data.");
      }
    }
    else idsIn.readFully(data);

    // Populate metadata store

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore();

    store.setImage((String) metadata.get("filename"), null, null, null);

    // populate Pixels element

    String o = (String) metadata.get("order");
    o = o.substring(o.indexOf("x"));
    char[] tempOrder = new char[(o.length() / 2) + 1];
    int pt = 0;
    for (int i=0; i<o.length(); i+=2) {
      tempOrder[pt] = o.charAt(i);
      pt++;
    }
    o = new String(tempOrder).toUpperCase();
    if (o.indexOf("Z") == -1) o = o + "Z";
    if (o.indexOf("T") == -1) o = o + "T";
    if (o.indexOf("C") == -1) o = o + "C";

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

    order = o;

    store.setPixels(
      new Integer(dimensions[1]), // SizeX
      new Integer(dimensions[2]), // SizeY
      new Integer(dimensions[3]), // SizeZ
      new Integer(dimensions[4]), // SizeC
      new Integer(dimensions[5]), // SizeT
      type, // PixelType
      new Boolean(!littleEndian), // BigEndian
      ord, // DimensionOrder
      null); // Use index 0
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new ICSReader().testRead(args);
  }

}
