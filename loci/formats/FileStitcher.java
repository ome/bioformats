//
// FileStitcher.java
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

package loci.formats;

import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.*;
import loci.util.FilePattern;

/** Logic to stitch together files with similar names. */
public class FileStitcher extends FormatReader {

  // -- Fields --

  /** FormatReader used to read the files. */
  private FormatReader reader;

  /** The Z, C and T dimensions. */
  private int[] dimensions;

  /** Total number of images. */
  private int numImages;

  /** Image count for each file. */
  private int[] imageCounts;

  /** The matching files. */
  private String[] files;

  /** Next plane number to open. */
  private int number;

  // -- Constructor --

  /** Constructs a FileStitcher with the given reader. */
  public FileStitcher(FormatReader r) throws FormatException {
    super("any", "*");
    if (r == null) throw new FormatException("FormatReader cannot be null");
    reader = r;
  }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for this file format. */
  public boolean isThisType(byte[] block) {
    return reader.isThisType(block);
  }

  /**
   * Allows the client to specify whether or not to separate channels.
   * By default, channels are left unseparated; thus if we encounter an RGB
   * image plane, it will be left as RGB and not split into 3 separate planes.
   */
  public void setSeparated(boolean separate) {
    this.separated = separate;
    reader.setSeparated(separate);
  }

  /** Determines the number of images in the given file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numImages;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    return reader.isRGB(id);
  }

  /** Get the size of the X dimension. */
  public int getSizeX(String id) throws FormatException, IOException {
    return reader.getSizeX(id);
  }

  /** Get the size of the Y dimension. */
  public int getSizeY(String id) throws FormatException, IOException {
    return reader.getSizeY(id);
  }

  /** Get the size of the Z dimension. */
  public int getSizeZ(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return dimensions[0];
  }

  /** Get the size of the C dimension. */
  public int getSizeC(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return dimensions[1];
  }

  /** Get the size of the T dimension. */
  public int getSizeT(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return dimensions[2];
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException
  {
    return reader.isLittleEndian(id);
  }

  /**
   * Return a five-character string representing the dimension order
   * within the file.
   */
  public String getDimensionOrder(String id) throws FormatException, IOException
  {
    return reader.getDimensionOrder(id);
  }

  /** Obtains the specified image from the given file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    return reader.openImage(findFile(no), number);
  }

  /** Obtains the specified image from the given file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    return reader.openBytes(findFile(no), number);
  }

  /** Closes the currently open file. */
  public void close() throws FormatException, IOException {
    reader.close();
  }

  // -- Internal FormatReader API methods --

  /** Initializes the given file. */
  protected void initFile(String id) throws FormatException, IOException {
    currentId = id;
    numImages = 0;
    dimensions = new int[3];

    // get the matching files

    FilePattern fp = new FilePattern(new File(id));
    files = fp.getFiles();
    imageCounts = new int[files.length];

    // determine the total number of images and build a list of dimensions
    // for each file

    int[][] dims = new int[files.length][3];

    for (int i=0; i<files.length; i++) {
      imageCounts[i] = reader.getImageCount(files[i]);
      numImages += imageCounts[i];
      dims[i][0] = reader.getSizeZ(files[i]);
      dims[i][1] = reader.getSizeC(files[i]);
      dims[i][2] = reader.getSizeT(files[i]);
    }

    // determine how many varying dimensions there are

    boolean varyZ = false;
    boolean varyC = false;
    boolean varyT = false;

    for (int i=1; i<dims.length; i++) {
      varyZ = dims[i][0] != dims[i-1][0];
      varyC = dims[i][1] != dims[i-1][1];
      varyT = dims[i][2] != dims[i-1][2];
    }

    if (!varyZ && !varyC && !varyT) {
      dimensions[0] = dims[0][0];
      dimensions[1] = dims[0][1];
      dimensions[2] = dims[0][2];

      String o = getDimensionOrder(id);
      if (o.endsWith("Z")) dimensions[0] *= files.length;
      else if (o.endsWith("C")) dimensions[1] *= files.length;
      else dimensions[2] *= files.length;
    }
    else {
      for (int j=0; j<3; j++) {
        int max = 0;
        int maxIndex = 0;
        for (int i=0; i<dims.length; i++) {
          if (dims[i][j] > max) {
            max = dims[i][j];
            maxIndex = i;
          }
        }

        dimensions[j] = dims[maxIndex][j];
      }
    }
  }

  // -- Helper methods --

  private String findFile(int no) {
    boolean found = false;
    String file = files[0];
    int ndx = 1; // index into the array of file names
    while (!found) {
      if (no < imageCounts[ndx - 1]) {
        found = true;
      }
      else {
        no -= imageCounts[ndx - 1];
        file = files[ndx];
        ndx++;
      }
    }
    number = no;
    return file;
  }

}
