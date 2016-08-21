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
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;

/**
 * Demonstrates the minimum amount of metadata
 * necessary to write out an image plane.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/utils/MinimumWriter.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/utils/MinimumWriter.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class MinimumWriter {

  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.out.println("Please specify an output file name.");
      System.exit(1);
    }
    String id = args[0];

    // create blank 512x512 image
    System.out.println("Creating random image...");
    int w = 512, h = 512, c = 1;
    int pixelType = FormatTools.UINT16;
    byte[] img = new byte[w * h * c * FormatTools.getBytesPerPixel(pixelType)];

    // fill with random data
    for (int i=0; i<img.length; i++) img[i] = (byte) (256 * Math.random());

    // create metadata object with minimum required metadata fields
    System.out.println("Populating metadata...");

    ServiceFactory factory = new ServiceFactory();
    OMEXMLService service = factory.getInstance(OMEXMLService.class);
    IMetadata meta = service.createOMEXMLMetadata();

    MetadataTools.populateMetadata(meta, 0, null, false, "XYZCT",
      FormatTools.getPixelTypeString(pixelType), w, h, 1, c, 1, c);

    // write image plane to disk
    System.out.println("Writing image to '" + id + "'...");
    IFormatWriter writer = new ImageWriter();
    writer.setMetadataRetrieve(meta);
    writer.setId(id);
    writer.saveBytes(0, img);
    writer.close();

    System.out.println("Done.");
  }

}
