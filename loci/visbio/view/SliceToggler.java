//
// SliceToggler.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2004 Curtis Rueden.

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

import java.awt.event.ActionEvent;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import loci.visbio.data.DataTransform;

import loci.visbio.util.DialogPane;
import loci.visbio.util.FormsUtil;
import loci.visbio.util.LAFUtil;

/** SliceToggler is a dialog pane for mass toggling stack slices. */
public class SliceToggler extends DialogPane {

  // -- GUI components --

  /** Starting slice index. */
  protected JTextField begin;

  /** Ending slice index. */
  protected JTextField end;

  /** Slice step increment. */
  protected JTextField step;

  /** Option for showing selected slices. */
  protected JRadioButton visible;

  /** Option for hiding selected slices. */
  protected JRadioButton hidden;


  // -- Other fields --

  /** Stack handler for this slice toggler. */
  protected StackHandler handler;

  /** Data transform upon which this slice toggler operates. */
  protected DataTransform transform;


  // -- Constructor --

  /** Constructs a control panel for adjusting viewing parameters. */
  public SliceToggler(StackHandler h) {
    super("Toggle slices");
    handler = h;

    // slice starting index text field
    begin = new JTextField(4);
    begin.setToolTipText("Starting slice index to toggle");

    // slice ending index text field
    end = new JTextField(4);
    end.setToolTipText("Ending slice index to toggle");

    // step increment text field
    step = new JTextField(4);
    step.setToolTipText("Step increment");

    // visible radio button
    visible = new JRadioButton("Visible");
    if (!LAFUtil.isMacLookAndFeel()) visible.setMnemonic('v');
    visible.setToolTipText("Toggles slices on");

    // hidden radio button
    hidden = new JRadioButton("Hidden");
    if (!LAFUtil.isMacLookAndFeel()) hidden.setMnemonic('h');
    hidden.setToolTipText("Toggles slices off");

    // group radio buttons
    ButtonGroup buttons = new ButtonGroup();
    buttons.add(visible);
    buttons.add(hidden);

    // lay out components
    add(FormsUtil.makeColumn(FormsUtil.makeRow(
      new Object[] {"S&lices", begin, "&to", end, "&step", step}),
      FormsUtil.makeRow("Change to", visible, hidden)
    ));
  }


  // -- SliceToggler API methods --

  /** Sets the transform affected by this slice toggler. */
  public void setTransform(DataTransform trans) { transform = trans; }


  // -- DialogPane API methods --

  /** Resets the slice toggler's components to their default states. */
  public void resetComponents() {
    begin.setText("1");
    StackLink link = (StackLink) handler.getLink(transform);
    int n = link.getSliceCount();
    end.setText("" + n);
    step.setText("1");
    visible.setSelected(true);
  }


  // -- ActionListener API methods --

  /** Handles GUI events. */
  public void actionPerformed(ActionEvent evt) {
    if (evt.getActionCommand().equals("ok")) {
      // parse GUI values
      int b = -1, e = -1, s = -1;
      try {
        b = Integer.parseInt(begin.getText());
        e = Integer.parseInt(end.getText());
        s = Integer.parseInt(step.getText());
      }
      catch (NumberFormatException exc) { }
      boolean vis = visible.isSelected();

      if (b >= 1 && e >= b && s >= 1) {
        // toggle slices using stack handler
        StackLink link = (StackLink) handler.getLink(transform);
        for (int i=b; i<=e; i+=s) link.setSliceVisible(i - 1, vis);
      }
    }
    super.actionPerformed(evt);
  }

}
