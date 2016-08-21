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
 * $RCSfile: RawImageInputStream.java,v $
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
 * $Date: 2005/02/11 05:01:20 $
 * $State: Exp $
 */
package com.sun.media.imageio.stream;

import java.awt.Dimension;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteOrder;
import java.util.StringTokenizer;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.media.imageioimpl.common.ImageUtil;

/**
 * This class defines the content of the <code>ImageInputStream</code>
 * containing several raw images with the same image type: the number of
 * the images, the image type, the offset for the first sample of each image,
 * and the image size information.
 *
 * <p><code>ImageInputStream</code> methods are not commented individually.
 * These methods merely forward the call to the <code>ImageInputStream</code>
 * specified when the <code>RawImageInputStream</code> is constructed.</p>
 */
public class RawImageInputStream implements ImageInputStream {
    private static final String[] preDefinedColorSpaces =
        new String[]{"GRAY", "sRGB", "LINEAR_RGB", "PYCC", "CIEXYZ"};

    private static final int[]  preDefinedTypes = new int[]{
        ColorSpace.CS_GRAY, ColorSpace.CS_sRGB, ColorSpace.CS_LINEAR_RGB,
        ColorSpace.CS_PYCC, ColorSpace.CS_CIEXYZ
    };

    /** Gets the attribute from the node. */
    private static String getAttribute(Node node, String name) {
        NamedNodeMap map = node.getAttributes();
        node = map.getNamedItem(name);
        return (node != null) ? node.getNodeValue() : null;
    }

    /** Gets the boolean type attribute. */
    private static boolean getBoolean(Node node, String name) {
        String s = getAttribute(node, name);
        return (s == null) ? false : (new Boolean(s)).booleanValue();
    }

    /** Gets the integer type attribute. */
    private static int getInt(Node node, String name) {
        String s = getAttribute(node, name);
        return (s == null) ? 0 : (new Integer(s)).intValue();
    }

    /** Gets the integer type attribute. */
    private static byte[] getByteArray(Node node, String name) {
        String s = getAttribute(node, name);
        if (s == null)
            return null;
        StringTokenizer token = new StringTokenizer(s);
        int count = token.countTokens();
        if (count == 0)
            return null;

        byte[] buf = new byte[count];
        int i = 0;
        while(token.hasMoreElements()) {
            buf[i++] = new Byte(token.nextToken()).byteValue();
        }
        return buf;
    }

    /** Gets the integer type attribute. */
    private static int[] getIntArray(Node node, String name) {
        String s = getAttribute(node, name);
        if (s == null)
            return null;

        StringTokenizer token = new StringTokenizer(s);
        int count = token.countTokens();
        if (count == 0)
            return null;

        int[] buf = new int[count];
        int i = 0;
        while(token.hasMoreElements()) {
            buf[i++] = new Integer(token.nextToken()).intValue();
        }
        return buf;
    }

    private static int getTransparency(String s) {
        if ("BITMASK".equals(s))
            return Transparency.BITMASK;
        else if ("OPAQUE".equals(s))
            return Transparency.OPAQUE;
        else if ("TRANSLUCENT".equals(s))
            return Transparency.TRANSLUCENT;
        else return 0;
    }

