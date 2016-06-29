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
 * $RCSfile: BMPMetadata.java,v $
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
 * $Date: 2006/04/21 23:14:37 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.bmp;

import java.io.UnsupportedEncodingException;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.SampleModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import org.w3c.dom.Node;
import com.sun.media.imageio.plugins.bmp.BMPImageWriteParam;
import com.sun.media.imageioimpl.common.ImageUtil;

public class BMPMetadata extends IIOMetadata
    implements Cloneable, BMPConstants {

    public static final String nativeMetadataFormatName =
        "com_sun_media_imageio_plugins_bmp_image_1.0";

    // Fields for Image Descriptor
    public String bmpVersion;
    public int width ;
    public int height;
    public short bitsPerPixel;
    public int compression;
    public int imageSize;

    // Fields for PixelsPerMeter
    public int xPixelsPerMeter;
    public int yPixelsPerMeter;

    public int colorsUsed;
    public int colorsImportant;

    // Fields for BI_BITFIELDS compression(Mask)
    public int redMask;
    public int greenMask;
    public int blueMask;
    public int alphaMask;

    public int colorSpace;

    // Fields for CIE XYZ for the LCS_CALIBRATED_RGB color space
    public double redX;
    public double redY;
    public double redZ;
    public double greenX;
    public double greenY;
    public double greenZ;
    public double blueX;
    public double blueY;
    public double blueZ;

    // Fields for Gamma values for the LCS_CALIBRATED_RGB color space
    public int gammaRed;
    public int gammaGreen;
    public int gammaBlue;

    public int intent;

    // Fields for the Palette and Entries
    public byte[] palette = null;
    public int paletteSize;
    public int red;
    public int green;
    public int blue;

    // Fields from CommentExtension
    // List of String
    public List comments = null; // new ArrayList();

    public BMPMetadata() {
        super(true,
              nativeMetadataFormatName,
              "com.sun.media.imageioimpl.bmp.BMPMetadataFormat",
              null, null);
    }

    public BMPMetadata(IIOMetadata metadata)
        throws IIOInvalidTreeException {

        this();

        if(metadata != null) {
            List formats = Arrays.asList(metadata.getMetadataFormatNames());

            if(formats.contains(nativeMetadataFormatName)) {
                // Initialize from native image metadata format.
                setFromTree(nativeMetadataFormatName,
                            metadata.getAsTree(nativeMetadataFormatName));
            } else if(metadata.isStandardMetadataFormatSupported()) {
                // Initialize from standard metadata form of the input tree.
                String format =
                    IIOMetadataFormatImpl.standardMetadataFormatName;
                setFromTree(format, metadata.getAsTree(format));
            }
        }
    }

    public boolean isReadOnly() {
        return false;
    }

    public Object clone() {
        BMPMetadata metadata;
        try {
            metadata = (BMPMetadata)super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
        
        return metadata;
    }

    public Node getAsTree(String formatName) {
        if (formatName.equals(nativeMetadataFormatName)) {
            return getNativeTree();
        } else if (formatName.equals
                   (IIOMetadataFormatImpl.standardMetadataFormatName)) {
            return getStandardTree();
        } else {
            throw new IllegalArgumentException(I18N.getString("BMPMetadata0"));
        }
    }

    private Node getNativeTree() {
        IIOMetadataNode root =
            new IIOMetadataNode(nativeMetadataFormatName);

        addChildNode(root, "BMPVersion", bmpVersion);
        addChildNode(root, "Width", new Integer(width));
        addChildNode(root, "Height", new Integer(height));
        addChildNode(root, "BitsPerPixel", new Short(bitsPerPixel));
        addChildNode(root, "Compression", new Integer(compression));
        addChildNode(root, "ImageSize", new Integer(imageSize));

        IIOMetadataNode node;
        if(xPixelsPerMeter > 0 && yPixelsPerMeter > 0) {
            node = addChildNode(root, "PixelsPerMeter", null);
            addChildNode(node, "X", new Integer(xPixelsPerMeter));
            addChildNode(node, "Y", new Integer(yPixelsPerMeter));
        }

        addChildNode(root, "ColorsUsed", new Integer(colorsUsed));
        addChildNode(root, "ColorsImportant", new Integer(colorsImportant));

        int version = 0;
        for (int i = 0; i < bmpVersion.length(); i++)
            if (Character.isDigit(bmpVersion.charAt(i)))
                version = bmpVersion.charAt(i) -'0';

        if (version >= 4) {
            node = addChildNode(root, "Mask", null);
            addChildNode(node, "Red", new Integer(redMask));
            addChildNode(node, "Green", new Integer(greenMask));
            addChildNode(node, "Blue", new Integer(blueMask));
            addChildNode(node, "Alpha", new Integer(alphaMask));

            addChildNode(root, "ColorSpaceType", new Integer(colorSpace));

            node = addChildNode(root, "CIEXYZEndpoints", null);
            addXYZPoints(node, "Red", redX, redY, redZ);
            addXYZPoints(node, "Green", greenX, greenY, greenZ);
            addXYZPoints(node, "Blue", blueX, blueY, blueZ);

            node = addChildNode(root, "Gamma", null);
            addChildNode(node, "Red", new Integer(gammaRed));
            addChildNode(node, "Green", new Integer(gammaGreen));
            addChildNode(node, "Blue", new Integer(gammaBlue));

            node = addChildNode(root, "Intent", new Integer(intent));
        }

        // Palette
        if ((palette != null) && (paletteSize > 0)) {
            node = addChildNode(root, "Palette", null);
            boolean isVersion2 =
                bmpVersion != null && bmpVersion.equals(VERSION_2);

            for (int i = 0, j = 0; i < paletteSize; i++) {
                IIOMetadataNode entry =
                    addChildNode(node, "PaletteEntry", null);
                blue = palette[j++] & 0xff;
                green = palette[j++] & 0xff;
                red = palette[j++] & 0xff;
                addChildNode(entry, "Red", new Integer(red));
                addChildNode(entry, "Green", new Integer(green));
                addChildNode(entry, "Blue", new Integer(blue));
                if(!isVersion2) j++; // skip reserved entry
            }
        }

        return root;
    }

    // Standard tree node methods
    protected IIOMetadataNode getStandardChromaNode() {

        IIOMetadataNode node = new IIOMetadataNode("Chroma");

        IIOMetadataNode subNode = new IIOMetadataNode("ColorSpaceType");
        String colorSpaceType;
        if (((palette != null) && (paletteSize > 0)) ||
            (redMask != 0 || greenMask != 0 || blueMask != 0) ||
            bitsPerPixel > 8) {
            colorSpaceType = "RGB";
        } else {
            colorSpaceType = "GRAY";
        }
        subNode.setAttribute("name", colorSpaceType);
        node.appendChild(subNode);

        subNode = new IIOMetadataNode("NumChannels");
        String numChannels;
        if (((palette != null) && (paletteSize > 0)) ||
            (redMask != 0 || greenMask != 0 || blueMask != 0) ||
            bitsPerPixel > 8) {
            if(alphaMask != 0) {
                numChannels = "4";
            } else {
                numChannels = "3";
            }
        } else {
            numChannels = "1";
        }
        subNode.setAttribute("value", numChannels);
        node.appendChild(subNode);

        if(gammaRed != 0 && gammaGreen != 0 && gammaBlue != 0) {
            subNode = new IIOMetadataNode("Gamma");
            Double gamma = new Double((gammaRed+gammaGreen+gammaBlue)/3.0);
            subNode.setAttribute("value", gamma.toString());
            node.appendChild(subNode);
        }

        if(numChannels.equals("1") &&
           (palette == null || paletteSize == 0)) {
            subNode = new IIOMetadataNode("BlackIsZero");
            subNode.setAttribute("value", "TRUE");
            node.appendChild(subNode);
        }

        if ((palette != null) && (paletteSize > 0)) {
            subNode = new IIOMetadataNode("Palette");
            boolean isVersion2 =
                bmpVersion != null && bmpVersion.equals(VERSION_2);

            for (int i = 0, j = 0; i < paletteSize; i++) {
                IIOMetadataNode subNode1 =
                    new IIOMetadataNode("PaletteEntry");
                subNode1.setAttribute("index", ""+i);
                subNode1.setAttribute("blue", "" + (palette[j++]&0xff));
                subNode1.setAttribute("green", "" + (palette[j++]&0xff));
                subNode1.setAttribute("red", "" + (palette[j++]&0xff));
                if(!isVersion2) j++; // skip reserved entry
                subNode.appendChild(subNode1);
            }
            node.appendChild(subNode);
        }

        return node;
    }

    protected IIOMetadataNode getStandardCompressionNode() {
        IIOMetadataNode node = new IIOMetadataNode("Compression");

        // CompressionTypeName
        IIOMetadataNode subNode = new IIOMetadataNode("CompressionTypeName");
        subNode.setAttribute("value", compressionTypeNames[compression]);
        node.appendChild(subNode);

        subNode = new IIOMetadataNode("Lossless");
        subNode.setAttribute("value",
                             compression == BI_JPEG ? "FALSE" : "TRUE");
        node.appendChild(subNode);

        return node;
    }

    protected IIOMetadataNode getStandardDataNode() {
        IIOMetadataNode node = new IIOMetadataNode("Data");

        String sampleFormat = (palette != null) && (paletteSize > 0) ?
            "Index" : "UnsignedIntegral";
        IIOMetadataNode subNode = new IIOMetadataNode("SampleFormat");
        subNode.setAttribute("value", sampleFormat);
        node.appendChild(subNode);

        String bits = "";
        if(redMask != 0 || greenMask != 0 || blueMask != 0) {
            bits =
                countBits(redMask) + " " +
                countBits(greenMask) + " " +
                countBits(blueMask);
            if(alphaMask != 0) {
                bits += " " + countBits(alphaMask);
            }
        } else if(palette != null && paletteSize > 0) {
            for(int i = 1; i <= 3; i++) {
                bits += bitsPerPixel;
                if(i != 3) {
                    bits += " ";
                }
            }
        } else {
            if (bitsPerPixel == 1) {
                bits = "1";
            } else if (bitsPerPixel == 4) {
                bits = "4";
            } else if (bitsPerPixel == 8) {
                bits = "8";
            } else if (bitsPerPixel == 16) {
                bits = "5 6 5";
            } else if (bitsPerPixel == 24) {
                bits = "8 8 8";
            } else if ( bitsPerPixel == 32) {
                bits = "8 8 8 8";
            }
        }

        if(!bits.equals("")) {
            subNode = new IIOMetadataNode("BitsPerSample");
            subNode.setAttribute("value", bits);
            node.appendChild(subNode);
        }

        return node;
    }

    protected IIOMetadataNode getStandardDimensionNode() {
	if (yPixelsPerMeter > 0 && xPixelsPerMeter > 0) {
	    IIOMetadataNode node = new IIOMetadataNode("Dimension");
	    float ratio = (float)yPixelsPerMeter / (float)xPixelsPerMeter;
	    IIOMetadataNode subNode = new IIOMetadataNode("PixelAspectRatio");
	    subNode.setAttribute("value", "" + ratio);
	    node.appendChild(subNode);

	    subNode = new IIOMetadataNode("HorizontalPixelSize");
	    subNode.setAttribute("value", "" + (1000.0F / xPixelsPerMeter));
	    node.appendChild(subNode);

	    subNode = new IIOMetadataNode("VerticalPixelSize");
	    subNode.setAttribute("value", "" + (1000.0F / yPixelsPerMeter));
	    node.appendChild(subNode);

            // Emit HorizontalPhysicalPixelSpacing and
            // VerticalPhysicalPixelSpacing for historical reasonse:
            // HorizontalPixelSize and VerticalPixelSize should have
            // been used in the first place.
	    subNode = new IIOMetadataNode("HorizontalPhysicalPixelSpacing");
	    subNode.setAttribute("value", "" + (1000.0F / xPixelsPerMeter));
	    node.appendChild(subNode);

	    subNode = new IIOMetadataNode("VerticalPhysicalPixelSpacing");
	    subNode.setAttribute("value", "" + (1000.0F / yPixelsPerMeter));
	    node.appendChild(subNode);

	    return node;
	}
	return null;
    }

    protected IIOMetadataNode getStandardDocumentNode() {
        if(bmpVersion != null) {
	    IIOMetadataNode node = new IIOMetadataNode("Document");
	    IIOMetadataNode subNode = new IIOMetadataNode("FormatVersion");
	    subNode.setAttribute("value", bmpVersion);
	    node.appendChild(subNode);
            return node;
        }
	return null;
    }

    protected IIOMetadataNode getStandardTextNode() {
        if(comments != null) {
	    IIOMetadataNode node = new IIOMetadataNode("Text");
            Iterator iter = comments.iterator();
            while(iter.hasNext()) {
                String comment = (String)iter.next();
                IIOMetadataNode subNode = new IIOMetadataNode("TextEntry");
                subNode.setAttribute("keyword", "comment");
                subNode.setAttribute("value", comment);
                node.appendChild(subNode);
            }
            return node;
        }
	return null;
    }

    protected IIOMetadataNode getStandardTransparencyNode() {
        IIOMetadataNode node = new IIOMetadataNode("Transparency");
        IIOMetadataNode subNode = new IIOMetadataNode("Alpha");
        String alpha;
        if(alphaMask != 0) {
            alpha = "nonpremultiplied";
        } else {
            alpha = "none";
        }

        subNode.setAttribute("value", alpha);
        node.appendChild(subNode);
        return node;
    }

    // Shorthand for throwing an IIOInvalidTreeException
    private void fatal(Node node, String reason)
        throws IIOInvalidTreeException {
        throw new IIOInvalidTreeException(reason, node);
    }

    // Get an integer-valued attribute
    private int getIntAttribute(Node node, String name,
                                int defaultValue, boolean required)
        throws IIOInvalidTreeException {
        String value = getAttribute(node, name, null, required);
        if (value == null) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    // Get a double-valued attribute
    private double getDoubleAttribute(Node node, String name,
                                      double defaultValue, boolean required)
        throws IIOInvalidTreeException {
        String value = getAttribute(node, name, null, required);
        if (value == null) {
            return defaultValue;
        }
        return Double.parseDouble(value);
    }

    // Get a required integer-valued attribute
    private int getIntAttribute(Node node, String name)
        throws IIOInvalidTreeException {
        return getIntAttribute(node, name, -1, true);
    }

    // Get a required double-valued attribute
    private double getDoubleAttribute(Node node, String name)
        throws IIOInvalidTreeException {
        return getDoubleAttribute(node, name, -1.0F, true);
    }

    // Get a String-valued attribute
    private String getAttribute(Node node, String name,
                                String defaultValue, boolean required)
        throws IIOInvalidTreeException {
        Node attr = node.getAttributes().getNamedItem(name);
        if (attr == null) {
            if (!required) {
                return defaultValue;
            } else {
                fatal(node, "Required attribute " + name + " not present!");
            }
        }
        return attr.getNodeValue();
    }

    // Get a required String-valued attribute
    private String getAttribute(Node node, String name)
        throws IIOInvalidTreeException {
        return getAttribute(node, name, null, true);
    }

    void initialize(ColorModel cm, SampleModel sm, ImageWriteParam param) {
        // bmpVersion and compression.
        if(param != null) {
            bmpVersion = BMPConstants.VERSION_3;

            if(param.getCompressionMode() == ImageWriteParam.MODE_EXPLICIT) {
                String compressionType = param.getCompressionType();
                compression =
                    BMPImageWriter.getCompressionType(compressionType);
            }
        } else {
            bmpVersion = BMPConstants.VERSION_3;
            compression = BMPImageWriter.getPreferredCompressionType(cm, sm);
        }

        // width and height
        width = sm.getWidth();
        height = sm.getHeight();

        // bitsPerPixel
        bitsPerPixel = (short)cm.getPixelSize();

        // mask
        if(cm instanceof DirectColorModel) {
            DirectColorModel dcm = (DirectColorModel)cm;
            redMask = dcm.getRedMask();
            greenMask = dcm.getGreenMask();
            blueMask = dcm.getBlueMask();
            alphaMask = dcm.getAlphaMask();
        }

        // palette and paletteSize
        if(cm instanceof IndexColorModel) {
	    IndexColorModel icm = (IndexColorModel)cm;
	    paletteSize = icm.getMapSize();

	    byte[] r = new byte[paletteSize];
	    byte[] g = new byte[paletteSize];
	    byte[] b = new byte[paletteSize];

	    icm.getReds(r);
	    icm.getGreens(g);
	    icm.getBlues(b);

            boolean isVersion2 =
                bmpVersion != null && bmpVersion.equals(VERSION_2);

            palette = new byte[(isVersion2 ? 3 : 4)*paletteSize];
            for(int i = 0, j = 0; i < paletteSize; i++) {
                palette[j++] = b[i];
                palette[j++] = g[i];
                palette[j++] = r[i];
                if(!isVersion2) j++; // skip reserved entry
            }
        }
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

    private void mergeNativeTree(Node root)
        throws IIOInvalidTreeException {
        Node node = root;
        if (!node.getNodeName().equals(nativeMetadataFormatName)) {
            fatal(node, "Root must be " + nativeMetadataFormatName);
        }
        
        byte[] r = null, g = null, b = null;
        int maxIndex = -1;

        node = node.getFirstChild();
        while (node != null) {
            String name = node.getNodeName();

            if (name.equals("BMPVersion")) {
                String value = getStringValue(node);
                if(value != null) bmpVersion = value;
            } else if (name.equals("Width")) {
                Integer value = getIntegerValue(node);
                if(value != null) width = value.intValue();
            } else if (name.equals("Height")) {
                Integer value = getIntegerValue(node);
                if(value != null) height = value.intValue();
            } else if (name.equals("BitsPerPixel")) {
                Short value = getShortValue(node);
                if(value != null) bitsPerPixel = value.shortValue();
            } else if (name.equals("Compression")) {
                Integer value = getIntegerValue(node);
                if(value != null) compression = value.intValue();
            } else if (name.equals("ImageSize")) {
                Integer value = getIntegerValue(node);
                if(value != null) imageSize = value.intValue();
            } else if (name.equals("PixelsPerMeter")) {
                Node subNode = node.getFirstChild();
                while (subNode != null) {
                    String subName = subNode.getNodeName();
                    if(subName.equals("X")) {
                        Integer value = getIntegerValue(subNode);
                        if(value != null)
                            xPixelsPerMeter = value.intValue();
                    } else if(subName.equals("Y")) {
                        Integer value = getIntegerValue(subNode);
                        if(value != null)
                            yPixelsPerMeter = value.intValue();
                    }
                    subNode = subNode.getNextSibling();
                }
            } else if (name.equals("ColorsUsed")) {
                Integer value = getIntegerValue(node);
                if(value != null) colorsUsed = value.intValue();
            } else if (name.equals("ColorsImportant")) {
                Integer value = getIntegerValue(node);
                if(value != null) colorsImportant = value.intValue();
            } else if (name.equals("Mask")) {
                Node subNode = node.getFirstChild();
                while (subNode != null) {
                    String subName = subNode.getNodeName();
                    if(subName.equals("Red")) {
                        Integer value = getIntegerValue(subNode);
                        if(value != null)
                            redMask = value.intValue();
                    } else if(subName.equals("Green")) {
                        Integer value = getIntegerValue(subNode);
                        if(value != null)
                            greenMask = value.intValue();
                    } else if(subName.equals("Blue")) {
                        Integer value = getIntegerValue(subNode);
                        if(value != null)
                            blueMask = value.intValue();
                    } else if(subName.equals("Alpha")) {
                        Integer value = getIntegerValue(subNode);
                        if(value != null)
                            alphaMask = value.intValue();
                    }
                    subNode = subNode.getNextSibling();
                }
            } else if (name.equals("ColorSpace")) {
                Integer value = getIntegerValue(node);
                if(value != null) colorSpace = value.intValue();
            } else if (name.equals("CIEXYZEndpoints")) {
                Node subNode = node.getFirstChild();
                while (subNode != null) {
                    String subName = subNode.getNodeName();
                    if(subName.equals("Red")) {
                        Node subNode1 = subNode.getFirstChild();
                        while (subNode1 != null) {
                            String subName1 = subNode1.getNodeName();
                            if(subName1.equals("X")) {
                                Double value = getDoubleValue(subNode1);
                                if(value != null)
                                    redX = value.doubleValue();
                            } else if(subName1.equals("Y")) {
                                Double value = getDoubleValue(subNode1);
                                if(value != null)
                                    redY = value.doubleValue();
                            } else if(subName1.equals("Z")) {
                                Double value = getDoubleValue(subNode1);
                                if(value != null)
                                    redZ = value.doubleValue();
                            }
                            subNode1 = subNode1.getNextSibling();
                        }
                    } else if(subName.equals("Green")) {
                        Node subNode1 = subNode.getFirstChild();
                        while (subNode1 != null) {
                            String subName1 = subNode1.getNodeName();
                            if(subName1.equals("X")) {
                                Double value = getDoubleValue(subNode1);
                                if(value != null)
                                    greenX = value.doubleValue();
                            } else if(subName1.equals("Y")) {
                                Double value = getDoubleValue(subNode1);
                                if(value != null)
                                    greenY = value.doubleValue();
                            } else if(subName1.equals("Z")) {
                                Double value = getDoubleValue(subNode1);
                                if(value != null)
                                    greenZ = value.doubleValue();
                            }
                            subNode1 = subNode1.getNextSibling();
                        }
                    } else if(subName.equals("Blue")) {
                        Node subNode1 = subNode.getFirstChild();
                        while (subNode1 != null) {
                            String subName1 = subNode1.getNodeName();
                            if(subName1.equals("X")) {
                                Double value = getDoubleValue(subNode1);
                                if(value != null)
                                    blueX = value.doubleValue();
                            } else if(subName1.equals("Y")) {
                                Double value = getDoubleValue(subNode1);
                                if(value != null)
                                    blueY = value.doubleValue();
                            } else if(subName1.equals("Z")) {
                                Double value = getDoubleValue(subNode1);
                                if(value != null)
                                    blueZ = value.doubleValue();
                            }
                            subNode1 = subNode1.getNextSibling();
                        }
                    }
                    subNode = subNode.getNextSibling();
                }
            } else if (name.equals("Gamma")) {
                Node subNode = node.getFirstChild();
                while (subNode != null) {
                    String subName = subNode.getNodeName();
                    if(subName.equals("Red")) {
                        Integer value = getIntegerValue(subNode);
                        if(value != null)
                            gammaRed = value.intValue();
                    } else if(subName.equals("Green")) {
                        Integer value = getIntegerValue(subNode);
                        if(value != null)
                            gammaGreen = value.intValue();
                    } else if(subName.equals("Blue")) {
                        Integer value = getIntegerValue(subNode);
                        if(value != null)
                            gammaBlue = value.intValue();
                    }
                    subNode = subNode.getNextSibling();
                }
            } else if (name.equals("Intent")) {
                Integer value = getIntegerValue(node);
                if(value != null) intent = value.intValue();
            } else if (name.equals("Palette")) {
                paletteSize = getIntAttribute(node, "sizeOfPalette");

                r = new byte[paletteSize];
                g = new byte[paletteSize];
                b = new byte[paletteSize];
                maxIndex = -1;
                
                Node paletteEntry = node.getFirstChild();
                if (paletteEntry == null) {
                    fatal(node, "Palette has no entries!");
                }

                int numPaletteEntries = 0;
                while (paletteEntry != null) {
                    if (!paletteEntry.getNodeName().equals("PaletteEntry")) {
                        fatal(node,
                              "Only a PaletteEntry may be a child of a Palette!");
                    }

                    int index = -1;
                    Node subNode = paletteEntry.getFirstChild();
                    while (subNode != null) {
                        String subName = subNode.getNodeName();
                        if(subName.equals("Index")) {
                            Integer value = getIntegerValue(subNode);
                            if(value != null)
                                index = value.intValue();
                            if (index < 0 || index > paletteSize - 1) {
                                fatal(node,
                                      "Bad value for PaletteEntry attribute index!");
                            }
                        } else if(subName.equals("Red")) {
                            Integer value = getIntegerValue(subNode);
                            if(value != null)
                                red = value.intValue();
                        } else if(subName.equals("Green")) {
                            Integer value = getIntegerValue(subNode);
                            if(value != null)
                                green = value.intValue();
                        } else if(subName.equals("Blue")) {
                            Integer value = getIntegerValue(subNode);
                            if(value != null)
                                blue = value.intValue();
                        }
                        subNode = subNode.getNextSibling();
                    }

                    if(index == -1) {
                        index = numPaletteEntries;
                    }
                    if (index > maxIndex) {
                        maxIndex = index;
                    }

                    r[index] = (byte)red;
                    g[index] = (byte)green;
                    b[index] = (byte)blue;

                    numPaletteEntries++;
                    paletteEntry = paletteEntry.getNextSibling();
                }
            } else if (name.equals("CommentExtensions")) {
                // CommentExtension
                Node commentExtension = node.getFirstChild();
                if (commentExtension == null) {
                    fatal(node, "CommentExtensions has no entries!");
                }

                if(comments == null) {
                    comments = new ArrayList();
                }

                while (commentExtension != null) {
                    if (!commentExtension.getNodeName().equals("CommentExtension")) {
                        fatal(node,
                              "Only a CommentExtension may be a child of a CommentExtensions!");
                    }

                    comments.add(getAttribute(commentExtension, "value"));

                    commentExtension = commentExtension.getNextSibling();
                }
            } else {
                fatal(node, "Unknown child of root node!");
            }
            
            node = node.getNextSibling();
        }

        if(r != null && g != null && b != null) {
            boolean isVersion2 =
                bmpVersion != null && bmpVersion.equals(VERSION_2);

            int numEntries = maxIndex + 1;
            palette = new byte[(isVersion2 ? 3 : 4)*numEntries];
            for(int i = 0, j = 0; i < numEntries; i++) {
                palette[j++] = b[i];
                palette[j++] = g[i];
                palette[j++] = r[i];
                if(!isVersion2) j++; // skip reserved entry
            }
        }
    }

    private void mergeStandardTree(Node root)
        throws IIOInvalidTreeException {
        Node node = root;
        if (!node.getNodeName()
            .equals(IIOMetadataFormatImpl.standardMetadataFormatName)) {
            fatal(node, "Root must be " +
                  IIOMetadataFormatImpl.standardMetadataFormatName);
        }

        String colorSpaceType = null;
        int numChannels = 0;
        int[] bitsPerSample = null;
        boolean hasAlpha = false;
        
        byte[] r = null, g = null, b = null;
        int maxIndex = -1;

        node = node.getFirstChild();
        while(node != null) {
            String name = node.getNodeName();

            if (name.equals("Chroma")) {
                Node child = node.getFirstChild();
                while (child != null) {
                    String childName = child.getNodeName();
                    if (childName.equals("ColorSpaceType")) {
                        colorSpaceType = getAttribute(child, "name");
                    } else if (childName.equals("NumChannels")) {
                        numChannels = getIntAttribute(child, "value");
                    } else if (childName.equals("Gamma")) {
                        gammaRed = gammaGreen = gammaBlue =
                            (int)(getDoubleAttribute(child, "value") + 0.5);
                    } else if (childName.equals("Palette")) {
                        r = new byte[256];
                        g  = new byte[256];
                        b = new byte[256];
                        maxIndex = -1;
                
                        Node paletteEntry = child.getFirstChild();
                        if (paletteEntry == null) {
                            fatal(node, "Palette has no entries!");
                        }

                        while (paletteEntry != null) {
                            if (!paletteEntry.getNodeName().equals("PaletteEntry")) {
                                fatal(node,
                                      "Only a PaletteEntry may be a child of a Palette!");
                            }
                    
                            int index = getIntAttribute(paletteEntry, "index");
                            if (index < 0 || index > 255) {
                                fatal(node,
                                      "Bad value for PaletteEntry attribute index!");
                            }
                            if (index > maxIndex) {
                                maxIndex = index;
                            }
                            r[index] =
                                (byte)getIntAttribute(paletteEntry, "red");
                            g[index] =
                                (byte)getIntAttribute(paletteEntry, "green");
                            b[index] =
                                (byte)getIntAttribute(paletteEntry, "blue");
                        
                            paletteEntry = paletteEntry.getNextSibling();
                        }
                    }

                    child = child.getNextSibling();
                }
            } else if (name.equals("Compression")) {
                Node child = node.getFirstChild();
                while(child != null) {
                    String childName = child.getNodeName();
                    if (childName.equals("CompressionTypeName")) {
                        String compressionName = getAttribute(child, "value");
                        compression =
                            BMPImageWriter.getCompressionType(compressionName);
                    }
                    child = child.getNextSibling();
                }
            } else if (name.equals("Data")) {
                Node child = node.getFirstChild();
                while(child != null) {
                    String childName = child.getNodeName();
                    if (childName.equals("BitsPerSample")) {
                        List bps = new ArrayList(4);
                        String s = getAttribute(child, "value");
                        StringTokenizer t = new StringTokenizer(s);
                        while(t.hasMoreTokens()) {
                            bps.add(Integer.valueOf(t.nextToken()));
                        }
                        bitsPerSample = new int[bps.size()];
                        for(int i = 0; i < bitsPerSample.length; i++) {
                            bitsPerSample[i] =
                                ((Integer)bps.get(i)).intValue();
                        }
                        break;
                    }
                    child = child.getNextSibling();
                }
            } else if (name.equals("Dimension")) {
                boolean gotWidth = false;
                boolean gotHeight = false;
                boolean gotAspectRatio = false;
                boolean gotSpaceX = false;
                boolean gotSpaceY = false;

                double width = -1.0F;
                double height = -1.0F;
                double aspectRatio = -1.0F;
                double spaceX = -1.0F;
                double spaceY = -1.0F;
                
                Node child = node.getFirstChild();
                while (child != null) {
                    String childName = child.getNodeName();
                    if (childName.equals("PixelAspectRatio")) {
                        aspectRatio = getDoubleAttribute(child, "value");
                        gotAspectRatio = true;
                    } else if (childName.equals("HorizontalPixelSize")) {
                        width = getDoubleAttribute(child, "value");
                        gotWidth = true;
                    } else if (childName.equals("VerticalPixelSize")) {
                        height = getDoubleAttribute(child, "value");
                        gotHeight = true;
                    } else if (childName.equals("HorizontalPhysicalPixelSpacing")) {
                        spaceX = getDoubleAttribute(child, "value");
                        gotSpaceX = true;
                    } else if (childName.equals("VerticalPhysicalPixelSpacing")) {
                        spaceY = getDoubleAttribute(child, "value");
                        gotSpaceY = true;
                        // } else if (childName.equals("ImageOrientation")) {
                        // } else if (childName.equals("HorizontalPosition")) {
                        // } else if (childName.equals("VerticalPosition")) {
                        // } else if (childName.equals("HorizontalPixelOffset")) {
                        // } else if (childName.equals("VerticalPixelOffset")) {
                    }
                    child = child.getNextSibling();
                }

                // Use PhysicalPixelSpacing if PixelSize not available.
                if(!(gotWidth || gotHeight) && (gotSpaceX || gotSpaceY)) {
                    width = spaceX;
                    gotWidth = gotSpaceX;
                    height = spaceY;
                    gotHeight = gotSpaceY;
                }

                // Round floating point values to obtain int resolution.
                if (gotWidth && gotHeight) {
                    xPixelsPerMeter = (int)(1000.0/width + 0.5);
                    yPixelsPerMeter = (int)(1000.0/height + 0.5);
                } else if (gotAspectRatio && aspectRatio != 0.0) {
                    if(gotWidth) {
                        xPixelsPerMeter = (int)(1000.0/width + 0.5);
                        yPixelsPerMeter =
                            (int)(aspectRatio*(1000.0/width) + 0.5);
                    } else if(gotHeight) {
                        xPixelsPerMeter =
                            (int)(1000.0/height/aspectRatio + 0.5);
                        yPixelsPerMeter = (int)(1000.0/height + 0.5);
                    }
                }
            } else if (name.equals("Document")) {
                Node child = node.getFirstChild();
                while(child != null) {
                    String childName = child.getNodeName();
                    if (childName.equals("FormatVersion")) {
                        bmpVersion = getAttribute(child, "value");
                        break;
                    }
                    child = child.getNextSibling();
                }
            } else if (name.equals("Text")) {
                Node child = node.getFirstChild();
                while(child != null) {
                    String childName = child.getNodeName();
                    if (childName.equals("TextEntry")) {
                        if(comments == null) {
                            comments = new ArrayList();
                        }
                        comments.add(getAttribute(child, "value"));
                    }
                    child = child.getNextSibling();
                }
            } else if (name.equals("Transparency")) {
                Node child = node.getFirstChild();
                while(child != null) {
                    String childName = child.getNodeName();
                    if (childName.equals("Alpha")) {
                        hasAlpha =
                            !getAttribute(child, "value").equals("none");
                        break;
                    }
                    child = child.getNextSibling();
                }
            } else {
                // XXX Ignore it.
            }
            
            node = node.getNextSibling();
        }

        // Set bitsPerPixel.
        if(bitsPerSample != null) {
            if(palette != null && paletteSize > 0) {
                bitsPerPixel = (short)bitsPerSample[0];
            } else {
                bitsPerPixel = 0;
                for(int i = 0; i < bitsPerSample.length; i++) {
                    bitsPerPixel += bitsPerSample[i];
                }
            }
        } else if(palette != null) {
            bitsPerPixel = 8;
        } else if(numChannels == 1) {
            bitsPerPixel = 8;
        } else if(numChannels == 3) {
            bitsPerPixel = 24;
        } else if(numChannels == 4) {
            bitsPerPixel = 32;
        } else if(colorSpaceType.equals("GRAY")) {
            bitsPerPixel = 8;
        } else if(colorSpaceType.equals("RGB")) {
            bitsPerPixel = (short)(hasAlpha ? 32 : 24);
        }

        // Set RGB masks.
        if((bitsPerSample != null && bitsPerSample.length == 4) ||
           bitsPerPixel >= 24) {
            redMask   = 0x00ff0000;
            greenMask = 0x0000ff00;
            blueMask  = 0x000000ff;
        }

        // Set alpha mask.
        if((bitsPerSample != null && bitsPerSample.length == 4) ||
           bitsPerPixel > 24) {
            alphaMask = 0xff000000;
        }

        // Set palette
        if(r != null && g != null && b != null) {
            boolean isVersion2 =
                bmpVersion != null && bmpVersion.equals(VERSION_2);

            paletteSize = maxIndex + 1;
            palette = new byte[(isVersion2 ? 3 : 4)*paletteSize];
            for(int i = 0, j = 0; i < paletteSize; i++) {
                palette[j++] = b[i];
                palette[j++] = g[i];
                palette[j++] = r[i];
                if(!isVersion2) j++; // skip reserved entry
            }
        }
    }

    public void reset() {
        // Fields for Image Descriptor
        bmpVersion = null;
        width = 0;
        height = 0;
        bitsPerPixel = 0;
        compression = 0;
        imageSize = 0;

        // Fields for PixelsPerMeter
        xPixelsPerMeter = 0;
        yPixelsPerMeter = 0;

        colorsUsed = 0;
        colorsImportant = 0;

        // Fields for BI_BITFIELDS compression(Mask)
        redMask = 0;
        greenMask = 0;
        blueMask = 0;
        alphaMask = 0;

        colorSpace = 0;

        // Fields for CIE XYZ for the LCS_CALIBRATED_RGB color space
        redX = 0;
        redY = 0;
        redZ = 0;
        greenX = 0;
        greenY = 0;
        greenZ = 0;
        blueX = 0;
        blueY = 0;
        blueZ = 0;

        // Fields for Gamma values for the LCS_CALIBRATED_RGB color space
        gammaRed = 0;
        gammaGreen = 0;
        gammaBlue = 0;

        intent = 0;

        // Fields for the Palette and Entries
        palette = null;
        paletteSize = 0;
        red = 0;
        green = 0;
        blue = 0;

        // Fields from CommentExtension
        comments = null;
    }

    private String countBits(int num) {
        int count = 0;
        while(num != 0) {
            if ((num & 1) == 1)
                count++;
            num >>>= 1;
        }

        return count == 0 ? "0" : "" + count;
    }

    private void addXYZPoints(IIOMetadataNode root, String name, double x, double y, double z) {
        IIOMetadataNode node = addChildNode(root, name, null);
        addChildNode(node, "X", new Double(x));
        addChildNode(node, "Y", new Double(y));
        addChildNode(node, "Z", new Double(z));
    }

    private IIOMetadataNode addChildNode(IIOMetadataNode root,
                                         String name,
                                         Object object) {
        IIOMetadataNode child = new IIOMetadataNode(name);
        if (object != null) {
            child.setUserObject(object);
	    child.setNodeValue(ImageUtil.convertObjectToString(object));
	}
        root.appendChild(child);
        return child;
    }

    private Object getObjectValue(Node node) {
        Object tmp = node.getNodeValue();

        if(tmp == null && node instanceof IIOMetadataNode) {
            tmp = ((IIOMetadataNode)node).getUserObject();
        }

        return tmp;
    }

    private String getStringValue(Node node) {
        Object tmp = getObjectValue(node);
        return tmp instanceof String ? (String)tmp : null;
    }

    private Byte getByteValue(Node node) {
        Object tmp = getObjectValue(node);
        Byte value = null;
        if(tmp instanceof String) {
            value = Byte.valueOf((String)tmp);
        } else if(tmp instanceof Byte) {
            value = (Byte)tmp;
        }
        return value;
    }

    private Short getShortValue(Node node) {
        Object tmp = getObjectValue(node);
        Short value = null;
        if(tmp instanceof String) {
            value = Short.valueOf((String)tmp);
        } else if(tmp instanceof Short) {
            value = (Short)tmp;
        }
        return value;
    }

    private Integer getIntegerValue(Node node) {
        Object tmp = getObjectValue(node);
        Integer value = null;
        if(tmp instanceof String) {
            value = Integer.valueOf((String)tmp);
        } else if(tmp instanceof Integer) {
            value = (Integer)tmp;
        } else if(tmp instanceof Byte) {
            value = new Integer(((Byte)tmp).byteValue() & 0xff);
        }
        return value;
    }

    private Double getDoubleValue(Node node) {
        Object tmp = getObjectValue(node);
        Double value = null;
        if(tmp instanceof String) {
            value = Double.valueOf((String)tmp);
        } else if(tmp instanceof Double) {
            value = (Double)tmp;
        }
        return value;
    }
}
