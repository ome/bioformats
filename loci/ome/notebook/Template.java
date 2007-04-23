//
// Template.java
//

/*
OME Metadata Notebook application for exploration and editing of OME-XML and
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

package loci.ome.notebook;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import org.openmicroscopy.xml.*;
import org.w3c.dom.Element;

/** 
 * Loads a template from a file, and stores the options associated with this 
 * template.  See template-format.txt for details of how to construct a
 * valid template file.
 */
public class Template {

  // -- Constants --
  
  /** Width to use if a value wasn't specified. */
  private static final int DEFAULT_WIDTH = 800;

  /** Height to use if a value wasn't specified. */
  private static final int DEFAULT_HEIGHT = 700;

  /** Font style to use if a value wasn't specified. */
  private static final String DEFAULT_FONT = "Arial";

  /** Font size to use if a value wasn't specified. */
  private static final int DEFAULT_FONT_SIZE = 12;

  /** Default font color (black). */
  private static final int[] DEFAULT_FONT_COLOR = new int[] {0, 0, 0};

  /** Default background color (white). */
  private static final int[] DEFAULT_BACKGROUND_COLOR = new int[] {
    255, 255, 255};

  // -- Fields --

  /** Tabs associated with this template. */
  private TemplateTab[] tabs;

  /** Additional GUI options defined by this template. */
  private Hashtable options;

  // -- Constructor --

  /** Constructs a new Template from the given filename. */
  public Template(String file) throws IOException {
    this(new File(file).getAbsoluteFile());
  }

  /** Constructs a new Template from the given file. */
  public Template(File file) throws IOException {
    this(new FileInputStream(file));
  }

  /** Constructs a new Template from the given InputStream. */
  public Template(InputStream stream) throws IOException {
    options = new Hashtable(); 
    
    try { 
      parse(stream);
    }
    catch (IOException io) {
      io.printStackTrace();
    }
  }

  /** Constructs a new Template from TemplateTabs and a list of options. */
  public Template(TemplateTab[] tabs, Hashtable options) {
    this.tabs = tabs;
    this.options = options;
  }

  // -- Template API methods --

  /** Parses the template options from the given InputStream. */
  public void parse(InputStream is) throws IOException {
    BufferedInputStream bis = new BufferedInputStream(is);

    byte[] b = new byte[bis.available()];
    bis.read(b);

    String s = new String(b);

    StringTokenizer lines = new StringTokenizer(s, "\n");
    boolean openField = false;
    boolean openGroup = false;
    boolean openTab = false; 
    StringBuffer field = new StringBuffer(); 
    Vector parsedFields = new Vector();

    TemplateGroup currentGroup = null;
    TemplateTab currentTab = null;
    Vector tempTabs = new Vector();

    while (lines.hasMoreTokens()) {
      String line = lines.nextToken().trim();
      if (line.startsWith("#") || line.length() == 0) continue;

      if (line.startsWith("field")) {
        openField = true;
        field.append(line);
        field.append("\n");
      } 
      else if (openField && line.startsWith("}")) {
        openField = false;
        field.append(line);
        field.append("\n");
        TemplateField t = new TemplateField((String) field.toString());
        if (currentGroup != null) currentGroup.addField(t);
        else if (currentTab != null) currentTab.addField(t);
        else {
          currentTab = new TemplateTab();
          currentTab.addField(t);
        } 
        field.delete(0, field.length());
      }
      else if (openField) {
        field.append(line);
        field.append("\n");
      }
      else if (line.startsWith("group")) {
        openGroup = true; 
        currentGroup = new TemplateGroup(); 
      } 
      else if (openGroup && line.startsWith("name")) {
        line = line.substring(line.indexOf(" ")).trim();
        line = line.substring(1, line.length() - 1);
        currentGroup.setName(line); 
      }
      else if (openGroup && line.startsWith("count")) {
        line = line.substring(line.indexOf(" ")).trim();
        line = line.substring(1, line.length() - 1);
        currentGroup.setRepetitions(Integer.parseInt(line)); 
      }
      else if (openGroup && line.startsWith("}")) {
        openGroup = false;
        if (currentTab == null) currentTab = new TemplateTab();
        currentTab.addGroup(currentGroup);
        currentGroup = null; 
      } 
      else if (line.startsWith("tab")) {
        openTab = true; 
        currentTab = new TemplateTab(); 
      } 
      else if (openTab && line.startsWith("name")) {
        line = line.substring(line.indexOf(" ")).trim();
        line = line.substring(1, line.length() - 1);
        currentTab.setName(line); 
      }
      else if (openTab && line.startsWith("grid")) {
        line = line.substring(line.indexOf(" ")).trim();
        line = line.substring(1, line.length() - 1);
        int r = Integer.parseInt(line.substring(0, line.indexOf(",")));
        int c = Integer.parseInt(line.substring(line.indexOf(",") + 1));
        currentTab.setRows(r);
        currentTab.setColumns(c);
      }
      else if (openTab && line.startsWith("}")) {
        openTab = false;
        tempTabs.add(currentTab); 
        currentTab = null; 
      }
      else {
        String key = line.substring(0, line.indexOf(" ")).trim();
        String value = line.substring(line.indexOf(" ")).trim();
        value = value.substring(1, value.length() - 1);
        options.put(key, value); 
      }
    } 

    tabs = new TemplateTab[tempTabs.size()];
    tempTabs.toArray(tabs);
  }

