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
package loci.formats.services;

import ch.systemsx.cisd.base.mdarray.MDByteArray;
import ch.systemsx.cisd.base.mdarray.MDIntArray;
import ch.systemsx.cisd.base.mdarray.MDShortArray;
import ch.systemsx.cisd.hdf5.HDF5CompoundDataMap;
import java.io.IOException;
import java.util.List;

import loci.common.services.Service;

/**
 * Utility class for working with NetCDF/HDF files.
 */
public interface JHDFService extends Service {

    /**
     * Initializes the service on a given file path.
     *
     * @param file Path to initialize the service with.
     * @throws IOException If there is an error initializing the service with
     * <code>file</code>.
     */
    public void setFile(String file) throws IOException;
    
    /**
     * Initializes the service on a given file path.
     *
     * @param file Path to initialize the service with.
     * @throws IOException If there is an error initializing the service with
     * <code>file</code>.
     */
    public void setFileForWrite(String file) throws IOException;
    

    /**
   * Retrieves the current initialized file path.
   * @return Current initialized file path or <code>null</code> if the service
   * has yet to be initialized or is closed.
   */
   public String getFile();

    /**
     * Retrieves the shape of a dataset.
     *
     * @param path HDF path to the dataset.
     * @return int[] of shapes.
     */
    public int[] getShape(String path);

    /**
     * Retrieves the all members of a group.
     *
     * @param path HDF path to the group.
     * @return List<String> of members.
     */
    public List<String> getMember(String path);
    
    /**
     * Retrieves the Element Size of a dataset in Byte.
     *
     * @param path HDF path to the group.
     * @return List<String> of members.
     */
    public int getElementSize(String path);

    /**
     * Reads a multi-dimensional byte array from path.
     *
     * @param path HDF path to the dataset.
     * @return MDByteArray array of bytes.
     */
    public MDByteArray readByteArray(String path);

    /**
     * Reads a multi-dimensional int array from path.
     *
     * @param path HDF path to the dataset.
     * @return MDIntArray array of int.
     */
    public MDIntArray readIntArray(String path);

    /**
     * Reads a multi-dimensional sub-block int array from path.
     *
     * @param path HDF path to the dataset.
     * @param offset Offset of the block to read.
     * @param size Output size of the array.
     * @return MDIntArray array of int.
     */
    public MDIntArray readIntBlockArray(String path, int[] offset, int[] size);
    
    /**
     * Reads a multi-dimensional sub-block byte array from path.
     *
     * @param path HDF path to the dataset.
     * @param offset Offset of the block to read.
     * @param size Output size of the array.
     * @return MDIntArray array of int.
     */
    public MDByteArray readByteBlockArray(String path, int[] offset, int[] size);
    
    /**
     * Reads a multi-dimensional sub-block short array from path.
     *
     * @param path HDF path to the dataset.
     * @param offset Offset of the block to read.
     * @param size Output size of the array.
     * @return MDIntArray array of int.
     */
    public MDShortArray readShortBlockArray(String path, int[] offset, int[] size);

    /**
     * Reads String array array from path.
     *
     * @param path HDF path to the dataset.
     * @return String[] array.
     */
    public String[] readStringArray(String path);

    /**
     * Reads Compound array of any type
     *
     * @param path HDF path to the dataset.
     * @return HDF5CompoundDataMap[] compound array map.
     */
    public HDF5CompoundDataMap[] readCompoundArrayDataMap(String path);

    /**
     * Checks if path in HDF5 file exists.
     *
     * @param path path to HDF5 group or dataset.
     * @return boolean true if path exists in HDF5 file else false.
     */
    public boolean exists(String path);

    /**
     * Creates an HDF5 dataset in path consisting of integers specified 
     * by bytes per pixel bpp.
     *
     * @param path path to HDF5 a dataset.
     * @param bpp bytes per pixel for the integer data type
     * bpp \in {1,2,4} == {Byte, Short, Integer}
     */
    public void initIntArray(String path, long[] dimensions, long bpp);
    
    /**
     * Writes an array as sub-slice into an existing HDF5 dataset
     *
     * @param path path to the (existing) HDF5 dataset.
     * @param image the data stored as MDArray of pixel type Byte
     * @param offset dimensions, where to start writing the image. The image 
     * will be written from offset to offset + image.dimensions()
     */
    public void writeArraySlice(String path, MDByteArray image, long[] offset);
    
    /**
     * Writes an array as sub-slice into an existing HDF5 dataset
     *
     * @param path path to the (existing) HDF5 dataset.
     * @param image the data stored as MDArray of pixel type Short
     * @param offset dimensions, where to start writing the image. The image 
     * will be written from offset to offset + image.dimensions()
     */
    public void writeArraySlice(String path, MDShortArray image, long[] offset);
    
    /**
     * Writes an array as sub-slice into an existing HDF5 dataset
     *
     * @param path path to the (existing) HDF5 dataset.
     * @param image the data stored as MDArray of pixel type int
     * @param offset dimensions, where to start writing the image. The image 
     * will be written from offset to offset + image.dimensions()
     */
    public void writeArraySlice(String path, MDIntArray image, long[] offset);
         
    /**
     * Writes an array as sub-slice into an existing HDF5 dataset
     *
     * @param path path of the HDF5 group.
     */
    public void createGroup(String path) throws IOException;
    /**
     * Closes and resets the service.
     *
     * @throws IOException If there is an error closing the file.
     */
    public void close() throws IOException;
}
