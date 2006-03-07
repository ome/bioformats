package loci.ome.ij;

// YTW: convert tiff to gray8 supported??

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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.ColorModel;
import java.io.File;

import java.util.Vector;

import loci.util.FilePattern;

public class WiscScan implements PlugIn {

  // private static data members
  private static boolean grayscale = false;
  private static double scale = 100.0;

  // private data members
  private FileInfo fi;
  private static Vector description;
  private static Vector ids;
  private static Vector names;
  private int n, start, increment;
  private String filter;
  private String info1;
  private int numFiles;  // number of images files
  private ImagePlus imp1;
  private ImageStack stack1;
  private int stackSize;
  private boolean hasTrans = false;  // transmitted channel?
  private boolean hasTP = false;
  private boolean hasZ = true;
  private int depth = 0; // number of "slices" in each image file
  private int depth2 = 0;
  private String lab3D;
  private String lab4D;
  private boolean cb4tp;
  private String firstPrefix;
  private int dim = 4;

  private int[] listTP;
  private int[] listC;
  private int idxList;
  private boolean animating = false;

  private String directory;
  private String name;
  private OpenDialog od;
  private static Vector window;
  private static Vector numImages;  // number of files in each window

  private boolean disk = false; // true if we are opening a stack from disk

  private int currentId = 0; // the ID of the next database image

  public static boolean firstTime = true;

  public static Vector getNumImages() {
    return numImages;
  }

  public static Vector getDescription() {
    return description;
  }

  public static Vector getIDs() {
    return ids;
  }

  public static Vector getNames() {
    return names;
  }

  public static void addName(String toAdd) {
    if(names == null) names = new Vector();
    names.add(toAdd);
  }

  public void initFile() {
    od = new OpenDialog("Open Sequence of Image Stacks:", "");
    directory = od.getDirectory();
    name = od.getFileName();
  }

