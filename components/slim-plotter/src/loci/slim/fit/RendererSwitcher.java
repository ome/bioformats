//
// RendererSwitcher.java
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

/**
 * Renderer manager capable of switching between multiple renderers.
 * Used primarily to switch between lifetime computation in multiple channels.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/slim-plotter/src/loci/slim/fit/RendererSwitcher.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/slim-plotter/src/loci/slim/fit/RendererSwitcher.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class RendererSwitcher implements ICurveRenderer {

  // -- Fields --

  protected ICurveRenderer[] renderers;
  protected int c;
  protected boolean alive;

  // -- Constructor --

  public RendererSwitcher(ICurveRenderer[] renderers) {
    this.renderers = renderers;
  }

  // -- RendererSwitcher methods --

  public void setCurrent(int current) {
    if (current == c) return;
    int lastC = c;
    c = current;
    renderers[lastC].stop();
  }

  public ICurveRenderer[] getCurveRenderers() {
    return renderers;
  }

  public CurveCollection[] getCurveCollections() {
    CurveCollection[] cc = new CurveCollection[renderers.length];
    for (int i=0; i<cc.length; i++) cc[i] = renderers[i].getCurveCollection();
    return cc;
  }

  // -- ICurveRenderer methods --

  public CurveCollection getCurveCollection() {
    return renderers[c].getCurveCollection();
  }

  public void run() {
    alive = true;
    while (alive) renderers[c].run();
  }

  public void stop() {
    alive = false;
    renderers[c].stop();
  }

  public int getCurrentIterations() {
    return renderers[c].getCurrentIterations();
  }

  public int getTotalIterations() {
    return renderers[c].getTotalIterations();
  }

  public int getCurrentX() {
    return renderers[c].getCurrentX();
  }

  public int getCurrentY() {
    return renderers[c].getCurrentY();
  }

  public int getSubsampleLevel() {
    return renderers[c].getSubsampleLevel();
  }

  public int getCurrentProgress() {
    return renderers[c].getCurrentProgress();
  }

  public int getMaxProgress() {
    return renderers[c].getMaxProgress();
  }

  public void setMaxIterations(int mi) {
    for (int i=0; i<renderers.length; i++) {
      renderers[i].setMaxIterations(mi);
    }
  }

  public void setMaxRCSE(double mr) {
    for (int i=0; i<renderers.length; i++) {
      renderers[i].setMaxRCSE(mr);
    }
  }

  public int getMaxIterations() {
    return renderers[c].getMaxIterations();
  }

  public double getMaxRCSE() {
    return renderers[c].getMaxRCSE();
  }

  public double[][] getImage() {
    return renderers[c].getImage();
  }

  public void setComponentCount(int numExp) {
    for (int i=0; i<renderers.length; i++) {
      renderers[i].setComponentCount(numExp);
    }
  }

  public int getImageX() {
    return renderers[c].getImageX();
  }

  public int getImageY() {
    return renderers[c].getImageY();
  }

  public double getTotalRCSE() {
    return renderers[c].getTotalRCSE();
  }

  public double getWorstRCSE() {
    return renderers[c].getWorstRCSE();
  }

}
