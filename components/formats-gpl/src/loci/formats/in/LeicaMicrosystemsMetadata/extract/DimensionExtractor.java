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

package loci.formats.in.LeicaMicrosystemsMetadata.extract;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.model.Dimension;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Dimension.DimensionKey;

/**
 * DimensionExtractor is a helper class for extracting image dimension information from LMS XML files.
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class DimensionExtractor extends Extractor {

    /**
   * Returns a list of {@link Dimension}s it extracts from LMS XML image description
   */
  public static List<Dimension> extractDimensions(Element imageDescription, boolean useOldPhysicalSizeCalculation) {
    List<Dimension> dimensions = new ArrayList<Dimension>();

    Element dimensionsNode = (Element) imageDescription.getElementsByTagName("Dimensions").item(0);
    NodeList dimensionNodes = dimensionsNode.getElementsByTagName("DimensionDescription");

    // add dimensions
    for (int i = 0; i < dimensionNodes.getLength(); i++) {
      Element dimensionElement = (Element) dimensionNodes.item(i);

      int id = parseInt(dimensionElement.getAttribute("DimID"));
      int size = parseInt(dimensionElement.getAttribute("NumberOfElements"));
      long bytesInc = parseLong(dimensionElement.getAttribute("BytesInc"));
      Double length = parseDouble(dimensionElement.getAttribute("Length"));
      String unit = dimensionElement.getAttribute("Unit");
      double origin = parseDouble(dimensionElement.getAttribute("Origin"));

      Dimension dimension = new Dimension(DimensionKey.with(id), size, bytesInc, unit, length, origin, useOldPhysicalSizeCalculation);
      dimensions.add(dimension);
    }

    return dimensions;
  }
}
