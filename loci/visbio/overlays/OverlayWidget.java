//
// OverlayWidget.java
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

package loci.visbio.overlays;

import java.awt.BorderLayout;

import java.util.Vector;

import javax.swing.*;

import loci.visbio.util.FormsUtil;
import loci.visbio.util.SwingUtil;

/** OverlayWidget is a set of GUI controls for an overlay transform. */
public class OverlayWidget extends JPanel {

  // -- Fields --

  /** Associated overlay object. */
  protected OverlayTransform ann;

  /** Button group ensuring only one tool can be selected at a time. */
  protected ButtonGroup buttonGroup;

  /** List of overlay tools. */
  protected Vector tools;

  /** List of buttons for each overlay tool. */
  protected Vector buttons;


  // -- Constructor --

  /** Creates overlay GUI controls. */
  public OverlayWidget(OverlayTransform ann) {
    super();
    this.ann = ann;
    buttonGroup = new ButtonGroup();

    OverlayTool[] toolList = {
      new PointerTool(ann),
      new LineTool(ann),
      new MarkerTool(ann),
      new TextTool(ann),
      new OvalTool(ann),
      new BoxTool(ann),
      new ArrowTool(ann)
    };
    tools = new Vector(toolList.length);
    buttons = new Vector(toolList.length);

    for (int i=0; i<toolList.length; i++) addTool(toolList[i]);

    // lay out components
    setLayout(new BorderLayout());
    Object[] buttonList = new Object[buttons.size()];
    buttons.copyInto(buttonList);
    add(FormsUtil.makeRow(buttonList));
  }


  // -- OverlayWidget API methods --

  /** Adds the given overlay tool to the overlay widget. */
  public void addTool(OverlayTool tool) {
    JToggleButton b = SwingUtil.makeToggleButton(
      this, tool.getIcon(), tool.getName(), 6, 6);
    b.setToolTipText(tool.getTip());
    buttonGroup.add(b);
    buttons.add(b);
    tools.add(tool);
  }

  /** Gets the currently selected overlay tool. */
  public OverlayTool getActiveTool() {
    int size = buttons.size();
    for (int i=0; i<size; i++) {
      JToggleButton b = (JToggleButton) buttons.elementAt(i);
      if (b.isSelected()) return (OverlayTool) tools.elementAt(i);
    }
    return null;
  }

}
