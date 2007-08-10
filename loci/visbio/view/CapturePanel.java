//
// CapturePanel.java
//

/*
VisBio application for visualization of multidimensional biological
image data. Copyright (C) 2002-@year@ Curtis Rueden and Abraham Sorber.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.visbio.view;

import com.jgoodies.forms.builder.ButtonStackBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;
import loci.visbio.VisBioFrame;
import loci.visbio.util.*;
import visad.*;

/**
 * Provides GUI controls for a display capture handler.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/view/CapturePanel.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/view/CapturePanel.java">SVN</a></dd></dl>
 */
public class CapturePanel extends JPanel implements ActionListener,
  ChangeListener, ItemListener, ListSelectionListener
{

  // -- Fields --

  /** Capture handler for this capture window. */
  protected CaptureHandler handler;

  /** Position list. */
  protected JList posList;

  /** Position list model. */
  protected DefaultListModel posListModel;

  /** Button for removing selected position. */
  protected JButton remove;

  /** Button for moving selected position upward. */
  protected JButton moveUp;

  /** Button for moving selected position downward. */
  protected JButton moveDown;

  /** Slider for adjusting movie speed. */
  protected JSlider speed;

  /** Output movie frames per second. */
  protected JSpinner fps;

  /** Check box for animation smoothness. */
  protected JCheckBox smooth;

  /** Progress bar for movie capture operation. */
  protected JProgressBar progress;

  // -- Constructor --

  /** Constructs a window for capturing display screenshots and movies. */
  public CapturePanel(CaptureHandler h) {
    super();
    handler = h;

    // positions list
    posListModel = new DefaultListModel();
    posList = new JList(posListModel);
    posList.setFixedCellWidth(120);
    posList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    posList.addListSelectionListener(this);
    posList.setToolTipText("List of captured display positions");

    // add button
    JButton add = new JButton("Add");
    add.setActionCommand("Add");
    add.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) add.setMnemonic('a');
    add.setToolTipText("Adds the current display position to the list");

    // remove button
    remove = new JButton("Remove");
    remove.setActionCommand("Remove");
    remove.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) remove.setMnemonic('r');
    remove.setToolTipText("Removes the selected position from the list");
    remove.setEnabled(false);

    // up button
    moveUp = new JButton("Up");
    moveUp.setActionCommand("Up");
    moveUp.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) moveUp.setMnemonic('u');
    moveUp.setToolTipText("Moves the selected position up in the list");
    moveUp.setEnabled(false);

    // down button
    moveDown = new JButton("Down");
    moveDown.setActionCommand("Down");
    moveDown.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) moveDown.setMnemonic('d');
    moveDown.setToolTipText("Moves the selected position down in the list");
    moveDown.setEnabled(false);

    // snapshot button
    JButton snapshot = new JButton("Snapshot");
    snapshot.setActionCommand("Snapshot");
    snapshot.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) snapshot.setMnemonic('n');
    snapshot.setToolTipText("Saves display snapshot to an image file");

    // send to ImageJ button
    JButton sendToImageJ = new JButton("Send to ImageJ");
    sendToImageJ.setActionCommand("SendImageJ");
    sendToImageJ.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) sendToImageJ.setMnemonic('j');
    sendToImageJ.setToolTipText("Sends display snapshot to ImageJ program");

    // speed slider
    speed = new JSlider(0, 16, 8);
    speed.setAlignmentY(JSlider.TOP_ALIGNMENT);
    speed.setMajorTickSpacing(4);
    speed.setMinorTickSpacing(1);
    Hashtable speedHash = new Hashtable();
    speedHash.put(new Integer(0), new JLabel("4 (slow)"));
    speedHash.put(new Integer(4), new JLabel("2"));
    speedHash.put(new Integer(8), new JLabel("1"));
    speedHash.put(new Integer(12), new JLabel("1/2"));
    speedHash.put(new Integer(16), new JLabel("1/4 (fast)"));
    speed.setLabelTable(speedHash);
    speed.setSnapToTicks(true);
    speed.setPaintTicks(true);
    speed.setPaintLabels(true);
    speed.addChangeListener(this);
    speed.setToolTipText("Adjusts seconds per transition");

    // frames per second spinner
    fps = new JSpinner(new SpinnerNumberModel(10, 1, 600, 1));
    fps.addChangeListener(this);
    fps.setToolTipText("Adjusts output movie's frames per second");

    // smoothness checkbox
    smooth = new JCheckBox(
      "Emphasize transition at each display position", true);
    smooth.addItemListener(this);
    if (!LAFUtil.isMacLookAndFeel()) smooth.setMnemonic('e');
    smooth.setToolTipText("Use smooth sine function transitions");

    // record button
    JButton record = new JButton("Record >");
    record.setActionCommand("Record");
    record.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) record.setMnemonic('r');
    record.setToolTipText(
      "Records a movie of transitions between display positions");

    // progress bar
    progress = new JProgressBar(0, 100);
    progress.setString("");
    progress.setStringPainted(true);
    progress.setToolTipText("Displays movie recording progress");

    // lay out buttons
    ButtonStackBuilder bsb = new ButtonStackBuilder();
    bsb.addGridded(add);
    bsb.addRelatedGap();
    bsb.addGridded(remove);
    bsb.addUnrelatedGap();
    bsb.addGridded(moveUp);
    bsb.addRelatedGap();
    bsb.addGridded(moveDown);
    JPanel buttons = bsb.getPanel();

    // lay out position list
    PanelBuilder positionList = new PanelBuilder(new FormLayout(
      "default:grow, 3dlu, pref", "fill:pref:grow"));
    CellConstraints cc = new CellConstraints();
    JScrollPane posScroll = new JScrollPane(posList);
    SwingUtil.configureScrollPane(posScroll);
    positionList.add(posScroll, cc.xy(1, 1));
    positionList.add(buttons, cc.xy(3, 1));

    // lay out transition speed slider
    PanelBuilder transitionSpeed = new PanelBuilder(new FormLayout(
      "pref, 3dlu, pref, 3dlu, pref:grow, 3dlu, pref", "pref"));
    JLabel speedLabel =
      transitionSpeed.addLabel("&Seconds per transition", cc.xy(1, 1));
    speedLabel.setLabelFor(speed);
    transitionSpeed.add(speed, cc.xy(5, 1));

    // lay out movie recording button and progress bar
    PanelBuilder movieRecord = new PanelBuilder(new FormLayout(
      "pref, 3dlu, pref:grow", "pref"));
    movieRecord.add(record, cc.xy(1, 1));
    movieRecord.add(progress, cc.xy(3, 1));

    // lay out components
    JPanel pane = FormsUtil.makeColumn(new Object[] {
      "Display positions", positionList.getPanel(),
      "Screenshots", FormsUtil.makeRow(snapshot, sendToImageJ),
      "Movies", FormsUtil.makeRow("&Frames per second", fps),
      transitionSpeed.getPanel(), smooth, movieRecord.getPanel()},
      "pref:grow", false);
    setLayout(new BorderLayout());
    add(pane);
  }

  // -- CapturePanel API methods --

  /** Sets the progress bar's value. */
  public void setProgressValue(int value) { progress.setValue(value); }

  /** Sets the progress bar's message. */
  public void setProgressMessage(String msg) { progress.setString(msg); }

  /** Sets positions on the list. */
  public void setPositions(Vector v) {
    posListModel.removeAllElements();
    if (v == null) return;
    int size = v.size();
    for (int i=0; i<size; i++) posListModel.addElement(v.elementAt(i));
  }

  /** Sets movie speed. */
  public void setSpeed(int speed) {
    if (speed != getSpeed()) this.speed.setValue(speed);
  }

  /** Sets movie frames per second. */
  public void setFPS(int fps) {
    if (fps != getFPS()) {
      SpinnerNumberModel fpsModel = (SpinnerNumberModel) this.fps.getModel();
      fpsModel.setValue(new Integer(fps));
    }
  }

  /** Sets whether transitions use a smoothing sine function. */
  public void setSmooth(boolean smooth) {
    if (smooth != isSmooth()) this.smooth.setSelected(smooth);
  }

  /** Gets positions on the list. */
  public Vector getPositions() {
    int size = posListModel.size();
    Vector v = new Vector(size);
    for (int i=0; i<size; i++) v.add(posListModel.elementAt(i));
    return v;
  }

  /** Gets movie speed. */
  public int getSpeed() { return speed.getValue(); }

  /** Gets movie frames per second. */
  public int getFPS() { return ((Integer) fps.getValue()).intValue(); }

  /** Gets whether transitions use a smoothing sine function. */
  public boolean isSmooth() { return smooth.isSelected(); }

  // -- ActionListener API methods --

  /** Called when a button is pressed. */
  public void actionPerformed(ActionEvent e) {
    DisplayWindow display = handler.getWindow();
    VisBioFrame bio = display.getVisBio();

    String cmd = e.getActionCommand();
    if (cmd.equals("Add")) {
      DisplayImpl d = display.getDisplay();
      if (d == null) return;
      ProjectionControl pc = d.getProjectionControl();
      double[] matrix = pc.getMatrix();
      String nextPos = "position" + (posListModel.getSize() + 1);
      String value = (String) JOptionPane.showInputDialog(this,
        "Position name:", "Add display position",
        JOptionPane.INFORMATION_MESSAGE, null, null, nextPos);
      if (value == null) return;
      posListModel.addElement(new DisplayPosition(value, matrix));
      bio.generateEvent(bio.getManager(DisplayManager.class),
        "position addition for " + display.getName(), true);
    }
    else if (cmd.equals("Remove")) {
      int ndx = posList.getSelectedIndex();
      if (ndx >= 0) {
        posListModel.removeElementAt(ndx);
        if (posListModel.size() > ndx) posList.setSelectedIndex(ndx);
        else if (ndx > 0) posList.setSelectedIndex(ndx - 1);
      }
      bio.generateEvent(bio.getManager(DisplayManager.class),
        "position removal for " + display.getName(), true);
    }
    else if (cmd.equals("Up")) {
      int ndx = posList.getSelectedIndex();
      if (ndx >= 1) {
        Object o = posListModel.getElementAt(ndx);
        posListModel.removeElementAt(ndx);
        posListModel.insertElementAt(o, ndx - 1);
        posList.setSelectedIndex(ndx - 1);
      }
      bio.generateEvent(bio.getManager(DisplayManager.class),
        "position list modification for " + display.getName(), true);
    }
    else if (cmd.equals("Down")) {
      int ndx = posList.getSelectedIndex();
      if (ndx >= 0 && ndx < posListModel.size() - 1) {
        Object o = posListModel.getElementAt(ndx);
        posListModel.removeElementAt(ndx);
        posListModel.insertElementAt(o, ndx + 1);
        posList.setSelectedIndex(ndx + 1);
      }
      bio.generateEvent(bio.getManager(DisplayManager.class),
        "position list modification for " + display.getName(), true);
    }
    else if (cmd.equals("Snapshot")) handler.saveSnapshot();
    else if (cmd.equals("SendImageJ")) handler.sendToImageJ();
    else if (cmd.equals("Record")) {
      // build popup menu
      JPopupMenu menu = new JPopupMenu();

      JMenuItem aviMovie = new JMenuItem("AVI movie...");
      aviMovie.setMnemonic('m');
      aviMovie.setActionCommand("AviMovie");
      aviMovie.addActionListener(this);
      menu.add(aviMovie);

      JMenuItem imageSequence = new JMenuItem("Image sequence...");
      imageSequence.setMnemonic('s');
      imageSequence.setActionCommand("ImageSequence");
      imageSequence.addActionListener(this);
      menu.add(imageSequence);

      // show popup menu
      JButton source = (JButton) e.getSource();
      menu.show(source, source.getWidth(), 0);
    }
    else if (cmd.equals("AviMovie") || cmd.equals("ImageSequence")) {
      int size = posListModel.size();
      Vector matrices = new Vector(size);
      for (int i=0; i<size; i++) {
        DisplayPosition pos = (DisplayPosition) posListModel.elementAt(i);
        matrices.add(pos.getMatrix());
      }
      double secPerTrans = Math.pow(2, 2 - speed.getValue() / 4.0);
      int framesPerSec = getFPS();
      boolean sine = smooth.isSelected();
      boolean movie = cmd.equals("AviMovie");
      handler.captureMovie(matrices, secPerTrans, framesPerSec, sine, movie);
    }
  }

  // -- ChangeListener API methods --

  /** Called when slider or spinner is adjusted. */
  public void stateChanged(ChangeEvent e) {
    DisplayWindow display = handler.getWindow();
    VisBioFrame bio = display.getVisBio();

    Object src = e.getSource();
    if (src == speed) {
      if (!speed.getValueIsAdjusting()) {
        bio.generateEvent(bio.getManager(DisplayManager.class),
          "transition speed adjustment for " + display.getName(), true);
      }
    }
    else if (src == fps) {
      bio.generateEvent(bio.getManager(DisplayManager.class),
        "capture FPS adjustment for " + display.getName(), true);
    }
  }

  // -- ItemListener API methods --

  /** Called when check box is toggled. */
  public void itemStateChanged(ItemEvent e) {
    DisplayWindow display = handler.getWindow();
    VisBioFrame bio = display.getVisBio();
    bio.generateEvent(bio.getManager(DisplayManager.class),
      (smooth.isSelected() ? "en" : "dis") +
      "able transition emphasis for " + display.getName(), true);
  }

  // -- ListSelectionListener API methods --

  /** Called when the a new display position is selected. */
  public void valueChanged(ListSelectionEvent e) {
    int ndx = posList.getSelectedIndex();
    remove.setEnabled(ndx >= 0);
    moveUp.setEnabled(ndx > 0);
    moveDown.setEnabled(ndx < posListModel.getSize() - 1);
    if (ndx < 0) return;
    DisplayPosition pos = (DisplayPosition) posListModel.getElementAt(ndx);
    double[] matrix = pos.getMatrix();
    DisplayWindow display = handler.getWindow();
    VisBioFrame bio = display.getVisBio();
    DisplayImpl d = display.getDisplay();
    if (d == null) return;
    ProjectionControl pc = d.getProjectionControl();
    try { pc.setMatrix(matrix); }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
  }

}
