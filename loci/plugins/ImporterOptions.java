//
// ImporterOptions.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
4D Data Browser, Bio-Formats Importer, Bio-Formats Exporter and OME plugins.
Copyright (C) 2006-@year@ Melissa Linkert, Christopher Peterson,
Curtis Rueden, Philip Huettl and Francis Wong.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.plugins;

import ij.*;
import ij.gui.GenericDialog;
import ij.io.OpenDialog;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import loci.formats.*;

/**
 * Helper class for managing Bio-Formats Importer options.
 * Gets parameter values through a variety of means, including
 * preferences from IJ_Prefs.txt, plugin argument string, macro options,
 * and user input from dialog boxes.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/ImporterOptions.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/ImporterOptions.java">SVN</a></dd></dl>
 */
public class ImporterOptions implements ItemListener {

  // -- Constants --

  // enumeration for status
  public static final int STATUS_OK = 0;
  public static final int STATUS_CANCELED = 1;
  public static final int STATUS_FINISHED = 2;

  // enumeration for stackFormat
  public static final String VIEW_NONE = "Metadata only";
  public static final String VIEW_STANDARD = "Standard ImageJ";
  public static final String VIEW_BROWSER = "4D Data Browser";
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

  public static final String MERGE_DEFAULT = "Do not merge";

  // class to check for each viewing option
  private static final String CLASS_BROWSER =
    "loci.plugins.browser.LociDataBrowser";
  private static final String CLASS_IMAGE_5D = "i5d.Image5D";
  private static final String CLASS_VIEW_5D = "View5D_";

  // enumeration for location
  public static final String LOCATION_LOCAL = "Local machine";
  public static final String LOCATION_HTTP = "Internet";
  public static final String LOCATION_OME = "OME server";
  public static final String[] LOCATIONS = {
    LOCATION_LOCAL, LOCATION_HTTP, LOCATION_OME
  };

  // keys for use in IJ_Prefs.txt
  public static final String PREF_FIRST = "bioformats.firstTime";
  public static final String PREF_STACK = "bioformats.stackFormat";
  public static final String PREF_ORDER = "bioformats.stackOrder";
  public static final String PREF_MERGE = "bioformats.mergeChannels";
  public static final String PREF_COLORIZE = "bioformats.colorize";
  public static final String PREF_C = "bioformats.splitWindows";
  public static final String PREF_Z = "bioformats.splitFocalPlanes";
  public static final String PREF_T = "bioformats.splitTimepoints";
  public static final String PREF_METADATA = "bioformats.showMetadata";
  public static final String PREF_GROUP = "bioformats.groupFiles";
  public static final String PREF_CONCATENATE = "bioformats.concatenate";
  public static final String PREF_RANGE = "bioformats.specifyRanges";
  public static final String PREF_AUTOSCALE = "bioformats.autoscale";
  public static final String PREF_THUMBNAIL = "bioformats.forceThumbnails";
  public static final String PREF_MERGE_OPTION = "bioformats.mergeOption";
  public static final String PREF_SERIES = "bioformats.series";
  public static final String PREF_WINDOWLESS = "bioformats.windowless";

  // labels for user dialog; when trimmed these double as argument & macro keys
  public static final String LABEL_STACK = "View stack with: ";
  public static final String LABEL_ORDER = "Stack_order: ";
  public static final String LABEL_MERGE = "Merge_channels to RGB";
  public static final String LABEL_COLORIZE = "Colorize channels";
  public static final String LABEL_C = "Split_channels into separate windows";
  public static final String LABEL_Z =
    "Split_focal planes into separate windows";
  public static final String LABEL_T = "Split_timepoints into separate windows";
  public static final String LABEL_METADATA =
    "Display_metadata in results window";
  public static final String LABEL_GROUP = "Group_files with similar names";
  public static final String LABEL_CONCATENATE =
    "Concatenate_series when compatible";
  public static final String LABEL_RANGE = "Specify_range for each series";
  public static final String LABEL_AUTOSCALE = "Autoscale images";

  public static final String LABEL_LOCATION = "Location: ";
  public static final String LABEL_ID = "Open";
  public static final String LABEL_MERGE_OPTION = "Merging Options";
  public static final String LABEL_WINDOWLESS = "windowless";
  public static final String LABEL_SERIES = "series";

  // -- Fields - GUI components --

  private Choice stackChoice;
  private Choice orderChoice;
  private Checkbox mergeBox;
  private Checkbox colorizeBox;
  private Checkbox splitCBox;
  private Checkbox splitZBox;
  private Checkbox splitTBox;
  private Checkbox metadataBox;
  private Checkbox groupBox;
  private Checkbox concatenateBox;
  private Checkbox rangeBox;
  private Checkbox autoscaleBox;
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
  private boolean showMetadata;
  private boolean groupFiles;
  private boolean concatenate;
  private boolean specifyRanges;
  private boolean autoscale;
  private boolean forceThumbnails;
  private boolean windowless;
  private String mergeOption;
  private String seriesString;

