//
// ColorPane.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2004 Curtis Rueden.

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

package loci.visbio.view;

import com.jgoodies.forms.builder.PanelBuilder;

import com.jgoodies.forms.factories.ButtonBarFactory;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.io.File;

import java.rmi.RemoteException;

import javax.swing.*;

import javax.swing.border.TitledBorder;

import javax.swing.event.*;

import loci.visbio.util.*;

import visad.*;

import visad.browser.Convert;

import visad.java2d.DisplayImplJ2D;

import visad.util.ColorMapWidget;
import visad.util.ExtensionFileFilter;
import visad.util.LabeledColorWidget;

/** ColorPane is a dialog pane for adjusting color settings. */
public class ColorPane extends DialogPane
  implements ChangeListener, DocumentListener, ItemListener
{

  // -- Constants --

  /** Names for preset color look-up table. */
  public static final String[] LUT_NAMES = {
    "Grayscale", "HSV", "RGB", null,
    "Red", "Green", "Blue", null,
    "Cyan", "Magenta", "Yellow", null,
    "Fire", "Ice"
  };

  /** Preset color look-up tables. */
  public static final float[][][] LUTS = {
    ColorUtil.LUT_GRAY, ColorUtil.LUT_HSV, ColorUtil.LUT_RGB, null,
    ColorUtil.LUT_RED, ColorUtil.LUT_GREEN, ColorUtil.LUT_BLUE, null,
    ColorUtil.LUT_CYAN, ColorUtil.LUT_MAGENTA, ColorUtil.LUT_YELLOW, null,
    ColorUtil.LUT_FIRE, ColorUtil.LUT_ICE
  };


  // -- GUI components --

  /** LUT file chooser. */
  protected JFileChooser fileBox;

  /** Color preview display. */
  protected DisplayImpl preview;

  /** GUI panel for color table widget. */
  protected JPanel widgetPane;

  /** Slider for level of brightness. */
  protected JSlider brightness;

  /** Label for current brightness value. */
  protected JLabel brightnessValue;

  /** Slider for level of contrast. */
  protected JSlider contrast;

  /** Label for current contrast value. */
  protected JLabel contrastValue;

  /** Option for RGB color model. */
  protected JRadioButton rgb;

  /** Option for HSV color model. */
  protected JRadioButton hsv;

  /** Option for composite coloring. */
  protected JRadioButton composite;

  /** Red/hue color map widget. */
  protected BioColorWidget red;

  /** Green/saturation color map widget. */
  protected BioColorWidget green;

  /** Blue/value color map widget. */
  protected BioColorWidget blue;

  /** Combo box for choosing color widgets. */
  protected JComboBox selector;

  /** Option for fixed color scaling. */
  protected JCheckBox fixed;

  /** Text field for low color scale value. */
  protected JTextField loVal;

  /** Label for fixed color scale. */
  protected JLabel toLabel;

  /** Text field for high color scale value. */
  protected JTextField hiVal;

  /** Button for loading color look-up table. */
  protected JButton lutLoad;

  /** Button for saving color look-up table. */
  protected JButton lutSave;

  /** Popup menu for selecting a preset color look-up table. */
  protected JPopupMenu lutsMenu;

  /** Button for selecting a color look-up table from a list of presets. */
  protected JButton lutPresets;


  // -- Other fields --

  /** Color handler for this color pane. */
  protected ColorHandler handler;

  /** List of mappings from range components to RGB. */
  protected ScalarMap[] maps;

  /** Should changes to the color components be ignored? */
  protected boolean ignore = false;


  // -- Constructor --

  /** Constructs a dialog for adjusting color parameters. */
  public ColorPane(ColorHandler h) {
    super("Edit colors - " + h.getWindow().getName());
    handler = h;

    // LUT file chooser
    fileBox = new JFileChooser(System.getProperty("user.dir"));
    fileBox.addChoosableFileFilter(
      new ExtensionFileFilter("lut", "Binary color table files"));

    // preview display
    preview = VisUtil.makeDisplay("bio_color_preview", false);

    // lay out left-hand panel
    PanelBuilder builder = new PanelBuilder(new FormLayout(
      "fill:pref:grow", "pref:grow, 3dlu, pref, 3dlu, pref"));
    CellConstraints cc = new CellConstraints();
    builder.add(preview.getComponent(), cc.xy(1, 1));
    builder.add(makeSliderPanel(), cc.xy(1, 3));
    builder.add(makeMappingsPanel(), cc.xy(1, 5));
    JPanel left = builder.getPanel();

    // lay out right-hand panel
    JPanel right = makeTablesPanel();

    // lay out main panel
    builder = new PanelBuilder(
      new FormLayout("pref:grow, 9dlu, pref:grow", "top:pref:grow"));
    builder.add(left, cc.xy(1, 1));
    builder.add(right, cc.xy(3, 1));
    add(builder.getPanel());
  }


  // -- ColorPane API methods - mutators --

  /** Sets the data displayed in the preview display to that given. */
  public void setPreviewData(FlatField ff)
    throws VisADException, RemoteException
  {
    VisUtil.setDisplayDisabled(preview, true);

    // clear old preview data
    preview.removeAllReferences();
    preview.clearMaps();
    widgetPane.removeAll();
    selector.removeActionListener(this);
    selector.removeAllItems();

    if (ff != null) {
      // guess at some default color mappings
      FunctionType ftype = (FunctionType) ff.getType();
      RealTupleType domain = ftype.getDomain();
      RealType[] xy = domain.getRealComponents();
      if (xy.length != 2) throw new VisADException("Invalid preview data");
      RealType rtX = xy[0];
      RealType rtY = xy[1];
      guessTypes();

      // create data reference
      DataReferenceImpl ref = new DataReferenceImpl("color_preview_ref");
      ref.setData(ff);

      // add scalar maps and data reference
      preview.addMap(new ScalarMap(rtX, Display.XAxis));
      preview.addMap(new ScalarMap(rtY, Display.YAxis));

      ScalarMap[] sm = handler.getMaps();
      maps = new ScalarMap[sm.length];
      for (int i=0; i<maps.length; i++) {
        maps[i] = (ScalarMap) sm[i].clone();
        preview.addMap(maps[i]);
        widgetPane.add(new LabeledColorWidget(new ColorMapWidget(maps[i])));
        selector.addItem(maps[i].getScalar().getName());
      }
      //preview.addReferences(new ImageRendererJ3D(), ref);
      preview.addReference(ref);

      // set aspect ratio
      GriddedSet set = (GriddedSet) ff.getDomainSet();
      float[] lo = set.getLow();
      float[] hi = set.getHi();
      double x = hi[0] - lo[0];
      double y = hi[1] - lo[1];
      double d = x > y ? x : y;
      double xasp = x / d;
      double yasp = y / d;
      ProjectionControl pc = preview.getProjectionControl();
      if (preview instanceof DisplayImplJ2D) {
        pc.setAspectCartesian(new double[] {xasp, yasp});
      }
      else pc.setAspectCartesian(new double[] {xasp, yasp, 1.0});

      // set zoom
      double[] zoom = preview.make_matrix(0, 0, 0,
        ViewHandler.DEFAULT_ZOOM_2D, 0, 0, 0);
      pc.setMatrix(zoom);
    }
    selector.addActionListener(this);

    VisUtil.setDisplayDisabled(preview, false);
  }

  /** Guesses reasonable mappings from range RealTypes to color components. */
  public void guessTypes() {
    red.removeItemListener(this);
    green.removeItemListener(this);
    blue.removeItemListener(this);

    ScalarMap[] sm = handler.getMaps();
    RealType[] rtypes = new RealType[sm.length];
    for (int i=0; i<sm.length; i++) rtypes[i] = (RealType) sm[i].getScalar();
    red.guessType(rtypes);
    green.guessType(rtypes);
    blue.guessType(rtypes);

    red.addItemListener(this);
    green.addItemListener(this);
    blue.addItemListener(this);
  }

  /** Sets the currently selected range component's color widget table. */
  public void setWidgetTable(float[][] table) {
    LabeledColorWidget lcw = (LabeledColorWidget)
      widgetPane.getComponent(selector.getSelectedIndex());
    float[][] oldTable = lcw.getTable();
    float[] alpha = oldTable.length > 3 ? oldTable[3] : null;
    table = ColorUtil.adjustColorTable(table, alpha, true);
    lcw.setTable(table);
  }

  // -- ColorPane API methods - accessors --

  /** Gets current brightness value. */
  public int getBrightness() { return brightness.getValue(); }

  /** Gets current contrast value. */
  public int getContrast() { return contrast.getValue(); }

  /** Gets current color model. */
  public int getModel() {
    return rgb.isSelected() ? ColorUtil.RGB_MODEL : (hsv.isSelected() ?
      ColorUtil.HSV_MODEL : ColorUtil.COMPOSITE_MODEL);
  }

  /** Gets current red RealType. */
  public RealType getRed() { return (RealType) red.getSelectedItem(); }

  /** Gets current green RealType. */
  public RealType getGreen() { return (RealType) green.getSelectedItem(); }

  /** Gets current blue RealType. */
  public RealType getBlue() { return (RealType) blue.getSelectedItem(); }

  /** Gets current color table range minimums. */
  public double[] getLo() {
    double[] lo = new double[maps.length];
    int ndx = selector.getSelectedIndex();
    for (int i=0; i<maps.length; i++) lo[i] = maps[i].getRange()[0];
    return lo;
  }

  /** Gets current color table range maximums. */
  public double[] getHi() {
    double[] hi = new double[maps.length];
    int ndx = selector.getSelectedIndex();
    for (int i=0; i<maps.length; i++) hi[i] = maps[i].getRange()[1];
    return hi;
  }

  /** Gets whether each color table has a fixed color range. */
  public boolean[] getFixed() {
    boolean[] fix = new boolean[maps.length];
    int ndx = selector.getSelectedIndex();
    for (int i=0; i<maps.length; i++) fix[i] = !maps[i].isAutoScale();
    return fix;
  }

  /** Gets current color table values. */
  public float[][][] getTables() {
    float[][][] tables = new float[maps.length][][];
    for (int i=0; i<maps.length; i++) {
      LabeledColorWidget lcw = (LabeledColorWidget) widgetPane.getComponent(i);
      tables[i] = lcw.getTable();
    }
    return tables;
  }


  // -- DialogPane API methods --

  /** Resets the color pane's components to their default states. */
  public void resetComponents() {
    int bright = handler.getBrightness();
    int cont = handler.getContrast();
    int model = handler.getModel();
    RealType rType = handler.getRed();
    RealType gType = handler.getGreen();
    RealType bType = handler.getBlue();
    double[] lo = handler.getLo();
    double[] hi = handler.getHi();
    boolean[] fix = handler.getFixed();

    ignore = true;
    brightness.setValue(bright);
    brightnessValue.setText("" + bright);
    contrast.setValue(cont);
    contrastValue.setText("" + cont);
    if (model == ColorUtil.RGB_MODEL) rgb.setSelected(true);
    else if (model == ColorUtil.HSV_MODEL) hsv.setSelected(true);
    else if (model == ColorUtil.COMPOSITE_MODEL) composite.setSelected(true);
    red.setSelectedItem(rType);
    green.setSelectedItem(gType);
    blue.setSelectedItem(bType);
    float[][][] tables = handler.getTables();
    if (maps != null) {
      VisUtil.setDisplayDisabled(preview, true);
      ColorUtil.setColorMode(preview, model);
      for (int i=0; i<maps.length; i++) {
        if (fix[i]) {
          try { maps[i].setRange(lo[i], hi[i]); }
          catch (VisADException exc) { exc.printStackTrace(); }
          catch (RemoteException exc) { exc.printStackTrace(); }
        }
        else maps[i].resetAutoScale();
        BaseColorControl cc = (BaseColorControl) maps[i].getControl();
        if (cc != null) {
          try { cc.setTable(tables[i]); }
          catch (VisADException exc) { exc.printStackTrace(); }
          catch (RemoteException exc) { exc.printStackTrace(); }
        }
      }
      VisUtil.setDisplayDisabled(preview, false);
      selector.setSelectedIndex(0);
    }
    ignore = false;
  }


  // -- ActionListener API methods --

  /** Handles GUI events. */
  public void actionPerformed(ActionEvent e) {
    Object o = e.getSource();
    if (o == rgb || o == hsv || o == composite) {
      int model = o == rgb ? ColorUtil.RGB_MODEL :
        o == hsv ? ColorUtil.HSV_MODEL : ColorUtil.COMPOSITE_MODEL;
      red.setModel(model);
      green.setModel(model);
      blue.setModel(model);
      guessTypes();
      doColorTables();
    }
    else if (o == selector) {
      int ndx = selector.getSelectedIndex();
      for (int i=0; i<widgetPane.getComponentCount(); i++) {
        widgetPane.getComponent(i).setVisible(i == ndx);
      }
      if (ndx >= 0) {
        ignore = true;
        fixed.setSelected(!maps[ndx].isAutoScale());
        double[] range = maps[ndx].getRange();
        loVal.setText("" + Convert.shortString(range[0]));
        hiVal.setText("" + Convert.shortString(range[1]));
        ignore = false;
      }
    }
    else if (o == lutLoad) {
      // ask user to specify the file
      int returnVal = fileBox.showOpenDialog(dialog);
      if (returnVal != JFileChooser.APPROVE_OPTION) return;
      File file = fileBox.getSelectedFile();

      float[][] table = ColorUtil.loadColorTable(file);
      if (table == null) {
        JOptionPane.showMessageDialog(dialog, "Error reading LUT file.",
          "Cannot load color table", JOptionPane.ERROR_MESSAGE);
      }
      else setWidgetTable(table);
    }
    else if (o == lutSave) {
      // ask user to specify the file
      int returnVal = fileBox.showSaveDialog(dialog);
      if (returnVal != JFileChooser.APPROVE_OPTION) return;
      File file = fileBox.getSelectedFile();
      String s = file.getAbsolutePath();
      if (!s.toLowerCase().endsWith(".lut")) file = new File(s + ".lut");

      LabeledColorWidget lcw = (LabeledColorWidget)
        widgetPane.getComponent(selector.getSelectedIndex());
      boolean success = ColorUtil.saveColorTable(lcw.getTable(), file);
      if (!success) {
        JOptionPane.showMessageDialog(dialog, "Error writing LUT file.",
          "Cannot save color table", JOptionPane.ERROR_MESSAGE);
      }
    }
    else if (o == lutPresets) {
      lutsMenu.show(lutPresets, lutPresets.getWidth(), 0);
    }
    else {
      // apply the chosen LUT preset
      String cmd = e.getActionCommand();
      if (cmd != null && cmd.startsWith("lut")) {
        setWidgetTable(LUTS[Integer.parseInt(cmd.substring(3))]);
      }
    }
    super.actionPerformed(e);
  }


  // -- ChangeListener API methods --

  /** Handles slider changes. */
  public void stateChanged(ChangeEvent e) { doColorTables(); }


  // -- DocumentListener API methods --

  /** Handles text field changes. */
  public void changedUpdate(DocumentEvent e) { doColorRanges(); }

  /** Handles text field additions. */
  public void insertUpdate(DocumentEvent e) { doColorRanges(); }

  /** Handles text field deletions. */
  public void removeUpdate(DocumentEvent e) { doColorRanges(); }


  // -- ItemListener API methods --

  /** ItemListener method for handling color mapping changes. */
  public void itemStateChanged(ItemEvent e) {
    if (e.getSource() == fixed) {
      boolean b = fixed.isSelected();
      loVal.setEnabled(b);
      toLabel.setEnabled(b);
      hiVal.setEnabled(b);
      doColorRanges();
    }
    else doColorTables();
  }


  // -- Helper methods --

  /** Constructs panel containing brightness and contrast sliders. */
  protected JPanel makeSliderPanel() {
    // brightness slider
    brightness = new JSlider(0, 256, 0);
    brightness.addChangeListener(this);
    brightness.setAlignmentY(JSlider.TOP_ALIGNMENT);
    brightness.setToolTipText("Adjusts the brightness of the display");

    // current brightness value
    brightnessValue = new JLabel("999");
    brightnessValue.setToolTipText("Current brightness value");

    // contrast slider
    contrast = new JSlider(0, 256, 0);
    contrast.addChangeListener(this);
    contrast.setAlignmentY(JSlider.TOP_ALIGNMENT);
    contrast.setMajorTickSpacing(ColorUtil.COLOR_DETAIL / 4);
    contrast.setMinorTickSpacing(ColorUtil.COLOR_DETAIL / 16);
    contrast.setPaintTicks(true);
    contrast.setToolTipText("Adjusts the contrast of the display");

    // current contrast value
    contrastValue = new JLabel("999");
    contrastValue.setToolTipText("Current contrast value");

    // RGB color model option
    rgb = new JRadioButton("RGB", true);
    rgb.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) rgb.setMnemonic('r');
    rgb.setToolTipText("Switches to a Red-Green-Blue color model");

    // HSV color model option
    hsv = new JRadioButton("HSV");
    hsv.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) hsv.setMnemonic('s');
    hsv.setToolTipText("Switches to a Hue-Saturation-Value color model");

    // composite color model option
    composite = new JRadioButton("Composite");
    composite.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) composite.setMnemonic('i');
    composite.setToolTipText(
      "Combines range values into a composite RGB color table");

    // color model button group
    ButtonGroup group = new ButtonGroup();
    group.add(rgb);
    group.add(hsv);
    group.add(composite);

    // lay out components
    PanelBuilder builder = new PanelBuilder(new FormLayout(
      "pref, 3dlu, pref:grow, 3dlu, pref",
      "pref, 3dlu, pref:grow, 3dlu, pref"
    ));
    CellConstraints cc = new CellConstraints();

    builder.addLabel("&Brightness", cc.xy(1, 1)).setLabelFor(brightness);
    builder.add(brightness, cc.xy(3, 1));
    builder.add(brightnessValue, cc.xy(5, 1));

    builder.addLabel("Co&ntrast", cc.xy(1, 3)).setLabelFor(contrast);
    builder.add(contrast, cc.xy(3, 3));
    builder.add(contrastValue, cc.xy(5, 3));

    builder.addLabel("Color model", cc.xy(1, 5));
    builder.add(FormsUtil.makeRow(rgb, hsv, composite), cc.xyw(3, 5, 3));

    return builder.getPanel();
  }

  /** Constructs panel containing color mapping combo boxes. */
  protected JPanel makeMappingsPanel() {
    // red/hue color map widget
    red = new BioColorWidget(0);
    red.addItemListener(this);
    if (!LAFUtil.isMacLookAndFeel()) red.setMnemonic('e');
    red.setToolTipText("Range mapping to Red/Hue color component");

    // green/saturation color map widget
    green = new BioColorWidget(1);
    green.addItemListener(this);
    if (!LAFUtil.isMacLookAndFeel()) green.setMnemonic('n');
    green.setToolTipText("Range mapping to Green/Saturation color component");

    // blue/value color map widget
    blue = new BioColorWidget(2);
    blue.addItemListener(this);
    if (!LAFUtil.isMacLookAndFeel()) blue.setMnemonic('u');
    blue.setToolTipText("Range mapping to Blue/Value color component");

    // lay out components
    return FormsUtil.makeRow(red, green, blue);
  }

  /** Constructs panel containing color table components. */
  protected JPanel makeTablesPanel() {
    // color widget selector
    //BaseRGBMap.USE_COLOR_CURSORS = true;
    selector = new JComboBox();
    selector.addActionListener(this);
    selector.setToolTipText("List of color tables for color components");

    // fixed color scaling option
    fixed = new JCheckBox("Fixed color range");
    fixed.addItemListener(this);
    if (!LAFUtil.isMacLookAndFeel()) fixed.setMnemonic('f');
    fixed.setToolTipText("Fixes color range between the given values");

    // low color scale value text field
    loVal = new JTextField("-", 6);
    loVal.getDocument().addDocumentListener(this);
    loVal.setToolTipText("Minimum color range value");

    // color scale label
    toLabel = new JLabel("to");

    // high color scale value text field
    hiVal = new JTextField("-", 6);
    hiVal.getDocument().addDocumentListener(this);
    hiVal.setToolTipText("Maximum color range value");

    // color widget pane
    widgetPane = new JPanel();
    widgetPane.setLayout(new BoxLayout(widgetPane, BoxLayout.X_AXIS));

    // load LUT button
    lutLoad = new JButton("Load LUT...");
    lutLoad.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) lutLoad.setMnemonic('l');
    lutLoad.setToolTipText("Loads a color table from disk");

    // save LUT button
    lutSave = new JButton("Save LUT...");
    lutSave.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) lutSave.setMnemonic('a');
    lutSave.setToolTipText("Saves this color table to disk");

    // LUTs popup menu
    lutsMenu = new JPopupMenu();
    for (int i=0; i<LUT_NAMES.length; i++) {
      if (LUT_NAMES[i] == null) lutsMenu.addSeparator();
      else {
        JMenuItem item = new JMenuItem(LUT_NAMES[i]);
        item.setActionCommand("lut" + i);
        item.addActionListener(this);
        lutsMenu.add(item);
      }
    }

    // LUTs button
    lutPresets = new JButton("LUTs >");
    lutPresets.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) lutPresets.setMnemonic('t');
    lutPresets.setToolTipText("Selects a LUT from the list of presets");

    // lay out components
    JPanel p = new JPanel();
    p.setLayout(new BorderLayout());
    p.add(FormsUtil.makeColumn(new Object[] {
      FormsUtil.makeRow("Ran&ge component", selector),
      FormsUtil.makeRow(new Object[] {fixed, loVal, toLabel, hiVal}),
      widgetPane, ButtonBarFactory.buildCenteredBar(lutLoad, lutSave,
      lutPresets)}, null, true));
    p.setBorder(new TitledBorder("Color tables"));
    return p;
  }

  /** Updates image color tables, when settings are adjusted. */
  protected void doColorTables() {
    if (ignore) return;

    int bright = getBrightness();
    int cont = getContrast();
    int model = getModel();
    RealType rType = getRed();
    RealType gType = getGreen();
    RealType bType = getBlue();

    brightnessValue.setText("" + bright);
    contrastValue.setText("" + cont);

    if (maps != null) {
      VisUtil.setDisplayDisabled(preview, true);
      float[][][] tables = ColorUtil.computeColorTables(maps,
        bright, cont, model, rType, gType, bType);
      ColorUtil.setColorMode(preview, model);
      ColorUtil.setColorTables(maps, tables);
      VisUtil.setDisplayDisabled(preview, false);
    }
  }

  /** Updates image color ranges, when settings are adjusted. */
  protected void doColorRanges() {
    if (ignore) return;

    VisUtil.setDisplayDisabled(preview, true);

    int ndx = selector.getSelectedIndex();
    if (ndx < 0) return;

    double lo;
    try { lo = Double.parseDouble(loVal.getText()); }
    catch (NumberFormatException exc) { lo = maps[ndx].getRange()[0]; }
    double hi;
    try { hi = Double.parseDouble(hiVal.getText()); }
    catch (NumberFormatException exc) { hi = maps[ndx].getRange()[1]; }
    boolean fix = fixed.isSelected();
    ColorUtil.setColorRange(preview, maps[ndx], lo, hi, fix);

    VisUtil.setDisplayDisabled(preview, false);
  }

}
