/*
 * #%L
 * OME Metadata Editor application for exploration and editing of OME-XML and
 * OME-TIFF metadata.
 * %%
 * Copyright (C) 2006 - 2012 Board of Regents of the University of
 * Wisconsin-Madison, Glencoe Software, Inc., and University of Dundee.
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

import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractCellEditor;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.JTextComponent;

import ome.xml.DOMUtil;

/**
 * An abstract superclass that will be extended by the cell editors
 * for both VariableTextFieldEditor and VariableTextAreaEditor.
 * Handles most events that should stop editing and holds the current
 * data of the TextComponent we're interested in as defined by the
 * last Event of some type that effected a TextComponent in this table.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/legacy/ome-editor/src/loci/ome/editor/VariableTextEditor.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/legacy/ome-editor/src/loci/ome/editor/VariableTextEditor.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Christopher Peterson crpeterson2 at wisc.edu
 */
public abstract class VariableTextEditor extends AbstractCellEditor
  implements TableCellEditor, ActionListener,
  DocumentListener, FocusListener, MouseListener
{

  // -- Fields --

  /** Refers to the TablePanel using this editor. */
  protected MetadataPane.TablePanel tableP;

  /** The data held by the last edited TextComponent in the table.*/
  protected String result;

  // -- Constructor --

  /** Construct a new VariableTextEditor. Only called in subclasses.*/
  public VariableTextEditor(MetadataPane.TablePanel tp) {
    tableP = tp;
    result = null;
  }

  /** Get the data of the current TextComponent.*/
  public Object getCellEditorValue() {
    return result;
  }

  /** Handles the changing of the OMEXMLNode tree.*/
  public void setNode(int row, String value) {
    TableModel tModel = tableP.table.getModel();
    String attrName = (String) tModel.getValueAt(row, 0);
    if (value == null || value.equals("") ) {
      if (tableP.oNode.getDOMElement().hasAttribute(attrName))
        tableP.oNode.getDOMElement().removeAttribute(attrName);
    }
    else {
      if (attrName.endsWith("CharData") ) {
        DOMUtil.setCharacterData(value, tableP.oNode.getDOMElement());
      }
      else tableP.oNode.setAttribute(attrName, value);
    }
  }

  /**
  * Sets the data of the current text component for a triggering
  * AWTEvent.
  */
  public void changeResult(AWTEvent e) {
    if (e.getSource() instanceof JTextComponent) {
      JTextComponent text = (JTextComponent) e.getSource();
      try {
        result = text.getDocument().getText(0,
          text.getDocument().getLength());
      }
      catch (Exception exc) {System.out.println(exc);}
    }
  }

  /**
  * Sets the data of the current text component for a triggering
  * DocumentEvent.
  */
  public void changeNode(DocumentEvent e) {
    try {
      result = e.getDocument().getText(0,
        e.getDocument().getLength());
    }
    catch (Exception exc) {System.out.println(exc);}
    RowDoc rd = (RowDoc) e.getDocument();
    setNode(rd.row, result);
    tableP.callStateChanged(true);
  }

  public void actionPerformed(ActionEvent e) {
    changeResult(e);
    fireEditingStopped();
  }

  public void focusGained(FocusEvent e) {
    changeResult(e);
  }

  public void focusLost(FocusEvent e) {}

  public void mousePressed(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON1) changeResult(e);
  }

  public void mouseReleased(MouseEvent e) {}
  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}

  public void insertUpdate(DocumentEvent e) {
    changeNode(e);
  }

  public void removeUpdate(DocumentEvent e) {
    changeNode(e);
  }

  public void changedUpdate(DocumentEvent e) {
    changeNode(e);
  }

  /**
   * Very simple extension of Document (text) that simply adds an int
   * field to designate which row this Document edits.
   */
  public class RowDoc extends DefaultStyledDocument {
    public int row;

    public RowDoc (int r) {
      super();
      row = r;
    }
  }

}
