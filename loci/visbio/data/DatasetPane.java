//
// DatasetPane.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2004 Curtis Rueden.

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

package loci.visbio.data;

import com.jgoodies.forms.builder.PanelBuilder;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import java.awt.BorderLayout;

import java.awt.event.ActionEvent;

import java.io.File;
import java.io.IOException;

import java.math.BigInteger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import loci.visbio.util.SwingUtil;
import loci.visbio.util.WizardPane;

import visad.VisADException;

/**
 * DatasetPane provides a full-featured set of options
 * for importing a multidimensional data series into VisBio.
 */
public class DatasetPane extends WizardPane implements DocumentListener {

  // -- Constants --

  /** Common dimensional types. */
  private static final String[] DIM_TYPES = {
    "Time", "Slice", "Channel", "Lifetime"
  };


  // -- GUI components, page 1 --

  /** Choose file dialog box. */
  private JFileChooser fileBox;

  /** File group text field. */
  private JTextField groupField;


  // -- GUI components, page 2 --

  /** Dataset name text field. */
  private JTextField nameField;

  /** Panel for dynamic second page components. */
  private JPanel second;

  /** Dimensional widgets. */
  private JComboBox[] widgets;

  /** Label for dimBox. */
  private JLabel dimLabel;

  /** Combo box for selecting each file's dimensional content. */
  private JComboBox dimBox;


  // -- Other fields --

  /** File pattern. */
  private FilePattern fp;

  /** List of data files. */
  private String[] ids;

  /** Raw dataset created from import pane state. */
  private Dataset data;

  /** Next free id number for dataset naming scheme. */
  private int nameId;


  // -- Constructor --

  /** Creates a file group import dialog. */
  public DatasetPane() { this(SwingUtil.getVisBioFileChooser()); }

  /** Creates a file group import dialog with the given file chooser. */
  public DatasetPane(JFileChooser fileChooser) {
    super("Open file group");
    fileBox = fileChooser;

    // file pattern field (first page)
    groupField = new JTextField(25);
    groupField.getDocument().addDocumentListener(this);

    // select file button (first page)
    JButton select = new JButton("Select file");
    select.setMnemonic('s');
    select.setActionCommand("select");
    select.addActionListener(this);

    // dataset name field (second page)
    nameField = new JTextField(4);

    // combo box for dimensional range type within each file (second page)
    dimBox = new JComboBox(DIM_TYPES);
    dimBox.setEditable(true);
    dimBox.setSelectedIndex(1);

    // lay out first page
    PanelBuilder builder = new PanelBuilder(new FormLayout(
      "pref, 3dlu, pref:grow, 3dlu, pref",
      "pref"));
    CellConstraints cc = new CellConstraints();
    builder.addLabel("File &pattern", cc.xy(1, 1)).setLabelFor(groupField);
    builder.add(groupField, cc.xy(3, 1));
    builder.add(select, cc.xy(5, 1));
    JPanel first = builder.getPanel();

    // lay out second page
    second = new JPanel();
    second.setLayout(new BorderLayout());

    setPages(new JPanel[] {first, second});
    next.setEnabled(false);
  }


  // -- DatasetPane API methods --

  /** Gets the Dataset object created from the import pane state. */
  public Dataset getDataset() { return data; }

  /** Examines the given file to determine if it is part of a file group. */
  public void selectFile(File file) {
    if (file == null || file.isDirectory()) {
      groupField.setText("");
      return;
    }
    String pattern = FilePattern.findPattern(file);
    if (pattern == null) {
      groupField.setText(file.getAbsolutePath());
      return;
    }
    groupField.setText(pattern);
  }


  // -- ActionListener API methods --

