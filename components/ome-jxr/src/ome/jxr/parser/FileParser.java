/*
 * #%L
 * OME library for reading the JPEG XR file format.
 * %%
 * Copyright (C) 2013 - 2016 Open Microscopy Environment:
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

package ome.jxr.parser;

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import ome.jxr.JXRException;
import ome.jxr.constants.File;

/**
 * Parses the first bytes of the image file and validates the extracted values
 * for conformance with the file format specification. Throws exceptions early,
 * so that further and more expensive operation can be avoided.
 *
 * <dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 * @deprecated See <a href="http://blog.openmicroscopy.org/file-formats/community/2016/01/06/format-support">blog post</a>
 */
@Deprecated
public final class FileParser extends Parser {

  private int encoderVersion;

  private boolean isLittleEndian;

  private int rootIFDOffset;

  private long fileSize;

  public int getEncoderVersion() {
    return encoderVersion;
  }

  public long getFileSize() {
    return fileSize;
  }

  public boolean isLittleEndian() {
    return isLittleEndian;
  }

  public int getRootIFDOffset() {
    return rootIFDOffset;
  }

  public FileParser(RandomAccessInputStream stream) {
    super(null, stream);
  }

  @Override
  public void parse(long parsingOffset) throws JXRException {
    super.parse(parsingOffset);
    try {
      checkFileSize();
      checkHeaderLength();
      checkFileStructureVersion();
      checkHeaderBOM();
      checkIfValidJpegXr();
      calculateIFDOffset();
    } catch (IOException ioe) {
      throw new JXRException(ioe);
    }
  }

  private void checkFileSize() throws IOException, JXRException {
    if (stream.length() > File.MAX_SIZE) {
      throw new JXRException("File size bigger than supported. Size: "
          + stream.length());
    }
    fileSize = stream.length();
  }

  private void checkHeaderLength() throws IOException, JXRException {
    if (stream.length() < 4) {
      throw new JXRException("File header too short.");
    }
  }

  private void checkFileStructureVersion() throws IOException, JXRException {
    stream.seek(0);
    stream.skipBytes(3);
    byte version = stream.readByte();
    if (version != File.CODESTREAM_VERSION) {
      throw new JXRException("Wrong file format version. Found: " + version);
    }
  }

  private void checkHeaderBOM() throws IOException, JXRException {
    stream.seek(0);
    short littleEndian = stream.readShort();
    if (littleEndian != File.LITTLE_ENDIAN) {
      throw new JXRException("File not using little-endian byte order. "
          + "BOM found: " + Integer.toHexString(littleEndian));
    }
    isLittleEndian = true;
  }

  private void checkIfValidJpegXr() throws IOException, JXRException {
    stream.seek(0);
    stream.skipBytes(2);
    byte magic = stream.readByte();
    if (magic != File.MAGIC_NUMBER) {
      throw new JXRException("Invalid magic byte. Found: "
          + Integer.toHexString(magic));
    }
    encoderVersion = stream.readByte();
  }

  private void calculateIFDOffset() throws IOException, JXRException {
    stream.seek(0);
    stream.skipBytes(4);
    int offset = stream.readInt();
    if (offset == 0) {
      throw new JXRException("IFD offset invalid. Found: " + offset);
    }
    rootIFDOffset = offset;
  }

  public void close() throws IOException {
    super.close();
  }
}
