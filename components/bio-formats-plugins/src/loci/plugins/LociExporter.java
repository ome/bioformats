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

package loci.plugins;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

import java.util.HashSet;

import loci.common.DebugTools;
import loci.plugins.out.Exporter;
import loci.plugins.util.LibraryChecker;

/**
 * ImageJ plugin for writing files using the OME Bio-Formats package.
 * Wraps core logic in {@link loci.plugins.out.Exporter}, to avoid
 * direct references to classes in the external Bio-Formats library.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class LociExporter implements PlugInFilter {

  // -- Fields --

  /** Argument passed to setup method. */
  public String arg;

  private Exporter exporter;

  // -- PlugInFilter API methods --

  /** Sets up the writer. */
  @Override
  public int setup(String arg, ImagePlus imp) {
    this.arg = arg;
    exporter = new Exporter(this, imp);
    return DOES_ALL + NO_CHANGES;
  }

  /** Executes the plugin. */
  @Override
  public void run(ImageProcessor ip) {
    DebugTools.enableLogging("INFO");
    if (!LibraryChecker.checkJava() || !LibraryChecker.checkImageJ()) return;
    HashSet<String> missing = new HashSet<String>();
    LibraryChecker.checkLibrary(LibraryChecker.Library.BIO_FORMATS, missing);
    LibraryChecker.checkLibrary(LibraryChecker.Library.OME_JAVA_XML, missing);
    if (!LibraryChecker.checkMissing(missing)) return;
    if (exporter != null) exporter.run();
  }

}
