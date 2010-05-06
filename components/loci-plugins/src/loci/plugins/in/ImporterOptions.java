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

package loci.plugins.in;

import ij.IJ;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import loci.common.Location;
import loci.common.Region;
import loci.formats.FilePattern;
import loci.formats.FormatException;
import loci.formats.meta.IMetadata;
import loci.plugins.BF;
import loci.plugins.prefs.OptionsDialog;
import loci.plugins.prefs.OptionsList;
import loci.plugins.prefs.StringOption;
import loci.plugins.util.ImageProcessorReader;
import loci.plugins.util.LibraryChecker;

/**
 * Helper class for managing Bio-Formats Importer options.
 * Gets parameter values through a variety of means, including
 * preferences from IJ_Prefs.txt, plugin argument string, macro options,
 * and user input from dialog boxes.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/ImporterOptions.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/ImporterOptions.java">SVN</a></dd></dl>
 */
public class ImporterOptions extends OptionsList {

  // -- Constants --

  // option keys
  public static final String KEY_AUTOSCALE       = "autoscale";
  public static final String KEY_COLORIZE        = "colorize";
  public static final String KEY_CONCATENATE     = "concatenate";
  public static final String KEY_CROP            = "crop";
  public static final String KEY_CUSTOM_COLORIZE = "customColorize";
  public static final String KEY_FIRST           = "firstTime";
  public static final String KEY_FORCE_THUMBS    = "forceThumbnails";
  public static final String KEY_GROUP_FILES     = "groupFiles";
  public static final String KEY_UNGROUP_FILES   = "ungroupFiles";
  public static final String KEY_ID              = "id";
  public static final String KEY_LOCATION        = "location";
  public static final String KEY_MERGE_CHANNELS  = "mergeChannels";
  public static final String KEY_MERGE_OPTION    = "mergeOption";
  public static final String KEY_OPEN_ALL_SERIES = "openAllSeries";
  public static final String KEY_QUIET           = "quiet";
  public static final String KEY_RECORD          = "record";
  public static final String KEY_SERIES          = "series";
  public static final String KEY_SHOW_METADATA   = "showMetadata";
  public static final String KEY_SHOW_OME_XML    = "showOMEXML";
  public static final String KEY_SHOW_ROIS       = "showROIs";
  public static final String KEY_SPECIFY_RANGES  = "specifyRanges";
  public static final String KEY_SPLIT_Z         = "splitFocalPlanes";
  public static final String KEY_SPLIT_T         = "splitTimepoints";
  public static final String KEY_SPLIT_C         = "splitWindows";
  public static final String KEY_STACK_FORMAT    = "stackFormat";
  public static final String KEY_STACK_ORDER     = "stackOrder";
  public static final String KEY_SWAP_DIMS       = "swapDimensions";
  public static final String KEY_UPGRADE_CHECK   = "upgradeCheck";
  public static final String KEY_VIRTUAL         = "virtual";
  public static final String KEY_WINDOWLESS      = "windowless";

  // possible values for location
  public static final String LOCATION_LOCAL = "Local machine";
  public static final String LOCATION_HTTP  = "Internet";
  public static final String LOCATION_OME   = "OME server";
  public static final String LOCATION_OMERO = "OMERO server";

  // possible values for stackFormat
  public static final String VIEW_NONE       = "Metadata only";
  public static final String VIEW_STANDARD   = "Standard ImageJ";
  public static final String VIEW_HYPERSTACK = "Hyperstack";
  public static final String VIEW_BROWSER    = "Data Browser";
  public static final String VIEW_VISBIO     = "VisBio";
  public static final String VIEW_IMAGE_5D   = "Image5D";
  public static final String VIEW_VIEW_5D    = "View5D";

  // class to check for each stackFormat value
  private static final String CLASS_VISBIO   = "loci.visbio.VisBio";
  private static final String CLASS_IMAGE_5D = "i5d.Image5D";
  private static final String CLASS_VIEW_5D  = "view5d.View5D_";

