//
// OMELoginPanel.java
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
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * OMELoginPanel is the class that handles
 * the window used to obtain the information
 * needed to log into the OME database.
 * @author Philip Huettl pmhuettl@wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */

public class OMELoginPanel implements ActionListener {
  //Fields
  private JButton ok, cancels;
  private JDialog in;
  private JTextField servField, useField;
  private JPasswordField passField;
  public boolean cancelPlugin;

  /** Constructor, sets up the dialog box */
  public OMELoginPanel(Frame frame) {
    cancelPlugin = false;
    //Creates the Dialog Box for getting OME login information
    in = new JDialog(frame, "OME Login", true);
    JPanel input1 = new JPanel();
    JPanel input = new JPanel();
    JPanel input2 = new JPanel();
    JPanel input3 = new JPanel();
    input1.setLayout(new BoxLayout(input1, BoxLayout.Y_AXIS));
    input.setLayout(new BoxLayout(input, BoxLayout.X_AXIS));
    input2.setLayout(new BoxLayout(input2, BoxLayout.Y_AXIS));
    input3.setLayout(new BoxLayout(input3, BoxLayout.Y_AXIS));
    input1.add(input);
    input.add(input2);
    input.add(input3);
    EmptyBorder bord = new EmptyBorder(5,5,5,5);
    passField = new JPasswordField("", 30);  // insert password here
    servField = new JTextField(OMESidePanel.getServer(), 30);
    useField = new JTextField(OMESidePanel.getUser(), 30);  // insert user here
    passField.setMaximumSize(new Dimension(passField.getMaximumSize().width,
      passField.getPreferredSize().height));
    servField.setMaximumSize(new Dimension(servField.getMaximumSize().width,
      servField.getPreferredSize().height));
    useField.setMaximumSize(new Dimension(useField.getMaximumSize().width,
      useField.getPreferredSize().height));
    JLabel ser = new JLabel("Server: ", JLabel.TRAILING);
    ser.setAlignmentX(JLabel.RIGHT);
    ser.setAlignmentY(JLabel.TOP);
    ser.setPreferredSize(ser.getMinimumSize());
    input2.add(ser);
    input3.add(servField);
    JLabel use = new JLabel("Username: ", JLabel.TRAILING);
    use.setAlignmentX(JLabel.RIGHT);
    use.setAlignmentY(JLabel.TOP);
    use.setBorder(new EmptyBorder(2,2,1,1));
    use.setPreferredSize(use.getMinimumSize());
    input2.add(use);
    input3.add(useField);
    JLabel pas = new JLabel("Password: ", JLabel.TRAILING);
    pas.setAlignmentX(JLabel.RIGHT);
    pas.setAlignmentY(JLabel.TOP);
    pas.setBorder(new EmptyBorder(2,1,1,1));
    pas.setPreferredSize(pas.getMinimumSize());
    input2.add(pas);
    input3.add(passField);
    in.setContentPane(input1);
    ok = new JButton("OK");
    cancels = new JButton("Cancel");
    ok.setMnemonic(KeyEvent.VK_ENTER);
    ok.setActionCommand("OK");
    cancels.setActionCommand("cancels");
    JPanel paneBut = new JPanel();
    paneBut.setLayout(new BoxLayout(paneBut,BoxLayout.X_AXIS));
    paneBut.add(ok);
    paneBut.add(cancels);
    input1.add(paneBut);
    cancels.addActionListener(this);
    ok.addActionListener(this);
    input1.setBorder(bord);
    in.pack();
    OMESidePanel.centerWindow(frame, in);
  }

  /** implements the ActionListener actionPerformed method */
  public void actionPerformed(ActionEvent e) {
    if ("OK".equals(e.getActionCommand())) {
      cancelPlugin = false;
      in.setVisible(false);
    }
    else{
      cancelPlugin = true;
      in.dispose();
    }
  }

  /** produces an error notification popup with the inputted text */
  public static void infoShow(Frame frame, String s, String x) {
    JOptionPane.showMessageDialog(frame,s,x,JOptionPane.INFORMATION_MESSAGE);
  }

  /** Method that retrieves the information to log onto the OME server */
  public String[] getInput(boolean b) {
    boolean error = false;
    if (b) {
      OMEDownPanel.error((Frame)in.getOwner(),
        "The login information is not valid.", "Input Error");
    }
    cancelPlugin = true;
    in.setVisible(true);
    if (cancelPlugin) return null;
    //checks and puts results into an array
    String[] results = new String[3];
    try {
       OMESidePanel.setServer(servField.getText());
       OMESidePanel.setUser(useField.getText());
       String server = servField.getText();
       String f = new String(passField.getPassword());
       String x = useField.getText();
       if (server.equals("") || f.equals("") || x.equals("")) {
         error = true;
       }
       if (server.startsWith("http:")) {
        server = server.substring(5);
       }
       while (server.startsWith("/")) server = server.substring(1);
       int slash = server.indexOf("/");
       if (slash >= 0) server = server.substring(0, slash);
       int colon = server.indexOf(":");
       if (colon >= 0) server = server.substring(0, colon);
       server = "http://" + server + "/shoola/";
       results[0] = server;
       results[1] = x;
       results[2] = f;
    }
    catch (NullPointerException e) {
      error = true;
    }
    if (error) {
      return getInput(true);
    }
    return results;
  }

}
