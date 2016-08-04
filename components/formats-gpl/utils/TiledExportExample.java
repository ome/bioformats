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

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.ImageReader;
import loci.formats.ImageWriter;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;

/**
 */
public class TiledExportExample {
  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      System.out.println("Usage: java TiledExportExample <infile> <outfile>");
      System.exit(1);
    }

    ImageReader reader = new ImageReader();
    ImageWriter writer = new ImageWriter();
    IMetadata meta;

    try {
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      meta = service.createOMEXMLMetadata();
    }
    catch (DependencyException exc) {
      throw new FormatException("Could not create OME-XML store.", exc);
    }
    catch (ServiceException exc) {
      throw new FormatException("Could not create OME-XML store.", exc);
    }

    reader.setMetadataStore(meta);

    reader.setId(args[0]);
    writer.setMetadataRetrieve(meta);
    writer.setId(args[1]);

    for (int series=0; series<reader.getSeriesCount(); series++) {
      reader.setSeries(series);
      writer.setSeries(series);

      for (int image=0; image<reader.getImageCount(); image++) {
        for (int row=0; row<2; row++) {
          for (int col=0; col<2; col++) {
            int w = reader.getSizeX() / 2;
            int h = reader.getSizeY() / 2;
            int x = col * w;
            int y = row * h;
            /* debug */ System.out.println("[" + x + ", " + y + ", " + w + ", " + h + "]");
            byte[] buf = reader.openBytes(image, x, y, w, h);
            writer.saveBytes(image, buf, x, y, w, h);
          }
        }
      }
    }

    reader.close();
    writer.close();
  }
}
