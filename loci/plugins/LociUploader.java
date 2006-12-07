//
// LociUploader.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the 4D
Data Browser, OME Plugin and Bio-Formats Exporter. Copyright (C) 2006
Melissa Linkert, Christopher Peterson, Curtis Rueden, Philip Huettl
and Francis Wong.

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
import java.util.Vector;
import ij.*;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.*;
import ij.io.FileInfo;
import loci.formats.*;
import loci.ome.upload.*;

/**
 * ImageJ plugin for uploading images to an OME server.
 *
 * @author Melissa Linkert linket at cs.wisc.edu
 */
public class LociUploader implements PlugIn {

  // -- Fields --

  private String server;
  private String user;
  private String pass;

  // -- PlugIn API methods --
 
  public synchronized void run(String arg) {
    // check that we can safely execute the plugin

    if (Util.checkVersion() && Util.checkLibraries(true, true, true, false)) {
      promptForLogin();
      uploadStack();
    }
    else {
    }
  }

  // -- Helper methods --
 
  /** Open a dialog box that prompts for a username, password, and server. */
  private void promptForLogin() {
    GenericDialog prompt = new GenericDialog("Login to OME"); 
    prompt.addStringField("Server:   ", Prefs.get("uploader.server", ""), 120);
    prompt.addStringField("Username: ", Prefs.get("uploader.user", ""), 120);
    prompt.addStringField("Password: ", "", 120);

    ((TextField) prompt.getStringFields().get(2)).setEchoChar('*');
    prompt.showDialog();

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
      OMEUploader ul = new OMEUploader(server, user, pass);
      ImagePlus imp = WindowManager.getCurrentImage();
      if (imp == null) {
        IJ.error("No open images!");
        IJ.showStatus("");
        return;
      }
      ImageStack is = imp.getImageStack();
      Vector pixels = new Vector();
      FileInfo fi = imp.getOriginalFileInfo();
      OMEXMLMetadataStore store = new OMEXMLMetadataStore();
      
      // if we opened this stack with the Bio-Formats importer, then the
      // appropriate OME-XML is in fi.description
      if (fi.description != null && fi.description.endsWith("</OME>")) {
        store.createRoot(fi.description);  
      }
      else {
        store.createRoot();
        int pixelType = FormatReader.UINT8;
        switch (imp.getBitDepth()) {
          case 16: pixelType = FormatReader.UINT16; break;
          case 32: pixelType = FormatReader.FLOAT; break;
        }

        store.setPixels(
          new Integer(imp.getWidth()),  
          new Integer(imp.getHeight()),
          new Integer(imp.getNSlices()),
          new Integer(imp.getNChannels()),
          new Integer(imp.getNFrames()),
          new Integer(pixelType),
          new Boolean(!fi.intelByteOrder),
          "XYCZT",  // TODO : figure out a way to calculate the dimension order
          null);
      
        store.setImage(fi.fileName, null, fi.info, null);
      
      }
      boolean little = !store.getBigEndian(null).booleanValue();
      
      for (int i=0; i<is.getSize(); i++) {
        IJ.showStatus("Reading plane " + (i+1) + "/" + is.getSize());
        Object pix = is.getProcessor(i + 1).getPixels();
      
        if (pix instanceof byte[]) {
          pixels.add((byte[]) pix);    
        }
        else if (pix instanceof short[]) {
          short[] s = (short[]) pix;
          byte[] b = new byte[s.length * 2];
          for (int j=0; j<s.length; j++) {
            byte[] a = DataTools.shortToBytes(s[j], little);
            b[i*2] = a[0];
            b[i*2 + 1] = a[1];
          }
          pixels.add(b);
        }
        else if (pix instanceof int[]) {
          int[] j = (int[]) pix;
          int channels = is.getProcessor(i+1) instanceof ColorProcessor ? 3 : 1;
          byte[] b = new byte[j.length * channels];
          for (int k=0; k<j.length; k++) {
            byte[] a = DataTools.intToBytes(j[k], little);
            b[k*3] = a[0];
            b[k*3 + 1] = a[1];
            b[k*3 + 2] = a[2];
          }
          pixels.add(b);
        }
        else if (pix instanceof float[]) {
          float[] f = (float[]) pix;
          byte[] b = new byte[f.length * 4];
          for (int j=0; j<f.length; j++) {
            int k = Float.floatToIntBits(f[j]);
            byte[] a = DataTools.intToBytes(k, little);
            b[i*4] = a[0];
            b[i*4 + 1] = a[1];
            b[i*4 + 2] = a[2];
            b[i*4 + 3] = a[3];
          }
          pixels.add(b);
        }
      }

      byte[][] planes = new byte[pixels.size()][];
      for (int i=0; i<pixels.size(); i++) {
        planes[i] = (byte[]) pixels.get(i);
      }

      IJ.showStatus("Sending data to server...");
      ul.uploadPlanes(planes, 0, planes.length - 1, 1, store, true);
      ul.logout();
      IJ.showStatus("Upload finished.");
    }
    catch (UploadException e) {
      IJ.error("Upload failed:\n" + e);
    }
  }

}
