/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2015 Open Microscopy Environment:
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
 * $RCSfile: TIFFJPEGDecompressor.java,v $
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
 * $Revision: 1.2 $
 * $Date: 2006/04/11 22:10:36 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.tiff;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.stream.ImageInputStream;
import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.TIFFDecompressor;
import com.sun.media.imageio.plugins.tiff.TIFFField;

public class TIFFJPEGDecompressor extends TIFFDecompressor {
    private static final boolean DEBUG = false; // XXX false for release.

    // Start of Image
    protected static final int SOI = 0xD8;

    // End of Image
    protected static final int EOI = 0xD9;

    //private static ImageReaderSpi jpegReaderSPI = null;//XXX

    protected ImageReader JPEGReader = null;
    protected ImageReadParam JPEGParam;

    protected boolean hasJPEGTables = false;
    protected byte[] tables = null;

    private byte[] data = new byte[0];

    /* XXX
    static {
        try {
            IIORegistry registry = IIORegistry.getDefaultInstance();
            Class imageReaderClass =
                Class.forName("javax.imageio.spi.ImageReaderSpi");
            Iterator readerSPIs =
                registry.getServiceProviders(imageReaderClass,
                                             new JPEGSPIFilter(),
                                             true);
            if(readerSPIs.hasNext()) {
                jpegReaderSPI = (ImageReaderSpi)readerSPIs.next();
            }
        } catch(Exception e) {
            // Ignore it ...
        }
    }
    */

    public TIFFJPEGDecompressor() {}

    /* XXX
    private static class JPEGSPIFilter implements ServiceRegistry.Filter {
        JPEGSPIFilter() {}

        public boolean filter(Object provider) {
            ImageReaderSpi readerSPI = (ImageReaderSpi)provider;

            if(readerSPI.getPluginClassName().startsWith("com.sun.imageio")) {
                String streamMetadataName =
                    readerSPI.getNativeStreamMetadataFormatName();
                if(streamMetadataName != null) {
                    return streamMetadataName.indexOf("jpeg_stream") != -1;
                } else {
                    return false;
                }
            }

            return false;
        }
    }
    */

    public void beginDecoding() {
        // Initialize the JPEG reader if needed.
        if(this.JPEGReader == null) {
            if(DEBUG) System.out.println("Initializing JPEGReader");

            /* XXX
            if(this.jpegReaderSPI != null) {
                try {
                    this.JPEGReader = jpegReaderSPI.createReaderInstance();
                } catch(Exception e) {
                }
            }

            if(this.JPEGReader == null) {
            */

            // Get all JPEG readers.
            Iterator iter = ImageIO.getImageReadersByFormatName("jpeg");

            if(!iter.hasNext()) {
                // XXX The exception thrown should be an IIOException.
                throw new IllegalStateException("No JPEG readers found!");
            }

            // Initialize reader to the first one.
            this.JPEGReader = (ImageReader)iter.next();

            if(DEBUG) System.out.println("Using "+
                                         JPEGReader.getClass().getName());

            this.JPEGParam = JPEGReader.getDefaultReadParam();
        }

        // Get the JPEGTables field.
        TIFFImageMetadata tmetadata = (TIFFImageMetadata)metadata;
        TIFFField f =
            tmetadata.getTIFFField(BaselineTIFFTagSet.TAG_JPEG_TABLES);

        if (f != null) {
            this.hasJPEGTables = true;
            this.tables = f.getAsBytes();
        } else {
            this.hasJPEGTables = false;
        }
    }

    public void decodeRaw(byte[] b,
                          int dstOffset,
                          int bitsPerPixel,
                          int scanlineStride) throws IOException {
        // Seek to the data position for this segment.
        stream.seek(offset);

        // Set the stream variable depending on presence of JPEGTables.
        ImageInputStream is;
        if(this.hasJPEGTables) {
            if(DEBUG) System.out.println("Reading abbreviated stream.");
            // The current strip or tile is an abbreviated JPEG stream.

            // Reallocate memory if there is not enough already.
            int dataLength = tables.length + byteCount;
            if(data.length < dataLength) {
                data = new byte[dataLength];
            }

            // Copy the tables ignoring any EOI and subsequent bytes.
            int dataOffset = tables.length;
            for(int i = tables.length - 2; i > 0; i--) {
                if((tables[i] & 0xff) == 0xff &&
                   (tables[i+1] & 0xff) == EOI) {
                    dataOffset = i;
                    break;
                }
            }
            System.arraycopy(tables, 0, data, 0, dataOffset);

            // Check for SOI and skip it if present.
            byte byte1 = (byte)stream.read();
            byte byte2 = (byte)stream.read();
            if(!((byte1 & 0xff) == 0xff && (byte2 & 0xff) == SOI)) {
                data[dataOffset++] = (byte)byte1;
                data[dataOffset++] = (byte)byte2;
            }

            // Read remaining data.
            stream.readFully(data, dataOffset, byteCount - 2);

            // Create ImageInputStream.
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            is = new MemoryCacheImageInputStream(bais);
        } else {
            if(DEBUG) System.out.println("Reading complete stream.");
            // The current strip or tile is a complete JPEG stream.
            is = stream;
        }

        // Set the stream on the reader.
        JPEGReader.setInput(is, false, true);

        // Set the destination to the raw image ignoring the parameters.
        JPEGParam.setDestination(rawImage);

        // Read the strip or tile.
        JPEGReader.read(0, JPEGParam);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        JPEGReader.dispose();
    }
}
