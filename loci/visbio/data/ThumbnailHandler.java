//
// ThumbnailHandler.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2004 Curtis Rueden.

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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import loci.visbio.util.LAFUtil;
import loci.visbio.util.MathUtil;
import visad.*;

/** Provides logic for handling data transform thumbnails. */
public class ThumbnailHandler
  implements ActionListener, Runnable, TransformListener
{

  // -- Fields --

  /** Data transform on which this thumbnail handler operates. */
  protected DataTransform data;

  /** Resolution of each thumbnail dimension. */
  protected int resolution = 96;

  /** Thumbnail data computed from data transform. */
  protected FlatField[] thumbs;

  /** Thumbnail disk cache for faster thumbnail retrieval. */
  protected ThumbnailCache cache;

  /** Flag indicating cache string ids are for use in default, global cache. */
  protected boolean global = false;

  /** Progress bar reporting background thumbnail generation progress. */
  protected JProgressBar progress;

  /** Button for toggling background thumbnail generation. */
  protected JButton toggle;

  /** Background thumbnail generation thread. */
  protected Thread loader;

  /** Number of thumbnails that have been generated. */
  protected int count;

  /** Flag indicating background thumbnail generation is enabled. */
  protected boolean on = true;


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

  /** Creates an image thumbnail from the given data. */
  public FlatField makeThumb(Data d) {
    if (d == null || !(d instanceof FlatField)) return null;
    FlatField ff = (FlatField) d;
    FunctionType ftype = (FunctionType) ff.getType();
    RealTupleType rtt = ftype.getDomain();
    int[] res = new int[rtt.getDimension()];
    for (int i=0; i<res.length; i++) res[i] = resolution;
    return DataSampling.resample(ff, res, null);
  }

  /** Starts or stops background thumbnail generation. */
  public void toggleGeneration(boolean on) {
    if (this.on != on) {
      this.on = on;
      if (on && count < thumbs.length) startGeneration();
    }
  }

  /** Clears thumbnails from memory and restarts background generation. */
  public void clear() {
    if (loader != null) {
      on = false;
      try { loader.join(); }
      catch (InterruptedException exc) { exc.printStackTrace(); }
      on = true;
    }
    thumbs = new FlatField[MathUtil.getRasterLength(data.getLengths())];
    count = 0;
    if (on) startGeneration();
  }

  /** Sets the resolution of generated thumbnails. */
  public void setResolution(int res) { resolution = res; }

  /** Sets the progress bar for reporting thumbnail generation progress. */
  public void setControls(JProgressBar bar, JButton button) {
    synchronized (data) {
      progress = bar;
      if (toggle != null) toggle.removeActionListener(this);
      toggle = button;
      if (toggle != null) toggle.addActionListener(this);
    }
    updateControls();
  }

  /** Gets the associated thumbnail disk cache object. */
  public ThumbnailCache getCache() { return cache; }


  // -- Internal ThumbnailHandler API methods --

  /**
   * Computes a thumbnail for the given dimensional position.
   * Subclasses may override this method to provide custom or
   * more efficient thumbnail creation behavior.
   */
  protected FlatField computeThumb(int[] pos) {
    Data d = data.getData(pos, 2);
    return makeThumb(d);
  }


  // -- ActionListener API methods --

  /** Handles toggling of background generation. */
  public void actionPerformed(ActionEvent e) { toggleGeneration(!on); }


  // -- Runnable API methods --

  /** Loads all thumbnails in the background. */
  public void run() {
    int[] lengths = data.getLengths();
    for (int i=count; i<thumbs.length; i++) {
      updateControls();
      if (thumbs[i] != null) {
        count++;
        continue;
      }
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
      if (!on) break;
    }
    updateControls();
  }


  // -- TransformListener API methods --

  /** Handles data transform parameter changes. */
  public void transformChanged(TransformEvent e) {
    int id = e.getId();
    if (id == TransformEvent.DATA_CHANGED) clear();
  }


  // -- Helper methods --

  /** Updates the linked progress bar to match thumbnail generation status. */
  private void updateControls() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        synchronized (data) {
          if (progress == null || toggle == null) return;
          progress.setMaximum(thumbs.length);
          if (count < thumbs.length) {
            if (on) {
              progress.setString("Generating thumbnail " +
                (count + 1) + " of " + thumbs.length);
            }
            else {
              progress.setString(count + " of " + thumbs.length +
                " thumbnails generated");
            }
            toggle.setEnabled(true);
          }
          else {
            progress.setString(count + " thumbnail" +
              (count == 1 ? "" : "s") + " generated");
            toggle.setEnabled(false);
          }
          progress.setValue(count);
          if (on) {
            toggle.setText("Stop");
            if (!LAFUtil.isMacLookAndFeel()) toggle.setMnemonic('s');
          }
          else {
            toggle.setText("Go");
            if (!LAFUtil.isMacLookAndFeel()) toggle.setMnemonic('g');
          }
        }
      }
    });
  }

  /** Generates thumbnails in a new background thread. */
  private void startGeneration() {
    loader = new Thread(this,
      "VisBio-ThumbnailGenerationThread-" + data.getName());
    loader.setPriority(Thread.MIN_PRIORITY);
    loader.start();
  }

}
