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
 * $RCSfile: J2KMetadata.java,v $
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
 * $Revision: 1.4 $
 * $Date: 2006/09/22 23:07:25 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.jpeg2000;

import java.io.InputStream;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.metadata.IIOInvalidTreeException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;
import java.io.IOException;
import java.awt.color.ICC_Profile;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.SampleModel;
import java.awt.Point;

import com.sun.media.imageio.plugins.jpeg2000.J2KImageReadParam;
import com.sun.media.imageio.plugins.jpeg2000.J2KImageWriteParam;

import jj2000.j2k.fileformat.FileFormatBoxes;
import jj2000.j2k.fileformat.reader.FileFormatReader;
import jj2000.j2k.io.RandomAccessIO;

/**
 * Metadata for the J2K plug-in.
 */
public class J2KMetadata extends IIOMetadata implements Cloneable {
    static final String nativeMetadataFormatName =
        "com_sun_media_imageio_plugins_jpeg2000_image_1.0";

    /** cache the metadata format */
    private J2KMetadataFormat format;

    /** The boxes of JP2 file used as meta data, i. e., all the boxes
     *  except the data stream box
     */
    private ArrayList boxes = new ArrayList();

    /**
     * Constructor containing code shared by other constructors.
     */
    public J2KMetadata() {
        super(true,  // Supports standard format
              nativeMetadataFormatName,  // and a native format
              "com.sun.media.imageioimpl.plugins.jpeg2000.J2KMetadataFormat",
              null, null);  // No other formats

        format = (J2KMetadataFormat)getMetadataFormat(nativeMetadataFormatName);
    }

    /*
     * Constructs a <code>J2KMetadata</code> object by reading the
     * contents of an <code>ImageInputStream</code>.  Has package-only
     * access.
     *
     * @param iis An <code>ImageInputStream</code> from which to read
     * the metadata.
     * @param reader The <code>J2KImageReader</code> calling this
     * constructor, to which warnings should be sent.
     */
    public J2KMetadata(ImageInputStream iis,
                       J2KImageReader reader) throws IOException {
        this();
        RandomAccessIO in = new IISRandomAccessIO(iis);

        iis.mark();
        // **** File Format ****
        // If the codestream is wrapped in the jp2 fileformat, Read the
        // file format wrapper
        FileFormatReader ff = new FileFormatReader(in, this);
        ff.readFileFormat();
        iis.reset();
    }

    /**
     * Constructs a default stream <code>J2KMetadata</code> object appropriate
     * for the given write parameters.
     */
    public J2KMetadata(ImageWriteParam param, ImageWriter writer) {
        this(null, param, writer);
    }

    /**
     * Constructs a default image <code>J2KMetadata</code> object appropriate
     * for the given image type and write parameters.
     */
    public J2KMetadata(ImageTypeSpecifier imageType,
                       ImageWriteParam param,
                       ImageWriter writer) {
        this(imageType != null ? imageType.getColorModel() : null,
             imageType != null ? imageType.getSampleModel() : null,
             0, 0,
             param, writer);
    }

