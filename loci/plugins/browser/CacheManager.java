//
// CacheManager.java
//

/*
LOCI 4D Data Browser package for quick browsing of 4D datasets in ImageJ.
Copyright (C) 2005-@year@ Christopher Peterson, Francis Wong, Curtis Rueden
and Melissa Linkert.

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

package loci.plugins.browser;

import ij.process.ImageProcessor;
import java.util.Arrays;
import loci.formats.*;

public class CacheManager implements Runnable {

  // -- Constants --

  public static final int Z_AXIS = 0x01;
  public static final int T_AXIS = 0x02;
  public static final int C_AXIS = 0x04;
  
  public static final int CROSS_MODE = 0x10;
  public static final int RECT_MODE = 0x20;
  
  public static final int FORWARD_FIRST = 0x0100;
  public static final int SURROUND_FIRST = 0x0200;
  
  public static final boolean DEBUG = true;

  // -- Fields --

  private ImageProcessor [] cache;
  private FormatReader read;
  private int axis;
  private int oldAxis;
  private int mode;
  private int oldMode;
  private int strategy;
  private int oldStrategy;
  private int backZ,backT,backC;
  private int oldBackZ,oldBackT,oldBackC;
  private int forwardZ,forwardT,forwardC;
  private int oldForwardZ,oldForwardT,oldForwardC;
  private int z,t,c;
  private int oldZ,oldT,oldC;
  private int sizeZ,sizeT,sizeC;
  private String fileName;
  private boolean quit;
  private boolean loop;

  protected int [] loadList;

  // -- Constructors --

  public CacheManager(int size, 
    FormatReader read, String fileName) 
  {
    this(0,size,read,fileName, T_AXIS, CROSS_MODE);
  }
  
  public CacheManager(int back, int forward,
    FormatReader read, String fileName, int axis, int mode)
  {
    this(0,0,0,back,forward,back,forward,back,
      forward,read,fileName,axis,mode,FORWARD_FIRST);
  }
  
  public CacheManager(int z, int t, int c, int backZ, int forwardZ,
    int backT, int forwardT, int backC, int forwardC,
    FormatReader read, String fileName, int axis, int mode, int strategy)
  {
    loop = true;
    this.z = z;
    this.t = t;
    this.c = c;
    oldZ = z;
    oldT = t;
    oldC = c;
    quit = false;
    this.fileName = fileName;
    this.read = read;
    try {
      sizeZ = read.getSizeZ(fileName);
      sizeT = read.getSizeT(fileName);
      sizeC = read.getSizeC(fileName);
      cache = new ImageProcessor[read.getImageCount(fileName)];
    }
    catch (Exception exc) {
      if (DEBUG) System.out.println("Error reading size of file.");
    }
    oldAxis = axis;
    oldMode = mode;
    oldBackZ = backZ;
    oldForwardZ = forwardZ;
    oldBackT = backT;
    oldForwardT = forwardT;
    oldBackZ = backZ;
    oldForwardZ = forwardZ;
    this.backZ = backZ;
    this.forwardZ = forwardZ;
    this.backT = backT;
    this.forwardT = forwardT;
    this.backC = backC;
    this.forwardC = forwardC;
    this.axis = axis;
    this.mode = mode;
    this.strategy = strategy;
    this.oldStrategy = strategy;
    updateCache();
  }
  
  // -- CacheManager API methods --
  
  public void setAxis(int axis) {
    quit = true;
    oldAxis = this.axis;
    this.axis = axis;
    updateCache();
  }
  
  public void setMode(int mode) {
    quit = true;
    oldMode = this.mode;
    this.mode = mode;
    updateCache();
  }
  
  public void setSize(int back, int forward) {
    setSize(back,forward,back,forward,back,forward);
  }
  
  public void setSize(int backZ, int forwardZ, int backT,
    int forwardT, int backC, int forwardC)
  {
    quit = true;
    oldBackZ = this.backZ;
    this.backZ = backZ;
    oldForwardZ = this.forwardZ;
    this.forwardZ = forwardZ;
    oldBackT = this.backT;
    this.backT = backT;
    oldForwardT = this.forwardT;
    this.forwardT = forwardT;
    oldBackC = this.backC;
    this.backC = backC;
    oldForwardC = this.forwardC;
    this.forwardC = forwardC;
    updateCache();
  }
  
  public int getSize() {
    int count = 0;
    for (int i = 0;i<cache.length;i++) {
      if( cache[i] == null) continue;
      count++;
    }
    return count;
  }
  
  public int getSlice() {
    int index;
    try {
      index = read.getIndex(fileName,z,c,t);
    }
    catch (Exception exc) { return -1;}
    return index;
  }
  
  public ImageProcessor getSlice(int index) {
    if(DEBUG) System.out.println("GETTING SLICE: " + index);

    int[] coords;
    try {
      coords = read.getZCTCoords(fileName,index);
      z = coords[0];
      c = coords[1];
      t = coords[2];
    }
    catch (Exception exc) { if(DEBUG) exc.printStackTrace();}
    
    ImageProcessor result = cache[index];
    if (Arrays.binarySearch(loadList,index) >= 0) {
      if(result != null) {
        if(DEBUG) System.out.println("Slice found in cache.");      
        return result;
      }
      else {
        if(DEBUG) System.out.println("Slice not found in cache. LOADING...");  
        result = ImagePlusWrapper.getImageProcessor(fileName,read,index);
        cache[index] = result;
        return result;
      }
    }
    else {
      result = ImagePlusWrapper.getImageProcessor(fileName,read,index);
      cache[index] = result;

      updateCache();
      return result;
    }
  }
  
  public ImageProcessor getSlice(int z, int t, int c) {
    int index;
    try {
      index = read.getIndex(fileName,z,c,t);
    }
    catch (Exception exc) {
      if (DEBUG) exc.printStackTrace();
      return null;
    }
    return getSlice(index);
  }
  
  public ImageProcessor getTempSlice(int index) {
    if (cache[index] != null) return cache[index];
    return ImagePlusWrapper.getImageProcessor(fileName,read,index);
  }
  
  public ImageProcessor getTempSlice(int z, int t, int c) {
    int index;
    try {
      index = read.getIndex(fileName,z,c,t);
    }
    catch (Exception exc) {
      if (DEBUG) exc.printStackTrace();
      return null;
    }
    return getTempSlice(index);
  }
  
  private int [] getToCache(boolean old) {
    int [] result = null;
    int z,t,c,backZ,forwardZ,backT,forwardT,backC,forwardC,axis,mode;
    if (old) {
      z = oldZ;
      t = oldT;
      c = oldC;
      backZ = oldBackZ;
      forwardZ = oldForwardZ;
      backT = oldBackT;
      forwardT = oldForwardT;
      backC = oldBackC;
      forwardC = oldForwardC;
      axis = oldAxis;
      mode = oldMode;
    }
    else {
      z = this.z;
      t = this.t;
      c = this.c;
      backZ = this.backZ;
      forwardZ = this.forwardZ;
      backT = this.backT;
      forwardT = this.forwardT;
      backC = this.backC;
      forwardC = this.forwardC;
      axis = this.axis;
      mode = this.mode;
    }
    
    if (mode == CROSS_MODE) {
      if (axis == Z_AXIS) {
        int lowBound = z - backZ;
        int upBound = z + forwardZ;
        
        int [] upSet = getUpSet(lowBound,upBound, Z_AXIS, z);
        int [] lowSet = getLowSet(lowBound,upBound, Z_AXIS, z);
        
        result = new int [upSet.length + lowSet.length];
        
        if (strategy == FORWARD_FIRST) {
          int count = 0;
        
          for(int i = 0;i<upSet.length;i++) {
            result[count] = upSet[i];
            count++;
          }
          for(int i = 0;i<lowSet.length;i++) {
            result[count] = lowSet[i];
            count++;
          }
        }
        else if(strategy == SURROUND_FIRST) {
          result = getMix(lowSet,upSet);
        }
      }
      else if (axis == (Z_AXIS | T_AXIS)) {
      }
      else if (axis == (Z_AXIS | C_AXIS)) {
      }
      else if (axis == (Z_AXIS | T_AXIS | C_AXIS)) {
      }
      else if (axis == T_AXIS) {
        int lowBound = t - backT;
        int upBound = t + forwardT;
        
        int [] upSet = getUpSet(lowBound,upBound, T_AXIS, t);
        int [] lowSet = getLowSet(lowBound,upBound, T_AXIS, t);
        
        result = new int [upSet.length + lowSet.length];
        
        if (strategy == FORWARD_FIRST) {
          int count = 0;
        
          for(int i = 0;i<upSet.length;i++) {
            result[count] = upSet[i];
            count++;
          }
          for(int i = 0;i<lowSet.length;i++) {
            result[count] = lowSet[i];
            count++;
          }
        }
        else if(strategy == SURROUND_FIRST) {
          result = getMix(lowSet,upSet);
        }
      }
      else if (axis == (T_AXIS | C_AXIS)) {
      }
      else if (axis == C_AXIS) {
        int lowBound = c - backC;
        int upBound = c + forwardC;
        
        int [] upSet = getUpSet(lowBound,upBound, C_AXIS, c);
        int [] lowSet = getLowSet(lowBound,upBound, C_AXIS, c);
        
        result = new int [upSet.length + lowSet.length];
        
        if (strategy == FORWARD_FIRST) {
          int count = 0;
        
          for(int i = 0;i<upSet.length;i++) {
            result[count] = upSet[i];
            count++;
          }
          for(int i = 0;i<lowSet.length;i++) {
            result[count] = lowSet[i];
            count++;
          }
        }
        else if(strategy == SURROUND_FIRST) {
          result = getMix(lowSet,upSet);
        }
      }
    }
    else if (mode == RECT_MODE) {
      if (axis == Z_AXIS) {
      }
      else if (axis == (Z_AXIS | T_AXIS)) {
      }
      else if (axis == (Z_AXIS | C_AXIS)) {
      }
      else if (axis == (Z_AXIS | T_AXIS | C_AXIS)) {
      }
      else if (axis == T_AXIS) {
      }
      else if (axis == (T_AXIS | C_AXIS)) {
      }
      else if (axis == C_AXIS) {
      }
    }
    else if (mode == (CROSS_MODE | RECT_MODE)) {
      if (axis == Z_AXIS) {
      }
      else if (axis == (Z_AXIS | T_AXIS)) {
      }
      else if (axis == (Z_AXIS | C_AXIS)) {
      }
      else if (axis == (Z_AXIS | T_AXIS | C_AXIS)) {
      }
      else if (axis == T_AXIS) {
      }
      else if (axis == (T_AXIS | C_AXIS)) {
      }
      else if (axis == C_AXIS) {
      }
    }
    return result;
  }
  
  public static int[] getMix(int[] lowSet, int[] upSet) {
    int [] result = new int[lowSet.length + upSet.length];
    int countUp = 0;
    int countLow = 0;
    
    boolean mixing = true;
    boolean getFromTop = true;
    
    for(int i = 0;i<result.length;i++) {
      if (mixing) {
        int value;
        if(getFromTop) {
          if(countUp >= upSet.length) {
            mixing = false;
            value = lowSet[countLow];
            countLow++;
          }
          else {
            value = upSet[countUp];
            countUp++;
          }
          getFromTop = false;
        }
        else {
          if(countLow >= lowSet.length) {
            mixing = false;
            value = upSet[countUp];
            countUp++;
          }
          else {
            value = lowSet[countLow];
            countLow++;
          }
          getFromTop = true;
          result[i] = value;
        }
      }
      else {
        int [] thisSet;
        int count;
        if(getFromTop) {
          thisSet = upSet;
          count = countUp;
        }
        else {
          thisSet = lowSet;
          count = countLow;
        }
          
        result[i] = thisSet[count];
        count++;
      }
    }
    return result;
  }
  
  private int [] getUpSet(int lowBound,int upBound,int someAxis, int mid) {
    int size = -1;
    if (someAxis == Z_AXIS) size = sizeZ;
    else if (someAxis == T_AXIS) size = sizeT;
    else size = sizeC;
    
    int [] result = null;
    
    if(size!= -1 && mid != -1) {
      if (upBound >= size) {
        if (lowBound < 0) upBound = size -1;
        else {
          int maxBig = (size - 1) + lowBound;
          if (upBound > maxBig) upBound = maxBig;
        }
      }
      
      result = new int[upBound - (mid - 1)];
      
      int count = 0;
      
      for(int i = mid;i<=upBound;i++) {
        int realCoord = i;
        if(i >= size) realCoord = i - size;

        int index;
        try {
          if (someAxis == Z_AXIS) {
            index = read.getIndex(fileName,realCoord,c,t);
          }
          else if (someAxis == T_AXIS) {
            index = read.getIndex(fileName,z,c,realCoord);
          }
          else {
            index = read.getIndex(fileName,z,realCoord,t);
          }
        }
        catch (Exception exc) { 
          if(DEBUG) exc.printStackTrace();
          return null;
        }
        
        result[count] = index;
        count++;
      }
    }
    
    return result;
  }
  
  private int [] getLowSet(int lowBound,int upBound,int someAxis, int mid) {
    int size = -1;
    if (someAxis == Z_AXIS) size = sizeZ;
    else if (someAxis == T_AXIS) size = sizeT;
    else size = sizeC;

    
    int [] result = null;
    
    if(size!= -1 && mid != -1) {
      if (lowBound < 0) {
        if (upBound >= size) lowBound = 0;
        else {
          int maxNeg = upBound - (size - 1);
          if (lowBound < maxNeg) lowBound = maxNeg;
        }
      }
      
      result = new int[mid - lowBound];
      
      int count = 0;
      
      for(int i = mid - 1;i>=lowBound;i--) {
        int realCoord = i;
        if(i < 0) realCoord = size + i;

        int index;
        try {
          if (someAxis == Z_AXIS) {
            index = read.getIndex(fileName,realCoord,c,t);
          }
          else if (someAxis == T_AXIS) {
            index = read.getIndex(fileName,z,c,realCoord);
          }
          else {
            index = read.getIndex(fileName,z,realCoord,t);
          }
        }
        catch (Exception exc) { 
          if(DEBUG) exc.printStackTrace();
          return null;
        }
        
        result[count] = index;
        count++;
      }
    }
    
    return result;
  }
    
  /**
   * Method to reflect the looping behavior of the cache when
   * trying to define upper and lower bounds of which images to
   * load.
   */
  private void clipBounds(int lowBound, int upBound, int axis) {
    int size;
    if(axis == Z_AXIS) size = sizeZ;
    else if(axis == T_AXIS) size = sizeT;
    else size = sizeC;
    
    if(!loop) {
      if (lowBound < 0) lowBound = 0;
      if (upBound >= size) upBound = size - 1;
    }
    else {
      if (lowBound < 0) {
        if(upBound < size) {
          int maxNeg = (upBound + 1) - size;
          if(lowBound < maxNeg) lowBound = maxNeg;
        }
        else {
          lowBound = 0;
          upBound = size - 1;
        }
      }
      else if (upBound >= size && lowBound > 0) {
        int maxBig = (size - 1) + lowBound;
        if (upBound > maxBig) upBound = maxBig;
      }
    }
    
    if (DEBUG) {
      System.out.println("Bounds: " + lowBound + " - " + upBound);
    }
  }
  
  private void updateCache() {
    if (DEBUG) System.out.println("UPDATING CACHE");
    
    clearCache();
    
    Thread loader = new Thread(this);
    loader.start();
  }
  
  private void clearCache() {
    if (DEBUG) System.out.println("CLEARING CACHE");
    
    int [] oldIndex = getToCache(true);
    oldZ = z;
    oldT = t;
    oldC = c;
    int [] newIndex = getToCache(false);
    if (DEBUG) System.out.println("oldIndex: " + oldIndex );
    if (DEBUG) System.out.println("newIndex: " + newIndex );
    Arrays.sort(newIndex);
    loadList = newIndex;
    
    for (int i = 0;i<oldIndex.length;i++) {
      if(Arrays.binarySearch(newIndex, oldIndex[i]) < 0)
        cache[oldIndex[i]] = null;
    }
  }
  
  // -- Runnable API methods --
  
  public void run() {
    quit = false;

    for(int i = 0;i<loadList.length;i++) {
      if(quit) break;
      if(cache[loadList[i]] == null) {
        ImageProcessor imp = ImagePlusWrapper.getImageProcessor(
          fileName,read,loadList[i]);
        cache[loadList[i]] = imp;
      }
    }
  }

}
