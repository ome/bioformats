//
// ConsoleManager.java
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

import java.io.PrintStream;
import javax.swing.JFrame;
import loci.visbio.state.BooleanOption;
import loci.visbio.state.OptionManager;
import loci.visbio.util.*;

/**
 * ConsoleManager is the manager encapsulating VisBio's console output logic.
 */
public class ConsoleManager extends LogicManager implements OutputListener {

  // -- Constants --

  /** String for automatically displaying console windows option. */
  private static final String AUTO_POPUP =
    "Pop up console windows whenever output is produced";

  /** String for debug mode option. */
  private static final String DEBUG_MODE =
    "Dump output to console rather than graphical windows";


  // -- Fields --

  /** Standard output console. */
  private OutputConsole out;

  /** Standard error console. */
  private OutputConsole err;

  /** The original output stream. */
  private PrintStream origOut;

  /** The original error stream. */
  private PrintStream origErr;

  /** Whether consoles should automatically be shown when output occurs. */
  private boolean autoPopup = true;

  /** Whether output should be dumped to the default console window. */
  private boolean debug;


  // -- Constructor --

  /** Constructs an exit manager. */
  public ConsoleManager(VisBioFrame bio) { super(bio); }


  // -- ConsoleManager API methods --

  /** Sets whether consoles are automatically shown when output occurs. */
  public void setAutoPopup(boolean autoPopup) { this.autoPopup = autoPopup; }

  /** Gets whether consoles are automatically shown when output occurs. */
  public boolean isAutoPopup() { return autoPopup; }

  /** Sets whether debugging mode is enabled. */
  public void setDebug(boolean debug) {
    if (this.debug == debug) return;
    this.debug = debug;

    if (debug) {
      System.setOut(origOut);
      System.setErr(origErr);
    }
    else {
      System.setOut(new PrintStream(out));
      System.setErr(new PrintStream(err));
    }
  }

  /** Gets whether debugging mode is enabled. */
  public boolean isDebug() { return debug; }


  // -- LogicManager API methods --

  /** Called to notify the logic manager of a VisBio event. */
  public void doEvent(VisBioEvent evt) {
    int eventType = evt.getEventType();
    if (eventType == VisBioEvent.LOGIC_ADDED) {
      Object src = evt.getSource();
      if (src == this) doGUI();
    }
    else if (eventType == VisBioEvent.STATE_CHANGED) {
      Object src = evt.getSource();
      if (src instanceof OptionManager) {
        OptionManager om = (OptionManager) src;
        BooleanOption autoPop = (BooleanOption) om.getOption(AUTO_POPUP);
        if (autoPop != null) setAutoPopup(autoPop.getValue());
        BooleanOption debugMode = (BooleanOption) om.getOption(DEBUG_MODE);
        if (debugMode != null) setDebug(debugMode.getValue());
      }
    }
  }

  /** Gets the number of tasks required to initialize this logic manager. */
  public int getTasks() { return 2; }


  // -- OutputListener API methods --

  /** Handles output and error console window updates. */
  public void outputProduced(OutputEvent e) {
    if (!autoPopup) return;
    Object src = e.getSource();
    JFrame frame = null;
    if (src == out) frame = out.getWindow();
    else if (src == err) frame = err.getWindow();
    if (frame != null && !frame.isVisible()) frame.show();
  }


  // -- Helper methods --

  /** Adds data-related GUI components to VisBio. */
  private void doGUI() {
    bio.setSplashStatus("Initializing console logic");
    out = new OutputConsole("Output Console");
    err = new OutputConsole("Error Console", "errors.log");

    origOut = System.out;
    origErr = System.err;
    if (!VisBioFrame.DEBUG) {
      System.setOut(new PrintStream(out));
      System.setErr(new PrintStream(err));
    }

    // listen for output produced within console windows
    out.addOutputListener(this);
    err.addOutputListener(this);

    // register console windows with window manager
    WindowManager wm = (WindowManager) bio.getManager(WindowManager.class);
    wm.addWindow(out.getWindow());
    wm.addWindow(err.getWindow());

    // options menu
    bio.setSplashStatus(null);
    OptionManager om = (OptionManager) bio.getManager(OptionManager.class);
    om.addBooleanOption("Debug", AUTO_POPUP, 'p',
      "Toggles whether output causes console windows to be shown", autoPopup);
    om.addBooleanOption("Debug", DEBUG_MODE, 'd',
      "Toggles whether output dumps to the default console", debug);

    // window menu
    bio.addMenuSeparator("Window");
    bio.addMenuItem("Window", "Output console",
      "loci.visbio.ConsoleManager.windowConsole(out)", 'o');
    bio.addMenuItem("Window", "Error console",
      "loci.visbio.ConsoleManager.windowConsole(err)", 'e');
  }


  // -- Menu commands --

  /** Displays the given output console window. */
  public void windowConsole(String console) {
    if (console.equals("err")) err.show();
    else if (console.equals("out")) out.show();
  }

}
