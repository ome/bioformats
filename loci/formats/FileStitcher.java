//
// FileStitcher.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
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
import java.util.Hashtable;
import java.util.Vector;

/** Logic to stitch together files with similar names. */
public class FileStitcher extends FormatReader {

  // -- Constants --

  /** Prefix endings indicating time dimension. */
  private static final String[] T = {
    "T", "TP", "TL", "Tl", "t", "tp", "Tp", "tP", "tL", "tl"
  };

  /** Prefix endings indicating space dimension. */
  private static final String[] Z = {
    "Z", "ZS", "FP", "SEC", "Focal", "z", "zs", "Zs", "zS", "fp", "Fp", "fP",
    "BF"
  };

  /** Prefix endings indicating channel dimension. */
  private static final String[] C = {"C", "CH", "W", "Ch", "ch"};

  // -- Fields --

  /** FormatReader used to read the files. */
  private IFormatReader reader;

  /** FilePattern used to build the list of files. */
  private FilePattern fp;

  /** The X, Y, Z, C and T dimensions. */
  private int[] dimensions;

  /** Total number of images. */
  private int numImages;

  /** Image count for each file. */
  private int[] imageCounts;

  /** The matching files. */
  private String[] files;

  /** Dimension order. */
  private String order;

  /** Next plane number to open. */
  private int number;

  private boolean varyZ, varyC, varyT;

  // -- Constructor --

  /** Constructs a FileStitcher with the given reader. */
  public FileStitcher(IFormatReader r) throws FormatException {
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

  /**
   * Obtains the hashtable containing the metadata field/value pairs from
   * the given file.
   */
  public Hashtable getMetadata(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return reader.getMetadata(id);
  }
  
  /**
   * Obtains the specified metadata field's value for the given file.
   *
   * @param field the name associated with the metadata field
   * @return the value, or null if the field doesn't exit
   */
  public Object getMetadataValue(String id, String field)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return reader.getMetadataValue(id, field);
  }

  /**
   * Retrieves the current metadata store for this reader. You can be
   * assured that this method will <b>never</b> return a <code>null</code>
   * metadata store.
   * @return a metadata store implementation.
   */
  public MetadataStore getMetadataStore(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    MetadataStore store = reader.getMetadataStore(id);
    store.setPixels(new Integer(getSizeX(id)), new Integer(getSizeY(id)),
      new Integer(getSizeZ(id)), new Integer(getSizeC(id)),
      new Integer(getSizeT(id)), null, null, null, null);
    return store;
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
    if (!id.equals(currentId)) initFile(id);
    return dimensions[0];
  }

  /** Get the size of the Y dimension. */
  public int getSizeY(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return dimensions[1];
  }

  /** Get the size of the Z dimension. */
  public int getSizeZ(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return dimensions[2];
  }

  /** Get the size of the C dimension. */
  public int getSizeC(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return dimensions[3];
  }

  /** Get the size of the T dimension. */
  public int getSizeT(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return dimensions[4];
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
    if (!id.equals(currentId)) initFile(id);
    return order;
  }

