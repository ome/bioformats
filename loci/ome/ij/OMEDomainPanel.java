

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
/**
 * OMEDomainPanel is the class that handles
 * the window used to obtain the domain information
 * on a multidimensional image to allow it to be read
 * in ImageJ.
 * @author Philip Huettl pmhuettl@wisc.edu
 */
public class OMEDomainPanel implements ActionListener, ChangeListener{
  //Fields
  private JButton ok, cancels;
  private JRadioButton time, space;
  private JDialog pick;
  private JSlider slide;
  private JLabel label4;
  public boolean cancelPlugin, timeDomain;
  private int sizeZ, sizeT;
  /**Constructor, sets up the dialog box*/
  public OMEDomainPanel(Frame frame, int z, int t){
    cancelPlugin=false;
    sizeZ=z;
    sizeT=t;
    //Creates the Dialog Box for getting OME login information
    pick=new JDialog(frame, "Domain Selection", true);
    JPanel mainpane=new JPanel(), paneR=new JPanel(), paneL=new JPanel(),
    paneM=new JPanel();
    mainpane.setLayout(new BoxLayout(mainpane, BoxLayout.Y_AXIS));
    paneR.setLayout(new BoxLayout(paneR, BoxLayout.Y_AXIS));
    paneL.setLayout(new BoxLayout(paneL, BoxLayout.Y_AXIS));
    paneM.setLayout(new BoxLayout(paneM, BoxLayout.X_AXIS));
    JLabel label=new JLabel("The image you have selected contains"),
    label1=new JLabel("variability in both the space and time domains."),
    label2=new JLabel("ImageJ does not support this, so choose the domain and"),
    label3=new JLabel("value to set as a constant."),
    label5=new JLabel("Selected Value: ");
    label4=new JLabel("1");
    paneL.add(label5);
    paneR.add(label4);
    mainpane.add(label);
    mainpane.add(label1);
    mainpane.add(label2);
    mainpane.add(label3);
    paneM.add(paneL);
    paneM.add(paneR);
    JRadioButton space = new JRadioButton("Space");
    space.setMnemonic(KeyEvent.VK_S);
    space.setActionCommand("Space");
    space.setSelected(true);
    timeDomain=false;
    space.addActionListener(this);
    mainpane.add(space);
    JRadioButton time= new JRadioButton("Time");
    time.setMnemonic(KeyEvent.VK_T);
    time.setActionCommand("Time");
    time.addActionListener(this);
    mainpane.add(time);
    mainpane.add(paneM);
    ButtonGroup group=new ButtonGroup();
    group.add(space);
    group.add(time);
    slide=new JSlider(0, sizeZ-1, 0);
    slide.setMajorTickSpacing(5);
    slide.setMinorTickSpacing(1);
    slide.setPaintTicks(true);
    slide.setPaintLabels(true);
    slide.addChangeListener(this);
    mainpane.add(slide);
    pick.setContentPane(mainpane);
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
    pick.pack();
    centerWindow(frame, pick);
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
  
  /**implement the ActionListener actionPerformed method*/
  public void actionPerformed(ActionEvent e){
    if ("OK".equals(e.getActionCommand())) {
      cancelPlugin=false;
      pick.hide();
    }else if ("Time".equals(e.getActionCommand())) {
      slide.setMaximum(sizeT-1);
      slide.setMajorTickSpacing(5);
      slide.setMinorTickSpacing(1);
      slide.setPaintTicks(true);
      slide.setPaintLabels(true);
      timeDomain=true;
      slide.repaint();
    }else if ("Space".equals(e.getActionCommand())) {
      slide.setMaximum(sizeZ-1);
      slide.setMajorTickSpacing(5);
      slide.setMinorTickSpacing(1);
      slide.setPaintTicks(true);
      slide.setPaintLabels(true);
      timeDomain=false;
      slide.repaint();
    }else{
      cancelPlugin=true;
      pick.dispose();
    }
  }//end of actionPerformed method
  
  /**Method that retrieves the information to input the specified domain*/
  public int[] getInput(){
    cancelPlugin=true;
    pick.show();
    if ( cancelPlugin) return null;
    //checks and puts results into an array
    int[] results=new int[2];
    if (timeDomain==true) {
      results[0]=1;
    }
    else results[0]=0;
    results[1]=slide.getValue();
    return results;
  }//end of getInput method
  
  /**Method that implements the ChangeListener stateChanged method*/
  public void stateChanged(ChangeEvent e){
    JSlider source = (JSlider)e.getSource();
    int fps = (int)source.getValue();
    label4.setText(String.valueOf(fps));    
  }//end of stateChanged method
  
}//end of OMEDownPanel class