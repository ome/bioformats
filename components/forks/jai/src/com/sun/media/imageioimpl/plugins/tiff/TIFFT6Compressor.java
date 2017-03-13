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
 * $RCSfile: TIFFT6Compressor.java,v $
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
 * $Date: 2006/04/11 22:10:37 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.tiff;

import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.TIFFCompressor;
import com.sun.media.imageio.plugins.tiff.TIFFField;
import com.sun.media.imageio.plugins.tiff.TIFFTag;
import java.io.IOException;
import javax.imageio.IIOException;

/**
 */
public class TIFFT6Compressor extends TIFFFaxCompressor {

    public TIFFT6Compressor() {
        super("CCITT T.6", BaselineTIFFTagSet.COMPRESSION_CCITT_T_6, true);
    }
    
    /**
     * Encode a buffer of data using CCITT T.6 Compression also known as
     * Group 4 facsimile compression.
     *
     * @param data        The row of data to compress.
     * @param lineStride  Byte step between the same sample in different rows.
     * @param colOffset   Bit offset within first <code>data[rowOffset]</code>.
     * @param width       Number of bits in the row.
     * @param height      Number of rows in the buffer.
     * @param compData    The compressed data.
     *
     * @return The number of bytes saved in the compressed data array.
     */
    public synchronized int encodeT6(byte[] data,
                                     int lineStride,
                                     int colOffset,
                                     int width,
                                     int height,
                                     byte[] compData)
    {
        //
        // ao, a1, a2 are bit indices in the current line
        // b1 and b2  are bit indices in the reference line (line above)
        // color is the current color (WHITE or BLACK)
        //
        byte[] refData = null;
        int refAddr  = 0;
        int lineAddr = 0;
        int  outIndex = 0;

        initBitBuf();

        //
        // Iterate over all lines
        //
        while(height-- != 0) {
            int a0   = colOffset;
            int last = a0 + width;

            int testbit =
                ((data[lineAddr + (a0>>>3)]&0xff) >>>
                 (7-(a0 & 0x7))) & 0x1;
            int a1 = testbit != 0 ?
                a0 : nextState(data, lineAddr, a0, last);

            testbit = refData == null ?
                0: ((refData[refAddr + (a0>>>3)]&0xff) >>>
                       (7-(a0 & 0x7))) & 0x1;
            int b1 = testbit != 0 ?
                a0 : nextState(refData, refAddr, a0, last);

            //
            // The current color is set to WHITE at line start
            //
            int color = WHITE;

            while(true) {
                int b2 = nextState(refData, refAddr, b1, last);
                if(b2 < a1) {          // pass mode
                    outIndex += add2DBits(compData, outIndex, pass, 0);
                    a0 = b2;
                } else {
                    int tmp = b1 - a1 + 3;
                    if((tmp <= 6) && (tmp >= 0)) { // vertical mode
                        outIndex += add2DBits(compData, outIndex, vert, tmp);
                        a0 = a1;
                    } else {            // horizontal mode
                        int a2 = nextState(data, lineAddr, a1, last);
                        outIndex += add2DBits(compData, outIndex, horz, 0);
                        outIndex += add1DBits(compData, outIndex, a1-a0, color);
                        outIndex += add1DBits(compData, outIndex, a2-a1, color^1);
                        a0 = a2;
                    }
                }
                if(a0 >= last) {
                    break;
                }
                color = ((data[lineAddr + (a0>>>3)]&0xff) >>>
                         (7-(a0 & 0x7))) & 0x1;
                a1 = nextState(data, lineAddr, a0, last);
                b1 = nextState(refData, refAddr, a0, last);
                testbit = refData == null ?
                    0: ((refData[refAddr + (b1>>>3)]&0xff) >>>
                           (7-(b1 & 0x7))) & 0x1;
                if(testbit == color) {
                    b1 = nextState(refData, refAddr, b1, last);
                }
            }

            refData = data;
            refAddr = lineAddr;
            lineAddr += lineStride;

        } // End while(height--)

        //
        // append eofb
        //
        outIndex += addEOFB(compData, outIndex);

        // Flip the bytes if inverse fill was requested.
        if(inverseFill) {
            for(int i = 0; i < outIndex; i++) {
                compData[i] = TIFFFaxDecompressor.flipTable[compData[i]&0xff];
            }
        }

        return outIndex;
    }

    public int encode(byte[] b, int off,
                      int width, int height,
                      int[] bitsPerSample,
                      int scanlineStride) throws IOException {
        if (bitsPerSample.length != 1 || bitsPerSample[0] != 1) {
            throw new IIOException(
                             "Bits per sample must be 1 for T6 compression!"); 
        }


        if (metadata instanceof TIFFImageMetadata) {
            TIFFImageMetadata tim = (TIFFImageMetadata)metadata;

            long[] options = new long[1];
            options[0] = 0;
            
            BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();
            TIFFField T6Options =
                new TIFFField(base.getTag(BaselineTIFFTagSet.TAG_T6_OPTIONS),
                              TIFFTag.TIFF_LONG,
                              1,
                              options);
            tim.rootIFD.addTIFFField(T6Options);
        }
         
        // See comment in TIFFT4Compressor
        int maxBits = 9*((width + 1)/2) + 2;
        int bufSize = (maxBits + 7)/8;
        bufSize = height*(bufSize + 2) + 12;

        byte[] compData = new byte[bufSize];
        int bytes = encodeT6(b, scanlineStride, 8*off, width, height,
                             compData);
        stream.write(compData, 0, bytes);
        return bytes;
    }
}
