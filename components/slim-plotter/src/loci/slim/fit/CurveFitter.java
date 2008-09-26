//
// CurveFitter.java
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
 * Base interface for curve fitting algorithms.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/slim-plotter/src/loci/slim/fit/CurveFitter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/slim-plotter/src/loci/slim/fit/CurveFitter.java">SVN</a></dd></dl>
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 */
public abstract class CurveFitter {

  protected int[] curveData;
  protected double[][] curveEstimate;
  protected int firstindex;
  protected int lastindex;

  /**
   * iterate() runs through one iteration of whatever curve fitting
   * technique this curve fitter uses. This will generally update the
   * information returned by getCurve and getChiSquaredError.
   */
  public abstract void iterate();

  /** Returns the Chi Squared Error of the current curve estimate. */
  public double getChiSquaredError() {
    return getChiSquaredError(curveEstimate);
  }

  public double getChiSquaredError(double[][] estCurve) {
    double total = 0.0d;
    double[] expected = getEstimates(curveData, estCurve);
    for (int i = firstindex; i < curveData.length && i <= lastindex; i++) {
      if (expected[i] > 0) {
        double observed = curveData[i];
        double term = (observed - expected[i]);
        // (o-e)^2
        term *= term;
        // (o-e)^2 / e
        term /= expected[i];
        //System.out.println("Obs: " + observed +
        //  " Expect: " + expected[i] + " Term: " + term);
        total += term;
      }
    }
    return total;
  }

  /**
   * Returns the Reduced Chi Squared Error of the current curve estimate
   * This is based on the number of datapoints in data and the number
   * of exponentials in setComponentCount.
   */
  public double getReducedChiSquaredError() {
    return getReducedChiSquaredError(curveEstimate);
  }

  public double getReducedChiSquaredError(double[][] estCurve) {
    int df = 1 + (estCurve.length * 2);
    int datapoints = curveData.length;
    if (datapoints - df > 0) {
      return getChiSquaredError(estCurve) / (datapoints - df);
    }
    return Double.MAX_VALUE;
  }

  public double[] getEstimates(int[] data, double[][] estimate) {
    double[] toreturn = new double[data.length];
    for (int i = 0; i < toreturn.length; i++) {
      double value = 0;
      for (int j = 0; j < estimate.length; j++) {
        // e^-bt
        double term = Math.pow(Math.E, -estimate[j][1] * i);
        // ae^-bt
        term *= estimate[j][0];
        // ae^-bt + c
        term += estimate[j][2];
        value += term;
      }
      toreturn[i] = value;
    }
    return toreturn;
  }


  /**
   * Sets the data to be used to generate curve estimates.
   * Single dimension of data... time values are index, since
   * we can assume that the datapoints are evenly spaced.
   */
  public abstract void setData(int[] data);

  /**
   * Gets the data to be used to generate curve estimates.
   * Single dimension of data... time values are index, since
   * we can assume that the datapoints are evenly spaced.
   */
  public abstract int[] getData();

  /** Sets how many exponentials are expected to be fitted. */
  public abstract void setComponentCount(int numExp);

  /** Returns the number of exponentials to be fitted. */
  public abstract int getComponentCount();

  /** Initializes the curve fitter with a starting curve estimate. */
  public abstract void estimate();

  /**
   * Returns the current curve estimate.
   * Return size is expected to be [numExponentials][3]
   * For each exponential of the form ae^-bt+c,
   * [][0] is a, [1] is b, [2] is c.
   * TODO: Make multiple exponentials a class, to remove multi-c stupidity
   */
  public abstract double[][] getCurve();

  /**
   * Sets the current curve estimate, useful if information about the
   * curve is already known.
   * See getCurve for information about the array to pass.
   */
  public abstract void setCurve(double[][] curve);

  public abstract void setFirst(int firstindex);

  public abstract void setLast(int lastindex);
}
