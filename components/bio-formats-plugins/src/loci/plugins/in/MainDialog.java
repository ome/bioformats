/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.plugins.in;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import ij.gui.GenericDialog;

import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Label;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import loci.plugins.util.WindowTools;

/**
 * Bio-Formats Importer general options dialog box.
 */
public class MainDialog extends ImporterDialog
  implements FocusListener, ItemListener, MouseListener
{

  // -- Constants --

  /** Initial message to display in help text box. */
  public static final String INFO_DEFAULT =
    "<i>Select an option for a detailed explanation. " +
    "Documentation written by Glen MacDonald and Curtis Rueden.</i>";

  // -- Fields --

  protected Checkbox autoscaleBox;
  protected Choice colorModeChoice;
  protected Checkbox concatenateBox;
  protected Checkbox cropBox;
  protected Checkbox groupFilesBox;
  protected Checkbox ungroupFilesBox;
  protected Checkbox openAllSeriesBox;
  //protected Checkbox recordBox;
  protected Checkbox showMetadataBox;
  protected Checkbox showOMEXMLBox;
  protected Checkbox showROIsBox;
  protected Choice roisModeChoice;
  protected Checkbox specifyRangesBox;
  protected Checkbox splitZBox;
  protected Checkbox splitTBox;
  protected Checkbox splitCBox;
  protected Choice stackFormatChoice;
  protected Choice stackOrderChoice;
  protected Checkbox swapDimsBox;
  protected Checkbox virtualBox;
  protected Checkbox stitchTilesBox;

  protected Map<Component, String> infoTable;
  protected JEditorPane infoPane;

  // -- Constructor --

  /** Creates a general options dialog for the Bio-Formats Importer. */
  public MainDialog(ImportProcess process) {
    super(process);
  }

  // -- ImporterDialog methods --

  @Override
  protected boolean needPrompt() {
    return !process.isWindowless();
  }

  @Override
  protected GenericDialog constructDialog() {
    GenericDialog gd = new GenericDialog("Bio-Formats Import Options");
    addCheckbox(gd, ImporterOptions.KEY_AUTOSCALE);
    addChoice(gd, ImporterOptions.KEY_COLOR_MODE);
    addCheckbox(gd, ImporterOptions.KEY_CONCATENATE);
    addCheckbox(gd, ImporterOptions.KEY_CROP);
    addCheckbox(gd, ImporterOptions.KEY_GROUP_FILES);
    addCheckbox(gd, ImporterOptions.KEY_UNGROUP_FILES);
    addCheckbox(gd, ImporterOptions.KEY_OPEN_ALL_SERIES);
    addCheckbox(gd, ImporterOptions.KEY_QUIET); // NB: invisible
    //addCheckbox(gd, ImporterOptions.KEY_RECORD);
    addCheckbox(gd, ImporterOptions.KEY_SHOW_METADATA);
    addCheckbox(gd, ImporterOptions.KEY_SHOW_OME_XML);
    addCheckbox(gd, ImporterOptions.KEY_SHOW_ROIS);
    addChoice(gd, ImporterOptions.KEY_ROIS_MODE);
    addCheckbox(gd, ImporterOptions.KEY_SPECIFY_RANGES);
    addCheckbox(gd, ImporterOptions.KEY_SPLIT_Z);
    addCheckbox(gd, ImporterOptions.KEY_SPLIT_T);
    addCheckbox(gd, ImporterOptions.KEY_SPLIT_C);
    addChoice(gd, ImporterOptions.KEY_STACK_FORMAT);
    addChoice(gd, ImporterOptions.KEY_STACK_ORDER);
    addCheckbox(gd, ImporterOptions.KEY_SWAP_DIMS);
    addCheckbox(gd, ImporterOptions.KEY_VIRTUAL);
    addCheckbox(gd, ImporterOptions.KEY_STITCH_TILES);
    rebuildDialog(gd);
    return gd;
  }

  @Override
  protected boolean harvestResults(GenericDialog gd) {
    options.setAutoscale(gd.getNextBoolean());
    options.setColorMode(options.getColorModes()[gd.getNextChoiceIndex()]);
    options.setConcatenate(gd.getNextBoolean());
    options.setCrop(gd.getNextBoolean());
    options.setGroupFiles(gd.getNextBoolean());
    options.setUngroupFiles(gd.getNextBoolean());
    options.setOpenAllSeries(gd.getNextBoolean());
    options.setQuiet(gd.getNextBoolean()); // NB: invisible
    //options.setRecord(gd.getNextBoolean());
    options.setShowMetadata(gd.getNextBoolean());
    options.setShowOMEXML(gd.getNextBoolean());
    options.setShowROIs(gd.getNextBoolean());
    options.setROIsMode(options.getROIsModes()[gd.getNextChoiceIndex()]);
    options.setSpecifyRanges(gd.getNextBoolean());
    options.setSplitFocalPlanes(gd.getNextBoolean());
    options.setSplitTimepoints(gd.getNextBoolean());
    options.setSplitChannels(gd.getNextBoolean());
    options.setStackFormat(options.getStackFormats()[gd.getNextChoiceIndex()]);
    options.setStackOrder(options.getStackOrders()[gd.getNextChoiceIndex()]);
    options.setSwapDimensions(gd.getNextBoolean());
    options.setVirtual(gd.getNextBoolean());
    options.setStitchTiles(gd.getNextBoolean());
    return true;
  }

  // -- FocusListener methods --

  /** Handles information pane updates when component focus changes. */
  @Override
  public void focusGained(FocusEvent e) {
    Object src = e.getSource();
    String text = infoTable.get(src);
    infoPane.setText("<html>" + text);
    infoPane.setCaretPosition(0);
  }

  @Override
  public void focusLost(FocusEvent e) { }

  // -- ItemListener methods --

  /** Handles toggling of mutually exclusive options. */
  @Override
  public void itemStateChanged(ItemEvent e) {
    Object src = e.getSource();
    verifyOptions(src);
    if (src == stackFormatChoice) {
      if (ImporterOptions.VIEW_STANDARD.equals(e.getItem().toString())) {
        infoPane.setText("<html><font color=\"red\">Warning!</font>"
          + "<i>Standard ImageJ</i> is deprecated; "
          + "please use <i>Hyperstack</i> instead.</html>");
      } else {
        infoPane.setText("<html>" + infoTable.get(src));
      }
    }
  }

  // -- MouseListener methods --

  /** Focuses the component upon mouseover. */
  @Override
  public void mouseEntered(MouseEvent e) {
    Object src = e.getSource();
    if (src instanceof Component) {
      ((Component) src).requestFocusInWindow();
    }
  }

  @Override
  public void mouseClicked(MouseEvent e) { }
  @Override
  public void mouseExited(MouseEvent e) { }
  @Override
  public void mousePressed(MouseEvent e) { }
  @Override
  public void mouseReleased(MouseEvent e) { }

  // -- Helper methods --

  /** Fancies up the importer dialog to look much nicer. */
  private void rebuildDialog(GenericDialog gd) {
    // extract GUI components from dialog and add listeners
    List<Checkbox> boxes = null;
    List<Choice> choices = null;
    List<Label> labels = null;
    Label colorModeLabel = null;
    Label roisModeLabel = null;
    Label stackFormatLabel = null;
    Label stackOrderLabel = null;
    Component[] c = gd.getComponents();
    if (c != null) {
      boxes = new ArrayList<Checkbox>();
      choices = new ArrayList<Choice>();
      labels = new ArrayList<Label>();
      for (int i=0; i<c.length; i++) {
        if (c[i] instanceof Checkbox) {
          Checkbox item = (Checkbox) c[i];
          item.addFocusListener(this);
          item.addItemListener(this);
          item.addMouseListener(this);
          boxes.add(item);
        }
        else if (c[i] instanceof Choice) {
          Choice item = (Choice) c[i];
          item.addFocusListener(this);
          item.addItemListener(this);
          item.addMouseListener(this);
          choices.add(item);
        }
        else if (c[i] instanceof Label) labels.add((Label) c[i]);
      }
      int boxIndex = 0, choiceIndex = 0, labelIndex = 0;
      autoscaleBox      = boxes.get(boxIndex++);
      colorModeChoice   = choices.get(choiceIndex++);
      colorModeLabel    = labels.get(labelIndex++);
      concatenateBox    = boxes.get(boxIndex++);
      cropBox           = boxes.get(boxIndex++);
      groupFilesBox     = boxes.get(boxIndex++);
      ungroupFilesBox   = boxes.get(boxIndex++);
      openAllSeriesBox  = boxes.get(boxIndex++);
      boxIndex++; // quiet
      //recordBox         = boxes.get(boxIndex++);
      showMetadataBox   = boxes.get(boxIndex++);
      showOMEXMLBox     = boxes.get(boxIndex++);
      showROIsBox       = boxes.get(boxIndex++);
      roisModeChoice    = choices.get(choiceIndex++);
      roisModeLabel     = labels.get(labelIndex++);
      specifyRangesBox  = boxes.get(boxIndex++);
      splitZBox         = boxes.get(boxIndex++);
      splitTBox         = boxes.get(boxIndex++);
      splitCBox         = boxes.get(boxIndex++);
      stackFormatChoice = choices.get(choiceIndex++);
      stackFormatLabel  = labels.get(labelIndex++);
      stackOrderChoice  = choices.get(choiceIndex++);
      stackOrderLabel   = labels.get(labelIndex++);
      swapDimsBox       = boxes.get(boxIndex++);
      virtualBox        = boxes.get(boxIndex++);
      stitchTilesBox    = boxes.get(boxIndex++);
    }
    verifyOptions(null);

    // TODO: The info table and focus logic could be split into
    // its own class, rather than being specific to this dialog.

    // associate information for each option
    infoTable = new HashMap<Component, String>();
    infoTable.put(autoscaleBox, options.getAutoscaleInfo());
    infoTable.put(colorModeChoice, options.getColorModeInfo());
    infoTable.put(colorModeLabel, options.getColorModeInfo());
    infoTable.put(concatenateBox, options.getConcatenateInfo());
    infoTable.put(cropBox, options.getCropInfo());
    infoTable.put(groupFilesBox, options.getGroupFilesInfo());
    infoTable.put(ungroupFilesBox, options.getUngroupFilesInfo());
    infoTable.put(openAllSeriesBox, options.getOpenAllSeriesInfo());
    //infoTable.put(recordBox, options.getRecordInfo());
    infoTable.put(showMetadataBox, options.getShowMetadataInfo());
    infoTable.put(showOMEXMLBox, options.getShowOMEXMLInfo());
    infoTable.put(showROIsBox, options.getShowROIsInfo());
    infoTable.put(roisModeChoice, options.getROIsModeInfo());
    infoTable.put(roisModeLabel, options.getROIsModeInfo());
    infoTable.put(specifyRangesBox, options.getSpecifyRangesInfo());
    infoTable.put(splitZBox, options.getSplitFocalPlanesInfo());
    infoTable.put(splitTBox, options.getSplitTimepointsInfo());
    infoTable.put(splitCBox, options.getSplitChannelsInfo());
    infoTable.put(stackFormatChoice, options.getStackFormatInfo());
    infoTable.put(stackFormatLabel, options.getStackFormatInfo());
    infoTable.put(stackOrderChoice, options.getStackOrderInfo());
    infoTable.put(stackOrderLabel, options.getStackOrderInfo());
    infoTable.put(swapDimsBox, options.getSwapDimensionsInfo());
    infoTable.put(virtualBox, options.getVirtualInfo());
    infoTable.put(stitchTilesBox, options.getStitchTilesInfo());

    // rebuild dialog using FormLayout to organize things more nicely

    String cols =
      // first column
      "pref, 3dlu, pref:grow, " +
      // second column
      "10dlu, pref, 3dlu, pref:grow, " +
      // third column
      "10dlu, fill:150dlu";

    String rows =
      // Stack viewing        | Metadata viewing
      "pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, " +
      // Dataset organization | Memory management
      "9dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, " +
      "3dlu, pref, " +
      // Color options        | Split into separate windows
      "9dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref";

    // TODO: Change "Use virtual stack" and "Record modifications to virtual
    // stack" checkboxes to "Stack type" choice with options:
    //   "Normal", "Virtual" or "Smart virtual"

    PanelBuilder builder = new PanelBuilder(new FormLayout(cols, rows));
    CellConstraints cc = new CellConstraints();

    // populate 1st column
    int row = 1;
    builder.addSeparator("Stack viewing", cc.xyw(1, row, 3));
    row += 2;
    builder.add(stackFormatLabel, cc.xy(1, row));
    builder.add(stackFormatChoice, cc.xy(3, row));
    row += 2;
    builder.add(stackOrderLabel, cc.xy(1, row));
    builder.add(stackOrderChoice, cc.xy(3, row));
    row += 6;
    builder.addSeparator("Dataset organization", cc.xyw(1, row, 3));
    row += 2;
    builder.add(groupFilesBox, xyw(cc, 1, row, 3));
    row += 2;
    builder.add(ungroupFilesBox, xyw(cc, 1, row, 3));
    row += 2;
    builder.add(swapDimsBox, xyw(cc, 1, row, 3));
    row += 2;
    builder.add(openAllSeriesBox, xyw(cc, 1, row, 3));
    row += 2;
    builder.add(concatenateBox, xyw(cc, 1, row, 3));
    row += 2;
    builder.add(stitchTilesBox, xyw(cc, 1, row, 3));
    row += 2;
    builder.addSeparator("Color options", cc.xyw(1, row, 3));
    row += 2;
    builder.add(colorModeLabel, cc.xy(1, row));
    builder.add(colorModeChoice, cc.xy(3, row));
    row += 2;
    builder.add(autoscaleBox, xyw(cc, 1, row, 3));
    row += 2;

    // populate 2nd column
    row = 1;
    builder.addSeparator("Metadata viewing", cc.xyw(5, row, 3));
    row += 2;
    builder.add(showMetadataBox, xyw(cc, 5, row, 3));
    row += 2;
    builder.add(showOMEXMLBox, xyw(cc, 5, row, 3));
    row += 2;
    builder.add(showROIsBox, xyw(cc, 5, row, 3));
    row += 2;
    builder.add(roisModeLabel, cc.xy(5, row));
    builder.add(roisModeChoice, cc.xy(7, row));
    row += 2;
    builder.addSeparator("Memory management", cc.xyw(5, row, 3));
    row += 2;
    builder.add(virtualBox, xyw(cc, 5, row, 3));
    row += 2;
    //builder.add(recordBox, xyw(cc, 5, row, 3));
    //row += 2;
    builder.add(specifyRangesBox, xyw(cc, 5, row, 3));
    row += 2;
    builder.add(cropBox, xyw(cc, 5, row, 3));
    row += 4;
    builder.addSeparator("Split into separate windows", cc.xyw(5, row, 3));
    row += 2;
    builder.add(splitCBox, xyw(cc, 5, row, 3));
    row += 2;
    builder.add(splitZBox, xyw(cc, 5, row, 3));
    row += 2;
    builder.add(splitTBox, xyw(cc, 5, row, 3));
    //row += 4;

    // information section
    builder.addSeparator("Information", cc.xy(9, 1));
    //row += 2;
    infoPane = new JEditorPane();
    infoPane.setContentType("text/html");
    infoPane.setEditable(false);
    infoPane.setText("<html>" + INFO_DEFAULT);
    builder.add(new JScrollPane(infoPane), cc.xywh(9, 3, 1, row));
    //row += 2;

    gd.removeAll();
    gd.add(builder.getPanel());

    WindowTools.addScrollBars(gd);
    gd.setBackground(Color.white); // HACK: workaround for JPanel in a Dialog
  }

  /**
   * Convenience method for creating a left-aligned,
   * vertically centered cell constraints object.
   */
  private CellConstraints xyw(CellConstraints cc, int x, int y, int w) {
    return cc.xyw(x, y, w, CellConstraints.LEFT, CellConstraints.CENTER);
  }

  /** Ensures that the options dialog has no mutually exclusive options. */
  private void verifyOptions(Object src) {
    // record GUI state

    boolean autoscaleEnabled = autoscaleBox.isEnabled();
    boolean colorModeEnabled = colorModeChoice.isEnabled();
    boolean concatenateEnabled = concatenateBox.isEnabled();
    boolean cropEnabled = cropBox.isEnabled();
    boolean groupFilesEnabled = groupFilesBox.isEnabled();
    boolean ungroupFilesEnabled = ungroupFilesBox.isEnabled();
    boolean openAllSeriesEnabled = openAllSeriesBox.isEnabled();
    //boolean recordEnabled = recordBox.isEnabled();
    boolean showMetadataEnabled = showMetadataBox.isEnabled();
    boolean showOMEXMLEnabled = showOMEXMLBox.isEnabled();
    boolean roisModeEnabled = roisModeChoice.isEnabled();
    boolean specifyRangesEnabled = specifyRangesBox.isEnabled();
    boolean splitZEnabled = splitZBox.isEnabled();
    boolean splitTEnabled = splitTBox.isEnabled();
    boolean splitCEnabled = splitCBox.isEnabled();
    //boolean stackFormatEnabled = stackFormatChoice.isEnabled();
    boolean stackOrderEnabled = stackOrderChoice.isEnabled();
    boolean swapDimsEnabled = swapDimsBox.isEnabled();
    boolean virtualEnabled = virtualBox.isEnabled();

    boolean isAutoscale = autoscaleBox.getState();
    String colorModeValue = colorModeChoice.getSelectedItem();
    boolean isConcatenate = concatenateBox.getState();
    boolean isCrop = cropBox.getState();
    boolean isGroupFiles = groupFilesBox.getState();
    boolean isUngroupFiles = ungroupFilesBox.getState();
    boolean isOpenAllSeries = openAllSeriesBox.getState();
    //boolean isRecord = recordBox.getState();
    boolean isShowMetadata = showMetadataBox.getState();
    boolean isShowOMEXML = showOMEXMLBox.getState();
    boolean isShowROIs = showROIsBox.getState();
    String roisModeValue = roisModeChoice.getSelectedItem();
    boolean isSpecifyRanges = specifyRangesBox.getState();
    boolean isSplitZ = splitZBox.getState();
    boolean isSplitT = splitTBox.getState();
    boolean isSplitC = splitCBox.getState();
    String stackFormatValue = stackFormatChoice.getSelectedItem();
    boolean isStackNone = stackFormatValue.equals(ImporterOptions.VIEW_NONE);
    boolean isStackStandard =
      stackFormatValue.equals(ImporterOptions.VIEW_STANDARD);
    boolean isStackHyperstack =
      stackFormatValue.equals(ImporterOptions.VIEW_HYPERSTACK);
    boolean isStackBrowser =
      stackFormatValue.equals(ImporterOptions.VIEW_BROWSER);
    boolean isStackImage5D =
      stackFormatValue.equals(ImporterOptions.VIEW_IMAGE_5D);
    boolean isStackView5D =
      stackFormatValue.equals(ImporterOptions.VIEW_VIEW_5D);
    String stackOrderValue = stackOrderChoice.getSelectedItem();
    boolean isSwap = swapDimsBox.getState();
    boolean isVirtual = virtualBox.getState();

    // toggle availability of each option based on state of earlier options

    // NB: The order the options are examined here defines their order of
    // precedence. This ordering is necessary because it affects which
    // component states are capable of graying out other components.

    // For example, we want to disable autoscaleBox when virtualBox is checked,
    // so the virtualBox logic must appear before the autoscaleBox logic.

    // To make it more intuitive for the user, the order of precedence should
    // match the component layout from left to right, top to bottom, according
    // to subsection.

    // == Stack viewing ==

    // stackOrderChoice
    stackOrderEnabled = isStackStandard;
    if (src == stackFormatChoice) {
      if (isStackHyperstack || isStackBrowser || isStackImage5D) {
        stackOrderValue = ImporterOptions.ORDER_XYCZT;
      }
      else if (isStackView5D) stackOrderValue = ImporterOptions.ORDER_XYZCT;
      else stackOrderValue = ImporterOptions.ORDER_DEFAULT;
    }

    // == Metadata viewing ==

    // showMetadataBox
    showMetadataEnabled = !isStackNone;
    if (!showMetadataEnabled) isShowMetadata = true;

    // showOMEXMLBox
    // NB: no other options affect showOMEXMLBox

    // roisModeChoice
    roisModeEnabled = isShowROIs;
    if (!roisModeEnabled) roisModeValue = ImporterOptions.ROIS_MODE_MANAGER;
    
    // == Dataset organization ==

    // groupFilesBox
    if (src == stackFormatChoice && isStackBrowser) {
      isGroupFiles = true;
    }
    else if (!options.isLocal()) {
      isGroupFiles = false;
      groupFilesEnabled = false;
    }

    // ungroupFilesBox

    if (options.isOMERO()) {
      isUngroupFiles = false;
      ungroupFilesEnabled = false;
    }

    // swapDimsBox
    // NB: no other options affect swapDimsBox

    // openAllSeriesBox
    // NB: no other options affect openAllSeriesBox

    // concatenateBox
    // NB: no other options affect concatenateBox

    // == Memory management ==

    // virtualBox
    virtualEnabled =
      !isStackNone && !isStackImage5D && !isStackView5D && !isConcatenate;
    if (!virtualEnabled) isVirtual = false;
    else if (src == stackFormatChoice && isStackBrowser) isVirtual = true;

    // recordBox
    //recordEnabled = isVirtual;
    //if (!recordEnabled) isRecord = false;

    // specifyRangesBox
    specifyRangesEnabled = !isStackNone && !isVirtual;
    if (!specifyRangesEnabled) isSpecifyRanges = false;

    // cropBox
    cropEnabled = !isStackNone && !isVirtual;
    if (!cropEnabled) isCrop = false;

    // == Color options ==

    // colorModeChoice
    colorModeEnabled = !isStackImage5D && !isStackView5D && !isStackStandard;
    if (!colorModeEnabled) colorModeValue = ImporterOptions.COLOR_MODE_DEFAULT;

    // autoscaleBox
    autoscaleEnabled = !isVirtual;
    if (!autoscaleEnabled) isAutoscale = false;

    // == Split into separate windows ==

    boolean splitEnabled = !isStackNone && !isStackBrowser &&
      !isStackImage5D && !isStackView5D;
    // TODO: Make splitting work with Data Browser.

    // splitCBox
    splitCEnabled = splitEnabled;
    if (!splitCEnabled) isSplitC = false;

    // splitZBox
    splitZEnabled = splitEnabled;
    if (!splitZEnabled) isSplitZ = false;

    // splitTBox
    splitTEnabled = splitEnabled;
    if (!splitTEnabled) isSplitT = false;

    // update state of each option, in case anything changed

    autoscaleBox.setEnabled(autoscaleEnabled);
    colorModeChoice.setEnabled(colorModeEnabled);
    concatenateBox.setEnabled(concatenateEnabled);
    cropBox.setEnabled(cropEnabled);
    groupFilesBox.setEnabled(groupFilesEnabled);
    ungroupFilesBox.setEnabled(ungroupFilesEnabled);
    openAllSeriesBox.setEnabled(openAllSeriesEnabled);
    //recordBox.setEnabled(recordEnabled);
    showMetadataBox.setEnabled(showMetadataEnabled);
    showOMEXMLBox.setEnabled(showOMEXMLEnabled);
    roisModeChoice.setEnabled(roisModeEnabled);
    specifyRangesBox.setEnabled(specifyRangesEnabled);
    splitZBox.setEnabled(splitZEnabled);
    splitTBox.setEnabled(splitTEnabled);
    splitCBox.setEnabled(splitCEnabled);
    //stackFormatChoice.setEnabled(stackFormatEnabled);
    stackOrderChoice.setEnabled(stackOrderEnabled);
    swapDimsBox.setEnabled(swapDimsEnabled);
    virtualBox.setEnabled(virtualEnabled);

    autoscaleBox.setState(isAutoscale);
    colorModeChoice.select(colorModeValue);
    concatenateBox.setState(isConcatenate);
    cropBox.setState(isCrop);
    groupFilesBox.setState(isGroupFiles);
    ungroupFilesBox.setState(isUngroupFiles);
    openAllSeriesBox.setState(isOpenAllSeries);
    //recordBox.setState(isRecord);
    showMetadataBox.setState(isShowMetadata);
    showOMEXMLBox.setState(isShowOMEXML);
    roisModeChoice.select(roisModeValue);
    specifyRangesBox.setState(isSpecifyRanges);
    splitZBox.setState(isSplitZ);
    splitTBox.setState(isSplitT);
    splitCBox.setState(isSplitC);
    //stackFormatChoice.select(stackFormatValue);
    stackOrderChoice.select(stackOrderValue);
    swapDimsBox.setState(isSwap);
    virtualBox.setState(isVirtual);

    if (IS_GLITCHED) {
      // HACK - work around a Mac OS X bug where GUI components do not update

      // list of affected components
      Component[] c = {
        autoscaleBox,
        colorModeChoice,
        concatenateBox,
        cropBox,
        groupFilesBox,
        ungroupFilesBox,
        openAllSeriesBox,
        //recordBox,
        showMetadataBox,
        showOMEXMLBox,
        specifyRangesBox,
        splitZBox,
        splitTBox,
        splitCBox,
        stackFormatChoice,
        stackOrderChoice,
        swapDimsBox,
        virtualBox
      };

      // identify currently focused component
      Component focused = null;
      for (int i=0; i<c.length; i++) {
        if (c[i].isFocusOwner()) focused = c[i];
      }

      // temporarily disable focus events
      for (int i=0; i<c.length; i++) c[i].removeFocusListener(this);

      // cycle through focus on all components
      for (int i=0; i<c.length; i++) c[i].requestFocusInWindow();

      // clear the focus globally
      KeyboardFocusManager kfm =
        KeyboardFocusManager.getCurrentKeyboardFocusManager();
      kfm.clearGlobalFocusOwner();
      sleep(100); // doesn't work if this value is too small

      // refocus the originally focused component
      if (focused != null) focused.requestFocusInWindow();

      // reenable focus events
      for (int i=0; i<c.length; i++) c[i].addFocusListener(this);
    }
  }

}
