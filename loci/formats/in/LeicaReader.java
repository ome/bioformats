//
// LeicaReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

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

import java.io.*;
import java.text.*;
import java.util.*;
import loci.formats.*;

/**
 * LeicaReader is the file format reader for Leica files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/LeicaReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/LeicaReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class LeicaReader extends FormatReader {

  // -- Constants -

  /** All Leica TIFFs have this tag. */
  private static final int LEICA_MAGIC_TAG = 33923;

  // -- Fields --

  protected Hashtable[] ifds;

  /** Array of IFD-like structures containing metadata. */
  protected Hashtable[] headerIFDs;

  /** Helper readers. */
  protected TiffReader[][] tiff;

  /** Array of image file names. */
  protected Vector[] files;

  /** Number of series in the file. */
  private int numSeries;

  /** Name of current LEI file */
  private String leiFilename;

  private int bpp;
  private Vector seriesNames;

  // -- Constructor --

  /** Constructs a new Leica reader. */
  public LeicaReader() {
    super("Leica", new String[] {"lei", "tif", "tiff"});
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
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

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    tiff[0][0].setId((String) files[0].get(0));
    return tiff[0][0].get8BitLookupTable();
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    tiff[0][0].setId((String) files[0].get(0));
    return tiff[0][0].get16BitLookupTable();
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return id.toLowerCase().endsWith(".lei") ? FormatTools.MUST_GROUP :
      FormatTools.CAN_GROUP;
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    tiff[series][no].setId((String) files[series].get(no));
    return tiff[series][no].openBytes(0, buf);
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    Vector v = new Vector();
    v.add(leiFilename);
    for (int i=0; i<files.length; i++) {
      for (int j=0; j<files[i].size(); j++) {
        v.add(files[i].get(j));
      }
    }
    return (String[]) v.toArray(new String[0]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    if (fileOnly) {
      if (in != null) in.close();
      if (tiff != null) {
        for (int i=0; i<tiff.length; i++) {
          if (tiff[i] != null) {
            for (int j=0; j<tiff[i].length; j++) {
              if (tiff[i][j] != null) tiff[i][j].close(fileOnly);
            }
          }
        }
      }
    }
    else close();
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    String lname = name.toLowerCase();
    if (lname.endsWith(".lei")) return true;
    else if (!lname.endsWith(".tif") && !lname.endsWith(".tiff")) return false;
    if (!open) return true; // not allowed to check the file contents
    if (!isGroupFiles()) return false;

    // just checking the filename isn't enough to differentiate between
    // Leica and regular TIFF; open the file and check more thoroughly
    Location file = new Location(name);
    if (!file.exists()) return false;
    long len = file.length();
    if (len < 4) return false;

    try {
      RandomAccessStream ras = new RandomAccessStream(name);
      Hashtable ifd = TiffTools.getFirstIFD(ras);
      ras.close();
      if (ifd == null) return false;

      String descr = (String) ifd.get(new Integer(TiffTools.IMAGE_DESCRIPTION));
      int ndx = descr == null ? -1 : descr.indexOf("Series Name");

      if (ndx == -1) return false;

      File f = new File(name).getAbsoluteFile();
      String[] listing = null;
      if (f.exists()) listing = f.getParentFile().list();
      else {
        listing =
          (String[]) Location.getIdMap().keySet().toArray(new String[0]);
      }

      for (int i=0; i<listing.length; i++) {
        if (listing[i].toLowerCase().endsWith(".lei")) return true;
      }
      return false;
    }
    catch (IOException exc) {
      if (debug) trace(exc);
    }
    catch (ClassCastException exc) {
      if (debug) trace(exc);
    }
    return false;
  }

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    leiFilename = null;
    files = null;
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

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("LeicaReader.initFile(" + id + ")");
    String idLow = id.toLowerCase();
    close();

    if (idLow.endsWith("tif") || idLow.endsWith("tiff")) {
      if (ifds == null) super.initFile(id);

      in = new RandomAccessStream(id);

      if (in.readShort() == 0x4949) {
        in.order(true);
      }

      in.seek(0);

      status("Finding companion file name");

      // open the TIFF file and look for the "Image Description" field

      ifds = TiffTools.getIFDs(in);
      if (ifds == null) throw new FormatException("No IFDs found");
      String descr = (String) TiffTools.getIFDValue(ifds[0],
        TiffTools.IMAGE_DESCRIPTION);

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

      while (descr.indexOf("[") != -1) {
        first = descr.substring(0, descr.indexOf("["));
        last = descr.substring(descr.indexOf("\n", descr.indexOf("[")));
        descr = first + last;
      }

      // each remaining line in descr is a (key, value) pair,
      // where '=' separates the key from the value

      String key;
      String value;
      int eqIndex = descr.indexOf("=");

      while (eqIndex != -1) {
        key = descr.substring(0, eqIndex);
        newLineNdx = descr.indexOf("\n", eqIndex);
        if (newLineNdx == -1) newLineNdx = descr.length();
        value = descr.substring(eqIndex+1, newLineNdx);
        addMeta(key.trim(), value.trim());
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

      super.initFile(id);

      leiFilename = id;
      in = new RandomAccessStream(id);

      seriesNames = new Vector();

      byte[] fourBytes = new byte[4];
      in.read(fourBytes);
      core.littleEndian[0] = (fourBytes[0] == TiffTools.LITTLE &&
        fourBytes[1] == TiffTools.LITTLE &&
        fourBytes[2] == TiffTools.LITTLE &&
        fourBytes[3] == TiffTools.LITTLE);

      in.order(core.littleEndian[0]);

      status("Reading metadata blocks");

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

      core = new CoreMetadata(numSeries);

      headerIFDs = new Hashtable[numSeries];
      files = new Vector[numSeries];

      v.copyInto(headerIFDs);

      // determine the length of a filename

      int nameLength = 0;

      int maxPlanes = 0;

      status("Parsing metadata blocks");

      core.littleEndian[0] = !core.littleEndian[0];

      for (int i=0; i<headerIFDs.length; i++) {
        if (headerIFDs[i].get(new Integer(10)) != null) {
          byte[] temp = (byte[]) headerIFDs[i].get(new Integer(10));
          nameLength = DataTools.bytesToInt(temp, 8, 4, core.littleEndian[0]);
        }

        Vector f = new Vector();
        byte[] tempData = (byte[]) headerIFDs[i].get(new Integer(15));
        int tempImages = DataTools.bytesToInt(tempData, 0, 4,
          core.littleEndian[0]);

        File dirFile = new File(id).getAbsoluteFile();
        String[] listing = null;
        String dirPrefix = "";
        if (dirFile.exists()) {
          listing = dirFile.getParentFile().list();
          dirPrefix = dirFile.getParent();
        }
        else {
          listing =
            (String[]) Location.getIdMap().keySet().toArray(new String[0]);
        }

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
          f.add(dirPrefix + File.separator + prefix);
          // test to make sure the path is valid
          Location test = new Location((String) f.get(f.size() - 1));
          if (tiffsExist) tiffsExist = test.exists();

          // get the series name from the stored file name
          int firstUnderscore = prefix.indexOf("_") + 1;
          int secondUnderscore = prefix.indexOf("_", firstUnderscore);
          String name = null;
          if (firstUnderscore < 0 || secondUnderscore < 0) name = prefix;
          else {
            String s = prefix.substring(firstUnderscore, secondUnderscore);
            if (seriesNames.contains(s)) {
              int suffix = 2;
              do {
                name = s + "-" + suffix;
                suffix++;
              }
              while (seriesNames.contains(name));
            }
            else name = s;
          }
          seriesNames.add(name);
        }

        // at least one of the TIFF files was renamed

        if (!tiffsExist) {
          status("Handling renamed TIFF files");

          // first thing is to get original LEI name associate with each TIFF
          // this lets us figure out which TIFFs we need for this dataset
          Hashtable leiMapping = new Hashtable();
          int numLeis = 0;
          for (int j=0; j<listing.length; j++) {
            RandomAccessStream ras = new RandomAccessStream(
              new Location(dirPrefix, listing[j]).getAbsolutePath());
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
                files[i].add(new Location(usedFiles[k]).getAbsolutePath());
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
              RandomAccessStream ras = new RandomAccessStream(
                new Location(dirPrefix, listing[j]).getAbsolutePath());
              Hashtable fd = TiffTools.getFirstIFD(ras);
              String stamp =
                (String) TiffTools.getIFDValue(fd, TiffTools.DATE_TIME);
              if (h.size() == tempImages) {
                String[] ks = (String[]) h.keySet().toArray(new String[0]);
                Arrays.sort(ks);
                for (int k=0; k<ks.length; k++) {
                  files[i].add(new Location(dirPrefix,
                    (String) h.get(ks[k])).getAbsolutePath());
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
              ras.close();
            }
            if (h.size() == tempImages) {
              String[] ks = (String[]) h.keySet().toArray(new String[0]);
              Arrays.sort(ks);
              for (int k=0; k<ks.length; k++) {
                files[i].add(new Location(dirPrefix,
                  (String) h.get(ks[k])).getAbsolutePath());
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
            for (int j=ndx; j<ndx+tempImages; j++) {
              files[i].add(new Location(dirPrefix,
                listing[j]).getAbsolutePath());
            }
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
        core.imageCount[i] = files[i].size();
        if (core.imageCount[i] > maxPlanes) maxPlanes = core.imageCount[i];
      }

      tiff = new TiffReader[numSeries][maxPlanes];

      for (int i=0; i<tiff.length; i++) {
        for (int j=0; j<tiff[i].length; j++) {
          tiff[i][j] = new TiffReader();
          if (j > 0) tiff[i][j].setMetadataCollected(false);
        }
      }

      status("Populating metadata");
      initMetadata();
    }
  }

  // -- Helper methods --

  protected void initMetadata() throws FormatException, IOException {
    if (headerIFDs == null) headerIFDs = ifds;

    for (int i=0; i<headerIFDs.length; i++) {
      byte[] temp = (byte[]) headerIFDs[i].get(new Integer(10));
      if (temp != null) {
        // the series data
        // ID_SERIES
        addMeta("Version",
          new Integer(DataTools.bytesToInt(temp, 0, 4, core.littleEndian[0])));
        addMeta("Number of Series",
          new Integer(DataTools.bytesToInt(temp, 4, 4, core.littleEndian[0])));
        addMeta("Length of filename",
          new Integer(DataTools.bytesToInt(temp, 8, 4, core.littleEndian[0])));
        Integer fileExtLen =
          new Integer(DataTools.bytesToInt(temp, 12, 4, core.littleEndian[0]));
        addMeta("Length of file extension", fileExtLen);
        addMeta("Image file extension",
          DataTools.stripString(new String(temp, 16, fileExtLen.intValue())));
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(15));
      if (temp != null) {
        // the image data
        // ID_IMAGES

        core.sizeZ[i] = DataTools.bytesToInt(temp, 0, 4, core.littleEndian[0]);
        core.sizeX[i] = DataTools.bytesToInt(temp, 4, 4, core.littleEndian[0]);
        core.sizeY[i] = DataTools.bytesToInt(temp, 8, 4, core.littleEndian[0]);

        addMeta("Number of images", new Integer(core.sizeZ[i]));
        addMeta("Image width", new Integer(core.sizeX[i]));
        addMeta("Image height", new Integer(core.sizeY[i]));
        addMeta("Bits per Sample",
          new Integer(DataTools.bytesToInt(temp, 12, 4, core.littleEndian[0])));
        addMeta("Samples per pixel",
          new Integer(DataTools.bytesToInt(temp, 16, 4, core.littleEndian[0])));
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(20));
      if (temp != null) {
        // dimension description
        // ID_DIMDESCR
        int pt = 0;
        addMeta("Voxel Version", new Integer(
          DataTools.bytesToInt(temp, 0, 4, core.littleEndian[0])));
        int voxelType = DataTools.bytesToInt(temp, 4, 4, core.littleEndian[0]);
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

        addMeta("VoxelType", type);

        bpp = DataTools.bytesToInt(temp, 8, 4, core.littleEndian[0]);
        addMeta("Bytes per pixel", new Integer(bpp));
        addMeta("Real world resolution",
          new Integer(DataTools.bytesToInt(temp, 12, 4, core.littleEndian[0])));
        int length = DataTools.bytesToInt(temp, 16, 4, core.littleEndian[0]);
        addMeta("Maximum voxel intensity",
          DataTools.stripString(new String(temp, 20, length)));
        pt = 20 + length;
        pt += 4;
        addMeta("Minimum voxel intensity",
          DataTools.stripString(new String(temp, pt, length)));
        pt += length;
        length = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
        pt += 4 + length + 4;

        length = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
        for (int j=0; j<length; j++) {
          int dimId = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
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
          addMeta("Dim" + j + " type", dimType);
          pt += 4;
          addMeta("Dim" + j + " size", new Integer(
            DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0])));
          pt += 4;
          int dist = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
          addMeta("Dim" + j + " distance between sub-dimensions",
            new Integer(dist));
          pt += 4;

          int len = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
          pt += 4;
          addMeta("Dim" + j + " physical length",
            DataTools.stripString(new String(temp, pt, len)));
          pt += len;

          len = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
          pt += 4;
          addMeta("Dim" + j + " physical origin",
            DataTools.stripString(new String(temp, pt, len)));
          pt += len;

          len = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
          pt += 4;
          addMeta("Dim" + j + " name",
            DataTools.stripString(new String(temp, pt, len)));
          pt += len;

          len = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
          pt += 4;
          addMeta("Dim" + j + " description",
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
        int nDims = DataTools.bytesToInt(temp, 0, 4, core.littleEndian[0]);
        addMeta("Number of time-stamped dimensions", new Integer(nDims));
        addMeta("Time-stamped dimension",
          new Integer(DataTools.bytesToInt(temp, 4, 4, core.littleEndian[0])));

        int pt = 8;

        for (int j=0; j < nDims; j++) {
          int v = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
          addMeta("Dimension " + j + " ID", new Integer(v));
          pt += 4;
          v = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
          addMeta("Dimension " + j + " size", new Integer(v));
          pt += 4;
          v = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
          addMeta("Dimension " + j + " distance between dimensions",
            new Integer(v));
          pt += 4;
        }

        int numStamps = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
        pt += 4;
        addMeta("Number of time-stamps", new Integer(numStamps));
        for (int j=0; j<numStamps; j++) {
          addMeta("Timestamp " + j,
            DataTools.stripString(new String(temp, pt, 64)));
          pt += 64;
        }

        int numTMs = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
        pt += 4;
        addMeta("Number of time-markers", new Integer(numTMs));
        for (int j=0; j<numTMs; j++) {
          int numDims = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
          pt += 4;

          for (int k=0; k<numDims; k++) {
            int v = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
            addMeta("Time-marker " + j +
              " Dimension " + k + " coordinate", new Integer(v));
            pt += 4;
          }
          addMeta("Time-marker " + j,
            DataTools.stripString(new String(temp, pt, 64)));
          pt += 64;
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
        int len = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
        pt += 4;

        addMeta("Image Description",
          DataTools.stripString(new String(temp, pt, 2*len)));
        pt += 2*len;
        len = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
        pt += 4;

        addMeta("Main file extension",
          DataTools.stripString(new String(temp, pt, 2*len)));
        pt += 2*len;

        len = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
        pt += 4;
        addMeta("Single image format identifier",
          DataTools.stripString(new String(temp, pt, 2*len)));
        pt += 2*len;

        len = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
        pt += 4;
        addMeta("Single image extension",
          DataTools.stripString(new String(temp, pt, 2*len)));
      }

      temp = (byte[]) headerIFDs[i].get(new Integer(70));
      if (temp != null) {
        // LUT data
        // ID_LUTDESC
        int pt = 0;
        int nChannels = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
        pt += 4;
        addMeta("Number of LUT channels", new Integer(nChannels));
        addMeta("ID of colored dimension",
          new Integer(DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0])));
        pt += 4;

        if (nChannels > 4) nChannels = 3;
        core.sizeC[i] = nChannels;

        for (int j=0; j<nChannels; j++) {
          int v = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
          addMeta("LUT Channel " + j + " version", new Integer(v));
          pt += 4;

          int invert = DataTools.bytesToInt(temp, pt, 1, core.littleEndian[0]);
          pt += 1;
          boolean inverted = invert == 1;
          addMeta("LUT Channel " + j + " inverted?",
            new Boolean(inverted).toString());

          int length = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
          pt += 4;
          addMeta("LUT Channel " + j + " description",
            DataTools.stripString(new String(temp, pt, length)));

          pt += length;
          length = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
          pt += 4;
          addMeta("LUT Channel " + j + " filename",
            DataTools.stripString(new String(temp, pt, length)));
          pt += length;
          length = DataTools.bytesToInt(temp, pt, 4, core.littleEndian[0]);
          pt += 4;

          String name = DataTools.stripString(new String(temp, pt, length));

          addMeta("LUT Channel " + j + " name", name);
          pt += length;

          pt += 8;
        }
      }
    }

    //core = new CoreMetadata(numSeries);
    Arrays.fill(core.orderCertain, true);

    // sizeC is null here if the file we opened was a TIFF.
    // However, the sizeC field will be adjusted anyway by
    // a later call to initMetadata.
    if (core.sizeC != null) {
      int oldSeries = getSeries();
      for (int i=0; i<core.sizeC.length; i++) {
        setSeries(i);
        core.sizeZ[i] /= core.sizeC[i];
      }
      setSeries(oldSeries);
    }

    Integer v = (Integer) getMeta("Real world resolution");

    // the metadata store we're working with
    MetadataStore store = getMetadataStore();

    byte[] f = new byte[4];
    for (int i=0; i<numSeries; i++) {
      core.orderCertain[i] = true;
      core.interleaved[i] = true;

      in.seek(0);
      in.read(f);
      core.littleEndian[i] = (f[0] == TiffTools.LITTLE &&
        f[1] == TiffTools.LITTLE && f[2] == TiffTools.LITTLE &&
        f[3] == TiffTools.LITTLE);

      if (core.sizeC[i] == 0) core.sizeC[i] = 1;
      core.sizeT[i] += 1;
      core.currentOrder[i] = core.sizeC[i] == 1 ? "XYZTC" : "XYCZT";
      if (core.sizeZ[i] == 0) core.sizeZ[i] = 1;

      switch (bpp) {
        case 1:
          core.pixelType[i] = FormatTools.UINT8;
          break;
        case 2:
          core.pixelType[i] = FormatTools.UINT16;
          break;
        case 3:
          core.pixelType[i] = FormatTools.UINT8;
          break;
        case 4:
          core.pixelType[i] = FormatTools.INT32;
          break;
        case 6:
          core.pixelType[i] = FormatTools.INT16;
          break;
        case 8:
          core.pixelType[i] = FormatTools.DOUBLE;
          break;
      }

      core.rgb[i] =
        core.imageCount[i] != core.sizeC[i] * core.sizeZ[i] * core.sizeT[i];

      Integer ii = new Integer(i);

      core.rgb[i] = false;
      //core.sizeC[i] *= 3;

      core.indexed[i] = true;
      core.falseColor[i] = true;
      core.metadataComplete[i] = true;

      String timestamp = (String) getMeta("Timestamp " + (i+1));
      String description = (String) getMeta("Image Description");

      if (timestamp != null) {
        SimpleDateFormat parse =
          new SimpleDateFormat("yyyy:MM:dd,HH:mm:ss:SSS");
        Date date = parse.parse(timestamp, new ParsePosition(0));
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        timestamp = fmt.format(date);
      }

      store.setImage((String) seriesNames.get(i), timestamp, description, ii);
    }
    FormatTools.populatePixels(store, this);

    for (int i=0; i<core.sizeC.length; i++) {
      for (int j=0; j<core.sizeC[i]; j++) {
        store.setLogicalChannel(j, null, null, null, null, null, null, null,
          null, null, null, null, null, null, null, null, null, null, null,
          null, null, null, null, null, new Integer(i));
        // TODO: get channel min/max from metadata
//        store.setChannelGlobalMinMax(j, getChannelGlobalMinimum(currentId, j),
//          getChannelGlobalMaximum(currentId, j), ii);
      }
    }
  }

  private boolean usedFile(String s) {
    if (files == null) return false;

    for (int i=0; i<files.length; i++) {
      if (files[i] == null) continue;
      for (int j=0; j<files[i].size(); j++) {
        if (((String) files[i].get(j)).endsWith(s)) return true;
      }
    }
    return false;
  }

}
