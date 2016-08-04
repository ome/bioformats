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
 * $RCSfile: TIFFField.java,v $
 *
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All  Rights Reserved.
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
 * $Date: 2006/04/28 01:28:49 $
 * $State: Exp $
 */
package com.sun.media.imageio.plugins.tiff;

import java.io.IOException;
import java.io.Serializable;
import java.util.StringTokenizer;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import com.sun.media.imageio.plugins.tiff.TIFFTag;
import com.sun.media.imageio.plugins.tiff.TIFFTagSet;
import com.sun.media.imageioimpl.plugins.tiff.TIFFFieldNode;

/**
 * A class representing a field in a TIFF 6.0 Image File Directory.
 *
 * <p> A field in a TIFF Image File Directory (IFD) is defined as a
 * tag number accompanied by a sequence of values of identical data type.
 * TIFF 6.0 defines 12 data types; a 13th type <code>IFD</code> is
 * defined in TIFF Tech Note 1 of TIFF Specification Supplement 1. These
 * TIFF data types are referred to by Java constants and mapped internally
 * onto Java language data types and type names as follows:
 *
 * <br>
 * <br>
 * <table border="1">
 *
 * <tr>
 * <th>
 * <b>TIFF Data Type</b>
 * </th>
 * <th>
 * <b>Java Constant</b>
 * </th>
 * <th>
 * <b>Java Data Type</b>
 * </th>
 * <th>
 * <b>Java Type Name</b>
 * </th>
 * </tr>
 *
 * <tr>
 * <td>
 * <tt>BYTE</tt>
 * </td>
 * <td>
 * {@link TIFFTag#TIFF_BYTE}
 * </td>
 * <td>
 * <code>byte</code>
 * </td>
 * <td>
 * <code>"Byte"</code>
 * </td>
 * </tr>
 *
 * <tr>
 * <td>
 * <tt>ASCII</tt>
 * </td>
 * <td>
 * {@link TIFFTag#TIFF_ASCII}
 * </td>
 * <td>
 * <code>String</code>
 * </td>
 * <td>
 * <code>"Ascii"</code>
 * </td>
 * </tr>
 *
 * <tr>
 * <td>
 * <tt>SHORT</tt>
 * </td>
 * <td>
 * {@link TIFFTag#TIFF_SHORT}
 * </td>
 * <td>
 * <code>char</code>
 * </td>
 * <td>
 * <code>"Short"</code>
 * </td>
 * </tr>
 *
 * <tr>
 * <td>
 * <tt>LONG</tt>
 * </td>
 * <td>
 * {@link TIFFTag#TIFF_LONG}
 * </td>
 * <td>
 * <code>long</code>
 * </td>
 * <td>
 * <code>"Long"</code>
 * </td>
 * </tr>
 *
 * <tr>
 * <td>
 * <tt>RATIONAL</tt>
 * </td>
 * <td>
 * {@link TIFFTag#TIFF_RATIONAL}
 * </td>
 * <td>
 * <code>long[2]</code> {numerator, denominator}
 * </td>
 * <td>
 * <code>"Rational"</code>
 * </td>
 * </tr>
 *
 * <tr>
 * <td>
 * <tt>SBYTE</tt>
 * </td>
 * <td>
 * {@link TIFFTag#TIFF_SBYTE}
 * </td>
 * <td>
 * <code>byte</code>
 * </td>
 * <td>
 * <code>"SByte"</code>
 * </td>
 * </tr>
 *
 * <tr>
 * <td>
 * <tt>UNDEFINED</tt>
 * </td>
 * <td>
 * {@link TIFFTag#TIFF_UNDEFINED}
 * </td>
 * <td>
 * <code>byte</code>
 * </td>
 * <td>
 * <code>"Undefined"</code>
 * </td>
 * </tr>
 *
 * <tr>
 * <td>
 * <tt>SSHORT</tt>
 * </td>
 * <td>
 * {@link TIFFTag#TIFF_SSHORT}
 * </td>
 * <td>
 * <code>short</code>
 * </td>
 * <td>
 * <code>"SShort"</code>
 * </td>
 * </tr>
 *
 * <tr>
 * <td>
 * <tt>SLONG</tt>
 * </td>
 * <td>
 * {@link TIFFTag#TIFF_SLONG}
 * </td>
 * <td>
 * <code>int</code>
 * </td>
 * <td>
 * <code>"SLong"</code>
 * </td>
 * </tr>
 *
 * <tr>
 * <td>
 * <tt>SRATIONAL</tt>
 * </td>
 * <td>
 * {@link TIFFTag#TIFF_SRATIONAL}
 * </td>
 * <td>
 * <code>int[2]</code> {numerator, denominator}
 * </td>
 * <td>
 * <code>"SRational"</code>
 * </td>
 * </tr>
 *
 * <tr>
 * <td>
 * <tt>FLOAT</tt>
 * </td>
 * <td>
 * {@link TIFFTag#TIFF_FLOAT}
 * </td>
 * <td>
 * <code>float</code>
 * </td>
 * <td>
 * <code>"Float"</code>
 * </td>
 * </tr>
 *
 * <tr>
 * <td>
 * <tt>DOUBLE</tt>
 * </td>
 * <td>
 * {@link TIFFTag#TIFF_DOUBLE}
 * </td>
 * <td>
 * <code>double</code>
 * </td>
 * <td>
 * <code>"Double"</code>
 * </td>
 * </tr>
 *
 * <tr>
 * <td>
 * <tt>IFD</tt>
 * </td>
 * <td>
 * {@link TIFFTag#TIFF_IFD_POINTER}
 * </td>
 * <td>
 * <code>long</code>
 * </td>
 * <td>
 * <code>"IFDPointer"</code>
 * </td>
 * </tr>
 *
 * </table>
 *
 * @see TIFFDirectory
 * @see TIFFTag
 */
