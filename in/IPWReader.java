//
// IPWReader.java
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
import java.io.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import loci.formats.*;

/**
 * IPWReader is the file format reader for Image-Pro Workspace (IPW) files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class IPWReader extends BaseTiffReader {

  // -- Constants --

  private static final String NO_POI_MSG = "You need to install Jakarta POI " +
    "from http://jakarta.apache.org/poi/";

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
    catch (Throwable exc) { noPOI = true; }
    return r;
  }

  // -- Fields --

  private Hashtable pixels;
  private Hashtable names;
  private byte[] header;  // general image header data
  private byte[] tags; // tags data
  private boolean rgb;
  private boolean little;
  private int totalBytes= 0;

  // -- Constructor --

  /** Constructs a new IPW reader. */
  public IPWReader() { super("Image-Pro Workspace", "ipw"); }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for an IPW file. */
  public boolean isThisType(byte[] block) {
    // all of our samples begin with 0xd0cf11e0
    return (block[0] == 0xd0 && block[1] == 0xcf &&
      block[2] == 0x11 && block[3] == 0xe0);
  }

  /** Determines the number of images in the given IPW file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numImages;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return rgb;
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return little;
  }

  /** Obtains the specified image from the given IPW file, as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

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
      byte[] b = new byte[numBytes + 4]; // append 0 for final offset
      r.setVar("data", b);
      r.exec("dis.read(data)");

      RandomAccessStream stream = new RandomAccessStream(b);
      ifds = TiffTools.getIFDs(stream);
      little = TiffTools.isLittleEndian(ifds[0]);
      byte[][] samples = TiffTools.getSamples(ifds[0], stream, 0);

      byte[] rtn = new byte[samples.length * samples[0].length];
      for (int i=0; i<samples.length; i++) {
        System.arraycopy(samples[i], 0, rtn, i*samples[i].length,
          samples[i].length);
      }
      stream.close();
      return rtn;
    }
    catch (ReflectException e) {
      noPOI = true;
      return new byte[0];
    }
  }

  /** Obtains the specified image from the given IPW file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    byte[] b = openBytes(id, no);
    int bytes = b.length / (sizeX[0] * sizeY[0]);
    return ImageTools.makeImage(b, sizeX[0], sizeY[0], bytes == 3 ? 3 : 1,
      false, bytes == 3 ? 1 : bytes, little);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
 
    pixels = null;
    names = null;
    header = null;
    tags = null;

    String[] vars = {"dirName", "root", "dir", "document", "dis", 
      "numBytes", "data", "fis", "fs", "iter", "isInstance", "isDocument", 
      "entry", "documentName", "entryName"};
    for (int i=0; i<vars.length; i++) r.setVar(vars[i], null);
  }

  /** Initializes the given IPW file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (noPOI) throw new FormatException(NO_POI_MSG);
    currentId = id;

    in = new RandomAccessStream(id);

    metadata = new Hashtable();
    pixels = new Hashtable();
    names = new Hashtable();
    numImages = 0;

    try {
      r.setVar("fis", new FileInputStream(id));
      r.exec("fs = new POIFSFileSystem(fis)");
      r.exec("dir = fs.getRoot()");
      parseDir(0, r.getVar("dir"));
      initMetadata(id);
    }
    catch (Throwable t) {
      noPOI = true;
      if (DEBUG) t.printStackTrace();
    }
  }

  // -- Internal BaseTiffReader API methods --

  /** Initialize metadata hashtable and OME-XML structure. */
  public void initMetadata(String id)
    throws FormatException, IOException
  {
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

    rgb = (TiffTools.getIFDIntValue(ifds[0],
      TiffTools.SAMPLES_PER_PIXEL, false, 1) > 1);

    if (!rgb) {
      rgb = TiffTools.getIFDIntValue(ifds[0],
        TiffTools.PHOTOMETRIC_INTERPRETATION, false, 1) ==
        TiffTools.RGB_PALETTE;
    }

    little = TiffTools.isLittleEndian(ifds[0]);

    // parse the image description
    String description = new String(tags, 22, tags.length-22);
    metadata.put("Image Description", description);

    // default values
    metadata.put("slices", "1");
    metadata.put("channels", "1");
    metadata.put("frames", new Integer(getImageCount(id)));

    // parse the description to get channels/slices/times where applicable
    // basically the same as in ImageProSeqForm
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
        metadata.put(label, data);
      }
    }

    metadata.put("Version", new String(header).trim());

    Integer tSize = Integer.valueOf((String) metadata.get("slices"));
    Integer cSize = Integer.valueOf((String) metadata.get("channels"));
    Integer zSize = Integer.valueOf(metadata.get("frames").toString());

    sizeX = new int[1];
    sizeY = new int[1];
    sizeZ = new int[1];
    sizeC = new int[1];
    sizeT = new int[1];
    pixelType = new int[1];
    currentOrder = new String[1];

    Hashtable h = ifds[0];
    sizeX[0] = TiffTools.getIFDIntValue(h, TiffTools.IMAGE_WIDTH);
    sizeY[0] = TiffTools.getIFDIntValue(h, TiffTools.IMAGE_LENGTH);
    sizeZ[0] = Integer.valueOf(metadata.get("frames").toString()).intValue();
    sizeC[0] = Integer.parseInt((String) metadata.get("channels"));
    sizeT[0] = Integer.parseInt((String) metadata.get("slices"));
    currentOrder[0] = "XY";

    if (rgb) sizeC[0] *= 3;

    int maxNdx = 0, max = 0;
    int[] dims = {sizeZ[0], sizeC[0], sizeT[0]};
    String[] axes = {"Z", "C", "T"};

    for (int i=0; i<dims.length; i++) {
      if (dims[i] > max) {
        max = dims[i];
        maxNdx = i;
      }
    }

    currentOrder[0] += axes[maxNdx];

    if (maxNdx != 1) {
      if (sizeC[0] > 1) {
        currentOrder[0] += "C";
        currentOrder[0] += (maxNdx == 0 ? axes[2] : axes[0]);
      }
      else currentOrder[0] += (maxNdx == 0 ? axes[2] : axes[0]) + "C";
    }
    else {
      if (sizeZ[0] > sizeT[0]) currentOrder[0] += "ZT";
      else currentOrder[0] += "TZ";
    }

    int bitsPerSample = TiffTools.getIFDIntValue(ifds[0],
      TiffTools.BITS_PER_SAMPLE);
    int bitFormat = TiffTools.getIFDIntValue(ifds[0], TiffTools.SAMPLE_FORMAT);

    while (bitsPerSample % 8 != 0) bitsPerSample++;
    if (bitsPerSample == 24 || bitsPerSample == 48) bitsPerSample /= 3;

    if (bitFormat == 3) pixelType[0] = FormatReader.FLOAT;
    else if (bitFormat == 2) {
      switch (bitsPerSample) {
        case 8:
          pixelType[0] = FormatReader.INT8;
          break;
        case 16:
          pixelType[0] = FormatReader.INT16;
          break;
        case 32:
          pixelType[0] = FormatReader.INT32;
          break;
      }
    }
    else {
      switch (bitsPerSample) {
        case 8:
          pixelType[0] = FormatReader.UINT8;
          break;
        case 16:
          pixelType[0] = FormatReader.UINT16;
          break;
        case 32:
          pixelType[0] = FormatReader.UINT32;
          break;
      }
    }

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore(id);

    store.setPixels(null, null, zSize, cSize, tSize, new Integer(pixelType[0]),
      new Boolean(!isLittleEndian(id)), getDimensionOrder(id), null);
    store.setImage(null, null, (String) metadata.get("Version"), null);
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
        r.exec("dis = new DocumentInputStream(entry)");
        r.exec("numBytes = dis.available()");
        int numbytes = ((Integer) r.getVar("numBytes")).intValue();
        byte[] data = new byte[numbytes + 4]; // append 0 for final offset
        r.setVar("data", data);
        r.exec("dis.read(data)");

        String entryName = (String) r.getVar("entryName");
        String dirName = (String) r.getVar("dirName");

        boolean isContents = entryName.equals("CONTENTS");
        totalBytes += data.length + entryName.length();

        if (isContents) {
          // software version
          header = data;
        }
        else if (entryName.equals("FrameRate")) {
          // should always be exactly 4 bytes
          // only exists if the file has more than one image
          metadata.put("Frame Rate",
            new Long(DataTools.bytesToInt(data, true)));
        }
        else if (entryName.equals("FrameInfo")) {
          // should always be 16 bytes (if present)
          for(int i=0; i<data.length/2; i++) {
            metadata.put("FrameInfo "+i,
              new Short(DataTools.bytesToShort(data, i*2, true)));
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
          numImages++;
        }
        r.exec("dis.close()");
        if (DEBUG) {
          print(depth + 1, ((byte[])
            r.getVar("data")).length + " bytes read.");
        }
      }
    }
  }

  /** Debugging utility method. */
  public static final void print(int depth, String s) {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<depth; i++) sb.append("  ");
    sb.append(s);
    System.out.println(sb.toString());
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new IPWReader().testRead(args);
  }

}
