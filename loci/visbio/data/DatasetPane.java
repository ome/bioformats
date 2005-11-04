//
// DatasetPane.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2005 Curtis Rueden.

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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import loci.ome.xml.OMENode;
import loci.ome.xml.DOMUtil;
import loci.visbio.VisBioFrame;
import loci.visbio.util.*;
import org.w3c.dom.Element;
import visad.VisADException;
import visad.util.Util;

/**
 * DatasetPane provides a full-featured set of options
 * for importing a multidimensional data series into VisBio.
 */
public class DatasetPane extends WizardPane implements DocumentListener {

  // -- Constants --

  /** Common dimensional types. */
  private static final String[] DIM_TYPES = {
    "Time", "Slice", "Channel", "Spectra", "Lifetime"
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
  private BioComboBox[] widgets;

  /** Combo box for selecting each file's dimensional content. */
  private BioComboBox dimBox;

  /** Checkbox for whether to use micron information. */
  private JCheckBox useMicrons;

  /** Text field for width in microns. */
  private JTextField micronWidth;

  /** Text field for height in microns. */
  private JTextField micronHeight;

  /** Text field for step size in microns. */
  private JTextField micronStep;

  /** Panel containing micron-related widgets. */
  private JPanel micronPanel;


  // -- Other fields --

  /** Associate data manager. */
  private DataManager dm;

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
  public DatasetPane(DataManager dm) {
    this(dm, SwingUtil.getVisBioFileChooser());
  }

  /** Creates a file group import dialog with the given file chooser. */
  public DatasetPane(DataManager dm, JFileChooser fileChooser) {
    super("Import data");
    this.dm = dm;
    fileBox = fileChooser;

    // -- Page 1 --

    // file pattern field
    groupField = new JTextField(25);
    groupField.getDocument().addDocumentListener(this);

    // select file button
    JButton select = new JButton("Select file");
    select.setMnemonic('s');
    select.setActionCommand("select");
    select.addActionListener(this);

    // lay out first page
    PanelBuilder builder = new PanelBuilder(new FormLayout(
      "pref, 3dlu, pref:grow, 3dlu, pref", "pref, 3dlu, pref"));
    CellConstraints cc = new CellConstraints();
    builder.addLabel("File &pattern", cc.xy(1, 1)).setLabelFor(groupField);
    builder.add(groupField, cc.xy(3, 1));
    builder.add(select, cc.xy(5, 1));
    JPanel first = builder.getPanel();

    // -- Page 2 --

    // dataset name field
    nameField = new JTextField(4);

    // combo box for dimensional range type within each file
    dimBox = new BioComboBox(DIM_TYPES);
    dimBox.setEditable(true);

    // microns checkbox
    useMicrons = new JCheckBox("Use microns instead of pixels");
    if (!LAFUtil.isMacLookAndFeel()) useMicrons.setMnemonic('u');
    useMicrons.setActionCommand("microns");
    useMicrons.addActionListener(this);

    // width in microns field
    micronWidth = new JTextField(4);
    micronWidth.setToolTipText("Width of each image in microns");

    // height in microns field
    micronHeight = new JTextField(4);
    micronHeight.setToolTipText("Height of each image in microns");

    // micron step size field
    micronStep = new JTextField(4);
    micronStep.setToolTipText("Distance between slices in microns");

    // panel with micron-related widgets
    micronPanel = FormsUtil.makeRow(new Object[]
      {useMicrons, micronWidth, "x", micronHeight, "step", micronStep},
      new boolean[] {false, true, false, true, false, true});

    // lay out second page
    second = new JPanel();
    second.setLayout(new BorderLayout());

    setPages(new JPanel[] {first, second});
    next.setEnabled(false);
  }


  // -- DatasetPane API methods --

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
      // start file chooser in the directory specified in the file pattern
      String groupText = groupField.getText();
      if (groupText.startsWith("~")) {
        String userHome = System.getProperty("user.home");
        if (userHome != null) {
          groupText = userHome + File.separator + groupText.substring(1);
        }
      }
      File dir = new File(groupText);
      if (dir.isDirectory()) {
        fileBox.setCurrentDirectory(dir);
        fileBox.setSelectedFile(null);
      }
      else {
        File file = dir;
        dir = dir = dir.getParentFile();
        if (dir != null && dir.isDirectory()) fileBox.setCurrentDirectory(dir);
        if (file.exists()) fileBox.setSelectedFile(file);
      }
      int returnVal = fileBox.showOpenDialog(this);
      if (returnVal != JFileChooser.APPROVE_OPTION) return;
      selectFile(fileBox.getSelectedFile());
    }
    else if (command.equals("microns")) {
      toggleMicronPanel(useMicrons.isSelected());
    }
    else if (command.equals("next")) {
      // lay out page 2 in a separate thread
      SwingUtil.setWaitCursor(this, true);
      disableButtons();
      new Thread("VisBio-CheckDatasetThread") {
        public void run() { buildPage(); }
      }.start();
    }
    else if (command.equals("ok")) { // Finish
      // check parameters
      final String name = nameField.getText();
      final String pattern = groupField.getText();
      final int[] lengths = fp.getCount();
      final String[] files = fp.getFiles();
      int len = lengths.length;
      boolean use = useMicrons.isSelected();
      float width = Float.NaN, height = Float.NaN, step = Float.NaN;
      try {
        width = use ? Float.parseFloat(micronWidth.getText()) : Float.NaN;
        height = use ? Float.parseFloat(micronHeight.getText()) : Float.NaN;
        step = use ? Float.parseFloat(micronStep.getText()) : Float.NaN;
      }
      catch (NumberFormatException exc) { }
      if (use) {
        String msg = null;
        if (width != width || width <= 0) {
          msg = "Invalid physical image width.";
        }
        else if (height != height || height <= 0) {
          msg = "Invalid physical image height.";
        }
        else if (step != step || step <= 0) {
          msg = "Invalid physical slice distance.";
        }
        if (msg != null) {
          JOptionPane.showMessageDialog(dialog,
            msg, "VisBio", JOptionPane.ERROR_MESSAGE);
          return;
        }
      }
      final float mw = width;
      final float mh = height;
      final float sd = step;

      // compile information on dimensional types
      boolean b = dimBox.isEnabled();
      final String[] dims = new String[b ? len + 1 : len];
      for (int i=0; i<len; i++) {
        dims[i] = (String) widgets[i].getSelectedItem();
      }
      if (b) dims[len] = (String) dimBox.getSelectedItem();

      // construct data object
      super.actionPerformed(e);
      new Thread("VisBio-FinishDatasetThread") {
        public void run() {
          dm.createDataset(name, pattern,
            files, lengths, dims, mw, mh, sd, null);
        }
      }.start();
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

  /** Helper method for DocumentListener methods. */
  protected void checkText() {
    next.setEnabled(!groupField.getText().trim().equals(""));
  }

  /** Builds the dataset pane's second page. */
  protected void buildPage() {
    // lay out page 2
    String pattern = groupField.getText();

    // parse file pattern
    fp = new FilePattern(pattern);
    if (!fp.isValid()) {
      SwingUtil.setWaitCursor(dialog, false);
      enableButtons();
      JOptionPane.showMessageDialog(dialog, fp.getErrorMessage(),
        "VisBio", JOptionPane.ERROR_MESSAGE);
      return;
    }
    int index = pattern.lastIndexOf(File.separator);
    String path = index < 0 ? "" : pattern.substring(0, index);
    pattern = pattern.substring(index + 1);

    // guess at a good name for the dataset
    String prefix = fp.getPrefix();
    nameField.setText(prefix.equals("") ? "data" + ++nameId : prefix);

    // check for matching files
    BigInteger[] min = fp.getFirst();
    BigInteger[] max = fp.getLast();
    BigInteger[] step = fp.getStep();
    ids = fp.getFiles();
    if (ids.length < 1) {
      SwingUtil.setWaitCursor(dialog, false);
      enableButtons();
      JOptionPane.showMessageDialog(dialog, "No files match the pattern.",
        "VisBio", JOptionPane.ERROR_MESSAGE);
      return;
    }
    int blocks = min.length;

    // check that pattern is not empty
    if (ids[0].trim().equals("")) {
      SwingUtil.setWaitCursor(dialog, false);
      enableButtons();
      JOptionPane.showMessageDialog(dialog, "Please enter a file pattern, " +
        "or click \"Select file\" to choose a file.", "VisBio",
        JOptionPane.INFORMATION_MESSAGE);
      return;
    }

    // check that first file really exists
    File file = new File(ids[0]);
    String filename = "\"" + file.getName() + "\"";
    if (!file.exists()) {
      SwingUtil.setWaitCursor(dialog, false);
      enableButtons();
      JOptionPane.showMessageDialog(dialog, "File " + filename +
        " does not exist.", "VisBio", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // get number of images and OME-XML metadata from the first file
    OMENode ome = null;
    int numImages = 0;
    ImageFamily loader = new ImageFamily();
    try {
      numImages = loader.getBlockCount(ids[0]);
      ome = (OMENode) loader.getOMENode(ids[0]);
    }
    catch (IOException exc) {
      if (VisBioFrame.DEBUG) exc.printStackTrace();
    }
    catch (VisADException exc) {
      if (VisBioFrame.DEBUG) exc.printStackTrace();
    }
    if (numImages < 1) {
      SwingUtil.setWaitCursor(dialog, false);
      enableButtons();
      JOptionPane.showMessageDialog(dialog,
        "Cannot determine number of images per file.\n" + filename +
        " may be corrupt or invalid.", "VisBio", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // extract dimensional axis counts from OME-XML metadata
    String dimOrder = null;
    int sizeZ = 1, sizeT = 1, sizeC = 1;
    if (ome != null) {
      try {
        Element pix = DOMUtil.findElement("Pixels", ome.getOMEDocument(true));
        String z = DOMUtil.getAttribute("SizeZ", pix);
        if (z != null && !z.equals("")) sizeZ = Integer.parseInt(z);
        String t = DOMUtil.getAttribute("SizeT", pix);
        if (t != null && !t.equals("")) sizeT = Integer.parseInt(t);
        String c = DOMUtil.getAttribute("SizeC", pix);
        if (c != null && !c.equals("")) sizeC = Integer.parseInt(c);
        dimOrder = DOMUtil.getAttribute("DimensionOrder", pix);
      }
      catch (Exception exc) {
        if (VisBioFrame.DEBUG) exc.printStackTrace();
      }
    }

    // autodetect dimensional types
    String[] kind = new String[blocks];
    int other = 1;
    boolean timeOk = true, sliceOk = true, channelOk = true, otherOk = true;
    for (int i=0; i<blocks; i++) {
      String p = fp.getPrefix(i).toLowerCase();

      // look for labels indicating dimensional type
      if (sliceOk && p.endsWith("focalplane")) {
        kind[i] = "Slice";
        sliceOk = false;
        continue;
      }

      // strip off trailing non-letters
      int q;
      char[] c = p.toCharArray();
      for (q=c.length; q>0; q--) if (c[q-1] >= 'a' && c[q-1] <= 'z') break;
      p = p.substring(0, q);

      // strip off leading letters
      c = p.toCharArray();
      for (q=c.length; q>0; q--) if (c[q-1] < 'a' || c[q-1] > 'z') break;
      p = p.substring(q);

      // look for short letter code indicating dimensional type
      if (timeOk && (p.equals("t") || p.equals("tp") || p.equals("tl"))) {
        kind[i] = "Time";
        timeOk = false;
      }
      else if (sliceOk &&
        (p.equals("z") || p.equals("zs") || p.equals("fp")))
      {
        kind[i] = "Slice";
        sliceOk = false;
      }
      else if (channelOk && p.equals("c")) {
        kind[i] = "Channel";
        channelOk = false;
      }
      else if (timeOk) {
        kind[i] = "Time";
        timeOk = false;
      }
      else if (sliceOk) {
        kind[i] = "Slice";
        sliceOk = false;
      }
      else if (channelOk) {
        kind[i] = "Channel";
        channelOk = false;
      }
      else if (otherOk) {
        kind[i] = "Other";
        otherOk = false;
      }
      else kind[i] = "Other" + ++other;
    }
    if (timeOk) dimBox.setSelectedIndex(0);
    else if (sliceOk) dimBox.setSelectedIndex(1);
    else if (channelOk) dimBox.setSelectedIndex(2);
    else if (otherOk) dimBox.setSelectedItem("Other");
    else dimBox.setSelectedItem("Other" + ++other);

    // construct dimensional widgets
    widgets = new BioComboBox[blocks];
    for (int i=0; i<blocks; i++) {
      widgets[i] = new BioComboBox(DIM_TYPES);
      widgets[i].setEditable(true);
      widgets[i].setSelectedItem(kind[i]);
    }

    // HACK - ignore buggy dimensional axis counts
    if (sizeZ * sizeT * sizeC != numImages) dimOrder = null;

    // lay out dimensions panel
    JPanel dimPanel = null;
    boolean multiFiles = dimOrder != null &&
      (sizeZ > 1 || sizeT > 1 || sizeC > 1);
    if (multiFiles || numImages > 1 || blocks > 0) {
      StringBuffer sb = new StringBuffer("pref");
      if (multiFiles) {
        if (sizeZ > 1) sb.append(", 3dlu, pref");
        if (sizeT > 1) sb.append(", 3dlu, pref");
        if (sizeC > 1) sb.append(", 3dlu, pref");
      }
      else if (numImages > 1) sb.append(", 3dlu, pref");
      for (int i=0; i<blocks; i++) sb.append(", 3dlu, pref");
      sb.append(", 9dlu");
      PanelBuilder builder = new PanelBuilder(new FormLayout(
        "pref, 12dlu, pref, 12dlu, pref:grow", sb.toString()));
      CellConstraints cc = new CellConstraints();
      builder.addSeparator("Dimensions", cc.xyw(1, 1, 5));
      int count = 0;
      if (multiFiles) {
        StringBuffer dimCross = new StringBuffer();
        for (int i=2; i<5; i++) {
          char c = dimOrder.charAt(i);
          switch (c) {
            case 'Z':
              if (sizeZ > 1) {
                int y = 2 * count + 3;
                builder.addLabel((count + 1) + ".",
                  cc.xy(1, y, "right, center"));
                builder.addLabel("Slice", cc.xy(3, y, "left, center"));
                builder.addLabel(sizeZ + " focal planes per file",
                  cc.xy(5, y, "left, center"));
                if (dimCross.length() > 0) dimCross.append(" x ");
                dimCross.append("Slice");
                count++;
              }
              break;
            case 'T':
              if (sizeT > 1) {
                int y = 2 * count + 3;
                builder.addLabel((count + 1) + ".",
                  cc.xy(1, y, "right, center"));
                builder.addLabel("Time", cc.xy(3, y, "left, center"));
                builder.addLabel(sizeT + " time points per file",
                  cc.xy(5, y, "left, center"));
                if (dimCross.length() > 0) dimCross.append(" x ");
                dimCross.append("Time");
                count++;
              }
              break;
            case 'C':
              if (sizeC > 1) {
                int y = 2 * count + 3;
                builder.addLabel((count + 1) + ".",
                  cc.xy(1, y, "right, center"));
                builder.addLabel("Channel", cc.xy(3, y, "left, center"));
                builder.addLabel(sizeC + " pixel channels per file",
                  cc.xy(5, y, "left, center"));
                if (dimCross.length() > 0) dimCross.append(" x ");
                dimCross.append("Channel");
                count++;
              }
              break;
          }
        }
        dimBox.setEnabled(true);
        dimBox.setSelectedItem(dimCross.toString());
      }
      else if (numImages > 1) {
        String num = (count + 1) + ".";
        if (count < 9) num = "&" + num;
        int y = 2 * count + 3;
        builder.addLabel(num,
          cc.xy(1, y, "right, center")).setLabelFor(dimBox);
        dimBox.setEnabled(true);
        builder.add(dimBox, cc.xy(3, y, "left, center"));
        builder.addLabel(numImages + " images per file",
          cc.xy(5, y, "left, center"));
        count++;
      }
      else dimBox.setEnabled(false);
      for (int i=0; i<blocks; i++) {
        String num = (count + 1) + ".";
        if (count < 9) num = "&" + num;
        int y = 2 * count + 3;
        builder.addLabel(num,
          cc.xy(1, y, "right, center")).setLabelFor(widgets[i]);
        builder.add(widgets[i], cc.xy(3, y, "left, center"));
        builder.addLabel(fp.getBlock(i), cc.xy(5, y, "left, center"));
        count++;
      }
      dimPanel = builder.getPanel();
    }

    // lay out widget panel
    StringBuffer sb = new StringBuffer();
    sb.append("pref, 3dlu, pref, 3dlu, pref, 3dlu, pref");
    if (dimPanel != null) sb.append(", 9dlu, pref");

    PanelBuilder builder = new PanelBuilder(new FormLayout(
      "pref, 3dlu, pref:grow", sb.toString()));
    CellConstraints cc = new CellConstraints();
    JLabel pathLabel = new JLabel("File path");
    pathLabel.setFont(pathLabel.getFont().deriveFont(Font.BOLD));
    builder.add(pathLabel, cc.xy(1, 1, "right, center"));
    builder.addLabel(path, cc.xy(3, 1, "left, center"));
    JLabel patternLabel = new JLabel("File pattern");
    patternLabel.setFont(patternLabel.getFont().deriveFont(Font.BOLD));
    builder.add(patternLabel, cc.xy(1, 3, "right, center"));
    builder.addLabel(pattern, cc.xy(3, 3, "left, center"));
    JLabel nameLabel = new JLabel("Dataset name");
    nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
    nameLabel.setDisplayedMnemonic('n');
    nameLabel.setLabelFor(nameField);
    builder.add(nameLabel, cc.xy(1, 5, "right, center"));
    builder.add(nameField, cc.xy(3, 5));
    builder.add(micronPanel, cc.xyw(1, 7, 3));
    if (dimPanel != null) builder.add(dimPanel, cc.xyw(1, 9, 3));

    second.removeAll();
    second.add(builder.getPanel());

    // clear out micron information
    micronWidth.setText("");
    micronHeight.setText("");
    micronStep.setText("");
    useMicrons.setSelected(false);
    toggleMicronPanel(false);

    Util.invoke(false, new Runnable() {
      public void run() {
        setPage(1);
        repack();
        SwingUtil.setWaitCursor(dialog, false);
        enableButtons();
      }
    });
  }

  /** Toggles availability of micron-related widgets. */
  protected void toggleMicronPanel(boolean on) {
    int count = micronPanel.getComponentCount();
    for (int i=1; i<count; i++) micronPanel.getComponent(i).setEnabled(on);
  }

}
