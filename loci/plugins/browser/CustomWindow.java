//
// CustomWindow.java
//

/*
LOCI 4D Data Browser package for quick browsing of 4D datasets in ImageJ.
Copyright (C) 2005-@year@ Francis Wong, Curtis Rueden and Melissa Linkert.

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

package loci.plugins.browser;

import ij.*;
import ij.gui.*;
import ij.measure.Calibration;
import java.io.File;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import loci.formats.ReflectedUniverse;
import loci.formats.ReflectException;
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
  private OptionsWindow ow;
  private String zString = Z_STRING;
  private String tString = T_STRING;
  private int fps = 10;
  private int z = 1, t = 1, c = 1;
  private boolean customVirtualization = false;
  private int z1 = 1, z2 = 1, t1 = 1, t2 = 1;
  protected int zMap,tMap,cMap,prevZ,prevT;
  private boolean setup;

  // -- Fields - widgets --

  private JLabel zLabel, tLabel, cLabel;
  private JScrollBar zSliceSel, tSliceSel;
  private JButton xml;
  private Timer animationTimer;
  private JButton animate;
  private JButton options;
  private Panel lowPane;
  private CellConstraints cc;
  private JSpinner channelSpin;
  private JCheckBox channelBox;
  private Color textColor;

  // -- Constructor --

  /** CustomWindow constructors, initialization. */
  public CustomWindow(LociDataBrowser db, ImagePlus imp, ImageCanvas canvas) {
    super(imp, canvas);
    this.db = db;
    ow = null;
    String title = imp.getTitle();
    this.setTitle(title.substring(title.lastIndexOf(File.separatorChar) + 1));
    
    //setup which variables the sliders are set to
    zMap = 0;
    tMap = 1;
    cMap = 2;
    prevZ = 0;
    prevT = 1;
    
    setup = false;

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
    imagePane.setBackground(Color.white);

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
    catch (Throwable e) { canDoXML = false; }
    xml = null;
    if (canDoXML) {
      xml = new JButton("Metadata");
      xml.addActionListener(this);
      xml.setActionCommand("xml");
    }
    
    channelBox = new JCheckBox("Transmitted");
    
    cLabel = new JLabel("channel");
    textColor = cLabel.getForeground();
    SpinnerModel model = new SpinnerNumberModel(1, 1, db.numC, 1);
    channelSpin = new JSpinner(model);
    channelSpin.setEditor(new JSpinner.NumberEditor(channelSpin));
    
    if(db.numC > 2) channelBox.setVisible(false);
    else {
      channelSpin.setVisible(false);
      cLabel.setForeground(Color.white);
    }

    // repack to take extra panel into account
    c = db.numC;

    if (db.numC * db.numT * db.numZ == imp.getStackSize()) {
      c = db.numC;
    }
    else c = 1;
    
    //setup the layout
    lowPane = new Panel();
    FormLayout layout = new FormLayout(
        TAB + ",pref," + TAB + ",pref:grow," + TAB + ",pref," + TAB + ",pref,"
        + TAB + ",pref," + TAB + ",pref," + TAB,
        TAB + ",pref," + TAB + ",pref," + TAB + ",pref," + TAB);
    lowPane.setLayout(layout);
    lowPane.setBackground(Color.white);

    cc = new CellConstraints();
    
    lowPane.add(zLabel, cc.xy(2,2));
    lowPane.add(zSliceSel, cc.xyw(4,2,5));
    lowPane.add(channelBox, cc.xy(12,2));
    lowPane.add(tLabel, cc.xy(2,4));
    lowPane.add(tSliceSel, cc.xyw(4,4,5));
    lowPane.add(cLabel, cc.xy(10,4));
    lowPane.add(channelSpin, cc.xy(12,4));
    lowPane.add(options, cc.xy(6,6));
    if(xml != null) lowPane.add(xml, cc.xy(8,6));
    lowPane.add(animate, cc.xyw(10,6,3, "right,center"));
    
    setC(2);
    
    Panel mainPane = new Panel();
    mainPane.setBackground(Color.white);
    FormLayout layout2 = new FormLayout(
      TAB + ",pref:grow," + TAB,
      TAB + ",pref:grow," + TAB + ",pref," + TAB);
    mainPane.setLayout(layout2);
    CellConstraints cc2 = new CellConstraints();
    mainPane.add(imagePane, cc2.xy(2,2));
    mainPane.add(lowPane, cc2.xy(2,4));
    
    setLayout(new GridLayout());
    add(mainPane);  
    
    //final GUI tasks
    pack();

    showSlice(z, t, c);

    // listen for arrow key presses
    addKeyListener(this);
    ic.addKeyListener(this);
  }

  // -- CustomWindow methods --
  
  public void setC(int cMap) {
    this.cMap = cMap;
    boolean hasThis = false;
    int numThis = -1;
    int value = -1;
    switch (cMap) {
      case 0:
        hasThis = db.hasZ;
        numThis = db.numZ;
        value = z;
        break;
      case 1:
        hasThis = db.hasT;
        numThis = db.numT;
        value = t;
        break;
      case 2:
        hasThis = db.hasC;
        numThis = db.numC;
        value = c;
        break;
    }
    if (numThis > 2) {
      // C spinner
      SpinnerNumberModel snm = (SpinnerNumberModel) channelSpin.getModel();
      snm.setMaximum((Comparable) new Integer(numThis));
      snm.setValue(new Integer(value));
      channelSpin.setVisible(true);
      cLabel.setForeground(textColor);
      if (!hasThis) channelSpin.setEnabled(false);
      channelSpin.addChangeListener(this);

      // C label
      if (!hasThis) cLabel.setEnabled(false);

      channelBox.setVisible(false);
    }
    else {
      // C checkbox
      channelBox.setVisible(true);
      if (!hasThis) channelBox.setEnabled(false);
      channelBox.addItemListener(this);
      channelBox.setBackground(Color.white);
      
      channelSpin.setVisible(false);
      cLabel.setForeground(Color.white);
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
      imp.setProcessor("", db.manager.getSlice(index - 1));
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

  // -- ImageWindow methods --

  /** Adds 3rd and 4th dimension slice position. */
  public void drawInfo(Graphics g) {
    if (db == null) return;
    int zVal = zSliceSel == null ? 1 : zSliceSel.getValue();
    int tVal = tSliceSel == null ? 1 : tSliceSel.getValue();

    int textGap = 0;

    int nSlices = db.numZ * db.numT * c;
    int currentSlice = imp.getCurrentSlice();
    if (db.manager != null) currentSlice = db.manager.getSlice();

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
      switch(zMap) {
        case 0:
          sb.append(db.numZ);
          break;
        case 1:
          sb.append(db.numT);
          break;
        case 2:
          sb.append(db.numC);
          break;
      }
      sb.append("; ");
    }
    if (db.hasT) {
      sb.append(tString);
      sb.append(": ");
      sb.append(tVal);
      sb.append("/");
      switch(tMap) {
        case 0:
          sb.append(db.numZ);
          break;
        case 1:
          sb.append(db.numT);
          break;
        case 2:
          sb.append(db.numC);
          break;
      }
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

  /** Sets the Z/T values for a custom virtualization. */
  public void setVirtualization(int[] values) {
    z1 = values[0];
    z2 = values[1];
    t1 = values[2];
    t2 = values[3];
    customVirtualization = true;
  }

  /** Returns true if we are using a custom virtualization. */
  public boolean isCustom() { return customVirtualization; }

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
      String[] args = {db.filename};
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
      }
    }
    else if (cmd.equals("options")) {
      if (ow!=null) ow.dispose();
      ow = new OptionsWindow(db.hasZ ? db.numZ : 1,
        db.hasT ? db.numT : 1, this);
      ow.setVisible(true);
    }
    else if (src instanceof Timer) {      
      switch (tMap) {
        case 0:
          z = tSliceSel.getValue() + 1;
          if ((z > db.numZ)) z = 1;
          tSliceSel.setValue(z);
          break;
        case 1:
          t = tSliceSel.getValue() + 1;
          if ((t > db.numT)) t = 1;
          tSliceSel.setValue(t);
          break;
        case 2:
          c = tSliceSel.getValue() + 1;
          if ((c > db.numC)) c = 1;
          tSliceSel.setValue(c);
          break;
      }

      showSlice(z, t, c);
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
    else if (src instanceof JComboBox &&
      e.getActionCommand().startsWith("mapping"))
    {
      setup = true;
      JComboBox jcb = (JComboBox) src;
      if(e.getActionCommand().endsWith("Z")) {
        zMap = jcb.getSelectedIndex();
        switch (zMap) {
          case 0:
            zSliceSel.setMaximum(db.hasZ ? db.numZ + 1 : 2);
            zSliceSel.setValue(z);
            if (db.hasZ) zSliceSel.setEnabled(true);
            else zSliceSel.setEnabled(false);
            if(tMap == 1) setC(2);
            else setC(1);
            break;
          case 1:
            zSliceSel.setMaximum(db.hasT ? db.numT + 1 : 2);
            zSliceSel.setValue(t);
            if (db.hasT) zSliceSel.setEnabled(true);
            else zSliceSel.setEnabled(false);
            if(tMap == 0) setC(2);
            else setC(0);
            break;
          case 2:
            zSliceSel.setMaximum(db.hasC ? db.numC + 1 : 2);
            zSliceSel.setValue(c);
            if (db.hasC) zSliceSel.setEnabled(true);
            else zSliceSel.setEnabled(false);
            if(tMap == 0) setC(1);
            else setC(0);
            break;
        }
        if(zMap == tMap) ow.tBox.setSelectedIndex(prevZ);
        prevZ = zMap;
      }
      else if(e.getActionCommand().endsWith("T")) {
        tMap = jcb.getSelectedIndex();
        switch (tMap) {
          case 0:
            tSliceSel.setMaximum(db.hasZ ? db.numZ + 1 : 2);
            tSliceSel.setValue(z);
            if (db.hasZ) tSliceSel.setEnabled(true);
            else tSliceSel.setEnabled(false);
            if(zMap == 1) setC(2);
            else setC(1);
            break;
          case 1:
            tSliceSel.setMaximum(db.hasT ? db.numT + 1 : 2);
            tSliceSel.setValue(t);
            if (db.hasT) tSliceSel.setEnabled(true);
            else tSliceSel.setEnabled(false);
            if(zMap == 0) setC(2);
            else setC(0);
            break;
          case 2:
            tSliceSel.setMaximum(db.hasC ? db.numC + 1 : 2);
            tSliceSel.setValue(c);
            if (db.hasC) tSliceSel.setEnabled(true);
            else tSliceSel.setEnabled(false);
            if(zMap == 0) setC(1);
            else setC(0);
            break;
        }
        if(zMap == tMap) ow.zBox.setSelectedIndex(prevT);
        prevT = tMap;
      }
      setup = false;
    }
  }

  // -- AdjustmentListener methods --

  public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
    Object src = adjustmentEvent.getSource();
    if(!setup) {
      if (src == zSliceSel) {
        if(zMap == 0) z = zSliceSel.getValue();
        else if(zMap == 1) t = zSliceSel.getValue();
        else if(zMap == 2) c = zSliceSel.getValue();
      }
      else if (src == tSliceSel) {
        if(tMap == 0) z = tSliceSel.getValue();
        else if(tMap == 1) t = tSliceSel.getValue();
        else if(tMap == 2) c = tSliceSel.getValue();
      }
    }
    showSlice(z, t, c);
  }

  // -- ChangeListener methods --

  public void stateChanged(ChangeEvent e) {
    JSpinner channels = (JSpinner) e.getSource();
    switch(cMap) {
      case 0:
        z = ((Integer) channels.getValue()).intValue();
        break;
      case 1:
        t = ((Integer) channels.getValue()).intValue();
        break;
      case 2:
        c = ((Integer) channels.getValue()).intValue();
        break;
    }
    
    showSlice(z, t, c);
  }

  // -- ItemListener methods --

  public synchronized void itemStateChanged(ItemEvent e) {
    JCheckBox channels = (JCheckBox) e.getSource();
    switch(cMap) {
      case 0:
        z = channels.isSelected() ? 1 : 2;
        break;
      case 1:
        t = channels.isSelected() ? 1 : 2;
        break;
      case 2:
        c = channels.isSelected() ? 1 : 2;
        break;
    }

    showSlice(z, t, c);
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
