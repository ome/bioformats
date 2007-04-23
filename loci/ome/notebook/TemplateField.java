//
// TemplateField.java
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

import java.util.*;
import javax.swing.*;

/** Stores information about a template field. */
public class TemplateField {

  // -- Fields --

  /** Named type of this field. */
  private String type;

  /** Default value of this field (may be null). */
  private Object defaultValue;

  /** The possible values of this field (null unless type == "enum"). */
  private String[] enums;

  /** The name of this field. */
  private String name;

  /** Represents how this field should be mapped into OME-CA. */
  private String omecaMap;

  /** Component used to represent this field. */
  private JComponent component;

  /** Row and column in the layout grid. */
  private int row = -1, column = -1;

  /** Width and height of the component (in terms of grid cells). */
  private int width = 1, height = 1;

  /** Flag indicating that this field is repeated indefinitely. */
  private boolean repeated = false;

  // -- Constructors --

  /** Constructs a new empty TemplateField. */
  public TemplateField() {
  }

  /** Constructs a new TemplateField from the given definition string. */
  public TemplateField(String s) {
    if (s.indexOf("{") != -1 && s.lastIndexOf("}") != -1) {
      s = s.substring(s.indexOf("{") + 1, s.lastIndexOf("}")).trim();
      
      StringTokenizer lines = new StringTokenizer(s, "\n");
      while (lines.hasMoreTokens()) {
        String line = lines.nextToken().trim();
       
        String key = line.substring(0, line.indexOf(" ")).trim();
        String value = line.substring(line.indexOf(" ")).trim();
        value = value.substring(1, value.length() - 1);

        if (key.startsWith("name")) name = value; 
        else if (key.startsWith("type")) type = value; 
        else if (key.startsWith("map")) omecaMap = value; 
        else if (key.startsWith("repeated")) {
          repeated = new Boolean(value).booleanValue(); 
        } 
        else if (key.startsWith("grid")) {
          row = Integer.parseInt(value.substring(0, value.indexOf(",")));
          column = Integer.parseInt(value.substring(value.indexOf(",") + 1));
        }
        else if (key.startsWith("span")) {
          width = Integer.parseInt(value.substring(0, value.indexOf(",")));
          height = Integer.parseInt(value.substring(value.indexOf(",") + 1));
        }        
        else if (key.startsWith("values")) {
          StringTokenizer e = new StringTokenizer(value, "\", ");
          Vector tokens = new Vector(); 
          while (e.hasMoreTokens()) {
            String token = e.nextToken().trim();
            tokens.add(token);
          }
          enums = new String[tokens.size()];
          tokens.toArray(enums);
        }
        else if (key.startsWith("default")) {
          if (type.equals("var") || type.equals("enum")) {
            defaultValue = value;    
          }
          else if (type.equals("bool")) {
            defaultValue = new Boolean(value);
          }
          else if (type.equals("int")) {
            defaultValue = new Integer(value);
          }
        }
      }
    }
  
    // create the associate JComponent 
    
    if (type.equals("var")) {
      JTextArea text = null;
      if (width != 1 || height != 1) {
        text = new JTextArea((String) defaultValue, width, height); 
      }
      else text = new JTextArea((String) defaultValue);
      component = new JScrollPane();
      ((JScrollPane) component).getViewport().add(text);
    }
    else if (type.equals("bool")) {
      component = new JCheckBox("", ((Boolean) defaultValue).booleanValue());
    }
    else if (type.equals("enum")) {
      component = new JComboBox(enums);
      ((JComboBox) component).setSelectedItem(defaultValue);
    }
    else if (type.equals("int")) {
      if (defaultValue == null) defaultValue = new Integer(0); 
      int v = ((Integer) defaultValue).intValue();
      component = 
        new JSpinner(new SpinnerNumberModel(v, 0, Integer.MAX_VALUE, 1));
    }
  }

  // -- TemplateField API methods --

  public TemplateField copy() {
    TemplateField rtn = new TemplateField();
    rtn.setType(getType());
    rtn.setDefaultValue(getDefaultValue());
    rtn.setEnums(getEnums());
    rtn.setName(getName());
    rtn.setMap(getMap());
    rtn.setColumn(getColumn());
    rtn.setWidth(getWidth());
    rtn.setHeight(getHeight());
    rtn.setRepeated(isRepeated());

    // copy the component
    JComponent comp = null;
    if (type.equals("var")) {
      JTextArea text = null;
      if (width != 1 || height != 1) {
        text = new JTextArea((String) defaultValue, width, height); 
      }
      else text = new JTextArea((String) defaultValue);
      comp = new JScrollPane();
      ((JScrollPane) comp).getViewport().add(text);
    }
    else if (type.equals("bool")) {
      comp = new JCheckBox("", ((Boolean) defaultValue).booleanValue());
    }
    else if (type.equals("enum")) {
      comp = new JComboBox(enums);
      ((JComboBox) comp).setSelectedItem(defaultValue);
    }
    else if (type.equals("int")) {
      int v = ((Integer) defaultValue).intValue();
      comp = new JSpinner(new SpinnerNumberModel(v, 0, Integer.MAX_VALUE, 1));
    }

    rtn.setComponent(comp);
    return rtn; 
  }

  public String getType() { return type; }

  public void setType(String type) { this.type = type; }

  public Object getDefaultValue() { return defaultValue; }

  public void setDefaultValue(Object d) { defaultValue = d; }

  public String[] getEnums() { return enums; }

  public void setEnums(String[] enums) { this.enums = enums; }

  public String getName() { return name; }

  public void setName(String name) { this.name = name; } 

  public String getMap() { return omecaMap; }

  public void setMap(String map) { omecaMap = map; }

  public JComponent getComponent() { return component; }

  public void setComponent(JComponent component) { this.component = component; }

  public int getRow() { return row; }

  public void setRow(int row) { this.row = row; }

  public int getColumn() { return column; }

  public void setColumn(int column) { this.column = column; }

  public int getWidth() { return width; }

  public void setWidth(int width) { this.width = width; }

  public int getHeight() { return height; }

  public void setHeight(int height) { this.height = height; }

  public boolean isRepeated() { return repeated; }

  public void setRepeated(boolean repeated) { this.repeated = repeated; }
}
