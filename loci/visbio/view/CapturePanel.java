//
// CapturePanel.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2005 Curtis Rueden.

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

/** Provides GUI controls for a display capture handler. */
public class CapturePanel extends JPanel implements ActionListener {

  // -- Fields --

  /** Capture handler upon which GUI controls operate. */
  protected CaptureHandler handler;

  /** Capture window. */
  protected CaptureWindow captureWindow;


  // -- Constructor --

  /** Creates a panel containing capture handler GUI controls. */
  public CapturePanel(CaptureHandler h) {
    super();
    handler = h;
    captureWindow = new CaptureWindow(handler);
    WindowManager wm = (WindowManager)
      h.getWindow().getVisBio().getManager(WindowManager.class);
    wm.addWindow(captureWindow);

    // capture button
    JButton capture = new JButton("Capture");
    if (!LAFUtil.isMacLookAndFeel()) capture.setMnemonic('p');
    capture.setToolTipText("Creates display screenshots or movies");
    capture.addActionListener(this);

    // lay out components
    setLayout(new BorderLayout());
    add(capture);
  }


  // -- CapturePanel API methods --

  /** Displays the capture window pane onscreen. */
  public void showCaptureWindow() {
    WindowManager wm = (WindowManager)
      handler.getWindow().getVisBio().getManager(WindowManager.class);
    wm.showWindow(captureWindow);
  }

  /** Gets the capture pane. */
  public CaptureWindow getCaptureWindow() { return captureWindow; }


  // -- ActionListener API methods --

  /** Handles button presses and combo box selections. */
  public void actionPerformed(ActionEvent e) { showCaptureWindow(); }

}
