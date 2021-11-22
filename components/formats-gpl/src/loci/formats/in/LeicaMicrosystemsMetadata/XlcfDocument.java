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
import org.w3c.dom.NodeList;

/**
 * This class loads and represents a Leica Microsystems XLCF xml document
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class XlcfDocument extends LMSXmlDocument {

  // -- Constructor --
  public XlcfDocument(String filepath) {
    super(filepath, InitFrom.FILEPATH);
  }

  // -- Getters --

  /**
   * Returns XlifDocuments for all xlif references found in the xlcf and in
   * referenced xlcfs
   * 
   * @return list of xlif documents
   */
  public List<XlifDocument> getXlifs() {
    List<XlifDocument> xlifDocs = new ArrayList<>();
    NodeList references = xPath("//Reference");
    for (int i = 0; i < references.getLength(); i++) {
      String path = parseFilePath(getAttr(references.item(i), "File"));
      if (LMSFileReader.fileExists(path)) {
        if (path.endsWith(".xlif") && !path.endsWith("iomanagerconfiguation.xlif")) {
          xlifDocs.add(new XlifDocument(path));
        } else if (path.endsWith(".xlcf")) {
          XlcfDocument xlcf = new XlcfDocument(path);
          xlifDocs.addAll(xlcf.getXlifs());
        }
      }

    }
    return xlifDocs;
  }
}
