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

import java.io.IOException;
import java.nio.ByteBuffer;

import loci.common.services.AbstractService;
import loci.common.services.Service;
import loci.formats.FormatException;

import ome.jxrlib.Decode;
import ome.jxrlib.DecodeException;

/**
 * Interface defining methods for working with JPEG-XR data
 */
public class JPEGXRServiceImpl extends AbstractService implements JPEGXRService {

  public JPEGXRServiceImpl() {
    checkClassDependency(ome.jxrlib.Decode.class);
  }

  /**
   * @see JPEGXRServiceImpl#decompress(byte[], int)
   */
  public byte[] decompress(byte[] compressed, int outputSize) throws FormatException {
    try {
      Decode decoder = new Decode(compressed);
      ByteBuffer output = ByteBuffer.allocateDirect(outputSize);
      decoder.toBytes(output);
      byte[] raw = new byte[outputSize];
      output.get(raw);
      output = null;
      return raw;
    }
    catch (DecodeException e) {
      throw new FormatException(e);
    }
  }

}
