//
// LociFunctions.java
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

package loci.plugins.macro;

import ij.IJ;
import ij.process.ImageProcessor;

import java.awt.Rectangle;
import java.io.IOException;

import loci.formats.ChannelSeparator;
import loci.formats.FileStitcher;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataRetrieve;
import loci.plugins.util.ImagePlusReader;
import loci.plugins.util.ImagePlusTools;

/**
 * This class provides macro extensions for ImageJ for Bio-Formats and other
 * LOCI tools. Currently, it is a fairly tight mirror to the
 * {@link loci.formats.IFormatReader} interface, with some additional
 * functions to control the type of format reader used.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/macro/LociFunctions.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/macro/LociFunctions.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class LociFunctions extends MacroFunctions {

  // -- Constants --

  /** URL for LOCI Software Javadocs. */
  public static final String URL_LOCI_SOFTWARE_JAVADOCS =
    "http://hudson.openmicroscopy.org.uk/job/LOCI/javadoc/";

  // -- Fields --

  private ImagePlusReader r;

  // -- Constructor --

  public LociFunctions() {
    r = new ImagePlusReader(new ChannelSeparator(
      new FileStitcher(ImagePlusReader.makeImageReader(), true)));
    r.setMetadataStore(MetadataTools.createOMEXMLMetadata());
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
    channelDimCount[0] = new Double(r.getChannelDimLengths().length);
  }

  public void getChannelDimLength(Double i, Double[] channelDimLength) {
    channelDimLength[0] = new Double(r.getChannelDimLengths()[i.intValue()]);
  }

  public void getChannelDimType(Double i, Double[] channelDimType) {
    channelDimType[0] = new Double(r.getChannelDimTypes()[i.intValue()]);
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

  public void openImage(String title, Double no)
    throws FormatException, IOException
  {
    ImageProcessor[] ip = r.openProcessors(no.intValue());
    ImagePlusTools.makeRGB(title, ip).show();
  }

  public void openSubImage(String title, Double no, Double x, Double y,
    Double width, Double height) throws FormatException, IOException
  {
    Rectangle crop = new Rectangle(x.intValue(), y.intValue(),
      width.intValue(), height.intValue());
    ImageProcessor[] ip = r.openProcessors(no.intValue(), crop);
    ImagePlusTools.makeRGB(title, ip).show();
  }

  public void close() throws IOException { r.close(); }
  public void closeFileOnly() throws IOException { r.close(true); }

  public void getSeriesCount(Double[] seriesCount) {
    seriesCount[0] = new Double(r.getSeriesCount());
  }

  public void setSeries(Double seriesNum) {
    r.setSeries(seriesNum.intValue());
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

  public void setMetadataCollected(Boolean collect) {
    r.setMetadataCollected(collect.booleanValue());
  }

  public void isMetadataCollected(Boolean[] collect) {
    collect[0] = new Boolean(r.isMetadataCollected());
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
    creationDate[0] = retrieve.getImageCreationDate(r.getSeries());
  }

  public void getPlaneTimingDeltaT(Double[] deltaT, Double no) {
    int imageIndex = r.getSeries();
    int planeIndex = getPlaneIndex(r, no.intValue());
    MetadataRetrieve retrieve = (MetadataRetrieve) r.getMetadataStore();
    Double val = null;
    if (planeIndex >= 0) {
      val = retrieve.getPlaneTimingDeltaT(imageIndex, 0, planeIndex);
    }
    deltaT[0] = val == null ? new Double(Double.NaN) : val;
  }

  public void getPlaneTimingExposureTime(Double[] exposureTime, Double no) {
    int imageIndex = r.getSeries();
    int planeIndex = getPlaneIndex(r, no.intValue());
    MetadataRetrieve retrieve = (MetadataRetrieve) r.getMetadataStore();
    Double val = null;
    if (planeIndex >= 0) {
      val = retrieve.getPlaneTimingExposureTime(imageIndex, 0, planeIndex);
    }
    exposureTime[0] = val == null ? new Double(Double.NaN) : val;
  }

  // -- PlugIn API methods --

  public void run(String arg) {
    if (IJ.macroRunning()) super.run(arg);
    else {
      IJ.showMessage("LOCI Plugins for ImageJ",
        "The macro extensions are designed to be used within a macro.\n" +
        "Instructions on doing so will be printed to the Results window.");

      IJ.write("To gain access to more advanced features of Bio-Formats");
      IJ.write("from within a macro, put the following line at the");
      IJ.write("beginning of your macro:");
      IJ.write("");
      IJ.write("run(\"Bio-Formats Macro Extensions\");");
      IJ.write("");
      IJ.write("This will enable the following macro functions:");
      IJ.write("");
      IJ.write("-= Usable any time =-");
      IJ.write("");
      IJ.write("Ext.getFormat(id, format)");
      IJ.write("-- Retrieves the file format of the given id (filename).");
      IJ.write("Ext.setId(id)");
      IJ.write("-- Initializes the given id (filename).");
      IJ.write("Ext.isThisType(name, thisType)");
      IJ.write("-- True if Bio-Formats recognizes the given file as a");
      IJ.write("-- supported image file format; if necessary, will ");
      IJ.write("-- examine the file contents to decide for sure.");
      IJ.write("Ext.isThisTypeFast(name, thisType)");
      IJ.write("-- True if Bio-Formats recognizes the given filename as a");
      IJ.write("-- supported image file format; will decide based on file");
      IJ.write("-- extension only, without examining file contents.");
      IJ.write("Ext.isMetadataComplete(complete)");
      IJ.write("-- True if Bio-Formats completely parses the current");
      IJ.write("-- dataset's file format. If this function returns false,");
      IJ.write("-- there are known limitations or missing features in how");
      IJ.write("-- Bio-Formats handles this file format.");
      IJ.write("Ext.fileGroupOption(id, fileGroupOption)");
      IJ.write("-- Returns a code indicating the file grouping policy for");
      IJ.write("-- for the current dataset. Possible values are:");
      IJ.write("--   must, can, cannot, unknown");
      IJ.write("");
      IJ.write("-= Usable before initializing a file =-");
      IJ.write("");
      IJ.write("Ext.setNormalized(normalize)");
      IJ.write("-- Sets whether to normalize floating point data to [0-1].");
      IJ.write("Ext.isNormalized(normalize)");
      IJ.write("-- Gets whether float data is being normalized to [0-1].");
      IJ.write("Ext.setMetadataCollected(collect)");
      IJ.write("-- Sets whether Bio-Formats should extract metadata at all.");
      IJ.write("Ext.isMetadataCollected(collect)");
      IJ.write("-- Gets whether Bio-Formats is supposed to extract metadata.");
      IJ.write("Ext.setOriginalMetadataPopulated(populate)");
      IJ.write("-- Sets whether Bio-Formats should save proprietary metadata");
      IJ.write("-- to the OME metadata store as custom attributes.");
      IJ.write("Ext.isOriginalMetadataPopulated(populate)");
      IJ.write("-- Sets whether Bio-Formats is saving proprietary metadata");
      IJ.write("-- to the OME metadata store as custom attributes.");
      IJ.write("Ext.setGroupFiles(group)");
      IJ.write("-- For multi-file formats, sets whether to force grouping.");
      IJ.write("Ext.isGroupFiles(group)");
      IJ.write("-- Gets whether grouping is forced for multi-file formats..");
      IJ.write("Ext.setMetadataFiltered(filter)");
      IJ.write("-- Sets whether to filter out ugly metadata from the table");
      IJ.write("-- (i.e., entries with unprintable characters, and extremely");
      IJ.write("-- long values).");
      IJ.write("Ext.isMetadataFiltered(filter)");
      IJ.write("-- Gets whether ugly metadata is being filtered out.");
      IJ.write("");
      IJ.write("-== Usable after initializing a file ==-");
      IJ.write("");
      IJ.write("Ext.getSeriesCount(seriesCount)");
      IJ.write("-- Gets the number of image series in the active dataset.");
      IJ.write("Ext.setSeries(seriesNum)");
      IJ.write("-- Sets the current series within the active dataset.");
      IJ.write("Ext.getSeries(seriesNum)");
      IJ.write("-- Gets the current series within the active dataset.");
      IJ.write("Ext.getUsedFileCount(count)");
      IJ.write("-- Gets the number of files that are part of this dataset.");
      IJ.write("Ext.getUsedFile(i, used)");
      IJ.write("-- Gets the i'th filename part of this dataset.");
      IJ.write("Ext.getCurrentFile(file)");
      IJ.write("-- Gets the base filename used to initialize this dataset.");
      IJ.write("Ext.openImage(title, no)");
      IJ.write("-- Opens the no'th plane in a new window named 'title'.");
      IJ.write("Ext.openImage(title, no, x, y, width, height)");
      IJ.write("-- Opens a subset of the no'th plane in a new window");
      IJ.write("-- named 'title'.");
      IJ.write("Ext.close()");
      IJ.write("-- Closes the active dataset.");
      IJ.write("Ext.closeFileOnly()");
      IJ.write("-- Closes open files, leaving the current dataset active.");
      IJ.write("");
      IJ.write("-== Applying to the current series ==-");
      IJ.write("");
      IJ.write("Ext.getImageCount(imageCount)");
      IJ.write("-- Gets the total number of planes in the current dataset.");
      IJ.write("Ext.getSizeX(sizeX)");
      IJ.write("-- Gets the width of each image plane in pixels.");
      IJ.write("Ext.getSizeY(sizeY)");
      IJ.write("-- Gets the height of each image plane in pixels.");
      IJ.write("Ext.getSizeZ(sizeZ)");
      IJ.write("-- Gets the number of focal planes in the dataset.");
      IJ.write("Ext.getSizeC(sizeC)");
      IJ.write("-- Gets the number of channels in the dataset.");
      IJ.write("Ext.getSizeT(sizeT)");
      IJ.write("-- Gets the number of time points in the dataset.");
      IJ.write("Ext.getPixelType(pixelType)");
      IJ.write("-- Gets a code representing the pixel type of the image.");
      IJ.write("-- Possible values include:");
      IJ.write("--   int8, uint8, int16, uint16, int32, uint32, float, double");
      IJ.write("Ext.getEffectiveSizeC(effectiveSizeC)");
      IJ.write("-- Gets the 'effective' number of channels, such that:");
      IJ.write("-- effectiveSizeC * sizeZ * sizeT == imageCount");
      IJ.write("Ext.getRGBChannelCount(rgbChannelCount)");
      IJ.write("-- Gets the number of channels per composite image plane:");
      IJ.write("-- sizeC / rgbChannelCount == effectiveSizeC");
      IJ.write("Ext.isIndexed(indexed)");
      IJ.write("-- Gets whether the image planes are stored as indexed color");
      IJ.write("-- (i.e., whether they have embedded LUTs).");
      IJ.write("Ext.getChannelDimCount(channelDimCount)");
      IJ.write("-- For highly multidimensional image data, the C dimension");
      IJ.write("-- may consist of multiple embedded 'sub' dimensions.");
      IJ.write("-- This function returns the number of such dimensions.");
      IJ.write("Ext.getChannelDimLength(i, channelDimLength)");
      IJ.write("-- Gets the length of the i'th embedded 'sub' dimension.");
      IJ.write("Ext.getChannelDimType(i, channelDimType)");
      IJ.write("-- Gets a string label for the i'th embedded 'sub' channel.");
      IJ.write("Ext.isLittleEndian(littleEndian)");
      IJ.write("-- For multi-byte pixel types, get the data's endianness.");
      IJ.write("Ext.getDimensionOrder(dimOrder)");
      IJ.write("-- Gets a five-character string representing the dimensional");
      IJ.write("-- rasterization order within the dataset. Valid orders are:");
      IJ.write("--   XYCTZ, XYCZT, XYTCZ, XYTZC, XYZCT, XYZTC");
      IJ.write("-- In cases where the channels are interleaved (e.g., CXYTZ),");
      IJ.write("-- C will be the first dimension after X and Y (e.g., XYCTZ)");
      IJ.write("-- and the isInterleaved function will return true.");
      IJ.write("Ext.isOrderCertain(orderCertain)");
      IJ.write("-- Gets whether the dimension order and sizes are known,");
      IJ.write("-- or merely guesses.");
      IJ.write("Ext.isInterleaved(interleaved)");
      IJ.write("-- Gets whether or not the channels are interleaved.");
      IJ.write("-- This function exists because X and Y must appear first");
      IJ.write("-- in the dimension order. For interleaved data, XYCTZ or");
      IJ.write("-- XYCZT is used, and this method returns true.");
      IJ.write("Ext.isInterleavedSubC(subC, interleaved)");
      IJ.write("-- Gets whether the given 'sub' channel is interleaved.");
      IJ.write("-- This method exists because some data with multiple");
      IJ.write("-- rasterized sub-dimensions within C have one sub-dimension");
      IJ.write("-- interleaved, and the other not -- e.g., the SDT reader");
      IJ.write("-- handles spectral-lifetime data with interleaved lifetime");
      IJ.write("-- bins and non-interleaved spectral channels.");
      IJ.write("Ext.getIndex(z, c, t, index)");
      IJ.write("-- Gets the rasterized index corresponding to the given");
      IJ.write("-- Z, C and T coordinates, according to the dataset's");
      IJ.write("-- dimension order.");
      IJ.write("Ext.getZCTCoords(index, z, c, t)");
      IJ.write("-- Gets the Z, C and T coordinates corresponding to the given");
      IJ.write("-- rasterized index value, according to the dataset's");
      IJ.write("-- dimension order.");
      IJ.write("Ext.getMetadataValue(field, value)");
      IJ.write("-- Obtains the specified metadata field's value.");
      IJ.write("Ext.getSeriesName(seriesName)");
      IJ.write("-- Obtains the name of the current series.");
      IJ.write("Ext.getImageCreationDate(creationDate)");
      IJ.write("-- Obtains the creation date of the dataset");
      IJ.write("-- in ISO 8601 format.");
      IJ.write("Ext.getPlaneTimingDeltaT(deltaT, no)");
      IJ.write("-- Obtains the time offset (seconds since the beginning ");
      IJ.write("-- of the experiment) for the no'th plane, or NaN if none.");
      IJ.write("Ext.getPlaneTimingExposureTime(exposureTime, no)");
      IJ.write("-- Obtains the exposure time (in seconds) for the no'th");
      IJ.write("-- plane, or NaN if none.");
      IJ.write("");
      IJ.write("For more information, see the online Javadocs");
      IJ.write("for the loci.formats.IFormatReader and ");
      IJ.write("loci.formats.meta.MetadataRetrieve interfaces:");
      IJ.write(URL_LOCI_SOFTWARE_JAVADOCS);
    }
  }

  // -- Utility methods --

  /** Finds the Plane index corresponding to the given image plane number. */
  private static int getPlaneIndex(IFormatReader r, int no) {
    MetadataRetrieve retrieve = (MetadataRetrieve) r.getMetadataStore();
    int imageIndex = r.getSeries();
    int planeCount = retrieve.getPlaneCount(imageIndex, 0);
    int[] zct = r.getZCTCoords(no);
    for (int i=0; i<planeCount; i++) {
      Integer theC = retrieve.getPlaneTheC(imageIndex, 0, i);
      Integer theT = retrieve.getPlaneTheT(imageIndex, 0, i);
      Integer theZ = retrieve.getPlaneTheZ(imageIndex, 0, i);
      if (zct[0] == theZ.intValue() && zct[1] == theC.intValue() &&
        zct[2] == theT.intValue())
      {
        return i;
      }
    }
    return -1;
  }

}
