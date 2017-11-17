/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.plugins.util;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.ImageCanvas;
import ij.gui.StackWindow;
import ij.io.FileInfo;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.xml.parsers.ParserConfigurationException;

import loci.formats.FormatTools;
import loci.formats.cache.Cache;
import loci.formats.gui.CacheIndicator;
import loci.formats.gui.XMLWindow;

import org.xml.sax.SAXException;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Extension of StackWindow with additional UI trimmings for animation,
 * virtual stack caching options, metadata, and general beautification.
 */
public class DataBrowser extends StackWindow {

  // -- Constants --

  protected static final int MIN_BROWSER_WIDTH = 400;

  // -- Fields --

//  protected volatile boolean done;

  protected JSpinner fpsSpin;
  protected Button animate, options, metadata;
  protected boolean anim = false;
  protected boolean allowShow = false;

  protected XMLWindow metaWindow;
  protected BrowserOptionsWindow optionsWindow;
  protected String xml;

  protected Scrollbar zScroll, cScroll, tScroll;
  protected Scrollbar[] cSliders;

  protected int[] cLengths;
  protected int[] cIndex;

  //private int slice;

  // -- Constructors --

  public DataBrowser(ImagePlus imp) {
    this(imp, null, null, null, null);
  }

  public DataBrowser(final ImagePlus imp, ImageCanvas ic,
    String[] channels, int[] cLengths)
  {
    this(imp, ic, channels, cLengths, null);
  }

