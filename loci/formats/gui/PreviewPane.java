//
// PreviewPane.java
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

package loci.formats.gui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import loci.formats.*;

/**
 * PreviewPane is a panel for use as a JFileChooser accessory, displaying
 * a thumbnail for the selected image, loaded in a separate thread.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/gui/PreviewPane.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/gui/PreviewPane.java">SVN</a></dd></dl>
 */
public class PreviewPane extends JPanel
  implements PropertyChangeListener, Runnable
{

  // -- Fields --

  /** Reader for use when loading thumbnails. */
  protected IFormatReader reader;

  /** Labels containing thumbnail and dimensional information. */
  protected JLabel iconLabel, resLabel, zctLabel, typeLabel;

  /** Thumbnail loading thread. */
  protected Thread loader;

  /** Flag indicating whether loader thread should keep running. */
  protected boolean loaderAlive;

  /** Current ID to load. */
  protected String loadId;

  /** Last ID loaded. */
  protected String lastId;

  // -- Constructors --

  /** Constructs a preview pane for the given file chooser. */
  public PreviewPane(JFileChooser jc) {
    super();
    setBorder(new EmptyBorder(0, 10, 0, 0));

    reader = new ImageReader();
    reader.setNormalized(true);

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    iconLabel = new JLabel();
    iconLabel.setMinimumSize(new java.awt.Dimension(128, -1));
    iconLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    add(iconLabel);
    add(Box.createVerticalStrut(7));
    resLabel = new JLabel("");
    resLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    add(resLabel);
    zctLabel = new JLabel("");
    zctLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    add(zctLabel);
    typeLabel = new JLabel("");
    typeLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    add(typeLabel);

    if (jc != null) {
      jc.setAccessory(this);
      jc.addPropertyChangeListener(this);

      // start separate loader thread
      loaderAlive = true;
      loader = new Thread(this, "Preview");
      loader.start();
    }
  }

  // -- Component API methods --

  /* @see java.awt.Component.getPreferredSize() */
  public Dimension getPreferredSize() {
    Dimension prefSize = super.getPreferredSize();
    return new Dimension(128, prefSize.height);
  }

  // -- PropertyChangeListener API methods --

  /**
   * Property change event, to listen for when a new
   * file is selected, or the file chooser closes.
   */
  public void propertyChange(PropertyChangeEvent e) {
    String prop = e.getPropertyName();
    if (prop.equals("JFileChooserDialogIsClosingProperty")) {
      // notify loader thread that it should stop
      loaderAlive = false;
    }

    if (!prop.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) return;

    File selection = (File) e.getNewValue();
    if (selection == null) return;

    loadId = selection.getAbsolutePath();
  }

  // -- Runnable API methods --

  /** Thumbnail loading routine. */
  public void run() {
    while (loaderAlive) {
      try { Thread.sleep(100); }
      catch (InterruptedException exc) { LogTools.trace(exc); }

      String id = loadId;
      if (id == lastId) continue;
      if (id != null && lastId != null) {
        String[] files = reader.getUsedFiles();
        boolean found = false;
        for (int i=0; i<files.length; i++) {
          if (id.equals(files[i])) {
            found = true;
            break;
          }
        }
        if (found) continue;
      }
      lastId = id;

      iconLabel.setIcon(null);
      resLabel.setText(id == null ? "" : "Reading...");
      zctLabel.setText("");
      typeLabel.setText("");

      if (id == null) continue;

      try { reader.setId(id); }
      catch (FormatException exc) {
        LogTools.trace(exc);
        resLabel.setText("Unsupported");
        boolean badFormat = exc.getMessage().startsWith("Unknown file format");
        zctLabel.setText(badFormat ? "format" : "file");
        continue;
      }
      catch (IOException exc) {
        LogTools.trace(exc);
        resLabel.setText("Unsupported");
        zctLabel.setText("file");
        continue;
      }
      if (id != loadId) continue;

      iconLabel.setIcon(id == null ? null :
        new ImageIcon(makeImage("Loading...")));
      resLabel.setText(reader.getSizeX() + " x " + reader.getSizeY());
      zctLabel.setText(reader.getSizeZ() + "Z x " +
        reader.getSizeT() + "T x " + reader.getSizeC() + "C");
      typeLabel.setText(reader.getRGBChannelCount() + " x " +
        FormatTools.getPixelTypeString(reader.getPixelType()));

      // open middle image thumbnail
      int z = reader.getSizeZ() / 2;
      int t = reader.getSizeT() / 2;
      int ndx = reader.getIndex(z, 0, t);
      BufferedImage thumb = null;
      try { thumb = reader.openThumbImage(ndx); }
      catch (FormatException exc) { LogTools.trace(exc); }
      catch (IOException exc) { LogTools.trace(exc); }
      iconLabel.setIcon(new ImageIcon(thumb == null ?
        makeImage("Failed") : thumb));
      repaint();
    }
  }

  // -- Helper methods --

  /**
   * Creates a blank image with the given message painted on top (e.g.,
   * a loading or error message), matching the size of the active reader's
   * thumbnails.
   */
  private BufferedImage makeImage(String message) {
    int w = reader.getThumbSizeX(), h = reader.getThumbSizeY();
    if (w < 128) w = 128;
    if (h < 32) h = 32;
    BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = image.createGraphics();
    Rectangle2D.Float r = (Rectangle2D.Float)
      g.getFont().getStringBounds(message, g.getFontRenderContext());
    g.drawString(message, (w - r.width) / 2, (h - r.height) / 2 + r.height);
    g.dispose();
    return image;
  }

}
