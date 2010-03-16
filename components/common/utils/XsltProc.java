//
// XsltProc.java
//

import java.io.FileReader;
import java.io.IOException;
import loci.common.xml.XMLTools;

import java.io.StringWriter;
import javax.xml.transform.Templates;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Transforms an XML document according to the given stylesheet,
 * similar to the xsltproc command line tool.
 */
public class XsltProc {
  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.out.println("Usage: java XsltProc stylesheet.xsl document.xml");
      System.exit(1);
    }
    String xsl = args[0];
    String xml = args[1];

    StreamSource xmlSource = new StreamSource(new FileReader(xml));
    StringWriter xmlWriter = new StringWriter();
    StreamResult xmlResult = new StreamResult(xmlWriter);

    // transform from source to result
    Templates stylesheet = XMLTools.getStylesheet(xsl, null);
    XMLTools.transformXML(xmlSource, stylesheet);

    String output = xmlWriter.toString();
    System.out.println(output);
  }
}
