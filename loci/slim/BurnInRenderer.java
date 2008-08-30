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
  private double[][] rcsecache;
  private double worstRCSE;

  public BurnInRenderer(CurveCollection cc) {
    super(cc);
    subsampleLevel = curveData.getSubsamplingDepth();
    currentX = 0;
    currentY = 0;
    totalIterations = 0;
    currentIterations = 0;
    currProgress = 0;
    maxDim = 1 << subsampleLevel; // 2^subsampleLevel
    //maxProgress = 2 * maxDim * maxDim - 1;
    maxProgress = 0;
    int thislevel = 1;
    for(int i = subsampleLevel; i >= 0; i--) {
      maxProgress += thislevel;
      thislevel *= 4;
    }
    alive = false;
    maxIterations = 1000;
    maxRCSE = 0.0d;
    currentDim = 1;
    numExponentials = 1;
    estimated = false;
    improving = false;
    setComponentCount(1);
    rcsecache = new double[maxDim][maxDim];
  }

  /** Set the number of exponentials to fit */
  public void setComponentCount(int numExp) {
    numExponentials = numExp;
    image = new double[numExponentials][maxDim*maxDim];
    curveData.setComponentCount(numExponentials);
  }

  /** Repeatedly update the image for this renderer, updating based on
   *  improvements from the curve fitters
   */
  public void run() {
    alive = true;
    // initial pass - estimates
    // First, go through on all the subsample levels and do an estimate
    // for each curve in each subsample level.
    // If stop is called, alive will be set to false, and we'll drop out.
    
    while(subsampleLevel >= 0 && alive && !estimated) {
      currentCurves = curveData.getCurves(subsampleLevel);
      while(currentY < currentDim && alive) {
        while(currentX < currentDim && alive) {
          // HACKY CRAP: GET RID OF THIS IN FINAL VERSION OR SUFFER
          int[] cdata = currentCurves[currentY][currentX].getData();
          int maxValue = 0;
          int maxIndex = 0;
          for(int i = 0; i < cdata.length; i++) {
            if(cdata[i] > maxValue) {
              maxValue = cdata[i];
              maxIndex = i;
            }
          }
          currentCurves[currentY][currentX].setFirst(maxIndex);
          // END HACKY CRAP
          currentCurves[currentY][currentX].estimate();
          //currentCurves[currentY][currentX].iterate();
          // This only really matters for the last subsampleLevel
          // We want to initialize a cache of the RCSEs, so we can quickly
          // find the worst one without a lot of function calls.
          if(subsampleLevel == 0) {
            double newRCSE = currentCurves[currentY][currentX].getReducedChiSquaredError();
            rcsecache[currentY][currentX] = newRCSE;
          }
          // Order the exponentials.
          double[][] curve = currentCurves[currentY][currentX].getCurve();
          double[] exponentials = new double[numExponentials];
          for(int i = 0; i < numExponentials; i++) {
            exponentials[i] = curve[i][1];
          }
          Arrays.sort(exponentials);
          // Update the image 
          int pixelsize = maxDim / currentDim;
          for(int x = 0; x < pixelsize; x++) {
            for(int y = 0; y < pixelsize; y++) {
              for(int c = 0; c < numExponentials; c++) {
                int indexa = numExponentials - c - 1;
                int indexb = ((currentY * pixelsize + y) * maxDim) + (currentX * pixelsize + x);
                image[indexa][indexb] = exponentials[c];
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
    // initial pass - iterations
    // Pass through the image a given number of times, calling iterate()
    maxIterations = 1; // TEMP?
    while(alive && !improving) {
      if(!estimated) {
        //System.out.println("Set estimated");
        estimated = true;
        currentX = 0;
        currentY = 0;
        maxProgress = maxDim * maxDim;
      }
      for(; currentX < maxDim; currentX++) {
        for(; currentY < maxDim; currentY++) {
          currentIterations = 0;
          currProgress = (currentX * maxDim) + currentY;
          while(currentIterations < maxIterations) {
            currentIterations++;
            double currRCSE = currentCurves[currentY][currentX].getReducedChiSquaredError();
            currentCurves[currentY][currentX].iterate();
            double newRCSE = currentCurves[currentY][currentX].getReducedChiSquaredError();
            rcsecache[currentY][currentX] = newRCSE;
            if(newRCSE < currRCSE) {
              if(newRCSE < maxRCSE) {
                currentIterations = maxIterations;
              }
              
              double[][] curve = currentCurves[currentY][currentX].getCurve();
              double[] exponentials = new double[numExponentials];
              for(int i = 0; i < numExponentials; i++) {
                exponentials[i] = curve[i][1];
              }
              Arrays.sort(exponentials);
              for(int c = 0; c < numExponentials; c++) {
                image[numExponentials-c-1][currentY * maxDim + currentX] = exponentials[c];
              }
            }
            if(!alive) return;
          }
        }
        currentY = 0;
      }
      improving = true;
    }
    // continuing improvement
    // Find the worst RCSE, and iterate. Repeat.
    while(alive) {
      improving = true;
      currProgress = maxProgress;
      // Find worst:
      int worstx = -1;
      int worsty = -1;
      double worstval = 0;
      for(int x = 0; x < maxDim; x++) {
        for(int y = 0; y < maxDim; y++) {
          if(rcsecache[y][x] > worstval) {
            worstval = rcsecache[y][x];
            worstx = x;
            worsty = y;
          }
        }
      }
      worstRCSE = worstval;
      currentX = worstx;
      currentY = worsty;
      currentCurves[currentY][currentX].iterate();
      totalIterations++;
      rcsecache[currentY][currentX] = currentCurves[currentY][currentX].getReducedChiSquaredError();
      if(rcsecache[currentY][currentX] < worstRCSE) {
        double[][] curve = currentCurves[currentY][currentX].getCurve();
        double[] exponentials = new double[numExponentials];
        for(int i = 0; i < numExponentials; i++) {
          exponentials[i] = curve[i][1];
        }
        Arrays.sort(exponentials);
        for(int c = 0; c < numExponentials; c++) {
          image[numExponentials-c-1][currentY * maxDim + currentX] = exponentials[c];
        }
      }
    }
  }

  /** Return the current image */
  public double[][] getImage() {
    return image;
  }

  /** Return the current X being worked on */
  public int getImageX() {
    return (currentX * (maxDim/currentDim)) + ((maxDim/currentDim) / 2);
  }

  /** Return the current Y being worked on */
  public int getImageY() {
    return (currentY * (maxDim/currentDim)) + ((maxDim/currentDim) / 2);
  }

  /** Get the current worst RCSE */
  public double getWorstRCSE() {
    return worstRCSE;
  }

}
