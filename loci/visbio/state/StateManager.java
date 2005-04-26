//
// StateManager.java
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

package loci.visbio.state;

import java.awt.event.KeyEvent;
import java.io.*;
import java.io.IOException;
import java.util.*;
import javax.swing.*;
import loci.visbio.*;
import loci.visbio.util.SwingUtil;
import visad.util.ExtensionFileFilter;

/** StateManager is the manager encapsulating VisBio's state logic. */
public class StateManager extends LogicManager {

  // -- Constants --

  /** Extension for VisBio state files. */
  protected static final String STATE_EXTENSION = "xml";


  // -- Fields --

  /** File chooser for state saves. */
  protected JFileChooser stateBox;

  /** The Undo menu item from the Edit menu. */
  protected JMenuItem editUndo;

  /** The Redo menu item from the Edit menu. */
  protected JMenuItem editRedo;

  /** Temporary file for storing temporary state information. */
  private File state;

  /** Stack of old states for multiple undo. */
  private Stack undoStates;

  /** Current program state. */
  private ProgramState currentState;

  /** Stack of new states for multiple redo. */
  private Stack redoStates;

  /** Initial program state. */
  private ProgramState initialState;

  /** Is state currently being restored? */
  private boolean restoring;

  /** Has the user saved the most recent state? */
  private boolean saved = true;


  // -- Constructors --

  /** Constructs a VisBio state management object. */
  public StateManager(VisBioFrame bio) { this(bio, "visbio.tmp"); }

  /** Constructs a VisBio state management object. */
  public StateManager(VisBioFrame bio, String state) {
    super(bio);
    this.state = new File(state);
  }


  // -- StateManager API methods --

  /** Sets whether state is currently being restored. */
  public void setRestoring(boolean restoring) { this.restoring = restoring; }

  /** Gets whether state is currently being restored. */
  public boolean isRestoring() { return restoring; }

  /** Saves the current state to the given state file. */
  public void saveState(File file) {
    try {
      PrintWriter fout = new PrintWriter(new FileWriter(file));
      saveState(fout);
    }
    catch (IOException exc) { exc.printStackTrace(); }
    catch (SaveException exc) { exc.printStackTrace(); }
  }

  /** Restores the current state from the given state file. */
  public void restoreState(File file) {
    try {
      BufferedReader fin = new BufferedReader(new FileReader(file));
      restoreState(fin);
    }
    catch (IOException exc) { exc.printStackTrace(); }
    catch (SaveException exc) { exc.printStackTrace(); }
    bio.generateEvent(this, "restore state", true);
  }

  /**
   * Saves an initial VisBio state, then checks to see if VisBio crashed last
   * time, and if so, asks the user whether to restore the previous state.
   */
  public void checkCrash() {
    boolean crashed = false;
    /* CTR TODO for v3.00 final
    boolean crashed = state.exists();
    if (crashed) {
      int ans = JOptionPane.showConfirmDialog(bio,
        "It appears that VisBio crashed last time. " +
        "Attempt to restore the previous state?", "VisBio",
        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
      if (ans != JOptionPane.YES_OPTION) crashed = false;
    }
    */
    saveState("", true, crashed); // compute currentState variable
    initialState = currentState;
    if (crashed) restoreState(state);
  }

  /**
   * Checks whether the program state has been saved,
   * and if not, prompts the user to save.
   */
  public boolean checkSave() {
    if (saved) return true;
    /* CTR TODO for v3.00 final
    int ans = JOptionPane.showConfirmDialog(bio,
      "Program state has been changed. Save before exiting?", "VisBio",
      JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (ans == JOptionPane.CANCEL_OPTION) return false;
    if (ans == JOptionPane.YES_OPTION) fileSave();
    */
    return true;
  }

  /** Deletes VisBio state temp file. */
  public void destroy() { if (state.exists()) state.delete(); }


  // -- Menu commands --

