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

package loci.plugins.importer;

import ij.IJ;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;

import loci.common.Location;
import loci.common.ReflectException;
import loci.common.ReflectedUniverse;
import loci.formats.ChannelSeparator;
import loci.formats.FilePattern;
import loci.formats.FileStitcher;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.plugins.prefs.OptionsDialog;
import loci.plugins.prefs.OptionsList;
import loci.plugins.prefs.StringOption;
import loci.plugins.util.BF;
import loci.plugins.util.IJStatusEchoer;
import loci.plugins.util.ImagePlusReader;
import loci.plugins.util.LibraryChecker;
import loci.plugins.util.LociPrefs;
import loci.plugins.util.VirtualReader;
import loci.plugins.util.WindowTools;

/**
 * Helper class for managing Bio-Formats Importer options.
 * Gets parameter values through a variety of means, including
 * preferences from IJ_Prefs.txt, plugin argument string, macro options,
 * and user input from dialog boxes.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/importer/ImporterOptions.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/importer/ImporterOptions.java">SVN</a></dd></dl>
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

  protected ImagePlusReader r;
  protected String idName;
  protected Location idLoc;
  protected IMetadata meta;
  protected String currentFile;

  // series options
  protected boolean[] series;

  // range options
  protected int[] cBegin, cEnd, cStep, cCount;
  protected int[] zBegin, zEnd, zStep, zCount;
  protected int[] tBegin, tEnd, tStep, tCount;

  // crop options
  protected Rectangle[] cropRegion;

  // -- Fields - internal --

  private IFormatReader baseReader;
  private VirtualReader virtualReader;
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
    baseReader = null;
    idName = null;
    idLoc = null;

    if (!promptLocation()) return false;
    if (!promptId()) return false;

    computeNameAndLocation();
    createBaseReader();

    if (!promptOptions()) return false;

    // save options as new defaults
    if (!isQuiet()) setFirstTime(false);
    saveOptions();

    IJ.showStatus("Analyzing " + getIdName());

    baseReader.setMetadataFiltered(true);
    baseReader.setOriginalMetadataPopulated(true);
    baseReader.setGroupFiles(!isUngroupFiles());
    baseReader.setId(getId());

    currentFile = baseReader.getCurrentFile();

    if (!promptFilePattern()) return false;

    initializeReader();

    if (!promptSeries()) return false;
    if (!promptSwap()) return false;
    if (!promptRange()) return false;
    if (!promptCrop()) return false;

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
    if (baseReader != null && LociPrefs.isWindowless(baseReader)) return true;
    return isSet(KEY_WINDOWLESS);
  }
  public void setWindowless(boolean b) { setValue(KEY_WINDOWLESS, b); }

  // -- ImporterOptions methods - derived values accessors --

  public String getIdName() { return idName; }
  public Location getIdLocation() { return idLoc; }
  public ImagePlusReader getReader() { return r; }
  public IMetadata getMetadata() { return meta; }
  public String getCurrentFile() { return currentFile; }

  // series options
  public int getCBegin(int s) { return cBegin[s]; }
  public int getCEnd(int s) { return cEnd[s]; }
  public int getCStep(int s) { return cStep[s]; }
  public int getCCount(int s) { return cCount[s]; }
  public int getZBegin(int s) { return zBegin[s]; }
  public int getZEnd(int s) { return zEnd[s]; }
  public int getZStep(int s) { return zStep[s]; }
  public int getZCount(int s) { return zCount[s]; }
  public int getTBegin(int s) { return tBegin[s]; }
  public int getTEnd(int s) { return tEnd[s]; }
  public int getTStep(int s) { return tStep[s]; }
  public int getTCount(int s) { return tCount[s]; }
  public boolean isSeriesOn(int s) { return series[s]; }

  // crop options
  public Rectangle getCropRegion(int s) { return cropRegion[s]; }

  // -- Helper methods --

  /** Initializes the idName and idLoc derived values. */
  private void computeNameAndLocation() {
    String id = getId();

    idLoc = null;
    idName = id;
    if (isLocal()) {
      idLoc = new Location(id);
      idName = idLoc.getName();
    }
    else if (isOME() || isOMERO()) {
      // NB: strip out username and password when opening from OME/OMERO
      StringTokenizer st = new StringTokenizer(id, "?&");
      StringBuffer idBuf = new StringBuffer();
      int tokenCount = 0;
      while (st.hasMoreTokens()) {
        String token = st.nextToken();
        if (token.startsWith("username=") || token.startsWith("password=")) {
          continue;
        }
        if (tokenCount == 1) idBuf.append("?");
        else if (tokenCount > 1) idBuf.append("&");
        idBuf.append(token);
        tokenCount++;
      }
      idName = idBuf.toString();
    }
  }

  /**
   * Initializes an {@link loci.formats.IFormatReader}
   * according to the current configuration.
   */
  private void createBaseReader() {
    if (isLocal() || isHTTP()) {
      if (!isQuiet()) IJ.showStatus("Identifying " + idName);
      ImageReader reader = ImagePlusReader.makeImageReader();
      try { baseReader = reader.getReader(getId()); }
      catch (FormatException exc) {
        WindowTools.reportException(exc, isQuiet(),
          "Sorry, there was an error reading the file.");
        return;
      }
      catch (IOException exc) {
        WindowTools.reportException(exc, isQuiet(),
          "Sorry, there was a I/O problem reading the file.");
        return;
      }
    }
    else if (isOMERO()) {
      // NB: avoid dependencies on optional loci.ome.io package
      try {
        ReflectedUniverse ru = new ReflectedUniverse();
        ru.exec("import loci.ome.io.OMEROReader");
        baseReader = (IFormatReader) ru.exec("new OMEROReader()");
      }
      catch (ReflectException exc) {
        WindowTools.reportException(exc, isQuiet(),
          "Sorry, there was a problem constructing the OMERO I/O engine");
        return;
      }
    }
    else if (isOME()) {
      // NB: avoid dependencies on optional loci.ome.io package
      try {
        ReflectedUniverse ru = new ReflectedUniverse();
        ru.exec("import loci.ome.io.OMEReader");
        baseReader = (IFormatReader) ru.exec("new OMEReader()");
      }
      catch (ReflectException exc) {
        WindowTools.reportException(exc, isQuiet(),
          "Sorry, there was a problem constructing the OME I/O engine");
        return;
      }
    }
    else {
      WindowTools.reportException(null, isQuiet(),
        "Sorry, there has been an internal error: unknown data source");
    }
    meta = MetadataTools.createOMEXMLMetadata();
    baseReader.setMetadataStore(meta);

    if (!isQuiet()) IJ.showStatus("");
    baseReader.addStatusListener(new IJStatusEchoer());
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
  private boolean promptFilePattern() throws FormatException, IOException {
    if (!isGroupFiles()) {
      BF.debug("no need to prompt for file pattern");
      return true;
    }
    BF.debug("prompt for the file pattern");

    FilePatternDialog dialog = new FilePatternDialog(this);
    if (dialog.showDialog() != OptionsDialog.STATUS_OK) return false;

    String id = getId();
    if (id == null) id = currentFile;
    FilePattern fp = new FilePattern(id);
    if (!fp.isValid()) id = currentFile;
    setId(id); // CTR CHECK -- probably the wrong way to do this
    return true;
  }

  /** Initializes the ImagePlusReader derived value. */
  private void initializeReader() throws FormatException, IOException {
    if (isGroupFiles()) baseReader = new FileStitcher(baseReader, true);
    if (isVirtual() || !isMergeChannels() ||
      FormatTools.getBytesPerPixel(baseReader.getPixelType()) != 1)
    {
      baseReader = new ChannelSeparator(baseReader);
    }
    virtualReader = new VirtualReader(baseReader);
    r = new ImagePlusReader(virtualReader);
    r.setId(getId());
  }

  /** Prompts for which series to import, if necessary. */
  private boolean promptSeries() throws FormatException, IOException {
    // initialize series-related derived values
    series = new boolean[r.getSeriesCount()];
    series[0] = true;

    // build descriptive label for each series
    int seriesCount = r.getSeriesCount();
    seriesLabels = new String[seriesCount];
    for (int i=0; i<seriesCount; i++) {
      r.setSeries(i);
      StringBuffer sb = new StringBuffer();
      sb.append("Series_");
      sb.append((i + 1));
      sb.append(": ");
      String name = getMetadata().getImageName(i);
      if (name != null && name.length() > 0) {
        sb.append(name);
        sb.append(": ");
      }
      sb.append(r.getSizeX());
      sb.append(" x ");
      sb.append(r.getSizeY());
      sb.append("; ");
      sb.append(r.getImageCount());
      sb.append(" plane");
      if (r.getImageCount() > 1) {
        sb.append("s");
        if (r.isOrderCertain()) {
          sb.append(" (");
          boolean first = true;
          if (r.getEffectiveSizeC() > 1) {
            sb.append(r.getEffectiveSizeC());
            sb.append("C");
            first = false;
          }
          if (r.getSizeZ() > 1) {
            if (!first) sb.append(" x ");
            sb.append(r.getSizeZ());
            sb.append("Z");
            first = false;
          }
          if (r.getSizeT() > 1) {
            if (!first) sb.append(" x ");
            sb.append(r.getSizeT());
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
      SeriesDialog dialog = new SeriesDialog(this, r, seriesLabels, series);
      if (dialog.showDialog() != OptionsDialog.STATUS_OK) return false;
    }
    else BF.debug("no need to prompt for series");

    if (openAllSeries() || isViewNone()) {
      Arrays.fill(series, true);
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

    SwapDialog dialog = new SwapDialog(this, virtualReader, series);
    return dialog.showDialog() == OptionsDialog.STATUS_OK;
  }

  /** Prompts for the range of planes to import, if necessary. */
  private boolean promptRange() {
    // initialize range-related derived values
    int seriesCount = r.getSeriesCount();
    cBegin = new int[seriesCount];
    cEnd = new int[seriesCount];
    cStep = new int[seriesCount];
    zBegin = new int[seriesCount];
    zEnd = new int[seriesCount];
    zStep = new int[seriesCount];
    tBegin = new int[seriesCount];
    tEnd = new int[seriesCount];
    tStep = new int[seriesCount];

    for (int i=0; i<seriesCount; i++) {
      r.setSeries(i);
      cBegin[i] = zBegin[i] = tBegin[i] = 0;
      cEnd[i] = r.getEffectiveSizeC() - 1;
      zEnd[i] = r.getSizeZ() - 1;
      tEnd[i] = r.getSizeT() - 1;
      cStep[i] = zStep[i] = tStep[i] = 1;
    }


    if (!isSpecifyRanges()) {
      BF.debug("open all planes");
      return true;
    }
    boolean needRange = false;
    for (int i=0; i<seriesCount; i++) {
      if (series[i] && r.getImageCount() > 1) needRange = true;
    }
    if (!needRange) {
      BF.debug("no need to prompt for planar ranges");
      return true;
    }
    BF.debug("prompt for planar ranges");
    IJ.showStatus("");

    RangeDialog dialog = new RangeDialog(this,
      r, series, seriesLabels, cBegin, cEnd, cStep,
      zBegin, zEnd, zStep, tBegin, tEnd, tStep);
    return dialog.showDialog() == OptionsDialog.STATUS_OK;
  }

  /** Prompts for cropping details, if necessary. */
  private boolean promptCrop() {
    // initialize crop-related derived values
    cropRegion = new Rectangle[r.getSeriesCount()];
    for (int i=0; i<cropRegion.length; i++) {
      if (series[i] && doCrop()) cropRegion[i] = new Rectangle();
    }

    if (!doCrop()) {
      BF.debug("no need to prompt for cropping region");
      return true;
    }
    BF.debug("prompt for cropping region");

    CropDialog dialog = new CropDialog(this,
      r, seriesLabels, series, cropRegion);
    return dialog.showDialog() == OptionsDialog.STATUS_OK;
  }

  /** Initializes the cCount, zCount and tCount derived values. */
  private void computeRangeCounts() {
    int seriesCount = r.getSeriesCount();
    cCount = new int[seriesCount];
    zCount = new int[seriesCount];
    tCount = new int[seriesCount];
    for (int i=0; i<seriesCount; i++) {
      if (!series[i]) cCount[i] = zCount[i] = tCount[i] = 0;
      else {
        if (isMergeChannels()) cCount[i] = 1;
        else cCount[i] = (cEnd[i] - cBegin[i] + cStep[i]) / cStep[i];
        zCount[i] = (zEnd[i] - zBegin[i] + zStep[i]) / zStep[i];
        tCount[i] = (tEnd[i] - tBegin[i] + tStep[i]) / tStep[i];
      }
    }
  }

}
