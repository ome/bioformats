//
// SlimPlotter.java
//

/*
SLIM Plotter application and curve fitting library for
combined spectral lifetime visualization and analysis.
Copyright (C) 2006-@year@ Curtis Rueden and Eric Kjellman.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

// Special thanks to Long Yan, Steve Trier, Kraig Kumfer,
// Paolo Provenzano and Tony Collins for suggestions and testing.

package loci.slim;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.text.Document;
import loci.formats.gui.ExtensionFileFilter;
import loci.slim.fit.*;
import loci.visbio.util.BreakawayPanel;
import loci.visbio.util.ColorUtil;
import loci.visbio.util.OutputConsole;
import visad.*;
import visad.java3d.*;

/**
 * A tool for visualization of spectral lifetime data.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/slim-plotter/src/loci/slim/SlimPlotter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/slim-plotter/src/loci/slim/SlimPlotter.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class SlimPlotter implements ActionListener, ChangeListener,
  DocumentListener, ItemListener, Runnable, WindowListener
{

  // -- Constants --

  protected static final boolean DEBUG = false;

  /** Number of iterations to perform for regional lifetime curve fitting. */
  private static final int NUM_ITERATIONS = 250;

  /** Default orientation for 3D decay curves display. */
  private static final double[] MATRIX_3D = {
    0.2821, 0.1503, -0.0201, 0.0418,
    -0.0500, 0.1323, 0.2871, 0.1198,
    0.1430, -0.2501, 0.1408, 0.0089,
    0.0000, 0.0000, 0.0000, 1.0000
  };

  /** Default orientation for 2D decay curve display. */
  private static final double[] MATRIX_2D = {
    0.400, 0.000, 0.000, 0.160,
    0.000, 0.400, 0.000, 0.067,
    0.000, 0.000, 0.400, 0.000,
    0.000, 0.000, 0.000, 1.000
  };

  private static final char TAU = 'T';

  // log base 10, for now
  private static final float BASE = 10;
  private static final float BASE_LOG = (float) Math.log(BASE);

  // -- Fields --

  /** Master data structure containing raw and derived SLIM data. */
  private SlimData data;

  /** Current binned data values, dimensioned [channels * timeBins]. */
  private float[] samps;

  private float maxVal;
  private float tauMin, tauMax;

  // fit parameters
  private float[][] tau;

  // system clipboard helper
  private TextTransfer clip = new TextTransfer();

  // menu items
  private MenuItem menuFileExit;
  private MenuItem menuViewSaveProj;
  private MenuItem menuViewLoadProj;
  private CheckboxMenuItem menuViewScale;
  private CheckboxMenuItem menuViewBox;
  private CheckboxMenuItem menuViewLines;
  private CheckboxMenuItem menuViewFWHMs;
  private CheckboxMenuItem menuViewDataSurface;
  private CheckboxMenuItem menuViewDataLines;
  private CheckboxMenuItem menuViewFitSurface;
  private CheckboxMenuItem menuViewFitLines;
  private CheckboxMenuItem menuViewResSurface;
  private CheckboxMenuItem menuViewResLines;
  private CheckboxMenuItem menuViewLogScale;
  private CheckboxMenuItem menuViewParallel;
  private CheckboxMenuItem menuViewColorTau;

  // GUI components for 2D pane
  private TwoDPane twoDPane;

  // GUI components for decay pane
  private JLabel decayLabel;
  private ColorWidget colorWidget;
  private JCheckBox zOverride;
  private JTextField zScaleValue;
  private JButton exportData;

  // GUI components for numerical results
  private JTextField a1Param, t1Param, a2Param, t2Param, cParam, chi2;

  // other GUI components
  private JFrame masterWindow;
  private OutputConsole console;

  // VisAD objects
  private SlimTypes types;
  private ScalarMap zMap, zMapFit, zMapRes, vMap, vMapRes;
  //private ScalarMap vMapFit;
  private DisplayImpl decayPlot;
  private DataRenderer lineRend, fwhmRend;
  private DataRenderer dataSurfaceRend, dataLinesRend;
  private DataRenderer fitSurfaceRend, fitLinesRend;
  private DataRenderer resSurfaceRend, resLinesRend;
  private DataReferenceImpl lineRef, fwhmRef;
  private DataReferenceImpl dataSurfaceRef, dataLinesRef;
  private DataReferenceImpl fitSurfaceRef, fitLinesRef;
  private DataReferenceImpl resSurfaceRef, resLinesRef;

  // -- Constructor --

  public SlimPlotter(String[] args) throws Exception {
    console = new OutputConsole("Log");
    console.getTextArea().setColumns(54);
    console.getTextArea().setRows(10);

    PrintStream err = new PrintStream(console);

    // HACK - suppress hard-coded exception dumps
    err = LMCurveFitter.filter(err);

    System.setErr(err);

    // progress estimate:
    // * Reading and processing data - 90%
    // * Creating types - 1%
    // * Building displays - 6%
    // * Creating plots - 3%
    ProgressMonitor progress = new ProgressMonitor(null,
      "Launching " + SlimData.TITLE, "Initializing", 0, 1000);
    progress.setMillisToPopup(0);
    progress.setMillisToDecideToPopup(0);

    try {
      // check for required libraries
      try {
        Class.forName("javax.vecmath.Point3d");
      }
      catch (Throwable t) {
        String os = System.getProperty("os.name").toLowerCase();
        String url = null;
        if (os.indexOf("windows") >= 0 ||
          os.indexOf("linux") >= 0 || os.indexOf("solaris") >= 0)
        {
          url = "https://java3d.dev.java.net/binary-builds.html";
        }
        else if (os.indexOf("mac os x") >= 0) {
          url = "http://www.apple.com/downloads/macosx/apple/" +
            "java3dandjavaadvancedimagingupdate.html";
        }
        else if (os.indexOf("aix") >= 0) {
          url = "http://www-128.ibm.com/developerworks/java/jdk/aix/index.html";
        }
        else if (os.indexOf("hp-ux") >= 0) {
          url = "http://www.hp.com/products1/unix/java/java2/java3d/" +
            "downloads/index.html";
        }
        else if (os.indexOf("irix") >= 0) {
          url = "http://www.sgi.com/products/evaluation/6.5_java3d_1.3.1/";
        }
        JOptionPane.showMessageDialog(null,
          SlimData.TITLE + " requires Java3D, but it was not found." +
          (url == null ? "" : ("\nPlease install it from:\n" + url)),
          SlimData.TITLE, JOptionPane.ERROR_MESSAGE);
        System.exit(3);
      }

      // parse command line arguments
      String filename = null;
      File file = null;
      if (args == null || args.length < 1) {
        JFileChooser jc = new JFileChooser(System.getProperty("user.dir"));
        jc.addChoosableFileFilter(new ExtensionFileFilter("sdt",
          "Becker & Hickl SPC-Image SDT"));
        int rval = jc.showOpenDialog(null);
        if (rval != JFileChooser.APPROVE_OPTION) {
          System.out.println("Please specify an SDT file.");
          System.exit(1);
        }
        file = jc.getSelectedFile();
        filename = file.getPath();
      }
      else {
        filename = args[0];
        file = new File(filename);
      }

      if (!file.exists()) {
        System.out.println("File does not exist: " + filename);
        System.exit(2);
      }

      // pop up progress monitor
      setProgress(progress, 1); // estimate: 0.1%

      // read data
      data = new SlimData(file.getPath(), progress);

      // create types
      progress.setNote("Creating types");
      types = new SlimTypes(data.width, data.height,
        data.channels, data.minWave, data.maxWave);
      setProgress(progress, 910); // estimate: 91%

      // plot intensity data in 2D display
      progress.setNote("Building displays");

      // plot decay curves in 3D display
      decayPlot = data.channels > 1 ? new DisplayImplJ3D("decay") :
        new DisplayImplJ3D("decay", new TwoDDisplayRendererJ3D());

      ScalarMap xMap = new ScalarMap(types.bType, Display.XAxis);
      ScalarMap yMap = new ScalarMap(types.cType, Display.YAxis);
      DisplayRealType heightAxis = data.channels > 1 ?
        Display.ZAxis : Display.YAxis;
      zMap = new ScalarMap(types.vType, heightAxis);
      zMapFit = new ScalarMap(types.vTypeFit, heightAxis);
      zMapRes = new ScalarMap(types.vTypeRes, heightAxis);
      vMap = new ScalarMap(types.vType2, Display.RGB);
      //vMapFit = new ScalarMap(types.vTypeFit, Display.RGB);
      vMapRes = new ScalarMap(types.vTypeRes, Display.RGB);
      decayPlot.addMap(xMap);
      if (data.channels > 1) decayPlot.addMap(yMap);
      decayPlot.addMap(zMap);
      decayPlot.addMap(zMapFit);
      decayPlot.addMap(zMapRes);
      decayPlot.addMap(vMap);
      //decayPlot.addMap(vMapFit);
      decayPlot.addMap(vMapRes);

      setProgress(progress, 950); // estimate: 95%

      // add reference for yellow lines
      lineRend = new DefaultRendererJ3D();
      lineRef = new DataReferenceImpl("line");
      ConstantMap[] lineMaps = null;
      if (data.channels == 1) { // 2-D plot
        lineMaps = new ConstantMap[] {
          new ConstantMap(1, Display.Red),
          new ConstantMap(1, Display.Green),
          new ConstantMap(0, Display.Blue)
          //new ConstantMap(2, Display.LineWidth)
        };
      }
      else { // 3-D plot
        lineMaps = new ConstantMap[] {
          new ConstantMap(1, Display.Red),
          new ConstantMap(1, Display.Green),
          new ConstantMap(0, Display.Blue),
          new ConstantMap(-1, Display.ZAxis)
          //new ConstantMap(2, Display.LineWidth)
        };
      }
      decayPlot.addReferences(lineRend, lineRef, lineMaps);

      // add reference for full width half maxes
      if (data.computeFWHMs) {
        fwhmRend = new DefaultRendererJ3D();
        fwhmRef = new DataReferenceImpl("fwhm");
        decayPlot.addReferences(fwhmRend, fwhmRef, new ConstantMap[] {
          new ConstantMap(0, Display.Red),
          new ConstantMap(1, Display.Green),
          new ConstantMap(0, Display.Blue),
          //new ConstantMap(2, Display.LineWidth)
        });
        fwhmRend.toggle(false);
      }

      // add references for raw data
      dataSurfaceRend = new DefaultRendererJ3D();
      dataSurfaceRef = new DataReferenceImpl("data surface");
      decayPlot.addReferences(dataSurfaceRend, dataSurfaceRef);
      dataSurfaceRend.toggle(data.channels > 1);
      dataLinesRend = new DefaultRendererJ3D();
      dataLinesRef = new DataReferenceImpl("data lines");
      decayPlot.addReferences(dataLinesRend, dataLinesRef);
      dataLinesRend.toggle(data.channels == 1);

      // add references for curve fitting
      if (data.allowCurveFit) {
        fitSurfaceRend = new DefaultRendererJ3D();
        fitSurfaceRef = new DataReferenceImpl("fit surface");
        decayPlot.addReferences(fitSurfaceRend, fitSurfaceRef,
          new ConstantMap[]
        {
          new ConstantMap(1, Display.Red),
          new ConstantMap(0.5, Display.Green),
          new ConstantMap(1, Display.Blue)
        });
        fitSurfaceRend.toggle(false);
        fitLinesRend = new DefaultRendererJ3D();
        fitLinesRef = new DataReferenceImpl("fit surface");
        decayPlot.addReferences(fitLinesRend, fitLinesRef,
          new ConstantMap[]
        {
          new ConstantMap(1, Display.Red),
          new ConstantMap(0.5, Display.Green),
          new ConstantMap(1, Display.Blue)
        });
        resSurfaceRend = new DefaultRendererJ3D();
        resSurfaceRef = new DataReferenceImpl("res surface");
        decayPlot.addReferences(resSurfaceRend, resSurfaceRef);
        resSurfaceRend.toggle(false);
        resLinesRend = new DefaultRendererJ3D();
        resLinesRef = new DataReferenceImpl("res surface");
        decayPlot.addReferences(resLinesRend, resLinesRef);
        resLinesRend.toggle(false);
      }
      setProgress(progress, 960); // estimate: 96%

      // configure axes
      xMap.setRange(0, data.timeRange);
      yMap.setRange(data.minWave, data.maxWave);
      AxisScale xScale = xMap.getAxisScale();
      Font font = Font.decode("serif 24");
      xScale.setFont(font);
      xScale.setTitle("Time (ns)");
      xScale.setSnapToBox(true);
      AxisScale yScale = yMap.getAxisScale();
      yScale.setFont(font);
      yScale.setTitle("Wavelength (nm)");
      yScale.setSide(AxisScale.SECONDARY);
      yScale.setSnapToBox(true);
      AxisScale zScale = zMap.getAxisScale();
      zScale.setFont(font);
      zScale.setTitle("Count");
      zScale.setSnapToBox(true); // workaround for weird axis spacing issue
      zMapFit.getAxisScale().setVisible(false);
      zMapRes.getAxisScale().setVisible(false);
      GraphicsModeControl gmc = decayPlot.getGraphicsModeControl();
      gmc.setScaleEnable(true);
      gmc.setTextureEnable(false);
      ProjectionControl pc = decayPlot.getProjectionControl();
      pc.setMatrix(data.channels > 1 ? MATRIX_3D : MATRIX_2D);
      pc.setAspectCartesian(new double[] {2, 1, 1});
      setProgress(progress, 970); // estimate: 97%

      // construct 2D pane
      progress.setNote("Creating plots");
      masterWindow = new JFrame(SlimData.TITLE + " - " + file.getName());
      masterWindow.addWindowListener(this);
      JPanel masterPane = new JPanel();
      masterPane.setLayout(new BorderLayout());
      masterWindow.setContentPane(masterPane);

      twoDPane = new TwoDPane(this, data, types);

      setProgress(progress, 980); // estimate: 98%

      // construct menu bar
      progress.setNote("Building GUI");

      MenuBar menubar = new MenuBar();
      masterWindow.setMenuBar(menubar);

      Menu menuFile = new Menu("File");
      //menubar.add(menuFile);
      Menu menuView = new Menu("View");
      menubar.add(menuView);

      menuFileExit = new MenuItem("Exit");
      menuFileExit.addActionListener(this);
      menuFile.add(menuFileExit);

      menuViewSaveProj = new MenuItem("Save projection to clipboard");
      menuViewSaveProj.addActionListener(this);
      menuView.add(menuViewSaveProj);

      menuViewLoadProj = new MenuItem("Restore projection from clipboard");
      menuViewLoadProj.addActionListener(this);
      menuView.add(menuViewLoadProj);

      menuView.addSeparator();

      menuViewScale = new CheckboxMenuItem("Scale bar", true);
      menuViewScale.addItemListener(this);
      menuView.add(menuViewScale);

      menuViewBox = new CheckboxMenuItem("Bounding box", true);
      menuViewBox.addItemListener(this);
      menuView.add(menuViewBox);

      menuViewLines = new CheckboxMenuItem("Fit bounds", data.allowCurveFit);
      menuViewLines.setEnabled(data.allowCurveFit);
      menuViewLines.addItemListener(this);
      menuView.add(menuViewLines);

      menuViewFWHMs = new CheckboxMenuItem("FWHMs");
      menuViewFWHMs.setEnabled(data.computeFWHMs);
      menuViewFWHMs.addItemListener(this);
      menuView.add(menuViewFWHMs);

      menuView.addSeparator();

      boolean threeD = data.channels > 1;

      menuViewDataSurface = new CheckboxMenuItem("Data surface", threeD);
      menuViewDataSurface.setEnabled(threeD);
      menuViewDataSurface.addItemListener(this);
      menuView.add(menuViewDataSurface);

      menuViewDataLines = new CheckboxMenuItem("Data lines", !threeD);
      menuViewDataLines.addItemListener(this);
      menuView.add(menuViewDataLines);

      menuViewFitSurface = new CheckboxMenuItem("Fit surface");
      menuViewFitSurface.setEnabled(data.allowCurveFit && threeD);
      menuViewFitSurface.addItemListener(this);
      menuView.add(menuViewFitSurface);

      menuViewFitLines = new CheckboxMenuItem("Fit lines", data.allowCurveFit);
      menuViewFitLines.setEnabled(data.allowCurveFit);
      menuViewFitLines.addItemListener(this);
      menuView.add(menuViewFitLines);

      menuViewResSurface = new CheckboxMenuItem("Residuals surface");
      menuViewResSurface.setEnabled(data.allowCurveFit && threeD);
      menuViewResSurface.addItemListener(this);
      menuView.add(menuViewResSurface);

      menuViewResLines = new CheckboxMenuItem("Residuals lines");
      menuViewResLines.setEnabled(data.allowCurveFit);
      menuViewResLines.addItemListener(this);
      menuView.add(menuViewResLines);

      menuView.addSeparator();

      menuViewLogScale = new CheckboxMenuItem("Log scale");
      menuViewLogScale.addItemListener(this);
      menuView.add(menuViewLogScale);

      menuViewParallel = new CheckboxMenuItem("Parallel projection");
      menuViewParallel.addItemListener(this);
      menuView.add(menuViewParallel);

      menuViewColorTau = new CheckboxMenuItem("Colorize data by lifetime");
      menuViewColorTau.setEnabled(data.allowCurveFit);
      menuViewColorTau.addItemListener(this);
      menuView.add(menuViewColorTau);

      // construct other GUI components
      JPanel decayPane = new JPanel();
      decayPane.setLayout(new BorderLayout());
      decayPane.add(decayPlot.getComponent(), BorderLayout.CENTER);

      decayLabel = new JLabel("Decay curve for all pixels");
      decayLabel.setToolTipText(
        "Displays information about the selected region of interest");
      decayPane.add(decayLabel, BorderLayout.NORTH);

      MouseBehaviorButtons mbButtons =
        new MouseBehaviorButtons(decayPlot, data.channels > 1, false);

      colorWidget = new ColorWidget(vMap, "Decay Color Mapping");

      zOverride = new JCheckBox("", false);
      zOverride.setToolTipText(
        "Toggles manual override of Z axis scale (Count)");
      zOverride.addActionListener(this);
      zScaleValue = new JTextField(9);
      zScaleValue.setToolTipText("Overridden Z axis scale value");
      zScaleValue.setEnabled(false);
      zScaleValue.getDocument().addDocumentListener(this);

      exportData = new JButton("Export");
      exportData.setToolTipText(
        "Exports the selected ROI's raw data to a text file");
      exportData.addActionListener(this);

      setProgress(progress, 990); // estimate: 99%

      JPanel scalePanel = new JPanel();
      scalePanel.setBorder(new TitledBorder("Z Scale Override"));
      scalePanel.setLayout(new BoxLayout(scalePanel, BoxLayout.X_AXIS));
      scalePanel.add(zOverride);
      scalePanel.add(zScaleValue);

      JPanel numbers = new JPanel();
      numbers.setBorder(new TitledBorder("Numbers"));
      numbers.setLayout(new GridLayout(5, 5));

      JLabel a1Label = new JLabel("a1 ");
      a1Label.setHorizontalAlignment(SwingConstants.RIGHT);
      numbers.add(a1Label);
      a1Param = new JTextField(9);
      a1Param.setEditable(false);
      numbers.add(a1Param);
      JLabel t1Label = new JLabel("t1 ");
      t1Label.setHorizontalAlignment(SwingConstants.RIGHT);
      numbers.add(t1Label);
      t1Param = new JTextField(9);
      t1Param.setEditable(false);
      numbers.add(t1Param);
      numbers.add(new JLabel(""));//TEMP

      JLabel a2Label = new JLabel("a2 ");
      a2Label.setHorizontalAlignment(SwingConstants.RIGHT);
      numbers.add(a2Label);
      a2Param = new JTextField(9);
      a2Param.setEditable(false);
      numbers.add(a2Param);
      JLabel t2Label = new JLabel("t2 ");
      t2Label.setHorizontalAlignment(SwingConstants.RIGHT);
      numbers.add(t2Label);
      t2Param = new JTextField(9);
      t2Param.setEditable(false);
      numbers.add(t2Param);
      numbers.add(new JLabel(""));//TEMP

      JLabel cLabel = new JLabel("c ");
      cLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      numbers.add(cLabel);
      cParam = new JTextField(9);
      cParam.setEditable(false);
      numbers.add(cParam);
      JLabel chi2Label = new JLabel("chi^2 ");
      chi2Label.setHorizontalAlignment(SwingConstants.RIGHT);
      numbers.add(chi2Label);
      chi2 = new JTextField(9);
      chi2.setEditable(false);
      numbers.add(chi2);
      numbers.add(new JLabel(""));//TEMP

      numbers.add(new JLabel(""));//TEMP
      numbers.add(new JLabel(""));//TEMP
      numbers.add(new JLabel(""));//TEMP
      numbers.add(new JLabel(""));//TEMP
      numbers.add(new JLabel(""));//TEMP

      numbers.add(new JLabel(""));//TEMP
      numbers.add(new JLabel(""));//TEMP
      numbers.add(new JLabel(""));//TEMP
      numbers.add(new JLabel(""));//TEMP
      numbers.add(new JLabel(""));//TEMP

      JPanel miscRow = new JPanel();
      miscRow.setLayout(new BoxLayout(miscRow, BoxLayout.X_AXIS));
      miscRow.add(scalePanel);
      miscRow.add(Box.createHorizontalStrut(5));
      miscRow.add(exportData);

      JPanel miscPanel = new JPanel();
      miscPanel.setLayout(new BoxLayout(miscPanel, BoxLayout.Y_AXIS));
      miscPanel.add(numbers);
      miscPanel.add(miscRow);

      JPanel stuff = new JPanel();
      stuff.setLayout(new BoxLayout(stuff, BoxLayout.X_AXIS));
      stuff.add(colorWidget);
      stuff.add(miscPanel);

      JPanel options = new JPanel();
      options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));
      options.add(mbButtons);
      options.add(stuff);
      options.setBorder(new EmptyBorder(8, 5, 8, 5));

      decayPane.add(options, BorderLayout.SOUTH);
      masterPane.add(decayPane, BorderLayout.CENTER);

      JPanel rightPanel = new JPanel() {
        public Dimension getMaximumSize() {
          Dimension pref = getPreferredSize();
          Dimension max = super.getMaximumSize();
          return new Dimension(pref.width, max.height);
        }
      };
      rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
      rightPanel.add(twoDPane);
      rightPanel.add(console.getWindow().getContentPane());
      BreakawayPanel breakawayPanel = new BreakawayPanel(masterPane,
        "Intensity Data - " + file.getName(), false);
      breakawayPanel.setEdge(BorderLayout.WEST);
      breakawayPanel.setUpEnabled(false);
      breakawayPanel.setDownEnabled(false);
      breakawayPanel.setContentPane(rightPanel);

      setProgress(progress, 999); // estimate: 99.9%

      // show window on screen
      masterWindow.pack();
      Dimension size = masterWindow.getSize();
      Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
      masterWindow.setLocation((screen.width - size.width) / 2,
        (screen.height - size.height) / 2);
      masterWindow.setVisible(true);
    }
    catch (Throwable t) {
      // display stack trace to the user
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      t.printStackTrace(new PrintStream(out));
      String stackTrace = new String(out.toByteArray());
      JOptionPane.showMessageDialog(null,
        "Sorry, " + SlimData.TITLE + " encountered a problem loading your " +
        "data:\n" + stackTrace, SlimData.TITLE, JOptionPane.ERROR_MESSAGE);
      System.exit(4);
    }

    setProgress(progress, 1000); // 100%
    progress.close();

    plotData(true, true, true);

    try { Thread.sleep(200); }
    catch (InterruptedException exc) { exc.printStackTrace(); }
  }

  // -- SlimPlotter methods --

  private Thread plotThread;
  private boolean plotCanceled;
  private boolean doProbe, doRecalc, doRescale, doRefit;

  public void plotProbe(boolean rescale) {
    plotData(true, true, rescale, true);
  }

  public void plotRegion(boolean recalc, boolean rescale, boolean refit) {
    plotData(false, recalc, rescale, refit);
  }

  public void plotData(boolean recalc, boolean rescale, boolean refit) {
    plotData(doProbe, recalc, rescale, refit);
  }

  /** Plots the data in a separate thread. */
  private void plotData(final boolean probe,
    final boolean recalc, final boolean rescale, final boolean refit)
  {
    final SlimPlotter sp = this;
    new Thread("PlotSpawner") {
      public void run() {
        synchronized (sp) {
          if (plotThread != null) {
            // wait for old thread to cancel out
            plotCanceled = true;
            try { plotThread.join(); }
            catch (InterruptedException exc) { exc.printStackTrace(); }
          }
          sp.doProbe = probe;
          sp.doRecalc = recalc;
          sp.doRescale = rescale;
          sp.doRefit = refit;
          plotCanceled = false;
          plotThread = new Thread(sp, "Plotter");
          plotThread.start();
        }
      }
    }.start();
  }

  // -- ActionListener methods --

  /** Handles checkbox and button presses. */
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src == menuFileExit) System.exit(0);
    else if (src == menuViewSaveProj) {
      String save = decayPlot.getProjectionControl().getSaveString();
      clip.setClipboardContents(save);
    }
    else if (src == menuViewLoadProj) {
      String save = clip.getClipboardContents();
      if (save != null) {
        try {
          decayPlot.getProjectionControl().setSaveString(save);
        }
        catch (VisADException exc) { }
        catch (RemoteException exc) { }
      }
    }
    else if (src == zOverride) {
      boolean manual = zOverride.isSelected();
      zScaleValue.setEnabled(manual);
      if (!manual) plotData(true, true, false);
    }
    else if (src == exportData) {
      // get output file from user
      JFileChooser jc = new JFileChooser(System.getProperty("user.dir"));
      jc.addChoosableFileFilter(new ExtensionFileFilter("txt",
        "Text files"));
      int rval = jc.showSaveDialog(exportData);
      if (rval != JFileChooser.APPROVE_OPTION) return;
      File file = jc.getSelectedFile();
      if (file == null) return;

      // write currently displayed binned data to file
      try {
        PrintWriter out = new PrintWriter(new FileWriter(file));
        out.println(data.timeBins + " x " + data.channels +
          " (count=" + twoDPane.getROICount() +
          ", percent=" + twoDPane.getROIPercent() +
          ", maxVal=" + maxVal + ")");
        for (int c=0; c<data.channels; c++) {
          for (int t=0; t<data.timeBins; t++) {
            if (t > 0) out.print("\t");
            float s = samps[data.timeBins * c + t];
            int is = (int) s;
            if (is == s) out.print(is);
            else out.print(s);
          }
          out.println();
        }
        out.close();
      }
      catch (IOException exc) {
        JOptionPane.showMessageDialog(exportData,
          "There was a problem writing the file: " + exc.getMessage(),
          SlimData.TITLE, JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  // -- ChangeListener methods --

  /** Handles slider changes. */
  public void stateChanged(ChangeEvent e) {
    plotData(true, false, true);
  }

  // -- DocumentListener methods --

  public void changedUpdate(DocumentEvent e) { documentUpdate(e); }
  public void insertUpdate(DocumentEvent e) { documentUpdate(e); }
  public void removeUpdate(DocumentEvent e) { documentUpdate(e); }

  private void documentUpdate(DocumentEvent e) {
    Document doc = e.getDocument();
    if (doc == zScaleValue.getDocument()) updateZAxis();
  }

  // -- ItemListener methods --

  public void itemStateChanged(ItemEvent e) {
    Object src = e.getSource();
    if (src == menuViewScale) {
      boolean scale = menuViewScale.getState();
      try { decayPlot.getGraphicsModeControl().setScaleEnable(scale); }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
    else if (src == menuViewBox) {
      boolean box = menuViewBox.getState();
      try { decayPlot.getDisplayRenderer().setBoxOn(box); }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
    else if (src == menuViewLines) {
      boolean lines = menuViewLines.getState();
      lineRend.toggle(lines);
    }
    else if (src == menuViewFWHMs) {
      boolean fwhms = menuViewFWHMs.getState();
      fwhmRend.toggle(fwhms);
    }
    else if (src == menuViewDataSurface) {
      boolean dataSurface = menuViewDataSurface.getState();
      dataSurfaceRend.toggle(dataSurface);
    }
    else if (src == menuViewDataLines) {
      boolean dataLines = menuViewDataLines.getState();
      dataLinesRend.toggle(dataLines);
    }
    else if (src == menuViewFitSurface) {
      boolean fitSurface = menuViewFitSurface.getState();
      fitSurfaceRend.toggle(fitSurface);
    }
    else if (src == menuViewFitLines) {
      boolean fitLines = menuViewFitLines.getState();
      fitLinesRend.toggle(fitLines);
    }
    else if (src == menuViewResSurface) {
      boolean resSurface = menuViewResSurface.getState();
      resSurfaceRend.toggle(resSurface);
    }
    else if (src == menuViewResLines) {
      boolean resLines = menuViewResLines.getState();
      resLinesRend.toggle(resLines);
    }
    else if (src == menuViewLogScale) plotData(true, true, true);
    else if (src == menuViewParallel) {
      boolean parallel = menuViewParallel.getState();
      int policy = parallel ?
        DisplayImplJ3D.PARALLEL_PROJECTION :
        DisplayImplJ3D.PERSPECTIVE_PROJECTION;
      try { decayPlot.getGraphicsModeControl().setProjectionPolicy(policy); }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
    else if (src == menuViewColorTau) {
      plotData(true, false, false);
    }
  }

  // -- Runnable methods --

  public void run() {
    debug("Plotting data: probe=" + doProbe + ", recalc=" + doRecalc +
      ", rescale=" + doRescale + ", refit=" + doRefit);

    decayPlot.disableAction();
    boolean doLog = menuViewLogScale.getState();

    if (doRecalc) {
      int progMax = data.channels * data.timeBins +
        (doRefit ? data.channels : 0) + 1;
      ProgressMonitor progress = new ProgressMonitor(null,
        "Plotting data", "Plotting data", 0, progMax);
      progress.setMillisToPopup(100);
      progress.setMillisToDecideToPopup(50);
      int p = 0;

      boolean doTauColors = menuViewColorTau.getState();

      // update scale labels for log scale
      if (doLog) {
        Hashtable zScaleLogTable = new Hashtable();
        double maxExp = Math.log(Integer.MAX_VALUE) / BASE_LOG;
        for (int exp=1, val=1; exp<maxExp; exp++, val*=BASE) {
          zScaleLogTable.put(new Double(Math.log(val) / BASE_LOG),
            exp > 3 ? "1e" + exp : "" + val);
        }
        AxisScale zScale = zMap.getAxisScale();
        try {
          zScale.setLabelTable(zScaleLogTable);
        }
        catch (VisADException exc) { exc.printStackTrace(); }
      }

      // calculate samples
      progress.setNote("Calculating sums");
      int numChanVis = 0;
      for (int c=0; c<data.channels; c++) {
        if (data.cVisible[c]) numChanVis++;
      }
      samps = new float[numChanVis * data.timeBins];
      maxVal = 0;
      float[] maxVals = new float[numChanVis];
      ICurveFitter[] curveFitters = null;
      if (doProbe) curveFitters = twoDPane.getCurveFitters();
      for (int c=0, cc=0; c<data.channels; c++) {
        if (!data.cVisible[c]) continue;
        int[] cfData = null;
        if (doProbe) cfData = curveFitters[c].getData();
        for (int t=0; t<data.timeBins; t++) {
          int ndx = data.timeBins * cc + t;
          int sum = 0;
          if (doProbe) {
            // use per-pixel lifetime probe rather than binned region
            sum = cfData[t];
          }
          else if (twoDPane.getROICount() == 1) {
            // single pixel "region"
            int roiX = twoDPane.getROIX();
            int roiY = twoDPane.getROIY();
            sum = data.data[c][roiY][roiX][t];
          }
          else {
            // sum pixels within region
            boolean[][] roiMask = twoDPane.getROIMask();
            for (int y=0; y<data.height; y++) {
              for (int x=0; x<data.width; x++) {
                if (roiMask[y][x]) sum += data.data[c][y][x][t];
              }
            }
          }
          samps[ndx] = sum;
          if (samps[ndx] > maxVal) maxVal = samps[ndx];
          if (samps[ndx] > maxVals[cc]) maxVals[cc] = samps[ndx];
          setProgress(progress, ++p, false);
          if (progress.isCanceled()) plotCanceled = true;
          if (plotCanceled) break;
        }
        if (plotCanceled) break;
        cc++;
      }

      // full width half maxes
      float[][][] fwhmLines = null;
      if (data.computeFWHMs) {
        log("Calculating full width half maxes");
        fwhmLines = new float[data.channels][3][2];
        int grandTotal = 0;
        for (int c=0, cc=0; c<data.channels; c++) {
          if (!data.cVisible[c]) continue;
          // sum across all pixels
          int[] sums = new int[data.timeBins];
          int sumTotal = 0;
          for (int t=0; t<data.timeBins; t++) {
            if (twoDPane.getROICount() == 1) {
              int roiX = twoDPane.getROIX();
              int roiY = twoDPane.getROIY();
              sums[t] = data.data[c][roiY][roiX][t];
            }
            else {
              boolean[][] roiMask = twoDPane.getROIMask();
              for (int y=0; y<data.height; y++) {
                for (int x=0; x<data.width; x++) {
                  if (roiMask[y][x]) sums[t] += data.data[c][y][x][t];
                }
              }
            }
            sumTotal += sums[t];
          }
          int maxSum = 0;
          for (int t=0; t<data.timeBins; t++) {
            if (sums[t] > maxSum) maxSum = sums[t];
          }
          grandTotal += sumTotal;
          // find full width half max
          float half = maxSum / 2f;
          float fwhm1 = Float.NaN, fwhm2 = Float.NaN;
          for (int t=0; t<data.timeBins-1; t++) {
            if (sums[t] <= half && sums[t + 1] >= half) { // upslope
              float q = (half - sums[t]) / (sums[t + 1] - sums[t]);
              // binsToNano
              fwhm1 = data.timeRange * (t + q) / (data.timeBins - 1);
            }
            else if (sums[t] >= half && sums[t + 1] <= half) { // downslope
              float q = (half - sums[t + 1]) / (sums[t] - sums[t + 1]);
              // binsToNano
              fwhm2 = data.timeRange * (t + 1 - q) / (data.timeBins - 1);
            }
            if (fwhm1 == fwhm1 && fwhm2 == fwhm2) break;
          }
          fwhmLines[c][0][0] = fwhm1;
          fwhmLines[c][0][1] = fwhm2;
          fwhmLines[c][1][0] = fwhmLines[c][1][1] =
            data.minWave + c * data.waveStep;
          if (doLog) half = linearToLog(half); // adjust for log scale
          fwhmLines[c][2][0] = fwhmLines[c][2][1] = half;
          String s = "#" + (c + 1);
          while (s.length() < 3) s = " " + s;
          float h1 = 1000 * fwhm1, h2 = 1000 * fwhm2;
          log("\tChannel " + s + ": fwhm = " + (h2 - h1) + " ps");
          log("\t             counts = " + sumTotal);
          log("\t             peak = " + maxSum);
          log("\t             center = " + ((h1 + h2) / 2) + " ps");
          log("\t             range = [" + h1 + ", " + h2 + "] ps");
          if (plotCanceled) break;
          cc++;
        }
        log("\tTotal counts = " + grandTotal);
      }

      // curve fitting
      double[][] fitResults = null;
      int[] fitFirst = null, fitLast = null, fitIter = null;
      double[] fitA1 = null, fitT1 = null;
      double[] fitA2 = null, fitT2 = null;
      double[] fitC = null, fitChi2 = null;
      if (data.allowCurveFit && doRefit) {
        // perform exponential curve fitting: y(x) = a * e^(-b*t) + c
        progress.setNote("Fitting curves");
        fitResults = new double[data.channels][];
        fitFirst = new int[data.channels];
        fitLast = new int[data.channels];
        fitIter = new int[data.channels];
        fitA1 = new double[data.channels];
        fitT1 = new double[data.channels];
        fitA2 = new double[data.channels];
        fitT2 = new double[data.channels];
        fitC = new double[data.channels];
        fitChi2 = new double[data.channels];
        tau = new float[data.channels][data.numExp];
        for (int c=0; c<data.channels; c++) Arrays.fill(tau[c], Float.NaN);

        int[] curveData = null;
        if (!doProbe) {
          curveData = new int[data.timeBins];

          StringBuffer equation = new StringBuffer();
          equation.append("y(t) = ");
          for (int i=0; i<data.numExp; i++) {
            equation.append("a");
            equation.append(i + 1);
            equation.append(" * e^(-t/");
            equation.append(TAU);
            equation.append(i + 1);
            equation.append(") + ");
          }
          equation.append("c");
          log("Computing fit parameters: " + equation.toString());
        }

        for (int c=0, cc=0; c<data.channels; c++) {
          if (!data.cVisible[c]) {
            fitResults[c] = null;
            continue;
          }

          ICurveFitter curveFitter = null;
          if (doProbe) {
            // use per-pixel lifetime results rather than fitting to region
            curveFitter = curveFitters[c];
          }
          else {
            // fit curve to region
            log("\tChannel #" + (c + 1) + ":");
            curveFitter = CurveCollection.newCurveFitter(data.curveFitterClass);
            for (int i=0; i<data.timeBins; i++) {
              curveData[i] = (int) samps[data.timeBins * cc + i];
            }
            curveFitter.setComponentCount(data.numExp);
            curveFitter.setData(curveData,
              data.maxPeak, data.timeBins - 1 - data.cutBins);
            curveFitter.estimate();
            for (int i=0; i<NUM_ITERATIONS; i++) curveFitter.iterate();
          }

          // extract fit results from curve fitter object
          double[][] results = curveFitter.getCurve();
          double aTotal = 0, cTotal = 0;
          for (int i=0; i<data.numExp; i++) {
            tau[c][i] = data.binsToPico((float) (1 / results[i][1]));
            aTotal += results[i][0];
            cTotal += results[i][2];
          }
          fitResults[c] = new double[2 * data.numExp + 1];
          for (int i=0; i<data.numExp; i++) {
            int e = 2 * i;
            fitResults[c][e] = results[i][0];
            fitResults[c][e + 1] = results[i][1];
            fitResults[c][2 * data.numExp] += results[i][2];
          }
          //fitResults[c][2 * data.numExp] = results[0][2];

          fitFirst[c] = curveFitter.getFirst();
          fitLast[c] = curveFitter.getLast();
          fitIter[c] = curveFitter.getIterations();
          fitChi2[c] = curveFitter.getReducedChiSquaredError();
          fitA1[c] = results[0][0];
          fitT1[c] = results[0][1];
          if (data.numExp > 1) {
            fitA2[c] = results[1][0];
            fitT2[c] = results[1][1];
          }
          fitC[c] = fitResults[c][2 * data.numExp];

          if (!doProbe) {
            // output results
            log("\t\treduced chi2=" + fitChi2[c]);
            log("\t\traw chi2=" + curveFitter.getChiSquaredError());
            log("\t\tbin range=[" + fitFirst[c] + ", " + fitLast[c] + "]");
            for (int i=0; i<data.numExp; i++) {
              log("\t\ta" + (i + 1) + "=" +
                (100 * results[i][0] / aTotal) + "%");
              log("\t\t" + TAU + (i + 1) + "=" + tau[c][i] + " ps");
            }
            log("\t\tc=" + cTotal);
          }

          setProgress(progress, ++p, false);
          cc++;
        }
      }

      tauMin = tauMax = Float.NaN;
      if (tau != null) {
        tauMin = tauMax = tau[0][0];
        for (int i=1; i<tau.length; i++) {
          if (tau[i][0] < tauMin) tauMin = tau[i][0];
          if (tau[i][0] > tauMax) tauMax = tau[i][0];
        }
      }
      StringBuffer sb = new StringBuffer();
      if (doProbe) sb.append("Per-pixel lifetime decay curve for ");
      else sb.append("Decay curve for ");
      if (twoDPane.getROICount() == 1) {
        int roiX = twoDPane.getROIX();
        int roiY = twoDPane.getROIY();
        sb.append("(");
        sb.append(roiX);
        sb.append(", ");
        sb.append(roiY);
        sb.append(")");
      }
      else {
        sb.append(twoDPane.getROICount());
        sb.append(" pixels (");
        sb.append(twoDPane.getROIPercent());
        sb.append("%)");
      }
      if (tauMin == tauMin && tauMax == tauMax) {
        sb.append("; ");
        sb.append(TAU);
        sb.append("=");
        if (tauMin != tauMax) {
          sb.append("[");
          sb.append(tauMin);
          sb.append(", ");
          sb.append(tauMax);
          sb.append("]");
        }
        else sb.append(tauMin);
        sb.append(" ps");
      }
      if (fitChi2 != null) {
        sb.append("; chi2=");
        for (int c=0; c<fitChi2.length; c++) {
          sb.append(c == 0 ? "[" : ", ");
          sb.append(fitChi2[c]);
        }
        sb.append("]");
      }
      if (fitIter != null) {
        sb.append("; iterations=");
        for (int c=0; c<fitIter.length; c++) {
          sb.append(c == 0 ? "[" : ", ");
          sb.append(fitIter[c]);
        }
        sb.append("]");
      }
      decayLabel.setText(sb.toString());

      // update Numbers fields
      // CTR TEMP - this is crap; refactor to MVC
      int activeC = twoDPane.getActiveC();
      a1Param.setText("" + fitA1[activeC]);
      t1Param.setText("" + fitT1[activeC]);
      a2Param.setText("" + fitA2[activeC]);
      t2Param.setText("" + fitT2[activeC]);
      cParam.setText("" + fitC[activeC]);
      chi2.setText("" + fitChi2[activeC]);

      try {
        // construct domain set for 3D surface plots
        float[][] bcGrid = new float[2][data.timeBins * numChanVis];
        for (int c=0, cc=0; c<data.channels; c++) {
          if (!data.cVisible[c]) continue;
          for (int t=0; t<data.timeBins; t++) {
            int ndx = data.timeBins * cc + t;
            bcGrid[0][ndx] = data.timeBins > 1 ?
              t * data.timeRange / (data.timeBins - 1) : 0;
            bcGrid[1][ndx] = data.channels > 1 ?
              c * (data.maxWave - data.minWave) /
              (data.channels - 1) + data.minWave : 0;
          }
          cc++;
        }
        Gridded2DSet bcSet = new Gridded2DSet(types.bc, bcGrid,
          data.timeBins, numChanVis, null, types.bcUnits, null, false);

        // compile color values for 3D surface plot
        float[] colors = new float[numChanVis * data.timeBins];
        for (int c=0, cc=0; c<data.channels; c++) {
          if (!data.cVisible[c]) continue;
          for (int t=0; t<data.timeBins; t++) {
            int ndx = data.timeBins * cc + t;
            colors[ndx] = doTauColors ?
              (tau == null ? Float.NaN : tau[c][0]) : samps[ndx];
          }
          cc++;
        }

        float[] fitSamps = null, residuals = null;
        if (fitResults != null) {
          // compute finite sampling matching fitted exponentials
          fitSamps = new float[numChanVis * data.timeBins];
          residuals = new float[numChanVis * data.timeBins];
          for (int c=0, cc=0; c<data.channels; c++) {
            if (!data.cVisible[c]) continue;
            double[] q = fitResults[c];
            for (int t=0; t<data.timeBins; t++) {
              int ndx = data.timeBins * cc + t;
              int et = t - fitFirst[c]; // adjust for peak offset
              if (et >= 0) {
                float sum = 0;
                for (int i=0; i<data.numExp; i++) {
                  int e = 2 * i;
                  // a * e ^ -bt
                  sum += (float) (q[e] * Math.exp(-q[e + 1] * et));
                }
                // c
                sum += (float) q[2 * data.numExp];
                fitSamps[ndx] = sum;
                if (t <= fitLast[c]) {
                  residuals[ndx] = samps[ndx] - fitSamps[ndx];
                }
              }
            }
            cc++;
          }
        }

        // construct "Data" plot
        if (doLog) {
          // convert samples to log values for plotting
          // this is done AFTER any computations involving samples (above)
          for (int i=0; i<samps.length; i++) {
            samps[i] = linearToLog(samps[i]);
          }
        }
        FlatField dataField = new FlatField(types.bcvFunc, bcSet);
        dataField.setSamples(new float[][] {samps, colors}, false);
        dataSurfaceRef.setData(dataField);
        dataLinesRef.setData(makeLines(dataField));

        // construct "Lines" plot
        if (fitFirst != null && fitLast != null) {
          RealTupleType lineType = null;
          float[][] firstSamps = null, lastSamps = null;
          if (data.channels == 1) { // 2-D plot
            lineType = types.bv;
            float first = fitFirst[0] * data.timeRange / (data.timeBins - 1);
            float last = fitLast[0] * data.timeRange / (data.timeBins - 1);
            float max = doLog ? linearToLog(maxVal) : maxVal;
            firstSamps = new float[][] {{first, first}, {0, max}};
            lastSamps = new float[][] {{last, last}, {0, max}};
          }
          else { // 3-D plot
            lineType = types.bc;
            firstSamps = new float[2][data.channels];
            lastSamps = new float[2][data.channels];
            for (int c=0; c<data.channels; c++) {
              float chan = c * (data.maxWave - data.minWave) /
                (data.channels - 1) + data.minWave;
              float first = fitFirst[c] * data.timeRange / (data.timeBins - 1);
              float last = fitLast[c] * data.timeRange / (data.timeBins - 1);
              firstSamps[0][c] = first;
              firstSamps[1][c] = chan;
              lastSamps[0][c] = last;
              lastSamps[1][c] = chan;
            }
          }
          Gridded2DSet[] lineSets = {
            new Gridded2DSet(lineType, firstSamps, firstSamps[0].length),
            new Gridded2DSet(lineType, lastSamps, lastSamps[0].length)
          };
          UnionSet lineSet = new UnionSet(lineType, lineSets);
          lineRef.setData(lineSet);
        }

        if (fwhmLines != null) {
          // construct "FWHMs" plot
          SampledSet[] fwhmSets = new SampledSet[data.channels];
          for (int c=0; c<data.channels; c++) {
            if (data.channels > 1) {
              fwhmSets[c] = new Gridded3DSet(types.bcv, fwhmLines[c], 2);
            }
            else {
              fwhmSets[c] = new Gridded2DSet(types.bv,
                new float[][] {fwhmLines[c][0], fwhmLines[c][2]}, 2);
            }
          }
          fwhmRef.setData(new UnionSet(fwhmSets));
        }

        if (fitSamps != null) {
          // construct "Fit" plot
          if (doLog) {
            // convert samples to log values for plotting
            // this is done AFTER any computations involving samples (above)
            for (int i=0; i<fitSamps.length; i++) {
              fitSamps[i] = linearToLog(fitSamps[i]);
            }
          }
          FlatField fit = new FlatField(types.bcvFuncFit, bcSet);
          fit.setSamples(new float[][] {fitSamps}, false);
          fitSurfaceRef.setData(fit);
          fitLinesRef.setData(makeLines(fit));
        }

        if (residuals != null) {
          // construct "Residuals" plot
          if (doLog) {
            // convert samples to log values for plotting
            // this is done AFTER any computations involving samples (above)
            for (int i=0; i<residuals.length; i++) {
              residuals[i] = linearToLog(residuals[i]);
            }
          }
          FlatField res = new FlatField(types.bcvFuncRes, bcSet);
          res.setSamples(new float[][] {residuals}, false);
          resSurfaceRef.setData(res);
          resLinesRef.setData(makeLines(res));
        }
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
      setProgress(progress, ++p, false);
      progress.close();

      // update scale labels for linear scale
      if (!doLog) {
        AxisScale zScale = zMap.getAxisScale();
        zScale.createStandardLabels(maxVal, 0, 0, maxVal);
      }

      colorWidget.updateColorScale();
    }

    if (doRescale) {
      float d = Float.NaN;
      boolean manual = zOverride.isSelected();
      if (manual) {
        try { d = Float.parseFloat(zScaleValue.getText()); }
        catch (NumberFormatException exc) { }
      }
      else {
        zScaleValue.getDocument().removeDocumentListener(this);
        zScaleValue.setText("" + maxVal);
        zScaleValue.getDocument().addDocumentListener(this);
      }
      float minZ = 0;
      float maxZ = d == d ? d : maxVal;
      if (doLog) maxZ = linearToLog(maxZ);
      if (maxZ != maxZ || maxZ == 0) maxZ = 1;
      try {
        zMap.setRange(minZ, maxZ);
        zMapFit.setRange(minZ, maxZ);
        zMapRes.setRange(minZ, maxZ);
        decayPlot.reAutoScale();
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }

    decayPlot.enableAction();
    plotThread = null;
  }

  // -- WindowListener methods --

  public void windowActivated(WindowEvent e) { }
  public void windowClosed(WindowEvent e) { }
  public void windowClosing(WindowEvent e) { System.exit(0); }
  public void windowDeactivated(WindowEvent e) { }
  public void windowDeiconified(WindowEvent e) { }
  public void windowIconified(WindowEvent e) { }
  public void windowOpened(WindowEvent e) { }

  // -- Helper methods --

  /** Converts linear value to logarithm value. */
  private float linearToLog(float v) {
    boolean inverse = v < 0;
    if (inverse) v = -v;
    float result = (float) v >= 1 ? (float) Math.log(v) / BASE_LOG : 0;
    return inverse ? -result : result;
  }

  private FieldImpl makeLines(FlatField surface) {
    try {
      // HACK - horrible conversion from aligned Gridded2DSet to ProductSet
      // probably could eliminate this by writing cleaner logic to convert
      // from 2D surface to 1D lines...
      Linear1DSet timeSet = new Linear1DSet(types.bType, 0,
        data.timeRange, data.timeBins, null,
        new Unit[] {types.bcUnits[0]}, null);
      int numChanVis = 0;
      for (int c=0; c<data.channels; c++) {
        if (data.cVisible[c]) numChanVis++;
      }
      float[][] cGrid = new float[1][numChanVis];
      for (int c=0, cc=0; c<data.channels; c++) {
        if (!data.cVisible[c]) continue;
        cGrid[0][cc++] = data.channels > 1 ?
          c * (data.maxWave - data.minWave) /
          (data.channels - 1) + data.minWave : 0;
      }
      Gridded1DSet waveSet = new Gridded1DSet(types.cType,
        cGrid, numChanVis, null, new Unit[] {types.bcUnits[1]}, null, false);
      ProductSet prodSet = new ProductSet(new SampledSet[] {timeSet, waveSet});
      float[][] samples = surface.getFloats(false);
      FunctionType ffType = (FunctionType) surface.getType();
      surface = new FlatField(ffType, prodSet);
      surface.setSamples(samples, false);

      return (FieldImpl) surface.domainFactor(types.cType);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return null;
  }

  private void updateZAxis() {
    float f = Float.NaN;
    try { f = Float.parseFloat(zScaleValue.getText()); }
    catch (NumberFormatException exc) { }
    if (f == f) plotData(false, true, false);
  }

  // -- Utility methods --

  protected static void setProgress(ProgressMonitor progress, int p) {
    setProgress(progress, p, true);
  }

  protected static void setProgress(ProgressMonitor progress,
    int base, int inc, double frac)
  {
    setProgress(progress, (int) (base + (long) inc * frac));
  }

  protected static void setProgress(ProgressMonitor progress,
    int p, boolean quitOnCancel)
  {
    progress.setProgress(p);
    if (quitOnCancel && progress.isCanceled()) System.exit(0);
  }

  /** Logs the given output to the appropriate location. */
  protected static void log(String msg) {
    final String message = msg;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() { System.err.println(message); }
    });
  }

  protected static void debug(String msg) {
    if (!DEBUG) return;
    String name = Thread.currentThread().getName();
    System.out.println("--> " + name + ": " + msg);
  }

  // -- Main method --

  public static void main(String[] args) throws Exception {
    new SlimPlotter(args);
  }

}