public class TIFFField implements Comparable {

    private static final String[] typeNames = {
        null,
        "Byte", "Ascii", "Short", "Long", "Rational",
        "SByte", "Undefined", "SShort", "SLong", "SRational",
        "Float", "Double", "IFDPointer"
    };

    private static final boolean[] isIntegral = {
        false,
        true, false, true, true, false,
        true, true, true, true, false,
        false, false, false
    };

    /** The tag. */
    private TIFFTag tag;

    /** The tag number. */
    private int tagNumber;

    /** The tag type. */
    private int type;

    /** The number of data items present in the field. */
    private int count;

    /** The field data. */
    private Object data;
    
    /** The default constructor. */
    private TIFFField() {}

    private static String getAttribute(Node node, String attrName) {
        NamedNodeMap attrs = node.getAttributes();
        return attrs.getNamedItem(attrName).getNodeValue();
    }

    private static void initData(Node node,
                                 int[] otype, int[] ocount, Object[] odata) {
        int type;
        int count;
        Object data = null;

        String typeName = node.getNodeName();
        typeName = typeName.substring(4);
        typeName = typeName.substring(0, typeName.length() - 1);
        type = TIFFField.getTypeByName(typeName);
        if (type == -1) {
            throw new IllegalArgumentException("typeName = " + typeName);
        }

        Node child = node.getFirstChild();

        count = 0;
        while (child != null) {
            String childTypeName = child.getNodeName().substring(4);
            if (!typeName.equals(childTypeName)) {
                // warning
            }
                            
            ++count;
            child = child.getNextSibling();
        }
                        
        if (count > 0) {
            data = createArrayForType(type, count);
            child = node.getFirstChild();
            int idx = 0;
            while (child != null) {
                String value = getAttribute(child, "value");
                
                String numerator, denominator;
                int slashPos;
                
                switch (type) {
                case TIFFTag.TIFF_ASCII:
                    ((String[])data)[idx] = value;
                    break;
                case TIFFTag.TIFF_BYTE:
                case TIFFTag.TIFF_SBYTE:
                    ((byte[])data)[idx] =
                        (byte)Integer.parseInt(value);
                    break;
                case TIFFTag.TIFF_SHORT:
                    ((char[])data)[idx] =
                        (char)Integer.parseInt(value);
                    break;
                case TIFFTag.TIFF_SSHORT:
                    ((short[])data)[idx] =
                        (short)Integer.parseInt(value);
                    break;
                case TIFFTag.TIFF_SLONG:
                    ((int[])data)[idx] =
                        (int)Integer.parseInt(value);
                    break;
                case TIFFTag.TIFF_LONG:
                case TIFFTag.TIFF_IFD_POINTER:
                    ((long[])data)[idx] =
                        (long)Long.parseLong(value);
                    break;
                case TIFFTag.TIFF_FLOAT:
                    ((float[])data)[idx] =
                        (float)Float.parseFloat(value);
                    break;
                case TIFFTag.TIFF_DOUBLE:
                    ((double[])data)[idx] =
                        (double)Double.parseDouble(value);
                    break;
                case TIFFTag.TIFF_SRATIONAL:
                    slashPos = value.indexOf("/");
                    numerator = value.substring(0, slashPos);
                    denominator = value.substring(slashPos + 1);
                    
                    ((int[][])data)[idx] = new int[2];
                    ((int[][])data)[idx][0] =
                        Integer.parseInt(numerator);
                    ((int[][])data)[idx][1] =
                        Integer.parseInt(denominator);
                    break;
                case TIFFTag.TIFF_RATIONAL:
                    slashPos = value.indexOf("/");
                    numerator = value.substring(0, slashPos);
                    denominator = value.substring(slashPos + 1);
                    
                    ((long[][])data)[idx] = new long[2];
                    ((long[][])data)[idx][0] =
                        Long.parseLong(numerator);
                    ((long[][])data)[idx][1] =
                        Long.parseLong(denominator);
                    break;
                default:
                    // error
                }
                
                idx++;
                child = child.getNextSibling();
            }
        }

        otype[0] = type;
        ocount[0] = count;
        odata[0] = data;
    }

