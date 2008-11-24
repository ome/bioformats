//
// MetamorphReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import loci.common.*;
import loci.formats.*;
import loci.formats.meta.*;

/**
 * Reader is the file format reader for Metamorph STK files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/MetamorphReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/MetamorphReader.java">SVN</a></dd></dl>
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Sebastien Huart Sebastien dot Huart at curie.fr
 */
public class MetamorphReader extends BaseTiffReader {

  // -- Constants --

  public static final String[] ND_SUFFIX = {"nd"};
  public static final String[] STK_SUFFIX = {"stk", "tif", "tiff"};

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

  /** The TIFF's emWavelength */
  private long[] emWavelength;

  private int mmPlanes; //number of metamorph planes

  private MetamorphReader stkReader;

  /** List of STK files in the dataset. */
  private String[][] stks;

  private String ndFilename;

  // -- Constructor --

  /** Constructs a new Metamorph reader. */
  public MetamorphReader() {
    super("Metamorph STK", new String[] {"stk", "nd"});
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    if (checkSuffix(id, ND_SUFFIX)) return FormatTools.MUST_GROUP;

    Location l = new Location(id).getAbsoluteFile();
    String[] files = l.getParentFile().list();

    for (int i=0; i<files.length; i++) {
      if (checkSuffix(files[i], ND_SUFFIX) &&
       id.startsWith(files[i].substring(0, files[i].lastIndexOf("."))))
      {
        return FormatTools.MUST_GROUP;
      }
    }

    return FormatTools.CANNOT_GROUP;
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    if (stks == null) return super.getUsedFiles();
    Vector v = new Vector();
    if (ndFilename != null) v.add(ndFilename);
    for (int i=0; i<stks.length; i++) {
      for (int j=0; j<stks[i].length; j++) {
        v.add(stks[i][j]);
      }
    }
    return (String[]) v.toArray(new String[0]);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    if (stks == null || stks[series].length == 1) {
      return super.openBytes(no, buf, x, y, w, h);
    }

    int[] coords = FormatTools.getZCTCoords(this, no % getSizeZ());
    int ndx = no / getSizeZ();
    String file = stks[series][ndx];

    // the original file is a .nd file, so we need to construct a new reader
    // for the constituent STK files
    if (stkReader == null) stkReader = new MetamorphReader();
    stkReader.setId(file);
    return stkReader.openBytes(coords[0], buf, x, y, w, h);
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    if (stkReader != null) stkReader.close();
    stkReader = null;
    imageName = imageCreationDate = null;
    emWavelength = null;
    stks = null;
    mmPlanes = 0;
    ndFilename = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (checkSuffix(id, ND_SUFFIX)) {
      // find an associated STK file
      String stkFile = id.substring(0, id.lastIndexOf("."));
      if (stkFile.indexOf(File.separator) != -1) {
        stkFile = stkFile.substring(stkFile.lastIndexOf(File.separator) + 1);
      }
      Location parent = new Location(id).getAbsoluteFile().getParentFile();
      String[] dirList = parent.list();
      for (int i=0; i<dirList.length; i++) {
        if (dirList[i].indexOf(stkFile) != -1 &&
          checkSuffix(dirList[i], STK_SUFFIX))
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

    if (checkSuffix(id, ND_SUFFIX)) ndfile = new Location(id);

    if (ndfile != null && ndfile.exists() &&
      (fileGroupOption(id) == FormatTools.MUST_GROUP || isGroupFiles()))
    {
      // parse key/value pairs from .nd file

      ndFilename = ndfile.getAbsolutePath();

      RandomAccessStream ndStream = new RandomAccessStream(ndFilename);
      String line = ndStream.readLine().trim();

      int zc = getSizeZ(), cc = getSizeC(), tc = getSizeT();
      String z = null, c = null, t = null;
      Vector hasZ = new Vector();
      Vector waveNames = new Vector();

      while (!line.equals("\"EndFile\"")) {
        String key = line.substring(1, line.indexOf(",") - 1).trim();
        String value = line.substring(line.indexOf(",") + 1).trim();

        addMeta(key, value);
        if (key.equals("NZSteps")) z = value;
        else if (key.equals("NWavelengths")) c = value;
        else if (key.equals("NTimePoints")) t = value;
        else if (key.startsWith("WaveDoZ")) {
          hasZ.add(new Boolean(value.toLowerCase()));
        }
        else if (key.startsWith("WaveName")) {
          waveNames.add(value);
        }

        line = ndStream.readLine().trim();
      }

      // figure out how many files we need

      if (z != null) zc = Integer.parseInt(z);
      if (c != null) cc = Integer.parseInt(c);
      if (t != null) tc = Integer.parseInt(t);

      int numFiles = cc * tc;

      // determine series count

      int seriesCount = 1;
      for (int i=0; i<cc; i++) {
        boolean hasZ1 = ((Boolean) hasZ.get(i)).booleanValue();
        boolean hasZ2 = i != 0 && ((Boolean) hasZ.get(i - 1)).booleanValue();
        if (i > 0 && hasZ1 != hasZ2) seriesCount = 2;
      }

      int channelsInFirstSeries = cc;
      if (seriesCount == 2) {
        channelsInFirstSeries = 0;
        for (int i=0; i<cc; i++) {
          if (((Boolean) hasZ.get(i)).booleanValue()) channelsInFirstSeries++;
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

      for (int i=0; i<cc; i++) {
        if (waveNames.get(i) != null) {
          String name = (String) waveNames.get(i);
          waveNames.setElementAt(name.substring(1, name.length() - 1), i);
        }
      }

      // build list of STK files

      int[] pt = new int[seriesCount];
      for (int i=0; i<tc; i++) {
        for (int j=0; j<cc; j++) {
          boolean validZ = ((Boolean) hasZ.get(j)).booleanValue();
          int seriesNdx = (seriesCount == 1 || validZ) ? 0 : 1;
          stks[seriesNdx][pt[seriesNdx]] = prefix;
          if (waveNames.get(j) != null) {
            stks[seriesNdx][pt[seriesNdx]] += "_w" + (j + 1) + waveNames.get(j);
          }
          stks[seriesNdx][pt[seriesNdx]] += "_t" + (i + 1) + ".STK";
          pt[seriesNdx]++;
        }
      }

      ndfile = ndfile.getAbsoluteFile();

      // check that each STK file exists

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

      core[0].sizeZ = zc;
      core[0].sizeC = cc;
      core[0].sizeT = tc;
      core[0].imageCount = zc * tc * cc;
      core[0].dimensionOrder = "XYZCT";

      if (stks.length > 1) {
        CoreMetadata[] newCore = new CoreMetadata[stks.length];
        for (int i=0; i<stks.length; i++) {
          newCore[i] = new CoreMetadata();
          newCore[i].sizeX = getSizeX();
          newCore[i].sizeY = getSizeY();
          newCore[i].sizeZ = getSizeZ();
          newCore[i].sizeC = getSizeC();
          newCore[i].sizeT = getSizeT();
          newCore[i].pixelType = getPixelType();
          newCore[i].imageCount = getImageCount();
          newCore[i].dimensionOrder = getDimensionOrder();
          newCore[i].rgb = isRGB();
          newCore[i].littleEndian = isLittleEndian();
          newCore[i].interleaved = isInterleaved();
          newCore[i].orderCertain = true;
        }
        newCore[0].sizeC = stks[0].length / newCore[0].sizeT;
        newCore[1].sizeC = stks[1].length / newCore[1].sizeT;
        newCore[1].sizeZ = 1;
        newCore[0].imageCount =
          newCore[0].sizeC * newCore[0].sizeT * newCore[0].sizeZ;
        newCore[1].imageCount = newCore[1].sizeC * newCore[1].sizeT;
        core = newCore;
      }
    }
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);
    for (int i=0; i<getSeriesCount(); i++) {
      MetadataTools.setDefaultCreationDate(store, id, i);
      store.setImageName("", i);
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
      core[0].imageCount = uic2.length;

      long[] uic3 = TiffTools.getIFDLongArray(ifds[0], UIC3TAG, true);
      for (int i=0; i<uic3.length; i++) {
        in.seek(uic3[i]);
        put("Wavelength [" + intFormatMax(i, mmPlanes) + "]",
          in.readLong() / in.readLong());
      }

      Hashtable[] tempIFDs = new Hashtable[getImageCount()];

      long[] oldOffsets = TiffTools.getStripOffsets(ifds[0]);
      long[] stripByteCounts = TiffTools.getStripByteCounts(ifds[0]);
      int rowsPerStrip = (int) TiffTools.getRowsPerStrip(ifds[0])[0];
      int stripsPerImage = getSizeY() / rowsPerStrip;
      if (stripsPerImage * rowsPerStrip != getSizeY()) stripsPerImage++;

      int check = TiffTools.getPhotometricInterpretation(ifds[0]);
      if (check == TiffTools.RGB_PALETTE) {
        TiffTools.putIFDValue(ifds[0], TiffTools.PHOTOMETRIC_INTERPRETATION,
          TiffTools.BLACK_IS_ZERO);
      }

      emWavelength = TiffTools.getIFDLongArray(ifds[0], UIC3TAG, true);

      // for each image plane, construct an IFD hashtable

      int pointer = 0;

      Hashtable temp;
      for (int i=0; i<getImageCount(); i++) {
        // copy data from the first IFD
        temp = new Hashtable(ifds[0]);

        // now we need a StripOffsets entry - the original IFD doesn't have this

        long[] newOffsets = new long[stripsPerImage];
        if (stripsPerImage * i < oldOffsets.length) {
          System.arraycopy(oldOffsets, stripsPerImage * i, newOffsets, 0,
            stripsPerImage);
        }
        else {
          for (int q=0; q<stripsPerImage; q++) {
            newOffsets[q] =
              oldOffsets[stripsPerImage - 1] + q*stripByteCounts[0];
          }
        }

        temp.put(new Integer(TiffTools.STRIP_OFFSETS), newOffsets);

        long[] newByteCounts = new long[stripsPerImage];
        if (stripsPerImage * i < stripByteCounts.length) {
          System.arraycopy(stripByteCounts, stripsPerImage * i, newByteCounts,
            0, stripsPerImage);
        }
        else {
          Arrays.fill(newByteCounts, stripByteCounts[0]);
        }
        temp.put(new Integer(TiffTools.STRIP_BYTE_COUNTS), newByteCounts);

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
    String descr = TiffTools.getComment(ifds[0]);
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
      if (getSizeZ() == 0) {
        core[0].sizeZ =
          TiffTools.getIFDLongArray(ifds[0], UIC2TAG, true).length;
      }
      if (getSizeT() == 0) core[0].sizeT = getImageCount() / getSizeZ();
    }
    catch (FormatException exc) {
      if (debug) trace(exc);
    }
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
    boolean end = false;
    short id;
    while (!end) {
      id = in.readShort();

      switch (id) {
        case 0:
          end = true;
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
    for (int i=0; i<uic1count; i++) {
      currentID = in.readInt();
      valOrOffset = in.readInt();
      lastOffset = in.getFilePointer();

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
          put("XCalibration", readRational(in, valOrOffset));
          in.seek(lastOffset);
          break;
        case 5:
          put("YCalibration", readRational(in, valOrOffset));
          in.seek(lastOffset);
          break;
        case 6:
          in.seek(valOrOffset);
          num = in.readInt();
          put("CalibrationUnits", in.readString(num));
          in.seek(lastOffset);
          break;
        case 7:
          in.seek(valOrOffset);
          num = in.readInt();
          imageName = in.readString(num);
          put("Name", imageName);
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
          put("Zoom", valOrOffset);
          break;
        case 16: // oh how we hate you Julian format...
          in.seek(valOrOffset);
          thedate = decodeDate(in.readInt());
          thetime = decodeTime(in.readInt());
          imageCreationDate = thedate + " " + thetime;
          put("DateTime", imageCreationDate);
          in.seek(lastOffset);
          break;
        case 17:
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
          put("grayX", readRational(in, valOrOffset));
          in.seek(lastOffset);
          break;
        case 22:
          put("gray", readRational(in, valOrOffset));
          in.seek(lastOffset);
          break;
        case 23:
          put("grayMin", readRational(in, valOrOffset));
          in.seek(lastOffset);
          break;
        case 24:
          put("grayMax", readRational(in, valOrOffset));
          in.seek(lastOffset);
          break;
        case 25:
          in.seek(valOrOffset);
          num = in.readInt();
          put("grayUnitName", in.readString(num));
          in.seek(lastOffset);
          break;
        case 26:
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
          put("AutoScaleLoInfo", readRational(in, valOrOffset));
          in.seek(lastOffset);
          break;
        case 39:
          put("AutoScaleHiInfo", readRational(in, valOrOffset));
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
          in.seek(valOrOffset);
          int xBin = in.readInt();
          int yBin = in.readInt();
          put("CameraBin", new String("(" + xBin + "," + yBin + ")"));
          in.seek(lastOffset);
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
    int ms = millis % 1000;
    millis -= ms;
    millis /= 1000;
    int seconds = millis % 60;
    millis -= seconds;
    millis /= 60;
    int minutes = millis % 60;
    millis -= minutes;
    millis /= 60;
    int hours = millis;
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

  private TiffRational readRational(RandomAccessStream s, long offset)
    throws IOException
  {
    s.seek(offset);
    int num = s.readInt();
    int denom = s.readInt();
    return new TiffRational(num, denom);
  }

}
