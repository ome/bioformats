//
// Read_Image.java
//

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import java.io.IOException;
import loci.formats.ChannelSeparator;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.plugins.Util;

/** A very simple example of using Bio-Formats in an ImageJ plugin. */
public class Read_Image implements PlugIn {
  public void run(String arg) {
    OpenDialog od = new OpenDialog("Open Image File...", arg);
    String dir = od.getDirectory();
    String name = od.getFileName();
    String id = dir + name;
    IFormatReader r = new ChannelSeparator();
    try {
      IJ.showStatus("Examining file " + name);
      r.setId(id);
      int num = r.getImageCount();
      int width = r.getSizeX();
      int height = r.getSizeY();
      ImageStack stack = new ImageStack(width, height);
      for (int i=0; i<num; i++) {
        IJ.showStatus("Reading image plane #" + (i + 1) + "/" + num);
        ImageProcessor ip = Util.openProcessors(r, i)[0];
        stack.addSlice("" + (i + 1), ip);
      }
      IJ.showStatus("Constructing image");
      ImagePlus imp = new ImagePlus(name, stack);
      imp.show();
      IJ.showStatus("");
    }
    catch (FormatException exc) {
      IJ.error("Sorry, an error occurred: " + exc.getMessage());
    }
    catch (IOException exc) {
      IJ.error("Sorry, an error occurred: " + exc.getMessage());
    }
  }
}
