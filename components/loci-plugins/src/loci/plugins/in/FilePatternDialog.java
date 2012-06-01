/*
 * #%L
 * LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2012 Open Microscopy Environment:
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

import java.io.File;
import java.math.BigInteger;

import ij.IJ;
import ij.gui.GenericDialog;

import loci.common.Location;
import loci.formats.FilePattern;
import loci.formats.FilePatternBlock;

/**
 * Bio-Formats Importer file pattern dialog box.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/loci-plugins/src/loci/plugins/in/FilePatternDialog.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/loci-plugins/src/loci/plugins/in/FilePatternDialog.java;hb=HEAD">Gitweb</a></dd></dl>
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
    GenericDialog gd = new GenericDialog("Bio-Formats File Stitching");
    int len = id.length() + 1;
    if (len > 80) len = 80;

    originalID = id;
    fp = new FilePattern(id);

    String[] prefixes = fp.getPrefixes();
    int[] counts = fp.getCount();
    paddingZeros = new int[counts.length];
    String[][] elements = fp.getElements();

    for (int i=0; i<prefixes.length; i++) {
      String prefix = "Axis_" + (i + 1);
      gd.addStringField(prefix + "_number_of_images", "" + counts[i]);
      gd.addStringField(prefix + "_axis_first_image", "1");
      gd.addStringField(prefix + "_axis_increment", "1");
      gd.addMessage("");

      try {
        paddingZeros[i] = elements[i][0].length() -
          String.valueOf(Integer.parseInt(elements[i][0])).length();
      }
      catch (NumberFormatException e) { }
    }

    gd.addStringField("File name contains:", "");
    gd.addStringField("Pattern: ", id, len);

    return gd;
  }

  @Override
  protected boolean harvestResults(GenericDialog gd) {
    String[] counts = new String[fp.getPrefixes().length];
    String[] firsts = new String[counts.length];
    String[] increments = new String[counts.length];
    int[] count = fp.getCount();

    boolean changedAxes = false;

    for (int i=0; i<counts.length; i++) {
      counts[i] = gd.getNextString();
      firsts[i] = gd.getNextString();
      increments[i] = gd.getNextString();

      if (!firsts[i].equals("1") || !increments[i].equals("1") ||
        Integer.parseInt(counts[i]) != count[i])
      {
        changedAxes = true;
      }
    }

    String contains = gd.getNextString();
    String id = gd.getNextString();

    if (!changedAxes) {
      if (contains.trim().length() > 0) {
        String dir =
          originalID.substring(0, originalID.lastIndexOf(File.separator) + 1);
        id = dir + ".*" + contains + ".*";
      }
    }
    else {
      String pattern =
        originalID.substring(0, originalID.lastIndexOf(File.separator) + 1);
      for (int i=0; i<counts.length; i++) {
        BigInteger first = new BigInteger(firsts[i]);
        BigInteger fileCount = new BigInteger(counts[i]);
        BigInteger increment = new BigInteger(increments[i]);

        FilePatternBlock block = new FilePatternBlock(fp.getBlock(i));

        first = first.add(block.getFirst()).subtract(BigInteger.ONE);
        fileCount = fileCount.multiply(increment).add(first);

        pattern += fp.getPrefix(i);
        pattern += "<";
        int firstPadding = paddingZeros[i] - first.toString().length() + 1;
        for (int zero=0; zero<firstPadding; zero++) {
          pattern += "0";
        }
        pattern += first;
        pattern += "-";
        fileCount = fileCount.subtract(BigInteger.ONE);
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

}
