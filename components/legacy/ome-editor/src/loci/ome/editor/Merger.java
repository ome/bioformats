/*
 * #%L
 * OME Metadata Editor application for exploration and editing of OME-XML and
 * OME-TIFF metadata.
 * %%
 * Copyright (C) 2006 - 2012 Board of Regents of the University of
 * Wisconsin-Madison, Glencoe Software, Inc., and University of Dundee.
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

package loci.ome.editor;

import java.io.File;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilderFactory;

import ome.xml.OMEXMLNode;

import org.openmicroscopy.xml.OMENode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/legacy/ome-editor/src/loci/ome/editor/Merger.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/legacy/ome-editor/src/loci/ome/editor/Merger.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Christopher Peterson crpeterson2 at wisc.edu
 */
public class Merger {

  // --Constants--

  /** Factory for generating document builders. */
  public static final DocumentBuilderFactory DOC_FACT =
    DocumentBuilderFactory.newInstance();

  /** Different mode constants.*/
  public static final int ALL_ORIGINAL = 0x01;
  public static final int ALL_COMPANION = 0x02;
  public static final int ORIGINAL_OVER = 0x04;
  public static final int COMPANION_OVER = 0x08;

  // --Members--

  protected OMENode ome, compOme, finalOme;
  protected int mode;
  private JComponent comp;

  public Merger(OMENode originalOme, File compFile, JComponent c) {
    ome = originalOme;
    comp = c;

    try {
      compOme = new OMENode(compFile);
    }
    catch (Exception exc) { exc.printStackTrace();}

    prompt();
    merge();
  }

  public OMENode getRoot() {
    return finalOme;
  }

  private void merge() {
    if(mode == ALL_ORIGINAL) finalOme = ome;
    else if(mode == ALL_COMPANION) finalOme = compOme;
    else if(mode == ORIGINAL_OVER) {
      finalOme = merge(ome,compOme);
    }
    else if(mode == COMPANION_OVER) {
      finalOme = merge(compOme,ome);
    }
  }

  /**
  * Merge two OME-XML Trees, when a conflict arrises, use the
  * "over" tree's node instead of the "under" tree's. NB: the
  * trees should be in OMECA (flattened) format before passing
  * them to this method.
  * @param over The OMENode that by default has higher priority.
  * @param under The OMENode that by default has lower priority.
  */
  public static OMENode merge(OMENode over, OMENode under) {
    OMENode result;
    OMEXMLNode tempNode = merge((OMEXMLNode)over,(OMEXMLNode)under);
    if (tempNode instanceof OMENode) result = (OMENode) tempNode;
    else result = null;
    return result;
  }

  private static OMEXMLNode merge(OMEXMLNode over, OMEXMLNode under) {
    OMEXMLNode result = over;
    Vector overList = result.getChildNodes();
    Vector underList = under.getChildNodes();
    Vector idList = new Vector();
    boolean isOverCustom = false;
    boolean isUnderCustom = false;
    boolean addedCustom = false;

    for(int i = 0;i<overList.size();i++) {
      OMEXMLNode overNode = (OMEXMLNode)(overList.get(i));
      String overID = overNode.getAttribute("ID");
      if (overID == null) isOverCustom = true;

      for(int j = 0;j<underList.size();j++) {
        OMEXMLNode underNode = (OMEXMLNode)(underList.get(j));
        String underID = underNode.getAttribute("ID");
        if (underID == null) isUnderCustom = true;

        if(isOverCustom && !isUnderCustom) {
          //do nothing to alter custom tree
          isOverCustom = false;
        }
        else if(!isOverCustom && isUnderCustom && !addedCustom) {
          result.getDOMElement().appendChild(createClone(
            underNode.getDOMElement(),overNode.getDOMElement().
            getOwnerDocument()));
          addedCustom = true;
          isUnderCustom = false;
        }
        else if (!isOverCustom && !isUnderCustom) {
          if (underID.equals(overID))
            merge(overNode,underNode);
          else {
            if(idList.indexOf(underID) > -1) {
              result.getDOMElement().appendChild(createClone(
                underNode.getDOMElement(),overNode.getDOMElement().
                getOwnerDocument()));
              idList.add(underID);
            }
          }
        }
      }
    }

    return result;
  }

  public static Element createClone(Element el, Document doc) {
    String tagName = el.getTagName();
    Element clone = doc.createElement(tagName);

    if(el.hasAttributes()) {
      NamedNodeMap map = el.getAttributes();
      for(int i = 0;i<map.getLength();i++) {
        Node thisAttr = map.item(i);
        String attrName = thisAttr.getNodeName();
        String attrValue = thisAttr.getNodeValue();
        clone.setAttribute(attrName,attrValue);
      }
    }

    if(el.hasChildNodes()) {
      NodeList nodes = el.getChildNodes();
      for(int i = 0;i<nodes.getLength();i++) {
        Node thisNode = nodes.item(i);
        if(thisNode instanceof Element) {
          Element origChild = (Element) thisNode;
          Element cloneChild = createClone(origChild,doc);
          clone.appendChild(cloneChild);
        }
      }
    }

    return clone;
  }

  private void prompt() {
    Object[] possibilities = {"Just use the original file",
      "Just use the companion file", "Merge, original file takes precedence",
      "Merge, companion file takes precedence"};
    String s = (String)JOptionPane.showInputDialog(
      comp.getTopLevelAncestor(),
      "How would you like to merge the companion file "
        + "with the original file?\nIf you choose to merge, "
        + "you must specify which file takes\nprecedence should "
        + "a conflict in the metadata arise.",
      "Merge Mode Selection",
      JOptionPane.QUESTION_MESSAGE,
      (javax.swing.Icon)null,
      possibilities,
      possibilities[1]);
    if ((s != null) && (s.length() > 0)) {
      if(s.equals(possibilities[0])) mode = ALL_ORIGINAL;
      else if (s.equals(possibilities[1])) mode = ALL_COMPANION;
      else if (s.equals(possibilities[2])) mode = ORIGINAL_OVER;
      else if (s.equals(possibilities[3])) mode = COMPANION_OVER;
    }
    else mode = ALL_ORIGINAL;
  }
}