    private static ColorSpace getColorSpace(Node node) throws IOException {
        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node child = nodes.item(i);
            if ("colorSpace".equals(child.getNodeName())) {
                String s = child.getNodeValue();
                for (int j = 0; j < preDefinedColorSpaces.length; j++) {
                    if (preDefinedColorSpaces[j].equals(s))
                        return ColorSpace.getInstance(preDefinedTypes[j]);
                }

                InputStream stm = new URL(s).openStream();

                ColorSpace cp = new ICC_ColorSpace(ICC_Profile.getInstance(stm));
                stm.close();
                return cp;
            }
        }
        return null;
    }

    /** The wrapperred <code>ImageInputStream</code>. */
    private ImageInputStream source;

    /** The image type for all the images in the stream. */
    private ImageTypeSpecifier type;

    /** The position of the first sample for each image. */
    private long[] imageOffsets;

    /** The image sizes. */
    private Dimension[] imageDimensions;

    /** Constructor.
     *  @param source The <code>ImageInputStream</code> containing all the raw
     *                images.
     *  @param type   The <code>ImageTypeSpecifier</code> for all the images
     *                in the stream.
     *  @param imageOffsets The position of the first sample for each image
     *                in the stream.
     *  @param imageDimensions The image size for each image in the stream.
     *
     *  @throws IllegalArgumentException If the sizes of
     *                <code>imageOffsets</code>
     *                and <code>imageDimensions</code> are different or if
     *                either array is null.
     */
    public RawImageInputStream(ImageInputStream source,
                               ImageTypeSpecifier type,
                               long[] imageOffsets,
                               Dimension[] imageDimensions) {
        if (imageOffsets == null || imageDimensions == null ||
            imageOffsets.length != imageDimensions.length) {
            throw new IllegalArgumentException
                (I18N.getString("RawImageInputStream0"));
        }

        this.source = source;
        this.type = type;
        this.imageOffsets = imageOffsets;
        this.imageDimensions = imageDimensions;
    }

    /**
     * Constructor.
     *
     * <p>This constructor is the same as
     * {@link #RawImageInputStream(ImageInputStream,ImageTypeSpecifier,
     * long[],Dimension[])} except that a <code>SampleModel</code> is
     * supplied instead of an <code>ImageTypeSpecifier</code>. This
     * constructor creates a <code>ColorModel</code> for the supplied
     * <code>SampleModel</code> and then creates an
     * <code>ImageTypeSpecifier</code>.</p>
     *
     * <p>Suitable <code>ColorModel</code>s are guaranteed to exist
     * for all instances of <code>ComponentSampleModel</code>.
     * For 1- and 3- banded <code>SampleModel</code>s, the
     * <code>ColorModel</code> will be opaque.  For 2- and 4-banded
     * <code>SampleModel</code>s, the output will use alpha transparency
     * which is not premultiplied.  1- and 2-banded data will use a
     * grayscale <code>ColorSpace</code>, and 3- and 4-banded data a sRGB
     * <code>ColorSpace</code>. Data with 5 or more bands will use a
     * <code>ColorSpace</code> which satisfies compatibility constraints
     * but is merely a placeholder and <i>does not perform correct color
     * conversion to and from the C.I.E. XYZ and sRGB color spaces</i>.</p>
     *
     * <p>An instance of <code>DirectColorModel</code> will be created for
     * instances of <code>SinglePixelPackedSampleModel</code> with no more
     * than 4 bands.</p>
     *
     * <p>An instance of <code>IndexColorModel</code> will be created for
     * instances of <code>MultiPixelPackedSampleModel</code>. The colormap
     * will be a grayscale ramp with <code>1&nbsp;<<&nbsp;numberOfBits</code>
     * entries ranging from zero to at most 255.</p>
     *
     *  @param source The <code>ImageInputStream</code> containing all the raw
     *                images.
     *  @param sampleModel The <code>SampleModel</code> for all the images
     *                in the stream.
     *  @param imageOffsets The position of the first sample for each image
     *                in the stream.
     *  @param imageDimensions The image size for each image in the stream.
     *
     *  @throws IllegalArgumentException If <code>sampleModel</code> is null.
     *  @throws IllegalArgumentException If the sizes of
     *                <code>imageOffsets</code>
     *                and <code>imageDimensions</code> are different or if
     *                either array is null.
     *  @throws IllegalArgumentException If it is not possible to create a
     *                <code>ColorModel</code> from the supplied
     *                <code>SampleModel</code>.
     */
    public RawImageInputStream(ImageInputStream source,
                               SampleModel sampleModel,
                               long[] imageOffsets,
                               Dimension[] imageDimensions) {
        if (imageOffsets == null || imageDimensions == null ||
            imageOffsets.length != imageDimensions.length) {
            throw new IllegalArgumentException
                (I18N.getString("RawImageInputStream0"));
        }

        this.source = source;
        ColorModel colorModel = ImageUtil.createColorModel(sampleModel);
        if(colorModel == null) {
            throw new IllegalArgumentException
                (I18N.getString("RawImageInputStream4"));
        }
        this.type = new ImageTypeSpecifier(colorModel, sampleModel);
        this.imageOffsets = imageOffsets;
        this.imageDimensions = imageDimensions;
    }

    /**
     * Constructor.
     *
     * The <code>xmlSource</code> must adhere to the following DTD:
     *
     *<pre>
     *&lt;!DOCTYPE "com_sun_media_imageio_stream_raw_1.0" [
     *
     *  &lt;!ELEMENT com_sun_media_imageio_stream_raw_1.0
     *    (byteOrder?, offset?, width?, height?,
     *     (ComponentSampleModel |
     *      MultiPixelPackedSampleModel |
     *      SinglePixelPackedSampleModel),
     *     (ComponentColorModel |
     *      DirectColorModel |
     *      IndexColorModel)?)&gt;
     *
     *    &lt;!ATTLIST com_sun_media_imageio_stream_raw_1.0
     *      xmlns CDATA #FIXED "http://com/sun/media/imageio"&gt;
     *
     *  &lt;!ELEMENT byteOrder (#PCDATA)&gt;
     *    &lt;!-- Byte order of data stream --&gt;
     *    &lt;!-- Either "NETWORK" or "REVERSE" --&gt;
     *    &lt;!-- Data type: String --&gt;
     *
     *  &lt;!ELEMENT offset (#PCDATA)&gt;
     *    &lt;!-- Byte offset to the image data in the stream --&gt;
     *    &lt;!-- Data type: long --&gt;
     *
     *  &lt;!ELEMENT width (#PCDATA)&gt;
     *    &lt;!-- Image width; default value is SampleModel width --&gt;
     *    &lt;!-- Data type: int --&gt;
     *
     *  &lt;!ELEMENT height (#PCDATA)&gt;
     *    &lt;!-- Image height; default value is SampleModel height --&gt;
     *    &lt;!-- Data type: int --&gt;
     *
     *  &lt;!ELEMENT ComponentSampleModel EMPTY&gt;
     *    &lt;!-- ComponentSampleModel --&gt;
     *
     *    &lt;!ATTLIST ComponentSampleModel
     *      dataType (BYTE | USHORT | SHORT | INT | FLOAT | DOUBLE) #REQUIRED
     *        &lt;!-- Data type: String --&gt;
     *      w              CDATA #REQUIRED
     *        &lt;!-- SampleModel width --&gt;
     *        &lt;!-- Data type: int --&gt;
     *      h              CDATA #REQUIRED
     *        &lt;!-- SampleModel height --&gt;
     *        &lt;!-- Data type: int --&gt;
     *      pixelStride    CDATA "1"
     *        &lt;!-- SampleModel pixel stride --&gt;
     *        &lt;!-- Data type: int --&gt;
     *      scanlineStride CDATA #REQUIRED
     *        &lt;!-- SampleModel line stride --&gt;
     *        &lt;!-- Data type: int --&gt;
     *      bankIndices    CDATA #IMPLIED
     *        &lt;!-- SampleModel bank indices --&gt;
     *        &lt;!-- Data type: int array --&gt;
     *      bandOffsets    CDATA #REQUIRED&gt;
     *        &lt;!-- SampleModel band offsets --&gt;
     *        &lt;!-- Data type: int array --&gt;
     *
     *  &lt;!ELEMENT MultiPixelPackedSampleModel EMPTY&gt;
     *    &lt;!-- MultiPixelPackedSampleModel --&gt;
     *
     *    &lt;!ATTLIST MultiPixelPackedSampleModel
     *      dataType       (BYTE | USHORT | INT) #REQUIRED
     *        &lt;!-- Data type: String --&gt;
     *      w              CDATA #REQUIRED
     *        &lt;!-- SampleModel width --&gt;
     *        &lt;!-- Data type: int --&gt;
     *      h              CDATA #REQUIRED
     *        &lt;!-- SampleModel height --&gt;
     *        &lt;!-- Data type: int --&gt;
     *      numberOfBits   CDATA #REQUIRED
     *        &lt;!-- Number of bits per pixel --&gt;
     *        &lt;!-- Data type: int --&gt;
     *      scanlineStride CDATA #REQUIRED
     *        &lt;!-- SampleModel line stride --&gt;
     *        &lt;!-- Data type: int --&gt;
     *      dataBitOffset  CDATA "0"&gt;
     *        &lt;!-- Offset to first valid bit in a line --&gt;
     *        &lt;!-- Data type: int --&gt;
     *
     *  &lt;!ELEMENT SinglePixelPackedSampleModel EMPTY&gt;
     *    &lt;!-- SinglePixelPackedSampleModel --&gt;
     *
     *    &lt;!ATTLIST SinglePixelPackedSampleModel
     *      dataType       (BYTE | USHORT | INT) #REQUIRED
     *        &lt;!-- Data type: String --&gt;
     *      w              CDATA #REQUIRED
     *        &lt;!-- SampleModel width --&gt;
     *        &lt;!-- Data type: int --&gt;
     *      h              CDATA #REQUIRED
     *        &lt;!-- SampleModel height --&gt;
     *        &lt;!-- Data type: int --&gt;
     *      scanlineStride CDATA #REQUIRED
     *        &lt;!-- SampleModel line stride --&gt;
     *        &lt;!-- Data type: int --&gt;
     *      bitMasks       CDATA #REQUIRED&gt;
     *        &lt;!-- Masks indicating RGBA positions --&gt;
     *        &lt;!-- Data type: int --&gt;
     *
     *  &lt;!ELEMENT ComponentColorModel EMPTY&gt;
     *    &lt;!-- ComponentColorModel --&gt;
     *
     *    &lt;!ATTLIST ComponentColorModel
     *      colorSpace (CIEXYZ | GRAY | LINEAR_RGB | PYCC | sRGB | URL)
     *                     #REQUIRED
     *        &lt;!-- A string representing a predefined ColorSpace or a URI
     *                representing the location of any ICC profile from which
     *                a ColorSpace may be created. --&gt;
     *        &lt;!-- Data type: String --&gt;
     *      bits                 CDATA          #IMPLIED
     *        &lt;!-- Number of bits per color component --&gt;
     *        &lt;!-- Data type: int --&gt;
     *      hasAlpha             (true | false) #REQUIRED
     *        &lt;!-- Whether an alpha channel is present --&gt;
     *        &lt;!-- Data type: boolean --&gt;
     *      isAlphaPremultiplied (true | false) #REQUIRED
     *        &lt;!-- Whether any alpha channel is premultiplied --&gt;
     *        &lt;!-- Data type: boolean --&gt;
     *      transparency         (BITMASK | OPAQUE | TRANSLUCENT) #REQUIRED
     *        &lt;!-- The type of transparency --&gt;
     *      transferType (BYTE | USHORT | SHORT | INT | FLOAT | DOUBLE)
     *                     #REQUIRED&gt;
     *        &lt;!-- The data transfer type --&gt;
     *
     *  &lt;!ELEMENT DirectColorModel EMPTY&gt;
     *    &lt;!-- DirectColorModel --&gt;
     *
     *    &lt;!ATTLIST DirectColorModel
     *      colorSpace           (LINEAR_RGB | sRGB | URL) #IMPLIED
     *        &lt;!-- A string representing a predefined RGB ColorSpace or a
     *                URL representing the location of any ICC profile from
     *                which an RGB ColorSpace may be created. --&gt;
     *        &lt;!-- Data type: String --&gt;
     *      bits       CDATA #REQUIRED
     *        &lt;!-- Number of bits per color component --&gt;
     *        &lt;!-- Data type: int --&gt;
     *      rmask      CDATA #REQUIRED
     *        &lt;!-- Bitmask of the red component --&gt;
     *        &lt;!-- Data type: int --&gt;
     *      gmask      CDATA #REQUIRED
     *        &lt;!-- Bitmask of the grenn component --&gt;
     *        &lt;!-- Data type: int --&gt;
     *      bmask      CDATA #REQUIRED
     *        &lt;!-- Bitmask of the blue component --&gt;
     *        &lt;!-- Data type: int --&gt;
     *      amask      CDATA "0"&gt;
     *        &lt;!-- Bitmask of the alpha component --&gt;
     *        &lt;!-- Data type: int --&gt;
     *
     *  &lt;!ELEMENT IndexColorModel EMPTY&gt;
     *    &lt;!-- IndexColorModel --&gt;
     *
     *    &lt;!ATTLIST IndexColorModel
     *      bits CDATA #REQUIRED
     *        &lt;!-- Number of bits per color component --&gt;
     *        &lt;!-- Data type: int --&gt;
     *      size CDATA #REQUIRED
     *        &lt;!-- Number of elements in the colormap --&gt;
     *        &lt;!-- Data type: int --&gt;
     *      r    CDATA #REQUIRED
     *        &lt;!-- Red elements of the colormap --&gt;
     *        &lt;!-- Data type: byte array --&gt;
     *      g    CDATA #REQUIRED
     *        &lt;!-- Green elements of the colormap --&gt;
     *        &lt;!-- Data type: byte array --&gt;
     *      b    CDATA #REQUIRED
     *        &lt;!-- Blue elements of the colormap --&gt;
     *        &lt;!-- Data type: byte array --&gt;
     *      a    CDATA #IMPLIED&gt;
     *        &lt;!-- Alpha elements of the colormap --&gt;
     *        &lt;!-- Data type: byte array --&gt;
     *]&gt;
     *</pre>
     *
     *
     *  @param source The <code>ImageInputStream</code> containing all the raw
     *                images.
     *  @param xmlSource The <code>org.xml.sax.InputSource</code> to provide
     *                the xml document in which the stream structure is defined.
     *
     *  @throws RuntimeException If the parse configuration isn't correct.
     *
     *  @throws IllegalArgumentException If the number of "width" elements isn't
     *                the same as the number of "height" elements.
     *
     *  @throws SAXException If one is thrown in parsing.
     *
     *  @throws IOException If one is thrown in parsing, or creating color space
     *                from a URL.
     */
    public RawImageInputStream(ImageInputStream source,
                               org.xml.sax.InputSource xmlSource)
                               throws SAXException, IOException {
        this.source = source;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(true);
        dbf.setNamespaceAware(true);
        dbf.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                       "http://www.w3.org/2001/XMLSchema");
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(I18N.getString("RawImageInputStream1"),
                                                      ex);
        }

        Document doc = db.parse(xmlSource);

        //gets the byte order
        NodeList nodes = doc.getElementsByTagName("byteOrder");
        String byteOrder = nodes.item(0).getNodeValue();
        if ("NETWORK".equals(byteOrder)) {
            this.setByteOrder(ByteOrder.BIG_ENDIAN);
            this.source.setByteOrder(ByteOrder.BIG_ENDIAN);
        } else  if ("REVERSE".equals(byteOrder)) {
            this.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            this.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        }

        //gets image offsets
        nodes = doc.getElementsByTagName("offset");
        int length = nodes.getLength();
        this.imageOffsets = new long[length];
        for (int i = 0; i < length; i++) {
            imageOffsets[i] = new Long(nodes.item(i).getNodeValue()).longValue();
        }

        //gets image dimensions
        nodes = doc.getElementsByTagName("width");
        NodeList nodes1 = doc.getElementsByTagName("height");
        length = nodes.getLength();
        if (length != nodes1.getLength())
            throw new IllegalArgumentException
                (I18N.getString("RawImageInputStream2"));

        this.imageDimensions = new Dimension[length];
        for (int i = 0; i < length; i++) {
            String w = nodes.item(i).getNodeValue();
            String h = nodes1.item(i).getNodeValue();

            imageDimensions[i] =
                new Dimension((new Integer(w)).intValue(),
                              (new Integer(h)).intValue());
        }

        //get sampleModel
        SampleModel sampleModel = null;

        // for ComponentSampleModel
        nodes = doc.getElementsByTagName("ComponentSampleModel");
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            int[] bankIndices =getIntArray (node, "bankIndices");

            if (bankIndices == null)
                sampleModel =
                    new ComponentSampleModel(getInt(node, "dataType"),
                                             getInt(node, "w"),
                                             getInt(node, "h"),
                                             getInt(node, "pixelStride"),
                                             getInt(node, "scanlineStride"),
                                             getIntArray(node, "bandOffsets"));
            else
                sampleModel =
                    new ComponentSampleModel(getInt(node, "dataType"),
                                             getInt(node, "w"),
                                             getInt(node, "h"),
                                             getInt(node, "pixelStride"),
                                             getInt(node, "scanlineStride"),
                                             bankIndices,
                                             getIntArray(node, "bandOffsets"));
        }

        // for MultiPixelPackedSampleModel
        nodes = doc.getElementsByTagName("MultiPixelPackedSampleModel");
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            sampleModel =
                new MultiPixelPackedSampleModel(getInt(node, "dataType"),
                                         getInt(node, "w"),
                                         getInt(node, "h"),
                                         getInt(node, "numberOfBits"),
                                         getInt(node, "scanlineStride"),
                                         getInt(node, "dataBitOffset"));
        }

        // for SinglePixelPackedSampleModel
        nodes = doc.getElementsByTagName("SinglePixelPackedSampleModel");
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            sampleModel =
                new SinglePixelPackedSampleModel(getInt(node, "dataType"),
                                         getInt(node, "w"),
                                         getInt(node, "h"),
                                         getInt(node, "scanlineStride"),
                                         getIntArray(node, "bitMasks"));
        }

        //get colorModel
        ColorModel colorModel = null;

        // for ComponentColorModel
        nodes = doc.getElementsByTagName("ComponentColorModel");
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            colorModel =
                new ComponentColorModel(getColorSpace(node),
                                        getIntArray(node, "bits"),
                                        getBoolean(node, "hasAlpha"),
                                        getBoolean(node, "isAlphaPremultiplied"),
                                        getTransparency(getAttribute(node, "transparency")),
                                        getInt(node, "transferType"));
        }

        // for DirectColorModel
        nodes = doc.getElementsByTagName("DirectColorModel");
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            colorModel =
                new DirectColorModel(getColorSpace(node),
                                     getInt(node, "bits"),
                                     getInt(node, "rmask"),
                                     getInt(node, "gmask"),
                                     getInt(node, "bmask"),
                                     getInt(node, "amask"),
                                     false,
                                     Transparency.OPAQUE);
        }

        // for IndexColorModel
        nodes = doc.getElementsByTagName("IndexColorModel");
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0);
            byte[] alpha = getByteArray(node, "a");

            if (alpha == null)
                colorModel =
                    new IndexColorModel(getInt(node, "bits"),
                                        getInt(node, "size"),
                                        getByteArray(node, "r"),
                                        getByteArray(node, "g"),
                                        getByteArray(node, "b"));
            else
                colorModel =
                    new IndexColorModel(getInt(node, "bits"),
                                        getInt(node, "size"),
                                        getByteArray(node, "r"),
                                        getByteArray(node, "g"),
                                        getByteArray(node, "b"),
                                        alpha);
        }

        //create image type
        this.type = new ImageTypeSpecifier(colorModel, sampleModel);

        //assign imagedimension based on the sample model
        if (this.imageDimensions.length == 0) {
            this.imageDimensions = new Dimension[this.imageOffsets.length];

            imageDimensions[0] = new Dimension(sampleModel.getWidth(),
                                               sampleModel.getHeight());
            for (int i = 1; i < imageDimensions.length; i++)
                imageDimensions[i] = imageDimensions[0];
        }
    }

    /**
     * Retrieves the image type.
     *
     * @return the image type
     */
    public ImageTypeSpecifier getImageType() {
        return type;
    }

    /**
     * Retrieves the image offset of the <code>imageIndex</code>th image.
     *
     * @param imageIndex the index of the image of interest.
     * @throws IllegalArgumentException If the provided parameter is out of
     *              range.
     * @return the offset in the stream to the specified image.
     */
    public long getImageOffset(int imageIndex) {
        if (imageIndex < 0 || imageIndex >= imageOffsets.length)
            throw new IllegalArgumentException
                (I18N.getString("RawImageInputStream3"));
        return imageOffsets[imageIndex];
    }

    /** Retrieves the dimnsion of the <code>imageIndex</code>th image.
     * @param imageIndex the index of the image of interest.
     *  @throws IllegalArgumentException If the provided parameter is out of
     *              rangle.
     * @return the size of the specified image.
     */
    public Dimension getImageDimension(int imageIndex) {
        if (imageIndex < 0 || imageIndex >= imageOffsets.length)
            throw new IllegalArgumentException
                (I18N.getString("RawImageInputStream3"));
        return imageDimensions[imageIndex];
    }

    /**
     * Retrieves the number of images in the <code>ImageInputStream</code>.
     * @return the number of image in the stream.
     */
    public int getNumImages() {
        return imageOffsets.length;
    }

    public void setByteOrder(ByteOrder byteOrder) {
        source.setByteOrder(byteOrder);
    }

    public ByteOrder getByteOrder() {
        return source.getByteOrder();
    }

    public int read() throws IOException {
        return source.read();
    }

    public int read(byte[] b) throws IOException {
        return source.read(b);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return source.read(b, off, len);
    }

    public void readBytes(IIOByteBuffer buf, int len) throws IOException {
        source.readBytes(buf, len);
    }

    public boolean readBoolean() throws IOException {
        return source.readBoolean();
    }

    public byte readByte() throws IOException {
        return source.readByte();
    }

    public int readUnsignedByte() throws IOException {
        return source.readUnsignedByte();
    }

    public short readShort() throws IOException {
        return source.readShort();
    }

    public int readUnsignedShort() throws IOException {
        return source.readUnsignedShort();
    }

    public char readChar() throws IOException {
        return source.readChar();
    }

    public int readInt() throws IOException {
        return source.readInt();
    }

    public long readUnsignedInt() throws IOException {
        return source.readUnsignedInt();
    }

    public long readLong() throws IOException {
        return source.readLong();
    }

    public float readFloat() throws IOException {
        return source.readFloat();
    }

    public double readDouble() throws IOException {
        return source.readDouble();
    }

    public String readLine() throws IOException {
        return source.readLine();
    }

    public String readUTF() throws IOException {
        return source.readUTF();
    }

    public void readFully(byte[] b, int off, int len) throws IOException {
        source.readFully(b, off, len);
    }

    public void readFully(byte[] b) throws IOException {
        source.readFully(b);
    }

    public void readFully(short[] s, int off, int len) throws IOException {
        source.readFully(s, off, len);
    }

    public void readFully(char[] c, int off, int len) throws IOException {
        source.readFully(c, off, len);
    }

    public void readFully(int[] i, int off, int len) throws IOException {
        source.readFully(i, off, len);
    }

    public void readFully(long[] l, int off, int len) throws IOException {
        source.readFully(l, off, len);
    }

    public void readFully(float[] f, int off, int len) throws IOException {
        source.readFully(f, off, len);
    }

    public void readFully(double[] d, int off, int len) throws IOException {
        source.readFully(d, off, len);
    }

    public long getStreamPosition() throws IOException {
        return source.getStreamPosition();
    }

    public int getBitOffset() throws IOException {
        return source.getBitOffset();
    }

    public void setBitOffset(int bitOffset) throws IOException {
        source.setBitOffset(bitOffset);
    }

    public int readBit() throws IOException {
        return source.readBit();
    }

    public long readBits(int numBits) throws IOException {
        return source.readBits(numBits);
    }

    public long length() throws IOException {
        return source.length();
    }

    public int skipBytes(int n) throws IOException {
        return source.skipBytes(n);
    }

    public long skipBytes(long n) throws IOException {
        return source.skipBytes(n);
    }

    public void seek(long pos) throws IOException {
        source.seek(pos);
    }

    public void mark() {
        source.mark();
    }

    public void reset() throws IOException {
        source.reset();
    }

    public void flushBefore(long pos) throws IOException {
        source.flushBefore(pos);
    }

    public void flush() throws IOException {
        source.flush();
    }

    public long getFlushedPosition() {
        return source.getFlushedPosition();
    }

    public boolean isCached() {
        return source.isCached();
    }

    public boolean isCachedMemory() {
        return source.isCachedMemory();
    }

    public boolean isCachedFile() {
        return source.isCachedFile();
    }

    public void close() throws IOException {
        source.close();
    }
}
