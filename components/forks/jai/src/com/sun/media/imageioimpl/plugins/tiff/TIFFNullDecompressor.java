/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2016 Open Microscopy Environment:
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
 * $RCSfile: TIFFNullDecompressor.java,v $
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
 * $Date: 2005/02/11 05:01:48 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.tiff;

import java.io.IOException;
import com.sun.media.imageio.plugins.tiff.TIFFDecompressor;

public class TIFFNullDecompressor extends TIFFDecompressor {

    private static final boolean DEBUG = false; // XXX false for release!

    /**
     * Whether to read the active source region only.
     */
    private boolean isReadActiveOnly = false;

    /** The original value of <code>srcMinX</code>. */
    private int originalSrcMinX;

    /** The original value of <code>srcMinY</code>. */
    private int originalSrcMinY;

    /** The original value of <code>srcWidth</code>. */
    private int originalSrcWidth;

    /** The original value of <code>srcHeight</code>. */
    private int originalSrcHeight;

    public TIFFNullDecompressor() {}

    //
    // This approach to reading the active reading is a hack of sorts
    // as the original values of the entire source region are stored,
    // overwritten, and then restored. It would probably be better to
    // revise TIFFDecompressor such that this were not necessary, i.e.,
    // change beginDecoding() and decode() to use the active region values
    // when random access is easy and the entire region values otherwise.
    //
    public void beginDecoding() {
        // Determine number of bits per pixel.
        int bitsPerPixel = 0;
        for(int i = 0; i < bitsPerSample.length; i++) {
            bitsPerPixel += bitsPerSample[i];
        }

        // Can read active region only if row starts on a byte boundary.
        if((activeSrcMinX != srcMinX || activeSrcMinY != srcMinY ||
            activeSrcWidth != srcWidth || activeSrcHeight != srcHeight) &&
           ((activeSrcMinX - srcMinX)*bitsPerPixel) % 8 == 0) {
            // Set flag.
            isReadActiveOnly = true;

            // Cache original region.
            originalSrcMinX = srcMinX;
            originalSrcMinY = srcMinY;
            originalSrcWidth = srcWidth;
            originalSrcHeight = srcHeight;

            // Replace region with active region.
            srcMinX = activeSrcMinX;
            srcMinY = activeSrcMinY;
            srcWidth = activeSrcWidth;
            srcHeight = activeSrcHeight;
        } else {
            // Clear flag.
            isReadActiveOnly = false;
        }

        if(DEBUG) {
            if(isReadActiveOnly) {
                System.out.println("Reading active region.");
                System.out.println("source region: "+
                                   new java.awt.Rectangle(originalSrcMinX,
                                                          originalSrcMinY,
                                                          originalSrcWidth,
                                                          originalSrcHeight));
                System.out.println("active region: "+
                                   new java.awt.Rectangle(activeSrcMinX,
                                                          activeSrcMinY,
                                                          activeSrcWidth,
                                                          activeSrcHeight));
            } else {
                System.out.println("Reading entire region.");
                System.out.println("source region: "+
                                   new java.awt.Rectangle(srcMinX,
                                                          srcMinY,
                                                          srcWidth,
                                                          srcHeight));
            }
            System.out.println("destination region: "+
                               new java.awt.Rectangle(dstMinX,
                                                      dstMinY,
                                                      dstWidth,
                                                      dstHeight));
        }

        super.beginDecoding();
    }

    public void decode() throws IOException {
        super.decode();

        // Reset state.
        if(isReadActiveOnly) {
            // Restore original source region values.
            srcMinX = originalSrcMinX;
            srcMinY = originalSrcMinY;
            srcWidth = originalSrcWidth;
            srcHeight = originalSrcHeight;

            // Unset flag.
            isReadActiveOnly = false;
        }
    }

    public void decodeRaw(byte[] b,
                          int dstOffset,
                          int bitsPerPixel,
                          int scanlineStride) throws IOException {
        if(isReadActiveOnly) {
            // Read the active source region only.

            int activeBytesPerRow = (activeSrcWidth*bitsPerPixel + 7)/8;
            int totalBytesPerRow = (originalSrcWidth*bitsPerPixel + 7)/8;
            int bytesToSkipPerRow = totalBytesPerRow - activeBytesPerRow;

            //
            // Seek to the start of the active region:
            //
            // active offset = original offset +
            //                 number of bytes to start of first active row +
            //                 number of bytes to first active pixel within row
            //
            // Since the condition for reading from the active region only is
            //
            //     ((activeSrcMinX - srcMinX)*bitsPerPixel) % 8 == 0
            //
            // the bit offset to the first active pixel within the first
            // active row is a multiple of 8.
            //
            stream.seek(offset +
                        (activeSrcMinY - originalSrcMinY)*totalBytesPerRow +
                        ((activeSrcMinX - originalSrcMinX)*bitsPerPixel)/8);

            int lastRow = activeSrcHeight - 1;
            for (int y = 0; y < activeSrcHeight; y++) {
                stream.read(b, dstOffset, activeBytesPerRow);
                dstOffset += scanlineStride;

                // Skip unneeded bytes (row suffix + row prefix).
                if(y != lastRow) {
                    stream.skipBytes(bytesToSkipPerRow);
                }
            }
        } else {
            // Read the entire source region.
            stream.seek(offset);
            int bytesPerRow = (srcWidth*bitsPerPixel + 7)/8;
            if(bytesPerRow == scanlineStride) {
                stream.read(b, dstOffset, bytesPerRow*srcHeight);
            } else {
                for (int y = 0; y < srcHeight; y++) {
                    stream.read(b, dstOffset, bytesPerRow);
                    dstOffset += scanlineStride;
                }
            }
        }
    }
}
