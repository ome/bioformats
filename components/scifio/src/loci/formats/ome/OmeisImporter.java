/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package loci.formats.ome;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import loci.common.Constants;
import loci.common.Location;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.ChannelFiller;
import loci.formats.ChannelSeparator;
import loci.formats.FileStitcher;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.MetadataTools;
import loci.formats.services.OMEXMLService;
import ome.xml.DOMUtil;
import ome.xml.r2003fc.ome.OMENode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A command line utility used by the OME Image Server (OMEIS)
 * to interface with Bio-Formats.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/ome/OmeisImporter.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/ome/OmeisImporter.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Ilya Goldberg igg at nih.gov
 */
public class OmeisImporter {

  // -- Constants --

  /** Debugging flag. */
  private static final boolean DEBUG = false;

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
  private IFormatReader reader;

  /** Metadata object, for gathering OME-XML metadata. */
  private AbstractOMEXMLMetadata omexmlMeta;

  private boolean stitch;

  // -- Constructor --

  public OmeisImporter() {
    this(true);
  }

  public OmeisImporter(boolean stitchFiles) {
    stitch = stitchFiles;
    reader = new ChannelSeparator(new ChannelFiller());
    if (stitch) reader = new FileStitcher(reader);

    try {
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      omexmlMeta = (AbstractOMEXMLMetadata) service.createOMEXMLMetadata();
    }
    catch (DependencyException de) { }
    catch (ServiceException se) { }

    reader.setOriginalMetadataPopulated(true);
    reader.setMetadataStore(omexmlMeta);
  }

  // -- OmeisImporter API methods - main functionality --

  /** Prints out the build date for the Bio-Formats OMEIS utility. */
  public void printVersion() {
    if (http) printHttpResponseHeader();
    System.out.println("Bio-Formats OMEIS importer, built on @date@.");
  }

  /**
   * Tests whether Bio-Formats is potentially capable of importing the given
   * file IDs. Outputs the IDs it can potentially import, one group per line,
   * with elements of the each group separated by spaces.
   */
  public void testIds(int[] fileIds)
    throws OmeisException, FormatException, IOException
  {
    Arrays.sort(fileIds);

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

    Arrays.sort(fileIds);

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
    Document omeDoc = ome.getDOMElement().getOwnerDocument();
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
      String pType = pixels.getAttribute("PixelType");
      if (pType.startsWith("u")) {
        pixels.setAttribute("PixelType", pType.replace('u', 'U'));
      }
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
    String xmlString = new String(xml.toByteArray(), Constants.ENCODING);
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
      new InputStreamReader(new URL(url).openStream(), Constants.ENCODING));
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
    boolean version = false, test = false, stitch = true;
    int[] fileIds = new int[args.length];

    // parse command line arguments
    int num = 0;
    for (int i=0; i<args.length; i++) {
      if ("-version".equalsIgnoreCase(args[i])) version = true;
      else if ("-test".equalsIgnoreCase(args[i])) test = true;
      else if ("-http-response".equalsIgnoreCase(args[i])) http = true;
      else if ("-nostitch".equalsIgnoreCase(args[i])) stitch = false;
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

    OmeisImporter importer = new OmeisImporter(stitch);

    // process the IDs
    try {
      if (version) importer.printVersion();
      else if (test) importer.testIds(fileIds);
      else importer.importIds(fileIds);
    }
    catch (Throwable t) {
      // NB: We really do want to catch all exception types here,
      // to redirect output properly for the OME server.
      if (http) {
        importer.printHttpErrorHeader();
        System.out.println("An exception occurred while processing FileIDs:");
        t.printStackTrace(System.out);
      }
      System.err.println("An exception occurred:");
      t.printStackTrace();
      System.exit(1);
    }
  }

}
