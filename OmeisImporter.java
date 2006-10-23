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
import java.util.*;

/**
 * A command line utility used by OMEIS to interface with Bio-Formats.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class OmeisImporter {

  // -- OmeisImporter API methods - main functionality --

  public void testIds(int[] fileIds) {
  }

  public void importIds(int[] fileIds) {
  }

  // -- OmeisImporter API methods - OMEIS method calls --

  /** Gets path to original file corresponding to the given file ID. */
  public String getLocalFilePath(int fileId) throws OmeisException {
    // ./omeis Method=GetLocalPath FileID=fid
    String[] s;
    try { s = omeis("GetLocalPath", "FileID=" + fileId); }
    catch (IOException exc) { throw new OmeisException(exc); }
    catch (InterruptedException exc) { throw new OmeisException(exc); }
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
    catch (InterruptedException exc) { throw new OmeisException(exc); }
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
    catch (InterruptedException exc) { throw new OmeisException(exc); }
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
    // ./omeis Method=GetEndianness
    String[] s;
    try { s = omeis("GetEndianness", ""); }
    catch (IOException exc) { throw new OmeisException(exc); }
    catch (InterruptedException exc) { throw new OmeisException(exc); }
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
    catch (InterruptedException exc) { throw new OmeisException(exc); }
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
    catch (InterruptedException exc) { throw new OmeisException(exc); }
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

  // -- Helper methods --

  /** Calls OMEIS, returning an array of strings (one per line of output). */
  private String[] omeis(String method, String params)
    throws IOException, InterruptedException
  {
    // build command argument array
    StringTokenizer st = new StringTokenizer(params);
    int num = st.countTokens();
    String[] cmd = new String[2 + num];
    cmd[0] = "./omeis";
    cmd[1] = "Method=" + method;
    for (int i=2; i<cmd.length; i++) cmd[i] = st.nextToken();

    // spawn process and wait for it to complete
    Process p = Runtime.getRuntime().exec(cmd);
    p.waitFor();
    int rval = p.exitValue();
    if (rval != 0) {
      System.err.println("Warning: omeis returned error code " + rval +
        " on method call: ./omeis Method=" + method + " " + params);
    }

    // obtain program output -- normally should be buffered and read in a
    // separate thread, but all our uses of OMEIS return such a small amount
    // of data that it should not be necessary
    BufferedReader in =
      new BufferedReader(new InputStreamReader(p.getInputStream()));
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

  // -- Main method --

  public static void main(String[] args) {
    OmeisImporter importer = new OmeisImporter();
    boolean test = false;
    int[] ids = new int[args.length];

    // parse command line arguments
    int num = 0;
    for (int i=0; i<args.length; i++) {
      if ("-test".equalsIgnoreCase(args[i])) test = true;
      else {
        try {
          int q = Integer.parseInt(args[i]);
          ids[num++] = q;
        }
        catch (NumberFormatException exc) {
          System.err.println("Warning: ignoring parameter: " + args[i]);
        }
      }
    }
    int[] trimIds = new int[num];
    System.arraycopy(ids, 0, trimIds, 0, num);
    ids = trimIds;

    // process the IDs
    try {
      if (test) importer.testIds(ids);
      else importer.importIds(ids);
    }
    catch (Exception exc) {
      System.err.println("An exception occurred:");
      exc.printStackTrace();
      System.exit(1);
    }
  }

}
