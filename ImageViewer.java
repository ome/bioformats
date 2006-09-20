//
// ImageViewer.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

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
  protected JPanel sliderPanel;
  protected JSlider nSlider, zSlider, tSlider, cSlider;
  protected JLabel probeLabel;
  protected JMenuItem fileSave;

  protected ImageReader myReader;
  protected ImageWriter myWriter;

  protected String filename;
  protected IFormatReader in;
  protected BufferedImage[] images;
  protected int sizeZ, sizeT, sizeC;

  // -- Constructor --

  /** Constructs an image viewer. */
  public ImageViewer() {
    super(TITLE);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pane = new JPanel();
    pane.setLayout(new BorderLayout());
    setContentPane(pane);
    setSize(350, 350); // default size

    // navigation sliders
    sliderPanel = new JPanel();
    sliderPanel.setVisible(false);
    sliderPanel.setBorder(new EmptyBorder(5, 3, 5, 3));
    sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
    pane.add(BorderLayout.SOUTH, sliderPanel);

    JPanel nPanel = new JPanel();
    nPanel.setLayout(new BoxLayout(nPanel, BoxLayout.X_AXIS));
    sliderPanel.add(nPanel);
    sliderPanel.add(Box.createVerticalStrut(2));

    nSlider = new JSlider(1, 1);
    nSlider.setEnabled(false);
    nSlider.addChangeListener(this);
    nPanel.add(new JLabel("N"));
    nPanel.add(Box.createHorizontalStrut(3));
    nPanel.add(nSlider);

    JPanel ztcPanel = new JPanel();
    ztcPanel.setLayout(new BoxLayout(ztcPanel, BoxLayout.X_AXIS));
    sliderPanel.add(ztcPanel);

    zSlider = new JSlider(1, 1);
    Dimension dim = zSlider.getPreferredSize();
    dim.width = 50;
    zSlider.setPreferredSize(dim);
    zSlider.setEnabled(false);
    zSlider.addChangeListener(this);
    ztcPanel.add(new JLabel("Z"));
    ztcPanel.add(Box.createHorizontalStrut(3));
    ztcPanel.add(zSlider);
    ztcPanel.add(Box.createHorizontalStrut(7));

    tSlider = new JSlider(1, 1);
    tSlider.setPreferredSize(dim);
    tSlider.setEnabled(false);
    tSlider.addChangeListener(this);
    ztcPanel.add(new JLabel("T"));
    ztcPanel.add(Box.createHorizontalStrut(3));
    ztcPanel.add(tSlider);
    ztcPanel.add(Box.createHorizontalStrut(7));

    cSlider = new JSlider(1, 1);
    cSlider.setPreferredSize(dim);
    cSlider.setEnabled(false);
    cSlider.addChangeListener(this);
    ztcPanel.add(new JLabel("C"));
    ztcPanel.add(Box.createHorizontalStrut(3));
    ztcPanel.add(cSlider);
    ztcPanel.add(Box.createHorizontalStrut(7));

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
    JMenu help = new JMenu("Help");
    menubar.add(help);
    JMenuItem helpAbout = new JMenuItem("About...");
    helpAbout.setActionCommand("about");
    helpAbout.addActionListener(this);
    help.add(helpAbout);

    // image I/O engine
    myReader = new ImageReader();
    myWriter = new ImageWriter();
  }

  /** Opens the given file using the ImageReader. */
  public void open(String id) {
    wait(true);
    try {
      File f = new File(id);
      id = f.getAbsolutePath();

      //images = myReader.open(id);
      IFormatReader r = myReader.getReader(id);
      FileStitcher fs = new FileStitcher(r);
      ChannelMerger cm = new ChannelMerger(fs);

      int num = cm.getImageCount(id);
      ProgressMonitor progress = new ProgressMonitor(this,
        "Reading " + id, null, 0, num + 1);
      sizeZ = cm.getSizeZ(id);
      sizeT = cm.getSizeT(id);
      sizeC = cm.getSizeC(id);
      progress.setProgress(1);
      BufferedImage[] img = new BufferedImage[num];
      for (int i=0; i<num; i++) {
        if (progress.isCanceled()) break;
        img[i] = cm.openImage(id, i);
        if (i == 0) setImages(id, cm, img);
        progress.setProgress(i + 2);
      }
      myReader.close();
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
      //myWriter.save(id, images);
      boolean stack = myWriter.canDoStacks(id);
      ProgressMonitor progress = new ProgressMonitor(this,
        "Saving " + id, null, 0, stack ? images.length : 1);
      if (stack) {
        // save entire stack
        for (int i=0; i<images.length; i++) {
          progress.setProgress(i);
          boolean canceled = progress.isCanceled();
          myWriter.save(id, images[i], i == images.length - 1 || canceled);
          if (canceled) break;
        }
        progress.setProgress(images.length);
      }
      else {
        // save current image only
        myWriter.save(id, getImage(), true);
        progress.setProgress(1);
      }
    }
    catch (Exception exc) { exc.printStackTrace(); }
    wait(false);
  }

  /** Sets the viewer to display the given images. */
  public void setImages(String id,
    IFormatReader reader, BufferedImage[] images)
  {
    filename = id;
    in = reader;
    this.images = images;

    try {
      sizeZ = reader.getSizeZ(id);
      sizeT = reader.getSizeT(id);
      sizeC = reader.getSizeC(id);
    }
    catch (Exception exc) { exc.printStackTrace(); }

    fileSave.setEnabled(true);
    nSlider.removeChangeListener(this);
    zSlider.removeChangeListener(this);
    tSlider.removeChangeListener(this);
    cSlider.removeChangeListener(this);
    nSlider.setValue(1);
    nSlider.setMaximum(images.length);
    nSlider.setEnabled(images.length > 1);
    zSlider.setValue(1);
    zSlider.setMaximum(sizeZ);
    zSlider.setEnabled(sizeZ > 1);
    tSlider.setValue(1);
    tSlider.setMaximum(sizeT);
    tSlider.setEnabled(sizeT > 1);
    cSlider.setValue(1);
    cSlider.setMaximum(sizeC);
    cSlider.setEnabled(sizeC > 1);
    nSlider.addChangeListener(this);
    zSlider.addChangeListener(this);
    tSlider.addChangeListener(this);
    cSlider.addChangeListener(this);
    sliderPanel.setVisible(images.length > 1);

    updateLabel(-1, -1);
    sb.setLength(0);
    if (id != null) {
      sb.append(new File(id).getName());
      sb.append(" ");
    }
    String format = reader.getFormat();
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
    int ndx = getImageIndex();
    return images == null || ndx >= images.length ? null : images[ndx];
  }

  /** Gets the index of the currently displayed image. */
  public int getImageIndex() { return nSlider.getValue() - 1; }

  /** Gets the Z value of the currently displayed image. */
  public int getZ() { return zSlider.getValue() - 1; }

  /** Gets the T value of the currently displayed image. */
  public int getT() { return tSlider.getValue() - 1; }

  /** Gets the C value of the currently displayed image. */
  public int getC() { return cSlider.getValue() - 1; }

  // -- ActionListener API methods --

  /** Handles menu commands. */
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if ("open".equals(cmd)) {
      wait(true);
      JFileChooser chooser = myReader.getFileChooser();
      wait(false);
      int rval = chooser.showOpenDialog(this);
      if (rval == JFileChooser.APPROVE_OPTION) {
        final File file = chooser.getSelectedFile();
        if (file != null) {
          new Thread() {
            public void run() { open(file.getAbsolutePath()); }
          }.start();
        }
      }
    }
    else if ("save".equals(cmd)) {
      wait(true);
      JFileChooser chooser = myWriter.getFileChooser();
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
    else if ("about".equals(cmd)) {
      // HACK - JOptionPane prevents shutdown on dispose
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      JOptionPane.showMessageDialog(this,
        "LOCI Bio-Formats\n" +
        "Built @date@\n\n" +
        "The Bio-Formats library is LOCI software written by\n" +
        "Melissa Linkert, Curtis Rueden, Chris Allan and Eric Kjellman.\n" +
        "http://www.loci.wisc.edu/ome/formats.html",
        "LOCI Bio-Formats", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  // -- ChangeListener API methods --

  /** Handles slider events. */
  public void stateChanged(ChangeEvent e) {
    Object src = e.getSource();
    if (src == nSlider) {
      // update Z, T and C sliders
      int ndx = getImageIndex();
      int[] zct = {-1, -1, -1};
      try { zct = in.getZCTCoords(filename, ndx); }
      catch (Exception exc) { exc.printStackTrace(); }
      if (zct[0] >= 0) {
        zSlider.removeChangeListener(this);
        zSlider.setValue(zct[0] + 1);
        zSlider.addChangeListener(this);
      }
      if (zct[1] >= 0) {
        cSlider.removeChangeListener(this);
        cSlider.setValue(zct[1] + 1);
        cSlider.addChangeListener(this);
      }
      if (zct[2] >= 0) {
        tSlider.removeChangeListener(this);
        tSlider.setValue(zct[2] + 1);
        tSlider.addChangeListener(this);
      }
    }
    else {
      // update N slider
      int ndx = -1;
      try { ndx = in.getIndex(filename, getZ(), getC(), getT()); }
      catch (Exception exc) { exc.printStackTrace(); }
      if (ndx >= 0) {
        nSlider.removeChangeListener(this);
        nSlider.setValue(ndx + 1);
        nSlider.addChangeListener(this);
      }
    }
    updateLabel(-1, -1);
    BufferedImage image = getImage();
    if (image != null) icon.setImage(getImage());
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
    int ndx = getImageIndex();
    sb.setLength(0);
    if (images.length > 1) {
      sb.append("N=");
      sb.append(ndx + 1);
      sb.append("/");
      sb.append(images.length);
    }
    if (sizeZ > 1) {
      sb.append("; Z=");
      sb.append(getZ() + 1);
      sb.append("/");
      sb.append(sizeZ);
    }
    if (sizeT > 1) {
      sb.append("; T=");
      sb.append(getT() + 1);
      sb.append("/");
      sb.append(sizeT);
    }
    if (sizeC > 1) {
      sb.append("; C=");
      sb.append(getC() + 1);
      sb.append("/");
      sb.append(sizeC);
    }
    BufferedImage image = images[ndx];
    int w = image == null ? -1 : image.getWidth();
    int h = image == null ? -1 : image.getHeight();
    if (x >= w) x = w - 1;
    if (y >= h) y = h - 1;
    if (x >= 0 && y >= 0) {
      if (images.length > 1) sb.append("; ");
      sb.append("X=");
      sb.append(x);
      if (w > 0) {
        sb.append("/");
        sb.append(w);
      }
      sb.append("; Y=");
      sb.append(y);
      if (h > 0) {
        sb.append("/");
        sb.append(h);
      }
      if (image != null) {
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
    viewer.setVisible(true);
    if (args.length > 0) viewer.open(args[0]);
  }

}
