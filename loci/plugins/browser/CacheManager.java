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
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;
import java.util.Stack;
import loci.formats.*;
import javax.swing.JScrollBar;

public class CacheManager implements Runnable {

  // -- Constants --

  /** Designates the z axis of a dataset.*/
  public static final int Z_AXIS = 0x01;

  /** Designates the t axis of a dataset.*/
  public static final int T_AXIS = 0x02;

  /** Designates the c axis of a dataset.*/
  public static final int C_AXIS = 0x04;

  /** Designates a cross-shaped cache should be used. */
  public static final int CROSS_MODE = 0x10;

  /** Designates a rectangle-shaped cache should be used. */
  public static final int RECT_MODE = 0x20;

  /** Designates that forward slices should be loaded first. */
  public static final int FORWARD_FIRST = 0x0100;

  /** Designates that back slices should be loaded first. */
  public static final int SURROUND_FIRST = 0x0200;

  /** Flags debug messages on/off.*/
  public static final boolean DEBUG = true;

  // -- Fields --

  /** The array of cached images.*/
  private ImageProcessor[] cache;

  /** The LociDataBrowser associated with this cache.*/
  private LociDataBrowser db;

  /** The IFormatReader that handles conversion of formats.*/
  private IFormatReader read;

  /** The FileStitcher we're using to stitch files.*/
  private FileStitcher fs;

  /** The two axes scrollbars in the CustomWindow.*/
  private JScrollBar zSel, tSel;

  /** The two indicators of the cache.*/
  private CacheIndicator zInd, tInd;

  /** Holds the current axes to be cached.*/
  private int curAxis;

  /** Holds the previous axes to be cached when clearing.*/
  private int oldAxis;

  /** Holds the current caching mode.*/
  private int curMode;

  /** Holds the previous caching mode when clearing the cache.*/
  private int oldMode;

  /** Determines the order in which the images are loaded for caching.*/
  private int strategy;

  /** Holds the previous caching strategy for clearing the cache.*/
  private int oldStrategy;

  /** Holds the amount of "back" images to store in the cache.*/
  private int curBackZ, curBackT, curBackC;

  /**
  * Holds the previous amount of back images stored in cache
  * to use when clearing the cache for a particular dimension.
  */
  private int oldBackZ, oldBackT, oldBackC;

  /** Holds the amount of "forward" images to store in the cache.*/
  private int curForwardZ, curForwardT, curForwardC;

  /**
  * Holds the previous amount of forward images stored in cache
  * to use when clearing the cache for a particular dimension.
  */
  private int oldForwardZ, oldForwardT, oldForwardC;

  /**
  * Holds the current "origin" slice's coordinates. These coordinates
  * are used to center the caching around this slice.
  */
  private int curZ, curT, curC;

  /** Holds the previous origin slice's coordinates.*/
  private int oldZ, oldT, oldC;

  /** Holds the total size of the various dimensions of the file(s).*/
  private int sizeZ, sizeT, sizeC;

  /** Holds the current priority of each axis.*/
  private int curZPriority, curTPriority, curCPriority;

  /** Holds the previous priority of each axis.*/
  private int oldZPriority, oldTPriority, oldCPriority;

  /** The path name of the image file being worked on.*/
  private String fileName;

  /** Stop-flag used to stop the caching thread.*/
  private volatile boolean quit;
  
  /** The loading images thread.*/
  private Thread loader;

  /**
  * A flag used to designate whether or not the forward/back slices
  * should wrap around to the begining/end for animation purposes.
  */
  private boolean loop;

  /**flag to zap the cache entirely if dimensions are switched.*/
  private boolean zapCache;

  /** A list of indeces to be loaded by the caching thread.*/
  protected int[] loadList;
  
  /** The actual queue used by the loader thread to load images.*/
  protected Stack queue;

  // -- Constructors --

  public CacheManager(int size, LociDataBrowser db, String fileName) {
    this(0, size, db, fileName, T_AXIS, CROSS_MODE);
  }

  public CacheManager(int back, int forward,
    LociDataBrowser db, String fileName, int axis, int mode)
  {
    this(0, 0, 0, back, forward, back, forward, back,
      forward, db, fileName, axis, mode, FORWARD_FIRST);
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
  * @param db the associated data browser object.
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
    LociDataBrowser db, String fileName, int axis, int mode, int strategy)
  {
    //Initialize fields
    zapCache = false;
    loop = true;
    curZ = z;
    curT = t;
    curC = c;
    oldZ = z;
    oldT = t;
    oldC = c;
    this.fileName = fileName;
    this.db = db;
    this.read = db.reader;
    fs = db.fStitch;
    zInd = null;
    tInd = null;
    zSel = null;
    tSel = null;
    if (fs != null) {
      synchronized (fs) {
        try {
          sizeZ = fs.getSizeZ(fileName);
          sizeT = fs.getSizeT(fileName);
          sizeC = fs.getSizeC(fileName);
          cache = new ImageProcessor[fs.getImageCount(fileName)];
        }
        catch (Exception exc) {
          if (DEBUG) System.out.println("Error reading size of file.");
          LociDataBrowser.exceptionMessage(exc);
        }
      }
    }
    else {
      synchronized (read) {
        try {
          sizeZ = read.getSizeZ(fileName);
          sizeT = read.getSizeT(fileName);
          sizeC = read.getSizeC(fileName);
          cache = new ImageProcessor[read.getImageCount(fileName)];
        }
        catch (Exception exc) {
          if (DEBUG) System.out.println("Error reading size of file.");
          LociDataBrowser.exceptionMessage(exc);
        }
      }
    }
    oldAxis = axis;
    oldMode = mode;
    oldBackZ = backZ;
    oldForwardZ = forwardZ;
    oldBackT = backT;
    oldForwardT = forwardT;
    oldBackZ = backZ;
    oldForwardZ = forwardZ;
    curBackZ = backZ;
    curForwardZ = forwardZ;
    curBackT = backT;
    curForwardT = forwardT;
    curBackC = backC;
    curForwardC = forwardC;
    curAxis = axis;
    curMode = mode;
    this.strategy = strategy;
    this.oldStrategy = strategy;

    //set default axes' priorities
    curTPriority = 2;
    curZPriority = 1;
    curCPriority = 0;
    oldTPriority = 2;
    oldZPriority = 1;
    oldCPriority = 0;
    queue = new Stack();
    loader = new Thread(this,"Browser-Loader");
    loader.setPriority(Thread.MIN_PRIORITY);
    loader.start();

    //Start the caching thread.
    updateCache();
  }

  // -- CacheManager API methods --

  /**
  * Change the axes to be cached.
  * @param axis The integer constant signifying the axes that
  * should be cached. Valid parameters: Z_AXIS, T_AXIS | C_AXIS,
  * etc.
  */
  public void setAxis(int axis) {
    oldAxis = curAxis;
    curAxis = axis;
    updateCache();
  }

