package loci.ome.notebook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class ExportDialog extends JDialog
                        implements ActionListener {
  private static ExportDialog dialog;
  private static Object [] value;
  private JList list;

  /**
   * Set up and show the dialog.  The first Component argument
   * determines which frame the dialog depends on; it should be
   * a component in the dialog's controlling frame. The second
   * Component argument should be null if you want the dialog
   * to come up with its left corner in the center of the screen;
   * otherwise, it should be the component on top of which the
   * dialog should appear.
   */
  public static Object [] showDialog(Component frameComp,
  																Component locationComp,
                                  String labelText,
                                  String title,
                                  Object[] possibleValues,
                                  Object[] initialValue,
                                  String longValue) {
      Frame frame = JOptionPane.getFrameForComponent(frameComp);
      dialog = new ExportDialog(frame,
      												locationComp,
                              labelText,
                              title,
                              possibleValues,
                              initialValue,
                              longValue);
      dialog.setVisible(true);
      return value;
  }

  private void setValue(Object [] newValue) {
    value = newValue;
    if (newValue != null) {
	    for(int i = 0;i < newValue.length;i++) {
	      list.setSelectedValue(value[i], false);
	    }
    }
  }

  private ExportDialog(Frame frame,
  									 Component locationComp,
                     String labelText,
                     String title,
                     Object[] data,
                     Object[] initialValue,
                     String longValue) {
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

  //Handle clicks on the Set and Cancel buttons.
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