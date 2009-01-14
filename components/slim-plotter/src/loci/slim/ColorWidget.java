//
// ColorWidget.java
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

package loci.slim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.File;
import loci.formats.gui.ExtensionFileFilter;
import loci.visbio.util.ColorUtil;
import visad.*;
import visad.util.ColorMapWidget;
import visad.util.Util;

/**
 * VisAD color widget with min/max override and LUT buttons.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/slim-plotter/src/loci/slim/ColorWidget.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/slim-plotter/src/loci/slim/ColorWidget.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ColorWidget extends JPanel
  implements ActionListener, DocumentListener, ScalarMapListener
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

  private static final Color INVALID_COLOR = Color.red.brighter();

  private static final int PREF_HEIGHT = 120;

  // -- Static fields --

  private static JFileChooser lutBox;

  // -- Fields --

  private ScalarMap map;
  private ColorMapWidget widget;
  private JCheckBox cOverride;
  private JTextField cMinValue, cMaxValue;
  private JButton lutLoad, lutSave, lutPresets;
  private JPopupMenu lutsMenu;
  private Color validColor;
  private boolean updating;

  // -- Constructors --

  public ColorWidget(ScalarMap map) throws VisADException, RemoteException {
    this(map, null);
  }

  public ColorWidget(ScalarMap map, String title)
    throws VisADException, RemoteException
  {
    this.map = map;
    map.addScalarMapListener(this);

    widget = new ColorMapWidget(map);
    Dimension prefSize = widget.getPreferredSize();
    widget.setPreferredSize(new Dimension(prefSize.width, PREF_HEIGHT));

    cOverride = new JCheckBox("", false);
    cOverride.setToolTipText("Toggles manual override of color range");
    cOverride.addActionListener(this);
    cMinValue = new JTextField();
    Util.adjustTextField(cMinValue);
    cMinValue.setToolTipText("Overridden color minimum");
    cMinValue.setEnabled(false);
    cMinValue.getDocument().addDocumentListener(this);
    cMaxValue = new JTextField();
    Util.adjustTextField(cMaxValue);
    cMaxValue.setToolTipText("Overridden color maximum");
    cMaxValue.setEnabled(false);
    cMaxValue.getDocument().addDocumentListener(this);

    lutLoad = new JButton("Load LUT...");
    lutLoad.setToolTipText("Loads a color table from disk");
    lutLoad.addActionListener(this);

    lutSave = new JButton("Save LUT...");
    lutSave.setToolTipText("Saves this color table to disk");
    lutSave.addActionListener(this);

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

    lutPresets = new JButton("LUTs >");
    lutPresets.setToolTipText("Selects a LUT from the list of presets");
    lutPresets.addActionListener(this);

    if (lutBox == null) {
      lutBox = new JFileChooser(System.getProperty("user.dir"));
      lutBox.addChoosableFileFilter(
        new ExtensionFileFilter("lut", "Binary color table files"));
    }

    validColor = cMinValue.getBackground();

    if (title != null) setBorder(new TitledBorder(title));
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    add(widget);

    JPanel colorRange = new JPanel();
    colorRange.setLayout(new BoxLayout(colorRange, BoxLayout.X_AXIS));
    colorRange.add(cOverride);
    colorRange.add(cMinValue);
    colorRange.add(cMaxValue);
    add(colorRange);

    JPanel colorButtons = new JPanel();
    colorButtons.setLayout(new BoxLayout(colorButtons, BoxLayout.X_AXIS));
    colorButtons.add(lutLoad);
    colorButtons.add(lutSave);
    colorButtons.add(lutPresets);
    add(colorButtons);
  }

  // -- ColorWidget methods --

  /** Sets the currently selected range component's color widget table. */
  public void setWidgetTable(float[][] table) {
    widget.setTableView(table);
  }

  public void updateColorScale() {
    boolean manual = cOverride.isSelected();
    float min = Float.NaN, max = Float.NaN;
    if (manual) {
      try { min = Float.parseFloat(cMinValue.getText()); }
      catch (NumberFormatException exc) { }
      try { max = Float.parseFloat(cMaxValue.getText()); }
      catch (NumberFormatException exc) { }
      cMinValue.setBackground(min == min && min < max ?
        validColor : INVALID_COLOR);
      cMaxValue.setBackground(max == max && min < max ?
        validColor : INVALID_COLOR);
      if (min < max) updateScalarMap(min, max);
    }
    else {
      map.resetAutoScale();
      map.getDisplay().reAutoScale();
      updateMinMaxFields();
    }
  }

  // -- ActionListener methods --

  /** Handles checkbox and button presses. */
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src == cOverride) {
      updateManualOverride();
      updateColorScale();
    }
    else if (src == lutLoad) {
      // ask user to specify the file
      int returnVal = lutBox.showOpenDialog(this);
      if (returnVal != JFileChooser.APPROVE_OPTION) return;
      File file = lutBox.getSelectedFile();

      float[][] table = ColorUtil.loadColorTable(file);
      if (table == null) {
        JOptionPane.showMessageDialog(this, "Error reading LUT file.",
          "Cannot load color table", JOptionPane.ERROR_MESSAGE);
      }
      else setWidgetTable(table);
    }
    else if (src == lutSave) {
      // ask user to specify the file
      int returnVal = lutBox.showSaveDialog(this);
      if (returnVal != JFileChooser.APPROVE_OPTION) return;
      File file = lutBox.getSelectedFile();
      String s = file.getAbsolutePath();
      if (!s.toLowerCase().endsWith(".lut")) file = new File(s + ".lut");

      boolean success =
        ColorUtil.saveColorTable(widget.getTableView(), file);
      if (!success) {
        JOptionPane.showMessageDialog(this, "Error writing LUT file.",
            "Cannot save color table", JOptionPane.ERROR_MESSAGE);
      }
    }
    else if (src == lutPresets) {
      lutsMenu.show(lutPresets, lutPresets.getWidth(), 0);
    }
    else {
      String cmd = e.getActionCommand();
      if (cmd != null && cmd.startsWith("lut")) {
        // apply the chosen LUT preset
        setWidgetTable(LUTS[Integer.parseInt(cmd.substring(3))]);
      }
    }
  }

  // -- DocumentListener methods --

  public void changedUpdate(DocumentEvent e) { documentUpdate(e); }
  public void insertUpdate(DocumentEvent e) { documentUpdate(e); }
  public void removeUpdate(DocumentEvent e) { documentUpdate(e); }

  private void documentUpdate(DocumentEvent e) {
    updating = true;
    updateColorScale();
    updating = false;
  }

  // -- ScalarMapListener methods --

  public void mapChanged(ScalarMapEvent e) {
    int id = e.getId();
    final boolean isAuto = id == ScalarMapEvent.AUTO_SCALE;
    final boolean isManual = id == ScalarMapEvent.MANUAL;
    if (!isAuto && !isManual) return;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        updateManualOverride(isManual);
        updateMinMaxFields();
      }
    });
  }

  public void controlChanged(ScalarMapControlEvent e) { }

  // -- Helper methods --

  /** Updates text field range values to match map values. */
  protected void updateMinMaxFields() {
    if (updating || cOverride.isSelected()) return;
    SlimPlotter.debug("updateMinMaxFields");
    double[] range = map.getRange();
    String minText = "" + range[0];
    if (!minText.equals(cMinValue.getText())) {
      cMinValue.getDocument().removeDocumentListener(this);
      cMinValue.setText(minText);
      cMinValue.getDocument().addDocumentListener(this);
    }
    String maxText = "" + range[1];
    if (!maxText.equals(cMaxValue.getText())) {
      cMaxValue.getDocument().removeDocumentListener(this);
      cMaxValue.setText(maxText);
      cMaxValue.getDocument().addDocumentListener(this);
    }
  }

  /**
   * Updates min/max text field availability
   * to match the current override value.
   */
  protected void updateManualOverride() {
    updateManualOverride(cOverride.isSelected());
  }

  /**
   * Updates manual override checkbox and min/max
   * text field availaability to match the given value.
   */
  protected void updateManualOverride(boolean manual) {
    SlimPlotter.debug("updateManualOverride(" + manual + ")");
    if (manual != cOverride.isSelected()) {
      cOverride.removeActionListener(this);
      cOverride.setSelected(manual);
      cOverride.addActionListener(this);
    }
    cMinValue.setEnabled(manual);
    cMaxValue.setEnabled(manual);
  }

  /** Updates scalar map range to match the given values. */
  protected void updateScalarMap(float min, float max) {
    SlimPlotter.debug("updateScalarMap(" + min + ", " + max + ")");
    double[] range = map.getRange();
    if (min != range[0] || max != range[1]) {
      try { map.setRange(min, max); }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
  }

}
