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

import ij.IJ;
import ij.ImagePlus;

import java.io.IOException;

import loci.formats.FormatException;
import loci.plugins.in.ImagePlusReader;
import loci.plugins.in.ImportProcess;
import loci.plugins.in.ImporterOptions;
import loci.plugins.in.ImporterPrompter;

/**
 * Miscellaneous plugins utility methods.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats-plugins/src/loci/plugins/BF.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats-plugins/src/loci/plugins/BF.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public final class BF {

  // -- Constructor --

  private BF() { }

  // -- Utility methods --

  public static void debug(String msg) {
    if (IJ.debugMode) IJ.log("Bio-Formats: " + msg);
  }

  public static void status(boolean quiet, String msg) {
    if (quiet) return;
    IJ.showStatus(msg);
  }

  public static void warn(boolean quiet, String msg) {
    if (quiet) return;
    IJ.log("Warning: " + msg);
  }

  public static void progress(boolean quiet, int value, int max) {
    if (quiet) return;
    IJ.showProgress(value, max);
  }

  public static ImagePlus[] openImagePlus(String path)
    throws FormatException, IOException
  {
    ImporterOptions options = new ImporterOptions();
    options.setId(path);
    return openImagePlus(options);
  }

  public static ImagePlus[] openThumbImagePlus(String path)
    throws FormatException, IOException
  {
    ImporterOptions options = new ImporterOptions();
    options.setId(path);
    return openThumbImagePlus(options);
  }

  public static ImagePlus[] openImagePlus(ImporterOptions options)
    throws FormatException, IOException
  {
    ImportProcess process = new ImportProcess(options);
    if (!process.execute()) return null;
    ImagePlusReader reader = new ImagePlusReader(process);
    ImagePlus[] imps = reader.openImagePlus();
    if (!options.isVirtual()) {
      process.getReader().close();
    }
    return imps;
  }

  public static ImagePlus[] openThumbImagePlus(ImporterOptions options)
    throws FormatException, IOException
  {
    options.setQuiet(true); // NB: Only needed due to ImporterPrompter.
    options.setWindowless(true); // NB: Only needed due to ImporterPrompter.

    ImportProcess process = new ImportProcess(options);

    new ImporterPrompter(process); // NB: Could eliminate this (see above).

    if (!process.execute()) return null;
    ImagePlusReader reader = new ImagePlusReader(process);
    ImagePlus[] imps = reader.openThumbImagePlus();
    if (!options.isVirtual()) {
      process.getReader().close();
    }
    return imps;
  }

}
