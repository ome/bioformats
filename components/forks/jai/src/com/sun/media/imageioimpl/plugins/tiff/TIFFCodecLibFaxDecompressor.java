/*
 * $RCSfile: TIFFCodecLibFaxDecompressor.java,v $
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
 * $Date: 2005/02/11 05:01:44 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.tiff;

import java.io.IOException;
import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.TIFFDecompressor;

public class TIFFCodecLibFaxDecompressor extends TIFFFaxDecompressor {

    private static final boolean DEBUG = false; // XXX 'false' for release!!!

    /**
     * com.sun.medialib.codec.g3fax.Decoder for T.4 or
     * com.sun.medialib.codec.g4fax.Decoder for T.6.
     */
    private Object decoder = null;

    /**
     * Constructor which initializes the internal codecLib decoder.
     *
     * @throws RuntimeException if <code>bilevelCompression</code> is
     * not T.4 or T.6 compression or if codecLib is not available.
     */
    public TIFFCodecLibFaxDecompressor(int bilevelCompression) {
        super();

        try {
            // 'compression' is set in the superclass method.
            if(bilevelCompression ==
               BaselineTIFFTagSet.COMPRESSION_CCITT_T_4) {
                com.sun.medialib.codec.g3fax.Decoder decoder =
                    new com.sun.medialib.codec.g3fax.Decoder();
                this.decoder = decoder;
            } else if(bilevelCompression ==
                      BaselineTIFFTagSet.COMPRESSION_CCITT_T_6) {
                com.sun.medialib.codec.g4fax.Decoder decoder =
                    new com.sun.medialib.codec.g4fax.Decoder();
                this.decoder = decoder;
            } else {
                throw new RuntimeException("Unknown compression = "+
                                           bilevelCompression);    
            }
        } catch (Throwable e) {
            throw new RuntimeException("CodecLib not available");
        }
}

    public synchronized final void decodeRaw(byte[] b, int dstOffset,
                                             int pixelBitStride, // always 1
                                             int scanlineStride)
        throws IOException {

        int bytesPerRow = (srcWidth + 7)/8;
        byte[] image = null;
        byte[] code = new byte[byteCount];
        stream.seek(offset);
        stream.readFully(code, 0, byteCount);

        // Flip the bytes if fill order is LSB-to-MSB.
        if(fillOrder == 2) {
            for(int i = 0; i < byteCount; i++) {
                code[i] = flipTable[code[i]&0xff];
            }
        }

        if (dstOffset == 0 && bytesPerRow == scanlineStride) {
            image = b;
        } else {
            image = new byte[srcWidth*srcHeight];
        }

        if (compression == BaselineTIFFTagSet.COMPRESSION_CCITT_T_6) {
            com.sun.medialib.codec.g4fax.Decoder decoder =
                (com.sun.medialib.codec.g4fax.Decoder)this.decoder;

            if(DEBUG) {
                System.out.println("Using MediaLib G4 decoder");
            }

            int result = com.sun.medialib.codec.g4fax.Constants.G4FAX_FAILURE;
            try {
                result = decoder.decode(image, code, srcWidth, srcHeight, 0);
            } catch(Throwable t) {
                ((TIFFImageReader)reader).forwardWarningMessage
                    ("codecLib T.6 decompressor failed; falling back to Java.");
                result = com.sun.medialib.codec.g4fax.Constants.G4FAX_FAILURE;
            }

            if(result ==
               com.sun.medialib.codec.g4fax.Constants.G4FAX_FAILURE) {
                // Fall back to Java decoder.
                if(DEBUG) {
                    System.out.println("Falling back to Java G4 decoder");
                }
                super.decodeRaw(b, dstOffset, pixelBitStride, scanlineStride);
                return;
            }
        } else {
             com.sun.medialib.codec.g3fax.Decoder decoder =
                 (com.sun.medialib.codec.g3fax.Decoder)this.decoder;
             if(DEBUG) {
                 System.out.println("Using MediaLib G3 decoder");
             }

             int decodingFlags = 0;
             if(oneD == 1) {
                 decodingFlags = 
                     decoder.G3FAX_VERTICAL_CODING |
                     decoder.G3FAX_NORTC;
                 if(DEBUG) {
                     System.out.print("G3FAX_VERTICAL_CODING"+
                                      " | G3FAX_NORTC");
                 }
             } else {
                 decodingFlags = 
                     decoder.G3FAX_HORIZONTAL_CODING |
                     decoder.G3FAX_NORTC;
                 if(DEBUG) {
                     System.out.print("G3FAX_HORIZONTAL_CODING"+
                                      " | G3FAX_NORTC");
                 }
             }

             if(fillBits == 1) {
                 if(DEBUG) {
                     System.out.print(" | G3FAX_EOLPADDING_CODING");
                 }
                 decodingFlags |= decoder.G3FAX_EOLPADDING;
             }

             if(DEBUG) {
                 System.out.println("");
             }

             int result = com.sun.medialib.codec.g3fax.Constants.G3FAX_FAILURE;
             try {
                 result = decoder.decode(image, code, srcWidth, srcHeight,
                                        decodingFlags);
             } catch(Throwable t) {
                 ((TIFFImageReader)reader).forwardWarningMessage
                     ("codecLib T.4 decompressor failed; falling back to Java.");
                 result = com.sun.medialib.codec.g3fax.Constants.G3FAX_FAILURE;
             }

             if(result ==
                com.sun.medialib.codec.g3fax.Constants.G3FAX_FAILURE) {
                 // Fall back to Java decoder.
                 if(DEBUG) {
                     System.out.println("Falling back to Java G3 decoder");
                 }
                 super.decodeRaw(b, dstOffset, pixelBitStride, scanlineStride);
                 return;
             }
        }
        
        if (image != b) {
            int srcOffset = 0;
            for (int row = 0; row < srcHeight; row++) {
                System.arraycopy(image, srcOffset, b, dstOffset, bytesPerRow);
                srcOffset += bytesPerRow;
                dstOffset += scanlineStride;
            }
        }
    }
}
