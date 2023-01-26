/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

package loci.formats.in;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.comparator.PathFileComparator;

import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.in.LeicaMicrosystemsMetadata.*;
import loci.formats.in.LeicaMicrosystemsMetadata.doc.LMSImageXmlDocument;
import loci.formats.in.LeicaMicrosystemsMetadata.doc.XlefDocument;
import loci.formats.in.LeicaMicrosystemsMetadata.doc.XlifDocument;
import loci.common.DataTools;
import loci.common.RandomAccessInputStream;

/**
 * XLEFReader is the file format reader for Leica Microsystems' XLEF files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class XLEFReader extends LMSFileReader {
  // -- Fields --
  private List<LMSFileReader> readers = new ArrayList<LMSFileReader>();

  // -- Constructor --

  /** Constructs a new Leica XLEF reader. */
  public XLEFReader() {
    super("Extended leica file", "xlef");
    hasCompanionFiles = true;
  }

  /* @see IFormatReader#openBytes(int, byte[], int, int, int, int) */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h) throws FormatException, IOException {
    int readerIndex = getReaderIndex(getSeries());
    LMSFileReader reader = readers.get(readerIndex);
    reader.setSeries(getSeriesPerReaderIndex(getSeries()));
   
    if (reader.getImageFormat() == ImageFormat.JPEG) {
      buf = openBytesJpeg(no, buf, x, y, w, h);
    } else {
      buf = reader.openBytes(no, buf, x, y, w, h);
    }
    return buf;
  }

  /* @see IFormatReader#openBytes(int, int, int, int, int) */
  @Override
  public byte[] openBytes(int no, int x, int y, int w, int h) throws FormatException, IOException {
    LMSFileReader reader = readers.get(getReaderIndex(getSeries()));
    
    int ch = getRGBChannelCount();
    int bpp = FormatTools.getBytesPerPixel(reader.getPixelType());
    byte[] newBuffer;
    try {
      newBuffer = DataTools.allocate(w, h, ch, bpp);
    } catch (IllegalArgumentException e) {
      throw new FormatException("Image plane too large. Only 2GB of data can "
          + "be extracted at one time. You can work around the problem by opening "
          + "the plane in tiles; for further details, see: " + "https://docs.openmicroscopy.org/bio-formats/"
          + FormatTools.VERSION + "/about/bug-reporting.html#common-issues-to-check", e);
    }

    return openBytes(no, newBuffer, x, y, w, h);
  }

  /* @see IFormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    // XLEF
    associatedXmlDoc = new XlefDocument(id);
    ((XlefDocument)associatedXmlDoc).printReferences();

    // XLIFs: create reader for each xlif referenced "image"
    List<XlifDocument> xlifs = ((XlefDocument)associatedXmlDoc).getXlifs();
    if (xlifs.size() == 0) {
      throw new FormatException("Cannot open project: project has no valid image references");
    }
    LMSFileReader reader;
    for (int i = 0; i < xlifs.size(); i++) {
      XlifDocument xlif = xlifs.get(i);
      switch (xlif.getImageFormat()) {
        case LOF:
          reader = new LOFReader(xlif);
          // we assume that an xlif always references only one lof file.
          // otherwise we would require a MultipleImagesReader here as well.
          // metadata from the xlif shall be used for LOFs in XLEF projects
          ((LOFReader)reader).setIdWithMetadata(xlif.getImagePaths().get(0), xlif);
          break;
        case TIF:
        case BMP:
        case JPEG:
        case PNG:
          reader = new MultipleImagesReader(xlif, i);
          break;
        case UNKNOWN:
        default:
          throw new FormatException("Cannot open project: project contains invalid image formats");
        }
        readers.add(reader);
    }

    translateMetadata((List<LMSImageXmlDocument>)(List<? extends LMSImageXmlDocument>) xlifs);
    setMetadataOfMultipleImagesReaders();
    sortMultipleImagesReaders();
  }

  /* @see IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#getUsedFiles(boolean) */
  @Override
  public String[] getUsedFiles(boolean noPixels) {

    FormatTools.assertId(currentId, true, 1);
    final List<String> files = new ArrayList<String>();
    files.add(currentId);
    files.addAll(((XlefDocument)associatedXmlDoc).getChildrenFiles(!noPixels));
    sortPaths(files);

    return files.toArray(new String[files.size()]);
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {

    FormatTools.assertId(currentId, true, 1);
    final List<String> files = new ArrayList<String>();

    LMSFileReader reader = readers.get(getReaderIndex(getSeries()));
    XlifDocument xlif = (XlifDocument)reader.associatedXmlDoc;
    files.add(xlif.getFilepath());
    files.addAll(xlif.getParentFiles());
    if (!noPixels){
      files.addAll(xlif.getImagePaths());
    }
    sortPaths(files);

    return files.toArray(new String[files.size()]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly){
      for (LMSFileReader reader : readers){
        if (reader instanceof LOFReader)
          ((LOFReader)reader).close(fileOnly);
        else if (reader instanceof MultipleImagesReader)
          ((MultipleImagesReader)reader).close(fileOnly);
      }
      readers.clear();
    }
  }

  /* @see IFormatReader#reopenFile() */
  @Override
  public void reopenFile() throws IOException {
    super.reopenFile();
    for (LMSFileReader reader : readers){
      reader.reopenFile();
    }
  }

  // -- Methods --

  /* @see loci.formats.LeicaFileReader#getImageFormat() */
  public ImageFormat getImageFormat() {
    return readers.get(getReaderIndex(getSeries())).getImageFormat();
  }

/**
   * Returns the index of the correct reader for the series index. Currently, tilescans are treated as if each tile was an own series (like in LIFReader),
   * so one reader may contain multiple series.
   * @param seriesIndex index of series
   * @return reader which contains the series
   */
  private int getReaderIndex(int seriesIndex){
    for (int readerIndex = 0; readerIndex < readers.size(); readerIndex++){
      int lastSeriesIndexLastReader = sum(metaTemp.tileCount, 0, readerIndex - 1) - 1;
      int lastSeriesIndexThisReader = lastSeriesIndexLastReader + metaTemp.tileCount[readerIndex];
      if (seriesIndex >= readerIndex && 
      seriesIndex > lastSeriesIndexLastReader &&
      seriesIndex <= lastSeriesIndexThisReader){
        return readerIndex;
      }
    }
    return -1;
  }

  /**
   * Returns the index of the series (tile) in the corresponding reader
   * @param series index of the series over all readers
   * @return
   */
  private int getSeriesPerReaderIndex(int series){
    int readerIndex = getReaderIndex(series);
    int sprIndex = series - sum(metaTemp.tileCount, 0, readerIndex-1);
    return sprIndex;
  }

  /**
   * Gets bytes of jpeg images. Originally monochrome images that were stored by
   * LASX as 24bit (RGB) jpegs are transformed to monochrome again (upscaled to their original bit depth)
   * 
   * @param no the plane index within the current series
   * @param buf a pre-allocated buffer
   * @param x X coordinate of the upper-left corner of the sub-image
   * @param y Y coordinate of the upper-left corner of the sub-image
   * @param w width of the sub-image
   * @param h height of the sub-image
   * @return
   * @throws FormatException
   * @throws IOException
   */
  private byte[] openBytesJpeg(int no, byte[] buf, int x, int y, int w, int h) throws FormatException, IOException {
    LMSFileReader fReader = readers.get(getReaderIndex(getSeries()));
    if (!(fReader.getImageFormat() == ImageFormat.JPEG)) {
      throw new IOException("Error: Cannot open jpeg bytes with reader " + fReader.getClass());
    }

    MultipleImagesReader reader = (MultipleImagesReader) fReader;

    if (!isRGB() && reader.getRGBChannelCount() == 3) {
      // first, get rgb data
      int bppJpeg = FormatTools.getBytesPerPixel(reader.getPixelType());
      byte[] rgbBuffer;
      try {
        rgbBuffer = DataTools.allocate(w, h, reader.getRGBChannelCount(), bppJpeg);
      } catch (IllegalArgumentException e) {
        throw new FormatException("Image plane too large. Only 2GB of data can "
            + "be extracted at one time. You can work around the problem by opening "
            + "the plane in tiles; for further details, see: " + "https://docs.openmicroscopy.org/bio-formats/"
            + FormatTools.VERSION + "/about/bug-reporting.html#common-issues-to-check", e);
      }
      rgbBuffer = reader.openBytes(no, rgbBuffer, x, y, w, h);

      if (rgbBuffer.length == buf.length){ //hotfix
        buf = rgbBuffer;
      } else {
        // for mono, we only need data from one rgb channel
        byte[] rBuf = getRgbChannel(0, rgbBuffer);

        // resolution in metadata might be different to 8bit from JPEGs --> upscaling to
        // 9 - 16bit required
        int bppXlef = FormatTools.getBytesPerPixel(getPixelType());
        if (bppXlef > bppJpeg) {
          int targetRes = (int) ((Long) getSeriesMetadataValue("Bits per Sample")).longValue();
          byte[] rBufUpscaled = new byte[rBuf.length * 2];
          transformBytes8To16(rBuf, rBufUpscaled, targetRes);
          System.arraycopy(rBufUpscaled, 0, buf, 0, buf.length);
        } else {
          System.arraycopy(rBuf, 0, buf, 0, buf.length);
        }
      }
    } else {
      buf = reader.openBytes(no, buf, x, y, w, h);
    }
    return buf;
  }
  
  /** Sorts frames of all MultipleImagesReaders as per core dimension sizes and order */
  private void sortMultipleImagesReaders() throws FormatException {
    for (int i = 0; i < core.size(); i++){
      LMSFileReader reader = readers.get(getReaderIndex(i));
      if (reader instanceof MultipleImagesReader && 
      reader.getImageFormat() != ImageFormat.LOF){
        ((MultipleImagesReader)reader).swapDimensions(); 
      }
    }
  }

  private void setMetadataOfMultipleImagesReaders(){
    for (int i = 0; i < core.size(); i++){
      LMSFileReader reader = readers.get(getReaderIndex(i));
      if (reader instanceof MultipleImagesReader){
        ((MultipleImagesReader)reader).setCoreMetadata(core.get(i));
        ((MultipleImagesReader)reader).setMetadataTempBuffer(metaTemp);
        ((MultipleImagesReader)reader).metadataTranslators = this.metadataTranslators;
      }
    }
  }

  // -- Helper functions --

  /**
   * Transforms an 8bit byte array to a 16bit byte array, optionally with bit upscaling (8-16bit)
   * 
   * @param in     8bit buffer with data
   * @param out    16bit buffer to be filled
   * @param outRes resolution to which data shall be upscaled (8-16)
   */
  private void transformBytes8To16(byte[] in, byte[] out, int outRes) {
    if (in.length * 2 != out.length || in.length == 0 || out.length == 0) {
      throw new IllegalArgumentException(
          "Error: buffer lengths are incorrect (in=" + in.length + ", out=" + out.length + ")");
    }
    if (outRes < 8 || outRes > 16)
      throw new IllegalArgumentException("Error: only 8-16bit bpp are possible for output (" + outRes + "bpp used)");

    int inPtr = 0;
    int outPtr = 0;
    double factor = Math.pow(2, outRes - 8);
    while (inPtr < in.length && outPtr < out.length) {
      int val = in[inPtr++];
      val *= factor;
      out[outPtr++] = (byte) (val >> 8);
      out[outPtr++] = (byte) val;
    }
  }

  /**
   * Returns single channel data for an rgb plane
   * 
   * @param channel Index of channel that shall be read
   * @param in      channel separated buffer
   */
  private byte[] getRgbChannel(int channel, byte[] in) {
    if (in.length == 0) {
      throw new IllegalArgumentException("Error: buffer is empty");
    }
    if (channel < 0 || channel > 2) {
      throw new IllegalArgumentException("Error: only channels 0 - 2 are allowed (R,G,B)");
    }
    byte[] out = new byte[in.length / 3];
    int channelOffset = in.length / 3;
    int rgbPtr = channelOffset * channel;
    int monoPtr = 0;
    while (monoPtr < out.length) {
      if (rgbPtr < in.length) {
        out[monoPtr++] = (byte) in[rgbPtr++];
      } else {
        break;
      }
    }
    return out;
  }

  /**
   * Sums up all values of an array, from start index to (including) end index
   * @param arr
   * @param start first array position whose value shall be added
   * @param end last array position whose value shall be added
   * @return
   */
  private int sum(int[] arr, int start, int end){
    start = start < 0 ? 0 : start;
    if (end < start) return 0;
    int sum = 0;
    for (int i = start; i <= end; i++){
      sum += arr[i];
    }
    return sum;
  }

  /**
   * Sorts file paths by name and depth and moves current file path to the beginning
   * @param paths
   */
  private void sortPaths(List<String> paths){
    List<File> files = new ArrayList<File>();
    for (String path : paths){
      files.add(new File(path));
    }
    Collections.sort(files, new PathComparator());
    paths.clear();
    for (File file : files){
      paths.add(file.getAbsolutePath());
    }
    paths.remove(currentId);
    paths.add(0, currentId);
  }

  /** Helper class to sort file paths by name and depth */
  static class PathComparator implements Comparator<File> {
    public int compare(File file1, File file2){
      PathFileComparator comp = new PathFileComparator();
      return comp.compare(file1, file2);
    }
  }
}
