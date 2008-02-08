//
// Importer.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
4D Data Browser, Bio-Formats Importer, Bio-Formats Exporter and OME plugins.
Copyright (C) 2005-@year@ Melissa Linkert, Christopher Peterson,
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
import ij.io.FileInfo;
import ij.process.*;
import java.awt.Rectangle;
import java.io.*;
import java.util.*;
import loci.formats.*;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.ome.OMEReader;
import loci.formats.ome.OMEROReader;
import loci.plugins.browser.LociDataBrowser;

/**
 * Core logic for the Bio-Formats Importer ImageJ plugin.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/Importer.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/Importer.java">SVN</a></dd></dl>
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

  // -- Constructor --

  public Importer(LociImporter plugin) {
    this.plugin = plugin;
  }

  // -- Importer API methods --

  /** Executes the plugin. */
  public void run(String arg) {

    // -- Step 1: parse core options --

    ImporterOptions options = new ImporterOptions();
    options.loadPreferences();
    options.parseArg(arg);

    int status = options.promptLocation();
    if (!statusOk(status)) return;
    status = options.promptId();
    if (!statusOk(status)) return;

    String id = options.getId();
    boolean quiet = options.isQuiet();
    boolean windowless = options.isWindowless();

    Location idLoc = options.getIdLocation();
    String idName = options.getIdName();
    String idType = options.getIdType();

    // -- Step 2: construct reader and check id --

    IFormatReader r = null;
    if (options.isLocal() || options.isHTTP()) {
      IJ.showStatus("Identifying " + idName);
      ImageReader reader = new ImageReader();
      try { r = reader.getReader(id); }
      catch (FormatException exc) {
        reportException(exc, quiet,
          "Sorry, there was an error reading the file.");
        return;
      }
      catch (IOException exc) {
        reportException(exc, quiet,
          "Sorry, there was a I/O problem reading the file.");
        return;
      }
    }
    else if (options.isOMERO()) r = new OMEROReader();
    else { // options.isOME
      r = new OMEReader();
    }
    MetadataStore store = MetadataTools.createOMEXMLMetadata();
    MetadataRetrieve retrieve = (MetadataRetrieve) store;
    r.setMetadataStore(store);

    IJ.showStatus("");
    r.addStatusListener(new StatusEchoer());

    // -- Step 3: get parameter values --

    if (!windowless) status = options.promptOptions();
    if (!statusOk(status)) return;

    boolean mergeChannels = options.isMergeChannels();
    boolean colorize = options.isColorize();
    boolean showMetadata = options.isShowMetadata();
    boolean groupFiles = options.isGroupFiles();
    boolean concatenate = options.isConcatenate();
    boolean specifyRanges = options.isSpecifyRanges();
    boolean autoscale = options.isAutoscale();
    boolean cropOnImport = options.doCrop();

    boolean viewNone = options.isViewNone();
    boolean viewStandard = options.isViewStandard();
    boolean viewImage5D = options.isViewImage5D();
    boolean viewBrowser = options.isViewBrowser();
    boolean viewView5D = options.isViewView5D();

    // -- Step 4: analyze and read from data source --

    // 'id' contains the user's password if we are opening from OME/OMERO
    String a = id;
    if (options.isOME() || options.isOMERO()) a = "...";
    IJ.showStatus("Analyzing " + a);

    try {
      r.setMetadataFiltered(true);
      r.setNormalized(true);
      r.setId(id);

      int pixelType = r.getPixelType();
      String currentFile = r.getCurrentFile();

      // -- Step 4a: prompt for the file pattern, if necessary --

      if (groupFiles) {
        try {
          status = options.promptFilePattern();
          if (!statusOk(status)) return;
        }
        catch (NullPointerException e) { }
        id = options.getId();
        if (id == null) id = currentFile;
      }

      if (groupFiles) r = new FileStitcher(r, true);
      r = new ChannelSeparator(r);
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
        String name = retrieve.getImageName(i);
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
      }

      if (seriesCount > 1 && !options.openAllSeries()) {
        status = options.promptSeries(r, seriesLabels, series);
        if (!statusOk(status)) return;
      }

      if (options.openAllSeries()) Arrays.fill(series, true);

      // -- Step 4c: prompt for the range of planes to import, if necessary --

      if (specifyRanges) {
        boolean needRange = false;
        for (int i=0; i<seriesCount; i++) {
          if (series[i] && num[i] > 1) needRange = true;
        }
        if (needRange) {
          IJ.showStatus("");
          status = options.promptRange(r, series, seriesLabels,
            cBegin, cEnd, cStep, zBegin, zEnd, zStep, tBegin, tEnd, tStep);
          if (!statusOk(status)) return;
        }
      }
      int[] cCount = new int[seriesCount];
      int[] zCount = new int[seriesCount];
      int[] tCount = new int[seriesCount];
      for (int i=0; i<seriesCount; i++) {
        cCount[i] = (cEnd[i] - cBegin[i] + cStep[i]) / cStep[i];
        zCount[i] = (zEnd[i] - zBegin[i] + zStep[i]) / zStep[i];
        tCount[i] = (tEnd[i] - tBegin[i] + tStep[i]) / tStep[i];
      }

      Rectangle[] cropOptions = new Rectangle[seriesCount];
      for (int i=0; i<cropOptions.length; i++) {
        if (series[i] && cropOnImport) cropOptions[i] = new Rectangle();
      }
      if (cropOnImport) {
        status = options.promptCropSize(r, seriesLabels, series, cropOptions);
        if (!statusOk(status)) return;
      }

      // -- Step 4e: display metadata, if appropriate --

      if (showMetadata) {
        IJ.showStatus("Populating metadata");

        // display standard metadata in a table in its own window
        Hashtable meta = new Hashtable();
        if (seriesCount == 1) meta = r.getMetadata();
        meta.put(idType, currentFile);
        int digits = digits(seriesCount);
        for (int i=0; i<seriesCount; i++) {
          if (!series[i]) continue;
          r.setSeries(i);
          meta.putAll(r.getCoreMetadata().seriesMetadata[i]);

          String s = retrieve.getImageName(i);
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
        StringBuffer sb = getMetadataString(meta, "\t");

        SearchableWindow w = new SearchableWindow("Metadata - " + id,
          "Key\tValue", sb.toString(), 400, 400);
        w.setVisible(true);
      }

      // -- Step 4e: read pixel data --

      // only read data explicitly if not using 4D Data Browser
      if (!viewBrowser) {
        IJ.showStatus("Reading " + currentFile);

        for (int i=0; i<seriesCount; i++) {
          if (!series[i]) continue;
          r.setSeries(i);

          boolean[] load = new boolean[num[i]];
          if (!viewNone) {
            for (int c=cBegin[i]; c<=cEnd[i]; c+=cStep[i]) {
              for (int z=zBegin[i]; z<=zEnd[i]; z+=zStep[i]) {
                for (int t=tBegin[i]; t<=tEnd[i]; t+=tStep[i]) {
                  int index = r.getIndex(z, c, t);
                  load[index] = true;
                }
              }
            }
          }
          int total = 0;
          for (int j=0; j<num[i]; j++) if (load[j]) total++;

          // dump OME-XML to ImageJ's description field, if available
          FileInfo fi = new FileInfo();
          fi.description = MetadataTools.getOMEXML(retrieve);

          // populate other common FileInfo fields
          String idDir = idLoc.getParent();
          if (idDir != null && !idDir.endsWith(File.separator)) {
            idDir += File.separator;
          }
          fi.fileName = idName;
          fi.directory = idDir;

          // place metadata key/value pairs in ImageJ's info field
          StringBuffer metadata = getMetadataString(r.getMetadata(), " = ");

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
          if (options.isViewView5D()) {
            stackOrder = ImporterOptions.ORDER_XYZCT;
          }
          if (options.isViewImage5D() || options.isViewHyperstack()) {
            stackOrder = ImporterOptions.ORDER_XYCZT;
          }

          if (options.isVirtual()) {
            boolean needComposite = options.isMergeChannels() &&
              (r.getSizeC() > 3 || (r.getPixelType() != FormatTools.UINT8 &&
              r.getPixelType() != FormatTools.INT8));
            stackB = new CustomStack(w, h, null, id, r, (needComposite ||
              !options.isMergeChannels()) ? 1 : r.getSizeC());
            for (int j=0; j<num[i]; j++) {
              ((CustomStack) stackB).addSlice(constructSliceLabel(j, r,
              retrieve, i, new int[][] {zCount, cCount, tCount}));
            }
          }
          else {
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

              int ndx = FormatTools.getReorderedIndex(r, stackOrder, j);

              String label = constructSliceLabel(ndx, r, retrieve, i,
                new int[][] {zCount, cCount, tCount});

              // get image processor for jth plane
              ImageProcessor ip = Util.openProcessor(r, ndx, cropOptions[i]);
              if (ip == null) {
                plugin.canceled = true;
                return;
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

          String seriesName = retrieve.getImageName(i);

          showStack(stackB, currentFile, seriesName, retrieve,
            cCount[i], zCount[i], tCount[i], sizeZ[i], sizeC[i], sizeT[i],
            fi, r, options, metadata);
          showStack(stackS, currentFile, seriesName, retrieve,
            cCount[i], zCount[i], tCount[i], sizeZ[i], sizeC[i], sizeT[i],
            fi, r, options, metadata);
          showStack(stackF, currentFile, seriesName, retrieve,
            cCount[i], zCount[i], tCount[i], sizeZ[i], sizeC[i], sizeT[i],
            fi, r, options, metadata);
          showStack(stackO, currentFile, seriesName, retrieve,
            cCount[i], zCount[i], tCount[i], sizeZ[i], sizeC[i], sizeT[i],
            fi, r, options, metadata);

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

          for (int i=0; i<imps.size(); i++) {
            ImagePlus imp = (ImagePlus) imps.get(i);
            int w = imp.getWidth();
            int h = imp.getHeight();
            int type = imp.getBitDepth();
            boolean append = false;
            for (int j=0; j<widths.size(); j++) {
              int width = ((Integer) widths.get(j)).intValue();
              int height = ((Integer) heights.get(j)).intValue();
              int t = ((Integer) types.get(j)).intValue();

              if (width == w && height == h && type == t) {
                ImagePlus oldImp = (ImagePlus) newImps.get(j);
                ImageStack is = oldImp.getStack();
                ImageStack newStack = imp.getStack();
                for (int k=0; k<newStack.getSize(); k++) {
                  is.addSlice(newStack.getSliceLabel(k+1),
                    newStack.getProcessor(k+1));
                }
                oldImp.setStack(oldImp.getTitle(), is);
                newImps.setElementAt(oldImp, j);
                append = true;
                j = widths.size();
              }
            }
            if (!append) {
              widths.add(new Integer(w));
              heights.add(new Integer(h));
              types.add(new Integer(type));
              newImps.add(imp);
            }
          }

          boolean splitC = options.isSplitChannels();
          boolean splitZ = options.isSplitFocalPlanes();
          boolean splitT = options.isSplitTimepoints();

          for (int i=0; i<newImps.size(); i++) {
            ImagePlus imp = (ImagePlus) newImps.get(i);
            imp.show();
            if (splitC || splitZ || splitT) {
              IJ.runPlugIn("loci.plugins.Slicer", "slice_z=" + splitZ +
                " slice_c=" + splitC + " slice_t=" + splitT +
                " stack_order=" + stackOrder + " keep_original=false " +
                "hyper_stack=" + options.isViewHyperstack() + " ");
            }
          }
        }
      }

      // -- Step 5: finish up --

      plugin.success = true;

      options.savePreferences();

      if (viewBrowser) {
        boolean first = true;
        for (int i=0; i<seriesCount; i++) {
          if (!series[i]) continue;
          IFormatReader reader = first ? r : null;
          new LociDataBrowser(reader, id, i, mergeChannels, colorize).run();
          first = false;
        }
      }
    }
    catch (FormatException exc) {
      reportException(exc, quiet,
        "Sorry, there was a problem reading the data.");
    }
    catch (IOException exc) {
      reportException(exc, quiet,
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
    int sizeZ, int sizeC, int sizeT, FileInfo fi, IFormatReader r,
    ImporterOptions options, StringBuffer metadata)
    throws FormatException, IOException
  {
    if (stack == null) return;
    ImagePlus imp = new ImagePlus(getTitle(r, file, series), stack);
    imp.setProperty("Info", metadata.toString());

    // retrieve the spatial calibration information, if available
    Util.applyCalibration(retrieve, imp, r.getSeries());
    imp.setFileInfo(fi);
    imp.setDimensions(cCount, zCount, tCount);
    displayStack(imp, r, options);
  }

  /** Displays the image stack using the appropriate plugin. */
  private void displayStack(ImagePlus imp,
    IFormatReader r, ImporterOptions options)
  {
    boolean mergeChannels = options.isMergeChannels();
    boolean colorize = options.isColorize();
    boolean concatenate = options.isConcatenate();
    int nChannels = imp.getNChannels();
    int nSlices = imp.getNSlices();
    int nFrames = imp.getNFrames();
    if (options.isAutoscale() && !options.isVirtual()) {
      Util.adjustColorRange(imp);
    }

    boolean splitC = options.isSplitChannels();
    boolean splitZ = options.isSplitFocalPlanes();
    boolean splitT = options.isSplitTimepoints();

    if (!concatenate && mergeChannels) imp.show();

    if (mergeChannels && options.isWindowless()) {
      IJ.runPlugIn("loci.plugins.Colorizer", "stack_order=" + stackOrder +
        " merge=true merge_option=[" + options.getMergeOption() + "] " +
        "hyper_stack=" + options.isViewHyperstack() + " ");
    }
    else if (mergeChannels) {
      IJ.runPlugIn("loci.plugins.Colorizer", "stack_order=" + stackOrder +
        " merge=true hyper_stack=" + options.isViewHyperstack() + " ");
    }

    imp.setDimensions(imp.getStackSize() / (nSlices * nFrames),
      nSlices, nFrames);

    if (options.isViewBrowser()) { }
    else if (options.isViewImage5D()) {
      ReflectedUniverse ru = new ReflectedUniverse();
      try {
        ru.exec("import i5d.Image5D");
        ru.setVar("title", imp.getTitle());
        ru.setVar("stack", imp.getStack());
        ru.setVar("sizeC", r.getSizeC());
        ru.setVar("sizeZ", r.getSizeZ());
        ru.setVar("sizeT", r.getSizeT());
        ru.exec("i5d = new Image5D(title, stack, sizeC, sizeZ, sizeT)");
        ru.setVar("cal", imp.getCalibration());
        ru.setVar("fi", imp.getOriginalFileInfo());
        ru.exec("i5d.setCalibration(cal)");
        ru.exec("i5d.setFileInfo(fi)");
        //ru.exec("i5d.setDimensions(sizeC, sizeZ, sizeT)");
        ru.exec("i5d.show()");
      }
      catch (ReflectException exc) {
        reportException(exc, options.isQuiet(),
          "Sorry, there was a problem interfacing with Image5D");
        return;
      }
    }
    else if (options.isViewView5D()) {
      WindowManager.setTempCurrentImage(imp);
      IJ.run("View5D ", "");
    }
    else if (!options.isViewNone()) {
      if (IJ.getVersion().compareTo("1.39l") >= 0) {
        imp.setOpenAsHyperStack(options.isViewHyperstack());
      }

      if (!concatenate) {
        imp.show();
        if (splitC || splitZ || splitT) {
          IJ.runPlugIn("loci.plugins.Slicer", "slice_z=" + splitZ +
            " slice_c=" + splitC + " slice_t=" + splitT +
            " stack_order=" + stackOrder + " keep_original=false " +
            "hyper_stack=" + options.isViewHyperstack() + " ");
          if (colorize) {
            int[] openImages = WindowManager.getIDList();
            for (int i=0; i<openImages.length; i++) {
              ImagePlus p = WindowManager.getImage(openImages[i]);
              String title = p.getTitle();
              if (title.startsWith(imp.getTitle()) &&
                title.indexOf("C=") != -1)
              {
                int channel =
                  Integer.parseInt(title.substring(title.indexOf("C=") + 2));
                WindowManager.setCurrentWindow(p.getWindow());
                IJ.runPlugIn("loci.plugins.Colorizer",
                  "stack_order=" + stackOrder + " merge=false colorize=true" +
                  " ndx=" + (channel % 3) + " hyper_stack=" +
                  options.isViewHyperstack() + " ");
              }
            }
          }
        }
        else if (colorize) {
          IJ.runPlugIn("loci.plugins.Colorizer", "stack_order=" + stackOrder +
            " merge=false colorize=true ndx=0 hyper_stack=" +
            options.isViewHyperstack() + " ");
        }
      }
      else imps.add(imp);
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
  private String getTitle(IFormatReader r, String file, String series) {
    String[] used = r.getUsedFiles();
    String title = file.substring(file.lastIndexOf(File.separator) + 1);
    if (used.length > 1) {
      FilePattern fp = new FilePattern(new Location(file));
      if (fp != null) {
        title = fp.getPattern();
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

  /** Construct slice label. */
  private String constructSliceLabel(int ndx, IFormatReader r,
    MetadataRetrieve retrieve, int series, int[][] counts)
  {
    r.setSeries(series);
    int[] zct = r.getZCTCoords(ndx);
    int[] subC = r.getChannelDimLengths();
    String[] subCTypes = r.getChannelDimTypes();
    StringBuffer sb = new StringBuffer();
    if (r.isOrderCertain()) {
      boolean first = true;
      if (counts[1][series] > 1) {
        if (first) first = false;
        else sb.append("; ");
        int[] subCPos = FormatTools.rasterToPosition(subC, zct[1]);
        for (int i=0; i<subC.length; i++) {
          if (!subCTypes[i].equals(FormatTools.CHANNEL)) sb.append(subCTypes[i]);
          else sb.append("ch");
          sb.append(":");
          sb.append(subCPos[i] + 1);
          sb.append("/");
          sb.append(subC[i]);
          if (i < subC.length - 1) sb.append("; ");
        }
      }
      if (counts[0][series] > 1) {
        if (first) first = false;
        else sb.append("; ");
        sb.append("z:");
        sb.append(zct[0] + 1);
        sb.append("/");
        sb.append(r.getSizeZ());
      }
      if (counts[2][series] > 1) {
        if (first) first = false;
        else sb.append("; ");
        sb.append("t:");
        sb.append(zct[2] + 1);
        sb.append("/");
        sb.append(r.getSizeT());
      }
    }
    else {
      sb.append("no:");
      sb.append(ndx + 1);
      sb.append("/");
      sb.append(r.getImageCount());
    }
    // put image name at the end, in case it is too long
    String imageName = retrieve.getImageName(series);
    if (imageName != null) {
      sb.append(" - ");
      sb.append(imageName);
    }
    return sb.toString();
  }

  /** Verifies that the given status result is OK. */
  private boolean statusOk(int status) {
    if (status == ImporterOptions.STATUS_CANCELED) plugin.canceled = true;
    return status == ImporterOptions.STATUS_OK;
  }

  /** Reports the given exception with stack trace in an ImageJ error dialog. */
  private void reportException(Throwable t, boolean quiet, String msg) {
    IJ.showStatus("");
    if (!quiet) {
      ByteArrayOutputStream buf = new ByteArrayOutputStream();
      t.printStackTrace(new PrintStream(buf));
      String s = new String(buf.toByteArray());
      StringTokenizer st = new StringTokenizer(s, "\n\r");
      while (st.hasMoreTokens()) IJ.write(st.nextToken());
      IJ.error("Bio-Formats Importer", msg);
    }
  }

  /** Return a StringBuffer with each key/value pair on its own line. */
  private StringBuffer getMetadataString(Hashtable meta, String separator) {
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
    return sb;
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

}