  // possible values for stackOrder
  public static final String ORDER_DEFAULT = "Default";
  public static final String ORDER_XYZCT   = "XYZCT";
  public static final String ORDER_XYZTC   = "XYZTC";
  public static final String ORDER_XYCZT   = "XYCZT";
  public static final String ORDER_XYTCZ   = "XYTCZ";
  public static final String ORDER_XYCTZ   = "XYCTZ";
  public static final String ORDER_XYTZC   = "XYTZC";

  // -- Fields - derived values --

  protected ImporterReader reader;
  protected ImporterMetadata metadata;
  protected int[] cCount, zCount, tCount;

  // -- Fields -- secondary values --

  // series options
  protected List<Boolean> seriesOn = new ArrayList<Boolean>();

  // TODO - swap options need to be programmatically settable

  // range options
  protected List<Integer> cBegin = new ArrayList<Integer>();
  protected List<Integer> cEnd = new ArrayList<Integer>();
  protected List<Integer> cStep = new ArrayList<Integer>();
  protected List<Integer> zBegin = new ArrayList<Integer>();
  protected List<Integer> zEnd = new ArrayList<Integer>();
  protected List<Integer> zStep = new ArrayList<Integer>();
  protected List<Integer> tBegin = new ArrayList<Integer>();
  protected List<Integer> tEnd = new ArrayList<Integer>();
  protected List<Integer> tStep = new ArrayList<Integer>();

  // crop options
  protected List<Region> cropRegion = new ArrayList<Region>();

  // -- Fields - internal --

  private String[] seriesLabels;

  // -- Constructor --

  public ImporterOptions() throws IOException {
    super("importer-options.txt", ImporterOptions.class);
    // remove unavailable stack formats
    StringOption stackFormat = getStringOption(KEY_STACK_FORMAT);
    if (!LibraryChecker.checkClass(CLASS_VISBIO)) {
      stackFormat.removePossible(VIEW_VISBIO);
    }
    if (!LibraryChecker.checkClass(CLASS_IMAGE_5D)) {
      stackFormat.removePossible(VIEW_IMAGE_5D);
    }
    if (!LibraryChecker.checkClass(CLASS_VIEW_5D)) {
      stackFormat.removePossible(VIEW_VIEW_5D);
    }
  }

  // -- ImporterOptions methods - option harvesting --

  /** Parses the plugin argument for parameter values. */
  public void parseArg(String arg) {
    if (arg == null || arg.length() == 0) return;
    if (new Location(arg).exists()) {
      // old style arg: entire argument is a file path

      // this style is used by the HandleExtraFileTypes plugin

      // NB: This functionality must not be removed, or the plugin
      // will stop working correctly with HandleExtraFileTypes.

      setLocation(LOCATION_LOCAL);
      setId(arg);
      setQuiet(true); // suppress obnoxious error messages and such
    }
    else {
      // new style arg: split up similar to a macro options string, but
      // slightly different than macro options, in that boolean arguments
      // must be of the form "key=true" rather than just "key"

      // only the core options are supported for now

      // NB: This functionality enables multiple plugin entries to achieve
      // distinct behavior by calling the LociImporter plugin differently.

      parseOptions(arg);
    }
  }

  /**
   * Displays dialog boxes prompting for additional configuration details.
   *
   * Which dialogs are shown depends on a variety of factors, including the
   * current configuration (i.e., which options are enabled), whether quiet or
   * windowless mode is set, and whether the method is being called from within
   * a macro.
   *
   * After calling this method, derived field values will also be populated.
   *
   * @return true if harvesting went OK, or false if something went wrong
   *   (e.g., the user canceled a dialog box)
   *
   * @see ij.gui.GenericDialog
   */
  public boolean showDialogs() throws FormatException, IOException {
    reader = null;

    if (!promptUpgrade()) return false;

    if (!promptLocation()) return false;
    if (!promptId()) return false;

    reader = new ImporterReader(this);

    if (!promptOptions()) return false;

    // save options as new defaults
    if (!isQuiet()) setFirstTime(false);
    saveOptions();

    IJ.showStatus("Analyzing " + getIdName());

    reader.prepareStuff();

    if (!promptFilePattern()) return false;

    reader.initializeReader();

    if (!promptSeries()) return false;
    if (!promptSwap()) return false;
    if (!promptRange()) return false;
    if (!promptCrop()) return false;

    initializeMetadata();
    computeRangeCounts();

    return true;
  }

