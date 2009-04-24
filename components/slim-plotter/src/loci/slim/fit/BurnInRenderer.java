//
// BurnInRenderer.java
//

/*
SLIM Plotter application and curve fitting library for
combined spectral lifetime visualization and analysis.
Copyright (C) 2006-@year@ Curtis Rueden and Eric Kjellman.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
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

import java.util.Arrays;

/**
 * Curve renderer implementation that delivers increasingly high resolution
 * lifetime image renderings, then continually improves the lifetime image
 * based on RCSE minimization thereafter.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/slim-plotter/src/loci/slim/fit/BurnInRenderer.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/slim-plotter/src/loci/slim/fit/BurnInRenderer.java">SVN</a></dd></dl>
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class BurnInRenderer extends CurveRenderer {

  // -- Constants --

  /** Number of consecutive failures before a pixel is judged to be stalled. */
  private static final int STALL_ITERATIONS = 10;

  // -- Fields --

  protected ICurveFitter[][] currentCurves = null;
  protected double[][] image;
  protected boolean[][] mask;
  protected int maskCount;
  private boolean estimated;
  private boolean improving;
  private double[][] rcseCache;
  private double worstRCSE, totalRCSE;
  private int worstX, worstY, worstIter;
  private boolean[][] stalled;
  private int stallCount;

  // -- Constructor --

  public BurnInRenderer(CurveCollection cc) {
    super(cc);
    subsampleLevel = curveData.getSubsamplingDepth();
    currentX = 0;
    currentY = 0;
    totalIterations = 0;
    currentIterations = 0;
    currProgress = 0;
    maxProgress = 0;
    int thislevel = 1;
    for (int i = subsampleLevel; i >= 0; i--) {
      maxProgress += thislevel;
      thislevel *= 4;
    }
    alive = false;
    maxIterations = 1;
    maxRCSE = 0.0d;
    numExponentials = 1;
    estimated = false;
    improving = false;
    setComponentCount(1);
    rcseCache = new double[numRows][numCols];
    stalled = new boolean[numRows][numCols];
    stallCount = 0;
    setMask(null);
  }

  // -- BurnInRenderer methods --

  public int getStallCount() { return stallCount; }

  // -- ICurveRenderer methods --

  public void run() {
    alive = true;

    // initial pass - estimates
    while (subsampleLevel >= 0 && alive && !estimated) {
      currentCurves = curveData.getCurves(subsampleLevel);
      int maxY = currentCurves.length;
      int maxX = currentCurves[0].length;
      while (currentY < maxY && alive) {
        while (currentX < maxX && alive) {
          //System.out.println("ssl: " + subsampleLevel + " x: " + currentX +
          //  " y: " + currentY);
          currentCurves[currentY][currentX].estimate();
          //currentCurves[currentY][currentX].iterate();
          // This only really matters for the last subsampleLevel
          if (subsampleLevel == 0) {
            double newRCSE =
              currentCurves[currentY][currentX].getReducedChiSquaredError();
            rcseCache[currentY][currentX] = newRCSE;
          }
          double[][] curve = currentCurves[currentY][currentX].getCurve();
          double[] exponentials = new double[numExponentials];
          for (int i = 0; i < numExponentials; i++) {
            exponentials[i] = curve[i][1];
            //System.out.println("b" + i + ": " + exponentials[i]);
          }
          Arrays.sort(exponentials);

          int sizeY = currentCurves.length;
          int sizeX = currentCurves[0].length;
          int imageX = getImageX();
          int imageY = getImageY();
          int widthY = numRows / sizeY;
          int widthX = numCols / sizeX;
          for (int c = 0; c < numExponentials; c++) {
            int expIndex = numExponentials - c - 1;
            for (int y = 0; y < widthY; y++) {
              for (int x = 0; x < widthX; x++) {
                int xyIndex = (imageY + y) * numCols + (imageX + x);
                image[expIndex][xyIndex] = exponentials[c];
              }
            }
          }
          currentX++;
          currProgress++;
        }
        if (alive) {
          currentX = 0;
          currentY++;
        }
      }
      if (alive) {
        subsampleLevel--;
        if (subsampleLevel >= 0) {
          currentX = 0;
          currentY = 0;
        }
      }
    }

    // initial pass - iterations
    while (alive && !improving) {
      if (!estimated) {
        //System.out.println("Set estimated");
        estimated = true;
        currentX = 0;
        currentY = 0;
        maxProgress = numRows * numCols;
      }
      for (; currentY < numRows; currentY++) {
        for (; currentX < numCols; currentX++) {
          currentIterations = 0;
          currProgress = (currentY * numCols) + currentX;
          while (currentIterations < maxIterations) {
            //System.out.println("x: " + currentX + " y: " + currentY +
            //  " iter: " + currentIterations);
            currentIterations++;
            double currRCSE =
              currentCurves[currentY][currentX].getReducedChiSquaredError();
            currentCurves[currentY][currentX].iterate();
            double newRCSE =
              currentCurves[currentY][currentX].getReducedChiSquaredError();
            rcseCache[currentY][currentX] = newRCSE;
            if (newRCSE < currRCSE) {
              if (newRCSE < maxRCSE) {
                currentIterations = maxIterations;
              }

              double[][] curve = currentCurves[currentY][currentX].getCurve();
              double[] exponentials = new double[numExponentials];
              for (int i = 0; i < numExponentials; i++) {
                exponentials[i] = curve[i][1];
              }
              Arrays.sort(exponentials);
              for (int c = 0; c < numExponentials; c++) {
                image[numExponentials-c-1][currentY * numCols + currentX] =
                  exponentials[c];
              }
            }
            if (!alive) return;
          }
        }
        currentX = 0;
      }
      improving = true;
    }

    // continuing improvement
    //System.out.println("Got to continuing");
    while (alive) {
      improving = true;
      currProgress = maxProgress;
      if (worstIter == 0) {
        // find new worst error
        worstX = worstY = -1;
        double totalVal = 0, worstVal = 0;
        for (int x = 0; x < numCols; x++) {
          for (int y = 0; y < numRows; y++) {
            totalVal += rcseCache[y][x];
            if ((mask == null || mask[y][x]) && !stalled[y][x] &&
              rcseCache[y][x] > worstVal)
            {
              worstVal = rcseCache[y][x];
              worstX = x;
              worstY = y;
            }
          }
        }
        totalRCSE = totalVal;
      }

      // HACK - workaround for probable bug
      if (worstY < 0 || worstX < 0) worstX = worstY = 0;

      double worst = rcseCache[worstY][worstX];
      if (stallCount == 0) worstRCSE = worst; // update global worst RCSE value
      currentX = worstX;
      currentY = worstY;
      currentCurves[currentY][currentX].iterate();
      totalIterations++;
      double newRCSE =
        currentCurves[currentY][currentX].getReducedChiSquaredError();
      totalRCSE += newRCSE - rcseCache[currentY][currentX];
      rcseCache[currentY][currentX] = newRCSE;
      if (rcseCache[currentY][currentX] < worst) {
        // error improved
        double[][] curve = currentCurves[currentY][currentX].getCurve();
        double[] exponentials = new double[numExponentials];
        for (int i = 0; i < numExponentials; i++) {
          exponentials[i] = curve[i][1];
        }
        Arrays.sort(exponentials);
        for (int c = 0; c < numExponentials; c++) {
          image[numExponentials-c-1][currentY * numCols + currentX] =
            exponentials[c];
          //image[numExponentials-c-1][currentY * numCols + currentX] = 1;
        }
        worstIter = 0;
      }
      else {
        // no improvement
        worstIter++;
        if (worstIter >= STALL_ITERATIONS) {
          // too many failures; this pixel is stalled
          worstIter = 0;
          stalled[currentY][currentX] = true;
          stallCount++;
          if (stallCount >= maskCount) {
            // every pixel is stalled; retry everything
            for (int y=0; y<numCols; y++) Arrays.fill(stalled[y], false);
            stallCount = 0;
          }
        }
      }
      //System.out.println("x: " + currentX + "  y: " + currentY + "   RCSE: " +
      //  currentCurves[currentY][currentX].getReducedChiSquaredError());
    }
  }

  public double[][] getImage() {
    return image;
  }

  public void setComponentCount(int numExp) {
    numExponentials = numExp;
    image = new double[numExponentials][numRows * numCols];
    curveData.setComponentCount(numExponentials);
  }

  public void setMask(boolean[][] mask) {
    if (mask == null) {
      this.mask = null;
      maskCount = numRows * numCols;
    }
    else {
      if (mask.length < numRows) {
        throw new IllegalArgumentException("Invalid mask: mask.length=" +
          mask.length + ", numRows=" + numRows);
      }
      int count = 0;
      for (int i=0; i<numRows; i++) {
        if (mask[i].length < numCols) {
          throw new IllegalArgumentException("Invalid mask: mask[" + i +
            "].length=" + mask[i].length + ", numCols=" + numCols);
        }
        for (int j=0; j<numCols; j++) if (mask[i][j]) count++;
      }
      this.mask = mask;
      maskCount = count;
    }
    // recount stalled pixels
    int count = 0;
    for (int y=0; y<numRows; y++) {
      Arrays.fill(stalled[y], false);
      for (int x=0; x<numCols; x++) {
        if (stalled[y][x]) count++;
      }
    }
    stallCount = count;
  }

  public boolean[][] getMask() {
    return mask;
  }

  public int getImageX() {
    int sizeX = currentCurves[0].length;
    return currentX * numCols / sizeX;
  }

  public int getImageY() {
    int sizeY = currentCurves.length;
    return currentY * numRows / sizeY;
  }

  public double getTotalRCSE() {
    return totalRCSE;
  }

  public double getWorstRCSE() {
    return worstRCSE;
  }

}
