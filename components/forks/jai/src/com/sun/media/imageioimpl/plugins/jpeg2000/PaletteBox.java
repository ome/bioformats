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
 * $RCSfile: PaletteBox.java,v $
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
 * $Date: 2005/02/11 05:01:36 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.jpeg2000;

import java.awt.image.IndexColorModel;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.media.imageioimpl.common.ImageUtil;

/** This class is designed to represent a palette box for JPEG 2000 JP2 file
 *  format.  A palette box has a length, and a fixed type of "pclr".
 *
 * Its content contains the number of palette entry, the number of color
 * components, the bit depths of the output components, the LUT.
 *
 * Currently, only 8-bit color index is supported.
 */
public class PaletteBox extends Box {
    /** The value of the data elements.
     */
    private int numEntries;
    private int numComps;
    private byte[] bitDepth;
    private byte[][] lut;

    /** Compute the length of this box. */
    private static int computeLength(IndexColorModel icm) {
        int size = icm.getMapSize();
        int[] comp = icm.getComponentSize();
        return 11 + comp.length + size * comp.length;
    }

    /** Gets the size of the components or the bit depth for all the color
     *  coomponents.
     */
    private static byte[] getCompSize(IndexColorModel icm) {
        int[] comp = icm.getComponentSize();
        int size = comp.length;
        byte[] buf = new byte[size];
        for (int i = 0; i < size; i++)
            buf[i] = (byte)(comp[i] - 1);
        return buf;
    }

    /** Gets the LUT from the <code>IndexColorModel</code> as an two-dimensional
     *  byte array.
     */
    private static byte[][] getLUT(IndexColorModel icm) {
        int[] comp = icm.getComponentSize();
        int size = icm.getMapSize();
        byte[][] lut = new byte[comp.length][size];
        icm.getReds(lut[0]);
        icm.getGreens(lut[1]);
        icm.getBlues(lut[2]);
        if (comp.length == 4)
            icm.getAlphas(lut[3]);
        return lut;
    }

    /** Constructs a <code>PlatteBox</code> from an
     *  <code>IndexColorModel</code>.
     */
    public PaletteBox(IndexColorModel icm) {
        this(computeLength(icm), getCompSize(icm), getLUT(icm));
    }

    /** Constructs a <code>PlatteBox</code> from an
     *  <code>org.w3c.dom.Node</code>.
     */
    public PaletteBox(Node node) throws IIOInvalidTreeException {
        super(node);
        byte[][] tlut = null;
        int index = 0;

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            String name = child.getNodeName();

            if ("NumberEntries".equals(name)) {
                numEntries = Box.getIntElementValue(child);
            }

            if ("NumberColors".equals(name)) {
                numComps = Box.getIntElementValue(child);
            }

            if ("BitDepth".equals(name)) {
                bitDepth = Box.getByteArrayElementValue(child);
            }

            if ("LUT".equals(name)) {
                tlut = new byte[numEntries][];

                NodeList children1 = child.getChildNodes();

                for (int j = 0; j <children1.getLength(); j++) {
                    Node child1 = children1.item(j);
                    name = child1.getNodeName();
                    if ("LUTRow".equals(name)) {
                        tlut[index++] = Box.getByteArrayElementValue(child1);
                    }
                }
            }
        }

        //XXX: currently only 8-bit LUT is supported so no decode is needed
        // For more refer to read palette box section.
        lut = new byte[numComps][numEntries];

        for (int i = 0; i < numComps; i++)
            for (int j = 0; j < numEntries; j++)
                lut[i][j] = tlut[j][i];

    }

    /** Constructs a <code>PlatteBox</code> from the provided length, bit
     *  depths of the color components and the LUT.
     */
    public PaletteBox(int length, byte[] comp, byte[][] lut) {
        super(length, 0x70636C72, null);
        this.bitDepth = comp;
        this.lut = lut;
        this.numEntries = lut[0].length;
        this.numComps = lut.length;
    }

    /** Constructs a <code>PlatteBox</code> from the provided byte array.
     */
    public PaletteBox(byte[] data) {
        super(8 + data.length, 0x70636C72, data);
    }

    /** Return the number of palette entries. */
    public int getNumEntries() {
        return numEntries;
    }

    /** Return the number of color components. */
    public int getNumComp() {
        return numComps;
    }

    /** Return the bit depths for all the color components. */
    public byte[] getBitDepths() {
        return bitDepth;
    }

    /** Return the LUT. */
    public byte[][] getLUT() {
        return lut;
    }

    /** creates an <code>IIOMetadataNode</code> from this palette box.
     *  The format of this node is defined in the XML dtd and xsd
     *  for the JP2 image file.
     */
    public IIOMetadataNode getNativeNode() {
        IIOMetadataNode node = new IIOMetadataNode(Box.getName(getType()));
        setDefaultAttributes(node);

        IIOMetadataNode child = new IIOMetadataNode("NumberEntries");
        child.setUserObject(new Integer(numEntries));
	child.setNodeValue("" + numEntries);
        node.appendChild(child);

        child = new IIOMetadataNode("NumberColors");
        child.setUserObject(new Integer(numComps));
	child.setNodeValue("" + numComps);
        node.appendChild(child);

        child = new IIOMetadataNode("BitDepth");
        child.setUserObject(bitDepth);
	child.setNodeValue(ImageUtil.convertObjectToString(bitDepth));
        node.appendChild(child);

        child = new IIOMetadataNode("LUT");
        for (int i = 0; i < numEntries; i++) {
            IIOMetadataNode child1 = new IIOMetadataNode("LUTRow");
            byte[] row = new byte[numComps];
            for (int j = 0; j < numComps; j++)
                row[j] = lut[j][i];

            child1.setUserObject(row);
	    child1.setNodeValue(ImageUtil.convertObjectToString(row));
            child.appendChild(child1);
        }
        node.appendChild(child);

        return node;
    }

    protected void parse(byte[] data) {
        if (data == null)
            return;
        numEntries = (short)(((data[0] & 0xFF) << 8) | (data[1] & 0xFF));

        numComps = data[2];
        bitDepth = new byte[numComps];
        System.arraycopy(data, 3, bitDepth, 0, numComps);

        lut = new byte[numComps][numEntries];
        for (int i = 0, k = 3 + numComps; i < numEntries; i++)
            for (int j = 0; j < numComps; j++)
                lut[j][i] = data[k++];
    }

    protected void compose() {
        if (data != null)
            return;
        data = new byte[3 + numComps + numEntries * numComps];
        data[0] = (byte)(numEntries >> 8);
        data[1] = (byte)(numEntries & 0xFF);

        data[2] = (byte)numComps;
        System.arraycopy(bitDepth, 0, data, 3, numComps);

        for (int i = 0, k = 3 + numComps; i < numEntries; i++)
            for (int j = 0; j < numComps; j++)
                data[k++] = lut[j][i];
    }
}
