//
// LociFunctions.java
//

package loci.plugins;

import ij.IJ;
import java.io.IOException;
import javax.swing.JOptionPane;
import loci.formats.*;
import loci.formats.ome.OMEXMLMetadata;

/**
  * This class provides macro extensions for ImageJ for Bio-Formats and other
  * LOCI tools. Currently, it is a fairly tight mirror to the
  * {@link loci.formats.IFormatReader} interface, with some additional
  * functions to control the type of format reader used.
  */
public class LociFunctions extends MacroFunctions {

  // -- Fields --

  private IFormatReader r;

  // -- Constructor --

  public LociFunctions() {
    r = new FileStitcher(true);
    r.setMetadataStore(new OMEXMLMetadata());
  }

  // -- LociFunctions API methods - loci.formats.IFormatReader --

  public void getSizeX(Double[] sizeX) { sizeX[0] = new Double(r.getSizeX()); }
  public void getSizeY(Double[] sizeY) { sizeY[0] = new Double(r.getSizeY()); }
  public void getSizeZ(Double[] sizeZ) { sizeZ[0] = new Double(r.getSizeZ()); }
  public void getSizeC(Double[] sizeC) { sizeC[0] = new Double(r.getSizeC()); }
  public void getSizeT(Double[] sizeT) { sizeT[0] = new Double(r.getSizeT()); }

  public void getPixelType(Double[] pixelType) {
    pixelType[0] = new Double(r.getPixelType());
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

  public void getThumbSizeX(Double[] thumbSizeX) {
    thumbSizeX[0] = new Double(r.getThumbSizeX());
  }

  public void getThumbSizeY(Double[] thumbSizeY) {
    thumbSizeY[0] = new Double(r.getThumbSizeY());
  }

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

  // -- LociFunction API methods - additional methods --

  public void setId(String id) throws FormatException, IOException {
    r.setId(id);
  }

  public void getSeriesName(String[] seriesName) {
    OMEXMLMetadata ms = (OMEXMLMetadata) r.getMetadataStore();
    seriesName[0] = ms.getImageName(new Integer(r.getSeries()));
  }

  // -- PlugIn API methods --

  public void run(String arg) {
    if (IJ.macroRunning()) super.run(arg);
    else {
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
      IJ.write("Ext.setId(id)");
      IJ.write("-- opens a file");
      IJ.write("Ext.isMetadataComplete(complete)");
      IJ.write("Ext.fileGroupOption(id, fileGroupOption)");
      IJ.write("");
      IJ.write("-= Usable before opening a file =-");
      IJ.write("");
      IJ.write("Ext.setNormalized(normalize)");
      IJ.write("Ext.isNormalized(normalize)");
      IJ.write("Ext.setMetadataCollected(collect)");
      IJ.write("Ext.isMetadataCollected(collect)");
      IJ.write("Ext.setOriginalMetadataPopulated(populate)");
      IJ.write("Ext.isOriginalMetadataPopulated(populate)");
      IJ.write("Ext.setGroupFiles(group)");
      IJ.write("Ext.isGroupFiles(group)");
      IJ.write("Ext.setMetadataFiltered(filter)");
      IJ.write("Ext.isMetadataFiltered(filter)");
      IJ.write("");
      IJ.write("-== Usable after opening a file ==-");
      IJ.write("");
      IJ.write("Ext.getSeriesCount(seriesCount)");
      IJ.write("-- gets the number of image series in the active dataset");
      IJ.write("Ext.setSeries(seriesNum)");
      IJ.write("-- sets the current series within the active dataset");
      IJ.write("Ext.getSeries(seriesNum)");
      IJ.write("-- gets the current series within the active dataset");
      IJ.write("Ext.getUsedFileCount(count)");
      IJ.write("Ext.getUsedFile(i, used)");
      IJ.write("Ext.getCurrentFile(file)");
      IJ.write("Ext.close()");
      IJ.write("-- closes the active dataset");
      IJ.write("Ext.closeFileOnly()");
      IJ.write("-- closes open files, leaving the current dataset active");
      IJ.write("");
      IJ.write("-== Applying to the current series ==-");
      IJ.write("");
      IJ.write("Ext.getSizeX(sizeX)");
      IJ.write("Ext.getSizeY(sizeY)");
      IJ.write("Ext.getSizeZ(sizeZ)");
      IJ.write("Ext.getSizeC(sizeC)");
      IJ.write("Ext.getSizeT(sizeT)");
      IJ.write("Ext.getPixelType(pixelType)");
      IJ.write("Ext.getEffectiveSizeC(effectiveSizeC)");
      IJ.write("Ext.getRGBChannelCount(rgbChannelCount)");
      IJ.write("Ext.isIndexed(indexed)");
      IJ.write("Ext.getChannelDimCount(channelDimCount)");
      IJ.write("Ext.getChannelDimLength(i, channelDimLength)");
      IJ.write("Ext.getChannelDimType(i, channelDimType)");
      IJ.write("Ext.getThumbSizeX(thumbSizeX)");
      IJ.write("Ext.getThumbSizeY(thumbSizeY)");
      IJ.write("Ext.isLittleEndian(littleEndian)");
      IJ.write("Ext.getDimensionOrder(dimOrder)");
      IJ.write("Ext.isOrderCertain(orderCertain)");
      IJ.write("Ext.isInterleaved(interleaved)");
      IJ.write("Ext.isInterleavedSubC(subC, interleaved)");
      IJ.write("Ext.getIndex(z, c, t, index)");
      IJ.write("Ext.getZCTCoords(index, z, c, t)");
      IJ.write("Ext.getMetadataValue(field, value)");
      IJ.write("Ext.getSeriesName(seriesName)");

      JOptionPane.showMessageDialog(null,
        "The macro extensions are designed to be used within a macro.\n" +
        "Instructions on doing so have been printed to the Results window.\n",
        "LOCI Plugins for ImageJ", JOptionPane.INFORMATION_MESSAGE);
    }
  }

}
