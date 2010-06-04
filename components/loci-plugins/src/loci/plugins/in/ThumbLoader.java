//
// ThumbLoader.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser, Stack Colorizer and Stack Slicer. Copyright (C) 2005-@year@
Melissa Linkert, Curtis Rueden and Christopher Peterson.

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

package loci.plugins.in;

import java.awt.Dialog;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.gui.AWTImageTools;
import loci.formats.gui.BufferedImageReader;
import loci.plugins.BF;
import loci.plugins.util.WindowTools;

/**
 * Loads thumbnails for Bio-Formats Importer
 * series chooser in a separate thread.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/ThumbLoader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/ThumbLoader.java">SVN</a></dd></dl>
 */
public class ThumbLoader implements Runnable {

  // -- Fields --

  private BufferedImageReader ir;
  private Panel[] p;
  private Dialog dialog;
  private boolean scale;
  private boolean stop;
  private Thread loader;

  // -- Constructor --

  /**
   * Constructs a new thumbnail loader.
   * @param ir the reader to use for obtaining the thumbnails
   * @param p the panels upon which to populate the results
   * @param dialog the dialog containing the panels
   */
  public ThumbLoader(IFormatReader ir, Panel[] p, Dialog dialog, boolean scale)
  {
    this.ir = BufferedImageReader.makeBufferedImageReader(ir);
    this.p = p;
    this.dialog = dialog;
    this.scale = scale;
    loader = new Thread(this, "BioFormats-ThumbLoader");
    loader.start();
  }

  // -- ThumbLoader methods --

  /** Instructs the thumbnail loader to stop loading thumbnails. */
  public void stop() {
    if (loader == null) return;
    stop = true;
    BF.status(false, "Canceling thumbnail generation");
    try {
      loader.join();
      loader = null;
    }
    catch (InterruptedException exc) {
      exc.printStackTrace();
    }
    BF.status(false, "");
  }

  // -- Runnable methods --

  /** Does the work of loading the thumbnails. */
  public void run() {
    BF.status(false, "Gathering series information");
    int seriesCount = ir.getSeriesCount();

    // find image plane for each series and sort by size
    SeriesInfo[] info = new SeriesInfo[seriesCount];
    for (int i=0; i<seriesCount; i++) {
      if (stop) return;
      ir.setSeries(i);
      info[i] = new SeriesInfo(i, ir.getSizeX() * ir.getSizeY());
    }
    if (stop) return;
    Arrays.sort(info);

    // open each thumbnail, fastest/smallest first
    for (int i=0; i<seriesCount; i++) {
      if (stop) return;
      final int ii = info[i].index;
      loadThumb(ir, ii, p[ii], false, scale);
      if (dialog != null) dialog.validate();
    }
  }

  // -- Helper methods --

  public static void loadThumb(BufferedImageReader thumbReader,
    int series, Panel panel, boolean quiet, boolean autoscale)
  {
    BF.status(quiet, "Reading thumbnail for series #" + (series + 1));
    thumbReader.setSeries(series);
    // open middle image thumbnail
    int z = thumbReader.getSizeZ() / 2;
    int t = thumbReader.getSizeT() / 2;
    int ndx = thumbReader.getIndex(z, 0, t);
    Exception exc = null;
    try {
      BufferedImage thumb = thumbReader.openThumbImage(ndx);
      boolean notFloat = thumbReader.getPixelType() != FormatTools.FLOAT;
      if (autoscale && notFloat) thumb = AWTImageTools.autoscale(thumb);
      ImageIcon icon = new ImageIcon(thumb);
      panel.removeAll();
      panel.add(new JLabel(icon));
    }
    catch (FormatException e) { exc = e; }
    catch (IOException e) { exc = e; }
    if (exc != null) {
      WindowTools.reportException(exc, quiet,
        "Error loading thumbnail for series #" + (series + 1));
    }
  }

  // -- Helper classes --

  /** Helper class for sorting series by image plane size. */
  public class SeriesInfo implements Comparable<SeriesInfo> {

    private int index, size;

    public SeriesInfo(int index, int size) {
      this.index = index;
      this.size = size;
    }

    public int compareTo(SeriesInfo info) {
      return size - info.size;
    }

  }

}
