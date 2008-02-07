//
// IPWReader.java
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
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * IPWReader is the file format reader for Image-Pro Workspace (IPW) files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/IPWReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/IPWReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class IPWReader extends BaseTiffReader {

  // -- Constants --

  private static final String NO_POI_MSG =
    "Jakarta POI is required to read IPW files. Please " +
    "obtain poi-loci.jar from http://loci.wisc.edu/ome/formats.html";

  // -- Static fields --

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
      r.exec("import org.apache.poi.util.RandomAccessStream");
      r.exec("import java.util.Iterator");
    }
    catch (Throwable t) {
      noPOI = true;
      if (debug) LogTools.trace(t);
    }
    return r;
  }

  // -- Fields --

  private Hashtable pixels;
  private Hashtable names;
  private byte[] header;  // general image header data
  private byte[] tags; // tags data

  // -- Constructor --

  /** Constructs a new IPW reader. */
  public IPWReader() { super("Image-Pro Workspace", "ipw"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    // all of our samples begin with 0xd0cf11e0
    return (block[0] == 0xd0 && block[1] == 0xcf &&
      block[2] == 0x11 && block[3] == 0xe0);
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    RandomAccessStream stream = getStream(0);
    ifds = TiffTools.getIFDs(stream);
    int[] bits = TiffTools.getBitsPerSample(ifds[0]);
    if (bits[0] <= 8) {
      int[] colorMap =
        (int[]) TiffTools.getIFDValue(ifds[0], TiffTools.COLOR_MAP);
      if (colorMap == null) return null;

      byte[][] table = new byte[3][colorMap.length / 3];
      int next = 0;
      for (int j=0; j<table.length; j++) {
        for (int i=0; i<table[0].length; i++) {
          table[j][i] = (byte) (colorMap[next++] >> 8);
        }
      }

      return table;
    }
    return null;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length, w, h);

    RandomAccessStream stream = getStream(no);
    ifds = TiffTools.getIFDs(stream);
    TiffTools.getSamples(ifds[0], stream, buf, x, y, w, h);
    stream.close();

    if (core.pixelType[0] == FormatTools.UINT16 ||
      core.pixelType[0] == FormatTools.INT16)
    {
      for (int i=0; i<buf.length; i+=2) {
        byte b = buf[i];
        buf[i] = buf[i + 1];
        buf[i + 1] = b;
      }
    }
    else if (core.pixelType[0] == FormatTools.UINT32 ||
      core.pixelType[0] == FormatTools.INT32)
    {
      for (int i=0; i<buf.length; i+=4) {
        byte b = buf[i];
        buf[i] = buf[i + 3];
        buf[i + 3] = b;
        b = buf[i + 1];
        buf[i + 1] = buf[i + 2];
        buf[i + 2] = b;
      }
    }

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();

    pixels = null;
    names = null;
    header = null;
    tags = null;

    try { r.exec("fis.close()"); }
    catch (ReflectException e) { }
    String[] vars = {"dirName", "root", "dir", "document", "dis",
      "numBytes", "data", "fis", "fs", "iter", "isInstance", "isDocument",
      "entry", "documentName", "entryName"};
    for (int i=0; i<vars.length; i++) r.setVar(vars[i], null);
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initMetadata() */
  public void initMetadata() throws FormatException, IOException {
    String directory = (String) pixels.get(new Integer(0));
    String name = (String) names.get(new Integer(0));

    try {
      r.setVar("dirName", directory);
      r.exec("root = fs.getRoot()");
      if (!directory.equals("Root Entry")) {
        r.exec("dir = root.getEntry(dirName)");
        r.setVar("entryName", name);
        r.exec("document = dir.getEntry(entryName)");
      }
      else {
        r.setVar("entryName", name);
        r.exec("document = root.getEntry(entryName)");
      }

      r.exec("dis = new DocumentInputStream(document, fis)");
      r.exec("numBytes = dis.available()");
      int numBytes = ((Integer) r.getVar("numBytes")).intValue();
      byte[] b = new byte[numBytes];
      r.setVar("data", b);
      r.exec("dis.read(data)");

      RandomAccessStream stream = new RandomAccessStream(b);
      ifds = TiffTools.getIFDs(stream);
      stream.close();
    }
    catch (ReflectException e) { }

    core.rgb[0] = (TiffTools.getIFDIntValue(ifds[0],
      TiffTools.SAMPLES_PER_PIXEL, false, 1) > 1);

    if (!core.rgb[0]) {
      core.indexed[0] = TiffTools.getIFDIntValue(ifds[0],
        TiffTools.PHOTOMETRIC_INTERPRETATION, false, 1) ==
        TiffTools.RGB_PALETTE;
    }

    core.littleEndian[0] = TiffTools.isLittleEndian(ifds[0]);

    // parse the image description
    String description = new String(tags).trim();
    addMeta("Image Description", description);

    // default values

    core.sizeZ[0] = 1;
    core.sizeC[0] = 1;
    core.sizeT[0] = getImageCount();
    addMeta("slices", "1");
    addMeta("channels", "1");
    addMeta("frames", new Integer(getImageCount()));

    String timestamp = null;

    // parse the description to get channels/slices/times where applicable
    // basically the same as in SEQReader
    if (description != null) {
      StringTokenizer tokenizer = new StringTokenizer(description, "\n");
      while (tokenizer.hasMoreTokens()) {
        String token = tokenizer.nextToken();
        String label = "Timestamp";
        String data;
        if (token.indexOf("=") != -1) {
          label = token.substring(0, token.indexOf("=")).trim();
          data = token.substring(token.indexOf("=") + 1).trim();
        }
        else {
          data = token.trim();
        }
        addMeta(label, data);
        if (label.equals("frames")) core.sizeZ[0] = Integer.parseInt(data);
        else if (label.equals("slices")) core.sizeT[0] = Integer.parseInt(data);
        else if (label.equals("channels")) {
          core.sizeC[0] = Integer.parseInt(data);
        }
        else if (label.equals("Timestamp")) timestamp = label;
      }
    }

    String version = new String(header).trim();
    addMeta("Version", version);

    Hashtable h = ifds[0];
    core.sizeX[0] = TiffTools.getIFDIntValue(h, TiffTools.IMAGE_WIDTH);
    core.sizeY[0] = TiffTools.getIFDIntValue(h, TiffTools.IMAGE_LENGTH);
    core.currentOrder[0] = core.rgb[0] ? "XYCTZ" : "XYTCZ";

    if (core.sizeZ[0] == 0) core.sizeZ[0] = 1;
    if (core.sizeC[0] == 0) core.sizeC[0] = 1;
    if (core.sizeT[0] == 0) core.sizeT[0] = 1;

    if (core.rgb[0]) core.sizeC[0] *= 3;

    int bitsPerSample = TiffTools.getIFDIntValue(ifds[0],
      TiffTools.BITS_PER_SAMPLE);
    int bitFormat = TiffTools.getIFDIntValue(ifds[0], TiffTools.SAMPLE_FORMAT);

    while (bitsPerSample % 8 != 0) bitsPerSample++;
    if (bitsPerSample == 24 || bitsPerSample == 48) bitsPerSample /= 3;

    core.pixelType[0] = FormatTools.UINT8;

    if (bitFormat == 3) core.pixelType[0] = FormatTools.FLOAT;
    else if (bitFormat == 2) {
      switch (bitsPerSample) {
        case 8:
          core.pixelType[0] = FormatTools.INT8;
          break;
        case 16:
          core.pixelType[0] = FormatTools.INT16;
          break;
        case 32:
          core.pixelType[0] = FormatTools.INT32;
          break;
      }
    }
    else {
      switch (bitsPerSample) {
        case 8:
          core.pixelType[0] = FormatTools.UINT8;
          break;
        case 16:
          core.pixelType[0] = FormatTools.UINT16;
          break;
        case 32:
          core.pixelType[0] = FormatTools.UINT32;
          break;
      }
    }

    // The metadata store we're working with.
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    store.setImageName("", 0);

    if (timestamp != null) {
      SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS aa");
      Date d = fmt.parse(timestamp, new ParsePosition(0));
      fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      store.setImageCreationDate(fmt.format(d), 0);
    }
    else {
      store.setImageCreationDate(
        DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), 0);
    }

    MetadataTools.populatePixels(store, this);
    store.setImageDescription(description, 0);
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("IPWReader.initFile(" + id + ")");
    if (noPOI) throw new FormatException(NO_POI_MSG);

    currentId = id;
    metadata = new Hashtable();
    core = new CoreMetadata(1);
    Arrays.fill(core.orderCertain, true);
    getMetadataStore().createRoot();

    in = new RandomAccessStream(id);

    pixels = new Hashtable();
    names = new Hashtable();

    try {
      in.order(true);
      in.seek(30);
      int size = (int) Math.pow(2, in.readShort());
      in.close();
      r.setVar("file", currentId);
      r.exec("fis = new RandomAccessStream(file)");
      r.setVar("size", size);
      r.setVar("littleEndian", true);
      r.exec("fis.order(littleEndian)");
      r.exec("fs = new POIFSFileSystem(fis, size)");
      r.exec("dir = fs.getRoot()");
      parseDir(0, r.getVar("dir"));
      status("Populating metadata");
      initMetadata();
    }
    catch (Throwable t) {
      noPOI = true;
      if (debug) trace(t);
    }
  }

  // -- Helper methods --

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
        parseDir(depth + 1, r.getVar("entry"));
      }
      else if (isDocument) {
        r.exec("entryName = entry.getName()");
        r.exec("dis = new DocumentInputStream(entry, fis)");
        r.exec("numBytes = dis.available()");
        int numbytes = ((Integer) r.getVar("numBytes")).intValue();

        String entryName = (String) r.getVar("entryName");
        String dirName = (String) r.getVar("dirName");

        boolean isContents = entryName.equals("CONTENTS");

        if (isContents) {
          // software version
          header = new byte[numbytes];
          r.setVar("data", header);
          r.exec("dis.read(data)");
        }
        else if (entryName.equals("FrameRate")) {
          // should always be exactly 4 bytes
          // only exists if the file has more than one image
          byte[] b = new byte[4];
          r.setVar("data", b);
          r.exec("dis.read(data)");
          addMeta("Frame Rate", new Integer(DataTools.bytesToInt(b, true)));
        }
        else if (entryName.equals("FrameInfo")) {
          byte[] b = new byte[2];
          r.setVar("data", b);
          for (int i=0; i<numbytes/2; i++) {
            r.exec("dis.read(data)");
            addMeta("FrameInfo " + i,
              new Short(DataTools.bytesToShort(b, true)));
          }
        }
        else if (entryName.equals("ImageInfo")) {
          // acquisition data
          tags = new byte[numbytes];
          r.setVar("data", tags);
          r.exec("dis.read(data)");
        }
        else if (entryName.equals("ImageTIFF")) {
          // pixel data
          String name = "0";
          if (!dirName.equals("Root Entry")) {
            name = dirName.substring(11, dirName.length());
          }

          Integer imageNum = Integer.valueOf(name);
          pixels.put(imageNum, dirName);
          names.put(imageNum, entryName);
          core.imageCount[0]++;
        }

        r.exec("dis.close()");
        if (debug) print(depth + 1, numbytes + " bytes read.");
      }
    }
  }

  /** Retrieve the file corresponding to the given image number. */
  private RandomAccessStream getStream(int no) throws IOException {
    try {
      String directory = (String) pixels.get(new Integer(no));
      String name = (String) names.get(new Integer(no));

      r.setVar("dirName", directory);
      r.exec("root = fs.getRoot()");

      if (!directory.equals("Root Entry")) {
        r.exec("dir = root.getEntry(dirName)");
        r.setVar("entryName", name);
        r.exec("document = dir.getEntry(entryName)");
      }
      else {
        r.setVar("entryName", name);
        r.exec("document = root.getEntry(entryName)");
      }

      r.exec("dis = new DocumentInputStream(document, fis)");
      r.exec("numBytes = dis.available()");
      int numBytes = ((Integer) r.getVar("numBytes")).intValue();
      byte[] b = new byte[numBytes];
      r.setVar("data", b);
      r.exec("dis.read(data)");

      return new RandomAccessStream(b);
    }
    catch (ReflectException e) {
      noPOI = true;
      return null;
    }
  }

  /** Debugging helper method. */
  protected void print(int depth, String s) {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<depth; i++) sb.append("  ");
    sb.append(s);
    debug(sb.toString());
  }

}
