//
// ZeissZVIReader.java
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
import java.util.*;
import loci.formats.*;
import loci.formats.codec.JPEGCodec;

/**
 * ZeissZVIReader is the file format reader for Zeiss ZVI files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/ZeissZVIReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/ZeissZVIReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class ZeissZVIReader extends FormatReader {

  // -- Constants --

  private static final String NO_POI_MSG =
    "Jakarta POI is required to read ZVI files. Please " +
    "obtain poi-loci.jar from http://loci.wisc.edu/ome/formats.html";

  // -- Static fields --

  private LegacyZVIReader legacy = new LegacyZVIReader();
  private static boolean noPOI = false;
  private static ReflectedUniverse r = createReflectedUniverse();

  private static ReflectedUniverse createReflectedUniverse() {
    r = null;
    try {
      r = new ReflectedUniverse();
      r.exec("import org.apache.poi.poifs.filesystem.POIFSFileSystem");
      r.exec("import org.apache.poi.poifs.filesystem.DirectoryEntry");
      r.exec("import org.apache.poi.poifs.filesystem.DocumentEntry");
      r.exec("import org.apache.poi.poifs.filesystem.DocumentInputStream");
      r.exec("import java.util.Iterator");
    }
    catch (ReflectException exc) {
      noPOI = true;
      if (debug) LogTools.trace(exc);
    }
    return r;
  }

  // -- Fields --

  /** Flag set to true if we need to use the legacy reader. */
  private boolean needLegacy = false;

  /** Number of bytes per pixel. */
  private int bpp;

  /** Hashtable containing the directory entry for each plane. */
  private Hashtable pixels;

  /**
   * Hashtable containing the document name for each plane,
   * indexed by the plane number.
   */
  private Hashtable names;

  /** Vector containing Z indices. */
  private Vector zIndices;

  /** Vector containing C indices. */
  private Vector cIndices;

  /** Vector containing T indices. */
  private Vector tIndices;

  private Hashtable offsets;

  private int zIndex = -1, cIndex = -1, tIndex = -1;

  private boolean isTiled;
  private int tileRows, tileColumns;
  private boolean isJPEG;

  // -- Constructor --

  /** Constructs a new ZeissZVI reader. */
  public ZeissZVIReader() { super("Zeiss Vision Image (ZVI)", "zvi"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    // all of our samples begin with 0xd0cf11e0
    return (block[0] == 0xd0 && block[1] == 0xcf &&
      block[2] == 0x11 && block[3] == 0xe0);
  }

  /* @see loci.formats.IFormatReader#setMetadataStore(MetadataStore) */
  public void setMetadataStore(MetadataStore store) {
    FormatTools.assertId(currentId, false, 1);
    super.setMetadataStore(store);
    if (noPOI || needLegacy) legacy.setMetadataStore(store);
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    if (noPOI || needLegacy) return legacy.openBytes(no, buf);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    try {
      int tiles = tileRows * tileColumns;
      if (tiles == 0) {
        tiles = 1;
        tileRows = 1;
        tileColumns = 1;
      }
      int start = no * tiles;

      int bytes =
        FormatTools.getBytesPerPixel(core.pixelType[0]) * getRGBChannelCount();
      int ex = core.sizeX[0] / tileColumns;
      int ey = core.sizeY[0] / tileRows;
      int row = ex * bytes;

      int[] tileOrder = new int[tiles];
      if (tiles == 1) tileOrder[0] = no;
      else {
        int p = 0;
        for (int r=0; r<tileRows; r++) {
          for (int c=0; c<tileColumns; c++) {
            int n = (no % core.sizeC[0]) +
              (tiles * core.sizeC[0] * (no / core.sizeC[0]));
            if (r % 2 == 0) {
              tileOrder[p] = p*core.sizeC[0] + n;
            }
            else {
              tileOrder[p] = (tileColumns - c)*core.sizeC[0];
              if (p > 0 && c == 0) tileOrder[p] += tileOrder[p - 1];
              else if (c > 0) tileOrder[p] = tileOrder[p - 1] - core.sizeC[0];
            }
            p++;
          }
        }
      }

      for (int i=start; i<start+tiles; i++) {
        Integer ii = new Integer(tileOrder[i - start]);
        Object directory = pixels.get(ii);
        String name = (String) names.get(ii);

        r.setVar("dir", directory);
        r.setVar("entryName", name);
        r.exec("document = dir.getEntry(entryName)");
        r.exec("dis = new DocumentInputStream(document)");
        r.exec("numBytes = dis.available()");
        r.setVar("skipBytes", ((Integer) offsets.get(ii)).longValue());
        r.exec("blah = dis.skip(skipBytes)");
        r.setVar("data", buf);

        int xf = ((i - start) % tileColumns) * ex * bytes;
        int yf = ((i - start) / tileRows) * ey;
        int offset = yf*core.sizeX[0]*bytes + xf;
        for (int y=0; y<ey; y++) {
          r.setVar("offset", offset);
          r.setVar("len", row);
          try {
            r.exec("dis.read(data, offset, len)");
          }
          catch (ReflectException e) { }
          offset += core.sizeX[0]*bytes;
        }

        if (isJPEG) {
          JPEGCodec codec = new JPEGCodec();
          buf = codec.decompress(buf);
        }
      }

      if (bpp > 6) bpp = 1;
      if (bpp == 3) {
        // reverse bytes in groups of 3 to account for BGR storage
        for (int i=0; i<buf.length; i+=3) {
          byte b = buf[i + 2];
          buf[i + 2] = buf[i];
          buf[i] = b;
        }
      }
      return buf;
    }
    catch (ReflectException e) {
      needLegacy = true;
      return openBytes(no, buf);
    }
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    if (fileOnly) {
      if (in != null) in.close();
      if (legacy != null) legacy.close(fileOnly);
    }
    else close();
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    needLegacy = false;

    if (legacy != null) legacy.close();
    pixels = null;
    names = null;
    offsets = null;

    String[] vars = {"dirName", "root", "dir", "document", "dis",
      "numBytes", "data", "fis", "fs", "iter", "isInstance", "isDocument",
      "entry", "documentName", "entryName"};
    for (int i=0; i<vars.length; i++) r.setVar(vars[i], null);
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("ZeissZVIReader.initFile(" + id + ")");
    if (noPOI || needLegacy) {
      legacy.setId(id);
      core = legacy.getCoreMetadata();
      return;
    }
    super.initFile(id);

    pixels = new Hashtable();
    names = new Hashtable();
    offsets = new Hashtable();
    zIndices = new Vector();
    cIndices = new Vector();
    tIndices = new Vector();

    try {
      in = new RandomAccessStream(id);

      // Don't uncomment this block.  Even though OIBReader has something
      // like this, it's really a bad idea here.  Every ZVI file we have *will*
      // break if you uncomment it.
      //
      //if (in.length() % 4096 != 0) {
      //  in.setExtend((4096 - (int) (in.length() % 4096)));
      //}

      r.setVar("fis", in);
      r.exec("fs = new POIFSFileSystem(fis)");
      r.exec("dir = fs.getRoot()");
      parseDir(0, r.getVar("dir"));

      status("Populating metadata");

      core.rgb[0] = core.sizeC[0] > 1 &&
        (core.sizeZ[0] * core.sizeC[0] * core.sizeT[0] != core.imageCount[0]);
      core.littleEndian[0] = true;
      core.interleaved[0] = !isJPEG;
      core.indexed[0] = false;
      core.falseColor[0] = false;
      core.metadataComplete[0] = true;

      core.sizeZ[0] = zIndices.size();
      core.sizeT[0] = tIndices.size();

      if (core.sizeC[0] != cIndices.size()) core.sizeC[0] *= cIndices.size();

      core.imageCount[0] = core.sizeZ[0] * core.sizeT[0] *
        (core.rgb[0] ? core.sizeC[0] / 3 : core.sizeC[0]);

      if (isTiled) {
        String zeroIndex = (String) getMeta("ImageTile Index 0");
        String oneIndex = (String) getMeta("ImageTile Index 1");
        if (zeroIndex == null || zeroIndex.equals("")) zeroIndex = null;
        if (oneIndex == null || oneIndex.equals("")) oneIndex = null;
        if (zeroIndex == null || oneIndex == null) {
          isTiled = false;
        }
        else {
          int lowerLeft = Integer.parseInt(zeroIndex);
          int middle = Integer.parseInt(oneIndex);

          tileColumns = lowerLeft - middle - 1;
          tileRows = (lowerLeft / tileColumns) + 1;
          if (tileColumns < 0) tileColumns = 1;
          if (tileRows < 0) tileRows = 1;
          core.sizeX[0] *= tileColumns;
          core.sizeY[0] *= tileRows;
          if (tileColumns == 1 && tileRows == 1) isTiled = false;
        }
      }

      if (cIndex != -1) {
        int[] dims = {core.sizeZ[0], core.sizeC[0], core.sizeT[0]};
        int max = 0, min = Integer.MAX_VALUE, maxNdx = 0, minNdx = 0;
        String[] axes = {"Z", "C", "T"};

        for (int i=0; i<dims.length; i++) {
          if (dims[i] > max) {
            max = dims[i];
            maxNdx = i;
          }
          if (dims[i] < min) {
            min = dims[i];
            minNdx = i;
          }
        }

        int medNdx = 0;
        for (int i=0; i<3; i++) {
          if (i != maxNdx && i != minNdx) medNdx = i;
        }

        core.currentOrder[0] =
          "XY" + axes[maxNdx] + axes[medNdx] + axes[minNdx];

        int num = core.sizeZ[0] * core.sizeT[0] - core.sizeC[0];
        if ((zIndex != -1 && tIndex != -1) && (zIndex != num && tIndex != num))
        {
          if (zIndex != core.sizeZ[0]) {
            if (core.sizeZ[0] != 1) {
              core.currentOrder[0] =
                core.currentOrder[0].replaceAll("Z", "") + "Z";
            }
            else {
              core.currentOrder[0] =
                core.currentOrder[0].replaceAll("T", "") + "T";
            }
          }
        }

        if (core.sizeZ[0] == core.sizeC[0] && core.sizeC[0] == core.sizeT[0]) {
          legacy.setId(id);
          core.currentOrder[0] = legacy.getDimensionOrder();
        }
      }
      else if (core.rgb[0]) {
        core.currentOrder[0] =
          (core.sizeZ[0] > core.sizeT[0]) ? "XYCZT" : "XYCTZ";
      }
      else {
        if (metadata.get("MultiChannelEnabled") != null ||
          metadata.get("MultiChannelEnabled 0") != null)
        {
          core.currentOrder[0] =
            (core.sizeZ[0] > core.sizeT[0]) ? "XYCZT" : "XYCTZ";
        }
        else {
          core.currentOrder[0] =
            (core.sizeZ[0] > core.sizeT[0]) ? "XYZTC" : "XYTZC";
        }
      }
    }
    catch (ReflectException exc) {
      needLegacy = true;
      if (debug) trace(exc);
      initFile(id);
    }

    // rearrange axis sizes, if necessary

    int lastZ = zIndices.size() == 0 ? Integer.MAX_VALUE :
      ((Integer) zIndices.get(zIndices.size() - 1)).intValue();
    int lastT = tIndices.size() == 0 ? Integer.MAX_VALUE :
      ((Integer) tIndices.get(tIndices.size() - 1)).intValue();

    if ((zIndex > lastZ || tIndex > lastT) && (zIndex == core.sizeC[0] - 1 ||
      tIndex == core.sizeC[0] - 1 ||
      (zIndex != 0 && zIndex % core.sizeC[0] == 0) ||
      (tIndex != 0 && tIndex % core.sizeC[0] == 0)) && zIndex != lastT)
    {
      if (zIndex >= core.sizeZ[0] || tIndex >= core.sizeT[0]) {
        int tmp = core.sizeZ[0];
        core.sizeZ[0] = core.sizeT[0];
        core.sizeT[0] = tmp;
      }
    }

    // correct emission/excitation wavelengths, if necessary

    if (metadata.size() > 0) {
      // HACK
      String lastEM =
        (String) getMeta("Emission Wavelength " + (core.sizeC[0] - 1));
      String nextToLastEM =
        (String) getMeta("Emission Wavelength " + (core.sizeC[0] - 2));
      if (lastEM == null || nextToLastEM == null ||
        lastEM.equals(nextToLastEM))
      {
        String lastDye = (String) getMeta("Reflector " + (core.sizeC[0] - 1));
        String nextToLastDye =
          (String) getMeta("Reflector " + (core.sizeC[0] - 2));
        if (lastDye == null) lastDye = "";
        if (nextToLastDye == null) nextToLastDye = "";

        lastDye = DataTools.stripString(lastDye);
        nextToLastDye = DataTools.stripString(nextToLastDye);

        if (nextToLastDye.indexOf("Rhodamine") != -1) {
          addMeta("Emission Wavelength " + (core.sizeC[0] - 2), "580");
          addMeta("Excitation Wavelength " + (core.sizeC[0] - 2), "540");
        }
        else if (nextToLastDye.indexOf("DAPI") != -1) {
          addMeta("Emission Wavelength " + (core.sizeC[0] - 2), "461");
          addMeta("Excitation Wavelength " + (core.sizeC[0] - 2), "359");
        }
        else if (nextToLastDye.startsWith("Alexa Fluor")) {
          addMeta("Emission Wavelength " + (core.sizeC[0] - 2), "519");
          addMeta("Excitation Wavelength " + (core.sizeC[0] - 2), "495");
        }
        else if (nextToLastDye.indexOf("Alexa Fluor") != -1) {
          addMeta("Emission Wavelength " + (core.sizeC[0] - 2), "668");
          addMeta("Excitation Wavelength " + (core.sizeC[0] - 2), "650");
        }

        if (lastDye.indexOf("Rhodamine") != -1) {
          addMeta("Emission Wavelength " + (core.sizeC[0] - 1), "580");
          addMeta("Excitation Wavelength " + (core.sizeC[0] - 1), "540");
        }
        else if (lastDye.indexOf("DAPI") != -1) {
          addMeta("Emission Wavelength " + (core.sizeC[0] - 1), "461");
          addMeta("Excitation Wavelength " + (core.sizeC[0] - 1), "359");
        }
        else if (lastDye.startsWith("Alexa Fluor")) {
          addMeta("Emission Wavelength " + (core.sizeC[0] - 1), "519");
          addMeta("Excitation Wavelength " + (core.sizeC[0] - 1), "495");
        }
        else if (lastDye.indexOf("Alexa Fluor") != -1) {
          addMeta("Emission Wavelength " + (core.sizeC[0] - 1), "668");
          addMeta("Excitation Wavelength " + (core.sizeC[0] - 1), "650");
        }
      }
    }

    try {
      initMetadata();
    }
    catch (FormatException exc) {
      if (debug) trace(exc);
    }
    catch (IOException exc) {
      if (debug) trace(exc);
    }

    // remove extra (invalid) metadata

    String[] keys = (String[]) metadata.keySet().toArray(new String[0]);
    for (int i=0; i<keys.length; i++) {
      String n = keys[i];
      if (n.indexOf(" ") != -1) {
        n = n.substring(n.lastIndexOf(" ") + 1);
        try {
          int ndx = Integer.parseInt(n);
          if (ndx >= core.sizeC[0]) metadata.remove(keys[i]);
        }
        catch (NumberFormatException e) { }
      }
    }
  }

  // -- Helper methods --

  /** Initialize metadata hashtable and OME-XML structure. */
  private void initMetadata() throws FormatException, IOException {
    MetadataStore store = getMetadataStore();

    String fname = (String) getMeta("Title");
    if (fname == null) fname = currentId;
    store.setImage(fname, null, null, null);

    if (bpp == 1 || bpp == 3) core.pixelType[0] = FormatTools.UINT8;
    else if (bpp == 2 || bpp == 6) core.pixelType[0] = FormatTools.UINT16;

    FormatTools.populatePixels(store, this);

    String pixX = (String) getMeta("Scale Factor for X");
    String pixY = (String) getMeta("Scale Factor for Y");
    String pixZ = (String) getMeta("Scale Factor for Z");

    store.setDimensions(
      pixX == null ? null : new Float(pixX),
      pixY == null ? null : new Float(pixY),
      pixZ == null ? null : new Float(pixZ),
      null, null, null);

    String scopeName = (String) getMeta("Microscope Name");
    if (scopeName == null) scopeName = (String) getMeta("Microscope Name 0");
    store.setInstrument(null, scopeName, null, null, null);

    for (int i=0; i<core.sizeC[0]; i++) {
      int idx = FormatTools.getIndex(this, 0, i % getEffectiveSizeC(), 0);
      String emWave = (String) getMeta("Emission Wavelength " + idx);
      String exWave = (String) getMeta("Excitation Wavelength " + idx);

      if (emWave != null && emWave.indexOf(".") != -1) {
        emWave = emWave.substring(0, emWave.indexOf("."));
      }
      if (exWave != null && exWave.indexOf(".") != -1) {
        exWave = exWave.substring(0, exWave.indexOf("."));
      }
      store.setLogicalChannel(i, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null,
        emWave == null ? null : new Integer(emWave),
        exWave == null ? null : new Integer(exWave), null, null, null);

      String black = (String) getMeta("BlackValue " + idx);
      String white = (String) getMeta("WhiteValue " + idx);
      String gamma = (String) getMeta("GammaValue " + idx);

      Double blackValue = null, whiteValue = null;
      Float gammaValue = null;

      try { blackValue = new Double(black); }
      catch (NumberFormatException e) { }
      catch (NullPointerException e) { }
      try { whiteValue = new Double(white); }
      catch (NumberFormatException e) { }
      catch (NullPointerException e) { }
      try { gammaValue = new Float(gamma); }
      catch (NumberFormatException e) { }
      catch (NullPointerException e) { }

      store.setDisplayChannel(new Integer(i), blackValue, whiteValue,
        gammaValue, null);
    }

    for (int i=0; i<core.imageCount[0]; i++) {
      int[] zct = FormatTools.getZCTCoords(this, i);
      String exposure = (String) getMeta("Exposure Time [ms] " + i);
      Float exp = new Float(0.0);
      try { exp = new Float(exposure); }
      catch (NumberFormatException e) { }
      catch (NullPointerException e) { }
      store.setPlaneInfo(zct[0], zct[1], zct[2], new Float(0.0), exp, null);
    }

    String objectiveName = (String) getMeta("Objective Name 0");
    if (objectiveName != null) {
      objectiveName = DataTools.stripString(objectiveName);
    }

    String objectiveMag = (String) getMeta("Objective Magnification 0");
    if (objectiveMag != null) {
      objectiveMag = DataTools.stripString(objectiveMag);
    }
    else objectiveMag = "1.0";

    String objectiveNA = (String) getMeta("Objective N.A. 0");
    if (objectiveNA != null) objectiveNA = DataTools.stripString(objectiveNA);
    else objectiveNA = "1.0";

    store.setObjective(null, objectiveName, null, new Float(objectiveNA),
      new Float(objectiveMag), null, null);
  }

  protected void parseDir(int depth, Object dir)
    throws IOException, FormatException, ReflectException
  {
    r.setVar("dir", dir);
    r.exec("dirName = dir.getName()");
    r.setVar("depth", depth);
    r.exec("iter = dir.getEntries()");
    Iterator iter = (Iterator) r.getVar("iter");
    while (iter.hasNext()) {
      r.setVar("entry", iter.next());
      r.exec("isInstance = entry.isDirectoryEntry()");
      r.exec("isDocument = entry.isDocumentEntry()");
      boolean isInstance = ((Boolean) r.getVar("isInstance")).booleanValue();
      boolean isDocument = ((Boolean) r.getVar("isDocument")).booleanValue();
      r.setVar("dir", dir);
      r.exec("dirName = dir.getName()");

      if (isInstance)  {
        status("Parsing embedded folder (" + (depth + 1) + ")");
        parseDir(depth + 1, r.getVar("entry"));
      }
      else if (isDocument) {
        status("Parsing embedded file (" + depth + ")");
        r.exec("entryName = entry.getName()");
        if (debug) {
          print(depth + 1, "Found document: " + r.getVar("entryName"));
        }
        r.exec("dis = new DocumentInputStream(entry)");
        r.exec("numBytes = dis.available()");
        int numbytes = ((Integer) r.getVar("numBytes")).intValue();
        byte[] data = new byte[numbytes + 4]; // append 0 for final offset
        r.setVar("data", data);

        // Suppressing an exception here looks like poor style.
        // However, this at least gives us a chance at reading files with
        // corrupt blocks.
        try {
          r.exec("dis.read(data)");
        }
        catch (ReflectException exc) {
          if (debug) trace(exc);
        }

        String entryName = (String) r.getVar("entryName");
        String dirName = (String) r.getVar("dirName");

        boolean isContents = entryName.toUpperCase().equals("CONTENTS");
        Object directory = r.getVar("dir");

        RandomAccessStream s = new RandomAccessStream(data);
        s.order(true);

        if (dirName.toUpperCase().equals("ROOT ENTRY") ||
          dirName.toUpperCase().equals("ROOTENTRY"))
        {
          if (entryName.equals("Tags")) {
            try { parseTags(s); }
            catch (EOFException e) { }
          }
        }
        else if (dirName.equals("Tags") && isContents) {
          try { parseTags(s); }
          catch (EOFException e) { }
        }
        else if (isContents && (dirName.equals("Image") ||
          dirName.toUpperCase().indexOf("ITEM") != -1) &&
          (data.length > core.sizeX[0]*core.sizeY[0]))
        {
          s.skipBytes(6);

          int vt = s.readShort();
          if (vt == 3) {
            s.skipBytes(6);
          }
          else if (vt == 8) {
            int l = s.readShort();
            s.skipBytes(l + 2);
          }
          int len = s.readShort();
          if (s.readShort() != 0) s.seek(s.getFilePointer() - 2);

          if (s.getFilePointer() + len <= s.length()) {
            s.skipBytes(len);
          }
          else break;

          vt = s.readShort();
          if (vt == 8) {
            len = s.readInt();
            s.skipBytes(len + 2);
          }

          int tw = s.readInt();
          if (core.sizeX[0] == 0 || (tw < core.sizeX[0] && tw > 0)) {
            core.sizeX[0] = tw;
          }
          s.skipBytes(2);
          int th = s.readInt();
          if (core.sizeY[0] == 0 || (th < core.sizeY[0] && th > 0)) {
            core.sizeY[0] = th;
          }

          s.skipBytes(14);

          int numImageContainers = s.readInt();
          s.skipBytes(6);

          // VT_CLSID - PluginCLSID
          while (s.readShort() != 65);

          // VT_BLOB - Others
          len = s.readInt();
          s.skipBytes(len);

          // VT_STORED_OBJECT - Layers
          s.skipBytes(2);
          long old = s.getFilePointer();
          len = s.readInt();

          s.skipBytes(8);

          int tidx = s.readInt();
          int cidx = s.readInt();
          int zidx = s.readInt();

          Integer zndx = new Integer(zidx);
          Integer cndx = new Integer(cidx);
          Integer tndx = new Integer(tidx);

          if (!zIndices.contains(zndx)) zIndices.add(zndx);
          if (!cIndices.contains(cndx)) cIndices.add(cndx);
          if (!tIndices.contains(tndx)) tIndices.add(tndx);

          s.seek(old + len + 4);

          boolean foundWidth = s.readInt() == core.sizeX[0];
          boolean foundHeight = s.readInt() == core.sizeY[0];
          boolean findFailed = false;
          while ((!foundWidth || !foundHeight) &&
            s.getFilePointer() + 1 < s.length())
          {
            s.seek(s.getFilePointer() - 7);
            foundWidth = s.readInt() == core.sizeX[0];
            foundHeight = s.readInt() == core.sizeY[0];
          }
          s.seek(s.getFilePointer() - 16);
          findFailed = !foundWidth && !foundHeight;

          // image header and data

          if (dirName.toUpperCase().indexOf("ITEM") != -1 ||
            (dirName.equals("Image") && numImageContainers == 0))
          {
            if (findFailed) s.seek(old + len + 92);
            long fp = s.getFilePointer();
            byte[] o = new byte[(int) (s.length() - fp)];
            s.read(o);

            int imageNum = 0;
            if (dirName.toUpperCase().indexOf("ITEM") != -1) {
              String num = dirName.substring(5);
              num = num.substring(0, num.length() - 1);
              imageNum = Integer.parseInt(num);
            }

            offsets.put(new Integer(imageNum), new Integer((int) fp + 32));
            parsePlane(o, imageNum, directory, entryName);
          }
        }
        else {
          try { parseTags(s); }
          catch (IOException e) { }
        }

        s.close();
        data = null;
        r.exec("dis.close()");
      }
    }
  }

  /** Debugging helper method. */
  protected void print(int depth, String s) {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<depth; i++) sb.append("  ");
    sb.append(s);
    debug(sb.toString());
  }

  /** Parse a plane of data. */
  private void parsePlane(byte[] data, int num, Object directory, String entry)
    throws IOException
  {
    RandomAccessStream s = new RandomAccessStream(data);
    s.order(true);

    if (s.readInt() == 0) s.skipBytes(4);

    core.sizeX[0] = s.readInt();
    core.sizeY[0] = s.readInt();
    s.skipBytes(4);
    bpp = s.readInt();
    //s.skipBytes(8);
    s.skipBytes(4);
    int valid = s.readInt();
    isJPEG = valid == 0 || valid == 1;

    pixels.put(new Integer(num), directory);
    names.put(new Integer(num), entry);
    core.imageCount[0]++;
    if (bpp % 3 == 0) core.sizeC[0] = 3;
    else core.sizeC[0] = 1;
  }

  /** Parse all of the tags in a stream. */
  private void parseTags(RandomAccessStream s) throws IOException {
    s.skipBytes(24);

    int count = s.readInt();

    // limit count to 4096
    if (count > 4096) count = 4096;

    for (int i=0; i<count; i++) {
      if (s.getFilePointer() + 2 >= s.length()) break;
      int type = s.readShort();

      String value = "";
      switch (type) {
        case 0:
          break;
        case 1:
          break;
        case 2:
          value = "" + s.readShort();
          break;
        case 3:
        case 22:
        case 23:
          value = "" + s.readInt();
          break;
        case 4:
          value = "" + s.readFloat();
          break;
        case 5:
          value = "" + s.readDouble();
          break;
        case 7:
        case 20:
        case 21:
          value = "" + s.readLong();
          break;
        case 69:
        case 8:
          int len = s.readInt();
          if (s.getFilePointer() + len < s.length()) {
            value = s.readString(len);
          }
          else return;
          break;
        case 66:
          int l = s.readShort();
          s.seek(s.getFilePointer() - 2);
          value = s.readString(l + 2);
          break;
        default:
          long old = s.getFilePointer();
          while (s.readShort() != 3 &&
            s.getFilePointer() + 2 < s.length());
          long fp = s.getFilePointer() - 2;
          s.seek(old - 2);
          value = s.readString((int) (fp - old + 2));
      }

      s.skipBytes(2);
      int tagID = 0;

      try { tagID = s.readInt(); }
      catch (IOException e) { }

      s.skipBytes(6);

      String key = getKey(tagID);
      if (key.equals("Image Index Z")) {
        try {
          zIndex = Integer.parseInt(DataTools.stripString(value));
        }
        catch (NumberFormatException f) { }
      }
      else if (key.equals("Image Index T")) {
        try {
          tIndex = Integer.parseInt(DataTools.stripString(value));
        }
        catch (NumberFormatException f) { }
      }
      else if (key.equals("Image Channel Index")) {
        try {
          cIndex = Integer.parseInt(DataTools.stripString(value));
        }
        catch (NumberFormatException f) { }
      }
      else if (key.equals("ImageWidth")) {
        try {
          if (core.sizeX[0] == 0) core.sizeX[0] = Integer.parseInt(value);
        }
        catch (NumberFormatException f) { }
      }
      else if (key.equals("ImageHeight")) {
        try {
          if (core.sizeY[0] == 0) core.sizeY[0] = Integer.parseInt(value);
        }
        catch (NumberFormatException f) { }
      }

      if (metadata.get(key) != null || metadata.get(key + " 0") != null) {
        if (metadata.get(key) != null) {
          Object v = metadata.remove(key);
          metadata.put(key + " 0", v);
        }

        int ndx = 0;
        while (metadata.get(key + " " + ndx) != null) ndx++;
        key += " " + ndx;
      }

      if (key.indexOf("ImageTile") != -1) isTiled = true;
      addMeta(key, value);
    }
  }

  /** Return the string corresponding to the given ID. */
  private String getKey(int tagID) {
    switch (tagID) {
      case 222: return "Compression";
      case 258: return "BlackValue";
      case 259: return "WhiteValue";
      case 260: return "ImageDataMappingAutoRange";
      case 261: return "Thumbnail";
      case 262: return "GammaValue";
      case 264: return "ImageOverExposure";
      case 265: return "ImageRelativeTime1";
      case 266: return "ImageRelativeTime2";
      case 267: return "ImageRelativeTime3";
      case 268: return "ImageRelativeTime4";
      case 333: return "RelFocusPosition1";
      case 334: return "RelFocusPosition2";
      case 513: return "ObjectType";
      case 515: return "ImageWidth";
      case 516: return "ImageHeight";
      case 517: return "Number Raw Count";
      case 518: return "PixelType";
      case 519: return "NumberOfRawImages";
      case 520: return "ImageSize";
      case 523: return "Acquisition pause annotation";
      case 530: return "Document Subtype";
      case 531: return "Acquisition Bit Depth";
      case 532: return "Image Memory Usage (RAM)";
      case 534: return "Z-Stack single representative";
      case 769: return "Scale Factor for X";
      case 770: return "Scale Unit for X";
      case 771: return "Scale Width";
      case 772: return "Scale Factor for Y";
      case 773: return "Scale Unit for Y";
      case 774: return "Scale Height";
      case 775: return "Scale Factor for Z";
      case 776: return "Scale Unit for Z";
      case 777: return "Scale Depth";
      case 778: return "Scaling Parent";
      case 1001: return "Date";
      case 1002: return "code";
      case 1003: return "Source";
      case 1004: return "Message";
      case 1025: return "Acquisition Date";
      case 1026: return "8-bit acquisition";
      case 1027: return "Camera Bit Depth";
      case 1029: return "MonoReferenceLow";
      case 1030: return "MonoReferenceHigh";
      case 1031: return "RedReferenceLow";
      case 1032: return "RedReferenceHigh";
      case 1033: return "GreenReferenceLow";
      case 1034: return "GreenReferenceHigh";
      case 1035: return "BlueReferenceLow";
      case 1036: return "BlueReferenceHigh";
      case 1041: return "FrameGrabber Name";
      case 1042: return "Camera";
      case 1044: return "CameraTriggerSignalType";
      case 1045: return "CameraTriggerEnable";
      case 1046: return "GrabberTimeout";
      case 1281: return "MultiChannelEnabled";
      case 1282: return "MultiChannel Color";
      case 1283: return "MultiChannel Weight";
      case 1284: return "Channel Name";
      case 1536: return "DocumentInformationGroup";
      case 1537: return "Title";
      case 1538: return "Author";
      case 1539: return "Keywords";
      case 1540: return "Comments";
      case 1541: return "SampleID";
      case 1542: return "Subject";
      case 1543: return "RevisionNumber";
      case 1544: return "Save Folder";
      case 1545: return "FileLink";
      case 1546: return "Document Type";
      case 1547: return "Storage Media";
      case 1548: return "File ID";
      case 1549: return "Reference";
      case 1550: return "File Date";
      case 1551: return "File Size";
      case 1553: return "Filename";
      case 1792: return "ProjectGroup";
      case 1793: return "Acquisition Date";
      case 1794: return "Last modified by";
      case 1795: return "User company";
      case 1796: return "User company logo";
      case 1797: return "Image";
      case 1800: return "User ID";
      case 1801: return "User Name";
      case 1802: return "User City";
      case 1803: return "User Address";
      case 1804: return "User Country";
      case 1805: return "User Phone";
      case 1806: return "User Fax";
      case 2049: return "Objective Name";
      case 2050: return "Optovar";
      case 2051: return "Reflector";
      case 2052: return "Condenser Contrast";
      case 2053: return "Transmitted Light Filter 1";
      case 2054: return "Transmitted Light Filter 2";
      case 2055: return "Reflected Light Shutter";
      case 2056: return "Condenser Front Lens";
      case 2057: return "Excitation Filter Name";
      case 2060: return "Transmitted Light Fieldstop Aperture";
      case 2061: return "Reflected Light Aperture";
      case 2062: return "Condenser N.A.";
      case 2063: return "Light Path";
      case 2064: return "HalogenLampOn";
      case 2065: return "Halogen Lamp Mode";
      case 2066: return "Halogen Lamp Voltage";
      case 2068: return "Fluorescence Lamp Level";
      case 2069: return "Fluorescence Lamp Intensity";
      case 2070: return "LightManagerEnabled";
      case 2071: return "tag_ID_2071";
      case 2072: return "Focus Position";
      case 2073: return "Stage Position X";
      case 2074: return "Stage Position Y";
      case 2075: return "Microscope Name";
      case 2076: return "Objective Magnification";
      case 2077: return "Objective N.A.";
      case 2078: return "MicroscopeIllumination";
      case 2079: return "External Shutter 1";
      case 2080: return "External Shutter 2";
      case 2081: return "External Shutter 3";
      case 2082: return "External Filter Wheel 1 Name";
      case 2083: return "External Filter Wheel 2 Name";
      case 2084: return "Parfocal Correction";
      case 2086: return "External Shutter 4";
      case 2087: return "External Shutter 5";
      case 2088: return "External Shutter 6";
      case 2089: return "External Filter Wheel 3 Name";
      case 2090: return "External Filter Wheel 4 Name";
      case 2103: return "Objective Turret Position";
      case 2104: return "Objective Contrast Method";
      case 2105: return "Objective Immersion Type";
      case 2107: return "Reflector Position";
      case 2109: return "Transmitted Light Filter 1 Position";
      case 2110: return "Transmitted Light Filter 2 Position";
      case 2112: return "Excitation Filter Position";
      case 2113: return "Lamp Mirror Position";
      case 2114: return "External Filter Wheel 1 Position";
      case 2115: return "External Filter Wheel 2 Position";
      case 2116: return "External Filter Wheel 3 Position";
      case 2117: return "External Filter Wheel 4 Position";
      case 2118: return "Lightmanager Mode";
      case 2119: return "Halogen Lamp Calibration";
      case 2120: return "CondenserNAGoSpeed";
      case 2121: return "TransmittedLightFieldstopGoSpeed";
      case 2122: return "OptovarGoSpeed";
      case 2123: return "Focus calibrated";
      case 2124: return "FocusBasicPosition";
      case 2125: return "FocusPower";
      case 2126: return "FocusBacklash";
      case 2127: return "FocusMeasurementOrigin";
      case 2128: return "FocusMeasurementDistance";
      case 2129: return "FocusSpeed";
      case 2130: return "FocusGoSpeed";
      case 2131: return "FocusDistance";
      case 2132: return "FocusInitPosition";
      case 2133: return "Stage calibrated";
      case 2134: return "StagePower";
      case 2135: return "StageXBacklash";
      case 2136: return "StageYBacklash";
      case 2137: return "StageSpeedX";
      case 2138: return "StageSpeedY";
      case 2139: return "StageSpeed";
      case 2140: return "StageGoSpeedX";
      case 2141: return "StageGoSpeedY";
      case 2142: return "StageStepDistanceX";
      case 2143: return "StageStepDistanceY";
      case 2144: return "StageInitialisationPositionX";
      case 2145: return "StageInitialisationPositionY";
      case 2146: return "MicroscopeMagnification";
      case 2147: return "ReflectorMagnification";
      case 2148: return "LampMirrorPosition";
      case 2149: return "FocusDepth";
      case 2150: return "MicroscopeType";
      case 2151: return "Objective Working Distance";
      case 2152: return "ReflectedLightApertureGoSpeed";
      case 2153: return "External Shutter";
      case 2154: return "ObjectiveImmersionStop";
      case 2155: return "Focus Start Speed";
      case 2156: return "Focus Acceleration";
      case 2157: return "ReflectedLightFieldstop";
      case 2158: return "ReflectedLightFieldstopGoSpeed";
      case 2159: return "ReflectedLightFilter 1";
      case 2160: return "ReflectedLightFilter 2";
      case 2161: return "ReflectedLightFilter1Position";
      case 2162: return "ReflectedLightFilter2Position";
      case 2163: return "TransmittedLightAttenuator";
      case 2164: return "ReflectedLightAttenuator";
      case 2165: return "Transmitted Light Shutter";
      case 2166: return "TransmittedLightAttenuatorGoSpeed";
      case 2167: return "ReflectedLightAttenuatorGoSpeed";
      case 2176: return "TransmittedLightVirtualFilterPosition";
      case 2177: return "TransmittedLightVirtualFilter";
      case 2178: return "ReflectedLightVirtualFilterPosition";
      case 2179: return "ReflectedLightVirtualFilter";
      case 2180: return "ReflectedLightHalogenLampMode";
      case 2181: return "ReflectedLightHalogenLampVoltage";
      case 2182: return "ReflectedLightHalogenLampColorTemperature";
      case 2183: return "ContrastManagerMode";
      case 2184: return "Dazzle Protection Active";
      case 2195: return "Zoom";
      case 2196: return "ZoomGoSpeed";
      case 2197: return "LightZoom";
      case 2198: return "LightZoomGoSpeed";
      case 2199: return "LightZoomCoupled";
      case 2200: return "TransmittedLightHalogenLampMode";
      case 2201: return "TransmittedLightHalogenLampVoltage";
      case 2202: return "TransmittedLightHalogenLampColorTemperature";
      case 2203: return "Reflected Coldlight Mode";
      case 2204: return "Reflected Coldlight Intensity";
      case 2205: return "Reflected Coldlight Color Temperature";
      case 2206: return "Transmitted Coldlight Mode";
      case 2207: return "Transmitted Coldlight Intensity";
      case 2208: return "Transmitted Coldlight Color Temperature";
      case 2209: return "Infinityspace Portchanger Position";
      case 2210: return "Beamsplitter Infinity Space";
      case 2211: return "TwoTv VisCamChanger Position";
      case 2212: return "Beamsplitter Ocular";
      case 2213: return "TwoTv CamerasChanger Position";
      case 2214: return "Beamsplitter Cameras";
      case 2215: return "Ocular Shutter";
      case 2216: return "TwoTv CamerasChangerCube";
      case 2218: return "Ocular Magnification";
      case 2219: return "Camera Adapter Magnification";
      case 2220: return "Microscope Port";
      case 2221: return "Ocular Total Magnification";
      case 2222: return "Field of View";
      case 2223: return "Ocular";
      case 2224: return "CameraAdapter";
      case 2225: return "StageJoystickEnabled";
      case 2226: return "ContrastManager Contrast Method";
      case 2229: return "CamerasChanger Beamsplitter Type";
      case 2235: return "Rearport Slider Position";
      case 2236: return "Rearport Source";
      case 2237: return "Beamsplitter Type Infinity Space";
      case 2238: return "Fluorescence Attenuator";
      case 2239: return "Fluorescence Attenuator Position";
      case 2261: return "Objective ID";
      case 2262: return "Reflector ID";
      case 2307: return "Camera Framestart Left";
      case 2308: return "Camera Framestart Top";
      case 2309: return "Camera Frame Width";
      case 2310: return "Camera Frame Height";
      case 2311: return "Camera Binning";
      case 2312: return "CameraFrameFull";
      case 2313: return "CameraFramePixelDistance";
      case 2318: return "DataFormatUseScaling";
      case 2319: return "CameraFrameImageOrientation";
      case 2320: return "VideoMonochromeSignalType";
      case 2321: return "VideoColorSignalType";
      case 2322: return "MeteorChannelInput";
      case 2323: return "MeteorChannelSync";
      case 2324: return "WhiteBalanceEnabled";
      case 2325: return "CameraWhiteBalanceRed";
      case 2326: return "CameraWhiteBalanceGreen";
      case 2327: return "CameraWhiteBalanceBlue";
      case 2331: return "CameraFrameScalingFactor";
      case 2562: return "Meteor Camera Type";
      case 2564: return "Exposure Time [ms]";
      case 2568: return "CameraExposureTimeAutoCalculate";
      case 2569: return "Meteor Gain Value";
      case 2571: return "Meteor Gain Automatic";
      case 2572: return "MeteorAdjustHue";
      case 2573: return "MeteorAdjustSaturation";
      case 2574: return "MeteorAdjustRedLow";
      case 2575: return "MeteorAdjustGreenLow";
      case 2576: return "Meteor Blue Low";
      case 2577: return "MeteorAdjustRedHigh";
      case 2578: return "MeteorAdjustGreenHigh";
      case 2579: return "MeteorBlue High";
      case 2582: return "CameraExposureTimeCalculationControl";
      case 2585: return "AxioCamFadingCorrectionEnable";
      case 2587: return "CameraLiveImage";
      case 2588: return "CameraLiveEnabled";
      case 2589: return "LiveImageSyncObjectName";
      case 2590: return "CameraLiveSpeed";
      case 2591: return "CameraImage";
      case 2592: return "CameraImageWidth";
      case 2593: return "CameraImageHeight";
      case 2594: return "CameraImagePixelType";
      case 2595: return "CameraImageShMemoryName";
      case 2596: return "CameraLiveImageWidth";
      case 2597: return "CameraLiveImageHeight";
      case 2598: return "CameraLiveImagePixelType";
      case 2599: return "CameraLiveImageShMemoryName";
      case 2600: return "CameraLiveMaximumSpeed";
      case 2601: return "CameraLiveBinning";
      case 2602: return "CameraLiveGainValue";
      case 2603: return "CameraLiveExposureTimeValue";
      case 2604: return "CameraLiveScalingFactor";
      case 2819: return "Image Index Z";
      case 2820: return "Image Channel Index";
      case 2821: return "Image Index T";
      case 2822: return "ImageTile Index";
      case 2823: return "Image acquisition Index";
      case 2827: return "Image IndexS";
      case 2841: return "Original Stage Position X";
      case 2842: return "Original Stage Position Y";
      case 3088: return "LayerDrawFlags";
      case 3334: return "RemainingTime";
      case 3585: return "User Field 1";
      case 3586: return "User Field 2";
      case 3587: return "User Field 3";
      case 3588: return "User Field 4";
      case 3589: return "User Field 5";
      case 3590: return "User Field 6";
      case 3591: return "User Field 7";
      case 3592: return "User Field 8";
      case 3593: return "User Field 9";
      case 3594: return "User Field 10";
      case 3840: return "ID";
      case 3841: return "Name";
      case 3842: return "Value";
      case 5501: return "PvCamClockingMode";
      case 8193: return "Autofocus Status Report";
      case 8194: return "Autofocus Position";
      case 8195: return "Autofocus Position Offset";
      case 8196: return "Autofocus Empty Field Threshold";
      case 8197: return "Autofocus Calibration Name";
      case 8198: return "Autofocus Current Calibration Item";
      case 20478: return "tag_ID_20478";
      case 65537: return "CameraFrameFullWidth";
      case 65538: return "CameraFrameFullHeight";
      case 65541: return "AxioCam Shutter Signal";
      case 65542: return "AxioCam Delay Time";
      case 65543: return "AxioCam Shutter Control";
      case 65544: return "AxioCam BlackRefIsCalculated";
      case 65545: return "AxioCam Black Reference";
      case 65547: return "Camera Shading Correction";
      case 65550: return "AxioCam Enhance Color";
      case 65551: return "AxioCam NIR Mode";
      case 65552: return "CameraShutterCloseDelay";
      case 65553: return "CameraWhiteBalanceAutoCalculate";
      case 65556: return "AxioCam NIR Mode Available";
      case 65557: return "AxioCam Fading Correction Available";
      case 65559: return "AxioCam Enhance Color Available";
      case 65565: return "MeteorVideoNorm";
      case 65566: return "MeteorAdjustWhiteReference";
      case 65567: return "MeteorBlackReference";
      case 65568: return "MeteorChannelInputCountMono";
      case 65570: return "MeteorChannelInputCountRGB";
      case 65571: return "MeteorEnableVCR";
      case 65572: return "Meteor Brightness";
      case 65573: return "Meteor Contrast";
      case 65575: return "AxioCam Selector";
      case 65576: return "AxioCam Type";
      case 65577: return "AxioCam Info";
      case 65580: return "AxioCam Resolution";
      case 65581: return "AxioCam Color Model";
      case 65582: return "AxioCam MicroScanning";
      case 65585: return "Amplification Index";
      case 65586: return "Device Command";
      case 65587: return "BeamLocation";
      case 65588: return "ComponentType";
      case 65589: return "ControllerType";
      case 65590: return "CameraWhiteBalanceCalculationRedPaint";
      case 65591: return "CameraWhiteBalanceCalculationBluePaint";
      case 65592: return "CameraWhiteBalanceSetRed";
      case 65593: return "CameraWhiteBalanceSetGreen";
      case 65594: return "CameraWhiteBalanceSetBlue";
      case 65595: return "CameraWhiteBalanceSetTargetRed";
      case 65596: return "CameraWhiteBalanceSetTargetGreen";
      case 65597: return "CameraWhiteBalanceSetTargetBlue";
      case 65598: return "ApotomeCamCalibrationMode";
      case 65599: return "ApoTome Grid Position";
      case 65600: return "ApotomeCamScannerPosition";
      case 65601: return "ApoTome Full Phase Shift";
      case 65602: return "ApoTome Grid Name";
      case 65603: return "ApoTome Staining";
      case 65604: return "ApoTome Processing Mode";
      case 65605: return "ApotmeCamLiveCombineMode";
      case 65606: return "ApoTome Filter Name";
      case 65607: return "Apotome Filter Strength";
      case 65608: return "ApotomeCamFilterHarmonics";
      case 65609: return "ApoTome Grating Period";
      case 65610: return "ApoTome Auto Shutter Used";
      case 65611: return "Apotome Cam Status";
      case 65612: return "ApotomeCamNormalize";
      case 65613: return "ApotomeCamSettingsManager";
      case 65614: return "DeepviewCamSupervisorMode";
      case 65615: return "DeepView Processing";
      case 65616: return "DeepviewCamFilterName";
      case 65617: return "DeepviewCamStatus";
      case 65618: return "DeepviewCamSettingsManager";
      case 65619: return "DeviceScalingName";
      case 65620: return "CameraShadingIsCalculated";
      case 65621: return "CameraShadingCalculationName";
      case 65622: return "CameraShadingAutoCalculate";
      case 65623: return "CameraTriggerAvailable";
      case 65626: return "CameraShutterAvailable";
      case 65627: return "AxioCam ShutterMicroScanningEnable";
      case 65628: return "ApotomeCamLiveFocus";
      case 65629: return "DeviceInitStatus";
      case 65630: return "DeviceErrorStatus";
      case 65631: return "ApotomeCamSliderInGridPosition";
      case 65632: return "Orca NIR Mode Used";
      case 65633: return "Orca Analog Gain";
      case 65634: return "Orca Analog Offset";
      case 65635: return "Orca Binning";
      case 65636: return "Orca Bit Depth";
      case 65637: return "ApoTome Averaging Count";
      case 65638: return "DeepView DoF";
      case 65639: return "DeepView EDoF";
      case 65643: return "DeepView Slider Name";
      case 65655: return "DeepView Slider Name";
      case 5439491: return "Acquisition Sofware";
      case 16777488: return "Excitation Wavelength";
      case 16777489: return "Emission Wavelength";
      case 101515267: return "File Name";
      case 101253123:
      case 101777411:
        return "Image Name";
      default: return "" + tagID;
    }
  }

}
