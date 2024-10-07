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

package loci.formats.in.LeicaMicrosystemsMetadata.write;

import java.util.Deque;
import java.util.Iterator;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import loci.formats.CoreMetadata;
import loci.formats.in.LeicaMicrosystemsMetadata.LMSFileReader;
import loci.formats.in.LeicaMicrosystemsMetadata.model.ImageDetails;
import loci.formats.meta.MetadataStore;

/**
 * ImageSettingsWriter writes image names and xml details to the reader's {@link CoreMetadata} and {@MetadataStore}
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class ImageSettingsWriter {
  
  /**
   *  Writes image details to the reader's {@link CoreMetadata}
   * @param store
   * @param reader
   * @param originalImageName
   * @param description
   * @param userComments
   * @param seriesIndex
   */
  public static void writeImageDetails(MetadataStore store, LMSFileReader reader, ImageDetails imageDetails, int seriesIndex){
    store.setImageName(imageDetails.targetImageName, seriesIndex);
    store.setImageDescription(imageDetails.description, seriesIndex);
    for (int i = 0; i < imageDetails.userComments.size(); i++){
      reader.addSeriesMeta("User-Comment[" + i + "]", imageDetails.userComments.get(i));
    }
  }

  /**
   * Creates key value pairs from attributes of the root's child nodes (tag |
   * attribute name : attribute value) and adds them to reader's
   * {@link CoreMetadata}
   * 
   * @param root
   *          xml node
   * @param nameStack
   *          list of node names to be prepended to key name
   */
  public static void populateOriginalMetadata(Element root, Deque<String> nameStack, LMSFileReader reader) {
    String name = root.getNodeName();
    if (root.hasAttributes() && !name.equals("Element") && !name.equals("Attachment")
        && !name.equals("LMSDataContainerHeader")) {
      nameStack.push(name);

      String suffix = root.getAttribute("Identifier");
      String value = root.getAttribute("Variant");
      if (suffix == null || suffix.trim().length() == 0) {
        suffix = root.getAttribute("Description");
      }
      final StringBuilder key = new StringBuilder();
      final Iterator<String> nameStackIterator = nameStack.descendingIterator();
      while (nameStackIterator.hasNext()) {
        final String k = nameStackIterator.next();
        key.append(k);
        key.append("|");
      }
      if (suffix != null && value != null && suffix.length() > 0 && value.length() > 0 && !suffix.equals("HighInteger")
          && !suffix.equals("LowInteger")) {
        reader.addSeriesMetaList(key.toString() + suffix, value);
      } else {
        NamedNodeMap attributes = root.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
          Attr attr = (Attr) attributes.item(i);
          if (!attr.getName().equals("HighInteger") && !attr.getName().equals("LowInteger")) {
            reader.addSeriesMeta(key.toString() + attr.getName(), attr.getValue());
          }
        }
      }
    }

    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Object child = children.item(i);
      if (child instanceof Element) {
        populateOriginalMetadata((Element) child, nameStack, reader);
      }
    }

    if (root.hasAttributes() && !name.equals("Element") && !name.equals("Attachment")
        && !name.equals("LMSDataContainerHeader")) {
      nameStack.pop();
    }
  }
}
