//
// DatasetWidget.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2004 Curtis Rueden.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.visbio.data;

import java.awt.BorderLayout;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import loci.visbio.util.SwingUtil;

/** DatasetWidget is a set of GUI controls for a Dataset transform. */
public class DatasetWidget extends JPanel implements ListSelectionListener {

  // -- Constants --

  /** Column headings. */
  protected static final String[] COLUMNS = {"Name", "Value"};


  // -- Fields --

  /** Associated dataset. */
  protected Dataset dataset;

  /** Dataset's associated metadata. */
  protected Hashtable[] metadata;

  /** Metadata hashtables' sorted key lists. */
  protected String[][] keys;

  /** List of dataset source files. */
  protected JList list;

  /** Table listing metadata fields. */
  protected JTable table;

  /** Table model backing metadata table. */
  protected DefaultTableModel tableModel;


  // -- Constructor --

  /** Constructs widget for display of dataset's associated metadata. */
  public DatasetWidget(Dataset dataset) {
    super();
    this.dataset = dataset;

    // get dataset's metadata
    metadata = dataset.getMetadata();
    keys = new String[metadata.length][];

    // sort metadata keys
    for (int i=0; i<metadata.length; i++) {
      if (metadata[i] == null) {
        keys[i] = new String[0];
        continue;
      }
      Enumeration e = metadata[i].keys();
      Vector v = new Vector();
      while (e.hasMoreElements()) v.add(e.nextElement());
      keys[i] = new String[v.size()];
      v.copyInto(keys[i]);
      Arrays.sort(keys[i]);
    }

    // list of filenames
    String[] ids = dataset.getFilenames();
    String[] names = new String[ids.length];
    for (int i=0; i<names.length; i++) names[i] = new File(ids[i]).getName();
    list = new JList(names);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane scrollList = new JScrollPane(list);
    SwingUtil.configureScrollPane(scrollList);
    list.addListSelectionListener(this);

    // metadata table
    tableModel = new DefaultTableModel(COLUMNS, 0);
    table = new JTable(tableModel);
    JScrollPane scrollTable = new JScrollPane(table);
    SwingUtil.configureScrollPane(scrollTable);
    list.setSelectedIndex(0);

    // split pane
    JSplitPane split =
      new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollList, scrollTable);

    // lay out components
    setLayout(new BorderLayout());
    add(split);
  }


  // -- ListSelectionListener API methods --

  /** Called when the value of the selection changes. */
  public void valueChanged(ListSelectionEvent e) {
    int index = list.getSelectedIndex();
    if (index < 0 || index >= keys.length) {
      tableModel.setRowCount(0);
      return;
    }

    int len = keys[index].length;
    tableModel.setRowCount(len);
    for (int i=0; i<len; i++) {
      tableModel.setValueAt(keys[index][i], i, 0);
      tableModel.setValueAt(metadata[index].get(keys[index][i]), i, 1);
    }
  }

}
