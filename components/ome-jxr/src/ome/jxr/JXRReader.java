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

package ome.jxr;

import java.io.IOException;

import loci.common.RandomAccessInputStream;

/**
 * OME library for reading the JPEG XR file format.
 *
 * <dl>
 * <dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-jxr/src/ome/jxr/JXRReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-jxr/src/ome/jxr/JXRReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 */
public class JXRReader {

  private RandomAccessInputStream stream;

  private boolean isLittleEndian;

  public JXRReader(String file) throws IOException, JXRException {
    this(new RandomAccessInputStream(file));
  }

  public JXRReader(RandomAccessInputStream stream) throws JXRException {
    this.stream = stream;
    try {
      initialize();
    } catch (IOException ioe) {
      throw new JXRException(ioe);
    }
  }

  public boolean isLittleEndian() {
    return isLittleEndian;
  }

  private void initialize() throws IOException, JXRException {
    checkHeaderLength();
    checkHeaderBOM();
  }

  private void checkHeaderLength() throws IOException, JXRException {
    if (stream.length() < 4) {
      throw new JXRException("File header too short."); 
    }
  }

  private void checkHeaderBOM() throws IOException, JXRException {
    stream.seek(0);
    int littleEndian = stream.read();
    littleEndian = littleEndian << Byte.SIZE | stream.read();
    if (littleEndian != JXRConstants.LITTLE_ENDIAN) {
      throw new JXRException("File not using little-endian byte order. "
          + "BOM found: " + Integer.toHexString(littleEndian));
    }
    isLittleEndian = true;
  }

}
