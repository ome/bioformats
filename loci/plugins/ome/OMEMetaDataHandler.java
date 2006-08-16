//
// OMEMetaDataHandler.java
//

/*
OME Plugin for ImageJ plugin for transferring images to and from an OME
database. Copyright (C) 2004-@year@ Philip Huettl and Melissa Linkert.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.plugins.ome;

import ij.*;
import ij.process.ImageProcessor;

import org.openmicroscopy.ds.dto.*;
import org.openmicroscopy.ds.*;
import org.openmicroscopy.ds.st.*;
import org.openmicroscopy.xml.*;
import org.w3c.dom.*;
import java.util.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.lang.reflect.*;

/**
 * OMEMetaDataHandler is the class that handles the download of metadata
 * from the database associated with an image and OME.  It also handles the
 * sifting of XML metadata in the header of an OME-TIFF file.
 *
 * @author Philip Huettl pmhuettl at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class OMEMetaDataHandler {

  // -- Constants --

  public static final String[] FEATURE_TYPES = {"Bounds", "Extent", "Location",
    "Ratio","Signal","Threshold", "Timepoint"};

  // -- Fields --

  private static ImageProcessor imageP;
  private static boolean isXML;
  private static OMENode omeNode;
  public static Vector defaultNode = new Vector();
  public static int pt = 0;  // next index
  public static int index = 0;  // current index
  private static int omeID;

  /**Method that begins the process of getting metadata from an OME_TIFF file*/
  public static DefaultMutableTreeNode exportMeta(Image image,
      ImagePlus imagePlus, DataFactory df) {

    imageP = imagePlus.getProcessor();
    isXML = false;
    omeNode = null;
    omeID = image.getID();

    IJ.showStatus("Metadata is being put into tree structure.");
    DefaultMutableTreeNode root =
      new DefaultMutableTreeNode(new XMLObject("OME-XML"));
    addDb(image, root, df, "Image");
    defaultNode.add(root);
    pt++;
    return root;
  }

  /**Method that begins the process of getting metadata from an OME_TIFF file*/
  public static void exportMeta(String descr, int ijimageID) {
    isXML = true;
    IJ.showStatus("Retrieving OME-TIFF header.");
    Object[] meta = null;

    if(descr == null) {
      IJ.showStatus("Not an OME-TIFF file.");
      if (meta == null) {
        meta = new Object[2];
        meta[0] = new Integer(0);
      }
      meta[1] = defaultNode.get(index);
      index++;
      OMESidePanel.hashInImage(ijimageID, meta);
      return;
    }

    IJ.showStatus("Parsing OME-TIFF header.");
    try {
      omeNode = new OMENode(descr);
    }
    catch (Exception e) {
      IJ.showStatus("Error parsing OME-XML metadata, possibly not present.");
      if (meta == null) {
        meta = new Object[2];
         meta[0] = new Integer(0);
      }
      meta[1] = null;
      OMESidePanel.hashInImage(ijimageID, meta);
      return;
    }
    if (meta == null) {
      meta = new Object[2];
      meta[0] = new Integer(0);
    }
    meta[1] = new DefaultMutableTreeNode(
      new XMLObject("OME-XML"));
    OMESidePanel.hashInImage(ijimageID, meta);
    IJ.showStatus("Metadata is being put into tree structure.");
    addDisk(omeNode, (DefaultMutableTreeNode) meta[1], null, "OME");
  }

  private static void exportMeta(Feature feature, DefaultMutableTreeNode root,
    DataFactory df) {
    IJ.showStatus("Retrieving Feature attributes.");
    DefaultMutableTreeNode featureNode = new DefaultMutableTreeNode(
      new XMLObject(XMLObject.FEATUREHEADING));
    root.add(featureNode);
    featureNode.add(new DefaultMutableTreeNode(
      new XMLObject("Name", feature.getName(), XMLObject.FEATURE)));
    featureNode.add(new DefaultMutableTreeNode(
      new XMLObject("Tag", feature.getTag(), XMLObject.FEATURE)));
    //add trajectory entries
    List trajects = null;
    if(df != null) {
      Criteria trajCriteria = new Criteria();
      trajCriteria.addWantedField("feature_id");
      trajCriteria.addFilter("feature_id", (new Integer(
        feature.getID())).toString());
      //retrieve element to add with all columns loaded
      trajects = OMERetrieve.retrieveList("Trajectory", trajCriteria, df);
    }
    else trajects = ((Trajectory)feature).getTrajectoryEntryList();

    Iterator iterTra = trajects.iterator();
    while (iterTra.hasNext()) {
      addDisk((Trajectory)iterTra.next(), featureNode, df, "Trajectory");
    }
    //add attributes to this element's node
    if(df != null) {
      for (int i=0; i<FEATURE_TYPES.length; i++) {
        Criteria criteria = new Criteria();
        criteria.addWantedField("feature_id");
        criteria.addFilter("feature_id", (new Integer(
          feature.getID())).toString());
        IJ.showStatus("Retrieving " + FEATURE_TYPES[i] + "s.");
        List customs = null;
        try {
          customs = OMERetrieve.retrieveList(FEATURE_TYPES[i], criteria, df);
        }
        catch(Exception e) {
          e.printStackTrace();
          IJ.showStatus("Error while retrieving " + FEATURE_TYPES[i] + "s.");
        }
        if(customs != null) {
          Iterator itCustoms = customs.iterator();
          while (itCustoms.hasNext()) {
            addDisk(itCustoms.next(), featureNode, df, FEATURE_TYPES[i]);
          }
        }
      }
    }
    else addDisk(feature, featureNode, df, "Feature");
    //add child features to tree
    List features = feature.getChildren();
    Iterator iter = features.iterator();
    while (iter.hasNext()) {
      exportMeta((Feature)iter.next(), featureNode, df);
    }
  }

  /**
   * Add an element's attributes and children to the metadata tree.
   * This method is only called if we are working with an image from the OME
   * database (requires OME-Java).
   */
  private static void addDb(Object element, DefaultMutableTreeNode root,
      DataFactory df, String identifier) {
    IJ.showStatus("Retrieving " + identifier + " attributes.");

    if(element == null) return;
    if(identifier.equals("DTOType")) return;
    try {
      Class elementClass = element.getClass();
      Method[] methods = elementClass.getDeclaredMethods();
      Vector newMethods = new Vector();
      Vector attrNames = new Vector();

      for(int i=0; i<methods.length; i++) {
        String name = methods[i].getName();
        // if this is an attribute method, we get the name right away
        if((name.indexOf("get") != -1)) {
          newMethods.add(methods[i]);
          if(!isChildMethod(methods[i])) {
                  attrNames.add(name.substring(3));
          }
        }
      }

      String[] attrs = new String[attrNames.size()];
      attrNames.copyInto(attrs);
      Criteria c = OMERetrieve.makeAttributeFields(attrs);
      c.addWantedField("id");
      c.addFilter("id", (new Integer(omeID)).toString());
      if(!inBanList(identifier)) element = df.retrieve(identifier, c);

      Method[] getMethods = new Method[newMethods.size()];
      newMethods.copyInto(getMethods);

      Vector getChildMethods = new Vector();
      Vector getAttrMethods = new Vector();

      for(int i=0; i<getMethods.length; i++) {
        if(isChildMethod(getMethods[i])) {
          getChildMethods.add(getMethods[i]);
        }
        else {
          getAttrMethods.add(getMethods[i]);
        }
      }

      // get the attribute values

      String[] values = new String[getAttrMethods.size()];
      for(int i=0; i<values.length; i++) {
        try {
          values[i] = "" +
            ((Method) getAttrMethods.get(i)).invoke(element, new Object[0]);
        }
        catch(Throwable e) { }
      }

      // add each attribute to the root node

      DefaultMutableTreeNode node = new DefaultMutableTreeNode(new XMLObject(
        identifier, XMLObject.ELEMENT));
      root.add(node);

      if(getAttrMethods.size() > 0) {
        for(int i=0; i<attrs.length; i++) {
          if(!inBanList(attrs[i])) {
            node.add(new DefaultMutableTreeNode(new XMLObject(attrs[i],
              values[i], XMLObject.ATTRIBUTE)));
          }
        }
      }

      // process child nodes

      for(int i=0; i<getChildMethods.size(); i++) {
        String name = ((Method) getChildMethods.get(i)).getName();
        name = name.substring(3);
        try {
          addDb(((Method) getChildMethods.get(i)).invoke(
            element, new Object[0]), node, df, name);
        }
        catch(InvocationTargetException e) { }
      }
    }
    catch(Throwable t) { }
  }

  /**
   * Determine whether a method will return an attribute value or a child node.
   * If the method returns a String or primitive type, then we assume it
   * returns an attribute value; else it returns a child node.
   */

  private static boolean isChildMethod(Method method) {
    Class rtn = method.getReturnType();
    String name = method.getName();
    return !(rtn.isPrimitive() || rtn.isInstance(new String("test")) ||
      name.substring(3).startsWith("Size"));
  }

  /**
   * Make sure that the object type is valid.
   */
  private static boolean inBanList(String id) {
    String[] ban = {"Image", "DTOType", "Datasets", "Features", "Owner",
      "DefaultPixels", "ClassLoader", "DTOTypeName"};
    for(int i=0; i<ban.length; i++) {
      if(id.equals(ban[i])) return true;
    }
    return false;
  }

  /**
   * Add an element's attributes and children to the metadata tree.
   * This method is only called if we are working with an image stored on
   * disk (requires LOCI's OME-XML package).
   */
  private static void addDisk(Object element, DefaultMutableTreeNode root,
    DataFactory df, String identifier) {
    IJ.showStatus("Retrieving " + identifier + " attributes.");
    if(element == null) return;

    OMEXMLNode xml = null;
    if(!element.toString().startsWith("[")) {
      xml = (OMEXMLNode) element;
    }

    String[] tempAttrs = null;
    String[] tempValues = null;

    try {
      tempAttrs = xml.getAttributeNames();
      tempValues = xml.getAttributeValues();
    }
    catch(NullPointerException n) { }

    // NodeList is a list of child DOM elements
    NodeList tempChilds = null;
    Object[] childs = null;
    String[] names = null;
    try {
      tempChilds = xml.getDOMElement().getChildNodes();
      childs = new Object[tempChilds.getLength()];
    }
    catch(NullPointerException n) { }

    try {
      for(int i=0; i<tempChilds.getLength(); i++) {
        String tmp = tempChilds.item(i).toString();
        String id = tmp.substring(1, tmp.indexOf(" "));
        String tmpId = tmp.substring(1, tmp.indexOf(">"));
        if(tmpId.length() < id.length()) id = tmpId;
        if(!id.endsWith("Node")) {
          id = id + "Node";
        }
        if(!id.equals("AttributeNode") && !id.equals("CustomAttributesNode") &&
            !id.equals("DatasetNode") && !id.equals("FeatureNode") &&
            !id.equals("ImageNode") && !id.equals("OMENode") &&
            !id.equals("ProjectNode")) {
                id = "org.openmicroscopy.xml.st." + id;
        }
        else {
          id = "org.openmicroscopy.xml." + id;
        }

        // construct a new node using this class
              Class toConstruct = Class.forName(id.trim());
        Constructor construct =
          toConstruct.getDeclaredConstructor(new Class[] {Element.class});
        childs[i] =
          construct.newInstance(new Object[] {(Element) tempChilds.item(i)});
      }

      names = new String[childs.length];
      for(int i=0; i<childs.length; i++) {
        String tmp = childs[i].toString();
        names[i] = tmp.substring(tmp.lastIndexOf(".")+1, tmp.lastIndexOf("@"));
      }
    }
    catch(Throwable t) { }

    if(df != null && tempAttrs != null) {
      Criteria criteria = OMERetrieve.makeAttributeFields(tempAttrs);
      criteria.addWantedField("id");
      criteria.addFilter("id", (new Integer(
        ((ImageGroup) element).getID())).toString());
      element = df.retrieve(identifier, criteria);
    }

    DefaultMutableTreeNode node = new DefaultMutableTreeNode(new XMLObject(
      identifier, XMLObject.ELEMENT));
    root.add(node);

    if(tempAttrs != null && tempValues != null) {
      for(int i=0; i<tempValues.length; i++) {
        node.add(new DefaultMutableTreeNode(new XMLObject(tempAttrs[i],
        tempValues[i], XMLObject.ATTRIBUTE)));
      }
    }

    if(tempChilds != null) {
      for(int i=0; i<childs.length; i++) {
        try {
                addDisk(childs[i], node, df, names[i]);
        }
        catch(NullPointerException e) { }
      }
    }
  }
}
