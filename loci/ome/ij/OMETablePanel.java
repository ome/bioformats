import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.util.ArrayList;
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
  private JPanel paneR, paneThumb;
  private Object[][] extra;
  private JTextArea own, typ, c1, t1, x1, y1, z1, des, thumb;
  //Constructor, sets up the dialog box
  public OMETablePanel(Frame frame, Object[][] tableData, String[] columnNames,
  Object[][] details){
    cancelPlugin=false;
    //Creates the Dialog Box for getting OME login information
    dialog=new JDialog(frame, "Search Results", true);
    JPanel mainpane=new JPanel();
    //sets up the detail panel
    paneR=new JPanel();
    paneR.setLayout(new BoxLayout(paneR, BoxLayout.Y_AXIS));
    JPanel paneOwner=new JPanel(), paneType=new JPanel();
    paneThumb=new JPanel();   
    paneOwner.setLayout(new GridLayout(7,2,5,5));
    paneType.setLayout(new GridLayout(1,2,5,5));
    paneR.add(paneThumb);
    paneR.add(paneOwner);
    paneR.add(paneType);
    JLabel owner=new JLabel("Owner: ", JLabel.RIGHT),
    type=new JLabel("Image Type: ", JLabel.RIGHT),
    c=new JLabel("Size C: ", JLabel.RIGHT),
    t=new JLabel("Size T: ", JLabel.RIGHT),
    x=new JLabel("Size X: ", JLabel.RIGHT),
    y=new JLabel("Size Y: ", JLabel.RIGHT),
    z=new JLabel("Size Z: ", JLabel.RIGHT),
    descrip=new JLabel("Description: ", JLabel.RIGHT);

    own=new JTextArea("", 1,6);
    typ=new JTextArea("", 1,6);
    c1=new JTextArea("", 1,6);
    t1=new JTextArea("", 1,6);
    x1=new JTextArea("", 1,6);
    y1=new JTextArea("", 1,6);
    z1=new JTextArea("",1,6);
    des=new JTextArea("", 2, 6);
    thumb=new JTextArea(2,15);
    des.setLineWrap(true);
    thumb.setLineWrap(true);
    des.setWrapStyleWord(true);
    thumb.setWrapStyleWord(true);
    own.setEditable(false);
    typ.setEditable(false);
    c1.setEditable(false);
    t1.setEditable(false);
    x1.setEditable(false);
    y1.setEditable(false);
    z1.setEditable(false);
    des.setEditable(false);
    thumb.setEditable(false);
    Dimension thsize=new Dimension(50,50);
    thumb.setMinimumSize(thsize);
    thumb.setMaximumSize(thsize);

    JScrollPane desScroll=new JScrollPane(des);
    paneOwner.add(owner);
    paneOwner.add(own);
    paneOwner.add(type);
    paneOwner.add(typ);
    paneOwner.add(c);
    paneOwner.add(c1);
    paneOwner.add(t);
    paneOwner.add(t1);
    paneOwner.add(x);
    paneOwner.add(x1);
    paneOwner.add(y);
    paneOwner.add(y1);
    paneOwner.add(z);
    paneOwner.add(z1);
    paneType.add(descrip);
    paneType.add(desScroll);
    paneThumb.add(thumb);
    
    extra=details;
    mainpane.setLayout(new BoxLayout(mainpane, BoxLayout.Y_AXIS));
    JLabel label=new JLabel("Select which image(s) to download to ImageJ.");
    label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    mainpane.add(label);
    //create table
    MyTableModel tableModel = new MyTableModel(tableData, columnNames);
    sorter = new TableSorter(tableModel); 
    table = new JTable(sorter);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    //Ask to be notified of selection changes.
    ListSelectionModel rowSM = table.getSelectionModel();
    rowSM.addListSelectionListener(new ListSelectionListener(){
      /**handles pane update when selection in table changes*/
      public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;

        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
        if (lsm.isSelectionEmpty()) {
          setPane(-1);
        } else {
          int selectedRow = lsm.getMinSelectionIndex();
          setPane(selectedRow);
        }
        getInput();
      }//end of valueChanged method
    });
    
    sorter.setTableHeader(table.getTableHeader());
    JScrollPane scrollPane = new JScrollPane(table);
    table.setPreferredScrollableViewportSize(new Dimension(300, 200));
    //size columns
    table.getColumnModel().getColumn(0).setMaxWidth(15);
    table.getColumnModel().getColumn(1).setPreferredWidth(50);
    table.getColumnModel().getColumn(2).setMaxWidth(23);
    table.getColumnModel().getColumn(3).setPreferredWidth(50);

    //put panel together
    JSplitPane splitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
    scrollPane, paneR);
    splitPane.setDividerLocation(200);
    splitPane.setAlignmentX(JSplitPane.CENTER_ALIGNMENT);
    mainpane.add(splitPane);
    dialog.setContentPane(mainpane);
    EmptyBorder bord=new EmptyBorder(5,5,5,5);
    JButton ok= new JButton("OK"), cancels=new JButton("Cancel");
    ok.setActionCommand("OK");
    cancels.setActionCommand("cancels");
    JPanel paneBut=new JPanel();
    paneBut.setLayout(new BoxLayout(paneBut,BoxLayout.X_AXIS));
    paneBut.add(ok);
    paneBut.add(cancels);
    paneBut.add(Box.createHorizontalGlue());
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
  
  
  
  /**method that builds the details pane for the selected image*/
  private void setPane(int row){
    paneThumb.removeAll();
    if ( row!=-1) {
      if ( extra[row][0]!=null) {
        final BufferedImage img = (BufferedImage)extra[row][0];
        JPanel imagePanel = new JPanel() {
          public void paint(Graphics g) {
            g.drawImage(img, 0, 0, this);
          }
          public Dimension getMinimumSize() {
            return new Dimension(img.getWidth(), img.getHeight());
          }
          public Dimension getPreferredSize() {
            return new Dimension(img.getWidth(), img.getHeight());
          }
          public Dimension getMaximumSize() {
            return new Dimension(img.getWidth(), img.getHeight());
          }
        };
        paneThumb.add(imagePanel);
      }else{
        paneThumb.add(thumb);
        thumb.setText("Java 1.4 required to display thumbnail.");
      }
      own.setText((String)extra[row][1]); 
      typ.setText((String)extra[row][2]);
      c1.setText((String)extra[row][3]);
      t1.setText((String)extra[row][4]);
      x1.setText((String)extra[row][5]);
      y1.setText((String)extra[row][6]);
      z1.setText((String)extra[row][7]);
      des.setText((String)extra[row][8]);
    }else{
      paneThumb.add(thumb);
      thumb.setText("");
      own.setText(""); 
      typ.setText("");
      c1.setText("");
      t1.setText("");
      x1.setText("");
      y1.setText("");
      z1.setText("");
      des.setText("");
    }
  }//end of setPane method
  
  /**Method that gets the images checked in the table to download to ImageJ*/
  public int[] getInput(){
    cancelPlugin=true;
    dialog.show();
    if ( cancelPlugin) return null;
    //checks and puts results into an array
    ArrayList al=new ArrayList();
    for ( int i=0;i<table.getRowCount() ;i++ ) {
      if ( (sorter.getValueAt(i,0)).equals(new Boolean(true))) {
        al.add(sorter.getValueAt(i,2));
      }
    }
    int[] results=new int[al.size()];
    for ( int i=0;i<al.size() ;i++ ) {
      results[i]=Integer.parseInt((String)al.get(i));
    }
    if ( results.length==0) {
      OMEDownPanel.error((Frame)dialog.getOwner(),
      "No images were selected to download.  Try again.",
      "OME Download");
      return getInput();
    }
    return results;
  }//end of getInput method
}//end of OMEDownPanel class