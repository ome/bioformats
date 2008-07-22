//
// LMCurveFitter.java
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

package loci.slim;

import jaolho.data.lma.LMA;
import jaolho.data.lma.LMAFunction;
import jaolho.data.lma.implementations.JAMAMatrix;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Levenberg-Marquardt curve fitter that uses L-M Fit package.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/slim/LMCurveFitter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/slim/LMCurveFitter.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class LMCurveFitter implements CurveFitter {

  // -- Fields --

  protected ExpFunction func;
  protected int numExp = 1;
  protected double[][] curve;
  protected double chi2;

  // histogram data
  protected int[] data;
  protected int maxVal;
  protected float[] xVals, yVals, weights;

  // -- Constructor --

  public LMCurveFitter() { setDegrees(1); }

  // -- LMCurveFitter API methods --

  public static PrintStream filter(PrintStream out) {
    return new ConsoleStream(out);
  }

  // -- CurveFitter API methods --

  /** HACK - does nothing. */
  public void iterate() { }

  /** Gets raw chi squared error. */
  public double getChiSquaredError() { return chi2; }

  /** Gets chi squared error scaled by degrees of freedom. */
  public double getReducedChiSquaredError() {
    return chi2 / (data.length - 2 * numExp - 1);
  }

  public void setData(int[] data) {
    this.data = data;
    int num = data.length;
    xVals = new float[num];
    yVals = new float[num];
    weights = new float[num];
    maxVal = 0;
    for (int i=0; i<num; i++) {
      if (data[i] > maxVal) maxVal = data[i];
      xVals[i] = i;
      yVals[i] = data[i];
      weights[i] = 1; // no weighting
    }
  }

  public int[] getData() { return data; }

  /** Sets the number of components in the fit. Must be 1 or 2. */
  public void setDegrees(int deg) {
    if (deg < 1 || deg > 2) {
      throw new IllegalArgumentException("Number of degrees must be 1 or 2");
    }
    numExp = deg;
    func = new ExpFunction(numExp);
  }

  /** Gets the number of components in the fit. */
  public int getDegrees() { return numExp; }

  /** HACK - performs the actual fit. */
  public void estimate() {
    final float[] guess = {5, 10};
    float[] params = new float[2 * numExp + 1];
    for (int i=0; i<numExp; i++) {
      int e = 2 * i;
      params[e] = maxVal / numExp;
      params[e + 1] = guess[i]; // ?
    }
    LMA lma = new LMA(func, params, new float[][] {xVals, yVals},
      weights, new JAMAMatrix(params.length, params.length));
    lma.fit();
    //log("\t\titerations=" + lma.iterationCount);

    // compute chi2
    chi2 = lma.chi2;
    //if (maxVal != 0) chi2 /= maxVal; // normalize chi2 by the peak value (?)

    // store parameters into curve array
    curve = new double[numExp][3];
    for (int i=0; i<numExp; i++) {
      int e = 2 * i;
      curve[i][0] = lma.parameters[e]; // a
      curve[i][1] = lma.parameters[e + 1]; // b
    }
    curve[0][2] = lma.parameters[2 * numExp]; // c
  }

  /** Gets the fit results. */
  public double[][] getCurve() { return curve; }

  /** Sets the fit results. */
  public void setCurve(double[][] curve) { this.curve = curve; }

  // -- Helper classes --

  /**
   * A summed exponential function of the form:
   * y(t) = a1*e^(-b1*t) + ... + an*e^(-bn*t) + c.
   */
  public class ExpFunction extends LMAFunction {

    /** Number of exponentials to fit. */
    private int numExp = 1;

    /** Constructs a function with the given number of summed exponentials. */
    public ExpFunction(int num) { numExp = num; }

    public double getY(double x, double[] a) {
      double sum = 0;
      for (int i=0; i<numExp; i++) {
        int e = 2 * i;
        sum += a[e] * Math.exp(-a[e + 1] * x);
      }
      sum += a[2 * numExp];
      return sum;
    }

    public double getPartialDerivate(double x, double[] a, int parameterIndex) {
      if (parameterIndex == 2 * numExp) return 1; // c
      int e = parameterIndex / 2;
      int off = parameterIndex % 2;
      switch (off) {
        case 0:
          return Math.exp(-a[e + 1] * x); // a
        case 1:
          return -a[e] * x * Math.exp(-a[e + 1] * x); // b
      }
      throw new RuntimeException("No such parameter index: " +
        parameterIndex);
    }
  }

  /**
   * HACK - OutputStream extension for filtering out hardcoded
   * RuntimeException.printStackTrace() exceptions within LMA library.
   */
  public static class ConsoleStream extends PrintStream {
    private boolean ignore;

    public ConsoleStream(OutputStream out) {
      super(out);
    }

    public void println(String s) {
      if (s.equals("java.lang.RuntimeException: Matrix is singular.")) {
        ignore = true;
      }
      else if (ignore && !s.startsWith("\tat ")) ignore = false;
      if (!ignore) super.println(s);
    }

    public void println(Object o) {
      String s = o.toString();
      println(s);
    }
  }


}
