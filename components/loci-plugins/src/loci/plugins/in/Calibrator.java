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

import java.util.Arrays;

import loci.formats.FormatTools;
import loci.formats.meta.IMetadata;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveInteger;

/**
 * Logic for calibrating images.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/Calibrator.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/Calibrator.java">SVN</a></dd></dl>
 */
public class Calibrator {

  // -- Fields --

  private final ImportProcess process;

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

    boolean xcalPresent = !Double.isNaN(xcal);
    boolean ycalPresent = !Double.isNaN(ycal);
    boolean zcalPresent = !Double.isNaN(zcal);
    boolean tcalPresent = !Double.isNaN(tcal);

    // HACK: If the physical width or height are missing,
    // assume that the width and height are equal.
    if (xcalPresent && !ycalPresent) ycal = xcal;
    else if (ycalPresent && !xcalPresent) xcal = ycal;

    // HACK: If the time increment is missing,
    // average any variable time interval values.
    if (!tcalPresent) tcal = computeVariableTimeInterval(meta, series);

    xcalPresent = !Double.isNaN(xcal);
    ycalPresent = !Double.isNaN(ycal);
    zcalPresent = !Double.isNaN(zcal);
    tcalPresent = !Double.isNaN(tcal);
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

  private double computeVariableTimeInterval(IMetadata meta, int series) {
    // collect variable time interval values
    final PositiveInteger sizeT = meta.getPixelsSizeT(series);
    final int tSize = sizeT == null ? 1 : sizeT.getValue();
    final int planeCount = meta.getPlaneCount(series);
    final double[] deltas = new double[tSize];
    Arrays.fill(deltas, Double.NaN);
    for (int p=0; p<planeCount; p++) {
      final NonNegativeInteger theZ = meta.getPlaneTheZ(series, p);
      final NonNegativeInteger theC = meta.getPlaneTheC(series, p);
      final NonNegativeInteger theT = meta.getPlaneTheT(series, p);
      if (theZ == null || theC == null || theT == null) continue;
      if (theZ.getValue() != 0 || theC.getValue() != 0) continue;
      // store delta T value at appropriate index
      final int t = theT.getValue();
      if (t >= tSize) continue;
      final Double deltaT = meta.getPlaneDeltaT(series, p);
      if (deltaT == null) continue;
      deltas[t] = deltaT;
    }
    // average delta T differences
    double tiTotal = 0;
    int tiCount = 0;
    for (int t=1; t<tSize; t++) {
      double delta1 = deltas[t - 1];
      double delta2 = deltas[t];
      if (Double.isNaN(delta1) || Double.isNaN(delta2)) continue;
      tiTotal += delta2 - delta1;
      tiCount++;
    }
    if (tiCount == 0) return Double.NaN;
    return (float) (tiTotal / tiCount);
  }

}
