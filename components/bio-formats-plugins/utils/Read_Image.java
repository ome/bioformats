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
import loci.plugins.util.ImageProcessorReader;
import loci.plugins.util.LociPrefs;

/**
 * An ImageJ plugin that uses Bio-Formats to build up an {@link ImageStack},
 * reading image planes one by one.
 */
public class Read_Image implements PlugIn {
  public void run(String arg) {
    OpenDialog od = new OpenDialog("Open Image File...", arg);
    String dir = od.getDirectory();
    String name = od.getFileName();
    String id = dir + name;
    ImageProcessorReader r = new ImageProcessorReader(
      new ChannelSeparator(LociPrefs.makeImageReader()));
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
