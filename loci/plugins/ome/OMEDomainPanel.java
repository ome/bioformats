//
// OMEDomainPanel.java
//

/*
OME Plugin for ImageJ plugin for transferring images to and from an OME
database. Copyright (C) 2004-@year@ Philip Huettl and Melissa Linkert.

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

package loci.ome.ij;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * OMEDomainPanel is the class that handles
 * the window used to obtain the domain information
 * on a multidimensional image to allow it to be read
 * in ImageJ.
 * @author Philip Huettl pmhuettl@wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */

public class OMEDomainPanel implements ActionListener, ChangeListener {

  // -- Fields --

  private JButton ok, cancels;
  private JRadioButton time, space;
  private JDialog pick;
  private JSlider slide;
  private JLabel label4;
  public boolean cancelPlugin = false;
  public boolean timeDomain = false;
  private int sizeZ, sizeT;

  // -- Constructor --

  public OMEDomainPanel(Frame frame, int z, int t) {
    sizeZ = z;
    sizeT = t;
    //Creates the Dialog Box for getting OME login information
    pick = new JDialog(frame, "Domain Selection", true);
    JPanel mainpane = new JPanel();
    JPanel paneR = new JPanel();
    JPanel paneL = new JPanel();
    JPanel paneM = new JPanel();
    mainpane.setLayout(new BoxLayout(mainpane, BoxLayout.Y_AXIS));
    paneR.setLayout(new BoxLayout(paneR, BoxLayout.Y_AXIS));
    paneL.setLayout(new BoxLayout(paneL, BoxLayout.Y_AXIS));
    paneM.setLayout(new BoxLayout(paneM, BoxLayout.X_AXIS));
    JLabel label = new JLabel("The image you have selected contains\n" +
      "variability in both the space and time domains.\nImageJ does not " +
      "support this, so choose the domain and value to set as a constant.\n");
    JLabel label5 = new JLabel("Selected Value: ");
    label4 = new JLabel("1");
    paneL.add(label5);
    paneR.add(label4);
    mainpane.add(label);
    paneM.add(paneL);
    paneM.add(paneR);
    space = new JRadioButton("Space");
    space.setMnemonic(KeyEvent.VK_S);
    space.setActionCommand("Space");
    space.setSelected(true);
    space.addActionListener(this);
    mainpane.add(space);
    time = new JRadioButton("Time");
    time.setMnemonic(KeyEvent.VK_T);
    time.setActionCommand("Time");
    time.addActionListener(this);
    mainpane.add(time);
    mainpane.add(paneM);
    ButtonGroup group = new ButtonGroup();
    group.add(space);
    group.add(time);
    slide = new JSlider(0, sizeZ-1, 0);
    slide.setMajorTickSpacing(5);
    slide.setMinorTickSpacing(1);
    slide.setPaintTicks(true);
    slide.setPaintLabels(true);
    slide.addChangeListener(this);
    mainpane.add(slide);
    pick.setContentPane(mainpane);
    EmptyBorder bord = new EmptyBorder(5,5,5,5);
    ok = new JButton("OK");
    cancels = new JButton("Cancel");
    ok.setActionCommand("OK");
    cancels.setActionCommand("cancels");
    JPanel paneBut = new JPanel();
    paneBut.setLayout(new BoxLayout(paneBut,BoxLayout.X_AXIS));
    paneBut.add(ok);
    paneBut.add(cancels);
    mainpane.add(paneBut);
    cancels.addActionListener(this);
    ok.addActionListener(this);
    mainpane.setBorder(bord);
    pick.pack();
    OMESidePanel.centerWindow(frame, pick);
  }

  /** implement the ActionListener actionPerformed method */
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if(command.equals("OK")) {
      cancelPlugin = false;
      pick.setVisible(false);
    }
    else if(command.equals("Time")) {
      slide.setMaximum(sizeT-1);
      slide.setMajorTickSpacing(5);
      slide.setMinorTickSpacing(1);
      slide.setPaintTicks(true);
      slide.setPaintLabels(true);
      timeDomain = true;
      slide.repaint();
    }
    else if(command.equals("Space")) {
      slide.setMaximum(sizeZ-1);
      slide.setMajorTickSpacing(5);
      slide.setMinorTickSpacing(1);
      slide.setPaintTicks(true);
      slide.setPaintLabels(true);
      timeDomain = false;
      slide.repaint();
    }
    else {
      cancelPlugin = true;
      pick.dispose();
    }
  }

  /** Method that retrieves the information to input the specified domain */
  public int[] getInput() {
    cancelPlugin = true;
    pick.setVisible(true);
    if (cancelPlugin) return null;

    //checks and puts results into an array

    int[] results = new int[2];
    if (timeDomain == true) {
      results[0] = 1;
    }
    else results[0] = 0;
    results[1] = slide.getValue();
    return results;
  }

  /** Method that implements the ChangeListener stateChanged method */
  public void stateChanged(ChangeEvent e) {
    JSlider source = (JSlider) e.getSource();
    int fps = (int) source.getValue();
    label4.setText(String.valueOf(fps));
  }

}