  /**
  * Change the caching mode.
  * @param mode The integer constant signifying the desired
  * caching mode. Valid Parameters: CROSS_MODE, RECT_MODE,
  * CROSS_MODE | RECT_MODE
  */
  public void setMode(int mode) {
    oldMode = curMode;
    curMode = mode;
    updateCache();
  }

  /**
  * Change the order in which the slices are cached.
  * @param strategy The integer constant which signifies
  * whether forward slices are cached first. Valid
  * parameters: FORWARD_FIRST, SURROUND_FIRST
  */
  public void setStrategy(int strategy) {
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
    setSize(back, forward, back, forward, back, forward);
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
    oldBackZ = curBackZ;
    curBackZ = backZ;
    oldForwardZ = curForwardZ;
    curForwardZ = forwardZ;
    oldBackT = curBackT;
    curBackT = backT;
    oldForwardT = curForwardT;
    curForwardT = forwardT;
    oldBackC = curBackC;
    curBackC = backC;
    oldForwardC = curForwardC;
    curForwardC = forwardC;
    updateCache();
  }

  /**
  * A method to set which axis to cache first.
  * @param top The axis constant with the highest priority.
  * @param mid The axis constant with the mid priority.
  * @param low The axis constant with the lowest priority.
  */
  public void setPriority(int top, int mid, int low) {
    if (top == mid || mid == low || top == low) return;

    int storeZ = curZPriority;
    int storeT = curTPriority;
    int storeC = curCPriority;

    if (top == Z_AXIS) curZPriority = 2;
    else if (top == T_AXIS) curTPriority = 2;
    else if (top == C_AXIS) curCPriority = 2;
    else {
      curZPriority = storeZ;
      curTPriority = storeT;
      curCPriority = storeC;
      return;
    }

    if (mid == Z_AXIS) curZPriority = 1;
    else if (mid == T_AXIS) curTPriority = 1;
    else if (mid == C_AXIS) curCPriority = 1;
    else {
      curZPriority = storeZ;
      curTPriority = storeT;
      curCPriority = storeC;
      return;
    }

    if (low == Z_AXIS) curZPriority = 0;
    else if (low == T_AXIS) curTPriority = 0;
    else if (low == C_AXIS) curCPriority = 0;
    else {
      curZPriority = storeZ;
      curTPriority = storeT;
      curCPriority = storeC;
      return;
    }

    oldZPriority = curZPriority;
    oldTPriority = curTPriority;
    oldCPriority = curCPriority;

    updateCache();
  }

