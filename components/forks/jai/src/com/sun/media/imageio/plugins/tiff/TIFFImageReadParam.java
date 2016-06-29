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
 * $RCSfile: TIFFImageReadParam.java,v $
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

import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageReadParam;

/**
 * A subclass of {@link ImageReadParam} allowing control over
 * the TIFF reading process.
 *
 * <p> Because TIFF is an extensible format, the reader requires
 * information about any tags used by TIFF extensions in order to emit
 * meaningful metadata.  Also, TIFF extensions may define new
 * compression types.  Both types of information about extensions may
 * be provided by this interface.
 *
 * <p> Additional TIFF tags must be organized into
 * <code>TIFFTagSet</code>s.  A <code>TIFFTagSet</code> may be
 * provided to the reader by means of the
 * <code>addAllowedTagSet</code> method.  By default, the tag sets
 * <code>BaselineTIFFTagSet</code>, <code>FaxTIFFTagSet</code>,
 * <code>EXIFParentTIFFTagSet</code>, and <code>GeoTIFFTagSet</code>
 * are included.
 *
 * <p> New TIFF decompressors are handled in a simple fashion.  If a
 * non-<code>null</code> <code>TIFFDecompressor</code> is provided by
 * means of the setTIFFDecompressor method, it will override the
 * reader's usual choice of decompressor.  Thus, to read an image with
 * a non-standard compression type, the application should first
 * attempt to read the image's metadata and extract the compression
 * type.  The application may then use its own logic to choose a
 * suitable <code>TIFFDecompressor</code>, instantiate it, and pass it
 * to the <code>ImageReadParam</code> being used.  The reader's
 * <code>read</code> method may be called with the
 * <code>ImageReadParam</code> set.
 */
public class TIFFImageReadParam extends ImageReadParam {

    List allowedTagSets = new ArrayList(4);

    TIFFDecompressor decompressor = null;

    TIFFColorConverter colorConverter = null;

    /**
     * Constructs a <code>TIFFImageReadParam</code>.  Tags defined by
     * the <code>TIFFTagSet</code>s <code>BaselineTIFFTagSet</code>,
     * <code>FaxTIFFTagSet</code>, <code>EXIFParentTIFFTagSet</code>, and
     * <code>GeoTIFFTagSet</code> will be supported.
     *
     * @see BaselineTIFFTagSet
     * @see FaxTIFFTagSet
     * @see EXIFParentTIFFTagSet
     * @see GeoTIFFTagSet
     */
    public TIFFImageReadParam() {
        addAllowedTagSet(BaselineTIFFTagSet.getInstance());
        addAllowedTagSet(FaxTIFFTagSet.getInstance());
        addAllowedTagSet(EXIFParentTIFFTagSet.getInstance());
        addAllowedTagSet(GeoTIFFTagSet.getInstance());
    }

    /**
     * Adds a <code>TIFFTagSet</code> object to the list of allowed
     * tag sets.
     *
     * @param tagSet a <code>TIFFTagSet</code>.
     *
     * @throws IllegalArgumentException if <code>tagSet</code> is
     * <code>null</code>.
     */
    public void addAllowedTagSet(TIFFTagSet tagSet) {
        if (tagSet == null) {
            throw new IllegalArgumentException("tagSet == null!");
        }
        allowedTagSets.add(tagSet);
    }

    /**
     * Removes a <code>TIFFTagSet</code> object from the list of
     * allowed tag sets.  Removal is based on the <code>equals</code>
     * method of the <code>TIFFTagSet</code>, which is normally
     * defined as reference equality.
     *
     * @param tagSet a <code>TIFFTagSet</code>.
     *
     * @throws IllegalArgumentException if <code>tagSet</code> is
     * <code>null</code>.
     */
    public void removeAllowedTagSet(TIFFTagSet tagSet) {
        if (tagSet == null) {
            throw new IllegalArgumentException("tagSet == null!");
        }
        allowedTagSets.remove(tagSet);
    }

    /**
     * Returns a <code>List</code> containing the allowed
     * <code>TIFFTagSet</code> objects.
     *
     * @return a <code>List</code> of <code>TIFFTagSet</code>s.
     */
    public List getAllowedTagSets() {
        return allowedTagSets;
    }

    /**
     * Sets the <code>TIFFDecompressor</code> object to be used by the
     * <code>ImageReader</code> to decode each image strip or tile.
     * A value of <code>null</code> allows the reader to choose its
     * own TIFFDecompressor.
     *
     * @param decompressor the <code>TIFFDecompressor</code> to be
     * used for decoding, or <code>null</code> to allow the reader to
     * choose its own.
     *
     * @see #getTIFFDecompressor
     */
    public void setTIFFDecompressor(TIFFDecompressor decompressor) {
        this.decompressor = decompressor;
    }

    /**
     * Returns the <code>TIFFDecompressor</code> that is currently set
     * to be used by the <code>ImageReader</code> to decode each image
     * strip or tile, or <code>null</code> if none has been set.
     *
     * @return decompressor the <code>TIFFDecompressor</code> to be
     * used for decoding, or <code>null</code> if none has been set
     * (allowing the reader to choose its own).
     *
     * @see #setTIFFDecompressor(TIFFDecompressor)
     */
    public TIFFDecompressor getTIFFDecompressor() {
        return this.decompressor;
    }

    /**
     * Sets the <code>TIFFColorConverter</code> object for the pixel data
     * being read.  The data will be converted from the given color
     * space to a standard RGB space as it is being read.  A value of
     * <code>null</code> disables conversion.
     *
     * @param colorConverter a <code>TIFFColorConverter</code> object
     * to be used for final color conversion, or <code>null</code>.
     *
     * @see #getColorConverter
     */
    public void setColorConverter(TIFFColorConverter colorConverter) {
        this.colorConverter = colorConverter;
    }

    /**
     * Returns the currently set <code>TIFFColorConverter</code> object,
     * or <code>null</code> if none is set.
     *
     * @return the current <code>TIFFColorConverter</code> object.
     *
     * @see #setColorConverter(TIFFColorConverter)
     */
    public TIFFColorConverter getColorConverter() {
        return this.colorConverter;
    }
}
