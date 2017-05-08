/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2017 Open Microscopy Environment:
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
 * $RCSfile: InvertedCMYKColorSpace.java,v $
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
 * $Date: 2006/04/24 20:53:01 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.common;

import java.awt.color.ColorSpace;

/**
 * Singleton class representing a simple, mathematically defined
 * inverted CMYK color space.
 */
public final class InvertedCMYKColorSpace extends ColorSpace {
    private static ColorSpace theInstance = null;
    private ColorSpace csRGB;

    /** The exponent for gamma correction. */
    private static final double power1 = 1.0 / 2.4;

    public static final synchronized ColorSpace getInstance() {
        if(theInstance == null) {
            theInstance = new InvertedCMYKColorSpace();
        }
        return theInstance;
    }

    private InvertedCMYKColorSpace() {
        super(TYPE_CMYK, 4);
        csRGB = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);
    }

    public boolean equals(Object o) {
        return o != null && o instanceof InvertedCMYKColorSpace;
    }

    public float[] toRGB(float[] colorvalue) {
        float C = colorvalue[0];
        float M = colorvalue[1];
        float Y = colorvalue[2];
        float K = colorvalue[3];

        // Convert from CMYK to linear RGB.
        float[] rgbvalue = new float[] {K*C, K*M, K*Y};

        // Convert from linear RGB to sRGB.
        for (int i = 0; i < 3; i++) {
            float v = rgbvalue[i];

            if (v < 0.0F) v = 0.0F;

            if (v < 0.0031308F) {
                rgbvalue[i] = 12.92F * v;
            } else {
                if (v > 1.0F) v = 1.0F;

                rgbvalue[i] = (float)(1.055 * Math.pow(v, power1) - 0.055);
            }
        }

        return rgbvalue;
    }

    public float[] fromRGB(float[] rgbvalue) {
        // Convert from sRGB to linear RGB.
        for (int i = 0; i < 3; i++) {
            if (rgbvalue[i] < 0.040449936F) {
                rgbvalue[i] /= 12.92F;
            } else {
                rgbvalue[i] =
                (float)(Math.pow((rgbvalue[i] + 0.055)/1.055, 2.4));
            }
        }

        // Convert from linear RGB to CMYK.
        float C = rgbvalue[0];
        float M = rgbvalue[1];
        float Y = rgbvalue[2];
        float K = Math.max(C, Math.max(M, Y));

        // If K == 0.0F, then C = M = Y = 0.0F.
        if(K != 0.0F) {
            C = C/K;
            M = M/K;
            Y = Y/K;
        } else { // K == 0.0F
            C = M = Y = 1.0F;
        }

        return new float[] {C, M, Y, K};
    }

    public float[] toCIEXYZ(float[] colorvalue) {
        return csRGB.toCIEXYZ(toRGB(colorvalue));
    }

    public float[] fromCIEXYZ(float[] xyzvalue) {
        return fromRGB(csRGB.fromCIEXYZ(xyzvalue));
    }
}
