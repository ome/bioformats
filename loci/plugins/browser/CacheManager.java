//
// CacheManager.java
//

/*
LOCI 4D Data Browser plugin for quick browsing of 4D datasets in ImageJ.
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
import loci.formats.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class CacheManager 
  implements Runnable
{
  // - Constants -
  /** The constant that designates the z axis of a dataset.*/
  public static final int Z_AXIS = 0x01;
  /** The constant that designates the t axis of a dataset.*/
  public static final int T_AXIS = 0x02;
  /** The constant that designates the c axis of a dataset.*/
  public static final int C_AXIS = 0x04;

  /**
  * The constant that designates a cross-shaped cache should
  * be used.
  */
  public static final int CROSS_MODE = 0x10;
  /**
  * The constant that designates a rectangle-shaped cache should
  * be used.
  */
  public static final int RECT_MODE = 0x20;
  
  /**
  * The constant that designates that forward slices should be
  * loaded first.
  */
  public static final int FORWARD_FIRST = 0x0100;
  /**
  * The constant that designates that back slices should be
  * loaded first.
  */
  public static final int SURROUND_FIRST = 0x0200;
  
  /** A constant that flags debug messages on/off.*/
  public static final boolean DEBUG = true;

  // - Fields -
  
  /** The array of cached images.*/
  private ImageProcessor [] cache;
  
  /** The IFormatReader that handles conversion of formats.*/
  private IFormatReader read;
  
  /** Holds the current axes to be cached.*/
  private int axis;
  
  /** Holds the previous axes to be cached when clearing.*/
  private int oldAxis;
  
  /** Holds the current caching mode.*/
  private int mode;
  
  /** Holds the previous caching mode when clearing the cache.*/
  private int oldMode;
  
  /** Determines the order in which the images are loaded for caching.*/
  private int strategy;
  
  /** Holds the previous caching strategy for clearing the cache.*/
  private int oldStrategy;
  
  /** Holds the amount of "back" images to store in the cache.*/
  private int backZ,backT,backC;
  
  /**
  * Holds the previous amount of back images stored in cache
  * to use when clearing the cache for a particular dimension.
  */
  private int oldBackZ,oldBackT,oldBackC;
  
  /** Holds the amount of "forward" images to store in the cache.*/
  private int forwardZ,forwardT,forwardC;
  
  /**
  * Holds the previous amount of forward images stored in cache
  * to use when clearing the cache for a particular dimension.
  */
  private int oldForwardZ,oldForwardT,oldForwardC;
  
  /**
  * Holds the current "origin" slice's coordinates. These coordinates
  * are used to center the caching around this slice.
  */
  private int z,t,c;
  
  /** Holds the previous origin slice's coordinates.*/
  private int oldZ,oldT,oldC;
  
  /** Holds the total size of the various dimensions of the file(s).*/
  private int sizeZ,sizeT,sizeC;
  
  /** The path name of the image file being worked on.*/
  private String fileName;
  
  /** Stop-flag used to stop the caching thread.*/
  private boolean quit;
  
  /**
  * A flag used to designate whether or not the forward/back slices
  * should wrap around to the begining/end for animation purposes.
  */
  private boolean loop;


  /** A list of indeces to be loaded by the caching thread.*/
  protected int [] loadList;

  // - Constructors
  public CacheManager(int size, 
    IFormatReader read, String fileName) 
  {
    this(0,size,read,fileName, T_AXIS, CROSS_MODE);
  }
  
  public CacheManager(int back, int forward,
    IFormatReader read, String fileName, int axis, int mode)
  {
    this(0,0,0,back,forward,back,forward,back,
      forward,read,fileName,axis,mode,FORWARD_FIRST);
  }
  
  /**
  * Create a new instance of the CacheManager Object.
  * @param z the z coordinate of the "starting" or "origin" slice
  * to begin forming the cache around initially.
  * @param t the t coordinate of the origin slice.
  * @param c the c coordinate of the origin slice.
  * @param backZ the amount of back slices to cache with respect to
  * the Z axis.
  * @param forwardZ the amount of forward slices to cache with respect
  * to the Z Axis.
  * @param backT the amount of back slices to cache with respect to
  * the T axis.
  * @param forwardT the amount of forward slices to cache with respect
  * to the T Axis.
  * @param backC the amount of back slices to cache with respect to
  * the C axis.
  * @param forwardC the amount of forward slices to cache with respect
  * to the C Axis.
  * @param read the IFormatReader being used to store the image slices.
  * @param fileName the name of the file that has the slices. If file
  * stiching is being used, the name of one of the files.
  * @param axis the axes that should be cached, e.g. Z_AXIS, T_AXIS,
  * C_AXIS, Z_AXIS | T_AXIS, etc. etc.
  * @param mode determines the "shape" of what gets cached, either as
  * a cross/lines (CROSS_MODE) or a rectangle/box (RECT_MODE)
  * @param strategy whether to cache the forward slices first, or mix
  * the forward and back slices equally. either FORWARD_FIRST or
  * SURROUND_FIRST
  */
  public CacheManager(int z, int t, int c, int backZ, int forwardZ,
    int backT, int forwardT, int backC, int forwardC,
    IFormatReader read, String fileName, int axis, int mode, int strategy)
  {
    //Initialize fields
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
    
    //Start the caching thread.
    updateCache();
  }
  
  // - CacheManager API -
  
  /**
  * Change the axes to be cached.
  * @param axis The integer constant signifying the axes that
  * should be cached. Valid parameters: Z_AXIS, T_AXIS | C_AXIS,
  * etc.
  */
  public void setAxis(int axis) {
    quit = true;
    oldAxis = this.axis;
    this.axis = axis;
    updateCache();
  }
  
  /**
  * Change the caching mode.
  * @param mode The integer constant signifying the desired
  * caching mode. Valid Parameters: CROSS_MODE, RECT_MODE,
  * CROSS_MODE | RECT_MODE
  */
  public void setMode(int mode) {
    quit = true;
    oldMode = this.mode;
    this.mode = mode;
    updateCache();
  }
  
  /**
  * Change the order in which the slices are cached.
  * @param strategy The integer constant which signifies
  * whether forward slices are cached first. Valid
  * parameters: FORWARD_FIRST, SURROUND_FIRST
  */
  public void setStrategy(int strategy) {
    quit = true;
    oldStrategy = this.strategy;
    this.strategy = strategy;
    updateCache();
  }
  
  /**
  * Change the sizes of back/forward slices to cache in every
  * dimension simultaneously.
  * @param back The number of back slices to cache.
  * @param forward The number of forward slices to cache.
  */
  public void setSize(int back, int forward) {
    setSize(back,forward,back,forward,back,forward);
  }
  
  /**
  * Change the sizes of back/forward slices to cache in each
  * particular dimension directly.
  * @param backZ the amount of back slices to cache for the 
  * z dimension.
  * @param forwardZ the amount of forward slices to cache for
  * the z dimension.
  * @param backT the amount of back slices to cache for the 
  * t dimension.
  * @param forwardT the amount of forward slices to cache for
  * the t dimension.
  * @param backC the amount of back slices to cache for the 
  * c dimension.
  * @param forwardC the amount of forward slices to cache for
  * the c dimension.
  */
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
  
  /**
  * Get the current size of the cache.
  * @return The number of images load into memory by the cache.
  */
  public int getSize() {
    int count = 0;
    for (int i = 0;i<cache.length;i++) {
      if( cache[i] == null) continue;
      count++;
//      if(DEBUG) System.out.println("In cache: " + i);
    }
    return count;
  }
  
  /**
  * Get the current origin slice of the cache.
  * @return The index number of the current origin slice that
  * the cache has been built around.
  */
  public int getSlice() {
    int index;
    try {
      index = read.getIndex(fileName,z,c,t);
    }
    catch (Exception exc) { return -1;}
    return index;
  }
  
  /**
  * Get the ImageProcessor containing the slice of designated index.
  * @param index The index number of the slice desired.
  * @return The ImageProcessor of the given slice.
  */
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
  
  /**
  * Get the ImageProcessor containing the slice of designated coordinates.
  * @param z The z coordinate of the slice desired.
  * @param t The t coordinate of the slice desired.
  * @param c The c coordinate of the slice desired.
  * @return The ImageProcessor of the given slice.
  */
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
  
  /**
  * Get an ImageProcessor for a given slice without changing the cache.
  * @param index The index number for the desired slice.
  * @return The ImageProcessor of the given slice.
  */
  public ImageProcessor getTempSlice(int index) {
    if (cache[index] != null) return cache[index];
    return ImagePlusWrapper.getImageProcessor(fileName,read,index);
  }
  
  /**
  * Get an ImageProcessor for a given slice without changing the cache.
  * @param z The z coordinate for the desired slice.
  * @param t The t coordinate for the desired slice.
  * @param c The c coordinate for the desired slice.
  * @return The ImageProcessor of the given slice.
  */
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
  
  /** Internal method used to get the indeces to cache.
  * @param old Whether or not to get the previously cached indeces.
  * @return An array of integers of the indeces that should be cached
  * based on the origin coordinates, axis, mode, and strategy. Indeces
  * to be cached first are at the beginning of this array.
  */
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
        
        int [] upSet = getUpSet(lowBound,upBound, Z_AXIS, z, t, c);
        int [] lowSet = getLowSet(lowBound,upBound, Z_AXIS, z, t, c);
        
        result = new int [upSet.length + lowSet.length];
        
        if (strategy == FORWARD_FIRST) {
          result = append(lowSet,upSet);
        }
        else if(strategy == SURROUND_FIRST) {
          result = getMix(lowSet,upSet);
        }
      }
      else if (axis == (Z_AXIS | T_AXIS)) {
        int lowBoundZ = z - backZ;
        int upBoundZ = z + forwardZ;
        int lowBoundT = t - backT;
        int upBoundT = t + forwardT;
        
        int [] upSetZ = getUpSet(lowBoundZ,upBoundZ, Z_AXIS, z, t, c);
        int [] lowSetZ = getLowSet(lowBoundZ,upBoundZ, Z_AXIS, z, t, c);
        int [] upSetT = getUpSet(lowBoundT,upBoundT, T_AXIS, z, t, c);
        int [] lowSetT = getLowSet(lowBoundT,upBoundT, T_AXIS, z, t, c);
        
        result = new int [upSetZ.length + lowSetZ.length + upSetT.length
          + lowSetT.length];
          
        int [] upSet = getMix(upSetT, upSetZ);
        int [] lowSet = getMix(lowSetT,lowSetZ);
        
        if (strategy == FORWARD_FIRST) {
          result = append(lowSet,upSet);
        }
        else if(strategy == SURROUND_FIRST) {
          result = getMix(lowSet,upSet);
        }
      }
      else if (axis == (Z_AXIS | C_AXIS)) {
      }
      else if (axis == (Z_AXIS | T_AXIS | C_AXIS)) {
      }
      else if (axis == T_AXIS) {
        int lowBound = t - backT;
        int upBound = t + forwardT;
        
        int [] upSet = getUpSet(lowBound,upBound, T_AXIS, z, t, c);
        int [] lowSet = getLowSet(lowBound,upBound, T_AXIS, z, t, c);
        
        result = new int [upSet.length + lowSet.length];
        
        if (strategy == FORWARD_FIRST) {
          result = append(lowSet,upSet);
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
        
        int [] upSet = getUpSet(lowBound,upBound, C_AXIS, z, t, c);
        int [] lowSet = getLowSet(lowBound,upBound, C_AXIS, z, t, c);
        
        result = new int [upSet.length + lowSet.length];
        
        if (strategy == FORWARD_FIRST) {
          result = append(lowSet,upSet);
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
  
  /**
  * Takes two integer arrays and adds one to the other.
  * @param lowSet The array to be tacked onto the end of the new
  * array.
  * @param upSet The array to be tacked onto the begining of the
  * new array.
  * @return The new array formend by concatenating the two
  * parameter arrays.
  */
  public static int[] append(int [] lowSet, int [] upSet) {
    int[] result = new int[upSet.length + lowSet.length];
    int count = 0;
  
    for(int i = 0;i<upSet.length;i++) {
      result[count] = upSet[i];
      count++;
    }
    for(int i = 0;i<lowSet.length;i++) {
      result[count] = lowSet[i];
      count++;
    }
    
    return result;
  }
  
  /**
  * Takes two integer arrays and mixes them until one runs out of
  * elements, then adds the rest of the remaining array's elements
  * @param lowSet The array to be mixed in second.
  * @param upSet The array to be mixed in first.
  * @return The new array formed by mixing the two parameter
  * arrays.
  */
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
        }
        result[i] = value;
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
  
  /**
  * Get the array of indeces of forward slices to be cached given a
  * lower bound, upper bound, the axis to be cached, and the z, t, c
  * coordinates of the origin slice to build the cache around. Note
  * that this is used only for CROSS_MODE or CROSS_MODE | RECT_MODE
  * @param lowBound The coordinate number of the origin coordinate
  * designated by someAxis minus the back number of slices to cache.
  * @param upBound The coordinate number of the origin coordinate
  * designated by someAxis plus the forward number of slices to
  * cache.
  * @param someAxis Designates the axis that we want to cache a
  * line-section of.
  * @param z The z coordinate of the origin slice to build the cache
  * around.
  * @param t The t coordinate of the origin slice to build the cache
  * around.
  * @param c The c coordinate of the origin slice to build the cache
  * around.
  * @return The array of integers of the indeces of forward slices
  * to be cached, with the first to be cached in the beginning of
  * the array.
  */
  private int [] getUpSet(int lowBound,int upBound,
    int someAxis, int z, int t, int c)
  {
    int size = -1;
    int mid = -1;
    if (someAxis == Z_AXIS) {
      size = sizeZ;
      mid = z;
    }
    else if (someAxis == T_AXIS) {
      size = sizeT;
      mid = t;
    }
    else {
      size = sizeC;
      mid = c;
    }
    
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
  
  /**
  * Get the array of indeces of back slices to be cached given a
  * lower bound, upper bound, the axis to be cached, and the z, t, c
  * coordinates of the origin slice to build the cache around. Note
  * that this is used only for CROSS_MODE or CROSS_MODE | RECT_MODE
  * @param lowBound The coordinate number of the origin coordinate
  * designated by someAxis minus the back number of slices to cache.
  * @param upBound The coordinate number of the origin coordinate
  * designated by someAxis plus the forward number of slices to
  * cache.
  * @param someAxis Designates the axis that we want to cache a
  * line-section of.
  * @param z The z coordinate of the origin slice to build the cache
  * around.
  * @param t The t coordinate of the origin slice to build the cache
  * around.
  * @param c The c coordinate of the origin slice to build the cache
  * around.
  * @return The array of integers of the indeces of back slices
  * to be cached, with the first to be cached in the beginning of
  * the array.
  */
  private int [] getLowSet(int lowBound,int upBound,int someAxis, int z, int t, int c) {
    int size = -1;
    int mid = -1;
    if (someAxis == Z_AXIS) {
      size = sizeZ;
      mid = z;
    }
    else if (someAxis == T_AXIS) {
      size = sizeT;
      mid = t;
    }
    else {
      size = sizeC;
      mid = c;
    }
    
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
  * Clears the cache, starts the thread to load the new slices
  * we want to cache in the background.
  */
  private void updateCache() {
    if (DEBUG) System.out.println("UPDATING CACHE");
    
    clearCache();
    
    Thread loader = new Thread(this, "Browser-Loader");
    loader.start();
  }
  
  /**
  * Clears the cache and sets the new loadList that the loader
  * thread will use to start loading slices into the cache.
  */
  private void clearCache() {
    if (DEBUG) System.out.println("CLEARING CACHE");
    quit = true;
    
    int [] oldIndex = getToCache(true);
    oldZ = z;
    oldT = t;
    oldC = c;
    int [] newIndex = getToCache(false);
    if (DEBUG) {
      for(int i = 0;i<oldIndex.length;i++) {
        System.out.println("oldIndex " + i + ": " + oldIndex[i] );
      }
      for(int i = 0;i<newIndex.length;i++) {
        System.out.println("newIndex " + i + ": " + newIndex[i] );
      }
    }
    
    loadList = newIndex;
    Arrays.sort(newIndex);
    
    for (int i = 0;i<oldIndex.length;i++) {
      if(Arrays.binarySearch(newIndex, oldIndex[i]) < 0)
        cache[oldIndex[i]] = null;
    }
    
    if (DEBUG) System.out.println("Cache Size after clear: " + getSize()); 
  }
  
  // - Runnable API -
  
  /** The thread method that does the slice loading.*/
  public void run() {
    quit = false;

    for(int i = 0;i<loadList.length;i++) {
      if(quit) break;
      if(cache[loadList[i]] == null) {
        if (DEBUG) System.out.println("CACHING... index: " + loadList[i]); 
        ImageProcessor imp = ImagePlusWrapper.getImageProcessor(
          fileName,read,loadList[i]);
        cache[loadList[i]] = imp;
      }
    }
    
  }
}