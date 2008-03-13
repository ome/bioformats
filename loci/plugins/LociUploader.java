//
// LociUploader.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Data Browser, Stack Colorizer,
Stack Slicer, and OME plugins. Copyright (C) 2005-@year@ Melissa Linkert,
Curtis Rueden, Christopher Peterson and Philip Huettl.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.plugins;

import java.awt.TextField;
import ij.*;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ColorProcessor;
import ij.io.FileInfo;
import java.util.HashSet;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.ome.OMEWriter;

/**
 * ImageJ plugin for uploading images to an OME server.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/LociUploader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/LociUploader.java">SVN</a></dd></dl>
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
    if (!Checker.checkJava() || !Checker.checkImageJ()) return;
    HashSet missing = new HashSet();
    Checker.checkLibrary(Checker.BIO_FORMATS, missing);
    Checker.checkLibrary(Checker.OME_JAVA_XML, missing);
    Checker.checkLibrary(Checker.OME_JAVA_DS, missing);
    if (!Checker.checkMissing(missing)) return;

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
      ul.setId(id);

      ImagePlus imp = WindowManager.getCurrentImage();
      if (imp == null) {
        IJ.error("No open images!");
        IJ.showStatus("");
        return;
      }
      ImageStack is = imp.getImageStack();
      FileInfo fi = imp.getOriginalFileInfo();
      MetadataStore store;

      // if we opened this stack with the Bio-Formats importer, then the
      // appropriate OME-XML is in fi.description
      if (fi != null && fi.description != null &&
        fi.description.endsWith("</OME>"))
      {
        store = MetadataTools.createOMEXMLMetadata(fi.description);
      }
      else {
        store = MetadataTools.createOMEXMLMetadata();
        int pixelType = FormatTools.UINT8;
        switch (imp.getBitDepth()) {
          case 16: pixelType = FormatTools.UINT16; break;
          case 32: pixelType = FormatTools.FLOAT; break;
        }

        store.setPixelsSizeX(new Integer(imp.getWidth()), 0, 0);
        store.setPixelsSizeY(new Integer(imp.getHeight()), 0, 0);
        store.setPixelsSizeZ(new Integer(imp.getNSlices()), 0, 0);
        store.setPixelsSizeC(new Integer(imp.getNChannels()), 0, 0);
        store.setPixelsSizeT(new Integer(imp.getNFrames()), 0, 0);
        store.setPixelsPixelType(
          FormatTools.getPixelTypeString(pixelType), 0, 0);
        store.setPixelsBigEndian(fi == null ?
          Boolean.TRUE : new Boolean(!fi.intelByteOrder), 0, 0);
        // TODO : figure out a way to calculate the dimension order
        store.setPixelsDimensionOrder("XYCZT", 0, 0);

        String name = fi == null ? imp.getTitle() : fi.fileName;
        store.setImageName(name, 0);
        if (fi != null) store.setImageDescription(fi.info, 0);
      }
      MetadataRetrieve retrieve = (MetadataRetrieve) store;
      ul.setMetadata(retrieve);

      boolean little = !retrieve.getPixelsBigEndian(0, 0).booleanValue();

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
            toUpload[j*2] = little ? (byte) (s[j] & 0xff) :
              (byte) ((s[j] >>> 8) & 0xff);
            toUpload[j*2 + 1] = little ? (byte) ((s[j] >>> 8) & 0xff):
              (byte) (s[j] & 0xff);
          }
        }
        else if (pix instanceof int[]) {
          if (is.getProcessor(i+1) instanceof ColorProcessor) {
            byte[][] rgb = new byte[3][((int[]) pix).length];
            ((ColorProcessor) is.getProcessor(i+1)).getRGB(rgb[0],
              rgb[1], rgb[2]);
            int channels = retrieve.getPixelsSizeC(0, 0).intValue();
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
              toUpload[j*4] = little ? (byte) (p[j] & 0xff) :
                (byte) ((p[j] >> 24) & 0xff);
              toUpload[j*4 + 1] = little ? (byte) ((p[j] >> 8) & 0xff) :
                (byte) ((p[j] >> 16) & 0xff);
              toUpload[j*4 + 2] = little ? (byte) ((p[j] >> 16) & 0xff) :
                (byte) ((p[j] >> 8) & 0xff);
              toUpload[j*4 + 3] = little ? (byte) ((p[j] >> 24) & 0xff) :
                (byte) (p[j] & 0xff);
            }
          }
        }
        else if (pix instanceof float[]) {
          float[] f = (float[]) pix;
          toUpload = new byte[f.length * 4];
          for (int j=0; j<f.length; j++) {
            int k = Float.floatToIntBits(f[j]);
            toUpload[j*4] = little ? (byte) (k & 0xff) :
              (byte) ((k >> 24) & 0xff);
            toUpload[j*4 + 1] = little ? (byte) ((k >> 8) & 0xff) :
              (byte) ((k >> 16) & 0xff);
            toUpload[j*4 + 2] = little ? (byte) ((k >> 16) & 0xff) :
              (byte) ((k >> 8) & 0xff);
            toUpload[j*4 + 3] = little ? (byte) ((k >> 24) & 0xff) :
              (byte) (k & 0xff);
          }
        }

        ul.saveBytes(toUpload, i == is.getSize() - 1);
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
