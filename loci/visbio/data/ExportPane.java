//
// ExportPane.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-@year@ Curtis Rueden.

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
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.swing.*;
import loci.formats.*;
import loci.formats.out.TiffWriter;
import loci.visbio.*;
import loci.visbio.util.*;
import visad.*;
import visad.util.DataUtility;

/**
 * ExportPane provides a full-featured set of options for exporting a
 * multidimensional data series from VisBio to several different formats.
 */
public class ExportPane extends WizardPane {

  // -- GUI components, page 1 --

  /** Folder on disk text field. */
  private JTextField folderField;

  /** Button for choosing output folder. */
  private JButton chooseButton;

  /** File pattern text field. */
  private JTextField patternField;

  /** File format combo box. */
  private BioComboBox formatBox;


  // -- GUI components, page 2 --

  /** Panel for dynamic second page components. */
  private JPanel second;

  /** Check box indicating whether file numbering should use leading zeroes. */
  private JCheckBox leadingZeroes;

  /** Frames per second for movie formats. */
  private JTextField fps;

  /** Checkbox for LZW compression (TIFF only). */
  private JCheckBox lzw;

  /** Combo boxes for mapping each star to corresponding dimensional axes. */
  private BioComboBox[] letterBoxes;


  // -- GUI components, page 3 --

  /** Panel for dynamic third page components. */
  private JPanel third;

  /** Pane containing export summary. */
  private JEditorPane summary;


  // -- Other fields --

  /** Associated VisBio frame (for displaying export status). */
  private VisBioFrame bio;

  /** Writer for saving images to disk. */
  private ImageWriter saver;

  /** Data object from which exportable data will be derived. */
  private ImageTransform trans;

  /** File pattern, divided into tokens. */
  private String[] tokens;

  /** Number of "stars" in the file pattern. */
  private int stars;

  /** Mapping from each star to corresponding dimensional axis. */
  private int[] maps;

  /** Excluded index (not mapped to a star). */
  private int excl;

  /** Number of total files to export. */
  private int numFiles;


  // -- Constructor --

  /** Creates a multidimensional data export dialog. */
  public ExportPane(VisBioFrame bio) {
    super("Export data");
    this.bio = bio;
    saver = new ImageWriter();

    // -- Page 1 --

    // folder widgets
    folderField = new JTextField(System.getProperty("user.dir"));
    chooseButton = new JButton("Choose...");
    chooseButton.setMnemonic('h');
    chooseButton.setActionCommand("folder");
    chooseButton.addActionListener(this);

    // pattern fields
    patternField = new JTextField();

    // format combo box
    String[] formats = new String[] {"TIFF", "MOV"};
    formatBox = new BioComboBox(formats);

    // lay out first page
    PanelBuilder builder = new PanelBuilder(new FormLayout(
      "pref, 3dlu, pref:grow, 3dlu, pref, 3dlu, pref", "pref, 3dlu, pref"));
    CellConstraints cc = new CellConstraints();
    builder.addLabel("F&older", cc.xy(1, 1)).setLabelFor(folderField);
    builder.add(folderField, cc.xyw(3, 1, 3));
    builder.add(chooseButton, cc.xy(7, 1));

    builder.addLabel("File &pattern", cc.xy(1, 3)).setLabelFor(patternField);
    builder.add(patternField, cc.xy(3, 3));
    builder.addLabel("&Format", cc.xy(5, 3)).setLabelFor(formatBox);
    builder.add(formatBox, cc.xy(7, 3));
    JPanel first = builder.getPanel();

    // -- Page 2 --

    // pad file numbering with leading zeroes checkbox
    leadingZeroes = new JCheckBox("Pad file numbering with leading zeroes");
    leadingZeroes.setMnemonic('p');

    // frames per second text field
    fps = new JTextField(4);

    // LZW compression checkbox
    lzw = new JCheckBox("LZW compression");
    lzw.setMnemonic('c');

    // lay out second page
    second = new JPanel();
    second.setLayout(new BorderLayout());

    // -- Page 3 --

    // summary text area
    summary = new JEditorPane();
    summary.setEditable(false);
    summary.setContentType("text/html");
    JScrollPane summaryScroll = new JScrollPane(summary);
    SwingUtil.configureScrollPane(summaryScroll);

    // lay out third page
    third = new JPanel();
    third.setLayout(new BorderLayout());

    // lay out pages
    setPages(new JPanel[] {first, second, third});
  }


  // -- ExportPane API methods --

  /** Associates the given data object with the export pane. */
  public void setData(ImageTransform trans) {
    this.trans = trans;
    if (trans == null) return;
  }

