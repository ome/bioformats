//
// LociPlugin.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

import ij.*;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;
import ij.process.*;
import java.awt.image.*;
import loci.formats.ImageReader;

/**
 * ImageJ plugin for the LOCI Bio-Formats package.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class LociPlugin implements PlugIn {

  // -- Static fields --

  /** Flag indicating whether last operation was successful. */
  public boolean success = false;


  // -- PlugIn API methods --

  /** Executes the plugin. */
  public synchronized void run(String arg) {
    success = false;
    boolean quiet = !"".equals(arg);
    OpenDialog od = new OpenDialog("Open...", arg);
    String directory = od.getDirectory();
    String fileName = od.getFileName();
    if (fileName == null) return;
    String id = directory + fileName;

    IJ.showStatus("Opening " + fileName);
    ImageReader reader = new ImageReader();
    try {
      int num = reader.getImageCount(id);
      ImageStack stack = null;
      for (int i=0; i<num; i++) {
        if (i % 5 == 4) IJ.showStatus("Reading plane " + (i + 1) + "/" + num);
        IJ.showProgress((double) i / num);
        BufferedImage img = reader.open(id, i);
        if (stack == null) {
          stack = new ImageStack(img.getWidth(), img.getHeight());
        }

        ImageProcessor ip = null;
        WritableRaster raster = img.getRaster();
        int c = raster.getNumBands();
        int tt = raster.getTransferType();
        if (c == 1) {
          int w = img.getWidth(), h = img.getHeight();
          if (tt == DataBuffer.TYPE_BYTE) {
            byte[] b = ImageTools.getBytes(img)[0];
            ip = new ByteProcessor(w, h, b, null);
          }
          else if (tt == DataBuffer.TYPE_USHORT) {
            short[] s = ImageTools.getShorts(img)[0];
            ip = new ShortProcessor(w, h, s, null);
          }
          else if (tt == DataBuffer.TYPE_FLOAT) {
            float[] f = ImageTools.getFloats(img)[0];
            ip = new FloatProcessor(w, h, f, null);
          }
        }
        if (ip == null) ip = new ImagePlus(null, img).getProcessor(); // slow

        stack.addSlice(fileName + ":" + (i + 1), ip);
      }
      IJ.showStatus("Creating image");
      IJ.showProgress(1);
      new ImagePlus(fileName, stack).show();
      success = true;
    }
    catch (Exception exc) {
      IJ.showStatus("");
      if (!quiet) IJ.showMessage("LOCI Bio-Formats", "" + exc.getMessage());
    }
  }

}

/*
Add the following code to HandleExtraFileTypes.java to enable
LOCI Bio-Formats through File/Open, drag & drop, etc.

    // CTR: try opening the file with LOCI Bio-Formats
    Object loci = IJ.runPlugIn("LociPlugin", path);
    if (loci != null) {
      // plugin exists and was launched
      try {
        // check whether plugin was successful
        boolean ok = loci.getClass().getField("success").getBoolean(loci);
        if (ok) {
          width = IMAGE_OPENED;
          return null;
        }
      }
      catch (Exception exc) { }
    }
*/
