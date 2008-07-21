package loci.slim;

import java.util.Arrays;
import loci.slim.CurveCollection;
import loci.slim.CurveFitter;

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
