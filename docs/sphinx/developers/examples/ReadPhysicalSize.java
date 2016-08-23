/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2016 Open Microscopy Environment:
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
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.unit.Unit;

/**
 * Example class that shows how to read and convert the physical X, Y and Z dimensions of a file
 * Bio-Formats version 5.1 or later.
 */
public class ReadPhysicalSize {

  /**
   * Reads the physical dimensions of the input file provided then converts and displays them in micrometers
   *
   * @param inputFile the file to be read
   * @throws FormatException if a parsing error occurs processing the file.
   * @throws IOException if an I/O error occurs processing the file
   */
  public static void readPhysicalSize(final String inputFile) throws FormatException, IOException {
    final ImageReader reader = new ImageReader();
    final IMetadata omeMeta = MetadataTools.createOMEXMLMetadata();
    reader.setMetadataStore(omeMeta);
    reader.setId(inputFile);

    final Unit<Length> targetUnit = UNITS.MICROMETER;

    for (int image=0; image<omeMeta.getImageCount(); image++) {
      final Length physSizeX = omeMeta.getPixelsPhysicalSizeX(image);
      final Length physSizeY = omeMeta.getPixelsPhysicalSizeY(image);
      final Length physSizeZ = omeMeta.getPixelsPhysicalSizeZ(image);

      System.out.println("Physical calibration - Image: " + image);

      if (physSizeX != null) {
        final Length convertedSizeX = new Length(physSizeX.value(targetUnit), targetUnit);
        System.out.println("\tX = " + physSizeX.value() + " " + physSizeX.unit().getSymbol()
            + " = " + convertedSizeX.value() + " " + convertedSizeX.unit().getSymbol());
      }
      if (physSizeY != null) {
        final Length convertedSizeY = new Length(physSizeY.value(targetUnit), targetUnit);
        System.out.println("\tY = " + physSizeY.value() + " " + physSizeY.unit().getSymbol()
            + " = " + convertedSizeY.value() + " " + convertedSizeY.unit().getSymbol());
      }
      if (physSizeZ != null) {
        final Length convertedSizeZ = new Length(physSizeZ.value(targetUnit), targetUnit);
        System.out.println("\tZ = " + physSizeZ.value() + " " + physSizeZ.unit().getSymbol()
            + " = " + convertedSizeZ.value() + " " + convertedSizeZ.unit().getSymbol());
      }
    }
    reader.close();
  }
    
  /**
   * To read the physical size dimensions and units of a file and display them in micrometers:
   *
   * $ java ReadPhysicalSize input-file.ome.tiff
   */
  public static void main(String[] args) throws Exception {
    readPhysicalSize(args[0]);
  }

}
