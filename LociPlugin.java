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
      ImageStack stackB = null, stackS = null, stackF = null, stackO = null;
      long start = System.currentTimeMillis();
      long time = start;
      for (int i=0; i<num; i++) {
        // limit message update rate
        long clock = System.currentTimeMillis();
        if (clock - time >= 50) {
          IJ.showStatus("Reading plane " + (i + 1) + "/" + num);
          time = clock;
        }
        IJ.showProgress((double) i / num);
        BufferedImage img = reader.open(id, i);

        ImageProcessor ip = null;
        WritableRaster raster = img.getRaster();
        int c = raster.getNumBands();
        int tt = raster.getTransferType();
        int w = img.getWidth(), h = img.getHeight();
        if (c == 1) {
          if (tt == DataBuffer.TYPE_BYTE) {
            byte[] b = ImageTools.getBytes(img)[0];
            ip = new ByteProcessor(w, h, b, null);
            if (stackB == null) stackB = new ImageStack(w, h);
            stackB.addSlice(fileName + ":" + (i + 1), ip);
          }
          else if (tt == DataBuffer.TYPE_USHORT) {
            short[] s = ImageTools.getShorts(img)[0];
            ip = new ShortProcessor(w, h, s, null);
            if (stackS == null) stackS = new ImageStack(w, h);
            stackS.addSlice(fileName + ":" + (i + 1), ip);
          }
          else if (tt == DataBuffer.TYPE_FLOAT) {
            float[] f = ImageTools.getFloats(img)[0];
            ip = new FloatProcessor(w, h, f, null);
            if (stackF == null) stackF = new ImageStack(w, h);
            stackF.addSlice(fileName + ":" + (i + 1), ip);
          }
        }
        if (ip == null) {
          ip = new ImagePlus(null, img).getProcessor(); // slow
          if (stackO == null) stackO = new ImageStack(w, h);
          stackO.addSlice(fileName + ":" + (i + 1), ip);
        }
      }
      IJ.showStatus("Creating image");
      IJ.showProgress(1);
      if (stackB != null) new ImagePlus(fileName, stackB).show();
      if (stackS != null) new ImagePlus(fileName, stackS).show();
      if (stackF != null) new ImagePlus(fileName, stackF).show();
      if (stackO != null) new ImagePlus(fileName, stackO).show();
      long end = System.currentTimeMillis();
      double elapsed = (end - start) / 1000.0;
      if (num == 1) IJ.showStatus(elapsed + " seconds");
      else {
        long average = (end - start) / num;
        IJ.showStatus(elapsed + " seconds (" + average + " ms per plane)");
      }
      success = true;
    }
    catch (Exception exc) {
      exc.printStackTrace();
      IJ.showStatus("");
      if (!quiet) {
        String msg = exc.getMessage();
        IJ.showMessage("LOCI Bio-Formats", "Sorry, there was a problem " +
          "reading the data" + (msg == null ? "." : (": " + msg)));
      }
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
