//
// FlexWidgets.java
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
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import loci.formats.codec.LuraWaveCodec;

/**
 * Custom widgets for configuring Bio-Formats Flex support.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/config/FlexWidgets.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/config/FlexWidgets.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class FlexWidgets implements DocumentListener, IFormatWidgets {

  // -- Fields --

  private String[] labels;
  private Component[] widgets;

  private JTextField licenseBox;

  // -- Constructor --

  public FlexWidgets() {
    // get license code from ImageJ preferences
    String prefCode = Prefs.get(LuraWaveCodec.LICENSE_PROPERTY, null);
    String propCode = System.getProperty(LuraWaveCodec.LICENSE_PROPERTY);
    String code = "";
    if (prefCode != null) code = prefCode;
    else if (propCode != null) code = null; // hidden code

    String licenseLabel = "LuraWave license code";
    licenseBox = ConfigWindow.makeTextField();
    licenseBox.setText(code == null ? "(Licensed)" : code);
    licenseBox.setEditable(code != null);
    licenseBox.getDocument().addDocumentListener(this);

    labels = new String[] {licenseLabel};
    widgets = new Component[] {licenseBox};
  }

  // -- DocumentListener API methods --

  public void changedUpdate(DocumentEvent e) {
    documentUpdate(e);
  }
  public void removeUpdate(DocumentEvent e) {
    documentUpdate(e);
  }
  public void insertUpdate(DocumentEvent e) {
    documentUpdate(e);
  }

  // -- IFormatWidgets API methods --

  public String[] getLabels() {
    return labels;
  }

  public Component[] getWidgets() {
    return widgets;
  }

  // -- Helper methods --

  private void documentUpdate(DocumentEvent e) {
    String code = licenseBox.getText();
    Prefs.set(LuraWaveCodec.LICENSE_PROPERTY, code);
  }

}