  // -- ImporterOptions methods - base options accessors and mutators --

  // autoscale
  public String getAutoscaleInfo() { return getInfo(KEY_AUTOSCALE); }
  public boolean isAutoscale() { return isSet(KEY_AUTOSCALE); }
  public void setAutoscale(boolean b) { setValue(KEY_AUTOSCALE, b); }

  // colorize
  public String getColorizeInfo() { return getInfo(KEY_COLORIZE); }
  public boolean isColorize() { return isSet(KEY_COLORIZE); }
  public void setColorize(boolean b) { setValue(KEY_COLORIZE, b); }

  // concatenate
  public String getConcatenateInfo() { return getInfo(KEY_CONCATENATE); }
  public boolean isConcatenate() { return isSet(KEY_CONCATENATE); }
  public void setConcatenate(boolean b) { setValue(KEY_CONCATENATE, b); }

  // crop
  public String getCropInfo() { return getInfo(KEY_CROP); }
  public boolean doCrop() { return isSet(KEY_CROP); }
  public void setCrop(boolean b) { setValue(KEY_CROP, b); }

  // customColorize
  public String getCustomColorizeInfo() { return getInfo(KEY_CUSTOM_COLORIZE); }
  public boolean isCustomColorize() { return isSet(KEY_CUSTOM_COLORIZE); }
  public void setCustomColorize(boolean b) { setValue(KEY_CUSTOM_COLORIZE, b); }

  // firstTime
  public String getFirstTimeInfo() { return getInfo(KEY_FIRST); }
  public boolean isFirstTime() { return isSet(KEY_FIRST); }
  public void setFirstTime(boolean b) { setValue(KEY_FIRST, b); }

  // forceThumbnails
  public String getForceThumbnailsInfo() { return getInfo(KEY_FORCE_THUMBS); }
  public boolean isForceThumbnails() { return isSet(KEY_FORCE_THUMBS); }
  public void setForceThumbnails(boolean b) { setValue(KEY_FORCE_THUMBS, b); }

  // groupFiles
  public String getGroupFilesInfo() { return getInfo(KEY_GROUP_FILES); }
  public boolean isGroupFiles() { return isSet(KEY_GROUP_FILES); }
  public void setGroupFiles(boolean b) { setValue(KEY_GROUP_FILES, b); }

  // ungroupFiles
  public String getUngroupFilesInfo() { return getInfo(KEY_UNGROUP_FILES); }
  public boolean isUngroupFiles() { return isSet(KEY_UNGROUP_FILES); }
  public void setUngroupFiles(boolean b) { setValue(KEY_UNGROUP_FILES, b); }

  // id
  public String getIdInfo() { return getInfo(KEY_ID); }
  public String getId() { return getValue(KEY_ID); }
  public void setId(String s) { setValue(KEY_ID, s); }

  // location
  public String getLocationInfo() { return getInfo(KEY_LOCATION); }
  public String getLocation() { return getValue(KEY_LOCATION); }
  public String[] getLocations() { return getPossible(KEY_LOCATION); }
  public boolean isLocal() { return LOCATION_LOCAL.equals(getLocation()); }
  public boolean isHTTP() { return LOCATION_HTTP.equals(getLocation()); }
  public boolean isOME() { return LOCATION_OME.equals(getLocation()); }
  public boolean isOMERO() { return LOCATION_OMERO.equals(getLocation()); }
  public void setLocation(String s) { setValue(KEY_LOCATION, s); }

  // mergeChannels
  public String getMergeChannelsInfo() { return getInfo(KEY_MERGE_CHANNELS); }
  public boolean isMergeChannels() { return isSet(KEY_MERGE_CHANNELS); }
  public void setMergeChannels(boolean b) { setValue(KEY_MERGE_CHANNELS, b); }