  private String location;
  private String id;
  private boolean quiet;

  private Location idLoc;
  private String idName;
  private String idType;

  // -- ImporterOptions API methods - accessors --

  public boolean isFirstTime() { return firstTime; }
  public String getStackFormat() { return stackFormat; }
  public String getStackOrder() { return stackOrder; }
  public boolean isMergeChannels() { return mergeChannels; }
  public boolean isColorize() { return colorize; }
  public boolean isSplitChannels() { return splitChannels; }
  public boolean isSplitFocalPlanes() { return splitFocalPlanes; }
  public boolean isSplitTimepoints() { return splitTimepoints; }
  public boolean isShowMetadata() { return showMetadata; }
  public boolean isGroupFiles() { return groupFiles; }
  public boolean isConcatenate() { return concatenate; }
  public boolean isSpecifyRanges() { return specifyRanges; }
  public boolean isForceThumbnails() { return forceThumbnails; }
  public boolean isAutoscale() { return autoscale; }
  public boolean isWindowless() { return windowless; }
  public String getMergeOption() { return mergeOption; }

  public boolean isViewNone() { return VIEW_NONE.equals(stackFormat); }
  public boolean isViewStandard() { return VIEW_STANDARD.equals(stackFormat); }
  public boolean isViewImage5D() { return VIEW_IMAGE_5D.equals(stackFormat); }
  public boolean isViewBrowser() { return VIEW_BROWSER.equals(stackFormat); }
  public boolean isViewView5D() { return VIEW_VIEW_5D.equals(stackFormat); }

  public String getLocation() { return location; }
  public String getId() { return id; }
  public boolean isQuiet() { return quiet; }

  public boolean isLocal() { return LOCATION_LOCAL.equals(location); }
  public boolean isHTTP() { return LOCATION_HTTP.equals(location); }
  public boolean isOME() { return LOCATION_OME.equals(location); }

  public Location getIdLocation() { return idLoc; }
  public String getIdName() { return idName; }
  public String getIdType() { return idType; }

  // -- ImporterOptions API methods - mutators --

  public void setStackFormat(String s) { stackFormat = s; }
  public void setStackOrder(String s) { stackOrder = s; }
  public void setMergeChannels(boolean b) { mergeChannels = b; }
  public void setColorize(boolean b) { colorize = b; }
  public void setSplitChannels(boolean b) { splitChannels = b; }
  public void setSplitFocalPlanes(boolean b) { splitFocalPlanes = b; }
  public void setSplitTimepoints(boolean b) { splitTimepoints = b; }
  public void setShowMetadata(boolean b) { showMetadata = b; }
  public void setGroupFiles(boolean b) { groupFiles = b; }
  public void setConcatenate(boolean b) { concatenate = b; }
  public void setSpecifyRanges(boolean b) { specifyRanges = b; }
  public void setForceThumbnails(boolean b) { forceThumbnails = b; }
  public void setAutoscale(boolean b) { autoscale = b; }
  public void setWindowless(boolean b) { windowless = b; }

  /** Loads default option values from IJ_Prefs.txt. */
  public void loadPreferences() {
    firstTime = Prefs.get(PREF_FIRST, true);
    stackFormat = Prefs.get(PREF_STACK, VIEW_STANDARD);
    stackOrder = Prefs.get(PREF_ORDER, ORDER_DEFAULT);
    mergeChannels = Prefs.get(PREF_MERGE, false);
    colorize = Prefs.get(PREF_COLORIZE, false);
    splitChannels = Prefs.get(PREF_C, true);
    splitFocalPlanes = Prefs.get(PREF_Z, false);
    splitTimepoints = Prefs.get(PREF_T, false);
    showMetadata = Prefs.get(PREF_METADATA, false);
    groupFiles = Prefs.get(PREF_GROUP, false);
    concatenate = Prefs.get(PREF_CONCATENATE, false);
    specifyRanges = Prefs.get(PREF_RANGE, false);
    forceThumbnails = Prefs.get(PREF_THUMBNAIL, false);
    autoscale = Prefs.get(PREF_AUTOSCALE, true);
    mergeOption = Prefs.get(PREF_MERGE_OPTION, MERGE_DEFAULT);
    seriesString = Prefs.get(PREF_SERIES, "0");
    windowless = Prefs.get(PREF_WINDOWLESS, false);
  }

