//
// GACurveFitter.java
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

import java.util.Random;

/**
 * Genetic algorithm for exponential curve fitting.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/slim/GACurveFitter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/slim/GACurveFitter.java">SVN</a></dd></dl>
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 */
public class GACurveFitter implements CurveFitter {

  // -- Fields --

  protected int[] curveData;
  protected double[][] curveEstimate;
  protected int degrees;
  protected double[][][] geneticData;
  protected double[] fitness;
  protected double currentRCSE;
  protected int stallGenerations;

  // -- Constructor --

  public GACurveFitter() {
    curveData = null;
    degrees = 1;
    curveEstimate = new double[degrees][3];
    geneticData = null;
    fitness = null;
    currentRCSE = Double.MAX_VALUE;
    stallGenerations = 0;
  }

  // -- CurveFitter API methods --

  // TODO: The set methods do not create internal copies of the passed
  //       arrays. Should they?

  // TODO: Change hardcoded values to consts

  /**
   * iterate() runs through one iteration of whatever curve fitting
   * technique this curve fitter uses. This will generally update the
   * information returned by getCurve and getChiSquaredError
   **/
  public void iterate() {
    if (currentRCSE == Double.MAX_VALUE) estimate();

    // TODO: Move these out, reuse them. Synchronized?
    double[][][] newGeneration = new double[25][degrees][3];
    Random r = new Random();

    // First make the new generation.
    // If we don't have generation or fitness data, generate it from whatever
    // the current estimate is.
    // Additionally, if we haven't improved for a number of generations,
    // shake things up.
    if (geneticData == null || fitness == null || stallGenerations > 10) {
      stallGenerations = 0;
      for (int i = 1; i < newGeneration.length; i++) {
        for (int j = 0; j < degrees; j++) {
          for (int k = 0; k < 3; k++) {
            double factor = r.nextDouble() * 2.0d;
            newGeneration[i][j][k] = curveEstimate[j][k] * factor;
          }
        }
      }
      for (int j = 0; j < degrees; j++) {
        for (int k = 0; k < 3; k++) {
          newGeneration[0][j][k] = curveEstimate[j][k];
        }
      }
    }
    else {
      // The fitness array is determined in the previous generation. It is
      // actually a marker for a range for a single random number, scaled to
      // 0.0-1.0. For example, if the raw fitness was {4, 3, 2, 1}, the
      // fitness array would contain {.4, .7, .9, 1.0}
      for (int q = 0; q < newGeneration.length; q++) {
        int mother = 0;
        int father = 0;
        double fchance = r.nextDouble();
        double mchance = r.nextDouble();
        for (int i = 0; i < fitness.length; i++) {
          if (fitness[i] > fchance) {
            father = i;
            break;
          }
        }
        for (int i = 0; i < fitness.length; i++) {
          if (fitness[i] > mchance) {
            mother = i;
            break;
          }
        }
        double minfluence = r.nextDouble();
        for (int j = 0; j < degrees; j++) {
          for (int k = 0; k < 3; k++) {
            newGeneration[q][j][k] =
              geneticData[mother][j][k] * minfluence +
              geneticData[father][j][k] * (1.0 - minfluence);
          }
        }
      }
      for (int i = 0; i < newGeneration.length; i++) {
        for (int j = 0; j < degrees; j++) {
          for (int k = 0; k < 3; k++) {
            // mutate, if necessary
            if (r.nextDouble() < .25) {
              newGeneration[i][j][k] *= (.9 + r.nextDouble() * .2);
            }
          }
        }
      }
    }
    geneticData = newGeneration;
    double total = 0.0d;
    double best = Double.MAX_VALUE;
    int bestindex = -1;
    stallGenerations++;
    for (int i = 0; i < geneticData.length; i++) {
      fitness = new double[geneticData.length];
      fitness[i] = getReducedChiSquaredError(geneticData[i]);
      if (fitness[i] < best) {
        best = fitness[i];
        bestindex = i;
      }
      fitness[i] = 1.0 / fitness[i];
      total += fitness[i];
    }
    for (int i = 0; i < geneticData.length; i++) {
      fitness[i] /= total;
    }
    if (best < currentRCSE) {
      stallGenerations = 0;
      currentRCSE = best;
      for (int j = 0; j < degrees; j++) {
        for (int k = 0; k < 3; k++) {
          curveEstimate[j][k] = geneticData[bestindex][j][k];
        }
      }
    }
  }

