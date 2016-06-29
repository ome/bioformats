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

import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;

/**
 * Demonstration of the sub-resolution API.
 */
public class SubResolutionExample {

  public static void main(String[] args) throws FormatException, IOException {
    // parse command line arguments
    if (args.length < 1) {
      System.err.println("Usage: java SubResolutionExample imageFile");
      System.exit(1);
    }
    String id = args[0];

    // configure reader
    IFormatReader reader = new ImageReader();
    reader.setFlattenedResolutions(false);
    System.out.println("Initializing file: " + id);
    reader.setId(id); // parse metadata

    int seriesCount = reader.getSeriesCount();

    System.out.println("  Series count = " + seriesCount);

    for (int series=0; series<seriesCount; series++) {
      reader.setSeries(series);
      int resolutionCount = reader.getResolutionCount();

      System.out.println("    Resolution count for series #" + series +
        " = " + resolutionCount);

      for (int r=0; r<resolutionCount; r++) {
        reader.setResolution(r);
        System.out.println("      Resolution #" + r + " dimensions = " +
          reader.getSizeX() + " x " + reader.getSizeY());
      }
    }

    reader.close();
  }

}
