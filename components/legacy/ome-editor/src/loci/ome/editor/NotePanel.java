/*
 * #%L
 * OME Metadata Editor application for exploration and editing of OME-XML and
 * OME-TIFF metadata.
 * %%
 * Copyright (C) 2006 - 2013 Open Microscopy Environment:
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

package loci.ome.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ome.xml.DOMUtil;

import org.openmicroscopy.xml.CustomAttributesNode;
import org.w3c.dom.Element;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * A panel that has a JList to display note names, and a JTextArea to display
 * the content of the note. Also has buttons for adding and deleting notes.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/legacy/ome-editor/src/loci/ome/editor/NotePanel.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/legacy/ome-editor/src/loci/ome/editor/NotePanel.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Christopher Peterson crpeterson2 at wisc.edu
 */
public class NotePanel extends JPanel implements ListSelectionListener {

  // -- Constants --

  /** The color of this component's background. */
  public static final Color BACK_COLOR =
    new Color(175,175,175);

  /** An icon that signifies that notes are present. */
  public static final ImageIcon NOTES_BULLET =
    MetadataPane.createImageIcon("Icons/bullet-green.gif",
      "An icon signifying that notes are present.");

  /** An icon that signifies that no notes are present. */
  public static final ImageIcon NO_NOTES_BULLET =
    MetadataPane.createImageIcon("Icons/bullet-red.gif",
      "An icon signifying that no notes are present.");

  /** The TablePanel that this NotePanel is a part of. */
  MetadataPane.TablePanel tableP;

  /** The JList SubClass that holds the note names. */
  public ClickableList noteList;

  /** The JTextArea that holds a note's content. */
  public JTextArea textArea;

  /** The list and textarea's name labels. */
  public JLabel nameLabel,noteLabel;

