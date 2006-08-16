//
// MultiLUT.java
//

package loci;

import visad.*;
import visad.data.*;
import visad.util.*;
import visad.bom.*;
import visad.java3d.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;
import java.rmi.RemoteException;

public class MultiLUT extends Object implements ActionListener {

  private static final int NFILES = 17;

  private TextType text = null;
  private RealType element = null, line = null, value = null;
  private TupleType textTuple = null;

  private float[][] dataValues = null;
  private float[][] values = null;
  private float[][] wedgeSamples = null;

  private FlatField data = null;
  private FieldImpl textField = null;
  private FlatField wedge = null;

  private DataReferenceImpl[] valueRefs = null;
  private DataReferenceImpl[] hueRefs = null;

  private int npixels = 0;

  private JLabel minmax = null;

  private DisplayImplJ3D display1 = null;

  ScalarMap vmap = null;
  ScalarMap hmap = null;
  ScalarMap huexmap = null;

  private DataReferenceImpl lineRef = null;
  /**
      run with 'java -mx256m MultiLUT'
      in directory with SPB1.PIC, SPB2.PIC, ..., SPB17.PIC
  */
  public static void main(String args[])
         throws IOException, VisADException, RemoteException {

    MultiLUT ml = new MultiLUT();
    ml.go(args);
  }

