// derived from Sun's FileChooserDemo2


package loci.browser;

import ij.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;

public class LociOpener extends JPanel implements ItemListener {
                             
    static private String newline = "\n";
    private JFileChooser fc;
    private String filename;
    private String dir;
    private boolean virtual;

    public LociOpener() {
        super(new BorderLayout());
	
        //Set up the file chooser.
        if (fc == null) {
            fc = new JFileChooser();

	    //Add a custom file filter and disable the default
	    //(Accept All) file filter.
	    fc.addChoosableFileFilter(new ImageFilter());
	    fc.setAcceptAllFileFilterUsed(false);

	    //Add the preview pane.
	    JPanel p = new JPanel(new BorderLayout());
	    JCheckBox c = new JCheckBox("Use virtual stack");
	    p.add(new ImagePreview(fc),BorderLayout.CENTER);
	    p.add(c,BorderLayout.SOUTH);
	    c.addItemListener(this);
            fc.setAccessory(p);
        }

        //Show it.
        int returnVal = fc.showDialog(LociOpener.this,
                                      "Open");

        //Process the results.
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
	    filename = file.getName();
	    dir = fc.getCurrentDirectory().getPath()+File.separator;
        } else {
	    Macro.abort();
        }
        //Reset the file chooser for the next time it's shown.
        fc.setSelectedFile(null);
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    protected static void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
	JFrame frame = new JFrame("LOCI 4D Opener");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new LociOpener();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public void itemStateChanged(ItemEvent e) {
	if (e.getStateChange() == ItemEvent.SELECTED) 
	    virtual = true;
	else virtual = false;
    }

    public String getDirectory() {
	return dir;
    }

    public String getFileName() {
	return filename;
    }
    
    public boolean getVirtual() {
	return virtual;
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
