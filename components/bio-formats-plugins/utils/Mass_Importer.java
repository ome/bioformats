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

import ij.IJ;
import ij.gui.YesNoCancelDialog;
import ij.io.DirectoryChooser;
import ij.plugin.PlugIn;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import loci.formats.FilePattern;
import loci.formats.ImageReader;
import loci.plugins.LociImporter;

/**
 * Processes all image files in the chosen directory,
 * grouping files with similar names.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats-plugins/utils/Mass_Importer.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats-plugins/utils/Mass_Importer.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class Mass_Importer implements PlugIn {

  public void run(String arg) {
    // prompt user for directory to process
    DirectoryChooser dc = new DirectoryChooser("Bio-Formats Mass Importer");
    String dirPath = dc.getDirectory();

    // create a list of files we have already processed
    HashSet<String> done = new HashSet<String>();

    // image reader object, for testing whether a file is in a supported format
    ImageReader tester = new ImageReader();

    // list of files to actually open with Bio-Formats Importer
    ArrayList<String> filesToOpen = new ArrayList<String>();

    // process all files in the chosen directory
    File dir = new File(dirPath);
    File[] files = dir.listFiles();
    IJ.showStatus("Scanning directory");
    for (int i=0; i<files.length; i++) {
      String id = files[i].getAbsolutePath();
      IJ.showProgress((double) i / files.length);

      // skip files that have already been processed
      if (done.contains(id)) continue;

      // skip unsupported files
      if (!tester.isThisType(id, false)) continue;

      // use FilePattern to group files with similar names
      String name = files[i].getName();
      FilePattern fp = new FilePattern(name, dirPath);

      // get a list of all files part of this group, and mark them as done
      String[] used = fp.getFiles();
      for (int j=0; j<used.length; j++) done.add(used[j]);

      filesToOpen.add(id);
    }
    IJ.showProgress(1.0);
    IJ.showStatus("");

    // confirm that user wants to proceed in opening the file groups
    int numToOpen = filesToOpen.size();
    if (numToOpen == 0) {
      IJ.showMessage("No file groups found.");
      return;
    }
    String groups = numToOpen == 1 ?
      "1 file group" : (numToOpen + " file groups");
    YesNoCancelDialog confirm = new YesNoCancelDialog(IJ.getInstance(),
      "Bio-Formats Mass Importer", "Found " + groups + " in directory '" +
      dirPath + "'; proceed?");
    if (!confirm.yesPressed()) return;

    // launch the Bio-Formats Importer plugin to open each group of files
    for (int i=0; i<numToOpen; i++) {
      String id = (String) filesToOpen.get(i);
      String params =
        "location=[Local machine] " +
        "windowless=true " +
        "groupFiles=true " +
        "id=[" + id + "] ";
      new LociImporter().run(params);
    }
    IJ.showStatus("");
  }

}