  // mergeOption
  public String getMergeOptionInfo() { return getInfo(KEY_MERGE_OPTION); }
  public String getMergeOption() { return getValue(KEY_MERGE_OPTION); }
  public void setMergeOption(String s) { setValue(KEY_MERGE_OPTION, s); }

  // openAllSeries
  public String getOpenAllSeriesInfo() { return getInfo(KEY_OPEN_ALL_SERIES); }
  public boolean openAllSeries() { return isSet(KEY_OPEN_ALL_SERIES); }
  public void setOpenAllSeries(boolean b) { setValue(KEY_OPEN_ALL_SERIES, b); }

  // quiet
  public String getQuietInfo() { return getInfo(KEY_QUIET); }
  public boolean isQuiet() { return isSet(KEY_QUIET); }
  public void setQuiet(boolean b) { setValue(KEY_QUIET, b); }

  // record
  public String getRecordInfo() { return getInfo(KEY_RECORD); }
  public boolean isRecord() { return isSet(KEY_RECORD); }
  public void setRecord(boolean b) { setValue(KEY_RECORD, b); }

  // series
  public String getSeriesInfo() { return getInfo(KEY_SERIES); }
  public String getSeries() { return getValue(KEY_SERIES); }
  public void setSeries(String s) { setValue(KEY_SERIES, s); }

  // showMetadata
  public String getShowMetadataInfo() { return getInfo(KEY_SHOW_METADATA); }
  public boolean isShowMetadata() { return isSet(KEY_SHOW_METADATA); }
  public void setShowMetadata(boolean b) { setValue(KEY_SHOW_METADATA, b); }

  // showOMEXML
  public String getShowOMEXMLInfo() { return getInfo(KEY_SHOW_OME_XML); }
  public boolean isShowOMEXML() { return isSet(KEY_SHOW_OME_XML); }
  public void setShowOMEXML(boolean b) { setValue(KEY_SHOW_OME_XML, b); }

  // showROIs
  public String getShowROIsInfo() { return getInfo(KEY_SHOW_ROIS); }
  public boolean showROIs() { return isSet(KEY_SHOW_ROIS); }
  public void setShowROIs(boolean b) { setValue(KEY_SHOW_ROIS, b); }

  // specifyRanges
  public String getSpecifyRangesInfo() { return getInfo(KEY_SPECIFY_RANGES); }
  public boolean isSpecifyRanges() { return isSet(KEY_SPECIFY_RANGES); }
  public void setSpecifyRanges(boolean b) { setValue(KEY_SPECIFY_RANGES, b); }

  // splitFocalPlanes
  public String getSplitFocalPlanesInfo() { return getInfo(KEY_SPLIT_Z); }
  public boolean isSplitFocalPlanes() { return isSet(KEY_SPLIT_Z); }
  public void setSplitFocalPlanes(boolean b) { setValue(KEY_SPLIT_Z, b); }

  // splitTimepoints
  public String getSplitTimepointsInfo() { return getInfo(KEY_SPLIT_T); }
  public boolean isSplitTimepoints() { return isSet(KEY_SPLIT_T); }
  public void setSplitTimepoints(boolean b) { setValue(KEY_SPLIT_T, b); }

  // splitWindows
  public String getSplitChannelsInfo() { return getInfo(KEY_SPLIT_C); }
  public boolean isSplitChannels() { return isSet(KEY_SPLIT_C); }
  public void setSplitChannels(boolean b) { setValue(KEY_SPLIT_C, b); }

  // stackFormat
  public String getStackFormatInfo() { return getInfo(KEY_STACK_FORMAT); }
  public String getStackFormat() { return getValue(KEY_STACK_FORMAT); }
  public String[] getStackFormats() { return getPossible(KEY_STACK_FORMAT); }
  public boolean isViewNone() { return VIEW_NONE.equals(getStackFormat()); }
  public boolean isViewStandard() {
    return VIEW_STANDARD.equals(getStackFormat());
  }
  public boolean isViewHyperstack() {
    return VIEW_HYPERSTACK.equals(getStackFormat());
  }
  public boolean isViewBrowser() {
    return VIEW_BROWSER.equals(getStackFormat());
  }
  public boolean isViewVisBio() { return VIEW_VISBIO.equals(getStackFormat()); }
  public boolean isViewImage5D() {
    return VIEW_IMAGE_5D.equals(getStackFormat());
  }
  public boolean isViewView5D() {
    return VIEW_VIEW_5D.equals(getStackFormat());
  }
  public void setStackFormat(String s) { setValue(KEY_STACK_FORMAT, s); }