    /**
     * Creates a <code>TIFFField</code> from a TIFF native image
     * metadata node. If the value of the <tt>"tagNumber"</tt> attribute
     * of the node is not found in <code>tagSet</code> then a new
     * <code>TIFFTag</code> with name <code>"unknown"</code> will be
     * created and assigned to the field.
     *
     * @param tagSet The <code>TIFFTagSet</code> to which the
     * <code>TIFFTag</code> of the field belongs.
     * @param node A native TIFF image metadata <code>TIFFField</code> node.
     * @throws IllegalArgumentException if <code>node</code> is
     * <code>null</code>.
     * @throws IllegalArgumentException if the name of the node is not
     * <code>"TIFFField"</code>.
     */
    public static TIFFField createFromMetadataNode(TIFFTagSet tagSet,
                                                   Node node) {
        if (node == null) {
            throw new IllegalArgumentException("node == null!");
        }
        String name = node.getNodeName();
        if (!name.equals("TIFFField")) {
            throw new IllegalArgumentException("!name.equals(\"TIFFField\")");
        }

        int tagNumber = Integer.parseInt(getAttribute(node, "number"));
        TIFFTag tag;
        if (tagSet != null) {
            tag = tagSet.getTag(tagNumber);
        } else {
            tag = new TIFFTag("unknown", tagNumber, 0, null);
        }

        int type = TIFFTag.TIFF_UNDEFINED;
        int count = 0;
        Object data = null;

        Node child = node.getFirstChild();
        if (child != null) {
            String typeName = child.getNodeName();
            if (typeName.equals("TIFFUndefined")) {
                String values = getAttribute(child, "value");
                StringTokenizer st = new StringTokenizer(values, ",");
                count = st.countTokens();

                byte[] bdata = new byte[count];
                for (int i = 0; i < count; i++) {
                    bdata[i] = (byte)Integer.parseInt(st.nextToken());
                }

                type = TIFFTag.TIFF_UNDEFINED;
                data = bdata;
            } else {
                int[] otype = new int[1];
                int[] ocount = new int[1];
                Object[] odata = new Object[1];

                initData(node.getFirstChild(), otype, ocount, odata); 
                type = otype[0];
                count = ocount[0];
                data = odata[0];
            }
        } else {
            int t = TIFFTag.MAX_DATATYPE;
            while(t >= TIFFTag.MIN_DATATYPE && !tag.isDataTypeOK(t)) {
                t--;
            }
            type = t;
        }

        return new TIFFField(tag, type, count, data);
    }

    /**
     * Constructs a <code>TIFFField</code> with arbitrary data. The
     * <code>type</code> parameter must be a value for which
     * {@link TIFFTag#isDataTypeOK <code>tag.isDataTypeOK()</code>}
     * returns <code>true</code>. The <code>data</code> parameter must
     * be an array of a Java type appropriate for the type of the TIFF
     * field unless {@link TIFFTag#isIFDPointer
     * <code>tag.isIFDPointer()</code>} returns <code>true</code> in
     * which case it must be a <code>TIFFDirectory</code> instance.
     *
     * <p><i>Neither the legality of <code>type</code> with respect to
     * <code>tag</code> nor that or <code>data</code> with respect to
     * <code>type</code> is verified by this constructor.</i> The methods
     * {@link TIFFTag#isDataTypeOK <code>TIFFTag.isDataTypeOK()</code>}
     * and {@link #createArrayForType <code>createArrayForType()</code>}
     * should be used programmatically to ensure that subsequent errors
     * such as <code>ClassCastException</code>s do not occur as a result
     * of providing inconsitent parameters to this constructor.</p>
     *
     * <p>Note that the value (data) of the <code>TIFFField</code>
     * will always be the actual field value regardless of the number of
     * bytes required for that value. This is the case despite the fact
     * that the TIFF <i>IFD Entry</i> corresponding to the field may
     * actually contain the offset to the field's value rather than
     * the value itself (the latter occurring if and only if the
     * value fits into 4 bytes). In other words, the value of the
     * field will already have been read from the TIFF stream. This
     * subsumes the case where <code>tag.isIFDPointer()</code> returns
     * <code>true</code> and the value will be a <code>TIFFDirectory</code>
     * rather than an array.</p>
     *
     * @param tag The tag to associated with this field.
     * @param type One of the <code>TIFFTag.TIFF_*</code> constants
     * indicating the data type of the field as written to the TIFF stream.
     * @param count The number of data values.
     * @param data The actual data content of the field.
     *
     * @throws IllegalArgumentException if <code>tag&nbsp;==&nbsp;null</code>.
     * @throws IllegalArgumentException if <code>dataType</code> is not
     * one of the <code>TIFFTag.TIFF_*</code> data type constants.
     * @throws IllegalArgumentException if <code>count&nbsp;&lt;&nbsp;0</code>.
     */
    public TIFFField(TIFFTag tag, int type, int count, Object data) {
        if(tag == null) {
            throw new IllegalArgumentException("tag == null!");
        } else if(type < TIFFTag.MIN_DATATYPE || type > TIFFTag.MAX_DATATYPE) {
            throw new IllegalArgumentException("Unknown data type "+type);
        } else if(count < 0) {
            throw new IllegalArgumentException("count < 0!");
        }
        this.tag = tag;
        this.tagNumber = tag.getNumber();
        this.type = type;
        this.count = count;
        this.data = data;
    }

