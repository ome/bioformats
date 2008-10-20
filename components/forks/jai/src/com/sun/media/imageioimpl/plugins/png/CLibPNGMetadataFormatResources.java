/*
 * $RCSfile: CLibPNGMetadataFormatResources.java,v $
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
 * $Date: 2005/02/11 05:01:39 $
 * $State: Exp $
 */

package com.sun.media.imageioimpl.plugins.png;

import java.util.ListResourceBundle;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public class CLibPNGMetadataFormatResources extends ListResourceBundle {

    static final Object[][] contents = {
        // Node name, followed by description
        { "IHDR", "The IHDR chunk, containing the header" },
        { "PLTE", "The PLTE chunk, containing the palette" },
        { "PLTEEntry", "A palette entry" },
        { "bKGD", "The bKGD chunk, containing the background color" },
        { "bKGD_RGB", "An RGB background color, for RGB and RGBAlpha images" },
        { "bKGD_Grayscale",
          "A grayscale background color, for Gray and GrayAlpha images" },
        { "bKGD_Palette", "A background palette index" },
        { "cHRM", "The cHRM chunk, containing color calibration" },
        { "gAMA", "The gAMA chunk, containing the image gamma" },
        { "hIST", "The hIST chunk, containing histogram information " },
        { "hISTEntry", "A histogram entry" },
        { "iCCP", "The iCCP chunk, containing an ICC color profile" },
        { "iTXt", "The iTXt chunk, containing internationalized text" },
        { "iTXtEntry", "A localized text entry" },
        { "pHYS",
          "The pHYS chunk, containing the pixel size and aspect ratio" },
        { "sBIT", "The sBIT chunk, containing significant bit information" },
        { "sBIT_Grayscale", "Significant bit information for gray samples" },
        { "sBIT_GrayAlpha",
          "Significant bit information for gray and alpha samples" },
        { "sBIT_RGB", "Significant bit information for RGB samples" },
        { "sBIT_RGBAlpha", "Significant bit information for RGBA samples" },
        { "sBIT_Palette",
          "Significant bit information for RGB palette entries" },
        { "sPLT", "The sPLT chunk, containing a suggested palette" },
        { "sPLTEntry", "A suggested palette entry" },
        { "sRGB", "The sRGB chunk, containing rendering intent information" },
        { "tEXt", "The tEXt chunk, containing text" },
        { "tEXtEntry", "A text entry" },
        { "tIME", "The tIME chunk, containing the image modification time" },
        { "tRNS", "The tRNS chunk, containing transparency information" },
        { "tRNS_Grayscale",
          "A grayscale value that should be considered transparent" },
        { "tRNS_RGB",
          "An RGB value that should be considered transparent" },
        { "tRNS_Palette",
          "A palette index that should be considered transparent" },
        { "zTXt", "The zTXt chunk, containing compressed text" },
        { "zTXtEntry", "A compressed text entry" },
        { "UnknownChunks", "A set of unknown chunks" },
        { "UnknownChunk", "Unknown chunk data stored as a byte array" },

        // Node name + "/" + AttributeName, followed by description
        { "IHDR/width", "The width of the image in pixels" },
        { "IHDR/height", "The height of the image in pixels" },
        { "IHDR/bitDepth", "The bit depth of the image samples" },
        { "IHDR/colorType", "The color type of the image" },
        { "IHDR/compressionMethod",
"The compression used for image data, always \"deflate\"" },
        { "IHDR/filterMethod",
"The filtering method used for compression, always \"adaptive\"" },
        { "IHDR/interlaceMethod",
          "The interlacing method, \"none\" or \"adam7\"" },

        { "PLTEEntry/index", "The index of a palette entry" },
        { "PLTEEntry/red", "The red value of a palette entry" },
        { "PLTEEntry/green", "The green value of a palette entry" },
        { "PLTEEntry/blue", "The blue value of a palette entry" },

        { "bKGD_Grayscale/gray", "A gray value to be used as a background" },
        { "bKGD_RGB/red", "A red value to be used as a background" },
        { "bKGD_RGB/green", "A green value to be used as a background" },
        { "bKGD_RGB/blue", "A blue value to be used as a background" },
        { "bKGD_Palette/index", "A palette index to be used as a background" },

        { "cHRM/whitePointX",
              "The CIE x coordinate of the white point, multiplied by 1e5" },
        { "cHRM/whitePointY",
              "The CIE y coordinate of the white point, multiplied by 1e5" },
        { "cHRM/redX",
              "The CIE x coordinate of the red primary, multiplied by 1e5" },
        { "cHRM/redY",
              "The CIE y coordinate of the red primary, multiplied by 1e5" },
        { "cHRM/greenX",
              "The CIE x coordinate of the green primary, multiplied by 1e5" },
        { "cHRM/greenY",
              "The CIE y coordinate of the green primary, multiplied by 1e5" },
        { "cHRM/blueX",
              "The CIE x coordinate of the blue primary, multiplied by 1e5" },
        { "cHRM/blueY",
              "The CIE y coordinate of the blue primary, multiplied by 1e5" },

        { "gAMA/value",
              "The image gamma, multiplied by 1e5" },

        { "hISTEntry/index", "The palette index of this histogram entry" },
        { "hISTEntry/value", "The frequency of this histogram entry" },

        { "iCCP/profileName", "The name of this ICC profile" },
        { "iCCP/compressionMethod",
              "The compression method used to store this ICC profile" },

        { "iTXtEntry/keyword", "The keyword" },
        { "iTXtEntry/compressionMethod",
              "The compression method used to store this iTXt entry" },
        { "iTXtEntry/languageTag",
              "The ISO tag describing the language of this iTXt entry" },
        { "iTXtEntry/translatedKeyword",
              "The translated keyword for iTXt entry" },
        { "iTXtEntry/text",
              "The localized text" },

        { "pHYS/pixelsPerUnitXAxis",
            "The number of horizontal pixels per unit, multiplied by 1e5" },
        { "pHYS/pixelsPerUnitYAxis",
            "The number of vertical pixels per unit, multiplied by 1e5" },
        { "pHYS/unitSpecifier",
            "The unit specifier for this chunk (i.e., meters)" },
        
        { "sBIT_Grayscale/gray",
            "The number of significant bits of the gray samples" },
        { "sBIT_GrayAlpha/gray",
            "The number of significant bits of the gray of gray/alpha samples" },
        { "sBIT_GrayAlpha/alpha",
            "The number of significant bits of the alpha of gray/alpha samples" },
        { "sBIT_RGB/red",
            "The number of significant bits of the red of RGB samples" },
        { "sBIT_RGB/green",
            "The number of significant bits of the green of RGB samples" },
        { "sBIT_RGB/blue",
            "The number of significant bits of the blue of RGB samples" },
        { "sBIT_RGBAlpha/red",
            "The number of significant bits of the red of RGBA samples" },
        { "sBIT_RGBAlpha/green",
            "The number of significant bits of the green of RGBA samples" },
        { "sBIT_RGBAlpha/blue",
            "The number of significant bits of the blue of RGBA samples" },
        { "sBIT_RGBAlpha/alpha",
            "The number of significant bits of the alpha of RGBA samples" },
        { "sBIT_Palette/red",
            "The number of significant bits of the red palette entries" },
        { "sBIT_Palette/green",
            "The number of significant bits of the green palette entries" },
        { "sBIT_Palette/blue",
            "The number of significant bits of the blue palette entries" },

        { "sPLTEntry/index", "The index of a suggested palette entry" },
        { "sPLTEntry/red", "The red value of a suggested palette entry" },
        { "sPLTEntry/green", "The green value of a suggested palette entry" },
        { "sPLTEntry/blue", "The blue value of a suggested palette entry" },
        { "sPLTEntry/alpha", "The blue value of a suggested palette entry" },

        { "sRGB/renderingIntent", "The rendering intent" },

        { "tEXtEntry/keyword", "The keyword" },
        { "tEXtEntry/value", "The text" },

        { "tIME/year", "The year when the image was last modified" },
        { "tIME/month",
          "The month when the image was last modified, 1 = January" },
        { "tIME/day",
          "The day of the month when the image was last modified" },
        { "tIME/hour",
          "The hour when the image was last modified" },
        { "tIME/minute",
          "The minute when the image was last modified" },
        { "tIME/second",
          "The second when the image was last modified, 60 = leap second" },

        { "tRNS_Grayscale/gray",
          "The gray value to be considered transparent" },
        { "tRNS_RGB/red",
          "The red value to be considered transparent" },
        { "tRNS_RGB/green",
          "The green value to be considered transparent" },
        { "tRNS_RGB/blue",
          "The blue value to be considered transparent" },
        { "tRNS_Palette/index",
          "A palette index to be considered transparent" },
        { "tRNS_Palette/alpha",
          "The transparency associated with the palette entry" },

        { "zTXtEntry/keyword", "The keyword" },
        { "zTXtEntry/compressionMethod", "The compression method" },
        { "zTXtEntry/text", "The compressed text" },

        { "UnknownChunk/type", "The 4-character type of the unknown chunk" }
    };

    public CLibPNGMetadataFormatResources() {}

    public Object[][] getContents() {
        return contents;
    }
}
