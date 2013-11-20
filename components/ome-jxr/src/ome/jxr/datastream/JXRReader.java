/*
 * #%L
 * OME library for reading the JPEG XR file format.
 * %%
 * Copyright (C) 2013 Open Microscopy Environment:
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

package ome.jxr.datastream;

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import ome.jxr.JXRException;
import ome.jxr.constants.File;
import ome.jxr.metadata.JXRMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reader for the JPEG XR image file format. Provides access to uncompressed
 * image data and image metadata. This class only validates the file header and
 * is a facade to a {@link JXRParser} instance that handles low-level data
 * stream operations. A successful instantiation of this class' instance
 * guarantees validity of the image resource passed into the constructor.
 *
 * <dl>
 * <dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-jxr/src/ome/jxr/datastream/JXRReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-jxr/src/ome/jxr/datastream/JXRReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 */
public class JXRReader {

  protected static final Logger LOGGER = LoggerFactory
      .getLogger(JXRReader.class);

  private JXRMetadata metadata;

  private JXRParser parser;

  private RandomAccessInputStream stream;

  private boolean isLittleEndian;

  private int encoderVersion;

  private int rootIFDOffset;

  public JXRReader(String file) throws IOException, JXRException {
    this(new RandomAccessInputStream(file));
  }

  public JXRReader(RandomAccessInputStream stream) throws JXRException {
    this.stream = stream;
    try {
      initialize();
      parser = new JXRParser();
      parser.setInputStream(this.stream);
      parser.setRootIFDOffset(rootIFDOffset);
    } catch (IOException ioe) {
      throw new JXRException(ioe);
    }
  }

  public int getEncoderVersion() {
    return encoderVersion;
  }

  public int getRootIFDOffset() {
    return rootIFDOffset;
  }

  public RandomAccessInputStream getDecompressedImage() throws IOException {
    return parser.getDecompressedImage();
  }

  public JXRMetadata getMetadata() throws IOException, JXRException {
    if (metadata == null) {
      metadata = parser.extractMetadata();
    }
    return metadata;
  }

  public boolean isLittleEndian() {
    return isLittleEndian;
  }

  private void initialize() throws IOException, JXRException {
    // JPEG XR is expected to be little-endian
    LOGGER.info("Initializing JPEG XR reader.");
    stream.order(true);
    checkFileSize();
    checkHeaderLength();
    checkFileStructureVersion();
    checkHeaderBOM();

    LOGGER.info("Validating JPEG XR header.");
    checkIfValidJpegXr();
    calculateIFDOffset();
  }

  private void checkFileSize() throws IOException, JXRException {
    if (stream.length() > File.MAX_SIZE) {
      throw new JXRException("File size bigger than supported. Size: "
          + stream.length());
    }
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
    if (version != File.ENCODER_VERSION) {
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

  public void close() {
    try {
      stream.close();
    } catch (IOException ioe) {
      LOGGER.debug("Cannot close stream.", ioe);
    }
  }

  @Override
  public String toString() {
    return "JXRReader [isLittleEndian=" + isLittleEndian + ", encoderVersion="
        + encoderVersion + ", rootIFDOffset=" + rootIFDOffset + "]";
  }

}