    /**
     * Constructs a default image <code>J2KMetadata</code> object appropriate
     * for the given image type and write parameters.
     */
    public J2KMetadata(ColorModel colorModel,
                       SampleModel sampleModel,
                       int width,
                       int height,
                       ImageWriteParam param,
                       ImageWriter writer) {
        this();
        addNode(new SignatureBox());
        addNode(new FileTypeBox(0x6A703220, 0, new int[]{0x6A703220}));

        ImageTypeSpecifier destType = null;

        if (param != null) {
            destType = param.getDestinationType();
            if (colorModel == null && sampleModel == null) {
                colorModel = destType == null ? null : destType.getColorModel();
                sampleModel =
                    destType == null ? null : destType.getSampleModel();
            }
        }

        int[] bitDepths = null;
        if(colorModel != null) {
            bitDepths = colorModel.getComponentSize();
        } else if(sampleModel != null) {
            bitDepths = sampleModel.getSampleSize();
        }

        int bitsPerComponent = 0xff;
        if(bitDepths != null) {
            bitsPerComponent = bitDepths[0];
            int numComponents = bitDepths.length;
            for(int i = 1; i < numComponents; i++) {
                /* XXX: This statement should be removed when BPC behavior
                   is corrected as derscribed below. */
                if(bitDepths[i] > bitsPerComponent) {
                    bitsPerComponent = bitDepths[i];
                }
                /* XXX: When the number of bits per component is not the
                   same for all components the BPC parameter of the Image
                   Header box should be set to 0xff and the actual number of
                   bits per component written in the Bits Per Component box.
                if(bitDepths[i] != bitsPerComponent) {
                    bitsPerComponent = 0xff;
                    break;
                }
                */
            }
        }

        if (colorModel != null) {
            ColorSpace cs = colorModel.getColorSpace();
            boolean iccColor = (cs instanceof ICC_ColorSpace);
            int type = cs.getType();

            if (type == ColorSpace.TYPE_RGB) {
                addNode(new ColorSpecificationBox((byte)1,
                                                  (byte)0, (byte)0,
                                                  ColorSpecificationBox.ECS_sRGB,
                                                  null));
            } else if (type == ColorSpace.TYPE_GRAY)
                addNode(new ColorSpecificationBox((byte)1,
                                                  (byte)0, (byte)0,
                                                  ColorSpecificationBox.ECS_GRAY,
                                                  null));
            else if (cs instanceof ICC_ColorSpace)
                addNode(new ColorSpecificationBox((byte)2,
                                                  (byte)0, (byte)0,
                                                  0,
                                                  ((ICC_ColorSpace)cs).getProfile()));

            if (colorModel.hasAlpha()) {
                addNode(new ChannelDefinitionBox(colorModel));
            }

            if (colorModel instanceof IndexColorModel) {
                addNode(new PaletteBox((IndexColorModel)colorModel));
		int numComp = colorModel.getComponentSize().length;
		short[] channels = new short[numComp];
		byte[] types = new byte[numComp];
		byte[] maps = new byte[numComp];
		for (int i = 0; i < numComp; i++) {
		    channels[i] = 0;
		    types[i] = 1;
		    maps[i] = (byte)i;
		}
		addNode(new ComponentMappingBox(channels, types, maps));
            }
        }

        if (sampleModel != null) {
            if (width <= 0)
                width = sampleModel.getWidth();
            if (height <= 0)
                height = sampleModel.getHeight();
            int bpc = bitsPerComponent == 0xff ?
                0xff : ((bitsPerComponent - 1) |
                        (isOriginalSigned(sampleModel) ? 0x80 : 0));
            addNode(new HeaderBox(height,
                                  width,
                                  sampleModel.getNumBands(),
                                  bpc,
                                  7,
                                  colorModel == null ? 1 : 0,
                                  getElement("JPEG2000IntellectualPropertyRightsBox")==null ? 0 : 1));
        }
    }

    public Object clone() {
        J2KMetadata theClone = null;

        try {
            theClone = (J2KMetadata) super.clone();
        } catch (CloneNotSupportedException e) {} // won't happen

        if (boxes != null) {
            int numBoxes = boxes.size();
            for(int i = 0; i < numBoxes; i++) {
                theClone.addNode((Box)boxes.get(i));
            }
        }
        return theClone;
    }

    public Node getAsTree(String formatName) {
        if (formatName == null) {
            throw new IllegalArgumentException(I18N.getString("J2KMetadata0"));
        }

        if (formatName.equals(nativeMetadataFormatName)) {
            return getNativeTree();
        }

        if (formatName.equals
            (IIOMetadataFormatImpl.standardMetadataFormatName)) {
            return getStandardTree();
        }

        throw  new IllegalArgumentException(I18N.getString("J2KMetadata1")
                                            + " " + formatName);
    }

