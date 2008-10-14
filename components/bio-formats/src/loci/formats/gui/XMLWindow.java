//
// XMLWindow.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * A window containing syntax highlighted XML as a tree.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/gui/XMLWindow.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/gui/XMLWindow.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class XMLWindow extends JFrame {

  // -- Fields --

  private Document doc;

  // -- Constructor --

  public XMLWindow(String title) {
    super(title);
  }

  // -- XMLWindow methods --

  /** Displays XML from the given string. */
  public void setXML(String xml)
    throws ParserConfigurationException, SAXException, IOException
  {
    setDocument(null);

    // parse XML from string into DOM structure
    DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = docFact.newDocumentBuilder();
    ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes());
    Document doc = db.parse(is);
    is.close();

    setDocument(doc);
  }

  /** Displays XML from the given file. */
  public void setXML(File file)
    throws ParserConfigurationException, SAXException, IOException
  {
    setDocument(null);

    // parse XML from file into DOM structure
    DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = docFact.newDocumentBuilder();
    Document doc = db.parse(file);

    setDocument(doc);
  }

  /** Displays XML from the given document. */
  public void setDocument(Document doc) {
    this.doc = doc;
    getContentPane().removeAll();
    if (doc == null) setVisible(false);
    else {
      // populate metadata window and size intelligently
      JTree tree = XMLCellRenderer.makeJTree(doc);
      for (int i=0; i<tree.getRowCount(); i++) tree.expandRow(i);
      getContentPane().add(new JScrollPane(tree));
      pack();
      Dimension dim = getSize();
      final int pad = 20;
      dim.width += pad;
      dim.height += pad;
      Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
      int maxWidth = 3 * ss.width / 4;
      int maxHeight = 3 * ss.height / 4;
      if (dim.width > maxWidth) dim.width = maxWidth;
      if (dim.height > maxHeight) dim.height = maxHeight;
      setSize(dim);
    }
  }

  /** Gets the XML document currently being displayed within the window. */
  public Document getDocument() {
    return doc;
  }

  // -- Main method --

  public static void main(String[] args) throws Exception {
    String filename = args[0];
    XMLWindow xmlWindow = new XMLWindow("XML Window - " + filename);
    xmlWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    xmlWindow.setXML(new File(filename));
    xmlWindow.setLocation(200, 200);
    xmlWindow.setVisible(true);
  }

}
