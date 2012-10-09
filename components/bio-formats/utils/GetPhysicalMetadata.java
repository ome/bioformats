/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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

import java.util.Arrays;

import loci.formats.FormatReader;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;

import ome.scifio.common.DateTools;
import ome.scifio.services.ServiceFactory;

import ome.xml.model.primitives.PositiveFloat;

/**
 * Uses Bio-Formats to extract some basic standardized
 * (format-independent) metadata.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/utils/GetPhysicalMetadata.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/utils/GetPhysicalMetadata.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class GetPhysicalMetadata {

  /** Outputs dimensional information. */
  public static void printPixelDimensions(IFormatReader reader) {
    // output dimensional information
    int sizeX = reader.getSizeX();
    int sizeY = reader.getSizeY();
    int sizeZ = reader.getSizeZ();
    int sizeC = reader.getSizeC();
    int sizeT = reader.getSizeT();
    int imageCount = reader.getImageCount();
    System.out.println();
    System.out.println("Pixel dimensions:");
    System.out.println("\tWidth = " + sizeX);
    System.out.println("\tHeight = " + sizeY);
    System.out.println("\tFocal planes = " + sizeZ);
    System.out.println("\tChannels = " + sizeC);
    System.out.println("\tTimepoints = " + sizeT);
    System.out.println("\tTotal planes = " + imageCount);
  }

  /** Outputs global timing details. */
  public static void printPhysicalDimensions(IMetadata meta, int series) {
    PositiveFloat physicalSizeX = meta.getPixelsPhysicalSizeX(series);
    PositiveFloat physicalSizeY = meta.getPixelsPhysicalSizeY(series);
    PositiveFloat physicalSizeZ = meta.getPixelsPhysicalSizeZ(series);
    Double timeIncrement = meta.getPixelsTimeIncrement(series);
    System.out.println();
    System.out.println("Physical dimensions:");
    System.out.println("\tX spacing = " + physicalSizeX + " microns");
    System.out.println("\tY spacing = " + physicalSizeY + " microns");
    System.out.println("\tZ spacing = " + physicalSizeZ + " microns");
    System.out.println("\tTime increment = " + timeIncrement + " seconds");
  }

  public static void main(String[] args) throws Exception {
    // parse command line arguments
    if (args.length < 1) {
      System.err.println("Usage: java GetMetadata imageFile [seriesNo]");
      System.exit(1);
    }
    String id = args[0];
    int series = args.length > 1 ? Integer.parseInt(args[1]) : 0;

    // create OME-XML metadata store
    ServiceFactory factory = new ServiceFactory();
    OMEXMLService service = factory.getInstance(OMEXMLService.class);
    IMetadata meta = service.createOMEXMLMetadata();

    // create format reader
    IFormatReader reader = new ImageReader();
    reader.setMetadataStore(meta);

    // initialize file
    System.out.println("Initializing " + id);
    reader.setId(id);

    int seriesCount = reader.getSeriesCount();
    if (series < seriesCount) reader.setSeries(series);
    series = reader.getSeries();
    System.out.println("\tImage series = " + series + " of " + seriesCount);

    printPixelDimensions(reader);
    printPhysicalDimensions(meta, series);
  }

}
