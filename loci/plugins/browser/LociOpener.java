//
// LociOpener.java
//

/*
LOCI 4D Data Browser plugin for quick browsing of 4D datasets in ImageJ.
Copyright (C) 2005-@year@ Christopher Peterson, Francis Wong, Curtis Rueden
and Melissa Linkert.

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

// derived from Sun's FileChooserDemo2

package loci.plugins.browser;

import ij.Macro;
import ij.io.OpenDialog;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import javax.swing.*;
import loci.formats.*;
import loci.formats.gui.GUITools;

public class LociOpener implements ItemListener {

  private String filename;
  private String dir;
  private boolean virtual;
  private String absPath;
  private int returnVal;
  private JFileChooser fc;

  public LociOpener() {
    // set up the file chooser
    ImageReader ir = new ImageReader();
    fc = GUITools.buildFileChooser(ir);
    String ijDir = OpenDialog.getDefaultDirectory();
    if (ijDir != null) fc.setCurrentDirectory(new File(ijDir));

    // add the preview pane
    JPanel p = new JPanel(new BorderLayout());
    JCheckBox c = new JCheckBox("Use virtual stack");
    p.add(new ImagePreview(fc, ir), BorderLayout.CENTER);
    p.add(c, BorderLayout.SOUTH);
    c.addItemListener(this);
    c.setSelected(true);
    fc.setAccessory(p);
  }

  public void show() {
    returnVal = fc.showDialog(null, "Open");

    // process the results
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      filename = file.getName();
      dir = fc.getCurrentDirectory().getPath();
      OpenDialog.setDefaultDirectory(dir);
      absPath = file.getAbsolutePath();
      dir += File.separator;
    }
    else Macro.abort();
    // reset the file chooser for the next time it's shown
    fc.setSelectedFile(null);
  }

  public void itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) virtual = true;
    else virtual = false;
  }

  public String getDirectory() { return dir; }

  public String getFileName() { return filename; }

  public boolean getVirtual() { return virtual; }

  public String getAbsolutePath() { return absPath; }

  public boolean isCanceled() {
    return returnVal == JFileChooser.CANCEL_OPTION;
  }

}
