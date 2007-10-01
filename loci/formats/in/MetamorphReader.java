//
// MetamorphReader.java
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
import java.text.DecimalFormat;
import java.util.*;
import loci.formats.*;

/**
 * Reader is the file format reader for Metamorph STK files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/MetamorphReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/MetamorphReader.java">SVN</a></dd></dl>
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Sebastien Huart Sebastien dot Huart at curie.fr
 */
public class MetamorphReader extends BaseTiffReader {

  // -- Constants --

  // IFD tag numbers of important fields
  private static final int METAMORPH_ID = 33628;
  private static final int UIC1TAG = METAMORPH_ID;
  private static final int UIC2TAG = 33629;
  private static final int UIC3TAG = 33630;
  private static final int UIC4TAG = 33631;

  // -- Fields --

  /** The TIFF's name */
  private String imageName;

  /** The TIFF's creation date */
  private String imageCreationDate;

  //** The TIFF's emWavelength */
  private long[] emWavelength;

  private int mmPlanes; //number of metamorph planes

  private MetamorphReader r;

  /** List of STK files in the dataset. */
  private String[][] stks;

  // -- Constructor --

  /** Constructs a new Metamorph reader. */
  public MetamorphReader() {
    super("Metamorph STK", new String[] {"stk", "nd"});
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    // If the file is a Metamorph STK file, it should have a specific IFD tag.
    // Most Metamorph files seem to have the IFD information at the end, so it
    // is difficult to determine whether or not the block is a Metamorph block
    // without being passed the entire file.  Therefore, we will check the only
    // things we can reasonably check at the beginning of the file, and if we
    // happen to be passed the entire file, well, great, we'll check that too.

    // Must be little-endian TIFF
    if (block.length < 3) return false;
    if (block[0] != TiffTools.LITTLE) return false; // denotes little-endian
    if (block[1] != TiffTools.LITTLE) return false;
    if (block[2] != TiffTools.MAGIC_NUMBER) return false; // denotes TIFF
    if (block.length < 8) return true; // we have no way of verifying further
    int ifdlocation = DataTools.bytesToInt(block, 4, true);
    if (ifdlocation + 1 > block.length) {
      // we have no way of verifying this is a Metamorph file.
      // It is at least a TIFF.
      return true;
    }
    else {
      int ifdnumber = DataTools.bytesToInt(block, ifdlocation, 2, true);
      for (int i = 0; i < ifdnumber; i++) {
        if (ifdlocation + 3 + (i * 12) > block.length) return true;
        else {
          int ifdtag = DataTools.bytesToInt(block,
            ifdlocation + 2 + (i * 12), 2, true);
          if (ifdtag == METAMORPH_ID) return true; // absolutely a valid file
        }
      }
      return false; // we went through the IFD; the ID wasn't found.
    }
  }

  /* @see loci.formats.IFormatReader#close() */
  public void close() throws IOException {
    super.close();
    if (r != null) r.close();
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    if (id.toLowerCase().endsWith(".nd")) return FormatTools.MUST_GROUP;

    Location l = new Location(id).getAbsoluteFile();
    String[] files = l.getParentFile().list();

    for (int i=0; i<files.length; i++) {
      String s = files[i].toLowerCase();
      if (s.endsWith(".nd") && id.startsWith(files[i].substring(0,
        s.lastIndexOf("."))))
      {
        return FormatTools.CAN_GROUP;
      }
    }

    return FormatTools.CANNOT_GROUP;
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    return stks == null ? super.getUsedFiles() : stks[series];
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    if (stks == null || stks[series].length == 1) {
      return super.openBytes(no, buf);
    }

    int[] coords = FormatTools.getZCTCoords(this, no % core.sizeZ[series]);
    int ndx = no / core.sizeZ[series];
    String file = stks[series][ndx];

    if (r == null) r = new MetamorphReader();
    r.setId(file);
    return r.openBytes(coords[0], buf);
  }

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (id.toLowerCase().endsWith(".nd")) {
      if (currentId != null) {
        String[] s = getUsedFiles();
        for (int i=0; i<s.length; i++) {
          if (id.equals(s[i])) return;
        }
      }

      close();
      currentId = id;
      metadata = new Hashtable();

      core = new CoreMetadata(1);
      Arrays.fill(core.orderCertain, true);

      // reinitialize the MetadataStore
      getMetadataStore().createRoot();

      // find an associated STK file
      String stkFile = id.substring(0, id.lastIndexOf("."));
      Location parent = new Location(id).getAbsoluteFile().getParentFile();
      String[] dirList = parent.list();
      for (int i=0; i<dirList.length; i++) {
        String s = dirList[i].toLowerCase();
        if (s.endsWith(".stk") && (dirList[i].indexOf(stkFile.substring(
          stkFile.lastIndexOf(File.separator) + 1) + "_w") != -1))
        {
          stkFile =
            new Location(parent.getPath(), dirList[i]).getAbsolutePath();
          break;
        }
      }

      super.initFile(stkFile);
    }
    else super.initFile(id);

