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
 * $RCSfile: BMPMetadataFormat.java,v $
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
 * $Date: 2006/04/14 21:29:14 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.bmp;

import java.util.Arrays;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public class BMPMetadataFormat extends IIOMetadataFormatImpl {

    private static IIOMetadataFormat instance = null;

    private BMPMetadataFormat() {
        super(BMPMetadata.nativeMetadataFormatName,
              CHILD_POLICY_SOME);

        // root -> ImageDescriptor
        addElement("ImageDescriptor",
                   BMPMetadata.nativeMetadataFormatName,
                   CHILD_POLICY_EMPTY);
        addAttribute("ImageDescriptor", "bmpVersion",
                     DATATYPE_STRING, true, null);
        addAttribute("ImageDescriptor", "width",
                     DATATYPE_INTEGER, true, null,
                     "0", "65535", true, true);
        addAttribute("ImageDescriptor", "height",
                     DATATYPE_INTEGER, true, null,
                     "1", "65535", true, true);
        addAttribute("ImageDescriptor", "bitsPerPixel",
                     DATATYPE_INTEGER, true, null,
                     "1", "65535", true, true);
        addAttribute("ImageDescriptor", "compression",
                      DATATYPE_INTEGER, false, null);
	addAttribute("ImageDescriptor", "imageSize",
                     DATATYPE_INTEGER, true, null,
                     "1", "65535", true, true);

	addElement("PixelsPerMeter",
		   BMPMetadata.nativeMetadataFormatName,
		   CHILD_POLICY_EMPTY);
	addAttribute("PixelsPerMeter", "X",
		     DATATYPE_INTEGER, false, null,
                     "1", "65535", true, true);
	addAttribute("PixelsPerMeter", "Y",
		     DATATYPE_INTEGER, false, null,
                     "1", "65535", true, true);

	addElement("ColorsUsed",
		   BMPMetadata.nativeMetadataFormatName,
		   CHILD_POLICY_EMPTY);
	addAttribute("ColorsUsed", "value",
		     DATATYPE_INTEGER, true, null,
                     "0", "65535", true, true);

	addElement("ColorsImportant",
		   BMPMetadata.nativeMetadataFormatName,
		   CHILD_POLICY_EMPTY);
	addAttribute("ColorsImportant", "value",
		     DATATYPE_INTEGER, false, null,
                     "0", "65535", true, true);

	addElement("BI_BITFIELDS_Mask",
		   BMPMetadata.nativeMetadataFormatName,
		   CHILD_POLICY_EMPTY);
	addAttribute("BI_BITFIELDS_Mask", "red",
		     DATATYPE_INTEGER, false, null,
                     "0", "65535", true, true);
	addAttribute("BI_BITFIELDS_Mask", "green",
		     DATATYPE_INTEGER, false, null,
                     "0", "65535", true, true);
	addAttribute("BI_BITFIELDS_Mask", "blue",
		     DATATYPE_INTEGER, false, null,
                     "0", "65535", true, true);

	addElement("ColorSpace",
		   BMPMetadata.nativeMetadataFormatName,
		   CHILD_POLICY_EMPTY);
	addAttribute("ColorSpace", "value",
		     DATATYPE_INTEGER, false, null,
                     "0", "65535", true, true);

	addElement("LCS_CALIBRATED_RGB",
		   BMPMetadata.nativeMetadataFormatName,
		   CHILD_POLICY_EMPTY);

	/// Should the max value be 1.7976931348623157e+308 ?
	addAttribute("LCS_CALIBRATED_RGB", "redX",
		     DATATYPE_DOUBLE, false, null,
                     "0", "65535", true, true);
	addAttribute("LCS_CALIBRATED_RGB", "redY",
		     DATATYPE_DOUBLE, false, null,
                     "0", "65535", true, true);
	addAttribute("LCS_CALIBRATED_RGB", "redZ",
		     DATATYPE_DOUBLE, false, null,
                     "0", "65535", true, true);
	addAttribute("LCS_CALIBRATED_RGB", "greenX",
		     DATATYPE_DOUBLE, false, null,
                     "0", "65535", true, true);
	addAttribute("LCS_CALIBRATED_RGB", "greenY",
		     DATATYPE_DOUBLE, false, null,
                     "0", "65535", true, true);
	addAttribute("LCS_CALIBRATED_RGB", "greenZ",
		     DATATYPE_DOUBLE, false, null,
                     "0", "65535", true, true);
	addAttribute("LCS_CALIBRATED_RGB", "blueX",
		     DATATYPE_DOUBLE, false, null,
                     "0", "65535", true, true);
	addAttribute("LCS_CALIBRATED_RGB", "blueY",
		     DATATYPE_DOUBLE, false, null,
                     "0", "65535", true, true);
	addAttribute("LCS_CALIBRATED_RGB", "blueZ",
		     DATATYPE_DOUBLE, false, null,
                     "0", "65535", true, true);

	addElement("LCS_CALIBRATED_RGB_GAMMA",
		   BMPMetadata.nativeMetadataFormatName,
		   CHILD_POLICY_EMPTY);
	addAttribute("LCS_CALIBRATED_RGB_GAMMA","red",
		     DATATYPE_INTEGER, false, null,
                     "0", "65535", true, true);
	addAttribute("LCS_CALIBRATED_RGB_GAMMA","green",
		     DATATYPE_INTEGER, false, null,
                     "0", "65535", true, true);
	addAttribute("LCS_CALIBRATED_RGB_GAMMA","blue",
		     DATATYPE_INTEGER, false, null,
                     "0", "65535", true, true);

	addElement("Intent",
		   BMPMetadata.nativeMetadataFormatName,
		   CHILD_POLICY_EMPTY);
	addAttribute("Intent", "value",
		     DATATYPE_INTEGER, false, null,
                     "0", "65535", true, true);

        // root -> Palette
        addElement("Palette",
                   BMPMetadata.nativeMetadataFormatName,
                   2, 256);
        addAttribute("Palette", "sizeOfPalette",
                     DATATYPE_INTEGER, true, null);
        addBooleanAttribute("Palette", "sortFlag",
                            false, false);

        // root -> Palette -> PaletteEntry
        addElement("PaletteEntry", "Palette",
                   CHILD_POLICY_EMPTY);
        addAttribute("PaletteEntry", "index",
                     DATATYPE_INTEGER, true, null,
                     "0", "255", true, true);
        addAttribute("PaletteEntry", "red",
                     DATATYPE_INTEGER, true, null,
                     "0", "255", true, true);
        addAttribute("PaletteEntry", "green",
                     DATATYPE_INTEGER, true, null,
                     "0", "255", true, true);
        addAttribute("PaletteEntry", "blue",
                     DATATYPE_INTEGER, true, null,
                     "0", "255", true, true);


        // root -> CommentExtensions
        addElement("CommentExtensions",
                   BMPMetadata.nativeMetadataFormatName,
                   1, Integer.MAX_VALUE);

        // root -> CommentExtensions -> CommentExtension
        addElement("CommentExtension", "CommentExtensions",
                   CHILD_POLICY_EMPTY);
        addAttribute("CommentExtension", "value",
                     DATATYPE_STRING, true, null);
    }

    public boolean canNodeAppear(String elementName,
                                 ImageTypeSpecifier imageType) {
        return true;
    }

    public static synchronized IIOMetadataFormat getInstance() {
        if (instance == null) {
            instance = new BMPMetadataFormat();
        }
        return instance;
    }
}
