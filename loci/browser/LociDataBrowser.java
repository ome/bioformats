//
// LociDataBrowser.java
//

// YTW 2/27/2006: hardcode deconvolve6_Pseudo_Tl000_Zs000.TIF
// YTW 3/1/2006: rewrite file to make better use of FilePattern

// MELISSA 03/15/2006: twoDimView takes a parameter and has public access;
//                     tweaked some of the logic in showSlice methods to allow
//                     use with OME plugin
// MELISSA 03/17/2006: added logic to preserve file's description

package loci.browser;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.io.FileInfo;
import ij.io.OpenDialog;
import ij.io.Opener;
import ij.measure.Calibration;
import ij.plugin.PlugIn;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import javax.swing.JScrollBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.awt.Panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.ColorModel;
import java.awt.event.WindowEvent;
import java.io.File;

import java.util.Hashtable;

import loci.ome.MetaPanel;
import loci.util.FilePattern;

/**
 * LociDataBrowser is a plugin for ImageJ that allows for browsing of 4D
 * image data (stacks of image planes over time) with two-channel support.
 *
 * @author Francis Wong yutaiwong at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class LociDataBrowser implements PlugIn {

  // private static data members
  private static boolean grayscale = false;
  private static double scale = 100.0;

  // private data members
  private FileInfo fi;
  private String description;
  private int n, start, increment;
  private String filter;
  private String info1;
  private int numFiles;  // number of images files
  private ImagePlus imp1;
  private ImageStack stack1;
  private int stackSize;
  private boolean hasTrans;  // transmitted channel?
  private boolean hasTP;
  private boolean hasZ;
  private int depth = 0; // number of "slices" in each image file
  private int depth2 = 0;
  private String lab3D;
  private String lab4D;
  private boolean cb4tp;
  private String firstPrefix;
  private int dim = 4;

  private int[] listTP;
  private int[] listC;
  private int[] listZ;

  private int idxList;
  private boolean animating = false;
  private int tpStep;

  private static String[] preTime={"_TP", "_Tl"};
  private static String[] preZ = {"_Z", "_Zs"};
  private static String[] preTrans = {"_C"};

  private static final boolean DEBUG = true;

  // stores the description of each image
  private static Hashtable extraData;

  private static String d;

  public void run(String arg) {
    OpenDialog od = new OpenDialog("Open Sequence of Image Stacks:", "");
    String directory = od.getDirectory();
    String name = od.getFileName();
    if (name == null) return;
    if (extraData == null) extraData = new Hashtable();

    // Find all the files having similar names (Using FilePattern class)
    String pattern = FilePattern.findPattern(new File(name), directory);
    // avoids NullPointerException if there is no suffix
    if (pattern == null) pattern = "";
    FilePattern fp = new FilePattern(pattern);
    String[] oldPrefixes = fp.getPrefixes();
    String[] prefixes = oldPrefixes;
    String [] absList = fp.getFiles();     // all the image files

    for (int i=0; i<prefixes.length; i++) {
      System.err.println("prefixes["+i+"] := "+prefixes[i]);
    }
    // find out what axes exist
    hasTP = false;
    hasZ = false;
    hasTrans = false;
    tpStep = 1;

    String [] list = new String[absList.length];
    int dirIndex = absList[0].lastIndexOf(File.separatorChar);
    for (int i=0; i < absList.length; i++) {
      list[i] = absList[i].substring(dirIndex+1);
    }
    // YTW Nov 21 2005
    // Multichannel support
    numFiles = list.length;

    listTP = new int[numFiles];
    listC  = new int[numFiles];
    listZ = new int[numFiles];
    for (int i=0; i<numFiles; i++) {
      listTP[i] = -1;
      listC[i] = -1;
      listZ[i] = -1;
    }

    int idxTP = -1;
    int idxC = -1;
    int idxZ = -1;

    if (numFiles == 1) {
      hasTP = false;
      hasZ = true;
      hasTrans = false;
    }
    else if (prefixes.length > 0) {

      for (int i=0; i<prefixes.length; i++) {
        System.err.println("Prefixes["+i+"] = "+prefixes[i]);
        prefixes[i]=prefixes[i].replaceAll("\\d+$", "");
      }
      
      
      firstPrefix = prefixes[0];
      System.err.println(firstPrefix);
      for (int i=0; i<prefixes.length; i++) {
        for (int j=0; j<preTime.length; j++) {
          if (prefixes[i].endsWith(preTime[j])) {
              hasTP = true;
              idxTP = i;
          }
        }
        for (int j=0; j<preZ.length; j++) {
          if (prefixes[i].endsWith(preZ[j])) {
              hasZ = true;
              idxZ = i;
          }
        }
        for (int j=0; j<preTrans.length; j++) {
          if (prefixes[i].endsWith(preTrans[j])) {
              hasTrans = true;
              idxC = i;
          }
        }
      }

      System.err.println(hasTrans);
      int [] repeat = new int[prefixes.length];
      repeat[0] = 1;

      for (int i=1; i<repeat.length; i++) {
        repeat[i] = fp.getCount()[i-1] * repeat[i-1];
      }

      int [][] indices = new int[prefixes.length][list.length];
      if (DEBUG) {
        System.err.println("idxTP = "+idxTP);
        System.err.println("idxC = "+idxC);
        System.err.println("idxZ = "+idxZ);
      }
      for (int i=0; i<list.length; i++) {
        for (int j=0; j<prefixes.length; j++) {
          if (j>0) indices[j][i] = (i/fp.getCount()[j-1])%fp.getCount()[j];
          else indices[j][i] = i%fp.getCount()[0];
        }
      }

      if (idxTP != -1) listTP = indices[idxTP];
      if (idxC != -1) listC = indices[idxC];
      if (idxZ != -1) listZ = indices[idxZ];

      if (DEBUG) {
        for (int i=0; i<prefixes.length; i++) {
          for (int j=0; j<list.length; j++) {
            System.err.print(indices[i][j] + " ");
          }
          System.err.println();
        }
        System.err.print("listZ = ");
        for (int i=0; i<listZ.length; i++) System.err.print(listZ[i]+" ");
        System.err.println();
        System.err.print("listTP = ");
        for (int i=0; i<listTP.length; i++) System.err.print(listTP[i]+" ");
        System.err.println();
        System.err.print("listC = ");
        for (int i=0; i<listC.length; i++) System.err.print(listC[i]+" ");
      }

    }
    //if ((!hasZ && prefixes.length == 1 && numFiles > 1) ||
    //  (hasTP && prefixes.length == 2))
    //{
    //  hasZ = true;
    //}
    if (!hasTP && prefixes.length == 2) hasZ = true;
    else if (!hasZ && prefixes.length <= 1 && numFiles > 1) {
      hasTP = true;
      hasZ = false;
      //      hasTrans = false;
    }

    int ndx = fp.getPrefix().indexOf('_');
    ndx = fp.getPrefix().lastIndexOf('_');
    String prefix = "";
    if (ndx > 1) prefix = fp.getPrefix().substring(ndx-2, ndx);

    //for (int aa=0; aa<absList.length; aa++) System.err.println(absList[aa]);

    //int idxTP = 0;
    //int idxC = 0;
    idxList = 0;

    if (IJ.debugMode) {
      IJ.log("Wisc_Scan opening files: "+directory+" ("+list.length+" files)");
    }

    int width = 0, height = 0, type = 0;

    // points to a stack of images
    ImageStack stack = null;
    double min = Double.MAX_VALUE;
    double max = -Double.MAX_VALUE;

    try {
      // first loop: to gather image info
      // ** no image is imported yet **
      boolean valid = true;
      for (int i=0; i<list.length; i++) {
        if (valid) {
          if (list[i].endsWith(".txt")) continue;
          ImagePlus imp = new Opener().openImage(directory, list[i]);

          if (imp != null) { // gather all info on the image
            width = imp.getWidth();
            height = imp.getHeight();
            depth = imp.getStackSize();
            type = imp.getType();
            fi = imp.getOriginalFileInfo();
            description = fi.description;
            d = description;
            imp.setFileInfo(fi);
            cb4tp = list[0].indexOf("_C") < list[0].indexOf("_TP");
            start = 1;
            break;
          }
          else valid = false;
        }
      }

      if (width == 0) {  // invalid image file
        IJ.showMessage("Import Sequence",
          "This folder does not appear to contain any TIFF,\n"
          + "JPEG, BMP, DICOM, GIF, FITS or PGM files.");
        return;
      }

      // n is number of image files imported
      if (n < 1) n = list.length;
      if (start < 1 || start > list.length) start = 1; // ??
      if (start + n - 1 > list.length) n = list.length - start + 1;  // ??

      // filter
      int filteredImages = n;

      if (filter != null && (filter.equals("") || filter.equals("*"))) {
        filter = null;
      }

      if (filter != null) {
        filteredImages = 0;
        for (int i=start-1; i<start-1+n; i++) {
          if (list[i].indexOf(filter) >= 0) filteredImages++;
        }
        if (filteredImages == 0) {
          IJ.error("None of the "+n+" files contain\n the string '" +
            filter + "' in their name.");
          return;
        }
      }
      n = filteredImages; // number of images aft9er filtering
      n *= depth;         // multiplied by image depth (# slice in each image)

      // now n = total # slices

      int count = 0;
      int counter = 0;
      increment = 1;

      double progress = 0.0;
      double step = (double) 1/n;
      Double dbStep = new Double(step);

      for (int i=start-1; i<list.length; i++) {
        if (list[i].endsWith(".txt")) continue;
        if (filter != null && (list[i].indexOf(filter) < 0)) continue;
        if ((counter++%increment) != 0)  continue;

        // open the image in ImagePlus

        ImagePlus imp = new Opener().openImage(directory, list[i]);

        if (imp != null && stack == null) {  // first image in the stack
          width = imp.getWidth();
          height = imp.getHeight();
          type = imp.getType();
          ColorModel cm = imp.getProcessor().getColorModel();
          if (scale < 100.0) {
            stack = new ImageStack((int) (width*scale/100.0),
              (int) (height*scale/100.0), cm);
          }
          else stack = new ImageStack(width, height, cm);

          info1 = (String)imp.getProperty("Info");
          idxTP = list[i].indexOf("_TP");
          idxC = list[i].indexOf("_C");
        }

        if (imp == null) {
          // invalid image
          if (!list[i].startsWith(".")) IJ.log(list[i] + ": unable to open");
        }
        else if (imp.getWidth() != width || imp.getHeight() != height) {
          // current image dimension different than those in the stack
          IJ.log(list[i] + ": wrong dimensions");
        }
        // current image file type different than those in the stack
        else if (imp.getType() != type) IJ.log(list[i] + ": wrong type");
        else {
          if (!hasTP && depth == 1) hasTP = false;
          else if (depth == 1) hasZ = false;

          count = stack.getSize() + 1;  // update image counts

          // update number on screen that shows count
          //IJ.showStatus(count+"/"+n);
          //IJ.showProgress((double)count/n);

          // process every slice in each TIFF stack
          for (int iSlice=1; iSlice<=depth; iSlice++) {
            imp.setSlice(iSlice);
            ImageProcessor ip = imp.getProcessor();
            if (grayscale) {
              ImageConverter ic = new ImageConverter(imp);
              ic.convertToGray8();
              ip = imp.getProcessor();
            }
            if (scale < 100.0) {
              ip = ip.resize((int) (width*scale/100.0),
                (int) (height*scale/100.0));
            }
            if (ip.getMin() < min) min = ip.getMin();
            if (ip.getMax() > max) max = ip.getMax();
            String label = imp.getTitle();
            String info = (String) imp.getProperty("Info");
            if (info != null) label += "\n" + info;
            stack.addSlice(label, ip);
            IJ.showProgress(progress);
            progress += step;
          }
        }
        if (count >= n) break;
        //System.gc();
      }
    }
    catch (OutOfMemoryError e) {
      IJ.outOfMemory("Wisc_Scan");
      if (stack != null) stack.trim();
    }
    if (stack != null && stack.getSize() > 0) {
      ImagePlus imp2 = new ImagePlus(name, stack);
      if (imp2.getType() == ImagePlus.GRAY16 ||
        imp2.getType() == ImagePlus.GRAY32)
      {
        imp2.getProcessor().setMinAndMax(min, max);
      }
      //imp2.setFileInfo(fi); // saves FileInfo of the first image
      if (imp2.getStackSize() == 1 && info1 != null) {
        imp2.setProperty("Info", info1);
      }
      imp2.show();
    }
    IJ.showProgress(1.0);
    twoDimView(WindowManager.getCurrentImage());
  }

  public void twoDimView(ImagePlus imp) {
    String name;
    imp1 = imp;
    if (depth == 0) depth = imp1.getStackSize();

    if (numFiles == 0) {
      // HACK - this will only be true if we called twoDimView(blah)
      // from the OME plugin
      FileInfo fileInfo = imp1.getOriginalFileInfo();
      String info = fileInfo.info;
      hasTP = Boolean.valueOf(info.substring(0,
        info.indexOf(" "))).booleanValue();
      if (hasTP) {
        numFiles = Integer.parseInt(info.substring(info.indexOf(" ") + 1));
        numFiles = depth / numFiles;
        depth = depth / numFiles;
      }
      else numFiles = 1;
    }

    //imp1 = WindowManager.getCurrentImage();
    if (imp1 == null || (imp1.getStackSize() == 0)) {
      IJ.error("No stack is being selected. Exiting.");
      return;
    }

    stack1 = imp1.getStack();
    stackSize = stack1.getSize();
    name = imp1.getTitle();

    // is it a right assumption that each image has the same number of slices?
    lab3D = "z-depth";
    lab4D = "time";
    if (imp1 instanceof ImagePlus && imp1.getStackSize() > 1) initFrame();
  }

  void initFrame() {
    int w = imp1.getWidth();
    int h = imp1.getHeight();
    ImageStack stack2 = new ImageStack(w, h);
    for (int i=1; i<=imp1.getStackSize(); i++) {
      //IJ.showStatus(i+"/"+imp1.getStackSize());
      stack2.addSlice(null, stack1.getProcessor(i));
      //IJ.showProgress((double)i/stackSize);
    }
    ImageProcessor ip;
    ImagePlus imp2 = new ImagePlus(imp1.getTitle(), stack2);
    ImageCanvas ic = new ImageCanvas(imp2);
    new CustomWindow(imp2, ic, hasTrans, firstPrefix);
    imp1.hide();
  }

  boolean isContained(String[] pre1, String pre2) {
    for (int i=0; i<pre1.length; i++) {
      if (pre1[i].equals(pre2)) return true;
    }
    return false;
  }

  public static Hashtable getTable() { return extraData; }


  // CustomWindow class begin
  private class CustomWindow extends ImageWindow
    implements ActionListener, AdjustmentListener, ItemListener
  {
    // animation frame rate
    private int fps = 10;

    private JScrollBar sliceSel1;
    private JScrollBar sliceSel2;
    private int z = 1;
    private int t = 1;
    private boolean hasTrans;
    private boolean trans = false;
    private javax.swing.Timer animationTimer;
    private JSpinner frameRate;
    private JButton xmlButton;        // button to display OME-XML
    private JLabel label1, label2;

    /** CustomWindow constructors, initialisation */
    CustomWindow(ImagePlus imp, ImageCanvas ic,
      boolean hasTrans, String prefix)
    {
      super(imp, ic);
      this.hasTrans = hasTrans;
      this.setTitle(prefix);
      addPanel();
    }

    /** adds the Scrollbar to the custom window*/
    void addPanel() {
      depth2 = stackSize/(depth*(hasTrans ? 2 : 1));

      if (!hasTP && numFiles > 1) {
        if (hasZ) {
          sliceSel1 = new JScrollBar(JScrollBar.HORIZONTAL, 1, 1, 1, depth+1);
          sliceSel2 = new JScrollBar(JScrollBar.HORIZONTAL, 1, 1, 1, depth2+1);
        }
        else {
          sliceSel1 = new JScrollBar(JScrollBar.HORIZONTAL, 1, 1, 1, depth2+1);
          sliceSel2 = new JScrollBar(JScrollBar.HORIZONTAL, 1, 1, 1, depth+1);
        }
      }
      else {
        sliceSel1 = new JScrollBar(JScrollBar.HORIZONTAL, 1, 1, 1, depth+1);
        sliceSel2 = new JScrollBar(JScrollBar.HORIZONTAL, 1, 1, 1, depth2+1);
      }

      sliceSel1.addAdjustmentListener(this);
      sliceSel2.addAdjustmentListener(this);

      int blockIncrement = depth/10;
      if (blockIncrement < 1) blockIncrement = 1;
      sliceSel1.setUnitIncrement(1);
      sliceSel1.setBlockIncrement(blockIncrement*10);

      blockIncrement = depth2/10;
      if (blockIncrement<1) blockIncrement = 1;
      sliceSel2.setUnitIncrement(1);
      sliceSel2.setBlockIncrement(blockIncrement);

      // CTR 11 Feb 2005
      add(sliceSel1);
      add(sliceSel2);
      Panel bottom = new Panel() {
        public Dimension getPreferredSize() {
          // panel is always the same width as the image canvas
          Dimension d = super.getPreferredSize();
          d.width = ic.getWidth();
          return d;
        }
      };
      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      bottom.setLayout(gridbag);
      label1 = new JLabel(lab3D);
      label2 = new JLabel(lab4D);
      JCheckBox channel2 = new JCheckBox("Transmitted");
      channel2.setBackground(Color.white);
      if (!hasTrans) channel2.setEnabled(false);
      else channel2.addItemListener(this);
      JButton animate = new JButton("Animate");

      SpinnerModel model = new SpinnerNumberModel(10, 1, 99, 1);
      frameRate = new JSpinner(model);

      if ((!hasTP && depth == 1) || (hasTP && depth2 == 1) || numFiles == 1) {
        animate.setEnabled(false);
        frameRate.setEnabled(false);
      }
      else {
        animate.addActionListener(this);
        frameRate.addChangeListener(new FrameRateListener());
      }

      if  (!hasTP && hasZ) {
        if (numFiles > 1) {
          sliceSel1.setEnabled(false);
        }
        else {
          sliceSel2.setEnabled(false);
        }
      }
      else if (!hasTP && depth == 1) sliceSel2.setEnabled(false);

      c.gridx = 0;
      c.gridy = 0;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0.0;
      c.ipadx = 30;
      c.anchor = GridBagConstraints.LINE_START;
      gridbag.setConstraints(label1, c);
      c.gridy = 2;
      gridbag.setConstraints(label2, c);
      c.anchor = GridBagConstraints.CENTER;

      c.gridx = 1;
      c.gridy = 0;
      c.gridwidth = 5;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1.0;
      gridbag.setConstraints(sliceSel1, c);
      c.gridy = 2;
      gridbag.setConstraints(sliceSel2, c);


      c.gridx = 6;
      c.gridy = 0;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0.0;
      c.insets = new Insets(0, 5, 0, 0);
      c.anchor = GridBagConstraints.LINE_END;
      c.gridwidth = 2; // end row
      gridbag.setConstraints(channel2, c);

      c.fill = GridBagConstraints.NONE;
      c.weightx = 0.0;
      c.gridheight = 1;
      c.gridx = 6;
      c.gridy = 3;
      c.gridwidth = GridBagConstraints.REMAINDER; //end row
      c.insets = new Insets(3, 5, 3, 0);
      gridbag.setConstraints(animate, c);

      // next row
      c.gridx = 7;
      c.gridy = 2;
      c.gridwidth = 1;
      c.fill = GridBagConstraints.REMAINDER;
      c.weightx = 0.0;
      bottom.add(new JLabel("fps"), c);

      c.gridx = 6;
      c.gridy = 2;
      c.ipadx = 20;
      //c.gridwidth = GridBagConstraints.REMAINDER; // end row
      gridbag.setConstraints(frameRate, c);
      bottom.add(frameRate);

      // add the XML button
      c.gridx = 5;
      c.gridy = 3;
      c.insets = new Insets(3, 30, 3, 5);

      xmlButton = new JButton("OME-XML");
      xmlButton.addActionListener(this);
      xmlButton.setActionCommand("xml");
      gridbag.setConstraints(xmlButton, c);

      c.gridx = 0;
      c.gridy = 3;
      c.gridwidth = 2;
      c.insets = new Insets(0, 0, 0, 0);
      JButton swapAxesButton = new JButton("Swap Axes");
      swapAxesButton.addActionListener(this);
      swapAxesButton.setActionCommand("swap");
      gridbag.setConstraints(swapAxesButton, c);

      bottom.add(label1);
      bottom.add(sliceSel1);
      bottom.add(channel2);
      bottom.add(label2);
      bottom.add(sliceSel2);
      bottom.add(animate);
      bottom.add(xmlButton);
      bottom.add(swapAxesButton);

      add(bottom);
      //      setSize(1000,getHeight());
      pack();
      setVisible(true);
      int previousSlice = imp.getCurrentSlice();
      imp.setSlice(hasTrans ? depth+1 : 1);
      WindowManager.addWindow(this);
      try {
        extraData.put(new Integer(imp.getID()), d);
      }
      catch (NullPointerException n) { }
      if (previousSlice > 1 && previousSlice <= imp.getStackSize()) {
        imp.setSlice(previousSlice);
      }

      
    }

    class FrameRateListener implements ChangeListener {
      public void stateChanged(ChangeEvent e) {
        JSpinner source = (JSpinner) e.getSource();
        int oldFPS = fps;
        fps = ((Integer) (source.getValue())).intValue();
        if (fps < 1) {
          frameRate.setValue(new Integer(1));
          fps = 1;
        }
        else if (fps > 99) {
          frameRate.setValue(new Integer(99));
          fps = 99;
        }
        if (animating) animationTimer.setDelay(1000/fps);
      }
    }

    // CTR 11 Feb 2005
    /** Button Listener */
    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if ("xml".equals(e.getActionCommand())) {
        int y = WindowManager.getCurrentImage().getID();
        Object[] meta = new Object[2];
        meta[0] = null;
        meta[1] = MetaPanel.exportMeta(description, y);
        MetaPanel metaPanel = new MetaPanel(IJ.getInstance(), y, meta);
        metaPanel.show();
      }
      else if ("swap".equals(e.getActionCommand())) {
        String tmp = label1.getText();
        label1.setText(label2.getText());
        label2.setText(tmp);
        label1.setHorizontalTextPosition(JLabel.LEFT);
        label2.setHorizontalTextPosition(JLabel.LEFT);
        String tmp2 = lab3D;
        lab3D = lab4D;
        lab4D = tmp2;
      }
      else if (src instanceof javax.swing.Timer) {
        boolean changed = false;
        if (lab3D.equals("time")) {
          z = sliceSel1.getValue() + 1;
          if (z >= sliceSel1.getMaximum()) z = sliceSel1.getMinimum();
          sliceSel1.setValue(z);
          changed = true;
        }
        if (lab4D.equals("time")) {
          t = sliceSel2.getValue() + 1;
          if (t >= sliceSel2.getMaximum()) t = sliceSel2.getMinimum();
          sliceSel2.setValue(t);
          changed = true;
        }
        if (changed) showSlice(z, t, trans);
      }
      else if (src instanceof JButton) {
        JButton animate = (JButton) src;
        if (animate.getText().equals("Animate")) {
          animating = true;
          animationTimer = new javax.swing.Timer(1000 / fps, this);
          animationTimer.start();
          animate.setText("Stop");
        }
        else {
          animationTimer.stop();
          animating = false;
          animationTimer = null;
          animate.setText("Animate");
        }
      }
    }

    public void windowClosed(WindowEvent e) {
      if (animationTimer != null) animationTimer.stop();
    }

    /** Checkbox Listener */
    public void itemStateChanged(ItemEvent e) {
      Object src = e.getSource();
      if (src instanceof JCheckBox) {
        JCheckBox channel2 = (JCheckBox) src;
        trans = channel2.isSelected();
        showSlice(z, t, trans);
      }
    }

    /** Scrollbar Listener */
    public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
      if (adjustmentEvent.getSource() == sliceSel1) {
        z = sliceSel1.getValue();
      }
      if (adjustmentEvent.getSource() == sliceSel2) {
        t = sliceSel2.getValue();
      }
      showSlice(z, t, trans);
    }

    /** selects and shows slice defined by z, t and trans */
    public void showSlice(int z, int t, boolean trans) {
      int c = trans ? 0 : 1;

      if (listTP == null || listC == null) {
        if (numFiles == 0) numFiles = 1;
        if (!hasTP) showSlice(z);
        else showSlice(t + depth*(z-1));
        return;
      }

      if (numFiles == 1 || (hasZ && !hasTP && !hasTrans && numFiles > 1)) {
        showSlice(z);
      }
      else if (!hasZ && hasTP && !hasTrans && numFiles > 1 && depth == 1) {
        showSlice(t);
      }
      else {
        if (listTP[0]==0) t -= tpStep;
        for (int i=0; i<numFiles; i++) {
          if ((listTP[i] == t && hasTP) && ((listC[i] == c && hasTrans) ||
            listC[i] == -1))
          {
            if (!hasTP) showSlice(t + depth*i);
            else showSlice(z + depth*i);
            break;
          }
          else if (DEBUG) System.err.println("Something wrong in showSlice()");
        }
      }
    }

    /** selects and shows slice defined by index */
    public void showSlice(int index) {
      if (index >= 1 && index <= imp.getStackSize()) imp.setSlice(index);
      else if (index < 1) System.err.println("Error: Slice index < 1");
      else System.err.println("Error: Slice index > stack size");
      imp.updateAndDraw();
    }

    /**
     * drawinfo overrides method from ImageWindow and adds 3rd and 4th
     * dimension slice position, original code from Wayne Rasband, modified
     * by me
     */
    public void drawInfo(Graphics g) {
      // CTR HACK - workaround for NullPointerException accessing enclosing
      // class's fields before it is initialized (official Sun compiler has
      // this bug, but not code compiled with jikes)
      try { int x = numFiles; }
      catch (NullPointerException exc) { return; }

      int textGap = 0;

      StringBuffer sb = new StringBuffer();
      Insets insets = super.getInsets();
      int nSlices = imp.getStackSize();
      if (nSlices > 1) {
        ImageStack stack = imp.getStack();
        int currentSlice = imp.getCurrentSlice();
        sb.append(currentSlice);
        sb.append("/");
        sb.append(nSlices);
        sb.append(" ");
        sb.append(lab3D);
        sb.append(": ");
        sb.append(sliceSel1 == null ? 0 : sliceSel1.getValue());
        sb.append("/");
        sb.append(numFiles == 1 ? depth : (!hasTP ? depth2 : depth));
        sb.append(" ");
        if (lab4D!=null) sb.append(lab4D);
        sb.append(": ");
        sb.append(sliceSel2 == null ? 0 : sliceSel2.getValue());
        sb.append("/");
        sb.append(!hasTP ? depth : depth2);

        boolean isLabel = false;
        String label = stack.getSliceLabel(currentSlice);
        if (label != null && label.length() > 0) {
          sb.append(" (");
          sb.append(label);
          sb.append(")");
        }
        sb.append("; ");
      }

      String name = imp.getTitle();
      int cndx = name.indexOf("_C");
      int tpndx = name.indexOf("_TP");
      if (cndx >= 0 && tpndx >= 0) {
        sb.append(name.substring(0, cndx));
        sb.append("_C");
        sb.append(trans ? "1" : "2");
        sb.append("_TP");
        sb.append(t);
        sb.append("; ");
      }

      Calibration cal = imp.getCalibration();
      if (cal.pixelWidth != 1.0 || cal.pixelHeight != 1.0) {
        sb.append(IJ.d2s(imp.getWidth()*cal.pixelWidth, 2));
        sb.append("x");
        sb.append(IJ.d2s(imp.getHeight()*cal.pixelHeight, 2));
        sb.append(" ");
        sb.append(cal.getUnits());
        sb.append(" (");
        sb.append(imp.getWidth());
        sb.append("x");
        sb.append(imp.getHeight());
        sb.append("); ");
      }
      else {
        sb.append(imp.getWidth());
        sb.append("x");
        sb.append(imp.getHeight());
        sb.append(" pixels; ");
      }
      int type = imp.getType();
      int size = (imp.getWidth()*imp.getHeight()*imp.getStackSize())/1048576;
      switch (type) {
        case ImagePlus.GRAY8:
          sb.append("8-bit grayscale");
          break;
        case ImagePlus.GRAY16:
          sb.append("16-bit grayscale");
          size *= 2;
          break;
        case ImagePlus.GRAY32:
          sb.append("32-bit grayscale");
          size *= 4;
          break;
        case ImagePlus.COLOR_256:
          sb.append("8-bit color");
          break;
        case ImagePlus.COLOR_RGB:
          sb.append("RGB");
          size *= 4;
          break;
      }
      sb.append("; ");
      sb.append(size);
      sb.append("M");
      g.drawString(sb.toString(), 5, insets.top + textGap);
    }
  } // CustomWindow class end

  /** This class represents an array of disk-resident images. */
  class VirtualStack extends ImageStack{
    static final int INITIAL_SIZE = 100;
    String path;
    int nSlices;
    String[] names;

    /** Creates a new, empty virtual stack. */
    public VirtualStack(int width, int height, ColorModel cm, String path) {
      super(width, height, cm);
      this.path = path;
      names = new String[INITIAL_SIZE];
      //IJ.log("VirtualStack: "+path);
    }

     /** Adds an image FILENAME to the end of the stack. */
    public void addSlice(String name) {
      if (name==null) throw new IllegalArgumentException("'name' is null!");
      nSlices++;
      //IJ.log("addSlice: "+nSlices+"  "+name);
      if (nSlices==names.length) {
        String[] tmp = new String[nSlices*2];
        System.arraycopy(names, 0, tmp, 0, nSlices);
        names = tmp;
      }
      names[nSlices-1] = name;
    }

    /** Does nothing. */
    public void addSlice(String sliceLabel, Object pixels) {
      System.err.println("ERROR: calling addSlice(sliceLabel, pixels)");
    }

    /** Does nothing.. */
    public void addSlice(String sliceLabel, ImageProcessor ip) {
      System.err.println("ERROR: calling addSlice(sliceLabel, ip)");
    }

    /** Does noting. */
    public void addSlice(String sliceLabel, ImageProcessor ip, int n) {
      System.err.println("ERROR: calling addSlice(sliceLabel, ip, n)");
    }

    /** Deletes the specified slice, were 1<=n<=nslices. */
    public void deleteSlice(int n) {
      if (n<1 || n>nSlices) {
        throw new IllegalArgumentException("Argument out of range: "+n);
      }
      if (nSlices<1) return;
      for (int i=n; i<nSlices; i++) names[i-1] = names[i];
      names[nSlices-1] = null;
      nSlices--;
    }

    /** Deletes the last slice in the stack. */
    public void deleteLastSlice() {
      if (nSlices>0) deleteSlice(nSlices);
    }

    /** Returns the pixel array for the specified slice, were 1<=n<=nslices. */
    public Object getPixels(int n) {
      ImageProcessor ip = getProcessor(n);
      return ip == null ? null : ip.getPixels();
    }

    /** Assigns a pixel array to the specified slice, were 1<=n<=nslices. */
    public void setPixels(Object pixels, int n) {
      // TODO
    }

    /**
     * Returns an ImageProcessor for the specified slice,
     * were 1<=n<=nslices. Returns null if the stack is empty.
     */
    public ImageProcessor getProcessor(int n) {
      //IJ.log("getProcessor: "+n+"  "+names[n-1]);
      ImagePlus imp = new Opener().openImage(path, names[n-1]);
      if (imp!=null) {
        int w = imp.getWidth();
        int h = imp.getHeight();
        int type = imp.getType();
        ColorModel cm = imp.getProcessor().getColorModel();
      }
      else return null;
      return imp.getProcessor();
    }

    /** Returns the number of slices in this stack. */
    public int getSize() { return nSlices; }

    /** Returns the file name of the Nth image. */
    public String getSliceLabel(int n) { return names[n-1]; }

    /** Returns null. */
    public Object[] getImageArray() { return null; }

    /** Does nothing. */
    public void setSliceLabel(String label, int n) { }

    /** Always return true. */
    public boolean isVirtual() { return true; }

    /** Does nothing. */
    public void trim() { }
  }

}
