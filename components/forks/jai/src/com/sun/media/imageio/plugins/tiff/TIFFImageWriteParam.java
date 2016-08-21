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
 * $RCSfile: TIFFImageWriteParam.java,v $
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
 * $Date: 2006/04/28 01:01:59 $
 * $State: Exp $
 */
package com.sun.media.imageio.plugins.tiff;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.imageio.ImageWriteParam;
import com.sun.media.imageioimpl.plugins.tiff.TIFFImageWriter;

/**
 * A subclass of {@link ImageWriteParam <code>ImageWriteParam</code>}
 * allowing control over the TIFF writing process. The set of innately
 * supported compression types is listed in the following table:
 *
 * <p>
 * <table border=1>
 * <caption><b>Supported Compression Types</b></caption>
 * <tr><th>Compression Type</th> <th>Description</th> <th>Reference</th></tr>
 * <tr>
 * <td>CCITT RLE</td>
 * <td>Modified Huffman compression</td>
 * <td>TIFF 6.0 Specification, Section 10</td>
 * </tr>
 * <tr>
 * <td>CCITT T.4</td>
 * <td>CCITT T.4 bilevel encoding/Group 3 facsimile compression</td>
 * <td>TIFF 6.0 Specification, Section 11</td>
 * </tr>
 * <tr>
 * <td>CCITT T.6</td>
 * <td>CCITT T.6 bilevel encoding/Group 4 facsimile compression</td>
 * <td>TIFF 6.0 Specification, Section 11</td></tr>
 * <tr>
 * <td>LZW</td>
 * <td>LZW compression</td>
 * <td>TIFF 6.0 Specification, Section 13</td></tr>
 * <tr>
 * <td>JPEG</td>
 * <td>"New" JPEG-in-TIFF compression</td>
 * <td><a href="ftp://ftp.sgi.com/graphics/tiff/TTN2.draft.txt">TIFF
 * Technical Note #2</a></td>
 * </tr>
 * <tr>
 * <td>ZLib</td>
 * <td>"Deflate/Inflate" compression (see note following this table)</td>
 * <td><a href="http://partners.adobe.com/asn/developer/pdfs/tn/TIFFphotoshop.pdf">
 * Adobe Photoshop&#174; TIFF Technical Notes</a> (PDF)</td>
 * </tr>
 * <tr>
 * <td>PackBits</td>
 * <td>Byte-oriented, run length compression</td>
 * <td>TIFF 6.0 Specification, Section 9</td>
 * </tr>
 * <tr>
 * <td>Deflate</td>
 * <td>"Zip-in-TIFF" compression (see note following this table)</td>
 * <td><a href="http://www.isi.edu/in-notes/rfc1950.txt">
 * ZLIB Compressed Data Format Specification</a>,
 * <a href="http://www.isi.edu/in-notes/rfc1951.txt">
 * DEFLATE Compressed Data Format Specification</a></td>
 * </tr>
 * <tr>
 * <td>EXIF JPEG</td>
 * <td>EXIF-specific JPEG compression (see note following this table)</td>
 * <td><a href="http://www.exif.org/Exif2-2.PDF">EXIF 2.2 Specification</a>
 * (PDF), section 4.5.5, "Basic Structure of Thumbnail Data"</td>
 * </table>
 * </p>
 * <p>
 * Old-style JPEG compression as described in section 22 of the TIFF 6.0
 * Specification is <i>not</i> supported.
 * </p>
 *
 * <p> The CCITT compression types are applicable to bilevel (1-bit)
 * images only.  The JPEG compression type is applicable to byte
 * grayscale (1-band) and RGB (3-band) images only.</p>
 *
 * <p>
 * ZLib and Deflate compression are identical except for the value of the
 * TIFF Compression field: for ZLib the Compression field has value 8
 * whereas for Deflate it has value 32946 (0x80b2). In both cases each
 * image segment (strip or tile) is written as a single complete zlib data
 * stream.
 * </p>
 *
 * <p>
 * "EXIF JPEG" is a compression type used when writing the contents of an
 * APP1 EXIF marker segment for inclusion in a JPEG native image metadata
 * tree. The contents appended to the output when this compression type is
 * used are a function of whether an empty or non-empty image is written.
 * If the image is empty, then a TIFF IFD adhering to the specification of
 * a compressed EXIF primary IFD is appended. If the image is non-empty,
 * then a complete IFD and image adhering to the specification of a
 * compressed EXIF thumbnail IFD and image are appended. Note that the
 * data of the empty image may <i>not</i> later be appended using the pixel
 * replacement capability of the TIFF writer.
 * </p>
 *
 * <p> If ZLib/Deflate or JPEG compression is used, the compression quality
 * may be set. For ZLib/Deflate the supplied floating point quality value is
 * rescaled to the range <tt>[1,&nbsp;9]</tt> and truncated to an integer
 * to derive the Deflate compression level. For JPEG the floating point
 * quality value is passed directly to the JPEG writer plug-in which
 * interprets it in the usual way.</p>
 *
 * <p> The <code>canWriteTiles</code> and
 * <code>canWriteCompressed</code> methods will return
 * <code>true</code>; the <code>canOffsetTiles</code> and
 * <code>canWriteProgressive</code> methods will return
 * <code>false</code>.</p>
 *
 * <p> If tiles are being written, then each of their dimensions will be
 * rounded to the nearest multiple of 16 per the TIFF specification. If
 * JPEG-in-TIFF compression is being used, and tiles are being written
 * each tile dimension will be rounded to the nearest multiple of 8 times
 * the JPEG minimum coded unit (MCU) in that dimension. If JPEG-in-TIFF
 * compression is being used and strips are being written, the number of
 * rows per strip is rounded to a multiple of 8 times the maximum MCU over
 * both dimensions.</p>
 */
