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

package loci.plugins.importer;

import ij.gui.GenericDialog;
import loci.formats.IFormatReader;
import loci.plugins.util.OptionsDialog;
import loci.plugins.util.WindowTools;

/**
 * Bio-Formats Importer range chooser dialog box.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/importer/RangeDialog.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/importer/RangeDialog.java">SVN</a></dd></dl>
 */
public class RangeDialog extends OptionsDialog {

  // -- Fields --

  /** LOCI plugins configuration. */
  protected ImporterOptions options;

  protected IFormatReader r;
  protected boolean[] series;
  protected String[] seriesLabels;
  protected int[] cBegin, cEnd, cStep;
  protected int[] zBegin, zEnd, zStep;
  protected int[] tBegin, tEnd, tStep;

  // -- Constructor --

  /**
   * Creates a range chooser dialog for the Bio-Formats Importer.
   *
   * @param r The reader to use for extracting details of each series.
   * @param series Boolean array indicating the series
   *   for which ranges should be determined.
   * @param seriesLabels Label to display to user identifying each series
   * @param cBegin First C index to include (populated by showDialog).
   * @param cEnd Last C index to include (populated by showDialog).
   * @param cStep C dimension step size (populated by showDialog).
   * @param zBegin First Z index to include (populated by showDialog).
   * @param zEnd Last Z index to include (populated by showDialog).
   * @param zStep Z dimension step size (populated by showDialog).
   * @param tBegin First T index to include (populated by showDialog).
   * @param tEnd Last T index to include (populated by showDialog).
   * @param tStep T dimension step size (populated by showDialog).
   */
  public RangeDialog(ImporterOptions options, IFormatReader r,
    boolean[] series, String[] seriesLabels,
    int[] cBegin, int[] cEnd, int[] cStep,
    int[] zBegin, int[] zEnd, int[] zStep,
    int[] tBegin, int[] tEnd, int[] tStep)
  {
    super(options);
    this.options = options;
    this.r = r;
    this.series = series;
    this.seriesLabels = seriesLabels;
    this.cBegin = cBegin;
    this.cEnd = cEnd;
    this.cStep = cStep;
    this.zBegin = zBegin;
    this.zEnd = zEnd;
    this.zStep = zStep;
    this.tBegin = tBegin;
    this.tEnd = tEnd;
    this.tStep = tStep;
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
    for (int i=0; i<seriesCount; i++) {
      if (!series[i]) continue;
      r.setSeries(i);
      gd.addMessage(seriesLabels[i].replaceAll("_", " "));
      String s = seriesCount > 1 ? "_" + (i + 1) : "";
      //if (r.isOrderCertain()) {
      if (r.getEffectiveSizeC() > 1) {
        gd.addNumericField("C_Begin" + s, cBegin[i] + 1, 0);
        gd.addNumericField("C_End" + s, cEnd[i] + 1, 0);
        gd.addNumericField("C_Step" + s, cStep[i], 0);
      }
      if (r.getSizeZ() > 1) {
        gd.addNumericField("Z_Begin" + s, zBegin[i] + 1, 0);
        gd.addNumericField("Z_End" + s, zEnd[i] + 1, 0);
        gd.addNumericField("Z_Step" + s, zStep[i], 0);
      }
      if (r.getSizeT() > 1) {
        gd.addNumericField("T_Begin" + s, tBegin[i] + 1, 0);
        gd.addNumericField("T_End" + s, tEnd[i] + 1, 0);
        gd.addNumericField("T_Step" + s, tStep[i], 0);
      }
      //}
      //else {
      //  gd.addNumericField("Begin" + s, cBegin[i] + 1, 0);
      //  gd.addNumericField("End" + s, cEnd[i] + 1, 0);
      //  gd.addNumericField("Step" + s, cStep[i], 0);
      //}
    }
    WindowTools.addScrollBars(gd);
    gd.showDialog();
    if (gd.wasCanceled()) return STATUS_CANCELED;

    for (int i=0; i<seriesCount; i++) {
      if (!series[i]) continue;
      r.setSeries(i);
      int sizeC = r.getEffectiveSizeC();
      int sizeZ = r.getSizeZ();
      int sizeT = r.getSizeT();
      boolean certain = r.isOrderCertain();

      //if (certain) {
      if (r.getEffectiveSizeC() > 1) {
        cBegin[i] = (int) gd.getNextNumber() - 1;
        cEnd[i] = (int) gd.getNextNumber() - 1;
        cStep[i] = (int) gd.getNextNumber();
      }
      if (r.getSizeZ() > 1) {
        zBegin[i] = (int) gd.getNextNumber() - 1;
        zEnd[i] = (int) gd.getNextNumber() - 1;
        zStep[i] = (int) gd.getNextNumber();
      }
      if (r.getSizeT() > 1) {
        tBegin[i] = (int) gd.getNextNumber() - 1;
        tEnd[i] = (int) gd.getNextNumber() - 1;
        tStep[i] = (int) gd.getNextNumber();
      }
      //}
      //else {
      //  cBegin[i] = (int) gd.getNextNumber() - 1;
      //  cEnd[i] = (int) gd.getNextNumber() - 1;
      //  cStep[i] = (int) gd.getNextNumber();
      //}
      int maxC = certain ? sizeC : r.getImageCount();
      if (cBegin[i] < 0) cBegin[i] = 0;
      if (cBegin[i] >= maxC) cBegin[i] = maxC - 1;
      if (cEnd[i] < cBegin[i]) cEnd[i] = cBegin[i];
      if (cEnd[i] >= maxC) cEnd[i] = maxC - 1;
      if (cStep[i] < 1) cStep[i] = 1;
      if (zBegin[i] < 0) zBegin[i] = 0;
      if (zBegin[i] >= sizeZ) zBegin[i] = sizeZ - 1;
      if (zEnd[i] < zBegin[i]) zEnd[i] = zBegin[i];
      if (zEnd[i] >= sizeZ) zEnd[i] = sizeZ - 1;
      if (zStep[i] < 1) zStep[i] = 1;
      if (tBegin[i] < 0) tBegin[i] = 0;
      if (tBegin[i] >= sizeT) tBegin[i] = sizeT - 1;
      if (tEnd[i] < tBegin[i]) tEnd[i] = tBegin[i];
      if (tEnd[i] >= sizeT) tEnd[i] = sizeT - 1;
      if (tStep[i] < 1) tStep[i] = 1;
    }

    return STATUS_OK;
  }

}
