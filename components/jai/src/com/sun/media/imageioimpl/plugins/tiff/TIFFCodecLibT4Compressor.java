/*
 * $RCSfile: TIFFCodecLibT4Compressor.java,v $
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
 * $Date: 2006/01/30 23:22:03 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.tiff;

public class TIFFCodecLibT4Compressor extends TIFFT4Compressor {

    private static final boolean DEBUG = false; // XXX 'false' for release!!!

    Object encoder;

    public TIFFCodecLibT4Compressor() {
        super();

        try {
            com.sun.medialib.codec.g3fax.Encoder encoder =
                new com.sun.medialib.codec.g3fax.Encoder();
            this.encoder = encoder;
        } catch(Throwable t) {
            throw new RuntimeException("CodecLib not available");
        }
    }
    
    /**
     * Encode a buffer of data using CCITT T.4 Compression also known as
     * Group 3 facsimile compression.
     *
     * @param is1DMode     Whether to perform one-dimensional encoding.
     * @param isEOLAligned Whether EOL bit sequences should be padded.
     * @param data         The row of data to compress.
     * @param lineStride   Byte step between the same sample in different rows.
     * @param colOffset    Bit offset within first <code>data[rowOffset]</code>.
     * @param width        Number of bits in the row.
     * @param height       Number of rows in the buffer.
     * @param compData     The compressed data.
     *
     * @return The number of bytes saved in the compressed data array.
     */
    public final int encodeT4(boolean is1DMode,
                              boolean isEOLAligned,
                              byte[] data,
                              int lineStride,
                              int colOffset,
                              int width,
                              int height,
                              byte[] compData) {

        // Defer to superclass if bit offset is not byte-aligned.
        if(colOffset % 8 != 0) {
            return super.encodeT4(is1DMode, isEOLAligned,
                                  data, lineStride, colOffset,
                                  width, height, compData);
        }

        // Set image to data if possible; otherwise copy.
        int bytesPerRow = (width + 7)/8;
        byte[] image = null;

        if(colOffset == 0 && bytesPerRow == lineStride) {
            image = data;
        } else {
            image = new byte[bytesPerRow*height];
            int dataOffset = colOffset / 8;
            int imageOffset = 0;
            for(int row = 0; row < height; row++) {
                System.arraycopy(data, dataOffset, image, imageOffset,
                                 bytesPerRow);
                dataOffset += lineStride;
                imageOffset += bytesPerRow;
            }
        }

        // Attempt the codecLib encoder.
        com.sun.medialib.codec.g3fax.Encoder clibEncoder =
            (com.sun.medialib.codec.g3fax.Encoder)encoder;
        //System.out.println("Using codecLib G3 encoder");

        // Set encoding flags.
        int encodingFlags =
            is1DMode ?
            com.sun.medialib.codec.g3fax.Constants.G3FAX_HORIZONTAL_CODING :
            com.sun.medialib.codec.g3fax.Constants.G3FAX_VERTICAL_CODING;
        if(isEOLAligned) {
            encodingFlags |=
                com.sun.medialib.codec.g3fax.Constants.G3FAX_EOLPADDING;
        }
        if(inverseFill) {
            encodingFlags |=
                com.sun.medialib.codec.g3fax.Constants.G3FAX_LSB2MSB;
        }

        int result =
            com.sun.medialib.codec.g3fax.Constants.G3FAX_FAILURE;
        try {
            if(DEBUG) {
                System.out.println("Using MediaLib G3 encoder");
            }
            result = clibEncoder.encode(compData, image, width, height,
                                        2, // k parameter
                                        encodingFlags);
        } catch(Throwable t) {
            if(DEBUG) {
                System.out.println("MediaLib G3 encoder failed: "+t);
            }
            // XXX Should write a warning to listeners here.
            result = com.sun.medialib.codec.g3fax.Constants.G3FAX_FAILURE;
        }

        // If the codecLib encoder failed, try the superclass.
        if(result == com.sun.medialib.codec.g3fax.Constants.G3FAX_FAILURE) {
            if(DEBUG) {
                System.out.println("Falling back to Java G3 encoder");
            }
            result = super.encodeT4(is1DMode, isEOLAligned,
                                    data, lineStride, colOffset,
                                    width, height, compData);
        }

        return result;
    }
}
