// OMETablePanel.java

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
/**
 * OMETablePanel is the class that handles
 * the table used in displaying the images
 * selectable for download.
 * @author Philip Huettl pmhuettl@wisc.edu
 */
public class OMETablePanel implements ActionListener {
  //Fields
  private JDialog dialog;
  private JTable table;
  private TableSorter sorter;
  public boolean cancelPlugin;
  //Constructor, sets up the dialog box
  public OMETablePanel(Frame frame, String[][] tableData, String[] columnNames){
    cancelPlugin=false;
    //Creates the Dialog Box for getting OME login information
    dialog=new JDialog(frame, "Search Results", true);
    JPanel mainpane=new JPanel(), paneR=new JPanel(), paneL=new JPanel(),
    paneM=new JPanel();
    mainpane.setLayout(new BoxLayout(mainpane, BoxLayout.Y_AXIS));
    JLabel label=new JLabel("Select which image(s) to download to ImageJ.");
    mainpane.add(label);
    //create table
    DefaultTableModel tableModel = new DefaultTableModel(tableData, columnNames);
    sorter = new TableSorter(tableModel); 
    table = new JTable(sorter);
    sorter.setTableHeader(table.getTableHeader());
    JScrollPane scrollPane = new JScrollPane(table);
    table.setPreferredScrollableViewportSize(new Dimension(600, 200));
    //size columns
    table.getColumnModel().getColumn(0).setPreferredWidth(50);
    table.getColumnModel().getColumn(1).setPreferredWidth(23);
    table.getColumnModel().getColumn(1).setMaxWidth(45);
    table.getColumnModel().getColumn(2).setPreferredWidth(50);
    table.getColumnModel().getColumn(3).setPreferredWidth(100);
    table.getColumnModel().getColumn(3).setMaxWidth(170);
    table.getColumnModel().getColumn(3).setMinWidth(80);
    table.getColumnModel().getColumn(4).setPreferredWidth(20);
    table.getColumnModel().getColumn(4).setMaxWidth(60);
    table.getColumnModel().getColumn(5).setPreferredWidth(15);
    table.getColumnModel().getColumn(5).setMaxWidth(40);
    table.getColumnModel().getColumn(6).setPreferredWidth(25);
    table.getColumnModel().getColumn(6).setMaxWidth(40);
    table.getColumnModel().getColumn(7).setPreferredWidth(25);
    table.getColumnModel().getColumn(7).setMaxWidth(40);
    table.getColumnModel().getColumn(8).setPreferredWidth(25);
    table.getColumnModel().getColumn(8).setMaxWidth(40);
    table.getColumnModel().getColumn(9).setPreferredWidth(25);
    table.getColumnModel().getColumn(9).setMaxWidth(50);
    table.getColumnModel().getColumn(10).setPreferredWidth(100);
    //put panel together
    mainpane.add(scrollPane);
    dialog.setContentPane(mainpane);
    EmptyBorder bord=new EmptyBorder(5,5,5,5);
    JButton ok= new JButton("OK"), cancels=new JButton("Cancel");
    ok.setActionCommand("OK");
    cancels.setActionCommand("cancels");
    JPanel paneBut=new JPanel();
    paneBut.setLayout(new BoxLayout(paneBut,BoxLayout.X_AXIS));
    paneBut.add(ok);
    paneBut.add(cancels);
    mainpane.add(paneBut);
    cancels.addActionListener(this);
    ok.addActionListener(this);
    mainpane.setBorder(bord);    
    dialog.pack();
    centerWindow(frame, dialog);
  }//end of public constructor
  
  //Methods
  /** Centers the given window within the specified parent window. */
  public static void centerWindow(Window parent, Window window) {
    Point loc = parent.getLocation();
    Dimension p = parent.getSize();
    Dimension w = window.getSize();
    int x = loc.x + (p.width - w.width) / 2;
    int y = loc.y + (p.height - w.height) / 2;
    if (x < 0) x = 0;
    if (y < 0) y = 0;
    window.setLocation(x, y);
  }//end of centerWindow method
  
  /**implements the ActionListener actionPerformed method*/
  public void actionPerformed(ActionEvent e){
    if ("OK".equals(e.getActionCommand())) {
      cancelPlugin=false;
      dialog.hide();
    }else{
      cancelPlugin=true;
      dialog.dispose();
    }
  }//end of actionPerformed method

  /**Method that gets the images selected from the table to download to ImageJ*/
  public int[] getInput(){
    cancelPlugin=true;
    dialog.show();
    if ( cancelPlugin) return null;
    //checks and puts results into an array
    int[] results=table.getSelectedRows();
    if ( results.length==0) {
      OMEDownPanel.error((Frame)dialog.getOwner(),
      "No images were selected to download.  Try again.",
      "OME Download");
      return getInput();
    }
    for ( int i=0;i<results.length ;i++ ) {
      results[i]=Integer.parseInt((String)sorter.getValueAt(results[i], 1));
    }
    return results;
  }//end of getInput method
}//end of OMEDownPanel class
