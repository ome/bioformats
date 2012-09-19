/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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

package loci.formats.tools;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import loci.common.Constants;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;

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

  private static final int MAX_LENGTH = 90;

  private static final String LINE_SEPARATOR = "+-------------------------------+--------------------------+----------------------------+";

  private PrintStream out;

  private int nameWidth = 0;
  private int extensionWidth = 0;

  /** Write the table header. */
  private void printHeader() {
    out.print("This table shows the extension of the file that you should ");
    out.println("choose if you want");
    out.println("to open/import a dataset in a particular format.");
    out.println();
    out.println(LINE_SEPARATOR);

    String nameHeader = "| **Format name**               ";
    nameWidth = nameHeader.length() - 1;
    out.print(nameHeader);

    String extensionHeader = "| **File to choose**       ";
    extensionWidth = extensionHeader.length();
    out.print(extensionHeader);

    out.println("| **Structure of files**     |");
    out.println(LINE_SEPARATOR.replaceAll("-", "="));
  }

  /** Write the table footer. */
  private void printFooter() {
    out.println();
    out.println("Flex Support");
    out.println("------------");
    out.println();
    out.println("OMERO.importer supports importing analysized Flex files from an Opera");
    out.println("system.");
    out.println();
    out.println("Basic configuration is done via the ``importer.ini``. Once the user has");
    out.println("run the Importer once, this file will be in the following location:");
    out.println();
    out.println("-  ``C:\\Documents and Settings\\<username>\\omero\\importer.ini``");
    out.println();
    out.println("The user will need to modify or add the ``[FlexReaderServerMaps]``");
    out.println("section of the INI file as follows:");
    out.println();
    out.println("::");
    out.println();
    out.println("    ...");
    out.println("    [FlexReaderServerMaps]");
    out.println("    CIA-1 = \\\\\\\\hostname1\\\\mount;\\\\\\\\archivehost1\\\\mount");
    out.println("    CIA-2 = \\\\\\\\hostname2\\\\mount;\\\\\\\\archivehost2\\\\mount");
    out.println();
    out.println("where the *key* of the INI file line is the value of the \"Host\" tag in");
    out.println("the ``.mea`` measurement XML file (here: ``<Host name=\"CIA-1\">``) and");
    out.println("the value is a semicolon-separated list of *escaped* UNC path names to");
    out.println("the Opera workstations where the Flex files reside.");
    out.println();
    out.println("Once this resolution has been encoded in the configuration file **and**");
    out.println("you have restarted the importer, you will be able to select the ``.mea``");
    out.println("measurement XML file from the Importer user interface as the import");
    out.println("target.");
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

    // determine if we need to wrap the middle column onto multiple lines

    ArrayList<String> extensionLines = new ArrayList<String>();
    if (extension.length() > extensionWidth - 2) {
      while (extension.length() > 0) {
        if (extension.length() > extensionWidth - 2) {
          int lastSpace = extension.lastIndexOf(" ", extensionWidth - 2);

          String ext = extension.substring(0, lastSpace);
          while (ext.length() < extensionWidth - 2) {
            ext += " ";
          }

          extensionLines.add(ext);
          extension = extension.substring(lastSpace + 1);
        }
        else {
          while (extension.length() < extensionWidth - 2) {
            extension += " ";
          }

          extensionLines.add(extension);
          extension = "";
        }
      }
    }
    else {
      extensionLines.add(extension);
    }

    // determine if we need to wrap the last column onto multiple lines

    int lineLength = format.length() + extensionLines.get(0).length() + 8;

    ArrayList<String> descriptionLines = new ArrayList<String>();
    if (lineLength + description.length() > MAX_LENGTH) {
      int maxDescriptionLength = MAX_LENGTH - lineLength;

      while (description.length() > 0) {
        if (description.length() > maxDescriptionLength) {
          int lastSpace = description.lastIndexOf(" ", maxDescriptionLength - 1);
          boolean addHyphen = false;
          if (lastSpace < 0) {
            lastSpace = maxDescriptionLength - 1;
            addHyphen = true;
          }
          descriptionLines.add(
            description.substring(0, lastSpace) + (addHyphen ? "-" : ""));
          description = description.substring(lastSpace + (addHyphen ? 0 : 1));
        }
        else {
          descriptionLines.add(description);
          description = "";
        }
      }
    }
    else {
      descriptionLines.add(description);
    }

    int numSpaces = MAX_LENGTH - format.length() - extensionLines.get(0).length() - 7;

    out.print("|");
    out.print(format);
    out.print("| ");
    out.print(extensionLines.get(0));
    out.print("| ");
    out.print(descriptionLines.get(0));
    for (int i=0; i<numSpaces-descriptionLines.get(0).length(); i++) {
      out.print(" ");
    }
    out.println("|");

    int lineCount =
      (int) Math.max(extensionLines.size(), descriptionLines.size());
    for (int i=1; i<lineCount; i++) {
      out.print("|");
      out.print(format.replaceAll(".", " "));
      out.print("| ");
      if (i < extensionLines.size()) {
        out.print(extensionLines.get(i));
      }
      else {
        out.print(extensionLines.get(0).replaceAll(".", " "));
      }
      out.print("| ");
      int len = 0;
      if (i < descriptionLines.size()) {
        out.print(descriptionLines.get(i));
        len = descriptionLines.get(i).length();
      }
      for (int q=0; q<numSpaces-len; q++) {
        out.print(" ");
      }
      out.println("|");
    }

    out.println(LINE_SEPARATOR);
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
      printFormatEntry(reader);
    }

    printFooter();
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
