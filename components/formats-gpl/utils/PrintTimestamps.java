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

import java.util.Arrays;

import loci.common.DateTools;
import loci.common.services.ServiceFactory;
import loci.formats.FormatReader;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;

import ome.xml.model.primitives.NonNegativeInteger;
import ome.units.quantity.Time;
import ome.units.UNITS;

/**
 * Uses Bio-Formats to extract timestamp information
 * in a format-independent manner from a dataset.
 */
public class PrintTimestamps {

  /** Outputs dimensional information. */
  public static void printDimensions(IFormatReader reader) {
    // output dimensional information
    int sizeX = reader.getSizeX();
    int sizeY = reader.getSizeY();
    int sizeZ = reader.getSizeZ();
    int sizeC = reader.getSizeC();
    int sizeT = reader.getSizeT();
    int imageCount = reader.getImageCount();
    System.out.println();
    System.out.println("Image dimensions:");
    System.out.println("\tWidth = " + sizeX);
    System.out.println("\tHeight = " + sizeY);
    System.out.println("\tFocal planes = " + sizeZ);
    System.out.println("\tChannels = " + sizeC);
    System.out.println("\tTimepoints = " + sizeT);
    System.out.println("\tTotal planes = " + imageCount);
  }

  /** Outputs global timing details. */
  public static void printGlobalTiming(IMetadata meta, int series) {
    String imageName = meta.getImageName(series);
    String creationDate = null;
    if (meta.getImageAcquisitionDate(series) != null) {
      creationDate = meta.getImageAcquisitionDate(series).getValue();
    }
    Time timeInc = meta.getPixelsTimeIncrement(series);
    System.out.println();
    System.out.println("Global timing information:");
    System.out.println("\tImage name = " + imageName);
    System.out.println("\tCreation date = " + creationDate);
    if (creationDate != null) {
      System.out.println("\tCreation time (in ms since epoch) = " +
        DateTools.getTime(creationDate, DateTools.ISO8601_FORMAT));
    }
    System.out.println("\tTime increment (in seconds) = " + timeInc.value(UNITS.SECOND).doubleValue());
  }

  /** Outputs timing details per timepoint. */
  public static void printTimingPerTimepoint(IMetadata meta, int series) {
    System.out.println();
    System.out.println(
      "Timing information per timepoint (from beginning of experiment):");
    int planeCount = meta.getPlaneCount(series);
    for (int i = 0; i < planeCount; i++) {
      Time deltaT = meta.getPlaneDeltaT(series, i);
      if (deltaT == null) continue;
      // convert plane ZCT coordinates into image plane index
      int z = meta.getPlaneTheZ(series, i).getValue().intValue();
      int c = meta.getPlaneTheC(series, i).getValue().intValue();
      int t = meta.getPlaneTheT(series, i).getValue().intValue();
      if (z == 0 && c == 0) {
        System.out.println("\tTimepoint #" + t + " = " + deltaT.value(UNITS.SECOND).doubleValue() + " s");
      }
    }
  }

  /**
   * Outputs timing details per plane.
   *
   * This information may seem redundant or unnecessary, but it is possible
   * that two image planes recorded at the same timepoint actually have
   * slightly different timestamps. Thus, OME allows for recording a separate
   * timestamp for every individual image plane.
   */
  public static void printTimingPerPlane(IMetadata meta, int series) {
    System.out.println();
    System.out.println(
      "Timing information per plane (from beginning of experiment):");
    int planeCount = meta.getPlaneCount(series);
    for (int i = 0; i < planeCount; i++) {
      Time deltaT = meta.getPlaneDeltaT(series, i);
      if (deltaT == null) continue;
      // convert plane ZCT coordinates into image plane index
      int z = meta.getPlaneTheZ(series, i).getValue().intValue();
      int c = meta.getPlaneTheC(series, i).getValue().intValue();
      int t = meta.getPlaneTheT(series, i).getValue().intValue();
      System.out.println("\tZ " + z + ", C " + c + ", T " + t + " = " +
        deltaT.value(UNITS.SECOND).doubleValue() + " s");
    }
  }

  public static void main(String[] args) throws Exception {
    // parse command line arguments
    if (args.length < 1) {
      System.err.println("Usage: java PrintTimestamps imageFile [seriesNo]");
      System.exit(1);
    }
    String id = args[0];
    int series = args.length > 1 ? Integer.parseInt(args[1]) : 0;

    // enable debugging
    //FormatReader.debug = true;

    // create OME-XML metadata store of the latest schema version
    ServiceFactory factory = new ServiceFactory();
    OMEXMLService service = factory.getInstance(OMEXMLService.class);
    IMetadata meta = service.createOMEXMLMetadata();
    // or if you want a specific schema version, you can use:
    //IMetadata meta = service.createOMEXMLMetadata(null, "2009-02");
    //meta.createRoot();

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

    printDimensions(reader);
    printGlobalTiming(meta, series);
    printTimingPerTimepoint(meta, series);
    printTimingPerPlane(meta, series);
  }

}
