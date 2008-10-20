/*
 * $RCSfile: TIFFLSBDecompressor.java,v $
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

public class TIFFLSBDecompressor extends TIFFDecompressor {

    /**
     * Table for flipping bytes from LSB-to-MSB to MSB-to-LSB.
     */
    private static byte[] flipTable = TIFFFaxDecompressor.flipTable;

    public TIFFLSBDecompressor() {}

    public void decodeRaw(byte[] b,
                          int dstOffset,
                          int bitsPerPixel,
                          int scanlineStride) throws IOException {
        stream.seek(offset);

        int bytesPerRow = (srcWidth*bitsPerPixel + 7)/8;
        if(bytesPerRow == scanlineStride) {
            int numBytes = bytesPerRow*srcHeight;
            stream.readFully(b, dstOffset, numBytes);
            int xMax = dstOffset + numBytes;
            for (int x = dstOffset; x < xMax; x++) {
                b[x] = flipTable[b[x]&0xff];
            }
        } else {
            for (int y = 0; y < srcHeight; y++) {
                stream.readFully(b, dstOffset, bytesPerRow);
                int xMax = dstOffset + bytesPerRow;
                for (int x = dstOffset; x < xMax; x++) {
                    b[x] = flipTable[b[x]&0xff];
                }
                dstOffset += scanlineStride;
            }
        }
    }
}
