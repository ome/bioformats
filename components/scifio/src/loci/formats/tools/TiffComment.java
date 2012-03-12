//
// TiffComment.java
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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;

import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.UpgradeChecker;
import loci.formats.tiff.TiffParser;
import loci.formats.tiff.TiffSaver;

/**
 * Extracts the comment from the first IFD of the given TIFF file(s).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/tools/TiffComment.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/tools/TiffComment.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class TiffComment {

  public static void main(String[] args) throws FormatException, IOException {
    UpgradeChecker checker = new UpgradeChecker();
    boolean canUpgrade = checker.newVersionAvailable();
    if (canUpgrade) {
      System.out.println("*** A new stable version is available. ***");
      System.out.println("*** Install the new version using:     ***");
      System.out.println("***   'upgradechecker -install'        ***");
    }

    if (args.length == 0) {
      System.out.println("Usage:");
      System.out.println(
        "tiffcomment [-set comment] [-edit] file1 [file2 ...]");
      System.out.println();
      System.out.println("If using the '-set' option, the new TIFF comment " +
        "must be specified.");
      System.out.println("The commment may take any of the following forms:");
      System.out.println();
      System.out.println("  * the text of the comment, e.g. 'new comment!'");
      System.out.println("  * the name of the file containing the text of " +
        "the comment, e.g. 'file.xml'");
      System.out.println("  * '-', to enter the comment using stdin.  " +
        "Entering a blank line will");
      System.out.println("    terminate reading from stdin.");
      return;
    }

    // parse flags
    boolean edit = false;
    String newComment = null;
    ArrayList<String> files = new ArrayList<String>();
    for (int i=0; i<args.length; i++) {
      if (!args[i].startsWith("-")) {
        files.add(args[i]);
        continue;
      }

      if (args[i].equals("-edit")) edit = true;
      else if (args[i].equals("-set")) {
        newComment = args[++i];
        if (new File(newComment).exists()) {
          newComment = DataTools.readFile(newComment);
        }
        else if (newComment.equals("-")) {
          newComment = null;
          BufferedReader reader =
            new BufferedReader(new InputStreamReader(System.in));
          String line = reader.readLine();
          while (line != null && line.length() > 0) {
            if (newComment == null) newComment = line;
            else {
              newComment += "\n" + line;
            }
            line = reader.readLine();
          }
        }
      }
      else System.out.println("Warning: unknown flag: " + args[i]);
    }

    // process files
    for (String file : files) {
      if (edit) EditTiffG.openFile(file);
      else if (newComment != null) {
        RandomAccessInputStream in = new RandomAccessInputStream(file);
        RandomAccessOutputStream out = new RandomAccessOutputStream(file);
        TiffSaver saver = new TiffSaver(out, file);
        saver.overwriteComment(in, newComment);
        in.close();
        out.close();
      }
      else {
        String comment = new TiffParser(file).getComment();
        System.out.println(comment == null ?
          file + ": no TIFF comment found." : comment);
      }
    }
  }

}
