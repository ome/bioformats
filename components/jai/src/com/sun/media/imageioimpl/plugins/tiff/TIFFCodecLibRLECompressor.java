/*
 * $RCSfile: TIFFCodecLibRLECompressor.java,v $
 *
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All  Rights Reserved.
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
 * $Date: 2006/04/22 00:04:23 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.tiff;

import java.io.IOException;
import javax.imageio.IIOException;

public class TIFFCodecLibRLECompressor extends TIFFRLECompressor {

    private static final boolean DEBUG = false; // XXX 'false' for release!!!

    Object encoder;

    public TIFFCodecLibRLECompressor() {
        super();

        try {
            com.sun.medialib.codec.g3fax.Encoder encoder =
                new com.sun.medialib.codec.g3fax.Encoder();
            this.encoder = encoder;
        } catch(Throwable t) {
            throw new RuntimeException("CodecLib not available");
        }
    }
    
    public int encode(byte[] b, int off,
                      int width, int height,
                      int[] bitsPerSample,
                      int scanlineStride) throws IOException {
        if (bitsPerSample.length != 1 || bitsPerSample[0] != 1) {
            throw new IIOException
                ("Bits per sample must be 1 for RLE compression!"); 
        }

        // Set image to data if possible; otherwise copy.
        int bytesPerRow = (width + 7)/8;
        byte[] image = null;

        if(off == 0 && bytesPerRow == scanlineStride) {
            image = b;
        } else {
            image = new byte[bytesPerRow*height];
            int dataOffset = off;
            int imageOffset = 0;
            for(int row = 0; row < height; row++) {
                System.arraycopy(b, dataOffset, image, imageOffset,
                                 bytesPerRow);
                dataOffset += scanlineStride;
                imageOffset += bytesPerRow;
            }
        }

        // In the worst case, 2 bits of input will result in 9 bits of output,
        // plus 2 extra bits if the row starts with black.
        int maxBits = 9*((width + 1)/2) + 2;
        byte[] compData = new byte[((maxBits + 7)/8)*height];

        // Attempt the codecLib encoder.
        com.sun.medialib.codec.g3fax.Encoder clibEncoder =
            (com.sun.medialib.codec.g3fax.Encoder)encoder;

        // Set RLE encoding flag.
        int encodingFlags =
            com.sun.medialib.codec.g3fax.Constants.G3FAX_RLE_CODING;
        if(inverseFill) {
            encodingFlags |=
                com.sun.medialib.codec.g3fax.Constants.G3FAX_LSB2MSB;
            if(DEBUG) {
                System.out.println("Setting LSB flag");
            }
        }

        // Set result flag.
        int result =
            com.sun.medialib.codec.g3fax.Constants.G3FAX_FAILURE;
        try {
            if(DEBUG) {
                System.out.println("Using MediaLib RLE encoder");
            }
            result = clibEncoder.encode(compData, image, width, height,
                                        2, // k parameter
                                        encodingFlags);
            stream.write(compData, 0, result);
        } catch(Throwable t) {
            if(DEBUG) {
                System.out.println("MediaLib RLE encoder failed: "+t);
            }
            // XXX Should write a warning to listeners here.
            result = com.sun.medialib.codec.g3fax.Constants.G3FAX_FAILURE;
        }

        // If the codecLib encoder failed, try the superclass.
        if(result == com.sun.medialib.codec.g3fax.Constants.G3FAX_FAILURE) {
            if(DEBUG) {
                System.out.println("Falling back to Java RLE encoder");
            }
            result = super.encode(b, off, width, height,
                                  bitsPerSample, scanlineStride);
        }

        return result;
    }
}