    /**
     * Constructs a data array using {@link #createArrayForType
     * <code>createArrayForType()</code>} and invokes
     * {@link #TIFFField(TIFFTag,int,int,Object)} with the supplied
     * parameters and the created array.
     *
     * @see #TIFFField(TIFFTag,int,int,Object)
     */
    public TIFFField(TIFFTag tag, int type, int count) {
        this(tag, type, count, createArrayForType(type, count));
    }

    /**
     * Constructs a <code>TIFFField</code> with a single integral value.
     * The field will have type
     * {@link TIFFTag#TIFF_SHORT  <code>TIFF_SHORT</code>} if
     * <code>val&nbsp;&lt;&nbsp;65536</code> and type
     * {@link TIFFTag#TIFF_LONG <code>TIFF_LONG</code>} otherwise.
     * <i>It is <b>not</b> verified whether the resulting type is
     * legal for <code>tag</code>.</i>
     *
     * @param tag The tag to associate with this field.
     * @param value The value to associate with this field.
     * @throws IllegalArgumentException if <code>tag&nbsp;==&nbsp;null</code>.
     * @throws IllegalArgumentException if <code>value&nbsp;&lt;&nbsp;0</code>.
     */
    public TIFFField(TIFFTag tag, int value) {
        if(tag == null) {
            throw new IllegalArgumentException("tag == null!");
        }
        if (value < 0) {
            throw new IllegalArgumentException("value < 0!");
        }

        this.tag = tag;
        this.tagNumber = tag.getNumber();
        this.count = 1;

        if (value < 65536) {
            this.type = TIFFTag.TIFF_SHORT;
            char[] cdata = new char[1];
            cdata[0] = (char)value;
            this.data = cdata;
        } else {
            this.type = TIFFTag.TIFF_LONG;
            long[] ldata = new long[1];
            ldata[0] = value;
            this.data = ldata;
        }
    }

    /**
     * Retrieves the tag associated with this field.
     *
     * @return The associated <code>TIFFTag</code>.
     */
    public TIFFTag getTag() {
        return tag;
    }

    /**
     * Retrieves the tag number in the range <code>[0,&nbsp;65535]</code>.
     *
     * @return The tag number.
     */
    public int getTagNumber() {
        return tagNumber;
    }

    /**
     * Returns the type of the data stored in the field.  For a TIFF 6.0
     * stream, the value will equal one of the <code>TIFFTag.TIFF_*</code>
     * constants. For future revisions of TIFF, higher values are possible.
     *
     * @return The data type of the field value.
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the name of the supplied data type constant.
     *
     * @param dataType One of the <code>TIFFTag.TIFF_*</code> constants
     * indicating the data type of the field as written to the TIFF stream.
     * @return The type name corresponding to the supplied type constant.
     * @throws IllegalArgumentException if <code>dataType</code> is not
     * one of the <code>TIFFTag.TIFF_*</code> data type constants.
     */
    public static String getTypeName(int dataType) {
        if (dataType < TIFFTag.MIN_DATATYPE ||
            dataType > TIFFTag.MAX_DATATYPE) {
            throw new IllegalArgumentException("Unknown data type "+dataType);
        }

        return typeNames[dataType];
    }

