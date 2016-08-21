/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.in;

import java.awt.color.CMMException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import loci.common.ByteArrayHandle;
import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.DelegateReader;
import loci.formats.FormatException;
import loci.formats.FormatTools;

/**
 * JPEGReader is the file format reader for JPEG images.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/JPEGReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/JPEGReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class JPEGReader extends DelegateReader {

  // -- Constants --

  private static final int MAX_SIZE = 8192;

  // -- Constructor --

  /** Constructs a new JPEGReader. */
  public JPEGReader() {
    super("JPEG", new String[] {"jpg", "jpeg", "jpe"});
    nativeReader = new DefaultJPEGReader();
    legacyReader = new TileJPEGReader();
    nativeReaderInitialized = false;
    legacyReaderInitialized = false;
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
    suffixNecessary = false;
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#setId(String) */
  public void setId(String id) throws FormatException, IOException {
    try {
      super.setId(id);
    }
    catch (CMMException e) {
      // strip out all but the first application marker
      // ImageIO isn't too keen on supporting multiple application markers
      // in the same stream, as evidenced by:
      //
      // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6488904

      in = new RandomAccessInputStream(id);
      ByteArrayOutputStream v = new ByteArrayOutputStream();

      byte[] tag = new byte[2];
      in.read(tag);
      v.write(tag);

      in.read(tag);
      int tagValue = DataTools.bytesToShort(tag, false) & 0xffff;
      boolean appNoteFound = false;
      while (tagValue != 0xffdb) {
        if (!appNoteFound || (tagValue < 0xffe0 && tagValue >= 0xfff0)) {
          v.write(tag);

          in.read(tag);
          int len = DataTools.bytesToShort(tag, false) & 0xffff;
          byte[] tagContents = new byte[len - 2];
          in.read(tagContents);
          v.write(tag);
          v.write(tagContents);
        }
        else {
          in.read(tag);
          int len = DataTools.bytesToShort(tag, false) & 0xffff;
          in.skipBytes(len - 2);
        }

        if (tagValue >= 0xffe0 && tagValue < 0xfff0 && !appNoteFound) {
          appNoteFound = true;
        }
        in.read(tag);
        tagValue = DataTools.bytesToShort(tag, false) & 0xffff;
      }
      v.write(tag);
      byte[] remainder = new byte[(int) (in.length() - in.getFilePointer())];
      in.read(remainder);
      v.write(remainder);

      ByteArrayHandle bytes = new ByteArrayHandle(v.toByteArray());

      Location.mapFile(currentId + ".fixed", bytes);
      super.setId(currentId + ".fixed");
    }
    if (getSizeX() > MAX_SIZE && getSizeY() > MAX_SIZE &&
      !legacyReaderInitialized)
    {
      close();
      useLegacy = true;
      super.setId(id);
    }
    currentId = id;
  }

  // -- IFormatReader API methods --

  /* @see IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    Location.mapId(currentId, null);
  }

  // -- Helper reader --

  static class DefaultJPEGReader extends ImageIOReader {
    public DefaultJPEGReader() {
      super("JPEG", new String[] {"jpg", "jpeg", "jpe"});
      suffixNecessary = false;
      suffixSufficient = false;
    }

    /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
    public boolean isThisType(String name, boolean open) {
      if (open) {
        return super.isThisType(name, open);
      }

      return checkSuffix(name, getSuffixes());
    }

    /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
    public boolean isThisType(RandomAccessInputStream stream) throws IOException
    {
      final int blockLen = 4;
      if (!FormatTools.validStream(stream, blockLen, false)) return false;

      byte[] signature = new byte[blockLen];
      stream.read(signature);

      if (signature[0] != (byte) 0xff || signature[1] != (byte) 0xd8 ||
        signature[2] != (byte) 0xff || ((int) signature[3] & 0xf0) == 0)
      {
        return false;
      }

      return true;
    }
  }

}
