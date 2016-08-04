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
 * $RCSfile: J2KImageWriteParam.java,v $
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
 * $Date: 2006/09/20 23:23:30 $
 * $State: Exp $
 */
package com.sun.media.imageio.plugins.jpeg2000;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.util.Collections;
import java.util.Locale;
import java.util.Iterator;
import javax.imageio.ImageWriteParam;

/**
 * A subclass of <code>ImageWriteParam</code> for writing images in
 * the JPEG 2000 format.
 *
 * <p>JPEG 2000 plugin supports to losslessly or lossy compress gray-scale,
 * RGB, and RGBA images with byte, unsigned short or short data type.  It also
 * supports losslessly compress bilevel, and 8-bit color indexed images.  The
 * result data is in the of JP2 format -- JPEG 2000 Part 1 or baseline format.
 *
 * <p>The parameters for encoding JPEG 2000 are listed in the following table:
 *
 * <p><table border=1>
 * <caption><b>JPEG 2000 Plugin Decoding Parameters</b></caption>
 * <tr><th>Parameter Name</th> <th>Description</th></tr>
 * <tr>
 *    <td>numDecompositionLevels</td>
 *    <td> The number of decomposition levels to generate. This value must
 *         be in the range
 *         <code>0&nbsp;&le;&nbsp;numDecompositionLevels&nbsp;&le;&nbsp;32
 *         </code>. The default value is <code>5</code>. Note that the number
 *         of resolution levels is
 *         <code>numDecompositionLevels&nbsp;+&nbsp;1</code>.
 *         The number of decomposition levels is constant across
 *         all components and all tiles.
 *    </td>
 * </tr>
 * <tr>
 *    <td>encodingRate</td>
 *    <td> The bitrate in bits-per-pixel for encoding.  Should be set when
 *    lossy compression scheme is used.  With the default value
 *    <code>Double.MAX_VALUE</code>, a lossless compression will be done.
 *    </td>
 * </tr>
 * <tr>
 *    <td>lossless</td>
 *    <td> Indicates using the lossless scheme or not.  It is equivalent to
 *    use reversible quantization and 5x3 integer wavelet filters.  The
 *    default is <code>true</code>.
 *    </td>
 * </tr>
 * <tr>
 *    <td>componentTransformation</td>
 *    <td> Specifies to utilize the component transformation on some tiles.
 *    If the wavelet transform is reversible (w5x3 filter), the Reversible
 *    Component Transformation (RCT) is applied. If not reversible
 *    (w9x7 filter), the Irreversible Component Transformation (ICT) is used.
 *    </td>
 * </tr>
 * <tr>
 *    <td>filters</td>
 *    <td> Specifies which wavelet filters to use for the specified
 *    tile-components.  JPEG 2000 part I only supports w5x3 and w9x7 filters.
 *    </td>
 * </tr>
 * <tr>
 *    <td>codeBlockSize</td>
 *    <td> Specifies the maximum code-block size to use for tile-component.
 *    The maximum width and height is 1024, however the block size
 *    (i.e. width x height) must not exceed 4096.  The minimum width and
 *    height is 4.  The default values are (64, 64).
 *    </td>
 * </tr>
 * <tr>
 *    <td>progressionType</td>
 *    <td> Specifies which type of progression should be used when generating
 *    the codestream.
 *    <p> The format is ont of the progression types defined below:
 *
 *    <p> res : Resolution-Layer-Component-Position
 *    <p> layer: Layer-Resolution-Component-Position
 *    <p> res-pos: Resolution-Position-Component-Layer
 *    <p> pos-comp: Position-Component-Resolution-Layer
 *    <p> comp-pos: Component-Position-Resolution-Layer
 *    </td>
 * </tr>
 * <tr>
 *    <td>SOP</td>
 *    <td>Specifies whether start of packet (SOP) markers should be used.
 *    true enables, false disables it.  The default value is false.
 *    </td>
 * </tr>
 * <tr>
 *    <td>EPH</td>
 *    <td>Specifies whether end of packet header (EPH) markers should be used.
 *    true enables, false disables it.  The default value is false.
 *    </td>
 * </tr>
 * <tr>
 *    <td>writeCodeStreamOnly</td>
 *    <td>Specifies whether write only the jpeg2000 code stream, i.e, no any
 *    box is written.  The default value is false.
 *    </td>
 * </tr> 
 * </table>
 */
