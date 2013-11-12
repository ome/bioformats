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
import ome.jxr.metadata.JXRCoreMetadata;
import ome.jxr.metadata.JXRTextMetadata;

/**
 * Provides methods for operating on and extracting data from JPEG XR byte
 * streams.
 *
 * <dl>
 * <dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-jxr/src/ome/jxr/JXRDecoder.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-jxr/src/ome/jxr/JXRDecoder.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Blazej Pindelski bpindelski at dundee.ac.uk
 */
public class JXRDecoder {

  private RandomAccessInputStream stream;

  private int firstIFDOffset;

  public JXRDecoder(JXRReader reader) throws IOException {
    stream = reader.getInputStream();
    firstIFDOffset = reader.getFirstIFDOffset();
  }

  public RandomAccessInputStream decompressImage() {
    // TODO Fill in logic
    return null;
  }

  public JXRCoreMetadata extractCoreMetadata() {
    // TODO Fill in logic
    return null;
  }

  public JXRTextMetadata extractTextMetadata() {
    // TODO Fill in logic
    return null;
  }

}
