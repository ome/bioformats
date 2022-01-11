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

package loci.formats.in.LeicaMicrosystemsMetadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.in.LOFReader;
import loci.formats.in.JPEGReader;
import loci.formats.in.TiffDelegateReader;
import loci.formats.in.LeicaMicrosystemsMetadata.Dimension.DimensionKey;
import loci.formats.in.BMPReader;
import loci.formats.in.APNGReader;

/**
 * MultipleImagesReader presents a number of image files as one image reference
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class MultipleImagesReader extends LMSFileReader {
  private List<FormatReader> readers = new ArrayList<>();
  public final ImageFormat imageFormat;
  public final int tileCount;
  private boolean dimensionsSwapped = false;
  private int series = 0; //this value is only needed for plane index calculation, if this reader refers to multiple series
  private int imageIndex;

  // -- Constructor --
  public MultipleImagesReader(XlifDocument xlif, int imageIndex) throws FormatException, IOException {
    super("Multiple images", ".*");
    imageFormat = xlif.getImageFormat();
    tileCount = xlif.getTileCount();
    associatedXmlDoc = xlif;
    initReaders(xlif.getImagePaths());
    this.imageIndex = imageIndex;
  }

  // -- IFormatReader API methods --

  /* @see IFormatReader#openBytes(int, byte[], int, int, int, int) */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h) throws FormatException, IOException {
    if (tileCount > 1){
      no = no + series * readers.size() / tileCount;
    }
    FormatReader reader = readers.get(no);
    buf = reader.openBytes(0, buf, x, y, w, h);
    return buf;
  }

  /** Sets the field MultipleImagesReader.series. Needs to be called before calling MultipleImagesReader.openBytes() */
  @Override
  public void setSeries(int no){
    series = no;
  }

  /* @see IFormatReader#getRGBChannelCount() */
  @Override
  public int getRGBChannelCount() {
    return readers.get(0).getRGBChannelCount(); // assuming that all images of a series have the same format
  }

  /* @see IFormatReader#getBitsPerPixel() */
  @Override
  public int getBitsPerPixel() {
    return readers.get(0).getBitsPerPixel(); // assuming that all images of a series have the same format
  }

  /* @see IFormatReader#getBitsPerPixel() */
  @Override
  public int getPixelType() {
    return readers.get(0).getPixelType(); // assuming that all images of a series have the same format
  }

  /* @see IFormatReader#getSizeC() */
  @Override
  public int getSizeC() {
    return readers.get(0).getSizeC(); // assuming that all images of a series have the same format
  }

  /* @see IFormatReader#isRGB() */
  @Override
  public boolean isRGB() {
    return readers.get(0).isRGB(); // assuming that all images of a series have the same format
  }

  // -- Methods --
  /**
   * Creates an internal reader for each image file
   * @param ids list of file ids
   * @throws FormatException
   * @throws IOException
   */
  private void initReaders(List<String> ids) throws FormatException, IOException{
    FormatReader reader;
    for (String id : ids){
      if (!LMSFileReader.fileExists(id)){
        throw new IOException("Project is not valid because referenced image file does not exist: "+id);
      } 
      switch (imageFormat){
        //this should however not happen since we expect that xlifs reference only one single LOF,
        //so no MultipleImagesReader is needed and a LOFReader can be used directly
        case LOF:
        reader = new LOFReader();
        reader.setId(id);
        readers.add(reader);
        break;
      case TIF:
        reader = new TiffDelegateReader();
        reader.setId(id);
        readers.add(reader);
        break;
      case BMP:
        reader = new BMPReader();
        reader.setId(id);
        readers.add(reader);
        break;
      case JPEG:
        reader = new JPEGReader();
        reader.setId(id);
        readers.add(reader);
        break;
      case PNG:
        reader = new APNGReader();
        reader.setId(id);
        readers.add(reader);
        break;
      case UNKNOWN:
      default:
        LOGGER.error("Unknown image format: "+id);;
        break;
      }
    }
  }

  public ImageFormat getImageFormat() {
    return imageFormat;
  }

  public void setMetadataTempBuffer(MetadataTempBuffer metaTemp){
    this.metaTemp = metaTemp;
  }

  public void setCoreMetadata(CoreMetadata cmd){
    if (core == null){
      core = new ArrayList<CoreMetadata>();
    }
    core.add(cmd);
  }

  /**
   * Rearranges frame order from ZSTC order (used in XLIFs) to desired CoreMetadata.dimensionOrder
   * @param cmd corresponding CoreMetadata (expecting that all tiles have the same dimension sizes)
   */
  public void swapDimensions() throws FormatException{
    CoreMetadata cmd = core.get(0);
    
    if (!dimensionsSwapped){
      int sizeZ = cmd.sizeZ;
      int sizeT = cmd.sizeT;
      int sizeS = tileCount;

      List<FormatReader> newOrder = new ArrayList<FormatReader>();
      List<Dimension> dimensions = metaTemp.getDimensions(imageIndex);

      Dimension dimZ = metaTemp.getDimension(imageIndex, DimensionKey.Z);
      Dimension dimC = metaTemp.getDimension(imageIndex, DimensionKey.C);
      Dimension dimT = metaTemp.getDimension(imageIndex, DimensionKey.T);
      Dimension dimS = metaTemp.getDimension(imageIndex, DimensionKey.S);

      //iterating through dimensions in desired order
      for (int indexDim5 = 0; indexDim5 < dimensions.get(5).size; indexDim5++){
        dimensions.get(5).frameIndex = indexDim5;
        for (int indexDim4 = 0; indexDim4 < dimensions.get(4).size; indexDim4++){
          dimensions.get(4).frameIndex = indexDim4;
          for (int indexDim3 = 0; indexDim3 < dimensions.get(3).size; indexDim3++){
            dimensions.get(3).frameIndex = indexDim3;
            for (int indexDim2 = 0; indexDim2 < dimensions.get(2).size; indexDim2++){
              dimensions.get(2).frameIndex = indexDim2;

              //offsetting dimensions in order they are listed by xlifs (ZSTC)
              int index = dimZ.frameIndex + 
              sizeZ * dimS.frameIndex + 
              sizeZ * sizeS * dimT.frameIndex + 
              sizeZ * sizeS * sizeT * dimC.frameIndex;
              newOrder.add(readers.get(index));
            }
          }
        }
      }
      readers = newOrder;
      dimensionsSwapped = true;
    }
  }
}
