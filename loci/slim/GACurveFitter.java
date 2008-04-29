package loci.slim;

import java.util.Random;


public class GACurveFitter implements CurveFitter {

  double[][] curveData;
  double[][] curveEstimate;
  int degrees;
  double[][][] geneticData;
  double[] fitness;
  double currentRCSE;
  int stallGenerations;

  public GACurveFitter() {
    curveData = null;
    degrees = 1;
    curveEstimate = new double[degrees][3];
    geneticData = null;
    fitness = null;
    currentRCSE = Double.MAX_VALUE;
    stallGenerations = 0;
  }
  
  // TODO: The set methods do not create internal copies of the passed
  //       arrays. Should they?
  
  // TODO: Change hardcoded values to consts
  
  /**
   * iterate() runs through one iteration of whatever curve fitting
   * technique this curve fitter uses. This will generally update the
   * information returned by getCurve and getChiSquaredError
   **/
  public void iterate() {
    if(currentRCSE == Double.MAX_VALUE) estimate();
 
    // TODO: Move these out, reuse them. Synchronized?
    double[][][] newGeneration = new double[25][degrees][3];
    Random r = new Random();
    
    // First make the new generation.
    // If we don't have generation or fitness data, generate it from whatever
    // the current estimate is.
    // Additionally, if we haven't improved for a number of generations,
    // shake things up.
    if(geneticData == null || fitness == null || stallGenerations > 10) {
      stallGenerations = 0;
      for(int i = 1; i < newGeneration.length; i++) {
        for(int j = 0; j < degrees; j++) {
          for(int k = 0; k < 3; k++) {
            double factor = r.nextDouble() * 2.0d;
            newGeneration[i][j][k] = curveEstimate[j][k] * factor;
          }
        }
      }
      for(int j = 0; j < degrees; j++) {
        for(int k = 0; k < 3; k++) {
          newGeneration[0][j][k] = curveEstimate[j][k];
        }
      }
    } else {
      // The fitness array is determined in the previous generation. It is
      // actually a marker for a range for a single random number, scaled to
      // 0.0-1.0. For example, if the raw fitness was {4, 3, 2, 1}, the 
      // fitness array would contain {.4, .7, .9, 1.0}
      for(int q = 0; q < newGeneration.length; q++) {
        int mother = 0;
        int father = 0;
        double fchance = r.nextDouble();
        double mchance = r.nextDouble();
        for(int i = 0; i < fitness.length; i++) {
          if(fitness[i] > fchance) {
            father = i;
            break;
          }
        }
        for(int i = 0; i < fitness.length; i++) {
          if(fitness[i] > mchance) {
            mother = i;
            break;
          }
        }
        double minfluence = r.nextDouble();
        for(int j = 0; j < degrees; j++) {
          for(int k = 0; k < 3; k++) {
            newGeneration[q][j][k] = geneticData[mother][j][k] * minfluence +
                                     geneticData[father][j][k] * (1.0 - minfluence);
          }
        }
      }
      for(int i = 0; i < newGeneration.length; i++) {
        for(int j = 0; j < degrees; j++) {
          for(int k = 0; k < 3; k++) {
            // mutate, if necessary
            if(r.nextDouble() < .25) {
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
    for(int i = 0; i < geneticData.length; i++) {
      fitness = new double[geneticData.length];
      fitness[i] = getReducedChiSquaredError(geneticData[i]);
      if(fitness[i] < best) {
        best = fitness[i];
        bestindex = i;
      }
      fitness[i] = 1.0 / fitness[i];
      total += fitness[i];
    }
    for(int i = 0; i < geneticData.length; i++) {
      fitness[i] /= total;
    }
    if(best < currentRCSE) {
      stallGenerations = 0;
      currentRCSE = best;
      for(int j = 0; j < degrees; j++) {
        for(int k = 0; k < 3; k++) {
          curveEstimate[j][k] = geneticData[bestindex][j][k];
        }
      }
    }
  }

  private double[] getEstimates(double[][] data, double[][] estimate) {
    double[] toreturn = new double[data.length];
    for(int i = 0; i < toreturn.length; i++) {
      double value = 0;
      for(int j = 0; j < estimate.length; j++) {
        // e^-bt
        double term = Math.pow(Math.E, -estimate[j][1] * data[i][0]);
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
  
  // Returns the Chi Squared Error of the current curve estimate
  public double getChiSquaredError() {
    double total = 0.0d;
    double[] expected = getEstimates(curveData, curveEstimate);
    for(int i = 0; i < curveData.length; i++) {
      if(expected[i] > 0) {
        double observed = curveData[i][1];
        double term = (observed - expected[i]);
        // (o-e)^2
        term *= term;
        // (o-e)^2 / e
        term /= expected[i];
        //System.out.println("Obs: " + observed + " Expect: " + expected[i] + " Term: " + term);
        total += term;
      }
    }
    return total;
  }

  private double getChiSquaredError(double[][] estCurve) {
    double total = 0.0d;
    double[] expected = getEstimates(curveData, estCurve);
    for(int i = 0; i < curveData.length; i++) {
      if(expected[i] > 0) {
        double observed = curveData[i][1];
        double term = (observed - expected[i]);
        // (o-e)^2
        term *= term;
        // (o-e)^2 / e
        term /= expected[i];
        //System.out.println("Obs: " + observed + " Expect: " + expected[i] + " Term: " + term);
        total += term;
      }
    }
    return total;
  }

  public double getReducedChiSquaredError() {
    int df = 1 + (degrees * 2);
    int datapoints = curveData.length;
    if(datapoints - df > 0) {
      return getChiSquaredError() / (datapoints - df);
    }
    return Double.MAX_VALUE;
  }

  private double getReducedChiSquaredError(double[][] estCurve) {
    int df = 1 + (degrees * 2);
    int datapoints = curveData.length;
    if(datapoints - df > 0) {
      return getChiSquaredError(estCurve) / (datapoints - df);
    }
    return Double.MAX_VALUE;
  }
  
  /**
   * Sets the data to be used to generate curve estimates.
   * The array is expected to be of size [datapoints][2].
   * [][0] contains a time value, [][1] contains the data value.
   **/
  public void setData(double[][] data) throws IllegalArgumentException {
    if(data[0].length != 2) {
      throw new IllegalArgumentException(
          "Wrong number of elements per datapoint.");
    }
    curveData = data;
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
  public void estimate() {
    if(degrees >= 1) {
      // TODO: Estimate c, factor it in below.
      /*
      double guess_c = Double.MAX_VALUE;
      for(int i = 0; i < curveData.length; i++) {
        if(curveData[i][1] < guess_c) guess_c = curveData[i][1];
      }
      */

      double guess_c = 0.0d;
      
      // First, get a guess for the exponent.
      // The exponent should be "constant", but we'd also like to weight
      // the area at the beginning of the curve more heavily.
      double num = 0.0;
      double den = 0.0;
      for(int i = 1; i < curveData.length; i++) {
        if(curveData[i][1] > guess_c && curveData[i-1][1] > guess_c) {
          double time = curveData[i][0] - curveData[i-1][0];
          double factor = (curveData[i][1] - guess_c) / (curveData[i-1][1] - guess_c);
          double guess = (1.0 / time) * -Math.log(factor);
          num += (guess * (curveData[i][1] - guess_c));
          den += curveData[i][1] - guess_c;
        }
      }
      double exp = num/den;
      num = 0.0;
      den = 0.0;
      for(int i = 0; i < curveData.length; i++) {
        if(curveData[i][1] > guess_c) {
          // calculate e^-bt based on our exponent estimate
          double value = Math.pow(Math.E, -curveData[i][0] * exp);
          // estimate a
          double guess_a = curveData[i][1] / value;
          num += guess_a * (curveData[i][1] - guess_c);
          den += curveData[i][1] - guess_c;
        }
      }
      double mult = num/den;
      curveEstimate[0][0] = mult;
      curveEstimate[0][1] = exp;
      curveEstimate[0][2] = guess_c;
    }
    if(degrees == 2) {
      double guess_c = 0.0d;
      curveEstimate[0][2] = guess_c;
      curveEstimate[1][2] = guess_c;
      
      // First, get a guess for the exponents.
      // To stabilize for error, do guesses over spans of 3 timepoints.
      double high = 0.0d;
      double low = Double.MAX_VALUE;
      for(int i = 3; i < curveData.length; i++) {
        if(curveData[i][1] > guess_c && curveData[i-3][1] > guess_c + 10) {
          double time = curveData[i][0] - curveData[i-3][0];
          double factor = (curveData[i][1] - guess_c) / (curveData[i-3][1] - guess_c);
          double guess = (1.0 / time) * -Math.log(factor);
          if(guess > high) high = guess;
          if(guess < low) low = guess;
        }
      }
      if(10.0 > low) low = 10.0;
      if(20.0 > high) high = 20.0;
      curveEstimate[0][1] = high;
      curveEstimate[1][1] = low;

      double high_a = 0.0d;
      double low_a = Double.MAX_VALUE;
      for(int i = 0; i < curveData.length; i++) {
        if(curveData[i][1] > guess_c + 10) {
          // calculate e^-bt based on our exponent estimate
          double value = Math.pow(Math.E, -curveData[i][0] * low);
          // estimate a
          double guess_a = curveData[i][1] / value;
          if(guess_a > high_a) high_a = guess_a;
          if(guess_a < low_a) low_a = guess_a;
        }
      }
      if(10.0 > low_a) low_a = 10.0;
      if(20.0 > high_a) high_a = 20.0;
      curveEstimate[0][0] = high_a - low_a;
      curveEstimate[1][0] = low_a;
      // It seems like the low estimates are pretty good, usually.
      // It may be possible to get a better high estimate by subtracting out
      // the low estimate, and then recalculating as if it were single
      // exponential.
      double[][] low_est = new double[1][3];
      low_est[0][0] = curveEstimate[1][0];
      low_est[0][1] = curveEstimate[1][1];
      low_est[0][2] = curveEstimate[1][2];
      double[] low_data = getEstimates(curveData, low_est);
      double[][] low_values = new double[curveData.length][2];
      for(int i = 0; i < low_values.length; i++) {
        low_values[i][0] = curveData[i][0];
        low_values[i][1] = curveData[i][1] - low_data[i];
      }

      // now, treat low_values as a single exponent.
      double num = 0.0;
      double den = 0.0;
      for(int i = 1; i < low_values.length; i++) {
        if(low_values[i][1] > guess_c && low_values[i-1][1] > guess_c) {
          double time = low_values[i][0] - low_values[i-1][0];
          double factor = (low_values[i][1] - guess_c) / (low_values[i-1][1] - guess_c);
          double guess = (1.0 / time) * -Math.log(factor);
          num += (guess * (low_values[i][1] - guess_c));
          den += low_values[i][1] - guess_c;
        }
      }
      double exp = num/den;
      num = 0.0;
      den = 0.0;
      for(int i = 0; i < low_values.length; i++) {
        if(low_values[i][1] > guess_c) {
          // calculate e^-bt based on our exponent estimate
          double value = Math.pow(Math.E, -low_values[i][0] * exp);
          // estimate a
          double guess_a = low_values[i][1] / value;
          num += guess_a * (low_values[i][1] - guess_c);
          den += low_values[i][1] - guess_c;
        }
      }
      double mult = num/den;
      curveEstimate[0][0] = mult;
      curveEstimate[0][1] = exp;
      curveEstimate[0][2] = guess_c;
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
    if(curve.length != degrees) {
      throw new IllegalArgumentException("Incorrect number of degrees.");
    }
    if(curve[0].length != 3) {
      throw new IllegalArgumentException(
          "Incorrect number of elements per degree.");
    }
    curveEstimate = curve;
    currentRCSE = getReducedChiSquaredError();
  }

}
