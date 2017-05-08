/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2017 Open Microscopy Environment:
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

/*
 * $RCSfile: TIFFJPEGCompressor.java,v $
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
 * $Revision: 1.3 $
 * $Date: 2006/04/11 22:10:36 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.tiff;

import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.TIFFField;
import com.sun.media.imageio.plugins.tiff.TIFFTag;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

/**
 * Compressor for encoding compression type 7, TTN2/Adobe JPEG-in-TIFF.
 */
public class TIFFJPEGCompressor extends TIFFBaseJPEGCompressor {

    private static final boolean DEBUG = false; // XXX false for release.

    // Subsampling factor for chroma bands (Cb Cr).
    static final int CHROMA_SUBSAMPLING = 2;

    /**
     * A filter which identifies the ImageReaderSpi of a JPEG reader
     * which supports JPEG native stream metadata.
     */
    private static class JPEGSPIFilter implements ServiceRegistry.Filter {
        JPEGSPIFilter() {}

        public boolean filter(Object provider) {
            ImageReaderSpi readerSPI = (ImageReaderSpi)provider;

            if(readerSPI != null) {
                String streamMetadataName =
                    readerSPI.getNativeStreamMetadataFormatName();
                if(streamMetadataName != null) {
                    return streamMetadataName.equals(STREAM_METADATA_NAME);
                } else {
                    return false;
                }
            }

            return false;
        }
    }

    /**
     * Retrieves a JPEG reader which supports native JPEG stream metadata.
     */
    private static ImageReader getJPEGTablesReader() {
        ImageReader jpegReader = null;

        try {
            IIORegistry registry = IIORegistry.getDefaultInstance();
            Class imageReaderClass =
                Class.forName("javax.imageio.spi.ImageReaderSpi");
            Iterator readerSPIs =
                registry.getServiceProviders(imageReaderClass,
                                             new JPEGSPIFilter(),
                                             true);
            if(readerSPIs.hasNext()) {
                ImageReaderSpi jpegReaderSPI =
                    (ImageReaderSpi)readerSPIs.next();
                jpegReader = jpegReaderSPI.createReaderInstance();
            }
        } catch(Exception e) {
            // Ignore it ...
        }

        return jpegReader;
    }

    public TIFFJPEGCompressor(ImageWriteParam param) {
        super("JPEG", BaselineTIFFTagSet.COMPRESSION_JPEG, false, param);
    }

