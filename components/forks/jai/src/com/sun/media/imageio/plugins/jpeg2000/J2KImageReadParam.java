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
 * $RCSfile: J2KImageReadParam.java,v $
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
 * $Date: 2006/09/29 19:25:32 $
 * $State: Exp $
 */
package com.sun.media.imageio.plugins.jpeg2000;

import javax.imageio.ImageReadParam;

/**
 * A subclass of <code>ImageReadParam</code> for reading images in
 * the JPEG 2000 format.
 *
 * <p>The decoding parameters for JPEG 2000 are listed below:
 *
 * <p><table border=1>
 * <caption><b>JPEG 2000 Plugin Decoding Parameters</b></caption>
 * <tr><th>Parameter Name</th> <th>Description</th></tr>
 * <tr>
 *    <td>decodingRate</td>
 *    <td>Specifies the decoding rate in bits per pixel (bpp) where the
 *    number of pixels is related to the image's original size (Note:
 *    this parameter is not affected by <code>resolution</code>).
 *    The default is <code>Double.MAX_VALUE</code>.
 *    It means decoding with the encoding rate.
 *    </td>
 * </tr>
 * <tr>
 *    <td>resolution</td>
 *    <td>Specifies the resolution level wanted for the decoded image
 *    (0 means the lowest available resolution, the resolution
 *    level gives an image with the original dimension).  If the given index
 *    is greater than the number of available resolution levels of the
 *    compressed image, the decoded image has the lowest available
 *    resolution (among all tile-components).  This parameter affects only
 *    the inverse wavelet transform and not the number of bytes read by the
 *    codestream parser, which depends only on <code>decodingRate</code>.
 *    The default value, -1, means to use the resolution level at encoding.
 *    </td>
 * </tr>
 * </table>
 */
public class J2KImageReadParam extends ImageReadParam {
    /** Specifies the decoding rate in bits per pixel (bpp) where the
     *  number of  pixels is related to the image's original size
     *  (Note: this number is not affected by <code>resolution</code>).
     */
    private double decodingRate = Double.MAX_VALUE;

    /** Specifies the resolution level wanted for the decoded image
     *  (0 means the lowest available resolution, the resolution
     *  level gives an image with the original dimension).  If the given index
     *  is greater than the number of available resolution levels of the
     *  compressed image, the decoded image has the lowest available
     *  resolution (among all tile-components).  This parameter
     *  affects only the inverse wavelet transform but not the number
     *  of bytes read by the codestream parser, which
     *  depends only on <code>decodingRate</code>.
     */
    private int resolution = -1;

    /** Constructs a default instance of <code>J2KImageReadParam</code>. */
    public J2KImageReadParam() {
        super();
    }

    /**
     * Sets <code>decodingRate</code>.
     *
     * @param rate the decoding rate in bits per pixel.
     * @see #getDecodingRate()
     */
    public void setDecodingRate(double rate) {
        this.decodingRate = rate;
    }

    /**
     * Gets <code>decodingRate</code>.
     *
     * @return the decoding rate in bits per pixel.
     * @see #setDecodingRate(double)
     */
    public double getDecodingRate() {
        return decodingRate;
    }

    /**
     * Sets <code>resolution</code>.
     *
     * @param resolution the resolution level with 0 being
     * the lowest available.
     * @see #getResolution()
     */
    public void setResolution(int resolution) {
        this.resolution = Math.max(resolution, -1);
    }

    /**
     * Gets <code>resolution</code>.
     *
     * @return the resolution level with 0 being
     * the lowest available.
     * @see #setResolution(int)
     */
    public int getResolution() {
        return resolution;
    }
}
