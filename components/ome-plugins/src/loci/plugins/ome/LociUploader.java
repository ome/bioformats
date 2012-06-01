/*
 * #%L
 * OME Plugins for ImageJ: a collection of ImageJ plugins
 * including the Download from OME and Upload to OME plugins.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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

package loci.plugins.ome;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.io.FileInfo;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;

import java.awt.TextField;
import java.io.IOException;
import java.util.HashSet;

import loci.common.DataTools;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.services.OMEXMLService;
import loci.ome.io.OMEWriter;
import loci.plugins.util.LibraryChecker;

import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;

/**
 * ImageJ plugin for uploading images to an OME server.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-plugins/src/loci/plugins/ome/LociUploader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-plugins/src/loci/plugins/ome/LociUploader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert linket at wisc.edu
 */
public class LociUploader implements PlugIn {

  // -- Fields --

  private String server;
  private String user;
  private String pass;
  private boolean canceled = false;

  // -- PlugIn API methods --

  public void run(String arg) {
    // check that we can safely execute the plugin
    if (!LibraryChecker.checkJava() || !LibraryChecker.checkImageJ()) return;
    HashSet missing = new HashSet();
    LibraryChecker.checkLibrary(LibraryChecker.Library.BIO_FORMATS, missing);
    LibraryChecker.checkLibrary(LibraryChecker.Library.OME_JAVA_XML, missing);
    LibraryChecker.checkLibrary(LibraryChecker.Library.OME_JAVA_DS, missing);
    if (!LibraryChecker.checkMissing(missing)) return;

    promptForLogin();
    if (canceled) {
      canceled = false;
      return;
    }

    uploadStack();
  }

  // -- Helper methods --

  /** Open a dialog box that prompts for a username, password, and server. */
  private void promptForLogin() {
    GenericDialog prompt = new GenericDialog("Login to OME");
    prompt.addStringField("Server:   ", Prefs.get("uploader.server", ""), 60);
    prompt.addStringField("Username: ", Prefs.get("uploader.user", ""), 60);
    prompt.addStringField("Password: ", "", 60);

    ((TextField) prompt.getStringFields().get(2)).setEchoChar('*');
    prompt.showDialog();

    if (prompt.wasCanceled()) {
      canceled = true;
      return;
    }

    server = prompt.getNextString();
    user = prompt.getNextString();
    pass = prompt.getNextString();

    Prefs.set("uploader.server", server);
    Prefs.set("uploader.user", user);
  }

  /** Log in to the OME server and upload the current image stack. */
  private void uploadStack() {
    try {
      IJ.showStatus("Starting upload...");
      OMEWriter ul = new OMEWriter();
      String id = server + "?user=" + user + "&password=" + pass;

      ImagePlus imp = WindowManager.getCurrentImage();
      if (imp == null) {
        IJ.error("No open images!");
        IJ.showStatus("");
        return;
      }
      ImageStack is = imp.getImageStack();
      FileInfo fi = imp.getOriginalFileInfo();
      MetadataStore store = null;

      OMEXMLService service = null;
      try {
        ServiceFactory factory = new ServiceFactory();
        service = (OMEXMLService) factory.getInstance(OMEXMLService.class);
      }
      catch (DependencyException e) {
        IJ.error("OME-XML library not found.");
      }

      // if we opened this stack with the Bio-Formats importer, then the
      // appropriate OME-XML is in fi.description
      if (fi != null && fi.description != null &&
        fi.description.endsWith("</OME>"))
      {
        Exception exc = null;
        try {
          store = service.createOMEXMLMetadata(fi.description);
        }
        catch (ServiceException e) { exc = e; }

        if (exc != null) {
          IJ.error("Could not create OMEXMLMetadataStore");
        }
      }
      else {
        Exception exc = null;
        try {
          store = service.createOMEXMLMetadata();
        }
        catch (ServiceException e) { exc = e; }

        if (exc != null) {
          IJ.error("Could not create OMEXMLMetadataStore");
        }

        int pixelType = FormatTools.UINT8;
        switch (imp.getBitDepth()) {
          case 16: pixelType = FormatTools.UINT16; break;
          case 32: pixelType = FormatTools.FLOAT; break;
        }

        store.setPixelsSizeX(
          new PositiveInteger(new Integer(imp.getWidth())), 0);
        store.setPixelsSizeY(
          new PositiveInteger(new Integer(imp.getHeight())), 0);
        store.setPixelsSizeZ(
          new PositiveInteger(new Integer(imp.getNSlices())), 0);
        store.setPixelsSizeC(
          new PositiveInteger(new Integer(imp.getNChannels())), 0);
        store.setPixelsSizeT(
          new PositiveInteger(new Integer(imp.getNFrames())), 0);
        try {
          store.setPixelsType(
            PixelType.fromString(FormatTools.getPixelTypeString(pixelType)), 0);
        }
        catch (EnumerationException e) { }
        store.setPixelsBinDataBigEndian(fi == null ?
          Boolean.TRUE : new Boolean(!fi.intelByteOrder), 0, 0);
        // TODO : figure out a way to calculate the dimension order
        store.setPixelsDimensionOrder(DimensionOrder.XYCZT, 0);

        String name = fi == null ? imp.getTitle() : fi.fileName;
        store.setImageName(name, 0);
        if (fi != null) store.setImageDescription(fi.info, 0);
      }
      MetadataRetrieve retrieve = (MetadataRetrieve) store;
      ul.setMetadataRetrieve(retrieve);
      ul.setId(id);

      boolean little = !retrieve.getPixelsBinDataBigEndian(0, 0).booleanValue();

      for (int i=0; i<is.getSize(); i++) {
        IJ.showStatus("Reading plane " + (i+1) + "/" + is.getSize());
        Object pix = is.getProcessor(i + 1).getPixels();

        byte[] toUpload = null;

        if (pix instanceof byte[]) {
          toUpload = (byte[]) pix;
        }
        else if (pix instanceof short[]) {
          short[] s = (short[]) pix;
          toUpload = new byte[s.length * 2];
          for (int j=0; j<s.length; j++) {
            DataTools.unpackBytes(s[j], toUpload, j * 2, 2, little);
          }
        }
        else if (pix instanceof int[]) {
          if (is.getProcessor(i+1) instanceof ColorProcessor) {
            byte[][] rgb = new byte[3][((int[]) pix).length];
            ((ColorProcessor) is.getProcessor(i+1)).getRGB(rgb[0],
              rgb[1], rgb[2]);
            int channels =
              ((Integer) retrieve.getPixelsSizeC(0).getValue()).intValue();
            if (channels > 3) channels = 3;
            toUpload = new byte[channels * rgb[0].length];
            for (int j=0; j<channels; j++) {
              System.arraycopy(rgb[j], 0, toUpload, 0, rgb[j].length);
            }
          }
          else {
            int[] p = (int[]) pix;
            toUpload = new byte[4 * p.length];
            for (int j=0; j<p.length; j++) {
              DataTools.unpackBytes(p[j], toUpload, j * 4, 4, little);
            }
          }
        }
        else if (pix instanceof float[]) {
          float[] f = (float[]) pix;
          toUpload = new byte[f.length * 4];
          for (int j=0; j<f.length; j++) {
            int k = Float.floatToIntBits(f[j]);
            DataTools.unpackBytes(k, toUpload, j * 4, 4, little);
          }
        }

        ul.saveBytes(i, toUpload);
      }

      IJ.showStatus("Sending data to server...");
      ul.close();
      IJ.showStatus("Upload finished.");
    }
    catch (Exception e) {
      IJ.error("Upload failed:\n" + e);
      e.printStackTrace();
    }
  }

}
