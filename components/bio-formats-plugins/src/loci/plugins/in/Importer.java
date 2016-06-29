/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2016 Open Microscopy Environment:
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

import ij.ImageJ;
import ij.ImagePlus;
import ij.Macro;

import java.io.IOException;

import loci.formats.FormatException;
import loci.plugins.BF;
import loci.plugins.LociImporter;
import loci.plugins.util.WindowTools;

/**
 * Core logic for the Bio-Formats Importer ImageJ plugin.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class Importer {

  // -- Fields --

  /**
   * A handle to the plugin wrapper, for toggling
   * the canceled and success flags.
   */
  private LociImporter plugin;

  // -- Constructor --

  public Importer(LociImporter plugin) {
    this.plugin = plugin;
  }

  // -- Importer API methods --

  /** Executes the plugin. */
  public void run(String arg) {
    ImporterOptions options = null;
    try {
      BF.debug("parse core options");
      options = parseOptions(arg);
      if (plugin.canceled) return;

      ImportProcess process = new ImportProcess(options);

      BF.debug("display option dialogs");
      showDialogs(process);
      if (plugin.canceled) return;

      BF.debug("display metadata");
      DisplayHandler displayHandler = new DisplayHandler(process);
      displayHandler.displayOriginalMetadata();
      displayHandler.displayOMEXML();

      BF.debug("read pixel data");
      ImagePlusReader reader = new ImagePlusReader(process);
      ImagePlus[] imps = readPixels(reader, options, displayHandler);

      BF.debug("display pixels");
      displayHandler.displayImages(imps);

      BF.debug("display ROIs");
      displayHandler.displayROIs(imps);

      BF.debug("finish");
      finish(process);
    }
    catch (FormatException exc) {
      boolean quiet = options == null ? false : options.isQuiet();
      WindowTools.reportException(exc, quiet,
        "Sorry, there was a problem during import.");
    }
    catch (IOException exc) {
      boolean quiet = options == null ? false : options.isQuiet();
      WindowTools.reportException(exc, quiet,
        "Sorry, there was an I/O problem during import.");
    }
  }

  /** Parses core options. */
  public ImporterOptions parseOptions(String arg) throws IOException {
    ImporterOptions options = new ImporterOptions();
    if (Macro.getOptions() == null) {
      options.loadOptions();
    }
    options.parseArg(arg);
    options.checkObsoleteOptions();
    return options;
  }

  public void showDialogs(ImportProcess process)
    throws FormatException, IOException
  {
    // TODO: Do not use the ImporterPrompter in batch mode. Unfortunately,
    // without avoiding usage of ImporterPrompter in batch mode, the
    // Bio-Formats Importer plugin cannot work in headless mode.
    //
    // The problem is that invoking the ImporterPrompter activates crucial
    // macro-related functionality (via GenericDialogs). We cannot currently
    // eliminate the use of ImporterPrompter in batch/headless mode, as we do
    // not do our own harvesting of importer options from the macro argument.
    //
    // Further, we need to be sure all the Dialog classes are not performing
    // any "side-effect" logic on the ImportProcess and/or ImporterOptions
    // before we can make this change.

    // attach dialog prompter to process
    new ImporterPrompter(process);

    // execute the preparation process
    process.execute();
    if (process.wasCanceled()) plugin.canceled = true;
  }

  public ImagePlus[] readPixels(ImagePlusReader reader, ImporterOptions options,
    DisplayHandler displayHandler) throws FormatException, IOException
  {
    if (options.isViewNone()) return null;
    if (!options.isQuiet()) reader.addStatusListener(displayHandler);
    ImagePlus[] imps = reader.openImagePlus();
    return imps;
  }

  public void finish(ImportProcess process) throws IOException {
    if (!process.getOptions().isVirtual()) process.getReader().close();
    plugin.success = true;
  }

  // -- Main method --

  /** Main method, for testing. */
  public static void main(String[] args) {
    new ImageJ(null);
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<args.length; i++) {
      if (i > 0) sb.append(" ");
      sb.append(args[i]);
    }
    new LociImporter().run(sb.toString());
  }

}
