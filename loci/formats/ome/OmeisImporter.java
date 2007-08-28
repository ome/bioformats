//
// OmeisImporter.java
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

package loci.formats.ome;

import java.io.*;
import java.net.URL;
import java.util.*;
import loci.formats.*;
import org.openmicroscopy.xml.DOMUtil;
import org.openmicroscopy.xml.OMENode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A command line utility used by OMEIS to interface with Bio-Formats.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/ome/OmeisImporter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/ome/OmeisImporter.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Ilya Goldberg igg at nih.gov
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
  private static boolean http = false;

  // -- Fields --

  /** Reader for handling file formats. */
  private FileStitcher reader;

  /** Metadata object, for gathering OME-XML metadata. */
  private OMEXMLMetadata omexmlMeta;

  // -- Constructor --

  public OmeisImporter() {
    reader = new FileStitcher(new ChannelSeparator());
    omexmlMeta = new OMEXMLMetadata();
    reader.setOriginalMetadataPopulated(true);
    reader.setMetadataStore(omexmlMeta);
  }

  // -- OmeisImporter API methods - main functionality --

  /**
   * Tests whether Bio-Formats is potentially capable of importing the given
   * file IDs. Outputs the IDs it can potentially import, one group per line,
   * with elements of the each group separated by spaces.
   */
  public void testIds(int[] fileIds)
    throws OmeisException, FormatException, IOException
  {
    // set up file path mappings
    String[] ids = new String[fileIds.length];
    for (int i=0; i<fileIds.length; i++) {
      Hashtable fileInfo = getFileInfo(fileIds[i]);
      ids[i] = (String) fileInfo.get("Name");
      String path = getLocalFilePath(fileIds[i]);
      Location.mapId(ids[i], path);
    }

    // check types and groups
    if (http) printHttpResponseHeader();
    boolean[] done = new boolean[fileIds.length];
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<fileIds.length; i++) {
      if (done[i]) continue; // already part of another group
      if (ids[i] == null) continue; // invalid id
      if (!reader.isThisType(ids[i])) continue; // unknown format
      reader.setId(ids[i]);
      String[] files = reader.getUsedFiles();

      if (files == null) continue; // invalid files list
      sb.setLength(0);

      for (int j=files.length - 1; j>=0; j--) {
        for (int ii=i; ii<fileIds.length; ii++) {
          if (files[j] == null) {
            log("Warning: FileID " + fileIds[ii] + " ('" +
              ids[ii] + "') has null used file #" + j);
          }
          else if (files[j].equals(ids[ii])) {
            if (done[ii]) {
              log("Warning: FileID " + fileIds[ii] + " ('" +
                ids[ii] + "') already belongs to a group");
            }
            done[ii] = true;
            if (j < files.length - 1) sb.append(" ");
            sb.append(fileIds[ii]);
            break;
          }
        }
      }
      System.out.println(sb.toString());
    }
  }

  /**
   * Attempts to import the given file IDs using Bio-Formats, as a single
   * group. Pixels are saved to the pixels file designated by OMEIS, and an
   * OME-XML metadata block describing the successfully imported data is
   * dumped to standard output.
   */
  public void importIds(int[] fileIds)
    throws OmeisException, FormatException, IOException
  {
    boolean doLittle = isLittleEndian();

    // set up file path mappings
    String[] ids = new String[fileIds.length];
    for (int i=0; i<fileIds.length; i++) {
      Hashtable fileInfo = getFileInfo(fileIds[i]);
      ids[i] = (String) fileInfo.get("Name");
      String path = getLocalFilePath(fileIds[i]);
      Location.mapId(ids[i], path);
    }

    // read file group
    String id = ids[0];
    String path = Location.getMappedId(id);
    if (DEBUG) log("Reading file '" + id + "' --> " + path);

    // verify that all given file IDs were grouped by the reader
    reader.setId(id);
    String[] used = reader.getUsedFiles();
    if (used == null) {
      throw new FormatException("Invalid file list for " + path);
    }
    if (used.length != ids.length) {
      throw new FormatException("File list length mismatch for " + path +
        ": used=" + a2s(used) + "; ids=" + a2s(ids));
    }

    boolean[] done = new boolean[ids.length];
    int numLeft = ids.length;
    for (int i=0; i<used.length; i++) {
      for (int j=0; j<ids.length; j++) {
        if (done[j]) continue;
        if (used[i].equals(ids[j])) {
          done[j] = true;
          numLeft--;
          break;
        }
      }
    }
    if (numLeft > 0) {
      throw new FormatException(
        "File list does not correspond to ID list for " + path);
    }

    int seriesCount = reader.getSeriesCount();

    // get DOM and Pixels elements for the file's OME-XML metadata
    OMENode ome = (OMENode) omexmlMeta.getRoot();
    Document omeDoc = null;
    try {
      omeDoc = ome.getOMEDocument(false);
    }
    catch (javax.xml.transform.TransformerException exc) {
      throw new FormatException(exc);
    }
    catch (org.xml.sax.SAXException exc) {
      throw new FormatException(exc);
    }
    catch (javax.xml.parsers.ParserConfigurationException exc) {
      throw new FormatException(exc);
    }
    Vector pix = DOMUtil.findElementList("Pixels", omeDoc);
    if (pix.size() != seriesCount) {
      throw new FormatException("Pixels element count (" +
        pix.size() + ") does not match series count (" +
        seriesCount + ") for '" + id + "'");
    }
    if (DEBUG) log(seriesCount + " series detected.");

    for (int s=0; s<seriesCount; s++) {
      reader.setSeries(s);

      // gather pixels information for this series
      int sizeX = reader.getSizeX();
      int sizeY = reader.getSizeY();
      int sizeZ = reader.getSizeZ();
      int sizeC = reader.getSizeC();
      int sizeT = reader.getSizeT();
      int pixelType = reader.getPixelType();
      int bytesPerPixel;
      boolean isSigned, isFloat;
      switch (pixelType) {
        case FormatTools.INT8:
          bytesPerPixel = 1;
          isSigned = true;
          isFloat = false;
          break;
        case FormatTools.UINT8:
          bytesPerPixel = 1;
          isSigned = false;
          isFloat = false;
          break;
        case FormatTools.INT16:
          bytesPerPixel = 2;
          isSigned = true;
          isFloat = false;
          break;
        case FormatTools.UINT16:
          bytesPerPixel = 2;
          isSigned = false;
          isFloat = false;
          break;
        case FormatTools.INT32:
          bytesPerPixel = 4;
          isSigned = true;
          isFloat = false;
          break;
        case FormatTools.UINT32:
          bytesPerPixel = 4;
          isSigned = false;
          isFloat = false;
          break;
        case FormatTools.FLOAT:
          bytesPerPixel = 4;
          isSigned = true;
          isFloat = true;
          break;
        case FormatTools.DOUBLE:
          bytesPerPixel = 8;
          isSigned = true;
          isFloat = true;
          break;
        default:
          throw new FormatException("Unknown pixel type for '" +
            id + "' series #" + s + ": " + pixelType);
      }
      boolean little = reader.isLittleEndian();
      boolean swap = doLittle != little && bytesPerPixel > 1 && !isFloat;

      // ask OMEIS to allocate new pixels file
      int pixelsId = newPixels(sizeX, sizeY, sizeZ, sizeC, sizeT,
        bytesPerPixel, isSigned, isFloat);
      String pixelsPath = getLocalPixelsPath(pixelsId);
      if (DEBUG) {
        log("Series #" + s + ": id=" + pixelsId + ", path=" + pixelsPath);
      }

      // write pixels to file
      FileOutputStream out = new FileOutputStream(pixelsPath);
      int imageCount = reader.getImageCount();
      if (DEBUG) {
        log("Processing " + imageCount + " planes (sizeZ=" + sizeZ +
          ", sizeC=" + sizeC + ", sizeT=" + sizeT + "): ");
      }
      // OMEIS expects XYZCT order --
      // interleaved RGB files will be handled a bit more slowly due to this
      // ordering (ChannelSeparator must read each plane three times), but
      // caching performed by the OS helps some
      for (int t=0; t<sizeT; t++) {
        for (int c=0; c<sizeC; c++) {
          for (int z=0; z<sizeZ; z++) {
            int ndx = reader.getIndex(z, c, t);
            if (DEBUG) {
              log("Reading plane #" + ndx +
                ": z=" + z + ", c=" + c + ", t=" + t);
            }
            byte[] plane = reader.openBytes(ndx);
            if (swap) { // swap endianness
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
        }
      }
      out.close();
      if (DEBUG) log("[done]");

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
      pixels.setAttribute("DimensionOrder", "XYZCT"); // ignored anyway
      if (DEBUG) log("Pixel attributes injected.");
    }

    reader.close();

    // accumulate XML into buffer
    ByteArrayOutputStream xml = new ByteArrayOutputStream();
    try {
      DOMUtil.writeXML(xml, omeDoc);
    }
    catch (javax.xml.transform.TransformerException exc) {
      throw new FormatException(exc);
    }

    // output OME-XML to standard output
    xml.close();
    String xmlString = new String(xml.toByteArray());
    if (DEBUG) log(xmlString);
    if (http) printHttpResponseHeader();
    System.out.println(xmlString);
  }

  // -- OmeisImporter API methods - OMEIS method calls --

  /** Gets path to original file corresponding to the given file ID. */
  public String getLocalFilePath(int fileId) throws OmeisException {
    // ./omeis Method=GetLocalPath FileID=fid
    String[] s;
    try { s = omeis("GetLocalPath", "FileID=" + fileId); }
    catch (IOException exc) { throw new OmeisException(exc); }
    if (s.length > 1) {
      log("Warning: ignoring " + (s.length - 1) +
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
        log("Warning: ignoring extraneous line in OMEIS FileInfo call: " +
          s[i]);
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
      log("Warning: ignoring " + (s.length - 1) +
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
      log("Warning: ignoring " + (s.length - 1) +
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
      log("Warning: ignoring " + (s.length - 1) +
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
      log("Warning: ignoring " + (s.length - 1) +
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
      log("Warning: ignoring " + (s.length - 1) +
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

  /** Prints a debugging message. */
  private void log(String msg) {
    System.err.println("Bio-Formats: " + msg);
  }

  /** Gets a printable version of the given array of strings. */
  private String a2s(String[] s) {
    StringBuffer sb = new StringBuffer();
    if (s == null) return "null";
    sb.append("[");
    if (s.length > 0) sb.append(s[0]);
    for (int i=1; i<s.length; i++) {
      sb.append(" ");
      sb.append(s[i]);
    }
    sb.append("]");
    return sb.toString();
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
      else if ("-http-response".equalsIgnoreCase(args[i])) http = true;
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
      // NB: We really do want to catch all exception types here,
      // to redirect output properly for the OME server.
      if (http) {
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
