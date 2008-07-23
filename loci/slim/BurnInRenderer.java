//
// BurnInRenderer.java
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

import java.util.Arrays;
import loci.slim.CurveCollection;
import loci.slim.CurveFitter;

/**
 * TODO
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/slim/BurnInRenderer.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/slim/BurnInRenderer.java">SVN</a></dd></dl>
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 */
public class BurnInRenderer extends Renderer {

  protected CurveFitter[][] currentCurves = null;
  protected int currentDim;
  protected double[][] image;
  protected int maxDim;
  private boolean estimated;
  private boolean improving;

  public BurnInRenderer(CurveCollection cc) {
    super(cc);
    subsampleLevel = curveData.getSubsamplingDepth();
    currentX = 0;
    currentY = 0;
    totalIterations = 0;
    currentIterations = 0;
    currProgress = 0;
    maxDim = 1 << subsampleLevel; // 2^subsampleLevel
    maxProgress = 2 * maxDim * maxDim - 1;
    image = new double[1][maxDim*maxDim];
    alive = false;
    maxIterations = Integer.MAX_VALUE;
    maxRCSE = 1.0d;
    currentDim = 1;
    numExponentials = 1;
    estimated = false;
    improving = false;
  }

  public void setComponentCount(int numExp) {
    numExponentials = numExp;
    image = new double[numExponentials][maxDim*maxDim];
    curveData.setComponentCount(numExponentials);
  }

  public void run() {
    // initial pass - estimates
    while(subsampleLevel >= 0 && alive && !estimated) {
      currentCurves = curveData.getCurves(subsampleLevel);
      while(currentY < currentDim && alive) {
        while(currentX < currentDim && alive) {
          currentCurves[currentY][currentX].estimate();
          double[][] curve = currentCurves[currentY][currentX].getCurve();
          double[] exponentials = new double[numExponentials];
          for(int i = 0; i < numExponentials; i++) {
            exponentials[i] = curve[i][1];
          }
          Arrays.sort(exponentials);
          int pixelsize = maxDim / currentDim;
          for(int x = 0; x < pixelsize; x++) {
            for(int y = 0; y < pixelsize; y++) {
              for(int c = 0; c < numExponentials; c++) {
                image[numExponentials-c-1][((currentY * pixelsize + y) * maxDim) +
                                           (currentX * pixelsize + x)] = exponentials[c];
              }
            }
          }
          currentX++;
          currProgress++;
        }
        if(alive) {
          currentX = 0;
          currentY++;
        }
      }
      if(alive) {
        subsampleLevel--;
        if(subsampleLevel >= 0) {
          currentX = 0;
          currentY = 0;
          currentDim *= 2;
        }
      }
    }
    estimated = true;
    // initial pass - iterations
    while(estimated && alive && !improving) {
      for(; currentX < currentDim && alive && currentIterations < maxIterations; currentX++) {
        for(; currentY < currentDim && alive && currentIterations < maxIterations; currentY++) {
          currentIterations++;
          currProgress++;
          double currRCSE = currentCurves[currentY][currentX].getReducedChiSquaredError();
          currentCurves[currentY][currentX].iterate();
          if(currentCurves[currentY][currentX].getReducedChiSquaredError() < currRCSE) {
            if(currentCurves[currentY][currentX].getReducedChiSquaredError() < maxRCSE) {
              currentIterations = maxIterations;
            }
            double[][] curve = currentCurves[currentY][currentX].getCurve();
            double[] exponentials = new double[numExponentials];
            for(int i = 0; i < numExponentials; i++) {
              exponentials[i] = curve[i][1];
            }
            Arrays.sort(exponentials);
            int pixelsize = maxDim / currentDim;
            for(int x = 0; x < pixelsize; x++) {
              for(int y = 0; y < pixelsize; y++) {
                for(int c = 0; c < numExponentials; c++) {
                  image[numExponentials-c-1][((currentY * pixelsize + y) * maxDim) +
                    (currentX * pixelsize + x)] = exponentials[c];
                }
              }
            }
          }
        }
      }
    }
    // continuing improvement
    while(alive) {
      improving = true;
      currProgress = maxProgress;
      // Find worst:
      int worstx = -1;
      int worsty = -1;
      double worstval = 0;
      for(int x = 0; x < maxDim; x++) {
        for(int y = 0; y < maxDim; y++) {
          if(currentCurves[y][x].getReducedChiSquaredError() > worstval) {
            worstval = currentCurves[y][x].getReducedChiSquaredError();
            worstx = x;
            worsty = y;
          }
        }
      }
      currentX = worstx;
      currentY = worsty;
      currentCurves[currentY][currentX].iterate();
    }
  }

  public double[][] getImage() {
    return image;
  }

  public int getImageX() {
    return (currentX * (maxDim/currentDim)) + ((maxDim/currentDim) / 2);
  }

  public int getImageY() {
    return (currentY * (maxDim/currentDim)) + ((maxDim/currentDim) / 2);
  }

}
