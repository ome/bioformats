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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import loci.common.Location;
import loci.common.Region;
import loci.plugins.prefs.OptionsList;
import loci.plugins.prefs.StringOption;
import loci.plugins.util.LibraryChecker;

/**
 * Helper class for managing Bio-Formats Importer options.
 * Gets default parameter values from IJ_Prefs.txt.
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
  //public static final String LOCATION_OMERO = "OMERO server";

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

  // -- Fields -- secondary values --

  // series options
  private List<Boolean> seriesOn = new ArrayList<Boolean>();

  // swap options
  private String inputOrder;

  // range options
  private List<Integer> cBegin = new ArrayList<Integer>();
  private List<Integer> cEnd = new ArrayList<Integer>();
  private List<Integer> cStep = new ArrayList<Integer>();
  private List<Integer> zBegin = new ArrayList<Integer>();
  private List<Integer> zEnd = new ArrayList<Integer>();
  private List<Integer> zStep = new ArrayList<Integer>();
  private List<Integer> tBegin = new ArrayList<Integer>();
  private List<Integer> tEnd = new ArrayList<Integer>();
  private List<Integer> tStep = new ArrayList<Integer>();

  // crop options
  private List<Region> cropRegion = new ArrayList<Region>();

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
  //public boolean isOMERO() { return LOCATION_OMERO.equals(getLocation()); }
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
  public boolean isWindowless() { return isSet(KEY_WINDOWLESS); }
  public void setWindowless(boolean b) { setValue(KEY_WINDOWLESS, b); }

  // -- ImporterOptions methods - secondary options accessors and mutators --

  // series options
  public boolean isSeriesOn(int s) {
    if (openAllSeries() || isViewNone()) return true;
    return get(seriesOn, s, s == 0);
  }
  public void setSeriesOn(int s, boolean value) {
    set(seriesOn, s, value, false);
  }

  // swap options
  public String getInputOrder() { return inputOrder; }
  public void setInputOrder(String dimOrder) { inputOrder = dimOrder; }

  // range options
  public int getCBegin(int s) { return get(cBegin, s, 0); }
  public void setCBegin(int s, int value) { set(cBegin, s, value, 0); }
  public int getCEnd(int s) { return get(cEnd, s, -1); }
  public void setCEnd(int s, int value) { set(cEnd, s, value, -1); }
  public int getCStep(int s) { return get(cStep, s, 1); }
  public void setCStep(int s, int value) { set(cStep, s, value, 1); }
  public int getCCount(int s) {
    if (!isSeriesOn(s)) return 0;
    if (isMergeChannels()) return 1;
    return (getCEnd(s) - getCBegin(s) + getCStep(s)) / getCStep(s);
  }

  public int getZBegin(int s) { return get(zBegin, s, 0); }
  public void setZBegin(int s, int value) { set(zBegin, s, value, 0); }
  public int getZEnd(int s) { return get(zEnd, s, -1); }
  public void setZEnd(int s, int value) { set(zEnd, s, value, -1); }
  public int getZStep(int s) { return get(zStep, s, 1); }
  public void setZStep(int s, int value) { set(zStep, s, value, 1); }
  public int getZCount(int s) {
    if (!isSeriesOn(s)) return 0;
    return (getZEnd(s) - getZBegin(s) + getZStep(s)) / getZStep(s);
  }

  public int getTBegin(int s) { return get(tBegin, s, 0); }
  public void setTBegin(int s, int value) { set(tBegin, s, value, 0); }
  public int getTEnd(int s) { return get(tEnd, s, -1); }
  public void setTEnd(int s, int value) { set(tEnd, s, value, -1); }
  public int getTStep(int s) { return get(tStep, s, 1); }
  public void setTStep(int s, int value) { set(tStep, s, value, 1); }
  public int getTCount(int s) {
    if (!isSeriesOn(s)) return 0;
    return (getTEnd(s) - getTBegin(s) + getTStep(s)) / getTStep(s);
  }

  // crop options
  public Region getCropRegion(int s) { return get(cropRegion, s, null); }
  public void setCropRegion(int s, Region r) { set(cropRegion, s, r, null); }

  // -- Helper methods - miscellaneous --

  private <T extends Object> void set(List<T> list,
    int index, T value, T fillValue)
  {
    while (list.size() <= index) list.add(fillValue);
    list.set(index, value);
  }

  private <T extends Object> T get(List<T> list, int index, T defaultValue) {
    if (list.size() <= index) return defaultValue;
    return list.get(index);
  }

}
