//
// LociExporter.java
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
import ij.plugin.filter.*;
import ij.process.*;
import ij.io.*;
import loci.formats.ImageWriter;

/**
 * ImageJ plugin for writing files using the LOCI Bio-Formats package.
 *
 * @author Melissa Linkert, linkert at cs.wisc.edu
 */
public class LociExporter implements PlugInFilter {

  // -- Static fields --

  /** Flag indicating whether last operation was successful. */
  public boolean success = false;

  // -- Fields --
  
  /** Current stack. */
  private ImagePlus imp;
  
  
  // -- PlugInFilter API methods --

  /** Sets up the writer. */
  public int setup(String arg, ImagePlus imp) {
    this.imp = imp;
    return DOES_ALL + NO_CHANGES;
  }
  
  /** Executes the plugin. */
  public synchronized void run(ImageProcessor ip) {
    success = false;
   
    // prompt for the filename to save to  
          
    SaveDialog sd = new SaveDialog("Save...", imp.getTitle(), "");
    String filename = sd.getFileName();
    if (filename == null) return;
    String fileDir = sd.getDirectory();
    filename = fileDir + filename; 

    try {
      long t1 = System.currentTimeMillis();
      ImageWriter writer = new ImageWriter();
      FormatWriter w2 = writer.getWriter(filename);
      
      if (imp == null) return;

      ImageStack stack = imp.getStack();
      int size = imp.getStackSize();
      
      long t3 = System.currentTimeMillis();
      for (int i=0; i<size; i++) {
        ImageProcessor proc = stack.getProcessor(i+1);
        Image img = proc.createImage(); 
        w2.setColorModel(proc.getColorModel());
        IJ.showStatus("writing plane " + (i+1) + " of " + size);
        w2.save(filename, img, i == (size - 1));         
      }
      long t4 = System.currentTimeMillis();
      if (size == 1) {
        IJ.showStatus((t4 - t1) + " ms");
      }
      else {
        long average = (t4 - t3) / size;
        IJ.showStatus((t4 - t1) + " ms (" + average + " ms per plane)");
      }       
      success = true;
    }
    catch (Exception exc) {
      exc.printStackTrace();
      IJ.showStatus("");
      String msg = exc.getMessage();
      IJ.showMessage("LOCI Bio-Formats", "Sorry, there was a problem " +
        "writing the data" + (msg == null ? "." : (": " + msg)));
    }    
  }

}
