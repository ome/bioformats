//
// ICurveFitter.java
//

/*
SLIM Plotter application and curve fitting library for
combined spectral lifetime visualization and analysis.
Copyright (C) 2006-@year@ Curtis Rueden and Eric Kjellman.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.slim.fit;

/**
 * Interface for curve fitting algorithms.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/slim-plotter/src/loci/slim/fit/ICurveFitter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/slim-plotter/src/loci/slim/fit/ICurveFitter.java">SVN</a></dd></dl>
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 */
public interface ICurveFitter {

  /**
   * iterate() runs through one iteration of whatever curve fitting
   * technique this curve fitter uses. This will generally update the
   * information returned by getCurve and getChiSquaredError.
   */
  void iterate();

  /** Returns the number of iterations so far. */
  int getIterations();

  /** Returns the Chi Squared Error of the current curve estimate. */
  double getChiSquaredError();

  double getChiSquaredError(double[][] estCurve);

  /**
   * Returns the Reduced Chi Squared Error of the current curve estimate
   * This is based on the number of datapoints in data and the number
   * of exponentials in setComponentCount.
   */
  double getReducedChiSquaredError();

  double getReducedChiSquaredError(double[][] estCurve);

  double[] getEstimates(double[][] estimate);

  /**
   * Sets the data to be used to generate curve estimates.
   * Single dimension of data... time values are index, since
   * we can assume that the datapoints are evenly spaced.
   */
  void setData(int[] data);

  void setData(int[] data, int first, int last);

  /**
   * Gets the data to be used to generate curve estimates.
   * Single dimension of data... time values are index, since
   * we can assume that the datapoints are evenly spaced.
   */
  int[] getData();

  int getFirst();

  int getLast();

  /** Sets how many exponentials are expected to be fitted. */
  void setComponentCount(int numExp);

  /** Returns the number of exponentials to be fitted. */
  int getComponentCount();

  /** Initializes the curve fitter with a starting curve estimate. */
  void estimate();

  /**
   * Returns the current curve estimate.
   * Return size is expected to be [components][3]
   * For each exponential of the form ae^-bt+c,
   * [][0] is a, [1] is b, [2] is c.
   **/
  double[][] getCurve();

  /**
   * Sets the current curve estimate, useful if information about the
   * curve is already known.
   * See getCurve for information about the array to pass.
   */
  void setCurve(double[][] curve);

}
