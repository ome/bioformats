//
// VirtualImg.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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

package ome.scifio.img.virtual;

import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;

import net.imglib2.IterableRealInterval;
import net.imglib2.img.AbstractImg;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import ome.scifio.img.ImgIOException;
import ome.scifio.img.ImgOpener;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * This class supports the ability to open an image and only load data into
 * memory one plane at a time. Data is read only in the sense that though
 * in memory values can be changed the data is never written to disk.
 *
 * @author Barry DeZonia
 */
public class VirtualImg<T extends NativeType<T> & RealType<T>>
  extends AbstractImg<T>
{
  private long[] dims;
  private IFormatReader reader;
  private T type;
  private boolean bytesOnly;

  // TODO
  // The reader gets shared among all copy()'s and randomAccess()'s and
  // cursor()'s, etc. so there might be threading issues. Unless we enforce
  // that only one user of the reader can be defined. Maybe reader is
  // already thread safe. Investigate.

  // Note - this constructor is clumsy and error prone. so we're making it
  // private and only invoking (always correctly) through the create() method.

  private VirtualImg(
    long[] dims, IFormatReader reader, T type, boolean bytesOnly)
  {
    super(dims);
    this.dims = dims.clone();
    this.reader = reader;
    this.type = type.copy();
    this.bytesOnly = bytesOnly;
    checkDimensions();
  }

  // TODO: Eliminate use of <?> generics in the methods below.

  /**
   * Factory method for creating VirtualImgs from file names
   * @param fileName - name of the file that contains data of interest
   * @param bytesOnly - a boolean that delineates whether data is to be
   *   accessed a byte at a time or in the actual backing primitive type
   *   one at a time.
   * @return a VirtualImg that gives read only access to data a plane at
   *   a time
   * @throws ImgIOException
   */
  public static VirtualImg<? extends RealType<?>>
    create(String fileName, boolean bytesOnly)
      throws ImgIOException
  {
    IFormatReader rdr = null;
    try {
      rdr = ImgOpener.createReader(fileName, false);  // TODO - or true?
    }
    catch (final FormatException e) {
      throw new ImgIOException(e);
    }
    catch (final IOException e) {
      throw new ImgIOException(e);
    }

    long[] dimensions = ImgOpener.getDimLengths(rdr);

    if (bytesOnly) {
      dimensions[0] *= FormatTools.getBytesPerPixel(rdr.getPixelType());
      return byteTypedVirtualImg(dimensions, rdr);
    }

    return correctlyTypedVirtualImg(dimensions, rdr);
  }

  public VirtualRandomAccess<T> randomAccess() {
    return new VirtualRandomAccess<T>(this);
  }

  public VirtualCursor<T> cursor() {
    return new VirtualCursor<T>(this);
  }

  public VirtualCursor<T> localizingCursor() {
    // TODO - not supporting actual localizing cursor
    return new VirtualCursor<T>(this);
  }

  public boolean equalIterationOrder(IterableRealInterval<?> f) {
    // TODO maybe support. For now, for simplicity, don't support
    return false;
  }

  public ImgFactory<T> factory() {
    return new VirtualImgFactory<T>();
  }

  public Img<T> copy() {
    return new VirtualImg<T>(dims, reader, type, bytesOnly);
  }

  public T getType() {
    return type;
  }

  public IFormatReader getReader() {
    return reader;
  }

  public boolean isByteOnly() {
    return bytesOnly;
  }

  // -- private helpers --

  private void checkDimensions() {
    if (dims.length < 2)
      throw new IllegalArgumentException(
        "VirtualImg must be of dimension two or higher");

    // NOTE - removed code that tested dim0 & dim1 since byteOnly code can
    // mess with dim0. And we setup dims ourself so we know they are correct.
  }

  private static VirtualImg<? extends RealType<?>>
    byteTypedVirtualImg(long[] dimensions, IFormatReader rdr)
  {
    return new VirtualImg<UnsignedByteType>(
        dimensions, rdr, new UnsignedByteType(), true);
  }

  private static VirtualImg<? extends RealType<?>>
    correctlyTypedVirtualImg(long[] dimensions, IFormatReader rdr)
  {
    switch (rdr.getPixelType()) {

      case FormatTools.UINT8:

        return new VirtualImg<UnsignedByteType>(
            dimensions, rdr, new UnsignedByteType(), false);

      case FormatTools.INT8:

        return new VirtualImg<ByteType>(
            dimensions, rdr, new ByteType(), false);

      case FormatTools.UINT16:

        return new VirtualImg<UnsignedShortType>(
            dimensions, rdr, new UnsignedShortType(), false);

      case FormatTools.INT16:

        return new VirtualImg<ShortType>(
            dimensions, rdr, new ShortType(), false);

      case FormatTools.UINT32:

        return new VirtualImg<UnsignedIntType>(
            dimensions, rdr, new UnsignedIntType(), false);

      case FormatTools.INT32:

        return new VirtualImg<IntType>(
            dimensions, rdr, new IntType(), false);

      case FormatTools.FLOAT:

        return new VirtualImg<FloatType>(
            dimensions, rdr, new FloatType(), false);

      case FormatTools.DOUBLE:

        return new VirtualImg<DoubleType>(
            dimensions, rdr, new DoubleType(), false);

      // TODO - add LONG case here when supported by BioFormats

      default:
        throw new IllegalArgumentException(
          "VirtualImg : unsupported pixel format");
    }
  }
}
