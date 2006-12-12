//
// ImagePreview.java
//

/*
LOCI 4D Data Browser plugin for quick browsing of 4D datasets in ImageJ.
Copyright (C) 2005-@year@ Christopher Peterson, Francis Wong, Curtis Rueden
and Melissa Linkert.

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

// Adapted from FileChooserDemo2

package loci.plugins.browser;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.*;
import loci.formats.*;

/** ImagePreview.java is a 1.4 example used by FileChooserDemo2.java. */
public class ImagePreview extends JComponent
  implements PropertyChangeListener, Runnable
{
  protected ImageReader ir;
  protected ImageIcon thumbnail = null;
  protected File file = null;
  protected boolean initial;

  public ImagePreview(JFileChooser fc, ImageReader ir) {
    setPreferredSize(new Dimension(100, 50));
    initial = true;
    fc.addPropertyChangeListener(this);
    this.ir = ir;
  }

  public void loadImage() {
    try {
      if (file == null) {
        thumbnail = null;
        return;
      }

      //Don't use createImageIcon (which is a wrapper for getResource)
      //because the image we're trying to load is probably not one
      //of this program's own resources.
      if (LociDataBrowser.DEBUG) {
        System.err.println("file path: "+file.getAbsolutePath());
      }

      

      synchronized(ir) {
        String path = file.getAbsolutePath();
        int sizeZ = ir.getSizeZ(path);
        int sizeT = ir.getSizeT(path);
        int index = ir.getIndex(path, sizeZ / 2, 0, sizeT / 2);
        BufferedImage image = ir.openThumbImage(path, index);
        ir.close();
        thumbnail = new ImageIcon(image);
      }
    }
    catch (Exception e) {
      thumbnail = null;
      return;
    }
  }

  public void propertyChange(PropertyChangeEvent e) {
    boolean update = false;
    String prop = e.getPropertyName();

    //If the directory changed, don't show an image.
    if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
      file = null;
      initial = true;
      update = true;
    }
    //If a file became selected, find out which one.
    else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
      file = (File) e.getNewValue();
      if (file == null) initial = true;
      else if (file != null && file.isDirectory()) initial = true; 
      update = true;
    }

    //Update the preview accordingly.
    if (update) {
      thumbnail = null;
      if (isShowing()) {
        repaint();
      }
    }
  }
  
  // -- Runnable API methods --

  /** The thread method that does the slice loading.*/
  public void run() {
    loadImage();
    repaint();
  }

  protected void paintComponent(Graphics g) {
    if (initial) initial = false;
    else if (thumbnail == null && !initial) {
      g.drawString("Loading Preview...",5,getHeight()/2);
      Thread loader = new Thread(this, "Browser-Thumbnail-Loader");
      loader.start();
    }
    else if (thumbnail != null && !initial) {
      int x = getWidth()/2 - thumbnail.getIconWidth()/2;
      int y = getHeight()/2 - thumbnail.getIconHeight()/2;
      if (y < 0) y = 0;
      if (x < 5) x = 5;
      thumbnail.paintIcon(this, g, x, y);
    }
  }

}
