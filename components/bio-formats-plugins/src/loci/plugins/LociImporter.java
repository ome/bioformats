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

package loci.plugins;

import ij.plugin.PlugIn;

import java.util.HashSet;

import loci.common.DebugTools;
import loci.plugins.in.Importer;
import loci.plugins.util.LibraryChecker;

/**
 * ImageJ plugin for reading files using the OME Bio-Formats package.
 * Wraps core logic in {@link loci.plugins.in.Importer}, to avoid
 * direct references to classes in the external Bio-Formats library.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats-plugins/src/loci/plugins/LociImporter.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats-plugins/src/loci/plugins/LociImporter.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class LociImporter implements PlugIn {

  // -- Fields --

  /**
   * Flag indicating whether last operation was successful.
   * NB: This field must be updated properly, or the plugin
   * will stop working correctly with HandleExtraFileTypes.
   */
  public boolean success;

  /**
   * Flag indicating whether last operation was canceled.
   * NB: This field must be updated properly, or the plugin
   * will stop working correctly with HandleExtraFileTypes.
   */
  public boolean canceled;

  // -- PlugIn API methods --

  /** Executes the plugin. */
  public void run(String arg) {
    DebugTools.enableLogging("INFO");
    canceled = false;
    success = false;
    if (!LibraryChecker.checkJava() || !LibraryChecker.checkImageJ()) return;
    HashSet<String> missing = new HashSet<String>();
    LibraryChecker.checkLibrary(LibraryChecker.Library.BIO_FORMATS, missing);
    LibraryChecker.checkLibrary(LibraryChecker.Library.OME_JAVA_XML, missing);
    LibraryChecker.checkLibrary(LibraryChecker.Library.FORMS, missing);
    if (!LibraryChecker.checkMissing(missing)) return;
    new Importer(this).run(arg);
  }

}
