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
 * @author Eric Kjellman egkjellma at wisc.edu
 */
public class BurnInRenderer extends Renderer {

  protected CurveFitter[][] currentCurves = null;
  protected int currentDim;
  protected double[][] image;
  protected int maxdim;

  public BurnInRenderer(CurveCollection cc) {
    super(cc);
    subsampleLevel = curveData.getSubsamplingDepth();
    currentX = 0;
    currentY = 0;
    totalIterations = 0;
    currentIterations = 0;
    currProgress = 0;
    //maxdim = Math.pow(2, subsampleLevel);
    maxdim = 1 << subsampleLevel;
    maxProgress = 2 * maxdim * maxdim - 1;
    image = new double[1][maxdim*maxdim];
    alive = false;
    ready = true;
    maxIterations = Integer.MAX_VALUE;
    maxRCSE = 1.0d;
    currentDim = 1;
    numExponentials = 1;
  }

  public void setComponentCount(int degrees) {
    image = new double[degrees][maxdim*maxdim];
  }

  public void run() {
    // initial pass - estimates
    while(subsampleLevel >= 0 && alive) {
      currentCurves = curveData.getCurves(subsampleLevel);
      while(currentY < currentDim && alive) {
        while(currentX < currentDim && alive) {
          currentCurves[currentY][currentX].setDegrees(numExponentials);
          currentCurves[currentY][currentX].estimate();
          double[][] curve = currentCurves[currentY][currentX].getCurve();
          double[] exponentials = new double[numExponentials];
          for(int i = 0; i < numExponentials; i++) {
            exponentials[i] = curve[i][1];
          }
          Arrays.sort(exponentials);
          int pixelsize = maxdim / currentDim;
          for(int x = 0; x < pixelsize; x++) {
            for(int y = 0; y < pixelsize; y++) {
              for(int c = 0; c < numExponentials; c++) {
                image[numExponentials-c-1][((currentY * pixelsize + y) * maxdim) +
                                           (currentX * pixelsize + x)] = exponentials[c];
              }
            }
          }
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
    // continuing improvement
    while(alive) {
    }
  }

  public double[][] getImage() {
    return image;
  }

  public int getImageX() {
    return (currentX * (maxdim/currentDim)) + ((maxdim/currentDim) / 2);
  }

  public int getImageY() {
    return (currentY * (maxdim/currentDim)) + ((maxdim/currentDim) / 2);
  }

}
