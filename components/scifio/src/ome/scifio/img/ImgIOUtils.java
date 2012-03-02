/*
ImgLib I/O logic using Bio-Formats.

Copyright (c) 2009, Stephan Preibisch & Stephan Saalfeld.
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.imglib2.exception.ImgLibException;
import net.imglib2.img.Img;

/**
 * Exception indicating something went wrong during I/O.
 *
 * @author Curtis Rueden
 */
public final class ImgIOUtils extends ImgLibException {

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

}
