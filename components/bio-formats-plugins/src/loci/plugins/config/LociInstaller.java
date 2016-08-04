/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2015 Open Microscopy Environment:
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

package loci.plugins.config;

import ij.plugin.PlugIn;
import loci.plugins.util.WindowTools;

/**
 * An ImageJ plugin for installing and upgrading libraries
 * related to the Bio-Formats plugins.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class LociInstaller implements PlugIn {

  // -- PlugIn API methods --

  /** Executes the plugin. */
  @Override
  public void run(String arg) {
    InstallWizard iw = new InstallWizard();
    WindowTools.placeWindow(iw);
    iw.setVisible(true);
  }

}
