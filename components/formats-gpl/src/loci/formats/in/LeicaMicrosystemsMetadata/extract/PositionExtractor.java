package loci.formats.in.LeicaMicrosystemsMetadata.extract;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.helpers.Tuple;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DimensionStore;
import ome.units.UNITS;
import ome.units.quantity.Length;

public class PositionExtractor extends Extractor {
  public static void extractFieldPositions(Element imageNode, Element setting, DimensionStore dimensionStore){
    NodeList attachments = Extractor.getDescendantNodesWithName(imageNode, "Attachment");
    Element tilescanInfo = (Element)Extractor.getNodeWithAttribute(attachments, "Name", "TileScanInfo");
    
    //XY positions
    if (tilescanInfo != null){
      NodeList tiles = tilescanInfo.getChildNodes();
      for (int i = 0; i < tiles.getLength(); i++){
        Element tile;
        try {
          tile = (Element)tiles.item(i);
        } catch (Exception e){
          continue;
        }

        String fieldPosXS = getAttributeValue(tile, "PosX");
        Length fieldPosX = parseLength(fieldPosXS, UNITS.METER);
        String fieldPosYS = getAttributeValue(tile, "PosY");
        Length fieldPosY = parseLength(fieldPosYS, UNITS.METER);
        dimensionStore.fieldPositions.add(new Tuple<Length,Length>(fieldPosX, fieldPosY));

      }
    }

    if (dimensionStore.fieldPositions.size() == 0){
      String fieldPosXS = getAttributeValue(setting, "StagePosX");
      Length fieldPosX = parseLength(fieldPosXS, UNITS.METER);
      String fieldPosYS = getAttributeValue(setting, "StagePosY");
      Length fieldPosY = parseLength(fieldPosYS, UNITS.METER);
      dimensionStore.fieldPositions.add(new Tuple<Length,Length>(fieldPosX, fieldPosY));
    }

    String flipXS = getAttributeValue(setting, "FlipX");
    dimensionStore.flipX = flipXS.equals("1");
    
    String flipYS = getAttributeValue(setting, "FlipY");
    dimensionStore.flipY = flipYS.equals("1");

    String swapXYS = getAttributeValue(setting, "FlipY");
    dimensionStore.swapXY = swapXYS.equals("1");

    for (int planeIndex = 0; planeIndex < dimensionStore.fieldPositions.size(); planeIndex++){
      Tuple<Length,Length> fieldPosition = dimensionStore.fieldPositions.get(planeIndex);

      if (dimensionStore.swapXY){
        Length temp = fieldPosition.x;
        fieldPosition.x = fieldPosition.y;
        fieldPosition.y = temp;
      }
  
      if (dimensionStore.flipX)
        fieldPosition.x = flip(fieldPosition.x);
  
      if (dimensionStore.flipY)
        fieldPosition.y = flip(fieldPosition.y);
    }
  }

  private static Length flip(Length pos) {
    return pos != null ? new Length(-pos.value().doubleValue(), pos.unit()) : null;
  }
}