public class J2KImageWriteParam extends ImageWriteParam {
    /** The filter for lossy compression. */
    public static final String FILTER_97 = "w9x7";

    /** The filter for lossless compression. */
    public static final String FILTER_53 = "w5x3";

    /**
     * The number of decomposition levels.
     */
    private int numDecompositionLevels = 5;

    /**
     * The bitrate in bits-per-pixel for encoding.  Should be set when lossy
     * compression scheme is used.  The default is
     * <code>Double.MAX_VALUE</code>.
     */
    private double encodingRate = Double.MAX_VALUE;

    /**
     * Indicates using the lossless scheme or not.  It is equivalent to
     * use reversible quantization and 5x3 integer wavelet filters.
     */
    private boolean lossless = true;

    /** Specifies to utilize the component transformation with some tiles.
     *  If the wavelet transform is reversible (w5x3 filter), the
     *  Reversible Component Transformation (RCT) is applied. If not reversible
     *  (w9x7 filter), the Irreversible Component Transformation (ICT)
     *  is used.
     */
    private boolean componentTransformation = true;

    /** Specifies which filters to use for the specified tile-components.
     *  JPEG 2000 part I only supports w5x3 and w9x7 filters.
     */
    private String filter = FILTER_53;

    /** Specifies the maximum code-block size to use for tile-component.
     *  The maximum width and height is 1024, however the image area
     *  (i.e. width x height) must not exceed 4096. The minimum
     *  width and height is 4.  Default: 64 64.
     */
    private int[] codeBlockSize = new int[]{64, 64};

    /** See above.
     */
    private String progressionType = "layer";

    /** Specifies whether end of packet header (EPH) markers should be used.
     *  true enables, false disables it.  Default: false.
     */
     private boolean EPH = false;

    /** Specifies whether start of packet (SOP) markers should be used.
     *  true enables, false disables it. Default: false.
     */
    private boolean SOP = false;

    /** Specifies whether write only the jpeg2000 code stream, i.e, no any
     *  box is written.  The default value is false.
     */
    private boolean writeCodeStreamOnly = false;

    /**
     * Constructor which sets the <code>Locale</code>.
     *
     * @param locale a <code>Locale</code> to be used to localize
     * compression type names and quality descriptions, or
     * <code>null</code>.
     */
    public J2KImageWriteParam(Locale locale) {
        super(locale);
        setDefaults();
    }

    /**
     * Constructs a <code>J2KImageWriteParam</code> object with default
     * values for all parameters.
     */
    public J2KImageWriteParam() {
        super();
        setDefaults();
    }

    /** Set source */
    private void setDefaults() {
        // override the params in the super class
        canOffsetTiles = true;
        canWriteTiles = true;
        canOffsetTiles = true;
        compressionTypes = new String[] {"JPEG2000"};
        canWriteCompressed = true;
        tilingMode = MODE_EXPLICIT;
    }

    /**
     * Sets <code>numDecompositionLevels</code>.
     *
     * @param numDecompositionLevels the number of decomposition levels.
     * @throws IllegalArgumentException if <code>numDecompositionLevels</code>
     * is negative or greater than 32.
     * @see #getNumDecompositionLevels
     */
    public void setNumDecompositionLevels(int numDecompositionLevels) {
        if(numDecompositionLevels < 0 || numDecompositionLevels > 32) {
            throw new IllegalArgumentException
                ("numDecompositionLevels < 0 || numDecompositionLevels > 32");
        }
        this.numDecompositionLevels = numDecompositionLevels;
    }

    /**
     * Gets <code>numDecompositionLevels</code>.
     *
     * @return the number of decomposition levels.
     * @see #setNumDecompositionLevels
     */
    public int getNumDecompositionLevels() {
        return numDecompositionLevels;
    }

    /**
     * Sets <code>encodingRate</code>.
     *
     * @param rate the encoding rate in bits-per-pixel.
     * @see #getEncodingRate()
     */
    public void setEncodingRate(double rate) {
        this.encodingRate = rate;
        if (encodingRate != Double.MAX_VALUE) {
            lossless = false;
	    filter = FILTER_97;
        } else {
            lossless = true;
	    filter = FILTER_53;
	}
    }

