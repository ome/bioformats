/*
 * #%L
 * OME Metadata Editor application for exploration and editing of OME-XML and
 * OME-TIFF metadata.
 * %%
 * Copyright (C) 2006 - 2012 Board of Regents of the University of
 * Wisconsin-Madison, Glencoe Software, Inc., and University of Dundee.
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

package loci.ome.editor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * A class that makes a dialog that gives a JList allowing
 * users to select the notes they want to export to a
 * text file.
 * Note that much of this code was found in an example in
 * the JDK 1.4 API Specs... ListDialog.java .
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/legacy/ome-editor/src/loci/ome/editor/ExportDialog.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/legacy/ome-editor/src/loci/ome/editor/ExportDialog.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Christopher Peterson crpeterson2 at wisc.edu
 */
public class ExportDialog extends JDialog implements ActionListener {

  // -- Fields --

  /** Holds the dialog. */
  private static ExportDialog dialog;

  /** The list of values returned as selected in the dialog. */
  private static Object [] value;

  /** The JList found in this dialog. */
  private JList list;

  // -- Constructor --

  /** Set up the GUI, etc. Can't directly instatiate this. */
  private ExportDialog(Frame frame, Component locationComp, String labelText,
    String title, Object[] data, Object[] initialValue, String longValue)
  {
    super(frame, title, true);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(this);
    final JButton exportButton = new JButton("Export Selected");
    exportButton.setActionCommand("Set");
    exportButton.addActionListener(this);
    exportButton.setToolTipText("Export the selected note(s).");
    final JButton exportAllButton = new JButton("Export All");
    exportAllButton.setActionCommand("SetAll");
    exportAllButton.addActionListener(this);
    exportAllButton.setToolTipText("Export all notes.");
    getRootPane().setDefaultButton(exportAllButton);

    list = new JList(data) {
      //Subclass JList to workaround bug 4832765, which can cause the
      //scroll pane to not let the user easily scroll up to the beginning
      //of the list.  An alternative would be to set the unitIncrement
      //of the JScrollBar to a fixed value. You wouldn't get the nice
      //aligned scrolling, but it should work.
      public int getScrollableUnitIncrement(Rectangle visibleRect,
                                            int orientation,
                                            int direction) {
        int row;
        if (orientation == SwingConstants.VERTICAL &&
          direction < 0 && (row = getFirstVisibleIndex()) != -1)
        {
          Rectangle r = getCellBounds(row, row);
          if ((r.y == visibleRect.y) && (row != 0))  {
            Point loc = r.getLocation();
            loc.y--;
            int prevIndex = locationToIndex(loc);
            Rectangle prevR = getCellBounds(prevIndex, prevIndex);

            if (prevR == null || prevR.y >= r.y) {
                return 0;
            }
            return prevR.height;
          }
        }
        return super.getScrollableUnitIncrement(
                        visibleRect, orientation, direction);
      }
    };

    list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    if (longValue != null) {
        list.setPrototypeCellValue(longValue); //get extra space
    }
    list.setLayoutOrientation(JList.VERTICAL);
    list.setVisibleRowCount(-1);
    JScrollPane listScroller = new JScrollPane(list);
    listScroller.setPreferredSize(new Dimension(300, 160));
    listScroller.setAlignmentX(LEFT_ALIGNMENT);

    JPanel listPane = new JPanel();

    FormLayout panelLayout = new FormLayout(
      "5dlu, pref:grow:right, 5dlu, pref, 5dlu, pref, 5dlu",
      "5dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu");
    listPane.setLayout(panelLayout);
    CellConstraints cc = new CellConstraints();

    listPane.setLayout(panelLayout);
    JLabel label = new JLabel(labelText);
    label.setLabelFor(list);
    listPane.add(label, cc.xyw(2,2,5));
    listPane.add(listScroller, cc.xyw(2,4,5));

    listPane.add(exportButton, cc.xy(2,6));
    listPane.add(exportAllButton, cc.xy(4,6));
    listPane.add(cancelButton, cc.xy(6,6));

    setContentPane(listPane);

    //Initialize values.
    setValue(initialValue);
    pack();
    setLocationRelativeTo(locationComp);
  }

  // -- ExportDialog API methods --

  /**
   * Set up and show the dialog.  The first Component argument
   * determines which frame the dialog depends on; it should be
   * a component in the dialog's controlling frame. The second
   * Component argument should be null if you want the dialog
   * to come up with its left corner in the center of the screen;
   * otherwise, it should be the component on top of which the
   * dialog should appear.
   */
  public static Object[] showDialog(Component frameComp,
    Component locationComp, String labelText, String title,
    Object[] possibleValues, Object[] initialValue, String longValue)
  {
    Frame frame = JOptionPane.getFrameForComponent(frameComp);
    dialog = new ExportDialog(frame, locationComp, labelText, title,
      possibleValues, initialValue, longValue);
    dialog.setVisible(true);
    return value;
  }

  // -- Helper methods --

  /** Set the selected values to a specified array. */
  private void setValue(Object [] newValue) {
    value = newValue;
    if (newValue != null) {
      for (int i = 0;i < newValue.length;i++) {
        list.setSelectedValue(value[i], false);
      }
    }
  }

  // -- ActionListener API methods --

  /** Handle clicks on the Export and ExportAll and cancel buttons. */
  public void actionPerformed(ActionEvent e) {
    if ("Set".equals(e.getActionCommand())) {
      ExportDialog.value = list.getSelectedValues();
    }
    if ("SetAll".equals(e.getActionCommand())) {
      list.setSelectionInterval(0, list.getModel().getSize() - 1);
      ExportDialog.value = list.getSelectedValues();
    }
    ExportDialog.dialog.setVisible(false);
  }

}
