//
// EditTiffG.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.tools;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.tiff.TiffParser;
import loci.formats.tiff.TiffSaver;

/**
 * Provides a GUI for editing TIFF file comments.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/tools/EditTiffG.java">Trac</a>
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/tools/EditTiffG.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
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
      String xml = new TiffParser(id).getComment();
      setXML(xml);
      file = f;
      setTitle(TITLE + " - " + id);
    }
    catch (IOException exc) {
      showError(exc);
    }
  }

  public void saveFile(File f) {
    try {
      String xml = getXML();
      String path = f.getAbsolutePath();
      RandomAccessInputStream in = new RandomAccessInputStream(path);
      RandomAccessOutputStream out = new RandomAccessOutputStream(path);
      TiffSaver saver = new TiffSaver(out, path);
      saver.overwriteComment(in, xml);
      in.close();
      out.close();
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

  public static void openFile(String filename) {
    EditTiffG etg = new EditTiffG();
    File f = new File(filename);
    if (f.exists()) etg.openFile(f);
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
    EditTiffG.openFile(args[0]);
  }

}
