/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

import java.io.IOException;
import java.util.ArrayList;

import loci.formats.CoreMetadata;
import loci.formats.DelegateReader;
import loci.formats.FormatException;
import loci.formats.UnsupportedCompressionException;

/**
 * TiffDelegateReader is a file format reader for TIFF files.
 * It does not read files directly, but chooses which TIFF reader
 * is more appropriate.
 *
 * @see TiffReader
 * @see TiffJAIReader
 */
public class TiffDelegateReader extends DelegateReader {

  // -- Constructor --

  /** Constructs a new TIFF reader. */
  public TiffDelegateReader() {
    super("Tagged Image File Format", TiffReader.TIFF_SUFFIXES);
    nativeReader = new TiffReader();
    legacyReader = new TiffJAIReader();
    nativeReaderInitialized = false;
    legacyReaderInitialized = false;
    suffixNecessary = false;
  }

  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (!isLegacy()) {
      try {
        return nativeReader.openBytes(no, buf, x, y, w, h);
      }
      catch (UnsupportedCompressionException e) {
        LOGGER.debug("Could not open plane with native reader", e);
        if (!legacyReaderInitialized) {
          legacyReader.setId(getCurrentFile());
          legacyReaderInitialized = true;
          nativeReader.close();
          nativeReaderInitialized = false;
        }
        return legacyReader.openBytes(no, buf, x, y, w, h);
      }
    }
    return super.openBytes(no, buf, x, y, w, h);
  }

  @Override
  public void setId(String id) throws FormatException, IOException {
    if (isLegacy()) {
      super.setId(id);
    }
    nativeReader.setId(id);
    nativeReaderInitialized = true;
    currentId = nativeReader.getCurrentFile();
    core = new ArrayList<CoreMetadata>(nativeReader.getCoreMetadataList());
    metadata = nativeReader.getGlobalMetadata();
    metadataStore = nativeReader.getMetadataStore();
  }

}
