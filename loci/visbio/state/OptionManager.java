//
// OptionManager.java
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

package loci.visbio.state;

import com.jgoodies.plaf.LookUtils;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import loci.visbio.*;
import loci.visbio.util.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** OptionManager is the manager encapsulating VisBio's options. */
public class OptionManager extends LogicManager {

  // -- Constants --

  /** Configuration file for storing VisBio options. */
  private static final File CONFIG_FILE = new File("visbio.ini");

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

    // HACK - ensure tabs appear in the right order
    options.addTab("General");
    options.addTab("Thumbnails");
    options.addTab("Warnings");
    options.addTab("Debug");
  }

  // -- OptionManager API methods --

  /** Adds an option allowing the user to toggle a check box. */
  public BooleanOption addBooleanOption(String tab,
    String text, char mnemonic, String tip, boolean value)
  {
    BooleanOption option = new BooleanOption(text, mnemonic, tip, value);
    addOption(tab, option);
    return option;
  }

  /** Adds an option allowing the user to enter a numerical value. */
  public NumericOption addNumericOption(String tab,
    String text, String unit, String tip, int value)
  {
    NumericOption option = new NumericOption(text, unit, tip, value);
    addOption(tab, option);
    return option;
  }

  /** Adds an option allowing the user to enter a numerical value. */
  public NumericOption addNumericOption(String tab,
    String text, String unit, String tip, double value)
  {
    NumericOption option = new NumericOption(text, unit, tip, value);
    addOption(tab, option);
    return option;
  }

  /** Adds an option allowing the user to select from a dropdown list. */
  public ListOption addListOption(String tab,
    String text, String tip, String[] choices)
  {
    ListOption option = new ListOption(text, tip, choices);
    addOption(tab, option);
    return option;
  }

  /**
   * Adds a custom GUI component to VisBio's options dialog.
   * Such options will not be saved in the INI file automatically.
   */
  public CustomOption addCustomOption(String tab, Component c) {
    CustomOption option = new CustomOption(c);
    addOption(tab, option);
    return option;
  }

  /** Adds an option to VisBio's options dialog. */
  public void addOption(String tab, BioOption option) {
    options.addOption(tab, option);
    list.add(option);
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
    if (!CONFIG_FILE.exists()) return;
    try {
      Document doc = XMLUtil.parseXML(CONFIG_FILE);
      Element el = doc == null ? null : doc.getDocumentElement();
      if (el != null) restoreState(el);
      bio.generateEvent(this, "read ini file", false);
    }
    catch (SaveException exc) { exc.printStackTrace(); }
  }

  /** Writes out configuration to configuration file. */
  public void writeIni() {
    Document doc = XMLUtil.createDocument("VisBio");
    try { saveState(doc.getDocumentElement()); }
    catch (SaveException exc) { exc.printStackTrace(); }
    XMLUtil.writeXML(CONFIG_FILE, doc);
  }

  /**
   * Checks whether to display a message using the given panel, and does so if
   * necessary. Message is displayed if the option corresponding to the
   * specified text (opt) is enabled.
   */
  public boolean checkMessage(Component parent, String opt,
    boolean allowCancel, JPanel panel, String title)
  {
    return checkMessage(parent, opt,
      new MessagePane(title, panel, allowCancel));
  }

  /**
   * Checks whether to display a warning with the given text, and does so if
   * necessary. Warning is displayed if the option corresponding to the
   * specified text (opt) is enabled.
   */
  public boolean checkWarning(Component parent, String opt,
    boolean allowCancel, String text)
  {
    return checkMessage(parent, opt, new WarningPane(text, allowCancel));
  }

  /**
   * Checks whether to display a message using the given message pane, and does
   * so if necessary. Message is displayed if the option corresponding to the
   * specified text (opt) is enabled.
   */
  public boolean checkMessage(Component parent, String opt, MessagePane pane) {
    BioOption option = getOption(opt);
    if (!(option instanceof BooleanOption)) return true;
    BooleanOption alwaysOption = (BooleanOption) option;
    if (!alwaysOption.getValue()) return true;
    JCheckBox alwaysBox = (JCheckBox) alwaysOption.getComponent();

    boolean success = pane.showDialog(parent) == MessagePane.APPROVE_OPTION;

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

        if (!LookUtils.IS_OS_MAC) {
          // file menu
          bio.addMenuSeparator("File");
          bio.addMenuItem("File", "Options...",
            "loci.visbio.state.OptionManager.fileOptions", 'o');
          SwingUtil.setMenuShortcut(bio, "File", "Options...", KeyEvent.VK_P);
        }
      }
    }
  }

  /** Gets the number of tasks required to initialize this logic manager. */
  public int getTasks() { return 1; }

  // -- Saveable API methods --

  /** Writes the current state to the given DOM element ("VisBio"). */
  public void saveState(Element el) throws SaveException {
    Element optionsElement = XMLUtil.createChild(el, "Options");
    for (int i=0; i<list.size(); i++) {
      BioOption option = (BioOption) list.elementAt(i);
      option.saveState(optionsElement);
    }
  }

  /** Restores the current state from the given DOM element ("VisBio"). */
  public void restoreState(Element el) throws SaveException {
    Element optionsElement = XMLUtil.getFirstChild(el, "Options");
    if (optionsElement != null) {
      for (int i=0; i<list.size(); i++) {
        BioOption option = (BioOption) list.elementAt(i);
        option.restoreState(optionsElement);
      }
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
