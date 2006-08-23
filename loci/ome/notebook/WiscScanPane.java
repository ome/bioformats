package loci.ome.notebook;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import org.openmicroscopy.xml.*;
import org.w3c.dom.*;

public class WiscScanPane extends JTabbedPane {
	public WiscScanPane() {
	  setPreferredSize(new Dimension(700, 500));
		JPanel loginPanel = new JPanel();
		JPanel experimentPanel = new ScrollablePanel();
		JScrollPane jScroll = new JScrollPane(experimentPanel);
		addTab("WiscScan Login", (Icon) null, loginPanel, 
		  "Emulates the login screen of WiscScan.");
	  addTab("Experiment Setup Information", (Icon) null, 
	    jScroll, "Emulates the Experiment Setup Information"
	    + " screen of WiscScan.");
	  
	  //gui setup for login screen
	  int w = 300, h = 20;
	  JLabel firstLabel = new JLabel("First Name");
	  firstLabel.setPreferredSize(new Dimension(w,h));
	  JTextField firstField = new JTextField();
	  firstField.setPreferredSize(new Dimension(w,h));
	  JLabel lastLabel = new JLabel("Last Name");
	  lastLabel.setPreferredSize(new Dimension(w,h));
	  JTextField lastField = new JTextField();
	  lastField.setPreferredSize(new Dimension(w,h));
	  JLabel OMELabel = new JLabel("OME Name (not supported)");
	  OMELabel.setPreferredSize(new Dimension(w,h));
	  JTextField OMEField = new JTextField();
	  OMEField.setEnabled(false);
	  OMEField.setPreferredSize(new Dimension(w,h));
	  JLabel passLabel = new JLabel("Password (not supported)");
	  passLabel.setPreferredSize(new Dimension(w,h));
	  JTextField passField = new JTextField();
	  passField.setEnabled(false);
	  passField.setPreferredSize(new Dimension(w,h));
	  JLabel emailLabel = new JLabel("Email");
	  emailLabel.setPreferredSize(new Dimension(w,h));
	  JTextField emailField = new JTextField();
	  emailField.setPreferredSize(new Dimension(w,h));
	  JLabel groupLabel = new JLabel("Group");
	  groupLabel.setPreferredSize(new Dimension(w,h));
	  JComboBox groupBox = new JComboBox();
	  groupBox.setPreferredSize(new Dimension(w,h));
	  
	  JPanel subPanel = null;
	  
	  FormLayout layout = new FormLayout(
        "5dlu,center:pref,5dlu",
        "5dlu,pref,5dlu,pref,5dlu,pref,5dlu,pref,5dlu,pref,5dlu,pref," +
          "5dlu,pref,5dlu,pref,5dlu,pref,5dlu,pref,5dlu,pref,5dlu,pref,5dlu");
    PanelBuilder build = new PanelBuilder(layout);
    CellConstraints cc = new CellConstraints();
    
    build.add(firstLabel,cc.xy(2,2));
    build.add(firstField,cc.xy(2,4));
    build.add(lastLabel,cc.xy(2,6));
    build.add(lastField,cc.xy(2,8));
    build.add(OMELabel,cc.xy(2,10));
    build.add(OMEField,cc.xy(2,12));
    build.add(passLabel,cc.xy(2,14));
    build.add(passField,cc.xy(2,16));
    build.add(emailLabel,cc.xy(2,18));
    build.add(emailField,cc.xy(2,20));
    build.add(groupLabel,cc.xy(2,22));
    build.add(groupBox,cc.xy(2,24));
    
    subPanel = build.getPanel();
    
    Border lineB = BorderFactory.createMatteBorder(1, 
    	1, 1, 1, Color.BLACK);
    EmptyBorder emptyB = new EmptyBorder(5,5,5,5);
    EmptyBorder empB = new EmptyBorder(5,5,5,5);
    CompoundBorder tempB = new CompoundBorder(lineB,emptyB);
    CompoundBorder finalB = new CompoundBorder(empB,lineB);
    subPanel.setBorder(finalB);
    
    JPanel holderP = new JPanel();
    holderP.add(subPanel);
    
    JLabel welcomeLabel = new JLabel("Welcome To WiscScan", 
    	JLabel.CENTER);
//    Font thisFont = welcomeLabel.getFont();
    Font thisFont = new Font("Serif",
      Font.PLAIN,64);
    welcomeLabel.setFont(thisFont);
    
    loginPanel.setLayout(new BorderLayout());
    loginPanel.add(welcomeLabel, BorderLayout.NORTH);
    loginPanel.add(holderP, BorderLayout.CENTER);
    
    //gui setup for experiment setup screen
    
    Border etchB = BorderFactory.createEtchedBorder(
    	EtchedBorder.LOWERED);
		TitledBorder infoB = BorderFactory.createTitledBorder(
		  etchB, "Experiment Information");
		TitledBorder filterB = BorderFactory.createTitledBorder(
		  etchB, "Filter");
		TitledBorder laserB = BorderFactory.createTitledBorder(
		  etchB, "Laser");
		TitledBorder detB = BorderFactory.createTitledBorder(
		  etchB, "Detector");

    JPanel infoP, filterP, laserP, detP;
    
    w = 250;
    
    JLabel exL = new JLabel("Experiment Type");
    JLabel prL = new JLabel("Project Name");
    JLabel desL = new JLabel("Description");
    JLabel tempL = new JLabel("Temperature");
    JLabel pocL = new JLabel("Pockel Cell");
    JLabel tapL = new JLabel("Tap Settings");
    
    JLabel wheeL = new JLabel("Wheel");
    JLabel hoL = new JLabel("Holder");
    
		JTextField prT = new JTextField();
		prT.setPreferredSize(new Dimension(w,h));
		JTextField tempT = new JTextField();
		tempT.setPreferredSize(new Dimension(w,h));
		tempT.setEnabled(false);
		JTextField pocT = new JTextField();
		pocT.setPreferredSize(new Dimension(w,h));
		pocT.setEnabled(false);
		JTextField tapT = new JTextField();
		tapT.setPreferredSize(new Dimension(w,h));
		tapT.setEnabled(false);
		
		JTextField tiT = new JTextField();
		tiT.setPreferredSize(new Dimension(80,h));
		
		JTextField pmtT = new JTextField();
		pmtT.setPreferredSize(new Dimension(80,h));
		pmtT.setEnabled(false);
		
		JTextArea desA = new JTextArea("",4,1);
		desA.setLineWrap(true);
    desA.setWrapStyleWord(true);
		JScrollPane desS = new JScrollPane(desA);
		desS.setPreferredSize(new Dimension(w,h*4));
		desS.setVerticalScrollBarPolicy(
      JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		JComboBox exB = new JComboBox();
		exB.setPreferredSize(new Dimension(w,h));
		JComboBox wheeB = new JComboBox();
		wheeB.setEnabled(false);
		JComboBox hoB = new JComboBox();
		hoB.setEnabled(false);
		
		JCheckBox tiC = new JCheckBox("Ti-Sapphire");
		JCheckBox phC = new JCheckBox("Photodiode Bio-Rad 1024TLD");
		JCheckBox pmtC = new JCheckBox("PMT Hamamatsu H7422");
		
		FormLayout layoutF = new FormLayout(
        "5dlu,pref,5dlu,pref:grow,5dlu",
        "5dlu,pref,5dlu,pref,5dlu");
    PanelBuilder buildF = new PanelBuilder(layoutF);
    CellConstraints ccF = new CellConstraints();
    
    buildF.add(wheeL, ccF.xy(2,2));
    buildF.add(wheeB, ccF.xy(4,2));
    buildF.add(hoL, ccF.xy(2,4));
    buildF.add(hoB, ccF.xy(4,4));
    
    filterP = buildF.getPanel();
    filterP.setBorder(filterB);
    
    FormLayout layoutL = new FormLayout(
        "5dlu,pref,5dlu,pref,pref:grow, 5dlu",
        "5dlu,pref,25dlu");
    PanelBuilder buildL = new PanelBuilder(layoutL);
    CellConstraints ccL = new CellConstraints();
    
    buildL.add(tiC, cc.xy(2,2));
    buildL.add(tiT, cc.xy(4,2));
    
    laserP = buildL.getPanel();
    laserP.setBorder(laserB);
    
    FormLayout layoutD = new FormLayout(
        "5dlu,pref,5dlu,pref,pref:grow, 5dlu",
        "5dlu,pref,5dlu,pref,20dlu");
    PanelBuilder buildD = new PanelBuilder(layoutD);
    CellConstraints ccD = new CellConstraints();
    
    buildD.add(phC, cc.xy(2,2));
    buildD.add(pmtC, cc.xy(2,4));
    buildD.add(pmtT, cc.xy(4,4));
    
    detP = buildD.getPanel();
    detP.setBorder(detB);
    
    FormLayout layoutE = new FormLayout(
        "5dlu,pref,5dlu,pref,5dlu,",
        "5dlu,pref,5dlu,pref,5dlu,top:pref,5dlu,pref,5dlu,pref,"
        + "5dlu,pref,5dlu,pref,5dlu,pref,5dlu,pref,5dlu");
    PanelBuilder buildE = new PanelBuilder(layoutE);
    CellConstraints ccE = new CellConstraints();
    
    buildE.add(exL, cc.xy(2,2));
    buildE.add(exB, cc.xy(4,2));
    buildE.add(prL, cc.xy(2,4));
    buildE.add(prT, cc.xy(4,4));
    buildE.add(desL, cc.xy(2,6));
    buildE.add(desS, cc.xy(4,6));
    buildE.add(tempL, cc.xy(2,8));
    buildE.add(tempT, cc.xy(4,8));
    buildE.add(pocL, cc.xy(2,10));
    buildE.add(pocT, cc.xy(4,10));
    buildE.add(tapL, cc.xy(2,12));
    buildE.add(tapT, cc.xy(4,12));
    buildE.add(filterP, cc.xyw(2,14,3));
    buildE.add(laserP, cc.xyw(2,16,3));
    buildE.add(detP, cc.xyw(2,18,3));
    
    infoP = buildE.getPanel();
    infoP.setBorder(infoB);
    
    experimentPanel.add(infoP);
	}
	
	// --Helper Classes--
	
	public class ScrollablePanel extends JPanel
	  implements Scrollable
	{
	  public ScrollablePanel() {
	    super();
	  }
	  
	  public Dimension getPreferredScrollableViewportSize() {
      return getPreferredSize();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect,
      int orientation, int direction) {return 5;}
    public int getScrollableBlockIncrement(Rectangle visibleRect,
      int orientation, int direction) {return visibleRect.height;}
    public boolean getScrollableTracksViewportWidth() {return true;}
    public boolean getScrollableTracksViewportHeight() {return false;}
  }
}