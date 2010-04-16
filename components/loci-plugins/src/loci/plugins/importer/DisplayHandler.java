//
// DisplayHandler.java
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
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.filter.PlugInFilterRunner;

import java.awt.image.IndexColorModel;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import loci.common.ReflectException;
import loci.common.ReflectedUniverse;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.gui.XMLWindow;
import loci.formats.services.OMEXMLService;
import loci.plugins.Colorizer;
import loci.plugins.util.ROIHandler;
import loci.plugins.util.SearchableWindow;
import loci.plugins.util.WindowTools;

import org.xml.sax.SAXException;

/**
 * Logic for displaying images and metadata onscreen using ImageJ.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/importer/DisplayHandler.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/importer/DisplayHandler.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 */
public class DisplayHandler {

  // -- Fields --
  
  protected ImporterOptions options;
  protected XMLWindow xmlWindow;

  // -- Constructor --

  public DisplayHandler(ImporterOptions options) {
    this.options = options;
  }

  // -- DisplayHandler API methods --

  /** Displays standard metadata in a table in its own window. */
  public SearchableWindow displayOriginalMetadata() throws IOException {
    if (!options.isShowMetadata()) return null;
    
    String name = options.getIdName();
    ImporterMetadata meta = options.getOriginalMetadata();
    String metaString = meta.getMetadataString("\t");
    SearchableWindow metaWindow = new SearchableWindow(
      "Original Metadata - " + name, "Key\tValue", metaString, 400, 400);
    metaWindow.setVisible(true);
    return metaWindow;
  }

  /** Displays OME-XML metadata in a tree in its own window. */
  public XMLWindow displayOMEXML() throws FormatException, IOException {
    if (!options.isShowOMEXML()) return null;

    XMLWindow metaWindow = null;
    metaWindow = new XMLWindow("OME Metadata - " + options.getIdName());
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
    xmlWindow = metaWindow; // save reference to OME-XML window
    return metaWindow;
  }
  
  /** Displays the given images according to the configured options. */
  public void displayImages(ImagePlus[] imps, String stackOrder, IndexColorModel[] colorModels) {
    for (ImagePlus imp : imps) displayImage(imp, stackOrder, colorModels);
  }
  
  /** Displays the given image according to the configured options. */
  public void displayImage(ImagePlus imp, String stackOrder, IndexColorModel[] colorModels) {
    if (options.isViewNone()) return;
    else if (options.isViewStandard() || options.isViewHyperstack()) {
      displayNormal(imp, stackOrder, colorModels);
    }
    else if (options.isViewBrowser()) displayDataBrowser(imp, stackOrder, colorModels);
    else if (options.isViewImage5D()) displayImage5D(imp, stackOrder, colorModels);
    else if (options.isViewView5D()) displayView5D(imp, stackOrder, colorModels);
    else if (options.isViewVisBio()) displayVisBio(imp);
    else throw new IllegalStateException("Unknown display mode");
  }

