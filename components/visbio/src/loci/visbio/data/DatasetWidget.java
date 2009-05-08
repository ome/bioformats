//
// DatasetWidget.java
//

/*
VisBio application for visualization of multidimensional biological
image data. Copyright (C) 2002-@year@ Curtis Rueden and Abraham Sorber.

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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;

import loci.formats.gui.XMLCellRenderer;
import loci.visbio.util.SwingUtil;
import ome.xml.OMEXMLNode;

import org.w3c.dom.Document;

/**
 * DatasetWidget is a set of GUI controls for a Dataset transform.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/visbio/src/loci/visbio/data/DatasetWidget.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/visbio/src/loci/visbio/data/DatasetWidget.java">SVN</a></dd></dl>
 */
public class DatasetWidget extends JPanel {

  // -- Constants --

  /** Column headings for metadata table. */
  protected static final String[] META_COLUMNS = {"Name", "Value"};

  // -- Fields --

  /** Associated dataset. */
  protected Dataset dataset;

  /** Dataset's associated metadata. */
  protected Hashtable metadata;

  /** Metadata hashtable's sorted key lists. */
  protected String[] keys;

  /** Table listing metadata fields. */
  protected JTable metaTable;

  /** Table model backing metadata table. */
  protected DefaultTableModel metaTableModel;

  // -- Constructor --

  /** Constructs widget for display of dataset's associated metadata. */
  public DatasetWidget(Dataset dataset) {
    super();
    this.dataset = dataset;

    // get dataset's metadata
    metadata = dataset.getMetadata();

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

    // -- First tab --

    // metadata table
    metaTableModel = new DefaultTableModel(META_COLUMNS, 0);
    metaTable = new JTable(metaTableModel);
    JScrollPane scrollMetaTable = new JScrollPane(metaTable);
    SwingUtil.configureScrollPane(scrollMetaTable);

    // -- Second tab --

    // OME-XML tree
    OMEXMLNode root = dataset.getOMEXMLRoot();
    Document doc = root.getDOMElement().getOwnerDocument();
    JTree xmlTree = XMLCellRenderer.makeJTree(doc);
    JScrollPane scrollTree = new JScrollPane(xmlTree);
    SwingUtil.configureScrollPane(scrollTree);

    // -- Main GUI --

    // tabbed pane
    JTabbedPane tabbed = new JTabbedPane();
    tabbed.addTab("Original metadata", scrollMetaTable);
    tabbed.addTab("OME-XML", scrollTree);

    // lay out components
    setLayout(new BorderLayout());
    add(tabbed);

    // populate metadata table
    int len = keys.length;
    metaTableModel.setRowCount(len);
    for (int i=0; i<len; i++) {
      metaTableModel.setValueAt(keys[i], i, 0);
      metaTableModel.setValueAt(metadata.get(keys[i]), i, 1);
    }
  }

}
