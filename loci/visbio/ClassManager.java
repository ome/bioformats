//
// ClassManager.java
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

package loci.visbio;

import java.io.*;
import java.util.Vector;

/**
 * ClassManager is the manager for preloading classes
 * to speed VisBio response time later on.
 */
public class ClassManager extends LogicManager {

  // -- Fields --

  /** List of classes to preload. */
  protected Vector preloadClasses;


  // -- Constructor --

  /** Constructs a class manager. */
  public ClassManager(VisBioFrame bio) {
    super(bio);

    // extract classes to preload from data file
    preloadClasses = new Vector();
    try {
      BufferedReader fin = new BufferedReader(new FileReader("classes.txt"));
      while (true) {
        String line = fin.readLine();
        if (line == null) break; // eof
        preloadClasses.add(line);
      }
      fin.close();
    }
    catch (IOException exc) { } // ignore data file I/O errors
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
  public int getTasks() { return preloadClasses.size(); }


  // -- Helper methods --

  /** Adds system-related GUI components to VisBio. */
  private void doGUI() {
    // preload a bunch of classes
    int size = preloadClasses.size();
    String pkg = "";
    for (int i=0; i<size; i++) {
      String className = (String) preloadClasses.elementAt(i);
      int dot = className.lastIndexOf(".");
      String prefix = className.substring(0, dot);
      if (!prefix.equals(pkg)) {
        pkg = prefix;
        bio.setSplashStatus("Loading " + pkg);
      }
      else bio.setSplashStatus(null);

      // preload class, ignoring errors
      try { Class.forName(className); }
      catch (ClassNotFoundException exc) { }
    }
  }

}
