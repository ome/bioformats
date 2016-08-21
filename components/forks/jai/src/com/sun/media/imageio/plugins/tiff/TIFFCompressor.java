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
 * $RCSfile: TIFFCompressor.java,v $
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
 * $Date: 2005/02/11 05:01:18 $
 * $State: Exp $
 */
package com.sun.media.imageio.plugins.tiff;

import java.io.IOException;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;
import com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriter;

/**
 * An abstract superclass for pluggable TIFF compressors.
 */
public abstract class TIFFCompressor {

    /**
     * The <code>ImageWriter</code> calling this
     * <code>TIFFCompressor</code>.
     */
    protected ImageWriter writer;

    /**
     * The <code>IIOMetadata</code> object containing metadata for the
     * current image.
     */
    protected IIOMetadata metadata;

    /**
     * The name of the compression type supported by this compressor.
     */
    protected String compressionType;

    /**
     * The value to be assigned to the TIFF <i>Compression</i> tag in the
     * TIFF image metadata.
     */
    protected int compressionTagValue;

    /**
     * Whether the compression is lossless.
     */
    protected boolean isCompressionLossless;

    /**
     * The <code>ImageOutputStream</code> to be written.
     */
    protected ImageOutputStream stream;

    /**
     * Creates a compressor object for use in compressing TIFF data. This
     * object may be passed to the
     * {@link TIFFImageWriteParam#setTIFFCompressor(TIFFCompressor)}
     * method to override the compressor of a supported compression type or
     * to provide the implementation of the compression algorithm of an
     * unsupported compression type.
     *
     * <p>The parameters <code>compressionTagValue</code> and
     * <code>isCompressionLossless</code> are provided to accomodate
     * compression types which are unknown. A compression type is
     * "known" if it is either among those already supported by the
     * TIFF writer (see {@link TIFFImageWriteParam}), or is listed in
     * the TIFF 6.0 specification but not supported. If the compression
     * type is unknown, the <code>compressionTagValue</code> and
     * <code>isCompressionLossless</code> parameters are ignored.</p>
     *
     * @param compressionType The name of the compression type.
     * @param compressionTagValue The value to be assigned to the TIFF
     * <i>Compression</i> tag in the TIFF image metadata; ignored if
     * <code>compressionType</code> is a known type.
     * @param isCompressionLossless Whether the compression is lossless;
     * ignored if <code>compressionType</code> is a known type.
     *
     * @throws IllegalArgumentException if <code>compressionType</code> is
     * <code>null</code> or <code>compressionTagValue</code> is less than
     * <code>1</code>.
     */
    public TIFFCompressor(String compressionType,
                          int compressionTagValue,
                          boolean isCompressionLossless) {
        if(compressionType == null) {
            throw new IllegalArgumentException("compressionType == null");
        } else if(compressionTagValue < 1) {
            throw new IllegalArgumentException("compressionTagValue < 1");
        }

        // Set the compression type.
        this.compressionType = compressionType;

        // Determine whether this type is either defined in the TIFF 6.0
        // specification or is already supported.
        int compressionIndex = -1;
        String[] compressionTypes = TIFFImageWriter.compressionTypes;
        int len = compressionTypes.length;
        for(int i = 0; i < len; i++) {
            if(compressionTypes[i].equals(compressionType)) {
                // Save the index of the supported type.
                compressionIndex = i;
                break;
            }
        }

        if(compressionIndex != -1) {
            // Known compression type.
            this.compressionTagValue =
                TIFFImageWriter.compressionNumbers[compressionIndex];
            this.isCompressionLossless =
                TIFFImageWriter.isCompressionLossless[compressionIndex];
        } else {
            // Unknown compression type.
            this.compressionTagValue = compressionTagValue;
            this.isCompressionLossless = isCompressionLossless;
        }
    }

    /**
     * Retrieve the name of the compression type supported by this compressor.
     *
     * @return The compression type name.
     */
    public String getCompressionType() {
        return compressionType;
    }

    /**
     * Retrieve the value to be assigned to the TIFF <i>Compression</i> tag
     * in the TIFF image metadata.
     *
     * @return The <i>Compression</i> tag value.
     */
    public int getCompressionTagValue() {
        return compressionTagValue;
    }

    /**
     * Retrieves a value indicating whether the compression is lossless.
     *
     * @return Whether the compression is lossless.
     */
    public boolean isCompressionLossless() {
        return isCompressionLossless;
    }

    /**
     * Sets the <code>ImageOutputStream</code> to be written.
     *
     * @param stream an <code>ImageOutputStream</code> to be written.
     *
     * @see #getStream
     */
    public void setStream(ImageOutputStream stream) {
        this.stream = stream;
    }

    /**
     * Returns the <code>ImageOutputStream</code> that will be written.
     * 
     * @return an <code>ImageOutputStream</code>.
     *
     * @see #setStream(ImageOutputStream)
     */
    public ImageOutputStream getStream() {
        return stream;
    }

    /**
     * Sets the value of the <code>writer</code> field.
     *
     * @param writer the current <code>ImageWriter</code>.
     *
     * @see #getWriter()
     */
    public void setWriter(ImageWriter writer) {
        this.writer = writer;
    }

    /**
     * Returns the current <code>ImageWriter</code>.
     *
     * @return an <code>ImageWriter</code>.
     *
     * @see #setWriter(ImageWriter)
     */
    public ImageWriter getWriter() {
        return this.writer;
    }

    /**
     * Sets the value of the <code>metadata</code> field.
     *
     * @param metadata the <code>IIOMetadata</code> object for the
     * image being written.
     *
     * @see #getMetadata()
     */
    public void setMetadata(IIOMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * Returns the current <code>IIOMetadata</code> object.
     *
     * @return the <code>IIOMetadata</code> object for the image being
     * written.
     *
     * @see #setMetadata(IIOMetadata)
     */
    public IIOMetadata getMetadata() {
        return this.metadata;
    }

    /**
     * Encodes the supplied image data, writing to the currently set
     * <code>ImageOutputStream</code>.
     *
     * @param b an array of <code>byte</code>s containing the packed
     * but uncompressed image data.
     * @param off the starting offset of the data to be written in the
     * array <code>b</code>.
     * @param width the width of the rectangle of pixels to be written.
     * @param height the height of the rectangle of pixels to be written.
     * @param bitsPerSample an array of <code>int</code>s indicting
     * the number of bits used to represent each image sample within
     * a pixel.
     * @param scanlineStride the number of bytes separating each
     * row of the input data.
     *
     * @return the number of bytes written.
     *
     * @throws IOException if the supplied data cannot be encoded by
     * this <code>TIFFCompressor</code>, or if any I/O error occurs
     * during writing.
     */
    public abstract int encode(byte[] b, int off,
                               int width, int height,
                               int[] bitsPerSample,
                               int scanlineStride) throws IOException;

}
