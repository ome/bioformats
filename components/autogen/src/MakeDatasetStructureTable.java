/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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


import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;

import loci.common.Constants;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;

/**
 * Utility class for generating a table containing the dataset structure for
 * every supported format.
 * This class is used to generate this wiki page:
 * http://www.openmicroscopy.org/site/support/bio-formats5.1/formats/dataset-table.html
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class MakeDatasetStructureTable {

  private PrintStream out;

  /** Write the table header. */
  private void printHeader() {
    out.println(".. Please don't even think about editing this file directly.");
    out.println(".. It is generated using the 'gen-structure-table' Ant");
    out.println(".. target in components/autogen, which uses");
    out.println(".. loci.formats.tools.MakeDatasetStructureTable, so please");
    out.println(".. update that instead.");
    out.println();
    out.println("Dataset Structure Table");
    out.println("=======================");
    out.println();
    out.print("This table shows the extension of the file that you should ");
    out.println("choose if you want");
    out.println("to open/import a dataset in a particular format.");
    out.println();
    out.println("You can sort this table by clicking on any of the headings.");
    out.println();
    out.println(".. list-table::");
    out.println("   :class: sortable");
    out.println("   :header-rows: 1");
    out.println();
    out.println("   * - Format name");
    out.println("     - File to choose");
    out.println("     - Structure of files");
  }

  /** Write the table footer. */
  private void printFooter() {
    out.println();
    out.println("Flex Support");
    out.println("------------");
    out.println();
    out.println("OMERO.importer supports importing analyzed Flex files from an Opera");
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
    out.println("    …");
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

    out.println("   * - " + format);
    out.println("     - " + extension);
    out.println("     - " + description);
  }

  /** Write the table to the file specified using setOutputFile(String[]) */
  public void printTable() {
    ImageReader baseReader = new ImageReader();
    IFormatReader[] allReaders = baseReader.getReaders();

    Comparator<IFormatReader> comparator = new Comparator<IFormatReader>() {
      @Override
      public int compare(IFormatReader r1, IFormatReader r2) {
        String s1 = r1.getFormat();
        String s2 = r2.getFormat();

        return s1.compareTo(s2);
      }
    };

    Arrays.sort(allReaders, comparator);

    printHeader();

    for (IFormatReader reader : allReaders) {
      try {
        printFormatEntry(reader);
      } catch (IllegalStateException e) {
      }
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
