//
// Read_Image.java
//

import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.process.LUT;
import java.io.IOException;
import loci.formats.ChannelSeparator;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.plugins.util.ImagePlusReader;

/** A very simple example of using Bio-Formats in an ImageJ plugin. */
public class Read_Image implements PlugIn {
  public void run(String arg) {
    OpenDialog od = new OpenDialog("Open Image File...", arg);
    String dir = od.getDirectory();
    String name = od.getFileName();
    String id = dir + name;
    ImagePlusReader r = new ImagePlusReader(
      new ChannelSeparator(ImagePlusReader.makeImageReader()));
    try {
      IJ.showStatus("Examining file " + name);
      r.setId(id);
      int num = r.getImageCount();
      int width = r.getSizeX();
      int height = r.getSizeY();
      ImageStack stack = new ImageStack(width, height);
      byte[][][] lookupTable = new byte[r.getSizeC()][][];
      for (int i=0; i<num; i++) {
        IJ.showStatus("Reading image plane #" + (i + 1) + "/" + num);
        ImageProcessor ip = r.openProcessors(i)[0];
        stack.addSlice("" + (i + 1), ip);
        int channel = r.getZCTCoords(i)[1];
        lookupTable[channel] = r.get8BitLookupTable();
      }
      IJ.showStatus("Constructing image");
      ImagePlus imp = new ImagePlus(name, stack);

      ImagePlus colorizedImage = applyLookupTables(r, imp, lookupTable);
      r.close();

      colorizedImage.show();
      IJ.showStatus("");
    }
    catch (FormatException exc) {
      IJ.error("Sorry, an error occurred: " + exc.getMessage());
    }
    catch (IOException exc) {
      IJ.error("Sorry, an error occurred: " + exc.getMessage());
    }
  }

  private ImagePlus applyLookupTables(IFormatReader r, ImagePlus imp,
    byte[][][] lookupTable)
  {
    // apply color lookup tables, if present
    // this requires ImageJ v1.39 or higher
    if (r.isIndexed()) {
      CompositeImage composite =
        new CompositeImage(imp, CompositeImage.COLOR);
      for (int c=0; c<r.getSizeC(); c++) {
        composite.setPosition(c + 1, 1, 1);
        LUT lut =
          new LUT(lookupTable[c][0], lookupTable[c][1], lookupTable[c][2]);
        composite.setChannelLut(lut);
      }
      composite.setPosition(1, 1, 1);
      return composite;
    }
    return imp;
  }
}
