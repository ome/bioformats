import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import org.openmicroscopy.ds.dto.*;
/**
 * OMEDownPanel is the class that handles
 * the window used to enter image search
 * criteria.
 * @author Philip Huettl pmhuettl@wisc.edu
 */
public class OMEDownPanel implements ActionListener{
  //Fields
  private JButton search, cancel;
  private JDialog dia;
  private JComboBox cproj, ctype, cowner;
  private JTextField id, name;
  public boolean cancelPlugin;
  
  //Constructor, sets up the dialog box
  public OMEDownPanel(Frame frame, Project[] projects, String[][] owners){
    cancelPlugin=false;
    //creates the dialog box for searching for images
    dia=new JDialog(frame, "OME Download Search", true);
    JPanel pane=new JPanel(), panel=new JPanel();
    pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    //labels
    JLabel enter=new JLabel("Specify search criteria");
    enter.setAlignmentX(Component.CENTER_ALIGNMENT);
    JLabel enter1=new JLabel("of the image you wish to");
    enter1.setAlignmentX(Component.CENTER_ALIGNMENT);
    JLabel enter2=new JLabel("upload into ImageJ.");
    enter2.setAlignmentX(Component.CENTER_ALIGNMENT);
    pane.add(panel);
    pane.setMaximumSize(pane.getPreferredSize());
    panel.add(enter);
    panel.add(enter1);
    panel.add(enter2);
    pane.setBorder(new EmptyBorder(5,5,5,5));
    //panels
    JPanel paneL=new JPanel(), paneR=new JPanel(), paneInfo=new JPanel(), paneButtons=
    new JPanel();
    paneL.setLayout(new BoxLayout(paneL, BoxLayout.Y_AXIS));
    paneR.setLayout(new BoxLayout(paneR, BoxLayout.Y_AXIS));
    paneInfo.setLayout(new BoxLayout(paneInfo, BoxLayout.X_AXIS));
    paneInfo.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED),new EmptyBorder(5,5,5,5)));
    paneButtons.setLayout(new BoxLayout(paneButtons, BoxLayout.X_AXIS));
    paneButtons.setBorder(new EmptyBorder(5,5,5,5));
    paneInfo.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    paneButtons.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    pane.add(paneInfo);
    paneInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
    paneButtons.setAlignmentX(Component.CENTER_ALIGNMENT);
    pane.add(paneButtons);
    paneInfo.add(paneL);
    paneInfo.add(paneR);
    dia.setContentPane(pane);
    //borders
    EmptyBorder bordCombo=new EmptyBorder(1,0,4,0);
    EmptyBorder bordText=new EmptyBorder(3,0,2,0);
    //more components
    JLabel lproj=new JLabel("Project:");
    lproj.setBorder(bordCombo);
    lproj.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
    paneL.add(lproj);
    String[] projectS=new String[projects.length+1];
    projectS[0]="All";
    for ( int i=0;i<projects.length ;i++ ) {
      projectS[i+1]=projects[i].getName();
    }
    cproj = new JComboBox(projectS);
    cproj.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    cproj.setMaximumSize(cproj.getPreferredSize());
    paneR.add(cproj);
    JLabel lowner=new JLabel("Owner:");
    lowner.setBorder(bordCombo);
    lowner.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
    paneL.add(lowner);
    String[] ownerS=new String[owners[0].length+1];
    ownerS[0]="All";
    for ( int i=0;i<owners[0].length ;i++ ) {
      ownerS[i+1]=owners[0][i]+" "+owners[1][i];
    }
    cowner = new JComboBox(ownerS);
    cowner.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    cowner.setMaximumSize(cowner.getPreferredSize());
    paneR.add(cowner);
  
    id = new JTextField(5);
    id.setMaximumSize(id.getPreferredSize());
    id.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    JLabel label = new JLabel("Image ID:");
    label.setBorder(bordText);
    label.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
    paneL.add(label);
    paneL.setBorder(new EmptyBorder(5,5,5,5));
    paneR.add(id);
    
    name=new JTextField(10);
    name.setMaximumSize(name.getPreferredSize());
    name.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    paneR.add(name);
    JLabel lname= new JLabel("Image Name:");
    lname.setBorder(bordText);
    lname.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
    paneL.add(lname);
    
    JButton search= new JButton("Search"), cancel=new JButton("Cancel"),
    resets=new JButton("Reset");
    resets.setActionCommand("reset");
    search.setActionCommand("search");
    cancel.setActionCommand("cancel");
    paneButtons.add(search);
    paneButtons.add(cancel);
    paneButtons.add(resets);
    search.addActionListener(this);
    cancel.addActionListener(this);
    resets.addActionListener(this);
    
    dia.pack();
    centerWindow(frame, dia);
  }//end of public constructor
  
  //Methods
  /**shows and retrieves info from the DownPanel*/
  public Object[] search(){
    cancelPlugin=true;
    dia.show();
    if ( cancelPlugin) return null;
    //checks and puts results into an array
    Object[] results=new Object[4];
    results[0]=cproj.getSelectedItem();
    results[1]=cowner.getSelectedItem();
    try {
       String s=id.getText();
       if ( s.equals("")) {
         s="0";
       }
       results[2]=Integer.decode(s);
    }
    catch (NumberFormatException n) {
      error((Frame)dia.getOwner(),"The image ID field is not valid.", "Input Error");
      return search();
    }
    catch (NullPointerException e) {
      results[2]=new Integer(0);
    }
    try {
       results[3]=name.getText();
    }
    catch (NullPointerException e) {
      results[3]="";
    }

    return results;
  }//end of search method
  
  /** Centers the given window within the specified parent window. */
  private void centerWindow(Window parent, Window window) {
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
    if ( "search".equals(e.getActionCommand())) {
      cancelPlugin=false;
      dia.hide();
    }else if ("reset".equals(e.getActionCommand()) ) {
      cancelPlugin=false;
      reset();
    }else{
      cancelPlugin=true;
      dia.dispose();
    }
  }//end of actionPerformed method
  
  /**produces an error notification popup with the inputted text*/
  public static void error(Frame frame, String s, String x){
    JOptionPane.showMessageDialog(frame,s,x,JOptionPane.ERROR_MESSAGE);
  }//end of error method
  
  /**Resets the options of the search dialog box*/
  public void reset(){
    cproj.setSelectedIndex(0);
    cowner.setSelectedIndex(0);
    ctype.setSelectedIndex(0);
    id.setText(null);
    name.setText(null);
  }//end of reset method

}//end of OMEDownPanel class