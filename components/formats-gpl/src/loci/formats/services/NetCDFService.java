/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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
import java.util.Hashtable;
import java.util.Vector;

import loci.common.services.Service;
import loci.common.services.ServiceException;

/**
 * Utility class for working with NetCDF/HDF files.  Uses reflection to
 * call the NetCDF Java library.
 */
public interface NetCDFService extends Service {

  /**
   * Initializes the service on a given file path.
   * @param file Path to initialize the service with.
   * @throws IOException If there is an error initializing the service
   * with <code>file</code>.
   */
  public void setFile(String file) throws IOException;

  /**
   * Retrieves the current initialized file path.
   * @return Current initialized file path or <code>null</code> if the service
   * has yet to be initialized or is closed.
   */
  public String getFile();

  /**
   * Retrieves an exhaustive list of the HDF paths for all attributes in the
   * HDF document.
   * @return Collection of attribute paths.
   */
  public Vector<String> getAttributeList();

  /**
   * Retrieves an exhaustive list of the HDF paths for all variables in the
   * HDF document.
   * @return Collection of variable paths.
   */
  public Vector<String> getVariableList();

  /**
   * Retrieves an attribute's value.
   * @param path HDF path to the attribute.
   * @return String value or <code>null</code> if the attribute is not a
   * string.
   */
  public String getAttributeValue(String path);

  /**
   * Retrieves a variable's value.
   * @param path HDF path to the variable.
   * @return The Java one-dimensional array representation of the variable's
   * values.
   * @throws ServiceException If there is an error with the range of values
   * or reading from the file.
   * @see ucar.ma2.Array#copyTo1DJavaArray()
   */
  public Object getVariableValue(String path) throws ServiceException;

  /**
   * Retrieves an HDF path's values.
   * @param path HDF path to the values.
   * @param origin Array specifying the starting index. If null, assume
   * all zeroes.
   * @param shape Array specifying the extents in each dimension. This
   * becomes the shape of the returned Array.
   * @return The Java n-dimensional array representation of the path's values.
   * @throws ServiceException If there is an error with the range of values
   * or reading from the file.
   * @see ucar.nc2.Variable#read(int[], int[])
   * @see ucar.ma2.Array#copyToNDJavaArray()
   */
  public Object getArray(String path, int[] origin, int[] shape)
    throws ServiceException;

  /**
   * Retrieves all of a variable's attributes.
   * @param path HDF path to the variable.
   * @return Hash table of attributes to Java one-dimensional array
   * representations of the attribute's values.
   * @see ucar.ma2.Array#copyTo1DJavaArray()
   */
  public Hashtable<String, Object> getVariableAttributes(String path);

  /**
   * Retrieves the length of a dimension.
   * @param path HDF path to the dimension.
   * @return Length of the dimension.
   */
  public int getDimension(String path);

  /**
   * Closes and resets the service.
   * @throws IOException If there is an error closing the file.
   */
  public void close() throws IOException;
}
