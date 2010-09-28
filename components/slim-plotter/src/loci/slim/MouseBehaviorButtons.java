//
// MouseBehaviorButtons.java
//

/*
SLIM Plotter application and curve fitting library for
combined spectral lifetime visualization and analysis.
Copyright (C) 2006-@year@ Curtis Rueden and Eric Kjellman.

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

package loci.slim;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;

import visad.DisplayImpl;
import visad.MouseHelper;
import visad.VisADException;

/**
 * Button bar for controlling a VisAD display's mouse behavior.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/slim-plotter/src/loci/slim/MouseBehaviorButtons.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/slim-plotter/src/loci/slim/MouseBehaviorButtons.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class MouseBehaviorButtons extends JPanel implements ActionListener {

  // -- Constants --

  private static final int[][][] CURSOR_MAP = {
    {{MouseHelper.CURSOR_TRANSLATE, // L
      MouseHelper.CURSOR_ZOOM},     // shift-L
     {MouseHelper.CURSOR_ROTATE,    // ctrl-L
      MouseHelper.NONE}},           // ctrl-shift-L
    {{MouseHelper.CURSOR_TRANSLATE, // M
      MouseHelper.CURSOR_ZOOM},     // shift-M
     {MouseHelper.CURSOR_ROTATE,    // ctrl-M
      MouseHelper.NONE}},           // ctrl-shift-M
    {{MouseHelper.CURSOR_TRANSLATE, // R
      MouseHelper.CURSOR_ZOOM},     // shift-R
     {MouseHelper.CURSOR_ROTATE,    // ctrl-R
      MouseHelper.NONE}}            // ctrl-shift-R
  };

  // -- Fields --

  protected DisplayImpl display;
  protected JToggleButton rotate, zoom, pan, probe, region;

  // -- Constructor --

  public MouseBehaviorButtons(DisplayImpl display,
    boolean threeD, boolean direct)
  {
    this.display = display;
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    setBorder(new EmptyBorder(0, 0, 5, 0));

    ButtonGroup group = new ButtonGroup();

    // Rotate
    if (threeD) {
      rotate = new JToggleButton("Rotate");
      rotate.addActionListener(this);
      group.add(rotate);
      add(rotate);
    }

    // Zoom
    zoom = new JToggleButton("Zoom");
    zoom.addActionListener(this);
    group.add(zoom);
    add(zoom);

    // Pan
    pan = new JToggleButton("Pan");
    pan.addActionListener(this);
    group.add(pan);
    add(pan);

    // Probe
    probe = new JToggleButton("Probe");
    probe.addActionListener(this);
    group.add(probe);
    add(probe);

    // Region
    if (direct) {
      region = new JToggleButton("Region");
      region.addActionListener(this);
      group.add(region);
      add(region);
    }

    if (threeD) rotate.doClick();
    else if (direct) region.doClick();
    else zoom.doClick();
  }

  // -- MouseBehaviorButtons methods --

  public boolean isRegion() { return region != null && region.isSelected(); }
  public boolean isProbe() { return probe != null && probe.isSelected(); }

  // -- ActionListener methods --

  /** Handles checkbox and button presses. */
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    int action = -1;
    int[][][] mouseMap = null;
    if (src == rotate) action = MouseHelper.ROTATE;
    else if (src == zoom) action = MouseHelper.ZOOM;
    else if (src == pan) action = MouseHelper.TRANSLATE;
    else if (src == probe) mouseMap = CURSOR_MAP;
    else if (src == region) action = MouseHelper.DIRECT;
    else return;

    if (mouseMap == null) {
      mouseMap = new int[3][2][2];
      for (int i=0; i<mouseMap.length; i++) {
        for (int j=0; j<mouseMap[i].length; j++) {
          Arrays.fill(mouseMap[i][j], action);
        }
      }
    }
    try {
      display.getMouseBehavior().getMouseHelper().setFunctionMap(mouseMap);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
  }

}
