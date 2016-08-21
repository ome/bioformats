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
import loci.formats.ImageReader;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;
import loci.formats.out.OMETiffWriter;

/**
 * Converts the given files to OME-TIFF format.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/utils/ConvertToOmeTiff.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/utils/ConvertToOmeTiff.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class ConvertToOmeTiff {

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println("Usage: java ConvertToOmeTiff file1 file2 ...");
      return;
    }
    ImageReader reader = new ImageReader();
    OMETiffWriter writer = new OMETiffWriter();
    for (int i=0; i<args.length; i++) {
      String id = args[i];
      int dot = id.lastIndexOf(".");
      String outId = (dot >= 0 ? id.substring(0, dot) : id) + ".ome.tif";
      System.out.print("Converting " + id + " to " + outId + " ");

      // record metadata to OME-XML format
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      IMetadata omexmlMeta = service.createOMEXMLMetadata();
      reader.setMetadataStore(omexmlMeta);
      reader.setId(id);

      // configure OME-TIFF writer
      writer.setMetadataRetrieve(omexmlMeta);
      writer.setId(outId);
      //writer.setCompression("J2K");

      // write out image planes
      int seriesCount = reader.getSeriesCount();
      for (int s=0; s<seriesCount; s++) {
        reader.setSeries(s);
        writer.setSeries(s);
        int planeCount = reader.getImageCount();
        for (int p=0; p<planeCount; p++) {
          byte[] plane = reader.openBytes(p);
          // write plane to output file
          writer.saveBytes(p, plane);
          System.out.print(".");
        }
      }
      writer.close();
      reader.close();
      System.out.println(" [done]");
    }
  }

}
