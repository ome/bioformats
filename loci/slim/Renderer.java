//
// Renderer.java
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

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/slim/Renderer.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/slim/Renderer.java">SVN</a></dd></dl>
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 */
public abstract class Renderer implements Runnable {

  protected boolean alive;
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

  /**
   * The renderer's run method should use the curve collection and somehow
   * continually update the image array returned by getImage()
   * The method for this is left to the sub class. The idea is that the
   * image should get better the longer it is run, so some sort of iterative
   * approach is best.
   * The run method should set alive to true at the beginning, and run until
   * stop is called (and alive is set to false)
   **/
  public abstract void run();

  /** Called to stop the run() method. */
  public void stop() {
    alive = false;
  }

  /** Constructor. Get the curve collection to generate the image from */
  public Renderer(CurveCollection cc) {
    curveData = cc;
  }

  // Accessor methods
  
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

  /** Return the current rendered image */
  public abstract double[][] getImage();

  public void setComponentCount(int numExp) {
    numExponentials = numExp;
  }

  /** Get the X coordinate currently being improved */
  public abstract int getImageX();

  /** Get the Y coordinate currently being improved */
  public abstract int getImageY();

  /** Get the current worst RCSE for any curve in the collection */
  public abstract double getWorstRCSE();
}
