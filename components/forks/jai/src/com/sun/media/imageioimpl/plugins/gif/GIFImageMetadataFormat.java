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
 * $RCSfile: GIFImageMetadataFormat.java,v $
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
 * $Date: 2006/03/24 22:30:10 $
 * $State: Exp $
 */

package com.sun.media.imageioimpl.plugins.gif;

import java.util.Arrays;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public class GIFImageMetadataFormat extends IIOMetadataFormatImpl {

    private static IIOMetadataFormat instance = null;

    private GIFImageMetadataFormat() {
        super(GIFImageMetadata.nativeMetadataFormatName,
              CHILD_POLICY_SOME);

        // root -> ImageDescriptor
        addElement("ImageDescriptor",
                   GIFImageMetadata.nativeMetadataFormatName,
                   CHILD_POLICY_EMPTY);
        addAttribute("ImageDescriptor", "imageLeftPosition",
                     DATATYPE_INTEGER, true, null,
                     "0", "65535", true, true); 
        addAttribute("ImageDescriptor", "imageTopPosition",
                     DATATYPE_INTEGER, true, null,
                     "0", "65535", true, true); 
        addAttribute("ImageDescriptor", "imageWidth",
                     DATATYPE_INTEGER, true, null,
                     "1", "65535", true, true); 
        addAttribute("ImageDescriptor", "imageHeight",
                     DATATYPE_INTEGER, true, null,
                     "1", "65535", true, true); 
        addBooleanAttribute("ImageDescriptor", "interlaceFlag",
                            false, false);

        // root -> LocalColorTable
        addElement("LocalColorTable",
                   GIFImageMetadata.nativeMetadataFormatName,
                   2, 256);
        addAttribute("LocalColorTable", "sizeOfLocalColorTable",
                     DATATYPE_INTEGER, true, null,
                     Arrays.asList(GIFStreamMetadata.colorTableSizes));
        addBooleanAttribute("LocalColorTable", "sortFlag",
                            false, false);

        // root -> LocalColorTable -> ColorTableEntry
        addElement("ColorTableEntry", "LocalColorTable",
                   CHILD_POLICY_EMPTY);
        addAttribute("ColorTableEntry", "index",
                     DATATYPE_INTEGER, true, null,
                     "0", "255", true, true);
        addAttribute("ColorTableEntry", "red",
                     DATATYPE_INTEGER, true, null,
                     "0", "255", true, true);
        addAttribute("ColorTableEntry", "green",
                     DATATYPE_INTEGER, true, null,
                     "0", "255", true, true);
        addAttribute("ColorTableEntry", "blue",
                     DATATYPE_INTEGER, true, null,
                     "0", "255", true, true);

        // root -> GraphicControlExtension
        addElement("GraphicControlExtension",
                   GIFImageMetadata.nativeMetadataFormatName,
                   CHILD_POLICY_EMPTY);
        addAttribute("GraphicControlExtension", "disposalMethod",
                     DATATYPE_STRING, true, null,
                     Arrays.asList(GIFImageMetadata.disposalMethodNames));
        addBooleanAttribute("GraphicControlExtension", "userInputFlag",
                            false, false);
        addBooleanAttribute("GraphicControlExtension", "transparentColorFlag",
                            false, false);
        addAttribute("GraphicControlExtension", "delayTime",
                     DATATYPE_INTEGER, true, null,
                     "0", "65535", true, true);
        addAttribute("GraphicControlExtension", "transparentColorIndex",
                     DATATYPE_INTEGER, true, null,
                     "0", "255", true, true);

        // root -> PlainTextExtension
        addElement("PlainTextExtension",
                   GIFImageMetadata.nativeMetadataFormatName,
                   CHILD_POLICY_EMPTY);
        addAttribute("PlainTextExtension", "textGridLeft",
                     DATATYPE_INTEGER, true, null,
                     "0", "65535", true, true);
        addAttribute("PlainTextExtension", "textGridTop",
                     DATATYPE_INTEGER, true, null,
                     "0", "65535", true, true);
        addAttribute("PlainTextExtension", "textGridWidth",
                     DATATYPE_INTEGER, true, null,
                     "1", "65535", true, true);
        addAttribute("PlainTextExtension", "textGridHeight",
                     DATATYPE_INTEGER, true, null,
                     "1", "65535", true, true);
        addAttribute("PlainTextExtension", "characterCellWidth",
                     DATATYPE_INTEGER, true, null,
                     "1", "65535", true, true);
        addAttribute("PlainTextExtension", "characterCellHeight",
                     DATATYPE_INTEGER, true, null,
                     "1", "65535", true, true);
        addAttribute("PlainTextExtension", "textForegroundColor",
                     DATATYPE_INTEGER, true, null,
                     "0", "255", true, true);
        addAttribute("PlainTextExtension", "textBackgroundColor",
                     DATATYPE_INTEGER, true, null,
                     "0", "255", true, true);
        
        // root -> ApplicationExtensions
        addElement("ApplicationExtensions",
                   GIFImageMetadata.nativeMetadataFormatName,
                   1, Integer.MAX_VALUE);

        // root -> ApplicationExtensions -> ApplicationExtension
        addElement("ApplicationExtension", "ApplicationExtensions",
                   CHILD_POLICY_EMPTY);
        addAttribute("ApplicationExtension", "applicationID",
                     DATATYPE_STRING, true, null);
        addAttribute("ApplicationExtension", "authenticationCode",
                     DATATYPE_STRING, true, null);
        addObjectValue("ApplicationExtension", byte.class,
                       0, Integer.MAX_VALUE);

        // root -> CommentExtensions
        addElement("CommentExtensions",
                   GIFImageMetadata.nativeMetadataFormatName,
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
            instance = new GIFImageMetadataFormat();
        }
        return instance;
    }
}
