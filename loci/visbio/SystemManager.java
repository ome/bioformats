//
// SystemManager.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-@year@ Curtis Rueden.

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
import java.awt.event.*;
import java.io.*;
import java.util.Vector;
import javax.swing.*;
import loci.visbio.help.HelpManager;
import visad.util.Util;

/**
 * SystemManager is the manager encapsulating
 * VisBio's system information report logic.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/SystemManager.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/SystemManager.java">SVN</a></dd></dl>
 */
public class SystemManager extends LogicManager
  implements ActionListener, Runnable
{

  // -- Control panel --

  /** System information control panel. */
  private SystemControls systemControls;

  /** JFrame containing system information control panel. */
  private JFrame systemFrame;

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
  public void writeScript(int heap, String laf, String j3d) {
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
    boolean j3dChanged = j3d == null;

    for (int i=0; i<size; i++) {
      String line = (String) lines.elementAt(i);

      if (heap >= 0) {
        // adjust maximum heap setting
        String heapString = "mx";
        int heapPos = line.indexOf(heapString);
        if (heapPos >= 0) {
          int end = line.indexOf("m", heapPos + 1);
          if (end >= 0) {
            line = line.substring(0, heapPos + heapString.length()) +
              heap + line.substring(end);
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

      if (j3d != null) {
        // check for J3D renderer setting
        String j3dString = "j3d.rend";
        int j3dPos = line.indexOf(j3dString);
        if (j3dPos >= 0) {
          int start = line.indexOf("=", j3dPos);
          int end = line.indexOf(" ", start);
          if (start >= 0) {
            line = line.substring(0, start + 1) + j3d + line.substring(end);
            j3dChanged = true;
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

  /** Detects whether VisBio was launched with Java Web Start. */
  public boolean isJNLP() { return System.getProperty("jnlpx.home") != null; }

  /** Gets associated control panel. */
  public SystemControls getControls() {
    if (systemControls == null) {
      // control panel construction delayed until
      // first use, to improve program startup speed
      WindowManager wm = (WindowManager) bio.getManager(WindowManager.class);
      wm.setWaitCursor(true);

      // control panel
      systemControls = new SystemControls(this);
      systemFrame = new JFrame("System Information");
      systemFrame.getContentPane().add(systemControls);

      // register system information window with window manager
      wm.addWindow(systemFrame);
      wm.setWaitCursor(false);
    }
    return systemControls;
  }

  // -- Menu commands --

  /** Displays the system information window. */
  public void showSystemInfo() {
    getControls();
    WindowManager wm = (WindowManager) bio.getManager(WindowManager.class);
    wm.showWindow(systemFrame);
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
    // menu items
    bio.setSplashStatus("Initializing system information window");
    JMenuItem system = bio.addMenuItem("Window", "System information",
      "loci.visbio.SystemManager.showSystemInfo", 'i');
    system.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
    bio.addMenuSeparator("Window");

    // help window
    bio.setSplashStatus(null);
    HelpManager hm = (HelpManager) bio.getManager(HelpManager.class);
    String s = "Control panels/System panel";
    hm.addHelpTopic(s, "system_panel.html");
    hm.addHelpTopic(s + "/Changing the memory limit", "memory_limit.html");
    hm.addHelpTopic(s + "/Changing VisBio's appearance", "look_and_feel.html");
    hm.addHelpTopic(s + "/Changing the renderer", "renderer.html");

    // RAM usage debugging output
    if (VisBioFrame.DEBUG) new Timer(500, this).start();
  }

  // -- Utility methods --

  /** Does some garbage collection, to free up memory. */
  public static void gc() {
    try {
      for (int i=0; i<2; i++) {
        System.gc();
        Thread.sleep(100);
        System.runFinalization();
        Thread.sleep(100);
      }
    }
    catch (InterruptedException exc) { exc.printStackTrace(); }
  }

}
