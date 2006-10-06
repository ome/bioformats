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
import java.util.Arrays;
import java.util.Vector;

/** Logic to stitch together files with similar names. */
public class FileStitcher extends ReaderWrapper {

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

  /** Initialized series. */
  private int validSeries = -1;

  /** Index into the array of readers. */
  private int index = 0;

  private boolean varyZ, varyC, varyT;
  
  private IFormatReader[] readers;
  private boolean multipleReaders = true;

  // -- Constructors --

  /** Constructs a FileStitcher around a new image reader. */
  public FileStitcher() { super(); }

  /** Constructs a FileStitcher with the given reader. */
  public FileStitcher(IFormatReader r) { super(r); }

  /** 
   * Constructs a FileStitcher with the given reader; 
   * if the flag is set to true, a separate reader is used for each file.
   */
  public FileStitcher(IFormatReader r, boolean flag) {
    super(r);
    multipleReaders = flag;
  }

  // -- IFormatReader API methods --

  /** Determines the number of images in the given file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (getSeries(id) != validSeries) initFile(id);
    return numImages;
  }

  /** Get the size of the X dimension. */
  public int getSizeX(String id) throws FormatException, IOException {
    if (getSeries(id) != validSeries) initFile(id);
    return dimensions[0];
  }

  /** Get the size of the Y dimension. */
  public int getSizeY(String id) throws FormatException, IOException {
    if (getSeries(id) != validSeries) initFile(id);
    return dimensions[1];
  }

  /** Get the size of the Z dimension. */
  public int getSizeZ(String id) throws FormatException, IOException {
    if (getSeries(id) != validSeries) initFile(id);
    return dimensions[2];
  }

  /** Get the size of the C dimension. */
  public int getSizeC(String id) throws FormatException, IOException {
    if (getSeries(id) != validSeries) initFile(id);
    return dimensions[3];
  }

  /** Get the size of the T dimension. */
  public int getSizeT(String id) throws FormatException, IOException {
    if (getSeries(id) != validSeries) initFile(id);
    return dimensions[4];
  }

  /**
   * Return a five-character string representing the dimension order
   * within the file.
   */
  public String getDimensionOrder(String id)
    throws FormatException, IOException
  {
    if (getSeries(id) != validSeries) initFile(id);
    return order;
  }

  /** Obtains the specified image from the given file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (getSeries(id) != validSeries) initFile(id);
    return readers[index].openImage(findFile(no), number);
  }

  /** Obtains the specified image from the given file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (getSeries(id) != validSeries) initFile(id);
    return readers[index].openBytes(findFile(no), number);
  }

  public int getIndex(String id, int z, int c, int t)
    throws FormatException, IOException
  {
    return FormatReader.getIndex(this, id, z, c, t);
  }

  public int[] getZCTCoords(String id, int index)
    throws FormatException, IOException
  {
    return FormatReader.getZCTCoords(this, id, index);
  }

  public boolean testRead(String[] args) throws FormatException, IOException {
    return FormatReader.testRead(this, args);
  }

  // -- Helper methods --

  /** Initializes the given file. */
  protected void initFile(String id) throws FormatException, IOException {
    numImages = 0;
    dimensions = new int[5];
    validSeries = getSeries(id);

    // get the matching files

    fp = new FilePattern(new File(id));
    files = fp.getFiles();

    if (files == null) {
      throw new FormatException("Invalid file pattern.  Please rename " +
        "your files or disable file stitching.");
    }

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
    setDimensions(id, dims);

    readers = new IFormatReader[files.length];
    if (multipleReaders) {
      readers[0] = reader;
      IFormatReader r = reader;
      while (r instanceof ReaderWrapper) r = ((ReaderWrapper) r).reader;
      ImageReader ir = null;
      if (r instanceof ImageReader) ir = (ImageReader) r;
      for (int i=1; i<readers.length; i++) {
        readers[i] = ir == null ? r : ir.getReader(files[i]);
        // now we know the type of reader to use, but want a separate
        // object for each file, so create a new one of that type
        try {
          readers[i] = (IFormatReader) readers[i].getClass().newInstance();
        }
        catch (InstantiationException exc) { exc.printStackTrace(); }
        catch (IllegalAccessException exc) { exc.printStackTrace(); }
      }
    }
    else Arrays.fill(readers, reader);

    MetadataStore s = reader.getMetadataStore(id);
    s.setPixels(new Integer(dimensions[0]), new Integer(dimensions[1]),
      new Integer(dimensions[2]), new Integer(dimensions[3]),
      new Integer(dimensions[4]), null, null, null, null);
    setMetadataStore(s);
  }

  /**
   * Set the X, Y, Z, C, and T dimensions; uses some special heuristics on the
   * filename patterns to determine how Z, C and T should be set.
   *
   * @param dims - the dimensions of each file in the dataset
   */
  private void setDimensions(String id, int[][] dims)
    throws FormatException, IOException
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

    int zSize = 1;
    int cSize = 1;
    int tSize = 1;

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
          zSize = counts[j];
        }
        else if (max == cpos) {
          if (cSize == 1 && !isRGB(id)) {
            ordering += "C";
            cSize = counts[j];
          }
          else {
            if (zSize == 1) {
              ordering += "Z";
              zSize = counts[j];
            }
            else if (tSize == 1) {
              ordering += "T";
              tSize = counts[j];
            }
            else cSize *= counts[j];
          }
        }
        else {
          ordering += "T";
          tSize = counts[j];
        }
      }
      else {
        // our simple check failed, so let's try some more complex stuff

        // if the count is 2 or 3, it's probably a C size
        if (counts[j] == 2 || counts[j] == 3) {
          if (!varyZ && !varyC && !varyT) {
            if (counts[j] != 1) {
              // we already set this dimension
              if (zSize == 1) {
                zSize = cSize;
                ordering += "Z";
              }
              else if (tSize == 1) {
                tSize = cSize;
                ordering += "T";
              }
            }

            if (ordering.indexOf("C") == -1) ordering += "C";
            cSize = counts[j];
          }
        }
        else {
          // the most likely choice is whichever dimension is currently set to 1

          if (dimensions[2] == 1) {
            ordering += "Z";
            zSize = counts[j];
          }
          else if (dimensions[4] == 1) {
            ordering += "T";
            tSize = counts[j];
          }
        }
      }
    }

    // reset the dimensions, preserving internal sizes

    dimensions[3] *= cSize;

    if (zSize > 1 && dimensions[2] > 1) {
      if (dimensions[4] == 1) {
        dimensions[4] = dimensions[2];
        dimensions[2] = zSize;
      }
      else dimensions[2] *= zSize;
    }
    else dimensions[2] *= zSize;

    if (tSize > 1 && dimensions[4] > 1) {
      if (dimensions[2] == 1) {
        dimensions[2] = dimensions[4];
        dimensions[4] = tSize;
      }
      else dimensions[4] *= tSize;
    }
    else dimensions[4] *= tSize;

    // make sure ordering is right
    String begin = "";
    String readerOrder = reader.getDimensionOrder(id);
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
        index = ndx;
        ndx++;
      }
    }
    number = no;
    return file;
  }

}
