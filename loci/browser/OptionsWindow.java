/* OptionsWindow.java
 * Pops up an options window with tabbed panes.
 * Allows users to specify virtualization options.
 */
package loci.browser;

import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.Box.Filler;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Frame;
import java.awt.Insets;

public class OptionsWindow extends JPanel implements ActionListener {
    private int zMax, tMax;
    private int zCurrent, tCurrent;
    private int z1, z2, t1, t2;
    private JDialog dlg;
    private boolean customVirtual;
    private JSpinner spinner1, spinner2, spinner3, spinner4;

    public OptionsWindow(int z, int t) {
	super (new GridLayout(1,1));
	customVirtual = false;
	zMax = z-1;
	tMax = t-1;
	JTabbedPane tabbedPane = new JTabbedPane();
	JComponent panel1 = makeVirtualizationPanel();
	tabbedPane.addTab("Virtualization", null, panel1,
			  "Virtualization options");

	JComponent panel2 = makeChannelPanel();
	tabbedPane.addTab("Channels", null, panel2,
			  "Channel options");
	add(tabbedPane);

    }

    private JComponent makeVirtualizationPanel() {
	JPanel panel = new JPanel(false);
	//	JLabel filler = new JLabel("Virtualization");
	//	filler.setHorizontalAlignment(JLabel.CENTER);
	// 	panel.add(filler);
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	c.fill = GridBagConstraints.HORIZONTAL;
	panel.setLayout(gridbag);
	
	JRadioButton timeButton = new JRadioButton("Virtualizes across time");
	timeButton.setActionCommand("time");
	timeButton.setSelected(true);
	c.gridx = 0;
	c.gridy = 0;
	c.gridwidth = GridBagConstraints.REMAINDER;
	panel.add(timeButton, c);
	
	JRadioButton zButton = new JRadioButton("Virtualizes across Z-axis");
	zButton.setActionCommand("z");
	c.gridx = 0;
	c.gridy = 1;
	panel.add(zButton, c);

	JRadioButton custButton = new JRadioButton("Custom virtualization:");
	custButton.setActionCommand("custom");
	c.gridx = 0;
	c.gridy = 2;
	panel.add(custButton, c);

	c.gridx = 1;
	c.gridy = 3;
	c.gridwidth = 1;

	c.insets = new Insets(10,20,0,5);
	panel.add(new JLabel("Z-axis: "),c);
	
	System.err.println("zMax = "+zMax);
	SpinnerNumberModel model1 = new SpinnerNumberModel(1, 1,
					   zMax < 1 ? 1:zMax, 1);

	spinner1 = new JSpinner(model1);
	c.fill = GridBagConstraints.NONE;
	c.gridx = 3;
	c.gridy = 3;
	c.ipadx = 10;
	c.insets = new Insets(10,20,0,5);
	panel.add(spinner1, c);

	c.gridx = 4;
	c.gridy = 3;
	c.insets = new Insets(10,5,0,5);
	panel.add(new JLabel(" to "),c);
	
	SpinnerNumberModel model2 = new SpinnerNumberModel(1, 1,
					   zMax < 1 ? 1:zMax, 1);

	spinner2 = new JSpinner(model2);
	c.gridx = 5;
	c.gridy = 3;
	c.insets = new Insets(10,0,0,0);
	panel.add(spinner2, c);

	c.gridx = 1;
	c.gridy = 4;
	c.gridwidth = 1;

	c.insets = new Insets(10,20,0,5);
	panel.add(new JLabel("Time: "),c);
	
	SpinnerNumberModel model3 = new SpinnerNumberModel(1, 1,
					   tMax < 1 ? 1:tMax, 1);
	spinner3 = new JSpinner(model3);
	c.fill = GridBagConstraints.NONE;
	c.gridx = 3;
	c.gridy = 4;
	c.insets = new Insets(10,20,0,5);
	panel.add(spinner3, c);

	c.gridx = 4;
	c.gridy = 4;
	c.insets = new Insets(10,5,0,5);
	panel.add(new JLabel(" to "),c);
	
	SpinnerNumberModel model4 = new SpinnerNumberModel(1, 1,
					   tMax < 1 ? 1:tMax, 1);
	System.err.println("Earlier... tMax = "+tMax);
	spinner4 = new JSpinner(model4);
	c.gridx = 5;
	c.gridy = 4;
	c.insets = new Insets(10,0,0,0);
	panel.add(spinner4, c);

	spinner1.setEnabled(false);
	spinner2.setEnabled(false);
	spinner3.setEnabled(false);
	spinner4.setEnabled(false);

	c.gridx = 5;
	c.gridy = 5;
	JButton okButton = new JButton("Ok");
	okButton.setActionCommand("ok");
	panel.add(okButton, c);
	
	
	ButtonGroup group = new ButtonGroup();
	group.add(timeButton);
	group.add(zButton);
	group.add(custButton);
	
	timeButton.addActionListener(this);
	zButton.addActionListener(this);
	custButton.addActionListener(this);
	okButton.addActionListener(this);
	
	panel.setBorder(new EmptyBorder(10,10,10,10));
	return panel;
    }


