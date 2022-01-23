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

import java.util.List;
import org.w3c.dom.Node;

/**
 * This class represents a Leica Microsystems XLEF xml document,
 * a project file which references xlifs and optionally xlcfs
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class XlefDocument extends LMSCollectionXmlDocument {

  // -- Constructor --
  public XlefDocument(String filepath) {
    super(filepath, null);
    initChildren();
  }

  // -- Getters --

  /**
   * Returns number of images which are referenced by xlifs
   * 
   * @return image number
   */
  public int getImageCount() {
    List<XlifDocument> xlifs = getXlifs();

    int imgCount = 0;
    for (LMSXmlDocument xlif : xlifs) {
      imgCount += ((XlifDocument)xlif).getImagePaths().size();
    }
    return imgCount;
  }

  // -- Methods --

  public void printReferences() {
    List<XlifDocument> xlifs = getXlifs();
    LOGGER.info("-------- XLEF INFO: " + xlifs.size() + " images found -------- ");
    for (XlifDocument xlif : xlifs) {
      xlif.printXlifInfo();
    }
    LOGGER.info("-------------------------------------------");
  }
}
