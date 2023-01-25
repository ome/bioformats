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

package loci.formats.in.LeicaMicrosystemsMetadata.doc;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NodeList;

/**
 * This class represents an LMS xml file that references other LMS xml files
 * such as xlcfs and xlifs.
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class LMSCollectionXmlDocument extends LMSXmlDocument {

  // -- Fields --
  private List<LMSXmlDocument> children = new ArrayList<>();

  // -- Constructor --

  public LMSCollectionXmlDocument(String filepath, LMSCollectionXmlDocument parent) {
    super(filepath, parent);
  }

  // -- Getters --

  /**
   * Returns all XlifDocuments referenced by this and by all referenced
   * XlcfDocuments
   * 
   * @return list of xlifs as XlifDocuments
   */
  public List<XlifDocument> getXlifs() {
    List<XlifDocument> xlifs = new ArrayList<>();
    for (LMSXmlDocument child : children) {
      if (child instanceof XlifDocument) {
        xlifs.add((XlifDocument) child);
      } else if (child instanceof XlcfDocument) {
        xlifs.addAll(((XlcfDocument) child).getXlifs());
      }
    }
    return xlifs;
  }

  /**
   * Returns filepaths of all files which are referenced by this document.
   * 
   * @param pixels if true, image files in which pixels are stored are included
   * @return
   */
  public List<String> getChildrenFiles(boolean pixels) {
    List<String> files = new ArrayList<>();
    for (LMSXmlDocument child : children) {
      files.add(child.filepath);
      if (child instanceof XlcfDocument) {
        files.addAll(((XlcfDocument) child).getChildrenFiles(pixels));
      } else if (child instanceof XlifDocument && pixels) {
        files.addAll(((XlifDocument) child).getImagePaths());
      }
    }
    return files;
  }

  // -- Methods --

  /** Adds all referenced xlcfs and valid xlifs as children */
  protected void initChildren() {
    NodeList references = xPath("//Reference");
    LOGGER.info("References Found: " + references.getLength());
    for (int i = 0; i < references.getLength(); i++) {
      String path = parseFilePath(getAttr(references.item(i), "File"));
      String correctedPath = fileExists(path);
      if (correctedPath != null) {
        if (correctedPath.endsWith(".xlif")) {
          XlifDocument xlif = new XlifDocument(correctedPath, this);
          if (xlif.isValid()) {
            children.add(xlif);
          }
          else {
            LOGGER.warn("XLIF file is invalid: " + correctedPath);
          }
        } else if (correctedPath.endsWith(".xlcf")) {
          children.add(new XlcfDocument(correctedPath, this));
        }
      }
      else {
        LOGGER.warn("Expected file at image path does not exist: " + path);
      }
    }
  }
  
}
