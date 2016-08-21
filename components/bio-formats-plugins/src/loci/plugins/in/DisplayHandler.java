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

package loci.plugins.in;

import ij.ImagePlus;
import ij.WindowManager;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.xml.parsers.ParserConfigurationException;

import loci.common.ReflectException;
import loci.common.ReflectedUniverse;
import loci.common.StatusEvent;
import loci.common.StatusListener;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.gui.XMLWindow;
import loci.formats.services.OMEXMLService;
import loci.plugins.BF;
import loci.plugins.util.DataBrowser;
import loci.plugins.util.ROIHandler;
import loci.plugins.util.SearchableWindow;
import loci.plugins.util.WindowTools;

import org.xml.sax.SAXException;

/**
 * Logic for displaying images and metadata onscreen using ImageJ.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats-plugins/src/loci/plugins/in/DisplayHandler.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats-plugins/src/loci/plugins/in/DisplayHandler.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class DisplayHandler implements StatusListener {

  // -- Fields --

  protected ImportProcess process;
  protected ImporterOptions options;
  protected XMLWindow xmlWindow;

  // -- Constructor --

  public DisplayHandler(ImportProcess process) {
    this.process = process;
    options = process.getOptions();
  }

  // -- DisplayHandler API methods --

  /** Displays standard metadata in a table in its own window. */
  public SearchableWindow displayOriginalMetadata() {
    if (!options.isShowMetadata()) return null;

    String name = process.getIdName();
    ImporterMetadata meta = process.getOriginalMetadata();
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
    metaWindow = new XMLWindow("OME Metadata - " + process.getIdName());
    Exception exc = null;
    try {
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      metaWindow.setXML(service.getOMEXML(process.getOMEMetadata()));
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
  public void displayImages(ImagePlus[] imps) {
    if (imps != null) {
      for (ImagePlus imp : imps) displayImage(imp);
    }
  }

  /** Displays the given image according to the configured options. */
  public void displayImage(ImagePlus imp) {
    if (options.isViewNone()) return;
    else if (options.isViewStandard()) displayNormal(imp);
    else if (options.isViewHyperstack()) displayNormal(imp);
    else if (options.isViewBrowser()) displayDataBrowser(imp);
    else if (options.isViewImage5D()) displayImage5D(imp);
    else if (options.isViewView5D()) displayView5D(imp);
    else throw new IllegalStateException("Unknown display mode");
  }

  /**
   * Displays in a normal ImageJ window.
   * ImageJ will show the image as either a standard 2D image window
   * or as a hyperstack (up to 5D, with ZCT sliders) depending on whether
   * imp.setOpenAsHyperStack(true) has been called.
   */
  public void displayNormal(ImagePlus imp) {
    imp.show();
  }

  public void displayDataBrowser(ImagePlus imp) {
    IFormatReader r = process.getReader();
    String[] dimTypes = r.getChannelDimTypes();
    int[] dimLengths = r.getChannelDimLengths();
    new DataBrowser(imp, null, dimTypes, dimLengths, xmlWindow);
  }

  public void displayImage5D(ImagePlus imp) {
    WindowManager.setTempCurrentImage(imp);

    IFormatReader r = process.getReader();
    ReflectedUniverse ru = new ReflectedUniverse();
    try {
      ru.exec("import i5d.Image5D");
      ru.setVar("title", imp.getTitle());
      ru.setVar("stack", imp.getStack());
      ru.setVar("sizeC", imp.getNChannels());
      ru.setVar("sizeZ", imp.getNSlices());
      ru.setVar("sizeT", imp.getNFrames());
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
    }
  }

  public void displayView5D(ImagePlus imp) {
    WindowManager.setTempCurrentImage(imp);
    //new view5d.View5D_("");
    Exception exc = null;
    try {
      Class<?> c = Class.forName("view5d.View5D_");
      Constructor<?> con = c.getConstructor();
      con.newInstance();
    }
    catch (ClassNotFoundException e) { exc = e; }
    catch (SecurityException e) { exc = e; }
    catch (NoSuchMethodException e) { exc = e; }
    catch (IllegalArgumentException e) { exc = e; }
    catch (InstantiationException e) { exc = e; }
    catch (IllegalAccessException e) { exc = e; }
    catch (InvocationTargetException e) { exc = e; }
    if (exc != null) {
      WindowTools.reportException(exc, options.isQuiet(),
        "Sorry, there was a problem interfacing with View5D");
    }
  }

  public void displayROIs(ImagePlus[] imps) {
    if (!options.showROIs()) return;
    ROIHandler.openROIs(process.getOMEMetadata(), imps);
  }

  // -- StatusListener methods --

  /** Reports status updates via ImageJ's status bar mechanism. */
  public void statusUpdated(StatusEvent e) {
    String msg = e.getStatusMessage();
    if (msg != null) BF.status(options.isQuiet(), msg);
    int value = e.getProgressValue();
    int max = e.getProgressMaximum();
    if (value >= 0 && max >= 0) BF.progress(options.isQuiet(), value, max);
  }

}