  /** Handles button press events. */
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (command.equals("select")) {
      int returnVal = fileBox.showOpenDialog(this);
      if (returnVal != JFileChooser.APPROVE_OPTION) return;
      selectFile(fileBox.getSelectedFile());
    }
    else if (command.equals("next")) {
      // lay out page 2
      String pattern = groupField.getText();
      //groupLabel.setText(pattern);

      // parse file pattern
      fp = new FilePattern(pattern);
      if (!fp.isValid()) {
        JOptionPane.showMessageDialog(dialog, fp.getErrorMessage(),
          "VisBio", JOptionPane.ERROR_MESSAGE);
        return;
      }

      // guess at a good name for the dataset
      String prefix = fp.getPrefix();
      nameField.setText(prefix.equals("") ? "data" + ++nameId : prefix);

      // check for matching files
      BigInteger[] min = fp.getFirst();
      BigInteger[] max = fp.getLast();
      BigInteger[] step = fp.getStep();
      ids = fp.getFiles();
      if (ids.length < 1) {
        JOptionPane.showMessageDialog(dialog, "No files match the pattern.",
          "VisBio", JOptionPane.ERROR_MESSAGE);
        return;
      }
      int blocks = min.length;

      // check that pattern is not empty
      if (ids[0].trim().equals("")) {
        JOptionPane.showMessageDialog(dialog, "Please enter a file pattern, " +
          "or click \"Select file\" to choose a file.", "VisBio",
          JOptionPane.INFORMATION_MESSAGE);
        return;
      }

      // check that first file really exists
      File file = new File(ids[0]);
      String filename = "\"" + file.getName() + "\"";
      if (!file.exists()) {
        JOptionPane.showMessageDialog(dialog, "File " + filename +
          " does not exist.", "VisBio", JOptionPane.ERROR_MESSAGE);
        return;
      }

      // determine number of images per file
      int numImages;
      ImageFamily loader = new ImageFamily();
      try { numImages = loader.getBlockCount(ids[0]); }
      catch (IOException exc) { numImages = 0; }
      catch (VisADException exc) { numImages = 0; }
      if (numImages < 1) {
        JOptionPane.showMessageDialog(dialog,
          "Cannot determine number of images per file.\n" + filename +
          " may be corrupt or invalid.", "VisBio", JOptionPane.ERROR_MESSAGE);
        return;
      }

      // autodetect each dimension's type
      String[] kind = new String[blocks];
      if (blocks > 0) kind[0] = "Time";
      if (blocks > 1) kind[1] = "Slice";
      for (int i=2; i<blocks; i++) kind[i] = "Other";

      // construct dimensional widgets
      widgets = new JComboBox[blocks];
      for (int i=0; i<blocks; i++) {
        widgets[i] = new JComboBox(DIM_TYPES);
        widgets[i].setEditable(true);
        widgets[i].setSelectedItem(kind[i]);
      }

      // lay out widget panel
      second.removeAll();
      StringBuffer sb = new StringBuffer();
      sb.append("pref, 3dlu, pref");
      for (int i=0; i<blocks; i++) sb.append(", 3dlu, pref");
      sb.append(", 3dlu, pref");

      PanelBuilder builder = new PanelBuilder(new FormLayout(
        "pref, 3dlu, pref:grow, 3dlu, pref", sb.toString()));
      CellConstraints cc = new CellConstraints();
      builder.addSeparator(pattern, cc.xyw(1, 1, 5));
      builder.addLabel("Dataset &name", cc.xy(1, 3)).setLabelFor(nameField);
      builder.add(nameField, cc.xyw(3, 3, 3));

      for (int i=0; i<blocks; i++) {
        String s = (i + 1) + " - [min=" + min[i] +
          "; max=" + max[i] + "; step=" + step[i] + "]";
        if (i < 9) s = "&" + s;
        int y = 2 * i + 5;
        JLabel label = builder.addLabel(s, cc.xyw(1, y, 3));
        label.setLabelFor(widgets[i]);
        label.setHorizontalAlignment(JLabel.RIGHT);
        builder.add(widgets[i], cc.xy(5, y));
      }

      dimLabel = builder.addLabel("File's images &define a new dimension",
        cc.xyw(1, 2 * blocks + 5, 3));
      dimLabel.setLabelFor(dimBox);
      dimLabel.setHorizontalAlignment(JLabel.RIGHT);
      builder.add(dimBox, cc.xy(5, 2 * blocks + 5));

      second.add(builder.getPanel());

      // enable/disable dimensional combo box
      boolean b = numImages > 1;
      dimLabel.setEnabled(b);
      dimBox.setEnabled(b);

      super.actionPerformed(e);
    }
    else if (command.equals("ok")) {
      String pattern = groupField.getText();
      int[] lengths = fp.getCount();
      String[] files = fp.getFiles();
      int len = lengths.length;

      // compile information on dimensional types
      boolean b = dimBox.isEnabled();
      String[] dims = new String[b ? len + 1 : len];
      for (int i=0; i<len; i++) {
        dims[i] = (String) widgets[i].getSelectedItem();
      }
      if (b) dims[len] = (String) dimBox.getSelectedItem();

      // construct data object
      data = new Dataset(nameField.getText(),
        groupField.getText(), files, lengths, dims);

      super.actionPerformed(e);
    }
    else super.actionPerformed(e);
  }


  // -- DocumentListener API methods --

  /** Gives notification that an attribute or set of attributes changed. */
  public void changedUpdate(DocumentEvent e) { checkText(); }

  /** Gives notification that there was an insert into the document. */
  public void insertUpdate(DocumentEvent e) { checkText(); }

  /** Gives notification that a portion of the document has been removed. */
  public void removeUpdate(DocumentEvent e) { checkText(); }


  // -- Helper methods --

  protected void checkText() {
    next.setEnabled(!groupField.getText().trim().equals(""));
  }

}
