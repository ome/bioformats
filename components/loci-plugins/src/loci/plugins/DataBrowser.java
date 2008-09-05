//
// DataBrowser.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser, Stack Colorizer and Stack Slicer. Copyright (C) 2005-@year@
Melissa Linkert, Curtis Rueden and Christopher Peterson.

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

package loci.plugins;

import com.jgoodies.forms.layout.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.ImageCanvas;
import ij.gui.StackWindow;
import ij.io.FileInfo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.parsers.*;
import loci.formats.cache.Cache;
import loci.formats.gui.CacheIndicator;
import loci.formats.gui.XMLCellRenderer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Extension of StackWindow with additional UI trimmings for animation,
 * virtual stack caching options, metadata, and general beautification.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/DataBrowser.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/DataBrowser.java">SVN</a></dd></dl>
 */
public class DataBrowser extends StackWindow implements ActionListener {

  // -- Fields --

  protected JSpinner fpsSpin;
  protected Button animate, options, metadata;
  protected boolean anim = false;
  protected boolean allowShow = false;

  protected JFrame metaWindow;
  protected BrowserOptionsWindow optionsWindow;
  protected String xml;

  // -- Constructors --

  public DataBrowser(ImagePlus imp) {
    this(imp, null);
  }

  public DataBrowser(final ImagePlus imp, ImageCanvas ic) {
    super(imp, ic);

    // build metadata window
    metaWindow = new JFrame("Metadata - " + getTitle());

    // build fancy UI widgets
    while (getComponentCount() > 1) remove(1);
    Panel controls = new Panel() {
      public Dimension getPreferredSize() {
        int minWidth = 200;
        int w = imp.getCanvas().getWidth();
        if (w < minWidth) w = minWidth;
        int h = super.getPreferredSize().height;
        return new Dimension(w, h);
      }
    };

    String cols =
      "5dlu, right:pref, 3dlu, pref, pref:grow, pref, 5dlu, pref, 5dlu";
    //       <-labels->        <------sliders------>       <misc>

    String rows = "4dlu, pref, 3dlu, pref, 3dlu, pref, 6dlu";
    //                   <Z->        <T->        <C->

    controls.setLayout(new FormLayout(cols, rows));
    controls.setBackground(Color.white);

    Label zLabel = new Label("z-depth");
    if (sliceSelector == null) zLabel.setEnabled(false);
    Label tLabel = new Label("time");
    if (frameSelector == null) tLabel.setEnabled(false);
    Label cLabel = new Label("channel");
    if (channelSelector == null) cLabel.setEnabled(false);

    Scrollbar zSlider = sliceSelector == null ?
      makeDummySlider() : sliceSelector;
    Scrollbar tSlider = frameSelector == null ?
      makeDummySlider() : frameSelector;
    Scrollbar cSlider = channelSelector == null ?
      makeDummySlider() : channelSelector;

    Panel zPanel = makeHeavyPanel(zSlider);
    Panel tPanel = makeHeavyPanel(tSlider);
    Panel cPanel = makeHeavyPanel(cSlider);

    fpsSpin = new JSpinner(new SpinnerNumberModel(10, 1, 99, 1));
    fpsSpin.setToolTipText("Animation rate in frames per second");
    Label fpsLabel = new Label(" FPS");
    Panel fpsPanel = new Panel();
    fpsPanel.setLayout(new BorderLayout());
    fpsPanel.add(fpsSpin, BorderLayout.CENTER);
    fpsPanel.add(fpsLabel, BorderLayout.EAST);

    ImageStack stack = imp.getStack();
    if (stack instanceof BFVirtualStack) {
      BFVirtualStack bfvs = (BFVirtualStack) stack;
      Cache cache = bfvs.getCache();
      CacheIndicator zCache = new CacheIndicator(cache, 1, zSlider, 10, 20);
      zPanel.add(zCache, BorderLayout.SOUTH);
      CacheIndicator tCache = new CacheIndicator(cache, 2, tSlider, 10, 20);
      tPanel.add(tCache, BorderLayout.SOUTH);
      CacheIndicator cCache = new CacheIndicator(cache, 0, cSlider, 10, 20);
      cPanel.add(cCache, BorderLayout.SOUTH);

      optionsWindow =
        new BrowserOptionsWindow("Options - " + getTitle(), cache);
    }

    animate = new Button("Animate");
    animate.addActionListener(this);
    if (frameSelector == null) {
      fpsSpin.setEnabled(false);
      fpsLabel.setEnabled(false);
      animate.setEnabled(false);
    }

    options = new Button("Options");
    options.addActionListener(this);
    options.setEnabled(optionsWindow != null);
    metadata = new Button("Metadata");
    metadata.addActionListener(this);
    metadata.setEnabled(false);

    CellConstraints cc = new CellConstraints();

    controls.add(zLabel, cc.xy(2, 2));
    controls.add(zPanel, cc.xyw(4, 2, 3));
    controls.add(fpsPanel, cc.xy(8, 2));

    controls.add(tLabel, cc.xy(2, 4));
    controls.add(tPanel, cc.xyw(4, 4, 3));
    controls.add(animate, cc.xy(8, 4));

    controls.add(cLabel, cc.xy(2, 6));
    controls.add(cPanel, cc.xy(4, 6));
    controls.add(options, cc.xy(6, 6));
    controls.add(metadata, cc.xy(8, 6));

    add(controls, BorderLayout.SOUTH);

    FileInfo fi = imp.getOriginalFileInfo();
    if (fi.description != null && fi.description.startsWith("<?xml")) {
      setXML(fi.description);
    }

    allowShow = true;
    pack();
    setVisible(true);

    // start up animation thread
    if (frameSelector != null) {
      // NB: Cannot implement Runnable because one of the superclasses does so
      // for its SliceSelector thread, and overriding results in a conflict.
      new Thread("DataBrowser-Animation") {
        public void run() {
          while (isVisible()) {
            int ms = 200;
            if (anim) {
              int c = imp.getChannel();
              int z = imp.getSlice();
              int t = imp.getFrame() + 1;
              int sizeT = frameSelector.getMaximum() - 1;
              if (t > sizeT) t = 1;
              setPosition(c, z, t);
              int fps = ((Number) fpsSpin.getValue()).intValue();
              ms = 1000 / fps;
            }
            try {
              Thread.sleep(ms);
            }
            catch (InterruptedException exc) { }
          }
        }
      }.start();
    }
  }