    /**
     * Gets <code>encodingRate</code>.
     *
     * @return the encoding rate in bits-per-pixel.
     * @see #setEncodingRate(double)
     */
    public double getEncodingRate() {
        return encodingRate;
    }

    /**
     * Sets <code>lossless</code>.
     *
     * @param lossless whether the compression scheme is lossless.
     * @see #getLossless()
     */
    public void setLossless(boolean lossless) {
        this.lossless = lossless;
    }

    /**
     * Gets <code>lossless</code>.
     *
     * @return whether the compression scheme is lossless.
     * @see #setLossless(boolean)
     */
    public boolean getLossless() {
        return lossless;
    }

    /**
     * Sets <code>filter</code>.
     *
     * @param value which wavelet filters to use for the specified
     * tile-components.
     * @see #getFilter()
     */
    public void setFilter(String value) {
        filter = value;
    }

    /**
     * Gets <code>filters</code>.
     *
     * @return which wavelet filters to use for the specified
     * tile-components.
     * @see #setFilter(String)
     */
    public String getFilter() {
        return filter;
    }

    /**
     * Sets <code>componentTransformation</code>.
     *
     * @param value whether to utilize the component transformation.
     * @see #getComponentTransformation()
     */
    public void setComponentTransformation(boolean value) {
        componentTransformation = value;
    }

    /**
     * Gets <code>componentTransformation</code>.
     *
     * @return whether to utilize the component transformation.
     * @see #setComponentTransformation(boolean)
     */
    public boolean getComponentTransformation() {
        return componentTransformation;
    }

    /**
     * Sets <code>codeBlockSize</code>.
     *
     * @param value the maximum code-block size to use per tile-component.
     * @see #getCodeBlockSize()
     */
    public void setCodeBlockSize(int[] value) {
        codeBlockSize = value;
    }

    /**
     * Gets <code>codeBlockSize</code>.
     *
     * @return the maximum code-block size to use per tile-component.
     * @see #setCodeBlockSize(int[])
     */
    public int[] getCodeBlockSize() {
        return codeBlockSize;
    }

    /**
     * Sets <code>SOP</code>.
     *
     * @param value whether start of packet (SOP) markers should be used.
     * @see #getSOP()
     */
    public void setSOP(boolean value) {
        SOP = value;
    }

    /**
     * Gets <code>SOP</code>.
     *
     * @return whether start of packet (SOP) markers should be used.
     * @see #setSOP(boolean)
     */
    public boolean getSOP() {
        return SOP;
    }

    /**
     * Sets <code>EPH</code>.
     *
     * @param value whether end of packet header (EPH) markers should be used.
     * @see #getEPH()
     */
    public void setEPH(boolean value) {
        EPH = value;
    }

    /**
     * Gets <code>EPH</code>.
     *
     * @return whether end of packet header (EPH) markers should be used.
     * @see #setEPH(boolean)
     */
    public boolean getEPH() {
        return EPH;
    }

    /**
     * Sets <code>progressionType</code>.
     *
     * @param value which type of progression should be used when generating
     * the codestream.
     * @see #getProgressionType()
     */
    public void setProgressionType(String value) {
        progressionType = value;
    }

    /**
     * Gets <code>progressionType</code>.
     *
     * @return which type of progression should be used when generating
     * the codestream.
     * @see #setProgressionType(String)
     */
    public String getProgressionType() {
        return progressionType;
    }

    /** Sets <code>writeCodeStreamOnly</code>.
     *
     * @param value Whether the jpeg2000 code stream only or the jp2 format
     *	       will be written into the output.
     * @see #getWriteCodeStreamOnly()
     */
    public void setWriteCodeStreamOnly(boolean value) {
        writeCodeStreamOnly = value;
    }

    /** Gets <code>writeCodeStreamOnly</code>.
     *
     * @return whether the jpeg2000 code stream only or the jp2 format
     *	       will be written into the output.
     * @see #setWriteCodeStreamOnly(boolean)
     */
    public boolean  getWriteCodeStreamOnly() {
        return writeCodeStreamOnly;
    }
}
