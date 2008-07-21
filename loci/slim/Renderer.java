package loci.slim;

import loci.slim.CurveCollection;

public abstract class Renderer implements Runnable {

  protected boolean alive;
  protected boolean ready;
  protected int maxIterations;
  protected double maxRCSE;
  protected int totalIterations;
  protected int currentIterations;
  protected int currentX;
  protected int currentY;
  protected int subsampleLevel;
  protected int currProgress;
  protected int maxProgress;
  protected CurveCollection curveData;
  protected int numExponentials;
  
  public abstract void run();

  public void stop() {
    // TODO: Check if alive and ready?
    alive = false;
  }

  public Renderer(CurveCollection cc) {
    curveData = cc;
  }

  public int getCurrentIterations() {
    return currentIterations;
  }

  public int getTotalIterations() {
    return totalIterations;
  }
  
  public int getCurrentX() {
    return currentX;
  }

  public int getCurrentY() {
    return currentY;
  }

  public int getSubsampleLevel() {
    return subsampleLevel;
  }

  public int getCurrentProgress() {
    return currProgress;
  }

  public int getMaxProgress() {
    return maxProgress;
  }
  
  public void setMaxIterations(int mi) {
    maxIterations = mi;
  }

  public void setMaxRCSE(double mr) {
    maxRCSE = mr;
  }

  public int getMaxIterations() {
    return maxIterations;
  }

  public double getMaxRCSE() {
    return maxRCSE;
  }
  
  public abstract double[][] getImage();
  
  public void setComponentCount(int degrees) {
    numExponentials = degrees;
  }

  public abstract int getImageX();

  public abstract int getImageY();
  
}
