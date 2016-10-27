/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2016 Open Microscopy Environment:
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

package loci.plugins.macro;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.io.IOException;
import java.util.Arrays;

import loci.common.Region;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.ChannelSeparator;
import loci.formats.FileStitcher;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.Modulo;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.services.OMEXMLService;
import loci.plugins.BF;
import loci.plugins.in.Calibrator;
import loci.plugins.in.ImagePlusReader;
import loci.plugins.in.ImportProcess;
import loci.plugins.in.ImporterOptions;
import loci.plugins.util.ImageProcessorReader;
import loci.plugins.util.LociPrefs;

import ome.xml.model.primitives.PositiveFloat;

import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.UNITS;

/**
 * This class provides macro extensions in ImageJ for Bio-Formats.
 * Currently, it is a fairly tight mirror to the
 * {@link loci.formats.IFormatReader} interface, with some additional
 * functions to control the type of format reader used.
 *
 * Note that public methods in this class can only accept parameters of String,
 * Double, String[], Double[], and Object[] types.  Anything else will prevent
 * the method from being usable within a macro.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class LociFunctions extends MacroFunctions {

  // -- Constants --

  /** URL for Javadocs. */
  public static final String URL_JAVADOCS =
    "http://ci.openmicroscopy.org/job/BIOFORMATS-5.1-latest/javadoc/";

  // -- Fields --

  private ImageProcessorReader r;
  private int series = 0;

  // -- Constructor --

  public LociFunctions() {
    boolean groupFiles = true;
    try {
      ImporterOptions options = new ImporterOptions();
      groupFiles = options.isGroupFiles();
    }
    catch (IOException exc) {
      IJ.handleException(exc);
    }
    if (groupFiles) {
      r = new ImageProcessorReader(new ChannelSeparator(
        new FileStitcher(LociPrefs.makeImageReader(), true)));
    }
    else {
      r = new ImageProcessorReader(new ChannelSeparator(
        LociPrefs.makeImageReader()));
    }
    try {
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      r.setMetadataStore(service.createOMEXMLMetadata());
    }
    catch (DependencyException de) { }
    catch (ServiceException se) { }
  }

  // -- LociFunctions API methods - version numbers --

  /**
   * @deprecated Use the general {@link #getVersionNumber(String[])} method.
   */
  public void getRevision(String[] revision) {
    revision[0] = FormatTools.VERSION;
  }

  public void getBuildDate(String[] date) {
    date[0] = FormatTools.DATE;
  }

  public void getVersionNumber(String[] version) {
    version[0] = FormatTools.VERSION;
  }

  // -- LociFunctions API methods - loci.formats.IFormatReader --

  public void getImageCount(Double[] imageCount) {
    imageCount[0] = new Double(r.getImageCount());
  }

  public void getSizeX(Double[] sizeX) { sizeX[0] = new Double(r.getSizeX()); }
  public void getSizeY(Double[] sizeY) { sizeY[0] = new Double(r.getSizeY()); }
  public void getSizeZ(Double[] sizeZ) { sizeZ[0] = new Double(r.getSizeZ()); }
  public void getSizeC(Double[] sizeC) { sizeC[0] = new Double(r.getSizeC()); }
  public void getSizeT(Double[] sizeT) { sizeT[0] = new Double(r.getSizeT()); }

  public void getPixelType(String[] pixelType) {
    pixelType[0] = FormatTools.getPixelTypeString(r.getPixelType());
  }

  public void getEffectiveSizeC(Double[] effectiveSizeC) {
    effectiveSizeC[0] = new Double(r.getEffectiveSizeC());
  }

  public void getRGBChannelCount(Double[] rgbChannelCount) {
    rgbChannelCount[0] = new Double(r.getRGBChannelCount());
  }

  public void isIndexed(String[] indexed) {
    indexed[0] = r.isIndexed() ? "true" : "false";
  }

  public void getChannelDimCount(Double[] channelDimCount) {
    Modulo moduloC = r.getModuloC();
    channelDimCount[0] = new Double(moduloC.length() > 1 ? 2 : 1);
  }

  public void getChannelDimLength(Double i, Double[] channelDimLength) {
    Modulo moduloC = r.getModuloC();
    if (i.intValue() == 0) { // index 0
      channelDimLength[0] = new Double(moduloC.length() > 1 ? r.getSizeC() / moduloC.length() : r.getSizeC());
    } else { // index 1
      channelDimLength[0] = new Double(moduloC.length());
    }
  }

  public void getChannelDimType(Double i, Double[] channelDimType) {
    Modulo moduloC = r.getModuloC();
    if (i.intValue() == 0) { // index 0
      channelDimType[0] = new Double(moduloC.length() > 1 ? moduloC.parentType : FormatTools.CHANNEL);
    } else { // index 1
      channelDimType[0] = new Double(moduloC.type);
    }
  }

