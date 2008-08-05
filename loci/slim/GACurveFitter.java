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
public class GACurveFitter extends CurveFitter {

  // -- Fields --

  protected int components;
  protected double[][][] geneticData;
  protected double[] fitness;
  protected double currentRCSE;
  protected int stallGenerations;
  private double mutationFactor;

  private static final boolean DEBUG = false;
  private static final int STALL_GENERATIONS = 5;
  private static final double STALLED_FACTOR = 2.0d;
  private static final double MUTATION_CHANCE = .25d;
  private static final int SPECIMENS = 25;
  // Must be 0 < x < 1
  private static final double INITIAL_MUTATION_FACTOR = .5;
  // Must be 0 < x < 1
  private static final double MUTATION_FACTOR_REDUCTION = .9;

  // -- Constructor --

  public GACurveFitter() {
    initialize();
  }

  public void initialize() {
    curveData = null;
    components = 1;
    curveEstimate = new double[components][3];
    geneticData = null;
    fitness = null;
    currentRCSE = Double.MAX_VALUE;
    stallGenerations = 0;
    mutationFactor = INITIAL_MUTATION_FACTOR;
  }


  // -- CurveFitter API methods --

  // TODO: The set methods do not create internal copies of the passed
  //       arrays. Should they?

  /**
   * iterate() runs through one iteration of whatever curve fitting
   * technique this curve fitter uses. This will generally update the
   * information returned by getCurve and getChiSquaredError
   **/
  public void iterate() {
    if (currentRCSE == Double.MAX_VALUE) estimate();

    // TODO: Move these out, reuse them. Synchronized?
    double[][][] newGeneration = new double[SPECIMENS][components][3];
    Random r = new Random();

    // First make the new generation.
    // If we don't have generation or fitness data, generate it from whatever
    // the current estimate is.
    // Additionally, if we haven't improved for a number of generations,
    // shake things up.
    if (geneticData == null || fitness == null || stallGenerations > STALL_GENERATIONS) {
      stallGenerations = 0;
      mutationFactor *= MUTATION_FACTOR_REDUCTION;
      for (int i = 1; i < newGeneration.length; i++) {
        for (int j = 0; j < components; j++) {
          for (int k = 0; k < 3; k++) {
            double factor = r.nextDouble() * STALLED_FACTOR;
            newGeneration[i][j][k] = curveEstimate[j][k] * factor;
          }
        }
      }
      for (int j = 0; j < components; j++) {
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
        for (int j = 0; j < components; j++) {
          for (int k = 0; k < 3; k++) {
            newGeneration[q][j][k] =
              geneticData[mother][j][k] * minfluence +
              geneticData[father][j][k] * (1.0 - minfluence);
          }
        }
      }
      for (int i = 0; i < newGeneration.length; i++) {
        for (int j = 0; j < components; j++) {
          for (int k = 0; k < 3; k++) {
            // mutate, if necessary
            if (r.nextDouble() < MUTATION_CHANCE) {
              newGeneration[i][j][k] *=
                ((1.0 - mutationFactor) + r.nextDouble() * (2.0 * mutationFactor));
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
      for (int j = 0; j < components; j++) {
        for (int k = 0; k < 3; k++) {
          curveEstimate[j][k] = geneticData[bestindex][j][k];
        }
      }
    }
    for(int q = 0; q < curveEstimate.length; q++) {
      if(DEBUG) System.out.println("c" + q + ": " + curveEstimate[q][2]);
    }
  }

  /**
   * Sets the data to be used to generate curve estimates.
   * The array is expected to be of size datapoints
   **/
  public void setData(int[] data) {
    curveData = data;
    firstindex = 0;
    lastindex = data.length - 1;
  }

  /**
   * Returns a reference to the data array.
   * Does not create a copy.
   */
  public int[] getData() {
    return curveData;
  }

  /**
   * Sets how many exponentials are expected to be fitted.
   * Currently, more than 2 is not supported.
   **/
  public void setComponentCount(int numExp) {
    components = numExp;
    curveEstimate = new double[numExp][3];
  }

  // Returns the number of exponentials to be fitted.
  public int getComponentCount() {
    return components;
  }

  // Initializes the curve fitter with a starting curve estimate.
  public void estimate() {
    /*
    System.out.println("****** DATA ******");
    for(int i = 0; i < curveData.length; i++) {
      System.out.println("i: " + i + "  data: " + curveData[i]);
    }
    try { Thread.sleep(1000); } catch(Exception e) {}
    */
    if (components >= 1) {
      // TODO: Estimate c, factor it in below.

      double guessC = Double.MAX_VALUE;
      for (int i = firstindex; i < curveData.length && i < lastindex; i++) {
        if (curveData[i] < guessC) guessC = curveData[i];
      }

      //double guessC = 0.0d;

      // First, get a guess for the exponent.
      // The exponent should be "constant", but we'd also like to weight
      // the area at the beginning of the curve more heavily.
      double num = 0.0;
      double den = 0.0;
      //for (int i = 1; i < curveData.length; i++) {
      for (int i = firstindex + 1; i < curveData.length && i < lastindex; i++) {
        if (curveData[i] > guessC && curveData[i-1] > guessC) {
          //double time = curveData[i][0] - curveData[i-1][0];
          double time = 1.0d;
          double factor =
            (curveData[i] - guessC) / (curveData[i-1] - guessC);
          double guess = 1.0 * -Math.log(factor);
          if(DEBUG) System.out.println("Guess: " + guess + "   Factor: " + factor);
          num += (guess * (curveData[i] - guessC));
          den += curveData[i] - guessC;
        }
      }
      double exp = num/den;
      if(DEBUG) System.out.println("Final exp guess: " + exp);
      num = 0.0;
      den = 0.0;
      // Hacky... we would like to do this over the entire curve length,
      // but the actual data is far too noisy to do this. Instead, we'll just
      // do it for the first 5, which have the most data.
      //for (int i = 0; i < curveData.length; i++)
      for(int i = firstindex; i < 5 + firstindex && i < curveData.length; i++) {
        if (curveData[i] > guessC) {
          // calculate e^-bt based on our exponent estimate
          double value = Math.pow(Math.E, -i * exp);
          // estimate a
          double guessA = (curveData[i] - guessC) / value;
          num += guessA * (curveData[i] - guessC);
          den += curveData[i] - guessC;
          if(DEBUG) System.out.println("Data: " + curveData[i] + " Value: " + value + " guessA: " + guessA);
        }
      }
      double mult = num/den;
      curveEstimate[0][0] = mult;
      curveEstimate[0][1] = exp;
      curveEstimate[0][2] = guessC;
    }
    
    if (components == 2) {
      double guessC = Double.MAX_VALUE;
      //for(int i = 0; i < curveData.length; i++) {
      for (int i = firstindex; i < curveData.length && i < lastindex; i++) {
        if(curveData[i] < guessC) guessC = curveData[i];
      }
      curveEstimate[0][2] = guessC;
      curveEstimate[1][2] = 0;

      // First, get a guess for the exponents.
      // To stabilize for error, do guesses over spans of 3 timepoints.
      double high = 0.0d;
      double low = Double.MAX_VALUE;
      for (int i = firstindex + 3; i < lastindex && i < curveData.length; i++) {
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
      curveEstimate[0][1] = high;
      curveEstimate[1][1] = low;

      double highA = 0.0d;
      double lowA = Double.MAX_VALUE;
      for(int i = firstindex; i < 5 + firstindex && i < curveData.length; i++) {
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
      int[] cdata = new int[lastindex - firstindex + 1];
      System.arraycopy(curveData, firstindex, cdata, 0, cdata.length);
      double[] lowData = getEstimates(cdata, lowEst);
      double[][] lowValues = new double[cdata.length][2];
      for (int i = 0; i < lowValues.length; i++) {
        lowValues[i][0] = i;
        lowValues[i][1] = cdata[i] - lowData[i];
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
      //for (int i = 0; i < lowValues.length; i++)
      for(int i = 0; i < 5; i++) {
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
    }

    // Sometimes, if the curve looks strange, we'll get a negative estimate
    // This will really have to be fixed in iteration, but until then we want
    // to get a "reasonable" positive estimate.
    // We'll take the high point of the curve, and then the farthest away
    // low point of the curve, and naively fit a single exponential to those
    // two points. In the case of a multiple exponential curve, we'll split the
    // a factor unevenly among the two, and then sort it out in iteration.
    // This is all very "last ditch effort" estimation.
    boolean doNegativeEstimation = false;
    for(int i = 0; i < curveEstimate.length; i++) {
      if(curveEstimate[i][1] < 0) {
        doNegativeEstimation = true;
        //System.out.println("Negative factor " + curveEstimate[i][1] + " found.");
      }
    }
    if(doNegativeEstimation) {
      // Find highest point in the curve
      int maxIndex = -1;
      int maxData = -1;
      for(int i = firstindex; i < lastindex && i < curveData.length; i++) {
        if(curveData[i] > maxData) {
          maxIndex = i; 
          maxData = curveData[i];
        }
      }
      int minIndex = -1; 
      int minData = Integer.MAX_VALUE;
      for(int i = maxIndex; i < lastindex && i < curveData.length; i++) {
        if(curveData[i] <= minData) {
          minIndex = i;
          minData = curveData[i];
        }
      }
      System.out.println("maxIndex: " + maxIndex + "  minIndex: " + minIndex);
      // If we have valid min and max data, perform the "estimate"
      double expguess = -1;
      
      // ae^-b(t0)+c = x
      // ae^-b(t1)+c = y.
      // if t0 = 0, and c is minData, then a = x - minData. (e^0 = 1)
      // Then, (x-min)e^-b(t1) = y
      // e^-b(t1) = y / (x-min)
      // ln e^-b(t1) = ln (y / (x - min))
      // -b(t1) = ln (y / (x - min))
      // -b = (ln (y / (x - min)) / t1)
      // b = -(ln (y / (x - min)) / t1)
      // and c = minData
      
      if(maxData != -1 && minData != -1) {
        double a = maxData - minData;
        // min value for many trouble cases is 0, which is problematic.
        // As an estimate, if minData is 0, we'll scan back to see how far it
        // until data, and use that distance as an estimate.
        double y = 0;
        int newmin = minIndex;
        while(curveData[newmin] == minData) newmin--;
        y = 1.0 / (minIndex - newmin);
        expguess = -(Math.log(y / (maxData - minData)) / (minIndex - maxIndex));
      }
      if(expguess < 0) {
        // If the guess is still somehow negative (example, ascending curve), punt;
        expguess = 1;
      }
      System.out.println("Estimating: " + expguess);
      System.out.println("maxData: " + maxData + "  firstindex: " + firstindex + "  data: " + curveData[firstindex]);
      if(components == 1) {
        curveEstimate[0][0] = maxData - minData;
        curveEstimate[0][1] = expguess;
        curveEstimate[0][2] = minData;
      } else {
        // 2 components
        curveEstimate[0][0] = maxData * .8;
        curveEstimate[0][1] = expguess;
        curveEstimate[1][0] = maxData * .2;
        curveEstimate[1][1] = expguess;
        curveEstimate[0][2] = minData;
      }
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
  public void setCurve(double[][] curve) {
    if (curve.length != components) {
      throw new IllegalArgumentException("Incorrect number of components.");
    }
    if (curve[0].length != 3) {
      throw new IllegalArgumentException(
          "Incorrect number of elements per degree.");
    }
    curveEstimate = curve;
    currentRCSE = getReducedChiSquaredError();
  }

  public void setFirst(int index) {
    firstindex = index;
  }

  public void setLast(int index) {
    lastindex = index;
  }

}
