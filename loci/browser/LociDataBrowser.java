//
// LociDataBrowser.java
//
// /deltavision/timelapse/1434.1_01_w528
// YTW 2/27/2006: hardcode deconvolve6_Pseudo_Tl000_Zs000.TIF
// YTW 3/1/2006: rewrite file to make better use of FilePattern

// MELISSA 03/15/2006: twoDimView takes a parameter and has public access;
//                     tweaked some of the logic in showSlice methods to allow
//                     use with OME plugin
// MELISSA 03/17/2006: added logic to preserve file's description

package loci.browser;

import ij.*;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.io.*;
import ij.measure.Calibration;
import ij.plugin.PlugIn;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ColorModel;
import java.io.File;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
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

  // -- Constants (LociDataBrowser) --

  private static final boolean DEBUG = true;


  // -- Static fields (LociDataBrowser) --

  private static boolean grayscale = false;
  private static double scale = 100.0;

  private static String[] preTime = {"_TP", "-TP", ".TP", "_TL", "-TL", ".TL"};
  private static String[] preZ = {"_Z", "-Z", ".Z", "_ZS", "-ZS", ".ZS"};
  private static String[] preTrans = {"_C", "-C", ".C"};

  // stores the description of each image
  private static Hashtable extraData;

  private static String d;


  // -- Fields (LociDataBrowser) --

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
  private String[] filename;

  private int[] listTP;
  private int[] listC;
  private int[] listZ;

  private int maxZ = 0;
  private int maxTP = 0;

  private int idxList;
  private boolean animating = false;
  private boolean preZ1;
  private int tpStep;


  // -- LociDataBrowser methods --

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
    int ss = imp1.getStackSize();
    String title = imp1.getTitle();
    imp1.hide();

    ImageStack stack2 = new ImageStack(w, h);
    for (int i=1; i<=ss; i++) {
      stack2.addSlice(null, stack1.getProcessor(i));
    }
    ImageProcessor ip;
    ImagePlus imp2 = new ImagePlus(title, stack2);
    ImageCanvas ic = new ImageCanvas(imp2);
    new CustomWindow(imp2, ic, hasTrans, firstPrefix);
  }

  boolean isContained(String[] pre1, String pre2) {
    for (int i=0; i<pre1.length; i++) {
      if (pre1[i].equals(pre2)) return true;
    }
    return false;
  }

  private void err(String msg) {
    System.err.println("LociDataBrowser: " + msg);
  }


  // -- Static LociDataBrowser methods --

  public static Hashtable getTable() { return extraData; }


  // -- Plugin methods --

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

    if (DEBUG) {
      for (int i=0; i<prefixes.length; i++) {
        err("prefixes[" + i + "] = " + prefixes[i]);
      }
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

    preZ1 = false;
    if (numFiles == 1) {
      hasTP = false;
      hasZ = true;
      hasTrans = false;
      if (list[0].indexOf('.') != -1) {
        firstPrefix = list[0].substring(0,list[0].indexOf('.'));
      }
    }
    else if (prefixes.length > 0) {
      for (int i=0; i<prefixes.length; i++) {
        if (DEBUG) err("prefixes[" + i + "] = " + prefixes[i]);
        prefixes[i]=prefixes[i].replaceAll("\\d+$", "");
      }

      firstPrefix = prefixes[0];
      if (DEBUG) err("firstPrefix = " + firstPrefix);
      for (int i=0; i<prefixes.length; i++) {
        String pre = prefixes[i].toUpperCase();
        for (int j=0; j<preTime.length; j++) {
          if (pre.endsWith(preTime[j])) {
            hasTP = true;
            idxTP = i;
            if (i==0) {
              firstPrefix = firstPrefix.substring(0,
                firstPrefix.toUpperCase().indexOf(preTime[j]));
            }
          }
        }
        for (int j=0; j<preZ.length; j++) {
          if (pre.endsWith(preZ[j])) {
            hasZ = true;
            idxZ = i;
            if (i==0) {
              firstPrefix = firstPrefix.substring(0,
                firstPrefix.toUpperCase().indexOf(preZ[j]));
            }
          }
        }
        for (int j=0; j<preTrans.length; j++) {
          if (pre.endsWith(preTrans[j])) {
            hasTrans = true;
            idxC = i;
            if (i==0) {
              firstPrefix = firstPrefix.substring(0,
                firstPrefix.toUpperCase().indexOf(preTrans[j]));
            }
          }
        }
      }

      if (!hasTP && !hasTrans && hasZ) preZ1 = true;
      if (DEBUG) err("hasTrans = " + hasTrans);
      int [] repeat = new int[prefixes.length];
      repeat[0] = 1;

      for (int i=1; i<repeat.length; i++) {
        repeat[i] = fp.getCount()[i-1] * repeat[i-1];
      }

      int [][] indices = new int[prefixes.length][list.length];
      if (DEBUG) {
        err("idxTP = " + idxTP);
        err("idxC = " + idxC);
        err("idxZ = " + idxZ);
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

      if (!hasTP && !hasZ && !hasTrans) {
        for (int aa = 0; aa < listTP.length; aa++) listTP[aa] = aa;
        hasTP = true;
      }

      if (DEBUG) {
        StringBuffer sb = new StringBuffer("all indices = ");
        for (int i=0; i<prefixes.length; i++) {
          for (int j=0; j<list.length; j++) {
            sb.append(indices[i][j]);
            sb.append(" ");
          }
          err(sb.toString());
        }
        sb.setLength(0);
        sb.append("listZ = ");
        for (int i=0; i<listZ.length; i++) {
          sb.append(listZ[i]);
          sb.append(" ");
        }
        err(sb.toString());
        sb.setLength(0);
        sb.append("listTP = ");
        for (int i=0; i<listTP.length; i++) {
          sb.append(listTP[i]);
          sb.append(" ");
        }
        err(sb.toString());
        sb.setLength(0);
        sb.append("listC = ");
        for (int i=0; i<listC.length; i++) {
          sb.append(listC[i]);
          sb.append(" ");
        }
        err(sb.toString());
        err("hasTP = " + hasTP);
        err("hasZ = " + hasZ);
        err("hasTrans = " + hasTrans);
      }
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
            if (fi != null) {
              description = fi.description;
              d = description;
            }
            imp.setFileInfo(fi);

            start = 1;
            if ((!hasTP && !hasZ && (depth>1))) {
              for (int ii=0; ii < list.length; ii++) listTP[ii] = ii;
            }
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
          IJ.error("None of the " + n + " files contain\n the string '" +
            filter + "' in their name.");
          return;
        }
      }
      n = filteredImages; // number of images after filtering
      n *= depth;         // multiplied by image depth (# slice in each image)

      // now n = total # slices

      filename = new String[n];
      int fileIndex = 0 ;
      int count = 0;
      int counter = 0;
      increment = 1;

      double progress = 0.0;
      double step = (double) 1/n;
      Double dbStep = new Double(step);

      for (int i=start-1; i<list.length; i++) {
        if (list[i].endsWith(".txt")) continue;
        if (filter != null && (list[i].indexOf(filter) < 0)) continue;
        if ((counter++ % increment) != 0)  continue;

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
          count = stack.getSize() + 1;  // update image counts

          // update number on screen that shows count
          //IJ.showStatus(count + "/" + n);
          //IJ.showProgress((double) count / n);

          // process every slice in each TIFF stack
          for (int iSlice=1; iSlice<=depth; iSlice++) {
            filename[fileIndex++] = list[i];
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
      IJ.outOfMemory("LociDataBrowser");
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


  // -- Helper classes --

  private class CustomWindow extends ImageWindow implements ActionListener,
    AdjustmentListener, ChangeListener, ItemListener, KeyListener
  {

    // -- Fields (CustomWindow) --

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
    private JButton xmlButton; // button to display OME-XML
    private JLabel label1, label2;
    private JButton animate;


    // -- Constructor (CustomWindow) --

    /** CustomWindow constructors, initialisation */
    CustomWindow(ImagePlus imp, ImageCanvas canvas,
      boolean hasTrans, String prefix)
    {
      super(imp, canvas);
      this.hasTrans = hasTrans;
      this.setTitle(prefix);

      // create panel for image canvas
      Panel imagePane = new Panel() {
        public void paint(Graphics g) {
          // paint bounding box here instead of in ImageWindow directly
          Point loc = ic.getLocation();
          Dimension csize = ic.getSize();
          g.drawRect(loc.x-1, loc.y-1, csize.width+1, csize.height+1);
        }
      };

      imagePane.setLayout(getLayout()); // ImageLayout
      imagePane.setBackground(Color.white);

      // redo layout for master window
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      remove(ic);
      add(Box.createVerticalStrut(2)); // leave some extra room for text on top
      add(imagePane);
      imagePane.add(ic);

      // add custom widgets panel
      add(makePanel());

      // repack to take extra panel into account
      pack();

      // listen for arrow key presses
      addKeyListener(this);
      ic.addKeyListener(this);
    }


    // -- CustomWindow methods --

    /** creates the custom widgets panel */
    JPanel makePanel() {
      JPanel pane = new JPanel() {
        public Dimension getMaximumSize() {
          Dimension max = super.getMaximumSize();
          Dimension pref = getPreferredSize();
          return new Dimension(max.width, pref.height);
        }
      };
      pane.setLayout(new BorderLayout());
      pane.setBackground(Color.white);
      pane.setBorder(new EmptyBorder(5, 5, 5, 5));

      depth2 = stackSize/(depth*(hasTrans ? 2 : 1));
      if (hasTP && hasZ && depth == 1) {
        maxZ = 0;
        maxTP = 0;
        for (int i=0; i<listZ.length; i++) {
          if (maxZ < listZ[i]) maxZ = listZ[i];
          if (maxTP < listTP[i]) maxTP = listTP[i];
        }
        sliceSel1 = new JScrollBar(JScrollBar.HORIZONTAL, 1, 1, 1, maxZ+2);
        sliceSel2 = new JScrollBar(JScrollBar.HORIZONTAL, 1, 1, 1, maxTP+2);
      }
      else if (!hasTP && numFiles > 1) {
        if (hasZ && !preZ1) {
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
      if (sliceSel1.getMinimum() + 1 == sliceSel1.getMaximum()) {
        sliceSel1.setEnabled(false);
      }

      if (sliceSel2.getMinimum() + 1 == sliceSel2.getMaximum()) {
        sliceSel2.setEnabled(false);
      }

      int blockIncrement = depth/10;
      if (blockIncrement < 1) blockIncrement = 1;
      sliceSel1.setUnitIncrement(1);
      sliceSel1.setBlockIncrement(blockIncrement*10);

      blockIncrement = depth2/10;
      if (blockIncrement<1) blockIncrement = 1;
      sliceSel2.setUnitIncrement(1);
      sliceSel2.setBlockIncrement(blockIncrement);

      Panel bottom = new Panel();
      bottom.setBackground(Color.white);
      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      bottom.setLayout(gridbag);
      label1 = new JLabel(lab3D);
      label1.setHorizontalTextPosition(JLabel.LEFT);
      label2 = new JLabel(lab4D);
      label2.setHorizontalTextPosition(JLabel.LEFT);
      JCheckBox channel2 = new JCheckBox("Transmitted");
      channel2.setBackground(Color.white);
      if (!hasTrans) channel2.setEnabled(false);
      else channel2.addItemListener(this);
      animate = new JButton("Animate");

      SpinnerModel model = new SpinnerNumberModel(10, 1, 99, 1);
      frameRate = new JSpinner(model);

      if ((!hasTP && depth == 1) || (hasTP && depth2 == 1) || numFiles == 1) {
        animate.setEnabled(false);
        frameRate.setEnabled(false);
      }
      animate.addActionListener(this);
      frameRate.addChangeListener(this);

      if  (!hasTP && hasZ) {
        if (numFiles > 1 && !preZ1) {
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

      // disable XML button if proper libraries are not installed
      try {
        Class.forName("loci.ome.xml.OMENode");
        Class.forName("org.openmicroscopy.ds.dto.Image");
      }
      catch (Throwable e) { xmlButton.setEnabled(false); }

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
      //bottom.setLocation(-100, bottom.getLocation().y);

      int previousSlice = imp.getCurrentSlice();
      imp.setSlice(hasTrans ? depth+1 : 1);
      try {
        extraData.put(new Integer(imp.getID()), d);
      }
      catch (NullPointerException n) { }
      if (previousSlice > 1 && previousSlice <= imp.getStackSize()) {
        imp.setSlice(previousSlice);
      }

      pane.add(bottom, BorderLayout.CENTER);
      return pane;
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
      else if (!hasTP && hasTrans && !hasZ && numFiles == 2) {
        showSlice(c * depth + t);
      }
      else {
        if (listTP[0]==0) t -= tpStep;
        for (int i=0; i<numFiles; i++) {
          if (((listTP[i] == t && hasTP) || !hasTP)
            && ((listC[i] == c && hasTrans) || !hasTrans))
          {
            if (!hasTP) showSlice(t + depth*i);
            else showSlice(z + depth*i);
            break;
          }
          else if (DEBUG && i==numFiles-1) {
            err("something wrong in showSlice");
            err("t = " + t);
            err("z = " + z);
            err("hasTP = " + hasTP);
            err("hasZ = " + hasZ);
            err("preZ1 = " + preZ1);
            err("hasTrans = " + hasTrans);
          }
        }
      }
    }

    /** selects and shows slice defined by index */
    public void showSlice(int index) {
      if (index >= 1 && index <= imp.getStackSize()) {
        imp.setSlice(index);
        imp.updateAndDraw();
      }
      else if (DEBUG) {
        err("index = " + index + " (stack size = " + imp.getStackSize() +
          "; sliceSel1 = " + sliceSel1.getValue() +
          "; sliceSel2 = " + sliceSel2.getValue() + ")");
      }
    }


    // -- ImageWindow methods (CustomWindow) --

    /** adds 3rd and 4th dimension slice position */
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
        sb.append("; ");
        sb.append(lab3D);
        sb.append(": ");
        sb.append(sliceSel1 == null ? 0 : sliceSel1.getValue());
        sb.append("/");
        sb.append(maxZ != 0 ? maxZ+1 :
          (numFiles == 1 ? depth : (!hasTP ? depth2 : depth)));
        sb.append("; ");
        if (lab4D != null) sb.append(lab4D);
        sb.append(": ");
        sb.append(sliceSel2 == null ? 0 : sliceSel2.getValue());
        sb.append("/");
        sb.append(maxTP != 0 ? maxTP+1 :
          (numFiles == 1 ? 1 : (!hasTP ? depth : depth2)));
        sb.append("; ");
        sb.append(filename[currentSlice-1]);
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


    // -- Component methods --

    public void paint(Graphics g) { drawInfo(g); }


    // -- ActionListener methods (CustomWindow) --

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
        String tmp2 = lab3D;
        lab3D = lab4D;
        lab4D = tmp2;
        if (lab3D.equals("time") &&
          sliceSel1.getMaximum() != sliceSel1.getMinimum() &&
          !animate.isEnabled())
        {
          animate.setEnabled(true);
          frameRate.setEnabled(true);
        }
        else if (lab3D.equals("z-depth") && !sliceSel2.isEnabled()) {
          animate.setEnabled(false);
          frameRate.setEnabled(false);
        }
        repaint();
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


    // -- AdjustmentListener methods (CustomWindow) --

    /** Scrollbar listener */
    public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
      if (adjustmentEvent.getSource() == sliceSel1) {
        z = sliceSel1.getValue();
      }
      if (adjustmentEvent.getSource() == sliceSel2) {
        t = sliceSel2.getValue();
      }
      showSlice(z, t, trans);
    }


    // -- ChangeListener methods (CustomWindow) --

    /** JSpinner listener */
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


    // -- ItemListener methods (CustomWindow) --

    /** Checkbox listener */
    public void itemStateChanged(ItemEvent e) {
      Object src = e.getSource();
      if (src instanceof JCheckBox) {
        JCheckBox channel2 = (JCheckBox) src;
        trans = channel2.isSelected();
        showSlice(z, t, trans);
      }
    }


    // -- KeyListener methods (CustomWindow) --

    public void keyPressed(KeyEvent e) {
      int code = e.getKeyCode();
      boolean swapped = lab3D.equals("time");
      if (code == KeyEvent.VK_UP) { // previous slice
        JScrollBar bar = swapped ? sliceSel2 : sliceSel1;
        int val = bar.getValue(), min = bar.getMinimum();
        if (val > min) bar.setValue(val - 1);
      }
      else if (code == KeyEvent.VK_DOWN) { // next slice
        JScrollBar bar = swapped ? sliceSel2 : sliceSel1;
        int val = bar.getValue(), max = bar.getMaximum();
        if (val < max) bar.setValue(val + 1);
      }
      else if (code == KeyEvent.VK_LEFT) { // previous time step
        JScrollBar bar = swapped ? sliceSel1 : sliceSel2;
        int val = bar.getValue(), min = bar.getMinimum();
        if (val > min) bar.setValue(val - 1);
      }
      else if (code == KeyEvent.VK_RIGHT) { // next time step
        JScrollBar bar = swapped ? sliceSel1 : sliceSel2;
        int val = bar.getValue(), max = bar.getMaximum();
        if (val < max) bar.setValue(val + 1);
      }
    }

    public void keyReleased(KeyEvent e) { }

    public void keyTyped(KeyEvent e) { }


    // -- WindowListener methods (CustomWindow) --

    public void windowClosed(WindowEvent e) {
      if (animationTimer != null) animationTimer.stop();
    }

  } // CustomWindow class end

  /** This class represents an array of disk-resident images. */
  class VirtualStack extends ImageStack {

    // -- Constants (VirtualStack) --

    static final int INITIAL_SIZE = 100;


    // -- Fields (VirtualStack) --

    String path;
    int nSlices;
    String[] names;


    // -- Constructor (VirtualStack) --

    /** Creates a new, empty virtual stack. */
    public VirtualStack(int width, int height, ColorModel cm, String path) {
      super(width, height, cm);
      this.path = path;
      names = new String[INITIAL_SIZE];
      //IJ.log("VirtualStack: "+path);
    }


    // -- VirtualStack methods --

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


    // -- ImageStack methods (VirtualStack) --

    /** Does nothing. */
    public void addSlice(String sliceLabel, Object pixels) {
      if (DEBUG) err("ERROR: calling addSlice(sliceLabel, pixels)");
    }

    /** Does nothing.. */
    public void addSlice(String sliceLabel, ImageProcessor ip) {
      if (DEBUG) err("ERROR: calling addSlice(sliceLabel, ip)");
    }

    /** Does noting. */
    public void addSlice(String sliceLabel, ImageProcessor ip, int n) {
      if (DEBUG) err("ERROR: calling addSlice(sliceLabel, ip, n)");
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
