//
// LeicaReader.java
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

package loci.formats.in;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.*;
import java.util.*;
import loci.formats.*;

/**
 * LeicaReader is the file format reader for Leica files.
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class LeicaReader extends BaseTiffReader {

  // -- Constants -

  /** All Leica TIFFs have this tag. */
  private static final int LEICA_MAGIC_TAG = 33923;

  // -- Fields --

  /** Flag indicating whether current file is little endian. */
  protected boolean littleEndian;

  /** Array of IFD-like structures containing metadata. */
  protected Hashtable[] headerIFDs;

  /** Helper readers. */
  protected TiffReader[][] tiff;

  /** Number of channels in the current series. */
  protected int[] numChannels;

  /** Array of image file names. */
  protected Vector[] files;

  /** Number of series in the file. */
  private int numSeries;

  /** Image widths. */
  private int[] widths;

  /** Image heights. */
  private int[] heights;

  /** Number of Z slices. */
  private int[] zs;

  /** Total number of planes in each series. */
  private int[] numPlanes;

  /** Number of significant bits per pixel. */
  private int[][] validBits;

  // -- Constructor --

  /** Constructs a new Leica reader. */
  public LeicaReader() {
    super("Leica", new String[] {"lei", "tif", "tiff"});
  }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a Leica file. */
  public boolean isThisType(byte[] block) {
    if (block.length < 4) return false;

    if (block.length < 8) {
      // we can only check whether it is a TIFF
      return (block[0] == 0x49 && block[1] == 0x49 && block[2] == 0x49 &&
        block[3] == 0x49) || (block[0] == 0x4d && block[1] == 0x4d &&
        block[2] == 0x4d && block[3] == 0x4d);
    }

    int ifdlocation = DataTools.bytesToInt(block, 4, true);
    if (ifdlocation < 0 || ifdlocation + 1 > block.length) {
      return false;
    }
    else {
      int ifdnumber = DataTools.bytesToInt(block, ifdlocation, 2, true);
      for (int i=0; i<ifdnumber; i++) {
        if (ifdlocation + 3 + (i*12) > block.length) return false;
        else {
          int ifdtag = DataTools.bytesToInt(block,
            ifdlocation + 2 + (i*12), 2, true);
          if (ifdtag == LEICA_MAGIC_TAG) return true;
        }
      }
      return false;
    }
  }

  /** Determines the number of images in the given Leica file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId) && !usedFile(id)) initFile(id);
    return numPlanes[series];
  }

  /** Return the number of series in the given Leica file. */
  public int getSeriesCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId) && !usedFile(id)) initFile(id);
    return numSeries;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId) && !usedFile(id)) initFile(id);
    tiff[series][0].setColorTableIgnored(isColorTableIgnored());
    return tiff[series][0].isRGB((String) files[series].get(0));
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    if (!id.equals(currentId) && !usedFile(id)) initFile(id);
    return littleEndian;
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return true;
  }

  /** Obtains the specified image from the given Leica file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId) && !usedFile(id)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }
    tiff[series][no].setColorTableIgnored(ignoreColorTable);
    byte[] b = tiff[series][no].openBytes((String) files[series].get(no), 0);
    tiff[series][no].close();
    return b;
  }

  /** Obtains the specified image from the given Leica file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId) && !usedFile(id)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    tiff[series][no].setColorTableIgnored(ignoreColorTable);
    BufferedImage b =
      tiff[series][no].openImage((String) files[series].get(no), 0);
    ColorModel cm = ImageTools.makeColorModel(b.getRaster().getNumBands(),
      b.getRaster().getTransferType(), validBits[series]);
    b = ImageTools.makeBuffered(b, cm);
    tiff[series][no].close();
    return b;
  }

  /* @see IFormatReader#getUsedFiles(String) */
  public String[] getUsedFiles(String id) throws FormatException, IOException {
    if (!id.equals(currentId) && !usedFile(id)) initFile(id);
    Vector v = new Vector();
    v.add(id);
    for (int i=0; i<files.length; i++) {
      for (int j=0; j<files[i].size(); j++) {
        v.add(files[i].get(j));
      }
    }
    return (String[]) v.toArray(new String[0]);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
    if (tiff != null) {
      for (int i=0; i<tiff.length; i++) {
        if (tiff[i] != null) {
          for (int j=0; j<tiff[i].length; j++) {
            if (tiff[i][j] != null) tiff[i][j].close();
          }
        }
      }
    }
  }

  /** Initializes the given Leica file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("initFile(" + id + ")");
    String idLow = id.toLowerCase();
    close();
    if (idLow.endsWith("tif") || idLow.endsWith("tiff")) {
      if (ifds == null) super.initFile(id);

      in = new RandomAccessStream(id);

      if (in.readShort() == 0x4949) {
        in.order(true);
      }

      in.seek(0);

      // open the TIFF file and look for the "Image Description" field

      ifds = TiffTools.getIFDs(in);
      try {
        super.initMetadata();
      }
      catch (NullPointerException n) { }

      if (ifds == null) throw new FormatException("No IFDs found");

      String descr = (String) metadata.get("Comment");
      metadata.remove("Comment");

      int ndx = descr.indexOf("Series Name");

      // should be more graceful about this
      if (ndx == -1) throw new FormatException("LEI file not found");

      String lei = descr.substring(descr.indexOf("=", ndx) + 1);
      int newLineNdx = lei.indexOf("\n");
      lei = lei.substring(0, newLineNdx == -1 ? lei.length() : newLineNdx);
      lei = lei.trim();

      String dir = id.substring(0, id.lastIndexOf("/") + 1);
      lei = dir + lei;

      // parse key/value pairs in ImageDescription

      // first thing is to remove anything of the form "[blah]"

      String first;
      String last;

      while(descr.indexOf("[") != -1) {
        first = descr.substring(0, descr.indexOf("["));
        last = descr.substring(descr.indexOf("\n", descr.indexOf("[")));
        descr = first + last;
      }

      // each remaining line in descr is a (key, value) pair,
      // where '=' separates the key from the value

      String key;
      String value;
      int eqIndex = descr.indexOf("=");

      while(eqIndex != -1) {
        key = descr.substring(0, eqIndex);
        newLineNdx = descr.indexOf("\n", eqIndex);
        if (newLineNdx == -1) newLineNdx = descr.length();
        value = descr.substring(eqIndex+1, newLineNdx);
        metadata.put(key.trim(), value.trim());
        newLineNdx = descr.indexOf("\n", eqIndex);
        if (newLineNdx == -1) newLineNdx = descr.length();
        descr = descr.substring(newLineNdx);
        eqIndex = descr.indexOf("=");
      }

      // now open the LEI file

      Location l = new Location(lei);
      if (l.getAbsoluteFile().exists()) initFile(lei);
      else {
        l = l.getAbsoluteFile().getParentFile();
        String[] list = l.list();
        for (int i=0; i<list.length; i++) {
          if (list[i].toLowerCase().endsWith("lei")) {
            initFile(list[i]);
            return;
          }
        }
      }
    }
    else {
      // parse the LEI file

      if (metadata == null) {
        currentId = id;
        metadata = new Hashtable();
      }
      else {
        if (currentId != id) currentId = id;
      }

      in = new RandomAccessStream(id);

      byte[] fourBytes = new byte[4];
      in.read(fourBytes);
      littleEndian = (fourBytes[0] == TiffTools.LITTLE &&
        fourBytes[1] == TiffTools.LITTLE &&
        fourBytes[2] == TiffTools.LITTLE &&
        fourBytes[3] == TiffTools.LITTLE);

      in.order(littleEndian);

      in.skipBytes(8);
      int addr = in.readInt();
      Vector v = new Vector();
      while (addr != 0) {
        numSeries++;
        Hashtable ifd = new Hashtable();
        v.add(ifd);
        in.seek(addr + 4);

        int tag = in.readInt();

        while (tag != 0) {
          // create the IFD structure
          int offset = in.readInt();

          long pos = in.getFilePointer();
          in.seek(offset + 12);

          int size = in.readInt();
          byte[] data = new byte[size];
          in.read(data);
          ifd.put(new Integer(tag), (Object) data);
          in.seek(pos);
          tag = in.readInt();
        }

        addr = in.readInt();
      }

      if (v.size() < numSeries) numSeries = v.size();

      numChannels = new int[numSeries];
      widths = new int[numSeries];
      heights = new int[numSeries];
      zs = new int[numSeries];
      headerIFDs = new Hashtable[numSeries];
      files = new Vector[numSeries];
      numPlanes = new int[numSeries];

      v.copyInto(headerIFDs);

      // determine the length of a filename

      int nameLength = 0;

      int maxPlanes = 0;

      for (int i=0; i<headerIFDs.length; i++) {
        if (headerIFDs[i].get(new Integer(10)) != null) {
          byte[] temp = (byte[]) headerIFDs[i].get(new Integer(10));
          nameLength = DataTools.bytesToInt(temp, 8, 4, littleEndian);
        }

        Vector f = new Vector();
        byte[] tempData = (byte[]) headerIFDs[i].get(new Integer(15));
        int tempImages = DataTools.bytesToInt(tempData, 0, 4, littleEndian);
        String dirPrefix =
          new Location(id).getAbsoluteFile().getParent();
        dirPrefix = dirPrefix == null ? "" : (dirPrefix + File.separator);
        
        String[] listing = (new Location(dirPrefix)).list();
        Vector list = new Vector();

        for (int k=0; k<listing.length; k++) {
          if (listing[k].toLowerCase().endsWith(".tif") ||
            listing[k].toLowerCase().endsWith(".tiff"))
          {
            list.add(listing[k]);
          }
        }

        listing = (String[]) list.toArray(new String[0]);

        boolean tiffsExist = true;

        String prefix = "";
        for (int j=0; j<tempImages; j++) {
          // read in each filename
          prefix = DataTools.stripString(new String(tempData, 
            20 + 2*(j*nameLength), 2*nameLength));
          f.add(dirPrefix + prefix); 
          // test to make sure the path is valid
          Location test = new Location((String) f.get(f.size() - 1));
          if (tiffsExist) tiffsExist = test.exists();
        }

        // at least one of the TIFF files was renamed
        
        if (!tiffsExist) {
          // first thing is to get original LEI name associate with each TIFF
          // this lets us figure out which TIFFs we need for this dataset
          Hashtable leiMapping = new Hashtable();
          int numLeis = 0;
          for (int j=0; j<listing.length; j++) {
            RandomAccessStream ras = 
              new RandomAccessStream((String) listing[j]);
            Hashtable ifd = TiffTools.getFirstIFD(ras);
            ras.close();
            String descr = 
              (String) ifd.get(new Integer(TiffTools.IMAGE_DESCRIPTION));
            int ndx = descr.indexOf("=", descr.indexOf("Series Name"));
            String leiFile = descr.substring(ndx + 1, descr.indexOf("\n", ndx));
            leiFile = leiFile.trim();
            if (!leiMapping.contains(leiFile)) numLeis++;
            leiMapping.put(listing[j], leiFile);
          }
       
          // compare original TIFF prefix with original LEI prefix
        
          f.clear();
          String[] keys = (String[]) leiMapping.keySet().toArray(new String[0]);
          for (int j=0; j<keys.length; j++) {
            String lei = (String) leiMapping.get(keys[j]);
            if (DataTools.samePrefix(lei, prefix)) {
              f.add(keys[j]);
            }
          }
        
          // now that we have our list of files, all that remains is to figure
          // out how they should be ordered

          // we'll try looking for a naming convention, using FilePattern
          String[] usedFiles = null;
          for (int j=0; j<f.size(); j++) {
            if (usedFiles != null) {
              for (int k=0; k<usedFiles.length; k++) {
                if (usedFiles[k].equals((String) f.get(j)) || 
                  usedFile((String) f.get(j))) 
                {
                  k = 0;
                  j++;
                }
              }
            }
            if (j >= f.size()) break;
            
            FilePattern fp = new FilePattern(new Location((String) f.get(j)));
            if (fp != null) usedFiles = fp.getFiles();
            if (usedFiles != null && usedFiles.length == tempImages) {
              files[i] = new Vector();
              for (int k=0; k<usedFiles.length; k++) {
                files[i].add(usedFiles[k]);
              }
              break;
            }
          }

          // failing that, we can check the datestamp in each TIFF file
          // note that this is not guaranteed to work - some versions of
          // the Leica software will write a blank datestamp
          if (files[i] == null || files[i].size() == 0) {
            files[i] = new Vector();
            Hashtable h = new Hashtable();
            for (int j=0; j<listing.length; j++) {
              RandomAccessStream ras = new RandomAccessStream(listing[j]);
              Hashtable fd = TiffTools.getFirstIFD(ras);
              String stamp = 
                (String) TiffTools.getIFDValue(fd, TiffTools.DATE_TIME);
              if (h.size() == tempImages) {
                String[] ks = (String[]) h.keySet().toArray(new String[0]);
                Arrays.sort(ks);
                for (int k=0; k<ks.length; k++) {
                  files[i].add(h.get(ks[k]));
                }
                h.clear();
                break;
              }
              else {
                if (!h.contains(stamp)) h.put(stamp, listing[j]);
                else {
                  h.clear();
                  h.put(stamp, listing[j]);
                }
              }
            }
            if (h.size() == tempImages) {
              String[] ks = (String[]) h.keySet().toArray(new String[0]);
              Arrays.sort(ks);
              for (int k=0; k<ks.length; k++) {
                files[i].add(h.get(ks[k]));
                /* debug */ System.out.println("adding " + h.get(ks[k]) + 
                  " to series " + i);
              }
            }
          }

          // Our final effort is to just sort the filenames lexicographically.
          // This gives us a pretty good chance of getting the order right,
          // but it's not perfect.  Basically covers the (hopefully) unlikely
          // case where filenames are nonsensical, and datestamps are invalid.
          if (files[i] == null || files[i].size() == 0) {
            if (debug) debug("File ordering is not obvious.");
            files[i] = new Vector();
            Arrays.sort(listing);
            int ndx = 0;
            for (int j=0; j<i; j++) ndx += files[j].size();
            for (int j=ndx; j<ndx+tempImages; j++) files[i].add(listing[j]);
          }

          // Ways to break the renaming heuristics:
          //
          // 1) Don't use a detectable naming convention, and remove datestamps
          //    from TIFF files.
          // 2) Use a naming convention such as plane 0 -> "5.tif", 
          //    plane 1 -> "4.tif", plane 2 -> "3.tif", etc.
          // 3) Place two datasets in the same folder:
          //      a) swap the two LEI file names
          //      b) use the same naming convention for both sets of TIFF files
          //      c) use the same naming convention AND make sure the datestamps
          //         are the same between TIFF files
        }
        else files[i] = f;
        numPlanes[i] = files[i].size();
        if (numPlanes[i] > maxPlanes) maxPlanes = numPlanes[i];
      }

      tiff = new TiffReader[numSeries][maxPlanes];

      for (int i=0; i<tiff.length; i++) {
        for (int j=0; j<tiff[i].length; j++) {
          tiff[i][j] = new TiffReader();
        }
      }

      initMetadata();
    }
  }

  // -- FormatHandler API methods --

  /**
   * Checks if the given string is a valid filename for a Leica file.
   * @param open If true, the (existing) file is opened for further analysis,
   *   since the file extension is insufficient to confirm that the file is in
   *   Leica format.
   */
  public boolean isThisType(String name, boolean open) {
    String lname = name.toLowerCase();
    if (lname.endsWith(".lei")) return true;
    else if (!lname.endsWith(".tif") && !lname.endsWith(".tiff")) return false;
    if (!open) return true; // now allowed to be any more thorough

    // just checking the filename isn't enough to differentiate between
    // Leica and regular TIFF; open the file and check more thoroughly
    Location file = new Location(name);
    if (!file.exists()) return false;
    long len = file.length();
    if (len < 4) return false;

    try {
      RandomAccessStream ras = new RandomAccessStream(name);
      Hashtable ifd = TiffTools.getFirstIFD(ras);
      if (ifd == null) return false;

      String descr = (String) ifd.get(new Integer(TiffTools.IMAGE_DESCRIPTION));
      int ndx = descr.indexOf("Series Name");

      if (ndx == -1) return false;

      String dir = new Location(name).getAbsoluteFile().getParent(); 
      String[] listing = new Location(dir).list();
      for (int i=0; i<listing.length; i++) {
        if (listing[i].toLowerCase().endsWith(".lei")) return true;
      }
      return false;
    }
    catch (IOException exc) { }
    catch (NullPointerException exc) { }
    return false;
  }

  // -- Helper methods --

  /* @see loci.formats.BaseTiffReader#initMetadata() */
  protected void initMetadata() {
    if (headerIFDs == null) headerIFDs = ifds;

    for (int i=0; i<headerIFDs.length; i++) {
      byte[] temp = (byte[]) headerIFDs[i].get(new Integer(10));
      if (temp != null) {
        // the series data
        // ID_SERIES
        metadata.put("Version",
          new Integer(DataTools.bytesToInt(temp, 0, 4, littleEndian)));
        metadata.put("Number of Series",
          new Integer(DataTools.bytesToInt(temp, 4, 4, littleEndian)));
        metadata.put("Length of filename",
          new Integer(DataTools.bytesToInt(temp, 8, 4, littleEndian)));
        metadata.put("Length of file extension",
          new Integer(DataTools.bytesToInt(temp, 12, 4, littleEndian)));
        Integer fileExtLen = (Integer) metadata.get("Length of file extension");
        metadata.put("Image file extension",
          DataTools.stripString(new String(temp, 16, fileExtLen.intValue())));
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(15));
      if (temp != null) {
        // the image data
        // ID_IMAGES

        zs[i] = DataTools.bytesToInt(temp, 0, 4, littleEndian);
        widths[i] = DataTools.bytesToInt(temp, 4, 4, littleEndian);
        heights[i] = DataTools.bytesToInt(temp, 8, 4, littleEndian);

        metadata.put("Number of images", new Integer(zs[i]));
        metadata.put("Image width", new Integer(widths[i]));
        metadata.put("Image height", new Integer(heights[i]));
        metadata.put("Bits per Sample", new Integer(
          DataTools.bytesToInt(temp, 12, 4, littleEndian)));
        metadata.put("Samples per pixel", new Integer(
          DataTools.bytesToInt(temp, 16, 4, littleEndian)));
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(20));
      if (temp != null) {
        // dimension description
        // ID_DIMDESCR
        int pt = 0;
        metadata.put("Voxel Version", new Integer(
          DataTools.bytesToInt(temp, 0, 4, littleEndian)));
        int voxelType = DataTools.bytesToInt(temp, 4, 4, littleEndian);
        String type = "";
        switch (voxelType) {
          case 0:
            type = "undefined";
            break;
          case 10:
            type = "gray normal";
            break;
          case 20:
            type = "RGB";
            break;
        }

        metadata.put("VoxelType", type);

        metadata.put("Bytes per pixel", new Integer(
          DataTools.bytesToInt(temp, 8, 4, littleEndian)));
        metadata.put("Real world resolution", new Integer(
          DataTools.bytesToInt(temp, 12, 4, littleEndian)));
        int length = DataTools.bytesToInt(temp, 16, 4, littleEndian);
        metadata.put("Maximum voxel intensity",
          DataTools.stripString(new String(temp, 20, length)));
        pt = 20 + length;
        pt += 4;
        metadata.put("Minimum voxel intensity",
          DataTools.stripString(new String(temp, pt, length)));
        pt += length;
        length = DataTools.bytesToInt(temp, pt, 4, littleEndian);
        pt += 4 + length + 4;

        length = DataTools.bytesToInt(temp, pt, 4, littleEndian);
        for (int j=0; j<length; j++) {
          int dimId = DataTools.bytesToInt(temp, pt, 4, littleEndian);
          String dimType = "";
          switch (dimId) {
            case 0:
              dimType = "undefined";
              break;
            case 120:
              dimType = "x";
              break;
            case 121:
              dimType = "y";
              break;
            case 122:
              dimType = "z";
              break;
            case 116:
              dimType = "t";
              break;
            case 6815843:
              dimType = "channel";
              break;
            case 6357100:
              dimType = "wave length";
              break;
            case 7602290:
              dimType = "rotation";
              break;
            case 7798904:
              dimType = "x-wide for the motorized xy-stage";
              break;
            case 7798905:
              dimType = "y-wide for the motorized xy-stage";
              break;
            case 7798906:
              dimType = "z-wide for the z-stage-drive";
              break;
            case 4259957:
              dimType = "user1 - unspecified";
              break;
            case 4325493:
              dimType = "user2 - unspecified";
              break;
            case 4391029:
              dimType = "user3 - unspecified";
              break;
            case 6357095:
              dimType = "graylevel";
              break;
            case 6422631:
              dimType = "graylevel1";
              break;
            case 6488167:
              dimType = "graylevel2";
              break;
            case 6553703:
              dimType = "graylevel3";
              break;
            case 7864398:
              dimType = "logical x";
              break;
            case 7929934:
              dimType = "logical y";
              break;
            case 7995470:
              dimType = "logical z";
              break;
            case 7602254:
              dimType = "logical t";
              break;
            case 7077966:
              dimType = "logical lambda";
              break;
            case 7471182:
              dimType = "logical rotation";
              break;
            case 5767246:
              dimType = "logical x-wide";
              break;
            case 5832782:
              dimType = "logical y-wide";
              break;
            case 5898318:
              dimType = "logical z-wide";
              break;
          }

          //if (dimType.equals("channel")) numChannels++;
          metadata.put("Dim" + j + " type", dimType);
          pt += 4;
          metadata.put("Dim" + j + " size", new Integer(
            DataTools.bytesToInt(temp, pt, 4, littleEndian)));
          pt += 4;
          metadata.put("Dim" + j + " distance between sub-dimensions",
            new Integer(DataTools.bytesToInt(temp, pt, 4, littleEndian)));
          pt += 4;

          int len = DataTools.bytesToInt(temp, pt, 4, littleEndian);
          pt += 4;
          metadata.put("Dim" + j + " physical length",
            DataTools.stripString(new String(temp, pt, len)));
          pt += len;

          len = DataTools.bytesToInt(temp, pt, 4, littleEndian);
          pt += 4;
          metadata.put("Dim" + j + " physical origin",
            DataTools.stripString(new String(temp, pt, len)));
          pt += len;

          len = DataTools.bytesToInt(temp, pt, 4, littleEndian);
          pt += 4;
          metadata.put("Dim" + j + " name",
            DataTools.stripString(new String(temp, pt, len)));
          pt += len;

          len = DataTools.bytesToInt(temp, pt, 4, littleEndian);
          pt += 4;
          metadata.put("Dim" + j + " description",
            DataTools.stripString(new String(temp, pt, len)));
        }
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(30));
      if (temp != null) {
        // filter data
        // ID_FILTERSET

        // not currently used
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(40));
      if (temp != null) {
        // time data
        // ID_TIMEINFO
        try {
          metadata.put("Number of time-stamped dimensions",
            new Integer(DataTools.bytesToInt(temp, 0, 4, littleEndian)));
          int nDims = DataTools.bytesToInt(temp, 4, 4, littleEndian);
          metadata.put("Time-stamped dimension", new Integer(nDims));

          int pt = 8;

          for (int j=0; j < nDims; j++) {
            metadata.put("Dimension " + j + " ID",
              new Integer(DataTools.bytesToInt(temp, pt, 4, littleEndian)));
            pt += 4;
            metadata.put("Dimension " + j + " size",
              new Integer(DataTools.bytesToInt(temp, pt, 4, littleEndian)));
            pt += 4;
            metadata.put("Dimension " + j + " distance between dimensions",
              new Integer(DataTools.bytesToInt(temp, pt, 4, littleEndian)));
            pt += 4;
          }

          int numStamps = DataTools.bytesToInt(temp, pt, 4, littleEndian);
          pt += 4;
          metadata.put("Number of time-stamps", new Integer(numStamps));
          for (int j=0; j<numStamps; j++) {
            metadata.put("Timestamp " + j,
              DataTools.stripString(new String(temp, pt, 64)));
            pt += 64;
          }

          int numTMs = DataTools.bytesToInt(temp, pt, 4, littleEndian);
          pt += 4;
          metadata.put("Number of time-markers", new Integer(numTMs));
          for (int j=0; j<numTMs; j++) {
            int numDims = DataTools.bytesToInt(temp, pt, 4, littleEndian);
            pt += 4;

            for (int k=0; k<numDims; k++) {
              metadata.put("Time-marker " + j +
                " Dimension " + k + " coordinate",
                new Integer(DataTools.bytesToInt(temp, pt, 4, littleEndian)));
              pt += 4;
            }
            metadata.put("Time-marker " + j,
              DataTools.stripString(new String(temp, pt, 64)));
            pt += 64;
          }
        }
        catch (Throwable t) {
          // CTR TODO - eliminate catch-all exception handling
          if (debug) t.printStackTrace();
        }
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(50));
      if (temp != null) {
        // scanner data
        // ID_SCANNERSET

        // not currently used
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(60));
      if (temp != null) {
        // experiment data
        // ID_EXPERIMENT
        int pt = 8;
        int len = DataTools.bytesToInt(temp, pt, 4, littleEndian);
        pt += 4;

        metadata.put("Image Description",
          DataTools.stripString(new String(temp, pt, 2*len)));
        pt += 2*len;
        len = DataTools.bytesToInt(temp, pt, 4, littleEndian);
        pt += 4;

        metadata.put("Main file extension",
          DataTools.stripString(new String(temp, pt, 2*len)));
        pt += 2*len;

        len = DataTools.bytesToInt(temp, pt, 4, littleEndian);
        pt += 4;
        metadata.put("Single image format identifier",
          DataTools.stripString(new String(temp, pt, 2*len)));
        pt += 2*len;

        len = DataTools.bytesToInt(temp, pt, 4, littleEndian);
        pt += 4;
        metadata.put("Single image extension",
          DataTools.stripString(new String(temp, pt, 2*len)));
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(70));
      if (temp != null) {
        // LUT data
        // ID_LUTDESC
        int pt = 0;
        int nChannels = DataTools.bytesToInt(temp, pt, 4, littleEndian);
        pt += 4;
        metadata.put("Number of LUT channels", new Integer(nChannels));
        metadata.put("ID of colored dimension",
          new Integer(DataTools.bytesToInt(temp, pt, 4, littleEndian)));
        pt += 4;

        numChannels[i] = nChannels;

        for (int j=0; j<nChannels; j++) {
          metadata.put("LUT Channel " + j + " version",
            new Integer(DataTools.bytesToInt(temp, pt, 4, littleEndian)));
          pt += 4;

          int invert = DataTools.bytesToInt(temp, pt, 1, littleEndian);
          pt += 1;
          boolean inverted = invert == 1;
          metadata.put("LUT Channel " + j + " inverted?",
            new Boolean(inverted).toString());

          int length = DataTools.bytesToInt(temp, pt, 4, littleEndian);
          pt += 4;
          metadata.put("LUT Channel " + j + " description",
            DataTools.stripString(new String(temp, pt, length)));

          pt += length;
          length = DataTools.bytesToInt(temp, pt, 4, littleEndian);
          pt += 4;
          metadata.put("LUT Channel " + j + " filename",
            DataTools.stripString(new String(temp, pt, length)));
          pt += length;
          length = DataTools.bytesToInt(temp, pt, 4, littleEndian);
          pt += 4;

          String name = DataTools.stripString(new String(temp, pt, length));
          if (name.equals("Green") || name.equals("Red") || name.equals("Blue"))
          {
            numChannels[i] = 3;
          }
          metadata.put("LUT Channel " + j + " name", name);
          pt += length;

          pt += 8;
        }
      }
    }

    sizeX = widths;
    sizeY = heights;
    sizeZ = zs;
    sizeC = numChannels;
    sizeT = new int[numSeries];
    pixelType = new int[numSeries];
    currentOrder = new String[numSeries];
    orderCertain = new boolean[numSeries];
    Arrays.fill(orderCertain, true);

    try {
      int oldSeries = getSeries(currentId);
      for (int i=0; i<sizeC.length; i++) {
        setSeries(currentId, i);
        if (isRGB(currentId)) sizeC[i] = 3;
        else sizeC[i] = 1;
      }
      setSeries(currentId, oldSeries);
    }
    catch (Exception e) {
      // NullPointerException caught here if the file we opened was a TIFF.
      // However, the sizeC field will be adjusted anyway by a later call to
      // BaseTiffReader.initMetadata

      // CTR TODO - eliminate catch-all exception handling
      if (debug) e.printStackTrace();
    }

    Integer v = (Integer) metadata.get("Real world resolution");

    if (v != null) {
      validBits = new int[sizeC.length][];

      for (int i=0; i<validBits.length; i++) {
        validBits[i] = new int[sizeC[i]];
        for (int j=0; j<validBits[i].length; j++) {
          validBits[i][j] = v.intValue();
        }
      }
    }
    else validBits = null;

    // The metadata store we're working with.
    MetadataStore store = new DummyMetadataStore();
    try {
      store = getMetadataStore(currentId);
    }
    catch (Exception e) {
      // CTR TODO - eliminate catch-all exception handling
      if (debug) e.printStackTrace();
    }

    for (int i=0; i<numSeries; i++) {
      orderCertain[i] = true;

      if (sizeC[i] == 0) sizeC[i] = 1;
      sizeT[i] += 1;
      currentOrder[i] = "XYZTC";

      int tPixelType = ((Integer) metadata.get("Bytes per pixel")).intValue();
      switch (tPixelType) {
        case 1:
          pixelType[i] = FormatReader.UINT8;
          break;
        case 2:
          pixelType[i] = FormatReader.UINT16;
          break;
        case 3:
          pixelType[i] = FormatReader.UINT8;
          break;
        case 4:
          pixelType[i] = FormatReader.INT32;
          break;
        case 6:
          pixelType[i] = FormatReader.INT16;
          break;
        case 8:
          pixelType[i] = FormatReader.DOUBLE;
          break;
      }

      store.setPixels(
        new Integer(widths[i]),
        new Integer(heights[i]),
        new Integer(zs[i]),
        new Integer(numChannels[i] == 0 ? 1 : numChannels[i]), // SizeC
        new Integer(1), // SizeT
        new Integer(pixelType[i]), // PixelType
        new Boolean(!littleEndian), // BigEndian
        "XYZTC", // DimensionOrder
        new Integer(i));

      String timestamp = (String) metadata.get("Timestamp " + (i+1));
      String description = (String) metadata.get("Image Description");

      try {
        store.setImage(null, timestamp.substring(3),
          description, new Integer(i));
      }
      catch (NullPointerException n) { }
    }
  }

  private boolean usedFile(String s) {
    for (int i=0; i<files.length; i++) {
      if (files[i] == null) continue;
      if (files[i].contains(s)) return true;
    }
    return false;
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new LeicaReader().testRead(args);
  }

}
