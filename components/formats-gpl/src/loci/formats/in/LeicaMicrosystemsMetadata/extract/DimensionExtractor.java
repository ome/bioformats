package loci.formats.in.LeicaMicrosystemsMetadata.extract;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.model.Dimension;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Dimension.DimensionKey;

public class DimensionExtractor extends Extractor {

    /**
   * Extracts information from dimension descriptions and writes it to reader's
   * {@link CoreMetadata} and {@link MetadataTempBuffer}
   * 
   * @param imageNode
   *          Image node from Leica xml
   * @param coreIndex
   * @throws FormatException
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
