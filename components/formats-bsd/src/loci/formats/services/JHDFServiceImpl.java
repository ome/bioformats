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

import java.io.IOException;
import java.util.List;

import loci.common.services.AbstractService;
import loci.formats.FormatTools;

import ch.systemsx.cisd.base.mdarray.MDByteArray;
import ch.systemsx.cisd.base.mdarray.MDIntArray;
import ch.systemsx.cisd.base.mdarray.MDShortArray;
import ch.systemsx.cisd.hdf5.HDF5CompoundDataMap;
import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.HDF5IntStorageFeatures;
import ch.systemsx.cisd.hdf5.IHDF5Reader;

import ch.systemsx.cisd.hdf5.IHDF5Writer;

/**
 * Utility class for working with HDF files.
 */
public class JHDFServiceImpl extends AbstractService
        implements JHDFService {

    // -- Constants --
    public static final String NO_JHDF_MSG
            = "JHDF is required to read HDF5 files. "
            + "Please obtain the necessary JAR files from "
            + "https://docs.openmicroscopy.org/bio-formats/"
            + FormatTools.VERSION + "/developers/java-library.html.\n"
            + "Required JAR files is cisd-jhdf5-batteries_included_lin_win_mac.jar.";

    // -- Fields --
    private String currentFile;

    /**
     * Root of the HDF5 file.
     */
    private IHDF5Reader hdfReader;
    
    /**
     * Root of the HDF5 file.
     */
    private IHDF5Writer hdfWriter;

    // -- NetCDFService API methods ---
    /**
     * Default constructor.
     */
    public JHDFServiceImpl() {
        // One check from each package
        checkClassDependency(ch.systemsx.cisd.base.mdarray.MDIntArray.class);
        checkClassDependency(ch.systemsx.cisd.hdf5.HDF5Factory.class);
    }

    /* (non-Javadoc)
     * @see loci.formats.JHDFService#setFile()
     */
    public void setFile(String file) throws IOException {
        this.currentFile = file;
        this.hdfReader = HDF5Factory.openForReading(file);
    }
    
    /* (non-Javadoc)
     * @see loci.formats.JHDFService#setFileForWrite()
     */
    public void setFileForWrite(String file) throws IOException {
        this.currentFile = file;
        this.hdfWriter = HDF5Factory.open(file);
    }

    /* (non-Javadoc)
     * @see loci.formats.NetCDFService#getFile()
     */
    public String getFile() {
        return currentFile;
    }

    /* (non-Javadoc)
     * @see loci.formats.JHDFService#getMember()
     */
    public List<String> getMember(String path) {
        return hdfReader.getGroupMembers(path);
    }

    /* (non-Javadoc)
     * @see loci.formats.JHDFService#getShape()
     */
    public int[] getShape(String path) {
        long[] tmp = hdfReader.getDataSetInformation(path).getDimensions();
        // Conversion from long[] to int[]
        int[] result = new int[tmp.length];
        for (int k = 0; k < tmp.length; k++) {
            result[k] = (int) tmp[k];
        }
        return result;
    }

    /* (non-Javadoc)
     * @see loci.formats.JHDFService#readByteArray()
     */
    public MDByteArray readByteArray(String path) {
        return this.hdfReader.int8().readMDArray(path);
    }

    /* (non-Javadoc)
     * @see loci.formats.JHDFService#readIntArray()
     */
    public MDIntArray readIntArray(String path) {
        return this.hdfReader.int32().readMDArray(path);
    }

    /* (non-Javadoc)
     * @see loci.formats.JHDFService#readIntBlockArray()
     */
    //public MDIntArray readIntBlockArray(String path, int z, int c, int t, int h, int w) {
    public MDIntArray readIntBlockArray(String path, int[] offset, int[] size) {
        long[] longOffset = new long[offset.length];
        for (int k = 0; k < offset.length; k++) {
            longOffset[k] = (long) offset[k];
        }
        return this.hdfReader.int32().readMDArrayBlockWithOffset(path, size, longOffset);
    }

    /* (non-Javadoc)
     * @see loci.formats.JHDFService#readIntBlockArray()
     */
    public MDByteArray readByteBlockArray(String path, int[] offset, int[] size) {
        long[] longOffset = new long[offset.length];
        for (int k = 0; k < offset.length; k++) {
            longOffset[k] = (long) offset[k];
        }
        return this.hdfReader.int8().readMDArrayBlockWithOffset(path, size, longOffset);
    }

     /* (non-Javadoc)
     * @see loci.formats.JHDFService#readIntBlockArray()
     */
    public MDShortArray readShortBlockArray(String path, int[] offset, int[] size) {
        long[] longOffset = new long[offset.length];
        for (int k = 0; k < offset.length; k++) {
            longOffset[k] = (long) offset[k];
        }
        return this.hdfReader.int16().readMDArrayBlockWithOffset(path, size, longOffset);
    }

    /* (non-Javadoc)
     * @see loci.formats.JHDFService#readStringArray()
     */
    public String[] readStringArray(String path) {
        return this.hdfReader.string().readArray(path);
    }

    /* (non-Javadoc)
     * @see loci.formats.JHDFService#readCompoundArrayDataMap()
     */
    public HDF5CompoundDataMap[] readCompoundArrayDataMap(String path) {
        return this.hdfReader.readCompoundArray(path, HDF5CompoundDataMap.class);
    }

    /* (non-Javadoc)
     * @see loci.formats.JHDFService#getElementSize()
     */
    public int getElementSize(String path) {
        return this.hdfReader.getDataSetInformation(path).getTypeInformation().getElementSize();
    }

    
    /* (non-Javadoc)
     * @see loci.formats.JHDFService#initIntArray()
     */
    public void initIntArray(String path, long[] dimensions, long bpp) {
        if (bpp == 1) {
            this.hdfWriter.uint8().createMDArray(path, dimensions, 
                new int[] {1,1,1, (int) dimensions[3], (int) dimensions[4]}, 
                        HDF5IntStorageFeatures.createDeflationKeep(1));
        } else if (bpp == 2) {
            this.hdfWriter.uint16().createMDArray(path, dimensions, 
                new int[] {1,1,1, (int) dimensions[3], (int) dimensions[4]}, 
                        HDF5IntStorageFeatures.createDeflationKeep(1));
        } else if (bpp == 4) {
            this.hdfWriter.int32().createMDArray(path, dimensions, 
                new int[] {1,1,1, (int) dimensions[3], (int) dimensions[4]}, 
                        HDF5IntStorageFeatures.createDeflationKeep(1));
        }
    }
    
    /* (non-Javadoc)
     * @see loci.formats.JHDFService#writeArraySlice()
     */
    public void writeArraySlice(String path, MDByteArray image, long[] offset) {
        int[] mem_offset = new int[] {0, 0, 0, 0, 0};
        this.hdfWriter.uint8().writeMDArrayBlockWithOffset(path, image, image.dimensions(), offset, mem_offset);
    }
    
    /* (non-Javadoc)
     * @see loci.formats.JHDFService#writeArraySlice()
     */
    public void writeArraySlice(String path, MDShortArray image, long[] offset) {
        int[] mem_offset = new int[] {0, 0, 0, 0, 0};
        this.hdfWriter.uint16().writeMDArrayBlockWithOffset(path, image, image.dimensions(), offset, mem_offset);
    }
    
    /* (non-Javadoc)
     * @see loci.formats.JHDFService#writeArraySlice()
     */
    public void writeArraySlice(String path, MDIntArray image, long[] offset) {
        int[] mem_offset = new int[] {0, 0, 0, 0, 0};
        this.hdfWriter.int32().writeMDArrayBlockWithOffset(path, image, image.dimensions(), offset, mem_offset);
    }
    
    /* (non-Javadoc)
     * @see loci.formats.JHDFService#createGroup()
     */
    public void createGroup(String path) throws IOException{
        try {
            hdfWriter.object().createGroup(path);
        }
        catch (Exception e) {
            throw new IOException("JHDFService: Unable to create group.\n" + e.getMessage());
        }
    }
    
    /* (non-Javadoc)
     * @see loci.formats.JHDFService#exists()
     */
    public boolean exists(String path) {
        return hdfReader.exists(path);
    }

    /* (non-Javadoc)
     * @see loci.formats.JHDFService#close()
     */
    public void close() throws IOException {
        if (hdfReader != null) {
            hdfReader.close();
        }
        
        if (hdfWriter != null) {
            hdfWriter.close();
        }
        currentFile = null;
        hdfReader = null;
    }
}
