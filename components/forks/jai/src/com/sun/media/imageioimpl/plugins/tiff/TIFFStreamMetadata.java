/*
 * $RCSfile: TIFFStreamMetadata.java,v $
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
 * $Date: 2005/02/11 05:01:50 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.tiff;

import java.nio.ByteOrder;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.metadata.IIOInvalidTreeException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class TIFFStreamMetadata extends IIOMetadata {

    // package scope
    static final String nativeMetadataFormatName =
        "com_sun_media_imageio_plugins_tiff_stream_1.0";

    static final String nativeMetadataFormatClassName =
        "com.sun.media.imageioimpl.plugins.tiff.TIFFStreamMetadataFormat";

    private static final String bigEndianString =
        ByteOrder.BIG_ENDIAN.toString();
    private static final String littleEndianString =
        ByteOrder.LITTLE_ENDIAN.toString();

    public ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

    public TIFFStreamMetadata() {
        super(false,
              nativeMetadataFormatName,
              nativeMetadataFormatClassName,
              null, null);
    }

    public boolean isReadOnly() {
        return false;
    }

    // Shorthand for throwing an IIOInvalidTreeException
    private static void fatal(Node node, String reason)
        throws IIOInvalidTreeException {
        throw new IIOInvalidTreeException(reason, node);
    }

    public Node getAsTree(String formatName) {
        IIOMetadataNode root = new IIOMetadataNode(nativeMetadataFormatName);

        IIOMetadataNode byteOrderNode = new IIOMetadataNode("ByteOrder");
        byteOrderNode.setAttribute("value", byteOrder.toString());

        root.appendChild(byteOrderNode);
        return root;
    }

//     public void setFromTree(String formatName, Node root) {
//     }

    private void mergeNativeTree(Node root) throws IIOInvalidTreeException {
        Node node = root;
        if (!node.getNodeName().equals(nativeMetadataFormatName)) {
            fatal(node, "Root must be " + nativeMetadataFormatName);
        }
        
        node = node.getFirstChild();
        if (node == null || !node.getNodeName().equals("ByteOrder")) {
            fatal(node, "Root must have \"ByteOrder\" child");
        } 

        NamedNodeMap attrs = node.getAttributes();
        String order = (String)attrs.getNamedItem("value").getNodeValue();

        if (order == null) {
            fatal(node, "ByteOrder node must have a \"value\" attribute");
        }
        if (order.equals(bigEndianString)) {
            this.byteOrder = ByteOrder.BIG_ENDIAN;
        } else if (order.equals(littleEndianString)) {
            this.byteOrder = ByteOrder.LITTLE_ENDIAN;
        } else {
            fatal(node, "Incorrect value for ByteOrder \"value\" attribute");
        }
    }

    public void mergeTree(String formatName, Node root)
        throws IIOInvalidTreeException {
        if (formatName.equals(nativeMetadataFormatName)) {
            if (root == null) {
                throw new IllegalArgumentException("root == null!");
            }
            mergeNativeTree(root);
        } else {
            throw new IllegalArgumentException("Not a recognized format!");
        }
    }

    public void reset() {
        this.byteOrder = ByteOrder.BIG_ENDIAN;
    }
}
