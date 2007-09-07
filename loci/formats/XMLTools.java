//
// XMLTools.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats;

import java.io.*;
import java.util.StringTokenizer;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A utility class for working with XML (not necessarily OME-XML).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/XMLTools.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/XMLTools.java">SVN</a></dd></dl>
 */
public final class XMLTools {

  // -- Constructor --

  private XMLTools() { }

  // -- Utility methods --

  /**
   * Attempts to validate the given XML string using
   * Java's XML validation facility. Requires Java 1.5+.
   * @param xml The XML string to validate.
   */
  public static void validateXML(String xml) { validateXML(xml, null); }

  /**
   * Attempts to validate the given XML string using
   * Java's XML validation facility. Requires Java 1.5+.
   * @param xml The XML string to validate.
   * @param label String describing the type of XML being validated.
   */
  public static void validateXML(String xml, String label) {
    if (label == null) label = "XML";

    // check Java version (XML validation only works in Java 1.5+)
    String version = System.getProperty("java.version");
    int dot = version.indexOf(".");
    if (dot >= 0) dot = version.indexOf(".", dot + 1);
    float ver = Float.NaN;
    if (dot >= 0) {
      try {
        ver = Float.parseFloat(version.substring(0, dot));
      }
      catch (NumberFormatException exc) { }
    }
    if (ver != ver) {
      LogTools.println("Warning: cannot determine if Java version\"" +
        version + "\" supports Java v1.5. XML validation may fail.");
    }

    if (ver < 1.5f) return; // do not attempt validation if not Java 1.5+

    // get path to schema from root element using SAX
    LogTools.println("Parsing schema path");
    ValidationSAXHandler saxHandler = new ValidationSAXHandler();
    SAXParserFactory saxFactory = SAXParserFactory.newInstance();
    Exception exception = null;
    try {
      SAXParser saxParser = saxFactory.newSAXParser();
      InputStream is = new ByteArrayInputStream(xml.getBytes());
      saxParser.parse(is, saxHandler);
    }
    catch (ParserConfigurationException exc) { exception = exc; }
    catch (SAXException exc) { exception = exc; }
    catch (IOException exc) { exception = exc; }
    if (exception != null) {
      LogTools.println("Error parsing schema path from " + label + ":");
      LogTools.trace(exception);
      return;
    }
    String schemaPath = saxHandler.getSchemaPath();
    if (schemaPath == null) {
      LogTools.println("No schema path found. Validation cannot continue.");
      return;
    }
    else LogTools.println(schemaPath);

    LogTools.println("Validating " + label);

    // use reflection to avoid compile-time dependency on optional
    // org.openmicroscopy.xml or javax.xml.validation packages
    ReflectedUniverse r = new ReflectedUniverse();

    try {
      // look up a factory for the W3C XML Schema language
      r.setVar("xmlSchemaPath", "http://www.w3.org/2001/XMLSchema");
      r.exec("import javax.xml.validation.SchemaFactory");
      r.exec("factory = SchemaFactory.newInstance(xmlSchemaPath)");

      // compile the schema
      r.exec("import java.net.URL");
      r.setVar("schemaPath", schemaPath);
      r.exec("schemaLocation = new URL(schemaPath)");
      r.exec("schema = factory.newSchema(schemaLocation)");

      // HACK - workaround for weird Linux bug preventing use of
      // schema.newValidator() method even though it is "public final"
      r.setAccessibilityIgnored(true);

      // get a validator from the schema
      r.exec("validator = schema.newValidator()");

      // prepare the XML source
      r.exec("import java.io.StringReader");
      r.setVar("xml", xml);
      r.exec("reader = new StringReader(xml)");
      r.exec("import org.xml.sax.InputSource");
      r.exec("is = new InputSource(reader)");
      r.exec("import javax.xml.transform.sax.SAXSource");
      r.exec("source = new SAXSource(is)");

      // validate the XML
      ValidationErrorHandler errorHandler = new ValidationErrorHandler();
      r.setVar("errorHandler", errorHandler);
      r.exec("validator.setErrorHandler(errorHandler)");
      r.exec("validator.validate(source)");
      if (errorHandler.ok()) LogTools.println("No validation errors found.");
    }
    catch (ReflectException exc) {
      LogTools.println("Error validating " + label + ":");
      LogTools.trace(exc);
    }
  }

  /** Indents XML to be more readable. */
  public static String indentXML(String xml) { return indentXML(xml, 3); }

  /** Indents XML by the given spacing to be more readable. */
  public static String indentXML(String xml, int spacing) {
    int indent = 0;
    StringBuffer sb = new StringBuffer();
    StringTokenizer st = new StringTokenizer(xml, "<>", true);
    boolean element = false;
    while (st.hasMoreTokens()) {
      String token = st.nextToken().trim();
      if (token.equals("")) continue;
      if (token.equals("<")) {
        element = true;
        continue;
      }
      if (element && token.equals(">")) {
        element = false;
        continue;
      }
      if (element && token.startsWith("/")) indent -= spacing;
      for (int j=0; j<indent; j++) sb.append(" ");
      if (element) sb.append("<");
      sb.append(token);
      if (element) sb.append(">");
      sb.append("\n");
      if (element && !token.startsWith("?") &&
        !token.startsWith("/") && !token.endsWith("/"))
      {
        indent += spacing;
      }
    }
    return sb.toString();
  }

  // -- Helper classes --

  /** Used by validateXML to parse the XML block's schema path using SAX. */
  private static class ValidationSAXHandler extends DefaultHandler {
    private String schemaPath;
    private boolean first;
    public String getSchemaPath() { return schemaPath; }
    public void startDocument() {
      schemaPath = null;
      first = true;
    }
    public void startElement(String uri,
      String localName, String qName, Attributes attributes)
    {
      if (!first) return;
      first = false;

      int len = attributes.getLength();
      String xmlns = null, xsiSchemaLocation = null;
      for (int i=0; i<len; i++) {
        String name = attributes.getQName(i);
        if (name.equals("xmlns")) xmlns = attributes.getValue(i);
        else if (name.equals("xsi:schemaLocation")) {
          xsiSchemaLocation = attributes.getValue(i);
        }
      }
      if (xmlns == null || xsiSchemaLocation == null) return; // not found

      StringTokenizer st = new StringTokenizer(xsiSchemaLocation);
      while (st.hasMoreTokens()) {
        String token = st.nextToken();
        if (xmlns.equals(token)) {
          // next token is the actual schema path
          if (st.hasMoreTokens()) schemaPath = st.nextToken();
          break;
        }
      }
    }
  }

  /** Used by validateXML to handle XML validation errors. */
  private static class ValidationErrorHandler implements ErrorHandler {
    private boolean ok = true;
    public boolean ok() { return ok; }
    public void error(SAXParseException e) {
      LogTools.println("error: " + e.getMessage());
      ok = false;
    }
    public void fatalError(SAXParseException e) {
      LogTools.println("fatal error: " + e.getMessage());
      ok = false;
    }
    public void warning(SAXParseException e) {
      LogTools.println("warning: " + e.getMessage());
      ok = false;
    }
  }

}

