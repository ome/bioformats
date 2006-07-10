//
// LociOpener.java
//

// derived from Sun's FileChooserDemo2

package loci.browser;

import ij.*;
import ij.io.OpenDialog;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LociOpener implements ItemListener {

  private String filename;
  private String dir;
  private boolean virtual;
  private String absPath;

  public LociOpener() {
    // set up the file chooser
    JFileChooser fc = new JFileChooser(OpenDialog.getDefaultDirectory());

    // add a custom file filter
    fc.addChoosableFileFilter(new ImageFilter());

    // add the preview pane
    JPanel p = new JPanel(new BorderLayout());
    JCheckBox c = new JCheckBox("Use virtual stack");
    p.add(new ImagePreview(fc), BorderLayout.CENTER);
    p.add(c, BorderLayout.SOUTH);
    c.addItemListener(this);
    fc.setAccessory(p);

    // show it
    int returnVal = fc.showDialog(null, "Open");

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

}
