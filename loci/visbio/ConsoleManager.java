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

import loci.visbio.util.OutputConsole;

/**
 * ConsoleManager is the manager encapsulating VisBio's console output logic.
 */
public class ConsoleManager extends LogicManager {

  // -- Constants --

  /** String for debug mode option. */
  private static final String DEBUG_MODE = "Debug mode";


  // -- Fields --

  /** Standard output console. */
  private OutputConsole out;

  /** Standard error console. */
  private OutputConsole err;

  /** The original output stream. */
  private PrintStream origOut;

  /** The original error stream. */
  private PrintStream origErr;

  /** Whether output should be dumped to the default console window. */
  private boolean debug;


  // -- Constructor --

  /** Constructs an exit manager. */
  public ConsoleManager(VisBioFrame bio) { super(bio); }


  // -- ConsoleManager API methods --

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
        BooleanOption option = (BooleanOption) om.getOption(DEBUG_MODE);
        setDebug(option.getValue());
      }
    }
  }

  /** Gets the number of tasks required to initialize this logic manager. */
  public int getTasks() { return 2; }


  // -- Helper methods --

  /** Adds data-related GUI components to VisBio. */
  private void doGUI() {
    bio.setStatus("Initializing console logic");
    out = new OutputConsole((JFrame) null, "Output Console");
    err = new OutputConsole((JFrame) null, "Error Console", "errors.log");

    origOut = System.out;
    origErr = System.err;
    if (!VisBioFrame.DEBUG) {
      System.setOut(new PrintStream(out));
      System.setErr(new PrintStream(err));
    }

    // register console windows with window manager
    WindowManager wm = (WindowManager) bio.getManager(WindowManager.class);
    wm.addWindow(out.getDialog());
    wm.addWindow(err.getDialog());

    // options menu
    bio.setStatus(null);
    OptionManager om = (OptionManager) bio.getManager(OptionManager.class);
    om.addBooleanOption("General", DEBUG_MODE, 'd',
      "Toggles whether output dumps to the default console", false);

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
