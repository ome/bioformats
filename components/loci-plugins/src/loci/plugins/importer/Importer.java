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

package loci.plugins.importer;

import ij.ImageJ;
import ij.ImagePlus;

import java.io.IOException;

import loci.formats.FormatException;
import loci.plugins.LociImporter;
import loci.plugins.util.BF;
import loci.plugins.util.ROIHandler;
import loci.plugins.util.WindowTools;

/**
 * Core logic for the Bio-Formats Importer ImageJ plugin.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/importer/Importer.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/importer/Importer.java">SVN</a></dd></dl>
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

      BF.debug("display option dialogs");
      showOptionDialogs(options);
      if (plugin.canceled) return;

      BF.debug("display metadata");
      DisplayHandler displayHandler = new DisplayHandler(options);
      displayHandler.displayOriginalMetadata();
      displayHandler.displayOMEXML();

      BF.debug("read pixel data");
      ImagePlus[] imps = readPixels(options, displayHandler);

      BF.debug("display ROIs");
      displayHandler.displayROIs(imps);

      BF.debug("finish");
      finish(options);
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

  public void showOptionDialogs(ImporterOptions options)
    throws FormatException, IOException
  {
    boolean success = options.showDialogs();
    if (!success) plugin.canceled = true;
  }

  /** Displays standard metadata in a table in its own window. */
  public void displayOriginalMetadata(ImporterOptions options) {
    DisplayHandler displayHandler = new DisplayHandler(options);
    displayHandler.displayOriginalMetadata();
  }

  public void displayOMEXML(ImporterOptions options)
    throws FormatException, IOException
  {
    DisplayHandler displayHandler = new DisplayHandler(options);
    displayHandler.displayOMEXML();
  }

  public ImagePlus[] readPixels(ImporterOptions options,
    DisplayHandler displayHandler) throws FormatException, IOException
  {
    if (options.isViewNone()) return null;
    ImagePlusReader ipr = new ImagePlusReader(options);
    ipr.addStatusListener(displayHandler);
    ImagePlus[] imps = ipr.openImagePlus();

    // TEMP!
    displayHandler.displayImages(imps, ipr.stackOrder, ipr.colorModels);

    return imps;
  }

  public void displayROIs(ImporterOptions options, ImagePlus[] imps) {
    if (options.showROIs()) {
      BF.debug("display ROIs");
      ROIHandler.openROIs(options.getOMEMetadata(), imps);
    }
    else BF.debug("skip ROIs");
  }

  public void finish(ImporterOptions options) throws IOException {
    if (!options.isVirtual()) options.getReader().close();
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