  // stackOrder
  public String getStackOrderInfo() { return getInfo(KEY_STACK_ORDER); }
  public String getStackOrder() { return getValue(KEY_STACK_ORDER); }
  public String[] getStackOrders() { return getPossible(KEY_STACK_ORDER); }
  public void setStackOrder(String s) { setValue(KEY_STACK_ORDER, s); }

  // swapDimensions
  public String getSwapDimensionsInfo() { return getInfo(KEY_SWAP_DIMS); }
  public boolean isSwapDimensions() { return isSet(KEY_SWAP_DIMS); }
  public void setSwapDimensions(boolean b) { setValue(KEY_SWAP_DIMS, b); }

  // upgradeCheck
  public String getUpgradeCheckInfo() { return getInfo(KEY_UPGRADE_CHECK); }
  public boolean doUpgradeCheck() { return isSet(KEY_UPGRADE_CHECK); }
  public void setUpgradeCheck(boolean b) { setValue(KEY_UPGRADE_CHECK, b); }

  // virtual
  public String getVirtualInfo() { return getInfo(KEY_VIRTUAL); }
  public boolean isVirtual() { return isSet(KEY_VIRTUAL); }
  public void setVirtual(boolean b) { setValue(KEY_VIRTUAL, b); }

  // windowless
  public String getWindowlessInfo() { return getInfo(KEY_WINDOWLESS); }
  public boolean isWindowless() {
    if (reader != null && reader.isWindowless()) return true;
    return isSet(KEY_WINDOWLESS);
  }
  public void setWindowless(boolean b) { setValue(KEY_WINDOWLESS, b); }

  // -- ImporterOptions methods - secondary options accessors and mutators --

  // series options
  public boolean isSeriesOn(int s) { return get(seriesOn, s); }
  public void setSeriesOn(int s, boolean value) {
    set(seriesOn, s, value, false);
  }

  // TODO - swap options

  // range options
  public int getCBegin(int s) { return get(cBegin, s); }
  public void setCBegin(int s, int value) { set(cBegin, s, value, 0); }
  public int getCEnd(int s) { return get(cEnd, s); }
  public void setCEnd(int s, int value) { set(cEnd, s, value, 0); }
  public int getCStep(int s) { return get(cStep, s); }
  public void setCStep(int s, int value) { set(cStep, s, value, 0); }

  public int getZBegin(int s) { return get(zBegin, s); }
  public void setZBegin(int s, int value) { set(zBegin, s, value, 0); }
  public int getZEnd(int s) { return get(zEnd, s); }
  public void setZEnd(int s, int value) { set(zEnd, s, value, 0); }
  public int getZStep(int s) { return get(zStep, s); }
  public void setZStep(int s, int value) { set(zStep, s, value, 0); }

  public int getTBegin(int s) { return get(tBegin, s); }
  public void setTBegin(int s, int value) { set(tBegin, s, value, 0); }
  public int getTEnd(int s) { return get(tEnd, s); }
  public void setTEnd(int s, int value) { set(tEnd, s, value, 0); }
  public int getTStep(int s) { return get(tStep, s); }
  public void setTStep(int s, int value) { set(tStep, s, value, 0); }

  // crop options
  public Region getCropRegion(int s) { return get(cropRegion, s); }
  public void setCropRegion(int s, Region r) { set(cropRegion, s, r, null); }

  // -- ImporterOptions methods - derived values accessors --

