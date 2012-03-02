/*
Copyright (c) 2011, Barry DeZonia.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the Fiji project developers nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
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
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
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
 *
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
      dimensions[0] *= getByteMultiplier(rdr);
      return byteTypedVirtualImg(dimensions, rdr);
    }

    return correctlyTypedVirtualImg(dimensions, rdr);
  }

  @Override
  public VirtualRandomAccess<T> randomAccess() {
    return new VirtualRandomAccess<T>(this);
  }

  @Override
  public VirtualCursor<T> cursor() {
    return new VirtualCursor<T>(this);
  }

  @Override
  public VirtualCursor<T> localizingCursor() {
    // TODO - not supporting actual localizing cursor
    return new VirtualCursor<T>(this);
  }

  @Override
  public boolean equalIterationOrder(IterableRealInterval<?> f) {
    // TODO maybe support. For now, for simplicity, don't support
    return false;
  }

  @Override
  public ImgFactory<T> factory() {
    return new VirtualImgFactory<T>();
  }

  @Override
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

  private static int getByteMultiplier(IFormatReader rdr)
  {
    switch (rdr.getPixelType()) {

      case FormatTools.UINT8:
      case FormatTools.INT8:
        return 1;

      case FormatTools.UINT16:
      case FormatTools.INT16:
        return 2;

      case FormatTools.UINT32:
      case FormatTools.INT32:
      case FormatTools.FLOAT:
        return 4;

      case FormatTools.DOUBLE:
        return 8;

      // TODO - add LONG case here when supported by BioFormats

      default:
        throw new IllegalArgumentException(
          "VirtualImg : unsupported pixel format");
    }
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
