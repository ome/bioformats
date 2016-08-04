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
 * $RCSfile: PNMMetadata.java,v $
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
 * $Date: 2005/02/11 05:01:41 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.pnm;

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
import java.util.StringTokenizer;
import java.io.IOException;
import java.awt.color.ICC_Profile;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.SampleModel;
import java.awt.Point;

import com.sun.media.imageio.plugins.pnm.PNMImageWriteParam;
import com.sun.media.imageioimpl.common.ImageUtil;
/**
 * Metadata for the PNM plug-in.
 */
public class PNMMetadata extends IIOMetadata implements Cloneable {
    static final String nativeMetadataFormatName =
        "com_sun_media_imageio_plugins_pnm_image_1.0";

    /** The max value for the encoded/decoded image. */
    private int maxSample;

    /** The image width. */
    private int width;

    /** The image height. */
    private int height;

    /** The image variants. */
    private int variant;

    /** The comments. */
    private ArrayList comments;

    /** Maximum number of bits per sample (not in metadata). */
    private int maxSampleSize;

    /**
     * Constructor containing code shared by other constructors.
     */
    PNMMetadata() {
        super(true,  // Supports standard format
              nativeMetadataFormatName,  // and a native format
              "com.sun.media.imageioimpl.plugins.pnm.PNMMetadataFormat",
              null, null);  // No other formats
    }