  // Returns the Chi Squared Error of the current curve estimate
  public double getChiSquaredError() {
    double total = 0.0d;
    double[] expected = getEstimates(curveData, curveEstimate);
    for (int i = 0; i < curveData.length; i++) {
      if (expected[i] > 0) {
        double observed = (double) curveData[i];
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

  public double getReducedChiSquaredError() {
    int df = 1 + (degrees * 2);
    int datapoints = curveData.length;
    if (datapoints - df > 0) {
      return getChiSquaredError() / (datapoints - df);
    }
    return Double.MAX_VALUE;
  }

  /**
   * Sets the data to be used to generate curve estimates.
   * The array is expected to be of size datapoints
   **/
  public void setData(int[] data) throws IllegalArgumentException {
    curveData = data;
  }

  /**
   * Returns a reference to the data array
   * Does not create a copy.
   **/ 
  public int[] getData() {
    return curveData;
  }

  /**
   * Sets how many exponentials are expected to be fitted.
   * Currently, more than 2 is not supported.
   **/
  public void setDegrees(int deg) {
    degrees = deg;
    curveEstimate = new double[deg][3];
  }

  // Returns the number of exponentials to be fitted.
  public int getDegrees() {
    return degrees;
  }

  // Initializes the curve fitter with a starting curve estimate.
  // 7/21 EK - Changed to use 1d data rows.
  public void estimate() {
    if (degrees >= 1) {
      // TODO: Estimate c, factor it in below.
      /*
      double guessC = Double.MAX_VALUE;
      for (int i = 0; i < curveData.length; i++) {
        if (curveData[i][1] < guessC) guessC = curveData[i][1];
      }
      */

      double guessC = 0.0d;

      // First, get a guess for the exponent.
      // The exponent should be "constant", but we'd also like to weight
      // the area at the beginning of the curve more heavily.
      double num = 0.0;
      double den = 0.0;
      for (int i = 1; i < curveData.length; i++) {
        if (curveData[i] > guessC && curveData[i-1] > guessC) {
          //double time = curveData[i][0] - curveData[i-1][0];
          double time = 1.0d;
          double factor =
            (curveData[i] - guessC) / (curveData[i-1] - guessC);
          double guess = 1.0 * -Math.log(factor);
          num += (guess * (curveData[i] - guessC));
          den += curveData[i] - guessC;
        }
      }
      double exp = num/den;
      num = 0.0;
      den = 0.0;
      for (int i = 0; i < curveData.length; i++) {
        if (curveData[i] > guessC) {
          // calculate e^-bt based on our exponent estimate
          double value = Math.pow(Math.E, -i * exp);
          // estimate a
          double guessA = curveData[i] / value;
          num += guessA * (curveData[i] - guessC);
          den += curveData[i] - guessC;
        }
      }
      double mult = num/den;
      curveEstimate[0][0] = mult;
      curveEstimate[0][1] = exp;
      curveEstimate[0][2] = guessC;
    }
    if (degrees == 2) {
      double guessC = 0.0d;
      curveEstimate[0][2] = guessC;
      curveEstimate[1][2] = guessC;

      // First, get a guess for the exponents.
      // To stabilize for error, do guesses over spans of 3 timepoints.
      double high = 0.0d;
      double low = Double.MAX_VALUE;
      for (int i = 3; i < curveData.length; i++) {
        if (curveData[i] > guessC && curveData[i-3] > guessC + 10) {
          //double time = curveData[i][0] - curveData[i-3][0];
          double time = 3.0d;
          double factor =
            (curveData[i] - guessC) / (curveData[i-3] - guessC);
          double guess = (1.0 / 3.0) * -Math.log(factor);
          if (guess > high) high = guess;
          if (guess < low) low = guess;
        }
      }
      /*
      if (10.0 > low) low = 10.0;
      if (20.0 > high) high = 20.0;
      */
      curveEstimate[0][1] = high;
      curveEstimate[1][1] = low;

      double highA = 0.0d;
      double lowA = Double.MAX_VALUE;
      for (int i = 0; i < curveData.length; i++) {
        if (curveData[i] > guessC + 10) {
          // calculate e^-bt based on our exponent estimate
          double value = Math.pow(Math.E, -i * low);
          // estimate a
          double guessA = curveData[i] / value;
          if (guessA > highA) highA = guessA;
          if (guessA < lowA) lowA = guessA;
        }
      }
      /*
      if (10.0 > lowA) lowA = 10.0;
      if (20.0 > highA) highA = 20.0;
      */
      curveEstimate[0][0] = highA - lowA;
      curveEstimate[1][0] = lowA;
      // It seems like the low estimates are pretty good, usually.
      // It may be possible to get a better high estimate by subtracting out
      // the low estimate, and then recalculating as if it were single
      // exponential.
      double[][] lowEst = new double[1][3];
      lowEst[0][0] = curveEstimate[1][0];
      lowEst[0][1] = curveEstimate[1][1];
      lowEst[0][2] = curveEstimate[1][2];
      double[] lowData = getEstimates(curveData, lowEst);
      double[][] lowValues = new double[curveData.length][2];
      for (int i = 0; i < lowValues.length; i++) {
        lowValues[i][0] = i;
        lowValues[i][1] = curveData[i] - lowData[i];
      }

      // now, treat lowValues as a single exponent.
      double num = 0.0;
      double den = 0.0;
      for (int i = 1; i < lowValues.length; i++) {
        if (lowValues[i][1] > guessC && lowValues[i-1][1] > guessC) {
          double time = lowValues[i][0] - lowValues[i-1][0];
          double factor =
            (lowValues[i][1] - guessC) / (lowValues[i-1][1] - guessC);
          double guess = (1.0 / time) * -Math.log(factor);
          num += (guess * (lowValues[i][1] - guessC));
          den += lowValues[i][1] - guessC;
        }
      }
      double exp = num/den;
      num = 0.0;
      den = 0.0;
      for (int i = 0; i < lowValues.length; i++) {
        if (lowValues[i][1] > guessC) {
          // calculate e^-bt based on our exponent estimate
          double value = Math.pow(Math.E, -lowValues[i][0] * exp);
          // estimate a
          double guessA = lowValues[i][1] / value;
          num += guessA * (lowValues[i][1] - guessC);
          den += lowValues[i][1] - guessC;
        }
      }
      double mult = num/den;
      curveEstimate[0][0] = mult;
      curveEstimate[0][1] = exp;
      curveEstimate[0][2] = guessC;
      // TODO: It may be possible to tweak the estimate further by adjusting
      // the values of a to more accurately account for actual values, instead
      // of this estimation. One method would be using binary search to
      // "weave" the curve in the first 10 data points, such that 5 are above
      // and 5 are below, or something similar.
      // For now, however, this produces reasonably good estimates, good
      // enough to start the GA process.

    }

    // To update currentRCSE.
    currentRCSE = getReducedChiSquaredError();
  }

  /**
   * Returns the current curve estimate.
   * Return size is expected to be [numExponentials][3]
   * For each exponential of the form ae^-bt+c,
   * [][0] is a, [1] is b, [2] is c.
   **/
  public double[][] getCurve() {
    return curveEstimate;
  }

  /**
   * Sets the current curve estimate, useful if information about the
   * curve is already known.
   * See getCurve for information about the array to pass.
   **/
  public void setCurve(double[][] curve) throws IllegalArgumentException {
    if (curve.length != degrees) {
      throw new IllegalArgumentException("Incorrect number of degrees.");
    }
    if (curve[0].length != 3) {
      throw new IllegalArgumentException(
          "Incorrect number of elements per degree.");
    }
    curveEstimate = curve;
    currentRCSE = getReducedChiSquaredError();
  }

  // -- Helper methods --

  private double[] getEstimates(int[] data, double[][] estimate) {
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
      // TODO: Ask Curtis if this is correct.
      // TODO: Answer: Make a boolean flag in the API for it.
      // The idea is that, since we can't actually get double values, we can
      // not expect double precision from our answers?
      // If we don't do this, we often get very strange chi square error numbers
      // toreturn[i] = (int) (value + 0.5);
      toreturn[i] = value;
    }
    return toreturn;
  }

  private double getChiSquaredError(double[][] estCurve) {
    double total = 0.0d;
    double[] expected = getEstimates(curveData, estCurve);
    for (int i = 0; i < curveData.length; i++) {
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

  private double getReducedChiSquaredError(double[][] estCurve) {
    int df = 1 + (degrees * 2);
    int datapoints = curveData.length;
    if (datapoints - df > 0) {
      return getChiSquaredError(estCurve) / (datapoints - df);
    }
    return Double.MAX_VALUE;
  }

}
