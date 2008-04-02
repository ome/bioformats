//
// PictWidgets.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Data Browser, Stack Colorizer,
Stack Slicer, and OME plugins. Copyright (C) 2005-@year@ Melissa Linkert,
Curtis Rueden, Christopher Peterson and Philip Huettl.

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

package loci.plugins.config;

import ij.Prefs;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;
import loci.plugins.Util;

/**
 * Custom widgets for configuring Bio-Formats PICT support.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/config/PictWidgets.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/config/PictWidgets.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class PictWidgets implements IFormatWidgets, ItemListener {

  // -- Fields --

  private String[] labels;
  private Component[] widgets;

  // -- Constructor --

  public PictWidgets() {
    boolean qtJava = Prefs.get(Util.PREF_PICT_QTJAVA, false);

    String legacyLabel = "Legacy";
    JCheckBox legacyBox = new JCheckBox(
      "Use QTJava instead of native PICT support", qtJava);
    legacyBox.addItemListener(this);
    legacyBox.setEnabled(false);//TEMP

    labels = new String[] {legacyLabel};
    widgets = new Component[] {legacyBox};
  }

  // -- IFormatWidgets API methods --

  public String[] getLabels() {
    return labels;
  }

  public Component[] getWidgets() {
    return widgets;
  }

  // -- ItemListener API methods --

  public void itemStateChanged(ItemEvent e) {
    JCheckBox box = (JCheckBox) e.getSource();
    Prefs.set(Util.PREF_PICT_QTJAVA, box.isSelected());
  }

}