    /**
     * Sets the value of the <code>metadata</code> field.
     *
     * <p>The implementation in this class also adds the TIFF fields
     * JPEGTables, YCbCrSubSampling, YCbCrPositioning, and
     * ReferenceBlackWhite superseding any prior settings of those
     * fields.</p>
     *
     * @param metadata the <code>IIOMetadata</code> object for the
     * image being written.
     *
     * @see #getMetadata()
     */
    public void setMetadata(IIOMetadata metadata) {
        super.setMetadata(metadata);

        if (metadata instanceof TIFFImageMetadata) {
            TIFFImageMetadata tim = (TIFFImageMetadata)metadata;
            TIFFIFD rootIFD = tim.getRootIFD();
            BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();

            TIFFField f =
                tim.getTIFFField(BaselineTIFFTagSet.TAG_SAMPLES_PER_PIXEL);
            int numBands = f.getAsInt(0);

            if(numBands == 1) {
                // Remove YCbCr fields not relevant for grayscale.

                rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_Y_CB_CR_SUBSAMPLING);
                rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_Y_CB_CR_POSITIONING);
                rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_REFERENCE_BLACK_WHITE);
            } else { // numBands == 3
                // Replace YCbCr fields.

                // YCbCrSubSampling
                TIFFField YCbCrSubSamplingField = new TIFFField
                    (base.getTag(BaselineTIFFTagSet.TAG_Y_CB_CR_SUBSAMPLING),
                     TIFFTag.TIFF_SHORT, 2,
                     new char[] {CHROMA_SUBSAMPLING, CHROMA_SUBSAMPLING});
                rootIFD.addTIFFField(YCbCrSubSamplingField);

                // YCbCrPositioning
                TIFFField YCbCrPositioningField = new TIFFField
                    (base.getTag(BaselineTIFFTagSet.TAG_Y_CB_CR_POSITIONING),
                     TIFFTag.TIFF_SHORT, 1,
                     new char[]
                        {BaselineTIFFTagSet.Y_CB_CR_POSITIONING_CENTERED});
                rootIFD.addTIFFField(YCbCrPositioningField);

                // ReferenceBlackWhite
                TIFFField referenceBlackWhiteField = new TIFFField
                    (base.getTag(BaselineTIFFTagSet.TAG_REFERENCE_BLACK_WHITE),
                     TIFFTag.TIFF_RATIONAL, 6,
                     new long[][] { // no headroon/footroom
                         {0, 1}, {255, 1},
                         {128, 1}, {255, 1},
                         {128, 1}, {255, 1}
                     });
                rootIFD.addTIFFField(referenceBlackWhiteField);
            }

            // JPEGTables field is written if and only if one is
            // already present in the metadata. If one is present
            // and has either zero length or does not represent a
            // valid tables-only stream, then a JPEGTables field
            // will be written initialized to the standard tables-
            // only stream written by the JPEG writer.

            // Retrieve the JPEGTables field.
            TIFFField JPEGTablesField =
                tim.getTIFFField(BaselineTIFFTagSet.TAG_JPEG_TABLES);

            // Initialize JPEG writer to one supporting abbreviated streams.
            if(JPEGTablesField != null) {
                // Intialize the JPEG writer to one that supports stream
                // metadata, i.e., abbreviated streams, and may or may not
                // support image metadata.
                initJPEGWriter(true, false);
            }

            // Write JPEGTables field if a writer supporting abbreviated
            // streams was available.
            if(JPEGTablesField != null && JPEGWriter != null) {
                if(DEBUG) System.out.println("Has JPEGTables ...");

                // Set the abbreviated stream flag.
                this.writeAbbreviatedStream = true;

                //Branch based on field value count.
                if(JPEGTablesField.getCount() > 0) {
                    if(DEBUG) System.out.println("JPEGTables > 0");

                    // Derive the stream metadata from the field.

                    // Get the field values.
                    byte[] tables = JPEGTablesField.getAsBytes();

                    // Create an input stream for the tables.
                    ByteArrayInputStream bais =
                        new ByteArrayInputStream(tables);
                    MemoryCacheImageInputStream iis =
                        new MemoryCacheImageInputStream(bais);

                    // Read the tables stream using the JPEG reader.
                    ImageReader jpegReader = getJPEGTablesReader();
                    jpegReader.setInput(iis);

                    // Initialize the stream metadata object.
                    try {
                        JPEGStreamMetadata = jpegReader.getStreamMetadata();
                    } catch(Exception e) {
                        // Fall back to default tables.
                        JPEGStreamMetadata = null;
                    } finally {
                        jpegReader.reset();
                    }
                    if(DEBUG) System.out.println(JPEGStreamMetadata);
                }

                if(JPEGStreamMetadata == null) {
                    if(DEBUG) System.out.println("JPEGTables == 0");

                    // Derive the field from default stream metadata.

                    // Get default stream metadata.
                    JPEGStreamMetadata =
                        JPEGWriter.getDefaultStreamMetadata(JPEGParam);

                    // Create an output stream for the tables.
                    ByteArrayOutputStream tableByteStream =
                        new ByteArrayOutputStream();
                    MemoryCacheImageOutputStream tableStream =
                        new MemoryCacheImageOutputStream(tableByteStream);

                    // Write a tables-only stream.
                    JPEGWriter.setOutput(tableStream);
                    try {
                        JPEGWriter.prepareWriteSequence(JPEGStreamMetadata);
                        tableStream.flush();
                        JPEGWriter.endWriteSequence();

                        // Get the tables-only stream content.
                        byte[] tables = tableByteStream.toByteArray();
                        if(DEBUG) System.out.println("tables.length = "+
                                                     tables.length);

                        // Add the JPEGTables field.
                        JPEGTablesField = new TIFFField
                            (base.getTag(BaselineTIFFTagSet.TAG_JPEG_TABLES),
                             TIFFTag.TIFF_UNDEFINED,
                             tables.length,
                             tables);
                        rootIFD.addTIFFField(JPEGTablesField);
                    } catch(Exception e) {
                        // Do not write JPEGTables field.
                        rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_JPEG_TABLES);
                        this.writeAbbreviatedStream = false;
                    }
                }
            } else { // Do not write JPEGTables field.
                // Remove any field present.
                rootIFD.removeTIFFField(BaselineTIFFTagSet.TAG_JPEG_TABLES);

                // Initialize the writer preferring codecLib.
                initJPEGWriter(false, false);
            }
        }
    }
}
