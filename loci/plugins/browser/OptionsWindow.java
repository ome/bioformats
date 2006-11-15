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
  ActionListener, ChangeListener, ItemListener
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

  /** The CacheManager for this instance of data browser.*/
  private CacheManager manager;

  /** CheckBoxes to indicate which axes to store.*/
  private JCheckBox zCheck,tCheck,cCheck;

  /** Spinners for slice storage.*/
  private JSpinner zFSpin,zBSpin,tFSpin,tBSpin,cFSpin,cBSpin;

  /** Combo Boxes for cache mode selection.*/
  private JComboBox modeBox,stratBox;

  /** Combo Boxes for dimensional priority selection.*/
  private JComboBox topBox,midBox,lowBox;

  /** Button to reset CacheManager to default modes. */
  private JButton resetBtn;

  /** A flag to turn off listening to gui components.*/
  private boolean update;

  /** Storage of what priority settings used to be.*/
  private int oldTop,oldMid,oldLow;

  // -- Constructor --
  public OptionsWindow(int numZ, int numT, CustomWindow c) {
    super("LOCI Data Browser - Options");
    setBackground(Color.gray);

    cw = c;

    manager = cw.db.manager;

    update = false;

    Border etchB = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

    // add Display Pane

    JPanel disPane = new JPanel();
    TitledBorder disB = BorderFactory.createTitledBorder(etchB,
      "Display Options");

    disPane.setBorder(disB);

    JLabel custLab = new JLabel("\u00B7" + "Custom Axes" + "\u00B7");
    JLabel fileLab = new JLabel("Filename:");
    JLabel zLab = new JLabel("Z axis:");
    zLab.setForeground(Color.red);
    JLabel tLab = new JLabel("Time:");
    tLab.setForeground(Color.blue);

    JPanel custPanel = new JPanel();
    custPanel.add(custLab);
    custPanel.setBackground(Color.darkGray);
    custLab.setForeground(Color.lightGray);

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

    disPane.add(custPanel,cc.xyw(1,1,5));
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

    JLabel typeL = new JLabel("\u00B7" + "Cache Type" + "\u00B7");
    JLabel axesL = new JLabel("Axes to Cache:");
    JLabel modeL = new JLabel("Cache Mode:");
    JLabel stratL = new JLabel("Loading Strategy:");
    JLabel sizeL = new JLabel("\u00B7" + "Slices to Store" + "\u00B7");
    JLabel forL = new JLabel("Forward");
    JLabel backL = new JLabel("Backward");
    JLabel zL = new JLabel("Z:");
    JLabel tL = new JLabel("T:");
    JLabel cL = new JLabel("C:");
    JLabel priorL = new JLabel("\u00B7" + "Axis Priority" + "\u00B7");
    JLabel topL = new JLabel("Top Priority:");
    JLabel midL = new JLabel("Mid Priority:");
    JLabel lowL = new JLabel("Low Priority:");

    JPanel typePanel = new JPanel();
    typePanel.add(typeL);
    typePanel.setBackground(Color.darkGray);
    typeL.setForeground(Color.lightGray);
    JPanel sizePanel = new JPanel();
    sizePanel.add(sizeL);
    sizePanel.setBackground(Color.darkGray);
    sizeL.setForeground(Color.lightGray);
    JPanel priorPanel = new JPanel();
    priorPanel.add(priorL);
    priorPanel.setBackground(Color.darkGray);
    priorL.setForeground(Color.lightGray);

    zCheck = new JCheckBox("Z");
    tCheck = new JCheckBox("T");
    tCheck.setSelected(true);
    cCheck = new JCheckBox("C");
    JPanel checkPanel = new JPanel(new GridLayout(1,3));
    checkPanel.add(zCheck);
    checkPanel.add(tCheck);
    checkPanel.add(cCheck);
    zCheck.addItemListener(this);
    tCheck.addItemListener(this);
    cCheck.addItemListener(this);

    String[] modes = {"Crosshair", "Rectangle", "Cross/Rect"};
    modeBox = new JComboBox(modes);
    String[] strats = {"Forward","Surround"};
    stratBox = new JComboBox(strats);
    String[] axes = {"Z","T","C"};
    topBox = new JComboBox(axes);
    midBox = new JComboBox(axes);
    lowBox = new JComboBox(axes);
    topBox.setSelectedIndex(1);
    midBox.setSelectedIndex(0);
    lowBox.setSelectedIndex(2);
    modeBox.addActionListener(this);
    stratBox.addActionListener(this);
    topBox.addActionListener(this);
    midBox.addActionListener(this);
    lowBox.addActionListener(this);

    SpinnerNumberModel zFMod = new SpinnerNumberModel(0,0,9999,1);
    zFSpin = new JSpinner(zFMod);
    SpinnerNumberModel zBMod = new SpinnerNumberModel(0,0,9999,1);
    zBSpin = new JSpinner(zBMod);
    SpinnerNumberModel tFMod = new SpinnerNumberModel(20,0,9999,1);
    tFSpin = new JSpinner(tFMod);
    SpinnerNumberModel tBMod = new SpinnerNumberModel(0,0,9999,1);
    tBSpin = new JSpinner(tBMod);
    SpinnerNumberModel cFMod = new SpinnerNumberModel(0,0,9999,1);
    cFSpin = new JSpinner(cFMod);
    SpinnerNumberModel cBMod = new SpinnerNumberModel(0,0,9999,1);
    cBSpin = new JSpinner(cBMod);
    zFSpin.addChangeListener(this);
    zBSpin.addChangeListener(this);
    tFSpin.addChangeListener(this);
    tBSpin.addChangeListener(this);
    cFSpin.addChangeListener(this);
    cBSpin.addChangeListener(this);

    resetBtn = new JButton("Reset CacheManager to Default");
    resetBtn.addActionListener(this);

    FormLayout layout3 = new FormLayout(
      TAB + ",pref," + TAB + ",pref:grow," + TAB + ",pref:grow," + TAB,
      "pref,pref,pref,pref," + TAB + ",pref,pref,pref,pref,pref,"
      + TAB + ",pref," + TAB + ",pref,pref,pref," + TAB + ",pref");
    cachePane.setLayout(layout3);
    CellConstraints cc3 = new CellConstraints();

    cachePane.add(typePanel,cc3.xyw(1,1,7));
    cachePane.add(axesL,cc3.xyw(2,2,3));
    cachePane.add(checkPanel,cc3.xy(6,2));
    cachePane.add(modeL,cc3.xyw(2,3,3));
    cachePane.add(modeBox,cc3.xy(6,3));
    cachePane.add(stratL,cc3.xyw(2,4,3));
    cachePane.add(stratBox,cc3.xy(6,4));
    cachePane.add(sizePanel,cc3.xyw(1,6,7));
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
    cachePane.add(priorPanel,cc3.xyw(1,12,7));
    cachePane.add(topL,cc3.xyw(2,14,3));
    cachePane.add(topBox,cc3.xy(6,14));
    cachePane.add(midL,cc3.xyw(2,15,3));
    cachePane.add(midBox,cc3.xy(6,15));
    cachePane.add(lowL,cc3.xyw(2,16,3));
    cachePane.add(lowBox,cc3.xy(6,16));
    cachePane.add(resetBtn,cc3.xyw(2,18,5,"right,center"));

    if(!cw.db.virtual) enableCache(false);

    //configure/layout content pane
    FormLayout lastLayout = new FormLayout(
      "pref:grow",
      "pref,pref,pref");
    setLayout(lastLayout);
    CellConstraints ccs = new CellConstraints();

    add(aniPane,ccs.xy(1,1));
    add(cachePane,ccs.xy(1,2));
    add(disPane,ccs.xy(1,3));

    oldTop = topBox.getSelectedIndex();
    oldMid = midBox.getSelectedIndex();
    oldLow = lowBox.getSelectedIndex();

    //useful frame method that handles closing of window
    setDefaultCloseOperation(HIDE_ON_CLOSE);
    //put frame in the right place, with the right size, and make visible
    setLocation(100, 100);
//    ((JComponent) getContentPane()).setPreferredSize(new Dimension(300,500));
    pack();
    setVisible(true);
    
    update = true;
  }

  /**
  * Converts a combo box index into a CacheManager constant
  * signifying an axis.
  */
  private int getConv(int index) {
    switch(index) {
      case 0:
        return CacheManager.Z_AXIS;
      case 1:
        return CacheManager.T_AXIS;
      case 2:
        return CacheManager.C_AXIS;
    }
    return -1;
  }

  /** Enables/Disables CacheManager options in option window.*/
  private void enableCache(boolean enable) {
    zCheck.setEnabled(enable);
    tCheck.setEnabled(enable);
    cCheck.setEnabled(enable);

    modeBox.setEnabled(enable);
    stratBox.setEnabled(enable);
    topBox.setEnabled(enable);
    midBox.setEnabled(enable);
    lowBox.setEnabled(enable);

    zBSpin.setEnabled(enable);
    zFSpin.setEnabled(enable);
    tBSpin.setEnabled(enable);
    tFSpin.setEnabled(enable);
    cBSpin.setEnabled(enable);
    cFSpin.setEnabled(enable);

    resetBtn.setEnabled(enable);
  }

  // -- ActionListener API methods --

  public void actionPerformed(ActionEvent e) {
    if(update) {
      Object source = e.getSource();

      if (source == modeBox) {
        if(modeBox.getSelectedIndex() == 0)
          manager.setMode(CacheManager.CROSS_MODE);
        else if(modeBox.getSelectedIndex() == 1)
          manager.setMode(CacheManager.RECT_MODE);
        else //modeBox.getSelectedIndex() == 2
          manager.setMode(CacheManager.CROSS_MODE | CacheManager.RECT_MODE);
      }
      else if (source == stratBox) {
        if(stratBox.getSelectedIndex() == 0)
          manager.setStrategy(CacheManager.FORWARD_FIRST);
        else //stratBox.getSelectedIndex() == 1
          manager.setStrategy(CacheManager.SURROUND_FIRST);
      }
      else if (source == resetBtn) {
        update = false;
        zCheck.setSelected(false);
        tCheck.setSelected(true);
        cCheck.setSelected(false);
        manager.setAxis(CacheManager.T_AXIS);
        modeBox.setSelectedIndex(0);
        manager.setMode(CacheManager.CROSS_MODE);
        stratBox.setSelectedIndex(0);
        manager.setStrategy(CacheManager.FORWARD_FIRST);
        topBox.setSelectedIndex(1);
        midBox.setSelectedIndex(0);
        lowBox.setSelectedIndex(2);
        manager.setPriority(CacheManager.T_AXIS,CacheManager.Z_AXIS,
          CacheManager.C_AXIS);

        Integer zeroI = new Integer(0);
        Integer twentyI = new Integer(20);
        zFSpin.setValue(zeroI);
        zBSpin.setValue(zeroI);
        tFSpin.setValue(twentyI);
        tBSpin.setValue(zeroI);
        cFSpin.setValue(zeroI);
        cBSpin.setValue(zeroI);
        manager.setSize(0,0,0,20,0,0);

        oldTop = topBox.getSelectedIndex();
        oldMid = midBox.getSelectedIndex();
        oldLow = lowBox.getSelectedIndex();
        update = true;
      }
      else if (source == topBox) {
        int newTop = topBox.getSelectedIndex();
        if (newTop == oldTop) return;
        if (newTop == oldMid) {
          update = false;
          midBox.setSelectedIndex(oldTop);
          oldMid = oldTop;
          update = true;
        }
        if (newTop == oldLow) {
          update = false;
          lowBox.setSelectedIndex(oldTop);
          oldLow = oldTop;
          update = true;
        }

        oldTop = newTop;
        manager.setPriority(getConv(topBox.getSelectedIndex()),
         getConv(midBox.getSelectedIndex()),
         getConv(lowBox.getSelectedIndex()));
      }
      else if (source == midBox) {
        int newMid = midBox.getSelectedIndex();
        if (newMid == oldMid) return;
        if (newMid == oldTop) {
          update = false;
          topBox.setSelectedIndex(oldMid);
          oldTop = oldMid;
          update = true;
        }
        if (newMid == oldLow) {
          update = false;
          lowBox.setSelectedIndex(oldMid);
          oldLow = oldMid;
          update = true;
        }

        oldMid = newMid;
        manager.setPriority(getConv(topBox.getSelectedIndex()),
         getConv(midBox.getSelectedIndex()),
         getConv(lowBox.getSelectedIndex()));
      }
      else if (source == lowBox) {
        int newLow = lowBox.getSelectedIndex();
        if (newLow == oldLow) return;
        if (newLow == oldTop) {
          update = false;
          topBox.setSelectedIndex(oldLow);
          oldTop = oldLow;
          update = true;
        }
        if (newLow == oldMid) {
          update = false;
          midBox.setSelectedIndex(oldLow);
          oldMid = oldLow;
          update = true;
        }

        oldLow = newLow;
        manager.setPriority(getConv(topBox.getSelectedIndex()),
         getConv(midBox.getSelectedIndex()),
         getConv(lowBox.getSelectedIndex()));
      }
    }
  }

  public void itemStateChanged(ItemEvent e) {
    if(update) {
      Object source = e.getItemSelectable();

      //if this is the only selected checkbox, leave it selected
      if (source == zCheck && e.getStateChange() == ItemEvent.DESELECTED &&
        !tCheck.isSelected() && !cCheck.isSelected())
      {
        update = false;
        zCheck.setSelected(true);
        update = true;
        return;
      }
      else if (source == tCheck && e.getStateChange() == ItemEvent.DESELECTED &&
        !zCheck.isSelected() && !cCheck.isSelected())
      {
        update = false;
        tCheck.setSelected(true);
        update = true;
        return;
      }
      else if (source == cCheck && e.getStateChange() == ItemEvent.DESELECTED &&
        !tCheck.isSelected() && !zCheck.isSelected())
      {
        update = false;
        cCheck.setSelected(true);
        update = true;
        return;
      }

      int zState = 0x00,tState = 0x00,cState = 0x00;

      if (zCheck.isSelected()) zState = CacheManager.Z_AXIS;
      if (tCheck.isSelected()) tState = CacheManager.T_AXIS;
      if (cCheck.isSelected()) cState = CacheManager.C_AXIS;

      int finalState = (zState | tState | cState);
      manager.setAxis(finalState);
    }
  }

  // -- ChangeListener API methods --

  public void stateChanged(ChangeEvent e) {
    if(update) {
      if (e.getSource() == fps) {
        // the frames per second changed
        cw.setFps(((Integer) fps.getValue()).intValue());
      }
      else {
        int zF,zB,tF,tB,cF,cB;
        zF = ((Integer) zFSpin.getValue()).intValue();
        zB = ((Integer) zBSpin.getValue()).intValue();
        tF = ((Integer) tFSpin.getValue()).intValue();
        tB = ((Integer) tBSpin.getValue()).intValue();
        cF = ((Integer) cFSpin.getValue()).intValue();
        cB = ((Integer) cBSpin.getValue()).intValue();
        manager.setSize(zB,zF,tB,tF,cB,cF);
      }
    }
  }
}
