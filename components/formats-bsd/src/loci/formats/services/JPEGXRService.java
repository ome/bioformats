/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2016 - 2017 Open Microscopy Environment:
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

import loci.common.services.Service;
import loci.formats.FormatException;

/**
 * Interface defining methods for working with JPEG-XR data
 */
public interface JPEGXRService extends Service {

  /**
   * Decompress the given JPEG-XR compressed byte array and return as a byte array.
   * Opening and closing of decoders and streams is handled internally.
   *
   * @param compressed the complete JPEG-XR compressed data
   * @return raw decompressed bytes
   * @throws FormatException if an error occurs during decompression
   */
  byte[] decompress(byte[] compressed) throws FormatException;

}
