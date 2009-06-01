/*
 * $RCSfile: CLibPNGMetadataFormat.java,v $
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListResourceBundle;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public class CLibPNGMetadataFormat extends IIOMetadataFormatImpl {

    private static IIOMetadataFormat instance = null;

    private static String VALUE_0 = "0";
    private static String VALUE_1 = "1";
    private static String VALUE_12 = "12";
    private static String VALUE_23 = "23";
    private static String VALUE_31 = "31";
    private static String VALUE_59 = "59";
    private static String VALUE_60 = "60";
    private static String VALUE_255 = "255";
    private static String VALUE_MAX_16 = "65535"; // 2^16 - 1
    private static String VALUE_MAX_32 = "2147483647"; // 2^32 - 1

    private CLibPNGMetadataFormat() {
        super(CLibPNGMetadata.nativeMetadataFormatName,
              CHILD_POLICY_SOME);

        // root -> IHDR
        addElement("IHDR", CLibPNGMetadata.nativeMetadataFormatName,
                   CHILD_POLICY_EMPTY);

        addAttribute("IHDR", "width", 
                     DATATYPE_INTEGER, true, null, 
                     VALUE_1, VALUE_MAX_32, true, true);

        addAttribute("IHDR", "height", 
                     DATATYPE_INTEGER, true, null, 
                     VALUE_1, VALUE_MAX_32, true, true);

        addAttribute("IHDR", "bitDepth", 
                     DATATYPE_INTEGER, true, null,
                     Arrays.asList(CLibPNGMetadata.IHDR_bitDepths));

        String[] colorTypes = {
            "Grayscale", "RGB", "Palette", "GrayAlpha", "RGBAlpha"
        };
        addAttribute("IHDR", "colorType", 
                     DATATYPE_STRING, true, null,
                     Arrays.asList(colorTypes));

        addAttribute("IHDR", "compressionMethod",
                     DATATYPE_STRING, true, null,
                     Arrays.asList(CLibPNGMetadata.IHDR_compressionMethodNames));

        addAttribute("IHDR", "filterMethod",
                     DATATYPE_STRING, true, null,
                     Arrays.asList(CLibPNGMetadata.IHDR_filterMethodNames));

        addAttribute("IHDR", "interlaceMethod",
                     DATATYPE_STRING, true, null,
                     Arrays.asList(CLibPNGMetadata.IHDR_interlaceMethodNames));

        // root -> PLTE
        addElement("PLTE", CLibPNGMetadata.nativeMetadataFormatName,
                   1, 256);
        
        // root -> PLTE -> PLTEEntry
        addElement("PLTEEntry", "PLTE",
                   CHILD_POLICY_EMPTY);

        addAttribute("PLTEEntry", "index",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);
        
        addAttribute("PLTEEntry", "red",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);
        
        addAttribute("PLTEEntry", "green",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);
        
        addAttribute("PLTEEntry", "blue",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        // root -> bKGD
        addElement("bKGD", CLibPNGMetadata.nativeMetadataFormatName,
                   CHILD_POLICY_CHOICE);

        // root -> bKGD -> bKGD_Grayscale
        addElement("bKGD_Grayscale", "bKGD",
                   CHILD_POLICY_EMPTY);

        addAttribute("bKGD_Grayscale", "gray",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_MAX_16, true, true);

        // root -> bKGD -> bKGD_RGB
        addElement("bKGD_RGB", "bKGD",
                   CHILD_POLICY_EMPTY);

        addAttribute("bKGD_RGB", "red",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_MAX_16, true, true);

        addAttribute("bKGD_RGB", "green",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_MAX_16, true, true);

        addAttribute("bKGD_RGB", "blue",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_MAX_16, true, true);

        // root -> bKGD -> bKGD_Palette
        addElement("bKGD_Palette", "bKGD",
                   CHILD_POLICY_EMPTY);

        addAttribute("bKGD_Palette", "index",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        // root -> cHRM
        addElement("cHRM", CLibPNGMetadata.nativeMetadataFormatName,
                   CHILD_POLICY_EMPTY);

        addAttribute("cHRM", "whitePointX",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_MAX_16, true, true);

        addAttribute("cHRM", "whitePointY",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_MAX_16, true, true);

        addAttribute("cHRM", "redX",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_MAX_16, true, true);

        addAttribute("cHRM", "redY",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_MAX_16, true, true);

        addAttribute("cHRM", "greenX",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_MAX_16, true, true);

        addAttribute("cHRM", "greenY",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_MAX_16, true, true);

        addAttribute("cHRM", "blueX",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_MAX_16, true, true);

        addAttribute("cHRM", "blueY",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_MAX_16, true, true);

        // root -> gAMA
        addElement("gAMA", CLibPNGMetadata.nativeMetadataFormatName,
                   CHILD_POLICY_EMPTY);

        addAttribute("gAMA", "value",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_MAX_32, true, true);

        // root -> hIST
        addElement("hIST", CLibPNGMetadata.nativeMetadataFormatName,
                   1, 256);

        // root -> hISTEntry
        addElement("hISTEntry", "hIST",
                   CHILD_POLICY_EMPTY);

        addAttribute("hISTEntry", "index",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        addAttribute("hISTEntry", "value",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_MAX_16, true, true);

        // root -> iCCP
        addElement("iCCP", CLibPNGMetadata.nativeMetadataFormatName,
                   CHILD_POLICY_EMPTY);

        addAttribute("iCCP", "profileName",
                     DATATYPE_STRING, true, null);

        addAttribute("iCCP", "compressionMethod",
                     DATATYPE_STRING, true, null,
                     Arrays.asList(CLibPNGMetadata.iCCP_compressionMethodNames));

        addObjectValue("iCCP", byte.class, 0, Integer.MAX_VALUE);

        // root -> iTXt
        addElement("iTXt", CLibPNGMetadata.nativeMetadataFormatName,
                   1, Integer.MAX_VALUE);

        // root -> iTXt -> iTXtEntry
        addElement("iTXtEntry", "iTXt",
                   CHILD_POLICY_EMPTY);

        addAttribute("iTXtEntry", "keyword",
                     DATATYPE_STRING, true, null);

        addBooleanAttribute("iTXtEntry", "compressionFlag",
                            false, false);

        addAttribute("iTXtEntry", "compressionMethod",
                     DATATYPE_STRING, true, null);

        addAttribute("iTXtEntry", "languageTag",
                     DATATYPE_STRING, true, null);

        addAttribute("iTXtEntry", "translatedKeyword",
                     DATATYPE_STRING, true, null);

        addAttribute("iTXtEntry", "text",
                     DATATYPE_STRING, true, null);

        // root -> pHYS
        addElement("pHYS", CLibPNGMetadata.nativeMetadataFormatName,
                   CHILD_POLICY_EMPTY);

        addAttribute("pHYS", "pixelsPerUnitXAxis",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_MAX_32, true, true);
        addAttribute("pHYS", "pixelsPerUnitYAxis",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_MAX_32, true, true);
        addAttribute("pHYS", "unitSpecifier",
                     DATATYPE_STRING, true, null,
                     Arrays.asList(CLibPNGMetadata.unitSpecifierNames));

        // root -> sBIT
        addElement("sBIT", CLibPNGMetadata.nativeMetadataFormatName,
                   CHILD_POLICY_CHOICE);

        // root -> sBIT -> sBIT_Grayscale
        addElement("sBIT_Grayscale", "sBIT",
                   CHILD_POLICY_EMPTY);

        addAttribute("sBIT_Grayscale", "gray",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        // root -> sBIT -> sBIT_GrayAlpha
        addElement("sBIT_GrayAlpha", "sBIT",
                   CHILD_POLICY_EMPTY);

        addAttribute("sBIT_GrayAlpha", "gray",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        addAttribute("sBIT_GrayAlpha", "alpha",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        // root -> sBIT -> sBIT_RGB
        addElement("sBIT_RGB", "sBIT",
                   CHILD_POLICY_EMPTY);

        addAttribute("sBIT_RGB", "red",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        addAttribute("sBIT_RGB", "green",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        addAttribute("sBIT_RGB", "blue",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        // root -> sBIT -> sBIT_RGBAlpha
        addElement("sBIT_RGBAlpha", "sBIT",
                   CHILD_POLICY_EMPTY);

        addAttribute("sBIT_RGBAlpha", "red",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        addAttribute("sBIT_RGBAlpha", "green",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        addAttribute("sBIT_RGBAlpha", "blue",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        addAttribute("sBIT_RGBAlpha", "alpha",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        // root -> sBIT -> sBIT_Palette
        addElement("sBIT_Palette", "sBIT",
                   CHILD_POLICY_EMPTY);

        addAttribute("sBIT_Palette", "red",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        addAttribute("sBIT_Palette", "green",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        addAttribute("sBIT_Palette", "blue",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        // root -> sPLT
        addElement("sPLT", CLibPNGMetadata.nativeMetadataFormatName,
                   1, 256);

        // root -> sPLT -> sPLTEntry
        addElement("sPLTEntry", "sPLT",
                   CHILD_POLICY_EMPTY);

        addAttribute("sPLTEntry", "index",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        addAttribute("sPLTEntry", "red",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        addAttribute("sPLTEntry", "green",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        addAttribute("sPLTEntry", "blue",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        addAttribute("sPLTEntry", "alpha",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        // root -> sRGB
        addElement("sRGB", CLibPNGMetadata.nativeMetadataFormatName,
                   CHILD_POLICY_EMPTY);

        addAttribute("sRGB", "renderingIntent",
                     DATATYPE_STRING, true, null,
                     Arrays.asList(CLibPNGMetadata.renderingIntentNames));

        // root -> tEXt
        addElement("tEXt", CLibPNGMetadata.nativeMetadataFormatName,
                   1, Integer.MAX_VALUE);

        // root -> tEXt -> tEXtEntry
        addElement("tEXtEntry", "tEXt",
                   CHILD_POLICY_EMPTY);

        addAttribute("tEXtEntry", "keyword",
                     DATATYPE_STRING, true, null);

        addAttribute("tEXtEntry", "value",
                     DATATYPE_STRING, true, null);

        // root -> tIME
        addElement("tIME", CLibPNGMetadata.nativeMetadataFormatName,
                   CHILD_POLICY_EMPTY);
        
        addAttribute("tIME", "year",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_MAX_16, true, true);

        addAttribute("tIME", "month",
                     DATATYPE_INTEGER, true, null,
                     VALUE_1, VALUE_12, true, true);

        addAttribute("tIME", "day",
                     DATATYPE_INTEGER, true, null,
                     VALUE_1, VALUE_31, true, true);

        addAttribute("tIME", "hour",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_23, true, true);

        addAttribute("tIME", "minute",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_59, true, true);

        addAttribute("tIME", "second",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_60, true, true);

        // root -> tRNS
        addElement("tRNS", CLibPNGMetadata.nativeMetadataFormatName,
                   CHILD_POLICY_CHOICE);
        
        // root -> tRNS -> tRNS_Grayscale
        addElement("tRNS_Grayscale", "tRNS",
                   CHILD_POLICY_EMPTY);

        addAttribute("tRNS_Grayscale", "gray",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_MAX_16, true, true);

        // root -> tRNS -> tRNS_RGB
        addElement("tRNS_RGB", "tRNS",
                   CHILD_POLICY_EMPTY);

        addAttribute("tRNS_RGB", "red",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_MAX_16, true, true);

        addAttribute("tRNS_RGB", "green",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_MAX_16, true, true);

        addAttribute("tRNS_RGB", "blue",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_MAX_16, true, true);

        // root -> tRNS -> tRNS_Palette
        addElement("tRNS_Palette", "tRNS",
                   CHILD_POLICY_EMPTY);

        addAttribute("tRNS_Palette", "index",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        addAttribute("tRNS_Palette", "alpha",
                     DATATYPE_INTEGER, true, null,
                     VALUE_0, VALUE_255, true, true);

        // root -> zTXt
        addElement("zTXt", CLibPNGMetadata.nativeMetadataFormatName,
                   1, Integer.MAX_VALUE);

        // root -> zTXt -> zTXtEntry
        addElement("zTXtEntry", "zTXt",
                   CHILD_POLICY_EMPTY);

        addAttribute("zTXtEntry", "keyword",
                     DATATYPE_STRING, true, null);

        addAttribute("zTXtEntry", "compressionMethod",
                     DATATYPE_STRING, true, null,
                     Arrays.asList(CLibPNGMetadata.zTXt_compressionMethodNames));

        addAttribute("zTXtEntry", "text", 
                     DATATYPE_STRING, true, null);

        // root -> UnknownChunks
        addElement("UnknownChunks", CLibPNGMetadata.nativeMetadataFormatName,
                   1, Integer.MAX_VALUE);

        // root -> UnknownChunks -> UnknownChunk
        addElement("UnknownChunk", "UnknownChunks",
                   CHILD_POLICY_EMPTY);

        addAttribute("UnknownChunk", "type",
                     DATATYPE_STRING, true, null);

        addObjectValue("UnknownChunk", byte.class, 0, Integer.MAX_VALUE);
    }

    public boolean canNodeAppear(String elementName,
                                 ImageTypeSpecifier imageType) {
        return true;
    }
    
    public static synchronized IIOMetadataFormat getInstance() {
        if (instance == null) {
            instance = new CLibPNGMetadataFormat();
        }
        return instance;
    }
}
