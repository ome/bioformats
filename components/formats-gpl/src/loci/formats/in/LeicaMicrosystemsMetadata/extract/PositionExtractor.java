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

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.helpers.Tuple;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Dimension;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Dimension.DimensionKey;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DimensionStore;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DimensionStore.ZDriveMode;
import ome.units.UNITS;
import ome.units.quantity.Length;

/**
 * PositionExtractor is a helper class for extracting XYZ field position and setup information from LMS XML files.
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class PositionExtractor extends Extractor {

  /***
   * Extracts XYZ field positions and configuration from LMS XML and adds it to dimensionStore
   * @param xmlNodes
   * @param dimensionStore
   */
  public static void extractFieldPositions(LMSMainXmlNodes xmlNodes, DimensionStore dimensionStore){
    Element mainSetting = xmlNodes.getAtlSetting();

    NodeList attachments = Extractor.getDescendantNodesWithName(xmlNodes.imageNode, "Attachment");
    Element tilescanInfo = (Element)Extractor.getNodeWithAttribute(attachments, "Name", "TileScanInfo");
    
    // flipX, flipY, swapXY
    if (tilescanInfo != null){
      String flipXS = getAttributeValue(tilescanInfo, "FlipX");
      dimensionStore.flipX = flipXS.equals("1");
      
      String flipYS = getAttributeValue(tilescanInfo, "FlipY");
      dimensionStore.flipY = flipYS.equals("1");

      String swapXYS = getAttributeValue(tilescanInfo, "SwapXY");
      dimensionStore.swapXY = swapXYS.equals("1");
    } else {
      String flipXS = getAttributeValue(mainSetting, "FlipX");
      dimensionStore.flipX = flipXS.equals("1");
      
      String flipYS = getAttributeValue(mainSetting, "FlipY");
      dimensionStore.flipY = flipYS.equals("1");
  
      String swapXYS = getAttributeValue(mainSetting, "SwapXY");
      dimensionStore.swapXY = swapXYS.equals("1");
    }

    // get XY(Z) field positions from tilescan info
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
        String fieldPosZS = getAttributeValue(tile, "PosZ");
        Length fieldPosZ = null;
        if (!fieldPosZS.isEmpty()){
          dimensionStore.tilescanInfoHasZ = true;
          fieldPosZ = parseLength(fieldPosZS, UNITS.METER);
        }
        dimensionStore.fieldPositions.add(new Tuple<Length,Length,Length>(fieldPosX, fieldPosY, fieldPosZ));
      }
    } else {
      String fieldPosXS = getAttributeValue(mainSetting, "StagePosX");
      Length fieldPosX = parseLength(fieldPosXS, UNITS.METER);
      String fieldPosYS = getAttributeValue(mainSetting, "StagePosY");
      Length fieldPosY = parseLength(fieldPosYS, UNITS.METER);
      dimensionStore.fieldPositions.add(new Tuple<Length,Length,Length>(fieldPosX, fieldPosY, null));
    }

    // swapping and flipping
    for (int planeIndex = 0; planeIndex < dimensionStore.fieldPositions.size(); planeIndex++){
      Tuple<Length,Length,Length> fieldPosition = dimensionStore.fieldPositions.get(planeIndex);

      if (dimensionStore.swapXY){
        Length temp = fieldPosition.first;
        fieldPosition.first = fieldPosition.second;
        fieldPosition.second = temp;
      }
  
      if (dimensionStore.flipX)
        fieldPosition.first = flip(fieldPosition.first);
  
      if (dimensionStore.flipY)
        fieldPosition.second = flip(fieldPosition.second);
    }

    // extract additional Z drive / position values
    String beginS = getAttributeValue(mainSetting, "Begin");
    dimensionStore.zBegin = parseDouble(beginS);
    String endS = getAttributeValue(mainSetting, "End");
    dimensionStore.zEnd = parseDouble(endS);

    String zUseMode = getAttributeValue(mainSetting, "ZUseMode");
    dimensionStore.zDriveMode = zUseMode.equals("1") ? ZDriveMode.ZGalvo : ZDriveMode.ZWide;

    Element zPositionList = getChildNodeWithNameAsElement(mainSetting, "AdditionalZPositionList");
    if (zPositionList != null){
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
    
    //SP5 images without ATL settings: main ATLConfocalSetting > begin / end do not exist, neither does AdditionalZPositionList
    if (dimensionStore.zBegin == 0 && dimensionStore.zEnd == 0){
      Dimension dimZ = dimensionStore.getDimension(DimensionKey.Z);
      dimensionStore.zBegin = dimZ.origin;
      int sign = xmlNodes.confocalSettingRecords.reverseZ ? -1 : 1;
      dimensionStore.zEnd = dimensionStore.zBegin + sign * dimZ.getPhysicalStepSize() * dimZ.size;
    }
  }

  private static Length flip(Length pos) {
    return pos != null ? new Length(-pos.value().doubleValue(), pos.unit()) : null;
  }
}