  public String getIdName() { return reader.idName; }
  public Location getIdLocation() { return reader.idLoc; }
  public String getCurrentFile() { return reader.currentFile; }
  public ImageProcessorReader getReader() { return reader.r; }
  public IMetadata getOMEMetadata() { return reader.meta; }
  public ImporterMetadata getOriginalMetadata() { return metadata; }
  public int getSeriesCount() { return reader.r.getSeriesCount(); }

  // series options
  public int getCCount(int s) { return cCount[s]; }
  public int getZCount(int s) { return zCount[s]; }
  public int getTCount(int s) { return tCount[s]; }

  // -- Helper methods - dialog prompts --

  private boolean promptUpgrade() {
    UpgradeDialog dialog = new UpgradeDialog(this);
    return dialog.showDialog() == OptionsDialog.STATUS_OK;
  }

  private boolean promptLocation() {
    LocationDialog dialog = new LocationDialog(this);
    return dialog.showDialog() == OptionsDialog.STATUS_OK;
  }

  private boolean promptId() {
    IdDialog dialog = new IdDialog(this);
    return dialog.showDialog() == OptionsDialog.STATUS_OK;
  }

  private boolean promptOptions() {
    if (isWindowless()) return true;

    ImporterDialog dialog = new ImporterDialog(this);
    return dialog.showDialog() == OptionsDialog.STATUS_OK;
  }

  /** Prompts for the file pattern, if necessary. May override id value. */
  private boolean promptFilePattern() {
    if (!isGroupFiles()) {
      BF.debug("no need to prompt for file pattern");
      return true;
    }
    BF.debug("prompt for the file pattern");

    FilePatternDialog dialog = new FilePatternDialog(this);
    if (dialog.showDialog() != OptionsDialog.STATUS_OK) return false;

    String id = getId();
    if (id == null) id = reader.currentFile;
    FilePattern fp = new FilePattern(id);
    if (!fp.isValid()) id = reader.currentFile;
    setId(id); // overwrite base filename with file pattern
    return true;
  }

  /** Prompts for which series to import, if necessary. */
  private boolean promptSeries() {
    // initialize series-related derived values
    setSeriesOn(0, true);

    // build descriptive label for each series
    int seriesCount = getSeriesCount();
    seriesLabels = new String[seriesCount];
    for (int i=0; i<seriesCount; i++) {
      reader.r.setSeries(i);
      StringBuffer sb = new StringBuffer();
      sb.append("Series_");
      sb.append((i + 1));
      sb.append(": ");
      String name = getOMEMetadata().getImageName(i);
      if (name != null && name.length() > 0) {
        sb.append(name);
        sb.append(": ");
      }
      sb.append(reader.r.getSizeX());
      sb.append(" x ");
      sb.append(reader.r.getSizeY());
      sb.append("; ");
      sb.append(reader.r.getImageCount());
      sb.append(" plane");
      if (reader.r.getImageCount() > 1) {
        sb.append("s");
        if (reader.r.isOrderCertain()) {
          sb.append(" (");
          boolean first = true;
          if (reader.r.getEffectiveSizeC() > 1) {
            sb.append(reader.r.getEffectiveSizeC());
            sb.append("C");
            first = false;
          }
          if (reader.r.getSizeZ() > 1) {
            if (!first) sb.append(" x ");
            sb.append(reader.r.getSizeZ());
            sb.append("Z");
            first = false;
          }
          if (reader.r.getSizeT() > 1) {
            if (!first) sb.append(" x ");
            sb.append(reader.r.getSizeT());
            sb.append("T");
            first = false;
          }
          sb.append(")");
        }
      }
      seriesLabels[i] = sb.toString();
      //seriesLabels[i] = seriesLabels[i].replaceAll(" ", "_");
    }

    if (seriesCount > 1 && !openAllSeries() && !isViewNone()) {
      BF.debug("prompt for which series to import");
      SeriesDialog dialog = new SeriesDialog(this,
        reader.r, seriesLabels);
      if (dialog.showDialog() != OptionsDialog.STATUS_OK) return false;
    }
    else BF.debug("no need to prompt for series");

    if (openAllSeries() || isViewNone()) {
      for (int s=0; s<getSeriesCount(); s++) setSeriesOn(s, true);
    }
    return true;
  }

