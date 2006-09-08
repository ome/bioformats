//
// OptionsWindow.java
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
  private static final String TAB = "5dlu";

  /**ComboBoxes for Custom Axes*/
  private JComboBox zBox, tBox;

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
    
    Border etchB = BorderFactory.createEtchedBorder(
    	EtchedBorder.LOWERED);

    // add Display Pane

    JPanel disPane = new JPanel();
    JPanel axesPane = new JPanel();
    TitledBorder disB = BorderFactory.createTitledBorder(
		  etchB, "Display Options");
		TitledBorder axesB = BorderFactory.createTitledBorder(
		  etchB, "Custom Axes");
		
		disPane.setBorder(disB);
		axesPane.setBorder(axesB);
    
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
     
    zBox.addItem(c.db.hasZ ? "\"z:\" <1-" + (c.db.numZ + 1) + ">" : "\"z:\" (no range)");
    zBox.addItem(c.db.hasT ? "\"t:\" <1-" + (c.db.numT + 1) + ">" : "\"t:\" (no range)");
    zBox.addItem(c.db.hasC ? "\"c:\" <1-" + (c.db.numC + 1) + ">" : "\"c:\" (no range)");
    zBox.setSelectedIndex(c.zMap);
    zBox.addActionListener(c);
    zBox.setActionCommand("mappingZ");
    
    tBox.addItem(c.db.hasZ ? "\"z:\" <1-" + (c.db.numZ + 1) + ">" : "\"z:\" (no range)");
    tBox.addItem(c.db.hasT ? "\"t:\" <1-" + (c.db.numT + 1) + ">" : "\"t:\" (no range)");
    tBox.addItem(c.db.hasC ? "\"c:\" <1-" + (c.db.numC + 1) + ">" : "\"c:\" (no range)");
    tBox.setSelectedIndex(c.tMap);
    tBox.addActionListener(c);
    tBox.setActionCommand("mappingT");

    FormLayout layout = new FormLayout(
        TAB + ",pref," + TAB + ",pref:grow," + TAB,
        "pref,pref,pref,pref");
    axesPane.setLayout(layout);
    CellConstraints cc = new CellConstraints();
    
    axesPane.add(fileLab,cc.xy(2,2));
    axesPane.add(filePane,cc.xy(4,2));
    axesPane.add(zLab,cc.xy(2,3));
    axesPane.add(zBox,cc.xy(4,3));
    axesPane.add(tLab,cc.xy(2,4));
    axesPane.add(tBox,cc.xy(4,4));
    
    FormLayout disLay = new FormLayout(
      "pref:grow", "pref:grow");
    disPane.setLayout(disLay);
    CellConstraints disC = new CellConstraints();
    
    disPane.add(axesPane, disC.xy(1,1));
    
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
    
    //configure/layout content pane
    setLayout(new BorderLayout());

    add(disPane,BorderLayout.CENTER);
    add(aniPane,BorderLayout.NORTH);

    //useful frame method that handles closing of window
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    //put frame in the right place, with the right size, and make visible
    setLocation(100, 100);
    ((JComponent) getContentPane()).setPreferredSize(new Dimension(225,200));
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
