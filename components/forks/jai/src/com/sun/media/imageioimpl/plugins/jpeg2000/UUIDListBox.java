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
 * $RCSfile: UUIDListBox.java,v $
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
 * $Date: 2006/10/10 23:48:57 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.jpeg2000;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.media.imageioimpl.common.ImageUtil;

/** This class is defined to represent a UUID list Box of JPEG JP2
 *  file format.  This type of box has a length, a type of "ulst".  Its
 *  contents include the number of UUID entry and a list of 16-byte UUIDs.
 */
public class UUIDListBox extends Box {
    /** The data elements of this box. */
    private short num;
    private byte[][] uuids;

    /** Constructs a <code>UUIDListBox</code> from the provided uuid number
     *  and uuids.  The provided uuids should have a size of 16; otherwise,
     *  <code>Exception</code> may thrown in later the process.  The provided
     *  number should consistent with the size of the uuid array.
     */
    public UUIDListBox(short num, byte[][] uuids) {
        super(10 + (uuids.length << 4), 0x756c7374, null);
        this.num = num;
        this.uuids = uuids;
    }

    /** Constructs a <code>UUIDListBox</code> from the provided content
     *  data array.
     */
    public UUIDListBox(byte[] data) {
        super(8 + data.length, 0x756c7374, data);
    }

    /** Constructs a <code>UUIDListBox</code> based on the provided
     *  <code>org.w3c.dom.Node</code>.
     */
    public UUIDListBox(Node node) throws IIOInvalidTreeException {
        super(node);
        NodeList children = node.getChildNodes();
        int index = 0;

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            if ("NumberUUID".equals(child.getNodeName())) {
                num = (short)Box.getShortElementValue(child);
                uuids = new byte[num][];
            }

	}

	for (int i = 0; i < children.getLength(); i++) {
	    Node child = children.item(i);

            if ("UUID".equals(child.getNodeName()) && index < num) {
                uuids[index++] = Box.getByteArrayElementValue(child);
            }
        }
    }

    /** Parses the data elements from the provided content data array. */
    protected void parse(byte[] data) {
        num = (short)(((data[0] & 0xFF) << 8) | (data[1] & 0xFF));

        uuids = new byte[num][];
        int pos = 2;
        for (int i = 0; i < num; i++) {
            uuids[i] = new byte[16];
            System.arraycopy(data, pos, uuids[i], 0, 16);
            pos += 16;
        }
    }

    /** Creates an <code>IIOMetadataNode</code> from this UUID list
     *  box.  The format of this node is defined in the XML dtd and xsd
     *  for the JP2 image file.
     */
    public IIOMetadataNode getNativeNode() {
        IIOMetadataNode node = new IIOMetadataNode(Box.getName(getType()));
        setDefaultAttributes(node);

        IIOMetadataNode child = new IIOMetadataNode("NumberUUID");
        child.setUserObject(new Short(num));
	child.setNodeValue("" + num);
        node.appendChild(child);

        for (int i = 0; i < num; i++) {
            child = new IIOMetadataNode("UUID");
            child.setUserObject(uuids[i]);
	    child.setNodeValue(ImageUtil.convertObjectToString(uuids[i]));
            node.appendChild(child);
        }

        return node;
    }

    protected void compose() {
        if (data != null)
            return;
        data = new byte[2 + num * 16];

        data[0] = (byte)(num >> 8);
        data[1] = (byte)(num & 0xFF);

        for (int i = 0, pos = 2; i < num; i++) {
            System.arraycopy(uuids[i], 0, data, pos, 16);
            pos += 16;
        }
    }
}
