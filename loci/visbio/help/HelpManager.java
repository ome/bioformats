//
// HelpManager.java
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

package loci.visbio.help;

import java.awt.event.KeyEvent;

import java.util.Vector;

import javax.swing.KeyStroke;

import loci.visbio.LogicManager;
import loci.visbio.VisBioEvent;
import loci.visbio.VisBioFrame;
import loci.visbio.WindowManager;

import visad.util.Util;

/** HelpManager is the manager encapsulating VisBio's help window logic. */
public class HelpManager extends LogicManager {

  // -- Fields --

  /** Help dialog for detailing basic program usage. */
  private HelpWindow help;

  /** List of help topics. */
  private Vector topics;


  // -- Constructor --

  /** Constructs an exit manager. */
  public HelpManager(VisBioFrame bio) { super(bio); }


  // -- HelpManager API methods --

  /** Adds a new control panel help topic. */
  public void addHelpTopic(String name, String source) {
    addHelpTopic("Topics", name, source);
  }

  /** Adds a new help topic. */
  public void addHelpTopic(String menu,
    String name, String source)
  {
    addHelpTopic(menu, name, source, true);
  }

  /** Adds a new help topic. */
  public void addHelpTopic(String menu, String name,
    String source, boolean doMenuItem)
  {
    topics.add(name);
    help.addTab(name, source);
    if (doMenuItem) {
      bio.addMenuItem(menu, name,
        "loci.visbio.help.HelpManager.helpShow(" + name + ")", name.charAt(0));
    }
  }


  // -- LogicManager API methods --

  /** Called to notify the logic manager of a VisBio event. */
  public void doEvent(VisBioEvent evt) {
    int eventType = evt.getEventType();
    if (eventType == VisBioEvent.LOGIC_ADDED) {
      LogicManager lm = (LogicManager) evt.getSource();
      if (lm == this) doGUI();
    }
  }

  /** Gets the number of tasks required to initialize this logic manager. */
  public int getTasks() { return 2; }


  // -- Helper methods --

  /** Adds help-related GUI components to VisBio. */
  private void doGUI() {
    bio.setStatus("Initializing help logic");
    topics = new Vector();
    help = new HelpWindow();
    WindowManager wm = (WindowManager) bio.getManager(WindowManager.class);
    wm.addWindow(help);

    // help menu
    bio.setStatus(null);

    addHelpTopic("Help", "Overview", "overview.html");
    bio.getMenuItem("Help", "Overview").setAccelerator(
      KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));

    addHelpTopic("Help", "QuickTime", "quicktime.html");

    bio.addMenuSeparator("Help");
    bio.addSubMenu("Help", "Topics", 't');

    if (!VisBioFrame.MAC_OS_X) bio.addMenuSeparator("Help");
    addHelpTopic("Help", "About", "about.html", !VisBioFrame.MAC_OS_X);
  }


  // -- Menu commands --

  /** Brings up a window detailing basic program usage. */
  public void helpShow(String name) {
    final String fname = name;
    Util.invoke(false, new Runnable() {
      public void run() {
        int tab = -1;
        for (int i=0; i<topics.size(); i++) {
          String s = (String) topics.elementAt(i);
          if (s.equals(fname)) {
            tab = i;
            break;
          }
        }
        if (tab >= 0) {
          help.setTab(tab);
          WindowManager wm = (WindowManager)
            bio.getManager(WindowManager.class);
          wm.showWindow(help);
        }
      }
    });
  }

}
