/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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
import java.io.InputStream;
import java.util.Vector;

import loci.common.RandomAccessInputStream;
import loci.common.services.Service;

/**
 * Interface defining methods for reading Microsoft OLE2 documents using
 * OME's fork of Apache POI.
 */
public interface POIService extends Service {

  /**
   * Construct a new POI filesystem around the given file.
   *
   * @throws IOException if an error occurred when opening the file.
   */
  public void initialize(String file) throws IOException;

  /**
   * Construct a new POI filesystem around the given stream.
   *
   * @throws IOException if an error occurred when reading from the stream.
   */
  public void initialize(RandomAccessInputStream stream) throws IOException;

  /**
   * Retrieve an InputStream corresponding to the given file name.
   * Either of the 'initialize' methods must be called before this method.
   *
   * @param file The name of the embedded file for which to
   *   retrieve an InputStream.
   * @throws IOException if an error occurred when reading the file
   */
  public InputStream getInputStream(String file) throws IOException;

  /**
   * Retrieve a RandomAccessInputStream corresponding to the given file name.
   * Either of the 'initialize' methods must be called before this method.
   *
   * @param file The name of the embedded file for which to
   *   retrieve a RandomAccessInputStream.
   * @throws IOException if an error occurred when reading the file
   */
  public RandomAccessInputStream getDocumentStream(String file)
    throws IOException;

  /**
   * Retrieve all of the raw bytes that correspond to the given file name.
   * Either of the 'initialize' methods must be called before this method.
   *
   * @param file The name of the embedded file for which to retrieve bytes.
   * @throws IOException if an error occurred when reading the file
   */
  public byte[] getDocumentBytes(String file) throws IOException;

  /**
   * Retrieve at most 'length' bytes that correspond to the given file name.
   * Either of the 'initialize' methods must be called before this method.
   *
   * @param file The name of the embedded file for which to retrieve bytes.
   * @throws IOException if an error occurred when reading the file
   */
  public byte[] getDocumentBytes(String file, int length) throws IOException;

  /**
   * Retrieve the total number of bytes in the given file.
   * Either of the 'initialize' methods must be called before this method.
   *
   * @param file The name of the embedded file.
   * @throws IOException if an error occurred when reading the file
   */
  public int getFileSize(String file);

  /**
   * Retrieve a list of all files in the current POI file system.
   */
  public Vector<String> getDocumentList();

  /** Close the current POI file system. */
  public void close() throws IOException;

}
