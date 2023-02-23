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

package loci.formats.in.LeicaMicrosystemsMetadata;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.LMSFileReader.ImageFormat;

/**
 * This class loads and represents a Leica Microsystems XLIF xml document
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class XlifDocument extends LMSImageXmlDocument {

  // -- Fields --
  ImageFormat imageFormat;
  List<String> imagePaths = new ArrayList<>();

  // -- Constructor
  public XlifDocument(String filepath, LMSCollectionXmlDocument parent) {
    super(filepath, parent);
    initImagePaths();
    imageFormat = checkImageFormat();
  }

  // -- Getters --
  public ImageFormat getImageFormat() {
    return imageFormat;
  }

  public List<String> getImagePaths() {
    return imagePaths;
  }

  public String getName() {
    return getAttr(xPath("//Element").item(0), "Name");
  }

  public Node getElementNode() {
    return xPath("//Element").item(0);
  }

  @Override
  public Node getImageNode() {
    for (int i = 0; i < doc.getDocumentElement().getChildNodes().getLength(); i++){
      Node child = doc.getDocumentElement().getChildNodes().item(i);
      if (child.getNodeName().equals("Element")){
        for (int k = 0; k < child.getChildNodes().getLength(); k++){
          Node elementChild = child.getChildNodes().item(k);
          if (elementChild.getNodeName().equals("Data")){
            for (int m = 0; m < elementChild.getChildNodes().getLength(); m++){
              Node dataChild = elementChild.getChildNodes().item(m);
              if (dataChild.getNodeName().equals("Image")){
                return dataChild;
              }
            }
          }
        }
      }
    }
    return null;
  }

  @Override
  public String getImageName(){
    return getAttr(xPath("//Element").item(0), "Name");
  }

  /**
   * Returns the number of tiles referenced by this xlif
   */
  public int getTileCount(){
    NodeList dimDescs = xPath("//DimensionDescription");
    for (int i = 0; i < dimDescs.getLength(); i++){
      if (getAttr(dimDescs.item(i), "DimID").equals("10")){
        return Integer.parseInt(getAttr(dimDescs.item(i), "NumberOfElements"));
      }
    }
    return 1;
  }

  // -- Methods --
  private ImageFormat checkImageFormat() {
    for (String path : imagePaths) {
      if (path.endsWith("tif") || path.endsWith("tiff"))
        return ImageFormat.TIF;
      else if (path.endsWith("bmp"))
        return ImageFormat.BMP;
      else if (path.endsWith("jpeg") || path.endsWith("jpg"))
        return ImageFormat.JPEG;
      else if (path.endsWith("png"))
        return ImageFormat.PNG;
      else if (path.endsWith("lof"))
        return ImageFormat.LOF;
    }
    return ImageFormat.UNKNOWN;
  }

  /** Searches the xml for referenced images and adds image paths */
  private void initImagePaths() {
    // TIF, PNG, ... references
    NodeList references = xPath("//Frame");
    if (references.getLength() == 0) {
      // LOF references
      references = xPath("//Block");
    }
    LOGGER.info("References Found: " + references.getLength());
    for (int i = 0; i < references.getLength(); i++) {
      String path = parseFilePath(getAttr(references.item(i), "File").toLowerCase());
      String correctedPath = fileExists(path);
      imagePaths.add(correctedPath);
    }
  }

  public void printXlifInfo() {
    String name = getAttr(xPath("//Element").item(0), "Name");
    LOGGER.info("---- Image name: " + name + ", references: " + imagePaths.size()
        + ", image format: " + imageFormat + " ----");
    LOGGER.info("path: " + filepath);
  }

  public boolean isValid(){
    return imagePaths.size() > 0;
  }
}
