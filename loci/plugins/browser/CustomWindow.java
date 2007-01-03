//
// CustomWindow.java
//

/*
LOCI 4D Data Browser plugin for quick browsing of 4D datasets in ImageJ.
Copyright (C) 2005-@year@ Christopher Peterson, Francis Wong, Curtis Rueden
and Melissa Linkert.

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
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1G307  USA
*/

package loci.plugins.browser;

import ij.*;
import ij.gui.*;
import ij.measure.Calibration;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import loci.formats.ReflectedUniverse;
import loci.formats.ReflectException;
import loci.formats.FilePattern;
//import loci.ome.notebook.MetadataNotebook;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class CustomWindow extends ImageWindow implements ActionListener,
  AdjustmentListener, ChangeListener, ItemListener, KeyListener
{

  // -- Constants --

  private static final String Z_STRING = "z-depth";
  private static final String T_STRING = "time";

  private static final String ANIM_STRING = "Animate";
  private static final String STOP_STRING = "Stop";

  /**Constant dlu size for indents in GUI*/
  private static final String TAB = "5dlu";

  // -- Fields - state --

  protected LociDataBrowser db;
  protected CacheIndicator zIndicator,tIndicator;
  protected OptionsWindow ow;
  private String zString = Z_STRING;
  private String tString = T_STRING;
  private int fps = 10;
  private int z = 1, t = 1, c = 1;

  // -- Fields - widgets --

  private JLabel zLabel, tLabel, cLabel;
  protected JScrollBar zSliceSel, tSliceSel;
  private JButton xml;
  private Timer animationTimer;
  private JButton animate;
  private JButton options;
  private Panel lowPane;
  private CellConstraints cc;
  protected JSpinner channelSpin;
  private JCheckBox channelBox;
  private CardLayout switcher;
  private JPanel channelPanel;
  private String patternTitle;
  protected boolean update;

  // -- Constructor --

  /** CustomWindow constructors, initialization. */
  public CustomWindow(LociDataBrowser db, ImagePlus imp, ImageCanvas canvas) {
    super(imp, canvas);
    this.db = db;
    ow = null;
    update = true;

    String id = db.id;

    FilePattern fp = null;

    if(db.fStitch != null) {
      try {
        fp = db.fStitch.getFilePattern(id);
        patternTitle = fp.getPattern();
      }
      catch (Exception exc) {
        exc.printStackTrace();
        LociDataBrowser.exceptionMessage(exc);
      }
    }

    if (fp == null) patternTitle = id;
    setTitle(patternTitle);

    // create panel for image canvas
    Panel imagePane = new Panel() {
      public void paint(Graphics g) {
        // paint bounding box here instead of in ImageWindow directly
        Point loc = ic.getLocation();
        Dimension csize = ic.getSize();
        g.drawRect(loc.x - 1, loc.y - 1, csize.width + 1, csize.height + 1);
      }
    };

//    imagePane.setLayout(getLayout()); // ImageLayout
//    imagePane.setBackground(Color.white);

    // redo layout for master window
    remove(ic);
    imagePane.add(ic);

    // Z scroll bar label
    zLabel = new JLabel(zString);
    zLabel.setHorizontalTextPosition(JLabel.LEFT);
    if (!db.hasZ) zLabel.setEnabled(false);

    // T scroll bar label
    tLabel = new JLabel(tString);
    tLabel.setHorizontalTextPosition(JLabel.LEFT);
    if (!db.hasT) tLabel.setEnabled(false);

    // Z scroll bar
    zSliceSel = new JScrollBar(JScrollBar.HORIZONTAL,
      1, 1, 1, db.hasZ ? db.numZ + 1 : 2);
    if (!db.hasZ) zSliceSel.setEnabled(false);
    zSliceSel.addAdjustmentListener(this);
    zSliceSel.setUnitIncrement(1);
    zSliceSel.setBlockIncrement(5);

    // T scroll bar
    tSliceSel = new JScrollBar(JScrollBar.HORIZONTAL,
      1, 1, 1, db.hasT ? db.numT + 1 : 2);
    if (!db.hasT) tSliceSel.setEnabled(false);
    tSliceSel.addAdjustmentListener(this);
    tSliceSel.setUnitIncrement(1);
    tSliceSel.setBlockIncrement(5);

    // animate button
    animate = new JButton(ANIM_STRING);
    if (!db.hasT && !db.hasZ) animate.setEnabled(false);
    animate.addActionListener(this);

    // options button

    options = new JButton("Options");
    options.setActionCommand("options");
    options.addActionListener(this);
    options.setEnabled(true);

    // OME-XML button
    boolean canDoXML = true;
    try {
      // disable XML button if proper libraries are not installed
      Class.forName("org.openmicroscopy.xml.OMENode"); // ome-java.jar
      Class.forName("loci.ome.notebook.MetadataNotebook"); // ome-notebook.jar
      Class.forName("com.jgoodies.forms.layout.FormLayout"); // forms-1.0.4.jar
    }
    catch (Throwable e) {
      canDoXML = false;
    }
    xml = null;
    if (canDoXML) {
      xml = new JButton("Metadata");
      xml.addActionListener(this);
      xml.setActionCommand("xml");
    }

    switcher = new CardLayout();
    channelPanel = new JPanel(switcher);
    channelPanel.setOpaque(false);

    channelBox = new JCheckBox("Transmitted");
    channelBox.setBackground(Color.white);
    channelPanel.add("one", channelBox);
    channelBox.addItemListener(this);

    JPanel subPane = new JPanel(new FlowLayout());
    subPane.setBackground(Color.white);
    cLabel = new JLabel("channel");
    SpinnerModel model = new SpinnerNumberModel(1, 1, db.numC, 1);
    channelSpin = new JSpinner(model);
    channelSpin.setEditor(new JSpinner.NumberEditor(channelSpin));
    channelSpin.addChangeListener(this);
    subPane.add(cLabel);
    subPane.add(channelSpin);
    channelPanel.add("many", subPane);

    // repack to take extra panel into account
    c = db.numC;

    if (db.numC * db.numT * db.numZ == imp.getStackSize()) {
      c = db.numC;
    }
    else c = 1;

    setC();

    JPanel zPanel = new JPanel(new BorderLayout());
    JPanel tPanel = new JPanel(new BorderLayout());
    zPanel.add(zSliceSel,BorderLayout.CENTER);
    tPanel.add(tSliceSel,BorderLayout.CENTER);

    if(db.virtual) {
      zIndicator = new CacheIndicator(zSliceSel);
      tIndicator = new CacheIndicator(tSliceSel);
      zPanel.add(zIndicator,BorderLayout.SOUTH);
      tPanel.add(tIndicator,BorderLayout.SOUTH);
    }

    //setup the layout
    lowPane = new Panel();
    FormLayout layout = new FormLayout(
        TAB + ",pref," + TAB + ",pref:grow," + TAB + ",pref," + TAB + ",pref,"
        + TAB + ",pref," + TAB,
        TAB + ",pref," + TAB + ",pref," + TAB + ",pref," + TAB);
    lowPane.setLayout(layout);
    lowPane.setBackground(Color.white);

    cc = new CellConstraints();

    lowPane.add(zLabel, cc.xy(2,2));
    lowPane.add(zPanel, cc.xyw(4,2,5));
    lowPane.add(channelPanel, cc.xy(10,2));
    lowPane.add(tLabel, cc.xy(2,4));
    lowPane.add(tPanel, cc.xyw(4,4,5));
    lowPane.add(options, cc.xy(6,6));
    if(xml != null) lowPane.add(xml, cc.xy(8,6));
    lowPane.add(animate, cc.xy(10,6, "right,center"));

    setBackground(Color.white);
    FormLayout layout2 = new FormLayout(
      TAB + ",pref:grow," + TAB,
      TAB + "," + TAB + ",pref:grow," + TAB + ",pref," + TAB);
    setLayout(layout2);
    CellConstraints cc2 = new CellConstraints();
    add(imagePane, cc2.xyw(1,3,3));
    add(lowPane, cc2.xy(2,5));

    //final GUI tasks
    pack();

    showSlice(z, t, c);

    // listen for arrow key presses
    addKeyListener(this);
    ic.addKeyListener(this);
  }

  // -- CustomWindow methods --

  public void updateControls() {
    update = false;
    zSliceSel.setMinimum(1);
    zSliceSel.setMaximum(db.hasZ ? db.numZ + 1 : 2);
    if (!db.hasZ) zSliceSel.setEnabled(false);
    else zSliceSel.setEnabled(true);
    tSliceSel.setMinimum(1);
    tSliceSel.setMaximum(db.hasT ? db.numT + 1 : 2);
    if (!db.hasT) tSliceSel.setEnabled(false);
    else tSliceSel.setEnabled(true);
    setC();
    repaint();
    update = true;
  }

  public int getC() {
    return c;
  }

  public void setC() {
    boolean hasThis = false;
    int numThis = -1;
    int value = -1;
    hasThis = db.hasC;
    numThis = db.numC;
    value = c;
    if (numThis > 2) {
      // C spinner
      switcher.last(channelPanel);
      SpinnerNumberModel snm = (SpinnerNumberModel) channelSpin.getModel();
      snm.setMaximum((Comparable) new Integer(numThis));
      snm.setValue(new Integer(value));
      c = ((Integer) channelSpin.getValue()).intValue();
      if (!hasThis) {
        channelSpin.setEnabled(false);
        cLabel.setEnabled(false);
        c = 1;
      }
    }
    else {
      // C checkbox
      switcher.first(channelPanel);
      c = channelBox.isSelected() ? 2 : 1;
      if (!hasThis) {
        channelBox.setEnabled(false);
        c = 1;
      }
    }
  }

  /** Selects and shows slice defined by z, t and c. */
  public synchronized void showSlice(int zVal, int tVal, int cVal) {
    int index = db.getIndex(zVal - 1, tVal - 1, cVal - 1);
    if (LociDataBrowser.DEBUG) {
      IJ.log("showSlice: index=" + index +
        "; z=" + zVal + "; t=" + tVal + "; c=" + cVal);
    }
    showSlice(index);
  }

  /** Selects and shows slice defined by index. */
  public void showSlice(int index) {
    index++;

    if (db.manager != null) {
      imp.setProcessor(patternTitle, db.manager.getSlice(index - 1));
      index = 1;
    }

    if (index >= 1 && index <= imp.getStackSize()) {
      synchronized (imp) {
        imp.setSlice(index);
      }
      imp.updateAndDraw();
    }
    else if (LociDataBrowser.DEBUG) {
      IJ.log("invalid index: " + index + " (size = " + imp.getStackSize() +
        "; zSliceSel = " + zSliceSel.getValue() +
        "; tSliceSel = " + tSliceSel.getValue() + ")");
    }
    repaint();
  }

  /** Selects and shows slice defined by z, t and c. */
  public synchronized void showTempSlice(int zVal, int tVal, int cVal) {
    int index = db.getIndex(zVal - 1, tVal - 1, cVal - 1);
    if (LociDataBrowser.DEBUG) {
      IJ.log("showSlice: index=" + index +
        "; z=" + zVal + "; t=" + tVal + "; c=" + cVal);
    }
    showTempSlice(index);
  }

  public void showTempSlice(int index) {
    imp.setProcessor(patternTitle, db.manager.getTempSlice(index));
    imp.updateAndDraw();

    if (LociDataBrowser.DEBUG) {
      IJ.log("invalid index: " + index + " (size = " + imp.getStackSize() +
        "; zSliceSel = " + zSliceSel.getValue() +
        "; tSliceSel = " + tSliceSel.getValue() + ")");
    }
    repaint();
  }

  // -- ImageWindow methods --

  /** Adds 3rd and 4th dimension slice position. */
  public void drawInfo(Graphics g) {
    if(update) {
      if (db == null) return;
      int zVal = zSliceSel == null ? 1 : zSliceSel.getValue();
      int tVal = tSliceSel == null ? 1 : tSliceSel.getValue();

      int textGap = 0;

      int nSlices = db.numZ * db.numT * db.numC;
      int currentSlice = imp.getCurrentSlice();
      if (db.manager != null) {
        currentSlice = db.manager.getSlice();
        currentSlice++;
      }

      StringBuffer sb = new StringBuffer();
      sb.append(currentSlice);
      sb.append("/");
      sb.append(nSlices);
      sb.append("; ");
      if (db.hasZ) {
        sb.append(zString);
        sb.append(": ");
        sb.append(zVal);
        sb.append("/");
        sb.append(db.numZ);
        sb.append("; ");
      }
      if (db.hasT) {
        sb.append(tString);
        sb.append(": ");
        sb.append(tVal);
        sb.append("/");
        sb.append(db.numT);
        sb.append("; ");
      }
      if (db.names != null) {
        String name = db.names[currentSlice - 1];
        if (name != null) {
          sb.append(name);
          sb.append("; ");
        }
      }

      int width = imp.getWidth(), height = imp.getHeight();
      Calibration cal = imp.getCalibration();
      if (cal.pixelWidth != 1.0 || cal.pixelHeight != 1.0) {
        sb.append(IJ.d2s(width * cal.pixelWidth, 2));
        sb.append("x");
        sb.append(IJ.d2s(height * cal.pixelHeight, 2));
        sb.append(" ");
        sb.append(cal.getUnits());
        sb.append(" (");
        sb.append(width);
        sb.append("x");
        sb.append(height);
        sb.append("); ");
      }
      else {
        sb.append(width);
        sb.append("x");
        sb.append(height);
        sb.append(" pixels; ");
      }
      int type = imp.getType();
      int stackSize = imp.getStackSize();
      if (db.manager != null) stackSize = db.manager.getSize();
      int size = (width * height * stackSize) / 1048576;
      switch (type) {
        case ImagePlus.GRAY8:
          sb.append("8-bit grayscale");
          break;
        case ImagePlus.GRAY16:
          sb.append("16-bit grayscale");
          size *= 2;
          break;
        case ImagePlus.GRAY32:
          sb.append("32-bit grayscale");
          size *= 4;
          break;
        case ImagePlus.COLOR_256:
          sb.append("8-bit color");
          break;
        case ImagePlus.COLOR_RGB:
          sb.append("RGB");
          size *= 4;
          break;
      }
      sb.append("; ");
      sb.append(size);
      sb.append("M");

      Insets insets = super.getInsets();
      g.drawString(sb.toString(), 5, insets.top + textGap);
    }
  }

  /** Sets the frames per second, and adjust the timer accordingly. */
  public void setFps(int value) {
    fps = value;
    if (animationTimer != null) animationTimer.setDelay(1000 / fps);
  }

  // -- Component methods --

  public void paint(Graphics g) { drawInfo(g);}

  // -- ActionListener methods --

  public synchronized void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    String cmd = e.getActionCommand();
    if (cmd == null) cmd = ""; // prevent NullPointer
    if (cmd.equals("xml")) {
      String[] args = {db.id};
      //MetadataNotebook metaNote = new MetadataNotebook(args, false, false);
      try {
        ReflectedUniverse r = new ReflectedUniverse();
        r.exec("import loci.ome.notebook.MetadataNotebook");
        r.setVar("args", args);
        r.setVar("false", false);
        r.exec("new MetadataNotebook(args, false, false)");
      }
      catch (ReflectException exc) {
        JOptionPane.showMessageDialog(this,
          "Sorry, there has been an error creating the metadata editor.",
          "LOCI 4D Data Browser", JOptionPane.ERROR_MESSAGE);
        LociDataBrowser.exceptionMessage(exc);
      }
    }
    else if (cmd.equals("options")) {
      if (ow == null) ow = new OptionsWindow(db.hasZ ? db.numZ : 1,
        db.hasT ? db.numT : 1, this);
      ow.setVisible(true);
    }
    else if (src instanceof Timer) {
          t = tSliceSel.getValue() + 1;
          if ((t > db.numT)) t = 1;
          tSliceSel.setValue(t);
    }
    else if (src instanceof JButton) {
      if (animate.getText().equals(ANIM_STRING)) {
        animationTimer = new Timer(1000 / fps, this);
        animationTimer.start();
        animate.setText(STOP_STRING);
      }
      else {
        animationTimer.stop();
        animationTimer = null;
        animate.setText(ANIM_STRING);
      }
    }
  }

  // -- AdjustmentListener methods --

  public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
    if(update) {
      JScrollBar src = (JScrollBar) adjustmentEvent.getSource();

      z = zSliceSel.getValue();
      t = tSliceSel.getValue();

      if (!src.getValueIsAdjusting() || db.manager == null) showSlice(z, t, c);
      else showTempSlice(z,t,c);
    }
  }

  // -- ItemListener methods --

  public synchronized void itemStateChanged(ItemEvent e) {
    if(update) {
      JCheckBox channels = (JCheckBox) e.getSource();

      z = zSliceSel.getValue();
      t = tSliceSel.getValue();
      c = channels.isSelected() ? 2 : 1;

      showSlice(z, t, c);
    }
  }

  // -- ChangeListener methods --

  public void stateChanged(ChangeEvent e) {
    if(update) {
      if( (JSpinner) e.getSource() == channelSpin) {
        c = ((Integer) channelSpin.getValue()).intValue();
        z = zSliceSel.getValue();
        t = tSliceSel.getValue();
        showSlice(z, t, c);
      }
    }
  }

  // -- KeyListener methods --

  public void keyPressed(KeyEvent e) {
    int code = e.getKeyCode();
    if (code == KeyEvent.VK_UP) { // previous slice
      JScrollBar bar = zSliceSel;
      int val = bar.getValue(), min = bar.getMinimum();
      if (val > min) bar.setValue(val - 1);
    }
    else if (code == KeyEvent.VK_DOWN) { // next slice
      JScrollBar bar = zSliceSel;
      int val = bar.getValue(), max = bar.getMaximum();
      if (val < max) bar.setValue(val + 1);
    }
    else if (code == KeyEvent.VK_LEFT) { // previous time step
      JScrollBar bar = tSliceSel;
      int val = bar.getValue(), min = bar.getMinimum();
      if (val > min) bar.setValue(val - 1);
    }
    else if (code == KeyEvent.VK_RIGHT) { // next time step
      JScrollBar bar = tSliceSel;
      int val = bar.getValue(), max = bar.getMaximum();
      if (val < max) bar.setValue(val + 1);
    }
  }

  public void keyReleased(KeyEvent e) { }

  public void keyTyped(KeyEvent e) { }

  // -- WindowListener methods --

  public void windowClosed(WindowEvent e) {
    if (animationTimer != null) animationTimer.stop();
    super.windowClosed(e);
  }

}