    IIOMetadataNode getNativeTree() {
        IIOMetadataNode root =
            new IIOMetadataNode(nativeMetadataFormatName);

        Box signatureBox = null, fileTypeBox = null, headerBox = null;
        int signatureIndex = -1, fileTypeIndex = -1, headerIndex = -1;

        int numBoxes = boxes.size();

        int found = 0;
        for(int i = 0; i < numBoxes && found < 3; i++) {
            Box box = (Box)boxes.get(i);
            if(Box.getName(box.getType()).equals("JPEG2000SignatureBox")) {
                signatureBox = box;
                signatureIndex = i;
                found++;
            } else if(Box.getName(box.getType()).equals("JPEG2000FileTypeBox")) {
                fileTypeBox = box;
                fileTypeIndex = i;
                found++;
            } else if(Box.getName(box.getType()).equals("JPEG2000HeaderBox")) {
                headerBox = box;
                headerIndex = i;
                found++;
            }
        }

        if(signatureBox != null) {
            insertNodeIntoTree(root, signatureBox.getNativeNode());
        }

        if(fileTypeBox != null) {
            insertNodeIntoTree(root, fileTypeBox.getNativeNode());
        }

        if(headerBox != null) {
            insertNodeIntoTree(root, headerBox.getNativeNode());
        }

        for(int i = 0; i < numBoxes; i++) {
            if(i == signatureIndex ||
               i == fileTypeIndex  ||
               i == headerIndex) continue;
            Box box = (Box)boxes.get(i);
            IIOMetadataNode node = box.getNativeNode();
            insertNodeIntoTree(root, node);
        }
        return root;
    }

    // Standard tree node methods
    protected IIOMetadataNode getStandardChromaNode() {
        HeaderBox header = (HeaderBox)getElement("JPEG2000HeaderBox");
        PaletteBox palette = (PaletteBox)getElement("JPEG2000PaletteBox");
        ColorSpecificationBox color =
            (ColorSpecificationBox)getElement("JPEG2000ColorSpecificationBox");

        IIOMetadataNode node = new IIOMetadataNode("Chroma");
        IIOMetadataNode subNode = null;
        if (header != null) {
            if (header.getUnknownColorspace() == 0) {
                if (color != null && color.getMethod() == 1) {
                    subNode = new IIOMetadataNode("ColorSpaceType");
                    int ecs = color.getEnumeratedColorSpace();
                    if (ecs == FileFormatBoxes.CSB_ENUM_SRGB)
                        subNode.setAttribute("name", "RGB");
                    if (ecs == FileFormatBoxes.CSB_ENUM_GREY)
                        subNode.setAttribute("name", "GRAY");
                    node.appendChild(subNode);
                }
            }

            subNode = new IIOMetadataNode("NumChannels");
            subNode.setAttribute("value", "" + header.getNumComponents());
            node.appendChild(subNode);

            if (palette != null) {
                subNode.setAttribute("value", "" + palette.getNumComp());
                subNode = new IIOMetadataNode("Palette");
                byte[][] lut = palette.getLUT();

                int size = lut[0].length;
                int numComp = lut.length;

                for (int i = 0; i < size; i++) {
                    IIOMetadataNode subNode1 =
                        new IIOMetadataNode("PaletteEntry");
                    subNode1.setAttribute("index", ""+i);
                    subNode1.setAttribute("red", "" + (lut[0][i]&0xff));
                    subNode1.setAttribute("green", "" + (lut[1][i]&0xff));
                    subNode1.setAttribute("blue", "" + (lut[2][i]&0xff));
                    if (numComp == 4)
                        subNode1.setAttribute("alpha", "" + (lut[3][i]&0xff));
                    subNode.appendChild(subNode1);
                }
                node.appendChild(subNode);
            }
        }
        return node;
    }

    protected IIOMetadataNode getStandardCompressionNode() {
        IIOMetadataNode node = new IIOMetadataNode("Compression");

        // CompressionTypeName
        IIOMetadataNode subNode = new IIOMetadataNode("CompressionTypeName");
        subNode.setAttribute("value", "JPEG2000");
        node.appendChild(subNode);
        return node;
    }

