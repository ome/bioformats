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
 * $RCSfile: GIFWritableStreamMetadata.java,v $
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

/*
 * The source for this class was copied verbatim from the source for
 * package com.sun.imageio.plugins.gif.GIFImageMetadata and then modified
 * to make the class read-write capable.
 */

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import org.w3c.dom.Node;

class GIFWritableStreamMetadata extends GIFStreamMetadata {

    // package scope
    static final String
    NATIVE_FORMAT_NAME = "javax_imageio_gif_stream_1.0";

    public GIFWritableStreamMetadata() {
        super(true,
              NATIVE_FORMAT_NAME,
              "com.sun.media.imageioimpl.plugins.gif.GIFStreamMetadataFormat",
              null, null);

        // initialize metadata fields by default values
        reset();
    }

    public boolean isReadOnly() {
        return false;
    }

    public void mergeTree(String formatName, Node root)
      throws IIOInvalidTreeException {
        if (formatName.equals(nativeMetadataFormatName)) {
            if (root == null) {
                throw new IllegalArgumentException("root == null!");
            }
            mergeNativeTree(root);
        } else if (formatName.equals
                   (IIOMetadataFormatImpl.standardMetadataFormatName)) {
            if (root == null) {
                throw new IllegalArgumentException("root == null!");
            }
            mergeStandardTree(root);
        } else {
            throw new IllegalArgumentException("Not a recognized format!");
        }
    }

    public void reset() {
        version = null;

        logicalScreenWidth = UNDEFINED_INTEGER_VALUE;
        logicalScreenHeight = UNDEFINED_INTEGER_VALUE;
        colorResolution = UNDEFINED_INTEGER_VALUE;
        pixelAspectRatio = 0;

        backgroundColorIndex = 0;
        sortFlag = false;
        globalColorTable = null;
    }

    protected void mergeNativeTree(Node root) throws IIOInvalidTreeException {
        Node node = root;
        if (!node.getNodeName().equals(nativeMetadataFormatName)) {
            fatal(node, "Root must be " + nativeMetadataFormatName);
        }

        node = node.getFirstChild();
        while (node != null) {
            String name = node.getNodeName();

            if (name.equals("Version")) {
                version = getStringAttribute(node, "value", null,
                                             true, versionStrings);
            } else if (name.equals("LogicalScreenDescriptor")) {
                /* NB: At the moment we use empty strings to support undefined
                 * integer values in tree representation.
                 * We need to add better support for undefined/default values
                 * later.
                 */
                logicalScreenWidth = getIntAttribute(node,
                                                     "logicalScreenWidth",
                                                     UNDEFINED_INTEGER_VALUE,
                                                     true,
                                                     true, 1, 65535);

                logicalScreenHeight = getIntAttribute(node,
                                                      "logicalScreenHeight",
                                                      UNDEFINED_INTEGER_VALUE,
                                                      true,
                                                      true, 1, 65535);

                colorResolution = getIntAttribute(node,
                                                  "colorResolution",
                                                  UNDEFINED_INTEGER_VALUE,
                                                  true,
                                                  true, 1, 8);

                pixelAspectRatio = getIntAttribute(node,
                                                   "pixelAspectRatio",
                                                   0, true,
                                                   true, 0, 255);
            } else if (name.equals("GlobalColorTable")) {
                int sizeOfGlobalColorTable =
                    getIntAttribute(node, "sizeOfGlobalColorTable",
                                    true, 2, 256);
                if (sizeOfGlobalColorTable != 2 &&
                   sizeOfGlobalColorTable != 4 &&
                   sizeOfGlobalColorTable != 8 &&
                   sizeOfGlobalColorTable != 16 &&
                   sizeOfGlobalColorTable != 32 &&
                   sizeOfGlobalColorTable != 64 &&
                   sizeOfGlobalColorTable != 128 &&
                   sizeOfGlobalColorTable != 256) {
                    fatal(node,
                          "Bad value for GlobalColorTable attribute sizeOfGlobalColorTable!");
                }

                backgroundColorIndex = getIntAttribute(node,
                                                       "backgroundColorIndex",
                                                       0, true,
                                                       true, 0, 255);

                sortFlag = getBooleanAttribute(node, "sortFlag", false, true);

                globalColorTable = getColorTable(node, "ColorTableEntry",
                                                 true, sizeOfGlobalColorTable);
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
                        globalColorTable = getColorTable(childNode,
                                                         "PaletteEntry",
                                                         false, -1);

                    } else if (childName.equals("BackgroundIndex")) {
                        backgroundColorIndex = getIntAttribute(childNode,
                                                               "value",
                                                               -1, true,
                                                               true, 0, 255);
                    }
                    childNode = childNode.getNextSibling();
                }
            } else if (name.equals("Data")) {
                Node childNode = node.getFirstChild();
                while(childNode != null) {
                    String childName = childNode.getNodeName();
                    if (childName.equals("BitsPerSample")) {
                        colorResolution = getIntAttribute(childNode,
                                                          "value",
                                                          -1, true,
                                                          true, 1, 8);
                        break;
                    }
                    childNode = childNode.getNextSibling();
                }
            } else if (name.equals("Dimension")) {
                Node childNode = node.getFirstChild();
                while(childNode != null) {
                    String childName = childNode.getNodeName();
                    if (childName.equals("PixelAspectRatio")) {
                        float aspectRatio = getFloatAttribute(childNode,
                                                              "value");
                        if (aspectRatio == 1.0F) {
                            pixelAspectRatio = 0;
                        } else {
                            int ratio = (int)(aspectRatio*64.0F - 15.0F);
                            pixelAspectRatio =
                                Math.max(Math.min(ratio, 255), 0);
                        }
                    } else if (childName.equals("HorizontalScreenSize")) {
                        logicalScreenWidth = getIntAttribute(childNode,
                                                             "value",
                                                             -1, true,
                                                             true, 1, 65535);
                    } else if (childName.equals("VerticalScreenSize")) {
                        logicalScreenHeight = getIntAttribute(childNode,
                                                              "value",
                                                              -1, true,
                                                              true, 1, 65535);
                    }
                    childNode = childNode.getNextSibling();
                }
            } else if (name.equals("Document")) {
                Node childNode = node.getFirstChild();
                while(childNode != null) {
                    String childName = childNode.getNodeName();
                    if (childName.equals("FormatVersion")) {
                        String formatVersion =
                            getStringAttribute(childNode, "value", null,
                                               true, null);
                        for (int i = 0; i < versionStrings.length; i++) {
                            if (formatVersion.equals(versionStrings[i])) {
                                version = formatVersion;
                                break;
                            }
                        }
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
