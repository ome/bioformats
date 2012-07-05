/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package loci.common.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import loci.common.RandomAccessInputStream;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A legacy delegator class for ome.scifio.xml.XMLTools.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/xml/XMLTools.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/xml/XMLTools.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Chris Allan callan at blackcat.ca
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public final class XMLTools {

  // -- Constants --

  // -- Fields --

  // -- Constructor --

  private XMLTools() { }

  // -- XML to/from DOM --

  /** Parses a DOM from the given XML file on disk. */
  public static Document parseDOM(File file)
    throws ParserConfigurationException, SAXException, IOException
  {
    return ome.scifio.xml.XMLTools.parseDOM(file);
  }

  /** Parses a DOM from the given XML string. */
  public static Document parseDOM(String xml)
    throws ParserConfigurationException, SAXException, IOException
  {
    return ome.scifio.xml.XMLTools.parseDOM(xml);
  }

  /** Parses a DOM from the given XML input stream. */
  public static Document parseDOM(InputStream is)
    throws ParserConfigurationException, SAXException, IOException
  {
    return ome.scifio.xml.XMLTools.parseDOM(is);
  }

  /** Converts the given DOM back to a string. */
  public static String getXML(Document doc)
    throws TransformerConfigurationException, TransformerException
  {
    return ome.scifio.xml.XMLTools.getXML(doc);
  }

  // -- Filtering --

  /** Escape special characters. */
  public static String escapeXML(String s) {
    return ome.scifio.xml.XMLTools.escapeXML(s);
  }

  /** Remove invalid characters from an XML string. */
  public static String sanitizeXML(String s) {
    return ome.scifio.xml.XMLTools.sanitizeXML(s);
  }

  /** Indents XML to be more readable. */
  public static String indentXML(String xml) {
    return ome.scifio.xml.XMLTools.indentXML(xml);
  }

  /** Indents XML by the given spacing to be more readable. */
  public static String indentXML(String xml, int spacing) {
    return ome.scifio.xml.XMLTools.indentXML(xml, spacing);
  }

  /**
   * Indents XML to be more readable, avoiding any whitespace
   * injection into CDATA if the preserveCData flag is set.
   */
  public static String indentXML(String xml, boolean preserveCData) {
    return ome.scifio.xml.XMLTools.indentXML(xml, preserveCData);
  }

  /**
   * Indents XML by the given spacing to be more readable, avoiding any
   * whitespace injection into CDATA if the preserveCData flag is set.
   */
  public static String indentXML(String xml, int spacing,
    boolean preserveCData)
  {
    return ome.scifio.xml.XMLTools.indentXML(xml, spacing, preserveCData);
  }

  // -- Parsing --

  /** Parses the given XML string into a list of key/value pairs. */
  public static Hashtable<String, String> parseXML(String xml)
    throws IOException
  {
    return ome.scifio.xml.XMLTools.parseXML(xml);
  }

  /**
   * Parses the given XML string using the specified XML handler.
   */
  public static void parseXML(String xml, DefaultHandler handler)
    throws IOException
  {
    ome.scifio.xml.XMLTools.parseXML(xml, handler);
  }

  /**
   * Parses the XML contained in the given input stream into
   * using the specified XML handler.
   * Be very careful, as 'stream' <b>will</b> be closed by the SAX parser.
   */
  public static void parseXML(RandomAccessInputStream stream,
    DefaultHandler handler) throws IOException
  {
    ome.scifio.xml.XMLTools.parseXML((InputStream)stream, handler);
  }

  /**
   * Parses the XML contained in the given byte array into
   * using the specified XML handler.
   */
  public static void parseXML(byte[] xml, DefaultHandler handler)
    throws IOException
  {
    ome.scifio.xml.XMLTools.parseXML(xml, handler);
  }

  /**
   * Parses the XML contained in the given InputStream using the
   * specified XML handler.
   */
  public static void parseXML(InputStream xml, DefaultHandler handler)
    throws IOException
  {
    ome.scifio.xml.XMLTools.parseXML(xml, handler);
  }

  // -- XSLT --

  /** Gets an XSLT template from the given resource location. */
  public static Templates getStylesheet(String resourcePath,
    Class<?> sourceClass)
  {
    return ome.scifio.xml.XMLTools.getStylesheet(resourcePath, sourceClass);
  }

  /** Replaces NS:tag with NS_tag for undeclared namespaces */
  public static String avoidUndeclaredNamespaces(String xml) {
    return ome.scifio.xml.XMLTools.avoidUndeclaredNamespaces(xml);
  }

  /** Transforms the given XML string using the specified XSLT stylesheet. */
  public static String transformXML(String xml, Templates xslt)
    throws IOException
  {
    return ome.scifio.xml.XMLTools.transformXML(xml, xslt);
  }

  /** Transforms the given XML data using the specified XSLT stylesheet. */
  public static String transformXML(Source xmlSource, Templates xslt)
    throws IOException
  {
    return ome.scifio.xml.XMLTools.transformXML(xmlSource, xslt);
  }

  // -- Validation --

  /**
   * Attempts to validate the given XML string using
   * Java's XML validation facility. Requires Java 1.5+.
   * @param xml The XML string to validate.
   * @return whether or not validation was successful.
   */
  public static boolean validateXML(String xml) {
    return ome.scifio.xml.XMLTools.validateXML(xml);
  }

  /**
   * Attempts to validate the given XML string using
   * Java's XML validation facility. Requires Java 1.5+.
   * @param xml The XML string to validate.
   * @param label String describing the type of XML being validated.
   * @return whether or not validation was successful.
   */
  public static boolean validateXML(String xml, String label) {
    return ome.scifio.xml.XMLTools.validateXML(xml, label);
  }

  // -- Helper class --
}
