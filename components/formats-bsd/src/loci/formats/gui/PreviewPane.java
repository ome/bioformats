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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import loci.formats.FormatException;
import loci.formats.FormatTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PreviewPane is a panel for use as a JFileChooser accessory, displaying
 * a thumbnail for the selected image, loaded in a separate thread.
 */
public class PreviewPane extends JPanel
  implements PropertyChangeListener, Runnable
{

  // -- Constants --

  private static final Logger LOGGER =
    LoggerFactory.getLogger(PreviewPane.class);

  // -- Fields --

  /** Reader for use when loading thumbnails. */
  protected BufferedImageReader reader;

  /** Current ID to load. */
  protected String loadId;

  /** Last ID loaded. */
  protected String lastId;

  /** Thumbnail loading thread. */
  protected Thread loader;

  /** Flag indicating whether loader thread should keep running. */
  protected boolean loaderAlive;

  /** Method for syncing the view to the model. */
  protected Runnable refresher;

  // -- Fields - view --

  /** Labels containing thumbnail and dimensional information. */
  protected JLabel iconLabel, formatLabel, resLabel, zctLabel, typeLabel;

  // -- Fields - model --

  protected ImageIcon icon;
  protected String iconText, formatText, resText, zctText, typeText;
  protected String iconTip, formatTip, resTip, zctTip, typeTip;

  // -- Constructor --

  /** Constructs a preview pane for the given file chooser. */
  public PreviewPane(JFileChooser jc) {
    super();

    reader = new BufferedImageReader();
    reader.setNormalized(true);

    // create view
    setBorder(new EmptyBorder(0, 10, 0, 10));
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    iconLabel = new JLabel();
    iconLabel.setMinimumSize(new java.awt.Dimension(128, -1));
    iconLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    add(iconLabel);
    add(Box.createVerticalStrut(7));
    formatLabel = new JLabel();
    formatLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    add(formatLabel);
    add(Box.createVerticalStrut(5));
    resLabel = new JLabel();
    resLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    add(resLabel);
    zctLabel = new JLabel();
    zctLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    add(zctLabel);
    typeLabel = new JLabel();
    typeLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
    add(typeLabel);

    // smaller font for most labels
    Font font = formatLabel.getFont();
    font = font.deriveFont(font.getSize2D() - 3);
    formatLabel.setFont(font);
    resLabel.setFont(font);
    zctLabel.setFont(font);
    typeLabel.setFont(font);

    // populate model
    icon = null;
    iconText = formatText = resText = zctText = typeText = "";
    iconTip = formatTip = resTip = zctTip = typeTip = null;

    if (jc != null) {
      jc.setAccessory(this);
      jc.addPropertyChangeListener(this);

      refresher = new Runnable() {
        @Override
        public void run() {
          iconLabel.setIcon(icon);
          iconLabel.setText(iconText);
          iconLabel.setToolTipText(iconTip);
          formatLabel.setText(formatText);
          formatLabel.setToolTipText(formatTip);
          resLabel.setText(resText);
          resLabel.setToolTipText(resTip);
          zctLabel.setText(zctText);
          zctLabel.setToolTipText(zctTip);
          typeLabel.setText(typeText);
          typeLabel.setToolTipText(typeTip);
        }
      };

      // start separate loader thread
      loaderAlive = true;
      loader = new Thread(this, "Preview");
      loader.start();
    }
  }

  // -- Component API methods --

  /* @see java.awt.Component#getPreferredSize() */
  @Override
  public Dimension getPreferredSize() {
    Dimension prefSize = super.getPreferredSize();
    return new Dimension(148, prefSize.height);
  }

  // -- PropertyChangeListener API methods --

  /**
   * Property change event, to listen for when a new
   * file is selected, or the file chooser closes.
   */
  @Override
  public void propertyChange(PropertyChangeEvent e) {
    String prop = e.getPropertyName();
    if (prop.equals("JFileChooserDialogIsClosingProperty")) {
      // notify loader thread that it should stop
      loaderAlive = false;
    }

    if (!prop.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) return;

    File f = (File) e.getNewValue();
    if (f != null && (f.isDirectory() || !f.exists())) f = null;

    loadId = f == null ? null : f.getAbsolutePath();
  }

  // -- Runnable API methods --

  /** Thumbnail loading routine. */
  @Override
  public void run() {
    while (loaderAlive) {
      try { Thread.sleep(100); }
      catch (InterruptedException exc) { LOGGER.info("", exc); }

      try { // catch-all for unanticipated exceptions
        final String id = loadId;
        if (id == lastId) continue;
        if (id != null && lastId != null) {
          String[] files = reader.getUsedFiles();
          boolean found = false;
          for (int i=0; i<files.length; i++) {
            if (id.equals(files[i])) {
              found = true;
              lastId = id;
              break;
            }
          }
          if (found) continue;
        }
        lastId = id;

        icon = null;
        iconText = id == null ? "" : "Reading...";
        formatText = resText = zctText = typeText = "";
        iconTip = id;
        formatTip = resTip = zctTip = typeTip = "";

        if (id == null) {
          SwingUtilities.invokeLater(refresher);
          continue;
        }

        try { reader.setId(id); }
        catch (FormatException exc) {
          LOGGER.debug("Failed to initialize {}", id, exc);
          boolean badFormat =
            exc.getMessage().startsWith("Unknown file format");
          iconText = "Unsupported " + (badFormat ? "format" : "file");
          formatText = resText = "";
          SwingUtilities.invokeLater(refresher);
          lastId = null;
          continue;
        }
        catch (IOException exc) {
          LOGGER.debug("Failed to initialize {}", id, exc);
          iconText = "Unsupported file";
          formatText = resText = "";
          SwingUtilities.invokeLater(refresher);
          lastId = null;
          continue;
        }
        if (id != loadId) {
          SwingUtilities.invokeLater(refresher);
          continue;
        }

        icon = new ImageIcon(makeImage("Loading..."));
        iconText = "";
        String format = reader.getFormat();
        formatText = format;
        formatTip = format;
        resText = reader.getSizeX() + " x " + reader.getSizeY();
        zctText = reader.getSizeZ() + "Z x " +
          reader.getSizeT() + "T x " + reader.getSizeC() + "C";
        typeText = reader.getRGBChannelCount() + " x " +
          FormatTools.getPixelTypeString(reader.getPixelType());
        SwingUtilities.invokeLater(refresher);

        // open middle image thumbnail
        int z = reader.getSizeZ() / 2;
        int t = reader.getSizeT() / 2;
        int ndx = reader.getIndex(z, 0, t);
        BufferedImage thumb = null;
        try { thumb = reader.openThumbImage(ndx); }
        catch (FormatException exc) {
          LOGGER.debug("Failed to read thumbnail #{} from {}",
            new Object[] {ndx, id}, exc);
        }
        catch (IOException exc) {
          LOGGER.debug("Failed to read thumbnail #{} from {}",
            new Object[] {ndx, id}, exc);
        }
        icon = new ImageIcon(thumb == null ? makeImage("Failed") : thumb);
        iconText = "";

        SwingUtilities.invokeLater(refresher);
      }
      catch (Exception exc) {
        LOGGER.info("", exc);
        icon = null;
        iconText = "Thumbnail failure";
        formatText = resText = zctText = typeText = "";
        iconTip = loadId;
        formatTip = resTip = zctTip = typeTip = "";
        SwingUtilities.invokeLater(refresher);
      }
    }
  }

  // -- PreviewPane API methods --

  /** Closes the underlying image reader. */
  public void close() throws IOException {
    if (reader != null) {
      reader.close();
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
