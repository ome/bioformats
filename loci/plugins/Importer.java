//
// Importer.java
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
import ij.io.FileInfo;
import ij.measure.Calibration;
import ij.process.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import loci.formats.*;
import loci.formats.ome.OMEReader;
import loci.formats.ome.OMEXMLMetadata;
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
          "Sorry, there was an error reading the file");
        return;
      }
      catch (IOException exc) {
        reportException(exc, quiet,
          "Sorry, there was a I/O problem reading the file");
        return;
      }
    }
    else { // options.isOME
      r = new OMEReader();
    }
    OMEXMLMetadata store = new OMEXMLMetadata();
    r.setMetadataStore(store);

    IJ.showStatus("");
    r.addStatusListener(new StatusEchoer());

    // -- Step 3: get parameter values --

    status = options.promptOptions();
    if (!statusOk(status)) return;

    boolean mergeChannels = options.isMergeChannels();
    boolean colorize = options.isColorize();
    boolean showMetadata = options.isShowMetadata();
    boolean groupFiles = options.isGroupFiles();
    boolean concatenate = options.isConcatenate();
    boolean specifyRanges = options.isSpecifyRanges();
    boolean autoscale = options.isAutoscale();

    boolean viewNone = options.isViewNone();
    boolean viewStandard = options.isViewStandard();
    boolean viewImage5D = options.isViewImage5D();
    boolean viewBrowser = options.isViewBrowser();
    boolean viewView5D = options.isViewView5D();

    // -- Step 4: analyze and read from data source --

    IJ.showStatus("Analyzing " + id);

    try {
      FileStitcher fs = null;
      r.setMetadataFiltered(true);
      r.setId(id);

      int pixelType = r.getPixelType();
      String currentFile = r.getCurrentFile();

      // -- Step 4a: prompt for the file pattern, if necessary --

      if (groupFiles) {
        status = options.promptFilePattern();
        if (!statusOk(status)) return;
        id = options.getId();
      }

      // CTR FIXME -- why is the file stitcher separate from the reader?
      // (answer: because of the clunkiness of the 4D Data Browser integration)
      if (groupFiles) r = fs = new FileStitcher(r, true);
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
        String name = store.getImageName(new Integer(i));
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

      if (seriesCount > 1) {
        status = options.promptSeries(r, seriesLabels, series);
        if (!statusOk(status)) return;
      }

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

      // -- Step 4d: display metadata, if appropriate --

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

          String s = store.getImageName(new Integer(i));
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
        Enumeration e = meta.keys();
        Vector v = new Vector();
        while (e.hasMoreElements()) v.add(e.nextElement());
        String[] keys = new String[v.size()];
        v.copyInto(keys);
        Arrays.sort(keys);

        StringBuffer sb = new StringBuffer();
        for (int i=0; i<keys.length; i++) {
          sb.append(keys[i]);
          sb.append("\t");
          sb.append(meta.get(keys[i]));
          sb.append("\n");
        }

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
          fi.description = store.dumpXML();

          long startTime = System.currentTimeMillis();
          long time = startTime;

          ImageStack stackB = null; // for byte images (8-bit)
          ImageStack stackS = null; // for short images (16-bit)
          ImageStack stackF = null; // for floating point images (32-bit)
          ImageStack stackO = null; // for all other images (24-bit RGB)

          int w = r.getSizeX();
          int h = r.getSizeY();
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
          if (options.isViewImage5D()) {
            stackOrder = ImporterOptions.ORDER_XYCZT;
          }

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

            // construct label for this slice
            int[] zct = r.getZCTCoords(ndx);
            StringBuffer sb = new StringBuffer();
            if (certain[i]) {
              boolean first = true;
              if (cCount[i] > 1) {
                if (first) first = false;
                else sb.append("; ");
                sb.append("ch:");
                sb.append(zct[1] + 1);
                sb.append("/");
                sb.append(sizeC[i]);
              }
              if (zCount[i] > 1) {
                if (first) first = false;
                else sb.append("; ");
                sb.append("z:");
                sb.append(zct[0] + 1);
                sb.append("/");
                sb.append(sizeZ[i]);
              }
              if (tCount[i] > 1) {
                if (first) first = false;
                else sb.append("; ");
                sb.append("t:");
                sb.append(zct[2] + 1);
                sb.append("/");
                sb.append(sizeT[i]);
              }
            }
            else {
              sb.append("no:");
              sb.append(j + 1);
              sb.append("/");
              sb.append(num[i]);
            }
            // put image name at the end, in case it is too long
            String imageName = store.getImageName(new Integer(i));
            if (imageName != null) {
              sb.append(" - ");
              sb.append(imageName);
            }
            String label = sb.toString();

            // get image processor for jth plane
            ImageProcessor ip = Util.openProcessor(r, ndx);
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

          IJ.showStatus("Creating image");
          IJ.showProgress(1);

          String seriesName = store.getImageName(new Integer(i));

          showStack(stackB, currentFile, seriesName, store,
            cCount[i], zCount[i], tCount[i], sizeZ[i], sizeC[i], sizeT[i],
            fi, r, fs, options);
          showStack(stackS, currentFile, seriesName, store,
            cCount[i], zCount[i], tCount[i], sizeZ[i], sizeC[i], sizeT[i],
            fi, r, fs, options);
          showStack(stackF, currentFile, seriesName, store,
            cCount[i], zCount[i], tCount[i], sizeZ[i], sizeC[i], sizeT[i],
            fi, r, fs, options);
          showStack(stackO, currentFile, seriesName, store,
            cCount[i], zCount[i], tCount[i], sizeZ[i], sizeC[i], sizeT[i],
            fi, r, fs, options);

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
        r.close();

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
              IJ.runPlugIn("loci.plugins.Slicer", "sliceZ=" + splitZ +
                ", sliceC=" + splitC + ", sliceT=" + splitT + ", stackOrder=" +
                stackOrder + ", keepOriginal=false");
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
          FileStitcher stitcher = first ? fs : null;
          //new LociDataBrowser(reader, id, i, mergeChannels).run();
          new LociDataBrowser(reader, stitcher, id, i, mergeChannels).run();
          first = false;
        }
      }
    }
    catch (FormatException exc) {
      reportException(exc, quiet,
        "Sorry, there was a problem reading the data");
    }
    catch (IOException exc) {
      reportException(exc, quiet,
        "Sorry, there was an I/O problem reading the data");
    }
  }

  // -- Helper methods --

  /**
   * Displays the given image stack according to
   * the specified parameters and import options.
   */
  private void showStack(ImageStack stack, String file, String series,
    OMEXMLMetadata store, int cCount, int zCount, int tCount,
    int sizeZ, int sizeC, int sizeT, FileInfo fi, IFormatReader r,
    FileStitcher fs, ImporterOptions options)
    throws FormatException, IOException
  {
    if (stack == null) return;
    ImagePlus imp = new ImagePlus(file + " - " + series, stack);
    imp.setProperty("Info", "File full path=" + file +
      "\nSeries name=" + series + "\n");

    // retrieve the spatial calibration information, if available
    applyCalibration(store, imp, r.getSeries());
    imp.setFileInfo(fi);
    imp.setDimensions(cCount, zCount, tCount);
    displayStack(imp, r, fs, options);
  }

  /** Displays the image stack using the appropriate plugin. */
  private void displayStack(ImagePlus imp,
    IFormatReader r, FileStitcher fs, ImporterOptions options)
  {
    boolean mergeChannels = options.isMergeChannels();
    boolean concatenate = options.isConcatenate();
    int nChannels = imp.getNChannels();
    int nSlices = imp.getNSlices();
    int nFrames = imp.getNFrames();
    if (options.isAutoscale()) Util.adjustColorRange(imp);

    // convert to RGB if needed
    int pixelType = r.getPixelType();
    if (mergeChannels && r.getSizeC() > 1 && r.getSizeC() < 4 &&
      (pixelType == FormatTools.UINT8 || pixelType == FormatTools.INT8) &&
      !r.isIndexed())
    {
      makeRGB(imp, r, r.getSizeC(), options.isAutoscale());
    }
    else if (mergeChannels && r.getSizeC() > 1 && r.getSizeC() < 4 &&
      !r.isIndexed())
    {
      // use compareTo instead of IJ.versionLessThan(...), because we want
      // to suppress the error message
      if (imp.getStackSize() == r.getSizeC() &&
        ImageJ.VERSION.compareTo("1.38n") < 0)
      {
        // use reflection to construct CompositeImage,
        // in case ImageJ version is too old
        ReflectedUniverse ru = new ReflectedUniverse();
        try {
          ru.exec("import ij.CompositeImage");
          ru.setVar("imp", imp);
          ru.setVar("sizeC", r.getSizeC());
          imp = (ImagePlus) ru.exec("new CompositeImage(imp, sizeC)");
        }
        catch (ReflectException exc) {
          imp = new CustomImage(imp, stackOrder, r.getSizeZ(),
            r.getSizeT(), r.getSizeC(), options.isAutoscale());
        }
      }
      else {
        imp = new CustomImage(imp, stackOrder, r.getSizeZ(),
          r.getSizeT(), r.getSizeC(), options.isAutoscale());
      }
    }
    else if (mergeChannels && r.getSizeC() >= 4) {
      // ask the user what they would like to do...
      // CTR FIXME -- migrate into ImporterOptions?
      // also test with macros, and merging multiple image stacks
      // (i.e., what happens if this code executes more than once?)

      int planes1 = r.getImageCount() / 2;
      if (planes1 * 2 < r.getImageCount()) planes1++;
      int planes2 = r.getImageCount() / 3;
      if (planes2 * 3 < r.getImageCount()) planes2++;

      if (options.promptMergeOption(planes1, planes2) ==
        ImporterOptions.STATUS_OK)
      {
        String option = options.getMergeOption();
        if (option.indexOf("2 channels") != -1) {
          makeRGB(imp, r, 2, options.isAutoscale());
        }
        else if (option.indexOf("3 channels") != -1) {
          makeRGB(imp, r, 3, options.isAutoscale());
        }
      }
    }

    boolean splitC = options.isSplitChannels();
    boolean splitZ = options.isSplitFocalPlanes();
    boolean splitT = options.isSplitTimepoints();

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
        ru.setVar("fi", imp.getFileInfo());
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
      if (!concatenate) {
        imp.show();
        if (splitC || splitZ || splitT) {
          IJ.runPlugIn("loci.plugins.Slicer", "slice_z=" + splitZ +
            " slice_c=" + splitC + " slice_t=" + splitT +
            " stack_order=" + stackOrder + " keep_original=false");
        }
      }
      else imps.add(imp);
    }
  }

  /** Applies spatial calibrations to an image stack. */
  private void applyCalibration(OMEXMLMetadata store,
    ImagePlus imp, int series)
  {
    double xcal = Double.NaN, ycal = Double.NaN, zcal = Double.NaN;
    Integer ii = new Integer(series);

    Float xf = store.getPixelSizeX(ii);
    if (xf != null) xcal = xf.floatValue();
    Float yf = store.getPixelSizeY(ii);
    if (yf != null) ycal = yf.floatValue();
    Float zf = store.getPixelSizeZ(ii);
    if (zf != null) zcal = zf.floatValue();

    if (xcal == xcal || ycal == ycal || zcal == zcal) {
      Calibration cal = new Calibration();
      cal.setUnit("micron");
      cal.pixelWidth = xcal;
      cal.pixelHeight = ycal;
      cal.pixelDepth = zcal;
      imp.setCalibration(cal);
    }
  }

  private void makeRGB(ImagePlus imp, IFormatReader r, int c, boolean scale) {
    ImageStack s = imp.getStack();
    ImageStack newStack = new ImageStack(s.getWidth(), s.getHeight());
    for (int i=0; i<s.getSize(); i++) {
      ImageProcessor p = s.getProcessor(i + 1).convertToByte(true);
      newStack.addSlice(s.getSliceLabel(i + 1), p);
    }
    imp.setStack(imp.getTitle(), newStack);
    if (scale) Util.adjustColorRange(imp);

    s = imp.getStack();
    newStack = new ImageStack(s.getWidth(), s.getHeight());

    int sizeZ = r.getSizeZ();
    int sizeT = r.getSizeT();

    int[][] indices = new int[c][s.getSize() / c];
    int[] pt = new int[c];
    Arrays.fill(pt, 0);

    for (int i=0; i<s.getSize(); i++) {
      int[] zct = FormatTools.getZCTCoords(stackOrder, sizeZ,
        r.getEffectiveSizeC(), sizeT, r.getImageCount(), i);
      int cndx = zct[1];
      indices[cndx][pt[cndx]++] = i;
    }

    for (int i=0; i<indices[0].length; i++) {
      ColorProcessor cp = new ColorProcessor(s.getWidth(), s.getHeight());
      byte[][] bytes = new byte[indices.length][];
      for (int j=0; j<indices.length; j++) {
        bytes[j] = (byte[]) s.getProcessor(indices[j][i] + 1).getPixels();
      }
      cp.setRGB(bytes[0], bytes[1], bytes.length == 3 ? bytes[2] :
        new byte[s.getWidth() * s.getHeight()]);
      newStack.addSlice(s.getSliceLabel(
        indices[indices.length - 1][i] + 1), cp);
    }

    imp.setStack(imp.getTitle(), newStack);
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

  /** Verifies that the given status result is OK. */
  private boolean statusOk(int status) {
    if (status == ImporterOptions.STATUS_CANCELED) plugin.canceled = true;
    return status == ImporterOptions.STATUS_OK;
  }

  /** Reports the given exception with stack trace in an ImageJ error dialog. */
  private void reportException(Throwable t, boolean quiet, String msg) {
    t.printStackTrace();
    IJ.showStatus("");
    if (!quiet) {
      ByteArrayOutputStream buf = new ByteArrayOutputStream();
      t.printStackTrace(new PrintStream(buf));
      IJ.error("Bio-Formats", msg + ":\n" + buf.toString());
    }
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
