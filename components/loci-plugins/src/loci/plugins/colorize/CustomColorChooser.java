//
// CustomColorChooser.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser, Stack Colorizer and Stack Slicer. Copyright (C) 2005-@year@
Melissa Linkert, Curtis Rueden and Christopher Peterson.

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

package loci.plugins.colorize;

import ij.gui.GenericDialog;
import ij.util.Tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.util.Vector;

import loci.plugins.util.WindowTools;

/**
 * Adapted from {@link ij.gui.ColorChooser}.
 * ColorChooser is not used because there is no way to change the slider
 * labels&mdash;this means that we can't record macros in which custom colors
 * are chosen for multiple channels.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/CustomColorChooser.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/CustomColorChooser.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class CustomColorChooser implements TextListener {
  Vector<TextField> colors;
  Panel panel;
  Color initialColor;
  int red, green, blue;
  String title;

  private int series, channel;

  public CustomColorChooser(String title, Color initialColor, int series,
    int channel)
  {
      this.title = title;
      if (initialColor == null) initialColor = Color.BLACK;
      this.initialColor = initialColor;
      red = initialColor.getRed();
      green = initialColor.getGreen();
      blue = initialColor.getBlue();
      this.series = series;
      this.channel = channel;
  }

  // -- ColorChooser API methods --

  /**
   * Displays a color selection dialog and returns the color
   *  selected by the user.
   */
  public Color getColor() {
    GenericDialog gd = new GenericDialog(title);
    gd.addSlider(makeLabel("Red:"), 0, 255, red);
    gd.addSlider(makeLabel("Green:"), 0, 255, green);
    gd.addSlider(makeLabel("Blue:"), 0, 255, blue);

    panel = new Panel();
    Dimension prefSize = new Dimension(100, 50);
    panel.setPreferredSize(prefSize);
    panel.setMinimumSize(prefSize);
    panel.setBackground(initialColor);
    gd.addPanel(panel, GridBagConstraints.CENTER, new Insets(10, 0, 0, 0));

    colors = WindowTools.getNumericFields(gd);
    for (int i=0; i<colors.size(); i++) {
      colors.get(i).addTextListener(this);
    }
    gd.showDialog();
    if (gd.wasCanceled()) return null;
    int red = (int) gd.getNextNumber();
    int green = (int) gd.getNextNumber();
    int blue = (int) gd.getNextNumber();
    return new Color(red, green, blue);
  }

  public void textValueChanged(TextEvent e) {
    int red = getColorValue(0);
    int green = getColorValue(1);
    int blue = getColorValue(2);
    panel.setBackground(new Color(red, green, blue));
    panel.repaint();
  }

  // -- Helper methods --

  private int getColorValue(int index) {
    int color = (int) Tools.parseDouble(colors.get(index).getText());
    if (color < 0) color = 0;
    if (color > 255) color = 255;
    return color;
  }

  private String makeLabel(String baseLabel) {
    return "Series_" + series + "_Channel_" + channel + "_" + baseLabel;
  }

}
