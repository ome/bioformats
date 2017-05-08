/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.plugins.in;

import ij.IJ;
import ij.gui.GenericDialog;
import ij.io.OpenDialog;

import java.awt.TextField;
import java.util.Vector;

import loci.common.Location;

/**
 * Bio-Formats Importer id chooser dialog box.
 */
public class IdDialog extends ImporterDialog {

  // -- Fields --

  private OpenDialog od;
  private String name;

  // -- Constructor --

  /** Creates an id chooser dialog for the Bio-Formats Importer. */
  public IdDialog(ImportProcess process) {
    super(process);
  }

  // -- ImporterDialog methods --

  @Override
  protected boolean needPrompt() {
    return options.getId() == null;
  }

  @Override
  protected GenericDialog constructDialog() {
    GenericDialog gd = null;
    if (options.isHTTP()) {
      gd = new GenericDialog("Bio-Formats URL");
      gd.addStringField("URL: ", "http://", 30);
    }
    else if (options.isOMERO()) {
      gd = new GenericDialog("OMERO Server Credentials");
      gd.addStringField("Server: ", "", 80);
      gd.addStringField("Port: ", "4064", 80);
      gd.addStringField("Username: ", "", 80);
      gd.addStringField("Password: ", "", 80);
      gd.addStringField("Group:    ", "default", 80);
      gd.addStringField("Image ID: ", "", 80);

      Vector v = gd.getStringFields();
      ((TextField) v.get(3)).setEchoChar('*');
    }
    return gd;
  }

  /**
   * Asks user whether Bio-Formats should automatically check for upgrades,
   * and if so, checks for an upgrade and prompts user to install it.
   *
   * @return status of operation
   */
  @Override
  protected boolean displayDialog(GenericDialog gd) {
    if (options.isLocal()) {
      if (options.isFirstTime() && IJ.isMacOSX() && !options.isQuiet()) {
        String osVersion = System.getProperty("os.version");
        if (osVersion == null ||
          osVersion.startsWith("10.4.") ||
          osVersion.startsWith("10.3.") ||
          osVersion.startsWith("10.2."))
        {
          // present user with one-time dialog box
          IJ.showMessage("Bio-Formats",
            "One-time warning: There is a bug in Java on Mac OS X with the " +
            "native file chooser\nthat crashes ImageJ if you click on a file " +
            "in CXD, IPW, OIB or ZVI format while in\ncolumn view mode. You " +
            "can work around the problem in one of two ways:\n \n" +
            "    1. Switch to list view (press Command+2)\n" +
            "    2. Check \"Use JFileChooser to Open/Save\" under " +
            "Edit>Options>Input/Output...");
        }
      }

      String idLabel = options.getLabel(ImporterOptions.KEY_ID);
      od = new OpenDialog(idLabel, options.getId());
      name = od.getFileName();
      if (name == null) return false;
    }
    else if (options.isHTTP() || options.isOMERO()) {
      gd.showDialog();
      if (gd.wasCanceled()) return false;
    }
    return true;
  }

  @Override
  protected boolean harvestResults(GenericDialog gd) {
    String id = null;
    if (options.isLocal()) {
      String dir = od.getDirectory();
      // NB: do not use od.getFileName() here.  That method has been called
      // above, so the macro recorder will record the file path twice if we
      // call od.getFileName() again.  See ticket #596.
      if (dir != null || name == null) {
        id = dir + name;
      }

      // verify validity
      Location idLoc = new Location(id);
      if (!idLoc.exists() && !id.toLowerCase().endsWith(".fake")) {
        if (!options.isQuiet()) {
          IJ.error("Bio-Formats",
            "The specified file (" + id + ") does not exist.");
        }
        return false;
      }
    }
    else if (options.isHTTP()) {
      id = gd.getNextString();
      if (id == null) {
        if (!options.isQuiet()) {
          IJ.error("Bio-Formats", "No URL was specified.");
        }
        return false;
      }
    }
    else if (options.isOMERO()) {
      StringBuffer omero = new StringBuffer("omero:");
      omero.append("server=");
      omero.append(gd.getNextString());
      omero.append("\nport=");
      omero.append(gd.getNextString());
      omero.append("\nuser=");
      omero.append(gd.getNextString());
      omero.append("\npass=");
      omero.append(gd.getNextString());

      String group = gd.getNextString();
      Long groupID = null;
      try {
        groupID = new Long(group);
      }
      catch (NumberFormatException e) { }

      omero.append("\ngroupName=");
      omero.append(group);
      if (groupID != null) {
        omero.append("\ngroupID=");
        omero.append(groupID);
      }
      omero.append("\niid=");
      omero.append(gd.getNextString());

      id = omero.toString();
    }
    options.setId(id);
    return true;
  }

}
