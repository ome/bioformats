//
// Importer.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser, Stack Colorizer and Stack Slicer. Copyright (C) 2005-@year@
Melissa Linkert, Curtis Rueden and Christopher Peterson.

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

package loci.plugins.in;

import ij.ImageJ;
import ij.ImagePlus;

import java.io.IOException;

import loci.formats.FormatException;
import loci.plugins.BF;
import loci.plugins.LociImporter;
import loci.plugins.util.WindowTools;

/**
 * Core logic for the Bio-Formats Importer ImageJ plugin.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/Importer.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/Importer.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
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
    options.loadOptions();
    options.parseArg(arg);
    return options;
  }

  public void showDialogs(ImportProcess process)
    throws FormatException, IOException
  {
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