  /** 
   * Populates the fields using the given OME root node and the 
   * template's OME-CA mapping. 
   */
  public void populateFields(OMENode root) {
    if (root == null) return;    
    
    try {
      for (int i=0; i<tabs.length; i++) {
        for (int j=0; j<tabs[i].getNumGroups(); j++) {
          TemplateGroup group = tabs[i].getGroup(j); 
          for (int k=0; k<group.getNumFields(); k++) {
            if (group.getRepetitions() == 0) {
              // special case : number of repetitions depends on the file
              // we're opening
            
              String map = group.getField(0, k).getMap(); 
              map = map.substring(0, map.indexOf(":"));

              Class st = null;
              try {
                st = Class.forName("org.openmicroscopy.xml." + map + "Node");
              }
              catch (ClassNotFoundException c) {
                st = Class.forName("org.openmicroscopy.xml.st." + map + "Node");
              } 
            
              Vector nodes = OMEXMLNode.createNodes(st, 
                DOMUtil.getChildElements(map, root.getDOMElement())); 
              group.setRepetitions(nodes.size()); 
            }
            
            for (int r=0; r<group.getRepetitions(); r++) { 
              populateField(root, group.getField(r, k));
            } 
          }
        }
      
        for (int j=0; j<tabs[i].getNumFields(); j++) {
          populateField(root, tabs[i].getField(j));
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Save fields to given OME root node. */
  public void saveFields(OMENode root) {
    if (root == null) return;

    try {
      for (int i=0; i<tabs.length; i++) {
        for (int j=0; j<tabs[i].getNumGroups(); j++) {
          TemplateGroup group = tabs[i].getGroup(j); 
          for (int k=0; k<group.getNumFields(); k++) {
            for (int r=0; r<group.getRepetitions(); r++) { 
              saveField(root, group.getField(r, k));
            } 
          }
        }

        for (int j=0; j<tabs[i].getNumFields(); j++) {
          saveField(root, tabs[i].getField(j));
        }
      } 
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Save the template to a file. */
  public void save(String filename) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(filename)); 
   
    if (options != null) {
      Enumeration keys = options.keys();
      while (keys.hasMoreElements()) {
        String key = keys.nextElement().toString();
        writer.write(key + " \"" + options.get(key) + "\"\n");
      }
    } 
    if (tabs == null) return;

    for (int i=0; i<tabs.length; i++) {
      writer.write("tab {\n");
      writer.write("  name \"" + tabs[i].getName() + "\"\n");
      
      for (int j=0; j<tabs[i].getNumFields(); j++) {
        TemplateField t = tabs[i].getField(j); 
        writer.write("  field {\n");
        writer.write("    name \"" + t.getName() + "\"\n");
        writer.write("    type \"" + t.getType() + "\"\n");
        if (t.getMap() != null) {
          writer.write("    map \"" + t.getMap() + "\"\n");
        } 
        if (t.getDefaultValue() != null) {
          writer.write("    default \"" + t.getDefaultValue() + "\"\n");
        }
        writer.write("    grid \"" + t.getRow() + "," + t.getColumn() + "\"\n");

        String[] enums = t.getEnums();
        if (enums != null && enums.length > 0) {
          writer.write("    values {");
          for (int k=0; k<enums.length; k++) {
            writer.write("\"" + enums[k] + "\""); 
            if (k < enums.length - 1) writer.write(", "); 
          }
          writer.write("}\n"); 
        }
        writer.write("  }\n"); 
      }
     
      for (int j=0; j<tabs[i].getNumGroups(); j++) {
        TemplateGroup g = tabs[i].getGroup(j); 

        writer.write("  group {\n");
        writer.write("    count \"" + g.getRepetitions() + "\"\n");
        writer.write("    name \"" + g.getName() + "\"\n");

        for (int k=0; k<g.getNumFields(); k++) {
          TemplateField t = g.getField(0, k); 
          writer.write("    field {\n");
          writer.write("      name \"" + t.getName() + "\"\n");
          writer.write("      type \"" + t.getType() + "\"\n");
          if (t.getMap() != null) {
            writer.write("      map \"" + t.getMap() + "\"\n");
          } 
          if (t.getDefaultValue() != null) {
            writer.write("      default \"" + t.getDefaultValue() + "\"\n");
          } 
          writer.write("      grid \"" + t.getRow() + "," + 
            t.getColumn() + "\"\n");
          
          String[] enums = t.getEnums();
          if (enums != null && enums.length > 0) {
            writer.write("      values {");
            for (int m=0; m<enums.length; m++) {
              writer.write("\"" + enums[m] + "\""); 
              if (m < enums.length - 1) writer.write(", "); 
            }
            writer.write("}\n"); 
          }
          writer.write("    }\n"); 
        }
      
        writer.write("  }\n"); 
      }

      writer.write("}\n");
    }
  
    writer.close(); 
  }

  /** Determine number of repetitions for each repeatable field. */
  public void initializeFields(OMENode root) {
    for (int i=0; i<tabs.length; i++) {
      int fields = tabs[i].getNumFields(); 
      for (int j=0; j<fields; j++) {
        if (tabs[i].getField(j).isRepeated()) {
          String map = tabs[i].getField(j).getMap();
          if (map.indexOf("-") != -1) map = map.substring(0, map.indexOf("-"));
          
          try {
            int nodeCount = getNodeCount(root, map);
            for (int k=1; k<nodeCount; k++) {
              TemplateField f = tabs[i].getField(j).copy();
              f.setMap(map + "-" + k);
              tabs[i].addField(f); 
            }
          }
          catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    
      for (int j=0; j<tabs[i].getNumGroups(); j++) {
        TemplateGroup g = tabs[i].getGroup(j);
        fields = g.getNumFields(); 
        for (int k=0; k<fields; k++) {
          TemplateField f = g.getField(0, k).copy();
          if (f.isRepeated()) { 
            String map = f.getMap();
            if (map.indexOf("-") != -1) {
              map = map.substring(0, map.indexOf("-"));
            }

            try {
              for (int m=1; m<getNodeCount(root, map); m++) {
                f.setMap(map + "-" + k);
                g.addField(f);
              }
            }
            catch (Exception e) {
              e.printStackTrace();  
            }
          } 
        }
      } 
    
    }
  }

  /** Change the value of an option. */
  public void changeValue(String key, String value) {
    options.put(key, value); 
  }

  /** Get the tabs defined by this template. */
  public TemplateTab[] getTabs() { return tabs; }

  /** Get the font style defined by this template. */
  public String getFontStyle() { 
    String style = (String) options.get("font-style"); 
    return style == null ? DEFAULT_FONT : style; 
  }

  /** Get the font size defined by this template. */
  public int getFontSize() {
    String size = (String) options.get("font-size");
    return size == null ? DEFAULT_FONT_SIZE : Integer.parseInt(size);
  }

  /** Get the font color defined by this template. */
  public int[] getFontColor() {
    String tuple = (String) options.get("font-color");

    if (tuple == null) return DEFAULT_FONT_COLOR;

    int comma = tuple.indexOf(",");

    String red = tuple.substring(0, comma);
    String green = tuple.substring(comma + 1, tuple.indexOf(",", comma + 1));
    String blue = tuple.substring(tuple.indexOf(",", comma + 1) + 1);
  
    return new int[] {
      Integer.parseInt(red), Integer.parseInt(green), Integer.parseInt(blue)};
  }

  /** Get the background color defined by this template. */
  public int[] getBackgroundColor() {
    String tuple = (String) options.get("background-color");

    if (tuple == null) return DEFAULT_BACKGROUND_COLOR; 

    int comma = tuple.indexOf(",");

    String red = tuple.substring(0, comma);
    String green = tuple.substring(comma + 1, tuple.indexOf(",", comma + 1));
    String blue = tuple.substring(tuple.indexOf(",", comma + 1) + 1);
  
    return new int[] {
      Integer.parseInt(red), Integer.parseInt(green), Integer.parseInt(blue)};
  }

  /** Get the default window width defined by this template. */
  public int getDefaultWidth() {
    String width = (String) options.get("default-width");
    return width == null ? DEFAULT_WIDTH : Integer.parseInt(width);
  }

  /** Get the default window height defined by this template. */
  public int getDefaultHeight() {
    String height = (String) options.get("default-height");
    return height == null ? DEFAULT_HEIGHT : Integer.parseInt(height);
  }

  /** Returns whether or not fields in this template can be edited. */
  public boolean isEditable() {
    String editable = (String) options.get("editable");
    return editable == null ? true : editable.toLowerCase().equals("true");
  }

  /** 
   * Returns whether or not the companion-file metadata should take precedence. 
   */
  public boolean preferCompanion() {
    String prefer = (String) options.get("prefer-companion");
    return prefer == null ? true : prefer.toLowerCase().equals("true"); 
  }

  /** Returns true if we can edit this template on-the-fly. */
  public boolean editTemplateFields() {
    String fields = (String) options.get("edit-template-fields");
    return fields == null ? false : fields.toLowerCase().equals("true");
  }

  /** Returns true if we can edit the OME-CA mapping on-the-fly. */
  public boolean editMapping() {
    String mapping = (String) options.get("edit-mapping");
    return mapping == null ? false : mapping.toLowerCase().equals("true");
  }

  // -- Helper methods --

  /** Populate the given TemplateField. */
  private void populateField(OMENode root, TemplateField t) throws Exception {
    OMEXMLNode node = findNode(root, t.getMap(), false); 
   
    if (node == null) {
      // unmapped field

      CustomAttributesNode ca = root.getCustomAttributes();

      if (ca != null) {
        Vector elements = DOMUtil.getChildElements("NotebookField", 
          ca.getDOMElement());
        for (int i=0; i<elements.size(); i++) {
          Element el = (Element) elements.get(i);
          if (DOMUtil.getAttribute("name", el).equals(t.getName())) {
            String v = DOMUtil.getAttribute("value", el); 
            setComponentValue(t, t.getComponent(), v);  
          }
        }
      }

      return;
    }

    String map = t.getMap();
    map = map.substring(map.lastIndexOf(":") + 1);
    if (map.indexOf("-") != -1) {
      map = map.substring(0, map.indexOf("-"));
    } 

    if (node instanceof AttributeNode) {
      setComponentValue(t, t.getComponent(), node.getAttribute(map));  
      return; 
    }

    String methodName1 = "get" + map;
    String methodName2 = "is" + map;

    if (map.equals("CreationDate")) methodName1 = "getCreated";

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

        // populate the corresponding Swing component

        setComponentValue(t, t.getComponent(), v);
        break; 
      }
    }
  }

  /** Save the given TemplateField. */
  private void saveField(OMENode root, TemplateField t) throws Exception {
    OMEXMLNode node = findNode(root, t.getMap(), true);
       
    JComponent c = t.getComponent();
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

    String map = t.getMap();
    
    if (map == null || map.length() == 0) {
      // this is a custom unmapped field, which gets stored in a
      // NotebookField ST

      CustomAttributesNode ca = root.getCustomAttributes();
      Element el = DOMUtil.createChild(ca.getDOMElement(), "NotebookField");
      OMEXMLNode newNode = OMEXMLNode.createNode(el); 
      newNode.setAttribute("name", t.getName());
      newNode.setAttribute("value", value.toString());
      return; 
    }
    
    map = map.substring(map.lastIndexOf(":") + 1);
    if (map.indexOf("-") != -1) {
      map = map.substring(0, map.indexOf("-"));
    }

    String methodName = "set" + map;

    if (map.equals("CreationDate")) methodName = "setCreated";

    Method[] methods = node.getClass().getMethods();
    for (int j=0; j<methods.length; j++) {
      String name = methods[j].getName();  
        
      Class[] params = methods[j].getParameterTypes();

      if (name.equals(methodName) && params[0].equals(String.class)) {
        methods[j].invoke(node, new Object[] {value});  
      } 
      else if (name.equals(methodName) && params[0].equals(Integer.class)) {
        methods[j].invoke(node, new Object[] {new Integer(value.toString())});
      }
    }
  }

  /** Retrieve the number of nodes corresponding to this map. */
  private int getNodeCount(OMENode root, String map) throws Exception {
    if (map == null || map.length() == 0 || root == null) return 0;
    map = map.substring(0, map.lastIndexOf(":"));
    map = map.substring(map.lastIndexOf(":") + 1);
    if (map.indexOf("-") != -1) map = map.substring(0, map.indexOf("-"));
    return DOMUtil.findElementList(map, root.getOMEDocument(true)).size(); 
  }

  /** 
   * Find the OME-XML node corresponding to the given map string. 
   * If the 'create' flag is set, a new node will be created if one does not
   * already exist.
   */
  private OMEXMLNode findNode(OMENode root, String map, boolean create) 
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

  /** Sets the value of the given component, based on the given string. */
  private void setComponentValue(TemplateField t, JComponent component, 
    String v) 
  {
    if (component instanceof JCheckBox) {
      ((JCheckBox) component).setSelected(v.startsWith("t"));
    }
    else if (component instanceof JComboBox) {
      String[] enums = t.getEnums();
      for (int k=0; k<enums.length; k++) {
        if (enums[k].toLowerCase().equals(v)) {
          ((JComboBox) component).setSelectedIndex(k);
          break;
        }
      }
    }
    else if (component instanceof JScrollPane) {
      JScrollPane scroll = (JScrollPane) component;
      JViewport view = scroll.getViewport();
      ((JTextArea) view.getView()).setText(v); 
    }
    else if (component instanceof JSpinner) {
      ((JSpinner) component).setValue(new Integer(v));
    }
  }

}
