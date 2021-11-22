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

/**
 * This class loads and represents a Leica Microsystems XLEF xml document
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class XlefDocument extends LMSXmlDocument {

  // -- Fields --
  private List<XlifDocument> xlifs = new ArrayList<>();
  private List<XlifDocument> validXlifs = new ArrayList<>(); // xlifs which actually point to image files

  // -- Constructor --
  public XlefDocument(String filepath) {
    super(filepath, InitFrom.FILEPATH);
    initXlifs();
  }

  // -- Getters --

  /**
   * Returns number of images which are referenced by xlifs
   * 
   * @return image number
   */
  public int getImageCount() {
    int imgCount = 0;
    for (XlifDocument xlif : xlifs) {
      imgCount += xlif.getImagePaths().size();
    }
    return imgCount;
  }

  /**
   * Returns xlifs found in the xlef and in referenced xlcfs
   * 
   * @return list of xlifs as XlifDocuments
   */
  public List<XlifDocument> getXlifs() {
    return xlifs;
  }

  /**
   * Returns xlifs found in the xlef and in referenced xlcfs, which actually point
   * to image files
   * 
   * @return list of xlifs as XlifDocuments
   */
  public List<XlifDocument> getValidXlifs() {
    return validXlifs;
  }

  // -- Methods --

  /**
   * Initializes xlifs found in the xlef and in referenced xlcfs, as
   * XlifDocuments.
   */
  private void initXlifs() {
    NodeList references = xPath("//Reference");
    for (int i = 0; i < references.getLength(); i++) {
      String path = parseFilePath(getAttr(references.item(i), "File"));
      if (LMSFileReader.fileExists(path)) {
        if (path.endsWith(".xlif") && !path.endsWith("iomanagerconfiguation.xlif")) {
          XlifDocument newXlif = new XlifDocument(path);
          xlifs.add(newXlif);
        } else if (path.endsWith(".xlcf")) {
          XlcfDocument xlcf = new XlcfDocument(path);
          xlifs.addAll(xlcf.getXlifs());
        }
      }
    }
    for (XlifDocument xlif : xlifs) {
      if (xlif.getImagePaths().size() > 0) {
        validXlifs.add(xlif);
      }
    }
  }

  public void printXlefInfo() {
    Node root = getDoc().getDocumentElement();
    System.out.println("---------- XLEF INFO: ----------");
    System.out.println("Root Element: \t" + root.getNodeName());

    Node exp = xPath("//Experiment").item(0);
    System.out.println("Path: \t\t" + getAttr(exp, "Path"));
    System.out.println("Is saved: \t" + getAttr(exp, "IsSavedFlag"));
  }

  public void printReferences() {
    System.out.println("-------- " + validXlifs.size() + " images found: -------- ");
    for (XlifDocument xlif : validXlifs) {
      xlif.printXlifInfo();
    }
    System.out.println("---------------------------------");
  }
}
