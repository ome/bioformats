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
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/slim-plotter/src/loci/slim/fit/LMCurveFitter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/slim-plotter/src/loci/slim/fit/LMCurveFitter.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class LMCurveFitter extends CurveFitter {

  // -- Constants --

  protected static final ExpFunction[] EXP_FUNCTIONS = {
    new ExpFunction(1), new ExpFunction(2)
  };

  // -- Fields --

  protected LMA lma;
  protected int iterCount;

  // -- Constructor --

  public LMCurveFitter() { setComponentCount(1); }

  // -- LMCurveFitter methods --

  public static PrintStream filter(PrintStream out) {
    return new ConsoleStream(out);
  }

  // -- ICurveFitter methods --

  /** HACK - performs the actual fit. */
  public void iterate() {
    iterCount++;
    lma.fit();

    // store parameters into curve array
    for (int i=0; i<components; i++) {
      int e = 2 * i;
      curveEstimate[i][0] = lma.parameters[e]; // a
      curveEstimate[i][1] = lma.parameters[e + 1]; // b
    }
    curveEstimate[0][2] = lma.parameters[2 * components]; // c
  }

  public int getIterations() { return iterCount; }

  /* @see CurveFitter#estimate() */
  public void estimate() {
    super.estimate();

    int num = lastIndex - firstIndex + 1;
    double[] xVals = new double[num];
    double[] yVals = new double[num];
    double[] weights = new double[num];
    for (int i=0, q=firstIndex; i<num; i++, q++) {
      xVals[i] = i;
      yVals[i] = curveData[q];
      weights[i] = 1; // no weighting
    }

    double[] params = new double[2 * components + 1];
    for (int i=0; i<components; i++) {
      int e = 2 * i;
      params[e] = curveEstimate[i][0];
      params[e + 1] = curveEstimate[i][1];
    }
    params[2 * components] = curveEstimate[0][2];

    lma = new LMA(EXP_FUNCTIONS[components - 1], params,
      new double[][] {xVals, yVals}, weights,
      new JAMAMatrix(params.length, params.length));
    lma.maxIterations = 1;
  }

  // -- Helper classes --

  /**
   * A summed exponential function of the form:
   * y(t) = a1*e^(-b1*t) + ... + an*e^(-bn*t) + c.
   */
  public static class ExpFunction extends LMAFunction {

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
      double aTerm = a[2 * e];
      double bTerm = a[2 * e + 1];
      switch (off) {
        case 0:
            return Math.exp(-bTerm * x); // a
        case 1:
          return -aTerm * x * Math.exp(-bTerm * x); // b
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
