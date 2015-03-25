/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

package loci.formats.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import loci.common.Constants;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * A window containing syntax highlighted XML as a tree.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class XMLWindow extends JFrame {

  // -- Fields --

  private Document doc;

  // -- Constructors --

  public XMLWindow() {
    super();
  }

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
    ByteArrayInputStream is =
      new ByteArrayInputStream(xml.getBytes(Constants.ENCODING));
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
    XMLWindow xmlWindow = new XMLWindow();
    xmlWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    if (args.length > 0) {
      String filename = args[0];
      xmlWindow.setXML(new File(filename));
      xmlWindow.setTitle("XML Window - " + filename);
    }
    else {
      BufferedReader in = new BufferedReader(
        new InputStreamReader(System.in, Constants.ENCODING));
      StringBuffer sb = new StringBuffer();
      while (true) {
        String line = in.readLine();
        if (line == null) break;
        sb.append(line);
        sb.append("\n");
      }
      xmlWindow.setXML(sb.toString());
      xmlWindow.setTitle("XML Window - <stdin>");
    }
    xmlWindow.setLocation(200, 200);
    xmlWindow.setVisible(true);
  }

}
