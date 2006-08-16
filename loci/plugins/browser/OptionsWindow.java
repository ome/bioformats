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

/** Window displaying custom virtualization options. */
public class OptionsWindow extends Frame implements
  ActionListener, ChangeListener, ItemListener
{
  // -- Fields --

  /** Group of buttons to choose how we display the stack. */
  private ButtonGroup buttons;
  private JRadioButton custom;
  private JRadioButton animateT;
  private JRadioButton animateZ;

  /** Spinners to control our custom virtualization. */
  private JSpinner zMin;
  private JSpinner zMax;
  private JSpinner tMin;
  private JSpinner tMax;

  /** FPS spinner */
  private JSpinner fps;

  /** Parent window. */
  private CustomWindow cw;

  /** Button to close the window. */
  private JButton close;

  // -- Constructor --
  public OptionsWindow(int numZ, int numT, CustomWindow c) {
    super("LOCI Data Browser - Options");
    setResizable(false);
    setSize(400, 250);
    setBackground(Color.gray);

    cw = c;

    JLabel filler = new JLabel("");
    JPanel pane = new JPanel();

    GridLayout frameLayout = new GridLayout(0, 1);
    setLayout(frameLayout);

    GridLayout grid = new GridLayout(0, 1);
    pane.setLayout(grid);
    pane.setBackground(Color.gray);

    buttons = new ButtonGroup();

    // add each radio button
    // we allow the user to choose a custom virtualization, animation across T,
    // or animation across Z (swap axes)

    custom = new JRadioButton("Custom Virtualization", cw.isCustom());
    custom.addItemListener(this);
    custom.setBackground(Color.gray);
    grid.addLayoutComponent("", custom);
    pane.add(custom);
    buttons.add(custom);

    add(pane);

    pane = new JPanel();
    pane.setBackground(Color.gray);
    grid = new GridLayout(0, 5);
    pane.setLayout(grid);

    // add each spinner

    filler.setBackground(Color.gray);
    grid.addLayoutComponent("", filler);
    pane.add(filler);

    SpinnerModel model = new SpinnerNumberModel(1, 1, numZ, 1);
    zMin = new JSpinner(model);
    model = new SpinnerNumberModel(1, 1, numZ, 1);
    zMax = new JSpinner(model);

    model = new SpinnerNumberModel(1, 1, numT, 1);
    tMin = new JSpinner(model);
    model = new SpinnerNumberModel(1, 1, numT, 1);
    tMax = new JSpinner(model);

    zMin.addChangeListener(this);
    zMax.addChangeListener(this);
    tMin.addChangeListener(this);
    tMax.addChangeListener(this);

    zMin.setEnabled(false);
    zMax.setEnabled(false);
    tMin.setEnabled(false);
    tMax.setEnabled(false);

    zMin.setBackground(Color.gray);
    zMax.setBackground(Color.gray);
    tMin.setBackground(Color.gray);
    tMax.setBackground(Color.gray);

    JLabel customLabel = new JLabel("Z : from ");
    customLabel.setBackground(Color.gray);
    grid.addLayoutComponent("", customLabel);
    pane.add(customLabel);

    grid.addLayoutComponent("", zMin);
    pane.add(zMin);

    customLabel = new JLabel(" to ");
    customLabel.setBackground(Color.gray);
    grid.addLayoutComponent("", customLabel);
    pane.add(customLabel);

    grid.addLayoutComponent("", zMax);
    pane.add(zMax);

    filler = new JLabel("");
    filler.setBackground(Color.gray);
    grid.addLayoutComponent("", filler);
    pane.add(filler);

    customLabel = new JLabel("T : from ");
    customLabel.setBackground(Color.gray);
    grid.addLayoutComponent("", customLabel);
    pane.add(customLabel);

    grid.addLayoutComponent("", tMin);
    pane.add(tMin);

    customLabel = new JLabel(" to ");
    customLabel.setBackground(Color.gray);
    grid.addLayoutComponent("", customLabel);
    pane.add(customLabel);

    grid.addLayoutComponent("", tMax);
    pane.add(tMax);

    add(pane);

    pane = new JPanel();
    pane.setBackground(Color.gray);
    grid = new GridLayout(0, 1);
    pane.setLayout(grid);

    filler = new JLabel("");
    filler.setBackground(Color.gray);
    grid.addLayoutComponent("", filler);
    pane.add(filler);

    animateT = new JRadioButton("Animate across T",
      (!cw.isSwapped() && !cw.isCustom() && numT > 1) || (numZ == 1));
    animateT.addItemListener(this);
    animateT.setBackground(Color.gray);

    grid.addLayoutComponent("", animateT);
    pane.add(animateT);
    buttons.add(animateT);

    filler = new JLabel("");
    filler.setBackground(Color.gray);
    grid.addLayoutComponent("", filler);
    pane.add(filler);

    animateZ = new JRadioButton("Animate across Z",
      (cw.isSwapped() && !cw.isCustom() && numZ > 1) || (numT == 1));
    animateZ.addItemListener(this);
    animateZ.setBackground(Color.gray);

    grid.addLayoutComponent("", animateZ);
    pane.add(animateZ);
    buttons.add(animateZ);

    filler = new JLabel("");
    filler.setBackground(Color.gray);
    grid.addLayoutComponent("", filler);
    pane.add(filler);

    add(pane);

    pane = new JPanel();
    pane.setBackground(Color.gray);
    grid = new GridLayout(0, 3);
    pane.setLayout(grid);

    // add the frames-per-second spinner

    customLabel = new JLabel("Frames per second");
    customLabel.setBackground(Color.gray);
    grid.addLayoutComponent("", customLabel);
    pane.add(customLabel);

    model = new SpinnerNumberModel(10, 1, 99, 1);
    fps = new JSpinner(model);
    fps.addChangeListener(this);
    fps.setBackground(Color.gray);
    grid.addLayoutComponent("", fps);
    pane.add(fps);

    // add the close button

    filler = new JLabel("");
    filler.setBackground(Color.gray);
    grid.addLayoutComponent("", filler);
    pane.add(filler);

    add(pane);

    pane = new JPanel();
    pane.setBackground(Color.gray);
    grid = new GridLayout(0, 1);
    pane.setLayout(grid);

    filler = new JLabel("");
    filler.setBackground(Color.gray);
    grid.addLayoutComponent("", filler);
    pane.add(filler);


    close = new JButton("Close");
    close.addActionListener(this);
    grid.addLayoutComponent("", close);
    pane.add(close);

    add(pane);
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
    else {
      // one of the virtualization spinner values changed
      int[] values = new int[] {
        ((Integer) zMin.getValue()).intValue(),
        ((Integer) zMax.getValue()).intValue(),
        ((Integer) tMin.getValue()).intValue(),
        ((Integer) tMax.getValue()).intValue()
      };

      cw.setVirtualization(values);
    }
  }

  // -- ItemListener API methods --

  public void itemStateChanged(ItemEvent e) {
    // the radio button values changed

    if (custom.isSelected()) {
      zMin.setEnabled(true);
      zMax.setEnabled(true);
      tMin.setEnabled(true);
      tMax.setEnabled(true);
      cw.setVirtualization(new int[] {1, 1, 1, 1});
    }
    else if (animateZ.isSelected()) {
      zMin.setEnabled(false);
      zMax.setEnabled(false);
      tMin.setEnabled(false);
      tMax.setEnabled(false);
      cw.swap(true);
    }
    else if (animateT.isSelected()) {
      zMin.setEnabled(false);
      zMax.setEnabled(false);
      tMin.setEnabled(false);
      tMax.setEnabled(false);
      cw.swap(false);
    }
  }

}
