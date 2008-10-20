/*
 * $RCSfile: MediaLibAccessor.java,v $
 *
 * 
 * Copyright (c) 2005 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 
 * 
 * - Redistribution of source code must retain the above copyright 
 *   notice, this  list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in 
 *   the documentation and/or other materials provided with the
 *   distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of 
 * contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any 
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND 
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL 
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF 
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR 
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES. 
 * 
 * You acknowledge that this software is not designed or intended for 
 * use in the design, construction, operation or maintenance of any 
 * nuclear facility. 
 *
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:01:36 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.jpeg2000;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.InputStream;
import java.io.IOException;
import java.lang.NoClassDefFoundError;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;

import com.sun.media.imageioimpl.common.ImageUtil;
import com.sun.medialib.codec.jiio.Util;
import com.sun.medialib.codec.jiio.Constants;
import com.sun.medialib.codec.jiio.mediaLibImage;

/**
 *  An adapter class for presenting image data in a mediaLibImage
 *  format, even if the data isn't stored that way.  MediaLibAccessor
 *  is meant to make the common case (ComponentRasters) and allow
 *  them to be accelerated via medialib.  Note that unlike RasterAccessor,
 *  MediaLibAccessor does not work with all cases.  In the event that
 *  MediaLibAccessor can not deal with a give collection of Rasters,
 *  findCompatibleTag will return the value MediaLibAccessor.TAG_INCOMPATIBLE.
 *  OpImages that use MediaLibAccessor should be paired with RIF's
 *  which check that findCompatibleTag returns a valid tag before
 *  actually constructing the Mlib OpImage.
 */

public class MediaLibAccessor {
    /**
     *  Value indicating how far COPY_MASK info is shifted to avoid
     *  interfering with the data type info
     */
    private static final int COPY_MASK_SHIFT = 7;

    /* Value indicating how many bits the COPY_MASK is */
    private static final int COPY_MASK_SIZE = 1;

    /** The bits of a FormatTag associated with how dataArrays are obtained. */
    public static final int COPY_MASK = 0x1 << COPY_MASK_SHIFT;

    /** Flag indicating data is raster's data. */
    public static final int UNCOPIED = 0x0 << COPY_MASK_SHIFT;

    /** Flag indicating data is a copy of the raster's data. */
    public static final int COPIED = 0x01 << COPY_MASK_SHIFT;

    /** The bits of a FormatTag associated with pixel datatype. */
    public static final int DATATYPE_MASK = (0x1 << COPY_MASK_SHIFT) - 1;

    /**
     * Value indicating how far BINARY_MASK info is shifted to avoid
     * interfering with the data type and copying info.
     */
    private static final int BINARY_MASK_SHIFT =
        COPY_MASK_SHIFT+COPY_MASK_SIZE;

    /** Value indicating how many bits the BINARY_MASK is */
    private static final int BINARY_MASK_SIZE = 1;

    /** The bits of a FormatTag associated with binary data. */
    public static final int BINARY_MASK =
        ((1 << BINARY_MASK_SIZE) - 1) << BINARY_MASK_SHIFT;

    /** Flag indicating data are not binary. */
    public static final int NONBINARY = 0x0 << BINARY_MASK_SHIFT;

    /** Flag indicating data are binary. */
    public static final int BINARY = 0x1 << BINARY_MASK_SHIFT;

    /** FormatTag indicating data in byte arrays and uncopied. */
    public static final int
        TAG_BYTE_UNCOPIED = DataBuffer.TYPE_BYTE | UNCOPIED;

    /** FormatTag indicating data in unsigned short arrays and uncopied. */
    public static final int
        TAG_USHORT_UNCOPIED = DataBuffer.TYPE_USHORT | UNCOPIED;

    /** FormatTag indicating data in short arrays and uncopied. */
    public static final int
        TAG_SHORT_UNCOPIED = DataBuffer.TYPE_SHORT | UNCOPIED;

    /** FormatTag indicating data in integer arrays and uncopied. */
    public static final int
        TAG_INT_UNCOPIED = DataBuffer.TYPE_INT | UNCOPIED;

    /** FormatTag indicating data in float arrays and uncopied. */
    public static final int
        TAG_FLOAT_UNCOPIED = DataBuffer.TYPE_FLOAT | UNCOPIED;

    /** FormatTag indicating data in double arrays and uncopied. */
    public static final int
        TAG_DOUBLE_UNCOPIED = DataBuffer.TYPE_DOUBLE | UNCOPIED;

    /** FormatTag indicating data in byte arrays and uncopied. */
    public static final int
        TAG_BYTE_COPIED = DataBuffer.TYPE_BYTE | COPIED;

    /** FormatTag indicating data in unsigned short arrays and copied. */
    public static final int
        TAG_USHORT_COPIED = DataBuffer.TYPE_USHORT | COPIED;

    /** FormatTag indicating data in short arrays and copied. */
    public static final int
        TAG_SHORT_COPIED = DataBuffer.TYPE_SHORT | COPIED;

    /** FormatTag indicating data in short arrays and copied. */
    public static final int
        TAG_INT_COPIED = DataBuffer.TYPE_INT | COPIED;

