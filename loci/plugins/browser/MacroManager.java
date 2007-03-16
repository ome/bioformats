//
// MacroManager.java
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

package loci.plugins.browser;

import java.awt.*;
import java.util.*;
import ij.plugin.frame.Recorder;

public class MacroManager implements Runnable {

  // -- Fields --

  /** Stack of macros to execute on each slice. */
  private Vector macros;

  /** TextArea containing the recorded macros. */
  private TextArea textBox;

  /** Previous textBox contents. */
  private String previousText;

  /** Macro recorder. */
  private Recorder r;

  // -- Constructor --

  public MacroManager() {
    macros = new Vector();
    r = new Recorder();

    Component[] components = r.getComponents();
    for (int i=0; i<components.length; i++) {
      if (components[i] instanceof TextArea) {
        textBox = (TextArea) components[i];
        break; 
      }
    }
    r.setVisible(false);
  }

  // -- Runnable API methods --

  public void run() {
    while (true) {
      String cmds = textBox.getText();
      if (previousText == null || !cmds.equals(previousText)) {
        StringTokenizer st = new StringTokenizer(cmds, "\n");
        for (int i=0; i<macros.size(); i++) st.nextToken();
        while (st.hasMoreTokens()) {
          String macro = st.nextToken(); 
          if (macro.equals("run(\"Undo\")")) {
            macros.removeElementAt(macros.size() - 1);
          }
          else macros.add(macro);
        }
        previousText = cmds; 
      }
      try {
        Thread.sleep(50);
      }
      catch (InterruptedException exc) { exc.printStackTrace(); }
    }
  }

  // -- MacroManager API methods --

  public Vector getMacros() {
    return macros;
  }
}
