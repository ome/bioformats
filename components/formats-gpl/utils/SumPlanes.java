/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

import java.awt.image.*;
import loci.formats.*;
import loci.formats.gui.BufferedImageReader;
import loci.formats.gui.BufferedImageWriter;

/**
 * Sums together the image planes from the given file,
 * and saves the result to a 16-bit TIFF.
 */
public class SumPlanes {

  public static void main(String[] args) throws Exception {
    String id = args[0];
    BufferedImageReader r = new BufferedImageReader();
    System.out.print("Reading " + id);
    r.setId(id);
    int imageCount = r.getImageCount();
    BufferedImage[] images = new BufferedImage[imageCount];
    for (int i=0; i<imageCount; i++) {
      System.out.print(".");
      images[i] = r.openImage(i);
    }
    r.close();
    System.out.println(" [done]");

    String outId = id + ".tif";
    BufferedImageWriter w = new BufferedImageWriter();
    System.out.print("Writing " + outId);
    w.setId(outId);
    w.saveImage(0, sum(images));
    w.close();
    System.out.println(" [done]");
  }

   public static BufferedImage sum(BufferedImage[] images) {
    // Assuming that all images have the same dimensions and type
    int w = images[0].getWidth();
    int h = images[0].getHeight();
    //int type = images[0].getType();

    BufferedImage result = new BufferedImage(w, h,
      BufferedImage.TYPE_USHORT_GRAY); // type == 0 for some reason...
    WritableRaster raster = result.getRaster().createCompatibleWritableRaster();
    int bands = raster.getNumBands();

    for (int y=0; y<h; y++) {
      for (int x=0; x<w; x++) {
        for (int b=0; b<bands; b++) {
          float sum = 0;
          for (int i=0; i<images.length; i++) {
            sum = sum + images[i].getRaster().getSample(x, y, b);
          }
          raster.setSample(x, y, b, sum);
        }
      }
    }
    result.setData(raster);
    return result;
  }

}