  /** Saves option values to IJ_Prefs.txt as the new defaults. */
  public void savePreferences() {
    Prefs.set(PREF_FIRST, false);
    Prefs.set(PREF_STACK, stackFormat);
    Prefs.set(PREF_ORDER, stackOrder);
    Prefs.set(PREF_MERGE, mergeChannels);
    Prefs.set(PREF_COLORIZE, colorize);
    Prefs.set(PREF_C, splitChannels);
    Prefs.set(PREF_Z, splitFocalPlanes);
    Prefs.set(PREF_T, splitTimepoints);
    Prefs.set(PREF_METADATA, showMetadata);
    Prefs.set(PREF_GROUP, groupFiles);
    Prefs.set(PREF_CONCATENATE, concatenate);
    Prefs.set(PREF_RANGE, specifyRanges);
    Prefs.set(PREF_MERGE_OPTION, mergeOption);
    Prefs.set(PREF_AUTOSCALE, autoscale);
    Prefs.set(PREF_SERIES, seriesString);
    Prefs.set(PREF_WINDOWLESS, windowless);
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

      mergeChannels = getMacroValue(arg, LABEL_MERGE, mergeChannels);
      colorize = getMacroValue(arg, LABEL_COLORIZE, colorize);
      splitChannels = getMacroValue(arg, LABEL_C, splitChannels);
      splitFocalPlanes = getMacroValue(arg, LABEL_Z, splitFocalPlanes);
      splitTimepoints = getMacroValue(arg, LABEL_T, splitTimepoints);
      showMetadata = getMacroValue(arg, LABEL_METADATA, showMetadata);
      groupFiles = getMacroValue(arg, LABEL_GROUP, groupFiles);
      concatenate = getMacroValue(arg, LABEL_CONCATENATE, concatenate);
      specifyRanges = getMacroValue(arg, LABEL_RANGE, specifyRanges);
      stackFormat = Macro.getValue(arg, LABEL_STACK, stackFormat);
      mergeOption = Macro.getValue(arg, LABEL_MERGE_OPTION, mergeOption);
      autoscale = getMacroValue(arg, LABEL_AUTOSCALE, autoscale);
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
    if (!isLocal && !isHTTP && !isOME) {
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
      // present user with one-time dialog box
      IJ.showMessage("Bio-Formats",
        "Please note: There is a bug in Java on Mac OS X with the\n" +
        "native file chooser that crashes ImageJ if you click on a file\n" +
        "in cxd, ipw, oib or zvi format while in column view mode.\n" +
        "You can work around the problem by switching to list view\n" +
        "(press Command+2) or by checking the \"Use JFileChooser\n" +
        "to Open/Save\" option in the Edit>Options>Input/Output...\n" +
        "dialog. This message will not appear again.");
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
      IJ.runPlugIn("loci.plugins.OMEPlugin", "");
      return STATUS_FINISHED;
    }

    idType = "OME address";
    return STATUS_OK;
  }

  public int promptMergeOption(int one, int two, int three) {
    if (windowless) return STATUS_OK;
    GenericDialog gd = new GenericDialog("Merging Options...");

    String[] options = new String[] {one + " planes, 2 channels per plane",
      two + " planes, 3 channels per plane",
      three + " planes, 4 channels per plane", MERGE_DEFAULT};

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
    if (Checker.checkClass(CLASS_BROWSER)) stackTypes.add(VIEW_BROWSER);
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
    gd.addCheckbox(LABEL_C, splitChannels);
    gd.addCheckbox(LABEL_Z, splitFocalPlanes);
    gd.addCheckbox(LABEL_T, splitTimepoints);
    gd.addCheckbox(LABEL_METADATA, showMetadata);
    gd.addCheckbox(LABEL_GROUP, groupFiles);
    gd.addCheckbox(LABEL_CONCATENATE, concatenate);
    gd.addCheckbox(LABEL_RANGE, specifyRanges);
    gd.addCheckbox(LABEL_AUTOSCALE, autoscale);

    // extract GUI components from dialog and add listeners
    Vector choices = gd.getChoices();
    if (choices != null) {
      stackChoice = (Choice) choices.get(0);
      orderChoice = (Choice) choices.get(1);
      for (int i=0; i<choices.size(); i++) {
        ((Choice) choices.get(i)).addItemListener(this);
      }
    }
    Vector boxes = gd.getCheckboxes();
    if (boxes != null) {
      mergeBox = (Checkbox) boxes.get(0);
      colorizeBox = (Checkbox) boxes.get(1);
      splitCBox = (Checkbox) boxes.get(2);
      splitZBox = (Checkbox) boxes.get(3);
      splitTBox = (Checkbox) boxes.get(4);
      metadataBox = (Checkbox) boxes.get(5);
      groupBox = (Checkbox) boxes.get(6);
      concatenateBox = (Checkbox) boxes.get(7);
      rangeBox = (Checkbox) boxes.get(8);
      autoscaleBox = (Checkbox) boxes.get(9);
      for (int i=0; i<boxes.size(); i++) {
        ((Checkbox) boxes.get(i)).addItemListener(this);
      }
    }

    gd.showDialog();
    if (gd.wasCanceled()) return STATUS_CANCELED;

    stackFormat = stackFormats[gd.getNextChoiceIndex()];
    stackOrder = stackOrders[gd.getNextChoiceIndex()];
    mergeChannels = gd.getNextBoolean();
    colorize = gd.getNextBoolean();
    splitChannels = gd.getNextBoolean();
    splitFocalPlanes = gd.getNextBoolean();
    splitTimepoints = gd.getNextBoolean();
    showMetadata = gd.getNextBoolean();
    groupFiles = gd.getNextBoolean();
    concatenate = gd.getNextBoolean();
    specifyRanges = gd.getNextBoolean();
    autoscale = gd.getNextBoolean();

    return STATUS_OK;
  }

