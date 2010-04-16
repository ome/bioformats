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

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;

import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.gui.XMLWindow;
import loci.formats.services.OMEXMLService;
import loci.plugins.LociImporter;
import loci.plugins.util.BF;
import loci.plugins.util.ROIHandler;
import loci.plugins.util.WindowTools;

import org.xml.sax.SAXException;

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

  private ArrayList<ImagePlus> imps = new ArrayList<ImagePlus>();
  private String stackOrder = null;

  private IndexColorModel[] colorModels;

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
      options = parseOptions(arg, false);
      if (plugin.canceled) return;

      BF.debug("display option dialogs");
      showOptionDialogs(options);
      if (plugin.canceled) return;

      if (options.isShowMetadata()) {
        BF.debug("display metadata");
        IJ.showStatus("Populating metadata");
        displayOriginalMetadata(options);
        if (plugin.canceled) return;
      }
      else BF.debug("skip metadata");

      if (options.isShowOMEXML()) {
        BF.debug("show OME-XML");
        displayOMEXML(options);
        if (plugin.canceled) return;
      }
      else BF.debug("skip OME-XML");

      if (!options.isViewNone()) {
        BF.debug("read pixel data");
        readPixelData(options);
        if (plugin.canceled) return;
      }
      else BF.debug("skip pixel data"); // nothing to display

      if (options.showROIs()) {
        BF.debug("display ROIs");
        displayROIs(options);
        if (plugin.canceled) return;
      }
      else BF.debug("skip ROIs");

      BF.debug("finish");
      finish(options);
      if (plugin.canceled) return;
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
  public ImporterOptions parseOptions(String arg, boolean quiet)
    throws IOException
  {
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
  public void displayOriginalMetadata(ImporterOptions options)
    throws IOException
  {
    ImporterMetadata meta = options.getOriginalMetadata();
    meta.showMetadataWindow(options.getIdName());
  }

  public void displayOMEXML(ImporterOptions options)
    throws FormatException, IOException
  {
    if (options.isViewBrowser()) {
      // NB: Data Browser has its own internal OME-XML metadata window,
      // which we'll trigger once we have created a Data Browser.
      // So there is no need to pop up a separate OME-XML here.
    }
    else {
      XMLWindow metaWindow =
        new XMLWindow("OME Metadata - " + options.getIdName());
      Exception exc = null;
      try {
        ServiceFactory factory = new ServiceFactory();
        OMEXMLService service = factory.getInstance(OMEXMLService.class);
        metaWindow.setXML(service.getOMEXML(options.getOMEMetadata()));
        WindowTools.placeWindow(metaWindow);
        metaWindow.setVisible(true);
      }
      catch (DependencyException e) { exc = e; }
      catch (ServiceException e) { exc = e; }
      catch (ParserConfigurationException e) { exc = e; }
      catch (SAXException e) { exc = e; }

      if (exc != null) throw new FormatException(exc);
    }
  }

  public void readPixelData(ImporterOptions options)
    throws FormatException, IOException
  {
    ImagePlusReader ipr = new ImagePlusReader(options);
    ipr.openImagePlus();
    // TODO - migrate display logic here
  }

  public void displayROIs(ImporterOptions options) {
    if (options.showROIs()) {
      BF.debug("display ROIs");

      ImagePlus[] impsArray = imps.toArray(new ImagePlus[0]);
      ROIHandler.openROIs(options.getOMEMetadata(), impsArray);
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