    public PNMMetadata(IIOMetadata metadata) throws IIOInvalidTreeException {

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

    /**
     * Constructs a default image <code>PNMMetadata</code> object appropriate
     * for the given image type and write parameters.
     */
    PNMMetadata(ImageTypeSpecifier imageType,
                ImageWriteParam param) {
        this();
        initialize(imageType, param);
    }

    void initialize(ImageTypeSpecifier imageType,
                    ImageWriteParam param) {
        ImageTypeSpecifier destType = null;

        if (param != null) {
            destType = param.getDestinationType();
            if (destType == null) {
                destType = imageType;
            }
        } else {
            destType = imageType;
        }

        if (destType != null) {
            SampleModel sm = destType.getSampleModel();
            int[] sampleSize = sm.getSampleSize();

            this.width = sm.getWidth();
            this.height = sm.getHeight();

            for (int i = 0; i < sampleSize.length; i++) {
                if (sampleSize[i] > maxSampleSize) {
                    maxSampleSize = sampleSize[i];
                }
            }
            this.maxSample = (1 << maxSampleSize) - 1;

            boolean isRaw = true; // default value
            if(param instanceof PNMImageWriteParam) {
                isRaw = ((PNMImageWriteParam)param).getRaw();
            }

            if (maxSampleSize == 1)
                variant = '1';
            else if (sm.getNumBands() == 1) {
                variant = '2';
            } else if (sm.getNumBands() == 3) {
                variant = '3';
            }

            // Force to Raw if the sample size is small enough.
            if (variant <= '3' && isRaw && maxSampleSize <= 8) {
                variant += 0x3;
            }
        }
    }

    protected Object clone() {
        PNMMetadata theClone = null;

        try {
            theClone = (PNMMetadata) super.clone();
        } catch (CloneNotSupportedException e) {} // won't happen

        if (comments != null) {
            int numComments = comments.size();
            for(int i = 0; i < numComments; i++) {
                theClone.addComment((String)comments.get(i));
            }
        }
        return theClone;
    }

    public Node getAsTree(String formatName) {
        if (formatName == null) {
            throw new IllegalArgumentException(I18N.getString("PNMMetadata0"));
        }

        if (formatName.equals(nativeMetadataFormatName)) {
            return getNativeTree();
        }

        if (formatName.equals
            (IIOMetadataFormatImpl.standardMetadataFormatName)) {
            return getStandardTree();
        }

        throw  new IllegalArgumentException(I18N.getString("PNMMetadata1") + " " +
                                                formatName);
    }

    IIOMetadataNode getNativeTree() {
        IIOMetadataNode root =
            new IIOMetadataNode(nativeMetadataFormatName);

        IIOMetadataNode child = new IIOMetadataNode("FormatName");
        child.setUserObject(getFormatName());
	child.setNodeValue(getFormatName());
        root.appendChild(child);

        child = new IIOMetadataNode("Variant");
        child.setUserObject(getVariant());
        child.setNodeValue(getVariant());
        root.appendChild(child);

        child = new IIOMetadataNode("Width");
	Object tmp = new Integer(width);
        child.setUserObject(tmp);
	child.setNodeValue(ImageUtil.convertObjectToString(tmp));
        root.appendChild(child);

        child = new IIOMetadataNode("Height");
	tmp = new Integer(height);
        child.setUserObject(tmp);
	child.setNodeValue(ImageUtil.convertObjectToString(tmp));
        root.appendChild(child);

        child = new IIOMetadataNode("MaximumSample");
	tmp = new Byte((byte)maxSample);
        child.setUserObject(tmp);
	child.setNodeValue(ImageUtil.convertObjectToString(new Integer(maxSample)));
        root.appendChild(child);

        if(comments != null) {
            for (int i = 0; i < comments.size(); i++) {
                child = new IIOMetadataNode("Comment");
                tmp = comments.get(i);
                child.setUserObject(tmp);
                child.setNodeValue(ImageUtil.convertObjectToString(tmp));
                root.appendChild(child);
            }
        }

        return root;
    }

    // Standard tree node methods
    protected IIOMetadataNode getStandardChromaNode() {
        IIOMetadataNode node = new IIOMetadataNode("Chroma");

        int temp = (variant - '1') % 3 + 1;

        IIOMetadataNode subNode = new IIOMetadataNode("ColorSpaceType");
        if (temp == 3) {
            subNode.setAttribute("name", "RGB");
        } else {
            subNode.setAttribute("name", "GRAY");
        }
        node.appendChild(subNode);

        subNode = new IIOMetadataNode("NumChannels");
        subNode.setAttribute("value", "" + (temp == 3 ? 3 : 1));
        node.appendChild(subNode);

        if(temp != 3) {
            subNode = new IIOMetadataNode("BlackIsZero");
            subNode.setAttribute("value", "TRUE");
            node.appendChild(subNode);
        }

        return node;
    }

    protected IIOMetadataNode getStandardDataNode() {
        IIOMetadataNode node = new IIOMetadataNode("Data");

        IIOMetadataNode subNode = new IIOMetadataNode("SampleFormat");
        subNode.setAttribute("value", "UnsignedIntegral");
        node.appendChild(subNode);

        int temp = (variant - '1') % 3 + 1;
        subNode = new IIOMetadataNode("BitsPerSample");
        if(temp == 1) {
            subNode.setAttribute("value", "1");
        } else if(temp == 2) {
            subNode.setAttribute("value", "8");
        } else {
            subNode.setAttribute("value", "8 8 8");
        }
        node.appendChild(subNode);

        subNode = new IIOMetadataNode("SignificantBitsPerSample");
        if(temp == 1 || temp == 2) {
            subNode.setAttribute("value", "" + maxSampleSize);
        } else {
            subNode.setAttribute("value",
                                 maxSampleSize + " " +
                                 maxSampleSize + " " +
                                 maxSampleSize);
        }
        node.appendChild(subNode);

        return node;
    }

    protected IIOMetadataNode getStandardDimensionNode() {
        IIOMetadataNode node = new IIOMetadataNode("Dimension");

        IIOMetadataNode subNode = new IIOMetadataNode("ImageOrientation");
        subNode.setAttribute("value", "Normal");
        node.appendChild(subNode);

        return node;
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

    public boolean isReadOnly() {
        return false;
    }

    public void mergeTree(String formatName, Node root)
        throws IIOInvalidTreeException {
        if (formatName == null) {
            throw new IllegalArgumentException(I18N.getString("PNMMetadata0"));
        }

        if (root == null) {
            throw new IllegalArgumentException(I18N.getString("PNMMetadata2"));
        }

        if (formatName.equals(nativeMetadataFormatName) &&
            root.getNodeName().equals(nativeMetadataFormatName)) {
            mergeNativeTree(root);
        } else if (formatName.equals
                    (IIOMetadataFormatImpl.standardMetadataFormatName)) {
            mergeStandardTree(root);
        } else {
            throw  new IllegalArgumentException(I18N.getString("PNMMetadata1") + " " +
                                                formatName);
        }
    }

    public void setFromTree(String formatName, Node root)
        throws IIOInvalidTreeException {
        if (formatName == null) {
            throw new IllegalArgumentException(I18N.getString("PNMMetadata0"));
        }

        if (root == null) {
            throw new IllegalArgumentException(I18N.getString("PNMMetadata2"));
        }

        if (formatName.equals(nativeMetadataFormatName) &&
            root.getNodeName().equals(nativeMetadataFormatName)) {
            mergeNativeTree(root);
        } else if (formatName.equals
                    (IIOMetadataFormatImpl.standardMetadataFormatName)) {
            mergeStandardTree(root);
        } else {
            throw  new IllegalArgumentException(I18N.getString("PNMMetadata2") + " " +
                                                formatName);
        }
    }

    public void reset() {
        maxSample = width = height = variant = maxSampleSize = 0;
        comments = null;
    }

    public String getFormatName() {
        int v = (variant - '1') % 3 + 1;
        if (v == 1)
            return "PBM";
        if (v == 2)
            return "PGM";
        if (v == 3)
            return "PPM";
        return null;
    }

    public String getVariant() {
        if (variant > '3')
            return "RAWBITS";
        return "ASCII";
    }

    boolean isRaw() {
        return getVariant().equals("RAWBITS");
    }

    /** Sets the variant: '1' - '6'. */
    public void setVariant(int v) {
        this.variant = v;
    }

    public void setWidth(int w) {
        this.width = w;
    }

    public void setHeight(int h) {
        this.height = h;
    }

    int getMaxBitDepth() {
        return maxSampleSize;
    }

    int getMaxValue() {
        return maxSample;
    }

    /** Set the maximum sample size and maximum sample value.
     *  @param maxValue The maximum sample value.  This method computes the
     *                  maximum sample size.
     */
    public void setMaxBitDepth(int maxValue) {
        this.maxSample = maxValue;

        this.maxSampleSize = 0;
        while(maxValue > 0) {
            maxValue >>>= 1;
            maxSampleSize++;
        }
    }

    public synchronized void addComment(String comment) {
        if (comments == null) {
            comments = new ArrayList();
        }
        comment = comment.replaceAll("[\n\r\f]", " ");
        comments.add(comment);
    }

    Iterator getComments() {
        return comments == null ? null : comments.iterator();
    }

    private void mergeNativeTree(Node root) throws IIOInvalidTreeException {
        NodeList list = root.getChildNodes();
        String format = null;
        String var = null;

        for (int i = list.getLength() - 1; i >= 0; i--) {
            IIOMetadataNode node = (IIOMetadataNode)list.item(i);
            String name = node.getNodeName();

            if (name.equals("Comment")) {
                addComment((String)node.getUserObject());
            } else if (name.equals("Width")) {
                this.width = ((Integer)node.getUserObject()).intValue();
            } else if (name.equals("Height")) {
                this.width = ((Integer)node.getUserObject()).intValue();
            } else if (name.equals("MaximumSample")) {
                int maxValue = ((Integer)node.getUserObject()).intValue();
                setMaxBitDepth(maxValue);
            } else if (name.equals("FormatName")) {
                format = (String)node.getUserObject();
            } else if (name.equals("Variant")) {
                var = (String)node.getUserObject();
            }
        }

        if (format.equals("PBM"))
            variant = '1';
        else if (format.equals("PGM"))
            variant = '2';
        else if (format.equals("PPM"))
            variant = '3';

        if (var.equals("RAWBITS"))
            variant += 3;
    }

    private void mergeStandardTree(Node root) throws IIOInvalidTreeException {
        NodeList children = root.getChildNodes();

        String colorSpace = null;
        int numComps = 0;
        int[] bitsPerSample = null;

        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            String name = node.getNodeName();
            if (name.equals("Chroma")) {
                NodeList children1 = node.getChildNodes();
                for (int j = 0; j < children1.getLength(); j++) {
                    Node child = children1.item(j);
                    String name1 = child.getNodeName();

                    if (name1.equals("NumChannels")) {
                        String s = (String)getAttribute(child, "value");
                        numComps = new Integer(s).intValue();
                    } else if (name1.equals("ColorSpaceType")) {
                        colorSpace = (String)getAttribute(child, "name");
                    }
                }
            } else if (name.equals("Compression")) {
                // Do nothing.
            } else if (name.equals("Data")) {
                NodeList children1 = node.getChildNodes();
                int maxBitDepth = -1;
                for (int j = 0; j < children1.getLength(); j++) {
                    Node child = children1.item(j);
                    String name1 = child.getNodeName();

                    if (name1.equals("BitsPerSample")) {
                        List bps = new ArrayList(3);
                        String s = (String)getAttribute(child, "value");
                        StringTokenizer t = new StringTokenizer(s);
                        while(t.hasMoreTokens()) {
                            bps.add(Integer.valueOf(t.nextToken()));
                        }
                        bitsPerSample = new int[bps.size()];
                        for(int k = 0; k < bitsPerSample.length; k++) {
                            bitsPerSample[k] =
                                ((Integer)bps.get(k)).intValue();
                        }
                    } else if (name1.equals("SignificantBitsPerSample")) {
                        String s = (String)getAttribute(child, "value");
                        StringTokenizer t = new StringTokenizer(s);
                        while(t.hasMoreTokens()) {
                            int sbps =
                                Integer.valueOf(t.nextToken()).intValue();
                            maxBitDepth = Math.max(sbps, maxBitDepth);
                        }
                    }
                }

                // Set maximum bit depth and value.
                if(maxBitDepth > 0) {
                    setMaxBitDepth((1 << maxBitDepth) - 1);
                } else if(bitsPerSample != null) {
                    for(int k = 0; k < bitsPerSample.length; k++) {
                        if(bitsPerSample[k] > maxBitDepth) {
                            maxBitDepth = bitsPerSample[k];
                        }
                    }
                    setMaxBitDepth((1 << maxBitDepth) - 1);
                }
            } else if (name.equals("Dimension")) {
                // Do nothing.
            } else if (name.equals("Document")) {
                // Do nothing.
            } else if (name.equals("Text")) {
                NodeList children1 = node.getChildNodes();
                for (int j = 0; j < children1.getLength(); j++) {
                    Node child = children1.item(j);
                    String name1 = child.getNodeName();

                    if (name1.equals("TextEntry")) {
                        addComment((String)getAttribute(child, "value"));
                    }
                }
            } else if (name.equals("Transparency")) {
                // Do nothing.
            } else {
                throw new IIOInvalidTreeException(I18N.getString("PNMMetadata3") + " " +
						name, node);
            }
        }

        // Go from higher to lower: PPM > PGM > PBM.
        if((colorSpace != null && colorSpace.equals("RGB")) ||
           numComps > 1 ||
           bitsPerSample.length > 1) {
            variant = '3';
        } else if(maxSampleSize > 1) {
            variant = '2';
        } else {
            variant = '1';
        }
    }

    public Object getAttribute(Node node, String name) {
        NamedNodeMap map = node.getAttributes();
        node = map.getNamedItem(name);
        return (node != null) ? node.getNodeValue() : null;
    }
}