  public void run(String arg) {
    initFile();
    disk = true;
    if (name == null) return;

    if(description == null) description = new Vector();
    if(ids == null) ids = new Vector();
    if(names == null) names = new Vector();
    if(window == null) window = new Vector();
    if(numImages == null) numImages = new Vector();

    // Find all the files having similar names (Using FilePattern class)
    String pattern = FilePattern.findPattern(new File(name), directory);
    // avoids NullPointerException if there is no suffix
    if(pattern == null) pattern = "";
    FilePattern fp = new FilePattern(pattern);
    int ndx = fp.getPrefix().indexOf('_');
    if(ndx != -1) firstPrefix = fp.getPrefix().substring(0, ndx);
    ndx = fp.getPrefix().lastIndexOf('_');
    String prefix = "";
    if(ndx > 1) prefix = fp.getPrefix().substring(ndx-2, ndx);

    int idx = 0;
    while (fp.getPrefix(idx) != null) {
      String pfx = fp.getPrefix(idx);
      if(pfx.length() > 1) {
        if (pfx.substring(pfx.length()-2).equals("TP"))  hasTP = true;
        else if ((pfx.substring(pfx.length()-1)).equals("C")) hasTrans = true;
      }
      idx++;
    }

    String [] absList = fp.getFiles();     // all the image files

    String [] list = new String[absList.length];
    int dirIndex = absList[0].lastIndexOf(File.separatorChar);

    for (int i=0; i < absList.length; i++) {
      list[i] = absList[i].substring(dirIndex+1);
    }

    // YTW Nov 21 2005
    // Multichannel support
    //if (fp.getMultichannel()) hasTrans = true;
    numFiles = list.length;
    numImages.add(new Integer(numFiles));

    listTP = new int[numFiles];
    listC  = new int[numFiles];
    for (int i=0; i<numFiles; i++) {
      listTP[i] = 0;
      listC[i] = 0;
    }
    int idxTP = 0;
    int idxC = 0;
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

      // the valid flag is used to determine if the first image in the stack
      // was opened successfully
      // if it wasn't, we set valid = false to prevent ImageJ from displaying
      // an error message for each image in the stack, since this can be very
      // annoying
      boolean valid = true;
      for (int i=0; i<list.length; i++) {
        if(valid) {
          // accounts for text files? Why will the list have text files?
          if (list[i].endsWith(".txt")) continue;
          ImagePlus imp = new Opener().openImage(directory, list[i]);

          if (imp != null) { // gather all info on the image
            width = imp.getWidth();
            height = imp.getHeight();
            depth = imp.getStackSize();
            type = imp.getType();
            fi = imp.getOriginalFileInfo();
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
      n = filteredImages; // number of images after filtering
      n *= depth; // multiplied by image depth (# slice in each image)

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
        FileInfo f = imp.getOriginalFileInfo();
        description.add(f.description);
        names.add(list[i]);
        ids.add(new Integer(imp.getID()));

        if (imp != null && stack == null) {  // first image in the stack
          width = imp.getWidth();
          height = imp.getHeight();
          type = imp.getType();
          ColorModel cm = imp.getProcessor().getColorModel();
          if (scale < 100.0) {
            stack = new ImageStack((int) (width*scale/100.0),
              (int) (height*scale/100.0), cm);
          }
          else {
            stack = new ImageStack(width, height, cm);
          }

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
          // everything is fine
          try {
            if (idxTP != -1 && idxC != -1) {
              if (cb4tp) {
                listC[idxList] = Integer.parseInt(
                list[i].substring(idxC+2, list[i].indexOf("_TP")));
                listTP[idxList++] = Integer.parseInt(list[i].substring(
                list[i].indexOf("_TP")+3, list[i].lastIndexOf('.')));
              }
              else {
                listTP[idxList] = Integer.parseInt(
                list[i].substring(idxTP+3, list[i].indexOf("_C")));
                listC[idxList++]  = Integer.parseInt(list[i].substring(
                list[i].indexOf("_C")+2, list[i].lastIndexOf('.')));
              }
            }
            else if (idxTP != -1 && idxC == -1) {
              listTP[idxList++] = Integer.parseInt(list[i].substring(
              list[i].indexOf("_TP")+3, list[i].lastIndexOf('.')));
            }
            else if (idxTP == -1 && idxC != -1) {
              listC[idxList++] = Integer.parseInt(list[i].substring(
              list[i].indexOf("_C")+2, list[i].lastIndexOf('.')));
            }
          }
          catch (NumberFormatException nfe) {
            System.err.println("Invalid Indices!");
          }
          catch (Exception e) {
            System.err.println("Unknown Error!");
          }

          if (!hasTP && depth == 1) hasTP = false;
          else if (depth == 1) hasZ = false;

          count = stack.getSize() + 1;  // update image counts

          // update number on screen that shows count
          // IJ.showStatus(count+"/"+n);
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
      }
    }
    catch(OutOfMemoryError e) {
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
      if (imp2.getStackSize() == 1 && info1 != null) {
        imp2.setProperty("Info", info1);
      }
      imp2.show();
    }
    IJ.showProgress(1.0);
    twoDimView(null, depth, hasTP);
  }

  public void twoDimView(ImagePlus ip, int width, boolean tp) {
    String imageName;
    imp1 = ip;
    if(imp1 == null) imp1 = WindowManager.getCurrentImage();
    stack1 = imp1.getStack();
    stackSize = stack1.getSize();
    imageName = imp1.getTitle();
    if(depth == 0) depth = width;
    hasTP = tp;
    if(numImages == null) numImages = new Vector();
    numImages.add(new Integer(1));

    // is it a right assumption that each image has the same number of slices?
    // yes :-)
    lab3D = "z-depth";
    lab4D = "time";

    if(stack1.getSize() > 0) initFrame();

    if(ids == null) ids = new Vector();
    if(description == null) description = new Vector();
    if(names == null) names = new Vector();
    if(!disk) {
      names.add(imp1.getTitle());
      ids.add(new Integer(currentId));
      currentId--;
      description.add(null);
    }
  }

  void initFrame() {
    if(imp1 != null) {
      int w = imp1.getWidth();
      int h = imp1.getHeight();
      ImageStack stack2 = new ImageStack(w,h);
      for (int i=1; i<=imp1.getStackSize(); i++) {
        stack2.addSlice(null, stack1.getProcessor(i));
      }

      ImageProcessor ip;
      ImagePlus imp2 = new ImagePlus(imp1.getTitle(), stack2);
      ImageCanvas ic = new ImageCanvas(imp2);
      if(window == null) {
        window = new Vector();
      }
      window.add(new CustomWindow(imp2, ic, hasTrans, firstPrefix));
      imp1.hide();
    }
  }

  /* CustomWindow class begin*/
  private class CustomWindow extends ImageWindow
    implements ActionListener, AdjustmentListener, ItemListener, WindowListener
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
    private boolean resized = false;

    /* CustomWindow constructors, initialisation*/
    CustomWindow(ImagePlus imp, ImageCanvas ic,
      boolean hasTrans, String prefix)
    {
      super(imp, ic);
      // resize the window so that the scrollbars show up properly
      if(ic.getWidth() < 512) {
        ic.setBounds(ic.getX(), ic.getY(), 512, ic.getHeight());
        resized = true;
      }
      this.hasTrans = hasTrans;
      //this.setTitle(prefix);
      this.setTitle(imp.getTitle());
      addPanel();
    }

    /* adds the Scrollbar to the custom window*/
    void addPanel() {
      depth2 = stackSize / depth;
      if(hasTrans) depth2 = depth2 / 2;

      if (!hasTP) {
        sliceSel1 = new JScrollBar(JScrollBar.HORIZONTAL, 1, 1, 1, depth2+1);
        sliceSel2 = new JScrollBar(JScrollBar.HORIZONTAL, 1, 1, 1, depth+1);
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
          if(ic.getWidth() < 512) {
            d.width = ((512 - ic.getWidth()) / 2) + ic.getWidth();
          }
          return d;
        }
      };

      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      bottom.setLayout(gridbag);
      JLabel label1 = new JLabel(lab3D);
      JLabel label2 = new JLabel(lab4D);
      JCheckBox channel2 = new JCheckBox("Transmitted");
      channel2.setBackground(Color.white);
      if (!hasTrans) channel2.setEnabled(false);
      else channel2.addItemListener(this);
      JButton animate = new JButton("Animate");

      SpinnerModel model = new SpinnerNumberModel(10,1,99,1);
      frameRate = new JSpinner(model);

      if ((!hasTP && depth == 1) || (hasTP && depth2 == 1)) {
        animate.setEnabled(false);
        frameRate.setEnabled(false);
      }
      else {
        animate.addActionListener(this);
        frameRate.addChangeListener(new FrameRateListener());
      }

      bottom.add(label1);
      bottom.add(sliceSel1);
      bottom.add(channel2);
      bottom.add(label2);
      bottom.add(sliceSel2);
      bottom.add(animate);
      bottom.add(frameRate);

      c.insets = new Insets(0, 5, 0, 5);

      if (!hasTP)  sliceSel1.setEnabled(false);
      if (!hasTP && depth == 1) sliceSel2.setEnabled(false);

      c.gridx = 0;
      c.gridy = 0;

      c.fill = GridBagConstraints.NONE;
      c.weightx = 0.0;
      gridbag.setConstraints(label1, c);
      c.gridx = 1;
      c.gridy = 0;
      c.gridwidth = 5;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1.0;
      gridbag.setConstraints(sliceSel1, c);

      c.gridx = 6;
      c.gridy = 0;
      c.fill = GridBagConstraints.NONE;
      c.weightx = 0.0;
      c.gridwidth = 2; // end row
      gridbag.setConstraints(channel2, c);

      // next row
      c.gridx = 0;
      c.gridy = 2;
      c.gridwidth = 1;
      gridbag.setConstraints(label2, c);
      c.gridx = 1;
      c.gridy = 2;
      c.gridwidth = 5;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = 1.0;
      gridbag.setConstraints(sliceSel2, c);

      c.fill = GridBagConstraints.NONE;
      c.weightx = 0.0;
      c.gridheight = 1;
      c.gridx = 6;
      c.gridy = 3;
      c.insets = new Insets(3, 5, 3, 5);
      gridbag.setConstraints(animate, c);

      // next row
      c.gridx = 6;
      c.gridy = 2;
      c.gridwidth = 1;
      c.fill = GridBagConstraints.REMAINDER;
      c.weightx = 0.0;

      bottom.add(new JLabel("fps"), c);

      c.gridx = 7;
      c.gridy = 2;
      c.gridwidth = GridBagConstraints.REMAINDER; // end row
      c.ipadx = 20;
      gridbag.setConstraints(frameRate, c);

      add(bottom);

      pack();
      setVisible(true);
      int previousSlice = imp.getCurrentSlice();
      imp.setSlice(depth+1);
      WindowManager.addWindow(this);

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
   /* Button Listener*/
   public void actionPerformed(ActionEvent e) {
     Object src = e.getSource();
     if (src instanceof javax.swing.Timer) {
       boolean changed = false;
       if (lab3D.equals("time")) {
         z = sliceSel1.getValue() + 1;
         if (z >= sliceSel1.getMaximum()) {
           z = sliceSel1.getMinimum();
         }
         sliceSel1.setValue(z);
         changed = true;
       }
       if (lab4D.equals("time")) {
         t = sliceSel2.getValue() + 1;
         if (z >= sliceSel1.getMaximum()) {
           z = sliceSel1.getMinimum();
         }
         if (t >= sliceSel2.getMaximum()) {
           t = sliceSel2.getMinimum();
         }
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

   /** WindowListener API methods */
   public void windowActivated(WindowEvent e) {
     // do nothing
   }

   public void windowClosed(WindowEvent e) {
     // do nothing
   }

   public void windowClosing(WindowEvent e) {
     // remove the name, description, and ID from appropriate Vectors

     int prevSize = window.size();

     CustomWindow w = (CustomWindow) e.getWindow();
     CustomWindow current;
     int n = 0;
     if(window != null) {
       for(int i=0; i<window.size(); i++) {
         current = (CustomWindow) window.get(i);
         if((w == current) && (w.getTitle().equals(current.getTitle()))) {
           window.remove(i);
           n = i;
         }
       }
     }

     if((prevSize < ids.size()) && firstTime) {
       n++;
       firstTime = false;
     }

     int numPlanes = 1;
     if(numImages != null) numPlanes = ((Integer) numImages.get(n)).intValue();
     if(ids != null) {
       for(int i=0; i<numPlanes; i++) {
         if(n < ids.size()) ids.remove(n);
       }
     }
     if(description != null) {
       for(int i=0; i<numPlanes; i++) {
         if(n < description.size()) description.remove(n);
       }
     }
     if(names != null) {
       for(int i=0; i<numPlanes; i++) {
         if(n < names.size()) names.remove(n);
       }
     }

     w.dispose();
     OMESidePanel.showIt();
   }

   public void windowDeactivated(WindowEvent e) {
     // do nothing
   }

   public void windowDeiconified(WindowEvent e) {
     // do nothing
   }

   public void windowIconified(WindowEvent e) {
     // do nothing
   }

   public void windowOpened(WindowEvent e) {
     // do nothing
   }

   /* Checkbox Listener*/
   public void itemStateChanged(ItemEvent e) {
     Object src = e.getSource();
     if (src instanceof JCheckBox) {
       JCheckBox channel2 = (JCheckBox) src;
       trans = channel2.isSelected();
       showSlice(z,t,trans);
     }
   }

   /* Scrollbar Listener*/
   public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
     if (adjustmentEvent.getSource() == sliceSel1) {
       z = sliceSel1.getValue();
     }
     if (adjustmentEvent.getSource() == sliceSel2) {
       t = sliceSel2.getValue();
     }
     showSlice(z, t, trans);
   }

   /* selects and shows slice defined by z, t and trans */
   public void showSlice(int z, int t, boolean trans) {
     int c = trans ? 1 : 2;
     if (dim == 2) {
       showSlice(t + depth*(c-1));
     }
     for (int i=0; i<numFiles;i++) {
       if ((listTP[i] == t || listTP[i] == 0) &&
         (listC[i] == c || listC[i] == 0))
       {
         if (!hasTP) {
           showSlice(t + depth*i);
         }
         else {
           showSlice(z + depth*i);
         }
       }
     }

     // database stack
     if(!disk) {
       // z = 1, t = 1 -> slice = 1
       if(!hasTP) showSlice(z);  // this could be broken
       else showSlice(z + depth*(t-1));
     }
   }

   /* selects and shows slice defined by index */
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
       sb.append(!hasTP ? depth2 : depth);
       sb.append(" ");
       sb.append(lab4D);
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
     sb.append(IJ.d2s(imp.getWidth()*cal.pixelWidth,2));
     sb.append("x");
     sb.append(IJ.d2s(imp.getHeight()*cal.pixelHeight,2));
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
 }

  /* CustomWindow class end*/
  /*
  private class FilenameComparator implements Comparator {
    public int compare(Object o1, Object o2) {
     if ((String)o1 > (String)o2)
    }
  }
  */
}
