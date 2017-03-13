/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.plugins.in;

import java.awt.Dialog;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import loci.common.DebugTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.gui.AWTImageTools;
import loci.formats.gui.BufferedImageReader;
import loci.plugins.BF;

/**
 * Loads thumbnails for Bio-Formats Importer
 * series chooser in a separate thread.
 */
public class ThumbLoader implements Runnable {

  // -- Fields --

  private BufferedImageReader ir;
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
    this.ir = BufferedImageReader.makeBufferedImageReader(ir);
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
  @Override
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
      loadThumb(ir, ii, p[ii], false);
      if (dialog != null) dialog.validate();
    }
  }

  // -- Helper methods --

  public static void loadThumb(BufferedImageReader thumbReader,
    int series, Panel panel, boolean quiet)
  {
    BF.status(quiet, "Reading thumbnail for series #" + (series + 1));
    // open middle image thumbnail in smallest pyramid resolution
    List<CoreMetadata> core = thumbReader.getCoreMetadataList();
    int index = series;
    for (int i=0; i<=index; i++) {
      if (core.get(i).resolutionCount > 1 &&
        i + core.get(i).resolutionCount > index)
      {
        index = i + core.get(i).resolutionCount - 1;
        break;
      }
    }
    thumbReader.setSeries(index);

    int z = thumbReader.getSizeZ() / 2;
    int t = thumbReader.getSizeT() / 2;
    int ndx = thumbReader.getIndex(z, 0, t);
    Exception exc = null;
    try {
      BufferedImage thumb = thumbReader.openThumbImage(ndx);
      thumb = AWTImageTools.autoscale(thumb);
      ImageIcon icon = new ImageIcon(thumb);
      panel.removeAll();
      panel.add(new JLabel(icon));
    }
    catch (FormatException e) { exc = e; }
    catch (IOException e) { exc = e; }
    if (exc != null) {
      BF.warn(quiet, "Error loading thumbnail for series #" + (series + 1));
      BF.debug(DebugTools.getStackTrace(exc));
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

    @Override
    public int compareTo(SeriesInfo info) {
      return size - info.size;
    }

  }

}
