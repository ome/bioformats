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
 * $RCSfile: TIFFYCbCrColorConverter.java,v $
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
 * $Date: 2006/04/11 22:10:37 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.tiff;

import java.awt.color.ColorSpace;
import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.TIFFColorConverter;
import com.sun.media.imageio.plugins.tiff.TIFFField;

/**
 */
public class TIFFYCbCrColorConverter extends TIFFColorConverter {

    private float LumaRed = 0.299f;
    private float LumaGreen = 0.587f;
    private float LumaBlue = 0.114f;

    private float referenceBlackY = 0.0f;
    private float referenceWhiteY = 255.0f;

    private float referenceBlackCb = 128.0f;
    private float referenceWhiteCb = 255.0f;

    private float referenceBlackCr = 128.0f;
    private float referenceWhiteCr = 255.0f;

    private float codingRangeY = 255.0f;
    private float codingRangeCbCr = 127.0f;

    public TIFFYCbCrColorConverter(TIFFImageMetadata metadata) {
        TIFFImageMetadata tmetadata = (TIFFImageMetadata)metadata;

        TIFFField f =
           tmetadata.getTIFFField(BaselineTIFFTagSet.TAG_Y_CB_CR_COEFFICIENTS);
        if (f != null && f.getCount() == 3) {
            this.LumaRed = f.getAsFloat(0);
            this.LumaGreen = f.getAsFloat(1);
            this.LumaBlue = f.getAsFloat(2);
        }

        f =
          tmetadata.getTIFFField(BaselineTIFFTagSet.TAG_REFERENCE_BLACK_WHITE);
        if (f != null && f.getCount() == 6) {
            this.referenceBlackY = f.getAsFloat(0);
            this.referenceWhiteY = f.getAsFloat(1);
            this.referenceBlackCb = f.getAsFloat(2);
            this.referenceWhiteCb = f.getAsFloat(3);
            this.referenceBlackCr = f.getAsFloat(4);
            this.referenceWhiteCr = f.getAsFloat(5);
        }
    }

    /*
      The full range component value is converted from the code by:

      FullRangeValue = (code - ReferenceBlack) * CodingRange
                / (ReferenceWhite - ReferenceBlack);

      The code is converted from the full-range component value by:

      code = (FullRangeValue * (ReferenceWhite - ReferenceBlack)
                / CodingRange) + ReferenceBlack;

     */
    public void fromRGB(float r, float g, float b, float[] result) {
        // Convert RGB to full-range YCbCr.
        float Y = (LumaRed*r + LumaGreen*g + LumaBlue*b);
        float Cb = (b - Y)/(2 - 2*LumaBlue);
        float Cr = (r - Y)/(2 - 2*LumaRed);

        // Convert full-range YCbCr to code.
        result[0] = Y*(referenceWhiteY - referenceBlackY)/codingRangeY +
            referenceBlackY;
        result[1] = Cb*(referenceWhiteCb - referenceBlackCb)/codingRangeCbCr +
            referenceBlackCb;
        result[2] = Cr*(referenceWhiteCr - referenceBlackCr)/codingRangeCbCr +
            referenceBlackCr;
    }

    public void toRGB(float x0, float x1, float x2, float[] rgb) {
        // Convert YCbCr code to full-range YCbCr.
        float Y = (x0 - referenceBlackY)*codingRangeY/
            (referenceWhiteY - referenceBlackY);
        float Cb = (x1 - referenceBlackCb)*codingRangeCbCr/
            (referenceWhiteCb - referenceBlackCb);
        float Cr = (x2 - referenceBlackCr)*codingRangeCbCr/
            (referenceWhiteCr - referenceBlackCr);

        // Convert YCbCr to RGB.
        rgb[0] = Cr*(2 - 2*LumaRed) + Y;
        rgb[2] = Cb*(2 - 2*LumaBlue) + Y;
        rgb[1] = (Y - LumaBlue*rgb[2] - LumaRed*rgb[0])/LumaGreen;
    }
}
