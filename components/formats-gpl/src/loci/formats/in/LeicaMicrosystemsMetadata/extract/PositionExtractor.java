package loci.formats.in.LeicaMicrosystemsMetadata.extract;

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.Tuple;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes.DataSourceType;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes.HardwareSettingLayout;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DimensionStore;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DimensionStore.ZDriveMode;
import ome.units.UNITS;
import ome.units.quantity.Length;

public class PositionExtractor extends Extractor {
  public static void extractFieldPositions(LMSMainXmlNodes xmlNodes, DimensionStore dimensionStore){
    Element mainSetting;
    
    if (xmlNodes.hardwareSettingLayout == HardwareSettingLayout.OLD){
      mainSetting = xmlNodes.dataSourceType == DataSourceType.CONFOCAL ? xmlNodes.masterConfocalSetting : xmlNodes.masterCameraSetting;
    } else {
      mainSetting = xmlNodes.dataSourceType == DataSourceType.CONFOCAL ? xmlNodes.mainConfocalSetting : xmlNodes.mainCameraSetting;
    }
    
    NodeList attachments = Extractor.getDescendantNodesWithName(xmlNodes.imageNode, "Attachment");
    Element tilescanInfo = (Element)Extractor.getNodeWithAttribute(attachments, "Name", "TileScanInfo");
    
    //XY positions
    if (tilescanInfo != null){
      String flipXS = getAttributeValue(tilescanInfo, "FlipX");
      dimensionStore.flipX = flipXS.equals("1");
      
      String flipYS = getAttributeValue(tilescanInfo, "FlipY");
      dimensionStore.flipY = flipYS.equals("1");

      String swapXYS = getAttributeValue(tilescanInfo, "SwapXY");
      dimensionStore.swapXY = swapXYS.equals("1");

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
    } else {
      String flipXS = getAttributeValue(mainSetting, "FlipX");
      dimensionStore.flipX = flipXS.equals("1");
      
      String flipYS = getAttributeValue(mainSetting, "FlipY");
      dimensionStore.flipY = flipYS.equals("1");
  
      String swapXYS = getAttributeValue(mainSetting, "SwapXY");
      dimensionStore.swapXY = swapXYS.equals("1");
    }

    if (dimensionStore.fieldPositions.size() == 0){
      String fieldPosXS = getAttributeValue(mainSetting, "StagePosX");
      Length fieldPosX = parseLength(fieldPosXS, UNITS.METER);
      String fieldPosYS = getAttributeValue(mainSetting, "StagePosY");
      Length fieldPosY = parseLength(fieldPosYS, UNITS.METER);
      dimensionStore.fieldPositions.add(new Tuple<Length,Length>(fieldPosX, fieldPosY));
    }

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

    //Z
    String beginS = getAttributeValue(mainSetting, "Begin");
    dimensionStore.zBegin = parseDouble(beginS);
    String endS = getAttributeValue(mainSetting, "End");
    dimensionStore.zEnd = parseDouble(endS);

    String zUseMode = getAttributeValue(mainSetting, "ZUseMode");
    dimensionStore.zDriveMode = zUseMode.equals("1") ? ZDriveMode.ZGalvo : ZDriveMode.ZWide;

    Element zPositionList = getChildNodeWithNameAsElement(mainSetting, "AdditionalZPositionList");
    if (zPositionList == null) return;
    List<Element> zPositions = getChildNodesWithNameAsElement(zPositionList, "AdditionalZPosition");
    for (Element zPosition : zPositions){
      String positionS = getAttributeValue(zPosition, "ZPosition");
      double position = parseDouble(positionS);
      String useMode = getAttributeValue(zPosition, "ZUseModeName");
      if (useMode.equals("z-galvo")){
        dimensionStore.zGalvoPosition = position;
      } else if (useMode.equals("z-wide")){
        dimensionStore.zWidePosition = position;
      }
    }
  }

  private static Length flip(Length pos) {
    return pos != null ? new Length(-pos.value().doubleValue(), pos.unit()) : null;
  }
}


