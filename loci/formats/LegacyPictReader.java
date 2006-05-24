//
// LegacyPictReader.java
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

/**
 * LegacyPictReader is the old file format reader for Apple PICT files.
 * To use it, QuickTime for Java must be installed.
 */
public class LegacyPictReader extends FormatReader {

  // -- Static fields --

  /** Helper reader for reading PICT data with QTJava library. */
  private static LegacyQTReader qtReader = new LegacyQTReader();


  // -- Constructor --

  /** Constructs a new PICT reader. */
  public LegacyPictReader() { super("PICT", "pict"); }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a PICT file. */
  public boolean isThisType(byte[] block) {
    return false;
  }

  /** Determines the number of images in the given PICT file. */
  public int getImageCount(String id) throws FormatException, IOException {
    return (!isRGB(id) || !separated) ? 1 : 3;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    return true;
  }         
  
  /** Obtains the specified image from the given file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    BufferedImage img = openImage(id, no);      
    if (separated) {
      return ImageTools.getBytes(img)[0];  
    }
    else {
      byte[][] p = ImageTools.getBytes(img);
      byte[] rtn = new byte[p.length * p[0].length];
      for (int i=0; i<p.length; i++) {
        System.arraycopy(p[i], 0, rtn, i*p[0].length, p[i].length);
      }
      return rtn;
    }        
  }

  /** Obtains the specified image from the given PICT file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }
      
    // read in PICT data
    File file = new File(id);
    int len = (int) (file.length() - 512);
    byte[] bytes = new byte[len];
    FileInputStream fin = new FileInputStream(file);
    fin.skip(512);  // skip 512 byte PICT header
    int read = 0;
    int left = len;
    while (left > 0) {
      int r = fin.read(bytes, read, left);
      read += r;
      left -= r;
    }
    fin.close();
    
    
    if (!separated) {
      return ImageTools.makeBuffered(qtReader.pictToImage(bytes));
    }
    else {
      return ImageTools.splitChannels(ImageTools.makeBuffered(
        qtReader.pictToImage(bytes)))[no];
    }        
  }  

  /** Closes any open files. */
  public void close() throws FormatException, IOException { }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new LegacyPictReader().testRead(args);
  }

}
