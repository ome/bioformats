//
// ThumbnailHandler.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2006 Curtis Rueden.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.visbio.data;

import java.rmi.RemoteException;
import loci.visbio.BioTask;
import loci.visbio.TaskManager;
import loci.visbio.util.*;
import visad.*;

/** Provides logic for handling data transform thumbnails. */
public class ThumbnailHandler implements Runnable, TransformListener {

  // -- Fields --

  /** Data transform on which this thumbnail handler operates. */
  protected DataTransform data;

  /** Resolution of each thumbnail dimension. */
  protected int[] resolution = {
    DataManager.DEFAULT_THUMBNAIL_RESOLUTION,
    DataManager.DEFAULT_THUMBNAIL_RESOLUTION
  };

  /** Thumbnail data computed from data transform. */
  protected FlatField[] thumbs;

  /** Thumbnail disk cache for faster thumbnail retrieval. */
  protected ThumbnailCache cache;

  /** Flag indicating cache string ids are for use in default, global cache. */
  protected boolean global = false;

  /**
   * Task manager used for reporting background thumbnail generation progress.
   */
  protected TaskManager tm;

  /** Background thumbnail generation thread. */
  protected Thread loader;

  /** Number of thumbnails that have been generated. */
  protected int count;

  /** Flag indicating background thumbnail generation is enabled. */
  protected boolean on = false;


  // -- Constructor --

  /** Creates a thumbnail handler. */
  public ThumbnailHandler(DataTransform data, String filename) {
    this.data = data;
    data.addTransformListener(this);
    if (filename != null) {
      cache = new ThumbnailCache(filename);
      global = cache.isDefault();
    }
    clear();
  }


  // -- ThumbnailHandler API methods --

  /** Gets the thumbnail at the given dimensional position. */
  public FlatField getThumb(int[] pos) {
    int ndx = MathUtil.positionToRaster(data.getLengths(), pos);
    return ndx >= 0 && ndx < thumbs.length ? thumbs[ndx] : null;
  }

  /** Sets the thumbnail at the given dimensional position. */
  public void setThumb(int[] pos, FlatField thumb) {
    int ndx = MathUtil.positionToRaster(data.getLengths(), pos);
    if (ndx >= 0 && ndx < thumbs.length) thumbs[ndx] = thumb;
  }

  /** Sets resolution of computed thumbnails. */
  public void setResolution(int[] res) { resolution = res; }

  /** Creates an image thumbnail from the given data. */
  public FlatField makeThumb(Data d) {
    if (d == null || !(d instanceof FlatField)) return null;
    FlatField ff = (FlatField) d;
    FunctionType ftype = (FunctionType) ff.getType();
    RealTupleType rtt = ftype.getDomain();
    int[] res = new int[rtt.getDimension()];
    for (int i=0; i<res.length; i++) res[i] = resolution[i];
    try { return DataUtil.resample(ff, res, null); }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return null;
  }

  /** Starts or stops background thumbnail generation. */
  public void toggleGeneration(boolean on) {
    if (this.on != on) {
      this.on = on;
      if (on && count < thumbs.length) startGeneration();
    }
  }

  /**
   * Clears thumbnails from memory, restarting background
   * generation if auto-generation is enabled.
   */
  public void clear() {
    if (loader != null) {
      boolean oldOn = on;
      on = false;
      try { loader.join(); }
      catch (InterruptedException exc) { exc.printStackTrace(); }
      on = oldOn;
    }
    thumbs = new FlatField[MathUtil.getRasterLength(data.getLengths())];
    count = 0;
    if (on) startGeneration();
  }

  /**
   * Sets the task manager to use for reporting thumbnail generation progress.
   */
  public void setTaskManager(TaskManager tm) {
    this.tm = tm;
  }

  /** Gets the associated thumbnail disk cache object. */
  public ThumbnailCache getCache() { return cache; }


  // -- Internal ThumbnailHandler API methods --

  /** Computes the ith thumbnail. */
  protected void loadThumb(int i) {
    if (thumbs[i] != null) return;

    int[] lengths = data.getLengths();
    String id = data.getCacheId(
      MathUtil.rasterToPosition(lengths, i), global);

    // attempt to grab thumbnail from the disk cache
    boolean cached = false;
    if (cache != null) {
      FlatField ff = cache.retrieve(id);
      if (ff != null) {
        thumbs[i] = ff;
        cached = true;
      }
    }

    if (!cached) {
      // compute thumbnail from data object
      thumbs[i] = computeThumb(MathUtil.rasterToPosition(lengths, i));
      if (cache != null && thumbs[i] != null) cache.store(id, thumbs[i]);
    }

    count++;
  }

  /**
   * Computes a thumbnail for the given dimensional position.
   * Subclasses may override this method to provide custom or
   * more efficient thumbnail creation behavior.
   */
  protected FlatField computeThumb(int[] pos) {
    Data d = data.getData(pos, 2, null);
    return makeThumb(d);
  }


  // -- Runnable API methods --

  /** Loads all thumbnails in the background. */
  public void run() {
    BioTask task = null;
    for (int i=count; i<thumbs.length; i++) {
      if (task == null && tm != null) {
        // register a task for thumbnail generation
        task = tm.createTask(data.getName());
        task.setStoppable(true);
      }
      if (task != null) {
        if (task.isStopped()) break;
        String message = on && count < thumbs.length ?
          ("Thumbnail " + (count + 1) + " of " + thumbs.length) :
          (count + " of " + thumbs.length + " thumbnails");
        task.setStatus(count, thumbs.length, message);
      }
      loadThumb(i);
      if (!on) break;
    }
    if (task != null) {
      task.setCompleted();
      task = null;
    }
  }


  // -- TransformListener API methods --

  /** Handles data transform parameter changes. */
  public void transformChanged(TransformEvent e) {
    int id = e.getId();
    if (id == TransformEvent.DATA_CHANGED) clear();
  }


  // -- Helper methods --

  /** Generates thumbnails in a new background thread. */
  private void startGeneration() {
    loader = new Thread(this,
      "VisBio-ThumbnailGenerationThread-" + data.getName());
    loader.setPriority(Thread.MIN_PRIORITY);
    loader.start();
  }

}