    protected IIOMetadataNode getStandardDataNode() {
        IIOMetadataNode node = new IIOMetadataNode("Data");
        PaletteBox palette = (PaletteBox)getElement("JPEG2000PaletteBox");
        boolean sampleFormat = false;

        if (palette != null) {
            IIOMetadataNode subNode = new IIOMetadataNode("SampleFormat");
            subNode.setAttribute("value", "Index");
            node.appendChild(subNode);
            sampleFormat = true;
        }

        BitsPerComponentBox bitDepth =
            (BitsPerComponentBox)getElement("JPEG2000BitsPerComponentBox");
        String value = "";
        boolean signed = false;
        boolean gotSampleInfo = false;

        // JPEG 2000 "B" parameter represents "bitDepth - 1" in the
        // right 7 least significant bits with the most significant
        // bit indicating signed if set and unsigned if not.
        if (bitDepth != null) {
            byte[] bits = bitDepth.getBitDepth();
            if ((bits[0] & 0x80) == 0x80)
                signed = true;

            int numComp = bits.length;
            for (int i = 0; i < numComp; i++) {
                value += (bits[i] & 0x7f) + 1;
                if(i != numComp - 1) value += " ";
            }

            gotSampleInfo = true;
        } else {
            HeaderBox header = (HeaderBox)getElement("JPEG2000HeaderBox");
            if(header != null) {
                int bits = header.getBitDepth();
                if ((bits & 0x80) == 0x80)
                    signed = true;
                bits = (bits & 0x7f) + 1;
                int numComp = header.getNumComponents();
                for (int i = 0; i < numComp; i++) {
                    value += bits;
                    if(i != numComp - 1) value += " ";
                }

                gotSampleInfo = true;
            }
        }

        IIOMetadataNode subNode = null;

        if(gotSampleInfo) {
            subNode = new IIOMetadataNode("BitsPerSample");
            subNode.setAttribute("value", value);
            node.appendChild(subNode);
        }

        subNode = new IIOMetadataNode("PlanarConfiguration");
        subNode.setAttribute("value", "TileInterleaved");
        node.appendChild(subNode);

        if (!sampleFormat && gotSampleInfo) {
            subNode = new IIOMetadataNode("SampleFormat");
            subNode.setAttribute("value",
                             signed ? "SignedIntegral": "UnsignedIntegral");
            node.appendChild(subNode);
        }

        return node;
    }

    protected IIOMetadataNode getStandardDimensionNode() {
        ResolutionBox box =
            (ResolutionBox)getElement("JPEG2000CaptureResolutionBox");
        if (box != null) {
            IIOMetadataNode node = new IIOMetadataNode("Dimension");
            float hRes = box.getHorizontalResolution();
            float vRes = box.getVerticalResolution();
            float ratio = vRes / hRes;
            IIOMetadataNode subNode = new IIOMetadataNode("PixelAspectRatio");
            subNode.setAttribute("value", "" + ratio);
            node.appendChild(subNode);

            subNode = new IIOMetadataNode("HorizontalPixelSize");
            subNode.setAttribute("value", "" + (1000 / hRes));
            node.appendChild(subNode);

            subNode = new IIOMetadataNode("VerticalPixelSize");
            subNode.setAttribute("value", "" + (1000 / vRes));
            node.appendChild(subNode);

            return node;
        }

        return null;
    }

    protected IIOMetadataNode getStandardTransparencyNode() {
        ChannelDefinitionBox channel =
            (ChannelDefinitionBox)getElement("JPEG2000ChannelDefinitionBox");
        if (channel != null) {
            IIOMetadataNode node = new IIOMetadataNode("Transparency");

            boolean hasAlpha = false;
            boolean isPremultiplied = false;
            short[] type = channel.getTypes();

            for (int i = 0; i < type.length; i++) {
                if (type[i] == 1)
                    hasAlpha = true;
                if (type[i] == 2)
                    isPremultiplied = true;
            }

            String value = "none";
            if (isPremultiplied)
                value = "premultiplied";
            else if (hasAlpha)
                value = "nonpremultiplied";

            IIOMetadataNode subNode = new IIOMetadataNode("Alpha");
            subNode.setAttribute("value", value);
            node.appendChild(subNode);

            return node;
        }

        IIOMetadataNode node = new IIOMetadataNode("Transparency");
        IIOMetadataNode subNode = new IIOMetadataNode("Alpha");
        subNode.setAttribute("value", "none");
        node.appendChild(subNode);

        return null;
    }

