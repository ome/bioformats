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
 * $RCSfile: ResolutionBox.java,v $
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
 * $Date: 2005/02/11 05:01:37 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.jpeg2000;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** This class is defined to represent a Resolution Box of JPEG JP2
 *  file format.  A Data Entry URL Box has a length, and a fixed type
 *  of "resc" (capture resolution) or "resd" (default display resolution).
 *
 * Its contens includes the resolution numerators, denominator, and the
 * exponents for both horizontal and vertical directions.
 */
public class ResolutionBox extends Box {
    /** The data elements in this box. */
    private short numV;
    private short numH;
    private short denomV;
    private short denomH;
    private byte expV;
    private byte expH;

    /** The cached horizontal/vertical resolutions. */
    private float hRes;
    private float vRes;

    /** Constructs a <code>ResolutionBox</code> from the provided type and
     *  content data array.
     */
    public ResolutionBox(int type, byte[] data) {
        super(18, type, data);
    }

    /** Constructs a <code>ResolutionBox</code> from the provided type and
     *  horizontal/vertical resolutions.
     */
    public ResolutionBox(int type, float hRes, float vRes) {
        super(18, type, null);
        this.hRes = hRes;
        this.vRes = vRes;
        denomH = denomV = 1;

        expV = 0;
        if (vRes >= 32768) {
            int temp = (int)vRes;
            while (temp >= 32768) {
                expV++;
                temp /= 10;
            }
            numV = (short)(temp & 0xFFFF);
        } else {
            numV = (short)vRes;
        }

        expH = 0;
        if (hRes >= 32768) {
            int temp = (int)hRes;
            while (temp >= 32768) {
                expH++;
                temp /= 10;
            }
            numH = (short)(temp & 0xFFFF);
        } else {
            numH = (short)hRes;
        }
    }

    /** Constructs a <code>ResolutionBox</code> based on the provided
     *  <code>org.w3c.dom.Node</code>.
     */
    public ResolutionBox(Node node) throws IIOInvalidTreeException {
        super(node);
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            String name = child.getNodeName();

            if ("VerticalResolutionNumerator".equals(name)) {
                numV = Box.getShortElementValue(child);
            }

            if ("VerticalResolutionDenominator".equals(name)) {
                denomV = Box.getShortElementValue(child);
            }

            if ("HorizontalResolutionNumerator".equals(name)) {
                numH = Box.getShortElementValue(child);
            }

            if ("HorizontalResolutionDenominator".equals(name)) {
                denomH = Box.getShortElementValue(child);
            }

            if ("VerticalResolutionExponent".equals(name)) {
                expV = Box.getByteElementValue(child);
            }

            if ("HorizontalResolutionExponent".equals(name)) {
                expH = Box.getByteElementValue(child);
            }
        }
    }

    /** Return the horizontal resolution. */
    public float getHorizontalResolution() {
        return hRes;
    }

    /** Return the vertical resolution. */
    public float getVerticalResolution() {
        return vRes;
    }

    /** Parse the data elements from the provided content data array. */
    protected void parse(byte[] data) {
        numV = (short)(((data[0] & 0xFF) << 8) | (data[1] & 0xFF));
        denomV = (short)(((data[2] & 0xFF) << 8) | (data[3] & 0xFF));
        numH = (short)(((data[4] & 0xFF) << 8) | (data[5] & 0xFF));
        denomH = (short)(((data[6] & 0xFF) << 8) | (data[7] & 0xFF));
        expV = data[8];
        expH = data[9];
        vRes = (float)((numV & 0xFFFF) * Math.pow(10, expV) / (denomV & 0xFFFF));
        hRes = (float)((numH & 0xFFFF)* Math.pow(10, expH) / (denomH & 0xFFFF));
    }

    /** Creates an <code>IIOMetadataNode</code> from this resolution
     *  box.  The format of this node is defined in the XML dtd and xsd
     *  for the JP2 image file.
     */
    public IIOMetadataNode getNativeNode() {
        IIOMetadataNode node = new IIOMetadataNode(Box.getName(getType()));
        setDefaultAttributes(node);

        IIOMetadataNode child = new IIOMetadataNode("VerticalResolutionNumerator");
        child.setUserObject(new Short(numV));
	child.setNodeValue("" + numV);
        node.appendChild(child);

        child = new IIOMetadataNode("VerticalResolutionDenominator");
        child.setUserObject(new Short(denomV));
	child.setNodeValue("" + denomV);
        node.appendChild(child);

        child = new IIOMetadataNode("HorizontalResolutionNumerator");
        child.setUserObject(new Short(numH));
	child.setNodeValue("" + numH);
        node.appendChild(child);

        child = new IIOMetadataNode("HorizontalResolutionDenominator");
        child.setUserObject(new Short(denomH));
	child.setNodeValue("" + denomH);
        node.appendChild(child);

        child = new IIOMetadataNode("VerticalResolutionExponent");
        child.setUserObject(new Byte(expV));
	child.setNodeValue("" + expV);
        node.appendChild(child);

        child = new IIOMetadataNode("HorizontalResolutionExponent");
        child.setUserObject(new Byte(expH));
	child.setNodeValue("" + expH);
        node.appendChild(child);

        return node;
    }

    protected void compose() {
        if (data != null)
            return;
        data = new byte[10];
        data[0] = (byte)(numV >> 8);
        data[1] = (byte)(numV & 0xFF);
        data[2] = (byte)(denomV >> 8);
        data[3] = (byte)(denomV & 0xFF);

        data[4] = (byte)(numH >> 8);
        data[5] = (byte)(numH & 0xFF);
        data[6] = (byte)(denomH >> 8);
        data[7] = (byte)(denomH & 0xFF);

        data[8] = expV;
        data[9] = expH;
    }
}
