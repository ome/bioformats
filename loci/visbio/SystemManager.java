//
// SystemManager.java
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

import com.jgoodies.plaf.LookUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Vector;
import javax.swing.Timer;
import loci.visbio.help.HelpManager;
import visad.util.Util;

/**
 * SystemManager is the manager encapsulating
 * VisBio's system information report logic.
 */
public class SystemManager extends LogicManager
  implements ActionListener, Runnable
{

  // -- Control panel --

  /** System control panel. */
  private SystemControls systemControls;


  // -- Constructor --

  /** Constructs a system manager. */
  public SystemManager(VisBioFrame bio) { super(bio); }


  // -- SystemManager API methods --

  /** Gets a string detailing current memory usage. */
  public String getMemoryUsage() {
    Runtime runtime = Runtime.getRuntime();
    long total = runtime.totalMemory();
    long free = runtime.freeMemory();
    long used = total - free;
    long memUsed = used >> 20;
    long memTotal = total >> 20;
    return memUsed + " MB used (" + memTotal + " MB reserved)";
  }

  /** Gets maximum amount of memory available to VisBio in megabytes. */
  public int getMaximumMemory() {
    return (int) (Runtime.getRuntime().maxMemory() / 1048376);
  }

  /** Calls the Java garbage collector to free wasted memory. */
  public void cleanMemory() { Util.invoke(false, this); }

  /**
   * Updates the VisBio launch parameters to specify the given
   * maximum heap and look and feel settings.
   */
  public void writeScript(int heap, String laf) {
    // a platform-dependent mess!
    String filename;
    if (LookUtils.IS_OS_WINDOWS) filename = "launcher.cfg";
    else if (LookUtils.IS_OS_MAC) filename = "VisBio.app/Contents/Info.plist";
    else filename = "visbio";

    // read in the VisBio startup script
    Vector lines = new Vector();
    try {
      BufferedReader fin = new BufferedReader(new FileReader(filename));
      while (true) {
        String line = fin.readLine();
        if (line == null) break;
        lines.add(line);
      }
      fin.close();
    }
    catch (IOException exc) { exc.printStackTrace(); }

    // alter settings in VisBio startup script
    PrintWriter fout = null;
    int size = 0;
    try {
      fout = new PrintWriter(new FileWriter(filename));
      size = lines.size();
    }
    catch (IOException exc) { exc.printStackTrace(); }

    boolean heapChanged = heap < 0;
    boolean lafChanged = laf == null;

    for (int i=0; i<size; i++) {
      String line = (String) lines.elementAt(i);

      if (heap >= 0) {
        // adjust maximum heap setting
        String heapString = "mx";
        int heapPos = line.indexOf(heapString);
        if (heapPos >= 0) {
          int space = line.indexOf(" ", heapPos);
          if (space >= 0) {
            line = line.substring(0, heapPos + heapString.length()) +
              heap + "m" + line.substring(space);
            heapChanged = true;
          }
        }
      }

      if (laf != null) {
        // check for L&F setting
        String lafString = "LookAndFeel";
        int lafPos = line.indexOf(lafString);
        if (lafPos >= 0) {
          int start = line.lastIndexOf(
            LookUtils.IS_OS_MAC ? ">" : "=", lafPos);
          if (start >= 0) {
            line = line.substring(0, start + 1) + laf +
              line.substring(lafPos + 11);
            lafChanged = true;
          }
        }
      }

      fout.println(line);
    }
    fout.close();

    if (!heapChanged) {
      System.err.println("Warning: no maximum heap setting found " +
        "in launch script " + filename + ".");
    }
    if (!lafChanged) {
      System.err.println("Warning: no Look & Feel setting found " +
        "in launch script " + filename + ".");
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


  // -- ActionListener API methods --

  /** Outputs current RAM usage to console. */
  public void actionPerformed(ActionEvent e) {
    System.out.println(System.currentTimeMillis() + ": " + getMemoryUsage());
  }


  // -- Runnable API methods --

  /** Performs garbage collection, displaying a wait cursor while doing so. */
  public void run() {
    WindowManager wm = (WindowManager) bio.getManager(WindowManager.class);
    wm.setWaitCursor(true);
    gc();
    wm.setWaitCursor(false);
  }


  // -- Helper methods --

  /** Adds system-related GUI components to VisBio. */
  private void doGUI() {
    // control panel
    bio.setSplashStatus("Initializing system information logic");
    systemControls = new SystemControls(this);
    PanelManager pm = (PanelManager) bio.getManager(PanelManager.class);
    pm.addPanel(systemControls);

    // help window
    bio.setSplashStatus(null);
    HelpManager hm = (HelpManager) bio.getManager(HelpManager.class);
    hm.addHelpTopic("Control panels/System panel", "system_panel.html");

    // RAM usage debugging output
    if (VisBioFrame.DEBUG) new Timer(500, this).start();
  }


  // -- Utility methods --

  /** Does some garbage collection, to free up memory. */
  public static void gc() {
    try {
      System.gc();
      Thread.sleep(100);
      System.runFinalization();
      Thread.sleep(100);
      System.gc();
      Thread.sleep(100);
      System.runFinalization();
      Thread.sleep(100);
    }
    catch (InterruptedException exc) { exc.printStackTrace(); }
  }

}
