/*
 * #%L
 * Bio-Formats command line tools for reading and converting files
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

/*
Contributed 2009 by the Center for BioImage Informatics, UCSB
*/

package loci.formats.tools;

import loci.formats.IFormatReader;
import loci.formats.IFormatWriter;
import loci.formats.ImageReader;
import loci.formats.ImageWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for printing a list of formats supported by Bio-Formats.
 */
public class PrintFormatTable {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(PrintFormatTable.class);

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

    boolean usage = false;
    if (args != null) {
      for (int i=0; i<args.length; i++) {
        if (args[i].equals("-help")) usage = true;
        else if (args[i].equals("-xml")) printStyle = PrintStyles.XML;
        else if (args[i].equals("-html")) printStyle = PrintStyles.HTML;
        else if (args[i].equals("-txt")) printStyle = PrintStyles.TXT;
        else {
          LOGGER.warn("unknown flag: {}; try -help for options", args[i]);
        }
      }
    }

    if (usage) {
      LOGGER.info("Usage: formatlist [-html] [-txt] [-xml]");
      LOGGER.info("  -html: show formats in an HTML table");
      LOGGER.info("   -txt: show formats in plaintext (default)");
      LOGGER.info("   -xml: show formats as XML data");
      return;
    }

    LOGGER.info(getHeader(printStyle));

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
      LOGGER.info(
        getFormatLine(printStyle, readerFormatName, read, write, wmp, ext));
    }

    LOGGER.info(getFooter(printStyle));
  }

  // -- Main method --

  public static void main(String[] args) {
    printSupportedFormats(args);
  }

}
