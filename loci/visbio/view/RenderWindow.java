//
// RenderWindow.java
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

import javax.swing.*;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import loci.visbio.VisBioFrame;

import loci.visbio.util.FormsUtil;
import loci.visbio.util.SwingUtil;

/** RenderWindow is a window for adjusting volume rendering settings. */
public class RenderWindow extends JFrame implements ChangeListener {

  // -- Fields --

  /** Handler for this volume rendering window. */
  protected RenderHandler handler;

  /** Checkbox indicating whether volume rendering is enabled. */
  protected JCheckBox render;

  /** Slider for adjusting volume resolution. */
  protected JSlider res;


  // -- Constructor --

  /** Constructs a window for capturing display screenshots and movies. */
  public RenderWindow(RenderHandler h) {
    super("Render - " + h.getWindow().getTitle());
    handler = h;

    // checkbox for toggling volume rendering
    render = new JCheckBox("Enable volume rendering");

    // slider for adjusting volume resolution
    res = new JSlider(RenderHandler.MINIMUM_RESOLUTION,
      RenderHandler.MAXIMUM_RESOLUTION, RenderHandler.DEFAULT_RESOLUTION);
    res.addChangeListener(this);

    // label for current resolution value

    // lay out components
    JScrollPane pane = new JScrollPane(FormsUtil.makeRow(new Object[] {
      "Resolution", res}, "pref:grow", true));
    SwingUtil.configureScrollPane(pane);
    setContentPane(pane);
  }


  // -- RenderWindow API methods --

  /** Sets the resolution slider's value. */
  public void setResolution(int value) { res.setValue(value); }

  /** Gets the resolution slider's value. */
  public int getResolution() { return res.getValue(); }


  // -- ChangeListener API methods --

  /** Called when slider is adjusted. */
  public void stateChanged(ChangeEvent e) {
    DisplayWindow display = handler.getWindow();
    VisBioFrame bio = display.getVisBio();

    Object src = e.getSource();
    if (src == res) {
      if (!res.getValueIsAdjusting()) {
        bio.generateEvent(bio.getManager(DisplayManager.class),
          "volume rendering resolution adjustment for " + display.getName(),
          true);
      }
    }
  }

}