    public void actionPerformed(ActionEvent e) {
	if ("z".equals(e.getActionCommand())) {
	    z1 = 1;
	    z2 = zMax;
	    t1 = t2 = tCurrent;

	    customVirtual = false;
	    spinner1.setEnabled(false);
	    spinner2.setEnabled(false);
	    spinner3.setEnabled(false);
	    spinner4.setEnabled(false);
	} else if ("time".equals(e.getActionCommand())) {
	    t1 = 1;
	    t2 = tMax;
	    z1 = zCurrent;
	    z2 = zCurrent;
	    customVirtual = false;
	    spinner1.setEnabled(false);
	    spinner2.setEnabled(false);
	    spinner3.setEnabled(false);
	    spinner4.setEnabled(false);
	} else if ("custom".equals(e.getActionCommand())) {
	    customVirtual = true;
	    if (zMax!=1) {
		System.err.println("zMax!=0... zMax = "+zMax);
		spinner1.setEnabled(true);
		spinner2.setEnabled(true);
	    }
	    if (tMax!=1) {
		System.err.println("tMax!=0... tMax = "+tMax);
		spinner3.setEnabled(true);
		spinner4.setEnabled(true);
	    }
	} else if ("ok".equals(e.getActionCommand())) {
	    if (customVirtual) {
		z1 = ((Integer)spinner1.getValue()).intValue();
		z2 = ((Integer)spinner2.getValue()).intValue();
		t1 = ((Integer)spinner3.getValue()).intValue();
		t2 = ((Integer)spinner4.getValue()).intValue();
	    }
	    System.err.println("Here!!!!!");
	    dlg.dispose();
	}
    }
    
    private JComponent makeChannelPanel() {
	JPanel panel = new JPanel(false);
	JLabel filler = new JLabel("Channels");
	filler.setHorizontalAlignment(JLabel.CENTER);
	panel.setLayout(new GridLayout(1, 1));
	panel.add(filler);
	return panel;
    }	

    public boolean isCustomVirtualization() {
	return customVirtual;
    }
    
    public int[] getVirtualBounds() {
	return new int[] {z1, z2, t1, t2};
    }
    
    public void popup(Frame frame, int z, int t) {
	//	JComponent newContentPane = new OptionsWindow();
	zCurrent = z;
	tCurrent = t;
 	setOpaque(true);
	JDialog.setDefaultLookAndFeelDecorated(true);
	dlg = new JDialog(frame, "Options", true);
	dlg.setLocationRelativeTo(frame);
	dlg.getContentPane().add(this,
				 BorderLayout.CENTER);
 	// position window at center of screen
 	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
 	Dimension frameSize = frame.getSize();
 	dlg.setLocation((screenSize.width - frameSize.width) / 2, 
 			  (screenSize.height - frameSize.height) / 2);
	dlg.pack();
	dlg.setVisible(true);
    }
    
}

    
    
    
