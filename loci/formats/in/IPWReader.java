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
import java.util.*;
import loci.formats.*;

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

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    RandomAccessStream stream = getStream(no);
    ifds = TiffTools.getIFDs(stream);
    core.littleEndian[0] = TiffTools.isLittleEndian(ifds[0]);
    TiffTools.getSamples(ifds[0], stream, buf);
    stream.close();
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

      r.exec("dis = new DocumentInputStream(document)");
      r.exec("numBytes = dis.available()");
      int numBytes = ((Integer) r.getVar("numBytes")).intValue();
      byte[] b = new byte[numBytes + 4]; // append 0 for final offset
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
      core.rgb[0] = TiffTools.getIFDIntValue(ifds[0],
        TiffTools.PHOTOMETRIC_INTERPRETATION, false, 1) ==
        TiffTools.RGB_PALETTE;
    }

    core.littleEndian[0] = TiffTools.isLittleEndian(ifds[0]);

    // parse the image description
    String description = new String(tags, 22, tags.length-22);
    addMeta("Image Description", description);

    // default values

    core.sizeZ[0] = 1;
    core.sizeC[0] = 1;
    core.sizeT[0] = getImageCount();
    addMeta("slices", "1");
    addMeta("channels", "1");
    addMeta("frames", new Integer(getImageCount()));

    // parse the description to get channels/slices/times where applicable
    // basically the same as in SEQReader
    if (description != null) {
      StringTokenizer tokenizer = new StringTokenizer(description, "\n");
      while (tokenizer.hasMoreTokens()) {
        String token = tokenizer.nextToken();
        String label = "Timestamp";
        String data;
        if (token.indexOf("=") != -1) {
          label = token.substring(0, token.indexOf("="));
          data = token.substring(token.indexOf("=")+1);
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
      }
    }

    addMeta("Version", new String(header).trim());

    Hashtable h = ifds[0];
    core.sizeX[0] = TiffTools.getIFDIntValue(h, TiffTools.IMAGE_WIDTH);
    core.sizeY[0] = TiffTools.getIFDIntValue(h, TiffTools.IMAGE_LENGTH);
    core.currentOrder[0] = "XY";

    if (core.sizeZ[0] == 0) core.sizeZ[0] = 1;
    if (core.sizeC[0] == 0) core.sizeC[0] = 1;
    if (core.sizeT[0] == 0) core.sizeT[0] = 1;

    if (core.rgb[0]) core.sizeC[0] *= 3;

    int maxNdx = 0, max = 0;
    int[] dims = {core.sizeZ[0], core.sizeC[0], core.sizeT[0]};
    String[] axes = {"Z", "C", "T"};

    for (int i=0; i<dims.length; i++) {
      if (dims[i] > max) {
        max = dims[i];
        maxNdx = i;
      }
    }

    core.currentOrder[0] += axes[maxNdx];

    if (maxNdx != 1) {
      if (core.sizeC[0] > 1) {
        core.currentOrder[0] += "C";
        core.currentOrder[0] += (maxNdx == 0 ? axes[2] : axes[0]);
      }
      else core.currentOrder[0] += (maxNdx == 0 ? axes[2] : axes[0]) + "C";
    }
    else {
      if (core.sizeZ[0] > core.sizeT[0]) core.currentOrder[0] += "ZT";
      else core.currentOrder[0] += "TZ";
    }

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
    MetadataStore store = getMetadataStore();

    FormatTools.populatePixels(store, this);
    store.setImage(currentId, null, (String) getMeta("Version"), null);
    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null);
    }
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
      r.setVar("fis", in);
      r.exec("fs = new POIFSFileSystem(fis)");
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
        status("Parsing embedded folder (" + (depth + 1) + ")");
        parseDir(depth + 1, r.getVar("entry"));
      }
      else if (isDocument) {
        status("Parsing embedded file (" + depth + ")");
        r.exec("entryName = entry.getName()");
        r.exec("dis = new DocumentInputStream(entry)");
        r.exec("numBytes = dis.available()");
        int numbytes = ((Integer) r.getVar("numBytes")).intValue();
        byte[] data = new byte[numbytes + 4]; // append 0 for final offset
        r.setVar("data", data);
        r.exec("dis.read(data)");

        RandomAccessStream ds = new RandomAccessStream(data);
        ds.order(true);

        String entryName = (String) r.getVar("entryName");
        String dirName = (String) r.getVar("dirName");

        boolean isContents = entryName.equals("CONTENTS");

        if (isContents) {
          // software version
          header = data;
        }
        else if (entryName.equals("FrameRate")) {
          // should always be exactly 4 bytes
          // only exists if the file has more than one image
          addMeta("Frame Rate", new Long(ds.readInt()));
        }
        else if (entryName.equals("FrameInfo")) {
          // should always be 16 bytes (if present)
          for(int i=0; i<data.length/2; i++) {
            addMeta("FrameInfo "+i, new Short(ds.readShort()));
          }
        }
        else if (entryName.equals("ImageInfo")) {
          // acquisition data
          tags = data;
        }
        else if (entryName.equals("ImageResponse")) {
          // skip this entry
        }
        else if (entryName.equals("ImageTIFF")) {
          // pixel data
          String name;
          if (!dirName.equals("Root Entry")) {
            name = dirName.substring(11, dirName.length());
          }
          else name = "0";

          Integer imageNum = Integer.valueOf(name);
          pixels.put(imageNum, dirName);
          names.put(imageNum, entryName);
          core.imageCount[0]++;
        }
        ds.close();
        r.exec("dis.close()");
        if (debug) {
          print(depth + 1, data.length + " bytes read.");
        }
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

      r.exec("dis = new DocumentInputStream(document)");
      r.exec("numBytes = dis.available()");
      int numBytes = ((Integer) r.getVar("numBytes")).intValue();
      byte[] b = new byte[numBytes + 4];
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
