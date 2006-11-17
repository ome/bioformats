//
// OmeisImporter.java
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

import java.io.*;
import java.net.URL;
import java.util.*;
import org.openmicroscopy.xml.DOMUtil;
import org.openmicroscopy.xml.OMENode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A command line utility used by OMEIS to interface with Bio-Formats.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class OmeisImporter {

  // -- Constants --

  /** Debugging flag. */
  private static final boolean DEBUG = true;

  /** Network path to OMEIS. */
  private static final String OMEIS_PATH = "http://localhost/cgi-bin/omeis";

  // -- Static fields --

  /**
   * Whether or not to print an HTTP header,
   * specified by -http-response CLI flag.
   */
  private static boolean httpResponse = false;

  // -- Fields --

  /** Reader for handling file formats. */
  private IFormatReader reader;

  /** Metadata store, for gathering OME-XML metadata. */
  private OMEXMLMetadataStore store;

  // -- Constructor --

  public OmeisImporter() {
    reader = new ChannelSeparator();
    store = new OMEXMLMetadataStore();
    reader.setMetadataStore(store);
  }

  // -- OmeisImporter API methods - main functionality --

  /**
   * Tests whether Bio-Formats is potentially capable of importing the given
   * file IDs. Outputs the IDs it can potentially import, one per line.
   */
  public void testIds(int[] fileIds) throws OmeisException {
    // set up file path mappings
    String[] ids = new String[fileIds.length];
    for (int i=0; i<fileIds.length; i++) {
      Hashtable fileInfo = getFileInfo(fileIds[i]);
      ids[i] = (String) fileInfo.get("Name");
      String path = getLocalFilePath(fileIds[i]);
      reader.mapId(ids[i], path);
    }
    // check types
    for (int i=0; i<fileIds.length; i++) {
      if (ids[i] != null && reader.isThisType(ids[i])) {
        if (httpResponse) printHttpResponseHeader();
        System.out.println(fileIds[i]);
      }
    }
  }

  /**
   * Attempts to import the given file IDs using Bio-Formats. Pixels are saved
   * to the pixels files designated by OMEIS, and an OME-XML metadata block
   * describing all successfully imported data is dumped to standard output.
   */
  public void importIds(int[] fileIds) throws OmeisException {
    boolean doLittle = isLittleEndian();

    // set up file path mappings
    String[] ids = new String[fileIds.length];
    for (int i=0; i<fileIds.length; i++) {
      Hashtable fileInfo = getFileInfo(fileIds[i]);
      ids[i] = (String) fileInfo.get("Name");
      String path = getLocalFilePath(fileIds[i]);
      reader.mapId(ids[i], path);
    }

    ByteArrayOutputStream xml = new ByteArrayOutputStream();

    // read files
    for (int i=0; i<fileIds.length; i++) {
      String id = ids[i];
      String path = reader.getMappedId(ids[i]);
      if (DEBUG) log("Reading file '" + id + "' --> " + path);
      try {
        int seriesCount = reader.getSeriesCount(id);

        // get DOM and Pixels elements for the file's OME-XML metadata
        OMENode ome = (OMENode) store.getRoot();
        Document omeDoc = ome.getOMEDocument(false);
        Vector pix = DOMUtil.findElementList("Pixels", omeDoc);
        if (pix.size() != seriesCount) {
          System.err.println("Error: Pixels element count (" +
            pix.size() + ") does not match series count (" +
            seriesCount + ") for '" + path + "'");
          continue;
        }
        if (DEBUG) log(seriesCount + " series detected.");

        for (int s=0; s<seriesCount; s++) {
          reader.setSeries(id, s);

          // gather pixels information for this series
          int sizeX = reader.getSizeX(id);
          int sizeY = reader.getSizeY(id);
          int sizeZ = reader.getSizeZ(id);
          int sizeC = reader.getSizeC(id);
          int sizeT = reader.getSizeT(id);
          int pixelType = reader.getPixelType(id);
          int bytesPerPixel;
          boolean isSigned, isFloat;
          switch (pixelType) {
            case FormatReader.INT8:
              bytesPerPixel = 1;
              isSigned = true;
              isFloat = false;
              break;
            case FormatReader.UINT8:
              bytesPerPixel = 1;
              isSigned = false;
              isFloat = false;
              break;
            case FormatReader.INT16:
              bytesPerPixel = 2;
              isSigned = true;
              isFloat = false;
              break;
            case FormatReader.UINT16:
              bytesPerPixel = 2;
              isSigned = false;
              isFloat = false;
              break;
            case FormatReader.INT32:
              bytesPerPixel = 4;
              isSigned = true;
              isFloat = false;
              break;
            case FormatReader.UINT32:
              bytesPerPixel = 4;
              isSigned = false;
              isFloat = false;
              break;
            case FormatReader.FLOAT:
              bytesPerPixel = 4;
              isSigned = true;
              isFloat = true;
              break;
            case FormatReader.DOUBLE:
              bytesPerPixel = 8;
              isSigned = true;
              isFloat = true;
              break;
            default:
              System.err.println("Error: unknown pixel type for '" +
                path + "' series #" + s + ": " + pixelType);
              continue;
          }
          boolean little = reader.isLittleEndian(id);
          boolean swap = doLittle != little;

          // ask OMEIS to allocate new pixels file
          int pixelsId = newPixels(sizeX, sizeY, sizeZ, sizeC, sizeT,
            bytesPerPixel, isSigned, isFloat);
          String pixelsPath = getLocalPixelsPath(pixelsId);
          if (DEBUG) {
            log("Series #" + s + ": id=" + pixelsId + ", path=" + pixelsPath);
          }

          // write pixels to file
          FileOutputStream out = new FileOutputStream(pixelsPath);
          int imageCount = reader.getImageCount(id);
          if (DEBUG) {
            log("Processing " + imageCount + " planes (sizeZ=" + sizeZ +
              ", sizeC=" + sizeC + ", sizeT=" + sizeT + "): ", false);
          }
          for (int j=0; j<imageCount; j++) {
            if (DEBUG) log(".", false);
            byte[] plane = reader.openBytes(id, j);
            if (swap && bytesPerPixel > 1 && !isFloat) { // swap endianness
              for (int b=0; b<plane.length; b+=bytesPerPixel) {
                for (int k=0; k<bytesPerPixel/2; k++) {
                  int i1 = b + k;
                  int i2 = b + bytesPerPixel - k - 1;
                  byte b1 = plane[i1];
                  byte b2 = plane[i2];
                  plane[i1] = b2;
                  plane[i2] = b1;
                }
              }
            }
            out.write(plane);
          }
          out.close();
          reader.close();
          if (DEBUG) log(" [done]");

          // tell OMEIS we're done
          pixelsId = finishPixels(pixelsId);
          if (DEBUG) log("finishPixels called (new id=" + pixelsId + ")");

          // get SHA1 hash for finished pixels
          String sha1 = getPixelsSHA1(pixelsId);
          if (DEBUG) log("SHA1=" + sha1);

          // inject important extra attributes into proper Pixels element
          Element pixels = (Element) pix.elementAt(s);
          pixels.setAttribute("FileSHA1", sha1);
          pixels.setAttribute("ImageServerID", "" + pixelsId);
          if (DEBUG) log("Pixel attributes injected.");
        }

        // accumulate XML into buffer
        DOMUtil.writeXML(xml, omeDoc);
        // TODO need to strip off <OME></OME> root, and readd it at
        // the end, so that all XML blocks are part of the same root.
      }
      catch (Exception exc) {
        System.err.println(
          "Error: an exception occurred reading " + path + ":");
        exc.printStackTrace();
      }
    }

    // output OME-XML to standard output
    try {
      xml.close();
      if (httpResponse) printHttpResponseHeader();
      System.out.println(new String(xml.toByteArray()));
    }
    catch (IOException exc) {
      System.err.println("Error: an exception occurred compiling OME-XML");
      exc.printStackTrace();
    }
  }

  // -- OmeisImporter API methods - OMEIS method calls --

  /** Gets path to original file corresponding to the given file ID. */
  public String getLocalFilePath(int fileId) throws OmeisException {
    // ./omeis Method=GetLocalPath FileID=fid
    String[] s;
    try { s = omeis("GetLocalPath", "FileID=" + fileId); }
    catch (IOException exc) { throw new OmeisException(exc); }
    if (s.length > 1) {
      System.err.println("Warning: ignoring " + (s.length - 1) +
        " extraneous lines in OMEIS GetLocalPath call");
    }
    else if (s.length < 1) {
      throw new OmeisException(
        "Failed to obtain local path for file ID " + fileId);
    }
    return s[0];
  }

  /**
   * Gets information about the file corresponding to the given file ID.
   * @return hashtable containing the information as key/value pairs
   */
  public Hashtable getFileInfo(int fileId) throws OmeisException {
    // ./omeis Method=FileInfo FileID=fid
    String[] s;
    try { s = omeis("FileInfo", "FileID=" + fileId); }
    catch (IOException exc) { throw new OmeisException(exc); }
    Hashtable info = new Hashtable();
    for (int i=0; i<s.length; i++) {
      int equals = s[i].indexOf("=");
      if (equals < 0) {
        System.err.println(
          "Warning: ignoring extraneous line in OMEIS FileInfo call: " + s[i]);
      }
      else {
        String key = s[i].substring(0, equals);
        String value = s[i].substring(equals + 1);
        info.put(key, value);
      }
    }
    return info;
  }

  /**
   * Instructs OMEIS to construct a new Pixels object.
   * @return pixels ID of the newly created pixels
   */
  public int newPixels(int sizeX, int sizeY, int sizeZ, int sizeC, int sizeT,
    int bytesPerPixel, boolean isSigned, boolean isFloat) throws OmeisException
  {
    // ./omeis Method=NewPixels Dims=sx,sy,sz,sc,st,Bpp IsSigned=0 IsFloat=0
    String[] s;
    try {
      s = omeis("NewPixels", "Dims=" + sizeX + "," + sizeY + "," +
        sizeZ + "," + sizeC + "," + sizeT + "," + bytesPerPixel +
        " IsSigned=" + (isSigned ? 1 : 0) + " IsFloat=" + (isFloat ? 1 : 0));
    }
    catch (IOException exc) { throw new OmeisException(exc); }
    if (s.length > 1) {
      System.err.println("Warning: ignoring " + (s.length - 1) +
        " extraneous lines in OMEIS NewPixels call output");
    }
    else if (s.length < 1) {
      throw new OmeisException("Failed to obtain pixels ID from NewPixels");
    }
    int pid = -1;
    try { pid = Integer.parseInt(s[0]); }
    catch (NumberFormatException exc) { }
    if (pid <= 0) {
      throw new OmeisException("Invalid pixels ID from NewPixels: " + s[0]);
    }
    return pid;
  }

  /** Gets whether the local system uses little-endian byte order. */
  public boolean isLittleEndian() throws OmeisException {
    // ./omeis Method=GetNativeEndian
    String[] s;
    try { s = omeis("GetNativeEndian", ""); }
    catch (IOException exc) { throw new OmeisException(exc); }
    if (s.length > 1) {
      System.err.println("Warning: ignoring " + (s.length - 1) +
        " extraneous lines in OMEIS GetLocalPath call output");
    }
    else if (s.length < 1) {
      throw new OmeisException("Failed to obtain endianness value");
    }
    if ("little".equalsIgnoreCase(s[0])) return true;
    else if ("big".equalsIgnoreCase(s[0])) return false;
    else throw new OmeisException("Invalid endianness value: " + s[0]);
  }

  /** Gets path to Pixels file corresponding to the given pixels ID. */
  public String getLocalPixelsPath(int pixelsId) throws OmeisException {
    // ./omeis Method=GetLocalPath PixelsID=pid
    String[] s;
    try { s = omeis("GetLocalPath", "PixelsID=" + pixelsId); }
    catch (IOException exc) { throw new OmeisException(exc); }
    if (s.length > 1) {
      System.err.println("Warning: ignoring " + (s.length - 1) +
        " extraneous lines in OMEIS GetLocalPath call");
    }
    else if (s.length < 1) {
      throw new OmeisException(
        "Failed to obtain local path for pixels ID " + pixelsId);
    }
    return s[0];
  }

  /**
   * Instructs OMEIS to process the Pixels file
   * corresponding to the given pixels ID.
   * @return final (possibly changed) pixels ID of the processed pixels
   */
  public int finishPixels(int pixelsId) throws OmeisException {
    // ./omeis Method=FinishPixels PixelsID=pid
    String[] s;
    try { s = omeis("FinishPixels", "PixelsID=" + pixelsId); }
    catch (IOException exc) { throw new OmeisException(exc); }
    if (s.length > 1) {
      System.err.println("Warning: ignoring " + (s.length - 1) +
        " extraneous lines in OMEIS FinishPixels call output");
    }
    else if (s.length < 1) {
      throw new OmeisException("Failed to obtain pixels ID from FinishPixels");
    }
    int pid = -1;
    try { pid = Integer.parseInt(s[0]); }
    catch (NumberFormatException exc) { }
    if (pid <= 0) {
      throw new OmeisException("Invalid pixels ID from FinishPixels: " + s[0]);
    }
    return pid;
  }

  /** Gets SHA1 hash for the pixels corresponding to the given pixels ID. */
  public String getPixelsSHA1(int pixelsId) throws OmeisException {
    // ./omeis Method=PixelsSHA1 PixelsID=pid
    String[] s;
    try { s = omeis("PixelsSHA1", "PixelsID=" + pixelsId); }
    catch (IOException exc) { throw new OmeisException(exc); }
    if (s.length > 1) {
      System.err.println("Warning: ignoring " + (s.length - 1) +
        " extraneous lines in OMEIS PixelsSHA1 call");
    }
    else if (s.length < 1) {
      throw new OmeisException(
        "Failed to obtain SHA1 for pixels ID " + pixelsId);
    }
    return s[0];
  }

  // -- Helper methods --

  /** Calls OMEIS, returning an array of strings (one per line of output). */
  private String[] omeis(String method, String params) throws IOException {
    // build OMEIS URL
    StringBuffer sb = new StringBuffer(OMEIS_PATH);
    sb.append("?Method=");
    sb.append(method);
    StringTokenizer st = new StringTokenizer(params);
    while (st.hasMoreTokens()) {
      sb.append("&");
      sb.append(st.nextToken());
    }
    String url = sb.toString();

    // call OMEIS via HTTP
    BufferedReader in = new BufferedReader(
      new InputStreamReader(new URL(url).openStream()));
    Vector v = new Vector();
    while (true) {
      String line = in.readLine();
      if (line == null) break;
      v.add(line);
    }
    String[] results = new String[v.size()];
    v.copyInto(results);
    return results;
  }

  private void log(String msg) { log(msg, true); }

  private void log(String msg, boolean nl) {
    if (nl) System.err.println(msg);
    else System.err.print(msg);
  }

  /** Prints an HTTP error response header. */
  private void printHttpErrorHeader() {
    System.out.print("Status: 500 Server Error\r\n");
    System.out.print("Content-Type: text/plain\r\n\r\n");
  }

  /** Prints an HTTP response header. */
  private void printHttpResponseHeader() {
    System.out.print("Status: 200 OK\r\n");
    System.out.print("Content-Type: text/plain\r\n\r\n");
  }

  // -- Main method --

  /**
   * Run ./omebf with a list of file IDs to import those IDs.
   * Run with the -test flag to ask Bio-Formats whether it
   * thinks it can import those files.
   */
  public static void main(String[] args) {
    OmeisImporter importer = new OmeisImporter();
    boolean test = false;
    int[] fileIds = new int[args.length];

    // parse command line arguments
    int num = 0;
    for (int i=0; i<args.length; i++) {
      if ("-test".equalsIgnoreCase(args[i])) test = true;
      else if ("-http-response".equalsIgnoreCase(args[i])) httpResponse = true;
      else {
        try {
          int q = Integer.parseInt(args[i]);
          fileIds[num++] = q;
        }
        catch (NumberFormatException exc) {
          System.err.println("Warning: ignoring parameter: " + args[i]);
        }
      }
    }
    int[] trimIds = new int[num];
    System.arraycopy(fileIds, 0, trimIds, 0, num);
    fileIds = trimIds;

    // process the IDs
    try {
      if (test) importer.testIds(fileIds);
      else importer.importIds(fileIds);
    }
    catch (Exception exc) {
      if (httpResponse) {
        importer.printHttpErrorHeader();
        System.out.println("An exception occurred while processing FileIDs:");
        exc.printStackTrace(System.out);
      }
      System.err.println("An exception occurred:");
      exc.printStackTrace();
      System.exit(1);
    }
  }

}
