//
// MakeDatasetStructureTable.java
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

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;

import loci.common.Constants;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.in.ZeissCZIReader;

/**
 * Utility class for generating a table containing the dataset structure for
 * every supported format.
 * This class is used to generate this wiki page:
 * http://trac.openmicroscopy.org.uk/ome/wiki/BioFormats-DatasetStructureTable
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/tools/MakeDatasetStructureTable.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/tools/MakeDatasetStructureTable.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class MakeDatasetStructureTable {

  private PrintStream out;

  private int nameWidth = 0;
  private int extensionWidth = 0;

  /** Write the table header. */
  private void printHeader() {
    out.print("This table shows the extension of the file that you should ");
    out.println("choose if you want");
    out.println("to open/import a dataset in a particular format.");
    out.println();
    out.println();

    String nameHeader = "||= '''Format name'''                   =";
    nameWidth = nameHeader.length() - 2;
    out.print(nameHeader);

    String extensionHeader = "||=  '''File to choose'''             =";
    extensionWidth = extensionHeader.length() - 2;
    out.print(extensionHeader);

    out.println("||=  '''Structure of files'''                         =||");
  }

  /** Write a line containing all of the columns for the specified reader. */
  private void printFormatEntry(IFormatReader reader) {
    String format = reader.getFormat();

    String[] suffixes = reader.getSuffixes();
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<suffixes.length; i++) {
      if (suffixes[i].trim().length() > 0) {
        sb.append(".");
        sb.append(suffixes[i]);
      }
      else {
        sb.append("(no extension)");
      }
      if (i != suffixes.length - 1) {
        sb.append(", ");
      }
    }

    String extension = sb.toString();
    String description = reader.getDatasetStructureDescription();

    while (format.length() < nameWidth) {
      format += " ";
    }
    while (extension.length() < extensionWidth - 2) {
      extension += " ";
    }

    out.print("||");
    out.print(format);
    out.print("||  ");
    out.print(extension);
    out.print("||  ");
    out.print(description);
    out.println();
  }

  /** Write the table to the file specified using setOutputFile(String[]) */
  public void printTable() {
    ImageReader baseReader = new ImageReader();
    IFormatReader[] allReaders = baseReader.getReaders();

    Comparator<IFormatReader> comparator = new Comparator<IFormatReader>() {
      public int compare(IFormatReader r1, IFormatReader r2) {
        String s1 = r1.getFormat();
        String s2 = r2.getFormat();

        return s1.compareTo(s2);
      }
    };

    Arrays.sort(allReaders, comparator);

    printHeader();

    for (IFormatReader reader : allReaders) {
      if (!(reader instanceof ZeissCZIReader)) {
        printFormatEntry(reader);
      }
    }
  }

  /**
   * Set the output file.
   * The output file will be the first element in 'args', or stdout if
   * 'args' has length 0.
   */
  public void setOutputFile(String[] args) throws IOException {
    if (args.length == 0) {
      out = System.out;
    }
    else {
      out = new PrintStream(args[0], Constants.ENCODING);
    }
  }

  /** Close the output file. */
  public void closeFile() throws IOException {
    out.close();
  }

  // -- Main method --

  public static void main(String[] args) throws IOException {
    MakeDatasetStructureTable table = new MakeDatasetStructureTable();
    table.setOutputFile(args);
    table.printTable();
    table.closeFile();
  }

}
