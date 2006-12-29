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
import loci.formats.*;
import java.util.Vector;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/** Window displaying custom virtualization options. */
public class OptionsWindow extends JFrame implements
  ActionListener, ChangeListener, ItemListener
{
  // -- Fields --

  /**Constant dlu size for indents in GUI*/
  private static final String TAB = "7dlu";

  /** FPS spinner */
  private JSpinner fps;

  /** Parent window. */
  private CustomWindow cw;

  /** The CacheManager for this instance of data browser.*/
  private CacheManager manager;
  
  /** The FileStitcher used to stich files together.*/
  private FileStitcher fStitch;

  /** CheckBoxes to indicate which axes to store.*/
  private JCheckBox zCheck,tCheck,cCheck;
  
  /** CheckBoxes to control if caching is on or off */
  private JCheckBox cacheToggle;

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
  
  FileStitcher fs;
  
  JComboBox[] blockBoxes;
  String id = null,order = null,suffix = null;
  String[] prefixes = null,blocks = null;
  int sizeZ = -1,sizeT = -1,sizeC = -1;
  int[] axes = null;
  FilePattern fp = null;
  
  JComboBox zGroup,tGroup,cGroup;

  // -- Constructor --
  public OptionsWindow(int numZ, int numT, CustomWindow c) {
    super("LOCI Data Browser - Options");
    setBackground(Color.gray);

    cw = c;

    manager = cw.db.manager;
    
    fs = cw.db.fStitch;

    update = false;

    Border etchB = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    
    // get FilePattern Data
    try {
      id = cw.db.id;
      order = fs.getDimensionOrder(id).substring(2);
      sizeZ = fs.getSizeZ(id);
      sizeT = fs.getSizeT(id);
      sizeC = fs.getSizeC(id);
      axes = fs.getAxisTypes(id);
      fp = fs.getFilePattern(id);
      prefixes = fp.getPrefixes();
      blocks = fp.getBlocks();
      suffix = fp.getSuffix();
    }
    catch(Exception exc) {
      exc.printStackTrace();
      LociDataBrowser.exceptionMessage(exc);
    }

    // add Display Pane

    JPanel disPane = new JPanel();
    TitledBorder disB = BorderFactory.createTitledBorder(etchB,
      "Custom Axes");

    disPane.setBorder(disB);

    JLabel sliceLab = new JLabel("\u00B7" + "Slice-Groups in File" + "\u00B7");
    JLabel blockLab = new JLabel("\u00B7" + "Blocks in Filenames" + "\u00B7");

    Vector blockLabelsV = new Vector();
    for(int i = 0;i<blocks.length;i++) {
      JLabel temp = new JLabel("Block " + blocks[i] + ":");
      blockLabelsV.add(temp);
    }
    Object[] blockLabelsO = blockLabelsV.toArray();
    JLabel[] blockLabels = new JLabel[blockLabelsO.length];
    for(int i = 0;i<blockLabelsO.length;i++) {
      blockLabels[i] = (JLabel) blockLabelsO[i];
      blockLabels[i].setForeground(getColor(i));
    }
    
    Object[] choices = {"Z-Depth", "Time", "Channel"};
    zGroup = new JComboBox(choices);
    setBox(zGroup,0);
    tGroup = new JComboBox(choices);
    setBox(tGroup,1);
    cGroup = new JComboBox(choices);
    setBox(cGroup,2);
    zGroup.addActionListener(this);
    tGroup.addActionListener(this);
    cGroup.addActionListener(this);
    Vector blockBoxesV = new Vector();
    for(int i = 0;i<blocks.length;i++) {
      JComboBox temp = new JComboBox(choices);
      if (axes[i] == AxisGuesser.Z_AXIS) temp.setSelectedIndex(0);
      else if (axes[i] == AxisGuesser.T_AXIS) temp.setSelectedIndex(1);
      else temp.setSelectedIndex(2);
      temp.setActionCommand("Block1");
      temp.addActionListener(this);
      blockBoxesV.add(temp);
    }
    Object[] blockBoxesO = blockBoxesV.toArray();
    blockBoxes = new JComboBox[blockBoxesO.length];
    for(int i = 0;i<blockBoxesO.length;i++) {
      JComboBox temp = (JComboBox) blockBoxesO[i];
      temp.setForeground(getColor(i));
      blockBoxes[i] = temp;
    }
    
    JPanel slicePanel = new JPanel();
    slicePanel.add(sliceLab);
    slicePanel.setBackground(Color.darkGray);
    sliceLab.setForeground(Color.lightGray);
    JPanel blockPanel = new JPanel();
    blockPanel.add(blockLab);
    blockPanel.setBackground(Color.darkGray);
    blockLab.setForeground(Color.lightGray);

    JPanel filePane = new JPanel(new FlowLayout());
    for(int i = 0;i<prefixes.length;i++) {
      JLabel prefLab = new JLabel(prefixes[i]);
      JLabel blokLab = new JLabel(blocks[i]);
      blokLab.setForeground(getColor(i));
      filePane.add(prefLab);
      filePane.add(blokLab);
    }
    JLabel sufLab = new JLabel(suffix);
    filePane.add(sufLab);
    
    int[] internalSizes = new int[3];
    for(int i = 0;i<internalSizes.length;i++) {
      internalSizes[i] = getOrderSize(i);
    }
    
    JLabel zLab = new JLabel("First ("+ internalSizes[0] + "):");
    JLabel tLab = new JLabel("Second (" + internalSizes[1] + "):");
    JLabel cLab = new JLabel("Third (" + internalSizes[2] + "):");
    JLabel fileLab = new JLabel("Filename:");
    
    String rowString = "pref," + TAB + ",pref,pref,pref," + TAB +
      ",pref,pref";
    for(int i = 0; i<blockLabels.length;i++) {
      rowString += ",pref";
    }
    
    FormLayout layout = new FormLayout(
        TAB + ",pref," + TAB + ",pref:grow," + TAB,
        rowString);
    disPane.setLayout(layout);
    CellConstraints cc = new CellConstraints();

    disPane.add(slicePanel,cc.xyw(1,1,5));
    disPane.add(zLab,cc.xy(2,3));
    disPane.add(zGroup,cc.xy(4,3));
    if(internalSizes[1] != 1) {
      disPane.add(tLab,cc.xy(2,4));
      disPane.add(tGroup,cc.xy(4,4));
    }
    if(internalSizes[2] != 1) {
      disPane.add(cLab,cc.xy(2,5));
      disPane.add(cGroup,cc.xy(4,5));
    }
    disPane.add(blockPanel,cc.xyw(1,7,5));
    disPane.add(fileLab,cc.xy(2,8));
    disPane.add(filePane,cc.xy(4,8));
    for(int i = 0;i<blockLabels.length;i++) {
      disPane.add(blockLabels[i], cc.xy(2,9+i));
      disPane.add(blockBoxes[i], cc.xy(4,9+i)); 
    }

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
    JLabel priorL = new JLabel("\u00B7" + "Axis Cacheing Priority" + "\u00B7");
    JLabel topL = new JLabel("Top Priority:");
    JLabel midL = new JLabel("Mid Priority:");
    JLabel lowL = new JLabel("Low Priority:");
    JLabel genL = new JLabel("\u00B7" + "General Controls" + "\u00B7");

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
    JPanel genPanel = new JPanel();
    genPanel.add(genL);
    genPanel.setBackground(Color.darkGray);
    genL.setForeground(Color.lightGray);

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
    
    cacheToggle = new JCheckBox("Cache Images (on/off)");
    cacheToggle.setSelected(cw.db.virtual);
    cacheToggle.addItemListener(this);
    

    String[] modes = {"Crosshair", "Rectangle", "Cross/Rect"};
    modeBox = new JComboBox(modes);
    String[] strats = {"Forward","Surround"};
    stratBox = new JComboBox(strats);
    String[] boxAxes = {"Z","T","C"};
    topBox = new JComboBox(boxAxes);
    midBox = new JComboBox(boxAxes);
    lowBox = new JComboBox(boxAxes);
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
      + TAB + ",pref," + TAB + ",pref,pref,pref," + TAB + ",pref," + TAB + ",pref," + TAB + ",pref");
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
    cachePane.add(genPanel,cc3.xyw(1,18,7));
    cachePane.add(cacheToggle,cc3.xyw(2,20,5,"left,center"));
    cachePane.add(resetBtn,cc3.xyw(2,22,5,"right,center"));

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
  
  private int getBoxIndex(JComboBox jcb) {
    for(int i = 0;i<blockBoxes.length;i++) {
      if (jcb == blockBoxes[i]) return i;
    }
    return -1;
  }

  public static Color getColor(int i) {
    switch(i) {
      case 0:
        return Color.blue;
      case 1:
        return Color.green;
      case 2:
        return Color.red;
      case 3:
        return Color.magenta;
      case 4:
        return Color.orange;
      default:
        int next = i;
        Color tempColor = getColor(i%5);
        while (next > 4) {
          next -= 5;
          tempColor = tempColor.darker();
        }
        return tempColor; 
    }
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
  
  /** Set up the combo box to reflect appropriate axis.*/
  private void setBox(JComboBox thisBox, int index) {
    switch(order.charAt(index)) {
      case 'Z':
        thisBox.setSelectedIndex(0);
        break;
      case 'T':
        thisBox.setSelectedIndex(1);
        break;
      case 'C':
        thisBox.setSelectedIndex(2);
        break;
    }
  }
  
  private char convertInt(int index) {
    switch (index) {
      case 0:
        return 'Z';
      case 1:
        return 'T';
      case 2:
        return 'C';
      default:
        return 'Q';
    }
  }
  
  private int convertChar(char c) {
    switch (c) {
      case 'Z':
        return 0;
      case 'T':
        return 1;
      case 'C':
        return 2;
      default:
        return 'Q';
    }
  }
  
  private int getOrderSize(int i) {
    int thisSize = 1;
    switch(order.charAt(i)) {
      case 'Z':
        thisSize = sizeZ;
        thisSize /= getBlockCount(0);
        break;
      case 'T':
        thisSize = sizeT;
        thisSize /= getBlockCount(1);
        break;
      case 'C':
        thisSize = sizeC;
        thisSize /= getBlockCount(2);
        break;
    }
    return thisSize;
  }
  
  private int getBlockCount(int index) {
    int total = 0;
    int[] blockSizes = fp.getCount();
    for(int i = 0;i<blockBoxes.length;i++) {
      if (blockBoxes[i].getSelectedIndex() == index) {
        total += blockSizes[i];
      }
    }
    if (total == 0) total = 1;
    return total;
  }
  
  private int getAxis(int i) {
    switch (i) {
      case 0:
        return AxisGuesser.Z_AXIS;
      case 1:
        return AxisGuesser.T_AXIS;
      case 2:
        return AxisGuesser.C_AXIS;
      default:
        return -55555;
    }
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
      else if (source == zGroup) {
        char oldChar = order.charAt(0);
        int sel = zGroup.getSelectedIndex();
        char zChar = convertInt(sel);
        
        sel = tGroup.getSelectedIndex();
        char tChar = convertInt(sel);
        if(tChar == zChar) tChar = oldChar;
        
        sel = cGroup.getSelectedIndex();
        char cChar = convertInt(sel);
        if(cChar == zChar) cChar = oldChar;
        
        order = String.valueOf(zChar) + String.valueOf(tChar)
          + String.valueOf(cChar);
        try {
          fs.swapDimensions(id,"XY" + order);
          sizeZ = fs.getSizeZ(id);
          sizeT = fs.getSizeT(id);
          sizeC = fs.getSizeC(id);
        }
        catch(Exception exc) {
          exc.printStackTrace();
          LociDataBrowser.exceptionMessage(exc);
        }
        update = false;
        setBox(zGroup,0);
        setBox(tGroup,1);
        setBox(cGroup,2);
        update = true;
        cw.db.setDimensions();
        if(cw.db.virtual) cw.db.manager.dimChange();
        cw.updateControls();
      }
      else if (source == tGroup) {
        char oldChar = order.charAt(1);
        int sel = tGroup.getSelectedIndex();
        char tChar = convertInt(sel);
        
        sel = zGroup.getSelectedIndex();
        char zChar = convertInt(sel);
        if(zChar == tChar) zChar = oldChar;
        
        sel = cGroup.getSelectedIndex();
        char cChar = convertInt(sel);
        if(cChar == tChar) cChar = oldChar;
        
        order = String.valueOf(zChar) + String.valueOf(tChar)
          + String.valueOf(cChar);
        try {
          fs.swapDimensions(id,"XY" + order);
          sizeZ = fs.getSizeZ(id);
          sizeT = fs.getSizeT(id);
          sizeC = fs.getSizeC(id);
        }
        catch(Exception exc) {
          exc.printStackTrace();
          LociDataBrowser.exceptionMessage(exc);
        }
        update = false;
        setBox(zGroup,0);
        setBox(tGroup,1);
        setBox(cGroup,2);
        update = true;
        cw.db.setDimensions();
        if(cw.db.virtual) cw.db.manager.dimChange();
        cw.updateControls();
      }
      else if (source == cGroup) {
        char oldChar = order.charAt(2);
        int sel = cGroup.getSelectedIndex();
        char cChar = convertInt(sel);
        
        sel = zGroup.getSelectedIndex();
        char zChar = convertInt(sel);
        if(zChar == cChar) zChar = oldChar;
        
        sel = tGroup.getSelectedIndex();
        char tChar = convertInt(sel);
        if(tChar == cChar) tChar = oldChar;
        
        order = String.valueOf(zChar) + String.valueOf(tChar)
          + String.valueOf(cChar);
        try {
          fs.swapDimensions(id,"XY" + order);
          sizeZ = fs.getSizeZ(id);
          sizeT = fs.getSizeT(id);
          sizeC = fs.getSizeC(id);
        }
        catch(Exception exc) {
          exc.printStackTrace();
          LociDataBrowser.exceptionMessage(exc);
        }
        update = false;
        setBox(zGroup,0);
        setBox(tGroup,1);
        setBox(cGroup,2);
        update = true;
        cw.db.setDimensions();
        if(cw.db.virtual) cw.db.manager.dimChange();
        cw.updateControls();
      }
      else if (getBoxIndex((JComboBox)source) >= 0) {
        cw.update = false;
        int index = getBoxIndex((JComboBox)source);
        axes[index] = getAxis(blockBoxes[index].getSelectedIndex());
        try {
          fs.setAxisTypes(id,axes);
        }
        catch(Exception exc) {
          exc.printStackTrace();
          LociDataBrowser.exceptionMessage(exc);
        }
        cw.db.setDimensions();
        if(cw.db.virtual) cw.db.manager.dimChange();
        cw.updateControls();
        cw.update = true;
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
      else if (source == cacheToggle) {
        if(e.getStateChange() == ItemEvent.DESELECTED) {
          cw.db.toggleCache(false);
        }
        else {
          cw.db.toggleCache(true);
        }
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