    /** FormatTag indicating data in float arrays and copied. */
    public static final int
        TAG_FLOAT_COPIED = DataBuffer.TYPE_FLOAT | COPIED;

    /** FormatTag indicating data in double arrays and copied. */
    public static final int
        TAG_DOUBLE_COPIED = DataBuffer.TYPE_DOUBLE | COPIED;

    /** The raster that is the source of pixel data. */
    protected Raster raster;

    /** The rectangle of the raster that MediaLibAccessor addresses. */
    protected Rectangle rect;

    /** The number of bands per pixel in the data array. */
    protected int numBands;

    /** The offsets of each band in the src image */
    protected int bandOffsets[];

    /** Tag indicating the data type of the data and whether its copied */
    protected int formatTag;

    /** Area of mediaLib images that represent image data */
    protected mediaLibImage mlimages[] = null;

    /**
     * Whether packed data are preferred when processing binary images.
     * This tag is ignored if the data are not binary.
     */
    private boolean areBinaryDataPacked = false;

    /**
     *  Returns the most efficient FormatTag that is compatible with
     *  the destination raster and all source rasters.
     *
     *  @param srcs the source <code>Raster</code>; may be <code>null</code>.
     *  @param dst  the destination <code>Raster</code>.
     */
    public static int findCompatibleTag(Raster src) {
        SampleModel dstSM = src.getSampleModel();
        int dstDT = dstSM.getDataType();

        int defaultDataType = dstSM.getDataType();

        boolean allComponentSampleModel =
             dstSM instanceof ComponentSampleModel;
        boolean allBinary = ImageUtil.isBinary(dstSM);

        if(allBinary) {
            // The copy flag is not set until the mediaLibImage is
            // created as knowing this information requires too much
            // processing to determine here.
            return DataBuffer.TYPE_BYTE | BINARY;
        }

        if (!allComponentSampleModel) {
            if ((defaultDataType == DataBuffer.TYPE_BYTE) ||
                (defaultDataType == DataBuffer.TYPE_USHORT) ||
                (defaultDataType == DataBuffer.TYPE_SHORT)) {
                defaultDataType = DataBuffer.TYPE_INT;
            }
        }

        int tag = defaultDataType | COPIED;

        if (!allComponentSampleModel) {
            return tag;
        }

        if (isPixelSequential(dstSM))
            return dstDT | UNCOPIED;
        return tag;
    }

    /**
     *  Determines if the SampleModel stores data in a way that can
     *  be represented by a mediaLibImage without copying
     */
    public static boolean isPixelSequential(SampleModel sm) {
        ComponentSampleModel csm = null;
        if (sm instanceof ComponentSampleModel) {
            csm = (ComponentSampleModel)sm;
        } else {
            return false;
        }
        int pixelStride = csm.getPixelStride();
        int bandOffsets[] = csm.getBandOffsets();
        int bankIndices[] = csm.getBankIndices();
        if (pixelStride != bandOffsets.length) {
            return false;
        }

        //XXX: for band-selection result
        if (pixelStride != sm.getNumBands())
            return false;

        for (int i = 0; i < bandOffsets.length; i++) {
            if (bandOffsets[i] >= pixelStride ||
                bankIndices[i] != bankIndices[0]) {
                return false;
            }
            for (int j = i+1; j < bandOffsets.length; j++) {
               if (bandOffsets[i] == bandOffsets[j]) {
                   return false;
               }

               //XXX: for BGR images
               if (bandOffsets[i] != i)
                  return false;
            }
        }
        return true;
    }

