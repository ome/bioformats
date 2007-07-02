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

      if (ca != null) {
        Vector elements = DOMUtil.getChildElements("NotesField", 
          ca.getDOMElement());
        int ndx = Integer.parseInt(map.substring(map.lastIndexOf("-") + 1));
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
    if (map == null || map.length() == 0) return null;

    // the 'map' string is a colon-separated list of nodes from the root
    // to the field we want to read
    // 
    // example: if we want to read the 'PixelType' value of a Pixels node,
    // the value of 'map' would be 'Image:Pixels:PixelType'
  
    String st = map.substring(0, map.indexOf(":"));
    map = map.substring(map.indexOf(":") + 1);

    int ndx = 0;
    if (map.indexOf("-") != -1 && map.indexOf("OriginalMetadata") == -1) {
      ndx = Integer.parseInt(map.substring(map.indexOf("-") + 1));
      map = map.substring(0, map.indexOf("-"));
    }
 
    Class stClass = null;

    try {
      stClass = Class.forName("org.openmicroscopy.xml." + st + "Node");
    }
    catch (ClassNotFoundException c) {
      stClass = Class.forName("org.openmicroscopy.xml.st." + st + "Node");
    }

    Vector nodes = OMEXMLNode.createNodes(stClass, 
      DOMUtil.getChildElements(st, root.getDOMElement())); 

    if (nodes.size() == 0 && create) {
      Class param = stClass.getName().startsWith("org.openmicroscopy.xml.st.") ?
        CustomAttributesNode.class : root.getClass();
      Constructor con = stClass.getConstructor(new Class[] {param});
      nodes.add((OMEXMLNode) con.newInstance(new Object[] {root}));
    }
    else if (nodes.size() == 0) return null;

    OMEXMLNode node = (OMEXMLNode) nodes.get(ndx < nodes.size() ? ndx : 0);

    if (map.indexOf("OriginalMetadata") != -1 && map.indexOf("-") != -1) {
      ndx = Integer.parseInt(map.substring(map.indexOf("-") + 1));
      map = map.substring(0, map.indexOf("-"));
    }

    while (map.indexOf(":") != -1) {
      String type = map.substring(0, map.indexOf(":"));
      map = map.substring(map.indexOf(":") + 1);

      String methodName1 = "get" + type;
      String methodName2 = "getDefault" + type;

      if (node instanceof CustomAttributesNode) {
        methodName2 = "getCAList";
      }

      // find the next node in the list

      Method[] methods = node.getClass().getMethods();

      for (int j=0; j<methods.length; j++) {
        String name = methods[j].getName();
        if (name.equals(methodName1) || name.equals(methodName2)) {
          if (node instanceof CustomAttributesNode) {
            Vector list = (Vector) methods[j].invoke(node, new Object[0]);

            int count = -1;
            for (int k=0; k<list.size(); k++) {
              String className = list.get(k).getClass().getName();
              int idx = className.lastIndexOf(".");
              className = className.substring(idx + 1);

              if (className.equals(type + "Node") ||
                (className.equals("AttributeNode") &&
                type.equals("OriginalMetadata")))
              {
                count++;
                if (count == ndx) {
                  node = (OMEXMLNode) list.get(k);
                  if (type.equals("OriginalMetadata")) return node;
                  break;
                }
              }
            }
          }
          else node = (OMEXMLNode) methods[j].invoke(node, new Object[0]);

          // check if we found a matching node; if not, create one

          if (node == null ||
            !node.getClass().getName().endsWith(type + "Node"))
          {
            Class target = null;

            try {
              target = Class.forName("org.openmicroscopy.xml." + type + "Node");
            }
            catch (ClassNotFoundException e) {
              try {
                target =
                  Class.forName("org.openmicroscopy.xml.st." + type + "Node");
              }
              catch (ClassNotFoundException cfe) {
                cfe.printStackTrace();
              }
            }
                                                                                             Class param =
              target.getName().startsWith("org.openmicroscopy.xml.st.") ?
              CustomAttributesNode.class : node.getClass();
            Constructor con = target.getConstructor(new Class[] {param});

            if (node == null) {
              node = (OMEXMLNode) nodes.get(ndx < nodes.size() ? ndx : 0);
              node = (OMEXMLNode) param.getConstructor(new Class[]
                {node.getClass()}).newInstance(new Object[] {node});
            }

            node = (OMEXMLNode) con.newInstance(new Object[] {node});
          }
          break;
        }
      }
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

}
