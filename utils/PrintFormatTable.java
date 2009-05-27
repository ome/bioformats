//
// PrintFormatTable.java
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

/*
Contributed 2009 by the Center for BioImage Informatics, UCSB
*/

import java.io.IOException;

import loci.common.LogTools;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.IFormatWriter;
import loci.formats.ImageReader;
import loci.formats.ImageWriter;

/**
 * Utility class for printing a list of formats supported by Bio-Formats.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/tools/PrintFormatTable.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/tools/PrintFormatTable.java">SVN</a></dd></dl>
 */
public class PrintFormatTable {

  public enum PrintStyles { TXT, XML, HTML }

  //----------------------------------------------------------
  // Text
  //----------------------------------------------------------
  public static String getTextHeader() {
    return "";
  }

  public static String getTextFooter() {
    return "";
  }

  public static String getTextFormatLine(String name, boolean read,
    boolean write, boolean wmp, String ext)
  {
    StringBuffer s = new StringBuffer(name);
    if (read) s.append(": can read");
    if (write) s.append(", can write");
    if (wmp) s.append(", can write multiple");
    s.append(" (");
    s.append(ext);
    s.append(")");
    return s.toString();
  }

  //----------------------------------------------------------
  // XML
  //----------------------------------------------------------
  public static String getXmlHeader() {
    return "<response>\n";
  }

  public static String getXmlFooter() {
    return "</response>\n";
  }

  public static String getXmlFormatLine(String name, boolean read,
    boolean write, boolean wmp, String ext)
  {
    StringBuffer s = new StringBuffer("<format name='");
    s.append(name);
    s.append("'>\n");
    if (read) s.append("  <tag name='support' value='reading' />\n");
    if (write) s.append("  <tag name='support' value='writing' />\n");
    if (wmp) {
      s.append("  <tag name='support' value='writing multiple pages' />\n");
    }
    s.append("  <tag name='extensions' value='");
    s.append(ext.replace(", ", "|"));
    s.append("' />\n</format>\n");
    return s.toString();
  }

  //----------------------------------------------------------
  // HTML
  //----------------------------------------------------------
  public static String getHtmlHeader() {
    return "<table><tr><th>Name</th><th>Reading</th><th>Writing</th>" +
      "<th>Extensions</th></tr>";
  }

  public static String getHtmlFooter() {
    return "</table>";
  }

  public static String getHtmlFormatLine(String name, boolean read,
    boolean write, String ext)
  {
    StringBuffer s = new StringBuffer("  <tr><td>");
    s.append(name);
    s.append("</td><td>");
    s.append(read ? "yes" : "no");
    s.append("</td><td>");
    s.append(write ? "yes" : "no");
    s.append("</td><td>");
    s.append(ext);
    s.append("</td></tr>");
    return s.toString();
  }

  public static String getHeader(PrintStyles style) {
    if (style == PrintStyles.XML) return getXmlHeader();
    if (style == PrintStyles.HTML) return getHtmlHeader();
    if (style == PrintStyles.TXT) return getTextHeader();
    return "";
  }

  public static String getFooter(PrintStyles style) {
    if (style == PrintStyles.XML) return getXmlFooter();
    if (style == PrintStyles.HTML) return getHtmlFooter();
    if (style == PrintStyles.TXT) return getTextFooter();
    return "";
  }

  public static String getFormatLine(PrintStyles style, String name,
    boolean read, boolean write, boolean wmp, String ext)
  {
    if (style == PrintStyles.XML) {
      return getXmlFormatLine(name, read, write, wmp, ext);
    }
    if (style == PrintStyles.HTML) {
      return getHtmlFormatLine(name, read, write, ext);
    }
    if (style == PrintStyles.TXT) {
      return getTextFormatLine(name, read, write, wmp, ext);
    }
    return "";
  }

  public static void printSupportedFormats(String[] args) {
    PrintStyles printStyle = PrintStyles.TXT;

    if (args != null) {
      for (int i=0; i<args.length; i++) {
        if (args[i].equals("-xml")) printStyle = PrintStyles.XML;
        else if (args[i].equals("-html")) printStyle = PrintStyles.HTML;
        else if (args[i].equals("-txt")) printStyle = PrintStyles.TXT;
      }
    }

    LogTools.println(getHeader(printStyle));

    // retrieve all of the file format readers and writers
    ImageReader baseReader = new ImageReader();
    IFormatReader[] readers = baseReader.getReaders();

    ImageWriter baseWriter = new ImageWriter();
    IFormatWriter[] writers = baseWriter.getWriters();

    for (int i=0; i<readers.length; i++) {
      String readerFormatName = readers[i].getFormat();
      String ext = "";
      boolean read = true;
      boolean write = false;
      boolean wmp = false;

      // check if there is a corresponding writer
      IFormatWriter writer = null;

      for (int j=0; j<writers.length; j++) {
        if (writers[j].getFormat().equals(readerFormatName)) {
          writer = writers[j];
        }
      }

      if (writer != null) {
        write = true;
        // this format can be written
        // determine whether or not multiple images can be
        // written to a single file
        if (writer.canDoStacks()) wmp = true;
      }

      String[] extensions = readers[i].getSuffixes();
      for (int j=0; j<extensions.length; j++) {
        ext += extensions[j];
        if (j < extensions.length - 1) ext += (", ");
      }

      // display information about the format
      LogTools.println(getFormatLine(printStyle, readerFormatName, read,
        write, wmp, ext));
    }

    LogTools.println(getFooter(printStyle));
  }

  // -- Main method --

  public static void main(String[] args) {
    printSupportedFormats(args);
  }

}