  public DataBrowser(final ImagePlus imp, ImageCanvas ic,
    String[] channels, int[] cLengths, XMLWindow xmlWindow)
  {
    super(imp, ic);

    if (channels == null || channels.length == 0) {
      channels = new String[] {"Channel"};
    }
    if (cLengths == null || cLengths.length == 0) {
      cLengths = new int[] {imp.getNChannels()};
    }
    this.cLengths = cLengths;
    cIndex = new int[cLengths.length];

    // build metadata window
    metaWindow = xmlWindow;
    if (metaWindow == null) {
      metaWindow = new XMLWindow("OME Metadata - " + getTitle());
    }

    // build fancy UI widgets
    while (getComponentCount() > 1) remove(1);
    Panel controls = new Panel() {
      @Override
      public Dimension getPreferredSize() {
        int minWidth = MIN_BROWSER_WIDTH;
        int w = imp.getCanvas().getWidth();
        if (w < minWidth) w = minWidth;
        int h = super.getPreferredSize().height;
        return new Dimension(w, h);
      }
    };

    String cols =
      "5dlu, right:pref, 3dlu, pref:grow, 5dlu, pref, 5dlu, pref, 5dlu";
    //       <-labels->        <------sliders------>       <misc>

    StringBuilder rows = new StringBuilder("4dlu, pref, 3dlu, pref");
    //                   <Z->        <T->        <C->

    for (int i=0; i<channels.length; i++) {
      rows.append(", 3dlu, pref");
    }
    rows.append(", 6dlu");

    controls.setLayout(new FormLayout(cols, rows.toString()));
    controls.setBackground(Color.white);

    int c = imp.getNChannels();
    int z = imp.getNSlices();
    int t = imp.getNFrames();

    boolean hasZ = z > 1;
    boolean hasC = c > 1;
    boolean hasT = t > 1;

    // remove everything except the image canvas
    Component[] comps = getComponents();
    for (Component comp : comps) {
      if (!(comp instanceof ImageCanvas)) remove(comp);
    }

    ImageJ ij = IJ.getInstance();

    if (hasC) {
      cScroll = new Scrollbar(Scrollbar.HORIZONTAL, 1, 1, 1, c + 1);
      add(cScroll);
      if (ij != null) cScroll.addKeyListener(ij);
      cScroll.addAdjustmentListener(this);
      // prevents scroll bar from blinking on Windows
      cScroll.setFocusable(false);
      cScroll.setUnitIncrement(1);
      cScroll.setBlockIncrement(1);
    }
    if (hasZ) {
      zScroll = new Scrollbar(Scrollbar.HORIZONTAL, 1, 1, 1, z + 1);
      add(zScroll);
      if (ij != null) zScroll.addKeyListener(ij);
      zScroll.addAdjustmentListener(this);
      zScroll.setFocusable(false);
      int blockIncrement = Math.max(z / 10, 1);
      zScroll.setUnitIncrement(1);
      zScroll.setBlockIncrement(blockIncrement);
    }
    if (hasT) {
      tScroll = new Scrollbar(Scrollbar.HORIZONTAL, 1, 1, 1, t + 1);
      add(tScroll);
      if (ij != null) tScroll.addKeyListener(ij);
      tScroll.addAdjustmentListener(this);
      tScroll.setFocusable(false);
      int blockIncrement = Math.max(t / 10, 1);
      tScroll.setUnitIncrement(1);
      tScroll.setBlockIncrement(blockIncrement);
    }

    Label zLabel = new Label("Z-depth");
    zLabel.setEnabled(hasZ);
    Label tLabel = new Label("Time");
    tLabel.setEnabled(hasT);

    Label[] cLabels = new Label[channels.length];
    for (int i=0; i<channels.length; i++) {
      cLabels[i] = new Label(channels[i]);
      cLabels[i].setEnabled(hasC);
    }

    final Scrollbar zSlider = hasZ ? zScroll : makeDummySlider();
    final Scrollbar tSlider = hasT ? tScroll : makeDummySlider();

    cSliders = new Scrollbar[channels.length];
    Panel[] cPanels = new Panel[channels.length];
    for (int i=0; i<channels.length; i++) {
      if (channels.length == 1) {
        cSliders[i] = hasC ? cScroll : makeDummySlider();
      }
      else if (cLengths[i] == 1) {
        cSliders[i] = makeDummySlider();
      }
      else {
        cSliders[i] =
          new Scrollbar(Scrollbar.HORIZONTAL, 1, 1, 1, cLengths[i] + 1);
        cSliders[i].addAdjustmentListener(this);
      }
      cPanels[i] = makeHeavyPanel(cSliders[i]);
    }

    Panel zPanel = makeHeavyPanel(zSlider);
    Panel tPanel = makeHeavyPanel(tSlider);

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
      if (hasZ) {
        CacheIndicator zCache =
          new CacheIndicator(cache, channels.length, zSlider, 10, 20);
        zPanel.add(zCache, BorderLayout.SOUTH);
      }
      if (hasT) {
        CacheIndicator tCache =
          new CacheIndicator(cache, channels.length + 1, tSlider, 10, 20);
        tPanel.add(tCache, BorderLayout.SOUTH);
      }
      for (int i=0; i<channels.length; i++) {
        if (cLengths[i] > 1) {
          CacheIndicator cCache =
            new CacheIndicator(cache, i, cSliders[i], 10, 20);
          cPanels[i].add(cCache, BorderLayout.SOUTH);
        }
      }

      String[] axes = new String[channels.length + 2];
      System.arraycopy(channels, 0, axes, 0, channels.length);
      axes[channels.length] = "Z";
      axes[channels.length + 1] = "T";
      optionsWindow =
        new BrowserOptionsWindow("Options - " + getTitle(), cache, axes);
    }

    animate = new Button("Animate");
    animate.addActionListener(this);

    fpsSpin.setEnabled(hasT);
    fpsLabel.setEnabled(hasT);
    animate.setEnabled(hasT);

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

    int row = 6;

    // place Options and Metadata buttons intelligently
    if (channels.length == 1) {
      controls.add(options, cc.xy(6, row));
      controls.add(metadata, cc.xy(8, row));
      controls.add(cLabels[0], cc.xy(2, row));
      controls.add(cPanels[0], cc.xy(4, row));
    }
    else {
      controls.add(options, cc.xy(8, row));
      controls.add(metadata, cc.xy(8, row + 2));
      for (int i=0; i<channels.length; i++) {
        int w = i < 2 ? 3 : 5;
        controls.add(cLabels[i], cc.xy(2, row));
        controls.add(cPanels[i], cc.xyw(4, row, w));
        row += 2;
      }
    }

    add(controls, BorderLayout.SOUTH);

    FileInfo fi = imp.getOriginalFileInfo();
    if (fi.description != null && fi.description.startsWith("<?xml")) {
      setXML(fi.description);
    }

    allowShow = true;
    pack();
    setVisible(true);