  /** Restores the current state from a text file specified by the user. */
  public void fileRestore() {
    int rval = stateBox.showOpenDialog(bio);
    if (rval == JFileChooser.APPROVE_OPTION) {
      final WindowManager wm = (WindowManager)
        bio.getManager(WindowManager.class);
      wm.setWaitCursor(true);
      JProgressBar status = bio.getProgressBar();
      status.setString("Restoring state...");
      status.setIndeterminate(true);
      Thread restoreThread = new Thread(new Runnable() {
        public void run() {
          restoreState(stateBox.getSelectedFile());
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              bio.resetStatus();
              wm.setWaitCursor(false);
            }
          });
        }
      });
      restoreThread.start();
    }
  }

  /** Saves the current state to a text file specified by the user. */
  public void fileSave() {
    int rval = stateBox.showSaveDialog(bio);
    if (rval == JFileChooser.APPROVE_OPTION) {
      WindowManager wm = (WindowManager) bio.getManager(WindowManager.class);
      wm.setWaitCursor(true);
      File file = stateBox.getSelectedFile();
      if (file.getName().indexOf(".") < 0) {
        file = new File(file.getAbsolutePath() + "." + STATE_EXTENSION);
      }
      saveState(file);
      wm.setWaitCursor(false);
      saved = true;
    }
  }

  /** Undoes the last action taken. */
  public void editUndo() {
    if (undoStates.isEmpty()) return;
    redoStates.push(currentState);
    currentState = (ProgramState) undoStates.pop();
    restoreState(currentState);
    updateMenuItems();
  }

  /** Redoes the last action undone. */
  public void editRedo() {
    if (redoStates.isEmpty()) return;
    undoStates.push(currentState);
    currentState = (ProgramState) redoStates.pop();
    restoreState(currentState);
    updateMenuItems();
  }

  /** Resets the program to its initial state. */
  public void editReset() {
    int ans = JOptionPane.showConfirmDialog(bio,
      "Are you sure you want to reset VisBio to its initial state?", "VisBio",
      JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (ans != JOptionPane.YES_OPTION) return;
    restoreState(initialState);
    bio.generateEvent(this, "reset state", true);
  }


  // -- Saveable API methods --

  /** Writes the current state to the given writer. */
  public void saveState(PrintWriter out) throws SaveException {
    LogicManager[] lm = bio.getManagers();
    for (int i=0; i<lm.length; i++) {
      if (lm[i] == this) continue; // bad recursion, no cookie
      lm[i].saveState(out);
    }
  }

  /** Restores the current state from the given reader. */
  public void restoreState(BufferedReader in) throws SaveException {
    restoring = true;
    try {
      LogicManager[] lm = bio.getManagers();
      for (int i=0; i<lm.length; i++) {
        if (lm[i] == this) continue; // bad recursion, no cookie
        lm[i].restoreState(in);
      }
    }
    finally { restoring = false; }
  }


  // -- LogicManager API methods --

  /** Called to notify the logic manager of a VisBio event. */
  public void doEvent(VisBioEvent evt) {
    int eventType = evt.getEventType();
    if (eventType == VisBioEvent.LOGIC_ADDED) {
      Object src = evt.getSource();
      if (src == this) doGUI();
      else if (src instanceof ExitManager) {
        // HACK - make state logic menu items appears in the proper location

        // file menu
        bio.addMenuSeparator("File");
        // CTR TEMP menu item disabled for beta release
        bio.addMenuItem("File", "Restore state...",
          "loci.visbio.state.StateManager.fileRestore", 'r').setEnabled(false);
        bio.addMenuItem("File", "Save state...",
          "loci.visbio.state.StateManager.fileSave", 's').setEnabled(false);
      }
    }
    else if (eventType == VisBioEvent.STATE_CHANGED) {
      Object src = evt.getSource();
      if (src instanceof ExitManager) {
        String msg = evt.getMessage();
        if (msg.equals("shutdown request")) {
          if (!checkSave()) ((ExitManager) src).cancelShutdown();
        }
        else if (msg.equals("shutdown")) destroy();
      }
      else if (evt.isUndoable() && !restoring) {
        saved = false;
        saveState(evt.getMessage(), false, false);
      }
    }
  }

  /** Gets the number of tasks required to initialize this logic manager. */
  public int getTasks() { return 3; }


  // -- Helper methods --

  /** Adds state-related GUI components to VisBio. */
  private void doGUI() {
    // save state file chooser
    bio.setSplashStatus("Initializing state logic");
    stateBox = new JFileChooser();
    stateBox.addChoosableFileFilter(new ExtensionFileFilter(
      STATE_EXTENSION, "VisBio state files"));

    // edit menu
    bio.setSplashStatus(null);
    editUndo = bio.addMenuItem("Edit", "Undo",
      "loci.visbio.state.StateManager.editUndo", 'u');
    SwingUtil.setMenuShortcut(bio, "Edit", "Undo", KeyEvent.VK_Z);
    editRedo = bio.addMenuItem("Edit", "Redo",
      "loci.visbio.state.StateManager.editRedo", 'r');
    SwingUtil.setMenuShortcut(bio, "Edit", "Redo", KeyEvent.VK_Y);
    JMenuItem editReset = bio.addMenuItem("Edit", "Reset",
      "loci.visbio.state.StateManager.editReset", 't');
    SwingUtil.setMenuShortcut(bio, "Edit", "Reset", KeyEvent.VK_R);

    // CTR TEMP menu items disabled for beta release
    editUndo.setEnabled(false);
    editRedo.setEnabled(false);

    // undo logic variables
    bio.setSplashStatus(null);
    undoStates = new Stack();
    redoStates = new Stack();
  }

  /** Restores the state from the given program state object. */
  private void restoreState(ProgramState ps) {
    try { restoreState(new BufferedReader(new StringReader(ps.state))); }
    catch (SaveException exc) { exc.printStackTrace(); }
  }

  /** Saves the state to the undo stack and the VisBio state temp file. */
  private void saveState(String msg, boolean init, boolean crashed) {
    // capture save state results to a string
    StringWriter sw = new StringWriter();
    try { saveState(new PrintWriter(sw)); }
    catch (SaveException exc) { exc.printStackTrace(); }
    String stateString = sw.toString();

    if (!crashed) {
      // write captured results to the state file
      try {
        PrintWriter fout = new PrintWriter(new FileWriter(state.getPath()));
        fout.println(stateString);
        fout.close();
      }
      catch (IOException exc) { exc.printStackTrace(); }
    }

    if (!init) {
      // update multiple undo stacks
      undoStates.push(currentState);
      redoStates.removeAllElements();
    }
    currentState = new ProgramState(msg, stateString);
    updateMenuItems();
  }

  /** Updates the Edit menu's Undo and Redo menu items. */
  private void updateMenuItems() {
    if (undoStates.isEmpty()) {
      editUndo.setText("Undo");
      editUndo.setEnabled(false);
    }
    else {
      editUndo.setText("Undo " + currentState.msg);
      // CTR TEMP menu item disabled for beta release
      //editUndo.setEnabled(true);
    }
    if (redoStates.isEmpty()) {
      editRedo.setText("Redo");
      editRedo.setEnabled(false);
    }
    else {
      editRedo.setText("Redo " + ((ProgramState) redoStates.peek()).msg);
      // CTR TEMP menu item disabled for beta release
      //editRedo.setEnabled(true);
    }
  }


  // -- Utility methods --

  /**
   * Merges states between two lists of dynamic objects.
   * The procedure is as follows:
   * <ol>
   * <li>Compare read object list with existing object list
   * <li>Find all matches between the two lists
   * <li>Reuse as many leftover existing objects as possible,
   *     initializing them to match compatible read objects
   * <li>If there are still leftover existing objects, discard them
   * <li>If there are still leftover read objects, initialize them
   * </ol>
   *
   * See the {@link loci.visbio.state.Dynamic} documentation
   * for more information.
   */
  public static void mergeStates(Vector oldList, Vector newList) {
    int osize = oldList.size();
    int nsize = newList.size();
    int[] oldIndex = new int[osize];
    int[] newIndex = new int[nsize];
    Arrays.fill(oldIndex, -1);
    Arrays.fill(newIndex, -1);

    // find all matches between the two lists
    for (int n=0; n<nsize; n++) {
      Dynamic newDyn = (Dynamic) newList.elementAt(n);
      for (int o=0; o<osize; o++) {
        if (oldIndex[o] >= 0) continue;
        Dynamic oldDyn = (Dynamic) oldList.elementAt(o);
        if (newDyn.matches(oldDyn)) {
          oldIndex[o] = n;
          newIndex[n] = o;
          // discard new object in favor of matching old one
          newDyn.discard();
          newList.setElementAt(oldDyn, n);
          break;
        }
      }
    }

    // initialize states between overlapping leftover objects
    for (int o=0; o<osize; o++) {
      if (oldIndex[o] >= 0) continue;
      Dynamic oldDyn = (Dynamic) oldList.elementAt(o);
      for (int n=0; n<nsize; n++) {
        if (newIndex[n] >= 0) continue;
        Dynamic newDyn = (Dynamic) newList.elementAt(n);
        if (!oldDyn.isCompatible(newDyn)) continue;
        oldDyn.initState(newDyn);
        oldIndex[o] = n;
        newIndex[n] = o;
        // discard new object in favor of reinitialized old one
        newDyn.discard();
        newList.setElementAt(oldDyn, n);
        break;
      }
    }

    // discard remaining old objects
    for (int o=0; o<osize; o++) {
      if (oldIndex[o] >= 0) continue;
      Dynamic oldDyn = (Dynamic) oldList.elementAt(o);
      oldDyn.discard();
    }

    // initialize remaining new objects
    for (int n=0; n<nsize; n++) {
      if (newIndex[n] >= 0) continue;
      Dynamic newDyn = (Dynamic) newList.elementAt(n);
      newDyn.initState(null);
    }
  }

}
