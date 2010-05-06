//
// RangeDialog.java
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

import ij.gui.GenericDialog;
import loci.formats.IFormatReader;
import loci.plugins.prefs.OptionsDialog;
import loci.plugins.util.WindowTools;

/**
 * Bio-Formats Importer range chooser dialog box.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/RangeDialog.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/RangeDialog.java">SVN</a></dd></dl>
 */
public class RangeDialog extends OptionsDialog {

  // -- Fields --

  /** LOCI plugins configuration. */
  protected ImporterOptions options;

  protected IFormatReader r;
  protected String[] seriesLabels;

  // -- Constructor --

  /**
   * Creates a range chooser dialog for the Bio-Formats Importer.
   *
   * @param r The reader to use for extracting details of each series.
   * @param seriesLabels Label to display to user identifying each series
   */
  public RangeDialog(ImporterOptions options,
    IFormatReader r, String[] seriesLabels)
  {
    super(options);
    this.options = options;
    this.r = r;
    this.seriesLabels = seriesLabels;
  }

  // -- OptionsDialog methods --

  /**
   * Gets the range of image planes to open from macro options,
   * or user prompt if necessary.
   *
   * @return status of operation
   */
  public int showDialog() {
    int seriesCount = r.getSeriesCount();

    // prompt user to specify series ranges (or grab from macro options)
    GenericDialog gd = new GenericDialog("Bio-Formats Range Options");
    for (int s=0; s<seriesCount; s++) {
      if (!options.isSeriesOn(s)) continue;
      r.setSeries(s);
      gd.addMessage(seriesLabels[s].replaceAll("_", " "));
      String suffix = seriesCount > 1 ? "_" + (s + 1) : "";
      //if (r.isOrderCertain()) {
      if (r.getEffectiveSizeC() > 1) {
        gd.addNumericField("C_Begin" + suffix, options.getCBegin(s) + 1, 0);
        gd.addNumericField("C_End" + suffix, options.getCEnd(s) + 1, 0);
        gd.addNumericField("C_Step" + suffix, options.getCStep(s), 0);
      }
      if (r.getSizeZ() > 1) {
        gd.addNumericField("Z_Begin" + suffix, options.getZBegin(s) + 1, 0);
        gd.addNumericField("Z_End" + suffix, options.getZEnd(s) + 1, 0);
        gd.addNumericField("Z_Step" + suffix, options.getZStep(s), 0);
      }
      if (r.getSizeT() > 1) {
        gd.addNumericField("T_Begin" + suffix, options.getTBegin(s) + 1, 0);
        gd.addNumericField("T_End" + suffix, options.getTEnd(s) + 1, 0);
        gd.addNumericField("T_Step" + suffix, options.getTStep(s), 0);
      }
      //}
      //else {
      //  gd.addNumericField("Begin" + suffix, options.getCBegin(s) + 1, 0);
      //  gd.addNumericField("End" + suffix, options.getCEnd(s) + 1, 0);
      //  gd.addNumericField("Step" + suffix, options.getCStep(s), 0);
      //}
    }
    WindowTools.addScrollBars(gd);
    gd.showDialog();
    if (gd.wasCanceled()) return STATUS_CANCELED;

    for (int s=0; s<seriesCount; s++) {
      if (!options.isSeriesOn(s)) continue;
      r.setSeries(s);
      int sizeC = r.getEffectiveSizeC();
      int sizeZ = r.getSizeZ();
      int sizeT = r.getSizeT();
      boolean certain = r.isOrderCertain();
      
      int cBegin = options.getCBegin(s);
      int cEnd = options.getCEnd(s);
      int cStep = options.getCStep(s);
      int zBegin = options.getZBegin(s);
      int zEnd = options.getZEnd(s);
      int zStep = options.getZStep(s);
      int tBegin = options.getTBegin(s);
      int tEnd = options.getTEnd(s);
      int tStep = options.getTStep(s);

      //if (certain) {
      if (r.getEffectiveSizeC() > 1) {
        cBegin = (int) gd.getNextNumber() - 1;
        cEnd = (int) gd.getNextNumber() - 1;
        cStep = (int) gd.getNextNumber();
      }
      if (r.getSizeZ() > 1) {
        zBegin = (int) gd.getNextNumber() - 1;
        zEnd = (int) gd.getNextNumber() - 1;
        zStep = (int) gd.getNextNumber();
      }
      if (r.getSizeT() > 1) {
        tBegin = (int) gd.getNextNumber() - 1;
        tEnd = (int) gd.getNextNumber() - 1;
        tStep = (int) gd.getNextNumber();
      }
      //}
      //else {
      //  cBegin = (int) gd.getNextNumber() - 1;
      //  cEnd = (int) gd.getNextNumber() - 1;
      //  cStep = (int) gd.getNextNumber();
      //}
      int maxC = certain ? sizeC : r.getImageCount();
      if (cBegin < 0) cBegin = 0;
      if (cBegin >= maxC) cBegin = maxC - 1;
      if (cEnd < cBegin) cEnd = cBegin;
      if (cEnd >= maxC) cEnd = maxC - 1;
      if (cStep < 1) cStep = 1;
      if (zBegin < 0) zBegin = 0;
      if (zBegin >= sizeZ) zBegin = sizeZ - 1;
      if (zEnd < zBegin) zEnd = zBegin;
      if (zEnd >= sizeZ) zEnd = sizeZ - 1;
      if (zStep < 1) zStep = 1;
      if (tBegin < 0) tBegin = 0;
      if (tBegin >= sizeT) tBegin = sizeT - 1;
      if (tEnd < tBegin) tEnd = tBegin;
      if (tEnd >= sizeT) tEnd = sizeT - 1;
      if (tStep < 1) tStep = 1;
      
      options.setCBegin(s, cBegin);
      options.setCEnd(s, cEnd);
      options.setCStep(s, cStep);
      options.setZBegin(s, zBegin);
      options.setZEnd(s, zEnd);
      options.setZStep(s, zStep);
      options.setTBegin(s, tBegin);
      options.setTEnd(s, tEnd);
      options.setTStep(s, tStep);
    }

    return STATUS_OK;
  }

}
