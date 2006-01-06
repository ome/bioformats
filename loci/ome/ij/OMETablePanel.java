import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * OMETablePanel is the class that handles
 * the table used in displaying the images
 * selectable for download.
 * 
 * @author Philip Huettl pmhuettl@wisc.edu
 * @author Melissa Linkert, linkert at cs.wisc.edu
 */
public class OMETablePanel implements ActionListener {
  
  // -- Fields --

  private JDialog dialog;
  private JTable table;
  private TableSorter sorter;
  public boolean cancelPlugin;
  private JPanel paneR, paneThumb;
  private Object[][] extra;
  private JTextArea own, typ, c1, t1, x1, y1, z1, des, thumb;
  private JTextArea[] areas;
  
  // -- Constructor --

  public OMETablePanel(Frame frame, Object[][] tableData, String[] columnNames,
      Object[][] details) {
	  
    cancelPlugin = false;
    //Creates the Dialog Box for getting OME login information
    dialog = new JDialog(frame, "Search Results", true);
    JPanel mainpane = new JPanel();
    mainpane.setMinimumSize(new Dimension(800, 100));
    //sets up the detail panel
    paneR = new JPanel();
    paneR.setLayout(new BoxLayout(paneR, BoxLayout.Y_AXIS));
    JPanel paneOwner = new JPanel();
    paneThumb = new JPanel();   

    GridBagLayout grid = new GridBagLayout();
    paneOwner.setLayout(grid);
    GridBagConstraints e = new GridBagConstraints();
    GridBagConstraints d = new GridBagConstraints();
    e.weightx = 0;
    e.weighty = 0;
    e.gridheight = 1;
    e.gridwidth = 1;
    e.gridx = 0;
    e.gridy = 0;
    e.anchor = GridBagConstraints.FIRST_LINE_END;
    e.insets = new Insets(2,2,2,2);
    e.fill = GridBagConstraints.NONE;
    d.gridwidth = GridBagConstraints.REMAINDER;
    d.gridheight = 1;
    d.weighty = 0;
    d.weightx = 1;
    d.gridx = 1;
    d.gridy = 0;
    d.fill = GridBagConstraints.HORIZONTAL;
    d.insets = new Insets(2,2,2,2);
    d.anchor = GridBagConstraints.FIRST_LINE_START;
    
    paneR.add(paneThumb);
    paneR.add(paneOwner);
    JLabel owner = new JLabel("Owner: ", JLabel.RIGHT);
    JLabel type = new JLabel("Image Type: ", JLabel.RIGHT);
    JLabel c = new JLabel("Size C: ", JLabel.RIGHT);
    JLabel t = new JLabel("Size T: ", JLabel.RIGHT);
    JLabel x = new JLabel("Size X: ", JLabel.RIGHT);
    JLabel y = new JLabel("Size Y: ", JLabel.RIGHT);
    JLabel z = new JLabel("Size Z: ", JLabel.RIGHT);
    JLabel descrip = new JLabel("Description: ", JLabel.RIGHT);
    owner.setMinimumSize(new Dimension(15, 2));
    type.setMinimumSize(new Dimension(15, 2));
    c.setMinimumSize(new Dimension(10, 2));
    t.setMinimumSize(new Dimension(10, 2));
    x.setMinimumSize(new Dimension(10, 2));
    y.setMinimumSize(new Dimension(10, 2));
    z.setMinimumSize(new Dimension(10, 2));
    descrip.setMinimumSize(new Dimension(20, 2));

    areas = new JTextArea[] {own, typ, c1, t1, x1, y1, z1, des};
    for(int i=0; i<areas.length; i++) {
      areas[i] = new JTextArea("", 1, 6);
      areas[i].setEditable(false);
    }

    thumb = new JTextArea(2,15);
    areas[areas.length-1].setLineWrap(true);
    thumb.setLineWrap(true);
    areas[areas.length-1].setWrapStyleWord(true);
    thumb.setWrapStyleWord(true);
    thumb.setEditable(false);
	    
    Dimension thsize = new Dimension(300,100);
    thumb.setMinimumSize(thsize);
    thumb.setMaximumSize(thsize);

    JScrollPane desScroll = new JScrollPane(des);
   
    JLabel[] labels = {owner, type, c, t, x, y, z};
    for(int i=0; i<labels.length; i++) {
      paneOwner.add(labels[i], e);
      paneOwner.add(areas[i], d);
      e.gridy++;
      d.gridy++;
    }	    
    
    e.gridheight = 2;
    e.weighty = 2;
    d.gridheight = 2;
    d.weighty = 2;
    e.fill = GridBagConstraints.VERTICAL;
    d.fill = GridBagConstraints.BOTH;
    paneOwner.add(descrip,e);
    paneOwner.add(desScroll,d);
    paneThumb.add(thumb);
    
    extra = details;
    mainpane.setLayout(new BoxLayout(mainpane, BoxLayout.Y_AXIS));
    JLabel label = new JLabel("Select which image(s) to download to ImageJ.");
    label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    mainpane.add(label);
    //create table
    MyTableModel tableModel = new MyTableModel(tableData, columnNames);
    sorter = new TableSorter(tableModel); 
    table = new JTable(sorter);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    
    //Ask to be notified of selection changes.
    ListSelectionModel rowSM = table.getSelectionModel();
    rowSM.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;

        ListSelectionModel lsm = (ListSelectionModel)e.getSource();
        if (lsm.isSelectionEmpty()) {
          setPane(-1);
        } 
        else {
          int selectedRow = lsm.getMinSelectionIndex();
          setPane(selectedRow);
        }
      }  
    });
    
    sorter.setTableHeader(table.getTableHeader());
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setMinimumSize(new Dimension(500, 300));
    table.setPreferredScrollableViewportSize(new Dimension(800, 300));
    //size columns
    table.getColumnModel().getColumn(0).setMaxWidth(10);
    table.getColumnModel().getColumn(1).setPreferredWidth(50);
    table.getColumnModel().getColumn(2).setPreferredWidth(30);
    table.getColumnModel().getColumn(2).setMaxWidth(40);
    table.getColumnModel().getColumn(3).setPreferredWidth(50);

    //put panel together
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
      scrollPane, paneR);
    splitPane.setDividerLocation(.5);
    splitPane.setResizeWeight(.5);
    splitPane.setAlignmentX(JSplitPane.CENTER_ALIGNMENT);
    mainpane.add(splitPane);
    dialog.setContentPane(mainpane);
    EmptyBorder bord = new EmptyBorder(5,5,5,5);
    JButton ok = new JButton("OK"), cancels = new JButton("Cancel");
    ok.setActionCommand("OK");
    cancels.setActionCommand("cancels");
    JPanel paneBut = new JPanel();
    paneBut.setLayout(new BoxLayout(paneBut,BoxLayout.X_AXIS));
    paneBut.add(ok);
    paneBut.add(cancels);
    paneBut.add(Box.createHorizontalGlue());
    mainpane.add(paneBut);
    cancels.addActionListener(this);
    ok.addActionListener(this);
    mainpane.setBorder(bord);
    mainpane.setMinimumSize(new Dimension(800,300));
    dialog.pack();
    OMESidePanel.centerWindow(frame, dialog);
  }
  
  // -- Methods --

  /** implements the ActionListener actionPerformed method */
  public void actionPerformed(ActionEvent e) {
    if ("OK".equals(e.getActionCommand())) {
      cancelPlugin = false;
      dialog.hide();
    }
    else {
      cancelPlugin = true;
      dialog.dispose();
    }
  }
  
  /** method that builds the details pane for the selected image */
  private void setPane(int row) {
    paneThumb.removeAll();
    if (row != -1) {
      if (extra[row][0] != null) {
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
      }
      else {
        paneThumb.add(thumb);
        thumb.setText("Java 1.4 required to display thumbnail.");
      }
      for(int i=0; i<areas.length; i++) {
        areas[i].setText((String) extra[row][i+1]);
      }	
    }
    else {
      paneThumb.add(thumb);
      thumb.setText("");
      for(int i=0; i<areas.length; i++) {
        areas[i].setText(""); 
      }
    }  
  }
  
  /** Method that gets the images checked in the table to download to ImageJ */
  public int[] getInput() {
    cancelPlugin = true;
    dialog.show();
    if (cancelPlugin) return null;
    //checks and puts results into an array
    ArrayList al = new ArrayList();
    for (int i=0; i<table.getRowCount(); i++) {
      if (((Boolean) (sorter.getValueAt(i,0))).booleanValue()) {
        al.add(sorter.getValueAt(i,2));
      }
    }
    int[] results = new int[al.size()];
    for (int i=0; i<al.size(); i++) {
      results[i] = Integer.parseInt((String) al.get(i));
    }
    if (results.length == 0) {
      OMEDownPanel.error((Frame)dialog.getOwner(),
      "No images were selected to download.  Try again.",
        "OME Download");
      return getInput();
    }
    return results;
  }
}
