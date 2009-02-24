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

package loci.slim.fit;

import java.util.Random;

/**
 * Genetic algorithm for exponential curve fitting.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/slim-plotter/src/loci/slim/fit/GACurveFitter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/slim-plotter/src/loci/slim/fit/GACurveFitter.java">SVN</a></dd></dl>
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 */
public class GACurveFitter extends CurveFitter {

  // -- Constants --

  private static final int STALL_GENERATIONS = 3;
  private static final double STALLED_FACTOR = 2.0d;
  private static final double MUTATION_CHANCE = .25d;
  private static final int SPECIMENS = 25;
  // Must be 0 < x < 1
  private static final double INITIAL_MUTATION_FACTOR = .5;
  // Must be 0 < x < 1
  private static final double MUTATION_FACTOR_REDUCTION = .99;

  // -- Fields --

  protected double[][][] geneticData;
  protected double[] fitness;
  protected int stallGenerations;
  protected double mutationFactor;
  protected int numIter;
  protected Random r;

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
    numIter = 0;
    r = new Random();
  }


  // -- ICurveFitter methods --

  /**
   * iterate() runs through one iteration of whatever curve fitting
   * technique this curve fitter uses. This will generally update the
   * information returned by getCurve and getChiSquaredError
   **/
  public void iterate() {
    if (currentRCSE == Double.MAX_VALUE) estimate();

    // TODO: Move these out, reuse them. Synchronized?
    double[][][] newGeneration = new double[SPECIMENS][components][3];

    // First make the new generation.
    // If we don't have generation or fitness data, generate it from whatever
    // the current estimate is.
    // Additionally, if we haven't improved for a number of generations,
    // shake things up.
    if (geneticData == null || fitness == null ||
      stallGenerations > STALL_GENERATIONS)
    {
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
              newGeneration[i][j][k] *= ((1.0 - mutationFactor) +
                r.nextDouble() * (2.0 * mutationFactor));
            }
          }
        }
      }
    }

    // ensure that fixed parameters do not change
    for (int i = 0; i < newGeneration.length; i++) {
      for (int j = 0; j < components; j++) {
        for (int k = 0; k < 3; k++) {
          if (curveFixed[j][k]) newGeneration[i][j][k] = curveEstimate[j][k];
        }
      }
    }

    // compute best candidate
    geneticData = newGeneration;
    double total = 0.0d;
    double best = Double.POSITIVE_INFINITY;
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
    if (DEBUG) {
      System.out.println("RCSE: " + currentRCSE);
      for (int j = 0; j < components; j++) {
        System.out.println("a: " + curveEstimate[j][0] + " b: " +
          curveEstimate[j][1] + " c: " + curveEstimate[j][2]);
      }
    }

    numIter++;
  }

  public int getIterations() { return numIter; }

}
