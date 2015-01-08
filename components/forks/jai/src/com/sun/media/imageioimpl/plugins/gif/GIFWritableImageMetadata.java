/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2014 Open Microscopy Environment:
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
 * $RCSfile: GIFWritableImageMetadata.java,v $
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
 * $Date: 2006/03/24 22:30:11 $
 * $State: Exp $
 */

package com.sun.media.imageioimpl.plugins.gif;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import org.w3c.dom.Node;

class GIFWritableImageMetadata extends GIFImageMetadata {

    // package scope
    static final String
    NATIVE_FORMAT_NAME = "javax_imageio_gif_image_1.0";

    GIFWritableImageMetadata() {
        super(true,
              NATIVE_FORMAT_NAME,
              "com.sun.media.imageioimpl.plugins.gif.GIFImageMetadataFormat",
              null, null);
    }

    public boolean isReadOnly() {
        return false;
    }

    public void reset() {
        // Fields from Image Descriptor
        imageLeftPosition = 0;
        imageTopPosition = 0;
        imageWidth = 0;
        imageHeight = 0;
        interlaceFlag = false;
        sortFlag = false;
        localColorTable = null;

        // Fields from Graphic Control Extension
        disposalMethod = 0;
        userInputFlag = false;
        transparentColorFlag = false;
        delayTime = 0;
        transparentColorIndex = 0;

        // Fields from Plain Text Extension
        hasPlainTextExtension = false;
        textGridLeft = 0;
        textGridTop = 0;
        textGridWidth = 0;
        textGridHeight = 0;
        characterCellWidth = 0;
        characterCellHeight = 0;
        textForegroundColor = 0;
        textBackgroundColor = 0;
        text = null;

        // Fields from ApplicationExtension
        applicationIDs = null;
        authenticationCodes = null;
        applicationData = null;

        // Fields from CommentExtension
        // List of byte[]
        comments = null;
    }

    private byte[] fromISO8859(String data) {
        try {
            return data.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            return (new String("")).getBytes();
        }
    }

    protected void mergeNativeTree(Node root) throws IIOInvalidTreeException {
        Node node = root;
        if (!node.getNodeName().equals(nativeMetadataFormatName)) {
            fatal(node, "Root must be " + nativeMetadataFormatName);
        }

        node = node.getFirstChild();
        while (node != null) {
            String name = node.getNodeName();

            if (name.equals("ImageDescriptor")) {
                imageLeftPosition = getIntAttribute(node,
                                                    "imageLeftPosition",
                                                    -1, true,
                                                    true, 0, 65535);

                imageTopPosition = getIntAttribute(node,
                                                   "imageTopPosition",
                                                   -1, true,
                                                   true, 0, 65535);

                imageWidth = getIntAttribute(node,
                                             "imageWidth",
                                             -1, true,
                                             true, 1, 65535);

                imageHeight = getIntAttribute(node,
                                              "imageHeight",
                                              -1, true,
                                              true, 1, 65535);

                interlaceFlag = getBooleanAttribute(node, "interlaceFlag",
                                                    false, true);
            } else if (name.equals("LocalColorTable")) {
                int sizeOfLocalColorTable =
                    getIntAttribute(node, "sizeOfLocalColorTable",
                                    true, 2, 256);
                if (sizeOfLocalColorTable != 2 &&
                    sizeOfLocalColorTable != 4 &&
                    sizeOfLocalColorTable != 8 &&
                    sizeOfLocalColorTable != 16 &&
                    sizeOfLocalColorTable != 32 &&
                    sizeOfLocalColorTable != 64 &&
                    sizeOfLocalColorTable != 128 &&
                    sizeOfLocalColorTable != 256) {
                    fatal(node,
                          "Bad value for LocalColorTable attribute sizeOfLocalColorTable!");
                }

                sortFlag = getBooleanAttribute(node, "sortFlag", false, true);

                localColorTable = getColorTable(node, "ColorTableEntry",
                                                true, sizeOfLocalColorTable);
            } else if (name.equals("GraphicControlExtension")) {
                String disposalMethodName =
                    getStringAttribute(node, "disposalMethod", null,
                                       true, disposalMethodNames);
                disposalMethod = 0;
                while(!disposalMethodName.equals(disposalMethodNames[disposalMethod])) {
                    disposalMethod++;
                }

                userInputFlag = getBooleanAttribute(node, "userInputFlag",
                                                    false, true);

                transparentColorFlag =
                    getBooleanAttribute(node, "transparentColorFlag",
                                        false, true);

                delayTime = getIntAttribute(node,
                                            "delayTime",
                                            -1, true,
                                            true, 0, 65535);

                transparentColorIndex =
                    getIntAttribute(node, "transparentColorIndex",
                                    -1, true,
                                    true, 0, 65535);
            } else if (name.equals("PlainTextExtension")) {
                hasPlainTextExtension = true;

                textGridLeft = getIntAttribute(node,
                                               "textGridLeft",
                                               -1, true,
                                               true, 0, 65535);

                textGridTop = getIntAttribute(node,
                                              "textGridTop",
                                              -1, true,
                                              true, 0, 65535);

                textGridWidth = getIntAttribute(node,
                                                "textGridWidth",
                                                -1, true,
                                                true, 1, 65535);

                textGridHeight = getIntAttribute(node,
                                                 "textGridHeight",
                                                 -1, true,
                                                 true, 1, 65535);

                characterCellWidth = getIntAttribute(node,
                                                     "characterCellWidth",
                                                     -1, true,
                                                     true, 1, 65535);

                characterCellHeight = getIntAttribute(node,
                                                      "characterCellHeight",
                                                      -1, true,
                                                      true, 1, 65535);

                textForegroundColor = getIntAttribute(node,
                                                      "textForegroundColor",
                                                      -1, true,
                                                      true, 0, 255);

                textBackgroundColor = getIntAttribute(node,
                                                      "textBackgroundColor",
                                                      -1, true,
                                                      true, 0, 255);

                // XXX The "text" attribute of the PlainTextExtension element
                // is not defined in the GIF image metadata format but it is
                // present in the GIFImageMetadata class. Consequently it is
                // used here but not required and with a default of "". See
                // bug 5082763.

                String textString =
                    getStringAttribute(node, "text", "", false, null);
                text = fromISO8859(textString);
            } else if (name.equals("ApplicationExtensions")) {
                IIOMetadataNode applicationExtension =
                    (IIOMetadataNode)node.getFirstChild();

                if (!applicationExtension.getNodeName().equals("ApplicationExtension")) {
                    fatal(node,
                          "Only a ApplicationExtension may be a child of a ApplicationExtensions!");
                }

                String applicationIDString =
                    getStringAttribute(applicationExtension, "applicationID",
                                       null, true, null);

                String authenticationCodeString =
                    getStringAttribute(applicationExtension, "authenticationCode",
                                       null, true, null);

                Object applicationExtensionData =
                    applicationExtension.getUserObject();
                if (applicationExtensionData == null ||
                    !(applicationExtensionData instanceof byte[])) {
                    fatal(applicationExtension,
                          "Bad user object in ApplicationExtension!");
                }

                if (applicationIDs == null) {
                    applicationIDs = new ArrayList();
                    authenticationCodes = new ArrayList();
                    applicationData = new ArrayList();
                }

                applicationIDs.add(fromISO8859(applicationIDString));
                authenticationCodes.add(fromISO8859(authenticationCodeString));
                applicationData.add(applicationExtensionData);
            } else if (name.equals("CommentExtensions")) {
                Node commentExtension = node.getFirstChild();
                if (commentExtension != null) {
                    while(commentExtension != null) {
                        if (!commentExtension.getNodeName().equals("CommentExtension")) {
                            fatal(node,
                                  "Only a CommentExtension may be a child of a CommentExtensions!");
                        }

                        if (comments == null) {
                            comments = new ArrayList();
                        }

                        String comment =
                            getStringAttribute(commentExtension, "value", null,
                                               true, null);

                        comments.add(fromISO8859(comment));

                        commentExtension = commentExtension.getNextSibling();
                    }
                }
            } else {
                fatal(node, "Unknown child of root node!");
            }

            node = node.getNextSibling();
        }
    }