    protected IIOMetadataNode getStandardTextNode() {
        if (boxes == null)
            return null;
        IIOMetadataNode text = null;

        Iterator iterator = boxes.iterator();

        while(iterator.hasNext()) {
            Box box = (Box)iterator.next();
            if (box instanceof XMLBox) {
                if (text == null)
                    text = new IIOMetadataNode("Text");
                IIOMetadataNode subNode = new IIOMetadataNode("TextEntry");
                String content = new String(box.getContent());
                subNode.setAttribute("value", content);
                text.appendChild(subNode);
            }
        }
        return text;
    }

    public boolean isReadOnly() {
        return false;
    }

    public void mergeTree(String formatName, Node root)
        throws IIOInvalidTreeException {
        if (formatName == null) {
            throw new IllegalArgumentException(I18N.getString("J2KMetadata0"));
        }

        if (root == null) {
            throw new IllegalArgumentException(I18N.getString("J2KMetadata2"));
        }

        if (formatName.equals(nativeMetadataFormatName) &&
            root.getNodeName().equals(nativeMetadataFormatName)) {
            mergeNativeTree(root);
        } else if (formatName.equals
                    (IIOMetadataFormatImpl.standardMetadataFormatName)) {
            mergeStandardTree(root);
        } else {
            throw  new IllegalArgumentException(I18N.getString("J2KMetadata1")
                                                + " " + formatName);
        }
    }

    public void setFromTree(String formatName, Node root)
        throws IIOInvalidTreeException {
        if (formatName == null) {
            throw new IllegalArgumentException(I18N.getString("J2KMetadata0"));
        }

        if (root == null) {
            throw new IllegalArgumentException(I18N.getString("J2KMetadata2"));
        }

        if (formatName.equals(nativeMetadataFormatName) &&
            root.getNodeName().equals(nativeMetadataFormatName)) {
            boxes = new ArrayList();
            mergeNativeTree(root);
        } else if (formatName.equals
                    (IIOMetadataFormatImpl.standardMetadataFormatName)) {
            boxes = new ArrayList();
            mergeStandardTree(root);
        } else {
            throw  new IllegalArgumentException(I18N.getString("J2KMetadata1")
                                                + " " + formatName);
        }
    }

    public void reset() {
        boxes.clear();
    }

    public void addNode(Box node) {
        if (boxes == null)
            boxes = new ArrayList();
        replace(Box.getName(node.getType()), node);
    }

    public Box getElement(String name) {
        for (int i = boxes.size() - 1; i >= 0; i--) {
            Box box = (Box)boxes.get(i);
            if (name.equals(Box.getName(box.getType())))
                return box;
        }
        return null;
    }

    private void mergeNativeTree(Node root) throws IIOInvalidTreeException {
        NodeList list = root.getChildNodes();
        for (int i = list.getLength() - 1; i >= 0; i--) {
            Node node = list.item(i);
            String name = node.getNodeName();
            if (format.getParent(name) != null) {
                if (format.isLeaf(name)) {
                    String s = (String)Box.getAttribute(node, "Type");
                    Box box = Box.createBox(Box.getTypeInt(s), node);
                    if (format.singleInstance(name)&&getElement(name) != null) {
                        replace(name, box);
                    } else
                        boxes.add(box);
                } else {
                    mergeNativeTree(node);
                }
            }
        }
    }

