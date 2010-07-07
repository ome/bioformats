//
// Calibrator.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser and Stack Slicer. Copyright (C) 2005-@year@ Melissa Linkert,
Curtis Rueden and Christopher Peterson.

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

import ij.ImagePlus;
import ij.measure.Calibration;

import loci.formats.FormatTools;
import loci.formats.meta.IMetadata;

/**
 * Logic for calibrating images.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/Calibrator.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/Calibrator.java">SVN</a></dd></dl>
 */
public class Calibrator {

  // -- Fields --

  private ImportProcess process;

  // -- Constructor --

  public Calibrator(ImportProcess process) {
    this.process = process;
  }

  // -- Calibrator methods --

  /** Applies spatial calibrations to an image stack. */
  public void applyCalibration(ImagePlus imp) {
    final IMetadata meta = process.getOMEMetadata();
    final int series = (Integer) imp.getProperty(ImagePlusReader.PROP_SERIES);

    double xcal = Double.NaN, ycal = Double.NaN;
    double zcal = Double.NaN, tcal = Double.NaN;

    Double xd = meta.getPixelsPhysicalSizeX(series);
    if (xd != null) xcal = xd.floatValue();
    Double yd = meta.getPixelsPhysicalSizeY(series);
    if (yd != null) ycal = yd.floatValue();
    Double zd = meta.getPixelsPhysicalSizeZ(series);
    if (zd != null) zcal = zd.floatValue();
    Double td = meta.getPixelsTimeIncrement(series);
    if (td != null) tcal = td.floatValue();

    final boolean xcalPresent = !Double.isNaN(xcal);
    final boolean ycalPresent = !Double.isNaN(ycal);
    final boolean zcalPresent = !Double.isNaN(zcal);
    final boolean tcalPresent = !Double.isNaN(tcal);

    // if the physical width or physical height are missing,
    // assume that the width and height are equal
    if (xcalPresent && !ycalPresent) ycal = xcal;
    else if (ycalPresent && !xcalPresent) xcal = ycal;

    final boolean hasSpatial = xcalPresent || ycalPresent || zcalPresent;
    final boolean hasCalibration = hasSpatial || ycalPresent;

    if (hasCalibration) {
      // set calibration only if at least one value is present
      Calibration cal = new Calibration();
      if (hasSpatial) cal.setUnit("micron");
      if (xcalPresent) cal.pixelWidth = xcal;
      if (ycalPresent) cal.pixelHeight = ycal;
      if (zcalPresent) cal.pixelDepth = zcal;
      if (tcalPresent) cal.frameInterval = tcal;
      imp.setCalibration(cal);
    }

    String type = meta.getPixelsType(series).toString();
    int pixelType = FormatTools.pixelTypeFromString(type);

    // NB: INT32 is represented with FloatProcessor, so no need to calibrate.
    boolean signed = pixelType == FormatTools.INT8 ||
      pixelType == FormatTools.INT16; // || pixelType == FormatTools.INT32;

    // set calibration function, so that both signed and unsigned pixel
    // values are shown
    if (signed) {
      int bitsPerPixel = FormatTools.getBytesPerPixel(pixelType) * 8;
      double min = -1 * Math.pow(2, bitsPerPixel - 1);
      imp.getLocalCalibration().setFunction(Calibration.STRAIGHT_LINE,
        new double[] {min, 1.0}, "gray value");
    }
  }

}
