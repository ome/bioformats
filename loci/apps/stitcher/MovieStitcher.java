//
// MovieStitcher.java
//

/*
Coded in 2006-@year@ by Curtis Rueden.
Permission is granted to use this code for anything.
*/

package loci.apps.stitcher;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.*;
import java.io.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import loci.formats.*;
import loci.formats.out.TiffWriter;
import org.openmicroscopy.xml.*;
import org.openmicroscopy.xml.st.*;

/**
 * A utility for reorganizing and converting QuickTime movies,
 * TIFF series and other 4D datasets.
 */
public class MovieStitcher extends JFrame implements 
  ActionListener, ChangeListener, Runnable 
{

  // -- Constants --

  private static final String TITLE = "Movie Stitcher";
  private static final int COLUMNS = 24;

  // -- Fields --

  private FileStitcher reader = new FileStitcher(true);
  private ImageWriter writer = new ImageWriter();
  private JFileChooser rc, wc;
  private boolean shutdown, force = true;

  private JTextField input, output;
  private JCheckBox qtJava, forceType, includeZ, includeT, includeC;
  private JLabel zLabel, tLabel, cLabel;
  private JComboBox zChoice, tChoice, cChoice, codec;
  private JSpinner fps, series;
  private JPanel seriesRow;
  private JProgressBar progress;
  private JButton convert;

  // -- Constructor --

  public MovieStitcher() {
    super(TITLE);

    // file choosers
    rc = reader.getFileChooser();
    wc = writer.getFileChooser();

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

    input = new JTextField(COLUMNS);
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

    output = new JTextField(COLUMNS);
    row2.add(output);
    limitHeight(output);

    row2.add(Box.createHorizontalStrut(4));

    JButton chooseOutput = new JButton("Choose");
    row2.add(chooseOutput);
    chooseOutput.setActionCommand("output");
    chooseOutput.addActionListener(this);

    row2.add(Box.createHorizontalStrut(4));

    // save a blank row here
    seriesRow = new RowPanel();
    seriesRow.setLayout(new BoxLayout(seriesRow, BoxLayout.X_AXIS));
    pane.add(seriesRow);

    // -- Row 3 --

    JPanel row3 = new RowPanel();
    row3.setLayout(new BoxLayout(row3, BoxLayout.X_AXIS));
    pane.add(row3);

    pane.add(Box.createVerticalStrut(9));

    String[] axisNames = new String[] {"Time", "Slice", "Channel"};

    zLabel = new JLabel(" 1) Slice       <0-0>");
    tLabel = new JLabel(" 2) Time      <0-0>");
    cLabel = new JLabel(" 3) Channel <0-0>");

    zChoice = new JComboBox(axisNames);
    zChoice.setSelectedIndex(1);
    zChoice.setPreferredSize(new Dimension(5, 9));
    zChoice.setActionCommand("zChoice");
    zChoice.addActionListener(this);

    tChoice = new JComboBox(axisNames);
    tChoice.setSelectedIndex(0);
    tChoice.setPreferredSize(new Dimension(5, 9));
    tChoice.setActionCommand("tChoice");
    tChoice.addActionListener(this);

    cChoice = new JComboBox(axisNames);
    cChoice.setSelectedIndex(2);
    cChoice.setPreferredSize(new Dimension(5, 9));
    cChoice.setActionCommand("cChoice");
    cChoice.addActionListener(this);

    includeZ = new JCheckBox("Include in file");
    includeZ.setEnabled(false);
    includeT = new JCheckBox("Include in file");
    includeT.setEnabled(false);
    includeC = new JCheckBox("Include in file");
    includeC.setEnabled(false);

    row3.add(zLabel);
    row3.add(zChoice);
    row3.add(includeZ);
    row3.add(Box.createHorizontalStrut(4));
    
    row3 = new RowPanel();
    row3.setLayout(new BoxLayout(row3, BoxLayout.X_AXIS));
    pane.add(row3);
    pane.add(Box.createVerticalStrut(9));
    
    row3.add(tLabel);
    row3.add(tChoice);
    row3.add(includeT);
    row3.add(Box.createHorizontalStrut(4));
    
    row3 = new RowPanel();
    row3.setLayout(new BoxLayout(row3, BoxLayout.X_AXIS));
    pane.add(row3);
    pane.add(Box.createVerticalStrut(9));
    
    row3.add(cLabel);
    row3.add(cChoice);
    row3.add(includeC);

    row3.add(Box.createHorizontalStrut(4));

    // -- Row 4 --

    JPanel row4 = new RowPanel();
    row4.setLayout(new BoxLayout(row4, BoxLayout.X_AXIS));
    pane.add(row4);

    pane.add(Box.createVerticalStrut(9));

    JLabel fpsLabel = new JLabel("Frames per second: ");
    row4.add(fpsLabel);
    fps = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
    row4.add(fps);
    row4.add(Box.createHorizontalStrut(3));
  
    JLabel codecLabel = new JLabel("Output compression type: ");
    row4.add(codecLabel);
    codec = new JComboBox(new String[0]);
    row4.add(codec);

    // -- Row 5 --

    JPanel row5 = new RowPanel();
    row5.setLayout(new BoxLayout(row5, BoxLayout.X_AXIS));
    pane.add(row5);

    pane.add(Box.createVerticalStrut(9));

    boolean canDoQT = new LegacyQTTools().canDoQT();
    qtJava = new JCheckBox("Use QTJava", canDoQT);
    qtJava.setEnabled(canDoQT);
    row5.add(qtJava);

    row5.add(Box.createHorizontalStrut(3));
   
    forceType = new JCheckBox("Force", true);
    forceType.setActionCommand("force");
    forceType.addActionListener(this);
    row5.add(forceType);

    row5.add(Box.createHorizontalStrut(3));
    row5.add(Box.createHorizontalGlue());

    convert = new JButton("Convert");
    row5.add(convert);
    convert.setActionCommand("convert");
    convert.addActionListener(this);

    row5.add(Box.createHorizontalStrut(4));

    JButton quit = new JButton("Quit");
    row5.add(quit);
    quit.setActionCommand("quit");
    quit.addActionListener(this);

    // -- Row 6 --

    JPanel row6 = new RowPanel();
    row6.setLayout(new BoxLayout(row6, BoxLayout.X_AXIS));
    pane.add(row6);

    progress = new JProgressBar();
    progress.setString("");
    progress.setStringPainted(true);
    row6.add(progress);

    row6.add(Box.createHorizontalStrut(8));

    JLabel version = new JLabel("Built on @date@");
    version.setFont(version.getFont().deriveFont(Font.ITALIC));
    row6.add(version);

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
      if (file != null) wc.setCurrentDirectory(file);
      String pattern = FilePattern.findPattern(file);
      input.setText(pattern);
     
      try {
        if (reader.getSeriesCount(pattern) > 1 && series == null) {
          JLabel seriesLabel = new JLabel("Series: ");
          series = new JSpinner(new SpinnerNumberModel(1, 1, 
            reader.getSeriesCount(pattern), 1)); 
          series.addChangeListener(this);
          seriesRow.add(seriesLabel);
          seriesRow.add(series);
          pack();
        }
        else if (series != null) {
          ((SpinnerNumberModel) series.getModel()).setMaximum(
            new Integer(reader.getSeriesCount(pattern)));
          pack();
        }
        else if (reader.getSeriesCount(pattern) == 1 && series != null) {
          seriesRow.remove(series);
          series = null;
          pack();
        }
      }
      catch (Exception exc) {
        exc.printStackTrace();
      }
      
      updateLabels(pattern); 
    }
    else if ("output".equals(cmd)) {
      int rval = wc.showSaveDialog(this);
      if (rval != JFileChooser.APPROVE_OPTION) return;
      File file = wc.getSelectedFile();
      if (file != null) rc.setCurrentDirectory(file);
      String s = file.getPath();
      output.setText(s);
      try {
        if (!writer.canDoStacks(s)) {
          includeZ.setEnabled(false);
          includeT.setEnabled(false);
          includeC.setEnabled(false);
        }
        codec.removeAllItems();
        String[] codecs = writer.getWriter(s).getCompressionTypes();
        if (codecs == null) codecs = new String[] {"Uncompressed"};
        for (int i=0; i<codecs.length; i++) {
          codec.addItem(codecs[i]);
        }
      }
      catch (Exception exc) {
        exc.printStackTrace();
      }
    }
    else if ("zChoice".equals(cmd)) {
      String newName = (String) zChoice.getSelectedItem();
      String label = zLabel.getText();
      String oldName = label.substring(4, label.indexOf(" ", 4));
      label = label.replaceAll(oldName, newName);
      zLabel.setText(label);
    }
    else if ("tChoice".equals(cmd)) {
      String newName = (String) tChoice.getSelectedItem();
      String label = tLabel.getText();
      String oldName = label.substring(4, label.indexOf(" ", 4));
      label = label.replaceAll(oldName, newName);
      tLabel.setText(label);
    }
    else if ("cChoice".equals(cmd)) {
      String newName = (String) cChoice.getSelectedItem();
      String label = cLabel.getText();
      String oldName = label.substring(4, label.indexOf(" ", 4));
      label = label.replaceAll(oldName, newName);
      cLabel.setText(label);
    }
    else if ("convert".equals(cmd)) new Thread(this).start();
    else if ("force".equals(cmd)) {
      force = forceType.isSelected();
    }
    else if ("quit".equals(cmd)) {
      shutdown = true;
      new Thread() {
        public void run() { dispose(); }
      }.start();
    }
  }
 
  // -- ChangeListener methods --

  public void stateChanged(ChangeEvent e) {
    if (e.getSource() == series) {
      try {
        reader.setSeries(reader.getCurrentFile(), 
          ((Integer) series.getValue()).intValue() - 1);
        updateLabels(input.getText());
      }
      catch (Exception exc) {
        exc.printStackTrace();
      }
    }
  }


  // -- Runnable methods --

  public void run() {
    convert.setEnabled(false);
    includeZ.setEnabled(false);
    includeT.setEnabled(false);
    includeC.setEnabled(false);
    zChoice.setEnabled(false);
    tChoice.setEnabled(false);
    cChoice.setEnabled(false);
    input.setEditable(false);
    fps.setEnabled(false);
    if (series != null) series.setEnabled(false);
    forceType.setEnabled(false);
    codec.setEnabled(false);

    progress.setString("Getting ready");
    try {
      String in = input.getText();
      String out = output.getText();
      if (in.trim().equals("")) {
        msg("Please specify input files.");
        convert.setEnabled(true);
        progress.setString("");
        return;
      }
      if (out.trim().equals("")) {
        File f = new File(in);
        String name = new FilePattern(in).getPrefix();
        String stitchDir = name + "-stitched";
        new File(f.getParent() + File.separator + stitchDir).mkdir();
        out = f.getParent() + File.separator + stitchDir + 
          File.separator + name + ".tif"; 
        out = out.replaceAll(File.separator + File.separator, File.separator);
        output.setText(out);
      }
      output.setEditable(false);
 
      if (series != null) {
        reader.setSeries(in, ((Integer) series.getValue()).intValue() - 1);
      }

      writer.setFramesPerSecond(((Integer) fps.getValue()).intValue());
      try {
        writer.getWriter(out).setCompression((String) codec.getSelectedItem());
      }
      catch (NullPointerException npe) { }

      boolean isQT = reader.getFormat().equals("QuickTime");
      boolean useQTJ = isQT && qtJava.isSelected();
      //((QTReader) reader.getReader(QTReader.class)).setLegacy(useQTJ);

      // swap dimensions based on user input

      String order = reader.getDimensionOrder(in);
      
      if (zLabel.getText().indexOf("Time") != -1) {
        order = order.replace('Z', 'T') ;
      }
      else if (zLabel.getText().indexOf("Channel") != -1) {
        order = order.replace('Z', 'C');
      }

      if (tLabel.getText().indexOf("Slice") != -1) {
        order = order.replace('T', 'Z');
      }
      else if (tLabel.getText().indexOf("Channel") != -1) {
        order = order.replace('T', 'C');
      }

      if (cLabel.getText().indexOf("Time") != -1) {
        order = order.replace('C', 'T');
      }
      else if (cLabel.getText().indexOf("Slice") != -1) {
        order = order.replace('C', 'Z');
      }

      reader.swapDimensions(in, order);

      OMEXMLMetadataStore store = new OMEXMLMetadataStore();
      store.createRoot();
      reader.close();
      reader.setMetadataStore(store);

      // determine internal and external dimensions for each axis

      int internalZ = includeZ.isSelected() ? reader.getSizeZ(in) : 1;
      int internalT = includeT.isSelected() ? reader.getSizeT(in) : 1;
      int internalC = includeC.isSelected() ? reader.getEffectiveSizeC(in) : 1;

      int externalZ = includeZ.isSelected() ? 1 : reader.getSizeZ(in);
      int externalT = includeT.isSelected() ? 1 : reader.getSizeT(in);
      int externalC = includeC.isSelected() ? 1 : reader.getEffectiveSizeC(in);

      int numFiles = externalZ * externalT * externalC;

      int zDigits = ("" + externalZ).length();
      int tDigits = ("" + externalT).length();
      int cDigits = ("" + externalC).length();

      progress.setMaximum(2 * reader.getImageCount(in));

      int star = out.lastIndexOf(".");
      if (star < 0) star = out.length();
      String pre = out.substring(0, star);
      String post = out.substring(star);

      // determine appropriate pixel type

      int type = reader.getPixelType(in);
      if (force && !writer.isSupportedType(out, type)) {
        int[] types = writer.getPixelTypes(out);
        for (int i=0; i<types.length; i++) {
          if (types[i] > type) {
            if (i == 0) {
              type = types[i];
              break;
            }
            else {
              type = types[i - 1];
              break;
            }
          }
          if (i == types.length - 1) type = types[i];
        }
      }
      else if (!force && !writer.isSupportedType(out, type)) {
        throw new FormatException("Unsupported pixel type: " + 
          FormatReader.getPixelTypeString(type) +
          "\nTo write to this format, the \"force\" box must be checked.\n" +
          "This may result in a loss of precision; for best results, " +
          "convert to TIFF instead.");
      }

      long start = System.currentTimeMillis();
      int plane = 0;

      for (int i=0; i<externalZ; i++) {
        for (int j=0; j<externalT; j++) {
          for (int k=0; k<externalC; k++) {
            // construct the numeric blocks
            String zBlock = "";
            String tBlock = "";
            String cBlock = "";
            
            if (externalZ > 1) {
              String num = "" + i;
              while (num.length() < zDigits) num = "0" + num;
              zBlock = "Z" + num + "_";
            }
            if (externalT > 1) {
              String num = "" + j;
              while (num.length() < tDigits) num = "0" + num;
              tBlock = "T" + num + "_";
            }
            if (externalC > 1) {
              String num = "" + k;
              while (num.length() < cDigits) num = "0" + num;
              cBlock = "C" + num;
            }

            String outFile = pre;
            if (zBlock.length() > 1) outFile += zBlock;
            if (tBlock.length() > 1) outFile += tBlock;
            if (cBlock.length() > 1) outFile += cBlock;
            if (outFile.endsWith("_")) {
              outFile = outFile.substring(0, outFile.length() - 1);
            }
            outFile += post;

            String outName = new File(outFile).getName();

            int planesPerFile = internalZ * internalT * internalC;
            int filePlane = 0;
    
            for (int z=0; z<internalZ; z++) {
              for (int t=0; t<internalT; t++) {
                for (int c=0; c<internalC; c++) {
                  int zPos = z*externalZ + i;
                  int tPos = t*externalT + j;
                  int cPos = c*externalC + k;
                 
                  progress.setString(outName + " " + (filePlane + 1) +
                    "/" + planesPerFile);
                  filePlane++;
                  progress.setValue(2 * (plane + 1));
                  plane++;
                  int ndx = reader.getIndex(in, zPos, cPos, tPos);

                  BufferedImage img = reader.openImage(in, ndx);
                  if (force && 
                    !writer.isSupportedType(out, reader.getPixelType(in))) 
                  {
                    int pixelType = 0;
                    switch (type) {
                      case FormatReader.INT8:
                      case FormatReader.UINT8: 
                        pixelType = DataBuffer.TYPE_BYTE;
                        break;
                      case FormatReader.INT16: 
                        pixelType = DataBuffer.TYPE_USHORT;
                        break;
                      case FormatReader.UINT16: 
                        pixelType = DataBuffer.TYPE_SHORT;
                        break;
                      case FormatReader.INT32:
                      case FormatReader.UINT32: 
                        pixelType = DataBuffer.TYPE_INT;
                        break;
                      case FormatReader.FLOAT: 
                        pixelType = DataBuffer.TYPE_FLOAT;
                        break;
                      case FormatReader.DOUBLE:
                        pixelType = DataBuffer.TYPE_DOUBLE;
                        break; 
                    }
                    img = ImageTools.makeType(img, pixelType);
                  }

                  writer.save(outFile, img, shutdown || 
                    (filePlane == planesPerFile));
                  if (shutdown) break;
                }
              }
            }
          
            // if we're writing a TIFF file, insert an OME-XML block 
            if (writer.getWriter(outFile) instanceof TiffWriter) {
              RandomAccessFile raf = new RandomAccessFile(outFile, "rw");
              
              OMENode root = (OMENode) 
                ((OMEXMLMetadataStore) reader.getMetadataStore(in)).getRoot();
                
              // add TiffData element here 
              Vector images = root.getChildNodes("Image");
              for (int p=0; p<images.size(); p++) {
                PixelsNode pix = 
                  (PixelsNode) ((ImageNode) images.get(p)).getDefaultPixels();
                DOMUtil.createChild(pix.getDOMElement(), "TiffData");
              }

              TiffTools.overwriteIFDValue(raf, 0, TiffTools.IMAGE_DESCRIPTION,
                root.writeOME(true));
              raf.close();
            }
          }
        }
      }

      progress.setValue(2 * reader.getImageCount(in));
      progress.setString("Finishing");
      if (writer != null) writer.close();

      long end = System.currentTimeMillis();
      double time = (end - start) / 1000.0;
      long avg = (end - start) / (reader.getImageCount(in));
      progress.setString(time + " s elapsed (" + avg + " ms/plane)");
      progress.setValue(0);
      if (reader != null) reader.close();
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
    includeZ.setEnabled(true);
    includeT.setEnabled(true);
    includeC.setEnabled(true);
    zChoice.setEnabled(true);
    tChoice.setEnabled(true);
    cChoice.setEnabled(true);
    input.setEditable(true);
    output.setEditable(true);
    fps.setEnabled(true);
    if (series != null) series.setEnabled(true);
    forceType.setEnabled(true);
    codec.setEnabled(true);
  }

  // -- Helper methods --

  private void limitHeight(JComponent jc) {
    int w = jc.getMaximumSize().width;
    int h = jc.getPreferredSize().height;
    jc.setMaximumSize(new Dimension(w, h));
  }

  private void msg(String msg) {
    JOptionPane.showMessageDialog(this, msg, TITLE, JOptionPane.ERROR_MESSAGE);
  }

  private void updateLabels(String pattern) {
    try {
      String z = zLabel.getText();
      z = z.substring(0, z.indexOf("<"));
      z += "<1-" + reader.getSizeZ(pattern) + ">";
      zLabel.setText(z);

      String t = tLabel.getText();
      t = t.substring(0, t.indexOf("<"));
      t += "<1-" + reader.getSizeT(pattern) + ">";
      tLabel.setText(t);

      String c = cLabel.getText();
      c = c.substring(0, c.indexOf("<"));
      c += "<1-" + reader.getEffectiveSizeC(pattern) + ">";
      cLabel.setText(c);
      
      includeZ.setEnabled(true);
      includeT.setEnabled(true);
      includeC.setEnabled(true);
    }
    catch (Exception exc) {
      exc.printStackTrace();
    }
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
    new MovieStitcher().setVisible(true);
  }

}
