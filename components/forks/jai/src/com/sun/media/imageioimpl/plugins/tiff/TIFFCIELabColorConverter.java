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
 * $RCSfile: TIFFCIELabColorConverter.java,v $
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

import com.sun.media.imageio.plugins.tiff.TIFFColorConverter;

/**
 */
public class TIFFCIELabColorConverter extends TIFFColorConverter {

    // XYX coordinate or reference white (CIE D65)
    private static final float Xn = 95.047f;
    private static final float Yn = 100.0f;
    private static final float Zn = 108.883f;

    private static final float THRESHOLD = (float)Math.pow(0.008856, 1.0/3.0);

    public TIFFCIELabColorConverter() {}


    private float clamp(float x) {
        if (x < 0.0f) {
            return 0.0f;
        } else if (x > 100.0f) {
            return 255.0f;
        } else {
            return x*(255.0f/100.0f);
        }
    }

    private float clamp2(float x) {
        if (x < 0.0f) {
            return 0.0f;
        } else if (x > 255.0f) {
            return 255.0f;
        } else {
            return x;
        }
    }

    public void fromRGB(float r, float g, float b, float[] result) {
        float X =  0.412453f*r + 0.357580f*g + 0.180423f*b;
        float Y =  0.212671f*r + 0.715160f*g + 0.072169f*b;
        float Z =  0.019334f*r + 0.119193f*g + 0.950227f*b;

        float YYn = Y/Yn;
        float XXn = X/Xn;
        float ZZn = Z/Zn;

        if (YYn < 0.008856f) {
            YYn = 7.787f*YYn + 16.0f/116.0f;
        } else {
            YYn = (float)Math.pow(YYn, 1.0/3.0);
        }
        
        if (XXn < 0.008856f) {
            XXn = 7.787f*XXn + 16.0f/116.0f;
        } else {
            XXn = (float)Math.pow(XXn, 1.0/3.0);
        }
        
        if (ZZn < 0.008856f) {
            ZZn = 7.787f*ZZn + 16.0f/116.0f;
        } else {
            ZZn = (float)Math.pow(ZZn, 1.0/3.0);
        }
        
        float LStar = 116.0f*YYn - 16.0f;
        float aStar = 500.0f*(XXn - YYn);
        float bStar = 200.0f*(YYn - ZZn);

        LStar *= 255.0f/100.0f;
        if (aStar < 0.0f) {
            aStar += 256.0f;
        }
        if (bStar < 0.0f) {
            bStar += 256.0f;
        }

        result[0] = clamp2(LStar);
        result[1] = clamp2(aStar);
        result[2] = clamp2(bStar);
    }

    public void toRGB(float x0, float x1, float x2, float[] rgb) {
        float LStar = x0*100.0f/255.0f;
        float aStar = (x1 > 128.0f) ? (x1 - 256.0f) : x1;
        float bStar = (x2 > 128.0f) ? (x2 - 256.0f) : x2;

        float YYn; // Y/Yn
        float fY; // 'F' value for Y
        
        if (LStar < 8.0f) {
            YYn = LStar/903.3f;
            fY = 7.787f*YYn + 16.0f/116.0f;
        } else {
            float YYn_cubeRoot = (LStar + 16.0f)/116.0f;
            YYn = YYn_cubeRoot*YYn_cubeRoot*YYn_cubeRoot;
            fY = (float)Math.pow(YYn, 1.0/3.0);
        }
        float Y = YYn*Yn;
        
        float fX = fY + (aStar/500.0f);
        float X;
        if (fX <= THRESHOLD) {
            X = Xn*(fX - 16.0f/116.0f)/7.787f;
        } else {
            X = Xn*fX*fX*fX;
        }
        
        float fZ = fY - bStar/200.0f;
        float Z;
        if (fZ <= THRESHOLD) {
            Z = Zn*(fZ - 16.0f/116.0f)/7.787f;
        } else {
            Z = Zn*fZ*fZ*fZ;
        }

        float R =  3.240479f*X - 1.537150f*Y - 0.498535f*Z;
        float G = -0.969256f*X + 1.875992f*Y + 0.041556f*Z;
        float B =  0.055648f*X - 0.204043f*Y + 1.057311f*Z;

        rgb[0] = clamp(R);
        rgb[1] = clamp(G);
        rgb[2] = clamp(B);
    }
}
