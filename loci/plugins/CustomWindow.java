//
// CustomWindow.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Data Browser, Stack Colorizer,
Stack Slicer, and OME plugins. Copyright (C) 2005-@year@ Melissa Linkert,
Curtis Rueden, Christopher Peterson, Philip Huettl and Francis Wong.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.plugins;

import com.jgoodies.forms.layout.*;
import java.awt.*;
import javax.swing.*;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.StackWindow;
import loci.formats.gui.CacheIndicator;

/**
 * Extension of StackWindow with additional UI trimmings for animation,
 * virtual stack caching options, metadata, and general beautification.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/CustomWindow.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/CustomWindow.java">SVN</a></dd></dl>
 */
public class CustomWindow extends StackWindow {

  // -- Fields --

  protected boolean allowShow = false;

  // -- Constructors --

  public CustomWindow(ImagePlus imp) {
    this(imp, null);
  }

  public CustomWindow(final ImagePlus imp, ImageCanvas ic) {
    super(imp, ic);

    // build fancy UI widgets
    while (getComponentCount() > 1) remove(1);
    Panel controls = new Panel() {
      public Dimension getPreferredSize() {
        Dimension prefSize = super.getPreferredSize();
        return new Dimension(imp.getCanvas().getWidth(), prefSize.height);
      }
    };
    String cols = "5dlu, right:pref, 3dlu, pref:grow, pref, 5dlu, pref, 5dlu";
    String rows = "5dlu, pref, 5dlu, pref, 5dlu, pref, 8dlu";
    controls.setLayout(new FormLayout(cols, rows));
    controls.setBackground(Color.white);

    Label zLabel = new Label("z-depth");
    Label tLabel = new Label("time");
    Label cLabel = new Label("channel");

    Scrollbar zSlider = sliceSelector == null ?
      makeDummySlider() : sliceSelector;
    Scrollbar tSlider = frameSelector == null ?
      makeDummySlider() : frameSelector;


    CacheIndicator zCache = new CacheIndicator(zSlider);
    Panel zPanel = makeHeavyPanel(zSlider);
    zPanel.add(zCache, BorderLayout.SOUTH);

    CacheIndicator tCache = new CacheIndicator(tSlider);
    Panel tPanel = makeHeavyPanel(tSlider);
    tPanel.add(tCache, BorderLayout.SOUTH);

    JSpinner fpsSpin = new JSpinner(new SpinnerNumberModel(10, 1, 99, 1));
    fpsSpin.setToolTipText("Animation rate in frames per second");
    Label fpsLabel = new Label(" FPS");
    Panel fpsPanel = new Panel();
    fpsPanel.setLayout(new BorderLayout());
    fpsPanel.add(fpsSpin, BorderLayout.CENTER);
    fpsPanel.add(fpsLabel, BorderLayout.EAST);

    Button animate = new Button("Animate");

    int sizeC = channelSelector == null ? 1 : channelSelector.getMaximum() - 1;
    Component cComp;
    if (sizeC < 3) {
      Checkbox cBox = new Checkbox("Transmitted");
      if (sizeC != 2) cBox.setEnabled(false);
      cComp = cBox;
    }
    else {
      JSpinner cSpin = new JSpinner(new SpinnerNumberModel(1, 1, sizeC, 1));
      cComp = makeHeavyPanel(cSpin);
    }

    Button options = new Button("Options");
    options.setEnabled(false);
    Button metadata = new Button("Metadata");
    metadata.setEnabled(false);

    CellConstraints cc = new CellConstraints();

    controls.add(zLabel, cc.xy(2, 2));
    controls.add(zPanel, cc.xyw(4, 2, 2));
    controls.add(fpsPanel, cc.xy(7, 2));

    controls.add(tLabel, cc.xy(2, 4));
    controls.add(tPanel, cc.xyw(4, 4, 2));
    controls.add(animate, cc.xy(7, 4));

    controls.add(cLabel, cc.xy(2, 6));
    controls.add(cComp, cc.xy(4, 6));
    controls.add(options, cc.xy(5, 6));
    controls.add(metadata, cc.xy(7, 6));

    add(controls, BorderLayout.SOUTH);

    allowShow = true;
    pack();
    setVisible(true);
  }

  // -- Window API methods --

  /** Overridden pack method to allow us to delay initial window sizing. */
  public void pack() {
    if (allowShow) super.pack();
  }

  // -- Component API methods --

  /** Overridden show method to allow us to delay initial window display. */
  public void setVisible(boolean b) {
    if (allowShow) super.setVisible(b);
  }

  // -- Helper methods --

  protected static Scrollbar makeDummySlider() {
    Scrollbar scrollbar = new Scrollbar(Scrollbar.HORIZONTAL, 1, 1, 1, 2);
    scrollbar.setFocusable(false);
    scrollbar.setUnitIncrement(1);
    scrollbar.setBlockIncrement(1);
    scrollbar.setEnabled(false);
    return scrollbar;
  }

  /** Makes AWT play nicely with Swing components. */
  protected static Panel makeHeavyPanel(Component c) {
    Panel panel = new Panel();
    panel.setLayout(new BorderLayout());
    panel.add(c, BorderLayout.CENTER);
    return panel;
  }

}
