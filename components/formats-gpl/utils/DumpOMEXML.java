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
import loci.formats.ImageReader;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;

/**
 * Extracts and prints out the OME-XML for a given file.
 */
public class DumpOMEXML {

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println("Usage: java DumpOMEXML file1 file2 ...");
      return;
    }
    for (int i=0; i<args.length; i++) dumpOMEXML(args[i]);
  }

  public static void dumpOMEXML(String path) throws FormatException,
    IOException, DependencyException, ServiceException
  {
    ServiceFactory serviceFactory = new ServiceFactory();
    OMEXMLService omexmlService =
      serviceFactory.getInstance(OMEXMLService.class);
    IMetadata meta = omexmlService.createOMEXMLMetadata();

    ImageReader r = new ImageReader();
    r.setMetadataStore(meta);
    r.setOriginalMetadataPopulated(true);
    r.setId(path);
    r.close();
    String xml = omexmlService.getOMEXML(meta);
    System.out.println(xml);
  }

}