    Location ndfile = null;

    if (id.toLowerCase().endsWith(".nd")) ndfile = new Location(id);

    if (ndfile != null && ndfile.exists() &&
      (fileGroupOption(id) == FormatTools.MUST_GROUP || isGroupFiles()))
    {
      RandomAccessStream ndStream =
        new RandomAccessStream(ndfile.getAbsolutePath());
      String line = ndStream.readLine().trim();

      while (!line.equals("\"EndFile\"")) {
        String key = line.substring(1, line.indexOf(",") - 1).trim();
        String value = line.substring(line.indexOf(",") + 1).trim();

        addMeta(key, value);
        line = ndStream.readLine().trim();
      }

      // figure out how many files we need

      String z = (String) getMeta("NZSteps");
      String c = (String) getMeta("NWavelengths");
      String t = (String) getMeta("NTimePoints");

      int zc = core.sizeZ[0], cc = core.sizeC[0], tc = core.sizeT[0];

      if (z != null) zc = Integer.parseInt(z);
      if (c != null) cc = Integer.parseInt(c);
      if (t != null) tc = Integer.parseInt(t);

      int numFiles = cc * tc;

      // determine series count

      boolean[] hasZ = new boolean[cc];
      int seriesCount = 1;
      for (int i=0; i<cc; i++) {
        hasZ[i] = ((String) getMeta("WaveDoZ" + (i + 1))).equals("TRUE");
        if (i > 0 && hasZ[i] != hasZ[i - 1]) seriesCount = 2;
      }

      int channelsInFirstSeries = cc;
      if (seriesCount == 2) {
        channelsInFirstSeries = 0;
        for (int i=0; i<cc; i++) {
          if (hasZ[i]) channelsInFirstSeries++;
        }
      }

      stks = new String[seriesCount][];
      if (seriesCount == 1) stks[0] = new String[numFiles];
      else {
        stks[0] = new String[channelsInFirstSeries * tc];
        stks[1] = new String[(cc - channelsInFirstSeries) * tc];
      }

      String prefix = ndfile.getPath();
      prefix = prefix.substring(prefix.lastIndexOf(File.separator) + 1,
        prefix.lastIndexOf("."));

      int[] pt = new int[seriesCount];
      for (int i=0; i<tc; i++) {
        for (int j=0; j<cc; j++) {
          String chName = (String) getMeta("WaveName" + (j + 1));
          chName = chName.substring(1, chName.length() - 1);
          int seriesNdx = seriesCount == 1 ? 0 : (hasZ[j] ? 0 : 1);
          stks[seriesNdx][pt[seriesNdx]++] =
            prefix + "_w" + (j + 1) + chName + "_t" + (i + 1) + ".STK";
        }
      }

      ndfile = ndfile.getAbsoluteFile();

      for (int s=0; s<stks.length; s++) {
        for (int f=0; f<stks[s].length; f++) {
          Location l = new Location(ndfile.getParent(), stks[s][f]);
          if (!l.exists()) {
            // '%' can be converted to '-'
            if (stks[s][f].indexOf("%") != -1) {
              stks[s][f] = stks[s][f].replaceAll("%", "-");
              l = new Location(ndfile.getParent(), stks[s][f]);
              if (!l.exists()) {
                // try replacing extension
                stks[s][f] = stks[s][f].substring(0,
                  stks[s][f].lastIndexOf(".")) + ".TIF";
                l = new Location(ndfile.getParent(), stks[s][f]);
                if (!l.exists()) {
                  stks = null;
                  return;
                }
              }
            }

            if (!l.exists()) {
              // try replacing extension
              stks[s][f] = stks[s][f].substring(0,
                stks[s][f].lastIndexOf(".")) + ".TIF";
              l = new Location(ndfile.getParent(), stks[s][f]);
              if (!l.exists()) {
                stks = null;
                return;
              }
            }
          }
          stks[s][f] = l.getAbsolutePath();
        }
      }

      core.sizeZ[0] = zc;
      core.sizeC[0] = cc;
      core.sizeT[0] = tc;
      core.imageCount[0] = zc * tc * cc;
      core.currentOrder[0] = "XYZCT";

      if (stks.length > 1) {
        CoreMetadata newCore = new CoreMetadata(stks.length);
        for (int i=0; i<stks.length; i++) {
          newCore.sizeX[i] = core.sizeX[0];
          newCore.sizeY[i] = core.sizeY[0];
          newCore.sizeZ[i] = core.sizeZ[0];
          newCore.sizeC[i] = core.sizeC[0];
          newCore.sizeT[i] = core.sizeT[0];
          newCore.pixelType[i] = core.pixelType[0];
          newCore.imageCount[i] = core.imageCount[0];
          newCore.currentOrder[i] = core.currentOrder[0];
          newCore.rgb[i] = core.rgb[0];
          newCore.littleEndian[i] = core.littleEndian[0];
          newCore.interleaved[i] = core.interleaved[0];
          newCore.orderCertain[i] = true;
        }
        newCore.sizeC[0] = stks[0].length / newCore.sizeT[0];
        newCore.sizeC[1] = stks[1].length / newCore.sizeT[1];
        newCore.sizeZ[1] = 1;
        newCore.imageCount[0] =
          newCore.sizeC[0] * newCore.sizeT[0] * newCore.sizeZ[0];
        newCore.imageCount[1] = newCore.sizeC[1] * newCore.sizeT[1];
        core = newCore;
      }
    }
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    try {
      // Now that the base TIFF standard metadata has been parsed, we need to
      // parse out the STK metadata from the UIC4TAG.
      TiffIFDEntry uic1tagEntry = TiffTools.getFirstIFDEntry(in, UIC1TAG);
      TiffIFDEntry uic2tagEntry = TiffTools.getFirstIFDEntry(in, UIC2TAG);
      TiffIFDEntry uic4tagEntry = TiffTools.getFirstIFDEntry(in, UIC4TAG);
      int planes = uic4tagEntry.getValueCount();
      mmPlanes = planes;
      parseUIC2Tags(uic2tagEntry.getValueOffset());
      parseUIC4Tags(uic4tagEntry.getValueOffset());
      parseUIC1Tags(uic1tagEntry.getValueOffset(),
        uic1tagEntry.getValueCount());
      in.seek(uic4tagEntry.getValueOffset());

      // copy ifds into a new array of Hashtables that will accommodate the
      // additional image planes
      long[] uic2 = TiffTools.getIFDLongArray(ifds[0], UIC2TAG, true);
      core.imageCount[0] = uic2.length;

      long[] uic3 = TiffTools.getIFDLongArray(ifds[0], UIC3TAG, true);
      for (int i=0; i<uic3.length; i++) {
        in.seek(uic3[i]);
        put("Wavelength [" + intFormatMax(i, mmPlanes) + "]",
          in.readLong() / in.readLong());
      }

      Hashtable[] tempIFDs = new Hashtable[core.imageCount[0]];

      long[] oldOffsets = TiffTools.getIFDLongArray(ifds[0],
          TiffTools.STRIP_OFFSETS, true);

      long[] stripByteCounts = TiffTools.getIFDLongArray(ifds[0],
          TiffTools.STRIP_BYTE_COUNTS, true);

      int stripsPerImage = oldOffsets.length;

      int check = TiffTools.getIFDIntValue(ifds[0],
        TiffTools.PHOTOMETRIC_INTERPRETATION);
      if (check == TiffTools.RGB_PALETTE) {
        TiffTools.putIFDValue(ifds[0], TiffTools.PHOTOMETRIC_INTERPRETATION,
          TiffTools.BLACK_IS_ZERO);
      }

      emWavelength = TiffTools.getIFDLongArray(ifds[0], UIC3TAG, true);

      // for each image plane, construct an IFD hashtable

      int pointer = 0;

      Hashtable temp;
      for(int i=0; i<core.imageCount[0]; i++) {
        temp = new Hashtable();

        // copy most of the data from 1st IFD
        temp.put(new Integer(TiffTools.LITTLE_ENDIAN), ifds[0].get(
            new Integer(TiffTools.LITTLE_ENDIAN)));
        temp.put(new Integer(TiffTools.IMAGE_WIDTH), ifds[0].get(
            new Integer(TiffTools.IMAGE_WIDTH)));
        temp.put(new Integer(TiffTools.IMAGE_LENGTH),
            ifds[0].get(new Integer(TiffTools.IMAGE_LENGTH)));
        temp.put(new Integer(TiffTools.BITS_PER_SAMPLE), ifds[0].get(
            new Integer(TiffTools.BITS_PER_SAMPLE)));
        temp.put(new Integer(TiffTools.COMPRESSION), ifds[0].get(
            new Integer(TiffTools.COMPRESSION)));
        temp.put(new Integer(TiffTools.PHOTOMETRIC_INTERPRETATION),
            ifds[0].get(new Integer(TiffTools.PHOTOMETRIC_INTERPRETATION)));
        temp.put(new Integer(TiffTools.STRIP_BYTE_COUNTS), ifds[0].get(
            new Integer(TiffTools.STRIP_BYTE_COUNTS)));
        temp.put(new Integer(TiffTools.ROWS_PER_STRIP), ifds[0].get(
            new Integer(TiffTools.ROWS_PER_STRIP)));
        temp.put(new Integer(TiffTools.X_RESOLUTION), ifds[0].get(
            new Integer(TiffTools.X_RESOLUTION)));
        temp.put(new Integer(TiffTools.Y_RESOLUTION), ifds[0].get(
            new Integer(TiffTools.Y_RESOLUTION)));
        temp.put(new Integer(TiffTools.RESOLUTION_UNIT), ifds[0].get(
            new Integer(TiffTools.RESOLUTION_UNIT)));
        temp.put(new Integer(TiffTools.PREDICTOR), ifds[0].get(
            new Integer(TiffTools.PREDICTOR)));

        // now we need a StripOffsets entry

        long planeOffset = i*(oldOffsets[stripsPerImage - 1] +
            stripByteCounts[stripsPerImage - 1] - oldOffsets[0]);

        long[] newOffsets = new long[oldOffsets.length];
        newOffsets[0] = planeOffset + oldOffsets[0];

        for(int j=1; j<newOffsets.length; j++) {
          newOffsets[j] = newOffsets[j-1] + stripByteCounts[0];
        }

        temp.put(new Integer(TiffTools.STRIP_OFFSETS), newOffsets);

        tempIFDs[pointer] = temp;
        pointer++;
      }
      ifds = tempIFDs;
    }
    catch (UnknownTagException exc) { trace(exc); }
    catch (NullPointerException exc) { trace(exc); }
    catch (IOException exc) { trace(exc); }
    catch (FormatException exc) { trace(exc); }

