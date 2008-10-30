//
// ImporterOptions.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser, Stack Colorizer and Stack Slicer. Copyright (C) 2005-@year@
Melissa Linkert, Curtis Rueden and Christopher Peterson.

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

package loci.plugins;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import ij.*;
import ij.gui.GenericDialog;
import ij.io.OpenDialog;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.*;
import loci.common.*;
import loci.formats.*;

/**
 * Helper class for managing Bio-Formats Importer options.
 * Gets parameter values through a variety of means, including
 * preferences from IJ_Prefs.txt, plugin argument string, macro options,
 * and user input from dialog boxes.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/ImporterOptions.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/ImporterOptions.java">SVN</a></dd></dl>
 */
public class ImporterOptions
  implements FocusListener, ItemListener, MouseListener
{

  // -- Constants --

  // enumeration for status
  public static final int STATUS_OK = 0;
  public static final int STATUS_CANCELED = 1;
  public static final int STATUS_FINISHED = 2;

  // enumeration for stackFormat
  public static final String VIEW_NONE = "Metadata only";
  public static final String VIEW_STANDARD = "Standard ImageJ";
  public static final String VIEW_HYPERSTACK = "Hyperstack";
  public static final String VIEW_BROWSER = "Data Browser";
  public static final String VIEW_VISBIO = "VisBio";
  public static final String VIEW_IMAGE_5D = "Image5D";
  public static final String VIEW_VIEW_5D = "View5D";

  // enumeration for stackOrder
  public static final String ORDER_DEFAULT = "Default";
  public static final String ORDER_XYZCT = "XYZCT";
  public static final String ORDER_XYZTC = "XYZTC";
  public static final String ORDER_XYCZT = "XYCZT";
  public static final String ORDER_XYTCZ = "XYTCZ";
  public static final String ORDER_XYCTZ = "XYCTZ";
  public static final String ORDER_XYTZC = "XYTZC";

  // merging options
  public static final String MERGE_DEFAULT = "Do not merge";
  public static final String MERGE_PROJECTION = "Spectral projection";

  // class to check for each viewing option
  private static final String CLASS_VISBIO = "loci.visbio.VisBio";
  private static final String CLASS_IMAGE_5D = "i5d.Image5D";
  private static final String CLASS_VIEW_5D = "View5D_";

  // enumeration for location
  public static final String LOCATION_LOCAL = "Local machine";
  public static final String LOCATION_HTTP = "Internet";
  public static final String LOCATION_OME = "OME server";
  public static final String LOCATION_OMERO = "OMERO server";
  public static final String[] LOCATIONS = {
    LOCATION_LOCAL, LOCATION_HTTP, LOCATION_OME, LOCATION_OMERO
  };

  // keys for use in IJ_Prefs.txt
  public static final String PREF_STACK = "bioformats.stackFormat";
  public static final String PREF_ORDER = "bioformats.stackOrder";
  public static final String PREF_MERGE = "bioformats.mergeChannels";
  public static final String PREF_COLORIZE = "bioformats.colorize";
  public static final String PREF_SPLIT_C = "bioformats.splitWindows";
  public static final String PREF_SPLIT_Z = "bioformats.splitFocalPlanes";
  public static final String PREF_SPLIT_T = "bioformats.splitTimepoints";
  public static final String PREF_CROP = "bioformats.crop";
  public static final String PREF_METADATA = "bioformats.showMetadata";
  public static final String PREF_OME_XML = "bioformats.showOMEXML";
  public static final String PREF_GROUP = "bioformats.groupFiles";
  public static final String PREF_CONCATENATE = "bioformats.concatenate";
  public static final String PREF_RANGE = "bioformats.specifyRanges";
  public static final String PREF_AUTOSCALE = "bioformats.autoscale";
  public static final String PREF_VIRTUAL = "bioformats.virtual";
  public static final String PREF_RECORD = "bioformats.record";
  public static final String PREF_ALL_SERIES = "bioformats.openAllSeries";

  public static final String PREF_MERGE_OPTION = "bioformats.mergeOption";
  public static final String PREF_WINDOWLESS = "bioformats.windowless";
  public static final String PREF_SERIES = "bioformats.series";

  public static final String PREF_FIRST = "bioformats.firstTime";
  public static final String PREF_THUMBNAIL = "bioformats.forceThumbnails";
  public static final String PREF_SWAP = "bioformats.swapDimensions";

  // labels for user dialog; when trimmed these double as argument & macro keys
  public static final String LABEL_STACK = "View stack with: ";
  public static final String LABEL_ORDER = "Stack_order: ";
  public static final String LABEL_MERGE = "Merge_channels to RGB";
  public static final String LABEL_COLORIZE = "Colorize channels";
  public static final String LABEL_SPLIT_C = "Split_channels";
  public static final String LABEL_SPLIT_Z = "Split_focal planes";
  public static final String LABEL_SPLIT_T = "Split_timepoints";
  public static final String LABEL_CROP = "Crop on import";
  public static final String LABEL_METADATA =
    "Display_metadata in results window";
  public static final String LABEL_OME_XML = "Display_OME-XML metadata";
  public static final String LABEL_GROUP = "Group_files with similar names";
  public static final String LABEL_CONCATENATE =
    "Concatenate_series when compatible";
  public static final String LABEL_RANGE = "Specify_range for each series";
  public static final String LABEL_AUTOSCALE = "Autoscale";
  public static final String LABEL_VIRTUAL = "Use_virtual_stack";
  public static final String LABEL_RECORD =
    "Record_modifications_to_virtual_stack";
  public static final String LABEL_ALL_SERIES = "Open_all_series";
  public static final String LABEL_SWAP = "Swap_dimensions";

  public static final String LABEL_MERGE_OPTION = "Merging Options";
  public static final String LABEL_WINDOWLESS = "windowless";
  public static final String LABEL_SERIES = "series";

  public static final String LABEL_LOCATION = "Location: ";
  public static final String LABEL_ID = "Open";

  // informative description of each option
  public static final String INFO_STACK = info(LABEL_STACK) +
    " - The type of image viewer to use when displaying the dataset." +
    "<br><br>Possible choices are:<ul>" +
    "<li><b>" + VIEW_NONE + "</b> - Display no pixels, only metadata.</li>" +
    "<li><b>" + VIEW_STANDARD + "</b> - Display the pixels in a standard " +
    "ImageJ window without multidimensional support.</li>" +
    "<li><b>" + VIEW_HYPERSTACK + "</b> - Display the pixels in ImageJ's " +
    "built-in 5D viewer. If you do not have this option, upgrade to a more " +
    "recent version of ImageJ.</li>" +
    "<li><b>" + VIEW_BROWSER + "</b> - Display the pixels in LOCI's " +
    "multidimensional Data Browser viewer. The Data Browser has some " +
    "additional features on top of the normal ImageJ hyperstack. If you do " +
    "not have this option, upgrade to a more recent version of ImageJ.</li>" +
    //"<li><b>" + VIEW_VISBIO + "</b> - Not yet implemented.</li>"
    "<li><b>" + VIEW_IMAGE_5D + "</b> - Display the pixels in " +
    "Joachim Walter's Image5D viewer. Requires the Image5D plugin.</li>" +
    "<li><b>" + VIEW_VIEW_5D + "</b> - Display the pixels in " +
    "Rainer Heintzmann's View5D viewer. Requires the View5D plugin.</li>" +
    "</ul>";
  public static final String INFO_ORDER = info(LABEL_ORDER) +
    " - Controls the rasterization order of the dataset's dimensional axes." +
    "<br><br>Unless you care about the order in which the image planes " +
    "appear, you probably don't need to worry too much about this option." +
    "<br><br>By default, Bio-Formats reads the image planes in whatever " +
    "order they are stored, which is format-dependent. However, several " +
    "stack view modes require a specific rasterization order:<ul>" +
    "<li>Hyperstacks must be in " + ORDER_XYCZT + " order.</li>" +
    "<li>Image5D must be in " + ORDER_XYCZT + " order.</li>" +
    "<li>View5D must be in " + ORDER_XYCZT + " order.</li>" +
    "</ul><b>Example:</b> For a dataset in " + ORDER_XYCZT + " order with " +
    "2 channels, 3 focal planes and 5 time points, the order would be:<ol>" +
    "<li>C1-Z1-T1</li>" +
    "<li>C2-Z1-T1</li>" +
    "<li>C1-Z2-T1</li>" +
    "<li>C2-Z2-T1</li>" +
    "<li>C1-Z3-T1</li>" +
    "<li>C2-Z3-T1</li>" +
    "<li>C1-Z1-T2</li>" +
    "<li>C2-Z1-T2</li>" +
    "<li>etc.</li>" +
    "</ol>";
  public static final String INFO_MERGE = info(LABEL_MERGE) +
    " - A dataset with multiple channels will be opened and merged with " +
    "channels pseudocolored in the order of the RGB color scheme; i.e., " +
    "channel 1 is red, channel 2 is green, and channel 3 is blue." +
    "<br><br>The bit depth will be preserved. If the dataset has more than " +
    "3 channels, Bio-Formats will ask how to combine them." +
    "<br><br>For example, a 12-channel image could be combined into:<ul>" +
    "<li>6 planes with 2 channels each (1st channel is red, 2nd is green)" +
    "</li>" +
    "<li>4 planes with 3 channels each (3rd channel is blue)</li>" +
    "<li>3 planes with 4 channels each (4th channel is gray)</li>" +
    "<li>3 planes with 5 channels each (5th channel is cyan)</li>" +
    "<li>2 planes with 6 channels each (6th channel is magenta)</li>" +
    "<li>2 planes with 7 channels each (7th channel is yellow)</li>" +
    "</ul>";
  public static final String INFO_COLORIZE = info(LABEL_COLORIZE) +
    " - Each channel is assigned an appropriate pseudocolor table rather " +
    "than the normal grayscale." +
    "<br><br>The first channel is colorized red, the second channel is " +
    "green, and the third channel is blue. This option is not available " +
    "when " + info(LABEL_MERGE) + " is set.";
  public static final String INFO_SPLIT_C = info(LABEL_SPLIT_C) +
    " - Each channel is opened as a separate stack." +
    "<br><br>This option is especially useful if you want to merge the " +
    "channels into a specific order, rather than automatically assign " +
    "channels to the order of RGB. The bit depth is preserved.";
  public static final String INFO_SPLIT_Z = info(LABEL_SPLIT_Z) +
    " - Each focal plane is opened as a separate stack.";
  public static final String INFO_SPLIT_T = info(LABEL_SPLIT_T) +
    " - Timelapse data will be opened as a separate stack for each timepoint.";
  public static final String INFO_CROP = info(LABEL_CROP) +
    " - Image planes may be cropped during import to conserve memory." +
    "<br><br>A window is opened with display of the pixel width and height " +
    "of the image plane. Enter the X and Y coordinates for the upper left " +
    "corner of the crop region and the width and height of the selection to " +
    "be displayed, in pixels.";
  public static final String INFO_METADATA = info(LABEL_METADATA) +
    " - Reads metadata that may be contained within the file format and " +
    "displays it. You can save it as a text file or copy it from the File " +
    "and Edit menus specific to the ImageJ Results window. Readability " +
    "depends upon the manner in which metadata is formatted in the data " +
    "source.";
  public static final String INFO_OME_XML = info(LABEL_OME_XML) +
    " - Displays a tree of metadata standardized into the OME data model. " +
    "This structure is the same regardless of file format, though some " +
    "formats will populate more information than others." +
    "<br><br><b>Examples:</b><ul>" +
    "<li>The title of the dataset is listed under " +
    "OME &gt; Image &gt; Name.</li>" +
    "<li>The time and date when the dataset was acquired is listed under " +
    "OME &gt; Image &gt; CreationDate.</li>" +
    "<li>The physical pixel sizes of each plane in microns is listed under " +
    "OME &gt; Image &gt; Pixels &gt; " +
    "PhysicalSizeX, PhysicalSizeY, PhysicalSizeZ.</li>" +
    "</ul>";
  public static final String INFO_GROUP = info(LABEL_GROUP) +
    " - Parses filenames in the selected folder to open files with similar " +
    "names as planes in the same dataset." +
    "<br><br>The base filename and path is presented before opening for " +
    "editing." +
    "<br><br><b>Example:</b> Suppose you have a collection of 12 TIFF files " +
    "numbered data1.tif, data2.tif, ..., data12.tif, with each file " +
    "representing one timepoint, and containing the 9 focal planes at that " +
    "timepoint. If you leave this option unchecked and attempt to import " +
    "data1.tif, Bio-Formats will create an image stack with 9 planes. " +
    "But if you enable this option, Bio-Formats will automatically detect " +
    "the other similarly named files and present a confirmation dialog with " +
    "the detected file pattern, which in this example would be " +
    "<code>data&lt;1-12&gt;.tif</code>. You can then edit the pattern in " +
    "case it is incorrect. Bio-Formats will then import all 12 x 9 = 108 " +
    "planes of the dataset.";
  public static final String INFO_CONCATENATE = info(LABEL_CONCATENATE) +
    " - Allows multiple image series to be joined end to end." +
    "<br><br><b>Example:</b> You want to join two sequential timelapse " +
    "series.";
  public static final String INFO_RANGE = info(LABEL_RANGE) +
    " - Opens only the specified range of image planes from a dataset." +
    "<br><br>After analyzing the dataset dimensional parameters, " +
    "Bio-Formats will present an additional dialog box prompting for the " +
    "desired range." +
    "<br><br><b>Example:</b> You only want to open the range of focal " +
    "planes in a z-series that actually contain structures of interest to " +
    "conserve memory.";
  public static final String INFO_AUTOSCALE = info(LABEL_AUTOSCALE) +
    " - Stretches the histogram of the image planes to fit the data range. " +
    "Does not alter underlying values in the image. If selected, histogram " +
    "is stretched for each stack based upon the global minimum and maximum " +
    "value throughout the stack. " +
    "<br><br>Note that you can use the Brightness &amp; Contrast or " +
    "Window/Level controls to adjust the contrast range regardless of " +
    "whether this option is used.";
  public static final String INFO_VIRTUAL = info(LABEL_VIRTUAL) +
    " - Only reads one image plane into memory at a time, loading from the " +
    "data source on the fly as the active image plane changes." +
    "<br><br>This option is essential for datasets too large to fit into " +
    "memory.";
  public static final String INFO_RECORD = info(LABEL_RECORD) +
    " - <i>BETA FEATURE</i> - Record and reapply changes to virtual stack " +
    "planes." +
    "<br><br>When viewing as a virtual stack with this option enabled, " +
    "Bio-Formats will attempt to record the operations you perform. When " +
    "you switch to a new image plane, Bio-Formats will \"play back\" those " +
    "same operations, so that the image plane undergoes the same processing " +
    "you performed previously. In this way, the image stack should behave " +
    "more like a normal, fully memory-resident image stack.";
  public static final String INFO_ALL_SERIES = info(LABEL_ALL_SERIES) +
    " - Opens every available image series without prompting." +
    "<br><br>Some datasets contain multiple distinct image series. Normally " +
    "when Bio-Formats detects such data it presents a dialog box with " +
    "thumbnails allowing individual selection of each available series. " +
    "Checking this box instructs Bio-Formats to bypass this dialog box and " +
    "instead open every available image series. Essentially, it is a " +
    "shortcut for checking all the boxes in the series selector dialog box. " +
    "It is also useful in a macro when the number of available image series " +
    "is unknown.";
  public static final String INFO_SWAP = info(LABEL_SWAP) + " - " +
    " - Allows reassignment of dimensional axes (e.g., channel, Z and time)." +
    "<br><br>Bio-Formats is supposed to be smart about handling " +
    "multidimensional image data, but in some cases gets things wrong. " +
    "For example, when stitching together a dataset from multiple files " +
    "using the " + info(LABEL_GROUP) + " option, Bio-Formats may not know " +
    "which dimensional axis the file numbering is supposed to represent. " +
    "It will take a guess, but in case it guesses wrong, you can use " +
    info(LABEL_SWAP) + " to reassign which dimensions are which.";

  public static final String INFO_DEFAULT =
    "<i>Select an option for a detailed explanation. " +
    "Documentation written by Glen MacDonald and Curtis Rueden.</i>";

  /** Flag indicating whether to invoke workaround for AWT refresh bug. */
  private static final boolean IS_GLITCHED =
    System.getProperty("os.name").indexOf("Mac OS X") >= 0;

  // -- Fields - GUI components --

  private Choice stackChoice;
  private Choice orderChoice;
  private Checkbox mergeBox;
  private Checkbox colorizeBox;
  private Checkbox splitCBox;
  private Checkbox splitZBox;
  private Checkbox splitTBox;
  private Checkbox metadataBox;
  private Checkbox omexmlBox;
  private Checkbox groupBox;
  private Checkbox concatenateBox;
  private Checkbox rangeBox;
  private Checkbox autoscaleBox;
  private Checkbox virtualBox;
  private Checkbox recordBox;
  private Checkbox allSeriesBox;
  private Checkbox cropBox;
  private Checkbox swapBox;

  private Hashtable infoTable;
  private JEditorPane infoPane;

  private Choice mergeChoice;

  // -- Fields - core options --

  private boolean firstTime;
  private String stackFormat;
  private String stackOrder;
  private boolean mergeChannels;
  private boolean colorize;
  private boolean splitChannels;
  private boolean splitFocalPlanes;
  private boolean splitTimepoints;
  private boolean crop;
  private boolean showMetadata;
  private boolean showOMEXML;
  private boolean groupFiles;
  private boolean concatenate;
  private boolean specifyRanges;
  private boolean autoscale;
  private boolean virtual;
  private boolean record;
  private boolean openAllSeries;
  private boolean swapDimensions;

  private String mergeOption;
  private boolean windowless;
  private String seriesString;

  private boolean forceThumbnails;

  private String location;
  private String id;
  private boolean quiet;

  private Location idLoc;
  private String idName;
  private String idType;

  // -- ImporterOptions methods - accessors --

  public boolean isFirstTime() { return firstTime; }

  public String getStackFormat() { return stackFormat; }
  public String getStackOrder() { return stackOrder; }
  public boolean isMergeChannels() { return mergeChannels; }
  public boolean isColorize() { return colorize; }
  public boolean isSplitChannels() { return splitChannels; }
  public boolean isSplitFocalPlanes() { return splitFocalPlanes; }
  public boolean isSplitTimepoints() { return splitTimepoints; }
  public boolean isShowMetadata() { return showMetadata; }
  public boolean isShowOMEXML() { return showOMEXML; }
  public boolean isGroupFiles() { return groupFiles; }
  public boolean isConcatenate() { return concatenate; }
  public boolean isSpecifyRanges() { return specifyRanges; }
  public boolean isForceThumbnails() { return forceThumbnails; }
  public boolean isAutoscale() { return autoscale; }
  public boolean isWindowless() { return windowless; }
  public boolean isVirtual() { return virtual; }
  public boolean isRecord() { return record; }
  public boolean openAllSeries() { return openAllSeries; }
  public boolean doCrop() { return crop; }
  public boolean isSwapDimensions() { return swapDimensions; }

  public String getMergeOption() { return mergeOption; }

  public boolean isViewNone() { return VIEW_NONE.equals(stackFormat); }
  public boolean isViewStandard() { return VIEW_STANDARD.equals(stackFormat); }
  public boolean isViewHyperstack() {
    return VIEW_HYPERSTACK.equals(stackFormat);
  }
  public boolean isViewBrowser() { return VIEW_BROWSER.equals(stackFormat); }
  public boolean isViewVisBio() { return VIEW_VISBIO.equals(stackFormat); }
  public boolean isViewImage5D() { return VIEW_IMAGE_5D.equals(stackFormat); }
  public boolean isViewView5D() { return VIEW_VIEW_5D.equals(stackFormat); }

  public String getLocation() { return location; }
  public String getId() { return id; }
  public boolean isQuiet() { return quiet; }

  public boolean isLocal() { return LOCATION_LOCAL.equals(location); }
  public boolean isHTTP() { return LOCATION_HTTP.equals(location); }
  public boolean isOME() { return LOCATION_OME.equals(location); }
  public boolean isOMERO() { return LOCATION_OMERO.equals(location); }

  public Location getIdLocation() { return idLoc; }
  public String getIdName() { return idName; }
  public String getIdType() { return idType; }

  // -- ImporterOptions methods - mutators --

  public void setStackFormat(String s) { stackFormat = s; }
  public void setStackOrder(String s) { stackOrder = s; }
  public void setMergeChannels(boolean b) { mergeChannels = b; }
  public void setColorize(boolean b) { colorize = b; }
  public void setSplitChannels(boolean b) { splitChannels = b; }
  public void setSplitFocalPlanes(boolean b) { splitFocalPlanes = b; }
  public void setSplitTimepoints(boolean b) { splitTimepoints = b; }
  public void setShowMetadata(boolean b) { showMetadata = b; }
  public void setShowOMEXML(boolean b) { showOMEXML = b; }
  public void setGroupFiles(boolean b) { groupFiles = b; }
  public void setConcatenate(boolean b) { concatenate = b; }
  public void setSpecifyRanges(boolean b) { specifyRanges = b; }
  public void setForceThumbnails(boolean b) { forceThumbnails = b; }
  public void setAutoscale(boolean b) { autoscale = b; }
  public void setWindowless(boolean b) { windowless = b; }
  public void setVirtual(boolean b) { virtual = b; }
  public void setRecord(boolean b) { record = b; }
  public void setOpenAllSeries(boolean b) { openAllSeries = b; }
  public void setCrop(boolean b) { crop = b; }
  public void setSwapDimensions(boolean b) { swapDimensions = b; }

  // -- ImporterOptions methods --

  /** Loads default option values from IJ_Prefs.txt. */
  public void loadPreferences() {
    stackFormat = Prefs.get(PREF_STACK, VIEW_STANDARD);
    stackOrder = Prefs.get(PREF_ORDER, ORDER_DEFAULT);
    mergeChannels = Prefs.get(PREF_MERGE, false);
    colorize = Prefs.get(PREF_COLORIZE, true);
    splitChannels = Prefs.get(PREF_SPLIT_C, true);
    splitFocalPlanes = Prefs.get(PREF_SPLIT_Z, false);
    splitTimepoints = Prefs.get(PREF_SPLIT_T, false);
    crop = Prefs.get(PREF_CROP, false);
    showMetadata = Prefs.get(PREF_METADATA, false);
    showOMEXML = Prefs.get(PREF_OME_XML, false);
    groupFiles = Prefs.get(PREF_GROUP, false);
    concatenate = Prefs.get(PREF_CONCATENATE, false);
    specifyRanges = Prefs.get(PREF_RANGE, false);
    autoscale = Prefs.get(PREF_AUTOSCALE, true);
    virtual = Prefs.get(PREF_VIRTUAL, false);
    record = Prefs.get(PREF_RECORD, true);
    openAllSeries = Prefs.get(PREF_ALL_SERIES, false);
    swapDimensions = Prefs.get(PREF_SWAP, false);

    mergeOption = Prefs.get(PREF_MERGE_OPTION, MERGE_DEFAULT);
    windowless = Prefs.get(PREF_WINDOWLESS, false);
    seriesString = Prefs.get(PREF_SERIES, "0");

    firstTime = Prefs.get(PREF_FIRST, true);
    forceThumbnails = Prefs.get(PREF_THUMBNAIL, false);
  }

  /** Saves option values to IJ_Prefs.txt as the new defaults. */
  public void savePreferences() {
    Prefs.set(PREF_STACK, stackFormat);
    Prefs.set(PREF_ORDER, stackOrder);
    Prefs.set(PREF_MERGE, mergeChannels);
    Prefs.set(PREF_COLORIZE, colorize);
    Prefs.set(PREF_SPLIT_C, splitChannels);
    Prefs.set(PREF_SPLIT_Z, splitFocalPlanes);
    Prefs.set(PREF_SPLIT_T, splitTimepoints);
    Prefs.set(PREF_CROP, crop);
    Prefs.set(PREF_METADATA, showMetadata);
    Prefs.set(PREF_OME_XML, showOMEXML);
    Prefs.set(PREF_GROUP, groupFiles);
    Prefs.set(PREF_CONCATENATE, concatenate);
    Prefs.set(PREF_RANGE, specifyRanges);
    Prefs.set(PREF_AUTOSCALE, autoscale);
    Prefs.set(PREF_VIRTUAL, virtual);
    Prefs.set(PREF_RECORD, record);
    Prefs.set(PREF_ALL_SERIES, openAllSeries);
    Prefs.set(PREF_SWAP, swapDimensions);

    Prefs.set(PREF_MERGE_OPTION, mergeOption);
    Prefs.set(PREF_WINDOWLESS, windowless);
    Prefs.set(PREF_SERIES, seriesString);

    Prefs.set(PREF_FIRST, false);
    //Prefs.set(PREF_THUMBNAIL, forceThumbnails);
  }

  /** Parses the plugin argument for parameter values. */
  public void parseArg(String arg) {
    if (arg == null || arg.length() == 0) return;
    if (new Location(arg).exists()) {
      // old style arg: entire argument is a file path

      // this style is used by the HandleExtraFileTypes plugin

      // NB: This functionality must not be removed, or the plugin
      // will stop working correctly with HandleExtraFileTypes.

      location = LOCATION_LOCAL;
      id = arg;
      quiet = true; // suppress obnoxious error messages and such
    }
    else {
      // new style arg: split up similar to a macro options string, but
      // slightly different than macro options, in that boolean arguments
      // must be of the form "key=true" rather than just "key"

      // only the core options are supported for now

      // NB: This functionality enables multiple plugin entries to achieve
      // distinct behavior by calling the LociImporter plugin differently.

      stackFormat = Macro.getValue(arg, LABEL_STACK, stackFormat);
      stackOrder = Macro.getValue(arg, LABEL_ORDER, stackOrder);
      mergeChannels = getMacroValue(arg, LABEL_MERGE, mergeChannels);
      colorize = getMacroValue(arg, LABEL_COLORIZE, colorize);
      splitChannels = getMacroValue(arg, LABEL_SPLIT_C, splitChannels);
      splitFocalPlanes = getMacroValue(arg, LABEL_SPLIT_Z, splitFocalPlanes);
      splitTimepoints = getMacroValue(arg, LABEL_SPLIT_T, splitTimepoints);
      crop = getMacroValue(arg, LABEL_CROP, crop);
      showMetadata = getMacroValue(arg, LABEL_METADATA, showMetadata);
      showOMEXML = getMacroValue(arg, LABEL_OME_XML, showOMEXML);
      groupFiles = getMacroValue(arg, LABEL_GROUP, groupFiles);
      concatenate = getMacroValue(arg, LABEL_CONCATENATE, concatenate);
      specifyRanges = getMacroValue(arg, LABEL_RANGE, specifyRanges);
      autoscale = getMacroValue(arg, LABEL_AUTOSCALE, autoscale);
      virtual = getMacroValue(arg, LABEL_VIRTUAL, virtual);
      record = getMacroValue(arg, LABEL_RECORD, record);
      openAllSeries = getMacroValue(arg, LABEL_ALL_SERIES, openAllSeries);
      swapDimensions = getMacroValue(arg, LABEL_SWAP, swapDimensions);

      mergeOption = Macro.getValue(arg, LABEL_MERGE_OPTION, mergeOption);
      windowless = getMacroValue(arg, LABEL_WINDOWLESS, windowless);
      seriesString = Macro.getValue(arg, LABEL_SERIES, "0");

      location = Macro.getValue(arg, LABEL_LOCATION, location);
      id = Macro.getValue(arg, LABEL_ID, id);
    }
  }

  /**
   * Gets the location (type of data source) from macro options,
   * or user prompt if necessary.
   * @return status of operation
   */
  public int promptLocation() {
    if (location == null) {
      // Open a dialog asking the user what kind of dataset to handle.
      // Ask only if the location was not already specified somehow.
      // ImageJ will grab the value from the macro options, when possible.
      GenericDialog gd = new GenericDialog("Bio-Formats Dataset Location");
      gd.addChoice(LABEL_LOCATION, LOCATIONS, LOCATION_LOCAL);
      gd.showDialog();
      if (gd.wasCanceled()) return STATUS_CANCELED;
      location = gd.getNextChoice();
    }

    // verify that location is valid
    boolean isLocal = LOCATION_LOCAL.equals(location);
    boolean isHTTP = LOCATION_HTTP.equals(location);
    boolean isOME = LOCATION_OME.equals(location);
    boolean isOMERO = LOCATION_OMERO.equals(location);
    if (!isLocal && !isHTTP && !isOME && !isOMERO) {
      if (!quiet) IJ.error("Bio-Formats", "Invalid location: " + location);
      return STATUS_FINISHED;
    }
    return STATUS_OK;
  }

  /**
   * Gets the id (e.g., filename or URL) to open from macro options,
   * or user prompt if necessary.
   * @return status of operation
   */
  public int promptId() {
    if (isLocal()) return promptIdLocal();
    else if (isHTTP()) return promptIdHTTP();
    else return promptIdOME(); // isOME
  }

  /**
   * Gets the filename (id) to open from macro options,
   * or user prompt if necessary.
   * @return status of operation
   */
  public int promptIdLocal() {
    if (firstTime && IJ.isMacOSX()) {
      String osVersion = System.getProperty("os.version");
      if (osVersion == null ||
        osVersion.startsWith("10.4.") ||
        osVersion.startsWith("10.3.") ||
        osVersion.startsWith("10.2."))
      {
        // present user with one-time dialog box
        IJ.showMessage("Bio-Formats",
          "One-time warning: There is a bug in Java on Mac OS X with the\n" +
          "native file chooser that crashes ImageJ if you click on a file\n" +
          "in cxd, ipw, oib or zvi format while in column view mode.\n" +
          "You can work around the problem by switching to list view\n" +
          "(press Command+2) or by checking the \"Use JFileChooser to\n" +
          "Open/Save\" option in the Edit>Options>Input/Output... dialog.");
      }
    }
    String ijVersion = IJ.getVersion();
    if (firstTime && (ijVersion == null || ijVersion.compareTo("1.39u") < 0)) {
      // present user with one-time dialog box
      if (ijVersion == null) ijVersion = "unknown";
      IJ.showMessage("Bio-Formats",
        "One-time warning: Some features of Bio-Formats, such as the\n" +
        "Data Browser and some color handling options, require ImageJ\n" +
        "v1.39u or later. Your version is " + ijVersion +
        "; you will need to upgrade\n" +
        "if you wish to take advantage of these features.");
    }
    if (id == null) {
      // prompt user for the filename (or grab from macro options)
      OpenDialog od = new OpenDialog(LABEL_ID, id);
      String dir = od.getDirectory();
      String name = od.getFileName();
      if (dir == null || name == null) return STATUS_CANCELED;
      id = dir + name;
    }

    // verify that id is valid
    if (id != null) idLoc = new Location(id);
    if (idLoc == null || !idLoc.exists()) {
      if (!quiet) {
        IJ.error("Bio-Formats", idLoc == null ?
          "No file was specified." :
          "The specified file (" + id + ") does not exist.");
      }
      return STATUS_FINISHED;
    }
    idName = idLoc.getName();
    idType = "Filename";
    return STATUS_OK;
  }

  /**
   * Gets the URL (id) to open from macro options,
   * or user prompt if necessary.
   * @return status of operation
   */
  public int promptIdHTTP() {
    if (id == null) {
      // prompt user for the URL (or grab from macro options)
      GenericDialog gd = new GenericDialog("Bio-Formats URL");
      gd.addStringField("URL: ", "http://", 30);
      gd.showDialog();
      if (gd.wasCanceled()) return STATUS_CANCELED;
      id = gd.getNextString();
    }

    // verify that id is valid
    if (id == null) {
      if (!quiet) IJ.error("Bio-Formats", "No URL was specified.");
      return STATUS_FINISHED;
    }
    idName = id;
    idType = "URL";
    return STATUS_OK;
  }

  /**
   * Gets the OME server and image (id) to open from macro options,
   * or user prompt if necessary.
   * @return status of operation
   */
  public int promptIdOME() {
    if (id == null) {
      // CTR FIXME -- eliminate this kludge
      IJ.runPlugIn("loci.plugins.ome.OMEPlugin", "");
      return STATUS_FINISHED;
    }

    idType = "OME address";
    return STATUS_OK;
  }

  public int promptMergeOption(int[] nums, boolean spectral) {
    if (windowless) return STATUS_OK;
    GenericDialog gd = new GenericDialog("Merging Options...");

    String[] options = new String[spectral ? 8 : 7];
    options[6] = MERGE_DEFAULT;
    if (spectral) options[7] = MERGE_PROJECTION;
    for (int i=0; i<6; i++) {
      options[i] = nums[i] + " planes, " + (i + 2) + " channels per plane";
    }

    gd.addMessage("How would you like to merge this data?");
    gd.addChoice(LABEL_MERGE_OPTION, options, MERGE_DEFAULT);
    gd.showDialog();
    if (gd.wasCanceled()) return STATUS_CANCELED;

    mergeOption = options[gd.getNextChoiceIndex()];

    return STATUS_OK;
  }

  /**
   * Gets option values from macro options, or user prompt if necessary.
   * @return status of operation
   */
  public int promptOptions() {
    Vector stackTypes = new Vector();
    stackTypes.add(VIEW_NONE);
    stackTypes.add(VIEW_STANDARD);
    if (IJ.getVersion().compareTo("1.39l") >= 0) {
      stackTypes.add(VIEW_HYPERSTACK);
      stackTypes.add(VIEW_BROWSER);
    }
    if (Checker.checkClass(CLASS_VISBIO)) stackTypes.add(VIEW_VISBIO);
    if (Checker.checkClass(CLASS_IMAGE_5D)) stackTypes.add(VIEW_IMAGE_5D);
    if (Checker.checkClass(CLASS_VIEW_5D)) stackTypes.add(VIEW_VIEW_5D);
    final String[] stackFormats = new String[stackTypes.size()];
    stackTypes.copyInto(stackFormats);

    String[] stackOrders = new String[] {
      ORDER_DEFAULT, ORDER_XYZCT, ORDER_XYZTC, ORDER_XYCZT, ORDER_XYCTZ,
      ORDER_XYTZC, ORDER_XYTCZ
    };

    // prompt user for parameters (or grab from macro options)
    GenericDialog gd = new GenericDialog("Bio-Formats Import Options");
    gd.addChoice(LABEL_STACK, stackFormats, stackFormat);
    gd.addChoice(LABEL_ORDER, stackOrders, stackOrder);
    gd.addCheckbox(LABEL_MERGE, mergeChannels);
    gd.addCheckbox(LABEL_COLORIZE, colorize);
    gd.addCheckbox(LABEL_SPLIT_C, splitChannels);
    gd.addCheckbox(LABEL_SPLIT_Z, splitFocalPlanes);
    gd.addCheckbox(LABEL_SPLIT_T, splitTimepoints);
    gd.addCheckbox(LABEL_CROP, crop);
    gd.addCheckbox(LABEL_METADATA, showMetadata);
    gd.addCheckbox(LABEL_OME_XML, showOMEXML);
    gd.addCheckbox(LABEL_GROUP, groupFiles);
    gd.addCheckbox(LABEL_CONCATENATE, concatenate);
    gd.addCheckbox(LABEL_RANGE, specifyRanges);
    gd.addCheckbox(LABEL_AUTOSCALE, autoscale);
    gd.addCheckbox(LABEL_VIRTUAL, virtual);
    gd.addCheckbox(LABEL_RECORD, record);
    gd.addCheckbox(LABEL_ALL_SERIES, openAllSeries);
    gd.addCheckbox(LABEL_SWAP, swapDimensions);

    // extract GUI components from dialog and add listeners

    Vector labels = null;
    Label stackLabel = null, orderLabel = null;
    Component[] c = gd.getComponents();
    if (c != null) {
      labels = new Vector();
      for (int i=0; i<c.length; i++) {
        if (c[i] instanceof Label) {
          Label item = (Label) c[i];
          labels.add(item);
        }
      }
      stackLabel = (Label) labels.get(0);
      orderLabel = (Label) labels.get(1);
    }

    Vector choices = gd.getChoices();
    if (choices != null) {
      stackChoice = (Choice) choices.get(0);
      orderChoice = (Choice) choices.get(1);
      for (int i=0; i<choices.size(); i++) {
        Choice item = (Choice) choices.get(i);
        item.addFocusListener(this);
        item.addItemListener(this);
        item.addMouseListener(this);
      }
    }

    Vector boxes = gd.getCheckboxes();
    if (boxes != null) {
      mergeBox = (Checkbox) boxes.get(0);
      colorizeBox = (Checkbox) boxes.get(1);
      splitCBox = (Checkbox) boxes.get(2);
      splitZBox = (Checkbox) boxes.get(3);
      splitTBox = (Checkbox) boxes.get(4);
      cropBox = (Checkbox) boxes.get(5);
      metadataBox = (Checkbox) boxes.get(6);
      omexmlBox = (Checkbox) boxes.get(7);
      groupBox = (Checkbox) boxes.get(8);
      concatenateBox = (Checkbox) boxes.get(9);
      rangeBox = (Checkbox) boxes.get(10);
      autoscaleBox = (Checkbox) boxes.get(11);
      virtualBox = (Checkbox) boxes.get(12);
      recordBox = (Checkbox) boxes.get(13);
      allSeriesBox = (Checkbox) boxes.get(14);
      swapBox = (Checkbox) boxes.get(15);
      for (int i=0; i<boxes.size(); i++) {
        Checkbox item = (Checkbox) boxes.get(i);
        item.addFocusListener(this);
        item.addItemListener(this);
        item.addMouseListener(this);
      }
    }

    verifyOptions(null);

    // associate information for each option
    infoTable = new Hashtable();
    infoTable.put(stackLabel, INFO_STACK);
    infoTable.put(stackChoice, INFO_STACK);
    infoTable.put(orderLabel, INFO_ORDER);
    infoTable.put(orderChoice, INFO_ORDER);
    infoTable.put(mergeBox, INFO_MERGE);
    infoTable.put(colorizeBox, INFO_COLORIZE);
    infoTable.put(splitCBox, INFO_SPLIT_C);
    infoTable.put(splitZBox, INFO_SPLIT_Z);
    infoTable.put(splitTBox, INFO_SPLIT_T);
    infoTable.put(cropBox, INFO_CROP);
    infoTable.put(metadataBox, INFO_METADATA);
    infoTable.put(omexmlBox, INFO_OME_XML);
    infoTable.put(groupBox, INFO_GROUP);
    infoTable.put(concatenateBox, INFO_CONCATENATE);
    infoTable.put(rangeBox, INFO_RANGE);
    infoTable.put(autoscaleBox, INFO_AUTOSCALE);
    infoTable.put(virtualBox, INFO_VIRTUAL);
    infoTable.put(recordBox, INFO_RECORD);
    infoTable.put(allSeriesBox, INFO_ALL_SERIES);
    infoTable.put(swapBox, INFO_SWAP);

    // rebuild dialog using FormLayout to organize things more nicely

    String cols =
      // first column
      "pref, 3dlu, pref:grow, " +
      // second column
      "10dlu, pref";

    String rows =
      // Stack viewing        | Metadata viewing
      "pref, 3dlu, pref, 3dlu, pref, " +
      // Dataset organization | Memory management
      "9dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, " +
      // Color options        | Split into separate windows
      "9dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, " +
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
    builder.add(stackLabel, cc.xy(1, row));
    builder.add(stackChoice, cc.xy(3, row));
    row += 2;
    builder.add(orderLabel, cc.xy(1, row));
    builder.add(orderChoice, cc.xy(3, row));
    row += 2;
    builder.addSeparator("Dataset organization", cc.xyw(1, row, 3));
    row += 2;
    builder.add(groupBox, xyw(cc, 1, row, 3));
    row += 2;
    builder.add(swapBox, xyw(cc, 1, row, 3));
    row += 2;
    builder.add(allSeriesBox, xyw(cc, 1, row, 3));
    row += 2;
    builder.add(concatenateBox, xyw(cc, 1, row, 3));
    row += 2;
    builder.addSeparator("Color options", cc.xyw(1, row, 3));
    row += 2;
    builder.add(mergeBox, xyw(cc, 1, row, 3));
    row += 2;
    builder.add(colorizeBox, xyw(cc, 1, row, 3));
    row += 2;
    builder.add(autoscaleBox, xyw(cc, 1, row, 3));
    row += 2;

    // populate 2nd column
    row = 1;
    builder.addSeparator("Metadata viewing", cc.xy(5, row));
    row += 2;
    builder.add(metadataBox, xyw(cc, 5, row, 1));
    row += 2;
    builder.add(omexmlBox, xyw(cc, 5, row, 1));
    row += 2;
    builder.addSeparator("Memory management", cc.xy(5, row));
    row += 2;
    builder.add(virtualBox, xyw(cc, 5, row, 1));
    row += 2;
    builder.add(recordBox, xyw(cc, 5, row, 1));
    row += 2;
    builder.add(rangeBox, xyw(cc, 5, row, 1));
    row += 2;
    builder.add(cropBox, xyw(cc, 5, row, 1));
    row += 2;
    builder.addSeparator("Split into separate windows", cc.xy(5, row));
    row += 2;
    builder.add(splitCBox, xyw(cc, 5, row, 1));
    row += 2;
    builder.add(splitZBox, xyw(cc, 5, row, 1));
    row += 2;
    builder.add(splitTBox, xyw(cc, 5, row, 1));
    row += 2;

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

    stackFormat = stackFormats[gd.getNextChoiceIndex()];
    stackOrder = stackOrders[gd.getNextChoiceIndex()];
    mergeChannels = gd.getNextBoolean();
    colorize = gd.getNextBoolean();
    splitChannels = gd.getNextBoolean();
    splitFocalPlanes = gd.getNextBoolean();
    splitTimepoints = gd.getNextBoolean();
    crop = gd.getNextBoolean();
    showMetadata = gd.getNextBoolean();
    showOMEXML = gd.getNextBoolean();
    groupFiles = gd.getNextBoolean();
    concatenate = gd.getNextBoolean();
    specifyRanges = gd.getNextBoolean();
    autoscale = gd.getNextBoolean();
    virtual = gd.getNextBoolean();
    record = gd.getNextBoolean();
    openAllSeries = gd.getNextBoolean();
    swapDimensions = gd.getNextBoolean();

    return STATUS_OK;
  }

  /**
   * Gets file pattern from id, macro options, or user prompt if necessary.
   * @return status of operation
   */
  public int promptFilePattern() {
    if (windowless) return STATUS_OK;

    id = FilePattern.findPattern(idLoc);
    if (id == null) {
      IJ.showMessage("Bio-Formats",
        "Warning: Bio-Formats was unable to determine a grouping that\n" +
        "includes the file you chose. The most common reason for this\n" +
        "situation is that the folder contains extraneous files with " +
        "similar\n" +
        "names and numbers that confuse the detection algorithm.\n" +
        " \n" +
        "For example, if you have multiple datasets in the same folder\n" +
        "named series1_z*_c*.tif, series2_z*_c*.tif, etc., Bio-Formats\n" +
        "may try to group all such files into a single series.\n" +
        " \n" +
        "For best results, put each image series's files in their own " +
        "folder,\n" +
        "or type in a file pattern manually.\n");
      id = idLoc.getAbsolutePath();
    }

    // prompt user to confirm file pattern (or grab from macro options)
    GenericDialog gd = new GenericDialog("Bio-Formats File Stitching");
    int len = id.length() + 1;
    if (len > 80) len = 80;
    gd.addStringField("Pattern: ", id, len);
    gd.showDialog();
    if (gd.wasCanceled()) return STATUS_CANCELED;
    id = gd.getNextString();
    return STATUS_OK;
  }

  /**
   * Gets which series to open from macro options, or user prompt if necessary.
   * @param r The reader to use for extracting details of each series.
   * @param seriesLabels Label to display to user identifying each series.
   * @param series Boolean array indicating which series to include
   *   (populated by this method).
   * @return status of operation
   */
  public int promptSeries(IFormatReader r,
    String[] seriesLabels, boolean[] series)
  {
    if (windowless) {
      if (seriesString != null) {
        if (seriesString.startsWith("[")) {
          seriesString = seriesString.substring(1, seriesString.length() - 2);
        }
        Arrays.fill(series, false);
        StringTokenizer tokens = new StringTokenizer(seriesString, " ");
        while (tokens.hasMoreTokens()) {
          String token = tokens.nextToken().trim();
          int n = Integer.parseInt(token);
          if (n < series.length) series[n] = true;
        }
      }
      return STATUS_OK;
    }
    int seriesCount = r.getSeriesCount();

    // prompt user to specify series inclusion (or grab from macro options)
    GenericDialog gd = new GenericDialog("Bio-Formats Series Options") {
      public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if ("select".equals(cmd)) {
          Checkbox[] boxes =
            (Checkbox[]) getCheckboxes().toArray(new Checkbox[0]);
          for (int i=0; i<boxes.length; i++) {
            boxes[i].setState(true);
          }
        }
        else if ("deselect".equals(cmd)) {
          Checkbox[] boxes =
            (Checkbox[]) getCheckboxes().toArray(new Checkbox[0]);
          for (int i=0; i<boxes.length; i++) {
            boxes[i].setState(false);
          }
        }
        else {
          super.actionPerformed(e);
        }
      }
    };

    GridBagLayout gdl = (GridBagLayout) gd.getLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridwidth = GridBagConstraints.REMAINDER;

    Panel[] p = new Panel[seriesCount];
    for (int i=0; i<seriesCount; i++) {
      gd.addCheckbox(seriesLabels[i], series[i]);
      r.setSeries(i);
      int sx = r.getThumbSizeX() + 10; // a little extra padding
      int sy = r.getThumbSizeY();
      p[i] = new Panel();
      p[i].add(Box.createRigidArea(new Dimension(sx, sy)));
      gbc.gridy = i;
      if (forceThumbnails) {
        IJ.showStatus("Reading thumbnail for series #" + (i + 1));
        int z = r.getSizeZ() / 2;
        int t = r.getSizeT() / 2;
        int ndx = r.getIndex(z, 0, t);
        try {
          BufferedImage img = r.openThumbImage(ndx);
          if (isAutoscale() && r.getPixelType() != FormatTools.FLOAT) {
            img = AWTImageTools.autoscale(img);
          }
          ImageIcon icon = new ImageIcon(img);
          p[i].removeAll();
          p[i].add(new JLabel(icon));
        }
        catch (Exception e) { }
      }
      gdl.setConstraints(p[i], gbc);
      gd.add(p[i]);
    }
    Util.addScrollBars(gd);

    Panel buttons = new Panel();

    Button select = new Button("Select All");
    select.setActionCommand("select");
    select.addActionListener(gd);
    Button deselect = new Button("Deselect All");
    deselect.setActionCommand("deselect");
    deselect.addActionListener(gd);

    buttons.add(select);
    buttons.add(deselect);

    gbc.gridx = 0;
    gbc.gridy = seriesCount;
    gdl.setConstraints(buttons, gbc);
    gd.add(buttons);

    if (forceThumbnails) gd.showDialog();
    else {
      ThumbLoader loader = new ThumbLoader(r, p, gd, isAutoscale());
      gd.showDialog();
      loader.stop();
    }
    if (gd.wasCanceled()) return STATUS_CANCELED;

    seriesString = "[";
    for (int i=0; i<seriesCount; i++) {
      series[i] = gd.getNextBoolean();
      if (series[i]) {
        seriesString += i + " ";
      }
    }
    seriesString += "]";

    if (concatenate) {
      // toggle on compatible series
      // CTR FIXME -- why are we doing this?
      for (int i=0; i<seriesCount; i++) {
        if (series[i]) continue;

        r.setSeries(i);
        int sizeX = r.getSizeX();
        int sizeY = r.getSizeY();
        int pixelType = r.getPixelType();
        int sizeC = r.getSizeC();

        for (int j=0; j<seriesCount; j++) {
          if (j == i || !series[j]) continue;
          r.setSeries(j);
          if (sizeX == r.getSizeX() && sizeY == r.getSizeY() &&
            pixelType == r.getPixelType() && sizeC == r.getSizeC())
          {
            series[i] = true;
            break;
          }
        }
      }
    }

    return STATUS_OK;
  }

  public int promptCropSize(IFormatReader r, String[] labels, boolean[] series,
    Rectangle[] box)
  {
    GenericDialog gd = new GenericDialog("Bio-Formats Crop Options");
    for (int i=0; i<series.length; i++) {
      if (!series[i]) continue;
      gd.addMessage(labels[i].replaceAll("_", " "));
      gd.addNumericField("X_Coordinate_" + i, 0, 0);
      gd.addNumericField("Y_Coordinate_" + i, 0, 0);
      gd.addNumericField("Width_" + i, 0, 0);
      gd.addNumericField("Height_" + i, 0, 0);
    }
    Util.addScrollBars(gd);
    gd.showDialog();
    if (gd.wasCanceled()) return STATUS_CANCELED;

    for (int i=0; i<series.length; i++) {
      if (!series[i]) continue;
      r.setSeries(i);
      box[i].x = (int) gd.getNextNumber();
      box[i].y = (int) gd.getNextNumber();
      box[i].width = (int) gd.getNextNumber();
      box[i].height = (int) gd.getNextNumber();

      if (box[i].x < 0) box[i].x = 0;
      if (box[i].y < 0) box[i].y = 0;
      if (box[i].x >= r.getSizeX()) box[i].x = r.getSizeX() - box[i].width - 1;
      if (box[i].y >= r.getSizeY()) box[i].y = r.getSizeY() - box[i].height - 1;
      if (box[i].width < 1) box[i].width = 1;
      if (box[i].height < 1) box[i].height = 1;
      if (box[i].width + box[i].x > r.getSizeX()) {
        box[i].width = r.getSizeX() - box[i].x;
      }
      if (box[i].height + box[i].y > r.getSizeY()) {
        box[i].height = r.getSizeY() - box[i].y;
      }
    }

    return STATUS_OK;
  }

  /**
   * Gets the range of image planes to open from macro options,
   * or user prompt if necessary.
   * @param r The reader to use for extracting details of each series.
   * @param series Boolean array indicating the series
   *   for which ranges should be determined.
   * @param seriesLabels Label to display to user identifying each series
   * @param cBegin First C index to include (populated by this method).
   * @param cEnd Last C index to include (populated by this method).
   * @param cStep C dimension step size (populated by this method).
   * @param zBegin First Z index to include (populated by this method).
   * @param zEnd Last Z index to include (populated by this method).
   * @param zStep Z dimension step size (populated by this method).
   * @param tBegin First T index to include (populated by this method).
   * @param tEnd Last T index to include (populated by this method).
   * @param tStep T dimension step size (populated by this method).
   * @return status of operation
   */
  public int promptRange(IFormatReader r,
    boolean[] series, String[] seriesLabels,
    int[] cBegin, int[] cEnd, int[] cStep,
    int[] zBegin, int[] zEnd, int[] zStep,
    int[] tBegin, int[] tEnd, int[] tStep)
  {
    int seriesCount = r.getSeriesCount();

    // prompt user to specify series ranges (or grab from macro options)
    GenericDialog gd = new GenericDialog("Bio-Formats Range Options");
    for (int i=0; i<seriesCount; i++) {
      if (!series[i]) continue;
      r.setSeries(i);
      gd.addMessage(seriesLabels[i].replaceAll("_", " "));
      String s = seriesCount > 1 ? "_" + (i + 1) : "";
      if (r.isOrderCertain()) {
        if (r.getEffectiveSizeC() > 1) {
          gd.addNumericField("C_Begin" + s, cBegin[i] + 1, 0);
          gd.addNumericField("C_End" + s, cEnd[i] + 1, 0);
          gd.addNumericField("C_Step" + s, cStep[i], 0);
        }
        if (r.getSizeZ() > 1) {
          gd.addNumericField("Z_Begin" + s, zBegin[i] + 1, 0);
          gd.addNumericField("Z_End" + s, zEnd[i] + 1, 0);
          gd.addNumericField("Z_Step" + s, zStep[i], 0);
        }
        if (r.getSizeT() > 1) {
          gd.addNumericField("T_Begin" + s, tBegin[i] + 1, 0);
          gd.addNumericField("T_End" + s, tEnd[i] + 1, 0);
          gd.addNumericField("T_Step" + s, tStep[i], 0);
        }
      }
      else {
        gd.addNumericField("Begin" + s, cBegin[i] + 1, 0);
        gd.addNumericField("End" + s, cEnd[i] + 1, 0);
        gd.addNumericField("Step" + s, cStep[i], 0);
      }
    }
    Util.addScrollBars(gd);
    gd.showDialog();
    if (gd.wasCanceled()) return STATUS_CANCELED;

    for (int i=0; i<seriesCount; i++) {
      if (!series[i]) continue;
      r.setSeries(i);
      int sizeC = r.getEffectiveSizeC();
      int sizeZ = r.getSizeZ();
      int sizeT = r.getSizeT();
      boolean certain = r.isOrderCertain();

      if (certain) {
        if (r.getEffectiveSizeC() > 1) {
          cBegin[i] = (int) gd.getNextNumber() - 1;
          cEnd[i] = (int) gd.getNextNumber() - 1;
          cStep[i] = (int) gd.getNextNumber();
        }
        if (r.getSizeZ() > 1) {
          zBegin[i] = (int) gd.getNextNumber() - 1;
          zEnd[i] = (int) gd.getNextNumber() - 1;
          zStep[i] = (int) gd.getNextNumber();
        }
        if (r.getSizeT() > 1) {
          tBegin[i] = (int) gd.getNextNumber() - 1;
          tEnd[i] = (int) gd.getNextNumber() - 1;
          tStep[i] = (int) gd.getNextNumber();
        }
      }
      else {
        cBegin[i] = (int) gd.getNextNumber() - 1;
        cEnd[i] = (int) gd.getNextNumber() - 1;
        cStep[i] = (int) gd.getNextNumber();
      }
      int maxC = certain ? sizeC : r.getImageCount();
      if (cBegin[i] < 0) cBegin[i] = 0;
      if (cBegin[i] >= maxC) cBegin[i] = maxC - 1;
      if (cEnd[i] < cBegin[i]) cEnd[i] = cBegin[i];
      if (cEnd[i] >= maxC) cEnd[i] = maxC - 1;
      if (cStep[i] < 1) cStep[i] = 1;
      if (zBegin[i] < 0) zBegin[i] = 0;
      if (zBegin[i] >= sizeZ) zBegin[i] = sizeZ - 1;
      if (zEnd[i] < zBegin[i]) zEnd[i] = zBegin[i];
      if (zEnd[i] >= sizeZ) zEnd[i] = sizeZ - 1;
      if (zStep[i] < 1) zStep[i] = 1;
      if (tBegin[i] < 0) tBegin[i] = 0;
      if (tBegin[i] >= sizeT) tBegin[i] = sizeT - 1;
      if (tEnd[i] < tBegin[i]) tEnd[i] = tBegin[i];
      if (tEnd[i] >= sizeT) tEnd[i] = sizeT - 1;
      if (tStep[i] < 1) tStep[i] = 1;
    }

    return STATUS_OK;
  }

  /** Prompt for dimension swapping options. */
  public int promptSwap(DimensionSwapper r, boolean[] series) {
    GenericDialog gd = new GenericDialog("Dimension swapping options");

    int oldSeries = r.getSeries();
    String[] labels = new String[] {"Z", "C", "T"};
    for (int n=0; n<r.getSeriesCount(); n++) {
      if (!series[n]) continue;
      r.setSeries(n);

      gd.addMessage("Series " + n + ":\n");

      int[] axisSizes = new int[] {r.getSizeZ(), r.getSizeC(), r.getSizeT()};

      for (int i=0; i<labels.length; i++) {
        gd.addChoice(axisSizes[i] + "_planes", labels, labels[i]);
      }
    }
    Util.addScrollBars(gd);
    gd.showDialog();
    if (gd.wasCanceled()) return STATUS_CANCELED;

    for (int n=0; n<r.getSeriesCount(); n++) {
      r.setSeries(n);
      String z = gd.getNextChoice();
      String c = gd.getNextChoice();
      String t = gd.getNextChoice();

      if (z.equals(t) || z.equals(c) || c.equals(t)) {
        IJ.error("Invalid swapping options - each axis can be used only once.");
        return promptSwap(r, series);
      }

      String originalOrder = r.getDimensionOrder();
      StringBuffer sb = new StringBuffer();
      sb.append("XY");
      for (int i=2; i<originalOrder.length(); i++) {
        if (originalOrder.charAt(i) == 'Z') sb.append(z);
        else if (originalOrder.charAt(i) == 'C') sb.append(c);
        else if (originalOrder.charAt(i) == 'T') sb.append(t);
      }

      r.swapDimensions(sb.toString());
    }
    r.setSeries(oldSeries);

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

  // -- Static helper methods --

  private static boolean getMacroValue(String options,
    String key, boolean defaultValue)
  {
    String s = Macro.getValue(options, key, null);
    return s == null ? defaultValue : s.equalsIgnoreCase("true");
  }

  private static String info(String label) {
    return "<b>" + label.replaceAll("[_:]", " ").trim() + "</b>";
  }

  private static CellConstraints xyw(CellConstraints cc, int x, int y, int w) {
    return cc.xyw(x, y, w, CellConstraints.LEFT, CellConstraints.CENTER);
  }

  // -- Helper methods --

  /** Ensures that the options dialog has no mutually exclusive options. */
  private void verifyOptions(Object src) {
    // record GUI state

    //boolean stackEnabled = stackChoice.isEnabled();
    boolean orderEnabled = orderChoice.isEnabled();
    boolean mergeEnabled = mergeBox.isEnabled();
    boolean colorizeEnabled = colorizeBox.isEnabled();
    boolean splitCEnabled = splitCBox.isEnabled();
    boolean splitZEnabled = splitZBox.isEnabled();
    boolean splitTEnabled = splitTBox.isEnabled();
    boolean metadataEnabled = metadataBox.isEnabled();
    boolean omexmlEnabled = omexmlBox.isEnabled();
    boolean groupEnabled = groupBox.isEnabled();
    boolean concatenateEnabled = concatenateBox.isEnabled();
    boolean rangeEnabled = rangeBox.isEnabled();
    boolean autoscaleEnabled = autoscaleBox.isEnabled();
    boolean virtualEnabled = virtualBox.isEnabled();
    boolean recordEnabled = recordBox.isEnabled();
    boolean allSeriesEnabled = allSeriesBox.isEnabled();
    boolean cropEnabled = cropBox.isEnabled();
    boolean swapEnabled = swapBox.isEnabled();

    boolean isStackNone = false;
    boolean isStackStandard = false;
    boolean isStackHyperstack = false;
    boolean isStackBrowser = false;
    boolean isStackVisBio = false;
    boolean isStackImage5D = false;
    boolean isStackView5D = false;
    String stackValue = stackChoice.getSelectedItem();
    if (stackValue.equals(VIEW_NONE)) isStackNone = true;
    else if (stackValue.equals(VIEW_STANDARD)) isStackStandard = true;
    else if (stackValue.equals(VIEW_HYPERSTACK)) isStackHyperstack = true;
    else if (stackValue.equals(VIEW_BROWSER)) isStackBrowser = true;
    else if (stackValue.equals(VIEW_VISBIO)) isStackVisBio = true;
    else if (stackValue.equals(VIEW_IMAGE_5D)) isStackImage5D = true;
    else if (stackValue.equals(VIEW_VIEW_5D)) isStackView5D = true;
    String orderValue = orderChoice.getSelectedItem();
    boolean isMerge = mergeBox.getState();
    boolean isColorize = colorizeBox.getState();
    boolean isSplitC = splitCBox.getState();
    boolean isSplitZ = splitZBox.getState();
    boolean isSplitT = splitTBox.getState();
    boolean isMetadata = metadataBox.getState();
    boolean isOMEXML = omexmlBox.getState();
    boolean isGroup = groupBox.getState();
    boolean isConcatenate = concatenateBox.getState();
    boolean isRange = rangeBox.getState();
    boolean isAutoscale = autoscaleBox.getState();
    boolean isVirtual = virtualBox.getState();
    boolean isRecord = recordBox.getState();
    boolean isAllSeries = allSeriesBox.getState();
    boolean isCrop = cropBox.getState();
    boolean isSwap = swapBox.getState();

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

    // orderChoice
    orderEnabled = isStackStandard || isStackVisBio;
    if (src == stackChoice) {
      if (isStackHyperstack || isStackBrowser || isStackImage5D) {
        orderValue = ORDER_XYCZT;
      }
      else if (isStackView5D) orderValue = ORDER_XYZCT;
      else orderValue = ORDER_DEFAULT;
    }

    // == Metadata viewing ==

    // metadataBox
    metadataEnabled = !isStackNone;
    if (!metadataEnabled) isMetadata = true;

    // omexmlBox
    // NB: no other options affect omexmlBox

    // == Dataset organization ==

    // groupBox
    // NB: no other options affect groupBox
    groupEnabled = !isOME() && !isOMERO();
    if (!groupEnabled) isGroup = false;
    else if (src == stackChoice && isStackBrowser) isGroup = true;

    // swapBox
    // NB: no other options affect swapBox

    // allSeriesBox
    // NB: no other options affect allSeriesBox

    // concatenateBox
    // NB: no other options affect concatenateBox

    // == Memory management ==

    // virtualBox
    virtualEnabled = !isStackNone && !isStackImage5D && !isStackView5D;
    if (!virtualEnabled) isVirtual = false;
    else if (src == stackChoice && isStackBrowser) isVirtual = true;

    // recordBox
    recordEnabled = isVirtual;
    if (!recordEnabled) isRecord = false;

    // rangeBox
    rangeEnabled = !isStackNone;
    if (!rangeEnabled) isRange = false;

    // cropBox
    cropEnabled = !isStackNone;
    if (!cropEnabled) isCrop = false;

    // == Color options ==

    // mergeBox
    mergeEnabled = !isStackImage5D;
    if (!mergeEnabled) isMerge = false;

    // colorizeBox
    colorizeEnabled = !isMerge && !isStackBrowser &&
      !isStackImage5D && !isStackView5D;
    if (!colorizeEnabled) isColorize = false;

    // autoscaleBox
    autoscaleEnabled = !isVirtual;
    if (!autoscaleEnabled) isAutoscale = false;

    // == Split into separate windows ==

    boolean splitEnabled = !isStackNone && !isStackBrowser &&
      !isStackVisBio && !isStackImage5D && !isStackView5D && !isVirtual;
    // TODO: make splitting work with Data Browser & virtual stacks

    // splitCBox
    splitCEnabled = splitEnabled && !isMerge;
    if (!splitCEnabled) isSplitC = false;

    // splitZBox
    splitZEnabled = splitEnabled;
    if (!splitZEnabled) isSplitZ = false;

    // splitTBox
    splitTEnabled = splitEnabled;
    if (!splitTEnabled) isSplitT = false;

    // update state of each option, in case anything changed

    //stackChoice.setEnabled(stackEnabled);
    orderChoice.setEnabled(orderEnabled);
    mergeBox.setEnabled(mergeEnabled);
    colorizeBox.setEnabled(colorizeEnabled);
    splitCBox.setEnabled(splitCEnabled);
    splitZBox.setEnabled(splitZEnabled);
    splitTBox.setEnabled(splitTEnabled);
    metadataBox.setEnabled(metadataEnabled);
    omexmlBox.setEnabled(omexmlEnabled);
    groupBox.setEnabled(groupEnabled);
    concatenateBox.setEnabled(concatenateEnabled);
    rangeBox.setEnabled(rangeEnabled);
    autoscaleBox.setEnabled(autoscaleEnabled);
    virtualBox.setEnabled(virtualEnabled);
    recordBox.setEnabled(recordEnabled);
    allSeriesBox.setEnabled(allSeriesEnabled);
    cropBox.setEnabled(cropEnabled);
    swapBox.setEnabled(swapEnabled);

    //stackChoice.select(stackValue);
    orderChoice.select(orderValue);
    mergeBox.setState(isMerge);
    colorizeBox.setState(isColorize);
    splitCBox.setState(isSplitC);
    splitZBox.setState(isSplitZ);
    splitTBox.setState(isSplitT);
    metadataBox.setState(isMetadata);
    omexmlBox.setState(isOMEXML);
    groupBox.setState(isGroup);
    concatenateBox.setState(isConcatenate);
    rangeBox.setState(isRange);
    autoscaleBox.setState(isAutoscale);
    virtualBox.setState(isVirtual);
    recordBox.setState(isRecord);
    allSeriesBox.setState(isAllSeries);
    cropBox.setState(isCrop);
    swapBox.setState(isSwap);

    if (IS_GLITCHED) {
      // HACK - workaround a Mac OS X bug where GUI components do not update

      // list of affected components
      Component[] c = {
        stackChoice,
        orderChoice,
        mergeBox,
        colorizeBox,
        splitCBox,
        splitZBox,
        splitTBox,
        metadataBox,
        omexmlBox,
        groupBox,
        concatenateBox,
        rangeBox,
        autoscaleBox,
        virtualBox,
        recordBox,
        allSeriesBox,
        cropBox,
        swapBox
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

  private void sleep(long ms) {
    try {
      Thread.sleep(ms);
    }
    catch (InterruptedException exc) { }
  }

}