  /**
   * Construct a NotePanel given the parent TablePanel.
   * @param tp The TablePanel that this NotePanel is a part of.
   */
  public NotePanel(MetadataPane.TablePanel tp) {
    super();

    tableP = tp;
    setBorder(new EmptyBorder(5,5,5,5));
    setBackground(BACK_COLOR);

    boolean editable = tableP.isEditable();

    Vector noteEleList = getNoteElements();
    DefaultListModel thisModel = new DefaultListModel();
    noteList = new ClickableList(thisModel,this);
    noteList.addListSelectionListener(this);

    if (noteEleList != null) {
      for (int i = 0;i<noteEleList.size();i++) {
        Element e = (Element) noteEleList.get(i);
        thisModel.addElement(e.getAttribute("Name"));
      }
    }

// buggy...
//    if (thisModel.getSize() > 0) noteList.setSelectedIndex(0);

    JScrollPane jScroll = new JScrollPane(noteList);
    jScroll.setPreferredSize(new Dimension(150,100));

    textArea = new JTextArea();
    if (!editable) textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.getDocument().addDocumentListener(noteList);
    JScrollPane jNoteScroll = new JScrollPane(textArea);
    jNoteScroll.setPreferredSize(new Dimension(450,100));
    jNoteScroll.setVerticalScrollBarPolicy(
      JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    FormLayout layout = new FormLayout(
      "pref, 5dlu, pref, 5dlu, pref:grow:right, 5dlu, pref",
      "pref,2dlu,pref");
    setLayout(layout);
    CellConstraints cc = new CellConstraints();

    if (thisModel.size() == 0)
      nameLabel = new JLabel("Name", NO_NOTES_BULLET, JLabel.LEFT);
    else nameLabel = new JLabel("Name", NOTES_BULLET, JLabel.LEFT);
    noteLabel = new JLabel("Notes", NO_NOTES_BULLET, JLabel.LEFT);
    Font thisFont = nameLabel.getFont();
    thisFont = new Font(thisFont.getFontName(), Font.BOLD,thisFont.getSize());
    nameLabel.setFont(thisFont);
    thisFont = noteLabel.getFont();
    thisFont = new Font(thisFont.getFontName(), Font.BOLD,thisFont.getSize());
    noteLabel.setFont(thisFont);

    JButton addBTN = new JButton("New Note");
//    addBTN.setPreferredSize(new Dimension(120,17));
    addBTN.setActionCommand("add");
    addBTN.addActionListener(noteList);
    addBTN.setToolTipText("Add a new note to the \"Name\" list.");
    addBTN.setForeground(MetadataPane.ADD_COLOR);
    addBTN.setOpaque(false);
    if (!editable) addBTN.setEnabled(false);

    JButton delBTN = new JButton("Delete Note");
//    delBTN.setPreferredSize(new Dimension(120,17));
    delBTN.setActionCommand("remove");
    delBTN.addActionListener(noteList);
    delBTN.setToolTipText("Delete the note selected in the \"Name\" list.");
    delBTN.setForeground(MetadataPane.DELETE_COLOR);
    delBTN.setOpaque(false);
    if (!editable) delBTN.setEnabled(false);

    add(nameLabel, cc.xy(1,1, "left,center"));
    add(noteLabel, cc.xy(3,1, "left,center"));

    add(addBTN, cc.xy(5,1, "right,center"));
    add(delBTN, cc.xy(7,1, "right,center"));

    add(jScroll, cc.xy(1,3, "fill,center"));
    add(jNoteScroll, cc.xyw(3,3,5, "fill,center"));
    setBackground(BACK_COLOR);
    setVisible(false);
    if (noteEleList != null) setVisible(true);
  }

  /**
   * Get the DOM Elements of all notes for this NotePanel's
   * TablePanel's OMEXMLNode.
   * @return A vector of all note-type elements for this NotePanel.
   */
  public Vector getNoteElements() {
    if (tableP.tPanel.oNode == null || tableP.oNode == null) return null;
    Vector results = new Vector();
    CustomAttributesNode caNode = null;
    Element currentCA = DOMUtil.getChildElement("CustomAttributes",
      tableP.tPanel.oNode.getDOMElement());
    if (currentCA != null) caNode = new CustomAttributesNode(currentCA);

    if (caNode == null) return null;
    Vector eleList = DOMUtil.getChildElements(
      tableP.el.getAttribute("XMLName") + "Annotation", caNode.getDOMElement());
    if (eleList == null || eleList.size() == 0) return null;
    for (int i = 0;i<eleList.size();i++) {
      Element anEle = (Element) eleList.get(i);
      String id = tableP.oNode.getAttribute("ID");
      if (anEle.getAttribute("NoteFor").equals(id)) results.add(anEle);
    }
    return results;
  }

  /**
   * Get a Hashtable of notenames and values for this NotePanel.
   * @return A Hashtable with keys of the notes' names and values
   * associated with that note name.
   */
  public Hashtable getNoteHash() {
    Hashtable results = new Hashtable();
    Vector noteV = getNoteElements();
    if (noteV != null) {
      for (int i = 0;i< noteV.size();i++) {
        Element ele = (Element) noteV.get(i);
        String key = ele.getAttribute("Name");
        String value = ele.getAttribute("Value");
        results.put(key,value);
      }
    }
    return results;
  }

  /** Get the number of notes associated with this NotePanel. */
  public int getNumNotes() {
    Vector thisVector = getNoteElements();
    if (thisVector == null) return 0;
    else return thisVector.size();
  }

  /**
   * Toggle the icon for the "Name" JLabel.
   * @param hasElements Does the list have note names in it?
   */
  public void setNameLabel(boolean hasElements) {
    if (hasElements) nameLabel.setIcon(NOTES_BULLET);
    else nameLabel.setIcon(NO_NOTES_BULLET);
  }

  /**
   * Toggle the icon for the "Notes" JLabel.
   * @param hasElements Does the textarea have note names in it?
   */
  public void setNotesLabel(boolean hasElements) {
    if (hasElements) noteLabel.setIcon(NOTES_BULLET);
    else noteLabel.setIcon(NO_NOTES_BULLET);
  }

  /**
   * Handles changes in the JList such as selection of a
   * different name or what have you.
   */
  public void valueChanged(ListSelectionEvent e) {
    String thisName = (String) noteList.getSelectedValue();
    if ( thisName != null ) {
      Element currentCA = DOMUtil.getChildElement("CustomAttributes",
        tableP.tPanel.oNode.getDOMElement());
      Vector childList = DOMUtil.getChildElements(
        tableP.el.getAttribute("XMLName") + "Annotation", currentCA);
      Element childEle = null;
      for (int i = 0;i<childList.size();i++) {
        Element thisEle = (Element) childList.get(i);
        if (thisEle.getAttribute("Name").equals(thisName)) childEle = thisEle;
      }
      if (childEle != null && childEle.getAttribute("Value") != null) {
        textArea.setText(childEle.getAttribute("Value"));
      }
    }
    else textArea.setText("");
  }

}
