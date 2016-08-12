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
import loci.formats.FormatTools;
import loci.formats.ImageReader;
import loci.formats.ImageWriter;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;
import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.unit.Unit;
import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;

/**
 * Example class that shows how to read and convert the physical X, Y and Z dimensions of a file
 * Bio-Formats version 5.2 or later.
 */
public class ReadPhysicalSize {
    
    /**
     * Construct a new ReadPhysicalSize that will read and convert the physical dimensions of a file
     *
     * @param inputFile the file to be read
     */
    public ReadPhysicalSize(String inputFile) {
        ImageReader reader = new ImageReader();
        IMetadata omeMeta = MetadataTools.createOMEXMLMetadata();
        reader.setMetadataStore(omeMeta);
        reader.setId(inputFile);
        
        Unit<Length> targetUnit = UNITS.MICROMETER;
        
        for (int series=0; series<reader.getSeriesCount(); series++) {
            reader.setSeries(series);
            for (int image=0; image<reader.getImageCount(); image++) {
                Length physSizeX = omeMeta.getPixelsPhysicalSizeX(image);
                Length physSizeY = omeMeta.getPixelsPhysicalSizeY(image);
                Length physSizeZ = omeMeta.getPixelsPhysicalSizeZ(image);
                
                System.out.println("Physical calibration - Series: " + series + " Image: " + image);
                
                if (physSizeX != null) {
                    Length convertedSizeX = new Length(physSizeX.value(targetUnit), targetUnit);
                    System.out.println("\tX = " + physSizeX.value() + " " + physSizeX.unit().getSymbol()
                                       + " = " + convertedSizeX.value() + " " + convertedSizeX.unit().getSymbol());
                }
                if (physSizeY != null) {
                    Length convertedSizeY = new Length(physSizeY.value(targetUnit), targetUnit);
                    System.out.println("\tY = " + physSizeY.value() + " " + physSizeY.unit().getSymbol()
                                       + " = " + convertedSizeY.value() + " " + convertedSizeY.unit().getSymbol());
                }
                if (physSizeZ != null) {
                    Length convertedSizeZ = new Length(physSizeZ.value(targetUnit), targetUnit);
                    System.out.println("\tZ = " + physSizeZ.value() + " " + physSizeZ.unit().getSymbol()
                                       + " = " + convertedSizeZ.value() + " " + convertedSizeZ.unit().getSymbol());
                }
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
        ReadPhysicalSize exporter = new ReadPhysicalSize(args[0]);
        
    }
    
}