  /**
  * Get the current size of the cache.
  * @return The number of images load into memory by the cache.
  */
  public int getSize() {
    int count = 0;
    for (int i = 0; i<cache.length; i++) {
      if (cache[i] == null) continue;
      count++;
//      if (DEBUG) System.out.println("In cache: " + i);
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
    synchronized (read) {
      try {
        index = read.getIndex(fileName, curZ, curC, curT);
      }
      catch (Exception exc) {
        LociDataBrowser.exceptionMessage(exc);
        return -1;
      }
    }
    return index;
  }

  /**
  * Get the ImageProcessor containing the slice of designated index.
  * @param index The index number of the slice desired.
  * @return The ImageProcessor of the given slice.
  */
  public ImageProcessor getSlice(int index) {
    if (DEBUG) System.out.println("GETTING SLICE: " + index);

    int[] coords;
    synchronized (read) {
      try {
        coords = read.getZCTCoords(fileName, index);
        curZ = coords[0];
        curC = coords[1];
        curT = coords[2];
      }
      catch (FormatException exc) {
        exc.printStackTrace();
        LociDataBrowser.exceptionMessage(exc);
      }
      catch (IOException exc) {
        exc.printStackTrace();
        LociDataBrowser.exceptionMessage(exc);
      }
    }

    ImageProcessor result = cache[index];
    if (Arrays.binarySearch(loadList, index) >= 0) {
      if (result != null) {
        if (DEBUG) System.out.println("Slice found in cache.");
        return result;
      }
      else {
        if (DEBUG) System.out.println("Slice not found in cache. LOADING...");
        result = ImagePlusWrapper.getImageProcessor(fileName, read, index);
        cache[index] = result;
        return result;
      }
    }
    else {
      result = ImagePlusWrapper.getImageProcessor(fileName, read, index);
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
    synchronized (read) {
      try {
        index = read.getIndex(fileName, z, c, t);
      }
      catch (Exception exc) {
        if (DEBUG) exc.printStackTrace();
        LociDataBrowser.exceptionMessage(exc);
        return null;
      }
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
    return ImagePlusWrapper.getImageProcessor(fileName, read, index);
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
    synchronized (read) {
      try {
        index = read.getIndex(fileName, z, c, t);
      }
      catch (Exception exc) {
        if (DEBUG) exc.printStackTrace();
        LociDataBrowser.exceptionMessage(exc);
        return null;
      }
    }
    return getTempSlice(index);
  }

  /** Internal method used to get the indeces to cache.
  * @param old Whether or not to get the previously cached indeces.
  * @return An array of integers of the indeces that should be cached
  * based on the origin coordinates, axis, mode, and strategy. Indeces
  * to be cached first are at the beginning of this array.
  */
  private int[] getToCache(boolean old) {
    int[] result = null;
    int z, t, c, backZ, forwardZ, backT, forwardT, backC, forwardC, axis, mode;
    int zPriority, tPriority, cPriority;
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
      zPriority = oldZPriority;
      tPriority = oldTPriority;
      cPriority = oldCPriority;
    }
    else {
      z = curZ;
      t = curT;
      c = curC;
      backZ = curBackZ;
      forwardZ = curForwardZ;
      backT = curBackT;
      forwardT = curForwardT;
      backC = curBackC;
      forwardC = curForwardC;
      axis = curAxis;
      mode = curMode;
      zPriority = curZPriority;
      tPriority = curTPriority;
      cPriority = curCPriority;
    }

    if (axis == Z_AXIS || axis == T_AXIS || axis == C_AXIS) {
      int lowBound, upBound;
      if (axis == Z_AXIS) {
        lowBound = z - backZ;
        upBound = z + forwardZ;
      }
      else if (axis == T_AXIS) {
        lowBound = t - backT;
        upBound = t + forwardT;
      }
      else {
        lowBound = c - backC;
        upBound = c + forwardC;
      }

      int[] upSet = getUpSet(lowBound, upBound, axis, z, t, c);
      int[] lowSet = getLowSet(lowBound, upBound, axis, z, t, c);

      result = new int [upSet.length + lowSet.length];

      if (strategy == FORWARD_FIRST) {
        result = append(lowSet, upSet);
      }
      else if (strategy == SURROUND_FIRST) {
        result = getMix(lowSet, upSet);
      }
    }
    else if (mode == CROSS_MODE && !(axis == Z_AXIS ||
      axis == T_AXIS || axis == C_AXIS))
    {
      if (axis == (Z_AXIS | T_AXIS) || axis == (Z_AXIS | C_AXIS) ||
        axis == (T_AXIS | C_AXIS))
      {
        int lowBound1, lowBound2;
        int upBound1, upBound2;
        int axis1, axis2;

        if (axis == (Z_AXIS | T_AXIS)) {
          lowBound1 = z - backZ;
          upBound1 = z + forwardZ;
          lowBound2 = t - backT;
          upBound2 = t + forwardT;
          axis1 = Z_AXIS;
          axis2 = T_AXIS;
        }
        else if (axis == (Z_AXIS | C_AXIS)) {
          lowBound1 = z - backZ;
          upBound1 = z + forwardZ;
          lowBound2 = c - backC;
          upBound2 = c + forwardC;
          axis1 = Z_AXIS;
          axis2 = C_AXIS;
        }
        else {
          lowBound1 = c - backC;
          upBound1 = c + forwardC;
          lowBound2 = t - backT;
          upBound2 = t + forwardT;
          axis1 = C_AXIS;
          axis2 = T_AXIS;
        }

        int[] upSet1 = getUpSet(lowBound1, upBound1, axis1, z, t, c);
        int[] lowSet1 = getLowSet(lowBound1, upBound1, axis1, z, t, c);
        int[] upSet2 = getUpSet(lowBound2, upBound2, axis2, z, t, c);
        int[] lowSet2 = getLowSet(lowBound2, upBound2, axis2, z, t, c);

        int[] upSet = getMix(upSet1, upSet2);
        int[] lowSet = getMix(lowSet1, lowSet2);

        if (strategy == FORWARD_FIRST) {
          result = append(lowSet, upSet);
        }
        else if (strategy == SURROUND_FIRST) {
          result = getMix(lowSet, upSet);
        }
      }
      else if ( (axis == (Z_AXIS | T_AXIS | C_AXIS))) {
        int lowBoundZ = z - backZ;
        int upBoundZ = z + forwardZ;
        int lowBoundT = t - backT;
        int upBoundT = t + forwardT;
        int lowBoundC = c - backC;
        int upBoundC = c + forwardC;

        int[] upSetZ = getUpSet(lowBoundZ, upBoundZ, Z_AXIS, z, t, c);
        int[] lowSetZ = getLowSet(lowBoundZ, upBoundZ, Z_AXIS, z, t, c);
        int[] upSetT = getUpSet(lowBoundT, upBoundT, T_AXIS, z, t, c);
        int[] lowSetT = getLowSet(lowBoundT, upBoundT, T_AXIS, z, t, c);
        int[] upSetC = getUpSet(lowBoundC, upBoundC, C_AXIS, z, t, c);
        int[] lowSetC = getLowSet(lowBoundC, upBoundC, C_AXIS, z, t, c);

        int[] upSet = getMix(upSetZ, upSetT, upSetC);
        int[] lowSet = getMix(lowSetZ, lowSetT, lowSetC);

        if (strategy == FORWARD_FIRST) {
          result = append(lowSet, upSet);
        }
        else if (strategy == SURROUND_FIRST) {
          result = getMix(lowSet, upSet);
        }
      }
      else return null;
    }
    else if (mode == RECT_MODE && !(axis == Z_AXIS ||
      axis == T_AXIS || axis == C_AXIS))
    {
      if (axis == (Z_AXIS | T_AXIS) || axis == (Z_AXIS | C_AXIS) ||
        axis == (T_AXIS | C_AXIS))
      {
        int firstLowBound, firstUpBound, secondLowBound, secondUpBound;
        int firstAxis, secondAxis;

        if( axis == (Z_AXIS | T_AXIS) ) {
          if (tPriority > zPriority) {
            firstLowBound = t - backT;
            firstUpBound = t + forwardT;
            secondLowBound = z - backZ;
            secondUpBound = z + forwardZ;
            firstAxis = T_AXIS;
            secondAxis = Z_AXIS;
          }
          else {
            firstLowBound = z - backZ;
            firstUpBound = z + forwardZ;
            secondLowBound = t - backT;
            secondUpBound = t + forwardT;
            firstAxis = Z_AXIS;
            secondAxis = T_AXIS;
          }
        }
        else if ( axis == (Z_AXIS | C_AXIS) ) {
          if (cPriority > zPriority) {
            firstLowBound = c - backC;
            firstUpBound = c + forwardC;
            secondLowBound = z - backZ;
            secondUpBound = z + forwardZ;
            firstAxis = C_AXIS;
            secondAxis = Z_AXIS;
          }
          else {
            firstLowBound = z - backZ;
            firstUpBound = z + forwardZ;
            secondLowBound = c - backC;
            secondUpBound = c + forwardC;
            firstAxis = Z_AXIS;
            secondAxis = C_AXIS;
          }
        }
        else {    //axis == (T_AXIS | C_AXIS)
          if (tPriority > cPriority) {
            firstLowBound = t - backT;
            firstUpBound = t + forwardT;
            secondLowBound = c - backC;
            secondUpBound = c + forwardC;
            firstAxis = T_AXIS;
            secondAxis = C_AXIS;
          }
          else {
            firstLowBound = c - backC;
            firstUpBound = c + forwardC;
            secondLowBound = t - backT;
            secondUpBound = t + forwardT;
            firstAxis = C_AXIS;
            secondAxis = T_AXIS;
          }
        }

        if (strategy == FORWARD_FIRST) {
          result = getRect(firstAxis,firstLowBound,firstUpBound,
            secondAxis,secondLowBound,secondUpBound,z,t,c);
        }
        else if (strategy == SURROUND_FIRST) {
          result = getSpiral(firstAxis,firstLowBound,firstUpBound,
            secondAxis,secondLowBound,secondUpBound,z,t,c);
        }
      }
      else if (axis == (Z_AXIS | T_AXIS | C_AXIS)) {
        //f: first | s: second | t: third
        int fLow,fUp,sLow,sUp,tLow,tUp,fAxis,sAxis,tAxis;

        if (zPriority > tPriority && zPriority > cPriority) {
          fLow = z - backZ;
          fUp = z + forwardZ;
          fAxis = Z_AXIS;

          if(tPriority > cPriority) {
            sLow = t - backT;
            sUp = t + forwardT;
            sAxis = T_AXIS;
            tLow = c - backC;
            tUp = c + forwardC;
            tAxis = C_AXIS;
          }
          else { //cPriority 2nd greatest
            sLow = c - backC;
            sUp = c + forwardC;
            sAxis = C_AXIS;
            tLow = t - backT;
            tUp = t + forwardT;
            tAxis = T_AXIS;
          }
        }
        else if (tPriority > zPriority && tPriority > cPriority) {
          fLow = t - backT;
          fUp = t + forwardT;
          fAxis = T_AXIS;

          if(zPriority > cPriority) {
            sLow = z - backZ;
            sUp = z + forwardZ;
            sAxis = Z_AXIS;
            tLow = c - backC;
            tUp = c + forwardC;
            tAxis = C_AXIS;
          }
          else { //cPriority 2nd greatest
            sLow = c - backC;
            sUp = c + forwardC;
            sAxis = C_AXIS;
            tLow = z - backZ;
            tUp = z + forwardZ;
            tAxis = Z_AXIS;
          }
        }
        else { //cPriority greatest
          fLow = c - backC;
          fUp = c + forwardC;
          fAxis = C_AXIS;

          if(tPriority > zPriority) {
            sLow = t - backT;
            sUp = t + forwardT;
            sAxis = T_AXIS;
            tLow = z - backZ;
            tUp = z + forwardZ;
            tAxis = Z_AXIS;
          }
          else { //zPriority 2nd greatest
            sLow = z - backZ;
            sUp = z + forwardZ;
            sAxis = Z_AXIS;
            tLow = t - backT;
            tUp = t + forwardT;
            tAxis = T_AXIS;
          }
        }

        if (strategy == FORWARD_FIRST) {
          result = getBrick(fAxis,fLow,fUp,
            sAxis,sLow,sUp,tAxis,tLow,tUp,z,t,c);
        }
        else if (strategy == SURROUND_FIRST) {
          result = getCyclone(fAxis,fLow,fUp,
            sAxis,sLow,sUp,tAxis,tLow,tUp,z,t,c);
        }
      }
      else return null;
    }
    else if (mode == (CROSS_MODE | RECT_MODE) && !(axis == Z_AXIS ||
      axis == T_AXIS || axis == C_AXIS))
    {
      if (axis == (Z_AXIS | T_AXIS) || axis == (Z_AXIS | C_AXIS) ||
        axis == (T_AXIS | C_AXIS))
      {

      }
      else if (axis == (Z_AXIS | T_AXIS | C_AXIS)) {

      }
      else return null;
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
  public static int[] append(int[] lowSet, int[] upSet) {
    if(lowSet.length == 0 && upSet.length != 0) return upSet;
    else if(lowSet.length != 0 && upSet.length == 0) return lowSet;
    else if(lowSet.length == 0 && upSet.length == 0) return lowSet;

    int[] result = new int[upSet.length + lowSet.length];
    int count = 0;

    for (int i = 0; i<upSet.length; i++) {
      result[count] = upSet[i];
      count++;
    }
    for (int i = 0; i<lowSet.length; i++) {
      result[count] = lowSet[i];
      count++;
    }

    return result;
  }

  /**
  * Takes two integer arrays and mixes them until one runs out of
  * elements, then adds the rest of the remaining array's elements.
  * Starts at the beginning position of each array.
  * @param lowSet The array to be mixed in second.
  * @param upSet The array to be mixed in first.
  * @return The new array formed by mixing the two parameter
  * arrays.
  */
  public static int[] getMix(int[] lowSet, int[] upSet) {
    return getMix(lowSet,0,upSet,0);
  }

  /**
  * Takes two integer arrays and mixes them until one runs out of
  * elements, then adds the rest of the remaining array's elements
  * @param lowSet The array to be mixed in second.
  * @param lowStart The position in the second array to start at.
  * @param upSet The array to be mixed in first.
  * @param upStart The position in the first array to start at.
  * @return The new array formed by mixing the two parameter
  * arrays.
  */
  public static int[] getMix(int[] lowSet, int lowStart,
    int[] upSet, int upStart)
  {
    int[] result = new int[lowSet.length + upSet.length - lowStart - upStart];
    int countUp = upStart;
    int countLow = lowStart;

    boolean mixing = true;
    boolean getFromTop = true;

    for (int i = 0; i<result.length; i++) {
      if (mixing) {
        int value;
        if (getFromTop) {
          if (countUp >= upSet.length) {
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
          if (countLow >= lowSet.length) {
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
        if (getFromTop) {
          result[i] = upSet[countUp];
          countUp++;
        }
        else {
          result[i] = lowSet[countLow];
          countLow++;
        }
      }
    }
    return result;
  }

  /**
  * Takes three integer arrays and mixes them until one runs out of
  * elements, then adds the rest of the remaining arrays' elements.
  * @param set1 The array to be mixed in first.
  * @param set2 The array to be mixed in second.
  * @param set3 The array to be mixed in third.
  * @return The new array formed by mixing the three parameter
  * arrays.
  */
  public static int[] getMix(int[] set1, int[] set2, int[] set3) {
    int[] result = new int[set1.length + set2.length + set3.length];
    int count1 = 0, count2 = 0, count3 = 0;
    int toggle = 0;
    int lastI = 0;

    for (int i = 0; i<result.length; i++) {
      int value;
      lastI = i;
      if (toggle == 0) {
        if (count1 < set1.length) {
          value = set1[count1];
          count1++;
        }
        else break;
      }
      else if (toggle == 1) {
        if (count2 < set2.length) {
          value = set2[count2];
          count2++;
        }
        else break;
      }
      else {
        if (count3 < set3.length) {
          value = set3[count3];
          count3++;
        }
        else break;
      }
      toggle++;
      toggle = toggle %3;
      result[i] = value;
    }

    int[] theRest;
    if (count1 >= set1.length) theRest =
      getMix(set3,count3,set2,count2);
    else if (count2 >= set2.length) theRest =
      getMix(set1,count1,set3,count3);
    else theRest = getMix(set2,count2,set1,count1);

    for (int i = 0; i<theRest.length; i++) {
      result[lastI + i] = theRest[i];
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
  private int[] getUpSet(int lowBound, int upBound,
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

    int[] result = null;

    if (size!= -1 && mid != -1) {
      if ((upBound - lowBound)> (size - 1)) {
        upBound = size - 1;
        lowBound = 0;
      }

      if (upBound >= size) {
        if (lowBound < 0) upBound = size -1;
        else {
          int maxBig = (size - 1) + lowBound;
          if (upBound > maxBig) upBound = maxBig;
        }
      }

      result = new int[upBound - (mid - 1)];

      int count = 0;

      for (int i = mid; i<=upBound; i++) {
        int realCoord = i;
        if (i >= size) realCoord = i - size;

        int index;
        synchronized (read) {
          try {
            if (someAxis == Z_AXIS) {
              index = read.getIndex(fileName, realCoord, c, t);
            }
            else if (someAxis == T_AXIS) {
              index = read.getIndex(fileName, z, c, realCoord);
            }
            else {
              index = read.getIndex(fileName, z, realCoord, t);
            }
          }
          catch (Exception exc) {
            exc.printStackTrace();
            LociDataBrowser.exceptionMessage(exc);
            return null;
          }
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
  private int[] getLowSet(int lowBound, int upBound,
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

    int[] result = null;

    if (size!= -1 && mid != -1) {
      if ((upBound - lowBound)> (size - 1)) {
        upBound = size - 1;
        lowBound = 0;
      }

      if (lowBound < 0) {
        if (upBound >= size) lowBound = 0;
        else {
          int maxNeg = upBound - (size - 1);
          if (lowBound < maxNeg) lowBound = maxNeg;
        }
      }

      result = new int[mid - lowBound];

      int count = 0;

      for (int i = mid - 1; i>=lowBound; i--) {
        int realCoord = i;
        if (i < 0) realCoord = size + i;

        int index;
        synchronized (read) {
          try {
            if (someAxis == Z_AXIS) {
              index = read.getIndex(fileName, realCoord, c, t);
            }
            else if (someAxis == T_AXIS) {
              index = read.getIndex(fileName, z, c, realCoord);
            }
            else {
              index = read.getIndex(fileName, z, realCoord, t);
            }
          }
          catch (Exception exc) {
            exc.printStackTrace();
            LociDataBrowser.exceptionMessage(exc);
            return null;
          }
        }

        result[count] = index;
        count++;
      }
    }

    return result;
  }

  /**
  * A method to get a 2d "ring" of indeces around a given point.
  * Note that if the ring would be outside of the maximum values
  * for a given dimension, that part of the ring is cropped out.
  * @param fAxis The constant signifying the first axis to cache.
  * @param sAxis The constant signifying the 2nd axis to cache.
  * @param z The z coordinate of the center of the ring.
  * @param t The t coordinate of the center of the ring.
  * @param c The c coordinate of the center of the ring.
  * @param r The radius of the ring.
  * @return An integer array containing the indeces of slices
  * to load in "correct" order (kind of arbitrary here).
  */
  private int [] getRing(int fAxis, int sAxis,
    int z,int t,int c,int r)
  {
    if (r == 0) {
      int index = -1;

      try {
        index = read.getIndex(fileName, z, c, t);
      }
      catch(Exception exc) {
        if(DEBUG) exc.printStackTrace();
        LociDataBrowser.exceptionMessage(exc);
      }

      int[] result = {index};
      return result;
    }
    else {
      int[] result = {};
      int fUp,fLow,fSize,sUp,sLow,sSize;

      if (fAxis == Z_AXIS) {
        fSize = sizeZ;
        fUp = z + r;
        fLow = z - r;
      }
      else if (fAxis == T_AXIS) {
        fSize = sizeT;
        fUp = t + r;
        fLow = t - r;
      }
      else { //fAxis == C_AXIS
        fSize = sizeC;
        fUp = c + r;
        fLow = c - r;
      }

      if (sAxis == Z_AXIS) {
        sSize = sizeZ;
        sUp = z + r;
        sLow = z - r;
      }
      else if (sAxis == T_AXIS) {
        sSize = sizeT;
        sUp = t + r;
        sLow = t - r;
      }
      else { //sAxis == C_AXIS
        sSize = sizeC;
        sUp = c + r;
        sLow = c - r;
      }

      Vector temp = new Vector();

      //The following code could be made more concise.
      if(fUp < fSize) {
        for(int s = sLow;s<=sUp;s++) {
          if(s >= 0 && s < sSize) {
            int index = -1;

            synchronized (read) {
              try {
                if (fAxis == Z_AXIS) {
                  if (sAxis == T_AXIS) {
                    index = read.getIndex(fileName, fUp, c, s);
                  }
                  else { //sAxis == C_AXIS
                    index = read.getIndex(fileName, fUp, s, t);
                  }
                }
                else if (fAxis == T_AXIS) {
                  if (sAxis == Z_AXIS) {
                    index = read.getIndex(fileName, s, c, fUp);
                  }
                  else { //sAxis == C_AXIS
                    index = read.getIndex(fileName, z, s, fUp);
                  }
                }
                else { //fAxis == C_AXIS
                  if (sAxis == Z_AXIS) {
                    index = read.getIndex(fileName, s, fUp, t);
                  }
                  else { //sAxis == T_AXIS
                    index = read.getIndex(fileName, z, fUp, s);
                  }
                }
              }
              catch(Exception exc) {
                if(DEBUG) exc.printStackTrace();
                LociDataBrowser.exceptionMessage(exc);
              }
            }

            if (index != -1) {
              Integer indexObj = new Integer(index);

              temp.add(indexObj);
            }
          }
          else continue;
        }
      }

      if(fLow >= 0) {
        for(int s = sLow;s<=sUp;s++) {
          if(s >= 0 && s < sSize) {
            int index = -1;

            synchronized (read) {
              try {
                if (fAxis == Z_AXIS) {
                  if (sAxis == T_AXIS) {
                    index = read.getIndex(fileName, fLow, c, s);
                  }
                  else { //sAxis == C_AXIS
                    index = read.getIndex(fileName, fLow, s, t);
                  }
                }
                else if (fAxis == T_AXIS) {
                  if (sAxis == Z_AXIS) {
                    index = read.getIndex(fileName, s, c, fLow);
                  }
                  else { //sAxis == C_AXIS
                    index = read.getIndex(fileName, z, s, fLow);
                  }
                }
                else { //fAxis == C_AXIS
                  if (sAxis == Z_AXIS) {
                    index = read.getIndex(fileName, s, fLow, t);
                  }
                  else { //sAxis == T_AXIS
                    index = read.getIndex(fileName, z, fLow, s);
                  }
                }
              }
              catch(Exception exc) {
                if(DEBUG) exc.printStackTrace();
                LociDataBrowser.exceptionMessage(exc);
              }
            }

            if (index != -1) {
              Integer indexObj = new Integer(index);

              temp.add(indexObj);
            }
          }
          else continue;
        }
      }

      if(sUp < sSize) {
        for(int f = fLow + 1;f < fUp;f++) {
          if(f >= 0 && f < fSize) {
            int index = -1;

            synchronized (read) {
              try {
                if (sAxis == Z_AXIS) {
                  if (fAxis == T_AXIS) {
                    index = read.getIndex(fileName, sUp, c, f);
                  }
                  else { //fAxis == C_AXIS
                    index = read.getIndex(fileName, sUp, f, t);
                  }
                }
                else if (sAxis == T_AXIS) {
                  if (fAxis == Z_AXIS) {
                    index = read.getIndex(fileName, f, c, sUp);
                  }
                  else { //fAxis == C_AXIS
                    index = read.getIndex(fileName, z, f, sUp);
                  }
                }
                else { //sAxis == C_AXIS
                  if (fAxis == Z_AXIS) {
                    index = read.getIndex(fileName, f, sUp, t);
                  }
                  else { //fAxis == T_AXIS
                    index = read.getIndex(fileName, z, sUp, f);
                  }
                }
              }
              catch(Exception exc) {
                if(DEBUG) exc.printStackTrace();
                LociDataBrowser.exceptionMessage(exc);
              }
            }

            if (index != -1) {
              Integer indexObj = new Integer(index);

              temp.add(indexObj);
            }
          }
          else continue;
        }
      }

      if(sLow >= 0) {
        for(int f = fLow + 1;f < fUp;f++) {
          if(f >= 0 && f < fSize) {
            int index = -1;

            synchronized (read) {
              try {
                if (sAxis == Z_AXIS) {
                  if (fAxis == T_AXIS) {
                    index = read.getIndex(fileName, sLow, c, f);
                  }
                  else { //fAxis == C_AXIS
                    index = read.getIndex(fileName, sLow, f, t);
                  }
                }
                else if (sAxis == T_AXIS) {
                  if (fAxis == Z_AXIS) {
                    index = read.getIndex(fileName, f, c, sLow);
                  }
                  else { //fAxis == C_AXIS
                    index = read.getIndex(fileName, z, f, sLow);
                  }
                }
                else { //sAxis == C_AXIS
                  if (fAxis == Z_AXIS) {
                    index = read.getIndex(fileName, f, sLow, t);
                  }
                  else { //fAxis == T_AXIS
                    index = read.getIndex(fileName, z, sLow, f);
                  }
                }
              }
              catch(Exception exc) {
                if(DEBUG) exc.printStackTrace();
                LociDataBrowser.exceptionMessage(exc);
              }
            }

            if (index != -1) {
              Integer indexObj = new Integer(index);

              temp.add(indexObj);
            }
          }
          else continue;
        }
      }

      Object[] tempArray = temp.toArray();
      result = new int[tempArray.length];
      for(int i = 0;i<tempArray.length;i++) {
        result[i] = ((Integer)tempArray[i]).intValue();
      }

      return result;
    }
  }

  /**
  * A method to get a 2d rectangular cache in FORWARD_FIRST strategy.
  * @param fAxis The axis constant to be cached first.
  * @param fLow The lower bound of the first cached axis.
  * @param fUp The upper bound of the first cached axis.
  * @param sAxis The axis constant to be cached second.
  * @param sLow The lower bound of the second cached axis.
  * @param sUp The upper bound of the second cached axis.
  * @param z The z coordinate to use as the z midpoint.
  * @param t The t coordinate to use as the t midpoint.
  * @param c The c coordinate to use as the c midpoint.
  * @return An integer array of indeces to be cached in order of
  * priority.
  */
  private int[] getRect(int fAxis,int fLow,int fUp,
    int sAxis,int sLow,int sUp,int z,int t,int c)
  {
    int [] result = {};
    int fSize,sSize,fMid,sMid;
    if (fAxis == Z_AXIS) {
      fSize = sizeZ;
      fMid = z;
    }
    else if (fAxis == T_AXIS) {
      fSize = sizeT;
      fMid = t;
    }
    else { //fAxis == C_AXIS
      fSize = sizeC;
      fMid = c;
    }

    if (sAxis == Z_AXIS) {
      sSize = sizeZ;
      sMid = z;
    }
    else if (sAxis == T_AXIS) {
      sSize = sizeT;
      sMid = t;
    }
    else { //sAxis == C_AXIS
      sSize = sizeC;
      sMid = c;
    }

    //clip bounds, no looping in RECT_MODE
    if(fLow < 0) fLow = 0;
    if(fUp >= fSize) fUp = fSize - 1;
    if(sLow < 0) sLow = 0;
    if(sUp >= sSize) sUp = sSize - 1;

    if(DEBUG) {
      System.out.println("fSize: " + fSize + " | fMid:" + fMid +
        " | fUp:" + fUp + " | fLow:" + fLow + " | sSize:" + sSize +
        " | sMid:" + sMid + " | sUp:" + sUp + " | sLow:" + sLow);
    }

    for(int sRow = sMid;sRow<=sUp;sRow++) {
      int[] toAdd = {};

      try {
        if (sAxis == Z_AXIS) toAdd = getUpSet(fLow,fUp,fAxis,sRow,t,c);
        else if (sAxis == T_AXIS) toAdd =
          getUpSet(fLow,fUp,fAxis,z,sRow,c);
        else toAdd = getUpSet(fLow,fUp,fAxis,z,t,sRow);
      }
      catch (Exception exc) {
        exc.printStackTrace();
        LociDataBrowser.exceptionMessage(exc);
      }

      result = append(toAdd,result);
    }

    if(sMid - 1 >= 0) {
      for(int sRow = sMid - 1;sRow>=sLow;sRow--) {
        int[] toAdd = {};

        try {
          if (sAxis == Z_AXIS) toAdd = getUpSet(fLow,fUp,fAxis,sRow,t,c);
          else if (sAxis == T_AXIS) toAdd =
            getUpSet(fLow,fUp,fAxis,z,sRow,c);
          else toAdd = getUpSet(fLow,fUp,fAxis,z,t,sRow);
        }
        catch (Exception exc) {
          exc.printStackTrace();
          LociDataBrowser.exceptionMessage(exc);
        }

        result = append(toAdd,result);
      }
    }

    for(int sRow = sMid;sRow<=sUp;sRow++) {
      int[] toAdd = {};

      try {
        if (sAxis == Z_AXIS) toAdd = getLowSet(fLow,fUp,fAxis,sRow,t,c);
        else if (sAxis == T_AXIS) toAdd =
          getLowSet(fLow,fUp,fAxis,z,sRow,c);
        else toAdd = getLowSet(fLow,fUp,fAxis,z,t,sRow);
      }
      catch (Exception exc) {
        exc.printStackTrace();
        LociDataBrowser.exceptionMessage(exc);
      }

      result = append(toAdd,result);
    }

    if(sMid - 1 >= 0) {
      for(int sRow = sMid - 1;sRow>=sLow;sRow--) {
        int[] toAdd = {};

        try {
          if (sAxis == Z_AXIS) toAdd = getLowSet(fLow,fUp,fAxis,sRow,t,c);
          else if (sAxis == T_AXIS) toAdd =
            getLowSet(fLow,fUp,fAxis,z,sRow,c);
          else toAdd = getLowSet(fLow,fUp,fAxis,z,t,sRow);
        }
        catch (Exception exc) {
          exc.printStackTrace();
          LociDataBrowser.exceptionMessage(exc);
        }

        result = append(toAdd,result);
      }
    }

    return result;
  }

  /**
  * A method to get a 3d "brick" cache in FORWARD_FIRST strategy.
  * @param fAxis The axis constant to be cached first.
  * @param fLow The lower bound of the first cached axis.
  * @param fUp The upper bound of the first cached axis.
  * @param sAxis The axis constant to be cached second.
  * @param sLow The lower bound of the second cached axis.
  * @param sUp The upper bound of the second cached axis.
  * @param tAxis The axis constant to be cached third.
  * @param tLow The lower bound of the third cached axis.
  * @param tUp The upper bound of the third cached axis.
  * @param z The z coordinate to use as the z midpoint.
  * @param t The t coordinate to use as the t midpoint.
  * @param c The c coordinate to use as the c midpoint.
  * @return An integer array of indeces to be cached in order of
  * priority.
  */
  private int[] getBrick(int fAxis,int fLow,int fUp,
    int sAxis,int sLow,int sUp,
    int tAxis,int tLow,int tUp, int z,int t,int c)
  {
          int [] result = {};
    int fSize,sSize,tSize,fMid,sMid,tMid;
    if (fAxis == Z_AXIS) {
      fSize = sizeZ;
      fMid = z;
    }
    else if (fAxis == T_AXIS) {
      fSize = sizeT;
      fMid = t;
    }
    else { //fAxis == C_AXIS
      fSize = sizeC;
      fMid = c;
    }

    if (sAxis == Z_AXIS) {
      sSize = sizeZ;
      sMid = z;
    }
    else if (sAxis == T_AXIS) {
      sSize = sizeT;
      sMid = t;
    }
    else { //sAxis == C_AXIS
      sSize = sizeC;
      sMid = c;
    }

    if (tAxis == Z_AXIS) {
      tSize = sizeZ;
      tMid = z;
    }
    else if (tAxis == T_AXIS) {
      tSize = sizeT;
      tMid = t;
    }
    else { //tAxis == C_AXIS
      tSize = sizeC;
      tMid = c;
    }

    //clip bounds, no looping in RECT_MODE
    if(fLow < 0) fLow = 0;
    if(fUp >= fSize) fUp = fSize - 1;
    if(sLow < 0) sLow = 0;
    if(sUp >= sSize) sUp = sSize - 1;
    if(tLow < 0) tLow = 0;
    if(tUp >= tSize) tUp = tSize - 1;

    for(int tRow = tMid;tRow<=tUp;tRow++) {
      int[] toAdd = {};
      if (tAxis == Z_AXIS)
        toAdd = getRect(fAxis,fLow,fUp,sAxis,sLow,sUp,tRow,t,c);
      else if(tAxis == T_AXIS)
        toAdd = getRect(fAxis,fLow,fUp,sAxis,sLow,sUp,z,tRow,c);
      else //tAxis == C_AXIS
        toAdd = getRect(fAxis,fLow,fUp,sAxis,sLow,sUp,z,t,tRow);

      result = append(toAdd,result);
    }

    if(tMid - 1 >= 0) {
      for(int tRow = tMid - 1;tRow>=tLow;tRow--) {
        int[] toAdd = {};
        if (tAxis == Z_AXIS)
          toAdd = getRect(fAxis,fLow,fUp,sAxis,sLow,sUp,tRow,t,c);
        else if(tAxis == T_AXIS)
          toAdd = getRect(fAxis,fLow,fUp,sAxis,sLow,sUp,z,tRow,c);
        else //tAxis == C_AXIS
          toAdd = getRect(fAxis,fLow,fUp,sAxis,sLow,sUp,z,t,tRow);

        result = append(toAdd,result);
      }
    }

    return result;
  }

  /**
  * A method to get a 2d rectangular cache in SURROUND_FIRST strategy.
  * @param fAxis The axis constant to be cached first.
  * @param fLow The lower bound of the first cached axis.
  * @param fUp The upper bound of the first cached axis.
  * @param sAxis The axis constant to be cached second.
  * @param sLow The lower bound of the second cached axis.
  * @param sUp The upper bound of the second cached axis.
  * @param z The z coordinate to use as the z midpoint.
  * @param t The t coordinate to use as the t midpoint.
  * @param c The c coordinate to use as the c midpoint.
  * @return An integer array of indeces to be cached in order of
  * priority.
  */
  private int[] getSpiral(int fAxis,int fLow,int fUp,
    int sAxis,int sLow,int sUp,int z,int t,int c)
  {
    int [] result = {};
    int fMax, sMax, rMax;

    if (fAxis == Z_AXIS) {
      fMax = Math.max(z-fLow,fUp-z);
    }
    else if (fAxis == T_AXIS) {
      fMax = Math.max(t-fLow,fUp-t);
    }
    else { //fAxis == C_AXIS
      fMax = Math.max(z-fLow,fUp-z);
    }

    if (sAxis == Z_AXIS) {
      sMax = Math.max(z-sLow,sUp-z);
    }
    else if (sAxis == T_AXIS) {
      sMax = Math.max(t-sLow,sUp-t);
    }
    else { //sAxis == C_AXIS
      sMax = Math.max(c-sLow,sUp-c);
    }

    rMax = Math.max(fMax,sMax);

    for(int r = 0;r <= rMax;r++) {
      int toAdd[];

      toAdd = getRing(fAxis,sAxis,z,t,c,r);
      result = append(toAdd,result);
    }

    return result;
  }

  /**
  * A method to get a 3d "cyclone" cache in SURROUND_FIRST strategy.
  * @param fAxis The axis constant to be cached first.
  * @param fLow The lower bound of the first cached axis.
  * @param fUp The upper bound of the first cached axis.
  * @param sAxis The axis constant to be cached second.
  * @param sLow The lower bound of the second cached axis.
  * @param sUp The upper bound of the second cached axis.
  * @param tAxis The axis constant to be cached third.
  * @param tLow The lower bound of the third cached axis.
  * @param tUp The upper bound of the third cached axis.
  * @param z The z coordinate to use as the z midpoint.
  * @param t The t coordinate to use as the t midpoint.
  * @param c The c coordinate to use as the c midpoint.
  * @return An integer array of indeces to be cached in order of
  * priority.
  */
  private int[] getCyclone(int fAxis,int fLow,int fUp,
    int sAxis,int sLow,int sUp,
    int tAxis,int tLow,int tUp,int z,int t,int c)
  {
    int[] result;
    int tMid;
    if (tAxis == Z_AXIS) {
      tMid = z;
    }
    else if (tAxis == T_AXIS) {
      tMid = t;
    }
    else { //tAxis == C_AXIS
      tMid = c;
    }

    int[] upSet = {};
    int[] lowSet = {};

    for(int tRow = tMid;tRow <= tUp;tRow++) {
      int[] toAdd = {};

      if(tAxis == Z_AXIS) toAdd = getSpiral(fAxis,fLow,fUp,
        sAxis,sLow,sUp,tRow,t,c);
      else if(tAxis == T_AXIS) toAdd = getSpiral(fAxis,fLow,fUp,
        sAxis,sLow,sUp,z,tRow,c);
      else toAdd = getSpiral(fAxis,fLow,fUp,
        sAxis,sLow,sUp,z,t,tRow);

      upSet = append(toAdd,upSet);
    }

    if(tMid - 1 >= 0) {
      for(int tRow = tMid - 1;tRow >= tLow;tRow--) {
        int[] toAdd = {};

        if(tAxis == Z_AXIS) toAdd = getSpiral(fAxis,fLow,fUp,
          sAxis,sLow,sUp,tRow,t,c);
        else if(tAxis == T_AXIS) toAdd = getSpiral(fAxis,fLow,fUp,
          sAxis,sLow,sUp,z,tRow,c);
        else toAdd = getSpiral(fAxis,fLow,fUp,
          sAxis,sLow,sUp,z,t,tRow);

        lowSet = append(toAdd,lowSet);
      }
    }

    result = getMix(lowSet,upSet);
    return result;
  }

  /**
  * Clears the cache, starts the thread to load the new slices
  * we want to cache in the background.
  */
  private synchronized void updateCache() {
    if (DEBUG) System.out.println("UPDATING CACHE");

    clearCache();
  }

  protected void dimChange() {
    zapCache = true;
    synchronized (fs) {
      try {
        sizeZ = fs.getSizeZ(fileName);
        sizeT = fs.getSizeT(fileName);
        sizeC = fs.getSizeC(fileName);
        cache = new ImageProcessor[fs.getImageCount(fileName)];
      }
      catch (Exception exc) {
        if (DEBUG) System.out.println("Error reading size of file.");
        LociDataBrowser.exceptionMessage(exc);
      }
    }
  }

  protected void setIndicators(int aZ, int aT, int aC) {
    zInd = db.cw.zIndicator;
    tInd = db.cw.tIndicator;

    Vector zInCache = new Vector();
    Vector tInCache = new Vector();

    for(int i = 0;i < sizeZ;i++) {
      int index = -1;
      try {
        index = read.getIndex(fileName, i, aC - 1, aT - 1);
      }
      catch(Exception exc) {
        LociDataBrowser.exceptionMessage(exc);
      }

      if (index != -1 && cache[index] != null) zInCache.add(new Integer(i));
    }

    for(int i = 0;i < sizeT;i++) {
      int index = -1;
      try {
        index = read.getIndex(fileName, aZ - 1, aC - 1, i);
      }
      catch(Exception exc) {
        LociDataBrowser.exceptionMessage(exc);
      }

      if (index != -1 && cache[index] != null) tInCache.add(new Integer(i));
    }

    int[] loadCopy = new int[loadList.length];
    System.arraycopy(loadList, 0, loadCopy, 0, loadList.length);
    Arrays.sort(loadCopy);

    Vector zLoad = new Vector();
    Vector tLoad = new Vector();

    for(int i = 0;i < loadCopy.length;i++) {
      int[] coords = null;
      try {
        coords = read.getZCTCoords(fileName, loadCopy[i]);
      }
      catch(Exception exc) {
        LociDataBrowser.exceptionMessage(exc);
      }
      if (coords != null) {
        int myZ = coords[0];
        int myC = coords[1];
        int myT = coords[2];
        if (myC == aC - 1 && myT == aT - 1) zLoad.add(new Integer(myZ));
        if (myC == aC - 1 && myZ == aZ - 1) tLoad.add(new Integer(myT));
      }
    }

    if(DEBUG) {
      System.out.println("Z in Cache: " + zInCache);
      System.out.println("T in Cache: " + tInCache);
      System.out.println("Z in Load: " + zLoad);
      System.out.println("T in Load: " + tLoad);
    }

    int[] zC = makeInt(zInCache.toArray());
    int[] tC = makeInt(tInCache.toArray());
    int[] zL = makeInt(zLoad.toArray());
    int[] tL = makeInt(tLoad.toArray());

    Arrays.sort(zL);
    Arrays.sort(tL);

    zInd.setIndicator(zC, zL, sizeZ);
    tInd.setIndicator(tC, tL, sizeT);
  }

  private static int [] makeInt(Object[] array) {
    int [] result  = new int[array.length];
    for(int i = 0;i<result.length;i++) {
      result[i] = ((Integer) array[i]).intValue();
    }
    return result;
  }
  
  /**
  * Clears the cache and sets the new loadList that the loader
  * thread will use to start loading slices into the cache.
  */
  private void clearCache() {
    if (DEBUG) System.out.println("CLEARING CACHE");

    queue = new Stack();
    int[] oldIndex = null;

    boolean erase = true;
    if(!zapCache) {
      oldIndex = getToCache(true);
    }
    else {
      try {
        cache = new ImageProcessor[fs.getImageCount(fileName)];
      }
      catch(Exception exc){
        LociDataBrowser.exceptionMessage(exc);
      }
      erase = false;
    }

    oldZ = curZ;
    oldT = curT;
    oldC = curC;

    oldStrategy = strategy;
    oldMode = curMode;
    oldAxis = curAxis;

    oldBackZ = curBackZ;
    oldBackT = curBackT;
    oldBackC = curBackC;
    oldForwardZ = curForwardZ;
    oldForwardT = curForwardT;
    oldForwardC = curForwardC;

    oldZPriority = curZPriority;
    oldTPriority = curZPriority;
    oldCPriority = curCPriority;

    int[] newIndex = getToCache(false);

    if (DEBUG) {
      System.out.print("OldIndex = {");
      for (int i = 0; i<oldIndex.length; i++) {
        if ( i != 0) System.out.print(",");
        System.out.print(oldIndex[i]);
      }
      System.out.println("}");

      System.out.print("NewIndex = {");
      for (int i = 0; i<newIndex.length; i++) {
        if ( i != 0) System.out.print(",");
        System.out.print(newIndex[i]);
      }
      System.out.println("}");
      System.out.println("oldBackZ = " + oldBackZ + "; oldForwardZ = " +
        oldForwardZ + "; oldBackT = " + oldBackT + "; oldForwardT = " +
        oldForwardT + "; oldBackC = " + oldBackC + "; oldForwardC = " +
        oldForwardC);
      System.out.println("curBackZ = " + curBackZ + "; curForwardZ = " +
        curForwardZ + "; curBackT = " + curBackT + "; curForwardT = " +
        curForwardT + "; curBackC = " + curBackC + "; curForwardC = " +
        curForwardC);
    }

    loadList = new int[newIndex.length];
    System.arraycopy(newIndex, 0, loadList, 0, newIndex.length);
    for(int i = loadList.length -1;i>=0;i--) {
      Integer temp = new Integer(loadList[i]);
      queue.push(temp);
    }
    Arrays.sort(newIndex);

    if(erase) {
      for (int i = 0; i<oldIndex.length; i++) {
        if (Arrays.binarySearch(newIndex, oldIndex[i]) < 0)
          cache[oldIndex[i]] = null;
      }
    }

    if (DEBUG) System.out.println("Cache Size after clear: " + getSize());
  }

  // -- Runnable API methods --

  /** The thread method that does the slice loading.*/
  public void run() {
    quit = false;

    while(!quit) {
      if(!queue.empty()) {
        int index = ((Integer)queue.pop()).intValue();
        if(DEBUG) System.out.println("Loading index " + index);
        ImageProcessor imp = ImagePlusWrapper.getImageProcessor(
          fileName, read, index);
        cache[index] = imp;
        
        if(db.cw != null) {
          int aC = 1;
          zSel = db.cw.zSliceSel;
          tSel = db.cw.tSliceSel;
          if (sizeC != 0) aC = (db.cw.getC());
          if (quit) break;
          setIndicators(zSel.getValue(), tSel.getValue(), aC);
        }
      }
      if(db == null) quit = true;
    }
  }
  
}
