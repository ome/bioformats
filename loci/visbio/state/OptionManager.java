//
// OptionManager.java
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

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JCheckBox;
import loci.ome.xml.OMEElement;
import loci.visbio.*;
import loci.visbio.util.WarningPane;
import org.xml.sax.SAXException;

/** OptionManager is the manager encapsulating VisBio's options. */
public class OptionManager extends LogicManager {

  // -- Constants --

  /** Configuration file for storing VisBio options. */
  private static final String CONFIG_FILE = "visbio.ini";


  // -- Fields --

  /** Option pane. */
  private OptionPane options;

  /** List of options. */
  private Vector list;


  // -- Constructor --

  /** Constructs an options manager. */
  public OptionManager(VisBioFrame bio) {
    super(bio);
    options = new OptionPane(this);
    list = new Vector();

    // ensure tabs appear in the right order
    options.addTab("General");
    options.addTab("Warnings");
    options.addTab("Debug");
  }


  // -- OptionManager API methods --

  /** Adds an option to VisBio's options dialog. */
  public void addBooleanOption(String tab,
    String text, char mnemonic, String tip, boolean value)
  {
    BooleanOption option = new BooleanOption(text, mnemonic, tip, value);
    options.addOption(tab, option);
    list.add(option);
    bio.generateEvent(this, "add option", false);
  }

  /** Adds an option allowing the user to enter a numerical value. */
  public void addNumericOption(String tab,
    String text, String unit, String tip, int value)
  {
    NumericOption option = new NumericOption(text, unit, tip, value);
    options.addOption(tab, option);
    list.add(option);
    bio.generateEvent(this, "add option", false);
  }

  /** Adds an option allowing the user to select from a dropdown list. */
  public void addListOption(String tab,
    String text, String tip, String[] choices)
  {
    ListOption option = new ListOption(text, tip, choices);
    options.addOption(tab, option);
    list.add(option);
    bio.generateEvent(this, "add option", false);
  }

  /**
   * Adds a custom GUI component to VisBio's options dialog.
   * Such options will not be saved in the INI file automatically.
   */
  public void addCustomOption(String tab, Component c) {
    CustomOption option = new CustomOption(c);
    options.addOption(tab, option);
    bio.generateEvent(this, "add option", false);
  }

  /** Gets the VisBio option with the given text. */
  public BioOption getOption(String text) {
    for (int i=0; i<list.size(); i++) {
      BioOption option = (BioOption) list.elementAt(i);
      if (option.getText().equals(text)) return option;
    }
    return null;
  }

  /** Reads in configuration from configuration file. */
  public void readIni() {
    if (!new File(CONFIG_FILE).exists()) return;
    OMEElement ome = new OMEElement();
    try { ome.readXML(CONFIG_FILE); }
    catch (IOException exc) { exc.printStackTrace(); }
    catch (SAXException exc) { exc.printStackTrace(); }
    try {
      restoreState(ome);
      bio.generateEvent(this, "read ini file", false);
    }
    catch (SaveException exc) { exc.printStackTrace(); }
  }

  /** Writes out configuration to configuration file. */
  public void writeIni() {
    OMEElement ome = new OMEElement();
    try { saveState(ome); }
    catch (SaveException exc) { exc.printStackTrace(); }
    try { ome.printXML(CONFIG_FILE); }
    catch (IOException exc) { exc.printStackTrace(); }
  }

  /** Checks whether to display a warning, and does so if necessary. */
  public boolean checkWarning(String warn, boolean allowCancel, String text) {
    BioOption option = getOption(warn);
    if (!(option instanceof BooleanOption)) return true;
    BooleanOption alwaysOption = (BooleanOption) option;
    if (!alwaysOption.getValue()) return true;
    JCheckBox alwaysBox = (JCheckBox) alwaysOption.getComponent();

    WarningPane pane = new WarningPane(text, allowCancel);
    boolean success = pane.showDialog(bio) == WarningPane.APPROVE_OPTION;

    boolean always = pane.isAlwaysDisplayed();
    if (alwaysBox.isSelected() != always) {
      alwaysBox.setSelected(always);
      writeIni();
    }

    return success;
  }


  // -- LogicManager API methods --

  /** Called to notify the logic manager of a VisBio event. */
  public void doEvent(VisBioEvent evt) {
    int eventType = evt.getEventType();
    if (eventType == VisBioEvent.LOGIC_ADDED) {
      Object src = evt.getSource();
      if (src == this) doGUI();
      else if (src instanceof ExitManager) {
        // HACK - make options menu item appear in the proper location

        if (!VisBioFrame.MAC_OS_X) {
          // file menu
          bio.addMenuSeparator("File");
          bio.addMenuItem("File", "Options...",
            "loci.visbio.state.OptionManager.fileOptions", 'o');
        }
      }
    }
  }

  /** Gets the number of tasks required to initialize this logic manager. */
  public int getTasks() { return 1; }


  // -- Saveable API methods --

  /** Writes the current state to the given XML object. */
  public void saveState(OMEElement ome) throws SaveException {
    for (int i=0; i<list.size(); i++) {
      BioOption option = (BioOption) list.elementAt(i);
      option.saveState(ome);
    }
  }

  /** Restores the current state from the given XML object. */
  public void restoreState(OMEElement ome) throws SaveException {
    for (int i=0; i<list.size(); i++) {
      BioOption option = (BioOption) list.elementAt(i);
      option.restoreState(ome);
    }
  }


  // -- Helper methods --

  /** Adds options-related GUI components to VisBio. */
  private void doGUI() {
    // options menu
    bio.setSplashStatus("Initializing options logic");
  }


  // -- Menu commands --

  /** Brings up the options dialog box. */
  public void fileOptions() {
    readIni();
    int rval = options.showDialog(bio);
    if (rval == OptionPane.APPROVE_OPTION) {
      writeIni();
      bio.generateEvent(this, "tweak options", true);
    }
    else readIni();
  }

}
