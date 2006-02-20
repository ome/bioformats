//
// IPWReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats;

import java.awt.Image;
import java.io.*;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * IPWReader is the file format reader for Image-Pro Workspace (IPW) files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class IPWReader extends BaseTiffReader {

  // -- Constants --

  private static final boolean DEBUG = false;
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

  private Hashtable pixelData = new Hashtable();
  private byte[] header;  // general image header data
  private byte[] tags; // tags data
  private Hashtable allIFDs;
  private RandomAccessArray ra;

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

  /** Obtains the specified image from the given IPW file. */
  public Image open(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }
    byte[] pixels = (byte[]) pixelData.get(new Integer(no));
    ifds = (Hashtable[]) allIFDs.get(new Integer(no));
    ra.setStream(pixels);
    return TiffTools.getImage(ifds[0], ra);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given IPW file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (noPOI) throw new FormatException(NO_POI_MSG);
    close();
    currentId = id;
    metadata = new Hashtable();
    ome = OMETools.createRoot();
    in = new RandomAccessFile(id, "r");

    allIFDs = new Hashtable();
    numImages = 0;
    ra = new RandomAccessArray(id, "r");

    try {
      r.setVar("fis", new FileInputStream(id));
      r.exec("fs = new POIFSFileSystem(fis)");
      r.exec("dir = fs.getRoot()");
      parseDir(0, r.getVar("dir"));
      for(int i=0; i<pixelData.size(); i++) {
        Integer key = new Integer(i);
        ra.setStream((byte[]) pixelData.get(key));
        allIFDs.put(key, TiffTools.getIFDs(ra));
      }
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

    ifds = (Hashtable[]) allIFDs.get(new Integer(0));
    super.initStandardMetadata();

    if (ome != null) {
      super.initOMEMetadata();
      OMETools.setAttribute(ome, "Pixels", "SizeZ", "" +
        metadata.get("slices"));
      OMETools.setAttribute(ome, "Pixels", "SizeC", "" +
        metadata.get("channels"));
      OMETools.setAttribute(ome, "Pixels", "SizeT", "" +
        metadata.get("frames"));
      OMETools.setAttribute(ome, "Image", "Description", "" +
        metadata.get("Version"));
    }
  }


  // -- Helper methods --

  protected void parseDir(int depth, Object dir)
    throws IOException, FormatException, ReflectException
  {
    r.setVar("dir", dir);
    r.exec("dirName = dir.getName()");
    if (DEBUG) print(depth, r.getVar("dirName") + " {");
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
        if (DEBUG) {
          print(depth + 1, "Found document: " + r.getVar("entryName"));
        }
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
            metadata.put("FrameInfo "+i, new Short(
            DataTools.bytesToShort(data, i*2, true)));
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
          pixelData.put(imageNum, data);
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

  /** Debugging utility method */
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
