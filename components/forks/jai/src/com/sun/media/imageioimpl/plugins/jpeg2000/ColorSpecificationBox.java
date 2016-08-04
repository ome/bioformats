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
 * $RCSfile: ColorSpecificationBox.java,v $
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

import java.awt.color.ICC_Profile;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** This class is defined to represent a Color Specification Box of JPEG JP2
 *  file format.  A Channel Definition Box has a length, and a fixed type
 *  of "colr".  Its content contains the method to define the color space,
 *  the precedence and approximation accuracy (0 for JP2 files), the
 *  enumerated color space, and the ICC color profile if any.
 */
public class ColorSpecificationBox extends Box {
    /** The enumerated color space defined in JP2 file format. */
    public static final int ECS_sRGB = 16;
    public static final int ECS_GRAY = 17;
    public static final int ECS_YCC = 18;

    /** Cache the element names for this box's xml definition */
    private static String[] elementNames = {"Method", "Precedence",
                                            "ApproximationAccuracy",
                                            "EnumeratedColorSpace",
                                            "ICCProfile"};

    /** This method will be called by the getNativeNodeForSimpleBox of the
     *  class Box to get the element names.
     */
    public static String[] getElementNames() {
        return elementNames;
    }

    /** The elements' values. */
    private byte method;
    private byte precedence;
    private byte approximation;
    private int ecs;
    private ICC_Profile profile;

    /** Computes the length of this box when profile is present. */
    private static int computeLength(byte m, ICC_Profile profile) {
        int ret = 15;
        if (m == 2 && profile != null) {
            ret += profile.getData().length;
        }
        return ret;
    }

    /** Creates a <code>ColorSpecificationBox</code> from the provided data
     *  elements.
     */
    public ColorSpecificationBox(byte m, byte p, byte a, int ecs,
                                 ICC_Profile profile) {
        super(computeLength(m, profile), 0x636F6C72, null);
        this.method = m;
        this.precedence = p;
        this.approximation = a;
        this.ecs = ecs;
        this.profile = profile;
    }

    /** Creates a <code>ColorSpecificationBox</code> from the provided byte
     *  array.
     */
    public ColorSpecificationBox(byte[] data) {
        super(8 + data.length, 0x636F6C72, data);
    }

    /** Constructs a <code>ColorSpecificationBox</code> based on the provided
     *  <code>org.w3c.dom.Node</code>.
     */
    public ColorSpecificationBox(Node node) throws IIOInvalidTreeException {
        super(node);
        NodeList children = node.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            String name = child.getNodeName();

            if ("Method".equals(name)) {
                method = Box.getByteElementValue(child);
            }

            if ("Precedence".equals(name)) {
                precedence = Box.getByteElementValue(child);
            }

            if ("ApproximationAccuracy".equals(name)) {
                approximation = Box.getByteElementValue(child);
            }

            if ("EnumeratedColorSpace".equals(name)) {
                ecs = Box.getIntElementValue(child);
            }

            if ("ICCProfile".equals(name)) {
                if (child instanceof IIOMetadataNode)
                    profile =
                        (ICC_Profile)((IIOMetadataNode)child).getUserObject();
		else {
		    String value = node.getNodeValue();
		    if (value != null)
			profile = ICC_Profile.getInstance(Box.parseByteArray(value));
		}
            }
        }
    }

    /** Returns the method to define the color space. */
    public byte getMethod() {
        return method;
    }

    /** Returns <code>Precedence</code>. */
    public byte getPrecedence() {
        return precedence;
    }

    /** Returns <code>ApproximationAccuracy</code>. */
    public byte getApproximationAccuracy() {
        return approximation;
    }

    /** Returns the enumerated color space. */
    public int getEnumeratedColorSpace() {
        return ecs;
    }

    /** Returns the ICC color profile in this color specification box. */
    public ICC_Profile getICCProfile() {
        return profile;
    }

    /** Creates an <code>IIOMetadataNode</code> from this color specification
     *  box.  The format of this node is defined in the XML dtd and xsd
     *  for the JP2 image file.
     */
    public IIOMetadataNode getNativeNode() {
        return getNativeNodeForSimpleBox();
    }

    protected void parse(byte[] data) {
        method = data[0];
        precedence = data[1];
        approximation = data[2];
        if (method == 2) {
            byte[] proData = new byte[data.length - 3];
            System.arraycopy(data, 3, proData, 0, data.length - 3);
            profile = ICC_Profile.getInstance(proData);
        } else
            ecs = ((data[3] & 0xFF) << 24) | ((data[4] & 0xFF) << 16) |
                  ((data[5] & 0xFF) << 8) | (data[6] & 0xFF);

    }

    protected void compose() {
        if (data != null)
            return;
        int len = 7;
        byte[] profileData = null;
        if (profile != null) {
            profileData = profile.getData();
            len += profileData.length;
        }

        data = new byte[len];

        data[0] = (byte)method;
        data[1] = (byte)precedence;
        data[2] = (byte)approximation;

        copyInt(data, 3, ecs);

        if (profile != null)
            System.arraycopy(profileData, 0, data, 7, len - 7);
    }
}
