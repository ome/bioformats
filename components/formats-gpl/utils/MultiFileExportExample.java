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

import java.io.IOException;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.ImageReader;
import loci.formats.ImageWriter;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;

/**
 * Writes each Z section in a dataset to a separate file.
 */
public class MultiFileExportExample {
  public static void main(String[] args) throws FormatException, IOException {
    if (args.length < 2) {
      System.out.println(
        "Usage: java MultiFileExportExample <infile> <output file extension>");
      System.exit(1);
    }

    ImageReader reader = new ImageReader();
    IMetadata metadata;

    try {
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      metadata = service.createOMEXMLMetadata();
    }
    catch (DependencyException exc) {
      throw new FormatException("Could not create OME-XML store.", exc);
    }
    catch (ServiceException exc) {
      throw new FormatException("Could not create OME-XML store.", exc);
    }

    reader.setMetadataStore(metadata);
    reader.setId(args[0]);

    ImageWriter writer = new ImageWriter();
    writer.setMetadataRetrieve(metadata);
    String baseFile = args[0].substring(0, args[0].lastIndexOf("."));
    writer.setId(baseFile + "_s0_z0" + args[1]);

    for (int series=0; series<reader.getSeriesCount(); series++) {
      reader.setSeries(series);
      writer.setSeries(series);

      int planesPerFile = reader.getImageCount() / reader.getSizeZ();
      for (int z=0; z<reader.getSizeZ(); z++) {
        String file = baseFile + "_s" + series + "_z" + z + args[1];
        writer.changeOutputFile(file);
        for (int image=0; image<planesPerFile; image++) {
          int zct[] = FormatTools.getZCTCoords(reader.getDimensionOrder(),
            1, reader.getEffectiveSizeC(), reader.getSizeT(),
            planesPerFile, image);
          int index = FormatTools.getIndex(reader, z, zct[1], zct[2]);
          writer.saveBytes(image, reader.openBytes(index));
        }
      }
    }

    reader.close();
    writer.close();
  }
}
