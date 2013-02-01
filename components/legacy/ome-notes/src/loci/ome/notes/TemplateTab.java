/*
 * #%L
 * OME Notes library for flexible organization and presentation of OME-XML
 * metadata.
 * %%
 * Copyright (C) 2007 - 2013 Open Microscopy Environment:
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

package loci.ome.notes;

import java.util.Vector;

/**
 * Stores information about a template tab.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/legacy/ome-notes/src/loci/ome/notes/TemplateTab.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/legacy/ome-notes/src/loci/ome/notes/TemplateTab.java;hb=HEAD">Gitweb</a></dd></dl>
 */
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

  public int getRows() {
    if (rows == 0) {
      for (int i=0; i<fields.size(); i++) {
        rows += ((TemplateField) fields.get(i)).getHeight();
      }

      for (int i=0; i<groups.size(); i++) {
        TemplateGroup g = (TemplateGroup) groups.get(i);
        for (int k=0; k<g.getRepetitions(); k++) {
          for (int j=0; j<g.getNumFields(); j++) {
            rows += g.getField(0, j).getHeight();
          }
          rows++;
        }
      }
    }

    return rows;
  }

  public void setRows(int rows) { this.rows = rows; }

  public int getColumns() {
    if (columns != 0) return columns;
    int max = 1;

    for (int i=0; i<fields.size(); i++) {
      TemplateField f = (TemplateField) fields.get(i);
      if (f.getColumn() > max) max = f.getColumn();
    }
    columns = max;
    return columns;
  }

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

  public void removeGroup(int ndx) { groups.removeElementAt(ndx); }

  public void removeField(int ndx) { fields.removeElementAt(ndx); }

  public String getName() { return name; }

  public void setName(String name) { this.name = name; }

}
