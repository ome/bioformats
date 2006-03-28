//
// ImageViewer.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * ImageViewer is a simple viewer/converter
 * for the LOCI Bio-Formats image formats.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ImageViewer extends JFrame
  implements ActionListener, ChangeListener, MouseMotionListener
{

  // -- Constants --

  protected static final String TITLE = "LOCI Bio-Formats Viewer";
  protected static final GraphicsConfiguration GC =
    ImageTools.getDefaultConfiguration();


  // -- Fields --

  protected JPanel pane;
  protected ImageIcon icon;
  protected JLabel iconLabel;
  protected JSlider slider;
  protected JLabel probeLabel;
  protected JMenuItem fileSave;

  protected ImageReader reader;
  protected ImageWriter writer;

  protected BufferedImage[] images;


  // -- Constructor --

  /** Constructs an image viewer. */
  public ImageViewer() {
    super(TITLE);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pane = new JPanel();
    pane.setLayout(new BorderLayout());
    setContentPane(pane);
    setSize(350, 350); // default size

    // navigation slider
    slider = new JSlider(1, 1);
    slider.setVisible(false);
    slider.setBorder(new EmptyBorder(5, 3, 5, 3));
    pane.add(BorderLayout.SOUTH, slider);
    slider.addChangeListener(this);

    // image icon
    BufferedImage dummy = ImageTools.makeImage(new byte[1][1], 1, 1);
    icon = new ImageIcon(dummy);
    iconLabel = new JLabel(icon, SwingConstants.LEFT);
    iconLabel.setVerticalAlignment(SwingConstants.TOP);
    pane.add(new JScrollPane(iconLabel));

    // cursor probe
    probeLabel = new JLabel(" ");
    probeLabel.setHorizontalAlignment(SwingConstants.CENTER);
    probeLabel.setBorder(new BevelBorder(BevelBorder.RAISED));
    pane.add(BorderLayout.NORTH, probeLabel);
    iconLabel.addMouseMotionListener(this);

    // menu bar
    JMenuBar menubar = new JMenuBar();
    setJMenuBar(menubar);
    JMenu file = new JMenu("File");
    menubar.add(file);
    JMenuItem fileOpen = new JMenuItem("Open...");
    fileOpen.setActionCommand("open");
    fileOpen.addActionListener(this);
    file.add(fileOpen);
    fileSave = new JMenuItem("Save...");
    fileSave.setEnabled(false);
    fileSave.setActionCommand("save");
    fileSave.addActionListener(this);
    file.add(fileSave);
    JMenuItem fileExit = new JMenuItem("Exit");
    fileExit.setActionCommand("exit");
    fileExit.addActionListener(this);
    file.add(fileExit);

    // image I/O engine
    reader = new ImageReader();
    writer = new ImageWriter();
  }

  /** Opens the given file using the ImageReader. */
  public void open(String id) {
    wait(true);
    String format = null;
    try {
      format = reader.getFormat(id);
      //images = reader.open(id);
      int num = reader.getImageCount(id);
      ProgressMonitor progress = new ProgressMonitor(this,
        "Reading " + id, null, 0, num);
      BufferedImage[] img = new BufferedImage[num];
      for (int i=0; i<num; i++) {
        if (progress.isCanceled()) break;
        progress.setProgress(i);
        img[i] = reader.open(id, i);
        if (i == 0) setImages(id, format, img);
      }
      reader.close();
      progress.setProgress(num);
    }
    catch (Exception exc) {
      exc.printStackTrace();
      wait(false);
      return;
    }
    wait(false);
  }

  /** Saves the current images to the given file using the ImageWriter. */
  public void save(String id) {
    if (images == null) return;
    wait(true);
    try {
      //writer.save(id, images);
      ProgressMonitor progress = new ProgressMonitor(this,
        "Saving " + id, null, 0, images.length);
      for (int i=0; i<images.length; i++) {
        progress.setProgress(i);
        boolean canceled = progress.isCanceled();
        writer.save(id, images[i], i == images.length - 1 || canceled);
        if (canceled) break;
      }
      progress.setProgress(images.length);
    }
    catch (Exception exc) { exc.printStackTrace(); }
    wait(false);
  }

  /** Sets the viewer to display the given images. */
  public void setImages(String id, String format, BufferedImage[] images) {
    this.images = images;
    fileSave.setEnabled(true);
    slider.removeChangeListener(this);
    slider.setValue(1);
    slider.setMaximum(images.length);
    slider.setVisible(images.length > 1);
    slider.addChangeListener(this);
    Dimension dim = ImageTools.getSize(images[0]);
    updateLabel(-1, -1);
    sb.setLength(0);
    if (id != null) {
      sb.append(new File(id).getName());
      sb.append(" ");
    }
    if (format != null) {
      sb.append("(");
      sb.append(format);
      sb.append(")");
      sb.append(" ");
    }
    if (id != null || format != null) sb.append("- ");
    sb.append(TITLE);
    setTitle(sb.toString());
    icon.setImage(images == null ? null : images[0]);
    pack();
  }

  /** Gets the currently displayed image. */
  public BufferedImage getImage() {
    int ndx = slider == null ? 0 : (slider.getValue() - 1);
    return images == null || ndx >= images.length ? null : images[ndx];
  }


  // -- ActionListener API methods --

  /** Handles menu commands. */
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if ("open".equals(cmd)) {
      wait(true);
      JFileChooser chooser = reader.getFileChooser();
      wait(false);
      int rval = chooser.showOpenDialog(this);
      if (rval == JFileChooser.APPROVE_OPTION) {
        final File file = chooser.getSelectedFile();
        if (file != null) {
          new Thread() {
            public void run() { open(file.getPath()); }
          }.start();
        }
      }
    }
    else if ("save".equals(cmd)) {
      wait(true);
      JFileChooser chooser = writer.getFileChooser();
      wait(false);
      int rval = chooser.showSaveDialog(this);
      if (rval == JFileChooser.APPROVE_OPTION) {
        final File file = chooser.getSelectedFile();
        if (file != null) {
          new Thread() {
            public void run() { save(file.getPath()); }
          }.start();
        }
      }
    }
    else if ("exit".equals(cmd)) dispose();
  }


  // -- ChangeListener API methods --

  /** Handles slider events. */
  public void stateChanged(ChangeEvent e) {
    updateLabel(-1, -1);
    icon.setImage(getImage());
    iconLabel.repaint();
  }


  // -- MouseMotionListener API methods --

  /** Handles cursor probes. */
  public void mouseDragged(MouseEvent e) { updateLabel(e.getX(), e.getY()); }

  /** Handles cursor probes. */
  public void mouseMoved(MouseEvent e) { updateLabel(e.getX(), e.getY()); }


  // -- Helper methods --

  protected StringBuffer sb = new StringBuffer();

  /** Updates cursor probe label. */
  protected void updateLabel(int x, int y) {
    if (images == null) return;
    int ndx = slider == null ? 0 : (slider.getValue() - 1);
    sb.setLength(0);
    if (images.length > 1) {
      sb.append("N=");
      sb.append(ndx + 1);
      sb.append("/");
      sb.append(images.length);
    }
    BufferedImage image = images[ndx];
    int w = image.getWidth();
    int h = image.getHeight();
    if (x >= w) x = w - 1;
    if (y >= h) y = h - 1;
    if (x >= 0 && y >= 0) {
      if (images.length > 1) sb.append("; ");
      sb.append("X=");
      sb.append(x);
      sb.append("; Y=");
      sb.append(y);
      if (image != null) {
        sb.append(" (");
        sb.append(image.getWidth());
        sb.append(" x ");
        sb.append(image.getHeight());
        sb.append(")");
        Raster r = image.getRaster();
        double[] pix = r.getPixel(x, y, (double[]) null);
        sb.append("; value");
        sb.append(pix.length > 1 ? "s=(" : "=");
        for (int i=0; i<pix.length; i++) {
          if (i > 0) sb.append(", ");
          sb.append(pix[i]);
        }
        if (pix.length > 1) sb.append(")");
      }
    }
    sb.append(" ");
    probeLabel.setText(sb.toString());
  }

  /** Toggles wait cursor. */
  protected void wait(boolean wait) {
    setCursor(wait ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : null);
  }


  // -- Main method --

  public static void main(String[] args) {
    ImageViewer viewer = new ImageViewer();
    viewer.show();
    if (args.length > 0) viewer.open(args[0]);
  }

}
