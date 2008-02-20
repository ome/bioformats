//
// ConfigurationFiles.java
//

/*
LOCI software automated test suite for TestNG. Copyright (C) 2007-@year@
Melissa Linkert and Curtis Rueden. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the UW-Madison LOCI nor the names of its
    contributors may be used to endorse or promote products derived from
    this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE UW-MADISON LOCI ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package loci.tests.testng;

import java.io.IOException;
import java.util.*;
import loci.formats.*;

/**
 * Stores data from a set of config files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/tests/testng/ConfigurationFiles.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/tests/testng/ConfigurationFiles.java">SVN</a></dd></dl>
 */
public class ConfigurationFiles {

  // -- Fields --

  private Hashtable entries = new Hashtable();

  private Vector parsedFiles = new Vector();

  private int currentSeries = 0;

  // -- Static ConfigurationFiles API methods --

  public static ConfigurationFiles newInstance() {
    return new ConfigurationFiles();
  }

  // -- ConfigurationFiles API methods --

  public int numFiles() { return entries.size(); }

  public boolean isParsed(String file) { return parsedFiles.contains(file); }

  public boolean initialized(String id) {
    return entries.containsKey(id) && currentSeries <= getNumSeries(id);
  }

  /**
   * Parses data from a new config file and stores it for later use.
   * Does not perform any checks - it is assumed that the file is properly
   * written.
   */
  public void addFile(String id) throws IOException {
    RandomAccessStream ras = new RandomAccessStream(id);
    Location l = new Location(id);
    if (parsedFiles.contains(l.getAbsolutePath())) return;
    else parsedFiles.add(l.getAbsolutePath());
    byte[] b = new byte[(int) ras.length()];
    ras.read(b);
    ras.close();
    StringTokenizer st = new StringTokenizer(new String(b), "\n");
    while (st.hasMoreTokens()) {
      String line = st.nextToken();
      if (!line.startsWith("#") && line.trim().length() > 0) {
        ConfigEntry entry = new ConfigEntry();

        String file = line.substring(1, line.lastIndexOf("\""));
        Location newLocation = new Location(l.getParent(), file);
        file = newLocation.getAbsolutePath();
        newLocation = null;
        line = line.substring(line.lastIndexOf("\"") + 2);

        // first check if 'test' is set to false; if so, don't parse anything
        // else

        String testValue = line.substring(line.indexOf("test=") + 5).trim();
        if (testValue.equals("false")) {
          entry.test = false;
        }
        else {
          entry.test = true;

          int ndx = line.indexOf("total_series=") + 13;
          int nSeries =
            Integer.parseInt(line.substring(ndx, line.indexOf(" ", ndx)));
          entry.numSeries = nSeries;

          entry.dimensions = new int[nSeries][5];
          entry.orders = new String[nSeries];
          entry.interleaved = new boolean[nSeries];
          entry.rgb = new boolean[nSeries];
          entry.thumbs = new int[nSeries][2];
          entry.type = new int[nSeries];
          entry.littleEndian = new boolean[nSeries];
          entry.md5 = new String[nSeries];

          for (int i=0; i<nSeries; i++) {
            ndx = line.indexOf("[series=" + i);
            String s = line.substring(ndx, line.indexOf("]", ndx));
            ndx = s.indexOf("x") + 2;
            entry.dimensions[i][0] =
              Integer.parseInt(s.substring(ndx, s.indexOf(" ", ndx)));
            ndx = s.indexOf("y") + 2;
            entry.dimensions[i][1] =
              Integer.parseInt(s.substring(ndx, s.indexOf(" ", ndx)));
            ndx = s.indexOf("z") + 2;
            entry.dimensions[i][2] =
              Integer.parseInt(s.substring(ndx, s.indexOf(" ", ndx)));
            ndx = s.indexOf("c") + 2;
            entry.dimensions[i][3] =
              Integer.parseInt(s.substring(ndx, s.indexOf(" ", ndx)));
            ndx = s.indexOf("t") + 2;
            entry.dimensions[i][4] =
              Integer.parseInt(s.substring(ndx, s.indexOf(" ", ndx)));

            ndx = s.indexOf("order") + 6;
            entry.orders[i] = s.substring(ndx, s.indexOf(" ", ndx));
            ndx = s.indexOf("interleave") + 11;
            entry.interleaved[i] =
              s.substring(ndx, s.indexOf(" ", ndx)).equals("true");
            ndx = s.indexOf("rgb") + 4;
            entry.rgb[i] = s.substring(ndx, s.indexOf(" ", ndx)).equals("true");
            ndx = s.indexOf("thumbx") + 7;
            entry.thumbs[i][0] =
              Integer.parseInt(s.substring(ndx, s.indexOf(" ", ndx)));
            ndx = s.indexOf("thumby") + 7;
            entry.thumbs[i][1] =
              Integer.parseInt(s.substring(ndx, s.indexOf(" ", ndx)));
            ndx = s.indexOf("type") + 5;
            entry.type[i] = FormatTools.pixelTypeFromString(
              s.substring(ndx, s.indexOf(" ", ndx)));
            ndx = s.indexOf("little") + 7;
            entry.littleEndian[i] =
              s.substring(ndx, s.indexOf(" ", ndx)).equals("true");
            ndx = s.indexOf("md5") + 4;
            entry.md5[i] = s.substring(ndx);
          }

          ndx = line.indexOf("access=") + 7;
          entry.access = Float.parseFloat(line.substring(ndx,
            line.indexOf(" ", ndx)));
          ndx = line.indexOf("mem=") + 4;
          entry.mem = (int) (Long.parseLong(line.substring(ndx,
            line.indexOf(" ", ndx))) >> 20);
        }
        entries.put(file, entry);
      }
    }
    l = null;
    System.gc();
  }

