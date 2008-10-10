//
// EditTiffG.java
//

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;
import loci.formats.FormatException;
import loci.formats.TiffTools;

/** Provides a GUI for editing TIFF file comments. */
public class EditTiffG extends JFrame implements ActionListener {

  // -- Constants --

  private static final String TITLE = "EditTiffG";

  // -- Fields --

  private JTextArea textArea;
  private JFileChooser fileBox;
  private File file;

  // -- Constructor --

  public EditTiffG() {
    setTitle(TITLE);
    setLayout(new BorderLayout());
    textArea = new JTextArea(25, 80);
    add(new JScrollPane(textArea), BorderLayout.CENTER);
    //textArea.setWrapStyleWord(true);
    textArea.setLineWrap(true);

    JMenuBar menubar = new JMenuBar();
    setJMenuBar(menubar);

    JMenu file = new JMenu("File");
    menubar.add(file);

    JMenuItem fileOpen = new JMenuItem("Open");
    file.add(fileOpen);
    fileOpen.addActionListener(this);
    fileOpen.setActionCommand("open");

    JMenuItem fileSave = new JMenuItem("Save");
    file.add(fileSave);
    fileSave.addActionListener(this);
    fileSave.setActionCommand("save");

    JMenuItem fileExit = new JMenuItem("Exit");
    file.add(fileExit);
    fileExit.addActionListener(this);
    fileExit.setActionCommand("exit");

    fileBox = new JFileChooser(System.getProperty("user.dir"));

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    pack();
    setVisible(true);
  }

  // -- EditTiffG methods --

  public String getXML() {
    return textArea.getText();
  }

  public void setXML(String xml) {
    textArea.setText(xml);
  }

  public void open() {
    int rval = fileBox.showOpenDialog(this);
    if (rval != JFileChooser.APPROVE_OPTION) return;
    File f = fileBox.getSelectedFile();
    openFile(f);
  }

  public void save() {
    saveFile(file);
  }

  public void exit() {
    System.exit(0);
  }

  public void openFile(File f) {
    try {
      String id = f.getAbsolutePath();
      String xml = TiffTools.getComment(id);
      setXML(xml);
      file = f;
      setTitle(TITLE + " - " + id);
    }
    catch (FormatException exc) {
      showError(exc);
    }
    catch (IOException exc) {
      showError(exc);
    }
  }

  public void saveFile(File f) {
    try {
      String xml = getXML();
      TiffTools.overwriteComment(f.getAbsolutePath(), xml);
    }
    catch (FormatException exc) {
      showError(exc);
    }
    catch (IOException exc) {
      showError(exc);
    }
  }

  public void showError(Throwable t) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    t.printStackTrace(new PrintWriter(out));
    String error = new String(out.toByteArray());
    JOptionPane.showMessageDialog(this, "Sorry, there was an error: " + error,
      TITLE, JOptionPane.ERROR_MESSAGE);
  }

  // -- ActionListener methods --

  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if ("open".equals(cmd)) open();
    else if ("save".equals(cmd)) save();
    else if ("exit".equals(cmd)) exit();
  }

  // -- Main method --

  public static void main(String[] args) throws Exception {
    EditTiffG etg = new EditTiffG();
    File f = new File(args[0]);
    if (f.exists()) etg.openFile(f);
  }

}
