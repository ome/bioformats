//
// CustomWindow.java
//

// bug(?): possible race condition between showslice and setindices

package loci.browser;

import ij.*;
import ij.gui.*;
import ij.io.FileInfo;
import ij.measure.Calibration;
import java.io.File;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import loci.ome.viewer.MetadataPane;

public class CustomWindow extends ImageWindow implements ActionListener,
  AdjustmentListener, ChangeListener, ItemListener, KeyListener
{

  // -- Constants --

  private static final String Z_STRING = "z-depth";
  private static final String T_STRING = "time";

  private static final String ANIM_STRING = "Animate";
  private static final String STOP_STRING = "Stop";

  // -- Fields - state --

  private LociDataBrowser db;
  private String zString = Z_STRING;
  private String tString = T_STRING;
  private int fps = 10;
  private int z = 1, t = 1, c = 1;
  private int oldZ, oldT; // for virtual stack
  private boolean customVirtualization = false;
  private int z1, z2, t1, t2;

  // -- Fields - widgets --

  private JLabel zLabel, tLabel, waitLabel;
  private JScrollBar zSliceSel, tSliceSel;
  private JLabel fpsLabel;
  private JSpinner frameRate;
  private JButton xml;
  private Timer animationTimer;
  private JButton animate;
  private ImageStack stack;
  private JProgressBar progressBar;
  private Thread thread;

  // -- Constructor --

  /** CustomWindow constructors, initialisation */
  public CustomWindow(LociDataBrowser db, ImagePlus imp, ImageCanvas canvas) {
    super(imp, canvas);
    this.db = db;
    String title = imp.getTitle();
    this.setTitle(title.substring(title.lastIndexOf(File.separatorChar)+1));

    // create panel for image canvas
    Panel imagePane = new Panel() {
      public void paint(Graphics g) {
        // paint bounding box here instead of in ImageWindow directly
        Point loc = ic.getLocation();
        Dimension csize = ic.getSize();
        g.drawRect(loc.x - 1, loc.y - 1, csize.width + 1, csize.height + 1);
      }
    };

    imagePane.setLayout(getLayout()); // ImageLayout
    imagePane.setBackground(Color.white);

    // redo layout for master window
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    remove(ic);
    add(Box.createVerticalStrut(2)); // leave extra room for text on top
    add(imagePane);
    imagePane.add(ic);

    // custom panel to house widgets
    Panel bottom = new Panel();
    bottom.setBackground(Color.white);
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    bottom.setLayout(gridbag);

    // Z scroll bar label
    zLabel = new JLabel(zString);
    zLabel.setHorizontalTextPosition(JLabel.LEFT);
    if (!db.hasZ) zLabel.setEnabled(false);

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0.0;
    gbc.ipadx = 30;
    gbc.anchor = GridBagConstraints.LINE_START;
    gridbag.setConstraints(zLabel, gbc);
    bottom.add(zLabel);

    // T scroll bar label
    tLabel = new JLabel(tString);
    tLabel.setHorizontalTextPosition(JLabel.LEFT);
    if (!db.hasT) tLabel.setEnabled(false);

    gbc.gridy = 2;
    gridbag.setConstraints(tLabel, gbc);
    bottom.add(tLabel);

//     waitLabel = new JLabel("  ");
//     waitLabel.setHorizontalTextPosition(JLabel.LEFT);
//     gbc.gridx = 0;
//     gbc.gridy = 4;
//     gbc.gridwidth = 3;
//     gbc.ipadx = 60;
//     gridbag.setConstraints(waitLabel, gbc);
//     bottom.add(waitLabel);

    gbc.ipadx = 30;

    // Z scroll bar
    zSliceSel = new JScrollBar(JScrollBar.HORIZONTAL,
      1, 1, 1, db.hasZ ? db.numZ + 1 : 2);
    if (!db.hasZ) zSliceSel.setEnabled(false);
    zSliceSel.addAdjustmentListener(this);
    zSliceSel.setUnitIncrement(1);
    zSliceSel.setBlockIncrement(5);

    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 5;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    gridbag.setConstraints(zSliceSel, gbc);
    bottom.add(zSliceSel);

    // T scroll bar
    tSliceSel = new JScrollBar(JScrollBar.HORIZONTAL,
      1, 1, 1, db.hasT ? db.numT + 1 : 2);
    if (!db.hasT) tSliceSel.setEnabled(false);
    tSliceSel.addAdjustmentListener(this);
    tSliceSel.setUnitIncrement(1);
    tSliceSel.setBlockIncrement(5);

    gbc.gridy = 2;
    gridbag.setConstraints(tSliceSel, gbc);
    bottom.add(tSliceSel);

    gbc.gridx = 6;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0.0;
    gbc.insets = new Insets(0, 5, 0, 0);

    if (db.numC > 2) {
      // C spinner
      SpinnerModel model = new SpinnerNumberModel(1, 1, db.numC, 1);
      JSpinner channels = new JSpinner(model);
      if (!db.hasC) channels.setEnabled(false);
      channels.addChangeListener(this);

      gbc.gridwidth = 1;
      gridbag.setConstraints(channels, gbc);
      bottom.add(channels);

      // C label
      JLabel cLabel = new JLabel("channel");
      if (!db.hasC) cLabel.setEnabled(false);

      gbc.gridx = 7;
      gbc.weightx = 0.0;
      gridbag.setConstraints(cLabel, gbc);
      bottom.add(cLabel);
    }
    else {
      // C checkbox
      JCheckBox channels = new JCheckBox("Transmitted");
      if (!db.hasC) channels.setEnabled(false);
      channels.addItemListener(this);
      channels.setBackground(Color.white);

      gbc.gridwidth = 2; // end row
      gbc.anchor = GridBagConstraints.LINE_END;
      gridbag.setConstraints(channels, gbc);
      bottom.add(channels);
    }

    // animate button
    animate = new JButton(ANIM_STRING);
    if (!db.hasT) animate.setEnabled(false);
    animate.addActionListener(this);

    gbc.gridx = 6;
    gbc.gridy = 3;
    gbc.gridwidth = GridBagConstraints.REMAINDER; // end row
    gbc.gridheight = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0.0;
    gbc.insets = new Insets(3, 5, 3, 0);
    gridbag.setConstraints(animate, gbc);
    bottom.add(animate);

    // FPS label
    fpsLabel = new JLabel("fps");
    if (!db.hasT) fpsLabel.setEnabled(false);

    gbc.gridx = 7;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.REMAINDER;
    gbc.weightx = 0.0;
    gridbag.setConstraints(fpsLabel, gbc);
    bottom.add(fpsLabel);

    // FPS spinner
    SpinnerModel model = new SpinnerNumberModel(10, 1, 99, 1);
    frameRate = new JSpinner(model);
    if (!db.hasT) frameRate.setEnabled(false);
    frameRate.addChangeListener(this);

    gbc.gridx = 6;
    gbc.gridy = 2;
    gbc.ipadx = 20;
    gridbag.setConstraints(frameRate, gbc);
    bottom.add(frameRate);

    // OME-XML button
    boolean canDoXML = true;
    try {
      // disable XML button if ome-java library is not installed
      Class.forName("org.openmicroscopy.xml.OMENode");
    }
    catch (Throwable e) { canDoXML = false; }
    if (canDoXML) {
      xml = new JButton("Metadata");
      xml.addActionListener(this);
      xml.setActionCommand("xml");
      FileInfo fi = imp.getOriginalFileInfo();
      String description = fi == null ? null : fi.description;
      if (description == null || description.length() < 5 ||
        !description.substring(0, 5).toLowerCase().equals("<?xml"))
      {
        xml.setEnabled(false);
      }

      gbc.gridx = 5;
      gbc.gridy = 3;
      gbc.insets = new Insets(3, 30, 3, 5);
      gridbag.setConstraints(xml, gbc);
      bottom.add(xml);
    }

    // swap axes button
    JButton swapAxes= new JButton("Swap Axes");
    if (!db.hasZ || !db.hasT) swapAxes.setEnabled(false);
    swapAxes.addActionListener(this);
    swapAxes.setActionCommand("swap");


    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    gbc.insets = new Insets(0, 0, 0, 0);
    gridbag.setConstraints(swapAxes, gbc);
    bottom.add(swapAxes);

    // options button
    JButton options = new JButton("Options...");
    options.addActionListener(this);
    options.setActionCommand("options");

    gbc.gridx = 6;
    gbc.gridy = 4;
    gridbag.setConstraints(options, gbc);
    bottom.add(options);


    progressBar = new JProgressBar();
    progressBar.setStringPainted(true);
    progressBar.setString(" ");
    progressBar.setValue(0);
    gbc.gridx = 1;
    gbc.gridy = 4;
    gbc.gridwidth = 1;
    gridbag.setConstraints(progressBar, gbc);
    bottom.add(progressBar);

    // create enclosing JPanel (for 5-pixel border)
    JPanel pane = new JPanel() {
      public Dimension getMaximumSize() {
        Dimension max = super.getMaximumSize();
        Dimension pref = getPreferredSize();
        return new Dimension(max.width, pref.height);
      }
    };
    pane.setLayout(new BorderLayout());
    pane.setBackground(Color.white);
    pane.setBorder(new EmptyBorder(5, 5, 5, 5));

    pane.add(bottom, BorderLayout.CENTER);
    add(pane);

    // repack to take extra panel into account
    c = db.numC == 1 ? 2 : 1;

    pack();

    showSlice(z, t, c);

    // listen for arrow key presses
    addKeyListener(this);
    ic.addKeyListener(this);
  }

  // -- CustomWindow methods --

  /** selects and shows slice defined by z, t and c */
  public synchronized void showSlice(int z, int t, int c) {
    int index = db.getIndex(z - 1, t - 1, c - 1);
    if (LociDataBrowser.DEBUG) {
      db.log("showSlice: index=" + index +
        "; z=" + z + "; t=" + t + "; c=" + c);
    }
    showSlice(index);
  }

  /** selects and shows slice defined by index */
  public void showSlice(int index) {
    if (index >= 1 && index <= imp.getStackSize()) {
      synchronized (imp) {
        imp.setSlice(index);
      }
      imp.updateAndDraw();
    }
    else if (LociDataBrowser.DEBUG) {
      db.log("invalid index: " + index + " (size = " + imp.getStackSize() +
        "; zSliceSel = " + zSliceSel.getValue() +
        "; tSliceSel = " + tSliceSel.getValue() + ")");
    }
  }

  // -- ImageWindow methods --

  /** adds 3rd and 4th dimension slice position */
  public void drawInfo(Graphics g) {
    if (db == null) return;
    int zVal = zSliceSel == null ? 1 : zSliceSel.getValue();
    int tVal = tSliceSel == null ? 1 : tSliceSel.getValue();

    int textGap = 0;

    int nSlices = imp.getStackSize();

    //    ImageStack stack = imp.getStack();
    int currentSlice = imp.getCurrentSlice();

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

  // -- Component methods --

  public void paint(Graphics g) { drawInfo(g); }

  // -- ActionListener methods --

  public synchronized void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    String cmd = e.getActionCommand();
    if ("xml".equals(cmd)) {
      int id = imp.getID();
      FileInfo fi = imp.getOriginalFileInfo();
      String description = fi == null ? null : fi.description;

      // HACK - if ImageDescription does not end with a null character
      // (older versions of ImageJ truncate the final character)
      if (description.endsWith("</OME")) description += ">";

      // pop up a new metadata viewer dialog
      JFrame meta = new JFrame("Metadata - " + getTitle());
      MetadataPane metaPane = new MetadataPane();
      meta.setContentPane(metaPane);
      metaPane.setOMEXML(description);
      meta.pack();
      GUI.center(meta);
      meta.setVisible(true);
    }
    else if ("swap".equals(cmd)) {
      // swap labels
      String tmp2 = zString;
      zString = tString;
      tString = tmp2;
      zLabel.setText(zString);
      tLabel.setText(tString);
      ImageStack is = imp.getStack();
      if (is instanceof VirtualStack) setIndices();

      // update buttons
      boolean swapped = zString.equals(T_STRING);
      boolean anim = (!swapped && db.hasT) || (swapped && db.hasZ);
      animate.setEnabled(anim);
      fpsLabel.setEnabled(anim);
      frameRate.setEnabled(anim);
      repaint(); // redraw info string
    }
    else if ("options".equals(cmd)) {
      // pops up options menu
      OptionsWindow ow = new OptionsWindow(zSliceSel.getMaximum(),
        tSliceSel.getMaximum());
      ow.popup(this,z,t);
      setIndices(ow.getVirtualBounds());
      customVirtualization = ow.isCustomVirtualization();
    }
    else if (src instanceof Timer) {
      boolean swapped = zString.equals(T_STRING);
      if (swapped) {
        z = zSliceSel.getValue() + 1;
        if (customVirtualization) {
          if (z > z2) z = z1;
        }
        else if (z > db.numZ) z = 1;
        zSliceSel.setValue(z);
      }
      else {
        t = tSliceSel.getValue() + 1;
        if (customVirtualization) {
          if (t > t2) t = t1;
        }
        else if (t > db.numT) t = 1;
        tSliceSel.setValue(t);
      }
      showSlice(z, t, c);
    }
    else if (src instanceof JButton) {
      if (animate.getText().equals(ANIM_STRING)) {
        animationTimer = new Timer(1000 / fps, this);
        animationTimer.start();
        animate.setText(STOP_STRING);
        if (db.virtual && !customVirtualization) {
          synchronized(imp) { setIndices(); }
        }
      }
      else {
        animationTimer.stop();
        animationTimer = null;
        animate.setText(ANIM_STRING);
      }
    }
  }

  /**
   * Initialize the virtual stack, using the argument as its boundaries.
   * The argument should be in the form of (z1, z2, t1, t2).
   */
  public synchronized void setIndices(int[] idx) {
    // log usage
    if (LociDataBrowser.DEBUG) {
      System.err.println("Calling setIndices(int[])");
      System.err.println("z: "+idx[0]+" to "+idx[1]);
      System.err.println("t: "+idx[2]+" to "+idx[3]);
    }
    customVirtualization = true;
    ImageStack is = imp.getStack();
    int[] indices = new int[(idx[1]-idx[0]+1)*(idx[3]-idx[2]+1)];
    z1 = idx[0]; z2 = idx[1]; t1 = idx[2]; t2 = idx[3];
    int k=0;
    if (is instanceof VirtualStack) {
      for (int i=idx[0]; i<=idx[1]; i++) {
        for (int j=idx[2]; j<=idx[3]; j++) {
          indices[k++] = db.getIndex(i-1,j-1,c-1);
        }
      }
    }
    ((VirtualStack) is).setIndices(indices,progressBar);
  }

  public synchronized void setIndices() {
    customVirtualization = false;
    ImageStack is = imp.getStack();
    if (is instanceof VirtualStack) {
      boolean swapped = zString.equals(T_STRING);
      if (swapped) { // animate top scrolling bar
        int[] indices = new int[zSliceSel.getMaximum()-1];
        for (int k=0; k<indices.length; k++) {
          indices[k] = db.getIndex(k,t-1,c-1);
        }
        if (LociDataBrowser.DEBUG) {
          System.err.println("Indices:");
          for (int k=0; k<indices.length; k++)
          System.err.print(indices[k]+" ");
          System.err.println();
        }
        ((VirtualStack) is).setIndices(indices,progressBar);
      }
      else {
        int[] indices = new int[tSliceSel.getMaximum()-1];
        for (int k=0; k<indices.length; k++) {
          indices[k] = db.getIndex(z-1,k,c-1);
        }
        ((VirtualStack) is).setIndices(indices,progressBar);
      }
    }
  }

  // -- AdjustmentListener methods --

  public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
    Object src = adjustmentEvent.getSource();
    if (src == zSliceSel) {
      z = zSliceSel.getValue();
      if (animate.getText().equals(STOP_STRING) && zString.equals(Z_STRING)) {
        setIndices();
      }
    }
    else if (src == tSliceSel) {
      t = tSliceSel.getValue();
      if (animate.getText().equals(STOP_STRING) && zString.equals(T_STRING)) {
        setIndices();
      }
    }
    showSlice(z, t, c);
  }

  // -- ChangeListener methods --

  public void stateChanged(ChangeEvent e) {
    Object src = e.getSource();
    if (src == frameRate) {
      fps = ((Integer) frameRate.getValue()).intValue();
      if (animationTimer != null) animationTimer.setDelay(1000 / fps);
    }
    else { // src == channels
      JSpinner channels = (JSpinner) src;
      c = ((Integer) channels.getValue()).intValue();
      showSlice(z, t, c);
    }
  }

  // -- ItemListener methods --

  public synchronized void itemStateChanged(ItemEvent e) {
    JCheckBox channels = (JCheckBox) e.getSource();
    c = channels.isSelected() ? 1 : 2;

    if (db.virtual) {setIndices();}
    showSlice(z, t, c);
  }

  // -- KeyListener methods --

  public void keyPressed(KeyEvent e) {
    int code = e.getKeyCode();
    boolean swapped = zString.equals(T_STRING);
    if (code == KeyEvent.VK_UP) { // previous slice
      JScrollBar bar = swapped ? tSliceSel : zSliceSel;
      int val = bar.getValue(), min = bar.getMinimum();
      if (val > min) bar.setValue(val - 1);
    }
    else if (code == KeyEvent.VK_DOWN) { // next slice
      JScrollBar bar = swapped ? tSliceSel : zSliceSel;
      int val = bar.getValue(), max = bar.getMaximum();
      if (val < max) bar.setValue(val + 1);
    }
    else if (code == KeyEvent.VK_LEFT) { // previous time step
      JScrollBar bar = swapped ? zSliceSel : tSliceSel;
      int val = bar.getValue(), min = bar.getMinimum();
      if (val > min) bar.setValue(val - 1);
    }
    else if (code == KeyEvent.VK_RIGHT) { // next time step
      JScrollBar bar = swapped ? zSliceSel : tSliceSel;
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