  // -- DataBrowser API methods --

  /**
   * Sets XML block associated with this window. This information will be
   * displayed in a tree structure when the Metadata button is clicked.
   */
  public void setXML(String xml) {
    this.xml = xml;
    metaWindow.getContentPane().removeAll();
    boolean success = false;
    if (xml == null) {
      metaWindow.setVisible(false);
      success = true;
    }
    else {
      try {
        // parse XML into DOM structure
        DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = docFact.newDocumentBuilder();
        ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes());
        Document doc = db.parse(is);
        is.close();

        // construct metadata window and size intelligently
        JTree tree = XMLCellRenderer.makeJTree(doc);
        for (int i=0; i<tree.getRowCount(); i++) tree.expandRow(i);
        metaWindow.getContentPane().add(new JScrollPane(tree));
        metaWindow.pack();
        Dimension dim = metaWindow.getSize();
        int pad = 20;
        dim.width += pad;
        dim.height += pad;
        Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
        int maxWidth = 3 * ss.width / 4;
        int maxHeight = 3 * ss.height / 4;
        if (dim.width > maxWidth) dim.width = maxWidth;
        if (dim.height > maxHeight) dim.height = maxHeight;
        metaWindow.setSize(dim);

        success = true;
      }
      catch (ParserConfigurationException exc) {
        exc.printStackTrace();
      }
      catch (SAXException exc) {
        exc.printStackTrace();
      }
      catch (IOException exc) {
        exc.printStackTrace();
      }
    }
    metadata.setEnabled(success);
  }

  // -- Window API methods --

  public void dispose() {
    super.dispose();
  }

  /** Overridden pack method to allow us to delay initial window sizing. */
  public void pack() {
    if (allowShow) super.pack();
  }

  // -- Component API methods --

  /** Overridden show method to allow us to delay initial window display. */
  public void setVisible(boolean b) {
    if (allowShow) super.setVisible(b);
  }

  // -- ActionListener API methods --

  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src == animate) {
      animate.setLabel(anim ? "Animate" : "Stop");
      anim = !anim;
    }
    else if (src == options) {
      // center window and show
      Rectangle r = getBounds();
      Dimension w = optionsWindow.getSize();
      int x = (int) Math.min(5, r.x + (r.width - w.width) / 2);
      int y = (int) Math.min(5, r.y + (r.height - w.height) / 2);
      optionsWindow.setLocation(x, y);
      optionsWindow.setVisible(true);
    }
    else if (src == metadata) {
      // center window and show
      Rectangle r = getBounds();
      Dimension w = metaWindow.getSize();
      int x = r.x + (r.width - w.width) / 2;
      int y = r.y + (r.height - w.height) / 2;
      if (x < 5) x = 5;
      if (y < 5) y = 5;
      metaWindow.setLocation(x, y);
      metaWindow.setVisible(true);
    }
    // NB: Do not eat superclass events. Om nom nom nom. :-)
    else super.actionPerformed(e);
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
