//
// Importer.java
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
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.io.FileInfo;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.awt.Rectangle;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import loci.common.Location;
import loci.common.ReflectException;
import loci.common.ReflectedUniverse;
import loci.formats.ChannelMerger;
import loci.formats.ChannelSeparator;
import loci.formats.DimensionSwapper;
import loci.formats.FilePattern;
import loci.formats.FileStitcher;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.StatusEvent;
import loci.formats.StatusListener;
import loci.formats.gui.XMLWindow;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataRetrieve;
import loci.plugins.Colorizer;
import loci.plugins.LociImporter;
import loci.plugins.prefs.OptionsDialog;
import loci.plugins.util.BFVirtualStack;
import loci.plugins.util.DataBrowser;
import loci.plugins.util.ImagePlusReader;
import loci.plugins.util.ImagePlusTools;
import loci.plugins.util.LociPrefs;
import loci.plugins.util.ROIHandler;
import loci.plugins.util.SearchableWindow;
import loci.plugins.util.VirtualImagePlus;
import loci.plugins.util.WindowTools;

/**
 * Core logic for the Bio-Formats Importer ImageJ plugin.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/importer/Importer.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/importer/Importer.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 */
public class Importer {

  // -- Fields --

  /**
   * A handle to the plugin wrapper, for toggling
   * the canceled and success flags.
   */
  private LociImporter plugin;

  private Vector imps = new Vector();
  private String stackOrder = null;

  private IndexColorModel[] colorModels;

  // -- Constructor --

  public Importer(LociImporter plugin) {
    this.plugin = plugin;
  }

  // -- Importer API methods --

