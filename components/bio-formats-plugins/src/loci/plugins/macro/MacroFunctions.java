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

package loci.plugins.macro;

import ij.IJ;
import ij.macro.ExtensionDescriptor;
import ij.macro.Functions;
import ij.macro.MacroExtension;
import ij.plugin.PlugIn;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import loci.plugins.util.LibraryChecker;
import loci.plugins.util.WindowTools;

/**
 * Convenience class that simplifies implemention of ImageJ macro extensions.
 * It uses reflection to create an extension method for each public method in
 * the implementing subclass. See {@link LociFunctions} for an example.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats-plugins/src/loci/plugins/macro/MacroFunctions.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats-plugins/src/loci/plugins/macro/MacroFunctions.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class MacroFunctions implements PlugIn, MacroExtension {

  // -- Fields --

  protected ExtensionDescriptor[] extensions = buildExtensions();

  // -- PlugIn API methods --

  public void run(String arg) {
    if (!LibraryChecker.checkImageJ()) return;
    if (!IJ.macroRunning()) {
      IJ.error("Cannot install extensions from outside a macro.");
      return;
    }
    Functions.registerExtensions(this);
  }

  // -- MacroExtension API methods --

  public ExtensionDescriptor[] getExtensionFunctions() {
    return extensions;
  }

  public String handleExtension(String name, Object[] args) {
    Class<?>[] c = null;
    if (args != null) {
      c = new Class[args.length];
      for (int i=0; i<args.length; i++) c[i] = args[i].getClass();
    }
    try {
      getClass().getMethod(name, c).invoke(this, args);
    }
    catch (NoSuchMethodException exc) {
      WindowTools.reportException(exc, false, "Macro error");
    }
    catch (IllegalAccessException exc) {
      WindowTools.reportException(exc, false, "Macro error");
    }
    catch (InvocationTargetException exc) {
      WindowTools.reportException(exc, false, "Macro error");
    }
    return null;
  }

  // -- Helper methods --

  /**
   * Builds the list of extensions, using reflection,
   * from public methods of this class.
   */
  protected ExtensionDescriptor[] buildExtensions() {
    Method[] m = getClass().getMethods();
    ExtensionDescriptor[] desc = new ExtensionDescriptor[m.length];
    for (int i=0; i<m.length; i++) {
      Class<?>[] c = m[i].getParameterTypes();
      int[] types = new int[c.length];
      for (int j=0; j<c.length; j++) {
        if (c[j] == String.class) types[j] = ARG_STRING;
        else if (c[j] == Double.class) types[j] = ARG_NUMBER;
        else if (c[j] == Object[].class) types[j] = ARG_ARRAY;
        else if (c[j] == String[].class) types[j] = ARG_OUTPUT + ARG_STRING;
        else if (c[j] == Double[].class) types[j] = ARG_OUTPUT + ARG_NUMBER;
      }
      desc[i] = ExtensionDescriptor.newDescriptor(m[i].getName(), this, types);
    }
    return desc;
  }

}