    private void mergeStandardTree(Node root) throws IIOInvalidTreeException {
        NodeList children = root.getChildNodes();
        int numComps = 0;

        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            String name = node.getNodeName();
            if (name.equals("Chroma")) {
                NodeList children1 = node.getChildNodes();
                for (int j = 0; j < children1.getLength(); j++) {
                    Node child = children1.item(j);
                    String name1 = child.getNodeName();

                    if (name1.equals("NumChannels")) {
                        String s = (String)Box.getAttribute(child, "value");
                        numComps = new Integer(s).intValue();
                    }

                    if (name1.equals("ColorSpaceType"))
                        createColorSpecificationBoxFromStandardNode(child);

                    if (name1.equals("Palette")) {
                        createPaletteBoxFromStandardNode(child);
                    }
                }
            } else if (name.equals("Compression")) {
                // Intentionally do nothing: just prevent entry into
                // the default "else" block and an ensuing
                // IIOInvalidTreeException; fixes 5110389.
            } else if (name.equals("Data")) {
                createBitsPerComponentBoxFromStandardNode(node);
                createHeaderBoxFromStandardNode(node, numComps);
            } else if (name.equals("Dimension")) {
                createResolutionBoxFromStandardNode(node);
            } else if (name.equals("Document")) {
                createXMLBoxFromStandardNode(node);
            } else if (name.equals("Text")) {
                createXMLBoxFromStandardNode(node);
            } else if (name.equals("Transparency")) {
                createChannelDefinitionFromStandardNode(node);
            } else {
                throw new IIOInvalidTreeException(I18N.getString("J2KMetadata3")
					+ " " + name, node);
            }
        }
    }

    private void createColorSpecificationBoxFromStandardNode(Node node) {
        if (node.getNodeName() != "ColorSpaceType")
            throw new IllegalArgumentException(I18N.getString("J2KMetadata4"));
        String name = (String)Box.getAttribute(node, "name");
        int ecs = name.equals("RGB") ? ColorSpecificationBox.ECS_sRGB :
                  (name.equals("Gray") ? ColorSpecificationBox.ECS_GRAY : 0);

        if (ecs == ColorSpecificationBox.ECS_sRGB ||
            ecs ==ColorSpecificationBox.ECS_GRAY) {
            replace ("JPEG2000ColorSpecificationBox",
                     new ColorSpecificationBox((byte)1, (byte)0, (byte)0,
                                               ecs, null));
        }
    }

    private void createPaletteBoxFromStandardNode(Node node) {
        if (node.getNodeName() != "Palette")
            throw new IllegalArgumentException(I18N.getString("J2KMetadata5"));
        NodeList children = node.getChildNodes();
        int maxIndex = -1;
        boolean hasAlpha = false;
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            String name = child.getNodeName();

            if (name.equals("PaletteEntry")) {
                String s = (String)Box.getAttribute(child, "index");
                int index = new Integer(s).intValue();
                if(index > maxIndex) {
                    maxIndex = index;
                }
                if(Box.getAttribute(child, "alpha") != null) {
                    hasAlpha = true;
                }
            }
        }

        // Determine palette size.
        int numBits = 32;
        int mask = 0x80000000;
        while(mask != 0 && (maxIndex & mask) == 0) {
            numBits--;
            mask >>>= 1;
        }
        int size = 1 << numBits;

        byte[] red = new byte[size];
        byte[] green = new byte[size];
        byte[] blue = new byte[size];
        byte[] alpha = hasAlpha ? new byte[size]: null;

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            String name = child.getNodeName();

            if (name.equals("PaletteEntry")) {
                String s = (String)Box.getAttribute(child, "index");
                int index = new Integer(s).intValue();
                s = (String)Box.getAttribute(child, "red");
                red[index] = (byte)(new Integer(s).intValue());
                s = (String)Box.getAttribute(child, "green");
                green[index] = (byte)(new Integer(s).intValue());
                s = (String)Box.getAttribute(child, "blue");
                blue[index] = (byte)(new Integer(s).intValue());

                byte t = (byte)255;
                s = (String)Box.getAttribute(child, "alpha");
                if(s != null) {
                    t = (byte)(new Integer(s).intValue());
                }

                if(alpha != null) {
                    alpha[index] = t;
                }
            }
        }

        IndexColorModel icm;
        if (alpha == null)
            icm = new IndexColorModel(numBits, size, red, green, blue);
        else
            icm = new IndexColorModel(numBits, size, red, green, blue, alpha);

        replace("JPEG2000PaletteBox", new PaletteBox(icm));
    }

    private void createBitsPerComponentBoxFromStandardNode(Node node) {
        if (node.getNodeName() != "Data")
            throw new IllegalArgumentException(I18N.getString("J2KMetadata6"));

        NodeList children = node.getChildNodes();

        byte[] bits = null;
        boolean isSigned = false;
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            String name = child.getNodeName();

            if (name.equals("BitsPerSample")) {
                String s = (String)Box.getAttribute(child, "value");
                bits = (byte[])Box.parseByteArray(s).clone();
            } else if(name.equals("SampleFormat")) {
                String s = (String)Box.getAttribute(child, "value");
                isSigned = s.equals("SignedIntegral");
            }
        }

        if(bits != null) {
            // JPEG 2000 "B" parameter represents "bitDepth - 1" in the
            // right 7 least significant bits with the most significant
            // bit indicating signed if set and unsigned if not.
            for (int i = 0; i < bits.length; i++) {
                bits[i] = (byte)((bits[i]&0xff) - 1);
                if(isSigned) {
                    bits[i] |= 0x80;
                }
            }

            replace("JPEG2000BitsPerComponent",
                    new BitsPerComponentBox(bits));
        }
    }

    private void createResolutionBoxFromStandardNode(Node node) {
        if (node.getNodeName() != "Dimension")
            throw new IllegalArgumentException(I18N.getString("J2KMetadata7"));
        NodeList children = node.getChildNodes();
        float hRes = 0.0f;
        float vRes = 0.0f;

        boolean gotH = false, gotV = false;

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            String name = child.getNodeName();

            if (name.equals("HorizontalPixelSize")) {
                String s = (String)Box.getAttribute(child, "value");
                hRes = new Float(s).floatValue();
                hRes = 1000 / hRes;
                gotH = true;
            }

            if (name.equals("VerticalPixelSize")) {
                String s = (String)Box.getAttribute(child, "value");
                vRes = new Float(s).floatValue();
                vRes = 1000 / vRes;
                gotV = true;
            }
        }

        if(gotH && !gotV) {
            vRes = hRes;
        } else if(gotV && !gotH) {
            hRes = vRes;
        }

        if(gotH || gotV) {
            replace("JPEG2000CaptureResolutionBox",
                    new ResolutionBox(0x72657363, hRes, vRes));
        }
    }

    private void createXMLBoxFromStandardNode(Node node) {
        NodeList children = node.getChildNodes();
        String value = "<" + node.getNodeName() + ">";

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            String name = child.getNodeName();
            value += "<" + name + " ";

            NamedNodeMap map = child.getAttributes();

            for (int j = 0; j < map.getLength(); j++) {
                Node att = map.item(j);
                value += att.getNodeName() + "=\"" +
                    att.getNodeValue() + "\" ";
            }

            value += " />";
        }

        value += "</" + node.getNodeName() + ">";

        boxes.add(new XMLBox(value.getBytes()));
    }

    private void createHeaderBoxFromStandardNode(Node node, int numComps) {
        HeaderBox header = (HeaderBox)getElement("JPEG2000HeaderBox");
        byte unknownColor =
            (byte)(getElement("JPEG2000ColorSpecificationBox") == null ? 1: 0);
        if (header != null) {
            if (numComps ==0);
                numComps = header.getNumComponents();

            header = new HeaderBox(header.getHeight(), header.getWidth(),
                                   numComps,
                                   header.getBitDepth(),
                                   header.getCompressionType(),
                                   unknownColor,
                                   header.getIntellectualProperty());
        } else {
            header = new HeaderBox(0, 0, numComps, 0, 0, unknownColor, 0);
        }
        replace("JPEG2000HeaderBox", header);
    }

    private void createChannelDefinitionFromStandardNode(Node node) {
        if (node.getNodeName() != "Transparency")
            throw new IllegalArgumentException(I18N.getString("J2KMetadata8"));

        HeaderBox header = (HeaderBox)getElement("JPEG2000HeaderBox");
        int numComps = 3;

        if (header != null) {
            numComps = header.getNumComponents();
        }

        NodeList children = node.getChildNodes();
        boolean hasAlpha = false;
        boolean isPremultiplied = false;

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            String name = child.getNodeName();

            if (name.equals("Alpha")) {
                String value = (String)Box.getAttribute(child, "value");
                if (value.equals("premultiplied"))
                    isPremultiplied = true;
                if (value.equals("nonpremultiplied"))
                    hasAlpha = true;
            }
        }

        if (!hasAlpha)
            return;

        int num = (short)(numComps * (isPremultiplied ? 3 : 2));
        short[] channels = new short[num];
        short[] types = new short[num];
        short[] associations = new short[num];
        ChannelDefinitionBox.fillBasedOnBands(numComps, isPremultiplied,
                                              channels, types, associations);
        replace("JPEG2000ChannelDefinitionBox",
                new ChannelDefinitionBox(channels, types, associations));
    }

    private void replace(String name, Box box) {
        for (int i = boxes.size() - 1; i >= 0; i--) {
            Box box1 = (Box)boxes.get(i);
            if (name.equals(Box.getName(box1.getType()))) {
                boxes.set(i, box);
                return;
            }
        }

        boxes.add(box);
    }

    private boolean insertNodeIntoTree(IIOMetadataNode root,
                                       IIOMetadataNode node) {
        String name = node.getNodeName();
        String parent = format.getParent(name);
        if (parent == null)
            return false;

        IIOMetadataNode parentNode = getNodeFromTree(root, parent, name);
        if (parentNode == null)
            parentNode = createNodeIntoTree(root, parent);
        parentNode.appendChild(node);
        return true;
    }

    private IIOMetadataNode getNodeFromTree(IIOMetadataNode root,
                                            String name,
                                            String childName) {
        if (name.equals(root.getNodeName()))
            return root;

        NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            IIOMetadataNode node = (IIOMetadataNode)list.item(i);
            if (node.getNodeName().equals(name)) {
                if (name.equals("JPEG2000UUIDInfoBox") &&
                    checkUUIDInfoBox(node, childName))
                    continue;
                else
                    return node;
            }
            node = getNodeFromTree(node, name, childName);
            if (node != null)
                return node;
        }

        return null;
    }

    private IIOMetadataNode createNodeIntoTree(IIOMetadataNode root,
                                               String name) {
        IIOMetadataNode node = getNodeFromTree(root, name, null);
        if (node != null)
            return node;

        node = new IIOMetadataNode(name);

        String parent = format.getParent(name);
        IIOMetadataNode parentNode = createNodeIntoTree(root, parent);
        parentNode.appendChild(node);

        return node;
    }

    private boolean isOriginalSigned(SampleModel sampleModel) {
        int type = sampleModel.getDataType();
        if (type == DataBuffer.TYPE_BYTE || type == DataBuffer.TYPE_USHORT)
            return false;
        return true;
    }

    /** Check whether the child with a name <code>childName</code> exists.
     *  This method is designed because UUID info box may have many instances.
     *  So if one of its sub-box is inserted into the tree, an empty slut for
     *  this sub-box has to be find or created to avoid one UUID info box
     *  has duplicated sub-boxes.  The users have to guarantee each UUID info
     *  box has all the sub-boxes.
     */
    private boolean checkUUIDInfoBox(Node node, String childName) {

        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            IIOMetadataNode child = (IIOMetadataNode)list.item(i);
            String name = child.getNodeName();

            if (name.equals(childName))
                return true;
        }

        return false;
    }
}
