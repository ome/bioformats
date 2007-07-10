//
// TemplateTools.java
//

/*
OME Metadata Notes application for exploration and editing of OME-XML and
OME-TIFF metadata. Copyright (C) 2006-@year@ Christopher Peterson.

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

package loci.ome.notes;

import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import org.openmicroscopy.xml.*;
import org.w3c.dom.Element;

public class TemplateTools {

  /** Get the value corresponding to the given map. */
  public static String getString(OMENode root, String map, boolean value) 
    throws Exception 
  {
    OMEXMLNode node = findNode(root, map, false);

    if (node == null) {
      // unmapped field

      CustomAttributesNode ca = root.getCustomAttributes();

      int[] indices = getMapIndices(map);

      if (ca != null && map != null) {
        Vector elements = DOMUtil.getChildElements("NotesField", 
          ca.getDOMElement());
        int ndx = indices[indices.length - 1];
        if (ndx >= elements.size()) return null;
        Element el = (Element) elements.get(ndx);
        return DOMUtil.getAttribute(value ? "value" : "name", el);
      } 
      return ""; 
    } 

    String newMap = map.substring(map.lastIndexOf(":") + 1);
    if (newMap.indexOf("-") != -1) {
      newMap = newMap.substring(0, newMap.indexOf("-"));
    }

    if (node instanceof AttributeNode) {
      return node.getAttribute(newMap);
    }

    String methodName1 = "get" + newMap;
    String methodName2 = "is" + newMap;

    if (newMap.equals("CreationDate")) methodName1 = "getCreated";

    // retrieve the appropriate value

    Method[] methods = node.getClass().getMethods();
    for (int j=0; j<methods.length; j++) {
      String name = methods[j].getName();

      if ((name.equals(methodName1) || name.equals(methodName2)) &&
        methods[j].getParameterTypes().length == 0)
      {
        Object o = methods[j].invoke(node, new Object[0]);
        String s = o == null ? "" : o.toString();
        String v = s.toLowerCase();
        return v;
      }
    }
    return "";
  }

  /** Get the number of nodes matching this map. */
  public static int getNodeCount(OMENode root, String map) throws Exception {
    if (map == null || map.length() == 0 || root == null) return 0;
    map = map.substring(0, map.lastIndexOf(":"));
    map = map.substring(map.lastIndexOf(":") + 1);
    if (map.indexOf("-") != -1) map = map.substring(0, map.indexOf("-"));
    return DOMUtil.findElementList(map, root.getOMEDocument(true)).size();
  }

  /** Find the OME-XML node corresponding to the given map string. */
  public static OMEXMLNode findNode(OMENode root, String map, boolean create)
    throws Exception
  {
    if (map == null) return null; 
    int elementCount = 0;
    int last = map.indexOf(":");
    while (last != -1) {
      elementCount++;
      last = map.indexOf(":", last + 1);
    }

    int[] indices = getMapIndices(map);

    String[] elements = new String[elementCount];
    for (int i=0; i<elementCount; i++) {
      elements[i] = map.substring(0, map.indexOf(":"));
      map = map.substring(map.indexOf(":") + 1);
    }

    OMEXMLNode node = root;
    for (int i=0; i<elementCount; i++) {
      Vector nodeList = DOMUtil.getChildElements(elements[i], 
        node.getDOMElement());
      if ((nodeList == null || nodeList.size() == 0) && create) {
        // TODO : create the node
        return null; 
      }
      else if (nodeList == null || nodeList.size() == 0) return null;

      int idx = indices[i];
      node = OMEXMLNode.createNode((Element) nodeList.get(idx)); 
    }
   
    return node; 
  }

  /** Get the value from the given component. */
  public static Object getComponentValue(JComponent c) {
    Object value = null;

    if (c instanceof JCheckBox) {
      value = new Boolean(((JCheckBox) c).isSelected());
    }
    else if (c instanceof JComboBox) {
      value = ((JComboBox) c).getSelectedItem();
    }
    else if (c instanceof JScrollPane) {
      JScrollPane scroll = (JScrollPane) c;
      JViewport view = scroll.getViewport();
      value = ((JTextArea) view.getView()).getText();
    }
    else if (c instanceof JSpinner) {
      value = ((JSpinner) c).getValue();
    }

    return value;
  }

  /** Get the index for each element in the map. */
  public static int[] getMapIndices(String map) {
    if (map == null) return new int[0]; 
    int elementCount = 0;
    int last = map.indexOf(":");
    while (last != -1) {
      elementCount++;
      last = map.indexOf(":", last + 1);
    }

    int[] indices = new int[elementCount];
    Arrays.fill(indices, 0);
    if (map.indexOf("-") == -1) return indices;

    String indexList = map.substring(map.indexOf("-") + 1);
    for (int i=0; i<elementCount; i++) {
      if (indexList.indexOf(",") == -1) {
        indices[i] = Integer.parseInt(indexList);
      }
      else {
        indices[i] = Integer.parseInt(indexList.substring(0, 
          indexList.indexOf(",")));
        indexList = indexList.substring(indexList.indexOf(",") + 1); 
      } 
    }
    return indices; 
  }

}
