//
// RenderPanel.java
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

import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import loci.visbio.WindowManager;

import loci.visbio.util.LAFUtil;

/** Provides GUI controls for a volume rendering handler. */
public class RenderPanel extends JPanel implements ActionListener {

  // -- Fields --

  /** Volume rendering handler upon which GUI controls operate. */
  protected RenderHandler handler;

  /** Volume rendering window. */
  protected RenderWindow renderWindow;


  // -- Constructor --

  /** Creates a panel containing volume rendering handler GUI controls. */
  public RenderPanel(RenderHandler h) {
    super();
    handler = h;
    renderWindow = new RenderWindow(handler);
    WindowManager wm = (WindowManager)
      h.getWindow().getVisBio().getManager(WindowManager.class);
    wm.addWindow(renderWindow);

    // render button
    JButton render = new JButton("Render");
    if (!LAFUtil.isMacLookAndFeel()) render.setMnemonic('n');
    render.setToolTipText("Performs volume rendering");
    render.addActionListener(this);

    // lay out components
    setLayout(new BorderLayout());
    add(render);
  }


  // -- RenderPanel API methods --

  /** Displays the volume rendering window pane onscreen. */
  public void showRenderWindow() {
    WindowManager wm = (WindowManager)
      handler.getWindow().getVisBio().getManager(WindowManager.class);
    wm.showWindow(renderWindow);
  }

  /** Gets the volume rendering window pane. */
  public RenderWindow getRenderWindow() { return renderWindow; }


  // -- ActionListener API methods --

  /** Handles button presses and combo box selections. */
  public void actionPerformed(ActionEvent e) { showRenderWindow(); }

}