public class TIFFImageWriteParam extends ImageWriteParam {

    TIFFCompressor compressor = null;

    TIFFColorConverter colorConverter = null;
    int photometricInterpretation;

    private boolean appendedCompressionType = false;

    /**
     * Constructs a <code>TIFFImageWriteParam</code> instance
     * for a given <code>Locale</code>.
     *
     * @param locale the <code>Locale</code> for which messages
     * should be localized.
     */
    public TIFFImageWriteParam(Locale locale) {
        super(locale);
        this.canWriteCompressed = true;
        this.canWriteTiles = true;
        this.compressionTypes = TIFFImageWriter.TIFFCompressionTypes;
    };

    public boolean isCompressionLossless() {
        if (getCompressionMode() != MODE_EXPLICIT) {
            throw new IllegalStateException
                ("Compression mode not MODE_EXPLICIT!");
        }

        if (compressionType == null) {
            throw new IllegalStateException("No compression type set!");
        }

        if(compressor != null &&
           compressionType.equals(compressor.getCompressionType())) {
            return compressor.isCompressionLossless();
        }

        for (int i = 0; i < compressionTypes.length; i++) {
            if (compressionType.equals(compressionTypes[i])) {
                return TIFFImageWriter.isCompressionLossless[i];
            }
        }

        return false;
    }

    /**
     * Sets the <code>TIFFCompressor</code> object to be used by the
     * <code>ImageWriter</code> to encode each image strip or tile.
     * A value of <code>null</code> allows the writer to choose its
     * own TIFFCompressor.
     *
     * <p>Note that invoking this method is not sufficient to set
     * the compression type:
     * {@link ImageWriteParam#setCompressionType(String) <code>setCompressionType()</code>}
     * must be invoked explicitly for this purpose. The following
     * code illustrates the correct procedure:
     * <pre>
     * TIFFImageWriteParam writeParam;
     * TIFFCompressor compressor;
     * writeParam.setCompressionMode(writeParam.MODE_EXPLICIT);
     * writeParam.setTIFFCompressor(compressor);
     * writeParam.setCompressionType(compressor.getCompressionType());
     * </pre>
     * If <code>compressionType</code> is set to a value different from
     * that supported by the <code>TIFFCompressor</code> then the
     * compressor object will not be used.
     * </p>
     *
     * <p>If the compression type supported by the supplied
     * <code>TIFFCompressor</code> is not among those in
     * {@link ImageWriteParam#compressionTypes <code>compressionTypes</code>},
     * then it will be appended to this array after removing any previously
     * appended compression type. If <code>compressor</code> is
     * <code>null</code> this will also cause any previously appended
     * type to be removed from the array.</p>
     *
     * @param compressor the <code>TIFFCompressor</code> to be
     * used for encoding, or <code>null</code> to allow the writer to
     * choose its own.
     *
     * @throws IllegalStateException if the compression mode is not
     * <code>MODE_EXPLICIT</code>.
     *
     * @see #getTIFFCompressor
     */
    public void setTIFFCompressor(TIFFCompressor compressor) {
        if (getCompressionMode() != MODE_EXPLICIT) {
            throw new IllegalStateException
                ("Compression mode not MODE_EXPLICIT!");
        }

        this.compressor = compressor;

        if(appendedCompressionType) {
            // Remove previously appended compression type.
            int len = compressionTypes.length - 1;
            String[] types = new String[len];
            System.arraycopy(compressionTypes, 0, types, 0, len);
            compressionTypes = types;
            appendedCompressionType = false;
        }

        if(compressor != null) {
            // Check whether compressor's type is already in the list.
            String compressorType = compressor.getCompressionType();
            int len = compressionTypes.length;
            boolean appendCompressionType = true;
            for(int i = 0; i < len; i++) {
                if(compressorType.equals(compressionTypes[i])) {
                    appendCompressionType = false;
                    break;
                }
            }

            if(appendCompressionType) {
                // Compressor's compression type not in the list; append it.
                String[] types = new String[len + 1];
                System.arraycopy(compressionTypes, 0, types, 0, len);
                types[len] = compressorType;
                compressionTypes = types;
                appendedCompressionType = true;
            }
        }
    }