  /**
   * Displays in a normal ImageJ window.
   * ImageJ will show the image as either a standard 2D image window
   * or as a hyperstack (up to 5D, with ZCT sliders) depending on whether
   * imp.setOpenAsHyperStack(true) has been called.
   */
  public void displayNormal(ImagePlus imp, String stackOrder,
    IndexColorModel[] colorModels)
  {
    IFormatReader r = options.getReader();
    boolean windowless = options.isWindowless();
    
    if (!options.isConcatenate() && options.isMergeChannels()) imp.show();

    if (imp.isVisible() && !options.isVirtual()) {
      String arg = "stack_order=" + stackOrder + " merge=true series=" +
        r.getSeries() + " hyper_stack=" + options.isViewHyperstack();
      if (windowless) arg += " merge_option=[" + options.getMergeOption() + "]";
      arg += " ";
      IJ.runPlugIn("loci.plugins.Colorizer", arg);
      if (WindowManager.getCurrentImage().getID() != imp.getID()) imp.close();
    }
    
    // NB: ImageJ 1.39+ is required for hyperstacks

    if (!options.isConcatenate()) {
      boolean hyper = options.isViewHyperstack() || options.isViewBrowser();

      boolean splitC = options.isSplitChannels();
      boolean splitZ = options.isSplitFocalPlanes();
      boolean splitT = options.isSplitTimepoints();

      if (!imp.isVisible()) imp.show();

      boolean customColorize = options.isCustomColorize();
      boolean browser = options.isViewBrowser();
      boolean virtual = options.isVirtual();

      if (options.isColorize() || customColorize) {
        IJ.runPlugIn("loci.plugins.Colorizer", "stack_order=" + stackOrder +
          " merge=false colorize=true ndx=" + (customColorize ? "-1" : "0") +
          " series=" + r.getSeries() + " hyper_stack=" +
          options.isViewHyperstack() + " ");
        imp.close();
      }
      else if (colorModels != null && !browser && !virtual) {
        Colorizer colorizer = new Colorizer();
        String arg = "stack_order=" + stackOrder + " merge=false " +
          "colorize=true series=" + r.getSeries() + " hyper_stack=" +
          hyper + " ";
        colorizer.setup(arg, imp);
        for (int channel=0; channel<colorModels.length; channel++) {
          byte[][] lut = new byte[3][256];
          colorModels[channel].getReds(lut[0]);
          colorModels[channel].getGreens(lut[1]);
          colorModels[channel].getBlues(lut[2]);
          colorizer.setLookupTable(lut, channel);
        }
        new PlugInFilterRunner(colorizer, "", arg);
        imp.close();
      }

      if (splitC || splitZ || splitT) {
        IJ.runPlugIn("loci.plugins.Slicer", "slice_z=" + splitZ +
          " slice_c=" + splitC + " slice_t=" + splitT +
          " stack_order=" + stackOrder + " keep_original=false " +
          "hyper_stack=" + hyper + " ");
        imp.close();
      }
    }
  }
  
  public void displayDataBrowser(ImagePlus imp, String stackOrder,
      IndexColorModel[] colorModels)
  {
    // NB: Use regular hyperstack display for now, since
    // recent versions of ImageJ v1.43+ broke DataBrowser
    // by removing the sliceSelector field.
    displayNormal(imp, stackOrder, colorModels);

//    IFormatReader r = options.getReader();
//    String[] dimTypes = r.getChannelDimTypes();
//    int[] dimLengths = r.getChannelDimLengths();
//    new DataBrowser(imp, null, dimTypes, dimLengths, xmlWindow);
  }
  
  public void displayImage5D(ImagePlus imp, String stackOrder, IndexColorModel[] colorModels) {
    displayNormal(imp, stackOrder, colorModels); //TEMP?

    IFormatReader r = options.getReader();
    ReflectedUniverse ru = new ReflectedUniverse();
    try {
      ru.exec("import i5d.Image5D");
      ru.setVar("title", imp.getTitle());
      ru.setVar("stack", imp.getStack());
      ru.setVar("sizeC", r.getSizeC());
      ru.setVar("sizeZ", r.getSizeZ());
      ru.setVar("sizeT", r.getSizeT());
      ru.exec("i5d = new Image5D(title, stack, sizeC, sizeZ, sizeT)");
      ru.setVar("cal", imp.getCalibration());
      ru.setVar("fi", imp.getOriginalFileInfo());
      ru.exec("i5d.setCalibration(cal)");
      ru.exec("i5d.setFileInfo(fi)");
      //ru.exec("i5d.setDimensions(sizeC, sizeZ, sizeT)");
      ru.exec("i5d.show()");
    }
    catch (ReflectException exc) {
      WindowTools.reportException(exc, options.isQuiet(),
        "Sorry, there was a problem interfacing with Image5D");
      return;
    }
  }

  public void displayView5D(ImagePlus imp, String stackOrder, IndexColorModel[] colorModels) {
    displayNormal(imp, stackOrder, colorModels); //TEMP?

    WindowManager.setTempCurrentImage(imp);
    IJ.run("start viewer", "");
  }

  public void displayVisBio(ImagePlus imp) {
    // NB: avoid dependency on optional loci.visbio packages
    ReflectedUniverse ru = new ReflectedUniverse();
    try {
      ru.exec("import loci.visbio.data.Dataset");
      //ru.setVar("name", name);
      //ru.setVar("pattern", pattern);
      ru.exec("dataset = new Dataset(name, pattern)");
      // TODO: finish VisBio logic
    }
    catch (ReflectException exc) {
      WindowTools.reportException(exc, options.isQuiet(),
        "Sorry, there was a problem interfacing with VisBio");
      return;
    }
  }

  public void displayROIs(ImagePlus[] imps) {
    if (!options.showROIs()) return;
    ROIHandler.openROIs(options.getOMEMetadata(), imps);
  }

}