//  public void getThumbSizeX(Double[] thumbSizeX) {
//    thumbSizeX[0] = new Double(r.getThumbSizeX());
//  }

//  public void getThumbSizeY(Double[] thumbSizeY) {
//    thumbSizeY[0] = new Double(r.getThumbSizeY());
//  }

  public void isLittleEndian(String[] littleEndian) {
    littleEndian[0] = r.isLittleEndian() ? "true" : "false";
  }

  public void getDimensionOrder(String[] dimOrder) {
    dimOrder[0] = r.getDimensionOrder();
  }

  public void isOrderCertain(String[] orderCertain) {
    orderCertain[0] = r.isOrderCertain() ? "true" : "false";
  }

  public void isInterleaved(String[] interleaved) {
    interleaved[0] = r.isInterleaved() ? "true" : "false";
  }

  public void isInterleavedSubC(Double subC, String[] interleaved) {
    interleaved[0] = r.isInterleaved(subC.intValue()) ? "true" : "false";
  }

  public void openImagePlus(String path) {
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setSeriesOn(series, true);
      imps = BF.openImagePlus(options);
      for (ImagePlus imp : imps) imp.show();
    }
    catch (IOException exc) {
      IJ.handleException(exc);
    }
    catch (FormatException exc) {
      IJ.handleException(exc);
    }
  }

  public void openThumbImagePlus(String path) {
    ImagePlus[] imps = null;
    try {
      ImporterOptions options = new ImporterOptions();
      options.setId(path);
      options.setSeriesOn(series, true);
      imps = BF.openThumbImagePlus(options);
      for (ImagePlus imp : imps) imp.show();
    }
    catch (IOException exc) {
      IJ.handleException(exc);
    }
    catch (FormatException exc) {
      IJ.handleException(exc);
    }
  }

  public void openThumbImage(String title, Double no)
    throws FormatException, IOException
  {
    ImporterOptions options = new ImporterOptions();
    options.setWindowless(true);
    options.setId(r.getCurrentFile());
    options.setCrop(true);
    options.setSpecifyRanges(true);
    options.setSeriesOn(r.getSeries(), true);

    int[] zct = r.getZCTCoords(no.intValue());
    options.setCBegin(r.getSeries(), zct[1]);
    options.setZBegin(r.getSeries(), zct[0]);
    options.setTBegin(r.getSeries(), zct[2]);
    options.setCEnd(r.getSeries(), zct[1]);
    options.setZEnd(r.getSeries(), zct[0]);
    options.setTEnd(r.getSeries(), zct[2]);

    ImportProcess process = new ImportProcess(options);
    process.execute();

    ImagePlusReader reader = new ImagePlusReader(process);
    final ImagePlus imp = reader.openThumbImagePlus()[0];
    Calibrator calibrator = new Calibrator(process);
    calibrator.applyCalibration(imp);
    process.getReader().close();
    imp.show();
  }

  public void openImage(String title, Double no)
    throws FormatException, IOException
  {
    openSubImage(title, no, 0d, 0d,
      new Double(r.getSizeX()), new Double(r.getSizeY()));
  }

  public void openSubImage(String title, Double no, Double x, Double y,
    Double w, Double h) throws FormatException, IOException
  {
    ImporterOptions options = new ImporterOptions();
    options.setWindowless(true);
    options.setId(r.getCurrentFile());
    options.setCrop(true);
    options.setSpecifyRanges(true);
    options.setSeriesOn(r.getSeries(), true);

    int[] zct = r.getZCTCoords(no.intValue());
    options.setCBegin(r.getSeries(), zct[1]);
    options.setZBegin(r.getSeries(), zct[0]);
    options.setTBegin(r.getSeries(), zct[2]);
    options.setCEnd(r.getSeries(), zct[1]);
    options.setZEnd(r.getSeries(), zct[0]);
    options.setTEnd(r.getSeries(), zct[2]);

    Region region =
      new Region(x.intValue(), y.intValue(), w.intValue(), h.intValue());
    options.setCropRegion(r.getSeries(), region);

    ImportProcess process = new ImportProcess(options);
    process.execute();

    ImagePlusReader reader = new ImagePlusReader(process);
    final ImagePlus imp = reader.openImagePlus()[0];
    Calibrator calibrator = new Calibrator(process);
    calibrator.applyCalibration(imp);
    process.getReader().close();
    imp.show();
  }

  public void close() throws IOException { r.close(); }
  public void closeFileOnly() throws IOException { r.close(true); }

  public void getSeriesCount(Double[] seriesCount) {
    seriesCount[0] = new Double(r.getSeriesCount());
  }

  public void setSeries(Double seriesNum) {
    if (seriesNum != null && seriesNum >= 0) {
      series = seriesNum.intValue();
      if (r.getCurrentFile() != null) {
        r.setSeries(series);
      }
    }
  }

  public void getSeries(Double[] seriesNum) {
    seriesNum[0] = new Double(r.getSeries());
  }

  public void setNormalized(Boolean normalize) {
    r.setNormalized(normalize.booleanValue());
  }

  public void isNormalized(Boolean[] normalize) {
    normalize[0] = new Boolean(r.isNormalized());
  }

  public void setOriginalMetadataPopulated(Boolean populate) {
    r.setOriginalMetadataPopulated(populate.booleanValue());
  }

  public void isOriginalMetadataPopulated(Boolean[] populate) {
    populate[0] = new Boolean(r.isOriginalMetadataPopulated());
  }

  public void setGroupFiles(String groupFiles) {
    r.setGroupFiles("true".equalsIgnoreCase(groupFiles));
  }

  public void isGroupFiles(String[] groupFiles) {
    groupFiles[0] = r.isGroupFiles() ? "true" : "false";
  }

  public void isMetadataComplete(String[] complete) {
    complete[0] = r.isMetadataComplete() ? "true" : "false";
  }

  public void fileGroupOption(String id, String[] fileGroupOption)
    throws FormatException, IOException
  {
    switch (r.fileGroupOption(id)) {
      case IFormatReader.MUST_GROUP:
        fileGroupOption[0] = "must";
        break;
      case IFormatReader.CAN_GROUP:
        fileGroupOption[0] = "can";
        break;
      case IFormatReader.CANNOT_GROUP:
        fileGroupOption[0] = "cannot";
        break;
      default:
        fileGroupOption[0] = "unknown";
    }
  }

  public void getUsedFileCount(Double[] count) {
    count[0] = new Double(r.getUsedFiles().length);
  }

  public void getUsedFile(Double i, String[] used) {
    used[0] = r.getUsedFiles()[i.intValue()];
  }

  public void getCurrentFile(String[] file) {
    file[0] = r.getCurrentFile();
  }

  public void getIndex(Double z, Double c, Double t, Double[] index) {
    index[0] = new Double(r.getIndex(z.intValue(), c.intValue(), t.intValue()));
  }

  public void getZCTCoords(Double index, Double[] z, Double[] c, Double[] t) {
    int[] zct = r.getZCTCoords(index.intValue());
    z[0] = new Double(zct[0]);
    c[0] = new Double(zct[1]);
    t[0] = new Double(zct[2]);
  }

  public void getMetadataValue(String field, String[] value) {
    Object o = r.getMetadataValue(field);
    value[0] = o == null ? null : o.toString();
  }

  public void getSeriesMetadataValue(String field, String[] value) {
    Object o = r.getSeriesMetadataValue(field);
    value[0] = o == null ? null : o.toString();
  }

  public void setMetadataFiltered(String metadataFiltered) {
    r.setMetadataFiltered("true".equalsIgnoreCase(metadataFiltered));
  }

  public void isMetadataFiltered(String[] metadataFiltered) {
    metadataFiltered[0] = r.isMetadataFiltered() ? "true" : "false";
  }

  // -- LociFunctions API methods - additional methods --

  public void getFormat(String id, String[] format)
    throws FormatException, IOException
  {
    ImageReader reader = new ImageReader();
    format[0] = reader.getFormat(id);
  }

  public void setId(String id) throws FormatException, IOException {
    r.setId(id);
  }

  public void isThisType(String name, String[] thisType) {
    thisType[0] = r.isThisType(name) ? "true" : "false";
  }

  public void isThisTypeFast(String name, String[] thisType) {
    thisType[0] = r.isThisType(name, false) ? "true" : "false";
  }

  public void getSeriesName(String[] seriesName) {
    MetadataRetrieve retrieve = (MetadataRetrieve) r.getMetadataStore();
    seriesName[0] = retrieve.getImageName(r.getSeries());
  }

  public void getImageCreationDate(String[] creationDate) {
    MetadataRetrieve retrieve = (MetadataRetrieve) r.getMetadataStore();
    if (retrieve.getImageAcquisitionDate(r.getSeries()) != null) {
      creationDate[0] = retrieve.getImageAcquisitionDate(r.getSeries()).getValue();
    }
  }

  public void getPlaneTimingDeltaT(Double[] deltaT, Double no) {
    int imageIndex = r.getSeries();
    int planeIndex = getPlaneIndex(r, no.intValue());
    MetadataRetrieve retrieve = (MetadataRetrieve) r.getMetadataStore();
    Double val = Double.NaN;
    if (planeIndex >= 0) {
      Time valTime = retrieve.getPlaneDeltaT(imageIndex, planeIndex);
      if (valTime != null ) {
        val = valTime.value(UNITS.SECOND).doubleValue();
      }
    }
    deltaT[0] = val;
  }

  public void getPlaneTimingExposureTime(Double[] exposureTime, Double no) {
    int imageIndex = r.getSeries();
    int planeIndex = getPlaneIndex(r, no.intValue());
    MetadataRetrieve retrieve = (MetadataRetrieve) r.getMetadataStore();
    Double val = null;
    if (planeIndex >= 0) {
      val = retrieve.getPlaneExposureTime(imageIndex, planeIndex).value(UNITS.SECOND).doubleValue();
    }
    exposureTime[0] = val == null ? new Double(Double.NaN) : val;
  }

  public void getPlanePositionX(Double[] positionX, Double no) {
    int imageIndex = r.getSeries();
    int planeIndex = getPlaneIndex(r, no.intValue());
    MetadataRetrieve retrieve = (MetadataRetrieve) r.getMetadataStore();
    Length val = null;
    if (planeIndex >= 0) {
      val = retrieve.getPlanePositionX(imageIndex, planeIndex);
    }
    positionX[0] =
      val == null ? Double.NaN : val.value(UNITS.REFERENCEFRAME).doubleValue();
  }

  public void getPlanePositionY(Double[] positionY, Double no) {
    int imageIndex = r.getSeries();
    int planeIndex = getPlaneIndex(r, no.intValue());
    MetadataRetrieve retrieve = (MetadataRetrieve) r.getMetadataStore();
    Length val = null;
    if (planeIndex >= 0) {
      val = retrieve.getPlanePositionY(imageIndex, planeIndex);
    }
    if (val == null) {
        val = new Length(Double.NaN, UNITS.REFERENCEFRAME);
    }
    positionY[0] =
      val == null ? Double.NaN : val.value(UNITS.REFERENCEFRAME).doubleValue();
  }

  public void getPlanePositionZ(Double[] positionZ, Double no) {
    int imageIndex = r.getSeries();
    int planeIndex = getPlaneIndex(r, no.intValue());
    MetadataRetrieve retrieve = (MetadataRetrieve) r.getMetadataStore();
    Length val = null;
    if (planeIndex >= 0) {
      val = retrieve.getPlanePositionZ(imageIndex, planeIndex);
    }
    if (val == null) {
        val = new Length(Double.NaN, UNITS.REFERENCEFRAME);
    }
    positionZ[0] =
      val == null ? Double.NaN : val.value(UNITS.REFERENCEFRAME).doubleValue();
  }

  public void getPixelsPhysicalSizeX(Double[] sizeX) {
    int imageIndex = r.getSeries();
    MetadataRetrieve retrieve = (MetadataRetrieve) r.getMetadataStore();
    Length x = retrieve.getPixelsPhysicalSizeX(imageIndex);
    if (x != null) {
      sizeX[0] = x.value(UNITS.MICROMETER).doubleValue();
    }
    if (sizeX[0] == null) sizeX[0] = new Double(Double.NaN);
  }

  public void getPixelsPhysicalSizeY(Double[] sizeY) {
    int imageIndex = r.getSeries();
    MetadataRetrieve retrieve = (MetadataRetrieve) r.getMetadataStore();
    Length y = retrieve.getPixelsPhysicalSizeY(imageIndex);
    if (y != null) {
      sizeY[0] = y.value(UNITS.MICROMETER).doubleValue();
    }
    if (sizeY[0] == null) sizeY[0] = new Double(Double.NaN);
  }

  public void getPixelsPhysicalSizeZ(Double[] sizeZ) {
    int imageIndex = r.getSeries();
    MetadataRetrieve retrieve = (MetadataRetrieve) r.getMetadataStore();
    Length z = retrieve.getPixelsPhysicalSizeZ(imageIndex);
    if (z != null) {
      sizeZ[0] = z.value(UNITS.MICROMETER).doubleValue();
    }
    if (sizeZ[0] == null) sizeZ[0] = new Double(Double.NaN);
  }

  public void getPixelsTimeIncrement(Double[] sizeT) {
    int imageIndex = r.getSeries();
    MetadataRetrieve retrieve = (MetadataRetrieve) r.getMetadataStore();
    sizeT[0] = retrieve.getPixelsTimeIncrement(imageIndex).value(UNITS.SECOND).doubleValue();
    if (sizeT[0] == null) sizeT[0] = new Double(Double.NaN);
  }

  // -- PlugIn API methods --

  @Override
  public void run(String arg) {
    if (IJ.macroRunning()) super.run(arg);
    else {
      IJ.showMessage("Bio-Formats Plugins for ImageJ",
        "The macro extensions are designed to be used within a macro.\n" +
        "Instructions on doing so will be printed to the Results window.");

      IJ.log("To gain access to more advanced features of Bio-Formats");
      IJ.log("from within a macro, put the following line at the");
      IJ.log("beginning of your macro:");
      IJ.log("");
      IJ.log("run(\"Bio-Formats Macro Extensions\");");
      IJ.log("");
      IJ.log("This will enable the following macro functions:");
      IJ.log("");
      IJ.log("-= Usable any time =-");
      IJ.log("");
      IJ.log("Ext.openImagePlus(path)");
      IJ.log("-- Opens the image at the given path with the default options.");
      IJ.log("Ext.openThumbImagePlus(path)");
      IJ.log("-- Opens the thumbnail image at the given path");
      IJ.log("-- with the default options.");
      IJ.log("Ext.getFormat(id, format)");
      IJ.log("-- Retrieves the file format of the given id (filename).");
      IJ.log("Ext.setId(id)");
      IJ.log("-- Initializes the given id (filename).");
      IJ.log("Ext.isThisType(name, thisType)");
      IJ.log("-- True if Bio-Formats recognizes the given file as a");
      IJ.log("-- supported image file format; if necessary, will ");
      IJ.log("-- examine the file contents to decide for sure.");
      IJ.log("Ext.isThisTypeFast(name, thisType)");
      IJ.log("-- True if Bio-Formats recognizes the given filename as a");
      IJ.log("-- supported image file format; will decide based on file");
      IJ.log("-- extension only, without examining file contents.");
      IJ.log("Ext.isMetadataComplete(complete)");
      IJ.log("-- True if Bio-Formats completely parses the current");
      IJ.log("-- dataset's file format. If this function returns false,");
      IJ.log("-- there are known limitations or missing features in how");
      IJ.log("-- Bio-Formats handles this file format.");
      IJ.log("Ext.fileGroupOption(id, fileGroupOption)");
      IJ.log("-- Returns a code indicating the file grouping policy for");
      IJ.log("-- for the current dataset. Possible values are:");
      IJ.log("--   must, can, cannot, unknown");
      IJ.log("Ext.getVersionNumber(version)");
      IJ.log("-- Returns the version number of the currently installed");
      IJ.log("-- version of Bio-Formats.");
      IJ.log("Ext.getBuildDate(date)");
      IJ.log("-- Returns the build date of the currently installed");
      IJ.log("-- version of Bio-Formats.");
      IJ.log("");
      IJ.log("-= Usable before initializing a file =-");
      IJ.log("");
      IJ.log("Ext.setNormalized(normalize)");
      IJ.log("-- Sets whether to normalize floating point data to [0-1].");
      IJ.log("Ext.isNormalized(normalize)");
      IJ.log("-- Gets whether float data is being normalized to [0-1].");
      IJ.log("Ext.setOriginalMetadataPopulated(populate)");
      IJ.log("-- Sets whether Bio-Formats should save proprietary metadata");
      IJ.log("-- to the OME metadata store as custom attributes.");
      IJ.log("Ext.isOriginalMetadataPopulated(populate)");
      IJ.log("-- Sets whether Bio-Formats is saving proprietary metadata");
      IJ.log("-- to the OME metadata store as custom attributes.");
      IJ.log("Ext.setGroupFiles(group)");
      IJ.log("-- For multi-file formats, sets whether to force grouping.");
      IJ.log("Ext.isGroupFiles(group)");
      IJ.log("-- Gets whether grouping is forced for multi-file formats..");
      IJ.log("Ext.setMetadataFiltered(filter)");
      IJ.log("-- Sets whether to filter out ugly metadata from the table");
      IJ.log("-- (i.e., entries with unprintable characters, and extremely");
      IJ.log("-- long values).");
      IJ.log("Ext.isMetadataFiltered(filter)");
      IJ.log("-- Gets whether ugly metadata is being filtered out.");
      IJ.log("");
      IJ.log("-== Usable after initializing a file ==-");
      IJ.log("");
      IJ.log("Ext.getSeriesCount(seriesCount)");
      IJ.log("-- Gets the number of image series in the active dataset.");
      IJ.log("Ext.setSeries(seriesNum)");
      IJ.log("-- Sets the current series within the active dataset.");
      IJ.log("Ext.getSeries(seriesNum)");
      IJ.log("-- Gets the current series within the active dataset.");
      IJ.log("Ext.getUsedFileCount(count)");
      IJ.log("-- Gets the number of files that are part of this dataset.");
      IJ.log("Ext.getUsedFile(i, used)");
      IJ.log("-- Gets the i'th filename part of this dataset.");
      IJ.log("Ext.getCurrentFile(file)");
      IJ.log("-- Gets the base filename used to initialize this dataset.");
      IJ.log("Ext.openImage(title, no)");
      IJ.log("-- Opens the no'th plane in a new window named 'title'.");
      IJ.log("Ext.openSubImage(title, no, x, y, width, height)");
      IJ.log("-- Opens a subset of the no'th plane in a new window");
      IJ.log("-- named 'title'.");
      IJ.log("Ext.openThumbImage(title, no)");
      IJ.log("-- Opens the no'th thumbnail in a new window named 'title'.");
      IJ.log("Ext.close()");
      IJ.log("-- Closes the active dataset.");
      IJ.log("Ext.closeFileOnly()");
      IJ.log("-- Closes open files, leaving the current dataset active.");
      IJ.log("");
      IJ.log("-== Applying to the current series ==-");
      IJ.log("");
      IJ.log("Ext.getImageCount(imageCount)");
      IJ.log("-- Gets the total number of planes in the current dataset.");
      IJ.log("Ext.getSizeX(sizeX)");
      IJ.log("-- Gets the width of each image plane in pixels.");
      IJ.log("Ext.getSizeY(sizeY)");
      IJ.log("-- Gets the height of each image plane in pixels.");
      IJ.log("Ext.getSizeZ(sizeZ)");
      IJ.log("-- Gets the number of focal planes in the dataset.");
      IJ.log("Ext.getSizeC(sizeC)");
      IJ.log("-- Gets the number of channels in the dataset.");
      IJ.log("Ext.getSizeT(sizeT)");
      IJ.log("-- Gets the number of time points in the dataset.");
      IJ.log("Ext.getPixelType(pixelType)");
      IJ.log("-- Gets a code representing the pixel type of the image.");
      IJ.log("-- Possible values include:");
      IJ.log("--   int8, uint8, int16, uint16, int32, uint32, float, double");
      IJ.log("Ext.getEffectiveSizeC(effectiveSizeC)");
      IJ.log("-- Gets the 'effective' number of channels, such that:");
      IJ.log("-- effectiveSizeC * sizeZ * sizeT == imageCount");
      IJ.log("Ext.getRGBChannelCount(rgbChannelCount)");
      IJ.log("-- Gets the number of channels per composite image plane:");
      IJ.log("-- sizeC / rgbChannelCount == effectiveSizeC");
      IJ.log("Ext.isIndexed(indexed)");
      IJ.log("-- Gets whether the image planes are stored as indexed color");
      IJ.log("-- (i.e., whether they have embedded LUTs).");
      IJ.log("Ext.getChannelDimCount(channelDimCount)");
      IJ.log("-- For highly multidimensional image data, the C dimension");
      IJ.log("-- may consist of multiple embedded 'sub' dimensions.");
      IJ.log("-- This function returns the number of such dimensions.");
      IJ.log("Ext.getChannelDimLength(i, channelDimLength)");
      IJ.log("-- Gets the length of the i'th embedded 'sub' dimension.");
      IJ.log("Ext.getChannelDimType(i, channelDimType)");
      IJ.log("-- Gets a string label for the i'th embedded 'sub' channel.");
      IJ.log("Ext.isLittleEndian(littleEndian)");
      IJ.log("-- For multi-byte pixel types, get the data's endianness.");
      IJ.log("Ext.getDimensionOrder(dimOrder)");
      IJ.log("-- Gets a five-character string representing the dimensional");
      IJ.log("-- rasterization order within the dataset. Valid orders are:");
      IJ.log("--   XYCTZ, XYCZT, XYTCZ, XYTZC, XYZCT, XYZTC");
      IJ.log("-- In cases where the channels are interleaved (e.g., CXYTZ),");
      IJ.log("-- C will be the first dimension after X and Y (e.g., XYCTZ)");
      IJ.log("-- and the isInterleaved function will return true.");
      IJ.log("Ext.isOrderCertain(orderCertain)");
      IJ.log("-- Gets whether the dimension order and sizes are known,");
      IJ.log("-- or merely guesses.");
      IJ.log("Ext.isInterleaved(interleaved)");
      IJ.log("-- Gets whether or not the channels are interleaved.");
      IJ.log("-- This function exists because X and Y must appear first");
      IJ.log("-- in the dimension order. For interleaved data, XYCTZ or");
      IJ.log("-- XYCZT is used, and this method returns true.");
      IJ.log("Ext.isInterleavedSubC(subC, interleaved)");
      IJ.log("-- Gets whether the given 'sub' channel is interleaved.");
      IJ.log("-- This method exists because some data with multiple");
      IJ.log("-- rasterized sub-dimensions within C have one sub-dimension");
      IJ.log("-- interleaved, and the other not -- e.g., the SDT reader");
      IJ.log("-- handles spectral-lifetime data with interleaved lifetime");
      IJ.log("-- bins and non-interleaved spectral channels.");
      IJ.log("Ext.getIndex(z, c, t, index)");
      IJ.log("-- Gets the rasterized index corresponding to the given");
      IJ.log("-- Z, C and T coordinates, according to the dataset's");
      IJ.log("-- dimension order.");
      IJ.log("Ext.getZCTCoords(index, z, c, t)");
      IJ.log("-- Gets the Z, C and T coordinates corresponding to the given");
      IJ.log("-- rasterized index value, according to the dataset's");
      IJ.log("-- dimension order.");
      IJ.log("Ext.getMetadataValue(field, value)");
      IJ.log("-- Obtains the specified metadata field's value.");
      IJ.log("Ext.getSeriesName(seriesName)");
      IJ.log("-- Obtains the name of the current series.");
      IJ.log("Ext.getImageCreationDate(creationDate)");
      IJ.log("-- Obtains the creation date of the dataset");
      IJ.log("-- in ISO 8601 format.");
      IJ.log("Ext.getPlaneTimingDeltaT(deltaT, no)");
      IJ.log("-- Obtains the time offset (seconds since the beginning ");
      IJ.log("-- of the experiment) for the no'th plane, or NaN if none.");
      IJ.log("Ext.getPlaneTimingExposureTime(exposureTime, no)");
      IJ.log("-- Obtains the exposure time (in seconds) for the no'th");
      IJ.log("-- plane, or NaN if none.");
      IJ.log("Ext.getPlanePositionX(positionX, no)");
      IJ.log("-- Obtains the X coordinate of the stage for the no'th plane");
      IJ.log("-- or NaN if none.");
      IJ.log("Ext.getPlanePositionY(positionY, no)");
      IJ.log("-- Obtains the Y coordinate of the stage for the no'th plane");
      IJ.log("-- or NaN if none.");
      IJ.log("Ext.getPlanePositionZ(positionZ, no)");
      IJ.log("-- Obtains the Z coordinate of the stage for the no'th plane");
      IJ.log("-- or NaN if none.");
      IJ.log("Ext.getPixelsPhysicalSizeX(sizeX)");
      IJ.log("-- Obtains the width of a pixel in microns, or NaN if the");
      IJ.log("-- the width is not stored in the original file.");
      IJ.log("Ext.getPixelsPhysicalSizeY(sizeY)");
      IJ.log("-- Obtains the height of a pixel in microns, or NaN if the");
      IJ.log("-- the height is not stored in the original file.");
      IJ.log("Ext.getPixelsPhysicalSizeZ(sizeZ)");
      IJ.log("-- Obtains the spacing between Z sections in microns, or NaN");
      IJ.log("-- if the spacing is not stored in the original file.");
      IJ.log("Ext.getPixelsTimeIncrement(sizeT)");
      IJ.log("-- Obtains the spacing between time points in seconds, or");
      IJ.log("-- NaN if the spacing is not stored in the original file.");
      IJ.log("");
      IJ.log("For more information, see the online Javadocs");
      IJ.log("for the loci.formats.IFormatReader and ");
      IJ.log("loci.formats.meta.MetadataRetrieve interfaces:");
      IJ.log(URL_JAVADOCS);
    }
  }

  // -- Utility methods --

  /** Finds the Plane index corresponding to the given image plane number. */
  private static int getPlaneIndex(IFormatReader r, int no) {
    MetadataRetrieve retrieve = (MetadataRetrieve) r.getMetadataStore();
    int imageIndex = r.getSeries();
    int planeCount = retrieve.getPlaneCount(imageIndex);
    int[] zct = r.getZCTCoords(no);
    for (int i=0; i<planeCount; i++) {
      Integer theC = retrieve.getPlaneTheC(imageIndex, i).getValue();
      Integer theT = retrieve.getPlaneTheT(imageIndex, i).getValue();
      Integer theZ = retrieve.getPlaneTheZ(imageIndex, i).getValue();
      if (zct[0] == theZ.intValue() && zct[1] == theC.intValue() &&
        zct[2] == theT.intValue())
      {
        return i;
      }
    }
    return -1;
  }

}
