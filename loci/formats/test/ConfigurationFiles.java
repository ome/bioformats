//
// ConfigurationFiles.java
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

package loci.formats.test;

import java.io.IOException;
import java.util.*;
import loci.formats.*;

/** Stores data from a set of config files. */
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
