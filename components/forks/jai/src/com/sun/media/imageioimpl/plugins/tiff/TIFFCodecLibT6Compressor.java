/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2012 Open Microscopy Environment:
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

/*
 * $RCSfile: TIFFCodecLibT6Compressor.java,v $
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
 * $Date: 2006/01/30 23:22:34 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.tiff;

public class TIFFCodecLibT6Compressor extends TIFFT6Compressor {

    private static final boolean DEBUG = false; // XXX 'false' for release!!!

    Object encoder;

    public TIFFCodecLibT6Compressor() {
        super();

        try {
            com.sun.medialib.codec.g4fax.Encoder encoder =
                new com.sun.medialib.codec.g4fax.Encoder();
            this.encoder = encoder;
        } catch(Throwable t) {
            throw new RuntimeException("CodecLib not available");
        }
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
    public synchronized final int encodeT6(byte[] data,
                                           int lineStride,
                                           int colOffset,
                                           int width,
                                           int height,
                                           byte[] compData) {

        // Defer to superclass if bit offset is not byte-aligned.
        if(colOffset % 8 != 0) {
            return super.encodeT6(data, lineStride, colOffset,
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
        com.sun.medialib.codec.g4fax.Encoder clibEncoder =
            (com.sun.medialib.codec.g4fax.Encoder)encoder;
        //System.out.println("Using codecLib G4 encoder");

        // Set encoding flags.
        int encodingFlags = inverseFill ?
            com.sun.medialib.codec.g4fax.Constants.G4FAX_LSB2MSB : 0;

        int result =
            com.sun.medialib.codec.g4fax.Constants.G4FAX_FAILURE;
        try {
            if(DEBUG) {
                System.out.println("Using MediaLib G4 encoder");
            }
            result = clibEncoder.encode(compData, image, width, height,
                                        encodingFlags);
        } catch(Throwable t) {
            if(DEBUG) {
                System.out.println("MediaLib G4 encoder failed: "+t);
            }
            // XXX Should write a warning to listeners here.
            result = com.sun.medialib.codec.g4fax.Constants.G4FAX_FAILURE;
        }

        // If the codecLib encoder failed, try the superclass.
        if(result == com.sun.medialib.codec.g4fax.Constants.G4FAX_FAILURE) {
            if(DEBUG) {
                System.out.println("Falling back to Java G4 encoder");
            }
            result = super.encodeT6(data, lineStride, colOffset,
                                    width, height, compData);
        }

        return result;
    }
}
