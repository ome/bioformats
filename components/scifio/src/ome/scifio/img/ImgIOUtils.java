//
// ImgIOUtils.java
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

package ome.scifio.img;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import loci.formats.FormatTools;

import net.imglib2.exception.ImgLibException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.basictypeaccess.PlanarAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.CharArray;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
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
 * Helper methods for converting between SCIFIO and ImgLib2 data structures.
 * 
 * @author Curtis Rueden
 */
public final class ImgIOUtils {

	private static final int BUFFER_SIZE = 256 * 1024; // 256K

	private ImgIOUtils() {
		// prevent instantiation of utility class
	}

	/**
	 * Downloads the given URL and caches it to a temporary file, which is deleted
	 * upon JVM shutdown. This is useful in conjuction with {@link ImgOpener} to
	 * open a URL as an {@link Img}.
	 * <p>
	 * Data compressed with zip or gzip is supported. In the case of zip, the
	 * first file in the archive is cached.
	 * </p>
	 */
	public static String cacheId(final String urlPath) throws ImgIOException {
		InputStream in = null;
		OutputStream out = null;
		try {
			final URL url = new URL(urlPath);
			final String path = url.getPath();
			final boolean zip = path.endsWith(".zip");
			final boolean gz = path.endsWith(".gz");
			String filename = path.substring(path.lastIndexOf("/") + 1);
	
			// save URL to temporary file
			ZipInputStream inZip = null;
			in = url.openStream();
			if (zip) {
				in = inZip = new ZipInputStream(in);
				final ZipEntry zipEntry = inZip.getNextEntry();
				filename = zipEntry.getName(); // use filename in the zip archive
			}
			if (gz) {
				in = new GZIPInputStream(in);
				filename = filename.substring(0, filename.length() - 3); // strip .gz
			}
			final int dot = filename.lastIndexOf(".");
			final String prefix = dot < 0 ? filename : filename.substring(0, dot);
			final String suffix = dot < 0 ? "" : "." + filename.substring(dot + 1);
			final File tmpFile = File.createTempFile(prefix + "-", suffix);
			tmpFile.deleteOnExit();
			out = new FileOutputStream(tmpFile);
			final byte[] buf = new byte[BUFFER_SIZE];
			while (true) {
				final int r = in.read(buf);
				if (r < 0) break; // eof
				out.write(buf, 0, r);
			}
			return tmpFile.getAbsolutePath();
		}
		catch (IOException e) {
			throw new ImgIOException(e);
		}
		finally {
			try {
				if (in != null) in.close();
			}
			catch (IOException e) {
				throw new ImgIOException(e);
			}
			try {
				if (out != null) out.close();
			}
			catch (IOException e) {
				throw new ImgIOException(e);
			}
		}
	}

  /** Obtains planar access instance backing the given img, if any. */
  @SuppressWarnings("unchecked")
  public static PlanarAccess<ArrayDataAccess<?>> getPlanarAccess(
    final ImgPlus<?> img)
  {
    if (img.getImg() instanceof PlanarAccess) {
      return (PlanarAccess<ArrayDataAccess<?>>) img.getImg();
    }
    return null;
  }

  /** Converts Bio-Formats pixel type to imglib Type object. */
  @SuppressWarnings("unchecked")
  public static <T extends RealType<T>> T makeType(final int pixelType) {
    final RealType<?> type;
    switch (pixelType) {
      case FormatTools.UINT8:
        type = new UnsignedByteType();
        break;
      case FormatTools.INT8:
        type = new ByteType();
        break;
      case FormatTools.UINT16:
        type = new UnsignedShortType();
        break;
      case FormatTools.INT16:
        type = new ShortType();
        break;
      case FormatTools.UINT32:
        type = new UnsignedIntType();
        break;
      case FormatTools.INT32:
        type = new IntType();
        break;
      case FormatTools.FLOAT:
        type = new FloatType();
        break;
      case FormatTools.DOUBLE:
        type = new DoubleType();
        break;
      default:
        type = null;
    }
    return (T) type;
  }
  
  /** Converts imglib Type object to Bio-Formats pixel type. */
  public static <T extends RealType<T>> int makeType(final T type) {
    int pixelType = FormatTools.UINT8;
    if(type instanceof UnsignedByteType) {
      pixelType = FormatTools.UINT8;
    }
    else if(type instanceof ByteType) {
      pixelType = FormatTools.INT8;
    }
    else if(type instanceof UnsignedShortType) {
      pixelType = FormatTools.UINT16;
    }
    else if(type instanceof ShortType) {
      pixelType = FormatTools.INT16;
    }
    else if(type instanceof UnsignedIntType) {
      pixelType = FormatTools.UINT32;
    }
    else if(type instanceof IntType) {
      pixelType = FormatTools.INT32;
    }
    else if(type instanceof FloatType) {
      pixelType = FormatTools.FLOAT;
    }
    else if(type instanceof DoubleType) {
      pixelType = FormatTools.DOUBLE;
    }
    
    return pixelType;
  }

  /** Wraps raw primitive array in imglib Array object. */
  public static ArrayDataAccess<?> makeArray(final Object array) {
    final ArrayDataAccess<?> access;
    if (array instanceof byte[]) {
      access = new ByteArray((byte[]) array);
    }
    else if (array instanceof char[]) {
      access = new CharArray((char[]) array);
    }
    else if (array instanceof double[]) {
      access = new DoubleArray((double[]) array);
    }
    else if (array instanceof int[]) {
      access = new IntArray((int[]) array);
    }
    else if (array instanceof float[]) {
      access = new FloatArray((float[]) array);
    }
    else if (array instanceof short[]) {
      access = new ShortArray((short[]) array);
    }
    else if (array instanceof long[]) {
      access = new LongArray((long[]) array);
    }
    else access = null;
    return access;
  }
}
