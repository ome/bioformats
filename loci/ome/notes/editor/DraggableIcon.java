//
// DraggableIcon.java
//

/*
OME Metadata Notes application for exploration and editing of OME-XML and
OME-TIFF metadata. Copyright (C) 2006-@year@ Christopher Peterson.
     
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

package loci.ome.notes.editor;

import java.awt.*;
import java.awt.event.*;
import javax.accessibility.*;
import javax.swing.*;

/** 
 * Drag and drop-able component.  Adapted from the Java 1.4 example,
 * 'http://java/sun.com/docs/books/tutorial/uiswing/examples/dnd/
 * index.html#DragPictureDemo'.
 */
public class DraggableIcon extends JPanel 
  implements Accessible, FocusListener, MouseListener, MouseMotionListener 
{
  private MouseEvent firstMouseEvent = null;
  protected JPanel image;
  protected JLabel label; 
  protected JPopupMenu menu; 
  protected boolean editable = true;
  protected int gridx = 0;
  protected int gridy = 0;
  protected ActionListener listener;

  public DraggableIcon(JPanel image, int gridx, int gridy) {
    this.gridx = gridx; 
    this.gridy = gridy; 
    this.image = image;
    this.add(image); 
    setFocusable(true);
    addMouseListener(this);
    addFocusListener(this);
    addMouseMotionListener(this);
  }

  public void setPanel(JPanel image) {
    remove(this.image); 
    
    if (menu.getComponents().length == 8) {
      menu.remove(4); 
      menu.getComponent(2).setEnabled(true); 
    } 

    if (editable && label != null) remove(label); 
    this.image = image;
    if (editable) {
      Component c = image.getComponents()[0]; 
      for (int i=0; i<TemplateEditor.COMPONENTS.length; i++) {
        if (c.getClass().equals(TemplateEditor.COMPONENTS[i])) {
          label = new JLabel(TemplateEditor.COMPONENT_NAMES[i]);
          add(label);
          
          if (TemplateEditor.COMPONENT_TYPES[i].equals("enum")) {
            JMenuItem item = new JMenuItem("Specify choices");
            item.setActionCommand("specifyChoices");
            item.addActionListener(listener);
            menu.insert(item, 3); 
          }
          else if (TemplateEditor.COMPONENT_TYPES[i].equals("thumbnail")) {
            JMenuItem item = new JMenuItem("Set image source");
            item.setActionCommand("setThumbSource");
            item.addActionListener(listener);
            menu.insert(item, 4); 
            menu.getComponent(2).setEnabled(false); 
          }
          
          break; 
        }
      } 
    }
    add(image); 
    getParent().getParent().repaint(); 
  }

  public void setEditable(boolean editable) { this.editable = editable; }

  public void mousePressed(MouseEvent e) {
    if (image == null) return;

    firstMouseEvent = e;
    e.consume();
  }

  public void mouseDragged(MouseEvent e) {
    if (image == null) return;

    if (firstMouseEvent != null) {
      e.consume();

      int action = TransferHandler.COPY;
      int dx = Math.abs(e.getX() - firstMouseEvent.getX());
      int dy = Math.abs(e.getY() - firstMouseEvent.getY());
      //Arbitrarily define a 5-pixel shift as the
      //official beginning of a drag.
      if (dx > 5 || dy > 5) {
        //This is a drag, not a click.
        JComponent c = (JComponent) e.getSource();
        TransferHandler handler = c.getTransferHandler();
        //Tell the transfer handler to initiate the drag.
        handler.exportAsDrag(c, firstMouseEvent, action);
        firstMouseEvent = null;
      }
    }
  }

  public void mouseReleased(MouseEvent e) {
    firstMouseEvent = null;
  }

  public void mouseMoved(MouseEvent e) { }

  // -- MouseListener API methods --

  public void mouseClicked(MouseEvent e) {
    requestFocusInWindow(); 
  }

  public void mouseEntered(MouseEvent e) { }
  public void mouseExited(MouseEvent e) { }

  // -- FocusListener API methods --

  public void focusGained(FocusEvent e) { repaint(); }

  public void focusLost(FocusEvent e) { repaint(); } 

  protected void paintComponent(Graphics g) {
    g = g.create();

    g.setColor(editable ? Color.RED : Color.BLACK);
    g.fillRect(0, 0, getWidth(), getHeight()); 
    g.clearRect(1, 1, getWidth() - 2, getHeight() - 2); 
  }

}
