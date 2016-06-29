/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;

/**
 * Uses Bio-Formats to extract lens numerical aperture
 * in a format-independent manner from a dataset.
 */
public class PrintLensNA {

  public static void main(String[] args)
    throws DependencyException, FormatException, IOException, ServiceException
  {
    // parse command line arguments
    if (args.length < 1) {
      System.err.println("Usage: java PrintLensNA imageFile");
      System.exit(1);
    }
    String id = args[0];

    // configure reader
    IFormatReader reader = new ImageReader();
    ServiceFactory factory = new ServiceFactory();
    OMEXMLService service = factory.getInstance(OMEXMLService.class);
    IMetadata meta = service.createOMEXMLMetadata();
    reader.setMetadataStore(meta);
    System.out.println("Initializing file: " + id);
    reader.setId(id); // parse metadata

    // output metadata values
    int instrumentCount = meta.getInstrumentCount();
    System.out.println("There are " + instrumentCount +
      " instrument(s) associated with this file");
    for (int i=0; i<instrumentCount; i++) {
      int objectiveCount = meta.getObjectiveCount(i);
      System.out.println();
      System.out.println("Instrument #" + i +
        " [" + meta.getInstrumentID(i) + "]: " +
        objectiveCount + " objective(s) found");
      for (int o=0; o<objectiveCount; o++) {
        Double lensNA = meta.getObjectiveLensNA(i, o);
        System.out.println("\tObjective #" + o +
          " [" + meta.getObjectiveID(i, o) + "]: LensNA=" + lensNA);
      }
    }
  }

}
