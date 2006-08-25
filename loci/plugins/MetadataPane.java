//
// MetadataPane.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the 4D
Data Browser, OME Plugin and Bio-Formats Exporter. Copyright (C) 2006
Melissa Linkert, Curtis Rueden, Philip Huettl and Francis Wong.

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

package loci.plugins;

import java.awt.BorderLayout;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MetadataPane extends JPanel {

  // -- Constants --

  /** Column headings for metadata table. */
  protected static final String[] META_COLUMNS = {"Name", "Value"};


  // -- Fields --

  /** Associated metadata. */
  protected Hashtable metadata;

  /** Metadata hashtable's sorted key list. */
  protected String[] keys;

  /** Table listing metadata fields. */
  protected JTable metaTable;

  /** Table model backing metadata table. */
  protected DefaultTableModel metaTableModel;


  // -- Constructor --

  /** Constructs a widget for display of a dataset's associated metadata. */
  public MetadataPane(Hashtable metadata) {
    super();

    // sort metadata keys
    if (metadata == null) keys = new String[0];
    else {
      Enumeration e = metadata.keys();
      Vector v = new Vector();
      while (e.hasMoreElements()) v.add(e.nextElement());
      keys = new String[v.size()];
      v.copyInto(keys);
      Arrays.sort(keys);
    }

    // metadata table
    metaTableModel = new DefaultTableModel(META_COLUMNS, 0);
    metaTable = new JTable(metaTableModel);
    JScrollPane scrollMetaTable = new JScrollPane(metaTable);

    // update metadata table
    int len = keys.length;
    metaTableModel.setRowCount(len);
    for (int i=0; i<len; i++) {
      metaTableModel.setValueAt(keys[i], i, 0);
      metaTableModel.setValueAt(metadata.get(keys[i]), i, 1);
    }

    // lay out components
    setLayout(new BorderLayout());
    add(scrollMetaTable);
  }

}
