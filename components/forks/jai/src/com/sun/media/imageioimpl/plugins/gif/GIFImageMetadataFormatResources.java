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
 * $RCSfile: GIFImageMetadataFormatResources.java,v $
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

import java.util.ListResourceBundle;

public class GIFImageMetadataFormatResources extends ListResourceBundle {

    public GIFImageMetadataFormatResources() {}

    protected Object[][] getContents() {
        return new Object[][] {

        // Node name, followed by description
        { "ImageDescriptor", "The image descriptor" },
        { "LocalColorTable", "The local color table" },
        { "ColorTableEntry", "A local color table entry" },
        { "GraphicControlExtension", "A graphic control extension" },
        { "PlainTextExtension", "A plain text (text grid) extension" },
        { "ApplicationExtensions", "A set of application extensions" },
        { "ApplicationExtension", "An application extension" },
        { "CommentExtensions", "A set of comments" },
        { "CommentExtension", "A comment" },

        // Node name + "/" + AttributeName, followed by description
        { "ImageDescriptor/imageLeftPosition",
          "The X offset of the image relative to the screen origin" },
        { "ImageDescriptor/imageTopPosition",
          "The Y offset of the image relative to the screen origin" },
        { "ImageDescriptor/imageWidth",
          "The width of the image" },
        { "ImageDescriptor/imageHeight",
          "The height of the image" },
        { "ImageDescriptor/interlaceFlag",
          "True if the image is stored using interlacing" },
        { "LocalColorTable/sizeOfLocalColorTable",
          "The number of entries in the local color table" },
        { "LocalColorTable/sortFlag",
          "True if the local color table is sorted by frequency" },
        { "ColorTableEntry/index", "The index of the color table entry" },
        { "ColorTableEntry/red",
          "The red value for the color table entry" },
        { "ColorTableEntry/green",
          "The green value for the color table entry" },
        { "ColorTableEntry/blue",
          "The blue value for the color table entry" },
        { "GraphicControlExtension/disposalMethod",
          "The disposal method for this frame" },
        { "GraphicControlExtension/userInputFlag",
          "True if the frame should be advanced based on user input" },
        { "GraphicControlExtension/transparentColorFlag",
          "True if a transparent color exists" },
        { "GraphicControlExtension/delayTime",
          "The time to delay between frames, in hundredths of a second" },
        { "GraphicControlExtension/transparentColorIndex",
          "The transparent color, if transparentColorFlag is true" },
        { "PlainTextExtension/textGridLeft",
          "The X offset of the text grid" },
        { "PlainTextExtension/textGridTop",
          "The Y offset of the text grid" },
        { "PlainTextExtension/textGridWidth",
          "The number of columns in the text grid" },
        { "PlainTextExtension/textGridHeight",
          "The number of rows in the text grid" },
        { "PlainTextExtension/characterCellWidth",
          "The width of a character cell" },
        { "PlainTextExtension/characterCellHeight",
          "The height of a character cell" },
        { "PlainTextExtension/textForegroundColor",
          "The text foreground color index" },
        { "PlainTextExtension/textBackgroundColor",
          "The text background color index" },
        { "ApplicationExtension/applicationID",
          "The application ID" },
        { "ApplicationExtension/authenticationCode",
          "The authentication code" },
        { "CommentExtension/value", "The comment" },

        };
    }
}
