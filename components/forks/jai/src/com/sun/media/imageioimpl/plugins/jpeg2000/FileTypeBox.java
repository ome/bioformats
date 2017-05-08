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
 * $RCSfile: FileTypeBox.java,v $
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

/** This class is defined to represent a File Type Box of JPEG JP2 file
 *  format.  A File Type Box has a length, and a fixed type of "ftyp".
 *
 * The content of a file type box contains the brand ("jp2 " for JP2 file",
 * the minor version (0 for JP2 file format), and a compatibility list (one of
 * which should be "jp2 " if brand is not "jp2 ".)
 */
public class FileTypeBox extends Box {
    /** Cache the element names for this box's xml definition */
    private static String[] elementNames = {"Brand",
                                            "MinorVersion",
                                            "CompatibilityList"};

    /** This method will be called by the getNativeNodeForSimpleBox of the
     *  class Box to get the element names.
     */
    public static String[] getElementNames() {
        return elementNames;
    }

    /** The element values. */
    private int brand;
    private int minorVersion;
    private int[] compatibility;

    /** Constructs a <code>FileTypeBox</code> from the provided brand, minor
     *  version and compatibility list.
     */
    public FileTypeBox(int br, int minorVersion, int[] comp) {
        super(16 + (comp == null ? 0 : (comp.length << 2)), 0x66747970, null);
        this.brand = br;
        this.minorVersion = minorVersion;
        this.compatibility = comp;
    }

    /** Constructs a <code>FileTypeBox</code> from the provided byte array.
     */
    public FileTypeBox(byte[] data) {
        super(8 + data.length, 0x66747970, data);
    }

    /** Constructs a <code>FileTypeBox</code> from
     *  <code>org.w3c.dom.Node</code>.
     */
    public FileTypeBox(Node node) throws IIOInvalidTreeException {
        super(node);
        NodeList children = node.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            String name = child.getNodeName();

            if ("Brand".equals(name)) {
                brand = Box.getIntElementValue(child);
            }

            if ("MinorVersion".equals(name)) {
                minorVersion = Box.getIntElementValue(child);
            }

            if ("CompatibilityList".equals(name)) {
                compatibility = Box.getIntArrayElementValue(child);
            }
        }
    }

    /** Returns the brand of this file type box. */
    public int getBrand() {
        return brand;
    }

    /** Returns the minor version of this file type box. */
    public int getMinorVersion() {
        return minorVersion;
    }

    /** Returns the compatibilty list of this file type box. */
    public int[] getCompatibilityList() {
        return compatibility;
    }

    /** Creates an <code>IIOMetadataNode</code> from this file type box.
     *  The format of this node is defined in the XML dtd and xsd
     *  for the JP2 image file.
     */
    public IIOMetadataNode getNativeNode() {
        return getNativeNodeForSimpleBox();
    }

    protected void parse(byte[] data) {
        if (data == null)
            return;
        brand = ((data[0] & 0xFF) << 24) | ((data[1] & 0xFF) << 16) |
                 ((data[2] & 0xFF) << 8) | (data[3] & 0xFF);

        minorVersion = ((data[4] & 0xFF) << 24) | ((data[5] & 0xFF) << 16) |
                 ((data[6] & 0xFF) << 8) | (data[7] & 0xFF);

        int len = (data.length - 8) / 4;
        if (len > 0) {
            compatibility = new int[len];
            for (int i = 0, j = 8; i < len; i++, j += 4)
                compatibility[i] = ((data[j] & 0xFF) << 24) |
                                   ((data[j+1] & 0xFF) << 16) |
                                   ((data[j+2] & 0xFF) << 8) |
                                   (data[j+3] & 0xFF);
        }
    }

    protected void compose() {
        if (data != null)
            return;
        data =
            new byte[8 +
                     (compatibility != null ? (compatibility.length << 2) : 0)];

        copyInt(data, 0, brand);
        copyInt(data, 4, minorVersion);
        if (compatibility != null)
            for (int i = 0, j = 8; i < compatibility.length; i++, j += 4)
                copyInt(data, j, compatibility[i]);
    }
}
