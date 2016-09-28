/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2016 Open Microscopy Environment:
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

package loci.formats.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import loci.common.services.AbstractService;
import loci.common.services.Service;
import loci.formats.FormatException;

import ome.jxrlib.Decode;

/**
 * Interface defining methods for working with JPEG-XR data
 */
public class JPEGXRServiceImpl extends AbstractService implements JPEGXRService {

  public JPEGXRServiceImpl() {
    checkClassDependency(ome.jxrlib.Decode.class);
  }

  /**
   * @see JPEGXRServiceImpl#decompress(byte[])
   */
  public byte[] decompress(byte[] compressed) throws FormatException {
    String outFile = null;
    try {
      outFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".jxr").getAbsolutePath();
      FileOutputStream out = new FileOutputStream(outFile);
      out.write(compressed);
      out.close();
    }
    catch (IOException e) {
      throw new FormatException("Could not write input bytes to temporary file", e);
    }
    Decode decoder = new Decode(new File(outFile));
    byte[] decompressed = null;
    try {
      decompressed = decoder.toBytes();
    }
    finally {
      decoder.close();
      new File(outFile).delete();
    }
    return decompressed;
  }

}
