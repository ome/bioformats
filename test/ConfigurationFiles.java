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
  Hashtable numSeries = new Hashtable();
  Hashtable dimensions = new Hashtable();
  Hashtable dimOrders = new Hashtable();
  Hashtable interleaved = new Hashtable();
  Hashtable rgb = new Hashtable();
  Hashtable thumbDims = new Hashtable();
  Hashtable pixelType = new Hashtable();
  Hashtable littleEndian = new Hashtable();
  Hashtable readTime = new Hashtable();
  Hashtable memUsage = new Hashtable();
  Hashtable doTest = new Hashtable();

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
    RandomAccessStream s = new RandomAccessStream(id);
    Location l = new Location(id);
    byte[] b = new byte[(int) s.length()];
    s.read(b);
    StringTokenizer st = new StringTokenizer(new String(b), "\n");
    while (st.hasMoreTokens()) {
      String line = st.nextToken();
      if (!line.startsWith("#") && line.trim().length() > 0) {
        // file name should be enclosed in double quotes
        String file = line.substring(1, line.indexOf("\"", 2));
        line = line.substring(line.indexOf("\"", 2) + 2);
        file = new Location(l.getParent(), file).getAbsolutePath();
        StringTokenizer spaces = new StringTokenizer(line, " ");
        int nSeries = Integer.parseInt(spaces.nextToken());
        numSeries.put(file, new Integer(nSeries));
        int[][] dims = new int[nSeries][5];
        String[] orders = new String[nSeries];
        boolean[] interleave = new boolean[nSeries];
        boolean[] isRGB = new boolean[nSeries];
        int[][] thumbs = new int[nSeries][2];
        int[] ptype = new int[nSeries];
        boolean[] little = new boolean[nSeries];

        for (int i=0; i<nSeries; i++) {
          dims[i][0] = Integer.parseInt(spaces.nextToken());  
          dims[i][1] = Integer.parseInt(spaces.nextToken());  
          dims[i][2] = Integer.parseInt(spaces.nextToken());  
          dims[i][3] = Integer.parseInt(spaces.nextToken());  
          dims[i][4] = Integer.parseInt(spaces.nextToken());  
          orders[i] = spaces.nextToken();
          interleave[i] = spaces.nextToken().equals("true");
          isRGB[i] = spaces.nextToken().equals("true");
          thumbs[i][0] = Integer.parseInt(spaces.nextToken());
          thumbs[i][1] = Integer.parseInt(spaces.nextToken());
          ptype[i] = FormatReader.pixelTypeFromString(spaces.nextToken());
          little[i] = spaces.nextToken().equals("true");
        }
        dimensions.put(file, dims);
        dimOrders.put(file, orders);
        interleaved.put(file, interleave);
        rgb.put(file, isRGB);
        thumbDims.put(file, thumbs);
        pixelType.put(file, ptype);
        littleEndian.put(file, little);
        readTime.put(file, new Float(spaces.nextToken()));
        memUsage.put(file, new Integer(spaces.nextToken()));
        doTest.put(file, new Boolean(spaces.nextToken()));
      }
    }
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
    return ((float[]) readTime.get(id))[currentSeries];
  }

  public int getFileSize(String id) {
    if (!initialized(id)) return 0;
    return ((int[]) memUsage.get(id))[currentSeries];
  }

  public boolean testFile(String id) {
    if (!initialized(id)) return true;
    return ((Boolean) doTest.get(id)).booleanValue(); 
  }
}
