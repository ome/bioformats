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

import loci.visbio.state.BooleanOption;
import loci.visbio.state.OptionManager;

/**
 * ClassManager is the manager for preloading classes
 * to speed VisBio response time later on.
 */
public class ClassManager extends LogicManager {

  // -- Constants --

  /** String for preload classes option. */
  public static final String PRELOAD = "Preload classes";


  // -- Fields --

  /** Whether class preloading is enabled. */
  protected boolean preload = true;

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
    else if (eventType == VisBioEvent.STATE_CHANGED) {
      Object src = evt.getSource();
      if (src instanceof OptionManager) {
        OptionManager om = (OptionManager) src;
        BooleanOption option = (BooleanOption) om.getOption(PRELOAD);
        setPreloadClasses(option.getValue());
      }
    }
  }

  /** Gets the number of tasks required to initialize this logic manager. */
  public int getTasks() { return 1 + (preload ? preloadClasses.size() : 0); }


  // -- Helper methods --

  /** Adds system-related GUI components to VisBio. */
  private void doGUI() {
    // options menu
    bio.setSplashStatus(null);
    OptionManager om = (OptionManager) bio.getManager(OptionManager.class);
    om.addBooleanOption("Debug", PRELOAD, 'p',
      "Preloading classes causes VisBio to start more slowly, " +
      "but respond more quickly once loaded", preload);

    // preload a bunch of classes if option is set
    if (preload) {
      int size = preloadClasses.size();
      for (int i=0; i<size; i++) {
        String className = (String) preloadClasses.elementAt(i);
        bio.setSplashStatus(i == 0 ? "Preloading classes" : null);
        preload(className, false);
      }
    }
  }

  /** Sets whether class preloading is enabled. */
  protected void setPreloadClasses(boolean preload) {
    if (this.preload == preload) return;
    this.preload = preload;
  }

}
