//
// ImporterDialog.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser, Stack Colorizer and Stack Slicer. Copyright (C) 2005-@year@
Melissa Linkert, Curtis Rueden and Christopher Peterson.

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

package loci.plugins.importer;

import ij.gui.GenericDialog;

import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Label;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import loci.plugins.prefs.OptionsDialog;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Bio-Formats Importer general options dialog box.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/importer/ImporterDialog.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/importer/ImporterDialog.java">SVN</a></dd></dl>
 */
public class ImporterDialog extends OptionsDialog
  implements FocusListener, ItemListener, MouseListener
{

  // -- Constants --

  /** Initial message to display in help text box. */
  public static final String INFO_DEFAULT =
    "<i>Select an option for a detailed explanation. " +
    "Documentation written by Glen MacDonald and Curtis Rueden.</i>";

  // -- Fields --

  /** LOCI plugins configuration. */
  protected ImporterOptions options;

  protected Checkbox autoscaleBox;
  protected Checkbox colorizeBox;
  protected Checkbox concatenateBox;
  protected Checkbox cropBox;
  protected Checkbox customColorizeBox;
  protected Checkbox groupFilesBox;
  protected Checkbox ungroupFilesBox;
  protected Checkbox mergeChannelsBox;
  protected Checkbox openAllSeriesBox;
  protected Checkbox recordBox;
  protected Checkbox showMetadataBox;
  protected Checkbox showOMEXMLBox;
  protected Checkbox showROIsBox;
  protected Checkbox specifyRangesBox;
  protected Checkbox splitZBox;
  protected Checkbox splitTBox;
  protected Checkbox splitCBox;
  protected Choice stackFormatChoice;
  protected Choice stackOrderChoice;
  protected Checkbox swapDimsBox;
  protected Checkbox virtualBox;

  protected HashMap<Component, String> infoTable;
  protected JEditorPane infoPane;

  // -- Constructor --

  /** Creates a general options dialog for the Bio-Formats Importer. */
  public ImporterDialog(ImporterOptions options) {
    super(options);
    this.options = options;
  }

  // -- OptionsDialog methods --

  /**
   * Gets importer settings from macro options, or user prompt if necessary.
   *
   * @return status of operation
   */
  public int showDialog() {
    // prompt user for parameters (or grab from macro options)
    GenericDialog gd = new GenericDialog("Bio-Formats Import Options");
    addCheckbox(gd, ImporterOptions.KEY_AUTOSCALE);
    addCheckbox(gd, ImporterOptions.KEY_COLORIZE);
    addCheckbox(gd, ImporterOptions.KEY_CONCATENATE);
    addCheckbox(gd, ImporterOptions.KEY_CROP);
    addCheckbox(gd, ImporterOptions.KEY_CUSTOM_COLORIZE);
    addCheckbox(gd, ImporterOptions.KEY_GROUP_FILES);
    addCheckbox(gd, ImporterOptions.KEY_UNGROUP_FILES);
    addCheckbox(gd, ImporterOptions.KEY_MERGE_CHANNELS);
    addCheckbox(gd, ImporterOptions.KEY_OPEN_ALL_SERIES);
    addCheckbox(gd, ImporterOptions.KEY_QUIET); // NB: invisible
    addCheckbox(gd, ImporterOptions.KEY_RECORD);
    addCheckbox(gd, ImporterOptions.KEY_SHOW_METADATA);
    addCheckbox(gd, ImporterOptions.KEY_SHOW_OME_XML);
    addCheckbox(gd, ImporterOptions.KEY_SHOW_ROIS);
    addCheckbox(gd, ImporterOptions.KEY_SPECIFY_RANGES);
    addCheckbox(gd, ImporterOptions.KEY_SPLIT_Z);
    addCheckbox(gd, ImporterOptions.KEY_SPLIT_T);
    addCheckbox(gd, ImporterOptions.KEY_SPLIT_C);
    addChoice(gd, ImporterOptions.KEY_STACK_FORMAT);
    addChoice(gd, ImporterOptions.KEY_STACK_ORDER);
    addCheckbox(gd, ImporterOptions.KEY_SWAP_DIMS);
    addCheckbox(gd, ImporterOptions.KEY_VIRTUAL);

    // extract GUI components from dialog and add listeners
    Vector<Checkbox> boxes = null;
    Vector<Choice> choices = null;
    Vector<Label> labels = null;
    Label stackFormatLabel = null, stackOrderLabel = null;
    Component[] c = gd.getComponents();
    if (c != null) {
      boxes = new Vector<Checkbox>();
      choices = new Vector<Choice>();
      labels = new Vector<Label>();
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
      autoscaleBox      = boxes.get(0);
      colorizeBox       = boxes.get(1);
      concatenateBox    = boxes.get(2);
      cropBox           = boxes.get(3);
      customColorizeBox = boxes.get(4);
      groupFilesBox     = boxes.get(5);
      ungroupFilesBox   = boxes.get(6);
      mergeChannelsBox  = boxes.get(7);
      openAllSeriesBox  = boxes.get(8);
      //quietBox        = boxes.get(9);
      recordBox         = boxes.get(10);
      showMetadataBox   = boxes.get(11);
      showOMEXMLBox     = boxes.get(12);
      showROIsBox       = boxes.get(13);
      specifyRangesBox  = boxes.get(14);
      splitZBox         = boxes.get(15);
      splitTBox         = boxes.get(16);
      splitCBox         = boxes.get(17);
      stackFormatChoice = choices.get(0);
      stackFormatLabel  = labels.get(0);
      stackOrderChoice  = choices.get(1);
      stackOrderLabel   = labels.get(1);
      swapDimsBox       = boxes.get(18);
      virtualBox        = boxes.get(19);
    }
    verifyOptions(null);

    // associate information for each option
    infoTable = new HashMap<Component, String>();
    infoTable.put(autoscaleBox, options.getAutoscaleInfo());
    infoTable.put(colorizeBox, options.getColorizeInfo());
    infoTable.put(concatenateBox, options.getConcatenateInfo());
    infoTable.put(cropBox, options.getCropInfo());
    infoTable.put(customColorizeBox, options.getCustomColorizeInfo());
    infoTable.put(groupFilesBox, options.getGroupFilesInfo());
    infoTable.put(ungroupFilesBox, options.getUngroupFilesInfo());
    infoTable.put(mergeChannelsBox, options.getMergeChannelsInfo());
    infoTable.put(openAllSeriesBox, options.getOpenAllSeriesInfo());
    infoTable.put(recordBox, options.getRecordInfo());
    infoTable.put(showMetadataBox, options.getShowMetadataInfo());
    infoTable.put(showOMEXMLBox, options.getShowOMEXMLInfo());
    infoTable.put(showROIsBox, options.getShowROIsInfo());
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

    // rebuild dialog using FormLayout to organize things more nicely

    String cols =
      // first column
      "pref, 3dlu, pref:grow, " +
      // second column
      "10dlu, pref";

    String rows =
      // Stack viewing        | Metadata viewing
      "pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, " +
      // Dataset organization | Memory management
      "9dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, " +
      "3dlu, pref, " +
      // Color options        | Split into separate windows
      "9dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, " +
      // Information
      "9dlu, pref, 3dlu, fill:100dlu";

    // TODO: change "Merge channels into RGB" checkbox to
    // "Channel merging" choice with options:
    //   "Default", "Merge channels" or "Separate channels"

    // TODO: change "Use virtual stack" and "Record modifications to virtual
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
    row += 4;
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
    builder.addSeparator("Color options", cc.xyw(1, row, 3));
    row += 2;
    builder.add(mergeChannelsBox, xyw(cc, 1, row, 3));
    row += 2;
    builder.add(colorizeBox, xyw(cc, 1, row, 3));
    row += 2;
    builder.add(customColorizeBox, xyw(cc, 1, row, 3));
    row += 2;
    builder.add(autoscaleBox, xyw(cc, 1, row, 3));
    row += 2;

    // populate 2nd column
    row = 1;
    builder.addSeparator("Metadata viewing", cc.xy(5, row));
    row += 2;
    builder.add(showMetadataBox, xyw(cc, 5, row, 1));
    row += 2;
    builder.add(showOMEXMLBox, xyw(cc, 5, row, 1));
    row += 2;
    builder.add(showROIsBox, xyw(cc, 5, row, 1));
    row += 2;
    builder.addSeparator("Memory management", cc.xy(5, row));
    row += 2;
    builder.add(virtualBox, xyw(cc, 5, row, 1));
    row += 2;
    builder.add(recordBox, xyw(cc, 5, row, 1));
    row += 2;
    builder.add(specifyRangesBox, xyw(cc, 5, row, 1));
    row += 2;
    builder.add(cropBox, xyw(cc, 5, row, 1));
    row += 4;
    builder.addSeparator("Split into separate windows", cc.xy(5, row));
    row += 2;
    builder.add(splitCBox, xyw(cc, 5, row, 1));
    row += 2;
    builder.add(splitZBox, xyw(cc, 5, row, 1));
    row += 2;
    builder.add(splitTBox, xyw(cc, 5, row, 1));
    row += 4;

    // information section
    builder.addSeparator("Information", cc.xyw(1, row, 5));
    row += 2;
    infoPane = new JEditorPane();
    infoPane.setContentType("text/html");
    infoPane.setEditable(false);
    infoPane.setText("<html>" + INFO_DEFAULT);
    builder.add(new JScrollPane(infoPane), cc.xyw(1, row, 5));
    row += 2;

    gd.removeAll();
    gd.add(builder.getPanel());

    // display dialog to user and harvest results
    gd.showDialog();
    if (gd.wasCanceled()) return STATUS_CANCELED;
    options.setAutoscale(gd.getNextBoolean());
    options.setColorize(gd.getNextBoolean());
    options.setConcatenate(gd.getNextBoolean());
    options.setCrop(gd.getNextBoolean());
    options.setCustomColorize(gd.getNextBoolean());
    options.setGroupFiles(gd.getNextBoolean());
    options.setUngroupFiles(gd.getNextBoolean());
    options.setMergeChannels(gd.getNextBoolean());
    options.setOpenAllSeries(gd.getNextBoolean());
    options.setQuiet(gd.getNextBoolean()); // NB: invisible
    options.setRecord(gd.getNextBoolean());
    options.setShowMetadata(gd.getNextBoolean());
    options.setShowOMEXML(gd.getNextBoolean());
    options.setShowROIs(gd.getNextBoolean());
    options.setSpecifyRanges(gd.getNextBoolean());
    options.setSplitFocalPlanes(gd.getNextBoolean());
    options.setSplitTimepoints(gd.getNextBoolean());
    options.setSplitChannels(gd.getNextBoolean());
    options.setStackFormat(options.getStackFormats()[gd.getNextChoiceIndex()]);
    options.setStackOrder(options.getStackOrders()[gd.getNextChoiceIndex()]);
    options.setSwapDimensions(gd.getNextBoolean());
    options.setVirtual(gd.getNextBoolean());

    return STATUS_OK;
  }

  // -- FocusListener methods --

  /** Handles information pane updates when component focus changes. */
  public void focusGained(FocusEvent e) {
    Object src = e.getSource();
    String text = (String) infoTable.get(src);
    infoPane.setText("<html>" + text);
    infoPane.setCaretPosition(0);
  }

  public void focusLost(FocusEvent e) { }

  // -- ItemListener methods --

  /** Handles toggling of mutually exclusive options. */
  public void itemStateChanged(ItemEvent e) {
    verifyOptions(e.getSource());
  }

  // -- MouseListener methods --

  /** Focuses the component upon mouseover. */
  public void mouseEntered(MouseEvent e) {
    Object src = e.getSource();
    if (src instanceof Component) {
      ((Component) src).requestFocusInWindow();
    }
  }

  public void mouseClicked(MouseEvent e) { }
  public void mouseExited(MouseEvent e) { }
  public void mousePressed(MouseEvent e) { }
  public void mouseReleased(MouseEvent e) { }

  // -- Helper methods --

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
    boolean colorizeEnabled = colorizeBox.isEnabled();
    boolean concatenateEnabled = concatenateBox.isEnabled();
    boolean cropEnabled = cropBox.isEnabled();
    boolean customColorizeEnabled = customColorizeBox.isEnabled();
    boolean groupFilesEnabled = groupFilesBox.isEnabled();
    boolean ungroupFilesEnabled = ungroupFilesBox.isEnabled();
    boolean mergeChannelsEnabled = mergeChannelsBox.isEnabled();
    boolean openAllSeriesEnabled = openAllSeriesBox.isEnabled();
    boolean recordEnabled = recordBox.isEnabled();
    boolean showMetadataEnabled = showMetadataBox.isEnabled();
    boolean showOMEXMLEnabled = showOMEXMLBox.isEnabled();
    boolean specifyRangesEnabled = specifyRangesBox.isEnabled();
    boolean splitZEnabled = splitZBox.isEnabled();
    boolean splitTEnabled = splitTBox.isEnabled();
    boolean splitCEnabled = splitCBox.isEnabled();
    //boolean stackFormatEnabled = stackFormatChoice.isEnabled();
    boolean stackOrderEnabled = stackOrderChoice.isEnabled();
    boolean swapDimsEnabled = swapDimsBox.isEnabled();
    boolean virtualEnabled = virtualBox.isEnabled();

    boolean isAutoscale = autoscaleBox.getState();
    boolean isColorize = colorizeBox.getState();
    boolean isConcatenate = concatenateBox.getState();
    boolean isCrop = cropBox.getState();
    boolean isCustomColorize = customColorizeBox.getState();
    boolean isGroupFiles = groupFilesBox.getState();
    boolean isUngroupFiles = ungroupFilesBox.getState();
    boolean isMergeChannels = mergeChannelsBox.getState();
    boolean isOpenAllSeries = openAllSeriesBox.getState();
    boolean isRecord = recordBox.getState();
    boolean isShowMetadata = showMetadataBox.getState();
    boolean isShowOMEXML = showOMEXMLBox.getState();
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
    boolean isStackVisBio =
      stackFormatValue.equals(ImporterOptions.VIEW_VISBIO);
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
    stackOrderEnabled = isStackStandard || isStackVisBio;
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

    // == Dataset organization ==

    // groupFilesBox
    groupFilesEnabled = !options.isOME() && !options.isOMERO();
    if (!groupFilesEnabled) isGroupFiles = false;
    else if (src == stackFormatChoice && isStackBrowser) isGroupFiles = true;

    // ungroupFilesBox
    // NB: no other options affect ungroupFilesBox

    // swapDimsBox
    // NB: no other options affect swapDimsBox

    // openAllSeriesBox
    // NB: no other options affect openAllSeriesBox

    // concatenateBox
    // NB: no other options affect concatenateBox

    // == Memory management ==

    // virtualBox
    virtualEnabled = !isStackNone && !isStackImage5D && !isStackView5D;
    if (!virtualEnabled) isVirtual = false;
    else if (src == stackFormatChoice && isStackBrowser) isVirtual = true;

    // recordBox
    recordEnabled = isVirtual;
    if (!recordEnabled) isRecord = false;

    // specifyRangesBox
    specifyRangesEnabled = !isStackNone && !isVirtual;
    if (!specifyRangesEnabled) isSpecifyRanges = false;

    // cropBox
    cropEnabled = !isStackNone && !isVirtual;
    if (!cropEnabled) isCrop = false;

    // == Color options ==

    // mergeChannelsBox
    mergeChannelsEnabled = !isStackImage5D;
    if (!mergeChannelsEnabled) isMergeChannels = false;

    // colorizeBox
    colorizeEnabled = !isMergeChannels && !isStackBrowser &&
      !isStackImage5D && !isStackView5D && !isCustomColorize;
    if (!colorizeEnabled) isColorize = false;

    // customColorizeBox
    customColorizeEnabled = !isMergeChannels && !isStackBrowser &&
      !isStackImage5D && !isStackView5D && !isColorize;
    if (!customColorizeEnabled) isCustomColorize = false;

    // autoscaleBox
    autoscaleEnabled = !isVirtual;
    if (!autoscaleEnabled) isAutoscale = false;

    // == Split into separate windows ==

    boolean splitEnabled = !isStackNone && !isStackBrowser &&
      !isStackVisBio && !isStackImage5D && !isStackView5D && !isVirtual;
    // TODO: make splitting work with Data Browser & virtual stacks

    // splitCBox
    splitCEnabled = splitEnabled && !isMergeChannels;
    if (!splitCEnabled) isSplitC = false;

    // splitZBox
    splitZEnabled = splitEnabled;
    if (!splitZEnabled) isSplitZ = false;

    // splitTBox
    splitTEnabled = splitEnabled;
    if (!splitTEnabled) isSplitT = false;

    // update state of each option, in case anything changed

    autoscaleBox.setEnabled(autoscaleEnabled);
    colorizeBox.setEnabled(colorizeEnabled);
    concatenateBox.setEnabled(concatenateEnabled);
    cropBox.setEnabled(cropEnabled);
    customColorizeBox.setEnabled(customColorizeEnabled);
    groupFilesBox.setEnabled(groupFilesEnabled);
    ungroupFilesBox.setEnabled(ungroupFilesEnabled);
    mergeChannelsBox.setEnabled(mergeChannelsEnabled);
    openAllSeriesBox.setEnabled(openAllSeriesEnabled);
    recordBox.setEnabled(recordEnabled);
    showMetadataBox.setEnabled(showMetadataEnabled);
    showOMEXMLBox.setEnabled(showOMEXMLEnabled);
    specifyRangesBox.setEnabled(specifyRangesEnabled);
    splitZBox.setEnabled(splitZEnabled);
    splitTBox.setEnabled(splitTEnabled);
    splitCBox.setEnabled(splitCEnabled);
    //stackFormatChoice.setEnabled(stackFormatEnabled);
    stackOrderChoice.setEnabled(stackOrderEnabled);
    swapDimsBox.setEnabled(swapDimsEnabled);
    virtualBox.setEnabled(virtualEnabled);

    autoscaleBox.setState(isAutoscale);
    colorizeBox.setState(isColorize);
    concatenateBox.setState(isConcatenate);
    cropBox.setState(isCrop);
    customColorizeBox.setState(isCustomColorize);
    groupFilesBox.setState(isGroupFiles);
    ungroupFilesBox.setState(isUngroupFiles);
    mergeChannelsBox.setState(isMergeChannels);
    openAllSeriesBox.setState(isOpenAllSeries);
    recordBox.setState(isRecord);
    showMetadataBox.setState(isShowMetadata);
    showOMEXMLBox.setState(isShowOMEXML);
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
        colorizeBox,
        concatenateBox,
        cropBox,
        customColorizeBox,
        groupFilesBox,
        ungroupFilesBox,
        mergeChannelsBox,
        openAllSeriesBox,
        recordBox,
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
