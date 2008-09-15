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

package loci.slim.fit;

import jaolho.data.lma.LMA;
import jaolho.data.lma.LMAFunction;
import jaolho.data.lma.implementations.JAMAMatrix;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Levenberg-Marquardt curve fitter that uses L-M Fit package.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/slim-plotter/src/loci/slim/LMCurveFitter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/slim-plotter/src/loci/slim/LMCurveFitter.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class LMCurveFitter extends CurveFitter {

  // -- Fields --

  protected ExpFunction func;
  protected int numExponentials = 1;

  protected int maxVal;
  protected float[] xVals, yVals, weights;

  // -- Constructor --

  public LMCurveFitter() { setComponentCount(1); }

  // -- LMCurveFitter API methods --

  public static PrintStream filter(PrintStream out) {
    return new ConsoleStream(out);
  }

  // -- CurveFitter API methods --

  /** HACK - does nothing. */
  public void iterate() { }

  public void setData(int[] data) {
    curveData = data;
    int num = curveData.length;
    xVals = new float[num];
    yVals = new float[num];
    weights = new float[num];
    maxVal = 0;
    for (int i=0; i<num; i++) {
      if (curveData[i] > maxVal) maxVal = curveData[i];
      xVals[i] = i;
      yVals[i] = curveData[i];
      weights[i] = 1; // no weighting
    }
  }

  public int[] getData() { return curveData; }

  /** Sets the number of components in the fit. Must be 1 or 2. */
  public void setComponentCount(int numExp) {
    if (numExp < 1 || numExp > 2) {
      throw new IllegalArgumentException("Number of degrees must be 1 or 2");
    }
    numExponentials = numExp;
    func = new ExpFunction(numExponentials);
  }

  /** Gets the number of components in the fit. */
  public int getComponentCount() { return numExponentials; }

  /** HACK - performs the actual fit. */
  public void estimate() {
    int num = curveData.length;
    final float[] guess = {num / 10, num / 5};
    float[] params = new float[2 * numExponentials + 1];
    for (int i=0; i<numExponentials; i++) {
      int e = 2 * i;
      params[e] = maxVal / numExponentials;
      params[e + 1] = guess[i]; // ?
    }
    LMA lma = new LMA(func, params, new float[][] {xVals, yVals},
      weights, new JAMAMatrix(params.length, params.length));
    lma.fit();
    //log("\t\titerations=" + lma.iterationCount);

    // store parameters into curve array
    curveEstimate = new double[numExponentials][3];
    for (int i=0; i<numExponentials; i++) {
      int e = 2 * i;
      curveEstimate[i][0] = lma.parameters[e]; // a
      curveEstimate[i][1] = lma.parameters[e + 1]; // b
    }
    curveEstimate[0][2] = lma.parameters[2 * numExponentials]; // c
  }

  /** Gets the fit results. */
  public double[][] getCurve() { return curveEstimate; }

  /** Sets the fit results. */
  public void setCurve(double[][] curve) { curveEstimate = curve; }

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

  // TODO: Dummy methods to implement CurveFitter
  public void setFirst(int index) {}

  public void setLast(int index) {}
  
}
