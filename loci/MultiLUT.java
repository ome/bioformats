//
// MultiLUT.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2003 Curtis Rueden.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci;

import visad.*;
import visad.data.*;
import visad.util.*;
import visad.bom.*;
import visad.java3d.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import java.io.IOException;
import java.rmi.RemoteException;

public class MultiLUT extends Object implements ActionListener {

  private static final int NFILES = 17;

  private TextType text = null;
  private RealType element = null, line = null, value = null;
  private TupleType text_tuple = null;

  private float[][] data_values = null;
  private float[][] values = null;
  private float[][] wedge_samples = null;

  private FlatField data = null;
  private FieldImpl text_field = null;
  private FlatField wedge = null;

  private DataReferenceImpl[] value_refs = null;
  private DataReferenceImpl[] hue_refs = null;

  private int npixels = 0;

  private JLabel minmax = null;

  private DisplayImplJ3D display1 = null;

  ScalarMap vmap = null;
  ScalarMap hmap = null;
  ScalarMap huexmap = null;

  private DataReferenceImpl line_ref = null;
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
    RealType[] value_types = new RealType[NFILES];
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
      value_types[i] = RealType.getRealType(name + (i+1), unit);
      float[][] temps = ff.getFloats(false);
      values[i] = temps[0];
      // System.out.println("data " + i + " type: " + value_types[i]);
    }

    npixels = values[0].length;

    RealTupleType range = new RealTupleType(value_types);
    FunctionType big_func = new FunctionType(domain, range);
    final FlatField big_data = new FlatField(big_func, set);
    big_data.setSamples(values, false);

    // RealType value = RealType.getRealType("value");
    RealType hue = RealType.getRealType("hue");
    RealType HUE = RealType.getRealType("HUE");
    RealTupleType new_range = new RealTupleType(value, hue);
    FunctionType new_func = new FunctionType(domain, new_range);
    data = new FlatField(new_func, set);
    data_values = new float[2][npixels];
    DataReferenceImpl ref1 = new DataReferenceImpl("ref1");
    ref1.setData(data);

    text = TextType.getTextType("text");
    RealType[] time = {RealType.Time};
    RealTupleType time_type = new RealTupleType(time);
    MathType[] mtypes = {element, line, text};
    text_tuple = new TupleType(mtypes);
    FunctionType text_function = new FunctionType(RealType.Time, text_tuple);
    Set time_set = new Linear1DSet(time_type, 0.0, 1.0, 2);
    text_field = new FieldImpl(text_function, time_set);
    DataReferenceImpl text_ref = new DataReferenceImpl("text_ref");
    text_ref.setData(text_field);

    Linear2DSet wedge_set =
      new Linear2DSet(domain, 0.0, 767.0, 768, 550.0, 570.0, 21);
    wedge = new FlatField(new_func, wedge_set);
    wedge_samples = new float[2][768 * 21];
    DataReferenceImpl wedge_ref = new DataReferenceImpl("wedge_ref");
    wedge_ref.setData(wedge);

    final DataReferenceImpl xref = new DataReferenceImpl("xref");

    // System.out.println("data type: " + new_func);

    display1 =
      new DisplayImplJ3D("display1", new TwoDDisplayRendererJ3D());
    ScalarMap xmap = new ScalarMap(element, Display.XAxis);
    display1.addMap(xmap);
    huexmap = new ScalarMap(HUE, Display.XAxis);
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
      TextControl text_control = (TextControl) ctrl;
      text_control.setSize(1.0);
      text_control.setJustification(TextControl.Justification.CENTER);
      text_control.setAutoSize(true);
    }

    // display1.getGraphicsModeControl().setScaleEnable(true);

    display1.addReference(ref1);

    DefaultRendererJ3D renderer = new DefaultRendererJ3D();
    display1.addReferences(renderer, xref);
    renderer.suppressExceptions(true);

    DefaultRendererJ3D text_renderer = new DefaultRendererJ3D();
    display1.addReferences(text_renderer, text_ref);
    text_renderer.suppressExceptions(true);

    DefaultRendererJ3D wedge_renderer = new DefaultRendererJ3D();
    display1.addReferences(wedge_renderer, wedge_ref);
    wedge_renderer.suppressExceptions(true);

    line_ref = new DataReferenceImpl("line");
    Gridded2DSet dummy_set = new Gridded2DSet(domain, null, 1);
    line_ref.setData(dummy_set);
    display1.addReferences(
      new RubberBandLineRendererJ3D(element, line), line_ref);

    final RealType channel = RealType.getRealType("channel");
    final RealType point = RealType.getRealType("point");
    RealType intensity = RealType.getRealType("intensity");
    final FunctionType spectrum_type = new FunctionType(channel, intensity);
    final FunctionType spectra_type = new FunctionType(point, spectrum_type);
    final FunctionType line_type = new FunctionType(point, intensity);
    final FunctionType lines_type = new FunctionType(channel, line_type);

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
        Set cell_set = (Set) line_ref.getData();
        if (cell_set == null) return;
        float[][] samples = cell_set.getSamples();
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
        FlatField line_field = (FlatField)
        //   big_data.resample(line, Data.WEIGHTED_AVERAGE, Data.NO_ERRORS);
          big_data.resample(line, Data.NEAREST_NEIGHBOR, Data.NO_ERRORS);
        float[][] line_samples =
          line_field.getFloats(false); // [NFILES][nsamp]
        Linear1DSet point_set = new Linear1DSet(point, 0.0, 1.0, nsamp);
        Integer1DSet channel_set = new Integer1DSet(channel, NFILES);
        FieldImpl spectra = new FieldImpl(spectra_type, point_set);
        for (int i=0; i<nsamp; i++) {
          FlatField spectrum = new FlatField(spectrum_type, channel_set);
          float[][] temp = new float[1][NFILES];
          for (int j=0; j<NFILES; j++) {
            temp[0][j] = line_samples[j][i];
          }
          spectrum.setSamples(temp, false);
          spectra.setSample(i, spectrum);
        }
        FieldImpl lines = new FieldImpl(lines_type, channel_set);
        for (int j=0; j<NFILES; j++) {
          FlatField linex = new FlatField(line_type, point_set);
          float[][] temp = new float[1][nsamp];
          for (int i=0; i<nsamp; i++) {
            temp[0][i] = line_samples[j][i];
          }
          linex.setSamples(temp, false);
          lines.setSample(j, linex);
        }

        ref2.setData(new Tuple(new Data[] {spectra, lines}));
      }
    };
    cell.addReference(line_ref);

    VisADSlider[] value_sliders = new VisADSlider[NFILES];
    VisADSlider[] hue_sliders = new VisADSlider[NFILES];
    value_refs = new DataReferenceImpl[NFILES];
    hue_refs = new DataReferenceImpl[NFILES];

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
      value_refs[i] = new DataReferenceImpl("value" + i);
      value_refs[i].setData(new Real(1.0));
      value_sliders[i] = new VisADSlider("value" + i, -100, 100, 100, 0.01,
                                         value_refs[i], RealType.Generic);
      left.add(value_sliders[i]);
      hue_refs[i] = new DataReferenceImpl("hue" + i);
      hue_refs[i].setData(new Real(1.0));
      hue_sliders[i] = new VisADSlider("hue" + i, -100, 100, 100, 0.01,
                                       hue_refs[i], RealType.Generic);
      center.add(hue_sliders[i]);
    }

    // slider button for setting all value sliders to 0
    JButton value_clear = new JButton("Zero values");
    value_clear.addActionListener(this);
    value_clear.setActionCommand("value_clear");
    left.add(Box.createVerticalStrut(10));
    left.add(value_clear);

    // slider button for setting all value sliders to 1
    JButton value_set = new JButton("One values");
    value_set.addActionListener(this);
    value_set.setActionCommand("value_set");
    left.add(value_set);

    // slider button for setting all hue sliders to 0
    JButton hue_clear = new JButton("Zero hues");
    hue_clear.addActionListener(this);
    hue_clear.setActionCommand("hue_clear");
    center.add(Box.createVerticalStrut(10));
    center.add(hue_clear);

    // slider button for setting all hue sliders to 1
    JButton hue_set = new JButton("One hues");
    hue_set.addActionListener(this);
    hue_set.setActionCommand("hue_set");
    center.add(hue_set);

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

    int WIDTH = 1200;
    int HEIGHT = 1000;

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setLocation(screenSize.width/2 - WIDTH/2,
                      screenSize.height/2 - HEIGHT/2);

    frame.setSize(WIDTH, HEIGHT);
    frame.setVisible(true);

    doit();

  }

  /** Handles button press events. */
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if (cmd.equals("compute")) {
      doit();
    }
    else if (cmd.equals("value_clear")) {
      try {
        for (int i=0; i<NFILES; i++) {
          value_refs[i].setData(new Real(0.0));
        }
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
    else if (cmd.equals("value_set")) {
      try {
        for (int i=0; i<NFILES; i++) {
          value_refs[i].setData(new Real(1.0));
        }
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
    else if (cmd.equals("hue_clear")) {
      try {
        for (int i=0; i<NFILES; i++) {
          hue_refs[i].setData(new Real(0.0));
        }
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
    else if (cmd.equals("hue_set")) {
      try {
        for (int i=0; i<NFILES; i++) {
          hue_refs[i].setData(new Real(1.0));
        }
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
  }

  public void doit() {
    try {
      float[] value_weights = new float[NFILES];
      float[] hue_weights = new float[NFILES];
      for (int i=0; i<NFILES; i++) {
        Real r = (Real) value_refs[i].getData();
        value_weights[i] = (float) r.getValue();
        r = (Real) hue_refs[i].getData();
        hue_weights[i] = (float) r.getValue();
      }
      float vmin = Float.MAX_VALUE;
      float vmax = Float.MIN_VALUE;
      float hmin = Float.MAX_VALUE;
      float hmax = Float.MIN_VALUE;
      for (int j=0; j<npixels; j++) {
        float v = 0, h = 0;
        for (int i=0; i<NFILES; i++) {
          v += value_weights[i] * values[i][j];
          h += hue_weights[i] * values[i][j];
        }
        data_values[0][j] = v;
        data_values[1][j] = h;
        if (v < vmin) vmin = v;
        if (v > vmax) vmax = v;
        if (h < hmin) hmin = h;
        if (h > hmax) hmax = h;
      }
      for (int i=0; i<768; i++) {
        float hue = hmin + (((float) i) / 767.0f) * (hmax - hmin);
        for (int k=i; k<768*21; k+=768) {
          wedge_samples[0][k] = vmax;
          wedge_samples[1][k] = hue;
        }
      }

      minmax.setText("vmin = " + vmin + "; vmax = " + vmax);
      double x1 = 0.0, x2 = 767.0, y = 525.0;
      text_field.setSample(0, new Tuple(text_tuple, new Scalar[] {
        new Real(element, x1), new Real(line, y), new Text(text, "" + hmin)
      }));
      text_field.setSample(1, new Tuple(text_tuple, new Scalar[] {
        new Real(element, x2), new Real(line, y), new Text(text, "" + hmax)
      }));

      display1.disableAction();
      vmap.setRange(vmin, vmax);
      hmap.setRange(hmin, hmax);
      huexmap.setRange(hmin, hmax);
      data.setSamples(data_values, false);
      wedge.setSamples(wedge_samples, false);
      display1.enableAction();
    }
    catch (VisADException ex) { ex.printStackTrace(); }
    catch (RemoteException ex) { ex.printStackTrace(); }
  }

}

