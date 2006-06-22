//
// QTConverter.java
//

package loci.qt;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import loci.formats.FormatReader;
import loci.formats.LegacyQTReader;
import loci.formats.LegacyQTTools;
import loci.formats.QTReader;
import loci.formats.TiffReader;
import loci.formats.TiffWriter;
import loci.util.FilePattern;

/** A utility for converting 4D QuickTime movies into 4D TIFF stacks. */
public class QTConverter extends JFrame implements ActionListener, Runnable {

  // -- Fields --

  private LegacyQTReader legacyReader = new LegacyQTReader();
  private QTReader qtReader = new QTReader();
  private TiffReader tiffReader = new TiffReader();
  private TiffWriter writer = new TiffWriter();
  private JFileChooser rc = qtReader.getFileChooser();
  private JFileChooser wc = writer.getFileChooser();
  private boolean shutdown;

  private JTextField input, output;
  private JCheckBox qtJava, swapAxes;
  private JProgressBar progress;
  private JButton convert;


  // -- Constructor --

  public QTConverter() {
    super("QuickTime to TIFF Converter");
    JPanel pane = new JPanel();
    pane.setBorder(new EmptyBorder(5, 5, 5, 5));
    pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
    setContentPane(pane);

    // -- Row 1 --

    JPanel row1 = new RowPanel();
    row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));
    pane.add(row1);

    pane.add(Box.createVerticalStrut(4));

    JLabel inputLabel = new JLabel("Input");
    row1.add(inputLabel);

    row1.add(Box.createHorizontalStrut(4));

    input = new JTextField(44);
    row1.add(input);
    limitHeight(input);

    row1.add(Box.createHorizontalStrut(4));

    JButton chooseInput = new JButton("Choose");
    row1.add(chooseInput);
    chooseInput.setActionCommand("input");
    chooseInput.addActionListener(this);

    row1.add(Box.createHorizontalStrut(4));

    // -- Row 2 --

    JPanel row2 = new RowPanel();
    row2.setLayout(new BoxLayout(row2, BoxLayout.X_AXIS));
    pane.add(row2);

    pane.add(Box.createVerticalStrut(9));

    JLabel outputLabel = new JLabel("Output");
    row2.add(outputLabel);
    inputLabel.setPreferredSize(outputLabel.getPreferredSize());

    row2.add(Box.createHorizontalStrut(4));

    output = new JTextField(44);
    row2.add(output);
    limitHeight(output);

    row2.add(Box.createHorizontalStrut(4));

    JButton chooseOutput = new JButton("Choose");
    row2.add(chooseOutput);
    chooseOutput.setActionCommand("output");
    chooseOutput.addActionListener(this);

    row2.add(Box.createHorizontalStrut(4));

    // -- Row 3 --

    JPanel row3 = new RowPanel();
    row3.setLayout(new BoxLayout(row3, BoxLayout.X_AXIS));
    pane.add(row3);

    pane.add(Box.createVerticalStrut(4));

    boolean canDoQT = new LegacyQTTools().canDoQT();
    qtJava = new JCheckBox("Use QTJava", canDoQT);
    qtJava.setEnabled(canDoQT);
    row3.add(qtJava);

    row3.add(Box.createHorizontalStrut(3));

    swapAxes = new JCheckBox("Swap axes", true);
    row3.add(swapAxes);

    row3.add(Box.createHorizontalStrut(8));

    progress = new JProgressBar();
    progress.setString("");
    progress.setStringPainted(true);
    row3.add(progress);

    row3.add(Box.createHorizontalStrut(8));

    convert = new JButton("Convert");
    row3.add(convert);
    convert.setActionCommand("convert");
    convert.addActionListener(this);

    row3.add(Box.createHorizontalStrut(4));

    JButton quit = new JButton("Quit");
    row3.add(quit);
    quit.setActionCommand("quit");
    quit.addActionListener(this);

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setLocation(100, 100);
    pack();
  }


  // -- ActionListener methods --

  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if ("input".equals(cmd)) {
      int rval = rc.showOpenDialog(this);
      if (rval != JFileChooser.APPROVE_OPTION) return;
      File file = rc.getSelectedFile();
      String pattern = FilePattern.findPattern(file);
      input.setText(pattern);
    }
    else if ("output".equals(cmd)) {
      int rval = wc.showSaveDialog(this);
      if (rval != JFileChooser.APPROVE_OPTION) return;
      File file = wc.getSelectedFile();
      String s = file.getPath();

      // determine file extension
      String name = file.getName();
      int dot = name.lastIndexOf(".");
      String ext = dot < 0 ? "" : name.substring(dot);

      boolean isTiff = writer.isThisType(s);
      if (dot >= 0) s = s.substring(0, s.lastIndexOf("."));
      s += "*" + ext;
      if (!isTiff) s += ".tif";
      output.setText(s);
    }
    else if ("convert".equals(cmd)) {
      new Thread(this).start();
    }
    else if ("quit".equals(cmd)) {
      shutdown = true;
      new Thread() {
        public void run() { dispose(); }
      }.start();
    }
  }


  // -- Runnable methods --

  public void run() {
    convert.setEnabled(false);
    progress.setString("Getting ready");
    try {
      String in = input.getText();
      String out = output.getText();

      FilePattern fp = new FilePattern(in);
      String[] inFiles = fp.getFiles();

      FormatReader reader = null;
      boolean useQTJ = qtJava.isSelected();
      reader = useQTJ ? (FormatReader) legacyReader : (FormatReader) qtReader;

      int numT = reader.getImageCount(inFiles[0]);
      int numZ = inFiles.length;
      boolean swap = swapAxes.isSelected();
      int outFiles = swap ? numT : numZ;
      int outPlanes = swap ? numZ : numT;

      int star = out.lastIndexOf("*");
      if (star < 0) {
        msg("Please use an asterisk in the output name " +
          "to indicate where the file numbers should go.");
        convert.setEnabled(true);
        return;
      }
      String pre = out.substring(0, star);
      String post = out.substring(star + 1);

      progress.setMaximum(2 * numZ * numT);
      long start = System.currentTimeMillis();

      FormatReader[] readers = null;
      TiffWriter[] writers = null;
      if (swap) {
        if (useQTJ) {
          // write to multiple files at once, for efficiency
          // (to avoid multiple simultaneous QTJava readers,
          // or storing multiple images in RAM simultaneously)
          writers = new TiffWriter[outFiles];
          writers[0] = writer;
          for (int i=1; i<writers.length; i++) writers[i] = new TiffWriter();
        }
        else {
          // read from multiple files at once, for efficiency
          // (to avoid writing to multiple files at once,
          // or storing multiple images in RAM simultaneously)
          readers = new FormatReader[inFiles.length];
          readers[0] = reader;
          for (int i=1; i<readers.length; i++) readers[i] = new QTReader();
        }
      }

      int digits = ("" + outFiles).length();
      if (swap && useQTJ) {
        // for each input file, write to all output files
        for (int p=0; p<outPlanes; p++) {
          for (int o=0; o<outFiles; o++) {
            String num = "" + (o + 1);
            while (num.length() < digits) num = "0" + num;
            String outFile = pre + num + post;
            String outName = new File(outFile).getName();
            progress.setString(outName + " " + (p + 1) + "/" + outPlanes);
            int value = 2 * (p * outFiles + o);
            progress.setValue(value);
            BufferedImage img =
              reader.openImage(inFiles[p], o); // p <=> Z, o <=> T
            progress.setValue(value + 1);
            writers[o].save(outFile, img, shutdown || p == outPlanes - 1);
            if (shutdown) break;
          }
          if (shutdown) break;
        }
      }
      else {
        for (int o=0; o<outFiles; o++) {
          String num = "" + (o + 1);
          while (num.length() < digits) num = "0" + num;
          String outFile = pre + num + post;
          String outName = new File(outFile).getName();
          for (int p=0; p<outPlanes; p++) {
            progress.setString(outName + " " + (p + 1) + "/" + outPlanes);
            int value = 2 * (o * outPlanes + p);
            progress.setValue(value);
            BufferedImage img = swap ?
              readers[p].openImage(inFiles[p], o) : // p <=> Z, o <=> T
              reader.openImage(inFiles[o], p); // p <=> T, o <=> Z
            progress.setValue(value + 1);
            writer.save(outFile, img, shutdown || p == outPlanes - 1);
            if (shutdown) break;
          }
          if (shutdown) break;
        }
      }
      progress.setValue(2 * numZ * numT);
      progress.setString("Finishing");
      if (readers == null) reader.close();
      else for (int i=0; i<readers.length; i++) readers[i].close();

      long end = System.currentTimeMillis();
      double time = (end - start) / 1000.0;
      long avg = (end - start) / (numZ * numT);
      progress.setString(time + " s elapsed (" + avg + " ms/plane)");
      progress.setValue(0);
    }
    catch (Exception exc) {
      exc.printStackTrace();
      String err = exc.getMessage();
      if (err == null) err = exc.getClass().getName();
      msg("Sorry, an error occurred: " + err);
      progress.setString("");
      progress.setValue(0);
    }
    convert.setEnabled(true);
  }


  // -- Helper methods --

  private void limitHeight(JComponent jc) {
    int w = jc.getMaximumSize().width;
    int h = jc.getPreferredSize().height;
    jc.setMaximumSize(new Dimension(w, h));
  }

  private void msg(String msg) {
    JOptionPane.showMessageDialog(this, msg,
      "QuickTime to TIFF Converter", JOptionPane.ERROR_MESSAGE);
  }


  // -- Helper classes --

  private class RowPanel extends JPanel {
    public Dimension getMaximumSize() {
      int w = super.getMaximumSize().width;
      int h = getPreferredSize().height;
      return new Dimension(w, h);
    }
  }


  // -- Main method --

  public static void main(String[] args) {
    new QTConverter().setVisible(true);
  }

}