  /** Executes the plugin. */
  public void run(String arg) {

    // -- Step 0: parse core options --

    debug("parse core options");

    ImporterOptions options = null;
    try {
      options = new ImporterOptions();
    }
    catch (IOException exc) {
      WindowTools.reportException(exc);
    }
    options.loadOptions();
    options.parseArg(arg);

    // -- Step 1: check if new version is available --

    debug("check if new version is available");

    UpgradeDialog upgradeDialog = new UpgradeDialog(options);
    int status = upgradeDialog.showDialog();
    if (!statusOk(status)) return;

    // -- Step 2: construct reader and check id --

    debug("construct reader and check id");

    LocationDialog locationDialog = new LocationDialog(options);
    status = locationDialog.showDialog();
    if (!statusOk(status)) return;

    IdDialog idDialog = new IdDialog(options);
    status = idDialog.showDialog();
    if (!statusOk(status)) return;

    String id = options.getId();
    boolean quiet = options.isQuiet();

    Location idLoc = null;
    String idName = id;
    if (options.isLocal()) {
      idLoc = new Location(id);
      idName = idLoc.getName();
    }
    else if (options.isOME() || options.isOMERO()) {
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

    IFormatReader base = null;
    if (options.isLocal() || options.isHTTP()) {
      IJ.showStatus("Identifying " + idName);
      ImageReader reader = ImagePlusReader.makeImageReader();
      try { base = reader.getReader(id); }
      catch (FormatException exc) {
        WindowTools.reportException(exc, quiet,
          "Sorry, there was an error reading the file.");
        return;
      }
      catch (IOException exc) {
        WindowTools.reportException(exc, quiet,
          "Sorry, there was a I/O problem reading the file.");
        return;
      }
    }
    else if (options.isOMERO()) {
      // NB: avoid dependencies on optional loci.ome.io package
      try {
        ReflectedUniverse ru = new ReflectedUniverse();
        ru.exec("import loci.ome.io.OMEROReader");
        base = (IFormatReader) ru.exec("new OMEROReader()");
      }
      catch (ReflectException exc) {
        WindowTools.reportException(exc, options.isQuiet(),
          "Sorry, there was a problem constructing the OMERO I/O engine");
        return;
      }
    }
    else if (options.isOME()) {
      // NB: avoid dependencies on optional loci.ome.io package
      try {
        ReflectedUniverse ru = new ReflectedUniverse();
        ru.exec("import loci.ome.io.OMEReader");
        base = (IFormatReader) ru.exec("new OMEReader()");
      }
      catch (ReflectException exc) {
        WindowTools.reportException(exc, options.isQuiet(),
          "Sorry, there was a problem constructing the OME I/O engine");
        return;
      }
    }
    else {
      WindowTools.reportException(null, options.isQuiet(),
        "Sorry, there has been an internal error: unknown data source");
    }
    IMetadata omexmlMeta = MetadataTools.createOMEXMLMetadata();
    base.setMetadataStore(omexmlMeta);

    IJ.showStatus("");
    base.addStatusListener(new StatusEchoer());

    // -- Step 3: get parameter values --

    debug("get parameter values");

    boolean windowless = options.isWindowless() || LociPrefs.isWindowless(base);
    if (!windowless) {
      ImporterDialog importerDialog = new ImporterDialog(options);
      status = importerDialog.showDialog();
    }
    if (!statusOk(status)) return;

    boolean mergeChannels = options.isMergeChannels();
    boolean colorize = options.isColorize();
    boolean showMetadata = options.isShowMetadata();
    boolean showOMEXML = options.isShowOMEXML();
    boolean groupFiles = options.isGroupFiles();
    boolean concatenate = options.isConcatenate();
    boolean specifyRanges = options.isSpecifyRanges();
    boolean cropOnImport = options.doCrop();
    boolean swapDimensions = options.isSwapDimensions();
    quiet = options.isQuiet();

    // save options as new defaults
    if (!quiet) options.setFirstTime(false);
    options.saveOptions();

    // -- Step 4: analyze and read from data source --

    debug("analyze and read from data source");

    IJ.showStatus("Analyzing " + idName);

    try {
      base.setMetadataFiltered(true);
      base.setNormalized(true);
      base.setId(id);

      int pixelType = base.getPixelType();
      String currentFile = base.getCurrentFile();

      // -- Step 4a: prompt for the file pattern, if necessary --

      if (groupFiles) {
        debug("prompt for the file pattern");
        FilePatternDialog filePatternDialog = new FilePatternDialog(options);
        status = filePatternDialog.showDialog();
        if (!statusOk(status)) return;
        id = options.getId();
        if (id == null) id = currentFile;
      }
      else debug("no need to prompt for file pattern");

      if (groupFiles) base = new FileStitcher(base, true);
      // NB: VirtualReader extends DimensionSwapper
      VirtualReader virtualReader =
        new VirtualReader(new ChannelSeparator(base));
      ImagePlusReader r = new ImagePlusReader(virtualReader);
      r.setId(id);

      // -- Step 4b: prompt for which series to import, if necessary --

      // populate series-related variables
      int seriesCount = r.getSeriesCount();
      int[] num = new int[seriesCount];
      int[] sizeC = new int[seriesCount];
      int[] sizeZ = new int[seriesCount];
      int[] sizeT = new int[seriesCount];
      boolean[] certain = new boolean[seriesCount];
      int[] cBegin = new int[seriesCount];
      int[] cEnd = new int[seriesCount];
      int[] cStep = new int[seriesCount];
      int[] zBegin = new int[seriesCount];
      int[] zEnd = new int[seriesCount];
      int[] zStep = new int[seriesCount];
      int[] tBegin = new int[seriesCount];
      int[] tEnd = new int[seriesCount];
      int[] tStep = new int[seriesCount];
      boolean[] series = new boolean[seriesCount];
      for (int i=0; i<seriesCount; i++) {
        r.setSeries(i);
        num[i] = r.getImageCount();
        sizeC[i] = r.getEffectiveSizeC();
        sizeZ[i] = r.getSizeZ();
        sizeT[i] = r.getSizeT();
        certain[i] = r.isOrderCertain();
        cBegin[i] = zBegin[i] = tBegin[i] = 0;
        //cEnd[i] = certain[i] ? sizeC[i] - 1 : num[i] - 1;
        cEnd[i] = sizeC[i] - 1;
        zEnd[i] = sizeZ[i] - 1;
        tEnd[i] = sizeT[i] - 1;
        cStep[i] = zStep[i] = tStep[i] = 1;
      }
      series[0] = true;

      // build descriptive label for each series
      String[] seriesLabels = new String[seriesCount];
      for (int i=0; i<seriesCount; i++) {
        r.setSeries(i);
        StringBuffer sb = new StringBuffer();
        String name = omexmlMeta.getImageName(i);
        if (name != null && name.length() > 0) {
          sb.append(name);
          sb.append(": ");
        }
        sb.append(r.getSizeX());
        sb.append(" x ");
        sb.append(r.getSizeY());
        sb.append("; ");
        sb.append(num[i]);
        sb.append(" plane");
        if (num[i] > 1) {
          sb.append("s");
          if (certain[i]) {
            sb.append(" (");
            boolean first = true;
            if (sizeC[i] > 1) {
              sb.append(sizeC[i]);
              sb.append("C");
              first = false;
            }
            if (sizeZ[i] > 1) {
              if (!first) sb.append(" x ");
              sb.append(sizeZ[i]);
              sb.append("Z");
              first = false;
            }
            if (sizeT[i] > 1) {
              if (!first) sb.append(" x ");
              sb.append(sizeT[i]);
              sb.append("T");
              first = false;
            }
            sb.append(")");
          }
        }
        seriesLabels[i] = sb.toString();
        seriesLabels[i] = seriesLabels[i].replaceAll(" ", "_");
      }

      if (seriesCount > 1 && !options.openAllSeries() && !options.isViewNone())
      {
        debug("prompt for which series to import");
        SeriesDialog seriesDialog = new SeriesDialog(options,
          r, seriesLabels, series);
        status = seriesDialog.showDialog();
        if (!statusOk(status)) return;
      }
      else debug("no need to prompt for series");

      if (options.openAllSeries() || options.isViewNone()) {
        Arrays.fill(series, true);
      }

      // -- Step 4c: prompt for dimension swapping parameters, if necessary --

      if (swapDimensions) {
        debug("prompt for dimension swapping parameters");

        SwapDialog swapDialog = new SwapDialog(options, virtualReader, series);
        status = swapDialog.showDialog();
        if (!statusOk(status)) return;

        for (int i=0; i<seriesCount; i++) {
          r.setSeries(i);
          num[i] = r.getImageCount();
          sizeC[i] = r.getEffectiveSizeC();
          sizeZ[i] = r.getSizeZ();
          sizeT[i] = r.getSizeT();
          certain[i] = r.isOrderCertain();
          cBegin[i] = zBegin[i] = tBegin[i] = 0;
          cEnd[i] = sizeC[i] - 1;
          zEnd[i] = sizeZ[i] - 1;
          tEnd[i] = sizeT[i] - 1;
          cStep[i] = zStep[i] = tStep[i] = 1;
        }
      }
      else debug("no need to prompt for dimension swapping");

      // -- Step 4d: prompt for the range of planes to import, if necessary --

      if (specifyRanges) {
        boolean needRange = false;
        for (int i=0; i<seriesCount; i++) {
          if (series[i] && num[i] > 1) needRange = true;
        }
        if (needRange) {
          debug("prompt for planar ranges");
          IJ.showStatus("");
          RangeDialog rangeDialog = new RangeDialog(options,
            r, series, seriesLabels, cBegin, cEnd, cStep,
            zBegin, zEnd, zStep, tBegin, tEnd, tStep);
          status = rangeDialog.showDialog();
          if (!statusOk(status)) return;
        }
        else debug("no need to prompt for planar ranges");
      }
      else debug("open all planes");
      int[] cCount = new int[seriesCount];
      int[] zCount = new int[seriesCount];
      int[] tCount = new int[seriesCount];
      for (int i=0; i<seriesCount; i++) {
        if (!series[i]) continue;
        cCount[i] = (cEnd[i] - cBegin[i] + cStep[i]) / cStep[i];
        zCount[i] = (zEnd[i] - zBegin[i] + zStep[i]) / zStep[i];
        tCount[i] = (tEnd[i] - tBegin[i] + tStep[i]) / tStep[i];
      }

      Rectangle[] cropOptions = new Rectangle[seriesCount];
      for (int i=0; i<cropOptions.length; i++) {
        if (series[i] && cropOnImport) cropOptions[i] = new Rectangle();
      }
      if (cropOnImport) {
        CropDialog cropDialog = new CropDialog(options,
          r, seriesLabels, series, cropOptions);
        status = cropDialog.showDialog();
        if (!statusOk(status)) return;
      }

      // -- Step 4e: display metadata, if appropriate --

      if (showMetadata) {
        debug("display metadata");
        IJ.showStatus("Populating metadata");

        // display standard metadata in a table in its own window
        Hashtable meta = r.getMetadata();
        //if (seriesCount == 1) meta = r.getMetadata();
        meta.put(options.getLocation(), currentFile);
        int digits = digits(seriesCount);
        for (int i=0; i<seriesCount; i++) {
          if (!series[i]) continue;
          r.setSeries(i);
          //meta.putAll(r.getCoreMetadata().seriesMetadata[i]);

          String s = omexmlMeta.getImageName(i);
          if ((s == null || s.trim().length() == 0) && seriesCount > 1) {
            StringBuffer sb = new StringBuffer();
            sb.append("Series ");
            int zeroes = digits - digits(i + 1);
            for (int j=0; j<zeroes; j++) sb.append(0);
            sb.append(i + 1);
            sb.append(" ");
            s = sb.toString();
          }
          else s += " ";

          final String pad = " "; // puts core values first when alphabetizing
          meta.put(pad + s + "SizeX", new Integer(r.getSizeX()));
          meta.put(pad + s + "SizeY", new Integer(r.getSizeY()));
          meta.put(pad + s + "SizeZ", new Integer(r.getSizeZ()));
          meta.put(pad + s + "SizeT", new Integer(r.getSizeT()));
          meta.put(pad + s + "SizeC", new Integer(r.getSizeC()));
          meta.put(pad + s + "IsRGB", new Boolean(r.isRGB()));
          meta.put(pad + s + "PixelType",
            FormatTools.getPixelTypeString(r.getPixelType()));
          meta.put(pad + s + "LittleEndian", new Boolean(r.isLittleEndian()));
          meta.put(pad + s + "DimensionOrder", r.getDimensionOrder());
          meta.put(pad + s + "IsInterleaved", new Boolean(r.isInterleaved()));
        }

        // sort metadata keys
        String metaString = getMetadataString(meta, "\t");

        SearchableWindow w = new SearchableWindow("Original Metadata - " + id,
          "Key\tValue", metaString, 400, 400);
        w.setVisible(true);
      }
      else debug("skip metadata");

      if (showOMEXML) {
        debug("show OME-XML");
        if (options.isViewBrowser()) {
          // NB: Data Browser has its own internal OME-XML metadata window,
          // which we'll trigger once we have created a Data Browser.
          // So there is no need to pop up a separate OME-XML here.
        }
        else {
          XMLWindow metaWindow = new XMLWindow("OME Metadata - " + id);
          try {
            metaWindow.setXML(MetadataTools.getOMEXML(omexmlMeta));
            WindowTools.placeWindow(metaWindow);
            metaWindow.setVisible(true);
          }
          catch (javax.xml.parsers.ParserConfigurationException exc) {
            WindowTools.reportException(exc, options.isQuiet(),
              "Sorry, there was a problem displaying the OME metadata");
          }
          catch (org.xml.sax.SAXException exc) {
            WindowTools.reportException(exc, options.isQuiet(),
              "Sorry, there was a problem displaying the OME metadata");
          }
        }
      }
      else debug("skip OME-XML");

      // -- Step 4f: read pixel data --

      if (options.isViewNone()) return;

      debug("read pixel data");

      IJ.showStatus("Reading " + currentFile);

      if (options.isVirtual()) {
        int totalSeries = 0;
        for (int i=0; i<seriesCount; i++) {
          if (series[i]) totalSeries++;
        }
        virtualReader.setRefCount(totalSeries);
      }

      for (int i=0; i<seriesCount; i++) {
        if (!series[i]) continue;
        r.setSeries(i);

        boolean[] load = new boolean[num[i]];
        if (!options.isViewNone()) {
          for (int c=cBegin[i]; c<=cEnd[i]; c+=cStep[i]) {
            for (int z=zBegin[i]; z<=zEnd[i]; z+=zStep[i]) {
              for (int t=tBegin[i]; t<=tEnd[i]; t+=tStep[i]) {
                //int index = r.isOrderCertain() ? r.getIndex(z, c, t) : c;
                int index = r.getIndex(z, c, t);
                load[index] = true;
              }
            }
          }
        }
        int total = 0;
        for (int j=0; j<num[i]; j++) if (load[j]) total++;

        FileInfo fi = new FileInfo();

        // populate other common FileInfo fields
        String idDir = idLoc == null ? null : idLoc.getParent();
        if (idDir != null && !idDir.endsWith(File.separator)) {
          idDir += File.separator;
        }
        fi.fileName = idName;
        fi.directory = idDir;

        // place metadata key/value pairs in ImageJ's info field
        String metadata = getMetadataString(r.getMetadata(), " = ");

        long startTime = System.currentTimeMillis();
        long time = startTime;

        ImageStack stackB = null; // for byte images (8-bit)
        ImageStack stackS = null; // for short images (16-bit)
        ImageStack stackF = null; // for floating point images (32-bit)
        ImageStack stackO = null; // for all other images (24-bit RGB)

        int w = cropOnImport ? cropOptions[i].width : r.getSizeX();
        int h = cropOnImport ? cropOptions[i].height : r.getSizeY();
        int c = r.getRGBChannelCount();
        int type = r.getPixelType();

        int q = 0;
        stackOrder = options.getStackOrder();
        if (stackOrder.equals(ImporterOptions.ORDER_DEFAULT)) {
          stackOrder = r.getDimensionOrder();
        }
        virtualReader.setOutputOrder(stackOrder);

        omexmlMeta.setPixelsDimensionOrder(stackOrder, i, 0);

        // dump OME-XML to ImageJ's description field, if available
        fi.description = MetadataTools.getOMEXML(omexmlMeta);

        if (options.isVirtual()) {
          int cSize = r.getSizeC();
          int pt = r.getPixelType();
          boolean doMerge = options.isMergeChannels();
          boolean eight = pt != FormatTools.UINT8 && pt != FormatTools.INT8;
          boolean needComposite = doMerge && (cSize > 3 || eight);
          int merge = (needComposite || !doMerge) ? 1 : cSize;

          // NB: ImageJ 1.39+ is required for VirtualStack
          BFVirtualStack virtualStackB = new BFVirtualStack(id,
            r, colorize, doMerge, options.isRecord());
          stackB = virtualStackB;
          if (doMerge) {
            cCount[i] = 1;
            for (int j=0; j<num[i]; j++) {
              int[] pos = r.getZCTCoords(j);
              if (pos[1] > 0) continue;
              String label = constructSliceLabel(
                new ChannelMerger(r).getIndex(pos[0], pos[1], pos[2]),
                new ChannelMerger(r), omexmlMeta, i, zCount, cCount, tCount);
              virtualStackB.addSlice(label);
            }
          }
          else {
            for (int j=0; j<num[i]; j++) {
              String label = constructSliceLabel(j, r,
                omexmlMeta, i, zCount, cCount, tCount);
              virtualStackB.addSlice(label);
            }
          }
        }
        else {
          if (r.isIndexed()) colorModels = new IndexColorModel[r.getSizeC()];

          for (int j=0; j<num[i]; j++) {
            if (!load[j]) continue;

            // limit message update rate
            long clock = System.currentTimeMillis();
            if (clock - time >= 100) {
              IJ.showStatus("Reading " +
                (seriesCount > 1 ? ("series " + (i + 1) + ", ") : "") +
                "plane " + (j + 1) + "/" + num[i]);
              time = clock;
            }
            IJ.showProgress((double) q++ / total);

            int ndx = j;

            String label = constructSliceLabel(ndx, r,
              omexmlMeta, i, zCount, cCount, tCount);

            // get image processor for jth plane
            ImageProcessor ip = r.openProcessors(ndx, cropOptions[i])[0];
            if (ip == null) {
              plugin.canceled = true;
              return;
            }

            int channel = r.getZCTCoords(ndx)[1];
            if (colorModels != null) {
              colorModels[channel] = (IndexColorModel) ip.getColorModel();
            }

            // add plane to image stack
            if (ip instanceof ByteProcessor) {
              if (stackB == null) stackB = new ImageStack(w, h);
              stackB.addSlice(label, ip);
            }
            else if (ip instanceof ShortProcessor) {
              if (stackS == null) stackS = new ImageStack(w, h);
              stackS.addSlice(label, ip);
            }
            else if (ip instanceof FloatProcessor) {
              // merge image plane into existing stack if possible
              if (stackB != null) {
                ip = ip.convertToByte(true);
                stackB.addSlice(label, ip);
              }
              else if (stackS != null) {
                ip = ip.convertToShort(true);
                stackS.addSlice(label, ip);
              }
              else {
                if (stackF == null) stackF = new ImageStack(w, h);
                stackF.addSlice(label, ip);
              }
            }
            else if (ip instanceof ColorProcessor) {
              if (stackO == null) stackO = new ImageStack(w, h);
              stackO.addSlice(label, ip);
            }
          }
        }

        IJ.showStatus("Creating image");
        IJ.showProgress(1);

        String seriesName = omexmlMeta.getImageName(i);

        showStack(stackB, currentFile, seriesName, omexmlMeta,
          cCount[i], zCount[i], tCount[i], sizeZ[i], sizeC[i], sizeT[i],
          fi, r, options, metadata, windowless);
        showStack(stackS, currentFile, seriesName, omexmlMeta,
          cCount[i], zCount[i], tCount[i], sizeZ[i], sizeC[i], sizeT[i],
          fi, r, options, metadata, windowless);
        showStack(stackF, currentFile, seriesName, omexmlMeta,
          cCount[i], zCount[i], tCount[i], sizeZ[i], sizeC[i], sizeT[i],
          fi, r, options, metadata, windowless);
        showStack(stackO, currentFile, seriesName, omexmlMeta,
          cCount[i], zCount[i], tCount[i], sizeZ[i], sizeC[i], sizeT[i],
          fi, r, options, metadata, windowless);

        long endTime = System.currentTimeMillis();
        double elapsed = (endTime - startTime) / 1000.0;
        if (num[i] == 1) {
          IJ.showStatus("Bio-Formats: " + elapsed + " seconds");
        }
        else {
          long average = (endTime - startTime) / num[i];
          IJ.showStatus("Bio-Formats: " + elapsed + " seconds (" +
            average + " ms per plane)");
        }
      }

      if (concatenate) {
        Vector widths = new Vector();
        Vector heights = new Vector();
        Vector types = new Vector();
        Vector newImps = new Vector();

        for (int j=0; j<imps.size(); j++) {
          ImagePlus imp = (ImagePlus) imps.get(j);
          int wj = imp.getWidth();
          int hj = imp.getHeight();
          int tj = imp.getBitDepth();
          boolean append = false;
          for (int k=0; k<widths.size(); k++) {
            int wk = ((Integer) widths.get(k)).intValue();
            int hk = ((Integer) heights.get(k)).intValue();
            int tk = ((Integer) types.get(k)).intValue();

            if (wj == wk && hj == hk && tj == tk) {
              ImagePlus oldImp = (ImagePlus) newImps.get(k);
              ImageStack is = oldImp.getStack();
              ImageStack newStack = imp.getStack();
              for (int s=0; s<newStack.getSize(); s++) {
                is.addSlice(newStack.getSliceLabel(s + 1),
                  newStack.getProcessor(s + 1));
              }
              oldImp.setStack(oldImp.getTitle(), is);
              newImps.setElementAt(oldImp, k);
              append = true;
              k = widths.size();
            }
          }
          if (!append) {
            widths.add(new Integer(wj));
            heights.add(new Integer(hj));
            types.add(new Integer(tj));
            newImps.add(imp);
          }
        }

        boolean splitC = options.isSplitChannels();
        boolean splitZ = options.isSplitFocalPlanes();
        boolean splitT = options.isSplitTimepoints();

        for (int j=0; j<newImps.size(); j++) {
          ImagePlus imp = (ImagePlus) newImps.get(j);
          imp.show();
          if (splitC || splitZ || splitT) {
            IJ.runPlugIn("loci.plugins.Slicer", "slice_z=" + splitZ +
              " slice_c=" + splitC + " slice_t=" + splitT +
              " stack_order=" + stackOrder + " keep_original=false " +
              "hyper_stack=" + options.isViewHyperstack() + " ");
            imp.close();
          }
          if (mergeChannels && windowless) {
            IJ.runPlugIn("loci.plugins.Colorizer", "stack_order=" + stackOrder +
              " merge=true merge_option=[" + options.getMergeOption() + "] " +
              "series=" + r.getSeries() + " hyper_stack=" +
              options.isViewHyperstack() + " ");
            imp.close();
          }
          else if (mergeChannels) {
            IJ.runPlugIn("loci.plugins.Colorizer", "stack_order=" + stackOrder +
              " merge=true series=" + r.getSeries() + " hyper_stack=" +
              options.isViewHyperstack() + " ");
            imp.close();
          }
        }
      }

      // display ROIs, if necessary

      if (options.showROIs()) {
        debug("display ROIs");

        ROIHandler.openROIs(omexmlMeta,
          (ImagePlus[]) imps.toArray(new ImagePlus[0]));
      }
      else debug("skip ROIs");

      // -- Step 5: finish up --

      debug("finish up");

      try {
        if (!options.isVirtual()) r.close();
      }
      catch (IOException exc) {
        WindowTools.reportException(exc, options.isQuiet(),
          "Sorry, there was a problem closing the file");
      }

      plugin.success = true;
    }
    catch (FormatException exc) {
      WindowTools.reportException(exc, quiet,
        "Sorry, there was a problem reading the data.");
    }
    catch (IOException exc) {
      WindowTools.reportException(exc, quiet,
        "Sorry, there was an I/O problem reading the data.");
    }
  }

  // -- Helper methods --

  /**
   * Displays the given image stack according to
   * the specified parameters and import options.
   */
  private void showStack(ImageStack stack, String file, String series,
    MetadataRetrieve retrieve, int cCount, int zCount, int tCount,
    int sizeZ, int sizeC, int sizeT, FileInfo fi, final IFormatReader r,
    final ImporterOptions options, String metadata, boolean windowless)
    throws FormatException, IOException
  {
    if (stack == null) return;
    String title = getTitle(r, file, series, options.isGroupFiles());
    ImagePlus imp = null;
    if (options.isVirtual()) {
      imp = new VirtualImagePlus(title, stack);
      ((VirtualImagePlus) imp).setReader(r);
    }
    else imp = new ImagePlus(title, stack);

    imp.setProperty("Info", metadata);

    // retrieve the spatial calibration information, if available
    ImagePlusTools.applyCalibration(retrieve, imp, r.getSeries());
    imp.setFileInfo(fi);
    imp.setDimensions(cCount, zCount, tCount);
    displayStack(imp, r, options, windowless);
  }

  /** Displays the image stack using the appropriate plugin. */
  private void displayStack(ImagePlus imp, IFormatReader r,
    ImporterOptions options, boolean windowless)
  {
    boolean mergeChannels = options.isMergeChannels();
    boolean colorize = options.isColorize();
    boolean customColorize = options.isCustomColorize();
    boolean concatenate = options.isConcatenate();
    int nSlices = imp.getNSlices();
    int nFrames = imp.getNFrames();
    if (options.isAutoscale() && !options.isVirtual()) {
      ImagePlusTools.adjustColorRange(imp);
    }
    else {
      // ImageJ may autoscale the images anyway, so we need to manually
      // set the display range to the min/max values allowed for
      // this pixel type
      imp.setDisplayRange(0, Math.pow(2, imp.getBitDepth()) - 1);
    }

    boolean splitC = options.isSplitChannels();
    boolean splitZ = options.isSplitFocalPlanes();
    boolean splitT = options.isSplitTimepoints();

    int z = r.getSizeZ();
    int c = r.getSizeC();
    int t = r.getSizeT();

    if (!concatenate && mergeChannels) imp.show();

    if (!options.isVirtual() && !concatenate) {
      if (mergeChannels && windowless) {
        IJ.runPlugIn("loci.plugins.Colorizer", "stack_order=" + stackOrder +
          " merge=true merge_option=[" + options.getMergeOption() + "] " +
          "series=" + r.getSeries() + " hyper_stack=" +
          options.isViewHyperstack() + " ");
      }
      else if (mergeChannels) {
        IJ.runPlugIn("loci.plugins.Colorizer", "stack_order=" + stackOrder +
          " merge=true series=" + r.getSeries() + " hyper_stack=" +
          options.isViewHyperstack() + " ");
      }
    }

    imp.setDimensions(imp.getStackSize() / (nSlices * nFrames),
      nSlices, nFrames);

    if (options.isViewVisBio()) {
      ReflectedUniverse ru = new ReflectedUniverse();
      try {
        ru.exec("import loci.visbio.data.Dataset");
        //ru.setVar("name", name);
        //ru.setVar("pattern", pattern);
        ru.exec("dataset = new Dataset(name, pattern)");
        // TODO: finish VisBio logic
      }
      catch (ReflectException exc) {
        WindowTools.reportException(exc, options.isQuiet(),
          "Sorry, there was a problem interfacing with VisBio");
        return;
      }
    }
    if (options.isViewImage5D()) {
      ReflectedUniverse ru = new ReflectedUniverse();
      try {
        ru.exec("import i5d.Image5D");
        ru.setVar("title", imp.getTitle());
        ru.setVar("stack", imp.getStack());
        ru.setVar("sizeC", c);
        ru.setVar("sizeZ", z);
        ru.setVar("sizeT", t);
        ru.exec("i5d = new Image5D(title, stack, sizeC, sizeZ, sizeT)");
        ru.setVar("cal", imp.getCalibration());
        ru.setVar("fi", imp.getOriginalFileInfo());
        ru.exec("i5d.setCalibration(cal)");
        ru.exec("i5d.setFileInfo(fi)");
        //ru.exec("i5d.setDimensions(sizeC, sizeZ, sizeT)");
        ru.exec("i5d.show()");
      }
      catch (ReflectException exc) {
        WindowTools.reportException(exc, options.isQuiet(),
          "Sorry, there was a problem interfacing with Image5D");
        return;
      }
    }
    else if (options.isViewView5D()) {
      WindowManager.setTempCurrentImage(imp);
      IJ.run("View5D ", "");
    }
    else if (!options.isViewNone()) {
      // NB: ImageJ 1.39+ is required for hyperstacks
      boolean hyper = options.isViewHyperstack() || options.isViewBrowser();
      imp.setOpenAsHyperStack(hyper);

      if (!concatenate) {
        if (options.isViewBrowser()) {
          DataBrowser dataBrowser = new DataBrowser(imp, null,
            r.getChannelDimTypes(), r.getChannelDimLengths());
          if (options.isShowOMEXML()) dataBrowser.showMetadataWindow();
        }
        else imp.show();

        boolean virtual = options.isViewBrowser() || options.isVirtual();

        if ((colorize || customColorize) && !virtual) {
          IJ.runPlugIn("loci.plugins.Colorizer", "stack_order=" + stackOrder +
            " merge=false colorize=true ndx=" + (customColorize ? "-1" : "0") +
            " series=" + r.getSeries() + " hyper_stack=" +
            options.isViewHyperstack() + " ");
        }
        else if (colorModels != null && !virtual) {
          Colorizer colorizer = new Colorizer();
          String arg = "stack_order=" + stackOrder + " merge=false " +
            "colorize=true series=" + r.getSeries() + " hyper_stack=" +
            options.isViewHyperstack() + " ";
          colorizer.setup(arg, imp);
          for (int channel=0; channel<colorModels.length; channel++) {
            byte[][] lut = new byte[3][256];
            colorModels[channel].getReds(lut[0]);
            colorModels[channel].getGreens(lut[1]);
            colorModels[channel].getBlues(lut[2]);
            colorizer.setLookupTable(lut, channel);
          }
          new PlugInFilterRunner(colorizer, "", arg);
        }

        if ((splitC || splitZ || splitT) && !virtual) {
          IJ.runPlugIn("loci.plugins.Slicer", "slice_z=" + splitZ +
            " slice_c=" + splitC + " slice_t=" + splitT +
            " stack_order=" + stackOrder + " keep_original=false " +
            "hyper_stack=" + options.isViewHyperstack() + " ");
          imp.close();
        }
      }
      imps.add(imp);
    }
  }

  /** Computes the given value's number of digits. */
  private int digits(int value) {
    int digits = 0;
    while (value > 0) {
      value /= 10;
      digits++;
    }
    return digits;
  }

  /** Get an appropriate stack title, given the file name. */
  private String getTitle(IFormatReader r, String file, String series,
    boolean groupFiles)
  {
    String[] used = r.getUsedFiles();
    String title = file.substring(file.lastIndexOf(File.separator) + 1);
    if (used.length > 1 && groupFiles) {
      FilePattern fp = new FilePattern(new Location(file));
      if (fp != null) {
        title = fp.getPattern();
        if (title == null) {
          title = file;
          if (title.indexOf(".") != -1) {
            title = title.substring(0, title.lastIndexOf("."));
          }
        }
        title = title.substring(title.lastIndexOf(File.separator) + 1);
      }
    }
    if (series != null && !file.endsWith(series) && r.getSeriesCount() > 1) {
      title += " - " + series;
    }
    if (title.length() > 128) {
      String a = title.substring(0, 62);
      String b = title.substring(title.length() - 62);
      title = a + "..." + b;
    }
    return title;
  }

  /** Constructs slice label. */
  private String constructSliceLabel(int ndx, IFormatReader r,
    MetadataRetrieve retrieve, int series,
    int[] zCount, int[] cCount, int[] tCount)
  {
    r.setSeries(series);
    int[] zct = r.getZCTCoords(ndx);
    int[] subC = r.getChannelDimLengths();
    String[] subCTypes = r.getChannelDimTypes();
    StringBuffer sb = new StringBuffer();
    boolean first = true;
    if (cCount[series] > 1) {
      if (first) first = false;
      else sb.append("; ");
      int[] subCPos = FormatTools.rasterToPosition(subC, zct[1]);
      for (int i=0; i<subC.length; i++) {
        boolean ch = subCTypes[i].equals(FormatTools.CHANNEL);
        sb.append(ch ? "c" : subCTypes[i]);
        sb.append(":");
        sb.append(subCPos[i] + 1);
        sb.append("/");
        sb.append(subC[i]);
        if (i < subC.length - 1) sb.append(", ");
      }
    }
    if (zCount[series] > 1) {
      if (first) first = false;
      else sb.append("; ");
      sb.append("z:");
      sb.append(zct[0] + 1);
      sb.append("/");
      sb.append(r.getSizeZ());
    }
    if (tCount[series] > 1) {
      if (first) first = false;
      else sb.append("; ");
      sb.append("t:");
      sb.append(zct[2] + 1);
      sb.append("/");
      sb.append(r.getSizeT());
    }
    // put image name at the end, in case it is long
    String imageName = retrieve.getImageName(series);
    if (imageName != null && !imageName.trim().equals("")) {
      sb.append(" - ");
      sb.append(imageName);
    }
    return sb.toString();
  }

  /** Verifies that the given status result is OK. */
  private boolean statusOk(int status) {
    if (status == OptionsDialog.STATUS_CANCELED) plugin.canceled = true;
    return status == OptionsDialog.STATUS_OK;
  }

  /** Returns a string with each key/value pair on its own line. */
  private String getMetadataString(Hashtable meta, String separator) {
    Enumeration e = meta.keys();
    Vector v = new Vector();
    while (e.hasMoreElements()) v.add(e.nextElement());
    String[] keys = new String[v.size()];
    v.copyInto(keys);
    Arrays.sort(keys);

    StringBuffer sb = new StringBuffer();
    for (int i=0; i<keys.length; i++) {
      sb.append(keys[i]);
      sb.append(separator);
      sb.append(meta.get(keys[i]));
      sb.append("\n");
    }
    return sb.toString();
  }

  /** Prints a debugging message to the ImageJ log if debug mode is set. */
  private void debug(String msg) {
    if (IJ.debugMode) IJ.log("Bio-Formats Importer: " + msg);
  }

  // -- Main method --

  /** Main method, for testing. */
  public static void main(String[] args) {
    new ImageJ(null);
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<args.length; i++) {
      if (i > 0) sb.append(" ");
      sb.append(args[i]);
    }
    new LociImporter().run(sb.toString());
  }

  // -- Helper classes --

  /** Used to echo status messages to the ImageJ status bar. */
  private static class StatusEchoer implements StatusListener {
    public void statusUpdated(StatusEvent e) {
      IJ.showStatus(e.getStatusMessage());
    }
  }

  private class VirtualReader extends DimensionSwapper {
    private int refCount;

    // -- Constructor --

    public VirtualReader(IFormatReader r) {
      super(r);
      refCount = 0;
    }

    // -- VirtualReader API methods --

    public void setRefCount(int refCount) {
      this.refCount = refCount;
    }

    // -- IFormatReader API methods --

    public void close() throws IOException {
      if (refCount > 0) refCount--;
      if (refCount == 0) super.close();
    }

  }

}