    try {
      super.initStandardMetadata();
    }
    catch (FormatException exc) {
      if (debug) trace(exc);
    }
    catch (IOException exc) {
      if (debug) trace(exc);
    }

    // parse (mangle) TIFF comment
    String descr = (String) getMeta("Comment");
    if (descr != null) {
      StringTokenizer st = new StringTokenizer(descr, "\n");
      StringBuffer sb = new StringBuffer();
      boolean first = true;
      while (st.hasMoreTokens()) {
        String line = st.nextToken();
        int colon = line.indexOf(": ");

        if (colon < 0) {
          // normal line (not a key/value pair)
          if (line.trim().length() > 0) {
            // not a blank line
            sb.append(line);
            if (!line.endsWith(".")) sb.append(".");
            sb.append("  ");
          }
          first = false;
          continue;
        }

        if (first) {
          // first line could be mangled; make a reasonable guess
          int dot = line.lastIndexOf(".", colon);
          if (dot >= 0) {
            String s = line.substring(0, dot + 1);
            sb.append(s);
            if (!s.endsWith(".")) sb.append(".");
            sb.append("  ");
          }
          line = line.substring(dot + 1);
          colon -= dot + 1;
          first = false;
        }

        // add key/value pair embedded in comment as separate metadata
        String key = line.substring(0, colon);
        String value = line.substring(colon + 2);
        put(key, value);
      }

      // replace comment with trimmed version
      descr = sb.toString().trim();
      if (descr.equals("")) metadata.remove("Comment");
      else put("Comment", descr);
    }
    try {
      if (core.sizeZ[0] == 0) {
        core.sizeZ[0] =
          TiffTools.getIFDLongArray(ifds[0], UIC2TAG, true).length;
      }
      if (core.sizeT[0] == 0) core.sizeT[0] = getImageCount() / core.sizeZ[0];
    }
    catch (FormatException exc) {
      if (debug) trace(exc);
    }
  }

  /* @see BaseTiffReader#getImageName() */
  protected String getImageName() {
    if (imageName == null) return super.getImageName();
    return imageName;
  }

  /* @see BaseTiffReader#getImageCreationDate() */
  protected String getImageCreationDate() {
    if (imageCreationDate == null) return super.getImageCreationDate();
    return imageCreationDate;
  }

  Integer getEmWave(int i) {
    if (emWavelength == null || emWavelength[i] == 0)  return null;
    return new Integer((int) emWavelength[i]);
  }

  // -- Helper methods --

  /**
   * Populates metadata fields with some contained in MetaMorph UIC2 Tag.
   * (for each plane: 6 integers:
   * zdistance numerator, zdistance denominator,
   * creation date, creation time, modif date, modif time)
   * @param uic2offset offset to UIC2 (33629) tag entries
   *
   * not a regular tiff tag (6*N entries, N being the tagCount)
   * @throws IOException
   */
  void parseUIC2Tags(long uic2offset) throws IOException {

    long saveLoc = in.getFilePointer();
    in.seek(uic2offset);

    /*number of days since the 1st of January 4713 B.C*/
    int cDate;
    /*milliseconds since 0:00*/
    int cTime;

    /*z step, distance separating previous slice from  current one*/
    double zDistance;
    String iAsString;

    for (int i=0; i<mmPlanes; i++) {
      iAsString = intFormatMax(i, mmPlanes);
      int num = in.readInt();
      int den = in.readInt();
      zDistance = (double) num / den;
      put("zDistance[" + iAsString + "]", zDistance);
      cDate = in.readInt();
      put("creationDate[" + iAsString + "]", decodeDate(cDate));

      cTime = in.readInt();
      put("creationTime[" + iAsString + "]", decodeTime(cTime));
      // modification date and time are skipped as they all seem equal to 0...?
      in.skip(8);
    }
    in.seek(saveLoc);
  }

  /**
   * UIC4 metadata parser
   *
   * UIC4 Table contains per-plane blocks of metadata
   * stage X/Y positions,
   * camera chip offsets,
   * stage labels...
   * @param long uic4offset: offset of UIC4 table (not tiff-compliant)
   * @throws IOException
   */
  private void parseUIC4Tags(long uic4offset) throws IOException {
    long saveLoc = in.getFilePointer();
    in.seek(uic4offset);
    boolean end=false;
    short id;
    while (!end) {
      id = in.readShort();

      switch (id) {
        case 0:
          end=true;
          break;
        case 28:
          readStagePositions();
          break;
        case 29:
          readCameraChipOffsets();
          break;
        case 37:
          readStageLabels();
          break;
        case 40:
          readAbsoluteZ();
          break;
        case 41:
          readAbsoluteZValid();
          break;
        default:
          //unknown tags: do nothing
          break;
        //28->stagePositions
        //29->cameraChipOffsets
        //30->stageLabel
        //40->AbsoluteZ
        //41AbsoluteZValid
        //0->end
      }
    }
    in.seek(saveLoc);
  }

  void readStagePositions() throws IOException {
    int nx, dx, ny, dy;
    // for each plane:
    // 2 ints (rational:numerator,denominator) for stage X,
    // 2 ints (idem) for stage Y position
    double xPosition, yPosition;
    String iAsString;
    for(int i=0; i<mmPlanes; i++) {
      nx = in.readInt();
      dx = in.readInt();
      ny = in.readInt();
      dy = in.readInt();
      xPosition = (dx == 0) ? Double.NaN : (double) nx / dx;
      yPosition = (dy == 0) ? Double.NaN : (double) ny / dy;
      iAsString = intFormatMax(i, mmPlanes);
      put("stageX[" + iAsString + "]", xPosition);
      put("stageY[" + iAsString + "]", yPosition);
    }

  }

  void readCameraChipOffsets() throws IOException {
    int nx, dx, ny, dy;
    double cameraXChipOffset, cameraYChipOffset;
    String iAsString;
    for(int i=0; i<mmPlanes; i++) {
      iAsString = intFormatMax(i, mmPlanes);
      nx = in.readInt();
      dx = in.readInt();
      ny = in.readInt();
      dy = in.readInt();
      cameraXChipOffset = (dx == 0) ? Double.NaN: (double) nx / dx;
      cameraYChipOffset = (dy == 0) ? Double.NaN: (double) ny/ dy;
      put("cameraXChipOffset[" + iAsString + "]", cameraXChipOffset);
      put("cameraYChipOffset[" + iAsString + "]", cameraYChipOffset);
    }
  }

  void readStageLabels() throws IOException {
    int strlen;
    byte[] curlabel;
    String iAsString;
    for (int i=0; i<mmPlanes; i++) {
      iAsString = intFormatMax(i, mmPlanes);
      strlen = in.readInt();
      curlabel = new byte[strlen];
      in.read(curlabel);
      put("stageLabel[" + iAsString + "]", new String(curlabel));
    }
  }

  void readAbsoluteZ() throws IOException {
    int nz, dz;
    double absoluteZ;
    for(int i=0; i<mmPlanes; i++) {
      nz = in.readInt();
      dz = in.readInt();
      absoluteZ = (dz == 0) ? Double.NaN : (double) nz / dz;
      put("absoluteZ[" + intFormatMax(i, mmPlanes) + "]", absoluteZ);
    }
  }

  void readAbsoluteZValid() throws IOException {
    for (int i=0; i<mmPlanes; i++) {
      put("absoluteZValid[" + intFormatMax(i, mmPlanes) + "]", in.readInt());
    }
  }

  /**
   * UIC1 entry parser
   * @throws IOException
   * @param long uic1offset : offset as found in the tiff tag 33628 (UIC1Tag)
   * @param int uic1count : number of entries in UIC1 table (not tiff-compliant)
   */
  private void parseUIC1Tags(long uic1offset, int uic1count) throws IOException
  {
    // Loop through and parse out each field. A field whose
    // code is "0" represents the end of the fields so we'll stop
    // when we reach that; much like a NULL terminated C string.
    long saveLoc = in.getFilePointer();
    in.seek(uic1offset);
    int currentID, valOrOffset;
    // variable declarations, because switch is dumb
    int num, denom;
    String thedate, thetime;
    long lastOffset;
    byte[] toread;
    for (int i=0; i<uic1count; i++) {
      currentID = in.readInt();
      valOrOffset = in.readInt();

      switch (currentID) {
        case 1:
          put("MinScale", valOrOffset);
          break;
        case 2:
          put("MaxScale", valOrOffset);
          break;
        case 3:
          int calib = valOrOffset;
          String calibration = calib != 0 ? "on" : "off";
          put("Spatial Calibration", calibration);
          break;
        case 4:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          num = in.readInt();
          denom = in.readInt();
          put("XCalibration", new TiffRational(num, denom));
          in.seek(lastOffset);
          break;
        case 5:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          num = in.readInt();
          denom = in.readInt();
          put("YCalibration", new TiffRational(num, denom));
          in.seek(lastOffset);
          break;
        case 6:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          num = in.readInt();
          toread = new byte[num];
          in.read(toread);
          put("CalibrationUnits", new String(toread));
          in.seek(lastOffset);
          break;
        case 7:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          num = in.readInt();
          toread = new byte[num];
          in.read(toread);
          String name = new String(toread);
          put("Name", name);
          imageName = name;
          in.seek(lastOffset);
          break;

        case 8:
          int thresh = valOrOffset;
          String threshState = "off";
          if (thresh == 1) threshState = "inside";
          else if (thresh == 2) threshState = "outside";
          put("ThreshState", threshState);
          break;
        case 9:
          put("ThreshStateRed", valOrOffset);
          break;
          // there is no 10
        case 11:
          put("ThreshStateGreen", valOrOffset);
          break;
        case 12:
          put("ThreshStateBlue", valOrOffset);
          break;
        case 13:
          put("ThreshStateLo", valOrOffset);
          break;
        case 14:
          put("ThreshStateHi", valOrOffset);
          break;
        case 15:
          int zoom = valOrOffset;
          put("Zoom", zoom);
          //OMETools.setAttribute(ome, "DisplayOptions", "Zoom", "" + zoom);
          break;
        case 16: // oh how we hate you Julian format...
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          thedate = decodeDate(in.readInt());
          thetime = decodeTime(in.readInt());
          put("DateTime", thedate + " " + thetime);
          imageCreationDate = thedate + " " + thetime;
          in.seek(lastOffset);
          break;
        case 17:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          thedate = decodeDate(in.readInt());
          thetime = decodeTime(in.readInt());
          put("LastSavedTime", thedate + " " + thetime);
          in.seek(lastOffset);
          break;
        case 18:
          put("currentBuffer", valOrOffset);
          break;
        case 19:
          put("grayFit", valOrOffset);
          break;
        case 20:
          put("grayPointCount", valOrOffset);
          break;
        case 21:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          num = in.readInt();
          denom = in.readInt();
          put("grayX", new TiffRational(num, denom));
          in.seek(lastOffset);
          break;
        case 22:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          num = in.readInt();
          denom = in.readInt();
          put("gray", new TiffRational(num, denom));
          in.seek(lastOffset);
          break;
        case 23:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          num = in.readInt();
          denom = in.readInt();
          put("grayMin", new TiffRational(num, denom));
          in.seek(lastOffset);
          break;
        case 24:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          num = in.readInt();
          denom = in.readInt();
          put("grayMax", new TiffRational(num, denom));
          in.seek(lastOffset);
          break;
        case 25:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          num = in.readInt();
          toread = new byte[num];
          in.read(toread);
          put("grayUnitName", new String(toread));
          in.seek(lastOffset);
          break;
        case 26:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          int standardLUT = in.readInt();
          in.seek(lastOffset);
          String standLUT;
          switch (standardLUT) {
            case 0:
              standLUT = "monochrome";
              break;
            case 1:
              standLUT = "pseudocolor";
              break;
            case 2:
              standLUT = "Red";
              break;
            case 3:
              standLUT = "Green";
              break;
            case 4:
              standLUT = "Blue";
              break;
            case 5:
              standLUT = "user-defined";
              break;
            default:
              standLUT = "monochrome"; break;
          }
          put("StandardLUT", standLUT);
          break;
        case 27:
          put("Wavelength", valOrOffset);
          break;
        case 30:
          put("OverlayMask", valOrOffset);
          break;
        case 31:
          put("OverlayCompress", valOrOffset);
          break;
        case 32:
          put("Overlay", valOrOffset);
          break;
        case 33:
          put("SpecialOverlayMask", valOrOffset);
          break;
        case 34:
          put("SpecialOverlayCompress", in.readInt());
          break;
        case 35:
          put("SpecialOverlay", valOrOffset);
          break;
        case 36:
          put("ImageProperty", valOrOffset);
          break;
        case 38:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          num = in.readInt();
          denom = in.readInt();
          put("AutoScaleLoInfo", new TiffRational(num, denom));
          in.seek(lastOffset);
          break;
        case 39:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          num = in.readInt();
          denom = in.readInt();
          put("AutoScaleHiInfo", new TiffRational(num, denom));
          in.seek(lastOffset);
          break;
        case 42:
          put("Gamma", valOrOffset);
          break;
        case 43:
          put("GammaRed", valOrOffset);
          break;
        case 44:
          put("GammaGreen", valOrOffset);
          break;
        case 45:
          put("GammaBlue", valOrOffset);
          break;
        case 46:
          lastOffset = in.getFilePointer();
          in.seek(valOrOffset);
          int xBin, yBin;
          xBin = in.readInt();
          yBin = in.readInt();
          put("CameraBin", new String("(" + xBin + "," + yBin + ")"));
          in.seek(lastOffset);
          break;
        default:
          break;
      }
    }
    in.seek(saveLoc);
  }

  // -- Utility methods --

  /** Converts a Julian date value into a human-readable string. */
  public static String decodeDate(int julian) {
    long a, b, c, d, e, alpha, z;
    short day, month, year;

    // code reused from the Metamorph data specification
    z = julian + 1;

    if (z < 2299161L) a = z;
    else {
      alpha = (long) ((z - 1867216.25) / 36524.25);
      a = z + 1 + alpha - alpha / 4;
    }

    b = (a > 1721423L ? a + 1524 : a + 1158);
    c = (long) ((b - 122.1) / 365.25);
    d = (long) (365.25 * c);
    e = (long) ((b - d) / 30.6001);

    day = (short) (b - d - (long) (30.6001 * e));
    month = (short) ((e < 13.5) ? e - 1 : e - 13);
    year = (short) ((month > 2.5) ? (c - 4716) : c - 4715);

    return day + "/" + month + "/" + year;
  }

  /** Converts a time value in milliseconds into a human-readable string. */
  public static String decodeTime(int millis) {
    int ms, seconds, minutes, hours;

    ms = millis % 1000;
    millis -= ms;
    millis /= 1000;
    seconds = millis % 60;
    millis -= seconds;
    millis /= 60;
    minutes = millis % 60;
    millis -= minutes;
    millis /= 60;
    hours = millis;
    return intFormat(hours, 2) + ":" + intFormat(minutes, 2) + ":" +
      intFormat(seconds, 2) + "." + intFormat(ms, 3);
  }

  /** Formats an integer value with leading 0s if needed. */
  public static String intFormat(int myint, int digits) {
    String formatstring = "0";
    while (formatstring.length() < digits) {
      formatstring += "0";
    }
    DecimalFormat df = new DecimalFormat(formatstring);
    return df.format(myint);
  }

  /**
   * Formats an integer with leading 0 using maximum sequence number.
   *
   * @param myint integer to format
   * @param maxint max of "myint"
   * @return String
   */
  public static String intFormatMax(int myint, int maxint) {
    return intFormat(myint, new Integer(maxint).toString().length());
  }

}
