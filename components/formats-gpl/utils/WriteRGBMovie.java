/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

import loci.common.services.ServiceFactory;
import loci.formats.*;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;

import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;

/**
 * Demonstrates writing multiple RGB image planes to a movie.
 */
public class WriteRGBMovie {

  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.out.println("Please specify an output file name.");
      System.exit(1);
    }
    String id = args[0];

    // create 20 blank 512x512 image planes
    System.out.println("Creating random image planes...");
    int w = 511, h = 507, numFrames = 20, numChannels = 3;
    int pixelType = FormatTools.UINT8;
    int bpp = FormatTools.getBytesPerPixel(pixelType);
    int planeSize = h * w * numChannels * bpp;
    byte[][] img = new byte[numFrames][planeSize];

    // fill with random data
    for (int t=0; t<numFrames; t++) {
      for (int i=0; i<img[t].length; i+=numChannels) {
        for (int c=0; c<numChannels; c++) {
          img[t][i + c] = (byte) (256 * Math.random());
        }
      }
    }

    // create metadata object with required metadata fields
    System.out.println("Populating metadata...");
    ServiceFactory factory = new ServiceFactory();
    OMEXMLService service = factory.getInstance(OMEXMLService.class);
    IMetadata meta = service.createOMEXMLMetadata();

    MetadataTools.populateMetadata(meta, 0, null, false, "XYZCT",
      FormatTools.getPixelTypeString(pixelType), w, h, 1, numChannels,
      numFrames, numChannels);

    // write image planes to disk
    System.out.print("Writing planes to '" + id + "'");
    IFormatWriter writer = new ImageWriter();
    writer.setMetadataRetrieve(meta);
    writer.setId(id);
    for (int t=0; t<numFrames; t++) {
      System.out.print(".");
      writer.saveBytes(t, img[t]);
    }
    writer.close();

    System.out.println("Done.");
  }

}