    /**
     * Returns the <code>TIFFCompressor</code> that is currently set
     * to be used by the <code>ImageWriter</code> to encode each image
     * strip or tile, or <code>null</code> if none has been set.
     *
     * @return compressor the <code>TIFFCompressor</code> to be
     * used for encoding, or <code>null</code> if none has been set
     * (allowing the writer to choose its own).
     *
     * @throws IllegalStateException if the compression mode is not
     * <code>MODE_EXPLICIT</code>.
     *
     * @see #setTIFFCompressor(TIFFCompressor)
     */
    public TIFFCompressor getTIFFCompressor() {
        if (getCompressionMode() != MODE_EXPLICIT) {
            throw new IllegalStateException
                ("Compression mode not MODE_EXPLICIT!");
        }
        return this.compressor;
    }

    /**
     * Sets the <code>TIFFColorConverter</code> object describing the
     * color space to which the input data should be converted for
     * storage in the input stream.  In addition, the value to be
     * written to the <code>PhotometricInterpretation</code> tag is
     * supplied.
     *
     * @param colorConverter a <code>TIFFColorConverter</code> object,
     * or <code>null</code>.
     * @param photometricInterpretation the value to be written to the
     * <code>PhotometricInterpretation</code> tag in the root IFD.
     *
     * @see #getColorConverter
     * @see #getPhotometricInterpretation
     */
    public void setColorConverter(TIFFColorConverter colorConverter,
                                  int photometricInterpretation) {
        this.colorConverter = colorConverter;
        this.photometricInterpretation = photometricInterpretation;
    }

    /**
     * Returns the current <code>TIFFColorConverter</code> object that
     * will be used to perform color conversion when writing the
     * image, or <code>null</code> if none is set.
     *
     * @return a <code>TIFFColorConverter</code> object, or
     * <code>null</code>.
     *
     * @see #setColorConverter(TIFFColorConverter, int)
     */
    public TIFFColorConverter getColorConverter() {
        return colorConverter;
    }

    /**
     * Returns the current value that will be written to the
     * <code>Photometricinterpretation</code> tag.  This method should
     * only be called if a value has been set using the
     * <code>setColorConverter</code> method.
     *
     * @return an <code>int</code> to be used as the value of the
     * <code>PhotometricInterpretation</code> tag.
     *
     * @see #setColorConverter(TIFFColorConverter, int)
     *
     * @throws IllegalStateException if no value is set.
     */
    public int getPhotometricInterpretation() {
        if (colorConverter == null) {
            throw new IllegalStateException("Color converter not set!");
        }
        return photometricInterpretation;
    }

    /**
     * Removes any currently set <code>ColorConverter</code> object and
     * <code>PhotometricInterpretation</code> tag value.
     *
     * @see #setColorConverter(TIFFColorConverter, int)
     */
    public void unsetColorConverter() {
        this.colorConverter = null;
    }
}
