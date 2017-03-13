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
 * $RCSfile: PCXMetadata.java,v $
 *
 * 
 * Copyright (c) 2007 Sun Microsystems, Inc. All  Rights Reserved.
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
 * $Date: 2007/09/07 19:12:25 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.pcx;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.sun.media.imageioimpl.common.ImageUtil;

public class PCXMetadata extends IIOMetadata implements Cloneable, PCXConstants {

    short version;
    byte bitsPerPixel;
    boolean gotxmin, gotymin;
    short xmin, ymin;
    int vdpi, hdpi;
    int hsize,vsize;

    PCXMetadata() {
        super(true, null, null, null, null);
        reset();
    }

    public Node getAsTree(String formatName) {
        if (formatName.equals(IIOMetadataFormatImpl.standardMetadataFormatName)) {
            return getStandardTree();
        } else {
            throw new IllegalArgumentException("Not a recognized format!");
        }
    }

    public boolean isReadOnly() {
	return false;
    }

    public void mergeTree(String formatName, Node root) throws IIOInvalidTreeException {
        if (formatName.equals(IIOMetadataFormatImpl.standardMetadataFormatName)) {
            if (root == null) {
                throw new IllegalArgumentException("root == null!");
            }
            mergeStandardTree(root);
        } else {
            throw new IllegalArgumentException("Not a recognized format!");
        }   
    }

    public void reset() {
        version = VERSION_3_0;
        bitsPerPixel = 0;
        gotxmin = false;
        gotymin = false;
        xmin = 0;
        ymin = 0;
        vdpi = 72;
        hdpi = 72;
        hsize = 0;
        vsize = 0;
    }

    public IIOMetadataNode getStandardDocumentNode() {
        String versionString;
        switch(version) {
        case VERSION_2_5:
            versionString = "2.5";
            break;
        case VERSION_2_8_W_PALETTE:
            versionString = "2.8 with palette";
            break;
        case VERSION_2_8_WO_PALETTE:
            versionString = "2.8 without palette";
            break;
        case VERSION_PC_WINDOWS:
            versionString = "PC Paintbrush for Windows";
            break;
        case VERSION_3_0:
            versionString = "3.0";
            break;
        default:
            // unknown
            versionString = null;
        }

        IIOMetadataNode documentNode = null;
        if(versionString != null) {
            documentNode = new IIOMetadataNode("Document");
            IIOMetadataNode node = new IIOMetadataNode("FormatVersion");
            node.setAttribute("value", versionString);
            documentNode.appendChild(node);
        }

        return documentNode;
    }

    public IIOMetadataNode getStandardDimensionNode() {
        IIOMetadataNode dimensionNode = new IIOMetadataNode("Dimension");
        IIOMetadataNode node = null; // scratch node

        node = new IIOMetadataNode("HorizontalPixelOffset");
        node.setAttribute("value", String.valueOf(xmin));
        dimensionNode.appendChild(node);

        node = new IIOMetadataNode("VerticalPixelOffset");
        node.setAttribute("value", String.valueOf(ymin));
        dimensionNode.appendChild(node);

        node = new IIOMetadataNode("HorizontalPixelSize");
        node.setAttribute("value", String.valueOf(254.0/hdpi));
        dimensionNode.appendChild(node);

        node = new IIOMetadataNode("VerticalPixelSize");
        node.setAttribute("value", String.valueOf(254.0/vdpi));
        dimensionNode.appendChild(node);

        if(hsize != 0) {
            node = new IIOMetadataNode("HorizontalScreenSize");
            node.setAttribute("value", String.valueOf(hsize));
            dimensionNode.appendChild(node);
        }

        if(vsize != 0) {
            node = new IIOMetadataNode("VerticalScreenSize");
            node.setAttribute("value", String.valueOf(vsize));
            dimensionNode.appendChild(node);
        }

        return dimensionNode;
    }

    private void mergeStandardTree(Node root) throws IIOInvalidTreeException {
        Node node = root;
        if (!node.getNodeName().equals(IIOMetadataFormatImpl.standardMetadataFormatName))
            throw new IIOInvalidTreeException("Root must be " +
                                              IIOMetadataFormatImpl.standardMetadataFormatName,
                                              node);

        node = node.getFirstChild();
        while (node != null) {
            String name = node.getNodeName();

            if (name.equals("Dimension")) {
                Node child = node.getFirstChild();

                while (child != null) {
                    String childName = child.getNodeName();
                    if (childName.equals("HorizontalPixelOffset")) {
                        String hpo = getAttribute(child, "value");
                        xmin = Short.valueOf(hpo).shortValue();
                        gotxmin = true;
                    } else if (childName.equals("VerticalPixelOffset")) {
                        String vpo = getAttribute(child, "value");
                        ymin = Short.valueOf(vpo).shortValue();
                        gotymin = true;
                    } else if (childName.equals("HorizontalPixelSize")) {
                        String hps = getAttribute(child, "value");
                        hdpi = (int)(254.0F/Float.parseFloat(hps) + 0.5F);
                    } else if (childName.equals("VerticalPixelSize")) {
                        String vps = getAttribute(child, "value");
                        vdpi = (int)(254.0F/Float.parseFloat(vps) + 0.5F);
                    } else if (childName.equals("HorizontalScreenSize")) {
                        String hss = getAttribute(child, "value");
                        hsize = Integer.valueOf(hss).intValue();
                    } else if (childName.equals("VerticalScreenSize")) {
                        String vss = getAttribute(child, "value");
                        vsize = Integer.valueOf(vss).intValue();
                    }

                    child = child.getNextSibling();
                }
            }

            node = node.getNextSibling();
        }
    }

    private static String getAttribute(Node node, String attrName) {
        NamedNodeMap attrs = node.getAttributes();
        Node attr = attrs.getNamedItem(attrName);
        return attr != null ? attr.getNodeValue() : null;
    }
}
