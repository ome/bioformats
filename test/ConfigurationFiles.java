//
// ConfigurationFiles.java
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

package loci.formats.test;

import java.io.IOException;
import java.util.*;
import loci.formats.*;

/** Stores data from a set of config files */
public class ConfigurationFiles {
  private Hashtable numSeries = new Hashtable();
  private Hashtable dimensions = new Hashtable();
  private Hashtable dimOrders = new Hashtable();
  private Hashtable interleaved = new Hashtable();
  private Hashtable rgb = new Hashtable();
  private Hashtable thumbDims = new Hashtable();
  private Hashtable pixelType = new Hashtable();
  private Hashtable littleEndian = new Hashtable();
  private Hashtable readTime = new Hashtable();
  private Hashtable memUsage = new Hashtable();
  private Hashtable doTest = new Hashtable();

  public Vector parsedFiles = new Vector();

  int currentSeries = 0;

  public static ConfigurationFiles newInstance() {
    return new ConfigurationFiles();
  }

  public int numFiles() { return numSeries.size(); }

  public boolean initialized(String id) {
    return numSeries.containsKey(id) && getNumSeries(id) <= currentSeries;
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
        String file = line.substring(1, line.lastIndexOf("\""));
        Location newLocation = new Location(l.getParent(), file);
        file = newLocation.getAbsolutePath();
        newLocation = null;
        line = line.substring(line.lastIndexOf("\"") + 2);

        // first check if 'test' is set to false; if so, don't parse anything
        // else
      
        String testValue = line.substring(line.indexOf("test=") + 5).trim();
        if (testValue.equals("false")) {
          doTest.put(file, new Boolean(false));
        }
        else {
          doTest.put(file, new Boolean(true));

          int ndx = line.indexOf("total_series=") + 13;
          int nSeries = 
            Integer.parseInt(line.substring(ndx, line.indexOf(" ", ndx)));
          numSeries.put(file, new Integer(nSeries));

          int[][] dims = new int[nSeries][5];
          String[] orders = new String[nSeries];
          boolean[] interleave = new boolean[nSeries];
          boolean[] isRGB = new boolean[nSeries];
          int[][] thumbs = new int[nSeries][2];
          int[] ptype = new int[nSeries];
          boolean[] little = new boolean[nSeries];

          for (int i=0; i<nSeries; i++) {
            ndx = line.indexOf("[series=" + i);
            String s = line.substring(ndx, line.indexOf("]", ndx));
            ndx = s.indexOf("x") + 2;
            dims[i][0] = 
              Integer.parseInt(s.substring(ndx, s.indexOf(" ", ndx)));
            ndx = s.indexOf("y") + 2;
            dims[i][1] = 
              Integer.parseInt(s.substring(ndx, s.indexOf(" ", ndx)));
            ndx = s.indexOf("z") + 2;
            dims[i][2] = 
              Integer.parseInt(s.substring(ndx, s.indexOf(" ", ndx)));
            ndx = s.indexOf("c") + 2;
            dims[i][3] = 
              Integer.parseInt(s.substring(ndx, s.indexOf(" ", ndx)));
            ndx = s.indexOf("t") + 2;
            dims[i][4] = 
              Integer.parseInt(s.substring(ndx, s.indexOf(" ", ndx)));
          
            ndx = s.indexOf("order") + 6;
            orders[i] = s.substring(ndx, s.indexOf(" ", ndx));
            ndx = s.indexOf("interleave") + 11;
            interleave[i] = 
              s.substring(ndx, s.indexOf(" ", ndx)).equals("true");
            ndx = s.indexOf("rgb") + 4;
            isRGB[i] = s.substring(ndx, s.indexOf(" ", ndx)).equals("true");
            ndx = s.indexOf("thumbx") + 7;
            thumbs[i][0] = 
              Integer.parseInt(s.substring(ndx, s.indexOf(" ", ndx)));
            ndx = s.indexOf("thumby") + 7;
            thumbs[i][1] = 
              Integer.parseInt(s.substring(ndx, s.indexOf(" ", ndx)));
            ndx = s.indexOf("type") + 5;
            ptype[i] = FormatReader.pixelTypeFromString(
              s.substring(ndx, s.indexOf(" ", ndx)));
            ndx = s.indexOf("little") + 7;
            little[i] = s.substring(ndx).equals("true");
          }

          dimensions.put(file, dims);
          dimOrders.put(file, orders);
          interleaved.put(file, interleave);
          rgb.put(file, isRGB);
          thumbDims.put(file, thumbs);
          pixelType.put(file, ptype);
          littleEndian.put(file, little);
          
          ndx = line.indexOf("access=") + 7;
          readTime.put(file, new Float(line.substring(ndx, 
            line.indexOf(" ", ndx))));
          ndx = line.indexOf("mem=") + 4;
          long mem = Long.parseLong(line.substring(ndx, 
            line.indexOf(" ", ndx)));
          memUsage.put(file, new Integer((int) (mem >> 20)));
        }
      }
    }
    l = null;
    System.gc();
  }

  public void setSeries(String id, int no) {
    if (no < getNumSeries(id)) currentSeries = no; 
  }

  public int getNumSeries(String id) { 
    if (!numSeries.contains(id)) return 0;
    return ((Integer) numSeries.get(id)).intValue();
  }
    
  public int getWidth(String id) {
    if (!initialized(id)) return 0;
    return ((int[][]) dimensions.get(id))[currentSeries][0];
  }
    
  public int getHeight(String id) { 
    if (!initialized(id)) return 0;
    return ((int[][]) dimensions.get(id))[currentSeries][1];
  }

  public int getZ(String id) { 
    if (!initialized(id)) return 0;
    return ((int[][]) dimensions.get(id))[currentSeries][2];
  }
    
  public int getC(String id) { 
    if (!initialized(id)) return 0;
    return ((int[][]) dimensions.get(id))[currentSeries][3];
  }
    
  public int getT(String id) { 
    if (!initialized(id)) return 0;
    return ((int[][]) dimensions.get(id))[currentSeries][4];
  }
    
  public String getDimOrder(String id) { 
    if (!initialized(id)) return null;
    return ((String[]) dimOrders.get(id))[currentSeries];
  }

  public boolean isInterleaved(String id) {
    if (!initialized(id)) return false;
    return ((boolean[]) interleaved.get(id))[currentSeries]; 
  }

  public boolean isRGB(String id) {
    if (!initialized(id)) return false;
    return ((boolean[]) rgb.get(id))[currentSeries];
  }

  public int getThumbX(String id) {
    if (!initialized(id)) return 0;
    return ((int[][]) thumbDims.get(id))[currentSeries][0];
  }

  public int getThumbY(String id) {
    if (!initialized(id)) return 0;
    return ((int[][]) thumbDims.get(id))[currentSeries][1];
  }

  public int getPixelType(String id) {
    if (!initialized(id)) return -1;
    return ((int[]) pixelType.get(id))[currentSeries]; 
  }

  public boolean isLittleEndian(String id) {
    if (!initialized(id)) return false;
    return ((boolean[]) littleEndian.get(id))[currentSeries];
  }

  public float getTimePerPlane(String id) {
    if (!initialized(id)) return 0;
    return ((Float) readTime.get(id)).floatValue();
  }

  public int getFileSize(String id) {
    if (!initialized(id)) return 0;
    return ((Float) memUsage.get(id)).intValue();
  }

  public boolean testFile(String id) {
    if (!doTest.containsKey(id)) return true;
    return ((Boolean) doTest.get(id)).booleanValue(); 
  }
}
