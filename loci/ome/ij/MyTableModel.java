//
// MyTableModel.java
//

/*
OME Plugin for ImageJ plugin for transferring images to and from an OME
database. Copyright (C) 2004-2006 Philip Huettl and Melissa Linkert.

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

package loci.ome.ij;

import javax.swing.table.DefaultTableModel;

/**
 * MyTableModel is the class that handles
 * the table used in displaying the images
 * selectable for download.
 * @author Philip Huettl pmhuettl@wisc.edu
 */

public class MyTableModel extends DefaultTableModel {

  public MyTableModel(Object[][] data, Object[] columnNames) {
    super(data, columnNames);
  }

  public Class getColumnClass(int c) {
    return getValueAt(0, c).getClass();
  }

  public boolean isCellEditable(int row, int col) {
    //Note that the data/cell address is constant,
    //no matter where the cell appears onscreen.
    if (col == 0)  return true;
    return false;
  }
}