  /** Exports the data according to the current input parameters. */
  public void export() {
    final int[] lengths = trans.getLengths();
    final int numImages = excl < 0 ? 1 : lengths[excl];
    final int numTotal = numFiles * numImages;
    final String format = (String) formatBox.getSelectedItem();
    TaskManager tm = (TaskManager) bio.getManager(TaskManager.class);
    final BioTask task = tm.createTask("Export " +
      trans.getName() + " to disk");
    new Thread() {
      public void run() {
        try {
          TiffWriter tiffSaver = null;
          if (format.equals("TIFF")) {
            tiffSaver = (TiffWriter) saver.getWriter(TiffWriter.class);
          }
          int count = 0;
          int max = 2 * numTotal;
          task.setStatus(0, max, "Exporting data");
          boolean doLZW = lzw.isSelected();
          boolean padZeroes = leadingZeroes.isSelected();
          int[] plen = new int[stars];
          for (int i=0; i<stars; i++) plen[i] = lengths[maps[i]];

          int[] lengths = trans.getLengths();
          for (int i=0; i<numFiles; i++) {
            int[] pos = MathUtil.rasterToPosition(plen, i);
            int[] npos = new int[lengths.length];
            for (int j=0; j<stars; j++) npos[maps[j]] = pos[j];

            // construct filename
            StringBuffer sb = new StringBuffer();
            for (int j=0; j<stars; j++) {
              sb.append(tokens[j]);
              if (padZeroes) {
                int len = ("" + lengths[maps[j]]).length() -
                  ("" + (pos[j] + 1)).length();
                for (int k=0; k<len; k++) sb.append("0");
              }
              sb.append(pos[j] + 1);
            }
            sb.append(tokens[stars]);
            String filename = sb.toString();

            // construct data object
            if (excl < 0) {
              task.setStatus(count++, max,
                "Reading #" + (i + 1) + "/" + numTotal);
              FlatField ff = (FlatField) trans.getData(npos, 2, null);
              Image image = ff instanceof ImageFlatField ?
                ((ImageFlatField) ff).getImage() :
                DataUtility.extractImage(ff, false);
              task.setStatus(count++, max,
                "Writing #" + (i + 1) + "/" + numTotal);
              if (tiffSaver == null) saver.save(filename, image, true);
              else {
                // save image to TIFF file
                Hashtable ifd = new Hashtable();
                TiffTools.putIFDValue(ifd,
                  TiffTools.SOFTWARE, VisBio.TITLE + " " + VisBio.VERSION);
                if (doLZW) {
                  TiffTools.putIFDValue(ifd,
                    TiffTools.COMPRESSION, TiffTools.LZW);
                  // do horizontal differencing
                  TiffTools.putIFDValue(ifd, TiffTools.PREDICTOR, 2);
                }
                tiffSaver.saveImage(filename, image, ifd, true);
              }
            }
            else {
              for (int j=0; j<lengths[excl]; j++) {
                int img = numImages * i + j + 1;
                task.setStatus(count++, max,
                  "Reading #" + img + "/" + numTotal);
                npos[excl] = j;
                FlatField ff = (FlatField) trans.getData(npos, 2, null);
                Image image = ff instanceof ImageFlatField ?
                  ((ImageFlatField) ff).getImage() :
                  DataUtility.extractImage(ff, false);
                task.setStatus(count++, max,
                  "Writing #" + img + "/" + numTotal);
                boolean last = j == lengths[excl] - 1;
                if (tiffSaver == null) saver.save(filename, image, last);
                else {
                  // save image to TIFF file
                  Hashtable ifd = new Hashtable();
                  TiffTools.putIFDValue(ifd,
                    TiffTools.SOFTWARE, VisBio.TITLE + " " + VisBio.VERSION);
                  if (doLZW) {
                    TiffTools.putIFDValue(ifd,
                      TiffTools.COMPRESSION, TiffTools.LZW);
                    // do horizontal differencing
                    TiffTools.putIFDValue(ifd, TiffTools.PREDICTOR, 2);
                  }
                  tiffSaver.saveImage(filename, image, ifd, last);
                }
              }
            }
          }
          task.setCompleted();
        }
        catch (FormatException exc) {
          exc.printStackTrace();
          JOptionPane.showMessageDialog(dialog,
            "Error exporting data: " + exc.getMessage(),
            "VisBio", JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException exc) {
          exc.printStackTrace();
          JOptionPane.showMessageDialog(dialog,
            "Error exporting data: " + exc.getMessage(),
            "VisBio", JOptionPane.ERROR_MESSAGE);
        }
      }
    }.start();
  }


  // -- DialogPane API methods --

  /** Resets the wizard pane's components to their default states. */
  public void resetComponents() {
    super.resetComponents();
    patternField.setText("");
    formatBox.setSelectedIndex(0);
  }


  // -- ActionListener API methods --

  /** Handles button press events. */
  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (command.equals("next")) {
      if (page == 0) { // lay out page 2
        // ensure file pattern ends with appropriate format extension
        // also determine visibility of "frames per second" text field
        String pattern = folderField.getText() +
          File.separator + patternField.getText();
        String format = (String) formatBox.getSelectedItem();
        String plow = pattern.toLowerCase();
        boolean doFPS = false, doLZW = false;
        if (format.equals("PIC")) {
          if (!plow.endsWith(".pic")) pattern += ".pic";
        }
        else if (format.equals("TIFF")) {
          if (!plow.endsWith(".tiff") && !plow.endsWith(".tif")) {
            pattern += ".tif";
            doLZW = true;
          }
        }
        else if (format.equals("MOV")) {
          if (!plow.endsWith(".mov")) pattern += ".mov";
          doFPS = true;
        }

        // parse file pattern
        StringTokenizer st = new StringTokenizer("#" + pattern + "#", "*");
        tokens = new String[st.countTokens()];
        for (int i=0; i<tokens.length; i++) {
          String t = st.nextToken();
          if (i == 0) t = t.substring(1);
          if (i == tokens.length - 1) t = t.substring(0, t.length() - 1);
          tokens[i] = t;
        }
        stars = tokens.length - 1;
        String[] dims = trans.getDimTypes();
        int q = dims.length - stars;
        if (q < 0 || q > 1) {
          String msg;
          if (dims.length == 0) {
            msg = "Please specify a single filename (no asterisks).";
          }
          else if (dims.length == 1) {
            msg = "Please specify either a single filename (no asterisks) " +
              "or a file pattern with\none asterisk to indicate where the " +
              "dimensional axes should be numbered.";
          }
          else {
            msg = "Please specify either " + (dims.length - 1) + " or " +
              dims.length + " asterisks in the file pattern to\nindicate " +
              "where the dimensional axes should be numbered.";
          }
          JOptionPane.showMessageDialog(dialog, msg, "VisBio",
            JOptionPane.ERROR_MESSAGE);
          return;
        }

        // determine visibility of "leading zeroes" checkbox
        boolean doZeroes = stars > 0;

        // build file pattern
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<stars; i++) {
          sb.append(tokens[i]);
          sb.append("<");
          sb.append((char) ('A' + i));
          sb.append(">");
        }
        sb.append(tokens[stars]);
        String pat = sb.toString();

        // build list of dimensional axes
        String[] dimChoices = new String[dims.length];
        for (int i=0; i<dims.length; i++) {
          dimChoices[i] = "<" + (i + 1) + "> " + dims[i];
        }

        // construct second page panel
        sb = new StringBuffer("pref");
        if (doFPS) sb.append(", 3dlu, pref");
        if (doLZW) sb.append(", 3dlu, pref");
        if (doZeroes) sb.append(", 3dlu, pref");
        for (int i=0; i<stars; i++) {
          sb.append(", 3dlu, pref");
        }
        PanelBuilder builder = new PanelBuilder(new FormLayout(
          "pref:grow, 3dlu, pref", sb.toString()));
        CellConstraints cc = new CellConstraints();
        builder.addSeparator(pat, cc.xyw(1, 1, 3));
        int row = 3;
        if (doFPS) {
          builder.addLabel("&Frames per second",
            cc.xy(1, row, "right,center")).setLabelFor(fps);
          builder.add(fps, cc.xy(3, row));
          row += 2;
        }
        if (doLZW) {
          builder.add(lzw, cc.xyw(1, row, 3, "right, center"));
          row += 2;
        }
        if (doZeroes) {
          builder.add(leadingZeroes, cc.xyw(1, row, 3, "right,center"));
          row += 2;
        }
        letterBoxes = new BioComboBox[stars];
        for (int i=0; i<stars; i++) {
          char letter = (char) ('A' + i);
          builder.addLabel("<" + letter + "> =",
            cc.xy(1, row, "right,center"));
          letterBoxes[i] = new BioComboBox(dimChoices);
          letterBoxes[i].setSelectedIndex(i);
          builder.add(letterBoxes[i], cc.xy(3, row));
          row += 2;
        }
        second.removeAll();
        second.add(builder.getPanel());
      }
      else if (page == 1) { // lay out page 3
        String[] dims = trans.getDimTypes();
        int[] lengths = trans.getLengths();

        // file pattern
        StringBuffer sb = new StringBuffer("File pattern: ");
        for (int i=0; i<stars; i++) {
          sb.append(tokens[i]);
          char letter = (char) ('A' + i);
          sb.append("<");
          sb.append(letter);
          sb.append(">");
        }
        sb.append(tokens[stars]);

        // file format
        sb.append("\nFormat: ");
        String format = (String) formatBox.getSelectedItem();
        if (format.equals("PIC")) sb.append("Bio-Rad PIC file");
        else if (format.equals("TIFF")) {
          if (dims.length == stars) sb.append("TIFF image");
          else sb.append("Multi-page TIFF stack");
          if (lzw.isSelected()) sb.append(" (LZW)");
        }
        else if (format.equals("MOV")) sb.append("QuickTime movie");
        else sb.append("Unknown");
        sb.append("\n \n");

        // dimensional mappings
        maps = new int[stars];
        if (stars > 0) {
          for (int i=0; i<stars; i++) {
            char letter = (char) ('A' + i);
            sb.append("<");
            sb.append(letter);
            sb.append("> numbered across dimension: ");
            maps[i] = letterBoxes[i].getSelectedIndex();
            sb.append("<");
            sb.append(maps[i] + 1);
            sb.append("> ");
            sb.append(dims[maps[i]]);
            sb.append("\n");
          }
        }
        else sb.append(" \n");

        // count dimensional axis exclusions
        excl = -1;
        boolean[] b = new boolean[dims.length];
        for (int i=0; i<stars; i++) b[maps[i]] = true;
        int exclude = 0;
        for (int i=0; i<dims.length; i++) {
          if (!b[i]) {
            excl = i;
            exclude++;
          }
        }

        // file contents
        sb.append("Each file will contain ");
        int q = dims.length - stars;
        if (q == 0) sb.append("a single image.\n \n");
        else { // q == 1
          int len = lengths[excl];
          sb.append(len);
          sb.append(" image");
          if (len != 1) sb.append("s");
          sb.append(" across dimension: <");
          sb.append(excl + 1);
          sb.append("> ");
          sb.append(dims[excl]);
          sb.append("\n \n");
        }

        // verify dimensional axis mappings are acceptable
        if (exclude != q) {
          JOptionPane.showMessageDialog(dialog,
            "Please map each letter to a different dimensional axis.",
            "VisBio", JOptionPane.ERROR_MESSAGE);
          return;
        }

        // verify number of range components is acceptable
        int rangeCount = trans.getRangeCount();
        if (format.equals("PIC") && rangeCount != 1) {
          JOptionPane.showMessageDialog(dialog,
            "Bio-Rad PIC format requires a data object with a single " +
            "range component. Please subsample your data object accordingly.",
            "VisBio", JOptionPane.ERROR_MESSAGE);
          return;
        }
        else if (rangeCount != 1 && rangeCount != 3) {
          JOptionPane.showMessageDialog(dialog,
            format + " format requires a data object with either 1 or 3 " +
            "range components. Please subsample your data object accordingly.",
            "VisBio", JOptionPane.ERROR_MESSAGE);
          return;
        }

        // number of files
        boolean padZeroes = leadingZeroes.isSelected();
        sb.append("\nFirst file: ");
        for (int i=0; i<stars; i++) {
          sb.append(tokens[i]);
          if (padZeroes) {
            int len = ("" + lengths[maps[i]]).length() - 1;
            for (int j=0; j<len; j++) sb.append("0");
          }
          sb.append("1");
        }
        sb.append(tokens[stars]);
        sb.append("\nLast file: ");
        numFiles = 1;
        for (int i=0; i<stars; i++) {
          sb.append(tokens[i]);
          sb.append(lengths[maps[i]]);
          numFiles *= lengths[maps[i]];
        }
        sb.append(tokens[stars]);
        sb.append("\nTotal number of files: ");
        sb.append(numFiles);

        // construct third page panel
        StringTokenizer st = new StringTokenizer(sb.toString(), "\n");
        int numLines = st.countTokens();
        sb = new StringBuffer("pref, 3dlu");
        for (int i=0; i<numLines; i++) sb.append(", pref");
        sb.append(", 5dlu");
        PanelBuilder builder = new PanelBuilder(new FormLayout(
          "pref:grow", sb.toString()));
        CellConstraints cc = new CellConstraints();
        builder.addSeparator("Summary", cc.xy(1, 1));
        for (int i=0; i<numLines; i++) {
          builder.addLabel(st.nextToken(), cc.xy(1, i + 3));
        }
        third.removeAll();
        third.add(builder.getPanel());
      }

      super.actionPerformed(e);
    }
    else if (command.equals("folder")) {
      // pop up folder chooser
      File dir = new File(folderField.getText());
      JFileChooser chooser = dir.isDirectory() ?
        new JFileChooser(dir) : new JFileChooser();
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        dir = chooser.getSelectedFile();
        if (dir != null) folderField.setText(dir.getPath());
      }
    }
    else super.actionPerformed(e);
  }

}