  public void setSeries(String id, int no) {
    if (no < getNumSeries(id)) currentSeries = no;
  }

  public int getNumSeries(String id) {
    if (!entries.containsKey(id)) return 0;
    return ((ConfigEntry) entries.get(id)).numSeries;
  }

  public int getWidth(String id) {
    if (!initialized(id)) return 0;
    return ((ConfigEntry) entries.get(id)).dimensions[currentSeries][0];
  }

  public int getHeight(String id) {
    if (!initialized(id)) return 0;
    return ((ConfigEntry) entries.get(id)).dimensions[currentSeries][1];
  }

  public int getZ(String id) {
    if (!initialized(id)) return 0;
    return ((ConfigEntry) entries.get(id)).dimensions[currentSeries][2];
  }

  public int getC(String id) {
    if (!initialized(id)) return 0;
    return ((ConfigEntry) entries.get(id)).dimensions[currentSeries][3];
  }

  public int getT(String id) {
    if (!initialized(id)) return 0;
    return ((ConfigEntry) entries.get(id)).dimensions[currentSeries][4];
  }

  public String getDimOrder(String id) {
    if (!initialized(id)) return null;
    return ((ConfigEntry) entries.get(id)).orders[currentSeries];
  }

  public boolean isInterleaved(String id) {
    if (!initialized(id)) return false;
    return ((ConfigEntry) entries.get(id)).interleaved[currentSeries];
  }

  public boolean isRGB(String id) {
    if (!initialized(id)) return false;
    return ((ConfigEntry) entries.get(id)).rgb[currentSeries];
  }

  public int getThumbX(String id) {
    if (!initialized(id)) return 0;
    return ((ConfigEntry) entries.get(id)).thumbs[currentSeries][0];
  }

  public int getThumbY(String id) {
    if (!initialized(id)) return 0;
    return ((ConfigEntry) entries.get(id)).thumbs[currentSeries][1];
  }

  public int getPixelType(String id) {
    if (!initialized(id)) return -1;
    return ((ConfigEntry) entries.get(id)).type[currentSeries];
  }

  public boolean isLittleEndian(String id) {
    if (!initialized(id)) return false;
    return ((ConfigEntry) entries.get(id)).littleEndian[currentSeries];
  }

  public String getMD5(String id) {
    if (!initialized(id)) return null;
    return ((ConfigEntry) entries.get(id)).md5[currentSeries];
  }

  public float getTimePerPlane(String id) {
    if (!initialized(id)) return 0;
    return ((ConfigEntry) entries.get(id)).access;
  }

  public int getFileSize(String id) {
    if (!initialized(id)) return 0;
    return ((ConfigEntry) entries.get(id)).mem;
  }

  public boolean testFile(String id) {
    if (!entries.containsKey(id)) return true;
    return ((ConfigEntry) entries.get(id)).test;
  }

  // -- Helper classes --

  /** Helper class representing a configuration entry for a dataset. */
  public class ConfigEntry {
    private int numSeries;
    private int[][] dimensions;
    private String[] orders;
    private boolean[] interleaved;
    private boolean[] rgb;
    private int[][] thumbs;
    private int[] type;
    private boolean[] littleEndian;
    private String[] md5;
    private float access;
    private int mem;
    private boolean test;
  }

}