    protected void mergeStandardTree(Node root)
      throws IIOInvalidTreeException {
        Node node = root;
        if (!node.getNodeName()
            .equals(IIOMetadataFormatImpl.standardMetadataFormatName)) {
            fatal(node, "Root must be " +
                  IIOMetadataFormatImpl.standardMetadataFormatName);
        }

        node = node.getFirstChild();
        while (node != null) {
            String name = node.getNodeName();

            if (name.equals("Chroma")) {
                Node childNode = node.getFirstChild();
                while(childNode != null) {
                    String childName = childNode.getNodeName();
                    if (childName.equals("Palette")) {
                        localColorTable = getColorTable(childNode,
                                                        "PaletteEntry",
                                                        false, -1);
                        break;
                    }
                    childNode = childNode.getNextSibling();
                }
            } else if (name.equals("Compression")) {
                Node childNode = node.getFirstChild();
                while(childNode != null) {
                    String childName = childNode.getNodeName();
                    if (childName.equals("NumProgressiveScans")) {
                        int numProgressiveScans =
                            getIntAttribute(childNode, "value", 4, false,
                                            true, 1, Integer.MAX_VALUE);
                        if (numProgressiveScans > 1) {
                            interlaceFlag = true;
                        }
                        break;
                    }
                    childNode = childNode.getNextSibling();
                }
            } else if (name.equals("Dimension")) {
                Node childNode = node.getFirstChild();
                while(childNode != null) {
                    String childName = childNode.getNodeName();
                    if (childName.equals("HorizontalPixelOffset")) {
                        imageLeftPosition = getIntAttribute(childNode,
                                                            "value",
                                                            -1, true,
                                                            true, 0, 65535);
                    } else if (childName.equals("VerticalPixelOffset")) {
                        imageTopPosition = getIntAttribute(childNode,
                                                           "value",
                                                           -1, true,
                                                           true, 0, 65535);
                    }
                    childNode = childNode.getNextSibling();
                }
            } else if (name.equals("Text")) {
                Node childNode = node.getFirstChild();
                while(childNode != null) {
                    String childName = childNode.getNodeName();
                    if (childName.equals("TextEntry") &&
                        getAttribute(childNode, "compression",
                                     "none", false).equals("none") &&
                        Charset.isSupported(getAttribute(childNode,
                                                         "encoding",
                                                         "ISO-8859-1",
                                                         false))) {
                        String value = getAttribute(childNode, "value");
                        byte[] comment = fromISO8859(value);
                        if (comments == null) {
                            comments = new ArrayList();
                        }
                        comments.add(comment);
                    }
                    childNode = childNode.getNextSibling();
                }
            } else if (name.equals("Transparency")) {
                Node childNode = node.getFirstChild();
                while(childNode != null) {
                    String childName = childNode.getNodeName();
                    if (childName.equals("TransparentIndex")) {
                        transparentColorIndex = getIntAttribute(childNode,
                                                                "value",
                                                                -1, true,
                                                                true, 0, 255);
                        transparentColorFlag = true;
                        break;
                    }
                    childNode = childNode.getNextSibling();
                }
            }

            node = node.getNextSibling();
        }
    }

    public void setFromTree(String formatName, Node root)
        throws IIOInvalidTreeException
    {
        reset();
        mergeTree(formatName, root);
    }
}
