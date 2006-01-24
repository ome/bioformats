//
// DialogPane.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2006 Curtis Rueden.

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

package loci.visbio.util;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import visad.util.Util;

/**
 * DialogPane provides an extensible interface for
 * creating dialogs with Ok and Cancel buttons.
 */
public class DialogPane extends JPanel implements ActionListener {

  // -- Constants --

  /** Return value if approve (ok) is chosen. */
  public static final int APPROVE_OPTION = 1;

  /** Return value if cancel is chosen. */
  public static final int CANCEL_OPTION = 2;


  // -- GUI components --

  /** Ok button. */
  protected JButton ok;

  /** Cancel button. */
  protected JButton cancel;

  /** Currently visible dialog. */
  protected JDialog dialog;

  /** Content pane for dialog. */
  protected JPanel pane;


  // -- Other fields --

  /** Dialog's title. */
  protected String title;

  /** Return value of dialog. */
  protected int rval;


  // -- Constructors --

  /** Creates a dialog pane. */
  public DialogPane(String title) { this(title, true); }

  /** Creates a dialog pane. */
  public DialogPane(String title, boolean doCancel) {
    super();
    this.title = title;
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    makeButtons(doCancel);

    // lay out components
    PanelBuilder builder = new PanelBuilder(new FormLayout(
      "pref:grow", "fill:pref:grow, 3dlu, pref"));
    builder.setDefaultDialogBorder();
    CellConstraints cc = new CellConstraints();
    builder.add(this, cc.xy(1, 1));
    builder.add(doButtonLayout(), cc.xy(1, 3));
    pane = builder.getPanel();
  }


  // -- DialogPane API methods --

  /** Displays a dialog using this dialog pane. */
  public int showDialog(Component parent) { return showDialog(parent, true); }

  /** Displays a dialog using this dialog pane. */
  public int showDialog(Frame parent) { return showDialog(parent, true); }

  /** Displays a dialog using this dialog pane. */
  public int showDialog(Dialog parent) { return showDialog(parent, true); }

  /** Displays a dialog using this dialog pane. */
  public int showDialog(Component parent, boolean modal) {
    Window w = SwingUtil.getWindow(parent);
    if (w instanceof Frame) return showDialog((Frame) w, modal);
    else if (w instanceof Dialog) return showDialog((Dialog) w, modal);
    else return showDialog((Frame) null, modal);
  }

  /** Displays a dialog using this dialog pane. */
  public int showDialog(Frame parent, boolean modal) {
    dialog = new JDialog(parent, title, modal);
    return showDialog();
  }

  /** Displays a dialog using this dialog pane. */
  public int showDialog(Dialog parent, boolean modal) {
    dialog = new JDialog(parent, title, modal);
    return showDialog();
  }

  /** Resets the dialog pane's components to their default states. */
  public void resetComponents() { }

  /** Internal method for creating dialog buttons. */
  protected void makeButtons(boolean doCancel) {
    // cancel button
    if (doCancel) {
      cancel = new JButton("Cancel");
      if (!LAFUtil.isMacLookAndFeel()) cancel.setMnemonic('c');
      cancel.setActionCommand("cancel");
      cancel.addActionListener(this);
    }

    // ok button
    ok = new JButton("Ok");
    if (!LAFUtil.isMacLookAndFeel()) ok.setMnemonic('o');
    ok.setActionCommand("ok");
    ok.addActionListener(this);
  }


  // -- ActionListener API methods --

  /** Handles button press events. */
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (command.equals("ok")) {
      rval = APPROVE_OPTION;
      dialog.setVisible(false);
    }
    else if (command.equals("cancel")) {
      rval = CANCEL_OPTION;
      dialog.setVisible(false);
    }
  }


  // -- Helper methods --

  /** Displays a dialog using this dialog pane. */
  protected int showDialog() {
    dialog.setContentPane(pane);
    dialog.getRootPane().setDefaultButton(ok);
    resetComponents();
    dialog.pack();
    Window owner = dialog.getOwner();
    if (owner == null) Util.centerWindow(dialog);
    else Util.centerWindow(owner, dialog);
    rval = CANCEL_OPTION;
    dialog.setVisible(true);
    return rval;
  }

  /** Performs button layout. */
  protected JPanel doButtonLayout() {
    return cancel == null ?
      ButtonBarFactory.buildOKBar(ok) :
      ButtonBarFactory.buildOKCancelBar(ok, cancel);
  }

}