  /** Obtains the specified image from the given file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return reader.openImage(findFile(isSeparated() ? no / getSizeC(id) : no),
      number);
  }

  /** Obtains the specified image from the given file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return reader.openBytes(findFile(isSeparated() ? no / getSizeC(id) : no),
      number);
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
    dimensions = new int[5];

    // get the matching files

    fp = new FilePattern(new File(id));
    files = fp.getFiles();
    imageCounts = new int[files.length];

    // determine the total number of images and build a list of dimensions
    // for each file

    int[][] dims = new int[files.length][5];

    for (int i=0; i<files.length; i++) {
      imageCounts[i] = reader.getImageCount(files[i]);
      numImages += imageCounts[i];
      dims[i][0] = reader.getSizeX(files[i]);
      dims[i][1] = reader.getSizeY(files[i]);
      dims[i][2] = reader.getSizeZ(files[i]);
      dims[i][3] = reader.getSizeC(files[i]);
      dims[i][4] = reader.getSizeT(files[i]);
    }

    // determine how many varying dimensions there are

    for (int i=1; i<dims.length; i++) {
      varyZ = dims[i][2] != dims[i-1][2];
      varyC = dims[i][3] != dims[i-1][3];
      varyT = dims[i][4] != dims[i-1][4];
    }

    // always set the width and height to the maximum values
   
    for (int i=0; i<dims.length; i++) {
      if (dims[i][0] > dimensions[0]) {
        dimensions[0] = dims[i][0];
      }
      if (dims[i][1] > dimensions[1]) {
        dimensions[1] = dims[i][1];
      }
    }

    if (varyZ || varyC || varyT) {
      int max = 0;
      int maxIndex = 0;
      for (int i=0; i<dims.length; i++) {
        if (dims[i][2] > max) {
          max = dims[i][2];
          maxIndex = i;
        }
      }

      dimensions[2] = dims[maxIndex][2];
    }
    else {
      dimensions[2] = dims[0][2];
      dimensions[3] = dims[0][3];
      dimensions[4] = dims[0][4];
    }
    setDimensions(dims);

    MetadataStore store = reader.getMetadataStore(id);
    store.setPixels(new Integer(getSizeX(id)), new Integer(getSizeY(id)),
      new Integer(getSizeZ(id)), new Integer(getSizeC(id)),
      new Integer(getSizeT(id)), null, null, null, null);
    setMetadataStore(store);
  }

  // -- Helper methods --

  /**
   * Set the X, Y, Z, C, and T dimensions; uses some special heuristics on the
   * filename patterns to determine how Z, C and T should be set.
   *
   * @param dims - the dimensions of each file in the dataset
   */
  private void setDimensions(int[][] dims) throws FormatException, IOException
  {
    // first set X and Y
    // this is relatively easy - we can just take the maximum value

    int maxX = 0;
    int maxY = 0;

    for (int i=0; i<dims.length; i++) {
      if (dims[i][0] > maxX) {
        maxX = dims[i][0];
      }
      if (dims[i][1] > maxY) {
        maxY = dims[i][1];
      }
    }

    for (int i=0; i<dimensions.length; i++) {
      if (dimensions[i] == 0) dimensions[i]++;
    }

    // now the tricky part - setting Z, C and T

    // first we'll get a list of the prefix blocks

    Vector prefixes = new Vector();
    int i = 0;
    String prefix = fp.getPrefix(i);
    while (prefix != null) {
      prefixes.add(prefix);
      i++;
      prefix = fp.getPrefix(i);
    }

    int[] counts = fp.getCount();

    int sizeZ = 1;
    int sizeC = 1;
    int sizeT = 1;

    String ordering = "";

    for (int j=0; j<counts.length; j++) {
      // which dimension is this?

      int zndx = -1;
      int cndx = -1;
      int tndx = -1;

      String p = (String) prefixes.get(j);
      for (int k=0; k<Z.length; k++) {
        if (p.indexOf(Z[k]) != -1) {
          zndx = k;
        }
      }

      if (counts[j] <= 4) {
        for (int k=0; k<C.length; k++) {
          if (p.indexOf(C[k]) != -1) {
            cndx = k;
          }
        }
      }

      for (int k=0; k<T.length; k++) {
        if (p.indexOf(T[k]) != -1) {
          tndx = k;
        }
      }

      if (zndx != -1 || cndx != -1 || tndx != -1) {
        // the largest of these three is the dimension we will choose
        int zpos = zndx == -1 ? -1 : p.indexOf(Z[zndx]);
        int cpos = cndx == -1 ? -1 : p.indexOf(C[cndx]);
        int tpos = tndx == -1 ? -1 : p.indexOf(T[tndx]);

        int max = zpos;
        if (cpos > max) max = cpos;
        if (tpos > max) max = tpos;

        if (max == zpos) {
          ordering += "Z";
          sizeZ = counts[j];
        }
        else if (max == cpos) {
          if (sizeC == 1 && !isRGB(currentId)) {
            ordering += "C";
            sizeC = counts[j];
          }
          else {
            if (sizeZ == 1) {
              ordering += "Z";
              sizeZ = counts[j];
            }
            else if (sizeT == 1) {
              ordering += "T";
              sizeT = counts[j];
            }
            else sizeC *= counts[j];
          }
        }
        else {
          ordering += "T";
          sizeT = counts[j];
        }
      }
      else {
        // our simple check failed, so let's try some more complex stuff

        // if the count is 2 or 3, it's probably a C size
        if (counts[j] == 2 || counts[j] == 3) {
          if (!varyZ && !varyC && !varyT) {
            if (counts[j] != 1) {
              // we already set this dimension
              if (sizeZ == 1) {
                sizeZ = sizeC;
                ordering += "Z";
              }
              else if (sizeT == 1) {
                sizeT = sizeC;
                ordering += "T";
              }
            }

            if (ordering.indexOf("C") == -1) ordering += "C";
            sizeC = counts[j];
          }
        }
        else {
          // the most likely choice is whichever dimension is currently set to 1

          if (dimensions[2] == 1) {
            ordering += "Z";
            sizeZ = counts[j];
          }
          else if (dimensions[4] == 1) {
            ordering += "T";
            sizeT = counts[j];
          }
        }
      }
    }

    // reset the dimensions, preserving internal sizes

    dimensions[3] *= sizeC;

    if (sizeZ > 1 && dimensions[2] > 1) {
      if (dimensions[4] == 1) {
        dimensions[4] = dimensions[2];
        dimensions[2] = sizeZ;
      }
      else dimensions[2] *= sizeZ;
    }
    else dimensions[2] *= sizeZ;

    if (sizeT > 1 && dimensions[4] > 1) {
      if (dimensions[2] == 1) {
        dimensions[2] = dimensions[4];
        dimensions[4] = sizeT;
      }
      else dimensions[4] *= sizeT;
    }
    else dimensions[4] *= sizeT;

    // make sure ordering is right
    String begin = "";
    String readerOrder = reader.getDimensionOrder(currentId);
    for (int j=0; j<readerOrder.length(); j++) {
      if (ordering.indexOf(readerOrder.substring(j, j+1)) == -1) {
        begin += readerOrder.substring(j, j+1);
      }
    }

    ordering = begin + ordering;
    order = ordering.substring(0, 5);
  }

  /** Determine which file to open, if we want the specified image. */
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
