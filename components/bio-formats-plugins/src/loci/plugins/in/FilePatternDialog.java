/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.plugins.in;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JPanel;

import ij.IJ;
import ij.gui.GenericDialog;

import loci.common.Location;
import loci.formats.FilePattern;
import loci.formats.FilePatternBlock;

/**
 * Bio-Formats Importer file pattern dialog box.
 */
public class FilePatternDialog extends ImporterDialog {

  // -- Fields --

  private FilePattern fp;
  private String originalID;
  private int[] paddingZeros;

  // -- Constructor --

  /** Creates a file pattern dialog for the Bio-Formats Importer. */
  public FilePatternDialog(ImportProcess process) {
    super(process);
  }

  // -- ImporterDialog methods --

  @Override
  protected boolean needPrompt() {
    return !process.isWindowless() && options.isGroupFiles();
  }

  @Override
  protected GenericDialog constructDialog() {
    // CTR - CHECK
    Location idLoc = new Location(options.getId());
    String id = FilePattern.findPattern(idLoc);
    if (id == null) {
      if (!options.isQuiet()) {
        IJ.showMessage("Bio-Formats",
          "Warning: Bio-Formats was unable to determine a grouping that\n" +
          "includes the file you chose. The most common reason for this\n" +
          "situation is that the folder contains extraneous files with\n" +
          "similar names and numbers that confuse the detection algorithm.\n" +
          " \n" +
          "For example, if you have multiple datasets in the same folder\n" +
          "named series1_z*_c*.tif, series2_z*_c*.tif, etc., Bio-Formats\n" +
          "may try to group all such files into a single series.\n" +
          " \n" +
          "For best results, put each image series's files in their own\n" +
          "folder, or type in a file pattern manually.\n");
      }
      id = idLoc.getAbsolutePath();
    }

    // construct dialog
    GenericDialog gd = new GenericDialog("Bio-Formats File Stitching") {
      @Override
      public void itemStateChanged(ItemEvent e) {
        super.itemStateChanged(e);

        Object source = e.getSource();

        if (!(source instanceof Checkbox)) {
          return;
        }

        boolean selected = e.getStateChange() == ItemEvent.SELECTED;

        Vector checkboxes = getCheckboxes();

        for (Object checkbox : checkboxes) {
          if (!checkbox.equals(source)) {
            if (selected) {
              ((Checkbox) checkbox).setState(false);
            }
          }
          else if (!selected && checkbox.equals(source)) {
            ((Checkbox) checkbox).setState(true);
          }
        }
      }
    };
    gd.addMessage(
      "The list of files to be grouped can be specified in one of the following ways:");

    // option one

    int len = id.length() + 1;
    if (len > 80) len = 80;

    originalID = id;
    fp = new FilePattern(id);

    String[] prefixes = fp.getPrefixes();

    if (prefixes.length > 0) {
      gd.addCheckbox("Dimensions", true);

      int[] counts = fp.getCount();
      paddingZeros = new int[counts.length];
      String[][] elements = fp.getElements();

      BigInteger[] first = fp.getFirst();
      BigInteger[] step = fp.getStep();

      for (int i=0; i<prefixes.length; i++) {
        String prefix = "Axis_" + (i + 1);
        gd.addStringField(prefix + "_number_of_images", "" + counts[i]);
        gd.addStringField(prefix + "_axis_first_image", first[i].toString());
        gd.addStringField(prefix + "_axis_increment", step[i].toString());

        try {
          paddingZeros[i] = elements[i][0].length() -
            String.valueOf(Integer.parseInt(elements[i][0])).length();
        }
        catch (NumberFormatException e) { }
      }
    }

    // option two

    gd.addCheckbox("File_name", false);
    gd.addStringField("contains", "");

    // option three

    gd.addCheckbox("Pattern", false);
    gd.addStringField("name", id, len);

    rebuild(gd);

    return gd;
  }

