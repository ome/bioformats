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
 * $RCSfile: HeaderBox.java,v $
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
 * $Date: 2005/02/11 05:01:32 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.jpeg2000;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** This class is defined to represent an Image Header Box of JPEG JP2 file
 *  format.  An Image Header Box has a length, and a fixed type of "ihdr".
 *
 * The content of an image header box contains the width/height, number of
 * image components, the bit depth (coded with sign/unsign information),
 * the compression type (7 for JP2 file), the flag to indicate the color
 * space is known or not, and a flag to indicate whether the intellectual
 * property information included in this file.
 */
public class HeaderBox extends Box {
    /** Cache the element names for this box's xml definition */
    private static String[] elementNames = {"Height",
                                            "Width",
                                            "NumComponents",
                                            "BitDepth",
                                            "CompressionType",
                                            "UnknownColorspace",
                                            "IntellectualProperty"};

    /** This method will be called by the getNativeNodeForSimpleBox of the
     *  class Box to get the element names.
     */
    public static String[] getElementNames() {
        return elementNames;
    }

    /** The element values. */
    private int width;
    private int height;
    private short numComp;
    private byte bitDepth;
    private byte compressionType;
    private byte unknownColor;
    private byte intelProp;

    /** Create an Image Header Box from the element values. */
    public HeaderBox(int height, int width, int numComp, int bitDepth,
                     int compressionType, int unknownColor, int intelProp) {
        super(22, 0x69686472, null);
        this.height = height;
        this.width = width;
        this.numComp = (short)numComp;
        this.bitDepth = (byte)bitDepth;
        this.compressionType = (byte)compressionType;
        this.unknownColor = (byte)unknownColor;
        this.intelProp = (byte)intelProp;
    }

    /** Create an Image Header Box using the content data. */
    public HeaderBox(byte[] data) {
        super(8 + data.length, 0x69686472, data);
    }

    /** Constructs an Image Header Box from a Node. */
    public HeaderBox(Node node) throws IIOInvalidTreeException {
        super(node);
        NodeList children = node.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            String name = child.getNodeName();

            if ("Height".equals(name)) {
                height = Box.getIntElementValue(child);
            }

            if ("Width".equals(name)) {
                width = Box.getIntElementValue(child);
            }

            if ("NumComponents".equals(name)) {
                numComp = Box.getShortElementValue(child);
            }

            if ("BitDepth".equals(name)) {
                bitDepth = Box.getByteElementValue(child);
            }

            if ("CompressionType".equals(name)) {
                compressionType = Box.getByteElementValue(child);
            }

            if ("UnknownColorspace".equals(name)) {
                unknownColor = Box.getByteElementValue(child);
            }

            if ("IntellectualProperty".equals(name)) {
                intelProp = Box.getByteElementValue(child);
            }
        }
    }

    /** Parse the data elements from the byte array of the content. */
    protected void parse(byte[] data) {
        height = ((data[0] & 0xFF) << 24) |
                 ((data[1] & 0xFF) << 16) |
                 ((data[2] & 0xFF) << 8) |
                 (data[3]& 0xFF);
        width = ((data[4] & 0xFF) << 24) |
                ((data[5] & 0xFF) << 16) |
                ((data[6] & 0xFF) << 8) |
                (data[7] & 0xFF);
        numComp = (short)(((data[8] & 0xFF) << 8) | (data[9] & 0xFF));
        bitDepth = data[10];
        compressionType = data[11];
        unknownColor = data[12];
        intelProp = data[13];
    }

    /** Returns the height of the image. */
    public int getHeight() {
        return height;
    }

    /** Returns the width of the image. */
    public int getWidth() {
        return width;
    }

    /** Returns the number of image components. */
    public short getNumComponents() {
        return numComp;
    }

    /** Returns the compression type. */
    public byte getCompressionType() {
        return compressionType;
    }

    /** Returns the bit depth for all the image components. */
    public byte getBitDepth() {
        return bitDepth;
    }

    /** Returns the <code>UnknowColorspace</code> flag. */
    public byte getUnknownColorspace() {
        return unknownColor;
    }

    /** Returns the <code>IntellectualProperty</code> flag. */
    public byte getIntellectualProperty() {
        return intelProp;
    }

    /** Creates an <code>IIOMetadataNode</code> from this image header box.
     *  The format of this node is defined in the XML dtd and xsd
     *  for the JP2 image file.
     */
    public IIOMetadataNode getNativeNode() {
        return getNativeNodeForSimpleBox();
    }

    protected void compose() {
        if (data != null)
            return;
        data = new byte[14];
        copyInt(data, 0, height);
        copyInt(data, 4, width);

        data[8] = (byte)(numComp >> 8);
        data[9] = (byte)(numComp & 0xFF);
        data[10] = bitDepth;
        data[11] = compressionType;
        data[12] = unknownColor;
        data[13] = intelProp;
    }
}
