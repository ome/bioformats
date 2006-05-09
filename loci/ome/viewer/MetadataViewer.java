//
// MetadataViewer.java
//

package loci.ome.viewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import loci.util.About;

/** MetadataViewer is a simple application for displaying OME-XML metadata. */
public class MetadataViewer extends JFrame implements ActionListener {

  // -- Fields --

  protected JFileChooser chooser;
  protected MetadataPane metadata;


  // -- Constructor --

  public MetadataViewer(String[] args) {
    super("OME-TIFF Metadata Viewer");
    metadata = new MetadataPane();
    setContentPane(metadata);

    JMenuBar menubar = new JMenuBar();
    setJMenuBar(menubar);
    JMenu file = new JMenu("File");
    menubar.add(file);
    JMenuItem fileOpen = new JMenuItem("Open");
    file.add(fileOpen);
    fileOpen.setActionCommand("open");
    fileOpen.addActionListener(this);
    JMenuItem fileExit = new JMenuItem("Exit");
    file.add(fileExit);
    fileExit.setActionCommand("exit");
    fileExit.addActionListener(this);
    JMenu help = new JMenu("Help");
    menubar.add(help);
    JMenuItem helpAbout = new JMenuItem("About");
    help.add(helpAbout);
    helpAbout.setActionCommand("about");
    helpAbout.addActionListener(this);

    chooser = new JFileChooser(System.getProperty("user.dir"));

    if (args.length > 0) openFile(new File(args[0]));
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocation(100, 100);
    pack();
    setVisible(true);
  }


  // -- MetadataViewer API methods --

  public void openFile(File file) { metadata.setOMEXML(file); }


  // -- ActionListener API methods --

  /** Handles menu commands. */
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if ("open".equals(cmd)) {
      int rval = chooser.showOpenDialog(this);
      if (rval == JFileChooser.APPROVE_OPTION) {
        openFile(chooser.getSelectedFile());
      }
    }
    else if ("exit".equals(cmd)) dispose();
    else if ("about".equals(cmd)) About.show();
  }



  // -- Main method --

  public static void main(String[] args) { new MetadataViewer(args); }

}
