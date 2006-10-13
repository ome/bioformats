//
// OptionsWindow.java
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
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.plugins.browser;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/** Window displaying custom virtualization options. */
public class OptionsWindow extends JFrame implements
  ActionListener, ChangeListener
{
  // -- Fields --

  /**Constant dlu size for indents in GUI*/
  private static final String TAB = "7dlu";

  /**ComboBoxes for Custom Axes*/
  protected JComboBox zBox, tBox;

  /** FPS spinner */
  private JSpinner fps;

  /** Parent window. */
  private CustomWindow cw;

  /** Button to close the window. */
  private JButton close;

  // -- Constructor --
  public OptionsWindow(int numZ, int numT, CustomWindow c) {
    super("LOCI Data Browser - Options");
    setBackground(Color.gray);

    cw = c;

    Border etchB = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

    // add Display Pane

    JPanel disPane = new JPanel();
    TitledBorder disB = BorderFactory.createTitledBorder(etchB,
      "Display Options");

    disPane.setBorder(disB);

    JLabel custLab = new JLabel("\u00B7" + "Custom Axes");
    JLabel fileLab = new JLabel("Filename:");
    JLabel zLab = new JLabel("Z axis:");
    zLab.setForeground(Color.red);
    JLabel tLab = new JLabel("Time:");
    tLab.setForeground(Color.blue);

    JPanel filePane = new JPanel();

    zBox = new JComboBox();
    zBox.setForeground(Color.red);
    tBox = new JComboBox();
    tBox.setForeground(Color.blue);

    zBox.addItem(c.db.hasZ ?
      "\"z:\" <1-" + (c.db.numZ + 1) + ">" : "\"z:\" (no range)");
    zBox.addItem(c.db.hasT ?
      "\"t:\" <1-" + (c.db.numT + 1) + ">" : "\"t:\" (no range)");
    zBox.addItem(c.db.hasC ?
      "\"c:\" <1-" + (c.db.numC + 1) + ">" : "\"c:\" (no range)");
    zBox.setSelectedIndex(c.zMap);
    zBox.addActionListener(c);
    zBox.setActionCommand("mappingZ");

    tBox.addItem(c.db.hasZ ?
      "\"z:\" <1-" + (c.db.numZ + 1) + ">" : "\"z:\" (no range)");
    tBox.addItem(c.db.hasT ?
      "\"t:\" <1-" + (c.db.numT + 1) + ">" : "\"t:\" (no range)");
    tBox.addItem(c.db.hasC ?
      "\"c:\" <1-" + (c.db.numC + 1) + ">" : "\"c:\" (no range)");
    tBox.setSelectedIndex(c.tMap);
    tBox.addActionListener(c);
    tBox.setActionCommand("mappingT");

    FormLayout layout = new FormLayout(
        TAB + ",pref," + TAB + ",pref:grow," + TAB,
        "pref,pref,pref,pref");
    disPane.setLayout(layout);
    CellConstraints cc = new CellConstraints();

    disPane.add(custLab,cc.xyw(1,1,5));
    disPane.add(fileLab,cc.xy(2,2));
    disPane.add(filePane,cc.xy(4,2));
    disPane.add(zLab,cc.xy(2,3));
    disPane.add(zBox,cc.xy(4,3));
    disPane.add(tLab,cc.xy(2,4));
    disPane.add(tBox,cc.xy(4,4));

    //set up animation options pane

    JPanel aniPane = new JPanel();
    TitledBorder aniB = BorderFactory.createTitledBorder(
      etchB, "Animation Options");
    aniPane.setBorder(aniB);

    JLabel fpsLab = new JLabel("Frames per second:");

    SpinnerNumberModel model = new SpinnerNumberModel(10, 1, 99, 1);
    fps = new JSpinner(model);
    fps.addChangeListener(this);

    FormLayout layout2 = new FormLayout(
        TAB + ",pref," + TAB + ",pref:grow," + TAB,
        "pref");
    aniPane.setLayout(layout2);
    CellConstraints cc2 = new CellConstraints();

    aniPane.add(fpsLab,cc2.xy(2,1));
    aniPane.add(fps,cc2.xy(4,1));
    
    JPanel cachePane = new JPanel();
    TitledBorder cacheB = BorderFactory.createTitledBorder(
      etchB, "CacheManager Options");
    cachePane.setBorder(cacheB);
    
    JLabel typeL = new JLabel("\u00B7" + "Cache Type");
    JLabel axesL = new JLabel("Axes to Cache:");
    JLabel modeL = new JLabel("Cache Mode:");
    JLabel stratL = new JLabel("Loading Strategy:");
    JLabel sizeL = new JLabel("\u00B7" + "Slices to Store");
    JLabel forL = new JLabel("Forward");
    JLabel backL = new JLabel("Backward");
    JLabel zL = new JLabel("Z:");
    JLabel tL = new JLabel("T:");
    JLabel cL = new JLabel("C:");
    JLabel priorL = new JLabel("\u00B7" + "Axis Priority");
    JLabel topL = new JLabel("Top:");
    JLabel midL = new JLabel("Mid:");
    JLabel lowL = new JLabel("Low:");
    
    JCheckBox zCheck = new JCheckBox("Z");
    JCheckBox tCheck = new JCheckBox("T");
    JCheckBox cCheck = new JCheckBox("C");
    JPanel checkPanel = new JPanel(new GridLayout(1,3));
    checkPanel.add(zCheck);
    checkPanel.add(tCheck);
    checkPanel.add(cCheck);
    
    String[] modes = {"Crosshair", "Rectangle", "Cross/Rect"};
    JComboBox modeBox = new JComboBox(modes);
    String[] strats = {"Forward","Surround"};
    JComboBox stratBox = new JComboBox(strats);
    String[] axes = {"Z","T","C"};
    JComboBox topBox = new JComboBox(axes);
    JComboBox midBox = new JComboBox(axes);
    JComboBox lowBox = new JComboBox(axes);
    
    SpinnerNumberModel zFMod = new SpinnerNumberModel(0,0,9999,1);
    JSpinner zFSpin = new JSpinner(zFMod);
    SpinnerNumberModel zBMod = new SpinnerNumberModel(0,0,9999,1);
    JSpinner zBSpin = new JSpinner(zBMod);
    SpinnerNumberModel tFMod = new SpinnerNumberModel(20,0,9999,1);
    JSpinner tFSpin = new JSpinner(tFMod);
    SpinnerNumberModel tBMod = new SpinnerNumberModel(0,0,9999,1);
    JSpinner tBSpin = new JSpinner(tBMod);
    SpinnerNumberModel cFMod = new SpinnerNumberModel(0,0,9999,1);
    JSpinner cFSpin = new JSpinner(cFMod);
    SpinnerNumberModel cBMod = new SpinnerNumberModel(0,0,9999,1);
    JSpinner cBSpin = new JSpinner(cBMod);
    
    JButton resetBtn = new JButton("Reset to Default");
    
    FormLayout layout3 = new FormLayout(
      TAB + ",pref," + TAB + ",pref:grow," + TAB + ",pref:grow," + TAB,
      "pref,pref,pref,pref," + TAB + ",pref,pref,pref,pref,pref,"
      + TAB + ",pref,pref,pref,pref," + TAB + ",pref");
    cachePane.setLayout(layout3);
    CellConstraints cc3 = new CellConstraints();
    
    cachePane.add(typeL,cc3.xyw(1,1,7));
    cachePane.add(axesL,cc3.xyw(2,2,3));
    cachePane.add(checkPanel,cc3.xy(6,2));
    cachePane.add(modeL,cc3.xyw(2,3,3));
    cachePane.add(modeBox,cc3.xy(6,3));
    cachePane.add(stratL,cc3.xyw(2,4,3));
    cachePane.add(stratBox,cc3.xy(6,4));
    cachePane.add(sizeL,cc3.xyw(1,6,7));
    cachePane.add(forL,cc3.xy(4,7));
    cachePane.add(backL,cc3.xy(6,7));
    cachePane.add(zL,cc3.xy(2,8));
    cachePane.add(zFSpin,cc3.xy(4,8));
    cachePane.add(zBSpin,cc3.xy(6,8));
    cachePane.add(tL,cc3.xy(2,9));
    cachePane.add(tFSpin,cc3.xy(4,9));
    cachePane.add(tBSpin,cc3.xy(6,9));
    cachePane.add(cL,cc3.xy(2,10));
    cachePane.add(cFSpin,cc3.xy(4,10));
    cachePane.add(cBSpin,cc3.xy(6,10));
    cachePane.add(priorL,cc3.xyw(1,12,7));
    cachePane.add(topL,cc3.xy(2,13));
    cachePane.add(topBox,cc3.xy(4,13));
    cachePane.add(midL,cc3.xy(2,14));
    cachePane.add(midBox,cc3.xy(4,14));
    cachePane.add(lowL,cc3.xy(2,15));
    cachePane.add(lowBox,cc3.xy(4,15));
    cachePane.add(resetBtn,cc3.xyw(2,17,5,"right,center"));

    //configure/layout content pane
    FormLayout lastLayout = new FormLayout(
      "pref:grow",
      "pref,pref,pref");
    setLayout(lastLayout);
    CellConstraints ccs = new CellConstraints();
    
    add(aniPane,ccs.xy(1,1));
    add(cachePane,ccs.xy(1,2));
    add(disPane,ccs.xy(1,3));
    

    //useful frame method that handles closing of window
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    //put frame in the right place, with the right size, and make visible
    setLocation(100, 100);
//    ((JComponent) getContentPane()).setPreferredSize(new Dimension(300,500));
    pack();
    setVisible(true);
  }

  // -- ActionListener API methods --

  public void actionPerformed(ActionEvent e) {
    dispose();
  }

  // -- ChangeListener API methods --

  public void stateChanged(ChangeEvent e) {
    if (e.getSource() == fps) {
      // the frames per second changed
      cw.setFps(((Integer) fps.getValue()).intValue());
    }
  }
}
