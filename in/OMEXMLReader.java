//
// OMEXMLReader.java
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
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.*;
import loci.formats.*;

/**
 * OMEXMLReader is the file format reader for OME-XML files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class OMEXMLReader extends FormatReader {

  // -- Fields --

  /** Current file. */
  protected RandomAccessStream in;

  /** Flag indicating whether current file is little endian. */
  protected boolean littleEndian;

  /** Number of image planes in the file. */
  protected int numImages = 0;

  /** Number of bits per pixel. */
  protected int bpp = 1;

  /** Offset to each plane's data. */
  protected Vector offsets;

  /** String indicating the compression type. */
  protected String compression;

  /** Image width. */
  private int width;

  /** Image height. */
  private int height;

  /** Number of channels. */
  private int numChannels;
  
  /** Internal OME-XML metadata store that we use for parsing metadata from the
   * OME-XML file itself.
   */
  private OMEXMLMetadataStore internalStore;

  // -- Constructor --

  /** Constructs a new OME-XML reader. */
  public OMEXMLReader() { super("OME-XML", "ome"); }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for an OME-XML file. */
  public boolean isThisType(byte[] block) {
    return new String(block, 0, 5).equals("<?xml");
  }

  /** Determines the number of images in the given OME-XML file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numImages;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    return false;
  }

  /** Returns the number of channels in the file. */
  public int getChannelCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numChannels;
  }

  /** Obtains the specified image from the given file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= numImages) {
      throw new FormatException("Invalid image number: " + no);
    }

    width = internalStore.getSizeX(null).intValue();
    height = internalStore.getSizeY(null).intValue();

    in.seek(((Integer) offsets.get(no)).intValue());

    byte[] buf;
    if (no < getImageCount(id) - 1) {

      buf = new byte[((Integer) offsets.get(no+1)).intValue() -
        ((Integer) offsets.get(no)).intValue()];
    }
    else {
      buf =
        new byte[(int) (in.length() - ((Integer) offsets.get(no)).intValue())];
    }
    in.read(buf);
    String data = new String(buf);

    // retrieve the compressed pixel data

    int dataStart = data.indexOf(">") + 1;
    String pix = data.substring(dataStart);
    if (pix.indexOf("<") > 0) {
      pix = pix.substring(0, pix.indexOf("<"));
    }

    byte[] pixels = Compression.decode(pix);

    if (compression.equals("bzip2")) {
      byte[] tempPixels = pixels;
      pixels = new byte[tempPixels.length - 2];
      System.arraycopy(tempPixels, 2, pixels, 0, pixels.length);

      ByteArrayInputStream bais = new ByteArrayInputStream(pixels);
      CBZip2InputStream bzip = new CBZip2InputStream(bais);
      pixels = new byte[width*height*bpp];
      for (int i=0; i<pixels.length; i++) {
        pixels[i] = (byte) bzip.read();
      }
    }
    else if (compression.equals("zlib")) {
      try {
        Inflater decompressor = new Inflater();
        decompressor.setInput(pixels, 0, pixels.length);
        pixels = new byte[width * height * bpp];
        int resultLength = decompressor.inflate(pixels);
        decompressor.end();
      }
      catch (DataFormatException dfe) {
        throw new FormatException("Error uncompressing zlib data.");
      }
    }
    return pixels;
  }

  /** Obtains the specified image from the given OME-XML file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    return ImageTools.makeImage(openBytes(id, no), width, height,
      1, false, bpp, littleEndian);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given OME-XML file. */
  protected void initFile(String id) throws FormatException, IOException {
    close();
    currentId = id;
    metadata = new Hashtable();
    in = new RandomAccessStream(id);
    offsets = new Vector();

    in.skipBytes(200);

    // read a block of 8192 characters, looking for the "BigEndian" pattern
    byte[] buf = new byte[8192];
    boolean found = false;
    while (!found) {
      if (in.getFilePointer() < in.length()) {
        in.read(buf, 9, 8183);
        String test = new String(buf);

        int ndx = test.indexOf("BigEndian");
        if (ndx != -1) {
          found = true;
          String endian = test.substring(ndx + 11);
          if (endian.toLowerCase().startsWith("t")) littleEndian = false;
          else littleEndian = true;
        }
      }
      else {
        throw new FormatException("Pixel data not found.");
      }
    }

    in.seek(0);

    // look for the first BinData element

    found = false;
    buf = new byte[8192];
    in.read(buf, 0, 14);
    while (!found) {
      if (in.getFilePointer() < in.length()) {
        int numRead = in.read(buf, 14, 8192-14);

        String test = new String(buf);

        int ndx = test.indexOf("<Bin");
        if (ndx == -1) throw new FormatException("Pixel data not found");
        while (!((ndx != -1) && (ndx != test.indexOf("<Bin:External")) &&
          (ndx != test.indexOf("<Bin:BinaryFile"))))
        {
          ndx = test.indexOf("<Bin", ndx+1);
        }
        found = true;
        numRead += 14;
        offsets.add(new Integer(
          (int) in.getFilePointer() - (numRead - ndx)));
      }
      else {
        throw new FormatException("Pixel data not found");
      }
    }

    in.seek(0);

    buf = new byte[((Integer) offsets.get(0)).intValue()];
    in.read(buf);
    String xml = new String(buf);
    xml += "</Pixels></Image></OME>";  // might lose some data this way

    // The metadata store we're working with.
    try {
      internalStore = new OMEXMLMetadataStore();
    }
    catch (UnsupportedMetadataStoreException e) {
      throw new FormatException("To use this feature, please install the " +
          "org.openmicroscopy.xml package, available from " +
          "http://www.openmicroscopy.org/");
    }
    
    internalStore.createRoot(xml);

    int sizeX = 0;
    int sizeY = 0;
    int sizeZ = 0;
    int sizeC = 0;
    int sizeT = 0;

    String type = internalStore.getPixelType(null);
    if (type.endsWith("16")) bpp = 2;
    else if (type.endsWith("32")) bpp = 4;
    else if (type.equals("float")) bpp = 8;

    sizeX = internalStore.getSizeX(null).intValue();
    sizeY = internalStore.getSizeY(null).intValue();
    sizeZ = internalStore.getSizeZ(null).intValue();
    sizeC = internalStore.getSizeC(null).intValue();
    sizeT = internalStore.getSizeT(null).intValue();

    numChannels = sizeC;

    // calculate the number of raw bytes of pixel data that we are expecting
    int expected = sizeX * sizeY * bpp;

    // find the compression type and adjust 'expected' accordingly
    buf = new byte[256];
    in.read(buf);
    String data = new String(buf);

    in.seek(((Integer) offsets.get(0)).intValue());

    int compressionStart = data.indexOf("Compression") + 13;
    int compressionEnd = data.indexOf("\"", compressionStart);
    if (compressionStart != -1 && compressionEnd != -1) {
      compression = data.substring(compressionStart, compressionEnd);
    }
    else compression = "none";

    expected /= 2;
    searchForData(expected, sizeZ * sizeT * sizeC);
    numImages = offsets.size();
    if (numImages < (sizeZ * sizeT * sizeC)) {
      // hope this doesn't happen too often
      in.seek(((Integer) offsets.get(0)).intValue());
      searchForData(0, sizeZ * sizeT * sizeC);
      numImages = offsets.size();
    }
  }


  // -- Helper methods --

  /** Searches for BinData elements, skipping 'safe' bytes in between. */
  private void searchForData(int safe, int numPlanes) throws IOException {
    int iteration = 0;
    boolean found = false;
    if (offsets.size() > 1) {
      Object zeroth = offsets.get(0);
      offsets.clear();
      offsets.add(zeroth);
    }

    in.skipBytes(1);
    while (((in.getFilePointer() + safe) < in.length()) &&
      (offsets.size() < numPlanes))
    {
      in.skipBytes(safe);

      // look for next BinData element
      found = false;
      byte[] buf = new byte[8192];
      while (!found) {
        if (in.getFilePointer() < in.length()) {
          int numRead = in.read(buf, 20, buf.length - 20);
          String test = new String(buf);

          // datasets with small planes could have multiple sets of pixel data
          // in this block
          int ndx = test.indexOf("<Bin");
          while (ndx != -1) {
            found = true;
            if (numRead == buf.length - 20) numRead = buf.length;
            offsets.add(new Integer(
              (int) in.getFilePointer() - (numRead - ndx)));
            ndx = test.indexOf("<Bin", ndx+1);
          }
        }
        else {
          found = true;
        }
      }

      iteration++;
    }
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new OMEXMLReader().testRead(args);
  }

}