    // start up animation thread
    if (hasT) {
      // NB: Cannot implement Runnable because one of the superclasses does so
      // for its SliceSelector thread, and overriding results in a conflict.
      new Thread("DataBrowser-Animation") {
        @Override
        public void run() {
          while (isVisible()) {
            int ms = 200;
            if (anim) {
              int c = imp.getChannel();
              int z = imp.getSlice();
              int t = imp.getFrame() + 1;
              int sizeT = tSlider.getMaximum() - 1;
              if (t > sizeT) t = 1;
              imp.setPosition(c, z, t);
              syncSliders();
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

  // -- DataBrowser methods --

  /**
   * Sets XML block associated with this window. This information will be
   * displayed in a tree structure when the Metadata button is clicked.
   */
  public void setXML(String xml) {
    try {
      metaWindow.setXML(xml);
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
    metadata.setEnabled(metaWindow.getDocument() != null);
  }

  /** Toggles whether the data browser is animating. */
  public void toggleAnimation() {
    animate.setLabel(anim ? "Animate" : "Stop");
    anim = !anim;
  }

  /** Displays the caching options window onscreen. */
  public void showOptionsWindow() {
    // center window and show
    Rectangle r = getBounds();
    Dimension w = optionsWindow.getSize();
    int x = Math.max(5, r.x + (r.width - w.width) / 2);
    int y = Math.max(5, r.y + (r.height - w.height) / 2);
    optionsWindow.setLocation(x, y);
    optionsWindow.setVisible(true);
  }

  /** Displays the OME-XML metadata window onscreen. */
  public void showMetadataWindow() {
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

  // -- Window methods --

  /** Overridden pack method to allow us to delay initial window sizing. */
  @Override
  public void pack() {
    if (allowShow) super.pack();
  }

  // -- Component methods --

  /** Overridden show method to allow us to delay initial window display. */
  @Override
  public void setVisible(boolean b) {
    if (allowShow) super.setVisible(b);
  }

  // -- ActionListener methods --

  @Override
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src == animate) toggleAnimation();
    else if (src == options) showOptionsWindow();
    else if (src == metadata) showMetadataWindow();
    // NB: Do not eat superclass events. Om nom nom nom. :-)
    else super.actionPerformed(e);
  }

  // -- AdjustmentListener methods --

  @Override
  public synchronized void adjustmentValueChanged(AdjustmentEvent e) {
    super.adjustmentValueChanged(e);
    syncPlane();
  }

  // -- MouseWheelListener methods --

  @Override
  public void mouseWheelMoved(MouseWheelEvent event) {
    super.mouseWheelMoved(event);
    syncSliders();
  }

  /*
  // -- Runnable methods --

  @Override
  public void run() {
    while (!done) {
      synchronized (this) {
        try {
          wait();
        }
        catch (InterruptedException e) { }
      }
      if (done) return;
      if (slice > 0 && slice != imp.getCurrentSlice()) {
        imp.setSlice(slice);
        slice = 0;
      }
    }
  }
  */

  // -- Helper methods --

  /*
  private void updateSlice() {
    int sizeZ = imp.getNSlices();
    int sizeC = imp.getNChannels();
    int sizeT = imp.getNFrames();
    int[] dims = new int[] {sizeZ, sizeC, sizeT};
    int z = imp.getSlice() - 1;
    int c = imp.getChannel() - 1;
    int t = imp.getFrame() - 1;
    int[] pos = new int[] {z, c, t};
    slice = FormatTools.positionToRaster(dims, pos) + 1;
  }
  */

  /** Updates the ImagePlus's displayed plane to match the slider values. */
  private void syncPlane() {
    for (int i=0; i<cSliders.length; i++) {
      cIndex[i] = cSliders[i].getValue() - 1;
    }
    int c = FormatTools.positionToRaster(cLengths, cIndex) + 1;
    int z = zScroll == null ? 1 : zScroll.getValue();
    int t = tScroll == null ? 1 : tScroll.getValue();
    setPosition(c, z, t);
    imp.setPosition(c, z, t);
  }

  /** Updates the slider values to match the ImagePlus's displayed plane. */
  private void syncSliders() {
    cIndex = FormatTools.rasterToPosition(cLengths, imp.getChannel() - 1);
    for (int i=0; i<cSliders.length; i++) {
      cSliders[i].setValue(cIndex[i] + 1);
    }
    if (zScroll != null) zScroll.setValue(imp.getSlice());
    if (tScroll != null) tScroll.setValue(imp.getFrame());
  }

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
