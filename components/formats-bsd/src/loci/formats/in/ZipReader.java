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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import loci.common.IRandomAccess;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.ZipHandle;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.ImageReader;

/**
 * Reader for Zip files.
 */
public class ZipReader extends FormatReader {

  // -- Fields --

  private transient ImageReader reader;
  private String entryName;

  private ArrayList<String> mappedFiles = new ArrayList<String>();
  private ZipInputStream zip;

  // -- Constructor --

  public ZipReader() {
    super("Zip", "zip");
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    return reader.get8BitLookupTable();
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    return reader.get16BitLookupTable();
  }

  /* @see loci.formats.IFormatReader#setGroupFiles(boolean) */
  @Override
  public void setGroupFiles(boolean groupFiles) {
    super.setGroupFiles(groupFiles);
    if (reader != null) reader.setGroupFiles(groupFiles);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (Location.getMappedFile(entryName) == null) {
      initFile(currentId);
    }
    reader.setId(entryName);
    return reader.openBytes(no, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (reader != null) reader.close(fileOnly);
    if (!fileOnly) reader = null;
    for (String name : mappedFiles) {
      IRandomAccess handle = Location.getMappedFile(name);
      Location.mapFile(name, null);
      if (handle != null) {
        handle.close();
      }
    }
    mappedFiles.clear();
    if (zip != null) zip.close();
    entryName = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    reader = new ImageReader();

    reader.setMetadataOptions(getMetadataOptions());
    reader.setMetadataFiltered(isMetadataFiltered());
    reader.setOriginalMetadataPopulated(isOriginalMetadataPopulated());
    reader.setNormalized(isNormalized());
    reader.setMetadataStore(getMetadataStore());

    String innerFile = id;
    if (checkSuffix(id, "zip")) {
      innerFile = id.substring(0, id.length() - 4);
    }
    int sep = innerFile.lastIndexOf(File.separator);
    if (sep < 0) {
      sep = innerFile.lastIndexOf("/");
    }
    if (sep >= 0) {
      innerFile = innerFile.substring(sep + 1);
    }

    // NB: We need a raw handle on the ZIP data itself, not a ZipHandle.
    IRandomAccess rawHandle = Location.getHandle(id, false, false);
    in = new RandomAccessInputStream(rawHandle, id);

    zip = new ZipInputStream(in);
    ZipEntry ze = null;
    entryName = null;
    boolean matchFound = false;
    while (true) {
      ze = zip.getNextEntry();
      if (ze == null) break;

      if (entryName == null) {
        entryName = ze.getName();
      }

      if (!matchFound && ze.getName().startsWith(innerFile)) {
        entryName = ze.getName();
        matchFound = true;
      }

      ZipHandle handle = new ZipHandle(id, ze);
      Location.mapFile(ze.getName(), handle);
      mappedFiles.add(ze.getName());
    }

    if (entryName == null) {
      throw new FormatException("Zip file does not contain any valid files");
    }

    reader.setId(entryName);

    metadataStore = reader.getMetadataStore();
    core = new ArrayList<CoreMetadata>(reader.getCoreMetadataList());
    metadata = reader.getGlobalMetadata();
  }

}
