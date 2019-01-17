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

package loci.plugins.out;

import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Macro;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.io.FileInfo;
import ij.io.OpenDialog;
import ij.measure.Calibration;
import ij.plugin.frame.Recorder;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.LUT;
import ij.process.ShortProcessor;

import java.awt.Checkbox;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import loci.common.DataTools;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatWriter;
import loci.formats.ImageWriter;
import loci.formats.MetadataTools;
import loci.formats.gui.AWTImageTools;
import loci.formats.gui.ExtensionFileFilter;
import loci.formats.gui.GUITools;
import loci.formats.gui.Index16ColorModel;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;
import loci.plugins.BF;
import loci.plugins.LociExporter;
import loci.plugins.util.ROIHandler;
import loci.plugins.util.RecordedImageProcessor;
import loci.plugins.util.WindowTools;
import ome.units.UNITS;
import ome.units.quantity.Time;
import ome.xml.meta.OMEXMLMetadataRoot;
import ome.xml.model.ROI;
import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;

/**
 * Core logic for the Bio-Formats Exporter ImageJ plugin.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class Exporter {

    // -- Constants --

    private static final String ORDER = "XYCZT";

    // -- Fields --

    /** Current stack. */
    private ImagePlus imp;

    private LociExporter plugin;

    /**
     * Removes the extension of the specified name.
     *
     * @param name The name to handle.
     * @return See above.
     */
    private String removeExtension(String name)
    {
        int index = name.lastIndexOf(".");
        //check if separator
        int unixPos = name.lastIndexOf('/');
        int windowsPos = name.lastIndexOf('\\');
        int max = Math.max(unixPos, windowsPos);
        if (max < index) {
            return name.substring(0, index);
        }
        return name;
    }
    // -- Constructor --

    public Exporter(LociExporter plugin, ImagePlus imp) {
        this.plugin = plugin;
        this.imp = imp;
    }

    // -- Exporter API methods --

    /** Executes the plugin. */
    public void run() {
        String outfile = null;
        Boolean splitZ = null;
        Boolean splitC = null;
        Boolean splitT = null;
        Boolean padded = null;
        Boolean saveRoi = null;
        String compression = null;
        Boolean noLookupTables = null;

        Boolean windowless = Boolean.FALSE;
        if (plugin.arg != null) {
            outfile = Macro.getValue(plugin.arg, "outfile", null);

            String z = Macro.getValue(plugin.arg, "splitZ", null);
            String c = Macro.getValue(plugin.arg, "splitC", null);
            String t = Macro.getValue(plugin.arg, "splitT", null);
            String zeroPad = Macro.getValue(plugin.arg, "padded", null);
            String sr = Macro.getValue(plugin.arg, "saveRoi", null);
            compression = Macro.getValue(plugin.arg, "compression", null);
            String id = Macro.getValue(plugin.arg, "imageid", null);
            splitZ = z == null ? null : Boolean.valueOf(z);
            splitC = c == null ? null : Boolean.valueOf(c);
            splitT = t == null ? null : Boolean.valueOf(t);
            padded = zeroPad == null ? null : Boolean.valueOf(zeroPad);
            saveRoi = sr == null ? null : Boolean.valueOf(sr);
            if (id != null) {
                try {
                    int imageID = Integer.parseInt(id);
                    ImagePlus plus = WindowManager.getImage(imageID);
                    if (plus != null) imp = plus;
                } catch (Exception e) {
                    //nothing to do, we use the current imagePlus
                }
            }
            String w =  Macro.getValue(plugin.arg, "windowless", null);
            if (w != null) {
                windowless = Boolean.valueOf(w);
            }
            String lut = Macro.getValue(plugin.arg, "skip_luts", "false");
            noLookupTables = Boolean.valueOf(lut);
            plugin.arg = null;
        }
        if (outfile == null) {
            String options = Macro.getOptions();
            if (options != null) {
                String save = Macro.getValue(options, "save", null);
                if (save != null) outfile = save;
            }
        }
        //create a temporary file if window less
        if (windowless && (outfile == null || outfile.length() == 0)) {
            File tmp = null;
            try {
                String name = removeExtension(imp.getTitle());
                String n = name+ ".ome.tif";
                tmp = File.createTempFile(name, ".ome.tif");
                File p = tmp.getParentFile();
                File[] list = p.listFiles();
                //make sure we delete a previous tmp file with same name if any
                if (list != null) {
                    File toDelete = null;
                    for (int i = 0; i < list.length; i++) {
                        if (list[i].getName().equals(n)) {
                            toDelete = list[i];
                            break;
                        }
                    }
                    if (toDelete != null) {
                        toDelete.delete();
                    }
                }
                outfile = new File(p, n).getAbsolutePath();
                if (Recorder.record) Recorder.recordPath("outputfile", outfile);
                IJ.log("exporter outputfile "+outfile);
            } catch (Exception e) {
                //fall back to window mode.
            } finally {
                if (tmp != null) tmp.delete();
            }
        }
        File f = null;
        if (outfile == null || outfile.length() == 0) {
            // open a dialog prompting for the filename to save
            // NB: Copied and adapted from ij.io.SaveDIalog.jSaveDispatchThread,
            // so that the save dialog has a file filter for choosing output format.

            String dir = null, name = null;
            JFileChooser fc = GUITools.buildFileChooser(new ImageWriter(), false);
            fc.setDialogTitle("Bio-Formats Exporter");
            String defaultDir = OpenDialog.getDefaultDirectory();
            if (defaultDir != null) fc.setCurrentDirectory(new File(defaultDir));

            // set OME-TIFF as the default output format
            FileFilter[] ff = fc.getChoosableFileFilters();
            FileFilter defaultFilter = null;
            for (int i=0; i<ff.length; i++) {
                if (ff[i] instanceof ExtensionFileFilter) {
                    ExtensionFileFilter eff = (ExtensionFileFilter) ff[i];
                    if (i == 0 || eff.getExtension().equals("ome.tif")) {
                        defaultFilter = eff;
                        break;
                    }
                }
            }
            if (defaultFilter != null) fc.setFileFilter(defaultFilter);

            int returnVal = fc.showSaveDialog(IJ.getInstance());
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                Macro.abort();
                return;
            }
            f = fc.getSelectedFile();
            dir = fc.getCurrentDirectory().getPath() + File.separator;
            name = fc.getName(f);
            if (f.exists()) {
                int ret = JOptionPane.showConfirmDialog(fc,
                        "The file " + f.getName() + " already exists. \n" +
                                "Would you like to replace it?", "Replace?",
                                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (ret != JOptionPane.OK_OPTION) f = null;
            } else {
                // ensure filename matches selected filter
                FileFilter filter = fc.getFileFilter();
                if (filter instanceof ExtensionFileFilter) {
                    ExtensionFileFilter eff = (ExtensionFileFilter) filter;
                    String[] ext = eff.getExtensions();
                    String lName = name.toLowerCase();
                    boolean hasExtension = false;
                    for (int i=0; i<ext.length; i++) {
                        if (lName.endsWith("." + ext[i])) {
                            hasExtension = true;
                            break;
                        }
                    }
                    if (!hasExtension && ext.length > 0) {
                        // append chosen extension
                        name = name + "." + ext[0];
                    }

                    f = fc.getSelectedFile();
                    String filePath = f.getAbsolutePath();
                    if(!filePath.endsWith("." + ext[0])) {
                        f = new File(filePath + '.' + ext[0]);
                    }
                    if (f.exists()) {
                        int ret1 = JOptionPane.showConfirmDialog(fc,
                                "The file " + f.getName() + " already exists. \n" +
                                        "Would you like to replace it?", "Replace?",
                                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (ret1 != JOptionPane.OK_OPTION) f = null;
                    }

                }
            }
            if (f == null) Macro.abort();
            else {
                // do some ImageJ bookkeeping
                OpenDialog.setDefaultDirectory(dir);
                if (Recorder.record) Recorder.recordPath("save", dir+name);
            }

            if (dir == null || name == null) return;
            outfile = new File(dir, name).getAbsolutePath();
            if (outfile == null) return;
        }

        if (windowless) {
            if (splitZ == null) splitZ = Boolean.FALSE;
            if (splitC == null) splitC = Boolean.FALSE;
            if (splitT == null) splitT = Boolean.FALSE;
            if (padded == null) padded = Boolean.FALSE;
            if (noLookupTables == null) noLookupTables = false;
        }
        if (splitZ == null || splitC == null || splitT == null) {
            // ask if we want to export multiple files

            GenericDialog multiFile =
                    new GenericDialog("Bio-Formats Exporter - Multiple Files");
            multiFile.addCheckbox("Write_each_Z_section to a separate file", false);
            multiFile.addCheckbox("Write_each_timepoint to a separate file", false);
            multiFile.addCheckbox("Write_each_channel to a separate file", false);
            multiFile.addCheckbox("Use zero padding for filename indexes", false);
            // prompt for lookup tables here instead of in compression options window,
            // since the compression window won't be shown for formats with
            // only one compression type
            //
            // checkbox is "Do not save..." instead of "Save..." because macro
            // recording/parsing only looks for checkboxes that are enabled
            // this preserves backwards compatibility with existing macros,
            // so that non-default lookup tables do not suddenly disappear
            multiFile.addCheckbox("Do_not_save_lookup_tables", false);

            // only allow LUTs to be turned off via macro for now
            Vector checkboxes = multiFile.getCheckboxes();
            ((Checkbox) checkboxes.get(checkboxes.size() - 1)).setVisible(false);
            multiFile.showDialog();

            splitZ = multiFile.getNextBoolean();
            splitT = multiFile.getNextBoolean();
            splitC = multiFile.getNextBoolean();
            padded = multiFile.getNextBoolean();
            noLookupTables = multiFile.getNextBoolean();
            if (multiFile.wasCanceled()) return;
        }

        try (IFormatWriter w = new ImageWriter().getWriter(outfile)) {
            int ptype = 0;
            int channels = 1;
            switch (imp.getType()) {
                case ImagePlus.GRAY8:
                case ImagePlus.COLOR_256:
                    ptype = FormatTools.UINT8;
                    break;
                case ImagePlus.COLOR_RGB:
                    channels = 3;
                    ptype = FormatTools.UINT8;
                    break;
                case ImagePlus.GRAY16:
                    ptype = FormatTools.UINT16;
                    break;
                case ImagePlus.GRAY32:
                    ptype = FormatTools.FLOAT;
                    break;
            }
            String title = imp.getTitle();

            w.setWriteSequentially(true);
            FileInfo fi = imp.getOriginalFileInfo();
            String xml = fi == null ? null : fi.description == null ? null :
                fi.description.indexOf("xml") == -1 ? null : fi.description;

            OMEXMLService service = null;
            IMetadata store = null;

            try {
                ServiceFactory factory = new ServiceFactory();
                service = factory.getInstance(OMEXMLService.class);
                store = service.createOMEXMLMetadata(xml);
            }
            catch (DependencyException de) { }
            catch (ServiceException se) { }



            if (store == null) IJ.error("OME-XML Java library not found.");

            OMEXMLMetadataRoot root = (OMEXMLMetadataRoot) store.getRoot();
            if (root.sizeOfROIList()>0){
                while (root.sizeOfROIList() > 0) {
                    ROI roi = root.getROI(0);
                    root.removeROI(roi);
                }
                store.setRoot(root);
            }
            if (xml == null) {
                store.createRoot();
            }
            else if (store.getImageCount() > 1) {
                // the original dataset had multiple series
                // we need to modify the IMetadata to represent the correct series
                ArrayList<Integer> matchingSeries = new ArrayList<Integer>();
                for (int series=0; series<store.getImageCount(); series++) {
                    String type = store.getPixelsType(series).toString();
                    int pixelType = FormatTools.pixelTypeFromString(type);
                    if (pixelType == ptype) {
                        String imageName = store.getImageName(series);
                        if (title.indexOf(imageName) >= 0) {
                            matchingSeries.add(series);
                        }
                    }
                }

                int series = 0;
                if (matchingSeries.size() > 1) {
                    for (int i=0; i<matchingSeries.size(); i++) {
                        int index = matchingSeries.get(i);
                        String name = store.getImageName(index);
                        boolean valid = true;
                        for (int j=0; j<matchingSeries.size(); j++) {
                            if (i != j) {
                                String compName = store.getImageName(matchingSeries.get(j));
                                if (compName.indexOf(name) >= 0) {
                                    valid = false;
                                    break;
                                }
                            }
                        }
                        if (valid) {
                            series = index;
                            break;
                        }
                    }
                }
                else if (matchingSeries.size() == 1) series = matchingSeries.get(0);

                ome.xml.model.Image exportImage = root.getImage(series);
                List<ome.xml.model.Image> allImages = root.copyImageList();
                for (ome.xml.model.Image img : allImages) {
                    if (!img.equals(exportImage)) {
                        root.removeImage(img);
                    }
                }
                store.setRoot(root);
            }

            store.setPixelsSizeX(new PositiveInteger(imp.getWidth()), 0);
            store.setPixelsSizeY(new PositiveInteger(imp.getHeight()), 0);
            store.setPixelsSizeZ(new PositiveInteger(imp.getNSlices()), 0);
            store.setPixelsSizeC(new PositiveInteger(channels*imp.getNChannels()), 0);
            store.setPixelsSizeT(new PositiveInteger(imp.getNFrames()), 0);

            if (store.getImageID(0) == null) {
                store.setImageID(MetadataTools.createLSID("Image", 0), 0);
            }
            if (store.getPixelsID(0) == null) {
                store.setPixelsID(MetadataTools.createLSID("Pixels", 0), 0);
            }

            // reset the pixel type, unless the only change is signedness
            // this prevents problems if the user changed the bit depth of the image
            boolean applyCalibrationFunction = false;
            try {
                int originalType = -1;
                if (store.getPixelsType(0) != null) {
                  originalType = FormatTools.pixelTypeFromString(
                  store.getPixelsType(0).toString());
                }
                if (ptype != originalType &&
                  (store.getPixelsType(0) == null ||
                  !FormatTools.isSigned(originalType) ||
                  FormatTools.getBytesPerPixel(originalType) !=
                  FormatTools.getBytesPerPixel(ptype)))
                {
                  store.setPixelsType(PixelType.fromString(
                    FormatTools.getPixelTypeString(ptype)), 0);
                }
                else if (FormatTools.isSigned(originalType)) {
                  applyCalibrationFunction = true;
                }
            }
            catch (EnumerationException e) { }

            if (store.getPixelsBinDataCount(0) == 0 ||
                    store.getPixelsBinDataBigEndian(0, 0) == null)
            {
                store.setPixelsBinDataBigEndian(Boolean.FALSE, 0, 0);
            }
            if (store.getPixelsDimensionOrder(0) == null) {
                try {
                    store.setPixelsDimensionOrder(DimensionOrder.fromString(ORDER), 0);
                }
                catch (EnumerationException e) { }
            }

            LUT[] luts = new LUT[imp.getNChannels()];

            for (int c=0; c<imp.getNChannels(); c++) {
                if (c >= store.getChannelCount(0) || store.getChannelID(0, c) == null) {
                    String lsid = MetadataTools.createLSID("Channel", 0, c);
                    store.setChannelID(lsid, 0, c);
                }
                store.setChannelSamplesPerPixel(new PositiveInteger(channels), 0, 0);

                if (imp instanceof CompositeImage) {
                    luts[c] = ((CompositeImage) imp).getChannelLut(c + 1);
                }
            }

            Calibration cal = imp.getCalibration();

            store.setPixelsPhysicalSizeX(FormatTools.getPhysicalSizeX(cal.pixelWidth, cal.getXUnit()), 0);
            store.setPixelsPhysicalSizeY(FormatTools.getPhysicalSizeY(cal.pixelHeight, cal.getYUnit()), 0);
            store.setPixelsPhysicalSizeZ(FormatTools.getPhysicalSizeZ(cal.pixelDepth, cal.getZUnit()), 0);
            store.setPixelsTimeIncrement(FormatTools.getTime(new Double(cal.frameInterval), cal.getTimeUnit()), 0);

            if (imp.getImageStackSize() !=
                    imp.getNChannels() * imp.getNSlices() * imp.getNFrames())
            {
                if (!windowless) {
                    IJ.showMessageWithCancel("Bio-Formats Exporter Warning",
                            "The number of planes in the stack (" + imp.getImageStackSize() +
                            ") does not match the number of expected planes (" +
                            (imp.getNChannels() * imp.getNSlices() * imp.getNFrames()) + ")." +
                            "\nIf you select 'OK', only " + imp.getImageStackSize() +
                            " planes will be exported. If you wish to export all of the " +
                            "planes,\nselect 'Cancel' and convert the Image5D window " +
                            "to a stack.");
                }
                store.setPixelsSizeZ(new PositiveInteger(imp.getImageStackSize()), 0);
                store.setPixelsSizeC(new PositiveInteger(1), 0);
                store.setPixelsSizeT(new PositiveInteger(1), 0);
            }

            Object info = imp.getProperty("Info");
            if (info != null) {
                String imageInfo = info.toString();
                if (imageInfo != null) {
                    String[] lines = imageInfo.split("\n");
                    for (String line : lines) {
                        int eq = line.lastIndexOf("=");
                        if (eq > 0) {
                            String key = line.substring(0, eq).trim();
                            String value = line.substring(eq + 1).trim();

                            if (key.endsWith("BitsPerPixel")) {
                                w.setValidBitsPerPixel(Integer.parseInt(value));
                                break;
                            }
                        }
                    }
                }
            }

            // NB: Animation rate code copied from ij.plugin.Animator#doOptions().
            final int rate;
            if (cal.fps != 0.0) {
                rate = (int) cal.fps;
            }
            else if (cal.frameInterval != 0.0 && cal.getTimeUnit().equals("sec")) {
                rate = (int) (1.0 / cal.frameInterval);
            }
            else {
                // NB: Code from ij.plugin.Animator#animationRate initializer.
                // The value is 7 by default in ImageJ, so must be 7 here as well.
                rate = (int) Prefs.getDouble(Prefs.FPS, 7.0);
            }
            if (rate > 0) w.setFramesPerSecond(rate);

            String[] outputFiles = new String[] {outfile};

            int sizeZ = store.getPixelsSizeZ(0).getValue();
            int sizeC = store.getPixelsSizeC(0).getValue();
            int sizeT = store.getPixelsSizeT(0).getValue();

            if (splitZ || splitC || splitT) {
                int nFiles = 1;
                if (splitZ) {
                    nFiles *= sizeZ;
                }
                if (splitC) {
                    nFiles *= sizeC;
                }
                if (splitT) {
                    nFiles *= sizeT;
                }

                outputFiles = new String[nFiles];

                int dot = outfile.indexOf(".", outfile.lastIndexOf(File.separator));
                String base = outfile.substring(0, dot);
                String ext = outfile.substring(dot);

                int nextFile = 0;
                for (int t=0; t<(splitT ? sizeT : 1); t++) {
                    for (int z=0; z<(splitZ ? sizeZ : 1); z++) {
                        for (int c=0; c<(splitC ? sizeC : 1); c++) {
                            int index = FormatTools.getIndex(ORDER, sizeZ, sizeC, sizeT, sizeZ*sizeC*sizeT, z, c, t);
                            String pattern = base + (splitZ ? "_Z%z" : "") + (splitC ? "_C%c" : "") + (splitT ? "_T%t" : "") + ext;
                            outputFiles[nextFile++] = FormatTools.getFilename(0, index, store, pattern, padded);
                        }
                    }
                }
            }

            if (!w.getFormat().startsWith("OME")) {
                if (splitZ) {
                    store.setPixelsSizeZ(new PositiveInteger(1), 0);
                }
                if (splitC) {
                    store.setPixelsSizeC(new PositiveInteger(1), 0);
                }
                if (splitT) {
                    store.setPixelsSizeT(new PositiveInteger(1), 0);
                }
            }

            // prompt for options

            String[] codecs = w.getCompressionTypes();
            ImageProcessor proc = imp.getImageStack().getProcessor(1);
            Image firstImage = proc.createImage();
            firstImage = AWTImageTools.makeBuffered(firstImage, proc.getColorModel());
            int thisType = AWTImageTools.getPixelType((BufferedImage) firstImage);
            if (proc instanceof ColorProcessor) {
                thisType = FormatTools.UINT8;
            }
            else if (proc instanceof ShortProcessor) {
                thisType = FormatTools.UINT16;
            }

            boolean notSupportedType = !w.isSupportedType(thisType);
            if (notSupportedType) {
                IJ.error("Pixel type (" + FormatTools.getPixelTypeString(thisType) +
                        ") not supported by this format.");
                return;
            }

            if (codecs != null && codecs.length > 1) {
                boolean selected = false;
                if (compression != null) {
                    for (int i = 0; i < codecs.length; i++) {
                        if (codecs[i].equals(compression)) {
                            selected = true;
                            break;
                        }
                    }
                }
                if (!selected && !windowless) {
                    GenericDialog gd =
                            new GenericDialog("Bio-Formats Exporter Options");

                    gd.addChoice("Compression type: ", codecs, codecs[0]);
                    if (saveRoi != null) {
                        gd.addCheckbox("Export ROIs", saveRoi.booleanValue());
                    } else {
                        gd.addCheckbox("Export ROIs", true);
                    }
                    gd.showDialog();
                    saveRoi = gd.getNextBoolean();

                    if (gd.wasCanceled()) return;
                    compression = gd.getNextChoice();
                }
            }
            boolean in = false;
            if (outputFiles.length > 1) {
                for (int i = 0; i < outputFiles.length; i++) {
                    if (new File(outputFiles[i]).exists()) {
                        in = true;
                        break;
                    }
                }
            }
            if (in && !windowless) {
                int ret1 = JOptionPane.showConfirmDialog(null,
                        "Some files already exist. \n" +
                                "Would you like to replace them?", "Replace?",
                                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (ret1 != JOptionPane.OK_OPTION) {
                    return;
                }
                //Delete the files overwrite does not correctly work
                for (int i = 0; i < outputFiles.length; i++) {
                    new File(outputFiles[i]).delete();
                }
            }
            //We are now ready to write the image
            if (f != null) f.delete(); //delete the file.
            if (compression != null) {
                w.setCompression(compression);
            }
            //Save ROI's
            if (saveRoi != null && saveRoi.booleanValue()) {
                ROIHandler.saveROIs(store);
            }
            w.setMetadataRetrieve(store);
            // convert and save slices

            int size = imp.getImageStackSize();
            ImageStack is = imp.getImageStack();
            boolean doStack = w.canDoStacks() && size > 1;
            int start = doStack ? 0 : imp.getCurrentSlice() - 1;
            int end = doStack ? size : start + 1;

            boolean littleEndian = false;
            if (w.getMetadataRetrieve().getPixelsBigEndian(0) != null)
            {
              littleEndian = !w.getMetadataRetrieve().getPixelsBigEndian(0).booleanValue();
            }
            else if (w.getMetadataRetrieve().getPixelsBinDataCount(0) == 0) {
              littleEndian = !w.getMetadataRetrieve().getPixelsBinDataBigEndian(0, 0).booleanValue();
            }
            byte[] plane = null;
            w.setInterleaved(false);

            int[] no = new int[outputFiles.length];
            for (int i=start; i<end; i++) {
                if (doStack) {
                    BF.status(false, "Saving plane " + (i + 1) + "/" + size);
                    BF.progress(false, i, size);
                }
                else BF.status(false, "Saving image");
                proc = is.getProcessor(i + 1);

                if (proc instanceof RecordedImageProcessor) {
                    proc = ((RecordedImageProcessor) proc).getChild();
                }

                int x = proc.getWidth();
                int y = proc.getHeight();

                if (proc instanceof ByteProcessor) {
                    if (applyCalibrationFunction) {
                      // don't alter 'pixels' directly as that will
                      // affect the open ImagePlus
                      byte[] pixels = (byte[]) proc.getPixels();
                      plane = new byte[pixels.length];
                      float[] calibration = proc.getCalibrationTable();
                      for (int pixel=0; pixel<pixels.length; pixel++) {
                        plane[pixel] = (byte) calibration[pixels[pixel] & 0xff];
                      }
                    }
                    else {
                      plane = (byte[]) proc.getPixels();
                    }
                }
                else if (proc instanceof ShortProcessor) {
                    short[] pixels = (short[]) proc.getPixels();
                    if (applyCalibrationFunction) {
                      // don't alter 'pixels' directly as that will
                      // affect the open ImagePlus
                      plane = new byte[pixels.length * 2];
                      float[] calibration = proc.getCalibrationTable();
                      for (int pixel=0; pixel<pixels.length; pixel++) {
                        short v = (short) calibration[pixels[pixel] & 0xffff];
                        DataTools.unpackBytes(
                          v, plane, pixel * 2, 2, littleEndian);
                      }
                    }
                    else {
                      plane = DataTools.shortsToBytes(pixels, littleEndian);
                    }
                }
                else if (proc instanceof FloatProcessor) {
                    plane = DataTools.floatsToBytes(
                            (float[]) proc.getPixels(), littleEndian);
                }
                else if (proc instanceof ColorProcessor) {
                    byte[][] pix = new byte[3][x*y];
                    ((ColorProcessor) proc).getRGB(pix[0], pix[1], pix[2]);
                    plane = new byte[3 * x * y];
                    System.arraycopy(pix[0], 0, plane, 0, x * y);
                    System.arraycopy(pix[1], 0, plane, x * y, x * y);
                    System.arraycopy(pix[2], 0, plane, 2 * x * y, x * y);

                    if (i == start) {
                        sizeC /= 3;
                    }
                }

                int fileIndex = 0;
                if (doStack) {
                    int[] coords =
                            FormatTools.getZCTCoords(ORDER, sizeZ, sizeC, sizeT, size, i);
                    int realZ = sizeZ;
                    int realC = sizeC;
                    int realT = sizeT;

                    if (!splitZ) {
                        coords[0] = 0;
                        realZ = 1;
                    }
                    if (!splitC) {
                        coords[1] = 0;
                        realC = 1;
                    }
                    if (!splitT) {
                        coords[2] = 0;
                        realT = 1;
                    }
                    fileIndex = FormatTools.getIndex(ORDER, realZ, realC, realT,
                            realZ * realC * realT, coords[0], coords[1], coords[2]);
                }

                if (notSupportedType) {
                    IJ.error("Pixel type not supported by this format.");
                }
                else {
                    w.changeOutputFile(outputFiles[fileIndex]);

                    int currentChannel = FormatTools.getZCTCoords(
                            ORDER, sizeZ, sizeC, sizeT, imp.getStackSize(), i)[1];

                    // only save the lookup table if it is not the default grayscale LUT
                    // saving a LUT for every plane can cause performance issues,
                    // especially for 16 bit data
                    // see https://trello.com/c/Qk6NBnPs/92-imagej-ome-tiff-writing-performance
                    if (!proc.isDefaultLut() && (noLookupTables == null || !noLookupTables)) {
                        if (luts[currentChannel] != null) {
                            // expand to 16-bit LUT if necessary

                            int bpp = FormatTools.getBytesPerPixel(thisType);
                            if (bpp == 1) {
                                w.setColorModel(luts[currentChannel]);
                            }
                            else if (bpp == 2) {
                                int lutSize = luts[currentChannel].getMapSize();
                                byte[][] lut = new byte[3][lutSize];
                                luts[currentChannel].getReds(lut[0]);
                                luts[currentChannel].getGreens(lut[1]);
                                luts[currentChannel].getBlues(lut[2]);

                                short[][] newLut = new short[3][65536];
                                int bins = newLut[0].length / lut[0].length;
                                for (int c=0; c<newLut.length; c++) {
                                    for (int q=0; q<newLut[c].length; q++) {
                                        int index = q / bins;
                                        newLut[c][q] = (short) ((lut[c][index] * lut[0].length) + (q % bins));
                                    }
                                }

                                w.setColorModel(new Index16ColorModel(16, newLut[0].length,
                                    newLut, littleEndian));
                            }
                        }
                        else {
                            w.setColorModel(proc.getColorModel());
                        }
                    }
                    w.saveBytes(no[fileIndex]++, plane);
                }
            }
            w.close();
        }
        catch (FormatException e) {
            WindowTools.reportException(e);
        }
        catch (IOException e) {
            WindowTools.reportException(e);
        }
    }

}
