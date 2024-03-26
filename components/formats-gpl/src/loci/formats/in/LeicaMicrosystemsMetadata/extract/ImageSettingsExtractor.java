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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.formats.in.LeicaMicrosystemsMetadata.helpers.LMSMainXmlNodes;
import loci.formats.in.LeicaMicrosystemsMetadata.model.ImageDetails;

/**
 * Helper class for extracting image details from LMS XML
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class ImageSettingsExtractor {
  
  /**
   * Extracts image details from LMS XML
   */
  public static void extractImageDetails(LMSMainXmlNodes xmlNodes, ImageDetails imageDetails){
    NodeList attachmentNodes = Extractor.getDescendantNodesWithName(xmlNodes.imageNode, "User-Comment");
    if (attachmentNodes != null){
      for (int i = 0; i < attachmentNodes.getLength(); i++) {
        Node attachment = attachmentNodes.item(i);
        imageDetails.userComments.add(attachment.getTextContent());
        if (i == 0)
        imageDetails.description = attachment.getTextContent();
      }
    }
  }
}
