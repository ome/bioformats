//
// TemplateTab.java
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

import java.util.Vector;

/** Stores information about a template tab. */
public class TemplateTab {

  // -- Fields --

  /** Name of this tab. */
  private String name;

  /** List of groups in this tab. */
  private Vector groups;

  /** List of ungrouped fields in this tab. */
  private Vector fields;

  /** Number of rows and columns in this tab. */
  private int rows, columns;

  // -- Constructor --

  public TemplateTab() {
    groups = new Vector();
    fields = new Vector();
  }

  // -- TemplateTab API methods --

  public int getRows() { return rows; }

  public void setRows(int rows) { this.rows = rows; }

  public int getColumns() { return columns; }

  public void setColumns(int columns) { this.columns = columns; }

  public int getNumGroups() { return groups.size(); }

  public int getNumFields() { return fields.size(); }

  public Vector getAllGroups() { return groups; }

  public Vector getAllFields() { return fields; }

  public TemplateGroup getGroup(int ndx) {
    return (TemplateGroup) groups.get(ndx);
  }

  public TemplateField getField(int ndx) {
    return (TemplateField) fields.get(ndx);
  }

  public TemplateField getField(int row, int col) {
    for (int i=0; i<fields.size(); i++) {
      TemplateField t = (TemplateField) fields.get(i);
      if (t.getRow() == row && t.getColumn() == col) return t;
    } 
    return null; 
  }

  public void addGroup(TemplateGroup g) { groups.add(g); }

  public void addField(TemplateField t) { fields.add(t); }

  public String getName() { return name; }

  public void setName(String name) { this.name = name; }

}