  @Override
  protected boolean harvestResults(GenericDialog gd) {
    String[] counts = new String[fp.getPrefixes().length];
    String[] firsts = new String[counts.length];
    String[] increments = new String[counts.length];
    int[] count = fp.getCount();

    boolean useRanges = counts.length > 0 ? gd.getNextBoolean() : false;
    boolean useRegex = gd.getNextBoolean();

    for (int i=0; i<counts.length; i++) {
      counts[i] = gd.getNextString();
      firsts[i] = gd.getNextString();
      increments[i] = gd.getNextString();
    }

    String contains = gd.getNextString();
    String id = gd.getNextString();

    if (useRegex) {
      if (contains.trim().length() > 0) {
        String dir =
          originalID.substring(0, originalID.lastIndexOf(File.separator) + 1);
        id = dir + ".*" + contains + ".*";
      }
    }
    else if (useRanges) {
      String pattern =
        originalID.substring(0, originalID.lastIndexOf(File.separator) + 1);
      for (int i=0; i<counts.length; i++) {
        BigInteger first = new BigInteger(firsts[i]);
        BigInteger fileCount = new BigInteger(counts[i]);
        BigInteger increment = new BigInteger(increments[i]);

        FilePatternBlock block = new FilePatternBlock(fp.getBlock(i));

        fileCount = fileCount.subtract(BigInteger.ONE).multiply(increment);

        pattern += fp.getPrefix(i);
        pattern += "<";
        int firstPadding = paddingZeros[i] - first.toString().length() + 1;
        for (int zero=0; zero<firstPadding; zero++) {
          pattern += "0";
        }
        pattern += first;
        pattern += "-";
        int lastPadding = paddingZeros[i] - fileCount.toString().length() + 1;
        for (int zero=0; zero<lastPadding; zero++) {
          pattern += "0";
        }
        pattern += fileCount;
        pattern += ":";
        pattern += increment;
        pattern += ">";
      }
      id = pattern + fp.getSuffix();
    }

    options.setId(id);
    return true;
  }

  private void rebuild(GenericDialog gd) {
    // rebuild dialog to organize things more nicely

    Vector checkboxes = gd.getCheckboxes();
    Vector fields = gd.getStringFields();
    final ArrayList<Label> labels = new ArrayList<Label>();

    for (Component c : gd.getComponents()) {
      if (c instanceof Label) {
        labels.add((Label) c);
      }
    }

    final String cols = "pref, 3dlu, pref, 3dlu, pref";

    final StringBuilder sb = new StringBuilder("pref, 3dlu, pref");
    for (int s=1; s<fields.size(); s++) {
      sb.append(", 3dlu, pref");
    }
    final String rows = sb.toString();

    final PanelBuilder builder = new PanelBuilder(new FormLayout(cols, rows));
    final CellConstraints cc = new CellConstraints();

    int row = 1;

    if (checkboxes.size() > 2 && fields.size() > 2) {
      builder.add((Component) labels.get(0), cc.xyw(1, row, 5));
      row += 2;
      builder.add((Component) checkboxes.get(0), cc.xy(1, row));

      for (int i=0; i<fields.size()-2; i++) {
        builder.add((Component) labels.get(i + 1), cc.xy(3, row));
        builder.add((Component) fields.get(i), cc.xy(5, row));
        row += 2;
      }
    }

    builder.add((Component) checkboxes.get(checkboxes.size() - 2), cc.xy(1, row));
    builder.add((Component) labels.get(labels.size() - 2), cc.xy(3, row));
    builder.add((Component) fields.get(fields.size() - 2), cc.xy(5, row));
    row += 2;

    builder.add((Component) checkboxes.get(checkboxes.size() - 1), cc.xy(1, row));
    builder.add((Component) labels.get(labels.size() - 1), cc.xy(3, row));
    builder.add((Component) fields.get(fields.size() - 1), cc.xy(5, row));
    row += 2;

    final JPanel masterPanel = builder.getPanel();

    gd.removeAll();

    GridBagLayout gdl = (GridBagLayout) gd.getLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 3;
    gbc.gridheight = fields.size();
    gdl.setConstraints(masterPanel, gbc);

    gd.add(masterPanel);

    gd.setBackground(Color.white); // HACK: workaround for JPanel in a Dialog
  }

}