  /** Prompts for dimension swapping parameters, if necessary. */
  private boolean promptSwap() {
    if (!isSwapDimensions()) {
      BF.debug("no need to prompt for dimension swapping");
      return true;
    }
    BF.debug("prompt for dimension swapping parameters");

    SwapDialog dialog = new SwapDialog(this, reader.virtualReader);
    return dialog.showDialog() == OptionsDialog.STATUS_OK;
  }

  /** Prompts for the range of planes to import, if necessary. */
  private boolean promptRange() {
    // initialize range-related secondary values
    int seriesCount = getSeriesCount();

    for (int s=0; s<seriesCount; s++) {
      reader.r.setSeries(s);
      setCBegin(s, 0);
      setZBegin(s, 0);
      setTBegin(s, 0);
      setCEnd(s, reader.r.getEffectiveSizeC() - 1);
      setZEnd(s, reader.r.getSizeZ() - 1);
      setTEnd(s, reader.r.getSizeT() - 1);
      setCStep(s, 1);
      setZStep(s, 1);
      setTStep(s, 1);
    }

    if (!isSpecifyRanges()) {
      BF.debug("open all planes");
      return true;
    }
    boolean needRange = false;
    for (int s=0; s<seriesCount; s++) {
      if (isSeriesOn(s) && reader.r.getImageCount() > 1) needRange = true;
    }
    if (!needRange) {
      BF.debug("no need to prompt for planar ranges");
      return true;
    }
    BF.debug("prompt for planar ranges");
    IJ.showStatus("");

    RangeDialog dialog = new RangeDialog(this, reader.r, seriesLabels);
    return dialog.showDialog() == OptionsDialog.STATUS_OK;
  }

  /** Prompts for cropping details, if necessary. */
  private boolean promptCrop() {
    // initialize crop-related secondary values
    int seriesCount = getSeriesCount();
    for (int s=0; s<seriesCount; s++) {
      setCropRegion(s, new Region());
    }

    if (!doCrop()) {
      BF.debug("no need to prompt for cropping region");
      return true;
    }
    BF.debug("prompt for cropping region");

    CropDialog dialog = new CropDialog(this, reader.r, seriesLabels);
    return dialog.showDialog() == OptionsDialog.STATUS_OK;
  }

  // -- Helper methods - derived value computation --

  /** Initializes the ImporterMetadata derived value. */
  private void initializeMetadata() {
    // only prepend a series name prefix to the metadata keys if multiple
    // series are being opened
    int seriesCount = getSeriesCount();
    int numEnabled = 0;
    for (int s=0; s<seriesCount; s++) {
      if (isSeriesOn(s)) numEnabled++;
    }
    metadata = new ImporterMetadata(reader.r, this, numEnabled > 1);
  }

  /** Initializes the cCount, zCount and tCount derived values. */
  private void computeRangeCounts() {
    int seriesCount = getSeriesCount();
    cCount = new int[seriesCount];
    zCount = new int[seriesCount];
    tCount = new int[seriesCount];
    for (int s=0; s<seriesCount; s++) {
      if (!isSeriesOn(s)) cCount[s] = zCount[s] = tCount[s] = 0;
      else {
        if (isMergeChannels()) cCount[s] = 1;
        else {
          cCount[s] = (getCEnd(s) - getCBegin(s) + getCStep(s)) / getCStep(s);
        }
        zCount[s] = (getZEnd(s) - getZBegin(s) + getZStep(s)) / getZStep(s);
        tCount[s] = (getTEnd(s) - getTBegin(s) + getTStep(s)) / getTStep(s);
      }
    }
  }
  
  // -- Helper methods - miscellaneous --
  
  private <T extends Object> void set(List<T> list,
    int index, T value, T defaultValue)
  {
    while (list.size() <= index) list.add(defaultValue);
    list.set(index, value);
  }

  private <T extends Object> T get(List<T> list, int index) {
    if (list.size() <= index) return null;
    return list.get(index);
  }

}
