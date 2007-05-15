//
// ThumbLoader.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
4D Data Browser, Bio-Formats Importer, Bio-Formats Exporter and OME plugins.
Copyright (C) 2006-@year@ Melissa Linkert, Christopher Peterson,
Curtis Rueden, Philip Huettl and Francis Wong.

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

package loci.plugins;

import ij.IJ;
import java.awt.Dialog;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import loci.formats.FormatException;
import loci.formats.IFormatReader;

/**
 * Loads thumbnails for Bio-Formats Importer
 * series chooser in a separate thread.
 */
public class ThumbLoader implements Runnable {

  // -- Fields --

  private IFormatReader ir;
  private Panel[] p;
  private Dialog dialog;
  private boolean stop;
  private Thread loader;

  // -- Constructor --

  /**
   * Constructs a new thumbnail loader.
   * @param ir the reader to use for obtaining the thumbnails
   * @param p the panels upon which to populate the results
   * @param dialog the dialog containing the panels
   */
  public ThumbLoader(IFormatReader ir, Panel[] p, Dialog dialog) {
    this.ir = ir;
    this.p = p;
    this.dialog = dialog;
    loader = new Thread(this, "BioFormats-ThumbLoader");
    loader.start();
  }

  // -- ThumbLoader methods --

  /** Instructs the thumbnail loader to stop loading thumbnails. */
  public void stop() {
    if (loader == null) return;
    stop = true;
    IJ.showStatus("Canceling thumbnail generation");
    try {
      loader.join();
      loader = null;
    }
    catch (InterruptedException exc) {
      exc.printStackTrace();
    }
    IJ.showStatus("");
  }

  // -- Runnable methods --

  /** Does the work of loading the thumbnails. */
  public void run() {
    try {
      IJ.showStatus("Gathering series information");
      int seriesCount = ir.getSeriesCount();

      // find image plane for each series and sort by size
      SeriesInfo[] info = new SeriesInfo[seriesCount];
      for (int i=0; i<seriesCount && !stop; i++) {
        ir.setSeries(i);
        info[i] = new SeriesInfo(i, ir.getSizeX() * ir.getSizeY());
      }
      Arrays.sort(info);

      // open each thumbnail, fastest/smallest first
      for (int i=0; i<seriesCount && !stop; i++) {
        final int ii = info[i].index;
        IJ.showStatus("Reading thumbnail for series #" + (ii + 1));
        ir.setSeries(ii);
        // open middle image thumbnail
        int z = ir.getSizeZ() / 2;
        int t = ir.getSizeT() / 2;
        int ndx = ir.getIndex(z, 0, t);
        BufferedImage thumb = ir.openThumbImage(ndx);
        ImageIcon icon = new ImageIcon(thumb);
        p[ii].removeAll();
        p[ii].add(new JLabel(icon));
        if (dialog != null) dialog.validate();
      }
    }
    catch (IOException exc) {
      exc.printStackTrace();
    }
    catch (FormatException exc) {
      exc.printStackTrace();
    }
  }

  // -- Helper classes --

  public class SeriesInfo implements Comparable {

    private int index, size;

    public SeriesInfo(int index, int size) {
      this.index = index;
      this.size = size;
    }

    public int compareTo(Object o) {
      SeriesInfo info = (SeriesInfo) o;
      return size - info.size;
    }

  }

}