    /**
     * Returns the data type constant corresponding to the supplied data
     * type name. If the name is unknown <code>-1</code> will be returned.
     *
     * @return One of the <code>TIFFTag.TIFF_*</code> constants or
     * <code>-1</code> if the name is not recognized.
     */
    public static int getTypeByName(String typeName) {
        for (int i = TIFFTag.MIN_DATATYPE; i <= TIFFTag.MAX_DATATYPE; i++) {
            if (typeName.equals(typeNames[i])) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Creates an array appropriate for the indicated data type.
     *
     * @param dataType One of the <code>TIFFTag.TIFF_*</code> data type
     * constants.
     * @param count The number of values in the array.
     *
     * @throws IllegalArgumentException if <code>dataType</code> is not
     * one of the <code>TIFFTag.TIFF_*</code> data type constants.
     * @throws IllegalArgumentException if <code>count&nbsp;&lt;&nbsp;0</code>.
     */
    public static Object createArrayForType(int dataType, int count) {
        if(count < 0) {
            throw new IllegalArgumentException("count < 0!");
        }
        switch (dataType) {
        case TIFFTag.TIFF_BYTE:
        case TIFFTag.TIFF_SBYTE:
        case TIFFTag.TIFF_UNDEFINED:
            return new byte[count];
        case TIFFTag.TIFF_ASCII:
            return new String[count];
        case TIFFTag.TIFF_SHORT:
            return new char[count];
        case TIFFTag.TIFF_LONG:
        case TIFFTag.TIFF_IFD_POINTER:
            return new long[count];
        case TIFFTag.TIFF_RATIONAL:
            return new long[count][2];
        case TIFFTag.TIFF_SSHORT:
            return new short[count];
        case TIFFTag.TIFF_SLONG:
            return new int[count];
        case TIFFTag.TIFF_SRATIONAL:
            return new int[count][2];
        case TIFFTag.TIFF_FLOAT:
            return new float[count];
        case TIFFTag.TIFF_DOUBLE:
            return new double[count];
        default:
            throw new IllegalArgumentException("Unknown data type "+dataType);
        }
    }

    /**
     * Returns the <code>TIFFField</code> as a node named either
     * <tt>"TIFFField"</tt> or <tt>"TIFFIFD"</tt> as described in the
     * TIFF native image metadata specification. The node will be named
     * <tt>"TIFFIFD"</tt> if and only if the field's data object is an
     * instance of {@link TIFFDirectory} or equivalently
     * {@link TIFFTag#isIFDPointer getTag.isIFDPointer()} returns
     * <code>true</code>.
     *
     * @return a <code>Node</code> named <tt>"TIFFField"</tt> or
     * <tt>"TIFFIFD"</tt>.
     */
    public Node getAsNativeNode() {
        return new TIFFFieldNode(this);
    }

    /**
     * Indicates whether the value associated with the field is of
     * integral data type.
     *
     * @return Whether the field type is integral.
     */
    public boolean isIntegral() {
        return isIntegral[type];
    }

    /**
     * Returns the number of data items present in the field.  For
     * <code>TIFFTag.TIFF_ASCII</code> fields, the value returned is the
     * number of <code>String</code>s, not the total length of the
     * data as in the file representation.
     */
    public int getCount() {
        return count;
    }

    /**
     * Returns a reference to the data object associated with the field.
     *
     * @return The data object of the field.
     */
    public Object getData() {
        return data;
    }

    /**
     * Returns the data as an uninterpreted array of
     * <code>byte</code>s.  The type of the field must be one of
     * <code>TIFFTag.TIFF_BYTE</code>, <code>TIFF_SBYTE</code>, or
     * <code>TIFF_UNDEFINED</code>.
     *
     * <p> For data in <code>TIFFTag.TIFF_BYTE</code> format, the application
     * must take care when promoting the data to longer integral types
     * to avoid sign extension.
     *
     * @throws ClassCastException if the field is not of type
     * <code>TIFF_BYTE</code>, <code>TIFF_SBYTE</code>, or
     * <code>TIFF_UNDEFINED</code>.
     */
    public byte[] getAsBytes() {
        return (byte[])data;
    }

    /**
     * Returns <code>TIFFTag.TIFF_SHORT</code> data as an array of
     * <code>char</code>s (unsigned 16-bit integers).
     *
     * @throws ClassCastException if the field is not of type
     * <code>TIFF_SHORT</code>.
     */
    public char[] getAsChars() {
        return (char[])data;
    }

    /**
     * Returns <code>TIFFTag.TIFF_SSHORT</code> data as an array of
     * <code>short</code>s (signed 16-bit integers).
     *
     * @throws ClassCastException if the field is not of type
     * <code>TIFF_SSHORT</code>.
     */
    public short[] getAsShorts() {
        return (short[])data;
    }

    /**
     * Returns <code>TIFFTag.TIFF_SLONG</code> data as an array of
     * <code>int</code>s (signed 32-bit integers).
     *
     * @throws ClassCastException if the field is not of type
     * <code>TIFF_SHORT</code>, <code>TIFF_SSHORT</code>, or
     * <code>TIFF_SLONG</code>.
     */
    public int[] getAsInts() {
        if (data instanceof int[]) {
            return (int[])data;
        } else if (data instanceof char[]){
            char[] cdata = (char[])data;
            int[] idata = new int[cdata.length];
            for (int i = 0; i < cdata.length; i++) {
                idata[i] = (int)(cdata[i] & 0xffff);
            }
            return idata;
        } else if (data instanceof short[]){
            short[] sdata = (short[])data;
            int[] idata = new int[sdata.length];
            for (int i = 0; i < sdata.length; i++) {
                idata[i] = (int)sdata[i];
            }
            return idata;
        } else {
            throw new ClassCastException(
                                        "Data not char[], short[], or int[]!");
        }
    }

    /**
     * Returns <code>TIFFTag.TIFF_LONG</code> or
     * <code>TIFF_IFD_POINTER</code> data as an array of
     * <code>long</code>s (signed 64-bit integers).
     *
     * @throws ClassCastException if the field is not of type
     * <code>TIFF_LONG</code> or <code>TIFF_IFD_POINTER</code>.
     */
    public long[] getAsLongs() {
        return (long[])data;
    }

    /**
     * Returns <code>TIFFTag.TIFF_FLOAT</code> data as an array of
     * <code>float</code>s (32-bit floating-point values).
     *
     * @throws ClassCastException if the field is not of type
     * <code>TIFF_FLOAT</code>.
     */
    public float[] getAsFloats() {
        return (float[])data;
    }

    /**
     * Returns <code>TIFFTag.TIFF_DOUBLE</code> data as an array of
     * <code>double</code>s (64-bit floating-point values).
     *
     * @throws ClassCastException if the field is not of type
     * <code>TIFF_DOUBLE</code>.
     */
    public double[] getAsDoubles() {
        return (double[])data;
    }

    /**
     * Returns <code>TIFFTag.TIFF_SRATIONAL</code> data as an array of
     * 2-element arrays of <code>int</code>s.
     *
     * @throws ClassCastException if the field is not of type
     * <code>TIFF_SRATIONAL</code>.
     */
    public int[][] getAsSRationals() {
        return (int[][])data;
    }

    /**
     * Returns <code>TIFFTag.TIFF_RATIONAL</code> data as an array of
     * 2-element arrays of <code>long</code>s.
     *
     * @throws ClassCastException if the field is not of type
     * <code>TIFF_RATIONAL</code>.
     */
    public long[][] getAsRationals() {
        return (long[][])data;
    }

    /**
     * Returns data in any format as an <code>int</code>.
     *
     * <p> <code>TIFFTag.TIFF_BYTE</code> values are treated as unsigned; that
     * is, no sign extension will take place and the returned value
     * will be in the range [0, 255].  <code>TIFF_SBYTE</code> data
     * will be returned in the range [-128, 127].
     *
     * <p> A <code>TIFF_UNDEFINED</code> value is treated as though
     * it were a <code>TIFF_BYTE</code>.
     *
     * <p> Data in <code>TIFF_SLONG</code>, <code>TIFF_LONG</code>,
     * <code>TIFF_FLOAT</code>, <code>TIFF_DOUBLE</code> or
     * <code>TIFF_IFD_POINTER</code> format are simply cast to
     * <code>int</code> and may suffer from truncation.
     *
     * <p> Data in <code>TIFF_SRATIONAL</code> or
     * <code>TIFF_RATIONAL</code> format are evaluated by dividing the
     * numerator into the denominator using double-precision
     * arithmetic and then casting to <code>int</code>.  Loss of
     * precision and truncation may occur.
     *
     * <p> Data in <code>TIFF_ASCII</code> format will be parsed as by
     * the <code>Double.parseDouble</code> method, with the result
     * case to <code>int</code>.
     */
    public int getAsInt(int index) {
        switch (type) {
        case TIFFTag.TIFF_BYTE: case TIFFTag.TIFF_UNDEFINED:
            return ((byte[])data)[index] & 0xff;
        case TIFFTag.TIFF_SBYTE:
            return ((byte[])data)[index];
        case TIFFTag.TIFF_SHORT:
            return ((char[])data)[index] & 0xffff;
        case TIFFTag.TIFF_SSHORT:
            return ((short[])data)[index];
        case TIFFTag.TIFF_SLONG:
            return ((int[])data)[index];
        case TIFFTag.TIFF_LONG: case TIFFTag.TIFF_IFD_POINTER:
            return (int)((long[])data)[index];
        case TIFFTag.TIFF_FLOAT:
            return (int)((float[])data)[index];
        case TIFFTag.TIFF_DOUBLE:
            return (int)((double[])data)[index];
        case TIFFTag.TIFF_SRATIONAL:
            int[] ivalue = getAsSRational(index);
            return (int)((double)ivalue[0]/ivalue[1]);
        case TIFFTag.TIFF_RATIONAL:
            long[] lvalue = getAsRational(index);
            return (int)((double)lvalue[0]/lvalue[1]);
        case TIFFTag.TIFF_ASCII:
             String s = ((String[])data)[index];
             return (int)Double.parseDouble(s);
        default:
            throw new ClassCastException();
        }
    }

    /**
     * Returns data in any format as a <code>long</code>.
     *
     * <p> <code>TIFFTag.TIFF_BYTE</code> and <code>TIFF_UNDEFINED</code> data
     * are treated as unsigned; that is, no sign extension will take
     * place and the returned value will be in the range [0, 255].
     * <code>TIFF_SBYTE</code> data will be returned in the range
     * [-128, 127].
     *
     * <p> Data in <code>TIFF_ASCII</code> format will be parsed as by
     * the <code>Double.parseDouble</code> method, with the result
     * cast to <code>long</code>.
     */
    public long getAsLong(int index) {
        switch (type) {
        case TIFFTag.TIFF_BYTE: case TIFFTag.TIFF_UNDEFINED:
            return ((byte[])data)[index] & 0xff;
        case TIFFTag.TIFF_SBYTE:
            return ((byte[])data)[index];
        case TIFFTag.TIFF_SHORT:
            return ((char[])data)[index] & 0xffff;
        case TIFFTag.TIFF_SSHORT:
            return ((short[])data)[index];
        case TIFFTag.TIFF_SLONG:
            return ((int[])data)[index];
        case TIFFTag.TIFF_LONG: case TIFFTag.TIFF_IFD_POINTER:
            return ((long[])data)[index];
        case TIFFTag.TIFF_SRATIONAL:
            int[] ivalue = getAsSRational(index);
            return (long)((double)ivalue[0]/ivalue[1]);
        case TIFFTag.TIFF_RATIONAL:
            long[] lvalue = getAsRational(index);
            return (long)((double)lvalue[0]/lvalue[1]);
        case TIFFTag.TIFF_ASCII:
             String s = ((String[])data)[index];
             return (long)Double.parseDouble(s);
        default:
            throw new ClassCastException();
        }
    }
    
    /**
     * Returns data in any format as a <code>float</code>.
     * 
     * <p> <code>TIFFTag.TIFF_BYTE</code> and <code>TIFF_UNDEFINED</code> data
     * are treated as unsigned; that is, no sign extension will take
     * place and the returned value will be in the range [0, 255].
     * <code>TIFF_SBYTE</code> data will be returned in the range
     * [-128, 127].
     *
     * <p> Data in <code>TIFF_SLONG</code>, <code>TIFF_LONG</code>,
     * <code>TIFF_DOUBLE</code>, or <code>TIFF_IFD_POINTER</code> format are
     * simply cast to <code>float</code> and may suffer from
     * truncation.
     *
     * <p> Data in <code>TIFF_SRATIONAL</code> or
     * <code>TIFF_RATIONAL</code> format are evaluated by dividing the
     * numerator into the denominator using double-precision
     * arithmetic and then casting to <code>float</code>.
     *
     * <p> Data in <code>TIFF_ASCII</code> format will be parsed as by
     * the <code>Double.parseDouble</code> method, with the result
     * cast to <code>float</code>.
     */
    public float getAsFloat(int index) {
        switch (type) {
        case TIFFTag.TIFF_BYTE: case TIFFTag.TIFF_UNDEFINED:
            return ((byte[])data)[index] & 0xff;
        case TIFFTag.TIFF_SBYTE:
            return ((byte[])data)[index];
        case TIFFTag.TIFF_SHORT:
            return ((char[])data)[index] & 0xffff;
        case TIFFTag.TIFF_SSHORT:
            return ((short[])data)[index];
        case TIFFTag.TIFF_SLONG:
            return ((int[])data)[index];
        case TIFFTag.TIFF_LONG: case TIFFTag.TIFF_IFD_POINTER:
            return ((long[])data)[index];
        case TIFFTag.TIFF_FLOAT:
            return ((float[])data)[index];
        case TIFFTag.TIFF_DOUBLE:
            return (float)((double[])data)[index];
        case TIFFTag.TIFF_SRATIONAL:
            int[] ivalue = getAsSRational(index);
            return (float)((double)ivalue[0]/ivalue[1]);
        case TIFFTag.TIFF_RATIONAL:
            long[] lvalue = getAsRational(index);
            return (float)((double)lvalue[0]/lvalue[1]);
        case TIFFTag.TIFF_ASCII:
             String s = ((String[])data)[index];
             return (float)Double.parseDouble(s);
        default:
            throw new ClassCastException();
        }
    }

    /**
     * Returns data in any format as a <code>double</code>.
     *
     * <p> <code>TIFFTag.TIFF_BYTE</code> and <code>TIFF_UNDEFINED</code> data
     * are treated as unsigned; that is, no sign extension will take
     * place and the returned value will be in the range [0, 255].
     * <code>TIFF_SBYTE</code> data will be returned in the range
     * [-128, 127].
     *
     * <p> Data in <code>TIFF_SRATIONAL</code> or
     * <code>TIFF_RATIONAL</code> format are evaluated by dividing the
     * numerator into the denominator using double-precision
     * arithmetic.
     *
     * <p> Data in <code>TIFF_ASCII</code> format will be parsed as by
     * the <code>Double.parseDouble</code> method.
     */
    public double getAsDouble(int index) {
        switch (type) {
        case TIFFTag.TIFF_BYTE: case TIFFTag.TIFF_UNDEFINED:
            return ((byte[])data)[index] & 0xff;
        case TIFFTag.TIFF_SBYTE:
            return ((byte[])data)[index];
        case TIFFTag.TIFF_SHORT:
            return ((char[])data)[index] & 0xffff;
        case TIFFTag.TIFF_SSHORT:
            return ((short[])data)[index];
        case TIFFTag.TIFF_SLONG:
            return ((int[])data)[index];
        case TIFFTag.TIFF_LONG: case TIFFTag.TIFF_IFD_POINTER:
            return ((long[])data)[index];
        case TIFFTag.TIFF_FLOAT:
            return ((float[])data)[index];
        case TIFFTag.TIFF_DOUBLE:
            return ((double[])data)[index];
        case TIFFTag.TIFF_SRATIONAL:
            int[] ivalue = getAsSRational(index);
            return (double)ivalue[0]/ivalue[1];
        case TIFFTag.TIFF_RATIONAL:
            long[] lvalue = getAsRational(index);
            return (double)lvalue[0]/lvalue[1];
        case TIFFTag.TIFF_ASCII:
             String s = ((String[])data)[index];
             return Double.parseDouble(s);
        default:
            throw new ClassCastException();
        }
    }

    /**
     * Returns a <code>TIFFTag.TIFF_ASCII</code> value as a
     * <code>String</code>.
     *
     * @throws ClassCastException if the field is not of type
     * <code>TIFF_ASCII</code>.
     */
    public String getAsString(int index) {
        return ((String[])data)[index];
    }

    /**
     * Returns a <code>TIFFTag.TIFF_SRATIONAL</code> data item as a
     * two-element array of <code>int</code>s.
     *
     * @throws ClassCastException if the field is not of type
     * <code>TIFF_SRATIONAL</code>.
     */
    public int[] getAsSRational(int index) {
        return ((int[][])data)[index];
    }

    /**
     * Returns a TIFFTag.TIFF_RATIONAL data item as a two-element array
     * of ints.
     *
     * @throws ClassCastException if the field is not of type
     * <code>TIFF_RATIONAL</code>.
     */
    public long[] getAsRational(int index) {
        return ((long[][])data)[index];
    }


    /**
     * Returns a <code>String</code> containing a human-readable
     * version of the data item.  Data of type
     * <code>TIFFTag.TIFF_RATIONAL</code> or <code>TIFF_SRATIONAL</code> are
     * represented as a pair of integers separated by a
     * <code>'/'</code> character.
     *
     * @throws ClassCastException if the field is not of one of the
     * legal field types.
     */
    public String getValueAsString(int index) {
        switch (type) {
        case TIFFTag.TIFF_ASCII:
            return ((String[])data)[index];
        case TIFFTag.TIFF_BYTE: case TIFFTag.TIFF_UNDEFINED:
            return Integer.toString(((byte[])data)[index] & 0xff);
        case TIFFTag.TIFF_SBYTE:
            return Integer.toString(((byte[])data)[index]);
        case TIFFTag.TIFF_SHORT:
            return Integer.toString(((char[])data)[index] & 0xffff);
        case TIFFTag.TIFF_SSHORT:
            return Integer.toString(((short[])data)[index]);
        case TIFFTag.TIFF_SLONG:
            return Integer.toString(((int[])data)[index]);
        case TIFFTag.TIFF_LONG: case TIFFTag.TIFF_IFD_POINTER:
            return Long.toString(((long[])data)[index]);
        case TIFFTag.TIFF_FLOAT:
            return Float.toString(((float[])data)[index]);
        case TIFFTag.TIFF_DOUBLE:
            return Double.toString(((double[])data)[index]);
        case TIFFTag.TIFF_SRATIONAL:
            int[] ivalue = getAsSRational(index);
            String srationalString;
            if(ivalue[1] != 0 && ivalue[0] % ivalue[1] == 0) {
                // If the denominator is a non-zero integral divisor
                // of the numerator then convert the fraction to be
                // with respect to a unity denominator.
                srationalString =
                    Integer.toString(ivalue[0] / ivalue[1]) + "/1";
            } else {
                // Use the values directly.
                srationalString =
                    Integer.toString(ivalue[0]) +
                    "/" +
                    Integer.toString(ivalue[1]);
            }
            return srationalString;
        case TIFFTag.TIFF_RATIONAL:
            long[] lvalue = getAsRational(index);
            String rationalString;
            if(lvalue[1] != 0L && lvalue[0] % lvalue[1] == 0) {
                // If the denominator is a non-zero integral divisor
                // of the numerator then convert the fraction to be
                // with respect to a unity denominator.
                rationalString =
                    Long.toString(lvalue[0] / lvalue[1]) + "/1";
            } else {
                // Use the values directly.
                rationalString =
                    Long.toString(lvalue[0]) +
                    "/" +
                    Long.toString(lvalue[1]);
            }
            return rationalString;
        default:
            throw new ClassCastException();
        }
    }

    /**
     * Compares this <code>TIFFField</code> with another
     * <code>TIFFField</code> by comparing the tags.
     *
     * <p><b>Note: this class has a natural ordering that is inconsistent
     * with <code>equals()</code>.</b>
     *
     * @throws IllegalArgumentException if the parameter is <code>null</code>.
     * @throws ClassCastException if the parameter is not a
     *         <code>TIFFField</code>.
     */
    public int compareTo(Object o) {
        if (o == null) {
            throw new IllegalArgumentException();
        }

        int oTagNumber = ((TIFFField)o).getTagNumber();
        if (tagNumber < oTagNumber) {
            return -1;
        } else if (tagNumber > oTagNumber) {
            return 1;
        } else {
            return 0;
        }
    }
}