  /**
   * Gets file pattern from id, macro options, or user prompt if necessary.
   * @return status of operation
   */
  public int promptFilePattern() {
    if (windowless) return STATUS_OK;

    id = FilePattern.findPattern(idLoc);

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
    GenericDialog gd = new GenericDialog("Bio-Formats Series Options");

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
          ImageIcon icon = new ImageIcon(r.openThumbImage(ndx));
          p[i].removeAll();
          p[i].add(new JLabel(icon));
        }
        catch (Exception e) { }
      }
      gdl.setConstraints(p[i], gbc);
      gd.add(p[i]);
    }
    Util.addScrollBars(gd);
    if (forceThumbnails) gd.showDialog();
    else {
      ThumbLoader loader = new ThumbLoader(r, p, gd);
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

  // -- ItemListener API methods --

  /** Handles toggling of mutually exclusive options. */
  public void itemStateChanged(ItemEvent e) {
    Object src = e.getSource();
    if (src == stackChoice) {
      String s = stackChoice.getSelectedItem();
      if (s.equals(VIEW_NONE)) {
        metadataBox.setState(true);
        rangeBox.setState(false);
      }
      if (s.equals(VIEW_STANDARD)) {
      }
      else if (s.equals(VIEW_BROWSER)) {
        splitCBox.setState(false);
        splitZBox.setState(false);
        splitTBox.setState(false);
        rangeBox.setState(false);
        concatenateBox.setState(false); // TEMPORARY
      }
      else if (s.equals(VIEW_IMAGE_5D)) {
        mergeBox.setState(false);
      }
      else if (s.equals(VIEW_VIEW_5D)) {
      }
    }
    else if (src == mergeBox) {
      if (mergeBox.getState()) {
        colorizeBox.setState(false);
        splitCBox.setState(false);
        String s = stackChoice.getSelectedItem();
        if (s.equals(VIEW_IMAGE_5D)) stackChoice.select(VIEW_STANDARD);
      }
    }
    else if (src == colorizeBox) {
      if (colorizeBox.getState()) {
        mergeBox.setState(false);
        splitCBox.setState(!stackChoice.getSelectedItem().equals(VIEW_BROWSER));
      }
    }
    else if (src == splitCBox) {
      if (splitCBox.getState()) {
        mergeBox.setState(false);
        String s = stackChoice.getSelectedItem();
        if (s.equals(VIEW_BROWSER)) stackChoice.select(VIEW_STANDARD);
      }
    }
    else if (src == metadataBox) {
      if (!metadataBox.getState()) {
        String s = stackChoice.getSelectedItem();
        if (s.equals(VIEW_NONE)) stackChoice.select(VIEW_STANDARD);
      }
    }
    else if (src == groupBox) {
      if (groupBox.getState() && getLocation().equals(LOCATION_OME)) {
        groupBox.setState(false);
      }
    }
    else if (src == concatenateBox) {
      if (concatenateBox.getState()) {
        // TEMPORARY
        String s = stackChoice.getSelectedItem();
        if (s.equals(VIEW_BROWSER)) stackChoice.select(VIEW_STANDARD);
      }
    }
    else if (src == rangeBox) {
      if (rangeBox.getState()) {
        String s = stackChoice.getSelectedItem();
        if (s.equals(VIEW_NONE) || s.equals(VIEW_BROWSER)) {
          stackChoice.select(VIEW_STANDARD);
        }
      }
    }
  }

  // -- Helper methods --

  private boolean getMacroValue(String options,
    String key, boolean defaultValue)
  {
    String s = Macro.getValue(options, key, null);
    return s == null ? defaultValue : s.equalsIgnoreCase("true");
  }

}
