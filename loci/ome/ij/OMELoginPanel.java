import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
/**
 * OMELoginPanel is the class that handles
 * the window used to obtain the information
 * needed to log into the OME database.
 * @author Philip Huettl pmhuettl@wisc.edu
 */
public class OMELoginPanel implements ActionListener{
  //Fields
  private JButton ok, cancels;
  private JDialog in;
  private JTextField servField, useField;
  private JPasswordField passField;
  public boolean cancelPlugin;
  
  /**Constructor, sets up the dialog box*/
  public OMELoginPanel(Frame frame){
    cancelPlugin=false;
    //Creates the Dialog Box for getting OME login information
    in=new JDialog(frame, "OME Login", true);
    JPanel input1=new JPanel(), input=new JPanel(), input2=new JPanel(),
    input3=new JPanel();
    input1.setLayout(new BoxLayout(input1, BoxLayout.Y_AXIS));
    input.setLayout(new BoxLayout(input, BoxLayout.X_AXIS));
    input2.setLayout(new BoxLayout(input2, BoxLayout.Y_AXIS));
    input3.setLayout(new BoxLayout(input3, BoxLayout.Y_AXIS));
    input1.add(input);
    input.add(input2);
    input.add(input3);
    EmptyBorder bord=new EmptyBorder(5,5,5,5);
    passField = new JPasswordField("blahblahblah",8);
    passField.setPreferredSize(passField.getMinimumSize());
    servField= new JTextField("skyking",8);
    servField.setPreferredSize(servField.getMinimumSize());
    useField= new JTextField("philip",8);
    useField.setPreferredSize(useField.getMinimumSize());
    JLabel ser=new JLabel("Server: ", JLabel.TRAILING);
    ser.setPreferredSize(ser.getMinimumSize());
    input2.add(ser);
    input3.add(servField);
    JLabel use=new JLabel("Username: ", JLabel.TRAILING);
    use.setPreferredSize(use.getMinimumSize());
    input2.add(use);
    input3.add(useField);    
    JLabel pas=new JLabel("Password: ", JLabel.TRAILING);
    pas.setPreferredSize(pas.getMinimumSize());
    input2.add(pas);
    input3.add(passField);    
    in.setContentPane(input1);
    JButton ok= new JButton("OK"), cancels=new JButton("Cancel");
    ok.setActionCommand("OK");
    cancels.setActionCommand("cancels");
    JPanel paneBut=new JPanel();
    paneBut.setLayout(new BoxLayout(paneBut,BoxLayout.X_AXIS));
    paneBut.add(ok);
    paneBut.add(cancels);
    input1.add(paneBut);
    cancels.addActionListener(this);
    ok.addActionListener(this);
    input1.setBorder(bord);    
    in.pack();
    centerWindow(frame, in);
  }//end of public constructor
  
  //Methods
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
    if ("OK".equals(e.getActionCommand())) {
      cancelPlugin=false;
      in.hide();
    }else{
      cancelPlugin=true;
      in.dispose();
    }
  }//end of actionPerformed method
  
  /**produces an error notification popup with the inputted text*/
  public static void infoShow(Frame frame, String s, String x){
    JOptionPane.showMessageDialog(frame,s,x,JOptionPane.INFORMATION_MESSAGE);
  }//end of error method
  
  /**Method that retrieves the information to log onto the OME server*/
  public String[] getInput(boolean b){
    boolean error=false;
    if ( b) {
      OMEDownPanel.error((Frame)in.getOwner(),"The login information is not valid.",
      "Input Error");
    }
    cancelPlugin=true;
    in.show();
    if ( cancelPlugin) return null;
    //checks and puts results into an array
    String[] results=new String[3];
    try {
       String server=servField.getText(), f=new String(passField.getPassword()),
       x=useField.getText();
       if ( server.equals("")||f.equals("")||x.equals("")) {
         error=true;
       }
       if (server.startsWith("http:")) {
        server = server.substring(5);
       }
       while (server.startsWith("/")) server = server.substring(1);
       int slash = server.indexOf("/");
       if (slash >= 0) server = server.substring(0, slash);
       int colon = server.indexOf(":");
       if (colon >= 0) server = server.substring(0, colon);
       server = "http://" + server + "/shoola/";
       results[0]=server;
       results[1]=x;
       results[2]=f;
    }
    catch (NullPointerException e) {
      error=true;
    }
    if ( error) {
      return getInput(true);
    }
    return results;
  }//end of getInput method
  
}//end of OMEDownPanel class