  public void go(String args[])
         throws IOException, VisADException, RemoteException {

    String dir = "";
    String slash = System.getProperty("file.separator");
    if (args.length > 0) {
      dir = args[0];
      if (!dir.endsWith(slash)) dir = dir + slash;
    }

    RealTupleType domain = null;
    Unit unit = null;
    String name = null;
    Set set = null;
    RealType[] valueTypes = new RealType[NFILES];
    values = new float[NFILES][];

    DefaultFamily loader = new DefaultFamily("loader");

    for (int i=0; i<NFILES; i++) {
      Tuple tuple = (Tuple) loader.open(dir + "SPB" + (i+1) + ".PIC");
      FieldImpl field = (FieldImpl) tuple.getComponent(0);
      FlatField ff = (FlatField) field.getSample(0);
      set = ff.getDomainSet();
/*
System.out.println("set = " + set);
set = Linear2DSet: Length = 393216
  Dimension 1: Length = 768 Range = 0.0 to 767.0
  Dimension 2: Length = 512 Range = 0.0 to 511.0
*/
      if (i == 0) {
        FunctionType func = (FunctionType) ff.getType();
        domain = func.getDomain();
        element = (RealType) domain.getComponent(0);
        line = (RealType) domain.getComponent(1);
        value = (RealType) func.getRange();
        unit = value.getDefaultUnit();
        name = value.getName();
      }
      valueTypes[i] = RealType.getRealType(name + (i+1), unit);
      float[][] temps = ff.getFloats(false);
      values[i] = temps[0];
      // System.out.println("data " + i + " type: " + valueTypes[i]);
    }

    npixels = values[0].length;

    RealTupleType range = new RealTupleType(valueTypes);
    FunctionType bigFunc = new FunctionType(domain, range);
    final FlatField bigData = new FlatField(bigFunc, set);
    bigData.setSamples(values, false);

    // RealType value = RealType.getRealType("value");
    RealType hue = RealType.getRealType("hue");
    RealType bigHue = RealType.getRealType("HUE");
    RealTupleType newRange = new RealTupleType(value, hue);
    FunctionType newFunc = new FunctionType(domain, newRange);
    data = new FlatField(newFunc, set);
    dataValues = new float[2][npixels];
    DataReferenceImpl ref1 = new DataReferenceImpl("ref1");
    ref1.setData(data);

    text = TextType.getTextType("text");
    RealType[] time = {RealType.Time};
    RealTupleType timeType = new RealTupleType(time);
    MathType[] mtypes = {element, line, text};
    textTuple = new TupleType(mtypes);
    FunctionType textFunction = new FunctionType(RealType.Time, textTuple);
    Set timeSet = new Linear1DSet(timeType, 0.0, 1.0, 2);
    textField = new FieldImpl(textFunction, timeSet);
    DataReferenceImpl textRef = new DataReferenceImpl("textRef");
    textRef.setData(textField);

    Linear2DSet wedgeSet =
      new Linear2DSet(domain, 0.0, 767.0, 768, 550.0, 570.0, 21);
    wedge = new FlatField(newFunc, wedgeSet);
    wedgeSamples = new float[2][768 * 21];
    DataReferenceImpl wedgeRef = new DataReferenceImpl("wedgeRef");
    wedgeRef.setData(wedge);

    final DataReferenceImpl xref = new DataReferenceImpl("xref");

    // System.out.println("data type: " + newFunc);

    display1 =
      new DisplayImplJ3D("display1", new TwoDDisplayRendererJ3D());
    ScalarMap xmap = new ScalarMap(element, Display.XAxis);
    display1.addMap(xmap);
    huexmap = new ScalarMap(bigHue, Display.XAxis);
    display1.addMap(huexmap);
    ScalarMap ymap = new ScalarMap(line, Display.YAxis);
    display1.addMap(ymap);
    ymap.setRange(0.0, 511.0);
    vmap = new ScalarMap(value, Display.Value);
    display1.addMap(vmap);
    hmap = new ScalarMap(hue, Display.Hue);
    display1.addMap(hmap);
    ScalarMap textmap = new ScalarMap(text, Display.Text);
    display1.addMap(textmap);
    display1.addMap(new ConstantMap(1.0, Display.Saturation));

    Control ctrl = textmap.getControl();
    if (ctrl != null && ctrl instanceof TextControl) {
      TextControl textControl = (TextControl) ctrl;
      textControl.setSize(1.0);
      textControl.setJustification(TextControl.Justification.CENTER);
      textControl.setAutoSize(true);
    }

    // display1.getGraphicsModeControl().setScaleEnable(true);

    display1.addReference(ref1);

    DefaultRendererJ3D renderer = new DefaultRendererJ3D();
    display1.addReferences(renderer, xref);
    renderer.suppressExceptions(true);

    DefaultRendererJ3D textRenderer = new DefaultRendererJ3D();
    display1.addReferences(textRenderer, textRef);
    textRenderer.suppressExceptions(true);

    DefaultRendererJ3D wedgeRenderer = new DefaultRendererJ3D();
    display1.addReferences(wedgeRenderer, wedgeRef);
    wedgeRenderer.suppressExceptions(true);

    lineRef = new DataReferenceImpl("line");
    Gridded2DSet dummySet = new Gridded2DSet(domain, null, 1);
    lineRef.setData(dummySet);
    display1.addReferences(
      new RubberBandLineRendererJ3D(element, line), lineRef);

    final RealType channel = RealType.getRealType("channel");
    final RealType point = RealType.getRealType("point");
    RealType intensity = RealType.getRealType("intensity");
    final FunctionType spectrumType = new FunctionType(channel, intensity);
    final FunctionType spectraType = new FunctionType(point, spectrumType);
    final FunctionType lineType = new FunctionType(point, intensity);
    final FunctionType linesType = new FunctionType(channel, lineType);

    final DataReferenceImpl ref2 = new DataReferenceImpl("ref2");

    DisplayImplJ3D display2 =
      new DisplayImplJ3D("display2");
    ScalarMap xmap2 = new ScalarMap(channel, Display.XAxis);
    display2.addMap(xmap2);
    ScalarMap ymap2 = new ScalarMap(intensity, Display.YAxis);
    display2.addMap(ymap2);
    ScalarMap zmap2 = new ScalarMap(point, Display.ZAxis);
    display2.addMap(zmap2);

    display2.getGraphicsModeControl().setScaleEnable(true);

    DefaultRendererJ3D renderer2 = new DefaultRendererJ3D();
    display2.addReferences(renderer2, ref2);
    renderer2.suppressExceptions(true);

    final RealTupleType fdomain = domain;
    CellImpl cell = new CellImpl() {
      public void doAction() throws VisADException, RemoteException {
        Set cellSet = (Set) lineRef.getData();
        if (cellSet == null) return;
        float[][] samples = cellSet.getSamples();
        if (samples == null) return;
        // System.out.println("box (" + samples[0][0] + ", " + samples[1][0] +
        //   ") to (" + samples[0][1] + ", " + samples[1][1] + ")");
        float x1 = samples[0][0];
        float y1 = samples[1][0];
        float x2 = samples[0][1];
        float y2 = samples[1][1];

        double dist = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        int nsamp = (int) (dist + 1.0);
        if (nsamp < 2) nsamp = 2;
        float[][] ss = new float[2][nsamp];
        for (int i=0; i<nsamp; i++) {
          float a = ((float) i) / (nsamp - 1.0f);
          ss[0][i] = x1 + a * (x2 - x1);
          ss[1][i] = y1 + a * (y2 - y1);
        }
        Gridded2DSet line = new Gridded2DSet(fdomain, ss, nsamp);
        xref.setData(line);
        FlatField lineField = (FlatField)
        //   bigData.resample(line, Data.WEIGHTED_AVERAGE, Data.NO_ERRORS);
          bigData.resample(line, Data.NEAREST_NEIGHBOR, Data.NO_ERRORS);
        float[][] lineSamples =
          lineField.getFloats(false); // [NFILES][nsamp]
        Linear1DSet pointSet = new Linear1DSet(point, 0.0, 1.0, nsamp);
        Integer1DSet channelSet = new Integer1DSet(channel, NFILES);
        FieldImpl spectra = new FieldImpl(spectraType, pointSet);
        for (int i=0; i<nsamp; i++) {
          FlatField spectrum = new FlatField(spectrumType, channelSet);
          float[][] temp = new float[1][NFILES];
          for (int j=0; j<NFILES; j++) {
            temp[0][j] = lineSamples[j][i];
          }
          spectrum.setSamples(temp, false);
          spectra.setSample(i, spectrum);
        }
        FieldImpl lines = new FieldImpl(linesType, channelSet);
        for (int j=0; j<NFILES; j++) {
          FlatField linex = new FlatField(lineType, pointSet);
          float[][] temp = new float[1][nsamp];
          for (int i=0; i<nsamp; i++) {
            temp[0][i] = lineSamples[j][i];
          }
          linex.setSamples(temp, false);
          lines.setSample(j, linex);
        }

        ref2.setData(new Tuple(new Data[] {spectra, lines}));
      }
    };
    cell.addReference(lineRef);

    VisADSlider[] valueSliders = new VisADSlider[NFILES];
    VisADSlider[] hueSliders = new VisADSlider[NFILES];
    valueRefs = new DataReferenceImpl[NFILES];
    hueRefs = new DataReferenceImpl[NFILES];

    JFrame frame = new JFrame("VisAD MultiLUT");
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {System.exit(0);}
    });

    // create JPanel in frame
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    panel.setAlignmentY(JPanel.TOP_ALIGNMENT);
    panel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
    frame.setContentPane(panel);

    JPanel left = new JPanel();
    left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
    left.setAlignmentY(JPanel.TOP_ALIGNMENT);
    left.setAlignmentX(JPanel.LEFT_ALIGNMENT);

    JPanel center = new JPanel();
    center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
    center.setAlignmentY(JPanel.TOP_ALIGNMENT);
    center.setAlignmentX(JPanel.LEFT_ALIGNMENT);

    JPanel right = new JPanel();
    right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
    right.setAlignmentY(JPanel.TOP_ALIGNMENT);
    right.setAlignmentX(JPanel.LEFT_ALIGNMENT);

    panel.add(left);
    panel.add(center);
    panel.add(right);

    Dimension d = new Dimension(300, 1000);
    left.setMaximumSize(d);
    center.setMaximumSize(d);

    for (int i=0; i<NFILES; i++) {
      valueRefs[i] = new DataReferenceImpl("value" + i);
      valueRefs[i].setData(new Real(1.0));
      valueSliders[i] = new VisADSlider("value" + i, -100, 100, 100, 0.01,
                                         valueRefs[i], RealType.Generic);
      left.add(valueSliders[i]);
      hueRefs[i] = new DataReferenceImpl("hue" + i);
      hueRefs[i].setData(new Real(1.0));
      hueSliders[i] = new VisADSlider("hue" + i, -100, 100, 100, 0.01,
                                       hueRefs[i], RealType.Generic);
      center.add(hueSliders[i]);
    }

    // slider button for setting all value sliders to 0
    JButton valueClear = new JButton("Zero values");
    valueClear.addActionListener(this);
    valueClear.setActionCommand("valueClear");
    left.add(Box.createVerticalStrut(10));
    left.add(valueClear);

    // slider button for setting all value sliders to 1
    JButton valueSet = new JButton("One values");
    valueSet.addActionListener(this);
    valueSet.setActionCommand("valueSet");
    left.add(valueSet);

    // slider button for setting all hue sliders to 0
    JButton hueClear = new JButton("Zero hues");
    hueClear.addActionListener(this);
    hueClear.setActionCommand("hueClear");
    center.add(Box.createVerticalStrut(10));
    center.add(hueClear);

    // slider button for setting all hue sliders to 1
    JButton hueSet = new JButton("One hues");
    hueSet.addActionListener(this);
    hueSet.setActionCommand("hueSet");
    center.add(hueSet);

    // slider button for setting all hue sliders to 0
    right.add(display1.getComponent());
    right.add(display2.getComponent());

    // vmin and vmax labels
    minmax = new JLabel(" ");
    left.add(Box.createVerticalStrut(30));
    left.add(minmax);

    // "GO" button for applying computation in sliders
    JButton compute = new JButton("Compute");
    compute.addActionListener(this);
    compute.setActionCommand("compute");
    left.add(Box.createVerticalStrut(10));
    left.add(compute);

    int width = 1200;
    int height = 1000;

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setLocation(screenSize.width/2 - width/2,
                      screenSize.height/2 - height/2);

    frame.setSize(width, height);
    frame.setVisible(true);

    doit();

  }

  /** Handles button press events. */
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if (cmd.equals("compute")) {
      doit();
    }
    else if (cmd.equals("valueClear")) {
      try {
        for (int i=0; i<NFILES; i++) {
          valueRefs[i].setData(new Real(0.0));
        }
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
    else if (cmd.equals("valueSet")) {
      try {
        for (int i=0; i<NFILES; i++) {
          valueRefs[i].setData(new Real(1.0));
        }
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
    else if (cmd.equals("hueClear")) {
      try {
        for (int i=0; i<NFILES; i++) {
          hueRefs[i].setData(new Real(0.0));
        }
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
    else if (cmd.equals("hueSet")) {
      try {
        for (int i=0; i<NFILES; i++) {
          hueRefs[i].setData(new Real(1.0));
        }
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
  }

  public void doit() {
    try {
      float[] valueWeights = new float[NFILES];
      float[] hueWeights = new float[NFILES];
      for (int i=0; i<NFILES; i++) {
        Real r = (Real) valueRefs[i].getData();
        valueWeights[i] = (float) r.getValue();
        r = (Real) hueRefs[i].getData();
        hueWeights[i] = (float) r.getValue();
      }
      float vmin = Float.MAX_VALUE;
      float vmax = Float.MIN_VALUE;
      float hmin = Float.MAX_VALUE;
      float hmax = Float.MIN_VALUE;
      for (int j=0; j<npixels; j++) {
        float v = 0, h = 0;
        for (int i=0; i<NFILES; i++) {
          v += valueWeights[i] * values[i][j];
          h += hueWeights[i] * values[i][j];
        }
        dataValues[0][j] = v;
        dataValues[1][j] = h;
        if (v < vmin) vmin = v;
        if (v > vmax) vmax = v;
        if (h < hmin) hmin = h;
        if (h > hmax) hmax = h;
      }
      for (int i=0; i<768; i++) {
        float hue = hmin + (((float) i) / 767.0f) * (hmax - hmin);
        for (int k=i; k<768*21; k+=768) {
          wedgeSamples[0][k] = vmax;
          wedgeSamples[1][k] = hue;
        }
      }

      minmax.setText("vmin = " + vmin + "; vmax = " + vmax);
      double x1 = 0.0, x2 = 767.0, y = 525.0;
      textField.setSample(0, new Tuple(textTuple, new Scalar[] {
        new Real(element, x1), new Real(line, y), new Text(text, "" + hmin)
      }));
      textField.setSample(1, new Tuple(textTuple, new Scalar[] {
        new Real(element, x2), new Real(line, y), new Text(text, "" + hmax)
      }));

      display1.disableAction();
      vmap.setRange(vmin, vmax);
      hmap.setRange(hmin, hmax);
      huexmap.setRange(hmin, hmax);
      data.setSamples(dataValues, false);
      wedge.setSamples(wedgeSamples, false);
      display1.enableAction();
    }
    catch (VisADException ex) { ex.printStackTrace(); }
    catch (RemoteException ex) { ex.printStackTrace(); }
  }

}

