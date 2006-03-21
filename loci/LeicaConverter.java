// LeicaConverter.java

// Coded 2006 Mar 3-4, 21 by Curtis Rueden, for Julie Simpson.
// Permission is granted to use this code for anything.

import java.awt.Image;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import loci.formats.*;

public class LeicaConverter extends JFrame
  implements ActionListener, Runnable
{
  private static final String NL = "" + (char) 13 + (char) 10;

  private ImageReader reader = new ImageReader();
  private ImageReader reader2 = new ImageReader();
  private TiffWriter writer = new TiffWriter();
  private JFileChooser rc = reader.getFileChooser();
  private JFileChooser wc = writer.getFileChooser();

  private JTextField input, output;
  private JRadioButton rgb, leica;
  private JProgressBar progress;
  private JButton convert;
  private boolean programQuit;

  public LeicaConverter() {
    super("Leica Converter");
    setLocation(100, 100);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    JPanel pane = new JPanel();
    pane.setBorder(new EmptyBorder(10, 10, 10, 10));
    pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
    setContentPane(pane);

    JPanel row1 = new JPanel();
    row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));
    pane.add(row1);

    JLabel inputLabel = new JLabel("Input file(s)");
    input = new JTextField();
    input.setColumns(25);
    input.setMaximumSize(
      new Dimension(Integer.MAX_VALUE, input.getPreferredSize().height));
    JButton inputButton = new JButton("Choose");
    inputButton.setActionCommand("input");
    inputButton.addActionListener(this);

    row1.add(inputLabel);
    row1.add(Box.createHorizontalStrut(3));
    row1.add(input);
    row1.add(Box.createHorizontalStrut(3));
    row1.add(inputButton);

    JPanel row2 = new JPanel();
    row2.setBorder(new EmptyBorder(5, 0, 5, 0));
    row2.setLayout(new BoxLayout(row2, BoxLayout.X_AXIS));
    pane.add(row2);

    JLabel outputLabel = new JLabel("Output file");
    output = new JTextField();
    output.setColumns(25);
    output.setMaximumSize(
      new Dimension(Integer.MAX_VALUE, output.getPreferredSize().height));
    JButton outputButton = new JButton("Choose");
    outputButton.setActionCommand("output");
    outputButton.addActionListener(this);

    row2.add(outputLabel);
    row2.add(Box.createHorizontalStrut(3));
    row2.add(output);
    row2.add(Box.createHorizontalStrut(3));
    row2.add(outputButton);

    JPanel row3 = new JPanel();
    row3.setLayout(new BoxLayout(row3, BoxLayout.X_AXIS));
    pane.add(row3);

    JLabel formatLabel = new JLabel("Output format");
    inputLabel.setPreferredSize(formatLabel.getPreferredSize());
    outputLabel.setPreferredSize(formatLabel.getPreferredSize());
    rgb = new JRadioButton("merged RGB", true);
    leica = new JRadioButton("split Leica");
    ButtonGroup group = new ButtonGroup();
    group.add(rgb);
    group.add(leica);

    row3.add(formatLabel);
    row3.add(Box.createHorizontalStrut(3));
    row3.add(rgb);
    row3.add(Box.createHorizontalStrut(3));
    row3.add(leica);
    row3.add(Box.createHorizontalGlue());

    JPanel row4 = new JPanel();
    row4.setLayout(new BoxLayout(row4, BoxLayout.X_AXIS));
    pane.add(Box.createVerticalStrut(10));
    pane.add(row4);

    progress = new JProgressBar();
    progress.setStringPainted(true);
    progress.setString("");
    convert = new JButton("Convert");
    convert.setActionCommand("convert");
    convert.addActionListener(this);
    JButton quit = new JButton("Quit");
    quit.setActionCommand("quit");
    quit.addActionListener(this);

    row4.add(progress);
    row4.add(Box.createHorizontalStrut(3));
    row4.add(convert);
    row4.add(Box.createHorizontalStrut(3));
    row4.add(quit);
  }

  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if ("input".equals(cmd)) {
      int rval = rc.showOpenDialog(this);
      if (rval != JFileChooser.APPROVE_OPTION) return;
      File file = rc.getSelectedFile();
      String path = file.getPath();
      int dot = path.lastIndexOf(".");
      if (dot < 0) dot = path.length();
      String pre = path.substring(0, dot - 1);
      char c = path.charAt(dot - 1);
      String post = path.substring(dot);
      if ((c == '1' && new File(pre + "2" + post).exists()) ||
        (c == '2' && new File(pre + "1" + post).exists()))
      {
        path = pre + "<1-2>" + post;
      }
      input.setText(path);
    }
    else if ("output".equals(cmd)) {
      int rval = wc.showSaveDialog(this);
      if (rval != JFileChooser.APPROVE_OPTION) return;
      File file = wc.getSelectedFile();
      String s = file.getPath();
      if (!writer.isThisType(s)) s += ".tif";
      output.setText(s);
    }
    else if ("convert".equals(cmd)) {
      new Thread(this).start();
    }
    else if ("quit".equals(cmd)) {
      programQuit = true;
      new Thread() {
        public void run() { dispose(); }
      }.start();
    }
  }

  public void run() {
    convert.setEnabled(false);
    try {
      String[] in = {input.getText()};
      String out = output.getText();
      boolean mergeRGB = rgb.isSelected();
      int ndx = in[0].indexOf("<1-2>");
      if (ndx >= 0) {
        String pre = in[0].substring(0, ndx);
        String post = in[0].substring(ndx + 5);
        in = new String[] {
          pre + "2" + post, // green
          pre + "1" + post  // red
        };
      }
      int planesPerFile = reader.getImageCount(in[0]);
      int numPlanes = planesPerFile * in.length;

      if (mergeRGB) { // merged RGB stack
        int np2 = numPlanes / 2;
        byte[][] data = null;
        progress.setMaximum(3 * np2);
        for (int i=0; i<np2; i++) {
          progress.setValue(3 * i);
          int q1 = i / planesPerFile;
          int c1 = i % planesPerFile;
          int q2 = (i + np2) / planesPerFile;
          int c2 = (i + np2) % planesPerFile;
          progress.setString(i + "/" + np2);
          BufferedImage ig = reader.open(in[q1], c1);
          progress.setValue(3 * i + 1);
          BufferedImage ir = reader2.open(in[q2], c2);
          progress.setValue(3 * i + 2);
          int width = ig.getWidth(), height = ig.getHeight();
          if (data == null || data[0].length != width * height) {
            data = new byte[3][width * height];
          }
          for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
              int q = y * width + x;
              int r = ir.getRGB(x, y) % 256;
              int g = ig.getRGB(x, y) % 256;
              data[0][q] = (byte) r;
              data[1][q] = (byte) g;
            }
          }
          BufferedImage img = ImageTools.makeImage(data, width, height);
          writer.save(out, img, programQuit || i == np2 - 1);
          if (programQuit) break;
        }
        progress.setValue(3 * np2);
      }
      else { // split Leica stack
        progress.setMaximum(2 * numPlanes);
        for (int i=0; i<numPlanes; i++) {
          progress.setValue(2 * i);
          int q = i / planesPerFile;
          int c = i % planesPerFile;
          progress.setString(i + "/" + numPlanes);
          Image img = reader.open(in[q], c);
          Hashtable ifd = null;
          if (i == 0) {
            ifd = new Hashtable();
            StringBuffer sb = new StringBuffer();
            sb.append("[GLOBAL]");
            sb.append(NL);
            sb.append("ImageWidth=");
            Dimension size = ImageTools.getSize(img);
            sb.append(size.width);
            sb.append(NL);
            sb.append("ImageLength=");
            sb.append(size.height);
            sb.append(NL);
            sb.append("NumOfFrames=");
            sb.append(numPlanes / 2);
            sb.append(NL);
            sb.append("BitsPerSample=8");
            sb.append(NL);
            sb.append("SamplesPerPixel=1");
            sb.append(NL);
            Object ome = reader.getOMENode(in[q]);
            Float pixelSizeX = OMETools.getPixelSizeX(ome);
            if (pixelSizeX != null) {
              sb.append("VoxelSizeX=");
              sb.append(pixelSizeX);
              sb.append(NL);
            }
            Float pixelSizeY = OMETools.getPixelSizeY(ome);
            if (pixelSizeY != null) {
              sb.append("VoxelSizeY=");
              sb.append(pixelSizeY);
              sb.append(NL);
            }
            Float pixelSizeZ = OMETools.getPixelSizeZ(ome);
            if (pixelSizeZ != null) {
              sb.append("VoxelSizeZ=");
              sb.append(pixelSizeZ);
              sb.append(NL);
            }
            TiffTools.putIFDValue(ifd,
              TiffTools.IMAGE_DESCRIPTION, sb.toString());
          }
          progress.setValue(2 * i + 1);
          writer.saveImage(out, img, ifd, programQuit || i == numPlanes - 1);
          if (programQuit) break;
        }
        progress.setValue(2 * numPlanes);
      }
      reader.close();
      reader2.close();
    }
    catch (Exception exc) {
      String err = exc.getMessage();
      if (err == null) err = exc.getClass().getName();
      JOptionPane.showMessageDialog(this, "Sorry, an error occurred: " +
        err, "Leica Converter", JOptionPane.ERROR_MESSAGE);
    }
    progress.setString("");
    progress.setValue(0);
    convert.setEnabled(true);
  }

  public static void main(String[] args) {
    LeicaConverter lc = new LeicaConverter();
    lc.pack();
    lc.show();
  }

}