    public static int getMediaLibDataType(int formatTag) {
        int dataType = formatTag & DATATYPE_MASK;
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:
                return Constants.MLIB_BYTE;
            case DataBuffer.TYPE_USHORT:
                return Constants.MLIB_USHORT;
            case DataBuffer.TYPE_SHORT:
                return Constants.MLIB_SHORT;
            case DataBuffer.TYPE_INT:
                return Constants.MLIB_INT;
            case DataBuffer.TYPE_DOUBLE:
                return Constants.MLIB_DOUBLE;
            case DataBuffer.TYPE_FLOAT:
                return Constants.MLIB_FLOAT;
        }
        return -1;
    }

    /**
     *  Constructs a MediaLibAccessor object out of a Raster, Rectangle
     *  and formatTag returned from MediaLibAccessor.findCompatibleTag().
     *
     *  In the case of binary data the copy mask bits of the formatTag
     *  will be reset within the constructor according to whether the
     *  data are in fact copied. This cannot be easily determined before
     *  the data are actually copied.
     */
    public MediaLibAccessor(Raster raster, Rectangle rect, int formatTag,
                            boolean preferPacked) {
        areBinaryDataPacked = preferPacked;

        this.raster = raster;
        this.rect = new Rectangle(rect);
        this.formatTag = formatTag;

        if(isBinary()) {
            // Set binary-specific fields and return.
            numBands = 1;
            bandOffsets = new int[] {0};

            int mlibType;
            int scanlineStride;
            byte[] bdata;
            mlimages = new mediaLibImage[1];

            if(areBinaryDataPacked) {
                mlibType = Constants.MLIB_BIT;
                scanlineStride = (rect.width+7)/8;
                bdata = ImageUtil.getPackedBinaryData(raster, rect);

                // Update format tag depending on whether the data were copied.
                if(bdata ==
                   ((DataBufferByte)raster.getDataBuffer()).getData()) {
                    this.formatTag |= UNCOPIED;
                } else {
                    this.formatTag |= COPIED;
                }
            } else { // unpacked
                mlibType = Constants.MLIB_BYTE;
                scanlineStride = rect.width;
                bdata = ImageUtil.getUnpackedBinaryData(raster, rect);
                this.formatTag |= COPIED;
            }

            mlimages[0] = new mediaLibImage(mlibType,
                                            1,
                                            rect.width,
                                            rect.height,
                                            scanlineStride,
                                            0,
                                            bdata);

            return;
        }

        if ((formatTag & COPY_MASK) == UNCOPIED) {
            ComponentSampleModel csm =
                (ComponentSampleModel)raster.getSampleModel();

            numBands = csm.getNumBands();
            bandOffsets = csm.getBandOffsets();
            int dataOffset = raster.getDataBuffer().getOffset();
            dataOffset +=
             (rect.y-raster.getSampleModelTranslateY())*csm.getScanlineStride()+
             (rect.x-raster.getSampleModelTranslateX())*csm.getPixelStride();

            // dataoffset should and is in terms of dataElements

            // scanline stride should be in terms of dataElements
            int scanlineStride = csm.getScanlineStride();

            switch (formatTag & DATATYPE_MASK) {
            case DataBuffer.TYPE_BYTE:
                DataBufferByte dbb = (DataBufferByte)raster.getDataBuffer();
                mlimages = new mediaLibImage[1];
                mlimages[0] =
                    new mediaLibImage(Constants.MLIB_BYTE,
                                      numBands,
                                      rect.width,
                                      rect.height,
                                      scanlineStride,
                                      dataOffset,
                                      dbb.getData());
                break;

            case DataBuffer.TYPE_USHORT:
                DataBufferUShort dbus =
                    (DataBufferUShort)raster.getDataBuffer();
                mlimages = new mediaLibImage[1];
                mlimages[0] =
                    new mediaLibImage(Constants.MLIB_USHORT,
                                      numBands,
                                      rect.width,
                                      rect.height,
                                      scanlineStride,
                                      dataOffset,
                                      dbus.getData());
                break;
            case DataBuffer.TYPE_SHORT:
                DataBufferShort dbs = (DataBufferShort)raster.getDataBuffer();
                mlimages = new mediaLibImage[1];
                mlimages[0] =
                    new mediaLibImage(Constants.MLIB_SHORT,
                                      numBands,
                                      rect.width,
                                      rect.height,
                                      scanlineStride,
                                      dataOffset,
                                      dbs.getData());
                break;
            case DataBuffer.TYPE_INT:
                DataBufferInt dbi = (DataBufferInt)raster.getDataBuffer();
                mlimages = new mediaLibImage[1];
                mlimages[0] =
                    new mediaLibImage(Constants.MLIB_INT,
                                      numBands,
                                      rect.width,
                                      rect.height,
                                      scanlineStride,
                                      dataOffset,
                                      dbi.getData());
                break;
            case DataBuffer.TYPE_FLOAT:
                DataBufferFloat dbf = (DataBufferFloat)raster.getDataBuffer();
                mlimages = new mediaLibImage[1];
                mlimages[0] =
                    new mediaLibImage(Constants.MLIB_FLOAT,
                                      numBands,
                                      rect.width,
                                      rect.height,
                                      scanlineStride,
                                      dataOffset,
                                      dbf.getData());
                break;
            case DataBuffer.TYPE_DOUBLE:
                DataBufferDouble dbd = (DataBufferDouble)raster.getDataBuffer();
                mlimages = new mediaLibImage[1];
                mlimages[0] =
                    new mediaLibImage(Constants.MLIB_DOUBLE,
                                      numBands,
                                      rect.width,
                                      rect.height,
                                      scanlineStride,
                                      dataOffset,
                                      dbd.getData());
                break;
            default:
                throw new IllegalArgumentException((formatTag & DATATYPE_MASK) +
                    "MediaLibAccessor does not recognize this datatype.");
            }
        } else {
            // Copying the data because we can't deal with it
            numBands = raster.getNumBands();
            bandOffsets = new int[numBands];
            for (int i = 0; i < numBands; i++) {
                bandOffsets[i] = i;
            }
            int scanlineStride = rect.width*numBands;

            switch (formatTag & DATATYPE_MASK) {
            case DataBuffer.TYPE_BYTE:
                byte bdata[] = new byte[rect.width*rect.height*numBands];
                mlimages = new mediaLibImage[1];
                mlimages[0] =
                    new mediaLibImage(Constants.MLIB_BYTE,
                                      numBands,
                                      rect.width,
                                      rect.height,
                                      scanlineStride,
                                      0,
                                      bdata);
                break;
            case DataBuffer.TYPE_USHORT:
                short usdata[] = new short[rect.width*rect.height*numBands];
                mlimages = new mediaLibImage[1];
                mlimages[0] =
                    new mediaLibImage(Constants.MLIB_USHORT,
                                      numBands,
                                      rect.width,
                                      rect.height,
                                      scanlineStride,
                                      0,
                                      usdata);
                break;
            case DataBuffer.TYPE_SHORT:
                short sdata[] = new short[rect.width*rect.height*numBands];
                mlimages = new mediaLibImage[1];
                mlimages[0] =
                    new mediaLibImage(Constants.MLIB_SHORT,
                                      numBands,
                                      rect.width,
                                      rect.height,
                                      scanlineStride,
                                      0,
                                      sdata);
                break;
            case DataBuffer.TYPE_INT:
                int idata[] = new int[rect.width*rect.height*numBands];
                mlimages = new mediaLibImage[1];
                mlimages[0] =
                    new mediaLibImage(Constants.MLIB_INT,
                                      numBands,
                                      rect.width,
                                      rect.height,
                                      scanlineStride,
                                      0,
                                      idata);
                break;
            case DataBuffer.TYPE_FLOAT:
                float fdata[] = new float[rect.width*rect.height*numBands];
                mlimages = new mediaLibImage[1];
                mlimages[0] =
                    new mediaLibImage(Constants.MLIB_FLOAT,
                                      numBands,
                                      rect.width,
                                      rect.height,
                                      scanlineStride,
                                      0,
                                      fdata);
                break;
            case DataBuffer.TYPE_DOUBLE:
                double ddata[] = new double[rect.width*rect.height*numBands];
                mlimages = new mediaLibImage[1];
                mlimages[0] =
                    new mediaLibImage(Constants.MLIB_DOUBLE,
                                      numBands,
                                      rect.width,
                                      rect.height,
                                      scanlineStride,
                                      0,
                                      ddata);
                break;
            default:
                throw new IllegalArgumentException((formatTag & DATATYPE_MASK) +
                    "MediaLibAccessor does not recognize this datatype.");
            }
            copyDataFromRaster();
        }
    }

    /**
     * Returns <code>true</code> if the <code>MediaLibAccessor</code>
     * represents binary data.
     */
    public boolean isBinary() {
        return ((formatTag & BINARY_MASK) == BINARY);
    }

    /**
     *  Returns an array of mediaLibImages which represents the input raster.
     *  An array is returned instead of a single mediaLibImage because
     *  in some cases, an input Raster can't be represented by one
     *  mediaLibImage (unless copying is done) but can be represented
     *  by several mediaLibImages without copying.
     */
    public mediaLibImage[] getMediaLibImages() {
        return mlimages;
    }

    /**
     *  Returns the data type of the RasterAccessor object. Note that
     *  this datatype is not necessarily the same data type as the
     *  underlying raster.
     */
    public int getDataType() {
        return formatTag & DATATYPE_MASK;
    }

    /**
     *  Returns true if the MediaLibAccessors's data is copied from it's
     *  raster.
     */
    public boolean isDataCopy() {
        return ((formatTag & COPY_MASK) == COPIED);
    }

    /** Returns the bandOffsets. */
    public int[] getBandOffsets() {
        return bandOffsets;
    }

    /**
     *  Returns parameters in the appropriate order if MediaLibAccessor
     *  has reordered the bands or is attempting to make a
     *  BandSequential image look like multiple PixelSequentialImages
     */
    public int[] getIntParameters(int band, int params[]) {
        int returnParams[] = new int[numBands];
        for (int i = 0; i < numBands; i++) {
            returnParams[i] = params[bandOffsets[i+band]];
        }
        return returnParams;
    }

    /**
     *  Returns parameters in the appropriate order if MediaLibAccessor
     *  has reordered the bands or is attempting to make a
     *  BandSequential image look like multiple PixelSequentialImages
     */
    public int[][] getIntArrayParameters(int band, int[][] params) {
        int returnParams[][] = new int[numBands][];
        for (int i = 0; i < numBands; i++) {
            returnParams[i] = params[bandOffsets[i+band]];
        }
        return returnParams;
    }

    /**
     *  Returns parameters in the appropriate order if MediaLibAccessor
     *  has reordered the bands or is attempting to make a
     *  BandSequential image look like multiple PixelSequentialImages
     */
    public double[] getDoubleParameters(int band, double params[]) {
        double returnParams[] = new double[numBands];
        for (int i = 0; i < numBands; i++) {
            returnParams[i] = params[bandOffsets[i+band]];
        }
        return returnParams;
    }


    /**
     *  Copy data from Raster to MediaLib image
     */
    private void copyDataFromRaster() {
        // Writeback should only be necessary on destRasters which
        // should be writable so this cast should succeed.

        if (raster.getSampleModel() instanceof ComponentSampleModel) {
            ComponentSampleModel csm =
               (ComponentSampleModel)raster.getSampleModel();
            int rasScanlineStride = csm.getScanlineStride();
            int rasPixelStride = csm.getPixelStride();

            int subRasterOffset =
             (rect.y-raster.getSampleModelTranslateY())*rasScanlineStride+
             (rect.x-raster.getSampleModelTranslateX())*rasPixelStride;

            int rasBankIndices[] = csm.getBankIndices();
            int rasBandOffsets[] = csm.getBandOffsets();
            int rasDataOffsets[] = raster.getDataBuffer().getOffsets();

            if (rasDataOffsets.length == 1) {
                for (int i = 0; i < numBands; i++) {
                    rasBandOffsets[i] += rasDataOffsets[0] +
                       subRasterOffset;
                }
            } else if (rasDataOffsets.length == rasBandOffsets.length) {
                for (int i = 0; i < numBands; i++) {
                    rasBandOffsets[i] += rasDataOffsets[i] +
                        subRasterOffset;
                }
            }

            Object mlibDataArray = null;
            switch (getDataType()) {
            case DataBuffer.TYPE_BYTE:
                byte bArray[][] = new byte[numBands][];
                for (int i = 0; i < numBands; i++) {
                    bArray[i] = mlimages[0].getByteData();
                }
                mlibDataArray = bArray;
                break;
            case DataBuffer.TYPE_USHORT:
                short usArray[][] = new short[numBands][];
                for (int i = 0; i < numBands; i++) {
                    usArray[i] = mlimages[0].getUShortData();
                }
                mlibDataArray = usArray;
                break;
            case DataBuffer.TYPE_SHORT:
                short sArray[][] = new short[numBands][];
                for (int i = 0; i < numBands; i++) {
                    sArray[i] = mlimages[0].getShortData();
                }
                mlibDataArray = sArray;
                break;
            case DataBuffer.TYPE_INT:
                int iArray[][] = new int[numBands][];
                for (int i = 0; i < numBands; i++) {
                    iArray[i] = mlimages[0].getIntData();
                }
                mlibDataArray = iArray;
                break;
            case DataBuffer.TYPE_FLOAT:
                float fArray[][] = new float[numBands][];
                for (int i = 0; i < numBands; i++) {
                    fArray[i] = mlimages[0].getFloatData();
                }
                mlibDataArray = fArray;
                break;
            case DataBuffer.TYPE_DOUBLE:
                double dArray[][] = new double[numBands][];
                for (int i = 0; i < numBands; i++) {
                    dArray[i] = mlimages[0].getDoubleData();
                }
                mlibDataArray = dArray;
                break;
            }



            Object rasDataArray = null;
            switch (csm.getDataType()) {
                case DataBuffer.TYPE_BYTE: {
                    DataBufferByte dbb =
                        (DataBufferByte)raster.getDataBuffer();
                    byte rasByteDataArray[][] = new byte[numBands][];
                    for (int i = 0; i < numBands; i++) {
                        rasByteDataArray[i] =
                            dbb.getData(rasBankIndices[i]);
                    }
                    rasDataArray = rasByteDataArray;
                    }
                    break;
                case DataBuffer.TYPE_USHORT: {
                    DataBufferUShort dbus =
                        (DataBufferUShort)raster.getDataBuffer();
                    short rasUShortDataArray[][] = new short[numBands][];
                    for (int i = 0; i < numBands; i++) {
                        rasUShortDataArray[i] =
                            dbus.getData(rasBankIndices[i]);
                    }
                    rasDataArray = rasUShortDataArray;
                    }
                    break;
                case DataBuffer.TYPE_SHORT: {
                    DataBufferShort dbs =
                        (DataBufferShort)raster.getDataBuffer();
                    short rasShortDataArray[][] = new short[numBands][];
                    for (int i = 0; i < numBands; i++) {
                        rasShortDataArray[i] =
                            dbs.getData(rasBankIndices[i]);
                    }
                    rasDataArray = rasShortDataArray;
                    }
                    break;
                case DataBuffer.TYPE_INT: {
                    DataBufferInt dbi =
                        (DataBufferInt)raster.getDataBuffer();
                    int rasIntDataArray[][] = new int[numBands][];
                    for (int i = 0; i < numBands; i++) {
                        rasIntDataArray[i] =
                            dbi.getData(rasBankIndices[i]);
                    }
                    rasDataArray = rasIntDataArray;
                    }
                    break;
                case DataBuffer.TYPE_FLOAT: {
                    DataBufferFloat dbf =
                        (DataBufferFloat)raster.getDataBuffer();
                    float rasFloatDataArray[][] = new float[numBands][];
                    for (int i = 0; i < numBands; i++) {
                        rasFloatDataArray[i] =
                            dbf.getData(rasBankIndices[i]);
                    }
                    rasDataArray = rasFloatDataArray;
                    }
                    break;
                case DataBuffer.TYPE_DOUBLE: {
                    DataBufferDouble dbd =
                        (DataBufferDouble)raster.getDataBuffer();
                    double rasDoubleDataArray[][] = new double[numBands][];
                    for (int i = 0; i < numBands; i++) {
                        rasDoubleDataArray[i] =
                            dbd.getData(rasBankIndices[i]);
                    }
                    rasDataArray = rasDoubleDataArray;
                    }
                    break;
            }


            // dst = mlib && src = ras
            Util.Reformat(
                    mlibDataArray,
                    rasDataArray,
                    numBands,
                    rect.width,rect.height,
                    getMediaLibDataType(this.getDataType()),
                    bandOffsets,
                    rect.width*numBands,
                    numBands,
                    getMediaLibDataType(csm.getDataType()),
                    rasBandOffsets,
                    rasScanlineStride,
                    rasPixelStride);
        } else {
            // If COPIED and the raster doesn't have ComponentSampleModel
            // data is moved with getPixel/setPixel (even byte/short)
            switch (getDataType()) {
            case DataBuffer.TYPE_INT:
                raster.getPixels(rect.x,rect.y,
                                 rect.width,rect.height,
                                 mlimages[0].getIntData());
                break;
            case DataBuffer.TYPE_FLOAT:
                raster.getPixels(rect.x,rect.y,
                                 rect.width,rect.height,
                                 mlimages[0].getFloatData());
                break;
            case DataBuffer.TYPE_DOUBLE:
                raster.getPixels(rect.x,rect.y,
                                 rect.width,rect.height,
                                 mlimages[0].getDoubleData());
                break;
            }
        }
    }


    /**
     *  Copies data back into the MediaLibAccessor's raster.  Note that
     *  the data is casted from the intermediate data format to
     *  the raster's format.  If clamping is needed, the call
     *  clampDataArrays() method needs to be called before
     *  calling the copyDataToRaster() method.
     */
    public void copyDataToRaster(int[] channelMap) {
        if (isDataCopy()) {

            if(isBinary()) {
                if(areBinaryDataPacked) {
                    ImageUtil.setPackedBinaryData(mlimages[0].getBitData(),
                                                  (WritableRaster)raster,
                                                  rect);
                } else { // unpacked
                    ImageUtil.setUnpackedBinaryData(mlimages[0].getByteData(),
                                                    (WritableRaster)raster,
                                                    rect);
                }
                return;
            }

            // Writeback should only be necessary on destRasters which
            // should be writable so this cast should succeed.
            WritableRaster wr = (WritableRaster)raster;

            if (wr.getSampleModel() instanceof ComponentSampleModel) {
                ComponentSampleModel csm =
                   (ComponentSampleModel)wr.getSampleModel();
                int rasScanlineStride = csm.getScanlineStride();
                int rasPixelStride = csm.getPixelStride();

                int subRasterOffset =
                 (rect.y-raster.getSampleModelTranslateY())*rasScanlineStride+
                 (rect.x-raster.getSampleModelTranslateX())*rasPixelStride;

                int rasBankIndices[] = csm.getBankIndices();
                int rasBandOffsets[] = csm.getBandOffsets();
                int rasDataOffsets[] = raster.getDataBuffer().getOffsets();

                if (rasDataOffsets.length == 1) {
                    for (int i = 0; i < numBands; i++) {
                        rasBandOffsets[i] += rasDataOffsets[0] +
                           subRasterOffset;
                    }
                } else if (rasDataOffsets.length == rasBandOffsets.length) {
                    for (int i = 0; i < numBands; i++) {
                        rasBandOffsets[i] += rasDataOffsets[i] +
                            subRasterOffset;
                    }
                }

                Object mlibDataArray = null;
                switch (getDataType()) {
                case DataBuffer.TYPE_BYTE:
                    byte bArray[][] = new byte[numBands][];
                    for (int i = 0; i < numBands; i++) {
                        bArray[i] = mlimages[0].getByteData();
                    }
                    mlibDataArray = bArray;
                    break;
                case DataBuffer.TYPE_USHORT:
                    short usArray[][] = new short[numBands][];
                    for (int i = 0; i < numBands; i++) {
                        usArray[i] = mlimages[0].getUShortData();
                    }
                    mlibDataArray = usArray;
                    break;
                case DataBuffer.TYPE_SHORT:
                    short sArray[][] = new short[numBands][];
                    for (int i = 0; i < numBands; i++) {
                        sArray[i] = mlimages[0].getShortData();
                    }
                    mlibDataArray = sArray;
                    break;
                case DataBuffer.TYPE_INT:
                    int iArray[][] = new int[numBands][];
                    for (int i = 0; i < numBands; i++) {
                        iArray[i] = mlimages[0].getIntData();
                    }
                    mlibDataArray = iArray;
                    break;
                case DataBuffer.TYPE_FLOAT:
                    float fArray[][] = new float[numBands][];
                    for (int i = 0; i < numBands; i++) {
                        fArray[i] = mlimages[0].getFloatData();
                    }
                    mlibDataArray = fArray;
                    break;
                case DataBuffer.TYPE_DOUBLE:
                    double dArray[][] = new double[numBands][];
                    for (int i = 0; i < numBands; i++) {
                        dArray[i] = mlimages[0].getDoubleData();
                    }
                    mlibDataArray = dArray;
                    break;
                }


		byte tmpDataArray[] = null;
                Object rasDataArray = null;
                switch (csm.getDataType()) {
                    case DataBuffer.TYPE_BYTE: {
                        DataBufferByte dbb =
                            (DataBufferByte)raster.getDataBuffer();
                        byte rasByteDataArray[][] = new byte[numBands][];
                        for (int i = 0; i < numBands; i++) {
                            rasByteDataArray[i] =
                                dbb.getData(rasBankIndices[i]);
                        }
			tmpDataArray =  rasByteDataArray[0];
                        rasDataArray = rasByteDataArray;
                        }
                        break;
                    case DataBuffer.TYPE_USHORT: {
                        DataBufferUShort dbus =
                            (DataBufferUShort)raster.getDataBuffer();
                        short rasUShortDataArray[][] = new short[numBands][];
                        for (int i = 0; i < numBands; i++) {
                            rasUShortDataArray[i] =
                                dbus.getData(rasBankIndices[i]);
                        }
                        rasDataArray = rasUShortDataArray;
                        }
                        break;
                    case DataBuffer.TYPE_SHORT: {
                        DataBufferShort dbs =
                            (DataBufferShort)raster.getDataBuffer();
                        short rasShortDataArray[][] = new short[numBands][];
                        for (int i = 0; i < numBands; i++) {
                            rasShortDataArray[i] =
                                dbs.getData(rasBankIndices[i]);
                        }
                        rasDataArray = rasShortDataArray;
                        }
                        break;
                    case DataBuffer.TYPE_INT: {
                        DataBufferInt dbi =
                            (DataBufferInt)raster.getDataBuffer();
                        int rasIntDataArray[][] = new int[numBands][];
                        for (int i = 0; i < numBands; i++) {
                            rasIntDataArray[i] =
                                dbi.getData(rasBankIndices[i]);
                        }
                        rasDataArray = rasIntDataArray;
                        }
                        break;
                    case DataBuffer.TYPE_FLOAT: {
                        DataBufferFloat dbf =
                            (DataBufferFloat)raster.getDataBuffer();
                        float rasFloatDataArray[][] = new float[numBands][];
                        for (int i = 0; i < numBands; i++) {
                            rasFloatDataArray[i] =
                                dbf.getData(rasBankIndices[i]);
                        }
                        rasDataArray = rasFloatDataArray;
                        }
                        break;
                    case DataBuffer.TYPE_DOUBLE: {
                        DataBufferDouble dbd =
                            (DataBufferDouble)raster.getDataBuffer();
                        double rasDoubleDataArray[][] = new double[numBands][];
                        for (int i = 0; i < numBands; i++) {
                            rasDoubleDataArray[i] =
                                dbd.getData(rasBankIndices[i]);
                        }
                        rasDataArray = rasDoubleDataArray;
                        }
                        break;
                }

                int[] bandOffsetCopy = (int[])bandOffsets.clone();
                if (channelMap != null) {
                    for (int i = 0; i < bandOffsetCopy.length; i++)
                        bandOffsetCopy[i] = channelMap[bandOffsetCopy[i]];
                }

                // src = mlib && dst = ras
                Util.Reformat(
                        rasDataArray,
                        mlibDataArray,
                        numBands,
                        rect.width,rect.height,
                        getMediaLibDataType(csm.getDataType()),
                        rasBandOffsets,
                        rasScanlineStride,
                        rasPixelStride,
                        getMediaLibDataType(this.getDataType()),
                        bandOffsetCopy,
                        rect.width*numBands,
                        numBands);
            } else {
                // If COPIED and the raster doesn't have ComponentSampleModel
                // data is moved with getPixel/setPixel (even byte/short)
                switch (getDataType()) {
                case DataBuffer.TYPE_INT:
                    wr.setPixels(rect.x,rect.y,
                                 rect.width,rect.height,
                                 mlimages[0].getIntData());
                    break;
                case DataBuffer.TYPE_FLOAT:
                    wr.setPixels(rect.x,rect.y,
                                 rect.width,rect.height,
                                 mlimages[0].getFloatData());
                    break;
                case DataBuffer.TYPE_DOUBLE:
                    wr.setPixels(rect.x,rect.y,
                                 rect.width,rect.height,
                                 mlimages[0].getDoubleData());
                    break;
                }
            }
        }
    }

    /**
     * Clamps data array values to a range that the underlying raster
     * can deal with.  For example, if the underlying raster stores
     * data as bytes, but the samples ares unpacked into integer arrays by
     * the RasterAccessor object for an operation, the operation will
     * need to call clampDataArrays() so that the data in the int
     * arrays is restricted to the range 0..255 before a setPixels()
     * call is made on the underlying raster.  Note that some
     * operations (for example, lookup) can guarantee that their
     * results don't need clamping so they can call
     * RasterAccessor.copyDataToRaster() without first calling this
     * function.
     */
    public void clampDataArrays () {
        if (!isDataCopy()) {
            return;
        }

        // additonal medialib check:  If it's a componentSampleModel
        // we get a free cast when we call medialibWrapper.Reformat
        // to copy the data to the source.  So we don't need to cast
        // here.
        if (raster.getSampleModel() instanceof ComponentSampleModel) {
            return;
        }

        int bits[] = raster.getSampleModel().getSampleSize();

        // Do we even need a clamp?  We do if there's any band
        // of the source image stored in that's less than 32 bits
        // and is stored in a byte, short or int format.  (The automatic
        // cast's between floats/doubles and 32-bit ints in setPixel()
        // generall do what we want.)

        boolean needClamp = false;
        boolean uniformBitSize = true;
        for (int i = 0; i < bits.length; i++) {
            int bitSize = bits[0];
            if (bits[i] < 32) {
                needClamp = true;
            }
            if (bits[i] != bitSize) {
               uniformBitSize = false;
            }
        }

        if (!needClamp) {
            return;
        }

        int dataType = raster.getDataBuffer().getDataType();
        double hiVals[] = new double[bits.length];
        double loVals[] = new double[bits.length];

        if (dataType == DataBuffer.TYPE_USHORT &&
            uniformBitSize && bits[0] == 16) {
            for (int i = 0; i < bits.length; i++) {
                hiVals[i] = (double)0xFFFF;
                loVals[i] = (double)0;
            }
        } else if (dataType == DataBuffer.TYPE_SHORT &&
            uniformBitSize && bits[0] == 16) {
            for (int i = 0; i < bits.length; i++) {
                hiVals[i] = (double)Short.MAX_VALUE;
                loVals[i] = (double)Short.MIN_VALUE;
            }
        } else if (dataType == DataBuffer.TYPE_INT &&
            uniformBitSize && bits[0] == 32) {
            for (int i = 0; i < bits.length; i++) {
                hiVals[i] = (double)Integer.MAX_VALUE;
                loVals[i] = (double)Integer.MIN_VALUE;
            }
        } else {
            for (int i = 0; i < bits.length; i++) {
                hiVals[i] = (double)((1 << bits[i]) - 1);
                loVals[i] = (double)0;
            }
        }
        clampDataArray(hiVals,loVals);
    }

    private void clampDataArray(double hiVals[], double loVals[]) {
        switch (getDataType()) {
        case DataBuffer.TYPE_INT:
            clampIntArrays(toIntArray(hiVals),toIntArray(loVals));
            break;
        case DataBuffer.TYPE_FLOAT:
            clampFloatArrays(toFloatArray(hiVals),toFloatArray(loVals));
            break;
        case DataBuffer.TYPE_DOUBLE:
            clampDoubleArrays(hiVals,loVals);
            break;
        }
    }

    private int[] toIntArray(double vals[]) {
        int returnVals[] = new int[vals.length];
        for (int i = 0; i < vals.length; i++) {
            returnVals[i] = (int)vals[i];
        }
        return returnVals;
    }

    private float[] toFloatArray(double vals[]) {
        float returnVals[] = new float[vals.length];
        for (int i = 0; i < vals.length; i++) {
            returnVals[i] = (float)vals[i];
        }
        return returnVals;
    }

    private void clampIntArrays(int hiVals[], int loVals[]) {
        int width = rect.width;
        int height = rect.height;
        int scanlineStride = numBands*width;
        for (int k = 0; k < numBands; k++)  {
            int data[] = mlimages[0].getIntData();
            int scanlineOffset = k;
            int hiVal = hiVals[k];
            int loVal = loVals[k];
            for (int j = 0; j < height; j++)  {
                int pixelOffset = scanlineOffset;
                for (int i = 0; i < width; i++)  {
                    int tmp = data[pixelOffset];
                    if (tmp < loVal) {
                        data[pixelOffset] = loVal;
                    } else if (tmp > hiVal) {
                        data[pixelOffset] = hiVal;
                    }
                    pixelOffset += numBands;
                }
                scanlineOffset += scanlineStride;
            }
        }
    }

    private void clampFloatArrays(float hiVals[], float loVals[]) {
        int width = rect.width;
        int height = rect.height;
        int scanlineStride = numBands*width;
        for (int k = 0; k < numBands; k++)  {
            float data[] =  mlimages[0].getFloatData();
            int scanlineOffset = k;
            float hiVal = hiVals[k];
            float loVal = loVals[k];
            for (int j = 0; j < height; j++)  {
                int pixelOffset = scanlineOffset;
                for (int i = 0; i < width; i++)  {
                    float tmp = data[pixelOffset];
                    if (tmp < loVal) {
                        data[pixelOffset] = loVal;
                    } else if (tmp > hiVal) {
                        data[pixelOffset] = hiVal;
                    }
                    pixelOffset += numBands;
                }
                scanlineOffset += scanlineStride;
            }
        }
    }

    private void clampDoubleArrays(double hiVals[], double loVals[]) {
        int width = rect.width;
        int height = rect.height;
        int scanlineStride = numBands*width;
        for (int k = 0; k < numBands; k++)  {
            double data[] = mlimages[0].getDoubleData();
            int scanlineOffset = k;
            double hiVal = hiVals[k];
            double loVal = loVals[k];
            for (int j = 0; j < height; j++)  {
                int pixelOffset = scanlineOffset;
                for (int i = 0; i < width; i++)  {
                    double tmp = data[pixelOffset];
                    if (tmp < loVal) {
                        data[pixelOffset] = loVal;
                    } else if (tmp > hiVal) {
                        data[pixelOffset] = hiVal;
                    }
                    pixelOffset += numBands;
                }
                scanlineOffset += scanlineStride;
            }
        }
    }

}

